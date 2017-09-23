package test.gaojy.cache.dbsource;

import java.io.IOException;
import java.net.UnknownHostException;

import org.voltdb.VoltTable;
import org.voltdb.VoltTable.ColumnInfo;
import org.voltdb.VoltType;
import org.voltdb.client.Client;
import org.voltdb.client.ClientFactory;
import org.voltdb.client.ClientResponse;
import org.voltdb.client.ProcCallException;
import org.voltdb.client.ProcedureCallback;

/**
 * @Title:
 * @Description:
 * @Author: gaojy
 * @Version: 1.0
 */
public class VoltdbClient
{
	public static void main(String[] args)
	{
		// @LoadMultipartitionTable

		Client client = ClientFactory.createClient();
		try
		{
			client.createConnection("10.1.243.18");
			
			VoltTable table = new VoltTable(new ColumnInfo("name",
					VoltType.STRING), new ColumnInfo("address", VoltType.STRING));
			table.addRow("java","xxx");
			// m_clientImpl.callProcedure(callback, m_procName, m_tableName, m_upsert, toSend);
			boolean flag = client.callProcedure(new ProcedureCallback()
			{
				
				@Override
				public void clientCallback(ClientResponse arg0) throws Exception
				{
					System.out.println(arg0.getStatus());
					
				}
			},"@LoadMultipartitionTable", "test", 1, table);
			
			System.out.println(flag);
		}
		catch (UnknownHostException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
