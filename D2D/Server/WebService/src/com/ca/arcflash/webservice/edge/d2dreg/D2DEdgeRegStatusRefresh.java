package com.ca.arcflash.webservice.edge.d2dreg;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.job.AFJob;
import com.ca.arcflash.jobqueue.JobQueueFactory;
import com.ca.arcflash.jobscript.base.JobType;
import com.ca.arcflash.jobscript.replication.ReplicationJobScript;
import com.ca.arcflash.webservice.data.edge.datasync.d2d.D2DStatus;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;
import com.ca.arcflash.webservice.edge.policymanagement.PolicyCheckStatus;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.HAService;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.VSphereService;
import com.ca.arcflash.webservice.toedge.IEdgeCM4D2D;
import com.ca.arcflash.webservice.toedge.WebServiceFactory;
import com.ca.arcflash.webservice.util.WebServiceMessages;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;

public class D2DEdgeRegStatusRefresh {
	private static final Logger logger = Logger.getLogger(D2DEdgeRegStatusRefresh.class);
	private volatile static boolean IsEdgeRegStatusRefreshThreadRunning  = false;
	
	private static String appTypeToString(ApplicationType appType) {
		
		if(appType == ApplicationType.CentralManagement)
			return "CentralManagement";
		else if(appType == ApplicationType.Report)
			return "Report";
		else if(appType == ApplicationType.VirtualConversionManager)
			return "VirtualConversionManager";
		else if(appType == ApplicationType.vShpereManager)
			return "vSPhereManager";
		else
			return "Unknown Application";
	}
	

	
	private boolean IsManagedByEdgeCM() throws ServiceException{
		boolean IsManagedByEdge = false;
		D2DEdgeRegistration edgeReg = new D2DEdgeRegistration();
		EdgeRegInfo edgeRegInfo = edgeReg.getEdgeRegInfo(ApplicationType.CentralManagement);
		if(edgeRegInfo == null || edgeRegInfo.getEdgeWSDL() == null || edgeRegInfo.getEdgeWSDL().isEmpty()) {
			logger.debug("There is no local registration information for " + appTypeToString(ApplicationType.CentralManagement));
			return false;
		}
		
		IEdgeCM4D2D proxy = WebServiceFactory.getEdgeService(edgeRegInfo.getEdgeWSDL(),IEdgeCM4D2D.class);
		
		if(proxy == null)
		{
			logger.error("Failed to get Edge proxy handle!!\n");
			throw new ServiceException("Cannot get Edge proxy handle", "EdgeConnectError");
		}
		
		String UUID = CommonService.getInstance().getNodeUUID();

		try {
			 proxy.validateUserByUUID(edgeRegInfo.getEdgeUUID());
		}catch(EdgeServiceFault e) {
			logger.error("Failed to establish connection to Edge Server(login failed)\n");
			throw new ServiceException(e.getMessage(), "EdgeLoginError");
		}
		
		try {
			IsManagedByEdge = proxy.isManagedByEdge(UUID);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.debug(e);
			throw new ServiceException(e.getMessage(),"EdgeConnectError");
		}
		
		return IsManagedByEdge;
	}
	
	private static synchronized boolean RefreshThreadStart(){
		if(IsEdgeRegStatusRefreshThreadRunning == true)
			return false;
		else {
			IsEdgeRegStatusRefreshThreadRunning = true;
			return true;
		}
	}
	
	private static synchronized void RefreshThreadStop(){
		IsEdgeRegStatusRefreshThreadRunning = false;
	}
	
	public void startRefreshThread() {
		Thread t = new Thread(new RegStatusRefreshThread());
		t.setDaemon(true);
		t.start();
	}
	
	public void cleanRegInfo4VSphereOrVCMIfNeed(){
		Update4VSphere();
		Update4VCM();
	}
	
	private class RegStatusRefreshThread implements Runnable{
		@Override
		public void run() {
			if(RefreshThreadStart() == false) {
				logger.error("There is another Registration Status Refresh Thread running! Exit...");
				return;
			}
			
			logger.info("Registration Status Refresh Thread started ...");
			
			try {
				Update4VSphere();
				
				Update4VCM();
				
				Update4CM();
				
			} catch (Exception e) {
				logger.error(e.getMessage());
			} finally {
				RefreshThreadStop();
			}
		}
	}
	
