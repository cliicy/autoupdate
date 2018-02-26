package com.ca.arcserve.edge.app.base.webservice.license.resolver;

import com.ca.arcflash.webservice.edge.license.BundledLicense;
import com.ca.arcflash.webservice.edge.license.LicenseCheckResult;
import com.ca.arcflash.webservice.edge.license.LicenseDef;
import com.ca.arcflash.webservice.edge.license.LicenseDef.UDP_CLIENT_TYPE;
import com.ca.arcflash.webservice.edge.license.MachineInfo;
import com.ca.arcserve.edge.app.base.webservice.license.ILicenseLocalWrapper;
import com.ca.arcserve.edge.app.base.webservice.license.ILicenseModuleWrapper;

public class HbbuClientResolver extends BaseResolver {
	public HbbuClientResolver(ILicenseModuleWrapper wrapper,
			ILicenseLocalWrapper localWrapper) {
		super(wrapper, localWrapper);
	}

	private BundledLicense[] needCheck_VM_Essentials=new BundledLicense[]{
			BundledLicense.UDPLIC_STANDARD_Per_SOCKET_Essentials,
			BundledLicense.UDPLIC_ADVANCED_Per_SOCKET_Essentials,
			BundledLicense.UDPLIC_PREMIUM_Per_SOCKET_Essentials,
			BundledLicense.UDPLIC_PREMIUM_Per_SOCKET_Essentials_PLUS,
	};
	
	private BundledLicense[] needCheckOther=new BundledLicense[]{
			
			BundledLicense.UDPLIC_STANDARD_Per_SOCKET,
			BundledLicense.UDPLIC_ADVANCED_Per_SOCKET,
			BundledLicense.UDPLIC_PREMIUM_Per_SOCKET,
			BundledLicense.UDPLIC_PREMIUM_Per_SOCKET_PLUS,
			
			BundledLicense.UDPLIC_STANDARD_Per_HOST,
			BundledLicense.UDPLIC_ADVANCED_Per_HOST,
			BundledLicense.UDPLIC_PREMIUM_Per_HOST,
			BundledLicense.UDPLIC_PREMIUM_Per_HOST_PLUS,
	};
	
	@Override
	protected UDP_CLIENT_TYPE getClientType() {
		return UDP_CLIENT_TYPE.UDP_CLIENT_HBBU;
	}

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
	public LicenseCheckResult checkLicense(MachineInfo machine,
			long required_feature) {
		LicenseCheckResult lic = super.checkLicense(machine, required_feature);
		if(lic!=null){
			BundledLicense wlic = localWrapper.findLicense(machine.getServerName(), UDP_CLIENT_TYPE.UDP_WINDOWS_AGENT, new int[1]);
			if(wlic!=null){
				logger.info("hyperv auto remove old license "+wlic.getCode()+" of "+machine.getServerName()+" by HBBU backup "+machine.getHostName());
				localWrapper.deleteLicenseByNode(machine.getServerName(), UDP_CLIENT_TYPE.UDP_WINDOWS_AGENT);
			}
		}
		return lic;
	}
	
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
	protected LicenseCheckResult findCommonLicense(MachineInfo machine,
			long required_feature) {
		if(hasFeature(required_feature, LicenseDef.SUBLIC_VMWare_Essential)){
			BundledLicense[] needCheck=needCheck_VM_Essentials;
			
			LicenseCheckResult t=checkLicenseList(needCheck, machine, required_feature);
			if(t!=null)
				return t;
		}
		{
			BundledLicense[] needCheck=needCheckOther;
			
			return checkLicenseList(needCheck, machine, required_feature);
		}
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
