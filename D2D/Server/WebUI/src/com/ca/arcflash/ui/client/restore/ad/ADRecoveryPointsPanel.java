package com.ca.arcflash.ui.client.restore.ad;

import com.ca.arcflash.ui.client.restore.RecoveryPointsPanel;
import com.ca.arcflash.ui.client.restore.RestoreWizardContainer;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.GridTreeNode;
import com.ca.arcflash.ui.client.model.RecoveryPointItemModel;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
//
public class ADRecoveryPointsPanel extends RecoveryPointsPanel {

	public ADRecoveryPointsPanel(RestoreWizardContainer restoreWizardContainer) {
		super(restoreWizardContainer);
	}
	
	@Override
	protected LayoutContainer renderHeaderSection() {
		LayoutContainer container = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(2);
		container.setLayout(tl);

		TableData td = new TableData();
		td.setWidth("5%");		

		LabelField label = new LabelField();
		Image image = AbstractImagePrototype.create(UIContext.IconBundle.restore_browse_ad_grt()).createImage();
		container.add(image, td);
		label.setValue(UIContext.Constants.restoreBrowseADButton());
		label.setStyleName("restoreWizardTitle");
		container.add(label);
		return container;
	}
	
	@Override
	public boolean validate(final AsyncCallback<Boolean> callback) {
		List<GridTreeNode> selectedNodes = GetSelectedNodes();
		
		//Check 1: No selection
		if (selectedNodes.size() == 0) {
			final MessageBox errMessage = MessageBox.info(UIContext.Constants
					.restoreBrowseButton(), UIContext.Constants
					.restoreMustSelectFiles(),null);
			errMessage.setModal(true);
			errMessage.setIcon(MessageBox.ERROR);
			Utils.setMessageBoxDebugId(errMessage);
			errMessage.show();

			callback.onSuccess(Boolean.FALSE);
			return false;
		}

		final GridTreeNode ad = selectedNodes.get(0);
		AsyncCallback pwdCallback = new AsyncCallback() {
			@Override
			public void onFailure(Throwable caught) {
				callback.onSuccess(false);
			}

			@Override
			public void onSuccess(Object result) {
				ad.setEncryptedKey(selectedSessionEncryptionKey);
				callback.onSuccess(true);
			}
		};
		checkAndUpdateSessionPassword(ad, pwdCallback);
		return true;
	}
	
	@Override
	public void fireRecoveryPointsChanged(Integer size) {
		AppEvent event = new AppEvent(RestoreWizardContainer.onRestoreDateChanged, size);       ///D2D Lite Integration
		event.setSource(RestoreWizardContainer.PAGE_AD_RECOVERY);
		fireEvent(RestoreWizardContainer.onRestoreDateChanged, event);	
	}
	
	@Override
	protected List<RecoveryPointItemModel> filterAvailableRecoveryPointItems(List<RecoveryPointItemModel> listOfRecoveryPointItems){
		List<RecoveryPointItemModel> result = new ArrayList<RecoveryPointItemModel>();
		for(RecoveryPointItemModel item : listOfRecoveryPointItems){
			if(item.getGuid().equals(GUID_ACTIVE_DIRECTORY)){
				result.add(item);
			}else{
				continue;
			}
		}
		return result;
	}
}
