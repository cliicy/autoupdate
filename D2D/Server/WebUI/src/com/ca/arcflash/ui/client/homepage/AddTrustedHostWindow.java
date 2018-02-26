package com.ca.arcflash.ui.client.homepage;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.model.TrustHostModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.KeyNav;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;

import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AddTrustedHostWindow extends Window {
	private String serverNameReg = "[^`~!@#\\$\\^&\\*\\(\\)=\\+\\[\\]{}\\\\\\|;:'\",<>/\\?]+";
	final CommonServiceAsync service = GWT.create(CommonService.class);
	
	private AddTrustedHostWindow window;
	private TextField<String> serverNameTF;
	private TextField<String> userTF;
	private TextField<String> pwdTF;
	private NumberField portTF;
	private KeyNav<ComponentEvent> enterKey;
	private Button okButton;
	private Button cancelButton;
	private CheckBox protocolCB;
	private String protocol="http:";
	public AddTrustedHostWindow() {
		this.window = this;
		this.setHeadingHtml(UIContext.Constants.addTrustedHostWindowTitle());
		this.setClosable(false);
		this.setSize(400, 220);
		addServerPanel(this);
		this.addButtons(this);
		
		enterKey = new KeyNav<ComponentEvent>(this){
			public void handleEvent(ComponentEvent ce) {  
	    		if( ce.getKeyCode() == 13 )
	    			okButton.fireEvent(Events.Select);
	    		else if( ce.getKeyCode() == 27 )
	    			cancelButton.fireEvent(Events.Select);
	    	}
		};
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
		serverNameTF.ensureDebugId("72abfb6b-9a65-463c-bf93-8ae8fc46f77d");
		serverNameTF.setAllowBlank(false);
		serverNameTF.setWidth("80%");
		serverNameTF.setRegex(serverNameReg);
		serverNameTF.setValidateOnBlur(false);
		serverNameTF.getMessages().setRegexText(UIContext.Messages.remoteDeployMsgServerNameInvalid(UIContext.productNameD2D));
		serverNameTF.setFieldLabel(UIContext.Constants.remoteDeployAddServerServerNameLabel());
		serverNameTF.setValidator(new Validator() {
			@Override
			public String validate(Field<?> field, String value) {
				ListStore<TrustHostModel> store = UIContext.homepagePanel.getTrustHostCombo().getStore();
				if (value != null && store != null) {
					boolean isExisted = false;
					for (int i = 0; i < store.getCount(); i++) {
						if (value.equalsIgnoreCase(store.getAt(i).getHostName())) {
							isExisted = true;
							break;
						}
					}
					
					if (isExisted) {
						return UIContext.Constants
								.remoteDeployMsgRemoteMachineAlreadyExists();
					}
				}
				return null;
			}
		});
		
		panel.add(serverNameTF);

		userTF = new TextField<String>();
		userTF.ensureDebugId("c9009415-31bc-4df2-8c23-f9f3a9ed5d52");
		userTF.setWidth("80%");
		userTF.setAllowBlank(false);
		userTF.setValidateOnBlur(false);
		userTF.setFieldLabel(UIContext.Constants.remoteDeployAddServerUserNameLabel());
		panel.add(userTF);

		pwdTF = new TextField<String>();
		pwdTF.ensureDebugId("d770b7d7-6974-4e80-95b6-6af607dc6401");
		pwdTF.setPassword(true);
		pwdTF.setWidth("80%");
		pwdTF.setFieldLabel(UIContext.Constants.remoteDeployAddServerPasswordLabel());
		panel.add(pwdTF);
		
		portTF = new NumberField();
		portTF.ensureDebugId("75dfeafb-1222-4008-afa8-76ac8d515042");
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
		
		this.protocolCB = new CheckBox();
		protocolCB.setFieldLabel("https");
		protocolCB.ensureDebugId("18aad112-a2c6-4fdf-bffe-0bec0029ad30");
		FormData fdP = new FormData();
		fdP.setWidth(15);
		//protocolCB.setBoxLabel("");
		protocolCB.setWidth("40");
		protocolCB.addListener(Events.Change, new Listener<FieldEvent>()
		{
			@Override
			public void handleEvent(FieldEvent be) {
				if(protocolCB.getValue()){
					protocol = "https:";
				}else
					protocol = "http:";
			}
		});
		panel.add(protocolCB,fdP);
		parent.add(panel);
	}
	
	
	
	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		this.serverNameTF.focus();
	}

	private void addButtons(ContentPanel cp) {
		okButton = new Button();
		okButton.setText(UIContext.Constants.ok());
		okButton.ensureDebugId("eea73e49-b4ce-4115-b9c1-3f91b82d384a");
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				if (window.validate()) {
					checkLocalhost();
				}
			}
			
			private void checkLocalhost() {
				window.mask(UIContext.Constants.addingTrustedHostMessage());
				service.isLocalHost(serverNameTF.getValue(),
						new BaseAsyncCallback<Boolean>() {
							@Override
							public void onFailure(Throwable caught) {
								super.onFailure(caught);
								window.unmask();
							}

							@Override
							public void onSuccess(Boolean result) {
								// True: is a localhost, block it.
								if (Boolean.TRUE.equals(result)) {
									MessageBox msg = new MessageBox();
									msg.setIcon(MessageBox.ERROR);
									msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
									msg.setMessage(UIContext.Constants.addTrustedHostMsgLocalMachineNotAllowed());
									msg.setModal(true);
									Utils.setMessageBoxDebugId(msg);
									msg.show();
									window.unmask();
								} else {
									final TrustHostModel model = new TrustHostModel();
									model.setHostName(serverNameTF.getValue());
									model.setUser(userTF.getValue());
									model.setPassword(pwdTF.getValue());
									model.setPort(portTF.getValue().intValue());
									model.setProtocol(protocol);
									service.validateRemoteServer(model, new AsyncCallback<TrustHostModel>(){

										@Override
										public void onFailure(Throwable caught) {
											if (caught instanceof BusinessLogicException){
												popupError(((BusinessLogicException)caught).getDisplayMessage());
											}
											
											window.unmask();
										}

										@Override
										public void onSuccess(TrustHostModel model) {
											model.setType(2);
											if(!sameType(model)) {
												popupError(UIContext.Messages.addServerNotSameType(UIContext.productNameD2D));
												window.unmask();
												return;
																
											}
											service.addTrustHost(model, new AsyncCallback<Void>(){

												@Override
												public void onFailure(
														Throwable caught) {
													if (caught instanceof BusinessLogicException){
														popupError(((BusinessLogicException)caught).getDisplayMessage());
													}
													window.unmask();
												}

												@Override
												public void onSuccess(Void result) {
													window.unmask();
													UIContext.homepagePanel.refreshTrustHost();
													window.hide();
												}
												
											});
										}
										
									});
								}
							}
						});
			}
		});

		cancelButton = new Button();
		cancelButton.ensureDebugId("56a56ef8-ccda-4272-b8ee-322e069e6e63");
		cancelButton.setText(UIContext.Constants.cancel());
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				window.hide();
			}
		});

		
		cp.setButtonAlign(HorizontalAlignment.RIGHT);
		cp.addButton(okButton);
		cp.addButton(cancelButton);
	}
	
	private void popupError(String msg) {
		MessageBox messageBox = new MessageBox();
		messageBox.setMaxWidth(400);
		messageBox.setMinWidth(400);
		messageBox.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
		messageBox.setMessage(msg);
		messageBox.setIcon(MessageBox.ERROR);
		messageBox.setModal(true);
		Utils.setMessageBoxDebugId(messageBox);
		messageBox.show();
	}
	
	public boolean sameType(TrustHostModel model) {
		Integer type = model.getProductType();
		if(type != null && type != UIContext.serverVersionInfo.getProductType()) {
			return false;
		}else {
			return true;
		}
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

		return true;

	}
}
