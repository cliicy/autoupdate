/**
 * 
 */
package com.ca.arcflash.webservice.edge.licensing;

import java.io.File;
import java.net.InetAddress;

import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.d2dreg.D2DEdgeRegistration;
import com.ca.arcflash.webservice.edge.d2dreg.EdgeRegInfo;
import com.ca.arcflash.webservice.edge.license.LicenseCheckResult;
import com.ca.arcflash.webservice.edge.license.LicenseDef;
import com.ca.arcflash.webservice.edge.license.MachineInfo;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.toedge.IEdgeCM4D2D;
import com.ca.arcflash.webservice.toedge.WebServiceFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;

/**
 * @author lijwe02
 * 
 */
public class LicenseUtils {
	private static final Logger logger = Logger.getLogger(LicenseUtils.class);
	private static final long FLAG_MANAGED_BY_EDGE = 1;
	private static final long FLAG_NOT_MANAGED_BY_EDGE = 2;
	private static final long FLAG_NETWORK_EXCEPTION = 3;
	private static final long FLAG_INVALID_CPM_UUID = 0xFF;
//	private static final long FLAG_RELEASED_BY_EDGE = 0xFE;
//	private static final NativeFacade nativeFacade = new NativeFacadeImpl();

//	public static final String LICENSE_DIRECTORY = CommonUtil.D2DHAInstallPath + "Configuration\\license";
//	private static final LicenseCache LICENSE_CACHE = new LicenseCache(LICENSE_DIRECTORY + "\\udplicense.lic");
	private static final String CACHE_LICENSE_DIRECTORY = ServiceContext.getInstance().getDataFolderPath()+File.separator+"udplicense";
	private static CachCentralLicense cachLicense = new CachCentralLicense(CACHE_LICENSE_DIRECTORY); 
	public static final long CHECK_FAILED = 2;
	public static final long CHECK_OK = 1;
	/**
	 * this method is for agent job process to check central license, return value means if success to call method. 
	 * if it is CHECK_OK, job will check ComponentInfo to detect if has license.
	 * @param in_machine
	 * @param in_feature
	 * @param out_info
	 * @return 
	 * @throws EdgeServiceFault
	 */
	public static long checkCentralLicenseFromEdge(MachineInfo in_machine, long in_feature, ComponentInfo out_info) throws EdgeServiceFault {
		try {
			if(in_machine==null){
				logger.error("input param MachineInfo is null!");
				return CHECK_FAILED;
			}
			EdgeRegInfo edgeRegInfo = new D2DEdgeRegistration().getEdgeRegInfo(ApplicationType.CentralManagement);;
			if(edgeRegInfo==null||StringUtil.isEmptyOrNull(edgeRegInfo.getEdgeWSDL()) || StringUtil.isEmptyOrNull(edgeRegInfo.getEdgeUUID())){
				String errorMessage = "Check centrial License From Edge failed. There is no edge registration flag!";
				logger.error(errorMessage);
				return CHECK_FAILED;
			}

			IEdgeCM4D2D proxy = null;
			try {
				proxy = WebServiceFactory.getEdgeService(edgeRegInfo.getEdgeWSDL(), IEdgeCM4D2D.class);
			} catch (SOAPFaultException e) {
				String errorMessage = "Check centrial License From Edge failed. Failed to establish connection to Edge Server, try to check from cache.";
				if (logger.isDebugEnabled()) {
					logger.debug(errorMessage, e);
				}
				convert2ComponentInfo(cachLicense.getLicenseWithLogActivity(in_machine.getJobId()), out_info);
				return CHECK_OK;
			}

			try {
				proxy.validateUserByUUID(edgeRegInfo.getEdgeUUID());
			} catch (EdgeServiceFault e) {
				String errorMessage = "Check centrial License From Edge failed. Login Edge Server:" + edgeRegInfo.getEdgeHostName()
						+ " Failed.";
				logger.error(errorMessage, e);
				return CHECK_FAILED;
			}
			
			String nodeName = edgeRegInfo.getRegHostName();
			if(StringUtil.isEmptyOrNull(nodeName)){
				logger.warn("there is no reg host name in edge registration. use local host name as reg host name!");
				nodeName = InetAddress.getLocalHost().getHostName();
			}
			try {
				in_machine.setHostName(nodeName);
				in_machine.setHostUuid(CommonService.getInstance().getNodeUUID());
				in_machine.setServerName(nodeName);
				in_machine.setServerUuid(in_machine.getHostUuid());
				logger.info("checkCentrialLicenseFromEdge:" + in_machine.toString() + " required_feature: "+in_feature);
				boolean isNCE = isNCE(in_machine.getJobType(), in_feature);
				boolean isVM = isVM(in_feature);
				LicenseCheckResult result = null; 
				if(isNCE){
					logger.info("This is No Charge Edition. isVM: "+isVM);
					result = proxy.checkLicenseNCE(in_machine, isVM);
				}else{
					result = proxy.checkLicense(LicenseDef.UDP_CLIENT_TYPE.UDP_WINDOWS_AGENT, in_machine, in_feature);
				}
				if(result!=null){
					logger.info("checkCentrialLicenseFromEdge return: " +result.getLicense().getCode()+" "+result.getLicense().getDisplayName()+" "+result.getLicense().getId());
					convert2ComponentInfo(result, out_info);
					cachLicense= new CachCentralLicense(result);
					cachLicense.flushToDisk(CACHE_LICENSE_DIRECTORY);
				}else{// Console return no license
					if(isWorkStation(in_feature)&&isBackup2RPS(in_machine.getJobType())){
						out_info.setComponentId(LicenseDef.ID_LIC_NCE_LICENSE);
					}
					logger.info("checkCentrialLicenseFromEdge return null. ");
				}
				return CHECK_OK;
			} catch (Exception e) {
				String errorMessage = "Check centrial License From Edge failed. Internal error occurs in Edge side. Fail to Fetch license from Edge:" + e.getMessage();
				logger.info(errorMessage, e);
				convert2ComponentInfo(cachLicense.getLicenseWithLogActivity(in_machine.getJobId()), out_info);
				return CHECK_OK;
			}
		} catch (Throwable t) {
			logger.error("Failed to check centrial license from edge, will check license from catch.", t);
			convert2ComponentInfo(cachLicense.getLicenseWithLogActivity(in_machine.getJobId()), out_info);
			return CHECK_OK;
		}
	}

