package com.gaojy.cache.voltdb.core.dbsource.opt;

import com.gaojy.cache.voltdb.core.utils.RowWithMetaData;

/**
 * @Title:
 * @Description:
 * @Author: gaojy
 * @Version: 1.0
 */
public interface IDataLoader
{
	/**
	 * 插入一行
	 * 
	 * @param data
	 * @param obj
	 */
	void insertRow(RowWithMetaData data, Object[] obj);


	/**
	 * update size
	 */
	void updateBatchSize();
	
	void close();
}
