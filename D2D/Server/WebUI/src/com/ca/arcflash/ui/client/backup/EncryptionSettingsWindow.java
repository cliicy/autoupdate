package com.ca.arcflash.ui.client.backup;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.PasswordTextField;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

public class EncryptionSettingsWindow extends Window {

	private EncryptionSettingsWindow thisWindow;
	private TextField<String> nameTextField;
	private PasswordTextField passwordTextField;
	private TextField<String> confirmTextField;
	
	public EncryptionSettingsWindow() {		
		thisWindow = this;
		this.setWidth(350);		
		
		TableLayout layout = new TableLayout();
		layout.setWidth("90%");
		layout.setColumns(2);
		layout.setCellPadding(4);
		layout.setCellSpacing(4);
		this.setLayout(layout);
		
		TableData tableData = new TableData();
		tableData.setWidth("45%");
		
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.settingsLabelEncryptionUser());
		this.add(label,tableData);
		
		BlankValidator validator = new BlankValidator();
		nameTextField = new TextField<String>();
		nameTextField.ensureDebugId("A56F7DF2-4AF2-4001-8147-20107FB93BCD");
		nameTextField.setWidth("100%");		
		this.add(nameTextField);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.settingsLabelEncyrptionPassword());
		this.add(label,tableData);
		
		passwordTextField = new PasswordTextField();
		passwordTextField.ensureDebugId("F6C02AB4-FB62-4da1-BFBD-4A26D21D6592");
		passwordTextField.setWidth("100%");
		passwordTextField.setPassword(true);
		this.add(passwordTextField);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.settingsLabelConfirmEncyrptionPassword());
		this.add(label,tableData);
		
		confirmTextField = new TextField<String>();
		confirmTextField.ensureDebugId("C9ACF771-F763-45f2-B53F-CA44692B7050");
		confirmTextField.setWidth("100%");
		confirmTextField.setPassword(true);
		this.add(confirmTextField);		
		
		this.add(new Html(""));
		
		LayoutContainer container = new LayoutContainer();
		ColumnLayout colLayout = new ColumnLayout();
		container.setLayout(colLayout);
						
		Button button = new Button();
		button.ensureDebugId("7D0628CC-EFE8-46af-9297-197FA0EFB8D2");
		button.setText(UIContext.Constants.settingsButtonEncryptionOk());
		container.add(button);
		button.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {				
				boolean isValid = true;
				
				//Doing validation manually here instead of using validators because 
				//the validators did not work as expected in some cases 
				
				nameTextField.clearInvalid();
				confirmTextField.clearInvalid();
				passwordTextField.clearInvalid();
				
				if (nameTextField.getValue() == null || nameTextField.getValue().length() == 0)
				{
					nameTextField.forceInvalid(UIContext.Constants.validatorBlank());
					isValid = false;
				}				
				if (confirmTextField.getValue() == null)
				{
					confirmTextField.forceInvalid("Encryption password cannot be blank");
					isValid = false;
				}
				if (passwordTextField.getValue() == null)
				{
					passwordTextField.forceInvalid("Encryption password cannot be blank");
					isValid = false;
				}
				if (passwordTextField.getValue() != null 
						&& confirmTextField.getValue()  != null
						&& confirmTextField.getValue().compareTo(passwordTextField.getValue()) != 0)
				{
					//Fail
					confirmTextField.forceInvalid("Encyrption password must match");
					isValid = false;
				}				
				
				if (isValid)
				{
					//OK
					thisWindow.hide();
				}
			}			
		});
		
		button = new Button();
		button.ensureDebugId("CDA7CF5D-3849-4ccd-A345-AE3FF83FE11E");
		button.setText(UIContext.Constants.settingsButtonEncryptionCancel());
		container.add(button);
		button.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				thisWindow.hide();
			}
		});
		
		this.add(container, new TableData(Style.HorizontalAlignment.RIGHT, Style.VerticalAlignment.MIDDLE));
	}
	class BlankValidator implements Validator
	{
		@Override
		public String validate(Field<?> field, String value) {			
			if (value == null || value.trim().length() == 0)
			{
				return UIContext.Constants.validatorBlank();
			}
			return null;
		}

	}
}
