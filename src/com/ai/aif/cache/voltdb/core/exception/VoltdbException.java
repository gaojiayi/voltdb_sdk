package com.ai.aif.cache.voltdb.core.exception;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
/**
 * 
 * 
 * @Title: 
 * @Description: 
 * @Copyright: Copyright (c) 2016 CMC CRM TA Dept-NJ All Rights Reserved
 * @Company: Asiainfo
 * @Author: gaojy5
 * @Version: 1.0
 */
public class VoltdbException extends Exception {
	
	private static final long serialVersionUID = -1491785349569612670L;
	private String exceCode;
	private Map<String, Object> exceParams;

	public VoltdbException(String exceCode) {

		this.exceCode = exceCode;
	}

	public VoltdbException(String execCode, String message) {
		super(message);
		this.exceCode = execCode;
	}
	VoltdbException(String exceCode, Throwable cause)
	{
		super(cause);
		this.exceCode = exceCode;
	}

	VoltdbException(String exceCode, String message, Throwable cause)
	{
		super(message, cause);
		this.exceCode = exceCode;
	}

	public String getExceCode()
	{
		return exceCode;
	}

	public void setExceCode(String exceCode)
	{
		this.exceCode = exceCode;
	}

	public VoltdbException put(String key, boolean value)
	{
		return checkAndPut(key, value);
	}

	public VoltdbException put(String key, short value)
	{
		return checkAndPut(key, value);
	}

	public VoltdbException put(String key, int value)
	{
		return checkAndPut(key, value);
	}

	public VoltdbException put(String key, long value)
	{
		return checkAndPut(key, value);
	}

	public VoltdbException put(String key, float value)
	{
		return checkAndPut(key, value);
	}

	public VoltdbException put(String key, double value)
	{
		return checkAndPut(key, value);
	}

	public VoltdbException put(String key, String value)
	{
		return checkAndPut(key, value);
	}

	private VoltdbException checkAndPut(String key, Object value)
	{
		if (StringUtils.isEmpty(key))
		{
			return this;
		}
		if (exceParams == null)
		{
			exceParams = new HashMap<String, Object>();
		}
		exceParams.put(key, value);
		return this;
	}

	public VoltdbException putAll(Map<String, String> params)
	{
		if (params == null || params.isEmpty())
		{
			return this;
		}
		if (exceParams == null)
		{
			exceParams = new HashMap<String, Object>();
		}
		exceParams.putAll(params);
		return this;
	}

	public Map<String, Object> getAll()
	{
		return exceParams;
	}

}
