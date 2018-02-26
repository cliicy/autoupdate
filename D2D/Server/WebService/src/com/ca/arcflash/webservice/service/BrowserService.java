package com.ca.arcflash.webservice.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;

import javax.print.attribute.standard.QueuedJobCount;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.JobDetailImpl;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.SimpleTriggerImpl;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.ca.arcflash.common.Base64;
import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.CommonRegistryKey;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.common.WindowsRegistry;
import com.ca.arcflash.ha.model.internal.HAException;
import com.ca.arcflash.ha.modelWebService.MonitorWebServiceErrorCode;
import com.ca.arcflash.service.jni.model.JNetConnInfo;
import com.ca.arcflash.service.jni.model.JSystemInfo;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.data.MountNode;
import com.ca.arcflash.webservice.data.archive.ArchiveDestinationConfig;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.browse.FileFolderItem;
import com.ca.arcflash.webservice.data.browse.Folder;
import com.ca.arcflash.webservice.data.browse.Volume;
import com.ca.arcflash.webservice.data.catalog.ArchiveCatalogItem;
import com.ca.arcflash.webservice.data.catalog.ArchiveFileVerionDetail;
import com.ca.arcflash.webservice.data.catalog.CatalogItem;
import com.ca.arcflash.webservice.data.catalog.GRTBrowsingContext;
import com.ca.arcflash.webservice.data.catalog.GRTCatalogItem;
import com.ca.arcflash.webservice.data.catalog.MsgSearchRec;
import com.ca.arcflash.webservice.data.catalog.PagedCatalogItem;
import com.ca.arcflash.webservice.data.catalog.PagedExchangeDiscoveryItem;
import com.ca.arcflash.webservice.data.catalog.PagedGRTCatalogItem;
import com.ca.arcflash.webservice.data.catalog.SearchContext;
import com.ca.arcflash.webservice.data.catalog.SearchResult;
import com.ca.arcflash.webservice.data.catalog.SearchSessionItem;
import com.ca.arcflash.webservice.data.restore.CatalogInfo;
import com.ca.arcflash.webservice.data.restore.ExchangeDiscoveryItem;
import com.ca.arcflash.webservice.data.restore.RecoveryPoint;
import com.ca.arcflash.webservice.data.restore.RecoveryPointItem;
import com.ca.arcflash.webservice.data.vss.Application;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.model.JArchiveCatalogDetail;
import com.ca.arcflash.webservice.jni.model.JArchiveFileVersionDetail;
import com.ca.arcflash.webservice.jni.model.JCatalogDetail;
import com.ca.arcflash.webservice.jni.model.JCatalogInfo;
import com.ca.arcflash.webservice.jni.model.JExchangeDiscoveryItem;
import com.ca.arcflash.webservice.jni.model.JMountPoint;
import com.ca.arcflash.webservice.jni.model.JMsgRec;
import com.ca.arcflash.webservice.jni.model.JMsgSearchRec;
import com.ca.arcflash.webservice.jni.model.JPagedCatalogItem;
import com.ca.arcflash.webservice.jni.model.JRWLong;
import com.ca.arcflash.webservice.jni.model.JSearchResult;
import com.ca.arcflash.webservice.scheduler.DiskMonitorJob;
import com.ca.arcflash.webservice.scheduler.MountMonitorJob;
import com.ca.arcflash.webservice.service.internal.RemoteFolderConnCache;
import com.ca.arcflash.webservice.util.ScheduleUtils;
import com.ca.arcflash.webservice.util.ServiceUtils;
import com.ca.arcflash.webservice.util.VSSXMLParser;
import com.ca.arcflash.webservice.util.VolumeXMLParser;
import com.ca.arcflash.webservice.util.WebServiceMessages;

public final class BrowserService extends BaseService {
	public static final String JOB_GROUP_MOUNT_MGR =  "MountMgrJobGroup";
	public static final String JOB_NAME_UNMOUNT_MNTPOINT =   "UnMountJob";
	private static final Logger logger = Logger.getLogger(BrowserService.class);
	private static final BrowserService instance = new BrowserService();
	private static final VSSXMLParser vssParser = new VSSXMLParser();
	private static final VolumeXMLParser volumeParser = new VolumeXMLParser();
	public static final int VOL_GUID_START_OFFSET = 48;
	private static final int SESSION_START_OFFSET = 69;
	private static final int SESSION_END_OFFSET = 59;
	private static final int DESTINATION_END_OFFSET=79;
	public static final String REGISTRY_INSTALLPATH			=	CommonRegistryKey.getD2DRegistryRoot()+"\\InstallPath";
	public static final String REGISTRY_KEY_PATH			=	"Path";
	private static final String mountConfigFile = CommonUtil.D2DInstallPath+"Configuration\\mount_configuration.xml";
	public volatile static boolean configLock = false; 
	private Scheduler scheduler;
	private static final int FSCAT_FINISH = 0x01;
	private static final int FSCAT_FAIL = 0x00;
	private static final int FSCAT_PENDING = 0x02;
	private static final int FSCAT_DISABLED = 0x03;
	public static final int BROWSE_LENGTH = 255; 
	public static final int ARCHIVE_DEST_MODE = 9;
	public static final int BACKUP_MODE = 0;

