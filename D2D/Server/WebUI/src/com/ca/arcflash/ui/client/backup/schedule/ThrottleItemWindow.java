package com.ca.arcflash.ui.client.backup.schedule;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.AdsTimeField;
import com.ca.arcflash.ui.client.common.ErrorAlignedNumberField;
import com.ca.arcflash.ui.client.common.HelpTopics;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class ThrottleItemWindow extends Window {
	private ThrottleModel model;
	
	private AdsTimeField startTime;
	
	private AdsTimeField endTime;
	
	private ErrorAlignedNumberField throttleField;
	
	private SelectionListener<ButtonEvent> OKButtonListener;
	
	private Button OKButton;
	
	private ScheduleDetail parentPanel;
	
	private static Integer allowMaxThrottleValue = new Integer(99999);
	private static Integer allowMinThrottleValue = new Integer(1);
	
	public ThrottleItemWindow(ThrottleModel model,boolean isNewAdd,ScheduleDetail parent ) {
		this.model = model;
		this.parentPanel = parent;
		this.setWidth(340);
		this.setResizable(false);
		this.ensureDebugId("25686133-5e9c-4258-9d2a-36b61316556e");
		if(isNewAdd)
			this.setHeadingHtml(parentPanel.getThrottleAddWindowHeader());
		else
			this.setHeadingHtml(parentPanel.getThrottleEditWindowHeader());
		
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
		addLine();
		addThrottlingRow();
		
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
	
	private void addLine(){
		TableData td = new TableData();
		td.setWidth("100%");
		td.setColspan(2);
		HTML html = new HTML("<hr>");
		add(html, td);
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
	
	private void addThrottlingRow() {
		LabelField throttleLabel = new LabelField(parentPanel.getThrottleSpeedLabel());
		
		LayoutContainer internalContainer = new LayoutContainer();
		TableLayout tableLayout = new TableLayout(2);
		internalContainer.setLayout(tableLayout);
		
		throttleField = new ErrorAlignedNumberField();
		throttleField.ensureDebugId("0d1e6551-8e0e-41c2-b430-b324946456ef");
		long throttleValue = model.getThrottleValue();
		if(throttleValue>0)
			throttleField.setValue((int)throttleValue);
		throttleField.setWidth(100);
		throttleField.setAllowBlank(false);
		throttleField.setMinValue(1);
		//throttleField.setEnabled(false);
		throttleField.setAllowDecimals(false);
		throttleField.setAllowNegative(false);
		throttleField.setMaxValue(allowMaxThrottleValue);
		throttleField.setMinValue(allowMinThrottleValue);
//		throttleField.setStyleAttribute("margin-right", "20px");
		internalContainer.add(throttleField);

		LabelField label = new LabelField();
		label.setStyleAttribute("margin-left", "5px");
		label.setValue(parentPanel.getThrottleUnit());
		internalContainer.add(label);
		
		addWidget(throttleLabel, internalContainer);
	}
	
	public ThrottleModel save() {
		model.startTimeModel = startTime.getTimeValue();
		model.endTimeModel = endTime.getTimeValue();
		model.setThrottleValue(throttleField.getValue().longValue());
		
		return model;
	}
	
	public boolean validate() {
		if(!startTime.validate())
			return false;
		if(!endTime.validate())
			return false;
		if(!throttleField.validate())
			return false;
		
		return true;
	}
	
	public ThrottleModel getCurrentModel() {
		ThrottleModel currentModel = new ThrottleModel();
		currentModel.startTimeModel = startTime.getTimeValue();
		currentModel.endTimeModel = endTime.getTimeValue();
		currentModel.setThrottleValue(throttleField.getValue().longValue());
		
		return currentModel;
	}
}
