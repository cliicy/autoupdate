package com.ca.arcflash.ui.client.backup;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseComboBox;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.BackupScheduleModel;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTip;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;

public class ScheduleSubSettings {
	
	private LayoutContainer container;
	private LayoutContainer rowContainer;

	private RadioGroup rg;
	private Radio repeat;
	private Radio never;

	private BaseComboBox<ScheduleInfo> repeatCombo;
	private NumberField repeatTextField;
	//private TimeField onceTime;
	//private DateField onceDate;	
	private LabelField titleLabel;
	private LabelField descriptionLabel;
	
	private String name;
	private String title;
	private String description;
	
	private ScheduleSubSettings thisPanel;
	
	public static final int Minute_Unit 	= 0;
	public static final int Hour_Unit 		= 1;
	public static final int Day_Unit	 	= 2;
	
	private ScheduleInfo day_info;
	private ScheduleInfo hour_info;
	private ScheduleInfo min_info;
	
	//debug id, add default value here, user must set new id for them to avoid duplicate id problem
	private String repeatID = "1DE36526-9D7B-4fcb-9238-12727F206F19";
	private String repeatComboID = "5369FCE1-6319-494d-8503-A9446FC80FA5";
	private String repeatTextFieldID = "78AF1499-F875-4e78-949C-7C8C1443C2BD";
	private String neverID = "4DC03447-A8E9-425f-84D9-E3D31A65C243";
	
	public enum BkpType {
		NA, INC, FULL;
	}

	public BkpType bkpType = BkpType.NA;

	public ScheduleSubSettings()
	{
		thisPanel = this;
	}
	public ScheduleSubSettings(String name, String title, String description)
	{
		this.name = name;
		this.title = title;
		this.description = description;
		thisPanel = this;
	}
	
	public LayoutContainer Render()
	{	
		ToolTipConfig tipConfig = null;
		ToolTip tip = null;

//		RowLayout rl = new RowLayout();
		rowContainer = new LayoutContainer();		
//		rowContainer.setLayout(rl);
		TableLayout tl = new TableLayout();
		tl.setWidth("98%");
		tl.setColumns(1);
		rowContainer.setLayout(tl);
				
//		if (title != null)
//		{
//			titleLabel = new LabelField();
//			titleLabel.setText(title);
//			titleLabel.addStyleName("restoreWizardSubItem");
//			rowContainer.add(titleLabel);
//		}
		if (description != null)
		{
			descriptionLabel = new LabelField();
			descriptionLabel.setValue(description);			
			rowContainer.add(descriptionLabel);
		}		
		
		container = new LayoutContainer();
		rg = new RadioGroup();
		
		repeat = new Radio();
		repeat.ensureDebugId(repeatID);
		repeat.setBoxLabel(UIContext.Constants.scheduleLabelRepeat());
		repeat.addStyleName("x-form-field");
		
		LayoutContainer repeatRadioContainer = new LayoutContainer();
		repeatRadioContainer.add(repeat);
		
		// Tool tip
		tipConfig = new ToolTipConfig(UIContext.Messages.scheduleLabelRepeatTooltip(title));
		tip = new ToolTip(repeatRadioContainer, tipConfig);
		tip.setHeaderVisible(false);
	
		
		repeat.addListener(Events.Change, new Listener<FieldEvent>()
		{

			@Override
			public void handleEvent(FieldEvent be) {
				if(repeat.getValue()) {
					repeatCombo.setEnabled(true);
					repeatTextField.setEnabled(true);	
				}
				else {
					repeatCombo.setEnabled(false);
					repeatTextField.setEnabled(false);	
				}
				thisPanel.container.repaint();
			}
			
		});			
		rg.add(repeat);
		
		/*
		once = new Radio();
		once.setBoxLabel(UIContext.Constants.scheduleLabelOnce());
		once.addListener(Events.Change, new Listener<FieldEvent>(){
			public void handleEvent(FieldEvent be)
			{
				onceTime.setEnabled(true);
				onceDate.setEnabled(true);
				
				repeatCombo.setEnabled(false);
				repeatTextField.setEnabled(false);
			}
		});
		*/
		
		
		never = new Radio();
		never.ensureDebugId(neverID);
		never.setBoxLabel(UIContext.Constants.scheduleLabelNever());
		never.addStyleName("x-form-field");
		
		LayoutContainer neverRadioContainer = new LayoutContainer();
		neverRadioContainer.add(never);
		
		// Tool tip
		tipConfig = new ToolTipConfig(UIContext.Messages.scheduleLabelNeverTooltip(title));
		tip = new ToolTip(neverRadioContainer, tipConfig);
		tip.setHeaderVisible(false);
		
		never.addListener(Events.Change, new Listener<FieldEvent>()
		{

			@Override
			public void handleEvent(FieldEvent be) {
				if(never.getValue()) {
					repeatCombo.setEnabled(false);
					repeatTextField.setEnabled(false);	
					
					// To fix (18774285)
					if (repeatCombo.getValue() == min_info) {
						repeatTextField.clearInvalid();
						repeatTextField.setValue(15);
						repeatTextField.setMinValue(15);
					}
				}
				else {
					repeatCombo.setEnabled(true);
					repeatTextField.setEnabled(true);	
				}
				thisPanel.container.repaint();
			}			
			
		});
		rg.add(never);
		
		TableLayout tableLayout = new TableLayout();
		tableLayout.setWidth("90%");
		tableLayout.setCellPadding(1);
		tableLayout.setCellSpacing(1);
		tableLayout.setColumns(4);
		
		//Repeat Section
		TableData td = new TableData();
		td.setWidth("40%");				
		container.setLayout(tableLayout);		
		container.add(repeatRadioContainer, td);
		
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.scheduleLabelEvery());	
		td = new TableData();
		td.setWidth("20%");
		container.add(label, td);
		
