package com.ca.arcserve.edge.app.base.webservice;

import java.lang.reflect.Proxy;
import java.util.List;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.WebServiceContext;

import com.ca.arcflash.rps.webservice.data.ds.DataStoreSettingInfo;
import com.ca.arcflash.rps.webservice.data.ds.DataStoreStatusListElem;
import com.ca.arcflash.webservice.data.FlashJobMonitor;
import com.ca.arcserve.edge.app.base.webservice.contract.backup.BackupType;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeVersionInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ItemOperationResult;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SiteFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SiteInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SitePagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SitePagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobHistoryFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobHistoryPagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobHistoryPagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.license.bundled.LicenseInformation;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogPagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogPagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.AddNodeResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DeployTarget;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DeployTargetDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeNodeFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Node;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodePagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodePagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RegistrationNodeResult;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.udpservice.IUDPService;
import com.ca.arcserve.edge.app.base.webservice.udpservice.data.nodemanagement.UpdateNodeResult;
import com.ca.arcserve.edge.app.base.webservice.udpservice.data.nodemanagement.UpdateRPSResult;
import com.ca.arcserve.edge.app.base.webservice.udpservice.fault.UDPServiceFault;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.RpsNode;

@WebService( endpointInterface = "com.ca.arcserve.edge.app.base.webservice.udpservice.IUDPService" )
public class UDPServiceImpl implements IUDPService
{
	private IUDPService requestProcessorProxy;
	
	@Resource
	private WebServiceContext wsContext;
	
	public UDPServiceImpl()
	{
		UDPServiceProcessor requestProcessor = new UDPServiceProcessor();
		
		UDPServiceProcessorInvokeHandler invocationHandler =
			new UDPServiceProcessorInvokeHandler( this, requestProcessor );
		
		this.requestProcessorProxy = (IUDPService) Proxy.newProxyInstance(
			requestProcessor.getClass().getClassLoader(),
			requestProcessor.getClass().getInterfaces(),
			invocationHandler );
	}
	
	public WebServiceContext getWebServiceContext()
	{
		return this.wsContext;
	}

	/////////////////////////////////////////////////////////////////////////

	@Override
	public EdgeVersionInfo getVersionInformation() throws UDPServiceFault
	{
		return this.requestProcessorProxy.getVersionInformation();
	}

	/////////////////////////////////////////////////////////////////////////

	@Override
	public void login( String username, String password, String domain )
		throws UDPServiceFault
	{
		this.requestProcessorProxy.login( username, password, domain );
	}

	@Override
	public void logout() throws UDPServiceFault
	{
		this.requestProcessorProxy.logout();
	}
	
	/////////////////////////////////////////////////////////////////////////

//	@Override
//	public AddNodeResult addNodes2( List<NodeRegInfo> regInfoList )
//		throws UDPServiceFault
//	{
//		return this.requestProcessorProxy.addNodes2( regInfoList );
//	}

	@Override
	public AddNodeResult addNodes( List<NodeRegistrationInfo> nodeInfoList )
		throws UDPServiceFault
	{
		return this.requestProcessorProxy.addNodes( nodeInfoList );
	}

	@Override
	public UpdateNodeResult updateNode( int nodeId, NodeRegistrationInfo nodeInfo ) throws UDPServiceFault
	{
		return this.requestProcessorProxy.updateNode( nodeId, nodeInfo );
	}

	@Override
	public void deleteNodes( List<Integer> nodeIdList, boolean keepCurrentSettings )
		throws UDPServiceFault
	{
		this.requestProcessorProxy.deleteNodes( nodeIdList, keepCurrentSettings );
	}

	@Override
	public NodePagingResult getNodeList( EdgeNodeFilter nodeFilter,
		NodePagingConfig pagingConfig ) throws UDPServiceFault
	{
		return this.requestProcessorProxy.getNodeList( nodeFilter, pagingConfig );
	}

	@Override
	public Node getNodeInfo( int nodeId ) throws UDPServiceFault
	{
		return this.requestProcessorProxy.getNodeInfo( nodeId );
	}

	/////////////////////////////////////////////////////////////////////////

	@Override
	public RegistrationNodeResult addRps(
		NodeRegistrationInfo rpsInfo ) throws UDPServiceFault
	{
		return this.requestProcessorProxy.addRps( rpsInfo );
	}

	@Override
	public UpdateRPSResult updateRps( int rpsId, NodeRegistrationInfo rpsInfo )
		throws UDPServiceFault
	{
		return this.requestProcessorProxy.updateRps( rpsId, rpsInfo );
	}

	@Override
	public void deleteRps( int rpsId, boolean keepCurrentSettings )
		throws UDPServiceFault
	{
		this.requestProcessorProxy.deleteRps( rpsId, keepCurrentSettings );
	}

	@Override
	public RpsNode getRpsInfo( int rpsId )
		throws UDPServiceFault
	{
		return this.requestProcessorProxy.getRpsInfo( rpsId );
	}

	@Override
	public List<RpsNode> getRpsList() throws UDPServiceFault
	{
		return this.requestProcessorProxy.getRpsList();
	}

	/////////////////////////////////////////////////////////////////////////

	@Override
	public int createDataStore( int rpsId, DataStoreSettingInfo dataStoreSettings,
		boolean isOnExistingPath ) throws UDPServiceFault
	{
		return this.requestProcessorProxy.createDataStore( rpsId, dataStoreSettings, isOnExistingPath );
	}

