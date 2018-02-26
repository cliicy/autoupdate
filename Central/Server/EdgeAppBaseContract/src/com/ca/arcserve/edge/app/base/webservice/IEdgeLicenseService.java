package com.ca.arcserve.edge.app.base.webservice;

import java.util.List;

import com.ca.arcflash.webservice.edge.license.BundledLicense;
import com.ca.arcflash.webservice.toedge.IEdgeLicense;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.license.LicensedNodeInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.license.LicensedVmInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.license.bundled.LicenseInformation;

public interface IEdgeLicenseService extends IEdgeLicense{
	int addLicenseKeyNew(String license) throws EdgeServiceFault;
	List<LicenseInformation> getLicenses() throws EdgeServiceFault;
	LicenseInformation getLicenseInfo(String key) throws EdgeServiceFault;
	List<LicensedNodeInfo> getLicensedNodeList(BundledLicense licenseId) throws EdgeServiceFault;
	List<LicensedNodeInfo> getUnLicensedNodeList(BundledLicense licenseId) throws EdgeServiceFault;
	void releaseNodeFromLicenseNew(BundledLicense licenseId, List<LicensedNodeInfo> nodeList) throws EdgeServiceFault;
	List<LicenseInformation> getNceLicenseList()throws EdgeServiceFault;
	List<LicensedNodeInfo> getNodeListOfNceLicense()throws EdgeServiceFault;
	List<LicensedVmInfo> getVmListByHypervisor(String hypervisor) throws EdgeServiceFault;
}
