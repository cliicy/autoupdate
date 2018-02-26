package com.ca.arcflash.ui.client.backup.advschedule;



import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.FlashUIConstants;
import com.ca.arcflash.ui.client.FlashUIMessages;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleUtils;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleUtils.ScheduleTypeModel;
import com.ca.arcflash.ui.client.model.DayTimeModel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.Element;

public class ScheduleAddItemMergeWindow extends ScheduleAddItemBaseWindow{
	private ScheduleAddDetailItemTimePanel startTimePanel;
	private ScheduleAddDetailItemTimePanel endTimePanel;
	private ScheduleAddDetailItemDaySelectionPanel daySelectionPanel;
	private static FlashUIMessages uiMessages=UIContext.Messages;
	private static FlashUIConstants uiConstants=UIContext.Constants;
	public ScheduleAddItemMergeWindow(ScheduleItemModel model, boolean isNewAdd) {
		super(model, isNewAdd);	
		this.setWidth(510);
		this.setHeight(240);
		
		
		LayoutContainer container = new LayoutContainer();
		container.ensureDebugId("99585d0c-a23f-406e-9d14-d14bbd5971ff");
		container.setStyleAttribute("margin", "5px");
		this.add(container);
		
		TableLayout tl = new TableLayout(2);
		tl.setWidth("100%");
		tl.setCellPadding(2);
		tl.setCellSpacing(2);
		container.setLayout(tl);
		
		addStartTimeRow(container);
		addSelectionDayRow(container);
		addEndTimeRow(container);

	}
	
	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		
		if(OKButtonListener!=null)
			OKButton.addSelectionListener(OKButtonListener);

	}
	
	private void addStartTimeRow(LayoutContainer container){
		LabelField startLabel = new LabelField(UIContext.Constants.scheduleStartAt());
		DayTimeModel startDayTimeModel = new DayTimeModel();
		startDayTimeModel.setHour(8);
		startDayTimeModel.setMinute(0);
		startTimePanel = new ScheduleAddDetailItemTimePanel(startDayTimeModel);
		ScheduleUtils.addWidget(container,startLabel,startTimePanel);
	}
	
	private void addSelectionDayRow(LayoutContainer container){
		LabelField dayLabel = new LabelField("");
		daySelectionPanel = new ScheduleAddDetailItemDaySelectionPanel();
		ScheduleUtils.addWidget(container,dayLabel,daySelectionPanel);
		
	}
	
	private void addEndTimeRow(LayoutContainer container){
		LabelField endLabel = new LabelField(UIContext.Constants.scheduleStopAt());
		DayTimeModel endDayTimeModel = new DayTimeModel();
		endDayTimeModel.setHour(18);
		endDayTimeModel.setMinute(0);
		endTimePanel = new ScheduleAddDetailItemTimePanel(endDayTimeModel);
		ScheduleUtils.addWidget(container,endLabel,endTimePanel);
	}


	@Override
	public ScheduleItemModel save() {
		saveModelData(model);
		return model;		
	}
	
	@Override
	public boolean validate() {
		if(!startTimePanel.validate())
			return false;
		if(!endTimePanel.validate())
			return false;
		
		List<Integer> dayList = daySelectionPanel.getDayIndexs();
		if(dayList.isEmpty()){
			ScheduleUtils.showMesssageBox(uiConstants.errorTitle(),uiConstants.selectADay(), MessageBox.ERROR);			
			return false;
		}
		
		return true;
	}
	
	@Override
	public ScheduleItemModel getCurrentModel() {
		ScheduleItemModel itemModel = new ScheduleItemModel();
		saveModelData(itemModel);
		return itemModel;
	}
	
	protected void saveModelData(ScheduleItemModel itemModel){
		itemModel.setScheduleType(ScheduleTypeModel.RepeatJob);
		itemModel.startTimeModel = startTimePanel.getTimeModel();
		itemModel.endTimeModel = endTimePanel.getTimeModel();
		itemModel.setJobType(ScheduleUtils.MERGE);
		daySelectionPanel.getValue(itemModel);
		itemModel.setDescription(uiConstants.scheduleMergeDescription());
	}
	
	@Override
	protected String getScheduleAddWindowHeader(){
		return UIContext.Constants.scheduleMergeAddWindowHeader();
	}
	
	@Override
	protected String getScheduleEditWindowHeader(){
		return UIContext.Constants.scheduleMergeEditWindowHeader();
	}	


	@Override
	public void updateData() {
		startTimePanel.setValue(model.startTimeModel);
		endTimePanel.setValue(model.endTimeModel);
		List<Integer> modifyItemIdxList = new ArrayList<Integer>();
		for(int i=0; i<7; i++){
			if(model.getDayofWeek(i)==ScheduleUtils.SELECTED)
				modifyItemIdxList.add(i);
		}
		daySelectionPanel.setValue(modifyItemIdxList);		
	}
	
	@Override
	protected String getScheduleHelpURL(){
		return UIContext.externalLinks.getBackupSettingAdvanceMergeHelp();
	}
}
