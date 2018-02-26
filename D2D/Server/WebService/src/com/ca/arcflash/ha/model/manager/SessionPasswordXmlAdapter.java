/**
 * 
 */
package com.ca.arcflash.ha.model.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.service.CommonService;

/**
 * @author lijwe02
 * 
 */
public class SessionPasswordXmlAdapter extends XmlAdapter<SessionPasswordHolderWrapper, Map<String, ArrayList<String>>> {
	private static NativeFacade nativeFacade = CommonService.getInstance().getNativeFacade();

	@Override
	public SessionPasswordHolderWrapper marshal(Map<String, ArrayList<String>> passwordMap) throws Exception {

		ArrayList<SessionPasswordHolder> holderList = new ArrayList<SessionPasswordHolder>();
		if (passwordMap != null) {
			Set<String> afGuidSet = passwordMap.keySet();
			for (String afGuid : afGuidSet) {
				ArrayList<String> passwordList = passwordMap.get(afGuid);
				ArrayList<String> encryptedPasswordList = new ArrayList<String>();
				for (String password : passwordList) {
					encryptedPasswordList.add(nativeFacade.encrypt(password));
				}
				holderList.add(new SessionPasswordHolder(afGuid, encryptedPasswordList));
			}
		}
		SessionPasswordHolderWrapper wrapper = new SessionPasswordHolderWrapper();
		wrapper.setHolderList(holderList);
		return wrapper;
	}

	@Override
	public Map<String, ArrayList<String>> unmarshal(SessionPasswordHolderWrapper wrapper) throws Exception {
		Map<String, ArrayList<String>> passwordMap = new HashMap<String, ArrayList<String>>();
		if (wrapper != null) {
			for (SessionPasswordHolder holder : wrapper.getHolderList()) {
				ArrayList<String> encryptedPasswordList = holder.getPasswordList();
				ArrayList<String> passwordList = new ArrayList<String>();
				for (String encryptedPassword : encryptedPasswordList) {
					passwordList.add(nativeFacade.decrypt(encryptedPassword));
				}
				passwordMap.put(holder.getAfGuid(), passwordList);
			}
		}
		return passwordMap;
	}

}
