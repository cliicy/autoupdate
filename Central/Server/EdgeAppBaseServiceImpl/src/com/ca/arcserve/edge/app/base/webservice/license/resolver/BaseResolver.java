package com.ca.arcserve.edge.app.base.webservice.license.resolver;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.edge.license.BundledLicense;
import com.ca.arcflash.webservice.edge.license.LicenseCheckResult;
import com.ca.arcflash.webservice.edge.license.LicenseCheckResult.LicenseExpiredState;
import com.ca.arcflash.webservice.edge.license.LicenseDef.UDP_CLIENT_TYPE;
import com.ca.arcflash.webservice.edge.license.LicenseStatus;
import com.ca.arcflash.webservice.edge.license.MachineInfo;
import com.ca.arcserve.edge.app.base.webservice.license.EdgeLicenseUtil;
import com.ca.arcserve.edge.app.base.webservice.license.ILicenseLocalWrapper;
import com.ca.arcserve.edge.app.base.webservice.license.ILicenseModuleWrapper;
import com.ca.arcserve.edge.app.base.webservice.license.ILicenseResolver;
import com.ca.arcserve.edge.app.base.webservice.license.LicenseLocalWrapper;
import com.ca.arcserve.edge.app.base.webservice.license.LicenseModuleWrapper;

public abstract class BaseResolver implements ILicenseResolver{
	protected final static Logger logger=Logger.getLogger(BaseResolver.class);
	protected ILicenseModuleWrapper wrapper=LicenseModuleWrapper.getInstance();
	protected ILicenseLocalWrapper localWrapper=LicenseLocalWrapper.getInstance();
	

	public BaseResolver(ILicenseModuleWrapper wrapper,
			ILicenseLocalWrapper localWrapper) {
		this.wrapper = wrapper;
		this.localWrapper = localWrapper;
	}

	@Override
	public LicenseCheckResult checkLicense(MachineInfo machine, long required_feature) {
		if(localWrapper.findLicenseNce(machine.getHostName())){
			localWrapper.deleteLicenseNce(machine.getHostName());
		}
		if(wrapper.hasAnyLicense()){
			// find current license
			LicenseCheckResult licc=checkCurrentLicense(machine, required_feature);
			if(licc!=null){
				if(logger.isDebugEnabled())
					logger.debug(getClientType()+" Current license " + licc.getLicense().getCode()+" to "+getBindedName(machine)+" ("+licc.getUsed_num()+","+licc.getState()+")"+" feature:"+required_feature);
				return licc;
			};
			
			LicenseCheckResult lic = findLicense(machine, required_feature);
			if(lic!=null){
				logger.info(getClientType()+" Assign license " + lic.getLicense().getCode()+" to "+getBindedName(machine)+" ("+lic.getUsed_num()+","+lic.getState()+")"+" feature:"+required_feature);
				return lic;
			}
		}else{
			return checkTrialLicense(machine, required_feature);
		}
		return null;
		
	}


	private LicenseCheckResult checkTrialLicense(MachineInfo machine,
			long required_feature) {
		// We use ALP Trial license as Trial license
		// If user has license other than ALP, we don't use ALP
		LicenseStatus r = checkLicenseUseInternal(BundledLicense.UDPLIC_ALP.getCode(), 1);
		if(LicenseStatus.TRIAL == r){
			BundledLicense lic = BundledLicense.UDPLIC_TRIAL_LICENSE;
			String bindedName = getBindedName(machine);
			localWrapper.useLicense(bindedName, getClientType(), lic.getCode(), required_feature, 1);
			return new LicenseCheckResult(lic, LicenseCheckResult.LicenseExpiredState.Trial, bindedName, getClientType(), 1);
		}
		if(isLicenseAvailable(r)){
			BundledLicense lic = BundledLicense.UDPLIC_ALP;
			String bindedName = getBindedName(machine);
			localWrapper.useLicense(bindedName, getClientType(), lic.getCode(), required_feature, 1);
			return new LicenseCheckResult(lic, getExpiredState(r), bindedName, getClientType(), 1);
		}
		return null;
	}

	private static BundledLicense[] mcLics=new BundledLicense[]{
			BundledLicense.UDPLIC_STANDARD_Managed_Capacity,
			BundledLicense.UDPLIC_ADVANCED_Managed_Capacity,
			BundledLicense.UDPLIC_PREMIUM_Managed_Capacity,
			BundledLicense.UDPLIC_PREMIUM_Managed_Capacity_PLUS};
	private LicenseCheckResult findLicense(MachineInfo machine,
			long required_feature) {
		//1. check ALP license
		LicenseCheckResult t = checkOneLicense(machine, required_feature, BundledLicense.UDPLIC_ALP);
		if(t!=null)
			return t;
		
		//2. check Managed Capacity license
		for(BundledLicense lic:mcLics){
			if (checkLicenseCompatible(lic.getFeature(), required_feature)){ // license compatible
				LicenseCheckResult tt=checkLicenseUse(machine, required_feature, lic, 1, 0);	// no count
				if(tt!=null)
					return tt;
			}
		}
		
		//3. check other license
		return findCommonLicense(machine, required_feature);
		
	}
	
	protected abstract LicenseCheckResult findCommonLicense(MachineInfo machine, long required_feature);

	protected boolean checkLicenseCompatible(long have_feature, long required_feature){
		return (have_feature & required_feature)==required_feature;
	}
	
	protected boolean checkCurrentLicenseCompatible(BundledLicense lic, long required_feature){
		return checkLicenseCompatible(lic.getFeature(), required_feature);
	}

