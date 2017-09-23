package com.gaojy.cache.voltdb.core.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.voltdb.CLIConfig;
import org.voltdb.client.Client;
import org.voltdb.client.ClientConfig;
import org.voltdb.client.ClientFactory;
import org.voltdb.client.ClientImpl;
import org.voltdb.client.ClientResponse;

/**
 * @Title:
 * @Description:
 * @Author: gaojy
 * @Version: 1.0
 */
public class JDBCLoader implements BulkLoaderErrorHandler
{
	private static final Log LOGGER = LogFactory.getLog(JDBCLoader.class);

	private static BasicDataSource dataSource = new BasicDataSource();

	private static AtomicLong failedRows = new AtomicLong(0);

	/**
	 * Path of invalid row file that will be created.
	 */
	static String pathInvalidrowfile = "";
	/**
	 * report file name
	 */
	static String pathReportfile = "jdbcloaderReport.log";
	/**
	 * log file name
	 */
	static String pathLogfile = "jdbcloaderLog.log";
	private static JDBCLoaderConfig config = null;
	private static long start = 0;
	private static BufferedWriter out_invaliderowfile;
	private static BufferedWriter out_logfile;
	private static BufferedWriter out_reportfile;

	protected static Client voltClient;

	/**
	 * Used for testing only.
	 */
	public static boolean testMode = false;

	private class ErrorInfoItem
	{
		public long lineNumber;
		public String[] errorInfo;

		ErrorInfoItem(long line, String[] info)
		{
			lineNumber = line;
			errorInfo = info;
		}
	}

	private static final int ERROR_INFO_QUEUE_SIZE = Integer.getInteger("ERROR_INFO_QUEUE_SIZE", 500);
	// Errors we keep track only upto maxerrors
	private final LinkedBlockingQueue<ErrorInfoItem> m_errorInfo = new LinkedBlockingQueue<ErrorInfoItem>(
			ERROR_INFO_QUEUE_SIZE);
	private volatile AtomicLong m_errorCount = new AtomicLong(0);

	private class ErrorInfoFlushProcessor extends Thread
	{
		@Override
		public void run()
		{
			while (true)
			{
				try
				{
					ErrorInfoItem currItem;
					currItem = m_errorInfo.take();

					if (currItem.lineNumber == -1)
						return;

					if (currItem.errorInfo.length != 2)
					{
						System.out.println("internal error, information is not enough");
					}
					out_invaliderowfile.write(currItem.errorInfo[0]);
					String message = "Invalid input on line " + currItem.lineNumber + ". " + currItem.errorInfo[1];
					LOGGER.error(message);
					out_logfile.write(message + "\n  Content: " + currItem.errorInfo[0]);

					m_errorCount.incrementAndGet();

				}
				catch (FileNotFoundException e)
				{
					LOGGER.error("JDBC Loader report directory '" + config.reportdir + "' does not exist.", e);
				}
				catch (Exception x)
				{
					LOGGER.error(x);
				}

			}
		}
	}

	private ErrorInfoFlushProcessor m_errorinfoProcessor = null;

	public void launchErrorFlushProcessor()
	{
		m_errorinfoProcessor = new ErrorInfoFlushProcessor();
		m_errorinfoProcessor.start();
	}

	public void waitForErrorFlushComplete() throws InterruptedException
	{

		// Put an empty ErrorInfoItem
		ErrorInfoItem emptyErrorInfo = new ErrorInfoItem(-1, null);

		if (!m_errorInfo.offer(emptyErrorInfo))
		{
			m_errorInfo.put(emptyErrorInfo);
		}

		if (m_errorinfoProcessor != null)
			m_errorinfoProcessor.join();
	}

	public boolean handleError(RowWithMetaData metaData, ClientResponse response, String error)
	{
		failedRows.incrementAndGet();
		// Dont collect more than we want to report.
		if (m_errorCount.get() + m_errorInfo.size() >= config.maxerrors)
		{
			return true;
		}

		String rowContent = "Unknown row content";
		String[] row = (String[]) metaData.rawLine;
		if (row != null && row.length == 1 && row[0] != null && !row[0].trim().isEmpty())
		{
			rowContent = row[0];
		}

		String infoStr = (response != null) ? response.getStatusString() : error;
		String[] info = { rowContent, infoStr };

		ErrorInfoItem newErrorInfo = new ErrorInfoItem(metaData.lineNumber, info);

		try
		{
			if (!m_errorInfo.offer(newErrorInfo))
			{
				m_errorInfo.put(newErrorInfo);
			}
		}
		catch (InterruptedException ignoreIt)
		{
		}

		if (response != null)
		{
			byte status = response.getStatus();
			if (status != ClientResponse.USER_ABORT && status != ClientResponse.GRACEFUL_FAILURE)
			{
				System.out.println("Fatal Response from server for: " + response.getStatusString() + " for: "
						+ rowContent);
				System.exit(1);
			}
		}
		return false;
	}

	public boolean hasReachedErrorLimit()
	{
		return m_errorCount.get() + m_errorInfo.size() >= config.maxerrors;
	}

