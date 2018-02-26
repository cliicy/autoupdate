package com.ca.arcflash.ui.client.backup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.advschedule.BackupScheduleTabItem;
import com.ca.arcflash.ui.client.backup.schedule.DailyScheduleDetailItemModel;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleDetailItemModel;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.BaseCommonSettingTab;
import com.ca.arcflash.ui.client.common.BaseLicenseAsyncCallback;
import com.ca.arcflash.ui.client.common.ExtCardPanel;
import com.ca.arcflash.ui.client.common.IRefreshable;
import com.ca.arcflash.ui.client.common.ISettingsContent;
import com.ca.arcflash.ui.client.common.ISettingsContentHost;
import com.ca.arcflash.ui.client.common.SettingsGroupType;
import com.ca.arcflash.ui.client.common.UserPasswordWindow;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.common.d2d.presenter.BackupSettingPresenter;
import com.ca.arcflash.ui.client.common.d2d.presenter.SettingPresenter;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.model.AccountModel;
import com.ca.arcflash.ui.client.model.BackupScheduleIntervalUnitModel;
import com.ca.arcflash.ui.client.model.BackupScheduleModel;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.ca.arcflash.ui.client.model.BackupTypeModel;
import com.ca.arcflash.ui.client.model.BackupVolumeModel;
import com.ca.arcflash.ui.client.model.D2DTimeModel;
import com.ca.arcflash.ui.client.model.DestinationCapacityModel;
import com.ca.arcflash.ui.client.model.IEmailConfigModel;
import com.ca.arcflash.ui.client.model.RetentionPolicyModel;
import com.ca.arcflash.ui.client.model.SRMAlertSettingModel;
import com.ca.arcflash.ui.client.service.Broker;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.core.FastMap;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class BackupSettingsContent extends LayoutContainer implements ISettingsContent
{
	protected BackupSettingsContent outerThis;
	protected ISettingsContentHost contentHost;
	
	public ISettingsContentHost getContentHost() {
		return contentHost;
	}
	public static final int DEFAULT_RETENTION_COUNT = 31;
	
	//AdvancedScheduleSettings schedule;
	protected RepeatAdvancedScheduleSettings schedule;
	PeriodAdvancedScheduleSettings periodSchedule;
	private BackupSettings settings;
	private AdvancedSettings advanced;
	
	private ScheduleSummaryPanel schedueSummary;
	private ScheduleSettings simpleSchedule;
	private BackupScheduleTabItem advScheduleItem;
	
	private BaseDestinationSettings destination;
	
	protected LayoutContainer destinationContainer;
	LayoutContainer scheduleContainer;
	LayoutContainer settingsContainer;
	LayoutContainer advancedContainer;
	
	public List<String> itemsToDisplay = new ArrayList<String>();
	
	public ScheduleSettings getSimpleSchedule(){
		return simpleSchedule;
	}
	
	public BackupScheduleTabItem getAdvScheduleItem() {
		return advScheduleItem;
	}
	
	
	public ExtCardPanel deckPanel;
	
	protected ToggleButton scheduleButton;
	protected ToggleButton settingsButton;
	protected ToggleButton advancedButton;
	protected ToggleButton destinationButton;
	
	protected ToggleButton scheduleLabel;
	protected ToggleButton settingsLabel;
	protected ToggleButton advancedLabel;
	protected ToggleButton destinationLabel;
		
	private ClickHandler scheduleButtonHandler;
	private ClickHandler settingsButtonHandler;
	private ClickHandler advancedButtonHandler;
	private ClickHandler destinationButtonHandler;
	
	protected VerticalPanel toggleButtonPanel;
	
//	public BackupSettingsModel model;
	public DestinationCapacityModel destModel;
	
	public static final int STACK_DESTINATION = 0;
	//public final int STACK_SCHEDULE = 1;
	public static final int STACK_SETTINGS = 2;
	public static final int STACK_ADVANCED = 3;
	//public final int STACK_SELFUPDATE = 4;
	//public final int STACK_EMAILALERT = 4;
	
	public static final int STACK_REPEAT = 4;
	public static final int STACK_PERIODICALLY = 5;
	public static final int STACK_STACK_SCHEDULE_SUMMARRY = 6;
	public static final int STACK_SCHEDULE_SIMPLE = 7;
	public static final int STACK_SCHEDULE_Adv = 8;
	
	public static final long AF_ERR_DEST_SYSVOL = 3758096417l;
	public static final long AF_ERR_DEST_BOOTVOL = 3758096418l; 
	public static final String ERR_REMOTE_DEST_WINSYSMSG = "17179869199";
	
//	//When validate backend failed with 17179869199, we let user try to input username/password again.
//	private boolean firstTry = true;
	
	private static int buttonSelected;
	
	private String refsVolList;

	private Boolean isAllVolumeIsRefsOrDedup = false;
	
	protected SettingsGroupType settingsGroupType;
	
	public Map<String, LayoutContainer> map = new FastMap<LayoutContainer>();
	
	public Boolean getIsAllVolumeIsRefsOrDedup() {
		return isAllVolumeIsRefsOrDedup;
	}

	public void setIsAllVolumeIsRefsOrDedup(Boolean isAllVolumeIsRefsOrDedup) {
		this.isAllVolumeIsRefsOrDedup = isAllVolumeIsRefsOrDedup;
	}
	
	public BaseDestinationSettings getDestination() {
		return destination;
	}
	public void setDestination(BaseDestinationSettings destination) {
		this.destination = destination;
	}
	private BackupSettingPresenter backupSettingPresenter;
	public BackupSettingsContent()
	{
		outerThis = this;
		backupSettingPresenter = new BackupSettingPresenter(this);	
	}

	
	public void focusPanel(int index){
		deckPanel.showWidget(index);
	}
	
	
	
	public String getRefsVolList() {
		return refsVolList;
	}

	public void setRefsVolList(String refsVolList) {
		this.refsVolList = refsVolList;
	}

	public static int getButtonSelected() {
		return buttonSelected;
	}

	public static void setButtonSelected(int buttonSelected) {
		BackupSettingsContent.buttonSelected = buttonSelected;
	}
	
	protected void addPanels(LayoutContainer contentPanel) {
		contentPanel.add( toggleButtonPanel, new RowData( 140, 1 ) );
		contentPanel.add( deckPanel, new RowData( 1, 1 ) );
		this.add( contentPanel, new RowData( 1, 1 ) );
	}
	
	protected void setLayout(LayoutContainer contentPanel) {
		this.setLayout( new RowLayout( Orientation.VERTICAL ) );
		contentPanel.setLayout( new RowLayout( Orientation.HORIZONTAL ) );
	}
	
	protected void doInitialization() {
		
		LayoutContainer contentPanel = new LayoutContainer();
		setLayout(contentPanel);

		this.setStyleAttribute("background-color","#DFE8F6");
				
		deckPanel = new ExtCardPanel();
		//deckPanel.setWidth("100%");
		//deckPanel.setHeight("100%");
		deckPanel.setStyleName("backupSettingCenter");
		
		createDestinationSettings();
		destinationContainer.setStyleAttribute("padding", "10px");
		deckPanel.add(destinationContainer);		
		
		itemsToDisplay.add(UIContext.Constants.backupSettingsDestination());
		
		AdvancedScheduleSettings schedule1 = new AdvancedScheduleSettings(this);
		scheduleContainer = new LayoutContainer();
		scheduleContainer.add(schedule1.Render());
		scheduleContainer.setStyleAttribute("padding", "10px");
		scheduleContainer.setStyleName("backupsetting_schedule_panel");
		deckPanel.add(scheduleContainer);
		
		itemsToDisplay.add(UIContext.Constants.backupSettingsSchedule());
		
		settings = new BackupSettings(this);
		settingsContainer = new LayoutContainer();
		settingsContainer.add(settings.Render());
		settingsContainer.setStyleAttribute("padding", "10px");
		settingsContainer.setStyleName("backupsetting_inner_panel");
		deckPanel.add(settingsContainer);
		
		itemsToDisplay.add(UIContext.Constants.backupSettingsSettings());
		
		advanced = new AdvancedSettings(this, settingsGroupType);
		advancedContainer = new LayoutContainer();
		advancedContainer.add(advanced.Render());
		advancedContainer.setStyleAttribute("padding", "10px");
		deckPanel.add(advancedContainer);
		
		itemsToDisplay.add(UIContext.Constants.backupSettingsPrePost());
		
		schedule = new RepeatAdvancedScheduleSettings(this);
		LayoutContainer repeatContainer = new LayoutContainer();
		repeatContainer.add(schedule.Render());
		repeatContainer.setStyleName("backupsetting_schedule_panel");
		repeatContainer.setStyleAttribute("padding", "10px");
		deckPanel.add(repeatContainer);		
		
		periodSchedule = new PeriodAdvancedScheduleSettings(this);
		LayoutContainer periodContainer = new LayoutContainer();
		periodContainer.add(periodSchedule.Render());
		periodContainer.setStyleAttribute("padding", "10px");
		deckPanel.add(periodContainer);
		
		schedueSummary = new ScheduleSummaryPanel();
//		LayoutContainer schedueSummaryContainer = new LayoutContainer();		
//		schedueSummaryContainer.add(schedueSummary.Render());
		//schedueSummaryContainer.setStyleAttribute("padding", "10px");
		deckPanel.add(schedueSummary);
		
		simpleSchedule = new ScheduleSettings(this, settingsGroupType);
		LayoutContainer simpleScheduleContainer = new LayoutContainer();		
		simpleScheduleContainer.add(simpleSchedule.Render());		
		deckPanel.add(simpleScheduleContainer);
		
		
		advScheduleItem = new BackupScheduleTabItem(settingsGroupType);
		LayoutContainer advScheduleItemContainer = new LayoutContainer();		
		advScheduleItemContainer.add(advScheduleItem);		
		deckPanel.add(advScheduleItemContainer);
		
		toggleButtonPanel = new VerticalPanel();
		toggleButtonPanel.setVerticalAlign(VerticalAlignment.MIDDLE);
		toggleButtonPanel.setHorizontalAlign(HorizontalAlignment.CENTER);
		toggleButtonPanel.setTableWidth("100%");
		//toggleButtonPanel.setHeight(520);
		toggleButtonPanel.setStyleAttribute("background-color","#DFE8F6");
		
//		destinationButton = new ToggleButton(UIContext.IconBundle.backupDestination().createImage());		
		destinationButton = new ToggleButton(AbstractImagePrototype.create(UIContext.IconBundle.backup_settings_protection()).createImage());
		destinationButton.setStylePrimaryName("demo-ToggleButton");
		destinationButton.setDown(true);
		destinationButton.ensureDebugId("942CA081-3A72-4dc5-A100-F4709EDAC891");
		
		
		destinationButtonHandler = new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {					
				deckPanel.showWidget(STACK_DESTINATION);				
				destinationButton.setDown(true);
				advancedButton.setDown(false);
				settingsButton.setDown(false);
				scheduleButton.setDown(false);					
				//selfUpdateSettingsButton.setDown(false);
				
				destinationLabel.setDown(true);
				advancedLabel.setDown(false);
				settingsLabel.setDown(false);
				scheduleLabel.setDown(false);
				setButtonSelected(1);
				//selfUpdateSettingsLabel.setDown(false);
				// Updating heading.
				contentHost.setCaption(UIContext.Messages.backupSettingsWindowWithTap(
						UIContext.Constants.backupSettingsDestination()));
			}			
		};
		scheduleButtonHandler = new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
			//	deckPanel.showWidget(STACK_SCHEDULE);	
				deckPanel.showWidget(STACK_REPEAT);
				destinationButton.setDown(false);
				advancedButton.setDown(false);
				settingsButton.setDown(false);
				scheduleButton.setDown(true);
				//selfUpdateSettingsButton.setDown(false);
				
				destinationLabel.setDown(false);
				advancedLabel.setDown(false);
				settingsLabel.setDown(false);
				scheduleLabel.setDown(true);
				setButtonSelected(2);
				
				// Updating heading.
				contentHost.setCaption(UIContext.Messages.backupSettingsWindowWithTap(
						UIContext.Constants.backupSettingsSchedule()));
			}
			
		};
			
		settingsButtonHandler = new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				deckPanel.showWidget(STACK_SETTINGS);
				destinationButton.setDown(false);
				advancedButton.setDown(false);
				settingsButton.setDown(true);
				scheduleButton.setDown(false);
				//selfUpdateSettingsButton.setDown(false);
				
				destinationLabel.setDown(false);
				advancedLabel.setDown(false);
				settingsLabel.setDown(true);
				scheduleLabel.setDown(false);
				setButtonSelected(3);
				// Updating heading.
				contentHost.setCaption(UIContext.Messages.backupSettingsWindowWithTap(
						UIContext.Constants.backupSettingsSettings()));
			}
			
		};
			
		advancedButtonHandler = new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				deckPanel.showWidget(STACK_ADVANCED);
				destinationButton.setDown(false);
				advancedButton.setDown(true);
				settingsButton.setDown(false);
				scheduleButton.setDown(false);	
				//selfUpdateSettingsButton.setDown(false);
				
				destinationLabel.setDown(false);
				advancedLabel.setDown(true);
				settingsLabel.setDown(false);
				scheduleLabel.setDown(false);
				setButtonSelected(4);
				// Updating heading.
				contentHost.setCaption(UIContext.Messages.backupSettingsWindowWithTap(
						UIContext.Constants.backupSettingsAdvanced()));
			}
			
		};
		
		
		destinationButton.addClickHandler(destinationButtonHandler);
		
		toggleButtonPanel.add(destinationButton);
		destinationLabel = new ToggleButton(UIContext.Constants.backupSettingsDestination());
		destinationLabel.ensureDebugId("D9BB83E8-A767-4629-A912-556E6F4E720E");
		destinationLabel.setStylePrimaryName("tb-settings");
		destinationLabel.setDown(true);	
		destinationLabel.addClickHandler(destinationButtonHandler);		
		toggleButtonPanel.add(destinationLabel);
		
