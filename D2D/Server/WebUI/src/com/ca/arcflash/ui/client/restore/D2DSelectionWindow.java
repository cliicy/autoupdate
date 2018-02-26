package com.ca.arcflash.ui.client.restore;

import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.PasswordTextField;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.BackupD2DModel;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.KeyNav;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;

public class D2DSelectionWindow extends Window {
	
	final LoginServiceAsync service = GWT.create(LoginService.class);
	
	private TextField<String> rpsHostnameField;
	private TextField<String> usernameField;
	private PasswordTextField passwordField;
	private Radio httpProtocol;
	private Radio httpsProtocol;
	private RadioGroup protocolGroup;
	private NumberField portTextField;
	private KeyNav<ComponentEvent> enterKey;
	private ComboBox<BackupD2DModel> d2dComboBox;
	private ListStore<BackupD2DModel> d2dList;
	private Button okButton;
	private Button cancelButton;
	private final static int MIN_BUTTON_WIDTH = 90; 
	private D2DSelectionWindow thisWindow;
	private RPSSelectionPanel rpsPanel;
	private String oldhostname;
	private String oldusername;
	private String oldpassword;
	private String oldprotocol;
	private int    oldport;
	
	public D2DSelectionWindow(RPSSelectionPanel selectionPanel){
		enterKey = new KeyNav<ComponentEvent>(this) {
			public void handleEvent(ComponentEvent ce) {
				if (ce.getKeyCode() == 13)
					okButton.fireEvent(Events.Select);
				else if (ce.getKeyCode() == 27)
					cancelButton.fireEvent(Events.Select);
			}
		};
		this.rpsPanel = selectionPanel;
		this.thisWindow = this;
		this.setWidth(350);
		this.setHeadingHtml(UIContext.Messages.restoreChangeRPSServer(UIContext.productNameRPS));
		
		TableLayout layout = new TableLayout();
		layout.setColumns(2);
		layout.setCellPadding(4);
		layout.setWidth("95%");
		setLayout(layout);
		
		TableData tb30 = new TableData();
		tb30.setWidth("30%");
		tb30.setHorizontalAlign(HorizontalAlignment.RIGHT);
		
		TableData tb70 = new TableData();
		tb70.setWidth("70%");
		tb70.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.restoreD2DSelectionHostname());
		add(label, tb30);
		
