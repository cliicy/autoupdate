package com.ca.arcserve.edge.app.base.webservice.notify;

import java.util.concurrent.ConcurrentHashMap;

import com.ca.arcflash.rps.webservice.data.datastore.DataStoreRunningState;
import com.ca.arcflash.rps.webservice.data.datastore.DataStoreStatus;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ValuePair;

public class MemCache {
	private static MemCache singleton=new MemCache();
	public static MemCache getInstance(){
		return singleton;
	}
	
	private ConcurrentHashMap<String, ValuePair<DataStoreRunningState, EdgeServiceFaultBean>> map=new ConcurrentHashMap<String, ValuePair<DataStoreRunningState, EdgeServiceFaultBean>>();
	
	private ConcurrentHashMap<String, DataStoreStatus> ds_nodeMap = new  ConcurrentHashMap<String, DataStoreStatus>();
	private MemCache(){
	}
	
	public void setValue(String key, ValuePair<DataStoreRunningState, EdgeServiceFaultBean> value){
		map.put(key, value);
	}
	
	public ValuePair<DataStoreRunningState, EdgeServiceFaultBean> getValue(String key){
		return map.get(key);
	}
	public void remove(String key){
		map.remove(key);
	}
	
	public void setDsSummary(String dsUuid, DataStoreStatus dss  ){
		if( dss ==null ) {  //some times is null
			ds_nodeMap.remove(dsUuid);
		}
		else { 
			ds_nodeMap.put(dsUuid, dss);
		}
	}
	
	public DataStoreStatus  getDsSummary ( String dsUuid ){
		return ds_nodeMap.get(dsUuid);
	}
	public void removeDsSummary( String dsUuid ){
		ds_nodeMap.remove(dsUuid);
	}
}
