package com.ca.arcflash.failover;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.JAXB;

import org.apache.log4j.Logger;

import com.ca.arcflash.failover.model.ADRConfigure;
import com.ca.arcflash.failover.model.Volume;
import com.ca.arcflash.instantvm.AdvancedIPConfig;
import com.ca.arcflash.instantvm.AlternateIPConfig;
import com.ca.arcflash.instantvm.DNSUpdaterParameter;
import com.ca.arcflash.instantvm.Gateway;
import com.ca.arcflash.instantvm.HyperVInfo;
import com.ca.arcflash.instantvm.HypervisorType;
import com.ca.arcflash.instantvm.IPAddressInfo;
import com.ca.arcflash.instantvm.InstantVMConfig;
import com.ca.arcflash.instantvm.InstantVMNode;
import com.ca.arcflash.instantvm.NetworkInfo;
import com.ca.arcflash.instantvm.VMInfo;
import com.ca.arcflash.instantvm.VMWareInfo;
import com.ca.arcflash.instantvm.VolumeDriveLetterMap;
import com.ca.arcflash.jobscript.failover.DNSUpdaterParameters;
import com.ca.arcflash.jobscript.failover.FailoverCommand;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.failover.HyperV;
import com.ca.arcflash.jobscript.failover.IPSetting;
import com.ca.arcflash.jobscript.failover.VMwareESX;
import com.ca.arcflash.jobscript.failover.VMwareVirtualCenter;
import com.ca.arcflash.jobscript.failover.VirtualizationType;
import com.ca.arcflash.webservice.WebServiceClientProxy;
import com.ca.arcflash.webservice.replication.MachineDetailManager;
import com.ca.arcflash.webservice.service.HAService;

public class PreFailoverCommand extends FailoverCommand {
	
	private static final Logger log = Logger.getLogger(PreFailoverCommand.class);
	private static final long serialVersionUID = -5508937673663162199L;
	
	private static final String INSTANT_VM_HELPER_SCRIPT = "InstantVMHelperScript.xml";
	
	@Override
	public boolean preExecuteFailover(String jobID, FailoverJobScript jobScript,
			String sessionGuid, boolean autoFailover) {
		if (autoFailover && !HAService.getInstance().ifNeedExecuteAutoFailover(jobScript, new Date())){
			return false;
		}
		try {
			WebServiceClientProxy converter = HAService.getInstance().getConverterService(jobScript);
			if (converter != null) {
				log.info("Try cancelling the current conversion job.");
				converter.getFlashServiceR16_5().cancelReplicationSync(jobScript.getAFGuid());
			}
		} catch (Exception e) {
			log.error("PreExecuteFailOver error: ", e);
		}
		return true;
	}
	
