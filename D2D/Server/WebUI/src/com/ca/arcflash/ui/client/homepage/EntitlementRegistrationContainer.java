package com.ca.arcflash.ui.client.homepage;

import java.util.HashMap;

import com.ca.arcflash.ui.client.FlashUIMessages;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.OnLineHelpTopics;
import com.ca.arcflash.ui.client.notifications.events.NotificationEventHandler;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutData;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.BlurEvent;
import com.sencha.gxt.widget.core.client.event.BlurEvent.BlurHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;

/**
 * @author RamaKishoreReddy.Upp
 *
 */
public class EntitlementRegistrationContainer extends VerticalLayoutContainer{

	/**
	 * @param args
	 */
	
	   public TextField nameFld;
	   public TextField companyFld;
	   public TextField contactNoFld;
	   public TextField emailIDFld;
	   public TextField fulFillmentNumFld;
	   public LabelField msgLabel;
	   public LabelField warningLabel;
	   public LabelField headerLabel;
	   public FieldLabel textFieldLabel;
	   public LabelField privacyPolicyLink;
	   public LabelField modelClauseLink;
	   public LabelField agreementLabel;
	   public CheckBox registerCheck;
	   public LabelField checkBoxLabel;
	   public LabelField resultLabel;
	   public LabelField cancelRegLabel;
	   public Button cancelRegButton;
	   public Anchor privacyPolicy;
	   public Anchor euModelClause;
	   public VerticalLayoutContainer registerVContainer;
	   public HBoxLayoutContainer checkBoxConatiner;
	   public VerticalLayoutContainer policyTermWidget;
	   private EntitlementRegistrationWindow entitlementRegistrationWindow = null;
	   
	   private FlashUIMessages commonMessages = GWT.create(FlashUIMessages.class);
	   private static final EntitlementRegisterServiceAsync entitlRegisterService = (EntitlementRegisterServiceAsync) GWT.create(EntitlementRegisterService.class);
	   
	   private static String widgStyle1 = "<div style=\"word-wrap: break-word; width:";
	   private static String widgStyle2 = ";\"" + ">";
	   private static String widgStyle3 = "</div>";
	   
