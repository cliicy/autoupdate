package com.ca.arcflash.ui.client.homepage;

import com.ca.arcflash.ui.client.common.ISettingsContent;
import com.ca.arcflash.ui.client.common.ISettingsContentHost;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.Widget;

public class PreferencesWindow extends Window implements ISettingsContentHost
{
	private ISettingsContent settingsContent;
	
	public PreferencesWindow()
	{
		this.setWidth(840);
		this.setHeight(600);
		this.setLayout( new FitLayout() );

		settingsContent = new PreferencesSettingsContent();
		settingsContent.initialize( this, false );
		this.add( settingsContent.getWidget() );
	}
	
	@Override
	protected void afterShow()
	{
		super.afterShow();
		settingsContent.loadData();
	}

	@Override
	public void setCaption( String text )
	{
		setHeadingHtml( text );
	}
	
	@Override
	public void close()
	{
		hide();
	}

	@Override
	public void increaseBusyCount( String message )
	{
		mask( message );
	}

	@Override
	public void increaseBusyCount()
	{
		mask();
	}

	@Override
	public void decreaseBusyCount()
	{
		unmask();
	}

	@Override
	public void showSettingsContent( int settingsContentId )
	{
	}

	@Override
	public void focusWidge( Widget widget )
	{
		this.setFocusWidget( widget );
	}

	@Override
	public void onAsyncOperationCompleted( int operation, int result,
		int settingsContentId )
	{
	}

	@Override
	public boolean isForCreate() {
		// TODO Auto-generated method stub
		return false;
	}
}