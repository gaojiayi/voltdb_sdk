package com.gaojy.cache.voltdb.core.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import au.com.bytecode.opencsv_voltpatches.CSVWriter;

import com.gaojy.cache.voltdb.core.commons.ImporterType;
import com.gaojy.cache.voltdb.core.dbsource.AIBasicDataSource;

/**
 * This is a single thread reader which feeds the lines after validating syntax to CSVDataLoader.
 */
class JDBCStatementReader implements Runnable
{
	private static final Log LOGGER = LogFactory.getLog(AIBasicDataSource.class);

	static AtomicLong m_totalRowCount = new AtomicLong(0);

	static JDBCLoader.JDBCLoaderConfig m_config = null;
	long m_parsingTime = 0;
	private final BulkLoaderErrorHandler m_errHandler;

	private VoltBulkLoader bloader;

	public static void initializeReader(JDBCLoader.JDBCLoaderConfig config)
	{
		m_config = config;
	}

	public JDBCStatementReader(VoltBulkLoader bloader, BulkLoaderErrorHandler errorHandler)
	{
		m_errHandler = errorHandler;
		this.bloader = bloader;
	}

	private void forceClose(Connection conn, PreparedStatement stmt, ResultSet rslt, CSVWriter csw)
	{
		if (null != csw)
		{
			try
			{
				csw.close();
			}
			catch (IOException e)
			{
				LOGGER.error(e);
			}
		}
		if (rslt != null)
		{
			try
			{
				rslt.close();
			}
			catch (Exception ignoreIt)
			{
			}
		}
		if (stmt != null)
		{
			try
			{
				stmt.close();
			}
			catch (Exception ignoreIt)
			{
			}
		}
		if (conn != null)
		{
			try
			{
				conn.close();
			}
			catch (Exception ignoreIt)
			{
			}
		}
		bloader.close();
	}

	public void susceptibleRun() throws SQLException
	{

		PreparedStatement stmt = null;
		Connection conn = null;
		ResultSet rslt = null;
		RowWithMetaData lineData = null;
		int columnCount = 0;

		ImporterType.Acceptor[] acceptors = null;
		Object[] columnValues = null;
		String[] stringValues = null;

		try
		{
			conn = DriverManager.getConnection(m_config.jdbcurl, m_config.jdbcuser, m_config.jdbcpassword);
			DatabaseMetaData dbmd = conn.getMetaData();
			int resultSetType = ResultSet.TYPE_FORWARD_ONLY;
			if (!dbmd.supportsResultSetType(resultSetType))
			{
				resultSetType = ResultSet.TYPE_SCROLL_INSENSITIVE;
			}
			stmt = conn.prepareStatement("select * from " + m_config.jdbctable, resultSetType,
					ResultSet.CONCUR_READ_ONLY);
			stmt.setFetchSize(m_config.fetchsize);
			rslt = stmt.executeQuery();
			ResultSetMetaData mdata = rslt.getMetaData();
			columnCount = mdata.getColumnCount();

			/*
			 * Each column from jdbc source must be an importable type. First we determine if there is a corresponding
			 * importer type for the given column. If there is one then determine the column acceptor that converts the
			 * database type to the appropriate volt type, and add it to the acceptors array
			 */
			acceptors = new ImporterType.Acceptor[columnCount];
			for (int i = 1; i <= columnCount; ++i)
			{
				ImporterType type = ImporterType.forClassName(mdata.getColumnClassName(i));
				if (type == null)
				{
					throw new SQLException(String.format("Unsupported data type %s for column %s",
							mdata.getColumnTypeName(i), mdata.getColumnName(i)));
				}
				acceptors[i - 1] = type.getAcceptorFor(rslt, i);
			}
		}
		catch (Exception ex)
		{
			LOGGER.error("database query initialization failed", ex);
			forceClose(conn, stmt, rslt, null);
		}

		StringWriter sw = new StringWriter(16384);
		PrintWriter pw = new PrintWriter(sw, true);
		CSVWriter csw = new CSVWriter(pw);
		StringBuffer sb = sw.getBuffer();

		stringValues = new String[columnCount];

		try
		{
			while (rslt.next())
			{
				long rownum = m_totalRowCount.incrementAndGet();

				Arrays.fill(stringValues, "NULL");
				columnValues = new Object[columnCount];

				lineData = new RowWithMetaData(new String[1], rownum);

				try
				{
					for (int i = 0; i < columnCount; ++i)
					{
						columnValues[i] = acceptors[i].convert();
						stringValues[i] = acceptors[i].format(columnValues[i]);
					}

					csw.writeNext(stringValues);
					((String[]) lineData.rawLine)[0] = sb.toString();
					// 重置buffer
					sb.setLength(0);

					bloader.insertRow(lineData, columnValues);

				}
				catch (SQLException ex)
				{
					m_errHandler.handleError(lineData, null, getExceptionAndCauseMessages(ex));
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		}
		finally
		{
			bloader.updateQueue();
			
			forceClose(conn, stmt, rslt, csw);
		}
		LOGGER.debug("JSBCLoader Done.");
	}

	public static String getExceptionAndCauseMessages(Throwable ex)
	{
		if (ex == null)
			return "";
		StringBuilder sb = new StringBuilder(8192).append(ex.getMessage());
		while (ex.getCause() != null)
		{
			ex = ex.getCause();
			sb.append("\n+-- Caused by: ").append(ex.getMessage());
		}
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		try
		{
			susceptibleRun();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}
}
