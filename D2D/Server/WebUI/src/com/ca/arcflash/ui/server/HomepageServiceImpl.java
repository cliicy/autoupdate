package com.ca.arcflash.ui.server;

import java.io.File;
import java.net.ConnectException;
import java.net.ProtocolException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.net.ssl.SSLException;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.ws.WebServiceException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.ca.arcflash.rps.webservice.data.datastore.DataStoreRunningState;
import com.ca.arcflash.serviceinfo.ServiceInfo;
import com.ca.arcflash.serviceinfo.ServiceInfoConstants;
import com.ca.arcflash.serviceinfo.ServiceInfoList;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.exception.ServiceConnectException;
import com.ca.arcflash.ui.client.exception.ServiceInternalException;
import com.ca.arcflash.ui.client.exception.SessionTimeoutException;
import com.ca.arcflash.ui.client.homepage.HomepageService;
import com.ca.arcflash.ui.client.model.ArchiveJobInfoModel;
import com.ca.arcflash.ui.client.model.BIPatchInfoModel;//added by cliicy.luo
import com.ca.arcflash.ui.client.model.BackupInformationSummaryModel;
import com.ca.arcflash.ui.client.model.BackupSetInfoModel;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.ca.arcflash.ui.client.model.BackupSettingsScheduleModel;
import com.ca.arcflash.ui.client.model.BackupVMModel;
import com.ca.arcflash.ui.client.model.DataStoreInfoModel;
import com.ca.arcflash.ui.client.model.DestinationCapacityModel;
import com.ca.arcflash.ui.client.model.JobMonitorModel;
import com.ca.arcflash.ui.client.model.LicInfoModel;
import com.ca.arcflash.ui.client.model.MergeJobMonitorModel;
import com.ca.arcflash.ui.client.model.MergeStatusModel;
import com.ca.arcflash.ui.client.model.NextScheduleEventModel;
import com.ca.arcflash.ui.client.model.PatchInfoModel;
import com.ca.arcflash.ui.client.model.ProtectionInformationModel;
import com.ca.arcflash.ui.client.model.RecentBackupModel;
import com.ca.arcflash.ui.client.model.RecoveryPointModel;
import com.ca.arcflash.ui.client.model.TrustHostModel;
import com.ca.arcflash.ui.client.model.VMStatusModel;
import com.ca.arcflash.ui.client.model.VirtualCenterNodeModel;
import com.ca.arcflash.ui.client.model.rps.RpsHostModel;
import com.ca.arcflash.ui.client.model.rps.RpsPolicy4D2D;
import com.ca.arcflash.ui.server.servlet.SessionConstants;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.IFlashService4RPS;
import com.ca.arcflash.webservice.IFlashService_R16_U7;
import com.ca.arcflash.webservice.IFlashVSphere;
import com.ca.arcflash.webservice.WebServiceClientProxy;
import com.ca.arcflash.webservice.WebServiceFactory;
import com.ca.arcflash.webservice.data.BackupInformationSummary;
import com.ca.arcflash.webservice.data.DestinationCapacity;
import com.ca.arcflash.webservice.data.LicInfo;
import com.ca.arcflash.webservice.data.NextArchiveScheduleEvent;
import com.ca.arcflash.webservice.data.NextScheduleEvent;
import com.ca.arcflash.webservice.data.ProtectionInformation;
import com.ca.arcflash.webservice.data.RPSDataStoreInfo;
import com.ca.arcflash.webservice.data.RecentBackup;
import com.ca.arcflash.webservice.data.TrustedHost;
import com.ca.arcflash.webservice.data.PM.BIPatchInfo;
import com.ca.arcflash.webservice.data.PM.PatchInfo;
import com.ca.arcflash.webservice.data.archive.ArchiveJobInfo;
import com.ca.arcflash.webservice.data.backup.BackupConfiguration;
import com.ca.arcflash.webservice.data.merge.BackupSetInfo;
import com.ca.arcflash.webservice.data.merge.MergeAPISource;
import com.ca.arcflash.webservice.data.merge.MergeJobMonitor;
import com.ca.arcflash.webservice.data.merge.MergeStatus;
import com.ca.arcflash.webservice.data.restore.RecoveryPoint;
import com.ca.arcflash.webservice.data.vsphere.BackupVM;
import com.ca.arcflash.webservice.data.vsphere.ESXServer;
import com.ca.arcflash.webservice.data.vsphere.VMItem;
import com.ca.arcflash.webservice.data.vsphere.VMStatus;
import com.ca.arcflash.webservice.data.vsphere.VirtualMachine;

public class HomepageServiceImpl extends BaseServiceImpl implements HomepageService{
	/**
	 *
	 */
	private static final long serialVersionUID = -6798754700270124598L;

	private static final Logger logger = Logger
			.getLogger(HomepageServiceImpl.class);

	@Override
	public ProtectionInformationModel[] getProtectionInformation() throws BusinessLogicException, ServiceConnectException, ServiceInternalException{
		try
		{
			ProtectionInformation[] result = getServiceClient().getService().getProtectionInformation();
			return convertProtectionInformation(result);
		}catch(WebServiceException exception){
			proccessAxisFaultException(exception);
		}

		return null;
	}

	private Boolean sessionInvalid() {
		if ( this.getThreadLocalRequest().getSession(true).getAttribute(SessionConstants.SRING_USERNAME) == null
				&& this.getThreadLocalRequest().getSession(true).getAttribute(SessionConstants.SRING_UUID) == null)
			
			return Boolean.TRUE;
		return Boolean.FALSE;
	}
	
	@Override
	public ProtectionInformationModel[] updateProtectionInformation() throws BusinessLogicException, ServiceConnectException, ServiceInternalException{
		if (sessionInvalid())
			throw new SessionTimeoutException();
		try{
			ProtectionInformation[] result = getServiceClient().getService().updateProtectionInformation();
			ProtectionInformationModel[] updatedModles = convertProtectionInformation(result);
			return updatedModles;

		}catch(WebServiceException exception){
			exception.printStackTrace();
			proccessAxisFaultException(exception);
		}

		return null;
	}
	
	@Override
	public ProtectionInformationModel[] updateVMProtectionInformation(BackupVMModel vmModel) throws BusinessLogicException, ServiceConnectException, ServiceInternalException{
		if (sessionInvalid())
			throw new SessionTimeoutException();
		try{
			ProtectionInformation[] result = getServiceClient().getServiceV2().updateVMProtectionInformation(ConvertToVirtualMachine(vmModel));
			ProtectionInformationModel[] updatedModles = convertProtectionInformation(result);
			return updatedModles;
			
		}catch(WebServiceException exception){
			exception.printStackTrace();
			proccessAxisFaultException(exception);
		}
		
		return null;
	}


	@Override
	public RecoveryPointModel[] getRecentBackups(int backupType, int backupStatus,int top)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		try
		{
			RecoveryPoint[] recoveryPoints = getServiceClient().getService().getMostRecentRecoveryPoints(backupType, backupStatus,top);
			if (recoveryPoints!=null && recoveryPoints.length>0){
				List<RecoveryPointModel> result = new LinkedList<RecoveryPointModel>();
				for(RecoveryPoint point : recoveryPoints){
					result.add(ConvertToModel(point));
				}

				return result.toArray(new RecoveryPointModel[0]);
			}else
				return null;
		}catch(WebServiceException exception){
			proccessAxisFaultException(exception);
		}

