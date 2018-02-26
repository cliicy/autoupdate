package com.ca.arcflash.ui.client.restore;

public interface IRestoreSourceListener {
	void onDefaultSourceInitialized(boolean succeed);
	
	void onRestoreSourceTypeChanged();
}
