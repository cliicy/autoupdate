package com.ca.arcserve.edge.app.base.appdaos;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.edge.app.base.dao.DaoException;

public class EdgeD2DSyncDaoImpl implements IEdgeD2DSyncDao {

	@Override
	public int as_edge_Get_Sync_Status(int componentid, int branchid,
			List<EdgeSyncStatus> syncStatus) {
		// TODO Auto-generated method stub
		String sqlStr = "SELECT branchid, last_cache_id, status, change_status from as_edge_sync_history"
				+ " WHERE componentid = ? AND branchid = ?";
		EdgeDaoCommonExecuter ede = new EdgeDaoCommonExecuter();
		List<Object> pa = new ArrayList<Object>();

		try {
			pa.add(new Integer(componentid));
			pa.add(new Integer(branchid));
			ede.ExecuteDao(sqlStr, pa, EdgeSyncStatus.class, syncStatus);
		} catch (Exception e) {
			throw new DaoException(e.getMessage(), e);
		} finally {
			ede.CloseDao();
		}

		return 0;
	}

	@Override
	public int as_edge_Insert_Sync_History(long lastCacheId, int status,
			int componentid, int branchid) throws DaoException {
		// TODO Auto-generated method stub
		EdgeDaoCommonExecuter ede = new EdgeDaoCommonExecuter();
		List<Object> pa = new ArrayList<Object>();
		String sqlStr = "INSERT INTO as_edge_sync_history (last_cache_id, status, componentid, branchid)"
				+ " VALUES (?, ?, ?, ?)";
		try {
			pa.add(new Long(lastCacheId));
			pa.add(new Integer(status));
			pa.add(new Integer(componentid));
			pa.add(new Integer(branchid));
			return ede.ExecuteDaoWithGenerateKey(sqlStr, pa);
		} catch (Exception e) {
			throw new DaoException(e.getMessage(), e);
		} finally {
			ede.CloseDao();
		}
	}

	@Override
	public int as_edge_Update_Sync_Status(long lastCacheId, int status,
			int componentid, int branchid) {
		// TODO Auto-generated method stub
		String sqlStrQuery = "SELECT status FROM as_edge_sync_history WHERE componentid = "
				+ componentid + " AND branchid = " + branchid;
		String sqlStrUpdate = "UPDATE as_edge_sync_history SET last_cache_id = "
				+ lastCacheId
				+ ", status = "
				+ status
				+ " "
				+ "WHERE componentid = "
				+ componentid
				+ " AND branchid = "
				+ branchid + " ";
		String sqlStrInsert = "INSERT INTO as_edge_sync_history (last_cache_id, status, componentid, branchid) "
				+ "VALUES ("
				+ lastCacheId
				+ ", "
				+ status
				+ ", "
				+ componentid
				+ ", " + branchid + ") ";
		EdgeDaoCommonExecuter ede = new EdgeDaoCommonExecuter();
		try {

			int generateKey = -1;
			List<Integer> result = new ArrayList<Integer>();
			ede.ExecuteDao(sqlStrQuery, null, result, 0);
			if (!result.isEmpty()) {
				generateKey = ede.ExecuteDaoWithGenerateKey(sqlStrUpdate, null);
			} else {
				generateKey = ede.ExecuteDaoWithGenerateKey(sqlStrInsert, null);
			}

			if (generateKey != -1)
				return 0;
			return -1;
		} catch (Exception e) {
			throw new DaoException(e.getMessage(), e);
		} finally {
			ede.CloseDao();
		}
	}

	@Override
	public int as_edge_update_change_status(int componentid, int branchid,
			int changeStatus) {
		String sqlStrQuery = "SELECT status FROM as_edge_sync_history WHERE componentid = "
				+ componentid + " AND branchid = " + branchid;
		String sqlStrUpdate = "UPDATE as_edge_sync_history SET status = "
				+ changeStatus + " " + "WHERE componentid = " + componentid
				+ " AND branchid = " + branchid;
		String sqlStrInsert = "INSERT INTO as_edge_sync_history (last_cache_id, status, componentid, branchid) "
				+ "VALUES (0, 0, " + componentid + ", " + branchid + ")";
		EdgeDaoCommonExecuter ede = new EdgeDaoCommonExecuter();
		try {
			List<Integer> result = new ArrayList<Integer>();
			int generateKey = -1;
			ede.ExecuteDao(sqlStrQuery, null, result, 0);
			if (!result.isEmpty()) {
				generateKey = ede.ExecuteDaoWithGenerateKey(sqlStrUpdate, null);
			} else {
				generateKey = ede.ExecuteDaoWithGenerateKey(sqlStrInsert, null);
			}

			if (generateKey != -1)
				return 0;
			return -1;

		} catch (Exception e) {
			throw new DaoException(e.getMessage(), e);
		} finally {
			ede.CloseDao();
		}
	}

	@Override
	public int as_edge_Get_Sync_Status_ex(int componentid, int branchid,
			List<EdgeSyncStatusEx> syncStatus) {
		// TODO Auto-generated method stub
		return 0;
	}

}
