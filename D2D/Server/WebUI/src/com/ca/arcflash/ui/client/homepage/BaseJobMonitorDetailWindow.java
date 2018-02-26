package com.ca.arcflash.ui.client.homepage;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.common.icons.FlashImageBundle;
import com.ca.arcflash.ui.client.model.JobMonitorModel;
import com.ca.arcflash.webservice.constants.JobType;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public abstract class  BaseJobMonitorDetailWindow extends Window {
	protected final CommonServiceAsync service = GWT.create(CommonService.class);
	
	protected static final int REFRESH_INTERVAL = 3;

	protected ProgressBar progressBar;
	protected Label progressLabel;
	protected Timer timer;
	protected FlashImageBundle IconBundle = GWT.create(FlashImageBundle.class);
	protected Image titleImage = new Image();
	
	protected Window thisWindow;
	protected boolean cancelled = false;
	protected JobMonitorModel jobMonitorModel = null;
	protected Button cancelButton= new Button();
	
	protected Label phasevalueLable = new Label();
	protected Label jobNameLabel = new Label();
	protected Label volumeLabel = new Label();
	protected Label startTimeLabel = new Label();
	protected Label elapsedLabel = new Label();
	protected Label estimateLabel = new Label();
	protected Label compressRateLabel = new Label();
	protected Label compressLevel = new Label();
	protected Label dedupeRateLabel = new Label();
	protected Label dedupeEnabled = new Label();
	protected Label totalSpaceLabel = new Label();
	protected Label encryptionLabel = new Label();
	protected Label writeSpeedLimitLabel = new Label();
	protected Label writeThrouhputLabel = new Label();
	protected Label readThroughputLabel = new Label();
	protected Label destinationTypeLabel = new Label();
	protected Label destinationPathLabel = new Label();
	
	protected FlexTable throghtTable = null;
	protected FlexTable titleFlexTable = new FlexTable();
	
	protected String jobType;
	protected Image detailIcon;
	protected LayoutContainer overallReduction;
	
	protected void setupButtonPanel(){
		FlexTable buttonFlexTable = new FlexTable();
		buttonFlexTable.getFlexCellFormatter().setWidth(0, 0, "90");
		buttonFlexTable.getFlexCellFormatter().setWidth(0, 1, "230");
		buttonFlexTable.getFlexCellFormatter().setWidth(0, 2, "90");
		buttonFlexTable.getFlexCellFormatter().setWidth(0, 3, "90");
	    
		Button closeButton= new Button();
		closeButton.setMinWidth(80);
		closeButton.setAutoWidth(true);
		closeButton.setText(UIContext.Constants.close());
		closeButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				thisWindow.hide();
			}
			
		});
		
		Button helpButton = new Button(UIContext.Constants.help());
		helpButton.setMinWidth(80);
		helpButton.setAutoWidth(true);
		helpButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				onHelp();
			}
		});
		
		buttonFlexTable.setWidget(0, 2, closeButton);
		buttonFlexTable.setWidget(0, 3, helpButton);
		
		addButton(closeButton);
		addButton(helpButton);
	}
	
	protected void cancelJob() {
		cancelled = true;
		service.cancelGroupJob(jobMonitorModel.getVmInstanceUUID(), (long)jobMonitorModel.getID(), jobMonitorModel.getJobType(), new BaseAsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				cancelButton.enable();
				cancelled = false;
			}

			@Override
			public void onSuccess(Void result) {
				cancelButton.disable();
				cancelled = true;
			}				
		});
	}

	protected Widget setupJobPhasePanel() {
		FlexTable cancelButtonFlexTable = new FlexTable();
		cancelButtonFlexTable.setWidth("100%");
		cancelButtonFlexTable.setBorderWidth(0);
		cancelButtonFlexTable.setCellPadding(0);
		cancelButtonFlexTable.setCellSpacing(0);
		cancelButtonFlexTable.getFlexCellFormatter().setWidth(0, 1, "90");
//		cancelButtonFlexTable.getFlexCellFormatter().setWidth(0, 2, "90");
		
		cancelButton.setMinWidth(80);
		cancelButton.setAutoWidth(true);
	    cancelButton.setText(UIContext.Constants.jobMonitorCancelButton());
	    cancelButton.ensureDebugId("00d4e871-46ce-4bfc-9922-c107c74c6bb2");
	    cancelButton.disable();
	    cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				if (jobMonitorModel!=null && jobMonitorModel.getID()>0){
					
					MessageBox box = MessageBox.confirm(UIContext.Constants.confirmMsgTitle(), getCancelJobWarningMessage(), new Listener<MessageBoxEvent>() {

						@Override
						public void handleEvent(MessageBoxEvent be) {
							
							if (be.getButtonClicked().getItemId().equals(com.extjs.gxt.ui.client.widget.Dialog.YES)) {
								cancelButton.disable();
								cancelJob();
							}
						}
					});
					Utils.setMessageBoxDebugId(box);
				}
			}
	    });
	    
