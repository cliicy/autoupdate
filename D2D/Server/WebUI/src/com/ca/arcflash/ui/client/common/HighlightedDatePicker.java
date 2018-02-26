package com.ca.arcflash.ui.client.common;

import java.util.ArrayList;
import java.util.Date;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.DatePicker;
import com.google.gwt.user.client.Element;

public class HighlightedDatePicker extends DatePicker {
	
	protected Element[] cells;
	public final ArrayList<String> selectedDates = new ArrayList<String>();
	public final ArrayList<String> neighbourDates =  new ArrayList<String>();
	private final static String today_tooltip = FormatUtil.getShortDateFormat().format(new Date());
	
	  @Override
	  protected void onRender(Element target, int index) {
		  super.onRender(target, index);
		  setContextMenu(null);
		  setValue(new Date());
		  setMaxDate(Utils.maxDate);
		  setMinDate(Utils.minDate);
		  highlightSelectedDates();

		  // liuwe05 2010-12-26 fix Issue: 19937300    Title: GUI:RECOVERY POINT DATE
		  // add an additional listener for today button, otherwise the selected dates won't be highlighted
		  // because onDayClick() won't be called for today button
		  if (todayBtn != null)
		  {
			  todayBtn.addSelectionListener(new SelectionListener<ButtonEvent>()
			  {
			  	  public void componentSelected(ButtonEvent ce)
				  {
					  highlightSelectedDates();
				  }
			  });
			  Utils.addToolTip(todayBtn, today_tooltip);
		  }
	  }	 
	  
	  public void clearAll()
	  {
		  selectedDates.clear();	
		  neighbourDates.clear();
	  }
	  
	  public void addSelectedDate(Date d, boolean isInCurrentMonth)
	  {
		  DateWrapper dr = new DateWrapper(d);
		  int year = dr.getFullYear();
		  int month = dr.getMonth();
		  int day = dr.getDate();
		  String dateStr = year + "," + month + "," + day;
		  if(isInCurrentMonth)
			  selectedDates.add(dateStr);	
		  else
			  neighbourDates.add(dateStr);
	  }
	  
	  public void highlightSelectedDates()
	  {
		//Highlight any selected dates
		  cells = Util.toElementArray(el().select("table.x-date-inner tbody td"));
		  
		  for (int i = 0; i < cells.length; i++)
		  {
			  El cellEl = new El(cells[i]);			  
			  String date = cellEl.firstChild().dom.getPropertyString("dateValue");
			  
			  if (selectedDates.contains(date))
			  {
				  cellEl.addStyleName("x-date-highlighted");
			  }
			  else if(neighbourDates.contains(date))
			  {
				  cellEl.addStyleName("x-date-highlighted-grey");
			  }
		  }
	  }
	  
	  protected void onDayClick(ComponentEvent ce) {
	    ce.stopEvent();
	    El target = ce.getTargetEl();
	    El e = target.findParent("a", 5);
	    if (e != null) {
	      String dt = e.dom.getPropertyString("dateValue");
	      if (dt != null) {
	        handleDateClick(e, dt);
	        highlightSelectedDates();
	        return;
	      }
	    }	    
	    highlightSelectedDates();
	  }
	  private void handleDateClick(El target, String dt) {
	    String[] tokens = dt.split(",");
	    int year = Integer.parseInt(tokens[0]);
	    int month = Integer.parseInt(tokens[1]);
	    int day = Integer.parseInt(tokens[2]);
	    Date d = new DateWrapper(year, month, day).asDate();
	    if (d != null && !target.getParent().hasStyleName("x-date-disabled")) {
	      setValue(d);
	    }
	  }
	 
	  public void showToday()
	  {
		  todayBtn.fireEvent(Events.Select);
	  }
	  
}
