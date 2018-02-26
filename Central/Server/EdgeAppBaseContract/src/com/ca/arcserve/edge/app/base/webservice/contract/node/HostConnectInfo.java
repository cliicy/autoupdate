/**
 * 
 */
package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;

/**
 * @author lijwe02
 * 
 */
public class HostConnectInfo implements Serializable {
	private static final long serialVersionUID = -8349040460591813922L;
	private int id;
	private int hostId;
	private String hostName;
	private int port;
	private Protocol protocol;
	private String userName;
	private String password;
	private String uuid;
	private VCMConverterType converterType;
	private int taskType;
	private int oldConverterId;
	private String authUuid;
	private GatewayId gatewayId = GatewayId.INVALID_GATEWAY_ID;
	
	public HostConnectInfo()
	{
	}

	/**
	 * Get host name of the host.
	 * 
	 * @return
	 */
	public String getHostName() {
		return hostName;
	}

	/**
	 * Set host name of the host.
	 * 
	 * @param hostName
	 */
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	/**
	 * Get port of the UDP agent web service running on the host.
	 * 
	 * @return
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Set port of the UDP agent web service running on the host.
	 * 
	 * @param port
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Get protocol of UDP agent the web service running on the host.
	 * 
	 * @return
	 */
	public Protocol getProtocol() {
		return protocol;
	}

	/**
	 * Set protocol of UDP agent the web service running on the host.
	 * 
	 * @param protocol
	 */
	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	/**
	 * Get user name used to login to the host.
	 * 
	 * @return
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * Set user name used to login to the host.
	 * 
	 * @param userName
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * Get password used to login to the host.
	 * 
	 * @return
	 */
	@EncryptSave
	public String getPassword() {
		return password;
	}

	/**
	 * Set password used to login to the host.
	 * 
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Get UUID of the UDP agent running on the host.
	 *  
	 * @return
	 */
	@EncryptSave
	public String getUuid() {
		return uuid;
	}

	/**
	 * Set UUID of the UDP agent running on the host.
	 * 
	 * @param uuid
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Get type of the converter.
	 * 
	 * @return
	 */
	public VCMConverterType getConverterType() {
		return converterType;
	}

	/**
	 * Set type of the converter.
	 * 
	 * @param converterType
	 */
	public void setConverterType(VCMConverterType converterType) {
		this.converterType = converterType;
	}

	public int getTaskType() {
		return taskType;
	}

	public void setTaskType(int taskType) {
		this.taskType = taskType;
	}

	public int getOldConverterId() {
		return oldConverterId;
	}

	public void setOldConverterId(int oldConverterId) {
		this.oldConverterId = oldConverterId;
	}

	@EncryptSave
	public String getAuthUuid() {
		return authUuid;
	}

	public void setAuthUuid(String authUuid) {
		this.authUuid = authUuid;
	}

	public int getHostId() {
		return hostId;
	}

	public void setHostId(int hostId) {
		this.hostId = hostId;
	}

	public GatewayId getGatewayId()
	{
		return gatewayId;
	}

	public void setGatewayId( GatewayId gatewayId )
	{
		if (gatewayId == null)
			gatewayId = GatewayId.INVALID_GATEWAY_ID;
		this.gatewayId = gatewayId;
	}

}
