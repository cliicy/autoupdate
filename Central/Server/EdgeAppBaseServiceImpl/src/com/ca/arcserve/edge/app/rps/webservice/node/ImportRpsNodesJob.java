package com.ca.arcserve.edge.app.rps.webservice.node;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.impl.JobDetailImpl;

import com.ca.arcflash.rps.webservice.data.ds.DataStoreSettingInfo;
import com.ca.arcserve.edge.app.base.scheduler.impl.SchedulerUtilsImpl;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFaultBean;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.IProductDeployService;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.Utils;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DeployTargetDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ImportNodeType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RegistrationNodeResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.RemoteNodeInfo;
import com.ca.arcserve.edge.app.base.webservice.monitor.ImportNodesJobMonitor;
import com.ca.arcserve.edge.app.base.webservice.monitor.JobMonitor;
import com.ca.arcserve.edge.app.base.webservice.monitor.JobMonitorManager;
import com.ca.arcserve.edge.app.base.webservice.node.ImportNodesJob;
import com.ca.arcserve.edge.app.base.webservice.node.NodeServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.productdeploy.ProductDeployServiceImpl;
import com.ca.arcserve.edge.app.rps.webservice.contract.rps.node.NodeRegistrationInfoForRPS;
import com.ca.arcserve.edge.app.rps.webservice.datastore.DataStoreManager;
import com.ca.arcserve.edge.app.rps.webservice.i18n.RpsMessageReader;

public class ImportRpsNodesJob extends ImportNodesJob {

	private static final String TAG_NODES = "nodes";
	private static final String TAG_TYPE = "type";
	private static final String TAG_SERVICE = "service";
	private static final String TAG_RPS_SERVICE = "rpsservice";
	private static final String TAG_EDGE_USERNAME = "edgeUsername";
	private static final String TAG_EDGE_PASSWORD = "edgePassword";
	private static final String TAG_EDGE_DOMAIN = "edgeDomain";
	private static final Logger logger = Logger.getLogger(ImportRpsNodesJob.class);	
	
	protected RPSNodeServiceImpl rpsNodeService;	
	protected NodeServiceImpl nodeService;
	protected IProductDeployService deployService = ProductDeployServiceImpl.getInstance();
	
	public JobDetail createJobDetail(NodeRegistrationInfo[] nodes, ImportNodeType type, RPSNodeServiceImpl nodeService) {
		
		JobDetail jobDetail = new JobDetailImpl(getClass().getSimpleName()+getId(), null, getClass());
		
		super.createJobDetail(jobDetail);//store job id into jobdetail

		jobDetail.getJobDataMap().put(TAG_NODES, nodes);
		jobDetail.getJobDataMap().put(TAG_TYPE, type);
		jobDetail.getJobDataMap().put(TAG_SERVICE, new NodeServiceImpl());
		jobDetail.getJobDataMap().put(TAG_RPS_SERVICE, nodeService);

		String edgeUser =(String) nodeService.serviceImpl.getSession().getAttribute(CommonUtil.STRING_SESSION_USERNAME);
		String edgePassword = (String)nodeService.serviceImpl.getSession().getAttribute(CommonUtil.STRING_SESSION_PASSWORD);
		String edgeDomain =(String) nodeService.serviceImpl.getSession().getAttribute(CommonUtil.STRING_SESSION_DOMAIN);

		jobDetail.getJobDataMap().put(TAG_EDGE_USERNAME, edgeUser);
		jobDetail.getJobDataMap().put(TAG_EDGE_PASSWORD, edgePassword);
		jobDetail.getJobDataMap().put(TAG_EDGE_DOMAIN, edgeDomain);

		return jobDetail;
	}
	
	public void importRpsNode(NodeRegistrationInfo[] nodes, RPSNodeServiceImpl rpsNodeService){
		if(rpsNodeService == null || nodes == null)
			return;
		
		this.rpsNodeService = rpsNodeService;
		nodeService = new NodeServiceImpl();
		rpsNodeService.addActivityLogForImportNodes(Severity.Information, type, 
				EdgeCMWebServiceMessages.getMessage("importNodes_Start", nodes.length));
		
		for (NodeRegistrationInfo node : nodes) {
			importSingle(node);
		}
		
		rpsNodeService.addActivityLogForImportNodes(Severity.Information, type, 
				EdgeCMWebServiceMessages.getMessage("importNodes_End"));
	}
	
