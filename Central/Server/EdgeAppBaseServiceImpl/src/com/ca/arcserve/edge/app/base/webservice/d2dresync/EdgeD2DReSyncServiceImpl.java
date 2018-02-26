package com.ca.arcserve.edge.app.base.webservice.d2dresync;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.edge.d2dreg.EdgeRegInfo;
import com.ca.arcflash.webservice.foredge.exception.D2DSyncErrorCode;
import com.ca.arcserve.edge.app.base.appdaos.EdgeConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.common.ApplicationUtil;
import com.ca.arcserve.edge.app.base.common.ConsoleUrlUtil;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.common.connection.D2DConnection;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.common.connection.LinuxD2DConnection;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.schedulers.EdgeExecutors;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.IEdgeD2DReSyncService;
import com.ca.arcserve.edge.app.base.webservice.client.BaseWebServiceFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeSortOrder;
import com.ca.arcserve.edge.app.base.webservice.contract.common.HostTypeUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeNodeFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Node;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeManagedStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodePagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodePagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeSortCol;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ProtectionType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyInfo;
import com.ca.arcserve.edge.app.base.webservice.d2ddatasync.D2DBackupDataSynchronizer;
import com.ca.arcserve.edge.app.base.webservice.d2ddatasync.D2DBaseXmlParser;
import com.ca.arcserve.edge.app.base.webservice.d2ddatasync.D2DSyncMessage;
import com.ca.arcserve.edge.app.base.webservice.d2ddatasync.D2DSyncTaskIDMap;
import com.ca.arcserve.linuximaging.webservice.ILinuximagingService;

