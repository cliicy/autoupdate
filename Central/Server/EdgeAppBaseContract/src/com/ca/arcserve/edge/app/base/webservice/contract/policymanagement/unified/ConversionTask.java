package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.node.HostConnectInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyTypes;

public class ConversionTask implements Serializable {
	private static final long serialVersionUID = 7324148852685979661L;
	private int taskType = PolicyTypes.VCM;
	private HostConnectInfo converter;
	private int sourceTaskId;
	private boolean sourceTaskRemoteReplicate = false;
	private JobScriptCombo4Wan conversionJobScript = new JobScriptCombo4Wan();
	
	/**
	 * Get connection information of the converter.
	 * 
	 * @return
	 */
	public HostConnectInfo getConverter() {
		return converter;
	}

	/**
	 * Set connection information of the converter.
	 * 
	 * @param converter
	 */
	public void setConverter(HostConnectInfo converter) {
		this.converter = converter;
	}

	/**
	 * Get job script, the configuration, of virtual standby job.
	 * 
	 * @return
	 */
	public JobScriptCombo4Wan getConversionJobScript() {
		return conversionJobScript;
	}

	/**
	 * Set job script, the configuration, of virtual standby job.
	 * 
	 * @param conversionJobScript
	 */
	public void setConversionJobScript(JobScriptCombo4Wan conversionJobScript) {
		this.conversionJobScript = conversionJobScript;
	}

	/**
	 * Get virtual standby type. Available values are listed below. These
	 * values are defined in {@link com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyTypes}.
	 * <p>
	 * <ul>
	 * <li>2: VCM, local conversion
	 * <li>4: RemoteVCM, remote conversion
	 * <li>8: RemoteVCMForRHA, remote conversion
	 * </ul>
	 * 
	 * @return
	 */
	public int getTaskType() {
		return taskType;
	}

	/**
	 * Set virtual standby type. Available values are listed below. These
	 * values are defined in {@link com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyTypes}.
	 * <p>
	 * <ul>
	 * <li>2: VCM, local conversion
	 * <li>4: RemoteVCM, remote conversion
	 * <li>8: RemoteVCMForRHA, remote conversion
	 * </ul>
	 * 
	 * @param taskType
	 */
	public void setTaskType(int taskType) {
		this.taskType = taskType;
	}

	/**
	 * Get index of task from which the source of virtual standby is retrieved.
	 * 
	 * @return
	 */
	public int getSourceTaskId() {
		return sourceTaskId;
	}

	/**
	 * Set index of task from which the source of virtual standby is retrieved.
	 * 
	 * @param sourceTaskId
	 */
	public void setSourceTaskId(int sourceTaskId) {
		this.sourceTaskId = sourceTaskId;
	}

	/**
	 *  Set whether source Task is remote replication task (Cross site replication task)
	 *  this value is valid when TaskType==2: VCM, local conversion
	 * @param sourceTaskIsRemoteReplicate
	 */
	public boolean isSourceTaskRemoteReplicate() {
		return sourceTaskRemoteReplicate;
	}

	/**
	 * Set whether source Task is remote replication task (Cross site replication task)
	 * this value is valid when TaskType==2: VCM, local conversion
	 * @param sourceTaskIsRemoteReplicate
	 */
	public void setSourceTaskRemoteReplicate(boolean sourceTaskRemoteReplicate) {
		this.sourceTaskRemoteReplicate = sourceTaskRemoteReplicate;
	}	
	
}
