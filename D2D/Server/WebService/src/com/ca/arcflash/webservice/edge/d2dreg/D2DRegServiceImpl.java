package com.ca.arcflash.webservice.edge.d2dreg;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.edge.datasync.BaseDataSyncer;
import com.ca.arcflash.webservice.edge.datasync.EdgeDataSynchronization;
import com.ca.arcflash.webservice.edge.policymanagement.policyapplyers.BasePolicyApplyer;
import com.ca.arcflash.webservice.edge.srmagent.SrmAlertMonitor;
import com.ca.arcflash.webservice.edge.srmagent.SrmJniCaller;
import com.ca.arcflash.webservice.toedge.IEdgeCM4D2D;
import com.ca.arcflash.webservice.toedge.WebServiceFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;

public class D2DRegServiceImpl implements ID2DRegService {
	private static final Logger logger = Logger.getLogger(D2DRegServiceImpl.class);
	protected BaseEdgeRegistration getEdgeRegistration(){
		return new D2DEdgeRegistration(); 
	}
	public boolean tryConnect2Edge(ApplicationType appType) {
		BaseEdgeRegistration _register = getEdgeRegistration();
		EdgeRegInfo regInfo = _register.getEdgeRegInfo(appType);
		if(regInfo == null || regInfo.getEdgeWSDL() == null || regInfo.getEdgeUUID() == null)
			return false;
		
		try {
			if(appType == ApplicationType.CentralManagement) {
				IEdgeCM4D2D proxy = WebServiceFactory.getEdgeService(regInfo.getEdgeWSDL(),IEdgeCM4D2D.class, 30000, 0);
				if(proxy == null){
					logger.error("Failed to get Edge proxy handle!!\n");
					return false;
				}
				
				proxy.validateUserByUUID(regInfo.getEdgeUUID());
			}
			else {
				logger.error("Improper Edge Application Type!\n");
				return false;
			}
		}catch(Exception e) {
			if(e instanceof WebServiceException) {
				logger.error("got exception 'WebServiceException', error message = " + e.getMessage());
			}
			else
			if(e instanceof SOAPFaultException) {
				logger.error("got exception 'SOAPFaultException', error message = " + e.getMessage());	
			}
			else
			if(e instanceof EdgeServiceFault) {
				EdgeServiceFault fault = (EdgeServiceFault) e;
				logger.error("got exception 'EdgeServiceFault', error code = " + fault.getFaultInfo().getCode() + ", error message = " + fault.getFaultInfo().getMessage());
			}
			else {
				logger.error("got unknown exception, type = " + e.getClass().getName() + ", error message = " + e.getMessage());
			}
			
			return false;
		}
		
		return true;
	}
	
	/**
	 * Only for backward compatibility
	 */
	@Override
	public String D2DRegister4Edge(String uuid, String policyUuid, ApplicationType appType, String edgeHostName, String edgeWSDL, String edgeLocale, boolean forceRegFlag, String regHostName) {
		return this.D2DRegister4Edge( uuid, policyUuid, appType, edgeHostName, edgeWSDL, edgeLocale, forceRegFlag, regHostName, "", null);
	}
	
