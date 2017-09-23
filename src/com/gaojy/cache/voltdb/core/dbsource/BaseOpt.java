package com.gaojy.cache.voltdb.core.dbsource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import com.gaojy.cache.voltdb.core.utils.AIUtils;

/**
 * @Title: DAO的基类
 * @Description:
 * @Author: gaojy
 * @Version: 1.0
 */
public abstract class BaseOpt
{
	private AIBasicDataSource dataSource = new AIBasicDataSource();
	
	

	/**
	 * 获取数据库，类型
	 * 
	 * @return
	 */
	abstract String getDBType();

	/**
	 * 获取连接
	 * 
	 * @return Connection
	 */
	public Connection getConnection() throws SQLException
	{
		Connection conn = dataSource.getConnection(getDBType());

		if (null == conn)
		{
			throw new SQLException("Get connection failed!");
		}
		
		AIUtils.setConnection(conn);
		
		return conn;
	}

	/**
	 * 获取Statement对象
	 * 
	 * @return Statement
	 * @throws SQLException
	 */
	public Statement createStatement() throws SQLException
	{
		Connection con = getConnection();

		return con.createStatement();
	}

	/**
	 * 获取PreparedStatement对象
	 * 
	 * @return Statement
	 * @throws SQLException
	 */
	public PreparedStatement createPreparedStatement(String sql, Object... parameters) throws SQLException
	{
		Connection con = getConnection();

		if (null == con)
		{
			throw new SQLException("Get connection failed!");
		}

		PreparedStatement pStatement = con.prepareStatement(sql);

		int i = 1;

		if (null != parameters)
		{
			for (Object obj : parameters)
			{
				pStatement.setObject(i, obj);
				i++;
			}
		}
		return pStatement;
	}
	/**
	 * 关闭当前连接
	 */
	public static void close()
	{
		Connection con = AIUtils.getConnection();
		
		try
		{
			con.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		finally
		{
			AIUtils.remove();
		}
	}
}
