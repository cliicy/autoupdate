package com.ca.arcflash.ui.client.common;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.ArchiveSettingsContent;
import com.ca.arcflash.ui.client.backup.BackupContext;
import com.ca.arcflash.ui.client.backup.BackupSettingsContent;
import com.ca.arcflash.ui.client.coldstandby.edge.setting.VCMSettingsContent;
import com.ca.arcflash.ui.client.homepage.PreferencesSettingsContent;
import com.ca.arcflash.ui.client.service.Broker;
import com.ca.arcflash.ui.client.vsphere.setting.VSphereBackupSettingContent;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

public class CommonSettingWindow extends Window implements ISettingsContentHost
{
//	protected final LoginServiceAsync 	service = GWT.create(LoginService.class);
	private ISettingsContent			settingsContent;
	private int 						count;
	private AppType 					appType;
	protected boolean						isD2DEditable;
	
	protected Button						okButton;
	protected Button						cancelButton;
	
	public CommonSettingWindow(AppType appType)
	{
		this.setResizable(true);
		this.setMaximizable(true);
		//this.setMinimizable(true);
		this.setLayout( new FitLayout() );
		this.count = 0;
		this.appType = appType;
		
		SettingsGroupType settingsGroupType = SettingsGroupType.D2DSettings;
		switch (appType)
		{
		case D2D:
			settingsGroupType = SettingsGroupType.AgentSettings;//SettingsGroupType.D2DSettings;
			break;
		
		case VCM:
			if (UIContext.isRemoteVCM) {
				settingsGroupType = SettingsGroupType.RemoteVCMSettings;
			} else {
				settingsGroupType = SettingsGroupType.VCMSettings;
			}
			break;
			
		case VSPHERE:
			settingsGroupType = SettingsGroupType.VMBackupSettings;
			break;
		default:
			break;
		}
		
		settingsContent = new CommonSettingContent(settingsGroupType);
		settingsContent.initialize( this, false );
		
		LayoutContainer panel = new LayoutContainer();
		panel.setLayout( new RowLayout( Orientation.VERTICAL ) );
		panel.add( this.settingsContent.getWidget(), new RowData( 1, 1, new Margins( 0, 0, 0, 0 ) ) );
		
		
		//and the buttons
		LayoutContainer buttonContainer = new LayoutContainer();
		buttonContainer.setStyleAttribute("background-color", "#DFE8F6");
		// buttonContainer.setHeight(80);

		TableLayout tableLayout = new TableLayout();
		tableLayout.setWidth("100%");
		tableLayout.setCellPadding(4);
		tableLayout.setCellSpacing(4);
		tableLayout.setColumns(4);

		// Repeat Section
		TableData td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.LEFT);
		td.setVerticalAlign(VerticalAlignment.BOTTOM);
		td.setWidth("100%");
		buttonContainer.setLayout(tableLayout);

		LabelField leftSpace = new LabelField();
		buttonContainer.add(leftSpace, td);

