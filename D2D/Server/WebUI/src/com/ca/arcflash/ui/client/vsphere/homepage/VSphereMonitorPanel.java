package com.ca.arcflash.ui.client.vsphere.homepage;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.homepage.JobMonitorPanel;
import com.ca.arcflash.ui.client.homepage.MergeJobContainer;
import com.ca.arcflash.ui.client.homepage.MonitorPanel;
import com.ca.arcflash.ui.client.model.JobMonitorModel;
import com.ca.arcflash.ui.client.model.NextScheduleEventModel;

public class VSphereMonitorPanel extends MonitorPanel {

	public void refresh(Object data){
		refreshNextScheduleEvent();
		mergeContainer.refresh();
		
		service.getVMJobMonitorMap(UIContext.backupVM,new BaseAsyncCallback<JobMonitorModel[]>(){

			@Override
			public void onFailure(Throwable caught) {
    			status.hideIndicator();
				super.onFailure(caught);
			}

			@Override
			public void onSuccess(JobMonitorModel[] result) {
				status.hideIndicator();
				
				if (result == null || result.length ==0){
					if(panels.size() > 0) {
						for(JobMonitorPanel panel : panels.values()){
							container.remove(panel);
							if((panel.getJobType() == JobMonitorModel.JOBTYPE_VM) 
									|| (panel.getJobType() == JobMonitorModel.JOBTYPE_VM_CATALOG_FS)
									|| (panel.getJobType() == JobMonitorModel.JOBTYPE_VM_CATALOG_FS_ONDEMAND)) 
								UIContext.vSphereHomepagePanel.refresh(null);
							
							/*if(panel.jobType == JobMonitorModel.JOBTYPE_CATALOG_FS)
								UIContext.recentBackupPanel.refresh(null);*/
						}
						panels.clear();
					}
					return;
				}
				cleanObseletePanel(result);
				for(JobMonitorModel model : result){
					if (Utils.isJobDone(model.getJobType(), model.getJobPhase(), model.getJobStatus().intValue())){
						continue;	
					}
					JobMonitorPanel panel = getPanel(model); 
					if(panel == null){
						//wanqi06 added
						if(model.getJobType().intValue() == JobMonitorModel.JOBTYPE_VM)
							UIContext.vSphereHomepagePanel.refresh(null);
						String grtDB = model.getGRTEDB();
						if(model.getJobPhase() != 0 || (grtDB != null && !grtDB.isEmpty())){
							panel = new VSphereJobMonitorPanel(model);
							container.add(panel);
	//						UIContext.hostPage.refresh(null);
							setPanelJobStatus(panel, model.getJobType());
							panels.put(model.getID(),panel);
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
						if((model.getJobType() == JobMonitorModel.JOBTYPE_VM))
							UIContext.vSphereHomepagePanel.refresh(null);
						
						/*if((model.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_FS) && (UIContext.recentBackupPanel != null))
							UIContext.recentBackupPanel.refresh(null);*/
					}
					
					panel.setBarVisable(model);
					
					panel.updateTimeLabel(model);
					
					if(model.getJobType() == JobMonitorModel.JOBTYPE_VM_CATALOG_FS
							|| model.getJobType() == JobMonitorModel.JOBTYPE_CATALOG_GRT
							|| model.getJobType() == JobMonitorModel.JOBTYPE_VM_CATALOG_FS_ONDEMAND)
						panel.getCatalogStatus().setHtml(Utils.getCatalogProgress(model));
					else
						Utils.updateProgress(panel.getProgress(), model);
				}
			}
		});
	}
	
	public void refreshNextScheduleEvent(){
		homepageService.getVMNextScheduleEvent(UIContext.backupVM, new BaseAsyncCallback<NextScheduleEventModel>(){
			@Override
			public void onFailure(Throwable caught) {
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
	
	@Override
	protected void cleanObseletePanel(JobMonitorModel[] result) {
		List<Long> deleteJobIds = new ArrayList<Long>();
		for(JobMonitorPanel panel : panels.values()){
			boolean isDelete = true;
			for(JobMonitorModel model : result){
				if(panel.getJobID() == model.getID().longValue()){
					isDelete = false;
					break;
				}
			}
			if(isDelete){
				container.remove(panel);
				deleteJobIds.add(panel.getJobID());
				if((panel.getJobType() == JobMonitorModel.JOBTYPE_VM) 
						|| (panel.getJobType() == JobMonitorModel.JOBTYPE_VM_CATALOG_FS)
						|| panel.getJobType() == JobMonitorModel.JOBTYPE_VM_CATALOG_FS_ONDEMAND)
					UIContext.vSphereHomepagePanel.refresh(null);
			}
		}
		for(long id : deleteJobIds) {
			panels.remove(id);
		}
	}

	@Override
	protected void onAdvancedScheduleClick() {
		VSphereScheduleSummaryWindow window = new VSphereScheduleSummaryWindow();
		window.show();
	}

	@Override
	protected MergeJobContainer createMergeJobContainer() {
		return new VSphereMergeJobContainer();
	}
}