	@Override
	protected int importSingle(NodeRegistrationInfo node) {
		int nodeId = 0;
		RemoteNodeInfo nodeInfo = getRemoteNodeInfo(node);
		if (nodeInfo == null) {
			nodeService.addActivityLogForImportNodes(node.getNodeName(), Severity.Warning, type, EdgeCMWebServiceMessages.getMessage("ImportNode_FailedQueryRemoteRegistry"));
			nodeInfo = new RemoteNodeInfo();
			nodeInfo.setD2DPortNumber(8014); 
			nodeInfo.setD2DProtocol(Protocol.Http);//use default setting to connect!
		}
		
		node.setNodeInfo(nodeInfo);
		node.setD2dPort(nodeInfo.getD2DPortNumber());
		node.setD2dProtocol(nodeInfo.getD2DProtocol());
		if (node.getNodeInfo().isD2DInstalled() ){
			double d2dVersion = 0;
			try{
	    		d2dVersion = Double.parseDouble(node.getNodeInfo().getD2DMajorVersion()+"."+node.getNodeInfo().getD2DMinorVersion());
	    		if (d2dVersion==REQUIRED_D2D_VERSION)
	    			node.setRegisterD2D(true);
	    	}catch (Exception e){
	    		
	    	}
		}
		try {
			RegistrationNodeResult rnr = rpsNodeService.registerRpsNode(false, node);
			if(rnr.getErrorCodes()[0]!=null)
			{		
				/**
				 *  Defect 10267 by Elvis
				 *  Since we already add the node into local database, if node can't be registered,
				 *  we'll only show a warning message instead of the error message in activity log.
				 *  But in log, it's ok to still use error for tracing. 
				 */
				if( "12884901905".equals(rnr.getErrorCodes()[0] ) || "21474836485".equals( rnr.getErrorCodes()[0] ) ) { ///already managed by other cpm
					logger.info("this rps is managed by other cpm, try force manage it!" );
					rpsNodeService.markRpsNodeAsManaged( node , true );
					logger.info("successfully force manage!" );
				}
				else {
					String message = RpsMessageReader.getErrorMessage(rnr.getErrorCodes()[0]);
					String importFailMsg = EdgeCMWebServiceMessages.getMessage("importNodes_ImportSingleFailed", node.getNodeName());
					logger.error(message);
					rpsNodeService.addActivityLogForImportNodes(node.getGatewayId().getRecordId(), node.getNodeName(), Severity.Warning, type, importFailMsg+"-" +message);	
				}		
			}
			
			String message = EdgeCMWebServiceMessages.getMessage("importNodes_ImportSingleFinished", node.getNodeName());
			logger.debug(message);
			rpsNodeService.addActivityLogForImportNodes(Severity.Information, type, message);

			nodeId = rnr.getHostID();
		}
		catch(EdgeServiceFault ef){	
			String message = generateExceptionFromUI(ef.getFaultInfo());
			logger.error(message, ef);
			
			String importFailMsg  = EdgeCMWebServiceMessages.getMessage("importNodes_ImportSingleFailed", node.getNodeName());
			rpsNodeService.addActivityLogForImportNodes(Severity.Error, type, importFailMsg+"-"+message);
			

		}catch (Exception e) {
			String message = EdgeCMWebServiceMessages.getMessage("importNodes_ImportSingleFailed", node.getNodeName());
			logger.error(message, e);
			rpsNodeService.addActivityLogForImportNodes(Severity.Error, type, message);
		}
		
		if( node instanceof NodeRegistrationInfoForRPS && nodeId >0 ){
			if(isNeedRemoteDeploy( node )){ //deploy!
				logger.info("start deploy rps node: name= "+  node.getNodeName() +" id: " + nodeId);
				DeployTargetDetail detail = ((NodeRegistrationInfoForRPS)node).getTargetDetail();
				detail.setNodeID(nodeId); //must set node id;
				try {
					this.deployService.submitRemoteDeploy(Arrays.asList(detail));
				} 
				catch (EdgeServiceFault e) {
					logger.error("failed deploy rps server when add rps", e);
				}
			}else{ // no need deploy
					
				List<DataStoreSettingInfo> settingInfos = DataStoreManager.getInstance().getDataStoreByNodeId(nodeId);
					for (DataStoreSettingInfo settingInfo : settingInfos) {
						if (settingInfo.getFlags() == DataStoreSettingInfo.PHASE_DATASTORE_WAITING_CREATE) {
							try{
								DataStoreManager.getDataStoreManager().save(settingInfo);
							}catch(Exception e){								
								logger.error("Rps already existed, create data store faild.", e);
							}						
						}
					}
			}
		}
		return nodeId;
		
	}

