package com.ca.arcserve.edge.app.base.webservice;

import javax.jws.WebService;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;

@WebService(targetNamespace="http://webservice.d2dresync.edge.arcserve.ca.com/")
public interface IEdgeD2DReSyncService {
	void EdgeD2DReSync(int[] d2dHostId)throws EdgeServiceFault;
	public void submitD2DSyncForGroup(int groupID, int groupType) throws EdgeServiceFault;
}
