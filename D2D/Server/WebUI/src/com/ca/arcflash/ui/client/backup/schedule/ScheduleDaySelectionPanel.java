package com.ca.arcflash.ui.client.backup.schedule;

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

public class ScheduleDaySelectionPanel extends LayoutContainer {
	private List<CheckBox> boxList;
	private List<Integer> modifyItemIdxList;
	public ScheduleDaySelectionPanel(){
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
			if(modifyItemIdxList!=null && !modifyItemIdxList.isEmpty() ){
				box.setEnabled(false);
				for(int index: modifyItemIdxList){
					if(index == i){
						box.setEnabled(true);
						box.setValue(true);
						break;
					}
				}
			}
		}
		
		return container;
	}

	public void setBoxesDefaultValue(List<Integer> modifyItemIdxList,int nCurDayIndex) {
		for (int i = 0; i < boxList.size(); i++) {
			CheckBox box = boxList.get(i);
			
			if (modifyItemIdxList != null && !modifyItemIdxList.isEmpty()) {
				box.setEnabled(false);
				for (int index : modifyItemIdxList) {
					if (index == i) {
						box.setEnabled(true);
						box.setValue(true);
						break;
					}
				}
			}
			
			if (i == (nCurDayIndex - 1)) {
				box.setValue(true);				
			}

		}
	}
	
	public static String getDayDisplayName(int nDay) {
		switch(nDay){
		case 0:
			return UIContext.Constants.Sunday();
		case 1:
			return UIContext.Constants.Monday();
		case 2:
			return UIContext.Constants.Tuesday();
		case 3:
			return UIContext.Constants.Wednesday();
		case 4:
			return UIContext.Constants.Thursday();
		case 5:
			return UIContext.Constants.Friday();
		case 6:
			return UIContext.Constants.Saturday();
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
	
}
