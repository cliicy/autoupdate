package com.ca.arcflash.ui.client.backup;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.BaseComboBox;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.PasswordTextField;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.encrypt.EncryptionAlgModel;
import com.ca.arcflash.ui.client.model.encrypt.EncryptionLibModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;

public class EncryptionPane extends LayoutContainer {
	
	final CommonServiceAsync service = GWT.create(CommonService.class);
	
	private BaseComboBox<EncryptionAlgModel> encryptionAlgOption;
	private ListStore<EncryptionAlgModel> encryptAlgStore;
	private LabelField encryptKeyLabel;
	private PasswordTextField encryptionKeyTextField;
	private LabelField reTypeEncryptKeyLabel;
	private PasswordTextField retypeEncryptionKeyTextField;
	private LayoutContainer warningContainer;
	
	private String keyID = "C604DA5B-8091-491b-9EFC-159CE15436E4";
	private String rekeyID = "210D99A7-BA54-4da4-BBD1-F122551790C6";
	private String algID = "3FD61FF2-9B25-4057-9D8A-3D9B424F0A02";

	private Integer selectedAlgortith;
	
	private int libType;

	public EncryptionPane() {

		TableLayout tableEncryptAlgLayout = new TableLayout();
//		tableEncryptAlgLayout.setWidth("100%");
		tableEncryptAlgLayout.setCellPadding(2);
		tableEncryptAlgLayout.setCellSpacing(2);
//		tableEncryptAlgLayout.setColumns(4);
		tableEncryptAlgLayout.setColumns(2);
		setLayout(tableEncryptAlgLayout);
		
//		LabelField algDscrLabel = new LabelField();
//		algDscrLabel.setText("Select encryption algorithm and provide encryption key.");
		TableData data = new TableData();
		data.setColspan(2);
		
//		encryptCheckBox = new CheckBox();
//		encryptCheckBox.setBoxLabel("Encrypt compressed backup files");
//		encryptCheckBox.setValue(true);
		
//		encryptionAlgContainer.add(encryptCheckBox, data);
		
		LabelField algLabel = new LabelField();
		algLabel.setValue(UIContext.Constants.encryptionAlgorithm());
		algLabel.addStyleName("backupDestinationEncrypt"); //setStyleAttribute("margin-left", "4px");
		data = new TableData();
//		data.setPadding(4);
//		data.setMargin(4);
//		data.setStyleName("backupDestinationEncrypt");
//		data.setWidth("10%");
//		algLabel.addStyleName("restoreWizardSubItem");
		add(algLabel, data);
		
//		encryptionAlgOption = new BaseSimpleComboBox<String>();
//		encryptionAlgOption.setEditable(false);
//		noAlgorithm = "No Encryption";
//		encryptionAlgOption.add(noAlgorithm);
//		algorithm1 = "AES-128";
//		algorithm2 = "AES-192";
//		algorithm3 = "AES-256";
//		encryptionAlgOption.add(algorithm1);
//		encryptionAlgOption.add(algorithm2);
//		encryptionAlgOption.add(algorithm3);
//		encryptionAlgOption.setSimpleValue(algorithm1);
//		encryptionAlgOption.setToolTip("Select an encryption algorithm.");
//		encryptionAlgOption.setWidth(200);
//		encryptionAlgOption.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>(){
		encryptionAlgOption = new BaseComboBox<EncryptionAlgModel>();
		encryptionAlgOption.ensureDebugId(algID);
		encryptionAlgOption.setDisplayField("name");
		encryptionAlgOption.setEditable(false);
		encryptionAlgOption.setWidth(200);
		encryptAlgStore = new ListStore<EncryptionAlgModel>();
		encryptionAlgOption.setStore(encryptAlgStore);
		encryptionAlgOption.addSelectionChangedListener(new SelectionChangedListener<EncryptionAlgModel>(){
				@Override
				public void selectionChanged(
						SelectionChangedEvent<EncryptionAlgModel> se) {
					setEncryptionPasswordControlsEnabled();
					fireValueChanged();
				}
			}
		);
		data = new TableData();
		data.setHorizontalAlign(HorizontalAlignment.LEFT);
		add(encryptionAlgOption, data);
		
		encryptKeyLabel = new LabelField();
		encryptKeyLabel.setValue(UIContext.Constants.encryptionPassword());
		encryptKeyLabel.addStyleName("backupDestinationEncrypt");
		data = new TableData();
//		data.setPadding(4);
//		data.setMargin(4);
//		data.setWidth("10%");
//		data.setStyleName("backupDestinationEncrypt");
//		pswLabel.addStyleName("restoreWizardSubItem");
		add(encryptKeyLabel, data);
		
		encryptionKeyTextField = new PasswordTextField();
		encryptionKeyTextField.ensureDebugId(keyID);
		encryptionKeyTextField.setPassword(true);
		//passwordTextField.setWidth("100%");
		encryptionKeyTextField.setWidth(200);
		encryptionKeyTextField.setValue("");
		encryptionKeyTextField.setMaxLength(Utils.EncryptionPwdLen);
		data = new TableData();
		data.setHorizontalAlign(HorizontalAlignment.LEFT);
		LayoutContainer lc = new LayoutContainer();
//		Layout layout = new HBoxLayout();
		lc.setWidth(250);
//		lc.setLayout(layout);
		lc.add(encryptionKeyTextField);
		encryptionKeyTextField.addListener(Events.Change, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				fireValueChanged();
			}
		});
		add(lc, data);
		
		reTypeEncryptKeyLabel = new LabelField();
		reTypeEncryptKeyLabel.addStyleName("backupDestinationEncrypt");
		reTypeEncryptKeyLabel.setValue(UIContext.Constants.retypeEncryptionPassword());
		data = new TableData();
