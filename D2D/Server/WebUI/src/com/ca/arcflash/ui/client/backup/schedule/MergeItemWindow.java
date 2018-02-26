
package com.ca.arcflash.ui.client.backup.schedule;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.HelpTopics;
import com.ca.arcflash.ui.client.common.AdsTimeField;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

public class MergeItemWindow extends Window {
	private MergeDetailItemModel model;
	
	private AdsTimeField startTime;
	
	private AdsTimeField endTime;
	
	private SelectionListener<ButtonEvent> OKButtonListener;
	
	private Button OKButton;
	
	private ScheduleDetail parentPanel;
	
	public MergeItemWindow(MergeDetailItemModel model,boolean isNewAdd,ScheduleDetail parent ) {
		this.model = model;
		this.parentPanel = parent;
		this.setWidth(340);
		this.setResizable(false);
		//this.ensureDebugId("25686133-5e9c-4258-9d2a-36b61316556e");
		if(isNewAdd)
			this.setHeadingHtml(parentPanel.getMergeAddWindowHeader());
		else
			this.setHeadingHtml(parentPanel.getMergeEditWindowHeader());
		
		OKButton = new Button(UIContext.Constants.ok());
		OKButton.ensureDebugId("02f54fa2-520c-406b-b9e9-b76d5c346b83");
		OKButton.setWidth(80);
		
		Button cancelButton = new Button(UIContext.Constants.backupSettingsCancel());
		cancelButton.ensureDebugId("8d32647c-e7e5-4393-8abd-cb3c9bff1d1a");
		cancelButton.setWidth(80);
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				hide();
			}
		});
		
		Button helpButton = new Button(UIContext.Constants.help());
		helpButton.setWidth(80);
		helpButton.ensureDebugId("1a61cf82-50ad-4509-8597-2f4af3e7a174");
		helpButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				String URL = parentPanel.getThrottleHelpURL();
				HelpTopics.showHelpURL(URL);
			}
		});
		
		this.addButton(OKButton);
		this.addButton(cancelButton);
		this.addButton(helpButton);
	}
	
	public void setOKButtonListener(SelectionListener<ButtonEvent> OKButtonListener){
		this.OKButtonListener = OKButtonListener;
	}
	
	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		
		TableLayout tl = new TableLayout(2);
		tl.setWidth("98%");
		tl.setCellPadding(2);
		tl.setCellSpacing(2);
		this.setLayout(tl);
		
		addStartTimeRow();
		addEndTimeRow();
		
		if(OKButtonListener!=null)
			OKButton.addSelectionListener(OKButtonListener);
	}
	
	private void addWidget(LabelField label, Widget widget){
		TableData td = new TableData();
		td.setWidth("35%");
		add(label, td);
		
		td = new TableData();
		td.setWidth("65%");
		add(widget, td);
	}
	
	private void addStartTimeRow(){
		LabelField startLabelField = new LabelField(UIContext.Constants.scheduleStartAt());
		startTime = new AdsTimeField("b63c3ba1-c06b-4757-ac4e-722d927ae312");
		startTime.ensureDebugId("4572811a-df57-4d4a-929c-6d57e90c5c4f");
		startTime.setTimeValue(model.startTimeModel);
		startTime.setEditable(false);
		startTime.setAllowBlank(false);
		
		addWidget(startLabelField, startTime);
	}
	private void addEndTimeRow() {
		LabelField endLabelField = new LabelField(UIContext.Constants.scheduleStopAt());
		endTime = new AdsTimeField("8820cf88-96df-4145-b20e-ab4762fbe902");
		endTime.ensureDebugId("79ebf329-7db3-4ec4-930d-6cf9fb9736d0");
		endTime.setTimeValue(model.endTimeModel);
		endTime.setEditable(false);
		endTime.setAllowBlank(false);
		
		addWidget(endLabelField, endTime);
	}
	
	public MergeDetailItemModel save() {
		model.startTimeModel = startTime.getTimeValue();
		model.endTimeModel = endTime.getTimeValue();
		
		return model;
	}
	
	public boolean validate() {
		if(!startTime.validate())
			return false;
		if(!endTime.validate())
			return false;
		return true;
	}
	
	public MergeDetailItemModel getCurrentModel() {
		MergeDetailItemModel currentModel = new MergeDetailItemModel();
		currentModel.startTimeModel = startTime.getTimeValue();
		currentModel.endTimeModel = endTime.getTimeValue();
		return currentModel;
	}
}
