package com.ai.aif.cache.voltdb.core.config.node;

public class PropertyNode
{
	private String name;
	private String value;
	public PropertyNode(String name, String value)
	{
		super();
		this.name = name;
		this.value = value;
	}
	public PropertyNode()
	{
		super();
		// TODO Auto-generated constructor stub
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getValue()
	{
		return value;
	}
	public void setValue(String value)
	{
		this.value = value;
	}
	
}
