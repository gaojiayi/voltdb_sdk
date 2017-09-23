package com.ai.aif.cache.voltdb.core.client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionUtils {
	public static Connection getConnection() throws ClassNotFoundException,
			SQLException {
		Connection conn = null;
		String userName = "program";
		String password = "";
		String url = "jdbc:voltdb://192.168.72.127:21212";
		String driver = "org.voltdb.jdbc.Driver";
		Class.forName(driver);
		conn = DriverManager.getConnection(url);

		return conn;
	}
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		System.out.println(getConnection());
	}
}
