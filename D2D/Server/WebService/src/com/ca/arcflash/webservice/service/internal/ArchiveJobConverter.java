
package com.ca.arcflash.webservice.service.internal;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.archive.ArchiveCloudDestInfo;
import com.ca.arcflash.webservice.data.archive.ArchiveConfiguration;
import com.ca.arcflash.webservice.data.archive.ArchiveConfigurationConstants;
import com.ca.arcflash.webservice.data.archive.ArchiveDiskDestInfo;
import com.ca.arcflash.webservice.data.archive.ArchiveJobScript;
import com.ca.arcflash.webservice.data.archive.ArchivePolicy;
import com.ca.arcflash.webservice.data.archive.ArchiveSizeFilter;
import com.ca.arcflash.webservice.data.archive.ArchiveSourceFilter;
import com.ca.arcflash.webservice.data.archive.ArchiveSourceFiltersConfiguration;
import com.ca.arcflash.webservice.data.archive.ArchiveSourceInfoConfiguration;
import com.ca.arcflash.webservice.data.archive.ArchiveTimeRangeFilter;
import com.ca.arcflash.webservice.data.archive.ArchiveVolItemAppComp;
import com.ca.arcflash.webservice.data.archive.FileCopyPolicy;
import com.ca.arcflash.webservice.data.archive.JobScriptArchiveNode;
import com.ca.arcflash.webservice.data.archive.JobScriptArchiveRestoreVolApp;
import com.ca.arcflash.webservice.data.archive.RestoreArchiveJob;
import com.ca.arcflash.webservice.data.archive.RestoreJobArchiveVolumeNode;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.catalog.CatalogType;
import com.ca.arcflash.webservice.data.restore.RestoreJobType;
import com.ca.arcflash.webservice.scheduler.BaseArchiveJob;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.DeleteArchiveService;
import com.ca.arcflash.webservice.service.ServiceException;

public class ArchiveJobConverter {
	private static final Logger logger = Logger.getLogger(BackupJobConverter.class);
	
	private static int ASTIO_TRAVERSEDIR = 0x00000001;
	private static int FILEENTRY = 0x00000002;

	private static int QJDTO_R_ONCONFLICT_REPLACE = 0x00000001;
	private static int QJDTO_R_REPLACE_ACTIVE = 0x00000002;
	private static int QJDTO_R_ONCONFLICT_RENAME = 0x00000004;
	private static int QJDTO_R_CREATEWHOLEPATH = 0x00400000; // Create whole
	private static int QJDFS_BLI_NT_RESTORE_FILE = 5059;
	
