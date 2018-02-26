package com.ca.arcflash.webservice.jni;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.locks.Lock;

import javax.servlet.http.HttpSession;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.failover.model.Disk;
import com.ca.arcflash.jni.common.JJobHistory;
import com.ca.arcflash.jni.common.JMountRecoveryPointParams;
import com.ca.arcflash.service.common.FlashSwitch;
import com.ca.arcflash.service.common.FlashSwitchDefine;
import com.ca.arcflash.service.jni.model.JActLogDetails;
import com.ca.arcflash.service.jni.model.JActivityLog;
import com.ca.arcflash.service.jni.model.JActivityLogResult;
import com.ca.arcflash.service.jni.model.JBackupItem;
import com.ca.arcflash.service.jni.model.JDataSizesFromStorage;
import com.ca.arcflash.service.jni.model.JMergeActiveJob;
import com.ca.arcflash.service.jni.model.JMergeData;
import com.ca.arcflash.service.jni.model.JNetConnInfo;
import com.ca.arcflash.service.jni.model.JProtectionInfo;
import com.ca.arcflash.service.jni.model.JRestorePoint;
import com.ca.arcflash.service.jni.model.JSystemInfo;
import com.ca.arcflash.service.jni.model.MergeJobScript;
import com.ca.arcflash.webservice.AxisFault;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.common.VCMMachineInfo;
import com.ca.arcflash.webservice.data.DataSizesFromStorage;
import com.ca.arcflash.webservice.data.DeployUpgradeInfo;
import com.ca.arcflash.webservice.data.HyperVDestinationInfo;
import com.ca.arcflash.webservice.data.LicInfo;
import com.ca.arcflash.webservice.data.NetworkPath;
import com.ca.arcflash.webservice.data.SourceNodeSysInfo;
import com.ca.arcflash.webservice.data.VMwareVolumeInfo;
import com.ca.arcflash.webservice.data.ad.ADAttribute;
import com.ca.arcflash.webservice.data.ad.ADNode;
import com.ca.arcflash.webservice.data.archive.ArchiveCloudDestInfo;
import com.ca.arcflash.webservice.data.archive.ArchiveDestinationConfig;
import com.ca.arcflash.webservice.data.archive.ArchiveDestinationDetailsConfig;
import com.ca.arcflash.webservice.data.archive.ArchiveFileItem;
import com.ca.arcflash.webservice.data.archive.ArchiveJobInfo;
import com.ca.arcflash.webservice.data.archive.ArchiveJobScript;
import com.ca.arcflash.webservice.data.archive.CloudProviderInfo;
import com.ca.arcflash.webservice.data.archive.JArchiveJob;
import com.ca.arcflash.webservice.data.backup.Account;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.backup.BackupVolumes;
import com.ca.arcflash.webservice.data.browse.FileFolderItem;
import com.ca.arcflash.webservice.data.browse.Volume;
import com.ca.arcflash.webservice.data.catalog.GRTBrowsingContext;
import com.ca.arcflash.webservice.data.catalog.GRTCatalogItem;
import com.ca.arcflash.webservice.data.catalog.PagedGRTCatalogItem;
import com.ca.arcflash.webservice.data.catalog.SearchContext;
import com.ca.arcflash.webservice.data.merge.MergeJobMonitor;
import com.ca.arcflash.webservice.data.remotedeploy.RemoteDeployTarget;
import com.ca.arcflash.webservice.data.restore.AlternativePath;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.license.MachineInfo;
import com.ca.arcflash.webservice.edge.pfc.ID2DPFCService;
import com.ca.arcflash.webservice.jni.WSJNI.RHAScenarioStatusValue;
import com.ca.arcflash.webservice.jni.model.ArchiveJobMinitor;
import com.ca.arcflash.webservice.jni.model.ArchiveSession;
import com.ca.arcflash.webservice.jni.model.CatalogJobContext;
import com.ca.arcflash.webservice.jni.model.IRemoteDeployCallback;
import com.ca.arcflash.webservice.jni.model.JApplicationStatus;
import com.ca.arcflash.webservice.jni.model.JApplicationWriter;
import com.ca.arcflash.webservice.jni.model.JArchiveCatalogDetail;
import com.ca.arcflash.webservice.jni.model.JBackupDestinationInfo;
import com.ca.arcflash.webservice.jni.model.JBackupInfo;
import com.ca.arcflash.webservice.jni.model.JBackupInfoSummary;
import com.ca.arcflash.webservice.jni.model.JBackupVM;
import com.ca.arcflash.webservice.jni.model.JBackupVMOriginalInfo;
import com.ca.arcflash.webservice.jni.model.JCatalogDetail;
import com.ca.arcflash.webservice.jni.model.JCatalogInfo;
import com.ca.arcflash.webservice.jni.model.JCatalogJob;
import com.ca.arcflash.webservice.jni.model.JDisk;
import com.ca.arcflash.webservice.jni.model.JExchangeDiscoveryItem;
import com.ca.arcflash.webservice.jni.model.JFileInfo;
import com.ca.arcflash.webservice.jni.model.JGRTItem;
import com.ca.arcflash.webservice.jni.model.JHostNetworkConfig;
import com.ca.arcflash.webservice.jni.model.JIVMJobStatus;
import com.ca.arcflash.webservice.jni.model.JJobMonitor;
import com.ca.arcflash.webservice.jni.model.JJobScript;
import com.ca.arcflash.webservice.jni.model.JJobScriptBackupVol;
import com.ca.arcflash.webservice.jni.model.JJobScriptNode;
import com.ca.arcflash.webservice.jni.model.JJobScriptRestoreVolApp;
import com.ca.arcflash.webservice.jni.model.JMachineDetail;
import com.ca.arcflash.webservice.jni.model.JMountPoint;
import com.ca.arcflash.webservice.jni.model.JMountedRecoveryPointItem;
import com.ca.arcflash.webservice.jni.model.JMsgRec;
import com.ca.arcflash.webservice.jni.model.JObjRet;
import com.ca.arcflash.webservice.jni.model.JPFCVMInfo;
import com.ca.arcflash.webservice.jni.model.JPagedCatalogItem;
import com.ca.arcflash.webservice.jni.model.JRWLong;
import com.ca.arcflash.webservice.jni.model.JSearchResult;
import com.ca.arcflash.webservice.jni.model.JVAppChildBackupVMRestorePointWrapper;
import com.ca.arcflash.webservice.jni.model.JWriterInfo;
import com.ca.arcflash.webservice.jni.model.JWindowsServiceModel;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.BackupServiceErrorCode;
import com.ca.arcflash.webservice.service.BrowserService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.service.RegConstants;
import com.ca.arcflash.webservice.service.RestoreService;
import com.ca.arcflash.webservice.service.ServiceContext;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.VSphereService;
import com.ca.arcflash.webservice.service.internal.BackupConverterUtil;
import com.ca.arcflash.webservice.service.internal.RemoteFolderConnCache;
import com.ca.arcflash.webservice.util.ApacheServiceUtil;
import com.ca.arcflash.webservice.util.WebServiceMessages;
import com.ca.ha.webservice.jni.HyperVException;
import com.ca.ha.webservice.jni.HyperVJNI;
import com.ca.ha.webservice.jni.JHyperVSystemInfo;
import com.ca.ha.webservice.jni.VMWareJNI;
import com.ca.arcflash.webservice.data.VMwareConnParams;

public class NativeFacadeImpl implements NativeFacade {
	
	private final int ERROR_NETWORK_UNREACHABLE = 0x000004CF;
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger
			.getLogger(NativeFacadeImpl.class);
	private static final int VOL_GUID_START_OFFSET = 48;

	public NativeFacadeImpl() {		
	}

	@Override
	public String browseVSSApplications() {
		return WSJNI.browseAppInforamtion();
	}

	@Override
	public FileFolderItem getFileFolderItem(String path, String userName,
			String pwd) throws ServiceException {
		FileFolderItem browseFileFolderItem;

		Lock lock = RemoteFolderConnCache.getInstance().getLockByPath(path);
		try {
			if(lock != null) {
				logger.debug("Getting lock to " + path + ", userName:" + userName);
				lock.lock();
				logger.debug("Locked to " + path);
			}

//			browseFileFolderItem = WSJNI.browseFileFolderItem(path, userName, pwd);
			int max=200;
			browseFileFolderItem = WSJNI.browseFileFolderItemEx(path, userName, pwd,max);
			if(browseFileFolderItem.getFolders().length==max){
				browseFileFolderItem.getFolders()[max-1].setName(WebServiceMessages.getResource("MaxFolderDescription"));
				browseFileFolderItem.getFolders()[max-1].setPath("");
				logger.debug(WebServiceMessages.getResource("MaxFolderDescription"));
			}
			if(browseFileFolderItem.getFiles().length==max){
				browseFileFolderItem.getFiles()[max-1].setName(WebServiceMessages.getResource("MaxFileDescription"));
				browseFileFolderItem.getFiles()[max-1].setPath("");
				logger.debug(WebServiceMessages.getResource("MaxFileDescription"));
			}
			

		}finally {
			//move to finally to make sure the connection is added for later disconnect
			//even if exception from browseFileFolderItem
			RemoteFolderConnCache.getInstance().addConnections(path, "", userName, pwd, true);
			if(lock != null) {
				lock.unlock();
				logger.debug("release lock");
			}
		}

		return browseFileFolderItem;
	}

	@Override
	public int validateUser(String username, String password, String domain) {
		logger.debug("validateUser(String, String, String) - start");
		//logger.debug("username:" + username);
		//logger.debug("domain:" + domain);

		int returnint = WSJNI.validate(username, domain, password);

		logger.debug("validateUser(String, String, String) - end:" + returnint);
		return returnint;
	}
	
	@Override
	public int[] getLastArchiveToTapeSession(String backupDest, String userName, String password){ 
		if (logger.isDebugEnabled()) {			
			logger.debug("getLastArchiveToTapeSession() - start.backupDest=" + backupDest + 
					", userName=" + userName + ", password=" + password);	
		}

		int[] sessionId = WSJNI.getLastArchiveToTapeSession(backupDest, userName, password);
		if (null == sessionId) {
			logger.error("getLastArchiveToTapeSession() return value is null");
		}
		else{
			if (logger.isDebugEnabled()) {
				String sSessionIdString = "";
				for (int i = 0; i < sessionId.length; i++) {
					sSessionIdString += sessionId[i] + " ";
				}
				logger.debug("getLastArchiveToTapeSession() - end:" + sSessionIdString);				
			}
		}

		return sessionId;
	}

	@Override
	public String getVolumes(boolean details, String destPath, String userName, String passwd) throws Throwable {
		//Zouyu said that the pass of the backup destination is 
		//just to check whether the volumn is the backup destination, 
		//we don't need to create connection 
		logger.debug("get volume start " + System.currentTimeMillis());
		String volumeXmlStr = WSJNI.BrowseVolumeInforamtion(details, destPath);
		logger.debug("volume xml string:" + volumeXmlStr);
		logger.debug("getVolumes(boolean, String, String, String) - end");
		return volumeXmlStr;
	}

	private void handleCatalogError(long handle, String filepath){
		if(handle == 0){
			throw new RuntimeException("Failed to open catalog for path: "
					+ filepath);
		}
	}

	@Override
	public List<JCatalogDetail> getCatalogItems(String filepath, long parentID) {
		logger.debug("getCatalogItems(String, long) - start");
		logger.debug("filepath:" + filepath);
		logger.debug("parentID:" + parentID);

		if (filepath == null) {
			filepath = "";
		}

		List<JCatalogDetail> returnList = WSJNI.browseCatalogChildren(filepath,
				parentID);

		if (returnList != null && returnList.size() > 0) {
			long handle = WSJNI.OpenCatalog(filepath);
			handleCatalogError(handle, filepath);
			String path = WSJNI.GetFullPath(handle, returnList.get(0)
					.getPathID());

			if (!StringUtil.isEmptyOrNull(path)) {
				try {
					path = path.substring(path.indexOf("\\", 3) + 1, path
							.length());
				} catch (Exception e) {
					logger.error(e);
				}
			}

			for (JCatalogDetail detail : returnList) {
				detail.setPath(path);
				JRWLong childrenCnt = new JRWLong();
				long ret = WSJNI.GetChildrenCount(handle, detail
						.getLongNameID(), childrenCnt);
				logger.debug("ret:" + ret);
				logger.debug("childrenCnt:" + childrenCnt.getValue());
				if (ret == 0) {
					detail.setChildrenCount(childrenCnt.getValue());
				}
			}
			WSJNI.CloseGetCatalog(handle);
		}

		if (logger.isDebugEnabled())
			logger.debug(StringUtil.convertList2String(returnList));
		logger.debug("getCatalogItems(String, long) - end");
		return returnList;
	}
	@Override
	public List<JCatalogDetail> getFileItems(String mountVolGUID, String volumeGUID, String parentID) {
		logger.debug("getCatalogItems(String, long) - start");
			int Length = volumeGUID.length();
			if(parentID != null)
			{
				parentID = mountVolGUID + parentID.substring(Length);
			}
			List<JCatalogDetail> returnList = WSJNI.browseFileChildren(mountVolGUID, parentID);
	
			if (returnList != null && returnList.size() > 0) {
	
				for (JCatalogDetail detail : returnList) {
					//detail.setPath(path);
					JRWLong childrenCnt = new JRWLong();
					long ret = WSJNI.GetFileChildrenCount(mountVolGUID, detail.getLongName(), childrenCnt);
					logger.debug("ret:" + ret);
					logger.debug("childrenCnt:" + childrenCnt.getValue());
					if (ret == 0) {
						detail.setChildrenCount(childrenCnt.getValue());
					}
					detail.setLongName(volumeGUID + detail.getLongName().substring(Length));
				}
			}
	
			if (logger.isDebugEnabled())
				logger.debug(StringUtil.convertList2String(returnList));
			logger.debug("getCatalogItems(String, long) - end");
			//WSJNI.UnMountVolume(jmntPoint.getDiskSignature());
			return returnList;
	}
	
	@Override
	public JMountPoint MountVolume(String userName, String passWord, String backupDest, 
			long sessionID, String volumeGUID, String encryptionKey) throws ServiceException {
		logger.debug("MountVolume(String, long) - start, userName " + userName 
				+ " backupDest " + backupDest + " sessionID " + sessionID + " encryptionKey "
				+ encryptionKey);
		logger.debug("backupDest:" + backupDest);
		JMountPoint jmntPoint = new JMountPoint();
		Lock lock = null;
		try {
			lock = RemoteFolderConnCache.getInstance().getLockByPath(backupDest);
			if(lock != null) {
				lock.lock();
			}
			long retVal = WSJNI.MountVolume(userName, passWord, backupDest, sessionID, 
					volumeGUID, jmntPoint, encryptionKey);
			
			if(retVal == 0)
			{
				return jmntPoint;
			}
			else
			{	
				logger.error("Failed to mount the volume. The return value is: " + retVal);
				throw new ServiceException(getErrorMsg(retVal), FlashServiceErrorCode.Common_MountVolume_Failure);
			}
		}finally {
			if(lock != null) {
				lock.unlock();
			}
		}
	}
	
	@Override
	public int UnMountVolume(JMountPoint mntPoint) {
		
		return WSJNI.UnMountVolume(mntPoint.getDiskSignature(), mntPoint.getMountHandle());
	}
	
	/*
	 * zxh,add VDDismountResBrsVols
	 */
	@Override
	public long VDDismountResBrsVols(boolean bForceDisMntAllResBrw) {
		return WSJNI.VDDismountResBrsVols(bForceDisMntAllResBrw);
	}

	/*
	 * zxh,add VDUpateVolumeExtData
	 */
	@Override
	public long VDUpateVolumeMountTimestamp(String sVolumeSignatureID) {
		return WSJNI.VDUpateVolumeMountTimestamp(sVolumeSignatureID);
	}

	@Override
	public JPagedCatalogItem getPagedFileItems(String mountVolGUID, String catPath,
			String parentID, int start, int size) throws ServiceException {
		logger.debug("getPagedCatalogItems(String, long, int, int) - enter");
		logger.debug(catPath);
		logger.debug(parentID);
		logger.debug(start);
		logger.debug(size);

		JPagedCatalogItem pagedItem = new JPagedCatalogItem();
		JRWLong totalCnt = new JRWLong();
		String volumeGUID = null;
		int Length = 0;
		if(catPath != null)
		{
			volumeGUID = catPath.substring(catPath.length()-VOL_GUID_START_OFFSET, catPath.length());
			Length =  volumeGUID.length();
		}
		long ret = WSJNI.GetFileChildrenCount(mountVolGUID, parentID, totalCnt);

		logger.debug("ret:" + ret);
		if (ret == 0) {
			long total = totalCnt.getValue();
			logger.debug("total:" + total);

			pagedItem.setTotal(total);

			if (total > 0) {
				JRWLong realCnt = new JRWLong();
				ArrayList<JCatalogDetail> retArr = new ArrayList<JCatalogDetail>();
				long dwRet = WSJNI.GetFileChildrenEx(mountVolGUID, parentID, start, size,
						realCnt, retArr);
				logger.debug("dwRet:" + dwRet);

				ArrayList<JCatalogDetail> clippedRetArr = new ArrayList<JCatalogDetail>();
				if (dwRet == 0) {

					if (retArr != null && retArr.size() > 0) {
						int curr = start;

						for (JCatalogDetail detail : retArr) {
							if (curr++ >= total) {
								logger.error("curr:" + curr);
								logger.error("retArr.size:" + retArr.size());
								logger.error("total:" + total);
								logger.error("start:" + start);
								logger.error("size:" + size);
								break;
							}

							detail.setFileDate(BackupConverterUtil
									.dosTime2UTC(detail.getFileDate()));
							JRWLong childrenCnt = new JRWLong();
							long retGetChildrenCount = WSJNI.GetFileChildrenCount(mountVolGUID, detail.getLongName(), childrenCnt);
							logger.debug("GetChildrenCount ret:"
									+ retGetChildrenCount);
							logger.debug("childrenCnt:"
									+ childrenCnt.getValue());
							if (ret == 0) {
								detail.setChildrenCount(childrenCnt.getValue());
							}
							detail.setLongName(volumeGUID + detail.getLongName().substring(Length));
							clippedRetArr.add(detail);
						}
					}

					pagedItem.setDetails(clippedRetArr);

					if (logger.isDebugEnabled())
						logger.debug(StringUtil
								.convertList2String(clippedRetArr));
					logger
							.debug("getPagedCatalogItems(String, long, int, int) - end");
				}
			}
		}
		return pagedItem;
	}

	
	protected SOAPFaultException convertServiceException2AxisFault(
			ServiceException serviceException) {
		logger.error(serviceException.getErrorCode());
		logger.error(serviceException.getMessage(), serviceException);
		return AxisFault.fromAxisFault(serviceException.getMessage(), serviceException
				.getErrorCode());
	}

	@Override
	public long GetChildCount(long sessNum, String catalogPath,
			String volumeGUID, String destination, String userName, String passWord, String encryptedPwd) throws ServiceException {
		JRWLong childrenCnt = new JRWLong();
		long ret = 0;
		boolean catalogExist = RestoreService.getInstance().useCatalog(
				destination, userName, passWord, sessNum, catalogPath);
				
		if(!catalogExist)
		{	
			JMountPoint mp = null;
			try {
				mp = BrowserService.getInstance().MountVolumeEx(destination, userName, passWord, 
						volumeGUID, sessNum, catalogPath, encryptedPwd);
			
				if(mp != null)
				{
					ret = WSJNI.GetFileChildrenCount(mp.getMountID(),null,childrenCnt);
					logger.debug("ret:" + ret);
					logger.debug("childrenCnt:" + childrenCnt.getValue());
					BrowserService.getInstance().UnMountVolume(mp,false,false);
				}
				else
				{
					logger.debug("Failed to mount the volume.");
				}
			}catch (ServiceException e) {
				throw e;
			} catch (Throwable e) {
				logger.error(e.getMessage(), e);
				throw AxisFault.fromAxisFault("Unhandled exception in web service",
						FlashServiceErrorCode.Common_ErrorOccursInService);
			}
			
		}else {
			long handle = WSJNI.OpenCatalog(catalogPath);
			if(handle == 0){
				logger.warn("Open catalog with path " + catalogPath
						+ " failed");
				return 0;
			}
			logger.debug("handle:" + handle);
			long parentID = -1;
			ret = WSJNI.GetChildrenCount(handle, parentID,
					childrenCnt);
			logger.debug("ret:" + ret);
			logger.debug("childrenCnt:" + childrenCnt.getValue());
			WSJNI.CloseGetCatalog(handle);
		}
		if (ret == 0) {
			return childrenCnt.getValue();
		}
		else
		{
			return 0;
		}
	}
	
	public long InitGRTMounter()
	{
		return WSJNI.InitGRTMounter();
	}
	
	public int ExitGRTMounter(long handle)
	{
		return WSJNI.ExitGRTMounter(handle);
	}

	public long SearchMountPoint(String mntVolumeGUID, String backupDestination, String volDisplayName, String sDir, boolean bCaseSensitive, boolean bIncludeSubDir, String pattern)
	{
		logger.debug("SearchMountPoint(String, String, boolean, boolean, String) - start");
		if (logger.isDebugEnabled()){
			logger.debug("volGUID:"+mntVolumeGUID);
			logger.debug("volDisplayName:"+ volDisplayName);
			logger.debug("searchFolder:"+sDir);
			logger.debug("caseSensitive:"+bCaseSensitive);
			logger.debug("includeSubDir:"+bIncludeSubDir);
			logger.debug("pattern:"+pattern);
		}

		long returnContext = WSJNI.SearchMountPoint(mntVolumeGUID, backupDestination, volDisplayName, sDir, bCaseSensitive, bIncludeSubDir, pattern);
		
		logger.debug("openSearchCatalog(String, String, boolean, boolean, String) - end");
		return returnContext;
	}
	
	public int FindNextSearchItems(long jhandle, long nRequest, ArrayList<JCatalogDetail> retArr, JRWLong nFound)
	{
		return 0;
		
	}
	
	public JSearchResult FindNextSearchItems(SearchContext context) {
		logger.debug("FindNextSearchItems(SearchContext) - start");
		if (logger.isDebugEnabled())
			logger.debug(StringUtil.convertObject2String(context));

		JSearchResult returnJSearchResult = WSJNI.GetNextSearchItems(context);

		if (logger.isDebugEnabled()) {
			logger.debug(StringUtil.convertObject2String(returnJSearchResult));
			if (returnJSearchResult != null)
				logger.debug(StringUtil.convertList2String(returnJSearchResult
						.getMsgDetail()));
		}
		logger.debug("FindNextSearchItems(FindNextSearchItems) - end");
		return returnJSearchResult;

	}
	public int FindCloseSearchItems(SearchContext context)
	{
		logger.debug("FindNextSearchItems(SearchContext) - start");
		if (logger.isDebugEnabled())
			logger.debug(StringUtil.convertObject2String(context));

		int ret = WSJNI.FindCloseSearchItems(context.getContextID());

		logger.debug("FindNextSearchItems(FindNextSearchItems) - end");
		return ret;
		
	}
	
	//pidma02: Support for search in single session for catalog enabled sessions
	
	@Override
	public SearchContext openSearchCatalogEx(String catalogSessionPath, String searchFolder, boolean caseSensitive,
			boolean includeSubDir, String pattern) {
		logger.debug("openSearchCatalog(String, String, boolean, boolean, String) - start");
		if (logger.isDebugEnabled()){
			logger.debug("destination:"+catalogSessionPath);
			logger.debug("searchFolder:"+searchFolder);
			logger.debug("caseSensitive:"+caseSensitive);
			logger.debug("includeSubDir:"+includeSubDir);
			logger.debug("pattern:"+pattern);
		}

		SearchContext returnSearchContext = WSJNI.SearchCatalogEx(catalogSessionPath, searchFolder, caseSensitive, includeSubDir, pattern);

		if (logger.isDebugEnabled())
			logger.debug(StringUtil.convertObject2String(returnSearchContext));
		logger.debug("openSearchCatalog(String, String, boolean, boolean, String) - end");
		return returnSearchContext;
	}

	@Override
	public JSearchResult searchNextEx(SearchContext context) {
		logger.debug("searchNext(SearchContext) - start");
		if (logger.isDebugEnabled())
			logger.debug(StringUtil.convertObject2String(context));

		JSearchResult returnJSearchResult = WSJNI.FindNextCatalogEx(context);

		if (logger.isDebugEnabled()){
			logger.debug(StringUtil.convertObject2String(returnJSearchResult));
			if (returnJSearchResult!=null)
			logger.debug(StringUtil.convertList2String(returnJSearchResult.getDetail()));
		}
		logger.debug("searchNext(SearchContext) - end");
		return returnJSearchResult;
	}
	
	@Override
	public int GetCatalogStatusForSession(String sessionPath)
	{
		return WSJNI.GetCatalogStatus(sessionPath);
	}
	
	@Override
	public int SetCatalogStatusForSession(String backupDest, String sessionPath)
	{
		return WSJNI.SetCatalogStatus(backupDest, sessionPath);
	}
	
	@Override
	public int UpdateCatalogJobScript(String backupDest, String userName, String passWord, long sessNum, String jobScript, String vmInstanceUUID)
	{
		return WSJNI.UpdateCatalogJobScript(backupDest, userName, passWord, sessNum, jobScript, vmInstanceUUID);
	}	

	@Override
	public int updateSessionPasswordByGUID(String[] guids, String[] pwds) {
		int ret = WSJNI.updateSessionPasswordByGUID(guids, pwds);
		if(ret != 0)
			logger.error("update session password error: " + ret);
		return ret;
	}
	@Override
	public void closeSearchCatalog(SearchContext context) {
		logger.debug("closeSearchCatalog(SearchContext) - start");
		if (logger.isDebugEnabled())
			logger.debug(StringUtil.convertObject2String(context));

		WSJNI.CloseSearchCatalog(context.getContextID());

		logger.debug("closeSearchCatalog(SearchContext) - end");
	}

	@Override
	public SearchContext openSearchCatalog(String destination, String searchFolder, boolean caseSensitive,
			boolean includeSubDir, String pattern) {
		logger.debug("openSearchCatalog(String, String, boolean, boolean, String) - start");
		if (logger.isDebugEnabled()){
			logger.debug("destination:"+destination);
			logger.debug("searchFolder:"+searchFolder);
			logger.debug("caseSensitive:"+caseSensitive);
			logger.debug("includeSubDir:"+includeSubDir);
			logger.debug("pattern:"+pattern);
		}

		SearchContext returnSearchContext = WSJNI.SearchCatalog(destination, searchFolder, caseSensitive, includeSubDir, pattern);

		if (logger.isDebugEnabled())
			logger.debug(StringUtil.convertObject2String(returnSearchContext));
		logger.debug("openSearchCatalog(String, String, boolean, boolean, String) - end");
		return returnSearchContext;
	}

	@Override
	public JSearchResult searchNext(SearchContext context) {
		logger.debug("searchNext(SearchContext) - start");
		if (logger.isDebugEnabled())
			logger.debug(StringUtil.convertObject2String(context));

		JSearchResult returnJSearchResult = WSJNI.FindNextCatalog(context);

		if (logger.isDebugEnabled()){
			logger.debug(StringUtil.convertObject2String(returnJSearchResult));
			if (returnJSearchResult!=null)
			logger.debug(StringUtil.convertList2String(returnJSearchResult.getDetail()));
		}
		logger.debug("searchNext(SearchContext) - end");
		return returnJSearchResult;
	}

	@Override
	public long backup(JJobScript job) {
		logger.debug("backup(JJobScript) - start");
		debugJJobScript(job);

		long returnlong = WSJNI.AFBackup(job);

		logger.debug("backup(JJobScript) - end");
		return returnlong;
	}

	//@Override
	public long getD2DFileSize(String path, String user, String pwd,
			String d2dFile, int startSessNo, int endSessNo, int nBackupDescType) {
		long size = 0;
		logger.debug("getD2DFileSize - start");

		size = WSJNI.getD2DFileSize(path, user, pwd, d2dFile, startSessNo, endSessNo, nBackupDescType);

		logger.debug("getD2DFileSize - end");
		return size;
	}

	@Override
	public JRestorePoint[] getRestorePoints(String destination, String domain, String userName, String pwd, Date beginDate, Date endDate, boolean isQueryDetail) {
		logger.debug("getRestorePoints(String, Date, Date) - start");
		logger.debug("destination:" + destination);
		logger.debug("beginDate:" + beginDate);
		logger.debug("endDate:" + endDate);
		logger.debug("isQueryDetail:" + isQueryDetail);

		List<JRestorePoint> list = new ArrayList<JRestorePoint>();
		String beginDateString = BackupConverterUtil.dateToUTCString(beginDate);
		String endDateString = BackupConverterUtil.dateToUTCString(endDate);
		logger.debug("transformed beginDate:" + beginDateString);
		logger.debug("trsformed endDate:" + endDateString);

		int jniResult = -1;
		JRestorePoint[] retJResPntArr;
		Lock lock = RemoteFolderConnCache.getInstance().getLockByPath(destination);
		try {
			if(lock != null) {
				logger.debug("lock to " + destination + ", domain:" + domain + ", userName:" + userName);
				lock.lock();
			}
			/*Only for test
			 * if(System.getProperty("WebServiceLocalTest") != null) {
				jniResult = WSJNIForTest.GetRecoveryPoint(destination, domain, userName,
						pwd, beginDateString, endDateString, list, isQueryDetail);
			}else */
			
			jniResult = WSJNI.GetRecoveryPoint(destination, domain, userName,
					pwd, beginDateString, endDateString, list, isQueryDetail);

			RemoteFolderConnCache.getInstance().addConnections(destination,domain, userName, pwd, false);

			retJResPntArr = list.toArray(new JRestorePoint[0]);

			if (retJResPntArr != null && retJResPntArr.length > 0) {
				for (JRestorePoint resPnt : retJResPntArr) {
					List<JBackupItem> bkpItemLst = resPnt.getItems();
					if (bkpItemLst != null && bkpItemLst.size() > 0) {
						for (JBackupItem bkpItem : bkpItemLst) {
							String catPath = bkpItem.getCatalogFilePath();
							long handle = WSJNI.OpenCatalog(catPath);
							if(handle == 0){
								logger.warn("Failed to open catalog for path: " + catPath);
								continue;
							}
							logger.debug("handle:" + handle);
							JRWLong childrenCnt = new JRWLong();
							long parentID = -1;
							long ret = WSJNI.GetChildrenCount(handle, parentID,
									childrenCnt);
							logger.debug("ret:" + ret);
							logger.debug("childrenCnt:" + childrenCnt.getValue());
							WSJNI.CloseGetCatalog(handle);
							if (ret == 0) {
								bkpItem.setChildrenCount(childrenCnt.getValue());
							}
						}
					}
				}
			}
		}finally {
			if(lock != null) {
				lock.unlock();
				logger.debug("release lock");
			}
		}

		if (logger.isDebugEnabled())
			logger.debug(StringUtil.convertList2String(list));
		logger.debug("getRestorePoints(String, Date, Date) - end:" + jniResult);
		return retJResPntArr;
	}
	
