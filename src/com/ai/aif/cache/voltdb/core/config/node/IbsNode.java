package com.ai.aif.cache.voltdb.core.config.node;

import java.util.HashMap;
import java.util.Map;

import com.ai.aif.cache.voltdb.core.constant.Constants;

public class IbsNode
{
	private static final String STRATEGY = Constants.Config.STRATEGY;
	private Map<String , PropertyNode> propMap = new HashMap<String , PropertyNode>();
	public void addProperty(PropertyNode node)
	{
		propMap.put(node.getName(),node);
	}
	public  String getStrategy()
	{
		return propMap.get(STRATEGY).getValue();
		
	} 
}
