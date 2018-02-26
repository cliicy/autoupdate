package com.ca.arcserve.edge.app.base.webservice.license;

import com.ca.arcflash.webservice.edge.license.BundledLicense;
import com.ca.arcflash.webservice.edge.license.LicenseCheckResult;
import com.ca.arcflash.webservice.edge.license.MachineInfo;

public interface ILicenseResolver {

	LicenseCheckResult checkLicense(MachineInfo machine, long required_feature);

	BundledLicense findBestLicense(MachineInfo machine, long required_feature);

}
