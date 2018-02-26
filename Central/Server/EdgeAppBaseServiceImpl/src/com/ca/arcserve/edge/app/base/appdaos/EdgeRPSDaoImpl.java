package com.ca.arcserve.edge.app.base.appdaos;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ca.arcflash.webservice.data.RPSInfo;
import com.ca.arcserve.edge.app.base.dao.DaoException;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeEsxHostMapInfo;
import com.ca.arcflash.webservice.jni.WSJNI;

public class EdgeRPSDaoImpl implements IEdgeRPSDao {

	@Override
	public int getHostIdForNodeImportedFromRPS(String uuid , String rpsuuid) {
		String encrConverterUUIDString = WSJNI.AFEncryptString(uuid);
		List<Object> paras = new ArrayList<Object>();
		String SQL = "SELECT HOST.RHOSTID FROM DBO.AS_EDGE_HOST AS HOST " +
				"LEFT JOIN DBO.AS_EDGE_NODE_DEST_INFO AS MAP " +
				"ON HOST.RHOSTID=MAP.HOSTID " +
				"LEFT JOIN DBO.AS_EDGE_RPS AS RPS " +
				"ON RPS.ID=MAP.RPSID " +
				"LEFT JOIN DBO.AS_EDGE_CONNECT_INFO AS CONNECTINFO " +
				"ON CONNECTINFO.HOSTID = HOST.RHOSTID " +
				"WHERE CONNECTINFO.UUID = ? AND RPS.UUID = ?" ;
		List<Integer> hostIdList = new ArrayList<Integer>();
		paras.add(encrConverterUUIDString);
		paras.add(rpsuuid);
		EdgeDaoCommonExecuter commonExecuter = new EdgeDaoCommonExecuter();
		try {
			commonExecuter.ExecuteDao(SQL, paras, hostIdList, 0);
		} finally {
			commonExecuter.CloseDao();
		}
		if (hostIdList.size() > 0) {
			if (hostIdList.size() > 1) {
				throw new DaoException("More than one nodes fit the query condition:" + paras);
			}
			return hostIdList.get(0);
		}
		return 0;
	}

	@Override
	public int getHostIdByVMINSTUUIDAndRPSUUID(String instanceuuid,
			String rpsuuid) {
		List<Object> paras = new ArrayList<Object>();
		String SQL = "SELECT HOST.RHOSTID FROM DBO.AS_EDGE_HOST AS HOST " +
				"LEFT JOIN DBO.AS_EDGE_ESX_HOST_MAP AS ESXMAP ON HOST.RHOSTID = ESXMAP.HOSTID  " +
				"LEFT JOIN DBO.AS_EDGE_NODE_DEST_INFO AS MAP ON HOST.RHOSTID=MAP.HOSTID " +
				"LEFT JOIN DBO.AS_EDGE_RPS AS RPS ON RPS.ID=MAP.RPSID " +
				"WHERE ESXMAP.VMINSTANCEUUID = ? " +
				"AND (HOST.rhosttype = 40 " +
				"OR ((DBO.BitwiseAnd(rhosttype, 256) != 0 OR DBO.BitwiseAnd(rhosttype, 512) != 0) AND RPS.UUID=?))";
		List<Integer> hostIdList = new ArrayList<Integer>();
		paras.add(instanceuuid);
		paras.add(rpsuuid);
		EdgeDaoCommonExecuter commonExecuter = new EdgeDaoCommonExecuter();
		try {
			commonExecuter.ExecuteDao(SQL, paras, hostIdList, 0);
		} finally {
			commonExecuter.CloseDao();
		}
		if (hostIdList.size() > 0) {
			if (hostIdList.size() > 1) {
				throw new DaoException("More than one nodes fit the query condition:" + paras);
			}
			return hostIdList.get(0);
		}
		return 0;
	}
	
