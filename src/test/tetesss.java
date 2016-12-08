package test;

import hotEntity.GeoData;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import dataProcess.DBConn;

public class tetesss {
	public static void main(String[]args) throws Exception, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException{
		DBConn db = DBConn.getDB();
		String sql = "select *from new_place";
		Map<String,GeoData> geoMap = new HashMap<String,GeoData>();
		ResultSet geodata = db.ExecuteGeneralSQLQuery(sql);
		while(geodata.next()){
			if(geoMap.containsKey(geodata.getString(2))){
				GeoData data = geoMap.get(geodata.getString(2));
				if(data.getLevel()>geodata.getInt(4)){
					GeoData tempData = new GeoData(geodata.getString(2),geodata.getString(3),geodata.getInt(4),geodata.getString(5),geodata.getString(6));
					System.out.println(geodata.getString(2)+"->"+geodata.getString(6));
					geoMap.put(geodata.getString(2), tempData);
				}
			}else{
				GeoData tempData = new GeoData(geodata.getString(2),geodata.getString(3),geodata.getInt(4),geodata.getString(5),geodata.getString(6));
				geoMap.put(geodata.getString(2), tempData);
			}
		}
		for(String ss:geoMap.keySet()){
			if(geoMap.get(ss).getName().equals(geoMap.get(ss).getParentName())){
				System.out.println(ss);
			}
		}
	}
}
