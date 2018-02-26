package com.ca.arcflash.ui.client.vsphere.backup;

import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.PasswordTextField;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.VMItemModel;
import com.ca.arcflash.ui.client.model.VirtualCenterModel;
import com.ca.arcflash.ui.client.model.VirtualCenterNodeModel;
import com.ca.arcflash.ui.client.vsphere.backup.ChooseVirtualMachine;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.KeyNav;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
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

public class VirtualCenterWindow extends Window {
	private String serverNameReg = "[^`~!@#\\$\\^&\\*\\(\\)=\\+\\[\\]{}\\\\\\|;:'\",<>/\\?]+";
	private final LoginServiceAsync service = GWT.create(LoginService.class);

	private VirtualCenterWindow window;
	private VSphereBackupSettingWindow parentWindow;
	private TextField<String> serverNameTF;
	private TextField<String> userTF;
	private PasswordTextField pwdTF;
	private NumberField portTF;
	private KeyNav<ComponentEvent> enterKey;
	private Button okButton;
	private Button cancelButton;
	private CheckBox protocolCB;
	private String protocol = "https";
	private VirtualCenterModel vcModel;
	private ChooseVirtualMachine chooseVM;

	public VirtualCenterWindow(VSphereBackupSettingWindow window) {
		parentWindow = window;
		this.window = this;

		this.setHeadingHtml(UIContext.Constants.addVirtualCenterWindowTitle());
		this.setClosable(false);
		this.setSize(400, 220);
		addVCPanel(this);
		this.addButtons(this);

		enterKey = new KeyNav<ComponentEvent>(this) {
			public void handleEvent(ComponentEvent ce) {
				if (ce.getKeyCode() == 13)
					okButton.fireEvent(Events.Select);
				else if (ce.getKeyCode() == 27)
					cancelButton.fireEvent(Events.Select);
			}
		};
	}

