package hotEntity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataProcess.DBConn;

public class EntityProcess {
	
	public static String Longer(String str1,String str2)
	{
		if(str1.length()>=str2.length())return str1;
		else return str2;
	}
	
	public static Map<String,Integer> getTyperAndFilter() throws FileNotFoundException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException, SQLException{
		DBConn db = DBConn.getDB();
		//people
		String peopleSql = "select name from people";
		ResultSet peopleFilter = db.ExecuteGeneralSQLQuery(peopleSql);
		Map<String,Integer> typer = new HashMap<String,Integer>(); 
		while(peopleFilter.next())
		{
			typer.put(peopleFilter.getString("name"),1);
		}
		peopleFilter.close();
		//place
		String placeSql = "select name from place";
		ResultSet placeFilter = db.ExecuteGeneralSQLQuery(placeSql);
		while(placeFilter.next())
		{
			typer.put(placeFilter.getString("name"),2);
		}
		placeFilter.close();
		//place2
		String placeSql2 = "select name from new_place";
		ResultSet placeFilter2 = db.ExecuteGeneralSQLQuery(placeSql2);
		while(placeFilter2.next())
		{
			typer.put(placeFilter2.getString("name"),2);
		}
		placeFilter2.close();
		//organization
		String organizationSql = "select name from organization";
		ResultSet organizationFilter = db.ExecuteGeneralSQLQuery(organizationSql);
		while(organizationFilter.next())
		{
			typer.put(organizationFilter.getString("name"),3);
		}
		organizationFilter.close();
		db.CloseConnection();
		return typer;
	}
	
	public static Map<String,String> getChangeToFilter() throws Exception{
		Map<String,String> changeMap = new HashMap<String,String>();
		DBConn db = DBConn.getDB();
		String sqlf = "select * from changeto";
		ResultSet filter = db.ExecuteGeneralSQLQuery(sqlf);
		while(filter.next()){
			changeMap.put(filter.getString(2), filter.getString(3));
		}
		return changeMap;
	}
	
	public static Map<String,EntityData> geoFilter(Map<String,EntityData> entityMap) throws FileNotFoundException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException, SQLException  {
		DBConn db = DBConn.getDB();
		String sql = "select *from new_place";
		Map<String,GeoData> geoMap = new HashMap<String,GeoData>();
		ResultSet geodata = db.ExecuteGeneralSQLQuery(sql);
		while(geodata.next()){
			if(geoMap.containsKey(geodata.getString(2))){
				GeoData data = geoMap.get(geodata.getString(2));
				if(data.getLevel()>geodata.getInt(4)){
					GeoData tempData = new GeoData(geodata.getString(2),geodata.getString(3),geodata.getInt(4),geodata.getString(5),geodata.getString(6));
					geoMap.put(geodata.getString(2), tempData);
				}
			}else{
				GeoData tempData = new GeoData(geodata.getString(2),geodata.getString(3),geodata.getInt(4),geodata.getString(5),geodata.getString(6));
				geoMap.put(geodata.getString(2), tempData);
			}
		}
		for(String ss:entityMap.keySet()){
			if(entityMap.get(ss).getPosNum()==2){
				String nojin = ss.split("##")[0];
				if(geoMap.containsKey(nojin)){
					String parent = geoMap.get(nojin).getParentName();
					if(!parent.equals("0")){
						EntityData tempED = entityMap.get(ss);
						tempED.setName(nojin+"("+parent+")");
						entityMap.put(ss, tempED);
//						entityMap.remove(parent);
					}
				}
			}
		}
//		for(String ss:removeList){
//			PPOMap.remove(ss);
//		}
		db.CloseConnection();
		return entityMap;
	}
	
	public static Map<String,Integer> getFilter() throws FileNotFoundException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException, SQLException{
		DBConn db = DBConn.getDB();
		Map<String,Integer> result = new HashMap<String,Integer>();
		String sql = "select * from hotwords_filter where typeId=4";
		ResultSet filter = db.ExecuteGeneralSQLQuery(sql);
		while(filter.next()){
			String str = filter.getString(2);
			int classid = filter.getInt(3);
			result.put(str,classid);
		}
		db.CloseConnection();
		return result;
	}
}