		return null;
	}

	@Override
	public RecoveryPointModel[] getRecentBackupsByServerTime(int backupType, int backupStatus, String serverBeginDate, String serverEndDate, boolean needCatalogStatus)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		try
		{
			RecoveryPoint[] recoveryPoints = getServiceClient().getServiceV2().getRecentBackupsByServerTime(backupType, backupStatus, serverBeginDate, serverEndDate, needCatalogStatus);
			if (recoveryPoints!=null && recoveryPoints.length>0){
				List<RecoveryPointModel> result = new LinkedList<RecoveryPointModel>();
				for(RecoveryPoint point : recoveryPoints){
					result.add(ConvertToModel(point));
				}

				return result.toArray(new RecoveryPointModel[0]);
			}else
				return null;
		}catch(WebServiceException exception){
			proccessAxisFaultException(exception);
		}

		return null;
	}

	@Override
	public BackupInformationSummaryModel getBackupInforamtionSummary()
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		try
		{
			BackupInformationSummary summary = this.getServiceClient().getServiceV2().getBackupInformationSummary();
			if(summary == null)
			{
				summary = new BackupInformationSummary();
			}

//			summary.setPatchInfo(this.getServiceClient().getServiceV2().getPatchInfo());
			return convertBackupInformationSummary(summary);
		}catch(WebServiceException exception){
			proccessAxisFaultException(exception);
		}

		return null;
	}

	@Override
	public BackupInformationSummaryModel getBackupInforamtionSummaryWithLicInfo()
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		try
		{
			BackupInformationSummary summary = this.getServiceClient().getService().getBackupInformationSummaryWithLicInfo();
			if(summary == null)
			{
				summary = new BackupInformationSummary();
			}
			//cliicy.luo for debug test 
			//summary.setBIPatchInfo(getBIPatchInfo("C:\\Program Files\\Arcserve\\Unified Data Protection\\Update Manager\\EngineUpdates\\r6.0_Binary\\Status.xml"));
			//cliicy.luo for debug test
			summary.setPatchInfo(this.getServiceClient().getServiceV2().getPatchInfo());
			summary.setBIPatchInfo(this.getServiceClient().getServiceV2().getPMBIPatchInfo());//added by cliicy.luo
			return convertBackupInformationSummary(summary);
		}catch(WebServiceException exception){
			proccessAxisFaultException(exception);
		}

		return null;
	}

	@Override
	public DestinationCapacityModel getDestSizeInformation(BackupSettingsModel model)
		throws BusinessLogicException, ServiceConnectException, ServiceInternalException{
		try{

			BackupConfiguration configuration = convertToBackupConfiguration(model);
			DestinationCapacity destInfo = this.getServiceClient().getService().getDestSizeInformation(configuration);
			DestinationCapacityModel destModel = convertDestinationInformation(destInfo);
			return destModel;

		}catch (WebServiceException e) {
			proccessAxisFaultException(e);
		}

		return null;
	}

	@Override
	public NextScheduleEventModel getNextScheduleEvent(int in_iJobType)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		try
		{
			NextScheduleEventModel eventModel = null;
			switch(in_iJobType)
			{
			case JobMonitorModel.JOBTYPE_ARCHIVE:
				NextArchiveScheduleEvent nextArchiveEvent = this.getServiceClient().getServiceV2().getNextArchiveScheduleEvent();
				eventModel = convertNextArchiveScheduleEvent(nextArchiveEvent);
				break;
			case JobMonitorModel.JOBTYPE_BACKUP:
				NextScheduleEvent nextEvent = this.getServiceClient().getServiceV2().getNextScheduleEvent();
				eventModel = convertNextScheduleEvent(nextEvent);
				break;
			}
			
			return eventModel; 
		}catch(WebServiceException exception){
			proccessAxisFaultException(exception);
		}

		return null;
	}
	
	private NextScheduleEventModel convertNextArchiveScheduleEvent(
			NextArchiveScheduleEvent nextArchiveEvent) {
		NextScheduleEventModel model = new NextScheduleEventModel();
		model.setBackupType(nextArchiveEvent.getJobType());
		model.setDate(nextArchiveEvent.getDate());
		model.setarchiveEvent(nextArchiveEvent.getarchiveEvent());
		
		return model;
	}

	private NextScheduleEventModel convertNextScheduleEvent(
			NextScheduleEvent nextEvent) {
		if (nextEvent == null)
			return null;

		NextScheduleEventModel model = new NextScheduleEventModel();
		model.setBackupType(nextEvent.getBackupType());
		model.setDate(nextEvent.getDate());
		model.setServerTimeZoneOffset(nextEvent.getTimeZoneOffset());
		return model;
	}

	private BackupInformationSummaryModel convertBackupInformationSummary(
			BackupInformationSummary summary) {
		if (summary!=null){
			BackupInformationSummaryModel result = new BackupInformationSummaryModel();

			result.setRecoveryPointCount(summary.getRecoveryPointCount());
			result.setRecoverySetCount(summary.getRecoverySetCount());
			result.setMergeStartTime(summary.getMergeJobScheduleTime());
			result.setTotalSuccessfulCount(summary.getTotalSuccessfulCount());
			result.setTotalFailedCount(summary.getTotalFailedCount());
			result.setTotalCanceledCount(summary.getTotalCanceledCount());
			result.setTotalCrashedCount(summary.getTotalCrashedCount());
			result.setRetentionCount(summary.getRetentionCount());
			result.setDestination(summary.getBackupDestination());
			result.setErrorCode(summary.getErrorCode());
			result.setSpaceMeasureNum(summary.getSpaceMeasureNum());
			result.setSpaceMeasureUnit(summary.getSpaceMeasureUnit());
			result.setAdvanced(summary.isAdvanced());
			result.setPeriodEnabled(summary.isPeriodEnabled());
			if(summary.getAdvanceSchedule() != null){
				result.setAdvanceScheduleModel(ConvertDataToModel.convertToAdvanceScheduleModel(summary.getAdvanceSchedule()));
			}	
			result.setRecoveryPointCount4Repeat(summary.getRecoveryPointCount4Repeat());
			result.setRecoveryPointCount4Day(summary.getRecoveryPointCount4Day());
			result.setRecoveryPointCount4Week(summary.getRecoveryPointCount4Week());
			result.setRecoveryPointCount4Month(summary.getRecoveryPointCount4Month());
			
			//wanqi06
			result.setBackupSet(summary.isBackupSet());
			result.setIsInSchedule(summary.isInSchedule());
			
			if (summary.getRpsInfo()!= null){
			    RpsHostModel rpsHost = new RpsHostModel();
			    rpsHost.setHostName(summary.getRpsInfo().getRpsHostName());
			    result.setRpsHostModel(rpsHost);
			    RpsPolicy4D2D rpsPolicy = new RpsPolicy4D2D();
                rpsPolicy.setName(summary.getRpsInfo().getRpsPolicy());
                rpsPolicy.setDataStoreDisplayName(summary.getRpsInfo().getRpsDataStore());
                rpsPolicy.setDataStoreName(summary.getRpsInfo().getRpsDataStoreGuid());
                result.setRpsPolicy4D2D(rpsPolicy);
                switch (summary.getDsHealth()) {
			        case RED: result.setDsHealth("RED"); break;
			        case YELLOW: result.setDsHealth("YELLOW"); break;
			        case GREEN: result.setDsHealth("GREEN"); break;
			        case UNKNOWN: result.setDsHealth("UNKNOWN"); break;
			    }
                int dsRunningState = 3;
                if(summary.getDsRunningState() == DataStoreRunningState.RUNNING)
                	dsRunningState = 1;
                else if(summary.getDsRunningState() == DataStoreRunningState.DELETED)
                	dsRunningState = 2;                
                else 
                	dsRunningState = 3; // not running 
                
                result.setDSRunningState(dsRunningState);          
			}
			
//			if (summary.getRpsInfo()!= null){
//				RPSInfo rpsInfo = summary.getRpsInfo();
//				RPSInfoModel rpsInfoModel = ConvertRpsInfoModel(rpsInfo);
//				result.setRpsInfoModel(rpsInfoModel);
//			}
			
			if (summary.getDestinationCapacity()!=null){
				DestinationCapacity capacity = summary.getDestinationCapacity();
				DestinationCapacityModel destinationCapacityModel = convetDestCapacityModel(capacity);
				result.setDestinationCapacityModel(destinationCapacityModel);
			}
			if (summary.getLicInfo()!= null){
				LicInfo licInfo = summary.getLicInfo();
				LicInfoModel licInfoM = convetLicInfoModel(licInfo);
				result.setLicInfo(licInfoM);
			}

			if(summary.getPatchInfo() != null)
			{
				PatchInfo patchInfo = summary.getPatchInfo();
				PatchInfoModel patchInfoModel = ConvertPatchInfoModel(patchInfo);
				result.setpatchInfoModel(patchInfoModel);
			}
			//added by cliicy.luo
			if(summary.getBIPatchInfo() != null)
			{
				BIPatchInfo patchInfo = summary.getBIPatchInfo();		
				BIPatchInfoModel patchInfoModel = BIPatchInfoModel.ConvertBIPatchInfoModel(patchInfo);
				result.setBIpatchInfoModel(patchInfoModel);
			}
			//added by cliicy.luo
			
			if(summary.getArchiveJobInfo() != null)
			{
				result.setArchiveJobInfo(convertArchiveInfoModel(summary.getArchiveJobInfo()));
			}

			result.setRecentFullBackup(convertRecentBackup(summary.getRecentFullBackup()));
			result.setRecentIncrementalBackup(convertRecentBackup(summary.getRecentIncrementalBackup()));
			result.setRecentResyncBackup(convertRecentBackup(summary.getRecentResyncBackup()));
			result.setupdateSettingsModel(ConvertDataToModel.convertToUpdateSettingsModel(summary.getUpdateSettings()));
			return result;
		}
		return null;
	}

	private ArchiveJobInfoModel convertArchiveInfoModel(ArchiveJobInfo archiveInfo) {
		if(archiveInfo == null)
			return null;
		ArchiveJobInfoModel jobInfoModel = new ArchiveJobInfoModel();
		jobInfoModel.setarchiveJobStatus(archiveInfo.getarchiveJobStatus());
		jobInfoModel.setbackupSessionId(archiveInfo.getbackupSessionId());
		jobInfoModel.setbackupSessionPath(archiveInfo.getbackupSessionPath());
		jobInfoModel.setArchiveDataSize(Long.parseLong(archiveInfo.getArchiveDataSize()));
		jobInfoModel.setCopyDataSize(Long.parseLong(archiveInfo.getCopyDataSize()));
		
		/*Calendar cd = Calendar.getInstance();
		cd.add(Calendar.HOUR, Integer.parseInt(Long.toString(archiveInfo.getHour())));
		cd.add(Calendar.MINUTE, Integer.parseInt(Long.toString(archiveInfo.getMin())));
		cd.add(Calendar.SECOND, Integer.parseInt(Long.toString(archiveInfo.getSec())));
		cd.add(Calendar.YEAR, Integer.parseInt(Long.toString(archiveInfo.getYear())));
		cd.add(Calendar.DAY_OF_MONTH, Integer.parseInt(Long.toString(archiveInfo.getDay())));
		cd.add(Calendar.MONTH, Integer.parseInt(Long.toString(archiveInfo.getMonth())));*/
		
		//java.util.Calendar cd = new java.util.GregorianCalendar((int)archiveInfo.getYear(),(int)archiveInfo.getMonth()-1,(int)archiveInfo.getDay(),(int)archiveInfo.getHour(),(int)archiveInfo.getMin(),(int)archiveInfo.getSec());
		//jobInfoModel.setlastJobDateTime(cd.getTime());
		jobInfoModel.setlastJobDateTime(archiveInfo.getlastArchiveDateTime());
		return jobInfoModel;
	}
	
	
	private PatchInfoModel ConvertPatchInfoModel(PatchInfo in_patchInfo)
	{
		PatchInfoModel patchInfoModel = new PatchInfoModel();

		//product information
		patchInfoModel.setMajorversion(in_patchInfo.getMajorversion());
		patchInfoModel.setMinorVersion(in_patchInfo.getMinorVersion());
		patchInfoModel.setServicePack(in_patchInfo.getServicePack());

		////patch information
		patchInfoModel.setPackageID(in_patchInfo.getPackageID());
		patchInfoModel.setPublishedDate(in_patchInfo.getPublishedDate());
		patchInfoModel.setDescription(in_patchInfo.getDescription());
		patchInfoModel.setPatchDownloadLocation(in_patchInfo.getPatchDownloadLocation());
		patchInfoModel.setPatchURL(in_patchInfo.getPatchURL());
		patchInfoModel.setRebootRequired(in_patchInfo.getRebootRequired());
		patchInfoModel.setSize(in_patchInfo.getSize());
		patchInfoModel.setPatchVersionNumber(in_patchInfo.getPatchVersionNumber());
		patchInfoModel.setAvailableStatus(in_patchInfo.getAvailableStatus());
		patchInfoModel.setDownloadStatus(in_patchInfo.getDownloadStatus());
		patchInfoModel.setInstallStatus(in_patchInfo.getInstallStatus());
		patchInfoModel.setErrorMessage(in_patchInfo.getErrorMessage());
		patchInfoModel.setError_Status(in_patchInfo.getError_Status());

		return patchInfoModel;
	}

	
	private LicInfoModel convetLicInfoModel(LicInfo licInfo) {
		if(licInfo == null)
			return null;
		LicInfoModel lim = new LicInfoModel();
		lim.setBaseLic(licInfo.getBase());
		lim.setAllowBMR(licInfo.getAllowBMR());
		lim.setAllowBMRAlt(licInfo.getAllowBMRAlt());
		lim.setbLI(licInfo.getbLI());
		lim.setProtectExchange(licInfo.getProtectExchange());
		lim.setProtectHyperV(licInfo.getProtectHyperV());
		lim.setProtectSql(licInfo.getProtectSql());
		lim.setDwEncryption(licInfo.getDwEncryption());
		lim.setDwScheduledExport(licInfo.getDwScheduledExport());
		lim.setDwExchangeDB(licInfo.getDwExchangeDB());
		lim.setDwExchangeGR(licInfo.getDwExchangeGR());
		lim.setDwD2D2D(licInfo.getDwD2D2D());
		return lim;
	}
	
