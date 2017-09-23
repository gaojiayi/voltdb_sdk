package com.ai.aif.cache.voltdb.core.policy.impl;

import java.util.LinkedList;

import com.ai.aif.cache.voltdb.core.policy.IPolicy;
import com.ai.aif.cache.voltdb.core.pool.VoltDBObjectPool;
import com.ai.aif.cache.voltdb.core.validate.ValidatePool;
/**
 * 
 * 
 * @Title:取列表中最近数据策略 
 * @Description: 
 * @Copyright: Copyright (c) 2016 CMC CRM TA Dept-NJ All Rights Reserved
 * @Company: Asiainfo
 * @Author: gaojy5
 * @Version: 1.0
 */
public class RoundRobinPolicy extends LinkedList implements IPolicy
{
	private int position;
	
	

	
	@Override
	public boolean contains(Object obj)
	{
		return ValidatePool.isAvaliable((VoltDBObjectPool)obj, this.toArray());
	}

	@Override
	public Object getPolicyObject() throws Exception
	{
		if(position >= super.size()-1)
		{
			position = 0;
		}
		else
		{
			position++;
		}
		return super.get(position);
	}

	
	public boolean remove(Object obj)
	{
		VoltDBObjectPool pool = (VoltDBObjectPool) obj;
		int j = 0;
		for(int i = 0;i < this.size();i++)
		{
			if(pool.equals((VoltDBObjectPool)this.get(i)))
			{
				j = i;
				break;
			}
		}
		return super.remove(this.get(j));
	}

	
}
