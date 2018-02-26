package com.ca.arcserve.edge.app.base.appdaos;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcserve.edge.app.base.dao.DaoException;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.vcm.VCMConnectionInfo;

public class EdgeVCMConnectionDAOImpl implements IEdgeVCMConnectionDAO {

	public void update(int id, String hostname, String username, String password,
			String uuid, int protocol, int port) {
		List<Object> pa = new ArrayList<Object>();
		EdgeDaoCommonExecuter ede = new EdgeDaoCommonExecuter();
		
		try {
			String str = "update as_edge_vcm_connection_info set hostname=?, username=?, password=?, uuid=?, protocol=?, port=?, " +
					"lastUpdateTime=GetCurrentUTCTime() where id=?";
			pa.add(EdgeDaoCommonExecuter.getSafeString(hostname));
			pa.add(EdgeDaoCommonExecuter.getSafeString(username));
			pa.add(DaoFactory.getEncrypt().encryptString(password));
			pa.add(DaoFactory.getEncrypt().encryptString(uuid));
			pa.add(new Integer(protocol));
			pa.add(new Integer(port));
			pa.add(new Integer(id));
			
			ede.ExecuteDao(str, pa);
		} catch (Exception e) {
			throw new DaoException(e.getMessage(), e);
		} finally {
			if (ede!=null)
				ede.CloseDao();
		}
	}

	public void addVCMVMMap(int policyID, int vcmID) {
		List<Object> pa = new ArrayList<Object>();
		EdgeDaoCommonExecuter ede = new EdgeDaoCommonExecuter();
		
		try {
			String str = "INSERT INTO as_edge_vcm_vm_map (vmID, vcmid) "
				+ "VALUES (?, ?)";
			pa.add(new Integer(policyID));
			pa.add(new Integer(vcmID));
			
			ede.ExecuteDao(str, pa);
		} catch (Exception e) {
			throw new DaoException(e.getMessage(), e);
		} finally {
			if (ede!=null)
				ede.CloseDao();
		}
		
	}

	public int insert(String hostname, String username, String password,
			String uuid, int protocol, int port) {
		List<Object> pa = new ArrayList<Object>();
		EdgeDaoCommonExecuter ede = new EdgeDaoCommonExecuter();
		
		try {
			String str = "INSERT INTO as_edge_vcm_connection_info (hostname, username, password, uuid, protocol, port, lastUpdateTime, lastNofityStatus) "
				+ "VALUES (?, ?, ?, ?, ?, ?, GetCurrentUTCTime(), ?)";
			pa.add(EdgeDaoCommonExecuter.getSafeString(hostname));
			pa.add(EdgeDaoCommonExecuter.getSafeString(username));
			pa.add(DaoFactory.getEncrypt().encryptString(password));
			pa.add(DaoFactory.getEncrypt().encryptString(uuid));
			pa.add(new Integer(protocol));
			pa.add(new Integer(port));
			pa.add(0);
			//pa.add(new Integer(VCMNotificationStatus.Init.ordinal()));
			
			ede.ExecuteDao(str, pa);
			
			ede.BeginTrans();
			List<Integer> result = new ArrayList<Integer>();
			str = "LOCK TABLE as_edge_vcm_connection_info IN EXCLUSIVE MODE";
			ede.ExecuteDao(str, null);
			str = "SELECT max(id) from as_edge_vcm_connection_info";
			ede.ExecuteDao(str, null, result, 0);
			ede.CommitTrans();
			
			if(!result.isEmpty())
				return result.get(0);
			return -1;
		} catch (Exception e) {
			if (ede!=null)
				ede.RollbackTrans();
			throw new DaoException(e.getMessage(), e);
		} finally {
			if (ede!=null)
				ede.CloseDao();
		}
	}

	@Override
	public int isVCMExists(String hostname) {
		List<Object> pa = new ArrayList<Object>();
		EdgeDaoCommonExecuter ede = new EdgeDaoCommonExecuter();
		
		try {
			String str = "select id from as_edge_vcm_connection_info where hostname=? ";
			pa.add(EdgeDaoCommonExecuter.getSafeString(hostname));
			
			List<Integer> result = new ArrayList<Integer>();
			ede.ExecuteDao(str, pa, result, 0);
			
			if (result.isEmpty())
				return -1;
			else
				return result.get(0).intValue();
		} catch (Exception e) {
			throw new DaoException(e.getMessage(), e);
		} finally {
			if (ede!=null)
				ede.CloseDao();
		}
	}

	@Override
	public void deleteAll() {
		List<Object> pa = new ArrayList<Object>();
		EdgeDaoCommonExecuter ede = new EdgeDaoCommonExecuter();
		
		try {
			String str = "delete from as_edge_vcm_connection_info";
			ede.ExecuteDao(str, pa);
			
		} catch (Exception e) {
			throw new DaoException(e.getMessage(), e);
		} finally {
			if (ede!=null)
				ede.CloseDao();
		}
	}

	@Override
	public void clearVCMVMMap() {
		List<Object> pa = new ArrayList<Object>();
		EdgeDaoCommonExecuter ede = new EdgeDaoCommonExecuter();
		
		try {
			String str = "delete from as_edge_vcm_vm_map";			
			ede.ExecuteDao(str, pa);
		} catch (Exception e) {
			throw new DaoException(e.getMessage(), e);
		} finally {
			if (ede!=null)
				ede.CloseDao();
		}
		
	}

	@Override
	public void getVCMConnection(List<VCMConnectionInfo> connectionList) {
		List<Object> pa = new ArrayList<Object>();
		EdgeDaoCommonExecuter ede = new EdgeDaoCommonExecuter();
		
		try {
			String str = "select * from as_edge_vcm_connection_info";
			ede.ExecuteDao(str, pa,VCMConnectionInfo.class, connectionList);
			
			for (VCMConnectionInfo connection:connectionList){
				connection.setPassword(CommonService.getInstance().getNativeFacade().decrypt(connection.getPassword()));
				connection.setUuid(CommonService.getInstance().getNativeFacade().decrypt(connection.getUuid()));
			}
		} catch (Exception e) {
			throw new DaoException(e.getMessage(), e);
		} finally {
			if (ede!=null)
				ede.CloseDao();
		}
	}

	@Override
	public void getVMIDList(List<Integer> idList) {
		List<Object> pa = new ArrayList<Object>();
		EdgeDaoCommonExecuter ede = new EdgeDaoCommonExecuter();
		
		try {
			String str = "select vmid from as_edge_vcm_vm_map";			
			ede.ExecuteDao(str, pa, idList, 0);
		} catch (Exception e) {
			throw new DaoException(e.getMessage(), e);
		} finally {
			if (ede!=null)
				ede.CloseDao();
		}
	}

	@Override
	public void getImportedVMInstaceUUID(int id, List<String> instanceUUID) {
		List<Object> pa = new ArrayList<Object>();
		EdgeDaoCommonExecuter ede = new EdgeDaoCommonExecuter();
		
		try {
			String str = "select b.vmInstanceUuid from as_edge_vcm_vm_map a, as_edge_esx_host_map b where a.vmid = ? and a.vmid = b.hostid";
			pa.add(id);
			ede.ExecuteDaoStringResult(str, pa, instanceUUID, 0);
		} catch (Exception e) {
			throw new DaoException(e.getMessage(), e);
		} finally {
			if (ede!=null)
				ede.CloseDao();
		}
	}

}