	private BrowserService(){
		try {
			scheduler = new StdSchedulerFactory().getScheduler();
			scheduler.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	public static BrowserService getInstance(){
		return instance;
	}
	
	private static synchronized boolean getConfigFileLock(){
		if(configLock)
			return false;

		configLock = true;
		return true;
	}

	public static void markConfigFileLock() {
		while(getConfigFileLock() == false) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static synchronized void releaseConfigFileLock(){
		configLock = false;
	}
	
	public void startMountManager(){
		
		SimpleTriggerImpl mountTrigger = ScheduleUtils.makeSecondlyTrigger(ServiceContext.getInstance().getSessionDismountTime());
		mountTrigger.setName("mountTrigger");
		JobDetail cycleJobDetail = new JobDetailImpl(JOB_NAME_UNMOUNT_MNTPOINT,JOB_GROUP_MOUNT_MGR,MountMonitorJob.class);
		try {
			
			scheduler.scheduleJob(cycleJobDetail, mountTrigger);
			
		} catch (SchedulerException e) {
			logger.error("startMountManager failed");
			logger.error(e.getMessage());
		}
	}

	public Application[] getVSSApplications() throws ServiceException{
		logger.debug("getVSSApplications() - start");

		try {
			String xmlString = getNativeFacade().browseVSSApplications();
			if (logger.isDebugEnabled())
				logger.debug(xmlString);

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new InputSource(new StringReader(xmlString)));

			List<Application> applications = vssParser.parseXML(doc);
			Application[] returnApplicationArray = applications.toArray(new Application[0]);


			if (logger.isDebugEnabled())
				logger.debug(StringUtil.convertArray2String(returnApplicationArray));
			logger.debug("getVSSApplications() - end");
			return returnApplicationArray;
		} catch(Throwable e){
			logger.error("getVSSApplications()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	/**
	 * Fetch the volumes on the D2D server.
	 * @param details if false, only volume name and GUID are valid in the returned Volume objects.
	 * @param backupDest if <code>details</code> is true, this value is used to fetch some volume detail information.
	 * @return
	 * @throws ServiceException
	 */
	public Volume[] getVolumes(boolean details, String backupDest, String userName, String passwd) throws ServiceException {
		logger.debug("getVolumes(boolean, String, String, String) - start");
		logger.debug("details:" + details);
		logger.debug("backupDest:" + backupDest);
		logger.debug("userName:" + userName);

		try
		{
			String xmlString = getNativeFacade().getVolumes(details, backupDest, userName, passwd);
			if (logger.isDebugEnabled())
				logger.debug(xmlString);

			if (StringUtil.isEmptyOrNull(xmlString)){
				logger.debug("XML string is empty, return empty");
				return new Volume[0];
			}

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new InputSource(new StringReader(xmlString)));

			Volume[] returnVolumeArray = volumeParser.parseXML(doc);

			if (returnVolumeArray!=null){
				Arrays.sort(returnVolumeArray, new Comparator<Volume>(){

					@Override
					public int compare(Volume arg0, Volume arg1) {
						return arg0.getName().compareTo(arg1.getName());
					}

				});
			}

			// for Issue: 19409518    Title: GRT EXCH:MNTD VOL ON BIN\TEMP
			// filter out a volume if it is mounted
			List<Volume> tempArrayList = new LinkedList<Volume>();

			for (int i = 0 ; i<returnVolumeArray.length; i++)
			{
				boolean bIsVolumeMounted = getNativeFacade().isVolumeMounted(returnVolumeArray[i].getGuid());

				// filter the mounted disk
				if (bIsVolumeMounted)
				{
					continue;
				}

				tempArrayList.add(returnVolumeArray[i]);
			}

			returnVolumeArray = tempArrayList.toArray(new Volume[0]);

			if (logger.isDebugEnabled())
				logger.debug(StringUtil.convertArray2String(returnVolumeArray));
			logger.debug("getVolumes() - end");
			return returnVolumeArray;
		} catch(Throwable e){
			logger.error("getVolumes()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public void createFolder(String parentPath, String subDir) throws ServiceException
	{
		logger.debug("createFolder(String, String) - start");
		logger.debug("parentPath:" + parentPath);
		logger.debug("subDir:" + subDir);

		if (StringUtil.isEmptyOrNull(parentPath))
			throw new IllegalArgumentException("Parent pathcan not be null.");
		if(StringUtil.isEmptyOrNull(subDir))
			throw new IllegalArgumentException("subDir can not be null.");

		try{
			getNativeFacade().createDir(parentPath, subDir);
		}
		catch (SecurityException e)
		{
			logger.debug("createFolder - SecurityException");
			throw generateAxisFault(FlashServiceErrorCode.Browser_PathNotFound);
		}
	}

	public FileFolderItem getFileFolder(String path) throws ServiceException {
		logger.debug("getFileFolder(String) - start");
		logger.debug(path);

		//check input
		if (StringUtil.isEmptyOrNull(path))
			throw generateAxisFault(FlashServiceErrorCode.Browser_PathNotFound);
		File pathFile = new File(path);
		if (!pathFile.exists())
			throw generateAxisFault(FlashServiceErrorCode.Browser_PathNotFound);
		if (!pathFile.isDirectory())
			throw generateAxisFault(FlashServiceErrorCode.Browser_PathNotFound);

		try{
			List<Folder> folderList = new LinkedList<Folder>();
			List<com.ca.arcflash.webservice.data.browse.File> fileList = new LinkedList<com.ca.arcflash.webservice.data.browse.File>();
			File rootFile = new File(path);
			File[] files = rootFile.listFiles();
			for(File file : files){
				if (file.isDirectory()){
					Folder folder = new Folder();
					folder.setName(file.getName());
					folder.setLastUpdateDate(new Date(file.lastModified()));
					folder.setPath(file.getAbsolutePath());
					folderList.add(folder);
				}else{
					com.ca.arcflash.webservice.data.browse.File fileItem = new com.ca.arcflash.webservice.data.browse.File();
					fileItem.setPath(file.getAbsolutePath());
					fileItem.setSize(file.length());
					fileItem.setLastUpdateDate(new Date(file.lastModified()));
					fileItem.setName(file.getName());
					fileList.add(fileItem);
				}
			}

			FileFolderItem result = new FileFolderItem();
			result.setFiles(fileList.toArray(new com.ca.arcflash.webservice.data.browse.File[0]));
			result.setFolders(folderList.toArray(new Folder[0]));

			if (logger.isDebugEnabled()){
				logger.debug(StringUtil.convertArray2String(result.getFiles()));
				logger.debug(StringUtil.convertArray2String(result.getFolders()));
			}
			logger.debug("getFileFolder(String) - end");
			return result;
		} catch (Throwable e) {
			logger.error("getFileFolder()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public FileFolderItem getFileFolder(String path, String userName, String pwd)
			throws ServiceException {
		logger.debug("getFileFolder(String) - start");
		logger.debug(path);

		// check input
		if (StringUtil.isEmptyOrNull(path))
			throw generateAxisFault(FlashServiceErrorCode.Browser_PathNotFound);
		
		if(path.length() > BROWSE_LENGTH)
			throw generateAxisFault(BROWSE_LENGTH+"",FlashServiceErrorCode.Browser_Source_Path_Exceeds_Max);

		try {
			FileFolderItem result = getNativeFacade().getFileFolderItem(path, userName, pwd);

			if (logger.isDebugEnabled()) {
				logger.debug(StringUtil.convertArray2String(result.getFiles()));
				logger.debug(StringUtil
						.convertArray2String(result.getFolders()));
			}
			logger.debug("getFileFolder(String) - end");
			return result;
		} catch (ServiceException ex) {
			throw ex;
		} catch (Throwable e) {
			logger.error("getFileFolder()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public CatalogItem[] getCatalogItems(String catalogFilePath, long parentID)  throws ServiceException{
		logger.debug("getCatalogItems(String, long) - start");
		logger.debug(catalogFilePath);
		logger.debug(parentID);

		if (parentID<0)
			parentID = -1;

		try{
			List<CatalogItem> resultList = new LinkedList<CatalogItem>();

			List<JCatalogDetail> details = getNativeFacade().getCatalogItems(catalogFilePath, parentID);
			for(JCatalogDetail detail : details){
				resultList.add(convert2CatalogItem(detail, true));
			}

			if (logger.isDebugEnabled()){
				logger.debug(StringUtil.convertList2String(resultList));
			}
			logger.debug("getCatalogItems(String, long) - end");
			return resultList.toArray(new CatalogItem[0]);
		}catch(Throwable e){
			logger.error("getTopLevelCatalogItems()", e);
			throw generateInternalErrorAxisFault();
		}
	}
	
	public CatalogItem[] getFileItems(String mountVolGUID, String volumeGUID, String parentPath)  throws ServiceException{
		logger.debug("getCatalogItems(String, long) - start");
		//logger.debug(catalogFilePath);
		//logger.debug(parentID);
/*		String backupDest = null;
		String sessionNumber = null;
		long session = 0;*/
//		String volumeGUID = null;

		try{
			List<CatalogItem> resultList = new LinkedList<CatalogItem>();
			/*if(catPath != null)
			{
				volumeGUID = catPath.substring(catPath.length()-VOL_GUID_START_OFFSET, catPath.length());
			}*/
			List<JCatalogDetail> details = getNativeFacade().getFileItems(mountVolGUID, volumeGUID, parentPath);
			for(JCatalogDetail detail : details){
				detail.setPath(detail.getLongName().substring(0, detail.getLongName().lastIndexOf("\\")));
				detail.setLongName(detail.getDisplayName());
				resultList.add(convert2CatalogItem(detail, false));
			}

			if (logger.isDebugEnabled()){
				logger.debug(StringUtil.convertList2String(resultList));
			}
			logger.debug("getCatalogItems(String, long) - end");
			return resultList.toArray(new CatalogItem[0]);
		}catch(Throwable e){
			logger.error("getTopLevelCatalogItems()", e);
			throw generateInternalErrorAxisFault();
		}
	}
	
	@Deprecated
	/**
	 * This API is only for webservice call which is deprecated.
	 * @param userName
	 * @param passWord
	 * @param catPath
	 * @param encryptionKey
	 * @return
	 * @throws ServiceException
	 */
	public JMountPoint MountVolume(String userName, String passWord, String catPath, String encryptionKey)  throws ServiceException{
		logger.debug("getCatalogItems(String, long) - start");
		//logger.debug(catalogFilePath);
		//logger.debug(parentID);
		String backupDest = null;
		String sessionNumber = null;
		long session = 0;
		String volumeGUID = null;

		try{
			if(catPath != null)
			{
				volumeGUID = catPath.substring(catPath.length()-VOL_GUID_START_OFFSET, catPath.length());
				sessionNumber = catPath.substring(catPath.length()-SESSION_START_OFFSET, catPath.length()-SESSION_END_OFFSET);
				session = Long.valueOf(sessionNumber);
				backupDest = catPath.substring(0, catPath.length()-DESTINATION_END_OFFSET);
			}
			
			//zxh,Mount the volume whatever it have mounted or not.because whether it really need to mount or not was checked in c++.
			JMountPoint mountPoint = getNativeFacade().MountVolume(userName, passWord, backupDest, session, volumeGUID, encryptionKey);
			if(mountPoint != null)
			{
				getNativeFacade().VDUpateVolumeMountTimestamp(mountPoint.getDiskSignature());
				logger.info("Volume with volumeguid " + volumeGUID + " has been mounted as " + mountPoint.getMountID());
				return mountPoint;
			}
			else
			{
				logger.error("MountVolume return value is null");
				return null;
			}
			
		}catch(Throwable e){
			logger.error("getTopLevelCatalogItems()", e);
			throw generateInternalErrorAxisFault();
		}
	}
	
	/**
	 * This mount is only for restore by browse.
	 * @param userName
	 * @param passWord
	 * @param volumeGUID
	 * @param sessionNumber
	 * @param catPath
	 * @param encryptionKey
	 * @return
	 * @throws ServiceException
	 */
	public synchronized JMountPoint MountVolumeEx(String backupDest, String userName, String passWord, String volumeGUID, 
			long sessionNumber, String catPath, String encryptionKey)  throws ServiceException{
		logger.debug("getCatalogItems(String, long) - start");
		
		//logger.debug(catalogFilePath);
		//logger.debug(parentID);
//		String backupDest = null;
		long session = 0;
		 String temp = null;
		 if(catPath.endsWith("$DISABLED$"))
			 temp = catPath.substring(0, catPath.lastIndexOf("$DISABLED$"));
		 else
			 temp = catPath;
		 
		
		try{
			if(catPath != null)
			{
				session = sessionNumber;				
				if(StringUtil.isEmptyOrNull(backupDest) && temp != null){
					logger.warn("Passed in backup destination is null, get it from catalog file path, it maybe wrong");
					backupDest =  temp.substring(0, temp.lastIndexOf("\\Catalog\\"));
				}
			}
			
			//zxh,Mount the volume whatever it have mounted or not.because whether it really need to mount or not was checked in c++.
			JMountPoint mountPoint = getNativeFacade().MountVolume(userName, passWord, backupDest, session, volumeGUID, encryptionKey);
			if(mountPoint != null)
			{
				getNativeFacade().VDUpateVolumeMountTimestamp(mountPoint.getDiskSignature());
				logger.info("Volume with volumeguid " + volumeGUID + " has been mounted as " + mountPoint.getMountID());
				return mountPoint;
			}
			else
			{
				logger.error("MountVolume return value is null");
				return null;
			}

		}catch(ServiceException se) {
			if(se.getMessage() != null && !se.getMessage().isEmpty()){
				String message = WebServiceMessages.getResource("restoreBrowseFailed", se.getMessage());
				throw new ServiceException(message, FlashServiceErrorCode.Common_General_Message);
			}else
				throw se;
		}catch(Throwable e){
			logger.error("getTopLevelCatalogItems()", e);
			throw generateInternalErrorAxisFault();
		}
	}
	/**
	 * This API is for restore by search.
	 * @param userName
	 * @param passWord
	 * @param volumeGUID
	 * @param sessionNumber
	 * @param backupDest
	 * @param encryptionKey
	 * @return
	 * @throws ServiceException
	 */
	public synchronized JMountPoint MountVolumeExForSearch(String userName, String passWord, 
			String volumeGUID, long sessionNumber, String backupDest, 
			String encryptionKey)  throws ServiceException{
		logger.debug("MountVolumeExForSearch(String, long) - start");
		logger.debug("userName: " + userName);
		logger.debug("sessionNumber: " + sessionNumber);
		logger.debug("backupDest: " + backupDest);
		logger.debug("encryptedPwd: " + encryptionKey);
		//logger.debug(catalogFilePath);
		//logger.debug(parentID);
		try{
			
			//zxh,Mount the volume whatever it have mounted or not.because whether it really need to mount or not was checked in c++.
			JMountPoint mountPoint = getNativeFacade().MountVolume(userName, passWord, backupDest, sessionNumber, volumeGUID, encryptionKey);
			if(mountPoint != null)
			{
				getNativeFacade().VDUpateVolumeMountTimestamp(mountPoint.getDiskSignature());
				logger.error("Volume with volumeguid " + volumeGUID + " has been mounted as " + mountPoint.getMountID());
				return mountPoint;
			}
			else
				return null;

		}catch(ServiceException se) {
			throw se;
		}catch(Throwable e){
			logger.error("getTopLevelCatalogItems()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public int UnMountVolume(JMountPoint mntPoint, boolean forceMount, boolean forSearch)  throws ServiceException{
		//zxh,though this function name is UnMountVolume,but it only update mount cache file. 
		//now it can do nothing, because the mount cache is implemented in c++.and from now on java will not have mount cache.
		return 0;
	}
	
	public SearchContext openSearchCatalog(String dest, String domain,
			String userName, String password,
			String sessionPath, String searchDir, boolean caseSensitive,	
			boolean includeSubDir, String pattern) throws ServiceException {
		logger.debug("openSearchCatalog() - start");
		if (logger.isDebugEnabled()){
			logger.debug("session path:"+sessionPath);
			logger.debug("searchDir:"+searchDir);
			logger.debug("caseSensitive:"+caseSensitive);
			logger.debug("includeSubDir:"+includeSubDir);
			logger.debug("pattern:"+pattern);
		}

		SearchContext context = null;
		Lock lock = RemoteFolderConnCache.getInstance().getLockByPath(dest);
		try{
			if (StringUtil.isEmptyOrNull(sessionPath))
				this.generateAxisFault(FlashServiceErrorCode.Common_NullParameter);
			
			// max length is 170 + 1 (1 is for the backslash. Refer to other places uses getPathMaxLength)
			long maxLength =  BackupService.getInstance().getPathMaxLength();
			if(sessionPath.length() > maxLength + 1){
				String strMaxLength = ""+ maxLength;
				throw new ServiceException(strMaxLength,FlashServiceErrorCode.RestoreJob_DestinationPath_Exceed_Max);
			}
			
			JNetConnInfo connInfo = new JNetConnInfo();
			connInfo.setSzDir(dest);
			connInfo.setSzDomain(domain);
			connInfo.setSzUsr(userName);
			connInfo.setSzPwd(password);
			
			try {
				RestoreService.getInstance().checkContainRecoveryPoints(connInfo);
			}catch(ServiceException e) {
				logger.error("Check contain recovery points error", e);
			}
			
			if(lock != null) {
				logger.debug("lock to " + dest + ", domain:" + domain + ", userName:" + userName);
				lock.lock();
			}
			context = getNativeFacade().openSearchCatalog(sessionPath, searchDir, caseSensitive, includeSubDir, pattern);

			if (logger.isDebugEnabled())
				logger.debug(StringUtil.convertObject2String(context));
		} catch (ServiceException se) {
			throw se;
		} catch(Throwable e){
			logger.error("openSearchCatalog()", e);
			throw generateInternalErrorAxisFault();
		} finally {
			if(lock != null) {
				lock.unlock();
			}
		}

		if (context != null && context.getContextID() == 0 && context.getTag() == 0)
		{
			//No results are found
			logger.error("openSearchCatalog() - No results found because context is not valid");
			throw generateAxisFault(FlashServiceErrorCode.Browser_SearchPathNotValid);
		}

		logger.debug("openSearchCatalog() - end");
		return context;
	}

	public SearchContext openSearchCatalog(String sessionPath, String searchDir, boolean caseSensitive,	boolean includeSubDir, String pattern) throws ServiceException {
		logger.debug("openSearchCatalog() - start");
		if (logger.isDebugEnabled()){
			logger.debug("session path:"+sessionPath);
			logger.debug("searchDir:"+searchDir);
			logger.debug("caseSensitive:"+caseSensitive);
			logger.debug("includeSubDir:"+includeSubDir);
			logger.debug("pattern:"+pattern);
		}

		SearchContext context = null;
		try{
			if (StringUtil.isEmptyOrNull(sessionPath))
				this.generateAxisFault(FlashServiceErrorCode.Common_NullParameter);
			
			// max length is 170 + 1 (1 is for the backslash. Refer to other places uses getPathMaxLength)
			long maxLength =  BackupService.getInstance().getPathMaxLength();
			if(sessionPath.length() > maxLength + 1){
				String strMaxLength = ""+ maxLength;
				throw new ServiceException(strMaxLength,FlashServiceErrorCode.RestoreJob_DestinationPath_Exceed_Max);
			}

			context = getNativeFacade().openSearchCatalog(sessionPath, searchDir, caseSensitive, includeSubDir, pattern);

			if (logger.isDebugEnabled())
				logger.debug(StringUtil.convertObject2String(context));
		}catch (ServiceException se) {
			throw se;
		}
		catch(Throwable e){
			logger.error("openSearchCatalog()", e);
			throw generateInternalErrorAxisFault();
		}

		if (context != null && context.getContextID() == 0 && context.getTag() == 0)
		{
			//No results are found
			logger.error("openSearchCatalog() - No results found because context is not valid");
			throw generateAxisFault(FlashServiceErrorCode.Browser_SearchPathNotValid);
		}

		logger.debug("openSearchCatalog() - end");
		return context;
	}
	
	public void closeSearchCatalog(SearchContext context) throws ServiceException {
		logger.debug("closeSearchCatalog() - start");
		if (logger.isDebugEnabled())
			logger.debug(StringUtil.convertObject2String(context));

		try{
			
			//pidma02
			if (context.getCurrKind() == SearchContext.KIND_MSG)
			{
				getNativeFacade().closeSearchCatalog(context);
			}
			else
			{
				CSearchSessionManager searchManager = CSearchSessionManager.getInstance();
				CSearchController searchCont = searchManager.getSearchController(context.getContextID());
			
				searchCont.closeSearch();
			}
			logger.debug("closeSearchCatalog() - end");
		}catch(Throwable e){
			logger.error("closeSearchCatalog()", e);
			throw generateInternalErrorAxisFault();
		}
	}

public SearchResult searchMsgNext(SearchContext context) throws ServiceException {
		logger.debug("searchNext() - start");
		if (logger.isDebugEnabled())
			logger.debug(StringUtil.convertObject2String(context));

		if (context == null)
			throw generateAxisFault(FlashServiceErrorCode.Common_NullParameter);
		SearchResult result = new SearchResult();
		result.setNextKind(2);
		try {
			JSearchResult sr = getNativeFacade().searchMsgNext(context);
			result.setCurrent(sr.getCurrent());
			result.setFound(sr.getFound());
			List<MsgSearchRec> itemList = new LinkedList<MsgSearchRec>();
			for (JMsgSearchRec detail : sr.getMsgDetail()) {
				itemList.add(convert2MsgSearchRec(detail));
			}
			result.setMsgDetail(itemList.toArray(new MsgSearchRec[0]));

			if (logger.isDebugEnabled()) {
				logger.debug(StringUtil.convertObject2String(result));
				logger
						.debug(StringUtil.convertArray2String(result
								.getDetail()));
			}
			logger.debug("searchNext() - end");
			return result;
		} catch (Throwable e) {
			logger.error("searchNext()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	private MsgSearchRec convert2MsgSearchRec(JMsgSearchRec detail) {
		MsgSearchRec rec = new MsgSearchRec();
		rec.setMsgRec(convert2GRTCatalogItem(detail.getMsgRec()));
		rec.setEdbDisplayName(detail.getEdbDisplayName());
		rec.setEdbFullPath(detail.getEdbFullPath());
		rec.setEdbType(detail.getEdbType());
		rec.setMailboxOrSameLevelName(detail.getMailboxOrSameLevelName());
		rec.setMailFullDisplayPath(detail.getMailFullDisplayPath());
		rec.setSessionNumber(detail.getSessionNumber());
		rec.setSubSessionNumber(detail.getSubSessionNumber());
	    
	    rec.setFullSessNum(detail.getUlFullSessNum());
	    rec.setEncryptInfo(detail.getUlEncryptInfo());
	    rec.setbKTime(detail.getUlBKTime() == 0 ? null : new Date(detail.getUlBKTime()));
	    rec.setBkTimeZoneOffset(ServiceUtils.getServerTimeZoneOffsetByDate(rec.getbKTime()));
	    rec.setbKDest(detail.getWzBKDest());
	    rec.setJobName(detail.getWzJobName());
	    rec.setpWDHash(detail.getWzPWDHash());
	    rec.setSessGUID(detail.getWzSessGUID());
	    rec.setFullSessGUID(detail.getWzFullSessGUID());
	    
		return rec;
	}

	public SearchResult searchFileNext(SearchContext context)
			throws ServiceException {
		logger.debug("BrowserService: searchNext() - start");
		SearchResult sr = null;
		if (logger.isDebugEnabled())
			logger.debug(StringUtil.convertObject2String(context));

		if (context == null)
			throw generateAxisFault(FlashServiceErrorCode.Common_NullParameter);

		try {
			
			CSearchSessionManager searchManager = CSearchSessionManager.getInstance();
			CSearchController searchCont = searchManager.getSearchController(context.getContextID());
			
			searchCont.setContext(context);
			sr = searchCont.searchNext();

		}catch(ServiceException se) {
			throw se;
		}catch(Throwable e){
			logger.error("searchNext()", e);
			throw generateInternalErrorAxisFault();
		}
		logger.debug("BrowserService: searchNext() - stop");
		return sr;
	}

	public SearchResult searchNext(SearchContext context) throws ServiceException {
		logger.debug("searchNext() - start");
		if (logger.isDebugEnabled())
			logger.debug(StringUtil.convertObject2String(context));

		if (context == null)
			throw generateAxisFault(FlashServiceErrorCode.Common_NullParameter);

		if (context.getCurrKind() == SearchContext.KIND_MSG) {
			SearchResult msgSR = searchMsgNext(context);
			if (msgSR.hasNext()) {
				msgSR.setNextKind(context.getCurrKind());
				return msgSR;
			} else {
				if ((context.getSearchkind() & SearchContext.KIND_FILE) > 0) {
					msgSR.setNextKind(SearchContext.KIND_FILE);
					context.setCurrKind(SearchContext.KIND_FILE);
				} else {
					msgSR.setNextKind(SearchContext.KIND_END);
				}
				return msgSR;
			}
		} else if(context.getCurrKind() == SearchContext.KIND_FILE || !context.isExcludeFileSystem()){
			SearchResult fileSR = searchFileNext(context);
			if (fileSR.hasNext()) {
				fileSR.setNextKind(context.getCurrKind());
				return fileSR;
			} else {
				fileSR.setNextKind(SearchContext.KIND_END);
				return fileSR;
			}
		}
		return null;
	}

	private CatalogItem convert2CatalogItem(JCatalogDetail detail, boolean useCatalog){
		CatalogItem item = new CatalogItem();
		item.setName(detail.getDisplayName());
		item.setComponentName(detail.getLongName());
		item.setId(detail.getLongNameID());
		item.setType(detail.getDataType());
		item.setSize((detail.getFileSizeHigh()<<32)+detail.getFileSize());
		item.setDate(detail.getFileDate() == 0 ? null : new Date(detail.getFileDate()));
		item.setServerTimeZoneOffset(ServiceUtils.getServerTimeZoneOffsetByDate(item.getDate()));
		item.setPath(detail.getPath());
		item.setSessionNumber(detail.getSessionNumber());
		item.setSubSessionNumber(detail.getSubSessionNumber());
		item.setChildrenCount(detail.getChildrenCount());
		item.setUsePathID(useCatalog);
		item.setFullSessNum(detail.getFullSessNum());
		item.setEncryptInfo(detail.getEncryptInfo());
		item.setBackupDest(detail.getBackupDest());
		item.setJobName(detail.getJobName());
		item.setBackupTime(detail.getBackupTime() == 0 ? null : new Date(detail.getBackupTime()));
		item.setBkServerTimeZoneOffset(ServiceUtils.getServerTimeZoneOffsetByDate(item.getBackupTime()));
		item.setPwdHash(detail.getPwdHash());
		item.setSessionGuid(detail.getSessionGuid());
		item.setFullSessionGuid(detail.getFullSessionGuid());
		item.setDefaultSessPwd(detail.isDefaultSessPwd());

		return item;
	}

	public long validateDest(String path, String domain, String user, String pwd,int mode)
			throws ServiceException {
		logger.debug("validateDest() - start");
		long result = 0;
		try {
			logger.debug("validateDest() - invoke JNI");
			//For Backup and Archive destination modes
			if(path !=null )
			{
				if(path.startsWith("\\\\") && !path.substring(2).contains("\\")){
					throw generateAxisFault(path, FlashServiceErrorCode.Browser_Invalid_Share_Folder_Path);
				}
				
				if(mode == ARCHIVE_DEST_MODE){
					 BackupService.getInstance().checkDestinationLengthForArchive(path);
				}else if(mode == BACKUP_MODE){
					BackupService.getInstance().checkDestinationLength(path);
				}else{
					if(path.length() > BROWSE_LENGTH){
						throw generateAxisFault(BROWSE_LENGTH+"",FlashServiceErrorCode.Browser_Source_Path_Exceeds_Max);
					}
				}
			}
			result = this.getNativeFacade().validateDestUser(path, domain,
					user, pwd);
		} catch (ServiceException se) {
			if (FlashServiceErrorCode.BackupConfig_ERR_DEST_WINSYSMSG
					.equalsIgnoreCase(se.getErrorCode())) {
				se.setErrorCode(FlashServiceErrorCode.BackupConfig_ERR_ValidateDestFailed);
			}
			throw se;
		} catch (Throwable e) {
			logger.error("Error during invoke JNI", e);
			throw generateInternalErrorAxisFault();
		}
		logger.debug("JNI return:" + result);
		logger.debug("validateDest - end");
		return result;
	}
	
	public long validateDest(String path, String domain, String user, String pwd)throws ServiceException
	{
		logger.debug("validateDest() - start");
		long result = 0;
		try {
			logger.debug("validateDest() - invoke JNI");
			result = this.getNativeFacade().validateDestUser(path, domain, user, pwd);
		} catch (ServiceException se) {
			if (FlashServiceErrorCode.BackupConfig_ERR_DEST_WINSYSMSG
						.equalsIgnoreCase(se.getErrorCode())) {
					se
							.setErrorCode(FlashServiceErrorCode.BackupConfig_ERR_ValidateDestFailed);
			}
			throw se;
		} catch (Throwable e) {
			logger.error("Error during invoke JNI", e);
			throw generateInternalErrorAxisFault();
		}
		logger.debug("JNI return:" + result);
		logger.debug("validateDest - end");
		return result;
      }

	public long validateSource(String path, String domain, String user,
			String pwd) throws ServiceException {
		logger.debug("validateSource() - start");
		long result = 0;
		if(path.length() > BrowserService.BROWSE_LENGTH)
			throw generateAxisFault(BrowserService.BROWSE_LENGTH+"",FlashServiceErrorCode.Browser_Source_Path_Exceeds_Max);	
		try {
			logger.debug("validateSource() - invoke JNI");
			result = this.getNativeFacade().validateDestUser(path, domain, user, pwd);
		} catch (ServiceException se) {
			if (FlashServiceErrorCode.BackupConfig_ERR_FolderIsNotWritable
					.equalsIgnoreCase(se.getErrorCode())) {
				logger.debug("Ignore not writable exception", se);
			} else if (FlashServiceErrorCode.BackupConfig_ERR_DEST_WINSYSMSG
					.equalsIgnoreCase(se.getErrorCode())) {
				se.setErrorCode(FlashServiceErrorCode.RestoreJob_SourceInvalid);
				throw se;
			} else {
				throw se;
			}
		} catch (Throwable e) {
			logger.error("Error during invoke JNI", e);
			throw generateInternalErrorAxisFault();
		}
		logger.debug("JNI return:" + result);
		logger.debug("validateSource - end");
		return result;
	}

	public long disconnectRemotePath(String path, String domain, String user,
			String pwd, boolean force) throws ServiceException{
		logger.debug("disconnectRemotePath() - start");
		
		Lock lock = null;
		try {
			lock = RemoteFolderConnCache.getInstance().getLockByPath(path);
			if(lock != null) {
				lock.lock();
			}
			
			long result = getNativeFacade().disconnectRemotePath(path, domain, user, pwd, force);
			logger.debug("result:" + result);
			logger.debug("disconnectRemotePath() - end");
			return result;
		}finally {
			if(lock != null) {
				lock.unlock();
			}
		}
	}

	public boolean checkRemotePathAccess(String path, String domain, String user,
			String pwd)  throws ServiceException{

		logger.debug("checkRemotePathAccess() - start");

		boolean result = getNativeFacade().checkRemotePathAccess(path, domain, user, pwd);
		logger.debug("result:" + result);

		logger.debug("checkRemotePathAccess() - end");
		return result;

	}

	public PagedCatalogItem getPagedCatalogItems(String catPath, long parentID,
			int start, int size) throws ServiceException {
		logger.debug("getCatalogItems(String, long) - start");
		logger.debug(catPath);
		logger.debug(parentID);
		logger.debug(parentID);

		if (parentID < 0)
			parentID = -1;

		try {
			List<CatalogItem> resultList = new LinkedList<CatalogItem>();

			JPagedCatalogItem jItem = getNativeFacade().getPagedCatalogItems(
					catPath, parentID, start, size);

			long total = 0;

			if (jItem != null && jItem.getDetails() != null) {
				total = jItem.getTotal();

				for (JCatalogDetail detail : jItem.getDetails()) {
					resultList.add(convert2CatalogItem(detail, true));
				}
			}

			if (logger.isDebugEnabled()) {
				logger.debug(StringUtil.convertList2String(resultList));
				logger.debug("total:" + total);
			}
			PagedCatalogItem pcItem = new PagedCatalogItem();
			pcItem.setCaltalogItems(resultList.toArray(new CatalogItem[0]));
			pcItem.setTotal(total);
			logger.debug("getCatalogItems(String, long) - end");
			return pcItem;
		} catch (Throwable e) {
			logger.error("getTopLevelCatalogItems()", e);
			throw generateInternalErrorAxisFault();
		}
	}
	
	public PagedCatalogItem getPagedFileItems(String mountVolGUID, String catPath, String parentID, int start, int size) throws ServiceException {
		logger.debug("getCatalogItems(String, long) - start");
		logger.debug(catPath);
		logger.debug(parentID);
		logger.debug(parentID);

		try {
			List<CatalogItem> resultList = new LinkedList<CatalogItem>();

			JPagedCatalogItem jItem = getNativeFacade().getPagedFileItems(
					mountVolGUID, catPath, parentID, start, size);

			long total = 0;

			if (jItem != null && jItem.getDetails() != null) {
				total = jItem.getTotal();

				for(JCatalogDetail detail : jItem.getDetails()){
					detail.setPath(detail.getLongName().substring(0, detail.getLongName().lastIndexOf("\\")));
					detail.setLongName(detail.getDisplayName());
					resultList.add(convert2CatalogItem(detail, false));
				}
			}

			if (logger.isDebugEnabled()) {
				logger.debug(StringUtil.convertList2String(resultList));
				logger.debug("total:" + total);
			}
			PagedCatalogItem pcItem = new PagedCatalogItem();
			pcItem.setCaltalogItems(resultList.toArray(new CatalogItem[0]));
			pcItem.setTotal(total);
			logger.debug("getCatalogItems(String, long) - end");
			return pcItem;
		} catch (Throwable e) {
			logger.error("getTopLevelCatalogItems()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public void checkRestoreSession(String sessionPath, String domain,
			String userName, String passwd, List<Integer> list) throws ServiceException{
		logger.debug("checkRestoreSession(String, String, String, String, List<Integer>) - start");
		logger.debug(sessionPath);
		logger.debug(domain);
		logger.debug(userName);
		logger.debug(domain);
		logger.debug(list);
		if(list == null || list.isEmpty()) return;
		if (logger.isDebugEnabled()) {
			logger.debug(StringUtil.convertList2String(list));
		}

		try {
			getNativeFacade().checkRestoreSession(sessionPath, domain, userName, passwd, list);
			logger.debug("checkRestoreSession(String, String, String, String, List<Integer>) - end");
		}catch (ServiceException se) {
			throw se;
		} catch (Throwable e) {
			logger.error("checkRestoreSession()", e);
			throw generateInternalErrorAxisFault();
		}

	}

	public GRTCatalogItem[] getGRTCatalogItems(String catalogFilePath,
			long lowSelfid, long highSelfid) throws ServiceException {
		logger.debug("getGRTCatalogItems(String, long, long) - start");
		logger.debug("filepath:" + catalogFilePath);
		logger.debug("lowSelfid:" + lowSelfid);
		logger.debug("highSelfid:" + highSelfid);

		try {
			List<GRTCatalogItem> retList = new LinkedList<GRTCatalogItem>();

			List<JMsgRec> details = getNativeFacade().getGRTCatalogItems(
					catalogFilePath, lowSelfid, highSelfid);
			for (JMsgRec detail : details) {
				retList.add(convert2GRTCatalogItem(detail));
			}

			if (logger.isDebugEnabled()) {
				logger.debug(StringUtil.convertList2String(retList));
			}
			logger.debug("getCatalogItems(String, long) - end");
			return retList.toArray(new GRTCatalogItem[0]);
		} catch (Throwable e) {
			logger.error("getTopLevelCatalogItems()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public GRTCatalogItem convert2GRTCatalogItem(JMsgRec d) {
		GRTCatalogItem item = new GRTCatalogItem();
		item.setCp_Flag(d.getCp_Flag());
		item.setHighObjBody(d.getHighObjBody());
		item.setLowObjBody(d.getLowObjBody());
		item.setHighObjParentid(d.getHighObjParentid());
		item.setLowObjParentid(d.getLowObjParentid());
		item.setHighObjSelfid(d.getHighObjSelfid());
		item.setLowObjSelfid(d.getLowObjSelfid());
		item.setHighObjSize(d.getHighObjSize());
		item.setLowObjSize(d.getLowObjSize());
		if (d.getObjDate() > 0) {
			item.setObjDate(GRTCatalogItem.dateToStandardString(new Date(d
					.getObjDate())));
		}
		item.setObjFlags(d.getObjFlags());
		item.setObjInfo(d.getObjInfo());
		item.setObjName(Base64.encode(d.getObjName()));
		item.setObjType(d.getObjType());
		item.setChildrenCount(d.getChildrenCount());

		item.setSender(d.getSender());
		item.setReceiver(d.getReceiver());
		item.setSentTime(d.getSentTime() == 0 ? null : new Date(d.getSentTime()*1000));
		item.setSendTZOffset(ServiceUtils.getServerTimeZoneOffsetByDate(item.getSentTime()));
		item.setReceivedTime(d.getReceivedTime() == 0 ? null : new Date(d.getReceivedTime()*1000));
		item.setReceivedTZOffset(ServiceUtils.getServerTimeZoneOffsetByDate(item.getReceivedTime()));
		item.setFlag(d.getFlag());
		item.setItemSize(d.getItemSize());

		return item;
	}

	public String getMsgCatalogPath(String dbIdentify, String backupDestination,
			long sessionNumber, long subSessionNumber) throws ServiceException {
		logger.debug("getMsgCatalogPath(String, String, long, long) - start");
		logger.debug("dbIdentify:" + dbIdentify);
		logger.debug("backupDestination:" + backupDestination);
		logger.debug("sessionNumber:" + sessionNumber);
		logger.debug("subSessionNumber:" + subSessionNumber);

		try {
			String ret = getNativeFacade().getMsgCatalogPath(dbIdentify, backupDestination,
					sessionNumber, subSessionNumber);

			logger.debug("msgCatalogPath:" + ret);
			logger.debug("getMsgCatalogPath(String, String, long, long) - end");
			return ret;
		} catch (Throwable e) {
			logger.error("getMsgCatalogPath()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public PagedGRTCatalogItem getPagedGRTCatalogItems(String msgCatPath, long lowSelfID, long highSelfID,
			int start, int size) throws ServiceException {
		logger.debug("getPagedGRTCatalogItems(String, long, long, int, int) - start");
		logger.debug(msgCatPath);
		logger.debug(lowSelfID);
		logger.debug(highSelfID);
		logger.debug(start);
		logger.debug(size);

		try {
			PagedGRTCatalogItem pagedItem = getNativeFacade().getPagedGRTCatalogItems(
					msgCatPath, lowSelfID, highSelfID, start, size);
			logger.debug("getPagedGRTCatalogItems(String, long, long, int, int) - end");
			return pagedItem;
		} catch (Throwable e) {
			logger.error("getPagedGRTCatalogItems()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public PagedGRTCatalogItem browseGRTCatalog(GRTBrowsingContext context) throws ServiceException
	{
		logger.debug("browseGRTCatalog(GRTBrowsingContext) - begin");

		try
		{
			PagedGRTCatalogItem pagedItem = getNativeFacade().browseGRTCatalog(context);
			logger.debug("browseGRTCatalog(GRTBrowsingContext) - end");
			return pagedItem;
		}
		catch (Throwable e)
		{
			logger.error("browseGRTCatalog(GRTBrowsingContext)", e);
			throw generateInternalErrorAxisFault();
		}
	}
	@Deprecated
	public void closeBrowseGRTCatalog(GRTBrowsingContext context) throws ServiceException {

		logger.debug("closeBrowseGRTCatalog(long) - begin");

		try
		{
			getNativeFacade().closeBrowseGRTCatalog(context);
			logger.debug("closeBrowseGRTCatalog(long) - end");
			return;
		}
		catch (Throwable e)
		{
			logger.error("closeBrowseGRTCatalog(long)", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public long d2dExCheckUser(String domain, String user, String password)
		throws ServiceException {
		logger.debug("d2dExCheckUser(String, String, String) - start");
		logger.debug(domain);
		logger.debug(user);
		//logger.debug(password);

		try {
			long ret = getNativeFacade().d2dExCheckUser(
					domain, user, password);
			logger.debug("d2dExCheckUser(String, String, String) - end");
			return ret;
		} catch (Throwable e) {
			logger.error("d2dExCheckUser()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public ExchangeDiscoveryItem[] getTreeExchangeChildren(ExchangeDiscoveryItem parentItem, String strUser, String strPassword) throws ServiceException {
		logger.debug("getTreeExchangeChildren(ExchangeDiscoveryItem) - start");
		//logger.debug("ExchangeDiscoveryItem:" + catalogFilePath);

		try
		{
			List<ExchangeDiscoveryItem> retList = new LinkedList<ExchangeDiscoveryItem>();

			if (parentItem == null)
			{
				ExchangeDiscoveryItem item = new ExchangeDiscoveryItem();

				String orgName = getNativeFacade().aoeGetOrganizationName(strUser, strPassword);

				if (orgName == null)
				{
					orgName = "";
				}

				item.setName(orgName);

				item.setType(ExchangeDiscoveryItem.EXCH_DISC_TYPE_ORGANIZATION);
				retList.add(item);
			}
			else
			{
				switch (parentItem.getType())
				{
				case ExchangeDiscoveryItem.EXCH_DISC_TYPE_ORGANIZATION:
				{
					List<JExchangeDiscoveryItem> itemList = getNativeFacade().aoeGetServers(strUser, strPassword);

					for (JExchangeDiscoveryItem jItem : itemList) {
						ExchangeDiscoveryItem item = jItem.Convert2ExchangeDiscoveryItem();
						item.setType(ExchangeDiscoveryItem.EXCH_DISC_TYPE_SERVER);
						retList.add(item);
					}
					break;
				}
				case ExchangeDiscoveryItem.EXCH_DISC_TYPE_SERVER: {

					List<JExchangeDiscoveryItem> itemList = null;
					if (parentItem.getnExVersion() == ExchangeDiscoveryItem.EXCH_VER_2010 || parentItem.getnExVersion() == ExchangeDiscoveryItem.EXCH_VER_2013)
					{
						itemList = getNativeFacade().aoeGetEDBs(parentItem.getPwszDN(), strUser, strPassword);
						for (JExchangeDiscoveryItem jItem : itemList) {
							ExchangeDiscoveryItem item =jItem.Convert2ExchangeDiscoveryItem();

							int nType = item.getIsPublic()==1 ? ExchangeDiscoveryItem.EXCH_DISC_TYPE_PUBLIC_FOLDER : ExchangeDiscoveryItem.EXCH_DISC_TYPE_MBS_DB;

							item.setType(nType);
							retList.add(item);
						}
					}
					else
					{
						itemList = getNativeFacade().aoeGetStorageGroups(parentItem.getPwszDN(), strUser, strPassword);
						for (JExchangeDiscoveryItem jItem : itemList) {
							ExchangeDiscoveryItem item = jItem.Convert2ExchangeDiscoveryItem();
							item.setType(ExchangeDiscoveryItem.EXCH_DISC_TYPE_STORAGE_GROUP);
							retList.add(item);
						}
					}

					break;
				}
				case ExchangeDiscoveryItem.EXCH_DISC_TYPE_STORAGE_GROUP: {
					List<JExchangeDiscoveryItem> itemList = getNativeFacade().aoeGetEDBs(parentItem.getPwszDN(), strUser, strPassword);
					for (JExchangeDiscoveryItem jItem : itemList) {
						ExchangeDiscoveryItem item = jItem.Convert2ExchangeDiscoveryItem();

						int nType = item.getIsPublic()==1 ? ExchangeDiscoveryItem.EXCH_DISC_TYPE_PUBLIC_FOLDER : ExchangeDiscoveryItem.EXCH_DISC_TYPE_MBS_DB;

						item.setType(nType);
						retList.add(item);
					}
					break;
				}
				case ExchangeDiscoveryItem.EXCH_DISC_TYPE_MBS_DB:
				case ExchangeDiscoveryItem.EXCH_DISC_TYPE_PUBLIC_FOLDER:
				{
					List<JExchangeDiscoveryItem> itemList = getNativeFacade().aoeGetMailboxes(parentItem.getPwszDN(), strUser, strPassword);
					for (JExchangeDiscoveryItem jItem : itemList) {
						ExchangeDiscoveryItem item = jItem.Convert2ExchangeDiscoveryItem();
						item.setType(ExchangeDiscoveryItem.EXCH_DISC_TYPE_MAILBOX);
						retList.add(item);
					}
					break;
				}
				default:
					break;
				}

			}

			if (logger.isDebugEnabled()) {
				logger.debug(StringUtil.convertList2String(retList));
			}

			logger.debug("getTreeExchangeChildren(ExchangeDiscoveryItem) - end");

			// sort the result by type then name
			Collections.sort(retList, new Comparator<ExchangeDiscoveryItem>()
			{
				@Override
				public int compare(ExchangeDiscoveryItem paramT1, ExchangeDiscoveryItem paramT2)
				{
					int ret = 0;
					if (paramT1 != null && paramT2 != null)
					{
						if (paramT1.getType() != paramT2.getType())
						{
							ret = paramT1.getType() - paramT2.getType();
						}
						else if (paramT1.getName() != null && paramT2.getName() != null)
						{
							ret = paramT1.getName().compareTo(paramT2.getName());
						}
					}
					return ret;
				}

			});

			return retList.toArray(new ExchangeDiscoveryItem[0]);

		} catch (Throwable e) {
			logger.error("getTreeExchangeChildren()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public long validateCatalogFileExist(String dbIdentify, String backupDestination, long sessionNumber,
			long subSessionNumber) throws ServiceException
	{
		try
		{
			long catalogFileEixst = getNativeFacade().validateCatalogFileExist(dbIdentify, backupDestination, sessionNumber,
					subSessionNumber);

			logger.debug("validateCatalogFileExist:" + catalogFileEixst);
			return catalogFileEixst;
		}
		catch (Throwable e)
		{
			logger.error("validateCatalogFileExist()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public PagedExchangeDiscoveryItem getPagedTreeExchangeChildren(ExchangeDiscoveryItem parentItem, int start, int size, String strUser, String strPassword)
			throws ServiceException
	{
		logger.debug("getPagedTreeExchangeChildren(ExchangeDiscoveryItem, int, int) - start");
		logger.debug(parentItem);
		logger.debug(start);
		logger.debug(size);

		try
		{
			ExchangeDiscoveryItem[] allItems = getTreeExchangeChildren(parentItem, strUser, strPassword);

			PagedExchangeDiscoveryItem pagedItem = new PagedExchangeDiscoveryItem();

			if (allItems != null)
			{
				pagedItem.setTotal(allItems.length);

				List<ExchangeDiscoveryItem> tempList = new LinkedList<ExchangeDiscoveryItem>();

				for (int i = start; i < start + size && i < allItems.length; i++)
				{
					tempList.add(allItems[i]);
				}

				pagedItem.setExchangeDiscoveryItems(tempList.toArray(new ExchangeDiscoveryItem[0]));
			}

			logger.debug("getPagedTreeExchangeChildren(ExchangeDiscoveryItem, int, int) - end");
			return pagedItem;
		}
		catch (Throwable e)
		{
			logger.error("getPagedTreeExchangeChildren()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	public ArchiveCatalogItem[] getArchiveCatalogItems(long in_volumeHandle,String in_catalogFilePath)  throws ServiceException{
		logger.debug("getArchiveCatalogItems(String, long) - start");
		logger.debug(in_volumeHandle);
		logger.debug(in_catalogFilePath);

		if (in_volumeHandle<0)
			in_volumeHandle = -1;

		try{
			/*long hHandle = in_volumeHandle;
			if(in_volumeHandle == 5)
			{
			hHandle = getNativeFacade().getArchivedVolumeHandle("C");
			}*/

			List<ArchiveCatalogItem> resultList = new ArrayList<ArchiveCatalogItem>();
			List<JArchiveCatalogDetail> details = null;
			details = getNativeFacade().getArchiveCatalogItems(in_volumeHandle,in_catalogFilePath);

			if(details != null)
			{
				for(JArchiveCatalogDetail detail : details){
					resultList.add(convert2ArchiveCatalogItem(detail));
				}
			}

			if (logger.isDebugEnabled()){
				logger.debug(StringUtil.convertList2String(resultList));
			}
			logger.debug("getArchiveCatalogItems(String, long) - end");
			return resultList.toArray(new ArchiveCatalogItem[0]);
		}catch(Throwable e){
			logger.error("getTopLevelCatalogItems()", e);
			throw generateInternalErrorAxisFault();
		}
	}
	
	
	public ArchiveCatalogItem[] getArchivePagedCatalogItems(long in_volumeHandle,String in_catalogFilePath,  long lIndex, long lCount)  throws ServiceException{
		logger.debug("getArchiveCatalogItems(String, long) - start");
		logger.debug(in_volumeHandle);
		logger.debug(in_catalogFilePath);

		if (in_volumeHandle<0)
			in_volumeHandle = -1;

		try{
			long hHandle = in_volumeHandle;
			if(in_volumeHandle == 5)
			{
			hHandle = getNativeFacade().getArchivedVolumeHandle("C");
			}

			List<ArchiveCatalogItem> resultList = new ArrayList<ArchiveCatalogItem>();
			List<JArchiveCatalogDetail> details = null;
			details = getNativeFacade().getArchiveCatalogItems(in_volumeHandle,in_catalogFilePath,lIndex,lCount);

			if(details != null)
			{
				for(JArchiveCatalogDetail detail : details){
					resultList.add(convert2ArchiveCatalogItem(detail));
				}
			}

			if (logger.isDebugEnabled()){
				logger.debug(StringUtil.convertList2String(resultList));
			}
			logger.debug("getArchiveCatalogItems(String, long) - end");
			return resultList.toArray(new ArchiveCatalogItem[0]);
		}catch(Throwable e){
			logger.error("getTopLevelCatalogItems()", e);
			throw generateInternalErrorAxisFault();
		}
	}

	private ArchiveCatalogItem convert2ArchiveCatalogItem(JArchiveCatalogDetail detail){
		ArchiveCatalogItem item = new ArchiveCatalogItem();

		item.setName(detail.getName());
		item.setVolumeHandle(detail.getVolumeHandle());
		item.setPath(detail.getFullPath());
		item.setFullPath(detail.getFullPath());
		item.setType(detail.getType());
		item.setChildrenCount(detail.getChildrenCount());
		Long lFileVersionsCount = detail.getVersionsCount() == null ? 0 : Long.parseLong(detail.getVersionsCount());
		item.setVersionsCount(lFileVersionsCount);

		if(lFileVersionsCount > 0)
		{
			ArchiveFileVerionDetail[] fileVersionsList = new ArchiveFileVerionDetail[lFileVersionsCount.intValue()];

			for(int iIndex = 0;iIndex < lFileVersionsCount;iIndex++)
			{
				JArchiveFileVersionDetail FileDetail = detail.getfileVersionsList().get(iIndex);

				fileVersionsList[iIndex] = new ArchiveFileVerionDetail();
				fileVersionsList[iIndex].setVersion(Integer.parseInt((FileDetail.getVersion())));
				fileVersionsList[iIndex].setFileSize((FileDetail.getFileSizeHigh()<<32)+FileDetail.getFileSizeLow());
				//Calendar cdModifiedDateTime = new java.util.GregorianCalendar((int)FileDetail.getmodifiedYear(),(int)FileDetail.getmodifiedMonth()-1,(int)FileDetail.getmodifiedDay(),(int)FileDetail.getmodifiedHour(),(int)FileDetail.getmodifiedMin(),(int)FileDetail.getmodifiedSec());
				//fileVersionsList[iIndex].setModifiedTime(cdModifiedDateTime.getTime());


				long longModifytime = FileDetail.getmodDateTime();
				// Filetime Epoch is JAN 01 1601
				// java date Epoch is January 1, 1970
				// so take the number and subtract java Epoch:
				long javaModifyTime = longModifytime - 0x19db1ded53e8000L;
				// convert UNITS from (100 nano-seconds) to (milliseconds)
				javaModifyTime /= 10000;
				// the specified number of milliseconds since the standard base
				// time known as "the epoch", namely January 1, 1970, 00:00:00 GMT.
				fileVersionsList[iIndex].setModifiedTime(new Date(javaModifyTime));
				//Calendar cdArchiveDateTime = new java.util.GregorianCalendar((int)FileDetail.getarchivedYear(),(int)FileDetail.getarchivedMonth()-1,(int)FileDetail.getarchivedDay(),(int)FileDetail.getarchivedHour(),(int)FileDetail.getarchivedMin(),(int)FileDetail.getarchivedSec());


				long longArchivetime = FileDetail.getarchiveDateTime();
				long javaArchiveTime = longArchivetime - 0x19db1ded53e8000L;
				javaArchiveTime /= 10000;
				fileVersionsList[iIndex].setArchivedTime(new Date(javaArchiveTime));
				fileVersionsList[iIndex].setFileType(FileDetail.getFileType());
				fileVersionsList[iIndex].setArchivedTimeZoneOffset(ServiceUtils
						.getServerTimeZoneOffsetByDate(fileVersionsList[iIndex]
								.getArchivedTime()));
				fileVersionsList[iIndex].setModifiedTimeZoneOffset(ServiceUtils
						.getServerTimeZoneOffsetByDate(fileVersionsList[iIndex]
								.getModifiedTime()));
			}
			item.setfileVersionsList(fileVersionsList);
		}
		return item;
	}

	public ArchiveCatalogItem[] searchArchiveDestinationItems(
			ArchiveDestinationConfig archiveDestConfig, String in_Searchpath,long in_lSearchOptions,
			String in_fileName,long in_lIndex,long in_lRequiredItems) throws ServiceException{

		String hostName = archiveDestConfig.getstrHostname();
		try{
			InetAddress add = InetAddress.getLocalHost();

			if(hostName == null || hostName.length() == 0)
				hostName = add.getHostName();
			
			List<ArchiveCatalogItem> resultList = new ArrayList<ArchiveCatalogItem>();
			List<JArchiveCatalogDetail> details = null;
			details = getNativeFacade().getArchiveCatalogItemsBySearch(in_fileName,hostName,in_Searchpath,archiveDestConfig,in_lSearchOptions,in_lIndex,in_lRequiredItems);

			if(details != null)
			{
				for(JArchiveCatalogDetail detail : details){
					resultList.add(convert2ArchiveCatalogItem(detail));
				}
			}

			if (logger.isDebugEnabled()){
				logger.debug(StringUtil.convertList2String(resultList));
			}
			logger.debug("getArchiveCatalogItems(String, long) - end");
			return resultList.toArray(new ArchiveCatalogItem[0]);
		}catch(ServiceException se) {
			throw se;
		}catch(Throwable e){
			logger.error("getTopLevelCatalogItems()", e);
			throw generateInternalErrorAxisFault();
		}
	}

    public CatalogInfo[] checkCatalogExist(String destination, long sessionNumber) throws ServiceException
	{
        logger.debug("checkCatalogExist(String, long) - start. destination=" + destination + " sessionNumber=" + sessionNumber);
		
        List<JCatalogInfo> retArr = getNativeFacade().AFSCheckCatalogExist(destination, sessionNumber);
		
        List<CatalogInfo> retList = new LinkedList<CatalogInfo>(); 
        
		for (JCatalogInfo jItem : retArr)
		{
			CatalogInfo item = jItem.Convert2CatalogInfo();
			retList.add(item);
		}        
			
		if (logger.isDebugEnabled())
		{
			logger.debug(StringUtil.convertList2String(retList));
		}
		
		logger.debug("checkCatalogExist(String, long) - end.");		
		
		return retList.toArray(new CatalogInfo[0]);	
	}
    
    
    public long getArchiveChildrenCount(long lVolumeHandle,String in_catalogFilePath)  throws ServiceException{
    	
    	
    	long childCount;
    	
		try {
			//long lVolumeHandle = getNativeFacade().getArchivedVolumeHandle(strVolume);
			
			JRWLong childrenCnt = new JRWLong();
			
			if (in_catalogFilePath == null) {
				in_catalogFilePath = "";
			}
			
			long ret = getNativeFacade().GetArchiveChildrenCount(lVolumeHandle, in_catalogFilePath, childrenCnt);
			
			childCount = 0;
			
			if (ret != 0) {
				childCount = childrenCnt.getValue();
			}
		}catch(Throwable e){
			logger.error("getTopLevelCatalogItems()", e);
			throw generateInternalErrorAxisFault();
		}
    	
    	return childCount;
    	
    }

	public int generateCatalogOnDemand(long sessNum, String backupDest,
			String userName, String passWord, String vmInstanceUUID) throws Exception {
		FileInputStream fIStream = null;
		FileOutputStream fOStream = null;
		Lock lock = null;
		int handle = 0;
		boolean bScriptFound = false;
		WindowsRegistry registry = new WindowsRegistry();
		try {
			handle = registry.openKey(REGISTRY_INSTALLPATH);
			String homeFolder = registry.getValue(handle, REGISTRY_KEY_PATH);
			lock = RemoteFolderConnCache.getInstance().getLockByPath(backupDest);
			if(backupDest.startsWith("\\\\") && lock == null) {
				logger.error("Failed to acquire the lock to path.");
				throw new Exception("Failed to acquire the lock to path.");
			}
			logger.debug("Getting lock to " + backupDest + ", userName:" + userName);
			if(lock != null)
			  lock.lock();
			long netConn = BrowserService.getInstance().getNativeFacade()
					.NetConn(userName, passWord,
							backupDest);
			if (netConn != 0) {
				logger.error("Failed to connect to remote configuration destination");
				logger.error("RemotePath: " + backupDest);
				throw new Exception(
						"Failed to connect to remote configuration destination.");
			}
			String sessionNumber = "S0000000001";
			sessionNumber = sessionNumber.substring(0,sessionNumber.length()-String.valueOf(sessNum).length())+sessNum;
			List<String> dests = new ArrayList<String>();

			long ret = BrowserService.getInstance().getNativeFacade()
					.GetAllBackupDestinations(backupDest, dests);
			// the dests contains the dest in the time order, from old to new
			if (ret != 0)
			{
				logger.error("No backup destinations have been found.");
				throw new Exception("No backup destinations have been found.");
			}
				
			String targetDest = null;
			String bckupDest = null;
			for (String dest : dests) {
				if (!dest.endsWith("\\"))
				{
					bckupDest = dest;
					dest += "\\";
				}
				else
				{
					bckupDest = dest.substring(0, dest.lastIndexOf("\\"));
				}
				File f = new File(dest + "VStore");
				File[] listSessions = f.listFiles();
				if (listSessions == null)
					continue;
				Arrays.sort(listSessions, new Comparator<File>() {

					@Override
					public int compare(File o1, File o2) {
						return o1.getName().compareTo(o2.getName());

					}
				});
				
				for (File session : listSessions) {
					if(session.getName().contentEquals(sessionNumber))
					{
						targetDest = dest + "VStore\\" + sessionNumber;
						break;
					}
				}
				if(targetDest != null)
					break;
			}
			if(targetDest == null)
			{
				logger.error("Failed to find the target session destination.");
				throw new Exception("Failed to find the target session destination.");
			}
			File f = new File(targetDest);
			if(!f.exists())
			{
				logger.error("The specified session folder was not found.");
				throw new Exception("The specified session folder was not found.");
			}
			int iResult = getNativeFacade().GetCatalogStatusForSession(targetDest);
			int iUpdateCatalog = 0;
			if(iResult == FSCAT_DISABLED)
			{
				File[] fileList = f.listFiles();
				for (File file : fileList) {
					if(file.getName().startsWith("JS"))
					{
						bScriptFound = true;
						fIStream =  new FileInputStream(file);
						if(fIStream == null)
						{
							logger.error("Unable to read the catalog job script.");
							throw new Exception("Unable to read the catalog job script.");
						}						
						String targetFolder = homeFolder + "BIN\\JobQueue\\Makeup\\";
						if(vmInstanceUUID != null && !vmInstanceUUID.isEmpty()){
							targetFolder =  homeFolder + "BIN\\JobQueue\\" + vmInstanceUUID + "\\Makeup\\";
						}
						File folder = new File(targetFolder);
						if(!folder.exists()) {
							try {
								Files.createDirectories(folder.toPath());
							}catch(Exception e) {
								logger.error("Failed to create makeup job queue " + targetFolder);
								return -1;
							}
						}
						String targetFile = targetFolder + file.getName();
						File tgtFile = new File(targetFile);
						if(tgtFile.exists() || tgtFile.createNewFile())
						{
							fOStream = new FileOutputStream(tgtFile);
							byte[] tmp = new byte[1024];
							int len = 0;
							while((len = fIStream.read(tmp)) > 0) {
								fOStream.write(tmp, 0, len);
							}
							fIStream.close();
							fOStream.close();
							iUpdateCatalog = getNativeFacade().SetCatalogStatusForSession(bckupDest,targetDest);
							if(iUpdateCatalog != 0){
								logger.error("Failed to update the catalog status in the session's backup info.xml. return value is "+iUpdateCatalog);
								throw new Exception("Failed to update the catalog status in the session's backup info.xml");
							}
							iUpdateCatalog = getNativeFacade().UpdateCatalogJobScript(backupDest, userName, passWord, sessNum, file.getName(), vmInstanceUUID);
							if(iUpdateCatalog != 0){
								logger.error("Failed to update the catalog jobscript with the destination information. return value is "+iUpdateCatalog);
								throw new Exception("Failed to update the catalog jobscript with the destination information.");
							}
						}
						else
						{
							logger.error("Filed to create file in the Makeup queue.");
							throw new Exception("Failed to create file in the Makeup queue.");
						}
						
						break;
					}
				}
				
			}
			else
			{
				logger.error("The catalog generation has been deployed already.");
				return 2;
			}
				
			} catch (ServiceException e) {
			throw new Exception(
					"Failed to generate on demand catalog.");
		}
		finally{
			if(fIStream != null)
				fIStream.close();
			if(fOStream != null)
				fOStream.close();
			if(lock != null)
				lock.unlock();
			registry.closeKey(handle);
		}
		return 1;
	}
	
	public int queryCatalogStatus(long sessNum, String backupDest,
			String userName, String passWord) throws Exception {
		// TODO Auto-generated method stub
		Lock lock = null;
		int handle = 0;
		int iResult = 2;
		WindowsRegistry registry = new WindowsRegistry();
		try {
			handle = registry.openKey(REGISTRY_INSTALLPATH);
			String homeFolder = registry.getValue(handle, REGISTRY_KEY_PATH);
			lock = RemoteFolderConnCache.getInstance().getLockByPath(backupDest);
			if(backupDest.startsWith("\\\\") && lock == null) {
				logger.error("Failed to acquire the lock to path.");
				throw new Exception("Failed to acquire the lock to path.");
			}
			logger.debug("Getting lock to " + backupDest + ", userName:" + userName);
			if(lock != null)
			  lock.lock();
			long netConn = BrowserService.getInstance().getNativeFacade()
					.NetConn(userName, passWord,
							backupDest);
			if (netConn != 0) {
				logger.error("Failed to connect to remote configuration destination");
				logger.error("RemotePath: " + backupDest);
				throw new Exception(
						"Failed to connect to remote configuration destination.");
			}
			String sessionNumber = "S0000000001";
			sessionNumber = sessionNumber.substring(0,sessionNumber.length()-String.valueOf(sessNum).length())+sessNum;
			List<String> dests = new ArrayList<String>();

			long ret = BrowserService.getInstance().getNativeFacade()
					.GetAllBackupDestinations(backupDest, dests);
			// the dests contains the dest in the time order, from old to new
			if (ret != 0)
			{
				logger.error("No backup destinations have been found.");
				throw new Exception("No backup destinations have been found.");
			}
				
			String targetDest = null;
			String bckupDest = null;
			for (String dest : dests) {
				if (!dest.endsWith("\\"))
				{
					bckupDest = dest;
					dest += "\\";
				}
				else
				{
					bckupDest = dest.substring(0, dest.lastIndexOf("\\"));
				}
				File f = new File(dest + "VStore");
				File[] listSessions = f.listFiles();
				if (listSessions == null)
					continue;
				Arrays.sort(listSessions, new Comparator<File>() {

					@Override
					public int compare(File o1, File o2) {
						return o1.getName().compareTo(o2.getName());

					}
				});
				
				for (File session : listSessions) {
					if(session.getName().contentEquals(sessionNumber))
					{
						targetDest = dest + "VStore\\" + sessionNumber;
						break;
					}
				}
				if(targetDest != null)
					break;
			}
			if(targetDest == null)
			{
				logger.error("Failed to find the target session destination.");
				throw new Exception("Failed to find the target session destination.");
			}
			File f = new File(targetDest);
			if(!f.exists())
			{
				logger.error("The specified session folder was not found.");
				throw new Exception("The specified session folder was not found.");
			}
			iResult = getNativeFacade().GetCatalogStatusForSession(targetDest);
						
			} catch (ServiceException e) {
			throw new Exception(
					"Failed to generate on demand catalog.");
		}
		finally{
			if(lock != null)
				lock.unlock();
			registry.closeKey(handle);
		}
		return iResult;
	}
    
	@SuppressWarnings("deprecation")
	public SearchContext openSearchCatalogEx(
			RecoveryPoint[] sessionItemsList, long sessionItemsCount,
			String sessionPath, String domain, String username, String password, String searchDir, boolean caseSensitive,
			boolean includeSubDir, String pattern, String[] encryptedHashKey, String[] encryptedPwd) throws ServiceException {
    	logger.debug("openSearchCatalogEx() - start");
		if (logger.isDebugEnabled()){
			logger.debug("Number of sessions to search in: "+sessionItemsCount);
			logger.debug("session path:"+sessionPath);
			logger.debug("searchDir:"+searchDir);
			logger.debug("caseSensitive:"+caseSensitive);
			logger.debug("includeSubDir:"+includeSubDir);
			logger.debug("pattern:"+pattern);
			logger.debug("encryptedHash:" + encryptedHashKey);
			logger.debug("encryptedPwd: " + encryptedPwd);
		}

		SearchContext context = null;
		try{
			if (StringUtil.isEmptyOrNull(sessionPath))
				this.generateAxisFault(FlashServiceErrorCode.Common_NullParameter);
			
			// max length is 170 + 1 (1 is for the backslash. Refer to other places uses getPathMaxLength)
			long maxLength =  BackupService.getInstance().getPathMaxLength();
			if(sessionPath.length() > maxLength + 1){
				String strMaxLength = ""+ maxLength;
				throw new ServiceException(strMaxLength,FlashServiceErrorCode.RestoreJob_DestinationPath_Exceed_Max);
			}

			//pidma02: Search in all sessions logic
			if(sessionItemsCount == 0)
			{
				Date startDate = new Date();
				Date currentDate = new Date();
				
				startDate.setYear(Calendar.YEAR - 100);
			
				sessionItemsList = RestoreService.getInstance().getRecoveryPoints(sessionPath, domain, 
						username, password, startDate, currentDate,true );
				sessionItemsCount = sessionItemsList.length;
			}//end if
			
			//Pidma02: here we call our new search class which can handle both
			//search in mounted volumes and search in catalog
			//context = getNativeFacade().openSearchCatalog(sessionPath, searchDir, caseSensitive, includeSubDir, pattern);
			if(domain != null && !domain.isEmpty()
					&& username != null && !username.isEmpty()){
				username = domain + "\\" + username;
			}
			RecoveryPoint[] searchSessions = getSeesionNeedToSearch(sessionItemsList);
			CSearchController searchController = new CSearchController(searchSessions, 
					searchSessions.length, sessionPath, searchDir, 
						caseSensitive, includeSubDir, pattern, getNativeFacade(), username, password,
						encryptedHashKey, encryptedPwd);
			
			CSearchSessionManager searchManager = CSearchSessionManager.getInstance();
			long contextID = searchManager.insert(searchController);
			
			context = new SearchContext();
			context.setContextID(contextID);
			
			if (logger.isDebugEnabled())
				logger.debug(StringUtil.convertObject2String(context));
			
		}catch (ServiceException se) {
			throw se;
		}
		catch(Throwable e){
			logger.error("openSearchCatalogEx()", e);
			throw generateInternalErrorAxisFault();
		}

		if (context != null && context.getContextID() == 0 && context.getTag() == 0)
		{
			//No results are found
			logger.error("openSearchCatalogEx() - No results found because context is not valid");
			throw generateAxisFault(FlashServiceErrorCode.Browser_SearchPathNotValid);
		}

		logger.debug("openSearchCatalogEx() - end");
		return context;
    }
	/**
	 * find session with catalog and refs volume. we will copy this session and only search refs volume for this session.
	 * @param inputSessions
	 * @return
	 */
	private RecoveryPoint[] getSeesionNeedToSearch(RecoveryPoint[] inputSessions){
		List<RecoveryPoint> sessionNeedToAdd = new ArrayList<RecoveryPoint>();
		JSystemInfo systemInfo;
		try {
			systemInfo = CommonService.getInstance().getNativeFacade().getSystemInfo();
			if(systemInfo.isWin8()){
				for(RecoveryPoint rp : inputSessions){
					if(rp.getFsCatalogStatus() == RecoveryPoint.FSCAT_FINISH){
						List<RecoveryPointItem> itemList = new ArrayList<RecoveryPointItem>();
						for(RecoveryPointItem item : rp.getItems()){
							if((item.getVolAttr() & RecoveryPointItem.RefsVol )>0 && systemInfo.isWin8() ){
								itemList.add(item);
							}
						}
						if(itemList.size() > 0){
							RecoveryPoint newRp = generateNewRecoveryPoint(rp);
							newRp.setItems(itemList);
							sessionNeedToAdd.add(newRp);
						}
					}
				}
				if(sessionNeedToAdd.size()>0){
					List<RecoveryPoint> searchSessionList = new ArrayList<RecoveryPoint>(Arrays.asList(inputSessions));
					searchSessionList.addAll(sessionNeedToAdd);
					return searchSessionList.toArray(new RecoveryPoint[0]);
				}
			}
		} catch (ServiceException e) {
			logger.error("Error in getSeesionNeedToSearch",e);
		}
		return inputSessions;
		
	}
	
	private RecoveryPoint generateNewRecoveryPoint(RecoveryPoint recoveryPoint){
		RecoveryPoint newRp = new RecoveryPoint();
		newRp.setArchiveJobStatus(recoveryPoint.getArchiveJobStatus());
		newRp.setBackupSetFlag(recoveryPoint.getBackupSetFlag());
		newRp.setBackupStatus(recoveryPoint.getBackupStatus());
		newRp.setBackupType(recoveryPoint.getBackupType());
		newRp.setCanCatalog(recoveryPoint.isCanCatalog());
		newRp.setCanMount(recoveryPoint.isCanMount());
		newRp.setDataSize(recoveryPoint.getDataSize());
		newRp.setEncryptPasswordHash(recoveryPoint.getEncryptPasswordHash());
		newRp.setEncryptType(recoveryPoint.getEncryptType());
		newRp.setFsCatalogStatus(RecoveryPoint.FSCAT_DISABLED);
		newRp.setName(recoveryPoint.getName());
		newRp.setSessionGuid(recoveryPoint.getSessionGuid());
		newRp.setPath(recoveryPoint.getPath());
		newRp.setSessionID(recoveryPoint.getSessionID());
		newRp.setSessionVersion(recoveryPoint.getSessionVersion());
		newRp.setTime(recoveryPoint.getTime());
		newRp.setTimeZoneOffset(recoveryPoint.getTimeZoneOffset());
		return newRp;
	}

	public int unmountVolume(JMountPoint point){
		//zxh, mount cache is implemented in c++
		int ret = -1;
		
		if(point == null)
			return ret;
		
		try {
			ret = getNativeFacade().UnMountVolume(point);
			logger.info("dismout volume " + point.getMountID() + ":UnMountVolume return val is " + ret);
		} catch (Exception e) {
			logger.error("MountMonitorJob---failed--- " + e.getMessage());
		} finally {
		}
		
		return ret;
	}
	
	public void cutAllRemoteConnections() throws ServiceException{
		try {
			this.getNativeFacade().cutAllRemoteConnections();
		} catch (Exception e) {
			logger.error("cutAllRemoteConnections" + e);
		}
	}
	
	/*public static void main(String[] args) {
		try {
			System.loadLibrary("NativeFacade");
			JMountPoint point = new JMountPoint();
			point.setDiskSignature("250622376");
			point.setMountHandle(243842096);
			point.setMountID("\\\\?\\Volume{38693146-6231-4b31-9b0c-50138a91a03f}");
			BrowserService.getInstance().unmountVolume(point);
		}catch(Exception e){
			e.printStackTrace();
		}
	}*/
	
}
