package com.ca.arcserve.edge.app.base.webservice.contract.externalLink;

import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeApplicationType;

public class ExternalLinkCreator {
	
	public static final String SUFFIX_CM = "CM";
	public static final String SUFFIX_VCM = "VCM";
	public static final String SUFFIX_VSPHERE = "VSphere";
	public static final String SUFFIX_REPORT= "Report";
	
	private String localeString;
	
	public ExternalLinkCreator( String localeString )
	{
		this.localeString = localeString;
	}
	
	public String create(String baseUrl,
			EdgeApplicationType application, 
			String keyName ){
		String url = baseUrl;
		String itemName = keyName;
		
		switch (application) {
		case CentralManagement:
			itemName += SUFFIX_CM;
			break;
		case VirtualConversionManager:
			itemName += SUFFIX_VCM;
			break;
		case vShpereManager:
			itemName += SUFFIX_VSPHERE;
			break;
		case Report:
			itemName += SUFFIX_REPORT;
			break;
		default:
			break;
		}
		
		url += "?key=" + itemName + "&lang=" + this.localeString;
		
		return url;
	}
}
