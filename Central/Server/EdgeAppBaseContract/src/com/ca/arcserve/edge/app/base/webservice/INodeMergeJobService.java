package com.ca.arcserve.edge.app.base.webservice;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;

public interface INodeMergeJobService {
	
	int pauseMergeJob(int nodeId) throws EdgeServiceFault;
	int resumeMergeJob(int nodeId) throws EdgeServiceFault;
	void pauseMultipleMergeJob(int[] nodeIds) throws EdgeServiceFault;
	void resumeMultipleMergeJob(int[] nodeIds) throws EdgeServiceFault;
	
	int pauseVMMergeJob(int vmHostId) throws EdgeServiceFault;
	int resumeVMMergeJob(int vmHostId) throws EdgeServiceFault;
	void pauseMultipleVMMergeJob(int[] vmHostIds) throws EdgeServiceFault;
	void resumeMultipleVMMergeJob(int[] vmHostIds) throws EdgeServiceFault;

}
