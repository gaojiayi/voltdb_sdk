package com.ai.aif.cache.voltdb.core.validate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.ai.aif.cache.voltdb.core.config.Configuration;
import com.ai.aif.cache.voltdb.core.constant.Constants;
import com.ai.aif.cache.voltdb.core.pool.VoltDBObjectPool;

public class ValidatePool
{
	/**
	 * 验证连接池是否有效
	 * 
	 * @param pool
	 * @return
	 * @throws Exception
	 */
	public static boolean validate(VoltDBObjectPool pool) throws Exception
	{
		String sql;
		try
		{
			sql = Configuration.getInstance().getCheckTask().getSql();

		}
		catch (Exception e)
		{
			sql = Constants.Config.SQL;
			// 日志：获取本地配置文件SQL异常，使用默认的sql
		}
		Connection conn = (Connection) pool.borrowObject();
		if (conn == null)
		{
			return false;
		}

		try
		{
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			return rs.next();
		}
		catch (Exception e)
		{
			return false;
		}

	}

	/**
	 * 判断当前对象是否在有效池中
	 * 
	 * @param pool
	 * @param pools
	 * @return
	 */
	public static boolean isAvaliable(VoltDBObjectPool pool, Object[] pools)
	{
		// VoltDBObjectPool pool = (VoltObjectPool)pools[i];
		Boolean ret = false;
		for (int i = 0; i < pools.length; i++)
		{
			VoltDBObjectPool obj = (VoltDBObjectPool) pools[i];
			ret = obj.equals(pool);
			if (ret == true)
			{
				break;
			}
		}
		return ret;
	}
}
