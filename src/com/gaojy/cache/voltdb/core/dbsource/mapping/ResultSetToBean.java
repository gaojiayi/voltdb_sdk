package com.gaojy.cache.voltdb.core.dbsource.mapping;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import com.gaojy.cache.voltdb.core.commons.AIColumn;
import com.gaojy.cache.voltdb.core.commons.AIEntity;

/**
 * @Title:
 * @Description:
 * @Author: gaojy
 * @Version: 1.0
 */
public class ResultSetToBean<T>
{

	/**
	 * 获取数据对象
	 * 
	 * @param rs
	 *            ResultSet
	 * @param clazz
	 *            需要填充的对象
	 * @return 集合
	 * @throws RuntimeException
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public List<T> mapRersultSetToObject(ResultSet rs, Class<?> clazz) throws RuntimeException, SQLException
	{
		// 返回空列表
		if (null == rs)
		{
			return new ArrayList<T>();
		}

		if (!clazz.isAnnotationPresent(AIEntity.class))
		{
			throw new RuntimeException(clazz.getSimpleName() + " does not have a AIEntity annotation.");
		}

		ResultSetMetaData metaData = rs.getMetaData();

		List<T> results = new ArrayList<T>();

		try
		{
			Field[] fields = clazz.getDeclaredFields();
			
			int ccount = metaData.getColumnCount();
			// 遍历结果
			while (rs.next())
			{
				T obj = (T) clazz.newInstance();
				for (int i = 1; i <= ccount; i++)
				{
					String cName = metaData.getColumnName(i);

					Object value = rs.getObject(cName);
					// 给对象赋值
					setBean(obj, fields, cName, value);
				}
				results.add(obj);
			}
		}
		catch (Exception e)
		{

		}
		return results;
	}

	private void setBean(T t, Field[] fields, String cName, Object value)
	{
		for (Field field : fields)
		{
			AIColumn aicolu = field.getAnnotation(AIColumn.class);

			if (aicolu.name().equalsIgnoreCase(cName))
			{
				try
				{
					BeanUtils.copyProperty(t, field.getName(), value);
				}
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
				catch (InvocationTargetException e)
				{
					e.printStackTrace();
				}
			}

		}
	}
}
