package com.ca.arcflash.webservice.edge.datasync;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.d2dreg.D2DEdgeRegistration;
import com.ca.arcflash.webservice.edge.d2dreg.EdgeRegInfo;
import com.ca.arcflash.webservice.foredge.exception.D2DSyncErrorCode;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.scheduler.Constants;
import com.ca.arcflash.webservice.service.BackupService;
import com.ca.arcflash.webservice.service.CommonService;
import com.ca.arcflash.webservice.toedge.IEdgeCM4D2D;
import com.ca.arcflash.webservice.toedge.IEdgeD2DService;
import com.ca.arcflash.webservice.toedge.WebServiceFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;

public class BaseDataSyncer implements IDataSynchronization {
	private static final Logger logger = Logger.getLogger(BaseDataSyncer.class);
	static NativeFacade nativeFacade = BackupService.getInstance().getNativeFacade();
	static String DATA_SYNC_PATH = "DataSync";
	static String dataSyncPath = "";
	static String FULL_SYNC_FINISH_MARK_POSTFIX = ".mark";
	static long edgeTaskId = 0;

	String FullSyncFinishMarkName = "DUMMY";

	static {
		dataSyncPath = CommonUtil.D2DInstallPath;
		if (!dataSyncPath.endsWith("\\")) {
			dataSyncPath += "\\";
		}
		dataSyncPath += DATA_SYNC_PATH;
		dataSyncPath += "\\";
	}

	protected boolean process(boolean isFullSync) throws EdgeServiceFault {
		// TODO
		return true;
	}

	@Override
	public boolean doSync(boolean isFullSync) {
		// TODO Auto-generated method stub
		boolean result = true;

		if (isFullSync)
			logger.debug("doSync(full) enter...");
		else
			logger.debug("doSync(incr) enter...");

		try {
			result = process(isFullSync);
		} catch (Exception e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
			return false;
		}

		return result;
	}
	
	public static int startSync(boolean fullSyncMode) throws EdgeServiceFault {
		D2DEdgeRegistration edgeRegInfo = new D2DEdgeRegistration();
		String wsdl = edgeRegInfo.GetEdgeWSDL();
		if (wsdl == null) {
			logger.error("D2DSync - doesn't managed by an Edge server yet!! (local edge registration info has been removed)");
			// writeActivityLog(BaseJob.AFRES_AFALOG_WARNING,
			// D2DSyncResourceID.AFRES_DATA_SYNC_NOT_MANAGED);
			return D2DSyncErrorCode.D2D_SYNC__NOT_MANAGED;
		}

		String edgeUUID = edgeRegInfo.GetEdgeUUID();
		String edgeHostName = edgeRegInfo.getEdgeRegInfo(ApplicationType.CentralManagement).getEdgeHostName();

		IEdgeD2DService proxy = null;
		proxy = WebServiceFactory.getEdgeService(wsdl, IEdgeCM4D2D.class, 30000, 0);
		String UUID = CommonService.getInstance().getNodeUUID();

		try {
			proxy.validateUserByUUID(edgeUUID);
		} catch (EdgeServiceFault e) {
			logger.error("Edge authentication failed:");
			logger.error(e.toString());
			writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_LOGIN_TO_EDGE_FAILURE,
					edgeHostName);
			return D2DSyncErrorCode.D2D_SYNC_LOGIN_TO_EDGE_FAILURE;
		}

		long result = proxy.D2DStartSync(fullSyncMode, UUID);