	public ArchiveJobScript convert(ArchiveConfiguration configuration,int in_archiveJobType, String hostname, long shrmemid, String backupSessionPath, String backupSessionId, String backupSessionPwd) {
		if (configuration == null)	return null;
		
		BackupConfiguration backupCfg = null;
		try {
			backupCfg = BackupService.getInstance().getBackupConfiguration();
		} catch (ServiceException e) {
			logger.debug("BackupService.getInstance().getBackupConfiguration() error");			
		}
		if(backupCfg == null) return null;
		
		ArchiveJobScript result = new ArchiveJobScript();
		result.setUlVersion(1);//xml version
		result.setUsJobType(in_archiveJobType);
		logger.debug("convert() - Job Type = " + in_archiveJobType);
		if(in_archiveJobType == BaseArchiveJob.Job_Type_Archive)
			result.setUsJobMethod(ArchiveConfigurationConstants.JOB_MOTHED_TYPE_INCR);
		else if(in_archiveJobType == BaseArchiveJob.Job_Type_ArchiveSourceDelete)
			result.setUsJobMethod(ArchiveConfigurationConstants.JOB_MOTHED_TYPE_FULL);
		result.setUlShrMemID(shrmemid);
		result.setDwCompressionLevel(configuration.getCompressionLevel());
		
		if(backupCfg != null)
		{
			result.setBackupDestinationPath(backupCfg.getDestination());
			result.setBackupUserName(backupCfg.getUserName());
			result.setbackupPassword(backupCfg.getPassword());
			result.setBackupDestType(ArchiveConfigurationConstants.SHARED_PATH); // 0 if non - rps
		}
		
		result.setUsRestPoint(configuration.getFileVersionRetentionCount());//Max file versions
		
		result.setNNodeItems(1);// not required for archive as of now.
		result.setPAFNodeList(new ArrayList<JobScriptArchiveNode>(1));//this is required to put policies.
		JobScriptArchiveNode node = new JobScriptArchiveNode();
		node.setPwszNodeName(hostname);
		node.setPwszSessPath(StringUtil.isEmptyOrNull(backupSessionPath) ? "" : backupSessionPath);
		/**after upgrade : filecopy job doesn't need sessionId*/
		//shuzh02 : Enable this is because currently backend can't work without this value
		node.setUlSessNum(StringUtil.isEmptyOrNull(backupSessionId) ? 0 : Integer.parseInt(backupSessionId));
		result.getPAFNodeList().add(node);
		logger.debug("backupSessionId = " + backupSessionId + ", backupSessionPath = " + backupSessionPath);
		
		ArchiveSourceInfoConfiguration[] archiveVolumes = configuration.getArchiveSources();
		if(archiveVolumes == null) 
		{
			node.setNVolumeApp(0);
		}
		else
		{
			node.setNVolumeApp(archiveVolumes.length);
		}
		
		result.setPwszAfterJob("");
		result.setPwszBeforeJob("");
		if(in_archiveJobType == JobType.JOBTYPE_FILECOPY_BACKUP)
			result.setPwszComments("run file copy job");
		else if(in_archiveJobType == JobType.JOBTYPE_FILECOPY_SOURCEDELETE)
			result.setPwszComments("run file archive job");
		result.setPwszPrePostUser("");
		result.setPwszPrePostPassword("");
		
		if(configuration.isbEncryption())
		{ 
			result.setlEncryption(1);
			result.setEncryptionPassword(configuration.getEncryptionPassword());
		}
		else
		{
			result.setlEncryption(0);
			result.setEncryptionPassword("");
		}
		
		ArchiveDiskDestInfo diskDestInfo = null;
		if(configuration.isbArchiveToDrive())
		{
			diskDestInfo = new ArchiveDiskDestInfo();
			diskDestInfo.setArchiveDiskDestPath(configuration.getStrArchiveToDrivePath());
			diskDestInfo.setArchiveDiskUserName(configuration.getStrArchiveDestinationUserName());
			diskDestInfo.setArchiveDiskPassword(configuration.getStrArchiveDestinationPassword());
			result.setDwArchiveDestType(4);
		}
		result.setDiskDestInfo(diskDestInfo);
		result.setDwArchiveLogDays(10);
		
		ArchiveCloudDestInfo cloudDestInfo = null;

		if(configuration.isbArchiveToCloud())
		{
			cloudDestInfo = new ArchiveCloudDestInfo();
			cloudDestInfo.setcloudVendorURL(configuration.getCloudConfig().getcloudVendorURL());
			cloudDestInfo.setAccountName(configuration.getCloudConfig().getAccountName());
			cloudDestInfo.setId(configuration.getCloudConfig().getId());
			String BucketName = configuration.getCloudConfig().getcloudBucketName();
			String EncodedBucketName = configuration.getCloudConfig().getEncodedCloudBucketName();
			if(BucketName!= null && BucketName.length() > 0)					
				cloudDestInfo.setcloudBucketName(BucketName);				
					
			if(EncodedBucketName!= null && EncodedBucketName.length() > 0)							
				cloudDestInfo.setEncodedCloudBucketName(EncodedBucketName);			
			else if(BucketName!= null && BucketName.length() > 0)
				cloudDestInfo.setEncodedCloudBucketName(BucketName);
			
			cloudDestInfo.setcloudBucketRegionName(configuration.getCloudConfig().getcloudBucketRegionName());
			cloudDestInfo.setcloudCertificatePassword("");
			cloudDestInfo.setcloudCertificatePath("");
			cloudDestInfo.setcloudVendorType(configuration.getCloudConfig().getcloudVendorType());
			cloudDestInfo.setCloudSubVendorType(configuration.getCloudConfig().getCloudSubVendorType());
			cloudDestInfo.setcloudVendorUserName(configuration.getCloudConfig().getcloudVendorUserName());
			cloudDestInfo.setcloudVendorPassword(configuration.getCloudConfig().getcloudVendorPassword());
			
			cloudDestInfo.setcloudProxyPassword(configuration.getCloudConfig().getcloudProxyPassword());
			cloudDestInfo.setcloudProxyPort(configuration.getCloudConfig().getcloudProxyPort());
			cloudDestInfo.setcloudProxyRequireAuth(configuration.getCloudConfig().iscloudProxyRequireAuth());
			cloudDestInfo.setcloudProxyServerName(configuration.getCloudConfig().getcloudProxyServerName());
			cloudDestInfo.setcloudProxyUserName(configuration.getCloudConfig().getcloudProxyUserName());
			cloudDestInfo.setcloudUseProxy(configuration.getCloudConfig().iscloudUseProxy());
			cloudDestInfo.setRRSFlag(configuration.getCloudConfig().getRRSFlag());
			
			//cloudDestInfo.setcloudVendorHostName("");
			//cloudDestInfo.setcloudVendorPort();
			
			result.setDwArchiveDestType(configuration.getCloudConfig().getcloudVendorType());
		}
		result.setCloudDestInfo(cloudDestInfo);
		result.setPurgeFileBeforeLowDate(getTime(configuration.getRetentiontime()));		
		
		result.setFtFileMergeDate(getTime(configuration.getFilesRetentionTime()));
		result.setPwszCatalogDirPath(StringUtil.isEmptyOrNull(configuration.getStrCatalogPath()) ? 
										CommonUtil.D2DInstallPath : configuration.getStrCatalogPath());
		result.setPwszCatalogDirUserName(StringUtil.isEmptyOrNull(configuration.getStrCatalogDirUserName()) ? 
				"" : configuration.getStrCatalogDirUserName());
		result.setPwszCatalogDirPassword(StringUtil.isEmptyOrNull(configuration.getStrCatalogDirPassword()) ?
				"" : configuration.getStrCatalogDirPassword());
		ArchiveSourceInfoConfiguration[] archiveSources = configuration.getArchiveSources();
		setArchiveSource(result, archiveSources);
		
		result.setPwszBackupEncrptionPassword(backupSessionPwd);
		
		return result;
	}
	