//		data.setStyleName("backupDestinationEncrypt");
		add(reTypeEncryptKeyLabel, data);
		
		retypeEncryptionKeyTextField = new PasswordTextField();
		retypeEncryptionKeyTextField.ensureDebugId(rekeyID);
		retypeEncryptionKeyTextField.setPassword(true);
		//passwordTextField.setWidth("100%");
		retypeEncryptionKeyTextField.setWidth(200);
		retypeEncryptionKeyTextField.setValue("");
		retypeEncryptionKeyTextField.setMaxLength(Utils.EncryptionPwdLen);
		retypeEncryptionKeyTextField.setValidateOnBlur(true);
		retypeEncryptionKeyTextField.setValidator(new Validator(){

			@Override
			public String validate(Field<?> field, String value) {
				EncryptionAlgModel encryptAlg = encryptionAlgOption.getValue();
	    		Boolean enableEncryption = encryptAlg != null && encryptAlg.getAlgType() > 0;
	    		if(enableEncryption) {
	    			if(encryptionKeyTextField.getValue() != null
	    				&& !encryptionKeyTextField.getValue().equals(retypeEncryptionKeyTextField.getValue())){
	    				return UIContext.Constants.verifyPassword();
	    			}else{
	    				return null;
	    			}
	    		}
	    		return null;
			}
			
		});
		data = new TableData();
		data.setHorizontalAlign(HorizontalAlignment.LEFT);
		LayoutContainer lc1 = new LayoutContainer();
//		Layout layout1 = new HBoxLayout();
		lc1.setWidth(250);
//		lc1.setLayout(layout1);//can not use HBoxLayou.(scheduled export settings)
		lc1.add(retypeEncryptionKeyTextField);
		add(lc1, data);
