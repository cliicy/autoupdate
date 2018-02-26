package com.ca.arcflash.ui.client.backup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

public class Settings4Backup extends BackupSettingsContent implements ISettingsContent {

	private BackupSettings advancedSettings;
	private AdvancedSettings prepostSettings;
	private ScheduleSettings simpleSchedule;
	private BackupScheduleTabItem advScheduleItem;
	private BaseDestinationSettings destination;
	private ISettingsContentHost contentHost;
	private boolean backupSettingFileExist = false;

	public ScheduleSettings getSimpleSchedule(){
		return simpleSchedule;
	}
	
	public BackupScheduleTabItem getAdvScheduleItem(){
		return advScheduleItem;
	}
	
	public static final int DEFAULT_RETENTION_COUNT = 31;
	public ExtCardPanel deckPanel;

	public DestinationCapacityModel destModel;

	public static final int STACK_DESTINATION = 0;
	public static final int STACK_SCHEDULE_SIMPLE = 1;
	public static final int STACK_SCHEDULE_Adv = 2;
	public static final int STACK_SETTINGS = 3;
	public static final int STACK_PrePost = 4;

	public static final long AF_ERR_DEST_SYSVOL = 3758096417l;
	public static final long AF_ERR_DEST_BOOTVOL = 3758096418l;
	public static final String ERR_REMOTE_DEST_WINSYSMSG = "17179869199";

	private String refsVolList;

	private Boolean isAllVolumeIsRefsOrDedup = false;

	protected SettingsGroupType settingsGroupType;

	public List<String> itemsToDisplay = new ArrayList<String>();

	public Boolean getIsAllVolumeIsRefsOrDedup() {
		return isAllVolumeIsRefsOrDedup;
	}

	public BaseDestinationSettings getDestination() {
		return destination;
	}

	public void setDestination(BaseDestinationSettings destination) {
		this.destination = destination;
	}

	public void setIsAllVolumeIsRefsOrDedup(Boolean isAllVolumeIsRefsOrDedup) {
		this.isAllVolumeIsRefsOrDedup = isAllVolumeIsRefsOrDedup;
	}

	private BackupSettingPresenter backupSettingPresenter;

	public Settings4Backup() {
		outerThis = this;
		backupSettingPresenter = new BackupSettingPresenter(this);
	}

	public void focusPanel(int index) {
		deckPanel.showWidget(index);
	}

	public String getRefsVolList() {
		return refsVolList;
	}

	public void setRefsVolList(String refsVolList) {
		this.refsVolList = refsVolList;
	}

	protected void doInitialization() {
		renderPanel();
	}

	private void renderPanel() {

		deckPanel = new ExtCardPanel();
		deckPanel.setStyleName("backupSettingCenter");

		destination = new D2DDestinationSettings(this);
		destination.setContentHost(contentHost);
		wrap(destination.Render());
		
		itemsToDisplay.add(UIContext.Constants.backupSettingsDestination());

		simpleSchedule = new ScheduleSettings(this);		
		wrap(simpleSchedule.Render());
		

		advScheduleItem = new BackupScheduleTabItem();
		wrap(advScheduleItem);

		itemsToDisplay.add(UIContext.Constants.backupSettingsSchedule());

		advancedSettings = new BackupSettings(this);
		wrap(advancedSettings.Render());

		itemsToDisplay.add(UIContext.Constants.backupSettingsSettings());

		prepostSettings = new AdvancedSettings(this, settingsGroupType);
		wrap(prepostSettings.Render());
		
		itemsToDisplay.add(UIContext.Constants.backupSettingsPrePost());
//		this.add(deckPanel, new RowData(1, 1));
		deckPanel.showWidget(STACK_DESTINATION);
	}

	private void wrap(LayoutContainer cont) {
		LayoutContainer wrap = new LayoutContainer();
		wrap.add(cont);
		wrap.setStyleAttribute("padding", "10px");
		deckPanel.add(wrap);
	}