	@Override
	public int getHostIdByVMINSTUUID(String instanceuuid) {
		List<Object> paras = new ArrayList<Object>();
		String SQL = "SELECT HOST.RHOSTID FROM DBO.AS_EDGE_HOST AS HOST " +
				"LEFT JOIN DBO.AS_EDGE_ESX_HOST_MAP AS ESXMAP ON HOST.RHOSTID = ESXMAP.HOSTID  " +
				"WHERE ESXMAP.VMINSTANCEUUID = ? ";
		List<Integer> hostIdList = new ArrayList<Integer>();
		paras.add(instanceuuid);
		EdgeDaoCommonExecuter commonExecuter = new EdgeDaoCommonExecuter();
		try {
			commonExecuter.ExecuteDao(SQL, paras, hostIdList, 0);
		} finally {
			commonExecuter.CloseDao();
		}
		if (hostIdList.size() > 0) {
			return hostIdList.get(0);
		}
		return 0;
	}
	
	@Override
	public String getHostNameByVMINSTUUID(String instanceuuid) {
		List<Object> paras = new ArrayList<Object>();
		String SQL = "SELECT HOST.RHOSTNAME FROM DBO.AS_EDGE_HOST AS HOST " +
				"LEFT JOIN DBO.AS_EDGE_ESX_HOST_MAP AS ESXMAP ON HOST.RHOSTID = ESXMAP.HOSTID  " +
				"WHERE ESXMAP.VMINSTANCEUUID = ? ";
		List<String> hostNameList = new ArrayList<String>();
		paras.add(instanceuuid);
		EdgeDaoCommonExecuter commonExecuter = new EdgeDaoCommonExecuter();
		try {
			commonExecuter.ExecuteDaoStringResult(SQL, paras, hostNameList, 0);
		} finally {
			commonExecuter.CloseDao();
		}
		if (hostNameList.size() > 0) {
			return hostNameList.get(0);
		}
		return "";
	}
	
	@Override
	public void as_edge_rps_update(
			String hostname, int port, int protocol, String username,
			String password,String rpsuuid, int[] result) {
		String SQL = "";
		List<Object> paras = new ArrayList<Object>();
		List<Integer> hostIdList;
		EdgeDaoCommonExecuter commonExecuter = new EdgeDaoCommonExecuter();
		try {
			SQL = "SELECT ID FROM DBO.AS_EDGE_RPS WHERE UUID = ?";
			hostIdList = new ArrayList<Integer>();
			paras.add(rpsuuid);
			commonExecuter.ExecuteDao(SQL, paras, hostIdList, 0);
			if (hostIdList.size() > 0) {
				//Update
				SQL = "UPDATE DBO.AS_EDGE_RPS"
						+ " SET hostname=?, port=?, protocol=?, username=?, password=? WHERE uuid=?";
				paras = new ArrayList<Object>();
				paras.add(hostname);
				paras.add(port);
				paras.add(protocol);
				paras.add(username);
				paras.add(password);
				paras.add(rpsuuid);
				commonExecuter.ExecuteDao(SQL, paras);
				result[0] = hostIdList.get(0);
			} else {
				//insert
				SQL = "INSERT INTO AS_EDGE_RPS (hostname, port, protocol, username, password , uuid) "
						+ "values(?, ?, ?, ?, ? , ?)";
				paras = new ArrayList<Object>();
				paras.add(hostname);
				paras.add(port);
				paras.add(protocol);
				paras.add(username);
				paras.add(password);
				paras.add(rpsuuid);
				commonExecuter.ExecuteDao(SQL, paras);
				SQL = "VALUES IDENTITY_VAL_LOCAL()";
				List<Integer> ids = new ArrayList<Integer>();
				commonExecuter.ExecuteDao(SQL, null, ids, 0);
				result[0] = (!ids.isEmpty()) ? ids.get(0) : 0;
			}
		} catch (DaoException e) {
			throw new DaoException(e.getMessage(), e);
		} finally {
			commonExecuter.CloseDao();
		}
	}

