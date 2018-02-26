package com.ca.arcflash.ui.client.remotedeploy;

import com.ca.arcflash.ui.client.UIContext;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class RemoteDeployWindow extends Window {
	public static final int WINDOW_WIDTH = 900;
	public static final int WINDOW_HEIGHT = 500;

	public RemoteDeployWindow() {
		this.setHeadingHtml(UIContext.Constants.remoteDeploymentTitle());
		// this.setScrollMode(Scroll.AUTOY);
		this.setResizable(false);
		this.setClosable(true);
		this.setHeight(WINDOW_HEIGHT);
		this.setWidth(WINDOW_WIDTH);
		this.setLayout(new FitLayout());

		LayoutContainer container = new LayoutContainer();
		container.setStyleAttribute("padding", "6px");
		container.setLayout(new FitLayout());

		RemoteDeployPanel rdp = new RemoteDeployPanel(this);
		container.add(rdp);

		this.add(container);
	}
}
