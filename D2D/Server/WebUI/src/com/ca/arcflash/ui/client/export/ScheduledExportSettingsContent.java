package com.ca.arcflash.ui.client.export;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.BaseCommonSettingTab;
import com.ca.arcflash.ui.client.common.ISettingsContent;
import com.ca.arcflash.ui.client.common.ISettingsContentHost;
import com.ca.arcflash.ui.client.common.SettingsGroupType;
import com.ca.arcflash.ui.client.common.UserPasswordWindow;
import com.ca.arcflash.ui.client.common.d2d.presenter.CopyRecoveryPointPresenter;
import com.ca.arcflash.ui.client.common.d2d.presenter.SettingPresenter;
import com.ca.arcflash.ui.client.model.IEmailConfigModel;
import com.ca.arcflash.ui.client.model.ScheduledExportSettingsModel;
import com.ca.arcflash.ui.client.service.Broker;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class ScheduledExportSettingsContent extends LayoutContainer implements
		ISettingsContent {	
	public static final int STACK_SCHEDULEDEXPORT = 0;
	
	protected ISettingsContentHost contentHost;
	
	public ISettingsContentHost getContentHost() {
		return contentHost;
	}
	public DeckPanel deckPanel;
	
	private ScheduledExportSettings scheduledExport;
	
	private LayoutContainer scheduledExportContainer;
	
	private ToggleButton scheduledExportButton;
	
	private ToggleButton scheduledExportLabel;
	
	private ClickHandler scheduledExportButtonHandler;
	
	private VerticalPanel toggleButtonPanel;
	//wanqi06
//	public static final String ERR_REMOTE_DEST_WINSYSMSG = "17179869199";
	public static final String ERR_REMOTE_DEST_WINSYSMSG = "30064771076";
	public static final String DEST_IN_USE = "20937965751";
	//When validate backend failed with 17179869199, we let user try to input username/password again.
//	private boolean firstTry = true;
	
//	private Button okButton;
//	private Button cancelButton;
	
	private SettingsGroupType			settingsGroupType;
	private CopyRecoveryPointPresenter copyRecoveryPointPresenter;
	
	public ScheduledExportSettingsContent(){
		copyRecoveryPointPresenter = new CopyRecoveryPointPresenter(this);
	}
	
	public ScheduledExportSettingsContent(SettingsGroupType settingsGroupType){
		copyRecoveryPointPresenter = new CopyRecoveryPointPresenter(this);
		this.settingsGroupType = settingsGroupType;
	}
	
	public ScheduledExportSettingsModel model;
	
	public List<String> itemsToDisplay = new ArrayList<String>();
	
	private void doInitialization() {
		
		this.setLayout(new RowLayout(Orientation.VERTICAL));
		setStyleAttribute("background-color","#DFE8F6");
		
		LayoutContainer contentPanel = new LayoutContainer();
		contentPanel.setLayout(new RowLayout(Orientation.HORIZONTAL));
		
		deckPanel = new DeckPanel();
		//deckPanel.setWidth("100%");
		//deckPanel.setHeight("100%");
		deckPanel.setStyleName("backupSettingCenter");
		
		scheduledExport = new ScheduledExportSettings(this);
		scheduledExportContainer = new LayoutContainer();
		scheduledExportContainer.add(scheduledExport.render());
		scheduledExportContainer.setStyleAttribute("padding", "10px");
		deckPanel.add(scheduledExportContainer);
		
		toggleButtonPanel = new VerticalPanel();
		toggleButtonPanel.setVerticalAlign(VerticalAlignment.MIDDLE);
		toggleButtonPanel.setHorizontalAlign(HorizontalAlignment.CENTER);
		toggleButtonPanel.setTableWidth("100%");
		toggleButtonPanel.setStyleAttribute("background-color","#DFE8F6");
		
		scheduledExportButtonHandler = new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				deckPanel.showWidget(STACK_SCHEDULEDEXPORT);
				scheduledExportButton.setDown(true);
				scheduledExportLabel.setDown(true);
			}
		};
		
