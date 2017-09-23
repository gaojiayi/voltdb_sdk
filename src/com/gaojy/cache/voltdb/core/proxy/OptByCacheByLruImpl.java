package com.gaojy.cache.voltdb.core.proxy;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.gaojy.cache.voltdb.core.cfg.DBConfigSource;
import com.gaojy.cache.voltdb.core.lru.LRUCache;
import com.gaojy.cache.voltdb.core.proxy.opt.IOptByCache;
import com.gaojy.cache.voltdb.core.task.DetectResultSet;
import com.gaojy.cache.voltdb.core.utils.AIUtils;

/**
 * @Title: 二级缓存的存储模型
 * @Description:
 * @Author: gaojy
 * @Version: 1.0
 */
public class OptByCacheByLruImpl implements IOptByCache
{
	private static DBConfigSource dbcfg = DBConfigSource.getInstance();

	/*
	 * (non-Javadoc)
	 * @see com.gaojy.cache.voltdb.core.proxy.opt.IOptByCache#before()
	 */
	@Override
	public Object before(String sql)
	{
		if (!dbcfg.getDBConfig().getLruInfo().isOpen())
		{
			return null;
		}
		return LRUCache.get(sql);
	}

	/*
	 * (non-Javadoc)
	 * @see com.gaojy.cache.voltdb.core.proxy.opt.IOptByCache#after()
	 */
	@Override
	public Object after(String sql, Object objs)
	{
		updateLRU(sql, objs);

		Connection con = AIUtils.getConnection();
		
		if (objs instanceof ResultSet)
		{
			new DetectResultSet((ResultSet) objs, con).start();
		}
		else
		{
			try
			{
				if (!con.isClosed())
				{
					con.close();
				}
			}
			catch (SQLException e)
			{
				// on nothing
			}
			finally
			{
				AIUtils.remove();
			}
		}

		return null;
	}

	/**
	 * 更新二级缓存
	 * 
	 * @param sql
	 * @param objs
	 */
	private void updateLRU(String sql, Object objs)
	{
		if (!dbcfg.getDBConfig().getLruInfo().isOpen())
		{
			return;
		}
		String temp = sql.toLowerCase(Locale.getDefault());

		if (temp.contains("update") || temp.contains("delete"))
		{
			LRUCache.clear();
		}
		else if (temp.contains("select"))
		{
			LRUCache.update(sql, objs);
		}
		else
		{
			// do nothing
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.gaojy.cache.voltdb.core.proxy.opt.IOptByCache#rule()
	 */
	@Override
	public List<String> rule()
	{
		return new ArrayList<String>()
		{
			private static final long serialVersionUID = 1L;
			{
				add("executeQuery");
				add("executeUpdate");
			}
		};
	}

}
