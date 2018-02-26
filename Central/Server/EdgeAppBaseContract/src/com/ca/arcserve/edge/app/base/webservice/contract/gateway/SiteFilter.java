package com.ca.arcserve.edge.app.base.webservice.contract.gateway;

import java.io.Serializable;

public class SiteFilter implements Serializable
{
	private static final long serialVersionUID = -378295949985863115L;

	// 0:all 1:valid 
	private int isGatewayValid;
	private String namePattern;
	
	public int getIsGatewayValid() {
		return isGatewayValid;
	}

	/**
	 * filter all/valid gateway
	 * @param isGatewayValid 0:all 1:valid 
	 */
	public void setIsGatewayValid(int isGatewayValid) {
		this.isGatewayValid = isGatewayValid;
	}

	public String getNamePattern()
	{
		return namePattern;
	}

	public void setNamePattern( String namePattern )
	{
		this.namePattern = namePattern;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append( "SiteFilter { " );
		sb.append( "isGatewayValid = " + isGatewayValid );
		sb.append( ", namePattern = " + namePattern );
		sb.append( " }" );
		return sb.toString();
	}
}
