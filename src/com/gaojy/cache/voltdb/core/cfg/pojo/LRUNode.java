package com.gaojy.cache.voltdb.core.cfg.pojo;

/**
 * @Title:
 * @Description:
 * @Author: gaojy
 * @Version: 1.0
 */
public class LRUNode extends Node
{
	// 默认50M
	private int maxSize = 50;

	// 是否开启
	private boolean isOpen = false;

	/**
	 * @return the maxSize
	 */
	public int getMaxSize()
	{
		return maxSize;
	}

	/**
	 * @param maxSize
	 *            the maxSize to set
	 */
	public void setMaxSize(int maxSize)
	{
		this.maxSize = maxSize;
	}

	/**
	 * @return the isOpen
	 */
	public boolean isOpen()
	{
		return isOpen;
	}

	/**
	 * @param isOpen the isOpen to set
	 */
	public void setIsOpen(boolean isOpen)
	{
		
		this.isOpen = isOpen;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("LRUNode\n{maxSize=");
		builder.append(maxSize);
		builder.append(",isOpen=");
		builder.append(isOpen);
		builder.append("}");
		return builder.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see com.gaojy.cache.voltdb.core.cfg.pojo.Node#getCopyObject()
	 */
	@Override
	protected Object getCopyObject()
	{
		return null;
	}
}
