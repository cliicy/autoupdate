package com.ca.arcserve.edge.app.base.webservice.license.resolver;

import com.ca.arcflash.webservice.edge.license.BundledLicense;
import com.ca.arcflash.webservice.edge.license.LicenseCheckResult;
import com.ca.arcflash.webservice.edge.license.LicenseDef;
import com.ca.arcflash.webservice.edge.license.LicenseStatus;
import com.ca.arcflash.webservice.edge.license.LicenseDef.UDP_CLIENT_TYPE;
import com.ca.arcflash.webservice.edge.license.MachineInfo;
import com.ca.arcserve.edge.app.base.webservice.license.ILicenseLocalWrapper;
import com.ca.arcserve.edge.app.base.webservice.license.ILicenseModuleWrapper;

public class WindowsAgentResolver extends BaseResolver{
	public WindowsAgentResolver(ILicenseModuleWrapper wrapper,
			ILicenseLocalWrapper localWrapper) {
		super(wrapper, localWrapper);
	}

	private final static LicenseDef.UDP_CLIENT_TYPE CLIENT_TYPE=LicenseDef.UDP_CLIENT_TYPE.UDP_WINDOWS_AGENT;
	
	@Override
	protected UDP_CLIENT_TYPE getClientType() {
		return CLIENT_TYPE;
	}

	@Override
	public LicenseCheckResult checkLicense(MachineInfo machine,
			long required_feature) {
		if(hasFeature(required_feature, LicenseDef.SUBLIC_VSB)){
			if(localWrapper.findLicenseNce(machine.getHostName())){
				return new LicenseCheckResult(BundledLicense.UDPLIC_NCE_LICENSE, LicenseCheckResult.LicenseExpiredState.Valid, machine.getHostName(),getClientType(), 1);
			}
		}
		{//checkAvailableHypervisorLicense
			BundledLicense hlic = localWrapper.findLicense(machine.getHostName(), UDP_CLIENT_TYPE.UDP_CLIENT_HBBU, new int[1]);
			if(hlic!=null){
				logger.info("hyperv find license "+hlic.getCode());
				LicenseStatus v = checkLicenseUseInternal(hlic.getCode(), 1);
				boolean r=isLicenseAvailable(v);
				if(r){
					BundledLicense wlic = localWrapper.findLicense(machine.getHostName(), UDP_CLIENT_TYPE.UDP_WINDOWS_AGENT, new int[1]);
					if(wlic!=null){
						logger.info("hyperv remove old license "+wlic.getCode());
						localWrapper.deleteLicenseByNode(machine.getHostName(), UDP_CLIENT_TYPE.UDP_WINDOWS_AGENT);
					}
					return new LicenseCheckResult(hlic, getExpiredState(v), machine.getHostName(), UDP_CLIENT_TYPE.UDP_CLIENT_HBBU, 1); 
				}else{
					logger.info("hyperv find license but not available "+v +", socket "+machine.getSocketCount());
				}
			}
		}
		return super.checkLicense(machine, required_feature);
	}
	
	@Override
	protected LicenseCheckResult findCommonLicense(MachineInfo machine,
			long required_feature) {
		if(!hasFeature(required_feature, LicenseDef.SUBLIC_OS_PM)){
			BundledLicense lic = BundledLicense.UDPLIC_Virtual_Machine;
			LicenseCheckResult t=checkOneLicense(machine, required_feature, lic );
			if(t!=null)
				return t;
		}
		if(hasFeature(required_feature, LicenseDef.SUBLIC_OS_WORKSTATION)){
			BundledLicense lic = BundledLicense.UDPLIC_STANDARD_WORKSTATION;
			LicenseCheckResult t=checkOneLicense(machine, required_feature, lic );
			if(t!=null)
				return t;
		}
		if(hasFeature(required_feature, LicenseDef.SUBLIC_OS_WORKSTATION) 
				|| hasFeature(required_feature, LicenseDef.SUBLIC_OS_SBS)){
			BundledLicense[] needCheck=new BundledLicense[]{
					BundledLicense.UDPLIC_STANDARD_Per_SOCKET_Essentials,
					
					BundledLicense.UDPLIC_ADVANCED_SBS,
					BundledLicense.UDPLIC_PREMIUM_SBS,
					
					BundledLicense.UDPLIC_ADVANCED_Per_SOCKET_Essentials,
					BundledLicense.UDPLIC_PREMIUM_Per_SOCKET_Essentials,
					BundledLicense.UDPLIC_PREMIUM_Per_SOCKET_Essentials_PLUS,
					
			};
			LicenseCheckResult t=checkLicenseList(needCheck, machine, required_feature);
			if(t!=null)
				return t;
		}
		{
			BundledLicense[] needCheck=new BundledLicense[]{
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
			LicenseCheckResult t=checkLicenseList(needCheck, machine, required_feature);
			return t;
		}
	}

	@Override
	public BundledLicense findBestLicense(MachineInfo machine,
			long required_feature) {
		if(hasFeature(required_feature, LicenseDef.SUBLIC_OS_WORKSTATION)){
			BundledLicense lic = BundledLicense.UDPLIC_STANDARD_WORKSTATION;
			doLogBestLicense(machine, required_feature, lic);
			return lic;
		}
		if(hasFeature(required_feature, LicenseDef.SUBLIC_APP_EXCHANGE)||
				hasFeature(required_feature, LicenseDef.SUBLIC_App_SQL)){
			BundledLicense lic = BundledLicense.UDPLIC_ADVANCED_Per_SOCKET;
			doLogBestLicense(machine, required_feature, lic);
			return lic;
		}
		BundledLicense lic = BundledLicense.UDPLIC_STANDARD_Per_SOCKET;
		doLogBestLicense(machine, required_feature, lic);
		return lic;
	}
	
}
