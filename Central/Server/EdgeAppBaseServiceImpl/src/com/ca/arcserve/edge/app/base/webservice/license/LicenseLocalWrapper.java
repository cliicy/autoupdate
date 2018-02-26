package com.ca.arcserve.edge.app.base.webservice.license;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.webservice.edge.license.BundledLicense;
import com.ca.arcflash.webservice.edge.license.LicenseDef;
import com.ca.arcflash.webservice.edge.license.LicenseDef.UDP_CLIENT_TYPE;
import com.ca.arcserve.edge.app.base.appdaos.ILicenseDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.license.LicensedNodeInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.license.LicensedVmInfo;

public class LicenseLocalWrapper implements ILicenseLocalWrapper {
	private static ILicenseLocalWrapper instance=null;
	private ILicenseDao dao=DaoFactory.getDao(ILicenseDao.class);
	
	public static synchronized ILicenseLocalWrapper getInstance(){
		if(instance==null)
			instance=new LicenseLocalWrapper();
		return instance;
	}
	
	// used for Mock test
	@SuppressWarnings("unused")
	private static void setInstance(ILicenseLocalWrapper instance_test){
		instance=instance_test;
	}
	
	private LicenseLocalWrapper(){
		
	}
	
	@Override
	public int getUsed(String code) {
		int[] result=new int[1];
		dao.get_license_used_num(code, result);
		return result[0];
	}

	@Override
	public int getNeeded(String code) {
		int[] result=new int[1];
		dao.get_license_needed_num(code, result);
		return result[0];
	}

	@Override
	public BundledLicense getLicense(String code) {
		return BundledLicense.parseByCode(code);
	}
	
	@Override
	public long getFeature(String code) {
		return getLicense(code).getFeature();
	}

	@Override
	public String getLicenseName(String code) {
		return getLicense(code).getDisplayName();
	}

	@Override
	public List<String> getLicenses() {
		List<String> codes=new ArrayList<String>();
		for(BundledLicense b:BundledLicense.values()){
			codes.add(b.getCode());
		}
		return codes;
	}

	@Override
	public BundledLicense findLicense(String bindNodeName, LicenseDef.UDP_CLIENT_TYPE client_type, int[] out_lic_id) {
		String[] code=new String[1];
		dao.find_license(bindNodeName, client_type.getValue(), code, out_lic_id);
		if(code[0]!=null)
			return BundledLicense.parseByCode(code[0]);
		return null;
	}
	
	@Override
	public BundledLicense findNeedLicense(String bindNodeName, LicenseDef.UDP_CLIENT_TYPE client_type) {
		String[] code=new String[1];
		dao.find_need_license(bindNodeName, client_type.getValue(), code);
		if(code[0]!=null)
			return BundledLicense.parseByCode(code[0]);
		return null;
	}
	
	@Override
	public void useLicense(String bindNodeName, LicenseDef.UDP_CLIENT_TYPE client_type, String code, long required_feature, int used_num){
		dao.assign_license(bindNodeName, client_type.getValue(), code, required_feature, used_num);
	}

	@Override
	public void needLicense(String bindNodeName, UDP_CLIENT_TYPE client_type,
			String code, long required_feature, int used_num) {
		dao.need_license(bindNodeName, client_type.getValue(), code, required_feature, used_num);		
	}

	@Override
	public List<LicensedNodeInfo> getLicensedMachine(String code) {
		List<LicensedNodeInfo> result=new ArrayList<LicensedNodeInfo>();
		dao.get_license_used_machine(code, result);
		return result;
	}

	@Override
	public List<LicensedNodeInfo> getUnLicensedMachine(String code) {
		List<LicensedNodeInfo> result=new ArrayList<LicensedNodeInfo>();
		dao.get_license_needed_machine(code, result);
		return result;
	}

	@Override
	public void releaseLicense(String bindNodeName,
			UDP_CLIENT_TYPE client_type, String code) {
		dao.release_license_used(bindNodeName, client_type.getValue(), code);
		if(client_type==UDP_CLIENT_TYPE.UDP_CLIENT_HBBU){// only manually release license need clear this map
			dao.as_edge_hypervisor_vm_unmark_all(bindNodeName);
		}
	}

	@Override
	public void deleteLicenseByNode(String bindNodeName, UDP_CLIENT_TYPE client_type) {
		dao.delete_license_by_machine(bindNodeName, client_type.getValue());		
	}

	@Override
	public boolean findLicenseNce(String hostname) {
		int[] id=new int[1];
		dao.as_edge_license_nce_find(hostname, id);
		return id[0]>0;
	}

	@Override
	public void addLicenseNce(String hostname) {
		dao.as_edge_license_nce_update(hostname);
	}
	
	@Override
	public void deleteLicenseNce(String hostname){
		dao.as_edge_license_nce_delete(hostname);
	}

	@Override
	public int getUsedNce() {
		int[] result=new int[1];
		dao.as_edge_license_nce_used_num(result);
		return result[0];
	}
	
	@Override
	public List<LicensedNodeInfo> getNceLicensedMachine() {
		List<LicensedNodeInfo> result=new ArrayList<LicensedNodeInfo>();
		dao.as_edge_license_nce_used_machine(result);
		return result;
	}

	@Override
	public void saveVmInfo(int lic_id, int node_id, String vm_name) {
		dao.as_edge_hypervisor_vm_save(lic_id, node_id, vm_name);
	}

	@Override
	public List<LicensedVmInfo> findVmInfo(int lic_id) {
		List<LicensedVmInfo> result = new ArrayList<LicensedVmInfo>();
		dao.as_edge_hypervisor_vm_find(lic_id, result);
		return result;
	}
}
