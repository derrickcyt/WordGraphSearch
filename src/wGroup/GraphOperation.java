package wGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GraphOperation {
	String[] posEqual = {"j","t","v","","","","","","",""};
	String[] posStart = {"v","","","","","","",""};
	static String[] stopWords = {"��/p","��/a","��/p","��/e","��/e","��/p","��/c"};
//	public static boolean importanceJudgement(String wordAndPos){
//		String[] afterSeg = wordAndPos.split("/");
//		String name = "";
//		String pos="";
//		if(afterSeg.length<2){
//			return false;
//		}else if(afterSeg.length>2){
//			for(int i=0;i<afterSeg.length-2;i++){
//				name+=afterSeg[i];
//			}
//			pos = afterSeg[afterSeg.length-1];
//			System.out.println("�ԡ�/�����зִʺ󣬳��ֶ��б�ܣ�"+name+"____"+pos);
//		}else{
//			name = afterSeg[0];
//			pos = afterSeg[1];
//		}
//		if(pos.equals("v")||pos.startsWith("n")){
//			return true;
//		}
//		return false;
//	}
	public static boolean importanceJudgement(String wordAndPos){
		List<String> stopwordList = new ArrayList<String>();
		for(String ss:stopWords){
			stopwordList.add(ss);
		}
		if(stopwordList.contains(wordAndPos)){
			return false;
		}
		return true;
	}
	
	
}
