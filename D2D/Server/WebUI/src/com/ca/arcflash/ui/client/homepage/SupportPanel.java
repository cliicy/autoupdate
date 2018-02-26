package com.ca.arcflash.ui.client.homepage;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.AppType;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.homepage.feedback.FeedbackWindow;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SupportPanel extends ContentPanel {

	private final CommonServiceAsync service = GWT.create(CommonService.class);
	private VerticalPanel verticalPanel;
	private FlexTable liveChatTable;
	private final LayoutContainer liveChatContainer = new LayoutContainer();
	
	private String KNOWLEDGE_CENTER_URL;// = UIContext.externalLinks.getKnowledgeCenterURL();
	private String VIDEO_URL;// = UIContext.externalLinks.getVideoURL();
	private String CA_SUPPORT_URL;// = UIContext.externalLinks.getCASupportURL();
	private String GOOGLE_GROUP_URL;// = UIContext.externalLinks.getGoogleGroupURL();
	private String FEEDBACK_URL;// = UIContext.externalLinks.getFeedBackURL();
	private String D2D_USER_CENTER;// = UIContext.externalLinks.getD2DUserCenter();
	private String VideoCASupportURL;
	private String CHAT_URL;
	private String EMAIL_SUPPORT_URL; // NCE support URL
	
	private AppType appType;

	public SupportPanel(AppType appType){

	    super();
		
	    this.appType = appType;
		this.setCollapsible(true);
	    this.setAutoHeight(true);
	    this.setBodyStyle("padding: 4px;");
	    this.setHeadingHtml(UIContext.Constants.homepageSupportHeader());
	    
		if(appType == AppType.D2D){
			KNOWLEDGE_CENTER_URL = UIContext.externalLinks.getKnowledgeCenterURL();
			VIDEO_URL = UIContext.externalLinks.getVideoURL();
			CA_SUPPORT_URL = UIContext.externalLinks.getCASupportURL();
			GOOGLE_GROUP_URL = UIContext.externalLinks.getGoogleGroupURL();
			FEEDBACK_URL = UIContext.externalLinks.getFeedBackURL();
			D2D_USER_CENTER = UIContext.externalLinks.getD2DUserCenter();
			VideoCASupportURL = UIContext.externalLinks.getVideoCASupportURL();
			CHAT_URL = UIContext.externalLinks.getLiveChatURL();
			EMAIL_SUPPORT_URL = UIContext.externalLinks.getEmailSupportURL();
		}
		else if(appType == AppType.VCM){
			KNOWLEDGE_CENTER_URL = UIContext.externalLinks.getKnowledgeCenterURL();
			VIDEO_URL = UIContext.externalLinks.getVirtualStandbyVideoURL();
			CA_SUPPORT_URL = UIContext.externalLinks.getVirtualStandbyCASupportURL();
			GOOGLE_GROUP_URL = UIContext.externalLinks.getVirtualStandbyGoogleGroupURL();
			FEEDBACK_URL = UIContext.externalLinks.getVirtualStandbyFeedBackURL();
			D2D_USER_CENTER = UIContext.externalLinks.getVirtualStandbyUserCenter();
			VideoCASupportURL = UIContext.externalLinks.getVirtualStandbyCASupportURL();
			CHAT_URL = UIContext.externalLinks.getCentralLiveChatURL();
		}
		else if(appType == AppType.VSPHERE){
			KNOWLEDGE_CENTER_URL = UIContext.externalLinks.getKnowledgeCenterURL();
			VIDEO_URL = UIContext.externalLinks.getVSphereVideoURL();
			CA_SUPPORT_URL = UIContext.externalLinks.getVSphereCASupportURL();
			GOOGLE_GROUP_URL = UIContext.externalLinks.getVSphereGoogleGroupURL();
			FEEDBACK_URL = UIContext.externalLinks.getVSphereFeedBackURL();
			D2D_USER_CENTER = UIContext.externalLinks.getVSphereD2DUserCenter();
			VideoCASupportURL = UIContext.externalLinks.getVSphereVideoCASupportURL();
			CHAT_URL = UIContext.externalLinks.getCentralLiveChatURL();
		}
	}
		
	@Override
	protected void onRender(Element parent, int pos)
	{
		super.onRender(parent, pos);
	    
	    verticalPanel = new VerticalPanel();
	    verticalPanel.setWidth("100%");
	    this.add(verticalPanel);
	    
	 
	    ClickHandler videoHandler = new ClickHandler()
	    {
			@Override
			public void onClick(ClickEvent event) {
				
				service.isYouTubeVideoSource(new BaseAsyncCallback<Boolean>(){
					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);						
						//Show the selection dialog
						Window.open(VIDEO_URL, "_BLANK", "");
					}

					@Override
					public void onSuccess(Boolean result) {
						
						//Show the YouTube or the CA Support link
						if (result)
						{
							Window.open(VIDEO_URL, "_BLANK", "");
						}
						else
						{
							Window.open(VideoCASupportURL, "_BLANK", "");
						}
					}
				});
				
				
			}	    	
	    };
	    
	    
	    
	    
	    String locale = UIContext.serverVersionInfo.getLocale();
	    if(UIContext.customizedModel.getShowKnowledgeCenter() == null || UIContext.customizedModel.getShowKnowledgeCenter()){
	    if(appType == AppType.D2D || appType == AppType.VCM || appType == AppType.VSPHERE){
	    	addItem(UIContext.externalLinks.getHomepageSupportKnowledgeCenter(), UIContext.externalLinks.getHomepageSupportKnowledgeCenterDescription(),
		    		KNOWLEDGE_CENTER_URL, AbstractImagePrototype.create(UIContext.IconBundle.support()), null, "2d38d2a7-00f0-4dbd-b681-95fefcbc72ff");
	    }
	    }
	    	
	    
	    if(UIContext.customizedModel.getShowVedios() == null || UIContext.customizedModel.getShowVedios()) {
	    	if(locale!=null && !locale.trim().equalsIgnoreCase("en")) {
		    	addItem(UIContext.externalLinks.getHomepageSupportVideoLabelOnlyEn(), UIContext.externalLinks.getHomepageSupportVideoDescription(),
		    			VIDEO_URL,  AbstractImagePrototype.create(UIContext.IconBundle.video()), videoHandler, "0ec161eb-27cb-41a1-bfb1-d1894d8635b0");
		    } else {
		    	addItem(UIContext.externalLinks.getHomepageSupportVideoLabel(), UIContext.externalLinks.getHomepageSupportVideoDescription(),
		    			VIDEO_URL,  AbstractImagePrototype.create(UIContext.IconBundle.video()), videoHandler, "0ec161eb-27cb-41a1-bfb1-d1894d8635b0");
		    }
	    }
	    
	    if(UIContext.customizedModel.getShowSupport() == null || UIContext.customizedModel.getShowSupport())
	    	addItem(UIContext.externalLinks.getHomepageSupportCASupport(), UIContext.externalLinks.getHomepageSupportOnlineDescription(),
	    			CA_SUPPORT_URL, AbstractImagePrototype.create(UIContext.IconBundle.support()), null, "e7149169-4fe5-446b-a244-5cd13df852de");

	    final String feedBackHtml = getFeedBackHtml();
	    	
		ClickHandler feedbackHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {

				FeedbackWindow feedbackWindow = new FeedbackWindow(feedBackHtml);
				// next time refresh it before showing
				if (feedbackWindow != null && feedbackWindow.isRendered()
						&& !feedbackWindow.isVisible()) {
					feedbackWindow.refresh();
				}

				feedbackWindow.feedMask();
				feedbackWindow.show();
			}

		};
		String sendFeedbackLabel = UIContext.externalLinks.getHomepageSupportSendFeedbackLabel();
		if(locale != null && locale.toLowerCase().trim().startsWith("ja")) {
			sendFeedbackLabel = UIContext.externalLinks.getHomepageSupportSendFeedbackLabelOnlyEn();
		}
		if(UIContext.customizedModel.getShowFeedback() == null || UIContext.customizedModel.getShowFeedback())
	    	addItem(sendFeedbackLabel, UIContext.externalLinks.getHomepageSupportSendFeedbackDescription(),
	    			FEEDBACK_URL, AbstractImagePrototype.create(UIContext.IconBundle.googleGroup()), null, "6c2d36cf-e76a-4168-a26c-e939e88b36f4");
		    
		if(UIContext.customizedModel.getShowCommunity() == null || UIContext.customizedModel.getShowCommunity())
		    	addItem(UIContext.externalLinks.getHomepageSupportGoogleGroupLabel(), UIContext.externalLinks.getHomepageSupportGoogleGroupLabelDescription(), 
		    			GOOGLE_GROUP_URL, AbstractImagePrototype.create(UIContext.IconBundle.googleGroup()), null, "dc95b1ae-4ce4-484c-9f94-2cc80a5f19eb");
	    
	    if(UIContext.customizedModel.getShowAdvice() == null || UIContext.customizedModel.getShowAdvice())
	    	addItem(UIContext.externalLinks.getHomepageSupportD2DUserCenterLabel(), UIContext.externalLinks.getHomepageSupportD2DUserCenterLabelDescription(),
	    			D2D_USER_CENTER, AbstractImagePrototype.create(UIContext.IconBundle.userCenter()), null, "27918838-d2e2-4efa-8088-a89280d8a4b8");
	    
	    // if NCE, show email support. if charge editon, show live chat.
	    if(UIContext.customizedModel.getShowLiveChat() == null || UIContext.customizedModel.getShowLiveChat())
	    addLiveChatItem();
	   
