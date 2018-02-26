package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified;

/**
 * This enum was used in UnifiedPolicy.orderList. It was defined in WizardPolicyUtil.java
 * by Ziming as a private enum. I moved it to contract when creating the third-party
 * APIs.<br><br>
 * 
 * Originally, UI will populate a task list according to what user done when editing
 * the plan. The task list uses TaskType to describe each task. The the UI server
 * translate the task list to a order list, which is using this enum, and then the
 * order list will be saved into database along with policy.<br><br>
 * 
 * This is unnecessary and it also discarded useful information, such as it translates
 * all backups into on value, then it's difficult for us to figure out what kind of
 * backup the user chosen.<br><br>
 * 
 * @author panbo01
 *
 */
public enum TaskOrder
{
	BackUP(11),
	Replication(12),
	Conversion(13),
	AgentInstallation(14),
	FileCopy(15),
	CopyRecoveryPoints(16),
	MspClientReplication(17),
	ArchiveToTape(18),
	FILE_ARCHIVE(19);
	
	final int value;
	TaskOrder(int v){
		value=v;
	}
	
	public int getValue(){
		return value;
	}
}
