package com.ca.arcserve.edge.app.base.webservice.productdeploy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeProductDeployDao;
import com.ca.arcserve.edge.app.base.common.EdgeCommonUtil;
import com.ca.arcserve.edge.app.base.common.NamingThreadFactory;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.scheduler.impl.SchedulerUtilsImpl;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.AgentInstallationInPlanHelper;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.util.WindowsRegistry;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.IActivityLogService;
import com.ca.arcserve.edge.app.base.webservice.IEdgeCommonService;
import com.ca.arcserve.edge.app.base.webservice.IProductDeployService;
import com.ca.arcserve.edge.app.base.webservice.IRemoteProductDeployService;
import com.ca.arcserve.edge.app.base.webservice.common.EdgeCommonServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeVersionInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.RebootType;
import com.ca.arcserve.edge.app.base.webservice.contract.gateway.GatewayEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.log.LogAddEntity;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DeployStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DeployTargetDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Node;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.productdeploy.DeployStatusInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.productdeploy.ProductImageDownloadInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.productdeploy.ProductImagesInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.taskmonitor.TaskStatus;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.base.webservice.jni.NativeFacade;
import com.ca.arcserve.edge.app.base.webservice.jni.NativeFacadeImpl;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.node.NodeServiceImpl;

public class ProductDeployServiceImpl implements IProductDeployService{
	private static ProductDeployServiceImpl instance=null;
	
	public final String DEPLOY_FOLDER = EdgeCommonUtil.EdgeInstallPath + "deployment\\";
	public final String DEPLOY_CONFIG_FOLDER = DEPLOY_FOLDER + "RemoteDeploy";
	private final String GCDeployFolder = EdgeCommonUtil.EdgeInstallPath + "deployment\\";
	
	public final static String SCHEDULE_DEPLOY_JOB_NAME = "RemoteDeploy";
	public final static String DEPLOYTARGET = "DeployTarget";
	public final static String SCHEDULE_DEPLOY_JOB_GROUP="RemoteDeployGroup";
	public final static String SCHEDULE_DEPLOY_TRIGGER_GROUP="RemoteDeployTriggerGroup";
	 
	private static final int DEFAULT_MAX_THREAD_COUNT = 16;
	private ThreadPoolExecutor remoteDeployExecutor; // thread pool for deploy task 
	
	private static final Logger logger = Logger.getLogger( ProductDeployServiceImpl.class );
	private IEdgeCommonService commonService = new EdgeCommonServiceImpl();
	private IActivityLogService activityLogService = new ActivityLogServiceImpl();
	private NodeServiceImpl nodeService = new NodeServiceImpl();
	private NativeFacade nativeFacade = new NativeFacadeImpl();
//	private Map<Integer, IRemoteProductDeployService> remoteDeployServiceMap = new HashMap<Integer, IRemoteProductDeployService>();
	private static IEdgeProductDeployDao deployTargetDao = DaoFactory.getDao(IEdgeProductDeployDao.class);
	private static IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	
	private ThreadLocal<IRemoteProductDeployService> remoteDeployService = new ThreadLocal<IRemoteProductDeployService>();
	
	public ProductDeployServiceImpl(){
		initDeployConfigFolder();
		initThreadPool();
	}
	public static synchronized ProductDeployServiceImpl getInstance(){
		if(instance == null)
			instance = new ProductDeployServiceImpl();
		return instance;
	}
	
	public void shutdownThreadPool(){
		remoteDeployExecutor.shutdownNow();
	}
	
	/**
	 * Submit remote deploy job According install type (immediately install or schedule install)
	 */
	@Override
	public void submitRemoteDeploy(List<DeployTargetDetail> targets){
		submitRemoteDeploy(targets,false);
	}

	public void submitRemoteDeploy(List<DeployTargetDetail> targets, boolean monitoronly){
		if(targets == null || targets.size()==0){
			logger.debug("[ProductDeployServiceImpl]: Submited remote deploy targets is null or empty.");
			return;
		}
		try {
			for(DeployTargetDetail target : targets){
				target.setStatus(DeployStatus.DEPLOY_PENDING_FOR_DEPLOY.value());
				NodeDetail nodeDetail = null;
				if (StringUtil.isEmptyOrNull( target.getServerName() ))
				{
					if (nodeDetail == null)
						nodeDetail = nodeService.getNodeDetailInformation(target.getNodeID());
					target.setServerName( nodeDetail.getHostname() );
				}
				if (StringUtil.isEmptyOrNull(target.getUsername())) {
					logger.debug("[ProductDeployServiceImpl]: Update deploy target's account from connection info.");
					if (nodeDetail == null)
						nodeDetail = nodeService.getNodeDetailInformation(target.getNodeID());
					target.setUsername(nodeDetail.getUsername());
					target.setPassword(nodeDetail.getPassword());
				}
				if (StringUtil.isEmptyOrNull(target.getUuid())) {
					if (nodeDetail == null)
						nodeDetail = nodeService.getNodeDetailInformation(target.getNodeID());
					String nodeUUID = nodeDetail.getD2DUUID();
					if(StringUtil.isEmptyOrNull(nodeUUID)){
						logger.warn("[ProductDeployServiceImpl]: The uuid is empty or null for the node: "+target.getNodeID()+"_"+target.getServerName()
								+ "If this remote deploy is triggered from plan, please redeploy the plan so that arcserve backup server can get right node uuid"
								+ "when plan have archive to tape task.");
						nodeUUID = UUID.randomUUID().toString();
						logger.info("[ProductDeployServiceImpl]: Generate an uuid for the node: "+target.getNodeID()+"_"+target.getServerName()+", the uuid is: "+nodeUUID);
					}
					target.setUuid(nodeUUID);
				}
				MergeRemoteDeployTarget(target);
				saveDeployTarget(target);
				if(target.getRebootType()==RebootType.RebootSchedule){
					target.setStatus(DeployStatus.DEPLOY_PENDING_SCHEDULE.value());
					updateRemoteDeployJob(target);
					updateStatus(target);
					PlanStatusUpdater.updatePlanPendingScheduleDeploy(target);
				}else {
					RemoveRemoteDeployScheduleJob(target, false);
					submitRemoteDeployImmediately(target, monitoronly);
				}
			}
		} catch (Exception e) {
			logger.error("[ProductDeployServiceImpl]: submiteRemoteDeploy job failed.",e);
		}
	}
	
