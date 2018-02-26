package com.ca.arcflash.webservice.edge.datasync.job;

import java.util.Observable;

import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import com.ca.arcflash.webservice.data.merge.MergeJobPhase;
import com.ca.arcflash.webservice.data.merge.MergeStatus;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.d2dreg.D2DEdgeRegistration;
import com.ca.arcflash.webservice.edge.d2dreg.EdgeRegInfo;
import com.ca.arcflash.webservice.edge.d2dstatus.SyncD2DStatusService;
import com.ca.arcflash.webservice.service.MergeService;
import com.ca.arcflash.webservice.toedge.IEdgeD2DJobService;

class MergeJobSyncMonitor extends AbstractMergeJobSyncMonitor {
	
	private static MergeJobSyncMonitor instance = new MergeJobSyncMonitor();
	
	public static MergeJobSyncMonitor getInstance() {
		return instance;
	}
	
	private MergeJobSyncMonitor() {
	}

	@Override
	protected EdgeWebServiceCache<IEdgeD2DJobService> createServiceCache() {
		return new EdgeWebServiceCache<IEdgeD2DJobService>(ApplicationType.CentralManagement);
	}

	@Override
	protected Observable getJobService() {
		return MergeService.getInstance();
	}

	@Override
	protected void doInitSync() throws SOAPFaultException, WebServiceException, Exception {
		D2DEdgeRegistration edgeReg = new D2DEdgeRegistration();
		EdgeRegInfo edgeRegInfo = edgeReg.getEdgeRegInfo(ApplicationType.CentralManagement);
		if (edgeRegInfo == null) {
			return;
		}
		
		MergeStatus status = MergeService.getInstance().getMergeJobStatus();
		if (status == null) {
			return;
		}
		
		doSync(new MergeStatus[] { status });
	}
	
	protected boolean doSync(MergeStatus[] syncData) {
		boolean result = super.doSync(syncData);
		if (result) {
			if (syncData != null && syncData.length != 0) {
				for (MergeStatus statusData : syncData) {
					if (statusData.getJobMonitor() != null
							&& (statusData.getJobMonitor().getDwMergePhase() == MergeJobPhase.EJP_END_OF_JOB.ordinal() 
							|| statusData.getJobMonitor().getDwMergePhase() == MergeJobPhase.EJP_PROC_EXIT.ordinal())) {

						try {
							SyncD2DStatusService.getInstance().syncD2DStatus();
						} catch (Exception e) {
							logSyncErrorMessage("doSync status failed, error message = "+ e.getMessage(), e);

						}
					}
				}
			}
		}
		return result;

	}
}
