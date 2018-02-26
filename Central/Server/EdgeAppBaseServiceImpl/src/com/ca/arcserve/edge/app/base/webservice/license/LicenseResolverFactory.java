package com.ca.arcserve.edge.app.base.webservice.license;

import com.ca.arcflash.webservice.edge.license.LicenseDef;
import com.ca.arcflash.webservice.edge.license.LicenseDef.UDP_CLIENT_TYPE;
import com.ca.arcflash.webservice.edge.license.MachineInfo;
import com.ca.arcserve.edge.app.base.webservice.license.resolver.HbbuClientResolver;
import com.ca.arcserve.edge.app.base.webservice.license.resolver.LinuxAgentResolver;
import com.ca.arcserve.edge.app.base.webservice.license.resolver.LinuxHypervisorAgentResolver;
import com.ca.arcserve.edge.app.base.webservice.license.resolver.WindowsAgentNceResolver;
import com.ca.arcserve.edge.app.base.webservice.license.resolver.WindowsAgentResolver;
import com.ca.arcserve.edge.app.base.webservice.license.resolver.WindowsHypervisorAgentResolver;

public class LicenseResolverFactory {
	private static ILicenseResolver windows_agent=new WindowsAgentResolver(LicenseModuleWrapper.getInstance(), LicenseLocalWrapper.getInstance());
	private static ILicenseResolver windows_hypervisor_agent=new WindowsHypervisorAgentResolver(LicenseModuleWrapper.getInstance(), LicenseLocalWrapper.getInstance());
	private static ILicenseResolver hbbu=new HbbuClientResolver(LicenseModuleWrapper.getInstance(), LicenseLocalWrapper.getInstance());
	private static ILicenseResolver linux=new LinuxAgentResolver(LicenseModuleWrapper.getInstance(), LicenseLocalWrapper.getInstance());
	private static ILicenseResolver linux_hypervisor=new LinuxHypervisorAgentResolver(LicenseModuleWrapper.getInstance(), LicenseLocalWrapper.getInstance());
	private static WindowsAgentNceResolver nce=new WindowsAgentNceResolver(LicenseModuleWrapper.getInstance(), LicenseLocalWrapper.getInstance());

	public static ILicenseResolver getResolver(UDP_CLIENT_TYPE type, MachineInfo machine, long required_feature) {
		switch(type){
		case UDP_WINDOWS_AGENT:
			if(EdgeLicenseUtil.hasFeature(required_feature, LicenseDef.SUBLIC_OS_HYPERV))
				return windows_hypervisor_agent;
			else
				return windows_agent;
		case UDP_CLIENT_HBBU:
			return hbbu;
		case UDP_LINUX_AGENT:
			if(EdgeLicenseUtil.hasFeature(required_feature, LicenseDef.SUBLIC_OS_HYPERV))
				return linux_hypervisor;
			else
				return linux;
		}
		return null;
	}

	public static WindowsAgentNceResolver getNceResolver(){
		return nce;
	}
			
}
