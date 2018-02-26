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

public class WindowsHypervisorAgentResolver extends BaseResolver{
	private ILicenseResolver windows_agent=new WindowsAgentResolver(LicenseModuleWrapper.getInstance(), LicenseLocalWrapper.getInstance());

	public WindowsHypervisorAgentResolver(ILicenseModuleWrapper wrapper,
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
	protected boolean checkCurrentLicenseCompatible(BundledLicense lic,
			long required_feature) {
		if(hasFeature(required_feature, LicenseDef.SUBLIC_VMWare_Essential) ||
				hasFeature(required_feature, LicenseDef.SUBLIC_OS_SBS) ||
				hasFeature(required_feature, LicenseDef.SUBLIC_OS_WORKSTATION)){
			
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
		if(hasFeature(required_feature, LicenseDef.SUBLIC_VMWare_Essential) ||
				hasFeature(required_feature, LicenseDef.SUBLIC_OS_SBS) ||
				hasFeature(required_feature, LicenseDef.SUBLIC_OS_WORKSTATION)){
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
	public LicenseCheckResult checkLicense(MachineInfo machine,
			long required_feature) {
		if(hasFeature(required_feature, LicenseDef.SUBLIC_VSB)){
			if(localWrapper.findLicenseNce(machine.getHostName())){
				return new LicenseCheckResult(BundledLicense.UDPLIC_NCE_LICENSE, LicenseCheckResult.LicenseExpiredState.Valid, machine.getHostName(), getClientType(), 1);
			}
		}
		{
			// delete windows license for hyperv, in order to make it use hypervisor license later
			BundledLicense wlic = localWrapper.findLicense(machine.getServerName(), UDP_CLIENT_TYPE.UDP_WINDOWS_AGENT, new int[1]);
			if(wlic!=null){
				logger.info("hyperv auto remove old license "+wlic.getCode()+" of "+machine.getServerName()+" by backup "+machine.getHostName());
				localWrapper.deleteLicenseByNode(machine.getServerName(), UDP_CLIENT_TYPE.UDP_WINDOWS_AGENT);
			}
		}
		LicenseCheckResult lic= super.checkLicense(machine, required_feature);
		if(lic!=null){
			return lic;
		}

		logger.info(getClientType()+" No available hypervisor license for " + machine.getServerName()+"("+machine.getHostName()+")"+" feature:"+required_feature);
		return windows_agent.checkLicense(machine, EdgeLicenseUtil.removeFeature(required_feature, LicenseDef.SUBLIC_OS_HYPERV));
	}
	
	@Override
	public BundledLicense findBestLicense(MachineInfo machine,
			long required_feature) {
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
	
	protected void dealCurrentLicenseUnavailable(MachineInfo machine) {
		
	}
}
