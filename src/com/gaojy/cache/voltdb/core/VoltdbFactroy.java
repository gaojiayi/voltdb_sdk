package com.gaojy.cache.voltdb.core;

import com.gaojy.cache.voltdb.core.commons.DBType;
import com.gaojy.cache.voltdb.core.dbsource.VoltdbDaoImpl;
import com.gaojy.cache.voltdb.core.dbsource.opt.IVoltdbDao;
import com.gaojy.cache.voltdb.core.proxy.OptByCacheByLruImpl;
import com.gaojy.cache.voltdb.core.proxy.VoltdbDyncProxy;

/**
 * @Title: API的工厂
 * @Description:
 * @Author: gaojy
 * @Version: 1.0
 */
public class VoltdbFactroy
{

	private VoltdbFactroy()
	{

	}

	/**
	 * 获取实例
	 * 
	 * @param 数据类型
	 * @return IVoltdbDao
	 */
	public static IVoltdbDao newInstance(DBType type)
	{
		VoltdbDyncProxy proxy = new VoltdbDyncProxy(new VoltdbDaoImpl(type.toString()), OptByCacheByLruImpl.class);

		return (IVoltdbDao) proxy.bind();
	}

	/**
	 * 获取实例，默认Voltdb
	 * 
	 * @return
	 */
	public static IVoltdbDao newInstance()
	{
		return newInstance(DBType.VOLTDB);
	}
}
