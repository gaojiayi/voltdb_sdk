package com.gaojy.cache.voltdb.core.cfg.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @Title:
 * @Description:
 * @Author: gaojy
 * @Version: 1.0
 */
public class BaseCfg
{
	private Map<String, List<DBNode>> dbMaps = new HashMap<String, List<DBNode>>();

	// 负载类型
	private String loadType;
	
	//探测周期
	private long interval; 

	private LRUNode lruInfo;

	/**
	 * @return the loadType
	 */
	public String getLoadType()
	{
		return loadType;
	}

	/**
	 * @param loadType
	 *            the loadType to set
	 */
	public void setLoadType(String loadType)
	{
		this.loadType = loadType;
	}

	/**
	 * @return the lruInfo
	 */
	public LRUNode getLruInfo()
	{
		return lruInfo;
	}

	/**
	 * @param lruInfo
	 *            the lruInfo to set
	 */
	public void setLruInfo(LRUNode lruInfo)
	{
		this.lruInfo = lruInfo;
	}

	/**
	 * @return the dbMaps
	 */
	public Map<String, List<DBNode>> getDbMaps()
	{
		return dbMaps;
	}

	/**
	 * @return the interval
	 */
	public long getInterval()
	{
		return interval;
	}

	/**
	 * @param interval the interval to set
	 */
	public void setInterval(long interval)
	{
		this.interval = interval;
	}

	/**
	 * @param dbMaps
	 *            the dbMaps to set
	 */
	public void addDB(final DBNode db)
	{
		String key = db.getDbType().toUpperCase(Locale.getDefault());
		
		if (dbMaps.containsKey(key))
		{
			List<DBNode> dblist = dbMaps.get(key);

			dblist.add(db);

		}
		else
		{
			dbMaps.put(key, new ArrayList<DBNode>()
			{
				private static final long serialVersionUID = 7519108328881368626L;

				{
					add(db);
				}
			});
		}
	}
}
