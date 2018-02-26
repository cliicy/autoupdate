package com.ca.arcserve.edge.app.base.webservice.notify;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;

import com.ca.arcflash.rps.common.Utils;
import com.ca.arcflash.rps.webservice.data.datastore.DataStoreRunningState;
import com.ca.arcflash.rps.webservice.data.datastore.DataStoreStatus;
import com.ca.arcflash.rps.webservice.data.ds.DataStoreSettingInfo;
import com.ca.arcflash.rps.webservice.data.ds.DataStoreStatusListElem;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.schedulers.EdgeExecutors;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ValuePair;
import com.ca.arcserve.edge.app.base.webservice.d2d.D2DEdgeServiceImpl;
import com.ca.arcserve.edge.app.rps.appdaos.IRpsDataStoreDao;
import com.ca.arcserve.edge.app.rps.appdaos.IRpsNodeDao;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsNode;
import com.ca.arcserve.edge.app.rps.webservice.common.RpsDataStoreUtil;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.RpsNode;
import com.ca.arcserve.edge.app.rps.webservice.datastore.RPSDataStoreServiceImpl;
import com.ca.arcserve.edge.app.rps.webservice.node.RPSNodeServiceImpl;

public class StatusUtil {
	private static final Logger logger = Logger.getLogger(StatusUtil.class);
	private static RPSDataStoreServiceImpl dsService = new RPSDataStoreServiceImpl();
	private static IRpsNodeDao rpsNodeDao = DaoFactory.getDao(IRpsNodeDao.class);

	public static String getServerUuid(int nodeID){
		List<EdgeRpsNode> nodeList = new ArrayList<EdgeRpsNode>();
		rpsNodeDao.as_edge_rps_node_list(nodeID, nodeList);
		if(nodeList == null || nodeList.size()==0){
			return null;
		}
		
		EdgeRpsNode host= nodeList.get(0);
		return host.getUuid();
	}
	public static void setDatastoreStatus(String serverUuid, String dataStoreUuid, DataStoreRunningState state, EdgeServiceFaultBean exception) throws EdgeServiceFault{
		String key="datastore_"+serverUuid+"_"+dataStoreUuid;
		logger.info(key+":"+state.toString()+(exception==null?"":","+exception.getCode()+","+exception.getMessage()));
		
		if(state==DataStoreRunningState.DELETED){
			IRpsDataStoreDao datastoreDao = DaoFactory.getDao(IRpsDataStoreDao.class);
			int rpsnodeid=D2DEdgeServiceImpl.getRpsHostId(serverUuid);
			datastoreDao.as_edge_rps_datastore_setting_delete(rpsnodeid, dataStoreUuid);
			MemCache.getInstance().remove(key);
		}else{
			ValuePair<DataStoreRunningState, EdgeServiceFaultBean> v = MemCache.getInstance().getValue(key);
			if(v!=null && v.getKey()==state && v.getValue()!=null && exception==null){
				logger.warn("ignore state change");
			}else{
				MemCache.getInstance().setValue(key, new ValuePair<DataStoreRunningState, EdgeServiceFaultBean>(state, exception));
			}
		}
	}
	
	
	
	public static ValuePair<DataStoreRunningState, EdgeServiceFaultBean> getDatastoreStatus(String serverUuid, String dataStoreUuid){
		String key="datastore_"+serverUuid+"_"+dataStoreUuid;
		return (ValuePair<DataStoreRunningState, EdgeServiceFaultBean>) MemCache.getInstance().getValue(key);
	}
	/**
	 * now, we never clear datastoresummary cache even the datastore is delete; it's no harm. 
	 * becasue caller will guarantee to pass in a dsuuid which is not be delete! ( the dsuuid always get from local db, never the synced dsElem sync from rps! )
	 * @param dsUuid
	 * @param dss
	 */
	public static void setDatastoreSummary(String serverUuid, String dataStoreUuid, DataStoreStatus dss ) {
		if( dss==null ) {
			logger.info("sync DataStoreStatus error! " + dataStoreUuid+" datastore status =null" );
		}
		else {
			logger.debug("sync DataStoreStatus success! " + dataStoreUuid+"with overall status =" +dss.getOverallStatus() + 
					" common status:" + ( dss.getCommonStoreStatus() !=null  ? "not null": "=null " ) +
						" dedup status:" +  ( dss.getGDDStoreStatus() !=null  ? "not null": "=null" ) +
						dss.getElapsedTimeInSec()+"/"+dss.getEstRemainTimeInSec()) ;
		}
		String key="datastore_"+serverUuid+"_"+dataStoreUuid;
		MemCache.getInstance().setDsSummary(key, dss);
	}
	
