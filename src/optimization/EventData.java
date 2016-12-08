package optimization;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import dataProcess.TEXT_ARTICLE;

public class EventData {
	Map<String,Integer> keyWords = new HashMap<String,Integer>();
	List<String> splitSentences = new ArrayList<String>();
	private String[] replaceFilter = {"【图】","(图)","(gif)","(gif/en)","(组图)","中国侨网-","-中国侨网","（图）","#","\"","“","”","，",",","《","》","(",")","（","）"};
	List<String> originSentences = new ArrayList<String>();
	//一些预处理信息，包括过滤垃圾信息，提取主题词等等
	public void preImfoProcessing(String sentence){
		//过滤垃圾信息
		for(String ss:replaceFilter){
			sentence = sentence.replace(ss, "");
		}
		//提取主题词
		String[] keys = sentence.split("\\|");
		if(keys.length>1){
			sentence = keys[0];
			for(int i=1;i<keys.length;i++){
				if(keys[i].length()>4){
					//System.out.println("EventData.keyWords："+keys[i]+".length>4");
				}
				if(keyWords.containsKey(keys[i])){
					int count = keyWords.get(keys[i]);
					count++;
					keyWords.put(keys[i], count);
				}else{
					keyWords.put(keys[i], 1);
				}
			}
		}
		//根据标点符号分句
		String[] aftersplit = sentence.split("？|\\?|。|!|！");
		for(String ss:aftersplit){
			String[]aftersplit2 = ss.split("\\s+| ");
			if(aftersplit2.length>1){
				for(int i=0;i<aftersplit2.length-1;i++){
					if(aftersplit2[i].length()<1){
						aftersplit2[i] = "";
						continue;
					}
					if(aftersplit2[i+1].length()<1){
						aftersplit2[i+1] = aftersplit2[i];
						aftersplit2[i]="";
						continue;
					}
					if((!isChinese(aftersplit2[i].charAt(aftersplit2[i].length()-1)))&&(!isChinese(aftersplit2[i+1].charAt(0)))){
						aftersplit2[i+1] = aftersplit2[i]+" "+aftersplit2[i+1];
						aftersplit2[i] = "";
					}
				}
			}
			boolean x = false;
			for(int i=0;i<aftersplit2.length;i++){
				if(aftersplit2[i].equals("")){
					continue;
				}
				if(x){
					aftersplit2[i]="#"+aftersplit2[i];
				}
				splitSentences.add(aftersplit2[i]);
				x = true;
			}
		}
	}
	
	public static boolean isChinese(char a) { 
		int v = (int)a; 
		return (v >=19968 && v <= 171941); 
	}
	
	public void getData(Map<String, TEXT_ARTICLE> articles){
		int allCount=0;
		int colonCount=0;
		for(String ss:articles.keySet()){
			allCount++;
			originSentences.add(articles.get(ss).getA_TITLE());
			if(articles.get(ss).getA_TITLE().contains(":|：")){
				colonCount++;
			}
		}
		if(colonCount*2<allCount){
			colonProcessing();
		}
		for(String ss:originSentences){
			preImfoProcessing(ss);
		}
	}
	
	public void colonProcessing(){
		for(int j=0;j<originSentences.size();j++){
			if(originSentences.get(j).contains(":|：")){
				String []tempArray = originSentences.get(j).split(":|：");
				int longerIndex = 0;
				int longerLength = 0;
				for(int i=0;i<tempArray.length;i++){
					if(tempArray[i].length()>longerLength){
						longerIndex=i;
						longerLength = tempArray[i].length();
					}
				}
				originSentences.set(j, tempArray[longerIndex]);
			}
		}
	}
	
	public String matchSymbol(String startSymbol){
		return "";
	}
	
	public Map<String, Integer> getKeyWords() {
		return keyWords;
	}

	public void setKeyWords(Map<String, Integer> keyWords) {
		this.keyWords = keyWords;
	}

	public List<String> getSplitSentences() {
		return splitSentences;
	}

	public void setSplitSentences(List<String> splitSentences) {
		this.splitSentences = splitSentences;
	}
	
	public static void main(String[]args){
		
	}
}
