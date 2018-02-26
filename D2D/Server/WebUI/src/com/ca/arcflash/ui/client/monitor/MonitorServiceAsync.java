package com.ca.arcflash.ui.client.monitor;

import com.ca.arcflash.ha.model.ARCFlashNodesSummary;
import com.ca.arcflash.ha.model.SummaryModel;
import com.ca.arcflash.ha.model.VMSnapshotsInfo;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MonitorServiceAsync {

	void getFailoverJobScript(String uuid, AsyncCallback<FailoverJobScript> callback);

	void queryFlashNodesSummary(AsyncCallback<ARCFlashNodesSummary> callback);

	void getSnapshots(String uuid, AsyncCallback<VMSnapshotsInfo[]> callback);

	void startFailover(String uuid, VMSnapshotsInfo vmSnapInfo,
			AsyncCallback<Void> callback);
	
	void isFailoverJobFinish(String afGuid, AsyncCallback<Boolean> callback); 
	
	void getSummaryModel(String afguid, String vmuuid, String vmname, AsyncCallback<SummaryModel> callback);
	
	void getCurrentRunningSnapShotGuid(String afGuid, AsyncCallback<String> callback);
	
	void shutDownVM(String afGuid, AsyncCallback<Integer> callback);
	
	void removeMonitee(String afGuid, AsyncCallback<Void> callback);

}