	//zxh,mark:ASBUGetRecoveryPoint
	@Override
	public JRestorePoint[] getRestorePoints4ASBU(String destination, String domain, String userName, String pwd, Date beginDate, Date endDate, boolean isQueryDetail) {
		logger.debug("getRestorePoints(String, Date, Date) - start");
		logger.debug("destination:" + destination);
		logger.debug("beginDate:" + beginDate);
		logger.debug("endDate:" + endDate);
		logger.debug("isQueryDetail:" + isQueryDetail);

		List<JRestorePoint> list = new ArrayList<JRestorePoint>();
		String beginDateString = BackupConverterUtil.dateToUTCString(beginDate);
		String endDateString = BackupConverterUtil.dateToUTCString(endDate);
		logger.debug("transformed beginDate:" + beginDateString);
		logger.debug("trsformed endDate:" + endDateString);

		int jniResult = -1;
		JRestorePoint[] retJResPntArr;
		/*Only for test
		 * if(System.getProperty("WebServiceLocalTest") != null) {
			jniResult = WSJNIForTest.GetRecoveryPoint(destination, domain, userName,
					pwd, beginDateString, endDateString, list, isQueryDetail);
		}else */
		
		if (FlashSwitch.isSiwtchEnableFromReg_Cache(FlashSwitchDefine.USEGetRecoveryPointModule.SWT_USEGETRECOVERYPOINT_FLAG, 
				FlashSwitchDefine.USEGetRecoveryPointModule.SWT_USEGETRECOVERYPOINT_PATH, "1", false)) {
			logger.debug("use old JNI");
			
			jniResult = WSJNI.GetRecoveryPoint(destination, domain, userName,
					pwd, beginDateString, endDateString, list, isQueryDetail);
		}
		else{	
			logger.debug("use new JNI");
			try {
				jniResult = WSJNI.ASBUGetRecoveryPoint(destination, beginDateString, endDateString, list, isQueryDetail);
			} catch (Throwable e) {
				logger.error(e);
			}
		}

		RemoteFolderConnCache.getInstance().addConnections(destination,domain, userName, pwd, false);

		retJResPntArr = list.toArray(new JRestorePoint[0]);

		if (retJResPntArr != null && retJResPntArr.length > 0) {
			for (JRestorePoint resPnt : retJResPntArr) {
				List<JBackupItem> bkpItemLst = resPnt.getItems();
				if (bkpItemLst != null && bkpItemLst.size() > 0) {
					for (JBackupItem bkpItem : bkpItemLst) {
						String catPath = bkpItem.getCatalogFilePath();
						long handle = WSJNI.OpenCatalog(catPath);
						if(handle == 0){
							logger.warn("Failed to open catalog for path: " + catPath);
							continue;
						}
						logger.debug("handle:" + handle);
						JRWLong childrenCnt = new JRWLong();
						long parentID = -1;
						long ret = WSJNI.GetChildrenCount(handle, parentID,
								childrenCnt);
						logger.debug("ret:" + ret);
						logger.debug("childrenCnt:" + childrenCnt.getValue());
						WSJNI.CloseGetCatalog(handle);
						if (ret == 0) {
							bkpItem.setChildrenCount(childrenCnt.getValue());
						}
					}
				}
			}
		}
		
		if (logger.isDebugEnabled())
			logger.debug(StringUtil.convertList2String(list));
		logger.debug("getRestorePoints(String, Date, Date) - end:" + jniResult);
		return retJResPntArr;
	}


	@Override
	public long restore(JJobScript job) {
		logger.debug("restore(JJobScript) - start");
		debugJJobScript(job);

		long returnlong = WSJNI.AFRestore(job);

		logger.debug("restore(JJobScript) - end");
		return returnlong;
	}

	@Override
	public JBackupInfoSummary GetBackupInfoSummary(String destination, String domain, String userName, String pwd, boolean onlyDestCapacity) throws Throwable {
		logger.debug("GetBackupInfoSummary(String) - start");
		//logger.debug("destination:"+destination);
		//logger.debug("domain:"+domain);
		//logger.debug("userName:"+userName);
		logger.debug("onlyDestCapacity:"+onlyDestCapacity);

		JBackupInfoSummary result = new JBackupInfoSummary();
		result.setBackupInfoList(new ArrayList<JBackupInfo>());
		result.setDestinationInfo(new JBackupDestinationInfo());
		Lock lock = RemoteFolderConnCache.getInstance().getLockByPath(destination);
		int jniResult;
		try {
			if(lock != null) {
				logger.debug("Getting lock ");
				lock.lock();
				logger.debug("locked ");
			}
			jniResult = WSJNI.GetBackupInfoSummary(destination, domain, userName, pwd, result, onlyDestCapacity,null);
			RemoteFolderConnCache.getInstance().addConnections(destination,domain, userName, pwd, false);
		}
		finally {
			if(lock != null) {
				lock.unlock();
				logger.debug("release lock");
			}
		}


		result.setErrorCode(jniResult);

		if (logger.isDebugEnabled()){
			logger.debug(StringUtil.convertObject2String(result));
			logger.debug(StringUtil.convertList2String(result.getBackupInfoList()));
		}
		logger.debug("GetBackupInfoSummary(String) - end:"+jniResult);
		return result;
	}

	@Override
	public JBackupDestinationInfo GetDestSizeInformation(String destination,String domain,
														 String userName, String pwd) throws Throwable{

		logger.debug("GetDestSizeInformation---- start");
		logger.debug("destination:"+destination);
		logger.debug("domain:"+domain);
		logger.debug("userName:"+userName);

		JNetConnInfo connInfo = new JNetConnInfo();
		connInfo.setSzDir(destination);
		connInfo.setSzDomain(domain);
		connInfo.setSzUsr(userName);
		connInfo.setSzPwd(pwd);

		JBackupDestinationInfo result = new JBackupDestinationInfo();

		long ret;
		Lock lock = RemoteFolderConnCache.getInstance().getLockByPath(destination);
		try {
			if(lock != null) {
				logger.debug("Getting lock to " + destination + ", domain:" + domain + ", userName:" + userName);
				lock.lock();
				logger.debug("locked to " + destination);
			}
			ret = WSJNI.AFCreateConnection(connInfo);
			if(ret != 0){
				logger.debug("AFCreateConnection---return value---" + ret);
				return result;
			}

			ret = WSJNI.GetDestSizeInformation(destination, result);
			logger.debug("GetDestSizeInformation---return value---" + ret);

		}finally{
			if(lock != null)
				lock.unlock();
			ret = WSJNI.AFCutConnection(connInfo, false);
			logger.debug("AFCutConnection---return value---" + ret);
		}
		logger.debug("GetDestSizeInformation---- end");
		return result;
	}

	@Override
	public JProtectionInfo[] GetProtectionInformation(String destination, String domain, String userName, String pwd) throws Throwable {
		logger.debug("GetProtectionInformation(String) - start");
		logger.debug("destination:"+destination);

		List<JProtectionInfo> protectionList = new ArrayList<JProtectionInfo>();
		int result;
		Lock lock = RemoteFolderConnCache.getInstance().getLockByPath(destination);
		try {
			if(lock != null) {
				logger.debug("Getting lock to " + destination + ", domain:" + domain + ", userName:" + userName);
				lock.lock();
				logger.debug("locked to " + destination);
			}
			result = WSJNI.GetProtectionInformation(destination, domain, userName, pwd, protectionList);
			RemoteFolderConnCache.getInstance().addConnections(destination, domain, userName, pwd, false);
		}finally {
			if(lock != null) {
				lock.unlock();
				logger.debug("release lock");
			}
		}

		JProtectionInfo[] returnJProtectionInfoArray = protectionList.toArray(new JProtectionInfo[0]);

		if (logger.isDebugEnabled()){
			logger.debug(StringUtil.convertList2String(protectionList));
		}
		logger.debug("GetProtectionInformation(String) - end:"+result);
		return returnJProtectionInfoArray;
	}
	
	@Override
	public JProtectionInfo[] GetCopyProtectionInformation(String destination, String domain, String userName, String pwd) throws Throwable {
		logger.debug("GetProtectionInformation(String) - start");
		logger.debug("destination:"+destination);
		
		List<JProtectionInfo> protectionList = new ArrayList<JProtectionInfo>();
		int result;
		Lock lock = RemoteFolderConnCache.getInstance().getLockByPath(destination);
		try {
			if(lock != null) {
				logger.debug("Getting lock to " + destination + ", domain:" + domain + ", userName:" + userName);
				lock.lock();
				logger.debug("locked to " + destination);
			}
			result = WSJNI.GetCopyProtectionInformation(destination, domain, userName, pwd, protectionList);
			RemoteFolderConnCache.getInstance().addConnections(destination, domain, userName, pwd, false);
		}finally {
			if(lock != null) {
				lock.unlock();
				logger.debug("release lock");
			}
		}
		
		JProtectionInfo[] returnJProtectionInfoArray = protectionList.toArray(new JProtectionInfo[0]);
		
		if (logger.isDebugEnabled()){
			logger.debug(StringUtil.convertList2String(protectionList));
		}
		logger.debug("GetProtectionInformation(String) - end:"+result);
		return returnJProtectionInfoArray;
	}

	@Override
	public JBackupInfo[] getMostRecentRecoveryPoints(String destination, String domain, String userName, String pwd,
			int backupType, int backupStatus, int top,String foldername) throws Throwable {
		logger.debug("getMostRecentRecoveryPoints(String, int) - start");
		logger.debug("destination:"+destination);
		logger.debug("top:"+top);

		List<JBackupInfo> backupInfoList = new ArrayList<JBackupInfo>();
		int result;
		Lock lock = RemoteFolderConnCache.getInstance().getLockByPath(destination);
		try {
			if(lock != null) {
				logger.debug("Getting lock to " + destination + ", domain:" + domain + ", userName:" + userName);
				lock.lock();
				logger.debug("locked to "  + destination);
			}
			result = WSJNI.GetRecentBakcupInfo(destination, domain, userName, pwd, backupType, backupStatus, top, backupInfoList,foldername);
			RemoteFolderConnCache.getInstance().addConnections(destination, domain, userName, pwd, false);
		}finally {
			if(lock != null) {
				lock.unlock();
				logger.debug("release lock");
			}
		}

		JBackupInfo[] returnJBackupInfoArray = backupInfoList.toArray(new JBackupInfo[0]);

		if (logger.isDebugEnabled()){
			logger.debug(StringUtil.convertList2String(backupInfoList));
		}
		logger.debug("getMostRecentRecoveryPoints(String, int) - end:"+result);
		return returnJBackupInfoArray;
	}
	
	@Override
	public JBackupInfo[] getRecentBackupsByServerTime(String destination, String domain, String userName, String pwd,
			int backupType, int backupStatus, Date beginDate, Date endDate, String foldername) throws Throwable {
		logger.debug("getRecentBackupsByServerTime(String, int) - start");
		logger.debug("destination:"+destination);

		String beginDateString = BackupConverterUtil.dateToUTCString(beginDate);
		String endDateString = BackupConverterUtil.dateToUTCString(endDate);
		
		List<JBackupInfo> backupInfoList = new ArrayList<JBackupInfo>();
		int result;
		Lock lock = RemoteFolderConnCache.getInstance().getLockByPath(destination);
		try {
			if(lock != null) {
				logger.debug("Getting lock to " + destination + ", domain:" + domain + ", userName:" + userName);
				lock.lock();
				logger.debug("locked to "  + destination);
			}
			result = WSJNI.GetRecentBackupsByServerTime(destination, domain, userName, pwd, backupType, backupStatus, beginDateString, endDateString, backupInfoList,foldername);
			RemoteFolderConnCache.getInstance().addConnections(destination, domain, userName, pwd, false);
		}finally {
			if(lock != null) {
				lock.unlock();
				logger.debug("release lock");
			}
		}

		JBackupInfo[] returnJBackupInfoArray = backupInfoList.toArray(new JBackupInfo[0]);

		if (logger.isDebugEnabled()){
			logger.debug(StringUtil.convertList2String(backupInfoList));
		}
		logger.debug("getRecentBackupsByServerTime(String, int) - end:"+result);
		return returnJBackupInfoArray;
	}
	
	@Override
	public JBackupInfo[] getRecoveryPoints(String destination, String domain, String userName, String pwd, Date beginDate, Date endDate,int top) throws Throwable
	{
		logger.debug("getRecoveryPoints() - start");
		logger.debug("destination:"+destination);
		logger.debug("top:"+top);

		List<JBackupInfo> backupInfoList = new ArrayList<JBackupInfo>();
		int result;
		Lock lock = RemoteFolderConnCache.getInstance().getLockByPath(destination);
		try {
			if(lock != null) {
				logger.debug("Getting lock to " + destination + ", domain:" + domain + ", userName:" + userName);
				lock.lock();
				logger.debug("locked to "  + destination);
			}
			String beginDateString = BackupConverterUtil.dateToUTCString(beginDate);
			String endDateString = BackupConverterUtil.dateToUTCString(endDate);
			result = WSJNI.GetBakcupInfo(destination, domain, userName, pwd, beginDateString,endDateString,top, backupInfoList);
			RemoteFolderConnCache.getInstance().addConnections(destination, domain, userName, pwd, false);
		}finally {
			if(lock != null) {
				lock.unlock();
				logger.debug("release lock");
			}
		}

		JBackupInfo[] returnJBackupInfoArray = backupInfoList.toArray(new JBackupInfo[0]);

		if (logger.isDebugEnabled()){
			logger.debug(StringUtil.convertList2String(backupInfoList));
		}
		logger.debug("getRecoveryPoints() - end:"+result);
		return returnJBackupInfoArray;
	}

	@Override
	public void addLogActivity(long level, long resourceID,	String... parameters) {
		logger.debug("addLogActivity(long, long, String[]) - start");
		if (logger.isDebugEnabled()){
			logger.debug("level:"+level);
			logger.debug("resourceID:"+resourceID);
			//logger.debug("parameters:"+StringUtil.convertArray2String(parameters));
		}
		
		String[] paras = {"","","","",""};
		
		if(parameters != null){
			if(parameters.length > paras.length) 
				paras = parameters;
			else
				for(int i=0;i<parameters.length;i++){
					paras[i] = parameters[i];
				}			
		}

		int result = WSJNI.LogActivity(level, resourceID, paras);

		logger.debug("addLogActivity(long, long, String[]) - end:"+result);
	}
	@Override
	public void addLogActivityWithJobID(long level, long jobID, long resourceID,	String[] parameters) {
		logger.debug("addLogActivityWithJobID(long, long,long, String[]) - start");
		if (logger.isDebugEnabled()){
			logger.debug("level:"+level);
			logger.debug("jobID:"+jobID);
			logger.debug("resourceID:"+resourceID);
			//logger.debug("parameters:"+StringUtil.convertArray2String(parameters));
		}

		int result = WSJNI.LogActivityWithID(level, jobID,resourceID, parameters);

		logger.debug("addLogActivityWithJobID(long, long, long, String[]) - end:"+result);
	}
	@Override
	public void addVMLogActivity(long level, long resourceID,	String[] parameters,String vmIndentification) {
		logger.debug("addVMLogActivity(long, long, String[]) - start");
		if (logger.isDebugEnabled()){
			logger.debug("level:"+level);
			logger.debug("resourceID:"+resourceID);
			//logger.debug("parameters:"+StringUtil.convertArray2String(parameters));
		}
		
		int result = WSJNI.VMLogActivity(level, resourceID, parameters,vmIndentification);
		
		logger.debug("addVMLogActivity(long, long, String[]) - end:"+result);
	}
	@Override
	public void addVMLogActivityWithJobID(long level, long jobID, long resourceID,	String[] parameters,String vmIndentification) {
		logger.debug("addVMLogActivityWithJobID(long, long,long, String[]) - start");
		if (logger.isDebugEnabled()){
			logger.debug("level:"+level);
			logger.debug("jobID:"+jobID);
			logger.debug("resourceID:"+resourceID);
			//logger.debug("parameters:"+StringUtil.convertArray2String(parameters));
		}
		
		int result = WSJNI.VMLogActivityWithID(level, jobID,resourceID, parameters,vmIndentification);
		
		logger.debug("addVMLogActivityWithJobID(long, long, long, String[]) - end:"+result);
	}
	@Override
	public void addLogActivityWithDetailsEx(JActLogDetails logDetails, long resourceID, String[] parameters) {
		logger.debug("LogActivityWithDetailsEx - start");
		if (logger.isDebugEnabled()){
			logger.debug("level:"+logDetails.getLogLevel());
			logger.debug("jobID:"+logDetails.getJobID());
			logger.debug("resourceID:"+resourceID);
			//logger.debug("parameters:"+StringUtil.convertArray2String(parameters));
		}
		
		int result = WSJNI.LogActivityWithDetailsEx(logDetails, resourceID, parameters);
		
		logger.debug("LogActivityWithDetailsEx - end:"+result);
	}
	@Override
	public JActivityLogResult getActivityLogs(int start, int count) {
		logger.debug("getActivityLogs(int, int) - start");
		logger.debug("start:"+start);
		logger.debug("count:"+count);

		JActivityLogResult logResult = new JActivityLogResult();
		List<JActivityLog> logs = new ArrayList<JActivityLog>();
		logResult.setLogs(logs);

		int result = WSJNI.GetLogActivity(start, count, logResult);

		if (logger.isDebugEnabled()){
			logger.debug(StringUtil.convertObject2String(logResult));
			logger.debug(StringUtil.convertList2String(logResult.getLogs()));
		}
		logger.debug("getActivityLogs(int, int) - end:"+result);
		return logResult;
	}

	@Override
	public void deleteActivityLog(int year, int month, int day, int hour, int minute, int second) {
		logger.debug("deleteActivityLog(Date) - start");
		if (logger.isDebugEnabled()){
			logger.debug("year:"+year);
			logger.debug("month:"+month);
			logger.debug("day:"+day);
			logger.debug("hour:"+hour);
			logger.debug("minute:"+minute);
			logger.debug("second:"+second);
		}

		int result = WSJNI.DeleteLogActivity(year, month, day, hour, minute, second);

		logger.debug("deleteActivityLog(Date) - end:"+result);
	}

	private void debugJJobScript(JJobScript script){
		if (logger.isDebugEnabled()){
			logger.debug(StringUtil.convertObject2String(script));
			if (script.getPAFNodeList()!=null){
				for (JJobScriptNode node : script.getPAFNodeList()){
					logger.debug("\t"+StringUtil.convertList2String(node.getPBackupVolumeList()));
					logger.debug("\t"+StringUtil.convertList2String(node.getPRestoreVolumeAppList()));

					if (node.getPBackupVolumeList()!=null){
						for (JJobScriptBackupVol backupVol : node.getPBackupVolumeList()){
							logger.debug("\t\t"+StringUtil.convertList2String(backupVol.getPVolItemAppCompList()));
						}
					}

					if (node.getPRestoreVolumeAppList()!=null){
						for (JJobScriptRestoreVolApp restoreVol : node.getPRestoreVolumeAppList()){
							logger.debug("\t\t"+StringUtil.convertList2String(restoreVol.getPVolItemAppCompList()));
						}
					}
				}
			}
		}
	}

	@Override
	public void StartToDeploy(String localDomain, String localUser,
			String localPassword, String uuid, String serverName,
			String username, String password, int port,
			String installDirectory, boolean autoStartRRService, boolean reboot, boolean bInstallDriver,
			boolean useHttps, boolean resumedAndCheck, IRemoteDeployCallback callback) {
		logger.debug("StartToDeploy - start");
		logger.debug("localDomain: " + localDomain);
		logger.debug("localUser: " + localUser);
		logger.debug("serverName:" + serverName);
		logger.debug("username:" + username);
		logger.debug("port:" + port);
		logger.debug("installDirectory:" + installDirectory);
		logger.debug("autoStartRRService:" + autoStartRRService);
		logger.debug("reboot:" + reboot);
		logger.debug("installDriver:" + bInstallDriver);
		logger.debug("useHttps:" + useHttps);
		logger.debug("resumedAndCheck:" + resumedAndCheck);

		WSJNI.StartToDeployWithDriver(localDomain, localUser, localPassword, uuid,
				serverName, username, password, port, installDirectory, autoStartRRService, reboot, bInstallDriver,
				useHttps, resumedAndCheck, callback);

	}

	@Override
	public String decrypt(String source) {
		return WSJNI.AFDecryptString(source);
	}

	@Override
	public String encrypt(String source) {
		return WSJNI.AFEncryptString(source);
	}

	@Override
	public boolean isCompressionLevelChanged(String destination, String domain,
			String username, String pwd, int level) {
		JNetConnInfo connection = new JNetConnInfo();
		connection.setSzDir(destination);
		connection.setSzDomain(domain);
		connection.setSzUsr(username);
		connection.setSzPwd(pwd);
		boolean isChanged;
		Lock lock = RemoteFolderConnCache.getInstance().getLockByPath(destination);
		try {
			if(lock != null) {
				logger.debug("Getting lock to " + destination + ", domain:" + domain + ", userName:" + username);
				lock.lock();
				logger.debug("locked to " + destination);
			}
			isChanged = WSJNI.AFCheckCompressLevelChanged(connection, level);
			RemoteFolderConnCache.getInstance().addConnections(destination, domain, username, pwd, false);
		}finally {
			if(lock != null) {
				lock.unlock();
				logger.debug("release lock");
			}
		}
		return isChanged;
	}

	@Override
	public boolean isEncryptionAlgorithmAndKeyChanged(String destination,
			String domain, String username, String pwd,
			int encryptionAlgorithm, String encryptionKey) {
		JNetConnInfo connection = new JNetConnInfo();
		connection.setSzDir(destination);
		connection.setSzDomain(domain);
		connection.setSzUsr(username);
		connection.setSzPwd(pwd);
		boolean isChanged;
		Lock lock = RemoteFolderConnCache.getInstance().getLockByPath(destination);
		try {
			if(lock != null) {
				logger.debug("Getting lock to " + destination + ", domain:" + domain + ", userName:" + username);
				lock.lock();
				logger.debug("locked to " + destination);
			}

			isChanged = WSJNI.AFCheckEncryptionAlgorithmAndKeyChanged(connection, encryptionAlgorithm, encryptionKey);
			RemoteFolderConnCache.getInstance().addConnections(destination, domain, username, pwd, false);
		}finally {
			if(lock != null) {
				lock.unlock();
				logger.debug("release lock");
			}
		}
		return isChanged;
	}

	@Override
	public long initBackupDestination(String newDestination, String newDomain,
			String newUsername, String newPassword, String destination,
			String domain, String username, String pwd, long bkpType) throws ServiceException {
		logger
				.debug("initBackupDestination(String, String,	String, String, String,	String, String, String) - start");
		//logger.debug("newDestination: " + newDestination);
		//logger.debug("newDomain: " + newDomain);
		//logger.debug("newUsername:" + newUsername);
		//logger.debug("newPassword:");
		//logger.debug("destination:" + destination);
		//logger.debug("domain:" + domain);
		//logger.debug("username:" + username);
		//logger.debug("pwd:");

		JNetConnInfo newConnection = new JNetConnInfo();
		newConnection.setSzDir(newDestination);
		newConnection.setSzDomain(newDomain);
		newConnection.setSzUsr(newUsername);
		newConnection.setSzPwd(newPassword);

		JNetConnInfo connection = new JNetConnInfo();
		connection.setSzDir(destination);
		connection.setSzDomain(domain);
		connection.setSzUsr(username);
		connection.setSzPwd(pwd);
		long ret;
		Lock newDestLock = RemoteFolderConnCache.getInstance().getLockByPath(newDestination);
		Lock destLock = RemoteFolderConnCache.getInstance().getLockByPath(destination);
		//to avoid deadlock we first lock the path which is lexicographically before another
		Lock firstLock = StringUtil.isEmptyOrNull(destination) || StringUtil.isEmptyOrNull(newDestination) || newDestination.compareTo(destination) < 0 ? newDestLock : destLock;
		Lock secondLock = firstLock == newDestLock ? destLock : newDestLock;
		try {
			if(firstLock != null) {
				logger.debug("Getting firstLock ");
				firstLock.lock();
				logger.debug("Locked ");
			}
			if(secondLock != null) {
				logger.debug("Getting secondLock ");
				secondLock.lock();
				logger.debug("locked");
			}

			ret = WSJNI.AFInitDestination(newConnection, connection, bkpType, false);
			RemoteFolderConnCache.getInstance().addConnections(newDestination, newDomain, newUsername, newPassword, false);
			RemoteFolderConnCache.getInstance().addConnections(destination, domain, username, pwd, false);
			if (ret != 0
					/* if WSJNI.AFInitDestination returns error code BackupConfig_ERR_DEST_INUSE it means the
					 new destination folder is not empty, we should block this case by throwing out an exception.
					 in this case backend module already printed an error message and new setting not saved. 
					*/
					//&& ret != (Long.parseLong(FlashServiceErrorCode.BackupConfig_ERR_DEST_INUSE) - FlashServiceErrorCode.BackupConfig_BASE)
					&& ret != (Long.parseLong(FlashServiceErrorCode.BackupConfig_ERR_FORMER_DEST_MISSING) - FlashServiceErrorCode.BackupConfig_BASE)
					) {
				String msg = WSJNI.AFGetErrorMsg(ret);
				throw new ServiceException(msg, FlashServiceErrorCode.BackupConfig_ERR_DEST_WINSYSMSG);
			}
		}
		finally {
			if(firstLock != null) {
				firstLock.unlock();
				logger.debug("release first lock.");
			}
			if(secondLock != null) {
				secondLock.unlock();
				logger.debug("release second lock.");
			}
		}
		logger
				.debug("initBackupDestination(String, String,	String, String, String,	String, String, String) - end:"
						+ ret);
		return ret;
	}

	@Override
	public long initCopyDestination(String destination, String domain,
			String userName, String password) throws ServiceException {
		logger.debug("initCopyDestination(String, String,	String, String) - start");
		logger.debug("destination:" + destination);
		logger.debug("domain:" + domain);
		logger.debug("username:" + userName);
		logger.debug("pwd:");
		JNetConnInfo dumpConn = new JNetConnInfo();
		dumpConn.setSzDir("");
		dumpConn.setSzDomain("");
		dumpConn.setSzUsr("");
		dumpConn.setSzPwd("");
		JNetConnInfo connection = new JNetConnInfo();
		connection.setSzDir(destination);
		connection.setSzDomain(domain);
		connection.setSzUsr(userName);
		connection.setSzPwd(password);

		long ret;
		Lock lock = RemoteFolderConnCache.getInstance().getLockByPath(destination);
		try {
			if(lock != null) {
				logger.debug("Getting lock to " + destination + ", domain:" + domain + ", userName:" + userName);
				lock.lock();
				logger.debug("locked to " + destination);
			}
			ret = WSJNI.AFInitDestination(connection, dumpConn, 0, true);
			RemoteFolderConnCache.getInstance().addConnections(destination, domain, userName, password, false);
		}
		finally {
			if(lock != null) {
				lock.unlock();
				logger.debug("release lock");
			}
		}
		logger.debug("AFInitDestination result:" + ret);
		if (ret != 0) {
			if (ret == (Long
					.parseLong(FlashServiceErrorCode.BackupConfig_ERR_DEST_INUSE) - FlashServiceErrorCode.BackupConfig_BASE))
				throw new ServiceException(
						FlashServiceErrorCode.BackupConfig_ERR_DEST_INUSE);
			else if (ret == (Long
					.parseLong(FlashServiceErrorCode.BackupConfig_ERR_FORMER_DEST_MISSING) - FlashServiceErrorCode.BackupConfig_BASE))
				throw new ServiceException(
						FlashServiceErrorCode.BackupConfig_ERR_FORMER_DEST_MISSING);
			else if(ret == (Long
					.parseLong(FlashServiceErrorCode.CopyJob_ErrorCopyDestInuse) - FlashServiceErrorCode.CopyJob_BASE))
				throw new ServiceException(
						FlashServiceErrorCode.CopyJob_ErrorCopyDestInuse);
			else {
				String msg = WSJNI.AFGetErrorMsg(ret);
				throw new ServiceException(msg,
						FlashServiceErrorCode.BackupConfig_ERR_DEST_WINSYSMSG);
			}
		}

		logger
				.debug("initCopyDestination(String, String,	String, String) - end:"
						+ ret);
		return ret;
	}

	@Override
	public JJobMonitor GetJobMonitor(long address) {
		JJobMonitor jobMonitor = new JJobMonitor();
		long result = WSJNI.getJobmonitor(address, jobMonitor);
		if (logger.isDebugEnabled()){
			logger.debug("result:"+result);
			logger.debug(StringUtil.convertObject2String(jobMonitor));
		}
		return result == 0?jobMonitor:null;
	}

	@Override
	public void releaseJobMonitor(long address) {
		logger.debug("releaseJobMonitor(long) - start");

		WSJNI.releaseJobMonitor(address);

		logger.debug("releaseJobMonitor(long) - end");
	}

	@Override
	public long createJobMonitor(long shrmemid) {
		return WSJNI.createJobMonitor(shrmemid);
	}

	@Override
	public DeployUpgradeInfo validRemoteDeploy(String localDomain, String localUser,
			String localPassword, RemoteDeployTarget remoteTarget)
			throws Throwable {
		logger
				.debug("validRemoteDeploy(String,String,String,RemoteDeployTarget) - start");
		logger.debug("localDomain:" + localDomain);
		logger.debug("localUser:" + localUser);
		if (remoteTarget != null) {
			logger.debug("RemoteDeployTarget:"
					+ StringUtil.convertObject2String(remoteTarget));
		}
		DeployUpgradeInfo deployUpgrade = new DeployUpgradeInfo();
		long ret = WSJNI.validRemoteDeploy(localDomain, localUser,
				localPassword, remoteTarget,deployUpgrade);
		deployUpgrade.setDwRet(ret);
		if (logger.isDebugEnabled()){
			logger.debug(StringUtil.convertObject2String(deployUpgrade));
		}

		logger
				.debug("validRemoteDeploy(String,String,String,RemoteDeployTarget)- end:"
						+ ret);
		return deployUpgrade;
	}

