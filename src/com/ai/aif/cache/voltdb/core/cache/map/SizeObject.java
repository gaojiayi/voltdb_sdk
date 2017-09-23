package com.ai.aif.cache.voltdb.core.cache.map;
/**
 * 
 * 
 * @Title: 
 * @Description: 
 * @Copyright: Copyright (c) 2016 CMC CRM TA Dept-NJ All Rights Reserved
 * @Company: Asiainfo
 * @Author: gaojy5
 * @Version: 1.0
 */
public class SizeObject
{
	/**
	 * 保存的对象
	 */
	private Object obj = null;
	
	/**
	 * 对象大小
	 */
	private int size = 0;

	public SizeObject(Object obj, int size)
	{
		this.obj = obj;
		this.size = size;
	}

	public Object getObj()
	{
		return obj;
	}

	public int getSize()
	{
		return size;
	}
}
