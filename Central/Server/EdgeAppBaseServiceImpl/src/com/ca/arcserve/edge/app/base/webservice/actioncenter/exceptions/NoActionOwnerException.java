package com.ca.arcserve.edge.app.base.webservice.actioncenter.exceptions;

import com.ca.arcserve.edge.app.base.webservice.contract.actioncenter.ActionCategory;

public class NoActionOwnerException extends ActionCenterException
{
	private static final long serialVersionUID = -1742069980755214248L;

	private ActionCategory actionCategory;
	
	public NoActionOwnerException( ActionCategory actionCategory )
	{
		this.actionCategory = actionCategory;
	}

	public ActionCategory getActionCategory()
	{
		return actionCategory;
	}
}
