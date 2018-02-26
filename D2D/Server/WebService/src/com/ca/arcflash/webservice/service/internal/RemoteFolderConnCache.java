package com.ca.arcflash.webservice.service.internal;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonRegistryKey;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.common.WindowsRegistry;
import com.ca.arcflash.service.jni.model.JNetConnInfo;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.BrowserService;
import com.ca.arcflash.webservice.service.RestoreService;
import com.ca.arcflash.webservice.service.ServiceException;

/**
 * Note: All locks to remote folders must be gotten using method{@link #getLockByPath getLockByPath}
 * if one lock is needed and {@link #}
 */
public class RemoteFolderConnCache {

	private static final Logger logger = Logger.getLogger(RemoteFolderConnCache.class);

	private static String LAST_ACCESS_PATH = "LAST_ACCESS_PATH";

	public static final int REMOTE_DRIVE = 4;

	private Map<String, Set<JNetConnInfo>> machineToConnMap = new HashMap<String, Set<JNetConnInfo>>();

	private Map<String, Lock> machineLockMap = new HashMap<String, Lock>();

	private static boolean useCache = true;

	public static final RemoteFolderConnCache cache = new RemoteFolderConnCache();

	static {
		try {
			WindowsRegistry registry = new WindowsRegistry();
			int handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
			String disconnectValue = registry.getValue(handle, "DisconnectWhenValidate");
			logger.debug("DisconnectWhenValidate:" + disconnectValue);
			registry.closeKey(handle);

			if (StringUtil.isEmptyOrNull(disconnectValue))
				useCache = true;
			else if (disconnectValue.equals("0"))
				useCache = false;
			else
				useCache = true;
		} catch (Throwable e) {
			logger.debug("get registry edit error:" + e.getMessage(), e);
		}
	}

	private RemoteFolderConnCache() {
	}

	public static RemoteFolderConnCache getInstance() {
		return cache;
	}

	public synchronized Set<JNetConnInfo> getConnections(String machine){
		return null;
	}

	/**
	 * Add the connection to the cache if the path is remote path and not cached.
	 * Note: To avoid deadlock, you should get the lock to the machine this <code>remotePath</code>
	 *       lies on and lock it before calling this method.
	 * @param remotePath
	 * @param domain
	 * @param userName
	 * @param passwd
	 * @param parseUsr
	 */
	public synchronized void addConnections(String remotePath, String domain, String userName, String passwd, boolean parseUsr) {
		if(!useCache())
			return;
		logger.debug("addConnections() - start");
		//logger.debug("remotePath:" + remotePath);
		//logger.debug("domain:" + domain);
		//logger.debug("userName:" + userName);
		if (parseUsr && domain.trim().length() == 0) {
			int indx = userName.indexOf('\\');
			if (indx > 0) {
				domain = userName.substring(0, indx);
				userName = userName.substring(indx + 1);
			}
		}

		//parse usr if parseUsr == true
		try {
			if (StringUtil.isEmptyOrNull(remotePath) || BackupService.getInstance().getDestDriveType(remotePath) != REMOTE_DRIVE) {
				logger.debug("remotePath is empty or null, or is local path - end");
				return;
			}

			String machineName = getMachineName(remotePath);
			if(StringUtil.isEmptyOrNull(machineName)) {
				logger.debug("Machine name is empty - end");
				return;
			}

			machineName = machineName.toLowerCase();
			Set<JNetConnInfo> sharePathSet = machineToConnMap.get(machineName);
			if(sharePathSet == null) {
				sharePathSet = new HashSet<JNetConnInfo>();
				machineToConnMap.put(machineName, sharePathSet);
			}
			for (Iterator<JNetConnInfo> iterator = sharePathSet.iterator(); iterator.hasNext();) {
				JNetConnInfo netConnInfo = iterator.next();
				if(getNormalizedPath(remotePath).equalsIgnoreCase(getNormalizedPath(netConnInfo.getSzDir()))
					&& getNonNullString(domain).equalsIgnoreCase(getNonNullString(netConnInfo.getSzDomain()))
					&& getNonNullString(userName).equalsIgnoreCase(getNonNullString(netConnInfo.getSzUsr()))) {
					logger.debug("Connection info already cached - end");
					return;
				}
			}

			JNetConnInfo info = new JNetConnInfo();
			info.setSzDir(remotePath);
			info.setSzDomain(domain);
			info.setSzUsr(userName);
			info.setSzPwd(passwd);

			sharePathSet.add(info);
		}catch(Throwable e) {
			logger.error(e.getMessage(), e);
		}
		logger.debug("addConnections() - end");
	}