//		scheduledExportButton = new ToggleButton(UIContext.IconBundle.backupDestination().createImage());
		scheduledExportButton = new ToggleButton(AbstractImagePrototype.create(UIContext.IconBundle.scheduledexport_settings_protection()).createImage());
		scheduledExportButton.ensureDebugId("abb3ed21-54ad-4363-ad2b-17f96f659907");
		scheduledExportButton.setStylePrimaryName("demo-ToggleButton");
		scheduledExportButton.setDown(true);
		scheduledExportButton.addClickHandler(scheduledExportButtonHandler);
		toggleButtonPanel.add(scheduledExportButton);
		
//		scheduledExportLabel = new ToggleButton(UIContext.Constants.backupSettingsDestination());
		scheduledExportLabel = new ToggleButton(UIContext.Constants.CopySettings());
		scheduledExportLabel.ensureDebugId("d790d63d-3819-4293-b33c-dafd7f510425");
		scheduledExportLabel.setStylePrimaryName("tb-settings");
		scheduledExportLabel.setDown(true);	
		scheduledExportLabel.addClickHandler(scheduledExportButtonHandler);
		toggleButtonPanel.add(scheduledExportLabel);
		
		itemsToDisplay.add(UIContext.Constants.CopySettings());
		
		contentPanel.add(toggleButtonPanel, new RowData( 140, 1 ));
		contentPanel.add(deckPanel, new RowData(1, 1));
		
		deckPanel.showWidget(STACK_SCHEDULEDEXPORT);
		
		add(contentPanel, new RowData(1, 1));
		
//		LayoutContainer buttonContainer = new LayoutContainer();
//		buttonContainer.setStyleAttribute("background-color","#DFE8F6");
//		
//		TableLayout tableLayout = new TableLayout();
//		tableLayout.setWidth("100%");
//		tableLayout.setCellPadding(4);
//		tableLayout.setCellSpacing(4);
//		tableLayout.setColumns(4);		
		
		//Repeat Section
//		TableData td = new TableData();
//		td.setHorizontalAlign(HorizontalAlignment.LEFT);
//		td.setVerticalAlign(VerticalAlignment.BOTTOM);
//		td.setWidth("100%");
//		buttonContainer.setLayout(tableLayout);				
//	
//		LabelField leftSpace = new LabelField();
//		buttonContainer.add(leftSpace, td);
//		
//		td = new TableData();
//		td.setHorizontalAlign(HorizontalAlignment.RIGHT);
//		td.setVerticalAlign(VerticalAlignment.BOTTOM);
		
//		okButton = new Button();
//		okButton.setMinWidth(80);
//		okButton.setText(UIContext.Constants.backupSettingsOk());
//		okButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
//			
//			@Override
//			public void componentSelected(ButtonEvent ce) {
//				saveSettings();
//			}
//		});
//		
//		buttonContainer.add(okButton, td);
		
//		cancelButton = new Button();	
//		cancelButton.setMinWidth(80);
//		cancelButton.setText(UIContext.Constants.backupSettingsCancel());
//		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
//			@Override
//			public void componentSelected(ButtonEvent ce) {
//				contentHost.close();
//			}
//		});
//		td = new TableData();
//		td.setHorizontalAlign(HorizontalAlignment.RIGHT);
//		td.setVerticalAlign(VerticalAlignment.BOTTOM);		
//		buttonContainer.add(cancelButton, td);
		
