package main;

import hotEntity.EntityData;
import hotEntity.EntityGeneration;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import params.Params;
import dataProcess.EntityDAO;
import polarityCalculation.PolarityDemo;
import dataProcess.DBConn;
import dataProcess.HotTopic_BackEnd;
import dataProcess.MongoDB;
import dataProcess.TEXT_ARTICLE;
import dataProcess.TopicTextDao;

public class EntityMain {
	public static void getEntityByType(String today,int classid) throws Exception{
		MongoDB mgdb = new MongoDB();
		DBConn db = DBConn.getDB();
		
		String getDataSql="select * from topic where updateTime='"+today+"' and typeId=1 and classifyId="+classid;
		ResultSet dbData = db.ExecuteGeneralSQLQuery(getDataSql);
		
		List<String> sentences = new ArrayList<String>();
		List<String> ids = new ArrayList<String>();
		Map<String, Map<String,String>> articleDatas = new HashMap<String, Map<String,String>>();
		
		System.out.println("开始从数据库获取数据");
		long startTime=System.currentTimeMillis();
		int articlesCount = 0;
		while(dbData.next()){
			String id = dbData.getString(2);
			List<HotTopic_BackEnd> dataList= mgdb.getTopicById(id);
			HotTopic_BackEnd hb = new HotTopic_BackEnd();
			if(dataList.size()>0){
				hb=dataList.get(0);
			}else{
				System.out.println("在MONGODB里找不到相应TOPICID"+id);
				continue;
			}
			Map<String, TEXT_ARTICLE> articles = hb.getT_ARTICLE();
			for(String ss:articles.keySet()){
				TopicTextDao textData = mgdb.getArticleById(ss);
				if(textData==null){
					System.out.println("在MONGODB里找不到相应TEXTID"+ss);
					continue;
				}
				articlesCount++;
				sentences.add(articles.get(ss).getA_TITLE().replaceAll("'",""));
				ids.add(ss);
				//if(classid!=1&&classid!=2&&classid!=11){
					String content = textData.getA_CONTENT();
					content = content.replaceAll("　|\n|'", "");
					sentences.add(content);
					ids.add(ss);
				//}
//				System.out.println(content);
//				for(String sc:content.split("!|。|！|\\s+")){
//					sentences.add(sc);
//					ids.add(ss);
//				}
				Map<String,String> tempMap = new HashMap<String,String>();
				tempMap.put(Params.HTABLE_QUAL_ATITLE, textData.getA_TITLE());
				tempMap.put(Params.HTABLE_QUAL_ATEXT,textData.getA_CONTENT());
				tempMap.put(Params.HTABLE_QUAL_AURL, textData.getA_URL());
				tempMap.put(Params.HTABLE_QUAL_ACRAWLTIME, String.valueOf(textData.getA_CRAWLTIME()));
				articleDatas.put(ss, tempMap);
			}
		}
		long endTime=System.currentTimeMillis();
		System.out.println("获取数据完成,共有文章"+articlesCount+"篇，用时"+(endTime-startTime)/1000+"s");
		mgdb.close();
		db.CloseConnection();
		
		EntityGeneration entityClass= new EntityGeneration();
		System.out.println("开始计算热点实体");
		startTime=System.currentTimeMillis();
		List<EntityData> entityList = entityClass.getCandidate(sentences,ids,500);
		endTime=System.currentTimeMillis();
		System.out.println("热点实体计算完成,用时"+(endTime-startTime)/1000+"s");
		
		System.out.println("开始存储数据");
		startTime=System.currentTimeMillis();
		saveDataToMysql(articleDatas,entityList,today,classid);
		endTime=System.currentTimeMillis();
		System.out.println("存取数据完成,用时"+(endTime-startTime)/1000+"s");
	}
	
	
	
