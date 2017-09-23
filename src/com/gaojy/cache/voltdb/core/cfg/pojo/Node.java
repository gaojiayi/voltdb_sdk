package com.gaojy.cache.voltdb.core.cfg.pojo;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;

/**
 * @Title:
 * @Description:
 * @Author: gaojy
 * @Version: 1.0
 */
public abstract class Node
{
	/**
	 * 获取需要填充的对象(在DBNode中给)
	 * @return
	 */
	protected abstract Object getCopyObject();
	
	public void setNodeValue(PropertyNode node)
	{
		try
		{
			if (null == getCopyObject())
			{
				BeanUtils.copyProperty(this, node.getName(), node.getValue());
			}
			else
			{
				BeanUtils.copyProperty(getCopyObject(), node.getName(), node.getValue());
			}
		}
		catch (IllegalAccessException e1)
		{
			e1.printStackTrace();
		}
		catch (InvocationTargetException e1)
		{
			e1.printStackTrace();
		}
	}

}
