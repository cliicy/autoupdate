package com.ca.arcserve.edge.app.base.webservice.actioncenter;

import java.util.List;

import com.ca.arcserve.edge.app.base.webservice.actioncenter.exceptions.InvalidCategoryParamException;
import com.ca.arcserve.edge.app.base.webservice.actioncenter.exceptions.NoActionOwnerException;
import com.ca.arcserve.edge.app.base.webservice.contract.actioncenter.ActionCategory;
import com.ca.arcserve.edge.app.base.webservice.contract.actioncenter.ActionItem;
import com.ca.arcserve.edge.app.base.webservice.contract.actioncenter.ActionItemId;
import com.ca.arcserve.edge.app.base.webservice.contract.actioncenter.ActionSeverity;

public interface IActionCenter
{
	ActionItemId addActionItem(
		ActionCategory actionCategory,
		int action,
		ActionSeverity severity,
		String description,
		Object categoryParam
		) throws NoActionOwnerException, InvalidCategoryParamException;
	
	void deleteActionItem(
		ActionItemId id );
	
	List<ActionItem> getActionItems(
		ActionCategory actionCategory,
		Object categoryParam
		) throws NoActionOwnerException, InvalidCategoryParamException;
	
	void deleteActionItems(
		ActionCategory actionCategory,
		Object categoryParam
		) throws NoActionOwnerException, InvalidCategoryParamException;
	
	void deleteActionItem(
		ActionCategory actionCategory,
		Object categoryParam,
		int action
		) throws NoActionOwnerException, InvalidCategoryParamException;
	
	List<ActionItem> getAllActionItems(
		) throws NoActionOwnerException, InvalidCategoryParamException;
}