		rpsHostnameField = new TextField<String>();
		rpsHostnameField.setAllowBlank(false);
		rpsHostnameField.setWidth("100%");
		rpsHostnameField.ensureDebugId("23b82f53-b2ad-4990-af18-d18aabde9cb7");
		add(rpsHostnameField, tb70);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.restoreD2DSelectionUsername());
		add(label, tb30);
		
		usernameField = new TextField<String>();
		usernameField.setAllowBlank(false);
		usernameField.setWidth("100%");
		usernameField.ensureDebugId("be56e57d-92eb-42c0-8658-949b288306e1");
		add(usernameField, tb70);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.restoreD2DSelectionPassword());
		add(label, tb30);
		
		passwordField = new PasswordTextField();
		passwordField.setAllowBlank(false);
		passwordField.setWidth("100%");
		passwordField.ensureDebugId("8c366378-6dfa-4361-a1fc-d7ee13a653e9");
		passwordField.setPassword(true);
		add(passwordField, tb70);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.restoreD2DSelectionProtocol());
		add(label, tb30);
		
		LayoutContainer protocolContainer = new LayoutContainer();
		TableLayout protocolTL = new TableLayout();
		protocolTL.setColumns(2);
		protocolContainer.setLayout(protocolTL);
		
		TableData proData = new TableData();
		
		httpProtocol = new Radio();
		httpProtocol.setValue(true);
		httpProtocol.ensureDebugId("8dffbfbe-6922-43cb-abf0-21b53e10b0ec");
		httpProtocol.setBoxLabel(UIContext.Constants.restoreD2DSelectionProtocolHttp());
		
		protocolContainer.add(httpProtocol, proData);
		httpsProtocol = new Radio();
		httpsProtocol.ensureDebugId("f7b5d82c-f61b-4e0c-b115-9f5850acbce2");
		httpsProtocol.setBoxLabel(UIContext.Constants.restoreD2DSelectionProtocolHttps());
		
		protocolContainer.add(httpsProtocol, proData);
		
		protocolGroup = new RadioGroup();
		protocolGroup.add(httpProtocol);
		protocolGroup.add(httpsProtocol);
		
		add(protocolContainer, tb70);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.restoreD2DSelectionPort());
		add(label, tb30);
		
		portTextField = new NumberField();
		portTextField.setAllowBlank(false);
		portTextField.setWidth("100%");
		portTextField.setValue(8014);
		portTextField.ensureDebugId("bb89e070-23a4-47c1-8210-623698468dd6");
		add(portTextField, tb70);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.restoreD2DSelectionBackedupD2D());
		add(label, tb30);
		
		d2dList = new ListStore<BackupD2DModel>();  
	    d2dComboBox = new ComboBox<BackupD2DModel>();
	    d2dComboBox.setAllowBlank(false);
	    d2dComboBox.setEmptyText(UIContext.Constants.restoreD2DSelectionSelectD2D());
	    d2dComboBox.ensureDebugId("3a0fbed4-3a1f-4ea9-ad57-e71971858f27");
	    d2dComboBox.setWidth("100%");  
	    d2dComboBox.setStore(d2dList);  
	    d2dComboBox.setDisplayField("displayName");
	    d2dComboBox.setTypeAhead(true);  
	    d2dComboBox.setTriggerAction(TriggerAction.ALL);
	    d2dComboBox.addListener(Events.OnMouseDown, new Listener<FieldEvent>(){
			@Override
			public void handleEvent(FieldEvent be) {
				if(!isInputValidate()||!isValueChanged())
					return;
				thisWindow.mask(UIContext.Constants.restoreD2DSelectionLoadingD2D());
				setOldValue();
				service.getBackupD2DList(rpsHostnameField.getValue(), usernameField.getValue(), passwordField.getValue(), getProtocol(), portTextField.getValue().intValue(), new BaseAsyncCallback<List<BackupD2DModel>>(){
					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						d2dList.removeAll();
						d2dComboBox.setEmptyText(UIContext.Messages.restoreD2DSelectionFailToGetD2D(
								UIContext.productNameRPS, UIContext.productNameD2D));
						thisWindow.unmask();
					}
					@Override
					public void onSuccess(List<BackupD2DModel> result){
						if(result!=null&&result.size()>0){
							d2dList.removeAll();
							processList(result);
							d2dList.add(result);
							d2dComboBox.setValue(result.get(0));
						}else{
							d2dComboBox.setEmptyText(UIContext.Messages.restoreD2DSelectionNoD2D(
									UIContext.productNameD2D, UIContext.productNameRPS));
						}
						thisWindow.unmask();
					}
				});
			}
	    });
		add(d2dComboBox);
	    
		add(new HTML(""));
		
		layout = new TableLayout();
		layout.setColumns(2);
		layout.setCellSpacing(5);
		LayoutContainer container = new LayoutContainer();
		container.setLayout(layout);
		
		TableData tb = new TableData();
		tb.setWidth("70%");
		tb.setHorizontalAlign(HorizontalAlignment.RIGHT);
		add(container,tb);
		
		okButton = new Button();
		okButton.ensureDebugId("59AC0E8C-D4DD-4d8c-8ADC-8E733F8EA1C1");
		okButton.setText(UIContext.Constants.ok());
		okButton.setMinWidth(MIN_BUTTON_WIDTH);
		container.add(okButton, new TableData(Style.HorizontalAlignment.RIGHT,
				Style.VerticalAlignment.MIDDLE));
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				if(!isInputValidateIncludeD2D())
					return;
				BackupD2DModel d2d = d2dComboBox.getValue();
				rpsPanel.setUsername(getUsername(d2d));
				rpsPanel.setPassword(getPassword(d2d));
				//rpsPanel.setDestination(getDestination(d2d));
				rpsPanel.setDestination(d2d.getDestination());
				rpsPanel.setDataStoreName(d2d.getDataStoreName());
				rpsPanel.setRPSServerName(rpsHostnameField.getValue());
				thisWindow.hide();
			}

		});
		
		cancelButton = new Button();
		cancelButton.ensureDebugId("9D4EB3A8-91A6-4d72-8F83-DAE835B5A789");
		cancelButton.setText(UIContext.Constants.cancel());
		cancelButton.setMinWidth(MIN_BUTTON_WIDTH);
		container.add(cancelButton, new TableData(Style.HorizontalAlignment.RIGHT,
				Style.VerticalAlignment.MIDDLE));

		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				thisWindow.hide();
			}

		});
		
	}
	
	private String getProtocol(){
		return httpProtocol.getValue() == true?"http:":"https:";
	}
	
	private boolean isInputValidateIncludeD2D(){
		return isInputValidate()&&d2dComboBox.validate();
	}
	
	private boolean isInputValidate(){
		return rpsHostnameField.validate()&&usernameField.validate()&&passwordField.validate()&&portTextField.validate(); 
	}
	
	private void setOldValue(){
		oldhostname = rpsHostnameField.getValue();
		oldusername = usernameField.getValue();
		oldpassword = passwordField.getValue();
		oldprotocol = getProtocol();
		oldport     = portTextField.getValue().intValue();
		
	}
	
	private boolean isValueChanged(){
		return (!rpsHostnameField.getValue().equals(oldhostname))
				||(!usernameField.getValue().equals(oldusername))
				||(!passwordField.getValue().equals(oldpassword))
				||(portTextField.getValue().intValue()!=oldport)
				||(!getProtocol().equals(oldprotocol));
	}
	
	private void processList(List<BackupD2DModel> list){
		for(BackupD2DModel d2d : list){
			d2d.set("displayName", d2d.getHostName()+"("+d2d.getDataStoreName()+")");
		}
	}
	
	private String getDestination(BackupD2DModel d2d){
		if(d2d.getDestination().startsWith("\\")){
			if(d2d.getDestination().endsWith("\\"))
				return d2d.getDestination()+d2d.getHostName();
			else
				return d2d.getDestination()+"\\"+d2d.getHostName();
		}else{
			StringBuffer retDestination  = new StringBuffer().append("\\\\").append(rpsHostnameField.getValue()).append("\\").append(d2d.getDestination().replace(":", "$"));
			if(!d2d.getDestination().endsWith("\\"))
				retDestination.append("\\");
			retDestination.append(d2d.getHostName());
			return retDestination.toString();
		}
	}
	
	private String getUsername(BackupD2DModel d2d){
		if(d2d.getDestination().startsWith("\\")){
			return d2d.getDesUsername();
		}else{
			return usernameField.getValue();
		}
	}
	
	private String getPassword(BackupD2DModel d2d){
		if(d2d.getDestination().startsWith("\\")){
			return d2d.getDesPassword();
		}else{
			return passwordField.getValue();
		}
	}
	
	@Override
	protected void onRender(Element target, int index){
		super.onRender(target, index);
		this.setFocusWidget(rpsHostnameField);
	}

}