	@Override
	public void updateDataStore( int rpsId, int dataStoreId,
		DataStoreSettingInfo dataStoreSettings ) throws UDPServiceFault
	{
		this.requestProcessorProxy.updateDataStore( rpsId, dataStoreId, dataStoreSettings );
	}

	@Override
	public void deleteDataStore( int rpsId, int dataStoreId )
		throws UDPServiceFault
	{
		this.requestProcessorProxy.deleteDataStore( rpsId, dataStoreId );
	}

	@Override
	public List<DataStoreStatusListElem> getDataStoreList( int rpsId,
		boolean includeStatus ) throws UDPServiceFault
	{
		return this.requestProcessorProxy.getDataStoreList( rpsId, includeStatus );
	}

	@Override
	public DataStoreStatusListElem getDataStoreInfo( int rpsId,
		int dataStoreId, boolean includeStatus ) throws UDPServiceFault
	{
		return this.requestProcessorProxy.getDataStoreInfo( rpsId, dataStoreId, includeStatus );
	}

	/////////////////////////////////////////////////////////////////////////

	@Override
	public int createPlan( UnifiedPolicy policy )
		throws UDPServiceFault
	{
		return this.requestProcessorProxy.createPlan( policy );
	}

	@Override
	public void updatePlan( int planId, UnifiedPolicy policy )
		throws UDPServiceFault
	{
		this.requestProcessorProxy.updatePlan( planId, policy );
	}

	@Override
	public List<ItemOperationResult> deletePlans( List<Integer> idList )
		throws UDPServiceFault
	{
		return this.requestProcessorProxy.deletePlans( idList );
	}

	@Override
	public List<PolicyInfo> getPlanStatusList() throws UDPServiceFault
	{
		return this.requestProcessorProxy.getPlanStatusList();
	}

	@Override
	public List<Integer> getPlanIdList() throws UDPServiceFault
	{
		return this.requestProcessorProxy.getPlanIdList();
	}

	@Override
	public UnifiedPolicy getPlan( int planId )
		throws UDPServiceFault
	{
		return this.requestProcessorProxy.getPlan( planId );
	}

	/////////////////////////////////////////////////////////////////////////

	@Override
	public void submitBackupJob( List<Integer> nodeIdList,
		BackupType backupType, String jobName ) throws UDPServiceFault
	{
		this.requestProcessorProxy.submitBackupJob( nodeIdList, backupType, jobName );
	}

	@Override
	public void cancelJob( int nodeId, int jobId ) throws UDPServiceFault
	{
		this.requestProcessorProxy.cancelJob( nodeId, jobId );
	}

	@Override
	public List<FlashJobMonitor> getJobStatusInfoList(
		int nodeId ) throws UDPServiceFault
	{
		return this.requestProcessorProxy.getJobStatusInfoList( nodeId );
	}

	@Override
	public JobHistoryPagingResult getJobHistoryList( int nodeId,
		JobHistoryFilter filter, JobHistoryPagingConfig pagingConfig )
		throws UDPServiceFault
	{
		return this.requestProcessorProxy.getJobHistoryList(
			nodeId, filter, pagingConfig );
	}
	
	/////////////////////////////////////////////////////////////////////////

 	@Override
	public LogPagingResult getActivityLogs( LogFilter filter,
		LogPagingConfig pagingConfig ) throws UDPServiceFault
	{
		return this.requestProcessorProxy.getActivityLogs( filter, pagingConfig );
	}

	/////////////////////////////////////////////////////////////////////////

	@Override
	public void startDeployingAgent( List<DeployTarget> targetList ) throws UDPServiceFault
	{
		this.requestProcessorProxy.startDeployingAgent( targetList );
	}

	@Override
	public List<DeployTargetDetail> getAgentDeploymentDetails(
		List<Integer> nodeIdList ) throws UDPServiceFault
	{
		return this.requestProcessorProxy.getAgentDeploymentDetails( nodeIdList );
	}

	/////////////////////////////////////////////////////////////////////////

	@Override
	public void addLicenseKey( String licenseKey ) throws UDPServiceFault
	{
		this.requestProcessorProxy.addLicenseKey( licenseKey );
	}

	@Override
	public List<LicenseInformation> getLicenses() throws UDPServiceFault
	{
		return this.requestProcessorProxy.getLicenses();
	}

	@Override
	public List<Node> getLinuxBackupServerList() throws UDPServiceFault
	{
		return this.requestProcessorProxy.getLinuxBackupServerList();
	}

	@Override
	public int addSite(SiteInfo siteInfo) throws UDPServiceFault {
		return this.requestProcessorProxy.addSite(siteInfo);
	}

	@Override
	public void updateSite(int siteId, SiteInfo siteInfo)
			throws UDPServiceFault {
		this.requestProcessorProxy.updateSite(siteId, siteInfo);
		
	}

	@Override
	public void deleteSite(int siteId) throws UDPServiceFault {
		this.deleteSite(siteId);
		
	}

	@Override
	public SiteInfo getSiteInfo(int siteId) throws UDPServiceFault {
		return this.requestProcessorProxy.getSiteInfo(siteId);
	}

	@Override
	public SitePagingResult getSites(SiteFilter siteFilter,
			SitePagingConfig pagingConfig) throws UDPServiceFault {
		return this.requestProcessorProxy.getSites(siteFilter, pagingConfig);
	}

}