//		add( buttonContainer, new RowData( 1, -1 ) );
		
	}
	
	public boolean saveSettings() {
		
		return save();
	}
	
	private void validateSettings() {
		if(scheduledExport.validate()) {
			scheduledExport.save();
		} else {
			deckPanel.showWidget(STACK_SCHEDULEDEXPORT);
			SettingPresenter.getInstance().setCurrentIndex(BaseCommonSettingTab.scheduledExportSettingsID, STACK_SCHEDULEDEXPORT);
			this.contentHost.showSettingsContent( this.settingsContentId );
			onValidatingCompleted(false);
			return;
		}
		
		validateBackendSetings();
	}
	
	public void onSavingCompleted( boolean isSuccessful )
	{
		
		SettingPresenter.getInstance().onAsyncOperationCompleted(
				ISettingsContentHost.Operations.SaveData,
				isSuccessful ? ISettingsContentHost.OperationResults.Succeeded :
					ISettingsContentHost.OperationResults.Failed,
				this.settingsContentId );
		
	}
	
	public void onValidatingCompleted( boolean isSuccessful )
	{
		
		SettingPresenter.getInstance().onAsyncOperationCompleted(
				ISettingsContentHost.Operations.Validate,
				isSuccessful ? ISettingsContentHost.OperationResults.Succeeded :
					ISettingsContentHost.OperationResults.Failed,
				this.settingsContentId );
		
	}
	
	protected void onLoadingCompleted( boolean isSuccessful )
	{
		
		SettingPresenter.getInstance().onAsyncOperationCompleted(
				ISettingsContentHost.Operations.LoadData,
				isSuccessful ? ISettingsContentHost.OperationResults.Succeeded :
					ISettingsContentHost.OperationResults.Failed,
				this.settingsContentId );
		
	}
	
	public void popupUserpasswordWindow() {
		final UserPasswordWindow dlg = new UserPasswordWindow(model.getDestination(), "", "");
		dlg.setModal(true);
		
		dlg.addWindowListener(new WindowListener()
		{				
			public void windowHide(WindowEvent we) {
				if (dlg.getCancelled() == false)
				{
					String username = dlg.getUsername();
					String password = dlg.getPassword();
					model.setDestUserName(username);
					model.setDestPassword(password);
					scheduledExport.pathSelection.setUsername(username);
					scheduledExport.pathSelection.setPassword(password);
					validateBackendSetings();
				}
				else {
					onValidatingCompleted(false);
				}
			}
		});
		dlg.show();
	
	}
	
	
	private void validateBackendSetings() {
		this.copyRecoveryPointPresenter.validate();
//		Broker.loginService.validateScheduledExportConfiguration(model, new BaseAsyncCallback<Long>() {
//			@Override
//			public void onSuccess(Long result) {
//				super.onSuccess(result);
//				onValidatingCompleted(true);
//			}
//			
//			@Override
//			public void onFailure(Throwable caught) {
//				if(caught instanceof BusinessLogicException 
//						&& ERR_REMOTE_DEST_WINSYSMSG.equals(((BusinessLogicException)caught).getErrorCode()))
//					{
//						final Throwable orginialExc = caught;
//						CommonServiceAsync commonService = GWT.create(CommonService.class);
//						commonService.getDestDriveType(model.getDestination(), new BaseAsyncCallback<Long>()
//					             {
//									@Override
//									public void onFailure(Throwable caught) {
//										onValidatingCompleted(false);
//										super.onFailure(orginialExc);
//									}
//							    	@Override
//									public void onSuccess(Long result) {
//							    		if(result == PathSelectionPanel.REMOTE_DRIVE )
//							    		{
//							    			popupUserpasswordWindow();
//							    		}
//							    		else {
//											onValidatingCompleted(false);
//							    			super.onFailure(orginialExc);
//							    		}
//									}
//						    	}
//					      	);
//					}else{
//						onValidatingCompleted(false);
//					    super.onFailure(caught);
//					}
//			}
//		});
	}
	
	private boolean save() {
		contentHost.increaseBusyCount();
		Broker.loginService.saveScheduledExportConfiguration(model,
				new BaseAsyncCallback<Long>() {
					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						onSavingCompleted(false);
						contentHost.decreaseBusyCount();
					}

					@Override
					public void onSuccess(Long result) {
						onSavingCompleted(true);
						contentHost.decreaseBusyCount();
					}
				});
		return false;
	}
	
	private ScheduledExportSettingsModel getDefaultModel() {
		ScheduledExportSettingsModel model = new ScheduledExportSettingsModel();
		model.setEnableScheduledExport(false);
		model.setExportInterval(ScheduledExportSettings.DEFAULTEXPORTINTERVAL);
		model.setKeepRecoveryPoints(ScheduledExportSettings.DEFAULTKEEPRP);
		
		return model;
	}

	//////////////////////////////////////////////////////////////////////////
	//
	//  ADDED FOR EDGE
	//
	//////////////////////////////////////////////////////////////////////////

	private boolean isForEdge = false;
	private int settingsContentId = -1;
	@Override
	public void initialize(ISettingsContentHost contentHost, boolean isForEdge) {
		this.contentHost = contentHost;
		this.isForEdge = isForEdge;
		doInitialization();
	}

	@Override
	public boolean isForEdge() {
		
		return this.isForEdge;
	}

	@Override
	public void setIsForEdge(boolean isForEdge) {
		this.isForEdge = isForEdge;

	}

	@Override
	public void setId(int settingsContentId) {
		this.settingsContentId = settingsContentId;
	}

	@Override
	public Widget getWidget() {
		return this;
	}

	@Override
	public void loadData() {
		ScheduledExportSettingsModel sModel = null; 
		
		if (this.settingsGroupType == SettingsGroupType.VMBackupSettings){
			if (SettingPresenter.model!=null)
				sModel = SettingPresenter.model.scheduledExportSettingsModel;
		}else
			sModel = SettingPresenter.getInstance().getD2dSettings().getScheduledExportSettingsModel();
	
		if(sModel == null) {
			model = getDefaultModel();
			SettingPresenter.getInstance().getD2dSettings().setScheduledExportSettingsModel(model);
		}else {
			model = sModel;
		}
		refreshData();
		onLoadingCompleted(true);
	}
	
