package com.ca.arcflash.ui.client.backup.schedule;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.Utils;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.IconAlign;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.DisclosurePanel;

public abstract class SchedulePanel extends LayoutContainer{

	protected ScheduleDetail[] scheduleDetails;
	private ContentPanel rightLayoutContainer;
	private int selectedIndex = 0;
	private TableData scheduleDetailTableData;
	private ToggleButton[] toggleButtons;
	private AdvanceScheduleModel model;
	//private PlayPauseControl playPause;
	private boolean showMergeGrid;
	
	public SchedulePanel(boolean showMergeGrid) {
		this.showMergeGrid = showMergeGrid;
		initScheduleDetails(showMergeGrid);
	}
	
	protected abstract void initScheduleDetails(boolean showMergeGrid);
	protected abstract String getScheduleSubTitle();
	protected abstract String getScheduleLabelDescription();
	protected abstract String getScheduleStartDateToolTip();
	protected abstract String getScheduleStartTimeToolTip();
	protected abstract String getScheduleDisableToolTip();
	protected abstract String getScheduleEnableToolTip();
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		TableLayout layout = new TableLayout();
		layout.setWidth("100%");
		this.setLayout(layout);
		
//		playPause = new PlayPauseControl();
//		playPause.setEnable(true);
//		playPause.setPauseIconText(UIContext.Constants.scheduleDisable());
//		playPause.setPauseIconTip(getScheduleDisableToolTip());
//		playPause.setPlayIconText(UIContext.Constants.scheduleEnable());
//		playPause.setPlayIconTip(getScheduleEnableToolTip());
//		playPause.setStyleAttribute("padding-left", "15px");
		//this.add(playPause);
		
		DisclosurePanel scheduleSettings = Utils.getDisclosurePanel(getScheduleSubTitle());
		LayoutContainer mainContainer = new LayoutContainer();
		TableLayout mainLayout = new TableLayout(2);
		if(GXT.isChrome)
			mainLayout.setWidth("100%");
		
		mainContainer.setLayout(mainLayout);
		TableData td1 = new TableData();
		td1.setVerticalAlign(VerticalAlignment.TOP);
		td1.setWidth("13%");
		mainContainer.add(getLeftPanel(),td1);
		TableData td2 = new TableData();
		td2.setVerticalAlign(VerticalAlignment.TOP);
		td2.setWidth("87%");
		mainContainer.add(getRightPanel(), td2);
		scheduleSettings.add(mainContainer);
		
