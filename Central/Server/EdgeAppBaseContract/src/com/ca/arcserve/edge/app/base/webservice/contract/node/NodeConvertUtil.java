package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.edge.app.base.webservice.contract.common.ApplicationUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeSortOrder;
import com.ca.arcserve.edge.app.base.webservice.contract.common.HostTypeUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.SortablePagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.ArcserveInfoSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.ConverterSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.D2DInfoSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.GatewaySummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.JobSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.NodeEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.NodeSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.NodeVcloudSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.PlanSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.RemoteDeployInfoSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.VmInfoSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.VsbSummary;
import com.ca.arcserve.edge.app.base.webservice.contract.node.filter.BitmapFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.node.filter.CommonNodeFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.node.filter.NodeFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.node.filter.NodeFilter.NodeFilterType;

public class NodeConvertUtil {
	
	public static List<Node> getNodeListByNodeEntityList(List<NodeEntity> nodeEntities){
		if(nodeEntities == null)
			return null;
		List<Node> nodes = new ArrayList<Node>();
		for (NodeEntity nodeEntity : nodeEntities) {
			Node node = NodeConvertUtil.getNodeByNodeEntity(nodeEntity);
			nodes.add(node);
		}
		return nodes;
	}
	
	public static Node getNodeByNodeEntity(NodeEntity nodeEntity){
		Node node = new Node();
		
		if(nodeEntity.getArcserveInfoSummary() != null){
			node.setArcserveBackupVersion(nodeEntity.getArcserveInfoSummary().getArcserveBackupVersion());
			node.setArcserveManaged(nodeEntity.getArcserveInfoSummary().getArcserveManagedStatus());
			node.setArcservePort(String.valueOf(nodeEntity.getArcserveInfoSummary().getArcservePort()));
			node.setArcserveProtocol(nodeEntity.getArcserveInfoSummary().getArcserveProtocol());
			node.setArcserveType(nodeEntity.getArcserveInfoSummary().getArcserveServerType());
			node.setAsbuLastUpdateTime(nodeEntity.getArcserveInfoSummary().getAsbuLastUpdateTime());
			node.setAsbuLastUpdateWarning(nodeEntity.getArcserveInfoSummary().isAsbuLastUpdateWarning());
			node.setAsbuSyncFrequency(nodeEntity.getArcserveInfoSummary().getAsbuSyncFrequency());
			node.setGDBId(nodeEntity.getArcserveInfoSummary().getGdbId());
			
			NodeSyncStatus arcSyncStatus = new NodeSyncStatus();
			arcSyncStatus.setStatus(nodeEntity.getArcserveInfoSummary().getArcSyncStatus());
			arcSyncStatus.setChangeStatus(nodeEntity.getArcserveInfoSummary().getArcSyncChangeStatus());
			node.setSyncStatus(arcSyncStatus);
			
		}
		
		if(nodeEntity.getConverterSummary() != null){
			node.setConverter(nodeEntity.getConverterSummary().getConverter());
			node.setConverterId(nodeEntity.getConverterSummary().getConverterId());
			node.setConverterPassword(nodeEntity.getConverterSummary().getConverterPassword());
			node.setConverterPort(nodeEntity.getConverterSummary().getConverterPort());
			node.setConverterProtocol(nodeEntity.getConverterSummary().getConverterProtocol());
			node.setConverterUsername(nodeEntity.getConverterSummary().getConverterUsername());
		}
		
		if(nodeEntity.getD2dInfoSummary() != null){
			node.setAuthUUID(nodeEntity.getD2dInfoSummary().getAuthUuid());
			
			NodeBkpStatus bkpStatus = new NodeBkpStatus();
			bkpStatus.setD2dStatus(nodeEntity.getD2dInfoSummary().getStatus());
			node.setBkpStatus(bkpStatus);
			
			node.setD2dBuildnumber(nodeEntity.getD2dInfoSummary().getBuildNumber());
			node.setD2dLastUpdateTime(nodeEntity.getD2dInfoSummary().getD2dLastUpdateTime());
			node.setD2dLastUpdateWarning(nodeEntity.getD2dInfoSummary().isD2dLastUpdateWarning());
			node.setD2DMajorversion(nodeEntity.getD2dInfoSummary().getMajorVersion());
			node.setD2dManaged(nodeEntity.getD2dInfoSummary().getManagedStatus());
			node.setD2dMinorversion(nodeEntity.getD2dInfoSummary().getMinorVersion());
			node.setD2dPort(String.valueOf(nodeEntity.getD2dInfoSummary().getPort()));
			node.setD2dProtocol(nodeEntity.getD2dInfoSummary().getProtocol());
			node.setD2dSyncFrequency(nodeEntity.getD2dInfoSummary().getD2dSyncFrequency());
			node.setD2dUpdateversionnumber(nodeEntity.getD2dInfoSummary().getUpdateNumber());
			node.setD2DUUID(nodeEntity.getD2dInfoSummary().getUuid());
			node.setPassword(nodeEntity.getD2dInfoSummary().getPassword());
			node.setRpsManagedStatus(nodeEntity.getD2dInfoSummary().getRpsManagedStatus());
			node.setUsername(nodeEntity.getD2dInfoSummary().getUsername());
		}
		
		if(nodeEntity.getGatewaySummary() != null){
			node.setGatewayId(new GatewayId(nodeEntity.getGatewaySummary().getGatewayId()));
			node.setSiteName(nodeEntity.getGatewaySummary().getSiteName());
			node.setLocalSite(nodeEntity.getGatewaySummary().getIsLocal()!=0);
		}
		if(nodeEntity.getJobSummary() != null){
			node.setD2dLastBackupJobStatus(JobStatus.parse(nodeEntity.getJobSummary().getLastBackupJobStatus()));
			node.setD2dLastBackupStartTime(nodeEntity.getJobSummary().getLastBackupJobTime());
			node.setJobRunning(nodeEntity.getJobSummary().isJobRunning());
			node.setLstJobHistory(nodeEntity.getJobSummary().getLatestJobHistories());
			node.setWaitingJobRunning(nodeEntity.getJobSummary().isWaittingJobToRun());
		}
		
		if(nodeEntity.getNodeSummary() != null){
			node.setAppStatus(nodeEntity.getNodeSummary().getAppStatus());
			node.setArcserveInstalled(ApplicationUtil.isArcserveInstalled(nodeEntity.getNodeSummary().getAppStatus()));
			node.setConsoleInstalled(ApplicationUtil.isConsoleInstalled(nodeEntity.getNodeSummary().getAppStatus()));
			node.setD2dInstalled(ApplicationUtil.isD2DInstalled(nodeEntity.getNodeSummary().getAppStatus()));
			node.setDomainName(nodeEntity.getNodeSummary().getDomainName());
			node.setExchangeInstalled(ApplicationUtil.isExchangeInstalled(nodeEntity.getNodeSummary().getAppStatus()));
			node.setHasVCMMonitorFlag(HostTypeUtil.isVCMMonitor(nodeEntity.getNodeSummary().getHostType()));
			node.setHostname(nodeEntity.getNodeSummary().getHostname());
			node.setHyperVMachine(HostTypeUtil.isHyperVVirtualMachine(nodeEntity.getNodeSummary().getHostType()));
			node.setId(nodeEntity.getNodeSummary().getId());
			node.setImportedFromRHA(HostTypeUtil.isNodeImportFromRHA(nodeEntity.getNodeSummary().getHostType()));
			node.setImportedFromRHAWithHBBU(HostTypeUtil.isNodeImportFromRHAWithHBBU(nodeEntity.getNodeSummary().getHostType()));
			node.setImportedFromRPS(HostTypeUtil.isNodeImportFromRPS(nodeEntity.getNodeSummary().getHostType()));
			node.setImportedFromRPSReplication(HostTypeUtil.isNodeImportFromRPSReplica(nodeEntity.getNodeSummary().getHostType()));
			node.setIpaddress(nodeEntity.getNodeSummary().getIpAddress());
			node.setIsVisible(nodeEntity.getNodeSummary().getVisible());
			node.setLastupdated(nodeEntity.getNodeSummary().getLastUpdated());
			node.setLinuxD2DInstalled(ApplicationUtil.isLinuxD2DInstalled(nodeEntity.getNodeSummary().getAppStatus()));
			node.setLinuxNode(HostTypeUtil.isLinuxNode(nodeEntity.getNodeSummary().getHostType()));
			node.setMachineType(nodeEntity.getNodeSummary().getMachineType());
			node.setNodeDescription(nodeEntity.getNodeSummary().getDescription());
			node.setOsDescription(nodeEntity.getNodeSummary().getOsDescription());
			node.setOsType(nodeEntity.getNodeSummary().getOsType());
			node.setPhysicalMachine(HostTypeUtil.isPhysicsMachine(nodeEntity.getNodeSummary().getHostType()));
			node.setProtectionTypeBitmap(nodeEntity.getNodeSummary().getProtectionTypeBitmap());
			node.setRhostType(nodeEntity.getNodeSummary().getHostType());
			node.setServerPrincipalName(nodeEntity.getNodeSummary().getServerPrincipalName());
			node.setSqlServerInstalled(ApplicationUtil.isSQLInstalled(nodeEntity.getNodeSummary().getAppStatus()));
			node.setTimezone(nodeEntity.getNodeSummary().getTimezone());
			node.setVCMMonitee(HostTypeUtil.isVCMMonitee(nodeEntity.getNodeSummary().getHostType()));
			node.setVMImportFromVSphere(HostTypeUtil.isVMImportFromVSphere(nodeEntity.getNodeSummary().getHostType()));
			node.setVMwareMachine(HostTypeUtil.isVMWareVirtualMachine(nodeEntity.getNodeSummary().getHostType()));
			boolean vmWindowsOs = !HostTypeUtil.isVMNonWindowsOS(nodeEntity.getNodeSummary().getHostType());
			//defect 764842
			if(!vmWindowsOs){
				if(!StringUtil.isEmptyOrNull(nodeEntity.getNodeSummary().getOsDescription())
						&&(nodeEntity.getNodeSummary().getOsDescription().contains("Windows") 
								|| nodeEntity.getNodeSummary().getOsDescription().contains("Microsoft"))){
					vmWindowsOs = true;
				}
			}
			node.setVmWindowsOS(vmWindowsOs);
			// banar05
			node.setCurrentConsoleMachineNameForCollectDiag(nodeEntity.getNodeSummary().getCurrentConsoleMachineNameForCollectDiag());
			node.setCurrentConsoleIPForCollectDiag(nodeEntity.getNodeSummary().getCurrentConsoleIPForCollectDiag());
		}
		
		if(nodeEntity.getNodeVcloudSummary() != null){
			node.setVcloudProperties(nodeEntity.getNodeVcloudSummary());
		}
		
		if(nodeEntity.getPlanSummary() != null){
			node.setEnableStatus(nodeEntity.getPlanSummary().getEnableStatus());
			node.setError(nodeEntity.getPlanSummary().getDeployError());
			node.setLastSuccessfulPolicyDeploy(nodeEntity.getPlanSummary().getLastSuccDeploy());
			node.setPolicyContentFlag(nodeEntity.getPlanSummary().getContentFlag());
			node.setPolicyDeployReason(nodeEntity.getPlanSummary().getDeployReason());
			node.setPolicyDeployStatus(nodeEntity.getPlanSummary().getDeployStatus());
			node.setPolicyIDForEsx(nodeEntity.getPlanSummary().getId());
			node.setPolicyName(nodeEntity.getPlanSummary().getName());
			node.setPolicyType(nodeEntity.getPlanSummary().getPolicytype());
			node.setWarning(nodeEntity.getPlanSummary().getDeployWarning());
			node.setWarnningAcknowledged(nodeEntity.getPlanSummary().getDeployWarningAcknowledged());
			node.setCrossSiteVsb(nodeEntity.getPlanSummary().isHasCrossSiteVsb());
		}
		
		if(nodeEntity.getRemoteDeployInfoSummary() != null){
			node.setDeployTaskStatus(nodeEntity.getRemoteDeployInfoSummary().getDeployTaskStatus());
			node.setInstallationType(nodeEntity.getRemoteDeployInfoSummary().getInstallationType());
			node.setRemoteDeployStatus(nodeEntity.getRemoteDeployInfoSummary().getDeployStatus());
			node.setRemoteDeployTime(nodeEntity.getRemoteDeployInfoSummary().getDeployTime());
			node.setScheduleDeployCanceled(nodeEntity.getRemoteDeployInfoSummary().isScheduleDeployCanceled());
		}
		
		if(nodeEntity.getVmInfoSummary() != null){
			node.setHyperVisor(nodeEntity.getVmInfoSummary().getHypervisor());
			node.setVerifyStatus(nodeEntity.getVmInfoSummary().getVerifyStatus());
			node.setVmInstanceUUID(nodeEntity.getVmInfoSummary().getVmInstanceUUID());
			node.setEsxName(nodeEntity.getVmInfoSummary().getEsxName());
			node.setVmName(nodeEntity.getVmInfoSummary().getVmName());
			node.setVmStatus(nodeEntity.getVmInfoSummary().getVmStatus());
		}
		
		if(nodeEntity.getVsbSummary() != null){
			node.setRunningVMName(nodeEntity.getVsbSummary().getRunningVMName());
			node.setVcmMonitor(nodeEntity.getVsbSummary().getVcmMonitor());
			node.setVcmSettings(nodeEntity.getVsbSummary().getVcmSettings());
			node.setVMRunning(nodeEntity.getVsbSummary().isVMRunning());
			node.setVsbSatusInfo(nodeEntity.getVsbSummary().getVsbSatusInfo());
			node.setCrossSiteVsb(nodeEntity.getPlanSummary()==null?false:nodeEntity.getPlanSummary().isHasCrossSiteVsb());
		}
		
		if(nodeEntity.getProxyInfoSummary() != null){
			node.setProxyInfos(nodeEntity.getProxyInfoSummary());
		}
		
		if(nodeEntity.getLinuxD2DInfoSummary() != null){
			node.setLinuxD2DInfoSummary(nodeEntity.getLinuxD2DInfoSummary());
		}
		//node.setD2dOnDInstalled(ApplicationUtil.isD2DODInstalled(nodeEntity.getNodeSummary().getAppStatus())); //deprecated
		//node.setDiscoveryESXOption(discoveryESXOption);//just used in original csv import. now csv import have not used it, so not convert it
		//node.setDisplayJobPhase(displayJobPhase); //deprecated
		//node.setHyperVVmAsPhysicalMachine(HostTypeUtil.isHyperVVmAsPhysicalMachine(nodeEntity.getNodeSummary().getHostType()));//deprecated
		//node.setJobPhase(); //deprecated
		//node.setMergeJobPhase(mergeJobPhase); //deprecated
		//node.setNodeType(); //Deprecated
		//node.setOsVersion(nodeEntity.getNodeSummary().getos); //Deprecated
		//node.setPasswordVerified(passwordVerified); //Deprecated
		//node.setPauseMergeJobEnabled(pauseMergeJobEnabled);//Deprecated
		//node.setProxyNode(proxyNode); //Deprecated
		//node.setRecoveryPointFolder(recoveryPointFolder); //From original sql, this item have been commented, so don't know how to handle it?
		//node.setResumeMergeJobEnabled(resumeMergeJobEnabled); //Deprecated
		//node.setRpsServer(rpsServer);//deprecated
		return node;
	}
	