//		scheduleButton = new ToggleButton(UIContext.IconBundle.backupSchedule().createImage());
		scheduleButton = new ToggleButton(AbstractImagePrototype.create(UIContext.IconBundle.backup_settings_schedule()).createImage());
		scheduleButton.ensureDebugId("BE7BB55F-5050-4b35-AFC0-B58705423C6C");
		scheduleButton.setStylePrimaryName("demo-ToggleButton");
		scheduleButton.addClickHandler(scheduleButtonHandler);
		
		toggleButtonPanel.add(scheduleButton);
		scheduleLabel = new ToggleButton(UIContext.Constants.backupSettingsSchedule());
		scheduleLabel.ensureDebugId("4F6A8B91-0A7A-4994-964D-AC7A3190610A");
		scheduleLabel.setStylePrimaryName("tb-settings");
		scheduleLabel.addClickHandler(scheduleButtonHandler);
		toggleButtonPanel.add(scheduleLabel);
		
//		settingsButton = new ToggleButton(UIContext.IconBundle.backupSettings().createImage());
		settingsButton = new ToggleButton(AbstractImagePrototype.create(UIContext.IconBundle.backup_settings_settings()).createImage());
		settingsButton.ensureDebugId("B38DF4DF-80AE-4b76-950C-1AE677D7BE0B");
		settingsButton.setStylePrimaryName("demo-ToggleButton");
		settingsButton.addClickHandler(settingsButtonHandler);
		
		toggleButtonPanel.add(settingsButton);
		settingsLabel = new ToggleButton(UIContext.Constants.backupSettingsSettings());
		settingsLabel.ensureDebugId("1AC09A83-8A01-4552-ADAF-CBBBD4146E90");
		settingsLabel.setStylePrimaryName("tb-settings");
		settingsLabel.addClickHandler(settingsButtonHandler);
		toggleButtonPanel.add(settingsLabel);
		
