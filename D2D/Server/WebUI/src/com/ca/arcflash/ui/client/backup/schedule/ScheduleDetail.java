package com.ca.arcflash.ui.client.backup.schedule;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.DayTimeModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public abstract  class ScheduleDetail extends LayoutContainer{
	
	protected int exceptIndex;
	private DailyScheduleDetailItemModel model;
	private ScheduleDetail outer;
	protected Grid<ScheduleDetailItemModel> scheduleGrid;
	protected ListStore<ScheduleDetailItemModel> scheduleStore;
	protected Grid<ThrottleModel> throttleGrid;
	protected ListStore<ThrottleModel> throttleStore;
	protected ListStore<MergeDetailItemModel> mergeStore;
	protected Grid<MergeDetailItemModel> mergeGrid;
	private LayoutContainer mergeButtonContainer;
	private Grid<MergeDetailItemModel> mergeGridContainer;
	private LayoutContainer scheduleButtonContainer;
	private LayoutContainer throttleButtonContainer;
	private boolean showMergeFlag=true;
	private boolean showScheduleFlag=true;
	private boolean showThrottleFlag=true;
	
	protected  Set<Label> linkLabelSet;
	protected  boolean isEditable;
	private int dayOfWeek;	
	
	public int getDayofWeek(){
		return this.dayOfWeek;
	}
	public ScheduleDetail(int exceptIndex,boolean showMergeGrid, int dayofWeek){
		this.exceptIndex = exceptIndex;
		this.outer = this;
		this.linkLabelSet = new HashSet<Label>();
		this.isEditable = true;
		this.dayOfWeek = dayofWeek;
		initScheduleDetail(showMergeGrid);		
	}
	
	public void ShowMerge(){
		showMergeFlag=true;
		mergeButtonContainer.show();
		mergeGridContainer.show();
	}
	
	public void HideMerge(){
		showMergeFlag=false;
		mergeButtonContainer.hide();
		mergeGridContainer.hide();
	}
	
	public boolean getShowMergeFlag(){
		return this.showMergeFlag;
	}
	
	public void ShowSchedule(){
		showScheduleFlag=true;
		scheduleButtonContainer.show();
		scheduleGrid.show();
	}
	
	public void HideSchedule(){
		showScheduleFlag=false;
		scheduleButtonContainer.hide();
		scheduleGrid.hide();
	}
	
	public boolean getShowScheduleFlag(){
		return this.showScheduleFlag;
	}

	public void ShowThrottle(){
		showThrottleFlag=true;
		throttleButtonContainer.show();
		throttleGrid.show();
	}
	
	public void HideThrottle(){
		showThrottleFlag=false;
		throttleButtonContainer.hide();
		throttleGrid.hide();
	}
	
	public boolean getShowThrottleFlag(){
		return this.showThrottleFlag;
	}
	
	
	public ListStore<ScheduleDetailItemModel> getScheduleDetailStore(){
		return scheduleStore;
	}
	public ListStore<ThrottleModel> getThrottleStore(){
		return throttleStore;
	}
	public ListStore<MergeDetailItemModel> getMergeStore(){
		return mergeStore;
	}
	
	public void copyScheduleItemsToOtherDays(List<Integer> destIndexs, boolean isCopySchedule, boolean isCopyThrottling, boolean isCopyMerge){
		
		ScheduleDetail[] allScheduleDetails = getParentPanel().getAllScheduleDetails();
		if(allScheduleDetails==null)
			return;
		
		for (int destIndex : destIndexs) {
			ScheduleDetail destScheduleDetail = allScheduleDetails[destIndex];
			if(destScheduleDetail==null)
				continue;
			
			if(isCopySchedule){
				ListStore<ScheduleDetailItemModel> destScheduleStore = destScheduleDetail.getScheduleDetailStore();
				destScheduleStore.removeAll();
				for (ScheduleDetailItemModel detailModel : scheduleStore.getModels()) {
					destScheduleStore.add(ScheduleUtils.cloneScheduleItemModel(detailModel));
				}
			}
			
			if(isCopyThrottling){
				ListStore<ThrottleModel> destThrottleStore = destScheduleDetail.getThrottleStore();
				destThrottleStore.removeAll();
				for (ThrottleModel throttleModel : throttleStore.getModels()) {
					destThrottleStore.add(ScheduleUtils.cloneThrottleItemModel(throttleModel));
				}
			}
			
			if(isCopyMerge){
				ListStore<MergeDetailItemModel> destMergeStore = destScheduleDetail.getMergeStore();
				destMergeStore.removeAll();
				for (MergeDetailItemModel mergeModel : mergeStore.getModels()) {
					destMergeStore.add(ScheduleUtils.cloneMergeItemModel(mergeModel));
				}
			}
			
			destScheduleDetail.checkButtonIcon();
		}
		
		processAfterCopyOption(isCopySchedule, isCopyThrottling, isCopyMerge && this.showMergeFlag);
	}
	
	private void initScheduleDetail(boolean showMergeGrid){
		
		TableLayout tl = new TableLayout();
		tl.setWidth("100%");
		tl.setCellPadding(0);
		tl.setCellSpacing(1);
		this.setLayout(tl);
		
		//this.add(new HTML("<HR>"));
		scheduleButtonContainer = getScheduleButtons();
		scheduleGrid = getScheduleGrid();
		throttleButtonContainer = getThrottleButtons();
		throttleGrid = getThrottleGrid();
		if(showScheduleFlag){
			this.add(scheduleButtonContainer);
			this.add(scheduleGrid);
		}else{
			this.scheduleButtonContainer.hide();
			this.scheduleGrid.hide();
			this.layout();
		}
		
		if(showThrottleFlag){
			this.add(throttleButtonContainer);
			this.add(throttleGrid);
		}else{
			this.throttleButtonContainer.hide();
			this.throttleGrid.hide();
			this.layout();
		}
		
		mergeButtonContainer=getMergeButtons();
		mergeGridContainer=getMergeGrid();
		if (showMergeGrid){
			this.add(mergeButtonContainer);
			this.add(mergeGridContainer);
		}else{
			this.mergeButtonContainer.hide();
			this.mergeGridContainer.hide();
			this.layout();
		}
		this.add(getCopyButtons());
		
	}

	protected LayoutContainer getCopyButtons(){
		Label copyLabel = new Label(UIContext.Constants.scheduleCopyText());
		copyLabel.ensureDebugId("4bdf670e-2ad5-4886-a950-33bb55170afc");
		copyLabel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(!isEditable)
					return;
				
				if(isEmptyItem())
					return;
				
				ScheduleCopyWindow window = new ScheduleCopyWindow(exceptIndex, outer);
				window.setModal(true);
				window.show();
			}
			
		});
		
		return getLayoutContainer(new LabelField(),  getLinkLabelContainer(AbstractImagePrototype.create(UIContext.IconBundle.schedule_copy_icon()).createImage(), copyLabel));
	}
	
	protected LayoutContainer getScheduleButtons(){
		LabelField labelDescription = new LabelField(getScheduleLabelTitle());
		labelDescription.setStyleName("schedule_item_lable");
		LayoutContainer layoutDescription = getNoLinkLabelContainer(AbstractImagePrototype.create(UIContext.IconBundle.schedule_job_icon()).createImage(), labelDescription);
		
		Label addLabel = new Label(getScheduleAddText());
		addLabel.ensureDebugId("4d843653-2a34-468a-a0af-f53add7d930c");
		addLabel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(!isEditable)
					return;
				
				if(scheduleStore.getModels().size()>=ScheduleUtils.MAX_SCHEDULE_ITEM_COUNT){
					String title = UIContext.productNameD2D;
					String msg = UIContext.Messages.scheduleMaxItem(ScheduleUtils.MAX_SCHEDULE_ITEM_COUNT);
					ScheduleUtils.showMesssageBox(title, msg, MessageBox.ERROR);
				}
				else{
					ScheduleDetailItemModel newModel = getDefaultScheduleItemModel();
					showScheduleDetailWindow(-1, newModel);
				}
			}
		});
		
		return getLayoutContainer(layoutDescription, getLinkLabelContainer(AbstractImagePrototype.create(UIContext.IconBundle.schedule_new_icon()).createImage(), addLabel));
	}
	
	protected Grid<ScheduleDetailItemModel> getScheduleGrid(){
		GridCellRenderer<ScheduleDetailItemModel> startTimeRenderer = new GridCellRenderer<ScheduleDetailItemModel>() {
			@Override
			public Object render(ScheduleDetailItemModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ScheduleDetailItemModel> store,
					Grid<ScheduleDetailItemModel> grid) 
			{
				return ScheduleUtils.formatTime(model.startTimeModel);
			}
			
		};
		
		GridCellRenderer<ScheduleDetailItemModel> endTimeRenderer = new GridCellRenderer<ScheduleDetailItemModel>() {
			@Override
			public Object render(ScheduleDetailItemModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ScheduleDetailItemModel> store,
					Grid<ScheduleDetailItemModel> grid) 
			{
				//model.setRepeatEnabled(true);
				return model.isRepeatEnabled() ? ScheduleUtils.formatTime(model.endTimeModel) : UIContext.Constants.NA();
			}
			
		};
		
		GridCellRenderer<ScheduleDetailItemModel> jobTypeRenderer = new GridCellRenderer<ScheduleDetailItemModel>() {
			@Override
			public Object render(ScheduleDetailItemModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ScheduleDetailItemModel> store,
					Grid<ScheduleDetailItemModel> grid) 
			{
				return ScheduleUtils.getJobTypeStr(model.getJobType());
			}
		};
		
		GridCellRenderer<ScheduleDetailItemModel> repeatRenderer = new GridCellRenderer<ScheduleDetailItemModel>() {
			@Override
			public Object render(ScheduleDetailItemModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ScheduleDetailItemModel> store,
					Grid<ScheduleDetailItemModel> grid) 
			{
				return model.isRepeatEnabled() ? ScheduleUtils.getScheduleRepeatStr(model.getInterval(), model.getIntervalUnit()) : UIContext.Constants.NA();
			}
			
		};
		
		GridCellRenderer<ScheduleDetailItemModel> actionRenderer = new GridCellRenderer<ScheduleDetailItemModel>() {
			@Override
			public Object render(ScheduleDetailItemModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ScheduleDetailItemModel> store,
					Grid<ScheduleDetailItemModel> grid) 
			{
				final int newRowIndex = rowIndex;
				final ScheduleDetailItemModel newModel = model;
				LayoutContainer layout = new LayoutContainer();
				TableLayout tl = new TableLayout(2);
				tl.setWidth("100%");
				layout.setLayout(tl);
				
				TableData td = new TableData();
				td.setWidth("48%");
				td.setHorizontalAlign(HorizontalAlignment.RIGHT);
				Label editLabel = new Label(UIContext.Constants.scheduleEditText());
				editLabel.ensureDebugId("c8b2eb66-e627-4d7e-b72e-2e07e428cda8");
				editLabel.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						if(!isEditable)
							return;
						
						showScheduleDetailWindow(newRowIndex, newModel);
					}
				});
				layout.add(getLinkLabelContainer(AbstractImagePrototype.create(UIContext.IconBundle.schedule_edit_icon()).createImage(),editLabel), td);
				
				td = new TableData();
				td.setWidth("52%");
				Label delLabel = new Label(UIContext.Constants.scheduleDelText());
				delLabel.ensureDebugId("096662b8-799f-47f1-bf07-cb9edd45fd53");
				delLabel.addClickHandler(getScheduleDelHandler(newModel));
				layout.add(getLinkLabelContainer(AbstractImagePrototype.create(UIContext.IconBundle.schedule_delete_icon()).createImage(),delLabel), td);
				
				return layout;
			}
			
		};
		
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		/*configs.add(Utils.createColumnConfig("from", UIContext.Constants.scheduleStartAt(), 80, startTimeRenderer));
		configs.add(Utils.createColumnConfig("to", UIContext.Constants.scheduleStopAt(), 80, endTimeRenderer));
		configs.add(Utils.createColumnConfig("jobType", UIContext.Constants.homepageRecentBackupColumnTypeHeader(), 129, jobTypeRenderer));
		configs.add(Utils.createColumnConfig("repeat", UIContext.Constants.scheduleRepeat(), 85, repeatRenderer));
		ColumnConfig config = Utils.createColumnConfig("action", UIContext.Constants.scheduleAction(), 152, actionRenderer);*/
		ColumnConfig configFrom = Utils.createColumnConfig("from", UIContext.Constants.scheduleStartAt(), 75, startTimeRenderer);
		configFrom.setAlignment(HorizontalAlignment.CENTER);
		configFrom.setResizable(true);
		configs.add(configFrom);
		ColumnConfig configTo = Utils.createColumnConfig("to", UIContext.Constants.scheduleStopAt(), 75, endTimeRenderer);
		configTo.setAlignment(HorizontalAlignment.CENTER);
		configTo.setResizable(true);
		configs.add(configTo);
		ColumnConfig configJobType = Utils.createColumnConfig("jobType", UIContext.Constants.homepageRecentBackupColumnTypeHeader(), 107, jobTypeRenderer);
		configJobType.setAlignment(HorizontalAlignment.CENTER);
		configJobType.setResizable(true);
		configs.add(configJobType);
		ColumnConfig configRepeat = Utils.createColumnConfig("repeat", UIContext.Constants.scheduleRepeat(), 84,repeatRenderer);
		configRepeat.setAlignment(HorizontalAlignment.CENTER);
		configRepeat.setResizable(true);
		configs.add(configRepeat);
		ColumnConfig configAction = Utils.createColumnConfig("action", UIContext.Constants.scheduleAction(), 184, actionRenderer);
		configAction.setAlignment(HorizontalAlignment.CENTER);
		configAction.setResizable(true);
		configs.add(configAction);
		Comparator<Object> comparator = new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				if((o1 instanceof ScheduleDetailItemModel) && (o2 instanceof ScheduleDetailItemModel)){
					ScheduleDetailItemModel model1 = (ScheduleDetailItemModel)o1;
					ScheduleDetailItemModel model2 = (ScheduleDetailItemModel)o2;
					return ScheduleUtils.compareDayTimeModel(model1.startTimeModel, model2.startTimeModel);
				}
				return 0;
				
			}
		};
		
		ColumnModel columnModel = new ColumnModel(configs);	
		scheduleStore = new ListStore<ScheduleDetailItemModel>();
		scheduleStore.setStoreSorter(new StoreSorter<ScheduleDetailItemModel>(comparator));
		scheduleGrid = new Grid<ScheduleDetailItemModel>(scheduleStore, columnModel);
		scheduleGrid.setBorders(true);
		//scheduleGrid.setAutoExpandColumn("action");
		scheduleGrid.setHeight(100);
		scheduleGrid.setAutoWidth(true);
		scheduleGrid.setStripeRows(true);   
		scheduleGrid.setColumnLines(true);   
		scheduleGrid.ensureDebugId("afb2f458-3e9a-49bc-9aa9-0a52eb36f054");
		//scheduleGrid.setColumnReordering(true);
		scheduleGrid.setAutoExpandColumn("action");
		scheduleGrid.setAutoExpandMax(3000);
		scheduleGrid.setAutoExpandMin(75);	 
		scheduleGrid.addStyleName("schedule_grid");
		return scheduleGrid;
	}
	
	protected LayoutContainer getThrottleButtons(){
		LabelField labelDescription = new LabelField(getThrottleLabelTitle());
		labelDescription.setStyleName("schedule_item_lable");
		LayoutContainer layoutDescription = getNoLinkLabelContainer(AbstractImagePrototype.create(UIContext.IconBundle.schedule_throttle_icon()).createImage(), labelDescription);
		
		Label addLabel = new Label(getThrottleAddText());
		addLabel.setStyleName("homepage_header_hyperlink_label");
		addLabel.ensureDebugId("4c807a0f-de7a-4c0c-89a9-6c7ee7b0ef44");
		addLabel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(!isEditable)
					return;
				
				if(throttleStore.getModels().size()>=ScheduleUtils.MAX_THROTTLE_ITEM_COUNT){
					String title = UIContext.productNameD2D;
					String msg = UIContext.Messages.scheduleThrottleMaxItem(ScheduleUtils.MAX_THROTTLE_ITEM_COUNT);
					ScheduleUtils.showMesssageBox(title, msg, MessageBox.ERROR);
				}
				else{
					ThrottleModel newModel = getDefaultThrottleItemModel();
					showThrottleWindow(-1, newModel);
				}

			}
		});
		
		return getLayoutContainer(layoutDescription, getLinkLabelContainer(AbstractImagePrototype.create(UIContext.IconBundle.schedule_new_icon()).createImage(), addLabel));
	}
	
	protected Grid<ThrottleModel> getThrottleGrid(){
		GridCellRenderer<ThrottleModel> startTimeRenderer = new GridCellRenderer<ThrottleModel>() {
			@Override
			public Object render(ThrottleModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ThrottleModel> store,
					Grid<ThrottleModel> grid) 
			{
				return ScheduleUtils.formatTime(model.startTimeModel);
			}
			
		};
		
		GridCellRenderer<ThrottleModel> endTimeRenderer = new GridCellRenderer<ThrottleModel>() {
			@Override
			public Object render(ThrottleModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ThrottleModel> store,
					Grid<ThrottleModel> grid) 
			{
				return ScheduleUtils.formatTime(model.endTimeModel);
			}
			
		};
		
		GridCellRenderer<ThrottleModel> throttleRenderer = new GridCellRenderer<ThrottleModel>() {
			@Override
			public Object render(ThrottleModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ThrottleModel> store,
					Grid<ThrottleModel> grid) 
			{
				LabelField throttleValueLabel = null;
				Long throttleValue = model.getThrottleValue();
				if(throttleValue>0){
					throttleValueLabel = new LabelField(throttleValue+" "+ getThrottleUnit());
				}
				else{
					throttleValueLabel = new LabelField(UIContext.Constants.NA());
					
				}
				return throttleValueLabel;
			}
		};
		
		GridCellRenderer<ThrottleModel> actionRenderer = new GridCellRenderer<ThrottleModel>() {
			@Override
			public Object render(ThrottleModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ThrottleModel> store,
					Grid<ThrottleModel> grid) 
			{
				final int newRowIndex = rowIndex;
				final ThrottleModel newModel = model;
				LayoutContainer layout = new LayoutContainer();
				TableLayout tl = new TableLayout(2);
				tl.setWidth("100%");
				layout.setLayout(tl);
				
				TableData td = new TableData();
				td.setWidth("48%");
				td.setHorizontalAlign(HorizontalAlignment.RIGHT);
				Label editLabel = new Label(UIContext.Constants.scheduleEditText());
				editLabel.ensureDebugId("b7d910a5-b3c6-4e1f-9262-1419198aa7a6");
				editLabel.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						if(!isEditable)
							return;
						
						showThrottleWindow(newRowIndex,newModel);
					}
				});
				layout.add(getLinkLabelContainer(AbstractImagePrototype.create(UIContext.IconBundle.schedule_edit_icon()).createImage(),editLabel), td);
				
				td = new TableData();
				td.setWidth("52%");
				Label delLabel = new Label(UIContext.Constants.scheduleDelText());
				delLabel.ensureDebugId("20a5dfb4-d6ee-41a6-b161-f6e47021c1d1");
				delLabel.addClickHandler(getThrottleDelHandler(newModel));
				layout.add(getLinkLabelContainer(AbstractImagePrototype.create(UIContext.IconBundle.schedule_delete_icon()).createImage(),delLabel), td);
				
				return layout;
			}
			
		};
		
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		ColumnConfig configFrom = Utils.createColumnConfig("from", UIContext.Constants.scheduleStartAt(), 90, startTimeRenderer);
		configFrom.setAlignment(HorizontalAlignment.CENTER);
		configFrom.setResizable(true);
		configs.add(configFrom);
		ColumnConfig configTo = Utils.createColumnConfig("to", UIContext.Constants.scheduleStopAt(), 90, endTimeRenderer);
		configTo.setAlignment(HorizontalAlignment.CENTER);
		configTo.setResizable(true);
		configs.add(configTo);
		ColumnConfig configThrottle = Utils.createColumnConfig("throttle", UIContext.Constants.scheduleThrottleText(), 149, throttleRenderer);
		configThrottle.setAlignment(HorizontalAlignment.CENTER);
		configThrottle.setResizable(true);
		configs.add(configThrottle);
		ColumnConfig configAction = Utils.createColumnConfig("action", UIContext.Constants.scheduleAction(), 197, actionRenderer);
		configAction.setAlignment(HorizontalAlignment.CENTER);
		configAction.setResizable(true);
		configs.add(configAction);
		
		Comparator<Object> comparator = new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				if((o1 instanceof ThrottleModel) && (o2 instanceof ThrottleModel)){
					ThrottleModel model1 = (ThrottleModel)o1;
					ThrottleModel model2 = (ThrottleModel)o2;
					return ScheduleUtils.compareDayTimeModel(model1.startTimeModel, model2.startTimeModel);
				}
				return 0;
				
			}
		};
		ColumnModel columnModel = new ColumnModel(configs);	
		throttleStore = new ListStore<ThrottleModel>();
		throttleStore.setStoreSorter(new StoreSorter<ThrottleModel>(comparator));
		throttleGrid = new Grid<ThrottleModel>(throttleStore, columnModel);
		throttleGrid.setBorders(true);
		throttleGrid.setHeight(100);
		throttleGrid.setAutoExpandColumn("action");
		throttleGrid.setAutoWidth(true);
		throttleGrid.setStripeRows(true);   
		throttleGrid.setColumnLines(true); 
		throttleGrid.ensureDebugId("a89307ec-9436-418d-a514-c3de0e747dfe");
		throttleGrid.setAutoExpandMax(3000);
		throttleGrid.setAutoExpandMin(75);
		throttleGrid.addStyleName("schedule_grid");
		return throttleGrid;
	}
	
	protected LayoutContainer getMergeButtons(){
		LabelField labelDescription = new LabelField(getMergeLabelTitle());
		labelDescription.setStyleName("schedule_item_lable");
		LayoutContainer layoutDescription = getNoLinkLabelContainer(AbstractImagePrototype.create(UIContext.IconBundle.schedule_throttle_icon()).createImage(), labelDescription);
		
		Label addLabel = new Label(getMergeAddText());
		addLabel.setStyleName("homepage_header_hyperlink_label");
		addLabel.ensureDebugId("aeadd7ac-0191-4963-982f-4f16b2c7167a");
		addLabel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(!isEditable)
					return;
				
				if(mergeStore.getModels().size()>=ScheduleUtils.MAX_MERGE_ITEM_COUNT){
					String title = UIContext.productNameD2D;
					String msg = UIContext.Messages.scheduleMergeMaxItem(ScheduleUtils.MAX_MERGE_ITEM_COUNT);
					ScheduleUtils.showMesssageBox(title, msg, MessageBox.ERROR);
				}
				else{
					MergeDetailItemModel newModel = getDefaultMergeItemModel();
					showMergeWindow(-1, newModel);
				}

			}
		});
		
		return getLayoutContainer(layoutDescription, getLinkLabelContainer(AbstractImagePrototype.create(UIContext.IconBundle.schedule_new_icon()).createImage(), addLabel));
	}
	
	protected Grid<MergeDetailItemModel> getMergeGrid(){
		GridCellRenderer<MergeDetailItemModel> startTimeRenderer = new GridCellRenderer<MergeDetailItemModel>() {
			@Override
			public Object render(MergeDetailItemModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<MergeDetailItemModel> store,
					Grid<MergeDetailItemModel> grid) 
			{
				return ScheduleUtils.formatTime(model.startTimeModel);
			}
			
		};
		
		GridCellRenderer<MergeDetailItemModel> endTimeRenderer = new GridCellRenderer<MergeDetailItemModel>() {
			@Override
			public Object render(MergeDetailItemModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<MergeDetailItemModel> store,
					Grid<MergeDetailItemModel> grid) 
			{
				return ScheduleUtils.formatTime(model.endTimeModel);
			}
			
		};
		
//		GridCellRenderer<MergeDetailItemModel> descriptionRenderer = new GridCellRenderer<MergeDetailItemModel>() {
//			@Override
//			public Object render(MergeDetailItemModel model, String property,
//					ColumnData config, int rowIndex, int colIndex,
//					ListStore<MergeDetailItemModel> store,
//					Grid<MergeDetailItemModel> grid) 
//			{
//				LabelField mergeDescriptionLabel = new LabelField(UIContext.Constants.scheduleMergeDescriptionText());
//				return mergeDescriptionLabel;
//			}
//		};
		
		GridCellRenderer<MergeDetailItemModel> actionRenderer = new GridCellRenderer<MergeDetailItemModel>() {
			@Override
			public Object render(MergeDetailItemModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<MergeDetailItemModel> store,
					Grid<MergeDetailItemModel> grid) 
			{
				final int newRowIndex = rowIndex;
				final MergeDetailItemModel newModel = model;
				LayoutContainer layout = new LayoutContainer();
				TableLayout tl = new TableLayout(2);
				tl.setWidth("100%");
				layout.setLayout(tl);
				
				TableData td = new TableData();
				td.setWidth("48%");
				td.setHorizontalAlign(HorizontalAlignment.RIGHT);
				Label editLabel = new Label(UIContext.Constants.scheduleEditText());
				editLabel.ensureDebugId("b7d910a5-b3c6-4e1f-9262-1419198aa7a6");
				editLabel.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						if(!isEditable)
							return;
						
						showMergeWindow(newRowIndex,newModel);
					}
				});
				layout.add(getLinkLabelContainer(AbstractImagePrototype.create(UIContext.IconBundle.schedule_edit_icon()).createImage(),editLabel), td);
				
				td = new TableData();
				td.setWidth("52%");
				Label delLabel = new Label(UIContext.Constants.scheduleDelText());
				delLabel.ensureDebugId("20a5dfb4-d6ee-41a6-b161-f6e47021c1d1");
				delLabel.addClickHandler(getMergeDelHandler(newModel));
				layout.add(getLinkLabelContainer(AbstractImagePrototype.create(UIContext.IconBundle.schedule_delete_icon()).createImage(),delLabel), td);
				
				return layout;
			}
			
		};
		
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		ColumnConfig configFrom = Utils.createColumnConfig("from", UIContext.Constants.scheduleStartAt(), 165, startTimeRenderer);
		configFrom.setAlignment(HorizontalAlignment.CENTER);
		configFrom.setResizable(true);
		configs.add(configFrom);
		ColumnConfig configTo = Utils.createColumnConfig("to", UIContext.Constants.scheduleStopAt(), 165, endTimeRenderer);
		configTo.setAlignment(HorizontalAlignment.CENTER);
		configTo.setResizable(true);
		configs.add(configTo);
