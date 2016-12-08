package test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dataProcess.DBConn;
import dataProcess.EntityDAO;
import dataProcess.HotTopic_BackEnd;
import dataProcess.MongoDB;

public class Filt {
	//过滤合并的话题#########################
	public static void main(String[]args) throws Exception, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		deleteEvent();
	}
	
	public static void deleteEvent() throws Exception, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		MongoDB mgdb = new MongoDB();
		DBConn db = DBConn.getDB();
		String sql = "select topicId from new_event";
		ResultSet sss = db.ExecuteGeneralSQLQuery(sql);
		List<String> deleteId = new ArrayList<String>();
		int count=0;
		while(sss.next()){
			List<HotTopic_BackEnd> art= mgdb.getTopicById(sss.getString(1));
			if(art==null||art.size()<1){
				deleteId.add(sss.getString(1));
			}
		}
		sss.close();
		mgdb.close();
		for(String ss:deleteId){
			System.out.println(count++);
			String sql2 = "delete from new_event where topicId='"+ss+"'";
			db.ExecuteSQLUpdate(sql2);
		}
	}
	
//	public static void deleteEntity() throws FileNotFoundException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
//		DBConn db = DBConn.getDB();
//		String sql = "select entityId from entity";
//		ResultSet sss = db.ExecuteGeneralSQLQuery(sql);
//		int count=0;
//		Map<Integer,String> deleteRepeat
//		while(sss.next()){
//			
//			List<EntityDAO> art= mgdb.getEntityById(sss.getInt(1));
//			if(art==null||art.size()<1){
//				deleteId.add(sss.getString(1));
//			}
//		}
//		sss.close();
//		mgdb.close();
//		for(String ss:deleteId){
//			System.out.println(count++);
//			String sql2 = "delete from new_event where topicId='"+ss+"'";
//			db.ExecuteSQLUpdate(sql2);
//		}
//	}
	
	public static void deleteEntity() throws FileNotFoundException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		DBConn db = DBConn.getDB();
		String sql = "delete from entity where polarity is null";
		db.ExecuteSQLUpdate(sql);
	}
	
	//#############################
//	public static void main(String[]args) throws Exception, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
//		MongoDB mgdb = new MongoDB();
//		DBConn db = DBConn.getDB();
//		String sql = "select entityId from entity";
//		ResultSet sss = db.ExecuteGeneralSQLQuery(sql);
//		int count=0;
//		while(sss.next()){
//			System.out.println(count++);
//			mgdb.mongofilter(sss.getInt(1));
//		}
//		sss.close();
//		mgdb.close();
//	}
	
//	public static void main(String[]args) throws Exception, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
//		MongoDB mgdb = new MongoDB();
//		DBConn db = DBConn.getDB();
//		String sql = "select entityId from entity";
//		ResultSet sss = db.ExecuteGeneralSQLQuery(sql);
//		int count=0;
//		while(sss.next()){
//			System.out.println(count++);
//			mgdb.mongofilter(sss.getInt(1));
//		}
//		sss.close();
//		mgdb.close();
//	}
}
