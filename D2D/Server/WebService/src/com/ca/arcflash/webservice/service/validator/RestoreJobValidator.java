package com.ca.arcflash.webservice.service.validator;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.data.restore.RecoveryPointItem;
import com.ca.arcflash.webservice.data.restore.RestoreJob;
import com.ca.arcflash.webservice.data.restore.RestoreJobADItem;
import com.ca.arcflash.webservice.data.restore.RestoreJobItem;
import com.ca.arcflash.webservice.data.restore.RestoreJobItemEntry;
import com.ca.arcflash.webservice.data.restore.RestoreJobNode;
import com.ca.arcflash.webservice.data.restore.RestoreJobType;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.BrowserService;
import com.ca.arcflash.webservice.service.RestoreService;
import com.ca.arcflash.webservice.service.ServiceException;

public class RestoreJobValidator {
	private static final int VOL_GUID_OFFSET = 49;
	
	public void validate(RestoreJob job) throws ServiceException{
		if (job == null)
			throw new ServiceException(FlashServiceErrorCode.Common_NullParameter);
		
		String sessionPath = job.getSessionPath();
		if (StringUtil.isEmptyOrNull(sessionPath))
			throw new ServiceException(FlashServiceErrorCode.RestoreJob_InvalidSessionPath);
		
		String userName = job.getUserName() == null ? "" : job.getUserName();
		String passwd = job.getPassword() == null ? "" : job.getPassword();
		String domain = "";
		int indx = userName.indexOf('\\');
		if (indx > 0) {
			domain = userName.substring(0, indx);
			userName = userName.substring(indx + 1);
		}
		BrowserService.getInstance().validateSource(sessionPath, domain,
				userName, passwd);		
		
		
		String destinationPath = job.getDestinationPath();
		if(job.getJobType()!= RestoreJobType.VM_RESTORE_FILE_TO_ALTER_VM && job.getJobType()!= RestoreJobType.VM_RESTORE_SQLSERVER_TO_ALTER_VM && job.getJobType()!= RestoreJobType.VM_RESTORE_EXCHANGE_TO_ALTER_VM){
			if (!StringUtil.isEmptyOrNull(destinationPath)) {
				String destUser = job.getDestUser() == null ? "" : job
						.getDestUser();
				String destpass = job.getDestPass() == null ? "" : job
						.getDestPass();
				String destDomain = "";
				int destindx = destUser.indexOf('\\');
				if (destindx > 0) {
					destDomain = destUser.substring(0, destindx);
					destUser = destUser.substring(destindx + 1);
				}
	
				checkDestination(destinationPath, destUser, destpass, destDomain);
			}
		}
		
		int jobType = job.getJobType();
		if(jobType != RestoreJobType.Recover_VM && jobType != RestoreJobType.Recover_VM_HYPERV && jobType != RestoreJobType.Recover_VMWARE_VAPP){
			if (job.getNodes() == null || job.getNodes().length == 0)
				throw new ServiceException("", FlashServiceErrorCode.RestoreJob_NoRestoreJobNodes);
		}
		if (job.getJobType() == RestoreJobType.FileSystem && job.getFileSystemOption()==null)
			throw new ServiceException("", FlashServiceErrorCode.RestoreJob_NoFileSystemOption);
		
		List<Integer> list = new ArrayList<Integer>();
		if(jobType != RestoreJobType.Recover_VM && jobType != RestoreJobType.Recover_VM_HYPERV && jobType != RestoreJobType.Recover_VMWARE_VAPP){
				for(RestoreJobNode node : job.getNodes()){
					
					validateJobNode(node);
					RecoveryPointItem[] rpItems = RestoreService.getInstance().getRecoveryPointItems(sessionPath, domain, userName, passwd, String.format("%s%010d", "S", node.getSessionNumber()));				
					
					for (RestoreJobItem item : node.getJobItems()){	
						if(item.getEntries() != null && item.getEntries().length >0) continue;
						
						//IF Full volume restore
						RecoveryPointItem temp = null;
						for(RecoveryPointItem rp: rpItems){
							if(rp.getSubSessionID() == item.getSubSessionNum()){
								temp = rp;
								break;
							}
						}
						
						if(temp !=null){
							int volAttr = temp.getVolAttr();
							if( (volAttr& RecoveryPointItem.BootVol) > 0 || (volAttr & RecoveryPointItem.SysVol) > 0){
								//restoreNotAllowFullVolumeRestore4BootOrSystem=Full volume restore for system or boot volume is not allowed. Run BMR instead.
								throw new ServiceException("", FlashServiceErrorCode.RestoreJob_FullBootVolumeRestoreNotAllowed);
							}
						}
					}
					
					list.add(node.getSessionNumber());			
				}
			}
		
			BrowserService.getInstance().checkRestoreSession(sessionPath, domain,
				userName, passwd, list);
		
		if (job.getJobType() == RestoreJobType.ActiveDirectory) {
			validateADRestoreJob(job);
		}
	}

	
	private void validateADRestoreJob(RestoreJob job) throws ServiceException {
		// RestoreJob, RestoreJobNode, RestoreJobItem and RestoreJobItemEntry have been validated in previoud method.
		for(RestoreJobNode node : job.getNodes()){
			for (RestoreJobItem item : node.getJobItems()){
				for (RestoreJobItemEntry entry : item.getEntries()){
					if (entry.getAdItems() == null || entry.getAdItems().length == 0)
						throw new ServiceException(FlashServiceErrorCode.RestoreJob_RestoreJobItemEntry_NoADItems);
					for(RestoreJobADItem adItem :entry.getAdItems()){
						validateADItem(adItem);
					}
				}
			}
		}
	}

