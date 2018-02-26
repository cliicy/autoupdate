package com.ca.arcflash.ui.client.recoverypoint;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Label;

public class RecoveryPointPanel extends LayoutContainer {
	public void render(Element target, int index) {
		super.render(target, index);
		this.add(new Label("Recovery Point"));
	}
}
