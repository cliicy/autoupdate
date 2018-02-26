package com.ca.arcflash.ui.client.common;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.model.BackupRPSDestSettingsModel;
import com.ca.arcflash.ui.client.model.rps.RpsHostModel;
import com.ca.arcflash.ui.client.model.rps.RpsPolicy4D2DSettings;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

public class SelectRPSSettingsPanel<T extends RpsPolicy4D2DSettings> extends LayoutContainer {
	protected int MIN_FIELD_WIDTH = 250;
	protected static final int DEFAULT_HTTP_PORT = 8014;
	
	protected final ICommonRPSService4D2DAsync configRPSInD2DService = GWT.create(ICommonRPSService4D2D.class);
	
	protected BaseComboBox<T> rpsPolicy = new BaseComboBox<T>();
	protected boolean isPolicyLoaded = false;
	protected HorizontalPanel policyWarningOrError;
//	private LayoutContainer destChangedBackupTypeContForRPS;
	
	protected TextField<String> rpsHostnameField;
	protected TextField<String> rpsUsernameField;
	protected PasswordTextField rpsPasswordField;
	protected NumberField rpsPortField;
	protected Radio httpProtocolRadio;
	protected Radio httpsProtocolRadio;
	
	protected ListStore<T> policyStore = new ListStore<T>();
	protected List<T> rpsPolicyList = new ArrayList<T>();

	protected LoadingStatus loading;
	//details
	protected DisclosurePanel policyDetailPanel;	
	protected LabelField detailDataStoreName;
	protected LabelField detailDedupe;
	protected LabelField detailCompression;
	protected LabelField detailEncryption;
	protected LabelField detailReplication;
	protected LabelField detailRetentionCount;
	private LabelField labelDailyCount;
	protected LabelField detailDailyCount;
	private LabelField labelWeeklyCount;
	protected LabelField detailWeeklyCount;
	private LabelField labelMonthlyCount;
	protected LabelField detailMonthlyCount;

	protected String oldRpsHostName;
	private String oldRpsUserName;
	private String oldRpsPassword;
	private Boolean oldRpsProtocol;
	private int oldRpsPort;
	
	private String validationError;
	protected SelectRPSSettingsPanel<T> thisWindow;
	
	protected BackupRPSDestSettingsModel currentModel;
	protected Button refreshButton;
	
	public SelectRPSSettingsPanel(){
		this(250);
	}	
	
	public SelectRPSSettingsPanel(int fieldWidth){
		this.MIN_FIELD_WIDTH = fieldWidth;
		thisWindow = this;		
		setWidth("97%");
		add(initRpsServerSettingFieldSet());
//		add(initRpsWarningContainer());
	}
	
