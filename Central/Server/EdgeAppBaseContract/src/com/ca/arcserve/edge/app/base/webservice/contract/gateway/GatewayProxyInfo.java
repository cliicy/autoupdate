package com.ca.arcserve.edge.app.base.webservice.contract.gateway;

import java.io.Serializable;

public class GatewayProxyInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3478713532115805604L;

	private GatewayProxyType proxyType;
	private String server;
	private int port;
	private boolean requireAuthentication;
	private String username;
	private String password;
	
	public GatewayProxyType getProxyType()
	{
		return proxyType;
	}
	public void setProxyType( GatewayProxyType proxyType )
	{
		this.proxyType = proxyType;
	}
	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public boolean isRequireAuthentication()
	{
		return requireAuthentication;
	}
	public void setRequireAuthentication( boolean requireAuthentication )
	{
		this.requireAuthentication = requireAuthentication;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append( this.getClass().getSimpleName() + " { " );
		sb.append( "proxyType = " + proxyType );
		sb.append( ", server = " + server );
		sb.append( ", port = " + port );
		sb.append( ", requireAuthentication = " + requireAuthentication );
		sb.append( ", username = " + username );
		sb.append( " }" );
		return sb.toString();
	}
	
}