		this.add(scheduleSettings);
		
	}
	
	private LayoutContainer getLeftPanel(){
		LayoutContainer leftLayoutContainer = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setCellPadding(2);
		tl.setCellSpacing(2);
		tl.setWidth("100%");
		leftLayoutContainer.setLayout(tl);
	   /*Calendar.SUNDAY = 1, ..., Calendar.SATURDAY=7*/
		toggleButtons = new ToggleButton[7];
		toggleButtons[0] = createToggleButton(UIContext.Constants.scheduleSunday(),"f89cf20c-5146-43a7-8288-72efc274548d", 0);
		toggleButtons[1] = createToggleButton(UIContext.Constants.scheduleMonday(),"02e225fe-d4b9-4cfa-99a6-37730c611d58", 1);
		toggleButtons[2] = createToggleButton(UIContext.Constants.scheduleTuesday(),"44df22f0-400c-4a34-bf97-118714eb7624", 2);
		toggleButtons[3] = createToggleButton(UIContext.Constants.scheduleWednesday(),"92bcb70d-4fba-41b3-b223-c3da69dfd756", 3);
		toggleButtons[4] = createToggleButton(UIContext.Constants.scheduleThursday(),"a4eb193e-0ea6-4cf2-a98c-5d953166af17", 4);
		toggleButtons[5] = createToggleButton(UIContext.Constants.scheduleFriday(),"08375f10-1f7d-4803-aa5a-19b0c98b4d81", 5);
		toggleButtons[6] = createToggleButton(UIContext.Constants.scheduleSaturday(),"c424c659-b89a-4a52-a944-bfd7e9a7cba3", 6);
		toggleButtons[0].toggle(true);
		
		for (ToggleButton button : toggleButtons) {
			leftLayoutContainer.add(button);
		}
		return leftLayoutContainer;
	}
	
	 private ToggleButton createToggleButton(String name,String debugID, final int index) {   
	    final ToggleButton button = new ToggleButton(name);
	    button.ensureDebugId(debugID);
	    button.setToggleGroup("weekLayoutButtons"); 
	    Listener<ButtonEvent> event = new Listener<ButtonEvent>() {
			@Override
			public void handleEvent(ButtonEvent be) {
				if(!be.<ToggleButton> getComponent().isPressed()) {   
			          return;   
			     }   

				showRightPanel(index);
			}
		};
		button.setWidth(100);
	    button.addListener(Events.Toggle, event);   
	    button.setAllowDepress(false);   
	    return button;   
	} 
	 
	 public void setButtonIcon(int index, boolean isShowJobIcon, boolean isShowThrottleIcon, boolean isShowMergeIcon){
		 if( index <0 || index >=7)
			 return;
		 ToggleButton button = toggleButtons[index];
		 NodeList<com.google.gwt.dom.client.Element> elements = button.getElement().getElementsByTagName("button");
		 if((elements != null) && (elements.getLength()>0)){
			elements.getItem(0).addClassName("schedule_item_text_align");
		 }
		 
		 AbstractImagePrototype icon = null;
		 if(isShowJobIcon && isShowThrottleIcon || isShowMergeIcon && isShowJobIcon){
			 icon = AbstractImagePrototype.create(UIContext.IconBundle.schedule_job_throttle_icon());
		 }
		 else if(isShowJobIcon ){
			 icon = AbstractImagePrototype.create(UIContext.IconBundle.schedule_job_icon());
		 }
		 else if(isShowThrottleIcon || isShowMergeIcon){
			 icon = AbstractImagePrototype.create(UIContext.IconBundle.schedule_throttle_icon());
		 }
		 
		 if(icon!=null){
			  button.setIconAlign(IconAlign.LEFT);
			  button.setIcon(icon);
			  
			  if(GXT.isGecko){
				  elements = button.getElement().getElementsByTagName("img");
				  if((elements != null) && (elements.getLength()>0)){
						 elements.getItem(0).addClassName("schedule_item_icon_ff");
				  }
			  }
		 }
		 else{
			 button.setIcon(null);
		 }
	 }
	 
	private LayoutContainer getRightPanel(){
		rightLayoutContainer = new ContentPanel();
		rightLayoutContainer.setHeaderVisible(false);
		TableLayout tl = new TableLayout();
		tl.setWidth("100%");
		rightLayoutContainer.setLayout(tl);
		
		scheduleDetailTableData= new TableData();
		scheduleDetailTableData.setPadding(4);
		scheduleDetailTableData.setVerticalAlign(VerticalAlignment.TOP);
		scheduleDetailTableData.setHorizontalAlign(HorizontalAlignment.CENTER);
		rightLayoutContainer.add(scheduleDetails[0], scheduleDetailTableData);
		rightLayoutContainer.layout();
		return rightLayoutContainer;
	}
	
	 private void showRightPanel(int index){
		 if(selectedIndex==index){
			 return;
		 }
		 
		 ScheduleDetail tempItem = scheduleDetails[selectedIndex];
		 rightLayoutContainer.remove(tempItem);
		 rightLayoutContainer.add(scheduleDetails[index],scheduleDetailTableData);
		 if (!showMergeGrid){
			 scheduleDetails[index].getMergeGrid().hide();
			 scheduleDetails[index].getMergeButtons().hide();
		 }
		 rightLayoutContainer.layout();
		 selectedIndex = index;
	 }

	 public List<ScheduleDetailItemModel> getSourceScheduleItem(int index){
		 if((index>=0)&&(index<7)){
			 return scheduleDetails[index].getScheduleDetailStore().getModels();
		 }
		 return null;
	 }
	 
	 public ScheduleDetail[] getAllScheduleDetails(){
		 return scheduleDetails;
	 }

	 public void refresh(AdvanceScheduleModel scheduleModel, boolean isEdit, boolean showMergeGrid){
		this.model = scheduleModel;
		 
//		Boolean enableSchedule = model.getIsEnableSchedule();
//		if(enableSchedule!=null)
//			playPause.setEnable(enableSchedule);
		
//		if((model.daylyScheduleDetailItemModel == null)||
//				(model.daylyScheduleDetailItemModel.size()==0)){
//			model.daylyScheduleDetailItemModel = new ArrayList<DailyScheduleDetailItemModel>(7); 
//			for(int i=0;i<7; i++){
//				DailyScheduleDetailItemModel dailyItem = new DailyScheduleDetailItemModel();
//				dailyItem.dayOfweek = i+1;
//				model.daylyScheduleDetailItemModel.add(dailyItem);
//			}
//		}else {	
		
		if(isEdit && model.daylyScheduleDetailItemModel != null){
			for (int i = 0; i < scheduleDetails.length; i++) {				
				for (DailyScheduleDetailItemModel detailItem : model.daylyScheduleDetailItemModel) {
					if (detailItem.dayOfweek == (i + 1)) {
						scheduleDetails[i].refresh(detailItem, isEdit, showMergeGrid);
						break;
					}
				}
			}
		}else{
			for (int i = 0; i < scheduleDetails.length; i++) {
				scheduleDetails[i].refresh(new DailyScheduleDetailItemModel(), isEdit, showMergeGrid);
			}
		}
//		}
		 
	 }
	 
	 public boolean validate(){
		for (ScheduleDetail detail : scheduleDetails) {
			boolean result = detail.validate();
			if(!result){
				return result;
			}
		}
		return true;
	 }
	 
	 public void save(){
		// model.setIsEnableBackup(playPause.isEnable());
		 if(model.daylyScheduleDetailItemModel == null){
			 model.daylyScheduleDetailItemModel = new ArrayList<DailyScheduleDetailItemModel>();
		 }else{
			 model.daylyScheduleDetailItemModel.clear();
		 }
		 for(int i = 0; i<scheduleDetails.length; i++){
			 scheduleDetails[i].save();
			 model.daylyScheduleDetailItemModel.add(scheduleDetails[i].getSchedleModel());
		 }
	 }
	 
	 public void setEditable(boolean editable) {
		// playPause.setEditable(editable);
		 for(int i = 0; i<scheduleDetails.length; i++)
			scheduleDetails[i].setEditable(editable);
			
	 }
}
