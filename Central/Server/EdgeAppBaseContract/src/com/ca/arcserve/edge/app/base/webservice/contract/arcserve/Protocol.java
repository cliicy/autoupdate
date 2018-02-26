package com.ca.arcserve.edge.app.base.webservice.contract.arcserve;

import javax.xml.bind.annotation.XmlType;

import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;


@XmlType( namespace = "com.ca.arcserve.edge.app.base.webservice.contract.arcserve" )
public enum Protocol
{
	UnKnown,
	Http,
	Https;
	
	public static Protocol parse(int value) {
		switch (value) {
		case 1: return Http;
		case 2: return Https;
		default: return UnKnown;
		}
	}
	
	public static Protocol parse(String value) {
		if(StringUtil.isEmptyOrNull(value))
			return UnKnown;
		value = value.toLowerCase();
		if(value.startsWith("https"))
			return Https;
		if(value.startsWith("http"))
			return Http;
		return UnKnown;
	}
}