	private void addPolicyDetailPanel(LayoutContainer container) {
		policyDetailPanel = Utils.getDisclosurePanel(UIContext.Constants.policyDetails());
		policyDetailPanel.setWidth("100%");
		TableData tableData = new TableData();
		tableData.setColspan(2);
		
		TableData left = new TableData();
		left.setWidth("50%");
		TableData right = new TableData();
		right.setWidth("50%");
		
		LayoutContainer policySettingsContainer = new LayoutContainer();
		policySettingsContainer.ensureDebugId("016bbd2c-7c74-4c13-8a61-8eb03fd38c6c");
		policySettingsContainer.setLayout(new TableLayout(2));
		policySettingsContainer.setStyleAttribute("margin-left", "0px");
		LabelField label = new LabelField(UIContext.Constants.rpsDedupInstName()+":");;
		detailDataStoreName = new LabelField();		
		policySettingsContainer.add(label, left);
		policySettingsContainer.add(detailDataStoreName, right);
						
		label = new LabelField(UIContext.Constants.rpsDsDedupEnable()+":");
		policySettingsContainer.add(label, left);
		detailDedupe = new LabelField();
		policySettingsContainer.add(detailDedupe, right);
				
		label = new LabelField(UIContext.Constants.settingsLabelCompression()+":");
		policySettingsContainer.add(label, left);
		detailCompression = new LabelField();
		policySettingsContainer.add(detailCompression, right);
				
		label = new LabelField(UIContext.Constants.settingsLabelEncryption()+":");
		policySettingsContainer.add(label, left);
		detailEncryption = new LabelField();
		policySettingsContainer.add(detailEncryption, right);
				
		label = new LabelField(UIContext.Constants.rpsSettingsReplicationEnable()+":");
		policySettingsContainer.add(label, left);
		detailReplication = new LabelField();
		policySettingsContainer.add(detailReplication, right);
		
		label = new LabelField(UIContext.Constants.rpsRecoveryPoints()+":");
		policySettingsContainer.add(label, left);
		detailRetentionCount = new LabelField();
		policySettingsContainer.add(detailRetentionCount, right);
		
		labelDailyCount = new LabelField(UIContext.Constants.rpsDailyRecoveryPoints()+":");
		policySettingsContainer.add(labelDailyCount, left);
		detailDailyCount = new LabelField();
		policySettingsContainer.add(detailDailyCount, right);
		setDailyCountVisible(false);
		
		labelWeeklyCount = new LabelField(UIContext.Constants.rpsWeeklyRecoveryPoints()+":");
		policySettingsContainer.add(labelWeeklyCount, left);
		detailWeeklyCount = new LabelField();
		policySettingsContainer.add(detailWeeklyCount, right);
		setWeeklyCountVisible(false);
		
		labelMonthlyCount = new LabelField(UIContext.Constants.rpsMonthlyRecoveryPoints()+":");
		policySettingsContainer.add(labelMonthlyCount, left);
		detailMonthlyCount = new LabelField();
		policySettingsContainer.add(detailMonthlyCount, right);
		setMonthlyCountVisible(false);
		
		policyDetailPanel.add(policySettingsContainer);
		policyDetailPanel.setWidth("100%");
		container.add(policyDetailPanel, tableData);
		policyDetailPanel.setVisible(false);
	}
	
	protected void setDailyCountVisible(boolean visible){
		labelDailyCount.setVisible(visible);
		detailDailyCount.setVisible(visible);
	}
	
	protected void setWeeklyCountVisible(boolean visible){
		labelWeeklyCount.setVisible(visible);
		detailWeeklyCount.setVisible(visible);
	}
	
	protected void setMonthlyCountVisible(boolean visible){
		labelMonthlyCount.setVisible(visible);
		detailMonthlyCount.setVisible(visible);
	}
	
	private void addRpsPolicyRow(LayoutContainer container) {		
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.policy());
		//container.add(label);
		
		HorizontalPanel panel = new HorizontalPanel();
		panel.setWidth("100%");
				
		rpsPolicy.setTriggerAction(TriggerAction.ALL);
		rpsPolicy.setStore(policyStore);
		rpsPolicy.setEditable(false);
		rpsPolicy.setDisplayField("policyName");
		rpsPolicy.setTemplate(getCommonBoxTemplate(rpsPolicy.getDisplayField()));
		rpsPolicy.setWidth(MIN_FIELD_WIDTH);
		rpsPolicy.ensureDebugId("850bdfef-fe2e-4f7b-b041-5f0e2f55da1a");
		rpsPolicy.addListener(Events.OnMouseDown, this.setupRPSPolicyMouseListener());		
		panel.add(rpsPolicy);
		