	public static void saveDataToMysql(Map<String, Map<String,String>> articleDatas, List<EntityData> entityList,String today,int classid) throws Exception{
		MongoDB mgdb = new MongoDB();
		DBConn db = DBConn.getDB();
		List<EntityDAO> mongoresult = new LinkedList<EntityDAO>();
		Map<String,Integer> todayData = new HashMap<String,Integer>();
		for(EntityData ed:entityList){
			String name = ed.getName();
			todayData.put(name, 1);
			int posNum = ed.getPosNum();
			String entityIds = name+today+posNum+classid;
			int entityId = entityIds.hashCode();
			int fre = ed.getFrequency();
			int hot = fre;
			
			SimpleDateFormat sdf =   new SimpleDateFormat("yyyy-MM-dd");
			Date hisDate = sdf.parse(today);
			hisDate = new Date(hisDate.getTime()-1000*3600*24*7);
			String hisDay = sdf.format(hisDate);
			String FindHistoryHotSql = "select hot from entity where name='"+name+"' and createTime='"+hisDay+"' and  classid1 in("+classid+",0)";
			
			ResultSet hisSet = db.ExecuteGeneralSQLQuery(FindHistoryHotSql);
			double hisHot = 0.0;
			while(hisSet.next()){
				hisHot = (double)hisSet.getInt("hot");
				break;
			}
			if((hot-hisHot)/hisHot<0.2){
				posNum=0;
			}else{
				hot = calculateHotNumber(name,fre,classid);
			}
			double polar=0;
			int countpolar = 0;
			for (String ss:ed.getTitles()) {
						polar = polar+PolarityDemo.calXinwen(ss,ss);
			}
			if(countpolar!=0)polar = polar/countpolar;
			int tempdd = (int)(polar*1000);
			polar =  ((double)tempdd)/1000;
			//System.out.println("polar"+polar);
			String sql = "insert into entity(entityId,name,createTime,typeId,hot,frequency,polarity,classid1) values('"
									+entityId+"','"
									+name+"','"
									+today+"','"
									+posNum+"','"
									+hot+"','"
									+fre+"','"
									+polar+"','"
									+classid
									+"');";
			if(hot>5){
				db.ExecuteSQLUpdate(sql);
				System.out.println(sql);
			}
			EntityDAO temp = new EntityDAO(entityId,name,articleDatas,new ArrayList<String>(ed.getIds()),polar);
			mongoresult.add(temp);
		}
		mgdb.insertAll(mongoresult);
		mgdb.close();
		//attenuation(todayData,classid);
		db.CloseConnection();
	}
	
	public static void StartMain(int date) throws Exception, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE,date);
		Date today = calendar.getTime();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String today_str = df.format(today);
		
		for(int i=1;i<18;i++){
			System.out.println("CLASSID"+i);
			getEntityByType(today_str,i);
		}
	}
	
	public static int calculateHotNumber(String key,int fre,int classid) throws FileNotFoundException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException, SQLException
	{
		int findHotDay = 3;
		Calendar calendar = Calendar.getInstance();
		int hotNum = fre;
		DBConn db = DBConn.getDB();
    	for(int i=1;i<findHotDay;i++)
    	{
    		calendar.add(Calendar.DATE, -i);
        	Date date = calendar.getTime();
        	SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
            String strdate = df.format(date);
            String sql = "select frequency from entity where name = '"+key+"' and createTime = '"+strdate+"' and classid1="+classid;
    		ResultSet set = db.ExecuteGeneralSQLQuery(sql);
    		if(set.next())
    		{
    			hotNum = hotNum+set.getInt("frequency")/((i+1)*(i+1));
    		}
    	}
    	return hotNum;
	}
	
	public static void attenuation(Map<String,Integer> todayData,int classid) throws FileNotFoundException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException, SQLException
	{
		SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		DBConn db = DBConn.getDB();
		String today = df.format(calendar.getTime());
		calendar.add(Calendar.DATE, -1);
    	Date date = calendar.getTime();
        String strdate = df.format(date);
        String sql = "select * from entity where createTime = '"+strdate+"' and classid1="+classid;
		ResultSet set = db.ExecuteGeneralSQLQuery(sql);
		while(set.next())
		{
			String name = set.getString("name");
			if(!todayData.containsKey(name)){
				int hot = set.getInt("hot");
				hot = (hot-5)/2;
				int entityId = set.getInt("entityId");
				if(hot>5){
					String sql2 = "insert into entity(entityId,name,createTime,typeId,hot,frequency,polarity,classid1) values('"
									+entityId+"','"
									+name+"','"
									+today+"','"
									+set.getInt("typeId")+"','"
									+hot+"','"
									+set.getInt("frequency")+"','"
									+set.getDouble("polarity")+"','"
									+classid
									+"');";;
					db.ExecuteSQLUpdate(sql2);
					System.out.println("attenuation:"+sql2);
				}
			}
		}
	}
	//热点实体函数入口
	public static void main(String[]args) throws Exception{
		for(int i=0;i<1;i++){
			StartMain(0);
		}
	}
}
