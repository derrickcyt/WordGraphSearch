package polarityCalculation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;

public class WordMaterial {
	private HashMap<String, Double> polarityWordsMap = new HashMap<String, Double>();
	private HashMap<String, Double> degreeAdvWordMap = new HashMap<String, Double>();
	private HashMap<String, Double> dongTaiCi = new HashMap<String, Double>();
	private HashSet<String> fanZhuanCi = new HashSet<String>();
	
	public WordMaterial(){
		loadPolarityWordMap();
		loadDegreeAdv();
		loadFanZhuanCi();
		loadDongTaiCi();
	}
	
	@SuppressWarnings("resource")
	private void loadPolarityWordMap(){
		File file = new File("./polarityrelate/EmotionWords//HownetAndDic_Total_New_NSW.txt");
		try {
			Reader in = new InputStreamReader(new FileInputStream(file), "utf-8");
			BufferedReader reader = new BufferedReader(in);
			String line;
			while((line = reader.readLine()) != null){
				String[] tmp = line.split("\\s{1,}");
				if(tmp.length == 2){
					polarityWordsMap.put(tmp[0], Double.valueOf(tmp[1]));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("resource")
	private void loadDegreeAdv(){
		File file = new File("./polarityrelate/WordMaterial//degreeAdv.txt");
		try {
			Reader in = new InputStreamReader(new FileInputStream(file), "utf-8");
			BufferedReader reader = new BufferedReader(in);
			String line;
			while((line = reader.readLine()) != null){
				String[] tmp = line.split("\\s{1,}");
				if(tmp.length == 2){
					degreeAdvWordMap.put(tmp[0], Double.valueOf(tmp[1]));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("resource")
	private void loadDongTaiCi(){
		File file = new File("./polarityrelate/WordMaterial//dongTaiCi.txt");
		try {
			Reader in = new InputStreamReader(new FileInputStream(file), "utf-8");
			BufferedReader reader = new BufferedReader(in);
			String line;
			while((line = reader.readLine()) != null){
				String[] tmp = line.split("\\s{1,}");
				if(tmp.length == 2){
					this.dongTaiCi.put(tmp[0], Double.valueOf(tmp[1]));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("resource")
	private void loadFanZhuanCi(){
		File file = new File("./polarityrelate/WordMaterial//fanZhuanCi.txt");
		try {
			Reader in = new InputStreamReader(new FileInputStream(file), "utf-8");
			BufferedReader reader = new BufferedReader(in);
			String line;
			while((line = reader.readLine()) != null){
				this.fanZhuanCi.add(line);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public HashMap<String, Double> getAdvDegreeWordMap() {
		return degreeAdvWordMap;
	}
	public void setAdvDegreeWordMap(HashMap<String, Double> advDegreeWordMap) {
		this.degreeAdvWordMap = advDegreeWordMap;
	}

	public HashMap<String, Double> getPolarityWordsMap() {
		return polarityWordsMap;
	}

	public void setPolarityWordsMap(HashMap<String, Double> polarityWordsMap) {
		this.polarityWordsMap = polarityWordsMap;
	}
	
	public HashMap<String, Double> getDegreeAdvWordMap() {
		return degreeAdvWordMap;
	}

	public HashMap<String, Double> getDongTaiCi() {
		return dongTaiCi;
	}

	public HashSet<String> getFanZhuanCi() {
		return fanZhuanCi;
	}
}