	public void updateNotification() {
		if (advancedSettings != null)
			advancedSettings.updateNotificationSet();
	}

	// ////////////////////////////////////////////////////////////////////////
	/**
	 * This method can be only used for disable edit. Don't use method to enable
	 * edit. Enable this edit using this method will make UI value wrong.
	 */
	public void enableEditing(boolean isEnabled) {
		this.destination.setEditable(isEnabled);
		this.advancedSettings.setEditable(isEnabled);
		this.prepostSettings.setEditable(isEnabled);
		this.simpleSchedule.setEditable(isEnabled);
		this.advScheduleItem.setEditable(isEnabled);
	}

	public void RefreshData() {
		advancedSettings.RefreshData(SettingPresenter.model, backupSettingFileExist);
		prepostSettings.RefreshData(SettingPresenter.model);
		destination.RefreshData(SettingPresenter.model, backupSettingFileExist);
		simpleSchedule.RefreshData(SettingPresenter.model);
		advScheduleItem.RefreshData(SettingPresenter.model, backupSettingFileExist, !destination.backupToRPS());
	}

	public void LoadSettings() {
		BaseAsyncCallback<BackupSettingsModel> callback = new BaseAsyncCallback<BackupSettingsModel>() {

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);

				onLoadingCompleted(false);
			}

