package com.ca.arcserve.edge.app.base.webservice.node.discovery;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.NotPrintAttribute;
//import com.ca.arcflash.ha.vmwaremanagerIntf.CAVirtualInfrastructureManager;
import com.ca.arcserve.edge.app.base.schedulers.EdgeExecutors;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryESXOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryHyperVOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryMonitor;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryMonitor.DiscoverServerError;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryPhase;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryServiceStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryVirtualMachineInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RemoteNodeInfo;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacade;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacadeFactory;
import com.ca.arcserve.edge.app.base.webservice.jni.NativeFacade;
import com.ca.arcserve.edge.app.base.webservice.jni.NativeFacadeImpl;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.vmwaremanagement.IVmwareManagerService;
import com.ca.arcserve.edge.app.base.webservice.vmwaremanagement.IVmwareManagerServiceFactory;

public class DiscoveryService {
	
	private static DiscoveryService			instance = null;
	private static DiscoveryServiceStatus 	status = DiscoveryServiceStatus.DISCOVERY_SERVICE_STOPPED;
	private static final Logger logger = Logger.getLogger(DiscoveryService.class);
	private IRemoteNativeFacadeFactory remoteNativeFacadeFactory = EdgeFactory.getBean(IRemoteNativeFacadeFactory.class);

	private DiscoveryMonitor				discoveryMonitor = null;
	private long							discoveryStartTime = 0;
	private long							discoveryStopTime = 0;
	private Future<String>					task = null;

	protected DiscoveryService()
	{
	}
	@NotPrintAttribute
	public synchronized static DiscoveryService getInstance()
	{
		if(instance == null)
		{
			instance = new DiscoveryService();
		}

		return instance;
	}

	public synchronized void start()
	{
		DiscoveryServiceStatus status = queryStatus();
		if( (status == DiscoveryServiceStatus.DISCOVERY_SERVICE_START_PENDING) ||
			(status == DiscoveryServiceStatus.DISCOVERY_SERVICE_RUNNING) )
			return;

		setStatus(DiscoveryServiceStatus.DISCOVERY_SERVICE_START_PENDING);

		setStatus(DiscoveryServiceStatus.DISCOVERY_SERVICE_RUNNING);
	}

	public synchronized void cancel()
	{
		if(task == null) 	return;
		if(task.isDone())	return;

		task.cancel(true);

		if(discoveryMonitor != null)
	        {
			synchronized(this)
			{
				discoveryMonitor.setDiscoveryStatus(DiscoveryStatus.DISCOVERY_STATUS_CANCELLED);
				discoveryMonitor.setDiscoveryPhase(DiscoveryPhase.DISCOVERY_PHASE_END);
				discoveryMonitor.setCurrentProcessNodeName("");
				discoveryStartTime = 0;
				discoveryStopTime = System.currentTimeMillis();
			}
	        }

		try
		{
			ActivityLogServiceImpl activityLogService = new ActivityLogServiceImpl();
			ActivityLog activityLog = new ActivityLog();

			activityLog.setModule(Module.Common);
			activityLog.setSeverity(Severity.Information);
			activityLog.setTime(new Date(System.currentTimeMillis()));
			activityLog.setMessage("The operation of discovering Node form Active Directory is canceled by user.");

			activityLogService.addLog(activityLog);
		}
		catch (EdgeServiceFault e)
		{
			logger.error(e.getMessage(), e);
		}
	}

	public synchronized void stop()
	{
		DiscoveryServiceStatus status = queryStatus();
		if( (status == DiscoveryServiceStatus.DISCOVERY_SERVICE_STOP_PENDING) ||
			(status == DiscoveryServiceStatus.DISCOVERY_SERVICE_STOPPED) )
			return;

		setStatus(DiscoveryServiceStatus.DISCOVERY_SERVICE_STOP_PENDING);
		setStatus(DiscoveryServiceStatus.DISCOVERY_SERVICE_STOPPED);
	}

	private void setStatus(DiscoveryServiceStatus status)
	{
		DiscoveryService.status = status;
	}

	public DiscoveryServiceStatus queryStatus()
	{
		return status;
	}

