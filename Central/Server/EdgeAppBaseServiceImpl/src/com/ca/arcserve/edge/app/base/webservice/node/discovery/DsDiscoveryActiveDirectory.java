package com.ca.arcserve.edge.app.base.webservice.node.discovery;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.util.EmailContentTemplate;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeAdDao;
import com.ca.arcserve.edge.app.base.common.ApplicationUtil;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.EdgeCMWebServiceMessages;
import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.IActivityLogService;
import com.ca.arcserve.edge.app.base.webservice.contract.discovery.DiscoveryHistory;
import com.ca.arcserve.edge.app.base.webservice.contract.log.ActivityLog;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Severity;
import com.ca.arcserve.edge.app.base.webservice.contract.node.AutoDiscoverySetting.SettingType;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryApplication;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryMonitor;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryOption;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryPhase;
import com.ca.arcserve.edge.app.base.webservice.contract.node.DiscoveryStatus;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EmailTemplateSetting;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Node;
import com.ca.arcserve.edge.app.base.webservice.contract.taskmonitor.TaskDetail;
import com.ca.arcserve.edge.app.base.webservice.contract.taskmonitor.TaskStatus;
import com.ca.arcserve.edge.app.base.webservice.email.EdgeEmailService;
import com.ca.arcserve.edge.app.base.webservice.gateway.EntityType;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacade;
import com.ca.arcserve.edge.app.base.webservice.jni.IRemoteNativeFacadeFactory;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.taskmonitor.TaskMonitor;
import com.ca.arcserve.edge.webservice.jni.model.JNode;

public class DsDiscoveryActiveDirectory implements Callable<String> {
	private static final String EDGEMAIL_DiscoveredNodes = EdgeCMWebServiceMessages
							.getResource("EDGEMAIL_DiscoveredNodes");
	private static final String EDGEMAIL_DiscoveredNodesAmount = EdgeCMWebServiceMessages
							.getResource("EDGEMAIL_DiscoveredNodesAmount");
	private static final String EDGEMAIL_AccessAddress = EdgeCMWebServiceMessages
							.getResource("EDGEMAIL_AccessAddress");
	private static final String EDGEMAIL_DiscoveryEndTime = EdgeCMWebServiceMessages
							.getResource("EDGEMAIL_DiscoveryEndTime");
	private static final String EDGEMAIL_DiscoveryBeginTime = EdgeCMWebServiceMessages
							.getResource("EDGEMAIL_DiscoveryBeginTime");
	private static final String EDGEMAIL_NodeNameFilter = EdgeCMWebServiceMessages
							.getResource("EDGEMAIL_NodeNameFilter");
	private static final String EDGEMAIL_Account = EdgeCMWebServiceMessages.getResource("EDGEMAIL_Account");
	private static final String EDGEMAIL_Exchange = EdgeCMWebServiceMessages.getResource("EDGEMAIL_Exchange");
	private static final String EDGEMAIL_SQLServer = EdgeCMWebServiceMessages.getResource("EDGEMAIL_SQLServer");
	private DiscoveryOption[]  				options = null;
	private static IEdgeAdDao				adDao = DaoFactory.getDao(IEdgeAdDao.class);
	private IActivityLogService 			activityLogService = new ActivityLogServiceImpl();
	private ActivityLog 					activityLog = new ActivityLog();
	private String uuid;
	private static Logger logger = Logger.getLogger( DsDiscoveryActiveDirectory.class );
	
	private IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	DsDiscoveryActiveDirectory(DiscoveryOption[] options){
		this.options = options;
		activityLog.setModule(Module.Common);
	}
	
