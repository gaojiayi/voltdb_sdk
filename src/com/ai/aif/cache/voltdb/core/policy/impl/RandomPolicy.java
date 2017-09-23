package com.ai.aif.cache.voltdb.core.policy.impl;

import java.util.LinkedList;

import com.ai.aif.cache.voltdb.core.policy.IPolicy;
import com.ai.aif.cache.voltdb.core.pool.VoltDBObjectPool;
import com.ai.aif.cache.voltdb.core.validate.ValidatePool;
/**
 * 
 * 
 * @Title: 随机策略
 * @Description: 
 * @Copyright: Copyright (c) 2016 CMC CRM TA Dept-NJ All Rights Reserved
 * @Company: Asiainfo
 * @Author: gaojy5
 * @Version: 1.0
 */
public class RandomPolicy extends LinkedList implements IPolicy
{
	public int getRandom(int min ,int max){
		return min+(int)(Math.random()*(double)(max-min+1));
	}

	

	@Override
	public boolean contains(Object obj)
	{
		// 验证连接池
		return ValidatePool.isAvaliable((VoltDBObjectPool)obj, this.toArray());
		
		
	}

	@Override
	public Object getPolicyObject() throws Exception
	{
		
		return super.get(getRandom(0,super.size()-1));
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
