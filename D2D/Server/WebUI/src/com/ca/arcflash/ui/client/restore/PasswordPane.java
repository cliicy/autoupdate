package com.ca.arcflash.ui.client.restore;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.PasswordTextField;
import com.ca.arcflash.ui.client.common.Utils;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

public class PasswordPane extends VerticalPanel {
	
	private PasswordTextField pwdField;
	private boolean isGettingPassword = false;
	private Label encryptTitle = null;
	private boolean isLitePasswordPane = false;  // a lite password pane used in message dialog
	private Label encrptlabel = new Label();
	private Label  encrptPasswordlabel = new Label();
	private String encrptlabelTxt = UIContext.Constants.recoveryPointsNeedSessionPassword();
	private String pwdDebugID = "FC1BD705-ED8E-4366-890F-A007116225F0";
	

	public String getEncrptlabelTxt() {
		return encrptlabelTxt;
	}

	public void setEncrptlabelTxt(String encrptlabelTxt) {
		this.encrptlabelTxt = encrptlabelTxt;
		encrptlabel.setText(encrptlabelTxt);
	}

	public Label getEncrptlabel() {
		return encrptlabel;
	}
	
	public Label getEncrptPasswordlabel() {
		return encrptPasswordlabel;
	}

	public PasswordPane()
	{		
		this.setEncryptTitle(new Label(UIContext.Constants.backupEncryptionPassword()));		
	}
	
	public void setFocus() {
		pwdField.clear();
		pwdField.focus();
	}
	
	public void forceInvalid(String msg) {
		pwdField.forceInvalid(msg);
	}
	
	 public void clearInvalid() {
		 pwdField.clearInvalid();
	 }

	@Override
	public void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		// render a lite pane
		if (isLitePasswordPane)
		{
			renderLite();
			return;
		}
		
		FlexTable flexTable = new FlexTable();
		flexTable.setCellPadding(5);
		flexTable.setCellSpacing(5);
		add(flexTable);
		
		flexTable.getFlexCellFormatter().setColSpan(0, 0, 4);
		flexTable.setWidget(0, 0, encryptTitle);
		
		encrptlabel.setText(encrptlabelTxt);
		encrptlabel.addStyleName("restoreWizardSubItemDescription");
		flexTable.getFlexCellFormatter().setColSpan(1, 0, 4);
		flexTable.setWidget(1, 0, encrptlabel);
		
		encrptPasswordlabel.setText(UIContext.Constants.settingsLabelEncyrptionPassword());
		encrptPasswordlabel.addStyleName("restoreWizardSubItemDescription");
		flexTable.setWidget(2, 0, encrptPasswordlabel);
		
		pwdField = new PasswordTextField();
		pwdField.ensureDebugId(pwdDebugID);
		pwdField.setPassword(true);
		pwdField.setMaxLength(Utils.EncryptionPwdLen);
		pwdField.setWidth(180);
		flexTable.setWidget(2, 3, pwdField);
		
