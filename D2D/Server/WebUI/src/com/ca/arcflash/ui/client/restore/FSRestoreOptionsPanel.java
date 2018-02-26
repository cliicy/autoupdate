package com.ca.arcflash.ui.client.restore;

import java.util.List;
import java.util.Map;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.PasswordTextField;
import com.ca.arcflash.ui.client.common.PathSelectionPanel;
import com.ca.arcflash.ui.client.common.UserPasswordWindow;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.ArchiveDestinationModel;
import com.ca.arcflash.ui.client.model.ArchiveGridTreeNode;
import com.ca.arcflash.ui.client.model.ArchiveSettingsModel;
import com.ca.arcflash.ui.client.model.BackupVMModel;
import com.ca.arcflash.ui.client.model.CatalogItemModel;
import com.ca.arcflash.ui.client.model.DestType;
import com.ca.arcflash.ui.client.model.ESXServerModel;
import com.ca.arcflash.ui.client.model.EncrypedRecoveryPoint;
import com.ca.arcflash.ui.client.model.EncrypedRecoveryPoint.VerifyStatus;
import com.ca.arcflash.ui.client.model.FileSystemOptionModel;
import com.ca.arcflash.ui.client.model.GridTreeNode;
import com.ca.arcflash.ui.client.model.RestoreArchiveJobModel;
import com.ca.arcflash.ui.client.model.RestoreJobModel;
import com.ca.arcflash.ui.client.model.SummaryDataModel;
import com.ca.arcflash.ui.client.model.VMItemModel;
import com.ca.arcflash.ui.client.model.VirtualCenterModel;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
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
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class FSRestoreOptionsPanel extends RestoreOptionsPanel {
	
	final LoginServiceAsync service = GWT.create(LoginService.class);
	
	protected PasswordPane archivePwdPane = new PasswordPane();
	private Radio overwrite;
	private Radio rename;
	private Radio skip;
	private CheckBox replace;
	private CheckBox baseFolder;

	private Radio originalLocation;
	private Radio sameVMAlternateLocation;
	private Radio alternateLocation;
	private Radio alternateVMLocation;
	private RadioGroup locationGroup;
	
	private Radio httpProtocol;
	private Radio httpsProtocol;
	private RadioGroup protocolGroup;
	
	private HTML orginalLocationLabel;
	
	private TextField<String> vcTextField;
	private NumberField portTextField;
	private TextField<String> usernameTextField;
	private PasswordTextField passwordTextField;
	private LayoutContainer vcContainer;
	
	private LayoutContainer vmContainer;
	private ComboBox<ESXServerModel> esxList;
	private ComboBox<VMItemModel> vmList;
	private TextField<String> vmUsernameTextField;
	private PasswordTextField vmPasswordTextField;
	private PathSelectionPanel pathSelectionForOtherVM;
	private Button connectToVC;
	
	//private HTML orginalLocationLabel;

	private PathSelectionPanel pathSelection;

	private FSRestoreOptionsPanel thisPanel;
	public static final String VolumeDisplayPrefix = "\\\\?\\Volume{";
	public static final String DriveWithoutDriveLetter_Suffix = "(\\\\\\\\\\x3f\\\\)?Volume\\x7b[A-Z0-9-]+\\x7d.*";
	public static final int HAS_DIRVER_LETER = 1;

	public FSRestoreOptionsPanel(RestoreWizardContainer restoreWizardWindow) {      ///D2D Lite Integration
		super(restoreWizardWindow);
		thisPanel = this;
		archivePwdPane.setDebugID("8F33C66C-6AA9-4a3a-9532-34AF3888ABF1");
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		//setStyleAttribute("margin", "10px");

		TableLayout tl = new TableLayout();
		tl.setWidth("100%");
		tl.setColumns(3);
		tl.setCellPadding(4);
		tl.setCellSpacing(0);
		this.setLayout(tl);

		TableData td = new TableData();
		td.setColspan(3);

		// Header Section
		this.add(renderHeaderSection(), td);

		// Destination Section
		Label label = new Label(UIContext.Constants.restoreDestination());
		label.addStyleName("restoreWizardSubItem");
		this.add(label, td);

		label = new Label(UIContext.Constants.restoreDestinationDescription());
		label.addStyleName("restoreWizardSubItemDescription");
		this.add(label, td);
		

		originalLocation = new Radio();
		originalLocation.ensureDebugId("0EE524EA-690B-46af-9B1D-998A50AB9F6F");
		originalLocation.setBoxLabel(UIContext.Constants
				.restoreToOriginalLocation());
		Utils.addToolTip(originalLocation, UIContext.Constants
				.restoreToOriginalLocationTooltip());


		originalLocation.addStyleName("restoreWizardLeftSpacing");
		this.add(originalLocation, td);
		
		orginalLocationLabel = new HTML(UIContext.Messages.restoreToOriginalLocationForVSphere(UIContext.productNameD2D));
		orginalLocationLabel.addStyleName("restoreWizardSubItemDescription");
		orginalLocationLabel.setVisible(false);
		this.add(orginalLocationLabel,td);
		
		sameVMAlternateLocation = new Radio();
		sameVMAlternateLocation.setVisible(false);
		sameVMAlternateLocation.setBoxLabel(UIContext.Constants
				.vmFileRestoreToSameVMAlternamteLocation());
		Utils.addToolTip(sameVMAlternateLocation, UIContext.Constants
				.vmFileRestoreToSameVMAlternamteLocationToolTip());
		
		sameVMAlternateLocation.addStyleName("restoreWizardLeftSpacing");
		this.add(sameVMAlternateLocation, td);
		
		alternateVMLocation = new Radio();
		alternateVMLocation.setBoxLabel(UIContext.Constants.vmFileRestoreToAlternateVM());
		Utils.addToolTip(alternateVMLocation, UIContext.Constants
				.vmFileRestoreToAlternateVMToolTip());


		alternateVMLocation.addStyleName("restoreWizardLeftSpacing");
		alternateVMLocation.setVisible(false);
		alternateVMLocation.addListener(Events.Change,
			new Listener<FieldEvent>() {

				@Override
				public void handleEvent(FieldEvent be) {
					vcContainer.setVisible(alternateVMLocation.getValue());
					vmContainer.setVisible(alternateVMLocation.getValue());
				}
		});
		this.add(alternateVMLocation, td);
		if(backupVMModel != null){
			alternateVMLocation.setVisible(true);
			sameVMAlternateLocation.setVisible(true);
			vcContainer = renderVC();
			vcContainer.setVisible(false);
			this.add(vcContainer,td);
			
			vmContainer = renderVM();
			vmContainer.setVisible(false);
			this.add(vmContainer,td);
			
		}
		alternateLocation = new Radio();
		alternateLocation.ensureDebugId("12BD4BA8-AA7A-49ff-BD34-35D8C9BAD740");
		alternateLocation.setBoxLabel(UIContext.Constants.restoreTo());
		Utils.addToolTip(alternateLocation, UIContext.Constants.restoreToTooltip());
		alternateLocation.addListener(Events.Change,
				new Listener<FieldEvent>() {

					@Override
					public void handleEvent(FieldEvent be) {

						Boolean checked = thisPanel.alternateLocation
								.getValue();
						pathSelection.setEnabled(checked);
						baseFolder.setEnabled(checked);
					}

				});
		alternateLocation.addStyleName("restoreWizardLeftSpacing");
		this.add(alternateLocation);

		locationGroup = new RadioGroup();
		locationGroup.setOrientation(Orientation.VERTICAL);
		locationGroup.add(originalLocation);
		locationGroup.add(alternateLocation);
		locationGroup.add(alternateVMLocation);
		locationGroup.add(sameVMAlternateLocation);

		pathSelection = new PathSelectionPanel(null);
		pathSelection.setWidth("450");
		pathSelection.setMode(PathSelectionPanel.RESTORE_ALT_MODE);
		pathSelection
				.setTooltipMode(PathSelectionPanel.TOOLTIP_RESTORE_ALT_MODE);
//		pathSelection.setAllowBlank(false);
		pathSelection.setPathFieldLength(290);
		pathSelection.setChangeListener(new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {

			}
		});
		pathSelection.addListener(PathSelectionPanel.onDisconnectionEvent,
				new Listener<BaseEvent>() {

					@Override
					public void handleEvent(BaseEvent be) {

					}

				});
		pathSelection.addStyleName("restoreWizardLeftSpacing");
		pathSelection.addDebugId("C5E3C4F2-BBA8-4b18-927B-CD92B9D09C66", 
				"D4618E33-DF59-4527-A913-48C11FE124CB", 
				"201E49F2-FBAF-4854-A438-E1DC8E5CCD37");
		TableData twoColspan = new TableData();
		twoColspan.setColspan(2);
		this.add(pathSelection, twoColspan);

		this.add(new Html("<HR>"), td);

		// Resolving Conflicts Section
		
		this.add(getResolveConflictContainer(), td);
		
		this.add(new Html("<HR>"), td);

		setDefaults();
		
		originalLocation.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {

				/*Boolean checked = thisPanel.originalLocation.getValue();
				pathSelection.setEnabled(!checked);
				baseFolder.setEnabled(!checked);
				if (!baseFolder.isEnabled())
					baseFolder.setValue(false);
				if (checked) {
					if (pathSelection.getDestinationTextField() != null) {
						pathSelection.getDestinationTextField().clearInvalid();
					}
				}*/
			}
		});
		
		this.add(pwdPane, td);
		
		archivePwdPane.setEncryptTitle(new Label(UIContext.Constants.archiveEncryptionPassword()));
		this.add(archivePwdPane,td);
		archivePwdPane.setVisible(false);
		this.add(recoveryPointPasswordPanel, td);
	}
	
	protected LayoutContainer getResolveConflictContainer(){
		LayoutContainer container = new LayoutContainer();
		TableLayout tLayout = new TableLayout();
		tLayout.setWidth("100%");
		tLayout.setColumns(2);		
		container.setLayout(tLayout);
		
		VerticalPanel conflictPanel = new VerticalPanel();
		Label label = new Label(UIContext.Constants.restoreResolvingConflicts());
		label.addStyleName("restoreWizardSubItem");
		conflictPanel.add(label);

		String productName = UIContext.productNameD2D;
		if(UIContext.uiType == Utils.UI_TYPE_VSPHERE){
			productName = UIContext.productNamevSphere;
		}
		label = new Label(UIContext.Messages.resolvingConflictsDescription());
		label.addStyleName("restoreWizardSubItemDescription");
		label.addStyleName("restoreWizardTopSpacing");
		conflictPanel.add(label);
		
		RadioGroup radioGroup = new RadioGroup();
		
		overwrite = new Radio();
		overwrite.ensureDebugId("52401560-2395-4cd7-99A1-337739B4ED83");
		overwrite.setValue(true);
		overwrite.setBoxLabel(UIContext.Constants.restoreConflictOverwrite());
		Utils.addToolTip(overwrite, UIContext.Constants.restoreConflictOverwriteTooltip());
		overwrite.addStyleName("restoreWizardLeftSpacing");
//		overwrite.setStyleAttribute("padding-top", "8px");
		conflictPanel.add(overwrite);
		radioGroup.add(overwrite);

		replace = new CheckBox();
		replace.ensureDebugId("69F7C6C8-2D24-4dcc-9968-7FD405B2D62D");
		replace.setStyleAttribute("padding-left", "35px");
		replace.setBoxLabel(UIContext.Constants.restoreConflictReplace());
		Utils.addToolTip(replace, UIContext.Constants.restoreConflictReplaceTooltip());
//		replace.setStyleAttribute("padding-top", "6px");
		replace.addStyleName("restoreWizardLeftSpacing");
		conflictPanel.add(replace);
		
		rename = new Radio();
		rename.ensureDebugId("F2791EAD-D404-43dc-92C5-63ACAAE445AE");
		rename.setBoxLabel(UIContext.Constants.restoreConflictRename());
		Utils.addToolTip(rename, UIContext.Constants.restoreConflictRenameTooltip());
		rename.addStyleName("restoreWizardLeftSpacing");
//		rename.setStyleAttribute("padding-top", "12px");
		conflictPanel.add(rename);
		radioGroup.add(rename);
		
		skip = new Radio();
		skip.ensureDebugId("4A837B0F-3143-402e-932C-81F38C0A1B10");
		skip.setBoxLabel(UIContext.Constants.restoreConflictSkip());
		Utils.addToolTip(skip, UIContext.Constants.restoreConflictSkipTooltip());
		skip.addStyleName("restoreWizardLeftSpacing");
//		skip.setStyleAttribute("padding-top", "12px");
		conflictPanel.add(skip);
		radioGroup.add(skip);
		
		radioGroup.setValue(overwrite);
//		this.add(conflictPanel, td);
		TableData data = new TableData();
		data.setWidth("50%");
		container.add(conflictPanel, data);
		
		VerticalPanel structurePanel = new VerticalPanel();
		
		label = new Label(UIContext.Constants.restoreDirectoryStructure());
		label.addStyleName("restoreWizardSubItem");
//		this.add(label, td);
		structurePanel.add(label);

		label = new Label(UIContext.Constants
				.restoreDirectoryStructureDescription());
		label.addStyleName("restoreWizardSubItemDescription");
		label.addStyleName("restoreWizardTopSpacing");
//		this.add(label, td);
		structurePanel.add(label);

		baseFolder = new CheckBox();
		baseFolder.ensureDebugId("96D0E4F1-D1DE-4532-A146-6F84BE8CE23E");
		baseFolder.setBoxLabel(UIContext.Constants
				.restoreConflictBaseFolderWillNotBeCreated());
		Utils.addToolTip(baseFolder, UIContext.Constants
				.restoreConflictBaseFolderWillNotBeCreatedTooltip());
		baseFolder.addStyleName("restoreWizardLeftSpacing");
//		this.add(baseFolder, td);
		structurePanel.add(baseFolder);
		
		data = new TableData();
		data.setVerticalAlign(VerticalAlignment.TOP);
		data.setWidth("50%");
		structurePanel.setStyleAttribute("padding-left", "12px");
		container.add(structurePanel, data);

		overwrite.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				replace.setEnabled(overwrite.getValue());
			}
		});

		return container;
	}

	private LayoutContainer renderHeaderSection() {
		LayoutContainer container = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(2);
		container.setLayout(tl);

		TableData td = new TableData();
		td.setWidth("5%");

		Image image = AbstractImagePrototype.create(UIContext.IconBundle.restore_options()).createImage();
		container.add(image, td);

		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.restoreOptions());
		label.setStyleName("restoreWizardTitle");
		container.add(label);

		return container;
	}
	
	private LayoutContainer renderVC(){
		FieldSet fieldSet = new FieldSet();  
	    fieldSet.setHeadingHtml(UIContext.Constants.vmRecoveryVCInfo()); 
		
		LayoutContainer container = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(4);
		tl.setWidth("100%");
		tl.setCellPadding(2);
		container.setLayout(tl);
		
		TableData td = new TableData();
		td.setVerticalAlign(VerticalAlignment.MIDDLE);
		td.setWidth("22%");
		TableData td35 = new TableData();
		td35.setWidth("28%");
		td35.setVerticalAlign(VerticalAlignment.MIDDLE);
		
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.vmRecoveryVirtualCenterLabel());
		container.add(label,td);
		
		vcTextField = new TextField<String>();
		vcTextField.setWidth(150);
		vcTextField.setAllowBlank(false);
		vcTextField.setValidateOnBlur(false);
		//vcTextField.setTabIndex(3);
		container.add(vcTextField, td35);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.vmRecoveryProtocolLabel());
		td.setStyle("padding-top:0");
		container.add(label,td);
		
		LayoutContainer protocolContainer = new LayoutContainer();
		TableLayout protocolTL = new TableLayout();
		protocolTL.setWidth("100%");
		protocolTL.setColumns(2);
		protocolContainer.setLayout(protocolTL);
		
		TableData proData = new TableData();
		proData.setWidth("50%");
		
		httpProtocol = new Radio();
		httpProtocol.setHideLabel(true);
		//httpProtocol.setTabIndex(7);
		httpProtocol.setBoxLabel(UIContext.Constants.vmRecoveryProtocolHttp());		
		httpProtocol.addListener(Events.Change, new Listener<FieldEvent>(){
			@Override
			public void handleEvent(FieldEvent be) {
				if(httpProtocol.getValue()==true){
					portTextField.setValue(80);
				}
			}
		});
		protocolContainer.add(httpProtocol, proData);
		httpsProtocol = new Radio();		
		//httpsProtocol.setTabIndex(8);
		httpsProtocol.setHideLabel(true);
		httpsProtocol.setBoxLabel(UIContext.Constants.vmRecoveryProtocolHttps());	
		httpsProtocol.addListener(Events.Change, new Listener<FieldEvent>(){
			@Override
			public void handleEvent(FieldEvent be) {
				if(httpsProtocol.getValue()==true){
					portTextField.setValue(443);
				}
			}
		});
		
		protocolContainer.add(httpsProtocol, proData);
		
		protocolGroup = new RadioGroup();
		protocolGroup.add(httpProtocol);
		protocolGroup.add(httpsProtocol);
		
		container.add(protocolContainer, td35);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.vmRecoveryUsernameLabel());
		container.add(label,td);
		
		usernameTextField = new TextField<String>();
		usernameTextField.setWidth(150);
		usernameTextField.setAllowBlank(false);
		usernameTextField.setValidateOnBlur(false);
		//usernameTextField.setValue("administrator");
		//usernameTextField.setTabIndex(4);
		container.add(usernameTextField, td35);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.vmRecoveryPortLabel());
		container.add(label,td);
		
		portTextField = new NumberField();
		portTextField.setWidth(100);
		portTextField.setAllowBlank(false);
		portTextField.setValidateOnBlur(false);
		portTextField.setMinValue(0);
		portTextField.setValue(443);
		//portTextField.setTabIndex(9);
		container.add(portTextField, td35);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.vmRecoveryPasswordLabel());
		container.add(label,td);
		
		passwordTextField = new PasswordTextField();
		passwordTextField.setWidth(150);
		passwordTextField.setPassword(true);
		//passwordTextField.setAllowBlank(false);
		passwordTextField.setValidateOnBlur(false);
		//passwordTextField.setTabIndex(5);
		container.add(passwordTextField, td35);
		
