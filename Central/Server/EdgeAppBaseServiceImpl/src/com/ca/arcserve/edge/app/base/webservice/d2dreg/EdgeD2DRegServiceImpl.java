package com.ca.arcserve.edge.app.base.webservice.d2dreg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.VersionInfo;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.d2dreg.EdgeRegInfo;
import com.ca.arcflash.webservice.edge.d2dreg.EdgeRegistrationReturnCode;
import com.ca.arcflash.webservice.edge.policymanagement.policyapplyers.BasePolicyApplyer;
import com.ca.arcserve.edge.app.base.appdaos.EdgeConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHostPolicyMap;
import com.ca.arcserve.edge.app.base.appdaos.EdgePolicy;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.common.ConsoleUrlUtil;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.common.connection.D2DConnection;
import com.ca.arcserve.edge.app.base.common.connection.DefaultConnectionContextProvider;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.IActivityLogService;
import com.ca.arcserve.edge.app.base.webservice.IEdgeD2DRegService;
import com.ca.arcserve.edge.app.base.webservice.WebServiceFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ConnectionContext;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ItemOperationResult;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogAddEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.AssignPolicyResultCodes;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.vSphere.ProxyConnectInfo;
import com.ca.arcserve.edge.app.base.webservice.exception.NodeExceptionUtil;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl;

public class EdgeD2DRegServiceImpl implements IEdgeD2DRegService {
	
	private static Logger logger = Logger.getLogger(EdgeD2DRegServiceImpl.class);
	
	private IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
	private IEdgeGatewayLocalService gatewayService;
	private IActivityLogService activityLogSvc = new ActivityLogServiceImpl();
	
	private IEdgeGatewayLocalService getGatewayService()
	{
		if (this.gatewayService == null)
			this.gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
		return this.gatewayService;
	}

	public EdgeConnectInfo tryConnectD2D(ConnectionContext d2dConnectionContext) throws EdgeServiceFault {
		EdgeConnectInfo d2DconnectInfo = new EdgeConnectInfo();
		VersionInfo d2dVersion;
		
		try (D2DConnection connection = connectionFactory.createD2DConnection(new DefaultConnectionContextProvider(d2dConnectionContext))) {
			connection.connect();
			
			d2dVersion = connection.getService().getVersionInfo();
			
			String d2dNodeUuid = connection.getNodeUuid();
			d2DconnectInfo.setUuid(d2dNodeUuid);
			
			EdgeRegInfo edgeInfo = connection.getService().getEdgeRegInfo(CommonUtil.getApplicationTypeForD2D());
			if (edgeInfo != null) {
				d2DconnectInfo.setRhostname(edgeInfo.getEdgeHostName());
			}
		}
		
		d2DconnectInfo.setBuildnumber(d2dVersion.getBuildNumber());
		d2DconnectInfo.setMajorversion(d2dVersion.getMajorVersion());
		d2DconnectInfo.setMinorversion(d2dVersion.getMinorVersion());
		d2DconnectInfo.setUpdateversionnumber(d2dVersion.getUpdateNumber());
		d2DconnectInfo.setProductType(d2dVersion.getProductType());
		d2DconnectInfo.setOsName(d2dVersion.getOsName());
		d2DconnectInfo.setD2dInstalledByReg(d2dVersion.isD2DInstalled());
		d2DconnectInfo.setSqlServerByReg(d2dVersion.isSQLServerInstalled());
		d2DconnectInfo.setMsExchangeByReg(d2dVersion.isExchangeInstalled());
		d2DconnectInfo.setPort(d2dConnectionContext.getPort());
		d2DconnectInfo.setProtocol(d2dConnectionContext.getProtocol().equalsIgnoreCase("https") ? Protocol.Https.ordinal() : Protocol.Http.ordinal());
		d2DconnectInfo.setRpsInstalledByReg(d2dVersion.isRPSInstalled());
		
		return d2DconnectInfo;
	}
	
	private void UpdateTimeZone(int hostId, TimeZone timeZone) {
		try {
			IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
			hostMgrDao.as_edge_host_update_timezone_by_id(hostId,
					timeZone.getRawOffset()/(1000*60));
		} catch (Exception e){
			logger.debug("Update TimeZone Failed");
			logger.debug(e + "as_edge_connect_info_update() failed!");
			return;
		}
		return;
	}
	
