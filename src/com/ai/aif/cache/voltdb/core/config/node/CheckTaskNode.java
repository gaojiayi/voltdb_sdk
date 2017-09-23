package com.ai.aif.cache.voltdb.core.config.node;

import java.util.HashMap;
import java.util.Map;

import com.ai.aif.cache.voltdb.core.constant.Constants;

public class CheckTaskNode
{
	private static final String KEY_DELAY = Constants.Config.KEY_DELAY;
	private static final String KEY_PERIOD = Constants.Config.KEY_PERIOD;
	private static final String KEY_SQL = Constants.Config.KEY_SQL;
	private Map<String, PropertyNode> propMap = new HashMap<String, PropertyNode>();

	public void addProperty(PropertyNode node)
	{
		propMap.put(node.getName(), node);
	}

	public Long getDelay()
	{
		return Long.parseLong(propMap.get(KEY_DELAY).getValue());
	}

	public Long getPeriod()
	{
		return Long.parseLong(propMap.get(KEY_PERIOD).getValue());
	}

	public String getSql()
	{
		return propMap.get(KEY_SQL).getValue();
	}
}
