/**
 * 
 */
package com.gaojy.cache.voltdb.core.cfg.pojo;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * @Title: 数据库的配置信息
 * @Description: 存储了数据库的相关信息
 * @Author: gaojy
 * @Version: 1.0
 */
public class DBNode extends Node
{
	private BasicDataSource dataSource = new BasicDataSource();
	
	private String dbType;
	
	//不是否正常
	private boolean normal = true;

	/**
	 * @return the dbType
	 */
	public String getDbType()
	{
		return dbType;
	}

	/**
	 * @param dbType the dbType to set
	 */
	public void setDbType(String dbType)
	{
		this.dbType = dbType;
	}
	
	/**
	 * get dbcp data source
	 * @return
	 */
	public BasicDataSource getBasicDataSource()
	{
		return dataSource;
	}

	/**
	 * @return the normal
	 */
	public boolean isNormal()
	{
		return normal;
	}

	/**
	 * @param normal the normal to set
	 */
	public void setNormal(boolean normal)
	{
		this.normal = normal;
	}

	/* (non-Javadoc)
	 * @see com.gaojy.cache.voltdb.core.cfg.pojo.Node#getCopyObject()
	 */
	@Override
	protected Object getCopyObject()
	{
		return dataSource;
	}
}
