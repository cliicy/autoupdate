package com.ca.arcflash.ui.client.backup.advschedule;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.layout.ColumnData;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.Widget;

public class ScheduleAddDetailItemDaySelectionPanel extends LayoutContainer {
	private List<CheckBox> boxList;
	public ScheduleAddDetailItemDaySelectionPanel(){
		boxList = new ArrayList<CheckBox>();
		LayoutContainer contentContainer = new LayoutContainer(new FitLayout());
		contentContainer.setBorders(true);
		contentContainer.add(this.createContent(), new FitData(2, 4, 2, 4));
		this.add(contentContainer);
	}
	
	private Widget createContent() {
		LayoutContainer container = new LayoutContainer();
		container.setLayout(new ColumnLayout());
		
		for(int i=0; i<7; i++){
			CheckBox box = new CheckBox();
			box.setBoxLabel(getDayDisplayName(i));			
			boxList.add(box);
			container.add(box, new ColumnData(100));
			box.setEnabled(true);
			box.setValue(true);
		}
		
		return container;
	}

	public void setValue(List<Integer> modifyItemIdxList) {
		for (int i = 0; i < boxList.size(); i++) {
			CheckBox box = boxList.get(i);
			box.setValue(false);
			
			if (modifyItemIdxList != null && !modifyItemIdxList.isEmpty()) {
				for (int index : modifyItemIdxList) {
					if (index == i) {
						box.setEnabled(true);
						box.setValue(true);
						break;
					}
				}
			}
		}
	}
	
	public static String getDayDisplayName(int nDay) {
		switch(nDay){
		case 0:
			return UIContext.Constants.scheduleSunday();
		case 1:
			return UIContext.Constants.scheduleMonday();
		case 2:
			return UIContext.Constants.scheduleTuesday();
		case 3:
			return UIContext.Constants.scheduleWednesday();
		case 4:
			return UIContext.Constants.scheduleThursday();
		case 5:
			return UIContext.Constants.scheduleFriday();
		case 6:
			return UIContext.Constants.scheduleSaturday();
		}
		return "";
	}
	
	public List<Integer> getDayIndexs(){
		List<Integer> daysCheckBox = new ArrayList<Integer>();
		for(int i=0;i<boxList.size();i++){
			if(boxList.get(i).getValue()){
				daysCheckBox.add(i);
			}
		}		
		return daysCheckBox;
	}

	public void getValue(ScheduleItemModel model) {
		for(int i=0; i<7; i++){
			model.setDayofWeek(i, 0);
		}

		for (int day : getDayIndexs()) {
			for (int i = 0; i < 7; i++) {
				if (i == day) {
					model.setDayofWeek(i, 1);
					break;
				}
			}
		}
	}
	

}