	private static void init()
	{
		dataSource.setDriverClassName("org.voltdb.jdbc.Driver");
		dataSource.setUrl("jdbc:voltdb://localhost:" + config.port);
		dataSource.setUsername(config.user);
		dataSource.setPassword(config.password);
	}

	public Connection getConnection() throws SQLException
	{
		return dataSource.getConnection();
	}

	/**
	 * Configuration options.
	 */
	public static class JDBCLoaderConfig extends CLIConfig
	{

		@Option(shortOpt = "p", desc = "procedure name to insert the data into the database")
		String procedure = "";

		@Option(shortOpt = "r", desc = "directory path for report files")
		String reportdir = System.getProperty("user.dir");

		@Option(shortOpt = "m", desc = "maximum errors allowed")
		int maxerrors = 100;

		@Option(shortOpt = "s", desc = "list of volt servers to connect to (default: localhost)")
		String servers = "localhost";

		@Option(desc = "username when connecting to the servers")
		String user = "";

		@Option(desc = "password to use when connecting to servers")
		String password = "";

		@Option(desc = "port to use when connecting to database (default: 21212)")
		int port = Client.VOLTDB_SERVER_PORT;

		@Option(desc = "JDBC Driver class to use to connect to JDBC servers.")
		String jdbcdriver = "";

		@Option(desc = "JDBC Url to connect to servers.")
		String jdbcurl = "";

		@Option(desc = "JDBC username when connecting to the servers")
		String jdbcuser = "";

		@Option(desc = "JDBC password to use when connecting to servers")
		String jdbcpassword = "";

		@Option(desc = "JDBC table to use for loading data from.")
		String jdbctable = "";

		@Option(desc = "Fetch Size for JDBC request (default: 100)")
		int fetchsize = 100;
		/**
		 * Batch size for processing batched operations.
		 */
		@Option(desc = "Batch Size for processing.")
		public int batch = 200;

		/**
		 * Table name to insert CSV data into.
		 */
		@AdditionalArgs(desc = "insert the data into the given table")
		public String table = "";
		// This is set to true when -p option us used.
		boolean useSuppliedProcedure = false;

		@Option(desc = "Use upsert instead of insert", hasArg = true)
		boolean update = true;

		/**
		 * Validate command line options.
		 */
		@Override
		public void validate()
		{
			if (maxerrors < 0)
			{
				exitWithMessageAndUsage("abortfailurecount must be >=0");
			}
			if (jdbcdriver.trim().equals(""))
			{
				exitWithMessageAndUsage("JDBC Driver can not be empty.");
			}
			if (jdbcurl.trim().equals(""))
			{
				exitWithMessageAndUsage("JDBC Url can not be empty.");
			}
			if (procedure.trim().equals("") && table.trim().equals(""))
			{
				exitWithMessageAndUsage("procedure name or a table name required");
			}
			if (!procedure.trim().equals("") && !table.trim().equals(""))
			{
				exitWithMessageAndUsage("Only a procedure name or a table name required, pass only one please");
			}
			if (port < 0)
			{
				exitWithMessageAndUsage("port number must be >= 0");
			}
			if (batch < 0)
			{
				exitWithMessageAndUsage("batch size number must be >= 0");
			}

			if ((procedure != null) && (procedure.trim().length() > 0))
			{
				useSuppliedProcedure = true;
			}
			if ((useSuppliedProcedure) && (update))
			{
				update = false;
				exitWithMessageAndUsage("update is not applicable when stored procedure specified");
			}
			if ("".equals(jdbctable.trim()))
			{
				jdbctable = table;
			}
			try
			{
				Class.forName(jdbcdriver);
			}
			catch (ClassNotFoundException ex)
			{
				exitWithMessageAndUsage("JDBC Driver class cannot be loaded make sure: " + jdbcdriver
						+ " is available in your classpath. You may specify it in CLASSPATH environent variable");
			}
		}

		/**
		 * Usage
		 */
		@Override
		public void printUsage()
		{
			System.out.println("Usage: jdbcloader [args] tablename");
			System.out.println("       jdbcloader [args] -p procedurename");
			super.printUsage();
		}
	}

	public static Client getClient(ClientConfig config, String[] servers, int port) throws Exception
	{
		final Client client = ClientFactory.createClient(config);

		for (String server : servers)
		{
			client.createConnection(server.trim(), port);
		}
		return client;
	}

