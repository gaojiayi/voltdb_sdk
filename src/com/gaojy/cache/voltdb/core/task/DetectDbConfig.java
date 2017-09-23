package com.gaojy.cache.voltdb.core.task;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gaojy.cache.voltdb.core.cfg.DBConfigSource;

/**
 * 
 * @Title: 
 * @Description: 
 * @Author: gaojy
 * @Version: 1.0
 */
public class DetectDbConfig extends Thread
{
	private static final Log LOGGER = LogFactory.getLog(DetectDbConfig.class);
	
	private File file;
	
	private long lastTime;
	
	private DBConfigSource cfg;
	
	public DetectDbConfig(File file,DBConfigSource cfg)
	{
		this.file = file;
		this.lastTime = file.lastModified();
		this.cfg = cfg;
	}
	/* (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run()
	{
		while(true)
		{
			try
			{
				sleep(5000);
				
				if (file.lastModified() != lastTime)
				{
					cfg.reload();
					
					LOGGER.info("Reload configuration!");
				}
			}
			catch (InterruptedException e)
			{
				LOGGER.error(e);
			}
		}

	}

}
