/**
 * 
 */
package com.gaojy.cache.voltdb.core.dbsource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gaojy.cache.voltdb.core.cfg.DBConfigSource;
import com.gaojy.cache.voltdb.core.cfg.pojo.BaseCfg;
import com.gaojy.cache.voltdb.core.cfg.pojo.DBNode;
import com.gaojy.cache.voltdb.core.commons.DBType;
import com.gaojy.cache.voltdb.core.commons.LoadType;
import com.gaojy.cache.voltdb.core.loadblr.IAILoadBalancer;
import com.gaojy.cache.voltdb.core.loadblr.LoadBalancerFactory;


/**
 * @Title: 
 * @Description: 
 * @Author: gaojy
 * @Version: 1.0
 */
public class AIBasicDataSource
{
	private static final Log LOGGER = LogFactory.getLog(AIBasicDataSource.class);
	
	private static final AtomicInteger INDEX = new AtomicInteger(0);
	
	private BaseCfg cfg = DBConfigSource.getInstance().getDBConfig();
	
	/**
	 * 获取算法具体实现
	 * @param type
	 * @return
	 */
	protected IAILoadBalancer<BasicDataSource,DBNode> getAILoadInstance(String type)
	{
		return LoadBalancerFactory.getLoadBalancer(LoadType.toType(type));
	}
	
	
	/**
	 * 获取connection
	 * @param aiload 算法实例
	 * @param lists datasource 集合
	 * @return Connection
	 */
	protected Connection getConnection(IAILoadBalancer<BasicDataSource,DBNode> aiload,List<DBNode> lists)
	{
		BasicDataSource node = aiload.getDataSource(lists);
		
		if (null == node)
		{
			INDEX.set(0);
			return null;
		}
		
		try
		{
			//记录
			INDEX.incrementAndGet();
			
			return node.getConnection();
		}
		catch (SQLException e)
		{
			if (INDEX.get() < lists.size())
			{
				LOGGER.error("Failed to access " + getOutParam(node));
				LOGGER.info("To find the next service available .");
				
				// 发现该连接故障
				return getConnection(aiload,lists);
			}
			else
			{
				INDEX.set(0);
				LOGGER.error("These services are not available . ",e);
			}
		}
		return null;
	}
	
	/**
	 * 根据数据库类型来获取连接
	 * @param dbType 可以不填，默认为voltdb; dbType值可以填多个，<b>注意:</b>只会取第一个参数。
	 * @return Connection
	 */
	public Connection getConnection(String... dbType)
	{
		String key = DBType.VOLTDB.toString();
		
		for (String type : dbType)
		{
			if (null != type && !"".equals(type.trim()))
			{
				key = type;
				break;
			}
		}
		
		List<DBNode>  lists = cfg.getDbMaps().get(key);
		
		IAILoadBalancer<BasicDataSource,DBNode> aiload = getAILoadInstance(cfg.getLoadType());
		
		return getConnection(aiload, lists);
	}

	private String getOutParam(BasicDataSource source)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(source.getUrl());// 这里需要过滤出IP
		return sb.toString();
	}
}	