	private void addVCPanel(ContentPanel parent) {
		FormPanel panel = new FormPanel();
		panel.setPadding(15);
		panel.setBorders(false);
		panel.setBodyBorder(false);
		panel.setFrame(false);
		panel.setHeaderVisible(false);
		panel.setLabelWidth(120);
		serverNameTF = new TextField<String>();
		serverNameTF.ensureDebugId("DC8344FA-7C43-4d0a-98BA-54BC47212F1C");
		serverNameTF.setAllowBlank(false);
		serverNameTF.setWidth("80%");
		serverNameTF.setRegex(serverNameReg);
		serverNameTF.setValidateOnBlur(false);
		serverNameTF.getMessages().setRegexText(
				UIContext.Messages.remoteDeployMsgServerNameInvalid(UIContext.productNamevSphere));
		serverNameTF.setFieldLabel(UIContext.Constants
				.remoteDeployAddServerServerNameLabel());
		panel.add(serverNameTF);

		userTF = new TextField<String>();
		userTF.ensureDebugId("370D9C99-7FA5-47ae-9499-53CA42BCFC94");
		userTF.setWidth("80%");
		userTF.setAllowBlank(false);
		userTF.setValidateOnBlur(false);
		userTF.setFieldLabel(UIContext.Constants
				.remoteDeployAddServerUserNameLabel());
		panel.add(userTF);

		pwdTF = new PasswordTextField();
		pwdTF.ensureDebugId("CEFF25B6-3D87-421f-A8F8-C385FC1E3554");
		pwdTF.setPassword(true);
		pwdTF.setWidth("80%");
		pwdTF.setFieldLabel(UIContext.Constants
				.remoteDeployAddServerPasswordLabel());
		panel.add(pwdTF);

		portTF = new NumberField();
		portTF.ensureDebugId("20CF23D8-7839-492c-ACF1-8D8631DA471E");
		portTF.setPropertyEditorType(Integer.class);
		portTF.setValue(443);
		portTF.setAllowBlank(false);
		portTF.setAllowDecimals(false);
		portTF.setAllowNegative(false);
		portTF.setMaxValue(65535);
		portTF.setMinValue(0);
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
		this.protocolCB.ensureDebugId("96DBCD64-8643-4959-ADF1-2991FFB5381A");
		protocolCB.setFieldLabel(UIContext.Constants
				.remoteDeployAddServerProtocolLabel());
		protocolCB.setValue(true);
		FormData fdP = new FormData();
		fdP.setWidth(15);
		// protocolCB.setBoxLabel("");
		protocolCB.setWidth("40");
		protocolCB.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				if (protocolCB.getValue()) {
					protocol = "https";
				} else
					protocol = "http";
			}
		});
		panel.add(protocolCB, fdP);
		parent.add(panel);
	}

	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		this.serverNameTF.focus();
	}

	private void addButtons(ContentPanel cp) {
		okButton = new Button();
		okButton.ensureDebugId("13C4081F-A662-4452-A4DB-42E097BF71A9");
		okButton.setText(UIContext.Constants.ok());
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				if (window.validate()) {
					window.mask(UIContext.Constants.connectToVC());

					// TODO ?? VirtualCenterNodeModel && VirtualCenterModel
					//?type
					vcModel = new VirtualCenterModel();
					vcModel.setVcName(serverNameTF.getValue());
					vcModel.setUsername(userTF.getValue());
					vcModel.setPassword(pwdTF.getValue());
					vcModel.setProtocol(protocol);
					if(portTF.getValue().intValue()==0){
						if(protocol.equals("http")){
							vcModel.setPort(80);
						}else{
							vcModel.setPort(443);
						}
					}else{
						vcModel.setPort(portTF.getValue().intValue());
					}
					window.saveSetting();
					service.validateVC(vcModel, new BaseAsyncCallback<Integer>(){
						@Override
						public void onFailure(Throwable caught){
							super.onFailure(caught);
							window.unmask();
						}
						@Override
						public void onSuccess(Integer result){
							if(result == 0){
								chooseVM = new ChooseVirtualMachine(parentWindow);
								chooseVM.setModal(true);										
								chooseVM.show();
								window.unmask();
								window.hide();
							}else{
								MessageBox msg = new MessageBox();
								msg.setIcon(MessageBox.ERROR);
								msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNamevSphere));
								msg.setMessage(UIContext.Constants.messageBoxConnectVCFail());
								msg.setModal(true);
								Utils.setMessageBoxDebugId(msg);
								msg.show();
								window.unmask();
							}
						}
					});
					
					/*service.getAllVM(vcNodeModel,
							new BaseAsyncCallback<List<VMItemModel>>() {
								@Override
								public void onFailure(Throwable caught) {
									super.onFailure(caught);
									window.unmask();
								}

								@Override
								public void onSuccess(List<VMItemModel> result) {
									if (result != null) {
										window.unmask();
										window.hide();

										chooseVM = new ChooseVirtualMachine(parentWindow,result);
										chooseVM.setModal(true);										
										chooseVM.show();

									} else {
										MessageBox msg = new MessageBox();
										msg.setIcon(MessageBox.ERROR);
										msg.setTitle(UIContext.Constants.messageBoxTitleError());
										msg.setMessage(UIContext.Constants.messageBoxConnectVCFail());
										msg.setModal(true);
										msg.show();

										window.unmask();
									}

								}
							});*/

				}
			}
		});

		cancelButton = new Button();
		cancelButton.ensureDebugId("F3890417-EB20-4671-BFD6-EF9F16F81BDC");
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

	public boolean saveSetting() {
		if(validate()){			
		    parentWindow.vcModel.setVcName(vcModel.getVcName());
		    parentWindow.vcModel.setPassword(vcModel.getPassword());
		    parentWindow.vcModel.setUsername(vcModel.getUsername());
		    parentWindow.vcModel.setProtocol(vcModel.getProtocol());
		    parentWindow.vcModel.setPort(vcModel.getPort());
		    return true;
		}else
			return false;
		
	}
	
	/*@Override
	protected void afterShow() {
		super.afterShow();
		RefreshData();
	}*/
	
	public void RefreshData() {
		/*VirtualCenterNodeModel model=new VirtualCenterNodeModel();
		model.set("name", parentWindow.vcModel.getVCName());
		model.set("password", parentWindow.vcModel.getVcPassword());
		model.set("username", parentWindow.vcModel.getVcUsername());
		model.set("protocol", parentWindow.vcModel.getVcProtocol());*/
		
		setDefaultValue(null);
		
	}

	private void setDefaultValue(VirtualCenterNodeModel model) {
		if(model!=null){
			if(model.getName()!=null && !model.getName().trim().equals(""))
				serverNameTF.setValue(model.getName());
			if(model.get("username")!=null && !((String) model.get("username")).trim().equals(""))
				userTF.setValue((String) model.get("username"));
			if(model.get("password")!=null && !((String) model.get("password")).trim().equals(""))
			    pwdTF.setValue((String) model.get("password"));
			if(model.get("port")!=null && ((Integer) model.get("port"))!=0)
			    portTF.setValue((Integer) model.get("port"));
			if(model.get("protocol")!=null && !((String) model.get("protocol")).trim().equals(""))
			    protocol = (String) model.get("protocol");
			
			if (model.get("protocol")!=null && ((String) model.get("protocol")).equals("https")) {
				protocolCB.setValue(true);
			}
		}	
	}
}
