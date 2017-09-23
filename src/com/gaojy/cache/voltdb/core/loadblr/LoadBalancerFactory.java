package com.gaojy.cache.voltdb.core.loadblr;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gaojy.cache.voltdb.core.cfg.pojo.DBNode;
import com.gaojy.cache.voltdb.core.commons.LoadType;
import com.gaojy.cache.voltdb.core.loadblr.mode.RandomImpl;
import com.gaojy.cache.voltdb.core.loadblr.mode.RoundRobinImpl;

/**
 * 
 * @Title: 算法工厂
 * @Description: 
 * @Author: gaojy
 * @Version: 1.0
 */
public class LoadBalancerFactory
{
	private static final Log LOGGER = LogFactory.getLog(RoundRobinImpl.class);
	
	/**
	 * 获取类型，返回对应的实例
	 * @param type
	 * @return
	 */
	public static IAILoadBalancer<BasicDataSource,DBNode> getLoadBalancer(LoadType type)
	{
		switch (type)
		{
			case RANDOM:
				return new RandomImpl();
			case ROUNDROBIN:
				return new RoundRobinImpl();
			default:
				LOGGER.error("Unknown type,By default round robin.");
				return new RoundRobinImpl(); 
		}
		
	}

}
