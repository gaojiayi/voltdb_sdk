package com.ai.aif.cache.voltdb.core.monitor;

import java.util.Map;

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
public class DataCompareMonitor
{
	/**
	 * 根据表名 得到oracle和voltdb中不同的数据，及不同数据的大小
	 * @param tableName
	 * @return
	 */
	public static DataCompareMonitorBean[]  getDifferDataFromTable(String tableName)
	{
		Map alldifferData = DataCompareUtils.getAlldifferData(tableName);
		if(null !=alldifferData&&alldifferData.size()>0)
		{
			for(Object dataBase:alldifferData.keySet())
			{
				alldifferData.get((String)dataBase);
			}
		}
		
		
		return null;
		
	}
	/**
	 * 得到所有表中不同数据总量
	 * @return
	 */
	public static int  getDifferCountFromAllTables()
	{
		return 0;
		
	}
}
