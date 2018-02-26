package com.ca.arcflash.ui.client.common.filecopy;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleUtils;
import com.ca.arcflash.ui.client.common.AdvScheduleUtil;
import com.ca.arcflash.ui.client.common.BaseComboBox;
import com.ca.arcflash.ui.client.common.FlashTimeField;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.DayTimeModel;
import com.ca.arcflash.webservice.data.AdvanceSchedule;
import com.ca.arcflash.webservice.data.DailyScheduleDetailItem;
import com.ca.arcflash.webservice.data.DayTime;
import com.ca.arcflash.webservice.data.EveryDaySchedule;
import com.ca.arcflash.webservice.data.EveryMonthSchedule;
import com.ca.arcflash.webservice.data.PeriodSchedule;
import com.ca.arcflash.webservice.data.ScheduleDetailItem;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.Time;
import com.extjs.gxt.ui.client.widget.layout.FlowData;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class FileCopySchedulePanel extends LayoutContainer {

	private static int FILE_COPY_JOB = 1;
	private RadioGroup rgDayOfMonth;
	private Radio rdDayOfMonth;
	private BaseComboBox<ModelData> comboDayOfMonth;
	private Radio rdWeekOfMonth;
	private ComboBox<ModelData> comboWeekNumOfMonth;
	private ComboBox<ModelData> comboWeekDayOfMonth;

	private LayoutContainer childContainer;

	private Radio copyAfterBackupRadio;
	private Radio copyOnScheduleRadio;
	//	private IntegerField throttlingBox;
	//	private CheckBox limitThrottlingCB;
	private FlashTimeField scheduleStartTimeField;
	private Radio weekDayRadio;
	private Radio monthlyRadio;
	private FlashTimeField scheduleEndTimeField;
	private ScheduleDaySelectionPanel weekDayWidget;
	private boolean isArchiveTask;

	public FileCopySchedulePanel(boolean isArchiveTask) {
		this.isArchiveTask = isArchiveTask;


		childContainer = new LayoutContainer();
		childContainer.ensureDebugId("adf56b63-9244-4976-b2a7-66fda4e0b3eb");

		copyAfterBackupRadio = new Radio();
		copyAfterBackupRadio.ensureDebugId("aee3e259-9e61-43a6-9b85-39634e99ca00");
		copyAfterBackupRadio.setBoxLabel(UIContext.Constants.copyImmediately());
		copyAfterBackupRadio.setItemId("copy_after_backup");
		copyAfterBackupRadio.setFireChangeEventOnSetValue(true);

		copyOnScheduleRadio = new Radio();
		copyOnScheduleRadio.setItemId("copy_on_schedule");
		copyOnScheduleRadio.ensureDebugId("7e670c08-0c67-48b7-96f5-5be92b9e1f69");
		copyOnScheduleRadio.setBoxLabel(UIContext.Constants.copyOnSchedule());
		copyOnScheduleRadio.setFireChangeEventOnSetValue(true);

		scheduleStartTimeField = new FlashTimeField(-1, -1, "","", "");
		scheduleStartTimeField.setDebugId("fe743b9a-160c-4a47-a317-ed3b719fb617",
				"a586398a-d345-4674-8021-affa40139583", "da1a5605-9bc0-475b-b707-4de11924b9ad");
		
		if(isArchiveTask){
			scheduleStartTimeField.setValue(new Time(6, 0));
		}else{
			scheduleStartTimeField.setValue(new Time(0, 0));
		}
		
		weekDayRadio = new Radio();
		monthlyRadio = new Radio();
		
		scheduleEndTimeField = new FlashTimeField(-1, -1, "","", "");
		scheduleEndTimeField.setDebugId("ef1e5342-eee4-4579-8b9b-dc47934bd27e",
				"866602c8-6b97-483b-abae-f3eba2d451b0", "90a2f9f4-d84e-4195-abc5-cef0278cee8b");
		scheduleEndTimeField.setValue(new Time(23, 59));
		
		RadioGroup fileCopyGrp = new RadioGroup();
		fileCopyGrp.add(copyAfterBackupRadio);
		fileCopyGrp.add(copyOnScheduleRadio);
		fileCopyGrp.addListener(Events.Change, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				childContainer.removeAll();
				if(weekDayRadio != null)
					weekDayRadio.setValue(false);
				if(monthlyRadio != null)
					monthlyRadio.setValue(false);
				RadioGroup rg = (RadioGroup) be.getSource();
				Radio selectedRadio = rg.getValue();
				if("copy_on_schedule".equals(selectedRadio.getItemId()))
					renderScheduleComponent();

			}
		});
		fileCopyGrp.setEnabled(false);
		LayoutContainer radioPanel = new LayoutContainer();
		radioPanel.add(copyAfterBackupRadio);
		FlowData layoutData = new FlowData(5, 0, 0, 0);
		radioPanel.add(copyOnScheduleRadio,layoutData);
		radioPanel.setVisible(!isArchiveTask);
		if(this.isArchiveTask)
			copyOnScheduleRadio.setValue(true);
		else
			copyAfterBackupRadio.setValue(true);
		
		this.add(AdvScheduleUtil.createFormLayout(UIContext.Constants.fileCopyIntervalLabel(),radioPanel), AdvScheduleUtil.createLineLayoutData());
		this.add(childContainer);
		
	}

	/*private void getThrottlingField() {
		throttlingBox = new IntegerField();
		throttlingBox.ensureDebugId("93432519-5c06-4694-a2e8-4952d87332a7");
		throttlingBox.setEnabled(false);
		throttlingBox.addValidator(new MinNumberValidator<Integer>(0));
		throttlingBox.setWidth(40);

		FieldLabel lb = new  FieldLabel();
		lb.setText(UIContext.Constants.destinationThrottleUnit());
		lb.setLabelSeparator("");

		limitThrottlingCB = new CheckBox();
		limitThrottlingCB.ensureDebugId("a1c31c55-3d48-44e5-b5ad-0d062ec5dd3f");
		limitThrottlingCB.setBoxLabel(UIContext.Constants.limitThroughputTo());

		limitThrottlingCB.addListener(Events.Change, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {
				throttlingBox.setEnabled(limitThrottlingCB.getValue());
			};
		});

		LayoutContainer container = new LayoutContainer();
		TableLayout tl = new TableLayout(3);
		tl.setCellSpacing(2);
		container.setLayout(tl);

		container.add(limitThrottlingCB);
		container.add(throttlingBox);
		container.add(lb);

		this.add(AdvScheduleUtil.createFormLayout(UIContext.Constants.throttlingLabel(),container), AdvScheduleUtil.createLineLayoutData());

	}*/

	private void renderScheduleComponent() {

		HorizontalPanel weekDayMonthContainer = new HorizontalPanel();


		weekDayRadio.ensureDebugId("52eeb33e-3043-4473-8ce3-c8d0d335a2ca");
		weekDayRadio.setBoxLabel(UIContext.Constants.daily());
		weekDayRadio.setItemId("week_day_radio");
		weekDayRadio.setFireChangeEventOnSetValue(true);
		weekDayMonthContainer.add(weekDayRadio);

		monthlyRadio.ensureDebugId("39fa7614-a620-449e-94a6-746772ac403f");
		monthlyRadio.setBoxLabel(UIContext.Constants.monthly());
		monthlyRadio.setItemId("monthly_radio");
		monthlyRadio.setFireChangeEventOnSetValue(true);
		monthlyRadio.setStyleAttribute("margin-left", "20px");
		weekDayMonthContainer.add(monthlyRadio);
		
		childContainer.add(AdvScheduleUtil.createFormLayout(UIContext.Constants.fileCopyScheduleType(),weekDayMonthContainer), AdvScheduleUtil.createLineLayoutData());
		childContainer.add(AdvScheduleUtil.createFormLayout(UIContext.Constants.scheduleStartTime(),scheduleStartTimeField), AdvScheduleUtil.createLineLayoutData());

		final LayoutContainer weekDayMonthlyWidgetPlaceHolder = new LayoutContainer();
		childContainer.add(AdvScheduleUtil.createFormLayout("",weekDayMonthlyWidgetPlaceHolder), AdvScheduleUtil.createLineLayoutData());

		
		
		RadioGroup recurrenceRadioToggle = new RadioGroup();
		recurrenceRadioToggle.add(weekDayRadio);
		recurrenceRadioToggle.add(monthlyRadio);
		recurrenceRadioToggle.setFireChangeEventOnSetValue(true);
		recurrenceRadioToggle.addListener(Events.Change, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				weekDayMonthlyWidgetPlaceHolder.removeAll();
				RadioGroup rg = (RadioGroup) be.getSource();
				Radio selectedRadio = rg.getValue();
				if("week_day_radio".equals(selectedRadio.getItemId())){
					getWeekDayWidget(weekDayMonthlyWidgetPlaceHolder);
				}
				else{
					getMonthlyWidget(weekDayMonthlyWidgetPlaceHolder);
				}

			}
		});

		recurrenceRadioToggle.setValue(weekDayRadio);
		
		if(!this.isArchiveTask)
			childContainer.add(AdvScheduleUtil.createFormLayout(UIContext.Constants.scheduleEndTime(),scheduleEndTimeField), AdvScheduleUtil.createLineLayoutData());

		childContainer.layout();

	}
	protected void getMonthlyWidget(LayoutContainer monthlyWidgetPlaceHolder) {
		monthlyWidgetPlaceHolder.add(addDayOrWeekRow());
		monthlyWidgetPlaceHolder.layout();

	}
	protected void getWeekDayWidget(LayoutContainer weekDayWidgetPlaceHolder) {
		weekDayWidget = new ScheduleDaySelectionPanel();
		weekDayWidget.setWidth(350);
		weekDayWidgetPlaceHolder.add(weekDayWidget);
		weekDayWidgetPlaceHolder.layout();
	}

	private LayoutContainer addDayOrWeekRow(){
		LayoutContainer dayweekContainer = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(3);
		tl.setCellSpacing(0);
		dayweekContainer.setLayout(tl);

		rgDayOfMonth = new RadioGroup();
		rgDayOfMonth.addListener(Events.Change, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
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

		TableData daytd = new TableData();
		daytd.setWidth("69px");

		rdDayOfMonth = new Radio();
		rdDayOfMonth.ensureDebugId("4b782430-fffc-44a8-b1bf-dc3b3ce5c699");
		rdDayOfMonth.setBoxLabel(UIContext.Constants.radio_day());
		rdDayOfMonth.setFireChangeEventOnSetValue(true);
		dayweekContainer.add(rdDayOfMonth, daytd);
		rgDayOfMonth.add(rdDayOfMonth);

		comboDayOfMonth = new BaseComboBox<ModelData>();
		comboDayOfMonth.setEditable(false);
		comboDayOfMonth.setEnabled(false);
		comboDayOfMonth.setValueField("value");
		
		com.extjs.gxt.ui.client.store.ListStore<ModelData> store = new com.extjs.gxt.ui.client.store.ListStore<ModelData>();		
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
		comboDayOfMonth.setWidth("110px");
		TableData tweek = new TableData();
		tweek.setColspan(2);
		dayweekContainer.add(comboDayOfMonth, tweek);
		
		rdWeekOfMonth = new Radio();
		rdWeekOfMonth.ensureDebugId("2095207f-ca1e-419c-9d44-18fd1e57cf21");
		rdWeekOfMonth.setBoxLabel(UIContext.Constants.radio_week());
		rdWeekOfMonth.setFireChangeEventOnSetValue(true);
		rdWeekOfMonth.setStyleAttribute("margin-top", "5px");
		dayweekContainer.add(rdWeekOfMonth, daytd);
		rgDayOfMonth.add(rdWeekOfMonth);

		comboWeekNumOfMonth = getWeekNumCombox();
		comboWeekNumOfMonth.setWidth("110px");
		comboWeekNumOfMonth.setEnabled(true);
		comboWeekNumOfMonth.setStyleAttribute("margin-top", "5px");

		dayweekContainer.add(comboWeekNumOfMonth);
		comboWeekDayOfMonth = getWeekCombox();
		comboWeekDayOfMonth.setEnabled(true);
		comboWeekDayOfMonth.setWidth("120px");	
		comboWeekDayOfMonth.setStyleAttribute("padding-left", "5px");
		comboWeekDayOfMonth.setStyleAttribute("margin-top", "5px");

		dayweekContainer.add(comboWeekDayOfMonth);

		rdDayOfMonth.setValue(true);
		return dayweekContainer;
	}

	private ComboBox<ModelData> getWeekCombox() {
		com.extjs.gxt.ui.client.store.ListStore<ModelData> weekStore = new com.extjs.gxt.ui.client.store.ListStore<ModelData>();
		BaseComboBox<ModelData> cbWeek = new BaseComboBox<ModelData>();
		cbWeek.setDisplayField("text");
		cbWeek.setValueField("dayOfWeek");
		cbWeek.setStore(weekStore);
		cbWeek.setEditable(false);
		cbWeek.setAllowBlank(false);	

		ModelData selectModel = null;
		for (int i = 0; i < ScheduleUtils.oneWeek.length; i++) {
			ModelData md = new BaseModelData();
			md.set("text", ScheduleUtils.oneWeek[i]);
			md.set("dayOfWeek", i + 1);
			weekStore.add(md);

			if (UIContext.Constants.scheduleFriday().equals(ScheduleUtils.oneWeek[i])) {
				selectModel = md;
			}
		}
		cbWeek.setValue(selectModel);
		return cbWeek;
	}

	private ComboBox<ModelData> getWeekNumCombox() {
		com.extjs.gxt.ui.client.store.ListStore<ModelData> weekStore = new com.extjs.gxt.ui.client.store.ListStore<ModelData>();
		BaseComboBox<ModelData> comboWeekNum = new BaseComboBox<ModelData>();
		comboWeekNum.setDisplayField("text");
		comboWeekNum.setValueField("value");
		comboWeekNum.setStore(weekStore);
		comboWeekNum.setEditable(false);
		comboWeekNum.setAllowBlank(false);

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

	public void setEnabled(boolean enabled){
		copyAfterBackupRadio.setEnabled(enabled);
		copyOnScheduleRadio.setEnabled(enabled);
		//		throttlingBox.setEnabled(enabled);
		//		limitThrottlingCB.setEnabled(enabled);
		scheduleStartTimeField.setEnabled(enabled);
		weekDayRadio.setEnabled(enabled);
		monthlyRadio.setEnabled(enabled);
		if(weekDayWidget != null)
			weekDayWidget.setEnabled(false);
		if(!isArchiveTask)
			scheduleEndTimeField.setEnabled(enabled);
	}


	public AdvanceSchedule getScheduleData(){

		AdvanceSchedule advSchedule = null;

		if(isArchiveTask || Boolean.TRUE.equals(copyOnScheduleRadio.getValue())){
			advSchedule = new AdvanceSchedule();

			if(weekDayRadio.getValue()){
				if(isArchiveTask){
					PeriodSchedule periodSchedule = new PeriodSchedule();
					EveryDaySchedule daySchedule = new EveryDaySchedule();
					daySchedule.setBkpType(FILE_COPY_JOB);
					daySchedule.setDayEnabled(weekDayWidget.getValue());
					daySchedule.setDayTime(ConvertToDayTime(scheduleStartTimeField.getValueModel()));
					daySchedule.setEnabled(true);
					periodSchedule.setDaySchedule(daySchedule);
					advSchedule.setPeriodSchedule(periodSchedule);
				}
				else{
					List<DailyScheduleDetailItem> dailyScheduleDetailItems = new ArrayList<DailyScheduleDetailItem>();

					Boolean[] selectedDays = weekDayWidget.getValue();

					for (int i = 0; i < selectedDays.length; i++) {
						if(selectedDays[i]){
							DailyScheduleDetailItem dailyItem = new DailyScheduleDetailItem();
							ArrayList<ScheduleDetailItem> scheduleDetailItems = new ArrayList<ScheduleDetailItem>();

							ScheduleDetailItem dailyScheduleItem = new ScheduleDetailItem();
							dailyScheduleItem.setStartTime(ConvertToDayTime(scheduleStartTimeField.getValueModel()));
							dailyScheduleItem.setEndTime(ConvertToDayTime(scheduleEndTimeField.getValueModel()));
							dailyScheduleItem.setJobType(FILE_COPY_JOB);
							dailyScheduleItem.setInterval(20);
							dailyScheduleItem.setIntervalUnit(0);
							dailyScheduleItem.setRepeatEnabled(false);

							scheduleDetailItems.add(dailyScheduleItem);
							dailyItem.setScheduleDetailItems(scheduleDetailItems);
							dailyItem.setDayofWeek(i+1);
							dailyScheduleDetailItems.add(dailyItem);
						}
					}
					advSchedule.setDailyScheduleDetailItems(dailyScheduleDetailItems);
				}
			}

			else if(monthlyRadio.getValue()){
				PeriodSchedule periodSchedule = new PeriodSchedule();

				boolean dayOfMonthEnabled = false;
				boolean weekOfMonthEnabled = false;

				if (rgDayOfMonth.getValue() == rdDayOfMonth)
					dayOfMonthEnabled = true;
				else
					weekOfMonthEnabled = true;

				int weekDayOfMonth = comboWeekDayOfMonth.getValue().<Integer> get(comboWeekDayOfMonth.getValueField());
				int weekNumOfMonth = comboWeekNumOfMonth.getValue().<Integer> get(comboWeekNumOfMonth.getValueField());
				int dayOfMonth = comboDayOfMonth.getValue().<Integer> get(comboDayOfMonth.getValueField());

				EveryMonthSchedule monthSchedule = new EveryMonthSchedule();
				monthSchedule.setBkpType(FILE_COPY_JOB);
				monthSchedule.setDayOfMonth(dayOfMonth);
				monthSchedule.setDayOfMonthEnabled(dayOfMonthEnabled);
				monthSchedule.setDayTime(ConvertToDayTime(scheduleStartTimeField.getValueModel()));
				if(!isArchiveTask)
					monthSchedule.setEndTime(ConvertToDayTime(scheduleEndTimeField.getValueModel()));
				monthSchedule.setEnabled(true);
				monthSchedule.setGenerateCatalog(false);
				monthSchedule.setWeekDayOfMonth(weekDayOfMonth);
				monthSchedule.setWeekNumOfMonth(weekNumOfMonth);
				monthSchedule.setWeekOfMonthEnabled(weekOfMonthEnabled);
				periodSchedule.setMonthSchedule(monthSchedule);
				advSchedule.setPeriodSchedule(periodSchedule);		
			}

		}

		return advSchedule;
	}

	public void setScheduleData(AdvanceSchedule model){
		if(model != null){
			copyOnScheduleRadio.setValue(true);

			List<DailyScheduleDetailItem> dailyScheduleItems = model.getDailyScheduleDetailItems();
			if(dailyScheduleItems != null && dailyScheduleItems.size() > 0){
				weekDayRadio.setValue(true);
				List<Integer> modifyItemIdxList = new ArrayList<Integer>();
				DayTime startTime = null;
				DayTime endTime = null;

				for (DailyScheduleDetailItem dailyScheduleDetailItem : dailyScheduleItems) {
					modifyItemIdxList.add(dailyScheduleDetailItem.getDayofWeek());
					ArrayList<ScheduleDetailItem> schedDetailItems = dailyScheduleDetailItem.getScheduleDetailItems();
					if(schedDetailItems != null && schedDetailItems.size() > 0){
						ScheduleDetailItem scheduleDetail = schedDetailItems.get(0);
						if(startTime == null)
							startTime = scheduleDetail.getStartTime();
						if(endTime == null)
							endTime = scheduleDetail.getEndTime();

					}
				}

				scheduleStartTimeField.setValue(new Time(startTime.getHour(), startTime.getMinute()));
				if(!isArchiveTask)
					scheduleEndTimeField.setValue(new Time(endTime.getHour(), endTime.getMinute()));
				weekDayWidget.setValue(modifyItemIdxList);
			}
			/*List<DailyScheduleDetailItemModel> dailySchedItemList = model.daylyScheduleDetailItemModel;
			if(dailySchedItemList != null && dailySchedItemList.size() > 0){
				DailyScheduleDetailItemModel dailySchedItem = dailySchedItemList.get(0);
				List<ScheduleDetailItemModel> schedDetailsItemModelList = dailySchedItem.scheduleDetailItemModels;
				if(schedDetailsItemModelList != null && schedDetailsItemModelList.size() > 0){
					ScheduleDetailItemModel schedModel = schedDetailsItemModelList.get(0);
					scheduleStartTimeField.setValue(schedModel.startTimeModel);
					scheduleEndTimeField.setValue(schedModel.endTimeModel);
				}
				List<ThrottleModel> throttleModelList = dailySchedItem.throttleModels;
				if(throttleModelList != null && throttleModelList.size() > 0){
					ThrottleModel throttleModel = throttleModelList.get(0);
					limitThrottlingCB.setValue(true);
					throttlingBox.setValue(throttleModel.getThrottleValue().intValue());
				}
			}*/

			else{
				PeriodSchedule periodSchedule = model.getPeriodSchedule();
				if(periodSchedule != null){
					EveryMonthSchedule monthSchedModel = periodSchedule.getMonthSchedule();
					if(monthSchedModel.isEnabled()){
						monthlyRadio.setValue(true);
						if(monthSchedModel.isDayOfMonthEnabled()){
							rdDayOfMonth.setValue(true);
							comboDayOfMonth.setEnabled(true);
							comboDayOfMonth.setValue(intToModelData(monthSchedModel.getDayOfMonth(), comboDayOfMonth));
							comboWeekDayOfMonth.setEnabled(false);
							comboWeekNumOfMonth.setEnabled(false);
						}else if(monthSchedModel.isWeekOfMonthEnabled()){
							rdWeekOfMonth.setValue(true);

							comboWeekDayOfMonth.setEnabled(true);
							comboWeekDayOfMonth.setValue(intToModelData(monthSchedModel.getWeekDayOfMonth(), comboWeekDayOfMonth));

							comboWeekNumOfMonth.setEnabled(true);
							comboWeekNumOfMonth.setValue(intToModelData(monthSchedModel.getWeekNumOfMonth(), comboWeekNumOfMonth));

							comboDayOfMonth.setEnabled(false);
						}

						DayTime startTime = monthSchedModel.getDayTime();
						DayTime endTime = monthSchedModel.getEndTime();

						scheduleStartTimeField.setValue(new Time(startTime.getHour(), startTime.getMinute()));
						if(!isArchiveTask)
							scheduleEndTimeField.setValue(new Time(endTime.getHour(), endTime.getMinute()));
					}
					else{
						EveryDaySchedule daySchedule = periodSchedule.getDaySchedule();
						if(daySchedule != null && daySchedule.isEnabled()){
							weekDayRadio.setValue(true);
							DayTime startTime = daySchedule.getDayTime();
							scheduleStartTimeField.setValue(new Time(startTime.getHour(), startTime.getMinute()));
							weekDayWidget.setValue(daySchedule.getDayEnabled());
						}
					}
				}
			}

		}else{
			if(!isArchiveTask)
				copyAfterBackupRadio.setValue(true);
			else
				copyOnScheduleRadio.setValue(true);
		}
	}

	public boolean validate(){
		if(copyOnScheduleRadio.getValue()){
			if(weekDayRadio.getValue()){
				if(weekDayWidget.getDayIndexs().size() < 1)
					return false;
			}else if(monthlyRadio.getValue()){
				if(rdWeekOfMonth.getValue()){
					if(comboWeekDayOfMonth.getValue()==null ||  comboWeekNumOfMonth.getValue() == null)
						return false;
				}else if(rdDayOfMonth.getValue()){
					if(comboDayOfMonth.getValue() == null)
						return false;
				}
			}else
				return false;

		}
		/*if(limitThrottlingCB.getValue()){
			if(throttlingBox.getValue() == null || throttlingBox.getValue() <= 0){
				throttlingBox.markInvalid(UIContext.Constants.throttle_field_validation());
				return false;
			}
		}*/
		return true;
	}

	public boolean isAdvanceScheduling(){
		return copyOnScheduleRadio.getValue();
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

	private DayTime ConvertToDayTime(DayTimeModel dailyBackupTime) {
		DayTime time = new DayTime();
		time.setHour(dailyBackupTime.getHour());
		time.setMinute(dailyBackupTime.getMinutes());

		return time;
	}

}