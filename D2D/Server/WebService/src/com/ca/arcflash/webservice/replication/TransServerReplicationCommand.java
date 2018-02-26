package com.ca.arcflash.webservice.replication;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.common.modelmanager.RepositoryUtil;
import com.ca.arcflash.failover.model.ADRConfigure;
import com.ca.arcflash.failover.model.BackupInfo;
import com.ca.arcflash.ha.model.ProductionServerRoot;
import com.ca.arcflash.ha.model.ReplicaRoot;
import com.ca.arcflash.ha.model.TransServerReplicaRoot;
import com.ca.arcflash.ha.model.VirtualMachineInfo;
import com.ca.arcflash.ha.model.internal.HAException;
import com.ca.arcflash.ha.modelWebService.MonitorWebServiceErrorCode;
import com.ca.arcflash.ha.utils.HACommon;
import com.ca.arcflash.ha.webservice.MonitorWebClientManager;
import com.ca.arcflash.jobscript.alert.AlertType;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.failover.Virtualization;
import com.ca.arcflash.jobscript.heartbeat.HeartBeatJobScript;
import com.ca.arcflash.jobscript.replication.ARCFlashStorage;
import com.ca.arcflash.jobscript.replication.DiskDestination;
import com.ca.arcflash.jobscript.replication.RepJobMonitor;
import com.ca.arcflash.jobscript.replication.ReplicationDestination;
import com.ca.arcflash.jobscript.replication.ReplicationJobScript;
import com.ca.arcflash.repository.RepositoryManager;
import com.ca.arcflash.webservice.WebServiceClientProxy;
import com.ca.arcflash.webservice.data.SourceNodeSysInfo;
import com.ca.arcflash.webservice.jni.FileItemModel;
import com.ca.arcflash.webservice.jni.HyperVRepParameterModel;
import com.ca.arcflash.webservice.jni.SourceItemModel;
import com.ca.ha.webservice.jni.HyperVException;
import com.ca.ha.webservice.jni.HyperVJNI;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.HAService;
import com.ca.arcflash.webservice.service.ServiceContext;

/**
 * we should pay attention that the destination should allow to take snapshots
 * 
 * @author gonro07
 * 
 */
public class TransServerReplicationCommand extends BaseTransReplicationCommand {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8068935545786675682L;
	private static final Logger log = Logger
			.getLogger(TransServerReplicationCommand.class);

	protected List<String> createTransCommands(String jobID,
			ReplicationJobScript jobScript, SessionInfo sessionInfo) {
		ReplicationDestination des = jobScript.getReplicationDestination().get(0);
		ARCFlashStorage arcflash = (ARCFlashStorage) des;
		File session = new File(sessionInfo.getSessionFolder());
		List<String> commands = new LinkedList<String>();
		commands.add(CommonUtil.D2DInstallPath + "BIN\\HATransClient.exe");
		commands.add("-socket");
		commands.add("-id:" + jobID + "_" + session.getName());
		commands.add("-src:" + session.getAbsolutePath());
		commands.add("-des:" + arcflash.getHostName());
		commands.add("-port:" + arcflash.getPort());
		commands.add("-folder:" + arcflash.getPath());
		commands.add("-productnode:"
				+ ServiceContext.getInstance().getLocalMachineName());
		commands.add("-desformat:" + arcflash.getDesCompressType());
		if (arcflash.getNetworkThrottlingInKB() != null
				&& arcflash.getNetworkThrottlingInKB() > 0) {
			commands.add("-throttling:" + arcflash.getNetworkThrottlingInKB());
		}
		if (log.isDebugEnabled()) {
			log.debug(Arrays.toString(commands.toArray(new String[0])));
		}
		return commands;
	}

