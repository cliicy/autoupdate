package com.ca.arcflash.ui.client.common.d2d.presenter;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.ISettingsContentHost;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.exception.ClientException;
import com.ca.arcflash.ui.client.homepage.PreferencesSettingsContent;
import com.ca.arcflash.ui.client.service.Broker;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class PreferencePresenter {
	private PreferencesSettingsContent prefSettingsContent;
	public PreferencePresenter(PreferencesSettingsContent prefSettingsContent) {
		this.prefSettingsContent = prefSettingsContent;

	}
	
	protected boolean validateSettings() {		
		getContentHost().increaseBusyCount();
		Broker.loginService.validatePreferences(prefSettingsContent.model, new AsyncCallback<Long>() {
			
			@Override
			public void onSuccess(Long result) {
				getContentHost().decreaseBusyCount();
				prefSettingsContent.setInitialTestConnection(false);				
				prefSettingsContent.onValidatingCompleted(true);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				getContentHost().decreaseBusyCount();

				prefSettingsContent.onValidatingCompleted(false);
				
				MessageBox msgError = new MessageBox();
				msgError.setIcon(MessageBox.ERROR);
				msgError.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
				msgError.setModal(true);
				String strMsg = UIContext.Messages.failedToSavePreferences();
				strMsg += (caught instanceof ClientException) ? ((ClientException) caught)
						.getDisplayMessage() : caught.getMessage();
				msgError.setMessage(strMsg);
				Utils.setMessageBoxDebugId(msgError);
				msgError.show();
			}
		});
		return true;
	}

	private ISettingsContentHost getContentHost() {		
		return prefSettingsContent.getContentHost();
	}

}
