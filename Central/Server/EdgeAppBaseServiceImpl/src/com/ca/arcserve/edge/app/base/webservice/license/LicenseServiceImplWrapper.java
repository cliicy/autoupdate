package com.ca.arcserve.edge.app.base.webservice.license;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.edge.license.BundledLicense;
import com.ca.arcflash.webservice.edge.license.LicenseCheckResult;
import com.ca.arcflash.webservice.edge.license.LicenseDef;
import com.ca.arcflash.webservice.edge.license.LicenseDef.UDP_CLIENT_TYPE;
import com.ca.arcflash.webservice.edge.license.MachineInfo;
import com.ca.arcserve.edge.app.base.appdaos.EdgeEsx;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHyperV;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeEsxDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeGatewayDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHyperVDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHypervisorDao;
import com.ca.arcserve.edge.app.base.appdaos.IntegerId;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.util.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.IActivityLogService;
import com.ca.arcserve.edge.app.base.webservice.IEdgeLicenseService;
import com.ca.arcserve.edge.app.base.webservice.contract.license.LicensedNodeInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.license.LicensedVmInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.license.bundled.LicenseInformation;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogAddEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.gateway.EntityType;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;

public class LicenseServiceImplWrapper implements IEdgeLicenseService{
	private ILicenseService licenseServiceNew = new LicenseServiceImpl();
	private final static Logger logger=Logger.getLogger(LicenseServiceImplWrapper.class);
	
	private IEdgeConnectInfoDao connectInfoDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
	private IEdgeEsxDao esxDao = DaoFactory.getDao(IEdgeEsxDao.class);
	private IEdgeHyperVDao hyperVDao = DaoFactory.getDao(IEdgeHyperVDao.class);
	private IEdgeHypervisorDao hypervisorDao = DaoFactory.getDao(IEdgeHypervisorDao.class);
	private IEdgeGatewayDao gatewayDao = DaoFactory.getDao(IEdgeGatewayDao.class);
	
	private IActivityLogService logService = new ActivityLogServiceImpl();
	
	public LicenseCheckResult checkLicense(UDP_CLIENT_TYPE type,
			MachineInfo machine, long required_feature) throws EdgeServiceFault{
		logger.info("[LicenseServiceImplWrapper] checkLicense start");
		com.ca.arcflash.webservice.edge.license.LicenseCheckResult lic;
		if(type==UDP_CLIENT_TYPE.UDP_WINDOWS_AGENT || type==UDP_CLIENT_TYPE.UDP_LINUX_AGENT){
			if(!hasFeature(required_feature, LicenseDef.SUBLIC_OS_PM)){ //D2D in VM
				required_feature=checkHypervisorInfo(type, machine, required_feature);
			}else{
				required_feature=checkHypervisorInfo(type, machine, required_feature);
				if(hasFeature(required_feature, LicenseDef.SUBLIC_OS_HYPERV)){
					removeFeature(required_feature, LicenseDef.SUBLIC_OS_PM);
					logger.info("license checkHypervisorInfo force SUBLIC_OS_HYPERV");
				}
			}
		}
		if(type==UDP_CLIENT_TYPE.UDP_WINDOWS_AGENT){
			lic = licenseServiceNew.checkLicense(type, machine, required_feature);
			if(lic==null){
				if(hasFeature(required_feature, LicenseDef.SUBLIC_APP_EXCHANGE) || hasFeature(required_feature, LicenseDef.SUBLIC_App_SQL)){
					long required_feature_low = removeFeature(required_feature, LicenseDef.SUBLIC_APP_EXCHANGE|LicenseDef.SUBLIC_App_SQL);
					lic = licenseServiceNew.checkLicense(type, machine, required_feature_low);
				}
			}
		}else{
			lic = licenseServiceNew.checkLicense(type, machine, required_feature);
		}
		
		if(type==UDP_CLIENT_TYPE.UDP_WINDOWS_AGENT || type==UDP_CLIENT_TYPE.UDP_LINUX_AGENT){
			if(hasFeature(required_feature, LicenseDef.SUBLIC_OS_HYPERV)){
				int hostId=getHostId(machine);
				if(hostId!=0)
					if(lic!=null){
						if(StringUtil.isEqual(machine.getServerName(), lic.getMachine_name())){
							logger.info("checkLicense for hypervisor mark, vm:"+machine.getHostName() +" hypervisor:" + machine.getServerName());
							markVmUsingHypervLicense(machine.getServerName(), hostId);
							licenseServiceNew.deleteLicenseByMachine(machine.getHostName(), type);
						}else{
							logger.info("checkLicense for hypervisor unmark1, vm:"+machine.getHostName() +" hypervisor:" + machine.getServerName());
							unmarkVmUsingHypervLicense(machine.getServerName(), hostId);
							checkAndReleaseHypervLicense(machine);
						}
					}else{
						logger.info("checkLicense for hypervisor unmark2, vm:"+machine.getHostName() +" hypervisor:" + machine.getServerName());
						unmarkVmUsingHypervLicense(machine.getServerName(), hostId);
						checkAndReleaseHypervLicense(machine);
					}
			}
		}
		
		if(type==UDP_CLIENT_TYPE.UDP_CLIENT_HBBU){
			logger.info("checkLicense for HBBU");
			if(lic!=null){
				markVmUsingHypervLicense(machine.getServerName(), -1);
			}else{
				unmarkVmUsingHypervLicense(machine.getServerName(), -1);
				checkAndReleaseHypervLicense(machine);
			}
		}
		
		if(lic!=null && hasFeature(required_feature, LicenseDef.SUBLIC_OS_HYPERV) && !hasFeature(required_feature, LicenseDef.SUBLIC_VSB)){
			logger.info("checkLicense saveVmInfo start");
			licenseServiceNew.saveVmInfo(machine, type == UDP_CLIENT_TYPE.UDP_CLIENT_HBBU);
		}
		
		if(lic==null){
			logger.info("checkLicense lic is null and findBestLicense start");
			BundledLicense bestlic = licenseServiceNew.findBestLicense(type, machine, required_feature);
			licenseServiceNew.logBestLicense(bestlic);
		}
		logger.info("[LicenseServiceImplWrapper] checkLicense end");
		return lic;
	}
	
