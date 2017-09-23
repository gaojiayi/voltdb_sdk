package com.ai.aif.cache.voltdb.core.query.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.ai.aif.cache.voltdb.core.cache.LocalCache;
import com.ai.aif.cache.voltdb.core.cache.map.SizeObject;
import com.ai.aif.cache.voltdb.core.io.IOFactory;
import com.ai.aif.cache.voltdb.core.policy.LoadPoolFactory;
import com.ai.aif.cache.voltdb.core.pool.VoltDBObjectPool;
import com.ai.aif.cache.voltdb.core.pool.VoltDBObjectPoolFactory;
import com.ai.aif.cache.voltdb.core.query.IVoltdb;
import com.ai.aif.cache.voltdb.core.util.VoltdbSQLUtils;

public class VoltdbQuery implements IVoltdb
{
	private static final VoltdbQuery voltdbQuery = new VoltdbQuery();

	public static VoltdbQuery getInstance()
	{
		return voltdbQuery;
	}

	@Override
	public Object[] query(String sql, Object[] params) throws Exception
	{
		Object[] objs = null;
		sql = VoltdbSQLUtils.parseSQL(sql, params);
		objs = queryLocal(sql);
		if (null != objs && objs.length > 0)
		{
			// 日志：在缓存中找到数据，，，，，，，，，，，，，，，，，，，，，，，，，
			return objs;
		}
		// 日志:找不到相应缓存
		objs = queryRemote(sql);
		return objs;
	}

	/**
	 * 本地调用
	 * 
	 * @param sql
	 * @return
	 */
	private Object[] queryLocal(String sql)
	{
		Object[] objs = null;
		SizeObject so = LocalCache.getInstance().queryLocal(sql);
		if (null != so)
		{
			objs = (Object[]) so.getObj();
		}
		return objs;

	}

	/**
	 * 远程调用
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws Exception
	 */
	private Object[] queryRemote(String sql) throws Exception
	{ // sql = VoltdbSQLUtils.parseSQL(sql, params);
		Object[] objs = null;
		List<Object> list = null;
		VoltDBObjectPool pool = null;
		Connection conn = null;

		ResultSet rs = null;
		try
		{
			pool = LoadPoolFactory.getInstance().getPool();
			conn = (Connection) pool.borrowObject();
			// 对conn失效进行判断
			if(conn == null)
			{
				LoadPoolFactory.getInstance().deletePool(pool);
				int poolNum = LoadPoolFactory.getInstance().getPoolNum();
				if(poolNum >= 2)
				{
					for(int i =0; i < poolNum-1;i++)
					{
						pool = LoadPoolFactory.getInstance().getPool();
						conn = (Connection)pool.borrowObject();
						if(conn == null)
						{
							LoadPoolFactory.getInstance().deletePool(pool);
							continue;
						}
					}
					
				}
				//对配置文件中的所有连接循环判断都为无效
				if(conn == null)
				{
					//日志：所有连接均无效
					return null;
				}
			}
			
			
			rs = conn.prepareStatement(sql).executeQuery();
			ResultSetMetaData metaData = rs.getMetaData();
			int columnCount = metaData.getColumnCount();
			if (columnCount > 0)
			{
				list = new ArrayList<Object>();
				Map<String, Object> map = null;
				String columnName = null;
				while (rs.next())
				{
					map = new HashMap<String, Object>(columnCount);
					for (int i = 1; i <= columnCount; i++)
					{
						columnName = metaData.getColumnName(i);
						map.put(columnName, rs.getObject(i));
					}
					list.add(map);
				}
			}
			objs = list.toArray();
			// 加入到本地缓存
			int size = IOFactory.object2byte(objs).length;
			LocalCache.getInstance().addLocal(sql, new SizeObject(objs, size));

		}
		catch (Exception e)
		{
			//log.error(new StringBuffer(LOG_PREFIX).append("根据SQL【").append(sql).append("】,").append("参数").append(QcubicCollectionUtils.array2String(params)).append("远程查询Qcubic数据库数据出现异常"), e);
			throw e;
		}
		finally
		{
			if (null != rs)
			{
				rs.close();
			}
			if (null != pool && null != conn)
			{
				pool.returnObject(conn);
			}
		}
		return objs;

	}

	/**
	 * 修改
	 */
	@Override
	public boolean update(String sql, Object[] params) throws Exception
	{
		sql = VoltdbSQLUtils.parseSQL(sql, params);
		VoltDBObjectPool pool = null;
		Connection conn = null;

		try
		{
			pool = LoadPoolFactory.getInstance().getPool(); // 后期还需修改可能调用SDK
			conn = (Connection) pool.borrowObject();
			conn.createStatement().execute(sql);
		}
		catch (Exception e)
		{
			// 打印日志־
			throw e;
		}
		finally
		{
			if (null != pool && null != conn)
			{
				pool.returnObject(conn);
			}
		}
		return true;
	}

	@Override
	public ResultSet query(String sql) throws Exception
	{
		Connection conn = null;
		VoltDBObjectPool pool = null;
		ResultSet rs = null;
		try
		{
			pool = LoadPoolFactory.getInstance().getPool();
			conn = (Connection) pool.borrowObject();
			rs = conn.createStatement().executeQuery(sql);
		}
		catch (Exception e)
		{
			// TODO: handle exception
		}

		return rs;
	}

	@Override
	public <T> List<T> getBeans(Class<T> clazz, String sql, Object[] params) throws Exception 
	{   
		ResultSet rs = query(sql);
		ResultSetMetaData metaData = rs.getMetaData();
		List<T>  beans = new ArrayList<T>();
		sql = VoltdbSQLUtils.parseSQL(sql, params);
		if(rs == null)
		{
			return beans;
		}
		if(rs.next())
		{
			T entity = clazz.newInstance();
			for(int i = 0;i < metaData.getColumnCount();i++)
			{
				BeanUtils.setProperty(entity, metaData.getColumnName(i), rs.getObject(i));
			}
			beans.add(entity);
		}
		return beans;
	}

	private Connection getConnection() throws Exception
	{
		return (Connection) LoadPoolFactory.getInstance().getPool().borrowObject();
	}

	@Override
	public <T> List<T> getBeans(ResultSet rs, Class<T> clazz) throws Exception
	{
		ResultSetToBean<T> resultSetToBean = new ResultSetToBean<T>();
		
		return resultSetToBean.mapResultSetToObject(rs, clazz);
	}

	@Override
	public <T> List<T> getAPPBeans(Class<T> clazz, String sql) throws Exception
	{
		ResultSet rs = query(sql);
		
		return ResultSetToAppBean.getBeans(clazz, rs);
	}

	@Override
	public <T> T getAPPBean(Class<T> clazz, String sql) throws Exception
	{
		ResultSet rs = query(sql);
		return ResultSetToAppBean.getBean(clazz, rs);
	}

	@Override
	public void batchUpdate(List<String> sqls) throws Exception
	{
		Connection conn = null;
		Statement st = null;	
		try
		{
			conn = LoadPoolFactory.getInstance().getConnection();
			st = conn.createStatement();
			for(int i =0;i<sqls.size();i++)
			{
				st.addBatch(sqls.get(i));
				//每100条执行一次
				if((i+1)%100==0)
				{
					st.executeBatch();
				}
				if(i == sqls.size()-1)
				{
					st.executeBatch();
				}
			}
			
		}
		catch (Exception e)
		{
			// TODO: handle exception
		}
		
		
		
	}

}
