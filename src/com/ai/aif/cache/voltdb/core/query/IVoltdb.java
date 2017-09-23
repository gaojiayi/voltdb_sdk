package com.ai.aif.cache.voltdb.core.query;

import java.sql.ResultSet;
import java.util.List;

public interface IVoltdb
{
	Object[] query(String sql, Object[] params) throws Exception;

	boolean update(String sql, Object[] params) throws Exception;

	ResultSet query(String sql) throws Exception;

	<T> List<T> getBeans(Class<T> clazz, String sql, Object[] params) throws Exception;

	<T> List<T> getBeans(ResultSet rs, Class<T> clazz) throws Exception;
	/**
	 * 该方法只合适APPframe框架中的bean
	 * @param clazz
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	<T> List<T> getAPPBeans(Class<T> clazz, String sql) throws Exception;
	/**
	 *该方法只适合APPframe框架中的bean
	 * @param clazz
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	<T> T getAPPBean(Class<T> clazz, String sql) throws Exception;
	
	

	void batchUpdate(List<String> sqls) throws Exception;
}
