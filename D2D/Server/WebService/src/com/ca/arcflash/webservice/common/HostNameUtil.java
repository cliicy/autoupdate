/**
 * 
 */
package com.ca.arcflash.webservice.common;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

/**
 * @author lijwe02
 * 
 */
public class HostNameUtil {
	private static final Logger logger = Logger.getLogger(HostNameUtil.class);

	/**
	 * Check whether the specify hostName is the ip address
	 * 
	 * @param hostName
	 *            the host name for check
	 * @return if the host name is ip address, return true, otherwise return fasle
	 */
	public static boolean isIPAddress(String hostName) {
		if (hostName == null || hostName.trim().length() == 0) {
			return false;
		}
		String[] parts = hostName.split("\\.");
		if (parts.length != 4) {
			return false;
		}
		for (String part : parts) {
			try {
				int intValue = Integer.parseInt(part);
				if (intValue <= 0 || intValue > 255) {
					return false;
				}
			} catch (NumberFormatException e) {
				logger.error("Error on parse ipAddress:" + hostName, e);
				return false;
			}
		}
		return true;
	}

	/**
	 * Parse the specify ip address, if the parameter is ip address, then return every parts of the ip address
	 * 
	 * @param ipAddress
	 *            the ip address for parse
	 * @return if the parameter is not ip address, then return null, other return byte array
	 */
	public static byte[] parseIPAddress(String ipAddress) {
		if (isIPAddress(ipAddress)) {
			String[] parts = ipAddress.split("\\.");
			int partLength = parts.length;
			byte[] ipParts = new byte[partLength];
			for (int i = 0; i < partLength; i++) {
				ipParts[i] = (byte) (Integer.parseInt(parts[i]));
			}
			return ipParts;
		}
		return null;
	}

	/**
	 * get the host name for the specify hostName, if the parameter is IP address, then return the host name of the host
	 * 
	 * @param hostName
	 *            host name to be deal with
	 * @return the name of the host
	 */
	public static String getHostName(String hostName) {
		try {
			String tempHostName = hostName;
			byte[] ipParts = parseIPAddress(hostName);
			if (ipParts != null) {
				InetAddress addr = InetAddress.getByAddress(ipParts);
				tempHostName = addr.getHostName();
			}
			if (isIPAddress(tempHostName)) {
				return tempHostName;
			}
			int dotIndex = tempHostName.indexOf('.');
			if (dotIndex != -1) {
				return tempHostName.substring(0, dotIndex);
			}
			return tempHostName;
		} catch (UnknownHostException e) {
			logger.error("Error on getHostName for host:" + hostName, e);
			return hostName;
		}
	}
	
	public static String getLocalHostName() {
		String hostname = "";
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			logger.error(e.getMessage(), e);
		}
		return hostname;
	}
}