	private static boolean isVM(long in_feature) {
		long flag = in_feature & LicenseDef.SUBLIC_OS_PM;
		return flag != LicenseDef.SUBLIC_OS_PM;
	}

	private static boolean isNCE(long jobType, long in_feature) {
		if(jobType==MachineInfo.JOBTYPE_BACKUP_DEST_RPS){
			return false;
		}
		long flag = in_feature & LicenseDef.SUBLIC_OS_WORKSTATION;
		return flag == LicenseDef.SUBLIC_OS_WORKSTATION;
	}
	
	private static boolean isBackup2RPS(long jobType) {
		return jobType==MachineInfo.JOBTYPE_BACKUP_DEST_RPS;
	}
	
	private static boolean isWorkStation(long in_feature) {
		long flag = in_feature & LicenseDef.SUBLIC_OS_WORKSTATION;
		return flag == LicenseDef.SUBLIC_OS_WORKSTATION;
	}

	private static void convert2ComponentInfo(LicenseCheckResult result, ComponentInfo out_info) {
		if(result==null||result.getLicense()==null){
			return;
		}
		out_info.setComponentId(result.getLicense().getId());
		out_info.setComponentCode(result.getLicense().getCode());
		out_info.setComponentName(result.getLicense().getDisplayName());
		out_info.setCheckResult(result.getState().ordinal());
	}

	/**
	 * Check whether the node is managed by CPM or not
	 * 
	 * @return true: managed by CPM, false: not managed by CPM
	 */
	public long isManagedByEdge() {
		logger.debug("LicenseUtils: isManagedByEdge Enter ...");

		D2DEdgeRegistration edgeRegInfo = new D2DEdgeRegistration();
//		String edgeWSDL = edgeRegInfo.GetEdgeWSDL();
//		if (edgeWSDL == null) {
//			logger.error("Check D2D License From Edge failed. There is no edge registration flag!");
//			return FLAG_NOT_MANAGED_BY_EDGE;
//		}
		EdgeRegInfo regInfo = edgeRegInfo.getEdgeRegInfo(ApplicationType.CentralManagement);
		if (regInfo==null||regInfo.getEdgeWSDL()==null){
			logger.error("Check D2D License From Edge failed. There is no edge registration flag!");
			return FLAG_NOT_MANAGED_BY_EDGE;
		}
		
		IEdgeCM4D2D proxy = null;
		try {
			proxy = WebServiceFactory.getEdgeService(regInfo.getEdgeWSDL(), IEdgeCM4D2D.class);
		} catch (Exception e) {
			String errorMessage = "Failed to establish connection to Edge Server, We assume this node is managed by Edge and try to check from cache.";
			if (logger.isDebugEnabled()) {
				logger.debug(errorMessage, e);
			}
			return FLAG_MANAGED_BY_EDGE;
		}

		try {
			proxy.validateUserByUUID(regInfo.getEdgeUUID());
			String flashGuid = CommonService.getInstance().getNodeUUID();
			if (!proxy.isManagedByEdge(flashGuid)) {
				logger.info("There is edge registry file, but edge return not managed by it.");
				return FLAG_NOT_MANAGED_BY_EDGE;
			}
		}catch (EdgeServiceFault e){
			String errorMessage = "Login Edge Server:" + regInfo.getEdgeHostName() + " Failed.";
			logger.error(errorMessage, e);
			return FLAG_INVALID_CPM_UUID;
		}catch (Exception e) {
			String errorMessage = "connect to Edge Server:" + regInfo.getEdgeHostName() + " Failed. We assume this node is managed by Edge and try to check from cache.";
			logger.error(errorMessage, e);
			return FLAG_MANAGED_BY_EDGE;
		}
		
		return FLAG_MANAGED_BY_EDGE;
	}
}