		repeatTextField = new NumberField();
		repeatTextField.ensureDebugId(repeatTextFieldID);
		repeatTextField.setAllowDecimals(false);
		repeatTextField.setAllowNegative(false);
		repeatTextField.setAllowBlank(false);
		repeatTextField.setWidth(95);	
		
		td = new TableData();
		td.setWidth("25%");
		container.add(repeatTextField, td);
		
		td = new TableData();
		td.setWidth("15%");

		repeatCombo = new BaseComboBox<ScheduleInfo>();
		repeatCombo.ensureDebugId(repeatComboID);
		repeatCombo.setDisplayField(ScheduleInfo.NAMEFIELD);
		repeatCombo.setEditable(false);		
		repeatCombo.setStore(CreateScheduleInfo());
		repeatCombo.setWidth(132);
		repeatCombo
				.addSelectionChangedListener(new SelectionChangedListener<ScheduleInfo>() {
					@Override
					public void selectionChanged(
							SelectionChangedEvent<ScheduleInfo> se) {
						if (!isRefreshing) {
							if (repeatCombo.getValue() == min_info) {
								repeatTextField.setMinValue(15);	
								if(!Validate(false))
									repeatTextField.setValue(15);
									
									
							} else {
								repeatTextField.setMinValue(1);
								if(!Validate(false))
									repeatTextField.setValue(1);
								
							}
							repeatTextField.validate();
						}
					}
				});
		container.add(repeatCombo, td);

		// Never Section
		container.add(neverRadioContainer);

