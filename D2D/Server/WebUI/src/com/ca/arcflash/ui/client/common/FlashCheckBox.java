package com.ca.arcflash.ui.client.common;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.google.gwt.user.client.Element;

public class FlashCheckBox extends LayoutContainer{
	private IconButton checkBoxButton;
	private boolean enabled = true;
	private int state = NONE;
	
	public static final int NONE = 0;
	public static final int PARTIAL = 1;
	public static final int FULL = 2;
	
	private SelectionListener<IconButtonEvent> listener;
	
	public FlashCheckBox()
	{
		this.setStyleAttribute("font-size", "0");
		enabled = true;
		state = NONE;
	}
	public FlashCheckBox(boolean enabled, int state)
	{
		this.enabled = enabled;
		this.state = state;
	}
	
	 public void addSelectionListener(SelectionListener<IconButtonEvent> listener) {
		 this.listener = listener;
	 }
	 public void removeSelectionListener()
	 {
		 listener = null;
	 }
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
	
		//ColumnLayout cl = new ColumnLayout();
		//this.setLayout(cl);
		
		checkBoxButton = new IconButton(getIconStyle());
		checkBoxButton.addSelectionListener(new SelectionListener<IconButtonEvent> ()
		{
			@Override
			public void componentSelected(IconButtonEvent ce) {
				if (enabled)
				{
					if (state == NONE || state == PARTIAL)
					{
						state = FULL;
					}
					else if (state == FULL)
					{
						state = NONE;
					}
					checkBoxButton.changeStyle(getIconStyle());
				
					if (listener != null)
					{
						listener.componentSelected(ce);
					}
				}
			}
		});
		
		this.add(checkBoxButton);
	}
	
	public String getIconStyle()
	{
		StringBuilder sb = new StringBuilder();
		
		if (enabled)
			sb.append("green-");
		else
			sb.append("grey-");
		
		switch (state)
		{
			case PARTIAL:
				sb.append("partial");
				break;
			case FULL:
				sb.append("full");
				break;
			default:
			case NONE:
				sb.append("none");
				break;
		}
		return sb.toString();
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if (this.isRendered())
		{
			checkBoxButton.changeStyle(getIconStyle());
		}
	}
	public int getSelectedState() {
		return state;
	}
	public void setSelectedState(int state) {
		this.state = state;
		if (this.isRendered())
		{
			checkBoxButton.changeStyle(getIconStyle());
		}
	}
	
}
