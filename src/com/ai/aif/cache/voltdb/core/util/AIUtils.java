package com.ai.aif.cache.voltdb.core.util;

import java.sql.Connection;

public class AIUtils
{
	private static ThreadLocal<Connection> threadLocal = new ThreadLocal();

	public static void setConnection(Connection conn)
	{
		threadLocal.set(conn);
	}

	public static Connection getConnnection()
	{
		return threadLocal.get();

	}

	/*
	 * 情况当前线程中的共享变量
	 */
	public static void removeConnection()
	{
		threadLocal.remove();
	}
}
