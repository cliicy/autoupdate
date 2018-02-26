package com.ca.arcflash.ui.client.backup;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.schedule.AdvanceScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.EveryDayScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.EveryMonthScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.EveryWeekScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.FlashFieldSetModel;
import com.ca.arcflash.ui.client.backup.schedule.PeriodScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleUtils;
import com.ca.arcflash.ui.client.common.AdsTimeField;
import com.ca.arcflash.ui.client.common.BaseComboBox;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.ca.arcflash.ui.client.model.DayTimeModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.SpinnerField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.i18n.client.NumberFormat;

public class PeriodAdvancedScheduleSettings {
	
	private BackupSettingsModel model;
	// daily
	private CheckBox cbDailyBackup;
	private ComboBox<FlashFieldSetModel> comboDailybkpType;
	private SpinnerField txDailyRetain;
	private AdsTimeField dailyRunAt;
	private CheckBox cbGenerateCatalog4Daily;

	// weekly
	private CheckBox cbWeeklyBackup;
	private ComboBox<FlashFieldSetModel> comboWeeklybkpType;
	private AdsTimeField weeklyRunAt;
	private ComboBox<ModelData> comboWeeklyDayOfWeek;
	private SpinnerField txWeeklyRetain;
	private CheckBox cbGenerateCatalog4Weekly;

	// monthly
	private CheckBox cbMonthlyBackup;
	private ComboBox<FlashFieldSetModel> comboMonthlybkpType;
	private AdsTimeField monthlyRunAt;
	private RadioGroup rgDayOfMonth;
	private Radio rdDayOfMonth;
	private ComboBox<ModelData> comboDayOfMonth;
	private Radio rdWeekOfMonth;
	private ComboBox<ModelData> comboWeekNumOfMonth;
	private ComboBox<ModelData> comboWeekDayOfMonth;
	private SpinnerField txMonthlyRetain;
	private CheckBox cbGenerateCatalog4Monthly;
	
	private static final int[] defaultRetentionCount = new int[]{7, 5, 12};
	
	private final static String[] oneWeek = { UIContext.Constants.scheduleSunday(), UIContext.Constants.scheduleMonday(),
			UIContext.Constants.scheduleTuesday(), UIContext.Constants.scheduleWednesday(),
			UIContext.Constants.scheduleThursday(), UIContext.Constants.scheduleFriday(),
			UIContext.Constants.scheduleSaturday() };
	
	

	public PeriodAdvancedScheduleSettings(BackupSettingsContent parent) {	
	}

