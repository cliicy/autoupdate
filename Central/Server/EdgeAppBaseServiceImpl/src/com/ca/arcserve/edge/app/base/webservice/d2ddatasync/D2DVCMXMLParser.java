package com.ca.arcserve.edge.app.base.webservice.d2ddatasync;

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.edge.datasync.vcm.VCMEventList;
import com.ca.arcflash.webservice.data.edge.datasync.vcm.VCMEventRec;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;

public class D2DVCMXMLParser extends D2DBaseXmlParser {
	private static final Logger logger = Logger.getLogger(D2DVCMXMLParser.class);

	private VCMEventList eventList = null;
	
	public D2DVCMXMLParser() {
	
	}
	
	private boolean unmashall(String xmlContent) {
		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance("com.ca.arcflash.webservice.data.edge.datasync.vcm", VCMEventList.class.getClassLoader());
			Unmarshaller unmarsh = jaxbContext.createUnmarshaller();
			eventList = (VCMEventList) unmarsh.unmarshal(new StreamSource(
					new StringReader(xmlContent)));

		} catch (Exception e) {
			logger.error(this.getClass().getName()+" xmlContent:'"+e.toString()+"'");
			return false;
		}

		return true;
	}
	
	/*
	 * Return Code: 0 succeeded 1 XML parser error 2 SQL operation error
	 */
	public int process(String xmlContent, boolean cleanFlag) {
		try {
			int result = processXML(xmlContent, cleanFlag);
			if(result != 0)
				writeActivityLog(Severity.Error, D2DSyncMessage.EDGE_D2D_SYNC_VCM_EVENT_FAILED);
			else
				writeActivityLog(Severity.Information, D2DSyncMessage.EDGE_D2D_SYNC_VCM_EVENT_SUCCEEDED);
			
			return result;
		}catch(Throwable t) {
			logger.error(t.toString());
			return -1;
		}
	}
	
	private int processXML(String xmlContent, boolean cleanFlag) {
		if(xmlContent == null || xmlContent == "")
		{
			logger.debug("Nothing to update!!");
			return 0;
		}

		if(unmashall(xmlContent) == false)
		{
			logger.debug("invalid XML Content!!");
			return 1;
		}

		D2DVCMSynchronizer syncer = new D2DVCMSynchronizer();
		syncer.setBranchid(branchid);

		boolean needEnd=false;
		boolean transResult=true;
		try {
			if(syncer.begin() == false)
			{
				logger.debug("SQL ERROR: begin transaction failed");
				return 2;
			}
			
			needEnd = true;
			
			if(cleanFlag == true) {//For Full sync, clean all old records
				if(false == syncer.DeleteAllEventsByBranch()) {
					logger.debug("SQL error: clean old records!");
					transResult=false;
					return 2;
				}
			}
			
			List<VCMEventRec> eventRecList = eventList.getVCMEventRec();
			Iterator<VCMEventRec> iter = eventRecList.iterator();
			while (iter.hasNext()) {
				if(false == syncer.UpdateVCMEvent(iter.next()))
				{
					logger.debug("SQL execution error!");
					transResult=false;
					return 2;
				}
			}
		}catch(Throwable t) {
			logger.error(t.toString());
			transResult=false;
			return -1;
		}finally {
			if(needEnd) {
				syncer.end(transResult);
			}
		}
		
		logger.debug("succeeded!");
		return 0;
	}
}