		refreshButton = new Button(UIContext.Constants.policyRefresh());
		refreshButton.ensureDebugId("8DCF4186-394E-42F7-AA7B-CA256CDABD0C");
		refreshButton.setMinWidth(80);
		refreshButton.setStyleAttribute("margin-left", "3px");
		refreshButton.addSelectionListener(this.setupRefreshButtonListener());
		panel.add(refreshButton);
	//	container.add(panel);			
	}

	private native String getCommonBoxTemplate(String displayName) /*-{  
    return  [  
    '<tpl for=".">',  
    '<div class="x-combo-list-item" qtip="{'+displayName+'}">{'+displayName+'}</div>',  
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
	

	private void addPolicyLoadingStatus(LayoutContainer container){
		container.add(new HTML(""));
		loading = new LoadingStatus();
		loading.setMsgLabelStyleName("update_rps_policy_loading_label");
		loading.setLoadingMsg(UIContext.Messages.rpsPolicyUpdatingPolicy(UIContext.productNameRPS));
		loading.setVisible(false);
		container.add(loading);
	}

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
		tableData.setWidth("15%");
		rpsServerSettingContainer.add(label, tableData);
		addRpsHostField(rpsServerSettingContainer);
	}
	/**
	 * For D2D, it's not a text field, for Central apps, it's combox loaded from current RPS servers.
	 * @param rpsServerSettingContainer
	 */
	protected void addRpsHostField(LayoutContainer rpsServerSettingContainer){
		rpsHostnameField = new TextField<String>();
		rpsHostnameField.setWidth(MIN_FIELD_WIDTH);
		rpsHostnameField.ensureDebugId("f947e33f-c15e-4ac9-ab98-1f07f5779fc6");
		rpsHostnameField.setAllowBlank(false);
		/*rpsHostnameField.addListener(Events.Blur, new Listener<BaseEvent>(){

			@Override
			public void handleEvent(BaseEvent be) {
				if(oldRpsHostName != null 
						&& !oldRpsHostName.equals(rpsHostnameField.getValue())){
					refreshPolicyList();
				}
			}
		});*/
		rpsHostnameField.addListener(Events.Blur, this.setUpHostNameChangeListener());

		TableData tableData = new TableData();
		tableData.setWidth("85%");
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
		rpsPortField.setWidth(MIN_FIELD_WIDTH);
		rpsPortField.ensureDebugId("4ac89556-3d26-416e-9013-f0ebfad4c4cd");
		rpsPortField.setValue(DEFAULT_HTTP_PORT);
		rpsPortField.setAllowBlank(false);
		rpsPortField.setAllowDecimals(false);
		rpsPortField.setAllowNegative(false);
		rpsServerSettingContainer.add(rpsPortField);
	}
	
	private LayoutContainer createDestBaseTableContainer(){
		LayoutContainer destCommonContainer = new LayoutContainer();
		TableLayout rpsBackupDestSettingsLayout = new TableLayout();
		rpsBackupDestSettingsLayout.setColumns(2);
		rpsBackupDestSettingsLayout.setCellPadding(4);
		rpsBackupDestSettingsLayout.setCellSpacing(0);
		rpsBackupDestSettingsLayout.setWidth("95%");
		destCommonContainer.setLayout(rpsBackupDestSettingsLayout);
		return destCommonContainer;
	}

	private FieldSet initRpsServerSettingFieldSet() {
		FieldSet rpsServerSettingFieldSet = new FieldSet();
		rpsServerSettingFieldSet.setWidth("98%");
		rpsServerSettingFieldSet.setHeadingHtml(UIContext.Messages.rpsServerSetting(UIContext.productNameRPS));
		rpsServerSettingFieldSet.ensureDebugId("35B4AAE5-BA7D-458b-8F92-86FAFD13C0BA");
		
		LayoutContainer rpsServerSettingContainer = createDestBaseTableContainer();
		
		addRpsHostnameRow(rpsServerSettingContainer);
		addRpsUsernameRow(rpsServerSettingContainer);
		addRpsPasswordRow(rpsServerSettingContainer);
		addRpsPortRow(rpsServerSettingContainer);
		addRpsProtocolRow(rpsServerSettingContainer);	
		addRpsPolicyRow(rpsServerSettingContainer);
		addPolicyLoadingStatus(rpsServerSettingContainer);
		addPolicyDetailPanel(rpsServerSettingContainer);
		rpsServerSettingFieldSet.add(rpsServerSettingContainer);
		rpsServerSettingFieldSet.add(initRpsWarningContainer());
		
		return rpsServerSettingFieldSet;
	}
	
	private HorizontalPanel initRpsWarningContainer(){
		policyWarningOrError = new HorizontalPanel();
		policyWarningOrError.setWidth("98%");
		policyWarningOrError.setVisible(false);
		policyWarningOrError.setLayoutOnChange(true);
		return policyWarningOrError;
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
	
	public Listener<FieldEvent> setupRPSPolicyMouseListener(){
		return new Listener<FieldEvent>(){
			@Override
			public void handleEvent(FieldEvent be) {
				if(!validateRPSConfig())
					return;
				if(isPolicyLoaded&&!isRpsChanged())
					return;
				refreshPolicyList();
			}
		};
	}
	
	protected String getCompressionLevel(RpsPolicy4D2DSettings ds){
		if(ds.getEnableCompression()==null || ds.getEnableCompression()==false){
			return UIContext.Constants.settingsCompressionNone();
		}else{
			if(ds.getCompressionMethod() == 1){
				return UIContext.Constants.settingsCompreesionStandard();
			}else{
				return UIContext.Constants.settingsCompressionMax();
			}
		}
	}

	public SelectionListener<ButtonEvent> setupRefreshButtonListener(){ 
		return new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				if(validateRPSConfig())
					refreshPolicyList();
			}
		};
	}
	
	protected String getRPSHostName(){
		return rpsHostnameField.getValue();
	}
	
	public void refreshData(BackupRPSDestSettingsModel rpsDestModel){
		if(rpsDestModel == null)
			return ;
	
		currentModel = rpsDestModel;
		this.setRpsHostValue(rpsDestModel.rpsHost);		
		setRpsServerValue(rpsDestModel.rpsHost);
		setOldRpsValue();
		if (rpsDestModel.getRpsPolicy()!=null && rpsDestModel.getRpsPolicyUUID() !=null
				&& !rpsDestModel.getRpsPolicy().equals("") && !rpsDestModel.getRpsPolicyUUID().equals("")){
			T policyModel = (T) new RpsPolicy4D2DSettings();
			policyModel.setPolicyName(rpsDestModel.getRpsPolicy());
			policyModel.setId(rpsDestModel.getRpsPolicyUUID());
			policyStore.add(policyModel);
			rpsPolicy.setValue(policyModel);
			updateRPSPolicy();
		}
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
	
	protected void updateRPSPolicy() {
		
	}
	
	protected void refreshPolicyList(){
		
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
			if(rpsPolicy.getValue() == null){
				validationError = UIContext
						.Messages.messageBoxTitleError(
								UIContext.Messages.backupRPSPolicyCannotBeBlank(UIContext.productNameRPS));
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
	
	protected void showPolicyNameChangeMsg(String oldName,String newName){
		policyWarningOrError.removeAll();
		
		Label label = new Label(UIContext.Messages.rpsPolicyNameChanged(oldName, newName));
		label.setStyleName("update_rps_policy_loading_label");
		Image warningImage = AbstractImagePrototype.create(UIContext.IconBundle.status_small_warning()).createImage();
		policyWarningOrError.add(warningImage);
		policyWarningOrError.add(label);
		policyWarningOrError.setVisible(true);
	}
	
	protected void showPolicyNotExistMsg(String policyName){
		policyWarningOrError.removeAll();
		Label label = new Label(UIContext.Messages.rpsPolicyNotExist(policyName,getRPSHostName(), UIContext.productNameRPS));
		label.setStyleName("update_rps_policy_loading_label");
		Image errorImage = AbstractImagePrototype.create(UIContext.IconBundle.status_small_error()).createImage();
		policyWarningOrError.add(errorImage);
		policyWarningOrError.add(label);
		policyWarningOrError.setVisible(true);
	}
	
	protected boolean isEmpty(final String value) {
		return value == null || value.isEmpty();
	}
	
	protected Listener<BaseEvent> setUpHostNameChangeListener(){
		return new Listener<BaseEvent>(){

			@Override
			public void handleEvent(BaseEvent be) {
				if(oldRpsHostName != null 
						&& !oldRpsHostName.equals(rpsHostnameField.getValue())){
					refreshPolicyList();
				}
			}
		};
	}
}
