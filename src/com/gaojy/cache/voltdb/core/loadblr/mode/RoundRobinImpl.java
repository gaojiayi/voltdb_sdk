package com.gaojy.cache.voltdb.core.loadblr.mode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gaojy.cache.voltdb.core.cfg.pojo.DBNode;
import com.gaojy.cache.voltdb.core.loadblr.IAILoadBalancer;

/**
 * @Title: 轮询
 * @Description: 公平算法，每个成员都有份
 * @Author: gaojy
 * @Version: 1.0
 */
public class RoundRobinImpl implements IAILoadBalancer<BasicDataSource,DBNode>
{
	private static final Log LOGGER = LogFactory.getLog(RoundRobinImpl.class);

	// 索引
	private AtomicInteger currIndex = new AtomicInteger(0);

	/*
	 * (non-Javadoc)
	 * @see com.gaojy.cache.voltdb.core.loadblr.IAILoadBalancer#getConnection(java.util.List)
	 */
	@Override
	public BasicDataSource getDataSource(List<DBNode> lists)
	{
		if (null == lists || lists.size() == 0)
		{
			LOGGER.warn("These services are not available.");
			return null;
		}

		int index = (currIndex.incrementAndGet() + 1) % lists.size();

		DBNode dbnode = lists.get(index);
		//发现故障，继续找可用的服务
		if (!dbnode.isNormal())
		{
			String url = dbnode.getBasicDataSource().getUrl();
			
			LOGGER.error("Failed to access " + url);
			
			LOGGER.info("To find the next service available .");
			
			//移除故障的
			List<DBNode> temp = new ArrayList<DBNode>(lists);
			
			temp.remove(dbnode);
			
			return getDataSource(temp);
		}
		return dbnode.getBasicDataSource();
	}
}