	@Override
	public String call() throws Exception {
		String strErrorCode = null;
		
		activityLog.setSeverity(Severity.Information);
		activityLog.setTime(new Date(System.currentTimeMillis()));
		String messageBegin = EdgeCMWebServiceMessages.getMessage("autoDiscovery_ESX_BeginDiscoverFromAD");				
		activityLog.setMessage(messageBegin);
		activityLogService.addLog(activityLog);
		
		DiscoveryService.getInstance().startDiscoveryMonitor();
		DiscoveryMonitor monitor =  DiscoveryService.getInstance().getDiscoveryMonitor();
			
		synchronized (monitor) {
				
			monitor.setUuid(this.getUuid());
		}
		try
		{
			DiscoverNodeFromAD(options);
			DiscoveryService.getInstance().stopDiscoveryMonitor();
		}
		catch (EdgeServiceFault e)
		{
			strErrorCode = e.getFaultInfo().getCode();
			DiscoveryService.getInstance().setDiscoveryError(strErrorCode);
			DiscoveryService.getInstance().stopDiscoveryMonitor();
				
			activityLog.setSeverity(Severity.Error);
			activityLog.setTime(new Date(System.currentTimeMillis()));
			activityLog.setMessage(e.getFaultInfo().getMessage());
			activityLogService.addLog(activityLog);
		}
		finally
		{
			activityLog.setSeverity(Severity.Information);
			activityLog.setTime(new Date(System.currentTimeMillis()));
			String messageEnd = EdgeCMWebServiceMessages.getMessage("autoDiscovery_ESX_EndDiscoverFromAD");				
			activityLog.setMessage(messageEnd);
			activityLogService.addLog(activityLog);
		}
		
		
		return strErrorCode;
	}	

	private Node convert2Node(JNode jNode){

		Node item = new Node();
		
		//if it returns FQDN name, split domain . 
		int index = jNode.getNodeName().indexOf(".");
		if(index > 0){
			item.setDomainName(jNode.getNodeName().substring(index+1));
		}else{
			item.setDomainName("");
		}
		item.setHostname(jNode.getNodeName());
		item.setIpaddress(jNode.getIpAddress());
		item.setServerPrincipalName(jNode.getServerPrincipalName());
		item.setLastupdated(new Date());
		//Convert other class members...

		return item;
	}
	
	private void setMonitorProcess(long nodeCount, DiscoveryPhase phase, DiscoveryOption option) {
		DiscoveryMonitor monitor =  DiscoveryService.getInstance().getDiscoveryMonitor();		
		synchronized (monitor) {
			monitor.setProcessedNodeNum(nodeCount);
			monitor.setDiscoveryPhase(phase);
			monitor.setOption(option);
			monitor.setDiscoveryStatus(DiscoveryStatus.DISCOVERY_STATUS_ACTIVE);
		}
	}
	
