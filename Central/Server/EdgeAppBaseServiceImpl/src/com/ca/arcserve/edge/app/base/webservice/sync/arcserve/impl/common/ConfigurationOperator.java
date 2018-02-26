package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common;


import java.util.Calendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.db.Configuration;
import com.ca.arcserve.edge.app.base.db.IConfiguration;
import com.ca.arcserve.edge.app.base.jni.BaseWSJNI;
import com.ca.arcserve.edge.app.base.schedulers.impl.EdgeTask;
import com.ca.arcserve.edge.app.base.schedulers.impl.EdgeTaskFactory;
import com.ca.arcserve.edge.app.base.util.EdgeImpersonation;

public class ConfigurationOperator {
	public static final int _ArcserveType = 3;
	public static final int _GDBType = 1;
	public static final int _IsVisible = 1;
	public static final int _maxJobQueueSize = 1;
	protected static final int _defaultRetryTimes = 5;
	protected static final int _defaultRetryInterval = 300;
	protected static final int _defaultCheckTimes = 30;
	protected static final int _defaultCheckInterval = 6000; 
	public static final String _Protocol = "http://";
	public static final String _ServiceString = "/SyncService/metadata";
	public static final String _DefaultSyncPath = "<EdgeHome>\\ASBUSync";
	public static final String _LibPath = "\\Tomcat\\webapps\\EdgeWebUI\\WEB-INF\\lib";
	public static final String _PrefixTableName = "sync_as_";
	public static final String _SyncRootPathPerfix = "<EdgeHome>\\";
	public static final String _ZipFileExtension = ".zip";
	public static final String _PrefixLogMessage = "[ASBU Sync]";
	public static final String _FormatFileDirectory = "SyncFormat\\";
	public static final String _FormatFileExtension = ".fmt";
	public static final String _dbConfigfilePath = EdgeCommonUtil.EdgeInstallPath+EdgeCommonUtil.EdgeCONFIGURATION_DIR
	+ Configuration.DBCONFIGURATION_FILE;
	
	private static Logger _log = Logger.getLogger(ConfigurationOperator.class);

	private static Calendar cwithoutdaylightsaves = null;
	


	static {
		if (cwithoutdaylightsaves == null) {
			cwithoutdaylightsaves = Calendar.getInstance();
			TimeZone defaultTimeZone = TimeZone.getDefault();
			SimpleTimeZone value = new SimpleTimeZone(defaultTimeZone
					.getRawOffset(), defaultTimeZone.getID(), 0, 0, 0, 0, 0, 0,
					0, 0);
			cwithoutdaylightsaves.setTimeZone(value);
		}
	}
	public static Calendar getCwithoutdaylightsaves() {
		return cwithoutdaylightsaves;
	}
	
	// static final String _DefaultNameSpace =
	// "edge-webservice-impl.jar/com/ca/arcserve/edge/webservice/sync/arcserve/impl/";

	public static EdgeTask GetEdgeTask()
	{
		EdgeTask arcserveSyncTask = EdgeTaskFactory.getInstance().getTask(
				EdgeTaskFactory.EDGE_TASK_ARCSERVE_SYNC);
		
		if (arcserveSyncTask != null) {
			return arcserveSyncTask;
		}
		EdgeTaskFactory etf = EdgeTaskFactory.getInstance();
		arcserveSyncTask = new EdgeTask();
		arcserveSyncTask
				.setMaxExecuteQueueSize(ConfigurationOperator._maxJobQueueSize);
		etf.Add(EdgeTaskFactory.EDGE_TASK_ARCSERVE_SYNC, arcserveSyncTask);
		etf.LanuchTask(EdgeTaskFactory.EDGE_TASK_ARCSERVE_SYNC);
		try {
			for (int i = 3; i > 0; i--) {
				arcserveSyncTask = EdgeTaskFactory.getInstance().getTask(
						EdgeTaskFactory.EDGE_TASK_ARCSERVE_SYNC);
				if (arcserveSyncTask != null) {
					Thread.sleep(1000); // seems sync task init need some
					// time
					return arcserveSyncTask;
				}

				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			_log.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	/*public static List<String> GetDumpFileList(String strConfigFile) {
		
		 * String prefix =
		 * ConfigurationOperater.class.getPackage().getName().replace('.', '/');
		 
		InputStream in = null;
		List<String> fileNameList = new LinkedList<String>();
		try {
			ClassLoader loader = ConfigurationOperator.class.getClassLoader();
			in = loader.getResourceAsStream(strConfigFile);

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = null;
			try {
				db = dbf.newDocumentBuilder();
			} catch (ParserConfigurationException e) {
				ConfigurationOperator.errorMessage(e.getMessage(), e);
			}

			Document doc = null;
			try {
				doc = db.parse(in);
			} catch (SAXException e) {
				ConfigurationOperator.errorMessage(e.getMessage(), e);
			} catch (IOException e) {
				ConfigurationOperator.errorMessage(e.getMessage(), e);
			}

			Element root = doc.getDocumentElement();
			NodeList dumpFileList = root.getElementsByTagName("Table");
			for (int i = 0; i < dumpFileList.getLength(); i++) {
				Element item = (Element) dumpFileList.item(i);
				String strFileName = item.getAttribute("Name");
				fileNameList.add(strFileName);
			}
		} catch (Exception e) {
			ConfigurationOperator.errorMessage(e.getMessage(), e);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				ConfigurationOperator.errorMessage(e.getMessage(), e);
			}
		}
		return fileNameList;
	}*/

	public static boolean ArcsyncImpersonateUser() {
		int x = EdgeImpersonation.getInstance().impersonate();
		if (x != 0) {
			ConfigurationOperator.errorMessage("impersonate user: "
					+ EdgeImpersonation.getInstance().getLastUsername() + " failed");
			return false;
		}
		return true;
	}

	public static void ArcsyncRevertToSelf() {
		BaseWSJNI.revertToSelf();
	}

	public static String GetServiceConnectString(String serverName, int port) {
		return ConfigurationOperator._Protocol + serverName + ":" + port
				+ ConfigurationOperator._ServiceString;
	}

	public static void debugMessage(String message) {
		_log.debug(_PrefixLogMessage + message);
	}

	public static void debugMessage(String message, Throwable t) {
		_log.debug(_PrefixLogMessage + message, t);
	}

	public static void errorMessage(String message) {
		_log.error(_PrefixLogMessage + message);
	}

	public static void errorMessage(String message, Throwable t) {
		_log.error(_PrefixLogMessage + message, t);
	}

	public static Logger getLogger() {
		return _log;
	}
	
	public static String getDefaultDB(){
		return IConfiguration.DEFAULT_DB;
	}

	public static int getDefaultretrytimes() {
		return _defaultRetryTimes;
	}

	public static int getDefaultretryinterval() {
		return _defaultRetryInterval;
	}

	public static int getDefaultchecktimes() {
		return _defaultCheckTimes;
	}

	public static int getDefaultcheckinterval() {
		return _defaultCheckInterval;
	}
	
	
	
}
