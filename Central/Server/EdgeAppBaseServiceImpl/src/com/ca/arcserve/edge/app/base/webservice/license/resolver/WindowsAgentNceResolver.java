package com.ca.arcserve.edge.app.base.webservice.license.resolver;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.edge.license.BundledLicense;
import com.ca.arcflash.webservice.edge.license.LicenseCheckResult;
import com.ca.arcflash.webservice.edge.license.LicenseCheckResult.LicenseExpiredState;
import com.ca.arcflash.webservice.edge.license.LicenseDef;
import com.ca.arcflash.webservice.edge.license.LicenseDef.UDP_CLIENT_TYPE;
import com.ca.arcflash.webservice.edge.license.LicenseStatus;
import com.ca.arcflash.webservice.edge.license.MachineInfo;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeEsxDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHyperVDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHypervisorDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.license.ILicenseLocalWrapper;
import com.ca.arcserve.edge.app.base.webservice.license.ILicenseModuleWrapper;

public class WindowsAgentNceResolver {
	protected final static Logger logger=Logger.getLogger(WindowsAgentNceResolver.class);
	private IEdgeConnectInfoDao connectInfoDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
	private IEdgeEsxDao esxDao = DaoFactory.getDao(IEdgeEsxDao.class);
	private IEdgeHyperVDao hyperVDao = DaoFactory.getDao(IEdgeHyperVDao.class);
	private IEdgeHypervisorDao hypervisorDao = DaoFactory.getDao(IEdgeHypervisorDao.class);
	
	private ILicenseModuleWrapper wrapper;
	private ILicenseLocalWrapper localWrapper;

	public WindowsAgentNceResolver(ILicenseModuleWrapper wrapper,
			ILicenseLocalWrapper localWrapper) {
				this.wrapper = wrapper;
				this.localWrapper = localWrapper;
	}

	public LicenseCheckResult checkLicenseNCE(MachineInfo machine, boolean isVM) throws EdgeServiceFault{
		String hostname=machine.getHostName();
		if(!wrapper.hasAnyLicense()){
			LicenseStatus r = wrapper.useLicense(BundledLicense.UDPLIC_ALP.getCode(), 1);
			if(LicenseStatus.TRIAL == r){
				if(localWrapper.findLicenseNce(hostname)){
					localWrapper.deleteLicenseNce(hostname);
				}
				localWrapper.useLicense(hostname, LicenseDef.UDP_CLIENT_TYPE.UDP_WINDOWS_AGENT, BundledLicense.UDPLIC_TRIAL_LICENSE.getCode(), 0, 1);
				return new LicenseCheckResult(BundledLicense.UDPLIC_TRIAL_LICENSE, LicenseCheckResult.LicenseExpiredState.Trial, hostname, LicenseDef.UDP_CLIENT_TYPE.UDP_WINDOWS_AGENT, 1);
			}
		}
		if(isNodeHasHypervisor(machine, isVM)){
			BundledLicense lic = localWrapper.findLicense(machine.getServerName(), LicenseDef.UDP_CLIENT_TYPE.UDP_CLIENT_HBBU, new int[1]);
			if(lic!=null){
				int used=localWrapper.getUsed(lic.getCode());
				LicenseStatus v = wrapper.useLicense(lic.getCode(), used);
				boolean r=isLicenseAvailable(v);
				if(r){
					if(localWrapper.findLicenseNce(hostname)){
						localWrapper.deleteLicenseNce(hostname);
					}
					return new LicenseCheckResult(lic, getExpiredState(v), machine.getServerName(), LicenseDef.UDP_CLIENT_TYPE.UDP_CLIENT_HBBU, 1);
				}
			}
		}
		return createNceResult(hostname);
	}
	
	private boolean isLicenseAvailable(LicenseStatus status){
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
	
	private LicenseExpiredState getExpiredState(LicenseStatus v) {
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

	private boolean isNodeHasHypervisor(MachineInfo machine, boolean isVm) {
		if(isVm){
			if (machine.getHostUuid() == null || machine.getHostUuid().isEmpty()) {
				return false;
			}
			
			int[] hostId = new int[1];
			connectInfoDao.as_edge_GetConnInfoByUUID(machine.getHostUuid(), hostId, new String[1], new int[1], new int[1]);
			if (hostId[0] == 0) {
				return false;
			}
			
			String[] esxHost = new String[1];
			int[] esxid = new int[1];
			int[] essential = new int[1];
			int[] socketCount = new int[1];
			esxDao.as_edge_esx_getLicenseInfo(hostId[0], esxid, esxHost, essential, socketCount);
			if (esxHost[0] != null) {
				machine.setServerName(esxHost[0]);
				machine.setServerSocketCount(socketCount[0]);
				logger.debug("license checkHypervisorInfo addFeature SUBLIC_OS_HYPERV");
				
				return true;
			}
			
			String[] hyperVHost = new String[1];
			hyperVDao.as_edge_hyperv_getLicenseInfo(hostId[0], new int[1], hyperVHost, socketCount);
			if (hyperVHost[0] != null) {
				machine.setServerName(hyperVHost[0]);
				machine.setServerSocketCount(socketCount[0]);
				logger.debug("license checkHypervisorInfo addFeature SUBLIC_OS_HYPERV");
				return true;
			}
			
			String[] hypervisor = new String[1];
			hypervisorDao.as_edge_hypervisor_vm_getLicenseInfo(hostId[0], hypervisor, socketCount);
			if (hypervisor[0] != null) {
				machine.setServerName(hypervisor[0]);
				machine.setServerSocketCount(socketCount[0]);
				logger.debug("license checkHypervisorInfo addFeature SUBLIC_OS_HYPERV");
				return true;
			}
		}
	
		return false;
	}

	private LicenseCheckResult createNceResult(String hostname) {
		UDP_CLIENT_TYPE clienttype = LicenseDef.UDP_CLIENT_TYPE.UDP_WINDOWS_AGENT;	// not support linux
		BundledLicense old = localWrapper.findLicense(hostname, clienttype, new int[1]);
		BundledLicense oldneed = localWrapper.findNeedLicense(hostname, clienttype);
		if(old!=null || oldneed!=null){
			logger.info("deleteLicenseByNode for NCE "+hostname+(oldneed!=null?" (needed)":""));
			localWrapper.deleteLicenseByNode(hostname, clienttype);
		}
		if(!localWrapper.findLicenseNce(hostname)){
			localWrapper.addLicenseNce(hostname);
			logger.info("create nce license for "+hostname);
		}
		return new LicenseCheckResult(BundledLicense.UDPLIC_NCE_LICENSE, LicenseCheckResult.LicenseExpiredState.Valid, hostname, clienttype, 1);
	}
}
