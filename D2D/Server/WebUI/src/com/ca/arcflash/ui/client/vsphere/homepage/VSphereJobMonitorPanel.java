package com.ca.arcflash.ui.client.vsphere.homepage;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.homepage.JobMonitorDetailWindow;
import com.ca.arcflash.ui.client.homepage.JobMonitorPanel;
import com.ca.arcflash.ui.client.model.JobMonitorModel;

public class VSphereJobMonitorPanel extends JobMonitorPanel {
	public VSphereJobMonitorPanel(JobMonitorModel model){
		super(model);
		detaiButton.ensureDebugId("81FC8CB3-4390-4ab7-877E-2B7DAB6D4C77");
	       	
	    if(this.jobType == JobMonitorModel.JOBTYPE_VM_CATALOG_FS
	    		|| jobType == JobMonitorModel.JOBTYPE_VM_CATALOG_FS_ONDEMAND){
	    	timeLabel.setHtml(UIContext.Constants.jobMonitorElapsedTime());
	    	jobStartTime = System.currentTimeMillis();
	    }
	    else 
	    	timeLabel.setHtml(UIContext.Constants.jobMonitorEstimatedTime());
	}
	
	public String getVmInstanceUUID() {
		return vmInstanceUUID;
	}

	public void setVmInstanceUUID(String vmInstanceUUID) {
		this.vmInstanceUUID = vmInstanceUUID;
	}
	
	public void setBarVisable(JobMonitorModel model) {
		if(bar == null)
			return;
		if (model.getJobType() == JobMonitorModel.JOBTYPE_VM){
			bar.setVisible(true);
		}else{
			if (model.getEstimateBytesJob() <=0){
				bar.setVisible(false);
			}else{
				bar.setVisible(true);
			}
		}
	}

	@Override
	protected boolean isCatalogJob(long jobType) {
		return this.jobType == JobMonitorModel.JOBTYPE_VM_CATALOG_FS
	    		|| jobType == JobMonitorModel.JOBTYPE_VM_CATALOG_FS_ONDEMAND;
	}

	@Override
	protected void onDetailButtonClick() {
		if(VSphereJobMonitorPanel.this.jobType == JobMonitorModel.JOBTYPE_RESTORE){
			JobMonitorDetailWindow window = new JobMonitorDetailWindow(
					String.valueOf(VSphereJobMonitorPanel.this.jobType), jobID);
			window.setModal(true);
			window.show();
		}else{
			VSphereJobMonitorDetailWindow window = new VSphereJobMonitorDetailWindow(model);
			window.setModal(true);
			window.show();
		}
	}
	
}
