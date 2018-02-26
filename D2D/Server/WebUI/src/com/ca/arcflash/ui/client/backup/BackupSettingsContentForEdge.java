package com.ca.arcflash.ui.client.backup;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.D2DCommonSettingTab;
import com.ca.arcflash.ui.client.common.ISettingsContentHost;
import com.ca.arcflash.ui.client.common.d2d.presenter.SettingPresenter;
import com.ca.arcflash.ui.client.model.D2DTimeModel;
import com.ca.arcflash.ui.client.service.Broker;
import com.extjs.gxt.ui.client.widget.LayoutContainer;

public class BackupSettingsContentForEdge extends BackupSettingsContent {
	
	protected void validateSchedule(final D2DTimeModel time) {
		Broker.commonService.getServerTimezoneOffset(time.getYear(), time.getMonth(),
				time.getDay(), time.getHourOfDay(), time.getMinute(),
				new BaseAsyncCallback<Long>() {
					@Override
					public void onFailure(Throwable caught) {
						schedule.Save(-1);
						validateAfterSchedule();
					}

					@Override
					public void onSuccess(Long result) {
						schedule.Save(result);
						validateAfterSchedule();
					}
				});
	}	

	@Override
	protected void checkBLIOnLoad() {
		UIContext.hasBLILic = true;
		UIContext.maxRPLimit = UIContext.maxRPLimitDEFAULT;
	}



	@Override
	protected void saveAllSettings() {
		onValidatingCompleted(true);
	}
	
	protected boolean Save()
	{	
		contentHost.increaseBusyCount(UIContext.Constants.settingsMaskText());
		return this.SaveForEdge();
	}
	
	@Override
	protected void onSaveSucceed() {
		contentHost.decreaseBusyCount();
		SettingPresenter.getInstance().onAsyncOperationCompleted(
			ISettingsContentHost.Operations.SaveData,
			ISettingsContentHost.OperationResults.Succeeded,
			outerThis.settingsContentId );
	}
	
	@Override
	public void onSaveFailed(BaseAsyncCallback<Long> callback, Throwable caught) {
		contentHost.decreaseBusyCount();
		SettingPresenter.getInstance().onAsyncOperationCompleted(
			ISettingsContentHost.Operations.SaveData,
			ISettingsContentHost.OperationResults.Failed,
			outerThis.settingsContentId );
	}
	
	protected boolean SaveForEdge()
	{
		if (!this.getDestination().checkShareFolder())
		{
			deckPanel.showWidget(STACK_DESTINATION);
			contentHost.decreaseBusyCount();
			onSavingCompleted(false);
			return false;
		}
		
		if(getDestination().backupToRPS()){
			//validate RPS policy
			validateRpsSettings();
		}else {
			validateRemoteFolder();
		}
		
		return true;
	}
	
	private void validateRpsSettings() {
		Broker.loginService.validateRpsDestSettings(SettingPresenter.model, new BaseAsyncCallback<Long>(){

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				deckPanel.showWidget(STACK_DESTINATION);
				contentHost.decreaseBusyCount();
				onSavingCompleted(false);
			}

			@Override
			public void onSuccess(Long result) {
				saveComplete();
			}
		});
	}
	
	private void validateRemoteFolder() {
		this.getDestination().validateRemotePath(new BaseAsyncCallback<Boolean>(false) {
			
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				deckPanel.showWidget(STACK_DESTINATION);
				contentHost.decreaseBusyCount();
				onSavingCompleted(false);
			}
			
			@Override
			public void onSuccess(Boolean result) {
				if (!result) {
					deckPanel.showWidget(STACK_DESTINATION);
					contentHost.decreaseBusyCount();
					onSavingCompleted(false);
					return;
				}
				saveComplete();
			}
			
		});
	}
	
	protected void createDestinationSettings() {
		setDestination(new D2DDestinationSettingsForEdge(this));
		getDestination().setContentHost(contentHost);
		destinationContainer = new LayoutContainer();
		destinationContainer.add(getDestination().Render());
	}
	
	private void saveComplete(){
	//	if(contentHost instanceof D2DCommonSettingTab) {
		if(this.isD2D()){
//			D2DCommonSettingTab tab = (D2DCommonSettingTab)contentHost;
//			tab.getD2dSettings().setBackupSettingsModel(model);
			SettingPresenter.getInstance().getD2dSettings().setBackupSettingsModel(SettingPresenter.model);
			contentHost.decreaseBusyCount();
			onSavingCompleted(true);
		}
	}
}
