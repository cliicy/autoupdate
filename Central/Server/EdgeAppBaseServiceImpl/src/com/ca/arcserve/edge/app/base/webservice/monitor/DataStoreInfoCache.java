package com.ca.arcserve.edge.app.base.webservice.monitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.rps.appdaos.IRpsDataStoreDao;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsDataStore;

public class DataStoreInfoCache
{
	private static DataStoreInfoCache instance = null;
	private Map<String, EdgeRpsDataStore> storeMap;
	private IRpsDataStoreDao storeDao;
	
	//////////////////////////////////////////////////////////////////////////

	protected DataStoreInfoCache()
	{
		this.storeDao = DaoFactory.getDao( IRpsDataStoreDao.class );
		this.storeMap = new HashMap<String, EdgeRpsDataStore>();
	}
	
	//////////////////////////////////////////////////////////////////////////

	public static synchronized DataStoreInfoCache getInstance()
	{
		if (instance == null)
			instance = new DataStoreInfoCache();
		
		return instance;
	}
	
	//////////////////////////////////////////////////////////////////////////

	public synchronized void clear()
	{
		this.storeMap.clear();
	}
	
	//////////////////////////////////////////////////////////////////////////

	public synchronized void reloadMap(){
		this.storeMap.clear();
		List<EdgeRpsDataStore> datastoreList = new ArrayList<EdgeRpsDataStore>();
		this.storeDao.as_edge_rps_dedup_setting_list_all( datastoreList );
		for (EdgeRpsDataStore store : datastoreList) {				
			this.storeMap.put( store.getDatastore_uuid().trim(), store );
		}
	}
	
	public synchronized EdgeRpsDataStore getDataStoreInfo( String storeUuid )
	{
		if (!this.storeMap.containsKey( storeUuid ))
		{
			this.reloadMap();
			if (this.storeMap.containsKey( storeUuid ))
				return this.storeMap.get( storeUuid );
			else
				return null;
		}		
		return this.storeMap.get( storeUuid );
	}
}
