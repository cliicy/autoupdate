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
public class SessionPasswordHolder {
	private String afGuid;
	private ArrayList<String> passwordList;

	public SessionPasswordHolder() {
		super();
	}

	public SessionPasswordHolder(String afGuid, ArrayList<String> passwordList) {
		this.afGuid = afGuid;
		this.passwordList = passwordList;
	}

	public String getAfGuid() {
		return afGuid;
	}

	public void setAfGuid(String afGuid) {
		this.afGuid = afGuid;
	}

	public ArrayList<String> getPasswordList() {
		return passwordList;
	}

	public void setPasswordList(ArrayList<String> passwordList) {
		this.passwordList = passwordList;
	}
}
