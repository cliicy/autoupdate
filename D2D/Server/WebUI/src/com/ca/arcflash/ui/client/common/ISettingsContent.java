package com.ca.arcflash.ui.client.common;

import java.util.List;

import com.ca.arcflash.ui.client.model.IEmailConfigModel;
import com.google.gwt.user.client.ui.Widget;

public interface ISettingsContent
{
	public class SettingsTab
	{
		private String id;
		private String displayName;
		
		public SettingsTab( String id, String displayName )
		{
			this.id = id;
			this.displayName = displayName;
		}
		
		public String getId()
		{
			return id;
		}
		
		public void setId( String id )
		{
			this.id = id;
		}
		
		public String getDisplayName()
		{
			return displayName;
		}

		public void setDisplayName( String displayName )
		{
			this.displayName = displayName;
		}
	}
	
	void initialize( ISettingsContentHost contentHost, boolean isForEdge );

	boolean isForEdge();
	void setIsForEdge( boolean isForEdge );
	void setId( int settingsContentId );
	
	boolean isForLiteIT();                        ///D2D Lite Integration
	void setisForLiteIT( boolean isForLiteIT );
	
	Widget getWidget();

	List<SettingsTab> getTabList();
	void switchTab( String tabId );
	
	void loadData();
	void loadDefaultData();
	void saveData();
	void validate();
	void setDefaultEmail(IEmailConfigModel iEmailConfigModel);
}
