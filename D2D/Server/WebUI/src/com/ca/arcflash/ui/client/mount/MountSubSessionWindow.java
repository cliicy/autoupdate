package com.ca.arcflash.ui.client.mount;

import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.BaseSimpleComboBox;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.JMountRecoveryPointParamsModel;
import com.ca.arcflash.ui.client.model.MountedRecoveryPointItemModel;
import com.ca.arcflash.ui.client.model.RecoveryPointModel;
import com.ca.arcflash.ui.client.restore.PasswordPane;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class MountSubSessionWindow extends Window implements IMountAsyncCallback {
	private final LoginServiceAsync service = GWT.create(LoginService.class);
	private RadioGroup group;
	private Radio mountDriveLetter;
	private BaseSimpleComboBox<String> availableDriveLetters;
	private Radio mountNTFSFolder;
	private SimplePathSelectionPanel pathSelection;
	private PasswordPane passwordPane;
	private MountVolumeContainer parentPanel;
	private MountedRecoveryPointItemModel mountItem;
	private LayoutContainer warningContainer;
	
	public MountSubSessionWindow(MountVolumeContainer parentPanel, MountedRecoveryPointItemModel itemModel){
		this.parentPanel = parentPanel;
		this.mountItem = itemModel;
		this.setHeadingHtml(UIContext.Constants.mountWindowTitle());
		getMaskAdapter().setWindow(this);
		
		TableLayout tableLayout = new TableLayout();
		tableLayout.setCellPadding(2);
		tableLayout.setCellSpacing(2);
		this.setLayout(tableLayout);
		group = new RadioGroup("mount");
		
		LabelField labelField = new LabelField(UIContext.Constants.mountWindowDescription());
		labelField.setStyleAttribute("font-weight","bold");
		this.add(labelField);
		
		this.add(getWarningContainer());
		this.add(getDriveLetterContainer());
		this.add(getNTFSFolderContainer());
		this.add(getSessionPasswordPanel());
		
		Button okButton = new Button(UIContext.Constants.ok());
		okButton.addSelectionListener(getOKButtonListener());
		okButton.ensureDebugId("D915800B-A19D-42fd-A9E3-73F7D4ABE881");
		this.addButton(okButton);
		
		Button cancelButton = new Button(UIContext.Constants.cancel());
		cancelButton.addSelectionListener(getCancelButtonListener());
		cancelButton.ensureDebugId("F02F5F7D-B526-4a27-AE52-A887DEE6D0DE");
		this.addButton(cancelButton);
		
	}
	
	@Override
	public void show() {
		super.show();
		updateVisibleOfPwdPane();
		getAvailableMountDriveLetters();
		this.setWidth(620);
	}
	
	@Override
	protected void onHide() {
		super.onHide();
		getMaskAdapter().setWindow(parentPanel.getMountWindow());
	}

	private boolean validate(){
		if(mountDriveLetter.getValue()){
			return availableDriveLetters.validate();
		}
		else{
			return pathSelection.validate();
		}
	}
	
	private String getToMountPath(){
		if(mountDriveLetter.getValue()){
			return availableDriveLetters.getSimpleValue();
		}
		else{
			return pathSelection.getDestinationPath();
		}
	}
	
	private SelectionListener<ButtonEvent> getOKButtonListener(){
		return new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				if(!validate())
					return;
				
				checkSessionPassword();
			}
		};
	}
	
	private SelectionListener<ButtonEvent> getCancelButtonListener(){
		return new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				hide();
			}
		};
	}
	
	private LayoutContainer getWarningContainer(){
		warningContainer = new LayoutContainer();
		TableLayout tableLayout = new TableLayout(2);
		warningContainer.setLayout(tableLayout);
		
		warningContainer.add(AbstractImagePrototype.create(UIContext.IconBundle.status_small_warning()).createImage());
		LabelField label = new LabelField(UIContext.Constants.mountWindowNoAvailableDriveLetter());
		label.setStyleAttribute("padding-left", "5px");
		warningContainer.add(label);
		warningContainer.setVisible(false);
		return warningContainer;
	}
	
	private void setSelectDriveLetterEnable(boolean isEnable){
		availableDriveLetters.setEnabled(isEnable);
		pathSelection.setEnabled(!isEnable);
	}
	
	private LayoutContainer getDriveLetterContainer(){
		LayoutContainer container = new LayoutContainer();
		TableLayout tableLayout = new TableLayout(2);
		container.setLayout(tableLayout);
		
		mountDriveLetter = new Radio(){
			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);
				setSelectDriveLetterEnable(true);
			}
		};
		mountDriveLetter.setValue(true);
		mountDriveLetter.setBoxLabel(UIContext.Constants.mountWindowDriveLetter());
		mountDriveLetter.ensureDebugId("8D9FDE36-8E0A-448a-8F3B-9E81159A4A37");
		group.add(mountDriveLetter);
		container.add(mountDriveLetter);
		
		availableDriveLetters = new BaseSimpleComboBox<String>();
		availableDriveLetters.setAllowBlank(false);
		availableDriveLetters.setWidth(80);
		availableDriveLetters.setEditable(false);
		availableDriveLetters.ensureDebugId("53AC9E06-EFEF-4868-A51D-BDDC8EB938DC");
		container.add(availableDriveLetters);
		
		return container;
		
	}
	
	private LayoutContainer getNTFSFolderContainer(){
		LayoutContainer container = new LayoutContainer();
		TableLayout tableLayout = new TableLayout(1);
		container.setLayout(tableLayout);
		
		mountNTFSFolder = new Radio(){
			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);
				setSelectDriveLetterEnable(false);
			}
		};
		mountNTFSFolder.setBoxLabel(UIContext.Constants.mountWindowNTFSFolder());
		mountNTFSFolder.ensureDebugId("07763168-A9C0-44f2-91E9-93DECD596D03");
		group.add(mountNTFSFolder);
		container.add(mountNTFSFolder);
		
		pathSelection = new SimplePathSelectionPanel();
		pathSelection.setWidth(402);
		pathSelection.setEnabled(false);
		pathSelection.setStyleAttribute("padding-left", "15px");
		container.add(pathSelection);
		
		return container;
	}
	
	private LayoutContainer getSessionPasswordPanel(){
		passwordPane = new PasswordPane();
		return passwordPane;
	}
	
	protected void updateVisibleOfPwdPane() {
		passwordPane.setEncrptlabelTxt(UIContext.Constants.mountVolNeedSessionPassword());
		RecoveryPointModel rpModel = parentPanel.getSelectedRecoveryPointModel();
		if (!rpModel.isEncrypted())
			passwordPane.setVisible(false);
		else {
			passwordPane.setVisible(true);
			passwordPane.autoFillPassword(rpModel.getSessionGuid());
		}
	}
	private String getEncryptPassword(){
		if(passwordPane.isVisible()) {
			return passwordPane.getPassword();
		}
		else{
			return "";
		}
	}
	
	private WindowsMaskAdapter getMaskAdapter(){
		return parentPanel.getMountWindow().getMaskAdapter();
	}
	
	private void maskWindow(String maskText){
		getMaskAdapter().maskWindow(maskText);
	}
	
	private void unmaskWindow(){
		getMaskAdapter().unmaskWindow();
	}
	
	private void mountVolume(){
		String dest = parentPanel.getDestination();
		String domain = "";
		String pwd = "";
		String userName = parentPanel.getUserName();
		if (userName != null) {
			int index = userName.indexOf('\\');
			if (index > 0) {
				domain = userName.substring(0, index);
				userName = userName.substring(index + 1);
			}
			pwd = parentPanel.getPassword();
		}
		RecoveryPointModel rpModel = parentPanel.getSelectedRecoveryPointModel();
		
		JMountRecoveryPointParamsModel mountParams = new JMountRecoveryPointParamsModel();
		mountParams.setRpsHostname(parentPanel.getRpsHostName());
		mountParams.setDatastoreName(parentPanel.getDatastoreDisplayName());
		mountParams.setDest(parentPanel.getDestination());
		mountParams.setDomain(domain);
		mountParams.setEncryptionType(rpModel.getEncryptionType());
		mountParams.setEncryptPassword( getEncryptPassword() );
		mountParams.setMountPath(getToMountPath());
		mountParams.setPwd(pwd);
		mountParams.setSubPath(rpModel.getPath());
		mountParams.setUser(userName);
		mountParams.setVolGUID(mountItem.getVolumeGuid());
		
//		service.mountRecoveryPointItem( dest, domain, userName, pwd, rpModel.getPath(), 
//				mountItem.getVolumeGuid(), rpModel.getEncryptionType(), getEncryptPassword(), getToMountPath(), 
//				new BaseAsyncCallback<Long>(){
		service.mountRecoveryPointItem(mountParams, 
			new BaseAsyncCallback<Long>(){
			@Override
			public void onFailure(Throwable caught) {
				unmaskWindow();
				
				//deal with the errorCode:4294967312
				if (caught instanceof BusinessLogicException){
					BusinessLogicException exception = (BusinessLogicException)caught;
					if (exception.getErrorCode().equals("4294967312")){
						Listener<MessageBoxEvent> handler = new Listener<MessageBoxEvent>() {
							@Override
							public void handleEvent(MessageBoxEvent be) {
								hide();
								maskWindow(UIContext.Constants.restoreLoading());
								parentPanel.getMountWindow().refreshUI();
							}
						};
						
						showMessageBox(exception.getDisplayMessage(), handler);
						return;
					}
				}
				super.onFailure(caught);
			}
			@Override
			public void onSuccess(Long result) {
				parentPanel.getMountWindow().refreshUI();
				//unmaskWindow();
				//hide();
			}
		});
	}
	
	private void checkSessionPassword() {
		maskWindow(UIContext.Constants.mountMarkText());
		if(passwordPane.isVisible()) {
			String password = passwordPane.getPassword();
			if(password != null && password.length() > 0) {
				CommonServiceAsync service = GWT.create(CommonService.class);
				String sessionPwdHashValue = parentPanel.getSelectedRecoveryPointModel().getEncryptPwdHashKey();
				service.validateSessionPasswordByHash(password, password.length(), 
						sessionPwdHashValue, sessionPwdHashValue.length(),new BaseAsyncCallback<Boolean>() {
					@Override
					public void onFailure(Throwable caught) {
						unmaskWindow();
						super.onFailure(caught);
					}

					@Override
					public void onSuccess(Boolean isValid) {
						if(isValid != null && isValid) {
							mountVolume();
						}
						else {
							unmaskWindow();
							showMessageBox( UIContext.Constants.recoveryPointsInvalidSessionPassword(), null);
						}
					}
				});
			}
			else {
				unmaskWindow();
				showMessageBox(UIContext.Constants.mountVolNeedSessionPassword(), null);
			}
		}else {
			mountVolume();
		}
		
	}
	private void getAvailableMountDriveLetters(){
		service.getAvailableMountDriveLetters(new BaseAsyncCallback<List<String>>(){
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
			}

			@Override
			public void onSuccess(List<String> result) {
				availableDriveLetters.add(result);
				if(result.size()>0){
					warningContainer.setVisible(false);
					setSelectDriveLetterEnable(true);
					mountDriveLetter.setEnabled(true);
					mountDriveLetter.setValue(true);
					availableDriveLetters.setSimpleValue(result.get(0));
				}
				else{
					warningContainer.setVisible(true);
					setSelectDriveLetterEnable(false);
					mountDriveLetter.setEnabled(false);
					mountNTFSFolder.setValue(true);
				}
			}
		});
	}
	
	private void showMessageBox(String msgStr,Listener<MessageBoxEvent> handler){
		MessageBox msg = new MessageBox();
		if(handler!=null)
			msg.addCallback(handler);
		msg.setIcon(MessageBox.ERROR);
		msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(Utils.getProductName()));
		msg.setMessage(msgStr);
		msg.setModal(true);
		Utils.setMessageBoxDebugId(msg);
		msg.show();
	}
	
	@Override
	public void loadComplete() {
		hide();		
		parentPanel.getMountWindow().loadComplete();
	}
}