	public static NodeEntity getNodeEntityByNode(Node node){
		NodeEntity entity = new NodeEntity();
		ArcserveInfoSummary arcserveInfoSummary = new ArcserveInfoSummary();
		D2DInfoSummary d2dInfoSummary = new D2DInfoSummary();
		JobSummary jobSummary = new JobSummary();
		NodeSummary nodeSummary = new NodeSummary();
		NodeVcloudSummary nodeVcloudSummary = new NodeVcloudSummary();
		PlanSummary planSummary = new PlanSummary();
		RemoteDeployInfoSummary remoteDeployInfoSummary = new RemoteDeployInfoSummary();
		VmInfoSummary vmInfoSummary = new VmInfoSummary();
		ConverterSummary converterSummary = new ConverterSummary();
		GatewaySummary gatewaySummary = new GatewaySummary();
		VsbSummary vsbSummary = new VsbSummary();
		
		arcserveInfoSummary.setArcserveBackupVersion(node.getArcserveBackupVersion());
		arcserveInfoSummary.setArcserveServerType(node.getArcserveType());
		arcserveInfoSummary.setGdbId(node.getGDBId());
		arcserveInfoSummary.setHostId(node.getId());
		arcserveInfoSummary.setArcserveManagedStatus(node.getArcserveManaged());
		arcserveInfoSummary.setArcservePort(Integer.parseInt(node.getArcservePort()));
		arcserveInfoSummary.setArcserveProtocol(node.getArcserveProtocol());
		arcserveInfoSummary.setArcSyncChangeStatus(node.getSyncStatus().getChangeStatus());
		arcserveInfoSummary.setArcSyncStatus(node.getSyncStatus().getStatus());
		arcserveInfoSummary.setAsbuLastUpdateTime(node.getAsbuLastUpdateTime());
		arcserveInfoSummary.setAsbuLastUpdateWarning(node.isAsbuLastUpdateWarning());
		arcserveInfoSummary.setAsbuSyncFrequency(node.getAsbuSyncFrequency());
		
		d2dInfoSummary.setBuildNumber(node.getD2dBuildnumber());
		d2dInfoSummary.setHostId(node.getId());
		d2dInfoSummary.setMajorVersion(node.getD2DMajorversion());
		d2dInfoSummary.setMinorVersion(node.getD2dMinorversion());
		d2dInfoSummary.setUpdateNumber(node.getD2dUpdateversionnumber());
		d2dInfoSummary.setAuthUuid(node.getAuthUUID());
		d2dInfoSummary.setD2dLastUpdateTime(node.getD2dLastUpdateTime());
		d2dInfoSummary.setD2dLastUpdateWarning(node.isD2dLastUpdateWarning());
		d2dInfoSummary.setD2dSyncFrequency(node.getD2dSyncFrequency());
		d2dInfoSummary.setManagedStatus(node.getD2dManaged());
		d2dInfoSummary.setPassword(node.getPassword());
		d2dInfoSummary.setPort(Integer.parseInt(node.getD2dPort()));
		d2dInfoSummary.setProtocol(node.getD2dProtocol());
		d2dInfoSummary.setRpsManagedStatus(node.getRpsManagedStatus());
		d2dInfoSummary.setStatus(node.getBkpStatus().getD2dStatus());
		d2dInfoSummary.setUsername(node.getUsername());
		d2dInfoSummary.setUuid(node.getD2DUUID());
		
		jobSummary.setHostId(node.getId());
		//jobSummary.setJobMonitorKey(node.getj);
		jobSummary.setJobRunning(node.isJobRunning());
		jobSummary.setLastBackupJobStatus(node.getD2dLastBackupJobStatus().getValue());
		jobSummary.setLastBackupJobTime(node.getD2dLastBackupStartTime());
		jobSummary.setLatestJobHistories(node.getLstJobHistory());
		jobSummary.setWaittingJobToRun(node.isWaitingJobRunning());
		
		nodeSummary.setAppStatus(node.getAppStatus());
		nodeSummary.setDescription(node.getNodeDescription());
		nodeSummary.setDomainName(node.getDomainName());
		nodeSummary.setHostname(node.getHostname());
		nodeSummary.setHostType(node.getRhostType());
		nodeSummary.setId(node.getId());
		nodeSummary.setIpAddress(node.getIpaddress());
		nodeSummary.setLastUpdated(node.getLastupdated());
		nodeSummary.setMachineType(node.getMachineType());
		//nodeSummary.setNodeStatus(node.gets);
		nodeSummary.setOsDescription(node.getOsDescription());
		nodeSummary.setOsType(node.getOsType());
		nodeSummary.setProtectionTypeBitmap(node.getProtectionTypeBitmap());
		//nodeSummary.setRawMachineType(node.getra);
		nodeSummary.setServerPrincipalName(node.getServerPrincipalName());
		nodeSummary.setTimezone(node.getTimezone());
		nodeSummary.setVisible(node.getIsVisible());
		
		nodeVcloudSummary.setNodeId(node.getId());
		if(node.getVcloudProperties() != null){
			nodeVcloudSummary.setOrganization(node.getVcloudProperties().getOrganization());
			nodeVcloudSummary.setvCenter(node.getVcloudProperties().getvCenter());
			nodeVcloudSummary.setvCloudDirector(node.getVcloudProperties().getvCloudDirector());
			nodeVcloudSummary.setVdc(node.getVcloudProperties().getVdc());
		}
		
		planSummary.setContentFlag(node.getPolicyContentFlag());
		planSummary.setDeployError(node.getError());
		planSummary.setDeployReason(node.getPolicyDeployReason());
		planSummary.setDeployStatus(node.getPolicyDeployStatus());
		planSummary.setDeployWarning(node.getWarning());
		planSummary.setDeployWarningAcknowledged(node.getWarnningAcknowledged());
		planSummary.setEnableStatus(node.getEnableStatus());
		planSummary.setHostId(node.getId());
		planSummary.setId((int)node.getPolicyIDForEsx());
		planSummary.setName(node.getPolicyName());
		planSummary.setLastSuccDeploy(node.getLastSuccessfulPolicyDeploy());
		planSummary.setPolicytype(node.getPolicyType());
		
		remoteDeployInfoSummary.setDeployStatus(node.getRemoteDeployStatus());
		remoteDeployInfoSummary.setDeployTaskStatus(node.getDeployTaskStatus());
		remoteDeployInfoSummary.setDeployTime(node.getRemoteDeployTime());
		remoteDeployInfoSummary.setHostId(node.getId());
		remoteDeployInfoSummary.setInstallationType(node.getInstallationType());
		remoteDeployInfoSummary.setScheduleDeployCanceled(node.isScheduleDeployCanceled());
		
		vmInfoSummary.setHostId(node.getId());
		vmInfoSummary.setHypervisor(node.getHyperVisor());
		vmInfoSummary.setVmInstanceUUID(node.getVmInstanceUUID());
		vmInfoSummary.setVmName(node.getVmName());
		vmInfoSummary.setVmStatus(node.getVmStatus());
		vmInfoSummary.setVerifyStatus(node.getVmStatus());
		
		converterSummary.setConverter(node.getConverter());
		converterSummary.setConverterId(node.getConverterId());
		converterSummary.setConverterPassword(node.getConverterPassword());
		converterSummary.setConverterPort(node.getConverterPort());
		converterSummary.setConverterProtocol(node.getConverterProtocol());
		converterSummary.setConverterUsername(node.getConverterUsername());
		converterSummary.setHostId(node.getId());
		
		gatewaySummary.setGatewayId(node.getGatewayId().getRecordId());
		gatewaySummary.setHostId(node.getId());
		gatewaySummary.setSiteName(node.getSiteName());
		
		vsbSummary.setRunningVMName(node.getRunningVMName());
		vsbSummary.setVcmMonitor(node.getVcmMonitor());
		vsbSummary.setVcmSettings(node.getVcmSettings());
		vsbSummary.setVMRunning(node.isVMRunning());
		vsbSummary.setVsbSatusInfo(node.getVsbSatusInfo());
		
		entity.setArcserveInfoSummary(arcserveInfoSummary);
		entity.setD2dInfoSummary(d2dInfoSummary);
		entity.setJobSummary(jobSummary);
		entity.setNodeSummary(nodeSummary);
		entity.setNodeVcloudSummary(nodeVcloudSummary);
		entity.setPlanSummary(planSummary);
		entity.setRemoteDeployInfoSummary(remoteDeployInfoSummary);
		entity.setVmInfoSummary(vmInfoSummary);
		entity.setConverterSummary(converterSummary);
		entity.setGatewaySummary(gatewaySummary);
		entity.setVsbSummary(vsbSummary);
		
		return entity;
	}
	
