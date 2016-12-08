package dataProcess;

import java.io.IOException;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DBConn {
	
		private String DRIVER;

		private String URL;

		private String USR;

		private String PWD;

		private Connection conn;

		private static DBConn db = null;
		private static Calendar calendar;
		private final static long WAITTIMEOUT = 8L * 3600 * 1000;

		public static DBConn getDB() throws FileNotFoundException, IOException,
				InstantiationException, IllegalAccessException,
				ClassNotFoundException, SQLException {

			synchronized (DBConn.class) {

				Calendar now = Calendar.getInstance();
				if (db != null) {
					if ((now.getTimeInMillis() - calendar.getTimeInMillis()) < WAITTIMEOUT)
						return db;
					else
						db.CloseConnection();
				}

				calendar = Calendar.getInstance();
				db = new DBConn();

				db.init();
				db.CreateConnection();

				return db;

			}
		}

		public static void setNull() {
			db = null;
		}

		public void init() throws FileNotFoundException, IOException {
			URL = "jdbc:mysql://125.216.227.56:3306/monitorsys?&useUnicode=True&autoReconnect=true";
			DRIVER = "com.mysql.jdbc.Driver";
			USR =  "root" ;
			PWD = "123456" ;

		}

		public void CreateConnection() throws InstantiationException,
				IllegalAccessException, ClassNotFoundException, SQLException {
			Class.forName(DRIVER);
			conn = DriverManager.getConnection(this.URL, this.USR, this.PWD);
		}

		public List<Object> ExecuteSQLQuery(String sql) throws SQLException {
			List<Object> resultObject = new ArrayList<Object>();
			Statement stmt = null;
			ResultSet res = null;

			stmt = conn.createStatement();
			res = stmt.executeQuery(sql);

			int i = 0;
			while (res.next()) {
				resultObject.add(res.getObject(++i));
			}

			res.close();
			stmt.close();

			return resultObject;
		}

		public List<String> ExecuteIndusSQLQuery(String sql) throws SQLException {
			List<String> resultObject = new ArrayList<String>();
			Statement stmt = null;
			ResultSet res = null;

			stmt = conn.createStatement();
			res = stmt.executeQuery(sql);

			int i = 0;
			while (res.next()) {
				// resultObject.add(res.getObject(i++));
				resultObject.add(res.getString("id"));
			}

			res.close();
			stmt.close();

			return resultObject;
		}

		public ResultSet ExecuteGeneralSQLQuery(String sql) throws SQLException {
			Statement stmt = null;
			ResultSet res = null;

			stmt = conn.createStatement();
			res = stmt.executeQuery(sql);

			return res;
		}

		public int ExecuteSQLUpdate(String sql) throws SQLException {

			Statement stmt = conn.createStatement();
			int ret = stmt.executeUpdate(sql);
			return ret;

		}

		public PreparedStatement getPstmt(String sql) throws SQLException,
				InstantiationException, IllegalAccessException,
				ClassNotFoundException, FileNotFoundException, IOException {
			if (conn == null) {
				if (db != null)
					db.CreateConnection();
				else
					getDB();
			}
			return conn.prepareStatement(sql);
		}

		public void CloseConnection() throws SQLException {
			if (this.conn != null)
				this.conn.close();
			DBConn.db = null;
		}
		
		public static void InsertPeople(String str) throws FileNotFoundException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException, SQLException
		{
			DBConn dbb = DBConn.getDB();
			String sql = "insert into people(name) values('"+str+"')";
			dbb.ExecuteSQLUpdate(sql);
		}
		
		public static void InsertPlace(String str) throws FileNotFoundException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException, SQLException
		{
			DBConn dbb = DBConn.getDB();
			String sql = "insert into place(name,level) values('"+str+"','"+1+"')";
			dbb.ExecuteSQLUpdate(sql);
		}
		
		public static void InsertOrganization(String str) throws FileNotFoundException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException, SQLException
		{
			DBConn dbb = DBConn.getDB();
			String sql = "insert into organization(name) values('"+str+"')";
			dbb.ExecuteSQLUpdate(sql);
		}
		
		public static void InsertHotwordsFilter(String str,int type) throws FileNotFoundException, InstantiationException, IllegalAccessException, ClassNotFoundException, IOException, SQLException
		{
			DBConn dbb = DBConn.getDB();
			String sql = "insert into hotwords_filter(name,typeId) values('"+str+"','"+type+"')";
			dbb.ExecuteSQLUpdate(sql);
		}
		
	public static void main(String[] args){
		String str = "insert into place(name,level) values('Tom',2);";
		try {
			DBConn db = DBConn.getDB();
			db.ExecuteSQLUpdate(str);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
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
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}