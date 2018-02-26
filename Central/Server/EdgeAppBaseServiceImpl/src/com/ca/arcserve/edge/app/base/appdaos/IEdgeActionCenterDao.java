package com.ca.arcserve.edge.app.base.appdaos;

import java.sql.Types;
import java.util.List;

import com.ca.arcserve.edge.app.base.dao.Out;
import com.ca.arcserve.edge.app.base.dao.ResultSet;
import com.ca.arcserve.edge.app.base.dao.StoredProcedure;

public interface IEdgeActionCenterDao
{
	@StoredProcedure(name = "as_edge_actionCenter_AddActionItem")
	void addActionItem(
		int actionCategory,
		String categoryParam,
		int action,
		int severity,
		String description,
		@Out(jdbcType = Types.INTEGER) int[] newActionItemId );
	
	@StoredProcedure(name = "as_edge_actionCenter_DeleteActionItem")
	void deleteActionItem(
		int actionItemId );
	
	@StoredProcedure(name = "as_edge_actionCenter_GetActionItems")
	void getActionItems(
		int actionCategory,
		String categoryParam,
		@ResultSet List<EdgeActionItem> actionItemList );
	
	@StoredProcedure(name = "as_edge_actionCenter_DeleteActionItems")
	void deleteActionItems(
		int actionCategory,
		String categoryParam );
	
	@StoredProcedure(name = "as_edge_actionCenter_DeleteActionItemByAction")
	void deleteActionItemByAction(
		int actionCategory,
		String categoryParam,
		int action );
	
	@StoredProcedure(name = "as_edge_actionCenter_GetAllActionItems")
	void getAllActionItems(
		@ResultSet List<EdgeActionItem> actionItemList );
}
