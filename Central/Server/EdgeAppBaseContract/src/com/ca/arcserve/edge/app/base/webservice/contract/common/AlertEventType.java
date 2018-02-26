package com.ca.arcserve.edge.app.base.webservice.contract.common;

import java.io.Serializable;

import com.ca.arcflash.webservice.constants.JobStatus;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.edge.email.CommonEmailInformation;


/**
 * we place this class in EdgeBaseService; because it used by AlertManager class when insert Alert data;
 * Alert Report Event Type aggregate job status and Event Type  from com.ca.arcflash.webservice.constants.JobStatus and com.ca.arcflash.webservice.edge.email.CommonEmailInformation 
 * it is not a simple map from JobStatus/Event Type to UI; it has a independent classify standard;
 */
public enum AlertEventType {
	// reference classification in ActivityLogMsgUtil; but not totally same;
	NONE(-1),
	All(0),
	///job related;            ////mapped Event Type and JobType in agent ; 
	BackUpJob_ForAlert( 0x1 ),  //JOBTYPE_BACKUP; JOBTYPE_VM_BACKUP;
						//// also map to event  VSPHERE_LICENSE_FAIL(10004),VSPHERE_HOST_NOT_FOUND(10005),VSPHERE_DATASTROE_NOT_ENOUGH(10006) when JobType = JOBTYPE_VM_BACKUP;         these events equals to status = JobStatus.JOBSTATUS_FAILED:
					   ///map to VSPHERE_MERGE_JOBQUEUE(10016) when JobType = JOBTYPE_VM_BACKUP;	   these events equals to status = JobStatus.JOBSTATUS_SKIPPED:			
	
	RestoreJob_ForAlert(0x2),       //JOBTYPE_RESTORE; JOBTYPE_FILECOPY_RESTORE;JOBTYPE_VM_RECOVERY
	CopyRecoveryPointJob_ForAlert(0x3),      ///JOBTYPE_COPY means Copy recovery point job.
	FileCopyJob_ForAlert(0x4),       ///JOBTYPE_FILECOPY_BACKUP;JOBTYPE_FILECOPY_PURGE;JOBTYPE_FILECOPY_CATALOGSYNC; 
	CatalogJob_ForAlert(0x5),        ///JOBTYPE_CATALOG_FS;JOBTYPE_CATALOG_APP;JOBTYPE_CATALOG_GRT;JOBTYPE_CATALOG_FS_ONDEMAND;JOBTYPE_VM_CATALOG_FS_ONDEMAND;JOBTYPE_FILECOPY_CATALOGSYNC; JOBTYPE_VM_CATALOG_FS
	
	Merge_ForAlert(0x6),    ////JOBTYPE_MERGE;JOBTYPE_VM_MERGE;JOBTYPE_RPS_MERGE;
							///map to VSPHERE_MERGE_JOBQUEUE(10016) when JobType = JOBTYPE_VM_MERGE;     these events equals to status = JobStatus.JOBSTATUS_SKIPPED:		
	VCMReplicate_ForAlert(0x7),   ///JOBTYPE_CONVERSION and 
					            // map to event and equals to status = JobStatus.JOBSTATUS_FAILED: VCM_LICENSEFAILED(10010) | VCM_CONVERSIONFAILED(10011)| VCM_VMHOSTNOTREACHABLE(10012)| VCM_FAILOVERFAILED(10015),;
								// map to event and equals to status = JobStatusJOB.JOBSTATUS_FINISHED ;VCM_CONVERSIONSUCCESS(10014)   VCM_AUTOFAIOVER(10007) | VCM_MAUALFAIOVER(10008) 

	ReplicationJob_ForAlert(0x8), //	JOBTYPE_RPS_REPLICATE = 22; JOBTYPE_RPS_REPLICATE_IN_BOUND = 24;
	/////below event type not have correlated job type except vcm related event;
	///resource related;
	SRM_ALERT( 0x21 ),    /// CommonEmailInformation.EVENT_TYPE.SRM_ALERT.getValue()
	DISK_ALERT( 0x22 ),  /// CommonEmailInformation.EVENT_TYPE.DISK_ALERT.getValue()   ; JobType=  VCM_REPLICATIONSPACEWARNING(10009) 

