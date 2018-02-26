package com.ca.arcflash.ui.client.common.d2d.presenter;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.BackupSettingsContent;
import com.ca.arcflash.ui.client.backup.ExtUtil;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.BaseCommonSettingTab;
import com.ca.arcflash.ui.client.common.IRefreshable;
import com.ca.arcflash.ui.client.common.ISettingsContentHost;
import com.ca.arcflash.ui.client.common.SettingsTypesForUI;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.ca.arcflash.ui.client.model.D2DSettingModel;
import com.ca.arcflash.ui.client.service.Broker;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.BaseObservable;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SettingPresenter {

	public static int currentRootSelectionIndex = BaseCommonSettingTab.d2dBackupSettingID;
	public static int currentChildSelectionIndex = 0; //BackupSettingsContent.STACK_DESTINATION
	
	private static SettingPresenter settingPresenter = new SettingPresenter();
	public static BackupSettingsModel model;
	public final static AppEvent backupDataFormatEvent = new AppEvent(new EventType());

	public final static AppEvent ValidateEvent = new AppEvent(new EventType());
	
	public ISettingsContentHost getContentHost() {
		return commonSettings.getContentHost();
	}

	private BaseCommonSettingTab commonSettings;

	public BaseCommonSettingTab getCommonSettings() {
		return commonSettings;
	}

	public void setCommonSettings(BaseCommonSettingTab commonSettings) {
		this.commonSettings = commonSettings;
	}

	private SettingPresenter() {
		ExtUtil.exportStaticMethod();
	}

	public static SettingPresenter getInstance() {
		return settingPresenter;
	}
	
	public D2DSettingModel getD2dSettings(){
		return commonSettings.getD2dSettings();
	}
	
	public void onAsyncOperationCompleted(int operation, int result,
			int settingsContentId){
		commonSettings.onAsyncOperationCompleted(operation, result, settingsContentId);
	}

	public void loadSetting() {
		getContentHost().increaseBusyCount(
				UIContext.Constants.settingsLoadingConfigMaskText());

		Broker.loginService
				.getD2DConfiguration(new BaseAsyncCallback<D2DSettingModel>() {
					public void onFailure(Throwable caught) {
						getContentHost().decreaseBusyCount();
						commonSettings.onLoadingCompleted(false);
						super.onFailure(caught);

					}

					@Override
					public void onSuccess(D2DSettingModel result) {
						commonSettings.setD2dSettings(result);
						Broker.loginService.isUsingEdgePolicySettings(
								SettingsTypesForUI.BackupSettings,
								new AsyncCallback<Boolean>() {
									@Override
									public void onFailure(Throwable caught) {
										getContentHost().decreaseBusyCount();
										commonSettings.initD2DSettings();
									}

									@Override
									public void onSuccess(Boolean result) {
										getContentHost().decreaseBusyCount();
										commonSettings
												.setD2dUsingEdgePolicy(result);
										commonSettings.initD2DSettings();
									}
								});

					}
				});
	}

	public void saveSetting(final BackupSettingsContent backupSetting) {
		getContentHost().increaseBusyCount(
				UIContext.Constants.settingsMaskText());
		// final BackupSettingsContent backupSetting =
		// getBackupSettingContent();

		Broker.loginService.saveD2DConfiguration(
				commonSettings.getD2dSettings(), new BaseAsyncCallback<Long>() {
					@Override
					public void onFailure(Throwable caught) {
						if (commonSettings.isForEdge()) {
							getContentHost().decreaseBusyCount();
							commonSettings.onSavingCompleted(false);
						} else {
							if (caught instanceof BusinessLogicException
									&& backupSetting.ERR_REMOTE_DEST_WINSYSMSG
											.equals(((BusinessLogicException) caught)
													.getErrorCode())) {
								backupSetting.checkDestDriverType(caught, true);
							} else {
								getContentHost().decreaseBusyCount();
								super.onFailure(caught);
								commonSettings.onSavingCompleted(false);
							}
						}
					}

					@Override
					public void onSuccess(Long result) {
						getContentHost().decreaseBusyCount();
						if (commonSettings.isForEdge()) {
							commonSettings.onSavingCompleted(true);
						} else {
							backupSetting.launchFirstBackupJobifNeeded();
							getContentHost().close();
							// refresh preference settings
							if (UIContext.d2dHomepagePanel != null)
								UIContext.d2dHomepagePanel.refresh(null,
										IRefreshable.CS_CONFIG_CHANGED);
							else if (UIContext.hostPage != null) {
								UIContext.hostPage.refresh(null);
							}
							// refresh archive settings
							UIContext.d2dHomepagePanel
									.refreshProtectionSummary(null);
						}
					}
				});
	}
	
	
	private BaseObservable observable = new BaseObservable();

	public void addListener(Listener<? extends BaseEvent> listener) {
		observable.addListener(backupDataFormatEvent.getType(), listener);
	}
	
	public void addValidateListener(Listener<? extends BaseEvent> listener) {
		observable.addListener(ValidateEvent.getType(), listener);
	}	

	public boolean fireEvent(AppEvent event) {
		return observable.fireEvent(event.getType(), event);
	}

	private boolean isAdvSchedule = true;

	public boolean isAdvSchedule() {
		return isAdvSchedule;
	}

	public void setAdvSchedule(boolean isAdvSchedule) {
		this.isAdvSchedule = isAdvSchedule;
	}

	public boolean isBackupDataFormatNew(AppEvent be) {
		Object obj = be.getData("format");
		if( obj!= null && obj instanceof Integer){
				if((Integer)obj == 1){
					return true;
				}
		}
		return false;
	}

	public void setCurrentIndex(int root, int child) {
		currentRootSelectionIndex = root;
		currentChildSelectionIndex = child;	
		
		observable.fireEvent(ValidateEvent.getType(), ValidateEvent);
	}
	 
	
}
