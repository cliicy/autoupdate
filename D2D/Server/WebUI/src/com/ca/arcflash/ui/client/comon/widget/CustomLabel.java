package com.ca.arcflash.ui.client.comon.widget;

import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.ui.Label;

public class CustomLabel extends Composite {
	Label l;

	public CustomLabel() {
		LayoutContainer c = new LayoutContainer();
		l = new Label();
		c.add(l);
		initComponent(c);
	}

	public void setValue(String text) {
		l.setText(text);
	}

}
