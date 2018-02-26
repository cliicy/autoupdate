package com.ca.arcflash.ui.client.homepage;


import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.LicenseAgreementWindow;
import com.ca.arcflash.ui.client.common.OnLineHelpTopics;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.VersionInfoModel;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.FlowData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

public class AboutWindow extends Window {
	
	private final LoginServiceAsync service = GWT.create(LoginService.class);
	
	public AboutWindow(){
		this.addStyleName("edgeAboutWindow");
		addButtons();
	}
	
	private void addButtons() {
		Button okButton = new Button(UIContext.Constants.ok());
		okButton.ensureDebugId("7B84D724-9F54-48ea-B7C7-6960EB55D94C");
		
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				AboutWindow.this.hide();
			}
			
		});
		
		this.setFocusWidget(okButton);
		this.addButton(okButton);
	}
	
	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		
		this.setWidth(500);
		this.setResizable(false);
		this.setModal(true);
		this.setHeadingHtml(UIContext.Messages.aboutWindowTitle(UIContext.productNameD2D));
		
		//this.add(new Image(UIContext.IconBundle.aboutProductLogo()));
		Html image = new Html("<span class=\"about_logo_name\">&nbsp</span>");
		image.setStyleName("about_productName_container");
		
		this.add(image);
		this.add(createContent(), new FlowData(0, 25, 0, 25));
	}
	
	private Widget createContent() {
		LayoutContainer container = new LayoutContainer();
		
		// product name
		container.add(createText(UIContext.productNameD2D, "7BF01D95-DD6D-439a-BF50-7DA4630BD516"));
		
		// version
		final LabelField versionLabel = createText(UIContext.Messages.aboutWindowReleaseNumber("","",""), "0AC90F52-115B-4eb0-B8AD-8B38B56F3C0E");
		container.add(versionLabel);
		
		// update number
		final LabelField updateNumberLabel = createText("", "D31F6FD3-4D26-4b99-8C38-E6248CF9C143");
		updateNumberLabel.setVisible(false);
		container.add(updateNumberLabel);
		
		// copyright
		container.add(createText(UIContext.Constants.aboutWindowCopyRightFull(), "BFD3BB06-1741-4f9a-A2A0-AAE834591DE8"));
		// license agreement
		container.add(createLicenseAgreementWidget());		
		// warnings
		LabelField warningLabel = createText(UIContext.Constants.aboutWindowWarning(), "34539981-B4F1-460a-9A99-E38DAB1DA4C4");
		warningLabel.addStyleName("warnings");
		container.add(warningLabel);
	    // links
		container.add(createLinkWidget());
		
		service.getVersionInfo(new BaseAsyncCallback<VersionInfoModel>() {
			
			@Override
			public void onSuccess(VersionInfoModel result) {
				versionLabel.setValue(UIContext.Messages.aboutWindowReleaseNumber(result.getMajorVersion(), result.getMinorVersion(), result.getBuildNumber()));
				
				if(result.getUpdateNumber()!=null&&!result.getUpdateNumber().isEmpty()&&result.getUpdateBuildNumber()!=null&&!result.getUpdateBuildNumber().isEmpty()){
					updateNumberLabel.setValue(UIContext.Messages.aboutWindowUpdateBuildNumber(result.getUpdateNumber(),result.getUpdateBuildNumber()));	
					updateNumberLabel.setVisible(true);
				}
			}
			
		});
		
		return container;
	}
	
	private LabelField createText(String text, String debugId) {
		LabelField label =  new LabelField(text);
		label.ensureDebugId(debugId);
		label.addStyleName("aboutText");
		return label;
	}
	
	private Widget createLicenseAgreementWidget() {
		LabelField license = createText(UIContext.Constants.licenseAgreement(), "2BAC8ADA-DC51-44fe-B682-36940D35C751");
		license.addStyleName("licenseAgreement");
		
		license.addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				LicenseAgreementWindow window = new LicenseAgreementWindow();
				window.ensureDebugId("FBA36AA7-0603-43d6-8DA8-E96C0A0AF4DB");
				window.show();
			}
			
		});
		
		
		HorizontalPanel panel = new HorizontalPanel();
		panel.add(license);
		return panel;
	}
	
	private Widget createLinkWidget() {
		HorizontalPanel panel = new HorizontalPanel();
		panel.ensureDebugId("b8619e26-13c1-4a38-8ac8-f1983ced7cf7");
		panel.addStyleName("links");
		
		Button onlineSupportButton = new Button(UIContext.Constants.brandingPanelCaSupportTxt());
		onlineSupportButton.ensureDebugId("2c724db2-95fe-45e6-8fd7-590cea059266");
		onlineSupportButton.addStyleName("ca-tertiaryText");
		onlineSupportButton.setBorders(true);
		onlineSupportButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				OnLineHelpTopics.showHelpURL(UIContext.externalLinks.getCASupportURL());
			}
			
		});
		panel.add(onlineSupportButton);
		
		Button releaseNotesButton = new Button(UIContext.Constants.releaseNotes());
		releaseNotesButton.ensureDebugId("ee6b65f0-5dd1-4df4-abb7-17ebb06cdbfe");
		releaseNotesButton.addStyleName("ca-tertiaryText");
		releaseNotesButton.setBorders(true);
		releaseNotesButton.setStyleAttribute("margin-left", "10px");
		releaseNotesButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				OnLineHelpTopics.showHelpURL(UIContext.externalLinks.getReleaseNotesURL());
			}
			
		});
		panel.add(releaseNotesButton);
		
		return panel;
	}
	
	@Override
	protected void onKeyPress(WindowEvent we) {
		super.onKeyPress(we);
		
		if (KeyCodes.KEY_ENTER == we.getKeyCode()) {
			this.hide();
		}
	}
	
}