package com.ca.arcflash.ui.client.homepage;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.ScheduleSummaryWindow;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.IRefreshable;
import com.ca.arcflash.ui.client.common.LoadingStatus;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.JobMonitorModel;
import com.ca.arcflash.ui.client.model.NextScheduleEventModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.StatusCodeException;

public class MonitorPanel extends LayoutContainer {
	protected final CommonServiceAsync service = GWT.create(CommonService.class);
	protected final HomepageServiceAsync homepageService = GWT.create(HomepageService.class);
	public static final int REFRESH_INTERVAL = 3 * 1000;
	protected Timer timer;
	protected com.extjs.gxt.ui.client.widget.Label nextEventText = new com.extjs.gxt.ui.client.widget.Label();	
	protected com.google.gwt.user.client.ui.Label advanceSchedule = new com.google.gwt.user.client.ui.Label(UIContext.Constants.JobMonitorLabelAdvanceSchedule());
	//Indicate whether the instance variable <code>panel</code> is visible. We do not use the method panel.visible() because 
	//this panel is now in a TabItem and the method will return false when the panel shows and the enclosing 
	//item is not active. Note: when changing the visibility of the panel, isMonitorVisible must also be updated
	//correspondingly.
	protected LoadingStatus status = new LoadingStatus();
	
	protected Map<Long, JobMonitorPanel> panels = new HashMap<Long, JobMonitorPanel>();
//	private VerticalPanel monitors = new VerticalPanel();
	protected LayoutContainer container = new LayoutContainer();
	
	private Map<String, Long> failureMethods = new HashMap<String, Long>();
	private boolean shown = false;
	protected MergeJobContainer mergeContainer;
	
	public void render(Element target, int index) {
		super.render(target, index);
	    this.setAutoHeight(true);
	    ContentPanel topContainer = new ContentPanel();		
	    topContainer.setCollapsible(true);
	    topContainer.setHeadingHtml(UIContext.Constants.jobMonitorPanelTitle());
	    topContainer.setBodyStyle("background-color: white; padding: 6px;");
	    topContainer.setLayout(new RowLayout());
		
	    HorizontalPanel nextEventPanel = new HorizontalPanel();
	    
	    nextEventText.setStyleName("homepage_nextScheduleEvent");
		    
	  //fix 18901838
	    status = new LoadingStatus();
	    nextEventPanel.add(status);
	    nextEventText.setHtml(UIContext.Constants.jobMonitorLabelNoJobRunning());
	    refreshNextScheduleEvent();
	    
	    advanceSchedule.setStyleName("homepage_header_hyperlink_label");
	    advanceSchedule.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onAdvancedScheduleClick();
			}
		});
	    
	    advanceSchedule.setVisible(false);

	    TableData td =  new TableData();
		td.setWidth("50%");
		td.setHorizontalAlign(HorizontalAlignment.LEFT);
		nextEventPanel.add(nextEventText, td);
	    td =  new TableData();
		td.setWidth("50%");
		td.setHorizontalAlign(HorizontalAlignment.RIGHT);
	    nextEventPanel.add(advanceSchedule, td);
	    nextEventPanel.setTableWidth("100%");
