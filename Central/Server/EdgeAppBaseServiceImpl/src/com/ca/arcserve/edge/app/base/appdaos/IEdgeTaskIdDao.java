package com.ca.arcserve.edge.app.base.appdaos;

import com.ca.arcserve.edge.app.base.dao.Out;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;

public interface IEdgeTaskIdDao {
	@StoredProcedure(name = "as_edge_get_next_taskid")
	void as_edge_get_next_taskid(@Out long[] taskId );
}