		rowContainer.add(container);
		return rowContainer;
	}
	
	public ListStore<ScheduleInfo> CreateScheduleInfo()
	{
		ListStore<ScheduleInfo> scheduleListStore = new ListStore<ScheduleInfo>();
		
		day_info = new ScheduleInfo();
		day_info.setName(UIContext.Constants.scheduleLabelDays());
		day_info.setIntervalUnit(Day_Unit);
		scheduleListStore.add(day_info);
		
		hour_info = new ScheduleInfo();
		hour_info.setName(UIContext.Constants.scheduleLabelHours());
		hour_info.setIntervalUnit(Hour_Unit);
		scheduleListStore.add(hour_info);
		
		min_info = new ScheduleInfo();
		min_info.setName(UIContext.Constants.scheduleLabelMinutes());
		min_info.setIntervalUnit(Minute_Unit);
		scheduleListStore.add(min_info);
		
		return scheduleListStore;
	}
	
	public BackupScheduleModel Save()
	{
		BackupScheduleModel model = new BackupScheduleModel();
		
		
		if (never.getValue())
		{
			model.setEnabled(false);
		}
		else
		{
			model.setEnabled(true);
			if (repeatTextField != null)
			{
				try
				{
					Number i = repeatTextField.getValue();
					Integer repeatVal = i.intValue();
					model.setInterval(repeatVal);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				Integer unit = repeatCombo.getValue().getIntervalUnit();
				
				if (unit != null)
				{				
					model.setIntervalUnit(unit);
				}
			}
		}
		
		
		return model;
	}
	
	class ScheduleInfo extends BaseModelData
	{
		private static final long serialVersionUID = -1190005990510526623L;
		public static final String NAMEFIELD = "Name";
		public static final String INTERVALUNITFIELD = "IntervalUnit";
		
		public String getName() {
			return get(NAMEFIELD);
		}
		public void setName(String name) {
			set(NAMEFIELD, name);
		}
		public Integer getIntervalUnit()
		{
			return get(INTERVALUNITFIELD);
		}
		public void setIntervalUnit(Integer unit)
		{
			set(INTERVALUNITFIELD, unit);
		}
	}

	private boolean isRefreshing = false;

	public void RefreshData(BackupScheduleModel model) {
		isRefreshing = true;
		if (model != null) {

			if (model.isEnabled()) {
				repeat.setValue(true);
				repeatTextField.setValue(model.getInterval());
				repeatTextField.setMinValue(1);
				Integer unit = model.getIntervalUnit();
				if (unit == null) {
					unit = 0;
				}

				switch (unit) {
				case Day_Unit:
					repeatCombo.setValue(day_info);
					break;
				case Hour_Unit:
					repeatCombo.setValue(hour_info);
					break;
				case Minute_Unit:
					repeatCombo.setValue(min_info);
					repeatTextField.setMinValue(15);
					break;
				}
			} else {
				never.setValue(true);
				repeatTextField.setValue(1);
				repeatCombo.setValue(day_info);
			}
		} else {
			never.setValue(true);
			repeatTextField.setValue(1);
			repeatTextField.setMinValue(1);
			repeatCombo.setValue(day_info);
		}
		isRefreshing = false;
	}

	private boolean Validate(boolean isShowMessage){
		boolean showError = false;
		String msgString = null;
		if (repeat.getValue()) {
			boolean isValid = repeatTextField.validate();
			if (!isValid)
				return false;

			Number n = repeatTextField.getValue();
			int value = n.intValue();
			if (UIContext.hasBLILic) {
				if (repeatCombo.getValue() == day_info && value > 365) {
					showError = true;
					msgString = UIContext.Constants
							.backupSettingsErrorDaysTooLarge();
					repeatTextField.setValue(365);
				} else if (repeatCombo.getValue() == hour_info && value > 8760) {
					showError = true;
					msgString = UIContext.Constants
							.backupSettingsErrorHoursTooLarge();
					repeatTextField.setValue(8760);
				} else if (repeatCombo.getValue() == min_info && value > 525600) {
					showError = true;
					msgString = UIContext.Constants
							.backupSettingsErrorMinutesTooLarge();
					repeatTextField.setValue(525600);
				} else if (value < 1) {
					showError = true;
					msgString = UIContext.Constants
							.backupSettingsErrorScheduleTooSmall();
					repeatTextField.setValue(1);
				}
			} else {
				if (bkpType == BkpType.FULL) {
					// <=7 days.
					if (repeatCombo.getValue() == day_info && value > 7) {
						showError = true;
						msgString = UIContext.Messages
								.backupSettingsErrorDaysTooLargeForNoLic(7);
						repeatTextField.setValue(7);
					} else if (repeatCombo.getValue() == hour_info
							&& value > 7 * 24) {
						showError = true;
						msgString = UIContext.Messages
								.backupSettingsErrorHoursTooLargeForNoLic(7 * 24);
						repeatTextField.setValue(7 * 24);
					} else if (repeatCombo.getValue() == min_info
							&& value > 7 * 24 * 60) {
						showError = true;
						msgString = UIContext.Messages
								.backupSettingsErrorMinutesTooLargeForNoLic(7 * 24 * 60);
						repeatTextField.setValue(7 * 24 * 60);
					} else if (value < 1) {
						showError = true;
						msgString = UIContext.Constants
								.backupSettingsErrorScheduleTooSmall();
						repeatTextField.setValue(1);
					}
				} else if (bkpType == BkpType.INC) {
					// >= 1 hour.
					if (repeatCombo.getValue() == day_info && value > 365) {
						showError = true;
						msgString = UIContext.Constants
								.backupSettingsErrorDaysTooLarge();
						repeatTextField.setValue(365);
					} else if (repeatCombo.getValue() == hour_info
							&& value > 8760) {
						showError = true;
						msgString = UIContext.Constants
								.backupSettingsErrorHoursTooLarge();
						repeatTextField.setValue(8760);
					} else if (repeatCombo.getValue() == min_info
							&& value > 525600) {
						showError = true;
						msgString = UIContext.Constants
								.backupSettingsErrorMinutesTooLarge();
						repeatTextField.setValue(525600);
					} else {
						if (value < 1) {
							showError = true;
							msgString = UIContext.Constants
									.backupSettingsErrorScheduleTooSmall();
							repeatTextField.setValue(1);
						} else if (repeatCombo.getValue() == min_info
								&& value < 60) {
							showError = true;
							msgString = UIContext.Messages
									.backupSettingsErrorMinutesTooSmallForNoLic(60);
							repeatTextField.setValue(60);
						}
					}
				}
			}

			if (showError && isShowMessage) {
				MessageBox msg = new MessageBox();
				msg.setIcon(MessageBox.ERROR);				
				msg.setTitleHtml(Utils.getProductName());
				msg.setMessage(msgString);
				msg.setModal(true);
				Utils.setMessageBoxDebugId(msg);
				msg.show();
				return false;
			}
		}

		return true;
	}
	
	public boolean Validate() {
		return Validate(true);
	}
	
	public void setEditable(boolean isEditable){
		repeat.setEnabled(isEditable);
		never.setEnabled(isEditable);
		repeatCombo.setEnabled(isEditable);
		repeatTextField.setEnabled(isEditable);
	}
	
	public void setDebugID(String never, String repeat, String repCom, String repTxt){
		neverID = never;
		repeatID = repeat;
		repeatComboID = repCom;
		repeatTextFieldID = repTxt;
	}
}
