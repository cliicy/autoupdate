package com.ca.arcflash.ui.client.monitor.servers;

import java.util.EventListener;

import com.ca.arcflash.ha.model.ARCFlashNode;

public interface ServerChangeListener extends EventListener{
	
	void onChanged(ARCFlashNode node, long serverTime);
}
