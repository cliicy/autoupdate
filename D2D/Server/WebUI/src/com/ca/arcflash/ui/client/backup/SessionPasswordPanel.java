package com.ca.arcflash.ui.client.backup;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.PasswordTextField;
import com.ca.arcflash.ui.client.common.Utils;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.Element;

/**
 * When use this panel, the password must NOT be null.
 * @author zhawe03
 *
 */
public class SessionPasswordPanel extends LayoutContainer {
	
	private PasswordTextField sessionPasswordField;
	
	private PasswordTextField sessionPasswordReTypeField;
	
	private boolean enableEncryption = false;
	
	public SessionPasswordPanel(){
		
		TableLayout tableSessionPasswordLayout = new TableLayout();
		tableSessionPasswordLayout.setCellPadding(2);
		tableSessionPasswordLayout.setCellSpacing(2);
		tableSessionPasswordLayout.setColumns(2);
		setLayout(tableSessionPasswordLayout);
		
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.settingsLabelSessionPassword());
		label.addStyleName("backupDestinationEncrypt");
		
		add(label);
		
		sessionPasswordField = new PasswordTextField();
		sessionPasswordField.ensureDebugId("c6b33493-a410-4463-a5a3-247bc87b3f81");
		sessionPasswordField.setPassword(true);
		sessionPasswordField.setWidth(200);
		sessionPasswordField.setValue("");
		sessionPasswordField.setMaxLength(Utils.EncryptionPwdLen);
		
		sessionPasswordField.addListener(Events.OnBlur, new Listener<BaseEvent>(){
			@Override
			public void handleEvent(BaseEvent be) {
				sessionPasswordReTypeField.validate();
				
			}});
		TableData data = new TableData();
		data.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		add(sessionPasswordField, data);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.sessionPasswordReType());
		label.setValidateOnBlur(true);		
		label.addStyleName("backupDestinationEncrypt");
		add(label);
		
		sessionPasswordReTypeField = new PasswordTextField(){
			@Override
			protected boolean validateValue(String value) {
				String val = sessionPasswordField.getValue();
				forceInvalidText = null;
				if (val != null && !val.equals(value)) {
					forceInvalidText = UIContext.Constants
							.verifySessionPassword();
				}						
				return super.validateValue(value);
			}
			
		};
		sessionPasswordReTypeField.ensureDebugId("93e3debd-e4c6-4a2c-a254-94f29d57c199");
		sessionPasswordReTypeField.setPassword(true);
		sessionPasswordReTypeField.setWidth(200);
		sessionPasswordReTypeField.setValue("");
		sessionPasswordReTypeField.setMaxLength(Utils.EncryptionPwdLen);
		
		add(sessionPasswordReTypeField, data);
		
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
	}
	
	public void setPassword(String password){
		sessionPasswordField.setValue(password);
		sessionPasswordReTypeField.setValue(password);
	}
	
	public String validate(){
		String sessionPassword = sessionPasswordField.getValue();
		sessionPassword = sessionPassword == null ? "":sessionPassword;
		String reTypeSessionPassword = sessionPasswordReTypeField.getValue();
		reTypeSessionPassword = reTypeSessionPassword == null ? "":reTypeSessionPassword;
		boolean isValid = true;
		String msgStr = null;
		
		if(sessionPassword.length() == 0 && reTypeSessionPassword.length()==0){
			msgStr = UIContext.Messages.sessionPwdNull(UIContext.productNameRPS);
			isValid = false;
		}
		else if(!sessionPassword.equals(reTypeSessionPassword))
		{
			msgStr = UIContext.Constants.verifySessionPassword();
			isValid = false;
		}
		else if(sessionPasswordField.isMaxLengthExceeded())
		{
			msgStr = UIContext.Constants.sessionPasswordBeyondLength();
			isValid = false;
		}
		
		if(!isValid){
			sessionPasswordField.focus();
		}
		
		return msgStr;
	}
	
	public String getPassword(){
		return sessionPasswordField.getValue();
	}
	
	public void setEnablePassword(boolean isEnable){
		sessionPasswordField.setEnabled(isEnable);
		sessionPasswordReTypeField.setEnabled(isEnable);
	}
	
	public void setEnableEncryption(boolean enable){
		this.enableEncryption = enable;
	}
	
	public void clear() {
		sessionPasswordField.clear();
		sessionPasswordReTypeField.clear();
	}
}
