package com.gaojy.cache.voltdb.core.lru;

import java.util.Collections;
import java.util.Map;

import com.gaojy.cache.voltdb.core.cfg.DBConfigSource;
import com.gaojy.cache.voltdb.core.cfg.pojo.BaseCfg;

/**
 * @Title:
 * @Description:
 * @Author: gaojy
 * @Version: 1.0
 */
public class LRUCache
{
	private static Map<Object, Object> LRU_MAP = null;

	static
	{
		LRU_MAP = synchronizedMap();
	}

	private LRUCache()
	{

	}

	/**
	 * 返回线程安全的
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static Map<Object, Object> synchronizedMap()
	{
		BaseCfg cfg = DBConfigSource.getInstance().getDBConfig();

		return Collections.synchronizedMap(new AiLRUMap(cfg.getLruInfo().getMaxSize()));
	}

	/**
	 * 向二级缓存放一条记录
	 * 
	 * @param sql
	 * @param obj
	 */
	public static void add(Object sql, Object obj)
	{
		// 如有重新，则更新
		LRU_MAP.put(sql, obj);
	}

	/**
	 * 更新二级缓存
	 * 
	 * @param sql
	 * @param obj
	 */
	public static void update(Object sql, Object obj)
	{
		if (LRU_MAP.containsKey(sql))
		{
			add(sql, obj);
		}

	}

	/**
	 * 从二级缓存中拿数据
	 * 
	 * @param sql
	 * @return Object
	 */
	public static Object get(Object sql)
	{
		return LRU_MAP.get(sql);
	}

	/**
	 * 清空缓存
	 */
	public static void clear()
	{
		LRU_MAP.clear();
	}
	
	/**
	 * 获取容量大小,单位字节
	 * 
	 * @return
	 */
	public static int getSize()
	{
		return LRU_MAP.size();
	}

}
