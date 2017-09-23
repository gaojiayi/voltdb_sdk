package com.gaojy.cache.voltdb.core.commons;

/**
 * 
 * @Title: 算法类型
 * @Description: 
 * @Author: gaojy
 * @Version: 1.0
 */
public enum LoadType
{
	/**
	 * 随机
	 */
	RANDOM,
	
	/**
	 * 轮询
	 */
	ROUNDROBIN,
	
	/**
	 * 未知
	 */
	UNKNOWN;
	
	
	/**
	 * 转换成枚举类型
	 * @param src
	 * @return LoadType
	 */
	public static LoadType toType(String src)
	{
		if (RANDOM.toString().equalsIgnoreCase(src))
		{
			return RANDOM;
		}
		else if (ROUNDROBIN.toString().equalsIgnoreCase(src))
		{
			return ROUNDROBIN;
		}
		else
		{
			return null;
		}
	}

}