//		label = new LabelField();
//		label.setText("");
//		container.add(label,td);
//		
//		label = new LabelField();
//		label.setText("");
//		container.add(label,td35);
		
		TableData tb1 =new TableData();
		tb1.setColspan(2);
		tb1.setWidth("50%");
		
//		label = new LabelField();
//		label.setText("");
//		container.add(label,td);
		
		connectToVC = new Button();
		//connectToVC.setTabIndex(10);
		connectToVC.setText(UIContext.Constants.vmRecoveryConnectToVCButton());
		connectToVC.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				
				if(!thisPanel.portTextField.validate()){
					return;
				}
				if(!thisPanel.vcTextField.validate()){
					return;
				}
				if(!thisPanel.httpsProtocol.validate()){
					return;
				}
				if(!thisPanel.usernameTextField.validate()){
					return;
				}
				if(!thisPanel.passwordTextField.validate()){
					return;
				}
				
				String vcName = vcTextField.getValue();
				int port = portTextField.getValue().intValue();
				String protocol = httpsProtocol.getValue()==true?"https":"http";
				String username = usernameTextField.getValue();
				String password = passwordTextField.getValue();
				if(password == null)
					password = "";
				final VirtualCenterModel vcModel = new VirtualCenterModel();
				vcModel.setPassword(password);
				vcModel.setUsername(username);
				vcModel.setPort(port);
				vcModel.setProtocol(protocol);
				vcModel.setVcName(vcName);
				connectToVC.setEnabled(false);
				esxList.setEnabled(false);
				esxList.getStore().removeAll();
				esxList.setEmptyText(UIContext.Constants.vmRecoveryEsxServerLoading());
				vmList.setEnabled(false);
				vmList.getStore().removeAll();
				vmList.setEmptyText(UIContext.Constants.vmRecoveryEsxServerLoading());
				vmUsernameTextField.setEnabled(false);
				vmPasswordTextField.setEnabled(false);
				pathSelectionForOtherVM.setEnabled(false);
				service.validateVC(vcModel, new BaseAsyncCallback<Integer>(){
					@Override
					public void onFailure(Throwable caught){
						connectToVC.setEnabled(true);
						esxList.setEmptyText("");
						vmList.setEmptyText("");
						super.onFailure(caught);
					}
					
					@Override
					public void onSuccess(Integer result){
						connectToVC.setEnabled(true);
						String productName = UIContext.productNameD2D;
						if(UIContext.uiType == 1){
							productName = UIContext.productNamevSphere;
						}
						if(result!=0){
							esxList.setEmptyText("");
							vmList.setEmptyText("");
							MessageBox msg = new MessageBox();
							msg.setIcon(MessageBox.ERROR);
							msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(productName));
							msg.setMessage(UIContext.Constants.messageBoxConnectVCFail());
							msg.setModal(true);
							msg.show();
						}else{
							service.getESXServer(vcModel, new BaseAsyncCallback<List<ESXServerModel>>(){

								@Override
								public void onFailure(Throwable caught) {
									esxList.setEmptyText("");
									vmList.setEmptyText("");
									super.onFailure(caught);
								}

								@Override
								public void onSuccess(List<ESXServerModel> result) {
									if(result!=null){
										esxList.setEnabled(true);
										esxList.getStore().add(result);
										esxList.setValue(result.get(0));
									}else {
										esxList.setEmptyText("");
										vmList.setEmptyText("");
										MessageBox msg = new MessageBox();
										msg.setIcon(MessageBox.ERROR);
										msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(productName));
										msg.setMessage(UIContext.Constants.messageBoxConnectVCFail());
										msg.setModal(true);
										msg.show();
									}
								}
								
							});
						}
					}
				});
				
			}
			
		});
		container.add(connectToVC,tb1);
		fieldSet.add(container);
		httpsProtocol.setValue(true);
		setDefaultVCInfo();
		return fieldSet;
	}
	
	private void setDefaultVCInfo(){
		if(UIContext.backupVM!=null){
			vcTextField.setValue(UIContext.backupVM.getEsxServerName());
			portTextField.setValue(UIContext.backupVM.getPort());
			usernameTextField.setValue(UIContext.backupVM.getEsxUsername());
			passwordTextField.setValue(UIContext.backupVM.getEsxPassword());
		}
	}
	
	private LayoutContainer renderVM(){
		FieldSet fieldSet = new FieldSet();  
	    fieldSet.setHeadingHtml(UIContext.Constants.vmFileRestoreVMInformation());
		
		LayoutContainer container = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setCellPadding(2);
		tl.setColumns(2);
		tl.setWidth("100%");
		container.setLayout(tl);
		
		TableData td = new TableData();
		td.setVerticalAlign(VerticalAlignment.MIDDLE);
		td.setWidth("22%");
		
		TableData td1 = new TableData();
		td1.setVerticalAlign(VerticalAlignment.MIDDLE);
		td1.setWidth("78%");
		
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.vmRecoveryEsxServerLabel());
		container.add(label,td);
		
		esxList = new ComboBox<ESXServerModel>();
		//esxList.setTabIndex(12);
		esxList.setWidth(200);
		esxList.setDisplayField("esxName");
		esxList.setAllowBlank(false);
		esxList.setValidateOnBlur(false);
		esxList.setEditable(false);
		esxList.setTriggerAction(TriggerAction.ALL);
		esxList.setStore(new ListStore<ESXServerModel>());
		esxList.addSelectionChangedListener(new SelectionChangedListener<ESXServerModel>(){

			@Override
			public void selectionChanged(
					SelectionChangedEvent<ESXServerModel> se) {
				VirtualCenterModel vcModel = getVCModelFromNewDest();
				service.getVMItem(vcModel, se.getSelectedItem(), new BaseAsyncCallback<List<VMItemModel>>(){
					@Override
					public void onFailure(Throwable caught) {
						vmList.setEmptyText("");
						super.onFailure(caught);
					}

					@Override
					public void onSuccess(List<VMItemModel> result) {
						if(result != null){
							vmList.setEnabled(true);
							vmList.getStore().add(result);
							vmList.setValue(result.get(0));
							vmUsernameTextField.setEnabled(true);
							vmPasswordTextField.setEnabled(true);
						}else{
							vmList.setEmptyText(UIContext.Constants.vmFileRestoreNoVM());
						}
					}
				});
			}
			
		});
		container.add(esxList, td1);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.vmFileRestoreVirtualMachine());
		container.add(label,td);
		
		vmList = new ComboBox<VMItemModel>();
		//esxList.setTabIndex(12);
		vmList.setWidth(200);
		vmList.setDisplayField("vmName");
		vmList.setAllowBlank(false);
		vmList.setEnabled(false);
		vmList.setValidateOnBlur(false);
		vmList.setEditable(false);
		vmList.setTriggerAction(TriggerAction.ALL);
		vmList.setStore(new ListStore<VMItemModel>());
		
		container.add(vmList, td1);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.vmRecoveryUsernameLabel());
		container.add(label,td);
		
		vmUsernameTextField = new TextField<String>();
		vmUsernameTextField.setWidth(200);
		vmUsernameTextField.setAllowBlank(false);
		vmUsernameTextField.setValidateOnBlur(false);
		vmUsernameTextField.setEnabled(false);
		vmUsernameTextField.addListener(Events.KeyUp, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				if(vmUsernameTextField.getValue()==null || vmUsernameTextField.getValue().length()==0){
					pathSelectionForOtherVM.setEnabled(false);
				}else{
					pathSelectionForOtherVM.setEnabled(true);
				}
			}
			
		});
		container.add(vmUsernameTextField,td1);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.vmRecoveryPasswordLabel());
		container.add(label,td);
		
		vmPasswordTextField = new PasswordTextField();
		vmPasswordTextField.setWidth(200);
		vmPasswordTextField.setPassword(true);
		vmPasswordTextField.setAllowBlank(false);
		vmPasswordTextField.setValidateOnBlur(false);
		vmPasswordTextField.setEnabled(false);
		container.add(vmPasswordTextField,td1);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.Destination()+":");
		container.add(label,td);
		
		pathSelectionForOtherVM = new PathSelectionPanel(null,this);
		pathSelectionForOtherVM.setWidth("450");
		pathSelectionForOtherVM.setMode(PathSelectionPanel.RESTORE_ALT_VM_MODE);
		pathSelectionForOtherVM
				.setTooltipMode(PathSelectionPanel.TOOLTIP_RESTORE_ALT_VM_MODE);
