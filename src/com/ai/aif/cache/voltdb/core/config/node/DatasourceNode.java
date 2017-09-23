package com.ai.aif.cache.voltdb.core.config.node;

import java.util.HashMap;
import java.util.Map;

public class DatasourceNode
{
	private Map<String,PoolNode> poolMap = new HashMap<String,PoolNode>();
	public void addPool(PoolNode pool)
	{
		this.poolMap.put(pool.getName(), pool);
	}
	
	public PoolNode getPool(String name)
	{
		return poolMap.get(name);
	}
	public PoolNode[] getPoolNodes(){
		Object[] array = this.poolMap.values().toArray();
		int length = array.length;
		PoolNode[] pools = new PoolNode[length];
		for(int i = 0;i < length;i++)
		{
			pools[i] = (PoolNode)array[i];
		}
		return pools;
	} 
}
