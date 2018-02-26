package com.ca.arcserve.edge.app.base.webservice;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.log4j.Logger;

import com.ca.arcflash.rps.webservice.data.ds.DataStoreSettingInfo;
import com.ca.arcflash.rps.webservice.data.ds.DataStoreStatusListElem;
import com.ca.arcflash.rps.webservice.data.policy.RPSPolicy;
import com.ca.arcflash.rps.webservice.data.policy.RPSReplicationSettings;
import com.ca.arcflash.webservice.data.FlashJobMonitor;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.webservice.contract.backup.BackupType;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeSortOrder;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeVersionInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ItemOperationResult;
import com.ca.arcserve.edge.app.base.webservice.contract.common.PagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.common.SortablePagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayId;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SiteFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SiteId;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SiteInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SitePagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SitePagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.SiteSortCol;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobHistoryFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobHistoryPagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.JobHistoryPagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.license.bundled.LicenseInformation;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogPagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogPagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.AddNodeResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DeployTarget;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DeployTargetDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeNodeFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Node;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeConvertUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeGroup;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodePagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodePagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeSortCol;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ProtectionType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RegistrationNodeResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RemoteNodeInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.entity.NodeEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.filter.NodeFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyTypes;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.TaskType;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified.UnifiedPolicy;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.ITaskValidator;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.ITaskValidator.ValidationError;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.TaskValidatorRegistry;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.validator.ValidationSession;
import com.ca.arcserve.edge.app.base.webservice.udpservice.IUDPService;
import com.ca.arcserve.edge.app.base.webservice.udpservice.data.nodemanagement.UpdateNodeResult;
import com.ca.arcserve.edge.app.base.webservice.udpservice.data.nodemanagement.UpdateRPSResult;
import com.ca.arcserve.edge.app.base.webservice.udpservice.data.nodemanagement.registration.HyperVVMRegInfo;
import com.ca.arcserve.edge.app.base.webservice.udpservice.data.nodemanagement.registration.LinuxBackupServerRegInfo;
import com.ca.arcserve.edge.app.base.webservice.udpservice.data.nodemanagement.registration.LinuxNodeRegInfo;
import com.ca.arcserve.edge.app.base.webservice.udpservice.data.nodemanagement.registration.NodeRegInfo;
import com.ca.arcserve.edge.app.base.webservice.udpservice.data.nodemanagement.registration.VMwareVMRegInfo;
import com.ca.arcserve.edge.app.base.webservice.udpservice.data.nodemanagement.registration.WindowsNodeRegInfo;
import com.ca.arcserve.edge.app.base.webservice.udpservice.fault.UDPServiceFault;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.RpsNode;

public class UDPServiceProcessor implements IUDPService
{
	private static Logger logger = Logger.getLogger( UDPServiceProcessor.class );
	
	private WebServiceContext wsContext;
	private EdgeWebServiceImpl edgeWebServiceImpl;
	
	public UDPServiceProcessor()
	{
		this.edgeWebServiceImpl = new EdgeWebServiceImpl();
	}
	
	public void setWebServiceContext( WebServiceContext context )
	{
		this.wsContext = context;
		this.edgeWebServiceImpl.setWebServiceContext( context );
	}
	
	private HttpSession getSession()
	{
		MessageContext msgContext = wsContext.getMessageContext();
		HttpSession session = ((javax.servlet.http.HttpServletRequest)
			msgContext.get( MessageContext.SERVLET_REQUEST )).getSession( false );
		return session;
	}
	
	/////////////////////////////////////////////////////////////////////////

	@Override
	public EdgeVersionInfo getVersionInformation() throws UDPServiceFault
	{
		try
		{
			return this.edgeWebServiceImpl.getVersionInformation();
		}
		catch (Exception e)
		{
			logger.error("[UDPServiceProcessor] getVersionInformation() failed.",e);
			throw UDPServiceFaultUtilities.handleException( e );
		}
	}

	/////////////////////////////////////////////////////////////////////////

	@Override
	public void login( String username, String password, String domain )
		throws UDPServiceFault
	{
		try
		{
			this.edgeWebServiceImpl.validateUser( username, password, domain );
		}
		catch (Exception e)
		{
			logger.error("[UDPServiceProcessor] login() failed.",e);
			throw UDPServiceFaultUtilities.handleException( e );
		}
	}

	@Override
	public void logout() throws UDPServiceFault
	{
		HttpSession session = this.getSession();
		if (session != null)
			session.invalidate();
	}

