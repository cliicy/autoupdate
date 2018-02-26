package com.ca.arcflash.webservice.service.validator;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.data.restore.CopyJob;
import com.ca.arcflash.webservice.data.restore.RestoreJobNode;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.BrowserService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.ServiceException;

public class CopyJobValidator {
	
	private static final Logger logger = Logger.getLogger(CopyJobValidator.class);
	private static final int COMPRESSIONNONEVHD = 0x1000;
	
	public void validate(CopyJob job) throws ServiceException{
		if (job == null)
			throw new ServiceException(FlashServiceErrorCode.Common_NullParameter);
		

		if (!CommonService.getInstance().isFolderAccessible(job.getSessionPath(), 
				                           null, job.getUserName(), job.getPassword()))
			throw new ServiceException(FlashServiceErrorCode.CopyJob_InvalidSessionPath);
		
//		if (!CommonService.getInstance().isFolderAccessible(job.getDestinationPath(), 
//				null, job.getDestinationUserName(), job.getDestinationPassword())){
//			throw new ServiceException(FlashServiceErrorCode.CopyJob_InvalidDestinationPath);
//		}

		List<Integer> list = new ArrayList<Integer>();
		list.add(job.getSessionNumber());
		if (job.getNodes() != null) {
			for (RestoreJobNode node : job.getNodes()) {
				list.add(node.getSessionNumber());
			}
		}

		String sessionPath = job.getSessionPath();
		String userName = job.getUserName() == null ? "" : job.getUserName();
		String passwd = job.getPassword() == null ? "" : job.getPassword();
		String domain = "";
		int indx = userName.indexOf('\\');
		if (indx > 0) {
			domain = userName.substring(0, indx);
			userName = userName.substring(indx + 1);
		}
		try {
			BrowserService.getInstance().checkRestoreSession(sessionPath,
					domain, userName, passwd, list);
		} catch (ServiceException se) {
			if (FlashServiceErrorCode.Restore_ERR_SessNotExist.equals(se
					.getErrorCode())) {
				se.setErrorCode(FlashServiceErrorCode.CopyJob_ERR_SessNotExist);
				throw se;
			}
		}
		
		if(job.getCompressionLevel() == COMPRESSIONNONEVHD){
			if(job.getEncryptTypeCopySession() > 0 
					|| !StringUtil.isEmptyOrNull(job.getEncryptPasswordCopySession())){
				throw new ServiceException(FlashServiceErrorCode.CopyJob_ErrorCopyVHDEncryption);
			}
		}
	}

	public int validateDestPath(CopyJob job)
		throws ServiceException {

		logger.debug("validateDestPath - start");
		String path = job.getDestinationPath();
		
		String username = job.getDestinationUserName();
		String password = job.getDestinationPassword();
		if (path == null)
			path = "";
		
		if (username == null)
			username = "";
		if (password == null)
			password = "";
		
		logger.debug("path" + path);
		logger.debug("username" + username);
		String domain = "";
		int indx = username.indexOf('\\');
		if (indx > 0) {
			domain = username.substring(0, indx);
			username = username.substring(indx + 1);
		}
		long pathMaxWithoutHostName = BackupService.getInstance().getPathMaxLength();
		if(path.length() > pathMaxWithoutHostName+1){
			generatePathExeedLimitException(pathMaxWithoutHostName);
		}
		 BrowserService.getInstance().getNativeFacade()
				.validateDestUser(path, domain, username, password);	
		
		logger.debug("validateDestPath - end");
		return (int)pathMaxWithoutHostName;
	}
	public int validateDestPath(CopyJob job, String folderName)
	throws ServiceException {
		
		logger.debug("validateDestPath - start");
		String path = job.getDestinationPath();
		
		String username = job.getDestinationUserName();
		String password = job.getDestinationPassword();
		if (path == null)
			path = "";
		
		if (username == null)
			username = "";
		if (password == null)
			password = "";
		
		logger.debug("path" + path);
		logger.debug("username" + username);
		String domain = "";
		int indx = username.indexOf('\\');
		if (indx > 0) {
			domain = username.substring(0, indx);
			username = username.substring(indx + 1);
		}
		long pathMaxWithoutHostName = BackupService.getInstance().getPathMaxLength();
		if(path.length() > pathMaxWithoutHostName+1){
			generatePathExeedLimitException(pathMaxWithoutHostName, folderName);
		}
		BrowserService.getInstance().getNativeFacade()
		.validateDestUser(path, domain, username, password);	
		
		logger.debug("validateDestPath - end");
		return (int)pathMaxWithoutHostName;
	}
	public void generatePathExeedLimitException(long pathMaxWithoutHostName)
			throws ServiceException {
		long pathMaxLength = pathMaxWithoutHostName - ServiceContext.getInstance().getLocalMachineName().length();
		throw new ServiceException("" + pathMaxLength, 
				        FlashServiceErrorCode.CopyJob_Destination_TooLong);
	}
		
	public void generatePathExeedLimitException(long pathMaxWithoutHostName,String folder)
			throws ServiceException {
		long pathMaxLength = pathMaxWithoutHostName - folder.length();
		throw new ServiceException("" + pathMaxLength, 
				FlashServiceErrorCode.CopyJob_Destination_TooLong);
	}
}
