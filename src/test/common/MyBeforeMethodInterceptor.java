package test.common;

import com.ai.aif.cache.voltdb.core.proxy.BeforeMethodInterceptor;

public class MyBeforeMethodInterceptor implements BeforeMethodInterceptor
{

	@Override
	public void interceptor(Object obj, String methodName, Object[] objectArray) throws Exception
	{
		System.out.println("开始执行"+obj.getClass().getName()+"的"+methodName+"方法！");

	}

}