	/////////////////////////////////////////////////////////////////////////

//	@Override
	public AddNodeResult addNodes2( List<NodeRegInfo> regInfoList )
		throws UDPServiceFault
	{
		try
		{
			List<NodeRegistrationInfo> internalRegInfoList = new ArrayList<>();
			
			for (NodeRegInfo regInfo : regInfoList)
			{
				if (regInfo instanceof WindowsNodeRegInfo)
					internalRegInfoList.add( internalizeWindowsNodeRegInfo( (WindowsNodeRegInfo) regInfo ) );
				else if (regInfo instanceof LinuxNodeRegInfo)
					internalRegInfoList.add( internalizeLinuxNodeRegInfo( (LinuxNodeRegInfo) regInfo ) );
				else if (regInfo instanceof LinuxBackupServerRegInfo)
					internalRegInfoList.add( internalizeLinuxBackupServerRegInfo( (LinuxBackupServerRegInfo) regInfo ) );
				else if (regInfo instanceof VMwareVMRegInfo)
					internalRegInfoList.add( internalizeVMwareVMRegInfo( (VMwareVMRegInfo) regInfo ) );
				else if (regInfo instanceof HyperVVMRegInfo)
					internalRegInfoList.add( internalizeHyperVVMRegInfo( (HyperVVMRegInfo) regInfo ) );
			}
			
			return this.edgeWebServiceImpl.addNodes( internalRegInfoList );
		}
		catch (Exception e)
		{
			logger.error("[UDPServiceProcessor] addNodes2() failed.",e);
			throw UDPServiceFaultUtilities.handleException( e );
		}
	}
	
	private NodeRegistrationInfo internalizeWindowsNodeRegInfo( WindowsNodeRegInfo regInfo )
	{
		NodeRegistrationInfo internalRegInfo = new NodeRegistrationInfo();
		
		internalRegInfo.setNodeName( regInfo.getNodeName() );
		internalRegInfo.setUsername( regInfo.getCredential().getUsername() );
		internalRegInfo.setPassword( regInfo.getCredential().getPassword() );
		internalRegInfo.setPhysicsMachine( true ); // TODO: need to confirm
		
		if (regInfo.hasD2D())
		{
			internalRegInfo.setRegisterD2D( true );
			internalRegInfo.setD2dProtocol( regInfo.getD2dWebSvcConnectInfo().getProtocol() );
			internalRegInfo.setD2dPort( regInfo.getD2dWebSvcConnectInfo().getPort() );
		}

		if (regInfo.hasASBU())
		{
			internalRegInfo.setRegisterARCserveBackup( true );
			internalRegInfo.setAbAuthMode( regInfo.getAsbuCredential().getAsbuAuthType() );
			internalRegInfo.setCarootUsername( regInfo.getAsbuCredential().getPassword() );
			internalRegInfo.setCarootPassword( regInfo.getAsbuCredential().getPassword() );
			internalRegInfo.setArcservePort( regInfo.getAsbuPort() );
			//internalRegInfo.setArcserveProtocol( Protocol.HTTP ); // TODO: need to confirm
		}
		
		return internalRegInfo;
	}
	
	private NodeRegistrationInfo internalizeLinuxNodeRegInfo( LinuxNodeRegInfo regInfo )
	{
		NodeRegistrationInfo internalRegInfo = new NodeRegistrationInfo();
		
		internalRegInfo.setNodeName( regInfo.getNodeName() );
		
		if (regInfo.isUseSSHKeyAuth())
		{
			internalRegInfo.setUsername( "" );
			internalRegInfo.setPassword( "" );
		}
		else // don't use SSH key auth
		{
			internalRegInfo.setUsername( "Administrator" );
			internalRegInfo.setPassword( "cnbjrdqa1!" );
		}
		internalRegInfo.setNodeDescription( "description" );
		internalRegInfo.setLinux( true );
		internalRegInfo.setPhysicsMachine( false );
		
		return internalRegInfo;
	}

	private NodeRegistrationInfo internalizeLinuxBackupServerRegInfo( LinuxBackupServerRegInfo regInfo )
	{
		NodeRegistrationInfo internalRegInfo = new NodeRegistrationInfo();
		
//		internalRegInfo.setNodeName( "nodename" );
//		internalRegInfo.setUsername( "username" );
//		internalRegInfo.setPassword( "password" );
//		internalRegInfo.setD2DProtocol( Protocol.HTTP );
//		internalRegInfo.setD2DPort( 8014 );
//		internalRegInfo.setNodeDescription( "description" );
//		internalRegInfo.setPhysicsMachine( false );
//		internalRegInfo.setProtectionType( ProtectionType.LINUX_D_2_D_SERVER );
//		internalRegInfo.setExistLinuxBackupServer( true ); // looks useless
		
		return internalRegInfo;
	}

	private NodeRegistrationInfo internalizeVMwareVMRegInfo( VMwareVMRegInfo regInfo )
	{
		NodeRegistrationInfo internalRegInfo = new NodeRegistrationInfo();
		
		return internalRegInfo;
	}

	private NodeRegistrationInfo internalizeHyperVVMRegInfo( HyperVVMRegInfo regInfo )
	{
		NodeRegistrationInfo internalRegInfo = new NodeRegistrationInfo();
		
		return internalRegInfo;
	}

	@Override
	public AddNodeResult addNodes( List<NodeRegistrationInfo> nodeInfoList )
		throws UDPServiceFault
	{
		try
		{
			GatewayEntity localGateway = this.edgeWebServiceImpl.getLocalGateway();
			for (NodeRegistrationInfo node : nodeInfoList) {
				if(node.getGatewayId()==null 
						||(node.getGatewayId() != null 
								&& node.getGatewayId().getRecordId()<=0)){
					if(localGateway == null){
						logger.warn("[UDPServiceProcessor] addNodes() can't get the localgateway, so set the gatewayid to 1.");
						node.setGatewayId(new GatewayId(1));
					}else {
						logger.warn("[UDPServiceProcessor] addNodes() nodeinfo have no gateway info, so set the gateway to local gateway.");
						node.setGatewayId(localGateway.getId());
					}
				}
			}
			return this.edgeWebServiceImpl.addNodes( nodeInfoList );
		}
		catch (Exception e)
		{
			logger.error("[UDPServiceProcessor] addNodes() failed.",e);
			throw UDPServiceFaultUtilities.handleException( e );
		}
	}

