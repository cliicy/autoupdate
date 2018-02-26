package com.ca.arcserve.edge.app.base.webservice.contract.arcserve;

import java.io.Serializable;

public class ASBUServerStatusInfo implements Serializable{

	private static final long serialVersionUID = -2764634553645416426L;
	
	private int value;
	private String name;
	private String message;
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append( this.getClass().getSimpleName() + " { " );
		sb.append( "value = " + value );
		sb.append( ", name = " + name );
		sb.append( ", message = " + message );
		sb.append( " }" );
		return sb.toString();
	}

}
