package test;

import java.util.Timer;

import com.ai.aif.cache.voltdb.core.policy.LoadPoolFactory;
import com.ai.aif.cache.voltdb.core.pool.VoltDBObjectPool;
import com.ai.aif.cache.voltdb.core.sync.Oracle2VoltdbInit;
import com.ai.aif.cache.voltdb.core.sync.SyncData;
import com.ai.aif.cache.voltdb.core.sync.SyncOneTableTask;

public class TestSync
{
	public static void main(String[] args) throws Exception
	{
		//Oracle2VoltdbInit.importDataFirst();
		/*Timer timer = new Timer();
		timer.schedule(new SyncData(), 3);*/
		
		new SyncData().run();
	}
	
}
