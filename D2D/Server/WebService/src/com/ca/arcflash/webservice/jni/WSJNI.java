package com.ca.arcflash.webservice.jni;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.ca.arcflash.failover.model.Disk;
import com.ca.arcflash.failover.model.Volume;
import com.ca.arcflash.jni.common.JCatalogJobScriptInfo;
import com.ca.arcflash.jni.common.JJobHistory;
import com.ca.arcflash.jni.common.JJobHistoryFilterCol;
import com.ca.arcflash.jni.common.JJobHistoryResult;
import com.ca.arcflash.jni.common.JMountRecoveryPointParams;
import com.ca.arcflash.service.jni.model.JActLogDetails;
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
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.DateFormat;
import com.ca.arcflash.webservice.data.DeployUpgradeInfo;
import com.ca.arcflash.webservice.data.LicInfo;
import com.ca.arcflash.webservice.data.NetworkPath;
import com.ca.arcflash.webservice.data.SourceNodeSysInfo;
import com.ca.arcflash.webservice.data.VMwareVolumeInfo;
import com.ca.arcflash.webservice.data.archive.ArchiveCloudDestInfo;
import com.ca.arcflash.webservice.data.archive.ArchiveDestinationConfig;
import com.ca.arcflash.webservice.data.archive.ArchiveDestinationDetailsConfig;
import com.ca.arcflash.webservice.data.archive.ArchiveFileItem;
import com.ca.arcflash.webservice.data.archive.ArchiveJobInfo;
import com.ca.arcflash.webservice.data.archive.ArchiveJobScript;
import com.ca.arcflash.webservice.data.archive.JArchiveJob;
import com.ca.arcflash.webservice.data.backup.Account;
import com.ca.arcflash.webservice.data.browse.FileFolderItem;
import com.ca.arcflash.webservice.data.browse.Folder;
import com.ca.arcflash.webservice.data.catalog.SearchContext;
import com.ca.arcflash.webservice.data.merge.MergeJobMonitor;
import com.ca.arcflash.webservice.data.remotedeploy.RemoteDeployTarget;
import com.ca.arcflash.webservice.data.vsphere.HyperVHostStorage;
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
import com.ca.arcflash.webservice.jni.model.JFileComparator;
import com.ca.arcflash.webservice.jni.model.JFileInfo;
import com.ca.arcflash.webservice.jni.model.JGRTItem;
import com.ca.arcflash.webservice.jni.model.JHostNetworkConfig;
import com.ca.arcflash.webservice.jni.model.JHypervInfo;
import com.ca.arcflash.webservice.jni.model.JHypervPFCDataConsistencyStatus;
import com.ca.arcflash.webservice.jni.model.JHypervResult;
import com.ca.arcflash.webservice.jni.model.JHypervVMInfo;
import com.ca.arcflash.webservice.jni.model.JIVMJobStatus;
import com.ca.arcflash.webservice.jni.model.JJobContext;
import com.ca.arcflash.webservice.jni.model.JJobMonitor;
import com.ca.arcflash.webservice.jni.model.JJobScript;
import com.ca.arcflash.webservice.jni.model.JJobStatus;
import com.ca.arcflash.webservice.jni.model.JMachineDetail;
import com.ca.arcflash.webservice.jni.model.JMountPoint;
import com.ca.arcflash.webservice.jni.model.JMountSession;
import com.ca.arcflash.webservice.jni.model.JMountedRecoveryPointItem;
import com.ca.arcflash.webservice.jni.model.JMsgRec;
import com.ca.arcflash.webservice.jni.model.JMsgSearchRec;
import com.ca.arcflash.webservice.jni.model.JPFCVMInfo;
import com.ca.arcflash.webservice.jni.model.JRWLong;
import com.ca.arcflash.webservice.jni.model.JSearchResult;
import com.ca.arcflash.webservice.jni.model.JTestConnectionStatus;
import com.ca.arcflash.webservice.jni.model.JVMNetworkConfig;
import com.ca.arcflash.webservice.jni.model.JWriterInfo;
import com.ca.arcflash.webservice.jni.model.JWindowsServiceModel;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.service.internal.BackupConverterUtil;
import com.ca.arcflash.webservice.data.VMwareConnParams;
import com.ca.ha.webservice.jni.HyperVException;

public class WSJNI {
	
	private static final Logger logger = Logger.getLogger(WSJNI.class);
	/*
	 * This method is supposed to be called only in JNI to throw excepton about wsjni
	 */
	private static void throwWSJNIException(String errorMessage, int errorCode) throws ServiceException{
		throw new ServiceException(errorMessage, Integer.toString(errorCode));
	}

	/**
	 * @return int value:1 - user/pwd is wrong 2 - user/pwd is passed but
	 *         non-admin. 0 - the user is valid and in the admin group
	 */
	public static native int validate(String username, String domain,
			String password);

	private static native long browseAppInforamtion(ArrayList<String> list);
	
	public static native int[] getLastArchiveToTapeSession(String backupDest, String userName, String password); 

	public static String browseAppInforamtion() {
		ArrayList<String> list = new ArrayList<String>();
		long ret = browseAppInforamtion(list);
		if (ret == 0) {
			if (list.size() > 0) {
				return list.get(0);
			}
		}
		return null;
	}

	private static native long BrowseVolumeInforamtion(ArrayList<String> list, boolean details, String backupDest);
	
	public static native int GetCatalogStatus(String sessionPath);
	
	public static native int SetCatalogStatus(String backupDest, String sessionPath);
	
	public static native int UpdateCatalogJobScript(String backupDest, String userName, String passWord, long sessNum, String jobScript, String vmInstanceUUID);

	public static String BrowseVolumeInforamtion(boolean details, String backupDest) {
		ArrayList<String> list = new ArrayList<String>();
		long ret = BrowseVolumeInforamtion(list, details, backupDest);
		if (ret == 0) {
			if (list.size() > 0) {
				return list.get(0);
			}
		}
		return null;
	}

	/**
	 * @param filename
	 *            the path and the filename like c:\zz.xml
	 */
	public static native int browseAppInforamtion2File(String filename);

	private static native int browseCatalogDetail(String catalogname,
			ArrayList<JCatalogDetail> retArr);

	public static List<JCatalogDetail> browseCatalogDetail(String catalogname) {
		ArrayList<JCatalogDetail> retArr = new ArrayList<JCatalogDetail>();
		int ret = browseCatalogDetail(catalogname, retArr);
		if (ret == 0) {
			return retArr;
		}
		return null;
	}
	
	public static native long InitGRTMounter();
	
	public static native int ExitGRTMounter(long handle);

	private static native int browseCatalogChildren(String catalogname,
			long longNameID, ArrayList<JCatalogDetail> retArr);

	public static List<JCatalogDetail> browseCatalogChildren(String catalogname,
			long longNameID) {
		ArrayList<JCatalogDetail> retArr = new ArrayList<JCatalogDetail>();
		int ret = browseCatalogChildren(catalogname, longNameID, retArr);
		if (ret == 0) {
			for(int i = 0, count = retArr.size(); i < count; i++) {
				JCatalogDetail detail = retArr.get(i);
				detail.setFileDate(BackupConverterUtil.dosTime2UTC(detail.getFileDate()));
			}
			return retArr;
		}
		return null;
	}

	private static native long GenerateIndexFiles(String catalogfile);

	private static native long OpenCatalogFile(String catalogname);

	private static native int GetChildren(long handle, long longNameID,
			ArrayList<JCatalogDetail> chilren);

	public static native long GetChildrenCount(long handle, long LongNameID, JRWLong Cnt);
	
	//public static native long GetFileChildrenCount(String userName, String passWord, String backupDest, long sessionID, String volumeGUID, String parentID, JRWLong Cnt);
	public static native long GetFileChildrenCount(String volumeGUID, String parentID, JRWLong Cnt);

	public static native long GetChildrenEx(long handle, long LongNameID, int nStart, int nRequest, JRWLong Cnt, ArrayList<JCatalogDetail> retArr);


	private static native int GetFullPath(long handle, long pathID,
			ArrayList<String> fullPath);

	private static native void CloseCatalogFile(long handle);

	private static native long SearchCatalogFile(String destination, String sDir,
			boolean bCaseSensitive, boolean bIncludeSubDir, String pattern,
			JRWLong totalCnt);

	private static native int FindNextCatalogFile(long handle, long nRequest,
			ArrayList<JCatalogDetail> retArr, JRWLong nFound, JRWLong nCurrent);

	private static native long GetD2DFileSizeByte(String path, String user, String pwd,
			String d2dFile, int startSessNo, int endSessNo, int nBackupDescType);

	private static native int GetFindFullPath(long handle, long PathID,
			ArrayList<String> fullPath);

	private static native void FindCloseCatalogFile(long handle);

	public static boolean GenerateCatIndexFiles(String catalogfile) {
		long jniRet = GenerateIndexFiles(catalogfile);
		if (jniRet != 0) {
			throw new RuntimeException(
					"failed to GenerateCatIndexFiles for catalog:"
							+ catalogfile);
		}
		return true;
	}

	public static long OpenCatalog(String catalogname) {
		long handle = OpenCatalogFile(catalogname);
		/*if (handle == 0) {
			throw new RuntimeException("failed to open catalog file:"
					+ catalogname);
		}*/
		return handle;
	}

	public static ArrayList<JCatalogDetail> GetChildren(long handle,
			long longNameID) {
		ArrayList<JCatalogDetail> chilren = new ArrayList<JCatalogDetail>();
		int jniRet = GetChildren(handle, longNameID, chilren);
		if (jniRet != 0) {
			throw new RuntimeException("failed to GetChildren!");
		}
		return chilren;
	}