		if (result < 0) {
			if (result == -3) // D2D is removed from Edge already
			{
				// logger.error("D2DSync - It's not managed by registered Edge Server, remove the local Registration info");
				// cleanLocalEdgeRegInfo();
				// writeActivityLog(Constants.AFRES_AFALOG_WARNING,
				// D2DSyncResourceID.AFRES_DATA_SYNC_NOT_MANAGED);
				logger.error("D2DSync - doesn't managed by an Edge server");
				return D2DSyncErrorCode.D2D_SYNC__NOT_MANAGED;
			}

			writeActivityLog(Constants.AFRES_AFALOG_WARNING, D2DSyncResourceID.AFRES_DATA_SYNC_UNKNOWN_ERROR,
					edgeHostName);
			return D2DSyncErrorCode.D2D_SYNC_UNKNOWN_ERROR;
		} else {
			edgeTaskId = result;
			return D2DSyncErrorCode.D2D_SYNC_SUCCEED;
		}
	}

	public static boolean endSync(boolean fullSyncMode, boolean result) throws EdgeServiceFault {
		D2DEdgeRegistration edgeRegInfo = new D2DEdgeRegistration();
		String wsdl = edgeRegInfo.GetEdgeWSDL();
		if (wsdl == null) {
			logger.error("D2DSync - doesn't managed by an Edge server yet!!");
			// writeActivityLog(Constants.AFRES_AFALOG_WARNING,
			// D2DSyncResourceID.AFRES_DATA_SYNC_NOT_MANAGED);
			return false;
		}

		String edgeUUID = edgeRegInfo.GetEdgeUUID();
		String edgeHostName = edgeRegInfo.getEdgeRegInfo(ApplicationType.CentralManagement).getEdgeHostName();

		IEdgeD2DService proxy = WebServiceFactory.getEdgeService(wsdl, IEdgeCM4D2D.class);
		String UUID = CommonService.getInstance().getNodeUUID();

		try {
			proxy.validateUserByUUID(edgeUUID);
		} catch (EdgeServiceFault e) {
			logger.error("D2DSync - Failed to establish connection to Edge Server(login failed)\n");
			writeActivityLog(Constants.AFRES_AFALOG_ERROR, D2DSyncResourceID.AFRES_DATA_SYNC_LOGIN_TO_EDGE_FAILURE,
					edgeHostName);
			return false;
		}

		int ret = proxy.D2DEndSync(fullSyncMode, edgeTaskId, UUID, result);
		if (ret != 0) {
			logger.error("D2DSync - Failed to end this sync with Edge!");
			writeActivityLog(Constants.AFRES_AFALOG_WARNING, D2DSyncResourceID.AFRES_DATA_SYNC_UNKNOWN_ERROR,
					edgeHostName);
			return false;
		} else
			return true;
	}

	private String fullSyncFlagFile() {
		return dataSyncPath + FullSyncFinishMarkName + FULL_SYNC_FINISH_MARK_POSTFIX;
	}

	@Override
	public boolean isFullSyncFinished() {
		// TODO Auto-generated method stub
		String fullSyncFinishedFlagFile = fullSyncFlagFile();

		logger.debug("fullSyncFinishedFlagFile is " + fullSyncFinishedFlagFile + "\n");

		try {
			File theFile = new File(fullSyncFinishedFlagFile);
			if (theFile.exists())
				return true;
			else
				return false;
		} catch (Exception e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
			return false;
		}
	}

	@Override
	public boolean markFullSyncFinished() {
		// TODO Auto-generated method stub
		String fullSyncFinishedFlagFile = fullSyncFlagFile();

		File theFile = new File(fullSyncFinishedFlagFile);

		try {
			if (theFile.exists())
				return true;
			else {
				File theFolder = theFile.getParentFile();
				if ((!theFolder.isDirectory()) && theFolder.exists())
					theFolder.mkdir();

				theFile.createNewFile();
				return true;
			}
		} catch (IOException e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
			return false;
		}
	}

	static private boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	static public boolean removeDataSyncFolder() {
		File theFile = null;
		try {
			theFile = new File(dataSyncPath);
			if (theFile.exists()) {
				int i = 0;
				File[] fileArray = theFile.listFiles();
				for (i = 0; i < fileArray.length; i++) {
					String thisFileName = fileArray[i].getName();
					if (thisFileName.endsWith(FULL_SYNC_FINISH_MARK_POSTFIX))
						fileArray[i].delete();
				}

				for (i = 0; i < 10; i++) {
					if (deleteDirectory(theFile))
						break;
				}

				if (i < 10)
					return true;
				else
					return false;
			} else
				return true;
		} catch (Exception e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
			return false;
		}
	}

	@Override
	public void cleanFullSyncFinished() {
		// TODO Auto-generated method stub
		String fullSyncFinishedFlagFile = fullSyncFlagFile();

		File theFile = new File(fullSyncFinishedFlagFile);

		try {
			if (theFile.exists())
				theFile.delete();
		} catch (Exception e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
		}
	}

	public long getEdgeTaskId() {
		return edgeTaskId;
	}

	public static void writeActivityLog(long level, long resourceID) {
		try {
			nativeFacade.addLogActivity(level, resourceID, new String[] { "", "", "", "", "" });
		} catch (Throwable t) {
			logger.error("write activity log failed:");
			logger.error(t.toString());
		}
	}

	public static void writeActivityLog(long level, long resourceID, String param) {
		try {
			nativeFacade.addLogActivity(level, resourceID, new String[] { param, "", "", "", "" });
		} catch (Throwable t) {
			logger.error("write activity log failed:");
			logger.error(t.toString());
		}
	}

	public static String getRegisteredEdgeHostName() {
		D2DEdgeRegistration edgeRegInfo = new D2DEdgeRegistration();
		String edgeHostName = edgeRegInfo.getEdgeRegInfo(ApplicationType.CentralManagement).getEdgeHostName();
		if (edgeHostName == null)
			return "";
		else
			return edgeHostName;
	}
}
