package com.ca.arcflash.ui.client.backup.schedule;

import java.util.ArrayList;
import java.util.List;
import com.ca.arcflash.ui.client.UIContext;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.ui.HTML;

public class ScheduleCopyWindow extends Window{
	private int exceptIndex;
	private ScheduleDetail parentDetail;
	private CheckBox[] daysCheckBox;
	private CheckBox enableCopySchedule;
	private CheckBox enableCopyThrotting;
	private CheckBox enableCopyMerge;
	
	public ScheduleCopyWindow(int except, ScheduleDetail parent){
		this.exceptIndex = except;
		this.parentDetail = parent;
		this.setWidth(450);
		this.setResizable(false);
		this.setHeadingHtml(parentDetail.getCopyButtonText());
		this.ensureDebugId("d31666fd-cd1f-4c6f-b260-c808eb36641a");
		
		TableLayout layout = new TableLayout();
		layout.setWidth("100%");
		layout.setCellPadding(2);
		layout.setCellSpacing(2);
		this.setLayout(layout);
		
		initSelectButtons();
		initDaysCheckBox();
		initCopyOption();
		initOKCancelButtons();
		
	}
	
	private void setDaysCheckBox(boolean value){
		for(int i=0;i<daysCheckBox.length;i++){
			if(i==exceptIndex)
				continue;
			daysCheckBox[i].setValue(value);
		}
	}
	
