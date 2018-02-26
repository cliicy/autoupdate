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
import com.ca.arcflash.ha.model.VMWareVirtualCenterReplicaRoot;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.failover.VMwareVirtualCenter;
import com.ca.arcflash.jobscript.failover.Virtualization;

public class VMwareCenterServerFailoverCommand extends
		VMwareBaseFailoverCommand {

	private static final Logger log = Logger
			.getLogger(VMwareCenterServerFailoverCommand.class);
	private static final long serialVersionUID = 7133688884356086077L;

	@Override
	VMWareInfo getVMWareInfo(Virtualization virtualization, String afguid) {

		if (virtualization == null) {
			log.error("Virtualization is null!!!!");
			return null;
		}

		VMWareInfo vmConf = new VMWareInfo();

		VMwareVirtualCenter vCenter = (VMwareVirtualCenter) virtualization;
		vmConf.hostname = vCenter.getESXHostName();
		vmConf.username = vCenter.getUserName();
		vmConf.password = vCenter.getPassword();
		vmConf.esxHost = vCenter.getEsxName();
		vmConf.vmuuid = vCenter.getUuid();
		vmConf.vmName = vCenter.getVmname();
		vmConf.protocol = vCenter.getProtocol();
		vmConf.port = vCenter.getPort();

		String xml = CommonUtil.D2DHAInstallPath
				+ "Configuration\\repository.xml";
		
		ProductionServerRoot prodRoot;
		try {
			
			prodRoot = RepositoryUtil.getInstance(xml).getProductionServerRoot(
					afguid);
			ReplicaRoot hostRoot = prodRoot.getReplicaRoot();

			VMWareVirtualCenterReplicaRoot vCenterRoot = (VMWareVirtualCenterReplicaRoot) hostRoot;
			if (vCenterRoot.getVmuuid() == null
					|| vCenterRoot.getVmname() == null) {
				log.error("VM UUID and name is not found. Failover stop!!!!!");
			}
			
			if(!StringUtil.isEmptyOrNull(vCenterRoot.getVmuuid()) 
				&& !vCenterRoot.getVmuuid().equals(vmConf.vmuuid)){
				log.info("vm install uuid differnt in failoverjobscrip and repository.xml");
				log.info("vm in failover job script. vmuuid=" + vmConf.vmuuid + ";vmname=" + vmConf.vmName);
				log.info("vm in repository. vmuuid=" + vCenterRoot.getVmuuid() + ";vmname=" + vCenterRoot.getVmname());
				vmConf.vmuuid = vCenterRoot.getVmuuid();
				vmConf.vmName = vCenterRoot.getVmname();
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
		VMwareVirtualCenter hyperV = (VMwareVirtualCenter) virtual;
		event.setDestHostName(hyperV.getHostName());
		event.setDestVMType(VCMVMType.VirtualCenter);
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
