package com.ca.arcflash.ui.client.vsphere.homepage;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.ScheduleSummaryWindow;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.VMBackupSettingModel;
import com.google.gwt.core.client.GWT;

public class VSphereScheduleSummaryWindow extends ScheduleSummaryWindow {

	public VSphereScheduleSummaryWindow(){		
		this.showWidget(getAdvSchedulePanel());
	}
	@Override
	protected void onLoad() {
		final LoginServiceAsync service = GWT.create(LoginService.class);
		service.getVMBackupConfiguration(UIContext.backupVM, new BaseAsyncCallback<VMBackupSettingModel>() {
			public void onFailure(Throwable caught) {			
				super.onFailure(caught);
			}
			@Override
			public void onSuccess(VMBackupSettingModel result) {			
				getAdvSchedulePanel().RefreshData(result);
			}
		});
	}

}