	public static DataStoreStatus getDatastoreSummary(String serverUuid, String dataStoreUuid ) {
		String key="datastore_"+serverUuid+"_"+dataStoreUuid;
		return MemCache.getInstance().getDsSummary(key);
	}
	public static List<DataStoreStatusListElem> getDataStoreSummaryByNodeId( Integer nodeId ) {
		List<DataStoreSettingInfo> settings = null;
		List<DataStoreStatusListElem> dsElms = new ArrayList<DataStoreStatusListElem>();
		
		try {
			settings = dsService.getDataStoreListByNode(nodeId);
		} 
		catch (EdgeServiceFault e) {
			logger.error("failed get datastore setting for node: "+nodeId, e);
		}
		try {
			String serverUuid=getServerUuid(nodeId);
			if( settings !=null && settings.size() > 0 ) {
				for( DataStoreSettingInfo setting : settings ) {
					
					//For those datastore which was created during importing RPS, need to get the status and error message from DB.-->
					if(setting.getFlags() == DataStoreSettingInfo.PHASE_DATASTORE_WAITING_CREATE){
						DataStoreStatusListElem dsElm = new DataStoreStatusListElem();
						dsElm.setDataStoreSetting(setting);
						DataStoreStatus dss = new DataStoreStatus();
						String errorMessage = RpsDataStoreUtil.getDataStoreMessage(setting);
						if(errorMessage != null && !errorMessage.isEmpty()){
							dss.setOverallStatus(DataStoreRunningState.CREATE_FAILED.getValue());
						}else{
							dss.setOverallStatus(DataStoreRunningState.CREATING.getValue());
						}
						
						dss.setStatusErrorCode(0);
						dss.setStatusErrorMessage(errorMessage);
						dsElm.setDataStoreStatus(dss);
						dsElms.add(dsElm);
						continue;
						
					}//<--
					
					String uuid = setting.getDatastore_name();
					DataStoreStatus dss = getDatastoreSummary(serverUuid, uuid);
					ValuePair<DataStoreRunningState, EdgeServiceFaultBean>  state =  getDatastoreStatus(serverUuid, uuid );
					
					if( state !=null ) {
						if( dss ==null ) {
							logger.debug( "DataStoreStatus " +setting.getDatastore_name() +" don't have status info! with running status = "+ state.getKey().getValue() );
							dss = new DataStoreStatus();
						}
						dss.setOverallStatus( state.getKey().getValue() );
						if( state.getValue()!=null &&  state.getValue().getCode() != null ) {
							long errorCode =0;
							try {
								errorCode= Long.parseLong( state.getValue().getCode() );
							}
							catch( Exception e ){ 
								logger.error("DataStoreStatus " +setting.getDatastore_name() +" failed to parse status error code:" +state.getValue().getCode() );
							}
							dss.setStatusErrorCode( errorCode );
							dss.setStatusErrorMessage( state.getValue().getMessage()!=null ? state.getValue().getMessage(): "" );
						}
						//debug
						logger.debug("DataStoreStatus " +setting.getDatastore_name() +" volume total size: " + 
								 ( dss.getCommonStoreStatus()!=null ? dss.getCommonStoreStatus().getDataVolumeTotalSize() :"" ) ) ;
					}
					else {  //state == null! it means we cannot get state from cache; it's a special status; only meaningful in cpm; and we assume in this case, datastorestatus must also be null
						if( dss ==null ) {
							logger.debug( "DataStoreStatus " +setting.getDatastore_name() +" don't have status info! with running status also not exist!" );
							dss = new DataStoreStatus();
						}
					
						dss.setOverallStatus( DataStoreRunningState.NOTGET.getValue() );						
					}
		
					DataStoreStatusListElem dsElm = new DataStoreStatusListElem();
					dsElm.setDataStoreSetting(setting);
					dsElm.setDataStoreStatus(dss);
					dsElms.add(dsElm);
				}
			}
		}
		catch( Exception e) {
			logger.error("failed get datastore status from cache for node: "+nodeId, e);
		}
		return dsElms;
	}
	public static void initDataStoreSummaryInfo(){
		EdgeExecutors.getCachedPool().submit(new InitDsTask());
	}
	
	public static class InitDsTask implements Runnable {
		