	private void initOKCancelButtons(){
		Button oKButton = new Button(UIContext.Constants.ok());
		oKButton.ensureDebugId("27286397-bcd5-46ac-99a9-712ebe3cd197");
		oKButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				processOKButton();
			}
		});
		this.addButton(oKButton);
		Button cancelButton = new Button(UIContext.Constants.cancel());
		cancelButton.ensureDebugId("252ca0e5-6eda-46d9-936d-181167dc673e");
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				hide();
			}
		});
		this.addButton(cancelButton);
	}
	
	private void processOKButton(){
		final List<Integer> destIndexs = new ArrayList<Integer>();
		for(int i=0;i<daysCheckBox.length;i++){
			if(daysCheckBox[i].getValue()){
				destIndexs.add(i);
			}
		}
		
		if(destIndexs.size()==0){
			ScheduleUtils.showMesssageBox(UIContext.productNameD2D,
					UIContext.Constants.scheduleCopySelectedDaysEmpty(),  MessageBox.ERROR);
			return;
		}
		
		if(!enableCopySchedule.isEnabled()){
			enableCopySchedule.setValue(false);
		}
		
		if(!enableCopyThrotting.isEnabled()){
			enableCopyThrotting.setValue(false);
		}
		
		if (enableCopyMerge !=null){
			if(!enableCopyMerge.isEnabled()){
				enableCopyMerge.setValue(false);
			}		
			if((enableCopySchedule.getValue() == false) && ( enableCopyThrotting.getValue() == false)&& (enableCopyMerge.getValue() == false)){
				ScheduleUtils.showMesssageBox(UIContext.productNameD2D,
						UIContext.Constants.scheduleCopyOptionEnable(),  MessageBox.ERROR);
				return;
			}
		}else{		
			if((enableCopySchedule.getValue() == false) && (enableCopyThrotting.getValue() == false)){
				ScheduleUtils.showMesssageBox(UIContext.productNameD2D,
						UIContext.Constants.scheduleCopyOptionEnableWithoutMerge(),  MessageBox.ERROR);
				return;
			}
		}
		
		Listener<MessageBoxEvent> messageBoxHandler = new Listener<MessageBoxEvent>() {
			public void handleEvent(MessageBoxEvent be) {
				if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
					if (parentDetail.getShowMergeFlag()){
						parentDetail.copyScheduleItemsToOtherDays(destIndexs, 
								enableCopySchedule.getValue(), enableCopyThrotting.getValue(), enableCopyMerge.getValue());
						hide();
					}else{
						parentDetail.copyScheduleItemsToOtherDays(destIndexs, 
								enableCopySchedule.getValue(), enableCopyThrotting.getValue(), false);
						hide();
					}
				}
			}
		};
		ScheduleUtils.showConfirmMsgBox(messageBoxHandler);
	}
	
	private void initSelectButtons(){
		LabelField descriptionLabel = new LabelField(parentDetail.getCopyWindowDescription());
		descriptionLabel.setStyleAttribute("font-weight","bold");
		this.add(descriptionLabel);
		
		LayoutContainer selectButtonsContainer= new LayoutContainer();
		TableLayout selectButtonsLayout = new TableLayout(2);
		selectButtonsContainer.setWidth("100%");
		selectButtonsContainer.setLayout(selectButtonsLayout);
		
		Button selectButton = new Button(UIContext.Constants.scheduleSelectAllDays());
		selectButton.ensureDebugId("1a38ab99-1dde-4ab7-b54d-3668a3cd8159");
		selectButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				setDaysCheckBox(true);
			}
		});
		TableData td = new TableData();
		td.setWidth("50%");
		selectButtonsContainer.add(selectButton, td);
		
		Button unSelectButton = new Button(UIContext.Constants.scheduleUnSelectAllDays());
		unSelectButton.ensureDebugId("335a9576-a89c-41e2-9e8c-acaa441508fe");
		unSelectButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				setDaysCheckBox(false);
			}
		});
		td = new TableData();
		td.setWidth("50%");
		selectButtonsContainer.add(unSelectButton, td);
		
		this.add(selectButtonsContainer);
		this.add(new HTML("<hr>"));
	}
	private void initDaysCheckBox(){
		LayoutContainer container = new LayoutContainer();
		TableLayout tableLayout = new TableLayout(4);
		tableLayout.setWidth("100%");
		container.setLayout(tableLayout);
		
		String[] checkBoxItemTexts = new String[7];
		checkBoxItemTexts[0] = UIContext.Constants.scheduleSunday();
		checkBoxItemTexts[1] = UIContext.Constants.scheduleMonday();
		checkBoxItemTexts[2] = UIContext.Constants.scheduleTuesday();
		checkBoxItemTexts[3] = UIContext.Constants.scheduleWednesday();
		checkBoxItemTexts[4] = UIContext.Constants.scheduleThursday();
		checkBoxItemTexts[5] = UIContext.Constants.scheduleFriday();
		checkBoxItemTexts[6] = UIContext.Constants.scheduleSaturday();		
		
		String[] checkBoxItemIDs = new String[7];
		checkBoxItemIDs[0] = "d8fd1fdf-d1c9-49e8-89da-81888d4578e3";
		checkBoxItemIDs[1] = "380810cf-ded9-4d60-bde2-dfc3abe470ba";
		checkBoxItemIDs[2] = "6e0fd0e4-c882-443a-b747-5e2b7ffabe13";
		checkBoxItemIDs[3] = "2ed132b6-8d91-4767-83aa-ae2a8ed4b4af";
		checkBoxItemIDs[4] = "3d135607-245c-4bee-a504-483acd5f4c4a";
		checkBoxItemIDs[5] = "ef498b9f-ecf5-46fe-b521-0d13840e66e9";
		checkBoxItemIDs[6] = "87b3cd30-69e3-42a5-8564-3a46d62d5d93";
		
		daysCheckBox = new CheckBox[7];
		for(int i=0;i<checkBoxItemTexts.length;i++){
			
			daysCheckBox[i] = new CheckBox();
			daysCheckBox[i].setBoxLabel(checkBoxItemTexts[i]);
			daysCheckBox[i].ensureDebugId(checkBoxItemIDs[i]);
			container.add(daysCheckBox[i]);
			if(i == exceptIndex){
				daysCheckBox[i].disable();
				daysCheckBox[i].setValue(false);
			}
		}
		
		this.add(container);
	}
	
	private void initCopyOption(){
		LayoutContainer container = new LayoutContainer();
		TableLayout tableLayout = new TableLayout(2);
		tableLayout.setWidth("100%");
		container.setLayout(tableLayout);
		
		enableCopySchedule = new CheckBox();
		enableCopySchedule.setBoxLabel(parentDetail.getCopyWindowScheduleOptionText());
		enableCopySchedule.ensureDebugId("0953189c-80c5-4f62-b01c-95ccd5e97bed");
		enableCopySchedule.setValue(true);
		container.add(enableCopySchedule);
		
		enableCopyThrotting = new CheckBox();
		enableCopyThrotting.setBoxLabel(parentDetail.getCopyWindowThrottleOptionText());
		enableCopyThrotting.ensureDebugId("15d659d1-1e64-4d79-937c-377d997be5b5");
		enableCopyThrotting.setValue(true);
		container.add(enableCopyThrotting);
		
		if (parentDetail.getShowMergeFlag()){
			enableCopyMerge = new CheckBox();
			enableCopyMerge.setBoxLabel(parentDetail.getCopyWindowMergeOptionText());
			enableCopyMerge.ensureDebugId("a4ccb1b9-4ebd-488b-9162-a40bcdce0778");
			enableCopyMerge.setValue(true);
			container.add(enableCopyMerge);
		}
		
		setEnables();
		
		this.add(new HTML("<hr>"));
		this.add(container);
		
	}
	
	
	private void setEnables(){
		if(this.parentDetail.getScheduleDetailStore()==null || this.parentDetail.getScheduleDetailStore().getCount()==0){
			enableCopySchedule.hide();
		}else{
			enableCopySchedule.enable();
		}
		enableCopySchedule.setValue(enableCopySchedule.isEnabled());
		
		if(this.parentDetail.getThrottleStore()==null || this.parentDetail.getThrottleStore().getCount()==0){
			enableCopyThrotting.hide();
		}else{
			enableCopyThrotting.enable();
		}
		enableCopyThrotting.setValue(enableCopyThrotting.isEnabled());
		
		if (parentDetail.getShowMergeFlag()) {
			
			if (this.parentDetail.getMergeStore() == null || this.parentDetail.getMergeStore().getCount() == 0) {
				enableCopyMerge.hide();
			} else {
				enableCopyMerge.enable();
			}
			enableCopyMerge.setValue(enableCopyMerge.isEnabled());
		}
	}
}
