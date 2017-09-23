package com.gaojy.cache.voltdb.core.lru;

import java.io.IOException;

import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gaojy.cache.voltdb.core.utils.AIUtils;

/**
 * @Title:
 * @Description:
 * @Author: gaojy
 * @Version: 1.0
 */
public class AiLRUMap extends LRUMap
{
	private static final Log LOGGER = LogFactory.getLog(AiLRUMap.class);

	/** Serialisation version **/
	private static final long serialVersionUID = -6194834795147998905L;

	// private static final Object lock = new Object();

	/** Max byte Size */
	private transient long maxByteSize;

	/** current size */
	private transient long byteSize;

	/**
	 * 设置字节大小,单位为M
	 * 
	 * @param maxByteSize
	 */
	public AiLRUMap(final int maxByteSize)
	{
		super(DEFAULT_MAX_SIZE, DEFAULT_LOAD_FACTOR);

		this.maxByteSize = maxByteSize * 1024 * 1024;
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.commons.collections.map.LRUMap#isFull()
	 */
	@Override
	public boolean isFull()
	{
		return byteSize >= maxByteSize;
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.commons.collections.map.LRUMap#addMapping(int, int, java.lang.Object, java.lang.Object)
	 */
	@Override
	protected void addMapping(int hashIndex, int hashCode, Object key, Object value)
	{
		try
		{
			byteSize += AIUtils.getBytesSize(value);
		}
		catch (IOException e)
		{
			LOGGER.error(e);
		}
		super.addMapping(hashIndex, hashCode, key, value);
	}

	/**
	 * 获取内在大小，单位M
	 * 
	 * @return double
	 */
	public double getSize()
	{
		return byteSize / 1024d / 1024d;
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.commons.collections.map.AbstractHashedMap#size()
	 */
	@Override
	public int size()
	{
		return (int)byteSize;
	}
}