	public ArchiveJobScript convert4PurgeJob(ArchiveConfiguration configuration,int in_archiveJobType, String hostname, long shrmemid, String backupSessionPath, String backupSessionId, String backupSessionPwd, boolean purgeJob4FC){
		ArchiveJobScript result = convert(configuration, in_archiveJobType, hostname, shrmemid, backupSessionPath, backupSessionId, backupSessionPwd);
		if(result != null && purgeJob4FC){
			try {
				ArchiveConfiguration config = DeleteArchiveService.getInstance().getArchiveDelConfiguration();
				ArchiveSourceInfoConfiguration[] archiveSources = config.getArchiveSources();
				setArchiveSource(result, archiveSources);
			} catch (ServiceException e) {
				logger.error("get File Archive configuration fail. " + e);
			}
		}
		return result;
	}
	
	public ArchiveJobScript convertToArchive(ArchiveConfiguration configuration,int in_archiveJobType, String hostname, long shrmemid, String backupSessionPath, String backupSessionId) {
		if (configuration == null)	return null;
		
		//BackupConfiguration backupCfg = null;
		/*try {
			backupCfg = BackupService.getInstance().getBackupConfiguration();
		} catch (ServiceException e) {
			logger.debug("BackupService.getInstance().getBackupConfiguration() error");
			e.printStackTrace();
		}*/
		//if(backupCfg == null) return null;
		
		ArchiveJobScript result = new ArchiveJobScript();
		result.setUlVersion(1);//xml version
		result.setUsJobType(in_archiveJobType);
		logger.debug("convert() - Job Type = " + in_archiveJobType);
		result.setUsJobMethod(0);//incremental
		result.setUlShrMemID(shrmemid);
		result.setDwCompressionLevel(configuration.getCompressionLevel());
		
			
		/*if(backupCfg != null)
		{
			result.setBackupDestinationPath(backupCfg.getDestination());
			result.setBackupUserName(backupCfg.getUserName());
			result.setbackupPassword(backupCfg.getPassword());
		}
		*/
		
		result.setBackupDestinationPath("");
		result.setBackupUserName("");
		result.setbackupPassword("");
		result.setBackupDestType(ArchiveConfigurationConstants.SHARED_PATH); // 0 if non - rps
		
		result.setUsRestPoint(configuration.getFileVersionRetentionCount());//Max file versions
		
		result.setNNodeItems(1);// not required for archive as of now.
		result.setPAFNodeList(new ArrayList<JobScriptArchiveNode>(1));//this is required to put policies.
		JobScriptArchiveNode node = new JobScriptArchiveNode();
		node.setPwszNodeName(hostname);
		node.setPwszSessPath(backupSessionPath == "" ? "" : backupSessionPath);
		node.setUlSessNum(backupSessionId == "" ? 0 : Integer.parseInt(backupSessionId));
		result.getPAFNodeList().add(node);
		
		ArchiveSourceInfoConfiguration[] archiveVolumes = configuration.getArchiveSources();
		if(archiveVolumes == null) 
		{
			node.setNVolumeApp(0);
		}
		else
		{
			node.setNVolumeApp(archiveVolumes.length);
		}
		
		result.setPwszAfterJob("");
		result.setPwszBeforeJob("");
		result.setPwszComments("run archive job");
		result.setPwszPrePostUser("");
		result.setPwszPrePostPassword("");
		
		if(configuration.isbEncryption())
		{ 
			result.setlEncryption(1);
			result.setEncryptionPassword(configuration.getEncryptionPassword());
		}
		else
		{
			result.setlEncryption(0);
			result.setEncryptionPassword("");
		}
		
		ArchiveDiskDestInfo diskDestInfo = null;
		if(configuration.isbArchiveToDrive())
		{
			diskDestInfo = new ArchiveDiskDestInfo();
			diskDestInfo.setArchiveDiskDestPath(configuration.getStrArchiveToDrivePath());
			diskDestInfo.setArchiveDiskUserName(configuration.getStrArchiveDestinationUserName());
			diskDestInfo.setArchiveDiskPassword(configuration.getStrArchiveDestinationPassword());
			result.setDwArchiveDestType(4);
		}
		result.setDiskDestInfo(diskDestInfo);
		result.setDwArchiveLogDays(10);
		
		ArchiveCloudDestInfo cloudDestInfo = null;

		if(configuration.isbArchiveToCloud())
		{
			cloudDestInfo = new ArchiveCloudDestInfo();
			cloudDestInfo.setcloudVendorURL(configuration.getCloudConfig().getcloudVendorURL());
			cloudDestInfo.setAccountName(configuration.getCloudConfig().getAccountName());
			cloudDestInfo.setId(configuration.getCloudConfig().getId());
			String BucketName = configuration.getCloudConfig().getcloudBucketName();
			String EncodedBucketName = configuration.getCloudConfig().getEncodedCloudBucketName();
			if(BucketName!= null && BucketName.length() > 0)					
				cloudDestInfo.setcloudBucketName(BucketName);				
					
			if(EncodedBucketName!= null && EncodedBucketName.length() > 0)							
				cloudDestInfo.setEncodedCloudBucketName(EncodedBucketName);			
			else if(BucketName!= null && BucketName.length() > 0)
				cloudDestInfo.setEncodedCloudBucketName(BucketName);
			
			cloudDestInfo.setcloudBucketRegionName(configuration.getCloudConfig().getcloudBucketRegionName());
			cloudDestInfo.setcloudCertificatePassword("");
			cloudDestInfo.setcloudCertificatePath("");
			cloudDestInfo.setcloudVendorType(configuration.getCloudConfig().getcloudVendorType());
			cloudDestInfo.setcloudVendorUserName(configuration.getCloudConfig().getcloudVendorUserName());
			cloudDestInfo.setcloudVendorPassword(configuration.getCloudConfig().getcloudVendorPassword());
			
			cloudDestInfo.setcloudProxyPassword(configuration.getCloudConfig().getcloudProxyPassword());
			cloudDestInfo.setcloudProxyPort(configuration.getCloudConfig().getcloudProxyPort());
			cloudDestInfo.setcloudProxyRequireAuth(configuration.getCloudConfig().iscloudProxyRequireAuth());
			cloudDestInfo.setcloudProxyServerName(configuration.getCloudConfig().getcloudProxyServerName());
			cloudDestInfo.setcloudProxyUserName(configuration.getCloudConfig().getcloudProxyUserName());
			cloudDestInfo.setcloudUseProxy(configuration.getCloudConfig().iscloudUseProxy());
			cloudDestInfo.setRRSFlag(configuration.getCloudConfig().getRRSFlag());
			
			//cloudDestInfo.setcloudVendorHostName("");
			//cloudDestInfo.setcloudVendorPort();
			
			result.setDwArchiveDestType(configuration.getCloudConfig().getcloudVendorType());
		}
		result.setCloudDestInfo(cloudDestInfo);		
		result.setPurgeFileBeforeLowDate(getTime(configuration.getRetentiontime()));
		return result;
	}

