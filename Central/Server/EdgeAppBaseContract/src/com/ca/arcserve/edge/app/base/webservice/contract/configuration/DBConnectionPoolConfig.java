package com.ca.arcserve.edge.app.base.webservice.contract.configuration;

import java.io.Serializable;

public class DBConnectionPoolConfig implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3119638996337276314L;

	private int maxConnections;
	private int minConnections;
	public int getMaxConnections() {
		return maxConnections;
	}
	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}
	public int getMinConnections() {
		return minConnections;
	}
	public void setMinConnections(int minConnections) {
		this.minConnections = minConnections;
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
		
		DBConnectionPoolConfig other = (DBConnectionPoolConfig)otherObject;
		if( maxConnections == other.maxConnections &&
				minConnections == other.minConnections )
			return true;
		else 
			return false;
	}
	
}