	public static String GetFullPath(long handle, long pathID) {
		ArrayList<String> fullPath = new ArrayList<String>(1);
		int jniRet = GetFullPath(handle, pathID, fullPath);
		if (jniRet != 0) {
			throw new RuntimeException("failed to GetFullPath!");
		}
		if (fullPath.size() > 0) {
			return fullPath.get(0);
		}
		return "";
	}

	public static void CloseGetCatalog(long handle) {
		if (handle != 0) {
			CloseCatalogFile(handle);
		}
	}

	public static SearchContext SearchCatalog(String destination, String sDir, boolean bCaseSensitive,
			boolean bIncludeSubDir, String pattern) {
		JRWLong totalCnt = new JRWLong();
		long handle = SearchCatalogFile(destination, sDir, bCaseSensitive, bIncludeSubDir,
				pattern, totalCnt);
		SearchContext sc = new SearchContext();
		sc.setContextID(handle);
		sc.setTag(totalCnt.getValue());
		return sc;
	}

	public static JSearchResult FindNextCatalog(SearchContext sc) {
		ArrayList<JCatalogDetail> retArr = new ArrayList<JCatalogDetail>();
		JRWLong nFound = new JRWLong();
		JRWLong nCurrent = new JRWLong();
		long handle = sc.getContextID();
		long nRequest = 40;
		int jniRet = FindNextCatalogFile(handle, nRequest, retArr, nFound,
				nCurrent);

		if (jniRet != 0) {
			throw new RuntimeException("failed to SearchCatalogFile!");
		}
		HashMap<Long, String> hm = new HashMap<Long, String>();
		for (JCatalogDetail cd : retArr) {
			cd.setFileDate(BackupConverterUtil.dosTime2UTC(cd.getFileDate()));
			cd.setBackupTime(BackupConverterUtil.dosTime2UTC(cd.getBackupTime()));
			Long pathId = cd.getPathID();
			if (hm.containsKey(pathId)) {
				cd.setPath(hm.get(pathId));
			} else {
				String fullPath = getFindFullPath(handle, pathId);
				cd.setPath(fullPath);
				hm.put(pathId, fullPath);
			}
		}

		JSearchResult sr = new JSearchResult();
		sr.setCurrent(nCurrent.getValue());
		sr.setFound(nFound.getValue());
		sr.setDetail(retArr);
		return sr;
	}

	public static long getD2DFileSize(String path, String user, String pwd,
				String d2dFile, int startSessNo, int endSessNo, int nBackupDescType) {
		long size = 0;

		size = GetD2DFileSizeByte(path, user, pwd, d2dFile, startSessNo, endSessNo, nBackupDescType);
		if (size <= 0) {
			throw new RuntimeException("failed to getD2DFileSize!");
		}

		return size;
	}

	public static String getFindFullPath(long handle, long pathID) {
		ArrayList<String> fullPath = new ArrayList<String>(1);
		int jniRet = GetFindFullPath(handle, pathID, fullPath);
		if (jniRet != 0) {
			throw new RuntimeException("failed to getFindFullPath!");
		}
		if (fullPath.size() > 0) {
			return fullPath.get(0);
		}
		return "";
	}

	public static void CloseSearchCatalog(long handle) {
		if (handle != 0) {
			FindCloseCatalogFile(handle);
		}
	}

	public static native long AFBackup(JJobScript backupJob);

	public static native long AFRestore(JJobScript restoreJob);

	private static native long AFSetJobStatus(JJobStatus pJobStatus);

	private static native long AFGetJobStatus(JJobStatus pJobStatus);
	
	/**
	 * @deprecated As of D2D r16 update7
	 * replaced by {@link #launchCatalogEx(CatalogJobContext)}
	 * 
	 */
	public static native long launchCatalogJob(long id, long type, String adminName, String adminPass);
	
	/**
	 * @deprecated As of D2D r16 update7
	 * replaced by {@link #launchCatalogEx(CatalogJobContext)}
	 */
	public static native long launchVSphereCatalogJob(long jobId, long type, String vmIndentification,String adminName, String adminPass);

	public static void SetJobStatus(JJobStatus jobStatus) {
		AFSetJobStatus(jobStatus);
	}

	public static JJobStatus GetJobStatus() {
		JJobStatus jobStatus = new JJobStatus();
		long jniRet = AFGetJobStatus(jobStatus);
		if (jniRet != 0) {
			throw new RuntimeException("failed to AFGetJobStatus!");
		}
		return jobStatus;
	}

	public static native int GetRecoveryPoint(String destination, String domain, String userName, String pwd,
			String beginDate, String endDate, List<JRestorePoint> list, boolean isQueryDetail);
	
	//zxh,mark:ASBUGetRecoveryPoint, add ASBUGetRecoveryPoint JNI
	public static native int ASBUGetRecoveryPoint(String destination, String beginDate, String endDate, List<JRestorePoint> list, boolean isQueryDetail);

	/*public static native int GetBackupInfoSummary(String destination, String domain, String userName, String pwd,
			JBackupInfoSummary summary, boolean onlyDestCapacity);*/
	public static native int GetBackupInfoSummary(String destination, String domain, String userName, String pwd,
			JBackupInfoSummary summary, boolean onlyDestCapacity,String uuid);

	public static native int GetDestSizeInformation(String destination, JBackupDestinationInfo dest);

	public static native int GetProtectionInformation(String destination, String domain, String userName, String pwd,
			List<JProtectionInfo> list);
	
	public static native int GetCopyProtectionInformation(String destination, String domain, String userName, String pwd,
			List<JProtectionInfo> list);

	public static native int GetRecentBakcupInfo(String destination, String domain, String userName, String pwd, int type,
			int status, int count, List<JBackupInfo> list,String foledername);
	
	public static native int GetRecentBackupsByServerTime(String destination, String domain, String userName, String pwd, int type,
			int status, String beginDate, String endDate, List<JBackupInfo> list,String foledername);
	
	/**
	 * this function will get RestorePoints from backuphistory directory during the specified period
	 * @param destination
	 * @param domain
	 * @param userName
	 * @param pwd
	 * @param beginDate
	 * @param endDate
	 * @param count, -1 to no limit for count
	 * @param list
	 * @return
	 */
	public static native int GetBakcupInfo(String destination, String domain, String userName, String pwd, String beginDate, String endDate,
			int count,List<JBackupInfo> list);


	public static native int LogActivity(long level, long resourceID, String[] param);
	public static native int LogActivityWithID(long level, long jobID,long resourceID, String[] param);
	public static native int VMLogActivity(long level, long resourceID, String[] param,String vmIndentification);
	public static native int VMLogActivityWithID(long level, long jobID,long resourceID, String[] param,String vmIndentification);
	public static native int LogActivityWithDetailsEx(JActLogDetails logDetails, long resourceID, String[] param);
	public static native int GetLogActivity(int start, int count, JActivityLogResult result);


	public static native int GetJobLogActivity(long jobNo, int start, int count, JActivityLogResult result);

	public static native int DeleteLogActivity(int year, int month, int day, int hour, int minute, int second);
	public static native String AFEncryptString(String toEncrypt);

	public static native String AFDecryptString(String toDecrypt);
	
	public static native String AFEncryptStringEx(String toEncrypt);

	public static native String AFDecryptStringEx(String toDecrypt);
	
	public static native byte[] GetRegBinaryValue (String rootReg, String valueName);
	
	public static native int SetRegBinaryValue (String rootReg, String valueName, byte[] byteArray);
	
	public static native byte[] AFEncryptBinary(byte[] input);
	public static native byte[] AFDecryptBinary(byte[] input);


	public static native boolean AFCheckCompressLevelChanged(JNetConnInfo info, int level);

	public static native boolean AFCheckEncryptionAlgorithmAndKeyChanged(JNetConnInfo info, int encryptionAlgorithm, String encryptionKey);


	//add a new parameter for this method to indicate whether to install driver.
	public static native void StartToDeployWithDriver(String localDomain,
			String localUser, String localPassword, String uuid,
			String serverName, String username, String password, int port,
			String installDirectory, boolean autoStartRRService, boolean reboot, boolean installDriver,
			boolean useHttps, boolean resumedAndCheck, IRemoteDeployCallback callback);
	// Agent Deploy Tool - Start
	public static native void StartToDeploy(String localDomain,
			String localUser, String localPassword, String uuid,
			String serverName, String username, String password, int port,
			String installDirectory, boolean autoStartRRService, boolean reboot, boolean resumedAndCheck,
			IRemoteDeployCallback callback);

	// Agent Deploy Tool - End
	/**
	 * type // 0: both files / folder; 1: folders only; 2: files only
	 */
	private static native long BrowseFileFolderItemEx(String path,int type,int maxCount, String domain,
			String userName, String pwd, ArrayList<JFileInfo> list);
	
