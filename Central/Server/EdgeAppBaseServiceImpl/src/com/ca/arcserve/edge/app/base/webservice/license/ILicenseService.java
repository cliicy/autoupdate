package com.ca.arcserve.edge.app.base.webservice.license;

import com.ca.arcflash.webservice.edge.license.BundledLicense;
import com.ca.arcflash.webservice.edge.license.LicenseDef;
import com.ca.arcflash.webservice.edge.license.LicenseDef.UDP_CLIENT_TYPE;
import com.ca.arcflash.webservice.edge.license.MachineInfo;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.IEdgeLicenseService;

public interface ILicenseService extends IEdgeLicenseService{
	BundledLicense findBestLicense(LicenseDef.UDP_CLIENT_TYPE type, MachineInfo machine, long items) throws EdgeServiceFault;
	void logBestLicense(BundledLicense license) throws EdgeServiceFault;
	void deleteLicenseByMachine(String machine_name, UDP_CLIENT_TYPE type) throws EdgeServiceFault;
	void saveVmInfo(MachineInfo machine, boolean isHBBU);
}
