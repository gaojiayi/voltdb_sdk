package com.ai.aif.cache.voltdb.core.util;

public class VoltdbSQLUtils {

	public static String parseSQL(String sql, Object[] params) {
		StringBuffer sqlBuffer = new StringBuffer();
		if (null != params && params.length > 0) {
			char[] chars = sql.toCharArray();
			int j = 0;
			try {
				for (int i = 0; i < chars.length; i++) {
					if (chars[i] == '?') {
						// 如果是String类型   参数加上''
						if (params[j] instanceof java.lang.String) {
							params[j] = "'" + params[j] + "'";
						}
						sqlBuffer.append(params[j]);
						j++;
						continue; // 不把？写入sql
					}
					sqlBuffer.append(chars[i]);
				}
			} catch (java.lang.ArrayIndexOutOfBoundsException e) {
				// 参数不足
				return null;
			}
			if (j < params.length) {
				// 参数过多
				return null;
			}
		} else {
			if (sql.lastIndexOf("?") > 0) {
				// 还有占位符的sql  却没有传入参数
				return null;
			}
			sqlBuffer.append(sql);
		}
		return sqlBuffer.toString();
	}
}
