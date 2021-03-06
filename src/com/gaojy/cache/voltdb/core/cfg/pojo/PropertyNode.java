package com.gaojy.cache.voltdb.core.cfg.pojo;

/**
 * 
 * @Title: 
 * @Description: 
 * @Author: gaojy
 * @Version: 1.0
 */
public class PropertyNode
{
	private String name;
	
	private String value;

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * @return the value
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("PropertyNode\n[name=");
		builder.append(name);
		builder.append("\n value=");
		builder.append(value);
		builder.append("]");
		return builder.toString();
	}
}