			@Override
			public void onSuccess(BackupSettingsModel result) {
				if (result != null) {
					backupSettingFileExist = true;
					SettingPresenter.model = result;
					RefreshData();

					onLoadingCompleted(true);
				} else {
					loadDefaultSettings();
				}
			}
		};
		fetchDataFromServer(callback);
	}

	// VSPHERE Settings Use the two API: over write the two API in the
	// VSphereBackupSettingContent
	protected void fetchDataFromServer(BaseAsyncCallback<BackupSettingsModel> callback) {

	}

	protected void saveBackupConfiguration() throws Exception {

	}

	//

	protected void loadDefaultSettings() {
		Broker.loginService.getAdminAccount(new BaseAsyncCallback<AccountModel>() {
			@Override
			public void onFailure(Throwable caught) {
				// the administrator account may does not exist.
				setServerTimeAndRepaint(null);
			}

			@Override
			public void onSuccess(AccountModel result) {
				setServerTimeAndRepaint(result);
			}

			private void setServerTimeAndRepaint(final AccountModel accountModel) {
				Broker.loginService.getServerTime(new BaseAsyncCallback<Date>() {
					@Override
					public void onSuccess(Date result) {
						SettingPresenter.model = getDefaultModel();
						long serverTimeInMilliseconds = result.getTime();
						// set backup start time plus 5 minutes
						serverTimeInMilliseconds += 5 * 60 * 1000;
						SettingPresenter.model.setBackupStartTime(serverTimeInMilliseconds);
						if (accountModel != null) {
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

		// fix 18898048
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
		model.setCompressionLevel(1); // Standard
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

	// protected void validateSchedule(final D2DTimeModel time) {
	//
	// Broker.commonService.validateBackupStartTime(time.getYear(),
	// time.getMonth(),
	// time.getDay(), time.getHourOfDay(), time.getMinute(), new
	// BaseAsyncCallback<Long>(){
	// @Override
	// public void onFailure(Throwable caught) {
	// if(caught instanceof BusinessLogicException) {
	// final BusinessLogicException ble = (BusinessLogicException)caught;
	// if(ble.getErrorCode() != null&& ble.getErrorCode().equals("-1")){
	// MessageBox msg = new MessageBox();
	// msg.setIcon(MessageBox.ERROR);
	// msg.setTitle(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
	// String[] timearr = formatStartTimeErrorMsg(time.getHourOfDay());
	// msg.setMessage(UIContext.Messages.settingDSTStartTime(timearr[0],
	// timearr[0] + "-" + timearr[1]));
	// msg.setModal(true);
	// msg.show();
	// scheduleValidateFail();
	// }else if(ble.getErrorCode() != null
	// && ble.getErrorCode().equals("-2")) {
	// MessageBox msg = new MessageBox();
	// msg.setIcon(MessageBox.WARNING);
	// msg.setButtons(MessageBox.YESNO);
	// msg.setTitle(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
	// String[] timearr = formatStartTimeErrorMsg(time.getHourOfDay());
	// msg.setMessage(UIContext.Messages.settingDSTEndTime(
	// timearr[1], timearr[0] + "-" + timearr[1],
	// UIContext.productNameD2D));
	// msg.setModal(true);
	// msg.addCallback(new Listener<MessageBoxEvent>(){
	//
	// @Override
	// public void handleEvent(MessageBoxEvent be) {
	// if(be.getButtonClicked().getItemId().equals(Dialog.YES)) {
	// schedule.Save(Long.parseLong(ble.getDisplayMessage()));
	// validateAfterSchedule();
	// }else {
	// scheduleValidateFail();
	// }
	// }
	// });
	// msg.show();
	// }
	// }else{
	// schedule.Save(-1);
	// validateAfterSchedule();
	// }
	// }
	//
	// @Override
	// public void onSuccess(Long result) {
	// schedule.Save(result);
	// validateAfterSchedule();
	// }
	// });
	// }

	void validateUISettings() {
		// model = new BackupSettingsModel();

		if (prepostSettings.Validate()) {
			this.prepostSettings.Save();
		} else {
			deckPanel.showWidget(STACK_PrePost);
			SettingPresenter.getInstance().setCurrentIndex(BaseCommonSettingTab.d2dBackupSettingID, STACK_PrePost);
			this.repaint(); // fix Issue: 20238307 Title: UI MIXED AFTER LICE
							// MSG BOX

			// layout.setActiveItem(advancedContainer);
			this.contentHost.showSettingsContent(this.settingsContentId);
			this.onValidatingCompleted(false);
			return;
		}

		if (destination.Validate()) {
			this.destination.Save();
		} else {
			deckPanel.showWidget(STACK_DESTINATION);
			this.repaint(); // fix Issue: 20238307 Title: UI MIXED AFTER LICE
							// MSG BOX

			SettingPresenter.getInstance().setCurrentIndex(BaseCommonSettingTab.d2dBackupSettingID, STACK_DESTINATION);
			// layout.setActiveItem(destinationContainer);
			this.contentHost.showSettingsContent(this.settingsContentId);
			this.onValidatingCompleted(false);
			return;
		}

		if (SettingPresenter.getInstance().isAdvSchedule()) {

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
			if (simpleSchedule.Validate()) {
				simpleSchedule.Save(UIContext.serverVersionInfo.getTimeZoneOffset());
			}

		}

		validateAfterSchedule();

	}

	protected boolean validateAfterSchedule() {
		if (advancedSettings.Validate()) {
			this.advancedSettings.Save();
		} else {
			deckPanel.showWidget(STACK_SETTINGS);
			// layout.setActiveItem(settingsContainer);
			SettingPresenter.getInstance().setCurrentIndex(BaseCommonSettingTab.d2dBackupSettingID, STACK_SETTINGS);
			this.contentHost.showSettingsContent(this.settingsContentId);
			this.onValidatingCompleted(false);
			return false;
		}

		checkVolumeAndSavingSettings();
		return true;
	}

	protected void checkVolumeAndSavingSettings() {
		if (((BackupDestinationSettings) destination).isVolumeSelectionChanges()) {
			MessageBox mb = new MessageBox();
			mb.setIcon(MessageBox.WARNING);
			mb.setButtons(MessageBox.YESNO);
			mb.setTitleHtml(UIContext.Messages.messageBoxTitleWarning(UIContext.productNameD2D));
			mb.setMessage(UIContext.Constants.backupSettingsVolumeSelectionChanges());
			Utils.setMessageBoxDebugId(mb);
			mb.addCallback(new Listener<MessageBoxEvent>() {
				public void handleEvent(MessageBoxEvent be) {
					if (be.getButtonClicked().getItemId().equals(Dialog.YES))
						saveAllSettings();
					else
						onValidatingCompleted(false);
				}
			});
			mb.show();
		} else {
			saveAllSettings();
		}
	}

	protected void saveAllSettings() {
		contentHost.increaseBusyCount(UIContext.Constants.settingsMaskText());
		validateBackendSetings();
	}

	private boolean validateBackendSetings() {
		return this.backupSettingPresenter.validate();
	}

	protected void saveAfterValidate(boolean isSaveConfig) {
		if (isSaveConfig) {
			Save();
		} else {
			validateBackendSetings();
		}
	}

	public void popupUserPasswordWindow(final boolean isSaveConfig) {
		final UserPasswordWindow dlg = new UserPasswordWindow(SettingPresenter.model.getDestination(), "", "");
		dlg.setModal(true);

		dlg.addWindowListener(new WindowListener() {
			public void windowHide(WindowEvent we) {
				if (dlg.getCancelled() == false) {
					String username = dlg.getUsername();
					String password = dlg.getPassword();
					SettingPresenter.model.setDestUserName(username);
					SettingPresenter.model.setDestPassword(password);
					destination.getPathSelectionPanel().setUsername(username);
					destination.getPathSelectionPanel().setPassword(password);
					saveAfterValidate(isSaveConfig);
				} else {
					contentHost.decreaseBusyCount();
					outerThis.onValidatingCompleted(false);
				}

			}
		});
		dlg.show();
	}

	public void checkDestDriverType(Throwable caught, boolean isSave) {
		this.backupSettingPresenter.checkDestDriverType(caught, isSave);
	}

	/**
	 * fix 18898048 The first time should meet below criteria: 1.Our backup
	 * configuration file does not exist. 2.There must be at least one type of
	 * backup job (full/incr/resync) which is scheduled as repeatable. 3.the
	 * start time is earlier than the time when user save the backup settings.
	 * 
	 * If these conditions are met, we prompt the user whether we should start a
	 * backup job for him/her at once. If user choose Yes, then we launch the
	 * backup job immediately. Or, we just schedule the job as usual.
	 */
	private long currentTime = 0;

	// private int firstLaunchBackupType = BackupTypeModel.Full;
	public void launchFirstBackupJobifNeeded() {

		// the first condition
		if (backupSettingFileExist)
			return;
		// decide the needed bakup type;

		int firstLaunchBackupType = BackupTypeModel.Unknown;
		if (isConfigureBackupSchedule(BackupTypeModel.Full, SettingPresenter.model)) {
			firstLaunchBackupType = BackupTypeModel.Full;
		} else if (isConfigureBackupSchedule(BackupTypeModel.Resync, SettingPresenter.model)) {
			firstLaunchBackupType = BackupTypeModel.Resync;
		} else if (isConfigureBackupSchedule(BackupTypeModel.Incremental, SettingPresenter.model)) {
			firstLaunchBackupType = BackupTypeModel.Incremental;
		}

		if (firstLaunchBackupType == BackupTypeModel.Unknown)
			return;

		final int finallyBackupType = firstLaunchBackupType;

		// I can not use the new Date() to get the current time because the
		// ROOT.war can be on different machine
		// than webservice machine
		// one minute gap
		Broker.loginService.getServerTime(new BaseAsyncCallback<Date>() {
			@Override
			public void onSuccess(Date result) {
				currentTime = result.getTime();
				// wanqi06
				// launchFirstJobWindow(firstLaunchBackupType);
				launchFirstJobWindow(finallyBackupType);

			}

			@Override
			public void onFailure(Throwable caught) {
				// if failed, select a earlier day, so that the first launch box
				// will not pop up. Is it wise or foolproof?
				Date date = new Date(0);
				DateWrapper dw = new DateWrapper(date);
				dw.addDays(1);
				currentTime = dw.getTime();
				// wanqi06
				// launchFirstJobWindow(firstLaunchBackupType);
				launchFirstJobWindow(finallyBackupType);
			}

		});

	}

	private void launchFirstJobWindow(int firstLaunchBackupType) {
		DateWrapper currdw = new DateWrapper(currentTime);
		currdw = currdw.addMinutes(-1);
		// if start time is after the server's current enough, we should not
		// bother to pop up the first launch box
		if (SettingPresenter.model.getBackupStartTime() > currdw.getTime())
			return;

		String startTime = Utils.formatTimeToServerTime(new Date(SettingPresenter.model.getBackupStartTime()));
		String messageText = Format.substitute(UIContext.Constants.firstJobDescription(), new Object[] { startTime });

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
				if (be.getButtonClicked().getItemId().equals(Dialog.OK)) {
					Broker.commonService.backup(fullBackup, UIContext.Constants.firstLaunchedJobName(), new BaseAsyncCallback<Void>() {

						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
						}

						@Override
						public void onSuccess(Void result) {
							// fix bug 18925882
							message.close();
							MessageBox box = MessageBox.info(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D),
									UIContext.Constants.backupNowWindowSubmitSuccessful(), null);
							Utils.setMessageBoxDebugId(box);

						}

					});
				}
			}
		});
		Utils.setMessageBoxDebugId(message);
		message.show();
	}

	protected boolean Save() {
		if (this.isD2D()) {
			SettingPresenter.getInstance().getD2dSettings().setBackupSettingsModel(SettingPresenter.model);
		}
		onSavingCompleted(true);

		return true;
	}

	public ISettingsContentHost getContentHost() {
		return contentHost;
	}

	protected void onSaveSucceed() {
		// fix 18898048
		launchFirstBackupJobifNeeded();

		contentHost.decreaseBusyCount();
		contentHost.close();

		if (UIContext.d2dHomepagePanel != null)
			UIContext.d2dHomepagePanel.refresh(null, IRefreshable.CS_CONFIG_CHANGED);
		else if (UIContext.hostPage != null) {
			UIContext.hostPage.refresh(null);
		}
		// refresh archive settings
		UIContext.d2dHomepagePanel.refreshProtectionSummary(null);
	}

	protected void onSaveFailed(BaseAsyncCallback<Long> callback, Throwable caught) {
		if (caught instanceof BusinessLogicException && ERR_REMOTE_DEST_WINSYSMSG.equals(((BusinessLogicException) caught).getErrorCode())) {
			checkDestDriverType(caught, true);
		} else {
			contentHost.decreaseBusyCount();
			outerThis.onSavingCompleted(false);
			callback.onFailure(caught);
		}
	}

	protected void checkBLIOnLoad() {
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
				} else {
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

	// ////////////////////////////////////////////////////////////////////////
	//
	// ADDED FOR EDGE
	//
	// ////////////////////////////////////////////////////////////////////////

	private boolean isForEdge = false;
	protected int settingsContentId = -1;

	@Override
	public void initialize(ISettingsContentHost contentHost, boolean isForEdge) {
		this.contentHost = contentHost;
		this.isForEdge = isForEdge;

		this.doInitialization();

		// if (!this.isForEdge)
		// disableEditingIfUsingEdgePolicy();

		checkBLIAndLimit();
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

	private boolean isD2D = false;

	@Override
	public void loadData() {
		// if (contentHost instanceof D2DCommonSettingTab || contentHost
		// instanceof AgentCommonSettingTree) {
		if (this.isD2D()) {
			// BaseCommonSettingTab commonTab = (BaseCommonSettingTab)
			// contentHost;
			// BackupSettingsModel tmodel = commonTab.getD2dSettings()
			// .getBackupSettingsModel();
			BackupSettingsModel tmodel = SettingPresenter.getInstance().getD2dSettings().getBackupSettingsModel();
			if (tmodel.getDestination() == null || tmodel.getDestination().isEmpty()) {
				BackupSettingsModel model = getDefaultModel();
				model.setAdminPassword(tmodel.getAdminPassword());
				model.setAdminUserName(tmodel.getAdminUserName());
				model.advanceScheduleModel = tmodel.advanceScheduleModel;
				model.setBackupStartTime(tmodel.getBackupStartTime());
				D2DTimeModel time = new D2DTimeModel();
				Date serverDate = Utils.localTimeToServerTime(new Date(tmodel.getBackupStartTime()));
				time.fromJavaDate(serverDate);
				model.startTime = time;
				model.retentionPolicy = new RetentionPolicyModel();
				model.retentionPolicy.setRetentionCount(model.getRetentionCount());
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
	public void loadDefaultData() {
		loadDefaultSettings();
	}

	@Override
	public void saveData() {
		// SaveSettings();
		Save();
	}

	@Override
	public void validate() {
		validateUISettings();
	}

	@Override
	public void setDefaultEmail(IEmailConfigModel iEmailConfigModel) {

	}

	protected void onSavingCompleted(boolean isSuccessful) {
		GWT.log("The backupsettingcontent save compelte:" + isSuccessful);
		SettingPresenter.getInstance().onAsyncOperationCompleted(ISettingsContentHost.Operations.SaveData,
				isSuccessful ? ISettingsContentHost.OperationResults.Succeeded : ISettingsContentHost.OperationResults.Failed, this.settingsContentId);
	}

	protected boolean isShowForVSphere() {
		return false;
	}

	public void onValidatingCompleted(boolean isSuccessful) {
		GWT.log("The backupsettingcontent validate compelte:" + isSuccessful);
		SettingPresenter.getInstance().onAsyncOperationCompleted(ISettingsContentHost.Operations.Validate,
				isSuccessful ? ISettingsContentHost.OperationResults.Succeeded : ISettingsContentHost.OperationResults.Failed, this.settingsContentId);
	}

	protected void onLoadingCompleted(boolean isSuccessful) {
		if (isSuccessful) {
			BackupSettingUtil.getInstance().setBackupSetting(this);
		}
		GWT.log("The backupsettingcontent load compelte:" + isSuccessful);
		SettingPresenter.getInstance().onAsyncOperationCompleted(ISettingsContentHost.Operations.LoadData,
				isSuccessful ? ISettingsContentHost.OperationResults.Succeeded : ISettingsContentHost.OperationResults.Failed, this.settingsContentId);
	}

	public BackupSettings getSettings() {
		return advancedSettings;
	}

	public void setSettings(BackupSettings settings) {
		this.advancedSettings = settings;
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
		return false;
	}

	@Override
	public void setisForLiteIT(boolean isForLiteIT) {

	}

	@Override
	public List<SettingsTab> getTabList() {
		return null;
	}

	@Override
	public void switchTab(String tabId) {

	}

	// wanqi06
	private boolean isConfigureBackupSchedule(int backupType, BackupSettingsModel model) {
		if (model.getBackupDataFormat() > 0) {
			if (model.advanceScheduleModel != null && model.advanceScheduleModel.daylyScheduleDetailItemModel != null) {
				for (DailyScheduleDetailItemModel dailyModel : model.advanceScheduleModel.daylyScheduleDetailItemModel) {
					if (dailyModel.scheduleDetailItemModels != null) {
						for (ScheduleDetailItemModel detailModel : dailyModel.scheduleDetailItemModels) {
							if (detailModel.getJobType() == backupType)
								return true;
						}
					}
				}
			}
		} else { // legacy schedule
			if (backupType == BackupTypeModel.Full && model.fullSchedule != null && model.fullSchedule.isEnabled()) {
				return true;
			} else if (backupType == BackupTypeModel.Incremental && model.incrementalSchedule != null && model.incrementalSchedule.isEnabled()) {
				return true;
			} else if (backupType == BackupTypeModel.Resync && model.resyncSchedule != null && model.resyncSchedule.isEnabled()) {
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
