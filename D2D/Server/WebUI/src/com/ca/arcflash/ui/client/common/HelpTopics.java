package com.ca.arcflash.ui.client.common;

import com.ca.arcflash.ui.client.UIContext;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.Window;

public class HelpTopics {
	
	public static void showHelpURL(String url) {
		Window.open(url, "_blank", "");
	}
	
	public static Button createHelpButton(final String url, int width)
	{
		Button helpButton = new Button();
		if(width > 0)
		  helpButton.setWidth(width);
		helpButton.setText(UIContext.Constants.help());
		helpButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				showHelpURL(url);
			}
		});
		return helpButton;
	}
	
}