	private int getHostId(MachineInfo machine){
		int[] hostId = new int[1];
		connectInfoDao.as_edge_GetConnInfoByUUID(machine.getHostUuid(), hostId, new String[1], new int[1], new int[1]);
		return hostId[0];
	}
	
	private void checkAndReleaseHypervLicense(MachineInfo machine) throws EdgeServiceFault {
		int[] usednum=new int[1];
		hypervisorDao.as_edge_hypervisor_vm_checkstate(machine.getServerName(), usednum);
		if(usednum[0]>0){
			
		}else{
			logger.info("checkAndReleaseHypervLicense deleteLicenseByMachine vm:" +machine.getHostName() +" hypervisor:" + machine.getServerName());
			licenseServiceNew.deleteLicenseByMachine(machine.getServerName(), UDP_CLIENT_TYPE.UDP_CLIENT_HBBU);
		}
		
	}


	private void unmarkVmUsingHypervLicense(String hypervisor, int hostId) {
		logger.info("checkLicense unmarkVmUsingHypervLicense, hypervisor:" + hypervisor+", hostid:"+hostId);
		hypervisorDao.as_edge_hypervisor_vm_unmark(hypervisor, hostId);
	}


	private void markVmUsingHypervLicense(String hypervisor, int hostId) {
		logger.info("checkLicense markVmUsingHypervLicense, hypervisor:" + hypervisor+", hostid:"+hostId);
		hypervisorDao.as_edge_hypervisor_vm_mark(hypervisor, hostId);
	}


