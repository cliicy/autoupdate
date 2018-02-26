package com.ca.arcflash.ui.client.coldstandby.event;

import com.ca.arcflash.ha.model.ARCFlashNode;
import com.ca.arcflash.ui.client.coldstandby.VirtualConversionServerNavigator.NODE_TYPE;
import com.google.gwt.event.shared.GwtEvent;

public class ServerSelectionChangedEvent extends GwtEvent<ServerSelectionChangedEventHandler> {
	
	private ARCFlashNode currentNode;
	private NODE_TYPE type;
	
	public static Type<ServerSelectionChangedEventHandler> TYPE = new Type<ServerSelectionChangedEventHandler>();
	
	public ServerSelectionChangedEvent(ARCFlashNode node, NODE_TYPE nodeType) {
		currentNode = node;
		type = nodeType;
	}

	@Override
	protected void dispatch(ServerSelectionChangedEventHandler handler) {
		handler.onServerChanged(this);
	}

	@Override
	public Type<ServerSelectionChangedEventHandler> getAssociatedType() {
		return TYPE;
	}
	
	public ARCFlashNode getCurrentNode() {
		return currentNode;
	}

	public void setCurrentNode(ARCFlashNode currentNode) {
		this.currentNode = currentNode;
	}
	
	public NODE_TYPE getType() {
		return type;
	}

	public void setType(NODE_TYPE type) {
		this.type = type;
	}
}
