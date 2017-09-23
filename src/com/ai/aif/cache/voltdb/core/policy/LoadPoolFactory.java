package com.ai.aif.cache.voltdb.core.policy;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ai.aif.cache.voltdb.core.client.VoltdbBaseDAO;
import com.ai.aif.cache.voltdb.core.config.Configuration;
import com.ai.aif.cache.voltdb.core.config.node.PoolNode;
import com.ai.aif.cache.voltdb.core.config.node.PropertyNode;
import com.ai.aif.cache.voltdb.core.policy.impl.RandomPolicy;
import com.ai.aif.cache.voltdb.core.policy.impl.RoundRobinPolicy;
import com.ai.aif.cache.voltdb.core.pool.VoltDBObjectPool;
import com.ai.aif.cache.voltdb.core.pool.VoltDBObjectPoolFactory;

/**
 * @Title:连接池工厂
 * @Description:
 * @Copyright: Copyright (c) 2016 CMC CRM TA Dept-NJ All Rights Reserved
 * @Company: Asiainfo
 * @Author: gaojy5
 * @Version: 1.0
 */
public class LoadPoolFactory
{
	private static LoadPoolFactory instance = null;
	private static Boolean isInit = Boolean.FALSE;
	private static int poolNum = 0;
	private IPolicy iPolicy;
	private List<PoolNode> allPools;

	private LoadPoolFactory()
	{
		allPools = new ArrayList<PoolNode>();
		try
		{
			// 读配置确定负载方式
			if (Configuration.getInstance().getIbs().equals("RANDOM"))
			{
				iPolicy = new RandomPolicy();
			}
			else
			{
				iPolicy = new RoundRobinPolicy();
			}
		}
		catch (Exception e)
		{
			// 读取负载方式失败，默认使用轮询方式
		}
		
		try
		{
			PoolNode[] pools = Configuration.getInstance().getPools();
			if (null != pools && pools.length > 0)
			{
				poolNum = pools.length;
				for (PoolNode poolNode : pools)
				{
					allPools.add(poolNode);
					addPool(makePool(poolNode));
				}
			}
		}
		catch (Exception e)
		{
			// TODO: handle exception
		}
	}

	public static LoadPoolFactory getInstance()
	{
		if (null == instance)
		{
			synchronized (LoadPoolFactory.class)
			{
				if (isInit == Boolean.FALSE)
				{
					instance = new LoadPoolFactory();
					isInit = Boolean.TRUE;
				}
			}
		}
		return instance;
	}

	public VoltDBObjectPool getPool()
	{
		VoltDBObjectPool pool = null;
		try
		{
			if (iPolicy.size() == 0)
			{
				// 获取连接失败
				return null;
			}
			pool = (VoltDBObjectPool) iPolicy.getPolicyObject();
			
		}
		catch (Exception e)
		{
			// 获取连接失败
		}
		return pool;
	}

	public void deletePool(VoltDBObjectPool pool)
	{
		synchronized (iPolicy)
		{
			if (iPolicy.contains(pool))
			{
				iPolicy.remove(pool);
				pool.clear();
				pool = null;
			}
		}
	}

	public VoltDBObjectPool makePool(PoolNode poolNode)
	{
		Map<String, PropertyNode> map = poolNode.getPropMap();
		VoltDBObjectPoolFactory factory = new VoltDBObjectPoolFactory(poolNode.getName(), map.get("dbName").getValue(),
				map.get("username").getValue(), map.get("password").getValue(), map.get("host").getValue(), map.get(
						"port").getValue());
		VoltDBObjectPool pool = new VoltDBObjectPool(factory);
		pool.setMaxActive(Integer.parseInt(map.get("maxActive").getValue()));
		pool.setMaxIdle(Integer.parseInt(map.get("maxIdle").getValue()));
		pool.setMaxWait(Long.parseLong(map.get("maxWait").getValue()));
		pool.setMinIdle(Integer.parseInt(map.get("minIdle").getValue()));
		return pool;
	}

	public void addPool(VoltDBObjectPool pool)
	{
		synchronized (pool)
		{
			iPolicy.add(pool);
		}
	}

	public Connection getConnection() throws Exception
	{
		VoltDBObjectPool pool =null;
		Connection conn =null;
		try
		{
			/*conn =  (Connection)instance.getPool().borrowObject();
			if(conn == null)
			{
				for(int i = 0;i<poolNum-1;i++)
				{
					conn =  (Connection)instance.getPool().borrowObject()
					if(conn ==null)
					 {
						continue;
					 }
					else
					 {
						 return conn;
					 }
				}
			}
			*/
			pool = instance.getPool();
			conn = (Connection) pool.borrowObject();
			if(conn == null)
			{
				instance.deletePool(pool);
				
				if(poolNum >= 2)
				{
					for(int i = 0; i < poolNum - 1; i++)
					{
						pool = instance.getPool();
						conn = (Connection) pool.borrowObject();
						if(conn == null)
						{
							instance.deletePool(pool);
							continue;
						}
					}
				}
				
				// 如果循环判断之后
				// 连接依然无效，那说明配置文件中配置的连接就是全都无效的
				if(conn == null)
				{
					//log.error(new StringBuffer(LOG_PREFIX).append("配置文件").append(Constants.Config.CONFIG_FILE).append("中配置的连接池全都是无效的，请检查连接池的有效性"));
					return null;
				}
			}
			
		}
		catch (Exception e)
		{
			throw e;
		}
		return conn;
	}
	
	public List<PoolNode> getAllPoolNodes()
	{
		return allPools;

	}

	public IPolicy getAvailablePools()
	{
		return iPolicy;
	}

	public int getPoolNum()
	{
		return poolNum;
	}

}