	public static FileFolderItem browseFileFolderItemEx(String path,
			String userName, String pwd,int maxCount) throws ServiceException {
		ArrayList<JFileInfo> JFileList = new ArrayList<JFileInfo>();
		ArrayList<JFileInfo> JFolderList = new ArrayList<JFileInfo>();
		long dwRet_file = BrowseFileFolderItemEx(path, 2, maxCount,"", userName, pwd, JFileList);
		long dwRet_folder = BrowseFileFolderItemEx(path, 1, maxCount,"", userName, pwd, JFolderList);
		logger.info("dwRet_file:" + dwRet_file);
		logger.info("dwRet_folder:" + dwRet_folder);
		logger.info("maxCount:" + maxCount);
		if (dwRet_file != 0) {
			String msg = WSJNI.AFGetErrorMsg(dwRet_file);
			if(dwRet_file == 5) // access denied
				throw new ServiceException(msg, FlashServiceErrorCode.Browser_GetFolderFailed_Acess_Denied);
			else if(dwRet_file == 259) //no folder exists
				throw new ServiceException(FlashServiceErrorCode.Browser_GetFolder_No_Content, new String[] {msg});
			else
				throw new ServiceException(msg, FlashServiceErrorCode.Browser_GetFolderFailed);
		}
		if (dwRet_folder!=0) {
			String msg = WSJNI.AFGetErrorMsg(dwRet_folder);
			if(dwRet_folder==5) // access denied
				throw new ServiceException(msg, FlashServiceErrorCode.Browser_GetFolderFailed_Acess_Denied);
			else if(dwRet_folder==259) //no folder exists
				throw new ServiceException(FlashServiceErrorCode.Browser_GetFolder_No_Content, new String[] {msg});
			else
				throw new ServiceException(msg, FlashServiceErrorCode.Browser_GetFolderFailed);
		}
		
		//sort by name ,ignoring case differences
		JFileComparator compatator=new JFileComparator();
		Collections.sort(JFileList,compatator);
		Collections.sort(JFolderList,compatator);
		
		List<Folder> folderList = new LinkedList<Folder>();
		List<com.ca.arcflash.webservice.data.browse.File> fileList = new LinkedList<com.ca.arcflash.webservice.data.browse.File>();

		for (JFileInfo finfo : JFileList) {

			com.ca.arcflash.webservice.data.browse.File fileItem = new com.ca.arcflash.webservice.data.browse.File();
			fileItem.setPath(finfo.getStrPath());
			fileItem.setSize(finfo.getNFileSizeLow()| finfo.getNFileSizeHigh() << 32);
			fileItem.setLastUpdateDate(new Date(finfo.getLastWriteDateTime() * 1000));
			fileItem.setCreationDate(new Date(finfo.getCreationDateTime() * 1000));
			fileItem.setName(finfo.getStrName());
			fileList.add(fileItem);
		}

		for(JFileInfo finfo : JFolderList){
			Folder folder = new Folder();
			folder.setName(finfo.getStrName());
			folder.setLastUpdateDate(new Date(
					finfo.getLastWriteDateTime() * 1000));
			folder.setCreationDate(new Date(
					finfo.getCreationDateTime() * 1000));
			folder.setPath(finfo.getStrPath());
			folderList.add(folder);
		}
		FileFolderItem result = new FileFolderItem();
		result.setFiles(fileList
				.toArray(new com.ca.arcflash.webservice.data.browse.File[0]));
		result.setFolders(folderList.toArray(new Folder[0]));
		return result;
	}
 
	private static native long BrowseFileFolderItem(String path, String domain,
			String userName, String pwd, ArrayList<JFileInfo> list);

	public static FileFolderItem browseFileFolderItem(String path,
			String userName, String pwd) throws ServiceException {
		ArrayList<JFileInfo> list = new ArrayList<JFileInfo>();
		long dwRet = BrowseFileFolderItem(path, "", userName, pwd, list);

		logger.info("dwRet:" + dwRet);
		if (dwRet != 0) {
			String msg = WSJNI.AFGetErrorMsg(dwRet);
			if(dwRet == 5) // access denied
				throw new ServiceException(msg, FlashServiceErrorCode.Browser_GetFolderFailed_Acess_Denied);
			else if(dwRet == 259) //no folder exists
				throw new ServiceException(FlashServiceErrorCode.Browser_GetFolder_No_Content, new String[] {msg});
			else
				throw new ServiceException(msg, FlashServiceErrorCode.Browser_GetFolderFailed);
		}

		List<Folder> folderList = new LinkedList<Folder>();
		List<com.ca.arcflash.webservice.data.browse.File> fileList = new LinkedList<com.ca.arcflash.webservice.data.browse.File>();

		for (JFileInfo finfo : list) {
			if (finfo.isDirectory()) {
				Folder folder = new Folder();
				folder.setName(finfo.getStrName());
				folder.setLastUpdateDate(new Date(
						finfo.getLastWriteDateTime() * 1000));
				folder.setCreationDate(new Date(
						finfo.getCreationDateTime() * 1000));
				folder.setPath(finfo.getStrPath());
				folderList.add(folder);
			} else {
				com.ca.arcflash.webservice.data.browse.File fileItem = new com.ca.arcflash.webservice.data.browse.File();
				fileItem.setPath(finfo.getStrPath());
				fileItem.setSize(finfo.getNFileSizeLow()
						| finfo.getNFileSizeHigh() << 32);
				fileItem.setLastUpdateDate(new Date(finfo
						.getLastWriteDateTime() * 1000));
				fileItem.setCreationDate(new Date(
						finfo.getCreationDateTime() * 1000));
				fileItem.setName(finfo.getStrName());
				fileList.add(fileItem);
			}
		}

		FileFolderItem result = new FileFolderItem();
		result.setFiles(fileList
				.toArray(new com.ca.arcflash.webservice.data.browse.File[0]));
		result.setFolders(folderList.toArray(new Folder[0]));
		return result;
	}

	public static native long AFCheckFolderAccess(JNetConnInfo pDest,
			ArrayList<JFileInfo> info);

	public static native long AFInitDestination(JNetConnInfo newDest,
			JNetConnInfo oldDest, long dwBakType, boolean isCopy);

	public static native long AFRetrieveSharedResource(JNetConnInfo pDest,
			ArrayList<String> ret);

	public static JFileInfo checkFolderAccess(JNetConnInfo pDest) {
		ArrayList<JFileInfo> info = new ArrayList<JFileInfo>(1);
		AFCheckFolderAccess(pDest, info);
		return info.get(0);
	}

	public static native long createJobMonitor(long id);

	private static native long getJobMonitor(long address, JJobMonitor jobMonitor);

	public static long getJobmonitor(long address, JJobMonitor jobMonitor) {
		return getJobMonitor(address, jobMonitor);
	}

	public static native void releaseJobMonitor(long shmId);

	private static native long TestADTConnect(String localDomain,
			String localUser, String localPwd, String remoteServerName,
			String remoteUser, String remotePwd, String remoteInstallPath,
            boolean bAutoStartRRService,DeployUpgradeInfo deployUpgrade);

	private static native long TestADTConnectWithDriver(String localDomain,
			String localUser, String localPwd, String remoteServerName,
			String remoteUser, String remotePwd, String remoteInstallPath,
            boolean bAutoStartRRService,boolean installDriver, DeployUpgradeInfo deployUpgrade);

	public static long validRemoteDeploy(String localDomain, String localUser,
			String localPassword, RemoteDeployTarget remoteTarget,DeployUpgradeInfo deployUpgrade) {
		String remoteServerName = remoteTarget.getServerName();
		String remoteUser = remoteTarget.getUsername();
		String remotePwd = remoteTarget.getPassword();
		long ret = TestADTConnectWithDriver(localDomain, localUser, localPassword,
				remoteServerName, remoteUser, remotePwd, remoteTarget.getInstallDirectory(), remoteTarget.isAutoStartRRService(),remoteTarget.isIntallDriver(), deployUpgrade);
		return ret;
	}

	public static native boolean getJobID(JRWLong jobID);
	
	public static native boolean getJobIDs(int count, JRWLong jobID);

	public static native long getCurrentJobID();

	public static native boolean checkJobExist();

	public static native boolean checkBMRPerformed();

	public static native long AFCopy(JJobScript restoreJob);

	public static native int cancelJob(long jobID);

	public static native String AFGetErrorMsg(long dwErr);

	public static native long AFGetPathMaxLength();
	
	public static native long AFGetVSpherePathMaxLength();

	public static native long AFCheckDestValid(String destination);

	/**
	 * 
	 * @param strPath destination path
	 * @param serverName current server name
	 * @param changedFull whether start with full when change backup destination
	 * @param strHostName returned host name list
	 * @return
	 */
	public static native long AFCheckDestNeedHostName(String strPath, String serverName, String nodeId, boolean isCreate, ArrayList<String> strHostName);

	public static native long AFSaveAdminAccount(Account account);

	public static native long AFReadAdminAccount(Account account);

	public static native long AFCheckAdminAccountValid(Account account);

	public static native long AFGetBakDestDriveType(String path);

	public static native long AFCutConnection(JNetConnInfo connInfo, boolean force);

	public static native long AFCreateConnection(JNetConnInfo connInfo);

	public static native boolean AFCheckPathAccess(JNetConnInfo connInfo);

	public static native long AFCreateDir(String parentPath, String subDir);

	public static native long AFSetThreshold(long threshold);

	public static native String AFTryToFindDest(String oldDestination);

	public static native long AFCheckSQLAlternateLocation(String basePath, String instName, String dbName, ArrayList<String> alterDestPath);

	public static native boolean AFCheckBLILic();

	public static native long AFCheckDestContainRecoverPoint(JNetConnInfo connInfo);

	public static native long AFCheckDirPathValid(JNetConnInfo connInfo);

	public static native boolean AFCheckBaseLic();

	public static native int GetLocalPackage();

	public static native long GatherExcludedFileListInWriter(List<String> volumeList, List<JApplicationWriter> appList);

	public static native long RegenerateWriterMetadata();

	public static native long AFGetLocalDestVolumes(String destPath, List<String> destList);

	public static native long GetBackupItem(String dest, String domain, String user,String pwd, String subPath, List<JBackupItem> bkpItemList);

	public static native long AFGetNetworkPathForMappedDrive(String userName, List<NetworkPath> pathList);

	public static native LicInfo AFGetLicenseInfo(List<String> volLst);
	public static native LicInfo AFGetLicenseError();
	public static native LicInfo AFGetLicenseInfoEx(List<String> volLst, boolean filterLicInfo);
	public static native boolean AFCheckSessExist(String strDest, long dwNum);
	public static native long AFGetMntFromPath(String path, List<String> vMnt);

	public static native boolean AFValidateSessPassword(String password, String destination,
			long sessionNum);
	public static native boolean AFValidateSessPasswordByHash(String password, long lpwdlen, String hashvalue, long lhashlen);

	public static native String[] AFGetSessionPasswordBySessionGuid(String[] sessionGuid);

