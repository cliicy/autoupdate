package com.ca.arcflash.ui.client.common.filecopy;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.backup.schedule.ScheduleUtils;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.form.CheckBox;

public class ScheduleDaySelectionPanel extends SimpleContainer {
	private List<CheckBox> boxList;
	public ScheduleDaySelectionPanel(){
		boxList = new ArrayList<CheckBox>();
		this.setBorders(true);
		this.add(this.createContent(), new MarginData(2,4,2,4));
	}

	private Widget createContent() {
		CssFloatLayoutContainer container = new CssFloatLayoutContainer();
		container.ensureDebugId("870b6149-443b-47f7-ba4f-c666ac8301e6");
		for(int i=0; i<7; i++){
			CheckBox box = new CheckBox();
			box.ensureDebugId("fc15a5f2-7c62-4448-a681-a21dfa4a8fa2");
			box.setBoxLabel(getDayDisplayName(i));
			box.setWidth(100);
			boxList.add(box);
			container.add(box);
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
					index = index-1;
					if (index == i) {
						box.setEnabled(true);
						box.setValue(true);
						break;
					}
				}
			}
		}
	}
	
	public void setValue(Boolean[] values){
		if(values.length == 7){
			for (int i = 0; i < values.length; i++) {
				boxList.get(i).setValue(values[i]);
			}
		}
	}

	public static String getDayDisplayName(int nDay) {
		return ScheduleUtils.getDayDisplayName(nDay);
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

	public Boolean[] getValue() {
		Boolean[] daysSelected = new Boolean[7];
		for (int i=0; i<boxList.size();i++) {
			daysSelected[i] = boxList.get(i).getValue();
		}
		
		return daysSelected;
	}


}