	private void validateADItem(RestoreJobADItem node) throws ServiceException {
		if (node == null)
			throw new ServiceException(FlashServiceErrorCode.RestoreJob_RestoreJobADItem_Invalid);
		if(!node.isAllChild()&&!node.isAllAttribute()){
			if(node.getAttrNames() == null || node.getAttrNames().size() == 0){
				throw new ServiceException(FlashServiceErrorCode.RestoreJob_RestoreJobADItem_Invalid);
			}
		}
	}

	private void checkDestination(String destinationPath, String destUser,
			String destpass, String destDomain) throws ServiceException {
		if (StringUtil.isEmptyOrNull(destinationPath))
			throw new ServiceException(
					FlashServiceErrorCode.RestoreJob_InvalidDestinationPath);
		long pathMaxWithoutHostName = BackupService.getInstance().getPathMaxLength();
		if(destinationPath.length() > pathMaxWithoutHostName+1){
			generatePathExeedLimitException(pathMaxWithoutHostName);
		}
		BrowserService.getInstance().getNativeFacade()
			.validateDestUser(destinationPath, destDomain, destUser, destpass);
	}
	
	private void validateJobNode(RestoreJobNode node) throws ServiceException{
		if (node == null)
			throw new ServiceException(FlashServiceErrorCode.Common_NullParameter);
		
		if (node.getSessionNumber()<=0)
			throw new ServiceException(FlashServiceErrorCode.RestoreJob_RestoreJobNode_InvalidSessionNumber);
		
		if (node.getJobItems() == null || node.getJobItems().length == 0)
			throw new ServiceException(FlashServiceErrorCode.RestoreJob_RestoreJobNode_NoJobItems);	
		
		for (RestoreJobItem item : node.getJobItems()){						
			validateJobItem(item);
		}
	}
	
		
	private void validateJobItem(RestoreJobItem item) throws ServiceException{
		if (item == null)
			throw new ServiceException(FlashServiceErrorCode.Common_NullParameter);
		
		if (StringUtil.isEmptyOrNull(item.getPath()))
			throw new ServiceException(FlashServiceErrorCode.RestoreJob_RestoreJobItem_InvalidaPath);
		
		if (item.getSubSessionNum()<=0)
			throw new ServiceException(FlashServiceErrorCode.RestoreJob_RestoreJobItem_InvalidSubSessionNumber);
		
		//if (item.getEntries() == null || item.getEntries().length == 0)
		//	throw new ServiceException(FlashServiceErrorCode.RestoreJob_RestoreJobItem_NoEntries);
		
		if (item.getEntries()!=null)
			for (RestoreJobItemEntry entry : item.getEntries())
				validateItemEntry(entry);
	}
	
	
	private void validateItemEntry(RestoreJobItemEntry entry) throws ServiceException{
		if (entry == null)
			throw new ServiceException(FlashServiceErrorCode.Common_NullParameter);
		
		if (StringUtil.isEmptyOrNull(entry.getPath()))
			throw new ServiceException(FlashServiceErrorCode.RestoreJob_RestoreJobItemEntry_InvalidPath);
		
		//if (entry.getType()!=CatalogType.File && entry.getType()!=CatalogType.Folder)
		//	throw new ServiceException(FlashServiceErrorCode.RestoreJob_RestoreJobItemEntry_InvalidType);
	}
	
	public void generatePathExeedLimitException(long pathMaxWithoutHostName)
		throws ServiceException {
		throw new ServiceException("" + pathMaxWithoutHostName, 
			        FlashServiceErrorCode.RestoreJob_Destination_TooLong);
	}
}