	private void DiscoverNodeFromAD(DiscoveryOption[] options) throws EdgeServiceFault {
		if (options == null || options.length == 0) {
			return;
		}
		
		int nodeCount = 0;
		Date beginTime = null;
		Date endTime = null;
		// init job with pending status
		for (DiscoveryOption option : options) {
			if(option.getTaskId() == -1){ 
				int taskId = TaskMonitor.registerNewTask(Module.Discovery, option.getUserName() + option.getComputerNameFilter(), getTaskDetail(option.getId(), DiscoveryStatus.DISCOVERY_STATUS_PENDING));
				option.setTaskId(taskId);
			}
		}
		adDao.as_edge_truncate_ad_result();
		Set<Integer> addednodes=new HashSet<Integer>();
		IRemoteNativeFacadeFactory remoteNativeFacadeFactory = EdgeFactory.getBean(IRemoteNativeFacadeFactory.class);
		for (DiscoveryOption option : options) {
			TaskMonitor.updateTaskStatus(option.getTaskId(), TaskStatus.InProcess, getTaskDetail(option.getId(), DiscoveryStatus.DISCOVERY_STATUS_ACTIVE));
			beginTime = new Date(System.currentTimeMillis());
			setMonitorProcess(nodeCount, DiscoveryPhase.DISCOVERY_PHASE_NODE, option);
			//modified by fanda03 102830; move from discovery service to here
			try {
				IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( option.getGatewayId() );
				nativeFacade.verifyADAccount(option.getTargetComputerName(), option.getUserName(), option.getPassword());
			} catch(EdgeServiceFault esf) {
				activityLog.setModule(Module.Common);
				activityLog.setSeverity(Severity.Error);
				activityLog.setTime(new Date(System.currentTimeMillis()));
				activityLog.setMessage(EdgeCMWebServiceMessages.getMessage("EDGEMAIL_CheckAccountError",option.getUserName(), esf.getMessage()));
				activityLogService.addLog(activityLog);
				
				String strErrorCode = esf.getFaultInfo().getCode();
				DiscoveryService.getInstance().setDiscoveryError(strErrorCode);
				//fanda03 add
				DiscoveryService.getInstance().onDiscoverFailed(option, esf.getFaultInfo().getMessage() );
				saveAutoDiscoveryResult(option.getId(), DiscoveryStatus.DISCOVERY_STATUS_FAILED.getDiscoveryStatus(), beginTime, new Date(System.currentTimeMillis()), -1);
				TaskMonitor.updateTaskStatus(option.getTaskId(), TaskStatus.Error,  getTaskDetail(option.getId(), DiscoveryStatus.DISCOVERY_STATUS_FAILED));
				continue;
			}
			
			if (Thread.currentThread().isInterrupted()) {
				for (DiscoveryOption opt : options) {
					TaskMonitor.updateTaskStatus(opt.getTaskId(), TaskStatus.Error,  getTaskDetail(option.getId(), DiscoveryStatus.DISCOVERY_STATUS_FAILED));
				}
				return;
			}
			
			try {
				List<JNode> jNodes = discoverSingle(option);	
				
				boolean enable = DiscoveryUtil.getEnableAutoDiscoveryEmailAlert();
				List<JNode> jNodesNewAdded = new LinkedList<JNode>();
				
				int[] isExist = new int[1];
				int[] hostId = new int[1];
				
				for (JNode jNode : jNodes) {
					nodeCount ++;
					setMonitorProcess(nodeCount, DiscoveryPhase.DISCOVERY_PHASE_UPDATE_DATA, option);
					if (Thread.currentThread().isInterrupted()) {
						TaskMonitor.updateTaskStatus(option.getTaskId(), TaskStatus.Error,  getTaskDetail(option.getId(), DiscoveryStatus.DISCOVERY_STATUS_FAILED));
						return;
					}
					
					Node node = convert2Node(jNode);
					
					// TODO: Merge the nodes into database
					/* Bellow codes are test Code Only, need to be replaced */
					isExist[0] = 0; 
					hostId[0] = 0;
					int appStatus = 0;
					
					if (jNode.getServerPrincipalName().indexOf("MSSQLSvc") != -1) {
						appStatus = ApplicationUtil.setSQLInstalled(appStatus);
					}
					
					if (jNode.getServerPrincipalName().indexOf("exchangeMDB") != -1) {
						appStatus = ApplicationUtil.setExchangeInstalled(appStatus);
					}
					
					adDao.as_edge_host_update_For_AD(option.getId(), node.getLastupdated(), 
							node.getHostname(), 
							node.getDomainName(),
							node.getIpaddress()==null?"":node.getIpaddress(), 
							jNode.getOperatingSystem(), 
							appStatus, 
							node.getServerPrincipalName(),isExist, hostId);
					this.gatewayService.bindEntity( option.getGatewayId(), hostId[0], EntityType.Node );
					if(!addednodes.contains(hostId[0])){
						adDao.as_edge_insert_ad_result(hostId[0]);
						addednodes.add(hostId[0]);
					}
					if(isExist[0] == 0) // schedule job
					{
						jNodesNewAdded.add(jNode);
					}
				}
				
				endTime = new Date(System.currentTimeMillis());
				saveAutoDiscoveryResult(option.getId(), DiscoveryStatus.DISCOVERY_STATUS_FINISHED.getDiscoveryStatus(), beginTime, endTime, jNodesNewAdded.size());
				if(option.getJobType() == 1 && enable && jNodesNewAdded.size() >0) // schedule job			
				{
					String content = "";
					String userName = option.getUserName();
					int pos = userName.indexOf('\\');
					String domainName = userName.substring(0, pos).toUpperCase();
									
					EmailTemplateSetting template = DiscoveryUtil.getEmailTemplateSetting();
					if( null == template)  // not configure the email template 
						continue;
					
					String subject = template.getSubject() + ": " + EdgeCMWebServiceMessages.getMessage("EDGEMAIL_AutoDiscovery_NewNodesDiscovered", domainName);;				
					
					if(template.getHtml_flag() == 1)
					{
						content = getHtmlContent(subject, option, beginTime, endTime, jNodesNewAdded);
					}
					else
					{
						content = getPlainTextContent(subject, option, beginTime, endTime, jNodesNewAdded);
					}
					
					if(!content.isEmpty())
					{	
						DiscoveryUtil.sendAutoDiscoveryEmailWithHost(subject, content);
						DiscoveryUtil.sendAutoDiscoveryEmailWithHostToCPM(subject, content);
					}
				}
				TaskMonitor.updateTaskStatus(option.getTaskId(), TaskStatus.OK,  getTaskDetail(option.getId(), DiscoveryStatus.DISCOVERY_STATUS_FINISHED));
				
			} catch(EdgeServiceFault esf) {	
				saveAutoDiscoveryResult(option.getId(), DiscoveryStatus.DISCOVERY_STATUS_FAILED.getDiscoveryStatus(), beginTime, new Date(System.currentTimeMillis()), -1);
				activityLog.setModule(Module.Common);		
				activityLog.setSeverity(Severity.Error);
				activityLog.setTime(new Date(System.currentTimeMillis()));
				activityLog.setMessage(esf.getFaultInfo().getMessage());
				activityLogService.addLog(activityLog);
				
				String strErrorCode = esf.getFaultInfo().getCode();
				DiscoveryService.getInstance().setDiscoveryError(strErrorCode);
				DiscoveryService.getInstance().onDiscoverFailed( option, esf.getFaultInfo().getMessage() );
				TaskMonitor.updateTaskStatus(option.getTaskId(), TaskStatus.Error,  getTaskDetail(option.getId(), DiscoveryStatus.DISCOVERY_STATUS_FAILED));

			} catch (Exception e) {
				saveAutoDiscoveryResult(option.getId(), DiscoveryStatus.DISCOVERY_STATUS_FAILED.getDiscoveryStatus(), beginTime, new Date(System.currentTimeMillis()), -1);
				logger.error("[DsDiscoveryActiveDirectory] DiscoverNodeFromAD failed.",e);
				TaskMonitor.updateTaskStatus(option.getTaskId(), TaskStatus.Error,  getTaskDetail(option.getId(), DiscoveryStatus.DISCOVERY_STATUS_FAILED));
			}
		}
	}
	
