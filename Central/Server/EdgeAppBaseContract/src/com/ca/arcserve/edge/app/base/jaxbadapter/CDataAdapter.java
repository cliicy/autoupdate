package com.ca.arcserve.edge.app.base.jaxbadapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class CDataAdapter extends XmlAdapter<String, String> {

	@Override
	public String marshal(String arg0) throws Exception {
		if(arg0==null || arg0.isEmpty()) return arg0;
		return "<![CDATA[" + arg0 + "]]>";

	}

	@Override
	public String unmarshal(String arg0) throws Exception {
		if(arg0==null) return arg0;
		int len = arg0.length();
		if(arg0.indexOf("<![CDATA[")==-1) return arg0;
		if(len<12) return arg0;
		String value =  arg0.substring(9, len-3);
		return value;
	}

}
