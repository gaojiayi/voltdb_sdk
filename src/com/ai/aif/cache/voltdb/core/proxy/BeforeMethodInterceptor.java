package com.ai.aif.cache.voltdb.core.proxy;

public interface BeforeMethodInterceptor
{
	/**
	 * 前置拦截器
	 * @param obj
	 * @param methodName
	 * @param objectArray
	 * @throws Exception
	 */
	public void interceptor(Object obj,String methodName,Object[] objectArray)throws Exception;
}