	private TaskDetail<DiscoveryHistory> getTaskDetail(int id, DiscoveryStatus status) {
		TaskDetail<DiscoveryHistory> detail = new TaskDetail<DiscoveryHistory>();
		DiscoveryHistory history = new DiscoveryHistory();
		history.setId(id);
		history.setDiscoveryType(SettingType.AD);
		history.setStatus(status);
		detail.setRawData(history);
		return detail;
	}
	
	private void saveAutoDiscoveryResult(int relatedId, int jobStatus, Date startTime, Date endTime, int result) {
		adDao.as_edge_save_ad_discovery_result(relatedId, SettingType.AD.ordinal(), jobStatus, startTime, endTime, result);
	}
	
	private List<JNode> discoverSingle(DiscoveryOption option) throws EdgeServiceFault {
		boolean bSQLFilter = false;
		boolean bExchangeFilter = false;
		
		Set<DiscoveryApplication> appFilter = option.getApplicationFilter();
		if (appFilter != null) {
			bSQLFilter = appFilter.contains(DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_SQL);
			bExchangeFilter = appFilter.contains(DiscoveryApplication.EDGE_DISCOVERY_NODE_APPLICATION_EXCH);
		}
		
		String strOperatingSystem = DiscoveryOperatingSystemMap.getOperatingSystemName(option.getNodeOperatingSystem());
		IRemoteNativeFacadeFactory remoteNativeFacadeFactory = EdgeFactory.getBean(IRemoteNativeFacadeFactory.class);
		IRemoteNativeFacade nativeFacade = remoteNativeFacadeFactory.createRemoteNativeFacade( option.getGatewayId() );
		return nativeFacade.getNodes(option.getTargetComputerName(), option.getUserName(), option.getPassword(), option.getComputerNameFilter(), strOperatingSystem, bSQLFilter, bExchangeFilter);
	}	
	
