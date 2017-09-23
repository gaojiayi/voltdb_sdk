package com.ai.aif.cache.voltdb.core.cache.map;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.collections.BoundedMap;
/**
 * 
 * 
 * @Title: 基于LRU算法的Map
 * @Description: 
 * @Copyright: Copyright (c) 2016 CMC CRM TA Dept-NJ All Rights Reserved
 * @Company: Asiainfo
 * @Author: gaojy5
 * @Version: 1.0
 */
public class SizeLRUMap extends AbstractLinkedMap implements BoundedMap,
		Serializable, Cloneable
{

	static final long serialVersionUID = -612114643488955218L;

	private int maxByteSize;
	private int overloadByteSize;

	private AtomicLong CURRENT_BYTE_SIZE = new AtomicLong(0);

	private AtomicLong MISS_COUNT = new AtomicLong(0);
	private AtomicLong HIT_COUNT = new AtomicLong(0);
	private AtomicLong EVICT_COUNT = new AtomicLong(0);
	private AtomicLong OVERLOAD_COUNT = new AtomicLong(0);

	private Object mutex = null;

	public SizeLRUMap(int initialCapacity, int maxByteSize)
	{
		this(initialCapacity, DEFAULT_LOAD_FACTOR, maxByteSize);
	}

	public SizeLRUMap(int initialCapacity, float loadFactor, int maxByteSize)
	{
		super((initialCapacity < 1 ? DEFAULT_CAPACITY : initialCapacity),
				loadFactor);
		if (initialCapacity < 1)
		{
			throw new IllegalArgumentException(
					"map max size must be greater than 0");
		}

		this.maxByteSize = maxByteSize;
		this.overloadByteSize = (int) (maxByteSize * 1.2);
		mutex = this;
	}

	public Object get(Object key)
	{
		synchronized (mutex)
		{
			LinkEntry entry = (LinkEntry) getEntry(key);
			if (entry == null)
			{
				MISS_COUNT.incrementAndGet();
				return null;
			}

			HIT_COUNT.incrementAndGet();

			moveToMRU(entry);
			return entry.getValue();
		}
	}

	public boolean containsKey(Object key)
	{
		boolean rtn = false;

		synchronized (mutex)
		{
			rtn = super.containsKey(key);
		}

		if (!rtn)
		{
			MISS_COUNT.incrementAndGet();
		}

		return rtn;
	}

	public Object put(Object key, Object value)
	{
		if (!(value instanceof SizeObject))
		{
			throw new RuntimeException("value " + value.getClass()
					+ " must be instanceof SizeObject");
		}

		synchronized (mutex)
		{
			return super.put(key, value);
		}
	}

	public void clear()
	{
		synchronized (mutex)
		{
			super.clear();
		}

		CURRENT_BYTE_SIZE.getAndSet(0);
		MISS_COUNT.getAndSet(0);
		HIT_COUNT.getAndSet(0);
		EVICT_COUNT.getAndSet(0);
		OVERLOAD_COUNT.getAndSet(0);
	}

	/**
	 * @return boolean
	 */
	public boolean isEmpty()
	{
		synchronized (mutex)
		{
			return super.isEmpty();
		}
	}

	/**
	 * @return int
	 */
	public int size()
	{
		synchronized (mutex)
		{
			return super.size();
		}
	}

	protected void moveToMRU(LinkEntry entry)
	{
		if (entry.after != header)
		{
			modCount++;
			// remove
			entry.before.after = entry.after;
			entry.after.before = entry.before;
			// add first
			entry.after = header;
			entry.before = header.before;
			header.before.after = entry;
			header.before = entry;
		}
	}

	protected void updateEntry(HashEntry entry, Object newValue)
	{
		moveToMRU((LinkEntry) entry); // handles modCount
		entry.setValue(newValue);
	}

	protected void addMapping(int hashIndex, int hashCode, Object key,
			Object value)
	{
		if (!(value instanceof SizeObject))
		{
			throw new RuntimeException("value " + value.getClass()
					+ " must be instanceof SizeObject");
		}

		int size = ((SizeObject) value).getSize();

		long curByteSize = CURRENT_BYTE_SIZE.addAndGet(size);

		if (curByteSize >= overloadByteSize)
		{
			boolean isOverLoad = true;
			if (header != null && header.after != null)
			{
				SizeObject tail = (SizeObject) header.after.value;
				if (tail != null && tail.getSize() >= size)
				{
					isOverLoad = false;
				}
			}

			if (isOverLoad)
			{
				CURRENT_BYTE_SIZE.addAndGet(-size);

				OVERLOAD_COUNT.incrementAndGet();
				return;
			}
		}

		if (curByteSize >= maxByteSize && removeLRU(header.before))
		{
			reuseMapping(header.after, hashIndex, hashCode, key, value);
			EVICT_COUNT.incrementAndGet();
		}
		else
		{
			super.addMapping(hashIndex, hashCode, key, value);
		}
	}

	protected void reuseMapping(LinkEntry entry, int hashIndex, int hashCode,
			Object key, Object value)
	{
		// find the entry before the entry specified in the hash table
		// remember that the parameters (except the first) refer to the new
		// entry,
		// not the old one
		int removeIndex = hashIndex(entry.hashCode, data.length);
		HashEntry loop = data[removeIndex];
		HashEntry previous = null;
		while (loop != entry)
		{
			previous = loop;
			loop = loop.next;
		}

		// reuse the entry
		modCount++;

		if (entry != null)
		{
			CURRENT_BYTE_SIZE.addAndGet(-((SizeObject) entry.value).getSize());
		}

		removeEntry(entry, removeIndex, previous);
		reuseEntry(entry, hashIndex, hashCode, key, value);
		addEntry(entry, hashIndex);
	}

	/**
	 * @param entry
	 *            LinkEntry
	 * @return boolean
	 */
	protected boolean removeLRU(LinkEntry entry)
	{
		return true;
	}

	/**
	 * 
	 * @return boolean
	 */
	public boolean isFull()
	{
		return (CURRENT_BYTE_SIZE.get() >= maxByteSize);
	}

	/**
	 * 
	 * @return int
	 */
	public int maxSize()
	{
		return maxByteSize;
	}

	/**
	 * 
	 * @return Object
	 */
	public Object clone()
	{
		return super.clone();
	}

	/**
	 * 
	 * @return long
	 */
	public long getHit()
	{
		return HIT_COUNT.get();
	}

	/**
	 * 
	 * @return long
	 */
	public long getMiss()
	{
		return MISS_COUNT.get();
	}

	/**
	 * 
	 * @return long
	 */
	public long getEvict()
	{
		return EVICT_COUNT.get();
	}

	/**
	 * 
	 * @return long
	 */
	public long getOverload()
	{
		return OVERLOAD_COUNT.get();
	}

	/**
	 * 
	 * @return long
	 */
	public long getCurrentByteSize()
	{
		return CURRENT_BYTE_SIZE.get();
	}
}
