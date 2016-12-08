package test;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import optimization.EventData;
import wGroup.WordGraph;
import dataProcess.DBConn;
import dataProcess.HotTopic_BackEnd;
import dataProcess.MongoDB;
import dataProcess.TEXT_ARTICLE;

public class getCenter {
	public static String getCenterSentence(List<String> sentences){
		int center=0;
		double maxScore=0;
		for(int i=0;i<sentences.size();i++){
			double score=0;
			for(int j=0;j<sentences.size();j++){
				if(i==j){
					continue;
				}
				score+=getSimilarityRatio(sentences.get(i),sentences.get(j));
			}
			if(score>maxScore){
				center=i;
				maxScore=score;
			}
		}
		return sentences.get(center);
	}
	
	public double simantic(String s1,String s2){
		return SimilarDegree(s1, s2);
	}
	
	public static String similarityResult(double resule){  
		  
        return  NumberFormat.getPercentInstance(new Locale( "en ", "US ")).format(resule);  
  
    }  
  
      
	static String[]keyWords = {"蔡文英"};
	static String key = "台湾领导人选举结果";
    /** 
 
     * 相似度比较 
 
     * @param strA 
 
     * @param strB 
 
     * @return 
 
     */  
  
    public static double SimilarDegree(String strA, String strB){  
  
        String newStrA = removeSign(strA);  
  
        String newStrB = removeSign(strB);  
  
        int temp = Math.max(newStrA.length(), newStrB.length());  
  
        int temp2 = longestCommonSubstring(newStrA, newStrB).length();  
  
        return temp2 * 1.0 / temp;  
  
    }  
  
      
  
    private static String removeSign(String str) {  
  
        StringBuffer sb = new StringBuffer();  
  
        for (char item : str.toCharArray())  
  
            if (charReg(item)){  
  
                //System.out.println("--"+item);  
  
                sb.append(item);  
  
            }  
  
        return sb.toString();  
  
    }  
  
  
  
    private static boolean charReg(char charValue) {  
  
        return (charValue >= 0x4E00 && charValue <= 0X9FA5)  
  
                || (charValue >= 'a' && charValue <= 'z')  
  
                || (charValue >= 'A' && charValue <= 'Z')  
  
                || (charValue >= '0' && charValue <= '9');  
  
    }  
  
    public static String missionFilter(String str){
    	for(String ss:keyWords){
    		if(str.contains(ss)){
    			return key;
    		}
    	}
    	return str;
    }
  
    private static String longestCommonSubstring(String strA, String strB) {  
  
        char[] chars_strA = strA.toCharArray();  
  
        char[] chars_strB = strB.toCharArray();  
  
        int m = chars_strA.length;  
  
        int n = chars_strB.length;  
  
        int[][] matrix = new int[m + 1][n + 1];  
  
        for (int i = 1; i <= m; i++) {  
  
            for (int j = 1; j <= n; j++) {  
  
                if (chars_strA[i - 1] == chars_strB[j - 1])  
  
                    matrix[i][j] = matrix[i - 1][j - 1] + 1;  
  
                else  
  
                    matrix[i][j] = Math.max(matrix[i][j - 1], matrix[i - 1][j]);  
  
            }  
  
        }  
  
        char[] result = new char[matrix[m][n]];  
  
        int currentIndex = result.length - 1;  
  
        while (matrix[m][n] != 0) {  
  
            if (matrix[m][n] == matrix[m][n - 1])  
  
                n--;  
  
            else if (matrix[m][n] == matrix[m - 1][n])   
  
                m--;  
  
            else {  
  
                result[currentIndex] = chars_strA[m - 1];  
  
                currentIndex--;  
  
                n--;  
  
                m--;  
  
            }  
        }  
  
        return new String(result);  
  
    }
    
    private static int compare(String str, String target) {
        int d[][]; // 矩阵
        int n = str.length();
        int m = target.length();
        int i; // 遍历str的
        int j; // 遍历target的
        char ch1; // str的
        char ch2; // target的
        int temp; // 记录相同字符,在某个矩阵位置值的增量,不是0就是1
        
        if (n == 0) {
            return m;
        }
        
        if (m == 0) {
            return n;
        }
        
        d = new int[n + 1][m + 1];
        
        for (i = 0; i <= n; i++) { // 初始化第一列
            d[i][0] = i;
        }
        
        for (j = 0; j <= m; j++) { // 初始化第一行
            d[0][j] = j;
        }
        
        for (i = 1; i <= n; i++) { // 遍历str
            ch1 = str.charAt(i - 1);
            // 去匹配target
            for (j = 1; j <= m; j++) {
                ch2 = target.charAt(j - 1);
                if (ch1 == ch2) {
                    temp = 0;
                } else {
                    temp = 1;
                }
                
                // 左边+1,上边+1, 左上角+temp取最小
                d[i][j] = min(d[i - 1][j] + 1, d[i][j - 1] + 1, d[i - 1][j - 1] + temp);
            }
        }
        
        return d[n][m];
    }
    
    private static int min(int one, int two, int three) {
        return (one = one < two ? one : two) < three ? one : three;
    }
    
    /**
     * 获取两字符串的相似度
     * 
     * @param str
     * @param target
     * 
     * @return
     */
    
    public static float getSimilarityRatio(String str, String target) {
        return 1 - (float) compare(str, target) / Math.max(str.length(), target.length());
        
    }
    
    public static void main(String[]args) throws Exception, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
    	Calendar calendar = Calendar.getInstance();
		Date today = calendar.getTime();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String today_str = df.format(today);
		MongoDB mgdb = new MongoDB();
		DBConn db = DBConn.getDB();
		String sql="select * from topic where updateTime='"+today_str+"' and typeId=1";
		ResultSet dbData = db.ExecuteGeneralSQLQuery(sql);
		
		String outputfile="log\\log"+today_str+".txt";
		FileWriter fw = new FileWriter(outputfile);
		
		while(dbData.next()){
			String id = dbData.getString(2);
			List<HotTopic_BackEnd> dataList= mgdb.getTopicById(id);
			HotTopic_BackEnd hb = new HotTopic_BackEnd();
			if(dataList.size()==1){
				hb=dataList.get(0);
			}else{
				System.out.println("在MONGODB里找不到相应TOPICID");
				continue;
			}
			Map<String, TEXT_ARTICLE> articles = hb.getT_ARTICLE();
			List<String> sentences = new ArrayList<String>();
			int tempcount=0;
			for(String ss:articles.keySet()){
				if(tempcount++<20){
					System.out.println("########"+articles.get(ss).getA_TITLE());
				}
				sentences.add(articles.get(ss).getA_TITLE());
			}
			System.out.println("中心句是："+getCenterSentence(sentences));
		}
		db.CloseConnection();
    }
}