	private long checkHypervisorInfo(UDP_CLIENT_TYPE type, MachineInfo machine, long required_feature) {
		if (machine.getHostUuid() == null || machine.getHostUuid().isEmpty()) {
			return required_feature;
		}
		
		int[] hostId = new int[1];
		connectInfoDao.as_edge_GetConnInfoByUUID(machine.getHostUuid(), hostId, new String[1], new int[1], new int[1]);
		if (hostId[0] == 0) {
			return required_feature;
		}
		
		List<IntegerId> gateway = new ArrayList<IntegerId>();
		gatewayDao.as_edge_gateway_entity_map_getGatewayId(hostId[0], EntityType.Node, gateway );
		if (gateway.size() == 0) {
			return required_feature;
		}
		int gatewayid=gateway.get(0).getId();
		
		String[] esxHost = new String[1];
		int[] esxid = new int[1];
		int[] essential = new int[1];
		int[] socketCount = new int[1];
		esxDao.as_edge_esx_getLicenseInfo(hostId[0], esxid, esxHost, essential, socketCount);
		if (esxHost[0] != null) {
			machine.setServerName(esxHost[0]);
			machine.setServerSocketCount(socketCount[0]);
			logger.info("license checkHypervisorInfo addFeature SUBLIC_OS_HYPERV");
			required_feature = addFeature(required_feature, LicenseDef.SUBLIC_OS_HYPERV);
			
			if (essential[0] > 0) {
				logger.info("license checkHypervisorInfo addFeature SUBLIC_VMWare_Essential");
				required_feature = addFeature(required_feature, LicenseDef.SUBLIC_VMWare_Essential);
			}
			
			return required_feature;
		}
		
		String[] hyperVHost = new String[1];
		hyperVDao.as_edge_hyperv_getLicenseInfo(hostId[0], new int[1], hyperVHost, socketCount);
		if (hyperVHost[0] != null) {
			machine.setServerName(hyperVHost[0]);
			machine.setServerSocketCount(socketCount[0]);
			logger.info("license checkHypervisorInfo addFeature SUBLIC_OS_HYPERV");
			return addFeature(required_feature, LicenseDef.SUBLIC_OS_HYPERV);
		}
		
		String[] hypervisor = new String[1];
		hypervisorDao.as_edge_hypervisor_vm_getLicenseInfo(hostId[0], hypervisor, socketCount);
		if (hypervisor[0] != null) {
			List<EdgeEsx> esxList = new ArrayList<EdgeEsx>();
			esxDao.as_edge_esx_getByName(gatewayid, hypervisor[0], esxList);
			if (!esxList.isEmpty()) {
				tryAddActivityLog(LogAddEntity.create(Severity.Error, hostId[0], EdgeCMWebServiceMessages.getMessage("SepcifyHypervisor_OtherIsEsx", hypervisor[0])));
				return required_feature;
			}
			
			List<EdgeHyperV> hyperVList = new ArrayList<EdgeHyperV>();
			hyperVDao.as_edge_hyperv_getByName(gatewayid, hypervisor[0], hyperVList);
			if (!hyperVList.isEmpty()) {
				tryAddActivityLog(LogAddEntity.create(Severity.Error, hostId[0], EdgeCMWebServiceMessages.getMessage("SepcifyHypervisor_OtherIsHyperV", hypervisor[0])));
				return required_feature;
			}
			
			machine.setServerName(hypervisor[0]);
			machine.setServerSocketCount(socketCount[0]);
			logger.info("license checkHypervisorInfo addFeature SUBLIC_OS_HYPERV");
			return addFeature(required_feature, LicenseDef.SUBLIC_OS_HYPERV);
		}
		
		return required_feature;
	}
	
	private void tryAddActivityLog(LogAddEntity entity) {
		try {
			logService.addUnifiedLog(entity);
		} catch (EdgeServiceFault e) {
		}
	}

	private boolean hasFeature(long required_feature, long check_feature) {
		return EdgeLicenseUtil.hasFeature(required_feature, check_feature);
	}
	
	private long addFeature(long current_feature, long need_feature){
		return EdgeLicenseUtil.addFeature(current_feature, need_feature);
	}

	private long removeFeature(long current_feature, long remove_feature){
		return EdgeLicenseUtil.removeFeature(current_feature, remove_feature);
	}


	@Override
	public int addLicenseKeyNew(String license) throws EdgeServiceFault {
		return licenseServiceNew.addLicenseKeyNew(license);
	}


	@Override
	public List<LicenseInformation> getLicenses() throws EdgeServiceFault {
		return licenseServiceNew.getLicenses();
	}


	@Override
	public LicenseInformation getLicenseInfo(String key)
			throws EdgeServiceFault {
		return licenseServiceNew.getLicenseInfo(key);
	}


	@Override
	public List<LicensedNodeInfo> getLicensedNodeList(BundledLicense licenseId)
			throws EdgeServiceFault {
		return licenseServiceNew.getLicensedNodeList(licenseId);
	}


	@Override
	public List<LicensedNodeInfo> getUnLicensedNodeList(BundledLicense licenseId)
			throws EdgeServiceFault {
		return licenseServiceNew.getUnLicensedNodeList(licenseId);
	}


	@Override
	public void releaseNodeFromLicenseNew(BundledLicense licenseId,
			List<LicensedNodeInfo> nodeList) throws EdgeServiceFault {
		licenseServiceNew.releaseNodeFromLicenseNew(licenseId, nodeList);
	}

	@Override
	public LicenseCheckResult checkLicenseNCE(MachineInfo machine, boolean isVM)
			throws EdgeServiceFault {
		return licenseServiceNew.checkLicenseNCE(machine, isVM);
	}

	@Override
	public List<LicenseInformation> getNceLicenseList() throws EdgeServiceFault {
		return licenseServiceNew.getNceLicenseList();
	}

	@Override
	public List<LicensedNodeInfo> getNodeListOfNceLicense()
			throws EdgeServiceFault {
		return licenseServiceNew.getNodeListOfNceLicense();
	}

	@Override
	public List<LicensedVmInfo> getVmListByHypervisor(String hypervisor)
			throws EdgeServiceFault {
		return licenseServiceNew.getVmListByHypervisor(hypervisor);
	}
}