//		advancedButton = new ToggleButton(UIContext.IconBundle.backupAdvanced().createImage());
		advancedButton = new ToggleButton(AbstractImagePrototype.create(UIContext.IconBundle.backup_settings_advanced()).createImage());
		advancedButton.ensureDebugId("E684DAA6-914F-482e-BB00-A0012D180B13");
		advancedButton.setStylePrimaryName("demo-ToggleButton");
		advancedButton.addClickHandler(advancedButtonHandler);
		
		toggleButtonPanel.add(advancedButton);
		advancedLabel = new ToggleButton(UIContext.Constants.backupSettingsAdvanced());
		advancedLabel.ensureDebugId("3A89A05C-EC63-49c0-898C-7841AF5178B8");
		advancedLabel.setStylePrimaryName("tb-settings");
		advancedLabel.addClickHandler(advancedButtonHandler);
		toggleButtonPanel.add(advancedLabel);
	
		addPanels(contentPanel);
		// Default Tab - destination.
		contentHost.setCaption(UIContext.Messages.backupSettingsWindowWithTap(
				UIContext.Constants.backupSettingsDestination()));
		//Load the Backup Settings
		deckPanel.showWidget(STACK_DESTINATION);
	}

	protected void createDestinationSettings() {
		destination = new D2DDestinationSettings(this);
		destination.setContentHost(contentHost);
		destinationContainer = new LayoutContainer();
		destinationContainer.add(destination.Render());
	}	
	
	public void updateNotification()
	{
		if(settings != null)
			settings.updateNotificationSet();
	}
	//////////////////////////////////////////////////////////////////////////
	/**
	 * This method can be only used for disable edit. 
	 * Don't use method to enable edit. Enable this edit using this method will make UI value wrong.
	 */
	public void enableEditing( boolean isEnabled )
	{ 
		//this.destinationContainer.setEnabled( isEnabled );
		this.destination.setEditable(isEnabled);
		this.schedule.setEditable(isEnabled);
		this.settings.setEditable(isEnabled);
		this.advanced.setEditable(isEnabled);
		//this.periodSchedule.setEditable(isEnabled);
		this.simpleSchedule.setEditable(isEnabled);
		this.advScheduleItem.setEditable(isEnabled);
	}
	
	public void RefreshData()
	{
		schedule.RefreshData(SettingPresenter.model, backupSettingFileExist, !destination.backupToRPS());
		settings.RefreshData(SettingPresenter.model, backupSettingFileExist);
		advanced.RefreshData(SettingPresenter.model);
		destination.RefreshData(SettingPresenter.model, backupSettingFileExist);
		schedueSummary.RefreshData(SettingPresenter.model);
		simpleSchedule.RefreshData(SettingPresenter.model);
		//periodSchedule.RefreshData(SettingPresenter.model, backupSettingFileExist);
		advScheduleItem.RefreshData(SettingPresenter.model, backupSettingFileExist, !destination.backupToRPS());
	}
	boolean backupSettingFileExist = false;
	public void LoadSettings()
	{
		BaseAsyncCallback<BackupSettingsModel> callback = new BaseAsyncCallback<BackupSettingsModel>(){
			
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				
				onLoadingCompleted(false);
			}
			@Override
			public void onSuccess(BackupSettingsModel result) {
				// TODO Auto-generated method stub
				if (result != null)
				{
					backupSettingFileExist = true;
					SettingPresenter.model = result;
					outerThis.RefreshData();
					
					onLoadingCompleted(true);
				}
				else
				{
					loadDefaultSettings();
				}
			}
		};
		fetchDataFromServer(callback);		
	}

	//VSPHERE Settings Use the two API: over write the two API in the VSphereBackupSettingContent
	protected void fetchDataFromServer(
			BaseAsyncCallback<BackupSettingsModel> callback) {
		
	}
	
	protected void saveBackupConfiguration() throws Exception {
		
	}
	//
	
	
	protected void loadDefaultSettings()
	{
		Broker.loginService.getAdminAccount(new BaseAsyncCallback<AccountModel>() {
			@Override
			public void onFailure(Throwable caught) {
				//the administrator account may does not exist.
				setServerTimeAndRepaint(null);
			}
			@Override
			public void onSuccess(AccountModel result) {
				setServerTimeAndRepaint(result);
			}

			private void setServerTimeAndRepaint(
					final AccountModel accountModel) {
				Broker.loginService.getServerTime(new BaseAsyncCallback<Date>() {
					@Override
					public void onSuccess(Date result) {
						SettingPresenter.model = getDefaultModel();
						long serverTimeInMilliseconds = result.getTime();
						//set backup start time plus 5 minutes
						serverTimeInMilliseconds += 5 * 60 * 1000;
						SettingPresenter.model.setBackupStartTime(serverTimeInMilliseconds);
						if(accountModel != null) {
							SettingPresenter.model.setAdminUserName(accountModel.getUserName());
							SettingPresenter.model.setAdminPassword(accountModel.getPassword());
						}
						D2DTimeModel time = new D2DTimeModel();
						Date serverDate = Utils.localTimeToServerTime(new Date(serverTimeInMilliseconds));
						time.fromJavaDate(serverDate);
						SettingPresenter.model.startTime = time;
						outerThis.RefreshData();
						
						onLoadingCompleted(true);
					}

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						
						onLoadingCompleted(false);
					}
				});
			}
		});
	}
	
	private BackupSettingsModel getDefaultModel() {
		
		//fix 18898048
		backupSettingFileExist = false;
		BackupSettingsModel model = SettingPresenter.model = createNewSettingModel();
		
		BackupVolumeModel backupVolumes = new BackupVolumeModel();
		backupVolumes.setIsFullMachine(Boolean.TRUE);
		model.setBackupVolumes(backupVolumes);
		
		model.fullSchedule = new BackupScheduleModel();
		model.fullSchedule.setEnabled(false);				
		
		model.incrementalSchedule = new BackupScheduleModel();
		model.incrementalSchedule.setEnabled(true);
		model.incrementalSchedule.setInterval(1);
		model.incrementalSchedule.setIntervalUnit(BackupScheduleIntervalUnitModel.Day);
		
		model.resyncSchedule = new BackupScheduleModel();
		model.resyncSchedule.setEnabled(false);
		
		model.setRetentionCount(DEFAULT_RETENTION_COUNT);
		model.setCompressionLevel(1); //Standard
		model.setEnableEncryption(false);
		
		model.setPurgeSQLLogDays(0L);
		model.setPurgeExchangeLogDays(0L);
		
		model.setExchangeGRTSetting(1L);
		model.setSharePointGRTSetting(0L);
		
		SRMAlertSettingModel alertModel = new SRMAlertSettingModel();
		alertModel.getDefaultValue();
		model.setSrmAlertSetting(alertModel);
		
		return model;
	}

	protected BackupSettingsModel createNewSettingModel() {
		return new BackupSettingsModel();
	}		
	
