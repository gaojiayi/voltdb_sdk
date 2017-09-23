package com.ai.aif.cache.voltdb.core.common;

public enum DataBase
{
	ORACLE,VOLTDB;

	public DataBase toType(String name)
	{
		if(ORACLE.toString().equalsIgnoreCase(name))
		{
			return ORACLE;
		}
		else
		{
			return VOLTDB;
		}
	}
	
}
