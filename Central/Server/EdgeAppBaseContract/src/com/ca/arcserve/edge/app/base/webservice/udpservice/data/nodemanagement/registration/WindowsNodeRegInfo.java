package com.ca.arcserve.edge.app.base.webservice.udpservice.data.nodemanagement.registration;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.common.ASBUCredential;
import com.ca.arcserve.edge.app.base.webservice.contract.common.WebServiceConnectInfo;

public class WindowsNodeRegInfo extends NodeRegInfo implements Serializable
{
	private static final long serialVersionUID = 8022281566796871547L;

	private boolean hasD2D;
	private boolean hasASBU;
	private WebServiceConnectInfo d2dWebSvcConnectInfo;
	private ASBUCredential asbuCredential;
	private int asbuPort;
	
	public boolean hasD2D()
	{
		return hasD2D;
	}
	
	public void setHasD2D( boolean hasD2D )
	{
		this.hasD2D = hasD2D;
	}
	
	public boolean hasASBU()
	{
		return hasASBU;
	}
	
	public void setHasASBU( boolean hasASBU )
	{
		this.hasASBU = hasASBU;
	}
	
	public WebServiceConnectInfo getD2dWebSvcConnectInfo()
	{
		return d2dWebSvcConnectInfo;
	}
	
	public void setD2dWebSvcConnectInfo( WebServiceConnectInfo d2dWebSvcConnectInfo )
	{
		this.d2dWebSvcConnectInfo = d2dWebSvcConnectInfo;
	}
	
	public ASBUCredential getAsbuCredential()
	{
		return asbuCredential;
	}
	
	public void setAsbuCredential( ASBUCredential asbuCredential )
	{
		this.asbuCredential = asbuCredential;
	}
	
	public int getAsbuPort()
	{
		return asbuPort;
	}
	
	public void setAsbuPort( int asbuPort )
	{
		this.asbuPort = asbuPort;
	}
}