	   public EntitlementRegistrationContainer(EntitlementRegistrationWindow entitlementRegistrationWindow)
	   {   
		   this.entitlementRegistrationWindow = entitlementRegistrationWindow;
		   
		   resultLabel =  new LabelField();
		   //resultLabel.setValue(widgStyle1 +"600px" + widgStyle2 + "" + widgStyle3);
		   this.add(resultLabel,new VerticalLayoutData(0, 0, new Margins(10, 0, 0, 15)));
		   
		   headerLabel = new LabelField(); 
		   headerLabel.setValue(widgStyle1 +"600px" + widgStyle2 + UIContext.Constants.getRegistrationHeader() + widgStyle3);
		   this.add(headerLabel,new VerticalLayoutData(0, 0, new Margins(10, 0, 0, 15)));
		   
		   cancelRegLabel =  new LabelField();
		   //cancelRegLabel.setValue(widgStyle1 +"600px" + widgStyle2 + "" + widgStyle3);
		   this.add(cancelRegLabel,new VerticalLayoutData(0, 0, new Margins(5, 0, 0, 15)));
		  
		   policyTermWidget = new VerticalLayoutContainer();
		   
		   privacyPolicyLink =  new LabelField();
		   privacyPolicyLink.setValue(widgStyle1 +"560px" + widgStyle2 + commonMessages.getRegisterPolicyLabel(UIContext.externalLinks.getUDPPrivacyPolicyURL()) + widgStyle3);
		   policyTermWidget.add(privacyPolicyLink,new VerticalLayoutData(0, 0, new Margins(5, 0, 0, 30)));
		 
		   modelClauseLink =  new LabelField();
		   modelClauseLink.setValue(widgStyle1 +"560px" + widgStyle2 + commonMessages.getEUModuleClauseLabel(UIContext.externalLinks.getUDPEUModelClauseURL()) + widgStyle3);
		   policyTermWidget.add(modelClauseLink,new VerticalLayoutData(0, 0, new Margins(5, 0, 0, 30)));
		 
		   agreementLabel =  new LabelField(); 
		   agreementLabel.setValue(widgStyle1 +"560px" + widgStyle2 +"&bull;&nbsp;" + UIContext.Constants.getRegAgreementLabel() + widgStyle3);
		   policyTermWidget.add(agreementLabel,new VerticalLayoutData(0, 0, new Margins(5, 0, 0, 30)));
		    
		    this.add(policyTermWidget, new VerticalLayoutData(0, 0, new Margins(5, 0, 0, 0)));
		   
			checkBoxConatiner = new HBoxLayoutContainer();
			registerCheck = new CheckBox();
			registerCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					if(registerVContainer!= null)
					{
						if(registerCheck.getValue())
						{
							msgLabel.setVisible(true);
							warningLabel.setVisible(true);
							registerVContainer.setVisible(true);
							registerVContainer.setEnabled(true);
						}
						else
						{
							msgLabel.setVisible(false);
							warningLabel.setVisible(false);
							registerVContainer.setVisible(false);
						}
						EntitlementRegistrationContainer.this.forceLayout();
					}
				}
			});
			registerCheck.setValue(true);
			
			checkBoxConatiner.add(registerCheck, new BoxLayoutData(new Margins(5, 0, 0, 0)));
			checkBoxLabel =  new LabelField();
			checkBoxLabel.setValue(widgStyle1 +"520px" + widgStyle2 + UIContext.Constants.getParticipateRegLabel() + widgStyle3);
			checkBoxConatiner.add(checkBoxLabel,new BoxLayoutData(new Margins(5, 0, 0, 4)));
			this.add(checkBoxConatiner, new VerticalLayoutData(0, 0, new Margins(20, 0, 0, 15)));
			
			registerVContainer =  new VerticalLayoutContainer();
			
			msgLabel =  new LabelField();
			msgLabel.setValue(widgStyle1 +"520px" + widgStyle2 + UIContext.Constants.getRegistrationInfo() + widgStyle3);
			registerVContainer.add(msgLabel,new VerticalLayoutData(0, 0, new Margins(5, 0, 0, 25)));
			
			warningLabel =  new LabelField();
			warningLabel.setValue(widgStyle1 +"520px" + widgStyle2 + UIContext.Constants.getRegistrationInfoRequired() + widgStyle3);
			registerVContainer.add(warningLabel,new VerticalLayoutData(0, 0, new Margins(10, 0, 10, 45)));
			
			nameFld = new TextField();
			nameFld.setId("Name");
			textFieldLabel = new FieldLabel(nameFld, UIContext.Constants.regName());
			textFieldLabel.setLabelWidth(120);
			textFieldLabel.setLabelSeparator("");
			registerVContainer.add(textFieldLabel ,new VerticalLayoutData(450, 0, new Margins(1, 0, 0, 80)));
			
			companyFld = new TextField();
			companyFld.setId("Company");
			textFieldLabel = new FieldLabel(companyFld, UIContext.Constants.regCompany());
			textFieldLabel.setLabelWidth(120);
			textFieldLabel.setLabelSeparator("");
			registerVContainer.add(textFieldLabel,new VerticalLayoutData(450, 0, new Margins(1, 0, 0, 80)));
			
			contactNoFld = new TextField();		
			contactNoFld.setId("ContactNumber");
			textFieldLabel = new FieldLabel(contactNoFld, UIContext.Constants.regContactNumber());
			textFieldLabel.setLabelWidth(120);
			textFieldLabel.setLabelSeparator("");
			registerVContainer.add(textFieldLabel , new VerticalLayoutData(450, 0, new Margins(1, 0, 0, 80)));
			
			emailIDFld = new TextField();		
			emailIDFld.setId("EmailID");
			emailIDFld.setAllowBlank(false);
			emailIDFld.addKeyUpHandler( new KeyUpHandler() {

				@Override
				public void onKeyUp(KeyUpEvent event) {
					validateInput();
				}
				
			});
			emailIDFld.addBlurHandler(new BlurHandler() {

				@Override
				public void onBlur(BlurEvent event) {
					validateInput();
				}
				
			});
			textFieldLabel = new FieldLabel(emailIDFld, "* " + UIContext.Constants.regEmailID());
			textFieldLabel.setLabelWidth(130);
			textFieldLabel.setLabelSeparator("");
			registerVContainer.add(textFieldLabel ,new VerticalLayoutData(450, 0, new Margins(1, 0, 0, 70)));
			
			fulFillmentNumFld = new TextField();
			fulFillmentNumFld.setId("FulFillmentNumber");
			fulFillmentNumFld.setWidth(245);
			textFieldLabel = new FieldLabel(fulFillmentNumFld, UIContext.Constants.regFulfillmentNumber());
			textFieldLabel.setLabelWidth(120);
			textFieldLabel.setLabelSeparator("");
			
			HBoxLayoutContainer hBox = new HBoxLayoutContainer();
			Image imgWidgetComponent = new Image(UIContext.IconBundle.homepage_help_icon());
			imgWidgetComponent.setTitle(UIContext.Constants.getFulFillmentToolTip());
			hBox.add(imgWidgetComponent, new BoxLayoutData(new Margins(2, 0, 0, 0)));
			hBox.add(textFieldLabel, new BoxLayoutData(new Margins(0, 0, 0, 5)));

			registerVContainer.add(hBox ,new VerticalLayoutData(0, 0, new Margins(1, 0, 0, 60)));
			
			this.add(registerVContainer,new VerticalLayoutData(0, 0, new Margins(2, 0, 0, 10)));
			
			 privacyPolicy = new Anchor();
			 privacyPolicy.setText(UIContext.Constants.getPrivacyPoilcyLinkLabel());
			 privacyPolicy.getElement().getStyle().setColor("Blue");
			 privacyPolicy.getElement().getStyle().setTextDecoration(TextDecoration.NONE);
			 privacyPolicy.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			 privacyPolicy.getElement().getStyle().setCursor(Style.Cursor.POINTER);
			 privacyPolicy.addClickHandler(new ClickHandler() {

				    @Override
				    public void onClick(ClickEvent cEv) {
				    	OnLineHelpTopics.showHelpURL(UIContext.externalLinks.getUDPPrivacyPolicyURL());
				    }
				});
			 privacyPolicy.setVisible(false);
			 
			 euModelClause = new Anchor();
			 euModelClause.setText(UIContext.Constants.getEUModelClauseLinkLabel());
			 euModelClause.getElement().getStyle().setColor("Blue");
			 euModelClause.getElement().getStyle().setTextDecoration(TextDecoration.NONE);
			 euModelClause.getElement().getStyle().setCursor(Style.Cursor.POINTER);
			 euModelClause.getElement().getStyle().setFontWeight(FontWeight.BOLD);
			 euModelClause.addClickHandler(new ClickHandler() {

				    @Override
				    public void onClick(ClickEvent cEv) {
				    	OnLineHelpTopics.showHelpURL(UIContext.externalLinks.getUDPEUModelClauseURL());
				    }
				});
			 euModelClause.setVisible(false);

			cancelRegButton = new Button(UIContext.Constants.cancelRegistration());
			cancelRegButton.setStyleAttribute("margin-top", "30px");
			cancelRegButton.setTitle(UIContext.Constants.cancelRegistration());
			cancelRegButton.ensureDebugId("dc1ec7d1-52b5-411d-8e51-4cf799011fba");
			cancelRegButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
				@Override
				public void componentSelected(ButtonEvent ce) {
					cancelRegistration();
				}
			});
			cancelRegButton.setVisible(false);
			
			VerticalPanel vp = new VerticalPanel();
			vp.setStyleAttribute("margin-top", "70px");
			vp.setStyleAttribute("margin-left", "15px");
			vp.add(privacyPolicy);
			vp.add(euModelClause);
			vp.add(cancelRegButton);
			this.add(vp);
			
			this.forceLayout();
	   }
	   
	   private void validateInput()
	   {
		   if(emailIDFld.getCurrentValue() != null && !"".equals(emailIDFld.getCurrentValue()))
			{
				EntitlementRegistrationWindow.registerButton.setEnabled(true);	
			}
			else{
				EntitlementRegistrationWindow.registerButton.setEnabled(false);	
			}
	   }
	   
	   public void cancelRegistration()
		{
			String name = nameFld.getValue() == null ? "" : nameFld.getValue();
			String company = companyFld.getValue() == null ? "" : companyFld.getValue();
			String contactNumber = contactNoFld.getValue() == null ? "" : contactNoFld.getValue();
			String emailID = emailIDFld.getValue() == null ? "" : emailIDFld.getValue();
			String fulFillmentNum = fulFillmentNumFld.getValue() == null ? "" : fulFillmentNumFld.getValue();								
			HashMap<String,String> entitlementRegisterMap = new HashMap<String,String>();
			entitlementRegisterMap.put("name", name);
			entitlementRegisterMap.put("company", company);
			entitlementRegisterMap.put("contactNumber", contactNumber);
			entitlementRegisterMap.put("emailID", emailID);
			entitlementRegisterMap.put("netSuiteId", fulFillmentNum);	
			entitlementRegistrationWindow.mask(UIContext.Constants.cancelRegDtls());
			entitlRegisterService.cancelRegistration(entitlementRegisterMap, new AsyncCallback<String>() {
				public void onFailure(Throwable caught) {
					entitlementRegistrationWindow.unmask();
				}
					
				@Override
				public void onSuccess(String result) {
					entitlementRegistrationWindow.unmask();
					if(!result.isEmpty())
					{
						if(result.equalsIgnoreCase("CANCELREGISTRATION_SUCCESS"))
						{
							resultLabel.setValue("<b>" + widgStyle1 +"600px" + widgStyle2 + UIContext.Constants.getCancelRegSuccess() + widgStyle3);
							registerVContainer.setVisible(false);
							policyTermWidget.setVisible(true);
							checkBoxConatiner.setVisible(true);
							registerCheck.setValue(false);
							nameFld.setValue("");
							companyFld.setValue("");
							contactNoFld.setValue("");
							emailIDFld.setValue("");
							fulFillmentNumFld.setValue("");
							euModelClause.setVisible(false);
							privacyPolicy.setVisible(false);
							cancelRegButton.setVisible(false);
							cancelRegLabel.setVisible(false);
							EntitlementRegistrationWindow.registerButton.setEnabled(false);
							EntitlementRegistrationContainer.this.forceLayout();
							NotificationEventHandler.getInstance().fireNotificationRefreshEvent();
						}
						else
						{
							MessageBox msgError = new MessageBox();
							msgError.setIcon(MessageBox.ERROR);
							msgError.setTitleHtml(UIContext.Constants.getRegistrationMsgHeader());
							msgError.setMessage(UIContext.Constants.getCancelRegFailure());
							msgError.setModal(true);
							msgError.setMinWidth(400);
							msgError.show();
						}
					}
				}
			});		
		}
}
