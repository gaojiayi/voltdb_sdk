package com.ai.aif.cache.voltdb.core.proxy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.HashMap;

/**
 * 
 * 
 * @Title: 代理工具，将所有拦截器和监听器加入到代理中
 * @Description: 
 * @Copyright: Copyright (c) 2016 CMC CRM TA Dept-NJ All Rights Reserved
 * @Company: Asiainfo
 * @Author: gaojy5
 * @Version: 1.0
 */
public class ProxyUtil
{
	private static final Class[] CONSTRUCT_PARAM = { InvocationHandler.class };
	private static final HashMap CLAZZ_CACHE = new HashMap(); //存放所有代理对象的class
	private ProxyUtil(){}
	/**
	 * 获得代理对象类 getProxyObject()  实际上是Proxy.newProxyInstance()的源码。其中把代理类型对象放入缓存。
	 * @param loader
	 * @param interfaces
	 * @param h
	 * @return
	 */
	public static final Object getProxyObject(ClassLoader loader,Class[] interfaces,InvocationHandler h)
	{
		String key = interfaces[0].getName();
		// log:log.debug(new StringBuilder(LOG_PREFIX).append("即将代理的类名：").append(key));
		Class clazz = (Class) CLAZZ_CACHE.get(key);
		if(clazz == null)
		{
			synchronized (CLAZZ_CACHE)
			{
				if(!CLAZZ_CACHE.containsKey(key))
				{
					/**
					 * 获得 与指定类加载器和一组接口 相关的代理类类型对象 
					 */
					Class c = Proxy.getProxyClass(loader, interfaces);
					CLAZZ_CACHE.put(key, c);
					
				}
				clazz = (Class) CLAZZ_CACHE.get(key);
			}
		}
		
		try
		{
			//log：将拦截器和监听器加入到代理类中，并返回代理对象
			Constructor cons = clazz.getConstructor(CONSTRUCT_PARAM);
			return (Object)cons.newInstance(new Object[]{h});
		}
		catch (NoSuchMethodException e)
		{
			throw new InternalError(e.toString());
		}
		catch (IllegalAccessException e)
		{
			throw new InternalError(e.toString());
		}
		catch (InstantiationException e)
		{
			throw new InternalError(e.toString());
		}
		catch (InvocationTargetException e)
		{
			throw new InternalError(e.toString());
		}
		
		
	}
}
