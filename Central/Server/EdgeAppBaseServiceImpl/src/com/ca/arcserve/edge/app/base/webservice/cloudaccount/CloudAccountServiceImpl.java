package com.ca.arcserve.edge.app.base.webservice.cloudaccount;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.webservice.data.archive.ArchiveCloudDestInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgePolicy;
import com.ca.arcserve.edge.app.base.appdaos.ICloudAccountDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.ICloudAccountService;
import com.ca.arcserve.edge.app.base.webservice.contract.common.PagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.common.PagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.destination.cloudaccount.ASCloudAccount;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.FileCopySettingWrapper;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.gateway.EntityType;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl;

public class CloudAccountServiceImpl implements ICloudAccountService{
	private ICloudAccountDao cloudDao = DaoFactory.getDao(ICloudAccountDao.class);
	private IEdgePolicyDao edgePolicyDao = DaoFactory.getDao( IEdgePolicyDao.class );
	private PolicyManagementServiceImpl policyImpl = new PolicyManagementServiceImpl();
	private static final Logger logger = Logger.getLogger( CloudAccountServiceImpl.class );
	private IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);

	@Override
	public ArchiveCloudDestInfo saveCloudAccount(ArchiveCloudDestInfo cloudInfo, GatewayId gatewayId) throws EdgeServiceFault {
		int id = cloudInfo.getId() < 0 ? 0 : cloudInfo.getId();
		int[] output = new int[1];
		String details;
		try {
			details = CommonUtil.marshal(cloudInfo);
		} catch (JAXBException e) {
			logger.error("marshal cloud account failed, error message = " + e.getMessage());
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "marshal cloud account content failed.");
		}
		cloudDao.as_edge_cloud_add_cloud_account(id, cloudInfo.getAccountName(), new Long(cloudInfo.getcloudVendorType()).intValue(), new Long(cloudInfo.getCloudSubVendorType()).intValue(), details, output);
		
		if(id > 0){
			try{
				modifyDependentPolicies(cloudInfo);
			}catch(Exception e){
				logger.error("Failed to update dependent policies for cloud account :: " + cloudInfo.getAccountName(), e);
			}
		}
		cloudInfo.setId(output[0]);
		
		if(gatewayId!=null)
			gatewayService.bindEntity(gatewayId, cloudInfo.getId(), EntityType.CloudAccount);
		
		return cloudInfo;
		
	}
	
	private void modifyDependentPolicies(ArchiveCloudDestInfo cloudInfoToModify) throws EdgeServiceFault {
		logger.info("Checking dependent policies.....");
		List<EdgePolicy> policyList = new ArrayList<EdgePolicy>();
		edgePolicyDao.as_edge_policy_list(0, 1, policyList);
		
		if(policyList != null){
			for (EdgePolicy edgePolicy : policyList) {
				
				UnifiedPolicy policy = policyImpl.loadUnifiedPolicyById(edgePolicy.getId());
				policy.setId(edgePolicy.getId());
				
				List<FileCopySettingWrapper> fileCopySettingsWrapper = policy.getFileCopySettingsWrapper();
				
				if(fileCopySettingsWrapper != null && fileCopySettingsWrapper.size() > 0){
					
					for (FileCopySettingWrapper fileCopySettingWrapper : fileCopySettingsWrapper) {
						ArchiveCloudDestInfo cloudConfig = fileCopySettingWrapper.getArchiveConfiguration().getCloudConfig();
						if(cloudConfig != null && cloudConfig.getId() == cloudInfoToModify.getId()){
							logger.info("Found dependent policy :: " + policy.getName());
							fileCopySettingWrapper.getArchiveConfiguration().setCloudConfig(cloudInfoToModify);
							policyImpl.updateUnifiedPolicy(policy);
							break;
						}
					}
					
				}
				
				if(policy.getFileArchiveConfiguration() != null){
					ArchiveCloudDestInfo cloudConfig = policy.getFileArchiveConfiguration().getCloudConfig();
					if(cloudConfig != null && cloudConfig.getId() == cloudInfoToModify.getId()){
						logger.info("Found dependent policy :: " + policy.getName());
						policy.getFileArchiveConfiguration().setCloudConfig(cloudInfoToModify);
						policyImpl.updateUnifiedPolicy(policy);
					}
				}

			}
		}
		
	}

	private List<ArchiveCloudDestInfo> getArchiveCloudDestInfo(
			List<ASCloudAccount> cloudAccounts, List<Integer> existingCloudAccountIDs, int currentGatewayId) throws EdgeServiceFault {
		
		List<ArchiveCloudDestInfo> archiveCloudInfoList = new ArrayList<>();
		for (ASCloudAccount asCloudAccount : cloudAccounts) {
			try {
				if(existingCloudAccountIDs == null || (existingCloudAccountIDs != null && !existingCloudAccountIDs.contains(asCloudAccount.getId())))
				{
					ArchiveCloudDestInfo cloudDestInfo = CommonUtil.unmarshal(asCloudAccount.getDetails(), ArchiveCloudDestInfo.class);
					cloudDestInfo.setId(asCloudAccount.getId());
					cloudDestInfo.setSiteName(asCloudAccount.getName());
					cloudDestInfo.setGatewayId(currentGatewayId);
					archiveCloudInfoList.add(cloudDestInfo);
				}
			} catch (JAXBException e) {
				logger.error("unmarshal cloud account failed, error message = " + e.getMessage());
				throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "unmarshal cloud account content failed.");
			}
		}
		return archiveCloudInfoList;
	}
	
	@Override
	public Set<String> deleteCloudAccounts(int[] cloudAccountIds)
			throws EdgeServiceFault {
		Set<String> unDeletableCloudAccounts = new HashSet<String>();
		List<EdgePolicy> policyList = new ArrayList<EdgePolicy>();
		edgePolicyDao.as_edge_policy_list(0, 1, policyList);
		for (int i = 0; i < cloudAccountIds.length; i++) {
			int cloudAccountToDelete = cloudAccountIds[i];
			String dependentCloudName = checkDependentPolicy(policyList,cloudAccountToDelete);
			if(dependentCloudName != null)
				unDeletableCloudAccounts.add(dependentCloudName);
			else{
				cloudDao.as_edge_cloud_delete(cloudAccountToDelete);
				logger.info( "deleteCloudAccounts() unbindEntity cloudAccountToDelete: "+cloudAccountToDelete);
				gatewayService.unbindEntity(cloudAccountToDelete, EntityType.CloudAccount);
			}
		}
		return unDeletableCloudAccounts;
		
	}

	private String checkDependentPolicy(List<EdgePolicy> policyList, int cloudAccountToDelete)
			throws EdgeServiceFault {
		if(policyList != null){
			for (EdgePolicy edgePolicy : policyList) {
				String policyXml = edgePolicy.getPolicyxml();
				policyXml=EdgeCommonUtil.decryptXml(policyXml);
				try {
					UnifiedPolicy policy = CommonUtil.unmarshal(policyXml, UnifiedPolicy.class);
					List<FileCopySettingWrapper> fileCopySettingsWrapper = policy.getFileCopySettingsWrapper();
					if(fileCopySettingsWrapper != null && fileCopySettingsWrapper.size() > 0){
						for (FileCopySettingWrapper fileCopySettingWrapper : fileCopySettingsWrapper) {
							ArchiveCloudDestInfo cloudConfig = fileCopySettingWrapper.getArchiveConfiguration().getCloudConfig();
							if(cloudConfig != null && cloudConfig.getId() == cloudAccountToDelete){
								return cloudConfig.getAccountName();
							}
						}
					}
					if(policy.getFileArchiveConfiguration() != null){
						ArchiveCloudDestInfo cloudConfig = policy.getFileArchiveConfiguration().getCloudConfig();
						if(cloudConfig != null && cloudConfig.getId() == cloudAccountToDelete){
							return cloudConfig.getAccountName();
						}
					}
				} catch (JAXBException e) {
					throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, e.getMessage());
				}
				
			}
		}
		return null;
	}

	@Override
	public List<ArchiveCloudDestInfo> getCloudAccountById(int cloudAccountId)
			throws EdgeServiceFault {
		List<ASCloudAccount> cloudAccounts = new ArrayList<>();
		cloudDao.as_edge_cloud_get_by_id(cloudAccountId, cloudAccounts);
		return getArchiveCloudDestInfo(cloudAccounts, null,0);
	}
	
	@Override
	public List<ArchiveCloudDestInfo> getCloudAccountsForDetails(String accountName, int id)
			throws EdgeServiceFault {
		List<ASCloudAccount> cloudAccounts = new ArrayList<>();
		cloudDao.as_edge_cloud_getCloudAccountsForDetails(accountName, id, cloudAccounts);
		return getArchiveCloudDestInfo(cloudAccounts, null,0);
	}

	@Override
	public PagingResult<ArchiveCloudDestInfo> getAllCloudAccountsByPaging(PagingConfig config, int gatewayId) throws EdgeServiceFault {
		int[] totalCount = new int[1];
		List<ASCloudAccount> cloudAccounts = new ArrayList<>();
		cloudDao.as_edge_cloud_getCloudAccounts_by_paging(config.getCount(), config.getStartIndex(), config.getOrderType().value(), "id", gatewayId, totalCount, cloudAccounts);
		List<ArchiveCloudDestInfo> archiveCloudInfoList = getArchiveCloudDestInfo(cloudAccounts, null, gatewayId);
		PagingResult<ArchiveCloudDestInfo> result = PagingResult.create(config, archiveCloudInfoList);
		return result;
	}

	@Override
	public List<ArchiveCloudDestInfo> getAllCloudAccounts(int cloudType,
			int cloudSubType, List<Integer> existingCloudAccountIDs,
			int currentGatewayId) throws EdgeServiceFault {
		List<ASCloudAccount> cloudAccounts = new ArrayList<>();
		cloudDao.as_edge_cloud_getCloudAccounts(cloudType,cloudSubType, currentGatewayId, cloudAccounts);
		
		return getArchiveCloudDestInfo(cloudAccounts, existingCloudAccountIDs, currentGatewayId);
	}

}
