package com.gaojy.cache.voltdb.core.task;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gaojy.cache.voltdb.core.cfg.pojo.BaseCfg;
import com.gaojy.cache.voltdb.core.cfg.pojo.DBNode;

/**
 * @Title: 探测数据库是正常
 * @Description:
 * @Author: gaojy
 * @Version: 1.0
 */
public class DetectDbStatus extends TimerTask
{
	private static final Log LOGGER = LogFactory.getLog(DetectDbStatus.class);

	private BaseCfg cfg;

	public DetectDbStatus(BaseCfg cfg)
	{
		this.cfg = cfg;
	}

	/*
	 * (non-Javadoc)
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run()
	{
		Map<String, List<DBNode>> map = cfg.getDbMaps();

		for (Map.Entry<String, List<DBNode>> en : map.entrySet())
		{
			for (DBNode node : en.getValue())
			{
				isNormal(node);

			}
		}

	}

	private void isNormal(DBNode node)
	{
		Connection con = null;
		try
		{
			con = node.getBasicDataSource().getConnection();

			if (!node.isNormal())
			{
				LOGGER.info(node.getBasicDataSource().getUrl()+" recover.");
				node.setNormal(true);
			}
		}
		catch (SQLException e)
		{
			if (node.isNormal())
			{
				LOGGER.error(node.getBasicDataSource().getUrl()+" abnormal.");
				node.setNormal(false);
			}
		}
		finally
		{
			try
			{
				if (null != con)
				{
					con.close();
				}
			}
			catch (SQLException e)
			{
				LOGGER.error(e);
			}
		}
	}

}
