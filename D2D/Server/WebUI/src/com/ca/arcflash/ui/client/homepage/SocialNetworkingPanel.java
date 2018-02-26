package com.ca.arcflash.ui.client.homepage;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.AppType;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

public class SocialNetworkingPanel extends ContentPanel {
	private String twitterLink ="";
	private String facebookLink = "";
	
	public SocialNetworkingPanel(AppType appType){

        super();
		
		this.setCollapsible(true);
		this.setAutoHeight(true);
		this.setBodyStyle("padding: 6px;");
		this.setHeadingHtml(UIContext.Constants.homepageSocialHeader());
		//this.setLayout(new FitLayout());

		if(appType == AppType.D2D){
			twitterLink = UIContext.externalLinks.getTwitterCom();
			facebookLink = UIContext.externalLinks.getFaceBookCom();
		}
		else if(appType == AppType.VCM){
			twitterLink = UIContext.externalLinks.getVirtualStandbyTwitterCom();
			facebookLink = UIContext.externalLinks.getVirtualStandbyFaceBookCom();
		}
		else if(appType == AppType.VSPHERE){
			twitterLink = UIContext.externalLinks.getVSphereTwitterCom();;
			facebookLink = UIContext.externalLinks.getVSphereFaceBookCom();
		}
	}
	public void render(Element target, int index) {
		super.render(target, index);
		
	    FlowPanel flowPanel = new FlowPanel();
	    flowPanel.setHeight("30px");
	    flowPanel.setWidth("100%");
	    
	    Image twitterImage = new Image("images/twitter_logo_204080.gif");
	    twitterImage.ensureDebugId("c1027f44-25b2-4877-8051-a851a71826dd");
	    twitterImage.setTitle("twitter");
	    twitterImage.setStyleName("homepage_task_icon");
	    twitterImage.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				Window.open(twitterLink, "_blank", "");
			}
	    	
	    });
	    
	    Image faceBookImage = new Image("images/facebook_logo_203943.gif");
	    faceBookImage.ensureDebugId("c84acb08-6d06-4008-8bd4-b3918bcdae0f");
	    faceBookImage.getElement().getStyle().setPaddingLeft(4, Unit.PX);
	    faceBookImage.setTitle("Facebook");
	    faceBookImage.setStyleName("homepage_task_icon");
	    faceBookImage.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				Window.open(facebookLink, "_blank", "");
			}
	    	
	    });
	    
	    if(UIContext.customizedModel.getShowTwitter() == null || UIContext.customizedModel.getShowTwitter())
	    	flowPanel.add(twitterImage);
	    if(UIContext.customizedModel.getShowFacebook() == null || UIContext.customizedModel.getShowFacebook())
	    	flowPanel.add(faceBookImage);
	    
	    this.add(flowPanel);
	}
	
	
}