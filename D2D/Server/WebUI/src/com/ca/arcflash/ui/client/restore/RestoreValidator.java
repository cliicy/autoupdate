package com.ca.arcflash.ui.client.restore;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RestoreValidator {
	public boolean validate(AsyncCallback<Boolean> callback);
}
