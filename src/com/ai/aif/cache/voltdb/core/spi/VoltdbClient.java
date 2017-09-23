package com.ai.aif.cache.voltdb.core.spi;

import com.ai.aif.cache.voltdb.core.query.IVoltdb;
import com.ai.aif.cache.voltdb.core.query.impl.VoltdbQuery;

public class VoltdbClient
{
	private static IVoltdb iVoltdb;
	static{
		iVoltdb = VoltdbQuery.getInstance();
		//增加定时任务调度
	}

	public VoltdbClient(){}
	
	public static IVoltdb getInstance(){
		return iVoltdb;
	}
	
	public static IVoltdb getVoltdbObj(){
		return iVoltdb;
	}
}
