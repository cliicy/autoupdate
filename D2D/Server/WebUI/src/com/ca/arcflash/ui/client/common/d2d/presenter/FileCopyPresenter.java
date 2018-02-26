package com.ca.arcflash.ui.client.common.d2d.presenter;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.ArchiveSettingsContent;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.ISettingsContentHost;
import com.ca.arcflash.ui.client.common.PathSelectionPanel;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.model.ArchiveSettingsModel;
import com.ca.arcflash.ui.client.service.Broker;
import com.extjs.gxt.ui.client.widget.MessageBox;

public class FileCopyPresenter {
	
	private ArchiveSettingsContent fileCopySetting;
	
	public FileCopyPresenter(ArchiveSettingsContent fileCopySetting) {
		this.fileCopySetting = fileCopySetting;
	}
	
	public ArchiveSettingsModel getFileCopyConfigModel() {
		return this.fileCopySetting.getArchiveConfigModel();
	}	

	public ISettingsContentHost getContentHost() {
		return this.fileCopySetting.getContentHost();
	}	
	
	public boolean validate() {
		fileCopySetting.Save();		
		Broker.loginService.validateArchiveConfiguration(getFileCopyConfigModel(), new BaseAsyncCallback<Long>() {

				@Override
				public void onSuccess(Long result) {
					getContentHost().decreaseBusyCount();
					fileCopySetting.onValidatingCompleted(true);
					//thisWindow.Save();
					//UIContext.d2dHomepagePanel.refreshProtectionSummary(null);
				}
				
				@Override
				public void onFailure(Throwable caught) {
									
					if(caught instanceof BusinessLogicException 
							&& ArchiveSettingsContent.ERR_REMOTE_DEST_WINSYSMSG.equals(((BusinessLogicException)caught).getErrorCode()))
						{
							final Throwable orginialExc = caught;							
							Broker.commonService.getDestDriveType(getFileCopyConfigModel().getArchiveToDrivePath(), new BaseAsyncCallback<Long>()
						    {
								@Override
								public void onFailure(Throwable caught) {
									getContentHost().decreaseBusyCount();
									fileCopySetting.onValidatingCompleted(false);
									super.onFailure(orginialExc);
								}
							
								@Override
								public void onSuccess(Long result) {
									if(result == PathSelectionPanel.REMOTE_DRIVE )
								    {
										fileCopySetting.popupUserPasswordWindow();
								    }
								    else {
								    	fileCopySetting.getArchiveDestSettings().getBrowseLocalOrNetworkDestinationPanel().setDestination("");
								    	getContentHost().decreaseBusyCount();
										fileCopySetting.onValidatingCompleted(false);
								    	super.onFailure(orginialExc);
								   	}
								}
				    	}
					);
					}
					else if(caught instanceof BusinessLogicException 
								&& ArchiveSettingsContent.INVALID_ARCHIVE_SOURCEFOUND.equals(((BusinessLogicException)caught).getErrorCode()))
						{
							fileCopySetting.focusPanel(fileCopySetting.STACK_ARCHIVE_SOURCE);
							getContentHost().decreaseBusyCount();
							fileCopySetting.onValidatingCompleted(false);
						    super.onFailure(caught);
						}
						else if (caught instanceof BusinessLogicException && "4294967295".equals(((BusinessLogicException)caught).getErrorCode()) ) {
							
							MessageBox msgError = new MessageBox();
							msgError.setIcon(MessageBox.ERROR);
							msgError.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
							msgError.setModal(true);
							msgError.setMinWidth(400);
							msgError.setMessage(((BusinessLogicException)caught).getDisplayMessage());		
							Utils.setMessageBoxDebugId(msgError);
							msgError.show();
							
							getContentHost().decreaseBusyCount();
							fileCopySetting.onValidatingCompleted(false);
//							super.onFailure(caught);
						}
						else {
							// Issue: 20231648    Title: FOCUS AFTER ENCRYPTION LIC ERR
							// Go to destination panel for Encryption License error. 
							if(caught instanceof BusinessLogicException 
								&& "4294967310".equals(((BusinessLogicException)caught).getErrorCode()))
							{
								fileCopySetting.focusPanel(fileCopySetting.STACK_ARCHIVE_DESTINATION);
							}
							
							getContentHost().decreaseBusyCount();
							fileCopySetting.onValidatingCompleted(false);
						    super.onFailure(caught);
						}				
				}				
			});
			
			return true;
	}
}