//	    cancelImage.setVisible(false);
	    
	    HorizontalPanel jobPhasePanel = new HorizontalPanel();
	    jobPhasePanel.setWidth("100%");
	    jobPhasePanel.setTableWidth("100%");
	    
		Label phaseLabel = new Label(UIContext.Constants.jobMonitorLabelPhase());
		phaseLabel.setStyleName("jobMonitor_label");
		phasevalueLable.setStyleName("jobMonitor_value");
		TableData tableData = new TableData();
		tableData.setWidth("15%");
		jobPhasePanel.add(phaseLabel, tableData);
		tableData = new TableData();
		tableData.setWidth("85%");
		jobPhasePanel.add(phasevalueLable, tableData);
	    
		cancelButtonFlexTable.setWidget(0, 0, jobPhasePanel);
		cancelButtonFlexTable.setWidget(0, 1, cancelButton);
		long type = Long.valueOf(jobType);
		if (type == JobType.JOBTYPE_BMR) {
			cancelButton.setVisible(false);
		}
		return cancelButtonFlexTable;
	}
	
	protected Widget setupThoughputPanel(){
		int type = Integer.parseInt(jobType);
		ContentPanel thoughputPanel = new ContentPanel();
		if (type == JobMonitorModel.JOBTYPE_RPS_REPLICATE_IN_BOUND ||
				type == JobMonitorModel.JOBTYPE_RPS_REPLICATE ||
				type == JobMonitorModel.JOBTYPE_RPS_DATA_SEEDING ||
				type == JobMonitorModel.JOBTYPE_RPS_DATA_SEEDING_IN) {
			thoughputPanel.setHeadingHtml(UIContext.Constants.jobMonitorThroughputTitleNetwork());
		} else {			
			thoughputPanel.setHeadingHtml(UIContext.Constants.jobMonitorThroughputTitle());
		}
		thoughputPanel.setAnimCollapse(false);
//		thoughputPanel.setWidth("100%");
		thoughputPanel.setCollapsible(true);
		thoughputPanel.expand();
		thoughputPanel.setBorders(false);
		thoughputPanel.setBodyBorder(false);
		thoughputPanel.setStyleAttribute("padding", "5px");
		thoughputPanel.addListener(Events.Collapse, new Listener<ComponentEvent>() {
			@Override
			public void handleEvent(ComponentEvent be) {
				thisWindow.setHeight(thisWindow.getHeight()- 100);
			}
		});
		thoughputPanel.addListener(Events.Expand, new Listener<ComponentEvent>() {
			@Override
			public void handleEvent(ComponentEvent be) {
				thisWindow.setHeight(thisWindow.getHeight()+ 100);
			}
		});
		
		throghtTable = new FlexTable();
		throghtTable.setWidth("510px");
		throghtTable.setCellPadding(4);
		throghtTable.setCellSpacing(4);
		throghtTable.getColumnFormatter().setWidth(0, "270px");
//		throghtTable.getColumnFormatter().setWidth(1, "60%");
//		throghtTable.getColumnFormatter().setWidth(2, "10%");
//		throghtTable.getColumnFormatter().setStyleName(1, "jobmonitor_window_value_column");
		Label label;
		
		label = new Label();
		label.setStyleName("jobMonitor_label");
		if (type == JobMonitorModel.JOBTYPE_RPS_REPLICATE || type == JobMonitorModel.JOBTYPE_RPS_REPLICATE_IN_BOUND
				|| type == JobMonitorModel.JOBTYPE_RPS_DATA_SEEDING || type == JobMonitorModel.JOBTYPE_RPS_DATA_SEEDING_IN) {
			label.setHtml(UIContext.Constants.jobMonitorBandwidthThrottle());
		} else {			
			label.setHtml(UIContext.Constants.jobMonitorWriteSpeedLimit());
		}
		throghtTable.setWidget(0, 0, label);
		writeSpeedLimitLabel.setStyleName("jobMonitor_value");
		throghtTable.setWidget(0, 1, writeSpeedLimitLabel);
		throghtTable.getRowFormatter().setVisible(0, false); // don't show "Write Speed Limit"
		
		label = new Label();
		label.setStyleName("jobMonitor_label");
		label.setHtml(UIContext.Constants.jobMonitorReadThroughput());
		throghtTable.setWidget(2, 0, label);
		readThroughputLabel.setStyleName("jobMonitor_value");
		throghtTable.setWidget(2, 1, readThroughputLabel);
		
		thoughputPanel.add(throghtTable);
		return thoughputPanel;
	}
	
	protected boolean enableCompress(long compressLevel) {
		return compressLevel != 0;
	}

	protected double adjustRatio(double ratio) {
		if(ratio < 0.0001) 	// This is used for percentage and we want to show 2 decimals like 0.01%
			ratio = 0.0001;
		if(ratio > 1.0)
			ratio = 1.0;
		return ratio;
	}
	
	protected String percentFormat(double n){
		String result = "";
		NumberFormat fmt = NumberFormat.getFormat("0.00");

		if(n > 1.0){
			n = 1.0;
		}
		else if (n < 0.0){
			n = 0.0;
		}
		
		try{			
			result = fmt.format(n * 100) + "%";		
		}
		catch (IllegalArgumentException e) {
			result = String.valueOf(n * 100) + "%";
		}
		
		return result;
	}
	
	protected void startJobMonitorTimer() {
		refresh(null);
		
		timer = new Timer() {
			public void run() {
				refresh(null);
			}
		};
		timer.schedule(REFRESH_INTERVAL*1000);
		timer.scheduleRepeating(REFRESH_INTERVAL*1000);
		
	}
	
	protected Widget setupTitlePanel(){
		long type = Long.parseLong(jobType);
		ContentPanel titlePanel = new ContentPanel();
		titlePanel.setHeaderVisible(false);
//		titlePanel.setWidth("100%");
		titlePanel.expand();
		
		titleFlexTable.getFlexCellFormatter().setWidth(0, 0, "50px");
		
//		HorizontalPanel jobPhasePanel = new HorizontalPanel();
		VerticalPanel verticalPanel = new VerticalPanel();
//		Label phaseLabel = new Label(UIContext.Constants.jobMonitorLabelPhase());
		if(type == JobMonitorModel.JOBTYPE_COPY) {
			titleImage = AbstractImagePrototype.create(IconBundle.tasks_recovery()).createImage();
		}else if(type == JobMonitorModel.JOBTYPE_RESTORE){
			titleImage = AbstractImagePrototype.create(IconBundle.tasks_restore()).createImage();
		}else
			titleImage = AbstractImagePrototype.create(IconBundle.tasks_backup()).createImage();
		
		
		titlePanel.setStyleAttribute("padding", "5px");
		titlePanel.setBodyBorder(false);
		titlePanel.setBorders(false);
//		phaseLabel.setStyleName("jobMonitor_label");
//		phasevalueLable.setStyleName("jobMonitor_value");
		jobNameLabel.setStyleName("jobmonitor_window_jobname");
		
//		jobPhasePanel.add(phaseLabel);
//		jobPhasePanel.add(phasevalueLable);
		
		
		verticalPanel.add(jobNameLabel);
//		verticalPanel.add(jobPhasePanel);
		verticalPanel.setWidth("100%");
		
		titleFlexTable.setWidth("100%");
		titleFlexTable.setWidget(0, 0, titleImage);
		titleFlexTable.setWidget(0, 1, jobNameLabel);
		
		titlePanel.add(titleFlexTable);
		
		return titlePanel;
	}
	
	protected void computeCompressionAndDedupe(JobMonitorModel jobMonitorModel){
		long totalSizeRead = jobMonitorModel.getTotalSizeRead();
		double totalSizeWritten = (double)jobMonitorModel.getTotalSizeWritten();
		long totalUniqueData = jobMonitorModel.getTotalUniqueData();
		Boolean dedupe = jobMonitorModel.isDedupe();
		boolean enableDedeup = dedupe != null && dedupe;
		long compressLevel = jobMonitorModel.getCompressLevel();
		this.compressLevel.setHtml(Utils.getCompressLevel(compressLevel));
		
		// only calculate compression ratio when data is written
		if(totalSizeRead>0 && totalSizeWritten>0) // if no compress, don't show compress rate.
		{			
			double zero = 0.0;	// used to for comparing to zero.
			double dedupeRatio = 0.0;
			double dedupeRatioReduced = 0.0;
			double compressRatio = 0.0;
			double compressRatioReduced = 0.0;
			
			// totalUniqueData might be 0, if all data blocks are existing on dedupe data store
			// so 0 is a valid value.
			if(totalUniqueData >= zero && enableDedeup) {
				dedupeRatio = ((double)totalUniqueData) / totalSizeRead;
				dedupeRatio = adjustRatio(dedupeRatio);				
				dedupeRatioReduced = 1 - dedupeRatio;	// on UI we display how much space reduced after dedupe 				
				dedupeRateLabel.setHtml(percentFormat(dedupeRatioReduced));
				
				// only when there is any data written, we can have the compression ratio. otherwise it should be N/A
				if(totalUniqueData > zero && enableCompress(compressLevel)){
					compressRatio = ((double) totalSizeWritten) / totalUniqueData;
					compressRatio = adjustRatio(compressRatio);
					compressRatioReduced = 1 - compressRatio;	// on UI we display how much space reduced after compression
					compressRateLabel.setHtml(percentFormat(compressRatioReduced));					
				}else if(totalUniqueData == zero && enableCompress(compressLevel)){
					compressRateLabel.setHtml(percentFormat(0.9999));
				}else{
					compressRateLabel.setHtml(UIContext.Constants.NA());
				}				
				
			}else {
				if(enableCompress(compressLevel)){
					compressRatio = totalSizeWritten / totalSizeRead;
					compressRatio = adjustRatio(compressRatio);					
					compressRatioReduced = 1-compressRatio;					
					compressRateLabel.setHtml(percentFormat(compressRatioReduced));
				}else {
					compressRateLabel.setHtml(UIContext.Constants.NA());
				}
				dedupeRateLabel.setHtml(UIContext.Constants.NA());
			}
			
			double totalRatio;
			String overallDetail = null;
			if (dedupeRatioReduced > zero && compressRatioReduced > zero) {
				// both dedupe and compression happened
				totalRatio = 1 - dedupeRatio * compressRatio;	// totally how much space reduced after dedupe and compression
				String dedupDetail=UIContext.Messages.jobMonitorReducedRatioDetail(Utils.bytes2String(totalSizeRead), dedupeRateLabel.getHtml(), Utils.bytes2String(totalUniqueData), UIContext.Constants.jobMonitorDedupe());
				String compressDetail=UIContext.Messages.jobMonitorFurtherReducedRatioDetail(Utils.bytes2String(totalUniqueData), compressRateLabel.getHtml(), Utils.bytes2String(jobMonitorModel.getTotalSizeWritten()), UIContext.Constants.settingsLabelCompression());
				overallDetail=dedupDetail+compressDetail;
			} else if (dedupeRatioReduced > zero) {
				// only dedupe happened
				totalRatio = dedupeRatioReduced;
				String dedupDetail=UIContext.Messages.jobMonitorReducedRatioDetail(Utils.bytes2String(totalSizeRead), dedupeRateLabel.getHtml(), Utils.bytes2String(totalUniqueData), UIContext.Constants.jobMonitorDedupe());
				overallDetail=dedupDetail;
			} else if (compressRatioReduced > zero) {
				// only compression happened
				totalRatio = compressRatioReduced;
				String compressDetail=UIContext.Messages.jobMonitorReducedRatioDetail(Utils.bytes2String(totalSizeRead), compressRateLabel.getHtml(), Utils.bytes2String(jobMonitorModel.getTotalSizeWritten()), UIContext.Constants.settingsLabelCompression());
				overallDetail=compressDetail;
			} else {
				// no dedupe / no compression happened
				totalRatio = totalSizeWritten / totalSizeRead;
				totalRatio = adjustRatio(totalRatio);
				totalRatio = 1 - totalRatio;
			}
			
			totalSpaceLabel.setHtml(percentFormat(totalRatio));
			overallDetail+=UIContext.Messages.jobMonitorOverallReducedDetail(totalSpaceLabel.getHtml());
			detailIcon.setVisible(true);
			Utils.addToolTip(overallReduction, overallDetail);
		}
		else {
			compressRateLabel.setHtml(UIContext.Constants.NA());
			totalSpaceLabel.setHtml(UIContext.Constants.NA());
			dedupeRateLabel.setHtml(UIContext.Constants.NA());
		}
		
		if(enableDedeup){
			dedupeEnabled.setHtml(UIContext.Constants.jobMonitorValueEnabled());
		}else {
			dedupeEnabled.setHtml(UIContext.Constants.jobMonitorValueDisabled());
			dedupeRateLabel.setHtml(UIContext.Constants.NA());
		}
	}
	
	protected abstract void onHelp();
	
	public abstract void refresh(Object data);
	public String getCancelJobWarningMessage(){
		return UIContext.Constants.jobMonitorCancelAlertMessage();
	}
}
