package com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.ca.arcflash.common.WindowsRegistry;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeSyncDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.jni.BaseWSJNI;
import com.ca.arcserve.edge.app.base.schedulers.EdgeExecutors;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.configuration.ConfigurationServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.DBConfigInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.DBConfigInfo.AuthenticationType;
import com.ca.arcserve.edge.app.base.webservice.sync.arcserve.impl.common.ConfigurationOperator;
import com.ca.arcserve.edge.webservice.jni.model.EdgeAccount;

public class BCPInvokeTaskImpl {

	private static ConfigurationServiceImpl config = null;
	private static String bcpPath = null;
	private static final String _sql2k8RegKey = "SOFTWARE\\Microsoft\\Microsoft SQL Server\\100\\Tools\\Setup";
	private static final String _sql2k5RegKey = "SOFTWARE\\Microsoft\\Microsoft SQL Server\\90\\Tools\\Setup";
	private static final String _sqlToolRegKey = "SQLPath";
	private static final String _bcpPostFix = "Binn\\bcp.exe";
	private static final String _defaultSql2k8BcpPath = "C:\\Program Files\\Microsoft SQL Server\\100\\Tools\\Binn\\bcp.exe";
	private static final String _defaultSql2k5BcpPath = "C:\\Program Files\\Microsoft SQL Server\\90\\Tools\\Binn\\bcp.exe";
	private static String localhost = null;
	//private static final String _prefixSRMTable = "sync_as_tbl_wcf";
	private ASBUJobInfo jobinfo = null;

	private BCPInvokeTaskImpl(){
		
	}
	public ASBUJobInfo getJobinf() {
		return jobinfo;
	}

	public void setJobinf(ASBUJobInfo jobinfo) {
		this.jobinfo = jobinfo;
	}

	static void readResult(final InputStream is) {
		EdgeExecutors.getCachedPool().submit(new Runnable() {
			public void run() {
				try {
					while (is.read() >= 0);
				} catch (IOException e) {
					ConfigurationOperator.debugMessage(e.getMessage(), e);
				} 
			}
		});
	}

	public static BCPInvokeTaskImpl GetBCPInvokeTask() {
		BCPInvokeTaskImpl instance = null;
		synchronized (BCPInvokeTaskImpl.class) {
			instance = new BCPInvokeTaskImpl();
			if (config == null)
				config = new ConfigurationServiceImpl(
						ConfigurationOperator._dbConfigfilePath);

			if (bcpPath == null)
				bcpPath = GetBCPPath();

			if (localhost == null) {
				try {
					localhost = InetAddress.getLocalHost().toString();
					if (localhost.indexOf('/') != -1)
						localhost = localhost.substring(0, localhost
								.indexOf('/'));
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					ConfigurationOperator.debugMessage("Get locahost failed"
							+ e.getMessage());
				}
			}
		}
		return instance;
	}

