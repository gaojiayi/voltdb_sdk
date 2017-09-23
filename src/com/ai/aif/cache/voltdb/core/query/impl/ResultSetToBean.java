package com.ai.aif.cache.voltdb.core.query.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import com.ai.aif.cache.voltdb.core.annotation.AIColumn;
import com.ai.aif.cache.voltdb.core.annotation.AIEntity;

/**
 * @Title: 通过注解方式实现ORM映射
 * @Description:
 * @Copyright: Copyright (c) 2016 CMC CRM TA Dept-NJ All Rights Reserved
 * @Company: Asiainfo
 * @Author: gaojy5
 * @Version: 1.0
 */
public class ResultSetToBean<T>
{
	public List<T> mapResultSetToObject(ResultSet rs ,Class<T> clazz) throws SQLException
	{
		
		if(null == rs)
		{
			return new ArrayList<T>();
		}
		if(!clazz.isAnnotationPresent(AIEntity.class))
		{
			throw new RuntimeException(clazz.getSimpleName()+"找不到AIEntity注解");
		}
		
		ResultSetMetaData metaData = rs.getMetaData();
		List<T> result = new ArrayList<T>();
		
		try
		{
			Field[] fields = clazz.getDeclaredFields();
			while(rs.next())
			{
				T entity = clazz.newInstance();
				for(int i = 1 ;i <= metaData.getColumnCount();i++)
				{
					setBean(entity,fields,metaData.getColumnName(i),rs.getObject(i));
				}
				result.add(entity);
			}
		}
		catch (Exception e)
		{
			// TODO: handle exception
		}
		
		return result;
	}
	
	private  void  setBean(T t,Field[] fields,String cName,Object value)
	{
		for(Field field : fields)
		{
			AIColumn aicolu = field.getAnnotation(AIColumn.class);
			if(aicolu.name().equals(cName))
			{
				try
				{
					BeanUtils.copyProperty(t, field.getName(), value);
					
				}
				catch (IllegalAccessException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (InvocationTargetException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
