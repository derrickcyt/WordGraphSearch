package centerSentence;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataProcess.ReadWriteTxt;

public class SynonmyWord {
	public Map<String,String> synonmy = new HashMap<String,String>();
	
	private void getSynonmyDic(){
		try {
			List<String> lines = ReadWriteTxt.readLines("synonym//syndic.txt");
			for(String ss:lines){
				String[] array = ss.split("\\s+");
				System.out.println(array.length);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
