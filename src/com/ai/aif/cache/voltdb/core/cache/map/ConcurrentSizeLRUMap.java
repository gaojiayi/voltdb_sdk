package com.ai.aif.cache.voltdb.core.cache.map;

import java.util.Set;
import java.util.*;

/**
 * 
 * 
 * @Title: 本地cache对象
 * @Description: 
 * @Copyright: Copyright (c) 2016 CMC CRM TA Dept-NJ All Rights Reserved
 * @Company: Asiainfo
 * @Author: gaojy5
 * @Version: 1.0
 */
public class ConcurrentSizeLRUMap
{
	private int bucketCount = 1;

	private int limitBytes = 0;

	private SizeLRUMap[] bucket = null;

	private long startTime = System.currentTimeMillis();

	/**
	 * @param bucketCount
	 *            int
	 * @param limitBytes
	 *            int
	 */
	public ConcurrentSizeLRUMap(int bucketCount, int limitBytes)
	{
		this.bucketCount = bucketCount;
		this.limitBytes = limitBytes;

		bucket = new SizeLRUMap[bucketCount];
		for (int i = 0; i < bucket.length; i++)
		{
			bucket[i] = (new SizeLRUMap(100, limitBytes / bucketCount));
		}
	}

	/**
	 * @param hashCode
	 *            int
	 * @return SizeLRUMap
	 */
	private SizeLRUMap getBucket(int hashCode)
	{
		return (SizeLRUMap) bucket[Math.abs(hashCode) % bucketCount];
	}

	/**
	 * @param key
	 *            Object
	 * @return Object
	 */
	public Object get(Object key)
	{
		return getBucket(key.hashCode()).get(key);
	}

	/**
	 * @param key
	 *            Object
	 * @return boolean
	 */
	public boolean containsKey(Object key)
	{
		return getBucket(key.hashCode()).containsKey(key);
	}

	/**
	 * @param key
	 *            Object
	 * @param value
	 *            Object
	 * @return Object
	 */
	public Object put(Object key, Object value)
	{
		return getBucket(key.hashCode()).put(key, value);
	}

	public void clear()
	{
		for (int i = 0; i < bucket.length; i++)
		{
			bucket[i].clear();
		}

		startTime = System.currentTimeMillis();
	}

	/**
	 * @param num
	 *            long
	 * @return List
	 */
	public List getHotKeys(long num)
	{
		List list = new ArrayList();

		long perCount = (num / bucket.length) + 1;
		for (int i = 0; i < bucket.length; i++)
		{
			long j = 0;
			Set keys = bucket[i].keySet();
			for (Iterator iter = keys.iterator(); iter.hasNext();)
			{
				Object item = (Object) iter.next();
				list.add(item);
				j++;

				if (j >= perCount)
				{
					break;
				}
			}
		}

		return list;
	}

	/**
	 * @return int
	 */
	public int size()
	{
		int size = 0;
		for (int i = 0; i < bucket.length; i++)
		{
			size += bucket[i].size();
		}
		return size;
	}

	/**
	 * @return long
	 */
	public long getHit()
	{
		long hit = 0;
		for (int i = 0; i < bucket.length; i++)
		{
			hit += bucket[i].getHit();
		}
		return hit;
	}

	/**
	 * @return long
	 */
	public long getMiss()
	{
		long miss = 0;
		for (int i = 0; i < bucket.length; i++)
		{
			miss += bucket[i].getMiss();
		}
		return miss;
	}

	/**
	 * @return long
	 */
	public long getEvict()
	{
		long evict = 0;
		for (int i = 0; i < bucket.length; i++)
		{
			evict += bucket[i].getEvict();
		}
		return evict;
	}

	public long getOverload()
	{
		long overload = 0;
		for (int i = 0; i < bucket.length; i++)
		{
			overload += bucket[i].getOverload();
		}
		return overload;
	}

	/**
	 * @return long
	 */
	public long getCurrentByteSize()
	{
		int byteSize = 0;
		for (int i = 0; i < bucket.length; i++)
		{
			byteSize += bucket[i].getCurrentByteSize();
		}
		return byteSize;
	}

	/**
	 * @return int
	 */
	public int getBucketCount()
	{
		return this.bucketCount;
	}

	/**
	 * @return int
	 */
	public int getLimitBytes()
	{
		return this.limitBytes;
	}

	/**
	 * @return long
	 */
	public long getStartTime()
	{
		return this.startTime;
	}
}
