/**
 * 
 */
package com.ca.arcserve.edge.app.base.appdaos;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcserve.edge.app.base.dao.DaoException;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceContext;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeApplicationType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RHAControlService;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RHASourceNode;

/**
 * @author lijwe02
 * 
 */
public class EdgeRHADaoImpl implements IEdgeRHADao {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ca.arcserve.edge.app.base.appdaos.IEdgeRHADao#as_edge_rha_cu(int, java.lang.String, int, int, int,
	 * java.lang.String, java.lang.String, int[])
	 */
	@Override
	public void as_edge_rha_cu(int id, String hostname, int protocol, int port, int visible, String userName,
			String password, int[] newId) {
		EdgeApplicationType appType = EdgeWebServiceContext.getApplicationType();
		String sqlStr = "";
		List<Object> pa = new ArrayList<Object>();
		EdgeDaoCommonExecuter commonExecuter = new EdgeDaoCommonExecuter();
		int t_id = id;
		try {
			if (appType == EdgeApplicationType.VirtualConversionManager) {
				if (id == 0) {
					t_id = getRHANodeId(hostname);
				}
				String encryptPassword = DaoFactory.getEncrypt().encryptString(password);
				if (t_id == -1) {
					// Insert
					sqlStr = "INSERT INTO as_edge_rha(hostname, protocol, port, username, password) values(?, ?, ?, ?, ?)";
					pa.add(EdgeDaoCommonExecuter.getSafeString(hostname));
					pa.add(new Integer(protocol));
					pa.add(new Integer(port));
					pa.add(EdgeDaoCommonExecuter.getSafeString(userName));
					pa.add(EdgeDaoCommonExecuter.getSafeString(encryptPassword));
					commonExecuter.ExecuteDao(sqlStr, pa);

					sqlStr = "VALUES IDENTITY_VAL_LOCAL()";
					List<Integer> ids = new ArrayList<Integer>();
					commonExecuter.ExecuteDao(sqlStr, null, ids, 0);
					t_id = (!ids.isEmpty()) ? ids.get(0) : 0;
				} else {
					// Update
					sqlStr = "update as_edge_rha set hostname=?, protocol=?, port=?, username=?, password=? where id=?";
					pa.add(hostname == null ? "" : hostname);
					pa.add(new Integer(protocol));
					pa.add(new Integer(port));
					pa.add(EdgeDaoCommonExecuter.getSafeString(userName));
					pa.add(EdgeDaoCommonExecuter.getSafeString(encryptPassword));
					pa.add(new Integer(t_id));
					commonExecuter.ExecuteDao(sqlStr, pa);
				}
				newId[0] = t_id;
			}
		} catch (DaoException e) {
			throw new DaoException(e.getMessage(), e);
		} finally {
			commonExecuter.CloseDao();
		}
	}

	private int getRHANodeId(String hostName) {

		EdgeDaoCommonExecuter commonExecuter = null;

		try {
			String sqlStr = "SELECT id FROM as_edge_rha WHERE UCASE(hostname) = UCASE(?)";

			List<Object> arguments = new ArrayList<Object>();
			arguments.add(EdgeDaoCommonExecuter.getSafeString(hostName));

			List<Integer> results = new ArrayList<Integer>();

			commonExecuter = new EdgeDaoCommonExecuter();
			commonExecuter.ExecuteDao(sqlStr, arguments, results, 0);

			return results.isEmpty() ? -1 : results.get(0);
		} catch (Exception e) {
			throw new DaoException(e.getMessage(), e);
		} finally {
			if (commonExecuter != null)
				commonExecuter.CloseDao();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ca.arcserve.edge.app.base.appdaos.IEdgeRHADao#as_edge_rha_scenario_cu(int, int, long, java.lang.String,
	 * int, int[])
	 */
	@Override
	public void as_edge_rha_scenario_cu(int id, int rhaId, long scenarioId, String scenarioName, int scenarioType,
			int[] newId) {
		EdgeApplicationType appType = EdgeWebServiceContext.getApplicationType();
		if (appType != EdgeApplicationType.VirtualConversionManager) {
			throw new DaoException("Only VCM is supported.");
		}
		String sqlStr = null;
		List<Object> pa = new ArrayList<Object>();
		EdgeDaoCommonExecuter commonExecuter = new EdgeDaoCommonExecuter();
		int t_id = id;
		try {
			if (id == 0) {
				t_id = getRHAScenarioId(rhaId, scenarioId);
			}
			if (t_id == -1) {
				// Insert
				sqlStr = "INSERT INTO as_edge_rha_scenario(rhaid, scenarioid, scenarioname, scenariotype) values(?, ?, ?, ?)";
				pa.add(new Integer(rhaId));
				pa.add(new Long(scenarioId));
				pa.add(EdgeDaoCommonExecuter.getSafeString(scenarioName));
				pa.add(new Integer(scenarioType));
				commonExecuter.ExecuteDao(sqlStr, pa);

				sqlStr = "VALUES IDENTITY_VAL_LOCAL()";
				List<Integer> ids = new ArrayList<Integer>();
				commonExecuter.ExecuteDao(sqlStr, null, ids, 0);
				t_id = (!ids.isEmpty()) ? ids.get(0) : 0;
			} else {
				// Update
				sqlStr = "update as_edge_rha_scenario set rhaid=?, scenarioid=?, scenarioname=?, scenariotype=? where id=?";
				pa.add(new Integer(rhaId));
				pa.add(new Long(scenarioId));
				pa.add(EdgeDaoCommonExecuter.getSafeString(scenarioName));
				pa.add(new Integer(scenarioType));
				pa.add(new Integer(t_id));
				commonExecuter.ExecuteDao(sqlStr, pa);
			}
			newId[0] = t_id;
		} catch (DaoException e) {
			throw new DaoException(e.getMessage(), e);
		} finally {
			commonExecuter.CloseDao();
		}
	}

	private int getRHAScenarioId(int rhaId, long scenarioId) {

		EdgeDaoCommonExecuter commonExecuter = null;

		try {
			String sqlStr = "SELECT id FROM as_edge_rha_scenario WHERE rhaid=? AND scenarioid=?";

			List<Object> arguments = new ArrayList<Object>();
			arguments.add(new Integer(rhaId));
			arguments.add(new Long(scenarioId));

			List<Integer> results = new ArrayList<Integer>();

			commonExecuter = new EdgeDaoCommonExecuter();
			commonExecuter.ExecuteDao(sqlStr, arguments, results, 0);

			return results.isEmpty() ? -1 : results.get(0);
		} catch (Exception e) {
			throw new DaoException(e.getMessage(), e);
		} finally {
			if (commonExecuter != null)
				commonExecuter.CloseDao();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ca.arcserve.edge.app.base.appdaos.IEdgeRHADao#as_edge_rha_scenario_host_map_cu(int, int,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, int)
	 */
	@Override
	public void as_edge_rha_scenario_host_map_cu(int rhaScenarioId, int hostId, String recoveryPointFolder,
			String vmInstanceUUID, String vmName, String hypervisorName, String masterHost, String masterIp,
			String replicaHost, String replicaIp, int converterId) {
		EdgeDaoCommonExecuter commonExecuter = new EdgeDaoCommonExecuter();
		List<Object> pa = new ArrayList<Object>();
		try {
			String sqlStr = null;

			if (isEdgeRHAHostExists(rhaScenarioId, hostId)) {
				// Update
				sqlStr = "UPDATE as_edge_rha_scenario_host_map set recoverypointfolder=?, vminstanceuuid=?, vmName=?, "
						+ "hypervisorName=?, masterHost=?, masterIp=?, replicaHost=?, replicaIp=?, converterid=? "
						+ "where rhaScenarioId=? and hostId=?";
				pa.add(EdgeDaoCommonExecuter.getSafeString(recoveryPointFolder));
				pa.add(EdgeDaoCommonExecuter.getSafeString(vmInstanceUUID));
				pa.add(EdgeDaoCommonExecuter.getSafeString(vmName));
				pa.add(EdgeDaoCommonExecuter.getSafeString(hypervisorName));
				pa.add(EdgeDaoCommonExecuter.getSafeString(masterHost));
				pa.add(EdgeDaoCommonExecuter.getSafeString(masterIp));
				pa.add(EdgeDaoCommonExecuter.getSafeString(replicaHost));
				pa.add(EdgeDaoCommonExecuter.getSafeString(replicaIp));
				pa.add(new Integer(converterId));
				pa.add(new Integer(rhaScenarioId));
				pa.add(new Integer(hostId));
			} else {
				// Insert
				sqlStr = "INSERT INTO as_edge_rha_scenario_host_map "
						+ "(rhaScenarioId, hostId, recoverypointfolder, vminstanceuuid, vmName, hypervisorName, "
						+ "masterHost, masterIp, replicaHost, replicaIp, converterid) "
						+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
				pa.add(new Integer(rhaScenarioId));
				pa.add(new Integer(hostId));
				pa.add(EdgeDaoCommonExecuter.getSafeString(recoveryPointFolder));
				pa.add(EdgeDaoCommonExecuter.getSafeString(vmInstanceUUID));
				pa.add(EdgeDaoCommonExecuter.getSafeString(vmName));
				pa.add(EdgeDaoCommonExecuter.getSafeString(hypervisorName));
				pa.add(EdgeDaoCommonExecuter.getSafeString(masterHost));
				pa.add(EdgeDaoCommonExecuter.getSafeString(masterIp));
				pa.add(EdgeDaoCommonExecuter.getSafeString(replicaHost));
				pa.add(EdgeDaoCommonExecuter.getSafeString(replicaIp));
				pa.add(new Integer(converterId));
			}

			commonExecuter.ExecuteDao(sqlStr, pa);
		} catch (Exception e) {
			throw new DaoException(e.getMessage(), e);
		} finally {
			commonExecuter.CloseDao();
		}
	}

	private boolean isEdgeRHAHostExists(int rhaScenarioId, int hostId) {
		EdgeDaoCommonExecuter commonExecuter = null;

		try {
			String sqlStr = "SELECT 1 FROM as_edge_rha_scenario_host_map shm WHERE shm.rhaScenarioId=? and shm.hostId=?";

			List<Object> arguments = new ArrayList<Object>();
			arguments.add(new Integer(rhaScenarioId));
			arguments.add(new Integer(hostId));

			List<Integer> results = new ArrayList<Integer>();

			commonExecuter = new EdgeDaoCommonExecuter();
			commonExecuter.ExecuteDao(sqlStr, arguments, results, 0);

			return results.isEmpty() ? false : true;
		} catch (Exception e) {
			throw new DaoException(e.getMessage(), e);
		} finally {
			if (commonExecuter != null)
				commonExecuter.CloseDao();
		}
	}

	@Override
	public void as_edge_source_node_list(String hostName, List<RHASourceNode> nodeList) {
		List<Object> pa = new ArrayList<Object>();

		String str = "select h.rhostid, h.rhostname as nodeName, rs.rhaid as rhaid, "
				+ "rs.scenarioid, shm.recoverypointfolder as recoveryPointFolder, shm.vmname as vmName, "
				+ "shm.vminstanceuuid as vmInstanceUUID, c.hostname as converter "
				+ "from as_edge_host h, as_edge_rha_scenario_host_map shm, as_edge_rha r, as_edge_rha_scenario rs, "
				+ "as_edge_host_OffsiteVCMConverters c "
				+ "where h.rhostid=shm.hostid and r.id=rs.rhaid and rs.id=shm.rhascenarioid and shm.converterid=c.id "
				+ "and h.isvisible=1 and UCASE(r.hostname) = UCASE(?)";
		pa.add(hostName);

		EdgeDaoCommonExecuter commonExecuter = new EdgeDaoCommonExecuter();
		try {
			commonExecuter.ExecuteDao(str, pa, RHASourceNode.class, nodeList);
		} finally {
			commonExecuter.CloseDao();
		}
	}

	@Override
	public void as_edge_rha_list(String serverNamePrefix, List<RHAControlService> controlServiceList) {
		List<Object> pa = new ArrayList<Object>();

		String str = "select id,hostname as server,protocol,port,username,password "
				+ "from as_edge_rha where hostname like ? order by server";
		if (serverNamePrefix != null) {
			pa.add(serverNamePrefix.trim() + "%");
		} else {
			pa.add("%");
		}

		EdgeDaoCommonExecuter commonExecuter = new EdgeDaoCommonExecuter();
		try {
			commonExecuter.ExecuteDao(str, pa, RHAControlService.class, controlServiceList);
		} catch (Exception e) {
			throw new DaoException(e.getMessage(), e);
		} finally {
			commonExecuter.CloseDao();
		}
	}

	@Override
	public void as_edge_host_offsitevcmconverters_getByHostId(int rhostId, List<EdgeOffsiteVCMConverterInfo> converterList) {
		List<Object> pa = new ArrayList<Object>();

		String str = "SELECT ID, HOSTNAME, PORT, PROTOCOL, USERNAME, PASSWORD, UUID "
				+ "FROM AS_EDGE_HOST_OFFSITEVCMCONVERTERS C, AS_EDGE_RHA_SCENARIO_HOST_MAP SHM, AS_EDGE_HOST H "
				+ "WHERE H.RHOSTID=SHM.HOSTID AND SHM.CONVERTERID = C.ID AND H.RHOSTID=?";
		pa.add(rhostId);

		EdgeDaoCommonExecuter commonExecuter = new EdgeDaoCommonExecuter();
		try {
			commonExecuter.ExecuteDao(str, pa, EdgeOffsiteVCMConverterInfo.class, converterList);
		} finally {
			commonExecuter.CloseDao();
		}

	}

	@Override
	public void as_edge_rha_getHostIdForNodeImportedFromRHA(String rhaServerName, long scenarioId, String sourceNodeName, String sourceVmInstanceUuid, int[] hostId) {
		List<Object> pa = new ArrayList<Object>();
		String str;
		if (!StringUtil.isEmptyOrNull(rhaServerName)) {
			str = "select h.rhostid from as_edge_host h, as_edge_rha r, as_edge_rha_scenario rs, as_edge_rha_scenario_host_map shm "
					+ "where h.rhostid = shm.hostid and r.id = rs.rhaid and rs.id=shm.rhascenarioid and r.hostname = ? "
					+ "and rs.scenarioid=? and h.rhostname = ? and shm.vminstanceuuid=? and h.isvisible=1";
			pa.add(rhaServerName);
			pa.add(scenarioId);
			pa.add(EdgeDaoCommonExecuter.getSafeString(sourceNodeName));
			pa.add(EdgeDaoCommonExecuter.getSafeString(sourceVmInstanceUuid));
		} else {
			str = "select h.rhostid from as_edge_host h, as_edge_rha_scenario rs, as_edge_rha_scenario_host_map shm "
					+ "where h.rhostid = shm.hostid and rs.id=shm.rhascenarioid "
					+ "and rs.scenarioid=? and h.rhostname = ? and shm.vminstanceuuid=? and h.isvisible=1";
			pa.add(scenarioId);
			pa.add(EdgeDaoCommonExecuter.getSafeString(sourceNodeName));
			pa.add(EdgeDaoCommonExecuter.getSafeString(sourceVmInstanceUuid));
		}

		EdgeDaoCommonExecuter commonExecuter = new EdgeDaoCommonExecuter();
		List<Integer> hostIdList = new ArrayList<Integer>();
		try {
			commonExecuter.ExecuteDao(str, pa, hostIdList, 0);
		} finally {
			commonExecuter.CloseDao();
		}
		if (hostIdList.size() > 0) {
			if (hostIdList.size() > 1) {
				throw new DaoException("More than one nodes fit the query condition:" + pa);
			}
			hostId[0] = hostIdList.get(0);
		} else {
			hostId[0] = 0;
		}
	}
}
