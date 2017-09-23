package com.gaojy.cache.voltdb.core.task;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @Title: 检查ResultSet 是否遍历结束；结束则关闭连接
 * @Description:如ResultSet遍历耗时10秒，还没结束；则强制关闭。
 * @Author: gaojy
 * @Version: 1.0
 */
public class DetectResultSet extends Thread
{
	private static final Log LOGGER = LogFactory.getLog(DetectResultSet.class);

	/** 默认10秒 */
	private static final long DEFAULT_PERIOD = 10000;

	/** ResultSet遍历耗时多少钱，则连接关闭 */
	private static final String DETECT_RESULT_PERIOD = "DETECT_RESULT_PERIOD";

	private ResultSet rs;

	private Connection con;

	/** 记录创建时间 */
	private long firstTime;

	/**
	 * @param rs
	 * @param con
	 */
	public DetectResultSet(ResultSet rs, Connection con)
	{
		this.rs = rs;
		this.con = con;
		this.firstTime = System.currentTimeMillis();
		setDaemon(true);
		setName("DetectResultSet");
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run()
	{
		try
		{
			long currentTime = System.currentTimeMillis();

			boolean flag = (currentTime - firstTime) > getPeriod();

			while (flag || !rs.isLast())
			{
				if (!con.isClosed())
				{
					con.close();
					break;
				}
				sleep(500);
			}
		}
		catch (SQLException e)
		{
			LOGGER.error(e);
		}
		catch (InterruptedException e)
		{
			LOGGER.error(e);
		}
	}

	/**
	 * 如需要改动该参数，以配置搞定。
	 * 
	 * @return long
	 */
	private long getPeriod()
	{
		String en = System.getenv(DETECT_RESULT_PERIOD);

		if (null == en || "".equals(en.trim()))
		{
			return DEFAULT_PERIOD;
		}

		try
		{
			return Long.parseLong(en);
		}
		catch (NumberFormatException e)
		{
			return DEFAULT_PERIOD;
		}
	}

}
