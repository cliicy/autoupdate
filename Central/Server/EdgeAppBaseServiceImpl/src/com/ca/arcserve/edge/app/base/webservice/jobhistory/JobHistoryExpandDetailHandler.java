package com.ca.arcserve.edge.app.base.webservice.jobhistory;

import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.jobqueue.encrypt.Base64;
import com.ca.arcflash.webservice.constants.JobType;
import com.ca.arcflash.webservice.data.edge.datasync.vcm.VCMJobReport;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeJobHistoryDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.d2ddatasync.BackupDetail;
import com.ca.arcserve.edge.app.base.webservice.d2ddatasync.BackupInfo;
import com.ca.arcserve.edge.app.base.webservice.d2ddatasync.RootItem;

public class JobHistoryExpandDetailHandler {
	private static IEdgeJobHistoryDao jobHistoryDao = DaoFactory.getDao(IEdgeJobHistoryDao.class);
	private static Logger logger =  Logger.getLogger(JobHistoryExpandDetailHandler.class);
	public  static  final Integer JobDetailFieldIndex = 19;
	
	public void parseJobExpandDetail( long jobHistoryId, long jobType, long jobStatus,  String jobDetail, boolean isMsp ) {
		try {
			if( jobHistoryId<=0 ) {
				logger.warn( "JobHistoryExpandDetailHandler: " + "the job history has a wrong id = " + jobHistoryId
						+ " jobType: " +jobType  + " jobStatus: " +jobStatus );
			}
			else if( jobType == JobType.JOBTYPE_BACKUP || jobType == JobType.JOBTYPE_VM_BACKUP || jobType ==JobType.JOBTYPE_CONVERSION ) {
				
				if( StringUtil.isEmptyOrNull( jobDetail ) ) {
					
					///now the job history detail table used as unique data source for report! so we always insert data, no matter jobDetail exist or not!!
					///all column use default value;
					jobHistoryDao.as_edge_d2dJobHistoryDetail_add( jobHistoryId, 0, 0, 0, -1, -1, -1, //size default 0; -1: the data is meaningless
							"",  "",  ///destination , session default empty
							-1, -1,  -1,  ///bmr and other session flag default -1
							"",   ///vcm related col default empty
							"", 
							-1,   //vcm destination vm type default -1;
							"" );
				}
				else {	
					jobDetail = jobDetail.trim();
		
					if( jobType == JobType.JOBTYPE_BACKUP || jobType == JobType.JOBTYPE_VM_BACKUP ) {
						parseBackupJobDetail( jobHistoryId , jobDetail,  isMsp );
					}
					if( jobType == JobType.JOBTYPE_CONVERSION ) {
						parseVCMJobDetail( jobHistoryId ,  jobDetail  );
					}
				}
			}
		}
		catch ( Exception e ) {
			logger.error( "JobHistoryExpandDetailHandler: " + "parse jon detail info failed with "
					+ " jobType: " +jobType  + " jobStatus: " +jobStatus +" details:" + jobDetail , e );
		}
	}
	
