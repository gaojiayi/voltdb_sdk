package com.gaojy.cache.voltdb.core.commons;


/**
 * 
 * @Title: 
 * @Description: 
 * @Author: gaojy
 * @Version: 1.0
 */
public enum DBType
{
	VOLTDB,
	
	ORACLE,
	
	UNKNOWN;
	
	public static DBType toDBType(String type)
	{
		if (VOLTDB.toString().equalsIgnoreCase(type))
		{
			return VOLTDB;
		}
		else if (ORACLE.toString().equalsIgnoreCase(type))
		{
			return ORACLE;
		}
		else
		{
			return UNKNOWN;
		}
	}

}