//	protected void validateSchedule(final D2DTimeModel time) {
//
//		Broker.commonService.validateBackupStartTime(time.getYear(), time.getMonth(), 
//				time.getDay(), time.getHourOfDay(), time.getMinute(), new BaseAsyncCallback<Long>(){
//			@Override
//			public void onFailure(Throwable caught) {
//				if(caught instanceof BusinessLogicException) {
//					final BusinessLogicException ble = (BusinessLogicException)caught;
//					if(ble.getErrorCode() != null&& ble.getErrorCode().equals("-1")){
//						MessageBox msg = new MessageBox();
//						msg.setIcon(MessageBox.ERROR);
//						msg.setTitle(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
//						String[] timearr = formatStartTimeErrorMsg(time.getHourOfDay());
//						msg.setMessage(UIContext.Messages.settingDSTStartTime(timearr[0], 
//								timearr[0] + "-" + timearr[1]));
//						msg.setModal(true);
//						msg.show();
//						scheduleValidateFail();
//					}else if(ble.getErrorCode() != null 
//							&& ble.getErrorCode().equals("-2")) {
//						MessageBox msg = new MessageBox();
//						msg.setIcon(MessageBox.WARNING);
//						msg.setButtons(MessageBox.YESNO);
//						msg.setTitle(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
//						String[] timearr = formatStartTimeErrorMsg(time.getHourOfDay());
//						msg.setMessage(UIContext.Messages.settingDSTEndTime(
//								timearr[1], timearr[0] + "-" + timearr[1], 
//								UIContext.productNameD2D));
//						msg.setModal(true);
//						msg.addCallback(new Listener<MessageBoxEvent>(){
//
//							@Override
//							public void handleEvent(MessageBoxEvent be) {
//								if(be.getButtonClicked().getItemId().equals(Dialog.YES)) {
//									schedule.Save(Long.parseLong(ble.getDisplayMessage()));
//									validateAfterSchedule();									
//								}else {
//									scheduleValidateFail();
//								}
//							}
//						});
//						msg.show();
//					}
//				}else{
//					schedule.Save(-1);
//					validateAfterSchedule();
//				}
//			}
//
//			@Override
//			public void onSuccess(Long result) {
//				schedule.Save(result);
//				validateAfterSchedule();
//			}
//		});
//	}
	
	void validateUISettings() {
		//model = new BackupSettingsModel();
		
		if (advanced.Validate())
		{
			this.advanced.Save();
		}
		else
		{
			deckPanel.showWidget(STACK_ADVANCED);			
			SettingPresenter.getInstance().setCurrentIndex(BaseCommonSettingTab.d2dBackupSettingID, STACK_ADVANCED);
			this.repaint(); // fix Issue: 20238307    Title: UI MIXED AFTER LICE MSG BOX
			
			//layout.setActiveItem(advancedContainer);
			this.contentHost.showSettingsContent( this.settingsContentId );
			this.onValidatingCompleted(false);
			return;
		}
		
		if (destination.Validate())
		{
			this.destination.Save();
		}
		else
		{
			deckPanel.showWidget(STACK_DESTINATION);
			this.repaint(); // fix Issue: 20238307    Title: UI MIXED AFTER LICE MSG BOX
			
			SettingPresenter.getInstance().setCurrentIndex(BaseCommonSettingTab.d2dBackupSettingID, STACK_DESTINATION);
			//layout.setActiveItem(destinationContainer);
			this.contentHost.showSettingsContent( this.settingsContentId );
			this.onValidatingCompleted(false);
			return;
		}
		
		if (SettingPresenter.getInstance().isAdvSchedule()) {
			
//			if (schedule.Validate()) {
//				model.fullSchedule = null;
//				model.incrementalSchedule = null;
//				model.resyncSchedule = null;
//				schedule.Save(-1);
//			} else {
//				// deckPanel.showWidget(STACK_SCHEDULE);
//				deckPanel.showWidget(STACK_REPEAT);
//				SettingPresenter.getInstance().setCurrentIndex(BaseCommonSettingTab.d2dBackupSettingID, STACK_REPEAT);
//				this.repaint();
//				this.contentHost.showSettingsContent(this.settingsContentId);
//				this.onValidatingCompleted(false);
//				return;
//			}
//			
//			
//			if (this.periodSchedule.Validate()) {				
//				periodSchedule.Save(-1);
//			} else {
//				// deckPanel.showWidget(STACK_SCHEDULE);
//				deckPanel.showWidget(this.STACK_PERIODICALLY);
//				SettingPresenter.getInstance().setCurrentIndex(BaseCommonSettingTab.d2dBackupSettingID, STACK_PERIODICALLY);
//				this.repaint();
//				this.contentHost.showSettingsContent(this.settingsContentId);
//				this.onValidatingCompleted(false);
//				return;
//			}
			
			
			if (this.advScheduleItem.validate()) {				
				advScheduleItem.buildValue(SettingPresenter.model);
			} else {				
				deckPanel.showWidget(STACK_SCHEDULE_Adv);
				SettingPresenter.getInstance().setCurrentIndex(BaseCommonSettingTab.d2dBackupSettingID, STACK_SCHEDULE_Adv);
				this.repaint();
				this.contentHost.showSettingsContent(this.settingsContentId);
				this.onValidatingCompleted(false);
				return;
			}	
			
		} else {
			SettingPresenter.model.advanceScheduleModel = null;
			if(simpleSchedule.Validate()){
				simpleSchedule.Save(UIContext.serverVersionInfo.getTimeZoneOffset());
			}

		}
		
		validateAfterSchedule();
		
	}

