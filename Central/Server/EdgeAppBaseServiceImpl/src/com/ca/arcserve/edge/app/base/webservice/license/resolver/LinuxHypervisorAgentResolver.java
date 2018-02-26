package com.ca.arcserve.edge.app.base.webservice.license.resolver;

import com.ca.arcflash.webservice.edge.license.BundledLicense;
import com.ca.arcflash.webservice.edge.license.LicenseCheckResult;
import com.ca.arcflash.webservice.edge.license.LicenseDef;
import com.ca.arcflash.webservice.edge.license.LicenseDef.UDP_CLIENT_TYPE;
import com.ca.arcflash.webservice.edge.license.MachineInfo;
import com.ca.arcserve.edge.app.base.webservice.license.EdgeLicenseUtil;
import com.ca.arcserve.edge.app.base.webservice.license.ILicenseLocalWrapper;
import com.ca.arcserve.edge.app.base.webservice.license.ILicenseModuleWrapper;
import com.ca.arcserve.edge.app.base.webservice.license.ILicenseResolver;
import com.ca.arcserve.edge.app.base.webservice.license.LicenseLocalWrapper;
import com.ca.arcserve.edge.app.base.webservice.license.LicenseModuleWrapper;

public class LinuxHypervisorAgentResolver extends BaseResolver {
	private ILicenseResolver linux=new LinuxAgentResolver(LicenseModuleWrapper.getInstance(), LicenseLocalWrapper.getInstance());

	public LinuxHypervisorAgentResolver(ILicenseModuleWrapper wrapper,
			ILicenseLocalWrapper localWrapper) {
		super(wrapper, localWrapper);
	}


	private BundledLicense[] needCheck_VM_Essentials=new BundledLicense[]{
			BundledLicense.UDPLIC_STANDARD_Per_SOCKET_Essentials,
			BundledLicense.UDPLIC_ADVANCED_Per_SOCKET_Essentials,
			BundledLicense.UDPLIC_PREMIUM_Per_SOCKET_Essentials,
			BundledLicense.UDPLIC_PREMIUM_Per_SOCKET_Essentials_PLUS,
	};
	
	private BundledLicense[] needCheck_VM=new BundledLicense[]{
			BundledLicense.UDPLIC_STANDARD_Per_SOCKET,
			BundledLicense.UDPLIC_STANDARD_Per_HOST,
			
			BundledLicense.UDPLIC_ADVANCED_Per_SOCKET,
			BundledLicense.UDPLIC_PREMIUM_Per_SOCKET,
			BundledLicense.UDPLIC_PREMIUM_Per_SOCKET_PLUS,
			
			BundledLicense.UDPLIC_ADVANCED_Per_HOST,
			BundledLicense.UDPLIC_PREMIUM_Per_HOST,
			BundledLicense.UDPLIC_PREMIUM_Per_HOST_PLUS,
			
	};
	

	@Override
	protected String getBindedName(MachineInfo machine) {
		return machine.getServerName();
	}
	
	@Override
	protected int getNeedCount(BundledLicense lic, long required_feature,
			MachineInfo machine) {
		BundledLicense[] needCheck=new BundledLicense[]{
				BundledLicense.UDPLIC_STANDARD_Per_SOCKET_Essentials,
				BundledLicense.UDPLIC_STANDARD_Per_SOCKET,
				BundledLicense.UDPLIC_ADVANCED_Per_SOCKET_Essentials,
				BundledLicense.UDPLIC_ADVANCED_Per_SOCKET,
				BundledLicense.UDPLIC_PREMIUM_Per_SOCKET_Essentials,
				BundledLicense.UDPLIC_PREMIUM_Per_SOCKET,
				BundledLicense.UDPLIC_PREMIUM_Per_SOCKET_Essentials_PLUS,
				BundledLicense.UDPLIC_PREMIUM_Per_SOCKET_PLUS,
		};
		for(BundledLicense n : needCheck){
			if(n==lic){
				return machine.getServerSocketCount();
			}
		}
		return super.getNeedCount(lic, required_feature, machine);
	}
	
	@Override
	protected UDP_CLIENT_TYPE getClientType() {
		return UDP_CLIENT_TYPE.UDP_CLIENT_HBBU;
	}
//	
	@Override
	protected boolean checkCurrentLicenseCompatible(BundledLicense lic,
			long required_feature) {
		if(hasFeature(required_feature, LicenseDef.SUBLIC_VMWare_Essential)){
			
		}else{
			for(BundledLicense l:needCheck_VM_Essentials){
				if(l==lic)
					return false;
			}
		}
		return super.checkCurrentLicenseCompatible(lic, required_feature);
	}
	
	@Override
	public LicenseCheckResult checkLicense(MachineInfo machine,
			long required_feature) {
		LicenseCheckResult lic= super.checkLicense(machine, required_feature);
		if(lic!=null)
			return lic;
		
		logger.info(getClientType()+" No available hypervisor license for " + getBindedName(machine)+" feature:"+required_feature);
		return linux.checkLicense(machine, EdgeLicenseUtil.removeFeature(required_feature, LicenseDef.SUBLIC_OS_HYPERV));
	}

	@Override
	protected LicenseCheckResult findCommonLicense(MachineInfo machine,
			long required_feature) {
		if(hasFeature(required_feature, LicenseDef.SUBLIC_VMWare_Essential)){
			BundledLicense[] needCheck=needCheck_VM_Essentials;
			
			LicenseCheckResult t=checkLicenseList(needCheck, machine, required_feature);
			if(t!=null)
				return t;
		}
		
		return checkLicenseList(needCheck_VM, machine, required_feature);
	}

	@Override
	public BundledLicense findBestLicense(MachineInfo machine,
			long required_feature) {
		BundledLicense lic = BundledLicense.UDPLIC_STANDARD_Per_SOCKET;
		doLogBestLicense(machine, required_feature, lic);
		return lic;
	}
	
	protected void dealCurrentLicenseUnavailable(MachineInfo machine) {
		
	}
}