		LabelField blank4Invalid = new LabelField("");
		blank4Invalid.ensureDebugId("cb92b9bf-3459-450b-80fb-88e7d08f1678");
		blank4Invalid.setWidth(20);
		flexTable.setWidget(2, 4, blank4Invalid);
	}
	
	public void setDebugID(String debugID) {
		pwdDebugID = debugID;
	}
	
	public String getPassword() {
		if( isGettingPassword )
			return "";
		else 
			return pwdField.getValue();
	}
	
	public void setPassword(String in_Password) {
		pwdField.setValue(in_Password);
	}
	
	public boolean validate(){
		boolean isValidate = true;
		String pwdString = pwdField.getValue();
		String msgStr = null;
		if(pwdField.isMaxLengthExceeded())
		{
			msgStr = UIContext.Constants.PasswordBeyondLength();
			isValidate = false;
		}
		
		if(!isValidate){
			MessageBox msg = new MessageBox();
			msg.setIcon(MessageBox.ERROR);
			msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
			msg.setMessage(msgStr);
			msg.setModal(true);
			Utils.setMessageBoxDebugId(msg);
			msg.show();
		}
		return isValidate;
	}
	
	public void autoFillPassword(String sessionGuid){
		if( sessionGuid == null || sessionGuid.isEmpty() )
			return;
		
		// liuwe05 2011-2-14 fix Issue: 20023965    Title: SESSION PWD LOST AFT GOING BCK
		// if the session GUID is not changed and the pwdField is not empty, we don't need to automatically fill the password again
		// otherwise, it might overwrite the user input.
		if (!shouldAutoFillPassword(sessionGuid))
		{
			return;
		}
		
		pwdField.clear();
		isGettingPassword = true;
		pwdField.disable();
		pwdField.mask(UIContext.Constants.loadingPassword());
		CommonServiceAsync service = GWT.create(CommonService.class);
		service.getSessionPasswordBySessionGuid(new String[]{sessionGuid}, new BaseAsyncCallback<String[]>(){

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				isGettingPassword = false;
				pwdField.enable();
				pwdField.unmask();
				pwdField.clear();
			}

			@Override
			public void onSuccess(String[] result) {
				super.onSuccess(result);
				isGettingPassword = false;
				pwdField.enable();
				pwdField.unmask();
				if (result != null && result.length > 0 && result[0] != null) {
					pwdField.setValue(result[0]);
				}
				else {
					pwdField.clear();
				}
			}
		});
	}

	public Label getEncryptTitle() {
		return encryptTitle;
	}

	public void setEncryptTitle(Label encryptTitle) {
		this.encryptTitle = encryptTitle;
		this.encryptTitle.addStyleName("restoreWizardSubItem");
	}


	// for a lite password pane
	public boolean isLitePasswordPane()
	{
		return isLitePasswordPane;
	}

	public void setLitePasswordPane(boolean isLitePasswordPane)
	{
		this.isLitePasswordPane = isLitePasswordPane;
	}
	
	protected void renderLite()
	{
		FieldSet fieldSet = new FieldSet();
		fieldSet.setHeadingHtml(UIContext.Constants.EncryptionPassword());
		
		LabelField encrptlabel = new LabelField(UIContext.Constants.restoreGRTPasswordForCatalog());
		encrptlabel.addStyleName("restoreWizardSubItemDescription");
		fieldSet.add(encrptlabel);
		
		
		LayoutContainer pwsContainer = new LayoutContainer();
		FormLayout layout = new FormLayout();
		layout.setLabelWidth(75); 		
		pwsContainer.setLayout(layout);  	
		pwsContainer.setStyleAttribute("padding-top", "5px");		
		
		pwdField = new PasswordTextField();
		pwdField.ensureDebugId("40814cc8-5df4-4558-9e66-bebddeeec6aa");
		pwdField.setFieldLabel(UIContext.Constants.settingsLabelEncyrptionPassword());
		pwdField.setPassword(true);
		pwdField.setMaxLength(Utils.EncryptionPwdLen);
		pwdField.addStyleName("restoreWizardLeftSpacing");
		pwdField.setWidth(230);	
		pwdField.setAllowBlank(false);
		pwdField.clearInvalid();
		
		pwsContainer.add(pwdField);
		
		fieldSet.add(pwsContainer);
		add(fieldSet);
	}

	protected static final String KEY_GUID = "sessionGuid";
	// should automatically fill the session password or not
	protected boolean shouldAutoFillPassword(String currentSessionGuid)
	{
		boolean bShould = true; // by default is should
		
		if (pwdField != null && currentSessionGuid != null)
		{
			// get the associated session GUID
			String previousSessionGuid = pwdField.getData(KEY_GUID);
			
			// if it is the same session
			if (previousSessionGuid != null && previousSessionGuid.equalsIgnoreCase(currentSessionGuid))
			{
				// and the session password field is not empty
				if (pwdField.getValue() != null && !(pwdField.getValue().isEmpty()))
				{
					// don't overwrite it
					bShould = false;
				}
			}	
			
			// set the associated session GUID
			pwdField.setData(KEY_GUID, currentSessionGuid);
		}
		
		return bShould;
	}

	public boolean isMaxLengthExceeded()
	{
		return pwdField != null && pwdField.isMaxLengthExceeded();
	}

}
