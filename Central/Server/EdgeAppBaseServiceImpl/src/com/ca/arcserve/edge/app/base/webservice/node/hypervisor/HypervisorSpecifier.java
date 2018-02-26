package com.ca.arcserve.edge.app.base.webservice.node.hypervisor;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.edge.license.LicenseDef.UDP_CLIENT_TYPE;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.schedulers.EdgeExecutors;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.IActivityLogService;
import com.ca.arcserve.edge.app.base.webservice.contract.common.HostTypeUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Utils;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.license.LicenseMachineType;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogAddEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Hypervisor;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ProtectionType;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacade;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacadeFactory;
import com.ca.arcserve.edge.app.base.webservice.license.ILicenseService;
import com.ca.arcserve.edge.app.base.webservice.license.LicenseServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.node.NodeServiceImpl;

public abstract class HypervisorSpecifier {
	
	private static Logger logger = Logger.getLogger(HypervisorSpecifier.class);
	protected IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
	
	protected static class Result {
		public boolean succeed;
		public LogAddEntity logEntity;
		public EdgeServiceFault fault;
		
		public Result() {
			this(null);
		}
		
		public Result(LogAddEntity logEntity) {
			succeed = true;
			this.logEntity = logEntity;
		}
		
		public Result(LogAddEntity logEntity, EdgeServiceFault fault) {
			succeed = false;
			this.logEntity = logEntity;
			this.fault = fault;
		}
	}
	