	/**
	 * Merge the plan id for deploy target
	 * eg: you submit a remote deploy which is from plan, and schedule at 3:00pm, then you submit the deploy from action, you also must update the plan status 
	 * @param target
	 */
	public void MergeRemoteDeployTarget(DeployTargetDetail target) {
		int nodeId = target.getNodeID();
        if(nodeId < 0)
              return;
        String jobName = nodeId + SCHEDULE_DEPLOY_JOB_NAME;
        try {
              Scheduler remoteDeployScheduler = SchedulerUtilsImpl.getScheduler();
              JobKey jobKey = getJobKeyByJobNameAndGoup(jobName, SCHEDULE_DEPLOY_JOB_GROUP);
              JobDetail detail = null;
              if(jobKey != null)
                    detail = remoteDeployScheduler.getJobDetail(jobKey);
              if(detail==null || detail.getJobDataMap() == null)
                    return;
              Object targetObject = detail.getJobDataMap().get(DEPLOYTARGET);
              if(targetObject == null)
                    return;
              DeployTargetDetail originalTarget = (DeployTargetDetail)targetObject;
              //merge plan
              if(AgentInstallationInPlanHelper.needMergedPlanIdsForTwoDeployTarget(originalTarget, target)){
                    target.setPlanIds(AgentInstallationInPlanHelper.mergedPlanIdsForTwoDeployTarget(originalTarget, target));
              }
        } catch (Exception e) {
              logger.error("[ProductDeployServiceImpl]: merge remote deploy target failed.",e);
        }
	}
	
