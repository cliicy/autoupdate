package com.ca.arcserve.edge.app.base.webservice.contract.node.entity;

import java.io.Serializable;

import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;

public class ConverterSummary implements Serializable{
	private static final long serialVersionUID = 2470836096193337089L;
	
	private int hostId;
	private int converterId;
	private String converter;
	private String converterUsername;
	private String  converterPassword;
	private int converterPort;
	private int converterProtocol;

	public int getConverterId() {
		return converterId;
	}
	public void setConverterId(int converterId) {
		this.converterId = converterId;
	}
	public String getConverter() {
		return converter;
	}
	public void setConverter(String converter) {
		this.converter = converter;
	}
	public String getConverterUsername() {
		return converterUsername;
	}
	public void setConverterUsername(String converterUsername) {
		this.converterUsername = converterUsername;
	}
	public String getConverterPassword() {
		return converterPassword;
	}
	public void setConverterPassword(String converterPassword) {
		this.converterPassword = converterPassword;
	}
	public int getConverterPort() {
		return converterPort;
	}
	public void setConverterPort(int converterPort) {
		this.converterPort = converterPort;
	}
	public int getConverterProtocol() {
		return converterProtocol;
	}
	public void setConverterProtocol(int converterProtocol) {
		this.converterProtocol = converterProtocol;
	}
	public int getHostId() {
		return hostId;
	}
	public void setHostId(int hostId) {
		this.hostId = hostId;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConverterSummary other = (ConverterSummary) obj;
		if(hostId != other.getHostId())
			return false;
		if(converterId != other.getConverterId())
			return false;
		if(!StringUtil.isEqual(converter, other.getConverter()))
			return false;
		if(!StringUtil.isEqual(converterUsername, other.getConverterUsername()))
			return false;
		if(!StringUtil.isEqual(converterPassword, other.getConverterPassword()))
			return false;
		if(converterPort != other.getConverterPort())
			return false;
		if(converterProtocol != other.getConverterProtocol())
			return false;
		return true;
	}
	
	public void update(ConverterSummary other) {
		if (other == null)
			return;
		if(hostId != other.getHostId())
			hostId = other.getHostId();
		if(converterId != other.getConverterId())
			converterId = other.getConverterId();
		if(!StringUtil.isEqual(converter, other.getConverter()))
			converter = other.getConverter();
		if(!StringUtil.isEqual(converterUsername, other.getConverterUsername()))
			converterUsername = other.getConverterUsername();
		if(!StringUtil.isEqual(converterPassword, other.getConverterPassword()))
			converterPassword = other.getConverterPassword();
		if(converterPort != other.getConverterPort())
			converterPort = other.getConverterPort();
		if(converterProtocol != other.getConverterProtocol())
			converterProtocol = other.getConverterProtocol();
	}
}
