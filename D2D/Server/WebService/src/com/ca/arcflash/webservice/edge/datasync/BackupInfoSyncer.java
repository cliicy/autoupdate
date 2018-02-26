package com.ca.arcflash.webservice.edge.datasync;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

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

public class BackupInfoSyncer extends BaseDataSyncer {
	private static final Logger logger = Logger.getLogger(ActiveLogSyncer.class);

	public BackupInfoSyncer() {
		FullSyncFinishMarkName = "backupJobFullSyncFinished";
	}

	public int transferFullData2Edge() throws EdgeServiceFault {
		NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();
		String xmlContent = nativeFacade.GetReSyncData();

		D2DEdgeRegistration edgeRegInfo = new D2DEdgeRegistration();
		String wsdl = edgeRegInfo.GetEdgeWSDL();
		if(wsdl == null)
		{
			logger.error("D2DSync(full) - doesn't managed by an Edge server yet!!");
			writeActivityLog(Constants.AFRES_AFALOG_WARNING, D2DSyncResourceID.AFRES_DATA_SYNC_NOT_MANAGED);
			return 1;
		}
		
		String edgeUUID = edgeRegInfo.GetEdgeUUID();
		String edgeHostName = edgeRegInfo.getEdgeRegInfo(ApplicationType.CentralManagement).getEdgeHostName();
		
		if(xmlContent.isEmpty()) {
			logger.debug("D2DSync(full) - nothing to sync!\n");
			return 0;
		}
		else if(xmlContent.equals("ERROR")) {
			logger.error("D2DSync(full) - failed to get xml content!\n");
			writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_GET_BK_DATA_FAILURE);
			return -1;
		}

		IEdgeD2DService proxy = WebServiceFactory.getEdgeService(wsdl,IEdgeCM4D2D.class);
		String UUID = CommonService.getInstance().getNodeUUID();

		try {
			proxy.validateUserByUUID(edgeUUID);
		}catch(EdgeServiceFault e) {
			logger.error("D2DSync(full) - Failed to establish connection to Edge Server(login failed)\n");
			writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_LOGIN_TO_EDGE_FAILURE, edgeHostName);
			return 1;
		}
		
		int result = proxy.D2DSyncXML(getEdgeTaskId(), xmlContent, UUID, true);

		if (result == 0)
		{
			logger.debug("D2DSync(full) - Sync Backup Data to Edge Server - succeeded!!\n");
			return 0;
		}
		else if( result == 1)
			logger.error("D2DSync(full) - Sync Backup Data to Edge Server - XML parser failed!!\n");
		else if (result == 2 )
			logger.error("D2DSync(full) - Sync Backup Data to Edge Server - SQL operation failed!!\n");
		else
			logger.error("D2DSync(full) - Sync Backup Data to Edge Server - Other error!! result = " + result + "\n");
		writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_BK_TO_EDGE_FAILURE, edgeHostName);
		return -1;
	}

	public int transferXML2Edge() throws EdgeServiceFault {
		NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();
		String CacheFileName = nativeFacade.GetCacheFile4Sync();
		logger.debug("D2DSync - nativeFacade.GetCacheFile4Sync() = "+CacheFileName);

		//String wsdl = "http://sonle01-w2k3-2:8080/EdgeWebUI/services/EdgeServiceImpl?wsdl";
		D2DEdgeRegistration edgeRegInfo = new D2DEdgeRegistration();
		String wsdl = edgeRegInfo.GetEdgeWSDL();
		if(wsdl == null)
		{
			logger.error("execute(JobExecutionContext) - doesn't managed by an Edge server yet!!");
			writeActivityLog(Constants.AFRES_AFALOG_WARNING, D2DSyncResourceID.AFRES_DATA_SYNC_NOT_MANAGED);
			return 1;
		}
		
		String edgeUUID = edgeRegInfo.GetEdgeUUID();
		String edgeHostName = edgeRegInfo.getEdgeRegInfo(ApplicationType.CentralManagement).getEdgeHostName();

		BufferedReader br;
		String xmlContent = "";

		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(CacheFileName), "utf-8"));
			String data = null;
			while((data = br.readLine())!=null){
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
			writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_GET_BK_DATA_FAILURE);
			return -1;
		}

		IEdgeD2DService proxy = WebServiceFactory.getEdgeService(wsdl,IEdgeCM4D2D.class);
		String UUID = CommonService.getInstance().getNodeUUID();

		try {
			proxy.validateUserByUUID(edgeUUID);
		}catch(EdgeServiceFault e) {
			logger.error("D2DSync - Failed to establish connection to Edge Server(login failed)\n");
			writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_LOGIN_TO_EDGE_FAILURE, edgeHostName);
			return 1;
		}
		
		int result = proxy.D2DSyncXML(getEdgeTaskId(), xmlContent, UUID, false);

		if (result == 0)
		{
			logger.debug("D2DSync - Sync Backup Data to Edge Server - succeeded!!\n");
			nativeFacade.DeleteCacheFile4Sync();
			return 0;
		}
		else if( result == 1)
			logger.error("D2DSync - Sync Backup Data to Edge Server - XML parser failed!!\n");
		else if (result == 2 )
			logger.error("D2DSync - Sync Backup Data to Edge Server - SQL operation failed!!\n");
		else
			logger.error("D2DSync - Sync Backup Data to Edge Server - Other error!! result = " + result + "\n");
		writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_BK_TO_EDGE_FAILURE, edgeHostName);
		return -1;
	}

	protected boolean process(boolean isFullSync) throws EdgeServiceFault{
		int result = -1;

		if(isFullSync)
			result = transferFullData2Edge();
		else
			result = transferXML2Edge();

		if(result != 0)
			return false;
		else
			return true;
	}
}
