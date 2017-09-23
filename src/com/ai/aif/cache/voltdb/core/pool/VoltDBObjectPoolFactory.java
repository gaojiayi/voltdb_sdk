package com.ai.aif.cache.voltdb.core.pool;

import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.commons.pool.PoolableObjectFactory;



public class VoltDBObjectPoolFactory implements PoolableObjectFactory {
	private String poolName;
	private String dbName;
	private String username;
	private String password;
	private String host;
	private String port;

	
	public VoltDBObjectPoolFactory(String poolName, String dbName, String username, String password, String host,
			String port)
	{
		super();
		this.poolName = poolName;
		this.dbName = dbName;
		this.username = username;
		this.password = password;
		this.host = host;
		this.port = port;
	}

	public VoltDBObjectPoolFactory()
	{
		super();
		// TODO Auto-generated constructor stub
	}


	public String getPoolName()
	{
		return poolName;
	}

	public void setPoolName(String poolName)
	{
		this.poolName = poolName;
	}

	public String getDbName()
	{
		return dbName;
	}

	public void setDbName(String dbName)
	{
		this.dbName = dbName;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public String getHost()
	{
		return host;
	}

	public void setHost(String host)
	{
		this.host = host;
	}

	public String getPort()
	{
		return port;
	}

	public void setPort(String port)
	{
		this.port = port;
	}

	@Override
	public void activateObject(Object obj) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroyObject(Object obj) throws Exception {
		if (!(obj instanceof Connection)) {

			return;
		}

		Connection conn = (Connection) obj;

		// �ر�����
		conn.close();

		conn = null;

	}

	@Override
	public Object makeObject() throws Exception {
		Class.forName("org.voltdb.jdbc.Driver");
		Connection conn = DriverManager.getConnection("jdbc:voltdb://"+host+":"+port,username,password);
		return conn;
	}

	@Override
	public void passivateObject(Object obj) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean validateObject(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}

}
