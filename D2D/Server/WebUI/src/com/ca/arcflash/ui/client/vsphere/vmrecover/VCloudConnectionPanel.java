package com.ca.arcflash.ui.client.vsphere.vmrecover;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.PasswordTextField;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.VCloudDirectorModel;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout.VBoxLayoutAlign;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Element;

public class VCloudConnectionPanel extends LayoutContainer {
	private final LoginServiceAsync service = GWT.create(LoginService.class);

	private TextField<String> hostTextField;
	private TextField<String> userTextField;
	private PasswordTextField passwordField;
	private NumberField portNumberField = new NumberField();
	private RadioGroup protocolGroup;
	private Radio httpProtocol;
	private Radio httpsProtocol;
	private Button loginButton;

	private BaseAsyncCallback<BaseModelData> callback;
	private VCloudDirectorModel inputVCloudDirectorModel;
	
	public VCloudConnectionPanel(BaseAsyncCallback<BaseModelData> callback) {
		this.callback = callback;
		createContent();
	}
	
	public void setInputVCloudDirectorModel(VCloudDirectorModel vCloudDirectorModel) {
		this.inputVCloudDirectorModel = vCloudDirectorModel;
	}
	
	public Button getLoginButton() {
		return loginButton;
	}
	
	public String getHostName() {
		return hostTextField.getValue();
	}
	
	public void setHostName(String host) {
		hostTextField.setValue(host);
	}

	public String getUserName() {
		return userTextField.getValue();
	}
	
	public void setUserName(String user) {
		userTextField.setValue(user);
	}

	public String getPassword() {
		return passwordField.getValue();
	}
	
	public void setPassword(String password) {
		passwordField.setValue(password);
	}

	public int getPort() {
		return portNumberField.getValue().intValue();
	}
	
	public void setPort(Integer port) {
		if (port == null) {
			String protocol = getProtocol();
			if ("HTTP".equalsIgnoreCase(protocol)){
				port = 80;
			} else {
				port = 443;
			}
		} 
		portNumberField.setValue(port);
	}

	public String getProtocol() {
		return httpProtocol.getValue() == true ? "HTTP" : "HTTPS";
	}
	
	public void setProtocol(String protocol) {
		if ("HTTP".equalsIgnoreCase(protocol)){
			httpProtocol.setValue(Boolean.TRUE);
			httpsProtocol.setValue(Boolean.FALSE);
		} else {
			httpProtocol.setValue(Boolean.FALSE);
			httpsProtocol.setValue(Boolean.TRUE);
		}
	}
	
