package com.ca.arcflash.ui.client.mount;

import java.util.HashMap;
import java.util.Map;

import com.extjs.gxt.ui.client.widget.Window;

public class WindowsMaskAdapter {
	public static final String MOUNTED_LIST_CONTAINER = "MOUNTED_LIST_CONTAINER";
	public static final String MOUNT_VOLUME_CONTAINER = "MOUNT_VOLUME_CONTAINER";
	private Window window;
	private Map<String, Boolean> refreshMap;
	
	public WindowsMaskAdapter(Window window){
		this.window = window;
		refreshMap = new HashMap<String, Boolean>();
//		cleanRefreshStatus();
	}
	
	public Window getWindow() {
		return window;
	}

	public void setWindow(Window window) {
		this.window = window;
	}

	public void maskWindow(String maskText){
		if(!window.isMasked())
			window.mask(maskText);
	}
	
	public void cleanRefreshStatus(){
		refreshMap.put(MOUNTED_LIST_CONTAINER, false);
		refreshMap.put(MOUNT_VOLUME_CONTAINER, false);
	}
	public void updateRefreshStatus(String key, Boolean value){
		refreshMap.put(key, value);
		for (Boolean result : refreshMap.values()) {
			if(!result)
				return;
		}
		unmaskWindow();
		
		((IMountAsyncCallback)window).loadComplete();
	}
	public void unmaskWindow(){
		if(window.isMasked())
			window.unmask();
	}
}
