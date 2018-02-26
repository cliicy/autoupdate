package com.ca.arcflash.webservice.edge.datasync;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.archive.ArchiveJobInfo;
import com.ca.arcflash.webservice.data.archive.ArchiveScheduleStatus;
import com.ca.arcflash.webservice.data.archive.JArchiveJob;
import com.ca.arcflash.webservice.data.edge.datasync.archive.ArchiveJob;
import com.ca.arcflash.webservice.data.edge.datasync.archive.ArchiveJobList;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.d2dreg.D2DEdgeRegistration;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.ArchiveService;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.toedge.IEdgeCM4D2D;
import com.ca.arcflash.webservice.toedge.IEdgeD2DService;
import com.ca.arcflash.webservice.toedge.WebServiceFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;

public class ArchiveSyncer extends BaseDataSyncer {
	private static final Logger logger = Logger.getLogger(ArchiveSyncer.class);

	public ArchiveSyncer(){
		FullSyncFinishMarkName = "archiveJobFullSyncFinished";
	}

	private String  marshXML2String(ArchiveJobList jobList) {
		StringWriter sw = new StringWriter();
		String xmlString = "";

		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance("com.ca.arcflash.webservice.data.edge.datasync.archive");
			Marshaller marsh = jaxbContext.createMarshaller();
			marsh.marshal(jobList, sw);
		} catch (Exception e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
			return null;
		}

