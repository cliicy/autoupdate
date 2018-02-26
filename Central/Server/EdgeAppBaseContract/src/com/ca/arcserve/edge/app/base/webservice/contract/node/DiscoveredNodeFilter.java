package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;

public class DiscoveredNodeFilter implements Serializable {

	private static final long serialVersionUID = -2287284094363409349L;
	
	private String namePattern;
	private String domainPattern;

	private boolean showHidden;
	
	public String getNamePattern() {
		return namePattern;
	}
	public void setNamePattern(String namePattern) {
		this.namePattern = namePattern;
	}
	public boolean isShowHidden() {
		return showHidden;
	}
	public void setShowHidden(boolean showHidden) {
		this.showHidden = showHidden;
	}
	public void setDomainPattern(String domainPattern) {
		this.domainPattern = domainPattern;
	}
	public String getDomainPattern() {
		return domainPattern;
	}

}
