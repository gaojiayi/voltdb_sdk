package com.ai.aif.cache.voltdb.core.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ai.aif.cache.voltdb.core.query.impl.ResultSetToAppBean;
import com.ai.aif.cache.voltdb.core.spi.VoltdbClient;
import com.ai.aif.cache.voltdb.core.sync.ConnectionUtils;

public class DataCompareUtils
{
	public static Map getAlldifferData(String tableName)
	{
		String sql = "SELECT * FROM " + tableName;
		// 得到VoltDB某张表中的List<Map>
		List<Map<String, Object>> voltList;
		List<Map<String, Object>> orclList;
		// Set<Map<String, Object>> voltSet = new HashSet();
		Set<Map<String, Object>> orclSet = new HashSet();
		List<String> insertSql = new ArrayList();
		List<String> deleteSql = new ArrayList();
		try
		{
			voltList = ResultSetToAppBean.RsToList(VoltdbClient.getInstance().query(sql));
			orclList = ResultSetToAppBean.RsToList(ConnectionUtils.queryUseOrcl(sql));
			// Set<Map<String,Object>> sameElem = new HashSet<Map<String,Object>>();
			// 使用迭代器可以完成对相同元素remove操作，但是无法进行多次遍历，调用next（）至容器最后无法回到起点。
			// 而在增强for循环的过程中进行remove操作将会报ConcurrentModificationException异常.
			// Iterator<Map<String,Object>> iteratorVolt = voltList.iterator();
			// Iterator<Map<String,Object>> iteratorOrcl = orclList.iterator();
			/*
			 * for(Map map:voltList) { voltSet.add(map); }
			 */
			for (Map orclMap : orclList)
			{
				boolean isAvail = false;
				Map<String, Object> sameElem = new HashMap();
				for (Map voltMap : voltList)
				{

					if (compareMaps(voltMap, orclMap))
					{

						isAvail = true;

						sameElem = voltMap;
						break;
					}

				}
				if (isAvail == false)
				{
					orclSet.add(orclMap);
				}
				else
				{
					if (voltList.contains(sameElem))
					{
						voltList.remove(sameElem);
					}
				}
			}
			
			Map map = new HashMap();
			map.put("oracle", orclSet);
			map.put("voltdb", voltList);
			return map;
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	private static boolean compareMaps(Map<String, Object> map1, Map<String, Object> map2)
	{
		List<Boolean> list = new ArrayList();
		for (String key : map1.keySet())
		{
			list.add(map1.get(key).toString().equals(map2.get(key).toString()));
		}

		if (list.contains(Boolean.FALSE))
		{
			return false;
		}
		return true;

	}
}
