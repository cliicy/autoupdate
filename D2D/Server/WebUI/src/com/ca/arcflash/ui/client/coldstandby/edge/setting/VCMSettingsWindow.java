package com.ca.arcflash.ui.client.coldstandby.edge.setting;

import com.ca.arcflash.ui.client.common.ISettingsContent;
import com.ca.arcflash.ui.client.common.ISettingsContentHost;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.Widget;

public class VCMSettingsWindow extends Window implements ISettingsContentHost
{
	//public static final int DEFAULT_RETENTION_COUNT = 31;
	private ISettingsContent settingsContent;
	private int count = 0;
	public VCMSettingsWindow()
	{
		this.setWidth(840);
		this.setHeight(600);
		this.setLayout( new FitLayout() );

		settingsContent = new VCMSettingsContent();
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
		synchronized(this) {
			if(count==0) {
				mask( message );
			}
			count++;
		}
		
	}

	@Override
	public void increaseBusyCount()
	{
		synchronized(this) {
			if(count==0) {
				mask();
			}
			count++;
		}
	}

	@Override
	public void decreaseBusyCount()
	{
		//unmask();
		synchronized(this) {
			if(count==1) {
				unmask();
			}
			count--;
		}
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