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

import ltpTools.LtpOnline;

import centerSentence.GetCenterSentence;

import optimization.EventData;
import test.getCenter;
import wGroup.WordGraph;
import dataProcess.DBConn;
import dataProcess.HotTopic_BackEnd;
import dataProcess.MongoDB;
import dataProcess.TEXT_ARTICLE;

public class EventMain {
	//�¼����������������������date�������ڼ����������Դ��ѡ��ʱ�䣬0��ʾѡ���������ݣ�-1��ʾѡ����������ݣ�-2��ʾѡ��ǰ������ݣ���������
	public static void startMain(int date) throws Exception{
		//��ȡ�ȵ��¼����ݻ�ȡ
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, date);
		Date today = calendar.getTime();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String today_str = df.format(today);
		String outputfile="log\\log"+today_str+".txt";
		FileWriter fw = new FileWriter(outputfile);//��־����ļ�
		MongoDB mgdb = new MongoDB();//mongodb��ʼ��
		DBConn db = DBConn.getDB();//mysql��ʼ��
		String sql="select * from topic where updateTime='"+today_str+"' and typeId=1 and hot>3";
		System.out.println(sql);
		ResultSet dbData = db.ExecuteGeneralSQLQuery(sql);//��mysql���ݿ��ж�ȡ�ȵ㻰�������
		int count=0;
		//��ÿһ����ȡ�Ļ������ݽ��м��㣬����ÿ�������Ӧ���ȵ��¼������
		while(dbData.next()){
			String id = dbData.getString(2);//��ȡ����ID
			List<HotTopic_BackEnd> dataList= mgdb.getTopicById(id);//����mysql�еĻ���id����mongo���ݿ��ж�ȡ�����������Ϣ
			HotTopic_BackEnd hb = new HotTopic_BackEnd();
			if(dataList.size()==1){
				hb=dataList.get(0);
			}else if(dataList.size()>1){
				fw.append("MONGODB���ж��TOPICID\n");
				System.out.println("MONGODB���ж��TOPICID");
				continue;
			}else{
				fw.append("��MONGODB���Ҳ�����ӦTOPICID\n");
				System.out.println("��MONGODB���Ҳ�����ӦTOPICID");
				continue;
			}
			//�¼�����������㷨
			WordGraph god=new WordGraph();//��ͼ��ʼ��
			
			Map<String, TEXT_ARTICLE> articles = hb.getT_ARTICLE();//��ȡ�����µ�����id
			if(articles.size()<10){//�����������С��10�Ͳ����м���
				continue;
			}
			fw.append(count+++"\n");
			System.out.println(count++);
			
			EventData OptEvent = new EventData();//�¼����ݴ�����ĳ�ʼ�����¼������࣬�����Ծ��ӵ�Ԥ�����Ը���������ŵĴ����������ַ������жϵȵ�
			
			fw.append("id:"+hb.getTOPIC_ID());
			List<String> titles = new ArrayList<String>();
			for(String ss:articles.keySet()){
				String sentence = articles.get(ss).getA_TITLE();//��ȡ���±���
				sentence = eventTitleFilter(sentence);//�������
				titles.add(sentence);
			}
			//��ȡ��ѡ��
			GetCenterSentence test = new GetCenterSentence(titles);//�øû����µľ��ӳ�ʼ����ѡ��������
			List<String> centerSentences = GetCenterSentence.getTopKSentence(test.ltpTitles,20,3);//��ȡ�����У����������������Ƶ�20�����⣬����3���ͼ���������
			for(String ss:centerSentences){
				fw.append("########"+ss+"\n");
				System.out.println("########"+ss);
				OptEvent.preImfoProcessing(ss);//������ϢԤ����
			}
			//�ú�ѡ���ӽ��д�ͼ����
			for(String ss:OptEvent.getSplitSentences()){
				god.AddCandidateToGraph(ss);//����ͼ�в������
			}
			fw.append("�㣺"+god.nodes.size()+"\n");
			fw.append("�ߣ�"+god.edges.size()+"\n");
			System.out.println("�㣺"+god.nodes.size());
			System.out.println("�ߣ�"+god.edges.size());
			//�Թ�����Ĵ�ͼ���м����������ҳ����ŵ�10��·��
			List<List<Integer>> testt = god.getBestNPath(10);//�����������Ĳ�����Ϊ10��Ѱ�����ŵ�10��·��
			String combineWords = "";
			String firstWords = "";
			//�����ɵ�����·�������ν�������䷨�������������Ԥ����ľ䷨��ʽ���ͽ�����Ϊ�ȵ��¼������
			for(int x=0;x<testt.size();x++){
				//��·��ת��Ϊ����
				for(int j:testt.get(x)){
					if(j==-1||j==0||j==1)continue;
					String[]tempArray =god.nodes.get(j).wordString.split("/");
					for(int k=0;k<tempArray.length-1;k++){
						combineWords+=tempArray[k];
					}
				}
				//�����һ�����ӣ�������о��Ӷ�������Ԥ����ľ䷨��ʽ���ͻ�ԭ��һ������Ϊ�¼��������
				if(x==0){
					firstWords = combineWords;
				}
				combineWords = LtpOnline.ltpAnalysis(LtpOnline.doltp(combineWords));//�����ɵı������LTP������������ʽ�����¼����ж�����������Ϊ��Ϊ���⣬����������������һ�����⣬֪����һ������������������ȫ����������ѡ���һ��������Ϊ���ս��
				System.out.println(combineWords);
				//				if(eventFilter.mysqlFilter(filterMap,temp))continue;
				if(combineWords.equals("false"))
				{
					combineWords="";
					continue;
				}else{
					break;
				}
			}
			if(combineWords==""){
				combineWords = firstWords;
			}
			//combineWords = getCenter.missionFilter(combineWords);
			fw.append(combineWords+"\n");
			System.out.println("�����"+combineWords);
			if(combineWords.length()<4){
				System.out.println("#########ERROR#########");
				continue;
			}
			//�洢����
			String findSql = "select * from new_event where topicId='"+dbData.getString(2)+"'";
			ResultSet rs=db.ExecuteGeneralSQLQuery(findSql);
			if(rs.next()==false){
				String insertSql = "insert into new_event (topicId,name,createTime,updateTime,typeId,classifyId,hot,textCount,todayCount,childCount,polar)values(?,?,?,?,?,?,?,?,?,?,?)";
				PreparedStatement ps = db.getPstmt(insertSql);
				ps.setString(1, dbData.getString(2));
				ps.setString(2, combineWords);
				ps.setString(3, dbData.getString(4));
				ps.setString(4, dbData.getString(5));
				ps.setInt(5, dbData.getInt(6));
				ps.setInt(6, dbData.getInt(7));
				ps.setFloat(7, dbData.getFloat(8));
				ps.setInt(8, dbData.getInt(9));
				ps.setInt(9, dbData.getInt(10));
				ps.setInt(10, dbData.getInt(11));
				ps.setFloat(11, dbData.getFloat(12));
				ps.execute();
				ps.close();
			}else{
				String updateSql = "update new_event set name=?,createTime=?,updateTime=?,typeId=?,classifyId=?,hot=?,textCount=?,todayCount=?,childCount=?,polar=? where topicId='"+dbData.getString(2)+"'";
				PreparedStatement ps = db.getPstmt(updateSql);
				ps.setString(1, combineWords);
				ps.setString(2, dbData.getString(4));
				ps.setString(3, dbData.getString(5));
				ps.setInt(4, dbData.getInt(6));
				ps.setInt(5, dbData.getInt(7));
				ps.setFloat(6, dbData.getFloat(8));
				ps.setInt(7, dbData.getInt(9));
				ps.setInt(8, dbData.getInt(10));
				ps.setInt(9, dbData.getInt(11));
				ps.setFloat(10, dbData.getFloat(12));
				ps.execute();
				ps.close();
			}
		}
		fw.flush();
		fw.close();
		db.CloseConnection();
	}
	
	public static String eventTitleFilter(String sentence){
		sentence = dealColon(sentence);
		return sentence;
	}
	
	public static String dealColon(String ss){
		String []temp = ss.split(":|��");
		if(temp.length==2){
			if(temp[0].length()>temp[1].length()){
				return temp[0];
			}else{
				return temp[1];
			}
			
		}else{
			return ss;
		}
	}
	//�ȵ��¼���ں�����������ֵ����(-��,0),����0��ʾ������յ����������ȵ��¼���-1��ʾ�������յ����������ȵ��¼�����������
	public static void main(String[]args) throws Exception{
		startMain(0);
	}
}