	private LicenseCheckResult checkCurrentLicense(MachineInfo machine, long required_feature) {
		BundledLicense lic=localWrapper.findLicense(getBindedName(machine), getClientType(), new int[1]);
		if(lic==null || lic==BundledLicense.UDPLIC_TRIAL_LICENSE || lic==BundledLicense.UDPLIC_ALP)
			return null;
		LicenseCheckResult r=null;
		if(checkCurrentLicenseCompatible(lic, required_feature)){
			int need=getNeedCount(lic, required_feature, machine);// only concern whether license expired
			int used=0;
			if(need+used==0)
				used=1;
			r=checkLicenseUse(machine, required_feature, lic, used, need);
		}
		if(r==null){
			logger.info("checkCurrentLicense deleteLicense machine:"+getBindedName(machine)+" ,client:"+getClientType());
			dealCurrentLicenseUnavailable(machine);
		}
		return r;
	}

	protected void dealCurrentLicenseUnavailable(MachineInfo machine) {
		localWrapper.deleteLicenseByNode(getBindedName(machine), getClientType());
	}

	protected abstract UDP_CLIENT_TYPE getClientType();

	private LicenseCheckResult checkLicenseUse(MachineInfo machine, long required_feature, BundledLicense lic, int used_num, int need_num) {
		LicenseStatus v = checkLicenseUseInternal(lic.getCode(), used_num+need_num);
		boolean r=isLicenseAvailable(v);
		if(r){
			String bindedName = getBindedName(machine);
			localWrapper.useLicense(bindedName, getClientType(), lic.getCode(), required_feature, need_num);
			return new LicenseCheckResult(lic, getExpiredState(v), bindedName, getClientType(), need_num);
		}
		return null;
	}

	protected LicenseExpiredState getExpiredState(LicenseStatus v) {
		switch(v){
		case VALID:
			return LicenseCheckResult.LicenseExpiredState.Valid;
		case WILL_EXPIRE:
			return LicenseCheckResult.LicenseExpiredState.Will_Expired;
		case EXPIRED:
			return LicenseCheckResult.LicenseExpiredState.Expired;
		case TRIAL:
		case WG_COUNT:
		case ERROR:
		case ERROR_NETWORK:
		case TERMINATE:
		default:
			break;
		}
		return null;
	}

	protected String getBindedName(MachineInfo machine) {
		return machine.getHostName();
	}

	protected LicenseStatus checkLicenseUseInternal(String code, int count) {
		LicenseStatus result = wrapper.useLicense(code, count);
		return result;
	}
	
	protected boolean isLicenseAvailable(LicenseStatus status){
		switch(status){
		case VALID:
		case WILL_EXPIRE:
		case EXPIRED:
			return true;
		case TRIAL:
		case TERMINATE:
		case WG_COUNT:
		case ERROR:
			return false;
		default:
			return false;
		}
	}

	// subclass implement this method.
	// In order to support return a different order from findCommonLicense(),
	// So I didn't reuse it.
	@Override
	public BundledLicense findBestLicense(MachineInfo machine, long required_feature) {
		// find all available license
		BundledLicense[] needCheck=mcLics;
		
		// check each license by order
		return checkBestLicenseList(needCheck, machine, required_feature);
	}

	protected BundledLicense checkBestLicenseList(BundledLicense[] needCheck, MachineInfo machine,
			long required_feature) {
		for(BundledLicense lic:needCheck){
			if(checkOneBestLicense(machine, required_feature, lic))
				return lic;
		}
		return null;
	}

	protected boolean checkOneBestLicense(MachineInfo machine,
			long required_feature, BundledLicense lic) {
		if (checkLicenseCompatible(lic.getFeature(), required_feature)){ // license compatible
			doLogBestLicense(machine, required_feature, lic);
			return true;
		}
		return false;
	}

	protected void doLogBestLicense(MachineInfo machine, long required_feature,
			BundledLicense lic) {
		int count=getNeedCount(lic, required_feature, machine);// only concern whether license expired
		localWrapper.needLicense(getBindedName(machine), getClientType(), lic.getCode(), required_feature, count);
		logger.info(getClientType()+" Need license " + lic.getCode()+" for "+getBindedName(machine)+" ("+count+")"+" feature:"+required_feature);
	}

	protected boolean hasFeature(long required_feature, long check_feature) {
		return EdgeLicenseUtil.hasFeature(required_feature, check_feature);
	}

	protected LicenseCheckResult checkLicenseList(BundledLicense[] needCheck, MachineInfo machine, long required_feature) {
		// check each license by order
		for(BundledLicense lic:needCheck){
			LicenseCheckResult t = checkOneLicense(machine, required_feature, lic);
			if(t!=null)
				return t;
		}
		return null;
	}

	protected LicenseCheckResult checkOneLicense(MachineInfo machine, long required_feature, BundledLicense lic) {
		if (checkLicenseCompatible(lic.getFeature(), required_feature)){ // license compatible
			int used=localWrapper.getUsed(lic.getCode());
			int needed=getNeedCount(lic, required_feature, machine);
			if(used + needed<=0)
				used=1;
			LicenseCheckResult r=checkLicenseUse(machine, required_feature, lic, used, needed);
			return r;
		}
		return null;
	}

	protected int getNeedCount(BundledLicense lic, long required_feature, MachineInfo machine) {
		for(BundledLicense n : mcLics){
			if(n==lic){
				return 0;
			}
		}
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
				return machine.getSocketCount();
			}
		}
		return 1;
	}
}
