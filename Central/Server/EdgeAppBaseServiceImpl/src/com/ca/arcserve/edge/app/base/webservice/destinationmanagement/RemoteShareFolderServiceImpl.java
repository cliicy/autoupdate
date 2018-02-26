package com.ca.arcserve.edge.app.base.webservice.destinationmanagement;

import java.util.concurrent.locks.Lock;

import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.data.NetworkPath;
import com.ca.arcflash.webservice.data.browse.FileFolderItem;
import com.ca.arcflash.webservice.data.browse.Volume;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.BrowserService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.internal.RemoteFolderConnCache;
import com.ca.arcflash.webservice.util.CommonServiceUtilImpl;
import com.ca.arcserve.edge.app.base.serviceexception.D2DServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean.FaultType;
import com.ca.arcserve.edge.app.base.util.D2DWebServiceErrorMessages;
import com.ca.arcserve.edge.app.base.webservice.IRemoteShareFolderService;

public class RemoteShareFolderServiceImpl implements IRemoteShareFolderService{

	private static final Logger logger = Logger.getLogger( RemoteShareFolderServiceImpl.class );
	
	@Override
	public FileFolderItem getFileFolderWithCredentials(String path,String user, String pwd) throws EdgeServiceFault{
		
		logger.info("path="+path+",user="+user);
		
		try {
			
			return BrowserService.getInstance().getFileFolder(path, user, pwd);
			
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(),e);
			
			throw D2DServiceFault.getFault(e.getErrorCode(), e.getMessage());
			
		} catch(Exception e){
			logger.error(e.getMessage(),e);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, e.getMessage());			
		}
	}
	
	@Override
	public boolean createFolder(String parentPath, String subDir) throws EdgeServiceFault{
		
		logger.info("parentPath="+parentPath+",subDir="+subDir);
		
		Lock lock = null;
		try {
			lock = RemoteFolderConnCache.getInstance().getLockByPath(parentPath);
			//TODO why need to lock
			//if lock != null, this path is a remote path
//			if(lock != null) {
//				lock.lock(); 
//				RemoteFolderConnCache.reEstalishConnetion(null);
//			}
			BrowserService.getInstance().createFolder(parentPath, subDir);
			return true;
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(),e);
			throw D2DServiceFault.getFault(e.getErrorCode(), e.getMessage());
		
		} catch(Exception e){
			logger.error(e.getMessage(),e);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, e.getMessage());
			
		}finally {
//			if(lock != null){
//				lock.unlock();
//			}
				
		}
		
	}
	
	@Override
	public NetworkPath[] getMappedNetworkPath(String userName) throws EdgeServiceFault{
		
		logger.info("usrename=" + userName);
		
		try {

			NetworkPath[] pathArr = CommonService.getInstance().getMappedNetworkPath(userName);
			logger.debug("getMappedNetworkPath(String) - end");
			return pathArr;

		} catch (ServiceException e) {
				
			// TODO Auto-generated catch block
			logger.error(e.getMessage(),e);
			throw EdgeServiceFault.getFault(e.getErrorCode(), e.getMessage());
				
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, e.getMessage());
			
		}
		
	}
	
	@Override
	public long getDestDriveType(String path) throws EdgeServiceFault{

		logger.info("path=" + path);
		
		try {
			
			return BackupService.getInstance().getDestDriveType(path);
		
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(),e);
			throw D2DServiceFault.getFault(e.getErrorCode(), e.getMessage());
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, e.getMessage());
			
		}
	}
	
	@Override
	public Volume[] getVolumes() throws EdgeServiceFault{
		
		try {
		
			Volume[] archiveVolumes = BrowserService.getInstance().getVolumes(
					false, null, null, null);
			
			return archiveVolumes;			
		
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(),e);
			throw D2DServiceFault.getFault(e.getErrorCode(), e.getMessage());
			
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, e.getMessage());
			
		}
		
	}
	
	@Override
	public String getMntPathFromVolumeGUID(String strGUID) throws EdgeServiceFault{
		
		logger.info("volumen guid=" + strGUID);
		
		try {
		
			return CommonService.getInstance().getMntPathFromVolumeGUID(strGUID);
		
		}catch(Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(),e);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, e.getMessage());
			
		}
	}
	
	@Override
	public long validateDest(String path, String domain, String user, String pwd) throws EdgeServiceFault{
		
		logger.info("path="+path+",domain="+domain+",user="+user);
		
		Lock lock = null;
		try {
			lock = RemoteFolderConnCache.getInstance().getLockByPath(path);
			if(lock != null) {
				lock.lock();
				RemoteFolderConnCache.getInstance().disconnectAllToMachine(RemoteFolderConnCache.getMachineName(path));
			}
			// TODO getSession
			//RemoteFolderConnCache.cachePathToSession(getSession(), path, domain, user, pwd);
			long validateDest = BrowserService.getInstance().validateDest(path, domain,user, pwd);
			return validateDest;
		} catch (ServiceException e) {
			logger.error(e.getMessage(), e);
			
			if (FlashServiceErrorCode.BackupConfig_ERR_ValidateDestFailed.equals(e.getErrorCode())) {
				throw generateValidateFailException(path);
			} else {
				throw convert(e);
			}
		}catch(Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(),e);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, e.getMessage());
			
		}finally {
			if(lock != null)
				lock.unlock();
		}
	}
	
	private EdgeServiceFault generateValidateFailException(String path) {
		String errorCode = FlashServiceErrorCode.BackupConfig_ERR_ValidateDestFailed;
		String errorMessage = D2DWebServiceErrorMessages.getMessage(errorCode, path);
		
		EdgeServiceFault fault = EdgeServiceFault.getFault(errorCode, errorMessage);
		fault.getFaultInfo().setFaultType(FaultType.D2D);
		
		return fault;
	}

	private CommonServiceUtilImpl commonUtil = new CommonServiceUtilImpl(null, null);
	
	private EdgeServiceFault convert(ServiceException serviceException) {
		SOAPFaultException soapFaultException;
		
		if (serviceException.getWebServiceCause() != null) {
			soapFaultException = serviceException.getWebServiceCause();
		} else {
			soapFaultException = commonUtil.convertServiceException2AxisFault(serviceException);			
		}
		
		String errorCode = soapFaultException.getFault().getFaultCodeAsQName().getLocalPart();
		String errorMessage = soapFaultException.getFault().getFaultString();
		
		EdgeServiceFault fault = D2DServiceFault.getFault(errorCode, errorMessage);
		
		return fault;
	}
	
	@Override
	public long validateDestForMode(String path, String domain, String user, String pwd,int mode)throws EdgeServiceFault{

		logger.info("path="+path+",domain="+domain+",user="+user+",mode="+mode);
		
		Lock lock = null;
		try {
			lock = RemoteFolderConnCache.getInstance().getLockByPath(path);
			if(lock != null) {
				lock.lock();
				RemoteFolderConnCache.getInstance().disconnectAllToMachine(RemoteFolderConnCache.getMachineName(path));
			}
			//if(!(mode == 2)) // 	public static final int COPY_MODE = 2;
			//	RemoteFolderConnCache.cachePathToSession(getSession(),path, domain, user, pwd);
	
			long validateDest = BrowserService.getInstance().validateDest(path, domain,user, pwd,mode);
			return validateDest;
		
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(),e);
			throw D2DServiceFault.getFault(e.getErrorCode(), e.getMessage());
		
		}catch(Exception e) {
			// TODO Auto-generated catch block
			logger.error(e.getMessage(),e);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, e.getMessage());
			
		}finally {
			if(lock != null)
				lock.unlock();
		}
	}

}