//	private String[] formatStartTimeErrorMsg(int hour) {
//		//hour is start time, we also need to compute the DST end time
//		boolean isAM = false;
//		boolean endAM = false;
//		int endHour = 0;
//		if( !Utils.is24Hours() ){							//for 12 hours
//			if( Utils.minHour() == 0 ){					//	for 0-11 clock
//				if( hour < 12 ){					//		for am
//					isAM = true;
//					endHour = hour + 1;
//					if(endHour == 12) {
//						endAM = false;
//						endHour = 0;	
//					}else {
//						endAM = true;
//					}
//				}
//				else{								//		for pm
//					isAM = false;
//					if( hour == 12 )				//			translate 12:30 to 0:30 pm
//						hour = 0;
//					else
//						hour = hour - 12;			//			translate 18:30 to 6:30 pm
//					endHour = hour + 1;
//					endAM = false;
//					if(endHour == 12) {
//						endHour = 0;
//						endAM = true;
//					}
//				}
//			}
//			else{									//	for 1-12 clock
//				if( hour < 12 ){					//		for am
//					isAM = true;
//					endHour = hour + 1;
//					endAM = true;
//					if(endHour == 12) {
//						endAM = false;
//					}
//					if( hour == 0 )					//			translate 0:30 to 12:30 am
//						hour = 12;
//				}
//				else{								//		for pm
//					isAM = false;					
//					if( hour != 12 ){				//			translate 12:30 to 12:30 pm
//						hour -= 12;
//						endHour = hour + 1;
//						endAM = false;
//						if(endHour == 12)
//							endAM = true;
//					}
//					else {
//						endHour = 1;
//						endAM = false;
//					}
//				}
//			}
//		}
//		else{										//for 24 hours
//			if( Utils.minHour() == 1)						//	for 1-24 clock
//			{
//				if( hour == 0 )						//		translate 0:30 to 24:30
//					hour = 24;
//				endHour = hour + 1;
//				if(endHour > 24)
//					endHour -= 24;
//			}else {
//				endHour = hour + 1;
//				if(endHour == 24)
//					endHour = 0;
//			}
//		}
//		
//		String start = hourToString(hour, isAM);
//		String end = hourToString(endHour, endAM);
//		return new String[]{start, end};
//	}
	
