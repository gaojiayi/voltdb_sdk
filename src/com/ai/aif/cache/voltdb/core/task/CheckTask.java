package com.ai.aif.cache.voltdb.core.task;

import java.util.List;
import java.util.TimerTask;

import com.ai.aif.cache.voltdb.core.config.node.PoolNode;
import com.ai.aif.cache.voltdb.core.policy.IPolicy;
import com.ai.aif.cache.voltdb.core.policy.LoadPoolFactory;
import com.ai.aif.cache.voltdb.core.pool.VoltDBObjectPool;
import com.ai.aif.cache.voltdb.core.validate.ValidatePool;

/**
 * 数据库连接池有效性检查
 * 一旦某个连接池中恢复起来之后，重新加入到Ipolicy，即有效连接池中
 * @Title: 
 * @Description: 
 * @Copyright: Copyright (c) 2016 CMC CRM TA Dept-NJ All Rights Reserved
 * @Company: Asiainfo
 * @Author: gaojy5
 * @Version: 1.0
 */
public class CheckTask extends TimerTask
{


	public void run()
	{
		Thread.currentThread().setName("VoltDB数据库连接池有效性检查线程");
		StringBuffer sb = null;
		//debug日志  准备进行数据库连接池有效性检查
		//有效池
		IPolicy avaliablePools = LoadPoolFactory.getInstance().getAvailablePools();
		//所有池
		List<PoolNode> allPools = LoadPoolFactory.getInstance().getAllPoolNodes();
		VoltDBObjectPool pool = null;
		
		try
		{
			for(PoolNode poolNode : allPools)
			{
				pool =LoadPoolFactory.getInstance().makePool(poolNode);
				if(ValidatePool.validate(pool))
				{
					//有效连接池不包含在有效库中时，新增。
					if(!avaliablePools.contains(pool))
					{
						avaliablePools.add(pool);
						continue;
					}
				}else
				{
					//删除无效链接。如果有效库本身存在，则删除。
					LoadPoolFactory.getInstance().deletePool(pool);
				}
			}
		}
		catch (Exception e)
		{
			// TODO: handle exception
		}
		finally
		{
			if(null != pool)
			{
				pool.clear();
				pool = null;
			}
		}
		
	}
	
}
