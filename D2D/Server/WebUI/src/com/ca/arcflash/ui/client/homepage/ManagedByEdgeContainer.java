package com.ca.arcflash.ui.client.homepage;

import com.ca.arcflash.ui.client.UIContext;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;

public class ManagedByEdgeContainer extends LayoutContainer {
	
	private String edgeUrl;
	private String edgeHostName;
	private Label edgeServerLabel = new Label();
	
	public ManagedByEdgeContainer() {
		setWidth("100%");
		setLayout(new TableLayout(2));
		
		Text text = new Text(UIContext.Constants.homepageManagedBy());
		text.setStyleName("homepage_header_user_label");
		add(text);
		
		edgeServerLabel .setStyleName("homepage_managing_server_label homepage_header_helplink_label");
		edgeServerLabel .addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				
				Window.open(edgeUrl, "_blank", "");
			}
			
		});
		add(edgeServerLabel );
		
		setVisible(false);
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
	}

	public void setEdgeServer(String edgeHost, String edgeUrl) {
		this.edgeUrl = edgeUrl;
		this.edgeHostName = edgeHost;
		if(isEmpty(edgeHost) || isEmpty(edgeUrl))
			setVisible(false);
		else {
			setVisible(true);
			edgeServerLabel.setText(edgeHostName);
		}
	}

	private boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}
}
