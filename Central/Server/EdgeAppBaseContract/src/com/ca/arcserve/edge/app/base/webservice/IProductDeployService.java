package com.ca.arcserve.edge.app.base.webservice;

import java.util.List;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DeployTargetDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Node;
import com.ca.arcserve.edge.app.base.webservice.contract.productdeploy.ProductImagesInfo;

public interface IProductDeployService extends IRemoteProductDeployService{
	void submitRemoteDeploy(List<DeployTargetDetail> targets) throws EdgeServiceFault;
	List<Node> cancelRemoteDeploy(List<Node> sourceNodes, String errorMessage) throws EdgeServiceFault;
	void deleteRemoteDeploy(List<DeployTargetDetail> targets) throws EdgeServiceFault;
	
	ProductImagesInfo getProductImagesInfo()  throws EdgeServiceFault;
	
	@Deprecated
	List<DeployTargetDetail> getDeployTargets( List<Integer> hostIds) throws EdgeServiceFault;
}