	public String D2DRegister4Edge(String uuid, String policyUuid, ApplicationType appType, String edgeHostName, String edgeWSDL, String edgeLocale, boolean forceRegFlag, String regHostName, String consoleUrl, List<String> nameList) {
		BaseEdgeRegistration _register = getEdgeRegistration();	

		//20110523 Step1: get current registration and policy status firstly
		EdgeRegInfo previousRegInfo = _register.getEdgeRegInfo(appType);
		
		int result = _register.saveEdgeWSDL2XML(uuid, appType, edgeHostName, edgeWSDL, edgeLocale, forceRegFlag, regHostName, consoleUrl, nameList);
		String rtn = Integer.toString(result);
		// add by wuvyu01, 2015.5.7
		if(result == EdgeRegistrationReturnCode.REG_ERROR_CODE_REGISTERED_BY_OTHER_EDGE){
			logger.error("Cannot register again, there is another CPM yet!");
			return rtn;
		}	
		
		if(result == EdgeRegistrationReturnCode.REG_ERROR_CODE_SUCCEED || 
				result == EdgeRegistrationReturnCode.REG_ERROR_CODE_REGISTERED_BY_SAME_EDGE) {
			if(tryConnect2Edge(appType) == false) {
				logger.error("Cannot connect to registered Edge service!");
				rtn = Integer.toString(EdgeRegistrationReturnCode.REG_ERROR_CODE_EDGE_CONNECTION_FAILURE);
				return rtn;
			}
		}
		
		//20110523
		// For CM,  we need to check whether the D2D node is managed by same Edge; If so, we don't need to clear policy marker
		// If not, we need to check whether D2D side policy need to refresh: Edge will get the policy uuid that has been deployed
		// to D2D side normally, and it will set the uuid in second parameter; by comparing the uuid and D2D local policy uuid, we
		// can know whether the policy need to refresh; If it need refresh, this function will return special code to Edge;
		// In this case, Edge will start a policy deployment process for this D2D node;
		// For HBVB and VS, we keep the original logic to clear the policy marker directly
		if(previousRegInfo == null) {
			previousRegInfo = new EdgeRegInfo();
			previousRegInfo.setEdgeUUID("");
		}
		
//		if(appType != ApplicationType.CentralManagement) {
//			clearPolicyUsageMarker( appType );
//		}else {
			if(result == EdgeRegistrationReturnCode.REG_ERROR_CODE_SUCCEED || 
					result == EdgeRegistrationReturnCode.REG_ERROR_CODE_REGISTERED_BY_SAME_EDGE) {
				String edgeUuidFromEdge = previousRegInfo.getEdgeUUID()==null?"":previousRegInfo.getEdgeUUID();
				if(edgeUuidFromEdge.equals(uuid) == false) {
					//This means that the D2D node has not been managed by same Edge yet. clear the policy marker
//					logger.info("Clean D2D policy marker since the D2D is not managed by this Edge previously");
//					clearPolicyUsageMarker( appType );
					
					//If Edge side indicate there is deployed policy,
					//we need to inform Edge to launch policy re-deployment task for this D2D node
					String edgePolicyUuid = (policyUuid==null || policyUuid.isEmpty())?"":(policyUuid+":"+BasePolicyApplyer.POLICYVERSION);
					if(edgePolicyUuid.length() > 0){
						logger.info(" Need to redeploy policy since there is inconsistent D2D policy with Edge.");
						rtn = Integer.toString(EdgeRegistrationReturnCode.REG_ERROR_CODE_REGISTERED_NeedRedeploy);
					}
				}
				else {
					//This means that the D2D node was managed by same Edge previously. need to check whether policy need to refresh
					String previousPolicyUuid	= previousRegInfo.getPolicyUuids().get("Default");
					String edgePolicyUuid = (policyUuid==null || policyUuid.isEmpty())?"":(policyUuid+":"+BasePolicyApplyer.POLICYVERSION);
					String d2dPolicyUuid  = previousPolicyUuid==null?"":previousPolicyUuid;
					
					//If Edge side indicate there is no deployed policy, clear the policy marker
					if(!edgePolicyUuid.equals("")) {
//						logger.info("Clear policy marker since there is no policy deployed from Edge.");
//						clearPolicyUsageMarker( appType );
//					}
//					else {
						//If Edge side indicate there is deployed policy, and it's inconsistent with D2D side policy,
						//we need to inform Edge to launch policy re-deployment task for this D2D node
						if(!edgePolicyUuid.equals(d2dPolicyUuid)) {
							logger.info("Need to redeploy policy from Edge since the policy is inconsistent between D2D and Edge.");
							logger.info(edgePolicyUuid+","+d2dPolicyUuid);
							rtn = Integer.toString(EdgeRegistrationReturnCode.REG_ERROR_CODE_REGISTERED_NeedRedeploy);
						}
					}
				}
			}
//		}
		
		if(appType == ApplicationType.CentralManagement && isEnableSRM()) {
			logger.debug("Got Registration request from CM, start SRM Monitor, and Data Sync thread!");
			SrmAlertMonitor.startMonitor();
			SrmJniCaller.enablePkiUtl(true);
			EdgeDataSynchronization.startSyncThread();
		}
		
		return rtn;
	}
	
