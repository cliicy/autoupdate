package com.ca.arcserve.edge.app.base.webservice.alert;

import java.util.Date;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.edge.email.CommonEmailInformation;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeSrmDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.util.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.AlertEventType;
import com.ca.arcserve.edge.app.base.webservice.contract.common.AlertEventType.Holder;
import com.ca.arcserve.edge.app.base.webservice.email.EdgeEmailService;

public class AlertManager {
	private static AlertManager instance = null;
	private static Logger logger = Logger.getLogger( AlertManager.class );
	public static synchronized AlertManager getInstance() {
			if (instance == null)
				instance = new AlertManager();
			return instance;
	}
	
	
	
	public int saveAlertToDB(String send_host,String protectedNode, long job_type, long raw_event_type,  String alert_subject,
			String alert_message, Date send_time, long product_type )  {
		logger.info("AlertManager-- start save alert with send_host = " + send_host+ " protectedNode: " + protectedNode  
				+" raw_event_type: " + raw_event_type +" time: " + send_time.toString() );
		int [] alertIDs = new int[1];
		try {

			if( StringUtil.isEmptyOrNull( send_host ) ) {
				send_host = EdgeEmailService.GetInstance().getHostName().toLowerCase();
			}
			if( StringUtil.isEmptyOrNull( protectedNode ) ) {
				protectedNode = EdgeEmailService.GetInstance().getHostName().toLowerCase();
			}
			send_host = send_host.toLowerCase();
			protectedNode = protectedNode.toLowerCase();
			alert_subject = replaceNodeNameToLowCase( send_host, protectedNode, alert_subject );
			alert_message = replaceNodeNameToLowCase( send_host, protectedNode, alert_message );
			IEdgeSrmDao srmDao = DaoFactory.getDao(IEdgeSrmDao.class);
			Holder<Integer> overAllEventType = new Holder<Integer>();
			Holder<Integer> jobStatus =new Holder<Integer>();
			AlertEventType.generateEventType_JobStatusFromRawEvent( raw_event_type, job_type,  overAllEventType, jobStatus  );
	
				srmDao.spedgeAlertMessageInsert( send_host, protectedNode,  raw_event_type, job_type, overAllEventType.value, jobStatus.value,
						alert_subject, alert_message, send_time, product_type, alertIDs );
		}
		catch( Exception e ) {
			logger.error( e.getMessage(), e );
		}
		return alertIDs[0];
	}
	/***fix issue 29168;
	 * this is not a good method; but the set ProtectedNode/send_host method is called everywhere; it's very hard to cover all case;so
	 * we do a upper-lower case conversion here;
	 */
	private String replaceNodeNameToLowCase( String send_host, String protectedNode, String target ) {
		
		return target.replaceAll(send_host.toUpperCase(), send_host ).replaceAll(protectedNode.toUpperCase(), protectedNode);
	}
	public int SaveAlertInfo( CommonEmailInformation info ) {

		int infoId = saveAlertToDB(
				info.getSendhost(),
				info.getProtectedNode(),
				info.getJobType(),
				info.getEventType(),
				info.getSubject(), 
				info.getContent(), 
				info.getSendTime(),
				info.getProductType() );
		if( infoId <= 0 ){
			return -1;
		}
		info.setAlertID(infoId);
		
		return 0;
	}
	
	
}
