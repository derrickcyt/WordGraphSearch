package test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.ansj.domain.Term;

import Semantic.Segmentation;

import dataProcess.HotTopic_BackEnd;
import dataProcess.MongoDB;
import dataProcess.TEXT_ARTICLE;

public class DataTrainForNGram {
	public static void main(String[]args) throws Exception{
		MongoDB mgdb = new MongoDB();
		Long endTime = System.currentTimeMillis();
		Long startTime = System.currentTimeMillis()-1000*3600*3;
		List<HotTopic_BackEnd> resultList= mgdb.getArticleByDateL(startTime, endTime);
		String outputfile="log\\data.txt";
		FileWriter fw = new FileWriter(outputfile);
		int count = 0;
		for(HotTopic_BackEnd topic:resultList){
			Map<String, TEXT_ARTICLE> articles = topic.getT_ARTICLE();
			count+=articles.size();
			System.out.println(count);
			for(String ss:articles.keySet()){
				String art = articles.get(ss).getA_CONTENT();
				art = art.replaceAll("\\s+|-|¡ª¡ª|_|[|]|¡¾|¡¿|\\{|\\}|\\(|\\)|£¨|£©|:|£º", "");
				String[] aftersplit = art.split("£¬|,|\\.|¡£|!|£¡");
				for(String sss:aftersplit){
					List<Term> terms = Segmentation.toAnalysis(sss);
					for(Term tt:terms){
						fw.append(tt.getName()+" ");
					}
					fw.append("\n");
				}
			}
		}
		fw.flush();
		fw.close();
	}
}
