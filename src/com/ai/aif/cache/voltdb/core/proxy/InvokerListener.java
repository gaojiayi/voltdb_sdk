package com.ai.aif.cache.voltdb.core.proxy;

/**
 * @Title:调用的监听器
 * @Description:
 * @Copyright: Copyright (c) 2016 CMC CRM TA Dept-NJ All Rights Reserved
 * @Company: Asiainfo
 * @Author: gaojy5
 * @Version: 1.0
 */
public interface InvokerListener
{
	/**
	 * 调用前监听方法
	 * 
	 * @param obj
	 * @param methodName
	 * @param objectArray
	 */
	public void beforeInvoker(Object obj, String methodName, Object[] objectArray);
	/**
	 * 调用后监听方法
	 * @param obj
	 * @param methodName
	 * @param objectArray
	 */
	public void afterInvoker(Object obj, String methodName, Object[] objectArray);
}
