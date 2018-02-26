package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.ca.arcflash.rps.webservice.data.host.RpsHost;
import com.ca.arcflash.rps.webservice.data.policy.RPSReplicationSettings;
import com.ca.arcflash.webservice.data.PM.PreferencesConfiguration;
import com.ca.arcflash.webservice.data.archive.ArchiveConfiguration;
import com.ca.arcflash.webservice.data.archive2tape.ArchiveConfig;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.D2DConfiguration;
import com.ca.arcflash.webservice.data.export.ScheduledExportConfiguration;
import com.ca.arcflash.webservice.data.vsphere.VSphereBackupConfiguration;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Version;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DeployTargetDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.ProtectedResourceIdentifier;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.ArchiveToTapeSettingsWrapper.ArchiveToTapeSourceType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.FileCopySettingWrapper.FileCopySourceType;
import com.ca.arcserve.edge.app.msp.webservice.contract.MspReplicationDestination;

/**
 * This class contains the contents of a plan. It groups protection
 * configurations by tasks. But don't try to map these configurations to UI's
 * task configurations exactly. Defining some UI tasks may need to fill
 * information in several configuration here. Following is the basic information
 * contained in this class:
 * <p>
 * <dl>
 * 
 * <dt>Node list
 * <dd>Contains ID of the nodes the plan will be deployed to.
 * 
 * <dt>Backup configuration
 * <dd>Configuration for Windows based backup.
 * 
 * <dt>Archive configuration
 * <dd>Configuration for file copy task.
 * 
 * <dt>Scheduled export configuration
 * <dd>Configuration for copy recovery point task.
 * 
 * <dt>RPS configuration list
 * <dd>Configuration for backing up to RPS and replication tasks.
 * 
 * <dt>vSphere backup configuration
 * <dd>Configuration for host-based backup.
 * 
 * <dt>Linux backup configuration
 * <dd>Configuration for Linux backup.
 * 
 * <dt>Conversion configuration
 * <dd>Configuration for conversion, including conversion for RHA imported nodes.
 * 
 * <dt>Preferences
 * <dd>Preferences for UDP agent.
 * 
 * <dt>Deploy agent settings
 * <dd>Settings for deploying UDP agent when deploying plan.
 * 
 * </dl>
 * <p>
 * 
 * @author panbo01
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class UnifiedPolicy implements Serializable {

	private static final long serialVersionUID = -8708380044960100100L;
	
	@NonPlanContent
	private int id;
	
	@NonPlanContent
	private String uuid;
	
	@NonPlanContent
	private String name = "";
	
	@NonPlanContent
	private List<ProtectedResourceIdentifier> protectedResources = new ArrayList<ProtectedResourceIdentifier>();
	//private List<Integer> node=new ArrayList<Integer>();
	
	private boolean enable = true;

	@NonPlanContent
	private Version generatorVersion;
	@NonPlanContent
	private String oldVersion;
	@NonPlanContent
	private GatewayId gatewayId = GatewayId.INVALID_GATEWAY_ID;
	
	private List<Integer> orderList=new ArrayList<Integer>();
	private List<TaskType> taskList = new ArrayList<TaskType>();
	private BackupConfiguration backupConfiguration = null;
	private PreferencesConfiguration preferencesConfiguration = new PreferencesConfiguration();
	private List<FileCopySettingWrapper> fileCopySettingsWrapperList = new ArrayList<FileCopySettingWrapper>();
	private ArchiveConfiguration fileArchiveConfiguration;
	private ScheduledExportConfiguration exportConfiguration;
	private List<RPSPolicyWrapper> rpsPolices=new ArrayList<RPSPolicyWrapper>();
	private ConversionTask conversionConfiguration;
	
	private VSphereBackupConfiguration vSphereBackupConfiguration;
	
	private LinuxBackupSettings linuxBackupsetting;
	
	private List<ArchiveToTapeSettingsWrapper> archiveToTapeSettingsWrapperList=new ArrayList<ArchiveToTapeSettingsWrapper>();
	@NonPlanContent
	private DeployTargetDetail deployD2Dsetting;
	
	private RPSReplicationSettings mspServerReplicationSettings;
	
	private RpsHost mspServer;
	private MspReplicationDestination mspReplicationDestination;
	
	private List<PlanTask> planTaskList = new ArrayList<PlanTask>();
	
	/**
	 * Create a plan object who contains an empty plan.
	 */
	public UnifiedPolicy() {
	}
	
	/**
	 * Returns ID of the plan.
	 * 
	 * @return
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Set ID of the plan.
	 * 
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Get name of the plan.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the plan name.
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Get the version information of the UDP that generates the plan.
	 * 
	 * @return
	 */
	public Version getGeneratorVersion()
	{
		return generatorVersion;
	}

	/**
	 * Set the version information of the UDP that generates the plan.
	 * 
	 * @param generatorVersion
	 */
	public void setGeneratorVersion( Version generatorVersion )
	{
		this.generatorVersion = generatorVersion;
	}

	/**
	 * Get configuration for Windows based backup.
	 * 
	 * @return
	 */
	public BackupConfiguration getBackupConfiguration() {
		return backupConfiguration;
	}

	/**
	 * Set configuration for Windows based backup.
	 * 
	 * @param backupConfiguration
	 */
	public void setBackupConfiguration(BackupConfiguration backupConfiguration) {
		this.backupConfiguration = backupConfiguration;
	}

	/**
	 * Get preferences for UDP agent.
	 * 
	 * @return
	 */
	public PreferencesConfiguration getPreferencesConfiguration() {
		return preferencesConfiguration;
	}

	/**
	 * Set preferences for UDP agent.
	 * 
	 * @param preferencesConfiguration
	 */
	public void setPreferencesConfiguration(PreferencesConfiguration preferencesConfiguration) {
		this.preferencesConfiguration = preferencesConfiguration;
	}

	/**
	 * Get nodes the plan will be deployed to.
	 * 
	 * @return
	 */
