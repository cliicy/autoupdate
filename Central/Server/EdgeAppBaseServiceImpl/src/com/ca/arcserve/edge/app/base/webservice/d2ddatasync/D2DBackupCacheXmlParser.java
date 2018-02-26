package com.ca.arcserve.edge.app.base.webservice.d2ddatasync;


import java.io.StringReader;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.util.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.d2ddatasync.rps.RpsBaseSynchronizer;
import com.ca.arcserve.edge.app.base.webservice.d2ddatasync.rps.RpsJobInfoSynchronizer;
import com.ca.arcserve.edge.app.base.webservice.d2ddatasync.rps.RpsSessionInfoSynchronizer;

public class D2DBackupCacheXmlParser extends D2DBaseXmlParser {
	private static final Logger logger = Logger.getLogger(D2DBackupCacheXmlParser.class);
	
	private boolean cleanFlag = false;
	private String m_xmlContent = null;
	private BackupCache m_cache = null;

	private D2DBackupDataSynchronizer syncer = null;

	private long _BackupCacheID = 0;
	private long _job_internal_id = 0;
	private java.sql.Timestamp _recover_point = null;
	private String _BackupName = "";
	private String _HostName = "";
	private String _CPU = "";
	private String _OS = "";
	private long _jobID = 0;
	private long _sessID = 0;
	private String _uniqueID = "";
	private String _sessGuid = "";
	private String _BackupType = "";
	private int _CompressType = 0;
	private long _DataSizeKB = 0;
	private long _TransferDataSizeKB = 0;
	private long _protectedDataSizeB = 0;
	private long _CatalogSizeB = 0;
	private int  _EncryptType = 0;
	private String _EncryptPasswordHash = "";
	private long _TotalRawDataSizeWritten = 0;
	private String _Status = "";
	private String _Operation = "";
	private String _PolicyName = "";
	private String _TargetDataStoreName = "";
	private String _TargetRps = "";

	private int _updateJobCnt = 0;
	private int _updateSessCnt = 0;

	
	public D2DBackupCacheXmlParser(String XMLContent) {
		m_xmlContent = XMLContent;
	}

	/*
	 * Return Code: 0 succeeded 1 XML parser error 2 SQL operation error
	 * 			   -1 unexpected exception
	 */
	public int processBackupCacheContent(boolean cleanFlag) {
		// boolean result = this.unmashallBackupCache();

		try {
			this.cleanFlag = cleanFlag;
			int result = doRoot();
	
			if(result != 0)
				writeActivityLog(Severity.Error, D2DSyncMessage.EDGE_D2D_SYNC_BACKUP_JOB_FAILED);
			else
				writeActivityLog(Severity.Information, D2DSyncMessage.EDGE_D2D_SYNC_BACKUP_JOB_SUCCEEDED);
			
			return result;
		}catch(Throwable t) {
			logger.error(t.toString());
			return -1;
		}
	}

	private int unmashallBackupCache() {
		if (m_xmlContent == null || m_xmlContent.equals("ERROR")) {
			return -1;
		}
		else if(m_xmlContent.isEmpty()) {
			return 0;
		}
		
		try {
			// JAXB
			JAXBContext jaxbContext = JAXBContext
					.newInstance("com.ca.arcserve.edge.app.base.webservice.d2ddatasync", BackupCache.class.getClassLoader());
			Unmarshaller unmarsh = jaxbContext.createUnmarshaller();
			m_cache = (BackupCache) unmarsh.unmarshal(new StreamSource(
					new StringReader(m_xmlContent)));

		} catch (Exception e) {
			logger.error(this.getClass().getName()+" xmlContent:'"+e.toString()+"'");
			return -1;
		}

		return 0;
	}

