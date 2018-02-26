package com.ca.arcflash.webservice.edge.datasync;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.ha.event.VCMEvent;
import com.ca.arcflash.ha.event.VCMEventException;
import com.ca.arcflash.ha.event.VCMEventManager;
import com.ca.arcflash.webservice.data.edge.datasync.vcm.VCMEventList;
import com.ca.arcflash.webservice.data.edge.datasync.vcm.VCMEventRec;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.d2dreg.D2DEdgeRegistration;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.toedge.IEdgeCM4D2D;
import com.ca.arcflash.webservice.toedge.IEdgeD2DService;
import com.ca.arcflash.webservice.toedge.WebServiceFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;

public class VCMSyncer extends BaseDataSyncer{
	private static final Logger logger = Logger.getLogger(VCMSyncer.class);
	private volatile static boolean cacheFileLockFlag = false;
	private static final String CACHE_FILE_NAME = "cache_vcm_info.xml";
	private static final String CACHE_FILE_TRANS_NAME = "cache_vcm_info_trans.xml";
	private static final String CACHE_FILE_FULL_NAME = "cache_vcm_info_full.xml";
	private String vcmEventHistoryPath = "";

	public VCMSyncer() {
		setPath();
		FullSyncFinishMarkName = "vcmEventFullSyncFinished";
	}

	private static synchronized boolean getCacheFileLock(){
		if(cacheFileLockFlag)
			return false;

		cacheFileLockFlag = true;
		return true;
	}

