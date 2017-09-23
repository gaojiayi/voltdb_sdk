package test.gaojy.cache.cfg;

import java.io.File;
import java.io.IOException;

/**
 * 
 * @Title: 
 * @Description: 
 * @Author: gaojy
 * @Version: 1.0
 */
public class Test
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		
		File file = new File("config/dbconfig.xml");
		
		System.out.println(file.getAbsolutePath());
		try
		{
			System.out.println(file.getCanonicalPath());
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
