package com.ca.arcflash.ui.client.homepage.navigation;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Popup;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class NavigationButtonItem extends LayoutContainer
{
	private AbstractImagePrototype image;
	private ContentPanel panel;
	
	private BorderLayoutData parentData;
	private Popup popup;
	private boolean expanded;
	
	public NavigationButtonItem(AbstractImagePrototype image, ContentPanel panel, BorderLayoutData data)
	{
		super();
		parentData = data;
		this.image = image;
		this.panel = panel;
	}

	@Override
	protected void onRender(Element parent, int index)
	{
		super.onRender(parent, index);
		
		if (image != null)
		{
			this.add(image.createImage());
		}
		
		if (panel != null && panel.getHeadingHtml() != null)
		{
			this.setTitle(panel.getHeadingHtml());
		}		

		this.sinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT | Event.ONMOUSEMOVE | Event.ONCLICK);
	}

	@Override
	public void onComponentEvent(ComponentEvent ce)
	{
		super.onComponentEvent(ce);
		
		switch (ce.getEventTypeInt())
		{
		case Event.ONMOUSEOVER:
			onMouseOver(ce);
			break;
		case Event.ONMOUSEOUT:
			onMouseOut(ce);
			break;
		case Event.ONCLICK:
			onClick(ce);
			break;
		}
	}
	
	protected void onMouseOver(ComponentEvent ce)
	{
		addStyleName("navigation-button-over");		
	}
	
	protected void onMouseOut(ComponentEvent ce)
	{
		removeStyleName("navigation-button-over");
	}

	protected void onClick(ComponentEvent ce)
	{
		setExpanded(!expanded);
	}
	
	public void setExpanded(boolean expanded)
	{
		if (panel != null)
		{
			if (!this.expanded && expanded)
			{
				onShowPanel(panel);
			}
			else if (this.expanded && !expanded)
			{
				onHidePanel(panel);
			}
		}		
	}

	protected void onShowPanel(ContentPanel panel)
	{
		this.expanded = true;

		if (popup == null)
		{
			popup = new Popup()
			{
				protected boolean onAutoHide(Event event)
				{
					setExpanded(false);
					return false;
				}
			};

			popup.getIgnoreList().add(getElement());
			popup.getIgnoreList().add(panel.getElement());
			popup.setStyleName("x-layout-popup");
			popup.setLayout(new FitLayout());
			popup.setShadow(true);
		}
		
		panel.setPosition(0, 0);
		panel.setBorders(false);
		panel.getHeader().hide();
		//panel.addStyleName("x-panel-popup-body");

		popup.add(panel);
		popup.setSize(200, 0);
		popup.layout();
		
		String align = null;
		int[] adj = new int[] { 0, 0 };
		switch (parentData.getRegion())
		{
		case WEST:
			align = "tl-tr";
			adj = new int[] { 0, 1 };
			break;
		case EAST:
			align = "tr-tl";
			adj = new int[] { 0, 1 };
			break;
		case NORTH:
			align = "tl-bl";
			break;
		case SOUTH:
			align = "bl-tl";
			break;
		}
		
		popup.show(getElement(), align, adj);

	}

	protected void onHidePanel(ContentPanel panel)
	{
		this.expanded = false;
		if (popup != null && panel != null)
		{
			//panel.removeStyleName("x-panel-popup-body");
			panel.getHeader().show();
			popup.hide();
			panel.setStyleAttribute("margin", "0px");
		}
	}

}