		td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.RIGHT);
		td.setVerticalAlign(VerticalAlignment.BOTTOM);

		okButton = new Button();
		okButton.ensureDebugId("85a02b2f-2294-4220-aa94-46053e32848f");
		okButton.setMinWidth(80);
		okButton.setText(UIContext.Constants.backupSettingsOk());
		okButton.disable();
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				increaseBusyCount(UIContext.Constants.settingsMaskText());
//				SummaryPanel.UpdateAutoUpdateStatus();
				settingsContent.saveData();
			}

		});
		buttonContainer.add(okButton, td);

		cancelButton = new Button();
		cancelButton.ensureDebugId("51dffd03-5b52-43e3-9828-dfe021ae625d");
		cancelButton.setMinWidth(80);
		cancelButton.setText(UIContext.Constants.backupSettingsCancel());
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				handleWhenCloseOrEsc();
			}

		});
		final Button helpButton = new Button();
		helpButton.ensureDebugId("c2bea043-6c17-420a-8655-cdcace5c361a");
		helpButton.setMinWidth(80);
		helpButton.setText(UIContext.Constants.help());
		helpButton.addSelectionListener(new SelectionListener<ButtonEvent>() {	

			@Override
			public void componentSelected(ButtonEvent ce) {	
				String URL ;
				if(BaseCommonSettingTab.presentTabSelectionIndex != 0)
				{
					if(BaseCommonSettingTab.presentTabSelectionIndex == BaseCommonSettingTab.d2dBackupSettingID)
					{
						int idxBtn = BackupSettingsContent.getButtonSelected();						
						URL = UIContext.externalLinks.getBackupSettingDestinationHelp();
						
						switch (idxBtn) {
						case 1:
							URL = UIContext.externalLinks.getBackupSettingDestinationHelp();
							break;
						case 2:
							URL = UIContext.externalLinks.getBackupSettingScheduleAdvancedHelp();
							break;
						case 3:
							URL = UIContext.externalLinks.getBackupSettingSettingsHelp();
							break;
						case 4:
							URL = UIContext.externalLinks.getBackupSettingAdvancedHelp();
							break;
						case 5:
							URL = UIContext.externalLinks.getBackupSettingScheduleAdvancedHelp();
							break;
						case 6:
							URL = UIContext.externalLinks.getBackupSettingScheduleAdvancedHelp();
							break;
						case 7:
							URL = UIContext.externalLinks.getBackupSettingScheduleStandardHelp();
							break;
						default:
							break;
						}

						HelpTopics.showHelpURL(URL);
						
					}
					else if(BaseCommonSettingTab.presentTabSelectionIndex == BaseCommonSettingTab.archiveSettingID)
					{
						URL = UIContext.externalLinks.getArchiveSourceSettings();
						//ArchiveSettingsContent archiveWindow = new ArchiveSettingsContent();
					    if(ArchiveSettingsContent.getButtonSelected() == 1)
					    	URL = UIContext.externalLinks.getArchiveSourceSettings();
					    if(ArchiveSettingsContent.getButtonSelected() == 2)
					    	URL = UIContext.externalLinks.getArchiveDestinationSettings();
					    if(ArchiveSettingsContent.getButtonSelected() == 3)
					    	URL = UIContext.externalLinks.getArchiveScheduleSettings();
					    if(ArchiveSettingsContent.getButtonSelected() == 4)
					    	URL = UIContext.externalLinks.getArchiveAdvancedSettings();
					    HelpTopics.showHelpURL(URL);
					    	
					}
					else if(BaseCommonSettingTab.presentTabSelectionIndex == BaseCommonSettingTab.fileArchiveSettingID)
					{
						URL = UIContext.externalLinks.getFileArchiveSourceSettings();
					    if(ArchiveSettingsContent.getButtonSelected() == 1)
					    	URL = UIContext.externalLinks.getFileArchiveSourceSettings();
					    if(ArchiveSettingsContent.getButtonSelected() == 2)
					    	URL = UIContext.externalLinks.getFileArchiveDestinationSettings();
					    if(ArchiveSettingsContent.getButtonSelected() == 3)
					    	URL = UIContext.externalLinks.getFileArchiveScheduleSettings();
					    if(ArchiveSettingsContent.getButtonSelected() == 4)
					    	URL = UIContext.externalLinks.getFileArchiveAdvancedSettings();
					    HelpTopics.showHelpURL(URL);
					    	
					}
					else if(BaseCommonSettingTab.presentTabSelectionIndex == BaseCommonSettingTab.d2dPreferenceSettingID)
					{
						URL = UIContext.externalLinks.getPreferencesGeneralSettings();
						//PreferencesSettingsContent preferenceWindow = new PreferencesSettingsContent();
						if(PreferencesSettingsContent.getButtonSelected() == 1)
							URL = UIContext.externalLinks.getPreferencesGeneralSettings();
						if(PreferencesSettingsContent.getButtonSelected() == 2)
							URL = UIContext.externalLinks.getPreferencesEmailSettings();
						if(PreferencesSettingsContent.getButtonSelected() == 3)
							URL = UIContext.externalLinks.getPreferencesAutoUpdateSettings();
						HelpTopics.showHelpURL(URL);						
					} else if(BaseCommonSettingTab.presentTabSelectionIndex == BaseCommonSettingTab.scheduledExportSettingsID) {
						URL = UIContext.externalLinks.getCopyRecoveryPointsSettings();
						HelpTopics.showHelpURL(URL);
					}
					
					else if(BaseCommonSettingTab.presentTabSelectionIndex == BaseCommonSettingTab.vcmSettingID){
						URL = UIContext.externalLinks.getVirtualizationServerHelp();
						if(VCMSettingsContent.getButtonSelected() == 0){
							URL =  UIContext.externalLinks.getVirtualizationServerHelp();
						}
						if(VCMSettingsContent.getButtonSelected() == 1){
							URL = UIContext.externalLinks.getVirtualizationMachineHelp();
						}
						if(VCMSettingsContent.getButtonSelected() == 2){
							URL = UIContext.externalLinks.getStandinSettingsHelp();
						}
						HelpTopics.showHelpURL(URL);
					}
					else if(BaseCommonSettingTab.presentTabSelectionIndex == BaseCommonSettingTab.vcmPreferenceID){
						URL = UIContext.externalLinks.getVirtualStandbyEmailAlertHelp();
						HelpTopics.showHelpURL(URL);
					}
					else if (BaseCommonSettingTab.presentTabSelectionIndex == BaseCommonSettingTab.vsphereSettingID){
						int idxBtn = VSphereBackupSettingContent.getButtonSelected();
						URL = UIContext.externalLinks.getVMBackupSettingDestinationHelp();
						
						switch(idxBtn){
						case 1:
							URL = UIContext.externalLinks.getVMBackupSettingDestinationHelp();
							break;
						case 2:
							URL = UIContext.externalLinks.getVMBackupSettingScheduleHelp();
							break;
						case 3:
							URL = UIContext.externalLinks.getVMBackupSettingAdvancedHelp();
							break;
						case 4:
							URL = UIContext.externalLinks.getVMBackupSettingSettingsHelp();
							break;
						case 5:
							URL = UIContext.externalLinks.getVMBackupSettingScheduleAdvancedHelp();
							break;
						case 6:
							URL = UIContext.externalLinks.getVMBackupSettingScheduleAdvancedHelp();
							break;
						case 7:
							URL = UIContext.externalLinks.getVMBackupSettingScheduleStandardHelp();
							break;
						default:
							break;
						}
						
						HelpTopics.showHelpURL(URL);
					}
					else if(BaseCommonSettingTab.presentTabSelectionIndex == BaseCommonSettingTab.vspherePreferenceID){
						URL = UIContext.externalLinks.getVMBackupSettingEmailHelp();
						HelpTopics.showHelpURL(URL);
					}
					
					
					
				}
								
			}
		});

		td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.RIGHT);
		td.setVerticalAlign(VerticalAlignment.BOTTOM);
		buttonContainer.add(cancelButton, td);
		buttonContainer.add(helpButton, td);
		panel.add(buttonContainer, new RowData(1, -1));

		disableEditingIfUsingEdgePolicy();
		

		this.add(panel);
		
	}
	
	@Override
	protected void onKeyPress(WindowEvent we) {
		int keyCode = we.getKeyCode();
	    if (isClosable() && isOnEsc() && keyCode == KeyCodes.KEY_ESCAPE
	    	// liuwe05 2011-06-01 fix Issue: 20312591    Title: INCORRECT BEHAVIOR EXHIBITED I
	    	// do not comment out below condition
	        && getElement().isOrHasChild((com.google.gwt.dom.client.Element) we.getEvent().getEventTarget().cast())) {
//	      hide();
	    	handleWhenCloseOrEsc();
	    }
	}
	
	private void handleWhenCloseOrEsc() {
		//If the d2d setting isn't managed by edge, show the warning message
		if((appType == AppType.D2D)&&isD2DEditable){
			// Cancel Clicked hide the dialog
			final Listener<MessageBoxEvent> messageBoxHandler = new Listener<MessageBoxEvent>() {
				public void handleEvent(MessageBoxEvent be) {
					if (be.getButtonClicked().getItemId()
							.equals(Dialog.YES)) {
						close();
					}
				}
			};

			MessageBox mb = new MessageBox();
			mb.setIcon(MessageBox.WARNING);
			mb.setButtons(MessageBox.YESNO);
			mb.setTitleHtml(UIContext.Messages.messageBoxTitleWarning(getMessageBoxTitle()));
			mb.setMessage(UIContext.Constants.backupSettingExistAlert());
			mb.addCallback(messageBoxHandler);
			Utils.setMessageBoxDebugId(mb);
			mb.show();
		}
		//If the d2d setting is managed by edge, or VCM settings, VSPHERE settings, don't show the warning message
		else{
			close();
		}

	}
	
	protected String getMessageBoxTitle(){
		String productName = "";
		if(appType == AppType.VCM){
			productName = UIContext.productNameVCM;
		}
		else if(appType == AppType.VSPHERE){
			productName = UIContext.productNamevSphere;
		}
		else{
			productName = UIContext.productNameD2D;
		}
		return UIContext.Messages.messageBoxTitleError(productName);
	}
	
	protected void disableEditingIfUsingEdgePolicy()
	{
		if(appType == AppType.D2D) {
			Broker.loginService.isUsingEdgePolicySettings(
					SettingsTypesForUI.BackupSettings, new AsyncCallback<Boolean>()
				{
					@Override
					public void onFailure( Throwable caught )
					{
						isD2DEditable = true;
						okButton.enable();
					}

					@Override
					public void onSuccess( Boolean result )
					{
						boolean isEditable = !result;
						isD2DEditable = isEditable;
						if(isEditable) {
							okButton.enable();
						}
						else {
							okButton.disable();
						}
					}
				});
		}
	
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
		BackupContext.destory();
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
		if(operation == ISettingsContentHost.Operations.SaveData)
		{
			decreaseBusyCount();
		}
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();
		resetSize();
	}

	@Override
	protected void onWindowResize(int width, int height) {
		super.onWindowResize(width, height);
		resetSize();
	}
	
	private void resetSize(){
		if(this.getHeight() > Utils.getScreenHeight() * 0.75) {
			this.setHeight((int)(Utils.getScreenHeight() * 0.75));
		}
	}

	@Override
	public boolean isForCreate() {
		// TODO Auto-generated method stub
		return false;
	}
	
	protected ISettingsContent getSettingsContent() {
		ISettingsContent theSettingsContent = new CommonSettingContent(appType);
		theSettingsContent.initialize( this, false );
		return theSettingsContent;
	}
}