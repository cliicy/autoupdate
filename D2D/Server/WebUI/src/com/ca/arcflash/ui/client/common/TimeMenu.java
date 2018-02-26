package com.ca.arcflash.ui.client.common;

import com.ca.arcflash.ui.client.UIContext;
import com.extjs.gxt.ui.client.event.PreviewEvent;
import com.extjs.gxt.ui.client.util.BaseEventPreview;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Event;

public class TimeMenu extends Menu {
	private TimePanel timePanel;		
	private Button okButton;
	private Button cancelButton;
		
	public TimeMenu(String id){
		timePanel = new TimePanel(id);
	    add(timePanel);
	    add(addButton(id));
		    
	    addStyleName("x-date-menu");
	    setAutoHeight(true);
	    plain = true;
	    showSeparator = false;
	    setEnableScrolling(false);
	    eventPreview = new BaseEventPreview() {

	        @Override
	        protected boolean onAutoHide(PreviewEvent pe) {
	          return TimeMenu.this.onAutoHide(pe);
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
	
	public TimePanel getTimePanel(){
		return timePanel;
	}
		
	private LayoutContainer addButton(String id) {
		LayoutContainer bc = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(3);	
		bc.setLayout(tl);
		
		TableData td = new TableData();
		td.setWidth("30%");
		bc.add(new LabelField(), td);
		
		td = new TableData();
		td.setWidth("30%");
		okButton = new Button();
	    okButton.setText(UIContext.Constants.ok());
	    okButton.ensureDebugId("A11FAAFD-6D0C-4208-B371-134297A8DD88" + id);
	    bc.add(okButton, td);
	    
	    td = new TableData();
		td.setWidth("30%");
	    cancelButton = new Button();
	    cancelButton.ensureDebugId("68A865D0-F241-459e-AA91-B6F466D22BA1" + id);
	    cancelButton.setText(UIContext.Constants.cancel());
	    bc.add(cancelButton, td);
	    return bc;
	}
	
	public Button getOKButton() {
		return okButton;
	}
	
	public Button getCancelButton(){
		return cancelButton;
	}
}