	public static native void AFUpdateAdminAccountInKeyMgmtDB(String adminUser, String password);
	
	//
	//	/*Patch Manager */
	//
	@Deprecated
	public static native int CreateFile(String fileName, int desiredAccess,
			int shareMode, int securityAttributes, int creationDisposition,
			int flagsAndAttributes, int templateFile);

	@Deprecated
	public static native String ReadFile(int file);
	

	public static native boolean IsPatchManagerRunning(String runningMutexName);

	@Deprecated
	public static native boolean IsPatchManagerRunningEx(String runningMutexName, int type);
	
	@Deprecated
	public static native int IsPatchManagerReady();

	@Deprecated
	public static native int WriteFile(int namedPipeHandle,
			String mCheckupDateReq, int length);

	@Deprecated
	public static native boolean CloseFile(int file);

	@Deprecated
	public static native int CreateFileEx(String fileName, int desiredAccess,
			int shareMode, int securityAttributes, int creationDisposition,
			int flagsAndAttributes, int templateFile, int type);

	@Deprecated
	public static native String ReadFileEx(int file, int type);

	@Deprecated
	public static native int WriteFileEx(int namedPipeHandle, String mCheckupDateReq,int length, int type);
	
	@Deprecated
	public static native boolean CloseFileEx(int file, int type);
	
	@Deprecated
	public static native long GetLastError();

	@Deprecated
	public static native String FormatMessage(int errorCode);

	@Deprecated
	public static native JTestConnectionStatus testDownloadServerConnection(
			String strDownloadURI, int iServerType, String strDownloadServer,
			String strDownloadServerPort, String strProxyServerName,
			String strProxyPort, String strProxyUserName,
			String strProxyPassword);
	
	@Deprecated
	public static native JTestConnectionStatus testDownloadServerConnectionEx(
			String strDownloadURI, int iServerType, String strDownloadServer,
			String strDownloadServerPort, String strProxyServerName,
			String strProxyPort, String strProxyUserName,
			String strProxyPassword, int type);
	
	//added by cliicy.luo
	@Deprecated
	public static native JTestConnectionStatus testBIDownloadServerConnection(
			String strDownloadURI, int iServerType, String strDownloadServer,
			String strDownloadServerPort, String strProxyServerName,
			String strProxyPort, String strProxyUserName,
			String strProxyPassword);
	
	@Deprecated
	public static native JTestConnectionStatus testBIDownloadServerConnectionEx(
			String strDownloadURI, int iServerType, String strDownloadServer,
			String strDownloadServerPort, String strProxyServerName,
			String strProxyPort, String strProxyUserName,
			String strProxyPassword, int type);
	//added by cliicy.luo
	
	@Deprecated
	public static native long AFRunPatchJob(String spatchURL);

	public static native long testUpdateServerConnection(	int iServerType, String strDownloadServer,
			String strDownloadServerPort, String strProxyServerName,
			String strProxyPort, String strProxyUserName,
			String strProxyPassword);
	
	public static native long testUpdateServerConnectionEx(	int iServerType, String strDownloadServer,
			String strDownloadServerPort, String strProxyServerName,
			String strProxyPort, String strProxyUserName,
			String strProxyPassword, int type);
	
	//added by cliicy.luo
	public static native long testBIUpdateServerConnection(	int iServerType, String strDownloadServer,
			String strDownloadServerPort, String strProxyServerName,
			String strProxyPort, String strProxyUserName,
			String strProxyPassword);
	
	public static native long testBIUpdateServerConnectionEx(	int iServerType, String strDownloadServer,
			String strDownloadServerPort, String strProxyServerName,
			String strProxyPort, String strProxyUserName,
			String strProxyPassword, int type);
	
	public static native long installBIUpdate( );
	
	public static native long installBIUpdateEx( int type );
	
	public static native String getBIUpdateStatusFile();
	
	public static native String getBIUpdateStatusFileEx(int type);
	//added by cliicy.luo
	
	public static native boolean IsPatchManagerBusy(String busyMutexName);

	public static native boolean IsPatchManagerBusyEx(String busyMutexName, int type);
	
	public static native long checkUpdate();
	
	public static native long checkUpdateEx(int type);
	
	//added by cliicy.luo to add Hotfix menu-item
	public static native long checkBIUpdateEx(int type);
	public static native long checkBIUpdate(int type);
	
	public static native long checkBIUpdateEx();
	public static native long checkBIUpdate();
	//added by cliicy.luo to add Hotfix menu-item

	public static native String getUpdateStatusFile();
	
	public static native String getUpdateStatusFileEx(int type);
	
	public static native String getUpdateSettingsFile();
	
	public static native String getUpdateSettingsFileEx(int type);
	
	public static native long installUpdate( );
	
	public static native long installUpdateEx( int type );
	
	public static native String getUpdateErrorMessage( int errorCode );
	
	public static native String getUpdateErrorMessageEx( int errorCode, int type );
	//
	// patch manager end
	//
	
	public static native String GetCacheFile4Sync();

	public static native long DeleteCacheFile4Sync();

	public static native String GetReSyncCacheFile();

	public static native String GetD2DSysFolder();

	public static native int cancelVMJob(long jobID,String vmIdentification);

	public static native long createVMJobMonitor(String vmIdentification);

	public static native long AFCheckDestNeedVMInfo(String strPath, String serverName, ArrayList<String> strHostName,String instanceUUID);

    public static native long GetBackupVMList(String destination,String domain,String username,String password,List<JBackupVM> backupVMList);
    
    public static native long GetBackupVM(String destination,String domain,String username,String password,List<JBackupVM> backupVMList);

	public static native boolean AFCheckVMDestination(String destination,String domain,String username,String password);

	public static native long AFGetBackupVMDisk(String destination,String subPath,String domain,String username,String password,List<JDisk> diskList);
	
	public static native long AFGetBackupVMOriginalInfo(String destination,int sessionNum,String domain,String username,String password,JBackupVMOriginalInfo originalInfo);

	public static native int GetVMLogActivity(int start, int count, JActivityLogResult result,String vmUUID);

	public static native int GetVMJobLogActivity(long jobNo, int start, int count, JActivityLogResult result,String vmUUID);

	public static native int GetJobLogActivityForVM(long jobNo, int start, int count, JActivityLogResult result,String vmUUID);

	public static native int DeleteVMLogActivity(int year, int month, int day, int hour, int minute, int second,String vmUUID);

	public static native String GetVDDKVersion();
	
	public static native String GetVIXVersion();
	
	public static native long GetAllBackupDestinations(String destDir,List<String> destinations);

	public static native long GetReplicatedSessions(String destDir,	List<String> destinations, String serverName, String serverPort, String username, String password);

	public static native long HyperVRep(HyperVRepParameterModel copyModel);

	public static native long getFlashAdrConfigureXML(String d2dBinLogsPath) throws WSJNIException;
	public static native long getDiskSignatureFromGPTGuid(String diskGuid) throws WSJNIException;

	//public static native String GetDiskSignatureFromGUID(String guid);

	public static native Map<String, String> GetHostAdapterList();

	public static native void EnableHostDHCP(String adapterName);

	public static native void EnableHostStatic(String adapterName, List<String> ipAddresses, List<String> vMasks);

	public static native void EnableHostDNS(String adapterName);

	public static native void SetHostDNSDomain(String adapterName,String dnsDomain);

	public static native void SetHostGateways(String adapterName, List<String> gateways, List<Integer> costMetrics);

	public static native void SetHostDNSServerSearchOrder(String adapterName, List<String> vDNSServerSearchOrder);

	public static native boolean HAIsHostOSGreaterEqual(int dwMajor,int dwMinor,short servicePackMajor,
			 short servicePackMinor) throws WSJNIException;
	
	public static native int CheckFolderCompressAttribute(String folderPath) throws WSJNIException;
	
	public static native int CheckVolumeCompressAttribute(String volumePath) throws WSJNIException;
	
	public static native int GetOnlineDisksAndVolumes(Vector<Disk> vecDisk, Vector<Volume> vecVolume) throws WSJNIException;

	public static native Vector<Disk> GetOnlineDisks()
	throws WSJNIException;

	public static native Vector<Volume> GetOnlineVolumes()
	throws WSJNIException;

	public static native Vector<JHostNetworkConfig> GetHostNetworkConfig()
	throws WSJNIException;

	public static native short GetHostProcessorArchitectural()
	throws WSJNIException;

	public static native long GetOfflinecopySize(String rootDest) throws WSJNIException;

	public static native int AdjustVolumeBootCodeForHyperV(String pwszVmGuid, String pwszSnapGuid) throws HyperVException;

	public static native int CancelReplicationForHyperV(String jobId) throws WSJNIException;

	public static native int StopHAServerProxy() throws WSJNIException;

	public static native boolean CancelReplicationForVMware(String jobId) throws WSJNIException;

	public static native boolean IsHyperVRoleInstalled();
	
	public static native int OnlineDisks();

	public static native int GenerateAdrconfigure(String outputDest);
	
	public static native int GenerateIVMAdrconfigure(String outputDest, String vmuuid, String vmname);
	
	public static native String GenerateAdrInfoC();
	
	public static native String GetDrInfoLocalCopyPath(String vmname,String vmuuid);
	
	public static native String GetAdrInfoCLocalCopyPath(String vmname,String vmuuid);
	
	public static native String GetDestSubRoot(HyperVRepParameterModel parameterModel) throws WSJNIException;

	// chefr03: SMART_COPY_BITMAP
	public static native int CreateSessionBitmap(String srcSessionDest, String destSessionDest, int nBackupDescType) throws WSJNIException;
	public static native int DeleteSessionBitmap(String sessionDest, String sessionName) throws WSJNIException;
	public static native int GetSessionBitmapList(String sessionDest, List<String> vBitmapList) throws WSJNIException;
	public static native int DetectAndRemoveObsolteBitmap(String sessionDest,String beginSession, String endSession) throws WSJNIException;
	// chefr03: SMART_COPY_BITMAP