	@Override
	public UpdateNodeResult updateNode( int nodeId, NodeRegistrationInfo nodeInfo ) throws UDPServiceFault
	{
		try
		{
			UpdateNodeResult result = new UpdateNodeResult();
			
			Node node = this.getNodeInfo( nodeId );
			if (node.isLinuxNode())
			{
				nodeInfo.setId( nodeId );
				
				RegistrationNodeResult regResult =
					this.edgeWebServiceImpl.registerLinuxNode( nodeInfo, false );
				
				String[] errorCodes = regResult.getErrorCodes();
				
				result.setUpdatingD2DSuccessful( errorCodes[0] == null );
				result.setUpdatingD2DErrorCode( (errorCodes[0] == null) ? "" : errorCodes[0] );
				result.setUpdatingASBUSuccessful( false );
				result.setUpdatingASBUErrorCode( "" );
			}
			else if ((node.getProtectionTypeBitmap() & ProtectionType.LINUX_D2D_SERVER.getValue()) ==
				ProtectionType.LINUX_D2D_SERVER.getValue())
			{
				nodeInfo.setId( nodeId );
				
				RegistrationNodeResult regResult =
					this.edgeWebServiceImpl.registerLinuxD2DServer( nodeInfo, false, false );
				
				String[] errorCodes = regResult.getErrorCodes();
				
				result.setUpdatingD2DSuccessful( errorCodes[0] == null );
				result.setUpdatingD2DErrorCode( (errorCodes[0] == null) ? "" : errorCodes[0] );
				result.setUpdatingASBUSuccessful( false );
				result.setUpdatingASBUErrorCode( "" );
			}
			else // non-linux nodes
			{
				nodeInfo.setId( nodeId );
				String[] returns = this.edgeWebServiceImpl.updateNode( true, nodeInfo );
				
				result.setUpdatingD2DSuccessful( returns[0] == null );
				result.setUpdatingD2DErrorCode( (returns[0] == null) ? "" : returns[0] );
				result.setUpdatingASBUSuccessful( returns[1] == null );
				result.setUpdatingASBUErrorCode( (returns[1] == null) ? "" : returns[1] );
			}
			
			return result;
		}
		catch (Exception e)
		{
			logger.error("[UDPServiceProcessor] updateNode() failed.",e);
			throw UDPServiceFaultUtilities.handleException( e );
		}
	}

	@Override
	public void deleteNodes( List<Integer> nodeIdList, boolean keepCurrentSettings )
		throws UDPServiceFault
	{
		try
		{
			Integer[] integerArray = nodeIdList.toArray( new Integer[0] );
			int[] intArray = new int[integerArray.length];
			for (int i = 0; i < integerArray.length; i ++)
				intArray[i] = integerArray[i];
			
			this.edgeWebServiceImpl.deleteNodes( intArray, keepCurrentSettings );
		}
		catch (Exception e)
		{
			logger.error("[UDPServiceProcessor] deleteNodes() failed.",e);
			throw UDPServiceFaultUtilities.handleException( e );
		}
	}

	@Override
	public NodePagingResult getNodeList( EdgeNodeFilter nodeFilter,
		NodePagingConfig pagingConfig ) throws UDPServiceFault
	{
		try
		{
			NodeGroup group = new NodeGroup();
			group.setId(NodeGroup.ALLGROUP);
			group.setType(NodeGroup.Default);
			
			List<NodeFilter> filters = NodeConvertUtil.getNodeFiltersByEdgeFilter(nodeFilter);
			
			SortablePagingConfig<NodeSortCol> sortablePagingConfig = NodeConvertUtil.getSortablePagingConfigByNodePagingConfig(pagingConfig);
			
			PagingResult<NodeEntity> nodeEntities = this.edgeWebServiceImpl.getPagingNodes(group, filters, sortablePagingConfig);
			
			List<Node> nodes = NodeConvertUtil.getNodeListByNodeEntityList(nodeEntities.getData());
			
			NodePagingResult nodePagingResult = new NodePagingResult();
			nodePagingResult.setData(nodes);
			nodePagingResult.setStartIndex(0);
			nodePagingResult.setTotalCount(nodes.size());
			
			return nodePagingResult;
		}
		catch (Exception e)
		{
			logger.error("[UDPServiceProcessor] getNodeList() failed.",e);
			throw UDPServiceFaultUtilities.handleException( e );
		}
	}

	@Override
	public Node getNodeInfo( int nodeId ) throws UDPServiceFault
	{
		try
		{
			return this.edgeWebServiceImpl.getNodeDetailInformation( nodeId );
		}
		catch (Exception e)
		{
			logger.error("[UDPServiceProcessor] getNodeInfo() failed.",e);
			throw UDPServiceFaultUtilities.handleException( e );
		}
	}

	/////////////////////////////////////////////////////////////////////////