//	private String hourToString(int hour, boolean isAM) {
//		String hourVal = "";
//		if( Utils.isHourPrefix() )
//			hourVal = Utils.prefixZero( hour, 2 ) + ":00";
//		else
//			hourVal = Integer.toString(hour) + ":00";
//		
//		if(!Utils.is24Hours()) {
//			if(isAM)
//				hourVal +=  UIContext.Constants.scheduleStartTimeAM();
//			else
//				hourVal +=  UIContext.Constants.scheduleStartTimePM();
//		}
//		
//		return hourVal;
//	}
//	
//	private void scheduleValidateFail() {
//		//deckPanel.showWidget(STACK_SCHEDULE);
//		deckPanel.showWidget(STACK_REPEAT);
//		this.repaint(); // fix Issue: 20238307    Title: UI MIXED AFTER LICE MSG BOX
//		
//		//layout.setActiveItem(scheduleContainer);
//		this.contentHost.showSettingsContent( this.settingsContentId );
//		this.onValidatingCompleted(false);
//		return;
//	}
	
	boolean validateAfterSchedule() {
		if (settings.Validate())
		{
			this.settings.Save();
		}
		else
		{
			deckPanel.showWidget(STACK_SETTINGS);
			//layout.setActiveItem(settingsContainer);
			SettingPresenter.getInstance().setCurrentIndex(BaseCommonSettingTab.d2dBackupSettingID, STACK_SETTINGS);
			this.contentHost.showSettingsContent( this.settingsContentId );
			this.onValidatingCompleted(false);
			return false;
		}
		
		checkVolumeAndSavingSettings();
		return true;
	}
	
	protected void checkVolumeAndSavingSettings() {
		if(((BackupDestinationSettings)destination).isVolumeSelectionChanges())
		{
			MessageBox mb = new MessageBox();
			mb.setIcon(MessageBox.WARNING);
			mb.setButtons(MessageBox.YESNO);
			mb.setTitleHtml(UIContext.Messages.messageBoxTitleWarning(UIContext.productNameD2D));
			mb.setMessage(UIContext.Constants.backupSettingsVolumeSelectionChanges());
			Utils.setMessageBoxDebugId(mb);
			mb.addCallback(new Listener<MessageBoxEvent>()
			{
				public void handleEvent(MessageBoxEvent be)
				{
					if (be.getButtonClicked().getItemId().equals(Dialog.YES))
						//Save();
						saveAllSettings();
					else
						onValidatingCompleted(false);
				}
			});
			mb.show();
		}
		else {
			saveAllSettings();
		}
	}

	protected void saveAllSettings() {
		contentHost.increaseBusyCount(UIContext.Constants.settingsMaskText());
		validateBackendSetings();
	}
	
	private boolean validateBackendSetings() {
		return this.backupSettingPresenter.validate();
//		Broker.loginService.validateBackupConfiguration(outerThis.model,
//				new BaseAsyncCallback<Long>() {
//					@Override
//					public void onSuccess(Long result) {
//						contentHost.decreaseBusyCount();
//						outerThis.onValidatingCompleted(true);
//					}
//
//					@Override
//					public void onFailure(Throwable caught) {
//
//						if (caught instanceof BusinessLogicException
//								&& ERR_REMOTE_DEST_WINSYSMSG
//										.equals(((BusinessLogicException) caught)
//												.getErrorCode())) {
//							if(outerThis.model.isBackupToRps() != null
//								 && outerThis.model.isBackupToRps()){
//								showErrorMessage(UIContext.Constants.destinationFailedRPS());
//								deckPanel.showWidget(STACK_DESTINATION);
//								contentHost.decreaseBusyCount();
//								outerThis.onValidatingCompleted(false);
//							}else{
//								checkDestDriverType(caught, false);
//							}
//						} else {
//
//							// Issue: 20231648 Title: FOCUS AFTER ENCRYPTION
//							// LIC ERR
//							// Go to destination panel for Encryption
//							// License error.
//							if (caught instanceof BusinessLogicException
//									&& "4294967310"
//											.equals(((BusinessLogicException) caught)
//													.getErrorCode())) {
//								deckPanel.showWidget(STACK_DESTINATION);
//							}
//
//							if (caught instanceof BusinessLogicException
//									&& !((BusinessLogicException) caught)
//											.getErrorCode()
//											.equals("4294967302")
//									&& !((BusinessLogicException) caught)
//											.getErrorCode()
//											.equals("4294967298")) {
//								showErrorMessage((BusinessLogicException) caught);
//								contentHost.decreaseBusyCount();
//								outerThis.onValidatingCompleted(false);
//							} else if (caught instanceof BusinessLogicException
//									&& ((BusinessLogicException) caught)
//									.getErrorCode()
//									.equals("17179869217")) {
//								showErrorMessage((BusinessLogicException) caught);
//								deckPanel.showWidget(STACK_DESTINATION);
//								contentHost.decreaseBusyCount();
//								outerThis.onValidatingCompleted(false);
//							} else {
//								contentHost.decreaseBusyCount();
//								outerThis.onValidatingCompleted(false);
//								super.onFailure(caught);
//							}
//						}
//					}
//				});
//
//		return true;
	}
	
	protected void saveAfterValidate(boolean isSaveConfig) {
		if(isSaveConfig) {
			Save();
		}else {
			validateBackendSetings();
		}
	}
	
	public void popupUserPasswordWindow(final boolean isSaveConfig) {
		final UserPasswordWindow dlg = new UserPasswordWindow(SettingPresenter.model.getDestination(), "", "");
		dlg.setModal(true);
		
		dlg.addWindowListener(new WindowListener()
		{				
			public void windowHide(WindowEvent we) {
				if (dlg.getCancelled() == false)
				{
					String username = dlg.getUsername();
					String password = dlg.getPassword();
					SettingPresenter.model.setDestUserName(username);
					SettingPresenter.model.setDestPassword(password);
					destination.getPathSelectionPanel().setUsername(username);
					destination.getPathSelectionPanel().setPassword(password);
					saveAfterValidate(isSaveConfig);
				}
				else {
					contentHost.decreaseBusyCount();
					outerThis.onValidatingCompleted(false);
				}
				
			}
		});
		dlg.show();
	}
	
	public void checkDestDriverType(Throwable caught,boolean isSave) {		
		this.backupSettingPresenter.checkDestDriverType(caught, isSave);
//		final Throwable orginialExc = caught;
//		final boolean isSaveConfig = isSave;
//		CommonServiceAsync commonService = GWT.create(CommonService.class);
//		commonService.getDestDriveType(model.getDestination(), new BaseAsyncCallback<Long>()
//	             {
//					@Override
//					public void onFailure(Throwable caught) {
//						contentHost.decreaseBusyCount();
//						super.onFailure(caught);
//					}
//					
//			    	@Override
//					public void onSuccess(Long result) {
//			    		if(result == PathSelectionPanel.REMOTE_DRIVE )
//			    		{
//			    			popupUserPasswordWindow(isSaveConfig);
//			    		}
//			    		else {
//			    			contentHost.decreaseBusyCount();
//			    			outerThis.onValidatingCompleted(false);
//			    			super.onFailure(orginialExc);
//			    		}
//					}
//		    	}
//	      	);
		
		
	}
	
	/**
	 * fix 18898048
	 * The first time should meet below criteria:
		1.Our backup configuration file does not  exist.
		2.There must be at least one type of backup job (full/incr/resync) which is scheduled as repeatable.
		3.the start time  is earlier than the time when  user save the backup settings. 

		If these conditions are met,  we prompt the user whether we should start a backup job 
		for him/her at once.  If user choose Yes, then we launch the backup job immediately. 
		Or, we just schedule the job as usual.

	 */
	private long currentTime = 0;