	public void connectToServer() {
		if (validate(true)) {
			loginButton.setEnabled(false);
			mask(UIContext.Constants.vAppRestoreVCloudConnectionMask());

			String password = passwordField.getValue();
			if (password == null) {
				password = "";
			}

			final VCloudDirectorModel directorModel = new VCloudDirectorModel();
			directorModel.setName(getHostName());
			directorModel.setUsername(getUserName());
			directorModel.setPassword(password);
			directorModel.setPort(getPort());
			directorModel.setProtocol(getProtocol());

			service.validateVCloud(directorModel, new BaseAsyncCallback<Integer>() {
				@Override
				public void onSuccess(Integer result) {
					loginButton.setEnabled(true);
					unmask();
					callback.onSuccess(storeServerModelData());
				}

				@Override
				public void onFailure(Throwable caught) {
					loginButton.setEnabled(true);
					unmask();
					super.onFailure(caught);
				}
			});
		}
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		applyDefaultInfo();
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				hostTextField.focus();
			}
		});
	}
	
	private void createContent() {
		this.setWidth(600);
		this.setHeight(420);
		this.setLayout(new RowLayout());
		
		//description
		LabelField label = new LabelField(UIContext.Constants.vAppRestoreVCloudConDiscription());
		label.setStyleName("restoreWizardTitle");
		this.add(label, new RowData(1, -1, new Margins(50, 0, 10, 95)));
		
		LayoutContainer container = new LayoutContainer();
		this.add(container, new RowData(1, 1, new Margins(0, 0, 0, 85)));
		TableLayout tl = new TableLayout(3);
		tl.setCellPadding(5);
		tl.setCellSpacing(5);
		tl.setWidth("100%");
		container.setLayout(tl);
		
		TableData labelTD = new TableData();
		labelTD.setWidth("40%");

		TableData fieldTD = new TableData();
		fieldTD.setWidth("60%");
		fieldTD.setColspan(2);
		
		// host name
		label = new LabelField();
		label.setValue(UIContext.Constants.vAppRestoreVCloudServerLabel());
		container.add(label, labelTD);

		hostTextField = new TextField<String>();
		hostTextField.ensureDebugId("7df4c57d-8b10-4200-9bff-6873bafc7713");
		hostTextField.setAllowBlank(false);
		hostTextField.setWidth(200);
		container.add(hostTextField, fieldTD);

		// user
		label = new LabelField();
		label.setValue(UIContext.Constants.vAppRestoreVCloudUserLabel());
		container.add(label, labelTD);

		userTextField = new TextField<String>();
		userTextField.ensureDebugId("ee06cfc5-8e85-42d4-88d0-1c8dcf061b4f");
		userTextField.setAllowBlank(false);
		userTextField.setWidth(200);
		container.add(userTextField, fieldTD);

		// password
		label = new LabelField();
		label.setValue(UIContext.Constants.vAppRestoreVCloudPasswordLabel());
		container.add(label, labelTD);

		passwordField = new PasswordTextField();
		passwordField.ensureDebugId("c904c8f2-0914-467b-9c72-851427a76b4d");
		passwordField.setPassword(true);
		passwordField.setWidth(200);
		container.add(passwordField, fieldTD);

		// port
		label = new LabelField();
		label.setValue(UIContext.Constants.vAppRestoreVCloudPortLabel());
		container.add(label, labelTD);
		
		portNumberField.setWidth("30%");
		portNumberField.setAllowDecimals(false);
		portNumberField.setAllowNegative(false);
		container.add(portNumberField, fieldTD);

		// protocol
		label = new LabelField();
		label.setValue(UIContext.Constants.vAppRestoreVCloudcProtocolLabel());
		container.add(label, labelTD);

		LayoutContainer protocolContainer = new LayoutContainer();
		TableLayout protocolTL = new TableLayout();
		protocolTL.setColumns(2);
		protocolContainer.setLayout(protocolTL);

		httpsProtocol = new Radio();
		httpsProtocol.ensureDebugId("8ae05acd-5e3b-49aa-b0d8-b01e5e5fe30e");
		httpsProtocol.setBoxLabel(UIContext.Constants.vmRecoveryProtocolHttps());
		httpsProtocol.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				if (httpsProtocol.getValue() == true) {
					portNumberField.setValue(443);
				}
			}
		});
		httpsProtocol.setValue(Boolean.TRUE);
		protocolContainer.add(httpsProtocol);

		httpProtocol = new Radio();
		httpProtocol.ensureDebugId("002e1ae1-874d-44c8-835e-19a30fe94685c3");
		httpProtocol.setBoxLabel(UIContext.Constants.vmRecoveryProtocolHttp());
		httpProtocol.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				if (httpProtocol.getValue() == true) {
					portNumberField.setValue(80);
				}
			}
		});
		protocolContainer.add(httpProtocol);

		protocolGroup = new RadioGroup();
		protocolGroup.add(httpProtocol);
		protocolGroup.add(httpsProtocol);
		container.add(protocolContainer, fieldTD);
		
		LayoutContainer bottomContainer = new LayoutContainer(new RowLayout(Orientation.HORIZONTAL));
		bottomContainer.ensureDebugId("89095551-5c1b-4636-aed9-b3bfd252f722");
		bottomContainer.setWidth("100%");
		bottomContainer.setHeight(25);
		this.add(bottomContainer, new RowData(1, -1, new Margins(30, 10, 10, 10)));
		
		// login
		LayoutContainer buttonContainer = new LayoutContainer(new VBoxLayout());
		buttonContainer.setHeight("100%");
		bottomContainer.add(buttonContainer, new RowData(0.5, 1));
		loginButton = new Button();
		loginButton.ensureDebugId("16f1af5c-ac61-4e2d-be2d-02abfd9796a7");
		loginButton.setText(UIContext.Constants.vAppRestoreVCloudConLogin());
		loginButton.setWidth(80);
		buttonContainer = new LayoutContainer(new VBoxLayout(VBoxLayoutAlign.RIGHT));
		buttonContainer.setHeight("100%");
		buttonContainer.add(loginButton);
		bottomContainer.add(buttonContainer, new RowData(0.5, 1));
		
		loginButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				connectToServer();
			}
		});
	}
	
	private boolean validate(boolean isClose) {
		boolean isValid = true;
		if (Utils.isEmptyOrNull(hostTextField.getValue())) {
			isValid = false;
			hostTextField.markInvalid(UIContext.Constants.vAppRestoreRequiredFieldMark());
		} else {
			hostTextField.clearInvalid();
		}
		
		if (Utils.isEmptyOrNull(userTextField.getValue())) {
			isValid = false;
			userTextField.markInvalid(UIContext.Constants.vAppRestoreRequiredFieldMark());
		} else {
			userTextField.clearInvalid();
		}
		
		if (portNumberField.getValue() == null) {
			isValid = false;
			portNumberField.markInvalid(UIContext.Constants.vAppRestoreRequiredFieldMark());
		} else {
			portNumberField.clearInvalid();
		}
		
		return isValid;
	}
	
	private void applyDefaultInfo() {
		loginButton.setVisible(true);
		hostTextField.enable();

		hostTextField.setValue(inputVCloudDirectorModel.getName());
		userTextField.setValue(inputVCloudDirectorModel.getUsername());
		passwordField.setValue(inputVCloudDirectorModel.getPassword());
		setProtocol(inputVCloudDirectorModel.getProtocol());
		setPort(inputVCloudDirectorModel.getPort());
	}
	
	private VCloudDirectorModel storeServerModelData() {
		inputVCloudDirectorModel.setName(getHostName());
		inputVCloudDirectorModel.setUsername(getUserName());
		inputVCloudDirectorModel.setPassword(getPassword());
		inputVCloudDirectorModel.setProtocol(getProtocol());
		inputVCloudDirectorModel.setPort(getPort());

		return inputVCloudDirectorModel;
	}
}