	/**
	 * If the first submit is schedule install, and later submit is immediately install, then remove the schedule job.
	 * @param target
	 */
	public static boolean RemoveRemoteDeployScheduleJob(DeployTargetDetail target, boolean cleanDb) {
		int nodeId = target.getNodeID();
		if(nodeId < 0)
			return false;
		String jobName = nodeId + SCHEDULE_DEPLOY_JOB_NAME;
		try {
			Scheduler remoteDeployScheduler = SchedulerUtilsImpl.getScheduler();
            SimpleTriggerImpl trigger = (SimpleTriggerImpl)getTriggerByJobNameAndGroupName(jobName,SCHEDULE_DEPLOY_JOB_GROUP);
            JobKey jobKey = getJobKeyByJobNameAndGoup(jobName, SCHEDULE_DEPLOY_JOB_GROUP);
            if(trigger!=null && jobKey!=null) {
                remoteDeployScheduler.deleteJob(jobKey);
				if (cleanDb) {
					if (hostMgrDao == null) {
						hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
					}
					hostMgrDao.as_edge_deploy_target_delete_by_id(nodeId);
				} 
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			logger.error("[ProductDeployServiceImpl]:Remove the remote deploy schedule job failed.",e);
			return false;
		}
	}
	
	private static Trigger getTriggerByJobNameAndGroupName(String jobName,String groupName){
	    try {
	          Scheduler scheduler = SchedulerUtilsImpl.getScheduler();
	          JobKey remoteDeployJobKey = getJobKeyByJobNameAndGoup(jobName, groupName);
	          if(remoteDeployJobKey != null){
	                @SuppressWarnings("unchecked")
					List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(remoteDeployJobKey);
	                if(triggers != null && !triggers.isEmpty()){
	                      return triggers.get(0);
	                }
	          }
	    } catch (Exception e) {
	          logger.error("[ProductDeployServiceImpl]:Schedule remote deploy job failed.",e);
	    }
	    return null;
	}

	private static JobKey getJobKeyByJobNameAndGoup(String jobName,String groupName){
        try {
              Scheduler scheduler = SchedulerUtilsImpl.getScheduler();
              for (String groupNameVar : scheduler.getJobGroupNames()) {
                    for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupNameVar))) {
                          if(jobName.equals(jobKey.getName()) && groupName.equals(jobKey.getGroup())){
                                return jobKey;
                          }
                  }
              }
        } catch (Exception e) {
              logger.error("[ProductDeployServiceImpl]:Schedule remote deploy job failed.",e);
        }
        return null;
  }


	/**
	 * If fist submit an schedule job and later submit an another schedule job, then update schedule job trigger time.
	 * @param target
	 */
	public static void updateRemoteDeployJob(DeployTargetDetail target) {
		int nodeId = target.getNodeID();
        if(nodeId < 0)
              return;
        String targetName = nodeId+target.getServerName()==null?"":target.getServerName();
        logger.info("[ProductDeployServiceImpl]: Schedule remote deploy job at "+target.getStartDeploymentTime()+" for target: "+targetName);
        String jobName = nodeId + SCHEDULE_DEPLOY_JOB_NAME;
        try {
              Scheduler remoteDeployScheduler = SchedulerUtilsImpl.getScheduler();
              SimpleTriggerImpl trigger = (SimpleTriggerImpl)getTriggerByJobNameAndGroupName(jobName,SCHEDULE_DEPLOY_JOB_GROUP);
              JobKey jobKey = getJobKeyByJobNameAndGoup(jobName, SCHEDULE_DEPLOY_JOB_GROUP);
              if(trigger != null && jobKey!=null){
                    remoteDeployScheduler.deleteJob(jobKey);
              }
              
              JobDetailImpl detail = new JobDetailImpl();
              detail.setName(jobName);
              detail.setGroup(SCHEDULE_DEPLOY_JOB_GROUP);
              detail.setJobClass(ProductDeployScheduleJob.class);

              detail.getJobDataMap().put(DEPLOYTARGET, target);
              trigger = new SimpleTriggerImpl();
              trigger.setStartTime(target.getStartDeploymentTime());
              trigger.setName(jobName);
  			  trigger.setGroup(SCHEDULE_DEPLOY_TRIGGER_GROUP);
              remoteDeployScheduler.scheduleJob(detail,trigger);
              
              if(remoteDeployScheduler.isShutdown())
                    remoteDeployScheduler.start();
              
        } catch (Exception e) {
              logger.error("[ProductDeployScheduleManager]:Schedule remote deploy job for: "+targetName+" failed.",e);
        }

	}
	
	/**
	 * Save the deploy target to DB.
	 * @param detail
	 * @return
	 * @throws EdgeServiceFault
	 */
	private synchronized int saveDeployTarget(DeployTargetDetail detail){
		String targetName = detail.getNodeID()+"_"+detail.getServerName();
		logger.info("[ProductDeployServiceImpl]Begin save deploy target: "+targetName);
		if (detail.getTargetId()==null ) {
			detail.setTargetId(0);
		}
		
		try {
			int targetId = detail.getTargetId();
			String serverName = detail.getServerName();
			String uuid = UUID.randomUUID().toString();
			String userName = detail.getUsername();
			String password = detail.getPassword();
			int protocol = detail.getProtocol().ordinal();
			int port = detail.getPort();
			int productType = detail.getProductType();
			String installDirectory = detail.getInstallDirectory();
			int installDriver = detail.isInstallDriver() ? 1:0;

			int rebootType = detail.getRebootType().ordinal();
			int selected = detail.isSelected() ? 1:0;
			Date startDeploymentTime = detail.getStartDeploymentTime();
			int status = detail.getStatus();
			int taskstatus = detail.getTaskstatus();
			long msgCode = detail.getMsgCode();
			int nodeID = detail.getNodeID();
			int percentage = detail.getPercentage();
			String progressMessage = detail.getProgressMessage();
			String warningMessage = detail.getWarningMessage();
			String planIds = detail.getPlanIds();
			int[] id = new int[1];
			deployTargetDao.as_edge_deploy_target_update(targetId, serverName, uuid, userName, password, protocol, port,
					productType, installDirectory, installDriver, 1 /*autoStartRRService always true*/, rebootType, selected,
					startDeploymentTime, status, taskstatus, msgCode, nodeID, percentage, progressMessage,warningMessage,planIds, id);
			logger.info("[ProductDeployServiceImpl]:End save deploy target: "+targetName+". The targetid is: "+id[0]);
			return id[0];
		} catch (Exception e) {
			logger.error("[ProductDeployServiceImpl]:Failed to save deploy target "+targetName+".", e);
			return 0;
		}
	}

	/**
	 * 
	 * @param target
	 */
	public synchronized void submitRemoteDeployImmediately(DeployTargetDetail target, boolean monitorOnly){
		if(target == null){
			logger.debug("[ProductDeployServiceImpl]: Have no target to deploy.");
			return;
		}
		if(target.getNodeID() <= 0){
			logger.error("[ProductDeployServiceImpl]: Target "+target.getServerName()+"have no node id.");
			return;
		}
		try
		{
			IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean( IEdgeGatewayLocalService.class );
			GatewayEntity gateway = gatewayService.getGatewayByHostId( target.getNodeID() );
			target.setGatewayId( gateway.getId() );
		}
		catch (Exception e)
		{
			logger.error( "submitRemoteDeployImmediately(): Failed to get gateway ID for target node. Node ID: " + target.getNodeID(), e );
			return;
		}
		String targetName = target.getNodeID()+"_"+(StringUtil.isEmptyOrNull(target.getServerName())?"":target.getServerName());
		logger.info("[ProductDeployServiceImpl]: Submit remote deploy job to target: " + targetName+" at the time: " + new Date());
		remoteDeployExecutor.execute(new ProductDeployTask(target, monitorOnly));
		logger.info("[ProductDeployServiceImpl]: Successfully Submited the remote deploy job for target: "+targetName);
	}
	
	public void writeActivityLogForDeployTarget(DeployTargetDetail deployTarget){
		try{
			Severity severity = Severity.All;
			switch (TaskStatus.parseTaskStatus(deployTarget.getTaskstatus())) {
			case OK:
				severity = Severity.Information;
				break;
			case Error:
				severity = Severity.Error;
				break;
			case Warning:
			case WarnningCanContinue:
				severity = Severity.Warning;
				break;
			default:
				break;
			}
			String logContent = "";
			if(!StringUtil.isEmptyOrNull(deployTarget.getFinalTitleMessage())){
				logContent = deployTarget.getFinalTitleMessage()+". ";
			}
			logContent = logContent+deployTarget.getFinalDetailMessage();
			//Now there is no remote deploy module , so not set the jobtype
			writeAcitivityLog(severity, 0, deployTarget.getNodeID(), logContent);
		}catch (Exception e){
			logger.error("[ProductDeployServiceImpl]: writeActivityLogForDeployTarget(): "
					+ "Writting activity log failed for target "+deployTarget.getNodeID()+"_"+deployTarget.getServerName()+".",e);
		}
	}
	
	public void writeAcitivityLog(Severity serverity,int jobId,int targetHostId,String logContent){
		try{
			LogAddEntity log = new LogAddEntity();
			log.setJobId( jobId );
			log.setTargetHostId(targetHostId);
			log.setSeverity(serverity);
			log.setMessage(logContent);
			activityLogService.addUnifiedLog(log);
		}catch (Exception e){
			logger.error("[ProductDeployServiceImpl]: writeAcitivityLog(): Failed to write activitylog.");
		}
	}
	
	public void deleteRemoteDeploy(List<DeployTargetDetail> targets)
			throws EdgeServiceFault {
		// TODO Auto-generated method stub
		
	}
	
	private void initDeployConfigFolder(){
		File deployPath = new File(DEPLOY_CONFIG_FOLDER);
		if(!deployPath.exists()){
			logger.info("[RemoteDeployServiceImpl]: Create remote deploy configuation folder : " + deployPath.getAbsolutePath());
			deployPath.mkdirs();
		}
	}
	
	private void initThreadPool(){
		int maxThreadCount = getMaxThreadCount();
		remoteDeployExecutor = new ThreadPoolExecutor(
					maxThreadCount, maxThreadCount, 1L, TimeUnit.SECONDS,
		            new LinkedBlockingQueue<Runnable>(), new NamingThreadFactory("RemoteDeploy"));
		remoteDeployExecutor.allowCoreThreadTimeOut(true);
	}
	
	/**
	 * Get the max count of deploy thread from registry.
	 * @return
	 */
	private int getMaxThreadCount(){
		int result = DEFAULT_MAX_THREAD_COUNT;
		String maxThreadCount = CommonUtil.getApplicationExtentionKey(
									WindowsRegistry.KEY_NAME_ROOT_CM, 
									WindowsRegistry.VALUE_NAME_DEPLOY_MAX_THREAD_COUNT);
		if(!StringUtil.isEmptyOrNull(maxThreadCount)){
			logger.debug("get deployMaxThreadCount from registry, value:" + maxThreadCount);
			try {
				int value = Integer.parseInt(maxThreadCount);
				if(value > 0){
					result = value;
				}
			} catch (Exception e) {
				logger.error("incorrect value for deployMaxThreadCount.",e);			
			}
		}
		logger.debug("get deployMaxThreadCount, result:"+result);			
		return result;
	}
	
	public void updateStatus(DeployTargetDetail detail){
		deployTargetDao.updateDeployTargetStatus(detail.getNodeID(), 
				detail.getProtocol().ordinal(), detail.getPort(), 
				detail.getStatus(), detail.getTaskstatus(), "", "");
	}
	
	public NativeFacade getNativeFacade(){
		return nativeFacade;
	}
	@Override
	public void startDeployProcess(DeployTargetDetail target)
			throws EdgeServiceFault {
		getRemoteDeployService(target.getNodeID()).startDeployProcess(target);
	}
	@Override
	public DeployStatusInfo getDeployStatus(DeployTargetDetail target)
			throws EdgeServiceFault {
		return getRemoteDeployService(target.getNodeID()).getDeployStatus(target);
	}
	@Override
	public int getDeployProcessExitValue(DeployTargetDetail target)
			throws EdgeServiceFault {
		return getRemoteDeployService(target.getNodeID()).getDeployProcessExitValue(target);
	}
	
	@Override
	public String getTargetUUID(DeployTargetDetail target)
			throws EdgeServiceFault {
		return getRemoteDeployService(target.getNodeID()).getTargetUUID(target);
	}
	
	public String getLicenseText() {
		String licenseText = "";
		if (licenseText != null && !licenseText.isEmpty())
			return licenseText;

		String filePath = GCDeployFolder + "D2D\\Install\\";
		licenseText = "";
		String line;
		String charSet = "UTF-8";

		String localeString = System.getProperty("user.language");
		if (localeString.compareToIgnoreCase("de") == 0) {
			filePath = filePath + "1031\\";
		} else if (localeString.compareToIgnoreCase("es") == 0) {
			filePath = filePath + "1034\\";
		} else if (localeString.compareToIgnoreCase("fr") == 0) {
			filePath = filePath + "1036\\";
		} else if (localeString.compareToIgnoreCase("it") == 0) {
			filePath = filePath + "1040\\";
		} else if (localeString.compareToIgnoreCase("ja") == 0) {
			filePath = filePath + "1041\\";
		} else if (localeString.compareToIgnoreCase("pt") == 0) {
			filePath = filePath + "1046\\";
		} else {
			filePath = filePath + "1033\\";
		}

		try {
			BufferedReader reader;
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(filePath + "License.txt"), charSet));

			try {
				while ((line = reader.readLine()) != null) {
					if (line.indexOf(">") != -1)
						line = line.replaceAll(">", "&#62;");
					if (line.indexOf("<") != -1)
						line = line.replaceAll("<", "&#60;");
					licenseText += line + "<br>";
				}
			} catch (IOException e) {
				logger.error("Could not read License.txt!" , e);
			}
			try {
				reader.close();
			} catch (IOException e) {
				logger.debug("Could not close License.txt!", e);
			}
		} catch (FileNotFoundException e) {
			logger.error("Could not find License.txt!", e );
		} catch (UnsupportedEncodingException e) {
			logger.error("getLicenseText() - Do not support UTF-8!" ,e);
		}

		return licenseText;
	}
	
	//when restart the web service, console should schedule the product-deploy job and
	//monitor the In-progress product-deploying job
	public void resumeDeployTargetProcess(){
		try{
			List<DeployTargetDetail> targets = new ArrayList<DeployTargetDetail>();
			deployTargetDao.as_edge_deploy_target_list(targets);
			List<DeployTargetDetail> needRedeployTargets=new ArrayList<DeployTargetDetail>();
			for(DeployTargetDetail targetDetail : targets){
				if(DeployStatus.isInProgress(targetDetail.getStatus())){
					needRedeployTargets.add(targetDetail);
					targetDetail.setStatus(DeployStatus.DEPLOY_PENDING_FOR_DEPLOY.value());
					targetDetail.setTaskstatus(TaskStatus.Pending.getValue());
					targetDetail.setProgressMessage("");
					targetDetail.setWarningMessage("");
					saveDeployTarget(targetDetail);
				}
				else if(targetDetail.getStatus()==DeployStatus.DEPLOY_PENDING_SCHEDULE.value()){
					needRedeployTargets.add(targetDetail);
				}
			}
			submitRemoteDeploy(needRedeployTargets,true);
		}catch(Exception e){
			logger.error("Failed to resume plan deployment in contextlistener.", e);
		}
	}
	
	@Deprecated
	@Override
	public synchronized List<DeployTargetDetail> getDeployTargets( List<Integer> hostIds) throws EdgeServiceFault {
		List<DeployTargetDetail> details = new ArrayList<DeployTargetDetail>();
		if (hostIds != null ) {
			for (Integer key : hostIds) {
				DeployTargetDetail target = getDeployTargetWithNodeId(key);
				if (target != null) {
					details.add(target);
				}
			}
		}
		return details;
	}
	
	/* Get deploy target detail from DB via nodeId */
	private DeployTargetDetail getDeployTargetWithNodeId(int nodeId) throws EdgeServiceFault {
		try {
			List<DeployTargetDetail> detailList = new ArrayList<DeployTargetDetail>();
			hostMgrDao.as_edge_deploy_target_list_by_nodeId(nodeId, detailList);
			if (detailList!=null && detailList.size() > 0) {
				return detailList.get(0);
			}
		} catch (Exception e) {
			logger.error("Failed to get deploy target's with nodeId.", e);
			throw EdgeServiceFault.getFault(EdgeServiceErrorCode.Common_Service_Dao_Execption, "");
		}
		return null;
	}
	@Override
	public List<Node> cancelRemoteDeploy(List<Node> sourceNodes, String errorMessage)
			throws EdgeServiceFault {
		for (Node node : sourceNodes) {
			DeployTargetDetail detail = new DeployTargetDetail();
			detail.setNodeID(node.getId());
			node.setScheduleDeployCanceled(RemoveRemoteDeployScheduleJob(detail, true));
			hostMgrDao.as_edge_cancel_deployment_update_status(node.getId(), errorMessage);
		}
		return sourceNodes;
	}
	
	private IRemoteProductDeployService getRemoteDeployService(int nodeId) throws EdgeServiceFault{
		//IRemoteProductDeployService remoteProductDeployService = remoteDeployServiceMap.get(nodeId);
		IRemoteProductDeployService remoteProductDeployService = remoteDeployService.get();
		if(remoteProductDeployService == null){
			IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
			GatewayEntity gateway = gatewayService.getGatewayByHostId(nodeId);
			IRemoteProductDeployServiceFactory remoteDeployServiceFactory = EdgeFactory.getBean(IRemoteProductDeployServiceFactory.class);
			remoteProductDeployService = remoteDeployServiceFactory.createRemoteProductDeployService(gateway);
			//remoteDeployServiceMap.put(nodeId, remoteProductDeployService);
			remoteDeployService.set( remoteProductDeployService );
		}
		return remoteProductDeployService;
	}
	
