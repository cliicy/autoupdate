/**
 * 
 */
package com.ca.arcflash.webservice.service;

import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.ca.arcflash.ha.vmwaremanager.CAVMwareInfrastructureManagerFactory;
import com.ca.arcflash.ha.vmwaremanagerIntf.CAVirtualInfrastructureManager;

/**
 * @author lijwe02
 * 
 */
public class VMWareConnectionCache {
	private static final Logger logger = Logger.getLogger(VMWareConnectionCache.class);
	private static Map<String, CAVirtualInfrastructureManager> CONNECTION_MAP = new Hashtable<String, CAVirtualInfrastructureManager>();
	private static Lock LOCK = new ReentrantLock();

	public static CAVirtualInfrastructureManager getVMWareConnection(String esxVCServer, String userName,
			String password, String protocol, boolean ignoreCertAuthentidation, long viPort) {
		LOCK.lock();
		try {
			String key = getKey(esxVCServer, userName, password, protocol, ignoreCertAuthentidation, viPort);
			CAVirtualInfrastructureManager con = CONNECTION_MAP.get(key);
			if (con == null) {
				try {
					con = CAVMwareInfrastructureManagerFactory.getCAVMwareVirtualInfrastructureManager(esxVCServer,
							userName, password, protocol, true, viPort);
					if (con != null) {
						CONNECTION_MAP.put(key, con);
					}
				} catch (Exception e) {
					logger.error("Failed to get vmware connection, esxServer=" + esxVCServer + ", userName=" + userName
							+ ", protocol:" + protocol + ", ignoreCertAuthentidation=" + ignoreCertAuthentidation
							+ ",port:" + viPort, e);
				}
			}
			return con;
		} finally {
			LOCK.unlock();
		}
	}

	public static String getKey(String esxVCServer, String userName, String password, String protocol,
			boolean ignoreCertAuthentidation, long viPort) {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append(esxVCServer).append("-").append(userName).append("-")
				.append(HAService.getInstance().getNativeFacade().encrypt(password)).append("-").append(protocol)
				.append("-").append(ignoreCertAuthentidation).append("-").append(viPort);
		return strBuf.toString();
	}
}
