package com.ai.aif.cache.voltdb.core.config.node;

import java.util.HashMap;
import java.util.Map;

public class PoolNode
{
	private String name;
	private Map<String, PropertyNode> propMap = new HashMap<String, PropertyNode>();

	public void addProperty(PropertyNode propertyNode)
	{
		propMap.put(propertyNode.getName(), propertyNode);
		
	}
	
	public PropertyNode[]  getAllProperties()
	{
		Object[] array = this.propMap.values().toArray();
		int length = array.length;
		PropertyNode[] properties = new PropertyNode[length];
		for(int i = 0;i<length;i++){
			properties[i] =	(PropertyNode)array[i];
		}
		return properties;
	}

	public String getName()
	{
		return name;
	}

	public Map<String, PropertyNode> getPropMap()
	{
		return propMap;
	}

	@Override
	public String toString()
	{
		return "PoolNode [name=" + name + ", propMap=" + propMap + "]";
	}
	
	
}