		private static final Logger logger = Logger.getLogger(InitDsTask.class);
		private static RPSNodeServiceImpl rpsService = new RPSNodeServiceImpl();	
		private static RPSDataStoreServiceImpl dsService = new RPSDataStoreServiceImpl();
		public static ReentrantLock runLock = new ReentrantLock();
		@Override
		public void run() {
			try {
				if( !runLock.tryLock() ) {
					logger.info("datastore sync thread is running , skip this call!");
					return;
				}
				logger.info("start init data store status");
				List<RpsNode> nodes =  rpsService.getRpsNodesByGroup(0, -1 ); //-1 all group; no group filer
				for( RpsNode node: nodes ) {
					
					try{
						dsService.forceRefreshDataStoreStatus(node.getNode_id());//defect 188448, force refresh data store status.
					}catch(EdgeServiceFault e){
						EdgeServiceFaultBean fault = e.getFaultInfo();
						logger.error("force refresh datastore status from "+ node.getNode_name()+ " " 
								+ " fails!  with message: "+ fault.getMessage() +" error code: "+ fault.getCode() , e );				
					}
					
					boolean webserviceOK=true;
					EdgeServiceFaultBean lastFault=null;
					List<DataStoreSettingInfo> dssList = dsService.getDataStoreListByNode(node.getNode_id());  //setting must get from db, shouldn't sync from rps! 
					if( dssList!=null ) {
						for( DataStoreSettingInfo dss : dssList ){
							if(!webserviceOK){
								StatusUtil.setDatastoreStatus(node.getUuid(), dss.getDatastore_name(), DataStoreRunningState.UNKNOWN, lastFault );
								StatusUtil.setDatastoreSummary(node.getUuid(), dss.getDatastore_name(), null );
							}
							else{	
								try {
									//get each datastore's status and update both runtimestatus enum and datastorestatus object in cache
									//we shouldn't use dataStoreSetting in dsElem, only use data store statu 
									DataStoreStatusListElem  dsElem = dsService.getDataStoreSummary( node.getNode_id(), dss.getDatastore_name() ); 
									DataStoreStatus status = dsElem.getDataStoreStatus();
									if( status !=null ) {
										long rs = status.getOverallStatus();
										DataStoreRunningState state =  DataStoreRunningState.parseInt( (int)rs);
										EdgeServiceFaultBean msg=null;
										if(state==DataStoreRunningState.ABNORMAL_BLOCK_ALL||state==DataStoreRunningState.ABNORMAL_RESTORE_ONLY){
											if( status.getStatusErrorCode()>0 && !Utils.isEmpty( status.getStatusErrorMessage() )){
												msg = new EdgeServiceFaultBean(""+status.getStatusErrorCode(), status.getStatusErrorMessage());
											}
										}
										StatusUtil.setDatastoreStatus(node.getUuid(), dss.getDatastore_name(), state, msg);
										StatusUtil.setDatastoreSummary(node.getUuid(),  dss.getDatastore_name(), status );
									}
									else {  //it's an error! we assume if dsService.getDataStoreSummary successfully return , it must contain a useable DataStoreStatus!
										logger.error("sync from rps server return  but without datastorestatus!!");
									}
								}			
								catch(EdgeServiceFault e){
									EdgeServiceFaultBean fault = e.getFaultInfo();
									logger.error("sync datastore status from  "+ node.getNode_name()+ " "+ dss.getDatastore_name() 
											+ " fails!  with message: "+ fault.getMessage() +" error code: "+ fault.getCode() , e );
									//The data store instance is not exist
								
									if(EdgeServiceErrorCode.POLICY_RPS_CANNOT_CONNECT.equals(fault.getCode()) ||
											EdgeServiceErrorCode.POLICY_RPS_WrongCredential.equals(fault.getCode())){
										webserviceOK=false;
										lastFault=fault;
									}
									StatusUtil.setDatastoreStatus(node.getUuid(), dss.getDatastore_name(), DataStoreRunningState.UNKNOWN, fault );
									StatusUtil.setDatastoreSummary(node.getUuid(),  dss.getDatastore_name(), null );
									
								}
							}
						}
					}
				}
				logger.info("end init data store status");
			} 
			catch (EdgeServiceFault e) {
				logger.error("init data store status failed" , e);
			}
			finally {
				if ( runLock.isHeldByCurrentThread() ) {
					runLock.unlock();
				}
			}
		}
	}
	
}
