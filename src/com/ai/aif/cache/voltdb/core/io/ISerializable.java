package com.ai.aif.cache.voltdb.core.io;

import java.io.IOException;

public interface ISerializable
{
	/**
	 * 
	 * @param object
	 * @return
	 * @throws IOException
	 */
    public byte[] object2byte(Object object)throws IOException;
   /**
    * 
    * @param bytes
    * @return
    * @throws IOException
    * @throws ClassNotFoundException
    */
    public Object byte2object(byte[] bytes) throws IOException, ClassNotFoundException;
}
