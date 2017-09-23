package com.ai.aif.cache.voltdb.core.sync;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionUtils
{
	public static String[] tables;
	public static  String URL;
	public static  String DRIVER ;
	public static  String USERNAME;
	public static  String PASSWORD;

	static
	{
		tables = getTables();
		URL = getValue("url");
		USERNAME = getValue("username");
		PASSWORD = getValue("password");
		DRIVER = getValue("driver");
	}

	public static Properties getProperties()
	{
		Properties prop = new Properties();
		try
		{
			prop.load(ConnectionUtils.class.getClassLoader().getResourceAsStream("oracle.properties"));
		}
		catch (IOException e)
		{
			// 加载文件失败
		}
		return prop;
	}

	public static String getValue(String key)
	{
		Properties prop = getProperties();
		return prop.getProperty(key);
	}

	public static String[] getTables()
	{
        
		return getValue("tables").split(",");

	}

	public static Connection getConnnection() throws IOException
	{

		Connection conn = null;
		try
		{
			Class.forName(DRIVER);
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return conn;
	}

	public static void closeConnection(Connection conn)
	{
		try
		{
			conn.close();
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static ResultSet queryUseOrcl(String sql)
	{
		ResultSet rs = null;
		Connection conn =null;
		try
		{
			conn = getConnnection();
			rs = conn.createStatement().executeQuery(sql);
		}
		catch (Exception e)
		{
			// TODO: handle exception
		}
		/*finally
		{
			if(rs!=null)
			{
				try
				{
					rs.close();
				}
				catch (SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			ConnectionUtils.closeConnection(conn);
		}*/
		return rs;
		
	}

}
