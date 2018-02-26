package com.ca.arcserve.edge.app.base.webservice.d2ddatasync;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.webservice.d2djobstatus.JobStatus2Edge;

public class D2DJobStatusXmlParser {
	private static final Logger logger = Logger.getLogger(D2DJobStatusXmlParser.class);

	private JobStatus2Edge m_jobstatus = null;
	
	private int branchid = 0;
	
	public void setBranchid(int value) {
		branchid = value;
	}
	
	public D2DJobStatusXmlParser() {
	
	}

	/*
	 * Return Code: 0 succeeded 1 XML parser error 2 SQL operation error
	 */
	public int processJobStatus(String xmlContent) {
		int ret;
		
		try {
			if(xmlContent == null || xmlContent == "")
			{
				logger.debug("Nothing to update!!");
				return 0;
			}
	
			if(unmashallD2DJobStatus(xmlContent) == false)
			{
				logger.debug("invalid XML Content!!");
				return 1;
			}
			
			D2DJobStatusSynchronizer syncer = new D2DJobStatusSynchronizer();
			syncer.setBranchid(branchid);
			ret = syncer.SyncD2DJobStatus(m_jobstatus);
			if(ret != 0)
			{
				logger.debug("SQL execution error!");
				return 2;
			}
			
			logger.debug("succeeded!");
			return 0;
		}catch(Throwable t) {
			logger.debug(t.toString());
			return -1;
		}
	}

	private boolean unmashallD2DJobStatus(String xmlContent) {
		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance("com.ca.arcserve.edge.app.base.webservice.d2djobstatus", JobStatus2Edge.class.getClassLoader());
			Unmarshaller unmarsh = jaxbContext.createUnmarshaller();
			m_jobstatus = (JobStatus2Edge) unmarsh.unmarshal(new StreamSource(
					new StringReader(xmlContent)));

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}

		return true;
	}
	
	/*
	public static void main(String[] args) {	
        StringWriter sw = new StringWriter();
        JobStatus2Edge jobStatus = new JobStatus2Edge();
        
		JAXBContext jaxbContext;
		try {
			jobStatus.setJobId(1);
			jobStatus.setStartTime("2010-08-05 20:00:01");
			jobStatus.setCurProcessDiskName("C:\\");
			jobStatus.setEstimateBytesDisk(999);
			jobStatus.setEstimateBytesJob(999);
			jobStatus.setFlags(999);
			jobStatus.setJobMethod(2);
			jobStatus.setJobPhase(2);
			jobStatus.setJobStatus(2);
			jobStatus.setJobType(2);
			jobStatus.setSessionID(9);
			jobStatus.setTransferBytesDisk(7777);
			jobStatus.setTransferBytesJob(7777);
			jobStatus.setElapsedTime(678);
			jobStatus.setVolMethod(5);
			
			jaxbContext = JAXBContext.newInstance("com.ca.arcserve.edge.app.base.app.base.webservice.contract.d2djobstatus");
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			marshaller.marshal(jobStatus, sw);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
		}
		
        String xmlContent = sw.toString();
        
        System.out.print("XMLContent is: " + xmlContent + "\n");
        
        D2DJobStatusXmlParser parser = new D2DJobStatusXmlParser();
        
        int ret = parser.processJobStatus(xmlContent);
        System.out.print("ret = " + ret + "\n");
        if(ret == 1)
        	System.out.print("xml parser error\n");
        else if(ret == 2)
        	System.out.print("SQL error\n");
        else
        	System.out.print("succeeded\n");
        
	}
	*/
}
