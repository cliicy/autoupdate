package com.ca.arcflash.webservice.jni.wrapper;

import java.util.UUID;

import com.ca.arcflash.common.CommonRegistryKey;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.common.WindowsRegistry;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.NativeFacadeImpl;

public class WebClientWrapper {	
	public static final String REGISTRY_KEY_GUID = "GUID";
	private static NativeFacade nativeFacade = null;
	static {
		System.loadLibrary("NativeFacade");
		nativeFacade = new NativeFacadeImpl();
	}
	
	@Deprecated
	public static String retrieveCurrentUUID() {
		NativeFacade nativeFacade = new NativeFacadeImpl();
		WindowsRegistry registry = new WindowsRegistry();
		String uuid = null;
		try {
			int handle = registry.openKey(CommonRegistryKey.getD2DRegistryRoot());
			if (handle == 0)
				handle = registry.createKey(CommonRegistryKey.getD2DRegistryRoot());
			// registry.closeKey(handle);
			// handle = registry.openKey(REGISTRY_ROOTKEY);

			uuid = registry.getValue(handle, REGISTRY_KEY_GUID);
			if (StringUtil.isEmptyOrNull(uuid)) {
				uuid = UUID.randomUUID().toString();
				registry.setValue(handle, REGISTRY_KEY_GUID, nativeFacade
						.encrypt(uuid));
			} else {
				uuid =nativeFacade.decrypt(uuid);
			}
			registry.closeKey(handle);
		} catch (Exception e) {
			
		}
		
		return uuid;
	}
}
