package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.ProductType;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
@XmlRootElement
public class DeployTargetDetail extends DeployTarget implements Serializable {


	private static final long serialVersionUID = 1L;
	
	public static String localAdmin = "";
	public static @NotPrintAttribute String localAdminPassword = "";
	public static String localDomain = "";

	private String serverName = "";
	private String uuid = ""; // not used
	private boolean autoStartRRService = false;
	private int status = 0;
	private int taskstatus= 0;
	private int percentage = 0;
	private String progressMessage = "";
	private long msgCode = 0;
	private boolean selected = false;
	private int productType = Integer.parseInt(ProductType.ProductD2D);
	private String warningMessage = "";
	private Integer targetId;
	private String planIds;
	private String connectTime="0";
	
	//For activity log will use the same message with UI , so the detail message will be built in the background
	private String finalTitleMessage="";
	private String finalDetailMessage="";
	
	private GatewayId gatewayId = GatewayId.INVALID_GATEWAY_ID;
	
	private boolean checkDestinationVersion = true;
	

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

	/**
	 * Get the host name of the host to which the agent is deploying to.
	 * 
	 * @return
	 */
	public String getServerName() {
		return serverName;
	}

	/**
	 * Set the host name of the host to which the agent is deploying to.
	 * 
	 * @param serverName
	 */
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	/**
	 * Not used.
	 * 
	 * @return
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * Not used.
	 * 
	 * @param uuid
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * Whether start the web service automatically.
	 * 
	 * @return
	 */
	public boolean isAutoStartRRService() {
		return autoStartRRService;
	}

	/**
	 * Set whether start the web service automatically.
	 * 
	 * @param autoStartRRService
	 */
	public void setAutoStartRRService(boolean autoStartRRService) {
		this.autoStartRRService = autoStartRRService;
	}

	/**
	 * Get status of the deployment.
	 * 
	 * @return
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * Set status of the deployment.
	 * 
	 * @param status
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * Get percentage of the deployment.
	 * 
	 * @return
	 */
	public int getPercentage() {
		return percentage;
	}

	/**
	 * Set percentage of the deployment.
	 * 
	 * @param percentage
	 */
	public void setPercentage(int percentage) {
		this.percentage = percentage;
	}

	/**
	 * Get progress message of the deployment.
	 * 
	 * @return
	 */
	public String getProgressMessage() {
		return progressMessage;
	}

	/**
	 * Set progress message of the deployment.
	 * 
	 * @param progressMessage
	 */
	public void setProgressMessage(String progressMessage) {
		this.progressMessage = progressMessage;
	}

	/**
	 * Get message code of the deployment.
	 * 
	 * @return
	 */
	public long getMsgCode() {
		return msgCode;
	}

	/**
	 * Set message code of the deployment.
	 * 
	 * @param msgCode
	 */
	public void setMsgCode(long msgCode) {
		this.msgCode = msgCode;
	}
	
	/**
	 * 
	 * @param isSelected
	 */
	public void setSelected(boolean isSelected) {
		this.selected = isSelected;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Get the type of the product which is current being deployed. See
	 * {@link com.ca.arcserve.edge.app.base.webservice.contract.configuration.ProductType} for values.
	 * 
	 * @return
	 */
	public int getProductType() {
		return productType;
	}

	/**
	 * Set the type of the product which is current being deployed. See
	 * {@link com.ca.arcserve.edge.app.base.webservice.contract.configuration.ProductType} for values.
	 * 
	 * @param productType
	 */
	public void setProductType(int productType) {
		this.productType = productType;
	}

	/**
	 * Get warning message of the deployment.
	 * 
	 * @return
	 */
	public String getWarningMessage() {
		return warningMessage;
	}

	/**
	 * Set warning message of the deployment.
	 * 
	 * @param warningMessage
	 */
	public void setWarningMessage(String warningMessage) {
		this.warningMessage = warningMessage;
	}

	public Integer getTargetId() {
		return targetId;
	}

	public void setTargetId(Integer targetId) {
		this.targetId = targetId;
	}

	public int getTaskstatus() {
		return taskstatus;
	}

	public void setTaskstatus(int taskstatus) {
		this.taskstatus = taskstatus;
	}

	public String getFinalTitleMessage() {
		return finalTitleMessage;
	}

	public void setFinalTitleMessage(String finalTitleMessage) {
		this.finalTitleMessage = finalTitleMessage;
	}

	public String getFinalDetailMessage() {
		return finalDetailMessage;
	}

	public void setFinalDetailMessage(String finalDetailMessage) {
		this.finalDetailMessage = finalDetailMessage;
	}

	public String getPlanIds() {
		return planIds;
	}

	public void setPlanIds(String planIds) {
		this.planIds = planIds;
	}

	public String getConnectTime() {
		return connectTime;
	}

	public void setConnectTime(String connectTime) {
		this.connectTime = connectTime;
	}

	public boolean isCheckDestinationVersion() {
		return checkDestinationVersion;
	}

	public void setCheckDestinationVersion(boolean checkDestinationVersion) {
		this.checkDestinationVersion = checkDestinationVersion;
	}
}
