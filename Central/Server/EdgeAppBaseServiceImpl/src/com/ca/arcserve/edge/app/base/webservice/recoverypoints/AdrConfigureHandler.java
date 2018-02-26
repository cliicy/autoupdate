package com.ca.arcserve.edge.app.base.webservice.recoverypoints;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.ca.arcflash.failover.model.ADRConfigure;
import com.ca.arcflash.failover.model.Volume;
import com.ca.arcflash.webservice.foredge.ID2D4EdgeService_Oolong;
import com.ca.arcserve.edge.app.base.common.D2DServiceUtils;
import com.ca.arcserve.edge.app.base.common.connection.D2DConnection;
import com.ca.arcserve.edge.app.base.common.connection.LinuxD2DConnection;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.destination.DestinationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PlanDestinationType;
import com.ca.arcserve.edge.app.base.webservice.contract.recoverypoint.MachineConfigure;
import com.ca.arcserve.edge.app.base.webservice.contract.recoverypoint.RecoveryPointInformationForCPM;
import com.ca.arcserve.edge.app.base.webservice.instantvm.InstantVMServiceUtil;
import com.ca.arcserve.linuximaging.webservice.ILinuximagingService;
import com.ca.arcserve.linuximaging.webservice.data.BackupLocationInfo;
import com.ca.arcflash.jobscript.failover.Gateway;
import com.ca.arcflash.jobscript.failover.IPAddressInfo;
import com.ca.arcflash.jobscript.failover.IPSetting;
import com.ca.arcflash.jobscript.failover.NetworkAdapter;

public class AdrConfigureHandler {
	private static final Logger logger = Logger.getLogger( AdrConfigureHandler.class );
	public MachineConfigure handleAdrConfigure(  RecoveryPointInformationForCPM rpWithNode ) throws EdgeServiceFault {
		if(rpWithNode == null)
			return null;
		if(rpWithNode.isWindowsSession()) {
			ADRConfigure configure = null;
			try (D2DConnection conn = RecoveryPointBrowseUtil.getInstance().getDestinationBrowserAgentService(rpWithNode.getBrowser()) ) {
				conn.connect();
				ID2D4EdgeService_Oolong service = conn.getService();
				DestinationInfo destInfo = RecoveryPointBrowseUtil.getInstance().getDestinationWithPasswordForWin( rpWithNode.getBrowser() );
				String[] parsedUsername = D2DServiceUtils.parseUsername(destInfo.getUserName()); 
				String sessionNum = RecoveryPointBrowseUtil.getInstance().parseSessionFromRecoveryPointPath( rpWithNode );
				configure = service.getADRConfig( rpWithNode.getNodeBackupDestination(), sessionNum, parsedUsername[1], destInfo.getPassword() , parsedUsername[0] );
			}
			logger.debug("windows agent getADRConfig() return: "+InstantVMServiceUtil.printObject(configure));
			return convertToMachineConfiguration( configure, rpWithNode.getProtectedNode().getNodeName());
		}else {//linux node or HBBU linux vm
			try (LinuxD2DConnection linuxConn = RecoveryPointBrowseUtil.getInstance().getLinuxDestinationBrowser(rpWithNode.getBrowser())) {				
				ILinuximagingService linuxServer = linuxConn.getService();
				BackupLocationInfo locationInfo = RecoveryPointBrowseUtil.getInstance().getDestinationWithPasswordForLinux(rpWithNode.getBrowser());
				
				com.ca.arcserve.linuximaging.webservice.data.RecoveryPoint linuxRecoveryPoint = linuxServer.getRecoveryPointFromSession(locationInfo, 
						RecoveryPointBrowseUtil.getInstance().convertToLinuxRecoveryPoint(rpWithNode.getRecoveryPoint()), 
						getMachineName(rpWithNode));
				
				logger.debug("linux server getRecoveryPointFromSession() return: "+InstantVMServiceUtil.printObject(linuxRecoveryPoint));
				return convertToMachineConfigurationLinux(linuxRecoveryPoint, rpWithNode.getProtectedNode().getNodeName());
			}
		}
	}
	
	private String getMachineName(RecoveryPointInformationForCPM rpWithNode){
		if(rpWithNode.getBrowser().getDestinationType() == PlanDestinationType.RPS){
//			return rpWithNode.getRecoveryPoint().getNodeUuid();
			int index = rpWithNode.getProtectedNode().getDestination().lastIndexOf("\\");
			String machineName = rpWithNode.getProtectedNode().getDestination().substring(index+1);
			return machineName;
		}else{
			return rpWithNode.getProtectedNode().getNodeName();
		}
	}
	
