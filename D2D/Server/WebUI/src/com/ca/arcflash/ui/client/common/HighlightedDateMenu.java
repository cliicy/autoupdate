package com.ca.arcflash.ui.client.common;

import java.util.Date;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.widget.menu.Menu;

public class HighlightedDateMenu extends Menu {
	
	  private Date selectedDate = null; 
	  /**
	   * The internal date picker.
	   */
	  protected HighlightedDatePicker picker;

	  public HighlightedDateMenu() {
	    picker = createHighlightedDatePicker();
	    add(picker);
	    addStyleName("x-date-menu");
	    setAutoHeight(true);
	    plain = true;
	    showSeparator = false;
	    setEnableScrolling(false);
	  }
	  
	  /**
	   * Create HighlightedDatePicker
	   * @return HighlightedDatePicker object
	   */
	  protected HighlightedDatePicker createHighlightedDatePicker() {
		  return new HighlightedDatePicker(){

				@Override
				protected void onLoad() {
					super.onLoad();
					 if(selectedDate != null) {
					    	setValue(selectedDate);
					    }
				};
		    };
	  }

	  /**
	   * Returns the selected date.
	   * 
	   * @return the date
	   */
	  public Date getDate() {
	    return picker.getValue();
	  }

	  /**
	   * Returns the date picker.
	   * 
	   * @return the date picker
	   */
	  public HighlightedDatePicker getDatePicker() {
	    return picker;
	  }
	  
//	  @Override
//	  protected void onShow() {
//	    super.onShow();
//	    if(selectedDate != null) {
//	    	picker.setValue(selectedDate);
//	    }
//	  }
	  
	  public void setDate(Date date) {
		  selectedDate = date;
	  }
	}
