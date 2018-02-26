package com.ca.arcserve.edge.app.base.common.eventcenter.events;

import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SiteInfo;

public class NewSiteCreatedEvent extends BaseEdgeEvent
{
	private SiteInfo siteInfo;
	private GatewayEntity gateway;
	
	public NewSiteCreatedEvent( SiteInfo siteInfo, GatewayEntity gateway )
	{
		this.siteInfo = siteInfo;
		this.gateway = gateway;
	}

	public SiteInfo getSiteInfo()
	{
		return siteInfo;
	}

	public GatewayEntity getGateway()
	{
		return gateway;
	}
}
