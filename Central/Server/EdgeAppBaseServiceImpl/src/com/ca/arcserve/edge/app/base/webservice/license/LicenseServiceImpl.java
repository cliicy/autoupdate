package com.ca.arcserve.edge.app.base.webservice.license;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.edge.license.BundledLicense;
import com.ca.arcflash.webservice.edge.license.LicenseCheckResult;
import com.ca.arcflash.webservice.edge.license.LicenseDef;
import com.ca.arcflash.webservice.edge.license.LicenseDef.UDP_CLIENT_TYPE;
import com.ca.arcflash.webservice.edge.license.MachineInfo;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeEsxDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHyperVDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHypervisorDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.contract.license.LicensedNodeInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.license.LicensedVmInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.license.bundled.LicenseInformation;

public class LicenseServiceImpl implements ILicenseService {
	private final static Logger logger=Logger.getLogger(LicenseServiceImpl.class);
	private ILicenseModuleWrapper wrapper=LicenseModuleWrapper.getInstance();
	private ILicenseLocalWrapper localWrapper=LicenseLocalWrapper.getInstance();
	private IEdgeConnectInfoDao connectInfoDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
	private IEdgeEsxDao esxDao = DaoFactory.getDao(IEdgeEsxDao.class);
	private IEdgeHyperVDao hyperVDao = DaoFactory.getDao(IEdgeHyperVDao.class);

	private static ReadWriteLock _lock = new ReentrantReadWriteLock();

