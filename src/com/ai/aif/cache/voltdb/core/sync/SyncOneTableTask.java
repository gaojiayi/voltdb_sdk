package com.ai.aif.cache.voltdb.core.sync;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimerTask;

import com.ai.aif.cache.voltdb.core.query.impl.ResultSetToAppBean;
import com.ai.aif.cache.voltdb.core.spi.VoltdbClient;

/**
 * @Title:数据一致性对比，Oracle中数据定时同步到VoltDB中
 * @Description:解决两种数据库数据类型差异，及oracle中差异数据在voltdb中新增，voltdb中差异数据进行删除
 * @Copyright: Copyright (c) 2016 CMC CRM TA Dept-NJ All Rights Reserved
 * @Company: Asiainfo
 * @Author: gaojy5
 * @Version: 1.0
 */
public class SyncOneTableTask extends Thread
{

	// public static String[] tables = ConnectionUtils.getTables();
	private String table;

	public SyncOneTableTask(String tableName)
	{
		this.table = tableName;
	}

	public void run()
	{

		String sql = "SELECT * FROM " + table;
		// 得到VoltDB某张表中的List<Map>
		List<Map<String, Object>> voltList;
		List<Map<String, Object>> orclList;
		// Set<Map<String, Object>> voltSet = new HashSet();
		Set<Map<String, Object>> orclSet = new HashSet();
		List<String>   insertSql = new ArrayList();
		List<String>   deleteSql = new ArrayList();
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

			// 对oracle中数据不一致数据在voltdb中新增
			if (null != orclSet && orclSet.size() > 0)
			{
				// 新增操作
				for (Map<String, Object> remainOfOrcl : orclSet)
				{
					StringBuilder sb = new StringBuilder();
					StringBuilder columns = new StringBuilder();
					for (String key : remainOfOrcl.keySet())
					{
						Object value = remainOfOrcl.get(key);
						if (value instanceof String)
						{
							value = "'" + value + "'";
						}
						if (value instanceof java.sql.Date)
						{
							java.util.Date utilDate = new java.util.Date(((java.sql.Date) value).getTime());
							value = new Timestamp(utilDate.getTime());

						}

						sb.append(value).append(",");
						columns.append(key).append(",");

					}

					if (sb.lastIndexOf(",") == sb.length() - 1)
					{
						sb.deleteCharAt(sb.length() - 1);

					}
					if (columns.lastIndexOf(",") == columns.length() - 1)
					{
						columns.deleteCharAt(columns.length() - 1);

					}

					// 多线程+批量新增
					insertSql.add("INSERT INTO " + table + "(" + columns.toString() + ")  VALUES ("
							+ sb.toString() + ")");
					
					System.out.println("INSERT INTO " + table + "(" + columns.toString() + ")  VALUES ("
							+ sb.toString() + ")");
					/*VoltdbClient.getInstance().update(
							"insert into " + table + "(" + columns.toString() + ")  values (" + sb.toString() + ")",
							null);*/
				}
				
				VoltdbClient.getInstance().batchUpdate(insertSql);
			}

			// 对voltdb中不一致的数据进行删除
			if (null != voltList && voltList.size() > 0)
			{
				// 删除操作传入sql delete from table where
				for (Map<String, Object> remainOfVolt : voltList)
				{
					StringBuilder sb = new StringBuilder();
					for (String key : remainOfVolt.keySet())
					{
						Object value = remainOfVolt.get(key);
						if (value instanceof String)
						{
							value = "'" + value + "'";
						}

						sb.append(key + " = " + value).append(" AND ");

					}
					sb.delete(sb.lastIndexOf("AND"), sb.length() - 1);
					// 多线程+批量delete
					deleteSql.add("DELETE FROM " + table + " WHERE " + sb.toString());
					System.out.println("DELETE FROM " + table + " WHERE " + sb.toString());
					//VoltdbClient.getInstance().update("delete from " + table + " where " + sb.toString(), null);
				}
				VoltdbClient.getInstance().batchUpdate(deleteSql);
			}
			
			
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();

		}

	}

	/**
	 * 比较两条数据是否一致
	 * 
	 * @param coll_volt
	 * @param coll_orcl
	 * @return
	 */
	public boolean compareCollections(Collection coll_volt, Collection coll_orcl)
	{
		// 比较日期类型！！！
		List<Boolean> list = new ArrayList();
		for (Object obj : coll_orcl)
		{
			if (obj instanceof java.sql.Date)
			{
				java.util.Date utilDate = new java.util.Date(((java.sql.Date) obj).getTime());
				obj = new Timestamp(utilDate.getTime());
			}

			/*
			 * if(obj instanceof BigDecimal) { obj = new BigDecimal)obj; }
			 */
			list.add(coll_volt.contains(obj));

		}
		if (list.contains(false))
		{
			return false;
		}
		return true;

	}

	/**
	 * 比较两条数据是否相同
	 * 
	 * @param map1
	 * @param map2
	 * @return
	 */
	public boolean compareMaps(Map<String, Object> map1, Map<String, Object> map2)
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

	/**
	 * 迭代器转list
	 * 
	 * @param iter
	 * @return
	 */
	public static <T> List<T> copyIterator(Iterator<T> iter)
	{
		List<T> copy = new ArrayList<T>();
		while (iter.hasNext())
			copy.add(iter.next());
		return copy;
	}

}
