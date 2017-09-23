package com.gaojy.cache.voltdb.core.dbsource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gaojy.cache.voltdb.core.dbsource.opt.IDataLoader;
import com.gaojy.cache.voltdb.core.utils.BulkLoaderErrorHandler;
import com.gaojy.cache.voltdb.core.utils.RowWithMetaData;
import com.gaojy.cache.voltdb.core.utils.VoltBulkLoader;

/**
 * @Title:
 * @Description:
 * @Author: gaojy
 * @Version: 1.0
 */
public class DataLoaderImpl implements IDataLoader
{
	private static final Log LOGGER = LogFactory.getLog(DataLoaderImpl.class);

	private static final List<FutureTask<String>> FUTURES = new ArrayList<FutureTask<String>>();

	private final ExecutorService m_es;

	private BulkLoaderErrorHandler m_errHandler;

	private VoltBulkLoader bulkloader;

	private LinkedBlockingQueue<Arrays> m_partitionRowQueue;

	private volatile int batchSize;

	public DataLoaderImpl(BulkLoaderErrorHandler m_errHandler, VoltBulkLoader bulkloader)
	{
		this.m_errHandler = m_errHandler;
		this.bulkloader = bulkloader;
		this.batchSize = bulkloader.getMaxBatchSize();
		m_es = getThreadPool(bulkloader.getTableName());
		m_partitionRowQueue = new LinkedBlockingQueue<Arrays>(batchSize * 5);
	}

	public ExecutorService getThreadPool(final String name)
	{
		return new ThreadPoolExecutor(2, 5, 3, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
				new ThreadFactory()
				{
					@Override
					public synchronized Thread newThread(Runnable r)
					{
						Thread thread = new Thread(r);
						thread.setDaemon(true);
						thread.setName(name);
						return thread;
					}
				}, new ThreadPoolExecutor.DiscardOldestPolicy());
	}

	/***
	 * update batch size
	 * 
	 * @param batchSize
	 */
	public void updateBatchSize()
	{
		int size = m_partitionRowQueue.size();

		if (size != 0 && size < batchSize)
		{
			this.batchSize = m_partitionRowQueue.size();
			executeBacth();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.gaojy.cache.voltdb.core.dbsource.opt.IDataLoader#insertRow(com.gaojy.cache.voltdb.core.utils.RowWithMetaData
	 * , java.lang.Object[])
	 */
	@Override
	public void insertRow(RowWithMetaData data, Object[] obj)
	{
		try
		{
			Arrays array = new Arrays(bulkloader, obj);
			array.setData(data);
			addBacth(array);
		}
		catch (InterruptedException e)
		{
			LOGGER.error(e);
		}
	}

	private void execute()
	{
		List<Arrays> list = new ArrayList<Arrays>();
		Connection conn = null;
		int index = 0;
		try
		{
			conn = bulkloader.getConnection();
			// conn.setAutoCommit(false);
			PreparedStatement ps = null;
			PreparedStatement bps = null;
			PreparedStatement dps = null;
			m_partitionRowQueue.drainTo(list);
			bps = conn.prepareStatement(insertSQL(list.get(0).getObj().length));
			for (Arrays objs : list)
			{
				if (bulkloader.isUpdate())
				{
					String pKey = null;
					int pValue = 0;
					for (Map.Entry<String, Integer> en : objs.getVloader().getPkey().entrySet())
					{
						pKey = en.getKey();
						pValue = en.getValue();
						break;
					}

					ps = conn.prepareStatement(selectSQL(pKey));

					ps.setObject(1, objs.getObj()[pValue]);

					boolean flag = ps.executeQuery().wasNull();

					if (!flag)
					{
						dps = conn.prepareStatement(delSQL(pKey));
						dps.setObject(1, objs.getObj()[pValue]);
						dps.execute();
					}
				}
				setValue(bps, objs.getObj());
				bps.addBatch();
				index++;
			}
			bps.executeBatch();
		}
		catch (SQLException e)
		{
			int i = 0;
			for (Arrays array : list)
			{
				if (i >= index)
				{
					break;
				}
				m_errHandler.handleError(array.data, null, "Failed to load batch:" + e.toString());
				i++;
			}
		}
		finally
		{
			if (null != conn)
			{
				try
				{
					// conn.commit();
					conn.close();
				}
				catch (SQLException e)
				{
					LOGGER.error(e);
				}
			}
		}
	}

	public void close()
	{
		for (FutureTask<String> fu : FUTURES)
		{
			try
			{
				fu.get();
			}
			catch (InterruptedException e1)
			{
				e1.printStackTrace();
			}
			catch (ExecutionException e1)
			{
				e1.printStackTrace();
			}
		}
		
		System.out.println("Data loader done!");
		
		m_es.shutdown();
		try
		{
			m_es.awaitTermination(365, TimeUnit.DAYS);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	private void setValue(PreparedStatement ps, Object[] objs) throws SQLException
	{
		int i = 1;
		for (Object ob : objs)
		{
			ps.setObject(i, ob);
			i++;
		}
	}

	private void addBacth(Arrays array) throws InterruptedException
	{
		m_partitionRowQueue.put(array);

		executeBacth();
	}

	/**
	 * execute bacth
	 */
	private void executeBacth()
	{
		if (m_partitionRowQueue.size() == batchSize)
		{
			FutureTask<String> fu = new FutureTask<String>(new Callable<String>()
			{
				@Override
				public String call() throws Exception
				{
					while (m_partitionRowQueue.size() >= batchSize)
					{
						execute();
					}
					return "1";
				}
			});

			m_es.submit(fu);

			FUTURES.add(fu);
		}
	}

	private String insertSQL(int colSize)
	{
		String tableName = bulkloader.getTableName();
		StringBuffer sb = new StringBuffer("insert into ");
		sb.append(tableName);
		sb.append(" values(");
		for (int i = 0; i < colSize; i++)
		{
			sb.append("?").append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		sb.append(")");

		return sb.toString();
	}

	private String selectSQL(String pkey)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("select * from ");
		sb.append(bulkloader.getTableName());
		sb.append(" where ");
		sb.append(pkey);
		sb.append("=");
		sb.append("?");
		return sb.toString();
	}

	private String delSQL(String pkey)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("delete from ");
		sb.append(bulkloader.getTableName());
		sb.append(" where ");
		sb.append(pkey);
		sb.append("=");
		sb.append("?");
		return sb.toString();
	}

	class Arrays
	{
		private VoltBulkLoader vloader;

		private RowWithMetaData data;

		private Object[] obj;

		/**
		 * @param vloader
		 * @param obj
		 */
		public Arrays(VoltBulkLoader vloader, Object[] obj)
		{
			super();
			this.vloader = vloader;
			this.obj = obj;
		}

		/**
		 * @return the data
		 */
		public RowWithMetaData getData()
		{
			return data;
		}

		/**
		 * @param data
		 *            the data to set
		 */
		public void setData(RowWithMetaData data)
		{
			this.data = data;
		}

		/**
		 * @return the vloader
		 */
		public VoltBulkLoader getVloader()
		{
			return vloader;
		}

		/**
		 * @param vloader
		 *            the vloader to set
		 */
		public void setVloader(VoltBulkLoader vloader)
		{
			this.vloader = vloader;
		}

		/**
		 * @return the obj
		 */
		public Object[] getObj()
		{
			return obj;
		}

		/**
		 * @param obj
		 *            the obj to set
		 */
		public void setObj(Object[] obj)
		{
			this.obj = obj;
		}
	}
}