	@Override
	public void as_edge_host_update_ImportFromRPS(int rhostid,
			Date lastupdated,
			String rhostname, int isVisible, int appStatus,
			int rhostType,String hostuuid, boolean isvm, int[] result) {
		String SQL = "";
		List<Object> paras = new ArrayList<Object>();
		EdgeDaoCommonExecuter commonExecuter = new EdgeDaoCommonExecuter();
		int t_rhostid = rhostid;
		int insert = 1;
		try {
			if (t_rhostid == 0) {
				// Insert
				if(isvm)
					rhostname=getHostNameByVMINSTUUID(hostuuid);
				SQL = "INSERT INTO as_edge_host(lastupdated, rhostname, IsVisible, appStatus, rhostType) "
						+ "values(?, ?, ?, ?, ?)";
				paras.clear();
				paras.add(new Timestamp(lastupdated.getTime()));
				paras.add(EdgeDaoCommonExecuter.getSafeString(rhostname));
				paras.add(new Integer(isVisible));
				paras.add(new Integer(appStatus));
				paras.add(new Integer(rhostType));
				commonExecuter.ExecuteDao(SQL, paras);

				SQL = "VALUES IDENTITY_VAL_LOCAL()";
				List<Integer> ids = new ArrayList<Integer>();
				commonExecuter.ExecuteDao(SQL, null, ids, 0);
				t_rhostid = (!ids.isEmpty()) ? ids.get(0) : 0;
				
				if(isvm && t_rhostid>0){// the node is hbbu node , then insert a new data in esx_host_map table
					List<EdgeEsxHostMapInfo> esxmapList = new ArrayList<EdgeEsxHostMapInfo>();
					SQL = "SELECT * FROM DBO.AS_EDGE_ESX_HOST_MAP WHERE VMINSTANCEUUID = ?";
					paras.clear();
					paras.add(hostuuid);
					commonExecuter.ExecuteDao(SQL, paras,EdgeEsxHostMapInfo.class,esxmapList);
					if(esxmapList.size()>0){
						EdgeEsxHostMapInfo esxmapinfo = esxmapList.get(0);
						esxmapinfo.setHostId(t_rhostid);
						SQL = "INSERT INTO DBO.AS_EDGE_ESX_HOST_MAP (ESXID , HOSTID , STATUS , VMNAME , VMUUID , VMINSTANCEUUID , ESXHOST , VMXPATH ) VALUES(?,?,?,?,?,?,?,?)";
						paras.clear();
						paras.add(esxmapinfo.getEsxId());
						paras.add(t_rhostid);
						paras.add(esxmapinfo.getStatus());
						paras.add(esxmapinfo.getVmName());
						paras.add(esxmapinfo.getVmUuid());
						paras.add(esxmapinfo.getVmInstanceUuid());
						paras.add(esxmapinfo.getEsxHost());
						paras.add(esxmapinfo.getVmXPath());
						commonExecuter.ExecuteDao(SQL, paras);
					}
				}
			} else {
				// Update
				if(isvm || StringUtil.isEmptyOrNull(rhostname)){
					SQL = "UPDATE as_edge_host "
							+ "SET lastupdated=?, IsVisible=?, appStatus=?, rhostType=? where rhostid=?";
					paras.clear();
					paras.add(new Timestamp(lastupdated.getTime()));
					paras.add(new Integer(isVisible));
					paras.add(new Integer(appStatus));
					paras.add(new Integer(rhostType));
					paras.add(new Integer(t_rhostid));
				}
				else {
					SQL = "UPDATE as_edge_host "
							+ "SET rhostname=?, lastupdated=?, IsVisible=?, appStatus=?, rhostType=? where rhostid=?";
					paras.clear();
					paras.add(rhostname == null ? "" : rhostname);
					paras.add(new Timestamp(lastupdated.getTime()));
					paras.add(new Integer(isVisible));
					paras.add(new Integer(appStatus));
					paras.add(new Integer(rhostType));
					paras.add(new Integer(t_rhostid));
				}	
				commonExecuter.ExecuteDao(SQL, paras);
				insert = 0;
			}
			result[0] = t_rhostid;
			result[1] = insert;			
		} catch (DaoException e) {
			throw new DaoException(e.getMessage(), e);
		} finally {
			commonExecuter.CloseDao();
		}
	}
	
