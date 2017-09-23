package test.gaojy.cache;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.gaojy.cache.voltdb.core.VoltdbFactroy;
import com.gaojy.cache.voltdb.core.dbsource.opt.IVoltdbDao;
import com.gaojy.cache.voltdb.core.utils.AIUtils;

/**
 * @Title:
 * @Description:
 * @Author: gaojy
 * @Version: 1.0
 */
public class BacthSave
{
	static long total = 0;

	public static void main(String[] args)
	{
		long startTime = System.currentTimeMillis();

		new BacthSave().execute();

		long endTime = System.currentTimeMillis();

		float time = (endTime - startTime) / 1000f;

		System.out.println("time:" + time + "(s)");

		System.out.println("Byte size:" + total + "(byte)");

		System.out.println("kB/s:" + total / time / 1024);
	}

	private synchronized void setTotal(int total)
	{
		this.total += total;
	}

	private void execute()
	{
		int threadSize = 80;
		long totalSize = 100000;
		ExecutorService exs = Executors.newFixedThreadPool(threadSize);

		IVoltdbDao dato = VoltdbFactroy.newInstance();

		List<Future<?>> result = new ArrayList<Future<?>>();

		long ptotal = (totalSize % threadSize) == 0 ? totalSize / threadSize : totalSize;

		System.out.println("Total:" + totalSize);
		System.out.println("Queue:" + ptotal);
		List<Object[]> list = new ArrayList<Object[]>();

		for (int index = 1; index <= totalSize; index++)
		{
			list.add(new Object[] { "name_" + index, "address_" + index });

			if (index % ptotal == 0)
			{
				List<Object[]> temp = new ArrayList<Object[]>(list);
				result.add(exs.submit(new SaveData(dato, temp)));
				list.clear();
			}
		}

		if (!list.isEmpty())
		{
			result.add(exs.submit(new SaveData(dato, list)));
		}

		for (Future<?> f : result)
		{
			try
			{
				f.get();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			catch (ExecutionException e)
			{
				e.printStackTrace();
			}
		}

	}

	class SaveData implements Runnable
	{
		private IVoltdbDao dao;

		private List<Object[]> parameters;

		public SaveData(IVoltdbDao dao, List<Object[]> parameters)
		{
			this.dao = dao;
			this.parameters = parameters;
		}

		/*
		 * (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run()
		{
			save();
		}

		private void save()
		{
			new Thread()
			{
				/*
				 * (non-Javadoc)
				 * @see java.lang.Thread#run()
				 */
				@Override
				public void run()
				{
					count(parameters);
				}
			}.start();

			try
			{
				dao.executeBatch("insert into test values(?,?)", parameters, 500);
			}
			catch (SQLException e)
			{
				e.printStackTrace();
			}
		}

		private void count(List<Object[]> list)
		{
			try
			{
				for (Object[] objs : list)
				{
					setTotal(AIUtils.getBytesSize(objs));
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

	}

}