//	private RPSInfoModel ConvertRpsInfoModel(RPSInfo rpsInfo) {
//		if(rpsInfo == null)
//			return null;
//		RPSInfoModel rpsModel = new RPSInfoModel();
//		rpsModel.setRpsHostName(rpsInfo.getRpsHostName());
//		rpsModel.setRpsPort(rpsInfo.getRpsPort());
//		rpsModel.setRpsProtocol(rpsInfo.getRpsProtocol());
//		rpsModel.setRpsUserName(rpsInfo.getRpsUserName());
//		rpsModel.setRpsPassword(rpsInfo.getRpsPassword());
//		rpsModel.setRpsPolicyUUID(rpsInfo.getRpsPolicyUUID());		
//		rpsModel.setRpsDataStore(rpsInfo.getRpsDataStore());
//		rpsModel.setRpsDataStoreGuid(rpsInfo.getRpsDataStoreGuid());
//		return rpsModel;
//	}

	public static DestinationCapacityModel convetDestCapacityModel(
			DestinationCapacity capacity) {
		if(capacity == null)
			return null;

		DestinationCapacityModel destinationCapacityModel = new DestinationCapacityModel();
		destinationCapacityModel.setFullBackupSize(capacity.getFullBackupSize());
		destinationCapacityModel.setIncrementalBackupSize(capacity.getIncrementalBackupSize());
		destinationCapacityModel.setResyncBackupSize(capacity.getResyncBackupSize());
		destinationCapacityModel.setTotalFreeSize(capacity.getTotalFreeSize());
		destinationCapacityModel.setTotalVolumeSize(capacity.getTotalVolumeSize());
		destinationCapacityModel.setCatalogSize(capacity.getCatalogSize());
		return destinationCapacityModel;
	}

	private RecentBackupModel convertRecentBackup(RecentBackup backup){
		if (backup!=null){
			RecentBackupModel result = new RecentBackupModel();

			result.setStatus(backup.getStatus());
			result.setTime(backup.getTime());
			result.setType(backup.getType());
			result.setName(backup.getName());
			result.setTimeZoneOffset(backup.getTimeZoneOffset());
			
			return result;
		}
		return null;
	}

	private ProtectionInformationModel[] convertProtectionInformation(ProtectionInformation[] sources){
		if (sources == null)
			return null;

		List<ProtectionInformationModel> result = new LinkedList<ProtectionInformationModel>();
		for(ProtectionInformation item : sources){
			if (item == null)
				continue;

			ProtectionInformationModel model = new ProtectionInformationModel();
			model.setBackupType(item.getBackupType());
			model.setCount(item.getCount());
			model.setNextRunTime(item.getNextRunTime());
			model.setTotalLogicalSize(item.getTotalLogicalSize());
			model.setSize(item.getSize());
			model.setDedupe(item.isDedupe());
			model.setLastBackupTime(item.getLastBackupTime());
			model.setNextTimeZoneOffset(item.getNextTimeZoneOffset());

			if (item.getShedule()!=null){
				model.setSchedule(new BackupSettingsScheduleModel());
				model.getSchedule().setEnabled(item.getShedule().isEnabled());
				model.getSchedule().setInterval(item.getShedule().getInterval());
				model.getSchedule().setIntervalUnit(item.getShedule().getIntervalUnit());
			}

			result.add(model);
		}

		return result.toArray(new ProtectionInformationModel[0]);
	}

	private RecoveryPointModel ConvertToModel(RecoveryPoint recoveryPoint) {
		RecoveryPointModel model = new RecoveryPointModel();
		model.setBackupStatus(recoveryPoint.getBackupStatus());
		model.setBackupType(recoveryPoint.getBackupType());
		model.setLogicalSize(recoveryPoint.getLogicalSize());
		model.setDataSize(recoveryPoint.getDataSize());
		model.setSessionID(new Long(recoveryPoint.getSessionID()).intValue());
		model.setTime(recoveryPoint.getTime());
		model.setTimeZoneOffset(recoveryPoint.getTimeZoneOffset());
		model.setName(recoveryPoint.getName());
		model.setArchiveJobStatus(recoveryPoint.getArchiveJobStatus());
		model.setFSCatalogStatus(recoveryPoint.getFsCatalogStatus());
		model.setBackupSetFlag(recoveryPoint.getBackupSetFlag());
		model.setPeriodRetentionFlag(recoveryPoint.getPeriodRetentionFlag());
		model.setVMHypervisor(recoveryPoint.getVmHypervisor());
		model.setAgentBackupType(recoveryPoint.getAgentBackupType());
		return model;
	}

	public TrustHostModel[] getTrustHosts() throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		try {
			TrustedHost[] trustedHosts = new TrustedHost[0];
			if (getServiceClient() == null) {
				return new TrustHostModel[0];
			}

			trustedHosts = getServiceClient().getService().getTrustedHosts();

			HttpSession session = this.getThreadLocalRequest().getSession(false);

			TrustHostModel selected = null;
			if(session!=null)
			{
				selected = (TrustHostModel)session.getAttribute(SessionConstants.SRING_SELTRUSTHOST);
			}

			TrustedHost local = getLocalWebServiceClient().getService()
					.getLocalHostAsTrust();
			if (local.getPort() == 0) {
				local.setPort(getLocalWebServiceClient().getPort());
			}
			local.setProtocol(getLocalWebServiceClient().getProtocol());
			TrustHostModel localModel = ConvertToModel(local);
			List<TrustHostModel> result = new LinkedList<TrustHostModel>();
			if (selected == null) {
				selected = localModel;
				this.getThreadLocalRequest().getSession(true).setAttribute(
						SessionConstants.SRING_SELTRUSTHOST, selected);
			}
			else
			{
				result.add(selected);
			}
			selected.setSelected(true);

//			if (localModel.getHostName().equalsIgnoreCase(
//					selected.getHostName())) {
//				localModel.setSelected(true);
//			} else {
//				localModel.setSelected(false);
//			}
			if(selected!=null && !selected.getUuid().equals(localModel.getUuid()))
			{
				result.add(localModel);
			}

			if (trustedHosts != null && trustedHosts.length > 0) {
				for (TrustedHost host : trustedHosts) {
					TrustHostModel model = ConvertToModel(host);
					if (model.getHostName().equalsIgnoreCase(
							selected.getHostName())) {
						model.setSelected(true);
					} else {
						model.setSelected(false);
					}
					if(!model.getUuid().equals(localModel.getUuid())&&
						!model.getUuid().equals(selected.getUuid()))
					{
						result.add(model);
					}
				}
			}
			return result.toArray(new TrustHostModel[0]);
		} catch (WebServiceException exception) {
			proccessAxisFaultException(exception);
		}
		return null;
	}

	private TrustHostModel ConvertToModel(TrustedHost host) {
		TrustHostModel model = new TrustHostModel();
		model.setHostName(host.getName());
		model.setPassword(host.getPassword());
		model.setPort(host.getPort());
		model.setType(host.getType());
		model.setUser(host.getUserName());
		model.setUuid(host.getUuid());
		model.setProtocol(host.getProtocol());
		model.setSelected(false);
		model.setD2DVersion(host.getD2dVersion());
		return model;
	}

	private DestinationCapacityModel convertDestinationInformation(DestinationCapacity source){
		if(source == null){
			return null;
		}
		DestinationCapacityModel model = new DestinationCapacityModel();
		model.setCatalogSize(source.getCatalogSize());
		model.setFullBackupSize(source.getFullBackupSize());
		model.setIncrementalBackupSize(source.getIncrementalBackupSize());
		model.setResyncBackupSize(source.getResyncBackupSize());
		model.setTotalFreeSize(source.getTotalFreeSize());
		model.setTotalVolumeSize(source.getTotalVolumeSize());
		return model;
	}

	private BackupConfiguration convertToBackupConfiguration(BackupSettingsModel model){
		if(model == null){
			return null;
		}
		BackupConfiguration bc = new BackupConfiguration();
		bc.setDestination(model.getDestination());
		bc.setUserName(model.getDestUserName());
		bc.setPassword(model.getDestPassword());
		return bc;
	}

	@Override
	public Boolean becomeTrustHost(TrustHostModel trustHostModel)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {

		WebServiceClientProxy client = null;
		try {
			String serviceID = ServiceInfoConstants.SERVICE_ID_D2D_PROPER;
			ServiceInfoList serviceInfoList = null;
			boolean oldD2D = false;
			try{
			serviceInfoList = WebServiceFactory.getServiceInfoList(trustHostModel.getProtocol(),trustHostModel.getHostName(),
					trustHostModel.getPort());

			}catch(WebServiceException e){
				if(e.getMessage().equals(FlashServiceErrorCode.Common_Service_FAIL_TO_GETLIST)){
					//we think it is old D2D
					client = WebServiceFactory.getFlassService(trustHostModel.getProtocol(),trustHostModel.getHostName(),
							trustHostModel.getPort(),serviceID);
					oldD2D = true;
				}else{
					throw e;
				}
			}
			if(!oldD2D){
				serviceID = ServiceInfoConstants.SERVICE_ID_D2D_R16_5;
				ServiceInfo featureServiceInfo = WebServiceFactory.getFeatureServiceInfo(serviceID, serviceInfoList);
				if(featureServiceInfo == null){
					//It is the D2D R16 GM web service
					serviceID = ServiceInfoConstants.SERVICE_ID_D2D_V2;
					featureServiceInfo = WebServiceFactory.getFeatureServiceInfo(serviceID, serviceInfoList);
				}
				
				client = WebServiceFactory.getFlassService(trustHostModel.getProtocol(),trustHostModel.getHostName(),
						trustHostModel.getPort(),serviceID,featureServiceInfo);

			}

			client.getService().validateUserByUUID(trustHostModel.getUuid());
			this.setServiceClient(client);
			
			//clear the selected monitee caches for Virtual Standby monitee in case user 
			//configures Virtual standby setting
			setCurrentMonitee(null);
			setMoniteeServiceClient(null);
			
			BackupInformationSummary backupInformationSummary = client.getService().getBackupInformationSummary();

			System.out.print(backupInformationSummary);
		} catch (WebServiceException ex) {
			if (ex.getCause() instanceof ProtocolException
					|| ex.getCause() instanceof ConnectException
					|| ex.getCause() instanceof SocketException
					|| ex.getCause() instanceof SSLException // add for that it cannot connect server when we change protocol, for issue 20015152
					|| ex.getCause() instanceof UnknownHostException) {
				logger.debug("Exception:" + ex.getCause());
				throw generateException(FlashServiceErrorCode.Common_CantConnectRemoteServer);
			}else{
				proccessAxisFaultException(ex);
			}
		}

		String domain = "";
		String user = trustHostModel.getUser();
		if (user != null && user.trim().length() > 0) {
			int indx = user.indexOf('\\');
			if (indx > 0) {
				domain = user.substring(0, indx);
				user = user.substring(indx + 1);
			}
		}

		this.getThreadLocalRequest().getSession(true).setAttribute(
				SessionConstants.SRING_DOMAIN, domain);
		this.getThreadLocalRequest().getSession(true).setAttribute(
				SessionConstants.SRING_USERNAME, user);
		this.getThreadLocalRequest().getSession(true).setAttribute(
				SessionConstants.SRING_PASSWORD, trustHostModel.getPassword());
		this.getThreadLocalRequest().getSession(true).setAttribute(
				SessionConstants.SRING_UUID, trustHostModel.getUuid());
		this.getThreadLocalRequest().getSession(true).setAttribute(
				SessionConstants.SRING_SELTRUSTHOST, trustHostModel);

		return Boolean.TRUE;
	}

	@Override
	public Boolean checkBaseLicense() throws BusinessLogicException, ServiceConnectException, ServiceInternalException{
		try
		{
			return this.getServiceClient().getService().checkBaseLicence();
		}catch(WebServiceException exception){
			proccessAxisFaultException(exception);
		}

		return null;
	}

	@Override
	public TrustHostModel getLocalHost() throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		// TODO Auto-generated method stub
		try {
			TrustedHost local = this.getServiceClient().getService()
			.getLocalHostAsTrust();
			TrustHostModel localModel = ConvertToModel(local);
			return localModel;
		} catch (WebServiceException e) {
			logger.error(e);
		}
		return null;
	}

	@Override
	public int PMInstallPatch(PatchInfoModel in_PatchinfoModel)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException
	{
		int iInstallstatus = 0;
		try {
			iInstallstatus = this.getServiceClient().getServiceV2().installUpdate();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return iInstallstatus;
	}
	
	//added by cliicy.luo
	@Override
	public int PMInstallBIPatch(PatchInfoModel in_PatchinfoModel)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException
	{
		int iInstallstatus = 0;
		try {
			iInstallstatus = this.getServiceClient().getServiceV2().installBIUpdate();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return iInstallstatus;
	}
	//added by cliicy.luo

	private boolean ConvertPatchInfoModelToPatchInfoObject(PatchInfoModel in_PatchinfoModel,PatchInfo in_pInstallPatchInfo)
	{
		boolean bConverted = false;

		//product information
		in_pInstallPatchInfo.setMajorversion(in_PatchinfoModel.getMajorversion());
		in_pInstallPatchInfo.setMinorVersion(in_PatchinfoModel.getMinorVersion());
		in_pInstallPatchInfo.setServicePack(in_PatchinfoModel.getServicePack());

		//patch information
		in_pInstallPatchInfo.setPackageID(in_PatchinfoModel.getPackageID());
		in_pInstallPatchInfo.setPublishedDate(in_PatchinfoModel.getPublishedDate());
		in_pInstallPatchInfo.setDescription(in_PatchinfoModel.getDescription());
		in_pInstallPatchInfo.setPatchDownloadLocation(in_PatchinfoModel.getPatchDownloadLocation());
		in_pInstallPatchInfo.setPatchURL(in_PatchinfoModel.getPatchURL());
		in_pInstallPatchInfo.setRebootRequired(in_PatchinfoModel.getRebootRequired());
		in_pInstallPatchInfo.setSize(in_PatchinfoModel.getSize());
		in_pInstallPatchInfo.setPatchVersionNumber(in_PatchinfoModel.getPatchVersionNumber());
		in_pInstallPatchInfo.setAvailableStatus(in_PatchinfoModel.getAvailableStatus());
		in_pInstallPatchInfo.setDownloadStatus(in_PatchinfoModel.getDownloadStatus());
		in_pInstallPatchInfo.setInstallStatus(in_PatchinfoModel.getInstallStatus());
		in_pInstallPatchInfo.setErrorMessage(in_PatchinfoModel.getErrorMessage());
		in_pInstallPatchInfo.setError_Status(in_PatchinfoModel.getError_Status());

		return bConverted;
	}

	public VirtualCenterNodeModel ConvertToModel(ESXServer server,VirtualCenterNodeModel parent){
		VirtualCenterNodeModel model = new VirtualCenterNodeModel();
		model.set("name", server.getEsxName());
		model.set("type", 2);
		model.set("dcName", server.getDataCenter());
		model.setParent(parent);
		return model;
	}
	
	public VirtualCenterNodeModel ConvertToModel(VirtualMachine vm){
		VirtualCenterNodeModel model = new VirtualCenterNodeModel();
		model.set("name", vm.getVmName());
		model.set("type", 3);
		model.set("uuid", vm.getVmUUID());
		model.set("vmInstanceUUID", vm.getVmInstanceUUID());
		model.set("state", vm.getState());
		model.set("showmenu", false);
		return model;
	}

	@Override
	public BackupInformationSummaryModel getVMBackupInforamtionSummary(
			BackupVMModel vmModel) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		try
		{
			if(vmModel == null){
				return null;
			}
			BackupInformationSummary summary = getServiceClient().getServiceV2().getVMBackupInformationSummary(ConvertToVirtualMachine(vmModel));
			return convertBackupInformationSummary(summary);
		}catch(WebServiceException exception){
			proccessAxisFaultException(exception);
		}
		
		return null;
	}
	
	public VirtualMachine ConvertToVirtualMachine(BackupVMModel vmModel){
		VirtualMachine vm = new VirtualMachine();
		vm.setVmHostName(vmModel.getVmHostName());
		vm.setVmName(vmModel.getVMName());
		vm.setVmUUID(vmModel.getUUID());
		vm.setVmInstanceUUID(vmModel.getVmInstanceUUID());
		return vm;
	}

	@Override
	public BackupInformationSummaryModel getVMBackupInforamtionSummaryWithLicInfo(
			BackupVMModel vmModel) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		try
		{
			if(vmModel == null){
				return null;
			}
			BackupInformationSummary summary = getServiceClient().getServiceV2().getVMBackupInformationSummaryWithLicInfo(ConvertToVirtualMachine(vmModel));
			return convertBackupInformationSummary(summary);
		}catch(WebServiceException exception){
			proccessAxisFaultException(exception);
		}
		
		return null;
	}

	@Override
	public ProtectionInformationModel[] getVMProtectionInformation(
			BackupVMModel vmModel) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		try
		{
			if(vmModel == null){
				return null;
			}
			ProtectionInformation[] result = getServiceClient().getServiceV2().getVMProtectionInformation(ConvertToVirtualMachine(vmModel));
			return convertProtectionInformation(result);
		}catch(WebServiceException exception){
			proccessAxisFaultException(exception);
		}
		
		return null;
	}

	@Override
	public NextScheduleEventModel getVMNextScheduleEvent(BackupVMModel vmModel)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		try
		{
			if(vmModel == null){
				return null;
			}
			NextScheduleEvent nextEvent = this.getServiceClient().getServiceV2().getVMNextScheduleEvent(ConvertToVirtualMachine(vmModel));
			return convertNextScheduleEvent(nextEvent);
		}catch(WebServiceException exception){
			proccessAxisFaultException(exception);
		}
		
		return null;
	}
	
	@Override
	public RecoveryPointModel[] getVMRecentBackups(int backupType, int backupStatus,int top,BackupVMModel vmModel)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		try
		{
			if(vmModel ==null ){
				return null;
			}
			RecoveryPoint[] recoveryPoints = getServiceClient().getServiceV2().getVMMostRecentRecoveryPoints(backupType, backupStatus,top,ConvertToVirtualMachine(vmModel));
			if (recoveryPoints!=null && recoveryPoints.length>0){
				List<RecoveryPointModel> result = new LinkedList<RecoveryPointModel>();
				for(RecoveryPoint point : recoveryPoints){
					result.add(ConvertToModel(point));
				}
				
				return result.toArray(new RecoveryPointModel[0]);
			}else
				return null;
		}catch(WebServiceException exception){
			proccessAxisFaultException(exception);
		}
		
		return null;
	}

	@Override
	public ArchiveJobInfoModel getArchiveInfoSummary()
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		try {
			ArchiveJobInfo arch = getServiceClient().getServiceV2().getArchiveSummaryInfo();
			return convertArchiveInfoModel(arch);
		}catch(WebServiceException wse) {
			proccessAxisFaultException(wse);
		}
		return null;
	}

	@Override
	public LicInfoModel getLicInfo() throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		try {
			LicInfo lic = getServiceClient().getServiceV2().getLicenseInfo();
			return convetLicInfoModel(lic);
		}catch(WebServiceException wse) {
			proccessAxisFaultException(wse);
		}
		return null;
	}

	@Override
	public RecoveryPointModel[] getVMRecentBackupsByServerTime(int backupType,
			int backupStatus, String serverBeginDate, String serverEndDate,
			boolean needCatalogStatus, BackupVMModel vmModel) throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		try
		{
			RecoveryPoint[] recoveryPoints = getServiceClient().getServiceV2().getVMRecentBackupsByServerTime(backupType, backupStatus, serverBeginDate, serverEndDate, needCatalogStatus,ConvertToVirtualMachine(vmModel));
			if (recoveryPoints!=null && recoveryPoints.length>0){
				List<RecoveryPointModel> result = new LinkedList<RecoveryPointModel>();
				for(RecoveryPoint point : recoveryPoints){
					result.add(ConvertToModel(point));
				}

				return result.toArray(new RecoveryPointModel[0]);
			}else
				return null;
		}catch(WebServiceException exception){
			proccessAxisFaultException(exception);
		}

		return null;
	}

	@Override
	public BackupVMModel[] getConfiguredVM() throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		try
		{
			VMItem[] vmList = getServiceClient().getServiceV2().getConfiguredVM();
			if(vmList !=null && vmList.length>0){
				List<BackupVMModel> vmModelList = new LinkedList<BackupVMModel>();
				for(VMItem item : vmList){
					vmModelList.add(convertToBackupModel(item));
				}
				return vmModelList.toArray(new BackupVMModel[0]);
			}else
				return null;
		}catch(WebServiceException exception){
			proccessAxisFaultException(exception);
		}
		return null;
	}
	
	private BackupVMModel convertToBackupModel(VMItem vmItem){
		if(vmItem == null)
			return null;
		BackupVMModel vmModel = new BackupVMModel();
		vmModel.setVmInstanceUUID(vmItem.getVmInstanceUUID());
		vmModel.setVmHostName(vmItem.getVmHostName());
		vmModel.setVMName(vmItem.getVmName());
		vmModel.setVMType(vmItem.getVmType());
		if(vmItem.getVmItems() != null && vmItem.getVmItems().length > 0) {
			for(int i = 0; i < vmItem.getVmItems().length; ++i) {
				BackupVMModel model = new BackupVMModel();
				model.setVmInstanceUUID(vmItem.getVmItems()[i].getVmInstanceUUID());
				model.setVmHostName(vmItem.getVmItems()[i].getVmHostName());
				model.setVMName(vmItem.getVmItems()[i].getVmName());
				model.setVMType(vmItem.getVmItems()[i].getVmType());
				vmModel.memberVMList.add(model);
			}
		}
		return vmModel;
	}

	@Override
	public VMStatusModel[] getVMStatusModel(BackupVMModel vmModel)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		try
		{
			VMStatus[] warnings = getServiceClient().getServiceV2().getVMStatus(ConvertToVirtualMachine(vmModel));
			if(warnings !=null && warnings.length>0){
				List<VMStatusModel> warningList = new LinkedList<VMStatusModel>();
				for(VMStatus warning: warnings){
					warningList.add(convertToVMStatusModel(warning));
				}
				return warningList.toArray(new VMStatusModel[0]);
			}else
				return null;
		}catch(WebServiceException exception){
			if (exception.getCause()!=null &&  exception.getCause() instanceof SocketTimeoutException) {
				if ((vmModel.getVMType() == BackupVM.Type.VMware.ordinal()) ||
					(vmModel.getVMType() == BackupVM.Type.VMware_VApp.ordinal())){
					throw generateException(FlashServiceErrorCode.VSPHERE_VC_NOT_RESPONSE);
				}
				else {
					throw generateException(FlashServiceErrorCode.VSPHERE_HYPERV_NOT_RESPONSE);
				}
			}else{
				proccessAxisFaultException(exception);
			}
		}
		return null;
	}
	
	private VMStatusModel convertToVMStatusModel(VMStatus status){
		if(status == null)
			return null;
		
		VMStatusModel statusModel = new VMStatusModel();
		statusModel.setStatus(status.getStatus());
		statusModel.setStatusType(status.getStatusType());
		statusModel.setSubType(status.getSubType());
		statusModel.setStatusParameter(status.getParameters());
		return statusModel;
	}

	
	@Override
	public MergeJobMonitorModel getMergeJobMonitor(String vmInstanceUUID)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		WebServiceClientProxy client = null;
		try {
			client = this.getServiceClient();
			if(client != null) {
				MergeJobMonitor jm = null;
				if(vmInstanceUUID == null || vmInstanceUUID.isEmpty())
					jm = client.getFlashService(IFlashService_R16_U7.class)
						.getMergeJobMonitor();
				else 
					jm = client.getFlashService(IFlashService_R16_U7.class)
						.getVMMergeJobMonitor(vmInstanceUUID);
				return ConvertDataToModel.convertToMergeJobMonitorModel(jm);
			}
			
		}catch(WebServiceException e) {
			proccessAxisFaultException(e);
		}
		
		return null;  
	}
	
	@Override
	public int pauseMerge(String vmInstanceUUID) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		WebServiceClientProxy client = null;
		try {
			client = this.getServiceClient();
			if(client != null) {
				if(vmInstanceUUID == null || vmInstanceUUID.isEmpty())
					client.getFlashService(IFlashService_R16_U7.class).pauseMergeEx(MergeAPISource.MANUALLY);
				else 
					client.getFlashService(IFlashService_R16_U7.class).pauseVMMergeEx(
							MergeAPISource.MANUALLY, 
							vmInstanceUUID);
			}
		}catch(WebServiceException e) {
			proccessAxisFaultException(e);
		}
		return 0;
	}

	@Override
	public int resumeMerge(String vmInstanceUUID)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		WebServiceClientProxy client = null;
		try {
			client = this.getServiceClient();
			if(client != null) {
				if(vmInstanceUUID == null || vmInstanceUUID.isEmpty())
					client.getFlashService(IFlashService_R16_U7.class).resumeMergeEx(MergeAPISource.MANUALLY);
				else 
					client.getFlashService(IFlashService_R16_U7.class).resumeVMMergeEx(
							MergeAPISource.MANUALLY, vmInstanceUUID);
			}
		}catch(WebServiceException e) {
			proccessAxisFaultException(e);
		}
		return 0;
	}

	@Override
    public MergeStatusModel getMergeStatus(String vmInstanceUUID)
            throws BusinessLogicException, ServiceConnectException,
            ServiceInternalException {
		WebServiceClientProxy client = null;
		try {
			client = this.getServiceClient();
			MergeStatus status = null;
			if(client != null) {
				if(vmInstanceUUID == null || vmInstanceUUID.isEmpty())
					status = client.getFlashService(IFlashService_R16_U7.class).getMergeJobStatus();
				else 
					status = client.getFlashService(IFlashService_R16_U7.class).getVMMergeJobStatus(vmInstanceUUID);
				return ConvertDataToModel.mergeStatus2MergeStatusModel(status);
			}
			
		}catch(WebServiceException e) {
			proccessAxisFaultException(e);
		}
		return new MergeStatusModel();
    }

	@Override
	public ArrayList<BackupSetInfoModel> getBackupSetInfo(String vmInstanceUUID)
			throws BusinessLogicException, ServiceConnectException,
			ServiceInternalException {
		WebServiceClientProxy client = null;
		ArrayList<BackupSetInfoModel> modelSets = new ArrayList<BackupSetInfoModel>();
		try {
			client = this.getServiceClient();
			List<BackupSetInfo> sets = null;
			if(client != null) {
				if(vmInstanceUUID == null || vmInstanceUUID.isEmpty())
					sets = client.getFlashService(IFlashService_R16_U7.class).getBackupSetInfo();
				else 
					sets = client.getFlashService(IFlashService_R16_U7.class).getVMBackupSetInfo(vmInstanceUUID);
				if(sets == null)
					return null;
				else {
					for(BackupSetInfo info : sets) {
						modelSets.add(this.backupSetInfoToBackupSetInfoModel(info));
					}
				}
			}
			
		}catch(WebServiceException e) {
			proccessAxisFaultException(e);
		}
		return modelSets;
	}
	
	private BackupSetInfoModel backupSetInfoToBackupSetInfoModel(BackupSetInfo info) {
		BackupSetInfoModel model = new BackupSetInfoModel();
		model.startRecoveryPoint = ConvertToModel(info.getStartRecoveryPoint());
		if(info.getEndRecoveryPoint() != null)
			model.endRecoveryPoint = ConvertToModel(info.getEndRecoveryPoint());
		model.setCount(info.getRecoveryPointCount());
		model.setTotalSize(info.getTotalSize());
		return model;
	}

	private DataStoreInfoModel convertToModel(RPSDataStoreInfo info) {
		DataStoreInfoModel model = new DataStoreInfoModel();
		model.setDataStorePath(info.getDataStorePath());
		model.setTotalSize(info.getTotalSize());
		model.setDirSize(info.getDirSize());
		model.setFreeSize(info.getFreeSize());
		model.setDedupe(info.isDedupe());
		if (model.isDedupe()) {
			model.setIndexPath(info.getIndexPath());
			model.setIndexTotalSize(info.getIndexTotalSize());
			model.setIndexDirSize(info.getIndexDirSize());
			model.setIndexFreeSize(info.getIndexFreeSize());
			model.setDataPath(info.getDataPath());
			model.setDataTotalSize(info.getDataTotalSize());
			model.setDataDirSize(info.getDataDirSize());
			model.setDataFreeSize(info.getDataFreeSize());
			model.setHashPath(info.getHashPath());
			model.setHashTotalSize(info.getHashTotalSize());
			model.setHashDirSize(info.getHashDirSize());
			model.setHashFreeSize(info.getHashFreeSize());
		}
		
		return model;
	}
	
	@Override
	public DataStoreInfoModel getDataStoreStatus(String dataStoreUUID) throws BusinessLogicException,
	    ServiceConnectException, ServiceInternalException {
		// TODO Auto-generated method stub
		WebServiceClientProxy client = null;
		DataStoreInfoModel model = null;
		try {
			client = this.getServiceClient();
			if(client != null) {
				model = convertToModel(client.getFlashService(IFlashService4RPS.class).getDataStoreInformation(dataStoreUUID));		
			}			
		}catch(WebServiceException e) {
			proccessAxisFaultException(e);
		}
		return model;
	}

	@Override
	public DataStoreInfoModel getVMDataStoreStatus(BackupVMModel vm, String dataStoreUUID) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		if(vm == null){
			return null;
		}
		
		WebServiceClientProxy client = null;
		DataStoreInfoModel model = null;
		try {
			client = this.getServiceClient();
			if(client != null) {
				model = convertToModel(client.getFlashServiceR16_5().getVMDataStoreInformation(ConvertToVirtualMachine(vm), dataStoreUUID));		
			}			
		}catch(WebServiceException e) {
			proccessAxisFaultException(e);
		}
		return model;
	}
	
	//added by cliicy.luo for debug test