//		encryptCheckBox.addListener(Events.Change, new Listener<BaseEvent>() {
//
//			@Override
//			public void handleEvent(BaseEvent be) {
//				if(encryptCheckBox.getValue()) {
//					encryptionAlgOption.setEnabled(true);
//					encryptionKeyTextField.setEnabled(true);
//				}
//				else {
//					encryptionAlgOption.setEnabled(false);
//					encryptionKeyTextField.setEnabled(false);
//					encryptionKeyTextField.setValue("");
//				}
//			}
//		});
		
		warningContainer = new LayoutContainer();
		TableLayout warningLayout = new TableLayout();
		warningLayout.setCellSpacing(4);
		warningLayout.setColumns(2);
		warningContainer.setLayout(warningLayout);
		
		Image warningImage = AbstractImagePrototype.create(UIContext.IconBundle.status_small_warning()).createImage();
		warningContainer.add(warningImage,new TableData());		
		LabelField warningLabel = new LabelField();
		warningLabel.setValue(UIContext.Constants.backupEnryptionAlgorithmAndKeyChanged());
		warningContainer.add(warningLabel,new TableData());
		warningContainer.setVisible(false);
		
		loadEncryptAlg();
	
	}
	
	private void fireValueChanged(){
		this.fireEvent(Events.Change);
	}
	
	private void setEncryptionPasswordControlsEnabled() {
		Integer algType = encryptionAlgOption.getValue().getAlgType();
		if(algType == null || algType == 0) {
			enableEncryptionPasswordControls(false);
		}
		else {
			enableEncryptionPasswordControls(true);
		}
	}
	
	private void enableEncryptionPasswordControls(boolean enable) {
		encryptKeyLabel.setEnabled(enable);
		encryptionKeyTextField.setEnabled(enable);
		reTypeEncryptKeyLabel.setEnabled(enable);
		retypeEncryptionKeyTextField.setEnabled(enable);
		Utils.addToolTip(encryptionKeyTextField, UIContext.Constants.scheduledExportToolTipPassword());
		Utils.addToolTip(retypeEncryptionKeyTextField, UIContext.Constants.scheduledExportToolTipRePassword());
		if(!enable) {
			encryptionKeyTextField.setValue("");
			retypeEncryptionKeyTextField.setValue("");
		}
	}
	
	public LayoutContainer getWarningContainer() {
		return warningContainer;
	}
	
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
	}
	
	public void setEncryptAlogrithm(Integer alg) {
		selectedAlgortith = alg;
		refreshEncryptionContainer();
	}
	
	public void setEncryptPassword(String password) {
		encryptionKeyTextField.setValue(password);
		retypeEncryptionKeyTextField.setValue(password);
	}

	public void setEncryptPasswordOnly(String password) {
		encryptionKeyTextField.setValue(password);
	}
	
	
	private void loadEncryptAlg() {
		service.getEncryptionAlgorithm(new BaseAsyncCallback<EncryptionLibModel>() {

			@Override
			public void onFailure(Throwable caught) {
//				super.onFailure(caught);
			}

			@Override
			public void onSuccess(EncryptionLibModel result) {
				initEncryptionAlgModels(result);
			}
			
		});
	}
	
	private void initEncryptionAlgModels(EncryptionLibModel libModel) {
		EncryptionAlgModel[] encryptAlgModels = libModel.algorithms;
//		encryptAlgStore = new ListStore<EncryptionAlgModel>();
		List<EncryptionAlgModel> list = new ArrayList<EncryptionAlgModel>();
		EncryptionAlgModel noEncryptModel = new EncryptionAlgModel();
		noEncryptModel.setName(UIContext.Constants.noEncryption());
		noEncryptModel.setAlgType(0);
		list.add(noEncryptModel);
		libType = libModel.getLibType() == null ? 0 : libModel.getLibType();
		if(encryptAlgModels != null) {
			for (int i = 0; i < encryptAlgModels.length; i++) {
				encryptAlgModels[i].setAlgType((libType << 16) | encryptAlgModels[i].getAlgType());
				list.add(encryptAlgModels[i]);
			}
		}
		
		encryptAlgStore.add(list);
//		encryptAlgModels = list.toArray(new EncryptionAlgModel[0]);
//		encryptionAlgOption.setStore(encryptAlgStore);
		refreshEncryptionContainer();
		
	}
	
	private void refreshEncryptionContainer() {
		if(encryptAlgStore.getCount() == 0)
			return;
		
		if(selectedAlgortith != null)
			encryptionAlgOption.setValue(getEncryptName(selectedAlgortith));
		else
			encryptionAlgOption.setValue(getEncryptName(0));
			
		// liuwe05 2010-11-30 fix Issue: 19882405    Title: ECRYP PASSWORD TXT SHOULD GRAY
		// Problem: If the backup setting is "No Encryption", when the backup setting is launched, the controls to input password will be not be disabled.
		// Manually fire the selection change event to update the controls' states
		if (encryptionAlgOption != null)
		{
			SelectionChangedEvent<EncryptionAlgModel> se = new SelectionChangedEvent<EncryptionAlgModel>(encryptionAlgOption, encryptionAlgOption.getSelection());
			encryptionAlgOption.fireEvent(Events.SelectionChange, se);
		}
	}
	
	private EncryptionAlgModel getEncryptName(Integer algrithm) {
		if(algrithm == null)
			return encryptAlgStore.getAt(0);
		
		for(int i = 0, count = encryptAlgStore.getCount(); i < count; i++) {
			EncryptionAlgModel alg = encryptAlgStore.getAt(i);
			if(algrithm.intValue() == alg.getAlgType())
				return alg;
		}
		
		return encryptAlgStore.getAt(0);
	}

	public Integer getEncryptAlgorithm() {
		EncryptionAlgModel encryptAlg = encryptionAlgOption.getValue();
		if(encryptAlg==null)
			return null;
		return encryptAlg.getAlgType();
	}
	
	public String getEncryptPassword() {
		String encryptPassword = encryptionKeyTextField.getValue();
		return encryptPassword;
	}
	
    public boolean validate() {
//	//	int selectedEncryption = encryptionAlgOption.getSelectedIndex();
//    	boolean isValid = true;
//		String encryptKey = encryptionKeyTextField.getValue();
//		encryptKey = encryptKey == null ? "" : encryptKey;
//		String retypeEcryptKey = retypeEncryptionKeyTextField.getValue();
//		retypeEcryptKey = retypeEcryptKey == null ? "" : retypeEcryptKey;
//		
//	//	Boolean enableEncryption = encryptCheckBox.getValue() && getCompressionLevel() > 0;
//		EncryptionAlgModel encryptAlg = encryptionAlgOption.getValue();
//		Boolean enableEncryption = encryptAlg != null && encryptAlg.getAlgType() > 0;
//		if(enableEncryption) {
//			String msgStr = null;
//			if(encryptKey.length() == 0 && retypeEcryptKey.length() == 0) {
//				msgStr = UIContext.Constants.provideEncryption();
//				isValid = false;
//			}
//			else if(!encryptKey.equals(retypeEcryptKey))
//			{
//				msgStr = UIContext.Constants.verifyPassword();
//				isValid = false;
//			}
//			else if(encryptionKeyTextField.isMaxLengthExceeded())
//			{
//				msgStr = UIContext.Constants.PasswordBeyondLength();
//				isValid = false;
//			}
//			
//			if(!isValid){
//				MessageBox msg = new MessageBox();
//				msg.setIcon(MessageBox.ERROR);
//				msg.setTitle(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
//				msg.setMessage(msgStr);
//				msg.setModal(true);
//				Utils.setMessageBoxDebugId(msg);
//				msg.show();
//				return false;
//			}
//		}
//		
//		return true;
		StringBuilder sb = new StringBuilder();
		if(!isValid(sb)){
			MessageBox msg = new MessageBox();
			msg.setIcon(MessageBox.ERROR);
			msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
			msg.setMessage(sb.toString());
			msg.setModal(true);
			Utils.setMessageBoxDebugId(msg);
			msg.show();
			return false;
		}
		return true;
    }

	public boolean isValid(StringBuilder sb) {
		// int selectedEncryption = encryptionAlgOption.getSelectedIndex();
		boolean isValid = true;
		if (this.isVisible() && this.isEnabled()) {
			String encryptKey = encryptionKeyTextField.getValue();
			encryptKey = encryptKey == null ? "" : encryptKey;
			String retypeEcryptKey = retypeEncryptionKeyTextField.getValue();
			retypeEcryptKey = retypeEcryptKey == null ? "" : retypeEcryptKey;

			// Boolean enableEncryption = encryptCheckBox.getValue() &&
			// getCompressionLevel() > 0;
			EncryptionAlgModel encryptAlg = encryptionAlgOption.getValue();
			Boolean enableEncryption = encryptAlg != null && encryptAlg.getAlgType() > 0;
			if (enableEncryption) {
				String msgStr = null;
				if (encryptKey.length() == 0 && retypeEcryptKey.length() == 0) {
					msgStr = UIContext.Constants.provideEncryption();
					isValid = false;
				} else if (!encryptKey.equals(retypeEcryptKey)) {
					msgStr = UIContext.Constants.verifyPassword();
					isValid = false;
				} else if (encryptionKeyTextField.isMaxLengthExceeded()) {
					msgStr = UIContext.Constants.PasswordBeyondLength();
					isValid = false;
				}
				if (sb != null) {
					sb.append(msgStr);
				}
			}
		}
		return isValid;
	}
    
    public void addEncAlgSelectionChangedHandler(SelectionChangedListener<EncryptionAlgModel> selectionChangedListener) {
    	if(selectionChangedListener != null)
    		encryptionAlgOption.addSelectionChangedListener(selectionChangedListener);
    }
    
    /**
     * should invoke after encryption algorithm And Key have initialized value.
     */
    public void addEncryptionAlgorithmAndKeyChangedHandler() {
    	encryptionAlgOption.addSelectionChangedListener(new SelectionChangedListener<EncryptionAlgModel>() {

			@Override
			public void selectionChanged(
					SelectionChangedEvent<EncryptionAlgModel> se) {
				encryptionAlgorithmAndKeyChangedWarning();
			}
    		
    	});
    	
    	encryptionKeyTextField.addListener(Events.Change, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				int length = encryptionKeyTextField.getValue().length();
				if(!encryptionKeyTextField.isMaxLengthExceeded()) {
					encryptionAlgorithmAndKeyChangedWarning();
				}
			}
		});
    }
    
    private void encryptionAlgorithmAndKeyChangedWarning() {
    	int encryptionAlgorithm = getEncryptAlgorithm();
    	String encryptionKey = getEncryptPassword();
    	CommonServiceAsync service = GWT.create(CommonService.class);
    	service.isBackupEncryptionAlgorithmAndKeyChanged(encryptionAlgorithm, encryptionKey, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(Boolean result) {
				if(result.booleanValue()) {
					warningContainer.setVisible(true);
				} else {
					warningContainer.setVisible(false);
				}
			}
		});
    }
    
    @Override
    public void enable() {
    	super.enable();

    	setEncryptionPasswordControlsEnabled();
    	
    }
    
    public void setDebugID(String keyID, String rekeyID, String algID){
    	this.keyID = keyID;
    	this.algID = algID;
    	this.rekeyID = rekeyID;
    }
    
    public void setEncryptionAlgorithmEnable(boolean isEnable){
    	encryptionAlgOption.setEnabled(isEnable);
    }
    
    public int getLibType(){
    	return this.libType;
    }
    
    public void setEncryptionKeyVisable(boolean isVisable){
    	encryptKeyLabel.setVisible(isVisable);
    	encryptionKeyTextField.setVisible(isVisable);
    	reTypeEncryptKeyLabel.setVisible(isVisable);
    	retypeEncryptionKeyTextField.setVisible(isVisable);
    }
}
