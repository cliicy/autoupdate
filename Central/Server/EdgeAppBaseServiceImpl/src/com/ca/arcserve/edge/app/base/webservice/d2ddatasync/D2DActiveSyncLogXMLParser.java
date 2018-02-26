package com.ca.arcserve.edge.app.base.webservice.d2ddatasync;

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.d2dactivelog.ActivityLogTrans;
import com.ca.arcserve.edge.app.base.webservice.d2dactivelog.LogRec; 

public class D2DActiveSyncLogXMLParser extends D2DBaseXmlParser {
	private static final Logger logger = Logger.getLogger(D2DActiveSyncLogXMLParser.class);

	private ActivityLogTrans m_activityLogTrans = null;
	
	public D2DActiveSyncLogXMLParser() {
	
	}

	/*
	 * Return Code: 0 succeeded 1 XML parser error 2 SQL operation error
	 * 			   -1 unexpected exception
	 */
	public int processActivityLogTrans(String xmlContent, boolean cleanFlag) {
		try {
			int result = processXML(xmlContent, cleanFlag);
			
			if(result != 0)
				writeActivityLog(Severity.Error, D2DSyncMessage.EDGE_D2D_SYNC_ACTIVE_LOG_FAILED);
			else
				writeActivityLog(Severity.Information, D2DSyncMessage.EDGE_D2D_SYNC_ACTIVE_LOG_SUCCEEDED);
			
			return result;
		}catch(Throwable t) {
			logger.error(t.toString());
			return -1;
		}
	}
	
	private int processXML(String xmlContent, boolean cleanFlag) {
		if(xmlContent == null || xmlContent.trim().isEmpty())
		{
			logger.debug("Nothing to update!!");
			return 0;
		}

		if(unmashallActivityLogTrans(xmlContent) == false)
		{
			logger.error("invalid XML Content!!");
			return 1;
		}

		D2DActiveSyncLogSynchronizer syncer = new D2DActiveSyncLogSynchronizer();
		syncer.setBranchid(branchid);

		if(syncer.begin() == false)
		{
			logger.error("SQL ERROR: begin transaction failed");
			return 2;
		}
		
		boolean result = true;
		try {
			if(cleanFlag == true) {//For Full sync, clean all old records
				if(false == syncer.CleanAllActiveLog()) {
					logger.error("SQL error: clean old records!");
					result = false;
					return 2;
				}
			}
			
			List<LogRec> recLst = m_activityLogTrans.getLogRec();
			Iterator<LogRec> iter = recLst.iterator();
			while (iter.hasNext()) {
				if(false == syncer.ProcActiveLog(iter.next()))
				{
					logger.error("SQL execution error!");
					result = false;
					return 2;
				}
			}
		}catch(Throwable t) {
			logger.error(t.toString());
			result = false;
			return -1;
		}finally {
			syncer.end(result);
		}
		
		logger.debug("succeeded!");
		return 0;
	}

	private boolean unmashallActivityLogTrans(String xmlContent) {
		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance("com.ca.arcserve.edge.app.base.webservice.d2dactivelog", ActivityLogTrans.class.getClassLoader());
			Unmarshaller unmarsh = jaxbContext.createUnmarshaller();
			m_activityLogTrans = (ActivityLogTrans) unmarsh.unmarshal(new StreamSource(
					new StringReader(xmlContent)));

		} catch (Exception e) {
			logger.error(this.getClass().getName()+" xmlContent:'"+e.toString()+"'");
			return false;
		}

		return true;
	}
}
