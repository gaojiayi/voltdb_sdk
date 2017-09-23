package test;

import java.util.Map;

import com.ai.aif.cache.voltdb.core.query.IVoltdb;
import com.ai.aif.cache.voltdb.core.query.impl.VoltdbQuery;

public class TestQuery
{
public static void main(String[] args) throws Exception
{
	VoltdbQuery vq = new VoltdbQuery();
	//vq.update("insert into user values(?)", new Object[]{"gaojy"});
	Object[] queryRemote = vq.query("select * from user", null);
	for(Object map :queryRemote){
		System.out.println(((Map)map).get("NAME"));
	}
}
}
