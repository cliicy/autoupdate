package com.ca.arcserve.edge.app.rps.webservice.node.setting;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.rps.common.Utils;
import com.ca.arcflash.rps.webservice.data.datastore.DataStoreRunningState;
import com.ca.arcflash.rps.webservice.data.datastore.DataStoreStatus;
import com.ca.arcflash.rps.webservice.data.ds.DataStoreSettingInfo;
import com.ca.arcflash.rps.webservice.data.ds.DataStoreStatusListElem;
import com.ca.arcflash.rps.webservice.data.policy.RPSPolicy;
import com.ca.arcserve.edge.app.base.appdaos.EdgePolicy;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.common.connection.IConnectionFactory;
import com.ca.arcserve.edge.app.base.common.connection.RPSConnection;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.notify.StatusUtil;
import com.ca.arcserve.edge.app.rps.appdaos.IRpsDataStoreDao;
import com.ca.arcserve.edge.app.rps.appdaos.IRpsPolicyDao;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsDataStore;
import com.ca.arcserve.edge.app.rps.webservice.common.RpsDataStoreUtil;
import com.ca.arcserve.edge.app.rps.webservice.datastore.RPSDataStoreServiceImpl;

public class RPSNodeSettingServiceImpl {
	
	private static final Logger logger = Logger.getLogger(RPSNodeSettingServiceImpl.class);

	private static RPSDataStoreServiceImpl dsService = new RPSDataStoreServiceImpl();
	private static IEdgePolicyDao edgePolicyDao = DaoFactory.getDao(IEdgePolicyDao.class);
	
	public static void syncNodeSetting(NodeRegistrationInfo node) throws EdgeServiceFault {
		DataStoreSettingInfo[] dsFromRps;
		
		IConnectionFactory connectionFactory = EdgeFactory.getBean(IConnectionFactory.class);
		try (RPSConnection connection = connectionFactory.createRPSConnection(node.getId())) {
			connection.connect();
			
			dsFromRps = connection.getService().getDataStoreInstance(null);
			
			deleteUselessRpsPolicy(connection);
			
			if (dsFromRps == null) {
				return;
			}
			
			clearPolicyAndDataStoreByNode(node);
			
			for (DataStoreSettingInfo dssi : dsFromRps) {
				insertDataStore(0, node, dssi);
			}
		}
		
		String nodeUuid = node.getNodeInfo().getD2DUUID();
		
		try {
			List<DataStoreStatusListElem> dsElems = dsService.getDataStoreSummariesByNode( node.getId() );
			for( DataStoreStatusListElem dsElem : dsElems ) {
				DataStoreStatus status = dsElem.getDataStoreStatus();
				if( status !=null && dsElem.getDataStoreSetting()!=null ) {
					long rs = status.getOverallStatus();
					DataStoreRunningState state =  DataStoreRunningState.parseInt( (int)rs);
					EdgeServiceFaultBean msg=null;
					if(state==DataStoreRunningState.ABNORMAL_BLOCK_ALL||state==DataStoreRunningState.ABNORMAL_RESTORE_ONLY){
						if( status.getStatusErrorCode()>0 && !Utils.isEmpty( status.getStatusErrorMessage() )){
							msg = new EdgeServiceFaultBean(""+status.getStatusErrorCode(), status.getStatusErrorMessage());
						}
					}
					StatusUtil.setDatastoreStatus(nodeUuid, dsElem.getDataStoreSetting().getDatastore_name(), state, msg);
					StatusUtil.setDatastoreSummary(nodeUuid, dsElem.getDataStoreSetting().getDatastore_name(), status );
				}
				else {  //it's an error! we assume if dsService.getDataStoreSummary successfully return , it must contain a useable DataStoreStatus!
					logger.error("sync from rps server return  but without datastorestatus!!");
				}
			}
		}
		catch(EdgeServiceFault e){
			EdgeServiceFaultBean fault = e.getFaultInfo();
			logger.error("sync datastore status from  "+ node.getNodeName() + " fails!  with message: "+ fault.getMessage() +" error code: "+ fault.getCode() , e );
			for (DataStoreSettingInfo dssi : dsFromRps) {
				try {
					StatusUtil.setDatastoreStatus(nodeUuid, dssi.getDatastore_name(), DataStoreRunningState.UNKNOWN, fault );
				} catch (EdgeServiceFault e1) {
					logger.error( "set datastore status fail!" +  e1);
				}
				StatusUtil.setDatastoreSummary(nodeUuid, dssi.getDatastore_name(), null );
			}		
		}
		catch(Exception e) {
			logger.error("sync datastore status from  "+ node.getNodeName() + " fails!" , e );
		}
	}
	
	private static void clearPolicyAndDataStoreByNode(NodeRegistrationInfo node){
		IRpsPolicyDao policyDao = DaoFactory.getDao(IRpsPolicyDao.class);
		policyDao.as_edge_rps_policy_delete_by_node(node.getId());
		
		IRpsDataStoreDao dataStoreDao = DaoFactory.getDao(IRpsDataStoreDao.class);
		List<EdgeRpsDataStore> datastoreList = new ArrayList<EdgeRpsDataStore>();
		List<DataStoreSettingInfo> dsList = new ArrayList<DataStoreSettingInfo>();
		dataStoreDao.as_edge_rps_datastore_setting_list_by_nodeid(node.getId(), datastoreList);
		
		for(EdgeRpsDataStore ds: datastoreList){//get the waiting for creating datastore list
			DataStoreSettingInfo  dsInfo = RpsDataStoreUtil.converEdgeRpsDataStore(ds);
			if(dsInfo.getFlags() == DataStoreSettingInfo.PHASE_DATASTORE_WAITING_CREATE){
				dsList.add(dsInfo);
			}
		}		
		dataStoreDao.as_edge_rps_datastore_unassign_by_node(node.getId());
		
		//add the waiting for creating datastore back
		for(DataStoreSettingInfo ds: dsList){
			try{
				ds.setDatastore_id(0);
				int dataStoreId = RpsDataStoreUtil.saveDatabase(ds);
				ds.setDatastore_id(dataStoreId);
			}catch(Exception e) {
				logger.error("save waiting created dataStore back "+ ds.getDatastore_name() + " fails!" , e );
			}
		}
	}

	private static void insertDataStore(int dsId, NodeRegistrationInfo node, DataStoreSettingInfo dsSetting) throws EdgeServiceFault {
		dsSetting.setNode_id(node.getId());
		dsSetting.setDatastore_id(dsId);
		RpsDataStoreUtil.saveDatabase(dsSetting);
	}
	
	private static void deleteUselessRpsPolicy(RPSConnection connection) throws EdgeServiceFault {
		RPSPolicy[] policyFromRps = connection.getService().getRPSPolicySummaries(null);
		if (policyFromRps == null) {
			return;
		}
		
		List<EdgePolicy> policyList = new ArrayList<EdgePolicy>();
		edgePolicyDao.as_edge_policy_list(0, 0, policyList);
		
		HashSet<String> plans = new HashSet<String>();
		for (EdgePolicy p : policyList) {
			plans.add(p.getUuid().toLowerCase());
		}
			
		List<String> toDelete = new ArrayList<String>();
		for (RPSPolicy p : policyFromRps) {
			if (!plans.contains(p.getPlanUUID().toLowerCase())) {
				toDelete.add(p.getId());
			}
		}
		
		if (!toDelete.isEmpty()) {
			connection.getService().deleteRPSPolicies(toDelete);
		}
	}

}
