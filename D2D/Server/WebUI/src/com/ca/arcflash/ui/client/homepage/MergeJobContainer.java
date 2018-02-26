package com.ca.arcflash.ui.client.homepage;

import java.util.Date;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.IRefreshable;
import com.ca.arcflash.ui.client.model.MergeJobMonitorModel;
import com.ca.arcflash.ui.client.model.MergeStatusModel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;

public class MergeJobContainer {
	
	protected HomepageServiceAsync service = GWT.create(HomepageService.class);
	
	protected MergeJobMonitorModel currentJobMonitor;
	protected MergeJobMonitorModel prevJobMonitor;
	protected MergeStatusModel currentStatus;
	private Timer mergeTimer;
	
	protected MergePanel panel;
	protected MergeRunningPanel runningPanel;
	protected LayoutContainer monitorPanel;
	protected BaseAsyncCallback<MergeStatusModel> callback;
	
	public MergeJobContainer() {
		setupCallback();		
	}
	
	public void setMonitorPanel(LayoutContainer monitorPanel) {
		this.monitorPanel = monitorPanel;
	}
	
	public BaseAsyncCallback<MergeStatusModel> getCallback() {
		return callback;
	}
	
	protected MergePanel createMergePanel(MergeStatusModel model){
		return new MergePanel(model);
	}
	
	protected MergeRunningPanel createMergeRunningPanel(MergeStatusModel model) {
		return new MergeRunningPanel(model);
	}
	
	protected void setupCallback() {
		callback = new BaseAsyncCallback<MergeStatusModel>(){
				@Override
                public void onSuccess(MergeStatusModel result) {					
					currentStatus = result;
					
					if(currentStatus.jobMonitor == null){
						if(runningPanel != null){
							monitorPanel.remove(runningPanel);
							runningPanel = null;
						}
						
						if(currentStatus.getStatus() == MergeStatusModel.PAUSED_MANUALLY){
							if(panel == null){
								panel = createMergePanel(currentStatus);
								monitorPanel.add(panel);
							}
							panel.refreshUI(result);
						}else {
							if(panel != null){
								monitorPanel.remove(panel);
								panel = null;
							}
							Date time = new Date();
							if(time.getTime() > result.getUpdateTime() &&
									(time.getTime() - result.getUpdateTime()) <= MonitorPanel.REFRESH_INTERVAL + 2) {
								refreshHostPage();
							}
						}
					}else {
						prevJobMonitor = currentJobMonitor;
						currentJobMonitor = result.jobMonitor;
						if(noNeedMerge()) return;
						
						if(panel != null){
							monitorPanel.remove(panel);
							panel = null;
						}
						
						if(runningPanel == null){
							runningPanel = createMergeRunningPanel(currentStatus);
							monitorPanel.add(runningPanel);
						}
						
						runningPanel.refreshUI(result);
						jobDone(currentJobMonitor);
					}
				}
		};
	}
	
	protected void refreshHostPage() {
		if(UIContext.hostPage != null)
			UIContext.hostPage.refresh(null, IRefreshable.CS_MERGEJOB_FINISHED);
	}
	
	protected void setupTimer() {
		mergeTimer = new Timer() {
			@Override
			public void run() {
				refresh();
			}
		};
		
		mergeTimer.schedule(500);
		mergeTimer.scheduleRepeating(3 * 1000);
	}
	
	private boolean noNeedMerge() {
		if(prevJobMonitor == null) {
			if(currentJobMonitor.getJobPhase() == 
					MergeJobMonitorModel.JobPhase.EJP_PROC_EXIT.ordinal()) {
				return true;
			}
		}else {
			if(prevJobMonitor.getJobPhase() == currentJobMonitor.getJobPhase()
					&& prevJobMonitor.getJobPhase() == 
						MergeJobMonitorModel.JobPhase.EJP_PROC_EXIT.ordinal()) {
				return true;
			}
		}
		return false;
	}
	
	private void jobDone(MergeJobMonitorModel jm) {
		if(jm.getJobPhase() == MergeJobMonitorModel.JobPhase.EJP_PROC_EXIT.ordinal()){
			//since once merge job is started, we will hide the merging RPS, so 
			//no matter the merge job is stopped or completed, we should refresh the host page
			prevJobMonitor =  currentJobMonitor = null;
		}
	}
	
	public void refresh() {
		service.getMergeStatus(null, callback);
	}
}