//	    if(UIContext.serverVersionInfo.isNCE()){
//	    	if(UIContext.customizedModel.getShowNCE() == null || UIContext.customizedModel.getShowNCE()){
//	    		addItem(UIContext.externalLinks.getHomepageEmailSupportLabel(), UIContext.externalLinks.getHomepageEmailSupportDescription(),
//		    			EMAIL_SUPPORT_URL , AbstractImagePrototype.create(UIContext.IconBundle.faq()), null, "ea4c63e3-f734-478e-b898-63b986eac6a5");
//	    	}
//	    }else{
//	    	ClickHandler chathandler = new ClickHandler() {
//				@Override
//				public void onClick(ClickEvent event) {
//					openChatURL();
//				}
//		    };
//		    if(UIContext.customizedModel.getShowLiveChat() == null || UIContext.customizedModel.getShowLiveChat())
//		    	addItem(UIContext.externalLinks.getHomepageLiveChatLabel(), UIContext.externalLinks.getHomepageLiveChatDescription(), 
//		    		CHAT_URL, AbstractImagePrototype.create(UIContext.IconBundle.liveChat()), chathandler, "7D7E971B-E82E-4196-B867-ED61558AB44A");	    
//	    }
	}
	
	private void addLiveChatItem(){
		liveChatContainer.sinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT | Event.ONMOUSEMOVE | Event.ONCLICK);
		liveChatContainer.addListener(Events.OnMouseOver, new Listener<ComponentEvent>()
		{

			@Override
			public void handleEvent(ComponentEvent be)
			{
				liveChatContainer.addStyleName("navigation-button-over");
			}

		});
		liveChatContainer.addListener(Events.OnMouseOut, new Listener<ComponentEvent>()
		{

			@Override
			public void handleEvent(ComponentEvent be)
			{
				liveChatContainer.removeStyleName("navigation-button-over");
			}

		});
		liveChatContainer.setStyleAttribute("padding", "2px");
		
		liveChatTable = new FlexTable();
		liveChatTable.setCellPadding(0);
		liveChatTable.setCellSpacing(0);
		liveChatTable.setWidth("100%");
		paintLiveChat();
		liveChatContainer.add(liveChatTable);
		verticalPanel.add(liveChatContainer);
	}
	
	private void paintLiveChat() {
		String label = null;
		String description = null;
		ClickHandler chathandler = null;
	    AbstractImagePrototype image = null;
		if (UIContext.serverVersionInfo.isNCE()) {
			label = UIContext.externalLinks.getHomepageEmailSupportLabel(); 
			description = UIContext.externalLinks.getHomepageEmailSupportDescription();
			chathandler = new ClickHandler(){
				@Override
				public void onClick(ClickEvent event) {
					Window.open(EMAIL_SUPPORT_URL, "_BLANK", "");
				}
			};
			image =  AbstractImagePrototype.create(UIContext.IconBundle.faq());
		}else{
			label = UIContext.externalLinks.getHomepageLiveChatLabel();
			description = UIContext.externalLinks.getHomepageLiveChatDescription();
			chathandler = new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					openChatURL();
				}
		    };
		    image = AbstractImagePrototype.create(UIContext.IconBundle.liveChat());
		}
		
		liveChatTable.removeAllRows();
		Image icon = image.createImage();
		icon.setTitle(description);
		icon.setStyleName("homepage_task_icon");		
		icon.addClickHandler(chathandler);
		liveChatTable.setWidget(0, 0, icon);
		liveChatTable.getFlexCellFormatter().setWidth(0, 0, "36px");
		liveChatTable.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_MIDDLE);
		Label text = new Label(label);
		text.ensureDebugId("7D7E971B-E82E-4196-B867-ED61558AB44A");
		text.setTitle(description);
		text.setStyleName("homepage_task_label");
		text.addClickHandler(chathandler);
		liveChatTable.setWidget(0, 1, text);
	}
	
	/**
	 * if No Charge Edition, show email support. if Charge Edition, show live chat.
	 */
	public void refreshLiveChat() {
		paintLiveChat();
		if (UIContext.serverVersionInfo.isNCE()) {
			if (UIContext.customizedModel.getShowNCE() == null || UIContext.customizedModel.getShowNCE()) {
				liveChatContainer.setVisible(true);
			} else {
				liveChatContainer.setVisible(false);
			}
		} else {
			if (UIContext.customizedModel.getShowLiveChat() == null || UIContext.customizedModel.getShowLiveChat()) {
				liveChatContainer.setVisible(true);
			} else {
				liveChatContainer.setVisible(false);
			}
		}
	}

	private void addItem(String label, String description, final String url, AbstractImagePrototype image, ClickHandler handler, String debugID){

		if (handler == null){
			handler = new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				Window.open(url, "_BLANK", "");
			}};
	    }
		
		final LayoutContainer container = new LayoutContainer();
		
		container.sinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT | Event.ONMOUSEMOVE | Event.ONCLICK);
		container.addListener(Events.OnMouseOver, new Listener<ComponentEvent>()
		{

			@Override
			public void handleEvent(ComponentEvent be)
			{
				container.addStyleName("navigation-button-over");
			}

		});
		container.addListener(Events.OnMouseOut, new Listener<ComponentEvent>()
		{

			@Override
			public void handleEvent(ComponentEvent be)
			{
				container.removeStyleName("navigation-button-over");
			}

		});
		
		container.setStyleAttribute("padding", "2px");
		
		FlexTable flexTable = new FlexTable();
		flexTable.setCellPadding(0);
		flexTable.setCellSpacing(0);
		flexTable.setWidth("100%");
		
		if (image != null){
			Image icon = image.createImage();
			icon.setTitle(description);
			icon.setStyleName("homepage_task_icon");		
			icon.addClickHandler(handler);
			flexTable.setWidget(0, 0, icon);
			flexTable.getFlexCellFormatter().setWidth(0, 0, "36px");
			flexTable.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_MIDDLE);
		}else{
			flexTable.setWidget(0, 0, new HTML(""));
			flexTable.getFlexCellFormatter().setWidth(0, 0, "36px");
			flexTable.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_MIDDLE);
		}
		
		Label text = new Label(label);
		text.ensureDebugId(debugID);
		text.setTitle(description);
		text.setStyleName("homepage_task_label");
		text.addClickHandler(handler);
		flexTable.setWidget(0, 1, text);
		
		container.add(flexTable);
		verticalPanel.add(container);
	}	
	
	private String getFeedBackHtml()
	{
	  	String feedBackHtml = "";
    	if(this.appType == AppType.D2D){
    		feedBackHtml = "getsatisfaction_feedback.htm";
    	}
    	else{
    		feedBackHtml = "app_getsatisfaction_feedback.htm";
    	}
    	
    	return feedBackHtml;	    	
	}
	
	private void openChatURL() {
		Window.open(this.CHAT_URL+UriUtils.encode(Window.Location.getHref()), "_blank", "resizable=yes,scrollbars=yes,location=yes,width=400,height=400");
	}
}


