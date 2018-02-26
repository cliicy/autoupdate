package com.ca.arcflash.ui.client.common.gxtex;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.util.Rectangle;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.IconSupport;
import com.extjs.gxt.ui.client.widget.Layer;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Accessibility;
import com.google.gwt.user.client.ui.Widget;

//Extend GXT2.3.1 Class to Fix grid popmenu style issue.
public class MenuItemEx extends MenuItem {

	public MenuItemEx() {
		super();
	}

	public MenuItemEx(String html) {
		super(html);
	}

	public MenuItemEx(String html, AbstractImagePrototype icon) {
		super(html, icon);
	}

	public MenuItemEx(String text, AbstractImagePrototype icon,
			SelectionListener<? extends MenuEvent> listener) {
		super(text, icon, listener);

	}

	public MenuItemEx(String text,
			SelectionListener<? extends MenuEvent> listener) {
		super(text, listener);
	}

	@Override
	protected void onRender(Element target, int index) {
		setElement(DOM.createAnchor(), target, index);
		getElement().setAttribute("unselectable", "on");
		if (GXT.isAriaEnabled()) {
			Accessibility.setRole(getElement(), Accessibility.ROLE_MENUITEM);
		} else {
			getElement().setPropertyString("href", "#");
		}
		String s = itemStyle + (subMenu != null ? " x-menu-item-arrow" : "");
		addStyleName(s);

		if (widget != null) {
			setWidget(widget);
		} else {
			setHtml(html);
		}

		if (subMenu != null) {
			Accessibility.setState(getElement(), "aria-haspopup", "true");
		}
	}

}