	private static void markCacheFileLock() {
		while(getCacheFileLock() == false) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				logger.error(e.getMessage() == null ? e : e.getMessage());
			}
		}
	}

	private static synchronized void releaseCacheFileLock(){
		cacheFileLockFlag = false;
	}

	private void setPath(){
		String d2dPath = CommonUtil.D2DInstallPath;
		if (!d2dPath.endsWith("\\")) {
			d2dPath += "\\";
		}

		vcmEventHistoryPath = d2dPath + CommonUtil.VCM_EVENT_HISTORY_DIR + "\\";

		return;
	}

	private VCMEventList unmarshalFromFile(String cacheFileName){
		VCMEventList vcmEventLst = null;

		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance("com.ca.arcflash.webservice.data.edge.datasync.vcm");
			Unmarshaller unmarsh = jaxbContext.createUnmarshaller();
			vcmEventLst = (VCMEventList) unmarsh.unmarshal(new File(cacheFileName));
		} catch (Exception e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
			return null;
		}
		return vcmEventLst;
	}

	private boolean marshalToFile(VCMEventList vcmEventLst, String cacheFileName){
		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance("com.ca.arcflash.webservice.data.edge.datasync.vcm");
			Marshaller marsh = jaxbContext.createMarshaller();
			marsh.marshal(vcmEventLst, new File(cacheFileName));
		} catch (Exception e) {
			logger.error(e.toString());
			return false;
		}

		return true;
	}

	private String ConvertDate2String(Date theDate){
		if(theDate != null){
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			df.setTimeZone(TimeZone.getTimeZone("UTC"));
			return df.format(theDate);
		}
		else {
			return "1980-01-01 12:00:00";
		}
	}

	private void ConvertEvent2Rec(VCMEvent event, VCMEventRec eventRec) {
		eventRec.SetTaskGuid(event.getTaskGuid());
		eventRec.SetTaskName(event.getTaskName());
		eventRec.SetTaskType(event.getTaskType());
		eventRec.SetStartTime(ConvertDate2String(event.getStartTime()));
		eventRec.SetEndTime(ConvertDate2String(event.getEndTime()));
		eventRec.SetSrcHostName(event.getSrcHostName());
		eventRec.SetSrcVMName(event.getSrcVMName());
		eventRec.SetSrcVirtualCenterName(event.getSrcVirtualCenterName());
		eventRec.SetSrcVMUUID(event.getSrcVMUUID());
		eventRec.SetSrcVMType(event.getSrcVMType());
		eventRec.SetDestHostName(event.getDestHostName());
		eventRec.SetDestVMName(event.getDestVMName());
		eventRec.SetDestVirtualCenterName(event.getDestVirtualCenterName());
		eventRec.SetDestVMUUID(event.getDestVMUUID());
		eventRec.SetDestVMType(event.getDestVMType());
		eventRec.SetStatus(event.getStatus());
		eventRec.SetStatusComment(event.getStatusComment());
		eventRec.SetVcmMonitorHost(event.getVcmMonitorHost());
		if(event.isProxy())
			eventRec.SetIsProxy("true");
		else
			eventRec.SetIsProxy("false");
		eventRec.SetAfGuid(event.getAfGuid());
		eventRec.SetJobId(event.getJobID());
	}

	private String SwitchCacheFileToTrans() {
		String transFileName = "";
		String cacheFileFullPath = dataSyncPath + CACHE_FILE_NAME;
		String cacheFileTransFullPath = dataSyncPath + CACHE_FILE_TRANS_NAME;
		VCMEventList eventLstDest = null;
		VCMEventList eventLst = null;

		markCacheFileLock();

		try {
			File theFileTrans = new File(cacheFileTransFullPath);
			if(theFileTrans.exists())
				eventLstDest = unmarshalFromFile(cacheFileTransFullPath);
			else
				eventLstDest = new VCMEventList();

			File theFile = new File(cacheFileFullPath);
			if(theFile.exists())
				eventLst = unmarshalFromFile(cacheFileFullPath);
			else
				eventLst = new VCMEventList();

			if(eventLstDest == null || eventLst == null) {
				logger.debug("unmarshalFromFile() failed!");
				return "ERROR";
			}

			List<VCMEventRec> eventRecDest = eventLstDest.getVCMEventRec();
			List<VCMEventRec> eventRec = eventLst.getVCMEventRec();

			if(eventRecDest.isEmpty() && eventRec.isEmpty()) {
				logger.debug("Nothing to sync!");
				return "";
			}

			Iterator<VCMEventRec> iter = eventRec.iterator();
			if(iter.hasNext())
				eventRecDest.add(iter.next());
			eventLstDest.setVCMEventRec(eventRecDest);

			if(false == marshalToFile(eventLstDest, cacheFileTransFullPath))
				transFileName = "ERROR";
			else {
				theFile.delete();
				transFileName = cacheFileTransFullPath;
			}
		}catch(Exception e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
			return "ERROR";
		}finally {
			releaseCacheFileLock();
		}

		return transFileName;
	}

	private List<String> GetAllEventHistorySubFolders(){
		File eventHistoryDir = new File(vcmEventHistoryPath);
		File[] children = eventHistoryDir.listFiles();

		if(children == null || children.length == 0) {
			return null;
		}
		
		List<String> childList = new ArrayList<String>();
		for(int i = 0 ; i < children.length ; i ++){
			if(children[0].isDirectory() &&
				(!children[0].getName().startsWith(".")) &&
				(!children[0].getName().startsWith("..")) ) {
				childList.add(children[0].getName());
			}
		}

		if(childList.isEmpty())
			return null;
		else
			return childList;
	}

	private boolean CreateEmptySyncFile(String fileName) {
		File syncFolder = new File(dataSyncPath);
		if(!syncFolder.exists()) {
			if(!syncFolder.mkdir()) {
				logger.debug("cannot create data sync folder (" + dataSyncPath + ")");
				return false;
			}
		}

		VCMEventList eventList = new VCMEventList();
		List<VCMEventRec> eventRecLst = new ArrayList<VCMEventRec>();
		eventRecLst.clear();
		eventList.setVCMEventRec(eventRecLst);
		this.marshalToFile(eventList, fileName);
		return true;
	}

	private boolean MergeHistoryToFullSyncTransFile(String cacheFileName, String child){
		VCMEventManager vcmEventMgr = VCMEventManager.getInstance();
		try {
			Collection<VCMEvent> eventCollection = vcmEventMgr.getVCMEvent(child);
			if(eventCollection == null || eventCollection.isEmpty())
				return true;

			Iterator<VCMEvent> iter = eventCollection.iterator();
			while(iter.hasNext()) {
				VCMEventRec eventRec = new VCMEventRec();
				ConvertEvent2Rec(iter.next(), eventRec);
				VCMEventList eventList = unmarshalFromFile(cacheFileName);
				if(eventList == null) {
					logger.debug("unmarshalFromFile(" + cacheFileName + ") failed!");
					return false;
				}

				List<VCMEventRec> eventRecLst = eventList.getVCMEventRec();
				eventRecLst.add(eventRec);
				eventList.setVCMEventRec(eventRecLst);
				if(false == marshalToFile(eventList, cacheFileName)) {
					logger.debug("marshalFromFile(" + cacheFileName + ") failed!");
					return false;
				}
			}

			return true;
		} catch (VCMEventException e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
			return false;
		}
	}

	private String GetFullSyncTransFile() {
		String strFullSyncFileName = dataSyncPath + CACHE_FILE_FULL_NAME;
		File fullSyncFile = new File(strFullSyncFileName);

		if(fullSyncFile.exists())
			fullSyncFile.delete();

		if(!CreateEmptySyncFile(strFullSyncFileName))
			return "ERROR";

		List<String> childList = GetAllEventHistorySubFolders();
		if(childList == null)
			return "";

		Iterator<String> iter = childList.iterator();
		while(iter.hasNext()) {
			String child = iter.next();
			if(false == MergeHistoryToFullSyncTransFile(strFullSyncFileName, child)) {
				logger.debug("MergeHistoryToFullSyncTransFile(" + strFullSyncFileName
						+ "," + child + ") failed!");
				if(fullSyncFile.exists())
					fullSyncFile.delete();
				return "ERROR";
			}
		}

		return strFullSyncFileName;
	}

	private int sendXMLToEdge(String xmlFileName, boolean cleanFlag) throws EdgeServiceFault {
		//TODO
		D2DEdgeRegistration edgeRegInfo = new D2DEdgeRegistration();
		String wsdl = edgeRegInfo.GetEdgeWSDL();
		if(wsdl == null)
		{
			logger.error("D2DSync(VCM) - doesn't managed by an Edge server yet!!");
			writeActivityLog(Constants.AFRES_AFALOG_WARNING, D2DSyncResourceID.AFRES_DATA_SYNC_NOT_MANAGED);
			return 1;
		}

		String edgeUUID = edgeRegInfo.GetEdgeUUID();
		String edgeHostName = edgeRegInfo.getEdgeRegInfo(ApplicationType.CentralManagement).getEdgeHostName();
		
		BufferedReader br;
		String xmlContent = "";

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(xmlFileName)));


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
			writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_GET_VCM_DATA_FAILURE);
			return -1;
		}

		IEdgeD2DService proxy = WebServiceFactory.getEdgeService(wsdl,IEdgeCM4D2D.class);
		String UUID = CommonService.getInstance().getNodeUUID();

		try {
			proxy.validateUserByUUID(edgeUUID);
		}catch(EdgeServiceFault e) {
			logger.error("D2DSync(VCM) - Failed to establish connection to Edge Server(login failed)\n");
			writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_LOGIN_TO_EDGE_FAILURE, edgeHostName);
			return 1;
		}
		
		int result = proxy.D2DSyncVCM(getEdgeTaskId(), xmlContent, UUID, cleanFlag);

		if (result == 0)
		{
			logger.debug("D2DSync(VCM) - Sync Backup Data to Edge Server - succeeded!!\n");
			return 0;
		}
		else if( result == 1)
			logger.error("D2DSync(VCM) - Sync Backup Data to Edge Server - XML parser failed!!\n");
		else if (result == 2 )
			logger.error("D2DSync(VCM) - Sync Backup Data to Edge Server - SQL operation failed!!\n");
		else
			logger.error("D2DSync(VCM) - Sync Backup Data to Edge Server - Other error!! result = " + result + "\n");
		writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_VCM_TO_EDGE_FAILURE, edgeHostName);
		return -1;
	}

	public int transferFullXML2Edge() throws EdgeServiceFault {
		int result = 0;

		String transFileName = GetFullSyncTransFile();
		if(transFileName.equals("ERROR")) {
			logger.error("Cannot get sync data for tranferring");
			writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_GET_VCM_DATA_FAILURE);
			return -1;
		}
		else if(transFileName.equals("")){
			logger.debug("There is nothing to sync!");
			return 0;
		}

		result = sendXMLToEdge(transFileName, true);

		File theFile = new File(transFileName);

		if(false == theFile.delete())
			logger.error("Failed to delete file " + transFileName + "!");

		return result;
	}

	public long SaveVCMEvent2CacheFile(VCMEvent theEvent){
		String cacheFileFullPath = dataSyncPath + CACHE_FILE_NAME;
		VCMEventList eventLst = null;
		File cacheFile = new File(cacheFileFullPath);

		if(!isFullSyncFinished())
			return 0;

		markCacheFileLock();

		try {
			if(cacheFile.exists())
				eventLst = unmarshalFromFile(cacheFileFullPath);
			else {
				if(!CreateEmptySyncFile(cacheFileFullPath)) {
					return -1;
				}

				eventLst = new VCMEventList();
			}
			if(eventLst == null){
				logger.debug("Failed on SaveVCMEvent2CacheFile(): get VCMEventList");
				return -1;
			}

			VCMEventRec eventRec = new VCMEventRec();
			ConvertEvent2Rec(theEvent, eventRec);
			List<VCMEventRec> eventRecLst = eventLst.getVCMEventRec();
			eventRecLst.add(eventRec);
			eventLst.setVCMEventRec(eventRecLst);
			if(false == marshalToFile(eventLst, cacheFileFullPath)) {
				logger.debug("Failed on SaveVCMEvent2CacheFile(): marshalToFile()");
				return -1;
			}

			return 0;
		}catch(Exception e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
			return -1;
		}finally {
			releaseCacheFileLock();
		}
	}

	public int transferXML2Edge() throws EdgeServiceFault {
		int result = 0;

		String transFileName = SwitchCacheFileToTrans();
		if(transFileName.equals("ERROR")) {
			logger.error("SwitchCacheFileToTrans() failed");
			writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_GET_VCM_DATA_FAILURE);
			return -1;
		}
		else if(transFileName.equals("")){
			logger.debug("There is nothing to sync!");
			return 0;
		}

		result = sendXMLToEdge(transFileName, false);

		if(result == 0) {
			File theFile = new File(transFileName);

			if(false == theFile.delete())
				logger.error("Failed to delete file " + transFileName + "!");
		}

		return result;
	}

	protected boolean process(boolean isFullSync) throws EdgeServiceFault {
		int result = -1;

		if(isFullSync)
			result = transferFullXML2Edge();
		else
			result = transferXML2Edge();

		if(result != 0)
			return false;
		else
			return true;
	}
	
	
	public String testGetFullSyncTransFile() {
		return GetFullSyncTransFile();
	}
	
	public static void main(String args[]) {
		VCMSyncer syncer = new VCMSyncer();
		syncer.testGetFullSyncTransFile();
	}
}
