package com.ca.arcflash.ui.client.common;

import com.google.gwt.user.client.Window;

public class OnLineHelpTopics {

	public static void showHelpURL(String url) {
		Window.open(url, "_blank",
				"toolbar=yes,menubar=yes,location=yes,resizable=yes,scrollbars=yes,status=yes");
	}
}
