package com.ca.arcserve.edge.app.base.webservice.linux;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.soap.SOAPFaultException;

import com.ca.arcflash.webservice.edge.license.LicenseDef;
import com.ca.arcflash.webservice.edge.license.LicenseCheckResult;
import com.ca.arcflash.webservice.edge.license.MachineInfo;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.util.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.linuximaging.webservice.data.license.LicenseResult;
import com.ca.arcserve.linuximaging.webservice.data.license.LicensedMachine;
import com.ca.arcserve.linuximaging.webservice.data.license.LicenseResult.LicenseComponent;
import com.ca.arcserve.linuximaging.webservice.data.license.LicenseResult.LicenseStatus;


public class EdgeService4LinuxD2DUtil {
	
	public static SOAPFaultException generateSOAPFaultException(String message, String faultcode){
		SOAPFactory fac;
		SOAPFault soapFault = null;
		try {
			fac = SOAPFactory.newInstance();
			soapFault = fac.createFault();
			String localPart = faultcode;
			if(localPart!=null)
				soapFault.setFaultCode(localPart);
			String messg = message;
			if(messg==null) messg ="";
				soapFault.setFaultString(messg);
		} catch (SOAPException e) {
			// TODO Auto-generated catch block
		}
		SOAPFaultException jf = new SOAPFaultException(soapFault);
		
		return jf;
	}
	
	public static int getD2DHostId(String uuid) {
		int[] rhostid = new int[1];
		String[] hostname = new String[1];
		String[] protocol = new String[1];
		int[] port = new int[1];
		
		GetConnInfoByUUID(uuid, rhostid, hostname, protocol, port);
		
		return rhostid[0];
	}
	
	private static int GetConnInfoByUUID(String uuid, int[] rhostid,
			String[] hostname, String[] protocol, int[] port) {
		int result = 0;
		int[] protocolN = new int[1];

		IEdgeConnectInfoDao connInfoDao = DaoFactory
				.getDao(IEdgeConnectInfoDao.class);
		result = connInfoDao.as_edge_GetConnInfoByUUID(uuid, rhostid, hostname,
				protocolN, port);
		if (result == 0) {
			if (protocolN[0] == Protocol.Https.ordinal())
				protocol[0] = "https";
			else
				protocol[0] = "http";
		}

		return result;
	}
	
	public static MachineInfo convertToMachineInfo(LicensedMachine node){
		if (node == null) {
			return null;
		}
		MachineInfo machine = new MachineInfo();
		machine.setHostName(node.getHostname());
		machine.setHostUuid(node.getHostname());
		machine.setServerName(node.getD2dserver());
		machine.setSocketCount(node.getSocketNumber());
		machine.setServerSocketCount(node.getSocketNumber());
		return machine;
	}
	
	public static long convertToRequiredFeature(int machinetype){
		long required_feature = LicenseDef.SUBLIC_OS_SERVER;
		if (machinetype == LicensedMachine.TYPE_PM) {
			required_feature = LicenseDef.SUBLIC_OS_SERVER | LicenseDef.SUBLIC_OS_PM;
		}
		return required_feature;
	}
	
	public static LicenseResult convertToLicenseResult(LicenseCheckResult result){
		if(result==null||result.getLicense()==null){
			return new LicenseResult(LicenseComponent.Basic, LicenseStatus.TERMINATE);
		}
		LicenseStatus status = LicenseStatus.VALID;
		if(result.getState()!=null){
			switch (result.getState()) {
			case Trial:
				status = LicenseStatus.TRIAL;
				break;
			case Valid:
				status = LicenseStatus.VALID;
				break;
//			case Will_EXPIRE:
//				return LicenseStatus.WILL_EXPIRE;
//				break;
			case Expired:
				status = LicenseStatus.EXPIRED;
				break;
			default:
				status = LicenseStatus.VALID;
			}
		}
		
		return new LicenseResult(LicenseComponent.parseComponentCode(result.getLicense().getCode()), status);
	}
	
	public static int getLinuxNodeHostId(String nodeName) {
		int rhostid = getD2DHostId(nodeName);
		if(rhostid==0){
			int[] targetHostId = new int[1];
			IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
			hostMgrDao.as_edge_host_getIdByHostnameForLinux(nodeName,null,1, targetHostId);
			rhostid=targetHostId[0];
		}
		
		return rhostid;
	}
	
	public static String getRealLinuxNodeName(String nodeName) {
		if (StringUtil.isEmptyOrNull(nodeName)) {			
			return "";
		} else {
			String [] nameArray = nodeName.split(":");
			return nameArray[0];
		}
	}

}
