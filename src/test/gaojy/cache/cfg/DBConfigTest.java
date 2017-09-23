package test.gaojy.cache.cfg;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.gaojy.cache.voltdb.core.cfg.DBConfigSource;
import com.gaojy.cache.voltdb.core.cfg.pojo.DBNode;

/**
 * 
 * @Title: 
 * @Description: 
 * @Author: gaojy
 * @Version: 1.0
 */
public class DBConfigTest
{

	@Test
	public void test()
	{
		DBConfigSource db = DBConfigSource.getInstance();
		
		Map<String, List<DBNode>> maps = db.getDBConfig().getDbMaps();
		
		
		for (Map.Entry<String, List<DBNode>> en : maps.entrySet())
		{
			System.out.println(en.getKey());
			System.out.println("------------------------");
			for (DBNode node : en.getValue())
			{
				System.out.println(node);
			}
		}
		System.out.println("------------------------");
		System.out.println(db.getDBConfig().getLruInfo());
	}

}