	public ArchiveJobScript convertArchiveRestoreJobToScript(long shrmemid,
			RestoreArchiveJob job, String localMachineName) {
		ArchiveJobScript archiveRestoreJobScript = new ArchiveJobScript();
		archiveRestoreJobScript.setProductType(job.getProductType());
		archiveRestoreJobScript.setVmInstanceUUID(job.getVmInstanceUUID());
		archiveRestoreJobScript.setUlVersion(1);//xml version
		archiveRestoreJobScript.setUsJobType(BaseArchiveJob.Job_Type_ArchiveRestore);
		logger.debug("convert() - Job Type = " + BaseArchiveJob.Job_Type_ArchiveRestore);
		archiveRestoreJobScript.setUsJobMethod(0);//incremental
		archiveRestoreJobScript.setUlShrMemID(shrmemid);
		
		if (job.getJobType() == 0) {
			job.setJobType(RestoreJobType.FileSystem);
		}
		
		return packageFileSystemJob(job,archiveRestoreJobScript, localMachineName);
	}

	private ArchiveJobScript packageFileSystemJob(RestoreArchiveJob job,
			ArchiveJobScript archiveRestoreJobScript, String localMachineName) {
		
		setUserPwd(job, archiveRestoreJobScript);
		
		archiveRestoreJobScript.setDwArchiveDestType(job.getArchiveDestType());
		archiveRestoreJobScript.setDiskDestInfo(job.getArchiveDiskInfo());
		ArchiveCloudDestInfo cloudInfo = job.getArchiveCloudInfo();
		if(cloudInfo!=null)
		{		
			String BucketName = cloudInfo.getcloudBucketName();
			String EncodedBucketName = cloudInfo.getEncodedCloudBucketName();
			if(BucketName!= null && BucketName.length() > 0)					
				cloudInfo.setcloudBucketName(BucketName);				
					
			if(EncodedBucketName!= null && EncodedBucketName.length() > 0)							
				cloudInfo.setEncodedCloudBucketName(EncodedBucketName);			
			else if(BucketName!= null && BucketName.length() > 0)
				cloudInfo.setEncodedCloudBucketName(BucketName);
		}
		archiveRestoreJobScript.setCloudDestInfo(cloudInfo);
		
		//set restore options
		if (job.getFileSystemOption().isCreateBaseFolder() == true)
			archiveRestoreJobScript.setFOptions(archiveRestoreJobScript.getFOptions()
					| QJDTO_R_CREATEWHOLEPATH);

		if (job.getFileSystemOption().isOverwriteExistingFiles()) {
			archiveRestoreJobScript.setFOptions(archiveRestoreJobScript.getFOptions()
					| QJDTO_R_ONCONFLICT_REPLACE);
			if (job.getFileSystemOption().isReplaceActiveFiles()) {
				archiveRestoreJobScript.setFOptions(archiveRestoreJobScript.getFOptions()
						| QJDTO_R_REPLACE_ACTIVE);
			}
		}else if (job.getFileSystemOption().isRenameFile()){
			archiveRestoreJobScript.setFOptions(archiveRestoreJobScript.getFOptions() | QJDTO_R_ONCONFLICT_RENAME);
		}
		/////
		
		List<JobScriptArchiveNode> listOfArchiveNodes = new ArrayList<JobScriptArchiveNode>();
		JobScriptArchiveNode archiveNode = new JobScriptArchiveNode();
		archiveNode.setPwszNodeName(localMachineName);

		try {
			InetAddress add = InetAddress.getByName(localMachineName);
			String ipaddress = add.getHostAddress().toString();
			archiveNode.setPwszNodeAddr(ipaddress);
		} catch (UnknownHostException e) {
			logger.error(e.getMessage() == null? e : e.getMessage());
		}
		
		archiveNode.setpArchiveVolumeList(null);
		List<JobScriptArchiveRestoreVolApp> archiveVolumes =ConvertToJobScriptRestoreArchiveVolumes(job.getArchiveNodes()[0].getPRestoreVolumeAppList(),localMachineName,archiveRestoreJobScript.getFOptions()
				| QJDTO_R_ONCONFLICT_REPLACE);
		archiveNode.setPRestoreVolumeAppList(archiveVolumes);
		
		archiveNode.setNVolumeApp(archiveVolumes.size());
		listOfArchiveNodes.add(archiveNode);
		
		archiveRestoreJobScript.setNNodeItems(listOfArchiveNodes.size());
		archiveRestoreJobScript.setPAFNodeList(listOfArchiveNodes);
		
		archiveRestoreJobScript.setPwszCatalogDirPath(StringUtil.isEmptyOrNull(job.getCatalogFolderPath()) ? 
				CommonUtil.D2DInstallPath : job.getCatalogFolderPath());
		archiveRestoreJobScript.setPwszCatalogDirUserName(StringUtil.isEmptyOrNull(job.getCatalogFolderUser()) ? 
				"" : job.getCatalogFolderUser());
		archiveRestoreJobScript.setPwszCatalogDirPassword(StringUtil.isEmptyOrNull(job.getCatalogFolderPassword()) ?
				"" : job.getCatalogFolderPassword());
		
		//if(job.getCatalogFolderPath()!=null){
			/*archiveRestoreJobScript.getPAFNodeList().get(0).setPwszSessPath(job.getStorePath());
			archiveRestoreJobScript.getPAFNodeList().get(0).setPwszUserName(job.getStoreUser());
			archiveRestoreJobScript.getPAFNodeList().get(0).setPwszUserPW(job.getStorePassword());*/
			/*archiveNode.setPwszSessPath(job.getCatalogFolderPath());
			archiveNode.setPwszUserName(job.getCatalogFolderUser());
			archiveNode.setPwszUserPW(job.getCatalogFolderPassword());*/
		//}
		
		archiveRestoreJobScript.setPwszAfterJob("");
		archiveRestoreJobScript.setPwszBeforeJob("");
		archiveRestoreJobScript.setPwszComments("run Archive Restore job");
		archiveRestoreJobScript.setPwszPrePostUser("");
		archiveRestoreJobScript.setPwszPrePostPassword("");
		
		/*ArchiveConfiguration config = ArchiveService.getInstance().getInternalArchiveConfiguration();
		
		if(config.isbEncryption())
		{
			archiveRestoreJobScript.setlEncryption(1);
			archiveRestoreJobScript.setEncryptionPassword(config.getEncryptionPassword());
		}
		else
		{
			archiveRestoreJobScript.setlEncryption(0);
			archiveRestoreJobScript.setEncryptionPassword("");
		}*/
		
		if((job.getEncryptedPassword() == null) || (job.getEncryptedPassword().length() == 0))
		{
			archiveRestoreJobScript.setlEncryption(0);
			archiveRestoreJobScript.setEncryptionPassword("");
		}
		else
		{
			archiveRestoreJobScript.setlEncryption(1);
			archiveRestoreJobScript.setEncryptionPassword(job.getEncryptedPassword());
		}
		
		return archiveRestoreJobScript;
	}


