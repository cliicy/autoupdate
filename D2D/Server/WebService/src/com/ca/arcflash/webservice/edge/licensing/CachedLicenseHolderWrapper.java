/**
 * 
 */
package com.ca.arcflash.webservice.edge.licensing;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * @author lijwe02
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@Deprecated
public class CachedLicenseHolderWrapper {
	private ArrayList<CachedLicenseHolder> holderList = new ArrayList<CachedLicenseHolder>();

	public ArrayList<CachedLicenseHolder> getHolderList() {
		return holderList;
	}

	public void setHolderList(ArrayList<CachedLicenseHolder> holderList) {
		this.holderList = holderList;
	}
}
