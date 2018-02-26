package com.ca.arcserve.edge.app.rps.webservice.common;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;

import com.ca.arcflash.rps.webservice.data.policy.RPSPolicy;
import com.ca.arcflash.webservice.jni.WSJNI;
import com.ca.arcserve.edge.app.base.dao.DaoException;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.StringUtil;
import com.ca.arcserve.edge.app.rps.appdaos.IRpsDataStoreDao;
import com.ca.arcserve.edge.app.rps.appdaos.IRpsPolicyDao;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsDataStore;
import com.ca.arcserve.edge.app.rps.appdaos.model.EdgeRpsPolicy;
import com.ca.arcserve.edge.app.rps.webservice.setting.datastore.DataStoreWebUtil;

public class RpsPolicyUtil {
	private final static Logger log = Logger.getLogger(RpsPolicyUtil.class);
	private static Marshaller policyMarsh = null;
	private static Object policyMarshObject = new Object();
	private static IRpsPolicyDao policyDao = DaoFactory.getDao(IRpsPolicyDao.class);
	private static IRpsDataStoreDao dsDao = DaoFactory.getDao(IRpsDataStoreDao.class);
	
	public static void encryptPolicy(RPSPolicy rpsPolicy) {
		if(rpsPolicy.getRpsSettings().getRpsReplicationSettings().isEnableReplication()){
			rpsPolicy
				.getRpsSettings()
				.getRpsReplicationSettings()
				.setPassword(
						WSJNI.AFEncryptStringEx(rpsPolicy.getRpsSettings()
								.getRpsReplicationSettings().getPassword()));
			if(rpsPolicy.getRpsSettings().getRpsReplicationSettings().isEnableProxy()){
				if(!StringUtil.isEmptyOrNull(rpsPolicy.getRpsSettings().getRpsReplicationSettings().getProxyPassword()))
					rpsPolicy
					.getRpsSettings()
					.getRpsReplicationSettings()
					.setProxyPassword(
							WSJNI.AFEncryptStringEx(rpsPolicy.getRpsSettings()
									.getRpsReplicationSettings().getProxyPassword()));
			}
		}
	}

	public static void decryptPolicy(RPSPolicy rpsPolicy){
		if(rpsPolicy.getRpsSettings().getRpsReplicationSettings().isEnableReplication()){
			rpsPolicy
				.getRpsSettings()
				.getRpsReplicationSettings()
				.setPassword(
						WSJNI.AFDecryptStringEx(rpsPolicy.getRpsSettings()
								.getRpsReplicationSettings().getPassword()));
			if(rpsPolicy.getRpsSettings().getRpsReplicationSettings().isEnableProxy()){
				if(!StringUtil.isEmptyOrNull(rpsPolicy.getRpsSettings().getRpsReplicationSettings().getProxyPassword()))
					rpsPolicy
					.getRpsSettings()
					.getRpsReplicationSettings()
					.setProxyPassword(
							WSJNI.AFDecryptStringEx(rpsPolicy.getRpsSettings()
									.getRpsReplicationSettings().getProxyPassword()));
			}
		}
	}
	
	public static EdgeRpsPolicy convertRPSPolicy(RPSPolicy rpsPolicy) {
		return convertRpsPolicyWithoutEncrypt(rpsPolicy);
	}

	public static EdgeRpsPolicy convertRpsPolicyWithoutEncrypt(
			RPSPolicy rpsPolicy) {
		try {
			if (rpsPolicy == null) {
				return null;
			}

			try {
				if (policyMarsh == null) {
					synchronized (policyMarshObject) {
						if (policyMarsh == null) {
							JAXBContext context = JAXBContext
									.newInstance(RPSPolicy.class);
							policyMarsh = context.createMarshaller();
						}
					}
				}
			} catch (JAXBException e) {
				log.error(e.getMessage());
				log.debug(e.getMessage());
			}

			EdgeRpsPolicy edgeRpsPolicy = new EdgeRpsPolicy();
			ByteArrayOutputStream os = new ByteArrayOutputStream();

			policyMarsh.marshal(rpsPolicy, os);
			rpsPolicy
					.getRpsSettings()
					.getRpsReplicationSettings()
					.setPassword(
							rpsPolicy.getRpsSettings()
									.getRpsReplicationSettings().getPassword());
			edgeRpsPolicy.setExternal_setting(os.toString("UTF-8"));

			edgeRpsPolicy.setPolicy_name(rpsPolicy.getName());
			edgeRpsPolicy.setPolicy_uuid(rpsPolicy.getId());
			edgeRpsPolicy.setStorage_size_limit(rpsPolicy.getRpsSettings()
					.getRpsBasicSettings().getStorageUsageUpperLimit());
			edgeRpsPolicy.setDatastore_id(rpsPolicy.getRpsSettings()
					.getRpsDataStoreSettings().getDataStoreId());
			edgeRpsPolicy.setDatastore_name(rpsPolicy.getRpsSettings()
					.getRpsDataStoreSettings().getDataStoreName());

			return edgeRpsPolicy;
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}
	
	public static int saveDatabase(int nodeId, RPSPolicy policy) throws EdgeServiceFault {
		EdgeRpsPolicy edgePolicy = RpsPolicyUtil.convertRPSPolicy(policy);
		int[] id = new int[1];
		id[0] = 0;
		try{
			policyDao.as_edge_rps_policy_update_by_uuid(
					edgePolicy.getPolicy_uuid(), edgePolicy.getPolicy_name(),
					nodeId, edgePolicy.getExternal_setting(), id);
			List<EdgeRpsDataStore> datastoreList = new ArrayList<EdgeRpsDataStore>();
			dsDao.as_edge_rps_datastore_setting_list(nodeId, edgePolicy.getDatastore_name(), datastoreList);

			if (!datastoreList.isEmpty())
				dsDao.as_edge_rps_datastore_setting_assign(datastoreList.get(0)
						.getDatastore_id(),id[0]);
		}catch(DaoException ex){
			throw DataStoreWebUtil.generateException(EdgeServiceErrorCode.POLICY_RPS_SAVE_DB_FAILED, ex.getMessage(), new Object[]{ex.getMessage()});
		}
		
		return id[0];
	}

}
