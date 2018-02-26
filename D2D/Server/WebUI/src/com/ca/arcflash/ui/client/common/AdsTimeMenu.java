package com.ca.arcflash.ui.client.common;

import com.extjs.gxt.ui.client.event.PreviewEvent;
import com.extjs.gxt.ui.client.util.BaseEventPreview;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.google.gwt.event.dom.client.KeyCodes;

public class AdsTimeMenu extends Menu {
	private AdsTimePanel timePanel;		
		
	public AdsTimeMenu(String id){
		timePanel = new AdsTimePanel(id);
	    add(timePanel);
		    
	    addStyleName("x-date-menu");
	    setAutoHeight(true);
	    plain = true;
	    showSeparator = false;
	    setEnableScrolling(false);
	    eventPreview = new BaseEventPreview() {

	        @Override
	        protected boolean onAutoHide(PreviewEvent pe) {
	          return AdsTimeMenu.this.onAutoHide(pe);
	        }

	        @Override
	        protected boolean onPreview(PreviewEvent pe) {
	        	return true;
	        }

	        @Override
	        protected void onPreviewKeyPress(PreviewEvent pe) {
	          super.onPreviewKeyPress(pe);
	          if (pe.getKeyCode() == KeyCodes.KEY_ESCAPE) {
	            hide(true);
	          }
	        }
	      };
	}
	
	public AdsTimePanel getTimePanel(){
		return timePanel;
	}
		
}