	public static String getHtmlContent(String subject, DiscoveryOption option, Date beginTime, Date endTime, List<JNode> nodes) throws EdgeServiceFault
	{
		String template = "";
		String templatePre = "";				
		// logger.debug("getHtmlContent - start");

		// HTML format
		StringBuffer htmlTemplate = new StringBuffer();
		StringBuffer htmlTemplate2 = new StringBuffer();
		StringBuffer htmlTemplate3 = new StringBuffer();
		htmlTemplate.append("<HTML>");
		htmlTemplate.append(EmailContentTemplate.getHTMLHeaderSection());
		htmlTemplate.append("	<BODY>");
		htmlTemplate.append("	<h1>%s</h1>");
		htmlTemplate.append("   <p/><p/>");
		htmlTemplate
				.append("	<TABLE border=\"1\" class=\"data_table\" cellspacing=\"0\" cellpadding=\"4\">");
		htmlTemplate
				.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
		htmlTemplate
				.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
		htmlTemplate
				.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
		htmlTemplate
				.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
		htmlTemplate
				.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD><a href=%s>%s</a></TD></TR>");
		htmlTemplate
				.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>%s</TD></TR>");
		htmlTemplate.append("		<TR><TD BGCOLOR=#DDDDDD><B>%s</B></TD><TD>");
		htmlTemplate
				.append("													<TABLE border=\"0\" class=\"data_table\" cellspacing=\"0\" cellpadding=\"4\" style=\"border:0\">");

		String nodeInfo = "";
		for (int i = 0; i < nodes.size(); i++) {
			JNode node = nodes.get(i);
			String sql = node.getBSql() ? (EDGEMAIL_SQLServer + ", ") : "";
			String exc = node.getBExch() ? (EDGEMAIL_Exchange + ", ") : "";
			nodeInfo = node.getNodeName() + "(" + sql + exc
					+ node.getOperatingSystem() + ")";
			htmlTemplate2.append("													<TR><TD>" + nodeInfo
					+ "</TD></TR>");
		}

		htmlTemplate3.append("													</TABLE>");
		htmlTemplate3.append("											 </TD></TR>");
		htmlTemplate3.append("	</TABLE>");

		htmlTemplate3.append("</BODY>");
		htmlTemplate3.append("</HTML>");

		String url = EdgeEmailService.GetInstance().getApplicationUrl();
		templatePre = String.format(htmlTemplate.toString(), subject,
				EDGEMAIL_Account,option.getUserName(), 
				EDGEMAIL_NodeNameFilter, option.getComputerNameFilter(),
				EDGEMAIL_DiscoveryBeginTime, beginTime.toString(), 
				EDGEMAIL_DiscoveryEndTime, endTime.toString(),
				EDGEMAIL_AccessAddress, url, url,
				EDGEMAIL_DiscoveredNodesAmount, Integer.toString(nodes.size()), 
				EDGEMAIL_DiscoveredNodes);

		template = templatePre + htmlTemplate2 + htmlTemplate3;
		
		/*logger.debug(template);
		logger.debug("getHtmlContent - end");*/
		return template;
	}
	
	public static String getPlainTextContent(String subject, DiscoveryOption option, Date beginTime, Date endTime, List<JNode> nodes) throws EdgeServiceFault
	{
		// logger.debug("getPlainTextContent - start");
		String template = "";

		StringBuffer plainTemplate = new StringBuffer();

		String url = EdgeEmailService.GetInstance().getApplicationUrl();
		plainTemplate.append(EDGEMAIL_Account+ ": ");
		plainTemplate.append(option.getUserName());
		plainTemplate.append("   |   ");

		plainTemplate.append(EDGEMAIL_NodeNameFilter+ ": ");
		plainTemplate.append(option.getComputerNameFilter());
		plainTemplate.append("   |   ");

		plainTemplate.append(EDGEMAIL_DiscoveryBeginTime+ ": ");
		plainTemplate.append(beginTime.toString());
		plainTemplate.append("   |   ");

		plainTemplate.append(EDGEMAIL_DiscoveryEndTime+ ": ");
		plainTemplate.append(endTime.toString());
		plainTemplate.append("   |   ");

		plainTemplate.append(EDGEMAIL_AccessAddress+ ": ");
		plainTemplate.append(url);
		plainTemplate.append("   |   ");

		plainTemplate.append(EDGEMAIL_DiscoveredNodesAmount+ ": ");
		plainTemplate.append(Integer.toString(nodes.size()));
		plainTemplate.append("   |   ");

		plainTemplate.append(EDGEMAIL_DiscoveredNodes+ ": " + "\r\n");
	
		for (int i = 0; i < nodes.size(); i++) {
			JNode node = nodes.get(i);
			StringBuilder sb = new StringBuilder(node.getNodeName());
			sb.append("(");
			if (node.getBSql())
				sb.append(EDGEMAIL_SQLServer + ", ");
			if (node.getBExch())
				sb.append(EDGEMAIL_Exchange + ", ");
			sb.append(node.getOperatingSystem());
			sb.append(")");

			plainTemplate.append(sb + "\n\n");

		}

		template = plainTemplate.toString();
	
		/*logger.debug(template);
		logger.debug("getPlainTextContent - end");*/
		return template;
	}
}
