package com.ca.arcflash.webservice.edge.datasync;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;

import com.ca.arcflash.service.util.ActivityLogConverter;
import com.ca.arcflash.webservice.edge.activelogsync.ActivityLogTrans;
import com.ca.arcflash.webservice.edge.activelogsync.LogRec;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.d2dreg.D2DEdgeRegistration;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.toedge.IEdgeCM4D2D;
import com.ca.arcflash.webservice.toedge.IEdgeD2DService;
import com.ca.arcflash.webservice.toedge.WebServiceFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;

public class ActiveLogSyncer extends BaseDataSyncer{
	private static final Logger logger = Logger.getLogger(ActiveLogSyncer.class);
	NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();

	public ActiveLogSyncer(){
		FullSyncFinishMarkName = "activeLogFullSyncFinished";
	}

	private String  marshXML2String(ActivityLogTrans logTrans) {
		StringWriter sw = new StringWriter();
		String xmlString = "";

		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance("com.ca.arcflash.webservice.edge.activelogsync");
			Marshaller marsh = jaxbContext.createMarshaller();
			marsh.marshal(logTrans, sw);
		} catch (Exception e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
			return null;
		}

		xmlString = sw.toString();
		try {
			sw.close();
		} catch (IOException e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		}

		return xmlString;
	}

	private boolean marshXML2File(String activeLogTransFileXML, ActivityLogTrans logTrans) {
		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance("com.ca.arcflash.webservice.edge.activelogsync");
			Marshaller marsh = jaxbContext.createMarshaller();
			marsh.marshal(logTrans, new File(activeLogTransFileXML));
		} catch (Exception e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
			return false;
		}

		return true;
	}

	private ActivityLogTrans unmarshXML(String activeLogTransFileXML) {
		ActivityLogTrans mActiveLogTrans = null;

		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance("com.ca.arcflash.webservice.edge.activelogsync");
			Unmarshaller unmarsh = jaxbContext.createUnmarshaller();
			mActiveLogTrans = (ActivityLogTrans) unmarsh.unmarshal(new File(activeLogTransFileXML));
		} catch (Exception e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
			return null;
		}

		return mActiveLogTrans;
	}

	private boolean transXML2Edge(String xmlContent, ActivityLogTrans logTrans, String activeLogTransFileXML, boolean cleanFlag) throws EdgeServiceFault {
		logger.debug("ActiveLogSync: transXML2Edge Enter ...");

		D2DEdgeRegistration edgeRegInfo = new D2DEdgeRegistration();
		String edgeWSDL = edgeRegInfo.GetEdgeWSDL();
		String edgeUUID = edgeRegInfo.GetEdgeUUID();
		
		if(edgeWSDL == null)
		{
			logger.error("ActiveLogSync: there is no edge registration flag!");
			writeActivityLog(Constants.AFRES_AFALOG_WARNING, D2DSyncResourceID.AFRES_DATA_SYNC_NOT_MANAGED);
			return false;
		}
		
		String edgeHostName = edgeRegInfo.getEdgeRegInfo(ApplicationType.CentralManagement).getEdgeHostName();

		IEdgeD2DService proxy = WebServiceFactory.getEdgeService(edgeWSDL, IEdgeCM4D2D.class);
		
		try {
			proxy.validateUserByUUID(edgeUUID);
		}catch(EdgeServiceFault e) {
			logger.error("ActiveLogSync - Failed to establish connection to Edge Server(login failed)\n");
			writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_LOGIN_TO_EDGE_FAILURE, edgeHostName);
			return false;
		}
		
		int result = proxy.D2DSyncActiveLogXML(getEdgeTaskId(), xmlContent, CommonService.getInstance().getNodeUUID(), cleanFlag);
		if (result == 0) {
			logger.error("ActiveLogSync - Sync active log succeeded!!\n");
			if(logTrans.getLogRec().size() == 0) {
				nativeFacade.DelD2DActiveLogTransFileXML();
			}
			else {
				if(false == marshXML2File(activeLogTransFileXML, logTrans)) {
					logger.error("ActiveLogSync - failed to marshal to XML file " + activeLogTransFileXML);
					writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_GET_LOG_DATA_FAILURE, edgeHostName);
					return false;
				}
			}

			return true;
		}
		else if( result == 1) {
			logger.error("ActiveLogSync - Sync active log XML parser failed!!\n");
			writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_LOG_TO_EDGE_FAILURE, edgeHostName);
			return false;
		}
		else if (result == 2 ) {
			logger.error("ActiveLogSync - Sync active log SQL operation failed!!\n");
			writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_LOG_TO_EDGE_FAILURE, edgeHostName);
			return false;
		}
		else {
			logger.error("ActiveLogSync - Sync active log Other error!!\n");
			writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_LOG_TO_EDGE_FAILURE, edgeHostName);
			return false;
		}
	}

	private int SyncActiveLog2Edge(boolean FullSync) throws EdgeServiceFault {
		int cnt = 0;
		boolean cleanFlag = FullSync;
		String xmlContent = "";

		String activeLogTransFileXML = "";

		if(FullSync)
			activeLogTransFileXML = nativeFacade.GetFullD2DActiveLogTransFileXML();
		else
			activeLogTransFileXML = nativeFacade.GetD2DActiveLogTransFileXML();

		if(activeLogTransFileXML.isEmpty()) {
			logger.debug("ActiveLogSync: nothing to sync!");
			return 0;
		}
		else if (activeLogTransFileXML.equals("ERROR")) {
			logger.error("failed to get XML file name!\n");
			writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_GET_LOG_DATA_FAILURE);
			return -1;
		}

		ActivityLogTrans logTrans = unmarshXML(activeLogTransFileXML);
		if(logTrans == null) {
			logger.error("ActiveLogSync: Cannot unmarshal " + activeLogTransFileXML);
			writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_GET_LOG_DATA_FAILURE);
			return -1;
		}

		ActivityLogTrans logTransSync = new ActivityLogTrans();
		List<LogRec> logRecLstSync = new ArrayList<LogRec>();
		List<LogRec> logRecLst = logTrans.getLogRec();
		Iterator<LogRec> iter = logRecLst.iterator();
		while (iter.hasNext()) {
			cnt ++;
			if((cnt%100) != 0){
				LogRec logRec = iter.next();
				logRec.setStrLog(ActivityLogConverter.trimLogSpecialSuffix(logRec.getStrLog()));
				logRecLstSync.add(logRec);
				iter.remove();
				continue;
			}

			logTransSync.SetLogRec(logRecLstSync);
			logTrans.SetLogRec(logRecLst);
			xmlContent = marshXML2String(logTransSync);
			if(xmlContent == null) {
				logger.error("ActiveLogSync: failed to marshal to XML string!");
				writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_GET_LOG_DATA_FAILURE);
				return -1;
			}

			if(false == transXML2Edge(xmlContent,logTrans,activeLogTransFileXML, cleanFlag)) {
				return -1;
			}
			cleanFlag = false;
		}

		if((cnt%100) != 0) {
			logTransSync.SetLogRec(logRecLstSync);
			logTrans.SetLogRec(logRecLst);

			xmlContent = marshXML2String(logTransSync);
			if(xmlContent == null) {
				logger.error("ActiveLogSync: failed to marshal to XML string!");
				writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_GET_LOG_DATA_FAILURE);
				return -1;
			}

			if(false == transXML2Edge(xmlContent,logTrans,activeLogTransFileXML, cleanFlag)) {
				return -1;
			}
		}

		return 0;
	}

	protected boolean process(boolean isFullSync) throws EdgeServiceFault {
		if(0 != SyncActiveLog2Edge(isFullSync))
			return false;
		else
			return true;
	}
}
