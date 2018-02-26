package com.ca.arcserve.edge.app.base.webservice.policymanagement;

import java.util.ArrayList;
import java.util.List;

import javax.xml.ws.WebServiceException;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.common.connection.LinuxD2DConnection;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.common.D2DRole;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.LinuxBackupLocationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl.D2DConnectInfo;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl.GetD2DConnectInfoException;
import com.ca.arcserve.linuximaging.webservice.ILinuximagingService;
import com.ca.arcserve.linuximaging.webservice.data.BackupLocationInfo;

public class LinuxPlanManagmentHelper {
	private static IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
	protected static final Logger logger = Logger.getLogger( LinuxPlanManagmentHelper.class );
	public static int saveLinuxUnifiedPolicy(PolicyManagementServiceImpl serviceImpl,UnifiedPolicy policy, boolean planContentChanged) throws EdgeServiceFault{
		return saveLinuxUnifiedPolicy(serviceImpl, policy, planContentChanged, true);
	}
	
	public static int saveLinuxUnifiedPolicy(PolicyManagementServiceImpl serviceImpl,UnifiedPolicy policy, boolean planContentChanged, boolean isNeedtoDeploy) throws EdgeServiceFault{
		int newPolicyId = serviceImpl.saveUnifiedPolicy(policy, planContentChanged);
		policy.setId(newPolicyId);
		serviceImpl.assignUnifiedPolicy(policy, policy.getProtectedResources(),isNeedtoDeploy);
		
		serviceImpl.getEdgePolicyDao().as_edge_policy_AddD2DRole(newPolicyId,policy.getLinuxBackupsetting().getLinuxD2DServerId(),D2DRole.LinuxD2D);
		return newPolicyId;
	}

	public static int updateLinuxUnifiedPolicy(PolicyManagementServiceImpl serviceImpl,UnifiedPolicy oldPolicy,UnifiedPolicy newPolicy, boolean planContentChanged) throws EdgeServiceFault{
		if(oldPolicy.getLinuxBackupsetting().getLinuxD2DServerId() != newPolicy.getLinuxBackupsetting().getLinuxD2DServerId()){
			removePlanInOriginalServer(serviceImpl,oldPolicy.getLinuxBackupsetting().getLinuxD2DServerId(),oldPolicy.getId());
			
		}
		int policyId = serviceImpl.saveUnifiedPolicy(newPolicy, planContentChanged);
		serviceImpl.getEdgePolicyDao().as_edge_policy_AddD2DRole(newPolicy.getId(),newPolicy.getLinuxBackupsetting().getLinuxD2DServerId(),D2DRole.LinuxD2D);
		return policyId;
	}
	
	private static void removePlanInOriginalServer(PolicyManagementServiceImpl serviceImpl,int d2dserverId,int policyId){
		ILinuximagingService service = null;
		D2DConnectInfo d2dServer = null;
		try {
			d2dServer = serviceImpl.getD2DConnectInfo(d2dserverId);
		} catch (GetD2DConnectInfoException e2) {
			logger.error("Failed to get linux D2D server information",e2);
		}
		try(LinuxD2DConnection connection = connectionFactory.createLinuxD2DConnection(d2dServer.getHostId())){
			connection.connect();
			service = connection.getService();
			int ret = service.validateByKey(d2dServer.getAuthUuid());
			if(ret == 0){
				try{
					ret = service.deletePlan(policyId);
					if(ret != 0 ){
						logger.error("Failed to remove plan in original d2d server ret:" + ret);
					}
				}catch(Exception e){
					logger.error("Failed to remove plan in original d2d server",e);
				}
			}
		}catch(WebServiceException e){
			logger.error("cannot connect to Linux D2D service",e);
		} catch (EdgeServiceFault e1) {
			logger.error(e1);
		}
	}
	
	public static List<String> getPrepostScriptList(PolicyManagementServiceImpl serviceImpl,int linuxD2DServerId)throws EdgeServiceFault {
		List<String> scriptList = new ArrayList<String>();
		ILinuximagingService service = null;
		D2DConnectInfo d2dServer = null;
		try{
			d2dServer = serviceImpl.getD2DConnectInfo(linuxD2DServerId);
		} catch (GetD2DConnectInfoException e1) {
			logger.error("Failed to get linux D2D server information",e1);
		}
		try(LinuxD2DConnection connection = connectionFactory.createLinuxD2DConnection(d2dServer.getHostId())){
			connection.connect();
			service = connection.getService();
			int ret = service.validateByKey(d2dServer.getAuthUuid());
			if(ret == 0){
				try{
					scriptList = service.getScripts(2);
				}catch(Exception e){
					logger.error("Failed to get prepos script",e);
				}
			}
		}catch(WebServiceException e){
			logger.error("cannot connect to Linux D2D service",e);
		}
		return scriptList;
	}
	
	public static boolean validateBackupLocation(PolicyManagementServiceImpl serviceImpl,int linuxD2DServerId,LinuxBackupLocationInfo locationInfo){
		boolean isValide = false;
		D2DConnectInfo d2dServer = null;
		try{
			d2dServer = serviceImpl.getD2DConnectInfo(linuxD2DServerId);
		} catch (GetD2DConnectInfoException e1) {
			logger.error("Failed to get linux D2D server information",e1);
		}
		try(LinuxD2DConnection connection = connectionFactory.createLinuxD2DConnection(d2dServer.getHostId())){
			connection.connect();
			ILinuximagingService service = connection.getService();
			
			int ret = service.validateByKey(d2dServer.getAuthUuid());
			if(ret == 0){
				try{
					BackupLocationInfo info = new BackupLocationInfo();
					info.setBackupDestLocation(locationInfo.getBackupDestLocation());
					info.setBackupDestPasswd(locationInfo.getBackupDestPasswd());
					info.setBackupDestUser(locationInfo.getBackupDestPasswd());
					info.setType(locationInfo.getType());
					isValide = service.validateBackupLocation(info);
				}catch(Exception e){
					logger.error(e);
				}
			}
		}catch(WebServiceException e){
			logger.error("cannot connect to Linux D2D service",e);
		} catch (EdgeServiceFault e1) {
			logger.error(e1);
		}
		return isValide;
	}
	
}
