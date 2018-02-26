package com.ca.arcflash.ui.client.homepage;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.LoadingStatus;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.JobMonitorModel;
import com.ca.arcflash.ui.client.vsphere.homepage.VSphereJobMonitorDetailWindow;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;

public class JobMonitorPanel extends ContentPanel {
	
	protected long jobType;
	protected ProgressBar bar;	
	protected LoadingStatus jobStatus = new LoadingStatus();	
	protected Label statusTimeLabel = new Label();
	protected Button detaiButton = new Button();
	protected Label catalogStatusLabel = new Label();
	protected long endsCounter = 0;
	protected long jobStartTime = 0;
	protected long jobID = -1;
	protected Label timeLabel;
	protected String vmInstanceUUID;
	protected JobMonitorModel model;
	public JobMonitorPanel(JobMonitorModel model){
		this.jobType = model.getJobType();
		this.jobID = model.getID();
		this.vmInstanceUUID = model.getVmInstanceUUID();
		this.model = model;
		TableLayout layout = new TableLayout();
	    layout.setColumns(5);
	    layout.setWidth("100%");
		setBodyBorder(true);		
		setHeaderVisible(false);
	    setLayout(layout);
	    
	    TableData data = new TableData();
	    data.setWidth("15%");
	    data.setMargin(2);
	    jobStatus.getLoadingLabelField().setStyleAttribute("font-size", "12px");
	    jobStatus.getLoadingLabelField().setStyleAttribute("padding-left", "0px");
	    	
	    add(jobStatus,data);
	    
	    data = new TableData();
	    data.setWidth("20%");
	    timeLabel = new Label();	    	
	    if(jobType == JobMonitorModel.JOBTYPE_CATALOG_GRT){
	    	timeLabel.setHtml(UIContext.Constants.jobMonitorGRTProcessedFolder());
	    }else if(this.jobType == JobMonitorModel.JOBTYPE_CATALOG_APP
	    		|| jobType == JobMonitorModel.JOBTYPE_CATALOG_FS
	    		|| jobType == JobMonitorModel.JOBTYPE_CATALOG_FS_ONDEMAND){
	    	timeLabel.setHtml(UIContext.Constants.jobMonitorElapsedTime());
	    	jobStartTime = System.currentTimeMillis();
	    }
	    else 
	    	timeLabel.setHtml(UIContext.Constants.jobMonitorEstimatedTime());
	    timeLabel.setStyleName("jobMonitor_label");
	    timeLabel.setStyleAttribute("margin-left", "0px");
	    add(timeLabel, data);
	    
	    data = new TableData();
	    data.setWidth("10%");
	    data.setMargin(2);
	    statusTimeLabel.setWidth("100%");
	    statusTimeLabel.setStyleName("jobMonitor_value");
	    add(statusTimeLabel,data);
	    
	    data = new TableData();
	    //data.setWidth("48%");
	    data.setWidth("50%");
	    if(isCatalogJob(jobType)){
	    	catalogStatusLabel.setWidth("100%");
	    	catalogStatusLabel.setStyleName("jobMonitor_progress_text");
	    	add(catalogStatusLabel, data);
	    }else {
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
	            
	            @Override
				public ProgressBar updateProgress(double value, String text) {
	            	super.updateProgress(value, text);
	            	String w = value * 100 + "%";
	            	//Set progress width to a relative value.
	            	el().firstChild().firstChild().setWidth(w);
					return this;
				}
	        };
	        bar.setAutoWidth(false);
	        bar.setWidth("100%");
	        bar.setHeight(20);
	        bar.show();
	        add(bar,data);
	    }
	    
        data = new TableData();
        data.setWidth("7%");
        data.setPadding(5);
        detaiButton.setMinWidth(80);
	    detaiButton.setText(UIContext.Constants.jobMonitorDetail());
	    detaiButton.ensureDebugId("cc922a57-d648-46b4-900c-4e7798a71345");
	    detaiButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				onDetailButtonClick();
			}
	    	
	    });
	    add(detaiButton, data);
//	    add(panel);
	}
	
	public void updateEndsCounter(){
		endsCounter ++;
	}
	
	public long getEndsCounter(){
		return endsCounter;
	}
	
	public void setJobStatus(String text){
		jobStatus.setLoadingMsg(text);
	}
	
	public void updateBarProgress(double value, String text){
		bar.updateProgress(value, text);
	}
	
	public void updateTimeLabel(JobMonitorModel model){
		if(jobStartTime > 0){
			statusTimeLabel.setHtml(Utils.milseconds2String(model.getElapsedTime()));
		}else
			statusTimeLabel.setHtml(Utils.getRemainTime(model));
	}
	
	public Label getCatalogStatus(){
		return catalogStatusLabel;
	}
	
	public ProgressBar getProgress(){
		return bar;
	}
	
	public void setBarVisable(JobMonitorModel model) {
		if(bar == null)
			return;
		if (model.getJobType() == JobMonitorModel.JOBTYPE_BACKUP){
			bar.setVisible(true);
		}else{
			if (model.getEstimateBytesJob() <=0){
				bar.setVisible(false);
			}else{
				bar.setVisible(true);
			}
		}
	}

	public long getJobID() {
		return jobID;
	}

	public void setJobID(long jobID) {
		this.jobID = jobID;
	}
	
	protected boolean isCatalogJob(long jobType) {
		return this.jobType == JobMonitorModel.JOBTYPE_CATALOG_APP
	    		|| jobType == JobMonitorModel.JOBTYPE_CATALOG_FS
	    		|| jobType == JobMonitorModel.JOBTYPE_CATALOG_GRT
	    		|| jobType == JobMonitorModel.JOBTYPE_CATALOG_FS_ONDEMAND;
	}
	
	protected void onDetailButtonClick() {
		if(JobMonitorPanel.this.jobType == JobMonitorModel.JOBTYPE_RECOVERY_VM){
			VSphereJobMonitorDetailWindow window = new VSphereJobMonitorDetailWindow(model);
			window.setModal(true);
			window.show();
		}else{
			JobMonitorDetailWindow window = new JobMonitorDetailWindow(
					String.valueOf(JobMonitorPanel.this.jobType), JobMonitorPanel.this.jobID);
			window.setModal(true);
			window.show();
		}
	}

	public long getJobType() {
		return jobType;
	}
}
