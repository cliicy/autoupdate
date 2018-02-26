package com.ca.arcserve.edge.app.base.webservice.contract.configuration;

import java.io.Serializable;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;

public class DBConfigInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3552002494116409427L;

	private String sqlServer;
	private String instance;
	private AuthenticationType authentication;
	private String authUserName;
	private @NotPrintAttribute String authPassword;
	private int serverPort;
	private DBConnectionPoolConfig dbConnPoolConfig;
	
	public String getAuthUserName() {
		return authUserName;
	}

	public void setAuthUserName(String authUserName) {
		this.authUserName = authUserName;
	}

	public String getAuthPassword() {
		return authPassword;
	}

	public void setAuthPassword(String authPassword) {
		this.authPassword = authPassword;
	}

	public String getSqlServer() {
		return sqlServer;
	}

	public void setSqlServer(String sqlServer) {
		this.sqlServer = sqlServer;
	}

	public String getInstance() {
		return instance;
	}

	public void setInstance(String instance) {
		this.instance = instance;
	}

	public AuthenticationType getAuthentication() {
		return authentication;
	}

	public void setAuthentication(AuthenticationType authentication) {
		this.authentication = authentication;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public DBConnectionPoolConfig getDbConnPoolConfig() {
		return dbConnPoolConfig;
	}

	public void setDbConnPoolConfig(DBConnectionPoolConfig dbConnPoolConfig) {
		this.dbConnPoolConfig = dbConnPoolConfig;
	}

	/**
	 * 
	 */
	public enum AuthenticationType {
		WindowsAuthentication(1), Mixed(2);
		
		private int value;
		private AuthenticationType(int val) {
			this.value = val;
		}
		public int getValue() {
			return this.value;
		}
		
		public static AuthenticationType parseInt(int val) {
			AuthenticationType type;
			switch(val) {
			case 1:
				type = AuthenticationType.WindowsAuthentication;
				break;
				
			case 2:
				type = AuthenticationType.Mixed;
				break;
				
			default:
				type = AuthenticationType.Mixed;
				break;
			}
			
			return type;
		}
	}
	
	@Override
	public boolean equals(Object otherObject){
		if( this == otherObject )
			return true;
		if( null == otherObject ){
			return false;
		}
		if( getClass() != otherObject.getClass() ){
			return false;
		}
		
		DBConfigInfo other = (DBConfigInfo)otherObject;

		if(authentication == other.authentication) {
			if(this.authentication != AuthenticationType.WindowsAuthentication
					&& (!StringUtil.isEqual(authUserName, other.authUserName) ||
					!StringUtil.isEqual(authPassword, other.authPassword)))
				return false;
			
		} else
			return false;

		return StringUtil.isEqual(sqlServer, other.sqlServer) &&
				StringUtil.isEqual(instance, other.instance) &&
				serverPort == other.serverPort &&
				dbConnPoolConfig.equals(other.dbConnPoolConfig);
	}
}
