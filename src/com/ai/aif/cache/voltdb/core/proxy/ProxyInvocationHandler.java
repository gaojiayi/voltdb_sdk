package com.ai.aif.cache.voltdb.core.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;



/**
 * 
 * 
 * @Title: 代理拦截实现
 * @Description: 
 * @Copyright: Copyright (c) 2016 CMC CRM TA Dept-NJ All Rights Reserved
 * @Company: Asiainfo
 * @Author: gaojy5
 * @Version: 1.0
 */
public  final class ProxyInvocationHandler implements InvocationHandler
{
	//要拦截的对象
	private Object _obj = null;
	//要使用的拦截器实现类（或者监听器）
	private Class[] _interceptors_class = null;
	

	public ProxyInvocationHandler(Object _obj, Class[] _interceptors_class)
	{
		super();
		this._obj = _obj;
		this._interceptors_class = _interceptors_class;
	}


	@Override
	public Object invoke(Object object, Method method, Object[] objectArray) throws Throwable
	{
		// 调用监听器，对方式执行进行监听
		List invokerListeners = new ArrayList(_interceptors_class.length);
		//拦截器的实例化
		Object[] _interceptors = new Object[_interceptors_class.length];
		//循环实例化
		for(int i =0;i<_interceptors_class.length;i++)
		{
			_interceptors[i] = _interceptors_class[i].newInstance();
		}
		//前置拦截器
		//环绕拦截器
		boolean[] isBeforeSuccess = new boolean[_interceptors.length];
		try
		{
			//log:即将执行前置拦截
			
			for(int i = 0;i < _interceptors.length;i++)
			{
				//log:即将判断的拦截器是_interceptors【i】
				
				if(_interceptors[i] instanceof BeforeMethodInterceptor)
				{
					//log:当前类是前置拦截器，即将执行前置拦截方法
					((BeforeMethodInterceptor)_interceptors[i]).interceptor(this._obj, method.getName(), objectArray);
					isBeforeSuccess[i] = true;
				}else if(_interceptors[i] instanceof AroundMethodInterceptor)
				{
					//log：当前类是环绕拦截器，即将执行前置拦截方法
					((AroundMethodInterceptor)_interceptors[i]).beforeInterceptor(this._obj, method.getName(), objectArray);
					isBeforeSuccess[i] = true;
				}
				
				//判断调用监听器
				if(_interceptors[i] instanceof InvokerListener)
				{
					//log:当前类是监听器，加入到监听器集合中，留待之后执行。
					invokerListeners.add(_interceptors[i]);
				}
			}
			
		}
		catch (Exception e)
		{
			//log.fatal("在方法前调用的拦截器工作失败")，将已经工作过的拦截器进行反向处理
			
			//将已经工作过的拦截器进行反向异常处理
			for(int i = _interceptors.length -1;i > 0; i--)
			{
				if(isBeforeSuccess[i] == true)
				{
					if(_interceptors[i] instanceof AroundMethodInterceptor)
					{
						//当前类是环绕拦截，反向执行异常拦截方法
						((AroundMethodInterceptor)_interceptors[i]).exceptionInterceptor(this._obj, method.getName(), objectArray);
					}
				}
			}
			throw e;
		}
		
		//监听器
		//真实方法
		Object rtn = null;
		try
		{
			//log：即将执行监听器和真实方法
			
			//调用真实方法前先调用监听器执行beforeInvoker方法
			if(!invokerListeners.isEmpty())
			{
				//log:执行前置监听器
				for(Iterator iter = invokerListeners.iterator();iter.hasNext();)
				{
					InvokerListener item  =	(InvokerListener)iter.next();
					item.beforeInvoker(this._obj, method.getName(), objectArray);
				}
				
			}
			
			//调用真实方法
			rtn = method.invoke(this._obj, objectArray);
			
			//调用真实方法后调用监听器执行afterInvoker方法
			if(!invokerListeners.isEmpty())
			{
				//log：调用后置监听器
				for(Iterator iter = invokerListeners.iterator();iter.hasNext();)
				{
					InvokerListener item = (InvokerListener)iter.next();
					item.afterInvoker(this._obj, method.getName(), objectArray);
				}
			}
			
		}
		catch (Throwable ex)
		{
			try
			{
				//将已经工作过的监听器和真实方法进行反向异常处理
				for(int i = _interceptors.length-1;i >= 0;i--)
				{
					if(_interceptors[i] instanceof AroundMethodInterceptor)
					{
						//log:当前类是环绕拦截，反向执行异常拦截方法
						((AroundMethodInterceptor) _interceptors[i]).exceptionInterceptor(this._obj, method.getName(), objectArray);
					}
				}
			}
			catch (Throwable ex2)
			{
				//log  ("拦截器异常拦截方法工作失败异常")
			}
			
			//对异常进行拆除成标准异常
			Throwable root = null;
			
			try
			{
				//log:将异常拆除成标准异常
				
				root  = ExceptionUtils.getRootCause(ex);
			}
			catch (Throwable ex3)
			{
				// log:ExceptionUtils.getRootCause出现异常,继续扔出原始异常
				throw ex;
			}
			
			if(root != null)
			{
				//log.error(new StringBuilder(LOG_PREFIX).append("方法异常"), root);
				throw root;
			}else
			{
				//	log.error(new StringBuilder(LOG_PREFIX).append("方法异常"), ex);
				throw ex;
			}
			//封装原始异常结束
		}
		
		//方法之后的拦截器   after
		//环绕拦截器
		try
		{
			//即将执行后置执行方法
			for(int i = _interceptors.length;i >= 0;i--)
			{
				if(_interceptors[i] instanceof AfterMethodInterceptor)
				{
					//log.debug(new StringBuilder(LOG_PREFIX).append("当前类").append(_interceptors[i]).append("是后置拦截器，即将执行后置拦截方法"));
					((AfterMethodInterceptor)_interceptors[i]).interceptor(this._obj, method.getName(), objectArray);
				}
				else if(_interceptors[i] instanceof AroundMethodInterceptor)
				{
					//log.debug(new StringBuilder(LOG_PREFIX).append("当前类").append(_interceptors[i]).append("是环绕拦截器，即将执行后置拦截方法"));
					((AroundMethodInterceptor)_interceptors[i]).afterInterceptor(this._obj, method.getName(), objectArray);
				}
			}
		}
		catch (Throwable ex)
		{
			// log.fatal(new StringBuilder(LOG_PREFIX).append("在方法调用后面的拦截器工作失败"), ex);
		}
		
		return rtn;
	}

}