	//cpm related:
	CPM_DISCOVERY_EVENT( 0x31 ),  /// CommonEmailInformation.EVENT_TYPE.CPM_DISCOVERY_EVENT.getValue()
	CPM_POLICY_COMMON_FAIL( 0x32 ),  //// CommonEmailInformation.EVENT_TYPE.CPM_POLICY_COMMON_FAIL.getValue()
	
	
	////other:
	AGENT_RPS_UPDTE_AVAILABLE( 0x41 ),   //// AGENT_RPS_UPDTE_AVAILABLE(10020)
	SYNC_ALERT ( 0x42 ),  /// CommonEmailInformation.EVENT_TYPE.SYNC_ALERT.getValue() 
	VCM_Monitor( 0x43 ),  /////JOBTYPE_CONVERSION and event type =  VCM_MISSHEATBEAT(10013);
	UDP_GATEWAY_EVENT( 0x44); /// CommonEmailInformation.EVENT_TYPE.UDP_GATEWAY_EVENT.getValue()
	private int value;
	
	private AlertEventType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return this.value;
	}
	/**
	 * 1.raw event type may contain job status or EventType 
	 * 2.this classification is not totally same as classification in ActivityLogMsgUtil;
	 * 3.default value for JobStatus/JobType = -1;  see com.ca.arcflash.webservice.util.EmailSender
	*/
	public static void generateEventType_JobStatusFromRawEvent( long rawEventType, long jobType, Holder<Integer> overAllEventType, Holder<Integer> jobStatus  ) {

		if( rawEventType >JobStatus.JOBSTATUS_MISSED  ) { // raw event;
			jobStatus.value = -1 ;
			if( jobType == JobType.JOBTYPE_VM_BACKUP &&
					( rawEventType == CommonEmailInformation.EVENT_TYPE.VSPHERE_LICENSE_FAIL.getValue()  ///vsphere
					|| rawEventType == CommonEmailInformation.EVENT_TYPE.VSPHERE_HOST_NOT_FOUND.getValue() 
					|| rawEventType == CommonEmailInformation.EVENT_TYPE.VSPHERE_DATASTROE_NOT_ENOUGH.getValue() ) ) {
				overAllEventType.value =  BackUpJob_ForAlert.value ;
				jobStatus.value = JobStatus.JOBSTATUS_FAILED ;
			}
			else if( jobType == JobType.JOBTYPE_VM_BACKUP && rawEventType == CommonEmailInformation.EVENT_TYPE.VSPHERE_MERGE_JOBQUEUE.getValue() ) {
				overAllEventType.value =  BackUpJob_ForAlert.value;
				jobStatus.value = JobStatus.JOBSTATUS_SKIPPED ;
			}
			else if( jobType == JobType.JOBTYPE_VM_MERGE && rawEventType == CommonEmailInformation.EVENT_TYPE.VSPHERE_MERGE_JOBQUEUE.getValue() ) {
				overAllEventType.value =   Merge_ForAlert.value;
				jobStatus.value =  JobStatus.JOBSTATUS_SKIPPED;
			}
			else if( jobType == JobType.JOBTYPE_CONVERSION &&
					( rawEventType ==  CommonEmailInformation.EVENT_TYPE.VCM_LICENSEFAILED.getValue() 
					|| rawEventType ==  CommonEmailInformation.EVENT_TYPE.VCM_CONVERSIONFAILED.getValue()
					|| rawEventType ==  CommonEmailInformation.EVENT_TYPE.VCM_VMHOSTNOTREACHABLE.getValue()
					|| rawEventType ==  CommonEmailInformation.EVENT_TYPE.VCM_FAILOVERFAILED.getValue()
					)) {
				overAllEventType.value =  VCMReplicate_ForAlert.value;
				jobStatus.value =JobStatus.JOBSTATUS_FAILED ;
			}
			else if( jobType == JobType.JOBTYPE_CONVERSION && 
					(
						rawEventType ==  CommonEmailInformation.EVENT_TYPE.VCM_CONVERSIONSUCCESS.getValue() 
						|| rawEventType == CommonEmailInformation.EVENT_TYPE.VCM_AUTOFAIOVER.getValue() 
					    || rawEventType == CommonEmailInformation.EVENT_TYPE.VCM_MAUALFAIOVER.getValue()
					)) {
				overAllEventType.value =  VCMReplicate_ForAlert.value;
				jobStatus.value =JobStatus.JOBSTATUS_FINISHED;
			}
			////no job event:
			//resource
			else if( rawEventType == CommonEmailInformation.EVENT_TYPE.SRM_ALERT.getValue() ) {
				overAllEventType.value =  SRM_ALERT.value;
			}
			else if( rawEventType == CommonEmailInformation.EVENT_TYPE.DISK_ALERT.getValue() || 
					rawEventType == CommonEmailInformation.EVENT_TYPE.VCM_REPLICATIONSPACEWARNING.getValue() ) {
				overAllEventType.value =  DISK_ALERT.value;
			}
			//cpm;
			else if( rawEventType == CommonEmailInformation.EVENT_TYPE.CPM_DISCOVERY_EVENT.getValue() ) {
				overAllEventType.value =  CPM_DISCOVERY_EVENT.value;
			}
			else if( rawEventType == CommonEmailInformation.EVENT_TYPE.CPM_POLICY_COMMON_FAIL.getValue() ) {
				overAllEventType.value =  CPM_POLICY_COMMON_FAIL.value;
			}
			//other
			else if( rawEventType == CommonEmailInformation.EVENT_TYPE.AGENT_RPS_UPDTE_AVAILABLE.getValue() ) {
				overAllEventType.value =   AGENT_RPS_UPDTE_AVAILABLE.value;
			}
			else if( rawEventType == CommonEmailInformation.EVENT_TYPE.SYNC_ALERT.getValue() ) {
				overAllEventType.value =  SYNC_ALERT.value;
			}
			else if(  rawEventType == CommonEmailInformation.EVENT_TYPE.VCM_MISSHEATBEAT.getValue() ) {
				overAllEventType.value =  VCM_Monitor.value;
			}
			else if( rawEventType == CommonEmailInformation.EVENT_TYPE.UDP_GATEWAY_EVENT.getValue() ) {
				overAllEventType.value =  UDP_GATEWAY_EVENT.value;
			}
			else { 
				overAllEventType.value =  NONE.value;
			}
		}
	
		///raw event type <= JobStatus.JOBSTATUS_MISSED  means job status
		else if( rawEventType <= JobStatus.JOBSTATUS_MISSED  && rawEventType >= JobStatus.JOBSTATUS_ACTIVE ){
			jobStatus.value = (int)rawEventType;
			if( jobType == JobType.JOBTYPE_BACKUP || jobType == JobType.JOBTYPE_VM_BACKUP ) {
				overAllEventType.value =  BackUpJob_ForAlert.value;
			} 
			else if ( jobType ==  JobType.JOBTYPE_RESTORE || jobType ==  JobType.JOBTYPE_VM_RECOVERY || jobType == JobType.JOBTYPE_FILECOPY_RESTORE ) {
				overAllEventType.value =  RestoreJob_ForAlert.value;
			}
			else if ( jobType == JobType.JOBTYPE_COPY ) {
				overAllEventType.value =  CopyRecoveryPointJob_ForAlert.value;
			}
			else if(  jobType == JobType.JOBTYPE_FILECOPY_SOURCEDELETE || jobType == JobType.JOBTYPE_FILECOPY_BACKUP || jobType == JobType.JOBTYPE_FILECOPY_PURGE || jobType == JobType.JOBTYPE_FILECOPY_CATALOGSYNC ) {
				overAllEventType.value = FileCopyJob_ForAlert.value;
			}
			else if( jobType == JobType.JOBTYPE_CATALOG_FS || jobType == JobType.JOBTYPE_CATALOG_APP||
						jobType == JobType.JOBTYPE_CATALOG_GRT || jobType == JobType.JOBTYPE_CATALOG_FS_ONDEMAND|| 
						jobType == JobType.JOBTYPE_VM_CATALOG_FS ||
						jobType == JobType.JOBTYPE_VM_CATALOG_FS_ONDEMAND || jobType == JobType.JOBTYPE_FILECOPY_CATALOGSYNC ) {
				overAllEventType.value =  CatalogJob_ForAlert.value;
			}
			else if( jobType == JobType.JOBTYPE_RPS_MERGE || jobType == JobType.JOBTYPE_MERGE || jobType == JobType.JOBTYPE_VM_MERGE ) {
				overAllEventType.value =  Merge_ForAlert.value;
			}
			///actually ;not exist; all VCM rawEventType are EventType ;not job status
			else if( jobType == JobType.JOBTYPE_CONVERSION || jobType == JobType.JOBTYPE_RPS_CONVERSION ) { 
				overAllEventType.value =  VCMReplicate_ForAlert.value;
			}
			else if( jobType== JobType.JOBTYPE_RPS_REPLICATE ||  jobType== JobType.JOBTYPE_RPS_REPLICATE_IN_BOUND 
					|| jobType == JobType.JOBTYPE_RPS_DATA_SEEDING || jobType == JobType.JOBTYPE_RPS_DATA_SEEDING_IN) {
				overAllEventType.value = ReplicationJob_ForAlert.value;
			}
			else { 
				overAllEventType.value = NONE.value;
			}
		}
		else { ///raw event type =0; error!!
			overAllEventType.value = NONE.value;
		}
	}
	public static enum AlertEventGroup {
		NONE, Job, CPM, Resource, Other;
		public static AlertEventGroup getGroupFromEventType( AlertEventType type ) {
			if( type.getValue()>= BackUpJob_ForAlert.getValue() && type.getValue()<0x20 ){
				return Job;
			}
			else if(  type.getValue()>0x20 &&  type.getValue()<0x30 ) {
				return Resource;
			}
			else if(  type.getValue()>0x30 &&  type.getValue()<0x40 ) {
				return CPM;
					}
			else if(  type.getValue()>0x40 ) {
				return  Other;
			}
			else {
				return NONE;
			}
		}
	}
	/**
	 * 
	 * @author fanda03
	 *cannot direct use holder in Javax; because this class is used in UI; GWT cannot compile the Holder class in java which beyond gwt committed java packages.
	 * @param <T>
	 */
	public static final class Holder<T> implements Serializable {

	    private static final long serialVersionUID = 2623699057546497185L;

	    /**
	     * The value contained in the holder.
	     */
	    public T value;

	    /**
	     * Creates a new holder with a <code>null</code> value.
	     */
	    public Holder() {
	    }

	    /**
	     * Create a new holder with the specified value.
	     *
	     * @param value The value to be stored in the holder.
	     */
	    public Holder(T value) {
	        this.value = value;
	    }
	}
	
	public static AlertEventType parseInt(int over_all_event_type) {
		switch (over_all_event_type) {
		case 0:
			return All;
		case 0x1:
			return BackUpJob_ForAlert;
		case 0x2:
			return RestoreJob_ForAlert;
		case 0x3:
			return CopyRecoveryPointJob_ForAlert;
		case 0x4:
			return FileCopyJob_ForAlert;
		case 0x5:
			return CatalogJob_ForAlert;
		case 0x6:
			return Merge_ForAlert;
		case 0x7:
			return VCMReplicate_ForAlert;
		case 0x8:
			return ReplicationJob_ForAlert;
		case 0x21:
			return SRM_ALERT;	
		case 0x22:
			return DISK_ALERT;
		case 0x31:
			return CPM_DISCOVERY_EVENT;
		case 0x32:
			return CPM_POLICY_COMMON_FAIL;
	
		case 0x41:
			return AGENT_RPS_UPDTE_AVAILABLE;
		case 0x42:
			return SYNC_ALERT;
		case 0x43:
			return VCM_Monitor;
		case 0x44:
			return UDP_GATEWAY_EVENT;
		default:
			 return NONE;
		}
	}

}