//	private static final String AGENT_GM_IMAGE_FILE_PATH = "$CONSOLE_HOME$\\Deployment\\Agent\\Arcserve_Unified_Data_Protection_Agent_Windows.zip";
//	private static final String GM_IMAGE_DOWNLOAD_URL = "/management/download/Agent/Arcserve_Unified_Data_Protection_Agent_Windows.zip";
//	private static final String GM_IMAGE_MD5_DOWNLOAD_URL = "/management/download/Agent/Arcserve_Unified_Data_Protection_Agent_Windows.txt";
	
//	@Override
//	public ProductImagesInfo getProductImagesInfo() throws EdgeServiceFault
//	{
//		String consoleHome = CommonUtil.BaseEdgeInstallPath;
//		
//		ProductImagesInfo imageInfo = new ProductImagesInfo();
//		
//		imageInfo.setConsoleVersionInfo( this.commonService.getVersionInformation() );
//		imageInfo.setUdpateVersionNumber( 0 );
//		
//		String agentGmImageFilePath = AGENT_GM_IMAGE_FILE_PATH.replace( "$CONSOLE_HOME$", consoleHome );
//		File agentGmImageFile = new File( agentGmImageFilePath );
//		
//		ProductImageDownloadInfo gmImageDownloadInfo = new ProductImageDownloadInfo();
//		gmImageDownloadInfo.setDownloadUrl( GM_IMAGE_DOWNLOAD_URL );
//		gmImageDownloadInfo.setLocalPath( "$CONSOLE_HOME$\\Deployment\\Agent\\Arcserve_Unified_Data_Protection_Agent_Windows.zip" );
//		gmImageDownloadInfo.setMd5DownloadUrl( GM_IMAGE_MD5_DOWNLOAD_URL );
//		gmImageDownloadInfo.setMd5LocalPath( "$CONSOLE_HOME$\\Deployment\\Agent\\Arcserve_Unified_Data_Protection_Agent_Windows.txt" );
//		gmImageDownloadInfo.setDataSize( agentGmImageFile.length() );
//		imageInfo.setGmImageDownloadInfo( gmImageDownloadInfo );
//		
//		DeploymentPackageInfo updatePkgInfo = getUpdatePackageInfo();
//		
//		ProductImageDownloadInfo updateImageDownloadInfo = new ProductImageDownloadInfo();
//		updateImageDownloadInfo.setDownloadUrl( updatePkgInfo.downloadUrlPath );
//		updateImageDownloadInfo.setLocalPath( updatePkgInfo.gatewayLocalPath );
//		updateImageDownloadInfo.setMd5DownloadUrl( "" );
//		updateImageDownloadInfo.setMd5LocalPath( "" );
//		imageInfo.setUpdateImageDownloadInfo( updateImageDownloadInfo );
//		
//		return imageInfo;
//	}
	
	@Override
	public ProductImagesInfo getProductImagesInfo() throws EdgeServiceFault
	{
		EdgeVersionInfo versionInfo = this.commonService.getVersionInformation();
		
		ProductImagesInfo imageInfo = new ProductImagesInfo();
		
		imageInfo.setConsoleVersionInfo( versionInfo );
		imageInfo.setUdpateVersionNumber( parseIntegerString( versionInfo.getUpdateNumber(), 0 ) );
		
		DeploymentPackageInfo gmPkgInfo = getGmPackageInfo();

		ProductImageDownloadInfo gmImageDownloadInfo = new ProductImageDownloadInfo();
		gmImageDownloadInfo.setDownloadUrl( gmPkgInfo.downloadUrlPath );
		gmImageDownloadInfo.setLocalPath( gmPkgInfo.gatewayLocalPath );
		gmImageDownloadInfo.setMd5DownloadUrl( gmPkgInfo.md5DownloadUrlPath );
		gmImageDownloadInfo.setMd5LocalPath( gmPkgInfo.md5GatewayLocalPath );
		gmImageDownloadInfo.setDataSize( getFileSize( gmPkgInfo.consoleLocalPath ) );
		imageInfo.setGmImageDownloadInfo( gmImageDownloadInfo );
		
		DeploymentPackageInfo updatePkgInfo = getUpdatePackageInfo();
		
		ProductImageDownloadInfo updateImageDownloadInfo = new ProductImageDownloadInfo();
		updateImageDownloadInfo.setDownloadUrl( updatePkgInfo.downloadUrlPath );
		updateImageDownloadInfo.setLocalPath( updatePkgInfo.gatewayLocalPath );
		updateImageDownloadInfo.setMd5DownloadUrl( "" );
		updateImageDownloadInfo.setMd5LocalPath( "" );
		gmImageDownloadInfo.setDataSize( getFileSize( updatePkgInfo.consoleLocalPath ) );
		imageInfo.setUpdateImageDownloadInfo( updateImageDownloadInfo );
		
		return imageInfo;
	}
	
	private long getFileSize( String filePath )
	{
		try
		{
			File file = new File( filePath );
			return file.length();
		}
		catch (Exception e)
		{
			logger.error( "Error getting file size. File path: " + filePath, e );
			return 0;
		}
	}
	
	private static final String PLACEHOLDER_UDP_HOME		= "<UDP_HOME>";
	private static final String PLACEHOLDER_CONSOLE_HOME	= "<CONSOLE_HOME>";
	private static final String PLACEHOLDER_MAJOR_VERSION	= "<MAJOR_VERSION>";
	private static final String PLACEHOLDER_MINOR_VERSION	= "<MINOR_VERSION>";
	private static final String PLACEHOLDER_UPDATE_NUMBER	= "<UPDATE_NUMBER>";
	
	private static final String GMPKG_DOWNLOAD_ROOT				= "/management/download/Agent/";
	private static final String GMPKG_FOLDER_TEMPLATE			= "<CONSOLE_HOME>\\Deployment\\Agent\\";
	private static final String GMPKG_NAME_TEMPLATE				= "Arcserve_Unified_Data_Protection_Agent_Windows.zip";
	private static final String GMPKG_MD5_NAME_TEMPLATE			= "Arcserve_Unified_Data_Protection_Agent_Windows.txt";
	
	private static final String UPDPKG_DOWNLOAD_ROOT			= "/management/UDPUpdates/";
	private static final String CONSOLE_UPDPKG_FOLDER_TEMPLATE	= "<UDP_HOME>\\Update Manager\\FullUpdates\\r<MAJOR_VERSION>.<MINOR_VERSION>";
	private static final String CONSOLE_UPDPKG_NAME_TEMPLATE	= "Arcserve_Unified_Data_Protection_<MAJOR_VERSION>.<MINOR_VERSION>_Update_<UPDATE_NUMBER>.exe";
	private static final String CONSOLE_UPDPKG_DOWNLOAD_FOLDER	= "FullUpdates/r<MAJOR_VERSION>.<MINOR_VERSION>/";
	private static final String AGENT_UPDPKG_FOLDER_TEMPLATE	= "<UDP_HOME>\\Update Manager\\EngineUpdates\\r<MAJOR_VERSION>.<MINOR_VERSION>";
	private static final String AGENT_UPDPKG_NAME_TEMPLATE		= "Arcserve_Unified_Data_Protection_Agent_<MAJOR_VERSION>.<MINOR_VERSION>_Update_<UPDATE_NUMBER>.exe";
	private static final String AGENT_UPDPKG_DOWNLOAD_FOLDER	= "EngineUpdates/r<MAJOR_VERSION>.<MINOR_VERSION>/";
	
	public static String instantiateTemplate( String template )
	{
		return instantiateTemplate( template, null );
	}
	
	public static String instantiateTemplateIgnoreHomes( String template )
	{
		List<String> exceptionPlaceholders = new ArrayList<>();
		exceptionPlaceholders.add( PLACEHOLDER_UDP_HOME );
		exceptionPlaceholders.add( PLACEHOLDER_CONSOLE_HOME );
		
		return instantiateTemplate( template, exceptionPlaceholders );
	}
	
	public static String instantiateTemplate( String template, List<String> exceptionPlaceholders )
	{
		if (exceptionPlaceholders == null)
			exceptionPlaceholders = new ArrayList<>();
		
		String udpHome = CommonUtil.udpHome;
		if (udpHome.endsWith( "\\" ))
			udpHome = udpHome.substring( 0, udpHome.length() - 1 );
		
		String consoleHome = CommonUtil.BaseEdgeInstallPath;
		if (consoleHome.endsWith( "\\" ))
			consoleHome = consoleHome.substring( 0, consoleHome.length() - 1 );
		
		EdgeVersionInfo versionInfo = EdgeCommonServiceImpl.getVersionInformation2();
		
		String result = template;
		
		if (!exceptionPlaceholders.contains( PLACEHOLDER_UDP_HOME ))
			result = result.replace( PLACEHOLDER_UDP_HOME, udpHome );
		
		if (!exceptionPlaceholders.contains( PLACEHOLDER_CONSOLE_HOME ))
			result = result.replace( PLACEHOLDER_CONSOLE_HOME, consoleHome );
		
		if (!exceptionPlaceholders.contains( PLACEHOLDER_MAJOR_VERSION ))
			result = result.replace( PLACEHOLDER_MAJOR_VERSION, Integer.toString( versionInfo.getMajorVersion() ) );
		
		if (!exceptionPlaceholders.contains( PLACEHOLDER_MINOR_VERSION ))
			result = result.replace( PLACEHOLDER_MINOR_VERSION, Integer.toString( versionInfo.getMinorVersion() ) );
		
		if (!exceptionPlaceholders.contains( PLACEHOLDER_UPDATE_NUMBER ))
			result = result.replace( PLACEHOLDER_UPDATE_NUMBER, Integer.toString( parseIntegerString( versionInfo.getUpdateNumber(), 0 ) ) );
		
		return result;
	}
	
	private static class DeploymentPackageInfo
	{
		public String downloadUrlPath = "";
		public String consoleLocalPath = "";
		public String gatewayLocalPath = "";
		public String md5DownloadUrlPath = "";
		public String md5ConsoleLocalPath = "";
		public String md5GatewayLocalPath = "";
	}
	
	private DeploymentPackageInfo getGmPackageInfo() throws EdgeServiceFault
	{
		String agentGmPkgFolder = instantiateTemplate( GMPKG_FOLDER_TEMPLATE );
		String agentGmPkgFolderIgnoreHomes = instantiateTemplateIgnoreHomes( GMPKG_FOLDER_TEMPLATE );
		
		String agentGmPkgName = instantiateTemplate( GMPKG_NAME_TEMPLATE );
		String agentGmPkgMd5Name = instantiateTemplate( GMPKG_MD5_NAME_TEMPLATE );
		
		DeploymentPackageInfo pkgInfo = new DeploymentPackageInfo();
		
		pkgInfo.downloadUrlPath = GMPKG_DOWNLOAD_ROOT + agentGmPkgName;
		pkgInfo.consoleLocalPath = agentGmPkgFolder + agentGmPkgName;
		pkgInfo.gatewayLocalPath = agentGmPkgFolderIgnoreHomes + agentGmPkgName;
		
		pkgInfo.md5DownloadUrlPath = GMPKG_DOWNLOAD_ROOT + agentGmPkgMd5Name;
		pkgInfo.md5ConsoleLocalPath = agentGmPkgFolder + agentGmPkgMd5Name;
		pkgInfo.md5GatewayLocalPath = agentGmPkgFolderIgnoreHomes + agentGmPkgMd5Name;
		
		return pkgInfo;
	}
	
	private DeploymentPackageInfo getUpdatePackageInfo() throws EdgeServiceFault
	{
		DeploymentPackageInfo pkgInfo = new DeploymentPackageInfo();

		EdgeVersionInfo versionInfo = this.commonService.getVersionInformation();
		if (parseIntegerString( versionInfo.getUpdateNumber(), 0 ) == 0)
			return pkgInfo;

		String agentUpdPkgFolder = instantiateTemplate( AGENT_UPDPKG_FOLDER_TEMPLATE );
		String agentUpdPkgName = instantiateTemplate( AGENT_UPDPKG_NAME_TEMPLATE );
		String agentUpdPkgDownloadFolder = instantiateTemplate( AGENT_UPDPKG_DOWNLOAD_FOLDER );
		String agentUpdPkgDownloadURL = UPDPKG_DOWNLOAD_ROOT + agentUpdPkgDownloadFolder + agentUpdPkgName;
		
		String agentUpdPkgLocalPath = concatPath( agentUpdPkgFolder, agentUpdPkgName );
		File agentUpdPkgFile = new File( agentUpdPkgLocalPath );
		if (agentUpdPkgFile.exists())
		{
			pkgInfo.downloadUrlPath = agentUpdPkgDownloadURL;
			pkgInfo.consoleLocalPath = agentUpdPkgLocalPath;
			pkgInfo.gatewayLocalPath = concatPath( instantiateTemplateIgnoreHomes( AGENT_UPDPKG_FOLDER_TEMPLATE ), agentUpdPkgName );
			
			return pkgInfo;
		}
		
		String consoleUpdPkgFolder = instantiateTemplate( CONSOLE_UPDPKG_FOLDER_TEMPLATE );
		String consoleUpdPkgName = instantiateTemplate( CONSOLE_UPDPKG_NAME_TEMPLATE );
		String consoleUpdPkgDownloadFolder = instantiateTemplate( CONSOLE_UPDPKG_DOWNLOAD_FOLDER );
		String consoleUpdPkgDownloadURL = UPDPKG_DOWNLOAD_ROOT + consoleUpdPkgDownloadFolder + consoleUpdPkgName;
		
		pkgInfo.downloadUrlPath = consoleUpdPkgDownloadURL;
		pkgInfo.consoleLocalPath = concatPath( consoleUpdPkgFolder, consoleUpdPkgName );
		pkgInfo.gatewayLocalPath = concatPath( instantiateTemplateIgnoreHomes( CONSOLE_UPDPKG_FOLDER_TEMPLATE ), consoleUpdPkgName );
		
		return pkgInfo;
	}
	
	private static int parseIntegerString( String string, int defaultValue )
	{
		try
		{
			return Integer.parseInt( string );
		}
		catch (Exception e)
		{
			return defaultValue;
		}
	}
	
	private String concatPath( String absPath, String relPath )
	{
		if (!absPath.endsWith( "\\" ))
			absPath += "\\";
		return absPath + relPath;
	}
}
