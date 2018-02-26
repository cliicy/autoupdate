package com.ca.arcflash.ui.client.remotedeploy;

import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.PasswordTextField;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.DeployUpgradeInfoModel;
import com.ca.arcflash.ui.client.model.RemoteDeployStatus;
import com.ca.arcflash.ui.client.model.ServerInfoModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.KeyNav;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.tips.ToolTip;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;

public class AddServerWindow extends Window {
	final CommonServiceAsync service = GWT.create(CommonService.class);
	private AddServerWindow window;
	private TextField<String> serverNameTF;
	private TextField<String> userTF;
	private PasswordTextField pwdTF;
	// private TextField<String> cfmPwdTF;
	private NumberField portTF;
	private TextField<String> installPathTF;
	private RadioGroup isRebootRG;
	private Radio yesRd;
	private Radio noRd;
	private ListStore<ServerInfoModel> store;
	private Grid<ServerInfoModel> grid;
	private int selectedIndex = -1;
	private CheckBox cbAutoStartRR;
	//private KeyNav<ComponentEvent> enterKey;
	private Button okButton;
	private Button cancelButton;
	private CheckBox cbInstallDriver;
	private CheckBox cbUseHttps;

	public AddServerWindow(ListStore<ServerInfoModel> listStore,
			Grid<ServerInfoModel> grid) {
		this.store = listStore;
		this.grid = grid;
		this.window = this;
		this.setHeadingHtml(UIContext.Constants.remoteDeployAddServerHeader());
		this.setClosable(false);
		this.setSize(400, 380);
		addServerPanel(this);
		addInstallDriverCB(this);
		// this.add(getRadioPanel());
		this.addButtons(this);

		 new KeyNav<ComponentEvent>(this) {
			public void handleEvent(ComponentEvent ce) {
				if (ce.getKeyCode() == 13)
					okButton.fireEvent(Events.Select);
				else if (ce.getKeyCode() == 27)
					cancelButton.fireEvent(Events.Select);
			}
		};

		this.addListener(Events.Disable, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				okButton.setEnabled(false);
				cancelButton.setEnabled(false);
			}
		});

		this.addListener(Events.Enable, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				okButton.setEnabled(true);
				cancelButton.setEnabled(true);
			}
		});
	}

	private void addServerPanel(ContentPanel parent) {
		FormPanel panel = new FormPanel();
		panel.setPadding(15);
		panel.setBorders(false);
		panel.setBodyBorder(false);
		panel.setFrame(false);
		panel.setHeaderVisible(false);
		panel.setLabelWidth(120);
		serverNameTF = new TextField<String>();		
		serverNameTF.ensureDebugId("13705A09-4469-42ac-94FD-52D859269409");
		serverNameTF.setAllowBlank(false);
		// serverNameTF.setMaxLength(15);
		serverNameTF.setWidth("80%");
		serverNameTF.setRegex(serverNameReg);
		serverNameTF.getMessages().setRegexText(
				UIContext.Messages.remoteDeployMsgServerNameInvalid(UIContext.productNameD2D));
		serverNameTF.setFieldLabel(UIContext.Constants
				.remoteDeployAddServerServerNameLabel());
		serverNameTF.setValidator(new Validator() {
			@Override
			public String validate(Field<?> field, String value) {
				if (value != null && store != null) {
					boolean isExisted = false;
					int selIndx = -2;
					for (int i = 0; i < store.getCount(); i++) {
						if (value.trim().equalsIgnoreCase(
								store.getAt(i).getServerName().trim())) {
							isExisted = true;
							selIndx = i;
							break;
						}
					}
					if (isExisted && selIndx != selectedIndex) {
						return UIContext.Constants
								.remoteDeployMsgRemoteMachineAlreadyExists();
					}
				}
				return null;
			}
		});
		panel.add(serverNameTF);

		userTF = new TextField<String>();
		userTF.ensureDebugId("77BB8416-BF75-44ad-AFE3-A26A650581B5");
		userTF.setWidth("80%");
		userTF.setAllowBlank(false);
		userTF.setFieldLabel(UIContext.Constants
				.remoteDeployAddServerUserNameLabel());
		ToolTipConfig tipConfig = new ToolTipConfig(UIContext.Constants.loginUsernameTooltip());
		ToolTip tip = new ToolTip(userTF, tipConfig);	
		tip.ensureDebugId("69926344-E5E6-4ba9-AED9-BDC5FD2EBDD3");
		tip.setHeaderVisible(false);
		panel.add(userTF);

		pwdTF = new PasswordTextField();
		pwdTF.ensureDebugId("8064DD67-3BF5-4ffb-BAB6-A780A0D32138");
		pwdTF.setPassword(true);
		pwdTF.setWidth("80%");
		pwdTF.setFieldLabel(UIContext.Constants
				.remoteDeployAddServerPasswordLabel());
		panel.add(pwdTF);

		portTF = new NumberField();
		portTF.ensureDebugId("2553B4F9-A2E0-4dae-BFFF-078D81047F6F");
		portTF.setPropertyEditorType(Integer.class);
		portTF.setValue(8014);
		portTF.setAllowBlank(false);
		portTF.setAllowDecimals(false);
		portTF.setAllowNegative(false);
		portTF.setMaxValue(65535);
		portTF.setMinValue(1024);
		portTF.setValidateOnBlur(true);
		portTF.setWidth("50%");
		portTF.setValidator(new Validator() {
			@Override
			public String validate(Field<?> field, String value) {
				return null;
			}
		});
		portTF.setFieldLabel(UIContext.Constants
				.remoteDeployAddServerPortLabel());
		FormData fd = new FormData();
		fd.setWidth(100);
		panel.add(portTF, fd);

		installPathTF = new TextField<String>();
		installPathTF.ensureDebugId("298B4517-E01A-453f-B217-8BD79A7DDC19");
		installPathTF.setWidth("90%");
		installPathTF.setValidateOnBlur(true);
		installPathTF.setAllowBlank(false);
		installPathTF.setValue("%ProgramFiles%\\CA\\ARCserve D2D");
		installPathTF.setValidator(new Validator() {

			@Override
			public String validate(Field<?> field, String value) {
				return null;
			}
		});
		installPathTF.setRegex(absoluteDirReg);
//		installPathTF.getMessages().setRegexText(UIContext.Messages.remoteDeployMsgInstallPathInvalid(UIContext.productNameD2D,UIContext.productNameD2D));
		installPathTF.getMessages().setRegexText(UIContext.Messages.remoteDeployMsgInstallPathInvalid(UIContext.productNameD2D));
		installPathTF.setFieldLabel(UIContext.Constants
				.remoteDeployAddServerInstallPathLabel());
		panel.add(installPathTF);
		parent.add(panel);
		
		cbUseHttps = new CheckBox();
		cbUseHttps.ensureDebugId("CEFC2F10-D3E3-4dc4-904C-B92968A805B0");
		cbUseHttps.setBoxLabel(UIContext.Constants.remoteDeployAddServerUseHttps());
		cbUseHttps.setValidateOnBlur(false);
		parent.add(cbUseHttps, new FitData(0, 0 ,0 , 15));
		
		cbAutoStartRR = new CheckBox() {
			@Override
			protected void onAttach() {
				super.onAttach();
				if(boxLabelEl != null) {
					//boxLabelEl.setStyleAttribute("white-space", "normal");
					boxLabelEl.dom.getStyle().setPropertyPx("left", 1);
					boxLabelEl.dom.getStyle().setPropertyPx("top", 5);
				}
			}
		};
		
		cbAutoStartRR.ensureDebugId("36495CEA-C3FA-4d39-A1E2-C629D933CF18");
		cbAutoStartRR.setBoxLabel(UIContext.Constants
				.remoteDeployAddServerAllowAutoStartRRS());
		cbAutoStartRR.setStyleAttribute("white-space", "normal");
		
		cbAutoStartRR.setValue(true);
		
		parent.add(cbAutoStartRR,new FitData(20, 0, 0, 15));
	}

	public void setSelectedIndex(int selectedIndex) {
		if (selectedIndex >= 0) {
			this.setHeadingHtml(UIContext.Constants.remoteDeployEditServerHeader());
			this.selectedIndex = selectedIndex;
			if (store != null) {
				ServerInfoModel model = store.getAt(selectedIndex);
				{
					setServerInfoModel(model);
				}
			}
		}
	}
	
	private void addInstallDriverCB(ContentPanel parent){
		cbInstallDriver = new CheckBox(){
			@Override
			protected void onClick(ComponentEvent ce) {
				super.onClick(ce);
				if(!this.getValue()){
					isRebootRG.setValue(noRd);
					isRebootRG.disable();
				}else {
					isRebootRG.enable();
					isRebootRG.setValue(yesRd);
				}
			}
		};
		cbInstallDriver.ensureDebugId("17EB2F87-67C5-4408-A6FC-C458D5D1A87C");
		cbInstallDriver.setBoxLabel(UIContext.Constants
				.remoteDeployAddServerInstallDriver());
		cbInstallDriver.setValue(true);	
		parent.add(cbInstallDriver, new FitData(20, 0, 0, 15));
		
		yesRd = new Radio();
		yesRd.ensureDebugId("201FF6A0-E476-473b-97EE-8EA64CC71F02");
		yesRd.setBoxLabel(UIContext.Constants.remoteDeployAddServerYesLabel());
		yesRd.setHideLabel(true);
		yesRd.setValue(true);

		noRd = new Radio();
		noRd.ensureDebugId("4EF4885F-0238-45c5-AE8E-B0C54ED8D106");
		noRd.setBoxLabel(UIContext.Constants.remoteDeployAddServerNoLabel());
		noRd.setHideLabel(true);

		isRebootRG = new RadioGroup();
		isRebootRG.add(yesRd);
		isRebootRG.add(noRd);
		isRebootRG.setFieldLabel(UIContext.Constants
				.remoteDeployAddServerIsRebootLabel());
		
		FormPanel panel = new FormPanel();
		panel.setPadding(5);
		panel.setBorders(false);
		panel.setBodyBorder(false);
		panel.setFrame(false);
		panel.setHeaderVisible(false);
		panel.setLabelWidth(70);
		panel.add(isRebootRG);
		
		parent.add(panel, new FitData(0,0,0,30));
	}

	public void setServerInfoModel(ServerInfoModel model) {
		if (model != null) {
			this.serverNameTF.setValue(model.getServerName());
			this.userTF.setValue(model.getUserName());
			this.pwdTF.setValue(model.getPassword());
			this.portTF.setValue(model.getPort());
			if (Boolean.TRUE.equals(model.isReboot())) {
				this.yesRd.setValue(true);
			} else {
				this.noRd.setValue(true);
			}
			if (Boolean.TRUE.equals(model.isAutoStartRemoteRegService())) {
				this.cbAutoStartRR.setValue(true);
			} else {
				this.cbAutoStartRR.setValue(false);
			}
			this.installPathTF.setValue(model.getInstallPath());
			this.cbInstallDriver.setValue(model.isInstallDriver());
			this.cbUseHttps.setValue(model.isUseHttps());
		}
	}

	public ServerInfoModel getServerInfoModel() {
		ServerInfoModel si = new ServerInfoModel();
		String nameTF = this.serverNameTF.getValue();
		if(nameTF !=null && !nameTF.equals("")){
			nameTF = nameTF.trim();
		}
		si.setServerName(nameTF);
		si.setUserName(this.userTF.getValue());
		si.setPassword(this.pwdTF.getValue());
		si.setPort(this.portTF.getValue().intValue());
		si.setAutoStartRemoteRegService(this.cbAutoStartRR.getValue());
		si.setReboot(this.yesRd.getValue());
		si.setInstallPath(this.installPathTF.getValue());
		si.setSelected(true);
		si.setDeployStatus(UIContext.Constants.remoteDeployAddServerNALabel());
		si.setDeployStatusCode(RemoteDeployStatus.DEPLOY_NA.value());
		si.setDeployMessage(UIContext.Constants.remoteDeployAddServerNALabel());
		si.setDeployPercentage(0);
		si.setInstallDriver(cbInstallDriver.getValue());
		si.setUseHttps(cbUseHttps.getValue());
		return si;
	}

	public boolean validate() {
		if (!this.serverNameTF.validate()) {
			return false;
		} else {
			serverNameTF.clearInvalid();
		}
		if (!this.userTF.validate()) {
			return false;
		} else {
			userTF.clearInvalid();
		}
		if (!this.pwdTF.validate()) {
			return false;
		} else {
			pwdTF.clearInvalid();
		}

		if (!this.portTF.validate()) {
			return false;
		} else {
			portTF.clearInvalid();
		}
		if (!this.installPathTF.validate()) {
			return false;
		} else {
			installPathTF.clearInvalid();
		}

		return true;

	}

	private boolean isEditMode() {
		return selectedIndex >= 0;
	}

	private void addButtons(ContentPanel cp) {
		okButton = new Button();
		okButton.ensureDebugId("7A734FD2-7BC0-40bf-9B69-6EB4BDD9A6D3");
		okButton.setText(UIContext.Constants.ok());
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			MessageBox box = null;
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				if (window.validate()) {
					checkLocalhost();
				}
			}

			private void checkLocalhost() {
				//window.setEnabled(false);
				window.mask();
				box = MessageBox.wait(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D), UIContext.Constants.validatingAndSaving(), "");
				Utils.setMessageBoxDebugId(box);
				String nameTF = serverNameTF.getValue();
				if(nameTF !=null && !nameTF.equals("")){
					nameTF = nameTF.trim();
				}
				service.isLocalHost(nameTF,
						new BaseAsyncCallback<Boolean>() {
							@Override
							public void onFailure(Throwable caught) {
								super.onFailure(caught);
								//window.setEnabled(true);
								window.unmask();
								box.close();
							}

							@Override
							public void onSuccess(Boolean result) {
								// True: is a localhost, block it.
								if (Boolean.TRUE.equals(result)) {
									MessageBox msg = new MessageBox();
									msg.setIcon(MessageBox.ERROR);
									msg.setTitleHtml(UIContext.Messages
											.messageBoxTitleError(UIContext.productNameD2D));
									msg
											.setMessage(UIContext.Constants
													.remoteDeployMsgLocalMachineNotAllowed());
									msg.setModal(true);
									Utils.setMessageBoxDebugId(msg);
									msg.show();
									//window.setEnabled(true);
									window.unmask();
									box.close();
								} else {
									test();
								}
							}
						});
			}
			
			private void test() {
				service.validDeploymentServer(getServerInfoModel(),
						new BaseAsyncCallback<DeployUpgradeInfoModel>() {
							@Override
							public void onFailure(Throwable caught) {
								super.onFailure(caught);
								//window.setEnabled(true);
								window.unmask();
								box.close();
							}

							@Override
							public void onSuccess(final DeployUpgradeInfoModel result) {
								if(result.getBuild()>0){
									String messageText = UIContext.Messages.upgradeWarningMessage(""+result.getBuild());
									
									
									final MessageBox message = new MessageBox();
									message.setTitleHtml(UIContext.Messages.upgradeWaringTitle());
									message.setIcon(MessageBox.WARNING);
									message.setMessage(messageText);
									message.setButtons(Dialog.OKCANCEL);
									Utils.setMessageBoxDebugId(message);
									message.setModal(true);
									
									message.addCallback(new Listener<MessageBoxEvent>() {
										@Override
										public void handleEvent(MessageBoxEvent be) {
											if(be.getButtonClicked().getItemId().equals(Dialog.OK)){
												portTF.setValue( new Long(result.getPort()));
												installPathTF.setValue(result.getInstallPath());
												if(result.useHttps() != null)
													cbUseHttps.setValue(result.useHttps());
												save();
											}else{
												//window.setEnabled(true);
												window.unmask();
												box.close();
											}
										}
									});
									message.show();
								}else
								save();
							}
						});
			}

			private void save() {
				List<ServerInfoModel> list = store.getModels();
				if (isEditMode()) {
					list.remove(store.getAt(selectedIndex));
				}

				list.add(getServerInfoModel());

				service.setDeploymentServers(list,
						new BaseAsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								super.onFailure(caught);
								//window.setEnabled(true);
								window.unmask();
							}

							@Override
							public void onSuccess(Void result) {
								final ServerInfoModel model = getServerInfoModel();
								if (isEditMode()) {
									store.remove(store.getAt(selectedIndex));
									store.insert(model,	selectedIndex);
								} else {
									store.add(model);
								}
								DeferredCommand.addCommand(new Command(){

									@Override
									public void execute() {
										grid.getSelectionModel().select(model, true);										
									}});
								
								//window.setEnabled(true);
								window.unmask();
								box.close();
								window.hide();
							}
						});
			}
		});

		cancelButton = new Button();
		cancelButton.ensureDebugId("DE96AB9B-4473-4a13-B862-4E91165F87CE");
		cancelButton.setText(UIContext.Constants.cancel());
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				window.hide();
			}
		});

		cp.setButtonAlign(HorizontalAlignment.CENTER);
		cp.addButton(okButton);
		cp.addButton(cancelButton);
	}

	// Note:Special meaning chars for pattern:[\^$.|?*+()
	// `~!@#$^&*()=+[]{}\|;:'",<>/?
	// pattern: not `~!@#\$\^&\*\(\)=\+\[\]{}\\\|;:'",<>/\?
	private String serverNameReg = "[^`~!@#\\$\\^&\\*\\(\\)=\\+\\[\\]{}\\\\\\|;:'\",<>/\\?]+";
	// \/:*?"<>|
	// pattern: not \\/:\*\?"<>\|
	private String directroy = "[^\\\\/:\\*\\?\"<>\\|@\\^&\\(=!%;',\\.]+";
	private String absoluteDirReg = "^([A-Za-z]{1}:|%ProgramFiles%)" + "(\\\\"
			+ directroy + ")*(\\\\)?$";
}
