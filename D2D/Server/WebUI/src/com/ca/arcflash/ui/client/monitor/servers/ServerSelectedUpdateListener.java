package com.ca.arcflash.ui.client.monitor.servers;

import java.util.EventListener;

import com.ca.arcflash.ha.model.ARCFlashNode;

public interface ServerSelectedUpdateListener extends EventListener{
	void onUpdate(ARCFlashNode node, long serverTime);
}
