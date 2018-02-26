package com.ca.arcserve.edge.app.base.webservice.contract.gateway;

import java.io.Serializable;

public class GatewayRegistrationResult implements Serializable
{
	private static final long serialVersionUID = -8571304494336109885L;
	
	private String consoleUuid = "";
	private String gatewayUuid = "";
	private GatewayId gatewayId = GatewayId.INVALID_GATEWAY_ID;
    private byte[] brokerCert;
	
	public String getConsoleUuid()
	{
		return consoleUuid;
	}

	public void setConsoleUuid( String consoleUuid )
	{
		this.consoleUuid = consoleUuid;
	}

	public String getGatewayUuid()
	{
		return gatewayUuid;
	}

	public void setGatewayUuid( String gatewayUuid )
	{
		this.gatewayUuid = gatewayUuid;
	}

	public GatewayId getGatewayId()
	{
		return gatewayId;
	}

	public void setGatewayId( GatewayId gatewayId )
	{
		if (gatewayId == null)
			gatewayId = GatewayId.INVALID_GATEWAY_ID;
		this.gatewayId = gatewayId;
	}

	public byte[] getBrokerCert() {
		return brokerCert;
	}

	public void setBrokerCert(byte[] brokerCert) {
		this.brokerCert = brokerCert;
	}
	
	
}
