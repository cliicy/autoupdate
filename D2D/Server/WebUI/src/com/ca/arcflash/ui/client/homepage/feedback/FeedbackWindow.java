package com.ca.arcflash.ui.client.homepage.feedback;

import com.ca.arcflash.ui.client.UIContext;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Frame;

public class FeedbackWindow extends Window {

	private LayoutContainer feedbackPanel;	
	private Frame frame;
	private String feedBackHtml;
	private LoadHandler loadHandler;	
	
	public FeedbackWindow(String feedBackHtml)
	{	
		
		this.feedBackHtml = feedBackHtml;		
		feedbackPanel = new LayoutContainer();			
		FitLayout fl = new FitLayout();
		feedbackPanel.setLayout(fl);
		this.setHeadingHtml(UIContext.externalLinks.getHomepageSupportSendFeedbackLabel());
		this.setWidth(700);
		this.setHeight(320);
		this.setScrollMode(Scroll.AUTO);
		this.setLayout(new FitLayout());
		this.addFrame(this);
		this.setResizable(false);				
	}
	
	@Override
	protected void onRender(Element parent, int pos) 
	{
		super.onRender(parent, pos);
	}
	
	private void addFrame(ContentPanel cp)
	{
		frame = new Frame();
		refresh();
		
	    IFrameElement as = IFrameElement.as(frame.getElement());
	    as.setFrameBorder(0);
	    as.setScrolling("auto");
	    as.setAttribute("frameborder", "0");
	    as.setAttribute("marginwidth", "1");
	    as.setAttribute("scrolling", "auto");
	    frame.setStyleName("");
	    
	    // frame.setWidth("780px");
	   // frame.setHeight("600px")
	    
	    frame.setWidth("100%");
	    frame.setHeight("100%");
	    
	    loadHandler  = new LoadHandler()
		{	
			@Override	
			public void onLoad(LoadEvent event)
			{					
				feedbackPanel.unmask();
			}
		};
		
	    frame.addLoadHandler(loadHandler);
	    
	    feedbackPanel.add(frame);	    
	    cp.add(feedbackPanel);
	}
	
	public void refresh()
	{
		if ((frame != null)&&(feedBackHtml != null))
		{				
			frame.setUrl(feedBackHtml);			
		}
	}	
	
	public void feedMask()
	{
		feedbackPanel.mask(UIContext.Constants.satisfactionLoading());		
	}
}
