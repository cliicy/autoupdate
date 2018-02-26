package com.ca.arcserve.edge.app.base.webservice;

import java.util.List;

import javax.jws.WebService;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
@WebService(targetNamespace="http://webservice.srm.edge.arcserve.ca.com/")
public interface IEdgeSRMService {
	boolean InvokeGetSrmInfo(int hostID, String protocol, String host, int port, int command)  throws EdgeServiceFault;
	boolean IsSrmProbeDone();
	void SrmProbeNow();
	void SrmProbeNodes(List<Integer> nodesIDList);
	void NodeDeleteProbeNodes(List<Integer> nodesIDList);
}
