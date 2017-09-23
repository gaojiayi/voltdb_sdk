package com.ai.aif.cache.voltdb.core.query.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Title:
 * @Description:
 * @Copyright: Copyright (c) 2016 CMC CRM TA Dept-NJ All Rights Reserved
 * @Company: Asiainfo
 * @Author: gaojy5
 * @Version: 1.0
 */
public class ResultSetToAppBean
{
	private static ConcurrentHashMap<Class<?>, Reflector> REF_MAP = new ConcurrentHashMap();

	/**
	 * 将结果集封装至list
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public static List RsToList(ResultSet rs) throws SQLException
	{
		// Map FieldMap = new HashMap();
		List rsList = new ArrayList();
		ResultSetMetaData metaData = rs.getMetaData();
		if (rs == null)
		{
			return new ArrayList();
		}

		while (rs.next())
		{
			Map OneOfRsMap = new HashMap();
			for (int i = 1; i <= metaData.getColumnCount(); i++)
			{
				Object object = rs.getObject(i);
				/*if(object instanceof BigDecimal)
				{
					object = ((BigDecimal) object).longValue();
				}*/
				if(object instanceof java.sql.Date)
				{
					java.util.Date utilDate = new java.util.Date(((java.sql.Date) object).getTime());
					object = new Timestamp(utilDate.getTime());
				}
				OneOfRsMap.put(metaData.getColumnName(i), object);
			}
			rsList.add(OneOfRsMap);
		}
		return rsList;

	}

	/**
	 * 将AppFrame中变量域及值封装至Map
	 * 
	 * @param obj
	 * @return
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static Map fieldToMap(Object obj) throws IllegalArgumentException, IllegalAccessException
	{
		Map fieldMap = new HashMap();
		Field[] fields = getAllDataField(obj);
		for (Field field : fields)
		{

			fieldMap.put(field.getName().replace("S_", ""), field.get(obj));
			// Class<?> type = field.getType();
		}
		return fieldMap;
	}

	/**
	 * 该方法对方法的重载失效 根据方法名 获得对应的Method
	 * 
	 * @param obj
	 * @param methodName
	 * @return
	 */
	public static Method getMethodByName(Object obj, String methodName)
	{
		Method method = null;
		for (Method m : obj.getClass().getMethods())
		{
			if (methodName.equalsIgnoreCase(m.getName()))
			{
				method = m;
				break;

			}
		}
		return method;
	}

	/**
	 * 获得AppFrameBean中所有的public static final 修饰的变量
	 * 
	 * @param object
	 * @return
	 */
	private static Field[] getAllDataField(Object object)
	{

		Field[] fields = object.getClass().getDeclaredFields();

		List<Field> list = new ArrayList();

		for (Field f : fields)
		{
			f.setAccessible(true);
			if (Modifier.isPublic(f.getModifiers()) && Modifier.isFinal(f.getModifiers())
					&& Modifier.isStatic(f.getModifiers()))
			{
				list.add(f);
			}

		}
		Field[] ret = new Field[list.size()];
		for (int i = 0; i < list.size(); i++)
		{
			ret[i] = (Field) (list.get(i));
		}
		return ret;
	}

	/**
	 * 将结果集封装成List对象，元素即为APPFrame中的Bean
	 * 
	 * @param clazz
	 * @param rs
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws SQLException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public static <T> List<T> getBeans(Class<T> clazz, ResultSet rs)
	{
		List<T> list = new ArrayList<T>();

		try
		{
			List<Map> rsToList = RsToList(rs);
			if (rsToList == null && rsToList.size() <= 0)
			{
				return list;
			}
			for (Map<String, Object> rsMap : rsToList)
			{
				Reflector ref = REF_MAP.get(clazz);
				T entity = clazz.newInstance();
				Map<String, String> fieldToMap = null;
				Map<String, Method> setMethodMap = null;
				if (ref == null)
				{
					Reflector reflector = new Reflector(clazz);
					fieldToMap = reflector.getFieldMap();
					setMethodMap = reflector.getMethodMap();
					REF_MAP.put(clazz, reflector);
				}
				else
				{
					fieldToMap = ref.getFieldMap();
					setMethodMap = ref.getMethodMap();
				}

				/*
				 * T entity =clazz.newInstance(); Map<String, String> fieldToMap = fieldToMap(entity);
				 */
				for (Map.Entry rs_entry : rsMap.entrySet())
				{
					for (Map.Entry field_entry : fieldToMap.entrySet())
					{
						if (((String) field_entry.getValue()).equalsIgnoreCase((String) rs_entry.getKey()))
						{
							// 调用set+field_entry。getKey 方法 传入 new Object[]{rs_entry.getvalue}
							setMethodMap.get("set" + field_entry.getKey()).invoke(entity,
									new Object[] { rs_entry.getValue() });

						}
					}
				}
				list.add(entity);
			}
		}
		catch (Exception e)
		{
			// 反射失败
		}
		finally
		{
			if (null != rs)
			{
				try
				{
					rs.close();
				}
				catch (SQLException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return list;
	}

	/**
	 * 获得某一个bean，该方法确定查询结果只有一条数据
	 * 
	 * @param clazz
	 * @param rs
	 * @return
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws SQLException
	 * @throws InvocationTargetException
	 */
	public static <T> T getBean(Class<T> clazz, ResultSet rs) throws IllegalArgumentException, InstantiationException,
			IllegalAccessException, SQLException, InvocationTargetException
	{
		List<T> beans = getBeans(clazz, rs);
		if (null == beans || beans.size() <= 0)
		{
			return null;
		}

		return getBeans(clazz, rs).get(0);
	}

	/**
	 * 测试方法
	 * 
	 * @param args
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	/*
	 * public static void main(String[] args) throws SQLException, ClassNotFoundException, IllegalArgumentException,
	 * InstantiationException, IllegalAccessException, InvocationTargetException {
	 * Class.forName("org.voltdb.jdbc.Driver"); Connection conn =
	 * DriverManager.getConnection("jdbc:voltdb://192.168.81.127:21212", "gaojy", ""); ResultSet rs =
	 * conn.createStatement().executeQuery("select * from AOP_MYCOLLECTION"); ResultSet rs1 =
	 * conn.createStatement().executeQuery("select * from AOP_MYCOLLECTION"); List<QAopMyCollectonBean> beans1 =
	 * getBeans(QAopMyCollectonBean.class, rs); QAopMyCollectonBean beans2 = getBean(QAopMyCollectonBean.class, rs1); //
	 * QAopMyCollectonBean bean = getBean(QAopMyCollectonBean.class , rs); // System.out.println(bean.getAbilityid());
	 * System.out.println(beans1.get(1).getAbilityid()); System.out.println(beans2.getPersontype()); }
	 */
}