	public void startDiscoveryMonitor()
	{
		synchronized(this)
		{
			if(discoveryMonitor == null)
		    {
				discoveryMonitor = new DiscoveryMonitor();
		    }

			discoveryMonitor.setCurrentProcessNodeName("");
			discoveryMonitor.setDiscoveryPhase(DiscoveryPhase.DISCOVERY_PHASE_NODE);
			discoveryMonitor.setProcessedNodeNum(0);
			discoveryMonitor.setElapsedTime(0);
			discoveryMonitor.setDiscoveryStatus(DiscoveryStatus.DISCOVERY_STATUS_ACTIVE);
			discoveryMonitor.setErrorCode(null);
			discoveryMonitor.getServerErrors().clear();
			discoveryStartTime = System.currentTimeMillis();
		}
	}

	public void stopDiscoveryMonitor()
	{
	    if(discoveryMonitor != null)
	    {
			synchronized(this)
			{
				if( discoveryMonitor.getErrorCode() != null)
				{
					discoveryMonitor.setDiscoveryStatus(DiscoveryStatus.DISCOVERY_STATUS_FAILED);
				}
				else
				{
				        discoveryMonitor.setDiscoveryStatus(DiscoveryStatus.DISCOVERY_STATUS_FINISHED);
				}

				discoveryMonitor.setDiscoveryPhase(DiscoveryPhase.DISCOVERY_PHASE_END);
				discoveryMonitor.setCurrentProcessNodeName("");
				discoveryStartTime = 0;
				discoveryStopTime = System.currentTimeMillis();
			}
	    }
	}

	public void setDiscoveryError(String errorCode)
	{
	    if(discoveryMonitor != null)
	    {
			synchronized(this)
			{
				discoveryMonitor.setErrorCode(errorCode);
			}
	    }
	}

	public boolean isDiscoveryMonitorRunning()
	{
		synchronized(this)
		{
			if( discoveryMonitor == null) 				return false;
			if( task != null && !task.isDone() )	 	return true;
			if( discoveryStartTime > 0 )				return true;
		}
		return false;
	}

	public DiscoveryMonitor getDiscoveryMonitor()
	{
		return discoveryMonitor;
	}
	@NotPrintAttribute
	public DiscoveryMonitor cloneDiscoveryMonitor()
	{
		DiscoveryMonitor monitor = null;

		if(discoveryMonitor != null)
		{
			synchronized (discoveryMonitor)
			{
				// We will wait GUI for 30 seconds to get the status
				if((discoveryStartTime == 0) && (task != null) && (task.isDone()))
				{
					//this is a special status!; discover meet warnning and user will see the monitor window
					if( !discoveryMonitor.getServerErrors().isEmpty() ) {
						monitor = new DiscoveryMonitor();
						monitor.setDiscoveryPhase(DiscoveryPhase.DISCOVERY_PHASE_END);
					}
					if((System.currentTimeMillis() - discoveryStopTime > 30000) || task.isCancelled())
					{
						discoveryMonitor.setDiscoveryStatus(DiscoveryStatus.DISCOVERY_STATUS_IDLE);
						discoveryMonitor.setElapsedTime(0);
						discoveryMonitor.setCurrentProcessNodeName("");
						discoveryMonitor.setErrorCode(null);
						discoveryMonitor.setProcessedNodeNum(0);
						discoveryMonitor.setDiscoveryPhase(DiscoveryPhase.DISCOVERY_PHASE_NONE);
						return monitor;
					}
				}

				if((discoveryStartTime > 0) && (task != null) && (!task.isDone()) && discoveryMonitor.getDiscoveryPhase() != DiscoveryPhase.DISCOVERY_PHASE_END)
				{
					discoveryMonitor.setElapsedTime(System.currentTimeMillis() - discoveryStartTime);
				}

				monitor = new DiscoveryMonitor();
				monitor.setCurrentProcessNodeName(discoveryMonitor.getCurrentProcessNodeName());
				monitor.setDiscoveryPhase(discoveryMonitor.getDiscoveryPhase());
				monitor.setElapsedTime(discoveryMonitor.getElapsedTime());
				monitor.setProcessedNodeNum(discoveryMonitor.getProcessedNodeNum());
				monitor.setDiscoveryStatus(discoveryMonitor.getDiscoveryStatus());
				monitor.setUuid(discoveryMonitor.getUuid());
				monitor.setServerErrors(discoveryMonitor.getServerErrors());
				monitor.setOption(discoveryMonitor.getOption());
			}
		}

		return monitor;
	}
	
