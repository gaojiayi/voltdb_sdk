package com.ai.aif.cache.voltdb.core.common;

public class Tables
{
	private String tableName;

	public String getTableName()
	{
		return tableName;
	}

	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}

	public Tables(String tableName)
	{
		super();
		this.tableName = tableName;
	}

	public Tables()
	{
		super();
		// TODO Auto-generated constructor stub
	}

}