public class EdgeD2DReSyncServiceImpl implements IEdgeD2DReSyncService {
	private static final Logger logger = Logger.getLogger(EdgeD2DReSyncServiceImpl.class);
	private static Map<Integer, String> d2dHostList = new HashMap<Integer, String>();
	public static final int DEFAULT_RESYNC_TIMEOUT = 18000000; // half hour
	private EdgeWebServiceImpl serviceImpl;
	private static final IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	
	private IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);

	private static synchronized boolean getD2DReSyncThreadLock(int d2dHostId){
		String runningStatus = d2dHostList.get(d2dHostId);
		if(runningStatus == null)
		{
			d2dHostList.put(d2dHostId, "running");
		    return true;
		}
		else
		    return false;
	}

	private static synchronized void releaseD2DReSyncThreadLock(int d2dHostId){
		if(d2dHostList.containsKey(d2dHostId))
			d2dHostList.remove(d2dHostId);
	}

	private int startD2DReSyncThread(int d2dHostId) {
		logger.info("startD2DReSyncThread(" + d2dHostId + ") - start");

		try {
			if(getD2DReSyncThreadLock(d2dHostId) == false)
			{
				logger.error("There is already one resync thread for this d2d node(histid:" + d2dHostId + ") Exit!");
				return 1;
			}
			
			EdgeExecutors.getFixedPool().submit(new D2DReSyncThread(d2dHostId)).get();
		}catch (InterruptedException e) {
			logger.error("Got InterruptedException: ");
			logger.error(e.toString());
		}catch(Throwable te) {
			logger.error("Got exception: ");
			logger.error(te.toString());
		}

		logger.info("startD2DReSyncThread(" + d2dHostId + ") - end");
		return 0;
	}

	private class D2DReSyncThread implements Runnable {
		private int _d2dHostId = 0;
		private String d2dHost = "";
		private int d2dPort = 0;
		private String d2dProtocol = "";
		private String localName = "";
		private boolean linuxd2d_server = false;
		private boolean linuxd2d_node = false;
		public D2DReSyncThread(int d2dHostId){
			_d2dHostId = d2dHostId;
		}
		@Override
		public void run() {
			try {
				D2DSyncTaskIDMap.getNextTaskId(_d2dHostId);
				
				int ret = EdgeD2DReSync(_d2dHostId);
				if(ret == D2DSyncErrorCode.D2D_SYNC_RESYNC_NOT_MANAGED) {
					ReMarkD2DNodeUnManaged(_d2dHostId);
					logger.error("Can not start the resync since D2D node is not managed by this Edge server currently!");
				}
				else if(ret != D2DSyncErrorCode.D2D_SYNC_SUCCEED){
					logger.error("Submit full Sync failed!! \n");
				}
				else{
					logger.info("Submit full sync finished!! \n");
				}
	
				D2DBackupDataSynchronizer syncer = new D2DBackupDataSynchronizer();
	
				if(ret == D2DSyncErrorCode.D2D_SYNC_SUCCEED) {
					ret = syncer.UpdateSyncHistory(_d2dHostId, 0,
							D2DBackupDataSynchronizer.SYNC_STATUS_SUCCEED);
				}
				else if(ret != D2DSyncErrorCode.D2D_SYNC_RESYNC_NOT_MANAGED) {
					ret = syncer.UpdateSyncHistory(_d2dHostId, 0,
							D2DBackupDataSynchronizer.SYNC_STATUS_FAILED);
				}
			}catch(Throwable t) {
				logger.error(t.toString());
			}finally {
				releaseD2DReSyncThreadLock(_d2dHostId);
			}
		}

		private void ReMarkD2DNodeUnManaged(int d2dHostId) {
			EdgeCommonUtil.changeNodeManagedStatus(d2dHostId, NodeManagedStatus.Unmanaged);
			return;
		}

		private int checkD2DRegStatus(D2DConnection connection, long theTaskId) {
			localName = EdgeCommonUtil.getLocalFqdnName();
			String edgeUUID = CommonUtil.retrieveCurrentAppUUID();

			int result = connection.getService().QueryEdgeMgrStatus(edgeUUID, CommonUtil.getApplicationTypeForD2D(), localName);
			if (result == 2) {
				EdgeRegInfo edgeInfo = connection.getService().getEdgeRegInfo(CommonUtil.getApplicationTypeForD2D());
				String CurRegisteredEdgeHostName = edgeInfo == null ? "" : edgeInfo.getEdgeHostName();				
				if (edgeInfo != null) {				
					String consoleName = ConsoleUrlUtil.getConsoleHostName(edgeInfo.getConsoleUrl());
					if(!StringUtil.isEmptyOrNull(consoleName))
						CurRegisteredEdgeHostName = consoleName;
				}				
				if (BaseWebServiceFactory.replaceIfLocalHost(CurRegisteredEdgeHostName).equalsIgnoreCase("localhost") == false) {
					D2DBaseXmlParser.writeActivityLog(d2dHost, theTaskId, Severity.Warning,
						D2DSyncMessage.EDGE_D2D_SYNC_RESYNC_MANAGED_BY_OTHER, CurRegisteredEdgeHostName);
				}
				else {
					D2DBaseXmlParser.writeActivityLog(d2dHost, theTaskId, Severity.Warning, 
						D2DSyncMessage.EDGE_D2D_SYNC_RESYNC_MANAGED_BY_OTHER_DETAIL);
				}
				logger.error("The D2D node was managed by other Edge!");
				return D2DSyncErrorCode.D2D_SYNC_RESYNC_NOT_MANAGED;
			}
			else if(result == 0) {
				D2DBaseXmlParser.writeActivityLog(d2dHost, theTaskId, Severity.Warning, 
						D2DSyncMessage.EDGE_D2D_SYNC_RESYNC_NOT_MANAGED);
				logger.error("The D2D node was not managed by any Edge!");
				return D2DSyncErrorCode.D2D_SYNC_RESYNC_NOT_MANAGED;
			}
			else if(result != 1) {
				logger.error("Got unknown error code from CheckD2DRegStatus() - " + result);
				return D2DSyncErrorCode.D2D_SYNC_UNKNOWN_ERROR;
			}
			
			return D2DSyncErrorCode.D2D_SYNC_SUCCEED;
		}
		
		private int resync2Edge(D2DConnection connection, int d2dHostId, long theTaskId) {
			D2DBackupDataSynchronizer syncer = new D2DBackupDataSynchronizer();
			
			int result = syncer.UpdateSyncHistory(d2dHostId, 0,
							D2DBackupDataSynchronizer.SYNC_STATUS_IN_PROGRESS);

			//D2DBaseXmlParser.writeActivityLog(d2dHost, theTaskId, Severity.Information,
			//					D2DSyncMessage.EDGE_D2D_SYNC_RESYNC_START);
			
			result = connection.getService().D2DResync2Edge(localName);
			if(result == D2DSyncErrorCode.D2D_SYNC_SUCCEED) {
				logger.info("Submitting D2D full synchronization to node " + d2dHost + " succeeded!");
				//D2DBaseXmlParser.writeActivityLog(d2dHost, theTaskId, Severity.Information,
						//D2DSyncMessage.EDGE_D2D_SYNC_RESYNC_SUCCEEDED);
			}
			else {
				if(result == D2DSyncErrorCode.D2D_SYNC_START_FULL_SYNC_THREAD_FAILURE) {
					logger.error("D2D cannot start full synchronization thread!");
					D2DBaseXmlParser.writeActivityLog(d2dHost, theTaskId, Severity.Error, D2DSyncMessage.EDGE_D2D_SYNC_START_FULL_SYNC_THREAD_FAILURE);	
				}
				else
				if(result == D2DSyncErrorCode.D2D_SYNC_ANOTHER_FULL_SYNC_THREAD_RUNNING) {
					logger.error("The D2D node is no longer managed by this Edge service!");
					D2DBaseXmlParser.writeActivityLog(d2dHost, theTaskId, Severity.Error, D2DSyncMessage.EDGE_D2D_SYNC_ANOTHER_FULL_SYNC_THREAD_RUNNING);	
				}
				else {
					logger.error("Got unknown error when start D2D full synchronization!");
					D2DBaseXmlParser.writeActivityLog(d2dHost, theTaskId, Severity.Error,D2DSyncMessage.EDGE_D2D_SYNC_RESYNC_UNKNOWN_FAILURE);
				}
			}
			
			return result;
		}
		
		private int EdgeD2DReSync(int d2dHostId) {
			long theTaskId = 0;
			try {
				theTaskId = D2DSyncTaskIDMap.getCurrentTaskId(_d2dHostId);
				
				int result = getConnInfoByHostId(d2dHostId);
				if(result != 0)
				{
					logger.debug("EdgeD2DReSync(): cannot get connect info!! \n");
					return -1;
				}
				
				//for Linux d2d
				if(linuxd2d_server||linuxd2d_node){
					logger.debug("launch linuxd2d resync.");
					return linuxD2DResync2Edge(theTaskId);
				}

				try (D2DConnection connection = connectionFactory.createD2DConnection(d2dHostId)) {
					try {
						connection.connect();
					} catch (Exception e) {
						D2DBaseXmlParser.writeActivityLog(d2dHost, theTaskId, Severity.Warning, 
								D2DSyncMessage.EDGE_D2D_SYNC_RESYNC_LOGIN_FAILED);
						logger.error("Cannot login to the D2D node!");
						return D2DSyncErrorCode.D2D_SYNC_RESYNC_LOGIN_TO_D2D_FAILURE_UUID;
					}
					
					result = checkD2DRegStatus(connection, theTaskId);
					if (result != D2DSyncErrorCode.D2D_SYNC_SUCCEED) {
						return result;
					}
					
					return resync2Edge(connection, d2dHostId, theTaskId);
				}
			}catch(Throwable t) {
				logger.error(t.toString());
				if(t instanceof SOAPFaultException) {// protocol problem due to D2D version doesn't match Edge version
					D2DBaseXmlParser.writeActivityLog(d2dHost, theTaskId, Severity.Warning, D2DSyncMessage.EDGE_D2D_SYNC_RESYNC_CONNECTION_FAILED);
					return D2DSyncErrorCode.D2D_SYNC_COMPATIBILITY_FAILURE;
				}
				else
				if(t instanceof WebServiceException) {// communication problem
					D2DBaseXmlParser.writeActivityLog(d2dHost, theTaskId, Severity.Warning, D2DSyncMessage.EDGE_D2D_SYNC_RESYNC_CONNECTION_FAILED);
					return D2DSyncErrorCode.D2D_SYNC_RESYNC_CONNECT_TO_D2D_FAILURE;
				}	
				else
				if((t.getCause() != null ) && (t.getCause() instanceof ConnectException)) {// communication problem
					D2DBaseXmlParser.writeActivityLog(d2dHost, theTaskId, Severity.Warning, D2DSyncMessage.EDGE_D2D_SYNC_RESYNC_CONNECTION_FAILED);
					return D2DSyncErrorCode.D2D_SYNC_RESYNC_CONNECT_TO_D2D_FAILURE;
				}	
				else {
					D2DBaseXmlParser.writeActivityLog(d2dHost, theTaskId, Severity.Error,D2DSyncMessage.EDGE_D2D_SYNC_RESYNC_UNKNOWN_FAILURE);
					return D2DSyncErrorCode.D2D_SYNC_UNKNOWN_ERROR;
				}
			}
		}
		
		private int linuxD2DResync2Edge(long theTaskId) {
			String edgeUUID = CommonUtil.retrieveCurrentAppUUID();
			logger.debug("LinuxD2DResync2Edge service: "+d2dProtocol+"://"+d2dHost+":"+d2dPort);
			try(LinuxD2DConnection connection = connectionFactory.createLinuxD2DConnection(_d2dHostId)){
				connection.connect();
				ILinuximagingService service = connection.getService();
				int result = service.resyncD2DServer2Edge(edgeUUID);
				logger.info("LinuxD2DResync2Edge taskid["+theTaskId+"] return: "+result);
				parseResyncResultFromLinuxD2D(result, theTaskId);
				return result;
			} catch (EdgeServiceFault e) {
				logger.error("connect linux d2d fail ", e);
			}
			return 0;
		}
		
		private void parseResyncResultFromLinuxD2D(int result, long theTaskId){
			switch(result){
			case D2DSyncErrorCode.D2D_SYNC_SUCCEED:
				logger.info("Submitting Linux D2D full synchronization succeeded!");
				break;
			case D2DSyncErrorCode.D2D_SYNC_UNKNOWN_ERROR:
				logger.error("Got unknown error when start D2D full synchronization!");
				D2DBaseXmlParser.writeActivityLog(d2dHost, theTaskId, Severity.Error,D2DSyncMessage.EDGE_D2D_SYNC_RESYNC_UNKNOWN_FAILURE);
				break;
			case D2DSyncErrorCode.D2D_SYNC_ANOTHER_FULL_SYNC_THREAD_RUNNING:
				logger.error("The D2D node is no longer managed by this Edge service!");
				D2DBaseXmlParser.writeActivityLog(d2dHost, theTaskId, Severity.Error, D2DSyncMessage.EDGE_D2D_SYNC_ANOTHER_FULL_SYNC_THREAD_RUNNING);
				break;
			case D2DSyncErrorCode.D2D_SYNC_START_FULL_SYNC_THREAD_FAILURE:
				logger.error("D2D cannot start full synchronization thread!");
				D2DBaseXmlParser.writeActivityLog(d2dHost, theTaskId, Severity.Error, D2DSyncMessage.EDGE_D2D_SYNC_START_FULL_SYNC_THREAD_FAILURE);
				break;
			case D2DSyncErrorCode.D2D_SYNC_RESYNC_NOT_MANAGED:
				logger.error("The D2D node was not managed!");
				D2DBaseXmlParser.writeActivityLog(d2dHost, theTaskId, Severity.Warning, D2DSyncMessage.EDGE_D2D_SYNC_RESYNC_NOT_MANAGED);
				break;
			default:
				logger.error("Got unknown error when start D2D full synchronization!");
				D2DBaseXmlParser.writeActivityLog(d2dHost, theTaskId, Severity.Error,D2DSyncMessage.EDGE_D2D_SYNC_RESYNC_UNKNOWN_FAILURE);
			}
		}

		private int getConnInfoByHostId(int d2dHostId) {
			IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
			List<EdgeHost> hostLst = new ArrayList<EdgeHost>();
			String ids = "(" + d2dHostId + ")";
			hostMgrDao.as_edge_hosts_list(ids, hostLst);
			EdgeHost edgeHost = null;
			Iterator<EdgeHost> iterHost = hostLst.iterator();
			if (iterHost.hasNext()) {
				edgeHost = iterHost.next();
				logger.debug("EdgeD2DReSyncService get node info: hostname:"+edgeHost.getRhostname()+" | rhostType:"+edgeHost.getRhostType()+" | protectionType:"+edgeHost.getProtectionTypeBitmap());
				d2dHost = edgeHost.getRhostname();
				if((edgeHost.getProtectionTypeBitmap()&ProtectionType.LINUX_D2D_SERVER.getValue())==ProtectionType.LINUX_D2D_SERVER.getValue()){//Linux D2D Server
					linuxd2d_server = true;
				}else if(HostTypeUtil.isLinuxNode(edgeHost.getRhostType())){//Linux D2D Node
					linuxd2d_node = true;
					List<PolicyInfo> policyList = new ArrayList<PolicyInfo>();
					IEdgePolicyDao policyDao=DaoFactory.getDao(IEdgePolicyDao.class);
					policyDao.as_edge_policy_list_by_hostId(d2dHostId, policyList);
					if(policyList.size() == 0){
						logger.debug("There is no plan with node: " + edgeHost.getRhostname() + ", HostId: " + d2dHostId);
						return -1;
					}
					PolicyInfo policy = policyList.get(0);
					//get linux d2d server info
					List<EdgeConnectInfo> connInfoLst = new ArrayList<EdgeConnectInfo>();
					IEdgeConnectInfoDao connectionInfoDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
					connectionInfoDao.as_edge_linux_d2d_server_by_policyid(policy.getPolicyId(), connInfoLst);
					EdgeConnectInfo d2dServerInfo = connInfoLst.get(0);
					d2dProtocol = Protocol.parse(d2dServerInfo.getProtocol()).name();
					d2dHost = d2dServerInfo.getRhostname();
					d2dPort = d2dServerInfo.getPort();
					return 0;
				}
			} else {
				logger.debug("EdgeD2DRegServiceImpl.UpdateRegInfoToD2D(): cannot get host info of d2d host id " + d2dHostId);
				return -1;
			}

			IEdgeConnectInfoDao connectInfoDao = DaoFactory
					.getDao(IEdgeConnectInfoDao.class);
			List<EdgeConnectInfo> connInfoLst = new ArrayList<EdgeConnectInfo>();
			connectInfoDao.as_edge_connect_info_list(d2dHostId, connInfoLst);
			EdgeConnectInfo connInfo = null;
			Iterator<EdgeConnectInfo> iter = connInfoLst.iterator();
			if (iter.hasNext()) {
				connInfo = iter.next();
				d2dPort = connInfo.getPort();
				if (connInfo.getProtocol() == Protocol.Https.ordinal())
					d2dProtocol = "https";
				else
					d2dProtocol = "http";
			} else {
				logger.debug("EdgeD2DRegServiceImpl.UpdateRegInfoToD2D(): cannot get connect info of d2d host id " + d2dHostId);
				return -1;
			}
			
			if (!ApplicationUtil.isD2DProductFamilyInstalled(edgeHost.getAppStatus())){
				logger.debug("No D2D installed or managed, ignore this node for sync");
				return -1;
			}

			logger.debug("EdgeD2DRegServiceImpl.UpdateRegInfoToD2D() connect Info: "
							+ d2dHost + " " + d2dProtocol + " " + d2dPort);
			return 0;
		}
	}



	@Override
	public void EdgeD2DReSync(int[] d2dHostId) throws EdgeServiceFault {
		Set<Integer> hostSet = new HashSet<Integer>();
		for (int i = 0; i < d2dHostId.length; i++) {
			List<EdgeConnectInfo> proxy = new ArrayList<EdgeConnectInfo>();
			hostMgrDao.as_edge_proxy_by_vmhostid(d2dHostId[i], proxy);
			if (proxy.size() > 0) {
				hostSet.add(proxy.get(0).getHostid());
			} else {
				hostSet.add(d2dHostId[i]);
			}
		}
		for (Integer hostId : hostSet) {			
			int result = startD2DReSyncThread(hostId);
			if (result != 0) {
				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.D2D_Sync_In_Progress, "There is another thread sync this d2d node, nodeid: " + hostId);
			}
		}
	}

	@Override
	public void submitD2DSyncForGroup(int groupID, int groupType)
			throws EdgeServiceFault {
		try{
			EdgeNodeFilter nodeFilter = new EdgeNodeFilter();
			
			List<Node> nodeList = null;
			NodePagingConfig pagingConfig = new NodePagingConfig();
			
			pagingConfig.setOrderCol(NodeSortCol.hostname);
			pagingConfig.setOrderType(EdgeSortOrder.ASC);
			pagingConfig.setPagesize(Integer.MAX_VALUE);
			pagingConfig.setStartpos(0);
			
			
			try {
				NodePagingResult result = serviceImpl.getNodesESXByGroupAndTypePaging(groupID, groupType, nodeFilter, pagingConfig);
				nodeList = result.getData();
			} catch (EdgeServiceFault e) {
				logger.error(e.getMessage(), e);
			}
			
			List<Integer> nodeIDs = new LinkedList<Integer>();
			for (Node node:nodeList){
				if (node.isD2dInstalled() && node.getD2dManaged() == NodeManagedStatus.Managed)
					nodeIDs.add(node.getId());
			}
			
			EdgeD2DReSync(CommonUtil.convertIntegerList2Array(nodeIDs));
		}catch(Exception e){
			logger.error(e);
		}
	}

	public void setServiceImpl(EdgeWebServiceImpl serviceImpl) {
		this.serviceImpl = serviceImpl;
	}
}
