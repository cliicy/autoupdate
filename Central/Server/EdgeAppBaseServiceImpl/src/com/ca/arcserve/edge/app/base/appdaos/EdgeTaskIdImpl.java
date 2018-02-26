package com.ca.arcserve.edge.app.base.appdaos;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.edge.app.base.dao.DaoException;

public class EdgeTaskIdImpl implements IEdgeTaskIdDao {

	@Override
	public void as_edge_get_next_taskid(long[] taskId) {
		// TODO Auto-generated method stub
		String sqlStrLockTable = "lock table as_edge_task_id in exclusive mode";
		String sqlGetCount = "select count(*) from as_edge_task_id";
		String sqlGetId = "select NextTaskID from as_edge_task_id";
		String sqlInsert = "insert into as_edge_task_id (NextTaskID, LastUpdate) values (?, GetCurrentUTCTime())";
		String sqlUpdate = "update as_edge_task_id set NextTaskID=?, LastUpdate=GetCurrentUTCTime()";
		
		EdgeDaoCommonExecuter ede = new EdgeDaoCommonExecuter();
		List<Object> pa = new ArrayList<Object>();
		
		try {
			ede.BeginTrans();
			
			ede.ExecuteDao(sqlStrLockTable, null);
			
			List<Integer> count = new ArrayList<Integer>();
			ede.ExecuteDao(sqlGetCount, null, count, 1);
			if(count.get(0) == 0) {
				pa.add(new Long(2));
				ede.ExecuteDao(sqlInsert, pa);
				taskId[0] = 1;
			}
			else {
				List<Long> nextTaskId = new ArrayList<Long>();
				ede.ExecuteDao(sqlGetId, pa,nextTaskId);
				pa.add(new Long(nextTaskId.get(0).longValue() + 1));
				ede.ExecuteDao(sqlUpdate, pa);
				taskId[0] = nextTaskId.get(0).longValue();
			}

			ede.CommitTrans();
		} catch (Exception e) {
			ede.RollbackTrans();
			throw new DaoException(e.getMessage(), e);
		} finally {
			ede.CloseDao();
		}
	}
}
