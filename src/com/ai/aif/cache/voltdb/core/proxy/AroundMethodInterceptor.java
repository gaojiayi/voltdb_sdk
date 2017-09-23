package com.ai.aif.cache.voltdb.core.proxy;

public interface AroundMethodInterceptor
{
	/**
	 * 前置拦截方法
	 * @param obj
	 * @param methodName
	 * @param objectArray
	 * @throws Exception
	 */
	public void beforeInterceptor(Object obj ,String methodName,Object[] objectArray)throws Exception;
	/**
	 * 后置拦截方法
	 * @param obj
	 * @param methodName
	 * @param objectArray
	 * @throws Exception
	 */
	public void afterInterceptor(Object obj,String methodName,Object[] objectArray)throws Exception;
	/**
	 * 异常拦截方法
	 * @param obj
	 * @param methodName
	 * @param objectArray
	 * @throws Exception
	 */
	public void exceptionInterceptor(Object obj,String methodName,Object[] objectArray)throws Exception;
}
