package test;

import com.ai.aif.cache.voltdb.core.cache.LocalCache;
import com.ai.aif.cache.voltdb.core.cache.map.SizeObject;

public class TestCache
{
	public static void main(String[] args)
	{
		String str = "{RULE_ID=100370000001, EFF_MONTH=10, PERIOD=0, RULE_DESC=0月/次,每次有效期10个月,共0次, EFF_DURATION=0, MODIFY_DATE=2008-11-02, CREATER=999, RULE_NAME=0月/次,每次有效期10个月,共0次, EFF_MODE=2, TIMES=0, DEL_FLAG=1, RULE_CODE=0|0|10|0|1, CREATE_DATE=2008-11-02, MODIFIER=999}";
		SizeObject so = new SizeObject(str, 1);
		int i = 0;
		while (Boolean.TRUE)
		{
			System.out.println("当前缓存容器大小为：" + LocalCache.getInstance().getCache().getCurrentByteSize() + ";准备存入的数据大小为："
					+ so.getSize());

			LocalCache.getInstance().addLocal(String.valueOf(i), so);
			System.out.println("缓存加载完毕之后，当前缓存容器大小为：" + LocalCache.getInstance().getCache().getCurrentByteSize());

			i++;
		}
	}
}
