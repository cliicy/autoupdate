package com.ca.arcflash.webservice.common;

import java.io.File;

import com.ca.arcflash.webservice.edge.license.LICENSEDSTATUS;

public class LicenseCheckManager {
	
	private static final LicenseCheckManager instance = new LicenseCheckManager();
	
	private LicenseCheckManager() {
		File directory = new File(LicenseCheck.LicenseDirectory);
		
		if(!directory.exists()) {
			directory.mkdir();
		}
	}
	
	public static LicenseCheckManager getInstance() {
		return instance;
	}
	
	/**
	 * Returns the license which the specified <code>machine</code> has
	 * 
	 * @param machine 
	 * @return
	 * @throws LicenseCheckException 
	 */
	public LICENSEDSTATUS checkVCMLicense(VCMMachineInfo vcmMachineInfo) throws LicenseCheckException {
		
		LICENSEDSTATUS license = VCMLicenseCheck.getInstance().checkLicense(vcmMachineInfo);
		return license;
	}

	public LICENSEDSTATUS checkVSphereBackupLicense(VSphereLicenseCheck.HyperVisorInfo hypervisor) throws LicenseCheckException {
		LICENSEDSTATUS license = null;
		license = VSphereLicenseCheck.getInstance().checkLicense(hypervisor);
		return license;		
	}
}