	public boolean run(String nodeName, String tableName, String fmt, String dat)
			throws Exception {

		synchronized (this) {
			DBConfigInfo dbInfo = null;
			if (dbInfo == null)
				try {
					dbInfo = config.getDatabaseConfiguration();
				} catch (EdgeServiceFault e) {
					// TODO Auto-generated catch block
					ConfigurationOperator.errorMessage(e.getMessage(), e);
				}
			String hostname2 = dbInfo.getSqlServer();

			if (hostname2.compareToIgnoreCase(localhost) == 0) {
				IEdgeSyncDao syncdao = DaoFactory.getDao(IEdgeSyncDao.class);
				syncdao.as_edge_sync_data(tableName, dat, fmt);
				return true;
			}

			String serverCmd = GetServerCommand(dbInfo);

			String paramenter = ConfigurationOperator.getDefaultDB() + ".dbo."
					+ tableName + " in \"" + dat + "\" -f \"" + fmt + "\" "
					+ serverCmd;

			try {
				ConfigurationOperator.debugMessage("BCP import data: " + dat
						+ "table: " + tableName);

				int bcpRetValue = 0;
				if (dbInfo.getAuthentication() == AuthenticationType.WindowsAuthentication) {
					EdgeAccount edgeAccount = new EdgeAccount();
					BaseWSJNI.getEdgeAccount(edgeAccount);

					bcpRetValue = BaseWSJNI.createProcessAsUser(edgeAccount
							.getUserName(), edgeAccount.getDomain(),
							edgeAccount.getPassword(), bcpPath + " "
									+ paramenter);
				} else {
					Process p = Runtime.getRuntime().exec(
							bcpPath + " " + paramenter);
					/*
					 * following two line is used to clear the input buffer and
					 * error buffer to solve waitFor hang problem.
					 */
					readResult(p.getInputStream());
					readResult(p.getErrorStream());

					p.waitFor();
					
					bcpRetValue = p.exitValue();

					if (bcpRetValue != 0) {
						OutputError(p.getInputStream(), dat);
						OutputError(p.getErrorStream(), dat);
					}
				}
				
				if (bcpRetValue != 0) {
					ConfigurationOperator
							.errorMessage("BCP return error for : " + dat
									+ " Exit code: " + bcpRetValue);
					return false;
				}

			} catch (Exception e) {
				SyncASBUActivityLog _log = SyncASBUActivityLog
						.GetInstance(jobinfo);
				_log.WriteError(nodeName, String.format(SyncActivityLogMsg
						.getSyncfullbcperrormsg(), dat, e.getMessage()));
				return false;
			}
			//}

			// ConfigurationOperator.debugMessage("BCPCOMPATH::::: " + bcpPath +
			// " " + paramenter);

		}

		return true;
	}

	private static String GetBCPPath() {
		String prefixStr = "\"";
		WindowsRegistry reg = new WindowsRegistry();
		int handle = 0;
		try {
			handle = reg.openKey(_sql2k8RegKey);
			if (handle == 0) {
				handle = reg.openKey(_sql2k5RegKey);
			}
			
			if (handle != 0) {
				String sqlToolPath = reg.getValue(handle, _sqlToolRegKey);
				return prefixStr + sqlToolPath + _bcpPostFix + prefixStr + " ";
			}
		} catch (Exception e) {
			ConfigurationOperator.errorMessage(e.getMessage(), e);
		} finally{
			if(reg != null && handle != 0)
				try {
					reg.closeKey(handle);
				} catch (Exception e) {
					//ignore
				}
		}

		return GetDefaultBcpPath();
	}

	public static String GetDefaultBcpPath() {
		String BcpPath;
		
		File bcpFile = new File(_defaultSql2k8BcpPath);
		if (bcpFile.exists()) {
			BcpPath = _defaultSql2k8BcpPath;
		} else {
			bcpFile = new File(_defaultSql2k5BcpPath);
			if (bcpFile.exists()) {
				BcpPath = _defaultSql2k5BcpPath;
			} else {
				BcpPath = _defaultSql2k8BcpPath;
			}
		}
		
		String defaultDirPath = System.getenv("SystemRoot");
		BcpPath = defaultDirPath.substring(0, 3) + BcpPath.substring(3);

		return "\"" + BcpPath + "\"";
	}
	
	private void OutputError(InputStream in, String dat) throws IOException {
		BufferedInputStream bfIn = new BufferedInputStream(in);
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(bfIn));
			String error = null;
			while ((error = br.readLine()) != null)
				ConfigurationOperator.errorMessage(String.format(
						SyncActivityLogMsg.getSyncfullbcperrormsg(), dat,
						String.format("Error:%s", error)));
		} finally {
			bfIn.close();
		}
	}
	
	public String GetServerCommand(DBConfigInfo dbInfo){
		String serverCmd = "-q -S " + dbInfo.getSqlServer();

		if (!StringUtil.isEmptyOrNull(dbInfo.getInstance())
				&& dbInfo.getInstance().compareToIgnoreCase("MSSQLSERVER") != 0)
			serverCmd += "\\" + dbInfo.getInstance();
		
		if(dbInfo.getServerPort() != -1){
			serverCmd += ","+dbInfo.getServerPort();
		}

		if (dbInfo.getAuthentication() == AuthenticationType.WindowsAuthentication)
			serverCmd += " -T ";
		else
			serverCmd += " -U " + dbInfo.getAuthUserName() + " -P "
					+ dbInfo.getAuthPassword();
		
		return serverCmd;
	}
}
