package main;

import java.io.FileWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import optimization.EventData;
import wGroup.WordGraph;
import dataProcess.DBConn;
import dataProcess.HotTopic_BackEnd;
import dataProcess.MongoDB;
import dataProcess.TEXT_ARTICLE;

public class Experiment {
//	public static void main(String[]args) throws Exception{
//		MongoDB mgdb = new MongoDB();
//		int dayday = 16;
//		Long startTime = System.currentTimeMillis()-1000*3600*24*dayday;
//		Long endTime = System.currentTimeMillis()-1000*3600*24*(dayday-7);
//		
//		System.out.println("start");
//		List<HotTopic_BackEnd> dataList= mgdb.getArticleByDateL(startTime, endTime);
//		System.out.println("end");
//		
//		int beamsize=1;
//		while(beamsize<1000){
//			String outputfile="log\\log"+beamsize+".txt";
//			FileWriter fw = new FileWriter(outputfile);
//			int allcount = 0;
//			int wenzhangcount=0;
//			int nodesize = 0;
//			int edgesize = 0;
//			long times = 0;
//			for(HotTopic_BackEnd hb:dataList){
//				WordGraph god=new WordGraph();
//				Map<String, TEXT_ARTICLE> articles = hb.getT_ARTICLE();
//				if(articles.size()<10){
//					continue;
//				}
//				allcount++;
//				EventData OptEvent = new EventData();
//				//System.out.println("id:"+hb.getTOPIC_ID());
//				for(String ss:articles.keySet()){
//					fw.append("########"+articles.get(ss).getA_TITLE()+"\n");
//					//System.out.println("########"+articles.get(ss).getA_TITLE());
//					OptEvent.preImfoProcessing(articles.get(ss).getA_TITLE());
//				}
//				for(String ss:OptEvent.getSplitSentences()){
//					god.AddCandidateToGraph(ss);
//				}
//				//System.out.println("点："+god.nodes.size());
//				nodesize+=god.nodes.size();
//				//System.out.println("边："+god.edges.size());
//				edgesize+=god.edges.size();
//				long start=System.currentTimeMillis(); 
//				List<List<Integer>> testt = god.getBestNPath(beamsize);
//				long end=System.currentTimeMillis();
//				times+=(end-start);
//				for(int i=0;i<testt.size();i++){
//					fw.append("第"+i+"句："+"\n");
//					//System.out.print("第"+i+"句：");
//					for(int j:testt.get(i)){
//						if(j==-1||j==0||j==1)continue;
//						fw.append(god.nodes.get(j).wordString);
//						//System.out.print(god.nodes.get(j).wordString);
//					}
//					fw.append("\n");
//					//System.out.println();
//				}
//				fw.append("\n");
//				//System.out.println();
//			}
//			
//			fw.append("beamsize:"+beamsize+"\n");
//			fw.append("程序运行时间:"+times+"ms"+"\n");
//			fw.append("allcount:"+allcount+"\n");
//			fw.append("nodezise:"+nodesize+"\n");
//			fw.append("edgesize:"+edgesize+"\n");
//			fw.flush();
//			System.out.println("beamsize:"+beamsize);
//			System.out.println("程序运行时间:"+times+"ms");
////			System.out.println("allcount:"+allcount);
////			System.out.println("nodezise:"+nodesize);
////			System.out.println("edgesize:"+edgesize);
//			beamsize+=50;
//		}
//	}
//	public static void main(String[]args) throws Exception{
//		MongoDB mgdb = new MongoDB();
//		int dayday = 16;
//		Long startTime = System.currentTimeMillis()-1000*3600*24*dayday;
//		Long endTime = System.currentTimeMillis()-1000*3600*24*(dayday-7);
//		
//		System.out.println("start");
//		List<HotTopic_BackEnd> dataList= mgdb.getArticleByDateL(startTime, endTime);
//		System.out.println("end");
//		
//		int beamsize=1;
//		while(beamsize<1000){
//			String outputfile="log\\log"+beamsize+".txt";
//			int wenzhangcount=0;
//			long times = 0;
//			for(HotTopic_BackEnd hb:dataList){
//				WordGraph god=new WordGraph();
//				Map<String, TEXT_ARTICLE> articles = hb.getT_ARTICLE();
//				if(articles.size()<beamsize){
//					continue;
//				}
//				for(String ss:articles.keySet()){
//					wenzhangcount++;
//				}
//			}
//			System.out.println("wenzhangcount"+wenzhangcount);
//			System.out.println("beamsize:"+beamsize);
//			beamsize+=10;
//		}
//	}
//	public static void main(String[]args) throws Exception{
//		//get data
//		Calendar calendar = Calendar.getInstance();
//		calendar.add(Calendar.DATE, -1);
//		Date today2 = calendar.getTime();
//		calendar.add(Calendar.DATE, -5);
//		Date today1 = calendar.getTime();
//		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//		String today_str1 = df.format(today1);
//		String today_str2 = df.format(today2);
//		//Long limitTime = today.getTime()-1000*3600*24*2;
//		String outputfile="exp\\exp"+today_str1+"2shortpath.txt";
//		FileWriter fw = new FileWriter(outputfile);
//		MongoDB mgdb = new MongoDB();
//		DBConn db = DBConn.getDB();
//		String sql="select * from topic_copy where createTime>='"+today_str1+"' and updateTime<='"+today_str2+"' and typeId=1";
//		System.out.println(sql);
//		ResultSet dbData = db.ExecuteGeneralSQLQuery(sql);
//		int topicCount=0;
//		int articleCount=0;
//		//start calculate
//		while(dbData.next()){
//			String id = dbData.getString(2);
//			List<HotTopic_BackEnd> dataList= mgdb.getArticleById(id);
//			String combineWords = "";
//			for(HotTopic_BackEnd hb:dataList){
//				WordGraph god=new WordGraph();
//				Map<String, TEXT_ARTICLE> articles = hb.getT_ARTICLE();
//				if(articles.size()<20){
//					continue;
//				}else{
//					topicCount++;
//					fw.append(topicCount+"\n");
//					System.out.println(topicCount);
//					articleCount+=articles.size();
//				}
//				EventData OptEvent = new EventData();
//				fw.append("id:"+hb.getTOPIC_ID());
//				System.out.println("id:"+hb.getTOPIC_ID());
//				int z=0;
//				for(String ss:articles.keySet()){
//					z++;
//					if(z>50){
//						break;
//					}
//					if(z<11){
//						fw.append("########"+articles.get(ss).getA_TITLE()+"\n");
//						System.out.println("########"+articles.get(ss).getA_TITLE());
//					}
//					OptEvent.preImfoProcessing(articles.get(ss).getA_TITLE());
//				}
//				
//				for(String ss:OptEvent.getSplitSentences()){
//					god.AddCandidateToGraph(ss);
//				}
//				fw.append("点："+god.nodes.size()+"\n");
//				fw.append("边："+god.edges.size()+"\n");
//				System.out.println("点："+god.nodes.size());
//				System.out.println("边："+god.edges.size());
//				List<List<Integer>> testt = god.getBestNPath(5);
//				for(int j:testt.get(0)){
//					if(j==-1||j==0||j==1)continue;
//					String[]tempArray =god.nodes.get(j).wordString.split("/");
//					for(int k=0;k<tempArray.length-1;k++){
//						combineWords+=tempArray[k];
//					}
//				}
//				fw.append(combineWords+"\n");
//				System.out.println("结果："+combineWords);
//				break;
//			}
//		}
//		System.out.println("articlesCount="+articleCount);
//		fw.append("articlesCount="+articleCount);
//		fw.flush();
//		fw.close();
//		db.CloseConnection();
//	}
	public static void main(String[]args) throws Exception{
		//get data
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -14);
		Date today2 = calendar.getTime();
		calendar.add(Calendar.DATE, -1);
		Date today1 = calendar.getTime();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String today_str1 = df.format(today1);
		String today_str2 = df.format(today2);
		DBConn db = DBConn.getDB();
		String sql="select * from topic where createTime>='"+today_str1+"' and typeId=1";
		System.out.println(sql);
		ResultSet dbData = db.ExecuteGeneralSQLQuery(sql);
		List<HotTopic_BackEnd> dataList = new ArrayList<HotTopic_BackEnd>();
		MongoDB mgdb = new MongoDB();
		while(dbData.next()){
			dataList.addAll(mgdb.getTopicById(dbData.getString(2)));
		}
		System.out.println("dataList.size="+dataList.size());
		db.CloseConnection();
		//getdata finish
		String outputfile="exp\\exp_beam_.txt";
		FileWriter fw = new FileWriter(outputfile);
		int beamsize = 1;
		