//	private void loadSettings() {
//		contentHost.increaseBusyCount();
//		Broker.loginService.getScheduledExportConfiguration(new BaseAsyncCallback<ScheduledExportSettingsModel>() {
//			@Override
//			public void onFailure(Throwable caught) {
//				contentHost.decreaseBusyCount();
//				super.onFailure(caught);
//				onLoadingCompleted(false);
//			}
//			
//			@Override
//			public void onSuccess(ScheduledExportSettingsModel result) {
//				if(result != null) {
//					model = result;
//					refreshData();
//				} else {
//					loadDefaultSettings();
//				}
//				contentHost.decreaseBusyCount();
//				onLoadingCompleted(true);
//			}
//		});
//	}
	
	private void loadDefaultSettings() {
		model = getDefaultModel();
		refreshData();
		onLoadingCompleted(true);
	}
	
	public void refreshData() {
		scheduledExport.refreshData(model);
	}

	@Override
	public void loadDefaultData() {
		loadDefaultSettings();
	}

	protected void SaveForEdge()
	{
		
		if (!this.scheduledExport.checkShareFolder())
		{
			deckPanel.showWidget(STACK_SCHEDULEDEXPORT);
			contentHost.decreaseBusyCount();
			onSavingCompleted(false);
			return ;
		}
		
		this.scheduledExport.validateRemotePath(new BaseAsyncCallback<Boolean>(false) {
			
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				deckPanel.showWidget(STACK_SCHEDULEDEXPORT);
				SettingPresenter.getInstance().setCurrentIndex(BaseCommonSettingTab.scheduledExportSettingsID, STACK_SCHEDULEDEXPORT);
				contentHost.decreaseBusyCount();
				onSavingCompleted(false);
			}
			
			@Override
			public void onSuccess(Boolean result) {
				if (!result) {
					deckPanel.showWidget(STACK_SCHEDULEDEXPORT);
					SettingPresenter.getInstance().setCurrentIndex(BaseCommonSettingTab.scheduledExportSettingsID, STACK_SCHEDULEDEXPORT);	
					contentHost.decreaseBusyCount();
					onSavingCompleted(false);
					return;
				}
				{
					contentHost.decreaseBusyCount();
					onSavingCompleted(true);
				}

			}
			
		});
		
		
	}
	@Override
	public void saveData() {
		SettingPresenter.getInstance().getD2dSettings().setScheduledExportSettingsModel(model);
		if(this.isForEdge){
			contentHost.increaseBusyCount(UIContext.Constants.settingsMaskText());
			this.SaveForEdge();
		}else{
			this.onSavingCompleted(true);
		}
//		save();
	}

	@Override
	public void validate() {
		validateSettings();
	}
	
	@Override
	public void setDefaultEmail(IEmailConfigModel iEmailConfigModel)
	{
		
	}
	/**
	 * This method can be only used for disable edit. 
	 * Don't use method to enable edit. Enable this edit using this method will make UI value wrong.
	 */
	public void enableEditing(boolean flag) {
		scheduledExport.setEditable(flag);
	}

	@Override
	public boolean isForLiteIT() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setisForLiteIT(boolean isForLiteIT) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<SettingsTab> getTabList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void switchTab(String tabId) {
		// TODO Auto-generated method stub
		
	}
}
