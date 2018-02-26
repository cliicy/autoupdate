package com.ca.arcflash.webservice.common;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.ha.vmwaremanager.CAVMwareInfrastructureManagerFactory;
import com.ca.arcflash.ha.vmwaremanager.CAVMwareVirtualInfrastructureManager;
import com.ca.arcflash.ha.vmwaremanager.ESXNode;
import com.ca.arcflash.ha.vmwaremanager.InvalidLoginException;
import com.ca.arcflash.ha.vmwaremanager.VM_Info;
import com.ca.arcflash.jobscript.failover.VirtualizationType;
import com.ca.arcflash.jobscript.replication.ReplicationDestination;
import com.ca.arcflash.jobscript.replication.ReplicationJobScript;
import com.ca.arcflash.jobscript.replication.VMwareESXStorage;
import com.ca.arcflash.jobscript.replication.VMwareVirtualCenterStorage;
import com.ca.arcflash.webservice.FlashServiceErrorCode;
import com.ca.arcflash.webservice.data.MachineDetail;
import com.ca.arcflash.webservice.edge.d2dreg.ApplicationType;
import com.ca.arcflash.webservice.edge.d2dreg.D2DEdgeRegistration;
import com.ca.arcflash.webservice.edge.d2dreg.EdgeRegInfo;
import com.ca.arcflash.webservice.edge.license.LICENSEDSTATUS;
import com.ca.arcflash.webservice.edge.license.LicenseCheckResult;
import com.ca.arcflash.webservice.edge.license.LicenseDef;
import com.ca.arcflash.webservice.edge.license.MachineInfo;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.toedge.IEdgeCM4D2D;
import com.ca.arcflash.webservice.toedge.WebServiceFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;

public class VCMLicenseCheck extends LicenseCheck<VCMMachineInfo>{

	private static final VCMLicenseCheck instance = new VCMLicenseCheck();

	protected static final String licFileName = "vcmLicense.lic";

	public static final String VCM_LICENSE_PHYSICAL = "physical";

	public static final String VCM_LICENSE_VM = "vm";

	private static ArrayList<String> locahostIPList;

	VCMLicenseCheck() {
		super(licFileName);
	}

	public static VCMLicenseCheck getInstance() {
		return instance;
	}

	protected String getCacheKey(VCMMachineInfo subject) {
		return "vm$$" +  subject.getMachineInfo().getHostName() + "$$" + subject.getMachineInfo().getServerName();
	}

	protected LICENSEDSTATUS getLicenseFromEdge(VCMMachineInfo vcmMachineInfo) throws LicenseCheckException {

		LicenseDef.UDP_CLIENT_TYPE type = LicenseDef.UDP_CLIENT_TYPE.UDP_CLIENT_UNKNOWN;
		long required_feature = LicenseDef.SUBLIC_VSB;
		if (vcmMachineInfo.getNodeType() == VCMMachineInfo.VCMNodeType.HBBU_VM) {
			type = LicenseDef.UDP_CLIENT_TYPE.UDP_CLIENT_HBBU;

			required_feature |= LicenseDef.SUBLIC_OS_HYPERV;
		} else {
			type = LicenseDef.UDP_CLIENT_TYPE.UDP_WINDOWS_AGENT;

			if (vcmMachineInfo.getNodeType() == VCMMachineInfo.VCMNodeType.PHYSICAL)
				required_feature |= LicenseDef.SUBLIC_OS_PM;
			
			if (vcmMachineInfo.getOsProductType() == 1)
				required_feature |= LicenseDef.SUBLIC_OS_WORKSTATION;
			else if (vcmMachineInfo.getOsProductType() == 2)
				required_feature |= LicenseDef.SUBLIC_OS_SBS;
			else if (vcmMachineInfo.getOsProductType() == 3)
				required_feature |= LicenseDef.SUBLIC_OS_SERVER;
		}

		return checkVCMLicense(type, vcmMachineInfo.getMachineInfo(), required_feature);
	}

