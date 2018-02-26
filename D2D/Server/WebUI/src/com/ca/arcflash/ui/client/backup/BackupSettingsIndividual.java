package com.ca.arcflash.ui.client.backup;


import com.ca.arcflash.ui.client.common.ISettingsContent;
import com.ca.arcflash.ui.client.common.ISettingsContentHost;
import com.ca.arcflash.ui.client.vsphere.setting.VSphereBackupSettingContent;
import com.ca.arcflash.ui.client.vsphere.setting.VSphereBackupSettingsContentForLiteLT;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;


public class BackupSettingsIndividual extends LayoutContainer implements ISettingsContentHost
{
	private ISettingsContent settingsContent;
	
	public static final String D2DPageClose = "http://ClosePageRequest";
	
	private boolean forVSphere;
	
	public BackupSettingsIndividual(Boolean bvSphere)
	{
		forVSphere = bvSphere;
		
		this.setWidth(854);
		
		this.setLayout(new TableLayout(1));
		
		if(bvSphere)
		{
			settingsContent = new VSphereBackupSettingsContentForLiteLT();
		}
		else
		{
			settingsContent = new BackupSettingsContentForLiteIT();
		}
		
		settingsContent.initialize(this, false);
		
		if(bvSphere)
		{
			((VSphereBackupSettingsContentForLiteLT) settingsContent).enableEditing(false);
		}
		
		this.add(settingsContent.getWidget(), new TableData(Style.HorizontalAlignment.LEFT, Style.VerticalAlignment.TOP));
	}
	
	@Override
	protected void onRender(Element parent, int index)
	{
		super.onRender(parent, index);
		settingsContent.loadData();
	}

	@Override
	public void close() {
			com.google.gwt.user.client.Window.Location.replace(D2DPageClose);	
	}

	@Override
	public void decreaseBusyCount() {
		unmask();
	}

	@Override
	public void increaseBusyCount(String message) {
		mask( message );
	}

	@Override
	public void increaseBusyCount() {
		mask();
	}

	@Override
	public void onAsyncOperationCompleted(int operation, int result,
			int settingsContentId) {
		if(forVSphere){
			((VSphereBackupSettingsContentForLiteLT) settingsContent).enableEditing(false);
		}
	}

	@Override
	public void setCaption(String text) {
		
	}

	@Override
	public void showSettingsContent( int settingsContentId )
	{
	}

	@Override
	public void focusWidge( Widget widget )
	{
	}

	@Override
	public boolean isForCreate() {
		// TODO Auto-generated method stub
		return false;
	}
}