//	    monitors.add(nextEventPanel);
//	    topContainer.add(monitors);	  
//	    monitors.setWidth("100%");
	    TableLayout layout = new TableLayout();
	    layout.setWidth("100%");
	    layout.setColumns(1);
	    container.setLayout(layout);
	    container.setLayoutOnChange(true);
	    container.setAutoHeight(true);
	    container.add(nextEventPanel);
	    topContainer.add(container);
	    mergeContainer= createMergeJobContainer();
	    mergeContainer.setMonitorPanel(container);

	    add(topContainer);
	    
	    timer = new Timer() {
			public void run() {
				refresh(null);
			}
		};
		timer.schedule(REFRESH_INTERVAL);
		timer.scheduleRepeating(REFRESH_INTERVAL);
		shown = false;
	}

	protected MergeJobContainer createMergeJobContainer() {
		return new MergeJobContainer();
	}

	private boolean isFailedForWebServiceDown(Throwable caught, String methodName) {
		if(caught instanceof StatusCodeException 
				&& ((StatusCodeException)caught).getStatusCode() == 0) {
			Long currentTime = new Date().getTime();
			if(!failureMethods.isEmpty() && !failureMethods.containsKey(methodName)) {
				Long time = failureMethods.values().iterator().next();
				if((currentTime - time) < 15 * 1000 && !shown){
					shown = true;
					return true;
				}else {
					failureMethods.clear();
					failureMethods.put(methodName, currentTime);
					return false;
				}	
			}else {
				failureMethods.put(methodName, currentTime);
				return false;
			}
		}
		
		return false;
	}
	
	public void refresh(Object data){
		
//		if(UIContext.serverVersionInfo != null && UIContext.serverVersionInfo.isSettingConfiged()!=null && UIContext.serverVersionInfo.isSettingConfiged()){
//			 advanceSchedule.setVisible(true);
//		}else{
//			 advanceSchedule.setVisible(false);
//		}
		
		refreshNextScheduleEvent();
		mergeContainer.refresh();
		
		service.getJobMonitorMap(new BaseAsyncCallback<JobMonitorModel[]>(){

			@Override
			public void onFailure(Throwable caught) {
    			status.hideIndicator();
    			if(UIContext.homepagePanel.processServerDown(caught, "getJobMonitorMap")) {
    				showMessageBoxForReload(UIContext.Constants.cantConnectToServer());
    			}else
    				super.onFailure(caught);
			}

			@Override
			public void onSuccess(JobMonitorModel[] result) {
				status.hideIndicator();
				
				if (result == null || result.length ==0){
					if(panels.size() > 0) {
						for(JobMonitorPanel panel : panels.values()){
							container.remove(panel);
							refreshHostPage(new Long(panel.jobType).intValue());
							
							if(panel.jobType == JobMonitorModel.JOBTYPE_CATALOG_FS || panel.jobType == JobMonitorModel.JOBTYPE_CATALOG_FS_ONDEMAND)
								UIContext.recentBackupPanel.refresh(null);
						}
						panels.clear();
					}
					return;
				}
				cleanObseletePanel(result);
				for(JobMonitorModel model : result){
					if(Utils.isJobDone(model.getJobType(), model.getJobPhase(), model.getJobStatus().intValue()))
					{
						continue;	
					}
					JobMonitorPanel panel = getPanel(model); 
					if(panel == null){
						//wanqi06 added
						if(model.getJobType().intValue() == JobMonitorModel.JOBTYPE_BACKUP || model.getJobType().intValue() == JobMonitorModel.JOBTYPE_ARCHIVE )
							refreshHostPage(model.getJobType().intValue());
						
						String grtDB = model.getGRTEDB();
						if(model.getJobPhase() != 0 || (grtDB != null && !grtDB.isEmpty())){
							panel = new JobMonitorPanel(model);
							container.add(panel);
	//						UIContext.hostPage.refresh(null);
							setPanelJobStatus(panel, model.getJobType());
							panels.put(model.getID(), panel);
						}else {
							continue;
						}
					}
					
					if (Utils.isJobDone(model.getJobType(), model.getJobPhase(), model.getJobStatus().intValue())){
						panel.updateEndsCounter();	
					}
					
					if(panel.getEndsCounter() >= 3) {
						panels.remove(model.getID());
						container.remove(panel);
						refreshHostPage(model.getJobType().intValue());
						
						if((model.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_FS || model.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_FS_ONDEMAND) && (UIContext.recentBackupPanel != null))
							UIContext.recentBackupPanel.refresh(null);
					}
					
					panel.setBarVisable(model);
					
					panel.updateTimeLabel(model);
					
					if(model.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_APP
				    		|| model.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_FS
				    		|| model.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_GRT
				    		|| model.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_FS_ONDEMAND)
						panel.getCatalogStatus().setHtml(Utils.getCatalogProgress(model));
					else
						Utils.updateProgress(panel.getProgress(), model);
				}
			}
		});
	}
	
	public void refreshNextScheduleEvent(){
		homepageService.getNextScheduleEvent(JobMonitorModel.JOBTYPE_BACKUP, 
				new BaseAsyncCallback<NextScheduleEventModel>(){
			@Override
			public void onFailure(Throwable caught) {
				/*if(isFailedForWebServiceDown(caught, "getNextScheduleEvent")){
    				showMessageBoxForReload(UIContext.Constants.cantConnectToServer());
    			}else*/
    				super.onFailure(caught);
			}

			@Override
			public void onSuccess(NextScheduleEventModel result) {
				if (result == null){
					nextEventText.setHtml(UIContext.Messages.homepageNextScheduledEvent(UIContext.Constants.NA(), ""));
					return;
				}
				
				nextEventText.setHtml(UIContext.Messages.homepageNextScheduledEvent(
						Utils.formatDateToServerTime(result.getDate(), result.getServerTimeZoneOffset()),
						Utils.backupType2String(result.getBackupType())));
			}
		});
	}
	
	protected void setPanelJobStatus(JobMonitorPanel panel, long jobtype){
		panel.setJobStatus(Utils.getJobStatusLabelString(jobtype));
	}
	
	protected void cleanObseletePanel(JobMonitorModel[] result) {
		Set<Long> contains = new HashSet(panels.keySet());
		
		for(Long id : contains){
			boolean isDelete = true;
			for(JobMonitorModel model : result){
				if(id == model.getID().longValue()){
					isDelete = false;
					break;
				}
			}
			if(isDelete){
				container.remove(panels.get(id));
				refreshHostPage((int)panels.get(id).jobType);
				panels.remove(id);
			}
		}
	}
	
	protected JobMonitorPanel getPanel(JobMonitorModel model){
		JobMonitorPanel jobMonitorPanel = null;
		for(JobMonitorPanel panel : panels.values()){
			if(panel.getJobID() == model.getID().longValue()){
				jobMonitorPanel = panel;
				break;
			}
		}
		return jobMonitorPanel;
	}
	
	private void refreshHostPage(int jobType) {
		switch(jobType){
		case JobMonitorModel.JOBTYPE_BACKUP:
			UIContext.hostPage.refresh(null, IRefreshable.CS_BACKUP_FINISHED);
			break;
		case JobMonitorModel.JOBTYPE_COPY:
			UIContext.hostPage.refresh(null, IRefreshable.CS_COPY_FINISHED);
			break;
		case JobMonitorModel.JOBTYPE_ARCHIVE:
			UIContext.hostPage.refresh(null, IRefreshable.CS_ARCHIVE_FINISHED);
			break;
		case JobMonitorModel.JOBTYPE_CATALOG_FS:
			UIContext.hostPage.refresh(null, IRefreshable.CS_FSCATALOG_FINISHED);
			break;
		case JobMonitorModel.JOBTYPE_CATALOG_FS_ONDEMAND:
			UIContext.hostPage.refresh(null, IRefreshable.CS_FSCATALOG_FINISHED);
			break;
			default:
				break;
		}
			
	}

	protected void onAdvancedScheduleClick() {
		ScheduleSummaryWindow window = new ScheduleSummaryWindow();
		window.show();
	}
}
