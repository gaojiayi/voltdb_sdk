package test.gaojy.cache.dbsource;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.gaojy.cache.voltdb.core.VoltdbFactroy;
import com.gaojy.cache.voltdb.core.dbsource.opt.IVoltdbDao;

/**
 * @Title:
 * @Description:
 * @Author: gaojy
 * @Version: 1.0
 */
public class QueryTest
{

	static final IVoltdbDao dato = VoltdbFactroy.newInstance();

	public static void main(String[] args)
	{
		long btime = System.currentTimeMillis();
		try
		{
			List<?> lists = dato.executeQuery("select * from dual", Empoyee.class);
			dato.executeUpdate("", "","","","");

			for (Object en : lists)
			{
				System.out.println(en);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		long enTime = System.currentTimeMillis();

		System.out.println((enTime - btime) / 1000f);

		ResultSet set;
		try
		{
			btime = System.currentTimeMillis();
			set = dato.executeQuery("select * from EMPLOYEE");
			while (set.next())
			{
				System.out.println(set.getObject(1) + "," + set.getObject(2));
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		System.out.println((endTime - btime) / 1000f);
	}
}
