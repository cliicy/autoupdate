package com.ca.arcflash.ui.client.notifications;

import java.util.HashMap;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author uppra02
 *
 */
@RemoteServiceRelativePath("notification")
public interface NotificationService extends RemoteService{
	HashMap<String, String> getNotifications();
}
