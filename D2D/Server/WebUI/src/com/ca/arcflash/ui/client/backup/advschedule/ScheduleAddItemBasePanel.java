package com.ca.arcflash.ui.client.backup.advschedule;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

public abstract class ScheduleAddItemBasePanel extends LayoutContainer{
	
	public ScheduleAddItemBasePanel(){
		this.setWidth(490);
	}
	
	protected LayoutContainer getBaseLayoutContainer(){
		LayoutContainer container = new LayoutContainer();
		container.ensureDebugId("99585d0c-a23f-406e-9d14-d14bbd5971ff");
		container.setStyleAttribute("margin", "10px");
		
		TableLayout tl = new TableLayout(2);
		tl.setWidth("100%");
		tl.setCellPadding(2);
		tl.setCellSpacing(2);
		container.setLayout(tl);
		
		return container;
	}
	
	public abstract ScheduleItemModel save();
	public abstract boolean validate();
	public abstract ScheduleItemModel getCurrentModel();
	public abstract void updateData();
}