	private boolean isNeedRemoteDeploy( NodeRegistrationInfo node ){
		RemoteNodeInfo info = node.getNodeInfo();
		if( info!=null &&  info.isD2DInstalled() && info.isRPSInstalled() ) {
			String installedVersion = getVersionString( info.getD2DMajorVersion() )+"."+ getVersionString( info.getD2DMinorVersion() ) +"." +
					getVersionString( info.getD2DBuildNumber())+"." +getVersionString( info.getUpdateVersionNumber() );
			
			String patchVersion = nodeService.getRemoteDeployParam(1);
			logger.info("node rps version: " + installedVersion +  " cpm patch version:" + patchVersion);
			
			if( installedVersion.compareTo(patchVersion) >=0 ) {  //installed version is same or newer than patch version
				return false;
			}
		}
		return true;
	}
	private String getVersionString( String v ){
		return v!= null && !v.equals("")  ? v : "0" ;
	}
	@Override
	protected void loadContextData(JobExecutionContext context) {
		
		super.loadContextData(context);
	
		if (context.getJobDetail().getJobDataMap().get(TAG_RPS_SERVICE) instanceof RPSNodeServiceImpl) {
			rpsNodeService = (RPSNodeServiceImpl)context.getJobDetail().getJobDataMap().get(TAG_RPS_SERVICE);
		}
		if (context.getJobDetail().getJobDataMap().get(TAG_SERVICE) instanceof NodeServiceImpl) {
			nodeService = (NodeServiceImpl)context.getJobDetail().getJobDataMap().get(TAG_SERVICE);
		}	
	}
	
	@Override
	protected boolean validateContextData() {
		
		boolean result = super.validateContextData();
		
		if(!result){
			// logger.error("job id is null.");
			return false;
		}		
		
		if (rpsNodeService == null) {
			logger.error("RPS Node service is null.");
			return false;
		}

		return true;
	}
	
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		
		loadContextData(context);
		
		if (!validateContextData()) {
			return;
		}
		
		JobMonitor monitor = JobMonitorManager.getInstance().getJobMonitor(getId(), ImportNodesJobMonitor.class);
		if(monitor == null){
			logger.error("job monitor is null. job id=" + getId());
			return;
		}
		
		synchronized (monitor) {
			rpsNodeService.addActivityLogForImportNodes(Severity.Information, type, 
					EdgeCMWebServiceMessages.getMessage("importNodes_Start", nodes.length));
			importAll();
			rpsNodeService.addActivityLogForImportNodes(Severity.Information, type, 
					EdgeCMWebServiceMessages.getMessage("importNodes_End"));
			
			try {
				SchedulerUtilsImpl.getScheduler().deleteJob(new JobKey(((JobDetailImpl)context.getJobDetail()).getName(), null));
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			monitor.notifyAll();
		}
		
	}
	
	private String generateExceptionFromUI(EdgeServiceFaultBean bean) {
		String errorCode = bean.getCode();
		String errorMessage = RpsMessageReader.getErrorMessage(errorCode);
		
		if (errorMessage != null && bean.getMessageParameters() != null && bean.getMessageParameters().length > 0) {
			errorMessage = Utils.getMessage(errorMessage, bean.getMessageParameters());
		}
		
		return errorMessage;
	}
	
}
