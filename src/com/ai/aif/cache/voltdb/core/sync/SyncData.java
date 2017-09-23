package com.ai.aif.cache.voltdb.core.sync;

import java.util.TimerTask;

public class SyncData  extends  TimerTask
{
	public static String[] tables = ConnectionUtils.getTables();
	@Override
	public void run()
	{
		for(String table : tables)
		{
			new SyncOneTableTask(table).start();
		}
		
	}

}
