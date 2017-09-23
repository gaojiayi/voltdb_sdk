package com.gaojy.cache.voltdb.core.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;

/**
 * @Title:
 * @Description:
 * @Author: gaojy
 * @Version: 1.0
 */
public abstract class AIUtils
{
	
	private static ThreadLocal<Connection> LOCALTHREAD = new ThreadLocal<Connection>();
	
	/**
	 * set connection
	 * @param con
	 */
	public static void setConnection(Connection con)
	{
		LOCALTHREAD.set(con);
	}
	
	/**
	 * get current connection
	 * @return
	 */
	public static Connection getConnection()
	{
		return LOCALTHREAD.get();
	}
	
	/**
	 * remove current connection
	 */
	public static void remove()
	{
		LOCALTHREAD.remove();
	}
	

	/**
	 * 获取对象大小
	 * @param obj src object
	 * @return size
	 * @throws IOException
	 */
	public static int getBytesSize(Object obj) throws IOException
	{
		byte[] bytes = Object2Bytes(obj);
		return bytes.length;
	}
	

	/**
	 * Object to bytes
	 * @param obj src object
	 * @return byte byte[]
	 * @throws IOException
	 */
	private static byte[] Object2Bytes(Object obj) throws IOException
	{
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bao);
		oos.writeObject(obj);
		return bao.toByteArray();
	}

}
