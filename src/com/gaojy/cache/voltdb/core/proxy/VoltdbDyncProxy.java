package com.gaojy.cache.voltdb.core.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Locale;

/**
 * @Title:
 * @Description:
 * @Author: gaojy
 * @Version: 1.0
 */
public class VoltdbDyncProxy implements InvocationHandler
{
	private static String PROXY_RULE = "query";

	private Class<?> delegate;

	private Class<?> rule;

	public VoltdbDyncProxy(Object delegate, Class<?> rule)
	{
		this.delegate = delegate.getClass();
		this.rule = rule;
	}

	/**
	 * 获取被代理的对象
	 * 
	 * @param delegate
	 *            需代理对象
	 * @param rule
	 *            规则
	 * @return Object
	 */
	public Object bind()
	{
		return Proxy.newProxyInstance(delegate.getClassLoader(), delegate.getInterfaces(), this);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		List<String> list = (List<String>) invoke("rule", null);

		String name = method.getName();

		boolean flag = false;
		
		// 过滤条件
		for (String str : list)
		{
			if (name.startsWith(str) || name.endsWith(str))
			{
				flag = true;
				break;
			}
		}

		if (flag)
		{
			name = name.toLowerCase(Locale.getDefault());
			//查询方法需执行的
			if (name.startsWith(PROXY_RULE) || name.endsWith(PROXY_RULE))
			{
				Object obj = invoke("before", new Class<?>[] { String.class }, args);

				if (null != obj && !"".equals(obj.toString()))
				{
					return obj;
				}
			}
			
		}

		Object result = null;
		try
		{
			result = method.invoke(this.delegate.newInstance(), args);
		}
		finally
		{
			if (flag)
			{
				invoke("after", new Class<?>[] { String.class, Object.class },new Object[]{args[0],result});
			}

		}
		return result;
	}

	private Object invoke(String methodName, Class<?>[] clazzs, Object... args) throws Throwable
	{
		Class<?> clazz = this.rule;
		// 拿规则
		Method ruleM;

		Object newObj = this.rule.newInstance();

		if (null == args || args.length == 0)
		{
			ruleM = clazz.getDeclaredMethod(methodName);

			return ruleM.invoke(newObj);
		}
		ruleM = clazz.getDeclaredMethod(methodName, clazzs);
		// 获取结果
		return ruleM.invoke(newObj, args);
	}
}