	public LayoutContainer Render() {
		LayoutContainer container = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(1);
		tl.setWidth("97%");
		container.setLayout(tl);

		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.scheduleMenuDailyWeeklyMonthly());
		label.addStyleName("restoreWizardTitle");
		container.add(label);
		container.add(getPeriodPanel());
		enableAll(false, false);
		return container;
	}

	private LayoutContainer getPeriodPanel() {
		LayoutContainer contentPanel = new LayoutContainer();
		contentPanel.setLayout(new RowLayout(Orientation.VERTICAL));
		contentPanel.add(getPeriodDailyPanel(), new RowData(1, -1));
		contentPanel.add(new Html("<HR>"));
		contentPanel.add(getPeriodWeeklyPanel(), new RowData(1, -1));
		contentPanel.add(new Html("<HR>"));
		contentPanel.add(getPeriodMonthlyPanel(), new RowData(1, -1));
		return contentPanel;
	}

	private LayoutContainer getPeriodDailyPanel() {
		LayoutContainer dailyContainer = new LayoutContainer();
		dailyContainer.setWidth("100%");
		TableLayout layout = new TableLayout();
		layout.setCellPadding(2);
		layout.setCellSpacing(2);
		layout.setColumns(4);
		layout.setWidth("100%");
		dailyContainer.setLayout(layout);

		// Row 1
		cbDailyBackup = new CheckBox();
		cbDailyBackup.setBoxLabel(UIContext.Constants.periodDailyBackup());
		cbDailyBackup.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				if (be.getValue() != null) {
					enableDaily((Boolean) be.getValue(), false);
				}
			}
		});
		TableData td = new TableData();
		td.setWidth("100%");
		td.setColspan(4);
		dailyContainer.add(cbDailyBackup, td);

		// Row 2
		LabelField bkpTypeLabel = new LabelField();
		bkpTypeLabel.setValue(UIContext.Constants.periodBackupType());
		td = new TableData();
		td.setWidth("15%");
		dailyContainer.add(bkpTypeLabel, td);

		comboDailybkpType = getBackupType();
		td = new TableData();
		td.setWidth("25%");
		dailyContainer.add(comboDailybkpType, td);

		LabelField lbRunAt = new LabelField(UIContext.Constants.scheduleStartAt());
		td = new TableData();
		td.setWidth("20%");
		td.setHorizontalAlign(HorizontalAlignment.RIGHT);
		dailyContainer.add(lbRunAt, td);

		dailyRunAt = new AdsTimeField("dailyRunAt");		
		dailyRunAt.setTimeValue(getRunAtTime());
		dailyRunAt.setEditable(false);
		dailyRunAt.setAllowBlank(false);
		dailyRunAt.setWidth(180);
		td = new TableData();
		td.setWidth("40%");
		dailyContainer.add(dailyRunAt, td);

		// row3
		LabelField lbDailyRetain = new LabelField(UIContext.Constants.periodDailyRetention());
		td = new TableData();
		td.setColspan(2);
		td.setWidth("70%");
		dailyContainer.add(lbDailyRetain, td);

		txDailyRetain = getSpinnerField(defaultRetentionCount[0], UIContext.DEFAULT_MAX_PERIOD_RETAIN_COUNT_DAILY);
		
		td = new TableData();
		td.setColspan(2);
		td.setHorizontalAlign(HorizontalAlignment.LEFT);
		td.setWidth("30%");
		dailyContainer.add(txDailyRetain, td);

		// row4
		cbGenerateCatalog4Daily = new CheckBox();
		cbGenerateCatalog4Daily.setBoxLabel(UIContext.Constants.periodDailyCatalog());
		td = new TableData();
		td.setWidth("100%");
		td.setColspan(4);
		dailyContainer.add(cbGenerateCatalog4Daily, td);

		return dailyContainer;
	}

	private DayTimeModel getRunAtTime() {
		DayTimeModel runAtTime = new DayTimeModel();
		runAtTime.setHour(20);
		runAtTime.setMinute(0);
		return runAtTime;
	}

	private SpinnerField getSpinnerField(int defaultValue, final int max) {
		SpinnerField spinnerfield = new SpinnerField();	
		spinnerfield.setValidator(new Validator() {
				@Override
				public String validate(Field<?> field, String value) {
					if(Integer.parseInt(value) > max){				
						return UIContext.Messages.settingsRetentionCountExceedMax(max);
					}
					return null;
				}
		});
		spinnerfield.setFormat(NumberFormat.getFormat("0"));
		//spinnerfield.setPropertyEditorType(Integer.class);
		spinnerfield.setMinValue(1);		
		spinnerfield.setValue(defaultValue);		
		return spinnerfield;
	}

	private LayoutContainer getPeriodWeeklyPanel() {
		LayoutContainer weeklyContainer = new LayoutContainer();
		weeklyContainer.setWidth("100%");
		TableLayout layout = new TableLayout();
		layout.setCellPadding(2);
		layout.setCellSpacing(2);
		layout.setColumns(5);
		layout.setWidth("100%");
		weeklyContainer.setLayout(layout);

		// Row 1
		cbWeeklyBackup = new CheckBox();
		cbWeeklyBackup.setBoxLabel(UIContext.Constants.periodWeeklyBackup());
		cbWeeklyBackup.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				if (be.getValue() != null) {
					enableWeekly((Boolean) be.getValue(), false);
				}
			}
		});
		TableData td = new TableData();
		td.setWidth("100%");
		td.setColspan(5);
		weeklyContainer.add(cbWeeklyBackup, td);

		// Row 2
		LabelField bkpTypeLabel = new LabelField();
		bkpTypeLabel.setValue(UIContext.Constants.periodBackupType());
		bkpTypeLabel.setStyleAttribute("white-space", "nowrap");
		td = new TableData();
		td.setWidth("15%");		
		weeklyContainer.add(bkpTypeLabel, td);

		comboWeeklybkpType = getBackupType();
		td = new TableData();
		td.setWidth("25%");
		weeklyContainer.add(comboWeeklybkpType, td);

		LabelField lbRunAt = new LabelField(UIContext.Constants.scheduleStartAt());
		td = new TableData();
		td.setWidth("20%");
		td.setHorizontalAlign(HorizontalAlignment.RIGHT);
		weeklyContainer.add(lbRunAt, td);

		weeklyRunAt = new AdsTimeField("weeklyRunAt");		
		weeklyRunAt.setTimeValue(getRunAtTime());
		weeklyRunAt.setEditable(false);
		weeklyRunAt.setAllowBlank(false);
		weeklyRunAt.setWidth(180);
		td = new TableData();
		td.setWidth("20%");
		weeklyContainer.add(weeklyRunAt, td);

		comboWeeklyDayOfWeek = getWeekCombox();
		td = new TableData();
		td.setWidth("20%");
		weeklyContainer.add(comboWeeklyDayOfWeek, td);

		// row3
		LabelField lbDailyRetain = new LabelField(UIContext.Constants.periodWeeklyRetention());
		td = new TableData();
		td.setColspan(2);
		td.setWidth("70%");
		weeklyContainer.add(lbDailyRetain, td);

		txWeeklyRetain = getSpinnerField(defaultRetentionCount[1],  UIContext.DEFAULT_MAX_PERIOD_RETAIN_COUNT_WEEKLY);
		td = new TableData();
		td.setColspan(3);
		td.setHorizontalAlign(HorizontalAlignment.LEFT);
		td.setWidth("30%");
		weeklyContainer.add(txWeeklyRetain, td);

		// row4
		cbGenerateCatalog4Weekly = new CheckBox();
		cbGenerateCatalog4Weekly.setBoxLabel(UIContext.Constants.periodWeeklyCatalog());
		td = new TableData();
		td.setWidth("100%");
		td.setColspan(5);
		weeklyContainer.add(cbGenerateCatalog4Weekly, td);

		return weeklyContainer;
	}

	private LayoutContainer getPeriodMonthlyPanel() {
		LayoutContainer monthlyContainer = new LayoutContainer();
		monthlyContainer.setWidth("100%");
		TableLayout layout = new TableLayout();
		layout.setCellPadding(2);
		layout.setCellSpacing(2);
		layout.setColumns(4);
		layout.setWidth("100%");
		monthlyContainer.setLayout(layout);

		// Row 1
		cbMonthlyBackup = new CheckBox();
		cbMonthlyBackup.setBoxLabel(UIContext.Constants.periodMonthlyBackup());
		cbMonthlyBackup.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				if (be.getValue() != null) {
					enableMonthly((Boolean) be.getValue(), false);
				}
			}

		});
		TableData td = new TableData();
		td.setWidth("100%");
		td.setColspan(4);
		monthlyContainer.add(cbMonthlyBackup, td);

		// Row 2
		LabelField bkpTypeLabel = new LabelField();
		bkpTypeLabel.setValue(UIContext.Constants.periodBackupType());
		td = new TableData();
		td.setWidth("15%");
		monthlyContainer.add(bkpTypeLabel, td);

		comboMonthlybkpType = getBackupType();
		td = new TableData();
		td.setWidth("25%");
		monthlyContainer.add(comboMonthlybkpType, td);

		LabelField lbRunAt = new LabelField(UIContext.Constants.scheduleStartAt());
		td = new TableData();
		td.setWidth("20%");
		td.setHorizontalAlign(HorizontalAlignment.RIGHT);
		monthlyContainer.add(lbRunAt, td);

		monthlyRunAt = new AdsTimeField("monthlyRunAt");		
		monthlyRunAt.setTimeValue(getRunAtTime());
		monthlyRunAt.setEditable(false);
		monthlyRunAt.setAllowBlank(false);
		monthlyRunAt.setWidth(180);
		td = new TableData();
		td.setWidth("40%");
		monthlyContainer.add(monthlyRunAt, td);

		// Row 3
		rgDayOfMonth = new RadioGroup();

		rgDayOfMonth.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				Boolean monthlyCheckedValue = cbMonthlyBackup.getValue(); 
				if(!cbMonthlyBackup.isEnabled() || monthlyCheckedValue == null || !monthlyCheckedValue) return;
				
				if (rgDayOfMonth.getValue() == rdDayOfMonth) {
					comboDayOfMonth.setEnabled(true);
					comboWeekDayOfMonth.setEnabled(false);
					comboWeekNumOfMonth.setEnabled(false);
				} else {
					comboDayOfMonth.setEnabled(false);
					comboWeekDayOfMonth.setEnabled(true);
					comboWeekNumOfMonth.setEnabled(true);
				}
			}
		});
		rdDayOfMonth = new Radio();
		rdDayOfMonth.setValue(true);
		rdDayOfMonth.setBoxLabel(UIContext.Constants.Day());
		td = new TableData();
		td.setWidth("40%");
		td.setColspan(2);

		monthlyContainer.add(rdDayOfMonth, td);
		rgDayOfMonth.add(rdDayOfMonth);

		comboDayOfMonth = new BaseComboBox<ModelData>();
		comboDayOfMonth.setEditable(false);
		comboDayOfMonth.setValueField("value");
		ListStore<ModelData> store = new ListStore<ModelData>();		
		ModelData defaultmd = null;
		for (int i = 1; i < 33; i += 1) {
			ModelData md = new BaseModelData();
			md.set("value", i);
			if (i == 1)
				defaultmd = md;
			if (i == 32) {
				md.set("text", UIContext.Constants.selectLastDayOfMonth());
			} else
				md.set("text", i + "");

			store.add(md);
		}
		comboDayOfMonth.setStore(store);
		comboDayOfMonth.setValue(defaultmd);
		Utils.addToolTip(comboDayOfMonth, UIContext.Constants.selectDayofMonthTooltip());
		//comboDayOfMonth.setWidth(120);
		//comboDayOfMonth.setStyleAttribute("margin-left", "5px");
		// comboDayOfMonth.setEnabled(false);
		td = new TableData();
		td.setWidth("60%");
		td.setColspan(2);
		monthlyContainer.add(comboDayOfMonth, td);

		// Row 4
		rdWeekOfMonth = new Radio();
		rdWeekOfMonth.setBoxLabel(UIContext.Constants.Week());
		td = new TableData();
		td.setWidth("40%");
		td.setColspan(2);
		monthlyContainer.add(rdWeekOfMonth, td);
		rgDayOfMonth.add(rdWeekOfMonth);

		comboWeekNumOfMonth = getWeekNumCombox();
		comboWeekNumOfMonth.setEnabled(false);
		td = new TableData();
		td.setWidth("10%");		
		monthlyContainer.add(comboWeekNumOfMonth, td);
		
		comboWeekDayOfMonth = getWeekCombox();
		comboWeekDayOfMonth.setEnabled(false);
		td = new TableData();
		td.setWidth("30%");		
		monthlyContainer.add(comboWeekDayOfMonth, td);

		// row 5
		LabelField lbDailyRetain = new LabelField(UIContext.Constants.periodMonthlyRetention());
		td = new TableData();
		td.setColspan(2);
		td.setWidth("70%");
		monthlyContainer.add(lbDailyRetain, td);

		txMonthlyRetain =  getSpinnerField(defaultRetentionCount[2],  UIContext.DEFAULT_MAX_PERIOD_RETAIN_COUNT_MONTHLY);		
		td = new TableData();
		td.setColspan(2);
		td.setHorizontalAlign(HorizontalAlignment.LEFT);
		td.setWidth("30%");
		monthlyContainer.add(txMonthlyRetain, td);

		// row 6
		cbGenerateCatalog4Monthly = new CheckBox();
		cbGenerateCatalog4Monthly.setBoxLabel(UIContext.Constants.periodMonthlyCatalog());
		td = new TableData();
		td.setWidth("100%");
		td.setColspan(4);
		monthlyContainer.add(cbGenerateCatalog4Monthly, td);

		return monthlyContainer;
	}

	private ComboBox<ModelData> getWeekCombox() {
		ListStore<ModelData> weekStore = new ListStore<ModelData>();
		BaseComboBox<ModelData> cbWeek = new BaseComboBox<ModelData>();
		cbWeek.setDisplayField("text");
		cbWeek.setValueField("dayOfWeek");
		cbWeek.setStore(weekStore);
		cbWeek.setEditable(false);
		cbWeek.setAllowBlank(false);
		// cbWeek.setWidth(100);		

		ModelData selectModel = null;
		for (int i = 0; i < oneWeek.length; i++) {
			ModelData md = new BaseModelData();
			md.set("text", oneWeek[i]);
			md.set("dayOfWeek", i + 1);
			weekStore.add(md);

			if (UIContext.Constants.scheduleFriday().equals(oneWeek[i])) {
				selectModel = md;
			}
		}
		cbWeek.setValue(selectModel);
		return cbWeek;
	}
	
	private ComboBox<ModelData> getWeekNumCombox() {
		ListStore<ModelData> weekStore = new ListStore<ModelData>();
		BaseComboBox<ModelData> comboWeekNum = new BaseComboBox<ModelData>();
		comboWeekNum.setDisplayField("text");
		comboWeekNum.setValueField("value");
		comboWeekNum.setStore(weekStore);
		comboWeekNum.setEditable(false);
		comboWeekNum.setAllowBlank(false);
		// cbWeek.setWidth(100);

		String[] weekNum = {UIContext.Constants.Last(), UIContext.Constants.First()};		

		ModelData selectModel = null;
		for (int i = 0; i < weekNum.length; i++) {
			ModelData md = new BaseModelData();
			md.set("text", weekNum[i]);
			md.set("value", i);
			weekStore.add(md);

			if (selectModel == null) {
				selectModel = md;
			}
		}
		comboWeekNum.setValue(selectModel);
		return comboWeekNum;
	}

	private ComboBox<FlashFieldSetModel> getBackupType() {
		BaseComboBox<FlashFieldSetModel> bkpType = new BaseComboBox<FlashFieldSetModel>();
		bkpType.setDisplayField("text");
		bkpType.setValueField("value");
		bkpType.setStore(ScheduleUtils.getBackupTypeModels());
		bkpType.setEditable(false);
		bkpType.setAllowBlank(false);
		bkpType.setWidth(120);
		setBkpType(bkpType, -1);
		return bkpType;
	}

	private FlashFieldSetModel getBkpType(ComboBox<FlashFieldSetModel> bkpType, int selbkpType, boolean retDefault) {
		FlashFieldSetModel selectModel = null;
		for (FlashFieldSetModel flashFieldSetModel : bkpType.getStore().getModels()) {
			if (selectModel == null && retDefault && flashFieldSetModel.getValue() ==1) {// Default is 1: Incremental.( 0 : full, Resync :2.)
				selectModel = flashFieldSetModel;
			} else if (flashFieldSetModel.getValue() == selbkpType) {
				selectModel = flashFieldSetModel;
				break;
			}
		}

		return selectModel;
	}

	private void setBkpType(ComboBox<FlashFieldSetModel> bkpType, int selbkpType) {
		bkpType.setValue(getBkpType(bkpType, selbkpType, true));
	}

	public void RefreshData(BackupSettingsModel model, boolean isEdit) {
		if(model == null) return;
		
		this.model = model;
		AdvanceScheduleModel scheduleModel = model.advanceScheduleModel;
		if (scheduleModel == null) {
			scheduleModel = new AdvanceScheduleModel();
			model.advanceScheduleModel = scheduleModel;
		}
		
		if(model.advanceScheduleModel.periodScheduleModel == null){
			model.advanceScheduleModel.periodScheduleModel = new PeriodScheduleModel();
		}
		
		if(isEdit)
			updateData(model.advanceScheduleModel.periodScheduleModel);
	}
	
	private void enableAll(boolean enabled, boolean includeItself) {
		enableDaily(enabled, includeItself);
		enableWeekly(enabled, includeItself);
		enableMonthly(enabled, includeItself);
	}

	private void enableDaily(boolean enabled, boolean includeItself) {
		if (includeItself) {
			cbDailyBackup.setEnabled(enabled);
		}
		comboDailybkpType.setEnabled(enabled);
		txDailyRetain.setEnabled(enabled);
		dailyRunAt.setEnabled(enabled);
		cbGenerateCatalog4Daily.setEnabled(enabled);
	}

	private void enableWeekly(boolean enabled, boolean includeItself) {
		if (includeItself) {
			cbWeeklyBackup.setEnabled(enabled);
		}
		comboWeeklybkpType.setEnabled(enabled);
		weeklyRunAt.setEnabled(enabled);
		comboWeeklyDayOfWeek.setEnabled(enabled);
		txWeeklyRetain.setEnabled(enabled);
		cbGenerateCatalog4Weekly.setEnabled(enabled);
	}

	private void enableMonthly(boolean enabled, boolean includeItself) {
		if (includeItself) {
			cbMonthlyBackup.setEnabled(enabled);
		}

		comboMonthlybkpType.setEnabled(enabled);
		monthlyRunAt.setEnabled(enabled);

		rgDayOfMonth.setEnabled(enabled);

		rdDayOfMonth.setEnabled(enabled);
		comboDayOfMonth.setEnabled(enabled);

		rdWeekOfMonth.setEnabled(enabled);
		comboWeekDayOfMonth.setEnabled(enabled);
		comboWeekNumOfMonth.setEnabled(enabled);

		if (enabled) {
			if (rgDayOfMonth.getValue() == rdDayOfMonth){
				comboWeekDayOfMonth.setEnabled(false);
				comboWeekNumOfMonth.setEnabled(false);
			}else{
				comboDayOfMonth.setEnabled(false);
			}
		}

		txMonthlyRetain.setEnabled(enabled);
		cbGenerateCatalog4Monthly.setEnabled(enabled);
	}

	private EveryDayScheduleModel getDailyData() {
		boolean dailyBackupEnabled = cbDailyBackup.getValue();
		int retentionCount = txDailyRetain.getValue().intValue();
		boolean generateCatalog = cbGenerateCatalog4Daily.getValue();
		DayTimeModel dm = dailyRunAt.getTimeValue();
		int bkpType = comboDailybkpType.getValue().getValue();

		EveryDayScheduleModel daily = new EveryDayScheduleModel();
		daily.setEnabled(dailyBackupEnabled);
		daily.setBkpType(bkpType);
		daily.setDayTime(dm);
		daily.setRetentionCount(retentionCount);
		daily.setGenerateCatalog(generateCatalog);
		return daily;
	}

	private EveryWeekScheduleModel getWeeklyData() {
		boolean weeklyBackupEnabled = cbWeeklyBackup.getValue();
		int bkpType = comboWeeklybkpType.getValue().getValue();
		DayTimeModel dm = weeklyRunAt.getTimeValue();
		int dayOfWeek = comboWeeklyDayOfWeek.getValue().<Integer> get(comboWeeklyDayOfWeek.getValueField());
		int retentionCount = txWeeklyRetain.getValue().intValue();
		boolean generateCatalog = cbGenerateCatalog4Weekly.getValue();

		EveryWeekScheduleModel weekly = new EveryWeekScheduleModel();
		weekly.setEnabled(weeklyBackupEnabled);
		weekly.setBkpType(bkpType);
		weekly.setDayOfWeek(dayOfWeek);
		weekly.setRetentionCount(retentionCount);
		weekly.setGenerateCatalog(generateCatalog);
		weekly.setDayTime(dm);
		return weekly;
	}

	private EveryMonthScheduleModel getMonthlyData() {
		boolean monthlyBackupEnabled = cbMonthlyBackup.getValue();
		int bkpType = comboMonthlybkpType.getValue().getValue();
		DayTimeModel dm = monthlyRunAt.getTimeValue();
		boolean dayOfMonthEnabled = false;
		boolean weekOfMonthEnabled = false;

		if (rgDayOfMonth.getValue() == rdDayOfMonth)
			dayOfMonthEnabled = true;
		else
			weekOfMonthEnabled = true;

		int weekDayOfMonth = comboWeekDayOfMonth.getValue().<Integer> get(comboWeekDayOfMonth.getValueField());
		int weekNumOfMonth = comboWeekNumOfMonth.getValue().<Integer> get(comboWeekNumOfMonth.getValueField());
		int dayOfMonth = comboDayOfMonth.getValue().<Integer> get(comboDayOfMonth.getValueField());
		int retentionCount = txMonthlyRetain.getValue().intValue();
		boolean generateCatalog = cbGenerateCatalog4Monthly.getValue();

		EveryMonthScheduleModel monthly = new EveryMonthScheduleModel();
		monthly.setWeekNumOfMonth(weekNumOfMonth);
		monthly.setWeekDayOfMonth(weekDayOfMonth);
		monthly.setDayOfMonth(dayOfMonth);
		monthly.setRetentionCount(retentionCount);
		monthly.setGenerateCatalog(generateCatalog);
		monthly.setDayOfMonthEnabled(dayOfMonthEnabled);
		monthly.setWeekOfMonthEnabled(weekOfMonthEnabled);
		monthly.setBkpType(bkpType);
		monthly.setEnabled(monthlyBackupEnabled);
		monthly.setGenerateCatalog(generateCatalog);
		monthly.setDayTime(dm);
		return monthly;
	}

	private void updateData(PeriodScheduleModel periodSchedule) {
		updateDailyData(periodSchedule.dayScheduleModel);
		updateWeeklyData(periodSchedule.weekScheduleModel);
		updateMonthlyData(periodSchedule.monthScheduleModel);		
	}
	
	private void updateDailyData(EveryDayScheduleModel daySchedule) {
		if(daySchedule == null) return;
		
		cbDailyBackup.setValue(daySchedule.isEnabled());		
		this.setBkpType(comboDailybkpType, daySchedule.getBkpType());
		dailyRunAt.setTimeValue(daySchedule.getDayTime());
		txDailyRetain.setValue(daySchedule.getRetentionCount());
		cbGenerateCatalog4Daily.setValue(daySchedule.isGenerateCatalog());
	}	
	
	private void updateWeeklyData(EveryWeekScheduleModel weekSchedule) {
		if(weekSchedule == null) return;
		
		cbWeeklyBackup.setValue(weekSchedule.isEnabled());		
		this.setBkpType(comboWeeklybkpType, weekSchedule.getBkpType());
		weeklyRunAt.setTimeValue(weekSchedule.getDayTime());
		txWeeklyRetain.setValue(weekSchedule.getRetentionCount());
		cbGenerateCatalog4Weekly.setValue(weekSchedule.isGenerateCatalog());		
		this.setDayOfWeek(weekSchedule.getDayOfWeek(), comboWeeklyDayOfWeek);	
	}

	private void updateMonthlyData(EveryMonthScheduleModel monthSchedule) {
		if(monthSchedule == null) return;
		
		cbMonthlyBackup.setValue(monthSchedule.isEnabled());		
		this.setBkpType(comboMonthlybkpType, monthSchedule.getBkpType());
		monthlyRunAt.setTimeValue(monthSchedule.getDayTime());		
		
		boolean dayOfMonthEnabled = monthSchedule.isDayOfMonthEnabled();
		
		if(dayOfMonthEnabled){
			rgDayOfMonth.setValue(rdDayOfMonth);			
			// day of month
			comboDayOfMonth.setValue(this.intToModelData(monthSchedule.getDayOfMonth(), comboDayOfMonth));	
			
		}else{
			rgDayOfMonth.setValue(rdWeekOfMonth);
			
			// # of week,  the weekday
			comboWeekNumOfMonth.setValue(this.intToModelData(monthSchedule.getWeekNumOfMonth(), comboWeekNumOfMonth));	
			this.setDayOfWeek(monthSchedule.getWeekDayOfMonth(), comboWeekDayOfMonth);					
		}		
		
		txMonthlyRetain.setValue(monthSchedule.getRetentionCount());
		cbGenerateCatalog4Monthly.setValue(monthSchedule.isGenerateCatalog());		
	}

	private void setDayOfWeek(int seldayOfWeek, ComboBox<ModelData> comboWeeklyDayOfWeek) {
		if(seldayOfWeek > 0){
			comboWeeklyDayOfWeek.setValue(intToModelData(seldayOfWeek, comboWeeklyDayOfWeek));
		}		
	}

	private ModelData intToModelData(int dayOfweek, ComboBox<ModelData> comboWeeklyDayOfWeek) {
		ListStore<ModelData> listStore = comboWeeklyDayOfWeek.getStore();		 
		ModelData selmd = null;
		for (int i = 0; i <listStore.getCount(); i++) {
			ModelData md = listStore.getAt(i);
			if (dayOfweek == md.<Integer>get(comboWeeklyDayOfWeek.getValueField())) {
				selmd = md;
			}
		}
		return selmd;		
	}

	public boolean Validate() {
		if(!txDailyRetain.validate()||!txWeeklyRetain.validate() || !txMonthlyRetain.validate()){
			return false;
		}		
		return true;
	}

	public void Save(long result) {		
		PeriodScheduleModel periodSchedule = new PeriodScheduleModel();
		periodSchedule.dayScheduleModel = this.getDailyData();
		periodSchedule.weekScheduleModel = this.getWeeklyData();
		periodSchedule.monthScheduleModel = this.getMonthlyData();
		model.advanceScheduleModel.periodScheduleModel = periodSchedule;
	}

	public void setEditable(boolean isEnabled) {
		this.enableAll(isEnabled, true);
		
	}
}
