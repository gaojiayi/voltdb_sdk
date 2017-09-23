package test.gaojy.cache.lru;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import com.gaojy.cache.voltdb.core.lru.AiLRUMap;
import com.gaojy.cache.voltdb.core.lru.LRUCache;

/**
 * @Title:
 * @Description:
 * @Author: gaojy
 * @Version: 1.0
 */
public class LRUTest
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		
		File file = new File("E:/简历.pdf");
		
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		FileInputStream fis = null;
		byte[] by = new byte[1024];
	    int i = 0;
	    try
		{
	    	fis = new FileInputStream(file);
			while( (i = fis.read(by)) != -1)
			{
				bao.write(by, 0, i);
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    LRUCache.add(1, 1); 
	    LRUCache.add(2, 1); 
	    LRUCache.add(2, bao.toByteArray()); 
		
		System.out.println(LRUCache.getSize());
		System.out.println(LRUCache.get(2));

	}
}
