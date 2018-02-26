package com.ca.arcflash.ui.client.coldstandby;

import java.util.Date;

import com.ca.arcflash.jobscript.replication.RepJobMonitor;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.HelpTopics;
import com.ca.arcflash.ui.client.common.Utils;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class JobMonitorDetailWindow extends Window{
	protected final ColdStandbyServiceAsync service = GWT.create(ColdStandbyService.class);
	protected Window thisWindow;
	protected Image titleImage = new Image();
	
	protected Label jobNameLabel = new Label();
	protected FlexTable titleFlexTable = new FlexTable();
	protected ProgressBar bar;
	protected RepJobMonitor jobMonitorModel = null;
	
	protected Label phaseLabel = new Label();
	protected Button cancelButton= new Button();
	
	protected Label sessionLabel = new Label();
	protected Label startTimeLabel = new Label();
	protected Label elapsedLabel = new Label();
	protected Label throughputLabel = new Label();
	protected Label estimatedRemainTimeLabel = new Label();
	
	protected Label sessionConvertedNumLabel = new Label();
	protected Label totalStartTimeLabel = new Label();
	protected Label totalElapsedTimeLabel = new Label();
	protected Label totalTimeRemainingLabel = new Label();
	protected Label pendSessionNumberLabel = new Label();
	
	protected int endsCounter = 0;
	
	
	protected static final int HEIGHT = 520;
	protected static final int WIDTH = 500;
	protected Label processedLabel;
	
	ContentPanel currentSessionPanel;
	
	public JobMonitorDetailWindow() {
		thisWindow = this;
		this.setResizable(true);
		this.setWidth(WIDTH);
		this.setHeight(HEIGHT);
		
		setHeadingHtml(VCMMessages.replicaJobDetailWindowTitle());
		
		setBodyStyle("background-color: white;");
		setScrollMode(Scroll.AUTOY);
		
		setLayout(new RowLayout(Orientation.VERTICAL));
		
		add(setupTitlePanel()); // title
		add(setupCurrentSessionPanel()); // progress
		add(setupAllSessionPanel()); // throughput
		
		setupButtonPanel();
	}
	
	protected Widget setupTitlePanel(){
		
		ContentPanel titlePanel = new ContentPanel();
		titlePanel.setHeaderVisible(false);
		titlePanel.expand();
		
		titleImage = AbstractImagePrototype.create(UIContext.IconBundle.vcm_virtual_standy()).createImage();
		
		titlePanel.setStyleAttribute("padding", "5px");
		titlePanel.setBodyBorder(false);
		titlePanel.setBorders(false);
		
		jobNameLabel.setStyleName("jobmonitor_window_jobname");
		jobNameLabel.setText(VCMMessages.productName);
		
		FlexTable titleTopFlexTable = new FlexTable();
		titleTopFlexTable.getFlexCellFormatter().setWidth(0, 0, "50px");
		titleTopFlexTable.setWidth("100%");
		titleTopFlexTable.setWidget(0, 0, titleImage);
		titleTopFlexTable.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
		titleTopFlexTable.setWidget(0, 1, jobNameLabel);
		
		titleFlexTable.setWidth("100%");
		titleFlexTable.setWidget(0, 0, titleTopFlexTable);
		
		titleFlexTable.setWidget(1, 0, setupJobPhasePanel());
		
		processedLabel = new Label();
		processedLabel.setStyleName("jobMonitor_value");
		titleFlexTable.setWidget(2, 0, processedLabel);
		
		titleFlexTable.setWidget(3, 0, createProgressCellWidget());
		
		titlePanel.add(titleFlexTable);
		
		return titlePanel;
	}

	private Widget createProgressCellWidget() {
		FlexTable progressCellWidget = new FlexTable();
		progressCellWidget.setWidth("100%");
		progressCellWidget.setCellSpacing(0);
		progressCellWidget.getFlexCellFormatter().setWidth(0, 0, "10px");
		progressCellWidget.setWidget(0, 0, new Label());

		bar = new ProgressBar() {
			@Override
			public void updateText(String text) {
				text = text != null ? text : "&#160;";
				if (rendered) {
					El inner = el().firstChild();
					El textBackElem = inner.childNode(1);// .firstChild();
					textBackElem.setInnerHtml(text);
				}
			}
		};
		bar.setWidth("99%");
		bar.setVisible(true);

		progressCellWidget.setWidget(0, 1, bar);
		return progressCellWidget;
	}
	
	public Widget setupJobPhasePanel() {
		
		FlexTable cancelButtonFlexTable = new FlexTable();
		cancelButtonFlexTable.setWidth("100%");
		cancelButtonFlexTable.setCellSpacing(0);
		cancelButtonFlexTable.getFlexCellFormatter().setWidth(0, 1, "110");
		cancelButton.ensureDebugId("aca40668-a7a3-40eb-8e3a-b8af45fddfc3");
		cancelButton.setWidth(80);
	    cancelButton.setText(UIContext.Constants.jobMonitorCancelButton());
	    cancelButton.disable();
	    cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				if (jobMonitorModel!=null && jobMonitorModel.getId()>0){
					
					MessageBox box = MessageBox.confirm(UIContext.Constants.confirmMsgTitle(), UIContext.Constants.jobMonitorCancelAlertMessage(),
							new Listener<MessageBoxEvent>() {

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
	    
	    HorizontalPanel jobPhasePanel = new HorizontalPanel();
		Label label = new Label(UIContext.Constants.jobMonitorLabelPhase()+" ");
		label.setStyleName("jobMonitor_label");
		phaseLabel.setStyleName("jobMonitor_value");
		phaseLabel.setText(UIContext.Constants.NA());
		jobPhasePanel.add(label);
		jobPhasePanel.add(phaseLabel);
		
		cancelButtonFlexTable.setWidget(0, 0, jobPhasePanel);
	    cancelButtonFlexTable.setWidget(0, 1, cancelButton);
		return cancelButtonFlexTable;
	}
	
	// JobMonitorDetailWindow only extends by ConversionJobMonitorDetailWindowForEdge
	// so cancelJob will be override by ConversionJobMonitorDetailWindowForEdge.cancelJob()
	protected void cancelJob() {
		String vmInstanceUUID = ColdStandbyManager.getVMInstanceUUID();
		service.cancelReplication(vmInstanceUUID,new BaseAsyncCallback<Void>(UIContext.productNameVCM){

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				cancelButton.enable();
			}

			@Override
			public void onSuccess(Void result) {
				cancelButton.disable();
				thisWindow.mask(UIContext.Constants.JobMonitorCancelMaskText());
			}						
		});
	}

	protected Widget setupCurrentSessionPanel(){
		currentSessionPanel = new ContentPanel();
		currentSessionPanel.setHeadingHtml(UIContext.Constants.replicaJobDetailCurrentSession());
		currentSessionPanel.setAnimCollapse(true);
		currentSessionPanel.setCollapsible(true);
		currentSessionPanel.expand();
		currentSessionPanel.setBorders(false);
		currentSessionPanel.setBodyBorder(false);
		currentSessionPanel.setStyleAttribute("padding", "5px");
		currentSessionPanel.addListener(Events.Collapse, new Listener<ComponentEvent>() {
			@Override
			public void handleEvent(ComponentEvent be) {
				thisWindow.setHeight(thisWindow.getHeight()- 130);
			}
		});
		currentSessionPanel.addListener(Events.Expand, new Listener<ComponentEvent>() {
			@Override
			public void handleEvent(ComponentEvent be) {
				thisWindow.setHeight(thisWindow.getHeight()+ 130);
			}
		});
		FlexTable currentSessionTable = new FlexTable(); 
		currentSessionTable.setWidth("100%");
		currentSessionTable.setCellPadding(4);
		currentSessionTable.setCellSpacing(4);
		currentSessionTable.getColumnFormatter().setWidth(0, "40%");
		currentSessionTable.getColumnFormatter().setWidth(1, "60%");
		currentSessionTable.getColumnFormatter().setStyleName(1, "jobmonitor_window_value_column");
		Label label;
		
		label = new Label();
		label.setStyleName("jobMonitor_label");
		label.setText(UIContext.Constants.replicaSourceSession());
		currentSessionTable.setWidget(0, 0, label);
		sessionLabel.setStyleName("jobMonitor_value");
		sessionLabel.setText(UIContext.Constants.NA());
		currentSessionTable.setWidget(0, 1, sessionLabel);
		
		label = new Label();
		label.setStyleName("jobMonitor_label");
		label.setText(UIContext.Constants.jobMonitorStartTime());
		currentSessionTable.setWidget(1, 0, label);
		startTimeLabel.setStyleName("jobMonitor_value");
		startTimeLabel.setText(UIContext.Constants.NA());
		currentSessionTable.setWidget(1, 1, startTimeLabel);
		
		label = new Label();
		label.setStyleName("jobMonitor_label");
		label.setText(UIContext.Constants.jobMonitorLabelElapsedTime());
		currentSessionTable.setWidget(2, 0, label);
		elapsedLabel.setStyleName("jobMonitor_value");
		elapsedLabel.setText(UIContext.Constants.NA());
		currentSessionTable.setWidget(2, 1, elapsedLabel);
		
		label = new Label();
		label.setStyleName("jobMonitor_label");
		label.setText(UIContext.Constants.replicaJobThroughput());
		currentSessionTable.setWidget(4, 0, label);
		throughputLabel.setStyleName("jobMonitor_value");
		throughputLabel.setText(UIContext.Constants.NA());
		currentSessionTable.setWidget(4, 1, throughputLabel);
		
		label = new Label();
		label.setStyleName("jobMonitor_label");
		label.setText(UIContext.Constants.jobMonitorEstimatedTime());
		currentSessionTable.setWidget(5, 0, label);
		estimatedRemainTimeLabel.setStyleName("jobMonitor_value");
		estimatedRemainTimeLabel.setText(UIContext.Constants.NA());
		currentSessionTable.setWidget(5, 1, estimatedRemainTimeLabel);
		
		currentSessionPanel.add(currentSessionTable);
		return currentSessionPanel;
	}
	
	protected Widget setupAllSessionPanel(){
		ContentPanel allSessionPanel = new ContentPanel();
		allSessionPanel.setHeadingHtml(UIContext.Constants.replicaJobDetailAllSession());
		allSessionPanel.setAnimCollapse(true);
		allSessionPanel.setCollapsible(true);
		allSessionPanel.expand();
		allSessionPanel.setBorders(false);
		allSessionPanel.setBodyBorder(false);
		allSessionPanel.setStyleAttribute("padding", "5px");
		allSessionPanel.addListener(Events.Collapse, new Listener<ComponentEvent>() {
			@Override
			public void handleEvent(ComponentEvent be) {
				thisWindow.setHeight(thisWindow.getHeight()- 125);
			}
		});
		allSessionPanel.addListener(Events.Expand, new Listener<ComponentEvent>() {
			@Override
			public void handleEvent(ComponentEvent be) {
				thisWindow.setHeight(thisWindow.getHeight()+ 125);
			}
		});
		
		FlexTable allSessionTable = new FlexTable();
		allSessionTable.setWidth("100%");
		allSessionTable.setCellPadding(4);
		allSessionTable.setCellSpacing(4);
		allSessionTable.getColumnFormatter().setWidth(0, "40%");
		allSessionTable.getColumnFormatter().setWidth(1, "60%");
		allSessionTable.getColumnFormatter().setStyleName(1, "jobmonitor_window_value_column");
		
		int row = 0;
		Label label = new Label();
		label.setStyleName("jobMonitor_label");
		label.setText(UIContext.Constants.replicaJobDetailConvertedNum());
		allSessionTable.setWidget(row, 0, label);
		sessionConvertedNumLabel.setStyleName("jobMonitor_value");
		sessionConvertedNumLabel.setText(UIContext.Constants.NA());
		allSessionTable.setWidget(row, 1, sessionConvertedNumLabel);

		row++;
		label = new Label();
		label.setStyleName("jobMonitor_label");
		label.setText(UIContext.Constants.jobMonitorStartTime());
		allSessionTable.setWidget(row, 0, label);
		totalStartTimeLabel.setStyleName("jobMonitor_value");
		totalStartTimeLabel.setText(UIContext.Constants.NA());
		allSessionTable.setWidget(row, 1, totalStartTimeLabel);
		
		row++;
		label = new Label();
		label.setStyleName("jobMonitor_label");
		label.setText(UIContext.Constants.jobMonitorLabelElapsedTime());
		allSessionTable.setWidget(row, 0, label);
		totalElapsedTimeLabel.setStyleName("jobMonitor_value");
		totalElapsedTimeLabel.setText(UIContext.Constants.NA());
		allSessionTable.setWidget(row, 1, totalElapsedTimeLabel);

		row++;
		label = new Label();
		label.setStyleName("jobMonitor_label");
		label.setText(UIContext.Constants.jobMonitorEstimatedTime());
		allSessionTable.setWidget(row, 0, label);
		totalTimeRemainingLabel.setStyleName("jobMonitor_value");
		totalTimeRemainingLabel.setText(UIContext.Constants.NA());
		allSessionTable.setWidget(row, 1, totalTimeRemainingLabel);
		
		row++;
		label = new Label();
		label.setStyleName("jobMonitor_label");
		label.setText(UIContext.Constants.replicaJobSessionsPending()); //"Number of Sessions Pending:"
		allSessionTable.setWidget(row, 0, label);
		pendSessionNumberLabel.setStyleName("jobMonitor_value");
		pendSessionNumberLabel.setText(UIContext.Constants.NA());
		allSessionTable.setWidget(row, 1, pendSessionNumberLabel);
		
		allSessionPanel.add(allSessionTable);
		return allSessionPanel;
	}
	public void refresh(RepJobMonitor result) {
		jobMonitorModel = result;
		
		if (result == null || result.getId()<0){
			
			cancelButton.disable();
//			cancelImage.setVisible(false);
			thisWindow.hide();
			return;
		}
		
		
		if (result.getRepPhase() == RepJobMonitor.REP_JOB_PHASE_EXIT){
			
			endsCounter++;
			if (endsCounter>=3){
				//GXT issue. Can't reset to 0.
				bar.updateProgress(0.0026,""); 
				cancelButton.disable();
//				cancelImage.setVisible(false);
				thisWindow.hide();
			}
			
			return;
		}
		
		int provisionPoint = result.getCurrentSnapshotCount() + 1;
		currentSessionPanel.setHeadingHtml(UIContext.Constants.replicaJobDetailCurrentSession() + " " + provisionPoint);
		
		if (result.getRepPhase() == RepJobMonitor.REP_JOB_PHASE_CANCELLING) {
			cancelButton.disable();
//			cancelImage.setVisible(true);
		}
		else if(result.getRepPhase() == RepJobMonitor.REP_JOB_PHASE_DATA_TRANSFER){
			cancelButton.enable();
//			cancelImage.setVisible(false);
		}
		else {
			cancelButton.disable();
//			cancelImage.setVisible(false);
		}
		
		endsCounter = 0;
		
		if (result.getRepPhase() == RepJobMonitor.REP_JOB_PHASE_SESSION_END || result.getRepPhase() == RepJobMonitor.REP_JOB_PHASE_SESSION_START){
			result.setRepElapsedTime(0);
			result.setRepStartTime(0);
			result.setRepStartNanoTime(0);
			result.setRepTotalSize(0);
			result.setRepTransedSize(0);
		}
		
		if(result.getRepPhase() == RepJobMonitor.REP_JOB_GET_CONNECTION){
			String tmp = UIContext.Messages.coldStandbyReplicaJobConnecting(result.getTargetMachine());
			phaseLabel.setText(tmp); 
		}else{
			phaseLabel.setText(Utils.replicaJobPhase2String(result.getRepPhase()));
		}	
		
		long totalSize = result.getRepTotalSize();
		long totalTransSize = result.getRepTransedSize();
		long transSizeAfterResume = result.getRepTransAfterResume();
		
		int numOfToRep = jobMonitorModel.getToRepSessions().length;
		int totalSessionNumbers = jobMonitorModel.getTotalSessionNumbers();
		int convertingNum = totalSessionNumbers - numOfToRep;
		if(totalSessionNumbers > 0 && convertingNum > 0) {
			processedLabel.setText(UIContext.Messages.virtualConversionProcessingAndTotal(convertingNum, totalSessionNumbers));
		} else
			processedLabel.setText("");
		
		String transferedSizeLabel = Utils.bytes2String(totalTransSize);
		String totalSizeLabel = Utils.bytes2String(totalSize);
		
		// to deal with the immediately started new job after first job fails. 
		//We need to refresh the process bar when it is just beginning
		/*if(result.getJobPhase() == JobMonitorModel.PHASE_BACKUP_PHASE_START_BACKUP || 
				result.getJobPhase() == JobMonitorModel.PHASE_BACKUP_PHASE_TAKING_SNAPSHOT ||
				result.getJobPhase() == JobMonitorModel.PHASE_BACKUP_PHASE_CREATING_VIRTUAL_DISKS)
		{
			bar.updateProgress(0.0026,""); 
		}
		else*/ 
		if (totalSize>0){
			double percent = ((double)totalTransSize)/totalSize;
			if (percent>=1)
				bar.updateProgress(1, UIContext.Messages.jobMonitorProgressBarLabel(100, transferedSizeLabel, totalSizeLabel));
			else
				bar.updateProgress(percent, UIContext.Messages.jobMonitorProgressBarLabel((int)(percent*100), transferedSizeLabel, totalSizeLabel));
		}else if(result.getRepPhase() == RepJobMonitor.REP_JOB_GET_CONNECTION){
			bar.updateProgress(0, UIContext.Constants.replicaJobConnect());
		}else{
			bar.updateProgress(0, UIContext.Constants.NA());
		}
		//set the labels for current session part
		if(result.getRepSessionName() == null || result.getRepSessionName().length() == 0 || result.getRepSessionBackupTime() == 0)
			sessionLabel.setText(Utils.convert2UILabel(result.getRepSessionName()));
		else
			sessionLabel.setText(UIContext.Messages.virtualConversionSessionCreatedAt(result.getRepSessionName()));
		
		if (result.getRepStartTime() != 0)
			startTimeLabel.setText(Utils.formatDateToServerTime(new Date(result.getRepStartTime())));
		else 
			startTimeLabel.setText(UIContext.Constants.NA());
		
		if (result.getRepElapsedTime()!=0)
			elapsedLabel.setText(Utils.milseconds2String(result.getRepElapsedTime()));
		else 
			elapsedLabel.setText(UIContext.Constants.NA());
			
		long throughput = 0; //bytes per millisecond
		double currentSessionRemainTime = 0;
		if (result.getRepElapsedTime()>1000){
			throughput = (transSizeAfterResume / result.getRepElapsedTime());
			throughputLabel.setText(Utils.bytes2MBString(throughput*1000*60));
			
			
			if (totalSize>0 && throughput >0){
				double remainSize = totalSize - totalTransSize;
				currentSessionRemainTime = remainSize/throughput;
				estimatedRemainTimeLabel.setText(Utils.milseconds2String((long)currentSessionRemainTime));
			}else
				estimatedRemainTimeLabel.setText(UIContext.Constants.NA());
		}else{
			throughputLabel.setText(UIContext.Constants.NA());
			estimatedRemainTimeLabel.setText(UIContext.Constants.NA());
		}
		
		//set the labels for "all sessions" part
		if(convertingNum > 0)
			sessionConvertedNumLabel.setText((convertingNum - 1)+"");

		// Start time
		if (result.getRepJobStartTime() > 0) {
			totalStartTimeLabel.setText(Utils.formatDateToServerTime(new Date(result.getRepJobStartTime())));
		} else {
			totalStartTimeLabel.setText(UIContext.Constants.NA());
		}

		//Elapsed time:
		if(result.getRepJobElapsedTime() > 0)
			totalElapsedTimeLabel.setText(Utils.milseconds2String(result.getRepJobElapsedTime()));
		else
			totalElapsedTimeLabel.setText(UIContext.Constants.NA());
		
		//Estimated time remaining:
		if((result.getToRepSessionsSize() > 0 || numOfToRep == 0) && throughput > 0) {
			double remainTime = result.getToRepSessionsSize()/throughput + currentSessionRemainTime;
			totalTimeRemainingLabel.setText(Utils.milseconds2String((long)remainTime));
		}
		else
			totalTimeRemainingLabel.setText(UIContext.Constants.NA());
		
		//Number of Sessions Pending
		int pendingSessions = result.getToRepSessions().length;
		if(pendingSessions > 0 
			|| (result.getRepSessionName()!= null && result.getRepSessionName() != "")){
			pendSessionNumberLabel.setText(pendingSessions+"");
		}else{
			pendSessionNumberLabel.setText(UIContext.Constants.NA());
		}
	}

	protected void setupButtonPanel(){
	    
		Button closeButton= new Button();
		closeButton.setWidth(80);
		closeButton.setText(UIContext.Constants.close());
		closeButton.ensureDebugId("4dda9a67-22c4-4ade-b22b-d3b0ba554c72");
		closeButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				thisWindow.hide();
			}
			
		});
		
		Button helpButton = new Button(UIContext.Constants.help());
		helpButton.setWidth(80);
		helpButton.ensureDebugId("c42ad45f-ed18-4939-957c-53be21ca3876");
		helpButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				onHelp();
			}
		});
		
		addButton(closeButton);
		addButton(helpButton);
	}
	
	protected void onHelp() {
		HelpTopics.showHelpURL(UIContext.externalLinks.getVirtualStandbyJobMonitorURL());
	}
}