	private List<JobScriptArchiveRestoreVolApp> ConvertToJobScriptRestoreArchiveVolumes(
			RestoreJobArchiveVolumeNode[] pRestoreVolumeAppList,String localMachineName,int in_iOnConflict) {
		List<JobScriptArchiveRestoreVolApp> archiveVolumes = new ArrayList<JobScriptArchiveRestoreVolApp>();
		
		for(int iIndex = 0;iIndex < pRestoreVolumeAppList.length;iIndex++)
		{
			RestoreJobArchiveVolumeNode volumeNodeFromUI = pRestoreVolumeAppList[iIndex];
			JobScriptArchiveRestoreVolApp volumeNode = new JobScriptArchiveRestoreVolApp();
			volumeNode.setfileSystem(QJDFS_BLI_NT_RESTORE_FILE);
			volumeNode.setPath("\\\\" + localMachineName + "\\" + volumeNodeFromUI.getPath()+ ":");
			volumeNode.setonConflictMethod(in_iOnConflict);
			
			List<ArchiveVolItemAppComp> listOfItems = new ArrayList<ArchiveVolItemAppComp>();
			ArchiveVolItemAppComp[] items = volumeNodeFromUI.getVolItemAppCompList();
			
			if(items == null) 
			{
				volumeNode.setvolItemAppComp(0);
				volumeNode.setvolItemAppCompList(null);
				archiveVolumes.add(volumeNode);
				continue;
			}
			
			for(int iItemIndex = 0;iItemIndex < items.length;iItemIndex++)
			{
				ArchiveVolItemAppComp item = items[iItemIndex];
				ArchiveVolItemAppComp selectedItem = new ArchiveVolItemAppComp();
				selectedItem.setfileorDirFullPath(item.getfileorDirFullPath());
				selectedItem.setfileVersion(item.getfileVersion());
				
				if(item.getfOptions() == CatalogType.File)
				{
					selectedItem.setfOptions(FILEENTRY);
				}
				else if(item.getfOptions() == CatalogType.Folder)
				{
					selectedItem.setfOptions(ASTIO_TRAVERSEDIR);
				}
				listOfItems.add(selectedItem);
			}
			
			volumeNode.setvolItemAppComp(listOfItems.size());
			volumeNode.setvolItemAppCompList(listOfItems);
			archiveVolumes.add(volumeNode);
		}
		
		return archiveVolumes;
	}

