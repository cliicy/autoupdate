package com.ca.arcflash.ui.client.monitor;

import com.ca.arcflash.ha.model.ARCFlashNodesSummary;
import com.ca.arcflash.ha.model.SummaryModel;
import com.ca.arcflash.ha.model.VMSnapshotsInfo;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.exception.ServiceConnectException;
import com.ca.arcflash.ui.client.exception.ServiceInternalException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("service/monitor")
public interface MonitorService extends RemoteService{
	
	ARCFlashNodesSummary queryFlashNodesSummary() throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	FailoverJobScript getFailoverJobScript(String uuid) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	VMSnapshotsInfo[] getSnapshots(String uuid) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	void startFailover(String uuid, VMSnapshotsInfo vmSnapInfo) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	boolean isFailoverJobFinish(String afGuid) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	//Get summary panel information
	SummaryModel getSummaryModel(String afguid, String vmuuid, String vmname) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	String getCurrentRunningSnapShotGuid(String afGuid) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	int shutDownVM(String afGuid) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
	
	void removeMonitee(String afGuid) throws BusinessLogicException, ServiceConnectException, ServiceInternalException;
}
