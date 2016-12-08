package search.lm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LMDBReader implements LMReader {
	private String DRIVER = "com.mysql.jdbc.Driver";
	private String URL = "jdbc:mysql://110.64.66.194:3306/cyt_homework?&useUnicode=True&characterEncoding=utf8";
	private String USR = "root";
	private String PWD = "123456";
	private Connection conn;
	
	

	public Connection getConn() throws SQLException, ClassNotFoundException {
		Class.forName(DRIVER);
		Connection connection = DriverManager.getConnection(URL, USR, PWD);

		return connection;
	}

	public void initConn() {
		if (conn == null) {
			try {
				conn = getConn();
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void closeConn() throws SQLException {
		conn.close();
	}

	@Override
	public float read(String w1, String w2) {
		initConn();
		float p = -1.0f;
		try {
			Statement stmt = conn.createStatement();
			String sql = "select p from two_gram where w1='" + w1 + "' and w2='" + w2 + "'";
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				p = rs.getFloat(1);
			}
			p = (float) Math.pow(10, p);
			return p;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return p;
	}

	@Override
	public float read(String w1) {
		// TODO Auto-generated method stub
		return 0;
	}

	public static void main(String[] args) {
		System.out.println(Math.log(new LMDBReader().read("我", "是","方法")));
	}

	private float defaultP=-5f;
	
	@Override
	public float read(String w1, String w2, String w3) {
		initConn();
		float p = defaultP;
		try {
			Statement stmt = conn.createStatement();
			String sql = "select p from three_gram where w1='" + w1 + "' and w2='" + w2 + "' and w3='" + w3 + "'";
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				p = rs.getFloat(1);
			}
			p = (float) Math.pow(10, p);
			return p;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		p = (float) Math.pow(10, p);
		return p;
	}

}
