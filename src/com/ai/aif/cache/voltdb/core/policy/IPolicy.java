package com.ai.aif.cache.voltdb.core.policy;
/**
 * 
 * 
 * @Title: 数据库连接池处理策略接口
 * @Description: 
 * @Copyright: Copyright (c) 2016 CMC CRM TA Dept-NJ All Rights Reserved
 * @Company: Asiainfo
 * @Author: gaojy5
 * @Version: 1.0
 */
public interface IPolicy
{   
     /**
      * 新增
      * @param obj
      * @return
      */
	public abstract boolean add(Object obj);
	/**
	 * 清除
	 */
	public  abstract void clear();
	/**
	 * 判断是否包含
	 * @param obj
	 * @return
	 */
	public abstract boolean contains(Object obj);
	/**
	 * 获取策略对象
	 * @return
	 * @throws Exception
	 */
	public abstract Object getPolicyObject() throws Exception;
	/**
	 * 清除某条数据
	 * @param obj
	 * @return
	 */
	public abstract boolean remove(Object obj);
	/**
	 * 链表长度
	 * @return
	 */
	public abstract int size();
	/**
	 * 将链表转化为数组
	 * @return
	 */
	public abstract Object[] toArray();
	
}