	@Override
	protected int postReplication(ReplicationJobScript jobScript,
			List<SessionInfo> sessions) {

		RepJobMonitor jobMonitor = CommonService.getInstance()
				.getRepJobMonitorInternal(jobScript.getAFGuid());
		long jobID = 0;
		synchronized (jobMonitor) {
			if (isVirtualStandbyCancelled(jobMonitor))
				return 1;

			jobID = jobMonitor.getId();
			jobMonitor.setRepPhase(RepJobMonitor.REP_JOB_TAKE_SNAPSHOT);
		}
		
		SessionInfo session = sessions.get(sessions.size()-1);
		
		jobScript.setSession(session.getSessionName());
		{
			String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_REPORT_MONITOR);
			
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			
		}
		if (log.isDebugEnabled()) {
			String getenv = System.getenv("SocketTrans");
			if (!StringUtil.isEmptyOrNull(getenv)) {
				log.debug("postReplication ->System is debugging SocketTrans hoping no dependency on HyperV, so we return without reporting");
				return 0;
			}
		}
		
		try {
			HACommon.updateDiskDestWithAdrconfigInfo(jobScript.getReplicationDestination().get(0), session);
		} catch (FileNotFoundException e1) {
			String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_FAILTO_ACCESS_SESSION);
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
		}
		
		StringBuilder sessionGuids = new StringBuilder();
		for (SessionInfo sessionInfo : sessions) {
			if(sessionGuids.length()==0){
				sessionGuids.append(sessionInfo.getSessionGuid());
			}else{
				sessionGuids.append("|"+sessionInfo.getSessionGuid());
			}
		}
		
		int result = checkReplicationSubRoot(jobScript);
		if(result!=0){
			log.error("Failed to checkReplicationSubRoot");
			
			String msg = printRepFailError(jobScript);
			
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
											new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			HAService.getInstance().sendAlertMail(jobScript.getAFGuid(), jobID, AlertType.ConversionFailed, msg);
			
			return result;
		}
		
		String backupLocalTime = HACommon.date2String(session.getBackupTime());
		
		HAService.getInstance().reportProductionServerRoot(jobScript,sessionGuids.toString(),backupLocalTime);
		HAService.getInstance().takeHyperVVMSnapshot(jobScript, jobID);
		
		synchronized (jobMonitor) {
			if (isVirtualStandbyCancelled(jobMonitor))
				return 1;
			jobMonitor.setRepPhase(RepJobMonitor.REP_JOB_CRATE_BOOTABLESNAPSHOT);
		}
		
		String bootableSnapshotName=session.getSessionName()+"_bootable";
		String afGuid = jobScript.getAFGuid();
		result = HAService.getInstance().createHyperVBootableSnapshotByMonitor(afGuid, sessionGuids.toString(), bootableSnapshotName, jobID);
		if(result!=0){
			log.error("Failed to create the bootable snapshot:"+bootableSnapshotName);
			
			HAService.getInstance().deleteHyperVVMSnapshotByMonitor(afGuid, session.getSessionGuid(), jobID);
			
			String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_CREATE_BOOTABLESHAPSHOT_FAIL,
					bootableSnapshotName);

			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			HAService.getInstance().sendAlertMail(jobScript.getAFGuid(), jobID, AlertType.ConversionFailed, msg);
		}else{
			log.info("Successfully create the bootable snapshot:"+bootableSnapshotName);
			String msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_CREATE_BOOTABLESHAPSHOT_SUCCESS,
					bootableSnapshotName);
			
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			
		}

		super.postReplication(jobScript, sessions);

		return result;
	}

	/**
	 * we get the latest guid from VM
	 * 
	 * @throws HAException
	 */
	@Override
	String[] getGuidsOfReplicatedSessions(ReplicationJobScript jobScript)
			throws Exception {
		String[] re = new String[0];
		if (log.isDebugEnabled()) {
			String getenv = System.getenv("SocketTrans");
			if (!StringUtil.isEmptyOrNull(getenv)) {
				log.debug("getGuidsOfReplicatedSessions ->System is debugging SocketTrans hoping no dependency on HyperV, so we return empty");
				return re;
			}
		}
		try {
			re = HAService.getInstance().getGuidsOfReplicatedSessionsFromVMSnapshots(jobScript);
		} catch (Exception e) {
			log.debug(e.getMessage());
			String msg = ReplicationMessage.getMonitorServiceErrorString(MonitorWebServiceErrorCode.Common_Get_Replicated_Sessions, e.getMessage());
			throw new Exception(msg);
		}
		return re;
	}

	@Override
	protected HyperVRepParameterModel createHyperVTransParams(
			BackupDestinationInfo backupDestinationInfo, ReplicationJobScript jobScript, long shrmemid,
			SessionInfo session) {
		ReplicationDestination replicationDestinaiton = jobScript.getReplicationDestination().get(0);
		HyperVRepParameterModel re = null;

		ARCFlashStorage arcflash = (ARCFlashStorage) replicationDestinaiton;
		re = new HyperVRepParameterModel();

		//set aguid in order to update afguid-related job monitor
		//this afguid will be passed back to hyperVUpdateRepJobMonitorProgress
		re.setAfGuid(jobScript.getAFGuid());
		
		re.setbCompressOnWire(false);
		re.setbEncryptOnWire(false);
		re.setbOverwriteExist(false);
		re.setPwszCryptPassword(null);

		re.setUlCtlFlag(0);
		
		boolean useVHDXformat = true;
		
		try {
			HeartBeatJobScript heartBeatJobScript = HAService.getInstance().getHeartBeatJobScript(jobScript.getAFGuid());
			WebServiceClientProxy client = MonitorWebClientManager.getMonitorWebClientProxy(heartBeatJobScript);
			SourceNodeSysInfo sourceNodeSysInfo = null;
			sourceNodeSysInfo = client.getServiceV2().getSourceNodeSysInfo();
			String targetPlatform = sourceNodeSysInfo.getVersion();
			if(targetPlatform.startsWith("6.0") || targetPlatform.startsWith("6.1"))
				useVHDXformat = false;
			
			if(useVHDXformat) {
				try {
					List<String> getAttachedDiskImage = client.getServiceV2().GetHyperVVMAttachedDiskImage(arcflash.getVmUUID());

					if (getAttachedDiskImage != null) {
						ListIterator<String> listIterator = getAttachedDiskImage.listIterator();
						if (listIterator.hasNext()) {
							String string = (String) listIterator.next();
							if(string.endsWith(".vhd") || string.endsWith(".avhd"))
								useVHDXformat = false;
						}
					}
				} catch (Throwable e) {
					log.warn("Cannot get disk images file of VM " + arcflash.getVmUUID(), e);
				}
			}
		} catch (Throwable e) {
			log.warn("Fail to get source node information, afguid = " + jobScript.getAFGuid(), e);
		}

		if(useVHDXformat)
		{
			//	#define HAJS_DES_VHD_ORIGIN         0x00        //does not change the format
			//	#define HAJS_DES_VHD_PLAIN          0x01        //des format is plain VHD
			//	#define HAJS_DES_VHD_COMPRESS       0x02        //des format is compressed VHD
			//	#define HAJS_DES_VHDX_ORIGIN        0x05        //does not change the format
			//	#define HAJS_DES_VHDX_PLAIN         0x06        //des format is plain VHDX
			//	#define HAJS_DES_VHDX_COMPRESS      0x07        //des format is compressed VHDX
			
			if(arcflash.getDesCompressType() == 0) 
				re.setUlDesVHDFormat(5);
			else if(arcflash.getDesCompressType() == 1)
				re.setUlDesVHDFormat(6);
			else if(arcflash.getDesCompressType() == 2) 
				re.setUlDesVHDFormat(7);
			else
				re.setUlDesVHDFormat(arcflash.getDesCompressType()); // default
		}
		else
			re.setUlDesVHDFormat(arcflash.getDesCompressType());
		
		re.setUlJobType(0);
		re.setUlProtocol(HyperVRepParameterModel.HAJS_PROTOCOL_SOCKET);
		re.setUlSrcItemCnt(1);
		re.setJobID(shrmemid);

		ArrayList<SourceItemModel> pSrcItemList = new ArrayList<SourceItemModel>();

		SourceItemModel sim = new SourceItemModel();

		sim.setPwszPath(session.getSessionFolder());
		sim.setPwszSFPassword(backupDestinationInfo.getNetConnPwd());
		sim.setPwszSFUsername(backupDestinationInfo.getNetConnUserName());
		
		//Get SubFolder to avoid destination conflict.
		
		//construct FileItemModel begin
		List<FileItemModel> fileItems = HACommon.getFileItemModels(replicationDestinaiton, session);
		//construct FilteItemModel ends.
		
		sim.setDiskCount(fileItems.size());
		sim.setFiles(fileItems);
		
		pSrcItemList.add(sim);

		re.setpSrcItemList(pSrcItemList);
		// re.setPwszJobID(session.getSessionGuid()+"_"+shrmemid);
		// save session will use the same job ID of dengfeng
		re.setPwszJobID(session.getSessionGuid());
		// why local username
		re.setPwszLocalPassword(null);
		re.setPwszLocalUsername(null); // deng feng will get it from D2D

		re.setPwszDesHostName(arcflash.getHostName());
		re.setPwszDesPort(HAService.getInstance().getHATranServerPort(jobScript.getAFGuid()));
		re.setPwszUserName(arcflash.getUserName());
		re.setPwszPassword(arcflash.getPassword());

		re.setPwszDesFolder(arcflash.getDiskDestinations().get(0).getStorage().getName());

		RepositoryUtil repository = RepositoryUtil.getInstance(CommonUtil.getRepositoryConfPath());
		String lastRepDest = null;
		try {
			lastRepDest = repository.getProductionServerRoot(jobScript.getAFGuid()).getReplicaRoot().getLastRepDest();
		} catch (Exception e1) {
			log.warn(e1.getMessage());
		}
		re.setPwszOldDesFolder(lastRepDest==null?"":lastRepDest);

		String productNode = HACommon.getProductionServerNameByAFRepJobScript(jobScript);
		re.setPwszProductNode(productNode);

		re.setBackupDescType(jobScript.getBackupDestType());
		return re;
	}
	
	@Override
	protected int preProcess(ReplicationJobScript jobScript, BackupDestinationInfo backupDestinationInfo) throws HAException {
		if (log.isDebugEnabled()) {
			String getenv = System.getenv("SocketTrans");
			if (!StringUtil.isEmptyOrNull(getenv)) {
				log.debug("preProcess ->System is debugging SocketTrans hoping no dependency on HyperV, so we return without VM creation");
				return CommonUtil.VM_EXIST;
			}
		}
		RepJobMonitor jobMonitor = CommonService.getInstance()
				.getRepJobMonitorInternal(jobScript.getAFGuid());
		
		long jobID = 0;
		synchronized (jobMonitor) {
			if (isVirtualStandbyCancelled(jobMonitor))
				return CommonUtil.VM_CREATE_CANCELED;

			jobID = jobMonitor.getId();
			jobMonitor.setRepPhase(RepJobMonitor.REP_JOB_CREATE_VM);
		}

		String host = "";
		String msg = "";
		String vmName = "";
		try {

			// created 1 for newly created one, 2 for old one
			int created = 0;
			ReplicationDestination repDest = jobScript.getReplicationDestination().get(0);
			ARCFlashStorage arcDes = (ARCFlashStorage)repDest;
			
			HeartBeatJobScript heartBeatScript = HAService.getInstance().getHeartBeatJobScript(jobScript.getAFGuid());
			host = heartBeatScript.getHeartBeatMonitorHostName();
			vmName = arcDes.getVirtualMachineDisplayName();

			WebServiceClientProxy client = MonitorWebClientManager.getMonitorWebClientProxy(heartBeatScript);
			
			int vmGeneration = 1;
			try {
				ADRConfigure adrConfigure = HACommon.getADRConfiguration(jobScript.getAFGuid(), backupDestinationInfo);
				if (adrConfigure == null) {
					log.warn("No ADRConfigure.xml under " + backupDestinationInfo.getBackupDestination());
				}
				BackupInfo backupInfo = HACommon.getBackupConfiguration(jobScript.getAFGuid(), backupDestinationInfo);
				if (backupInfo == null) {
					log.warn("No BackupInfo.xml under " + backupDestinationInfo.getBackupDestination());
				}
		
				if (adrConfigure != null && backupInfo != null) {
					boolean isUEFI = adrConfigure.isUEFI();
					if(isUEFI) {
						String msg1 = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_HYPERV_VM_UEFI, jobScript.getAgentNodeName());
						HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
								new String[] { msg1,"", "", "", "" }, jobScript.getAFGuid());

						SourceNodeSysInfo sourceNodeSysInfo = null;
						sourceNodeSysInfo = client.getServiceV2().getSourceNodeSysInfo();
						if (RepositoryManager.IsTargetPlatformSupportVMGeneretion2(adrConfigure, backupInfo, sourceNodeSysInfo))
							vmGeneration = 2;
						else {
							msg1 = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_HYPERV_FAIL_FOR_UEFI, host);
							HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
									new String[] { msg1,"", "", "", "" }, jobScript.getAFGuid());
							return CommonUtil.VM_CREATE_FAILED;
						}
					}
				} else {
					log.warn("Use VM Generation 1 as both ADRConfigure.xml and BackupInfo.xml can't be found.");
				}
			} catch (Exception e1) {
				log.warn("Get souce machine info failed!", e1);
			}
			
			VirtualMachineInfo vmInfo = new VirtualMachineInfo();
			boolean createNewVM = ifNeedCreateNewVM(jobScript, vmName, client, vmInfo);
			
			if(!createNewVM){
				msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_FIND_EXISTING_VM, vmName);
				created = CommonUtil.VM_EXIST;
				
				repDest.setVmName(vmInfo.getVmName());
				repDest.setVmUUID(vmInfo.getVmGUID());
				HAService.getInstance().updateRepJobScript(jobScript);
			}else{
				if (!StringUtil.isEmptyOrNull(jobScript.getVmNamePrefix())) {
					String stdVMName = jobScript.getVmNamePrefix() + jobScript.getAgentVMName();
					vmName = stdVMName;
				}
				String vmFinalName = vmName;
				String vmGUID = client.getServiceV2().isHyperVVMNameExist(vmName);
				if (!StringUtil.isEmptyOrNull(vmGUID)) {
					vmFinalName = vmName + "(" + String.valueOf(System.currentTimeMillis()) + ")";
					log.info(String.format("VM name %s is existing, will create VM with new name %s", vmName, vmFinalName));
				}

				// For create new VM, reset DiskDesinations in vsb job script and production server info in repository.xml
				resetVMConfigure(jobScript.getAFGuid(), client);
				
				String vmguid = HAService.getInstance().createHyperVVM(jobScript, vmGeneration, vmFinalName, client);
				
				updateReplicationAndFailoverScript(jobScript.getAFGuid(), vmFinalName, vmguid);

				updateRepository(vmguid,vmFinalName,jobScript, vmGeneration);
				
				//If VM is newly created, clear last replication subroot
				RepositoryUtil repository = null;
				try{
					String xml = CommonUtil.getRepositoryConfPath();
					repository = RepositoryUtil.getInstance(xml);
				}catch (Exception e) {
					log.error(e.getMessage(),e);
				}
				
				ReplicaRoot repRoot = repository.getProductionServerRoot(jobScript.getAFGuid()).getReplicaRoot();
				if(repRoot != null) {
					repRoot.setRepSubRoot(null);
					repRoot.setLastRepDest(null);
					repository.saveProductionServerRoot();
				}
				
				msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_CREATE_NEW_VM, vmFinalName);
				created = CommonUtil.VM_CREATED;
				
			}
			
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_INFO, jobID, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			
			return created;

		} catch (SOAPFaultException e) {
			log.error("Failed to create VM", e);
			
			msg = e.getFault().getFaultString();
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
											new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			
			msg = ReplicationMessage.getMonitorServiceErrorString("8889900000", vmName);
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			return CommonUtil.VM_CREATE_FAILED;

		}catch (WebServiceException ws) {
			log.error("Failed to connect to HyperV server: "+host, ws);
			
			msg = ReplicationMessage.getResource(ReplicationMessage.REPLICATION_CONVERSION_FAIL_TO_CONNECT_HOST, host, ws.getMessage());
			HACommon.addActivityLogByAFGuid(Constants.AFRES_AFALOG_ERROR, jobID, Constants.AFRES_AFJWBS_GENERAL, 
					new String[] { msg,"", "", "", "" }, jobScript.getAFGuid());
			
			return CommonUtil.VM_CREATE_FAILED;
		}

	}

	@Override
	protected int onReplicationFailure(ReplicationJobScript jobScript, List<SessionInfo> sessions)
			throws SOAPFaultException {
		StringBuilder sessionGuids = new StringBuilder();
		for (SessionInfo sessionInfo : sessions) {
			if (sessionGuids.length() == 0) {
				sessionGuids.append(sessionInfo.getSessionGuid());
			} else {
				sessionGuids.append("|" + sessionInfo.getSessionGuid());
			}
		}

		SessionInfo session = sessions.get(sessions.size() - 1);
		String backupLocalTime = HACommon.date2String(session.getBackupTime());

		HAService.getInstance().reportProductionServerRoot(jobScript, sessionGuids.toString(), backupLocalTime);

		return 0;
	}

	
	private void updateLocalRepositoryFromHyperV(ReplicationJobScript jobScript, String vmName, WebServiceClientProxy client){
		String afguid = jobScript.getAFGuid();
		
		String remoteFile = HAService.getInstance().generateRemoteRepositoryPath(vmName, jobScript);
		
		log.info("Try download repository.xml on hyper-V datastore, remoteFile " + remoteFile);
		ProductionServerRoot repository = null;
		try {
			repository = client.getServiceV2().downloadProductionServerRoot(remoteFile, afguid);
		}catch (Exception e) {
			log.error("Fail to download repository.xml from hyper-V datastore", e);
			return;
		}
		
		if (repository == null) {
			log.error("repository.xml on hyper-V datastore is null.");
			return;
		}

		String xml = CommonUtil.D2DInstallPath+ "Configuration\\repository.xml";
		try {
			RepositoryUtil.getInstance(xml).saveProductionServerRoot(repository);
		} catch (HAException e) {
			log.error(String.format("Fail to update local %s. ", xml), e);
		}
	}

	private VirtualMachineInfo loadUUIDFromRepository(ReplicationJobScript jobScript, String vmName, WebServiceClientProxy client) {
		VirtualMachineInfo vmInfo = null;
		TransServerReplicaRoot root = getReplicaRoot(jobScript);
		if (root != null) {
			log.info(String.format("Get VM %s from repository, and its uuid is %s", root.getVmname(), root.getVmuuid()));

			vmInfo = new VirtualMachineInfo();
			vmInfo.setVmGUID(root.getVmuuid());
			vmInfo.setVmName(root.getVmname());
		} else {
			log.info("Not find VM from local repository, try load from the monitor server");
			
			// Try get the production server root from the monitor server
			updateLocalRepositoryFromHyperV(jobScript, vmName, client);
			ReplicaRoot root2 = getReplicaRoot(jobScript);
			if (root2 != null) {
				log.info(String.format("Get VM %s from repository, and its uuid is %s", root2.getVmname(), root2.getVmuuid()));

				vmInfo = new VirtualMachineInfo();
				vmInfo.setVmGUID(root2.getVmuuid());
				vmInfo.setVmName(root2.getVmname());
			}
		}
		
		return vmInfo;
	}
	
	private boolean ifNeedCreateNewVM(ReplicationJobScript jobScript, String vmName, WebServiceClientProxy client, VirtualMachineInfo vmInfo) {
		
		String vmGUID = client.getServiceV2().isHyperVVMNameExist(vmName);
		if (StringUtil.isEmptyOrNull(vmGUID)) {
			log.info(String.format("VM %s does not exist", vmName));
			return true;
		}

		VirtualMachineInfo localVMInfo = loadUUIDFromRepository(jobScript, vmName, client);
		if(localVMInfo == null)
			return true;

		if(!localVMInfo.getVmName().equalsIgnoreCase(vmName)) {
			log.info(String.format("The VM name in repository is %s, while in job script it's %s. Need to create new VM", localVMInfo.getVmName(), vmName));
			return true;
		}
		
		if (!localVMInfo.getVmGUID().equalsIgnoreCase(vmGUID)) {
			log.info(String.format("The VM GUID of %s on hyperV server is %s, while in job script it's %s. Need to create new VM", vmGUID, vmName, localVMInfo.getVmGUID()));
			return true;
		}

		if (!IsRemoteVmFileExist(localVMInfo.getVmName(), jobScript, client))
		{
		    log.info(String.format("VMSnapshotsModel.xml is not exist in hyperV. Need to create new VM"));
            return true;
		}
		
		vmInfo.setVmGUID(localVMInfo.getVmGUID());
		vmInfo.setVmName(localVMInfo.getVmName());
		return false;
	}

	private	boolean IsRemoteVmFileExist(String localVmName,
			ReplicationJobScript jobScript, WebServiceClientProxy client)
	{
		String remoteFile = generateRemoteVmSnapshotXMLPath(localVmName, jobScript);
		
        return client.getServiceV2().IsVmFileExist(remoteFile);
	}

	private String generateRemoteVmSnapshotXMLPath(String localVmName,
			                                                ReplicationJobScript jobScript) 
	{
		ReplicationDestination replicationDestinaiton = HAService.getInstance().getReplicationDestinaiton(jobScript);
		ARCFlashStorage arcDes = (ARCFlashStorage) replicationDestinaiton;
		String filePath = "";	
		String repSubRoot = "";
		if(StringUtil.isEmptyOrNull(arcDes.getRepSubRoot()))
		{
            repSubRoot = localVmName + "\\" + "sub0001";
        }
		else
		{
            repSubRoot = arcDes.getRepSubRoot();
        }

        String moniteeHost = arcDes.getMoniteeHostName();
        DiskDestination diskDestination = arcDes.getDiskDestinations().get(0);
        String repDest = diskDestination.getStorage().getName();
        if(!repDest.endsWith("\\"))
		{
            repDest += "\\";
        }
            
        if(!repSubRoot.endsWith("\\"))
		{
            repSubRoot += "\\";
        }

        if(!repDest.endsWith(repSubRoot))
		{
            filePath = repDest + repSubRoot + moniteeHost + "\\" + CommonUtil.SNAPSHOT_XML_FILE;
        }else
        {
            filePath = repDest + moniteeHost + "\\" + CommonUtil.SNAPSHOT_XML_FILE;
        }
        
		return filePath;
	}

	private void updateReplicationAndFailoverScript(String afGuid,String vmname,String vmGUID){
		
		ReplicationJobScript repJobScript = HAService.getInstance().getReplicationJobScript(afGuid);
		ARCFlashStorage dest = (ARCFlashStorage)repJobScript.getReplicationDestination().get(0);

		dest.setVirtualMachineDisplayName(vmname);
		dest.setVmName(vmname);
		dest.setVmUUID(vmGUID);
		
		HAService.getInstance().updateRepJobScript(repJobScript);
		
		FailoverJobScript failoverScript = HAService.getInstance().getFailoverJobScript(afGuid);
		Virtualization hyperv = failoverScript.getFailoverMechanism().get(0);
		
		hyperv.setVirtualMachineDisplayName(vmname);
		
		HAService.getInstance().setFailoverJobScript(failoverScript);
		
	}

	private void resetVMConfigure(String afGuid, WebServiceClientProxy client){
		
		ReplicationJobScript repJobScript = HAService.getInstance().getReplicationJobScript(afGuid);
		ARCFlashStorage dest = (ARCFlashStorage)repJobScript.getReplicationDestination().get(0);

		List<DiskDestination> diskDestinations = dest.getDiskDestinations();
		diskDestinations.removeAll(diskDestinations);
		for (DiskDestination diskDestination : dest.getCFGDiskDestinations()) {
			diskDestinations.add(diskDestination.deepCopy());
		}

		HAService.getInstance().updateRepJobScript(repJobScript);
		
		try {
			HAService.getInstance().removeReplicatedInfo(afGuid, repJobScript.getVirtualType(), null);

			client.getServiceV2().removeReplicatedInfo(afGuid, repJobScript.getVirtualType(), null);
		} catch(Exception e) {
			log.warn("Failed to remove the Replication info", e);
		}
	}
}
