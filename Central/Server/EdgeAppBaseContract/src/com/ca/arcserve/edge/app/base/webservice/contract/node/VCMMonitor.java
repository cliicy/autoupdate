package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Utils;

/**
 * Information of a virtual conversion monitor.
 * 
 * @author panbo01
 *
 */
public class VCMMonitor implements Serializable {

	private static final long serialVersionUID = 8493152394006664836L;
	
	private String hostName;
	private String userName;
	private String password;
	private Protocol protocol;
	private int port;
	private String uuid;
	
	/**
	 * Get host name of the monitor.
	 * 
	 * @return
	 */
	public String getHostName() {
		return hostName;
	}
	
	/**
	 * Set host name of the monitor.
	 * 
	 * @param hostName
	 */
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}
	
	/**
	 * Get user name of the monitor.
	 * 
	 * @return
	 */
	public String getUserName() {
		return userName;
	}
	
	/**
	 * Set user name of the monitor.
	 * 
	 * @param userName
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	/**
	 * Get password of the monitor.
	 * 
	 * @return
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * Set password of the monitor.
	 * 
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	/**
	 * Get protocol of web service the monitor.
	 * 
	 * @return
	 */
	public Protocol getProtocol() {
		return protocol;
	}
	
	/**
	 * Set protocol of web service the monitor.
	 * 
	 * @param protocol
	 */
	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}
	
	/**
	 * Get port of web service the monitor.
	 * 
	 * @return
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Set port of web service the monitor.
	 * 
	 * @param port
	 */
	public void setPort(int port) {
		this.port = port;
	}
	
	/**
	 * Get UUID of the monitor.
	 * 
	 * @return
	 */
	public String getUuid() {
		return uuid;
	}
	
	/**
	 * Set UUID of the monitor.
	 * 
	 * @param uuid
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VCMMonitor other = (VCMMonitor) obj;
		if(!Utils.simpleObjectEquals(hostName, other.getHostName()))
			return false;
		if(!Utils.simpleObjectEquals(userName, other.getUserName()))
			return false;
		if(!Utils.simpleObjectEquals(password, other.getPassword()))
			return false;
		if(!Utils.simpleObjectEquals(protocol,other.getProtocol()))
			return false;
		if(port != other.getPort())
			return false;
		if(!Utils.simpleObjectEquals(uuid, other.getUuid()))
			return false;
		return true;
	}
	
	public void update(VCMMonitor other){
		if (other == null)
			return;
		if(!Utils.simpleObjectEquals(hostName, other.getHostName()))
			hostName = other.getHostName();
		if(!Utils.simpleObjectEquals(userName, other.getUserName()))
			userName = other.getUserName();
		if(!Utils.simpleObjectEquals(password, other.getPassword()))
			password = other.getPassword();
		if(!Utils.simpleObjectEquals(protocol,other.getProtocol()))
			protocol = other.getProtocol();
		if(port != other.getPort())
			port = other.getPort();
		if(!Utils.simpleObjectEquals(uuid, other.getUuid()))
			uuid = other.getUuid();
	}
	
}