	@Override
	public long getJobID() {
		JRWLong longValue = new JRWLong();
		boolean returnValue = WSJNI.getJobID(longValue);

		logger.debug("getJobID:" + returnValue);
		logger.debug("retuen job id:" + longValue.getValue());

		if (returnValue)
			return longValue.getValue();
		else
			return -1;
	}

	@Override
	public long getJobIDs(int count) {
		JRWLong longValue = new JRWLong();
		boolean returnValue = WSJNI.getJobIDs(count, longValue);

		logger.debug("getJobID:" + returnValue);
		logger.debug("retuen job id:" + longValue.getValue());

		if (returnValue)
			return longValue.getValue();
		else
			return -1;
		
	}
	@Override
	public long getCurrentJobID() {

		long currentJobID = WSJNI.getCurrentJobID();
		logger.debug("getCurrentJobID:" + currentJobID);

		return currentJobID;
	}


	@Override
	public boolean checkJobExist() {
		boolean returnValue = WSJNI.checkJobExist();
		logger.debug("checkJobExist:" + returnValue);
		return returnValue;
	}

	@Override
	public boolean checkBMRPerformed() {
		boolean returnValue = WSJNI.checkBMRPerformed();
		logger.debug("checkBMRPerformed:" + returnValue);
		return returnValue;
	}

	@Override
	public long copy(JJobScript targetJob) throws Throwable {
		logger.debug("copy(JJobScript) - start");
		debugJJobScript(targetJob);

		long returnlong = WSJNI.AFCopy(targetJob);

		logger.debug("copy(JJobScript) - end");
		return returnlong;
	}

	@Override
	public int cancelJob(long jobID) throws Throwable {
		logger.debug("cancelJob:" + jobID);
		int returnValue = WSJNI.cancelJob(jobID);
		logger.debug("return value:" + returnValue);
		return returnValue;
	}

	@Override
	public long checkFolderAccess(String path, String domain, String user,
			String pwd) throws ServiceException {
		logger
				.debug("checkFolderAccess(String, String, String, String) - start");
		//logger.debug("path:" + path);
		//logger.debug("domain:" + domain);
		//logger.debug("user:" + user);
		//logger.debug("pwd:");
		
		if(this.remoteDriverWithoutUserNamePwd(path, user, pwd)) {
			throw new ServiceException(WebServiceMessages.getResource("destUserNamePasswordNull"),
					FlashServiceErrorCode.BackupConfig_ERR_DEST_WINSYSMSG);
		}
		
		JNetConnInfo dest = new JNetConnInfo();
		dest.setSzDir(path);
		dest.setSzDomain(domain);
		dest.setSzUsr(user);
		dest.setSzPwd(pwd);

		long dwRet;
		ArrayList<JFileInfo> info = new ArrayList<JFileInfo>(1);

		Lock lock = RemoteFolderConnCache.getInstance().getLockByPath(path);
		try {
			if(lock != null) {
				logger.debug("Getting lock ");
				lock.lock();
				logger.debug("Locked ");
			}
			dwRet = WSJNI.AFCheckFolderAccess(dest, info);
			RemoteFolderConnCache.getInstance().addConnections(path, domain, user, pwd, false);
		}finally {
			if(lock != null) {
				lock.unlock();
				logger.debug("release lock");
			}
		}
		logger.debug("JNI return:" + dwRet);

		if (dwRet != 0) {
//			String msg = WSJNI.AFGetErrorMsg(dwRet);
			String msg = String.format(
					WebServiceMessages.getResource("cannotAccessFolder"), path);
			throw new ServiceException(msg,
					FlashServiceErrorCode.BackupConfig_ERR_DEST_WINSYSMSG);
		}
		if (info.size() > 0) {
			if ((JFileInfo.FILE_ATTRIBUTE_READONLY & info.get(0)
					.getDwFileAttributes()) > 0) {
				throw new ServiceException(FlashServiceErrorCode.BackupConfig_ERR_FolderIsNotWritable);
			}
		}
		logger.debug("checkFolderAccess(String, String, String, String) - end");
		return dwRet;
	}

	@Override
	public JActivityLogResult getJobActivityLogs(long jobNo, int start,
			int count) {
		logger.debug("getJobActivityLogs(long, int, int) - start");
		logger.debug("jobNo:" + jobNo);
		logger.debug("start:"+start);
		logger.debug("count:"+count);

		JActivityLogResult logResult = new JActivityLogResult();
		List<JActivityLog> logs = new ArrayList<JActivityLog>();
		logResult.setLogs(logs);

		int result = WSJNI.GetJobLogActivity(jobNo, start, count, logResult);

		if (logger.isDebugEnabled()){
			logger.debug(StringUtil.convertObject2String(logResult));
			logger.debug(StringUtil.convertList2String(logResult.getLogs()));
		}
		logger.debug("getJobActivityLogs(int, int) - end:"+result);
		return logResult;
	}

	@Override
	public long checkDestinationValid(String path) throws Throwable {
		logger.debug("checkDestinationValid() - start");
		logger.debug("path:" + path);
		long result = WSJNI.AFCheckDestValid(path);
		logger.debug("result:" + result);
		logger.debug("checkDestinationValid() - end");
		return result;
	}

	@Override
	public long getPathMaxLength() throws Throwable {
		logger.debug("getPathMaxLength() - start");
		long len = WSJNI.AFGetPathMaxLength();
		logger.debug("getPathMaxLength() - end");
		return len;
	}
	
	@Override
	public long getVSpherePathMaxLength() throws Throwable {
		logger.debug("getVSpherePathMaxLength() - start");
		long len = WSJNI.AFGetVSpherePathMaxLength();
		logger.debug("getVSpherePathMaxLength() - end");
		return len;
	}

	@Override
	public JObjRet<String> checkDestNeedHostName(String path, String serverName, String nodeId, String userName, String password, boolean isCreate)
			throws ServiceException {
		logger.debug("checkDestNeedHostName(String) - start");
		//logger.debug("path:" + path);
		//logger.debug("serverName:" + serverName);

		String domain = "";
		String pwd = "";
		if (userName != null && userName.trim().length() > 0) {
			userName = userName.trim();
			int index = userName.indexOf("\\");
			if (index > 0) {
				domain = userName.substring(0, index);
				userName = userName.substring(index + 1);
			}
		}
		
		JNetConnInfo conn = new JNetConnInfo();
		conn.setSzDir(path);
		conn.setSzDomain(domain);
		conn.setSzPwd(password);
		conn.setSzUsr(userName);
		
		Lock lock = null;
		JObjRet<String> retObj = new JObjRet<String>();

		try {
			lock = RemoteFolderConnCache.getInstance().getLockByPath(path);
			if(lock != null)
				lock.lock();
			WSJNI.AFCreateConnection(conn);
			ArrayList<String> hostNameList = new ArrayList<String>(1);
//			if(changedBackupType < 0)
//				changedBackupType = 0;
//			boolean changedFull = changedBackupType == 0;
			long dwRet = WSJNI.AFCheckDestNeedHostName(path, serverName, nodeId, isCreate, hostNameList);
			logger.debug("JNI return:" + dwRet);

			if (dwRet != 0 && dwRet != BackupServiceErrorCode.WARN_FolderWithSIDExist) {
				
				if (dwRet == (Long
						.parseLong(FlashServiceErrorCode.BackupConfig_ERR_DEST_INUSE) - FlashServiceErrorCode.BackupConfig_BASE)) {
					throw new ServiceException(
							FlashServiceErrorCode.BackupConfig_ERR_DEST_INUSE);
				} else if (dwRet == (Long
						.parseLong(FlashServiceErrorCode.BackupConfig_ERR_DEST_UNDER_BAKDEV) - FlashServiceErrorCode.BackupConfig_BASE)) {
					throw new ServiceException(
							FlashServiceErrorCode.BackupConfig_ERR_DEST_UNDER_BAKDEV);
				} else if (dwRet == (Long
						.parseLong(FlashServiceErrorCode.BackupConfig_ERR_DEST_FOR_OTHER) - FlashServiceErrorCode.BackupConfig_BASE)) {
					throw new ServiceException(
							FlashServiceErrorCode.BackupConfig_ERR_DEST_FOR_OTHER);
				} else {
					String msg = WSJNI.AFGetErrorMsg(dwRet);
					throw new ServiceException(
							msg,
							FlashServiceErrorCode.BackupConfig_ERR_DEST_WINSYSMSG);
				}
			}
			if (hostNameList.size() > 0) {
				retObj.setItem(hostNameList.get(0));
			}
			retObj.setRetCode(dwRet);
		}finally {
			if(lock != null)
				lock.unlock();
			WSJNI.AFCutConnection(conn, false);
		}

		logger.debug("checkDestNeedHostName(String) - end");
		return retObj;
	}

	@Override
	public JObjRet<String> checkDestNeedVMInfo(String path, String serverName,String instanceUUID)
	throws ServiceException {
		logger.debug("checkDestNeedHostName(String) - start");
		//logger.debug("path:" + path);
		//logger.debug("serverName:" + serverName);

		JObjRet<String> retObj = new JObjRet<String>();
		ArrayList<String> hostNameList = new ArrayList<String>(1);
		long dwRet = WSJNI.AFCheckDestNeedVMInfo(path, serverName, hostNameList,instanceUUID);
		logger.debug("JNI return:" + dwRet);

		if (dwRet != 0) {
			if (dwRet == (Long
					.parseLong(FlashServiceErrorCode.BackupConfig_ERR_DEST_INUSE) - FlashServiceErrorCode.BackupConfig_BASE)) {
				throw new ServiceException(
						FlashServiceErrorCode.BackupConfig_ERR_DEST_INUSE);
			} else if (dwRet == (Long
					.parseLong(FlashServiceErrorCode.BackupConfig_ERR_DEST_UNDER_BAKDEV) - FlashServiceErrorCode.BackupConfig_BASE)) {
				throw new ServiceException(
						FlashServiceErrorCode.BackupConfig_ERR_DEST_UNDER_BAKDEV);
			} else if (dwRet == (Long
					.parseLong(FlashServiceErrorCode.BackupConfig_ERR_DEST_FOR_OTHER) - FlashServiceErrorCode.BackupConfig_BASE)) {
				throw new ServiceException(
						FlashServiceErrorCode.BackupConfig_ERR_DEST_FOR_OTHER);
			} else {
				String msg = WSJNI.AFGetErrorMsg(dwRet);
				throw new ServiceException(msg,
						FlashServiceErrorCode.BackupConfig_ERR_DEST_WINSYSMSG);
			}
		}
		if (hostNameList.size() > 0) {
			retObj.setItem(hostNameList.get(0));
		}
		retObj.setRetCode(dwRet);

		logger.debug("checkDestNeedHostName(String) - end");
		return retObj;
	}

