package com.ca.arcflash.ui.client.restore;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.BaseComboBox;
import com.ca.arcflash.ui.client.common.ICommonRPSService4D2D;
import com.ca.arcflash.ui.client.common.ICommonRPSService4D2DAsync;
import com.ca.arcflash.ui.client.common.PasswordTextField;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.BackupRPSDestSettingsModel;
import com.ca.arcflash.ui.client.model.RpsPolicy4D2DRestoreModel;
import com.ca.arcflash.ui.client.model.rps.RpsHostModel;
import com.ca.arcflash.ui.client.model.rps.RpsPolicy4D2DSettings;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;

public class RestoreRPSSettingsPanel extends ContentPanel {
	
	private RestoreSelectRPSSettingWindow restoreWindow;
	private boolean initialized = false;
	
	protected int MIN_FIELD_WIDTH = 250;
	protected int MIN_COMBO_WIDTH = 150;
	protected static final int DEFAULT_HTTP_PORT = 8014;
	
	protected final ICommonRPSService4D2DAsync configRPSInD2DService = 
			GWT.create(ICommonRPSService4D2D.class);
	
	protected BaseComboBox<RpsPolicy4D2DRestoreModel> rpsDataStore = 
			new BaseComboBox<RpsPolicy4D2DRestoreModel>();
	protected boolean isDataStoreLoaded = false;
//	protected HorizontalPanel dataStoreWarningOrError;
	
	protected TextField<String> rpsHostnameField;
	protected TextField<String> rpsUsernameField;
	protected PasswordTextField rpsPasswordField;
	protected NumberField rpsPortField;
	protected Radio httpProtocolRadio;
	protected Radio httpsProtocolRadio;
	
	protected ListStore<RpsPolicy4D2DRestoreModel> dsStore 
				= new ListStore<RpsPolicy4D2DRestoreModel>();
	protected List<RpsPolicy4D2DRestoreModel> rpsDataStoreList 
				= new ArrayList<RpsPolicy4D2DRestoreModel>();
	
	protected String oldRpsHostName;
	private String oldRpsUserName;
	private String oldRpsPassword;
	private Boolean oldRpsProtocol;
	private int oldRpsPort;
	
	private String validationError;
	protected RestoreRPSSettingsPanel thisWindow;	
//	protected BackupRPSDestSettingsModel currentModel;
	protected Button refreshButton;
	
	public RestoreRPSSettingsPanel(RestoreSelectRPSSettingWindow parent){
		this.restoreWindow = parent;
		//this.MIN_FIELD_WIDTH = 150;
		thisWindow = this;
//		setWidth("99%");
//		setHeight("100%");
		setHeaderVisible(false);
		TableLayout rpsBackupDestSettingsLayout = new TableLayout();
		rpsBackupDestSettingsLayout.setColumns(2);
		rpsBackupDestSettingsLayout.setCellPadding(2);
		rpsBackupDestSettingsLayout.setCellSpacing(4);
		rpsBackupDestSettingsLayout.setWidth("98%");
		rpsBackupDestSettingsLayout.setHeight("100%");
		setLayout(rpsBackupDestSettingsLayout);
		initRpsServerSettingFieldSet();
		rpsDataStore.addSelectionChangedListener(this.setupRPSDataStoreChangeListener());
	}
	
