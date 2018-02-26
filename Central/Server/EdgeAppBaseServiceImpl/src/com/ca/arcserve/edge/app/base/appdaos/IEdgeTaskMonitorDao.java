package com.ca.arcserve.edge.app.base.appdaos;

import java.sql.Types;
import java.util.Date;
import java.util.List;

import com.ca.arcserve.edge.app.base.dao.In;
import com.ca.arcserve.edge.app.base.dao.Out;
import com.ca.arcserve.edge.app.base.dao.ResultSet;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.taskmonitor.TaskStatus;

public interface IEdgeTaskMonitorDao {
	@StoredProcedure(name = "as_edge_task_register")
	void as_edge_task_register(
			Module module,
			@In(jdbcType = Types.VARCHAR) String target, 
			TaskStatus status,
			@In(jdbcType = Types.VARCHAR) String details,
			@Out int[] taskID );

	@StoredProcedure(name = "as_edge_task_set_started")
	void as_edge_task_set_started(
			int taskID,
			TaskStatus status,
			@In(jdbcType = Types.TIMESTAMP) Date startTime);
	
	@StoredProcedure(name = "as_edge_task_update_status")
	void as_edge_task_update_status( 
			int taskID, 
			TaskStatus status, 
			@In(jdbcType = Types.VARCHAR) String details,
			@In(jdbcType = Types.TIMESTAMP) Date endTime );

	@StoredProcedure(name = "as_edge_task_get_list")
	void as_edge_task_get_list(@ResultSet List<EdgeDaoTask> result);

	@StoredProcedure(name = "as_edge_task_delete")
	void as_edge_task_delete(int taskID);
	@StoredProcedure(name = "as_edge_task_get_list_by_module")
	void as_edge_task_get_list_by_module(Module module, @ResultSet List<EdgeDaoTask> result);
	@StoredProcedure(name = "as_edge_task_monitor_delete_by_moduleAndTarget")
	void as_edge_task_monitor_delete_by_moduleAndTarget(Module module, @In(jdbcType = Types.VARCHAR) String target);
}
