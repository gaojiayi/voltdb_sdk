package com.ai.aif.cache.voltdb.core.config;

import java.io.File;
import java.io.InputStream;
import java.util.Timer;

import org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ai.aif.cache.voltdb.core.config.node.CheckTaskNode;
import com.ai.aif.cache.voltdb.core.config.node.DatasourceNode;
import com.ai.aif.cache.voltdb.core.config.node.IbsNode;
import com.ai.aif.cache.voltdb.core.config.node.LocalCacheNode;
import com.ai.aif.cache.voltdb.core.config.node.PoolNode;
import com.ai.aif.cache.voltdb.core.config.node.PropertyNode;
import com.ai.aif.cache.voltdb.core.config.node.VoltdbNode;
import com.ai.aif.cache.voltdb.core.constant.Constants;
import com.ai.aif.cache.voltdb.core.task.ReloadConfig;

/**
 * @Title:
 * @Description:
 * @Copyright: Copyright (c) 2016 CMC CRM TA Dept-NJ All Rights Reserved
 * @Company: Asiainfo
 * @Author: gaojy5
 * @Version: 1.0
 */
public class Configuration
{
	private static transient Log log = LogFactory.getLog(Configuration.class);
	private static Configuration instance = null;
	private static VoltdbNode voltdbConfig = null;
	private static Boolean isInit = Boolean.FALSE;

	private Configuration()
	{
		//startup();
	}

	private static VoltdbNode loadConfiguration() throws Exception
	{
		String cnf = Constants.Config.CONFIG_FILE;
		InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(cnf);
		if (input == null)
		{
			throw new Exception("找不到文件");
		}
		Digester digester = new Digester();
		digester.setValidating(false);

		digester.addObjectCreate("voltdb", VoltdbNode.class.getName());
		digester.addSetProperties("voltdb");

		digester.addObjectCreate("voltdb/localcache", LocalCacheNode.class.getName());
		digester.addSetProperties("voltdb/localcache");
		digester.addObjectCreate("voltdb/localcache/property", PropertyNode.class.getName());
		digester.addSetProperties("voltdb/localcache/property");
		
		digester.addObjectCreate("voltdb/checktask", CheckTaskNode.class.getName());
		digester.addSetProperties("voltdb/checktask");
		digester.addObjectCreate("voltdb/checktask/property", PropertyNode.class.getName());
		digester.addSetProperties("voltdb/checktask/property");
		
		digester.addObjectCreate("voltdb/datasource-lbs", IbsNode.class.getName());
		digester.addSetProperties("voltdb/datasource-lbs");
	   digester.addObjectCreate("voltdb/datasource-lbs/property", PropertyNode.class.getName());
		digester.addSetProperties("voltdb/datasource-lbs/property");

		digester.addObjectCreate("voltdb/datasource", DatasourceNode.class.getName());
		digester.addSetProperties("voltdb/datasource");
		digester.addObjectCreate("voltdb/datasource/pool", PoolNode.class.getName());
		digester.addSetProperties("voltdb/datasource/pool");
		digester.addObjectCreate("voltdb/datasource/pool/property", PropertyNode.class);
		digester.addSetProperties("voltdb/datasource/pool/property");

		digester.addSetNext("voltdb/localcache", "setLocalCache", LocalCacheNode.class.getName());
		digester.addSetNext("voltdb/datasource", "setDataSource", DatasourceNode.class.getName());
		digester.addSetNext("voltdb/checktask", "setCheckTaskNode",CheckTaskNode.class.getName());
		digester.addSetNext("voltdb/datasource-lbs", "setIbsNode", IbsNode.class.getName());
		
		digester.addSetNext("voltdb/datasource-lbs/property","addProperty",PropertyNode.class.getName());
		digester.addSetNext("voltdb/checktask/property", "addProperty",PropertyNode.class.getName());
		digester.addSetNext("voltdb/localcache/property", "addProperty", PropertyNode.class.getName());
		
		digester.addSetNext("voltdb/datasource/pool", "addPool", PoolNode.class.getName());
		digester.addSetNext("voltdb/datasource/pool/property", "addProperty", PropertyNode.class.getName());

		return (VoltdbNode) digester.parse(input);

	}

	public static Configuration getInstance() throws Exception
	{
		if (isInit == Boolean.FALSE)
		{
			synchronized (Configuration.class)
			{
				if (isInit == Boolean.FALSE)
				{
					voltdbConfig = loadConfiguration();
					isInit = Boolean.TRUE;
					instance = new Configuration();
				}
			}
		}
		return instance;
	}
	
	public String getBucketCount()
	{
		String bucketCount = voltdbConfig.getLocalCache().getBucket();
		return bucketCount;
	}
	
	public void reload() throws Exception
	{
		voltdbConfig = loadConfiguration();
		
	}
	private  void startup()
	{
		/*Timer timer = new Timer("ReloadConfig",true);
		timer.schedule(task, firstTime, period);*/
		//5秒内循环检测配置文件是否改动
		ReloadConfig rc = new ReloadConfig(new File(Constants.Config.CONFIG_FILE),this);
		rc.setName("ReloadConfig_Thread");
		rc.start();
		rc.setDaemon(true);
	}
	public static PoolNode[] getPools()
	{
		
		return voltdbConfig.getDataSource().getPoolNodes();
	}
	public String getIbs()
	{
		return voltdbConfig.getIbsNode().getStrategy();
	}
	public CheckTaskNode getCheckTask()
	{
		return voltdbConfig.getCheckTaskNode();
	}
}