	private MachineConfigure convertToMachineConfiguration( ADRConfigure configure, String nodeName ) {
		if( configure == null ) {
			logger.warn("protected Node: " + nodeName  + "has empty ADRConfigure");
			return null;
		}
		else {
			MachineConfigure machineConfigure = new MachineConfigure();
			if( configure.getNetadapters() == null ) {
				logger.warn("the network adapter "+ nodeName +" is empty! " );
			}
			machineConfigure.setCpuCount( (int)configure.getCpu() );
			machineConfigure.setMemoryMBSize( (int)configure.getMemory() );
			
			//if dhcp is true, clear gateway.
			if(configure.getNetadapters()!=null){
				for(NetworkAdapter adapter : configure.getNetadapters()){
					for(IPSetting sett : adapter.getIpSettings()){
						if(sett.isDhcp()){
							sett.setGateways(new ArrayList<Gateway>());
						}
					}
					
				} 
			}
			
			machineConfigure.setNetAdapters(configure.getNetadapters());
			if (null != configure.getVolumes()) {
				boolean hasBootVol = false;
				boolean hasSysVol = false;
				for (Volume volumn : configure.getVolumes()) {
					if (null == volumn || !volumn.isBackuped()) {
						continue;
					}					
					if ((volumn.getFlag() & Volume.VOLUME_FLAG_BOOT_VOLUME) == Volume.VOLUME_FLAG_BOOT_VOLUME) {
						hasBootVol = true;			
					}
					if ((volumn.getFlag() & Volume.VOLUME_FLAG_SYSTEM_VOLUME) == Volume.VOLUME_FLAG_SYSTEM_VOLUME) {
						hasSysVol = true;
					}
				}
				machineConfigure.setHasBootVol(hasBootVol);
				machineConfigure.setHasSysVol(hasSysVol);
			}

			return machineConfigure;
		}
	}

	private MachineConfigure convertToMachineConfigurationLinux(com.ca.arcserve.linuximaging.webservice.data.RecoveryPoint linuxRecoveryPoint, String nodeName){
		if(linuxRecoveryPoint == null){
			logger.warn("protected Node: " + nodeName  + "has empty RecoveryPoint");
			return null;
		}
		MachineConfigure machineConfigure = new MachineConfigure();
		machineConfigure.setCpuCount(linuxRecoveryPoint.getCpuCount());
		machineConfigure.setMemoryMBSize(linuxRecoveryPoint.getMemory());
		machineConfigure.setVersion(linuxRecoveryPoint.getVersion());
		if (linuxRecoveryPoint.getBootVolumeExistFlag() > 0 && linuxRecoveryPoint.getBootVolumeBackupFlag() == 0) {
			machineConfigure.setHasBootVol(false);
		}
		if (linuxRecoveryPoint.getRootVolumeBackupFlag() == 0) {
			machineConfigure.setHasSysVol(false);
		}
		if(linuxRecoveryPoint.getNetworkInfoList()!=null && linuxRecoveryPoint.getNetworkInfoList().size()!=0){
			List<NetworkAdapter> adapters = new ArrayList<NetworkAdapter>();
			for(com.ca.arcserve.linuximaging.webservice.data.restore.NetworkInfo info : linuxRecoveryPoint.getNetworkInfoList()){
				if(info!=null){
					NetworkAdapter adapter = new NetworkAdapter();
					List<IPSetting> setts = new ArrayList<IPSetting>();
					IPSetting sett = new IPSetting();
					sett.setDhcp(info.isDHCP());
					
					List<String> dns = new ArrayList<String>();
					dns.add(info.getDns());
					sett.setDnses(dns);
					
					if(!info.isDHCP()){
						List<Gateway> gateways = new ArrayList<Gateway>();
						Gateway gateway = new Gateway();
						gateway.setGatewayAddress(info.getGateway());
						gateways.add(gateway);
						sett.setGateways(gateways);
						
						List<IPAddressInfo> ipAddresses = new ArrayList<IPAddressInfo>();
						IPAddressInfo ip = new IPAddressInfo();
						ip.setIp(info.getIpAddress());
						ip.setSubnet(info.getNetmask());
						ipAddresses.add(ip);
						sett.setIpAddresses(ipAddresses);
					}
					
					setts.add(sett);
					adapter.setIpSettings(setts);
					adapter.setAdapterType(info.getNetworkAdapterType());
					if(info.isLegacy()){
						adapter.setAdapterType("Legacy Network Adapter");
					}
					adapter.setAdapterName(UUID.randomUUID().toString());
					adapters.add(adapter);
				}
			}
			
			machineConfigure.setNetAdapters(adapters);
		}
		return machineConfigure;
	}
}
