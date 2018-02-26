package com.ca.arcflash.ui.client.login;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.AbsoluteLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.Element;

public class LoginPanel extends LayoutContainer {
	
	private LoginForm loginForm = new LoginForm();
	
	@Override
	protected void onRender(Element parent, int index) {

		super.onRender(parent, index);
		this.setStyleAttribute("padding", "0px");
		this.setStyleName("login_container");

		TableLayout layout = new TableLayout(1);
		layout.setWidth("100%");
		this.setLayout(layout);

		TableData td = new TableData();
		td.setWidth("100%");
		td.setHorizontalAlign(HorizontalAlignment.CENTER);

		LayoutContainer container = new LayoutContainer();
		AbsoluteLayout absLayout = new AbsoluteLayout();
		container.setLayout(absLayout);
		container.setHeight(750);
		container.setWidth(937);
		
		//Image image = new Image(UIContext.IconBundle.agent_login_header());
		Html image = new Html("<span class=\"login_logo_name\">&nbsp</span>");
		image.setStyleName("login_productName_container");
		container.add(image);

		loginForm.setStyleName("login_form_container");
		container.add(loginForm);

		this.add(container, td);
	}
	
	public LoginForm getLoginWindow()
	{
		return loginForm;				
	}
	
}