	/**
	 * jdbcloader main. (main is directly used by tests as well be sure to reset statics that you need to start over)
	 *
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException, InterruptedException
	{
		start = System.currentTimeMillis();
		long insertTimeStart = start;
		long insertTimeEnd;

		final JDBCLoaderConfig cfg = new JDBCLoaderConfig();
		cfg.parse(JDBCLoader.class.getName(), args);

		config = cfg;

		configuration();

		// Split server list
		final String[] serverlist = config.servers.split(",");

		// Create connection
		final ClientConfig c_config = new ClientConfig(config.user, config.password);
		c_config.setProcedureCallTimeout(0); // Set procedure all to infinite
		try
		{
			voltClient = JDBCLoader.getClient(c_config, serverlist, config.port);
		}
		catch (Exception e)
		{
			// m_log.error("Error connecting to the servers: " + config.servers, e);
			System.exit(-1);
		}
		assert (voltClient != null);

		init();

		try
		{
			long readerTime;
			long insertCount;
			long ackCount;
			final JDBCLoader errHandler = new JDBCLoader();

			errHandler.launchErrorFlushProcessor();

			// Created Source reader
			JDBCStatementReader.initializeReader(cfg);

			VoltBulkLoader vbloader = new VoltBulkLoader((ClientImpl) voltClient, errHandler, cfg.table, cfg.batch,
					cfg.update);

			JDBCStatementReader jdbcReader = new JDBCStatementReader(vbloader, errHandler);
			Thread readerThread = new Thread(jdbcReader);
			readerThread.setName("JDBCSourceReader");
			//readerThread.setDaemon(true);

			// Wait for reader to finish.
			readerThread.start();
			readerThread.join();

			insertTimeEnd = System.currentTimeMillis();

			errHandler.waitForErrorFlushComplete();

			readerTime = (jdbcReader.m_parsingTime) / 1000000;
			insertCount = vbloader.getOutstandingRowCount();
			ackCount = insertCount - failedRows.get();

			if (errHandler.hasReachedErrorLimit())
			{
				LOGGER.warn("The number of failed rows exceeds the configured maximum failed rows: " + config.maxerrors);
			}

			if (LOGGER.isDebugEnabled())
			{
				LOGGER.debug("Parsing CSV file took " + readerTime + " milliseconds.");
				LOGGER.debug("Inserting Data took " + ((insertTimeEnd - insertTimeStart) - readerTime)
						+ " milliseconds.");
			}
			LOGGER.info("Read " + insertCount + " rows from file and successfully inserted " + ackCount
					+ " rows (final)");

			errHandler.produceFiles(ackCount, insertCount);

			close_cleanup();

			voltClient.close();
		}
		catch (Exception ex)
		{
			LOGGER.error("Exception Happened while loading CSV data", ex);
			System.exit(1);
		}
	}

	private static void configuration()
	{
		String insertProcedure = config.table.toUpperCase() + ".insert";
		;
		/*
		 * if (!config.table.equals("")) { insertProcedure = config.table.toUpperCase() + ".insert"; } else {
		 * insertProcedure = config.procedure; }
		 */
		if (!config.reportdir.endsWith("/"))
		{
			config.reportdir += "/";
		}
		try
		{
			File dir = new File(config.reportdir);
			if (!dir.exists())
			{
				dir.mkdirs();
			}
		}
		catch (Exception x)
		{
			LOGGER.error(x);
			System.exit(-1);
		}

		insertProcedure = insertProcedure.replaceAll("\\.", "_");
		pathInvalidrowfile = config.reportdir + "jdbcloader_" + insertProcedure + "_" + "invalidrows.csv";
		pathLogfile = config.reportdir + "jdbcloader_" + insertProcedure + "_" + "log.log";
		pathReportfile = config.reportdir + "jdbcloader_" + insertProcedure + "_" + "report.log";

		try
		{
			out_invaliderowfile = new BufferedWriter(new FileWriter(pathInvalidrowfile));
			out_logfile = new BufferedWriter(new FileWriter(pathLogfile));
			out_reportfile = new BufferedWriter(new FileWriter(pathReportfile));
		}
		catch (IOException e)
		{
			LOGGER.error(e);
			System.exit(-1);
		}
	}

	private void produceFiles(long ackCount, long insertCount)
	{
		long latency = System.currentTimeMillis() - start;
		LOGGER.info("Elapsed time: " + latency / 1000F + " seconds");

		try
		{
			// Get elapsed time in seconds
			float elapsedTimeSec = latency / 1000F;
			out_reportfile.write("JDBCLoader elaspsed: " + elapsedTimeSec + " seconds\n");
			long totalRowCnt;

			totalRowCnt = JDBCStatementReader.m_totalRowCount.get();

			out_reportfile.write("Input stopped after " + totalRowCnt + " rows read" + "\n");
			out_reportfile.write("Number of rows read from source: " + totalRowCnt + "\n");
			out_reportfile.write("Number of rows successfully inserted: " + ackCount + "\n");
			// if prompted msg changed, change it also for test case
			out_reportfile.write("Number of rows that could not be inserted: " + m_errorCount + "\n");
			out_reportfile.write("JDBCLoader rate: " + insertCount / elapsedTimeSec + " row/s\n");

			LOGGER.info("Invalid row file: " + pathInvalidrowfile);
			LOGGER.info("Log file: " + pathLogfile);
			LOGGER.info("Report file: " + pathReportfile);

			out_invaliderowfile.flush();
			out_logfile.flush();
			out_reportfile.flush();
		}
		catch (FileNotFoundException e)
		{
			LOGGER.error("JDBC Loader report directory '" + config.reportdir + "' does not exist.", e);
		}
		catch (Exception x)
		{
			LOGGER.error(x);
		}

	}

	private static void close_cleanup() throws IOException, InterruptedException
	{
		out_invaliderowfile.close();
		out_logfile.close();
		out_reportfile.close();
	}
}