	public static native int GetIpAddressFromDns(String hostName, List<String> ipList) throws WSJNIException;

	public static native long MsgOpenCatalogFile(String catalogname);
	public static native long MsgGetChildren(long handle, long lowSelfid, long highSelfid, ArrayList<JMsgRec> retArr);
	public static native String GetMsgCatalogPath(String dbIdentify, String backupDestination, long sessionNumber, long subSessionNumber);
	public static native long MsgGetChildrenCount(long handle, long lowSelfID, long highSelfID, JRWLong Cnt);
	public static native long MsgGetChildrenEx(long handle, long lowSelfID, long highSelfID, int nStart, int nRequest, JRWLong Cnt, ArrayList<JMsgRec> retArr);

	public static native long MsgGetChildrenByFilterFirst(long handle, long nRequestIndex, long lowSelfID, long highSelfID, String queryString, long nRequest, JRWLong cnt, JRWLong total, ArrayList<JMsgRec> retArr);
	public static native long MsgGetChildrenByFilterNext (long handle, long nRequestIndex, long nStart, long nRequest, JRWLong cnt, ArrayList<JMsgRec> retArr);

	public static native long D2DExCheckUser(String domain, String user, String password);
	public static native int FindNextMsgCatalogFile(long handle, long nRequest, ArrayList<JMsgSearchRec> retArr, JRWLong nFound, JRWLong nCurrent);

	//pidma02
	public static native long SearchMountPoint(String mntVolumeGUID, String backupDestination, String volDisplayName, String sDir, boolean bCaseSensitive, boolean bIncludeSubDir, String pattern);
	
	public static native int FindNextSearchItems(long jhandle, long nRequest, ArrayList<JCatalogDetail> retArr, JRWLong nFound);
	
	public static native int FindCloseSearchItems(long jhandle);
	
	
	//pidma02: Support for search in single session for catalog enabled sessions
	private static native long SearchCatalogFileEx(String catalogSessionPath, String sDir,
			boolean bCaseSensitive, boolean bIncludeSubDir, String pattern,
			JRWLong totalCnt);

	//pidma02: Support for search in single session for catalog enabled sessions
	private static native int FindNextCatalogFileEx(long handle, long nRequest,
			ArrayList<JCatalogDetail> retArr, JRWLong nFound, JRWLong nCurrent);

	public static JSearchResult GetNextSearchItems(SearchContext context)
	{
		ArrayList<JCatalogDetail> retArr = new ArrayList<JCatalogDetail>();
		JRWLong nFound = new JRWLong();
		JRWLong nCurrent = new JRWLong();
		long handle = context.getContextID();
		long nRequest = 40;
		
		int jniRet = FindNextSearchItems(handle, nRequest, retArr, nFound);

		if (jniRet != 0) {
			throw new RuntimeException("failed to SearchCatalogFile!");
		}
		//HashMap<Long, String> hm = new HashMap<Long, String>();
		for (JCatalogDetail cd : retArr) {
			cd.setFileDate(BackupConverterUtil.dosTime2UTC(cd.getFileDate()));
			cd.setBackupTime(BackupConverterUtil.dosTime2UTC(cd.getBackupTime()));
			/*Long pathId = cd.getPathID();
			if (hm.containsKey(pathId)) {
				cd.setPath(hm.get(pathId));
			} else {
				String fullPath = getFindFullPath(handle, pathId);
				cd.setPath(fullPath);
				hm.put(pathId, fullPath);
			}*/
		}

		JSearchResult sr = new JSearchResult();
		//sr.setCurrent(nCurrent.getValue());
		sr.setFound(nFound.getValue());
		sr.setDetail(retArr);
		return sr;
	}

	public static JSearchResult FindNextMsgCatalog(SearchContext sc) {
		ArrayList<JMsgSearchRec> retArr = new ArrayList<JMsgSearchRec>();
		JRWLong nFound = new JRWLong();
		JRWLong nCurrent = new JRWLong();
		long handle = sc.getContextID();
		long nRequest = 40;
		long jniRet = FindNextMsgCatalogFile(handle, nRequest, retArr, nFound, nCurrent);

		if (jniRet != 0) {
			throw new RuntimeException("failed to SearchCatalogFile!");
		}
		
		for (JMsgSearchRec cd : retArr) {
			cd.setUlBKTime(BackupConverterUtil.dosTime2UTC(cd.getUlBKTime()));			
		}
		
		JSearchResult sr = new JSearchResult();
		sr.setCurrent(nCurrent.getValue());
		sr.setFound(nFound.getValue());
		sr.setMsgDetail(retArr);
		return sr;
	}

	public static native String AOEGetOrganizationName(String strUser, String strPassword);
	public static native long AOEGetServers(ArrayList<JExchangeDiscoveryItem> retArr,String strUser, String strPassword);
	public static native long AOEGetStorageGroups(String dn, ArrayList<JExchangeDiscoveryItem> retArr, String strUser, String strPassword);
	public static native long AOEGetEDBs(String dn, ArrayList<JExchangeDiscoveryItem> retArr,String strUser, String strPassword);
	public static native long AOEGetMailboxes(String dn, ArrayList<JExchangeDiscoveryItem> retArr, String strUser, String strPassword);
	public static native long AOECheckServiceStatus(String serviceName);
	public static native long AHDesktopFile(String backupDestination);
	public static native long AFCatalogJob(JCatalogJob catalogJob);
	public static native boolean AFIsCatalogJobRunning(String backupDestination, long sessionNumber, long subSessionNumber);
	/**
	 * If there is catalog job to run, the returned value is the job type {@link JobType#JOBTYPE_CATALOG_FS} 
	 * 	or {@link JobType#JOBTYPE_CATALOG_GRT}; or return 0.
	 * @param dwQueueType
	 * @param pwzJobQIdentity
	 * @return
	 */
	public static native long AFIsCatalogAvailable(long dwQueueType, String pwzJobQIdentity, String rpsSvrIdentity);
	public static native boolean AFIsCatalogJobInQueue(long queueType, String backupDestination, long sessionNumber, long subSessionNumber, String vmInstanceUUID);
	public static native long AFCGRTSkipDisk(String volName, JRWLong skipped);
	public static native long CheckSessVerByNo(String destination, long sessionNumber);

	public static native void RebootSystem(boolean force) throws WSJNIException;

	public static native String FindHAConfigurationFileURL(String lastRepDest, String fileName) throws WSJNIException;

	public static native String FindHAConfigurationFileURLByVMGUID(String vmguid, String fileName) throws WSJNIException;

	public static native String GetD2DActiveLogTransFileXML();
	public static native long DelD2DActiveLogTransFileXML();
	public static native String GetFullD2DActiveLogTransFileXML();
	public static native boolean IsFirstD2DSyncCalled();
	public static native void MarkFirstD2DSyncCalled();

	//Archive
	public static native long AFGetArchiveDestinationVolumes(String hostName,ArchiveDestinationConfig destConfig,List<String> volumeList);
	public static native long searchArchiveCatalogChildren(String inFileName,String inHostName, String inSearchpath, ArchiveDestinationConfig inArchiveDestConfig,
			long in_lSearchOptions, long lIndex, long lCount, ArrayList<JArchiveCatalogDetail> returnList)throws NoSuchMethodException;


	public static native long GetArchivedVolumeHandle(String strVolume);

	public static native long browseArchiveCatalogChildren(
			long inVolumeHandle, String inCatalogFilePath,List<JArchiveCatalogDetail> archiveCatalogItems);
	public static native long browseArchiveCatalogChildrenEx(
			long inVolumeHandle, String inCatalogFilePath,long lIndex,long lCount,List<JArchiveCatalogDetail> archiveCatalogItems);

	public static native long GetArchiveChildrenCount(long inVolumeHandle,
			String path, JRWLong childrenCnt);

	public static native long AFArchive(ArchiveJobScript archiveJob);
	public static native long AFArchiveRestore(ArchiveJobScript Job);
	public static native long AFArchiveCatalogSync(ArchiveJobScript Job);
	public static native long AFArchiveSourceDelete(ArchiveJobScript Job);

	public static native long CanArchiveJobBeSubmitted(JArchiveJob outJArchiveJob);

	public static native long AFArchivePurge(ArchiveJobScript jobScript);

	public static native long AFGetArchivableFilesInformation(String archiveSourcePoliciesFilePath,List<ArchiveFileItem> out_filesInfo);

	public static native long GetPreviewFilesHandle(String archiveSourcePoliciesFilePath,JRWLong out_jHandle,JRWLong out_jFilesCount);
	public static native long ReadPreviewFilesList(long in_lHandle,long in_lIndex,long in_lCount,List<JFileInfo> archiveFilesList)throws NoSuchMethodException;
	public static native long ClosePreviewFilesSession(long lhandle);

	public static native long GetArchiveJobsInfo(JArchiveJob inArchiveJob,List<ArchiveJobInfo> archiveJobsList) ;

	public static native boolean IsArchiveJobRunning();
	public static native boolean IsArchiveRestoreJobRunning();
	public static native boolean IsArchivePurgeJobRunning();
	public static native boolean IsArchiveCatalogSyncJobRunning();
	public static native boolean IsArchiveSourceDeleteJobRunning();
	public static native boolean IsFileArchiveJobRunning();

	public static native String GetArchiveDNSHostName();
	public static native String GetArchiveDNSHostSID();
	
	public static native  long GetLastArchiveCatalogUpdateTime(ArchiveDestinationConfig inArchiveDestConfig,ArchiveDestinationDetailsConfig outDestinationChangeDetails);