//	public List<Integer> getNodes() {
//		return node;
//	}
//
	/**
	 * Set the list of nodes the plan will be deployed to.
	 * 
	 * @param nodes
	 */
//	public void setNodes(List<Integer> nodes) {
//		this.node = nodes;
//	}

	public List<ProtectedResourceIdentifier> getProtectedResources() {
		return protectedResources;
	}

	public void setProtectedResources(
			List<ProtectedResourceIdentifier> protectedResources) {
		this.protectedResources = protectedResources;
	}
	
	public List<FileCopySettingWrapper> getFileCopySettingsWrapper() {
		return fileCopySettingsWrapperList;
	}
	
	public void setFileCopySettingsWrapper(
			List<FileCopySettingWrapper> fileCopySettingsWrapper) {
		this.fileCopySettingsWrapperList = fileCopySettingsWrapper;
	}
	
	public ArchiveConfiguration getFileArchiveConfiguration() {
		return fileArchiveConfiguration;
	}
	
	public void setFileArchiveConfiguration(
			ArchiveConfiguration fileArchiveConfiguration) {
		this.fileArchiveConfiguration = fileArchiveConfiguration;
	}

	/**
	 * Get configuration for copy recovery point task.
	 * 
	 * @return
	 */
	public ScheduledExportConfiguration getExportConfiguration() {
		return exportConfiguration;
	}

	/**
	 * Set configuration for copy recovery point task.
	 * 
	 * @param exportConfiguration
	 */
	public void setExportConfiguration(
			ScheduledExportConfiguration exportConfiguration) {
		this.exportConfiguration = exportConfiguration;
	}
	
	/**
	 * Get configuration for conversion task.
	 * 
	 * @return
	 */
	public ConversionTask getConversionConfiguration() {
		return conversionConfiguration;
	}

	/**
	 * Set configuration for conversion task.
	 * 
	 * @param conversionConfiguration
	 */
	public void setConversionConfiguration(ConversionTask conversionConfiguration) {
		this.conversionConfiguration = conversionConfiguration;
	}
	
	/**
	 * Get configuration for host-based backup.
	 * 
	 * @return
	 */
	public VSphereBackupConfiguration getVSphereBackupConfiguration() {
		return vSphereBackupConfiguration;
	}
	
	/**
	 * Set configuration for host-based backup.
	 * 
	 * @param vsSphereBackupConfiguration
	 */
	public void setVSphereBackupConfiguration(VSphereBackupConfiguration vsSphereBackupConfiguration) {
		this.vSphereBackupConfiguration = vsSphereBackupConfiguration;
	}

	/**
	 * Get configuration for Linux backup.
	 * 
	 * @return
	 */
	public LinuxBackupSettings getLinuxBackupsetting() {
		return linuxBackupsetting;
	}

	/**
	 * Set configuration for Linux backup.
	 * 
	 * @param linuxBackupsetting
	 */
	public void setLinuxBackupsetting(LinuxBackupSettings linuxBackupsetting) {
		this.linuxBackupsetting = linuxBackupsetting;
	}
	
	public List<ArchiveToTapeSettingsWrapper> getArchiveToTapeSettingsWrapperList() {
	    return archiveToTapeSettingsWrapperList;
    }

    public void setArchiveToTapeSettingsWrapperList(List<ArchiveToTapeSettingsWrapper> archiveToTapeSettingsWrapperList) {
	    this.archiveToTapeSettingsWrapperList = archiveToTapeSettingsWrapperList;
    }

	/**
	 * Get RPS settings list. When backing up to RPS, the first settings in the
	 * list is for backup.
	 * 
	 * @return
	 */
	public List<RPSPolicyWrapper> getRpsPolices() {
		return rpsPolices;
	}

	/**
	 * Set RPS settings list. When backing up to RPS, the first settings in the
	 * list is for backup.
	 * 
	 * @param rpsPolices
	 */
	public void setRpsPolices(List<RPSPolicyWrapper> rpsPolices) {
		this.rpsPolices = rpsPolices;
	}

	/**
	 * This is for internal use.
	 * 
	 * @return
	 */
	public List<Integer> getOrderList() {
		return orderList;
	}

	/**
	 * This is for internal use.
	 * 
	 * @param orderList
	 */
	public void setOrderList(List<Integer> orderList) {
		this.orderList = orderList;
	}

	/**
	 * Get the tasks the plan contains. This is for internal use, and will be
	 * generated automatically.
	 * 
	 * @return
	 */
	public List<TaskType> getTaskList()
	{
		return taskList;
	}

	/**
	 * Set the tasks the plan contains. This is for internal use.
	 * 
	 * @param taskList
	 */
	public void setTaskList( List<TaskType> taskList )
	{
		this.taskList = taskList;
	}

	/**
	 * Get the settings for deploying UDP agent when deploying plan.
	 * 
	 * @return
	 */
	public DeployTargetDetail getDeployD2Dsetting() {
		return deployD2Dsetting;
	}

	/**
	 * Set the settings for deploying UDP agent when deploying plan.
	 * 
	 * @param deployD2Dsetting
	 */
	public void setDeployD2Dsetting(DeployTargetDetail deployD2Dsetting) {
		this.deployD2Dsetting = deployD2Dsetting;
	}

	/**
	 * Get UUID of the plan.
	 * 
	 * @return
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * Set the UUID of the plan.
	 * 
	 * @param uuid
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	/**
	 * Get settings for replication to MSP's server.
	 * 
	 * @return
	 */
	public RPSReplicationSettings getMspServerReplicationSettings() {
		return mspServerReplicationSettings;
	}

	/**
	 * Set settings for replication to MSP's server.
	 * 
	 * @param mspServerReplicationSettings
	 */
	public void setMspServerReplicationSettings(RPSReplicationSettings mspServerReplicationSettings) {
		this.mspServerReplicationSettings = mspServerReplicationSettings;
	}

	/**
	 * Get the information of the MSP server.
	 * 
	 * @return
	 */
	public RpsHost getMspServer() {
		return mspServer;
	}

	/**
	 * Set the information of the MSP server.
	 * 
	 * @param mspServer
	 */
	public void setMspServer(RpsHost mspServer) {
		this.mspServer = mspServer;
	}

	/**
	 * Get information for MSP replication destinaltion.
	 * 
	 * @return
	 */
	public MspReplicationDestination getMspReplicationDestination() {
		return mspReplicationDestination;
	}

	/**
	 * Set information for MSP replication destinaltion.
	 * 
	 * @return
	 */
	public void setMspReplicationDestination(MspReplicationDestination mspReplicationDestination) {
		this.mspReplicationDestination = mspReplicationDestination;
	}

	/**
	 * Check if the plan is enabled.
	 * 
	 * @return
	 */
	public boolean isEnable() {
		return enable;
	}

	/**
	 * Set the plan to enabled or disabled.
	 * 
	 * @param enable
	 */
	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public GatewayId getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(GatewayId gatewayId) {
		if (gatewayId == null)
			gatewayId = GatewayId.INVALID_GATEWAY_ID;
		this.gatewayId = gatewayId;
	}

	public D2DConfiguration toD2DConfiguration() {
		D2DConfiguration configuration = new D2DConfiguration();
		
		configuration.setBackupConfiguration(backupConfiguration);
		
		if(fileCopySettingsWrapperList != null){
			for (FileCopySettingWrapper fileCopySettingsWrapper : fileCopySettingsWrapperList) {
				if(fileCopySettingsWrapper.getFileCopySourceType() == FileCopySourceType.BackUp){
					configuration.setArchiveConfiguration(fileCopySettingsWrapper.getArchiveConfiguration());
				}
			}
		}
//		configuration.setArchiveConfiguration(fileCopyConfiguration);
		configuration.setArchiveDelConfiguration(fileArchiveConfiguration);
		configuration.setScheduledExportConfiguration(exportConfiguration);
		configuration.setPreferencesConfiguration(preferencesConfiguration);
		
		if (archiveToTapeSettingsWrapperList != null) {
			ArchiveConfig config = new ArchiveConfig();
			for(ArchiveToTapeSettingsWrapper archiveToTapeWrapper : archiveToTapeSettingsWrapperList){
				if(archiveToTapeWrapper.getArchiveToTapeSourceType() == ArchiveToTapeSourceType.BackUp){
					config.setSource(archiveToTapeWrapper.getArchiveToTapeSettings().getArchiveConfig().getSource());
					break;
				}
			}
			configuration.setArchiveToTapeConfig(config);	
		}
		
		return configuration;
	}

	public List<PlanTask> getPlanTaskList() {
		return planTaskList;
	}

	public void setPlanTaskList(List<PlanTask> taskList) {
		this.planTaskList = taskList;
	}
	
	/**
	 * For internal use only.
	 */
	public void generateOrderList()
	{
		assert taskList != null : "Task list is null. Generate task list first.";
		if (taskList == null)
			return;
		
		if (orderList == null)
			orderList = new ArrayList<Integer>();
		orderList.clear();
		
		boolean hasLinuxBackupTask = hasLinuxBackupTask();
		for (TaskType taskType : taskList)
		{
			TaskOrder taskOrder = taskType2TaskOrder( taskType, hasLinuxBackupTask );
			if (taskOrder != null)
				orderList.add( taskOrder.getValue() );
		}
	}
	
	/**
	 * For internal use only.
	 * 
	 * @return
	 */
	public boolean hasLinuxBackupTask()
	{
		assert taskList != null : "Task list is null. Generate task list first.";
		if (taskList == null)
			return false;
		
		for (TaskType taskType : taskList)
		{
			if (taskType == TaskType.LinuxBackUP)
				return true;
		}
		
		return false;
	}
	
	private static TaskOrder taskType2TaskOrder( TaskType taskType, boolean hasLinuxBackupTask )
	{
		if(taskType == null)
			return null;
		
		TaskOrder taskOrder = null;
		
		if(taskType==TaskType.BackUP || taskType==TaskType.VSphereBackUP || taskType == TaskType.LinuxBackUP){//Backup
			return TaskOrder.BackUP;
		} else if (taskType == TaskType.Replication) {//replication
			return TaskOrder.Replication;
		} else if (taskType.isConversionTask()) {// conversion
			return TaskOrder.Conversion;
		}else if (taskType == TaskType.AgentInstallation && !hasLinuxBackupTask) {
			return TaskOrder.AgentInstallation;
		}else if (taskType == TaskType.FileCopy) {
			return TaskOrder.FileCopy;
		}else if (taskType == TaskType.FILE_ARCHIVE) {
			return TaskOrder.FILE_ARCHIVE;
		}else if (taskType == TaskType.MspServerReplication) {
			return TaskOrder.Replication;
		} else if (taskType == TaskType.CopyRecoveryPoints) {
			return TaskOrder.CopyRecoveryPoints;
		}else if (taskType == TaskType.MspClientReplication) {
			return TaskOrder.MspClientReplication;
		}else if(taskType == TaskType.ArchiveToTape){
			return TaskOrder.ArchiveToTape;
		}
		
		return taskOrder;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append( "Plan brief information:" );
		
		sb.append( "\n    Plan ID:                      " + this.getId() );
		sb.append( "\n    Plan name:                    " + this.getName() );
		sb.append( "\n    Is enabled:                   " + this.isEnable() );
		sb.append( "\n    BackupConfiguration:          " + this.getBackupConfiguration() );
		sb.append( "\n    PreferencesConfiguration:     " + this.getPreferencesConfiguration() );
		sb.append( "\n    FileCopyConfiguration:        " + this.getFileCopySettingsWrapper() );
		sb.append( "\n    FileArchiveConfiguration:     " + this.getFileArchiveConfiguration() );
		sb.append( "\n    ScheduledExportConfiguration: " + this.getExportConfiguration() );
		sb.append( "\n    RPS policy count:             " + ((this.getRpsPolices() == null) ? "null" : this.getRpsPolices().size()) );
		sb.append( "\n    ConversionTask:               " + this.getConversionConfiguration() );
		sb.append( "\n    VSphereBackupConfiguration:   " + this.getVSphereBackupConfiguration() );
		sb.append( "\n    LinuxBackupSettings:          " + this.getLinuxBackupsetting() );
		sb.append( "\n    DeployTargetDetail:           " + this.getDeployD2Dsetting() );
		sb.append( "\n    RPSReplicationSettings:       " + this.getMspServerReplicationSettings() );
		sb.append( "\n    RpsHost:                      " + this.getMspServer() );
		sb.append( "\n    MspReplicationDestination:    " + this.getMspReplicationDestination() );
		
		return sb.toString();
	}

	public String getOldVersion() {
		return oldVersion;
	}

	public void setOldVersion(String oldVersion) {
		this.oldVersion = oldVersion;
	}

}