//		pathSelection.setAllowBlank(false);
		pathSelectionForOtherVM.setPathFieldLength(290);
		pathSelectionForOtherVM.setEnabled(false);
		container.add(pathSelectionForOtherVM,td1);
		
		fieldSet.add(container);
		return fieldSet;
	}
	
	public VirtualCenterModel getVCModelFromNewDest(){
		if(backupVMModel == null)
			return null;
		VirtualCenterModel vcModel = new VirtualCenterModel();
		if(alternateVMLocation.getValue()){
			String vcName = vcTextField.getValue();
			int port = portTextField.getValue().intValue();
			String protocol = httpsProtocol.getValue()==true?"https":"http";
			String username = usernameTextField.getValue();
			String password = passwordTextField.getValue();
			
			vcModel.setPassword(password);
			vcModel.setUsername(username);
			vcModel.setPort(port);
			vcModel.setProtocol(protocol);
			vcModel.setVcName(vcName);
		}else if(sameVMAlternateLocation.getValue()){
			vcModel.setUsername(win.getUsername());
			vcModel.setPassword(win.getPassword());
			vcModel.setPort(win.getPort());
			vcModel.setProtocol(win.getProtocol());
			vcModel.setVcName(backupVMModel.getEsxServerName());
		}
		return vcModel;
	}
	
	public VMItemModel getVMItemModel(){
		if(backupVMModel == null)
			return null;
		VMItemModel vmModel = new VMItemModel();
		if(alternateVMLocation.getValue()){
			vmModel = vmList.getValue();
			vmModel.setUsername(vmUsernameTextField.getValue());
			vmModel.setPassword(vmPasswordTextField.getValue());
		}else if(sameVMAlternateLocation.getValue()){
			vmModel.setVmName(backupVMModel.getVMName());
			vmModel.setVmInstanceUUID(backupVMModel.getVmInstanceUUID());
			vmModel.setUsername(win.getVMUsername());
			vmModel.setPassword(win.getVMPassword());
			vmModel.setVmVMX(backupVMModel.getVmVMX());
		}
		return vmModel;
	}
	
	protected void setDefaultValue(){
		originalLocation.setValue(true);
		skip.setValue(true);
		pathSelection.setEnabled(false);
		baseFolder.setEnabled(false);
	}
	
	protected void setVSphereDefaultValue(){
		originalLocation.disable();
		originalLocation.setBoxLabel(UIContext.Constants
				.restoreToOriginalLocation()+" ("+UIContext.Constants.disable()+")");
		orginalLocationLabel.setVisible(true);
		alternateLocation.setValue(true);
		skip.setValue(true);
		pathSelection.setEnabled(true);
		baseFolder.setEnabled(true);
	}
	
	private String getParentFolder(String destination){
		if(destination==null || destination.equals("")){
			return null;
		}
		int index = destination.lastIndexOf("\\");
		if(index>0){
			return destination.substring(0,index);
		}else{
			return null;
		}
	}
	
	public static boolean isInDriveWithoutDriveLetter(String displayName,
			String path) {
		if(path == null || path.length() == 0) {
			//select the volume without drive letter
			if(displayName != null && displayName.toLowerCase().startsWith(VolumeDisplayPrefix.toLowerCase()))
				return true;
			
		} else {
			if(path.toLowerCase().matches(DriveWithoutDriveLetter_Suffix.toLowerCase())) {
				return true; 
			}
		}
		
		return false;
	}

	@Override
	public int processOptions() {
		
		if(restoreWizardWindow.restoreType == RestoreWizardContainer.RESTORE_BY_BROWSE_ARCHIVE)
		{
			RestoreArchiveJobModel model = RestoreContext.getRestoreArchiveJobModel();
			// restore to alternate
			if (alternateLocation.getValue()) {
				model.setDestType(DestType.AlterLoc.getValue());
				model.setarchiveRestoreDestinationPath(pathSelection.getDestination());
				model.setarchiveUserName(pathSelection.getUsername());
				model.setarchivePassword(pathSelection.getPassword());
			} else {
				model.setDestType(DestType.OrigLoc.getValue());
				model.setarchiveRestoreDestinationPath("");
			}
	
			FileSystemOptionModel fileSystemOption = new FileSystemOptionModel();
			fileSystemOption.setOverwriteExistingFiles(overwrite.getValue());
			fileSystemOption.setReplaceActiveFiles(replace.getValue());
			fileSystemOption.setCreateBaseFolder(baseFolder.getValue());
			fileSystemOption.setRename(rename.getValue());
			model.setFileSystemOption(fileSystemOption);
			
			if(archivePwdPane.isVisible()) {
				String password = archivePwdPane.getPassword();
				model.setEncrpytionPassword(password);
			}else
			{
				model.setEncrpytionPassword("");
			}
			
			model.setRestoreType(RestoreWizardContainer.RESTORE_BY_BROWSE_ARCHIVE);
		}
		else
		{
			if((restoreWizardWindow.restoreType == RestoreWizardContainer.RESTORE_BY_BROWSE) || ((restoreWizardWindow.restoreType == RestoreWizardContainer.RESTORE_BY_SEARCH) && restoreWizardWindow.restoreSearchPanel.bSearchBackups))
			{
				RestoreJobModel model = RestoreContext.getRestoreModel();
				// restore to alternate
				if (alternateLocation.getValue()) {
					model.setDestType(DestType.AlterLoc.getValue());
					model.setDestinationPath(pathSelection.getDestination());
					model.setDestUser(pathSelection.getUsername());
					model.setDestPass(pathSelection.getPassword());
				} else if (alternateVMLocation.getValue()){
					model.setDestType(DestType.AlterVM.getValue());
					model.setDestinationPath(pathSelectionForOtherVM.getDestination());
					
					VirtualCenterModel vcModel = getVCModelFromNewDest();
					VMItemModel vmModel = getVMItemModel();
					if(targetVMModel == null)
						targetVMModel = new BackupVMModel();
					targetVMModel.setEsxServerName(vcModel.getVcName());
					targetVMModel.setEsxUsername(vcModel.getUsername());
					targetVMModel.setEsxPassword(vcModel.getPassword());
					targetVMModel.setProtocol(vcModel.getProtocol());
					targetVMModel.setPort(vcModel.getPort());
					
					targetVMModel.setVmInstanceUUID(vmModel.getVmInstanceUUID());
					targetVMModel.setVMName(vmModel.getVmName());
					targetVMModel.setUsername(vmModel.getUsername());
					targetVMModel.setPassword(vmModel.getPassword());
					targetVMModel.setVmVMX(vmModel.getVmVMX());
					processBackupVM(true);
					
				} else if(sameVMAlternateLocation.getValue()){
					model.setDestType(DestType.AlterVM.getValue());
					model.setDestinationPath(backupVMModel.getDestination());
					processBackupVM(false);
				} else {
					model.setDestType(DestType.OrigLoc.getValue());
					model.setDestinationPath("");
					processBackupVM(false);
				}
				if(pwdPane.isVisible()) {
					String password = pwdPane.getPassword();
					model.setEncryptPassword(password);
				}
		
				model.fileSystemOption = new FileSystemOptionModel();
				model.fileSystemOption.setOverwriteExistingFiles(overwrite.getValue());
				model.fileSystemOption.setReplaceActiveFiles(replace.getValue());
				model.fileSystemOption.setCreateBaseFolder(baseFolder.getValue());
				model.fileSystemOption.setRename(rename.getValue());
																																										
			}

			if((restoreWizardWindow.restoreType == RestoreWizardContainer.RESTORE_BY_SEARCH) && restoreWizardWindow.restoreSearchPanel.bSearchArchives)
			{
				RestoreArchiveJobModel model = RestoreContext.getRestoreArchiveJobModel();
				// restore to alternate
				if (alternateLocation.getValue()) {
					model.setDestType(DestType.AlterLoc.getValue());
					model.setarchiveRestoreDestinationPath(pathSelection.getDestination());
					model.setarchiveUserName(pathSelection.getUsername());
					model.setarchivePassword(pathSelection.getPassword());
				} else {
					model.setDestType(DestType.OrigLoc.getValue());
					model.setarchiveRestoreDestinationPath("");
				}
		
				FileSystemOptionModel fileSystemOption = new FileSystemOptionModel();
				fileSystemOption.setOverwriteExistingFiles(overwrite.getValue());
				fileSystemOption.setReplaceActiveFiles(replace.getValue());
				fileSystemOption.setCreateBaseFolder(baseFolder.getValue());
				fileSystemOption.setRename(rename.getValue());
				model.setFileSystemOption(fileSystemOption);
				
				if(archivePwdPane.isVisible()) {
					String password = archivePwdPane.getPassword();
					model.setEncrpytionPassword(password);
				}
				else
				{
					model.setEncrpytionPassword("");
				}
				
				model.setRestoreType(RestoreWizardContainer.RESTORE_BY_SEARCH_ARCHIVE);
			}
		}
		return 0;
	}

	@Override
	public boolean validate(final AsyncCallback<Boolean> callback) {
		
//		Boolean isValid = Boolean.TRUE;
//		String errorMsg = null;
		//If user select one vm backup session and restoring to original, we will need to get source exsserver/vc credential.
		if (originalLocation.getValue() && backupVMModel != null){
			if(backupVMModel.getEsxPassword()==null || backupVMModel.getEsxPassword().equals("")){
				popUpSetCredentialWindow(thisPanel,false,callback);
				return false;
			}
		}
		if (sameVMAlternateLocation.getValue() && backupVMModel != null){
			if(backupVMModel.getEsxPassword()==null || backupVMModel.getEsxPassword().equals("")){
				popUpSetCredentialWindow(thisPanel,true,callback);
				return false;
			}
		}
		if ((restoreWizardWindow.restoreType == RestoreWizardContainer.RESTORE_BY_SEARCH)&&(restoreWizardWindow.restoreSearchPanel.bSearchBackups)){   	   ///D2D Lite Integration
			boolean isPassed = this.recoveryPointPasswordPanel.validate();
			if (!isPassed){
				String errorMsg = UIContext.Constants.restoreAllSessionPasswordRequired();
				showErrorMessage(callback, errorMsg);
				return false;
			}else
				RestoreContext.setEncrypedRecoveryPoints(recoveryPointPasswordPanel.getModel());
		}
			
		
		if (originalLocation.getValue()) {// ORIG LOC
			boolean isAllWithoutDriveLetter = true;
			  if (restoreWizardWindow.restoreType == RestoreWizardContainer.RESTORE_BY_SEARCH) {	   ///D2D Lite Integration
				  
				if(restoreWizardWindow.restoreSearchPanel.bSearchBackups)
				{
					List<CatalogItemModel> restoreSources = RestoreContext
							.getRestoreSearchSources();
					for (int i = 0; i < restoreSources.size(); i++) {
						SummaryDataModel model = FSRestoreSummaryPanel
									.convertToSummaryData(restoreSources.get(i));
						String displayName = model.getName();
						String path = model.getPath();
						if (restoreSources.get(i).getDriverLetterAttr() != null && restoreSources.get(i).getDriverLetterAttr() == HAS_DIRVER_LETER) {
							isAllWithoutDriveLetter = false;
							break;
						}
					}
				}
				
				if(restoreWizardWindow.restoreSearchPanel.bSearchArchives)
				{
					List<CatalogItemModel> restoreSources = RestoreContext.getRestoreArchiveSearchSources();
					
					for (int i = 0; i < restoreSources.size(); i++) {
						SummaryDataModel model = FSRestoreSummaryPanel
									.convertToSummaryData(restoreSources.get(i));
						String displayName = model.getName();
						String path = model.getPath();
						if (!isInDriveWithoutDriveLetter(displayName, path)) {
							isAllWithoutDriveLetter = false;
							break;
						}
					}
				}
				
				if (isAllWithoutDriveLetter) {
					String errorMsg = UIContext.Constants.restoreFilesAllInDriveWithoutDriveLetter();
					showErrorMessage(callback, errorMsg);
					return false;
				} 
				
				checkArchiveAndBackupSessionPasswords(callback);	//for validating both archive backup session passwords at a time			
			} else if (restoreWizardWindow.restoreType == RestoreWizardContainer.RESTORE_BY_BROWSE)
			{
				List<GridTreeNode> restoreSources = RestoreContext
						.getRestoreRecvPointSources();
				for (int i = 0; i < restoreSources.size(); i++) {
					String displayName = restoreSources.get(i).getDisplayName();
					String path = restoreSources.get(i).getPath();
					if (restoreSources.get(i).isHasDriverLetter()) {
						isAllWithoutDriveLetter = false;
						break;
					}
				}
				
				if (isAllWithoutDriveLetter) {
					String errorMsg = UIContext.Constants.restoreFilesAllInDriveWithoutDriveLetter();
					showErrorMessage(callback, errorMsg);
					return false;
				} 
				
				checkSessionPassword(callback);//for validating the backup encryption passwords.
			}
			else if (restoreWizardWindow.restoreType == RestoreWizardContainer.RESTORE_BY_BROWSE_ARCHIVE){
				List<ArchiveGridTreeNode> restoreSources = RestoreContext
						.getRestoreSelectedArchiveNodes();
				for (int i = 0; i < restoreSources.size(); i++) {
					String displayName = restoreSources.get(i).getDisplayName();
					String path = restoreSources.get(i).getFullPath();
					if (!isInDriveWithoutDriveLetter(displayName, path)) {
						isAllWithoutDriveLetter = false;
						break;
					}
				}
				
				if (isAllWithoutDriveLetter) {
					String errorMsg = UIContext.Constants.restoreFilesAllInDriveWithoutDriveLetter();
					showErrorMessage(callback, errorMsg);
					return false;
				} 
				
				checkArchiveEncryptionPassword(callback);//for validating the archive encryption password
			}

			//checkSessionPassword(callback);
			//callback.onSuccess(Boolean.TRUE);
		} 
		else if(alternateLocation.getValue())
		{// ALT LOC			
			String destination = thisPanel.pathSelection.getDestination();
			String userName = thisPanel.pathSelection.getUsername();
			String password = thisPanel.pathSelection.getPassword();
			String errorMsg = null;
			
			if (destination == null || destination.length() == 0) {
				errorMsg = UIContext.Constants.restoreAltLocCannotBeBlank();
			} else if(destination.length() > 2 && destination.startsWith("\\\\")) {
				int index = destination.indexOf("\\", 2);
				if(index < 0 || index == 2 || index == destination.length() - 1) { 
					errorMsg = UIContext.Constants.invalidRemotePath();
				}
			}
			
			if(errorMsg != null){
				showErrorMessage(callback, errorMsg);
				return false;
			} else if((userName==null || userName=="" || password==null || password=="")
					&& Utils.isValidRemotePath(pathSelection.getDestination())){
				final UserPasswordWindow dlg = new UserPasswordWindow(pathSelection.getDestination(), "", "");
				dlg.setModal(true);
				
				dlg.addWindowListener(new WindowListener()
				{				
					public void windowHide(WindowEvent we) {
						if (dlg.getCancelled() == false)
						{
							String username = dlg.getUsername();
							String password = dlg.getPassword();
							pathSelection.setUsername(username);
							pathSelection.setPassword(password);
							
							if((restoreWizardWindow.restoreType == RestoreWizardContainer.RESTORE_BY_BROWSE_ARCHIVE) ||((restoreWizardWindow.restoreType == RestoreWizardContainer.RESTORE_BY_SEARCH) && (restoreWizardWindow.restoreSearchPanel.bSearchArchives)))
							{
								//validate archive password panel
								checkArchiveEncryptionPassword(callback);
							}else if((restoreWizardWindow.restoreType == RestoreWizardContainer.RESTORE_BY_BROWSE) ||((restoreWizardWindow.restoreType == RestoreWizardContainer.RESTORE_BY_SEARCH) && (restoreWizardWindow.restoreSearchPanel.bSearchBackups)))
							{
								//validate backup password panel
								checkSessionPassword(callback);
							}
							else
								callback.onSuccess(Boolean.TRUE);
						}
						else {
							callback.onSuccess(Boolean.FALSE);
						}
					}
				});
				dlg.show();
			}
			else {
				if((restoreWizardWindow.restoreType == RestoreWizardContainer.RESTORE_BY_SEARCH) && (restoreWizardWindow.restoreSearchPanel.bSearchBackups) && (restoreWizardWindow.restoreSearchPanel.bSearchArchives))
					checkArchiveAndBackupSessionPasswords(callback);	//for validating both archive backup session passwords at a time
				else if((restoreWizardWindow.restoreType == RestoreWizardContainer.RESTORE_BY_BROWSE) ||((restoreWizardWindow.restoreType == RestoreWizardContainer.RESTORE_BY_SEARCH) && (restoreWizardWindow.restoreSearchPanel.bSearchBackups)))
					checkSessionPassword(callback);
				else if((restoreWizardWindow.restoreType == RestoreWizardContainer.RESTORE_BY_BROWSE_ARCHIVE) ||((restoreWizardWindow.restoreType == RestoreWizardContainer.RESTORE_BY_SEARCH) && (restoreWizardWindow.restoreSearchPanel.bSearchArchives)))
					checkArchiveEncryptionPassword(callback);
				else
					callback.onSuccess(Boolean.TRUE);
			}
		}else if (alternateVMLocation.getValue() == true){
			String errorMsg = "";
			boolean ret = true;
			if(vcTextField.getValue() == null || vcTextField.getValue().length()==0){
				errorMsg = UIContext.Constants.vmFileRestoreEsxServerNameCannotBeBlank();
				vcTextField.focus();
				ret = false;
			}
			if(ret && portTextField.getValue()==null){
				errorMsg = UIContext.Constants.vmFileRestoreEsxServerNameCannotBeBlank();
				portTextField.focus();
				ret = false;
			}
			if(ret && (usernameTextField.getValue() == null || usernameTextField.getValue().length()==0)){
				errorMsg = UIContext.Constants.vmFileRestoreEsxServerUsernameCannotBeBlank();
				usernameTextField.focus();
				ret = false;
			}
			if(ret && (passwordTextField.getValue() == null || passwordTextField.getValue().length()==0)){
				errorMsg = UIContext.Constants.vmFileRestoreEsxServerPasswordCannotBeBlank();
				passwordTextField.focus();
				ret = false;
			}
			
			if(ret && esxList.getValue() == null){
				errorMsg = UIContext.Constants.vmFileRestoreEsxServerListCannotBeBlank();
				esxList.focus();
				ret = false;
			}
			
			if(ret && vmList.getValue() == null){
				errorMsg = UIContext.Constants.vmFileRestoreVMListCannotBeBlank();
				vmList.focus();
				ret = false;
			}
			
			if(ret && (vmUsernameTextField.getValue() == null || vmUsernameTextField.getValue().length()==0)){
				errorMsg = UIContext.Constants.vmFileRestoreVMUsernameCannotBeBlank();
				vmUsernameTextField.focus();
				ret = false;
			}
			
			if(ret && (vmPasswordTextField.getValue() == null || vmPasswordTextField.getValue().length()==0)){
				errorMsg = UIContext.Constants.vmFileRestoreVMPasswordCannotBeBlank();
				vmPasswordTextField.focus();
				ret = false;
			}
			if(!ret){
				showErrorMessage(callback, errorMsg);
				return false;
			}
			callback.onSuccess(Boolean.TRUE);
		}else if(sameVMAlternateLocation.getValue()){
			callback.onSuccess(Boolean.TRUE);
		}else // here user has not chosen the option
		{
			String errorMsg = UIContext.Constants.noRestoreDestinationSelected();
			showErrorMessage(callback, errorMsg);
			return false;
		}
	
		return true;
	}

	
	public void validateRestoreEncryptionDetails(final AsyncCallback<Boolean> callback)
	{
		RestoreArchiveJobModel jobModel =  updateRestoreEncryptionDetails();				
		service.ValidateRestoreArchiveJob(jobModel,new BaseAsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) 
			{			
				super.onFailure(caught);
				callback.onSuccess(Boolean.FALSE);			
				
			}
			@Override
			public void onSuccess(Boolean result)
			{				
				callback.onSuccess(Boolean.TRUE);			
			}
		});
		
		
	}
	
	public void validateBackupArchiveRestoreEncryptionDetails(final AsyncCallback<Boolean> callback)
	{
		RestoreArchiveJobModel jobModel =  updateRestoreEncryptionDetails();				
		service.ValidateRestoreArchiveJob(jobModel,new BaseAsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) 
			{				
				super.onFailure(caught);
				callback.onSuccess(Boolean.FALSE);	
				
			}
			@Override
			public void onSuccess(Boolean result)
			{				
				 //Checking for Backup Restore Session Password
			     checkSessionPassword(callback);
			
			}
		});
		
		
	}
	
	public RestoreArchiveJobModel updateRestoreEncryptionDetails()
	{
		RestoreArchiveJobModel jobModel = restoreWizardWindow.getArchiveRestoreJobModel();		
		if (alternateLocation.getValue()) {
			jobModel.setDestType(DestType.AlterLoc.getValue());
			jobModel.setarchiveRestoreDestinationPath(pathSelection.getDestination());
			jobModel.setarchiveUserName(pathSelection.getUsername());
			jobModel.setarchivePassword(pathSelection.getPassword());
		} else {
			jobModel.setDestType(DestType.OrigLoc.getValue());
			jobModel.setarchiveRestoreDestinationPath("");
		}
		FileSystemOptionModel fileSystemOption = new FileSystemOptionModel();
		fileSystemOption.setOverwriteExistingFiles(overwrite.getValue());
		fileSystemOption.setReplaceActiveFiles(replace.getValue());
		fileSystemOption.setCreateBaseFolder(baseFolder.getValue());
		fileSystemOption.setRename(rename.getValue());
		jobModel.setFileSystemOption(fileSystemOption);		
		if(archivePwdPane.isVisible()) {
			String password = archivePwdPane.getPassword();
			jobModel.setEncrpytionPassword(password);
		}else
		{
			jobModel.setEncrpytionPassword("");
		}
		if(restoreWizardWindow.restoreType == RestoreWizardContainer.RESTORE_BY_BROWSE_ARCHIVE)
			jobModel.setRestoreType(RestoreWizardContainer.RESTORE_BY_BROWSE_ARCHIVE);
		else if ((restoreWizardWindow.restoreType == RestoreWizardContainer.RESTORE_BY_SEARCH) && (restoreWizardWindow.restoreSearchPanel.bSearchArchives))
			jobModel.setRestoreType(RestoreWizardContainer.RESTORE_BY_SEARCH_ARCHIVE);		
		return jobModel;
	}
	
	private void checkArchiveAndBackupSessionPasswords(AsyncCallback<Boolean> callback) {
    /*boolean bValidated = false;
		if(archivePwdPane.isVisible() && (archivePwdPane.getPassword() != null))
		{
			if(archivePwdPane.getPassword().length() > Utils.EncryptionPwdLen)
			{
				archivePwdPane.setPassword("");
				showErrorMessage(callback, UIContext.Constants.PasswordBeyondLength());
				return;
			}
		}
		else
			bValidated = true;*/	
		
		if(pwdPane.isVisible() && archivePwdPane.isVisible())
		{
		   checkArchiveBackupEncryptionPassword(callback);
		}
	    else if(pwdPane.isVisible())
		{
			checkSessionPassword(callback);
		}
		else if(archivePwdPane.isVisible())
		{
			checkArchiveEncryptionPassword(callback);
		}    
		else
		{
			callback.onSuccess(true);
		}
	
	}

	/*private void checkArchiveEncryptionPassword(AsyncCallback<Boolean> callback) {
		if(!archivePwdPane.isVisible() || (archivePwdPane.getPassword() == null))
		{
			callback.onSuccess(true);
			return;
		}
		
		if(archivePwdPane.getPassword().length() > Utils.EncryptionPwdLen)
		{
			archivePwdPane.setPassword("");
			showErrorMessage(callback, UIContext.Constants.PasswordBeyondLength());
			return;
		}
		else
			callback.onSuccess(false);
	}*/
	
	private void checkArchiveEncryptionPassword(AsyncCallback<Boolean> callback) 
	{
		if(archivePwdPane.isVisible())
		{
			if(archivePwdPane.isMaxLengthExceeded())
			{
				archivePwdPane.setPassword("");
				showErrorMessage(callback, UIContext.Constants.PasswordBeyondLength());
				return;
			}
			if(archivePwdPane.getPassword()==null || archivePwdPane.getPassword().isEmpty())
			{
				showErrorMessage(callback, UIContext.Constants.ProvidePasswordforEncryption());
				return;
			}
			validateRestoreEncryptionDetails(callback);
		}
		else
		{
			callback.onSuccess(true);
		}
	}
	
	private void checkArchiveBackupEncryptionPassword(AsyncCallback<Boolean> callback) 
	{
		if(archivePwdPane.isVisible())
		{
			if(archivePwdPane.isMaxLengthExceeded())
			{
				archivePwdPane.setPassword("");
				showErrorMessage(callback, UIContext.Constants.PasswordBeyondLength());
				return;
			}
			validateBackupArchiveRestoreEncryptionDetails(callback);
		}
		else
		{
			callback.onSuccess(true);
		}
	}
	
	
	protected void updateVisibleOfPwdPane() {
			
		pwdPane.setVisible(false);
		archivePwdPane.setVisible(false);
		recoveryPointPasswordPanel.setVisible(false);
		
		if(restoreWizardWindow.restoreType == RestoreWizardContainer.RESTORE_BY_SEARCH) {	   ///D2D Lite Integration
			if(restoreWizardWindow.restoreSearchPanel.bSearchBackups == true)
			{
				Map<String, EncrypedRecoveryPoint> encrypedRecoveryPoint = RestoreUtil.filterEncryptedRecoveryPoint(RestoreContext.getRestoreSearchSources(), recoveryPointPasswordPanel.getModel());
				if(encrypedRecoveryPoint != null){
					Map<String, String> pwdsByHash = restoreWizardWindow.restoreSearchPanel.getEncryptedPwd();
					for(Map.Entry<String, EncrypedRecoveryPoint> epd : encrypedRecoveryPoint.entrySet()) {
						if(pwdsByHash.containsKey(epd.getKey())) {
							EncrypedRecoveryPoint ep = epd.getValue();
							ep.setPassword(pwdsByHash.get(epd.getKey()));
							ep.setPasswordVerified(VerifyStatus.SUCCESS_VERIFIED);
						}
					}
					if (encrypedRecoveryPoint == null || encrypedRecoveryPoint.isEmpty())
						recoveryPointPasswordPanel.setVisible(false);
					else{
						recoveryPointPasswordPanel.setVisible(true);
					}
					if (encrypedRecoveryPoint != null)
						recoveryPointPasswordPanel.setModel(encrypedRecoveryPoint);
				}
			}
			
			//madra04 modifications
			if(restoreWizardWindow.restoreSearchPanel.bSearchArchives == true && restoreWizardWindow.restoreSearchResult.isArchiveSelected())
			{
				
		    	if(ArchivePathSelectionWindow.archiveDestination !=null)
				{
					if(!ArchivePathSelectionWindow.archiveDestination.equals(RestoreSearchPanel.archiveDestination))
					{
						if(restoreWizardWindow.isEncrypted())
						{
						    archivePwdPane.setVisible(true);
							archivePwdPane.setEncrptlabelTxt(UIContext.Constants.recoveryPointsNeedAlternateSessionPassword());
							archivePwdPane.setPassword("");
						}
						else
						{
							archivePwdPane.setVisible(false);
						}
					    return;
					}
					
				}
				ArchiveSettingsModel archiveConfig = restoreWizardWindow.getarchiveConfig();
				if(archiveConfig.getEncryption())
				{
					archivePwdPane.setVisible(true);
					archivePwdPane.setPassword(archiveConfig.getEncryptionPassword());
				}
				else
				{
					archivePwdPane.setVisible(false);
				}
			}
			else
			{
				archivePwdPane.setVisible(false);
			}
			return;
		}
		
		if(restoreWizardWindow.restoreType == RestoreWizardContainer.RESTORE_BY_BROWSE_ARCHIVE)
		{
			
			if(ArchivePathSelectionWindow.archiveDestination != null)
			{
				
				if(!ArchivePathSelectionWindow.archiveDestination.equals(RestoreArchiveBrowsePanel.archiveDestination))
				{
					if(restoreWizardWindow.isEncrypted())
					{
						archivePwdPane.setVisible(true);
						archivePwdPane.setEncrptlabelTxt(UIContext.Constants.recoveryPointsNeedAlternateSessionPassword());
						archivePwdPane.setPassword("");
					   
					}
					else
					{
						archivePwdPane.setVisible(false);
						
					}
					return;
				}
				
				
			}
			archivePwdPane.setVisible(false);
			ArchiveDestinationModel archiveConfig = restoreWizardWindow.getArchiveDestinationModel();

			/*
			 * Note: The reason being commented the below code is: some times user create plan with FC encryption. And then move the node to other plan with same FC dest but no encr pwd.
			 * Since FC PFC is optional, this is needed.
			 * Fix for bug: 761806
			 */
				
			/*if(archiveConfig!=null&&archiveConfig.getEncryption()!=null&&archiveConfig.getEncryption()){
				archivePwdPane.setVisible(true);
				archivePwdPane.setPassword(archiveConfig.getEncryptionPassword());
			}
			else
				archivePwdPane.setVisible(false);*/
			
			if(restoreWizardWindow.isEncrypted()){
				archivePwdPane.setVisible(true);
				archivePwdPane.setPassword(archiveConfig.getEncryptionPassword()==null || archiveConfig.getEncryptionPassword().isEmpty()? "": archiveConfig.getEncryptionPassword());
			}
			
			return;
		}
		
		if(restoreWizardWindow.restoreType == RestoreWizardContainer.RESTORE_BY_BROWSE) {
			if (!RestoreContext.getRecoveryPointModel().isEncrypted())
				pwdPane.setVisible(false);
			else {
				pwdPane.setVisible(true);
				RecoveryPointsPanel panel = restoreWizardWindow.getRecoveryPointsPanel();
				if(panel.getSelectedRecoveryPointEncKey() != null
						&& !panel.getSelectedRecoveryPointEncKey().isEmpty())
					pwdPane.setPassword(panel.getSelectedRecoveryPointEncKey());
				else
					pwdPane.autoFillPassword(restoreWizardWindow.getSelectedRecoveryPoint().getSessionGuid());
			}
			return;
		}
		
		super.updateVisibleOfPwdPane();
	}
}