	protected void addRpsDataStore(LayoutContainer container){
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.rpsDataStore());
		container.add(label);
		rpsDataStore.setTriggerAction(TriggerAction.ALL);
		rpsDataStore.setStore(dsStore);
		rpsDataStore.setEditable(false);
		rpsDataStore.setDisplayField("dataStoreDisplayName");
		rpsDataStore.setTemplate(getCommonBoxTemplate(rpsDataStore.getDisplayField()));
		rpsDataStore.setWidth(MIN_COMBO_WIDTH);
		rpsDataStore.ensureDebugId("850bdfef-fe2e-4f7b-b041-5f0e2f55da1a");
		rpsDataStore.addListener(Events.OnMouseDown, this.setupRPSDataStoreMouseListener());	
	}
	
	protected void addRpsDataStoreRow(LayoutContainer container) {		
		addRpsDataStore(container);
		HorizontalPanel panel = new HorizontalPanel();
		panel.setWidth("100%");
		panel.add(rpsDataStore);		
		refreshButton = new Button(UIContext.Constants.policyRefresh());
		refreshButton.ensureDebugId("8DCF4186-394E-42F7-AA7B-CA256CDABD0C");
		refreshButton.setMinWidth(80);
		refreshButton.setStyleAttribute("margin-left", "3px");
		refreshButton.addSelectionListener(this.setupRefreshButtonListener());
		panel.add(refreshButton);
		container.add(panel);			
	}

	protected native String getCommonBoxTemplate(String displayName) /*-{  
    return  [  
	    '<tpl for=".">',  
	    '<div class="x-combo-list-item" qtip="<div style=\'word-break:break-all;\'>{'+displayName+'}</div>">{'+displayName+'}</div>',  
	    '</tpl>'  
	    ].join("");  
	 }-*/;
	
	private native String getPolicyTemplate() /*-{
		return  [ 
    		'<tpl for=".">', 
    		'<div class="x-combo-list-item" qtip="{policyName}" qtitle="">{policyName}</div>', 
    		'</tpl>' 
    		].join(""); 
	}-*/;


	private void addRpsProtocolRow(LayoutContainer rpsServerSettingContainer){
		LabelField label = new LabelField(UIContext.Constants.protocol());
		rpsServerSettingContainer.add(label);
		
		LayoutContainer rpsProtocolSettingContainer = new LayoutContainer();
		TableLayout rpsProtocolSettingLayout = new TableLayout();
		rpsProtocolSettingLayout.setColumns(2);
		rpsProtocolSettingContainer.setLayout(rpsProtocolSettingLayout);
		
		final RadioGroup protocolGroup = new RadioGroup();
		httpProtocolRadio = new Radio();
		httpProtocolRadio.setBoxLabel(UIContext.Constants.restoreD2DSelectionProtocolHttp());
		httpProtocolRadio.ensureDebugId("bca449d7-71a1-4dcf-bd79-5858c270d279");
		httpProtocolRadio.setValue(true);
		httpsProtocolRadio = new Radio();
		httpsProtocolRadio.setBoxLabel(UIContext.Constants.restoreD2DSelectionProtocolHttps());
		httpsProtocolRadio.ensureDebugId("f6e83c3d-b283-4c86-b028-6b95dfa4c3b3");
		protocolGroup.add(httpProtocolRadio);
		protocolGroup.add(httpsProtocolRadio);

		rpsProtocolSettingContainer.add(httpProtocolRadio);
		rpsProtocolSettingContainer.add(httpsProtocolRadio);
		
		rpsServerSettingContainer.add(rpsProtocolSettingContainer);
	}
	
	private void addRpsHostnameRow(LayoutContainer rpsServerSettingContainer){
		LabelField label = new LabelField(UIContext.Constants.RPSHostname());
		TableData tableData = new TableData();
		tableData.setWidth("25%");
		rpsServerSettingContainer.add(label, tableData);
		addRpsHostField(rpsServerSettingContainer);
	}

	protected void addRpsHostField(LayoutContainer rpsServerSettingContainer){
		rpsHostnameField = new TextField<String>();
		rpsHostnameField.setWidth(MIN_FIELD_WIDTH);
		rpsHostnameField.ensureDebugId("f947e33f-c15e-4ac9-ab98-1f07f5779fc6");
		rpsHostnameField.setAllowBlank(false);
//		rpsHostnameField.addListener(Events.Blur, this.setUpHostNameChangeListener());

		TableData tableData = new TableData();
		tableData.setWidth("75%");
		rpsServerSettingContainer.add(rpsHostnameField, tableData);
	}
	
	private void addRpsUsernameRow(LayoutContainer rpsServerSettingContainer){
		LabelField label = new LabelField(UIContext.Constants.UserName());
		rpsServerSettingContainer.add(label);		
		rpsUsernameField = new TextField<String>();
		rpsUsernameField.setWidth(MIN_FIELD_WIDTH);
		rpsUsernameField.ensureDebugId("16d7aea0-95f3-46de-95f7-17cb79ca7a8a");
		rpsUsernameField.setAllowBlank(false);
		rpsServerSettingContainer.add(rpsUsernameField);
	}
	
	private void addRpsPasswordRow(LayoutContainer rpsServerSettingContainer){
		LabelField label = new LabelField(UIContext.Constants.Password());
		rpsServerSettingContainer.add(label);
		rpsPasswordField = new PasswordTextField();
		rpsPasswordField.setPassword(true);
		rpsPasswordField.setWidth(MIN_FIELD_WIDTH);
		rpsPasswordField.ensureDebugId("66efc28d-9d3c-4a89-ac73-f776a11d5c0c");
		rpsPasswordField.setAllowBlank(false);
		rpsServerSettingContainer.add(rpsPasswordField);
	}
	
	private void addRpsPortRow(LayoutContainer rpsServerSettingContainer){
		LabelField label = new LabelField(UIContext.Constants.Port());
		rpsServerSettingContainer.add(label);
		rpsPortField = new NumberField();
		//rpsPortField.setWidth(MIN_FIELD_WIDTH);
		rpsPortField.ensureDebugId("4ac89556-3d26-416e-9013-f0ebfad4c4cd");
		rpsPortField.setValue(DEFAULT_HTTP_PORT);
		rpsPortField.setAllowBlank(false);
		rpsPortField.setAllowDecimals(false);
		rpsPortField.setAllowNegative(false);
		rpsServerSettingContainer.add(rpsPortField);
	}
	
	private void createHeader(LayoutContainer container){
		LabelField title = new LabelField(UIContext.Messages.rpsServerSetting(
				UIContext.Constants.productShortNameRPS()));
		title.setStyleName("restoreWizardSubItem");
		TableData td = new TableData();
		td.setColspan(2);
		container.add(title, td);
		LabelField lf = new LabelField();
		container.add(lf, td);
		return;
	}

	private void initRpsServerSettingFieldSet() {
//		FieldSet rpsServer = new FieldSet();
//		ContentPanel rpsServer = new ContentPanel();
//		rpsServer.setHeaderVisible(false);
//		rpsServer.setHeight("100%");
//		TableLayout rpsBackupDestSettingsLayout = new TableLayout();
//		rpsBackupDestSettingsLayout.setColumns(2);
//		rpsBackupDestSettingsLayout.setCellPadding(2);
//		rpsBackupDestSettingsLayout.setCellSpacing(2);
//		rpsBackupDestSettingsLayout.setWidth("98%");
//		rpsBackupDestSettingsLayout.setHeight("100%");
//		rpsServer.setLayout(rpsBackupDestSettingsLayout);
//		rpsServer.ensureDebugId("35B4AAE5-BA7D-458b-8F92-86FAFD13C0BA");
		
		createHeader(this);		
		addRpsHostnameRow(this);
		addRpsUsernameRow(this);
		addRpsPasswordRow(this);
		addRpsPortRow(this);
		addRpsProtocolRow(this);	
		addRpsDataStoreRow(this);
//		rpsServer.add(rpsServerSettingContainer);
		
//		return rpsServer;
	}
	
	private boolean isRpsChanged(){
		if(!oldRpsHostName.equals(getRPSHostName())
				||!oldRpsUserName.equals(rpsUsernameField.getValue())
				||!oldRpsPassword.equals(rpsPasswordField.getValue()) 
				||oldRpsProtocol !=httpProtocolRadio.getValue()
				|| oldRpsPort != rpsPortField.getValue().intValue())
			return true;
		return false;
	}
	
	public Listener<FieldEvent> setupRPSDataStoreMouseListener(){
		return new Listener<FieldEvent>(){
			@Override
			public void handleEvent(FieldEvent be) {
				if(!validateRPSConfig())
					return;
				if(isDataStoreLoaded&&!isRpsChanged())
					return;
				refreshDataStoreList();
			}
		};
	}

	public SelectionListener<ButtonEvent> setupRefreshButtonListener(){ 
		return new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				if(validateRPSConfig())
					refreshDataStoreList();
			}
		};
	}
	
	protected String getRPSHostName(){
		return rpsHostnameField.getValue();
	}

	protected void setRpsServerValue(RpsHostModel rpsHost) {
		rpsUsernameField.setValue(rpsHost.getUserName());
		rpsPasswordField.setValue(rpsHost.getPassword());
		
		if (rpsHost.getPort() != null && rpsHost.getPort() > 0) {					
			rpsPortField.setValue(rpsHost.getPort());
		} else {
			rpsPortField.setValue(DEFAULT_HTTP_PORT);
		}
		if (rpsHost.getIsHttpProtocol() != null && rpsHost.getIsHttpProtocol()) {
			httpProtocolRadio.setValue(Boolean.TRUE);
		} else {
			httpsProtocolRadio.setValue(Boolean.TRUE);
		}
	}
	
	
	public void setRpsHostValue(RpsHostModel host){
		rpsHostnameField.setValue(host.getHostName());
	}
	
	protected void setOldRpsValue(){
		oldRpsHostName = getRPSHostName();
		oldRpsUserName = rpsUsernameField.getValue();
		oldRpsPassword = rpsPasswordField.getValue();
		oldRpsProtocol = httpProtocolRadio.getValue();
		oldRpsPort = rpsPortField.getValue().intValue();
	}
	
	public boolean validate(boolean forSave) {
		boolean isValid = true;
		validationError = null;
		
		if(isValid){
			if(isEmpty(getRPSHostName())){
				validationError = UIContext.Messages
						.messageBoxTitleError(UIContext.Messages.backupRPSDestHostNameIsNull(UIContext.productNameRPS));
				isValid = false;
			}
		}
		
		if(isValid){
			if(isEmpty(rpsUsernameField.getValue())){
				validationError = UIContext.Messages
						.messageBoxTitleError(UIContext.Messages.backupRPSDestUserNameIsNull(UIContext.productNameRPS));
				isValid = false;
			}
		}
		
		if(isValid){
			if(isEmpty(rpsPasswordField.getValue())){
				validationError = UIContext.Messages
						.messageBoxTitleError(UIContext.Messages.backupRPSDestPwdIsNull(UIContext.productNameRPS));
				isValid = false;
			}
		}
		
		if(isValid){
			if(rpsPortField.getValue()==null){
				validationError = UIContext
						.Messages.messageBoxTitleError(UIContext.Constants.portCannotBeBlank());
				isValid = false;
			}
		}
		
		if(isValid && forSave){
			if(rpsDataStore.getValue() == null){
				validationError = UIContext
						.Messages.messageBoxTitleError(
								UIContext.Messages.restoreRPSDataStoreCannotBeBlank(UIContext.productNameRPS));
				isValid = false;
			}
		}
		return isValid;
	}
	
	protected boolean validateRPSConfig() {
		boolean isValid = validate(false);
		if(!isValid){
			popupError(validationError);
		}
		return isValid;
	}
	
	public void popupError(String message){
		MessageBox msg = new MessageBox();
		msg.setIcon(MessageBox.ERROR);
		msg.setTitleHtml(Utils.getProductName());
		msg.setMessage(message);
		msg.setModal(true);
		msg.show();
	}
	
	public String getValidationError(){
		return validationError;
	}
	
	protected void showDataStoreNameChangeMsg(String oldName,String newName){
		showMessage(UIContext.Messages.rpsDataStoreNameChanged(oldName, newName),
				MessageBox.WARNING);
	}
	
	protected void showDataStoreNotExistMsg(String policyName){
		showMessage(UIContext.Messages.rpsDataStoreNotExist(
				policyName,getRPSHostName(), UIContext.productNameRPS),
				MessageBox.ERROR);
	}
	
	private void showMessage(String message, String icon){
		MessageBox box = new MessageBox();
		box.setTitleHtml(UIContext.productNameD2D);
		box.setButtons(MessageBox.OK);
		box.setIcon(icon);
		box.setModal(true);
		box.setMessage(message);
		box.show();
	}
	
	protected boolean isEmpty(final String value) {
		return value == null || value.isEmpty();
	}

	public void refreshData(BackupRPSDestSettingsModel rpsDestModel, 
			List<RpsPolicy4D2DRestoreModel> rpsPolicyList) {
		if(initialized)
			return;
		if(rpsDestModel == null)
			return ;
	
//		currentModel = rpsDestModel;
		this.setRpsHostValue(rpsDestModel.rpsHost);		
		setRpsServerValue(rpsDestModel.rpsHost);
		setOldRpsValue();
		if (rpsDestModel.getRPSDataStoreName()!=null && rpsDestModel.getRPSDataStoreUUID() !=null
				&& !rpsDestModel.getRPSDataStoreName().equals("") && !rpsDestModel.getRPSDataStoreUUID().equals("")){
			RpsPolicy4D2DRestoreModel policyModel = new RpsPolicy4D2DRestoreModel();
			policyModel.setDataStoreDisplayName(rpsDestModel.getRPSDataStoreName());
			policyModel.setDataStoreName(rpsDestModel.getRPSDataStoreUUID());
			dsStore.add(policyModel);
			rpsDataStore.setValue(policyModel);
			updateRPSPolicy(rpsPolicyList);
		}
	}
	
	public BackupRPSDestSettingsModel saveData(){
		BackupRPSDestSettingsModel model = new BackupRPSDestSettingsModel();
		model.rpsHost = new RpsHostModel();
		
		String hostName = getRPSHostName();
		model.rpsHost.setHostName(hostName);
		model.rpsHost.setUserName(rpsUsernameField.getValue());
		model.rpsHost.setPassword(rpsPasswordField.getValue());
		model.rpsHost.setPort(rpsPortField.getValue().intValue());		
		model.rpsHost.setIsHttpProtocol(httpProtocolRadio.getValue());

		model.setRPSDataStoreName(rpsDataStore.getValue().getDataStoreDisplayName());
		model.setRPSDataStoreUUID(rpsDataStore.getValue().getDataStoreName());	
		return model;
	}
	
	protected void updateRPSPolicy(List<RpsPolicy4D2DRestoreModel> rpsPolicies) {
		if(rpsPolicies != null) {
			dataStoreUpdated(rpsPolicies);
		}else {
			thisWindow.mask(UIContext.Messages.loadingDataStore(UIContext.productNameRPS));
			String protocol = httpProtocolRadio.getValue()?"HTTP:":"HTTPS:" ;
			configRPSInD2DService.getRPSPolicyList4Restore(getRPSHostName(), 
					rpsUsernameField.getValue(), rpsPasswordField.getValue(), 
					rpsPortField.getValue().intValue(), protocol,
					new BaseAsyncCallback<List<RpsPolicy4D2DRestoreModel>>(){
				@Override
				public void onFailure(Throwable caught) {
					thisWindow.unmask();
					super.onFailure(caught);
					initialized = true;
				}
				
				@Override
				public void onSuccess(List<RpsPolicy4D2DRestoreModel> result) {
					thisWindow.unmask();
					dataStoreUpdated(result);
				}
			});
		}
	}
	
	private void dataStoreUpdated(List<RpsPolicy4D2DRestoreModel> rpsDSs) {
		initialized= true;
		//isPolicyLoad = true;
		RpsPolicy4D2DRestoreModel currentModel = rpsDataStore.getValue();
		rpsDataStoreList = rpsDSs;
		if(rpsDSs == null || rpsDSs.size()==0){
			rpsDataStoreList= null;
			dsStore.removeAll();
			rpsDataStore.clear();
			showDataStoreNotExistMsg(currentModel.getDataStoreDisplayName());
		}else{
			boolean isExist = false;
			boolean isNameChanged = false;
			dsStore.removeAll();
			this.isDataStoreLoaded = true;
			String oldDataStoreName = currentModel.getDataStoreDisplayName();
			for(RpsPolicy4D2DRestoreModel model : rpsDSs){
				if(model.getDataStoreName().equals(currentModel.getDataStoreName())){
					isExist = true;
					currentModel.copy(model);
					if(!model.getDataStoreDisplayName().equals(oldDataStoreName)){
						dsStore.removeAll();
						isNameChanged = true;
						currentModel.setDataStoreDisplayName(model.getDataStoreDisplayName());
						dsStore.add(currentModel);
						rpsDataStore.setValue(currentModel);
						break;
					}
				}
			}
			dsStore.add(rpsDSs);
			rpsDataStoreList = rpsDSs;
			if(!isExist){
				rpsDataStore.setValue(null);
				showDataStoreNotExistMsg(currentModel.getDataStoreDisplayName());
			}
			if(isNameChanged){
				showDataStoreNameChangeMsg(oldDataStoreName,currentModel.getDataStoreDisplayName());
			}
			restoreWindow.onPolicySelectionChanged(currentModel);
		}
	}

	protected void refreshDataStoreList() {
		setOldRpsValue();
		initialized = false;
		thisWindow.mask(UIContext.Messages.loadingDataStore(UIContext.productNameRPS));
		String protocol = httpProtocolRadio.getValue()?"HTTP:":"HTTPS:" ;
		configRPSInD2DService.getRPSPolicyList4Restore(getRPSHostName()
				, rpsUsernameField.getValue(), rpsPasswordField.getValue()
				, rpsPortField.getValue().intValue(), protocol,
				new BaseAsyncCallback<List<RpsPolicy4D2DRestoreModel>>(){
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				rpsDataStoreList= null;
				dsStore.removeAll();
				rpsDataStore.clear();
				thisWindow.unmask();
				initialized = true;
			}

			@Override
			public void onSuccess(List<RpsPolicy4D2DRestoreModel> result) {
				initialized = true;
				if(result==null||result.isEmpty()){
					MessageBox messageBox = new MessageBox();
					messageBox.addCallback(new Listener<MessageBoxEvent>(){

						@Override
						public void handleEvent(MessageBoxEvent be) {
							rpsDataStoreList= null;
							dsStore.removeAll();
							rpsDataStore.clear();
							thisWindow.unmask();
						}
						
					});
					
					messageBox.setTitleHtml(Utils.getProductName());
					messageBox.setMessage(UIContext.Constants.restoreEmptyInfo());
					messageBox.setIcon(MessageBox.WARNING);
					messageBox.setModal(true);
					messageBox.setMinWidth(400);
					Utils.setMessageBoxDebugId(messageBox);
					messageBox.show();
					return;
				}
				isDataStoreLoaded = true;

				RpsPolicy4D2DRestoreModel currentDataStore = rpsDataStore.getValue();
				dsStore.removeAll();
				boolean isExist = false;
				for (RpsPolicy4D2DSettings dataStore : result) {
					if(currentDataStore!=null && dataStore.getDataStoreName()
							.equals(currentDataStore.getDataStoreName())){
						isExist = true;
					}
				}
				rpsDataStoreList = result;
				dsStore.add(result);
				if(currentDataStore == null || !isExist){
					rpsDataStore.setValue(result.get(0));
				}else{
					rpsDataStore.setValue(currentDataStore);
				}
				thisWindow.unmask();
				rpsDataStore.focus();
				rpsDataStore.expand();
			}
		});
	}

	public SelectionChangedListener<RpsPolicy4D2DRestoreModel> setupRPSDataStoreChangeListener() {
		return new SelectionChangedListener<RpsPolicy4D2DRestoreModel>() {
			
			@Override
			public void selectionChanged(
					SelectionChangedEvent<RpsPolicy4D2DRestoreModel> se) {
				if(!isDataStoreLoaded || rpsDataStoreList == null || rpsDataStoreList.size()==0)
					return;
				
				restoreWindow.onPolicySelectionChanged(se.getSelectedItem());
			}
		};
	}	

	protected Listener<BaseEvent> setUpHostNameChangeListener() {
		return new Listener<BaseEvent>(){

			@Override
			public void handleEvent(BaseEvent be) {
				if(oldRpsHostName != null 
						&& !oldRpsHostName.equals(rpsHostnameField.getValue())){
					refreshDataStoreList();
					restoreWindow.clearD2DList();
				}
			}
		};
	}

	public String getRPSHost() {
		return getRPSHostName();
	}
}
