package com.gaojy.cache.voltdb.core.utils;


/**
 * @Title:
 * @Description:
 * @Author: gaojy
 * @Version: 1.0
 */
public class RowWithMetaData
{
	final public Object rawLine;

	final public long lineNumber;


	public RowWithMetaData(Object rawLine, long ln)
	{
		this.rawLine = rawLine;
		lineNumber = ln;
	}
}
