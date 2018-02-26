package com.ca.arcflash.ui.client.common;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.util.Util;

public class EnhancedHightlightedDataPicker extends HighlightedDatePicker {
	private Map<String, Boolean> selectedDatesMap = new HashMap<String, Boolean>();
	private Map<String, Boolean> neighbourDatesMap = new HashMap<String, Boolean>();
	private Set<String> backupSetFlag = new HashSet<String>();
	
	@Override
	public void highlightSelectedDates() {
		//Highlight any selected dates
		  cells = Util.toElementArray(el().select("table.x-date-inner tbody td"));
		  
		  for (int i = 0; i < cells.length; i++)
		  {
			  El cellEl = new El(cells[i]);			  
			  String date = cellEl.firstChild().dom.getPropertyString("dateValue");
			  boolean contains = false;
			  Boolean st = null;
			  
			  if (selectedDatesMap.containsKey(date))
			  {
				  st = selectedDatesMap.get(date);
				  contains = true;
				  				  
			  }
			  else if(neighbourDatesMap.containsKey(date))
			  {
				  st = neighbourDatesMap.get(date);
				  contains = true;
			  }
			
			  if(contains){
				  if(st == null) {
					  //all fail, error
					  cellEl.addStyleName("x-date-highlighted-Error");
				  }else if(st) {
					  //all successful, OK
					  cellEl.addStyleName("x-date-highlighted");
				  }else {
					  //some failed, warning
					  cellEl.addStyleName("x-date-highlighted-Warning");
				  }
			  }
			  
			  if(backupSetFlag.contains(date)) {
				  cellEl.addStyleName("backupSetStartDate");
			  }
		  }
	}
	
	public void addSelectedDate(Date d, boolean status, boolean isInCurrentMonth) {
		DateWrapper dr = new DateWrapper(d);
		int year = dr.getFullYear();
		int month = dr.getMonth();
		int day = dr.getDate();
		String dateStr = year + "," + month + "," + day;
		if (isInCurrentMonth)
			addDate(dateStr, status, selectedDatesMap);
		else
			addDate(dateStr, status, neighbourDatesMap);
	}
	
	public void addBackupSetFlag(Date d) {
		DateWrapper dr = new DateWrapper(d);
		int year = dr.getFullYear();
		int month = dr.getMonth();
		int day = dr.getDate();
		String dateStr = year + "," + month + "," + day;
		backupSetFlag.add(dateStr);
	}
	
	private void addDate(String dateStr, boolean status, Map<String, Boolean> map ) {
		Boolean st = map.get(dateStr);
		
		if(st == null && status) {
			st = status;
		}else if(st != null)
			st = st && status;
		
		map.put(dateStr, st);
	}
	
	public void clearAll() {
		selectedDates.clear();
		selectedDatesMap.clear();
		neighbourDates.clear();
		neighbourDatesMap.clear();
		backupSetFlag.clear();
	}
}
