package com.ca.arcserve.edge.app.base.webservice.license.resolver;

import com.ca.arcflash.webservice.edge.license.BundledLicense;
import com.ca.arcflash.webservice.edge.license.LicenseCheckResult;
import com.ca.arcflash.webservice.edge.license.LicenseDef;
import com.ca.arcflash.webservice.edge.license.LicenseDef.UDP_CLIENT_TYPE;
import com.ca.arcflash.webservice.edge.license.MachineInfo;
import com.ca.arcserve.edge.app.base.webservice.license.ILicenseLocalWrapper;
import com.ca.arcserve.edge.app.base.webservice.license.ILicenseModuleWrapper;

public class LinuxAgentResolver extends BaseResolver {
	public LinuxAgentResolver(ILicenseModuleWrapper wrapper,
			ILicenseLocalWrapper localWrapper) {
		super(wrapper, localWrapper);
	}

	private BundledLicense[] needCheck_PM=new BundledLicense[]{
			BundledLicense.UDPLIC_STANDARD_SERVER,
			
			BundledLicense.UDPLIC_STANDARD_Per_SOCKET,

			BundledLicense.UDPLIC_ADVANCED_SERVER,
			BundledLicense.UDPLIC_PREMIUM_SERVER,
			BundledLicense.UDPLIC_PREMIUM_SERVER_PLUS,

			BundledLicense.UDPLIC_ADVANCED_Per_SOCKET,
			BundledLicense.UDPLIC_PREMIUM_Per_SOCKET,
			BundledLicense.UDPLIC_PREMIUM_Per_SOCKET_PLUS,

			BundledLicense.UDPLIC_BASIC,
			
	};
	
	private BundledLicense[] needCheck_VM=new BundledLicense[]{
			BundledLicense.UDPLIC_Virtual_Machine,
			
			BundledLicense.UDPLIC_STANDARD_Per_SOCKET,
			BundledLicense.UDPLIC_STANDARD_SERVER,
			
			BundledLicense.UDPLIC_ADVANCED_Per_SOCKET,
			BundledLicense.UDPLIC_PREMIUM_Per_SOCKET,
			BundledLicense.UDPLIC_PREMIUM_Per_SOCKET_PLUS,
			
			BundledLicense.UDPLIC_ADVANCED_SERVER,
			BundledLicense.UDPLIC_PREMIUM_SERVER,
			BundledLicense.UDPLIC_PREMIUM_SERVER_PLUS,

			BundledLicense.UDPLIC_BASIC,
			
	};
	
	@Override
	protected UDP_CLIENT_TYPE getClientType() {
		return UDP_CLIENT_TYPE.UDP_LINUX_AGENT;
	}

	@Override
	protected LicenseCheckResult findCommonLicense(MachineInfo machine,
			long required_feature) {
		if(hasFeature(required_feature, LicenseDef.SUBLIC_OS_PM)){
			return checkLicenseList(needCheck_PM, machine, required_feature);
		}else{
			return checkLicenseList(needCheck_VM, machine, required_feature);
		}
	}

	@Override
	public BundledLicense findBestLicense(MachineInfo machine,
			long required_feature) {
		BundledLicense lic = BundledLicense.UDPLIC_STANDARD_Per_SOCKET;
		doLogBestLicense(machine, required_feature, lic);
		return lic;
	}
}