    public static native long  AFSCheckCatalogExist(String destination, long sessionNumber, ArrayList<JCatalogInfo> retArr);
	public static native long  AFCheckGrtSession(String backupDestination, long sessionNumber);
	
	public static native long AFSaveJS4FSOndemand(String backupDestination, String userName, String password, String domain, long sessionNumber,String vmInstanceUUID,
				long subSessionNumber, String sessionPass);
    
    public static native long addGRTCatalogInfo(String backupDestination, String userName, String password, String domain, long sessionNumber,
			long subSessionNumber, String sessionPass, List<String> grtEdbList, String vmInstanceUUID);
    public static native String GetCachedVmInfo4Trans();
    public static native String GetAllVmInfo4Trans();
    public static native long DeleteVmInfoTransFile();
    public static native long DeleteAllVmInfoTransFile();
    public static native String GetArchiveCacheFileName4Trans();
    public static native long DeleteArchiveCacheFileTrans();
	
	   //cloud test
    
	public static native long verifyBucketName(ArchiveCloudDestInfo cloudDestInfo)throws WSJNIException;
	public static native long getCloudBuckets(ArchiveCloudDestInfo cloudDestInfo,List<String> out_Buckets)throws WSJNIException;
	public static native long getCloudRegions(ArchiveCloudDestInfo cloudDestInfo,List<String> out_CloudRegions)throws WSJNIException;
	public static native String getRegionForBucket(ArchiveCloudDestInfo cloudDestInfo,JRWLong out_jRet)throws WSJNIException;
	public static native long testConnection(ArchiveCloudDestInfo cloudDestInfo)throws WSJNIException;

	// scheduled export
    public static native long AFEnabledScheduledExport(boolean enabled);
    public static native boolean AFCheckShExpBackupNum(int interval);
    public static native long AFAddSucceedBackupNum();
    public static native String AFIGetSessPwdFromKeyMgmtBySessNum(long sessionNum, String destPath);// get backup encryption pwd.
    
    public static native boolean AFCheckRecoveryVMJobExist(String vmName,String esxServerName);
	public static native boolean AFCheckFolderContainsBackup(String folder);
	public static native long getActiveJobs(List<JJobContext> jobs);
	public static native int getVMwareToolStatus() throws ServiceException;
	public static native long getVMApplicationStatus(String vmInstanceUUID,JApplicationStatus appStatus);
	private static native String AFGetMntFromVolumeGuid(String strGuid);
	public static String getMntFromVolumeGUID(String strGUID) {
		return AFGetMntFromVolumeGuid(strGUID);
	}
	public static native long AFGetMntSess(List<JMountSession> vMntSess);	
	public static native long validateEncryptionSettings(ArchiveJobScript in_archiveConfig,JRWLong errorcode,JRWLong CCIErrorCode)throws WSJNIException ;
	
	public static native boolean AFCheckDestChainAccess(JNetConnInfo curDest, JNetConnInfo baseDest, JNetConnInfo errDest, boolean bPrev);

	//check & update new credentials into backup chain.
	public static native long AFCheckUpdateNetConn(JNetConnInfo dest1, JNetConnInfo dest2, boolean bPrev);
	
	public static native long AFRetrieveConnections(ArrayList<JNetConnInfo> connections);

	public static native long AFMoveLogs();
	
	public static native String GetSymbolicLinkActualPath(String inFolderPath,JRWLong errorCode);
	
	public static native long AFDeleteLicError(long licCode);
    
	public static native int getCACloudStorageKey(ArchiveCloudDestInfo cloudDestInfo,String user, String password,ArrayList<String> newStoragekey)throws WSJNIException;
	public static native int getGeminarePortalURL(ArchiveCloudDestInfo cloudDestInfo,ArrayList<String> newStorageKey)throws WSJNIException;
	public static native long updateStorageKey(ArchiveCloudDestInfo cloudDestInfo,ArrayList<String> newStoragekey)throws WSJNIException;
    public static native long CreateEvent(String eventName, boolean manualReset, boolean initialState);
    
    public static native int SetEvent(long handle);
    
    public static native int ResetEvent(long handle);
    
    public static native long WaitForSingleObject(long handle, long milliSeconds);
    
    public static native long createMutex(boolean initiallyOwned, String mutexName);
    public static native boolean releaseMutex(long handle);

	public static native long getApplicationDetailsInESXVM(List<JWriterInfo> appList,
			String esxServerName, String esxUserName, String esxPassword,
			String vmName, String vmVMX, String userName, String password);

	public static native long getVMInformation(JPFCVMInfo vmInfo,
			String esxServerName, String esxUserName, String esxPassword,
			String vmName, String vmVMX, String userName, String password);
	public static native long AFISetPreAllocSpacePercent(long dwPercent);
	public static native long AFIGetPreAllocSpacePercent(JRWLong dwPercent);
	public static native boolean IsFirmwareuEFI();
	
	public static native String GetRestorePointPath(String dest, String domain, String user,String pwd, String subPath);
	
    //D2D mount volume API begin
    public static native long GetAllMountedRecoveryPointItems(List<JMountedRecoveryPointItem> mountedItem);
	
    public static native long GetMountedRecoveryPointItems(String dest,String domain, String user, String pwd, String subPath, List<JMountedRecoveryPointItem> mountedItem);
	
    @Deprecated
	public static native long MountRecoveryPointItem(String dest,String domain, String user, String pwd, String subPath, String volGUID,int encryptionType,String encryptPassword, String mountPath);
	
    public static native long MountRecoveryPointItemEx( JMountRecoveryPointParams jMntParams );
    
	public static native long DisMountRecoveryPointItem(String mountPath,int mountDiskSignature);
	
	public static native long GetAvailableMountDriveLetters(List<String> driveLetters);
	
	public static native String GetWindowsTempDir();
	
	public static native long AFGetMntInfoForVolume(String dest,String domain, String user, String pwd, String subPath, String volGUID, List<JMountedRecoveryPointItem> mountedItem);
	//end
	
	public static native boolean CheckIfExistDiskLargerThan2T() throws WSJNIException;
	
    // used for C++ callback
    public static native long StartD2dCallback();
    public static native long StopD2dCallback();
    
    //////////////////////////////////////////////////////////////////////////
    // Manual Conversion
    
    public static native long createVssManager(); // returns vssManagerHandle, 0 if error
    public static native int vssManager_Init( long vssManagerHandle, int flag, boolean isPersistent ); // returns 0 for success, and -1 for failure
    public static native String vssManager_CreateSnapshotSet( long vssManagerHandle, List<String> volumes ); // returns snapshotSetId
    public static native int vssManager_DeleteSnapshotSet( long vssManagerHandle, String snapshotSetId ); // returns 0 for success, and -1 for failure
    public static native long vssManager_GetSnapshotSetByGuid( long vssManagerHandle, String snapshotSetId ); // returns snapshotSetHandle, 0 if error
    public static native void vssManager_Release( long vssManagerHandle ); // returns 0 for success, and -1 for failure
    
    public static native String vcmSnapshotSet_QuerySnapshotDeviceName( long snapshotSetHandle, String originalVolumeName ); // returns snapshot volume name
    public static native void vcmSnapshotSet_Release( long snapshotSetHandle ); // returns 0 for success, and -1 for failure
    
    public static native int GetIntactSessions( String sessionFolderPath, List<Integer> sessionList );
    
    public static native int GetLocalPathOfShareName( String shareName, List<String> localPathList, List<Integer> errorCodeList );
    
    public static class RHAScenarioStatusValue
    {
    	public static final int Run		= 0;
    	public static final int Stop	= 1;
    	public static final int Sync	= 2;
    	public static final int Unknown	= 3;
    }
    
    public static native int GetRHAScenarioState( String rootPath, List<Integer> resultCodeList );
    
    public static native boolean IsRHASyncReplicated( String rootPath, List<Integer> resultCodeList );
    
       private static native int browseFileChildren(String volumeGUID, String parentID, ArrayList<JCatalogDetail> retArr);

	public static List<JCatalogDetail> browseFileChildren(String volumeGUID, String parentID) {
		ArrayList<JCatalogDetail> retArr = new ArrayList<JCatalogDetail>();
		int ret = browseFileChildren(volumeGUID, parentID, retArr);
		if (ret == 0) {
			for(int i = 0, count = retArr.size(); i < count; i++) {
				JCatalogDetail detail = retArr.get(i);
				detail.setFileDate(BackupConverterUtil.dosTime2UTC(detail.getFileDate()));
			}
			return retArr;
		}
		return null;
	}
	
	public static native long MountVolume(String userName, String passWord, String backupDest, long sessionID, 
				String volumeGUID, JMountPoint jObject, String encryptionKey);
	
	public static native int UnMountVolume(String diskSignature, long handle);
	
	public static native int updateSessionPasswordByGUID(String[] uuids, String[] passwords);

	public static native long GetFileChildrenEx(String mountVolGUID, String parentID,
			int start, int size, JRWLong realCnt,
			ArrayList<JCatalogDetail> retArr);

	//pidma02: Support for search in single session for catalog enabled sessions
	
	public static SearchContext SearchCatalogEx(String catalogSessionPath, String sDir, boolean bCaseSensitive,
			boolean bIncludeSubDir, String pattern) {
		JRWLong totalCnt = new JRWLong();
		logger.debug("SearchCatalogEx: sessionPath: " + catalogSessionPath + "folder: " + sDir
				+ " pattern " + pattern);
		long handle = SearchCatalogFileEx(catalogSessionPath, sDir, bCaseSensitive, bIncludeSubDir,
				pattern, totalCnt);
		SearchContext sc = new SearchContext();
		sc.setContextID(handle);
		sc.setTag(totalCnt.getValue());
		logger.debug("SearchCatalogEx return handle: " + handle + " count: " + totalCnt.getValue());
		return sc;
	}

