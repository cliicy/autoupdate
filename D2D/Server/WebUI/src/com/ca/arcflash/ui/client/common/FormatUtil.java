package com.ca.arcflash.ui.client.common;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.i18n.DataFormat;
import com.ca.arcflash.ui.client.model.DataFormatModel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;

public class FormatUtil {
	public static DataFormat dfConstant = GWT.create(DataFormat.class);
	
	private static DateTimeFormat timeDateFormat;
	private static DateTimeFormat timeFormat;
	private static DateTimeFormat shortTimeFormat;
	private static DateTimeFormat shortDateFormat;

	private static boolean isFieldIsNull(String fieldName){
		if((UIContext.serverVersionInfo == null) || (UIContext.serverVersionInfo.dataFormat == null)){
			return true;
		}
		else{
			DataFormatModel model = UIContext.serverVersionInfo.dataFormat;
			String field = (String)model.get(fieldName);
			if((field == null) ||(field.isEmpty())){
				return true;
			}
			else{
				return false;
			}
		}
		
	}
	public static DateTimeFormat getTimeDateFormat(){
		if(timeDateFormat == null){
			if(isFieldIsNull("timeDateFormat")){
				timeDateFormat = DateTimeFormat.getFormat(dfConstant.timeDateFormat());
			}
			else{
				timeDateFormat = DateTimeFormat.getFormat(UIContext.serverVersionInfo.dataFormat.getTimeDateFormat());
			}
		}
		return timeDateFormat;
	}
	
	public static DateTimeFormat getTimeFormat(){
		if(timeFormat == null){
			if(isFieldIsNull("timeFormat")){
				timeFormat = DateTimeFormat.getFormat(dfConstant.timeFormat());
			}
			else{
				timeFormat = DateTimeFormat.getFormat(UIContext.serverVersionInfo.dataFormat.getTimeFormat());
			}
			
		}
		return timeFormat;
	}
	
	public static DateTimeFormat getShortTimeFormat(){
		if(shortTimeFormat == null){
			if(isFieldIsNull("shortTimeFormat")){
				shortTimeFormat = DateTimeFormat.getFormat(dfConstant.shortTimeFormat());
			}
			else{
				shortTimeFormat = DateTimeFormat.getFormat(UIContext.serverVersionInfo.dataFormat.getShortTimeFormat());
			}
			
		}
		return shortTimeFormat;
	}
	
	public static DateTimeFormat getShortDateFormat(){
		if(shortDateFormat == null){
			if(isFieldIsNull("dateFormat")){
				shortDateFormat = DateTimeFormat.getFormat(dfConstant.dateFormat());
			}
			else{
				shortDateFormat = DateTimeFormat.getFormat(UIContext.serverVersionInfo.dataFormat.getDateFormat());
			}
			
		}
		return shortDateFormat;
	}
	
	public static String getTimeDateFormatPattern(){
		return getTimeDateFormat().getPattern();
	}
}
