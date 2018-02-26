package com.ca.arcflash.ui.client.backup.advschedule;

import java.util.ArrayList;

import com.ca.arcflash.ui.client.UIContext;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.ui.Image;

public class NotificateSet {
	private FieldSet notificationSet;
	private ArrayList<String> notificationMessages = new ArrayList<String>();
	private ArrayList<String> warningNotificationMessages = new ArrayList<String>();
	private ArrayList<String> infoNotificationMessages = new ArrayList<String>();
	public NotificateSet(){
		notificationSet = new FieldSet();
		//shaji02: for adding debug id.
		notificationSet.ensureDebugId("a6b20dcc-e40d-412e-abac-74b0a43c22ac");
		notificationSet.setHeadingHtml(UIContext.Messages.ArchiveSettingsNodifications());
		notificationSet.setCollapsible(true);
		TableLayout displayLayout = new TableLayout();
		displayLayout.setWidth("100%");
		displayLayout.setCellSpacing(1);
		displayLayout.setColumns(2);
		notificationSet.setLayout(displayLayout);
		notificationSet.setStyleAttribute("margin-top", "5px");
		notificationSet.setVisible(false);
		
	}
	
	public FieldSet getNotificateFieldSet(){
		return notificationSet;
	}
	
	private void addDisplayErrorIcon() {
		Image errorImage = new Image(UIContext.IconBundle.logError());
		TableData tableData = new TableData();
		tableData.setStyle("padding: 2px 3px 3px 0px;"); // refer to the GWT default setting.
		tableData.setVerticalAlign(VerticalAlignment.TOP);
		notificationSet.add(errorImage, tableData);
	}
	
	private void addDisplayWaringIcon() {
		Image warningImage = new Image(UIContext.IconBundle.logWarning());
		TableData tableData = new TableData();
		tableData.setStyle("padding: 2px 3px 3px 0px;"); // refer to the GWT default setting.
		tableData.setVerticalAlign(VerticalAlignment.TOP);
		notificationSet.add(warningImage, tableData);
	}
	
	private void addDisplayInfoIcon() {
		Image warningImage = new Image(UIContext.IconBundle.logMsg());
		TableData tableData = new TableData();
		tableData.setStyle("padding: 2px 3px 3px 0px;"); // refer to the GWT default setting.
		tableData.setVerticalAlign(VerticalAlignment.TOP);
		notificationSet.add(warningImage, tableData);
	}
	
	public void showDisplayErrorNotificateSet(String strMsg){
		boolean addToNotifiSet = true;
		String message = strMsg; 
		if(notificationMessages != null){
			if(notificationMessages.contains(message)){
				addToNotifiSet = false;				
			}else{
				addToNotifiSet = true;
				notificationMessages.add(message);
			}			
		}
		if(addToNotifiSet){
			addDisplayErrorIcon();
			notificationSet.add(new LabelField(message));
			notificationSet.setVisible(true);
			notificationSet.expand();
			updateNotificationPane();
		}
	}
	
	public void showDisplayWarningNotificateSet(String strMsg){
		boolean addToNotifiSet = true;
		String message = strMsg; 
		if(warningNotificationMessages != null){
			if(warningNotificationMessages.contains(message)){
				addToNotifiSet = false;				
			}else{
				addToNotifiSet = true;
				warningNotificationMessages.add(message);
			}			
		}
		if(addToNotifiSet){
			addDisplayWaringIcon();
			notificationSet.add(new LabelField(message));
			notificationSet.setVisible(true);
			notificationSet.expand();
			updateNotificationPane();
		}
	}
	

	public void showDisplayInfoNotificateSet(String strMsg){
		boolean addToNotifiSet = true;
		String message = strMsg; 
		if(infoNotificationMessages != null){
			if(infoNotificationMessages.contains(message)){
				addToNotifiSet = false;				
			}else{
				addToNotifiSet = true;
				infoNotificationMessages.add(message);
			}			
		}
		if(addToNotifiSet){
			addDisplayInfoIcon();
			notificationSet.add(new LabelField(message));
			notificationSet.setVisible(true);
			notificationSet.expand();
			updateNotificationPane();
		}
	}
	
	private void updateNotificationPane() {
		notificationSet.setHeadingHtml(UIContext.Messages.ArchiveSettingsNodifications());		
		if(!notificationSet.isExpanded())
			notificationSet.expand();
		notificationSet.layout(true);
	}
	
	public void removeMessageFromErrorNotificationSet(String strMsg){
		String message = strMsg;
		notificationMessages.remove(message);	
		hideOrReconfigureNotificationSet();
	}
	
	public void removeMessageFromWaringNotificationSet(String strMsg){
		String message = strMsg;
		
		warningNotificationMessages.remove(message);	
		hideOrReconfigureNotificationSet();
		
	}
	
	public void removeMessageFromInfoNotificationSet(String strMsg){
		String message = strMsg;
		
		infoNotificationMessages.remove(message);	
		hideOrReconfigureNotificationSet();		
	}
	
	private void hideOrReconfigureNotificationSet(){
		if(infoNotificationMessages.size() == 0 && 
				notificationMessages.size() == 0 && 
				warningNotificationMessages.size() == 0){
			hideNotificationSet();
			notificationSet.getItems().clear();					
		}else{					
			notificationSet.getItems().clear();									
			reconfigureNotificationSet();
		}
	}
	
	private void reconfigureNotificationSet(){
		for(int i = 0; i<notificationMessages.size() ; i++){
			String message =  notificationMessages.get(i);
			addDisplayErrorIcon();
			notificationSet.add(new LabelField(message));		
		}
		
		for(int i = 0; i<warningNotificationMessages.size() ; i++){
			String message =  warningNotificationMessages.get(i);
			addDisplayWaringIcon();
			notificationSet.add(new LabelField(message));			
		}
		
		for(int i = 0; i<infoNotificationMessages.size() ; i++){
			String message =  infoNotificationMessages.get(i);
			addDisplayInfoIcon();
			notificationSet.add(new LabelField(message));			
		}
		
		notificationSet.setVisible(true);
		notificationSet.expand();
		updateNotificationPane();
		
	}	
	
	private void hideNotificationSet()
	{
		notificationSet.setVisible(false);	
	}
	
}
