package com.ca.arcserve.edge.app.base.webservice.d2ddatasync;

import java.io.StringReader;
import java.util.Iterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.edge.datasync.archive.ArchiveJob;
import com.ca.arcflash.webservice.data.edge.datasync.archive.ArchiveJobList;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;

public class D2DArchiveXMLParser extends D2DBaseXmlParser {
	private static final Logger logger = Logger.getLogger(D2DArchiveXMLParser.class);

	private ArchiveJobList m_archiveJobs = null;
	
	public D2DArchiveXMLParser() {
	
	}
	
	private boolean unmashall(String xmlContent) {
		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance("com.ca.arcflash.webservice.data.edge.datasync.archive", ArchiveJobList.class.getClassLoader());
			Unmarshaller unmarsh = jaxbContext.createUnmarshaller();
			m_archiveJobs = (ArchiveJobList) unmarsh.unmarshal(new StreamSource(
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
				writeActivityLog(Severity.Error, D2DSyncMessage.EDGE_D2D_SYNC_ARCHIVE_JOB_FAILED);
			else
				writeActivityLog(Severity.Information, D2DSyncMessage.EDGE_D2D_SYNC_ARCHIVE_JOB_SUCCEEDED);
			
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

		if(unmashall(xmlContent) == false)
		{
			logger.debug("invalid XML Content!!");
			return 1;
		}
		
		D2DArchiveSynchronizer syncer = new D2DArchiveSynchronizer();
		
		if(false == syncer.begin()){
			logger.debug("begin() failed.");
			return 2;	
		}
		
		boolean result = true;
		try {
			if(cleanFlag){
				try {
					syncer.DelArchiveJobByBranch(branchid);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error(e.toString());
					result = false;
					return 2;
				}
			}
			
			Iterator<ArchiveJob> iter = m_archiveJobs.getArchiveJob().iterator();
			while(iter.hasNext()){
				if(false == syncer.SaveArchiveJob(iter.next(), branchid))
				{
					logger.error("SQL execution error!");
					result=false;
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
}
