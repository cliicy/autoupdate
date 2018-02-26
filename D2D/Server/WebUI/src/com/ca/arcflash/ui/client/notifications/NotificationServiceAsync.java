package com.ca.arcflash.ui.client.notifications;


import java.util.HashMap;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author uppra02
 *
 */
public interface NotificationServiceAsync {
	public void getNotifications(AsyncCallback<HashMap<String,String>> callback);
}
