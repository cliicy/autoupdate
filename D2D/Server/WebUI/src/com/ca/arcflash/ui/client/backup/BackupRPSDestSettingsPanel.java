package com.ca.arcflash.ui.client.backup;

import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.SelectRPSSettingsPanel;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.BackupRPSDestSettingsModel;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.ca.arcflash.ui.client.model.RetentionPolicyModel;
import com.ca.arcflash.ui.client.model.rps.RpsHostModel;
import com.ca.arcflash.ui.client.model.rps.RpsPolicy4D2DSettings;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.widget.MessageBox;

public class BackupRPSDestSettingsPanel extends SelectRPSSettingsPanel<RpsPolicy4D2DSettings> {
	
	protected BaseDestinationSettings parentWindow;
	
	public BackupRPSDestSettingsPanel(BaseDestinationSettings parentWindow){
		this.parentWindow = parentWindow;
		rpsPolicy.addSelectionChangedListener(setupRPSPolicyChangeListener());
	}	
	
	public SelectionChangedListener<RpsPolicy4D2DSettings> setupRPSPolicyChangeListener() {
		return new SelectionChangedListener<RpsPolicy4D2DSettings>() {
			
			@Override
			public void selectionChanged(
					SelectionChangedEvent<RpsPolicy4D2DSettings> se) {
				if(!isPolicyLoaded || rpsPolicyList == null || rpsPolicyList.size()==0)
					return;
				//destChangedBackupTypeContForRPS.enable();
				RpsPolicy4D2DSettings selectedItem = se.getSelectedItem();
				if(currentModel != null && !currentModel.getRpsPolicyUUID()
						.equalsIgnoreCase(selectedItem.getId())){
					parentWindow.clearSessionPassword();
				}
				updatePolicyDetailPanel(selectedItem);
				updateCompressionAndEncryption(selectedItem);			
			}
		};
	}	
	protected void updatePolicyDetailPanel(RpsPolicy4D2DSettings selectedItem) {
		for(RpsPolicy4D2DSettings policy : rpsPolicyList){
			if(selectedItem.getId().equals(policy.getId())){
				this.detailDataStoreName.setValue(policy.getDataStoreDisplayName());
				if(policy.isEnableGDD()){
					this.detailDedupe.setValue(UIContext.Constants.trueValue());
				}else {
					this.detailDedupe.setValue(UIContext.Constants.falseValue());
				}
				this.detailCompression.setValue(this.getCompressionLevel(policy));
				this.detailEncryption.setValue(this.getEncryptionType(policy));
				if(policy.isEnableReplication() != null && policy.isEnableReplication()) {
					this.detailReplication.setValue(UIContext.Constants.trueValue());
				}else {
					this.detailReplication.setValue(UIContext.Constants.falseValue());
				}
				this.detailRetentionCount.setValue(String.valueOf(policy.getRetentionCount()));
				updatePeriodRetentionCount(policy);
				break;
			}
		}
		policyDetailPanel.setVisible(true);
	}
	
	private void updatePeriodRetentionCount(RpsPolicy4D2DSettings policy) {
		this.detailDailyCount.setValue(String.valueOf(policy.getDailyCount()));
		this.setDailyCountVisible(policy.getDailyCount()>0);
		this.detailWeeklyCount.setValue(String.valueOf(policy.getWeeklyCount()));
		this.setWeeklyCountVisible(policy.getWeeklyCount()>0);
		this.detailMonthlyCount.setValue(String.valueOf(policy.getMonthlyCount()));
		this.setMonthlyCountVisible(policy.getMonthlyCount()>0);
	}

	private String getEncryptionType(RpsPolicy4D2DSettings ds){
		if(ds.getEnableEncryption() == null || ds.getEnableEncryption()== false){
			return UIContext.Constants.noEncryption();
		}else{
			if(ds.getEncryptionMethod()==1){
				return UIContext.Constants.AES128();
			}else if(ds.getEncryptionMethod()==2){
				return UIContext.Constants.AES192();
			}else{
				return UIContext.Constants.AES256();
			}
		}
	}
		
