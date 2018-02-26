package com.ca.arcserve.edge.app.base.webservice.actioncenter;

import java.util.HashMap;
import java.util.Map;

import com.ca.arcserve.edge.app.base.webservice.contract.actioncenter.ActionCategory;
import com.ca.arcserve.edge.app.base.webservice.gateway.SiteActionOwner;

public class ActionOwnerRegistry
{
	private static ActionOwnerRegistry instance = new ActionOwnerRegistry();
	private static Map<ActionCategory, IActionOwner> registry = new HashMap<>();
	
	static
	{
		registry.put( ActionCategory.SiteManagement, new SiteActionOwner() );
	}
	
	private ActionOwnerRegistry()
	{
	}
	
	public static ActionOwnerRegistry getInstance()
	{
		return instance;
	}
	
	public IActionOwner getActionOwner( ActionCategory actionCategory )
	{
		return registry.get( actionCategory );
	}
}
