package com.ca.arcserve.edge.app.rps.webservice.datastore;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.rps.webservice.data.ds.DataStoreSettingInfo;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.rps.appdaos.IRpsDataStoreDao;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsDataStore;
import com.ca.arcserve.edge.app.rps.webservice.common.RpsDataStoreUtil;
import com.ca.arcserve.edge.app.rps.webservice.setting.datastore.DataStoreManagerImpl;
import com.ca.arcserve.edge.app.rps.webservice.setting.datastore.IDataStoreManager;

/*
 * Luoca01: a class to manage the staff about dedup stor, such as dedup deploy 
 */
public class DataStoreManager {

    private final static DataStoreManager INSTANCE = new DataStoreManager();
    private IRpsDataStoreDao datastoreDao = DaoFactory.getDao(IRpsDataStoreDao.class);
    
    private static IDataStoreManager dsm;
    
    private DataStoreManager()
    {
    }
    
    public static DataStoreManager getInstance() {
        return INSTANCE;
    }
    
    public static IDataStoreManager getDataStoreManager(){
        if(dsm==null){
            dsm=new DataStoreManagerImpl();
        }
        return dsm;
    }
    
    public DataStoreSettingInfo getDataStoreByDsId(Integer dedupId)
    {
        
        List<EdgeRpsDataStore> dataStoreList = new ArrayList<EdgeRpsDataStore>();
        datastoreDao.as_edge_rps_datastore_setting_list(dedupId, dataStoreList);
        
        if(dataStoreList.size() == 0) 
               return null; 

        return xmlToDataStoreSetting(dataStoreList.get(0));
    }
   public List<DataStoreSettingInfo> getDataStoreByNodeId(Integer nodeId)
    {
        List<DataStoreSettingInfo> settings = new ArrayList<DataStoreSettingInfo>();
        List<EdgeRpsDataStore> dedupList = new ArrayList<EdgeRpsDataStore>();
        datastoreDao.as_edge_rps_datastore_setting_list_by_nodeid(nodeId, dedupList);
        
        for (EdgeRpsDataStore dedup : dedupList)
        {           
            settings.add(xmlToDataStoreSetting(dedup));
        }
        return settings;
    }
    
    private DataStoreSettingInfo xmlToDataStoreSetting(EdgeRpsDataStore eRpsDedup) {
        return RpsDataStoreUtil.converEdgeRpsDataStore(eRpsDedup);
    }
    
}