	//pidma02: Support for search in single session for catalog enabled sessions
	public static JSearchResult FindNextCatalogEx(SearchContext sc) {
		logger.debug("FindNextCatalogEx, handle is " + sc.getContextID());
		ArrayList<JCatalogDetail> retArr = new ArrayList<JCatalogDetail>();
		JRWLong nFound = new JRWLong();
		JRWLong nCurrent = new JRWLong();
		nCurrent.setValue(-1);
		long handle = sc.getContextID();
		long nRequest = 40;
		if(handle == 0){
			logger.warn("Handle is 0, will find nothing here");
		}
		int jniRet = FindNextCatalogFileEx(handle, nRequest, retArr, nFound,
				nCurrent);

		/*if (jniRet != 0) {
			throw new RuntimeException("failed to SearchCatalogFile!");
		}*/
		HashMap<Long, String> hm = new HashMap<Long, String>();
		for (JCatalogDetail cd : retArr) {
			cd.setFileDate(BackupConverterUtil.dosTime2UTC(cd.getFileDate()));
			cd.setBackupTime(BackupConverterUtil.dosTime2UTC(cd.getBackupTime()));
			Long pathId = cd.getPathID();
			if (hm.containsKey(pathId)) {
				cd.setPath(hm.get(pathId));
			} else {
				String fullPath = getFindFullPath(handle, pathId);
				cd.setPath(fullPath);
				hm.put(pathId, fullPath);
			}
		}

		JSearchResult sr = new JSearchResult();
		sr.setCurrent(nCurrent.getValue());
		sr.setFound(nFound.getValue());
		sr.setDetail(retArr);
		logger.debug("Found: current " + sr.getCurrent() + " count " + sr.getFound());
		return sr;
	}
    
    /**
     * The API to read registry value under D2D, no matter it's DWORD or String, it's converted to String,
     * the caller can convert it back to DWORD if needed.
     * @param keyName
     * @param valueName
     * @return
     */
	
	public static native int GetRegIntValue(String keyName, String valueName, String regRoot, JRWLong value);
	
	public static native int SetRegIntValue(String keyName, String valueName, String regRoot, int regIntValue);
	
	
	public static native int GetRegMultiStringValue(String keyName, String valueName, String regRoot, List<String> regValues);
	
	public static native int SetRegMultiStringValue(String keyName, String valueName, String regRoot, List<String> regValues);
	
	public static native boolean isShowUpdate();
		
	public static native long startMerge(MergeJobScript mjs, String mJSPath);
	public static native int AFGetSystemInfo(JSystemInfo jSystemInfo);
	
	public static JSystemInfo getSystemInfo(){
		JSystemInfo systemInfo = new JSystemInfo();
		int ret = AFGetSystemInfo(systemInfo);
		if(ret == 0){
			return systemInfo;
		}
		return null;
	}
	
	public static native long DoRVCMInjectService(
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
		List<Volume> volumesOnNonBootOrSystemDisk,
		boolean isX86,
		String scriptPath);
	
	public static native long DoRVCMInjectServiceForHyperV(
		String vmWinDir,
		String winSystemDir,
		List<Volume> volumesOnNonBootOrSystemDisk, 
		boolean isX86,
		String scriptPath);
		
	public static native long stopMerge(long jobId)/* {
		return MergeJNIMock.stopMerge(jobId);
	}*/;
	
	public static native long getMergeJobMonitor(long handle, MergeJobMonitor mjm)/*{
//		logger.info("Get merge job monitor");
		return MergeJNIMock.getMergeJobMonitor(handle, mjm);
	}*/;
	
	public static native long createMergeJobMonitor(long jobId);	
	public static native long releaseMergeJobMonitor(long handle);
	
	public static native long setBackupSetFlag(String destination, long sessionNum, int flagValue, String vmInstanceUUID);
			
	/**
	 * Check whether need/can start merge job
	 * @param retentionCount
	 * @param destination
	 * @param vmUUID
	 * @param userName
	 * @param password
	 * @return 0: need to start merge job; 
	 * 		   2012: don't need to start merge job, no session need to merge
	 * 		   2013: cannot start merge job, maybe another job is already running.
	 */
	public static native long isMergeJobAvailable(int retentionCount, String destination, String vmUUID, String userName, 
			String password);
	
	public static native long AFIIsMergeJobAvailableEx(String pwzBKDest, int dwRetentionCnt, int  dwDailyCnt, int dwWeeklyCnt, int dwMonthlyCnt, String pwzVMGUID, String pwzDS4Replication, String pwzBKUsr, String pwzBKPwd);
	
	//wanqi06
	public static native long AFIRetrieveMergeJM(List<JMergeActiveJob> jobs);	
	
	public static native long launchCatalogEx(CatalogJobContext context);	
	
	/**
	 * option is 1: list volume or folder 
	 * option is 2: create sub folder
	 * @param path
	 * @param subPath folder to be created
	 * @param option  browse or create
	 * @param jBackupVM
	 * @param list
	 * @return
	 */
	public static native long BrowseVMFileFolderItem(String path,String subPath,int option,JBackupVM jBackupVM,ArrayList<JFileInfo> list);
	
	public static FileFolderItem browseVMFileFolderItem(String path,
			JBackupVM jBackupVM) throws ServiceException {
		ArrayList<JFileInfo> JFolderList = new ArrayList<JFileInfo>();
		long dwRet_folder = BrowseVMFileFolderItem(path,"",1, jBackupVM, JFolderList);
		logger.info("dwRet_folder:" + dwRet_folder);
		if (dwRet_folder!=0) {
			String msg = WSJNI.AFGetErrorMsg(dwRet_folder);
			if(dwRet_folder==5) // access denied
				throw new ServiceException(msg, FlashServiceErrorCode.Browser_GetFolderFailed_Acess_Denied);
			else if(dwRet_folder==259) //no folder exists
				throw new ServiceException(FlashServiceErrorCode.Browser_GetFolder_No_Content, new String[] {msg});
			else
				throw new ServiceException(msg, FlashServiceErrorCode.Browser_GetFolderFailed);
		}
		
		//sort by name ,ignoring case differences
		JFileComparator compatator=new JFileComparator();
		Collections.sort(JFolderList,compatator);
		
		List<Folder> folderList = new LinkedList<Folder>();

		for(JFileInfo finfo : JFolderList){
			Folder folder = new Folder();
			folder.setName(finfo.getStrName());
			folder.setLastUpdateDate(new Date(
					finfo.getLastWriteDateTime() * 1000));
			folder.setCreationDate(new Date(
					finfo.getCreationDateTime() * 1000));
			folder.setPath(finfo.getStrPath());
			folderList.add(folder);
		}
		FileFolderItem result = new FileFolderItem();
		result.setFolders(folderList.toArray(new Folder[0]));
		return result;
	}
	
	
	public static native long DeleteAllPendingFileCopyJobs(String strDest,String strDestDomain, String strDestUserName, String strDestPassword) throws WSJNIException;

	public static native long GetSessNumListForNextMerge(int retentionCount, String backDest, ArrayList<Integer> sessionNumberList, String userName, String password);
	
	public static native long AFIGetFullSess4Inc(JRWLong fullSessNumber, long increSessNumber, String backupDest);

	public static native long getMachineDetailFromBackupSession(String backupDest, JMachineDetail result);
	
	public static native long getTickCount();
	
	public static native void StartNICMonitor();
	
	public static native void StopNICMonitor();
	
	public static native void StartClusterMonitor();
	
	public static native void StopClusterMonitor();

	
	public static native boolean isX86();
	
	/**
	 * Get datastore hash key or session encryption key for a specified. 
	 * <br/> A connection to the datastore shared path should be created before call it.
	 * @param sharePath: datastore shared path
	 * @param sessionNumber: the specified session number if you want to get the encryption key for it.
	 * @param sessionPwd: session password for the session encryption key or datastore password for datastore hash key. 
	 * @return If the passed in session number is not 0 and the passed in sessionPwd if the session password, 
	 * it will return the encryption key for that session.<p/>
	 * If the sessinoNumber is 0 and the sessionPwd is datastore password, it will return the datastor hashkey
	 */
	public static native String getRPSDataStoreHashKey(String sharePath, int sessionNumber, String sessionPwd);
	
	public static native boolean isCatalogExist(String backupdest, int sessionNumber);
	
	public static native String getSessPathByNo(String destPath, int sessionNumber);
	
	/**
	 * Validate destination connection with connection manager
	 * @param destination
	 * @param userName
	 * @param password
	 * @return return 0 for success, else other values
	 */
	public static native int AFIVerifyDestUser(String destination, String userName, String password);
	
	public static native void getIPList(List<String> ipList);
	
	/*Discover Hyper-V VM*/
	public static native ArrayList<JHypervVMInfo> GetVmList(String serverName, String user, String password, boolean onlyUnderThisHyperv)  throws ServiceException;
	
	/*Discover Hyper-V list*/
	public static native int GetHypervInfoList(String serverName, String user, String password, ArrayList<JHypervInfo> hypervInfo) throws ServiceException;
	
	// for Hyper-V Cluster
	/**
	 * Get the protection type by connection information.
	 * @param serverName	the server host name or IP address.
	 * @param user	user name
	 * @param password	password
	 * @param type 	out parameter, bit wise of protection type. 
	 * 				0x0: unknown;
	 * 				0x1: supports stand alone backup;
	 * 				0x2: supports cluster backup.
	 * @param errorCode 	out parameter, error code. 
	 */
	public static native void getHyperVProtectionTypes(String serverName, String user, String password, JRWLong type, JRWLong errorCode, JHypervResult hypervResult);
	
	/**
	 * Get CVS cluster virtual host name and other identity information by the specified Hyper-V Physical Node.
	 * @param serverName	the Hyper-V physical server host name or IP address.
	 * @param user	user name
	 * @param password	password
	 * @return When virtual host name and other information are available: return null 
	 * 		   Otherwise, return String array: index 0 is host name (null if the host name is not available);
	 * 		   while other information follows (null if they are not available);
	 */
	public static native String[] getClusterVirtualByPyhsicalNode(String serverName, String user, String password);
	
