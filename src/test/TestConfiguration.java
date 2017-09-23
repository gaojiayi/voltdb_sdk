package test;

import java.util.List;

import com.ai.aif.cache.voltdb.core.config.Configuration;
import com.ai.aif.cache.voltdb.core.config.node.PoolNode;
import com.ai.aif.cache.voltdb.core.config.node.PropertyNode;
import com.ai.aif.cache.voltdb.core.policy.LoadPoolFactory;
import com.ai.aif.cache.voltdb.core.pool.VoltDBObjectPool;
import com.ai.aif.cache.voltdb.core.pool.VoltDBObjectPoolFactory;

public class TestConfiguration
{
public static void main(String[] args) throws Exception
{
	
	
	
	VoltDBObjectPoolFactory factory = new VoltDBObjectPoolFactory("","",
			"gaojy", "", "1192.168.81.127", "21212");
	VoltDBObjectPool pool = new VoltDBObjectPool(factory);
	System.out.println(pool);
	
	PoolNode[] pools = Configuration.getInstance().getPools();
	VoltDBObjectPool makePool = LoadPoolFactory.getInstance().makePool(pools[0]);
	System.out.println(makePool.toString());
	
	
	
	/*List<PoolNode> allPoolNodes = LoadPoolFactory.getInstance().getAllPoolNodes();
	System.out.println(allPoolNodes);*/
	
	
	///PoolNode[] pools = Configuration.getInstance().getPools();
	PropertyNode port = pools[0].getPropMap().get("port");
	System.out.println(port.getValue());
}
}