	public void validateADAccount(GatewayId gatewayId, String server, String username, String password) throws EdgeServiceFault {
		IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( gatewayId );
		nativeFacade.verifyADAccount(server, username, password);
	}

	public synchronized String discoverNodeFromAD(DiscoveryOption[] options) throws EdgeServiceFault
	{
		if (options == null || options.length == 0) {
			return null;
		}
		
		if (isDiscoveryMonitorRunning() && (options[0].getTaskId() != -1)){ //if taskID == -1, means it will use UI task framework, no need DiscoveryMonitor. 
			return null;
		}
		DsDiscoveryActiveDirectory adDiscovery = new DsDiscoveryActiveDirectory(options );
		String uuid = UUID.randomUUID().toString();
		adDiscovery.setUuid(uuid);
		task = EdgeExecutors.getCachedPool().submit(adDiscovery);
		return uuid;
	}
	//fanda03 fix 102830. only used when one target name failed. not all task failed . 
	public void onDiscoverFailed( DiscoveryOption option, String errorMsg  ) {		
		DiscoverServerError adError = new DiscoverServerError();
		adError.setErrorMsg( errorMsg );
		if( option.getUserName()!=null ) {
			adError.setName( option.getUserName() );
		}
		else {
			adError.setName("");
		}
		discoveryMonitor.setServerError(adError);
	}
/*	public void probeNode(DiscoveryOption option) throws EdgeServiceFault
	{
		try
		{
			if( isDiscoveryMonitorRunning() ) return;

			if(option.getApplicationFilter().contains(DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_D2D))
			{
				DsDiscoveryServer d2dDiscovery = new DsDiscoveryServer(option);
				if(executorPool == null)	start();
				task = executorPool.submit(d2dDiscovery);
			}
		}
		catch (Exception e)
		{
			throw EdgeServiceFault.getFault(
				EdgeServiceErrorCode.Node_CantProbeD2DNodesWithUDP, "" );
		}
	}
*/
	public List<String> getDomainControllerList(String domainName) throws EdgeServiceFault
	{
		NativeFacade  nativeFacade = new NativeFacadeImpl();
		List<String> dcList = nativeFacade.getDcList(domainName);
		return dcList;
	}

	public RemoteNodeInfo scanRemoteNode(GatewayId gatewayId, String edgeUser, String edgeDomain, @NotPrintAttribute String edgePassword, String hostname, String username, @NotPrintAttribute String password)  throws EdgeServiceFault {
		IRemoteNativeFacadeFactory remoteNativeFacadeFactory = EdgeFactory.getBean(IRemoteNativeFacadeFactory.class);
		IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( gatewayId );
		RemoteNodeInfo nodeInfo = nativeFacade.scanRemoteNode(edgeUser, edgeDomain, edgePassword, hostname, username, password);
		return nodeInfo;
	}
	
//	public CAVirtualInfrastructureManager createVMWareManager(DiscoveryESXOption esxOption) throws EdgeServiceFault {
//		return VMwareManagerAdapter.getInstance().createVMWareManager(esxOption);
//	}
	
	public void validateEsxAccount(DiscoveryESXOption esxOption) throws EdgeServiceFault {
		IVmwareManagerServiceFactory vmwareServiceFactory = EdgeFactory.getBean( IVmwareManagerServiceFactory.class );
		IVmwareManagerService vmwareService = vmwareServiceFactory.createVmwareManagerService( esxOption.getGatewayId() );
		try{
			vmwareService.validateEsxAccount(esxOption);
		} finally {
			vmwareService.close();
		}
	}
	
	public void validateHyperVAccount(DiscoveryHyperVOption hyperVOption) throws EdgeServiceFault {
		HyperVManagerAdapter.getInstance().validateHyperVAccount(hyperVOption);
	}

	public List<DiscoveryVirtualMachineInfo> getVMVirtualMachineList(DiscoveryESXOption esxOption) throws EdgeServiceFault {
		IVmwareManagerServiceFactory vmwareServiceFactory = EdgeFactory.getBean( IVmwareManagerServiceFactory.class );
		IVmwareManagerService vmwareService = vmwareServiceFactory.createVmwareManagerService( esxOption.getGatewayId() );
		List<DiscoveryVirtualMachineInfo> vmEntryList = vmwareService.getVMVirtualMachineList(esxOption);
		vmwareService.close();
		return vmEntryList;
	}

}

