package test.gaojy.cache.dbsource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Random;

import com.gaojy.cache.voltdb.core.VoltdbFactroy;
import com.gaojy.cache.voltdb.core.dbsource.AIBasicDataSource;
import com.gaojy.cache.voltdb.core.dbsource.opt.IVoltdbDao;

/**
 * @Title:
 * @Description:
 * @Author: gaojy
 * @Version: 1.0
 */
public class DbSourceTest
{
	static final IVoltdbDao dato = VoltdbFactroy.newInstance();

	public static void main(String[] args)
	{
		// test();
		read();

	/*	new Thread("A")
		{

			@Override
			public void run()
			{
				while (true)
				{
					System.out.println(this.getName() + " : begin ------------------------");
					read();
					System.out.println(this.getName() + " : end ------------------------");

					try
					{
						sleep(1000);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
		};
*/
	}

	public static void read()
	{
		Random r = new Random();

		ResultSet set;
		try
		{
			set = dato.executeQuery("select * from test");

			// String sql = "insert into dual values('"+r.nextInt(1000)+"')";

			// System.out.println(sql);

			// dato.executeUpdate(sql);
			
			ResultSetMetaData mdata = set.getMetaData();
			
			System.out.println(mdata.getColumnClassName(1));

			while (set.next())
			{
				System.out.println(set.getObject(1));
			}
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void test()
	{
		final AIBasicDataSource datas = new AIBasicDataSource();

		new Thread()
		{
			public void run()
			{
				while (true)
				{
					Connection con = datas.getConnection();
					System.out.println(this.getName() + "=" + con);
					try
					{
						con.close();
						sleep(1000);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					catch (SQLException e)
					{
						e.printStackTrace();
					}

				}
			};
		}.start();

		new Thread()
		{
			public void run()
			{
				while (true)
				{
					Connection con = datas.getConnection();
					System.out.println(this.getName() + "=" + con);
					try
					{
						con.close();
						sleep(1000);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					catch (SQLException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			};
		}.start();
	}
}
