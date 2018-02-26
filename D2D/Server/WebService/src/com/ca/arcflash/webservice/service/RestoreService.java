package com.ca.arcflash.webservice.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;

import javax.servlet.http.HttpSession;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;
import org.quartz.Scheduler;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.service.common.FlashSwitch;
import com.ca.arcflash.service.common.FlashSwitchDefine;
import com.ca.arcflash.service.jni.model.JBackupItem;
import com.ca.arcflash.service.jni.model.JNetConnInfo;
import com.ca.arcflash.service.jni.model.JRestorePoint;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupType;
import com.ca.arcflash.webservice.data.backup.RpsPolicy4D2D;
import com.ca.arcflash.webservice.data.catalog.CatalogItem;
import com.ca.arcflash.webservice.data.catalog.PagedCatalogItem;
import com.ca.arcflash.webservice.data.job.rps.RestoreJobArg;
import com.ca.arcflash.webservice.data.restore.AlternativePath;
import com.ca.arcflash.webservice.data.restore.CopyJob;
import com.ca.arcflash.webservice.data.restore.RecoveryPoint;
import com.ca.arcflash.webservice.data.restore.RecoveryPointItem;
import com.ca.arcflash.webservice.data.restore.RestoreJob;
import com.ca.arcflash.webservice.data.restore.RestoreJobItem;
import com.ca.arcflash.webservice.data.restore.RestoreJobNode;
import com.ca.arcflash.webservice.jni.WSJNI;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.internal.RecoveryPointConverter;
import com.ca.arcflash.webservice.service.internal.RemoteFolderConnCache;
import com.ca.arcflash.webservice.service.rps.SettingsService;
import com.ca.arcflash.webservice.service.validator.CopyJobValidator;
import com.ca.arcflash.webservice.service.validator.RestoreJobValidator;
import com.ca.arcflash.webservice.util.ConvertErrorCodeUtil;
import com.ca.arcflash.webservice.util.ScheduleUtils;
import com.ca.arcflash.webservice.util.ServiceUtils;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public class RestoreService extends BaseService {
	private static final Logger logger = Logger.getLogger(BrowserService.class);
	private static final RestoreService instance = new RestoreService();
	private RecoveryPointConverter recoveryPointConverter = new RecoveryPointConverter();
	private RestoreJobValidator restoreJobValidator = new RestoreJobValidator();
	private CopyJobValidator copyJobValidator = new CopyJobValidator();
	
	private RestoreService(){
		
	}
	
	public static RestoreService getInstance(){
		return instance;
	}
	/**
	 * Get the recovery points in the time range with the latest recovery point as the first element in the array.
	 * @param destination
	 * @param domain
	 * @param userName
	 * @param pwd
	 * @param beginDate
	 * @param endDate
	 * @param isQueryDetail
	 * @return
	 * @throws ServiceException
	 */
	public RecoveryPoint[] getRecoveryPoints(String destination,String domain, String userName, String pwd, Date beginDate, Date endDate, boolean isQueryDetail) throws ServiceException{
		logger.debug("getRecoveryPoints - start");
		logger.debug(destination);
		logger.debug(beginDate);
		logger.debug(endDate);
		if(destination == null || destination.trim().isEmpty()){
			logger.error("No destination selected, destination is null or empty:" + destination);
			throw new ServiceException("", FlashServiceErrorCode.Common_Invalid_DestPath);
		}
		// max length is 170 + 1 (1 is for the backslash. Refer to other places uses getPathMaxLength)
		long maxLength =  BackupService.getInstance().getPathMaxLength();
		if(destination.length() > maxLength + 1){
			String strMaxLength = ""+ maxLength;
			throw new ServiceException(strMaxLength,FlashServiceErrorCode.RestoreJob_DestinationPath_Exceed_Max);
		}
		
		if (!CommonService.getInstance().isFolderAccessible(destination, domain, userName, pwd))
			throw generateAxisFault(FlashServiceErrorCode.Restore_DestinationPathNotFound);
		
		try{
			logger.debug(String.format("destination=%s, domain=%s, userName=%s, beginDate=%s, endDate=%s, isQueryDetail=%s", destination, domain, userName, beginDate.toString(), endDate.toString(), String.valueOf(isQueryDetail)));
			JRestorePoint[] restorePoints = getNativeFacade().getRestorePoints(destination, domain, userName, pwd, beginDate, endDate, isQueryDetail);
			RecoveryPoint[] result = recoveryPointConverter.convert2RecoveryPoints(restorePoints);
			if(isQueryDetail){
				checkIfCanCatalogAndMount(result);
			}
			if (logger.isDebugEnabled())
				logger.debug(StringUtil.convertArray2String(restorePoints));
			logger.debug("getRecoveryPoints - end");
			return result;
		}catch(Throwable e){
			logger.error("getRecoveryPoints()", e);
			throw generateInternalErrorAxisFault();
		}
	}
	
	/**
	 * Get the recovery points in the time range with the latest recovery point as the first element in the array.
	 * @param destination
	 * @param domain
	 * @param userName
	 * @param pwd
	 * @param beginDate
	 * @param endDate
	 * @param isQueryDetail
	 * @return
	 * @throws ServiceException
	 */
	//zxh,mark:ASBUGetRecoveryPoint
	public RecoveryPoint[] getRecoveryPoints4ASBU(String destination,String domain, String userName, String pwd, Date beginDate, Date endDate, boolean isQueryDetail) throws ServiceException{
		logger.debug("getRecoveryPoints - start");
		logger.debug(destination);
		logger.debug(beginDate);
		logger.debug(endDate);
		if(destination == null || destination.trim().isEmpty()){
			logger.error("No destination selected, destination is null or empty:" + destination);
			throw new ServiceException("", FlashServiceErrorCode.Common_Invalid_DestPath);
		}
		// max length is 170 + 1 (1 is for the backslash. Refer to other places uses getPathMaxLength)
		long maxLength =  BackupService.getInstance().getPathMaxLength();
		if(destination.length() > maxLength + 1){
			String strMaxLength = ""+ maxLength;
			throw new ServiceException(strMaxLength,FlashServiceErrorCode.RestoreJob_DestinationPath_Exceed_Max);
		}
		if (FlashSwitch.isSiwtchEnableFromReg_Cache(FlashSwitchDefine.USEGetRecoveryPointModule.SWT_USEGETRECOVERYPOINT_FLAG, 
				FlashSwitchDefine.USEGetRecoveryPointModule.SWT_USEGETRECOVERYPOINT_PATH, "1", false)) {
			logger.debug("use old JNI");
			
			if (!CommonService.getInstance().isFolderAccessible(destination, domain, userName, pwd))
				throw generateAxisFault(FlashServiceErrorCode.Restore_DestinationPathNotFound);
		}
		
		
		try{
			logger.debug(String.format("destination=%s, domain=%s, userName=%s, beginDate=%s, endDate=%s, isQueryDetail=%s", destination, domain, userName, beginDate.toString(), endDate.toString(), String.valueOf(isQueryDetail)));
			JRestorePoint[] restorePoints = getNativeFacade().getRestorePoints4ASBU(destination, domain, userName, pwd, beginDate, endDate, isQueryDetail);
			RecoveryPoint[] result = recoveryPointConverter.convert2RecoveryPoints(restorePoints);
			if(isQueryDetail){
				checkIfCanCatalogAndMount(result);
			}
			if (logger.isDebugEnabled())
				logger.debug(StringUtil.convertArray2String(restorePoints));
			logger.debug("getRecoveryPoints - end");
			return result;
		}catch(Throwable e){
			logger.error("getRecoveryPoints()", e);
			throw generateInternalErrorAxisFault();
		}
	}
	
	//wanqi06 suggestion 3
	public CatalogItem[] getCatalogItems(String destination, String userName, String passWord,
			long sessionNumber, String volumeGUID, String catlogFilePath,
			long parentID, String parentPath, String encryptedPwd, HttpSession session) throws ServiceException{
		Lock lock = null;
		CatalogItem[] items = null;
		com.ca.arcflash.webservice.jni.model.JMountPoint mp = null;
		boolean useCatalog = false;
		try {
			useCatalog = this.useCatalog(destination, userName, passWord, sessionNumber, catlogFilePath);
			if(useCatalog)
			{
				lock = RemoteFolderConnCache.getInstance().getLockByPath(catlogFilePath);
				//if lock != null, this path is a remote path
				if(lock != null) {
					lock.lock();
					RemoteFolderConnCache.reEstalishConnetion(session);
				}
				items =  BrowserService.getInstance().getCatalogItems(catlogFilePath, parentID);
			}
			// If failed to open catalog, try to mount this volume to get the items
			if(!this.isAppWriter(catlogFilePath) 
					&& !isSubitemWithCatalog(parentPath, volumeGUID)
					&& (items == null || items.length ==0))
			{
				mp =  BrowserService.getInstance().MountVolumeEx(destination, userName, passWord, volumeGUID, 
						sessionNumber, catlogFilePath, encryptedPwd);
				if(mp != null){
					items = BrowserService.getInstance().getFileItems(mp.getMountID(), volumeGUID, parentPath);
				}
			}
		}  finally {
			if(lock != null) {
				lock.unlock();
			}
			if(mp != null)
			{
				int ret;
				try {
					ret = BrowserService.getInstance().UnMountVolume(mp, false, false);
					if(ret != 0){
						logger.error("Unmount volume failed");
					}
				} catch (ServiceException e) {
					logger.error("Unmount volume failed", e);
				}
			}
		}
		return items;
	}
	
	/**
	 * check whether the folder to browse is a subfolder of recovery point with catalog generated.
	 * @param parentPath
	 * @param volumeGUID
	 * @return
	 */
	private boolean isSubitemWithCatalog(String parentPath, String volumeGUID) {
		return parentPath != null && !parentPath.contains(volumeGUID);
	}
	
	public PagedCatalogItem getPagedCatalogItemsEx(String destination, String userName, String passWord, long sessionNumber, 
			String volumeGUID, String catlogFilePath, long parentID, 
			String parentPath,int start, int size, String encryptedPwd, HttpSession session) throws ServiceException {
		Lock lock = null;
		PagedCatalogItem Items = null;
		com.ca.arcflash.webservice.jni.model.JMountPoint mp = null;
		boolean useCatalog = false;
		try {
			useCatalog = this.useCatalog(destination, userName, passWord, sessionNumber, catlogFilePath);
			if(useCatalog)
			{
				lock = RemoteFolderConnCache.getInstance().getLockByPath(catlogFilePath);
				//if lock != null, this path is a remote path
				if(lock != null) {
					lock.lock();
					RemoteFolderConnCache.reEstalishConnetion(session);
				}
				Items =  BrowserService.getInstance().getPagedCatalogItems(catlogFilePath, parentID, start, size);
				Items.setUseCatalog(true);
			}
	
			if(!this.isAppWriter(catlogFilePath) 
					&& !isSubitemWithCatalog(parentPath, volumeGUID)
					&& (Items == null || Items.getTotal() ==0))
			{
				mp =  BrowserService.getInstance().MountVolumeEx(destination, userName, passWord, 
						volumeGUID, sessionNumber, catlogFilePath, encryptedPwd);
				if(mp != null){
					Items = BrowserService.getInstance().getPagedFileItems(mp.getMountID(), catlogFilePath + volumeGUID, parentPath, start, size);
					Items.setUseCatalog(false);
				}
			}
		}  finally {
			if(lock != null) {
				lock.unlock();
			}
			if(mp != null)
			{
				int ret;
				try {
					ret = BrowserService.getInstance().UnMountVolume(mp, false, false);
					if(ret != 0){
						logger.error("Unmount volume failed");
					}
				} catch (ServiceException e) {
					logger.error("Unmount volume failed", e);
				
				}
			}
		}
		return Items;
	}
	
	public RecoveryPoint[] getAllRecoveryPoints(String destination,String domain, String userName, String pwd, boolean isQueryDetail, int backupType) throws ServiceException{
		Calendar start = Calendar.getInstance();
		start.setTimeInMillis(0);
		Calendar end = Calendar.getInstance();
		end.set(9999, Calendar.DECEMBER, 31, 23, 59);
		RecoveryPoint[] points = getRecoveryPoints(destination, domain,
				userName, pwd, start.getTime(), end.getTime(), isQueryDetail);
		if(backupType == BackupType.All)
			return points;
		else {
			List<RecoveryPoint> typePoints = new ArrayList<RecoveryPoint>();
			for(RecoveryPoint point : points) {
				if(point.getBackupType() == backupType){
					typePoints.add(point);
				}
			}
			return typePoints.toArray(new RecoveryPoint[0]);
		}
	}

	private void checkIfCanCatalogAndMount(RecoveryPoint[] rps) {
		for(int i = 0; i < rps.length ; i++)
		{
			List<RecoveryPointItem> items = rps[i].getItems();
			if(items==null||items.size()==0){
				logger.debug("After QueryDetail, items of RecoveryPoint "+rps[i].getName()+(items==null?" is null" : " size==0")+". It is a vm recovery point.");
				continue;
			}
			List<RecoveryPointItem> recoveryPointItems = new ArrayList<RecoveryPointItem>();
			int counter_refs=0, counter_dedupe=0;
			for(RecoveryPointItem item : items)
			{
				recoveryPointItems.add(item);
				if(item.getVolAttr()==RecoveryPointItem.RefsVol){
					counter_refs++;
				}else if(item.getVolAttr()==RecoveryPointItem.DedupVol){
					counter_dedupe++;
				}
			}
			if(items.size()==counter_refs){
				rps[i].setCanCatalog(false);
			}else if(rps[i].getFsCatalogStatus()==RecoveryPoint.FSCAT_DISABLED||rps[i].getFsCatalogStatus()==RecoveryPoint.FSCAT_FAIL){
				rps[i].setCanCatalog(true);
			}else{
				rps[i].setCanCatalog(false);
			}
			logger.debug("RecoveryPoint "+rps[i].getName()+" can catalog "+rps[i].isCanCatalog());
			if(!CommonService.getInstance().isWin8()&& counter_refs+counter_dedupe==items.size()){
				rps[i].setCanMount(false);
			}else{
				rps[i].setCanMount(true);
			}
			logger.debug("RecoveryPoint "+rps[i].getName()+" can mount "+rps[i].isCanMount());
		}
		
	}

	public void submitRestoreJob(RestoreJob job) throws ServiceException{
		logger.debug("submitRestoreJob - start");
		printRestoreJob(job);
		RpsPolicy4D2D rpsPolicy = null;
		try{
			restoreJobValidator.validate(job);
			rpsPolicy = SettingsService.instance().checkDataStore4Job(job.getSrcRpsHost(),
					job.getRpsDataStoreName(), JobType.JOBTYPE_RESTORE);
		}catch(ServiceException e){
			logger.debug("Restore Job Validation Failed:"+e.getErrorCode());
			ConvertErrorCodeUtil.submitRecoveryVMJobConvert(e);	
			throw e;
		}catch(Exception e){
			logger.debug(e);
		}
		
		//check whether there is running jobs
		Scheduler scheduler = BackupService.getInstance().getOtherScheduler();
		if (scheduler==null)
			return;
			
		if (isCheckJob()){
			throw generateAxisFault(FlashServiceErrorCode.Common_OtherJobIsRunning);
		}
		
		try {
			JobDetailImpl jobDetail = new JobDetailImpl(JOB_NAME_RESTORE+System.nanoTime(),null,com.ca.arcflash.webservice.scheduler.RestoreJob.class);
			jobDetail.getJobDataMap().put("Job", job);
			jobDetail.getJobDataMap().put("NativeFacade", this.getNativeFacade());
			if(rpsPolicy != null){
				rpsPolicy.setPolicyUUID(job.getRpsPolicy());
				jobDetail.getJobDataMap().put(RPS_POLICY_UUID, rpsPolicy.getPolicyUUID());
				jobDetail.getJobDataMap().put(RPS_DATASTORE_UUID, rpsPolicy.getDataStoreName());
				jobDetail.getJobDataMap().put(RPS_HOST, job.getSrcRpsHost());				
			}
			jobDetail.getJobDataMap().put(RPS_DATASTORE_DISPLAY_NAME, job.getRpsDataStoreDisplayName());
			SimpleTriggerImpl trigger = ScheduleUtils.makeImmediateTrigger(0,0);
			trigger.setName(jobDetail.getName()+"Trigger");
			scheduler.scheduleJob(jobDetail, trigger);
			
			checkForMergeRunning(ServiceUtils.jobType2String(Constants.AF_JOBTYPE_RESTORE, 0));
			
			logger.debug("submitRestoreJob - end");
		} catch (ServiceException se) {
			throw se;
		} catch (Throwable e) {
			logger.error("submitRestoreJob()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	private void printRestoreJob(RestoreJob job) {
		if (logger.isDebugEnabled()){
			logger.debug(StringUtil.convertObject2String(job));
			
			if (job!=null){
				logger.debug(StringUtil.convertArray2String(job.getNodes()));
				logger.debug(StringUtil.convertObject2String(job.getFileSystemOption()));
				if (job.getNodes()!=null){
					for(RestoreJobNode node:job.getNodes()){
						if (node == null)
							continue;
						
						logger.debug(StringUtil.convertArray2String(node.getJobItems()));
						if (node!=null && node.getJobItems()!=null){
							for(RestoreJobItem item : node.getJobItems()){
								if (item == null)
									continue;
								logger.debug(StringUtil.convertArray2String(item.getEntries()));
							}
						}
					}
				}
			}
		}
	}

	public void submitCopyJob(CopyJob job) throws ServiceException {
		logger.debug("submitCopyJob - start");
		printCopyJob(job);
		int pathMaxWithoutHostName =0;
		RpsPolicy4D2D rpsPolicy = null;
		try{
			//The following two lines's position cannot be exchanged because the validateDestPath will establish
			//a connection on which appendHostNameIfNeeded depends.   
			copyJobValidator.validate(job);
			pathMaxWithoutHostName = copyJobValidator.validateDestPath(job);
			rpsPolicy = SettingsService.instance().checkDataStore4Job(job.getRpsHost(),
					job.getRpsDataStore(), JobType.JOBTYPE_COPY);
		} catch (ServiceException e) {
			logger.debug("submitCopyJob Validation Failed:" + e.getErrorCode());
			throw e;
		}
		
//		if(job.getEncryptTypeCopySession() != CommonService.ENCRYPTION_LIC) {
//			if(!CommonService.getInstance().checkLicense(0)) {
//				String msg = WebServiceMessages.getResource("LicenseEncryption", WebServiceMessages.getResource("CopyJob"));
//				throw new ServiceException(msg, FlashServiceErrorCode.Common_License_Failure);
//			}
//		}
		
		//check whether there is running jobs
		Scheduler scheduler = BackupService.getInstance().getOtherScheduler();
		if (scheduler==null)
			return;
			
		if (com.ca.arcflash.webservice.scheduler.CopyJob.isJobRunning()
				|| this.getNativeFacade().checkJobExist()){
			throw generateAxisFault(FlashServiceErrorCode.Common_OtherJobIsRunning);
		}
		
		checkSameSourceDestPath(job);
			
		String sourceServer = getLastFolderName(job.getSessionPath());
		logger.debug("Server name(folder name) :" + sourceServer);
		if(sourceServer == null || sourceServer.length() == 0)
			throw new ServiceException(FlashServiceErrorCode.CopyJob_InvalidSessionPath);
		String originalDest = job.getDestinationPath();
		String username = job.getDestinationUserName();
		String password = job.getDestinationPassword();
		
		if (username == null)
			username = "";
		if (password == null)
			password = "";
		
		String dest = BackupService.getInstance().appendHostNameIfNeeded(job.getDestinationPath(), sourceServer, username, password, 1);
		
		if(!StringUtil.isEmptyOrNull(dest) &&  !StringUtil.isEmptyOrNull(originalDest)
				&& dest.length() > originalDest.length()) {
				//int maxLength = pathMaxWithoutHostName - BackupConfigurationValidator.WINDOWS_HOST_NAME_MAX_LENGTH;
				int backslash = 1;
				if(originalDest.endsWith("\\") || originalDest.endsWith("/")){
					backslash = 0;
				}
				if(dest.length() > (pathMaxWithoutHostName + backslash))
					copyJobValidator.generatePathExeedLimitException(pathMaxWithoutHostName);
			}
		job.setDestinationPath(dest);
		
		checkSameSourceDestPath(job);
		
		logger.debug("username" + username);
		String domain = "";
		int indx = username.indexOf('\\');
		if (indx > 0 && indx < username.length() - 1) {
			domain = username.substring(0, indx);
			username = username.substring(indx + 1);
		}
		
		getNativeFacade().initCopyDestination(dest, domain, username, password);
		
		try {
			JobDetailImpl jobDetail = new JobDetailImpl(JOB_NAME_COPYJOB,null,com.ca.arcflash.webservice.scheduler.CopyJob.class);
			jobDetail.getJobDataMap().put("Job", job);
			jobDetail.getJobDataMap().put("NativeFacade", this.getNativeFacade());
			if(rpsPolicy != null){
				String hashKey = BackupService.getInstance().getNativeFacade().getRPSDataStoreHashKey(
						rpsPolicy.getDataStoreSharedPath(), rpsPolicy.getStoreUserName(), 
						rpsPolicy.getStorePassword(), 0, rpsPolicy.getEncryptionPassword());
				
				job.setDataStoreHashKey(hashKey);
				jobDetail.getJobDataMap().put(RPS_POLICY_UUID, rpsPolicy.getPolicyUUID());
				jobDetail.getJobDataMap().put(RPS_DATASTORE_UUID, rpsPolicy.getDataStoreName());
				jobDetail.getJobDataMap().put(RPS_HOST, job.getRpsHost());
			}
			jobDetail.getJobDataMap().put(ON_DEMAND_JOB, true);
			jobDetail.getJobDataMap().put(RPS_DATASTORE_DISPLAY_NAME, job.getRpsDataStoreDisplayName());
			SimpleTriggerImpl trigger = ScheduleUtils.makeImmediateTrigger(0,0);
			trigger.setName(jobDetail.getName()+"Trigger");
			scheduler.scheduleJob(jobDetail, trigger);
			
			checkForMergeRunning(ServiceUtils.jobType2String(Constants.AF_JOBTYPE_COPY, 0));
			
			logger.debug("submitCopyJob - end");
		} catch (ServiceException se) {
			throw se;
		} catch (Throwable e) {
			logger.error("submitCopyJob()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	private String getLastFolderName(String sourcePath) {
		if(sourcePath == null || sourcePath.isEmpty())
			return null;
		if(sourcePath.endsWith("\\") || sourcePath.endsWith("/"))
			sourcePath = sourcePath.substring(0, sourcePath.length() - 1);
		
		int indexBSlash = sourcePath.lastIndexOf("\\");
		int indexSlash = indexBSlash < 0 || sourcePath.lastIndexOf("/") > indexBSlash ? sourcePath.lastIndexOf("/")  : indexBSlash;		
		
		
		if (indexSlash >= 0) {
			String retPath = sourcePath.substring(indexSlash + 1);

			if (retPath.endsWith("]")) {// remove the SID for copy recovery point job.
				int indx = retPath.lastIndexOf("[");
				if (indx > 0) {
					return retPath.substring(0, indx);
				}
			}else{
				return retPath;
			}
		}
		return null;
	}

	private String checkSameSourceDestPath(CopyJob job) throws ServiceException {
		String destinationPath = getNormalizedPath(job.getDestinationPath());
		String sourcePath = getNormalizedPath(job.getSessionPath());
		if(destinationPath.equalsIgnoreCase(sourcePath))
			throw new ServiceException(FlashServiceErrorCode.CopyJob_SameSourceDestPath);
		return destinationPath;
	}

	private String getNormalizedPath(String destinationPath) {
		String path = destinationPath == null ? "" : destinationPath;
		if(path.endsWith("\\") || path.endsWith("/"))
			path = path.substring(0, path.length() - 1);
		return path;
	}

	private void printCopyJob(CopyJob job) {
		if (logger.isDebugEnabled()){
			logger.debug(StringUtil.convertObject2String(job));
		}
	}
	public AlternativePath[] checkSQLAlternateLocation(String[] basePath, String[] instName,
			String[] dbName) throws ServiceException {
		logger.debug("checkSQLAlternateLocation - start");
		logger.debug("basePath:" + StringUtil.convertArray2String(basePath));
		logger.debug("instName:" + StringUtil.convertArray2String(instName));
		logger.debug("dbName:" + StringUtil.convertArray2String(dbName));
		
		if(basePath.length != instName.length || instName.length != dbName.length)
			throw new IllegalArgumentException("The lengths of the arrays are not equal.");
		
		AlternativePath[] ret = new AlternativePath[basePath.length];
		for (int i = 0; i < basePath.length; i++) {
			ret[i] = getNativeFacade().checkSQLAlternateLocation(basePath[i], instName[i], dbName[i]);
		}
		logger.debug(StringUtil.convertArray2String(ret));
		
		logger.debug("checkSQLAlternateLocation - end");
		return ret;
	}
	
	public void checkContainRecoveryPoints(JNetConnInfo connInfo) throws ServiceException {
		logger.debug("checkContainRecoveryPoints(JNetConnInfo) - start");
		logger.debug("path:" + connInfo.getSzDir());
		logger.debug("domain:" + connInfo.getSzDomain());
		logger.debug("userName:" + connInfo.getSzUsr());
		
		getNativeFacade().checkContainRecoveryPoints(connInfo);
		
		logger.debug("checkSQLAlternateLocation - end");
	}
	
	public RecoveryPointItem[] getRecoveryPointItems(String dest,String domain, String user, String pwd, String subPath) throws ServiceException{
		logger.debug("getRecoveryPointItems(String, String, String, String, String) - start");
		logger.debug("dest:" + dest);
		logger.debug("domain:" + domain);
		logger.debug("user:" + user);
		logger.debug("pwd:");
		logger.debug("subPath:" + subPath);
		
		if (!CommonService.getInstance().isFolderAccessible(dest, domain, user, pwd))
			throw generateAxisFault(FlashServiceErrorCode.Restore_DestinationPathNotFound);
		
		if(subPath == null || subPath.trim().isEmpty()){
			throw generateAxisFault(FlashServiceErrorCode.RestoreJob_InvalidSessionPath);
		}
		
		try{
			List<JBackupItem> bkpItems = getNativeFacade().GetBackupItem(dest, domain, user, pwd, subPath);			
			List<RecoveryPointItem> recPointItems = new ArrayList<RecoveryPointItem>();
			if (bkpItems!=null && bkpItems.size()>0){
				for(JBackupItem item : bkpItems){
					try{
						recPointItems.add(recoveryPointConverter.convertBackupItem2RecoveryPointItem(item));
					}catch(Exception e){
						logger.error("Error during convert backup item", e);
					}
				}
			}
			
			RecoveryPointItem[] result = recPointItems.toArray(new RecoveryPointItem[0]);
			if (logger.isDebugEnabled())
				logger.debug(StringUtil.convertArray2String(result));
			logger.debug("getRecoveryPointItems - end");
			return result;
		}catch(SOAPFaultException se) {
			throw se;
		}catch(Throwable e){
			logger.error("getRecoveryPoints()", e);
			throw generateInternalErrorAxisFault();
		}
	}
	
	public long getRecoveryPointItemsChildCount(long sessNum, String catalogPath, 
			String volumeGUID, String destination, String userName, String passWord, String encryptedPwd) throws ServiceException{
		try{
			long childCount = getNativeFacade().GetChildCount( sessNum, catalogPath, 
					volumeGUID, destination, userName, passWord, encryptedPwd);			
			return childCount;
		}catch(ServiceException se) {
			throw se;
		}catch(Throwable e){
			logger.error("getRecoveryPoints()", e);
			throw generateInternalErrorAxisFault();
		}
	}
	
	private boolean isAppWriter(String catalogFilePath) {
		return catalogFilePath != null && catalogFilePath.endsWith(".CAT") && catalogFilePath.contains("Writer");			
	}
	
	public boolean useCatalog(String backupDest, String userName,
				String password, long sessionNumber, String catlogFilePath) {
		if(isAppWriter(catlogFilePath))
			return true;
		else
			return this.getNativeFacade().isCatalogExist(backupDest, userName, password, (int)sessionNumber);
	}
	
	public boolean isCheckJob(){
		if (!com.ca.arcflash.webservice.scheduler.RestoreJob.isAllowMultiple() && (com.ca.arcflash.webservice.scheduler.RestoreJob.isJobRunning() 
				|| getNativeFacade().checkJobExist())){
			return true;
		}
		return false;		
	}
	
	public long restoreNow(RestoreJobArg jobArg) throws ServiceException {
		if (handleErrorFromRPS(jobArg) == -1)
			return -1;
		
		logger.debug("Restore now from rps job queue");
		if(isCheckJob()){
			throw generateAxisFault(FlashServiceErrorCode.Common_OtherJobIsRunning);
		}
		
		try {
			JobDetailImpl jobDetail = new JobDetailImpl(jobArg.getJobDetailName(),Constants.RUN_NOW,com.ca.arcflash.webservice.scheduler.RestoreJob.class);
			jobDetail.getJobDataMap().put("Job", jobArg.getJobScript());
			jobDetail.getJobDataMap().put("NativeFacade", this.getNativeFacade());
			jobDetail.getJobDataMap().put(RPS_POLICY_UUID, jobArg.getPolicyUUID());
			jobDetail.getJobDataMap().put(RPS_DATASTORE_UUID, jobArg.getDataStoreUUID());
			jobDetail.getJobDataMap().put(RPS_DATASTORE_DISPLAY_NAME, jobArg.getDataStoreName());
			jobDetail.getJobDataMap().put(RPS_HOST, jobArg.getSrcRps());
			jobDetail.getJobDataMap().put(Constants.RUN_NOW, Boolean.TRUE);
			jobDetail.getJobDataMap().put(JOB_ID, jobArg.getJobId());
			
			SimpleTriggerImpl trigger = ScheduleUtils.makeImmediateTrigger(0,0);
			trigger.setName(jobDetail.getName()+"Trigger");
			BackupService.getInstance().getOtherScheduler().scheduleJob(jobDetail, trigger);
			
			logger.debug("RestoreNow - end");
			return 0;
		} catch (Throwable e) {
			logger.error("submitRestoreJob()", e);
			throw generateInternalErrorAxisFault();
		}	
	}
	
	public List<String> getE15CASList(String userName, String password){
		return this.getNativeFacade().getE15CASList(userName, password);
	}
	
	public String getDefaultE15CAS(String userName, String password) {
		String cas = this.getNativeFacade().getDefaultE15CAS();
		if(StringUtil.isEmptyOrNull(cas)){
			List<String> list = getE15CASList(userName, password);
			if(list!=null && list.size()>0){
				cas = list.get(0);
			}
		}
		return cas;
	}
	
	public void  setDefaultE15CAS(String cas) {
		if(CommonService.getInstance().isExchange2013()&&cas!=null){
			logger.debug("set default E15 CAS: "+cas);
			this.getNativeFacade().setDefaultE15CAS(cas);
		}
	}
}