	public static List<NodeFilter> getNodeFiltersByEdgeFilter(EdgeNodeFilter filter){
		if(filter != null){
			List<NodeFilter> filters = new ArrayList<NodeFilter>();
			CommonNodeFilter filter1 = new CommonNodeFilter();
			filter1.setApplicationBitmap(filter.getApplicationBitmap());
			filter1.setHostTypeBitmap(filter.getHostTypeBitmap());
			filter1.setNodeNamePattern(filter.getNodeName());
			filter1.setOsBitmap(filter.getOsBitmap());
			filter1.setType(NodeFilterType.Common);
			BitmapFilter filter2 = new BitmapFilter();
			filter2.setBitmap(filter.getJobStatusBitmap());
			filter2.setType(NodeFilterType.JobStatus);
			BitmapFilter filter3 = new BitmapFilter();
			filter3.setBitmap(filter.getProtectionTypeBitmap());
			filter3.setType(NodeFilterType.PlanProtectionType);
			BitmapFilter filter4 = new BitmapFilter();
			filter4.setBitmap(filter.getNodeStatusBitmap());
			filter4.setType(NodeFilterType.NodeStatus);
			BitmapFilter filter5 = new BitmapFilter();
			filter5.setBitmap(filter.getRemoteDeployBitmap());
			filter5.setType(NodeFilterType.RemoteDeployStatus);
			BitmapFilter filter6 = new BitmapFilter();
			filter6.setBitmap(filter.getNotnullfieldBitmap());
			filter6.setType(NodeFilterType.NotNullField);
			BitmapFilter filter7 = new BitmapFilter();
			filter7.setBitmap(filter.getNotnullfieldBitmap());
			filter7.setType(NodeFilterType.NotNullField);
			BitmapFilter filter8 = new BitmapFilter();
			filter8.setBitmap(filter.getLastBackupStatusBitmap());
			filter8.setType(NodeFilterType.LastBackupStatus);
			BitmapFilter filter9 = new BitmapFilter();
			filter9.setBitmap(filter.getGatewayId());
			filter9.setType(NodeFilterType.GateWay);
			filters.add(filter1);
			filters.add(filter2);
			filters.add(filter3);
			filters.add(filter4);
			filters.add(filter5);
			filters.add(filter6);
			filters.add(filter7);
			filters.add(filter8);
			filters.add(filter9);
			return filters;
		}
		return null;
	}
	
	public static SortablePagingConfig<NodeSortCol> getSortablePagingConfigByNodePagingConfig(NodePagingConfig config){
		SortablePagingConfig<NodeSortCol> pagingConfig = new SortablePagingConfig<NodeSortCol>();
		if(config == null){
			pagingConfig.setAsc(true);
			pagingConfig.setSortColumn(NodeSortCol.hostname);
			pagingConfig.setCount(Integer.MAX_VALUE);
			pagingConfig.setStartIndex(0); 
		}else {
			pagingConfig.setAsc(config.getOrderType()==EdgeSortOrder.ASC);
			pagingConfig.setSortColumn(config.getOrderCol());
			pagingConfig.setCount(config.getPagesize());
			pagingConfig.setStartIndex(config.getStartpos()); 
		}
		return pagingConfig;
	}
}
