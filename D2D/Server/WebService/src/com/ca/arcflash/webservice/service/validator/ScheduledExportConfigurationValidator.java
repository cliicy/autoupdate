package com.ca.arcflash.webservice.service.validator;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.data.export.ScheduledExportConfiguration;
import com.ca.arcflash.webservice.data.vsphere.BackupVM;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.BrowserService;
import com.ca.arcflash.webservice.service.CopyService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.VMCopyService;

public class ScheduledExportConfigurationValidator {
	private static final Logger logger = Logger.getLogger(ScheduledExportConfigurationValidator.class);
	
	public long validateDestPath(ScheduledExportConfiguration configuration) throws ServiceException {
		logger.debug("validateDestPath - start");
		
		String destPath = configuration.getDestination();
		String userName = configuration.getDestUserName();
		String password = configuration.getDestPassword();
		
		destPath = destPath==null ? "":destPath;
		userName = userName==null ? "":userName;
		password = password==null ? "":password;
		logger.info("destPath: " + destPath);
		logger.info("userName: " + userName);
		
		String domain="";
		int indx = userName.indexOf('\\');
		if (indx > 0) {
			domain = userName.substring(0, indx);
			userName = userName.substring(indx + 1);
		}
		long pathMaxWithoutHostName = BackupService.getInstance().getPathMaxLength();
		if(destPath.length() > pathMaxWithoutHostName+1){
			generatePathExeedLimitException(pathMaxWithoutHostName);
		}
		BrowserService.getInstance().getNativeFacade().validateDestUser(destPath, domain, userName, password);
		ScheduledExportConfiguration conf = CopyService.getInstance().getScheduledExportConfiguration();
		String path = BackupService.getInstance().appendHostNameIfNeeded(destPath, null, userName, password, 1);
		int backslash = 1;
		if(destPath.endsWith("\\") || destPath.endsWith("/")){
			backslash = 0;
		}
		if(path.length() > pathMaxWithoutHostName + backslash){
			generatePathExeedLimitException(pathMaxWithoutHostName);
		}
		
		// output some debug log only
		if (conf != null) {
			if (conf.getDestination() == null)
				logger.info("Original CRP destination: null");
			else
				logger.info("Original CRP destination: " + conf.getDestination());
		}

		if (path != null)
			logger.info("New CRP destination: " + path);
		// end
		
		if(configuration.isEnableScheduledExport()) {
			String userNameWithDomain = configuration.getDestUserName() != null ? configuration.getDestUserName() : "";
			//validate is when: 1. the first time; 2. destination changed; 
			//3. If saved path is null (disabled case), and current path is not null, also check.
			if(conf == null || (conf.getDestination() != null &&!conf.getDestination().equalsIgnoreCase(path))
					|| conf.getDestination() == null && path != null) {
				if(BrowserService.getInstance().getNativeFacade().AFCheckFolderContainsBackup(null, userNameWithDomain, password, path)){
					throw new ServiceException(FlashServiceErrorCode.CopyJob_ErrorCopyDestInuse);
				}
			}
		}
		
		
		logger.debug("validateDestPath - end");
		
		return 0;
	}
	
	public void generatePathExeedLimitException(long pathMaxWithoutHostName)
		throws ServiceException {
		long pathMaxLength = pathMaxWithoutHostName - ServiceContext.getInstance().getLocalMachineName().length();
		throw new ServiceException("" + pathMaxLength, 
				        FlashServiceErrorCode.ScheduledExportConfig_ERR_FileNameTooLong);
	}
	
	public long validateDestPath(ScheduledExportConfiguration configuration, BackupVM vm) throws ServiceException {
		String destPath = configuration.getDestination();
		String userName = configuration.getDestUserName();
		String password = configuration.getDestPassword();
		
		destPath = destPath==null ? "":destPath;
		userName = userName==null ? "":userName;
		password = password==null ? "":password;
		logger.info("destPath: " + destPath);
		logger.info("userName: " + userName);
		
		String domain="";
		int indx = userName.indexOf('\\');
		if (indx > 0) {
			domain = userName.substring(0, indx);
			userName = userName.substring(indx + 1);
		}
		long pathMaxWithoutHostName = BackupService.getInstance().getPathMaxLength();
		if(destPath.length() > pathMaxWithoutHostName+1){
			generatePathExeedLimitException(pathMaxWithoutHostName);
		}
		BrowserService.getInstance().getNativeFacade().validateDestUser(destPath, domain, userName, password);
		ScheduledExportConfiguration conf = VMCopyService.getInstance().getScheduledExportConfiguration(vm);
		String path = VMCopyService.getInstance().appendVMInfoIfNeeded(destPath, VMCopyService.getInstance().filterVMName(vm.getVmName())+ "@" + vm.getEsxServerName().trim(), vm
				.getInstanceUUID(), true);
		int backslash = 1;
		if(destPath.endsWith("\\") || destPath.endsWith("/")){
			backslash = 0;
		}
		if(path.length() > pathMaxWithoutHostName + backslash){
			generatePathExeedLimitException(pathMaxWithoutHostName);
		}
		
		// output some debug log only
		if (conf != null) {
			if (conf.getDestination() == null)
				logger.info("Original CRP destination: null");
			else
				logger.info("Original CRP destination: " + conf.getDestination());
		}

		if (path != null)
			logger.info("New CRP destination: " + path);
		// end
		
		if(configuration.isEnableScheduledExport()) {
			String userNameWithDomain = configuration.getDestUserName() != null ? configuration.getDestUserName() : "";
			//validate is when: 1. the first time; 2. destination changed; 
			//3. If saved path is null (disabled case), and current path is not null, also check.
			if(conf == null || (conf.getDestination() != null &&!conf.getDestination().equalsIgnoreCase(path))
					|| conf.getDestination() == null && path != null) {
				if(BrowserService.getInstance().getNativeFacade().AFCheckFolderContainsBackup(null, userNameWithDomain, password, path)){
					throw new ServiceException(FlashServiceErrorCode.CopyJob_ErrorCopyDestInuse);
				}
			}
		}
		
		
		logger.debug("validateDestPath - end");
		
		return 0;
	}

}
