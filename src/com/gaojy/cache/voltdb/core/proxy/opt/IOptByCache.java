package com.gaojy.cache.voltdb.core.proxy.opt;

import java.util.List;

/**
 * @Title:
 * @Description:
 * @Author: gaojy
 * @Version: 1.0
 */
public interface IOptByCache
{
	/**
	 * 过滤条件；只有满足这个才会触发，下面的动着。
	 * 
	 * @return String[]
	 */
	List<String> rule();

	/**
	 * 在代理方法前执行的
	 */
	Object before(String sql);

	/**
	 * 在代理方法之后执行
	 */
	Object after(String sql, Object objs);
}
