package test.gaojy.cache.proxy;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Test;

import com.gaojy.cache.voltdb.core.dbsource.VoltdbDaoImpl;
import com.gaojy.cache.voltdb.core.dbsource.opt.IVoltdbDao;
import com.gaojy.cache.voltdb.core.lru.LRUCache;
import com.gaojy.cache.voltdb.core.proxy.OptByCacheByLruImpl;
import com.gaojy.cache.voltdb.core.proxy.VoltdbDyncProxy;

/**
 * @Title:
 * @Description:
 * @Author: gaojy
 * @Version: 1.0
 */
public class ProxyTest
{
	VoltdbDyncProxy proxy = new VoltdbDyncProxy(new VoltdbDaoImpl(), OptByCacheByLruImpl.class);

	@Test
	public void test()
	{
		IVoltdbDao data = (IVoltdbDao) proxy.bind();

		try
		{
			ResultSet set = data.executeQuery("select * from EMPLOYEE");

			ResultSet set1 = data.executeQuery("select * from HELLOWORLD");

			ResultSet set2 = data.executeQuery("select * from test");

			System.out.println(set);
			System.out.println(set1);
			System.out.println(set2);

			Object obj = LRUCache.get("select * from EMPLOYEE");

			Object obj1 = LRUCache.get("select * from HELLOWORLD");

			Object obj2 = LRUCache.get("select * from test");

			System.out.println("-------------------------");

			System.out.println(obj);
			System.out.println(obj1);
			System.out.println(obj2);

			System.out.println("size :" + LRUCache.getSize());

			System.out.println("-----------------------");

			int i = data.executeUpdate("INSERT INTO EMPLOYEE VALUES ('003', 'java')");

			System.out.println(i);
			Object obj3 = LRUCache.get("INSERT INTO EMPLOYEE VALUES ('003', 'java')");
			System.out.println(obj3);

			Object obj4 = LRUCache.get("update EMPLOYEE set name='jack' where id='003'");
			System.out.println(obj4);

		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	@Test
	public void closeCon()
	{
		IVoltdbDao data = (IVoltdbDao) proxy.bind();
		
		try
		{
			ResultSet set = data.executeQuery("select * from EMPLOYEE");
			while(set.next())
			{
				System.out.println(set.getObject(1)+","+set.getObject(2));
			}
			
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
