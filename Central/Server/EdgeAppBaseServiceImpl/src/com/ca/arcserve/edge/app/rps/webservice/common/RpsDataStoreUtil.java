package com.ca.arcserve.edge.app.rps.webservice.common;


import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.rps.webservice.data.ds.DataStoreSettingInfo;
import com.ca.arcflash.rps.webservice.data.ds.DataStoreSettingInfoXML;
import com.ca.arcflash.webservice.jni.WSJNI;
import com.ca.arcserve.edge.app.base.dao.DaoException;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.rps.appdaos.IRpsDataStoreDao;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsDataStore;
import com.ca.arcserve.edge.app.rps.webservice.setting.datastore.DataStoreCapture;
import com.ca.arcserve.edge.app.rps.webservice.setting.datastore.DataStoreWebUtil;

public class RpsDataStoreUtil {

	private static IRpsDataStoreDao dsDao = DaoFactory.getDao(IRpsDataStoreDao.class);
	public static DataStoreSettingInfo converEdgeRpsDataStore(
			EdgeRpsDataStore eRpsDedup) {
		if (eRpsDedup == null)
			return null;

		DataStoreCapture ds = new DataStoreCapture();
		DataStoreSettingInfoXML settingXml = ds
				.getObjectFromXmlString(eRpsDedup.getDatastore_setting());

		DataStoreSettingInfo info = settingXml.getInfo();

		// decrypt password
		String pass1 = info.getEncryptionPwd();
		if(!StringUtil.isEmptyOrNull(pass1)){
			info.setEncryptionPwd(WSJNI.AFDecryptStringEx(pass1));
		}
		String pass2 = info.getDSCommSetting().getPassword();
		if(!StringUtil.isEmptyOrNull(pass2)){
			info.getDSCommSetting().setPassword(WSJNI.AFDecryptStringEx(pass2));
		}
		if (info.getEnableGDD() > 0) {
			String pass3 = info.getGDDSetting().getDataStorePassword();
			if(!StringUtil.isEmptyOrNull(pass3)){
				info.getGDDSetting().setDataStorePassword(
						WSJNI.AFDecryptStringEx(pass3));
			}
			
			String pass4 = info.getGDDSetting().getIndexStorePassword();
			if(!StringUtil.isEmptyOrNull(pass4)){
				info.getGDDSetting().setIndexStorePassword(
						WSJNI.AFDecryptStringEx(pass4));
			}
			
			String pass5 = info.getGDDSetting().getHashStorePassword();
			if(!StringUtil.isEmptyOrNull(pass5)){
				info.getGDDSetting().setHashStorePassword(
						WSJNI.AFDecryptStringEx(pass5));
			}
			
		}

		// some data need to get from db since they're not in the xml
		info.setDatastore_id(eRpsDedup.getDatastore_id());
		info.setNode_id(eRpsDedup.getNode_id());
		info.setDisplayName(eRpsDedup.getDatastore_name());
		info.setDatastore_name(eRpsDedup.getDatastore_uuid());
		info.setStatus(eRpsDedup.getStatus());

		return info;
	}

	public static String convertToXML(DataStoreSettingInfo dsi) {
		// encryption password
//		encryptSetting(dsi);
		
		String dsXml = convertToXMLWithoutEncrypt(dsi);		
		
//		decryptSetting(dsi);
		
		return dsXml;
	}
	
	//This means dsi password have alreay been encrypted
	public static String convertToXMLWithoutEncrypt(DataStoreSettingInfo dsi){
		DataStoreCapture ds = new DataStoreCapture();
		DataStoreSettingInfoXML settingXml = new DataStoreSettingInfoXML();
		settingXml.setInfo(dsi);
		ds.writexml(settingXml);
		return ds.getSetting();
	}
	
	public static DataStoreSettingInfo loadDataStoreById(int dsId) {
		List<EdgeRpsDataStore> dsList = new ArrayList<EdgeRpsDataStore>();
		dsDao.as_edge_rps_datastore_setting_list(dsId, dsList);

		if (dsList.isEmpty())
			return null;
		
		return converEdgeRpsDataStore(dsList.get(0));
	}
	
	public static DataStoreSettingInfo loadDataStoreByUUID(int nodeId, String uuid) {
		List<EdgeRpsDataStore> dsList = new ArrayList<EdgeRpsDataStore>();		
		dsDao.as_edge_rps_datastore_setting_list(nodeId, uuid, dsList);

		if (dsList.isEmpty())
			return null;

		return converEdgeRpsDataStore(dsList.get(0));
	}
	
	public static int saveDatabase(DataStoreSettingInfo settingInfo) throws EdgeServiceFault {
		String xml = RpsDataStoreUtil.convertToXML(settingInfo);

		int[] dedupId = new int[1];
		try{
			dsDao.as_edge_rps_datastore_setting_update(settingInfo.getDatastore_id(),
					settingInfo.getNode_id(), settingInfo.getDatastore_name(),
					settingInfo.getDisplayName(), xml, dedupId);
		}catch(DaoException ex){
			throw DataStoreWebUtil.generateException(EdgeServiceErrorCode.POLICY_RPS_SAVE_DB_FAILED, ex.getMessage(), new Object[]{ex.getMessage()});
		}
		return dedupId[0];
	}
	
	public static void updateDataStoreMessageToDB(DataStoreSettingInfo settingInfo, String message) throws EdgeServiceFault {
		try{
			dsDao.as_edge_rps_datastore_setting_updateStatus(settingInfo.getDatastore_id(), settingInfo.getNode_id(),
					settingInfo.getDatastore_name(), message);
		}catch(DaoException ex){
			throw DataStoreWebUtil.generateException(EdgeServiceErrorCode.POLICY_RPS_SAVE_DB_FAILED, ex.getMessage(), new Object[]{ex.getMessage()});
		}
	}
	
	public static String getDataStoreMessage(DataStoreSettingInfo settingInfo) throws EdgeServiceFault {
		String[] message = new String[1];
		dsDao.as_edge_rps_get_datastore_setting_statusMessage(settingInfo.getDatastore_id(), message);
		return message[0];			
	}

}