	private NodeServiceImpl nodeServiceImpl = new NodeServiceImpl();
	private IActivityLogService logService = new ActivityLogServiceImpl();
	private IEdgeHostMgrDao hostDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	
	public static HypervisorSpecifier createSpecifier(LicenseMachineType hypervisorType) throws EdgeServiceFault {
		if (hypervisorType == null) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "Invalid parameters, hypervisorType is null.");
		}
		
		switch (hypervisorType) {
		case VSHPERE_VM: return new EsxSpecifier();
		case HYPER_V_VM: return new HyperVSpecifier();
		case Other: return new DefaultHypervisorSpecifier();
		default: throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "Invalid hypervisor type " + hypervisorType);
		}
	}
	
	protected abstract LicenseMachineType getMachineType();
	protected abstract List<Result> doSpecify(Hypervisor hypervisor, List<EdgeHost> hosts);
	protected abstract String getSpecifyBeginMessage(Hypervisor hypervisor);
	protected abstract String getSpecifyEndMessage(Hypervisor hypervisor);
	protected abstract void testConnection(Hypervisor hypervisor) throws EdgeServiceFault;
	
	protected void validate(Hypervisor hypervisor) throws EdgeServiceFault {
		if (hypervisor == null) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "hypervisor value is null.");
		}
		
		if (StringUtil.isEmptyOrNull(hypervisor.getServerName())) {
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, "Invalid hypervisor value, server name is null or empty.");
		}
	}
	
	public void specifySingle(Hypervisor hypervisor, int nodeId) throws EdgeServiceFault {
		validate(hypervisor);
		
		testConnection(hypervisor);
		
		List<EdgeHost> hosts = nodeServiceImpl.getEdgeHostByIDs(Arrays.asList(nodeId), null);
		if (hosts.isEmpty()) {
			return;
		}
		
		Result result = validateVMType(hosts.get(0));
		if (!result.succeed) {
			throw result.fault;
		}
		
		List<Result> results = doSpecify(hypervisor, hosts);
		if (!results.isEmpty() && !results.get(0).succeed) {
			throw results.get(0).fault;
		}
	}
	
	private Result validateVMType(EdgeHost host) {
		if (HostTypeUtil.isLinuxNode(host.getRhostType()) || Utils.hasBit(host.getProtectionTypeBitmap(), ProtectionType.LINUX_D2D_SERVER)) {
			return new Result();
		}
		
		if (HostTypeUtil.isVMWareVirtualMachine(host.getRhostType())) {
			String errorMessage = EdgeCMWebServiceMessages.getMessage("SepcifyHypervisor_EsxVMNotAllowed");
			return new Result(
					LogAddEntity.create(Severity.Warning, host.getRhostid(), errorMessage),
					EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, errorMessage));
		}
		
		if (HostTypeUtil.isHyperVVirtualMachine(host.getRhostType())) {
			String errorMessage = EdgeCMWebServiceMessages.getMessage("SepcifyHypervisor_HyperVVMNotAllowed");
			return new Result(
					LogAddEntity.create(Severity.Warning, host.getRhostid(), errorMessage),
					EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_General, errorMessage));
		}
		
		LicenseMachineType nodeMachineType = LicenseMachineType.parseValue(host.getMachineType());
		LicenseMachineType newMachineType = tryDetectMachineType(host);
		if (newMachineType != LicenseMachineType.Undetected) {
			nodeMachineType = newMachineType; 
		}
		
		if (nodeMachineType == LicenseMachineType.Undetected) {
			String errorMessage = EdgeCMWebServiceMessages.getMessage("SepcifyHypervisor_UndetectedError");
			return new Result(
					LogAddEntity.create(Severity.Error, host.getRhostid(), errorMessage),
					EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_SpecifyHypervisor_Undetected, errorMessage));
		}
		
		if (nodeMachineType == LicenseMachineType.PHYSICAL_MACHINE) {
			String errorMessage = EdgeCMWebServiceMessages.getMessage("SepcifyHypervisor_PhysicalNotAllowed");
			return new Result(
					LogAddEntity.create(Severity.Error, host.getRhostid(), errorMessage),
					EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_SpecifyHypervisor_Physical, errorMessage));
		}
		
		if (nodeMachineType == LicenseMachineType.Unsupported) {
			String errorMessage = EdgeCMWebServiceMessages.getMessage("SepcifyHypervisor_Unsupported");
			return new Result(
					LogAddEntity.create(Severity.Error, host.getRhostid(), errorMessage),
					EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_SpecifyHypervisor_Unsupported, errorMessage));
		}
		
		if (nodeMachineType != getMachineType()) {
			String errorMessage = EdgeCMWebServiceMessages.getMessage("SepcifyHypervisor_TypeMismatch");
			return new Result(
					LogAddEntity.create(Severity.Error, host.getRhostid(), errorMessage),
					EdgeServiceFault.getFault(EdgeServiceErrorCode.Node_SpecifyHypervisor_TypeMismatch, errorMessage));
		}
		
		return new Result();
	}
	
	private LicenseMachineType tryDetectMachineType(EdgeHost host) {
		if (StringUtil.isEmptyOrNull(host.getRhostname()) || StringUtil.isEmptyOrNull(host.getUsername())) {
			return null;
		}
		
		GatewayEntity gateway = null;
		
		try
		{
			IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
			gateway = gatewayService.getGatewayByHostId( host.getRhostid() );
		}
		catch (Exception e)
		{
			logger.error( "tryDetectMachineType(): Error getting gateway information.", e );
		}
		
		try {
			IRemoteNativeFacadeFactory remoteNativeFacadeFactory = EdgeFactory.getBean(IRemoteNativeFacadeFactory.class);
			IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( gateway.getId() );
			LicenseMachineType machineType = nativeFacade.getLicenseMachineType(host.getRhostname(), host.getUsername(), host.getPassword());
			if (machineType != LicenseMachineType.Undetected) {
				hostDao.as_edge_host_updateMachineType(host.getRhostid(), machineType.getValue());
			}
			
			return machineType;
		} catch (Exception e) {
			logger.debug("HypervisorSpecifier - try detect machine type failed.", e);
			return LicenseMachineType.Undetected;
		}
	}

	public void specifyNodes(final Hypervisor hypervisor, final List<Integer> nodeIds) throws EdgeServiceFault {
		validate(hypervisor);
		
		testConnection(hypervisor);
		
		EdgeExecutors.getCachedPool().submit(new Runnable() {
			
			@Override
			public void run() {
				tryAddUnifiedLog(LogAddEntity.create(Severity.Information, getSpecifyBeginMessage(hypervisor)));
				
				try {
					List<EdgeHost> hosts = nodeServiceImpl.getEdgeHostByIDs(nodeIds, null);
					if (hosts.isEmpty()) {
						return;
					}
					
					Iterator<EdgeHost> hostIterator = hosts.iterator();
					while (hostIterator.hasNext()) {
						EdgeHost host = hostIterator.next();
						Result result = validateVMType(host);
						if (!result.succeed) {
							tryAddUnifiedLog(result.logEntity);
							hostIterator.remove();
						}
					}
					
					List<Result> results;
					results = doSpecify(hypervisor, hosts);
					for (Result result : results) {
						tryAddUnifiedLog(result.logEntity);
					}
				} finally {
					tryAddUnifiedLog(LogAddEntity.create(Severity.Information, getSpecifyEndMessage(hypervisor)));
				}
			}
			
		});
	}
	
	protected void tryAddUnifiedLog(LogAddEntity entity) {
		if (entity == null) {
			return;
		}
		
		try {
			logService.addUnifiedLog(entity);
		} catch (Exception e) {
			logger.error("HypervisorSpecifier - try add unified log failed.", e);
		}
	}
	
	protected void removeLicense(EdgeHost host) {
		ILicenseService service = new LicenseServiceImpl();
		boolean linux = HostTypeUtil.isLinuxNode(host.getRhostType()) || Utils.hasBit(host.getProtectionTypeBitmap(), ProtectionType.LINUX_D2D_SERVER);
		
		try {
			service.deleteLicenseByMachine(host.getRhostname(), linux ? UDP_CLIENT_TYPE.UDP_LINUX_AGENT : UDP_CLIENT_TYPE.UDP_WINDOWS_AGENT);
		} catch (Exception e) {
			logger.error("release physical machine license failed, error message = " + e.getMessage());
		}
	}

}
