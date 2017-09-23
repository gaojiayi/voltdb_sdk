package com.gaojy.cache.voltdb.core.dbsource.mapping;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Reflector<T>
{
	private Map<String, String> fieldMap;
	private Map<String, Method> methodMap;

	public Reflector(Class<T> clazz) throws InstantiationException, IllegalAccessException
	{
		
			T instance = clazz.newInstance();
			// fieldMap= new HashMap();
			// methodMap = new HashMap();
		fieldMap = ResultSetToAppBean.fieldToMap(instance);
			methodMap = new HashMap<String, Method>();
			for (Map.Entry field_entry : fieldMap.entrySet())
			{
				String methodName = "set" + field_entry.getKey();
				
				methodMap.put(methodName, ResultSetToAppBean.getMethodByName(instance, methodName));
			}
		
		

	}

	public Map<String, String> getFieldMap()
	{
		return fieldMap;
	}

	public void setFieldMap(Map<String, String> fieldMap)
	{
		this.fieldMap = fieldMap;
	}

	public Map<String, Method> getMethodMap()
	{
		return methodMap;
	}

	public void setMethodMap(Map<String, Method> methodMap)
	{
		this.methodMap = methodMap;
	}

	public Method getMethodByName(String methodName)
	{
		return methodMap.get(methodName);
	}
}