	@Override
	public RegistrationNodeResult addRps(
		NodeRegistrationInfo rpsInfo ) throws UDPServiceFault
	{
		try
		{
			if(rpsInfo.getGatewayId()==null 
					|| (rpsInfo.getGatewayId()!=null 
							&& rpsInfo.getGatewayId().getRecordId()<=0)){
				GatewayEntity gatewayEntity = this.edgeWebServiceImpl.getLocalGateway();
				if(gatewayEntity != null){
					rpsInfo.setGatewayId(gatewayEntity.getId());
					logger.warn("[UDPServiceProcessor] addRps() rpsInfo have no gateway info or gateway info is not valid, "
							+ "so set the gatewayid to local gateway.");
				}else {
					logger.warn("[UDPServiceProcessor] addRps() have not get the gateway, so set the gatewayid to 1");
					rpsInfo.setGatewayId(new GatewayId(1));
				}
			}
			
			if (rpsInfo.getNodeInfo() == null)
				rpsInfo.setNodeInfo( new RemoteNodeInfo() );
			rpsInfo.getNodeInfo().setRPSInstalled( true );
			
			return this.edgeWebServiceImpl.registerRpsNode( true, rpsInfo );
		}
		catch (Exception e)
		{
			logger.error("[UDPServiceProcessor] addRps() failed.",e);
			throw UDPServiceFaultUtilities.handleException( e );
		}
	}

	@Override
	public UpdateRPSResult updateRps( int rpsId, NodeRegistrationInfo rpsInfo )
		throws UDPServiceFault
	{
		try
		{
			String[] returns = this.edgeWebServiceImpl.updateRpsNode( false, rpsInfo, true);
			
			UpdateRPSResult result = new UpdateRPSResult();
			result.setSuccessful( returns[0] == null );
			result.setErrorCode( (returns[0] == null ) ? "" : returns[0] );
			
			return result;
		}
		catch (Exception e)
		{
			logger.error("[UDPServiceProcessor] updateRps() failed.",e);
			throw UDPServiceFaultUtilities.handleException( e );
		}
	}

	@Override
	public void deleteRps( int rpsId, boolean keepCurrentSettings )
		throws UDPServiceFault
	{
		try
		{
			this.edgeWebServiceImpl.deleteRpsNode( rpsId, keepCurrentSettings );
		}
		catch (Exception e)
		{
			logger.error("[UDPServiceProcessor] deleteRps() failed.",e);
			throw UDPServiceFaultUtilities.handleException( e );
		}
	}

	@Override
	public RpsNode getRpsInfo( int rpsId )
		throws UDPServiceFault
	{
		try
		{
			return this.edgeWebServiceImpl.getRpsNodeDetailInformation( rpsId );
		}
		catch (Exception e)
		{
			logger.error("[UDPServiceProcessor] getRpsInfo() failed.",e);
			throw UDPServiceFaultUtilities.handleException( e );
		}
	}

	@Override
	public List<RpsNode> getRpsList() throws UDPServiceFault
	{
		try
		{
			return this.edgeWebServiceImpl.getRpsNodesByGroup(0, 0 );
		}
		catch (Exception e)
		{
			logger.error("[UDPServiceProcessor] getRpsList() failed.",e);
			throw UDPServiceFaultUtilities.handleException( e );
		}
	}

	/////////////////////////////////////////////////////////////////////////

	@Override
	public int createDataStore( int rpsId, DataStoreSettingInfo dataStoreSettings,
		boolean isOnExistingPath ) throws UDPServiceFault
	{
		try
		{
			dataStoreSettings.setNode_id( rpsId );
			dataStoreSettings.setDatastore_name( null ); // indicate creation
			String dataStoreUuid = this.edgeWebServiceImpl.saveDataStoreSetting( dataStoreSettings );
			dataStoreSettings = this.edgeWebServiceImpl.getDataStoreByGuid( rpsId, dataStoreUuid );
			return dataStoreSettings.getDatastore_id();
		}
		catch (Exception e)
		{
			logger.error("[UDPServiceProcessor] createDataStore() failed.",e);
			throw UDPServiceFaultUtilities.handleException( e );
		}
	}

	@Override
	public void updateDataStore( int rpsId, int dataStoreId,
		DataStoreSettingInfo dataStoreSettings ) throws UDPServiceFault
	{
		try
		{
			String dataStoreUuid = getDataStoreUuid( dataStoreId );
			if (dataStoreUuid == null)
				return;  // TODO: throw DataStoreNotFoundException
			
			dataStoreSettings.setNode_id( rpsId );
			dataStoreSettings.setDatastore_id( dataStoreId );
			dataStoreSettings.setDatastore_name( dataStoreUuid );
			this.edgeWebServiceImpl.saveDataStoreSetting( dataStoreSettings );
		}
		catch (Exception e)
		{
			logger.error("[UDPServiceProcessor] updateDataStore() failed.",e);
			throw UDPServiceFaultUtilities.handleException( e );
		}
	}

	@Override
	public void deleteDataStore( int rpsId, int dataStoreId )
		throws UDPServiceFault
	{
		try
		{
			this.edgeWebServiceImpl.deleteDataStoreById( rpsId,
				this.getDataStoreUuid( dataStoreId ) );
		}
		catch (Exception e)
		{
			logger.error("[UDPServiceProcessor] deleteDataStore() failed.",e);
			throw UDPServiceFaultUtilities.handleException( e );
		}
	}