	public void parseBackupJobDetail( long jobHistoryId,  String jobDetail, boolean isMsp ) throws Exception {
	
		String backupInfoXml = BackupInfoPaser.decodeBackupInfoXml(jobDetail);     /// test   test_backupInfo();   
		BackupInfo bkInfo = BackupInfoPaser.ParseBackupInfo(backupInfoXml);
		if(bkInfo == null || bkInfo.getBackupDetail()==null) {
			logger.warn( "JobHistoryExpandDetailHandler.parseBackupJobDetail: backupInfo or backDetail is null:  " + jobDetail );
			return;
		}
		
		BackupDetail bkDetail = bkInfo.getBackupDetail();
		
		long protectedDataSize = parseStringToLong(bkDetail.getProtectedDataSizeB());
		long rawDataSize = bkDetail.getTotalRawDataSizeWritten()==null?0:bkDetail.getTotalRawDataSizeWritten();
		long backupedDataSize =  bkDetail.getDataSizeKB()==null?0:bkDetail.getDataSizeKB();
		long sessionIds = bkDetail.getID()==null?0:Long.valueOf(bkDetail.getID().longValue());
		long syncReadSixe = bkDetail.getSyntheticRawSizeRead();
		long ntfsVolumeSize = bkDetail.getNtfsVolumeSizeByBitMap();
		long virtualDiskProvisionSize = bkDetail.getVirtualDiskProvisionSize();
		
		String backupDestination = bkDetail.getBackupDest()==null?"":bkDetail.getBackupDest();
		
		////0 not encrypted; 1 encrypt
		int sessionEncrypted = (bkDetail.getEncryptType()==null || bkDetail.getEncryptType() == 0)?0:1;
		//bmr
		int bmrFlag  = bkDetail.getBMRFlag()==null?-1:bkDetail.getBMRFlag();////0 or bigger(1168) value can do bmr; -1 can not
		///recoveryPoint type only used by hbbu 
		int recoveryPointType = 1;   ///1 means only machine level recovery;

		List<RootItem> rootItemList = bkDetail.getRootItem();  //fix issue 16861
		if(rootItemList!=null && rootItemList.size()>0) {
			boolean isSystem = false;
			boolean isBootable = false;
			recoveryPointType = 0;////0 means file level; if the backup info has volume info, recoveryPointType =0;
			for( RootItem item : rootItemList  ) {
				if( item.getType()!=null && item.getType().equalsIgnoreCase("Volume") ) {
					if( item.getIsBootVolume()!=null && item.getIsBootVolume().equalsIgnoreCase("TRUE") ) {
						isBootable = true;
					}
					if( item.getIsSystemVolume() !=null && item.getIsSystemVolume().equalsIgnoreCase("TRUE") ) {
						isSystem = true;
					}
				}
			}
			bmrFlag = ( isSystem && isBootable ) == true ? 0: bmrFlag;  //// 0 or bigger(1168) value can do bmr; -1 can not same as asbu;
			
			// fix 108750: Most Recent Successful Disaster Recovery Backup is not displayed for EFI boot server
			if(  isBootable == true && isSystem == false ) {
				if(  bkInfo.getServerInfo().getBootFirmware() != null &&
						"UEFI".equalsIgnoreCase( bkInfo.getServerInfo().getBootFirmware().trim() ) ) {
					bmrFlag = 0;
				}
			}
		}
		jobHistoryDao.as_edge_d2dJobHistoryDetail_add( jobHistoryId, protectedDataSize, rawDataSize, backupedDataSize, syncReadSixe, ntfsVolumeSize, virtualDiskProvisionSize,
				backupDestination,  Long.toString(sessionIds ),  bmrFlag, sessionEncrypted,  recoveryPointType, "", "", -1, "");
		
		if( !isMsp ) {
			//RecoveryPointHandler.getInstance().addRecoveryPoint(jobHistoryId, bkDetail, backupDestination, rawDataSize, backupedDataSize );
		}
		
	}
	
	private long parseStringToLong( String rawData){
		if(StringUtil.isEmptyOrNull(rawData))
			return 0;
		long longData =0;
		try {
			longData = Long.parseLong( rawData );
		}
		catch( Exception e ) {
			logger.error("failed parse protectedDataSize with: " + " expression: " + rawData, e );
		}
		return longData;
	}
	
	public void parseVCMJobDetail( long jobHistoryId , String detail ) {
		VCMJobReport jobReport = null;
		String jobXMLString ="";
		try {
			jobXMLString = Base64.decode(detail);
		}	
		catch( Exception e ) { 
			logger.error("parse vcm job detail info from base64 fail! with job detail: " + detail  , e);
			return;
		}
		try {
			jobReport = CommonUtil.unmarshal(jobXMLString, VCMJobReport.class);
	
		}
		catch( Exception e ) { 
			logger.error("parse vcm job detail info from xml fail! with job xml: " + jobXMLString  , e);
			return;
		}
		if( StringUtil.isEmptyOrNull(jobReport.getConvertSessions()) ) {
			logger.error("parse vcm job session info is empty! with  " + jobXMLString  );
		}
		jobHistoryDao.as_edge_d2dJobHistoryDetail_add( jobHistoryId, 0, 0, 0,  -1, -1, -1, //-1: the data is meaningless
				"",  jobReport.getConvertSessions(),  -1, -1,  -1, 
				jobReport.getHypervisorHostname(), 
				StringUtil.isEmptyOrNull( jobReport.getVCenterHostname() )?"":jobReport.getVCenterHostname() , 
				jobReport.getVMDestType(), 
				StringUtil.isEmptyOrNull( jobReport.getMonitorHostname() )? "": jobReport.getMonitorHostname() );

	}
	
	
//	private String test_backupInfo(){
//		try {
//			String xmlname = "F:\\hbbu_BackupInfo2.xml";
//			File f = new File( xmlname );
//			FileInputStream input = new FileInputStream(f);
//			
//			/*use channel parse as string*/
//			FileChannel inChannel = input.getChannel();
//			MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, f.length());
//			
//			CharsetDecoder decoder = Charset.forName("ISO-8859-1").newDecoder();
//			CharBuffer charBuffer =  decoder.decode(buffer);
//			return charBuffer.toString();
//		}
//		catch( Exception e ){
//			return "";
//		}
//	}
}