	private int doRoot() {
		boolean sync_result;
		
		if (m_xmlContent == null)
			return 1;

		syncer = new D2DBackupDataSynchronizer();

		syncer.connect();
		
		boolean needEnd=false;
		boolean transResult = true;
		try {
			int parsRet = unmashallBackupCache();
			if (parsRet < 0) {
				logger.debug("invalid backup cache XML content!!!");
				return 1;
			}
			else if(parsRet == 1) {
				logger.debug("nothing to sync, return!!!");
				return 0;
			}
			
			sync_result = syncer.begin();
			if (sync_result == false) {
				logger.debug("doRoot(): SQL Operation failed - D2DBackupDataSynchronizer.begin()\n");
				return 2;
			}
			
			needEnd = true;
			
			int result = doBackupCacheID(m_cache.getBackupCacheID(), cleanFlag);
			if (result < 0) {
				transResult = false;
				logger.debug("doBackupCacheID() failed!!!");
				return 2;
			}
			
			if( false == syncer.end(true, -1, -1) )
			{
				logger.debug("doRoot(): SQL Operation failed - D2DBackupDataSynchronizer.end()\n");
				needEnd = false;
				return 2;
			}
			needEnd = false;
			
			sync_result = syncer.begin();
			if (sync_result == false) {
				logger.debug("doRoot(): SQL Operation failed - D2DBackupDataSynchronizer.begin()\n");
				return 2;
			}
			
			needEnd = true;
			transResult = true;
			
			List<BackupRecord> Records = m_cache.getBackupRecord();
			Iterator<BackupRecord> iter = Records.iterator();
			while (iter.hasNext()) {
				BackupRecord _record = iter.next();
				result = doBackupRecord(_record);

				if (result != 0) {
					transResult = false;
					logger.debug("doBackupRecord() failed!!!");
					return result;
				}
			}
		} catch (Throwable e) {
			logger.error("doRoot(): XML parser failed!");
			logger.error(e.toString());
			transResult = false;
			return -1;
		} finally {
			try {
				if(needEnd) {
					sync_result = syncer.end(transResult, _BackupCacheID, branchid);
					if (sync_result == false) {
						logger.error("doRoot(): SQL Operation failed - close transaction\n");
						return 2;
					}
				}
			}catch(Throwable t) {
				logger.error(t.toString());
				return -1;
			}finally {
				syncer.disconnect();
			}
		}

		logger.debug("Synchronization succeeded! (totally updated job: "
				+ _updateJobCnt + "; totally updated session: "
				+ _updateSessCnt + ")\n");

		return 0;
	}
	
	private int doBackupCacheID(BigInteger BackupCacheID, boolean cleanFlag) {
		_BackupCacheID = BackupCacheID.longValue();

		if (_BackupCacheID == 0 || cleanFlag == true) {
			logger.debug("Got ReSync requerst, delete all records firstly!!\n");
			boolean result = syncer.DeleteAllByBranchId(branchid); // TODO
			if (result == false) {
				logger.debug("DeleteAllByBranchId() failed!!\n");
				return -2;
			}
		}

		return 0;
	}

	private int doBackupRecord(BackupRecord record) {
		String Operation = record.getOperation();
		BackupInfo info = record.getBackupInfo();
		RpsJobInfo rpsJobInfo = record.getRpsJobHistoryData();
		List<SessionInfo> sessionInfo = record.getSessionInfo();

		int result = 0;
		
		if(Operation.matches("ADD") || Operation.matches("DELETE"))
		{
			_Operation = Operation;
			
			_uniqueID = record.getUniqueID();
			
			if (rpsJobInfo != null) {
				result = doRpsJobInfo(rpsJobInfo);
			} else if (sessionInfo != null && !sessionInfo.isEmpty()) {
				result = doRpsSessionInfo(sessionInfo);
			} else {
				result = doBackupInfo(info);
			}
			
			return result;
		}
		else
		{
			logger.debug("Invalid operation: (" + Operation + ")\n");
			return 1;
		}
	}
	
	private int doRpsSessionInfo(List<SessionInfo> info) {
		RpsBaseSynchronizer rpsSyncer = new RpsSessionInfoSynchronizer(branchid, info);
		
		int result = rpsSyncer.doSync(_Operation);
		if(result != 0){
			logger.debug("Invalid RpsSessionInfo");
			return result;
		}
		return 0;
	}
	
	private int doRpsJobInfo(RpsJobInfo info) {
		RpsBaseSynchronizer rpsSyncer = new RpsJobInfoSynchronizer(branchid, info);
		
		int result = rpsSyncer.doSync(_Operation);
		if (result != 0) {
			logger.debug("Invalid RpsJobInfo");
			return result;
		}
		return 0;
	}

	private int doBackupInfo(BackupInfo Info) {
		String Status = Info.getBackupStatus().getStatus();
		BackupDetail detail = Info.getBackupDetail();
		TimeStamp stamp = Info.getTimeStamp();
		ServerInfo serverInfo = Info.getServerInfo();

		_BackupName = Info.getBackupName();

		doServerInfo(serverInfo);

		doTimeStamp(stamp);
		
		doBackupStatus(Status);

		int result = doBackupDetail(detail);
		if (result != 0) {
			logger.debug("Invalid BackupInfo (BackupDetail)\n");
			return 1;
		}

		return 0;
	}

	private void doBackupStatus(String BackupStatus) {
		_Status = BackupStatus;
	}

