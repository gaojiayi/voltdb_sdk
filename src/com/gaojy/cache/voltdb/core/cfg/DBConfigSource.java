/**
 * 
 */
package com.gaojy.cache.voltdb.core.cfg;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.gaojy.cache.voltdb.core.cfg.pojo.BaseCfg;
import com.gaojy.cache.voltdb.core.task.DetectDbConfig;
import com.gaojy.cache.voltdb.core.task.DetectDbStatus;

/**
 * @Title:
 * @Description:
 * @Author: gaojy
 * @Version: 1.0
 */
public class DBConfigSource
{
	private static final Log LOGGER = LogFactory.getLog(DBConfigSource.class);

	private static final DBConfigSource DBCONFIG = new DBConfigSource();

	private static final String CFG_PATH = "config/dbconfig.xml";

	private static final String CFG_RULES = "dbconfig-Rules.xml";

	//环境变量
	private static final String VOLTDB_CFG = "VOLTDB_CFG";

	private static final Object lock = new Object();

	private BaseCfg basecfg;

	private DBConfigSource()
	{
		initialize();

		startup();
	}

	/**
	 * 获取实例
	 * 
	 * @return
	 */
	public synchronized static DBConfigSource getInstance()
	{
		return DBCONFIG;
	}

	/**
	 * 获取数据配置
	 * 
	 * @return BaseCfg
	 */
	public BaseCfg getDBConfig()
	{
		if (null == basecfg)
		{
			synchronized (lock)
			{
				LOGGER.warn("Reload the configuration. ");
				initialize();
			}
		}
		return basecfg;
	}

	/**
	 * reload
	 */
	public void reload()
	{
		initialize();
	}

	/**
	 * 加载配置
	 */
	private void initialize()
	{
		File file = getCfgFile();
		
		InputStream inputs = this.getClass().getResourceAsStream(CFG_RULES);
		
		Digester digs;

		try
		{
			digs = DigesterLoader.createDigester(new InputSource(inputs));

			basecfg = (BaseCfg) digs.parse(file);
		}
		catch (IOException e)
		{
			LOGGER.error("Failed to load " + CFG_PATH + " Or " + CFG_RULES, e);
		}
		catch (SAXException e)
		{
			LOGGER.error("Analytical file failed ", e);
		}
	}

	private File getCfgFile()
	{
		File file = new File(CFG_PATH);

		if (!file.exists())
		{
			String path = System.getenv(VOLTDB_CFG);
			
			file = new File(path);
		}
		
		return file;
	}

	/**
	 * 启动探测TASK
	 */
	private void startup()
	{
		Timer timer = new Timer("DetectDbTimer", true);

		long period = getDBConfig().getInterval();

		timer.schedule(new DetectDbStatus(getDBConfig()), period, period);

		// 探测配置文件是否被修改，修改后及时刷新
		DetectDbConfig ddcfg = new DetectDbConfig(new File(CFG_PATH), this);
		ddcfg.setName("DetectDbCfg_Thread");
		ddcfg.setDaemon(true);
		ddcfg.start();
	}

}