/*		public static BIPatchInfo getBIPatchInfo(String StatusXmlFilePath)
		{
			BIPatchInfo biPInfo = new BIPatchInfo();
			File file = new File(StatusXmlFilePath);
			if (!file.exists())
			{
				biPInfo.setError_Status(PatchInfo.ERROR_NONEW_PATCHES_AVAILABLE);
				return biPInfo;
			}
			try 
			{
			    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			    Document doc = docBuilder.parse (new File(StatusXmlFilePath));

			    // normalize text representation
			    doc.getDocumentElement().normalize();
			    //System.out.println ("Root element of the Status.xml is " + doc.getDocumentElement().getNodeName());
			    NodeList listOfPkgs = null;
			    int iPnode = 0 , itotalPkgs = 0;
			    String sPackageFlag;
			    //get the total number of Packagexx. like Package0 Package1 Package2
			    do {
			    	sPackageFlag = "Package" + Integer.toString(iPnode);
			    	listOfPkgs = doc.getElementsByTagName(sPackageFlag);
			    	itotalPkgs = listOfPkgs.getLength();
			    	if ( itotalPkgs == 0 ) break;
			    	iPnode++;
			    } while (true);
			    //get the total number of Packagexx. like Package0 Package1 Package2
			    biPInfo.aryPatchInfo = new PatchInfo[iPnode] ;
			    iPnode = 0;
			    
			    do {
			    	sPackageFlag = "Package" + Integer.toString(iPnode);
			    	listOfPkgs = doc.getElementsByTagName(sPackageFlag);
			    	itotalPkgs = listOfPkgs.getLength();    		
			    	if ( itotalPkgs == 0 ) break;   	
			    	
					for (int i = 0; i < itotalPkgs ; i++) {
						if ( i >= 1 ) break;//make sure the Package name is not the same, is different. 
						Node firstPatchNode = listOfPkgs.item(i);
						if (firstPatchNode.getNodeType() == Node.ELEMENT_NODE) {
							i = iPnode;
							biPInfo.aryPatchInfo[i] = new PatchInfo();
							// ------- get update ID name
							Element firstElement = (Element) firstPatchNode;
							String sPID = firstElement.getAttribute("Id");
							biPInfo.aryPatchInfo[i].setPackageID(sPID);
		
							// ------- get update PublishedDate
							String sPdate = firstElement.getAttribute("PublishedDate");
							biPInfo.aryPatchInfo[i].setPublishedDate(sPdate.toString());
		
							// System.out.println("Id : " + sPID);
							// System.out.println("PublishedDate : " + sPdate);
		
							// ------- get update patch name
							NodeList firstNameList = firstElement.getElementsByTagName("Update");
							Element firstNameElement = (Element) firstNameList.item(0);
							NodeList textFNList = firstNameElement.getChildNodes();
							String pckname = ((Node) textFNList.item(0)).getNodeValue().trim();
							biPInfo.aryPatchInfo[i].setPackageUpdateName(pckname);
							// System.out.println("Update Patch Name : " + pckname);
		
							// ------- get Dependency update patch name
							NodeList dyNameList = firstElement.getElementsByTagName("Dependency");
							Element dyNameElement = (Element) dyNameList.item(0);
							NodeList dyNList = dyNameElement.getChildNodes();
							String dyname = ((Node) dyNList.item(0)).getNodeValue().trim();
							biPInfo.aryPatchInfo[i].setPackageDepy(dyname);
							
							// ------- get Size
							NodeList secNameList = firstElement.getElementsByTagName("Size");
							Element secNameElement = (Element) secNameList.item(0);
							NodeList textSNList = secNameElement.getChildNodes();
							String ssize = ((Node) textSNList.item(0)).getNodeValue().trim();
							int nsize = Integer.parseInt(ssize.toString());
							biPInfo.aryPatchInfo[i].setSize(nsize);
							// System.out.println("Size of Patch : " + ssize);
		
							// ------- get Checksum
							NodeList chsNameList = firstElement
									.getElementsByTagName("Checksum");
							Element chsNameElement = (Element) chsNameList.item(0);
							NodeList chsNList = chsNameElement.getChildNodes();
							String schs = ((Node) chsNList.item(0)).getNodeValue().trim().toString();		
							// System.out.println("Checksum of Patch : " + schs);
		
							// ------- get Downloadedlocation
							NodeList dlocNameList = firstElement.getElementsByTagName("Downloadedlocation");
							Element dlocNameElement = (Element) dlocNameList.item(0);
		
							NodeList dlocNList = dlocNameElement.getChildNodes();
							String sloc = ((Node) dlocNList.item(0)).getNodeValue().trim();
							biPInfo.aryPatchInfo[i].setPatchDownloadLocation(sloc.toString());
							System.out.println("Downloadedlocation of Patch : " +sloc);
		
							// ------- get DownloadStatus
							NodeList dlsNameList = firstElement.getElementsByTagName("DownloadStatus");
							Element dlsNameElement = (Element) dlsNameList.item(0);
		
							NodeList dlsNList = dlsNameElement.getChildNodes();
							String sls = ((Node) dlsNList.item(0)).getNodeValue().trim();
							biPInfo.aryPatchInfo[i].setDownloadStatus(Integer.parseInt(sls.toString()));
							biPInfo.setDownloadStatus(Integer.parseInt(sls.toString()));
		
							if (biPInfo.aryPatchInfo[i].getDownloadStatus() == 1) {
								File downloadFile = new File(sloc.toString());
								if (!downloadFile.exists()) {
									biPInfo.aryPatchInfo[i].setDownloadStatus(0);
									biPInfo.setDownloadStatus(0);
								} else {
									biPInfo.aryPatchInfo[i].setDownloadStatus(1);
									biPInfo.setDownloadStatus(1);
								}
							}
							// System.out.println("DownloadStatus of Patch : " + sls);
		
							// ------- get AvailableStatus
							NodeList avsNameList = firstElement
									.getElementsByTagName("AvailableStatus");
							Element avsNameElement = (Element) avsNameList.item(0);
		
							NodeList avsNList = avsNameElement.getChildNodes();
							String savs = ((Node) avsNList.item(0)).getNodeValue().trim();
							biPInfo.aryPatchInfo[i].setAvailableStatus(Integer.parseInt(savs.toString()));
							biPInfo.setAvailableStatus(Integer.parseInt(savs.toString()));
							// System.out.println("AvailableStatus of Patch : " + savs);
		
							// ------- get UpdateBuild
							NodeList upbNameList = firstElement.getElementsByTagName("UpdateBuild");
							Element upbNameElement = (Element) upbNameList.item(0);
		
							NodeList upbNList = upbNameElement.getChildNodes();
							String supb = ((Node) upbNList.item(0)).getNodeValue().trim();
							String[] supbx = supb.toString().split("\\.");
							biPInfo.aryPatchInfo[i].setBuildNumber(Integer.parseInt(supbx[0]));
							biPInfo.setBuildNumber(Integer.parseInt(supbx[0]));
							// System.out.println("UpdateBuild of Patch : " + supb);
		
							// ------- get UpdateVersionNumber
							NodeList upvNameList = firstElement
									.getElementsByTagName("UpdateVersionNumber");
							Element upvNameElement = (Element) upvNameList.item(0);
		
							NodeList upvNList = upvNameElement.getChildNodes();
							String supv = ((Node) upvNList.item(0)).getNodeValue()
									.trim();
							biPInfo.aryPatchInfo[i].setPatchVersionNumber(Integer
									.parseInt(supv.toString()));
							// System.out.println("UpdateVersionNumber of Patch : " +
							// supv);
		
							// ------- get RebootRequired
							NodeList rbNameList = firstElement
									.getElementsByTagName("RebootRequired");
							Element rbNameElement = (Element) rbNameList.item(0);
		
							NodeList rbNList = rbNameElement.getChildNodes();
							String srb = ((Node) rbNList.item(0)).getNodeValue().trim();
							biPInfo.aryPatchInfo[i].setRebootRequired(Integer
									.parseInt(srb.toString()));
							// System.out.println("RebootRequired of Patch : " + srb);
		
							// ------- get LastRebootableUpdateVersion
							NodeList lrbNameList = firstElement
									.getElementsByTagName("LastRebootableUpdateVersion");
							Element lrbNameElement = (Element) lrbNameList.item(0);
		
							NodeList lrbNList = lrbNameElement.getChildNodes();
							// System.out.println("LastRebootableUpdateVersion of Patch : "
							// + ((Node)lrbNList.item(0)).getNodeValue().trim());
		
							// ------- get RequiredVersionOfAutoUpdate
							NodeList vaupNameList = firstElement
									.getElementsByTagName("RequiredVersionOfAutoUpdate");
							Element vaupNameElement = (Element) vaupNameList.item(0);
		
							NodeList vaupNList = vaupNameElement.getChildNodes();
							// System.out.println("RequiredVersionOfAutoUpdate of Patch : "
							// + ((Node)vaupNList.item(0)).getNodeValue().trim());
		
							// ------- get InstallStatus
							NodeList itsNameList = firstElement
									.getElementsByTagName("InstallStatus");
							Element itsNameElement = (Element) itsNameList.item(0);
		
							NodeList itsNList = itsNameElement.getChildNodes();
							String sits = ((Node) itsNList.item(0)).getNodeValue().trim();
							biPInfo.aryPatchInfo[i].setInstallStatus(Integer.parseInt(sits.toString()));
							biPInfo.setInstallStatus(Integer.parseInt(sits.toString()));
							// System.out.println("InstallStatus of Patch : " + sits);
		
							// ------- get Desc
							String sdec = "Desc";
							NodeList desNameList = firstElement.getElementsByTagName(sdec);
							Element desNameElement = (Element) desNameList.item(0);
		
							NodeList desNList = desNameElement.getChildNodes();
							String sdes = ((Node) desNList.item(0)).getNodeValue().trim();
							biPInfo.aryPatchInfo[i].setDescription(sdes.toString());
							// System.out.println("Desc of Patch : " + sdes);
							//
							// ------- get UpdateURL
							String surl = "UpdateURL" ;
							NodeList urlNameList = firstElement.getElementsByTagName(surl);
							Element urlNameElement = (Element) urlNameList.item(0);
		
							NodeList urlNList = urlNameElement.getChildNodes();
							String surlV = ((Node) urlNList.item(0)).getNodeValue().trim();
							biPInfo.aryPatchInfo[i].setPatchURL(surlV.toString());
							// System.out.println("UpdateURL of Patch : " + surlV);
		
							// Error Message
							NodeList errorMNameList = firstElement.getElementsByTagName("ErrorMessage");
							biPInfo.setError_Status(PatchInfo.ERROR_GET_PATCH_INFO_SUCCESS);
							if (errorMNameList.item(0) != null) {
								Element errorMElement = (Element) errorMNameList.item(0);
		
								NodeList errorMNList = errorMElement.getChildNodes();
								String serrorV = ((Node) errorMNList.item(0)).getNodeValue().trim();
								biPInfo.aryPatchInfo[i].setErrorMessage(serrorV.toString());
								biPInfo.setErrorMessage(serrorV.toString());
		
								if (serrorV.toString().length() > 0) {
									biPInfo.setError_Status(PatchInfo.ERROR_GET_PATCH_INFO_FAIL);
								} else {
									biPInfo.setError_Status(PatchInfo.ERROR_GET_PATCH_INFO_SUCCESS);
								}
							}
						}
					}//end of for loop with s var
					iPnode++;
			    }while ( true );
			    return biPInfo;
			} catch (Exception err) {
				err.printStackTrace ();
				biPInfo.setError_Status(PatchInfo.ERROR_GET_PATCH_INFO_FAIL);
			}
			biPInfo.setError_Status(PatchInfo.ERROR_GET_PATCH_INFO_FAIL);
		    return biPInfo;
		}*/
		//added by cliicy.luo for debug test
}

