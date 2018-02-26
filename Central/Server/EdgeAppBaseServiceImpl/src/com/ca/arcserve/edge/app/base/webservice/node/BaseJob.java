package com.ca.arcserve.edge.app.base.webservice.node;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.EdgeHostPolicyMap;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.D2DWebServiceErrorMessages;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.INodeService;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeSortOrder;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeNodeFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Node;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodePagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodePagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeSortCol;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.ParsedBackupPolicy;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyTypes;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl;

public abstract class BaseJob {
	private static final Logger logger = Logger.getLogger(BaseJob.class);
	
	protected static ActivityLogServiceImpl logService = new ActivityLogServiceImpl();
	protected static IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	protected static IEdgePolicyDao policyDao = DaoFactory.getDao(IEdgePolicyDao.class);
	
	protected void generateLog(Severity severity, Module module, Node node, String message) {
		ActivityLog log = new ActivityLog();
		if (node.getHostname()==null || node.getHostname().isEmpty())
			log.setNodeName(EdgeCMWebServiceMessages.getResource( "policyDeployment_UnknownNode" ));
		else
			log.setNodeName(node.getHostname());
		log.setModule(module);
		log.setSeverity(severity);
		log.setTime(new Date());
		log.setMessage(message);
		
		try {
			logService.addLog(log);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	protected NodeDetail getNodeDetailWithPolicy(INodeService service, int nodeID) throws Exception{
		NodeDetail nodeDetail = service.getNodeDetailInformation(nodeID);
		
		List<EdgeHostPolicyMap> map = new  LinkedList<EdgeHostPolicyMap>();
		
		int policyType = PolicyTypes.VCM;
		if (nodeDetail.isImportedFromRHA()) {
			policyType = PolicyTypes.RemoteVCM;
		}
		policyDao.getHostPolicyMap(nodeID, policyType, map);
		
		if (map.size()>0){
			EdgeHostPolicyMap firstMap = map.get(0);
			ParsedBackupPolicy policy = PolicyManagementServiceImpl.getInstance().getParsedBackupPolicy(firstMap.getPolicyId());
			nodeDetail.setPolicyName(policy.getGeneralInfo().getName());
			nodeDetail.setPolicyDeployStatus( firstMap.getDeployStatus() );
			nodeDetail.setLastSuccessfulPolicyDeploy( firstMap.getLastSuccDeploy() );
			nodeDetail.setPolicyIDForEsx(firstMap.getPolicyId());
			nodeDetail.setVcmSettings(policy.getVcmSettings());
		}
		
		return nodeDetail;
	}
	
	protected String getD2DErrorMessage(String errorCode){
		return D2DWebServiceErrorMessages.getMessage(errorCode);
	}
	
	protected int[] getNodeIDByGroup(INodeService nodeService, int groupID, int groupType) {
		EdgeNodeFilter nodeFilter = new EdgeNodeFilter();
		List<Node> nodeList = null;
		NodePagingConfig pagingConfig = new NodePagingConfig();
		
		pagingConfig.setOrderCol(NodeSortCol.hostname);
		pagingConfig.setOrderType(EdgeSortOrder.ASC);
		pagingConfig.setPagesize(Integer.MAX_VALUE);
		pagingConfig.setStartpos(0);
		
		
		try {
			NodePagingResult result = nodeService.getNodesESXByGroupAndTypePaging(groupID, groupType, nodeFilter, pagingConfig);
			nodeList = result.getData();
		} catch (EdgeServiceFault e) {
			logger.error(e.getMessage(), e);
			return new int[0];
		}
		
		int[] ids = new int[nodeList.size()];
		for(int i=0;i<nodeList.size();i++)
			ids[i] = nodeList.get(i).getId();
		
		return ids;
	}
}