	@Override
	public void as_edge_node_dest_update(int rhostid,
			int rpsId,
			int converterId,
			String datastoreName,
			String datastoreUUID,
			String policyUUID,
			String destination , String vmName) {
		String SQL = "";
		List<Object> paras = new ArrayList<Object>();
		List<Integer> hostIdList;
		EdgeDaoCommonExecuter commonExecuter = new EdgeDaoCommonExecuter();
		try {
			SQL = "SELECT ID FROM DBO.AS_EDGE_NODE_DEST_INFO WHERE HOSTID = ? AND RPSID = ? AND CONVERTERID=?";
			hostIdList = new ArrayList<Integer>();
			paras.add(rhostid);
			paras.add(rpsId);
			paras.add(converterId);
			commonExecuter.ExecuteDao(SQL, paras, hostIdList, 0);
			if (hostIdList.size() > 0) {
				//Update
				SQL = "UPDATE DBO.AS_EDGE_NODE_DEST_INFO "
						+ "SET rpsid=?, converterid=? , datastorename=?, datastoreuuid=?, policyuuid=?, destination=? ,vmName=? WHERE hostid=?";
				paras = new ArrayList<Object>();
				paras.add(rpsId);
				paras.add(converterId);
				paras.add(datastoreName);
				paras.add(datastoreUUID);
				paras.add(policyUUID);
				paras.add(destination);
				paras.add(vmName);
				paras.add(rhostid);
				commonExecuter.ExecuteDao(SQL, paras);
			} else {
				//Insert
				SQL = "INSERT INTO AS_EDGE_NODE_DEST_INFO(hostid, rpsid,converterid, datastorename, datastoreuuid, policyuuid, destination ,vmName) "
						+ "values(?, ?, ?,?, ?, ?, ?,?)";
				paras = new ArrayList<Object>();
				paras.add(rhostid);
				paras.add(rpsId);
				paras.add(converterId);
				paras.add(datastoreName);
				paras.add(datastoreUUID);
				paras.add(policyUUID);
				paras.add(destination);
				paras.add(vmName);
				commonExecuter.ExecuteDao(SQL, paras);
			}
		} catch (DaoException e) {
			throw new DaoException(e.getMessage(), e);
		} finally {
			commonExecuter.CloseDao();
		}
		
	}

	@Override
	public void as_edge_rps_info_by_hostID(int rhostid, List<RPSInfo> rpsInfo) {
		EdgeDaoCommonExecuter commonExecuter = new EdgeDaoCommonExecuter();
		try {
			String SQL = " SELECT rps.hostname as rpsHostName, " +
					" rps.port as rpsPort, " +
					" rps.protocol as rpsProtocol, " +
					" rps.username as rpsUserName, " +
					" rps.password as rpsPassword, " +
					" map.policyuuid as rpsPolicyUUID, " +
					" map.datastorename as rpsDataStore, " +
					" map.datastoreuuid as rpsDataStoreGuid " +
					" FROM dbo.as_edge_rps rps , dbo.as_edge_node_dest_info map " +
					" WHERE rps.id = map.rpsid and map.hostid = ? ";
			List<Object> paras = new ArrayList<Object>();
			paras.add(rhostid);
			commonExecuter.ExecuteDao(SQL, paras, RPSInfo.class, rpsInfo);
		} catch (DaoException e) {
			throw new DaoException(e.getMessage(), e);
		} finally {
			commonExecuter.CloseDao();
		}
	}
}
