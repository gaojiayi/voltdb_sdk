package com.gaojy.cache.voltdb.core.loadblr;

import java.util.List;

import com.gaojy.cache.voltdb.core.cfg.pojo.Node;

/**
 * 
 * @Title: 负载接口
 * @Description: 
 * @Author: gaojy
 * @Version: 1.0
 */
public interface IAILoadBalancer<T,E extends Node>
{
	/**
	 * 根据算法，从列表中获取相应的对象
	 * @param lists 对象集合
	 * @return 返回对象
	 */
	public T getDataSource(List<E> lists);

}
