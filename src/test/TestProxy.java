package test;



import java.io.Serializable;

import test.common.Dao;
import test.common.DaoImpl;
import test.common.MyBeforeMethodInterceptor;
import test.common.User;

import com.ai.aif.cache.voltdb.core.proxy.ProxyInvocationHandler;
import com.ai.aif.cache.voltdb.core.proxy.ProxyUtil;

public class TestProxy
{
	public static void main(String[] args)
	
	{
		Dao d = new DaoImpl();
		d=(Dao)ProxyUtil.getProxyObject(User.class.getClassLoader(), d.getClass().getInterfaces(), new ProxyInvocationHandler(d, new Class[]{MyBeforeMethodInterceptor.class} ));
		d.update();
		
	}
}