	public String getBackupDestination() {
		if (rpsPolicyList.size()==0) {
			return parentWindow.getOldDestination();
		}
		return rpsPolicy.getValue().getDataStoreSharedPath();
		/*String path = rpsPolicy.getValue().getStorePath();
		if (!path.startsWith("\\")){
			path = formatPolicyDestination(
					getRPSHostName(),
					rpsPolicy.getValue().getStorePath());
			return path.trim();
		}else{
			return null;
		}*/
	}
	
	private void updateCompressionAndEncryption(RpsPolicy4D2DSettings selectedItem){
		for(RpsPolicy4D2DSettings policy : rpsPolicyList){
			
			if(selectedItem.getId().equals(policy.getId())){
				String compression = getCompressionLevel(policy);
				parentWindow.updateCompression4Rps(compression);
				
				if(policy.getEnableEncryption() == null || !policy.getEnableEncryption()){
					parentWindow.updateEncryption4Rps(0);
				}else{
					parentWindow.updateEncryption4Rps(policy.getEncryptionMethod());
				}				
				break;
			}
		}
	}

	protected String getRPSHostName(){
		return rpsHostnameField.getValue();
	}
	
	protected void updateRPSPolicy() {
		loading.setVisible(true);
		String protocol = httpProtocolRadio.getValue()?"HTTP:":"HTTPS:" ;
		configRPSInD2DService.getRPSPolicyList(getRPSHostName(), 
				rpsUsernameField.getValue(), rpsPasswordField.getValue(), 
				rpsPortField.getValue().intValue(), protocol,
				new BaseAsyncCallback<List<RpsPolicy4D2DSettings>>(){
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				loading.setVisible(false);
			}
			
			@Override
			public void onSuccess(List<RpsPolicy4D2DSettings> result) {
				loading.setVisible(false);
				//isPolicyLoad = true;
				policyStore.removeAll();
				RpsPolicy4D2DSettings currentModel = rpsPolicy.getValue();
				rpsPolicyList = result;
				if(result == null || result.size()==0){
					rpsPolicyList= null;
					rpsPolicy.clear();
					showPolicyNotExistMsg(currentModel.getPolicyName());
				}else{
					boolean isExist = false;
					boolean isNameChanged = false;					
					isPolicyLoaded = true;					
					String oldPolicyName = currentModel.getPolicyName();
					for(RpsPolicy4D2DSettings model : result){
						if(model.getId().equals(currentModel.getId())){
							isExist = true;
							String oldName = currentModel.getPolicyName();
							currentModel.copy(model);
							if(!model.getPolicyName().equals(oldName)){
								isNameChanged = true;
								currentModel.setPolicyName(model.getPolicyName());
							}
							rpsPolicy.setValue(currentModel);
							updatePolicyDetailPanel(currentModel);
						}
						policyStore.add(model);
					}
					if(!isExist){
						rpsPolicyList= null;
						policyStore.removeAll();
						rpsPolicy.setValue(null);
						rpsPolicy.clear();
						showPolicyNotExistMsg(currentModel.getPolicyName());
					}
					if(isNameChanged){
						showPolicyNameChangeMsg(oldPolicyName,currentModel.getPolicyName());
					}
				}
				
			}
		});
	}
	
	protected void refreshPolicyList(){
		setOldRpsValue();
		thisWindow.mask(UIContext.Messages.loadingInfo(UIContext.productNameRPS));
		String protocol = httpProtocolRadio.getValue()?"HTTP:":"HTTPS:" ;
		configRPSInD2DService.getRPSPolicyList(getRPSHostName()
				, rpsUsernameField.getValue(), rpsPasswordField.getValue()
				, rpsPortField.getValue().intValue(), protocol,
				new BaseAsyncCallback<List<RpsPolicy4D2DSettings>>(){
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				rpsPolicyList= null;
				policyStore.removeAll();
				rpsPolicy.clear();
				policyDetailPanel.setVisible(false);
				thisWindow.unmask();
			}

			@Override
			public void onSuccess(List<RpsPolicy4D2DSettings> result) {
				if(result==null||result.isEmpty()){
					MessageBox messageBox = new MessageBox();
					messageBox.addCallback(new Listener<MessageBoxEvent>(){

						@Override
						public void handleEvent(MessageBoxEvent be) {
							rpsPolicyList= null;
							policyStore.removeAll();
							rpsPolicy.clear();
							policyDetailPanel.setVisible(false);
							thisWindow.unmask();
						}
						
					});
					
					messageBox.setTitleHtml(Utils.getProductName());
					messageBox.setMessage(UIContext.Messages.noRPSPolicy(UIContext.productNameRPS, getRPSHostName()));
					messageBox.setIcon(MessageBox.WARNING);
					messageBox.setModal(true);
					messageBox.setMinWidth(400);
					Utils.setMessageBoxDebugId(messageBox);
					messageBox.show();
					return;
				}
				isPolicyLoaded = true;

				RpsPolicy4D2DSettings currentPolicy = rpsPolicy.getValue();
				policyStore.removeAll();
				boolean isExist = false;
				for (RpsPolicy4D2DSettings policy : result) {
					if(currentPolicy!=null && policy.getId().equals(currentPolicy.getId())){
						isExist = true;
					}
				}
				rpsPolicyList = result;
				policyStore.add(result);
				if(currentPolicy == null || !isExist){
					rpsPolicy.setValue(result.get(0));
				}else{
					rpsPolicy.setValue(currentPolicy);
				}
				policyWarningOrError.setVisible(false);
				thisWindow.unmask();
				rpsPolicy.focus();
				rpsPolicy.expand();
			}
		});
	}

	public void saveData(BackupSettingsModel backupSettingsModel){
		BackupRPSDestSettingsModel model = backupSettingsModel.rpsDestSettings;
		if(model == null){
			model = new BackupRPSDestSettingsModel();
			model.rpsHost = new RpsHostModel();
			backupSettingsModel.rpsDestSettings = model;
		}
		String hostName = getRPSHostName();
		model.rpsHost.setHostName(hostName);
		model.rpsHost.setUserName(rpsUsernameField.getValue());
		model.rpsHost.setPassword(rpsPasswordField.getValue());
		model.rpsHost.setPort(rpsPortField.getValue().intValue());		
		model.rpsHost.setIsHttpProtocol(httpProtocolRadio.getValue());

		model.setRpsPolicy(rpsPolicy.getValue().getPolicyName());
		String policyUUID = rpsPolicy.getValue().getId();
		model.setRpsPolicyUUID(policyUUID);
		RpsPolicy4D2DSettings policy = rpsPolicy.getValue();
		backupSettingsModel.setDestination(policy.getDataStoreSharedPath());
		if(policy.getStoreUser() == null || policy.getStoreUser().isEmpty()){
			String username = rpsUsernameField.getValue();
			if(!username.contains("\\")){
				username = hostName +"\\" + username;
			}
			backupSettingsModel.setDestUserName(username);
			backupSettingsModel.setDestPassword(rpsPasswordField.getValue());
		}else {
			backupSettingsModel.setDestUserName(policy.getStoreUser());
			backupSettingsModel.setDestPassword(policy.getStorePassword());
		}	
		
		RetentionPolicyModel retModel = backupSettingsModel.retentionPolicy;
		if (retModel == null) {
			retModel = new RetentionPolicyModel();
			backupSettingsModel.retentionPolicy = retModel;
		}
		retModel.setRetentionCount(Integer.valueOf((String)(detailRetentionCount.getValue())));
	}
	
	public void setEditable(boolean editable){
		if(rpsHostnameField != null){
			rpsHostnameField.setEnabled(editable);
		}
		rpsUsernameField.setEnabled(editable);
		rpsPasswordField.setEnabled(editable);
		rpsPortField.setEnabled(editable);
		httpProtocolRadio.setEnabled(editable);
		httpsProtocolRadio.setEnabled(editable);
		rpsPolicy.setEnabled(editable);
		refreshButton.setEnabled(editable);
	}
}