	/**
	 * Get the vStorage API license status for ESX/VC server.
	 * @param jobScript Replication job script.
	 * @return {@code LICENSEDSTATUS.VALID}: vStorage API license is available.
	 * <br>{@code LICENSEDSTATUS.INVALID}: vStorage API license is unavailable.
	 * <br>{@code LICENSEDSTATUS.ERROR}: check vStorage API license failed.
	 * @author qiubo01
	 */
	public LICENSEDSTATUS getVMwareVStorageApiLicense(ReplicationJobScript jobScript) throws Exception{
		if (jobScript.getVirtualType() != VirtualizationType.VMwareESX && jobScript.getVirtualType() != VirtualizationType.VMwareVirtualCenter) {
			return LICENSEDSTATUS.ERROR;
		}
		
		List<ReplicationDestination> dests = jobScript.getReplicationDestination();
		if (dests.isEmpty()) {
			return LICENSEDSTATUS.ERROR;
		}
		
		ReplicationDestination dest = dests.get(0);
		
		String hypervisorHostname;
		String hypervisorUsername;
		String hypervisorPassword;
		String hypervisorProtocol;
		int hypervisorPort;
		ESXNode esxNode = null;
		
		if (dest instanceof VMwareESXStorage) {
			VMwareESXStorage storage = (VMwareESXStorage) dest;
			hypervisorHostname = storage.getESXHostName();
			hypervisorUsername = storage.getESXUserName();
			hypervisorPassword = storage.getESXPassword();
			hypervisorProtocol = storage.getProtocol();
			hypervisorPort = storage.getPort();
		} else if (dest instanceof VMwareVirtualCenterStorage) {
			VMwareVirtualCenterStorage storage = (VMwareVirtualCenterStorage) dest;
			hypervisorHostname = storage.getVirtualCenterHostName();
			hypervisorUsername = storage.getVirtualCenterUserName();
			hypervisorPassword = storage.getVirtualCenterPassword();
			hypervisorProtocol = storage.getProtocol();
			hypervisorPort = storage.getPort();
			
			esxNode = new ESXNode();
			esxNode.setEsxName(storage.getEsxName());
			esxNode.setDataCenter(storage.getDcName());
		} else {
			return LICENSEDSTATUS.ERROR;
		}
		
		CAVMwareVirtualInfrastructureManager vmwareManager = null;
		
		try {
			vmwareManager = CAVMwareInfrastructureManagerFactory.getCAVMwareVirtualInfrastructureManager(
					hypervisorHostname, hypervisorUsername, hypervisorPassword, hypervisorProtocol, true, hypervisorPort);
			
			int result = esxNode == null ? vmwareManager.CheckESXLicenseforESX() : vmwareManager.CheckESXLicense(esxNode);
			logger.debug("vmwareManager.CheckESXLicense(ESXNode), the result is " + result);
			
			return result == 1 ? LICENSEDSTATUS.VALID : LICENSEDSTATUS.INVALID;
		} catch (Exception e) {
			logger.warn("getVStorageApiLicense failed, error message = " + e.getMessage());
			throw new Exception(e);
		} finally {
			if(vmwareManager != null) {
				try {
					vmwareManager.close();
				} catch (Exception e) {
					logger.error("close vmware manager failed, error message = " + e.getMessage());
				}
			}
		}
	}

