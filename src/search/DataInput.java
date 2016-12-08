package search;

import java.util.ArrayList;
import java.util.List;

import derrick.io.file.TextReader;
import search.bean.Topic;

public class DataInput {

	public static String testPath="test_data/dataset.txt";
	
	
	public static List<Topic> readTestData(){
		List<Topic> data=new ArrayList<>();
		TextReader reader=new TextReader(testPath, "GBK");
		reader.init();
		
		String line=null;
		int count=0;
		List<String> titles=new ArrayList<String>();
		
		while((line=reader.readLine())!=null){
			if(line.startsWith("########")){
				titles.add(line.replace("########", ""));
			}else if (line.startsWith("------")) {
				data.add(new Topic(titles, titles.size(), count));
				count++;
				titles=new ArrayList<>();
			}
		}
		
		reader.close();
		return data;
	}
	
	public static void main(String[] args) {
		List<Topic> data=readTestData();
		for(Topic t:data){
			System.out.println(t.getTopicId());
			for(String title:t.getTitles()){
				System.out.println(title);
			}
		}
	}
	
	
}
