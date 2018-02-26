package com.ca.arcserve.edge.app.base.webservice;

import java.util.List;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.actioncenter.ActionItem;
import com.ca.arcserve.edge.app.base.webservice.contract.actioncenter.ActionItemId;

public interface IActionCenterService
{
	void deleteActionItem(
		ActionItemId id
		) throws EdgeServiceFault;
	
	List<ActionItem> getAllActionItems(
		) throws EdgeServiceFault;
}
