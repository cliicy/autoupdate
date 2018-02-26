package com.ca.arcflash.ui.client.homepage;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.HelpTopics;
import com.ca.arcflash.ui.client.model.TrustHostModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class FeedsWindow extends Window 
{
	private final HomepageServiceAsync service = GWT.create(HomepageService.class);
	private FeedsWindow window;
	private LayoutContainer panel;
	public final int MIN_WIDTH = 90;
	
	public FeedsWindow()
	{
		this.setResizable(false);
		this.window = this;
		this.setHeadingHtml(UIContext.Constants.allFeeds());
		this.setClosable(true);		
		this.setSize(425, 335);
				
		LayoutContainer container = new LayoutContainer();
		container.setStyleAttribute("margin", "8px");
		TableLayout tl = new TableLayout();		
		tl.setColumns(3);
		container.setLayout(tl);
		
		TableData td = new TableData();
		td.setWidth("10%");		
		container.add(AbstractImagePrototype.create(UIContext.IconBundle.rssIconBig()).createImage(), td);
		
		td = new TableData();
		td.setColspan(2);
		Label label = new Label(UIContext.Constants.newsFeeds());
		label.addStyleName("rssDialogTitle");						
		container.add(label, td);
		
		td = new TableData();
		td.setColspan(3);
		label = new Label(UIContext.Messages.allFeedsSubscribe(UIContext.productNameD2D));
		label.addStyleName("rssDialogDescription");
		container.add(label, td);
		
		ClickHandler handler = new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				
				service.getTrustHosts(new BaseAsyncCallback<TrustHostModel[]>() {
					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);						
					}

					@Override
					public void onSuccess(TrustHostModel[] result) {
						TrustHostModel selectedHost = null;
						for (TrustHostModel model : result)
						{
							if (model.isSelected())
							{
								selectedHost = model;
							}
						}
						if (selectedHost != null)
						{
							StringBuilder url = new StringBuilder();
							url.append(selectedHost.getProtocol());
							url.append("//");
							url.append(selectedHost.getHostName());
							url.append(":");
							url.append(selectedHost.getPort());
							url.append("/jobfeed.xml");
							
							com.google.gwt.user.client.Window.open(url.toString(), "_BLANK", "");
						}
						else
						{
							com.google.gwt.user.client.Window.open("http://localhost:8014/jobfeed.xml", "_BLANK", "");
						}
					}
				});
				
				
			}
	    };
		
	    addItem(container, UIContext.Constants.unsuccessfulFeed(), null, handler, "812D53CD-89FC-40f8-97F0-B6A9CBE0180F");
		String locale = UIContext.serverVersionInfo.getLocale();
	    if(locale!=null && !locale.trim().equalsIgnoreCase("en")) {
	    	addItem(container, UIContext.externalLinks.getHomepageSupportVideoLabelOnlyEn(), UIContext.externalLinks.getVideoRSSURL(), null, "ec7ab0e5-d242-448c-be92-ea1c7ccfdc7a");
	    } else {
	    	addItem(container, UIContext.externalLinks.getHomepageSupportVideoLabel(), UIContext.externalLinks.getVideoRSSURL(), null,"a6999a4d-e521-4c25-a5c0-260ecf22b6c6");
	    }
		addItem(container, UIContext.externalLinks.getHomepageSupportSendFeedbackLabel(), UIContext.externalLinks.getFeedBackRSSURL(), null, "a92ccba7-d6ee-4134-b980-4698875f63a0");
		addItem(container, UIContext.externalLinks.getHomepageSupportGoogleGroupLabel(), UIContext.externalLinks.getGoogleGroupRSSURL(), null, "e37d2677-96b4-4cfc-8632-ff481118af10");
		addItem(container, UIContext.externalLinks.getHomepageSupportD2DUserCenterLabel(), UIContext.externalLinks.getD2DUserCenterRSSURL(), null,"5a84fef8-ad3a-49b6-81c9-1833fa9a00d0");
				
	    
	    Button closeButton = new Button();
	    closeButton.ensureDebugId("15672f21-bd26-419d-921b-1131f45ed949");
	    closeButton.setText(UIContext.Constants.close());
	    closeButton.setMinWidth(MIN_WIDTH);
	    closeButton.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {				
				window.hide();				
			}});		
		this.addButton(closeButton);
		this.add(container);
		Button helpButton = HelpTopics.createHelpButton(UIContext.externalLinks.getAllFeedsHelp(), MIN_WIDTH);
		helpButton.ensureDebugId("77b21728-c8ec-4a71-8822-cbeca1d6ad73");
        this.addButton(helpButton);
	    	    
	}

	private void addItem(LayoutContainer container, String name, final String url, ClickHandler handler, String debugID) {
		
		if (handler == null)
		{
			handler = new ClickHandler(){
				@Override
				public void onClick(ClickEvent event) {
					com.google.gwt.user.client.Window.open(url, "_BLANK", "");
				}
		    };
		}
		
		container.add(new HTML("&nbsp;"));
		TableData td = new TableData();
		td.setWidth("5%");
		
		Image img = AbstractImagePrototype.create(UIContext.IconBundle.rssIcon()).createImage();
		img.addClickHandler(handler);
		img.addStyleName("rssDialogItem");
		container.add(img, td);
		td = new TableData();
		td.setWidth("85%");
				
		Label label = new Label(name);
		label.ensureDebugId(debugID);
		label.addClickHandler(handler);
		label.addStyleName("rssDialogItem");
		container.add(label, td);
	}
	
	
}
