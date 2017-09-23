package com.ai.aif.cache.voltdb.core.constant;
/**
 * @Title: 常量
 * @Description: 
 * @Copyright: Copyright (c) 2016 CMC CRM TA Dept-NJ All Rights Reserved
 * @Company: Asiainfo
 * @Author: gaojy5
 * @Version: 1.0
 */
public interface Constants
{
	/**
	 * @Title: 配置接口
	 * @Description: 
	 * @Copyright: Copyright (c) 2016 CMC CRM TA Dept-NJ All Rights Reserved
	 * @Company: Asiainfo
	 * @Author: gaojy5
	 * @Version: 1.0
	 */
	interface Config
	{
		/**
		 * 缓存中心配置文件
		 */
		String CONFIG_FILE = "voltdb.xml";
		
		/**
		 * 桶数量
		 */
		int BUCKET_COUNT = 5;

		/**
		 * 本地缓存最大容量(单位：M)
		 */
		int LOCAL_CACHE_MAX_LIMIT = 10;
		
		/**
		 * 桶数量KEY
		 */
		String KEY_BUCKET = "bucketCount";

		/**
		 * 本地缓存最大容量KEY
		 */
		String KEY_LOCAL_CACHE_MAX_LIMIT = "maxSize";
		
		/**
		 * 延迟时间KEY
		 */
		String KEY_DELAY = "delay";
		
		/**
		 * 执行间隔KEY
		 */
		String KEY_PERIOD = "period";
		
		/**
		 * 延迟执行时间,单位/MS
		 */
		long DELAY = 60 * 1000;
		
		/**
		 * 执行间隔，单位/MS
		 */
		long PERIOD = 100 * 1000;
		
		
		String SQL = "select * from dual"; 
		
		String KEY_SQL = "sql"; 
		
		String STRATEGY = "strategy";
		
	
	}
	
	/**
	 * @Title: 日志记录前缀
	 * @Description: 
	 * @Copyright: Copyright (c) 2016 CMC CRM TA Dept-NJ All Rights Reserved
	 * @Company: Asiainfo
	 * @Author: gaojy5
	 * @Version: 1.0
	 */

	interface LogPrefix
	{
		/**
		 * cache模块日志记录前缀
		 */
		String CACHE_LOG_PREFIX = "【Memcache】【cache】";
		
		/**
		 * config模块日志记录前缀
		 */
		String CONFIG_LOG_PREFIX = "【Memcache】【config】";
		
		/**
		 * execute模块日志记录前缀
		 */
		String EXECUTE_LOG_PREFIX = "【Memcache】【execute】";
		
		/**
		 * task模块日志记录前缀
		 */
		String TASK_LOG_PREFIX = "【Memcache】【task】";
		
		/**
		 * spi模块日志记录前缀
		 */
		String SPI_LOG_PREFIX = "【Memcache】【spi】";
	}
	
	/**
	 * 
	 * 
	 * @Title: 二级缓存KEY值前缀
	 * @Description: 
	 * @Copyright: Copyright (c) 2016 CMC CRM TA Dept-NJ All Rights Reserved
	 * @Company: Asiainfo
	 * @Author: gaojy5
	 * @Version: 1.0
	 */
	interface KeyPrefix
	{
		/**
		 * 根据class和key获取value的cache-key前缀
		 */
		String CLASS_KEY_PREFIX = "class";
		
		/**
		 * 根据dataType和key获取value的cache-key前缀
		 */
		String DATATYPE_KEY_PREFIX = "dataType";
	}
}
