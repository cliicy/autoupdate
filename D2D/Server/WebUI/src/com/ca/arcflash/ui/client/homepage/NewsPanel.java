package com.ca.arcflash.ui.client.homepage;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Frame;
/**
 * 
 * @author gonro07
 *we use rss.html to render RSS.
 *The String registry key ShowRSS will used by filter to disable the rss.html rendering.
 */
public class NewsPanel extends LayoutContainer {
	String language= "";
	String  country= "";
	public NewsPanel(String language, String country){
		this.language = language;
		this.country = country;
	}
	public void render(Element target, int index) {
		super.render(target, index);
		//this.setStyleAttribute("padding", "4px");
		this.setStyleAttribute("margin", "0px 2px 0px 4px");
	    Frame frame = new Frame("rss.jsp?language="+language+"&country="+this.country);
	    IFrameElement as = IFrameElement.as(frame.getElement());
	    as.setFrameBorder(0);
	    as.setScrolling("no");
	    as.setAttribute("frameborder", "0");
//	    frameborder (1|0)          1         -- request frame borders? --
//	    		  marginwidth %Pixels;       #IMPLIED  -- margin widths in pixels --
//	    		  marginheight %Pixels;      #IMPLIED  -- margin height in pixels --
//	    		  scrolling   (yes|no|auto)  auto      -- scrollbar or none --
//	    		  align       %IAlign;       #IMPLIED  -- vertical or horizontal alignment --
//	    		  height      %Length;       #IMPLIED  -- frame height --
//	    		  width       %Length;       #IMPLIED  -- frame width --
	   //Maybe we can use css to control frame
	    as.setAttribute("marginwidth", "0");
	    as.setAttribute("scrolling", "no");
	    as.setAttribute("height", "23");
	    frame.setStyleName("");

	    frame.setWidth("100%");

	    this.add(frame);
	}
	
}