	@Override
	public List<DataStoreStatusListElem> getDataStoreList( int rpsId,
		boolean includeStatus ) throws UDPServiceFault
	{
		try
		{
			if (!includeStatus)
			{
				List<DataStoreSettingInfo> infoList =
					this.edgeWebServiceImpl.getDataStoreListByNode( rpsId );
				List<DataStoreStatusListElem> returnList = new ArrayList<DataStoreStatusListElem>();
				for (DataStoreSettingInfo info : infoList)
				{
					DataStoreStatusListElem returnInfo = new DataStoreStatusListElem();
					returnInfo.setDataStoreSetting( info );
					returnInfo.setDataStoreStatus( null );
					returnList.add( returnInfo );
				}
				
				return returnList;
			}
			else // include status
			{
				//invoke RPS API to get data store list
				List<DataStoreStatusListElem> returnList = this.edgeWebServiceImpl.getDataStoreSummariesByNode( rpsId );
				//get data store list from DB
				//So as to get data store id from DB
				List<DataStoreSettingInfo> dbInfoList = this.edgeWebServiceImpl.getDataStoreListByNode( rpsId );
				for(DataStoreStatusListElem item : returnList){
					if(item.getDataStoreSetting() == null || item.getDataStoreSetting().getDatastore_name()==null)
						continue;
					for (DataStoreSettingInfo dbItem : dbInfoList) {
						if(dbItem.getDatastore_name()!=null){
							if(item.getDataStoreSetting().getDatastore_name().
									equalsIgnoreCase(dbItem.getDatastore_name())){
								item.getDataStoreSetting().setDatastore_id(dbItem.getDatastore_id());
								break;
							}
						}
					}
				}	
				return returnList;
			}
		}
		catch (Exception e)
		{
			logger.error("[UDPServiceProcessor] getDataStoreList() failed.",e);
			throw UDPServiceFaultUtilities.handleException( e );
		}
	}

	@Override
	public DataStoreStatusListElem getDataStoreInfo( int rpsId,
		int dataStoreId, boolean includeStatus ) throws UDPServiceFault
	{
		try
		{
			if (!includeStatus)
			{
				DataStoreStatusListElem info = new DataStoreStatusListElem();
				info.setDataStoreSetting( this.edgeWebServiceImpl.getDataStoreById( dataStoreId ) );
				info.setDataStoreStatus( null );
				return info;
			}
			else // include status
			{
				String dataStoreUuid = getDataStoreUuid( dataStoreId );
				if (dataStoreUuid == null)
					return null;  // TODO: throw DataStoreNotFoundException
				
				return this.edgeWebServiceImpl.getDataStoreSummary( rpsId, dataStoreUuid );
			}
		}
		catch (Exception e)
		{
			logger.error("[UDPServiceProcessor] getDataStoreInfo() failed.",e);
			throw UDPServiceFaultUtilities.handleException( e );
		}
	}
	
	private String getDataStoreUuid( int dataStoreId ) throws UDPServiceFault
	{
		try
		{
			DataStoreSettingInfo settingInfo = this.edgeWebServiceImpl.getDataStoreById( dataStoreId );
			if (settingInfo == null)
				return null;
			
			return settingInfo.getDatastore_name();
		}
		catch (Exception e)
		{
			logger.error("[UDPServiceProcessor] getDataStoreUuid() failed.",e);
			throw UDPServiceFaultUtilities.handleException( e );
		}
	}

	/////////////////////////////////////////////////////////////////////////

	@Override
	public int createPlan( UnifiedPolicy policy )
		throws UDPServiceFault
	{
		try
		{
			this.generateTaskList( policy );
			this.validatePolicy( policy );
			policy.generateOrderList();
			GatewayEntity localGateway = this.edgeWebServiceImpl.getLocalGateway();			
			if((policy.getGatewayId()==null)||(!policy.getGatewayId().isValid())){
				policy.setGatewayId(localGateway.getId());
			}			
			return this.edgeWebServiceImpl.createUnifiedPolicy( policy );
		}
		catch (Exception e)
		{
			logger.error("[UDPServiceProcessor] createPlan() failed.",e);
			throw UDPServiceFaultUtilities.handleException( e );
		}
	}

	@Override
	public void updatePlan( int planId, UnifiedPolicy policy )
		throws UDPServiceFault
	{
		try
		{
			this.generateTaskList( policy );
			this.validatePolicy( policy );
			policy.generateOrderList();
			
			policy.setId( planId );
			this.edgeWebServiceImpl.updateUnifiedPolicy( policy );
		}
		catch (Exception e)
		{
			logger.error("[UDPServiceProcessor] updatePlan() failed.",e);
			throw UDPServiceFaultUtilities.handleException( e );
		}
	}

	@Override
	public List<ItemOperationResult> deletePlans( List<Integer> idList )
		throws UDPServiceFault
	{
		try
		{
			return this.edgeWebServiceImpl.deleteUnifiedPolicies( idList );
		}
		catch (Exception e)
		{
			logger.error("[UDPServiceProcessor] deletePlans() failed.",e);
			throw UDPServiceFaultUtilities.handleException( e );
		}
	}

