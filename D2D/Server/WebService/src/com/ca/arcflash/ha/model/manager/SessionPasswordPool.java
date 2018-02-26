/**
 * 
 */
package com.ca.arcflash.ha.model.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * @author lijwe02
 * 
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SessionPasswordPool")
public class SessionPasswordPool {
	@XmlJavaTypeAdapter(SessionPasswordXmlAdapter.class)
	Map<String, ArrayList<String>> passwordPool = new HashMap<String, ArrayList<String>>();

	public void addPassword(String afGuid, String password) {
		ArrayList<String> passwordList = passwordPool.get(afGuid);
		if (passwordList == null) {
			passwordList = new ArrayList<String>();
			passwordPool.put(afGuid, passwordList);
		}
		if (passwordList.contains(password)) {
			return;
		}
		passwordList.add(0, password);
	}

	public void removePassword(String afGuid, String password) {
		ArrayList<String> passwordList = passwordPool.get(afGuid);
		if (passwordList == null) {
			return;
		}
		if (passwordList.contains(password)) {
			passwordList.remove(password);
		}
		if (passwordList.size() == 0) {
			passwordPool.remove(afGuid);
		}
	}

	public void clearPassword(String afGuid) {
		if (passwordPool.containsKey(afGuid)) {
			passwordPool.remove(afGuid);
		}
	}

	public ArrayList<String> getPasswordList(String afGuid) {
		if (afGuid != null) {
			return passwordPool.get(afGuid);
		}
		return null;
	}
}
