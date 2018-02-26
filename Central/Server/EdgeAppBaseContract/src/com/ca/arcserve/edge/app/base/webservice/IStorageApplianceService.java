package com.ca.arcserve.edge.app.base.webservice;

import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.storageappliance.StorageApplianceInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.storageappliance.StorageAppliancePagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.storageappliance.StorageAppliancePagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.storageappliance.StorageApplianceValidationResponse;

public interface IStorageApplianceService {
	public void AddStorageAppliance(StorageApplianceInfo info, GatewayId gatewayId) throws EdgeServiceFault;
	public StorageAppliancePagingResult getInfrastructureListByPaging(
			StorageAppliancePagingConfig config) throws EdgeServiceFault;
	public void deleteInfrastructures(int[] infrastructuresIds,GatewayId gatewayId) throws EdgeServiceFault;
	
	public StorageApplianceInfo getInfrastructureById(int infraId) throws EdgeServiceFault;
	//Jan sprint
	public StorageApplianceValidationResponse validateNASServer(GatewayId gatewayId, StorageApplianceInfo info) throws EdgeServiceFault;
	//For AQA to use
	public StorageApplianceInfo getInfrastructureByHostnames(String serverIp,String dataIp) throws EdgeServiceFault;
}
