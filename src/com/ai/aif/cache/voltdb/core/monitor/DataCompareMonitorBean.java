package com.ai.aif.cache.voltdb.core.monitor;

import java.util.List;

import com.ai.aif.cache.voltdb.core.common.DataBase;

public class DataCompareMonitorBean
{
	private String tableName;
	private List<String> differData;
	private Integer differSize;
	private DataBase  dataSource;        
	public DataBase getDataSource()
	{
		return dataSource;
	}
	public void setDataSource(DataBase dataSource)
	{
		this.dataSource = dataSource;
	}
	public String getTableName()
	{
		return tableName;
	}
	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}
	public List<String> getDifferData()
	{
		return differData;
	}
	public void setDifferData(List<String> differData)
	{
		this.differData = differData;
	}
	public Integer getDifferSize()
	{
		return differSize;
	}
	public void setDifferSize(Integer differSize)
	{
		this.differSize = differSize;
	}

}
