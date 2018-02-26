package com.ca.arcflash.ui.client.backup.advschedule;

import com.ca.arcflash.ui.client.FlashUIConstants;
import com.ca.arcflash.ui.client.UIContext;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class AddScheduleButton extends Button implements Listener<MenuEvent> {	
	protected MenuItem backupScheduleMenuItem;
	protected MenuItem mergeScheduleMenuItem;
	protected MenuItem throttleScheduleMenuItem;	
	protected ScheduleItemModel scheduleItemModel;
	private ScheduleGridPanel parentPanel;
	private FlashUIConstants uiConstants = UIContext.Constants;
	public AddScheduleButton(ScheduleGridPanel parentPanel){
		this.parentPanel = parentPanel;
		this.setText(uiConstants.scheduleAddText());
		this.setIcon(AbstractImagePrototype.create(UIContext.IconBundle.tran_add()));
		this.addStyleName("ARCSERVE-STYLE-BUTTON");
		Menu menu = new Menu();
		addScheduleMenuItems(menu);
		this.setMenu(menu);	
		
		enabelMenuItems();
	}
	
	@Override
	public void onLoad(){
		menu.addStyleName("ARCSERVE-STYLE-MENU");
		backupScheduleMenuItem.addStyleName("ARCSERVE-STYLE-MENU-ITEM");
		mergeScheduleMenuItem.addStyleName("ARCSERVE-STYLE-MENU-ITEM");
		throttleScheduleMenuItem.addStyleName("ARCSERVE-STYLE-MENU-ITEM");
	}
	
	protected void addScheduleMenuItems(Menu menu) {
		backupScheduleMenuItem = new MenuItem(getBackupMenuName());
		backupScheduleMenuItem.ensureDebugId("3c4c556f-a9d2-4597-9372-366d253ee32b");
		backupScheduleMenuItem.addListener(Events.Select, this);
		menu.add(backupScheduleMenuItem);
		
		mergeScheduleMenuItem = new MenuItem(getMergeMenuName());
		mergeScheduleMenuItem.ensureDebugId("3c4c556f-a9d2-4597-9372-366d253ee33c");
		mergeScheduleMenuItem.addListener(Events.Select, this);
		menu.add(mergeScheduleMenuItem);

		throttleScheduleMenuItem = new MenuItem(getThrottleMenuName());
		throttleScheduleMenuItem.ensureDebugId("3c4c556f-a9d2-4597-9372-366d253ee34d");
		throttleScheduleMenuItem.addListener(Events.Select, this);
		menu.add(throttleScheduleMenuItem);

	}
	
	protected void enabelMenuItems(){
		backupScheduleMenuItem.show();
		mergeScheduleMenuItem.show();
		throttleScheduleMenuItem.show();
	}
	
	protected String getBackupMenuName(){
		return uiConstants.addBackupSchedule();
	}
	
	protected String getMergeMenuName(){
		return uiConstants.addMergeSchedule();
	}
	
	protected String getThrottleMenuName(){
		return uiConstants.addThrottleSchedule();
	}
	
	@Override
	public void handleEvent(MenuEvent be) {

		if (be.getType() == Events.Select) {
			if (be.getItem() == backupScheduleMenuItem) {
			ScheduleItemModel newModel = new ScheduleItemModel();
				parentPanel.showScheduleDetailWindow(-1, newModel);				
			} else if (be.getItem() == mergeScheduleMenuItem) {			
				ScheduleItemModel newModel = new ScheduleItemModel();
				parentPanel.showMergeWindow(-1, newModel);
			} else if (be.getItem() == throttleScheduleMenuItem) {
				ScheduleItemModel newModel = new ScheduleItemModel();
				parentPanel.showThrottleWindow(-1, newModel);
			}
		}	
	}	


}
