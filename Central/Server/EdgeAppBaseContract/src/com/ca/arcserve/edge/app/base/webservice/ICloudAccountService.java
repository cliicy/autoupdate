package com.ca.arcserve.edge.app.base.webservice;

import java.util.List;
import java.util.Set;

import com.ca.arcflash.webservice.data.archive.ArchiveCloudDestInfo;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.common.PagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.common.PagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;

public interface ICloudAccountService {
	
	public ArchiveCloudDestInfo saveCloudAccount(ArchiveCloudDestInfo cloudInfo, GatewayId gatewayId)throws EdgeServiceFault;
	
	public Set<String> deleteCloudAccounts(int[] cloudAccountIds) throws EdgeServiceFault;
	
	public List<ArchiveCloudDestInfo> getCloudAccountById(int cloudAccountId) throws EdgeServiceFault;
	
	public List<ArchiveCloudDestInfo> getCloudAccountsForDetails(String accountName, int id) throws EdgeServiceFault;

	public PagingResult<ArchiveCloudDestInfo> getAllCloudAccountsByPaging(
			PagingConfig config, int gatewayId)throws EdgeServiceFault;

	public List<ArchiveCloudDestInfo> getAllCloudAccounts(int cloudType,
			int cloudSubType, List<Integer> existingCloudAccountIDs,
			int currentGatewayId) throws EdgeServiceFault;

}
