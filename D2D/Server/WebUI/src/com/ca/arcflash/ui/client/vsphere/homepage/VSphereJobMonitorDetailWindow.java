package com.ca.arcflash.ui.client.vsphere.homepage;

import java.util.Date;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.HelpTopics;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.homepage.BaseJobMonitorDetailWindow;
import com.ca.arcflash.ui.client.model.BackupVMModel;
import com.ca.arcflash.ui.client.model.JobMonitorModel;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class VSphereJobMonitorDetailWindow extends BaseJobMonitorDetailWindow{
	//private static final int HEIGHT = 720;
	private static final int HEIGHT = 580;
	private static final int WIDTH = 550;
	
	protected FlexTable progressTable = new FlexTable();
	
	//For GRT catalog
//	private Label currentFolderLabel = new Label();
	private Label totalFolderLabel = new Label();
	private Label processedFolderLabel = new Label();
	protected long jobId;
	protected boolean isShowTranportMode = true;
	protected boolean isVAppJob = false;
	private Label transferModeLabel = new Label();
	private Label destinationRPSServerLabel = new Label();
	private Label targetDatastoreNameLabel = new Label();
	private String vmInstanceUUID;

	public VSphereJobMonitorDetailWindow(JobMonitorModel model) {
		thisWindow = this;
		cancelButton.ensureDebugId("23155782-0A97-4b77-AE7A-5270A57B8107");
		this.jobType = String.valueOf(model.getJobType());
		this.jobId = model.getID();
		this.vmInstanceUUID = model.getVmInstanceUUID();
		setHeaderText(model);
		this.setResizable(false);
		this.setWidth(WIDTH);
		this.setHeight(HEIGHT);
		
		setBodyStyle("background-color: white;");
		setScrollMode(Scroll.AUTOY);
		
		setLayout(new RowLayout(Orientation.VERTICAL));
		
		// add four parts.
	    add(setupTitlePanel()); // title
	    add(setupProgressPanel()); // progress
	    long type = Long.parseLong(jobType);
	    if(!(type == JobMonitorModel.JOBTYPE_VM_CATALOG_FS 
	    		|| type == JobMonitorModel.JOBTYPE_CATALOG_GRT
	    		|| type == JobMonitorModel.JOBTYPE_VM_CATALOG_FS_ONDEMAND))
	    {	
	    	add(setupThoughputPanel()); // throughput
	    }	
	    setupButtonPanel();
	    startJobMonitorTimer();
	    isShowTranportMode = UIContext.backupVM!=null && 
	    					 (UIContext.backupVM.getVMType() == BackupVMModel.Type.VMware.ordinal() ||
	    					  UIContext.backupVM.getVMType() == BackupVMModel.Type.VMware_VApp.ordinal());
	    isVAppJob = UIContext.backupVM!=null && UIContext.backupVM.getVMType() == BackupVMModel.Type.VMware_VApp.ordinal();
	}

	public String getVmInstanceUUID() {
		return vmInstanceUUID;
	}

	public void setVmInstanceUUID(String vmInstanceUUID) {
		this.vmInstanceUUID = vmInstanceUUID;
	}
	
	@Override
	public void refresh(Object data) {
		service.getVMJobMonitor(vmInstanceUUID,jobType,jobId,new BaseAsyncCallback<JobMonitorModel>() {
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);

			}

			@Override
			public void onSuccess(JobMonitorModel result) {
				jobMonitorModel = result;
				PupulateUI();
			}
		});
	}
	
	private void setHeaderText(JobMonitorModel jobMonitorModel){
		// dynamic set the window heading
		if(getHeadingHtml()==null || getHeadingHtml().trim().length()<=0) {
			String heading = null;
			if( jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_VM) {
				heading = UIContext.Constants.jobMonitorBackupWindow();
				if(isVAppJob) {
					heading = "Backup vApp";
				}
			}
			else if( jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_RESTORE || jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_RECOVERY_VM) {
				heading = UIContext.Constants.JobMonitorRestoreWindow();
				if(isVAppJob) {
					heading = "Restore vApp";
				}
			}
			else if( jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_COPY)
				heading = UIContext.Constants.jobMonitorExportWindow();
			else if( jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_APP
					|| jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_FS
					|| jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_GRT
					|| jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_VM_CATALOG_FS
					|| jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_FS_ONDEMAND
					|| jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_VM_CATALOG_FS_ONDEMAND)
				heading = UIContext.Constants.JobMonitorCatalogWindow();
			else 
				heading = "";
			setHeadingHtml(heading);
		}
	}
	
	protected void PupulateUI(){
		if(jobMonitorModel==null || jobMonitorModel.getSessionID().longValue()<=0){
			cancelButton.disable();
//			cancelImage.setVisible(false);
			thisWindow.hide();
			return;
		}
		setHeaderText(jobMonitorModel);	
		if(titleFlexTable.getWidget(0, 0) == null){
			if( jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_VM)
				titleImage = AbstractImagePrototype.create(IconBundle.tasks_backup()).createImage();
			else if( jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_RESTORE || jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_RECOVERY_VM)
				titleImage = AbstractImagePrototype.create(IconBundle.tasks_restore()).createImage();
			else if( jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_COPY)
				titleImage = AbstractImagePrototype.create(IconBundle.tasks_recovery()).createImage();
			else if( jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_VM_CATALOG_FS
					|| jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_VM_CATALOG_FS_ONDEMAND)
				titleImage = AbstractImagePrototype.create(IconBundle.tasks_recovery()).createImage();
			titleFlexTable.setWidget(0, 0, titleImage);
		}
		
		if (jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_VM ) {
			throghtTable.getRowFormatter().setVisible(0, true);
			progressTable.getRowFormatter().setVisible(6, true);
			progressTable.getRowFormatter().setVisible(7, true);
			progressTable.getRowFormatter().setVisible(8, true);
			progressTable.getRowFormatter().setVisible(9, true);
			progressTable.getRowFormatter().setVisible(10, true);
			progressTable.getRowFormatter().setVisible(11, true);
			progressTable.getRowFormatter().setVisible(12, isShowTranportMode);
			if(!Utils.isEmptyOrNull(jobMonitorModel.getRpsServerName())){				
				destinationRPSServerLabel.setHtml(jobMonitorModel.getRpsServerName());			
				progressTable.getRowFormatter().setVisible(13, true);
			}
			if(!Utils.isEmptyOrNull(jobMonitorModel.getRpsDataStoreName())){
				targetDatastoreNameLabel.setHtml(jobMonitorModel.getRpsDataStoreName());
				progressTable.getRowFormatter().setVisible(14, true);
			}
		} 
		else if(jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_RECOVERY_VM){
			throghtTable.getRowFormatter().setVisible(0, false);
			progressTable.getRowFormatter().setVisible(6, false);
			progressTable.getRowFormatter().setVisible(7, false);
			progressTable.getRowFormatter().setVisible(9, false);
			progressTable.getRowFormatter().setVisible(10, false);
			progressTable.getRowFormatter().setVisible(12, isShowTranportMode);
		}
		else if (jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_RESTORE ) {
			throghtTable.getRowFormatter().setVisible(0, false);
			progressTable.getRowFormatter().setVisible(6, false);
			progressTable.getRowFormatter().setVisible(7, false);
			progressTable.getRowFormatter().setVisible(8, false);
		} else if (jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_COPY) {
			throghtTable.getRowFormatter().setVisible(0, false);
			progressTable.getRowFormatter().setVisible(8, true);
			progressTable.getRowFormatter().setVisible(10, true);
			progressTable.getRowFormatter().setVisible(6, true);
		} else if (jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_VM_CATALOG_FS
				|| jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_VM_CATALOG_FS_ONDEMAND){
			progressTable.getRowFormatter().setVisible(4, false);
		} 
		else if(jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_GRT){
			totalFolderLabel.setHtml(String.valueOf(jobMonitorModel.getGRTTotalFolder()));
			processedFolderLabel.setHtml(String.valueOf(jobMonitorModel.getGRTProcessedFolder()));
			progressLabel.setHtml(Utils.getCatalogProgress(jobMonitorModel));	
			progressTable.getRowFormatter().setVisible(2, false);
		}

		
		String phase = Utils.jobMonitorPhase2String(jobMonitorModel);			
		if(phase !=null){
			phasevalueLable.setHtml(phase);
		}
		jobNameLabel.setHtml(Utils.jobMonitorType2String(jobMonitorModel));
		if(jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_VM || jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_RECOVERY_VM){
			volumeLabel.setHtml(jobMonitorModel.getCurrentProcessDiskName());
		}
		else{
			volumeLabel.setHtml(Utils.getCurrentVolumn(jobMonitorModel));
		}
		// the start time is sent from backend module, in milliseconds
		startTimeLabel.setHtml(Utils.formatDateToServerTime(new Date(jobMonitorModel.getBackupStartTime())));
		elapsedLabel.setHtml(Utils.milseconds2String(jobMonitorModel.getElapsedTime()));

		long processedSize = jobMonitorModel.getTransferBytesJob();
		long totalSize = jobMonitorModel.getEstimateBytesJob();
		if (processedSize > totalSize){
			processedSize = totalSize;
		}	
		
		computeCompressionAndDedupe(jobMonitorModel);
		
		encryptionLabel.setHtml(Utils.jobMonitorEncrytionAlgorithm2String(jobMonitorModel.getEncInfoStatus()));
		transferModeLabel.setHtml(Utils.VSphereJobMonitorTransferMode(jobMonitorModel.getTransferMode()));
		if(jobMonitorModel.getThrottling() == -1) { // if the value is -1, so we don't get the throttling.
			writeSpeedLimitLabel.setHtml(UIContext.Constants.NA());
		}else if(jobMonitorModel.getThrottling()!=0) {
			writeSpeedLimitLabel.setHtml(UIContext.Messages.jobMonitorThroughout(new Long(jobMonitorModel.getThrottling()).toString()));
		} else { // if we don't set the limit, show the value "No limit"
			writeSpeedLimitLabel.setHtml(UIContext.Constants.jobMonitorNoLimit());
		}
		
		long throughput = 0;
		if(jobMonitorModel.getElapsedTime() != 0){
			throughput = (processedSize / jobMonitorModel.getElapsedTime())*1000*60;
		}
		if(jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_RECOVERY_VM){
			readThroughputLabel.setHtml(Utils.bytes2MBString(throughput));
		}else if (jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_VM){
			readThroughputLabel.setHtml(UIContext.Messages.jobMonitorThroughout(new Long(jobMonitorModel.getReadSpeed()).toString()));
		} else {
			writeThrouhputLabel.setHtml(UIContext.Messages.jobMonitorThroughout(new Long(jobMonitorModel.getWriteSpeed()).toString()));
			readThroughputLabel.setHtml(Utils.bytes2MBString(throughput));
		}
		
		if (jobMonitorModel.getElapsedTime()>1000 && totalSize>0 && throughput > 0){
			long bytePerSec = (long)((double)throughput)/60;
			if( bytePerSec > 0 ){
				estimateLabel.setHtml(Utils.getRemainTime(jobMonitorModel));
			}
			else {
				estimateLabel.setHtml(UIContext.Constants.NA());
			}
			
		}else{
			estimateLabel.setHtml(UIContext.Constants.NA());
		}
		
		String transferedSizeLabel = Utils.bytes2String(processedSize);
		String totalSizeLabel = Utils.bytes2String(totalSize);
		
		if(jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_BACKUP) {
			progressBar.setVisible(true);
		} 
		else if( jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_VM_CATALOG_FS
				|| jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_GRT
				|| jobMonitorModel.getJobType() == JobMonitorModel.JOBTYPE_VM_CATALOG_FS_ONDEMAND){
			progressLabel.setHtml(Utils.getCatalogProgress(jobMonitorModel));
			estimateLabel.setHtml(UIContext.Constants.NA());
		}
		else {
			if(jobMonitorModel.getEstimateBytesJob()<=0 && jobMonitorModel.getTotalVMJobCount() <= 0) {
				progressBar.setVisible(false);
			} else {
				progressBar.setVisible(true);
			}
		}
		if(progressBar!=null){
			if(isVAppJob) {
				long totalVMJobs = jobMonitorModel.getTotalVMJobCount();
				long completedVMJobs = jobMonitorModel.getFinishedVMJobCount() + jobMonitorModel.getFailedVMJobCount() + jobMonitorModel.getCanceledVMJobCount();
				if(totalVMJobs > 0) {
					double percent = ((double)completedVMJobs) / jobMonitorModel.getTotalVMJobCount();
					if(percent >= 1) {
						progressBar.updateProgress(1, UIContext.Messages.jobMonitorVAppProgressBarLabel(100, String.valueOf(completedVMJobs), String.valueOf(totalVMJobs)));
					} else {
						progressBar.updateProgress(percent, UIContext.Messages.jobMonitorVAppProgressBarLabel((int)(percent*100), String.valueOf(completedVMJobs), String.valueOf(totalVMJobs)));
					}
				}
			} else {
				
				
				// liuwe05 2015-01-29 fix defect 205763: HBBU backup progress dialog chkdsk phase stay at 100% user can not know what is it doing and will think it hangs if chkdsk takes a long time
				// for checking recovery point, showing a fake (auto) progress bar
				if (jobMonitorModel.getJobPhase() == JobMonitorModel.BACKUP_PHASE_CHECK_RECOVERY_POINT)
				{
					if (!progressBar.isRunning()) // if the auto progress is not enabled
					{
						progressBar.auto();
						progressBar.updateText(Utils.jobMonitorPhase2String(jobMonitorModel));
					}
				}
				else
				{
					if (progressBar.isRunning()) // once check recovery point is done, restore the original progress bar
					{
						progressBar.reset();
					}
			
				
					if(jobMonitorModel.getJobPhase() == JobMonitorModel.PHASE_BACKUP_PHASE_START_BACKUP || 
							jobMonitorModel.getJobPhase() == JobMonitorModel.PHASE_BACKUP_PHASE_TAKING_SNAPSHOT ||
							jobMonitorModel.getJobPhase() == JobMonitorModel.PHASE_BACKUP_PHASE_CREATING_VIRTUAL_DISKS)
					{
						progressBar.updateProgress(0.0026,""); 
					}
					else if (totalSize>0){
						double percent = ((double)processedSize)/totalSize;
						if (percent>=1)
							progressBar.updateProgress(1, UIContext.Messages.jobMonitorProgressBarLabel(100, transferedSizeLabel, totalSizeLabel));
						else
							progressBar.updateProgress(percent, UIContext.Messages.jobMonitorProgressBarLabel((int)(percent*100), transferedSizeLabel, totalSizeLabel));
					}
				}
			}
		}
		
		if (jobMonitorModel.getJobPhase() == JobMonitorModel.PHASE_CANCELING
				|| jobMonitorModel.getJobPhase() == JobMonitorModel.JOBSTATUS_CANCELLED
				|| jobMonitorModel.getJobPhase() == JobMonitorModel.BACKUP_PHASE_CONNECT_TO_STUB
				|| jobMonitorModel.getJobPhase() == JobMonitorModel.BACKUP_PHASE_UPGRADE_CBT
				|| jobMonitorModel.getJobPhase() == JobMonitorModel.BACKUP_PHASE_INITIALIZE_STUB
				|| jobMonitorModel.getJobPhase() == JobMonitorModel.BACKUP_PHASE_COLLECT_DATA
				|| jobMonitorModel.getJobStatus() == JobMonitorModel.JOBSTATUS_CANCELLED){
			cancelButton.disable();
		}else
			cancelButton.enable();
		
		if (Utils.isJobDone(jobMonitorModel.getJobType(), jobMonitorModel
				.getJobPhase(), jobMonitorModel.getJobStatus().intValue())) {
			thisWindow.hide();
			cancelButton.disable();
		}
	}
	
	private Widget setupProgressPanel(){
		ContentPanel progressPanel = new ContentPanel();
		progressPanel.setHeadingHtml(UIContext.Constants.jobMonitorProgressTitle());
		progressPanel.setAnimCollapse(false);
//		progressPanel.setWidth("100%");
		progressPanel.setCollapsible(true);
		progressPanel.expand();
		progressPanel.setBorders(false);
		progressPanel.setBodyBorder(false);
		progressPanel.setScrollMode(Scroll.AUTO);
		progressPanel.setStyleAttribute("padding", "5px");
		progressPanel.addListener(Events.Collapse, new Listener<ComponentEvent>() {
			@Override
			public void handleEvent(ComponentEvent be) {
				thisWindow.setHeight(thisWindow.getHeight()- 200);
			}
		});
		progressPanel.addListener(Events.Expand, new Listener<ComponentEvent>() {
			@Override
			public void handleEvent(ComponentEvent be) {
				thisWindow.setHeight(thisWindow.getHeight()+ 200);
			}
		});
		
		progressTable.setWidth("510px");
		progressTable.setCellPadding(4);
		progressTable.setCellSpacing(4);
		progressTable.getColumnFormatter().setWidth(0, "270px");
//		progressTable.getColumnFormatter().setStyleName(1, "jobmonitor_window_value_column");
		Label label;
		Label currVolLable;
		
		progressTable.getFlexCellFormatter().setColSpan(0, 0, 2);
		progressTable.setWidget(0, 0, setupJobPhasePanel());
		progressTable.getFlexCellFormatter().setColSpan(1, 0, 2);
		progressTable.getCellFormatter().setStyleName(1, 0, "jobMonitor_progress");
		
		int type = Integer.parseInt(jobType);
		
		if( type == JobMonitorModel.JOBTYPE_VM_CATALOG_FS
				|| type == JobMonitorModel.JOBTYPE_CATALOG_GRT
				|| type == JobMonitorModel.JOBTYPE_VM_CATALOG_FS_ONDEMAND){
		    ContentPanel panel = new ContentPanel();
			panel.setWidth("100%");
			panel.setBodyBorder(true);		
			panel.setHeaderVisible(false);
			progressLabel = new Label();
			progressLabel.setWidth("510");
			progressLabel.setStyleName("jobMonitor_value");
			panel.add(progressLabel);
			progressTable.setWidget(1, 0, panel);
		}else{
			progressBar = new ProgressBar(){
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
			progressBar.ensureDebugId("0851367A-E262-48ba-851C-D5600220C290");
			progressBar.setWidth(510);
			progressBar.setVisible(false);
			progressTable.setWidget(1, 0, progressBar);
		}
		
		label = new Label();
		label.setStyleName("jobMonitor_label");
		label.setHtml(UIContext.Constants.jobMonitorStartTime());
		LayoutContainer lc2 = new LayoutContainer();
		lc2.add(label);
		//lc2.setWidth(230);
		progressTable.setWidget(2, 0, lc2);
		startTimeLabel.setStyleName("jobMonitor_value");
		progressTable.setWidget(2, 1, startTimeLabel);
		
		label = new Label();
		label.setStyleName("jobMonitor_label");		
		
		if(type == JobMonitorModel.JOBTYPE_CATALOG_GRT){
			processedFolderLabel.setStyleName("jobMonitor_value");
			progressTable.setWidget(3, 1, processedFolderLabel);
			label.setHtml(UIContext.Constants.jobMonitorGRTProcessedFolder());
		}else{
			elapsedLabel.setStyleName("jobMonitor_value");
			progressTable.setWidget(3, 1, elapsedLabel);
			label.setHtml(UIContext.Constants.jobMonitorLabelElapsedTime());
		}
		
		progressTable.setWidget(3, 0, label);
		
		label = new Label();
		label.setStyleName("jobMonitor_label");
		
		if(type == JobMonitorModel.JOBTYPE_CATALOG_GRT){
			label.setHtml(UIContext.Constants.jobMonitorGRTTotalFolder());
			this.totalFolderLabel.setStyleName("jobMonitor_value");
			progressTable.setWidget(4, 1, totalFolderLabel);
		}else{ 
			label.setHtml(UIContext.Constants.jobMonitorEstimatedTime());
			estimateLabel.setStyleName("jobMonitor_value");
			progressTable.setWidget(4, 1, estimateLabel);
		}	
		progressTable.setWidget(4, 0, label);
		
		
		currVolLable = new Label();
		currVolLable.setStyleName("jobMonitor_label");
		if(type == JobMonitorModel.JOBTYPE_VM || type == JobMonitorModel.JOBTYPE_RECOVERY_VM){
			currVolLable.setHtml(UIContext.Constants.jobMonitorDiskLabel());
		}
		else {
			currVolLable.setHtml(UIContext.Constants.jobMonitorVolumeLabel());
		}
		//currVolLable.setWidth("230");
		progressTable.setWidget(5, 0, currVolLable);
		volumeLabel.setStyleName("jobMonitor_value");
		//volumeLabel.setWidth("200");	
		volumeLabel.setStyleAttribute("white-space", "pre-wrap");
		LayoutContainer lc = new LayoutContainer();
		lc.setWidth(230);
		lc.add(volumeLabel);
		progressTable.setWidget(5, 1, lc);
		
		label = new Label();
		label.setStyleName("jobMonitor_label");
		label.setHtml(UIContext.Constants.jobMonitorEncryptionStatus());
		progressTable.setWidget(6, 0, label);
		encryptionLabel.setStyleName("jobMonitor_value");
		progressTable.setWidget(6, 1, encryptionLabel);
		progressTable.getRowFormatter().setVisible(6, false);
		
		label = new Label();
		label.setStyleName("jobMonitor_label");
		label.setHtml(UIContext.Constants.jobMonitorDedupe());
		progressTable.setWidget(7, 0, label);
		dedupeEnabled.setStyleName("jobMonitor_value");
		progressTable.setWidget(7, 1, dedupeEnabled);
		progressTable.getRowFormatter().setVisible(7, false);
		
		label = new Label();
		label.setStyleName("jobMonitor_label");
		label.setHtml(UIContext.Constants.jobMonitorCompressLevel());
		progressTable.setWidget(8, 0, label);
		compressLevel.setStyleName("jobMonitor_value");
		progressTable.setWidget(8, 1, compressLevel);
		progressTable.getRowFormatter().setVisible(8, false);
		
		label = new Label();
		label.setStyleName("jobMonitor_label");
		label.setHtml(UIContext.Constants.jobMonitorDedupeRate());
		progressTable.setWidget(9, 0, label);
		dedupeRateLabel.setStyleName("jobMonitor_value");
		progressTable.setWidget(9, 1, dedupeRateLabel);
		progressTable.getRowFormatter().setVisible(9, false);
		
		label = new Label();
		label.setStyleName("jobMonitor_label");
		label.setHtml(UIContext.Constants.jobMonitorCompressRate());
		progressTable.setWidget(10, 0, label);
		compressRateLabel.setStyleName("jobMonitor_value");
		progressTable.setWidget(10, 1, compressRateLabel);
		progressTable.getRowFormatter().setVisible(10, false);
		
		label = new Label();
		label.setStyleName("jobMonitor_label");
		label.setHtml(UIContext.Constants.jobMonitorOverDataReduction());
		progressTable.setWidget(11, 0, label);
		//totalSpaceLabel.setStyleName("jobMonitor_value");
		//progressTable.setWidget(11, 1, totalSpaceLabel);
		overallReduction = new LayoutContainer(new RowLayout());
		overallReduction.setStyleName("jobMonitor_value");
		totalSpaceLabel.setStyleAttribute("padding-right", "5px");
		overallReduction.add(totalSpaceLabel);
		detailIcon=new Image(UIContext.IconBundle.jobmonitor_detail());
		detailIcon.setVisible(false);
		overallReduction.add(detailIcon);
		progressTable.setWidget(11, 1, overallReduction);
		progressTable.getRowFormatter().setVisible(11, false);
		
		label = new Label();
		label.setStyleName("jobMonitor_label");
		label.setHtml(UIContext.Constants.jobMonitorTransferMode());
		progressTable.setWidget(12, 0, label);
		transferModeLabel.setStyleName("jobMonitor_value");
		progressTable.setWidget(12, 1, transferModeLabel);
		progressTable.getRowFormatter().setVisible(12, false);
				
		Label descServerLabel = new Label();
		descServerLabel.setStyleName("jobMonitor_label");
		descServerLabel.setHtml(UIContext.Constants.destinationRPSServer());
		progressTable.setWidget(13, 0, descServerLabel);
		destinationRPSServerLabel.setStyleName("jobMonitor_value");
		progressTable.setWidget(13, 1, destinationRPSServerLabel);
		progressTable.getRowFormatter().setVisible(13, false);

		Label targetDSNameLabel = new Label();
		targetDSNameLabel.setStyleName("jobMonitor_label");
		targetDSNameLabel.setHtml(UIContext.Constants.targetDatastoreName());
		progressTable.setWidget(14, 0, targetDSNameLabel);
		LayoutContainer targetDSContainer = new LayoutContainer(new RowLayout());
		targetDSContainer.setWidth(220);
		targetDSContainer.setStyleName("jobMonitor_value");
		targetDSContainer.setStyleAttribute("word-wrap", "break-word");
		targetDSContainer.add(targetDatastoreNameLabel);
		progressTable.setWidget(14, 1, targetDSContainer);
		progressTable.getRowFormatter().setVisible(14, false);
		
		progressPanel.add(progressTable);
		return progressPanel;
	}
	
	@Override
	protected void onHelp() {
		HelpTopics.showHelpURL(UIContext.externalLinks.getVMJobMonitorHelp());
	}
}