//		ColumnConfig configMerge = Utils.createColumnConfig("merge", UIContext.Constants.scheduleMergeText(), 149, descriptionRenderer);
//		configMerge.setAlignment(HorizontalAlignment.CENTER);
//		configMerge.setResizable(true);
//		configs.add(configMerge);
		ColumnConfig configAction = Utils.createColumnConfig("action", UIContext.Constants.scheduleAction(), 197, actionRenderer);
		configAction.setAlignment(HorizontalAlignment.CENTER);
		configAction.setResizable(true);
		configs.add(configAction);
		
		Comparator<Object> comparator = new Comparator<Object>() {
			@Override
			public int compare(Object o1, Object o2) {
				if((o1 instanceof MergeDetailItemModel) && (o2 instanceof MergeDetailItemModel)){
					MergeDetailItemModel model1 = (MergeDetailItemModel)o1;
					MergeDetailItemModel model2 = (MergeDetailItemModel)o2;
					return ScheduleUtils.compareDayTimeModel(model1.startTimeModel, model2.startTimeModel);
				}
				return 0;
				
			}
		};
		ColumnModel columnModel = new ColumnModel(configs);	
		mergeStore = new ListStore<MergeDetailItemModel>();
		mergeStore.setStoreSorter(new StoreSorter<MergeDetailItemModel>(comparator));
		mergeGrid = new Grid<MergeDetailItemModel>(mergeStore, columnModel);
		mergeGrid.setBorders(true);
		mergeGrid.setHeight(100);
		mergeGrid.setAutoExpandColumn("action");
		mergeGrid.setAutoWidth(true);
		mergeGrid.setStripeRows(true);   
		mergeGrid.setColumnLines(true); 
		mergeGrid.ensureDebugId("a89307ec-9436-418d-a514-c3de0e747dfe");
		mergeGrid.setAutoExpandMax(3000);
		mergeGrid.setAutoExpandMin(75);
		mergeGrid.addStyleName("schedule_grid");
		return mergeGrid;
	}
	
	
	private LayoutContainer getLayoutContainer(Widget widget1, Widget widget2){
		LayoutContainer container = new LayoutContainer();
		TableLayout tableLayout = new TableLayout(2);
		tableLayout.setWidth("100%");
		container.setLayout(tableLayout);
		
		TableData td =  new TableData();
		td.setWidth("50%");
		td.setHorizontalAlign(HorizontalAlignment.LEFT);
		container.add(widget1, td);
		
		td = new TableData();
		td.setWidth("50%");
		td.setHorizontalAlign(HorizontalAlignment.RIGHT);
		container.add(widget2, td);
		
		return container;
	}
	
	private LayoutContainer getLinkLabelContainer(Image icon, Label label){
		LayoutContainer container = new LayoutContainer();
		TableLayout tableLayout = new TableLayout(2);
		tableLayout.setCellPadding(1);
		tableLayout.setCellSpacing(1);
		container.setLayout(tableLayout);
		
		updateLinkLabelStyle(label);
		
		container.add(icon);
		container.add(label);
		return container;
	}
	
	private LayoutContainer getNoLinkLabelContainer(Image icon, LabelField label){
		LayoutContainer container = new LayoutContainer();
		TableLayout tableLayout = new TableLayout(2);
		tableLayout.setCellPadding(1);
		tableLayout.setCellSpacing(1);
		container.setLayout(tableLayout);
		
		container.add(icon);
		container.add(label);
		return container;
	}
	
	protected void showScheduleDetailWindow(int rowIndex,ScheduleDetailItemModel newModel){
		boolean isNewAdd = rowIndex<0 ? true:false;
		ScheduleDetailItemWindow window = new ScheduleDetailItemWindow( newModel, isNewAdd, this);
		window.setOKButtonListener(getScheduleConfirmHandler(rowIndex, newModel, window));
		window.setModal(true);
		window.show();
	}
	
	private void showThrottleWindow(int rowIndex, ThrottleModel newModel){
		boolean isNewAdd = rowIndex<0 ? true:false;
		final ThrottleItemWindow window = new ThrottleItemWindow( newModel, isNewAdd, this);
		window.setOKButtonListener(getThrottleConfirmHandler(rowIndex, newModel, window));
		window.setModal(true);
		window.show();
	}
	
	private void showMergeWindow(int rowIndex, MergeDetailItemModel newModel){
		boolean isNewAdd = rowIndex<0 ? true:false;
		final MergeItemWindow window = new MergeItemWindow( newModel, isNewAdd, this);
		window.setOKButtonListener(getMergeConfirmHandler(rowIndex, newModel, window));
		window.setModal(true);
		window.show();
	}
	
	private boolean isEmptyItem(){
		if((scheduleStore.getModels().size() != 0)||(throttleStore.getModels().size() !=0)||(mergeStore.getModels().size() !=0)){
			return false;
		}else{
			ScheduleUtils.showMesssageBox(UIContext.Constants.wrongInfo(), UIContext.Constants.scheduleCopySourceItemEmpty(),  MessageBox.ERROR);
			return true;
		}
	}
	
	protected void refresh(DailyScheduleDetailItemModel daylyModel, boolean isEdit, boolean showMergeGrid){
		model = daylyModel;
		
		if(model.scheduleDetailItemModels == null){
			model.scheduleDetailItemModels = new ArrayList<ScheduleDetailItemModel>();
			if(!isEdit){
				model.scheduleDetailItemModels.add(getDefaultScheduleItemModel());
			}
		}
		if(model.throttleModels == null){
			model.throttleModels = new ArrayList<ThrottleModel>();
		}
		if(model.mergeModels == null){
			model.mergeModels = new ArrayList<MergeDetailItemModel>();
		}
		
		for (ScheduleDetailItemModel itemModel : model.scheduleDetailItemModels) {
			scheduleStore.add(itemModel);
		}
		
		for (ThrottleModel throttleModel : model.throttleModels) {
			throttleStore.add(throttleModel);
		}
		
		if (showMergeGrid){
			for (MergeDetailItemModel mergeModel : model.mergeModels) {
				mergeStore.add(mergeModel);
			}
		}	
		checkButtonIcon();
	}
	
	protected void checkButtonIcon(){
		boolean isShowJobIcon = false;
		boolean isShowThrottleIcon = false;
		boolean isShowMergeIcon = false;
		
		if(scheduleStore.getModels().size() > 0){
			isShowJobIcon = true;
		}
		if(throttleStore.getModels().size() > 0){
			isShowThrottleIcon = true;
		}
		
		if((mergeStore!=null) && (mergeStore.getModels().size() > 0)){
			isShowMergeIcon = true;
		}
		getParentPanel().setButtonIcon(exceptIndex, isShowJobIcon, isShowThrottleIcon, isShowMergeIcon);
	}
	
	protected boolean validate(){
		return true;
	}
	
	protected void save(){
		//model.setIsEnableSchedule(enableOption.getValue());
		
		if(model == null){
			model = new DailyScheduleDetailItemModel();
		}
		
		model.dayOfweek = this.dayOfWeek;
		
		//Save backup Schedule
		if(model.scheduleDetailItemModels != null){		
			model.scheduleDetailItemModels.clear();
		}else if(scheduleStore.getModels().size() >0){
			model.scheduleDetailItemModels = new ArrayList<ScheduleDetailItemModel>();			
		}	
		
		for (ScheduleDetailItemModel itemModel : scheduleStore.getModels()) {
			model.scheduleDetailItemModels.add(itemModel);
		}
		
		//Save Throttle Schedule
		if(model.throttleModels != null){		
			model.throttleModels.clear();
		}else if(throttleStore.getModels().size() >0){
			model.throttleModels = new ArrayList<ThrottleModel>();			
		}
		
		for (ThrottleModel throttleModel : throttleStore.getModels()) {
			model.throttleModels.add(throttleModel);
		}		
		
		//Save Merge Schedule
		if(model.mergeModels != null){		
			model.mergeModels.clear();
		}else if(mergeStore.getModels().size() >0){
			model.mergeModels = new ArrayList<MergeDetailItemModel>();			
		}
		for (MergeDetailItemModel mergeModel : mergeStore.getModels()) {
			model.mergeModels.add(mergeModel);
		}
	}
	
	protected DailyScheduleDetailItemModel getSchedleModel(){
		return model;
	}
	
	private void updateLinkLabelStyle(Label label){
		 if(isEditable)
			 label.setStyleName("homepage_header_hyperlink_label");
		 else
			 label.setStyleName("homepage_header_hyperlink_label_disable");
		 
		 linkLabelSet.add(label);
	}
	
	public void setEditable(boolean editable) {
		 isEditable = editable;
		 for (Label label : linkLabelSet) {
			 if(isEditable)
				 label.setStyleName("homepage_header_hyperlink_label");
			 else
				 label.setStyleName("homepage_header_hyperlink_label_disable");
		}
	 }
	
	protected void popUpWarning(String message, Listener<MessageBoxEvent> callback) {		
		MessageBox box = new MessageBox();
		box.setIcon(MessageBox.WARNING);
		box.setButtons(Dialog.YESNO);
		box.setMessage(message);
		box.setTitleHtml(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D));
		box.addCallback(callback);
		box.setModal(true);
		Utils.setMessageBoxDebugId(box);
		box.show();	
	}
	
	protected void processAfterCopyOption(boolean isCopySchedule, boolean isCopyThrottling, boolean isCopyMerge){
		
	}
	
	protected abstract ScheduleDetailItemModel getDefaultScheduleItemModel();
	protected abstract ThrottleModel getDefaultThrottleItemModel();
	protected abstract MergeDetailItemModel getDefaultMergeItemModel();
	
	protected abstract String getScheduleLabelTitle();
	protected abstract String getScheduleAddText();
	public abstract String getScheduleAddWindowHeader();
	public abstract String getScheduleEditWindowHeader();
	protected abstract String getThrottleLabelTitle();
	protected abstract String getThrottleAddText();
	protected abstract String getThrottleAddWindowHeader();
	protected abstract String getThrottleEditWindowHeader();
	protected abstract String getMergeLabelTitle();
	protected abstract String getMergeAddText();
	protected abstract String getMergeAddWindowHeader();
	protected abstract String getMergeEditWindowHeader();
	protected abstract String getCopyButtonText();
	protected abstract String getCopyWindowDescription();
	protected abstract String getCopyWindowScheduleOptionText();
	protected abstract String getCopyWindowThrottleOptionText();
	protected abstract String getCopyWindowMergeOptionText();
	
	protected abstract String getThrottleSpeedLabel();
	protected abstract String getThrottleUnit();
	
	protected abstract SchedulePanel getParentPanel();
	public abstract ListStore<FlashFieldSetModel> getJobTypeModels();
	
	protected abstract ClickHandler getScheduleDelHandler(ScheduleDetailItemModel newModel);
	protected abstract ClickHandler getThrottleDelHandler(ThrottleModel newModel);
	protected abstract ClickHandler getMergeDelHandler(MergeDetailItemModel newModel);
	
	protected abstract SelectionListener<ButtonEvent> getScheduleConfirmHandler(int rowIndex,ScheduleDetailItemModel newModel, ScheduleDetailItemWindow window);
	protected abstract SelectionListener<ButtonEvent> getThrottleConfirmHandler(int rowIndex,ThrottleModel newModel, ThrottleItemWindow window);
	protected abstract SelectionListener<ButtonEvent> getMergeConfirmHandler(int rowIndex,MergeDetailItemModel newModel, MergeItemWindow window);
	
	public abstract String getScheduleHelpURL();
	protected abstract String getThrottleHelpURL();
	protected abstract String getMergeHelpURL();
	
	//the sub class uses the api
	protected boolean checkThrottleItems(DayTimeModel start, DayTimeModel end, ThrottleModel exceptModel){
		if(!checkThrottleItemStartEndTime(start, end))
			return false;
		
		List<ThrottleModel> models = getThrottleItems(exceptModel);
		for (ThrottleModel itemModel : models) {
			int result1 = ScheduleUtils.compareDayTimeModel(end, itemModel.startTimeModel);
			int result2 = ScheduleUtils.compareDayTimeModel(start, itemModel.endTimeModel);
			if(result1<=0){
				continue;
			}
			else if(result2>=0){
				continue;
			}
			else{
				//show the message
				String title = UIContext.productNameD2D;
				String message = UIContext.Messages.scheduleThrottleItemOverLap(
						ScheduleUtils.formatTime(itemModel.startTimeModel), 
						ScheduleUtils.formatTime(itemModel.endTimeModel));
				ScheduleUtils.showMesssageBox(title, message, MessageBox.ERROR);
				return false;
			}
		}
		
		return true;
	}
	
	protected boolean checkMergeItems(DayTimeModel start, DayTimeModel end, MergeDetailItemModel exceptModel){
		if(!checkMergeItemStartEndTime(start, end))
			return false;
		
		List<MergeDetailItemModel> models = getMergeItems(exceptModel);
		for (MergeDetailItemModel itemModel : models) {
			int result1 = ScheduleUtils.compareDayTimeModel(end, itemModel.startTimeModel);
			int result2 = ScheduleUtils.compareDayTimeModel(start, itemModel.endTimeModel);
			if(result1<=0){
				continue;
			}
			else if(result2>=0){
				continue;
			}
			else{
				//show the message
				String title = UIContext.productNameD2D;
				String message = UIContext.Messages.scheduleMergeItemOverLap(
						ScheduleUtils.formatTime(itemModel.startTimeModel), 
						ScheduleUtils.formatTime(itemModel.endTimeModel));
				ScheduleUtils.showMesssageBox(title, message, MessageBox.ERROR);
				return false;
			}
		}
		
		return true;
	}
	
	private List<ThrottleModel> getThrottleItems(ThrottleModel exceptModel){
		ArrayList<ThrottleModel> models = new ArrayList<ThrottleModel>();
		//boolean isFirst = true;		
		for (ThrottleModel detail : getThrottleStore().getModels()) {
			
			if(detail == exceptModel){
				continue;
			}
			models.add(detail);
		}
		return models;
	}
	
	private List<MergeDetailItemModel> getMergeItems(MergeDetailItemModel exceptModel){
		ArrayList<MergeDetailItemModel> models = new ArrayList<MergeDetailItemModel>();
		//boolean isFirst = true;		
		for (MergeDetailItemModel detail : getMergeStore().getModels()) {
			
			if(detail == exceptModel){
				continue;
			}
			models.add(detail);
		}
		return models;
	}
	
	private boolean checkThrottleItemStartEndTime(DayTimeModel start, DayTimeModel end){
		if(ScheduleUtils.compareDayTimeModel(start, end)>=0){
			//show the message
			String title = UIContext.productNameD2D;
			String message = UIContext.Constants.scheduleThrottleStartTimeBeforeEndTime();
			ScheduleUtils.showMesssageBox(title, message, MessageBox.ERROR);
			return false;
		}
		int endTimeMins = end.getHour()*60 + end.getMinutes();
		int startTimeMins = start.getHour()*60 + start.getMinutes();
		if(endTimeMins-startTimeMins<15){
			//show the message
			String title = UIContext.productNameD2D;
			String message = UIContext.Constants.scheduleThrottleStartTimeBeforeEndTime15Mins();
			ScheduleUtils.showMesssageBox(title, message, MessageBox.ERROR);
			return false;
		}
		
		return true;
	}
	
	protected boolean checkMergeItemStartEndTime(DayTimeModel start, DayTimeModel end){
		if(ScheduleUtils.compareDayTimeModel(start, end)>=0){
			//show the message
			String title = UIContext.productNameD2D;
			String message = UIContext.Constants.scheduleMergeStartTimeBeforeEndTime();
			ScheduleUtils.showMesssageBox(title, message, MessageBox.ERROR);
			return false;
		}
		int endTimeMins = end.getHour()*60 + end.getMinutes();
		int startTimeMins = start.getHour()*60 + start.getMinutes();
		if(endTimeMins-startTimeMins<15){
			//show the message
			String title = UIContext.productNameD2D;
			String message = UIContext.Constants.scheduleMergeStartTimeBeforeEndTime15Mins();
			ScheduleUtils.showMesssageBox(title, message, MessageBox.ERROR);
			return false;
		}
		
		return true;
	}
	
	protected boolean checkScheduleItemStartEndTime(DayTimeModel start, DayTimeModel end, boolean isRepeatEnabled){
		
		if (isRepeatEnabled)
		{
			if(ScheduleUtils.compareDayTimeModel(start, end)>=0){
				//show the message
				String title = UIContext.productNameD2D;
				String message = UIContext.Constants.scheduleStartTimeBeforeEndTime();
				ScheduleUtils.showMesssageBox(title, message, MessageBox.ERROR);
				return false;
			}
			int endTimeMins = end.getHour()*60 + end.getMinutes();
			int startTimeMins = start.getHour()*60 + start.getMinutes();
			if(endTimeMins-startTimeMins<15){
				//show the message
				String title = UIContext.productNameD2D;
				String message = UIContext.Constants.scheduleStartTimeBeforeEndTime15Mins();
				ScheduleUtils.showMesssageBox(title, message, MessageBox.ERROR);
				return false;
			}
		}
		else
		{
			// if this is not an repeat job, do not let the start time later than 11:57
			// 11:58~11:59 is not allowed
			int startTimeMins = start.getHour()*60 + start.getMinutes();
			if(startTimeMins > 23*60 + 57){
				//show the message
				String title = UIContext.productNameD2D;
				String message = UIContext.Messages.scheduleStartTimeNoLaterThan("11:57 PM");
				ScheduleUtils.showMesssageBox(title, message, MessageBox.ERROR);
				return false;
			}
		}
		
		
		return true;
	}
	
	protected boolean checkScheduleItems(DayTimeModel start, DayTimeModel end, 
			int backupType, boolean isRepeatEnabled, ScheduleDetailItemModel exceptModel){
		if(!checkScheduleItemStartEndTime(start, end, isRepeatEnabled))
			return false;
		
		List<ScheduleDetailItemModel> models = getScheduleItemsByJobType(backupType, exceptModel);
		for (ScheduleDetailItemModel itemModel : models) {
			
			String message = null;
			
			// if both schedule items don't repeat
			if (!isRepeatEnabled && !itemModel.isRepeatEnabled())
			{
				if (ScheduleUtils.compareDayTimeModel(start, itemModel.startTimeModel) == 0)
				{
					message = UIContext.Messages.scheduleItemNoRepeatSameStartTime(
							ScheduleUtils.getJobTypeStr(backupType), 
							ScheduleUtils.formatTime(start));					
				}
				else
				{
					int result1 = ScheduleUtils.compareDayTimeModel(end, itemModel.startTimeModel);
					int result2 = ScheduleUtils.compareDayTimeModel(start, itemModel.endTimeModel);
					if(result1<=0){
					continue;
				}
					else if(result2>=0){
					continue;
				}
					else{
						message = UIContext.Messages.scheduleItemOverLapWithNoRepeat(
							ScheduleUtils.getJobTypeStr(backupType), 
							ScheduleUtils.formatTime(start), 
								ScheduleUtils.formatTime(itemModel.startTimeModel));					
				}
				}
			}
			else
			{
				int result1 = ScheduleUtils.compareDayTimeModel(end, itemModel.startTimeModel);
				int result2 = ScheduleUtils.compareDayTimeModel(start, itemModel.endTimeModel);
				if(result1<=0){
					continue;
				}
				else if(result2>=0){
					continue;
				}
				else{
					message = UIContext.Messages.scheduleItemOverLap(
							ScheduleUtils.getJobTypeStr(backupType), 
							ScheduleUtils.formatTime(itemModel.isRepeatEnabled() ? itemModel.startTimeModel : start), 
							ScheduleUtils.formatTime(itemModel.isRepeatEnabled() ? itemModel.endTimeModel : end));
				}
			}			
			
			
			//show the message
			String title = UIContext.productNameD2D;			
			ScheduleUtils.showMesssageBox(title, message, MessageBox.ERROR);
			return false;
		}
		
		return true;
	}
	
	protected List<ScheduleDetailItemModel> getScheduleItemsByJobType(int jobType, ScheduleDetailItemModel exceptModel){
		ArrayList<ScheduleDetailItemModel> models = new ArrayList<ScheduleDetailItemModel>();
		//boolean isFirst = true;		
		for (ScheduleDetailItemModel detail : getScheduleDetailStore().getModels()) {
			
			if(detail == exceptModel){
				continue;
			}
			
			if(detail.getJobType()== jobType){
				models.add(detail);
			}
		}
		return models;
	}
}