	private void Update4CM() {
		try {
			logger.info("Refresh Edge Registration status for " + appTypeToString(ApplicationType.CentralManagement));			
			int status=BackupService.getInstance().refreshBackupConfigSettingWithEdge();
			logger.info("CM RefreshBackupConfigSettingWithEdge status is "+status);
//			if(IsManagedByEdge(ApplicationType.CentralManagement) == false) {
//				// The local registration info for edge CPM will not be cleaned in this case because of 4in1
//				 logger.info("Clean local registration information for Edge Central Manager...");
//				 D2DEdgeRegistration.cleanRegInfo4CM();
//			}
//			D2DStatus d2dStatus = CommonService.getInstance().checkD2DStatusFromEdgeCM();
//			logger.info("D2DStatus is "+d2dStatus.name());
//			switch (d2dStatus) {
//			case NodeDeleted:
//				logger.info("clean registration info ...");
//				PlanUtil.cleanRegInfo4CM();
//			case NoPolicy:
//				logger.info("clean plan info ...");
////				PlanUtil.cleanPlanInfo4CM();
//				PlanUtil.cleanPlan();
//			case Ok:
//			case PolicyChanged:
//			case StandAlone:
//			default:
//			}
//		} catch (ServiceException e) {
//			logger.error(e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
	
	private void Update4VSphere() {
		try {
			logger.info("Refresh Edge Registration status for " + appTypeToString(ApplicationType.vShpereManager));
		
//			if(IsManagedByEdge(ApplicationType.vShpereManager) == false) {
				// logger.info("Clean local registration information for Edge VSphere Manager...");
				// cleanLocalEdgeRegInfo(ApplicationType.vShpereManager);
//			}
			
			VSphereService svc = VSphereService.getInstance();
			List<VirtualMachine> VMs = svc.RefreshBackupConfigSettingWithEdge();
			
			EdgeRegInfo info = new D2DEdgeRegistration().getEdgeRegInfo(ApplicationType.vShpereManager);
			for (VirtualMachine vm : VMs) {
				String policyUuid = "UNKNOWNPOLICY";
				if(info.getPolicyUuids().containsKey(vm.getVmInstanceUUID())){
					policyUuid = info.getPolicyUuids().get(vm.getVmInstanceUUID());
					if(policyUuid!=null && !policyUuid.isEmpty()){
						policyUuid = policyUuid.split(":")[0];
					}
				}
				int state=svc.checkPolicyFromEdge(vm.getVmInstanceUUID(), policyUuid, false);
				switch(state){
				case PolicyCheckStatus.UNKNOWN:
				case PolicyCheckStatus.SAMEPOLICY:
					break;
				case PolicyCheckStatus.NOPOLICY:
					VSphereService.getInstance().getNativeFacade().addVMLogActivity(Constants.AFRES_AFALOG_WARNING,Constants.AFRES_AFJWBS_GENERAL,
							new String[]{WebServiceMessages.getResource("autoUnassignPolicy"), "","","",""},vm.getVmInstanceUUID());
					logger.warn(WebServiceMessages.getResource("autoUnassignPolicy"));
					svc.detachVSpherePolicy(new VirtualMachine[]{vm});
					break;
				case PolicyCheckStatus.DIFFERENTPOLICY:
					VSphereService.getInstance().getNativeFacade().addVMLogActivity(Constants.AFRES_AFALOG_WARNING,Constants.AFRES_AFJWBS_GENERAL,
							new String[]{WebServiceMessages.getResource("autoRedeployPolicy"), "","","",""},vm.getVmInstanceUUID());
					logger.warn(WebServiceMessages.getResource("autoRedeployPolicy"));
					break;
				case PolicyCheckStatus.POLICYDEPLOYING:
					break;
				case PolicyCheckStatus.POLICYFAILED:
					VSphereService.getInstance().getNativeFacade().addVMLogActivity(Constants.AFRES_AFALOG_WARNING,Constants.AFRES_AFJWBS_GENERAL,
							new String[]{WebServiceMessages.getResource("autoRedeployFailedPolicy"), "","","",""},vm.getVmInstanceUUID());
					logger.warn(WebServiceMessages.getResource("autoRedeployFailedPolicy"));
					break;
				}
			}
		} catch (ServiceException e) {
			logger.error(e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	private void Update4VCM() {
		//TODO
		try {
			logger.info("Refresh Edge Registration status for " + appTypeToString(ApplicationType.VirtualConversionManager));
		
//			if(IsManagedByEdge(ApplicationType.VirtualConversionManager) == false) {
//				logger.info("Clean local registration information for Edge Virtual Convertion Manager...");
//				cleanLocalEdgeRegInfo(ApplicationType.VirtualConversionManager);
//			}
			
			//TODO: RefreshVcmVMSettingWithEdge();
			
			// check policy status
			Collection<AFJob> jobs = JobQueueFactory.getDefaultJobQueue().findByJobType(JobType.Replication);
			for (AFJob afJob : jobs) {
				ReplicationJobScript jobScript = (ReplicationJobScript) afJob.getJobScript();
				if (jobScript != null) {
					int state=HAService.getInstance().RefreshBackupConfigSettingWithEdge(jobScript.getAFGuid(), jobScript.getPlanUUID());
					logger.info("VCM RefreshBackupConfigSettingWithEdge "+afJob.getJobScript().getAFGuid()+" is "+state);
				}
			}
			
//		} catch (ServiceException e) {
//			logger.error(e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}
}