	private boolean isEnableSRM = true;
	
//	private void clearPolicyUsageMarker( ApplicationType appType )
//	{
//		try
//		{
//			int policyType = getPolicyTypeByAppType( appType );
//			PolicyUsageMarker.getInstance().setUsePolicy( policyType, false );
//		}
//		catch (Exception e)
//		{
//			// should write log here
//		}
//	}
	
//	private int getPolicyTypeByAppType( ApplicationType appType ) throws Exception
//	{
//		switch (appType)
//		{
//		case CentralManagement:
//			return ID2DPolicyManagementService.PolicyTypes.BackupAndArchiving;
//			
//		case VirtualConversionManager:
//			return ID2DPolicyManagementService.PolicyTypes.VCM;
//			
//		case vShpereManager:
//			return ID2DPolicyManagementService.PolicyTypes.VMBackup;
//			
//		default:
//			throw new Exception();
//		}
//	}
	
	public int D2DUnRegister4Edge(String uuid, ApplicationType appType, String edgeHostName, boolean forceUnRegFlag) {
		BaseEdgeRegistration _register = getEdgeRegistration();
		int result = _register.removeEdge(uuid, appType, edgeHostName, forceUnRegFlag);
		
		if (result == 1 && isEnableSRM()) {
			SrmAlertMonitor.stopMonitor();
			SrmJniCaller.enablePkiUtl(false);
		}
		
		if(result != 2){ // 2  D2D is managed by other Edge Server
			EdgeDataSynchronization.stopSyncThread();
			BaseDataSyncer.removeDataSyncFolder();
		}
		
		return result;
	}
	
	/*
	 * return code:
	 * 		0 not registered yet
	 * 		1 registered already with same Edge host
	 * 		2 registered with different Edge host
	 */
	public int QueryEdgeMgrStatus(String uuid, ApplicationType appType, String edgeHostName) {
		BaseEdgeRegistration _register = getEdgeRegistration();
		int result = _register.getRegStatus(uuid, appType);
		return result;
	}

	@Override
	public String QueryD2DTimeZoneID() {
		Calendar now = Calendar.getInstance();
		TimeZone t = now.getTimeZone();
		return t.getID();
	}
	
	public EdgeRegInfo getEdgeRegInfo(ApplicationType appType) {
		BaseEdgeRegistration _register = getEdgeRegistration();
		return _register.getEdgeRegInfo(appType);
	}
	public boolean isEnableSRM() {
		return isEnableSRM;
	}
	public void setEnableSRM(boolean isEnableSRM) {
		this.isEnableSRM = isEnableSRM;
	}
	@Override
	public String register4Console(EdgeRegInfo edgeRegInfo, boolean forceRegFlag) {
		return this.D2DRegister4Edge(edgeRegInfo.getEdgeUUID(), 
				(edgeRegInfo.getPolicyUuids()!=null && edgeRegInfo.getPolicyUuids().containsKey("Default"))?edgeRegInfo.getPolicyUuids().get("Default"):"", 
				edgeRegInfo.getEdgeAppType(), edgeRegInfo.getEdgeHostName(), edgeRegInfo.getEdgeWSDL(),
				edgeRegInfo.getEdgeLocale(), forceRegFlag, edgeRegInfo.getRegHostName(), edgeRegInfo.getConsoleUrl(),
				edgeRegInfo.edgeConnectNameList);
	}
	
}
