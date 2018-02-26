package com.ca.arcflash.webservice.servlet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.failover.NetworkAdapter;
import com.ca.arcflash.jobscript.failover.Virtualization;
import com.ca.arcflash.webservice.jni.NativeFacade;
import com.ca.arcflash.webservice.service.BackupService;

class ConfigNetworkAdatperThread extends Thread{
	private static final Logger logger = Logger.getLogger(ContextListener.class);
	private String failoverJobScriptString="";
	
	public ConfigNetworkAdatperThread(){
		super();
	}
	
	public boolean Init(){
		logger.info("configureNetworkAdapter.");

		try {
			logger.info("Unmarshal failover jobscript.");
			failoverJobScriptString = CommonUtil.getFailoverJobScript();
			if(StringUtil.isEmptyOrNull(failoverJobScriptString)){
				logger.info("No failover jobscript is injected into registry.");
				return false;
			}
			else{
				logger.info("Succesfully get the fialover jobscript!");
				return true;
			}
		}catch (Exception e) {
			logger.error("Failed to get failover jobscript from registry.");
			return false;
		}
	}
	public void run(){
		try{
			if(StringUtil.isEmptyOrNull(failoverJobScriptString)){
				logger.warn("The failover script is emtpy!");
				return;
			}
			
			FailoverJobScript failoverJobScript = CommonUtil.unmarshal(failoverJobScriptString, FailoverJobScript.class);
			if(failoverJobScript.getFailoverMechanism().size() == 0){
				logger.info("No failover mechanism.");
				return;
			}
			
			Virtualization virtualType = failoverJobScript.getFailoverMechanism().get(0);
			//VirtualizationType type = virtualType.getVirtualizationType();
			List<NetworkAdapter> adapters = null;
			
			adapters = virtualType.getNetworkAdapters();
			
			if(adapters == null){
				logger.error("adaters is null in replication jobscript.");
				return;
			}
			
			for(int m=0;m<60;m++){
				logger.info("Try to get the network adapter:"+m+" time");
				sleep(30*1000); //sleep 30 seconds
				
				NativeFacade facade = BackupService.getInstance().getNativeFacade();
				
				Map<String, String> machineAdapters = facade.GetHostAdapterList();
				if(machineAdapters == null || machineAdapters.size() == 0){
					logger.error("Can not get adapter list from vm:"+m+" time");
					continue;
				}
				
				String[] machineAdapterNames = machineAdapters.values().toArray(new String[0]);
				
				for(Entry<String, String> entry : machineAdapters.entrySet()){
					logger.info(entry.getKey());
					logger.info(entry.getValue());
				}
				logger.info("Configure network.");
				int adapterCount = machineAdapterNames.length < adapters.size() ? machineAdapterNames.length : adapters.size();
				for(int i=0; i<adapterCount; i++){
					
					NetworkAdapter adapter = adapters.get(i);
					String adapterName = machineAdapterNames[i];
					logger.info("Begin set the network adapter:"+adapterName);
					if(adapter.isDynamicIP()){
						logger.info("The IP is DHCP");
						facade.EnableHostDHCP(adapterName);
					}else {
						logger.info("The IP is static IP");
						List<String> ipAddresses = adapter.getIP();
						List<String> ipMasks = new ArrayList<String>();
						ipMasks.add(adapter.getSubnetMask());
						facade.EnableHostStatic(adapterName, ipAddresses, ipMasks);
						
						List<String> gateways = new ArrayList<String>();
						gateways.add(adapter.getGateway());
						facade.SetHostGateways(adapterName, gateways, new ArrayList<Integer>());
					}
					if(adapter.isDynamicDNS()){
						logger.info("The DNS is dynamic");
						facade.EnableHostDNS(adapterName);
					}
					else{
						List<String> dnses = new ArrayList<String>();
						dnses.add(adapter.getPreferredDNS());
						dnses.add(adapter.getAlternateDNS());
						logger.info("Begin to the set the static DNS: Preferred->"+
								adapter.getPreferredDNS()+" AleterDNS:"+adapter.getAlternateDNS());
						facade.SetHostDNSServerSearchOrder(adapterName, dnses);	
					}

				}
				return;
			}

		} catch (Exception e) {
			logger.error("Failed to configure nic card");
			logger.error(e.getMessage());
		}
	}
	
}