		while(beamsize<1000){
			System.out.println("beamsize="+beamsize);
			int topicCount=0;
			int articleCount=0;
			long times = 0;
			//start calculate
				
			for(HotTopic_BackEnd hb:dataList){
				String combineWords = "";
				WordGraph god=new WordGraph();
				Map<String, TEXT_ARTICLE> articles = hb.getT_ARTICLE();
				if(articles.size()<50){
					continue;
				}else{
					topicCount++;
					fw.append(topicCount+"\n");
					articleCount+=articles.size();
				}
				EventData OptEvent = new EventData();
				fw.append("id:"+hb.getTOPIC_ID()+"\n");
				int z=0;
				for(String ss:articles.keySet()){
					z++;
					if(z<11){
						fw.append("########"+articles.get(ss).getA_TITLE()+"\n");
					}
					OptEvent.preImfoProcessing(articles.get(ss).getA_TITLE());
				}
				for(String ss:OptEvent.getSplitSentences()){
					god.AddCandidateToGraph(ss);
				}
				fw.append("点："+god.nodes.size()+"\n");
				fw.append("边："+god.edges.size()+"\n");
				
				long start=System.currentTimeMillis(); 
				List<List<Integer>> testt = god.getBestNPath(beamsize);
				long end=System.currentTimeMillis();
				times+=(end-start);
				for(int j:testt.get(0)){
					if(j==-1||j==0||j==1)continue;
					String[]tempArray =god.nodes.get(j).wordString.split("/");
					for(int k=0;k<tempArray.length-1;k++){
						combineWords+=tempArray[k];
					}
				}
				fw.append(combineWords+"\n");
			}
			System.out.println("times="+times);
			System.out.println("topicCount="+topicCount);
			System.out.println("articlesCount="+articleCount);
			fw.append("beamsize="+beamsize+"#####################\n");
			if(beamsize<100){
				beamsize+=5;
			}else{
				beamsize+=200;
			}
		}
		fw.flush();
		fw.close();
	}
}