	private static boolean useCache() {
//		try {
//			WindowsRegistry registry = new WindowsRegistry();
//			int handle = registry.openKey(REGISTRY_ROOT);
//			String disconnectValue = registry.getValue(handle, "DisconnectWhenValidate");
//			logger.debug("DisconnectWhenValidate:" + disconnectValue);
//			registry.closeKey(handle);
//
//			if (StringUtil.isEmptyOrNull(disconnectValue))
//				useCache = true;
//			else if (disconnectValue.equals("0"))
//				useCache = false;
//			else
//				useCache = true;
//		} catch (Throwable e) {
//			logger.debug("get registry edit error:" + e.getMessage(), e);
//		}
		return useCache;
	}

	private static String getNormalizedPath(String remotePath) {
		remotePath = getNonNullString(remotePath);
		if(remotePath.endsWith("\\") || remotePath.endsWith("/"))
			remotePath = remotePath.substring(0, remotePath.length() - 1);
		return remotePath;
	}

	private static String getNonNullString(String remotePath) {
		remotePath = remotePath == null ? "" : remotePath;
		return remotePath;
	}

	/**
	 * Note: To avoid deadlock, you should first get the lock to the machine and lock it
	 * 		 before calling this method.
	 * @param machineName
	 */
	public synchronized void disconnectAllToMachine(String machineName) {
		if(!useCache())
			return;
		logger.debug("disconnectAllToMachine() - start");
		// parse usr if parseUsr == true
		try {
			Set<String> keySet = machineToConnMap.keySet();
			logger.debug("Cached connection information, machine number is " + keySet.size());
			for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
				String name = iterator.next();
				Set<JNetConnInfo> info = machineToConnMap.get(name);
				logger.debug("machine:" + name + ", paths: " + convertConnInfoToString(info.toArray(new JNetConnInfo[0])));
			}

			if (StringUtil.isEmptyOrNull(machineName)) {
				logger.debug("Machine name is empty.");
				return;
			}
			else {
				logger.debug("machine name:" + machineName);
			}

			machineName = machineName.toLowerCase();
			Set<JNetConnInfo> sharePathSet = machineToConnMap.get(machineName);
			if (sharePathSet == null || sharePathSet.isEmpty()) {
				logger.debug("No share path cached.");
				return;
			}

			for (Iterator<JNetConnInfo> iterator = sharePathSet.iterator(); iterator
					.hasNext();) {
				JNetConnInfo connInfo = iterator.next();
				try {
					logger.debug("disconnect to " + connInfo.getSzDir());
					BrowserService.getInstance().disconnectRemotePath(
							connInfo.getSzDir(), connInfo.getSzDomain(),
							connInfo.getSzUsr(), connInfo.getSzPwd(), true);
				}catch (Throwable e) {
					logger.debug("Ignorable error: Fails to disconnect to " + connInfo.getSzDir() + ", " + e.getMessage());
				}

			}
			sharePathSet.clear();
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}
		logger.debug("disconnectAllToMachine() - end");

	}

	public static String getMachineName(String remotePath) {
		remotePath = getNormalizedPath(remotePath);
		if(remotePath.startsWith("\\\\") && remotePath.length() > 2) {
			int indexBackSlash = remotePath.indexOf("\\", 3);
			int indexSlash = remotePath.indexOf("/", 3);
			int index = indexBackSlash == -1 || indexSlash > 0 && indexBackSlash > indexSlash ? indexSlash : indexBackSlash;
			if(index < 0)
				index = remotePath.length();

			return remotePath.substring(2, index);
		}
		else
			logger.debug("Cannot get machine name from remote path ");
		return null;
	}

	public synchronized Lock getLockByMachine(String machineName) {
		if(!useCache())
			return null;
		logger.debug("getLockByMachine(String) - start");
		if(StringUtil.isEmptyOrNull(machineName)) {
			logger.debug("machine name is null. - getLockByMachine(String) - end");
			return null;
		}

		machineName = machineName.toLowerCase();
		Lock machineLock = machineLockMap.get(machineName);

		if(machineLock == null) {
			machineLock = new ReentrantLock();
			machineLockMap.put(machineName, machineLock);
		}

		logger.debug("getLockByMachine(String) - end");
		return machineLock;
	}

	public synchronized Lock getLockByPath(String destination) {
		if(!useCache())
			return null;
		String machine = getMachineName(destination);
		return getLockByMachine(machine);
	}

	/*
	 * Returns the last used connection information for the current session.
	 */
	private static JNetConnInfo getLastUsedConnInfo(HttpSession session) {
		if(!useCache())
			return null;
		logger.debug("getLastUsedConnInfo - start");
		{
			JNetConnInfo connInfo = (JNetConnInfo)session.getAttribute(LAST_ACCESS_PATH);
			if(connInfo == null) {
				logger.error("Fails to get the last used connection information.");
				try {
					BackupConfiguration config = BackupService.getInstance().getBackupConfiguration();
					if(config == null || StringUtil.isEmptyOrNull(config.getDestination())) {
						logger.debug("Fails to get backup configuration or the destination in backup setting is null. - reEstalishConnetion end");
						return null;
					}

					logger.debug("Get backup configuration");

					connInfo = new JNetConnInfo();
					connInfo.setSzDir(config.getDestination());
					connInfo.setSzDomain(getDomainName(config.getUserName()));
					connInfo.setSzUsr(getUserName(config.getUserName()));
					connInfo.setSzPwd(config.getPassword());
				}
				catch(Throwable e) {
					logger.debug("Fails to get backup configuration:" + e.getMessage());
				}
			}

			if(connInfo != null)
				logger.debug("Last used connection information:" + convertConnInfoToString(new JNetConnInfo[] {connInfo}));

			logger.debug("getLastUsedConnInfo - end");
			return connInfo;
		}

	}

	private static String convertConnInfoToString(JNetConnInfo[] connInfo) {
		if(connInfo == null)
			return "";
		StringBuilder str = new StringBuilder();
		str.append("total:").append(connInfo.length);
		for (int i = 0; i < connInfo.length; i++) {
			str.append(",").append(i).append(":");
			str.append("path:").append(connInfo[i].getSzDir())
			    .append(",domain:").append(connInfo[i].getSzDomain())
				.append(",userName:").append(connInfo[i].getSzUsr());
		}
		return str.toString();
	}

	/**
	 * Reestablish connection to the remote folder the session last accessed
	 * @param context
	 * @throws ServiceException
	 */
	public static void reEstalishConnetion(HttpSession session) throws ServiceException {
		if(!useCache())
			return;
		logger.debug("reEstalishConnetion - start");

		//Reestablish connection whether the connection exists or not. Backend API knows when to establish it.
		JNetConnInfo connInfo = getLastUsedConnInfo(session);
		if(connInfo == null) {
			logger.debug("Can not get connection information from the current session and there's no backup setting configuration file. ReestalishConnetion - end");
			return;
		}
		try {
			//logger.debug("Reestablish connection to path:" + connInfo.getSzDir() + ", domain:" + connInfo.getSzDomain()
			//		+ ", user:" + connInfo.getSzUsr() + ", password:" + connInfo.getSzPwd());
			RestoreService.getInstance().checkContainRecoveryPoints(connInfo);
		}
		catch(Throwable e) {
			//the exception should never be caught since validation has passed before this method call.
			//That is, execution flow should never reach here.
			logger.error("Ignorable error, reestalishConnetion fails:" + e.getMessage());
		}

		logger.debug("reEstalishConnetion - end");
	}

	/**
	 * Cache the path with credential to the current session.
	 * When to call this function? only in ValidateSource in not enough. How about this function is not called?
	 * Note: This method cache not only local but remote path. this function should be called
	 * only if its following web service methods invocations rely on the connection established.
	 *
	 * @param remotePath
	 * @param getNormalizedPath(domain)
	 * @param getNormalizedPath(userName)
	 * @param getNormalizedPath(passwd)
	 */
	public static void cachePathToSession(HttpSession session,String path, String domain, String userName, String passwd) {
		if(!useCache())
			return;
		logger.debug("cachePathToSession - start");
		{
			JNetConnInfo connInfo = new JNetConnInfo();
			connInfo.setSzDir(path);
			connInfo.setSzDomain(domain);
			connInfo.setSzUsr(userName);
			connInfo.setSzPwd(passwd);

			session.setAttribute(LAST_ACCESS_PATH, connInfo);
			logger.debug("Cache the connection information. Path:" + path + ", domain:" + domain + ", user name:" + userName);
		}
		logger.debug("cachePathToSession - end");
	}

	/**
	 * Returns the cached path for the current session.
	 * @return
	 */
	public static String getCachedPath(HttpSession session) {
		logger.debug("getCachedPath() - start");
		JNetConnInfo connInfo = getLastUsedConnInfo(session);
		if(connInfo != null) {
			logger.debug("path: " + connInfo.getSzDir());
			logger.debug("getCachedPath() - end");
			return connInfo.getSzDir();
		}
		logger.debug("getCachedPath() - end");
		return null;
	}

	/**
	 * Returns user name in the string like "domain\\userName"
	 * @param domainUserName
	 * @return
	 */
	public static String getUserName(String domainUserName)
	{
		if (domainUserName == null || domainUserName.isEmpty())
			return "";

		int pos = domainUserName.indexOf("\\");
		if (pos > 0 && pos < domainUserName.length() - 1)
			return domainUserName.substring(pos+1);

		return domainUserName;
	}

	/**
	 * Returns domain name in the string like "domain\\userName"
	 * @param userName
	 * @return
	 */
	public static String getDomainName(String domainUserName)
	{
		if (domainUserName == null || domainUserName.isEmpty())
			return "";

		int pos = domainUserName.indexOf("\\");
		if (pos > 0)
			return domainUserName.substring(0, pos);

		return "";
	}

	public synchronized void clearConnection(String destination) {
		String machineName = getMachineName(destination);
		if(StringUtil.isEmptyOrNull(machineName))
			return;
		Set connection = machineToConnMap.get(machineName);
		if(connection != null)
			connection.clear();
	}
	
	/**
	 * return locks for the specified destinations.
	 * @param destination
	 * @return
	 */
//	public synchronized Lock[] getBatchLocks(String[] destinations) {
//		Arrays.sort(destinations);
//		for (int i = 0; i < destinations.length; i++) {
//			Lock lock = getInstance().getLock(destinations[0]);
//
//		}
////		return
//	}

//	public synchronized void batchUnlock(String[] destinations) {
//		Arrays.sort(destinations);
//		for (int i = 0; i < destinations.length; i++) {
//			Lock lock = getInstance().getLock(destinations[0]);
//			if(lock != null)
//				lock.unlock();
//		}
//	}
//	In search restore: \\gaosa01-w2k3-1\copy\VM-W2K3 with validation
//	In export recovery points: \\gaosa01-w2k3-1\VM-W2K3s\6\VM-W2K3 without validate
}
