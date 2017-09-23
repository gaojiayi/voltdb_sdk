package com.ai.aif.cache.voltdb.core.io;

import java.io.IOException;

/**
 * 
 * 
 * @Title: IO工厂
 * @Description: 
 * @Copyright: Copyright (c) 2016 CMC CRM TA Dept-NJ All Rights Reserved
 * @Company: Asiainfo
 * @Author: gaojy5
 * @Version: 1.0
 */
public class IOFactory
{
	private static Boolean isInit =	Boolean.FALSE;
	private static ISerializable SERIALIZABLE = null;
	private IOFactory() {}
	static{
		init();
	}
	public static void init(){
		if(isInit.equals(Boolean.FALSE)){
			synchronized (IOFactory.class)
			{
				if(isInit.equals(Boolean.FALSE))
				{
					SERIALIZABLE = new JavaSerializable();
					isInit = Boolean.TRUE;
				}
			}
		}
	}
	/**
	 * 对象转化成字节数组
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	public static byte[] object2byte(Object obj) throws IOException
	{
		return SERIALIZABLE.object2byte(obj);
	}
	/**
	 * 字节数组转化成对象
	 * @param bytes
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object byte2Object(byte[] bytes) throws IOException, ClassNotFoundException
	{
		return SERIALIZABLE.byte2object(bytes);
	}
}
