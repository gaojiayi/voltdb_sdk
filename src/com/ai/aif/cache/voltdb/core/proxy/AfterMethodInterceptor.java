package com.ai.aif.cache.voltdb.core.proxy;

public interface AfterMethodInterceptor
{	
	/**
	 * 后置拦截方法
	 * @param obj
	 * @param methodName
	 * @param objectArray
	 * @throws Exception
	 */
	public  void interceptor(Object obj,String methodName,Object[] objectArray) throws Exception;
}
