package com.ca.arcflash.failover;

import java.util.Date;
import java.util.UUID;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.common.modelmanager.RepositoryUtil;
import com.ca.arcflash.ha.event.VCMEvent;
import com.ca.arcflash.ha.event.VCMVMType;
import com.ca.arcflash.ha.model.ProductionServerRoot;
import com.ca.arcflash.ha.model.ReplicaRoot;
import com.ca.arcflash.ha.model.VMWareESXHostReplicaRoot;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.failover.VMwareESX;
import com.ca.arcflash.jobscript.failover.Virtualization;

public class VMwareESXFailoverCommand extends VMwareBaseFailoverCommand {
	private static final Logger log = Logger
			.getLogger(VMwareESXFailoverCommand.class);
	private static final long serialVersionUID = 908827158639891754L;

	@Override
	VMWareInfo getVMWareInfo(Virtualization virtualization, String afguid) {

		if (virtualization == null) {
			log.error("Virtualization is null!!!!");
			return null;
		}

		VMWareInfo vmConf = new VMWareInfo();
		VMwareESX vmware = (VMwareESX) virtualization;
		vmConf.hostname = vmware.getHostName();
		vmConf.username = vmware.getUserName();
		vmConf.password = vmware.getPassword();
		vmConf.esxHost = vmware.getHostName();
		vmConf.vmuuid = vmware.getUuid();
		vmConf.vmName = vmware.getVmname();
		vmConf.protocol = vmware.getProtocol();
		vmConf.port = vmware.getPort();

		String xml = CommonUtil.D2DHAInstallPath
				+ "Configuration\\repository.xml";
		ProductionServerRoot prodRoot;
		try {
			prodRoot = RepositoryUtil.getInstance(xml).getProductionServerRoot(
					afguid);
			ReplicaRoot hostRoot = prodRoot.getReplicaRoot();
			VMWareESXHostReplicaRoot esxRoot = (VMWareESXHostReplicaRoot) hostRoot;
			if (esxRoot.getVmuuid() == null || esxRoot.getVmname() == null) {
				log.error("VM UUID and name is not found. Failover stop!!!!!");
			}
			
			if(!StringUtil.isEmptyOrNull(esxRoot.getVmuuid()) 
				&& !esxRoot.getVmuuid().equals(vmConf.vmuuid)){
				log.info("vm install uuid differnt in failoverjobscrip and repository.xml");
				log.info("vm in failover job script. vmuuid=" + vmConf.vmuuid + ";vmname=" + vmConf.vmName);
				log.info("vm in repository. vmuuid=" + esxRoot.getVmuuid() + ";vmname=" + esxRoot.getVmname());
				vmConf.vmuuid = esxRoot.getVmuuid();
				vmConf.vmName = esxRoot.getVmname();
			}
			
		} catch (Exception e) {
		}

		return vmConf;

	}

	VCMEvent getVCMEvent(FailoverJobScript jobScript) {

		VCMEvent event = new VCMEvent();
		event.setTaskGuid(UUID.randomUUID().toString());
		event.setTaskName("Failover Task");
		event.setEndTime(new Date());
		Virtualization virtual = jobScript.getFailoverMechanism().get(0);
		VMwareESX hyperV = (VMwareESX) virtual;
		event.setDestHostName(hyperV.getHostName());
		event.setDestVMType(VCMVMType.VMware);
		event.setDestVMName(hyperV.getVirtualMachineDisplayName());

		ProductionServerRoot prodRoot = null;
		String afguid = jobScript.getAFGuid();
		String xml = CommonUtil.D2DInstallPath
				+ "Configuration\\repository.xml";
		try {
			prodRoot = RepositoryUtil.getInstance(xml).getProductionServerRoot(
					afguid);
			String vmuuid = prodRoot.getReplicaRoot().getVmuuid();
			String vmname = prodRoot.getReplicaRoot().getVmname();
			event.setDestVMUUID(vmuuid);
			event.setDestVMName(vmname);
		} catch (Exception e) {
		}

		return event;

	}

}