	@Override
	public List<PolicyInfo> getPlanStatusList() throws UDPServiceFault
	{
		try
		{
			return this.edgeWebServiceImpl.getPlanList();
		}
		catch (Exception e)
		{
			logger.error("[UDPServiceProcessor] getPlanStatusList() failed.",e);
			throw UDPServiceFaultUtilities.handleException( e );
		}
	}

	@Override
	public List<Integer> getPlanIdList() throws UDPServiceFault
	{
		try
		{
			return this.edgeWebServiceImpl.getPlanIds();
		}
		catch (Exception e)
		{
			logger.error("[UDPServiceProcessor] getPlanIdList() failed.",e);
			throw UDPServiceFaultUtilities.handleException( e );
		}
	}

	@Override
	public UnifiedPolicy getPlan( int planId )
		throws UDPServiceFault
	{
		try
		{
			return this.edgeWebServiceImpl.loadUnifiedPolicyById( planId );
		}
		catch (Exception e)
		{
			logger.error("[UDPServiceProcessor] getPlan() failed.",e);
			throw UDPServiceFaultUtilities.handleException( e );
		}
	}
	
	private void generateTaskList( UnifiedPolicy policy )
	{
		logger.info( "Begin to generate task list." );
		
		assert policy != null : "policy is null";
		
		if (policy.getTaskList() == null)
			policy.setTaskList( new ArrayList<TaskType>() );
		policy.getTaskList().clear();
		
		if (policy.getLinuxBackupsetting() != null)
			policy.getTaskList().add( TaskType.LinuxBackUP );
		else if (policy.getVSphereBackupConfiguration() != null)
			policy.getTaskList().add( TaskType.VSphereBackUP );
		else if (policy.getBackupConfiguration() != null)
			policy.getTaskList().add( TaskType.BackUP );
		
		if (policy.getMspServerReplicationSettings() != null)
			policy.getTaskList().add( TaskType.MspServerReplication );
		
		for (int i = 0; i < policy.getRpsPolices().size(); i ++)
		{
			RPSPolicy rpsPolicy = policy.getRpsPolices().get( i ).getRpsPolicy();
			RPSReplicationSettings rep = rpsPolicy.getRpsSettings().getRpsReplicationSettings();
			if (i > 0) // the 1st RPS policy is for backup
				policy.getTaskList().add( TaskType.Replication );
			if (rep.getMspReplicationSettings() != null)
				policy.getTaskList().add( TaskType.MspClientReplication );
		}
		
		if (policy.getConversionConfiguration() != null)
		{
			TaskType taskType = TaskType.Conversion;
			int policyType = policy.getConversionConfiguration().getTaskType();
			switch (policyType)
			{
			case PolicyTypes.VCM:
				taskType = TaskType.Conversion;
				break;
				
			case PolicyTypes.RemoteVCM:
				taskType = TaskType.RemoteConversion;
				break;
				
			case PolicyTypes.RemoteVCMForRHA:
				taskType = TaskType.RemoteConversionForRHA;
				break;
			}
			policy.getTaskList().add( taskType );
		}
		
		if (policy.getFileCopySettingsWrapper() != null && !policy.getFileCopySettingsWrapper().isEmpty())
			policy.getTaskList().add( TaskType.FileCopy );
		
		if (policy.getFileArchiveConfiguration() != null)
			policy.getTaskList().add( TaskType.FILE_ARCHIVE );
		
		if (policy.getDeployD2Dsetting() != null)
			policy.getTaskList().add( TaskType.AgentInstallation );
		
		if (policy.getExportConfiguration() != null)
			policy.getTaskList().add( TaskType.CopyRecoveryPoints );
	}

	private void validatePolicy( UnifiedPolicy policy ) throws UDPServiceFault
	{
		this.validateTaskList( policy.getTaskList() );
		this.validateTasks( policy );
	}
	
	private void validateTaskList( List<TaskType> taskList ) throws UDPServiceFault
	{
		logger.info( "Begin to validate task list." );
		
		if ((taskList == null) || (taskList.size() == 0))
			throw UDPServiceFaultUtilities.createUDPServiceFault(
				EdgeServiceErrorCode.PolicyValidation_NoTasks, "No tasks in policy." );
		
		TaskType firstTask = taskList.get( 0 );
		if ((firstTask != TaskType.BackUP) &&
			(firstTask != TaskType.LinuxBackUP) &&
			(firstTask != TaskType.VSphereBackUP) &&
			(firstTask != TaskType.MspServerReplication) &&
			(firstTask != TaskType.RemoteConversionForRHA))
			throw UDPServiceFaultUtilities.createUDPServiceFault(
				EdgeServiceErrorCode.PolicyValidation_WrongFirstTask, new Object[] { firstTask },
				"Wrong first task in policy. The first task: " + firstTask );
		
		if (firstTask == TaskType.MspServerReplication)
		{
			for (int i = 1; i < taskList.size(); i ++)
			{
				TaskType task = taskList.get( i );
				if ((task != TaskType.Replication) &&
					(task != TaskType.RemoteConversion))
				{
					throw UDPServiceFaultUtilities.createUDPServiceFault(
						EdgeServiceErrorCode.PolicyValidation_WrongSubsequentTaskForMSPServerRep, new Object[] { task },
						"Wrong subsequent task for MSP server replication task. Task: " + task );
				}
			}
		}
		else if (firstTask == TaskType.RemoteConversionForRHA)
		{
			if (taskList.size() > 1)
			{
				throw UDPServiceFaultUtilities.createUDPServiceFault(
					EdgeServiceErrorCode.PolicyValidation_WrongSubsequentTaskForRemoteConvRHA,
					"Wrong subsequent task for remote conversion for RHA task." );
			}
		}
	}
	
