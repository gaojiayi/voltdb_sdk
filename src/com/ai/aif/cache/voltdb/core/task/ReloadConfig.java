package com.ai.aif.cache.voltdb.core.task;

import java.io.File;
import java.util.TimerTask;

import com.ai.aif.cache.voltdb.core.config.Configuration;
/**
 * 
 * 
 * @Title: 线程：定时重加载配置
 * @Description: 
 * @Copyright: Copyright (c) 2016 CMC CRM TA Dept-NJ All Rights Reserved
 * @Company: Asiainfo
 * @Author: gaojy5
 * @Version: 1.0
 */
public class ReloadConfig extends Thread
{
	private File file;
	private long lastTime;
	private Configuration config;

	public ReloadConfig(File file, Configuration config)
	{
		this.file = file;
		this.config = config;
		this.lastTime = file.lastModified();
	}

	public void run()
	{
		while (true)
		{
			try
			{
				sleep(5000);
				if (file.lastModified() != lastTime)
				{
					// 重新加载配置文件
					Configuration.getInstance().reload();
				}

			}
			catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
