package com.ca.arcflash.webservice.util;

import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.service.ServiceException;

public class ConvertErrorCodeUtil {
		
	public static void checkScheduleConfigurationConvert(ServiceException e) {
		if(e.getErrorCode() != null && e.getErrorCode().equals(FlashServiceErrorCode.BackupConfig_ERR_DEST_INUSE))
			e.setErrorCode(FlashServiceErrorCode.CopyJob_ErrorCopyDestInuse);
		else if(e.getErrorCode() != null && e.getErrorCode().equals(FlashServiceErrorCode.BackupConfig_ERR_DEST_WINSYSMSG))
			e.setErrorCode(FlashServiceErrorCode.CopyJob_VaildateCopyDestFailed);
	}
	
	public static void submitCopyJobConvert(ServiceException e) {
		if(e.getErrorCode() != null && e.getErrorCode().equals(FlashServiceErrorCode.BackupConfig_ERR_DEST_WINSYSMSG))
			e.setErrorCode(FlashServiceErrorCode.CopyJob_FailedToSubmitCopy);
	}
	
	
	public static void submitRecoveryVMJobConvert(ServiceException e) {
		if(e.getErrorCode() != null && e.getErrorCode().equals(FlashServiceErrorCode.BackupConfig_ERR_DEST_WINSYSMSG)) {
			e.setErrorCode(FlashServiceErrorCode.Restore_ERR_ValidateAltLocFailed);
		}
	}
	
	public static void submitVMCopyJobConvert(ServiceException e) {
		if(e.getErrorCode() != null && e.getErrorCode().equals(FlashServiceErrorCode.BackupConfig_ERR_DEST_WINSYSMSG)) {
			e.setErrorCode(FlashServiceErrorCode.CopyJob_FailedToSubmitCopy);
		}
	}
	
	public static void submitArchiveRestoreJobConvert(ServiceException e) {
		if(e.getErrorCode() != null && e.getErrorCode().equals(FlashServiceErrorCode.BackupConfig_ERR_DEST_WINSYSMSG)) {
			e.setErrorCode(FlashServiceErrorCode.Restore_ERR_ValidateAltLocFailed);
		}
	}
	

}
