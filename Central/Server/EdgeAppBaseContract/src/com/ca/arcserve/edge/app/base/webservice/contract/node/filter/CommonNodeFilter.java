package com.ca.arcserve.edge.app.base.webservice.contract.node.filter;

import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;

public class CommonNodeFilter extends NodeFilter {

	private static final long serialVersionUID = -8845539886321623536L;
	
	private String nodeNamePattern;
	private int applicationBitmap;
	private int osBitmap;
	private int hostTypeBitmap;
	
	public CommonNodeFilter() {
		super(NodeFilterType.Common);
	}
	
	@Override
	public boolean isEnabled() {
		return !StringUtil.isEmptyOrNull(nodeNamePattern) || applicationBitmap > 0 || osBitmap > 0 || hostTypeBitmap > 0;
	}
	
	public String getNodeNamePattern() {
		return nodeNamePattern;
	}

	public void setNodeNamePattern(String nodeNamePattern) {
		this.nodeNamePattern = nodeNamePattern;
	}

	public int getApplicationBitmap() {
		return applicationBitmap;
	}

	public void setApplicationBitmap(int applicationBitmap) {
		this.applicationBitmap = applicationBitmap;
	}

	public int getOsBitmap() {
		return osBitmap;
	}

	public void setOsBitmap(int osBitmap) {
		this.osBitmap = osBitmap;
	}

	public int getHostTypeBitmap() {
		return hostTypeBitmap;
	}

	public void setHostTypeBitmap(int hostTypeBitmap) {
		this.hostTypeBitmap = hostTypeBitmap;
	}

}
