package com.ai.aif.cache.voltdb.core.config.node;

import java.util.HashMap;
import java.util.Map;

import com.ai.aif.cache.voltdb.core.constant.Constants;

public class LocalCacheNode
{
	/**
	 * 桶数量
	 */
	private static final String KEY_BUCKET = Constants.Config.KEY_BUCKET;
	/**
	 * 本地缓存最大容量
	 */
	private static final  int KEY_MAX_LIMIT = Constants.Config.LOCAL_CACHE_MAX_LIMIT;
	
	/**
	 * 缓存参数对象集合
	 */
	private Map<String ,PropertyNode> propMap = new HashMap<String ,PropertyNode>(2);
	
	public void addProperty(PropertyNode property)
	{
		this.propMap.put(property.getName(), property);
	}
	
	public String getBucket()
	{
		return propMap.get(KEY_BUCKET).getValue();
	}

	public String getLimitSize()
	{
		return propMap.get(KEY_MAX_LIMIT).getValue();
	}
}
