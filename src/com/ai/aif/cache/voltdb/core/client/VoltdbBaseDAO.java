package com.ai.aif.cache.voltdb.core.client;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;


public class VoltdbBaseDAO {

	/**
	 * µÃµ½connection
	 * 
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static Connection getConnection() throws ClassNotFoundException,
			SQLException {
		return ConnectionUtils.getConnection();
	}

	public static <T> T getBean(Class<T> clazz, String sql) {

		return null;

	}

	public static <T> T[] getBeans(Class<T> clazz, String sql) {
		return null;
	}

	public static void save(String sql) {

	}

	public static ResultSet executeQuery(String sql) {
		return null;

	}

	public static long getRowCount(String sql) {
		return 0;

	}

	public static void batchUpdate(String[] sqls) {

	}

}
