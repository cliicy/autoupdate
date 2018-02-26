/**
 * 
 */
package com.ca.arcflash.ha.model.manager;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * @author lijwe02
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class SessionPasswordHolderWrapper {
	private ArrayList<SessionPasswordHolder> holderList = new ArrayList<SessionPasswordHolder>();

	public ArrayList<SessionPasswordHolder> getHolderList() {
		return holderList;
	}

	public void setHolderList(ArrayList<SessionPasswordHolder> holderList) {
		this.holderList = holderList;
	}
}
