package com.ca.arcflash.ui.client.homepage;

import java.util.HashMap;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.OnLineHelpTopics;
import com.ca.arcflash.ui.client.notifications.events.NotificationEventHandler;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * @author RamaKishoreReddy.Upp
 *
 */
public class EntitlementRegistrationWindow extends Window{

	/**
	 * @param args
	 */
	private EntitlementRegistrationContainer registrationContainer;
	public static Button registerButton;
	private Button cancelButton;
	private Button helpButton;
	private EntitlementRegistrationWindow thisWindow;
	
	
	private static final EntitlementRegisterServiceAsync entitlRegisterService = (EntitlementRegisterServiceAsync) GWT.create(EntitlementRegisterService.class);
	
	private static String widgStyle1 = "<div style=\"word-wrap: break-word; width:";
	private static String widgStyle2 = ";\"" + ">";
	private static String widgStyle3 = "</div>";
	
	public EntitlementRegistrationWindow()
	{
		this.setSize("650px", "620px");
		this.setHeadingText(UIContext.Constants.registrationWindowHeader());		
		this.setBorders(true);
		this.setBodyStyle("background-color: white;");
		this.setModal(true);
		
		thisWindow = this;
		
		registrationContainer = new EntitlementRegistrationContainer(this);
		
		thisWindow.add(registrationContainer);
		
		helpButton = new Button(UIContext.Constants.regHelp());
		helpButton.setTitle(UIContext.Constants.regHelp());
		helpButton.ensureDebugId("c0c9e655-79bd-47ff-8705-61c7460556b3");
		helpButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				OnLineHelpTopics.showHelpURL(UIContext.externalLinks.getUDPRegistrationWindowHelpURL());
			}
		});
		this.setButtonAlign(HorizontalAlignment.LEFT);
		this.addButton(helpButton);
		this.getButtonBar().add(new FillToolItem());
		
		registerButton = new Button(UIContext.Constants.sendVerificationMail());
		registerButton.setTitle(UIContext.Constants.sendVerificationMail());
		registerButton.setEnabled(false);
		registerButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
					validateRegistrationDetails();
			}
		});
		this.getButtonBar().add(registerButton);
		
		cancelButton = new Button(UIContext.Constants.regClose());
		cancelButton.setTitle(UIContext.Constants.regClose());
		cancelButton.ensureDebugId("61b7ff2c-0655-4500-bdbf-8c06961c58d9");
		
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				EntitlementRegistrationWindow.this.hide();
			}
		});
		
		helpButton.addStyleName("ca-tertiaryText");
		cancelButton.addStyleName("ca-tertiaryText");
		
		this.getButtonBar().add(cancelButton);
		
		this.setFocusWidget(registerButton);
		
		this.setLayoutOnChange(true);
		this.show();
		loadRegistrationDetails();
	}
	
	public void registerEntitlementDetails()
	{
		String name = registrationContainer.nameFld.getValue() == null ? "" : registrationContainer.nameFld.getValue();
		String company = registrationContainer.companyFld.getValue() == null ? "" : registrationContainer.companyFld.getValue();
		String contactNumber = registrationContainer.contactNoFld.getValue() == null ? "" : registrationContainer.contactNoFld.getValue();
		String emailID = registrationContainer.emailIDFld.getValue() == null ? "" : registrationContainer.emailIDFld.getValue();
		String fulFillmentNum = registrationContainer.fulFillmentNumFld.getValue() == null ? "" : registrationContainer.fulFillmentNumFld.getValue();								
		HashMap<String,String> entitlementRegisterMap = new HashMap<String,String>();
		entitlementRegisterMap.put("name", name);
		entitlementRegisterMap.put("company", company);
		entitlementRegisterMap.put("contactNumber", contactNumber);
		entitlementRegisterMap.put("emailID", emailID);
		entitlementRegisterMap.put("netSuiteId", fulFillmentNum);													
		entitlRegisterService.registerEntitlementDetails(entitlementRegisterMap, new AsyncCallback<HashMap<String,String>>() {
			public void onFailure(Throwable caught) {	
				unmask();
				registerButton.setEnabled(true);
				registrationContainer.resultLabel.setValue("<b>" + widgStyle1 +"600px" + widgStyle2 + UIContext.Constants.getRegistrationFailedInvalidDetails() + widgStyle3);
			}
				
			@Override
			public void onSuccess(HashMap<String, String> result) {
				unmask();
				if(result != null && result.size() > 0)
				{
					String responseMsg = result.containsKey("responseMsg")? result.get("responseMsg") : UIContext.Constants.getRegistrationFailedInvalidDetails();
					if(result.containsKey("submitAERPJob"))
					{
						registrationContainer.nameFld.setEnabled(false);
						registrationContainer.companyFld.setEnabled(false);
						registrationContainer.contactNoFld.setEnabled(false);
						registrationContainer.emailIDFld.setEnabled(false);
						registrationContainer.fulFillmentNumFld.setEnabled(false);
						registrationContainer.policyTermWidget.setVisible(false);
						registrationContainer.checkBoxConatiner.setVisible(false);
						registrationContainer.euModelClause.setVisible(false);
						registrationContainer.privacyPolicy.setVisible(false);
						registrationContainer.msgLabel.setVisible(false);
						registrationContainer.cancelRegButton.setVisible(true);
						//if(usageStatisticsContainer.usageStatschkBx.getValue())
						//{
							entitlRegisterService.submitAERPJob(new AsyncCallback<String>() {
							@Override
							public void onFailure(Throwable caught) {
									
							}

							@Override
							public void onSuccess(String result) {
					
								}
							});
						//}
					}
					else{
						registerButton.setEnabled(true);
					}
					registrationContainer.resultLabel.setValue("<b>" + widgStyle1 +"600px" + widgStyle2 + responseMsg + widgStyle3);
					registrationContainer.cancelRegLabel.setValue(widgStyle1 +"600px" + widgStyle2 + UIContext.Constants.getCancelRegLabel() + widgStyle3);
					registrationContainer.forceLayout();
					NotificationEventHandler.getInstance().fireNotificationRefreshEvent();
				}
			}
		});		
	}
	
	public void validateRegistrationDetails()
	{
		String name = registrationContainer.nameFld.getValue() == null ? "" : registrationContainer.nameFld.getValue();
		String company = registrationContainer.companyFld.getValue() == null ? "" : registrationContainer.companyFld.getValue();
		String contactNumber = registrationContainer.contactNoFld.getValue() == null ? "" : registrationContainer.contactNoFld.getValue();
		String emailID = registrationContainer.emailIDFld.getValue() == null ? "" : registrationContainer.emailIDFld.getValue();
		String fulFillmentNum = registrationContainer.fulFillmentNumFld.getValue() == null ? "" : registrationContainer.fulFillmentNumFld.getValue();						
		HashMap<String,String> entitlementRegisterMap = new HashMap<String,String>();
		entitlementRegisterMap.put("name", name);
		entitlementRegisterMap.put("company", company);
		entitlementRegisterMap.put("contactNumber", contactNumber);
		entitlementRegisterMap.put("emailID", emailID);
		entitlementRegisterMap.put("netSuiteId", fulFillmentNum);		
		registerButton.setEnabled(false);
		mask(UIContext.Constants.regDtls());
		entitlRegisterService.validateRegistrationDetails(entitlementRegisterMap, new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				unmask();
				registerButton.setEnabled(true);
			}
				
			@Override
			public void onSuccess(String result) {
				if(!result.isEmpty())
				{
					unmask();
					registerButton.setEnabled(true);
					String message = "";
					if(result.equalsIgnoreCase("REGISTRATION_FAILED_INVALIDEMAIL"))
					{
						message = UIContext.Constants.invalidEmailID();
					}
					else if(result.equalsIgnoreCase("REGISTRATION_FAILED_INVALIDCONTACTNUMBER"))
					{
						message = UIContext.Constants.invalidContactNumber();
					}
					else if(result.equalsIgnoreCase("REGISTRATION_FAILED_INVALIDFULFILLMENT"))
					{
						message = UIContext.Constants.invalidFulFillment();
					}
					else if(result.equalsIgnoreCase("REGISTRATION_FAILED_INVALIDCOMPANYNAME"))
					{
						message = UIContext.Constants.invalidCompanyName();
					}
					else if(result.equalsIgnoreCase("REGISTRATION_FAILED_INVALIDUSERNAME"))
					{
						message = UIContext.Constants.invalidUserName();
					}
					MessageBox msgError = new MessageBox();
					msgError.setIcon(MessageBox.ERROR);
					msgError.setTitleHtml(UIContext.Constants.getRegistrationMsgHeader());
					msgError.setMessage(message);
					msgError.setModal(true);
					msgError.setMinWidth(400);
					msgError.show();
				}
				else
				{
					registerEntitlementDetails();
				}
			}
		});		
	}
	
	public void loadRegistrationDetails()
	{					
		mask(UIContext.Constants.loadingRegDtls());
		entitlRegisterService.getRegistrationDetails(new AsyncCallback<HashMap<String, String>>() {
		public void onFailure(Throwable caught) {	
			unmask();	 
			}

		@Override
		public void onSuccess(HashMap<String, String> result) {
			unmask();
				if(result != null && result.size() > 0)
				{
					registrationContainer.nameFld.setValue(result.get("name"));
					registrationContainer.companyFld.setValue(result.get("company"));
					registrationContainer.contactNoFld.setValue(result.get("contactNumber"));
					registrationContainer.emailIDFld.setValue(result.get("emailID"));
					registrationContainer.fulFillmentNumFld.setValue(result.get("netSuiteId"));
					
					if(result.get("emailID") != null && !result.get("emailID").isEmpty())
					{
						registerButton.setEnabled(true);
					}
					
					String responseMSG = "";
					boolean isRegCancelled=false;
					boolean isActiveUser = false;
					if(result.get("isActivated").equalsIgnoreCase("ISACTIVATED_ACTIVE"))
					{
						responseMSG = UIContext.Constants.regActiveMessage();
						isActiveUser = true;
					}
					else if(result.get("isActivated").equalsIgnoreCase("ISACTIVATED_INACTIVE"))
					{
						responseMSG = UIContext.Constants.regInActiveMessage();
					}
					else if(result.get("isActivated").equalsIgnoreCase("USER_CANCELLED_REGISTRATION"))
					{
						responseMSG = UIContext.Constants.getCancelRegSuccess();
						isRegCancelled = true;
					}
					if(responseMSG != "")
					{
						registrationContainer.resultLabel.setValue("<b>" + widgStyle1 +"600px" + widgStyle2 + responseMSG + widgStyle3);
						if(isActiveUser)
						{
							registrationContainer.privacyPolicy.setVisible(true);
							registrationContainer.euModelClause.setVisible(true);
							registrationContainer.msgLabel.setVisible(false);
						}
						if(isRegCancelled)
						{
							registrationContainer.registerCheck.setValue(false);
							registrationContainer.registerVContainer.setVisible(false);
						}
						else
						{
							registrationContainer.checkBoxConatiner.setVisible(false);
							registrationContainer.policyTermWidget.setVisible(false);
							registrationContainer.msgLabel.setVisible(false);
							registrationContainer.cancelRegButton.setVisible(true);
							registrationContainer.cancelRegLabel.setValue(widgStyle1 +"600px" + widgStyle2 + UIContext.Constants.getCancelRegLabel() + widgStyle3);
						}
						registrationContainer.forceLayout();
					}
				}
			}
		});		
	}
}
