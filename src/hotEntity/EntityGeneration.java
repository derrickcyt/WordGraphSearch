package hotEntity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataProcess.DBConn;

import ltpTools.LtpService;
import ltpTools.Word;

public class EntityGeneration {
	private static Map<String,Integer> POSMap = new HashMap<String,Integer>();
	private static Map<String,Integer> TYPEMap = new HashMap<String,Integer>();
	private static Map<String,String> CHANGETOMap = new HashMap<String,String>();
	private static Map<String,Integer> FILTERMap = new HashMap<String,Integer>();
	static{
		POSMap.put("nh", 1);
		POSMap.put("ns", 2);
		POSMap.put("ni", 3);
		try {
			TYPEMap = EntityProcess.getTyperAndFilter();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			CHANGETOMap = EntityProcess.getChangeToFilter();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			FILTERMap = EntityProcess.getFilter();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<EntityData> getCandidate(List<String> sentences,List<String> sentenceIds,int topk) throws Exception, Exception, IllegalAccessException, ClassNotFoundException, IOException, SQLException{
		Map<String,EntityData> result = new HashMap<String,EntityData>();
		for(int i=0;i<sentences.size();i++){
			List<Word> afterSegment = LtpService.segmentation(sentences.get(i));
			for(Word ww:afterSegment){
				int posnum = entityJudgement(ww);
				if(posnum==0){
					continue;
				}
				if(CHANGETOMap.containsKey(ww.getContent())){
					ww.setContentByOne(CHANGETOMap.get(ww.getContent()));
				}
				if(result.containsKey(ww.getContentByOne()+"##"+posnum)){
					result.get(ww.getContentByOne()+"##"+posnum).addIds(sentenceIds.get(i));
					result.get(ww.getContentByOne()+"##"+posnum).updateFrequency();
					result.get(ww.getContentByOne()+"##"+posnum).addTitles(sentences.get(i));
				}else{
					EntityData tempData = new EntityData(ww.getContentByOne(),posnum,1,sentenceIds.get(i),sentences.get(i));
					result.put(ww.getContentByOne()+"##"+posnum, tempData);
				}
			}
		}
		result = EntityProcess.geoFilter(result);
		List<EntityData> resultList = new ArrayList<EntityData>();
		for(String ss:result.keySet()){
			resultList.add(result.get(ss));
		}
		
		Collections.sort(resultList, new Comparator<EntityData>(){ 
		   public int compare(EntityData mapping1,EntityData mapping2){
			   if(mapping1.getFrequency()<mapping2.getFrequency()){
				   return 1;
			   } 
			   return -1; 
		   } 
		});
		if(resultList.size()>topk){
			resultList = resultList.subList(0, topk);
		}
		return resultList;
	}
	
	private int entityJudgement(Word word){
		if(word.getContentByOne().length()<2){
			return 0;
		}
		String POS = word.getPos();
		if(POSMap.containsKey(POS)){
			if(FILTERMap.containsKey(word.getContentByOne())){
				if(FILTERMap.get(word.getContentByOne())==POSMap.get(POS)){
					return 0;
				}
			}
			if(word.getContentByOne().length()==2&&word.getContentByOne().endsWith("某")&&POS.equals("nh")){
				return 0;
			}
			if(word.getContentByOne().length()==2&&word.getContentByOne().endsWith("媒")&&POS.equals("nh")){
				return 0;
			}
			if(word.getContentByOne().length()==2&&word.getContentByOne().startsWith("老")&&POS.equals("nh")){
				return 0;
			}
			if(word.getContentByOne().length()==2&&word.getContentByOne().startsWith("小")&&POS.equals("nh")){
				return 0;
			}
			if(!isChineseCharacter(word.getContentByOne())){
				return 0;
			}
			if(POS.equals("ns")||POS.equals("ni")){
				if(!TYPEMap.containsKey(word.getContentByOne())){
					return 0;
				}
			}
			return POSMap.get(POS);
		}
		return 0;
	}
	
	public static final boolean isChineseCharacter(String chineseStr) {  
        char[] charArray = chineseStr.toCharArray();  
        for (int i = 0; i < charArray.length; i++) {  
            if ((charArray[i] >= 0x4e00) && (charArray[i] <= 0x9fbb)) {  
                return true;  
            }  
        }  
        return false;  
    }
}
