package com.ca.arcflash.webservice.edge.pfc;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.edge.data.pfc.PFCVMInfo;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.jni.NativeFacadeImpl;
import com.ca.arcflash.webservice.jni.model.JPFCVMInfo;
import com.ca.arcflash.webservice.service.VSphereService;

public class D2DPFCServiceImpl implements ID2DPFCService
{
	private static final Logger logger = Logger.getLogger( D2DPFCServiceImpl.class );
	private static D2DPFCServiceImpl instance = null;
	NativeFacade nativeFacade = new NativeFacadeImpl();;

	public static synchronized D2DPFCServiceImpl getInstance()
	{
		if (instance == null)
			instance = new D2DPFCServiceImpl();
		return instance;
	}

	private long getMajorVer(String version)	{
		int idx = version.indexOf('.');
		if(idx > 0)
			version = version.substring(0, idx);
		return Long.valueOf(version);
	}
	
	@Override
	public long checkVIXVersion() {
		String version = nativeFacade.getVIXVersion();
		long majorVer = getMajorVer(version);
		long checkMajorVer = getMajorVer(VSphereService.VIX_VERSION_REQUIRED);
		if(version == null || version.equals(VSphereService.VIX_NOT_INSTALL)){
			return ID2DPFCService.PFC_CHECK_APP_VIX_NOT_INSTALL;
		}else if(majorVer < checkMajorVer){
			return ID2DPFCService.PFC_CHECK_APP_VIX_OUT_OF_DATE;
		}
		return 0;
	}

	@Override
	public PFCVMInfo getVMInformation(String esxServerName, String esxUserName,
			String esxPassword, String vmName, String vmVMX, String userName,
			String password){
		PFCVMInfo result = new PFCVMInfo();
		JPFCVMInfo vmInfo = new JPFCVMInfo();
		
		// Convert host name to IP ,because VIXxx.exe require a IP.
		long retCode = nativeFacade.getVMInformation(vmInfo , 
				esxServerName, esxUserName, esxPassword, 
				vmName, vmVMX, userName, password);
		if(retCode < 0){
			logger.error( "Error getting VM info. retCode=" +retCode);
			result.setErrorCode(retCode);
		}else{
			result.setErrorCode(retCode);
			result.setVmOSVersion(vmInfo.getVmOSVersion());
			result.setHasDynamicDisk(vmInfo.isHasDynamicDisk());
			result.setExchangeInstalled(vmInfo.isExchangeInstalled());
			result.setSqlserverInstalled(vmInfo.isSqlserverInstalled());
			result.setHasStorageSpaces(vmInfo.isHasStorageSpaces());
		}
		
		return result;
	}
	
	private String convertHostNameToIP(String hostName){
		String ipAddress = hostName;
		try {
			InetAddress addr = InetAddress.getByName(hostName);
			ipAddress = addr.getHostAddress();
		} catch (UnknownHostException e) {
			logger.error( "convertHostNameToIP failed.",e);
		}
	    return ipAddress;
	}

}
