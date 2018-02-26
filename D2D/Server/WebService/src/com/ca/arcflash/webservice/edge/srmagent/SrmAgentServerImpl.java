package com.ca.arcflash.webservice.edge.srmagent;

import java.util.Date;
import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.data.backup.SRMPkiAlertSetting;


public class SrmAgentServerImpl{
		
	private static final Logger logger = Logger.getLogger(SrmAgentServerImpl.class);
	
	public static String GetSrmInfo(int command){
		try {
			if ( !SrmJniCaller.isSRMEnabled() ) {
				Date time = new Date();
				logger.debug(time.toString() + ": SRM is disabled on local machine!");
				return "";
			}
			
			switch ( command ){
			
				case SrmCommand.GET_HARDWARE_INFO: {
					String hardwareInfo = SrmJniCaller.getSysHardwareInfo();
					return hardwareInfo==null ? "" : hardwareInfo;
				}
					
				case SrmCommand.GET_SOFTWARE_INFO: {
					String  softwareInfo = SrmJniCaller.getSysSoftwareInfo();
					return softwareInfo==null ? "" : softwareInfo;
				}
					
				case SrmCommand.GET_SERVERPKI_INFO: {
					String  serverPkiInfo = SrmJniCaller.getServerPkiInfo(1);
					return serverPkiInfo==null ? "" : serverPkiInfo;
				}
					
				case SrmCommand.GET_HARDWARE_INFO | SrmCommand.GET_SOFTWARE_INFO: {
					String hardwareInfo = SrmJniCaller.getSysHardwareInfo();
					String softwareInfo = SrmJniCaller.getSysSoftwareInfo();
					StringBuilder xmlContent = new StringBuilder();
					
					xmlContent = composeXmlBuffer(xmlContent, hardwareInfo, SrmCommand.GET_HARDWARE_INFO);
					xmlContent = composeXmlBuffer(xmlContent, softwareInfo, SrmCommand.GET_SOFTWARE_INFO);
						
					return xmlContent.toString();
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage() == null ? e : e.getMessage());
				Date time = new Date();
				logger.error(time.toString() + ": Exception happens when getting SRM info(" + command + ") <" + e.getMessage()+">");
				return "";
		}
		
		Date time = new Date();
		logger.error(time.toString() + ": Wrong command to get SRM info(" + command + ")");
		return "";
	}
	
	public static int SaveAlertSetting(SRMPkiAlertSetting setting) {
		try {
			return SrmJniCaller.savePkiAlertPolicy(setting);
		}catch (Exception e) {
			Date time = new Date();
			logger.error(time.toString() + ": Exception when saving alert settings <" + e.getMessage() + ">");
			return -1;
		}
	}
	
	/*
	 *  The xml buffer format for multiple xml is:
	 *  "srmInfoType srmXmlLenghth srmXmlContent""srmInfoType srmXmlLenghth srmXmlContent".....
	 * */
	private static StringBuilder composeXmlBuffer(StringBuilder xmlContent, String srmXmlInfo, int srmInfoType) {
		if ( srmXmlInfo != null && srmXmlInfo.length() > 0 ) {
		    xmlContent.append(srmInfoType);
		    xmlContent.append(' ');
		    xmlContent.append(srmXmlInfo.length());
			xmlContent.append(' ');
			xmlContent.append(srmXmlInfo);
		}
		
		return xmlContent;
	}
}