	private void validateTasks( UnifiedPolicy policy ) throws UDPServiceFault
	{
		logger.info( "Begin to validate each task." );
		
		ValidationSession validationSession = new ValidationSession();
		
		int taskIndex = 0;
		for (TaskType task : policy.getTaskList())
		{
			ITaskValidator taskValidator = TaskValidatorRegistry.getValidator( task );
			ValidationError error = taskValidator.validate( policy, task, taskIndex, validationSession );
			if (error != null)
			{
				logger.info( error.getErrorMessageForLog() );
				throw UDPServiceFaultUtilities.createUDPServiceFault(
					error.getErrorCode(), error.getErrorParams(),
					error.getErrorMessageForLog() );
			}
			taskIndex ++;
		}
	}

	/////////////////////////////////////////////////////////////////////////

	@Override
	public void submitBackupJob( List<Integer> nodeIdList,
		BackupType backupType, String jobName ) throws UDPServiceFault
	{
		try
		{
			Integer[] integerArray = nodeIdList.toArray( new Integer[0] );
			int[] intArray = new int[integerArray.length];
			for (int i = 0; i < integerArray.length; i ++)
				intArray[i] = integerArray[i];
			
			this.edgeWebServiceImpl.backupNodesForEDGE(
				intArray, backupType.getValue(), jobName );
		}
		catch (Exception e)
		{
			logger.error("[UDPServiceProcessor] submitBackupJob() failed.",e);
			throw UDPServiceFaultUtilities.handleException( e );
		}
	}

	@Override
	public void cancelJob( int nodeId, int jobId ) throws UDPServiceFault
	{
		try
		{
			this.edgeWebServiceImpl.cancelJob( nodeId, "", jobId );
		}
		catch (Exception e)
		{
			logger.error("[UDPServiceProcessor] cancelJob() failed.",e);
			throw UDPServiceFaultUtilities.handleException( e );
		}
	}

	@Override
	public List<FlashJobMonitor> getJobStatusInfoList(
		int nodeId ) throws UDPServiceFault
	{
		try
		{
			String nodeTypeName = "";
			
			Node node = this.getNodeInfo( nodeId );
			if (node.isLinuxNode())
				nodeTypeName = "LinuxD2D";
			else if ((node.getProtectionTypeBitmap() & ProtectionType.RPS.getValue()) != 0)
				nodeTypeName = "RPS";
			else // d2d
				nodeTypeName = "D2D";
				
			return this.edgeWebServiceImpl.getJobStatusInfoList( nodeTypeName + "-" + nodeId + "-" );
		}
		catch (Exception e)
		{
			logger.error("[UDPServiceProcessor] getJobStatusInfoList() failed.",e);
			throw UDPServiceFaultUtilities.handleException( e );
		}
	}

	@Override
	public JobHistoryPagingResult getJobHistoryList( int nodeId,
		JobHistoryFilter filter, JobHistoryPagingConfig pagingConfig )
		throws UDPServiceFault
	{
		try
		{
			if (filter == null)
				filter = new JobHistoryFilter();
			
			if (pagingConfig == null)
			{
				pagingConfig = new JobHistoryPagingConfig();
				pagingConfig.setStartIndex( 0 );
				pagingConfig.setCount( Integer.MAX_VALUE );
			}
			
			Node node = this.getNodeInfo( nodeId );
			if (node.isLinuxNode())
				return this.edgeWebServiceImpl.getLinuxD2DJobHistoryList( nodeId, pagingConfig, filter );
			else if ((node.getProtectionTypeBitmap() & ProtectionType.RPS.getValue()) != 0)
				return this.edgeWebServiceImpl.getRpsJobHistoryList( nodeId, pagingConfig, filter );
			else // d2d
				return this.edgeWebServiceImpl.getD2DJobHistoryList( nodeId, pagingConfig, filter );
		}
		catch (Exception e)
		{
			logger.error("[UDPServiceProcessor] getJobHistoryList() failed.",e);
			throw UDPServiceFaultUtilities.handleException( e );
		}
	}

	/////////////////////////////////////////////////////////////////////////

	@Override
	public LogPagingResult getActivityLogs( LogFilter filter,
		LogPagingConfig pagingConfig ) throws UDPServiceFault
	{
		try
		{
			if (filter == null)
			{
				filter = new LogFilter();
				filter.setSeverity( Severity.All );
			}
			
			if (pagingConfig == null)
			{
				pagingConfig = new LogPagingConfig();
				pagingConfig.setStartIndex( 0 );
				pagingConfig.setCount( Integer.MAX_VALUE );
			}
				
			return this.edgeWebServiceImpl.getUnifiedLogs( pagingConfig, filter );
		}
		catch (Exception e)
		{
			logger.error("[UDPServiceProcessor] getActivityLogs() failed.",e);
			throw UDPServiceFaultUtilities.handleException( e );
		}
	}

	/////////////////////////////////////////////////////////////////////////

