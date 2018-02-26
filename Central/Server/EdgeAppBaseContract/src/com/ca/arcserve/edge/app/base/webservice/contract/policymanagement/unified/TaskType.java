package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified;

/**
 * The type of plan tasks and will be used in {@link UnifiedPolicy}.taskList to
 * indicate what tasks are added to the plan by user, and the order of the
 * tasks as well.
 */
/* 
 * This was defined by Ziming (zhazi01) in UI client originally. When I was
 * working on third-party APIs, I moved it to contract project.
 * 
 * panbo01
 * 2014-08-05
 */
public enum TaskType
{
	Unknown,
	
	/**
	 * Backup: Agent-Based Windows
	 */
	BackUP,
	/**
	 * Replicate
	 */
	Replication,
	/**
	 * Virtual Standby
	 */
	Conversion,
	
	/**
	 * Not used
	 */
	Synchronize,
	
	/**
	 * Not used
	 */
	LaunchAnotherPlan,
	/**
	 * Backup: Host-Based Agentless
	 */
	VSphereBackUP,
	/**
	 * Product installation
	 */
	AgentInstallation,
	/**
	 * Backup: Agent-Based Linux
	 */
	LinuxBackUP,
	/**
	 * File Copy
	 */
	FileCopy,
	/**
	 * Replicate from a remote RPS
	 */
	MspServerReplication,
	/**
	 * Replicate to a remotely-managed RPS
	 */
	MspClientReplication,
	/**
	 * Copy Recovery Points
	 */
	CopyRecoveryPoints,
	/**
	 * Virtual Standby
	 */
	RemoteConversion,
	/**
	 * Virtual Standby
	 */
	RemoteConversionForRHA,
	ArchiveToTape,
	FILE_ARCHIVE;
	
	/**
	 * Parse an integer value to TaskType.
	 * 
	 * @param	ordinal
	 * 			The integer value.
	 * @return	The corresponding task type.
	 */
	public static TaskType parse(int ordinal) {
		for (TaskType taskType : TaskType.values()) {
			if (ordinal == taskType.ordinal()) {
				return taskType;
			}
		}
		return Unknown;
	}

	/**
	 * Check if the task is a conversion task.
	 * 
	 * @return	true if yes, otherwise false.
	 */
	public boolean isConversionTask() {
		return TaskType.Conversion == this || this.isRemoteConversionTask();
	}

	/**
	 * Check if the task is a remote conversion task.
	 * 
	 * @return	true if yes, otherwise false.
	 */
	public boolean isRemoteConversionTask() {
		return (TaskType.RemoteConversion == this || TaskType.RemoteConversionForRHA == this);
	}
}
