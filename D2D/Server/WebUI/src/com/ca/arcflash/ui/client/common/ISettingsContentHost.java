package com.ca.arcflash.ui.client.common;

import com.google.gwt.user.client.ui.Widget;

public interface ISettingsContentHost
{
	public class Operations
	{
		public static final int SaveData	= 1;
		public static final int LoadData	= 2;
		public static final int Validate	= 3;
	}
	
	public class OperationResults
	{
		public static final int Succeeded	= 1;
		public static final int Failed		= 2;
	}
	
	void setCaption( String text );
	void close();
	void increaseBusyCount( String message );
	void increaseBusyCount();
	void decreaseBusyCount();
	void showSettingsContent( int settingsContentId );
	void focusWidge( Widget widget );
	void onAsyncOperationCompleted( int operation, int result, int settingsContentId );
	
	// default is false in D2D
	boolean isForCreate();
}
