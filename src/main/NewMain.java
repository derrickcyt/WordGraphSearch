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

import optimization.EventData;
import test.getCenter;
import wGroup.WordGraph;

import ltpTools.Word;

import centerSentence.GetCenterSentence;
import dataProcess.DBConn;
import dataProcess.HotTopic_BackEnd;
import dataProcess.MongoDB;
import dataProcess.TEXT_ARTICLE;

public class NewMain {
	public static void main(String[]args) throws Exception{
		String outputfile="log\\logcentersentence.txt";
		FileWriter fw = new FileWriter(outputfile);
		MongoDB mgdb = new MongoDB();
		//start calculate
		long startTime=1452500000000L;
		long endTime = 1452600000000L;
		int count=0;
		List<HotTopic_BackEnd> dataList= mgdb.getArticleByDateL(startTime, endTime);
		for(HotTopic_BackEnd hb:dataList){
			Map<String, TEXT_ARTICLE> articles = hb.getT_ARTICLE();
			if(articles.size()<5){
				continue;
			}
			System.out.println("count="+count++);
			fw.append("id:"+hb.getTOPIC_ID());
			System.out.println("id:"+hb.getTOPIC_ID());
			int maxArticles = 0;
			List<String> titles = new ArrayList<String>();
			for(String ss:articles.keySet()){
				if(maxArticles++>50){
					break;
				}
				fw.append("########"+articles.get(ss).getA_TITLE()+"\n");
				//System.out.println("########"+articles.get(ss).getA_TITLE());
				titles.add(articles.get(ss).getA_TITLE());
			}
			GetCenterSentence test = new GetCenterSentence(titles);
			List<String> abc = GetCenterSentence.getTopKSentence(test.ltpTitles,1,1);
			List<String> abc2 = GetCenterSentence.getTopKSentence(test.ltpTitles,1,3);
			for(String ss:abc){
				fw.append("BasicMethod:"+ss);
				System.out.println("BasicMethod:"+ss);
			}
			for(String ss:abc2){
				fw.append("HownetMethod:"+ss);
				System.out.println("HownetMethod:"+ss);
			}
		}
		fw.flush();
		fw.close();
	}
}