	private int doBackupDetail(BackupDetail backupDetail) {
		_jobID = backupDetail.getJobID().longValue();
		_sessID = backupDetail.getID().longValue();
		_sessGuid = backupDetail.getSessGuid();
		_BackupType = backupDetail.getBackupType();
		_CompressType = backupDetail.getCompressType().intValue();
		_DataSizeKB = backupDetail.getDataSizeKB().longValue();
		_EncryptType = backupDetail.getEncryptType().intValue();
		_EncryptPasswordHash = backupDetail.getEncryptPasswordHash();
		_TotalRawDataSizeWritten = backupDetail.getTotalRawDataSizeWritten().longValue();
		_PolicyName = backupDetail.getPolicyName();
		_TargetDataStoreName = backupDetail.getTargetDataStoreName();
		_TargetRps = backupDetail.getTargetRps();
		
		if(_Operation.matches("DELETE"))
		{//Purge record
			logger.debug("Purge (sessid:" + _sessID + ",uniqueID:" + _uniqueID + ",branchid:" + branchid + ")...\n");
			if( false == syncer.Purge_D2D_Job(_sessID, _uniqueID, branchid))
			{
				logger.debug("SQL Operation failed.(syncer.Purge_D2D_Job)\n");
				return 2;
			}
			
			return 0;
		}
		
		String tmpStr = backupDetail.getTransferDataSizeKB();
		if(tmpStr.isEmpty())
			_TransferDataSizeKB = 0;
		else
			_TransferDataSizeKB = Long.valueOf(tmpStr);
		//20130704 add for ManagedCapacityReport , which need to know the correct protected data size what ever Full backup or Incremental backup
		if(StringUtil.isEmptyOrNull(backupDetail.getProtectedDataSizeB()))
			_protectedDataSizeB = 0;
		else
			_protectedDataSizeB = Long.valueOf(backupDetail.getProtectedDataSizeB());
		//End add
		_CatalogSizeB = backupDetail.getCatalogSizeB().longValue();

		Integer BMRFlag = (backupDetail.getBMRFlag()==null)?-1:backupDetail.getBMRFlag(); //20110628 for sync BMR Flag
		
		long dest_path_id = 0; // TODO
		String backupDest = (backupDetail.getBackupDest()==null)?"":backupDetail.getBackupDest();
		boolean result = syncer
				.Insert_D2D_Job(_jobID, _sessID, _sessGuid, _uniqueID, _BackupName, _BackupType,
						_CompressType, _DataSizeKB, _TransferDataSizeKB,_protectedDataSizeB,
						_CatalogSizeB, _Status, dest_path_id, backupDest, _recover_point,
						_EncryptType, _EncryptPasswordHash,
						_TotalRawDataSizeWritten, BMRFlag, branchid);
		if (result == false) {
			logger.debug("SQL Operation failed.(syncer.Insert_D2D_Job)\n");
			return 2;
		}
		
		if (!StringUtil.isEmptyOrNull(_TargetDataStoreName)
				|| !StringUtil.isEmptyOrNull(_PolicyName)) {
			result = syncer.Insert_D2D_Job_RPS_Info(_jobID, _uniqueID,
					branchid, _TargetDataStoreName, _PolicyName);
			if (result == false) {
				logger.debug("SQL Operation failed.(syncer.Insert_D2D_Job_RPS_Info)\n");
				return 2;
			}
		}
		
		_updateJobCnt++;

		List<RootItem> itemLst = backupDetail.getRootItem();
		Iterator<RootItem> iter = itemLst.iterator();
		while (iter.hasNext()) {
			RootItem item = iter.next();

			int ret = doRootItem(item);
			if (ret != 0) {
				logger.debug("Invalid RootItem!!\n");
				return 1;
			}
		}

		return 0;
	}

	private int doRootItem(RootItem rootItem) {

		String _Type = rootItem.getType();
		String _DisplayName = rootItem.getDisplayName();
		String _MountPoint = rootItem.getMountPoints();
		String _GUID = rootItem.getGUID();
		long _VolumeDataSizeB = rootItem.getVolumeDataSizeB();
		long _SubSessNo = rootItem.getSubSessNo().longValue();
		String _CatalogFile = rootItem.getCatalogFile();
		String _IsBootVolume = rootItem.getIsBootVolume();
		String _IsSystemVolume = rootItem.getIsSystemVolume();

		long _job_internal_id = syncer.GetJobInternalId(_jobID, _uniqueID, branchid);
		if (_job_internal_id == -1) {
			logger.debug("SQL Operation Failed: (syncer.GetJobInternalId)\n");
			return 2;
		}

		boolean result = syncer.Insert_D2D_Session(_SubSessNo,
				_job_internal_id, _Type, 0, _DisplayName, _MountPoint, _GUID,
				_VolumeDataSizeB, _CatalogFile, 
				Integer.parseInt(_IsBootVolume), 
				Integer.parseInt(_IsSystemVolume), 
				branchid);
		if (result == false) {
			logger.debug("SQL Operation Failed: (syncer.Insert_D2D_Session)\n");
			return 2;
		}
		_updateSessCnt++;

		return 0;
	}
    
	private void doTimeStamp(TimeStamp timeStamp) {
		String StampDateTime = "";

		if(_Operation.matches("ADD"))
		{
			StampDateTime = timeStamp.getDate() + " " + timeStamp.getTime();
			
			_recover_point = Timestamp.valueOf(StampDateTime);
		}
	}

	private void doServerInfo(ServerInfo serverInfo) {
		_HostName = serverInfo.getHostName();
		;

		_CPU = serverInfo.getCPU();

		_OS = serverInfo.getOS();
	}

	public BackupCache getCache() {
		return m_cache;
	}
}