		xmlString = sw.toString();
		try {
			sw.close();
		} catch (IOException e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());;
		}

		return xmlString;
	}

	private String marshall(List<ArchiveJobInfo> archiveJobs){
		String xmlContent = "";
		List<ArchiveJob> jobList = new ArrayList<ArchiveJob>();

		for(ArchiveJobInfo archiveJobInfo : archiveJobs) {
			ArchiveJob archiveJob = new ArchiveJob();
			
			archiveJob.SetOperation("ADD");

			long archiveDataSize = Long.parseLong(archiveJobInfo.getArchiveDataSize());
			archiveJob.SetArchiveDataSize(archiveDataSize);

			long copyDataSize = Long.parseLong(archiveJobInfo.getCopyDataSize());
			archiveJob.SetCopyDataSize(copyDataSize);

			archiveJob.SetDay(archiveJobInfo.getDay());
			archiveJob.SetHour(archiveJobInfo.getHour());
			archiveJob.SetMinute(archiveJobInfo.getMin());
			archiveJob.SetMonth(archiveJobInfo.getMonth());
			archiveJob.SetSecond(archiveJobInfo.getSec());
			archiveJob.SetYear(archiveJobInfo.getYear());
			archiveJob.SetStatus(archiveJobInfo.getarchiveJobStatus());
			archiveJob.SetDestPath(archiveJobInfo.getArchiveDestinationPath()==null?"":
				archiveJobInfo.getArchiveDestinationPath());
			archiveJob.SetDestType(archiveJobInfo.getArchiveDestinationType());
			
			archiveJob.SetCompressionFlag((int)archiveJobInfo.getCompression());

			int Id = 0;
			try {
				Id = Integer.valueOf(archiveJobInfo.getbackupSessionId()).intValue();
			}catch (Exception e) {
				logger.error(e.getMessage() == null ? e : e.getMessage());
				Id = 0;
			}
			archiveJob.SetId(Id);
			
			archiveJob.SetArchiveJobID((int)archiveJobInfo.getarchiveJobId());

			long lEncryptionStatus = archiveJobInfo.getEncryptionStatus();
			archiveJob.SetIsEncrypted((int)lEncryptionStatus);
			
/*			boolean IsEncrypted = archiveJobs[i].getIsEncrypted();
			if(IsEncrypted)
				archiveJob.SetIsEncrypted(1);
			else
				archiveJob.SetIsEncrypted(0);*/

			archiveJob.SetJobMethod((int)archiveJobInfo.getJobMethod());
			archiveJob.SetPath(archiveJobInfo.getbackupSessionPath());

			archiveJob.SetScheduleCount((int)archiveJobInfo.getscheduleCount()); //TBD

			jobList.add(archiveJob);
		}

		if(archiveJobs.size() > 0) {
			ArchiveJobList archiveJobList = new ArchiveJobList();
			archiveJobList.setArchiveJob(jobList);
			xmlContent = marshXML2String(archiveJobList);
		}

		return xmlContent;
	}

	private String getAllArchiveHistoryRec4Sync()
	{
		JArchiveJob archiveJob = new JArchiveJob();
        archiveJob.setScheduleType(ArchiveScheduleStatus.ScheduleAll);
        archiveJob.setbOnlyOneSession(false);
		
		List<ArchiveJobInfo> archiveJobs = ArchiveService.getInstance().GetArchiveJobsInfo(archiveJob);

		if(archiveJobs == null) {
			logger.debug("D2DSync(archive-full) - impl.GetArchiveJobsInfo() return null");
			return null;
		}
		else
			return marshall(archiveJobs);
	}

	private int transferData2Edge() throws EdgeServiceFault {
		//TODO
		D2DEdgeRegistration edgeRegInfo = new D2DEdgeRegistration();
		String wsdl = edgeRegInfo.GetEdgeWSDL();
		if(wsdl == null)
		{
			logger.error("D2DSync(archive) - doesn't managed by an Edge server yet!!");
			writeActivityLog(Constants.AFRES_AFALOG_WARNING, D2DSyncResourceID.AFRES_DATA_SYNC_NOT_MANAGED);
			return 1;
		}

		String edgeUUID = edgeRegInfo.GetEdgeUUID();
		String edgeHostName = edgeRegInfo.getEdgeRegInfo(ApplicationType.CentralManagement).getEdgeHostName();
		
		NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();
		String cacheFileName = nativeFacade.GetArchiveCacheFileName4Trans();

		if(cacheFileName.equals("ERROR")){
			logger.error("D2DSync(archive) - failed to get transfer file name!\n");
			writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_GET_FILECOPY_DATA_FAILURE, edgeHostName);
			return 1;
		}
		else if(cacheFileName.isEmpty()) {
			logger.debug("D2DSync(archive) - nothing to sync!\n");
			return 0;
		}

		BufferedReader br;
		String xmlContent = "";

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(cacheFileName)));


			String data = null;
			while((data = br.readLine())!=null)
			{
				xmlContent += data;
			}

			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			logger.error(e.toString());
			return 0;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error(e.toString());
			writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_GET_FILECOPY_DATA_FAILURE, edgeHostName);
			return -1;
		}

		IEdgeD2DService proxy = WebServiceFactory.getEdgeService(wsdl, IEdgeCM4D2D.class);
		String UUID = CommonService.getInstance().getNodeUUID();

		try {
			proxy.validateUserByUUID(edgeUUID);
		}catch(EdgeServiceFault e) {
			logger.error("D2DSync(archive) - Failed to establish connection to Edge Server(login failed)\n");
			writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_LOGIN_TO_EDGE_FAILURE, edgeHostName);
			return 1;
		}
		
		int result = proxy.D2DSyncArchive(getEdgeTaskId(), xmlContent, UUID, false);

		if (result == 0)
		{
			logger.debug("D2DSync(archive) - Sync Backup Data to Edge Server - succeeded!!\n");
			nativeFacade.DeleteArchiveCacheFileTrans();
			return 0;
		}
		else if( result == 1)
			logger.error("D2DSync(archive) - Sync Backup Data to Edge Server - XML parser failed!!\n");
		else if (result == 2 )
			logger.error("D2DSync(archive) - Sync Backup Data to Edge Server - SQL operation failed!!\n");
		else
			logger.error("D2DSync(archive) - Sync Backup Data to Edge Server - Other error!! result = " + result + "\n");
		writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_FILECOPY_TO_EDGE_FAILURE, edgeHostName);
		return -1;
	}
	
	private int transferFullData2Edge() throws EdgeServiceFault {
		NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();
		String xmlContent = getAllArchiveHistoryRec4Sync();

		D2DEdgeRegistration edgeRegInfo = new D2DEdgeRegistration();
		String wsdl = edgeRegInfo.GetEdgeWSDL();
		if(wsdl == null)
		{
			logger.error("D2DSync(archive-full) - doesn't managed by an Edge server yet!!");
			writeActivityLog(Constants.AFRES_AFALOG_WARNING, D2DSyncResourceID.AFRES_DATA_SYNC_NOT_MANAGED);
			return 1;
		}
		
		String edgeUUID = edgeRegInfo.GetEdgeUUID();
		String edgeHostName = edgeRegInfo.getEdgeRegInfo(ApplicationType.CentralManagement).getEdgeHostName();
		
		if(xmlContent == null || xmlContent.isEmpty()) {
			logger.debug("D2DSync(archive-full) - nothing to sync!");
			return 0;
		}
		else if(xmlContent.equals("ERROR")) {
			logger.error("D2DSync(archive-full) - failed to get XML to sync!");
			writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_GET_FILECOPY_DATA_FAILURE, edgeHostName);
			return -1;
		}

		IEdgeD2DService proxy = WebServiceFactory.getEdgeService(wsdl, IEdgeCM4D2D.class);
		String UUID = CommonService.getInstance().getNodeUUID();

		try {
			proxy.validateUserByUUID(edgeUUID);
		}catch(EdgeServiceFault e) {
			logger.error("D2DSync(archive-full) - Failed to establish connection to Edge Server(login failed)\n");
			writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_LOGIN_TO_EDGE_FAILURE, edgeHostName);
			return 1;
		}
		
		int result = proxy.D2DSyncArchive(getEdgeTaskId(), xmlContent, UUID, true);

		if (result == 0)
		{
			logger.debug("D2DSync(archive-full) - Sync Backup Data to Edge Server - succeeded!!\n");
			nativeFacade.DeleteAllVmInfoTransFile();
			return 0;
		}
		else if( result == 1)
			logger.error("D2DSync(archive-full) - Sync Backup Data to Edge Server - XML parser failed!!\n");
		else if (result == 2 )
			logger.error("D2DSync(archive-full) - Sync Backup Data to Edge Server - SQL operation failed!!\n");
		else
			logger.error("D2DSync(archive-full) - Sync Backup Data to Edge Server - Other error!! result = " + result + "\n");
		writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_FILECOPY_TO_EDGE_FAILURE, edgeHostName);
		return -1;
	}

	protected boolean process(boolean isFullSync) throws EdgeServiceFault{
		int result = -1;

		if(isFullSync)
			result = transferFullData2Edge();
		else
			result = transferData2Edge();

		if(result != 0)
			return false;
		else
			return true;
	}
}
