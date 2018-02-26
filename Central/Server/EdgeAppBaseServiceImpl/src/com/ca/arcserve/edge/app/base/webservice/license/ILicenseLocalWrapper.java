package com.ca.arcserve.edge.app.base.webservice.license;

import java.util.List;

import com.ca.arcflash.webservice.edge.license.BundledLicense;
import com.ca.arcflash.webservice.edge.license.LicenseDef;
import com.ca.arcflash.webservice.edge.license.LicenseDef.UDP_CLIENT_TYPE;
import com.ca.arcserve.edge.app.base.webservice.contract.license.LicensedNodeInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.license.LicensedVmInfo;

public interface ILicenseLocalWrapper {

	public int getUsed(String code);

	public int getNeeded(String code);

	public BundledLicense getLicense(String code);

	public long getFeature(String code);

	public String getLicenseName(String code);

	public List<String> getLicenses();

	public BundledLicense findLicense(String bindNodeName,
			LicenseDef.UDP_CLIENT_TYPE client_type, int[] out_lic_id);

	public BundledLicense findNeedLicense(String bindNodeName, UDP_CLIENT_TYPE client_type);

	public void useLicense(String bindNodeName,
			LicenseDef.UDP_CLIENT_TYPE client_type, String code,
			long required_feature, int used_num);
	
	public void releaseLicense(String bindNodeName,	LicenseDef.UDP_CLIENT_TYPE client_type, String code);
	
	public void needLicense(String bindNodeName,
			LicenseDef.UDP_CLIENT_TYPE client_type, String code,
			long required_feature, int used_num);
	
	public List<LicensedNodeInfo> getLicensedMachine(String code);
	
	public List<LicensedNodeInfo> getUnLicensedMachine(String code);

	public void deleteLicenseByNode(String machine_name, UDP_CLIENT_TYPE type);

	public boolean findLicenseNce(String hostname);

	public void addLicenseNce(String hostname);

	public void deleteLicenseNce(String hostname);
	
	public int getUsedNce();

	public List<LicensedNodeInfo> getNceLicensedMachine();

	public void saveVmInfo(int lic_id, int node_id, String vm_name);

	public List<LicensedVmInfo> findVmInfo(int lic_id);

}