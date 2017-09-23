package com.ai.aif.cache.voltdb.core.cache;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ai.aif.cache.voltdb.core.cache.map.ConcurrentSizeLRUMap;
import com.ai.aif.cache.voltdb.core.cache.map.SizeObject;
import com.ai.aif.cache.voltdb.core.constant.Constants;



/**
 * 
 * 
 * @Title: 本地二级缓存对象及操作方法
 * @Description: 
 * @Copyright: Copyright (c) 2016 CMC CRM TA Dept-NJ All Rights Reserved
 * @Company: Asiainfo
 * @Author: gaojy5
 * @Version: 1.0
 */
public class LocalCache
{
	private static transient Log log = LogFactory.getLog(LocalCache.class);
	
	// 日志记录前缀
	private static final String LOG_PREFIX = Constants.LogPrefix.CACHE_LOG_PREFIX;

	// 本地cache对象
	private static ConcurrentSizeLRUMap LOCAL_CACHE = null;

	// 本地二级缓存实例
	private static LocalCache instance = null;

	// 是否已初始化
	private static Boolean isInit = Boolean.FALSE;

	// 默认桶数量为
	private static int BUCKET_COUNT = Constants.Config.BUCKET_COUNT;

	// 默认本地缓存最大容量
	private static int LOCAL_CACHE_MAX_LIMIT = Constants.Config.LOCAL_CACHE_MAX_LIMIT;

	// 最后一次清除时间
	public static long LAST_LOCAL_CACHE_CLEAR_TIME = System.currentTimeMillis();

	static
	{
		// 默认5个bucket
		int bucket = BUCKET_COUNT;

		// 默认10M
		int limitBytes = LOCAL_CACHE_MAX_LIMIT * 1024 * 1024;

		// 实际配置    后期还需修改
		/*try
		{
			String tempBucket = Configuration.getInstance().getBucketCount();
			if (StringUtils.isNotEmpty(tempBucket) && StringUtils.isNumeric(tempBucket))
			{
				bucket = Integer.parseInt(tempBucket);
			}

			String tempLimitBytes = Configuration.getInstance().getMaxCacheSize();
			if (StringUtils.isNotEmpty(tempLimitBytes) && StringUtils.isNumeric(tempLimitBytes))
			{
				limitBytes = Integer.parseInt(tempLimitBytes) * 1024 * 1024;
			}
		}
		catch (Exception ex)
		{
			log.error(new StringBuilder(LOG_PREFIX).append("获得配置文件").append(Constants.Config.CONFIG_FILE).append("中的bucketCount或maxSize参数出错,采用默认值,不影响系统运行"), ex);
		}*/
		LOCAL_CACHE = new ConcurrentSizeLRUMap(bucket, limitBytes);
	}

	private LocalCache() {}

	/**
	 * 获取本地缓存实例
	 * 
	 * @return LocalCache 本地缓存实例
	 */
	public static LocalCache getInstance()
	{
		if (isInit.equals(Boolean.FALSE))
		{
			synchronized (LocalCache.class)
			{
				if (isInit.equals(Boolean.FALSE))
				{
					try
					{
						instance = new LocalCache();
					}
					catch (Exception e)
					{
						log.error(new StringBuilder(LOG_PREFIX).append("初始化LocalCache失败"), e);
						throw new RuntimeException(e);
					}
					isInit = Boolean.TRUE;
				}
			}
		}
		return instance;
	}

	/**
	 * 新增缓存
	 * 
	 * @param key
	 * @param so
	 */
	public void addLocal(String key, SizeObject so)
	{
		if (log.isDebugEnabled())
		{
			log.debug(new StringBuffer(LOG_PREFIX).append("当前准备存储到本地缓存的数据大小为：").append(so.getSize()).append("(单位Byte),而此时本地LRU缓存容器容量为：").append(LOCAL_CACHE.size()).append("(单位Byte)"));
		}
		LOCAL_CACHE.put(key, so);
		if (log.isDebugEnabled())
		{
			log.debug(new StringBuffer(LOG_PREFIX).append("存储后,此时本地缓存容器容量为：").append(LOCAL_CACHE.getCurrentByteSize()).append("(单位Byte)"));
		}
	}

	/**
	 * 判断缓存中是否有某KEY
	 * 
	 * @param key
	 * @return
	 */
	public boolean containsKey(String key)
	{
		return LOCAL_CACHE.containsKey(key);
	}

	/**
	 * 本地缓存查询
	 * 
	 * @param key
	 * @return
	 */
	public SizeObject queryLocal(String key)
	{
		return (SizeObject) LOCAL_CACHE.get(key);
	}

	/**
	 * 返回本地缓存
	 * 
	 * @return ConcurrentSizeLRUMap 本地缓存
	 */
	public ConcurrentSizeLRUMap getCache()
	{
		return LOCAL_CACHE;
	}

	/**
	 * 清空本地缓存
	 */
	public void clearLocalCache()
	{
		if (log.isDebugEnabled())
		{
			log.debug(new StringBuilder(LOG_PREFIX).append("清空LocalCache"));
		}
		LOCAL_CACHE.clear();
		LAST_LOCAL_CACHE_CLEAR_TIME = System.currentTimeMillis();
	}
}