//	private int firstLaunchBackupType = BackupTypeModel.Full;
	public void launchFirstBackupJobifNeeded(){
		
		
		// the first condition
		if(backupSettingFileExist) 
			return; 
		//decide the needed bakup type;
		
		int firstLaunchBackupType = BackupTypeModel.Unknown;
		if(isConfigureBackupSchedule(BackupTypeModel.Full, SettingPresenter.model)){
			firstLaunchBackupType=BackupTypeModel.Full;
		}
		else if(isConfigureBackupSchedule(BackupTypeModel.Resync, SettingPresenter.model)){
			firstLaunchBackupType=BackupTypeModel.Resync;
		}
		else if(isConfigureBackupSchedule(BackupTypeModel.Incremental, SettingPresenter.model)){
			firstLaunchBackupType=BackupTypeModel.Incremental;
		}
		
		if(firstLaunchBackupType == BackupTypeModel.Unknown ) 
			return;
		
		final int finallyBackupType=firstLaunchBackupType;

		//I can not use the new Date() to get the current time because the ROOT.war can be on different machine
		//than webservice machine
		//one minute gap	
		Broker.loginService.getServerTime(new BaseAsyncCallback<Date>() {
			@Override
			public void onSuccess(Date result) {
				currentTime = result.getTime();
				//wanqi06
//				launchFirstJobWindow(firstLaunchBackupType);
				launchFirstJobWindow(finallyBackupType);
				
			}

			@Override
			public void onFailure(Throwable caught) {
				//if failed, select a earlier day, so that the first launch box will not pop up. Is it wise or foolproof?
				Date date = new Date(0);
				DateWrapper dw = new DateWrapper(date);
				dw.addDays(1);
				currentTime = dw.getTime();
				//wanqi06
//				launchFirstJobWindow(firstLaunchBackupType);
				launchFirstJobWindow(finallyBackupType);
			}
			
		});
		
		
	}
	private void launchFirstJobWindow(int firstLaunchBackupType) {
		DateWrapper currdw = new DateWrapper(currentTime);
		currdw = currdw.addMinutes(-1);
		//if start time is after the server's current enough, we should not bother to pop up the first launch box
		if(SettingPresenter.model.getBackupStartTime() > currdw.getTime()) 
			return;

		String startTime = Utils.formatTimeToServerTime(new Date(SettingPresenter.model.getBackupStartTime()));
		String messageText = Format.substitute( UIContext.Constants.firstJobDescription(),new Object[]{startTime});
		
		final MessageBox message = new MessageBox();
		message.setTitleHtml(UIContext.Constants.fisrtLaunchWindowTitle());
		message.setIcon(MessageBox.INFO);
		message.setMessage(messageText);
		message.setButtons(Dialog.OKCANCEL);
		message.setModal(true);
		final int fullBackup = firstLaunchBackupType;
		message.addCallback(new Listener<MessageBoxEvent>() {
			@Override
			public void handleEvent(MessageBoxEvent be) {
				if(be.getButtonClicked().getItemId().equals(Dialog.OK)){				
					Broker.commonService.backup(fullBackup,  UIContext.Constants.firstLaunchedJobName(),  new BaseAsyncCallback<Void>(){
						
						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
						}

						@Override
						public void onSuccess(Void result) {
							//fix bug 18925882
							message.close();
							MessageBox box = MessageBox.info(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D), UIContext.Constants.backupNowWindowSubmitSuccessful(), null);
							Utils.setMessageBoxDebugId(box);
							
						}
						
					});
				}
			}
		});
		Utils.setMessageBoxDebugId(message);
		message.show();
	}
	
	protected boolean Save()
	{
		//if (contentHost instanceof D2DCommonSettingTab || contentHost instanceof AgentCommonSettingTree ) {
		if(this.isD2D()){
			//BaseCommonSettingTab tab = (BaseCommonSettingTab) contentHost;
			//tab.getD2dSettings().setBackupSettingsModel(model);
			SettingPresenter.getInstance().getD2dSettings().setBackupSettingsModel(SettingPresenter.model);
		}
		onSavingCompleted(true);

		return true;
	}
	
	protected void onSaveSucceed() {
		//fix 18898048
		launchFirstBackupJobifNeeded();
		
		contentHost.decreaseBusyCount();
		contentHost.close();
		
		//refresh backup settings information
//							UIContext.homepagePanel.refresh(null);
		//refresh preference settings 
		if (UIContext.d2dHomepagePanel != null)
			UIContext.d2dHomepagePanel.refresh(null, IRefreshable.CS_CONFIG_CHANGED);
		else if(UIContext.hostPage != null)
		{
			UIContext.hostPage.refresh(null);
		}
		//refresh archive settings
		UIContext.d2dHomepagePanel.refreshProtectionSummary(null);
	}
	
	protected void onSaveFailed(BaseAsyncCallback<Long> callback, Throwable caught) {
		if (caught instanceof BusinessLogicException
				&& ERR_REMOTE_DEST_WINSYSMSG.equals(((BusinessLogicException) caught)
								.getErrorCode())) {
			checkDestDriverType(caught, true);
		} else {
			contentHost.decreaseBusyCount();
			outerThis.onSavingCompleted(false);
			callback.onFailure(caught);
		}
	}
	
	protected void checkBLIOnLoad(){
		checkBLIAndLimit();	
	}

	private void checkBLIAndLimit() {
		Broker.commonService.checkBLILic(new BaseLicenseAsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				UIContext.hasBLILic = false;
			}

			@Override
			public void onSuccess(Boolean result) {
				if (result != null) {
					UIContext.hasBLILic = result;
				} else {
					UIContext.hasBLILic = false;
				}
			}
		});

		Broker.commonService.getMaxRPLimit(new BaseAsyncCallback<Long>() {
			@Override
			public void onSuccess(Long result) {
				if (result != null && result > 0) {
					UIContext.maxRPLimit = result;
				}else{
					UIContext.maxRPLimit = UIContext.maxRPLimitDEFAULT;
				}
			}
		});
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		
		this.checkBLIAndLimit();
	}


	//////////////////////////////////////////////////////////////////////////
	//
	//  ADDED FOR EDGE
	//
	//////////////////////////////////////////////////////////////////////////

	private boolean isForEdge = false;
	protected int settingsContentId = -1;
	
	@Override
	public void initialize( ISettingsContentHost contentHost, boolean isForEdge )
	{
		this.contentHost = contentHost;
		this.isForEdge = isForEdge;
		
		this.doInitialization();
		
//		if (!this.isForEdge)
//			disableEditingIfUsingEdgePolicy();
		
		checkBLIAndLimit();
	}
	
	@Override
	public boolean isForEdge()
	{
		return this.isForEdge;
	}

	@Override
	public void setIsForEdge( boolean isForEdge )
	{
		this.isForEdge = isForEdge;
	}
	
	@Override
	public void setId( int settingsContentId )
	{
		this.settingsContentId = settingsContentId;
	}
	
	@Override
	public Widget getWidget()
	{
		return this;
	}
	
	private boolean isD2D = false;

	@Override
	public void loadData()
	{
		//if (contentHost instanceof D2DCommonSettingTab || contentHost instanceof AgentCommonSettingTree) {	
		if(this.isD2D()){
			//BaseCommonSettingTab commonTab = (BaseCommonSettingTab) contentHost;
//			BackupSettingsModel tmodel = commonTab.getD2dSettings()
//					.getBackupSettingsModel();
			BackupSettingsModel tmodel = SettingPresenter.getInstance().getD2dSettings().getBackupSettingsModel();
			if (tmodel.getDestination() == null
					|| tmodel.getDestination().isEmpty()) {
				BackupSettingsModel model = getDefaultModel();
				model.setAdminPassword(tmodel.getAdminPassword());
				model.setAdminUserName(tmodel.getAdminUserName());
				
				//wanqi06
				model.advanceScheduleModel = tmodel.advanceScheduleModel;
				
				model.setBackupStartTime(tmodel.getBackupStartTime());
				D2DTimeModel time = new D2DTimeModel();
				Date serverDate = Utils.localTimeToServerTime(new Date(tmodel
						.getBackupStartTime()));
				time.fromJavaDate(serverDate);
				model.startTime = time;
				model.retentionPolicy = new RetentionPolicyModel();
				model.retentionPolicy.setRetentionCount(model
						.getRetentionCount());
				model.retentionPolicy.setUseTimeRange(false);
				SettingPresenter.getInstance().getD2dSettings().setBackupSettingsModel(model);
			} else {
				SettingPresenter.model = tmodel;
				backupSettingFileExist = true;
			}
			BackupSettingUtil.getInstance().setBackupSetting(this);
			RefreshData();
		}
		onLoadingCompleted(true);
	}

	@Override
	public void loadDefaultData()
	{
		loadDefaultSettings();
	}

	@Override
	public void saveData()
	{
		//SaveSettings();
		Save();
	}
	
	@Override
	public void validate()
	{
		validateUISettings();
	}
	
	@Override
	public void setDefaultEmail(IEmailConfigModel iEmailConfigModel)
	{
		
	}
	
	
	protected void onSavingCompleted( boolean isSuccessful )
	{
		GWT.log("The backupsettingcontent save compelte:"+isSuccessful);
		SettingPresenter.getInstance().onAsyncOperationCompleted(
				ISettingsContentHost.Operations.SaveData,
				isSuccessful ? ISettingsContentHost.OperationResults.Succeeded :
					ISettingsContentHost.OperationResults.Failed,
				this.settingsContentId );
	}

	protected boolean isShowForVSphere() {
		return false;
	}

	public void onValidatingCompleted( boolean isSuccessful )
	{
		GWT.log("The backupsettingcontent validate compelte:"+isSuccessful);
		SettingPresenter.getInstance().onAsyncOperationCompleted(
				ISettingsContentHost.Operations.Validate,
				isSuccessful ? ISettingsContentHost.OperationResults.Succeeded :
					ISettingsContentHost.OperationResults.Failed,
				this.settingsContentId );
	}
	
	protected void onLoadingCompleted( boolean isSuccessful )
	{
		if(isSuccessful) {
			BackupSettingUtil.getInstance().setBackupSetting(this);
		}
		GWT.log("The backupsettingcontent load compelte:"+isSuccessful);
		SettingPresenter.getInstance().onAsyncOperationCompleted(
				ISettingsContentHost.Operations.LoadData,
				isSuccessful ? ISettingsContentHost.OperationResults.Succeeded :
					ISettingsContentHost.OperationResults.Failed,
				this.settingsContentId );
	}
	
	public RepeatAdvancedScheduleSettings getAdScheduleSettings() {
		return this.schedule;
	}
	
	public BackupSettings getSettings() {
		return settings;
	}

	public void setSettings(BackupSettings settings) {
		this.settings = settings;
	}
	protected void addEmailAlertToDeckPanel() {
	}
	
	protected void downEmailAlertButtonAndLabel() {
		
	}

	protected void addEmailAlertButtonPanel() {
	}
	
	protected boolean validateEmailAlert() {
		return true;
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
	
	//wanqi06
	private boolean isConfigureBackupSchedule(int backupType, BackupSettingsModel model) {
		if (model.getBackupDataFormat() > 0) {
			if (model.advanceScheduleModel != null && model.advanceScheduleModel.daylyScheduleDetailItemModel != null) {
				for (DailyScheduleDetailItemModel dailyModel : model.advanceScheduleModel.daylyScheduleDetailItemModel) {
					if (dailyModel.scheduleDetailItemModels != null){
						for (ScheduleDetailItemModel detailModel : dailyModel.scheduleDetailItemModels) {
							if (detailModel.getJobType() == backupType)
								return true;
						}
					}
				}
			}
		} else { // legacy schedule
			if (backupType == BackupTypeModel.Full
					&& model.fullSchedule != null
					&& model.fullSchedule.isEnabled()) {
				return true;
			} else if (backupType == BackupTypeModel.Incremental
					&& model.incrementalSchedule != null
					&& model.incrementalSchedule.isEnabled()) {
				return true;
			} else if (backupType == BackupTypeModel.Resync
					&& model.resyncSchedule != null
					&& model.resyncSchedule.isEnabled()) {
				return true;
			}
		}

		return false;
	}

	public boolean isD2D() {
		return isD2D;
	}

	public void setD2D(boolean isD2D) {
		this.isD2D = isD2D;
	}

	
}