	/**
	 * To add license key for a given component id into License DB
	 * @param componentid
	 * @param key
	 * @return 0 for success, 1 for already registered, 2 unmatched between key and component, 3 invalid key, other  for error
	 */
	@Override
	public int addLicenseKeyNew(String key) throws EdgeServiceFault {
		Lock lock=getWriteLock();
		lock.lock();
		try{
			int i = wrapper.addLicenseKey(key);
			switch(i){
				case 0: return 0;
				case 1: throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_License_KEY_EXISTED,
				"License key for given compoent already registred!");
				case 2:  throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_License_KEY_UNMATCHED,
				"License key is not for given compoent!");
				case 3:   throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_License_KEY_INVALID,
				"Invalid License key!");
				default: throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_License_ERROR_IN_DB, "Error In Edge DB impl");
			}

		}finally{
			lock.unlock();
		}
	}
	
	@Override
	public List<LicenseInformation> getLicenses() throws EdgeServiceFault {
		Lock lock=getReadLock();
		lock.lock();
		try{
			List<LicenseInformation> l=new ArrayList<LicenseInformation>();
			for(String c:localWrapper.getLicenses()){
				l.add(getLicenseInfo(c));
			}
			return l;
		}finally{
			lock.unlock();
		}
	}

	@Override
	public LicenseInformation getLicenseInfo(String code)
			throws EdgeServiceFault {
		Lock lock=getReadLock();
		lock.lock();
		try{
			LicenseInformation info=new LicenseInformation();
			info.setLicense(localWrapper.getLicense(code));
			info.setTotal(wrapper.getTotalCount(code));
			info.setUsed(localWrapper.getUsed(code));
			info.setNeeded(localWrapper.getNeeded(code));
			info.setInstallTime(wrapper.getInstallTime(code));
			return info;
		}finally{
			lock.unlock();
		}
	}

	@Override
	public LicenseCheckResult checkLicense(UDP_CLIENT_TYPE type, MachineInfo machine,
			long required_feature) {
		logger.debug("LicenseCheckResult checkLicense start!");
		Lock lock=getWriteLock();
		lock.lock();
		logger.debug("LicenseCheckResult checkLicense lock!");
		try{
			ILicenseResolver resolver=LicenseResolverFactory.getResolver(type, machine, required_feature);
			return resolver.checkLicense(machine, required_feature);
		}finally{
			lock.unlock();
			logger.debug("LicenseCheckResult checkLicense unlock!");
		}
	}

	private Lock getWriteLock() {
		return _lock.writeLock();
	}

	private Lock getReadLock() {
		return _lock.readLock();
	}

	@Override
	public BundledLicense findBestLicense(UDP_CLIENT_TYPE type, MachineInfo machine,
			long required_feature) {
		Lock lock=getReadLock();
		lock.lock();
		try{
			ILicenseResolver resolver=LicenseResolverFactory.getResolver(type, machine, required_feature);
			return resolver.findBestLicense(machine, required_feature);
		}finally{
			lock.unlock();
		}
	}

	@Override
	public List<LicensedNodeInfo> getLicensedNodeList(BundledLicense licenseId) {
		Lock lock=getReadLock();
		lock.lock();
		try{
			return localWrapper.getLicensedMachine(licenseId.getCode());
		}finally{
			lock.unlock();
		}
	}

	@Override
	public List<LicensedNodeInfo> getUnLicensedNodeList(BundledLicense licenseId) {
		Lock lock=getReadLock();
		lock.lock();
		try{
			return localWrapper.getUnLicensedMachine(licenseId.getCode());
		}finally{
			lock.unlock();
		}
	}

	@Override
	public void releaseNodeFromLicenseNew(BundledLicense licenseId,
			List<LicensedNodeInfo> nodeList) {
		Lock lock=getWriteLock();
		lock.lock();
		try{
			for(LicensedNodeInfo n:nodeList){
				logger.info("releaseNodeFromLicense machine:"+n.getNodeName()+" ,client:"+n.getClientType()+", code:"+licenseId.getCode());
				localWrapper.releaseLicense(n.getNodeName(), LicenseDef.UDP_CLIENT_TYPE.parseByValue(n.getClientType()), licenseId.getCode());
			}
		}finally{
			lock.unlock();
		}
	}

	@Override
	public void logBestLicense(BundledLicense license) throws EdgeServiceFault {
		Lock lock=getWriteLock();
		lock.lock();
		try{
			String code=license.getCode();
			int total=wrapper.getTotalCount(code);
			int need=localWrapper.getNeeded(code);
			wrapper.logLicense(code, total+need);
		}finally{
			lock.unlock();
		}		
	}

	@Override
	public void deleteLicenseByMachine(String machine_name, UDP_CLIENT_TYPE type)
			throws EdgeServiceFault {
		Lock lock=getWriteLock();
		lock.lock();
		try{
			logger.info("deleteLicense machine:"+machine_name+" ,client:"+type);
			localWrapper.deleteLicenseByNode(machine_name, type);
		}finally{
			lock.unlock();
		}			
	}

	@Override
	public LicenseCheckResult checkLicenseNCE(MachineInfo machine, boolean isVM)
			throws EdgeServiceFault {
		Lock lock=getWriteLock();
		lock.lock();
		try{
			return LicenseResolverFactory.getNceResolver().checkLicenseNCE(machine, isVM);
		}finally{
			lock.unlock();
		}
	}

	@Override
	public List<LicenseInformation> getNceLicenseList() throws EdgeServiceFault {
		LicenseInformation lic=new LicenseInformation();
		lic.setLicense(BundledLicense.UDPLIC_NCE_LICENSE);
		lic.setUsed(localWrapper.getUsedNce());
		lic.setTotal(lic.getUsed());
		List<LicenseInformation> l=new ArrayList<LicenseInformation>();
		l.add(lic);
		return l;
	}

	@Override
	public List<LicensedNodeInfo> getNodeListOfNceLicense()
			throws EdgeServiceFault {
		return localWrapper.getNceLicensedMachine();
	}

	@Override
	public void saveVmInfo(MachineInfo machine, boolean isHBBU) {
		int node_id = findVmId(machine, isHBBU);
		if(node_id<=0){
			return;
		}
		int lic_id = findLicenseId(machine.getServerName());
		if(lic_id<=0){
			return;
		}
		localWrapper.saveVmInfo(lic_id, node_id, machine.getHostName());
	}
	
	private int findLicenseId(String serverName) {
		int[] out_lic_id = new int[1];
		localWrapper.findLicense(serverName, LicenseDef.UDP_CLIENT_TYPE.UDP_CLIENT_HBBU, out_lic_id);
		return out_lic_id[0];
	}

	private int findVmId(MachineInfo machine, boolean isHBBU) {
		if(!isHBBU){
			int[] hostId = new int[1];
			connectInfoDao.as_edge_GetConnInfoByUUID(machine.getHostUuid(), hostId, new String[1], new int[1], new int[1]);
			return hostId[0];
		}else{
			String vminstanceuuid=machine.getHostUuid();
			if(vminstanceuuid==null || vminstanceuuid.isEmpty())
				return 0;
			int[] hostId = new int[1];
			esxDao.as_edge_host_getHostByInstanceUUID(0, vminstanceuuid, hostId);	//TODO: gateway
			if(hostId[0]>0)
				return hostId[0];
			hyperVDao.as_edge_hyperv_host_map_isExistByVMInstanceUuid(0, vminstanceuuid, hostId);	//TODO: need gateway
			if(hostId[0]>0)
				return hostId[0];
			return 0;
		}
	}

	@Override
	public List<LicensedVmInfo> getVmListByHypervisor(String hypervisor)
			throws EdgeServiceFault {
		int lic_id = findLicenseId(hypervisor);
		if(lic_id<=0){
			return null;
		}
		return localWrapper.findVmInfo(lic_id);
	}
}