	/**
	 * validate whether the vm is in the specified ESX server/VCenter
	 * @param machine
	 * @return true if it is; otherwise, false
	 * @throws ServiceException 
	 */
	public static VM_Info validateVMByHostName(MachineDetail machine) throws ServiceException {
		String vmName = machine.getHostName();
		String esxHost = machine.getESXHostName();
		String serverName = machine.getHypervisorHostName();
		String protocol = machine.getHypervisorProtocol();
		int port = machine.getHypervisorPort();
		String userName = machine.getHypervisorUserName();
		String password = machine.getHypervisorPassword();

		CAVMwareVirtualInfrastructureManager vmwareManager = null;
		
		if(StringUtil.isEmptyOrNull(vmName)) {
			vmName = getVMHostName();
		}

		try {
			vmwareManager = CAVMwareInfrastructureManagerFactory
				.getCAVMwareVirtualInfrastructureManager(serverName,userName,password, protocol, true, port);
		} catch(InvalidLoginException e) {
			logger.error("Invalid VC/ESX credentials. Machine detail:"	+ machine + ". Error:" + e.getMessage());
			throw new ServiceException("Invalid VC/ESX credentials", FlashServiceErrorCode.VCM_VC_ESX_INVALID_CREDENTIALS);
		}
		catch (Exception e) {
			logger.error("Failed to get vmware web service connection. Machine detail:"
					+ machine + ". Error:" + e.getMessage(), e);
			throw new ServiceException("Invalid VC/ESX credentials", FlashServiceErrorCode.VCM_VC_ESX_CONNECT_ERROR);
		};

		try {
			VM_Info esxVM = null;
			if(vmwareManager != null) {
				ArrayList<String> ipList = getLocalHostIpAddresses();
				ArrayList<ESXNode> nodeList = vmwareManager.getESXNodeList();
				ESXNode esxNode = null;
				if(nodeList != null) {
					for(int order = 0, count = nodeList.size(); order < count; order++) {
						esxNode = nodeList.get(order);
						if(esxHost != null && esxHost.length() > 0){
							if(esxHost.equalsIgnoreCase(esxNode.getEsxName())) {
								esxVM = vmExistenceOnESX(vmwareManager, ipList, esxNode, vmName);
								break;
							}
						}
						else {
							esxVM = vmExistenceOnESX(vmwareManager, ipList, esxNode, vmName);
							if(esxVM != null) {
								if(esxHost == null || esxHost.length() == 0) {
									String esxHostName = esxNode.getEsxName();
									try {
										InetAddress netAddress = InetAddress.getByName(esxNode.getEsxName());
										String host = netAddress.getHostName();
										if(!StringUtil.isEmptyOrNull(host))
											esxHostName = host;
									} catch (Exception e) {					
										logger.warn("Fail to get host name by InetAddress. Error:" + e.getMessage(), e); 
									}
									
									machine.setESXHostName(esxHostName);
								}
								break;
							}
						}
					}
				}
				else {
					logger.info("nodeList is empty");
				}
			}

			if(logger.isDebugEnabled())
				logger.debug("Check whether " + vmName + " is running in Esx Server:" + (esxVM != null));

			return esxVM;
		} catch (Exception e) {
			logger.error("Failed to validate whether " + vmName
					+ " is in ESX server " + esxHost + ". Machine detail:"  + machine + ". Error:" + e.getMessage(), e);
		}
		finally {
			if(vmwareManager != null) {
				try {
					vmwareManager.close();
				} catch (Exception e) {
				}
			}
		}

		return null;
	}

	private static String getVMHostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
		}
		catch(Exception e) {
			logger.error("Fail to get host name.", e);
		}
		return null;
	}

	private static VM_Info vmExistenceOnESX(
			CAVMwareVirtualInfrastructureManager vmwareManager,
			ArrayList<String> ipList, ESXNode esxNode, String vmName) {
		VM_Info vm_info;
		try {
			vm_info = vmwareManager.getVMInfoByIP(ipList, esxNode.getEsxName(), esxNode.getDataCenter());
			logger.info("machine: " + vmName + (vm_info != null ? "exists " : " does not exist ") + " on ESX " + esxNode.getEsxName());
			return vm_info;
		} catch (Exception e) {
			logger.error("Failed to validate whether vm is in ESX server " + esxNode.getEsxName() 
					+ ", VC: "  + esxNode.getDataCenter() + ". Error:" + e.getMessage(), e);
		}
		return null;
	}

	public static synchronized ArrayList<String> getLocalHostIpAddresses(){
		if(locahostIPList == null) {
			Set<String> ipSet = new HashSet<String>();
			try {
				InetAddress local = InetAddress.getLocalHost();
				addIpToList(local.getHostAddress(), ipSet);
				
				InetAddress[] addressList = InetAddress.getAllByName(local.getCanonicalHostName());
				if(addressList != null && addressList.length > 1) {
					for(int i = 0; i < addressList.length; i++) 
						addIpToList(addressList[i].getHostAddress(), ipSet);
				}
			}catch (Exception e) {
				logger.error("Fail to resolve all ip addresses of the local host by InetAddress." + e.getMessage());
			}
			
			 try {
				 Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
				 if(en != null) {
					while (en.hasMoreElements()) {
						NetworkInterface intf = en.nextElement();
						Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
						if(enumIpAddr != null) {
							while (enumIpAddr.hasMoreElements()) {
								addIpToList(enumIpAddr.nextElement().getHostAddress(), ipSet);
							}
						}
					}
				 }
			} catch (Exception e) {
				logger.error("Fail to resolve all ip addresses of the local host by NetworkInterface." + e.getMessage());
			}
			
			locahostIPList = new ArrayList<String>();
			locahostIPList.addAll(ipSet);
			logger.info(locahostIPList.size() + "Ip Addresses:" + StringUtil.convertList2String(locahostIPList));
		}
		return locahostIPList;
	}

	private static void addIpToList(String hostAddress, Set<String> ipList) {
		if(!"127.0.0.1".equals(hostAddress.trim()))
			ipList.add(hostAddress);
	}