	private void setUserPwd(RestoreArchiveJob job, ArchiveJobScript result) {
		if (job == null || result == null)
			return;

		if (job.getarchiveRestoreUserName() != null && job.getarchiveRestoreUserName().trim().length() > 0) {
			result.setPwszPrePostUser(job.getarchiveRestoreUserName());
			if (job.getarchiveRestorePassword() != null) {
				result.setPwszPrePostPassword(job.getarchiveRestorePassword());
			}
		}
		
		if (!StringUtil.isEmptyOrNull(job.getarchiveRestoreDestinationPath())) {
			packageJobDest(job, result);
		}
	}
	
	private void packageJobDest(RestoreArchiveJob job, ArchiveJobScript result) {
		if (job == null || result == null)
			return;

		result.setBackupDestinationPath(job.getarchiveRestoreDestinationPath());

		if (job.getarchiveRestoreUserName() != null && job.getarchiveRestoreUserName().trim().length() > 0) {
			result.setBackupUserName(job.getarchiveRestoreUserName());

			if (job.getarchiveRestorePassword() != null) {
				result.setbackupPassword(job.getarchiveRestorePassword());
			}
		}
		//since it is for restore, not setting the below irrelavent info
		//result.setBackupDestType(0); // 0 if non - rps
	}
	
	
	private ArchivePolicy getArchivePolicy(ArchiveSourceInfoConfiguration archiveSource){
		ArchivePolicy archivePolicy = new ArchivePolicy();
		if(archiveSource == null)
			return archivePolicy;
		
		ArchiveSourceFiltersConfiguration[] filters =  archiveSource.getArchiveSourceFiltersConfig();
		if(filters != null && filters.length > 0){
			List<ArchiveSourceFilter> fileFilters = new ArrayList<ArchiveSourceFilter>();
			List<ArchiveSourceFilter> folderFilters = new ArrayList<ArchiveSourceFilter>();
			List<ArchiveSizeFilter> sizeFilters = new ArrayList<ArchiveSizeFilter>();
			List<ArchiveTimeRangeFilter> timeFilters = new ArrayList<ArchiveTimeRangeFilter>();
			for(ArchiveSourceFiltersConfiguration filter : filters){
				//File Filter
				if(ArchiveConfigurationConstants.FILTER_TYPE_FILE.equalsIgnoreCase(filter.getFilterOrCriteriaName())){
					ArchiveSourceFilter fileFilter = new ArchiveSourceFilter();
					fileFilter.setnInclExcl(Integer.parseInt(filter.getFilterOrCriteriaType()));
					if(filter.isIsDefaultFilter()){
						fileFilter.setPszFilterName(getPattern(filter.getFilterOrCriteriaLowerValue()));
					}else{
						fileFilter.setPszFilterName(filter.getFilterOrCriteriaLowerValue());
					}
					fileFilter.setPszFilterDisplayName(getDisplayName(filter.getLocFilterOrCriteriaLowerValue()));
					fileFilters.add(fileFilter);
				//Folder Filter
				}else if(ArchiveConfigurationConstants.FILTER_TYPE_FOLDER.equalsIgnoreCase(filter.getFilterOrCriteriaName())){
					ArchiveSourceFilter folderFilter = new ArchiveSourceFilter();
					folderFilter.setnInclExcl(Integer.parseInt(filter.getFilterOrCriteriaType()));
					if(filter.isIsDefaultFilter()){
						folderFilter.setPszFilterName(getPattern(filter.getFilterOrCriteriaLowerValue()));
					}else{
						folderFilter.setPszFilterName(filter.getFilterOrCriteriaLowerValue());
					}
					folderFilter.setPszFilterDisplayName(getDisplayName(filter.getLocFilterOrCriteriaLowerValue()));
					folderFilters.add(folderFilter);
				//Size Filter
				}else if("FileSize".equalsIgnoreCase(filter.getFilterOrCriteriaName())){
					ArchiveSizeFilter sizeFilter = new ArchiveSizeFilter();
					sizeFilter.setnInclExcl(Integer.parseInt(filter.getFilterOrCriteriaType()));
					int compareType = Integer.parseInt(filter.getCriteriaOperator());
					sizeFilter.setnCompareType(compareType);
					sizeFilter.setFileSize1(getFileSize(filter.getFilterOrCriteriaLowerValue()));
					if(compareType == ArchiveConfigurationConstants.SIZE_TYPE_BETWEEN){
						sizeFilter.setFileSize2(getFileSize(filter.getFilterOrCriteriaHigherValue()));
					}
					sizeFilters.add(sizeFilter);
				//Time Range Filter
				}else if(filter.getFilterOrCriteriaName() != null && filter.getFilterOrCriteriaName().endsWith("Time")){
					ArchiveTimeRangeFilter timeRangeFilter = new ArchiveTimeRangeFilter();
					timeRangeFilter.setnInclExcl(Integer.parseInt(filter.getFilterOrCriteriaType()));
					String dateType = filter.getFilterOrCriteriaName();
					if(dateType.toLowerCase().startsWith("c")){
						timeRangeFilter.setnDateType(ArchiveConfigurationConstants.TIME_RANGE_TYPE_CREATION);
					}else if(dateType.toLowerCase().startsWith("m")){
						timeRangeFilter.setnDateType(ArchiveConfigurationConstants.TIME_RANGE_TYPE_MODIFY);
					}else if(dateType.toLowerCase().startsWith("a")){
						timeRangeFilter.setnDateType(ArchiveConfigurationConstants.TIME_RANGE_TYPE_ACCESS);
					}
//					timeRangeFilter.setnCompareType(0);
					timeRangeFilter.setLlTimeElapsed(getTime(filter.getFilterOrCriteriaLowerValue()));
					timeFilters.add(timeRangeFilter);
				}
			}
			archivePolicy.setpFileFilters(fileFilters);
			archivePolicy.setpFolderFilters(folderFilters);
			archivePolicy.setpFileSizeFilters(sizeFilters);
			archivePolicy.setpFileTimeFilters(timeFilters);
		}
		archivePolicy.setSzSourceFolder(archiveSource.getStrSourcePath());
		
		return archivePolicy;
	}
	