	@Override
	public void startDeployingAgent( List<DeployTarget> targetList ) throws UDPServiceFault
	{
		try
		{
			List<DeployTargetDetail> detailList = new ArrayList<DeployTargetDetail>();
			for (DeployTarget target : targetList)
			{
				DeployTargetDetail detail = new DeployTargetDetail();
				detail.setNodeID( target.getNodeID() );
				detail.setUsername( target.getUsername() );
				detail.setPassword( target.getPassword() );
				detail.setPort( target.getPort() );
				detail.setInstallDirectory( target.getInstallDirectory() );
				detail.setRebootType( target.getRebootType() );
				detail.setProtocol( target.getProtocol() );
				detailList.add( detail );
			}
			this.edgeWebServiceImpl.submitRemoteDeploy( detailList );
		}
		catch (Exception e)
		{
			logger.error("[UDPServiceProcessor] startDeployingAgent() failed.",e);
			throw UDPServiceFaultUtilities.handleException( e );
		}
	}

	@Override
	public List<DeployTargetDetail> getAgentDeploymentDetails(
		List<Integer> nodeIdList ) throws UDPServiceFault
	{
		try
		{
			return this.edgeWebServiceImpl.getDeployTargets( nodeIdList );
		}
		catch (Exception e)
		{
			logger.error("[UDPServiceProcessor] getAgentDeploymentDetails() failed.",e);
			throw UDPServiceFaultUtilities.handleException( e );
		}
	}

	/////////////////////////////////////////////////////////////////////////

	@Override
	public void addLicenseKey( String licenseKey ) throws UDPServiceFault
	{
		try
		{
			this.edgeWebServiceImpl.addLicenseKeyNew( licenseKey );
		}
		catch (Exception e)
		{
			logger.error("[UDPServiceProcessor] addLicenseKey() failed.",e);
			throw UDPServiceFaultUtilities.handleException( e );
		}
	}

	@Override
	public List<LicenseInformation> getLicenses() throws UDPServiceFault
	{
		try
		{
			return this.edgeWebServiceImpl.getLicenses();
		}
		catch (Exception e)
		{
			logger.error("[UDPServiceProcessor] getLicenses() failed.",e);
			throw UDPServiceFaultUtilities.handleException( e );
		}
	}

	@Override
	public List<Node> getLinuxBackupServerList() throws UDPServiceFault
	{
		try
		{
			GatewayEntity localGateway = this.edgeWebServiceImpl.getLocalGateway();
			return this.edgeWebServiceImpl.getNodesByGroup( localGateway.getId().getRecordId(), 0, NodeGroup.LinuxD2D);
		}
		catch (Exception e)
		{
			logger.error("[UDPServiceProcessor] getLinuxBackupServerList() failed.",e);
			throw UDPServiceFaultUtilities.handleException( e );
		}
	}

	@Override
	public int addSite(SiteInfo siteInfo) throws UDPServiceFault {
		try {
			
			SiteId siteId = this.edgeWebServiceImpl.createSite(siteInfo);
			return siteId.getRecordId();
			
		} catch (Exception e) {
			logger.error("[UDPServiceProcessor] addSite() failed.",e);
			throw UDPServiceFaultUtilities.handleException(e);
		}
	}

	@Override
	public void updateSite(int siteId, SiteInfo siteInfo)
			throws UDPServiceFault {
		try {
			
			SiteId siteId2 = new SiteId(siteId);
			this.edgeWebServiceImpl.updateSite(siteId2, siteInfo);
			
		} catch (Exception e) {
			logger.error("[UDPServiceProcessor] updateSite() failed.",e);
			throw UDPServiceFaultUtilities.handleException(e);
		}	
	}

	@Override
	public void deleteSite(int siteId) throws UDPServiceFault {
		try {
			
			SiteId siteId2 = new SiteId(siteId);
			this.edgeWebServiceImpl.deleteSite(siteId2);
			
		} catch (Exception e) {
			logger.error("[UDPServiceProcessor] deleteSite() failed.",e);
			throw UDPServiceFaultUtilities.handleException(e);
		}
	}

	@Override
	public SiteInfo getSiteInfo(int siteId) throws UDPServiceFault {
		try {
			
			SiteId siteId2 = new SiteId(siteId);
			return this.edgeWebServiceImpl.getSite(siteId2);
			
		} catch (Exception e) {
			logger.error("[UDPServiceProcessor] getSiteInfo() failed.",e);
			throw UDPServiceFaultUtilities.handleException(e);
		}
	}

	@Override
	public SitePagingResult getSites(SiteFilter siteFilter,
			SitePagingConfig pagingConfig) throws UDPServiceFault {
		try {
			
			if(pagingConfig == null){
				pagingConfig = new SitePagingConfig();
				pagingConfig.setPageSize(Integer.MAX_VALUE);
				pagingConfig.setSortColumn(SiteSortCol.siteName);
				pagingConfig.setSortOrder(EdgeSortOrder.ASC);
				pagingConfig.setStartIndex(0);
			}
			
			return this.edgeWebServiceImpl.pageQuerySites(siteFilter, pagingConfig);
			
		} catch (Exception e) {
			logger.error("[UDPServiceProcessor] getSites() failed.",e);
			throw UDPServiceFaultUtilities.handleException(e);
		}
	}

}
