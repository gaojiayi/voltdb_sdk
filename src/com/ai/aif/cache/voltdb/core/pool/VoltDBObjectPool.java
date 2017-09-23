package com.ai.aif.cache.voltdb.core.pool;

import org.apache.commons.pool.impl.GenericObjectPool;

public class VoltDBObjectPool extends GenericObjectPool
{
	private int maxActive;
	private int maxIdle;
	private long maxWait;
	private int minIdle;
	private VoltDBObjectPoolFactory objFactory;

	/*
	 * static { maxIdle = 8; minIdle = 8; maxActive = 8; maxWait = -1; }
	 */

	public VoltDBObjectPool(VoltDBObjectPoolFactory objFactory)
	{
		super(objFactory);
		this.objFactory = null;
		// 默认为 8 8 8 -1
		setMaxIdle(8);
		setMinIdle(8);
		setMaxActive(8);
		setMaxWait(-1);
		this.objFactory = objFactory;
	}

	public String getUsername()
	{
		return objFactory.getUsername();
	}

	public String getPassword()
	{
		return objFactory.getPassword();
	}

	public String getHost()
	{
		return objFactory.getHost();
	}

	public String getDBName()
	{
		return objFactory.getDbName();
	}

	public String getPort()
	{
		return objFactory.getPort();
	}

	public int getMaxActive()
	{
		return maxActive;
	}

	public void setMaxActive(int maxActive)
	{
		this.maxActive = maxActive;
	}

	public int getMaxIdle()
	{
		return maxIdle;
	}

	public void setMaxIdle(int maxIdle)
	{
		this.maxIdle = maxIdle;
	}

	public long getMaxWait()
	{
		return maxWait;
	}

	public void setMaxWait(int maxWait)
	{
		this.maxWait = maxWait;
	}

	public int getMinIdle()
	{
		return minIdle;
	}

	public void setMinIdle(int minIdle)
	{
		this.minIdle = minIdle;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + maxActive;
		result = prime * result + maxIdle;
		result = prime * result + (int) (maxWait ^ (maxWait >>> 32));
		result = prime * result + minIdle;
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VoltDBObjectPool other = (VoltDBObjectPool) obj;
		if (this.maxActive != other.maxActive)
			return false;
		if (this.maxIdle != other.maxIdle)
			return false;
		if (this.maxWait != other.maxWait)
			return false;
		if (this.minIdle != other.minIdle)
			return false;
		if (getUsername() != other.getUsername())
			return false;
		if (getHost() != other.getHost())
			return false;
		if (getDBName() != other.getDBName())
			return false;
		if (getPort() != other.getPort())
			return false;
		return true;
	}

}