	public String PrepareInstantVMHelperScript(FailoverJobScript jobScript, List<IPSetting> ipSettings, ADRConfigure adrConfigure) {
		InstantVMConfig instantVMHelperScript = new InstantVMConfig();
		InstantVMNode node = new InstantVMNode();
		VMInfo vmInfo = new VMInfo();
		List<NetworkInfo> networks = new ArrayList<NetworkInfo>();
		
		for (IPSetting ips : ipSettings) {
			NetworkInfo network = new NetworkInfo();
			network.setVirtualNetwork(ips.getVirtualNetwork());
			network.setAdapterType(ips.getNicType());
			network.setUseDHCP(ips.isDhcp());
			network.setDynamicDNS(ips.isAutoDNS());
			AdvancedIPConfig advancedIPConfig = new AdvancedIPConfig();
			advancedIPConfig.setDnses(ips.getDnses());
			advancedIPConfig.setWins(ips.getWins());
			List<Gateway> gateways = new ArrayList<Gateway>();
			List<IPAddressInfo> ipAddressInfos = new ArrayList<IPAddressInfo>();
			
			// Set gateways
			for (com.ca.arcflash.jobscript.failover.Gateway g : ips.getGateways()) {
				Gateway gateway = new Gateway();
				gateway.setGatewayAddress(g.getGatewayAddress());
				gateway.setGatewayMetric(g.getGatewayMetric());
				gateways.add(gateway);
			}
			advancedIPConfig.setGateways(gateways);
			
			// Set IP
			for (com.ca.arcflash.jobscript.failover.IPAddressInfo ip : ips.getIpAddresses()) {
				IPAddressInfo ipInfo = new IPAddressInfo();
				ipInfo.setIp(ip.getIp());
				ipInfo.setSubnet(ip.getSubnet());
				ipAddressInfos.add(ipInfo);
			}
			advancedIPConfig.setIpAddresses(ipAddressInfos);
			network.setAdvancedIPConfig(advancedIPConfig);
			
			AlternateIPConfig alternateIPConfig = new AlternateIPConfig();
			for (int j = 0; j < ips.getWins().size(); j++) {	
				if (j == 0)
					alternateIPConfig.setPreferredWINS(ips.getWins().get(j));
				if (j == 1)
					alternateIPConfig.setAlternateWINS(ips.getWins().get(j));
			}
			network.setAlternateIPConfig(alternateIPConfig);
			networks.add(network);
		}

		vmInfo.setNetworkInfo(networks);
		
		// Save dns update parameter
		List<DNSUpdaterParameters> dnsParamaters = jobScript.getDnsParameters();
		if (dnsParamaters != null) {
			List<DNSUpdaterParameter> params = new ArrayList<DNSUpdaterParameter>();
			for (com.ca.arcflash.jobscript.failover.DNSUpdaterParameters d : dnsParamaters) {
				DNSUpdaterParameter param = new DNSUpdaterParameter();
				// encrypt useing dns password
				if (d.getCredential() != null) {
					String plainText = HAService.getInstance().getNativeFacade()
							.decrypt(d.getCredential());
					param.setCredential(HAService.getInstance().getNativeFacade()
							.EncryptDNSPassword(plainText));
				}
				param.setDns(d.getDns());
				param.setDnsServerType(d.getDnsServerType());
				param.setHostIp(d.getHostIp());
				param.setHostname(d.getHostname());
				param.setKeyFile(d.getKeyFile());
				param.setTtl(d.getTtl());
				param.setUsername(d.getUsername());
				params.add(param);
			}
			vmInfo.setDnsParameters(params);
		}
		
		// Save volume map
		if (adrConfigure != null) {
			List<VolumeDriveLetterMap> volumeDriveLetterMap = new ArrayList<VolumeDriveLetterMap>();
			for (Volume v : adrConfigure.getVolumes()) {
				if (v.getDriveLetter() == null || v.getDriveLetter().length() == 0)
					continue;
				volumeDriveLetterMap.add(new VolumeDriveLetterMap(v.getDriveLetter() + ":\\", v.getGuidPath()));
			}
			vmInfo.setVolumeDriveLetterMap(volumeDriveLetterMap);
		}
		
		//This VmUUID is set to the AFGuid value which is just used to conform to the Instant VM Job script and is meanless.
		//The CurrentNodeUUID should equal to the VmUUID.
		vmInfo.setVmUUID(jobScript.getAFGuid());
		node.setVmInfo(vmInfo);
		List<InstantVMNode> instantVMNodes = new ArrayList<InstantVMNode>();
		instantVMNodes.add(node);
		
		// Set Hypervisor type and info
		if (jobScript.getVirtualType() == VirtualizationType.HyperV) {
			HyperV hyperv = (HyperV)jobScript.getFailoverMechanism().get(0);
			instantVMHelperScript.setHypervisorType(HypervisorType.HYPERV);
			HyperVInfo hypervInfo = new HyperVInfo();
			hypervInfo.setCluster(false);
			hypervInfo.setHostname(hyperv.getHostName());
			hypervInfo.setPassword(hyperv.getPassword());
			hypervInfo.setUserName(hyperv.getUserName());
			instantVMHelperScript.setHypervisorInfo(hypervInfo);
		}
		else {
			instantVMHelperScript.setHypervisorType(HypervisorType.VMWARE);
			VMWareInfo vmwareInfo = new VMWareInfo();
			if (jobScript.getVirtualType() == VirtualizationType.VMwareESX) {
				VMwareESX vmwareEsx = (VMwareESX)jobScript.getFailoverMechanism().get(0);
				vmwareInfo.setCluster(false);
				vmwareInfo.setHostname(vmwareEsx.getHostName());
			} else if (jobScript.getVirtualType() == VirtualizationType.VMwareVirtualCenter) {
				VMwareVirtualCenter vc = (VMwareVirtualCenter)jobScript.getFailoverMechanism().get(0);
				vmwareInfo.setCluster(false);
				vmwareInfo.setHostname(vc.getHostName());
			}
		}		
		
		instantVMHelperScript.setInstantVMNodes(instantVMNodes);
		instantVMHelperScript.setCurrentNodeUUID(jobScript.getAFGuid());
		
		// Save to xml
		File xmlLocation = new File(MachineDetailManager.detailPath);
		if(!xmlLocation.exists()){
			xmlLocation.mkdirs();
		}

		String xmlFileName = jobScript.getAFGuid() + "-" + INSTANT_VM_HELPER_SCRIPT;
		log.info("Save instant vm helper script to xml file: " + xmlFileName);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(xmlLocation, xmlFileName));
			JAXB.marshal(instantVMHelperScript, fos);
			log.info("Save to xml file successfully, file name is " + xmlFileName);
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
			return null;
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}
		return xmlLocation.getAbsolutePath() + "\\" + xmlFileName;
	}
}
