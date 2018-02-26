package com.ca.arcflash.ui.client.vsphere.backup;

import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.PasswordTextField;
import com.ca.arcflash.ui.client.model.VMItemModel;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;

public class SetCredentialWindow extends Window {
	
	private SetCredentialWindow thisWindow;
	
	private ChooseVirtualMachine parent;
	
	private TextField<String> usernameField;
	
	private PasswordTextField passwordField;
	
	private Button okButton;
	
	private Button cancelButton;
	
	private int MIN_BUTTON_WIDTH = 90; 
	
	public SetCredentialWindow(ChooseVirtualMachine parentWindow){
		this.thisWindow = this;
		this.parent = parentWindow;
		this.setWidth(350);
		
		this.setHeadingHtml(UIContext.Constants.setCredentialWindowHeading());
		TableLayout layout = new TableLayout();
		layout.setColumns(2);
		layout.setCellPadding(4);
		layout.setWidth("95%");
		this.setLayout(layout);
		TableData tb = new TableData();
		tb.setWidth("30%");
		tb.setHorizontalAlign(HorizontalAlignment.CENTER);
		
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.loginLabelUsername());
		this.add(label, tb);
		
		tb = new TableData();
		tb.setWidth("70%");
		tb.setHorizontalAlign(HorizontalAlignment.LEFT);
		usernameField = new TextField<String>();
		usernameField.ensureDebugId("29FD98C0-2E5F-41ce-919F-AF5BBE02656C");
		usernameField.setAllowBlank(false);
		usernameField.setWidth("100%");
		this.add(usernameField, tb);
		
		tb = new TableData();
		tb.setWidth("30%");
		tb.setHorizontalAlign(HorizontalAlignment.CENTER);
		label = new LabelField();
		label.setValue(UIContext.Constants.loginLabelPassword());
		this.add(label, tb);
		
		tb = new TableData();
		tb.setWidth("70%");
		tb.setHorizontalAlign(HorizontalAlignment.LEFT);
		passwordField = new PasswordTextField();
		passwordField.ensureDebugId("F484F11D-C3B5-4e13-878F-C270451B1B01");
		passwordField.setPassword(true);
		passwordField.setAllowBlank(false);
		passwordField.setWidth("100%");
		this.add(passwordField, tb);
		
		this.add(new HTML(""));
		
		layout = new TableLayout();
		layout.setColumns(2);
		layout.setCellSpacing(5);
		LayoutContainer container = new LayoutContainer();
		container.setLayout(layout);
		
		tb = new TableData();
		tb.setWidth("70%");
		tb.setHorizontalAlign(HorizontalAlignment.RIGHT);
		this.add(container,tb);
		okButton = new Button();
		okButton.ensureDebugId("42C5DDFA-5EA5-4f61-9F30-ED96E8BF055B");
		okButton.setText(UIContext.Constants.ok());
		okButton.setMinWidth(MIN_BUTTON_WIDTH);
		container.add(okButton, new TableData(Style.HorizontalAlignment.RIGHT,
				Style.VerticalAlignment.MIDDLE));
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				if(!usernameField.isValid()){
					return;
				}
				if(!passwordField.isValid()){
					return;
				}
				List<VMItemModel> vmSelectedList = parent.getVmGrid().getSelectionModel().getSelectedItems();
				for(VMItemModel model : vmSelectedList){
					model.setUsername(usernameField.getValue());
					model.setPassword(passwordField.getValue());
				}
				parent.getVmGrid().getView().refresh(false);
				thisWindow.hide();
			}
			
		});
		
		cancelButton = new Button();
		cancelButton.ensureDebugId("89833860-4FD5-4b3f-AEB4-38A865EBC640");
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
	@Override
	protected void onRender(Element target, int index){
		super.onRender(target, index);
		this.setFocusWidget(usernameField);
		this.passwordField.focus();
		
	}

}
