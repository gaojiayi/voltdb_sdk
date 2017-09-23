package com.ai.aif.cache.voltdb.core.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @Title:序列化
 * @Description:
 * @Copyright: Copyright (c) 2016 CMC CRM TA Dept-NJ All Rights Reserved
 * @Company: Asiainfo
 * @Author: gaojy5
 * @Version: 1.0
 */
public class JavaSerializable implements ISerializable
{

	public byte[] object2byte(Object object) throws IOException
	{
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		new ObjectOutputStream(b).writeObject(object);
		return b.toByteArray();
	}

	public Object byte2object(byte[] bytes) throws IOException, ClassNotFoundException
	{

		return new ObjectInputStream(new ByteArrayInputStream(bytes)).readObject();
	}

}