	private FileCopyPolicy getFileCopyPolicy(ArchiveSourceInfoConfiguration archiveSource){
		FileCopyPolicy fileCopyPolicy = new FileCopyPolicy();
		if(archiveSource == null)
			return fileCopyPolicy;
		
		ArchiveSourceFiltersConfiguration[] filters =  archiveSource.getArchiveSourceFiltersConfig();
		if(filters != null && filters.length > 0){
			List<ArchiveSourceFilter> fileFilters = new ArrayList<ArchiveSourceFilter>();
			List<ArchiveSourceFilter> folderFilters = new ArrayList<ArchiveSourceFilter>();
			List<ArchiveSizeFilter> sizeFilters = new ArrayList<ArchiveSizeFilter>();
			for(ArchiveSourceFiltersConfiguration filter : filters){
				//File Filter
				if(ArchiveConfigurationConstants.FILTER_TYPE_FILE.equalsIgnoreCase(filter.getFilterOrCriteriaName())){
					ArchiveSourceFilter fileFilter = new ArchiveSourceFilter();
					fileFilter.setnInclExcl(Integer.parseInt(filter.getFilterOrCriteriaType()));
					if(filter.isIsDefaultFilter()){
						fileFilter.setPszFilterName(getPattern(filter.getLocFilterOrCriteriaLowerValue()));
					}else{
						fileFilter.setPszFilterName(filter.getFilterOrCriteriaLowerValue());
					}
					fileFilter.setPszFilterDisplayName(getDisplayName(filter.getLocFilterOrCriteriaLowerValue()));
					fileFilters.add(fileFilter);
				//Folder Filter
				}else if(ArchiveConfigurationConstants.FILTER_TYPE_FOLDER.equalsIgnoreCase(filter.getFilterOrCriteriaName())){
					ArchiveSourceFilter folderFilter = new ArchiveSourceFilter();
					folderFilter.setnInclExcl(Integer.parseInt(filter.getFilterOrCriteriaType()));
					if(filter.isIsDefaultFilter()){
						folderFilter.setPszFilterName(getPattern(filter.getLocFilterOrCriteriaLowerValue()));
					}else{
						folderFilter.setPszFilterName(filter.getFilterOrCriteriaLowerValue());
					}
					folderFilter.setPszFilterDisplayName(getDisplayName(filter.getLocFilterOrCriteriaLowerValue()));
					folderFilters.add(folderFilter);
				//Size Filter
				}else if("FileSize".equalsIgnoreCase(filter.getFilterOrCriteriaName())){
					ArchiveSizeFilter sizeFilter = new ArchiveSizeFilter();
					sizeFilter.setnInclExcl(Integer.parseInt(filter.getFilterOrCriteriaType()));
					int compareType = Integer.parseInt(filter.getCriteriaOperator());
					sizeFilter.setnCompareType(compareType);
					sizeFilter.setFileSize1(getFileSize(filter.getFilterOrCriteriaLowerValue()));
					if(compareType == ArchiveConfigurationConstants.SIZE_TYPE_BETWEEN){
						sizeFilter.setFileSize2(getFileSize(filter.getFilterOrCriteriaHigherValue()));
					}
					sizeFilters.add(sizeFilter);
				}
			}
			fileCopyPolicy.setpFileFilters(fileFilters);
			fileCopyPolicy.setpFolderFilters(folderFilters);
			fileCopyPolicy.setpFileSizeFilters(sizeFilters);
		}
		fileCopyPolicy.setSzSourceFolder(archiveSource.getStrSourcePath());
		
		return fileCopyPolicy;
	}
	
