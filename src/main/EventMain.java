package main;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ltpTools.LtpOnline;

import centerSentence.GetCenterSentence;

import optimization.EventData;
import test.getCenter;
import wGroup.WordGraph;
import dataProcess.DBConn;
import dataProcess.HotTopic_BackEnd;
import dataProcess.MongoDB;
import dataProcess.TEXT_ARTICLE;

public class EventMain {
	//事件主题句生成主函数，参数date控制用于计算的数据来源的选择时间，0表示选择今天的数据，-1表示选择昨天的数据，-2表示选择前天的数据，依次类推
	public static void startMain(int date) throws Exception{
		//获取热点事件数据获取
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, date);
		Date today = calendar.getTime();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String today_str = df.format(today);
		String outputfile="log\\log"+today_str+".txt";
		FileWriter fw = new FileWriter(outputfile);//日志输出文件
		MongoDB mgdb = new MongoDB();//mongodb初始化
		DBConn db = DBConn.getDB();//mysql初始化
		String sql="select * from topic where updateTime='"+today_str+"' and typeId=1 and hot>3";
		System.out.println(sql);
		ResultSet dbData = db.ExecuteGeneralSQLQuery(sql);//从mysql数据库中读取热点话题的数据
		int count=0;
		//对每一个获取的话题数据进行计算，生成每个话题对应的热点事件主题句
		while(dbData.next()){
			String id = dbData.getString(2);//获取话题ID
			List<HotTopic_BackEnd> dataList= mgdb.getTopicById(id);//根据mysql中的话题id，从mongo数据库中读取话题的所有信息
			HotTopic_BackEnd hb = new HotTopic_BackEnd();
			if(dataList.size()==1){
				hb=dataList.get(0);
			}else if(dataList.size()>1){
				fw.append("MONGODB里有多个TOPICID\n");
				System.out.println("MONGODB里有多个TOPICID");
				continue;
			}else{
				fw.append("在MONGODB里找不到相应TOPICID\n");
				System.out.println("在MONGODB里找不到相应TOPICID");
				continue;
			}
			//事件主题句生成算法
			WordGraph god=new WordGraph();//词图初始化
			
			Map<String, TEXT_ARTICLE> articles = hb.getT_ARTICLE();//获取话题下的文章id
			if(articles.size()<10){//如果文章数量小于10就不进行计算
				continue;
			}
			fw.append(count+++"\n");
			System.out.println(count++);
			
			EventData OptEvent = new EventData();//事件数据处理类的初始化，事件处理类，包括对句子的预处理，对各种特殊符号的处理，对中文字符串的判断等等
			
			fw.append("id:"+hb.getTOPIC_ID());
			List<String> titles = new ArrayList<String>();
			for(String ss:articles.keySet()){
				String sentence = articles.get(ss).getA_TITLE();//获取文章标题
				sentence = eventTitleFilter(sentence);//标题过滤
				titles.add(sentence);
			}
			//获取候选集
			GetCenterSentence test = new GetCenterSentence(titles);//用该话题下的句子初始化候选集生成类
			List<String> centerSentences = GetCenterSentence.getTopKSentence(test.ltpTitles,20,3);//获取标题中，与其他标题最相似的20条标题，采用3类型计算相似性
			for(String ss:centerSentences){
				fw.append("########"+ss+"\n");
				System.out.println("########"+ss);
				OptEvent.preImfoProcessing(ss);//标题信息预处理
			}
			//用候选句子进行词图构建
			for(String ss:OptEvent.getSplitSentences()){
				god.AddCandidateToGraph(ss);//往词图中插入标题
			}
			fw.append("点："+god.nodes.size()+"\n");
			fw.append("边："+god.edges.size()+"\n");
			System.out.println("点："+god.nodes.size());
			System.out.println("边："+god.edges.size());
			//对构建完的词图进行集束搜索，找出最优的10条路径
			List<List<Integer>> testt = god.getBestNPath(10);//将集束搜索的参数设为10，寻找最优的10条路径
			String combineWords = "";
			String firstWords = "";
			//对生成的所有路径，依次进行依存句法分析，如果符合预定义的句法格式，就将其作为热点事件主题句
			for(int x=0;x<testt.size();x++){
				//将路径转换为句子
				for(int j:testt.get(x)){
					if(j==-1||j==0||j==1)continue;
					String[]tempArray =god.nodes.get(j).wordString.split("/");
					for(int k=0;k<tempArray.length-1;k++){
						combineWords+=tempArray[k];
					}
				}
				//保存第一个句子，如果所有句子都不符合预定义的句法格式，就还原第一个句子为事件的主题句
				if(x==0){
					firstWords = combineWords;
				}
				combineWords = LtpOnline.ltpAnalysis(LtpOnline.doltp(combineWords));//对生成的标题进行LTP分析，如果其格式符合事件的判断条件，就认为其为标题，如果不符合则分析下一跳标题，知道有一个标题符合条件，如果全部不符合则选择第一个标题作为最终结果
				System.out.println(combineWords);
				//				if(eventFilter.mysqlFilter(filterMap,temp))continue;
				if(combineWords.equals("false"))
				{
					combineWords="";
					continue;
				}else{
					break;
				}
			}
			if(combineWords==""){
				combineWords = firstWords;
			}
			//combineWords = getCenter.missionFilter(combineWords);
			fw.append(combineWords+"\n");
			System.out.println("结果："+combineWords);
			if(combineWords.length()<4){
				System.out.println("#########ERROR#########");
				continue;
			}
			//存储数据
			String findSql = "select * from new_event where topicId='"+dbData.getString(2)+"'";
			ResultSet rs=db.ExecuteGeneralSQLQuery(findSql);
			if(rs.next()==false){
				String insertSql = "insert into new_event (topicId,name,createTime,updateTime,typeId,classifyId,hot,textCount,todayCount,childCount,polar)values(?,?,?,?,?,?,?,?,?,?,?)";
				PreparedStatement ps = db.getPstmt(insertSql);
				ps.setString(1, dbData.getString(2));
				ps.setString(2, combineWords);
				ps.setString(3, dbData.getString(4));
				ps.setString(4, dbData.getString(5));
				ps.setInt(5, dbData.getInt(6));
				ps.setInt(6, dbData.getInt(7));
				ps.setFloat(7, dbData.getFloat(8));
				ps.setInt(8, dbData.getInt(9));
				ps.setInt(9, dbData.getInt(10));
				ps.setInt(10, dbData.getInt(11));
				ps.setFloat(11, dbData.getFloat(12));
				ps.execute();
				ps.close();
			}else{
				String updateSql = "update new_event set name=?,createTime=?,updateTime=?,typeId=?,classifyId=?,hot=?,textCount=?,todayCount=?,childCount=?,polar=? where topicId='"+dbData.getString(2)+"'";
				PreparedStatement ps = db.getPstmt(updateSql);
				ps.setString(1, combineWords);
				ps.setString(2, dbData.getString(4));
				ps.setString(3, dbData.getString(5));
				ps.setInt(4, dbData.getInt(6));
				ps.setInt(5, dbData.getInt(7));
				ps.setFloat(6, dbData.getFloat(8));
				ps.setInt(7, dbData.getInt(9));
				ps.setInt(8, dbData.getInt(10));
				ps.setInt(9, dbData.getInt(11));
				ps.setFloat(10, dbData.getFloat(12));
				ps.execute();
				ps.close();
			}
		}
		fw.flush();
		fw.close();
		db.CloseConnection();
	}
	
	public static String eventTitleFilter(String sentence){
		sentence = dealColon(sentence);
		return sentence;
	}
	
	public static String dealColon(String ss){
		String []temp = ss.split(":|：");
		if(temp.length==2){
			if(temp[0].length()>temp[1].length()){
				return temp[0];
			}else{
				return temp[1];
			}
			
		}else{
			return ss;
		}
	}
	//热点事件入口函数，参数的值域是(-∞,0),其中0表示计算今日的数据生成热点事件，-1表示计算昨日的数据生成热点事件，依此类推
	public static void main(String[]args) throws Exception{
		startMain(0);
	}
}