	@Override
	public void UpdateRegInfoToProxy(ProxyConnectInfo proxyConnectInfo, boolean forceFlag) throws EdgeServiceFault {
		String edgeHostName = EdgeCommonUtil.getLocalFqdnName();
		int edgePort = EdgeCommonUtil.getEdgeWebServicePort();
		String edgeProtocol = EdgeCommonUtil.getEdgeWebServiceProtocol();
		String edgeWSDL = WebServiceFactory.getGateWayWSDL(edgeHostName, edgePort, edgeProtocol);

		ConnectionContext context = new ConnectionContext(proxyConnectInfo.getProtocol(), proxyConnectInfo.getHostName(), proxyConnectInfo.getPort());
		context.buildCredential(proxyConnectInfo.getUsername(), proxyConnectInfo.getPassword(), proxyConnectInfo.getDomain());
		GatewayEntity gateway = getGatewayService().getGatewayById(proxyConnectInfo.getGatewayId());
		context.setGateway(gateway);
		
		try (D2DConnection connection = connectionFactory.createD2DConnection(new DefaultConnectionContextProvider(context))) {
			connection.connect();
			
			String consoleUrl = EdgeCommonUtil.getConsoleUrl( edgeHostName, edgePort, edgeProtocol );
			
			EdgeRegInfo edgeRegInfo = new EdgeRegInfo();
			edgeRegInfo.setEdgeConnectNameList(CommonUtil.getConnectNameList());
			edgeRegInfo.setEdgeAppType(ApplicationType.CentralManagement);
			edgeRegInfo.setEdgeHostName(edgeHostName);
			edgeRegInfo.setEdgeLocale(Locale.getDefault().toString());
			edgeRegInfo.setEdgeUUID(CommonUtil.retrieveCurrentAppUUID());
			edgeRegInfo.setEdgeWSDL(edgeWSDL);
			edgeRegInfo.setRegHostName(proxyConnectInfo.getHostName());
			edgeRegInfo.setConsoleUrl( consoleUrl );
			String rtnCode = "";
			try {
				logger.info("[EdgeD2DRegServiceImpl] UpdateRegInfoToD2D "+forceFlag+" "+proxyConnectInfo.getHostName());
				rtnCode = connection.getService().register4Console(edgeRegInfo,forceFlag);
			} catch (Exception e) {
				logger.error("[EdgeD2DRegServiceImpl] UpdateRegInfoToProxy() invoke register4Console failed.",e);
				if(e instanceof SOAPFaultException){// If Proxy is lower version, not contains this method, then invoke old API
					try {
						logger.info("[EdgeD2DRegServiceImpl] UpdateRegInfoToProxy() will invoke the old interface D2DRegister4Edge");
						rtnCode = connection.getService().D2DRegister4Edge(
								edgeRegInfo.getEdgeUUID(), "", CommonUtil.getApplicationTypeForD2D(), 
								edgeHostName, edgeWSDL, edgeRegInfo.getEdgeLocale(), forceFlag, proxyConnectInfo.getHostName());
					} catch (WebServiceException e2) {
						NodeExceptionUtil.convertWebServiceException(e2, context, 
								NodeExceptionUtil.getNodeMessageSubject(), NodeExceptionUtil.getAgentServiceNames());
					}
				}else {
					NodeExceptionUtil.convertWebServiceException(e, context, 
							NodeExceptionUtil.getNodeMessageSubject(), NodeExceptionUtil.getAgentServiceNames());
				}
			}
			
			Integer result = 0;
			if(StringUtil.isEmptyOrNull(rtnCode)){
				logger.error("[EdgeD2DRegServiceImpl] UpdateRegInfoToProxy(): invoke register4Console return code failed,rtnCode is empty or null");
			}else {
				result = Integer.valueOf(rtnCode);
			}

			if (result == EdgeRegistrationReturnCode.REG_ERROR_CODE_FATAL_ERROR) {
				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_D2D_Reg_Fatal_Error, "Call D2D interface D2DRegister4Edge() failed");
			} else if (result == EdgeRegistrationReturnCode.REG_ERROR_CODE_REGISTERED_BY_OTHER_EDGE) {
				EdgeRegInfo edgeInfo = connection.getService().getEdgeRegInfo(CommonUtil.getApplicationTypeForD2D());
				String otherEdge = edgeInfo != null ? edgeInfo.getEdgeHostName() : "Unknown";
				String consoleName = ConsoleUrlUtil.getConsoleHostName(edgeInfo.getConsoleUrl());
				if(!StringUtil.isEmptyOrNull(consoleName))
					otherEdge = consoleName;				
				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_D2D_Reg_Duplicate, otherEdge);
			} else if (result == EdgeRegistrationReturnCode.REG_ERROR_CODE_EDGE_CONNECTION_FAILURE) {
				String gatewayHostName = EdgeCommonUtil.getLocalFqdnName();
				if(gateway.getHostName()!=null
						&&!gateway.isLocal()){
					gatewayHostName = gateway.getHostName();
				}
				EdgeServiceFaultBean bean = new EdgeServiceFaultBean(EdgeServiceErrorCode.Node_D2D_Reg_D2D_CANNOT_CONNECT_EDGE, "D2D service cannot connect to this Edge service");
				bean.setMessageParameters(new String[]{gatewayHostName});
				throw new EdgeServiceFault(bean);
			}
		}
	}

	private int getDeployedD2DPolicyIdByHostId(int d2dHostId) {
		if(d2dHostId == 0)
			return -1;
		int policyId = -1;
		try {
			IEdgePolicyDao policyDao = DaoFactory.getDao(IEdgePolicyDao.class);
			List<EdgeHostPolicyMap> theMaps = new ArrayList<EdgeHostPolicyMap>();
			policyDao.getHostPolicyMap(d2dHostId, PolicyManagementServiceImpl.getPolicyTypeByApplicationType(), theMaps);
			Iterator<EdgeHostPolicyMap> iterMap = theMaps.iterator();
			if(iterMap.hasNext()) {
				EdgeHostPolicyMap policyMap = iterMap.next();
				if( PolicyDeployStatus.DeployedSuccessfully == policyMap.getDeployStatus() ) {
					policyId = policyMap.getPolicyId();
				}
			}
		}catch(Throwable t) {
			logger.error(t.toString());
		}
		return policyId;
	}
	
	private String getDeployedD2DPolicyUuidByPolicyId(int policyId) {
		String policyUuid = "";
		try {
			IEdgePolicyDao policyDao = DaoFactory.getDao(IEdgePolicyDao.class);
			List<EdgePolicy> thePolicys = new ArrayList<EdgePolicy>();
			policyDao.as_edge_policy_list(policyId, 0, thePolicys);
			Iterator<EdgePolicy> iterPolicy = thePolicys.iterator();
			if(iterPolicy.hasNext()) {
				policyUuid = iterPolicy.next().getUuid();
			}
		}catch(Throwable t) {
			logger.error(t.toString());
		}
		
		return policyUuid;
	}
	
	private boolean lanuchReDeployPolicy(int d2dHostId, int policyId) {
		boolean result = true;
		
		try {
			PolicyManagementServiceImpl svcImpl = new PolicyManagementServiceImpl();
			List<Integer> nodeIdList = new ArrayList<Integer>();
			nodeIdList.add(d2dHostId);
			List<ItemOperationResult> resultLst = svcImpl.redeployPolicyToNodes(nodeIdList, PolicyManagementServiceImpl.getPolicyTypeByApplicationType(), policyId);
			if(resultLst != null && resultLst.size() > 0 && resultLst.get(0).getResultCode() != AssignPolicyResultCodes.Successful) {
				logger.debug("lanuchReDeployPolicy() failed");
				result = false;
			}
		}catch(Throwable t) {
			logger.debug("lanuchReDeployPolicy() failed - got exception:");
			logger.debug(t.toString());
			result = false;
		}
		return result;
	}
	
	private void writeActivityLog(int hostId, Severity severity, String messageKey) throws EdgeServiceFault {
		String message = EdgeCMWebServiceMessages.getResource(messageKey);
		LogAddEntity entity = LogAddEntity.create(severity, hostId, message);
		activityLogSvc.addUnifiedLog(entity);
	}
	
	@Override
	public void UpdateRegInfoToD2D(ConnectionContext connectionContext, int d2dHostId, boolean forceFlag) throws EdgeServiceFault {
		if(d2dHostId == 0 && connectionContext == null)
			return;
		
		String edgeHostName = EdgeCommonUtil.getLocalFqdnName();
		String edgeProtocol = EdgeCommonUtil.getEdgeWebServiceProtocol();
		int edgePort = EdgeCommonUtil.getEdgeWebServicePort();
		String edgeWSDL = WebServiceFactory.getGateWayWSDL(edgeHostName, edgePort, edgeProtocol);
		logger.debug("UpdateRegInfoToD2D(): edgeWSDL = " + edgeWSDL);
		
		D2DConnection connection = null;
		if(d2dHostId != 0){
			connection = connectionFactory.createD2DConnection(d2dHostId);
		}else {
			connection = connectionFactory.createD2DConnection(new DefaultConnectionContextProvider(connectionContext));
		}
		try{
			connection.connect();
			
			int policyId = getDeployedD2DPolicyIdByHostId(d2dHostId);
			String policyUuid = "";
			if (policyId != -1) {
				policyUuid = getDeployedD2DPolicyUuidByPolicyId(policyId);
			}

			String consoleUrl = EdgeCommonUtil.getConsoleUrl( edgeHostName, edgePort, edgeProtocol );
			String d2dHost = connection.getClientProxy().getHost();
			
			EdgeRegInfo edgeRegInfo = new EdgeRegInfo();
			edgeRegInfo.setEdgeConnectNameList(CommonUtil.getConnectNameList());
			edgeRegInfo.setEdgeAppType(ApplicationType.CentralManagement);
			edgeRegInfo.setEdgeHostName(edgeHostName);
			edgeRegInfo.setEdgeLocale(Locale.getDefault().toString());
			edgeRegInfo.setEdgeUUID(CommonUtil.retrieveCurrentAppUUID());
			edgeRegInfo.setEdgeWSDL(edgeWSDL);
//			edgeRegInfo.getPolicyUuids().put("Default", policyUuid);	// don't check policy uuid, set it to null as it doesn't important, but this check would make agent auto deploy after upgrade console
			edgeRegInfo.setRegHostName(d2dHost);
			edgeRegInfo.setConsoleUrl( consoleUrl );
			
			String rtnCode = "";
			try {
				logger.info("[EdgeD2DRegServiceImpl] UpdateRegInfoToD2D "+forceFlag+" "+d2dHostId+" "+d2dHost+" plan:"+policyUuid);
				rtnCode = connection.getService().register4Console(edgeRegInfo,forceFlag);
			} catch (Exception e) {
				if(e instanceof SOAPFaultException ){ // If Agent is lower version, not contains this method, then invoke old API
					try {
						logger.info("[EdgeD2DRegServiceImpl] UpdateRegInfoToD2D() will invoke the old interface D2DRegister4Edge");
						rtnCode = connection.getService().D2DRegister4Edge(	// add BasePolicyApplyer.POLICYVERSION to compact with old d2d code
								edgeRegInfo.getEdgeUUID(), null, CommonUtil.getApplicationTypeForD2D(), edgeHostName, edgeWSDL, edgeRegInfo.getEdgeLocale(), forceFlag, d2dHost);
						// don't check policy uuid, set it to null as it doesn't important, but this check would make agent auto deploy after upgrade console
					} catch (WebServiceException ex) {
						NodeExceptionUtil.convertWebServiceException(ex, connectionContext, 
								NodeExceptionUtil.getNodeMessageSubject(), NodeExceptionUtil.getAgentServiceNames());
					}
				}else{
					logger.error("[EdgeD2DRegServiceImpl] UpdateRegInfoToD2D() invoke register4Console failed.",e);
				}
			}
			int result = 0;
			if(!StringUtil.isEmptyOrNull(rtnCode)){
				result = Integer.valueOf(rtnCode);
			}else {
				logger.error("[EdgeD2DRegServiceImpl]:UpdateRegInfoToD2D(), invoke connection.getService().register4Console return invalid result:"+rtnCode);
			}
			if (result == EdgeRegistrationReturnCode.REG_ERROR_CODE_SUCCEED 
					|| result == EdgeRegistrationReturnCode.REG_ERROR_CODE_REGISTERED_BY_SAME_EDGE
					|| result == EdgeRegistrationReturnCode.REG_ERROR_CODE_REGISTERED_NeedRedeploy) {
				String timeZoneString = connection.getService().QueryD2DTimeZoneID();
				TimeZone d2dTimeZone = TimeZone.getTimeZone(timeZoneString);
				UpdateTimeZone(d2dHostId, d2dTimeZone);

				//20110523 policy is inconsistent between Edge and D2D, need to re-deploy policy to the D2D node
				if (result == EdgeRegistrationReturnCode.REG_ERROR_CODE_REGISTERED_NeedRedeploy) {
					if (!lanuchReDeployPolicy(d2dHostId, policyId)) {
						logger.debug("lanuch policy redeployment task failed. (d2D node: " + d2dHostId + ")");
						writeActivityLog(d2dHostId, Severity.Error, "policyDeployment_LanuchReDeployment_Failure");
					}
				}
			} else if (result == EdgeRegistrationReturnCode.REG_ERROR_CODE_FATAL_ERROR) {
				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_D2D_Reg_Fatal_Error, "Call D2D interface D2DRegister4Edge() failed");
			} else if (result == EdgeRegistrationReturnCode.REG_ERROR_CODE_REGISTERED_BY_SAME_EDGE) {
				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_D2D_Reg_Again, "Registered already. update it again");
			} else if (result == EdgeRegistrationReturnCode.REG_ERROR_CODE_REGISTERED_BY_OTHER_EDGE) {
				StringBuilder message = new StringBuilder("Registered by another Console. Cannot update");
				EdgeRegInfo edgeInfo = connection.getService().getEdgeRegInfo(CommonUtil.getApplicationTypeForD2D());
				if (edgeInfo != null) {
					String consoleName = ConsoleUrlUtil.getConsoleHostName(edgeInfo.getConsoleUrl());
					if(!StringUtil.isEmptyOrNull(consoleName))
						message.append(" ^" + consoleName);	
					else
						message.append(" ^" + edgeInfo.getEdgeHostName());
				}				
				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_D2D_Reg_Duplicate, message.toString());
			} else if(result == EdgeRegistrationReturnCode.REG_ERROR_CODE_EDGE_CONNECTION_FAILURE) {
				String gatewayHostName = EdgeCommonUtil.getLocalFqdnName();
				if(d2dHostId > 0){
					gatewayHostName = EdgeCommonUtil.getGatewayHostNameByNodeId(d2dHostId);
				}else if(connectionContext != null && connectionContext.getGateway()!= null 
						&&connectionContext.getGateway().getHostName()!=null
						&&!connectionContext.getGateway().isLocal()){
					gatewayHostName = connectionContext.getGateway().getHostName();
				}
				EdgeServiceFaultBean bean = new EdgeServiceFaultBean(EdgeServiceErrorCode.Node_D2D_Reg_D2D_CANNOT_CONNECT_EDGE, "D2D service cannot connect to this Edge service");
				bean.setMessageParameters(new String[]{gatewayHostName});
				throw new EdgeServiceFault(bean);
			}
		}catch(Exception e){
			throw e;
		}
	}

	@Override
	public void RemoveRegInfoFromD2D(int d2dHostId, boolean forceFlag) throws EdgeServiceFault {
		String edgeHostName = EdgeCommonUtil.getLocalFqdnName();
		String edgeUUID = CommonUtil.retrieveCurrentAppUUID();

		try (D2DConnection connection = connectionFactory.createD2DConnection(d2dHostId)) {
			connection.connect();

			int result = connection.getService().D2DUnRegister4Edge(
					edgeUUID, CommonUtil.getApplicationTypeForD2D(), edgeHostName, forceFlag);

			if (result == -1) {
				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_D2D_Reg_Fatal_Error, "Call D2D interface D2DUnRegister4Edge() failed");
			} else if (result == 0) {
				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_D2D_UnReg_Not_Exist, "Don't need remove since it's not registered");
			} else if (result == 2) {
				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_D2D_UnReg_Not_Owner, "Registered by another Edge Server. Cannot remove it");
			}
		}
	}

}
