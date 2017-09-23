package com.ai.aif.cache.voltdb.core.sync;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.ai.aif.cache.voltdb.core.io.IOFactory;
import com.ai.aif.cache.voltdb.core.policy.LoadPoolFactory;
import com.ai.aif.cache.voltdb.core.pool.VoltDBObjectPool;
import com.ai.aif.cache.voltdb.core.pool.VoltDBObjectPoolFactory;

/**
 * voltdb初始化导入数据及数据同步
 * 
 * @Title:
 * @Description:
 * @Copyright: Copyright (c) 2016 CMC CRM TA Dept-NJ All Rights Reserved
 * @Company: Asiainfo
 * @Author: gaojy5
 * @Version: 1.0
 */
public class Oracle2VoltdbInit
{
	// 获得所有表名
	public static String[] tables = ConnectionUtils.getTables();

	public static void importDataFirst() throws Exception
	{
		
		VoltDBObjectPool pool = LoadPoolFactory.getInstance().getPool();
		Connection connOfVolt = (Connection) (pool.borrowObject());
		Connection connOfOrcl = ConnectionUtils.getConnnection();

		try
		{
			//connOfVolt.setAutoCommit(false);
			for (String table : tables)
			{
				connOfVolt.createStatement().execute("DELETE FROM " + table.trim());
				ResultSet rs = connOfOrcl.createStatement().executeQuery("SELECT * FROM " + table.trim());
				rsToVoltdb(rs, connOfVolt, table);
			}
			//connOfVolt.commit();
		}
		catch (Exception e)
		{
			throw e;

		}
		finally
		{
			if (connOfVolt != null)
			{
				ConnectionUtils.closeConnection(connOfVolt);
			}
			if (connOfOrcl != null)
			{
				ConnectionUtils.closeConnection(connOfOrcl);
			}
		}

	}

	public static void rsToVoltdb(ResultSet rs, Connection connOfVolt, String tableName) throws SQLException, IOException
	{
		ResultSetMetaData metaData = rs.getMetaData();
		int columnCount = metaData.getColumnCount();
		while (rs.next())
		{

			StringBuilder sb = new StringBuilder();
			for (int i = 1; i <= columnCount; i++)
			{
				Object obj = rs.getObject(i);
				if (obj instanceof String)
				{
					obj = "'" + (String) obj + "'";
				}
                /* //对于number类型且值超过6位有效数字  则转化成varchar类型
				if(IOFactory.object2byte(obj).length>=6)
				{
					obj = "'" + (String) obj + "'";
				}*/
				if (obj instanceof java.sql.Date)
				{
					java.util.Date utilDate = new java.util.Date(((java.sql.Date) obj).getTime());
					obj = new Timestamp(utilDate.getTime());

				}

				if (i != columnCount)
				{
					sb.append(obj).append(",");
				}
				else
				{
					sb.append(obj);
				}
			}
			//String ss = "insert into " + tableName + " values (" + sb.toString() + ")";
			//System.out.println(ss);
			connOfVolt.createStatement().execute("insert into " + tableName + " values (" + sb.toString() + ")");
		}
	}
}
