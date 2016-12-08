package dataProcess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadWriteTxt {
	public static void toTxtReplace(String str,String file) throws IOException{
		File f=new File(file);
		if(!f.exists()) f.createNewFile();
		FileWriter fw=new FileWriter(file);
		fw.write(str);
		fw.close();		
	}
	public static void toTxtAppend(String str,String file) throws IOException{
		File f=new File(file);
		if(!f.exists()) f.createNewFile();
		BufferedWriter bw=new BufferedWriter(new FileWriter(file,true));
		bw.append("\n"+str);
		bw.close();
	}
	
	public static List<String> readLines(String file) throws IOException{
		BufferedReader fr=new BufferedReader(new FileReader(file));
		String str=null;
		List<String> list=new ArrayList<String>();
		while((str=fr.readLine())!=null)
			list.add(str);
		return list;
	}
}