//
	private LICENSEDSTATUS checkVCMLicense(LicenseDef.UDP_CLIENT_TYPE type, MachineInfo machine, long required_feature) throws LicenseCheckException{

		EdgeRegInfo info = null;
		try {
			D2DEdgeRegistration edgeRegInfo = new D2DEdgeRegistration();

			info = edgeRegInfo.getEdgeRegInfo(ApplicationType.VirtualConversionManager);

			if(info == null)
				return LICENSEDSTATUS.VALID;
		}
		catch(Exception ex) {
			logger.error("Unexpected exception occurs. Fail to get Fetch license from Edge:" + ex.getMessage(), ex);
			return null;
		}
		
		IEdgeCM4D2D edgeService = null;
		try {
			edgeService = WebServiceFactory.getEdgeService(info.getEdgeWSDL(),IEdgeCM4D2D.class);
		}
		catch(Exception ex) {
			logger.error("Unexpected exception occurs when connecting to Edge. " +
					"Fail to get Fetch license from Edge:" + ex.getMessage(), ex);
			throw new LicenseCheckException("Fails to connect to Edge:" + info.getEdgeWSDL(), LicenseCheckException.FAIL_CONNECT_EDGE);
		}
		
		try {
			edgeService.validateUserByUUID(info.getEdgeUUID());
		} catch (EdgeServiceFault e) {
			logger.error("Check VSB License From Edge failed. Login Edge Server:" + info.getEdgeHostName() + " Failed.", e);
			throw new LicenseCheckException("Fails to Login Edge:" + info.getEdgeWSDL(), LicenseCheckException.FAIL_CONNECT_EDGE);
		}

		try {
			LICENSEDSTATUS status = LICENSEDSTATUS.INVALID;;
			
			String msg = String.format("VCMLicenseCheck: hostName[%s] hypervisor[%s] CPU[%d] type[%d] feature[%d]", 
					machine.getHostName(), machine.getServerName(), machine.getSocketCount(), type.getValue(), required_feature);
			logger.info(msg);

			LicenseCheckResult licenseResult = edgeService.checkLicense(type, machine, required_feature);
			if (licenseResult != null) {
				status = LICENSEDSTATUS.VALID;

				msg = String.format("VCMLicenseCheck: result[%s - %s]", licenseResult.getLicense().getDisplayName(), licenseResult.getState().toString());
				logger.info(msg);
			} else {
				logger.info("VCMLicenseCheck: result[null]");
			}

			return status;

		} catch (Exception e) {
			logger.error("Internal error occurs in Edge side. Fail to get Fetch license from Edge:" 
					+ e.getMessage(), e);
			throw new LicenseCheckException("Internal error occurs in Edge side", LicenseCheckException.EDGE_INTERNAL_ERROR);
		}
		
	}

}