	public static native int updateD2DJobHistory( JJobHistory jobHistory );
	
	public static native String getD2DServerSID();
	
	public static native int markD2DJobEnd(long jobID, long jobStatus, String jobDetails);
	
	public static native long getActivityLogPaths4Sync(int logType, String vmInstanceUUID, ArrayList<String> logPaths);
	
	public static native long getActivityLogPaths4FullSync(int logType, String vmInstanceUUID, ArrayList<String> logPaths);
	
	public static native long getRecoveryPoint4Sync( String strDest, String strDomain, String strUser, String strPassword, String strVmUUID, boolean bFullSync, ArrayList<String> filePaths );
	
	public static native long getJobHistoryPaths4IncrementalSync(ArrayList<String> logPaths);
	
	public static native long getJobHistoryPaths4FullSync(ArrayList<String> logPaths);
	
	public static native int getJobHistory(long start, long request,
			JJobHistoryFilterCol filter, JJobHistoryResult result);
	
	public static native int updateThrottling(long jobID, long throttling);

	public static native int addMissedD2DJobHistory( JJobHistory jobHistory );
	
	public static native boolean ifJobHistoryExist(int productType, long jobID,
			int jobType, String agentNodeID);
		
	public static native long AFGetHyperVBackupVMDisk(String destination,String subPath,String domain,String username,String password,List<JDisk> diskList);
	
	
	public static native long queryJobQueue(long jobQType, String jobQIdentity,
			JCatalogJobScriptInfo jJobScriptList, boolean jCreateJobQFolder,
			String jCatalogModeID);
	
	public static native long AFGetHyperVVMNetworkList(String destination,String subPath,String domain,String username,String password,List<JVMNetworkConfig> networkList);
	
	public static native long getFilePaths4Sync(long syncTo, long syncFrom, long syncType, String nodeID, ArrayList<String> pathsList);
	
	/**
	 * please call NativeFacade#OpenHypervHandle and NativeFacade#CloseHypervHandle
	 * please refer to FlashServiceImpl#getHypervInfo
	 */
	public static native JHypervPFCDataConsistencyStatus getHypervPFCDataConsistentStatus(String vmGuid, String vmUserName, String vmPassword, long handle);
	
	public static native long AEGetE15CASList(ArrayList<JExchangeDiscoveryItem> retArr, String strUser, String strPassword);

	public static native long AEGetDefaultE15CAS(ArrayList<String> retArr);
	
	public static native long AESetDefaultE15CAS(String cas);
	
	public static native long getHyperVVMPathType(String vmPath);
	
	/**
	 * Get a integer value from INI file.
	 * @param strApp
	 * @param strKey
	 * @param nDefault
	 * @param fullPathOfIniFile: Optional. If fullPathOfIniFile is null or empty, get value from ..\Configuraion\Switch.ini
	 * @return return the value. If switch not define, return nDefault
	 */
	public static native int getSwitchIntFromFile( String strApp, String strKey, int nDefault, String fullPathOfIniFile);
	
	/**
	 * 
	 * @param strValueName
	 * @param nDefaultValue
	 * @param strSubKey: Optional. If strSubKey is null or empty, get value from "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine"
	 * @return
	 */
	public static native int getSwitchIntFromReg( String strValueName, int nDefaultValue, String strSubKey);
	
	/**
	 * 
	 * @param strApp
	 * @param strKey
	 * @param strDefault
	 * @param fullPathOfIniFile: Optional. If fullPathOfIniFile is null or empty, get value from ..\Configuraion\Switch.ini
	 * @return
	 */
	public static native String getSwitchStringFromFile( String strApp, String strKey, String strDefault, String fullPathOfIniFile);
	
	/**
	 * 
	 * @param strValueName
	 * @param strDefaultValue
	 * @param strSubKey: Optional. If strSubKey is null or empty, get value from "HKEY_LOCAL_MACHINE\SOFTWARE\Arcserve\Unified Data Protection\Engine"
	 * @return
	 */
	public static native String getSwitchStringFromReg( String strValueName, String strDefaultValue, String strSubKey );
	
	/**
	 * 
	 * @param fileList
	 * @return
	 */
	public static native long getMailAlertFiles( ArrayList<String> fileList);
	/**
	 * 
	 * @param fileList
	 * @return
	 */
	public static native void getSourceNodeSysInfo( SourceNodeSysInfo sourceNodeSysInfo);
	
	/**
	 * 
	 * @param type
	 * @param fileList
	 * @return
	 */
	public static native long getMailAlertFilesEx( int type, ArrayList<String> fileList);
	
	public static native ArrayList<String> browseHyperVHostFolder(long handle, String parentFolder) throws ServiceException;
	
	public static native boolean createHyperVHostFolder(long handle, String path, String folder) throws ServiceException;
	
	public static native int getHyperVCPUSocketCount(long handle);
	
	public static native List<HyperVHostStorage> getHyperVHostStorage(long handle);
	
	public static native int CompareHyperVVersion(long handle, String sessUserName, String sessPassword, String sessRootPath, int sessNumber) throws ServiceException;
	
	/**
	 * 
	 * @param dateFormat
	 * @return
	 */
	public static native int getDateTimeFormat(DateFormat dateFormat);
	
	public static native String getHyperVDefaultFolderOfVHD(long handle);
	
	public static native String GetHyperVDefaultFolderOfVM(long handle);
	
	public static native boolean CheckVHDMerging(String strRootPath, int beginSessNumber, int endSessNumber);
	
	public static native boolean IsOSCompatibleWithProxy(String proxyServer, String domain, String user, String password);
	
	public static native long AFGetLicenseStatus(boolean standAlone);
	
	public static native long GetAllDnsSuffixes(List<String> dnsSuffixes);
	
	public static native int getHyperVServerType(long handle);
	
	public static native int getHyperVClusterNodes(long handle, List<String> nodes);
	
	public static native long startInstantVM(String paraXMLPath, int startFlag);
	
	public static native long stopInstantVM(String ivmJobUUID, long jobID, int jobType, boolean isDelete);
	
	public static native long startHydration(String ivmJobUUID);
	
	public static native long stopHydration(String ivmJobUUID);
	
	public static native long getInstantVMJobStatus(JIVMJobStatus jobStatus);

	public static native int getVAppChildBackupVMsAndRecoveryPoints(String vAppDestination, int vAppSessionNumer, String domain, String username, String password,
			List<JBackupVM> backupVMList, Map<String, JRestorePoint> recoveryPointMap);
	
	public static native long AFIIsMergeJobAvailableExt(JMergeData mergeData);
	
	public static native void CleanShareMemory(String ivmJobUUID);

	public static native int verifyHyperVAdminAccount(String computerName, String userName,  String password, boolean isCluster);
	
	public static native String getHyperVServerOsVersion(long handle);
	
	public static native JHypervVMInfo getHypervVMInfo(long handle, String vmUuid);
	
	public static native long isInstantVMProxyMeetRequirement(PreCheckInstantVMProxyModel precheckProxyStruct, ArrayList<String> warningList, StringBuilder errMsg);
	public static native void setClusterAccessHint(String serverName, String username, String password);
	public static native String EncryptDNSPassword(String dnsPassword);
	public static native String DecryptDNSPassword(String dnsPassword);
	// scheduled export for VM
    public static native long AFEnabledScheduledExportVM(boolean enabled, String vmInstanceUUID );
    public static native boolean AFCheckShExpBackupNumVM(int interval, String vmInstanceUUID);
    public static native long AFAddSucceedBackupNumVM(String vmInstanceUUID);
    
    public static native String AFGetHyperVPhysicalName(String serverName, String user, String password, String vmInstaceUUID);
	public static native long getGRTItem(String destination, String userName, String password, long sessionNumber, String encryptedPwd, long subSessionID, long appType, long dataType, long nodeID, List<JGRTItem> result);

	public static native String getOSVersion();
	public static native int cancelGroupJob(long jobType, long jobID, List<Long> finishedChildJobIDs);
    public static native long getDataSizesFromStorage(ArrayList<JDataSizesFromStorage> dataSizes, String path, String usrName, String usrPwd);
    
    public static native int getVolumeSize(String directoryName, List<Long> sizes);
    
    public static native String GetFileCopyCatalogPathy(String MachineName, long ProductType);
    
    public static native boolean isIVMAgentExist(String ivmJobUUID);
    
    public static native int checkServiceState(String hostName, String serviceName, String userName, String password, JWindowsServiceModel service);
    
    public static native long DisableFileCopy(String strDest, String strDestDomain, String strDestUserName, String strDestPassword);
    public static native long CanArchiveSourceDeleteJobBeSubmitted(JArchiveJob outJArchiveJob);
    public static native boolean isHyperVVmExist(String guid, long handle);
   
    
    /*zxh,
    * Method:    		VDDismountResBrsVols
    * description: 		this function is used to Dismount ResBrowse Volumn.the param bForceDisMntAllResBrw may be always false for java caller
    */
    public static native long VDDismountResBrsVols(boolean bForceDisMntAllResBrw);

    /*zxh,
    * Class:     		com_ca_arcflash_webservice_jni_WSJNI
    * Method:    		VDUpateVolumeMountTimestamp
    * description: 		notice c++ api to update the Timestamp in the c++ mount cache
    */
    public static native long VDUpateVolumeMountTimestamp(String sVolumeSignatureID);
    
    public static native boolean CheckASBUAgent();
    
    public static native String GetDisplayLanguage();
    
    public static native String getHostFQDN();
    
    public static native long AFGetArchiveJobInfoCount(JArchiveJob outJArchiveJob, JRWLong jobCount);
}
