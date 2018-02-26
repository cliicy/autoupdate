package com.ca.arcserve.edge.app.base.webservice.log;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.webservice.contract.log.LogExportMessage;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogExportMessage.MessageType;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogExportProgress;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogExportProgress.LogExportStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogPagingConfig;

public class LogExportController {

	private static Logger log = Logger.getLogger( LogExportController.class );
	private static Map<String, LogExportProgress > progressInCache = new HashMap<String, LogExportProgress >(); 
	private static Map<String, ActivityLogExporter > exporterInCache = new HashMap<String, ActivityLogExporter >(); 
	private static ExecutorService executor;

	public static void initLogExportController( ExecutorService _executor ) {
		executor = _executor;
	}
	public static void generateExportFile ( LogPagingConfig config,  LogFilter filter, String exportIdentifier  )  {
		
		LogExportProgress progress = new LogExportProgress();
		progressInCache.put(exportIdentifier, progress);
		setProgress( exportIdentifier, LogExportStatus.Initial, 5 );

		ActivityLogExporter exporter = new ActivityLogExporter(config, filter, exportIdentifier  ); 
		if( executor!=null ) {
			executor.submit(exporter);
			exporterInCache.put(exportIdentifier, exporter);
		}
	}
	public static synchronized LogExportMessage logExportCommunicate( LogExportMessage request, String exportIdentifier ) {
		LogExportMessage response = new LogExportMessage(); 
		response.setType(MessageType.RESPONSE);
		
		if( request.isRequestCancel() ) {
			ActivityLogExporter exporter = exporterInCache.get(exportIdentifier);
			if( exporter !=null ) {
				exporter.setCanceled(true);
				progressInCache.remove(exportIdentifier);
				exporterInCache.remove(exportIdentifier);
			}
		}
		else {
			LogExportProgress progress=  progressInCache.get(exportIdentifier);
			if( progress == null ) {
				log.error("LogExportController: cannot obtain progress object using specified identifier " );
				response.setProgress(null);
			}
			else {
				if( progress.getExportStatus()== LogExportStatus.FAIL || progress.getExportStatus() == LogExportStatus.SUCCESS ) {
					progressInCache.remove(exportIdentifier);
					exporterInCache.remove(exportIdentifier);
				}
				response.setProgress(progress);
			}
		}
		return response;
	
	}
	static synchronized void setProgress( String exportIdentifier, LogExportStatus status, int value ) {
//		try {
//			Thread.sleep(1000);   ///test only!!!
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		LogExportProgress progress = progressInCache.get(exportIdentifier);
		if( progress == null ) {
			log.error("LogExportController: cannot obtain progress object using specified identifier " );
		}
		else {
			progress.setExportStatus( status ); 
			progress.setExportProgress(value);
		}
	}
	static synchronized void setResult( String exportIdentifier, LogExportStatus status, String exportFile, String errorMsg ) {
		LogExportProgress progress = progressInCache.get(exportIdentifier);
		if( progress == null ) {
			log.error("LogExportController: cannot obtain progress object using specified identifier " );
		}
		else {
			progress.setExportStatus( status ); 
			progress.setErrorMsg(errorMsg);
			progress.setExportFileName(exportFile);
		}
	}
	
}