	private String getPattern(String localizedPattern){
		int patternLength = localizedPattern.length();
		int openBrace = localizedPattern.indexOf("(");
		if(openBrace != -1){
			localizedPattern = localizedPattern.substring(openBrace + 1, patternLength - 1);
		}
		if(!localizedPattern.endsWith(";")){
			localizedPattern += ";";
		}
		return localizedPattern;
	}
	
	private String getDisplayName(String localizedPattern){
		int openBrace = localizedPattern.indexOf("(");
		if(openBrace != -1){
			localizedPattern = localizedPattern.substring(0, openBrace);
		}
		return localizedPattern;
	}
	
	private long getFileSize(String size){
		if(StringUtil.isEmptyOrNull(size)) return 0;
		String[] arrSize = size.split("\\\\");
		if(arrSize.length != 3) return 0;
		return Long.parseLong(arrSize[0]) + 1024*Long.parseLong(arrSize[1]) + 1024*1024*Long.parseLong(arrSize[2]);
	}
	
	private long getTime(String time){
		if(StringUtil.isEmptyOrNull(time)) return 0;
		String[] arrTime = time.split("\\\\");
		if(arrTime.length != 3) return 0;
		return 30*Long.parseLong(arrTime[0]) + Long.parseLong(arrTime[1]) + 365*Long.parseLong(arrTime[2]);
	}
	
	private void setArchiveSource(ArchiveJobScript result, ArchiveSourceInfoConfiguration[] archiveSources){
		List<ArchivePolicy> archivePolicyList = new ArrayList<ArchivePolicy>();
		List<FileCopyPolicy> fileCopyPolicyList = new ArrayList<FileCopyPolicy>();
		if(archiveSources != null && archiveSources.length > 0){
			for(int i=0;i<archiveSources.length;i++){
				ArchiveSourceInfoConfiguration archiveSource = archiveSources[i];
				if(archiveSource.isbArchiveFiles()){
					archivePolicyList.add(getArchivePolicy(archiveSource));
				}else if(archiveSource.isbCopyFiles()){
					fileCopyPolicyList.add(getFileCopyPolicy(archiveSource));
				}
			}
			result.setpArchivePolicy(archivePolicyList);
			result.setpFileCopyPolicy(fileCopyPolicyList);
		}
	}
	
}