	@Override
	public Account getAdminAccount() throws ServiceException {
		Account account = new Account();
		try {
		logger.debug("getAdminAccount() - start");
		long result = WSJNI.AFReadAdminAccount(account);
		logger.debug("return value:" + result + ", account name: " + account.getUserName());
		if(result != 0)
		{
			String msg = WSJNI.AFGetErrorMsg(result);
			throw new ServiceException(msg, FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		logger.debug("getAdminAccount() - end");
		}catch(ServiceException se)
		{
			throw se;
		}
		catch(Exception e) {
			logger.debug(e.getMessage(), e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		return account;
	}

	@Override
	public void saveAdminAccount(Account account) throws ServiceException {
		try {
		logger.debug("saveAdminAccount() - start");
		account.setUserName(account.getUserName() == null ? "" : account.getUserName());
		account.setPassword(account.getPassword() == null ? "" : account.getPassword());
		long result = WSJNI.AFSaveAdminAccount(account);
		logger.debug("result:" + result);
		if(result != 0) {
			String msg = WSJNI.AFGetErrorMsg(result);
			throw new ServiceException(msg, FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		logger.debug("saveAdminAccount() - end");
		}catch(ServiceException se)
		{
			throw se;
		}
		catch(Exception e) {
			logger.debug(e.getMessage(), e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public void validateAdminAccount(Account account) throws ServiceException {
		try {
		logger.debug("validateAdminAccount() - start");
		long result = WSJNI.AFCheckAdminAccountValid(account);
		logger.debug("result:" + result);

		if(result == (Long
				.parseLong(FlashServiceErrorCode.BackupConfig_ERR_INVALID_USER) - FlashServiceErrorCode.BackupConfig_BASE))
			throw new ServiceException(FlashServiceErrorCode.BackupConfig_ERR_INVALID_USER);
		else if(result == (Long
						.parseLong(FlashServiceErrorCode.BackupConfig_ERR_USER_NOT_IN_GROUP) - FlashServiceErrorCode.BackupConfig_BASE))
			throw new ServiceException(FlashServiceErrorCode.BackupConfig_ERR_USER_NOT_IN_GROUP);
		else if(result != 0)
		{
			String msg = WSJNI.AFGetErrorMsg(result);
			throw new ServiceException(msg, FlashServiceErrorCode.Common_ErrorOccursInService);
		}

		logger.debug("validateAdminAccount() - end");
		}
		catch(ServiceException se)
		{
			throw se;
		}
		catch(Exception e) {
			logger.debug(e.getMessage(), e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public long getDestDriveType(String path) throws ServiceException {
		try {
			logger.debug("getDestDriveType() - start");
			long result = WSJNI.AFGetBakDestDriveType(path);
			logger.debug("result:" + result);
			logger.debug("getDestDriveType() - end");
			return result;
		}
		catch(Exception e)
		{
			logger.debug(e.getMessage(), e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public void createDir(String parentPath, String subDir)
			throws ServiceException {
		try {
			logger.debug("createDir() - start");
			logger.debug("parenPath: " + parentPath);
			logger.debug("subDir: " + subDir);

			long result = WSJNI.AFCreateDir(parentPath, subDir);
			logger.debug("result:" + result);

			if(result > 0)
			{
				String msg = WSJNI.AFGetErrorMsg(result);
				throw new ServiceException(msg,
						FlashServiceErrorCode.Browser_CreateFolderFailed);
			}

			logger.debug("createDir() - end");
		}
		catch (ServiceException e) {
			logger.debug(e.getMessage(), e);
			throw e;
		}
		catch (Exception e) {
			logger.debug(e.getMessage(), e);
			throw new ServiceException(
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public long disconnectRemotePath(String path, String domain, String user,
			String pwd, boolean force) throws ServiceException {
		try {
			logger.debug("cutConnection() - start");
			logger.debug("path:" + path);
			logger.debug("domain:" + domain);
			logger.debug("user:" + user);
			logger.debug("pwd: *");

			JNetConnInfo dest = new JNetConnInfo();
			dest.setSzDir(path);
			dest.setSzDomain(domain);
			dest.setSzUsr(user);
			dest.setSzPwd(pwd);

			long result = WSJNI.AFCutConnection(dest, force);
			logger.debug("result:" + result);

			if(result > 0)
			{
				String msg = WSJNI.AFGetErrorMsg(result);
				throw new ServiceException(msg,
						FlashServiceErrorCode.Common_ErrorOccursInService);
			}

			logger.debug("cutConnection() - end");
			return result;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public boolean checkRemotePathAccess(String path, String domain, String user,
			String pwd)  throws ServiceException{
		Lock lock = null;
		try {
			lock = RemoteFolderConnCache.getInstance().getLockByPath(path);
			if(lock != null)
				lock.lock();
			logger.debug("cutConnection() - start");
			logger.debug("path:" + path);
			logger.debug("domain:" + domain);
			logger.debug("user:" + user);
			logger.debug("pwd: *");

			JNetConnInfo dest = new JNetConnInfo();
			dest.setSzDir(path);
			dest.setSzDomain(domain);
			dest.setSzUsr(user);
			dest.setSzPwd(pwd);

			boolean result = WSJNI.AFCheckPathAccess(dest);
			logger.debug("result:" + result);
			RemoteFolderConnCache.getInstance().addConnections(
					dest.getSzDir(), dest.getSzDomain(),
					dest.getSzUsr(), dest.getSzPwd(), false);
			logger.debug("cutConnection() - end");
			return result;
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
			throw new ServiceException(
					FlashServiceErrorCode.Common_ErrorOccursInService);
		} finally {
			if(lock != null)
				lock.unlock();
		}
	}

	@Override
	public JPagedCatalogItem getPagedCatalogItems(String catPath,
			long parentID, int start, int size) throws ServiceException {
		logger.debug("getPagedCatalogItems(String, long, int, int) - enter");
		logger.debug(catPath);
		logger.debug(parentID);
		logger.debug(start);
		logger.debug(size);

		JPagedCatalogItem pagedItem = new JPagedCatalogItem();
		long handle = WSJNI.OpenCatalog(catPath);
//		handleCatalogError(handle, catPath);
		if(handle == 0)
			return null;
		JRWLong totalCnt = new JRWLong();
		long ret = WSJNI.GetChildrenCount(handle, parentID, totalCnt);

		logger.debug("ret:" + ret);
		if (ret == 0) {
			long total = totalCnt.getValue();
			logger.debug("total:" + total);

			pagedItem.setTotal(total);

			if (total > 0) {
				JRWLong realCnt = new JRWLong();
				ArrayList<JCatalogDetail> retArr = new ArrayList<JCatalogDetail>();
				long dwRet = WSJNI.GetChildrenEx(handle, parentID, start, size,
						realCnt, retArr);
				logger.debug("dwRet:" + dwRet);

				ArrayList<JCatalogDetail> clippedRetArr = new ArrayList<JCatalogDetail>();
				if (dwRet == 0) {

					if (retArr != null && retArr.size() > 0) {
						String path = WSJNI.GetFullPath(handle, retArr.get(0)
								.getPathID());
						if (!StringUtil.isEmptyOrNull(path)) {
							try {
								path = path.substring(
										path.indexOf("\\", 3) + 1, path
												.length());
							} catch (Exception e) {
								logger.error(e);
							}
						}

						int curr = start;

						for (JCatalogDetail detail : retArr) {
							if (curr++ >= total) {
								logger.error("curr:" + curr);
								logger.error("retArr.size:" + retArr.size());
								logger.error("total:" + total);
								logger.error("start:" + start);
								logger.error("size:" + size);
								break;
							}

							detail.setPath(path);
							detail.setFileDate(BackupConverterUtil
									.dosTime2UTC(detail.getFileDate()));
							JRWLong childrenCnt = new JRWLong();
							long retGetChildrenCount = WSJNI
									.GetChildrenCount(handle, detail
											.getLongNameID(), childrenCnt);
							logger.debug("GetChildrenCount ret:"
									+ retGetChildrenCount);
							logger.debug("childrenCnt:"
									+ childrenCnt.getValue());
							if (ret == 0) {
								detail.setChildrenCount(childrenCnt.getValue());
							}
							clippedRetArr.add(detail);
						}
					}

					pagedItem.setDetails(clippedRetArr);

					if (logger.isDebugEnabled())
						logger.debug(StringUtil
								.convertList2String(clippedRetArr));
					logger
							.debug("getPagedCatalogItems(String, long, int, int) - end");
				}
			}
		}

		WSJNI.CloseGetCatalog(handle);

		return pagedItem;
	}

	@Override
	public long saveThreshold(long threshold) throws Throwable {
		//logger.debug("Threshold:"+threshold);
		return WSJNI.AFSetThreshold(threshold);
	}

	@Override
	public String findNewDestination(String oldDestination) throws Throwable {
		logger.debug("findNewDestination:"+oldDestination);
		return WSJNI.AFTryToFindDest(oldDestination);
	}

	@Override
	public AlternativePath checkSQLAlternateLocation(String basePath, String instName,
			String dbName) throws ServiceException {

		try {
			logger.debug("checkSQLAlternateLocation() - start");
			logger.debug("basePath:" + basePath);
			logger.debug("instName:" + instName);
			logger.debug("dbName:" + dbName);

			ArrayList<String> alterPath = new ArrayList<String>(1);
			long result = WSJNI.AFCheckSQLAlternateLocation(basePath, instName, dbName, alterPath);
			logger.debug("result:" + result);
			AlternativePath altPath = new AlternativePath();
			altPath.setMaxPathLength(result);
			if(alterPath.size() > 0) {
				logger.debug("alterDestPath:" + alterPath.get(0));
				altPath.setAlterPath(alterPath.get(0));
			}

			logger.debug("checkSQLAlternateLocation() - end");
			return altPath;
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
			throw new ServiceException(
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}

	}

	@Override
	public boolean checkBLILic() {
		logger.debug("checkBLILic() - start");
		boolean result = WSJNI.AFCheckBLILic();
		logger.debug("Has BLILic:" + result);
		logger.debug("checkBLILic() - end");
		return result;
	}

	@Override
	public boolean checkDirPathValid(String path, String domain, String user,
			String pwd) throws ServiceException {
		logger.debug("checkDirPathValid(String, String, String, String) - start");
		JNetConnInfo connInfo = new JNetConnInfo();
		connInfo.setSzDir(path);
		connInfo.setSzDomain(domain);
		connInfo.setSzUsr(user);
		connInfo.setSzPwd(pwd);

		long ret=0;
		Lock lock = RemoteFolderConnCache.getInstance().getLockByPath(path);
		try {
			if (lock != null) {
				logger.debug("Getting lock to " + path
						+ ", domain:" + domain + ", userName:"
						+ user);
				lock.lock();
				logger.debug("Locked to " + path);
			}
			ret = WSJNI.AFCheckDirPathValid(connInfo);
			logger.debug("JNI return:" + ret);

			RemoteFolderConnCache.getInstance().addConnections(
					connInfo.getSzDir(), connInfo.getSzDomain(),
					connInfo.getSzUsr(), connInfo.getSzPwd(), false);
		} finally {
			if (lock != null) {
				lock.unlock();
				logger.debug("release lock");
			}
		}
		
		if(ret==-1) { // can connect the remote share folder, but the folder doesn't exits.
			return false;
		}
		
		if(ret!=0) {
			String msg = WSJNI.AFGetErrorMsg(ret);
			throw new ServiceException(msg,	FlashServiceErrorCode.BackupConfig_ERR_DEST_WINSYSMSG);
		}

		logger.debug("checkDirPathValid(String, String, String, String) - end");
		return true;
	}
	
	@Override
	public boolean validateDirPath(String path, String domain, String user,
			String pwd) throws ServiceException {
		logger.debug("checkDirPathValid(String, String, String, String) - start");
		JNetConnInfo connInfo = new JNetConnInfo();
		connInfo.setSzDir(path);
		connInfo.setSzDomain(domain);
		connInfo.setSzUsr(user);
		connInfo.setSzPwd(pwd);

		long ret=0;
		Lock lock = RemoteFolderConnCache.getInstance().getLockByPath(path);
		try {
			if (lock != null) {
				logger.debug("Getting lock to " + path
						+ ", domain:" + domain + ", userName:"
						+ user);
				lock.lock();
				logger.debug("Locked to " + path);
			}
			if(!StringUtil.isEmptyOrNull(domain))
				user = domain + "\\" + user; 
			int dwRet = WSJNI.AFIVerifyDestUser(path, user, pwd);

			if (dwRet != 0) {
				// String msg = WSJNI.AFGetErrorMsg(dwRet);
				String msg = String.format(
						WebServiceMessages.getResource("cannotAccessFolder"), path);
				throw new ServiceException(msg,
						FlashServiceErrorCode.BackupConfig_ERR_DEST_WINSYSMSG);
			}else 
				ret = WSJNI.AFCheckDirPathValid(connInfo);
			logger.debug("JNI return:" + ret);

			RemoteFolderConnCache.getInstance().addConnections(
					connInfo.getSzDir(), connInfo.getSzDomain(),
					connInfo.getSzUsr(), connInfo.getSzPwd(), false);
		} finally {
			if (lock != null) {
				lock.unlock();
				logger.debug("release lock");
			}
		}
		
		if(ret==-1) { // can connect the remote share folder, but the folder doesn't exits.
			return false;
		}
		
		if(ret!=0) {
			String msg = WSJNI.AFGetErrorMsg(ret);
			throw new ServiceException(msg,	FlashServiceErrorCode.BackupConfig_ERR_DEST_WINSYSMSG);
		}

		logger.debug("checkDirPathValid(String, String, String, String) - end");
		return true;
	}

	@Override
	public void checkContainRecoveryPoints(JNetConnInfo connInfo)
			throws ServiceException {
		logger.debug("checkRecoveryPoints(JNetConnInfo) - start");
		logger.debug("path:" + connInfo.getSzDir());
		logger.debug("domain:" + connInfo.getSzDomain());
		logger.debug("user:" + connInfo.getSzUsr());
		logger.debug("pwd:");

		long dwRet;
		Lock lock = RemoteFolderConnCache.getInstance().getLockByPath(
				connInfo.getSzDir());
		try {
			if (lock != null) {
				logger.debug("Getting lock to " + connInfo.getSzDir()
						+ ", domain:" + connInfo.getSzDomain() + ", userName:"
						+ connInfo.getSzUsr());
				lock.lock();
				logger.debug("Locked to " + connInfo.getSzDir());
			}
			dwRet = WSJNI.AFCheckDestContainRecoverPoint(connInfo);
			logger.debug("JNI return:" + dwRet);

			RemoteFolderConnCache.getInstance().addConnections(
					connInfo.getSzDir(), connInfo.getSzDomain(),
					connInfo.getSzUsr(), connInfo.getSzPwd(), false);
		} finally {
			if (lock != null) {
				lock.unlock();
				logger.debug("release lock");
			}
		}

		if(dwRet == (Long.parseLong(FlashServiceErrorCode.RestoreJob_NotBackupDestination) - FlashServiceErrorCode.RestoreJob_BASE))
			throw new ServiceException(
					FlashServiceErrorCode.RestoreJob_NotBackupDestination);
		else if(dwRet == (Long.parseLong(FlashServiceErrorCode.RestoreJob_NotContainRecoveryPoints) - FlashServiceErrorCode.RestoreJob_BASE))
			throw new ServiceException(
					FlashServiceErrorCode.RestoreJob_NotContainRecoveryPoints);
		else if (dwRet != 0) {
			String msg = WSJNI.AFGetErrorMsg(dwRet);
			throw new ServiceException(msg,
					FlashServiceErrorCode.RestoreJob_SourceInvalid);
		}

		logger.debug("checkRecoveryPoints(JNetConnInfo) - end");
	}

	@Override
	public boolean checkBaseLic() {
		boolean result = WSJNI.AFCheckBaseLic();
		logger.debug(result);

		return result;
	}

	@Override
	public int getLocalADTPackage() {
		int ret = WSJNI.GetLocalPackage();
		logger.debug(ret);
		return ret;
	}

	@Override
	public List<JApplicationWriter> getExcludedAppComponents(List<String> volumeList)
		throws ServiceException {
		try {
			logger.debug("getExcludedAppComponents(List<String>) - start");
			logger.debug("volumeList:" + StringUtil.convertList2String(volumeList));

			 List<JApplicationWriter> appList = new ArrayList<JApplicationWriter>();
			long result = WSJNI.GatherExcludedFileListInWriter(volumeList, appList);
			logger.debug("result:" + result);

			if(result != 0)
			{
				String msg = WSJNI.AFGetErrorMsg(result);
				throw new ServiceException(msg, FlashServiceErrorCode.Common_ErrorOccursInService);
			}

			logger.debug("resulted app list:" + StringUtil.convertList2String(appList));

			logger.debug("getExcludedAppComponents(List<String>) - end");
			return appList;
		}
		catch(ServiceException se)
		{
			throw se;
		}
		catch(Exception e) {
			logger.debug(e.getMessage(), e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public void regenerateWriterMetadata() throws ServiceException{
		try {
			logger.debug("regenerateWriterMetadata() - start");
			long result = WSJNI.RegenerateWriterMetadata();
			if(result != 0)
			{
				String msg = WSJNI.AFGetErrorMsg(result);
				throw new ServiceException(msg, FlashServiceErrorCode.Common_ErrorOccursInService);
			}
			logger.debug("regenerateWriterMetadata() - end");
		}
		catch(ServiceException se) {
			throw se;
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public List<String> getLocalDestVolumes(String destPath) throws ServiceException {
		try {
			logger.debug("getLocalDestVolumes(String) - start");
			logger.debug("destPath:" + destPath);

			ArrayList<String> volumeList = new ArrayList<String>();
			long result = WSJNI.AFGetLocalDestVolumes(destPath, volumeList);
			if(result != 0)
			{
				String msg = WSJNI.AFGetErrorMsg(result);
				throw new ServiceException(msg, FlashServiceErrorCode.Common_ErrorOccursInService);
			}
			return volumeList;
		}catch(ServiceException se) {
			throw se;
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public List<JBackupItem> GetBackupItem(String dest, String domain,
			String user, String pwd, String subPath) {
		logger
				.debug("GetBackupItem(String, String, String, String, String) - start");
		logger.debug("dest:" + dest);
		logger.debug("domain:" + domain);
		logger.debug("user:" + user);
		logger.debug("pwd:");
		logger.debug("subPath:" + subPath);

		List<JBackupItem> bkpItemList = new ArrayList<JBackupItem>();

		long jniResult;
//		JRestorePoint[] retJResPntArr;
		Lock lock = RemoteFolderConnCache.getInstance().getLockByPath(dest);
		try {
			if (lock != null) {
				logger.debug("lock to " + dest + ", domain:" + domain
						+ ", userName:" + user);
				lock.lock();
			}
			jniResult = WSJNI.GetBackupItem(dest, domain, user, pwd, subPath,
					bkpItemList);
			
			if(jniResult == 2) {
				String msg = WebServiceMessages.getResource("getBackupItemSessionMerged");
				throw AxisFault.fromAxisFault(msg, FlashServiceErrorCode.Common_General_Message);
			}

			RemoteFolderConnCache.getInstance().addConnections(dest, domain,
					user, pwd, false);

			if (bkpItemList != null && bkpItemList.size() > 0) {
				for (JBackupItem bkpItem : bkpItemList) {
					if(bkpItem.getGuid().equals(JBackupItem.GUID_ACTIVE_DIRECTORY)){
						continue;
					};
					String catPath = bkpItem.getCatalogFilePath();
					long handle = WSJNI.OpenCatalog(catPath);
					if(handle == 0){
						logger.warn("Open catalog with path " + catPath
								+ " failed");
						continue;
					}
					logger.debug("handle:" + handle);
					JRWLong childrenCnt = new JRWLong();
					long parentID = -1;
					long ret = WSJNI.GetChildrenCount(handle, parentID,
							childrenCnt);
					logger.debug("ret:" + ret);
					logger.debug("childrenCnt:" + childrenCnt.getValue());
					WSJNI.CloseGetCatalog(handle);
					if (ret == 0) {
						bkpItem.setChildrenCount(childrenCnt.getValue());
					}
				}
				for (JBackupItem bkpItem : bkpItemList) {
					logger.debug(bkpItem.toString());
				}
			}
			
		} finally {
			if (lock != null) {
				lock.unlock();
				logger.debug("release lock");
			}
		}

		if (logger.isDebugEnabled())
			logger.debug(StringUtil.convertList2String(bkpItemList));
		logger
		.debug("GetBackupItem(String, String, String, String, String) end:" + jniResult);
		return bkpItemList;
	}

	@Override
	public List<NetworkPath> getNetworkPathForMappedDrive(String userName)
			throws ServiceException {
		try {
			logger.debug("getetNetworkPathForMappedDrive(String) - start");
			logger.debug("userName:" + userName);

			ArrayList<NetworkPath> pathList = new ArrayList<NetworkPath>();

			if(!StringUtil.isEmptyOrNull(userName)) {
				long ret = WSJNI.AFGetNetworkPathForMappedDrive(userName, pathList);
				logger.debug("Native Facade return value:" + ret);
			}

//			if(ret != 0)
//			{
//				String msg = WSJNI.AFGetErrorMsg(ret);
//				throw new ServiceException(msg, FlashServiceErrorCode.Common_ErrorOccursInService);
//			}
//
			logger.debug("getetNetworkPathForMappedDrive(String) - end");
			return pathList;
//		}catch(ServiceException se) {
//			throw se;
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	private List<String> getVolumes() throws Exception {
		logger.debug("getLicInfo() - start");
		List<String> lst = new ArrayList<String>();
		BackupConfiguration conf = BackupService.getInstance().getBackupConfiguration();
		if(conf != null){
			BackupVolumes bkpV = conf.getBackupVolumes();
			if (bkpV == null || bkpV.isFullMachine()) {
				String backupDest = conf.getDestination();
				if (backupDest == null)
					backupDest = "";

				logger.debug("backupDest:" + backupDest);

				String usr = conf.getUserName();
				String pwd = conf.getPassword();

				if (usr == null)
					usr = "";
				if (pwd == null)
					pwd = "";

				Volume[] volumes = BrowserService.getInstance().getVolumes(
						false, backupDest, usr, pwd);

				if (volumes != null && volumes.length > 0) {
					if (backupDest.length() > 1 && backupDest.endsWith("/")) {
						backupDest = backupDest.substring(0, backupDest
								.length() - 2)
								+ "\\";
					} else if (!backupDest.endsWith("\\")) {
						backupDest += "\\";
					}
					List<String> vMnt = new ArrayList<String>();
					long ret = WSJNI.AFGetMntFromPath(backupDest, vMnt);
					logger.debug("ret:" + ret);

					String matchedVolume = "";
					int index = -1;
					for (int i = 0; i < volumes.length; i++) {
						Volume v = volumes[i];
						if (v != null && v.getName() != null) {
							String name = v.getName();
							if (ret == 0) {
								boolean isFound = false;
								for (String mp : vMnt) {
									if (mp.equalsIgnoreCase(name)) {
										matchedVolume += mp + ",";
										isFound = true;
										break;
									}
								}
								if (!isFound) {
									lst.add(name);
								}
							} else {
								if (backupDest.toLowerCase().startsWith(
										name.toLowerCase())
										&& name.length() > matchedVolume
												.length()) {
									matchedVolume = name;
									index = i;
								}

							}
						}
					}

					logger.debug("matchedVolume:" + matchedVolume);

					if (ret != 0) {
						for (int i = 0; i < volumes.length; i++) {
							if (i != index) {
								lst.add(volumes[i].getName());
							}
						}
					}
				}
			} else if (bkpV != null && bkpV.getVolumes() != null
					&& bkpV.getVolumes().size() > 0) {
				lst = bkpV.getVolumes();
			}
		}
		if (logger.isDebugEnabled())
			logger.debug(StringUtil.convertList2String(lst));
		
		return lst;
	}
	
	@Override
	public LicInfo getLicInfo() throws ServiceException	{
		try {
			//List<String> lst = this.getVolumes();
			LicInfo info = WSJNI.AFGetLicenseError();
			logger.debug("getLicInfo() - end");
			return info;
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public void checkRestoreSession(String sessionPath, String domain,
			String userName, String passwd, List<Integer> list)
			throws ServiceException {
		boolean isSessExist = true;

		logger.debug("checkRestoreSession()- start");
		logger.debug("sessionPath:" + sessionPath);
		logger.debug("domain:" + domain);
		logger.debug("userName:" + userName);

		JNetConnInfo connInfo = new JNetConnInfo();
		connInfo.setSzDir(sessionPath);
		connInfo.setSzDomain(domain);
		connInfo.setSzUsr(userName);
		connInfo.setSzPwd(passwd);
		long ret;
		Lock lock = RemoteFolderConnCache.getInstance().getLockByPath(
				sessionPath);
		try {
			if (lock != null) {
				logger.debug("Getting lock to " + sessionPath + ", domain:"
						+ domain + ", userName:" + userName);
				lock.lock();
				logger.debug("locked to " + sessionPath);
			}
			ret = WSJNI.AFCreateConnection(connInfo);
			if (ret != 0) {
				logger.debug("AFCreateConnection:" + ret);
				throw new ServiceException(
						FlashServiceErrorCode.RestoreJob_InvalidSessionPath);
			}

			for (int sessNum : list) {
				isSessExist = WSJNI.AFCheckSessExist(sessionPath, sessNum);
				if (!isSessExist) {
					logger.debug("isSessExist = false, sessNum:" + sessNum);
					throw new ServiceException(
							FlashServiceErrorCode.Restore_ERR_SessNotExist);
				}
			}

			logger.debug("checkRestoreSession() - end");
		} finally {
			if (lock != null) {
				lock.unlock();
			}
			ret = WSJNI.AFCutConnection(connInfo, false);
			logger.debug("AFCutConnection:" + ret);
		}
	}

	@Override
	public boolean AFValidateSessPasswordByHash(String password, long pwdLen, String hashValue,
			long hashLen, HttpSession session) throws ServiceException {
		if(logger.isDebugEnabled()) {
			logger.debug("AFValidateSessPasswordByHash(String, long, String, long) - start");
		}

		Lock lock = null;
		try {
			lock = RemoteFolderConnCache.getInstance().getLockByPath(RemoteFolderConnCache.getCachedPath(session));
			//if lock != null, this path is a remote path
			if(lock != null) {
				lock.lock();
				RemoteFolderConnCache.reEstalishConnetion(session);
			}

			boolean isValid = WSJNI.AFValidateSessPasswordByHash(password, pwdLen, hashValue, hashLen);
			if(logger.isDebugEnabled()) {
				logger.debug("validateSessionPassword(String, String, long) - end");
				logger.debug("isValid:" + isValid);

			}
			return isValid;
		}
		finally {
			if(lock != null)
				lock.unlock();
		}
	}

		@Override
	public boolean validateSessionPassword(String password, String destination,
			long sessionNum, HttpSession session) throws ServiceException {
		if(logger.isDebugEnabled()) {
			logger.debug("validateSessionPassword(String, String, long) - start");
			logger.debug("password:");
			logger.debug("destination:" + destination);
			logger.debug("sessionNum:" + sessionNum);
		}

		Lock lock = null;
		try {
			lock = RemoteFolderConnCache.getInstance().getLockByPath(RemoteFolderConnCache.getCachedPath(session));
			//if lock != null, this path is a remote path
			if(lock != null) {
				lock.lock();
				RemoteFolderConnCache.reEstalishConnetion(session);
			}

			boolean isValid = WSJNI.AFValidateSessPassword(password, destination, sessionNum);
			if(logger.isDebugEnabled()) {
				logger.debug("validateSessionPassword(String, String, long) - end");
				logger.debug("isValid:" + isValid);

			}
			return isValid;
		}
		finally {
			if(lock != null)
				lock.unlock();
		}
	}

	/*
	 * If password of some session is not found in the password management file,
	 * the password in the returned array will be set to null.
	 * If password is empty, then return String.empty for that session
	 */
	@Override
	public String[] getSessionPasswordBySessionGuid(String[] sessionGuid) throws ServiceException{
		if(logger.isDebugEnabled()){
			logger.debug("getSessionPasswordBySessionGuid - start");
			logger.debug("session guid: " + StringUtil.convertArray2String(sessionGuid));
		}
		try{
			String[] passwords = WSJNI.AFGetSessionPasswordBySessionGuid(sessionGuid);
			return passwords;
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	// *** Patch Manager ***
	@Override
	public    boolean IsPatchManagerRunning(String runningMutexName)
	{	boolean isrunning=false;
		try{
			isrunning=WSJNI.IsPatchManagerRunning(runningMutexName);
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
		return isrunning;
	}
	
	@Deprecated	
	@Override
	public    long GetLastError()
	{
		return WSJNI.GetLastError();
	}
	
	@Deprecated
	@Override
	public    String FormatMessage(int errorCode)
	{
		return WSJNI.FormatMessage(errorCode);
	}	
	
	@Override
	public    boolean IsPatchManagerBusy(String busyMutexName)
	{
		return WSJNI.IsPatchManagerBusy(busyMutexName);
	}
	
	@Override
	public boolean IsPatchManagerBusyEx(String busyMutexName, int type) {
		return WSJNI.IsPatchManagerBusyEx(busyMutexName, type);
	}

	
	@Override
	public long checkUpdate( ){
		return WSJNI.checkUpdate( );
	}
	
	@Override
	public long checkUpdateEx( int type ){
		return WSJNI.checkUpdateEx( type );
	}

	//added by cliicy.luo to add Hotfix menu-item
	@Override
	public long checkBIUpdate( ){
		return WSJNI.checkBIUpdate( );
	}
	
	//@Override
	//public long checkBIUpdate( int type ){
	//	return WSJNI.checkBIUpdate( type );
	//}
	
	@Override
	public long checkBIUpdateEx( int type ){
		return WSJNI.checkBIUpdateEx( type );	
	}
	//added by cliicy.luo to add Hotfix menu-item
	
	@Override
	public String getUpdateStatusFile(){
		return WSJNI.getUpdateStatusFile();
	}
	
	@Override
	public String getUpdateStatusFileEx( int type ){
		return WSJNI.getUpdateStatusFileEx( type );
	}
	
	@Override
	public String getUpdateSettingsFile(){
		return WSJNI.getUpdateSettingsFile();
	}
	
	@Override
	public String getUpdateSettingsFileEx( int type ){
		return WSJNI.getUpdateSettingsFileEx( type );
	}
	
	@Override
	public long testUpdateServerConnection(	int iServerType, String strDownloadServer, String strDownloadServerPort, String strProxyServerName,
			String strProxyPort, String strProxyUserName, String strProxyPassword){
		return WSJNI.testUpdateServerConnection( iServerType, strDownloadServer,  strDownloadServerPort, strProxyServerName,
				strProxyPort, strProxyUserName, strProxyPassword );
	}
	
	@Override
	public long testUpdateServerConnectionEx( int iServerType, String strDownloadServer,  String strDownloadServerPort, String strProxyServerName,
			String strProxyPort, String strProxyUserName, String strProxyPassword, int type){		
		return WSJNI.testUpdateServerConnectionEx(iServerType, strDownloadServer, strDownloadServerPort, strProxyServerName, 
				strProxyPort, strProxyUserName, strProxyPassword, type);				
	}
	
	@Override
	public String getBIUpdateStatusFile(){
		return WSJNI.getBIUpdateStatusFile();
	}
	
	@Override
	public String getBIUpdateStatusFileEx( int type ){
		return WSJNI.getBIUpdateStatusFileEx( type );
	}
	
	//added by cliicy.luo
	@Override
	public long testBIUpdateServerConnection(	int iServerType, String strDownloadServer, String strDownloadServerPort, String strProxyServerName,
			String strProxyPort, String strProxyUserName, String strProxyPassword){
		return WSJNI.testBIUpdateServerConnection( iServerType, strDownloadServer,  strDownloadServerPort, strProxyServerName,
				strProxyPort, strProxyUserName, strProxyPassword );
	}
	
	@Override
	public long testBIUpdateServerConnectionEx( int iServerType, String strDownloadServer,  String strDownloadServerPort, String strProxyServerName,
			String strProxyPort, String strProxyUserName, String strProxyPassword, int type){		
		return WSJNI.testBIUpdateServerConnectionEx(iServerType, strDownloadServer, strDownloadServerPort, strProxyServerName, 
				strProxyPort, strProxyUserName, strProxyPassword, type);				
	}
	
	@Override
	public long installBIUpdate( ){
		return WSJNI.installBIUpdate();
	}
	
	@Override
	public long installBIUpdateEx( int type){
		return WSJNI.installBIUpdateEx(type);
	}
			
	//added by cliicy.luo
	
	@Override
	public long installUpdate( ){
		return WSJNI.installUpdate();
	}
	
	@Override
	public long installUpdateEx( int type){
		return WSJNI.installUpdateEx(type);
	}
	
	@Override
	public String getUpdateErrorMessage( int errorCode ){
		return WSJNI.getUpdateErrorMessage(errorCode);
	}
	
	@Override
	public String getUpdateErrorMessageEx( int errorCode, int type ){
		return WSJNI.getUpdateErrorMessageEx(errorCode, type);
	}	
	
	// *** Patch Manager End ***

	@Override
	public String GetCacheFile4Sync()
	{
		logger.debug("D2DSync - BEFORE GetCacheFile4Sync()");
		String CacheFileName = WSJNI.GetCacheFile4Sync();
		logger.debug("D2DSync - CacheFileName is " + CacheFileName);

		return CacheFileName;
	}

	@Override
	public long DeleteCacheFile4Sync()
	{
		return WSJNI.DeleteCacheFile4Sync();
	}

	@Override
	public String GetReSyncData()
	{
		System.out.print("NativeFacadeImpl.GetReSyncData() Enter ...\n");

		String contentXML = "";
		String cacheFileName = WSJNI.GetReSyncCacheFile();

		System.out.print("NativeFacadeImpl.GetReSyncData() cache file name is " + cacheFileName + "\n");
		if ( cacheFileName.isEmpty())
			return contentXML;
		else if ( cacheFileName.equals("ERROR") )
		{
			contentXML = "ERROR";
			return contentXML;
		}
		else
		{
			contentXML = "";

			BufferedReader br;

			try {
				br = new BufferedReader(new InputStreamReader(new FileInputStream(cacheFileName),"utf-8"));

				String data = null;
				while((data = br.readLine())!=null)
				{
					contentXML += data;
				}

				br.close();
			} catch (FileNotFoundException e) {
				logger.error(e.getMessage() == null ? e : e.getMessage());
				contentXML = "ERROR";
			} catch (IOException e) {
				logger.error(e.getMessage() == null ? e : e.getMessage());
				contentXML = "ERROR";
			}

			WSJNI.DeleteCacheFile4Sync();

			return contentXML;
		}
	}

	@Override
	public String GetD2DSysFolder()
	{
		return WSJNI.GetD2DSysFolder();
	}

	@Override
	public int cancelVMJob(long jobID,String vmIdentification) throws Throwable {
		logger.debug("cancelVMJob:" + vmIdentification);
		int returnValue = WSJNI.cancelVMJob(jobID,vmIdentification);
		logger.debug("return value:" + returnValue);
		return returnValue;
	}

	@Override
	public List<JBackupVM> getBackupVMList(String destination,String domain, String username,
			String password) throws ServiceException {
		List<JBackupVM> backupVMList = new ArrayList<JBackupVM>();
		logger.debug("getBackupVMList start");
		logger.debug("destination:" + destination);
		logger.debug("domain:" + domain);
		logger.debug("username:" + username);
		Lock lock = RemoteFolderConnCache.getInstance().getLockByPath(destination);
		try {
			if(lock != null) {
				logger.debug("Getting lock to " + destination + ", domain:" + domain + ", userName:" + username);
				lock.lock();
				logger.debug("locked to " + destination);
			}
			long ret = WSJNI.GetBackupVMList(destination, domain,username, password, backupVMList);
			if(ret !=0){
				logger.debug("getBackupVMList return:" + ret);
			}
			RemoteFolderConnCache.getInstance().addConnections(destination,domain, username, password, false);
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}finally {
			if(lock != null) {
				lock.unlock();
				logger.debug("release lock");
			}
		}
		return backupVMList;
	}
	
	@Override
	public JBackupVM getBackupVM(String destination,String domain, String username,
			String password) throws ServiceException {
		List<JBackupVM> backupVMList = new ArrayList<JBackupVM>();
		logger.debug("getBackupVM start");
		logger.debug("destination:" + destination);
		logger.debug("domain:" + domain);
		logger.debug("username:" + username);
		Lock lock = RemoteFolderConnCache.getInstance().getLockByPath(destination);
		try {
			if(lock != null) {
				logger.debug("Getting lock to " + destination + ", domain:" + domain + ", userName:" + username);
				lock.lock();
				logger.debug("locked to " + destination);
			}
			long ret = WSJNI.GetBackupVM(destination, domain,username, password, backupVMList);
			if(ret !=0){
				logger.debug("getBackupVM return:" + ret);
				return null;
			}
			RemoteFolderConnCache.getInstance().addConnections(destination,domain, username, password, false);
			if(backupVMList.size() > 0){
				return backupVMList.get(0);
			}
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}finally {
			if(lock != null) {
				lock.unlock();
				logger.debug("release lock");
			}
		}
		return null;
	}
	
	@Override
	public List<JVAppChildBackupVMRestorePointWrapper> getVAppChildBackupVMsAndRecoveryPoints(String vAppDestination, int vAppSessionNumer, String domain, String username, String password)
			throws ServiceException {
		List<JVAppChildBackupVMRestorePointWrapper> childBackupVMRPList = new ArrayList<JVAppChildBackupVMRestorePointWrapper>();
		List<JBackupVM> childBackupVMList = new ArrayList<JBackupVM>();
		Map<String, JRestorePoint> childRestorePointMap = new HashMap<String, JRestorePoint>();

		logger.debug("getVAppChildBackupVMsAndRecoveryPoint start");
		logger.debug("vAppDestination:" + vAppDestination);
		logger.debug("vAppSessionNumer:" + vAppSessionNumer);
		logger.debug("domain:" + domain);
		logger.debug("username:" + username);
		Lock lock = RemoteFolderConnCache.getInstance().getLockByPath(vAppDestination);
		try {
			if (lock != null) {
				logger.debug("Getting lock to " + vAppDestination + ", domain:" + domain + ", userName:" + username);
				lock.lock();
				logger.debug("locked to " + vAppDestination);
			}
			long ret = WSJNI.getVAppChildBackupVMsAndRecoveryPoints(vAppDestination, vAppSessionNumer, domain,
					username, password, childBackupVMList, childRestorePointMap);
			if (ret != 0) {
				logger.debug("getBackupVM return:" + ret);
				return null;
			}
			RemoteFolderConnCache.getInstance().addConnections(vAppDestination, domain, username, password, false);
			
			int listSize = childBackupVMList.size();
			int mapSize = childRestorePointMap.size(); 
			if (listSize > 0 && mapSize > 0 && listSize == mapSize) {
				for (JBackupVM backupVM : childBackupVMList) {
					String instanceUUID = backupVM.getInstanceUUID();
					processChildVMName(backupVM);
					JRestorePoint restorePoint = childRestorePointMap.get(instanceUUID);
					childBackupVMRPList.add(new JVAppChildBackupVMRestorePointWrapper(backupVM, restorePoint));
				}
			} else {
				logger.warn("The size of backupVMList = " + listSize);
				logger.warn("The size of recoveryPointMap = " + mapSize);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		} finally {
			if (lock != null) {
				lock.unlock();
				logger.debug("release lock");
			}
		}
		return childBackupVMRPList;
	}
	
	private void processChildVMName(JBackupVM backupVM) {
		String oldVMName = backupVM.getVmName();
		if (oldVMName == null || oldVMName.trim().isEmpty()) {
			return;
		}
		
		int index = oldVMName.lastIndexOf("(");
		if (index > 0 && index < oldVMName.length() - 1) {
			String newName = oldVMName.substring(0, index);
			if (newName == null || newName.trim().isEmpty()) {
				return;
			}
			backupVM.setVmName(newName.trim());
		}
		
	}
	

	@Override
	public boolean checkVMDestination(String destination,String domain,String username,String password) throws ServiceException {
		logger.debug("checkVMDestination start");
		logger.debug("destination:" + destination);
		logger.debug("domain:" + domain);
		logger.debug("username:" + username);
		boolean ret = false;
		Lock lock = RemoteFolderConnCache.getInstance().getLockByPath(destination);
		try {
			if(lock != null) {
				logger.debug("Getting lock to " + destination + ", domain:" + domain + ", userName:" + username);
				lock.lock();
				logger.debug("locked to " + destination);
			}
			ret = WSJNI.AFCheckVMDestination(destination, domain, username, password);
			RemoteFolderConnCache.getInstance().addConnections(destination,domain, username, password, false);
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}finally {
			if(lock != null) {
				lock.unlock();
				logger.debug("release lock");
			}
		}
		return ret;
	}

	@Override
	public List<JDisk> getBackupVMDisk(String destination, String subPath,
			String domain, String username, String password)
			throws ServiceException {
		logger.debug("getBackupVMDisk start");
		logger.debug("destination:" + destination);
		logger.debug("domain:" + domain);
		logger.debug("username:" + username);
		Lock lock = RemoteFolderConnCache.getInstance().getLockByPath(destination);
		try {
			if(lock != null) {
				logger.debug("Getting lock to " + destination + ", domain:" + domain + ", userName:" + username);
				lock.lock();
				logger.debug("locked to " + destination);
			}
			String netConnUser = username;
			if (!StringUtil.isEmptyOrNull(domain)) {
				if (!domain.trim().endsWith("\\"))
					domain += "\\";
				netConnUser = domain + username;
			}
			NetConn(netConnUser, password, destination);
			List<JDisk> diskList = new ArrayList<JDisk>();
			long ret = WSJNI.AFGetBackupVMDisk(destination, subPath, domain, username, password, diskList);
			logger.debug("getBackupVMDisk return:" + ret);
			RemoteFolderConnCache.getInstance().addConnections(destination,domain, username, password, false);
			return diskList;
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}finally {
			if(lock != null) {
				lock.unlock();
				logger.debug("release lock");
			}
		}
	}

	@Override
	public JBackupInfoSummary GetBackupInfoSummary(String destination, String domain, String userName, String pwd, boolean onlyDestCapacity,String foldername) throws Throwable {
		logger.debug("GetBackupInfoSummary(String) - start");
		logger.debug("destination:"+destination);
		logger.debug("domain:"+domain);
		logger.debug("userName:"+userName);
		logger.debug("onlyDestCapacity:"+onlyDestCapacity);

		JBackupInfoSummary result = new JBackupInfoSummary();
		result.setBackupInfoList(new ArrayList<JBackupInfo>());
		result.setDestinationInfo(new JBackupDestinationInfo());
		Lock lock = RemoteFolderConnCache.getInstance().getLockByPath(destination);
		int jniResult;
		try {
			if(lock != null) {
				logger.debug("Getting lock to " + destination + ", domain:" + domain + ", userName:" + userName);
				lock.lock();
				logger.debug("locked to " + destination);
			}
			jniResult = WSJNI.GetBackupInfoSummary(destination, domain, userName, pwd, result, onlyDestCapacity,foldername);
			RemoteFolderConnCache.getInstance().addConnections(destination,domain, userName, pwd, false);
		}
		finally {
			if(lock != null) {
				lock.unlock();
				logger.debug("release lock");
			}
		}


		result.setErrorCode(jniResult);

		if (logger.isDebugEnabled()){
			logger.debug(StringUtil.convertObject2String(result));
			logger.debug(StringUtil.convertList2String(result.getBackupInfoList()));
		}
		logger.debug("GetBackupInfoSummary(String) - end:"+jniResult);
		return result;
	}

	@Override
	public long createVMJobMonitor(String vmIdentification) {
		return WSJNI.createVMJobMonitor(vmIdentification);
	}

	@Override
	public JActivityLogResult getVMActivityLogs(int start, int count,String vmUUID) {
		logger.debug("getVMActivityLogs(int, int) - start");
		logger.debug("start:"+start);
		logger.debug("count:"+count);

		JActivityLogResult logResult = new JActivityLogResult();
		List<JActivityLog> logs = new ArrayList<JActivityLog>();
		logResult.setLogs(logs);

		int result = WSJNI.GetVMLogActivity(start, count, logResult,vmUUID);

		if (logger.isDebugEnabled()){
			logger.debug(StringUtil.convertObject2String(logResult));
			logger.debug(StringUtil.convertList2String(logResult.getLogs()));
		}
		logger.debug("getVMActivityLogs(int, int) - end:"+result);
		return logResult;
	}

	@Override
	public JActivityLogResult getVMJobActivityLogs(long jobNo, int start,
			int count,String vmUUID) {
		logger.debug("getVMJobActivityLogs(long, int, int) - start");
		logger.debug("jobNo:" + jobNo);
		logger.debug("start:"+start);
		logger.debug("count:"+count);

		JActivityLogResult logResult = new JActivityLogResult();
		List<JActivityLog> logs = new ArrayList<JActivityLog>();
		logResult.setLogs(logs);

		int result = WSJNI.GetVMJobLogActivity(jobNo, start, count, logResult,vmUUID);

		if (logger.isDebugEnabled()){
			logger.debug(StringUtil.convertObject2String(logResult));
			logger.debug(StringUtil.convertList2String(logResult.getLogs()));
		}
		logger.debug("getVMJobActivityLogs(int, int) - end:"+result);
		return logResult;
	}

	@Override
	public JActivityLogResult GetJobLogActivityForVM(long jobNo, int start,
			int count,String vmUUID) {
		logger.debug("GetJobLogActivityForVM(long, int, int) - start");
		logger.debug("jobNo:" + jobNo);
		logger.debug("start:"+start);
		logger.debug("count:"+count);

		JActivityLogResult logResult = new JActivityLogResult();
		List<JActivityLog> logs = new ArrayList<JActivityLog>();
		logResult.setLogs(logs);

		int result = WSJNI.GetJobLogActivityForVM(jobNo, start, count, logResult,vmUUID);

		if (logger.isDebugEnabled()){
			logger.debug(StringUtil.convertObject2String(logResult));
			logger.debug(StringUtil.convertList2String(logResult.getLogs()));
		}
		logger.debug("GetJobLogActivityForVM(int, int) - end:"+result);
		return logResult;
	}

	@Override
	public void deleteVMActivityLog(int year, int month, int day, int hour, int minute, int second,String vmUUID) {
		logger.debug("deleteVMActivityLog(Date) - start");
		if (logger.isDebugEnabled()){
			logger.debug("year:"+year);
			logger.debug("month:"+month);
			logger.debug("day:"+day);
			logger.debug("hour:"+hour);
			logger.debug("minute:"+minute);
			logger.debug("second:"+second);
		}

		int result = WSJNI.DeleteVMLogActivity(year, month, day, hour, minute, second,vmUUID);

		logger.debug("deleteVMActivityLog(Date) - end:"+result);
	}
	@Override
	public String getVDDKVersion(){
		logger.debug("Get VDDK Version - start");
		
		String version = WSJNI.GetVDDKVersion();
			
		logger.debug("Get VDDK version -end" + version);
		return version;
	}
	@Override
	public String getVIXVersion(){
		logger.debug("Get VIX Version - start");
		
		String version = WSJNI.GetVIXVersion();
			
		logger.debug("Get VIX version -end" + version);
		return version;
	}
	
	@Override
	public long NetCancel(String remotePath, boolean isForce) throws ServiceException {
//			String path, String domain, String user,
//			String pwd, boolean force
			return disconnectRemotePath(remotePath,"","","",isForce);
	}

	@Override
	public long NetConn(String username, String password, String remoteName) throws ServiceException {
		String domain = "";
		if(username!=null){
			int indx = username.indexOf('\\');
			if (indx > 0) {
				domain = username.substring(0, indx);
				username = username.substring(indx + 1);
			}
		}
		JNetConnInfo connInfo = new JNetConnInfo();
		connInfo.setSzDir(remoteName);
		connInfo.setSzDomain(domain);
		connInfo.setSzUsr(username);
		connInfo.setSzPwd(password);
		long dwRet = WSJNI.AFCreateConnection(connInfo);
		if (dwRet != 0) {
			String msg = WSJNI.AFGetErrorMsg(dwRet);
			throw new ServiceException(msg,
					FlashServiceErrorCode.BackupConfig_ERR_DEST_WINSYSMSG);
		}
		return dwRet;
	}

	@Override
	public int NetConnWithLocal(String username, String password,
			String localName, String remoteName) {
		//return WSJNI.NetConnWithLocal(username, password, localName, remoteName);
		throw new RuntimeException("NetConnWithLocal  is not implemented yet!");
	}

	@Override
	public long GetAllBackupDestinations(String destDir,
			List<String> destinations) {
		return WSJNI.GetAllBackupDestinations(destDir, destinations);

	}

	@Override
	public long getReplicatedSessions(String destDir,
			List<String> destinations, String serverName, String serverPort, String username, String password) {
		return WSJNI.GetReplicatedSessions(destDir, destinations,serverName, serverPort,username,password);
	}

	@Override
	public long HyperVRep(HyperVRepParameterModel prams) {
		return WSJNI.HyperVRep(prams);
	}

	@Override
	public long ConvertVHD2VMDK(String afGuid,String vhdFileName,String moref,
								String hostname,String username,String password,
								String diskURL,int vddkPort,VMwareConnParams exParams, int diskType, 
								String snapMoref ,String jobID,long blockSize, int nBackupDescType,
								String NetConnUserName, String NetConnPwd,List<Long> errorCode){
		return VMWareJNI.ConvertVHD2VMDK(afGuid,vhdFileName, moref, hostname, username,
										 password, diskURL,vddkPort,exParams, diskType, snapMoref ,
										 jobID,blockSize,nBackupDescType,NetConnUserName, NetConnPwd, errorCode);
	}
	
	@Override
	public long UpdateDiskSigViaNBD(String afGuid,String vhdFileName,String moref, String hostname,String username,String password,
			String diskURL,int vddkPort,VMwareConnParams exParams, int diskType,String snapMoref,String jobID, int nBackupDescType,String netConnUserName,String netConnPwd){

		return VMWareJNI.UpdateDiskSigViaNBD(afGuid,vhdFileName, moref, hostname, username, password, diskURL, vddkPort, exParams, diskType, snapMoref, jobID, nBackupDescType, netConnUserName, netConnPwd);

	}

	@Override
	public long D2D2VmdkSmartCopy(String afGuid,String d2dFilePathBegin, String d2dFilePathEnd,
		    String moref, String hostname,String username,String password,
		    String diskURL,int vddkPort,VMwareConnParams exParams, int diskType,String snapMoref,String jobID,
		    long blockSize, int nBackupDescType, String NetConnUserName, String NetConnPwd,List<Long> errorCode){
		return VMWareJNI.D2D2VmdkSmartCopy(afGuid,d2dFilePathBegin, d2dFilePathEnd, moref, hostname, 
				username, password, diskURL, vddkPort,exParams, diskType, snapMoref, jobID,blockSize,nBackupDescType,
				NetConnUserName, NetConnPwd,errorCode);
	}
	
	@Override
	public String GetVMDKSignature(String afguid, String esxHost,String esxUser,String esxPassword,
			String moreInf,int port,VMwareConnParams exParams, String snapMoref, String vmdkUrl,String jobID){
		return VMWareJNI.GetVMDKSignature(afguid, esxHost, esxUser, esxPassword, moreInf,
										  port, exParams, snapMoref,vmdkUrl,jobID);
	}

	@Override
	public long SetVMDKGeometry(String esxHost,String esxUser,String esxPassword,
								 String moreInf,int port,VMwareConnParams exParams, String vmdkUrl, long volOffset, String JobID, String afguid){

		return VMWareJNI.SetVMDKGeometry(esxHost, esxUser, esxPassword, moreInf, port,exParams, vmdkUrl, volOffset, JobID, afguid);

	}

	@Override
	public long DoVMWareDriverInjection(String esxServer,String username, String password,
			  							String morefId, int adminPort, VMwareConnParams exParams, List<String> vmdkUrls,
			  							VMwareVolumeInfo volumeInfo,String hostname,String failoverMode,
			  							String key,String value,String jobID, String afguid, boolean isUEFI, String drzFilePath){
		return VMWareJNI.DoVMWareDriverInjection(esxServer, username, password, morefId, adminPort, exParams,
												 vmdkUrls, volumeInfo, hostname, failoverMode,key,value,jobID, afguid, isUEFI, drzFilePath);
	}
	
	@Override
	public long DriverInjectSingleVMDK(String esxServer,String username, String password,
			   						   String morefId, int adminPort,VMwareConnParams exParams, String vmdkUrl,
			   						   String hostname,String failoverMode,
			   						   String key,String value,String jobID, String afguid){
		
		return VMWareJNI.DriverInjectSingleVMDK(esxServer, username, password, morefId, adminPort,exParams, 
												 vmdkUrl, hostname, failoverMode, key, value, jobID, afguid);		
	}

	@Override
	public String GetGuestID(){
		return VMWareJNI.GetGuestID();
	}

	@Override
	public void InstallVMwareTools(){
		VMWareJNI.InstallVMwareTools();
	}

	@Override
	public Map<String, String> GetHostAdapterList(){
		try{
			return WSJNI.GetHostAdapterList();
		}catch (Exception e) {
			return null;
		}
	}
	@Override
	public void EnableHostDHCP(String adapterName){
		try{
			WSJNI.EnableHostDHCP(adapterName);
		}catch (Exception e) {
		}

	}

	@Override
	public void EnableHostStatic(String adapterName, List<String> ipAddresses, List<String> vMasks){
		try{
			WSJNI.EnableHostStatic(adapterName, ipAddresses, vMasks);
		}catch (Exception e) {
		}
	}

	@Override
	public void EnableHostDNS(String adapterName){
		try {
			WSJNI.EnableHostDNS(adapterName);
		} catch (Exception e) {
		}

	}
	@Override
	public void SetHostDNSDomain(String adapterName,String dnsDomain){
		try {
			WSJNI.SetHostDNSDomain(adapterName, dnsDomain);
		} catch (Exception e) {
		}

	}
	@Override
	public void SetHostGateways(String adapterName, List<String> gateways, List<Integer> costMetrics){
		try {
			WSJNI.SetHostGateways(adapterName, gateways, costMetrics);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	@Override
	public void SetHostDNSServerSearchOrder(String adapterName, List<String> vDNSServerSearchOrder){
		try {
			WSJNI.SetHostDNSServerSearchOrder(adapterName, vDNSServerSearchOrder);
		} catch (Exception e) {
		}
	}

	@Override
	public int GetMaxCPUSForHypervVm(long handle) throws ServiceException{
		try {
			return HyperVJNI.GetMaxCPUSForHypervVm(handle);
		} catch (Exception e) {
			logger.error("Failed to get hyperv cpu count." + e.getMessage());
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public long GetMaxRAMForHypervVm(long handle) throws ServiceException{
		try {
			return HyperVJNI.GetMaxRAMForHypervVm(handle);
		} catch ( Exception e) {
			logger.error("Failed to get hyperv memory size." + e.getMessage());
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public long GetHyperVSystemInfo(long handle, JHyperVSystemInfo hyperVSysInfo) throws ServiceException{
		
		try {
			return HyperVJNI.GetHyperVSystemInfo(handle, hyperVSysInfo);
		} catch ( Exception e) {
			logger.error("Failed to get hyperv system info." + e.getMessage());
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public long OpenHypervHandle(String host, String user, String password) throws ServiceException{
		try {
			return HyperVJNI.OpenHypervHandle(host, user, password);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}

	}

	@Override
	public void CloseHypervHandle(long handle) throws ServiceException{
		try {
			HyperVJNI.CloseHypervHandle(handle);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}

	}

	@Override
	public Map<String,String> GetHyperVVmList(long handle)	throws ServiceException{
		try{
			return HyperVJNI.GetVmList(handle);
		}catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public String GetHyperVVmNotes(long handle, String vmGuid)	throws ServiceException{
		try{
			return HyperVJNI.GetVmNotes(handle, vmGuid);
		}catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public int GetHyperVVmState(long handle, String vmGuid) throws ServiceException{
		try {
			return HyperVJNI.GetVmState(handle, vmGuid);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public Map<String,String> GetHyperVVmSnapshots(long handle, String vmGuid) throws ServiceException{
		try {
			return HyperVJNI.GetVmSnapshots(handle, vmGuid);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public int PowerOnHyperVVM(long handle, String vmGuid) throws ServiceException{
		try{
			return HyperVJNI.PowerOnVM(handle, vmGuid);
		}catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public int ShutdownHyperVVM(long handle, String vmGuid) throws ServiceException{
		try{
			return HyperVJNI.ShutdownVM(handle, vmGuid);
		}catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}


	@Override
	public String TakeHyperVVmSnapshot(long handle, String vmGuid, String snapshotName,
			String snapshotNotes) throws ServiceException{
		try{
			return HyperVJNI.TakeVmSnapshot(handle, vmGuid, snapshotName, snapshotNotes);
		}catch (Exception e) {
			// TODO: handle exception
			logger.error(e.getMessage());
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public boolean HAIsHostOSGreaterEqual(int dwMajor,int dwMinor,short servicePackMajor,
			 short servicePackMinor) throws ServiceException
	{
		try {
			return WSJNI.HAIsHostOSGreaterEqual(dwMajor, dwMinor, servicePackMajor, servicePackMinor);
		} catch (WSJNIException e) {
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public int CheckFolderCompressAttribute(String folderPath) throws ServiceException
	{
		try {
			return WSJNI.CheckFolderCompressAttribute(folderPath);
		} catch (WSJNIException e) {
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public int CheckVolumeCompressAttribute(String volumePath) throws ServiceException
	{
		try {
			return WSJNI.CheckVolumeCompressAttribute(volumePath);
		} catch (WSJNIException e) {
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public int GetOnlineDisksAndVolumes(Vector<Disk> vecDisk, Vector<com.ca.arcflash.failover.model.Volume> vecVolume) throws ServiceException
	{
		try {
			return WSJNI.GetOnlineDisksAndVolumes(vecDisk, vecVolume);
		} catch (WSJNIException e) {
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	@Override
	public Vector<Disk> getOnlineDisks() throws ServiceException{
		try {
			Vector<Disk> disks = WSJNI.GetOnlineDisks();
			return disks;
		} catch (WSJNIException e) {
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public Vector<com.ca.arcflash.failover.model.Volume> GetOnlineVolumes() throws ServiceException{
		try {
			Vector<com.ca.arcflash.failover.model.Volume> volumes = WSJNI.GetOnlineVolumes();
			return volumes;
		} catch (Exception e) {
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	@Override
	public Vector<JHostNetworkConfig> GetHostNetworkConfig() throws ServiceException{
		try{
			Vector<JHostNetworkConfig> networkConfigs=WSJNI.GetHostNetworkConfig();
			return networkConfigs;
		}catch (Exception e) {
			// TODO: handle exception
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	@Override
	public short GetHostProcessorArchitectural() throws ServiceException{
		try{
			short cpuArchitectual=WSJNI.GetHostProcessorArchitectural();
			return cpuArchitectual;
		}catch (Exception e) {
			// TODO: handle exception
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public long GetOfflinecopySize(String rootDest) throws ServiceException {
		try{
			long offlineCopySize = WSJNI.GetOfflinecopySize(rootDest);
			return offlineCopySize;
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	public int AdjustVolumeBootCodeForHyperV(String pwszVmGuid, String pwszSnapGuid) throws ServiceException{
		int result = 1;
		try {
			result = WSJNI.AdjustVolumeBootCodeForHyperV(pwszVmGuid, pwszSnapGuid);
		} catch (Exception e) {
			throw new ServiceException(e.getMessage());
		}
		return result;
	}

	@Override
	public void CancelReplicationForHyperV(String jobId) throws ServiceException {
		int ret = -1;
		try{
			ret = WSJNI.CancelReplicationForHyperV(jobId);
			logger.debug("CancelReplicationForHyperV return " + ret);
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}

		if(ret != 0) {
			logger.error("CancelReplicationForHyperV return " + ret);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public void stopHAServerProxy() throws ServiceException {
		int ret = -1;
		try{
			ret = WSJNI.StopHAServerProxy();
		}
		catch (Exception e) {
			logger.error("return code=" + ret);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}

		if(ret != 0)
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
	}

	@Override
	public void CancelReplicationForVMware(String jobId)
			throws ServiceException {

		boolean ret = false;
		try{
			ret = WSJNI.CancelReplicationForVMware(jobId);
			logger.debug("CancelReplicationForVMware return " + ret);
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}

		if(!ret) {
			logger.error("CancelReplicationForVMware return " + ret);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	@Override
	public int OnlineDisks() throws ServiceException {
		int result = 0;
		try{
			result = WSJNI.OnlineDisks();
			logger.debug("OnlineDisks return " + result);
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		return result;
	}
	

	@Override
	public String GetLastSnapshotForHyper(long handle, String vmGuid)
			throws ServiceException {
		try{
			String ret = HyperVJNI.GetLastSnapshot(handle, vmGuid);
			return ret;
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}

	}

	@Override
	public int generateAdrconfigure(String dest) throws ServiceException {

		try {
			int ret = WSJNI.GenerateAdrconfigure(dest);
			return ret;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}	

	@Override
	public int generateIVMAdrconfigure(String dest, String vmuuid, String vmname) throws ServiceException {

		try {
			int ret = WSJNI.GenerateIVMAdrconfigure(dest, vmuuid, vmname);
			return ret;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}	
	
	@Override
	public String GenerateAdrInfoC() throws ServiceException {

		try {
			return WSJNI.GenerateAdrInfoC();
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}

	}
		
	@Override
	public String GetDrInfoLocalCopyPathForSnapNow_HyperV(String vmname,String vmuuid) throws ServiceException{

		try {
			String fullPath = WSJNI.GetDrInfoLocalCopyPath(vmname, vmuuid);
			return fullPath;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public String GetAdrInfoCLocalCopyPathForSnapNow_HyperV(String vmname,String vmuuid) throws ServiceException{

		try {
			String fullPath = WSJNI.GetAdrInfoCLocalCopyPath(vmname, vmuuid);
			return fullPath;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public String GetDestSubRoot(HyperVRepParameterModel parameterModel) throws ServiceException{
		try {
			String subFolder = WSJNI.GetDestSubRoot(parameterModel);
			return subFolder;
		} catch (WSJNIException e) {
			logger.error(e.getMessage(),e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService,e.getErrorCode()+"");
		}
	}

	// chefr03: SMART_COPY_BITMAP
	@Override
	public int CreateSessionBitmap(String srcSessionDest, String destSessionDest, int nBackupDescType) throws ServiceException {
		try {
			int	ret = WSJNI.CreateSessionBitmap(srcSessionDest, destSessionDest, nBackupDescType);
			return ret;
		} catch (WSJNIException e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService, e.getErrorCode() + "");
		}
	}
	// chefr03: SMART_COPY_BITMAP

	@Override
	public List<String> getIpAddressFromDns(String hostName){
		List<String> ipList = new ArrayList<String>();
		try{
			int ret = WSJNI.GetIpAddressFromDns(hostName, ipList);
			if(ret != 0) {
				String msg = WSJNI.AFGetErrorMsg(ret);
				logger.error("Fails to refresh the ip address of host " + hostName + ". System error:" + msg);
			}
			return ipList;
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			return ipList;
		}
	}

		@Override
	public List<JMsgRec> getGRTCatalogItems(String filepath, long lowSelfid, long highSelfid) {
		logger.debug("getGRTCatalogItems(String, long, long) - start");
		logger.debug("filepath:" + filepath);
		logger.debug("lowSelfid:" + lowSelfid);
		logger.debug("highSelfid:" + highSelfid);

		if (filepath == null) {
			filepath = "";
		}

		long handle = WSJNI.MsgOpenCatalogFile(filepath);

		ArrayList<JMsgRec> retArr = new ArrayList<JMsgRec>();
		long ret = WSJNI.MsgGetChildren(handle, lowSelfid, highSelfid, retArr);
		if (ret == 0) {
			for (JMsgRec msg : retArr) {
				msg.setObjDate(BackupConverterUtil.dosTime2UTC(msg.getObjDate()));

				// get children count
				JRWLong childrenCnt = new JRWLong();
				long ret2 = WSJNI.MsgGetChildrenCount(handle, msg.getLowObjSelfid(),
						msg.getHighObjSelfid(), childrenCnt);
				logger.debug("MsgGetChildrenCount ret :" + ret2);
				logger.debug("MsgGetChildrenCount childrenCnt :" + childrenCnt.getValue());
				if (ret2 == 0) {
					msg.setChildrenCount(childrenCnt.getValue());
				}
			}
		}

		if (handle > 0) {
			WSJNI.CloseGetCatalog(handle);
		}

		if (logger.isDebugEnabled())
			logger.debug(StringUtil.convertList2String(retArr));
		logger.debug("getGRTCatalogItems(String, long, long) - end");
		return retArr;
	}

	@Override
	public String getMsgCatalogPath(String dbIdentify, String backupDestination,
			long sessionNumber, long subSessionNumber) {
		logger.debug("getMsgCatalogPath(String, String, long, long) - start");
		logger.debug("dbIdentify:" + dbIdentify);
		logger.debug("backupDestination:" + backupDestination);
		logger.debug("sessionNumber:" + sessionNumber);
		logger.debug("subSessionNumber:" + subSessionNumber);

		String ret = WSJNI.GetMsgCatalogPath(dbIdentify, backupDestination,
				sessionNumber, subSessionNumber);

		logger.debug("msgCatalogPath:" + ret);
		logger.debug("getMsgCatalogPath(String, String, long, long) - end");
		return ret;
	}

	@Override
	public PagedGRTCatalogItem getPagedGRTCatalogItems(String catPath,
			long lowSelfID, long highSelfID, int start, int size)
			throws ServiceException {
		logger
				.debug("getPagedGRTCatalogItems(String, long, long, int, int) - begin");
		logger.debug(catPath);
		logger.debug(lowSelfID);
		logger.debug(highSelfID);
		logger.debug(start);
		logger.debug(size);

		PagedGRTCatalogItem pagedItem = new PagedGRTCatalogItem();
		long handle = WSJNI.OpenCatalog(catPath);
		this.handleCatalogError(handle, catPath);
		JRWLong totalCnt = new JRWLong();
		long ret = WSJNI.MsgGetChildrenCount(handle, lowSelfID, highSelfID,
				totalCnt);

		logger.debug("ret:" + ret);

		if (ret == 0) {
			long total = totalCnt.getValue();
			logger.debug("total:" + total);

			pagedItem.setTotal(total);

			if (total > 0) {
				JRWLong realCnt = new JRWLong();
				ArrayList<JMsgRec> retArr = new ArrayList<JMsgRec>();
				long dwRet = WSJNI.MsgGetChildrenEx(handle, lowSelfID,
						highSelfID, start, size, realCnt, retArr);
				logger.debug("dwRet:" + dwRet);

				List<GRTCatalogItem> l = new ArrayList<GRTCatalogItem>();
				if (dwRet == 0) {
					for (JMsgRec rec : retArr) {
						ret = WSJNI.MsgGetChildrenCount(handle, rec.getLowObjSelfid(), rec.getHighObjSelfid(),
								totalCnt);
						if (ret == 0) {
							rec.setChildrenCount(totalCnt.getValue());
						}else{
							logger.error("dwRet:" + ret);
						}
						l.add(BrowserService.getInstance()
								.convert2GRTCatalogItem(rec));
						}
				}

				pagedItem.setGrtCataItems(l.toArray(new GRTCatalogItem[0]));
			}
		}
		WSJNI.CloseGetCatalog(handle);

		logger.debug("getPagedGRTCatalogItems(String, long, long, int, int) - end");
		return pagedItem;
	}

//	@Override
//	public PagedGRTCatalogItem browseGRTCatalog(GRTBrowsingContext context) throws ServiceException
//	{
//		logger.debug("begin >>>>>>>>>>");
//
//		PagedGRTCatalogItem pagedItem = new PagedGRTCatalogItem();
//
//		if (context != null)
//		{
//			logger.debug("input - GRTBrowsingContext: " + context.toLogString());
//
//			long dwRet = 0;
//			JRWLong realCnt = new JRWLong();
//			ArrayList<JMsgRec> retArr = new ArrayList<JMsgRec>();
//
//			if (context.getCatalogFileHandle() == 0)
//			{
//				long handle = WSJNI.MsgOpenCatalogFile(context.getCatalogFilePath());
//				context.setCatalogFileHandle(handle);
//
//				JRWLong totalCnt = new JRWLong();
//
//				dwRet = WSJNI.MsgGetChildrenByFilterFirst(
//							context.getCatalogFileHandle(),
//							1,//context.getResultBufferIndex(),
//							context.getLselfid(),
//							context.getHselfid(),
//							context.getQueryString(),
//							context.getRequestSize(),
//							realCnt, totalCnt, retArr);
//
//				pagedItem.setTotal(totalCnt.getValue());             // return the total
//				pagedItem.setHandle(context.getCatalogFileHandle()); // return the handle
//				
//				// if it is browsing mailboxes or mails
//				if (!context.isFolderOnly() /*&& !context.isMailOnly()*/ )
//				{
//					// if it has keyword, 
//					if (context.getFilterKeyword() != null || context.getSearchKeyword() != null)
//					{
//						// get the total count without filter
//						JRWLong totalCountWithoutFilter = new JRWLong();
//						dwRet = WSJNI.MsgGetChildrenCount(handle, context.getLselfid(), context.getHselfid(),
//								totalCountWithoutFilter);
//						
//						pagedItem.setTotalWithoutFilter(totalCountWithoutFilter.getValue());
//					}
//					else
//					{						
//						pagedItem.setTotalWithoutFilter(totalCnt.getValue());
//					}
//				}
//			}
//			else
//			{
//				dwRet = WSJNI.MsgGetChildrenByFilterNext(
//					context.getCatalogFileHandle(),
//					1,//context.getResultBufferIndex(),
//					context.getRequestStart(),
//					context.getRequestSize(),
//					realCnt, retArr);
//
//				pagedItem.setTotal(context.getTotal());              // return the total again
//				pagedItem.setHandle(context.getCatalogFileHandle()); // return the handle again
//				pagedItem.setTotalWithoutFilter(context.getTotalWithoutFilter()); // return the total count without filter again
//			}
//
//			List<GRTCatalogItem> l = new ArrayList<GRTCatalogItem>();
//			if (dwRet == 0)
//			{
//				for (JMsgRec rec : retArr)
//				{
//					l.add(BrowserService.getInstance().convert2GRTCatalogItem(rec));
//				}
//			}
//
//			pagedItem.setGrtCataItems(l.toArray(new GRTCatalogItem[0]));
//
//			logger.debug("output - PagedGRTCatalogItem:" + pagedItem.toLogString());
//
//			// this is not paging, close the handle after using it
//			if (context.getRequestSize() == 0)
//			{
//				WSJNI.CloseGetCatalog(context.getCatalogFileHandle());
//				pagedItem.setHandle(0);
//			}
//		}
//
//		logger.debug("end <<<<<<<<<<");
//		return pagedItem;
//	}
	
	@Override
	public PagedGRTCatalogItem browseGRTCatalog(GRTBrowsingContext context) throws ServiceException
	{
		PagedGRTCatalogItem pagedItem = new PagedGRTCatalogItem();
		if (context == null){
			return pagedItem;
		}
		
		long handle = 0;
		try{
			logger.debug("input - GRTBrowsingContext: " + context.toLogString());

			long dwRet = 0;
			JRWLong realCnt = new JRWLong();
			ArrayList<JMsgRec> retArr = new ArrayList<JMsgRec>();

			handle = WSJNI.MsgOpenCatalogFile(context.getCatalogFilePath());

			JRWLong totalCnt = new JRWLong();

			dwRet = WSJNI.MsgGetChildrenByFilterFirst(
					handle,
					1,// context.getResultBufferIndex(),
					context.getLselfid(), context.getHselfid(),
					context.getQueryString(), context.getRequestSize(),
					realCnt, totalCnt, retArr);
			
			if(context.getRequestStart() > 0)	
			{
				retArr.clear();
				
				dwRet = WSJNI.MsgGetChildrenByFilterNext(
						handle,
						1,
						context.getRequestStart(),
						context.getRequestSize(),
						realCnt,
						retArr);
			}	

			pagedItem.setTotal(totalCnt.getValue()); // return the total

			// if it is browsing mailboxes or mails
			if (!context.isFolderOnly() /* && !context.isMailOnly() */) {
				// if it has keyword,
				if (context.getFilterKeyword() != null
						|| context.getSearchKeyword() != null) {
					// get the total count without filter
					JRWLong totalCountWithoutFilter = new JRWLong();
					dwRet = WSJNI.MsgGetChildrenCount(handle,
							context.getLselfid(), context.getHselfid(),
							totalCountWithoutFilter);

					pagedItem.setTotalWithoutFilter(totalCountWithoutFilter
							.getValue());
				} else {
					pagedItem.setTotalWithoutFilter(totalCnt.getValue());
				}
			}

			List<GRTCatalogItem> l = new ArrayList<GRTCatalogItem>();
			if (dwRet == 0)
			{
				for (JMsgRec rec : retArr)
				{
					l.add(BrowserService.getInstance().convert2GRTCatalogItem(rec));
				}
			}

			pagedItem.setGrtCataItems(l.toArray(new GRTCatalogItem[0]));
			
			logger.debug("output - PagedGRTCatalogItem:" + pagedItem.toLogString());

		}catch(Exception e){
			logger.error(e);
		}finally{
			WSJNI.CloseGetCatalog(handle);
		}
		return pagedItem;
	}

	@Override
	public void closeBrowseGRTCatalog(GRTBrowsingContext context) throws ServiceException {
//		logger.debug("begin. Handle=" + (context == null ? "" : context.getCatalogFileHandle()));
//
//		if (context != null)
//		{
//			WSJNI.CloseGetCatalog(context.getCatalogFileHandle());
//		}
//
//		logger.debug("end");
	}

	@Override
	public long d2dExCheckUser(String domain,
			String user, String password)
			throws ServiceException {
		logger.debug("d2dExCheckUser(String, String, String) - begin");
		logger.debug(domain);
		logger.debug(user);
		//logger.debug(password);

		long ret = WSJNI.D2DExCheckUser(domain, user, password);

		logger.debug("ret:" + ret);
		logger.debug("d2dExCheckUser(String, String, String) - end");
		return ret;
	}

	public JSearchResult searchMsgNext(SearchContext context) {
		logger.debug("searchNext(SearchContext) - start");
		if (logger.isDebugEnabled())
			logger.debug(StringUtil.convertObject2String(context));

		JSearchResult returnJSearchResult = WSJNI.FindNextMsgCatalog(context);

		if (logger.isDebugEnabled()) {
			logger.debug(StringUtil.convertObject2String(returnJSearchResult));
			if (returnJSearchResult != null)
				logger.debug(StringUtil.convertList2String(returnJSearchResult
						.getMsgDetail()));
		}
		logger.debug("searchNext(SearchContext) - end");
		return returnJSearchResult;

	}

	@Override
	public String aoeGetOrganizationName(String strUser, String strPassword)
	{
		logger.debug("aoeGetOrganizationName() - start");

		String ret = WSJNI.AOEGetOrganizationName(strUser, strPassword);

		if (ret == null || ret.isEmpty())
		{
			logger.error("aoeGetOrganizationName() ret:" + ret);
		}
		else
		{
			logger.debug("ret:" + ret);
		}

		logger.debug("aoeGetOrganizationName() - end");

		return ret;
	}

	@Override
	public List<JExchangeDiscoveryItem> aoeGetServers(String strUser, String strPassword) throws ServiceException {
		logger.debug("aoeGetServers() - start");

		ArrayList<JExchangeDiscoveryItem> retArr = new ArrayList<JExchangeDiscoveryItem>();

		WSJNI.AOEGetServers(retArr, strUser, strPassword);

		if (logger.isDebugEnabled())
			logger.debug(StringUtil.convertList2String(retArr));
		logger.debug("aoeGetServers() - end");

		return retArr;
	}

	@Override
	public List<JExchangeDiscoveryItem> aoeGetStorageGroups(String dn, String strUser, String strPassword) throws ServiceException {
		logger.debug("aoeGetStorageGroups(String) - start");
		logger.debug("dn:" + dn);

		ArrayList<JExchangeDiscoveryItem> retArr = new ArrayList<JExchangeDiscoveryItem>();

		if (dn != null)
		{
			WSJNI.AOEGetStorageGroups(dn, retArr, strUser, strPassword);
		}

		if (logger.isDebugEnabled())
			logger.debug(StringUtil.convertList2String(retArr));
		logger.debug("aoeGetStorageGroups(String) - end");

		return retArr;
	}

	@Override
	public List<JExchangeDiscoveryItem> aoeGetEDBs(String dn, String strUser, String strPassword) throws ServiceException {
		logger.debug("aoeGetEDBs(String) - start");
		logger.debug("dn:" + dn);

		ArrayList<JExchangeDiscoveryItem> retArr = new ArrayList<JExchangeDiscoveryItem>();

		if (dn != null)
		{
			WSJNI.AOEGetEDBs(dn, retArr, strUser, strPassword);
		}

		if (logger.isDebugEnabled())
			logger.debug(StringUtil.convertList2String(retArr));
		logger.debug("aoeGetEDBs(String) - end");

		return retArr;
	}

	@Override
	public List<JExchangeDiscoveryItem> aoeGetMailboxes(String dn, String strUser, String strPassword) throws ServiceException {
		logger.debug("aoeGetMailboxes(String) - start");
		logger.debug("dn:" + dn);

		ArrayList<JExchangeDiscoveryItem> retArr = new ArrayList<JExchangeDiscoveryItem>();

		if (dn != null)
		{
			WSJNI.AOEGetMailboxes(dn, retArr, strUser, strPassword);
		}

		if (logger.isDebugEnabled())
			logger.debug(StringUtil.convertList2String(retArr));
		logger.debug("aoeGetMailboxes(String) - end");

		return retArr;
	}

	@Override
	public long aoeCheckServiceStatus(String serviceName)
	{
		long returnValue = WSJNI.AOECheckServiceStatus(serviceName);
		logger.debug("aoeCheckServiceStatus:" + serviceName + returnValue);
		return returnValue;
	}

	@Override
	public long createDesktopINI(String backupDestination) throws Throwable
	{
		logger.debug("createDesktopINI()");

		long nRet = WSJNI.AHDesktopFile(backupDestination);

		logger.debug("AHDesktopFile ret="+nRet);

		return nRet;
	}

	@Override
	public long catalogJob(JCatalogJob job) {
		logger.debug("catalogJob(JCatalogJob) - start");

		if (logger.isDebugEnabled())
		{
			logger.debug(StringUtil.convertObject2String(job));
		}

		long returnlong = WSJNI.AFCatalogJob(job);

		logger.debug("catalogJob(JCatalogJob) - end");
		return returnlong;
	}

	@Override
	public boolean isCatalogJobRunning(String backupDestination, long sessionNumber, long subSessionNumber)
	{
		boolean returnValue = WSJNI.AFIsCatalogJobRunning(backupDestination, sessionNumber, subSessionNumber);
		logger.debug("isCatalogJobRunning:" + returnValue);
		return returnValue;
	}

	@Override
	public boolean isCatalogJobInQueue(long queueType, String backupDestination, long sessionNumber, long subSessionNumber, String vmInstanceUUID)
	{
		boolean returnValue = WSJNI.AFIsCatalogJobInQueue(queueType, backupDestination, sessionNumber, subSessionNumber, vmInstanceUUID);
		logger.debug("isCatalogJobInQueue:" + returnValue);
		return returnValue;
	}

	@Override
	public long validateCatalogFileExist(String dbIdentify, String backupDestination, long sessionNumber,
			long subSessionNumber)
	{
		logger.debug("validateCatalogFileExist(String, String, long, long) - start");
		logger.debug("dbIdentify:" + dbIdentify);
		logger.debug("backupDestination:" + backupDestination);
		logger.debug("sessionNumber:" + sessionNumber);
		logger.debug("subSessionNumber:" + subSessionNumber);

		long catalogFileEixst = 0; // catalog not exist

		// check if the server which generated the session and the current D2D server have the same OS, otherwise it cannot be restored
		long sameOS = WSJNI.AFCheckGrtSession(backupDestination, sessionNumber);		
		if (sameOS != 1)
		{
			catalogFileEixst = 4; // OS mismatch, the GRT restore will fail.
		}
		else
		{
			String catalogPath = WSJNI.GetMsgCatalogPath(dbIdentify, backupDestination, sessionNumber, subSessionNumber);
			logger.debug("msgCatalogPath:" + catalogPath);
			if (catalogPath != null && !catalogPath.isEmpty())
			{
				long handle = WSJNI.MsgOpenCatalogFile(catalogPath);
				if (handle != 0)
				{
					catalogFileEixst = 1; // catalog exists
					WSJNI.CloseGetCatalog(handle);
					return catalogFileEixst;
				}
			}
			
			if (isCatalogJobRunning(backupDestination, sessionNumber, subSessionNumber))
			{
				catalogFileEixst = 2; // catalog is generating
			}
			//romove the check for job in queue, just check it when submit catalog and start it directly if it's already in queue.
			/*else if(isCatalogJobInQueue(CatalogService.REGULAR_JOB, backupDestination, sessionNumber, subSessionNumber) ||
					isCatalogJobInQueue(CatalogService.ONDEMAND_JOB, backupDestination, sessionNumber, subSessionNumber))
			{
				catalogFileEixst = 3; // catalog job is in queue
			}*/
			/*else
			{
				String catalogPath = WSJNI.GetMsgCatalogPath(dbIdentify, backupDestination, sessionNumber, subSessionNumber);
				logger.debug("msgCatalogPath:" + catalogPath);
	
				if (catalogPath != null && !catalogPath.isEmpty())
				{
					long handle = WSJNI.MsgOpenCatalogFile(catalogPath);
					if (handle != 0)
					{
						catalogFileEixst = 1; // catalog exists
						WSJNI.CloseGetCatalog(handle);
					}
				}
			}*/
		}

		

		logger.debug("validateCatalogFileExist(String, String, long, long) - end");
		return catalogFileEixst;
	}

	@Override
	public boolean isVolumeMounted(String volName)
	{
		logger.debug("isVolumeMounted(volName) - start");
		boolean bIsMounted = false;

		if (volName != null && !volName.isEmpty())
		{
			JRWLong skipped = new JRWLong();
			WSJNI.AFCGRTSkipDisk(volName, skipped);
			bIsMounted = skipped.getValue() > 0L;
		}

		logger.debug("isVolumeMounted(String) - end. volName" + volName + " bIsMounted=" + bIsMounted);
		return bIsMounted;
	}

	@Override
	public long CheckSessVerByNo(String destination, long sessionNumber)
	{
		logger.debug("CheckSessVerByNo(String, long) - start. destination=" + destination + " sessionNumber=" + sessionNumber);

		long result = 0;

		if (destination != null && !destination.isEmpty())
		{
			result = WSJNI.CheckSessVerByNo(destination, sessionNumber);
		}

		logger.debug("CheckSessVerByNo(String, long) - end. result=" + result);
		return result;
	}

	public void RebootSystem(boolean force) throws ServiceException {
		try{
			WSJNI.RebootSystem(force);
		}catch (WSJNIException e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService, e.getErrorCode() + "");
		}

	}

	@Override
	public String getHAConfigurationFileURL(String lastRepDest, String fileName) throws ServiceException{
		try {
			return WSJNI.FindHAConfigurationFileURL(lastRepDest, fileName);
		} catch (WSJNIException e) {
			logger.error(e.getMessage());
			logger.error(e.getErrorCode());
			throw new ServiceException(e.getMessage(), e.getErrorCode()+"");
		}

	}

	@Override
	public String getHAConfigurationFileURLByVMGUID(String vmGUID, String fileName) throws ServiceException{
		try {
			return WSJNI.FindHAConfigurationFileURLByVMGUID(vmGUID, fileName);
		} catch (WSJNIException e) {
			logger.error(e.getMessage());
			logger.error(e.getErrorCode());
			throw new ServiceException(e.getMessage(), e.getErrorCode()+"");
		}

	}

	@Override
	// Delete bitmaps for specified session and all older sessions
	public int DeleteSessionBitmap(String sessionDest, String sessionName) throws ServiceException {
		try {
			int ret = WSJNI.DeleteSessionBitmap(sessionDest, sessionName);
			return ret;
		} catch (WSJNIException e){
			logger.error(e.getMessage(), e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService, e.getErrorCode() + "");
		}
	}

	@Override
	// Get the session name list for bitmaps
	public int GetSessionBitmapList(String sessionDest, List<String> vBitmapList) throws ServiceException {
		try {
			int ret = WSJNI.GetSessionBitmapList(sessionDest, vBitmapList);
			return ret;
		} catch (WSJNIException e){
			logger.error(e.getMessage(), e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService, e.getErrorCode() + "");
		}
	}
	// chefr03: SMART_COPY_BITMAP
	
	@Override
	public int DetectAndRemoveObsolteBitmap(String sessionDest,String beginSession, String endSession) {
		try {
			return WSJNI.DetectAndRemoveObsolteBitmap(sessionDest, beginSession, endSession);
		}catch(WSJNIException e) {
			logger.error(e.getMessage(), e);
			return -1;
		}
	}

	public String GetD2DActiveLogTransFileXML()
	{
		return WSJNI.GetD2DActiveLogTransFileXML();
	}

	public long DelD2DActiveLogTransFileXML()
	{
		return WSJNI.DelD2DActiveLogTransFileXML();
	}

	public String GetFullD2DActiveLogTransFileXML()
	{
		return WSJNI.GetFullD2DActiveLogTransFileXML();
	}

	@Override
	public boolean IsFirstD2DSyncCalled() {
		// TODO Auto-generated method stub

		return WSJNI.IsFirstD2DSyncCalled();
	}

	@Override
	public void MarkFirstD2DSyncCalled() {
		// TODO Auto-generated method stub
		WSJNI.MarkFirstD2DSyncCalled();
	}

	@Override
	public ArchiveJobMinitor getArchiveJobStatus(long jobType)
			throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArchiveSession getArchiveSession() throws ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getArchiveDestinationVolumes(String hostName,ArchiveDestinationConfig in_archiveDestConfig)
			throws ServiceException {
		try {
			logger.debug("getArchiveDestinationVolumes(String,String,String,String) - start");
			logger.debug("hostName:" + hostName);

			/*JArchiveDestinationConfig jDestConfig = new JArchiveDestinationConfig();
			jDestConfig.setbArchiveToDrive(in_archiveDestConfig.isbArchiveToDrive());
			jDestConfig.setStrArchiveToDrivePath(in_archiveDestConfig.getStrArchiveToDrivePath());
			jDestConfig.setStrArchiveDestinationUserName(in_archiveDestConfig.getStrArchiveDestinationUserName());
			jDestConfig.setStrArchiveDestinationPassword(in_archiveDestConfig.getStrArchiveDestinationPassword());

			jDestConfig.setbArchiveToCloud(in_archiveDestConfig.isbArchiveToCloud());
			jDestConfig.setJcloudConfig(ConvertCloudConfig(in_archiveDestConfig.getCloudConfig()));*/

			ArrayList<String> volumeList = new ArrayList<String>();
			long result = WSJNI.AFGetArchiveDestinationVolumes(hostName, in_archiveDestConfig, volumeList);

			if(result != 0)
			{
				String msg = WSJNI.AFGetErrorMsg(result);
				throw new ServiceException(msg, FlashServiceErrorCode.Common_ErrorOccursInService);
			}
			return volumeList;
		}catch(ServiceException se) {
			throw se;
		}
		catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public long getArchivedVolumeHandle(String strVolume) {
		long handle = WSJNI.GetArchivedVolumeHandle(strVolume);
		return handle;
	}

	@Override
	public List<JArchiveCatalogDetail> getArchiveCatalogItems(
			long in_VolumeHandle, String in_CatalogFilePath, long lIndex, long lCount) throws Throwable
			{
		logger.debug("getArchiveCatalogItems(String, long) - start");
		logger.debug("volumeHandle:" + in_VolumeHandle);
		logger.debug("CatalogFilePath:" + in_CatalogFilePath);

		if (in_CatalogFilePath == null)
		{
			in_CatalogFilePath = "";
		}
		ArrayList<JArchiveCatalogDetail> returnList = new ArrayList<JArchiveCatalogDetail>();
		try{
			logger.info("browseArchiveCatalogChildrenEx called."+lIndex+"##"+lCount);
			WSJNI.browseArchiveCatalogChildrenEx(in_VolumeHandle,in_CatalogFilePath,lIndex,lCount,returnList);

		}catch(Exception ex)
		{
			logger.error(ex.getMessage() == null ? ex : ex.getMessage());
		}
		if (returnList != null && returnList.size() > 0) {

			for (JArchiveCatalogDetail detail : returnList) {
//				JRWLong childrenCnt = new JRWLong();
//				long ret = WSJNI.GetArchiveChildrenCount(in_VolumeHandle, detail.getFullPath(), childrenCnt);
//				logger.debug("ret:" + ret);
//				logger.debug("childrenCnt:" + childrenCnt.getValue());
				detail.setVolumeHandle(in_VolumeHandle);
//				if (ret != 0) {
//					detail.setChildrenCount(childrenCnt.getValue());
//				}
			}
	//		WSJNI.CloseGetCatalog(handle);
		}

	//	out_archiveCatalogItems = returnList;

		if (logger.isDebugEnabled())
			logger.debug(StringUtil.convertList2String(returnList));
		logger.debug("getArchiveCatalogItems(String, long) - end");
		return returnList;
	}
	
	
	
	

	@Override
	public List<JArchiveCatalogDetail> getArchiveCatalogItems(
			long in_VolumeHandle, String in_CatalogFilePath) throws Throwable
			{
		logger.debug("getArchiveCatalogItems(String, long) - start");
		logger.debug("volumeHandle:" + in_VolumeHandle);
		logger.debug("CatalogFilePath:" + in_CatalogFilePath);

		if (in_CatalogFilePath == null)
		{
			in_CatalogFilePath = "";
		}
		ArrayList<JArchiveCatalogDetail> returnList = new ArrayList<JArchiveCatalogDetail>();
		try{
			WSJNI.browseArchiveCatalogChildren(in_VolumeHandle,in_CatalogFilePath,returnList);

		}catch(Exception ex)
		{
			logger.error(ex.getMessage() == null ? ex : ex.getMessage());
		}
		if (returnList != null && returnList.size() > 0) {

			for (JArchiveCatalogDetail detail : returnList) {
//				JRWLong childrenCnt = new JRWLong();
//				long ret = WSJNI.GetArchiveChildrenCount(in_VolumeHandle, detail.getFullPath(), childrenCnt);
//				logger.debug("ret:" + ret);
//				logger.debug("childrenCnt:" + childrenCnt.getValue());
				detail.setVolumeHandle(in_VolumeHandle);
//				if (ret != 0) {
//					detail.setChildrenCount(childrenCnt.getValue());
//				}
			}
	//		WSJNI.CloseGetCatalog(handle);
		}

	//	out_archiveCatalogItems = returnList;

		if (logger.isDebugEnabled())
			logger.debug(StringUtil.convertList2String(returnList));
		logger.debug("getArchiveCatalogItems(String, long) - end");
		return returnList;
	}
	
	@Override
	public long GetArchiveChildrenCount(long in_lVolumeHandle, String strPath,
			JRWLong in_childrenCnt) {
		JRWLong childrenCnt = new JRWLong();
		long ret = 0L;
		try {
			
			ret = WSJNI.GetArchiveChildrenCount(in_lVolumeHandle, strPath,
					childrenCnt);
			in_childrenCnt.setValue(childrenCnt.getValue());
		} catch (Exception e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
			ret = -1;
		}
		return ret;
	}

	@Override
	public long archiveRestore(ArchiveJobScript in_archiveRestoreJob) throws Throwable {
		logger.debug("archiveRestore(JJobScript) - start");

		long returnlong = WSJNI.AFArchiveRestore(in_archiveRestoreJob);

		logger.debug("archiveRestore(JJobScript) - end");
		return returnlong;
	}
	
	@Override
	public long archiveCatalogSync(ArchiveJobScript in_archiveCatalogSyncJob) throws Throwable {
		logger.debug("archive Catalog Sync job(JJobScript) - start");

		long returnlong = WSJNI.AFArchiveCatalogSync(in_archiveCatalogSyncJob);

		logger.debug("archive Catalog Sync job(JJobScript) - end");
		return returnlong;
	}

	@Override
	public long CanArchiveJobBeSubmitted(JArchiveJob out_JArchiveJob)
			throws Throwable {
		logger.debug("CanArchiveJobBeSubmitted(JArchiveJob) - start");

		//out_JArchiveJob = new JArchiveJob();
		long ret = WSJNI.CanArchiveJobBeSubmitted(out_JArchiveJob);

		logger.debug("CanArchiveJobBeSubmitted - end");
		return ret;
	}
	
	@Override
	public long CanArchiveSourceDeleteJobBeSubmitted(JArchiveJob out_jArchiveJob)throws Throwable{
		logger.debug("can archive source delete job be submitted - start");
		long ret = WSJNI.CanArchiveSourceDeleteJobBeSubmitted(out_jArchiveJob);
		logger.debug("can archive source delete job be submitted - end");
		return ret;
	}

	@Override
	public long archive(ArchiveJobScript archiveJob) throws Throwable {
		logger.debug("archive(JJobScript) - start");

		long returnlong = WSJNI.AFArchive(archiveJob);

		logger.debug("archive(JJobScript) - end");
		return returnlong;
	}

	@Override
	public long purge(ArchiveJobScript jobScript) throws Throwable {
		logger.info("purge(JJobScript) - start");

		long returnlong = WSJNI.AFArchivePurge(jobScript);

		logger.info("purge(JJobScript) - end");
		return returnlong;
	}

	@Override
	public long getArchivableFilesInformation(String archiveSourcePoliciesFilePath,	List<ArchiveFileItem> out_filesList) throws Throwable {
		logger.debug("getArchivableFilesInformation(strFilepath) - start");

		out_filesList = new ArrayList<ArchiveFileItem>();
		long returnlong = WSJNI.AFGetArchivableFilesInformation(archiveSourcePoliciesFilePath, out_filesList);

		logger.debug("getArchivableFilesInformation - end");
		return returnlong;
	}

	@Override
	public List<JArchiveCatalogDetail> getArchiveCatalogItemsBySearch(String in_FileName,String in_HostName, String in_Searchpath,
			ArchiveDestinationConfig in_ArchiveDestConfig,long in_lSearchOptions,long lIndex,long lCount)throws Throwable {
		logger.debug("getArchiveCatalogItemsBySearch - start");

		ArrayList<JArchiveCatalogDetail> returnList = new ArrayList<JArchiveCatalogDetail>();

		try{
			long ret = WSJNI.searchArchiveCatalogChildren(in_FileName,
					in_HostName, in_Searchpath, in_ArchiveDestConfig,
					in_lSearchOptions, lIndex, lCount, returnList);
			 
			if(ret == ERROR_NETWORK_UNREACHABLE) {
				throw new ServiceException(FlashServiceErrorCode.Browser_Archive_Path_Invalid, 
						new Object[]{});
			}

		}catch(ServiceException e) {
			throw e;
		}catch(Exception ex)
		{
			throw ex;
		}

		if (logger.isDebugEnabled())
			logger.debug(StringUtil.convertList2String(returnList));
		logger.debug("getArchiveCatalogItemsBySearch - end");
		return returnList;
	}

	@Override
	public List<ArchiveJobInfo> GetArchiveJobsInfo(JArchiveJob in_ArchiveJob) {
		List<ArchiveJobInfo> archiveJobsList = new ArrayList<ArchiveJobInfo>();

		long retValue = WSJNI.GetArchiveJobsInfo(in_ArchiveJob,archiveJobsList);

		if(retValue == 0)
			return archiveJobsList;

		return null;
	}

	@Override
	public boolean IsArchiveJobRunning() throws Throwable {
		return WSJNI.IsArchiveJobRunning();
	}
	
	@Override
	public boolean IsArchiveCatalogSyncJobRunning() throws Throwable {
		return WSJNI.IsArchiveCatalogSyncJobRunning();
	}

	@Override
	public boolean IsArchivePurgeJobRunning() throws Throwable {
		return WSJNI.IsArchivePurgeJobRunning();
	}

	@Override
	public boolean IsArchiveRestoreJobRunning() throws Throwable {
		return WSJNI.IsArchiveRestoreJobRunning();
	}
	
	@Override
	public boolean IsFileArchiveJobRunning() throws Throwable {
		return WSJNI.IsFileArchiveJobRunning();
	}

	@Override
	public String GetArchiveDNSHostName() throws Throwable {


		return WSJNI.GetArchiveDNSHostName();

		//return strHostName;
	}

	@Override
	public String GetArchiveDNSHostSID() throws Throwable {
		return WSJNI.GetArchiveDNSHostSID();
	}
	
	@Override
	public ArchiveDestinationDetailsConfig getArchiveChangedDestinationDetails(
			ArchiveDestinationConfig inArchiveDestConfig) throws Throwable {
		
		ArchiveDestinationDetailsConfig DestinationChangeDetails = new ArchiveDestinationDetailsConfig();
		
		//long ret = WSJNI.GetArchiveDestinationChangeDetails(inArchiveDestConfig,DestinationChangeDetails);
		
		long ret = WSJNI.GetLastArchiveCatalogUpdateTime(inArchiveDestConfig,DestinationChangeDetails);
		
		long lastModifiedTime = DestinationChangeDetails.getLastSyncTime();
		// Filetime Epoch is JAN 01 1601
		// java date Epoch is January 1, 1970
		// so take the number and subtract java Epoch:
		long javaModifyTime = lastModifiedTime - 0x19db1ded53e8000L;
		// convert UNITS from (100 nano-seconds) to (milliseconds)
		javaModifyTime /= 10000;
		// the specified number of milliseconds since the standard base
		// time known as "the epoch", namely January 1, 1970, 00:00:00 GMT.
		DestinationChangeDetails.setLastSyncDate((new Date(javaModifyTime)));
		
		return DestinationChangeDetails;
	}
	

	
    @Override
	public List<JCatalogInfo> AFSCheckCatalogExist(String destination, long sessionNumber)
	 throws ServiceException 
	{
		logger.debug("AFSCheckCatalogExist(String, long) - start. destination=" + destination + " sessionNumber=" + sessionNumber);
		
		ArrayList<JCatalogInfo> retArr = new ArrayList<JCatalogInfo>();
		
		if (destination != null && !destination.isEmpty())
		{
			WSJNI.AFSCheckCatalogExist(destination, sessionNumber, retArr);			
		}		
			
		if (logger.isDebugEnabled())
		{
			logger.debug(StringUtil.convertList2String(retArr));
		}
		
		logger.debug("AFSCheckCatalogExist(String, long) - end.");
		
		
		return retArr;
	}

	@Override
	public long DeleteAllVmInfoTransFile() {
		// TODO Auto-generated method stub
		return WSJNI.DeleteAllVmInfoTransFile();
	}

	@Override
	public long DeleteVmInfoTransFile() {
		// TODO Auto-generated method stub
		return WSJNI.DeleteVmInfoTransFile();
	}

	@Override
	public String GetAllVmInfo4Trans() {
		// TODO Auto-generated method stub
		return WSJNI.GetAllVmInfo4Trans();
	}

	@Override
	public String GetCachedVmInfo4Trans() {
		// TODO Auto-generated method stub
		return WSJNI.GetCachedVmInfo4Trans();
	}

	@Override
	public long DeleteArchiveCacheFileTrans() {
		// TODO Auto-generated method stub
		return WSJNI.DeleteArchiveCacheFileTrans();
	}

	@Override
	public String GetArchiveCacheFileName4Trans() {
		// TODO Auto-generated method stub
		String CacheFileName = WSJNI.GetArchiveCacheFileName4Trans();

		return CacheFileName;
	}	
	
		@Override
	public String[] getCloudBuckets(ArchiveCloudDestInfo cloudDestInfo)
			throws ServiceException {
		List<String> cloudBuckets = new ArrayList<String>();
		long errorCode = 0L;
		try {
			errorCode = WSJNI.getCloudBuckets(cloudDestInfo, cloudBuckets);		
			logger.info("error code in getCloudBuckets( )" + errorCode);
		} catch (WSJNIException se) {
			logger.error(se.getMessage(), se);
			throw new ServiceException(
					FlashServiceErrorCode.Common_ErrorOccursInService);
		} catch (Exception t) {
			logger.error(t.getMessage(), t);
			throw new ServiceException(
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		
		if(errorCode != 0L)
		{
			return new String[]{"Error_"+errorCode};
		}
		else
			return (String[]) cloudBuckets.toArray(new String[0]);
		
/*		if (cloudBuckets != null)
			return (String[]) cloudBuckets.toArray(new String[0]);
		else	
			return new String[]{"Error_"+errorCode};*/
	}

	@Override
	public String[] getCloudRegions(ArchiveCloudDestInfo cloudDestInfo)
			throws ServiceException {
		List<String> regions = new ArrayList<String>();
		long errorCode = 0L;
		try {
			errorCode = WSJNI.getCloudRegions(cloudDestInfo, regions);
			logger.info("error code in getCloudRegions( )" + errorCode);
		} catch (WSJNIException se) {
			logger.error(se.getMessage(), se);
			throw new ServiceException(
					FlashServiceErrorCode.Common_ErrorOccursInService);
		} catch (Exception t) {
			logger.error(t.getMessage(), t);
			throw new ServiceException(
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		
		if(errorCode != 0L)
		{
			return new String[]{"Error_"+errorCode};
		}
		else
			return (String[]) regions.toArray(new String[regions.size()]);
		
/*		if (regions != null)
			return (String[]) regions.toArray(new String[regions.size()]);
		else	
			return new String[]{"Error_"+errorCode};*/
	}

	@Override
	public String getRegionForBucket(ArchiveCloudDestInfo cloudDestInfo)
			throws ServiceException {
		String region = null;
		JRWLong retValue = new JRWLong();
		try {
			region = WSJNI.getRegionForBucket(cloudDestInfo,retValue);
			
			if(retValue.getValue() != 0L)//success
			{
				region = "Error_" + retValue.getValue(); 
			}
			
			StringBuffer strMessage = new StringBuffer();
			strMessage.append("error code in getRegionForBucket()");
			strMessage.append(retValue.getValue());
			
			logger.info(strMessage);
		} catch (WSJNIException se) {
			logger.error(se.getMessage(), se);
			throw new ServiceException(
					FlashServiceErrorCode.Common_ErrorOccursInService);
		} catch (Exception t) {
			logger.error(t.getMessage(), t);
			throw new ServiceException(
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		return region;
	}

	@Override
	public long testConnection(ArchiveCloudDestInfo cloudDestInfo)
			throws ServiceException {
		
		long connectionStatus = 0L;
		try {
			connectionStatus = WSJNI.testConnection(cloudDestInfo);
			logger.info("error code in testConnection( )" + connectionStatus);
		} catch (WSJNIException se) {
			logger.error(se.getMessage(), se);
			throw new ServiceException(
					FlashServiceErrorCode.Common_ErrorOccursInService);
		} catch (Exception t) {
			logger.error(t.getMessage(), t);
			throw new ServiceException(
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		return connectionStatus;
	}

	@Override
	public String getCACloudStorageKey(ArchiveCloudDestInfo cloudDestInfo,String userName, String password) throws ServiceException{
		String result=null;
		try {
			ArrayList<String> newStorageKey=new ArrayList<String>();
			int ret = WSJNI.getCACloudStorageKey(cloudDestInfo,userName,password,newStorageKey);
			if(ret!=0){
				logger.error("error code in getCACloudStorageKey: " + ret);
				result="Error_"+ret;
			}else{
				result=newStorageKey.get(0);
				logger.info("get CACloudStorageKey successfully!"+ result);
			}
		} catch (WSJNIException se) {
			logger.error(se.getMessage(), se);
			throw new ServiceException(
					FlashServiceErrorCode.Common_ErrorOccursInService);
		} catch (Exception t) {
			logger.error(t.getMessage(), t);
			throw new ServiceException(
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		return result;
	}
	
	@Override
	public String getGeminarePortalURL(ArchiveCloudDestInfo cloudDestInfo) throws ServiceException {
		String result=null;
		try {
			ArrayList<String> newStorageKey=new ArrayList<String>();
			int ret = WSJNI.getGeminarePortalURL(cloudDestInfo, newStorageKey);
			if(ret!=0){
				logger.error("error code in getGeminarePortalURL: " + ret);
				result="Error_"+ret;
			}else{
				result=newStorageKey.get(0);
				logger.info("getGeminarePortalURL return: "+ result);
			}
		} catch (WSJNIException se) {
			logger.error(se.getMessage(), se);
			throw new ServiceException(
					FlashServiceErrorCode.Common_ErrorOccursInService);
		} catch (Exception t) {
			logger.error(t.getMessage(), t);
			throw new ServiceException(
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		return result;
	}
	

	@Override
	public String updateStorageKey(ArchiveCloudDestInfo cloudDestInfo) throws ServiceException {
		String result=null;
		try {
			ArrayList<String> newStorageKey=new ArrayList<String>();
			long ret = WSJNI.updateStorageKey(cloudDestInfo,newStorageKey);
			if(ret!=0){
				logger.error("error code in WSJNI.updateCACloudStorageKey: " + ret);
				result="Error_"+ret;
			}else{
				result=newStorageKey.get(0);
				logger.info("WSJNI.updateCACloudStorageKey return: "+result);
			}
		} catch (WSJNIException se) {
			logger.error(se.getMessage(), se);
			throw new ServiceException(
					FlashServiceErrorCode.Common_ErrorOccursInService);
		} catch (Exception t) {
			logger.error(t.getMessage(), t);
			throw new ServiceException(
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		return result;
	}
	
	@Override
	public long verifyBucketName(ArchiveCloudDestInfo cloudDestInfo)
			throws ServiceException {
		long isBucketExits = 0;
		try {
			isBucketExits = WSJNI.verifyBucketName(cloudDestInfo);
			logger.info("error code in verifyBucketName( )" + isBucketExits);
		} catch (WSJNIException se) {
			logger.error(se.getMessage(), se);
			throw new ServiceException(
					FlashServiceErrorCode.Common_ErrorOccursInService);
		} catch (Exception t) {
			logger.error(t.getMessage(), t);
			throw new ServiceException(
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		return isBucketExits;

	}
	
	@Override
	public long enabledScheduledExport(boolean enabled) {
		return WSJNI.AFEnabledScheduledExport(enabled);
	}
	
	@Override
	public boolean checkScheduledExportInterval(int interval) {
		boolean ret = WSJNI.AFCheckShExpBackupNum(interval);
		
		return ret;
	}
	
	@Override
	public long addSucceedBackupNum() {
		long ret = WSJNI.AFAddSucceedBackupNum();
		
		return ret;
	}
	
	@Override
	public long getHyperVVMPathType(String vmPath) {
		return WSJNI.getHyperVVMPathType(vmPath);
	}
	
	@Override
	public void getHyperVDestInfo(List<String> vmPathList, HyperVDestinationInfo retObj) {
		HyperVJNI.getHyperVDestInfo(vmPathList, retObj);
	}
	
	@Override
	public void getSourceNodeSysInfo(SourceNodeSysInfo sourceNodeSysInfo) {
		WSJNI.getSourceNodeSysInfo(sourceNodeSysInfo);
	}
	
	@Override
	public String getSessPwdFromKeyMgmtBySessNum(long sessionNum,
			String destPath) {
		
		String sessionPwd = WSJNI.AFIGetSessPwdFromKeyMgmtBySessNum(sessionNum, destPath);
		return sessionPwd;
	
	}
	
	@Override
	public long MountVHDGetWinSysBootVol(long handle, String vhdFile,List<String> bootVolumePaths,List<String> systemVolume){
		int retry=2;
		int sleeptime=30000;
		long result=0;
		for(int i=0;i<=retry;i++){
			result = HyperVJNI.MountVHDGetWinSysBootVol(handle, vhdFile, bootVolumePaths, systemVolume);
			if(result!=0xE000010AL){
				break;
			}
			if(i<retry){
				logger.warn(i+" need retry MountVHDGetWinSysBootVol() "+vhdFile);
				try {
					HyperVJNI.UnmountVHD(handle, vhdFile);
				} catch (HyperVException e) {
					logger.warn(e.getMessage());
				}
				try {
					Thread.sleep(sleeptime);
				} catch (InterruptedException ex) {
					logger.warn(ex.getMessage());
					break;
				}
			}
		}
		return result;
		
	}
	
	@Override
	public long HAUnlockCTF2(long handle){
		long result = VMWareJNI.HAUnlockCTF2(handle);
		return result;
	}

	@Override
	public long GetVDDKRegistryKey(String keyName){
		long result = VMWareJNI.GetVDDKRegistryKey(keyName);
		return result;
	}
	
	@Override
	public long launchVSphereCatalogJob(long jobId, long type,
			String vmIndentification, String adminName, String adminPass) {
		return WSJNI.launchVSphereCatalogJob(jobId, type, vmIndentification, adminName, adminPass);
	}

	@Override
	public boolean checkRecoveryVMJobExist(String vmName,
			String esxServerName) {
		return WSJNI.AFCheckRecoveryVMJobExist(vmName, esxServerName);
	}
	@Override
	public boolean AFCheckFolderContainsBackup(String domain, String userName, String password, String destination) throws ServiceException {
		//create the connection
		Lock lock = null;
		boolean bRet = false;
		try {
			lock = RemoteFolderConnCache.getInstance().getLockByPath(destination);
			if(lock != null) {
				lock.lock();
			}
			this.NetConn(userName, password, destination);
			bRet = WSJNI.AFCheckFolderContainsBackup(destination);
		}finally {
			if(lock != null)
				lock.unlock();
			try {
				this.disconnectRemotePath(destination, domain, userName, password, false);
			}catch(Exception e) {
				logger.error("Disconnect " + destination + " failed");
			}
			
		}
		
		return bRet;
	}
	
	@Override
	public int getVMwareToolStatus() throws ServiceException{
		int mwareToolStatus = WSJNI.getVMwareToolStatus();
		logger.info("getVMwareToolStatus, return:" + mwareToolStatus);
		return mwareToolStatus;
	}

	@Override
	public long getVMApplicationStatus(String vmInstanceUUID,
			JApplicationStatus appStatus) {
		return WSJNI.getVMApplicationStatus(vmInstanceUUID, appStatus);
	}


	
	public CloudProviderInfo getCloudProviderInfo(long cloudProviderType)
			throws ServiceException {
		
		CloudProviderInfo providerInfo = new CloudProviderInfo();
		
		String vendorUrl = getVendorUrl(cloudProviderType);
		providerInfo.setUrl(vendorUrl);
		
		return providerInfo;	
		
	}
	
	private String getVendorUrl(long type)
	{
		if(type==0L)
			return "s3.amazonaws.com";
		if(type==1L)
			return "https://blob.core.windows.net";
		return "";
	}

	@Override
	public ArrayList validateEncryptionSettings(ArchiveJobScript in_archiveConfig) throws ServiceException {
		ArrayList errorList = new ArrayList();	
		JRWLong errorcode = new JRWLong();
		JRWLong cciErrorCode = new JRWLong();
		try
		{
			WSJNI.validateEncryptionSettings(in_archiveConfig,errorcode,cciErrorCode);
			errorList.add(errorcode.getValue());
			errorList.add(cciErrorCode.getValue());		
		} catch (WSJNIException se) {
			logger.error("JNI Excpetion"+se.getMessage(), se);
			throw new ServiceException(
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}  catch (Exception t) {
			logger.error(t.getMessage(), t);
			throw new ServiceException(
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
		return errorList;
	}
	
	
	
	@Override
	public String getSymbolicLinkActualPath(String sourcePath) throws ServiceException {
		String actualPath = "";
		JRWLong retValue = new JRWLong();
		try
		{
			logger.info("getSymbolicLinkActualPath - ErrorCode"+retValue.getValue());
			if(retValue.getValue() == 0)
			{
				actualPath = WSJNI.GetSymbolicLinkActualPath(sourcePath,retValue);			
			}
			else
			{
				throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
			}
			
		} catch (Exception t) {
			logger.error(t.getMessage(), t);
			throw new ServiceException(
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}		
		return actualPath;
	}
	@Override
	public LicInfo AFGetLicenseEx(boolean filterLicInfo) throws ServiceException {
		try {
			List<String> lst = this.getVolumes();
			LicInfo info = WSJNI.AFGetLicenseInfo(lst);
			logger.debug("getLicInfoEx() - end");
			return info;
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}	
	
	@Override
	public void cutAllRemoteConnections() throws ServiceException{
		ArrayList<JNetConnInfo> connections = new ArrayList<JNetConnInfo>();
		WSJNI.AFRetrieveConnections(connections);
		
		for(JNetConnInfo conn : connections) {
			Lock lock = RemoteFolderConnCache.getInstance().getLockByPath(conn.getSzDir());
			try {
				if(lock != null)
					lock.lock();
				WSJNI.AFCutConnection(conn, true);
				RemoteFolderConnCache.getInstance().clearConnection(conn.getSzDir());
			}finally {
				if(lock != null)
					lock.unlock();
			}
		}
	}

	@Override
	public JBackupVMOriginalInfo getBackupVMOriginalInfo(String destination,
			int sessionNum, String domain, String username, String password)
			throws ServiceException {
		logger.debug("getBackupVMOriginalInfo start");
		logger.debug("destination:" + destination);
		logger.debug("domain:" + domain);
		logger.debug("username:" + username);
		Lock lock = RemoteFolderConnCache.getInstance().getLockByPath(destination);
		try {
			if(lock != null) {
				logger.debug("Getting lock to " + destination + ", domain:" + domain + ", userName:" + username);
				lock.lock();
				logger.debug("locked to " + destination);
			}
			JBackupVMOriginalInfo originalInfo = new JBackupVMOriginalInfo();
			long ret = WSJNI.AFGetBackupVMOriginalInfo(destination, sessionNum, domain, username, password, originalInfo);
			logger.debug("getBackupVMOriginalInfo return:" + ret);
			RemoteFolderConnCache.getInstance().addConnections(destination,domain, username, password, false);
			return originalInfo;
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}finally {
			if(lock != null) {
				lock.unlock();
				logger.debug("release lock");
			}
		}
	}
	
	@Override
	public long deleteLicError(long licCode) {
		return WSJNI.AFDeleteLicError(licCode);
	}
	
	public boolean remoteDriverWithoutUserNamePwd(String path, String user, String pwd) throws ServiceException{
		if((getDestDriveType(path) == 4)
				&& (user == null || user.isEmpty() || pwd == null || pwd.isEmpty())) {
			return true;
		}else {
			return false;
		}
	}
	
	@Override
	public long CreateEvent(String eventName, boolean manualReset, boolean initialState) {
		long handle = WSJNI.CreateEvent(eventName, manualReset, initialState);
		if( handle == 0 ){
			long errorCode = WSJNI.GetLastError();
			throw new Error("Fail to create event object. ec=" + errorCode);
		}
		return handle;
	}

	@Override
	public void ResetEvent(long handle) {
		int ret = WSJNI.ResetEvent(handle);
		if( ret == 0 ){
			long errorCode = WSJNI.GetLastError();
			throw new Error("Fail to reset event object. ec=" + errorCode);
		}
	}

	@Override
	public void SetEvent(long handle) {
		int ret = WSJNI.SetEvent(handle);
		if( ret == 0 ){
			long errorCode = WSJNI.GetLastError();
			throw new Error("Fail to set event object. ec=" + errorCode);
		}
	}

	@Override
	public int WaitForSingleObject(long handle, long milliSeconds) {
		if( milliSeconds < 0 )
			milliSeconds = 0xFFFFFFFFL;
		long res = WSJNI.WaitForSingleObject(handle, milliSeconds);
		if( res == 0xFFFFFFFF ){
			long errorCode = WSJNI.GetLastError();
			throw new Error("WaitForSingleObject failed, ec=" + errorCode);
		}
		else if( res == 0x00000080L )
			throw new Error("WaitForSingleObject is abandoned.");
		else if( res == 0x00000102L )
			return 1;
		return 0;
	}

	@Override
	public long createMutex(boolean initiallyOwned, String mutexName) {
		long handle = WSJNI.createMutex(initiallyOwned, mutexName);
		if (handle == 0) {
			long errorCode = WSJNI.GetLastError();
			throw new Error("Failed to create mutex object. error code = " + errorCode);
		}
		
		return handle;
	}

	@Override
	public void releaseMutex(long handle) {
		boolean ret = WSJNI.releaseMutex(handle);
		if (!ret) {
			long errorCode = WSJNI.GetLastError();
			throw new Error("Fail to release mutex object. error code = " + errorCode);
		}
	}

	@Override
	public long getApplicationDetailsInESXVM(
			String esxServerName, String esxUserName, String esxPassword,
			String vmName, String vmVMX, String userName, String password) {
		
		List<JWriterInfo> appList = new ArrayList<JWriterInfo>();
		long result = WSJNI.getApplicationDetailsInESXVM(appList,
				esxServerName, esxUserName, esxPassword,
				vmName, vmVMX, userName, password);
		if(result == 0) {
			if(appList.size() > 0){
				for(JWriterInfo app : appList){
					String appName = app.getWszWriterName();
					if("SqlServerWriter".equals(appName)){
						result |= ID2DPFCService.PFC_CHECK_APP_HAS_SQLSERVER;
					}else if("Microsoft Exchange Writer".equals(appName)){
						result |= ID2DPFCService.PFC_CHECK_APP_HAS_EXCHANGE;
					}
				}
			}
		}
		
		return result;
	}

	@Override
	public long getVMInformation(JPFCVMInfo vmInfo, String esxServerName,
			String esxUserName, String esxPassword, String vmName,
			String vmVMX, String userName, String password) {
		return WSJNI.getVMInformation(vmInfo, esxServerName, esxUserName,
				esxPassword, vmName, vmVMX, userName, password);
	}

	private static final int DEFAULT_PRE_ALLOCATION = 10;
	
	@Override
	public long getPreAllocSpacePercent() throws ServiceException {
		JRWLong rwRet = new JRWLong();		
		long ret = WSJNI.AFIGetPreAllocSpacePercent(rwRet);
		if(ret == 0)
			return rwRet.getValue();
		else
			return DEFAULT_PRE_ALLOCATION;
	}

	@Override
	public long setPreAllocSpacePercent(long dwPercent) throws ServiceException {
		logger.info("Pre allocation value is " + dwPercent);
		long ret = WSJNI.AFISetPreAllocSpacePercent(dwPercent);
		
		if(ret != 0){
			logger.error("Failed to save pre allocation value");
		}
		return 0;
	}
	
	
	@Override
	 public boolean IsFirmwareuEFI(){
		return WSJNI.IsFirmwareuEFI();
	}
	
	@Override
	 public String GetRestorePointPath(String dest, String domain, String user,String pwd, String subPath){
		return WSJNI.GetRestorePointPath(dest, domain, user, pwd, subPath);
	}
	
    //D2D mount volume API begin
	@Override
    public List<JMountedRecoveryPointItem> getAllMountedRecoveryPointItems(){
		List<JMountedRecoveryPointItem> mountedItems = new ArrayList<JMountedRecoveryPointItem>();
    	long result = WSJNI.GetAllMountedRecoveryPointItems(mountedItems);
    	return mountedItems;
    }
	@Override
    public List<JMountedRecoveryPointItem> getMountedRecoveryPointItems(String dest,String domain, String user, String pwd, String subPath){
		if(logger.isDebugEnabled()){
			logger.debug("getMountedRecoveryItems: dest " + dest 
					+ " domain " + domain
					+ " user " + user
					+ " subPath " + subPath);
		}
		Lock lock = null;
		try {
			lock = RemoteFolderConnCache.getInstance().getLockByPath(dest);
			if(lock != null){
				lock.lock();
			}
			List<JMountedRecoveryPointItem> mountedItems = new ArrayList<JMountedRecoveryPointItem>();
	    	long result = WSJNI.GetMountedRecoveryPointItems(dest, domain, user, pwd, subPath, mountedItems);	    	
	    	return mountedItems;
		}finally {
			if(lock != null){
				lock.unlock();
			}
			logger.debug("End get mounted recovery point items");
		}
	}
	
	@Deprecated
	@Override
	public long mountRecoveryPointItem(String dest,String domain, String user, String pwd, String subPath, String volGUID,int encryptionType,String encryptPassword, String mountPath){
		if(logger.isDebugEnabled()) {
			logger.debug("MountRecoveryPointItem: dest " + dest
					+ " domain " + domain
					+ " user " + user
					+ " subPath " + subPath
					+ " volGUID " + volGUID
					+ " encryptionPassword " + (encryptPassword == null) 
					+ " mountPath " + mountPath);
		}
		Lock lock = null;
		try {
			lock = RemoteFolderConnCache.getInstance().getLockByPath(dest);
			if(lock != null){
				lock.lock();
			}
			long result = WSJNI.MountRecoveryPointItem(dest, domain, user, pwd, subPath, volGUID, encryptionType, encryptPassword, mountPath);
			return result;
		}finally {
			if(lock != null){
				lock.unlock();
			}
			logger.debug("End mountRecoveryPointItem");
		}
	}
	
	@Override
	public long mountRecoveryPointItem( JMountRecoveryPointParams jMntParams ){
		if(logger.isDebugEnabled()) {
			logger.debug("MountRecoveryPointItem: rps " + jMntParams.getRpsHostname()
					+ " datastore " + jMntParams.getDatastoreName()
					+ " dest " + jMntParams.getDest()
					+ " domain " + jMntParams.getDomain()
					+ " user " + jMntParams.getUser()
					+ " subPath " + jMntParams.getSubPath()
					+ " volGUID " + jMntParams.getVolGUID()
					+ " encryptionPassword " + ( jMntParams.getEncryptPassword() == null) 
					+ " mountPath " + jMntParams.getMountPath() );
		}
		Lock lock = null;
		try {
			lock = RemoteFolderConnCache.getInstance().getLockByPath( jMntParams.getDest() );
			if(lock != null){
				lock.lock();
			}
			long result = WSJNI.MountRecoveryPointItemEx( jMntParams );
			return result;
		}finally {
			if(lock != null){
				lock.unlock();
			}
			logger.debug("End mountRecoveryPointItem");
		}
	}
	
	@Override
	public long disMountRecoveryPointItem(String mountPath,int mountDiskSignature){
		long result = WSJNI.DisMountRecoveryPointItem(mountPath, mountDiskSignature);
		return result;
	}
	@Override
	public List<String> getAvailableMountDriveLetters(){
		List<String> avaliableDriveLetters = new ArrayList<String>();
		long result = WSJNI.GetAvailableMountDriveLetters(avaliableDriveLetters);
		return avaliableDriveLetters;
	}
	@Override
	public String getWindowsTempDir(){
		return WSJNI.GetWindowsTempDir();
	}
	@Override
	public List<JMountedRecoveryPointItem> getMntInfoForVolume(String dest,String domain, String user, String pwd, String subPath, String volGUID){
		if(logger.isDebugEnabled()) {
			logger.debug("getMntInfoForVolume: dest " + dest
					+ " domain " + domain
					+ " user " + user
					+ " subPath " + subPath
					+ " volGUID " + volGUID);
		}
		Lock lock = null;
		try {
			lock = RemoteFolderConnCache.getInstance().getLockByPath(dest);
			if(lock != null){
				lock.lock();
			}
			List<JMountedRecoveryPointItem> mountedItems = new ArrayList<JMountedRecoveryPointItem>();
			long result = WSJNI.AFGetMntInfoForVolume(dest, domain, user, pwd, subPath, volGUID, mountedItems);
			return mountedItems;
		}finally {
			if(lock != null) {
				lock.unlock();
			}
			logger.debug("End getMntInfoForVolume");
		}
	}
	//end

	@Override
	public String getErrorMsg(long dwErrCode){
		return WSJNI.AFGetErrorMsg(dwErrCode);
	}
	
	@Override
	public boolean CheckIfExistDiskLargerThan2T() throws ServiceException {
		try {
			return WSJNI.CheckIfExistDiskLargerThan2T();
		} catch (WSJNIException e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService, e.getErrorCode() + "");
		}
	}	


	
	@Override
	public long startD2dCallback() throws ServiceException
	{
		long result = 0;
		try
		{
			result = WSJNI.StartD2dCallback();
			logger.debug("Start D2D C++ callback module. ret=" + result);
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService, new Object[]{});
		}
		return result;
	}

	@Override
	public long stopD2dCallback() throws ServiceException
	{
		long result = 0;
		try
		{
			result = WSJNI.StopD2dCallback();
			logger.debug("Stop D2D C++ callback module. ret=" + result);
		}
		catch (Exception e)
		{
			logger.error(e.getMessage(), e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService, new Object[]{});
		}
		return result;
	}

	@Override
	public boolean isShowUpdate() {
		String updateKey = "HideAutoUpdate";
		String updateManager = "Update Manager";
		JRWLong jValue = new JRWLong();
		int result = WSJNI.GetRegIntValue(updateManager, updateKey, "", jValue); 
		if(result == 0 ){
			return jValue.getValue() == 0;
		}
		else{
			return true;
		}

	}


	@Override
	public boolean isAdjustJavaHeapSize(ApplicationType appType){
		boolean isAMD64 = false;
		try {
			short cpu = GetHostProcessorArchitectural();
			if(cpu == 9){
				isAMD64 = true;
			}
		} catch (Exception e) {
			logger.error(e);
		}
		
		return ApacheServiceUtil.getInstance().adjustJavaVM(appType, isAMD64);
	}

	//////////////////////////////////////////////////////////////////////////
	// Manual Conversion
	
	public long createVssManager()
	{
		return WSJNI.createVssManager();
	}
	
    public int vssManager_Init( long vssManagerHandle, int flag, boolean isPersistent )
    {
    	return WSJNI.vssManager_Init( vssManagerHandle, flag, isPersistent );
    }
    
    public String vssManager_CreateSnapshotSet( long vssManagerHandle, List<String> volumes )
    {
    	return WSJNI.vssManager_CreateSnapshotSet( vssManagerHandle, volumes );
    }
    
    public int vssManager_DeleteSnapshotSet( long vssManagerHandle, String snapshotSetId )
    {
    	return WSJNI.vssManager_DeleteSnapshotSet( vssManagerHandle, snapshotSetId );
    }
    
    public long vssManager_GetSnapshotSetByGuid( long vssManagerHandle, String snapshotSetId )
    {
    	return WSJNI.vssManager_GetSnapshotSetByGuid( vssManagerHandle, snapshotSetId );
    }
    
    public void vssManager_Release( long vssManagerHandle )
    {
    	WSJNI.vssManager_Release( vssManagerHandle );
    }
    
    public String vcmSnapshotSet_QuerySnapshotDeviceName( long snapshotSetHandle, String originalVolumeName )
    {
    	return WSJNI.vcmSnapshotSet_QuerySnapshotDeviceName( snapshotSetHandle, originalVolumeName );
    }
    
    public void vcmSnapshotSet_Release( long snapshotSetHandle )
    {
    	WSJNI.vcmSnapshotSet_Release( snapshotSetHandle );
    }
    @Override
	public List<Integer> getIntactSessions( String sessionFolderPath )
	{
		List<Integer> sessionList = new ArrayList<Integer>();
		int ret = WSJNI.GetIntactSessions( sessionFolderPath, sessionList );
		return sessionList;
	}
    
    @Override
    public int GetLocalPathOfShareName( String shareName, List<String> localPathList, List<Integer> errorCodeList )
    {
    	return WSJNI.GetLocalPathOfShareName( shareName, localPathList, errorCodeList );
    }
    
    @Override
    public RHAScenarioState getRHAScenarioState( String rootPath ) throws Exception
    {
    	if (rootPath == null)
    	{
    		logger.error( "Error getting RHA scenario state. The rootPath is null." );
    		return RHAScenarioState.Unknown;
    	}
    	
    	List<Integer> resultCodeList = new ArrayList<Integer>();
    	int statusValue = WSJNI.GetRHAScenarioState( rootPath, resultCodeList );
    	if ((resultCodeList.size() != 0) && (resultCodeList.get( 0 ) != 0))
    		throw new Exception( "Native function GetRHAScenarioState() failed." );
    	
    	RHAScenarioState status = RHAScenarioState.Unknown;
    	switch (statusValue)
    	{
    	case RHAScenarioStatusValue.Run:
    		status = RHAScenarioState.Run;
    		break;
    		
    	case RHAScenarioStatusValue.Stop:
    		status = RHAScenarioState.Stop;
    		break;
    		
    	case RHAScenarioStatusValue.Sync:
    		status = RHAScenarioState.Sync;
    		break;
    		
    	case RHAScenarioStatusValue.Unknown:
    		status = RHAScenarioState.Unknown;
    		break;
    		
    	default:
    		throw new Exception( "Unknown RHA scenario status value. Value: " + statusValue );
    	}
    	
    	return status;
    }
    
    @Override
    public boolean IsRHASyncReplicated( String rootPath ) throws Exception
    {
    	List<Integer> resultCodeList = new ArrayList<Integer>();
    	boolean isSyncReplicated = WSJNI.IsRHASyncReplicated( rootPath, resultCodeList );
    	if ((resultCodeList.size() != 0) && (resultCodeList.get( 0 ) != 0))
    		throw new Exception( "Native function IsRHASyncReplicated() failed." );
    	
    	return isSyncReplicated;
    }

    @Override
	public JSystemInfo getSystemInfo() throws ServiceException {
		return WSJNI.getSystemInfo();
	}
	
    @Override
	public long DoRVCMInjectService(
		String esxHostname,
		int esxPort,
		VMwareConnParams exParams,
		String username,
		String password,
		String moRefId,
		VMwareVolumeInfo volumeInfo,
		String failoverMode,
		List<String> vmdkUrlList,
		String iniFolderPath,
		String jobId,
		List<com.ca.arcflash.failover.model.Volume> volumesOnNonBootOrSystemDisk,
		boolean isX86,
		String scriptPath)
	{
		return WSJNI.DoRVCMInjectService(
			esxHostname, esxPort, exParams, username, password, moRefId, volumeInfo, failoverMode,
			vmdkUrlList, iniFolderPath, jobId, volumesOnNonBootOrSystemDisk, isX86, scriptPath);
	}
	
	@Override
	public long DoRVCMInjectServiceForHyperV(
		String vmWinDir,
		String winSystemDir,
		List<com.ca.arcflash.failover.model.Volume> volumesOnNonBootOrSystemDisk, 
		boolean isX86,
		String scriptPath)
	{
		return WSJNI.DoRVCMInjectServiceForHyperV(
			vmWinDir, winSystemDir, volumesOnNonBootOrSystemDisk, isX86, scriptPath);
	}

	@Override
    public long startMerge(MergeJobScript mjs)
            throws ServiceException {
		logger.debug("************Start Merge job******************");
		String binpath = ServiceContext.getInstance().getBinFolderPath();
		File tmp = new File(binpath + "\\temp");
		if(!tmp.exists()) {
			if(!tmp.mkdir()){
				logger.error("Failed to create temp folder under bin, " +
						"cannot start merge job");
				return -1;
			}
		}
	/*	File jsFile = ServiceUtils.marshallMergeJobScript(mjs, tmp.getAbsolutePath());
		if(jsFile == null) {
			logger.error("Failed to marshall merge jobscript");
			return -1;
		}*/
		String filepath = tmp.getAbsolutePath() + "\\MergeJS_" + mjs.getDwJobID() 
				+ "_" + new Date().getTime() + ".xml";
		long ret = WSJNI.startMerge(mjs, filepath);
		logger.debug("Merge job started with return " + ret);
	    return ret;
    }

	@Override
    public long stopMerge(long jobId) throws ServiceException {
		logger.debug("************Merge job stopped******************");
		long ret = WSJNI.stopMerge(jobId);
		logger.debug("End stop merge");
	    return ret;
    }

	@Override
    public MergeJobMonitor getMergeJobMonitor(MergeJobMonitor mJM, long handle) {
		long ret = WSJNI.getMergeJobMonitor(handle, mJM);
		if(ret != 0)
			logger.error("Failed to get merge job monitor, with return " + ret);
	    return mJM;
    }

	@Override
    public long createMergeJobMonitor(long jobId) {
	    return WSJNI.createMergeJobMonitor(jobId);
    }

	@Override
    public long releaseMergeJobMonitor(long handle) {
	    return WSJNI.releaseMergeJobMonitor(handle);
    }
	
	@Override
	public boolean markBackupSetFlag(String destination, String domain, String userName, 
			String password, long sessionNum, String vmInstanceUUID){
		Lock lock = null;
		logger.debug("Set recovery set start point for " + sessionNum + " vm is " + vmInstanceUUID);
		try {
			lock = RemoteFolderConnCache.getInstance().getLockByPath(destination);
			if(lock != null) {
				lock.lock();
				if(domain != null && !domain.isEmpty()) {
					userName = domain + "\\" + userName;
				}
				this.NetConn(userName, password, destination);
			}
			
			long ret = WSJNI.setBackupSetFlag(destination, sessionNum, 1, vmInstanceUUID);
			if(ret != 0) {
				logger.error("Failed to mark backup set flag for " + sessionNum 
						+ ", return " + ret);
				return false;
			}else {
				return true;
			}
		}catch(Exception e) {
			logger.error("Failed to mark backup set flag " + e);
			return false;
		}finally {
			if(lock != null) {
				lock.unlock();
			}
			try {
				this.disconnectRemotePath(destination, domain, userName, password, false);
			}catch(Exception e) {
				logger.debug("Disconnect " + destination + " error"); 
			}
		}
	}
	
	@Override
	public void unmarkBackupSetFlag(String destination, String domain, String userName, 
			String password, long sessionNum, String vmInstanceUUID){
		Lock lock = null;
		logger.debug("Remove recovery set start point for " + sessionNum + " vm is " + vmInstanceUUID);
		try {
			lock = RemoteFolderConnCache.getInstance().getLockByPath(destination);
			if(lock != null) {
				lock.lock();
				if(domain != null && !domain.isEmpty()) {
					userName = domain + "\\" + userName;
				}
				this.NetConn(userName, password, destination);
			}

			long ret = WSJNI.setBackupSetFlag(destination, sessionNum, 0, vmInstanceUUID);
			if(ret != 0) {
				logger.error("Failed to unmark backup set flag for " + sessionNum 
						+ ", return " + ret);
			}
		}catch(Exception e) {
			logger.error("Failed to unmark backup set flag " + e);
		}finally {
			if(lock != null) {
				lock.unlock();
			}
			try {
				this.disconnectRemotePath(destination, domain, userName, password, false);
			}catch(Exception e) {
				logger.debug("Disconnect " + destination + " error"); 
			}
		}
		logger.debug("End remove recovery set start point for " + sessionNum + " vm is " + vmInstanceUUID);
	}
	/**
	 *@return 0: need to start merge job;
	 *		  1: failed to connect destination
	 *		  2: exception occurs 
	 * 		  2012: don't need to start merge job, no session need to merge
	 * 		  2013: cannot start merge job, maybe another job is already running.
	 * 		 
	 */
	@Override
	public long isMergeJobAvailable(int retentionCount, String destination,
			String vmUUID, String userName, String password) {
		logger.debug("Check for merge job avaiable with retention " + 
				retentionCount + " and destination as " + destination);
		Lock lock = RemoteFolderConnCache.getInstance().getLockByPath(destination);
		try {
			if(lock != null) {
				lock.lock();
			}	
			logger.debug("Lock to destination " + destination);
			long ret = -1;
			for(int i = 0; i < 5; i ++){
				try {
					if(userName == null)
						userName = "";
					if(password == null)
						password = "";
					ret = this.NetConn(userName, password, destination);
					break;
				}catch(ServiceException se) {
					logger.debug("Net connetion error " + se.getMessage());
				}
			}
			if(ret != 0) {
				logger.error("Failed to connect to backup destination.");
				return 1;
			}
			ret = WSJNI.isMergeJobAvailable(retentionCount, destination, vmUUID, userName, password);
			
			if(ret != 0) {
				logger.info("Merge job is unavaiable with return code " + ret);
				return ret;
			}else {
				logger.debug("Merge job can run");
				return ret;
			}	
		}catch(Exception e){
			logger.error("Failed to check merge job available with exception " + e.getMessage());
			return 2;
		}finally {
			if(lock != null) {
				lock.unlock();
				logger.debug("unlock destination " + destination);
			}			
			try {
				this.disconnectRemotePath(destination, "", userName, password, false);
			}catch(Exception e) {
				logger.debug("Failed to cut connection to " + destination);
			}
		}
		
	}	
		
	@Override
	public long isMergeJobAvailable(JMergeData mergeInfo) {
		logger.debug("Check for merge job avaiable");	
		String destination = mergeInfo.getBackupDest();
		String userName = mergeInfo.getBackupUser();
		String password = mergeInfo.getBackupPassword();
		
		Lock lock = RemoteFolderConnCache.getInstance().getLockByPath(destination);
		try {
			if(lock != null) {
				lock.lock();
			}	
			logger.debug("Lock to destination " + destination);
			long ret = -1;
			for(int i = 0; i < 5; i ++){
				try {
					if(userName == null)
						userName = "";
					if(password == null)
						password = "";
					
					ret = this.NetConn(userName, password, destination);
					break;
				}catch(ServiceException se) {
					logger.debug("Net connetion error " + se.getMessage());
				}
			}
			if(ret != 0) {
				logger.error("Failed to connect to backup destination.");
				return 1;
			}
			
			ret = WSJNI.AFIIsMergeJobAvailableExt(mergeInfo);			
			
			if(ret != 0) {
				logger.info("Merge job is unavaiable with return code " + ret);
				return ret;
			}else {
				logger.debug("Merge job can run");
				return ret;
			}	
		}catch(Exception e){
			logger.error("Failed to check merge job available with exception " + e.getMessage());
			return 2;
		}finally {
			if(lock != null) {
				lock.unlock();
				logger.debug("unlock destination " + destination);
			}			
			try {
				this.disconnectRemotePath(destination, "", userName, password, false);
			}catch(Exception e) {
				logger.debug("Failed to cut connection to " + destination);
			}
		}
		
	}
	@Override
	public long isMergeJobAvailableEx(String destination, int retentionCount, int  dailyCount, int weeklyCount, int monthlyCount, String vmUUID, String userName, String password) {
		logger.debug("Check for merge job avaiable with retention " + 
				retentionCount + " and destination as " + destination);
		Lock lock = RemoteFolderConnCache.getInstance().getLockByPath(destination);
		try {
			if(lock != null) {
				lock.lock();
			}	
			logger.debug("Lock to destination " + destination);
			long ret = -1;
			for(int i = 0; i < 5; i ++){
				try {
					if(userName == null)
						userName = "";
					if(password == null)
						password = "";
					ret = this.NetConn(userName, password, destination);
					break;
				}catch(ServiceException se) {
					logger.debug("Net connetion error " + se.getMessage());
				}
			}
			if(ret != 0) {
				logger.error("Failed to connect to backup destination.");
				return 1;
			}
			ret = WSJNI.AFIIsMergeJobAvailableEx(destination, retentionCount, dailyCount, weeklyCount, monthlyCount, vmUUID, "", userName, password);
			
			if(ret != 0) {
				logger.info("Merge job is unavaiable with return code " + ret);
				return ret;
			}else {
				logger.debug("Merge job can run");
				return ret;
			}	
		}catch(Exception e){
			logger.error("Failed to check merge job available with exception " + e.getMessage());
			return 2;
		}finally {
			if(lock != null) {
				lock.unlock();
				logger.debug("unlock destination " + destination);
			}			
			try {
				this.disconnectRemotePath(destination, "", userName, password, false);
			}catch(Exception e) {
				logger.debug("Failed to cut connection to " + destination);
			}
		}
		
	}
	
	//wanqi06
	@Override
	public List<JMergeActiveJob> getActiveMergeJobInfo(){
		List<JMergeActiveJob> activeMergeJobs = new ArrayList<JMergeActiveJob>();
		long result = WSJNI.AFIRetrieveMergeJM(activeMergeJobs);
		if(result != 0)
			logger.error("AFIRetrieveMergeJM return error! " + result);
		return activeMergeJobs;
	}

	@Override
	public long lauchCatalogJob(long jobId, long type, String vmInstanceUUID,
			String destination, String userName, String password) {
		JNetConnInfo conn = createNetConnInfo(userName, password, destination);
		CatalogJobContext context = new CatalogJobContext(jobId, type, conn);
		context.setVmIndentification(vmInstanceUUID);
		long ret = WSJNI.launchCatalogEx(context);
		if(ret != 0){
			logger.error("Failed to lauchCatalogJob, jobId is " + jobId);
		}
		return ret;
	}
	
	private JNetConnInfo createNetConnInfo(String userName, String password, String destination) {
		JNetConnInfo connInfo = new JNetConnInfo();
		connInfo.setSzDir(destination);
		connInfo.setSzDomain("");
		connInfo.setSzUsr(userName);
		connInfo.setSzPwd(password);
		return connInfo;
	}

	@Override
	public JRestorePoint[] getRestorePointsForBackupSet(String destination,
			String domain, String userName, String pwd, Date beginDate,
			Date endDate) throws ServiceException {
		logger.debug("getRestorePointsForBackupSet(String, Date, Date) - start");
		logger.debug("destination:" + destination);
		logger.debug("beginDate:" + beginDate);
		logger.debug("endDate:" + endDate);

		List<JRestorePoint> list = new ArrayList<JRestorePoint>();
		String beginDateString = BackupConverterUtil.dateToUTCString(beginDate);
		String endDateString = BackupConverterUtil.dateToUTCString(endDate);
		logger.debug("transformed beginDate:" + beginDateString);
		logger.debug("trsformed endDate:" + endDateString);

		int jniResult;
		JRestorePoint[] retJResPntArr = null;
		Lock lock = RemoteFolderConnCache.getInstance().getLockByPath(destination);
		try {
			if(lock != null) {
				logger.debug("lock to " + destination + ", domain:" + domain + ", userName:" + userName);
				lock.lock();
			}
			
			jniResult = WSJNI.GetRecoveryPoint(destination, domain, userName,
					pwd, beginDateString, endDateString, list, false);
			if(jniResult != 0){
				logger.error("GetRecoveryPoint from " + destination + " returns error " + jniResult);
			}
			retJResPntArr = list.toArray(new JRestorePoint[0]);
			if(jniResult != 0 && jniResult != 0x02 && jniResult != 0x03 && jniResult != 0x103)
				throw new ServiceException("Failed to GetRecoveryPoint with error code " + jniResult, 
						FlashServiceErrorCode.Common_General_Message);
				
		}finally {
			if(lock != null) {
				lock.unlock();
			}
		}
		logger.debug("Get the restore points for backup set end");
		return retJResPntArr;
	}
	
	@Override
	public FileFolderItem getVMVolumes(JBackupVM jBackupVM) throws Throwable {
		logger.debug("getVMVolumes start " + System.currentTimeMillis());
		FileFolderItem item = WSJNI.browseVMFileFolderItem("/",jBackupVM);
		logger.debug("getVMVolumes(JBackupVM) - end");
		return item;
	}

	@Override
	public FileFolderItem getVMFileFolderItem(String path, JBackupVM jBackupVM)
			throws ServiceException {
		logger.debug("getVMFileFolderItem start " + System.currentTimeMillis());
		FileFolderItem item = WSJNI.browseVMFileFolderItem(path,jBackupVM);
		logger.debug("getVMFileFolderItem(JBackupVM) - end");
		return item;
	}

	@Override
	public void createVMDir(String parentPath, String subDir,
			JBackupVM jBackupVM) throws ServiceException {
		try {
			logger.debug("createVMDir() - start");
			logger.debug("parenPath: " + parentPath);
			logger.debug("subDir: " + subDir);

			long result = WSJNI.BrowseVMFileFolderItem(parentPath,subDir,2, jBackupVM,null);
			logger.debug("result:" + result);

			if(result > 0)
			{
				String msg = WSJNI.AFGetErrorMsg(result);
				throw new ServiceException(msg,
						FlashServiceErrorCode.Browser_CreateFolderFailed);
			}

			logger.debug("createVMDir() - end");
		}
		catch (ServiceException e) {
			logger.debug(e.getMessage(), e);
			throw e;
		}
		catch (Exception e) {
			logger.debug(e.getMessage(), e);
			throw new ServiceException(
					FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}

	@Override
	public long deleteAllPendingFileCopyJobs(String backupDestination,
			String destDomain, String destUserName, String destPassword)
			throws ServiceException {
		long ret = 0;
		
		ret = WSJNI.DisableFileCopy(backupDestination, destDomain, destUserName, destPassword);
		
		return ret;
	}

	/**
	 * Before call this method, the caller must connect to remote destination
	 * @param retentionCount
	 * @param backupDest
	 * @return
	 */
	@Override
	public List<Integer> GetSessNumListForNextMerge(int retentionCount, String backupDest) {		
		logger.debug("Getting session number list for next merge, retentionCount: " + retentionCount
				+ " backupDest: " + backupDest);
		ArrayList<Integer> sessionNumberList = new ArrayList<Integer>();
		try {
			long ret = WSJNI.GetSessNumListForNextMerge(retentionCount, backupDest, sessionNumberList, null, null); 
			
			if(ret != 0){
				logger.error("Failed to get session number list for next merge, error " + ret);
			}
			logger.debug("Return: Getting session Number list for next merge ");
			return sessionNumberList;
		}catch(Exception e){
			logger.error("Failed to get session number list with exception " + e.getMessage());
			return sessionNumberList;
		}
	}

	/**
	 * Before call this API, the caller must connect to remote destination
	 * @param incrSessNumber
	 * @param backupDest
	 * @return
	 */
	@Override
	public long getFullSessNumber4Incre(long incrSessNumber, String backupDest) {
		logger.debug("getFullSession Nubmer for : " + incrSessNumber
				+ " backupDest: " + backupDest);
		try {
			JRWLong fullSessNumber = new JRWLong();
			long ret = WSJNI.AFIGetFullSess4Inc(fullSessNumber, incrSessNumber, backupDest);
			
			if(ret != 0){
				logger.error("Failed to get full session number, error " + ret);
			}else{
				logger.debug("Return: Getting full session Number");
				return fullSessNumber.getValue();
			}
		}catch(Exception e){
			logger.error("Failed to get full session number " + e.getMessage());
		}
		return 0;
	}

	@Override
	public VCMMachineInfo getMachineDetailFromBackupSession(String backupDest) {
		logger.debug("getMachineDetailFromBackupSession , backupDest: " + backupDest);
		VCMMachineInfo result = null;
		try {
			JMachineDetail machineDetail = new JMachineDetail();
			long ret = WSJNI.getMachineDetailFromBackupSession(backupDest, machineDetail);
			if(ret != 0){
				logger.error("Failed to get machine detail from backup session, error : " + ret);
			}else{
				result = new VCMMachineInfo();
				switch (machineDetail.getMachineType()) {
				case 0:
					result.setNodeType(VCMMachineInfo.VCMNodeType.PHYSICAL);
					break;
				case 1:
					result.setNodeType(VCMMachineInfo.VCMNodeType.HYPERV_VM);
					break;
				case 2:
					result.setNodeType(VCMMachineInfo.VCMNodeType.ESX_VM);
					break;
				case 3:
					result.setNodeType(VCMMachineInfo.VCMNodeType.HBBU_VM);
					break;
				default:
					result.setNodeType(VCMMachineInfo.VCMNodeType.OTHER_VM);
					break;
				}
				result.setMachineInfo(new MachineInfo());
				result.getMachineInfo().setHostName(machineDetail.getHostName());
				result.getMachineInfo().setServerName(machineDetail.getHypervisorHostName());
				if (result.getNodeType() == VCMMachineInfo.VCMNodeType.HBBU_VM) {
					result.getMachineInfo().setSocketCount(machineDetail.getNumberOfProcessors());
					result.getMachineInfo().setServerSocketCount(machineDetail.getHyperVisorNumberOfProcessors());
				} else {
					result.getMachineInfo().setSocketCount(machineDetail.getNumberOfProcessors());
					result.setOsProductType(machineDetail.getOsProductType());
				}
			}
		}catch(Throwable e){
			logger.error("Failed to get machine detail from backup session" ,e);
		}
		return result;
	}

	@Override
	public long getTickCount()
	{
		return WSJNI.getTickCount();
	}

	@Override
	public void startNICMonitor() {
		WSJNI.StartNICMonitor();
		logger.info("Start NIC monitor thread");
	}

	@Override
	public void stopNICMonitor() {
		WSJNI.StopNICMonitor();
		logger.info("Stop NIC monitor thread");
	}
	
	@Override
	public void startClusterMonitor() {
		WSJNI.StartClusterMonitor();
		logger.info("Start cluster monitor thread");
	}

	@Override
	public void stopClusterMonitor() {
		WSJNI.StopClusterMonitor();
		logger.info("Stop cluster monitor thread");
	}
	
	
	@Override
	public boolean isX86() {
		return WSJNI.isX86();
	}

	@Override
	public String getRPSDataStoreHashKey(String destination, String userName, String password,
			int sessionNumber, String sessionPwd) {
		if(logger.isDebugEnabled())
			logger.debug("Get rps datastore hash key for " + destination + 
				" session is " + sessionNumber);
		Lock lock = null;
		String key = null;
		try {
			lock = RemoteFolderConnCache.getInstance().getLockByPath(destination);
			if(lock != null){
				lock.lock();
			}
			this.NetConn(userName, password, destination);
			key = WSJNI.getRPSDataStoreHashKey(destination, sessionNumber, sessionPwd);
			if(key == null){
				logger.error("Failed to get key for destination: " + destination 
						+ " session: " + sessionNumber);
			}
			this.disconnectRemotePath(destination, "", userName, password, false);
			logger.debug("Gotten the datastore hash key");
			return key;
		} catch (ServiceException e) {
			if (e.getErrorCode() == FlashServiceErrorCode.BackupConfig_ERR_DEST_WINSYSMSG) {
				String message = WebServiceMessages.getResource(
						"connectDestinationFolderError", destination, userName,
						e.getMessage());
				addLogActivity(Constants.AFRES_AFALOG_ERROR,
						Constants.AFRES_AFJWBS_GENERAL, new String[] { message,
								"", "", "", "" });
			} else {
				logger.error("Failed to get key for destination : "
						+ destination + " session: " + sessionNumber, e);
			}
			return key;
		}catch(Exception e){
			logger.error("Failed to get key for destination: " + destination 
					+ " session: " + sessionNumber, e);
			return key;
		}finally {
			if(lock != null)
				lock.unlock();
		}
		
	}

	@Override
	public boolean isCatalogExist(String backupDest, String userName,
			String password, int sessionNumber) {
		if(logger.isDebugEnabled())
			logger.debug("Check catalog for session " + sessionNumber
					+ " at destination" + backupDest);
		Lock lock = null;
		boolean catalogExist = false;
		try {
			lock = RemoteFolderConnCache.getInstance().getLockByPath(backupDest);
			if(lock != null)
				lock.lock();
			this.NetConn(userName, password, backupDest);
			catalogExist = WSJNI.isCatalogExist(backupDest, sessionNumber);
			this.disconnectRemotePath(backupDest, "", userName, password, false);
		}catch(Exception e) {
			logger.error("Exception when check catalog", e);
		}finally {
			if(lock != null){
				lock.unlock();
			}
		}
		logger.debug("Checking catalog exist return");
		return catalogExist;
	}

	@Override
	public long validateDestUser(String destination, String domain, String userName,
			String password) throws ServiceException {
		logger.debug("validateDestUser(String, String, String, String) - start");

		if (this.remoteDriverWithoutUserNamePwd(destination, userName, password)) {
			throw new ServiceException(
					WebServiceMessages.getResource("destUserNamePasswordNull"),
					FlashServiceErrorCode.BackupConfig_ERR_DEST_WINSYSMSG);
		}

		int dwRet;
		ArrayList<JFileInfo> info = new ArrayList<JFileInfo>(1);

		Lock lock = RemoteFolderConnCache.getInstance().getLockByPath(destination);
		try {
			if (lock != null) {
				lock.lock();
			}
			if(!StringUtil.isEmptyOrNull(domain))
				userName = domain + "\\" + userName;
			dwRet = WSJNI.AFIVerifyDestUser(destination, userName, password);
			
			logger.debug("JNI return:" + dwRet);

			if (dwRet != 0) {
				// String msg = WSJNI.AFGetErrorMsg(dwRet);
				String msg = String.format(
						WebServiceMessages.getResource("cannotAccessFolder"),
						destination);
				throw new ServiceException(msg,
						FlashServiceErrorCode.BackupConfig_ERR_DEST_WINSYSMSG);
			}else {
				this.checkFolderAccess(destination, "", userName, password);
			}
			if (info.size() > 0) {
				if ((JFileInfo.FILE_ATTRIBUTE_READONLY & info.get(0)
						.getDwFileAttributes()) > 0) {
					throw new ServiceException(
							FlashServiceErrorCode.BackupConfig_ERR_FolderIsNotWritable);
				}
			}
		} finally {
			if (lock != null) {
				lock.unlock();
				logger.debug("release lock");
			}
		}

		logger.debug("validateDestUser(String, String, String, String) - end");
		return dwRet;
	}

	@Override
	public List<String> getD2DIPList() {
		List<String> ipList = new ArrayList<String>(); 
		WSJNI.getIPList(ipList);
		return ipList;
	}


	@Override
	public long updateJobHistory(JJobHistory jobHistory) {
		logger.debug("Update D2D job history for " + jobHistory.getJobId());

		Long startTime = System.currentTimeMillis();
		jobHistory.setJobUTCStartTime(startTime.toString());

		String strD2DUUID = CommonService.getInstance().getNodeUUID();
		if (!StringUtil.isEmptyOrNull(jobHistory.getJobDisposeNodeUUID())
				&& strD2DUUID.compareToIgnoreCase(jobHistory.getJobDisposeNodeUUID()) != 0
				&& StringUtil.isEmptyOrNull(jobHistory.getJobDisposeNode())) {
			jobHistory.setJobDisposeNode(VSphereService.getInstance().getVMName(jobHistory.getJobDisposeNodeUUID()));
		}

		if (!StringUtil.isEmptyOrNull(jobHistory.getJobRunningNodeUUID())
				&& strD2DUUID.compareToIgnoreCase(jobHistory.getJobRunningNodeUUID()) != 0
				&& StringUtil.isEmptyOrNull(jobHistory.getJobRunningNode())) {
			jobHistory.setJobRunningNode(VSphereService.getInstance().getVMName(jobHistory.getJobRunningNodeUUID()));
		}		
		
		int ret = WSJNI.updateD2DJobHistory(jobHistory);
		if (ret != 0) {
			logger.error("Failed to update job history of job "
					+ jobHistory.getJobId());
		}
		if (logger.isDebugEnabled())
			logger.debug("Update D2D job history for " + jobHistory.getJobId()
					+ " end.");
		return startTime;
	}

	
	@Override
	public int addMissedJobHistory( JJobHistory jobHistory ) {
		logger.debug("Add missed D2D job history for " + jobHistory.getJobId() );
		
		Long startTime = System.currentTimeMillis();
		jobHistory.setJobUTCStartTime( startTime.toString() );
		String strD2DUUID = CommonService.getInstance().getNodeUUID();
		if (!StringUtil.isEmptyOrNull(jobHistory.getJobDisposeNodeUUID())
				&& strD2DUUID.compareToIgnoreCase(jobHistory.getJobDisposeNodeUUID()) != 0
				&& StringUtil.isEmptyOrNull(jobHistory.getJobDisposeNode())) {
			jobHistory.setJobDisposeNode(VSphereService.getInstance().getVMName(jobHistory.getJobDisposeNodeUUID()));
		}
		if (!StringUtil.isEmptyOrNull(jobHistory.getJobRunningNodeUUID())
				&& strD2DUUID.compareToIgnoreCase(jobHistory.getJobRunningNodeUUID()) != 0
				&& StringUtil.isEmptyOrNull(jobHistory.getJobRunningNode())) {
			jobHistory.setJobRunningNode(VSphereService.getInstance().getVMName(jobHistory.getJobRunningNodeUUID()));
		}
		
		int ret = WSJNI.addMissedD2DJobHistory( jobHistory );
		if (ret != 0) {
			logger.error("Failed to Add missed D2D job history for " + jobHistory.getJobId() );
		}
		if (logger.isDebugEnabled())
			logger.debug("Add missed D2D job history for " + jobHistory.getJobId()  + " end.");
		return ret;
	}

	public boolean markD2DJobEnd(long jobID, long jobStatus, String jobDetails)
	{
		int result = WSJNI.markD2DJobEnd(jobID, jobStatus, jobDetails);

		if (result != 0) {
			logger.error("Mark d2d job as end successful for job: " + jobID
					+ " jobStatus: " + jobStatus + " Result : " + result);
			return false;
		}

		logger.info("Mark d2d job as end successful for jobID: " + jobID
				+ " jobStatus: " + jobStatus);
		return true;
	}
	
	public boolean ifJobHistoryExist(int productType, long jobID, int jobType, String agentNodeID)
	{
		return WSJNI.ifJobHistoryExist(productType, jobID, jobType, agentNodeID);
	}
	
	public int updateThrottling(long jobID, long throttling) {
		return WSJNI.updateThrottling(jobID, throttling);
	}

	@Override
	public List<JDisk> getHyperVBackupVMDisk(String destination, String subPath, String domain, String username, String password) throws ServiceException {
		logger.debug("getHyperVBackupVMDisk start");
		logger.debug("destination:" + destination);
		logger.debug("domain:" + domain);
		logger.debug("username:" + username);
		
		try {
			List<JDisk> diskList = new ArrayList<JDisk>();
			long ret = WSJNI.AFGetHyperVBackupVMDisk(destination, subPath, domain, username, password, diskList);
			logger.debug("getHyperVBackupVMDisk return:" + ret);
			return diskList;
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}
	}
	
	@Override
	public List<String> getE15CASList(String strUser, String strPassword) {
		logger.debug("getE15CASList() - start");
		ArrayList<JExchangeDiscoveryItem> retArr = new ArrayList<JExchangeDiscoveryItem>();
		try{
			long returnValue = WSJNI.AEGetE15CASList(retArr, strUser, strPassword);
			logger.debug("getE15CASList() return: " + returnValue);
			logger.debug(StringUtil.convertList2String(retArr));
		}catch(Throwable e){
			logger.error(e);
		}
		
		ArrayList<String> result = new ArrayList<String>(retArr.size());
		for(JExchangeDiscoveryItem item : retArr){
			result.add(item.getPwszName());
		}
		return result;
	}
	@Override
	public String getDefaultE15CAS() {
		logger.debug("getDefaultE15CAS() - start");
		ArrayList<String> retArr = new ArrayList<String>();
		try{
			long returnValue = WSJNI.AEGetDefaultE15CAS(retArr);
			logger.debug("getDefaultE15CAS() return: " + returnValue);
		}catch(Throwable e){
			logger.error(e);
		}
		String result=null;
		if(retArr.size()>0){
			result = retArr.get(0);
		}
		logger.debug("default E15 CAS is: " + result);
		return result;
	}
	@Override
	public void setDefaultE15CAS(String cas){
		logger.debug("setDefaultE15CAS() - cas: " + cas);
		try{
			long returnValue = WSJNI.AESetDefaultE15CAS(cas);
			logger.debug("setDefaultE15CAS() return: " + returnValue);
		}catch(Throwable e){
			logger.error(e);
		}
	}
	
	/**
	 * Functions to get switch through JNI
	 */
	/**
	 * Get a integer value from INI file.
	 * @param strApp
	 * @param strKey
	 * @param nDefault
	 * @param fullPathOfIniFile: Optional. If fullPathOfIniFile is null or empty, get value from ..\Configuraion\Switch.ini
	 * @return return the value. If switch not define, return nDefault
	 */
	@Override
	public int getSwitchIntFromFile( String strApp, String strKey, int nDefault, String fullPathOfIniFile){
		return WSJNI.getSwitchIntFromFile(strApp, strKey, nDefault, fullPathOfIniFile);
	}
	
	/**
	 * 
	 * @param strValueName
	 * @param nDefaultValue
	 * @param strSubKey: Optional. If strSubKey is null or empty, get value from "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\\Unified Data Protection\Engine"
	 * @return
	 */
	@Override
	public int getSwitchIntFromReg( String strValueName, int nDefaultValue, String strSubKey){
		return WSJNI.getSwitchIntFromReg(strValueName, nDefaultValue, strSubKey);
	}
	
	/**
	 * 
	 * @param strApp
	 * @param strKey
	 * @param strDefault
	 * @param fullPathOfIniFile: Optional. If fullPathOfIniFile is null or empty, get value from ..\Configuraion\Switch.ini
	 * @return
	 */
	@Override
	public String getSwitchStringFromFile( String strApp, String strKey, String strDefault, String fullPathOfIniFile){
		return WSJNI.getSwitchStringFromFile( strApp, strKey, strDefault, fullPathOfIniFile);
	}
	
	/**
	 * 
	 * @param strValueName
	 * @param strDefaultValue
	 * @param strSubKey: Optional. If strSubKey is null or empty, get value from "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine"
	 * @return
	 */
	@Override
	public String getSwitchStringFromReg( String strValueName, String strDefaultValue, String strSubKey ){
		return WSJNI.getSwitchStringFromReg( strValueName, strDefaultValue, strSubKey );
	}
	/*
	 * end
	 */
	
	@Override
	public long getMailAlertFiles( ArrayList<String> fileList){
		return WSJNI.getMailAlertFiles(fileList);
	}

	@Override
	public long getMailAlertFilesEx( int type, ArrayList<String> fileList ){
		return WSJNI.getMailAlertFilesEx(type, fileList);
	}

	@Override
	public int getHyperVCPUSocketCount(long handle) {
		return WSJNI.getHyperVCPUSocketCount(handle);
	}

	@Override
	public String getHyperVDefaultFolderOfVHD(long handle) {
		return WSJNI.getHyperVDefaultFolderOfVHD(handle);
	}

	@Override
	public String GetHyperVDefaultFolderOfVM(long handle) {
		return WSJNI.GetHyperVDefaultFolderOfVM(handle);
	}
	
	@Override
	public boolean CheckVHDMerging(String strRootPath, int beginSessNumber, int endSessNumber) {
		return WSJNI.CheckVHDMerging(strRootPath, beginSessNumber, endSessNumber);
	}
	
	@Override
	public long getLicenseStatus(boolean standAlone){
		try {
			long status = WSJNI.AFGetLicenseStatus(standAlone);
			logger.debug("AFGetLienseStatus() return: "+status);
			return status;
		}catch (Throwable t) {
			logger.error("Get license information meet error", t);
		}
		return -1;
	}

	@Override
	public long getRecoveryPoint4Sync( String strDest, String strDomain, String strUser, 
			String strPassword, String strVmUUID, boolean bFullSync, ArrayList<String> filePaths ){
		return WSJNI.getRecoveryPoint4Sync(strDest, strDomain, strUser, strPassword, strVmUUID, bFullSync, filePaths);
	}

	@Override
	public int getHyperVServerType(long handle) {
		return WSJNI.getHyperVServerType(handle);
	}

	@Override
	public List<String> getHyperVClusterNodes(long handle) {
		List<String> nodes = new ArrayList<String>();
		try {
			int result = WSJNI.getHyperVClusterNodes(handle, nodes);
			logger.debug("getHyperVClusterNodes() return: "+result);
			return nodes;
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
			return nodes;
		}
	}
	
	@Override
	public long startInstantVM(String paraXMLPath, int startFlag) {
		return WSJNI.startInstantVM(paraXMLPath, startFlag);
	}
	
	@Override
	public long stopInstantVM(String ivmJobUUID, long jobID, int jobType, boolean isDelete) {
		return WSJNI.stopInstantVM(ivmJobUUID, jobID, jobType, isDelete);
	}
	
	@Override
	public long startHydration(String ivmJobUUID) {
		return WSJNI.startHydration(ivmJobUUID);
	}
	
	@Override
	public long stopHydration(String ivmJobUUID) {
		return WSJNI.stopHydration(ivmJobUUID);
	}
	
	@Override
	public long getHyperVIPAddresses(long handle, String ivmUUID, List<String> ips) throws HyperVException {
		return HyperVJNI.getHyperVIPAddresses(handle, ivmUUID, ips);
	}
	
	@Override
	public long checkHyperVVMToolVersion(long handle, String ivmUUID) throws HyperVException {
			return HyperVJNI.checkHyperVVMToolVersion(handle, ivmUUID);
	}
	
	@Override
	public long getInstantVMJobStatus(JIVMJobStatus jobStatus) {
		return WSJNI.getInstantVMJobStatus(jobStatus);
	}
	
	@Override
	public void CleanShareMemory(String ivmJobUUID) {
		WSJNI.CleanShareMemory(ivmJobUUID);
	}
	
	@Override
	public long isInstantVMProxyMeetRequirement(PreCheckInstantVMProxyModel precheckProxyStruct, ArrayList<String> warningList, StringBuilder errMsg) {
		return WSJNI.isInstantVMProxyMeetRequirement(precheckProxyStruct, warningList, errMsg);
	}
	
	@Override
	public int cancelGroupJob(long jobType, long jobID, List<Long> finishedChildJobIDs) {
		return WSJNI.cancelGroupJob(jobType, jobID, finishedChildJobIDs);
	}
	
	@Override
	public long enabledScheduledExport(boolean enabled, String vmInstanceUUID) {
		return WSJNI.AFEnabledScheduledExportVM(enabled, vmInstanceUUID);
	}

	@Override
	public boolean checkScheduledExportInterval(int interval, String vmInstanceUUID) {
		return WSJNI.AFCheckShExpBackupNumVM(interval, vmInstanceUUID);
	}

	@Override
	public long addSucceedBackupNum(String vmInstanceUUID) {
		return WSJNI.AFAddSucceedBackupNumVM(vmInstanceUUID);
	}
	
	@Override
	public String getHyperVPhysicalName(String serverName, String user,
			String password, String vmInstaceUUID) {
		return WSJNI.AFGetHyperVPhysicalName(serverName, user, password, vmInstaceUUID);
	}

	@Override
	public List<ADAttribute> getADAttributes(String destination, String userName, String password, long sessionNumber, long subSessionID, String encryptedPwd, long nodeID) throws ServiceException {
		List<ADAttribute> result=new ArrayList<ADAttribute>();
		try {
			List<JGRTItem> items = new ArrayList<JGRTItem>();
			long ret = WSJNI.getGRTItem(destination, userName, password, sessionNumber, encryptedPwd, subSessionID, JGRTItem.APP_GRT_AD, JGRTItem.APP_DATA_AD_ATTr, nodeID, items);
			if(ret!=0){
				logger.error("getGRTItem() return: "+ret);
				if(ret == JGRTItem.ERROR_MOUNT_SESSION_TIMEOUT)
					throw new ServiceException(FlashServiceErrorCode.Common_ServiceRequestTimeout);
			}else{
				logger.debug("getGRTItem() return: "+ret);
			}
			for(JGRTItem item : items){
				ADAttribute attr = new ADAttribute();
				attr.setName(item.getName());
				attr.setValue(item.getValue());
				attr.setNodeID(nodeID);
				result.add(attr);
			}
		}catch(ServiceException e) {
			throw e;
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}catch(Throwable e){
			logger.error(e);
		}
		return result;
	}
	
	@Override
	public String EncryptDNSPassword(String dnsPassword) {
		 return WSJNI.EncryptDNSPassword(dnsPassword);
	}
	
	@Override
	public String DecryptDNSPassword(String dnsPassword)
	{
		return WSJNI.DecryptDNSPassword(dnsPassword);
	}

	@Override
	public List<ADNode> getADNodes(String destination, String userName, String password, long sessionNumber, long subSessionID, String encryptedPwd, long parentID) throws ServiceException {
		List<ADNode> result=new ArrayList<ADNode>();
		try {
			List<JGRTItem> items = new ArrayList<JGRTItem>();
			long ret = WSJNI.getGRTItem(destination, userName, password, sessionNumber, encryptedPwd, subSessionID, JGRTItem.APP_GRT_AD, JGRTItem.APP_DATA_AD_CHILD, parentID, items);
			if(ret!=0){
				logger.error("getGRTItem() return: "+ret);
				if(ret == JGRTItem.ERROR_MOUNT_SESSION_TIMEOUT)
					throw new ServiceException(FlashServiceErrorCode.Common_ServiceRequestTimeout);
			}else{
				logger.debug("getGRTItem() return: "+ret);
			}
			
			for(JGRTItem item : items){
				ADNode node = new ADNode();
				node.setId(item.getId());
				node.setName(item.getName());
				node.setType((int)item.getGroup());
				node.setFlags((int)item.getFlags());
				result.add(node);
			}
		}catch(ServiceException e) {
			throw e;
		}catch(Exception e) {
			logger.error(e.getMessage(), e);
			throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		}catch(Throwable e){
			logger.error(e);
		}
		return result;
	}
    
	@Override
	public String getOSVersion() {
		return WSJNI.getOSVersion();
	}
    
	@Override
	public ArrayList<DataSizesFromStorage> getDataSizesFromStorage(String path, String usrName, String usrPwd) throws ServiceException {
		ArrayList<DataSizesFromStorage>  sizesList = new ArrayList<DataSizesFromStorage>();
		
		ArrayList<JDataSizesFromStorage> jSizesList = new ArrayList<JDataSizesFromStorage>();
		long result = WSJNI.getDataSizesFromStorage(jSizesList, path, usrName, usrPwd);
		if (result >= 0) {
		    for (JDataSizesFromStorage jsizes : jSizesList) {
		    	DataSizesFromStorage sizes = new DataSizesFromStorage();
		    	sizes.setNodeUUID(jsizes.getNodeUUID());
		    	sizes.setNodeName(jsizes.getNodeName());
		    	sizes.setVmName(jsizes.getVmName());
		    	sizes.setDestination(jsizes.getDestination());
		    	sizes.setDataStorageSize(jsizes.getDataStorageSize());
		    	sizes.setRawDataSize(jsizes.getRawDataSize());
		    	sizes.setRestorableDataSize(jsizes.getRestorableDataSize());
		    	sizesList.add(sizes);
		    }
	    }
	    else
		    throw new ServiceException(FlashServiceErrorCode.Common_ErrorOccursInService);
		
		return sizesList;
	}
	
	@Override
	public int getVolumeSize(String directoryName, List<Long> sizes) {
		return WSJNI.getVolumeSize(directoryName, sizes);
	}
	
	@Override
	public String getFileCopyCatalogPath(String MachineName, long ProductType) {		
		return WSJNI.GetFileCopyCatalogPathy(MachineName, ProductType);
	}

	@Override
	public boolean isArchiveSourceDeleteJobRunning() {
		return WSJNI.IsArchiveSourceDeleteJobRunning();
	}

	@Override
	public long archiveSourceDelete(ArchiveJobScript jobScript) {
		logger.debug("archive source delete(JJobScript) - start");
		return WSJNI.AFArchiveSourceDelete(jobScript);
	}
	
	@Override
	public boolean isIVMAgentExist(String ivmJobUUID) {
		return WSJNI.isIVMAgentExist(ivmJobUUID);
	}
	
	@Override
	public int checkServiceState(String hostName, String serviceName,
			String userName, String password, JWindowsServiceModel service) {
		return WSJNI.checkServiceState(hostName,serviceName,userName,password,service);
	}
	
	@Override
	public boolean isHyperVVmExist(String guid, long handle) {
		return WSJNI.isHyperVVmExist(guid, handle);
	}
	@Override
	public String getDisplayLanguage(){
		String lang=Locale.getDefault().getLanguage();
		try {
			lang=WSJNI.GetDisplayLanguage();
		}catch(Exception e){
			logger.error("Error occurs while querying display language",e);
		}
		return lang;
	}

	@Override
	public String getHostFQDN() {
		return WSJNI.getHostFQDN();
	}

	@Override
	public long AFGetArchiveJobInfoCount(JArchiveJob out_archiveJob) {
		JRWLong jobCount = new JRWLong();
		WSJNI.AFGetArchiveJobInfoCount(out_archiveJob, jobCount);
		return jobCount.getValue();
	}
}
