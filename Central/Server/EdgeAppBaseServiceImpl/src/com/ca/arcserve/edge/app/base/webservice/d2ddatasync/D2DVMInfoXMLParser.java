package com.ca.arcserve.edge.app.base.webservice.d2ddatasync;

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.webservice.data.vsphere.TempVMHostInfo;
import com.ca.arcflash.webservice.data.vsphere.TempVMHostList;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcflash.webservice.data.edge.datasync.vsphere.*;

public class D2DVMInfoXMLParser extends D2DBaseXmlParser {
	private static final Logger logger = Logger.getLogger(D2DVMInfoXMLParser.class);

	private VMInfoLst m_vmInfoLst = null;
	
	public D2DVMInfoXMLParser() {
	
	}
	
	private boolean unmashallVMInfoLst(String xmlContent) {
		try {
			JAXBContext jaxbContext = JAXBContext
					.newInstance("com.ca.arcflash.webservice.data.edge.datasync.vsphere", VMInfoLst.class.getClassLoader());
			Unmarshaller unmarsh = jaxbContext.createUnmarshaller();
			m_vmInfoLst = (VMInfoLst) unmarsh.unmarshal(new StreamSource(
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
			int ret = processVMInfoLs(xmlContent, cleanFlag);
			
			if(ret != 0)
				writeActivityLog(Severity.Error, D2DSyncMessage.EDGE_D2D_SYNC_VM_INFO_FAILED);
			else
				writeActivityLog(Severity.Information, D2DSyncMessage.EDGE_D2D_SYNC_VM_INFO_SUCCEEDED);
			
			return ret;
		}catch(Throwable t) {
			logger.error(t.toString());
			return -1;
		}
	}
	
	private int processVMInfoLs(String xmlContent, boolean cleanFlag) {
		if(xmlContent == null || xmlContent.trim().isEmpty())
		{
			logger.debug("Nothing to update!!");
			return 0;
		}

		if(unmashallVMInfoLst(xmlContent) == false)
		{
			logger.error("invalid XML Content!!");
			return 1;
		}

		D2DVMInfoSynchronizer syncer = new D2DVMInfoSynchronizer();
		syncer.setBranchid(branchid);

		boolean needEnd = false;
		boolean transResult = true;
		try {
			if(syncer.begin() == false)
			{
				logger.error("SQL ERROR: begin transaction failed");
				return 2;
			}
			
			needEnd = true;
			
			List<VMInfoXml> recLst = m_vmInfoLst.getVMInfoXml();
			Iterator<VMInfoXml> iter = recLst.iterator();
			while (iter.hasNext()) {
				VMInfoXml vminfoxml = iter.next();
				String vmName = vminfoxml.getVM().getVmName();
				if(vmName == null) vmName = "";
				
				String vmInstUUID = vminfoxml.getVM().getVmInstUUID();
				if(vmInstUUID == null) vmInstUUID = "";
				
				String vmHostName = vminfoxml.getVM().getVmHost();
				if(vmHostName == null) vmHostName = "";
				
				String vCenterName = vminfoxml.getVCenter().getServerName();
				if(vCenterName == null) vCenterName = "";
				
				String esxName = vminfoxml.getN_ESX().getServerName();
				if(esxName == null) vCenterName = "";
				
				long   vmGuestOSTypeValue = vminfoxml.getGuestOS().getOSTypeValue();
				String vmGuestOSTypeString= vminfoxml.getGuestOS().getOSTypeString();
				if(vmGuestOSTypeString == null) vmGuestOSTypeString = "";
				
				String sessGuid = vminfoxml.getSessionUniqueID();
				if(sessGuid == null) sessGuid = "";
				
				int appType = vminfoxml.getAppType();
				
				if(false == syncer.UpdateD2DJobVMInfo(vmName, vmInstUUID, vmHostName, vCenterName, 
						esxName, vmGuestOSTypeValue, vmGuestOSTypeString, appType, sessGuid))
				{
					logger.error("SQL execution error!");
					transResult = false;
					return 2;
				}
			}
		}catch(Throwable t) {
			logger.error(t.toString());
			transResult = false;
		}finally {
			if(needEnd) {
				syncer.end(transResult);
			}
		}
		
		logger.debug("succeeded!");
		return 0;
	}
	
	public int processVMTempHostInfo(String vmTempHostXml , boolean isFullSync) {
		if(vmTempHostXml == null || vmTempHostXml == "")
		{
			logger.info("Nothing to update for vm temp host!");
			return 0;
		}
		
		D2DVMInfoSynchronizer syncer = null;
		TempVMHostList vmTempInforList = null;
		boolean needEnd = false;
		boolean transResult = true;
		
		try {
			vmTempInforList = CommonUtil.unmarshal(vmTempHostXml, TempVMHostList.class);
			if(vmTempInforList == null || vmTempInforList.getVmList().size()==0){
				logger.info("Nothing to update for vm temp host!");
				return 0;
			}
				
			syncer = new D2DVMInfoSynchronizer();
			syncer.setBranchid(branchid);
			
			if(syncer.begin() == false)
			{
				logger.error("SQL ERROR: begin transaction failed");
				return 2;
			}
			
			needEnd = true;
			
			if(isFullSync){ //When full sync , first delete all the vm in the database and then add the new vm list to database. 
				if(false == syncer.DeleteAllTempVMHost()){
					logger.error("SQL execution error!");
					transResult = false;
					return 2;
				}	
			}
			
			List<TempVMHostInfo> recLst = vmTempInforList.getVmList();
			Iterator<TempVMHostInfo> iter = recLst.iterator();
			while (iter.hasNext()) {
				TempVMHostInfo vminfoxml = iter.next();
				
				String vmInstUUID = vminfoxml.getVmInstanceUUID();
				int overAllStatus = vminfoxml.getOverallStatus();
				String vmHostName = vminfoxml.getVmHostName();
				String vmName = vminfoxml.getVmName();
				
				if(false == syncer.UpdateTempVMHostInfo(vmInstUUID, overAllStatus,vmHostName,vmName))
				{
					logger.error("SQL execution error!");
					transResult = false;
					return 2;
				}
			}	
		} catch (JAXBException e) {
			logger.error("processVMTempHostInfo: ",e);
			return 1;
		}catch(Throwable t) {
			logger.error("processVMTempHostInfo: " ,t);
			transResult = false;
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
