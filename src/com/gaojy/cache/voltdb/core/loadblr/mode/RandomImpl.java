package com.gaojy.cache.voltdb.core.loadblr.mode;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gaojy.cache.voltdb.core.cfg.pojo.DBNode;
import com.gaojy.cache.voltdb.core.loadblr.IAILoadBalancer;

/**
 * @Title: 随机算法
 * @Description: 对于无状态服务比较适用，随便选取一台机器就可以
 * @Author: gaojy
 * @Version: 1.0
 */
public class RandomImpl implements IAILoadBalancer<BasicDataSource,DBNode>
{
	private static final Log LOGGER = LogFactory.getLog(RandomImpl.class);

	private Random random = new Random();

	/*
	 * (non-Javadoc)
	 * @see com.gaojy.cache.voltdb.core.loadblr.IAILoadBalancer#getDataSource(java.util.List)
	 */
	@Override
	public BasicDataSource getDataSource(List<DBNode> lists)
	{
		if (null == lists || lists.size() == 0)
		{
			LOGGER.warn("These services are not available.");
			return null;
		}
		DBNode dbnode = lists.get(random.nextInt(lists.size()));
		
		if (!dbnode.isNormal())
		{
			LOGGER.info("To find the next service available .");
			
			//移除故障的
			List<DBNode> temp = new ArrayList<DBNode>(lists);
			
			temp.remove(dbnode);
			
			return getDataSource(temp);
		}
		return dbnode.getBasicDataSource();
	}

}
