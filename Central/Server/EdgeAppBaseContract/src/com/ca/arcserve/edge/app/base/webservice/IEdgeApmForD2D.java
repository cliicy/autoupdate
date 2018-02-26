package com.ca.arcserve.edge.app.base.webservice;

import com.ca.arcflash.webservice.data.PM.PMResponse;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;

public interface IEdgeApmForD2D {
	public PMResponse SubmitAPMRequestD2D(int RequestType) throws EdgeServiceFault;
}
