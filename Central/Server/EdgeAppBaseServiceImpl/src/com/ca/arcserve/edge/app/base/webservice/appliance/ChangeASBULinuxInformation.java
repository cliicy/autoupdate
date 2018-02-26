package com.ca.arcserve.edge.app.base.webservice.appliance;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcserve.edge.app.base.appdaos.EdgeConnectInfo;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.schedulers.EdgeExecutors;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.webservice.IEdgeLinuxNodeService;
import com.ca.arcserve.edge.app.base.webservice.contract.arcserve.Protocol;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ApplianceUtils;
import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeSortOrder;
import com.ca.arcserve.edge.app.base.webservice.contract.common.StringUtil;
import com.ca.arcserve.edge.app.base.webservice.contract.node.EdgeNodeFilter;
import com.ca.arcserve.edge.app.base.webservice.contract.node.Node;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodePagingConfig;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodePagingResult;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeRegistrationInfo;
import com.ca.arcserve.edge.app.base.webservice.contract.node.NodeSortCol;
import com.ca.arcserve.edge.app.base.webservice.contract.node.ProtectionType;
import com.ca.arcserve.edge.app.base.webservice.jni.NativeFacade;
import com.ca.arcserve.edge.app.base.webservice.jni.NativeFacadeImpl;
import com.ca.arcserve.edge.app.base.webservice.node.LinuxNodeServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.node.NodeServiceImpl;
import com.ca.arcserve.linuximaging.webservice.ILinuximagingService;
import com.ca.arcserve.linuximaging.webservice.client.BaseWebServiceClientProxy;

public class ChangeASBULinuxInformation implements Runnable {
	private String hostName;
	private String userName;
	private String password;
	private Logger logger = Logger.getLogger(ChangeASBULinuxInformation.class);
	private static IEdgeHostMgrDao hostMgrDao = DaoFactory.getDao(IEdgeHostMgrDao.class);
	private IEdgeConnectInfoDao connectDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);

	private int updateDomainRet = -1;
	private int updatePrimaryRet = -1;
	
	private static final long CONNECT_LINUX_WS_TIMEOUT = 30*60*1000; // 30 minutes
	private static final long CONNECT_LINUX_WS_INTERVAL = 1*60*1000; // 1 minute
	
	public ChangeASBULinuxInformation(String hostName, String userName,
			String password) {
		this.hostName = hostName;
		this.userName = userName;
		this.password = password;
	}

	@Override
	public void run() {
//		int retryCount = 3;
//		while (retryCount>0) {
//			if(updateDomainRet != 0){
//				logger.info("Try to update ASBU domain name: try times: "+retryCount);
//				updateASBUDomainName(hostName, userName, password);
//			}
//			if(updatePrimaryRet != 0){
//				logger.info("Try to update ASBU Primary Server name: try times: "+retryCount);
//				updateASBUPrimaryServerName(hostName, userName, password);
//			}
//			if(updateDomainRet==0 && updatePrimaryRet==0){
//				break;
//			}
//			retryCount -= 1;
//		}
//		automaticASBUService();
//		startASBUService();
		updateLinuxServer(hostName);
	}
	// If you want check the return code, please refer to:
	// https://support.ca.com/cadocs/0/CA%20ARCserve%20Backup%20r16%205-ENU/Bookshelf_Files/HTML/cmndline/index.htm?toc.htm?cl_arcservecfg_exe_trouble.htm?zoom_highlightsub=ARCserveCfg.exe%2Bcmd
	// https://support.ca.com/cadocs/0/CA%20ARCserve%20Backup%20r16%205-ENU/Bookshelf_Files/HTML/cmndline/index.htm?toc.htm?cl_arcservecfg_exe_trouble.htm?zoom_highlightsub=ARCserveCfg.exe%2Bcmd
	private void updateASBUDomainName(String hostName, String userName,
			String password) {
		// Change domain name username password
		logger.info("[updateASBUDomainName]: Begin to update the ASBU domain name.");
		String arcserveBackupPath = "";
		try {
			arcserveBackupPath = ApplianceUtils.getArcserveBackupInstallPath();
			if (!arcserveBackupPath.endsWith("\\")) {
				arcserveBackupPath = arcserveBackupPath + "\\";
			}
			logger.info("[updateASBUDomainName]: Command path:" + arcserveBackupPath);
			String asbuPwd = ApplianceUtils.getASBUPassword();
			if (StringUtil.isEmptyOrNull(asbuPwd)) {
				logger.info("[updateASBUDomainName]: caroot password is invalid.");
				return;
			}
			NativeFacade nativeFacade = new NativeFacadeImpl();
			updateDomainRet = nativeFacade.updateASBUDomainName(arcserveBackupPath, hostName, userName, password, asbuPwd);
			logger.info("[updateASBUDomainName]: return code:" + updateDomainRet);
		} catch (Exception e) {
			logger.error(
					"[updateASBUDomainName]: Update ASBU domain name failed.",
					e);
		}
		logger.info("[updateASBUDomainName]:End updating the ASBU domain name.");
	}
	
	private void updateASBUPrimaryServerName(String hostName, String userName,
			String password) {
		logger.info("[updateASBUPrimaryServerName]: Begin to update ASBU primary server name.");
		String arcserveBackupPath = "";
		try {
			arcserveBackupPath = ApplianceUtils.getArcserveBackupInstallPath();
			if (!arcserveBackupPath.endsWith("\\")) {
				arcserveBackupPath = arcserveBackupPath + "\\";
			}
			logger.info("[updateASBUPrimaryServerName]: Command path:" + arcserveBackupPath);
			String asbuPwd = ApplianceUtils.getASBUPassword();
			if (StringUtil.isEmptyOrNull(asbuPwd)) {
				logger.info("[updateASBUPrimaryServerName]: caroot password is invalid.");
				return;
			}
			NativeFacade nativeFacade = new NativeFacadeImpl();
			updatePrimaryRet = nativeFacade.updateASBUPrimaryServerName(arcserveBackupPath,userName, password, asbuPwd);
			logger.info("[updateASBUPrimaryServerName]: return code:" + updatePrimaryRet);
		} catch (Exception e) {
			logger.error(
					"[updateASBUPrimaryServerName]: Update the ASBU primary server name failed.",
					e);
		}
		logger.info("[updateASBUPrimaryServerName]:End updating ASBU primary server name.");
	}

	private void automaticASBUService() {
		// Change Arcserve Backup service to automatic
		logger.info("[automaticASBUService]: Begin to change Arcserve Backup Service to automatic.");
		try {
			List<String> automaticServiceCommands = new ArrayList<String>();
			automaticServiceCommands.add("sc config PatchManagerService start= auto");
			automaticServiceCommands
					.add("sc config CA\" \"ARCserve\" \"Communication\" \"Foundation start= auto");
			automaticServiceCommands.add("sc config CASDBEngine start= auto");
			automaticServiceCommands.add("sc config CASDiscovery start= auto");
			automaticServiceCommands
					.add("sc config CASUnivDomainSvr start= auto");
			automaticServiceCommands.add("sc config CASJobEngine start= auto");
			automaticServiceCommands.add("sc config CASMgmtSvc start= auto");
			automaticServiceCommands
					.add("sc config CASMessageEngine start= auto");
			automaticServiceCommands.add("sc config CASportmapper start= auto");
			automaticServiceCommands
					.add("sc config CASSvcControlSvr start= auto");
			automaticServiceCommands.add("sc config CASTapeEngine start= auto");
			automaticServiceCommands
					.add("sc config CASUniversalAgent start= auto");
			for (String command : automaticServiceCommands) {
				logger.info("[automaticASBUService]: Launch the command:"
						+ command);
				Runtime.getRuntime().exec(command, null, null);
			}
		} catch (Exception e) {
			logger.error(
					"[automaticASBUService]: Change ACSERVE BACKUP service to automatic failed.",
					e);
		}
		logger.info("[automaticASBUService]: End changing Arcserve Backup Service to automatic.");
	}

	private void startASBUService() {
		// Start Arcserve Backup service
		logger.info("[automaticASBUService]: Begin to start related Arcserve Backup Service.");
		try {
			String arcserveBackupPath = ApplianceUtils
					.getArcserveBackupInstallPath();
			if (!arcserveBackupPath.endsWith("\\")) {
				arcserveBackupPath = arcserveBackupPath + "\\";
			}
			String batCommand = arcserveBackupPath + "cstart.bat";
			batCommand.replaceAll(" ", "\" \"");
			logger.info("[automaticASBUService]: launch the command:"
					+ batCommand);
			Process p = Runtime.getRuntime().exec(batCommand);
			ReadStream s1 = new ReadStream("[automaticASBUService]-stdin",
					p.getInputStream());
			ReadStream s2 = new ReadStream("[automaticASBUService]-stderr",
					p.getErrorStream());
			EdgeExecutors.getCachedPool().execute(s1);
			EdgeExecutors.getCachedPool().execute(s2);
			p.waitFor();
		} catch (Exception e) {
			logger.error(
					"[automaticASBUService]: Start Arcserve backup service failed.",
					e);
		}
		logger.info("[automaticASBUService]: End starting related Arcserve Backup Service.");
	}
	
	private void updateLinuxServer(String hostName) {
		logger.info("[updateLinuxServer]: Begin to update the linuxServer.");
		try {
			
			String oldlinuxServerName = ApplianceUtils.getLinuxServerName();
			if (StringUtil.isEmptyOrNull(oldlinuxServerName)) {
				logger.info("[updateLinuxServer]: Original linuxServer name: "
						+ oldlinuxServerName + " is invalid.");
				return;
			}
			
			Node linuxServerNode = getLinuxServerNode(oldlinuxServerName);
			if(linuxServerNode == null){
				logger.info("[updateLinuxServer]: Can't find the linux server node: "
						+ oldlinuxServerName + ".");
				return;
			}
			
			hostMgrDao.as_edge_host_update_hostInfo(linuxServerNode.getId(), hostName, null);
			linuxServerNode.setHostname(hostName);
			logger.info("[updateLinuxServer]: updated linuxServer name, original hostname is: "
					+ oldlinuxServerName
					+ ", the new hostname is "
					+ hostName);
			
			boolean connectWsSuccess = connectLinuxServerWebService(linuxServerNode);
			
			if(connectWsSuccess){
				updateLinuxServerWithTimeout(linuxServerNode);
			}
		} catch (Exception e) {
			logger.error(
					"[updateLinuxServer]: Change linux server name failed.",
					e);
		}
		logger.info("[updateLinuxServer]: End update the linuxServer.");
	}

	private Node getLinuxServerNode(String oldLinuxServerName) throws Exception{
		NodeServiceImpl nodeService = new NodeServiceImpl();
	
		EdgeNodeFilter nodeFilter = new EdgeNodeFilter();
		NodePagingConfig config = new NodePagingConfig();
		config.setOrderCol(NodeSortCol.hostname);
		config.setOrderType(EdgeSortOrder.ASC);
		config.setPagesize(Integer.MAX_VALUE);
		config.setStartpos(0);
		NodePagingResult nodePagingResult = nodeService
				.getNodesESXByGroupAndTypePaging(0, -31, nodeFilter, config);
		List<Node> nodes = nodePagingResult.getData();
		for (Node node : nodes) {
			if (oldLinuxServerName.equalsIgnoreCase(node.getHostname())
					&& ProtectionType.LINUX_D2D_SERVER.getValue() == node
							.getProtectionTypeBitmap()) {
				return node;
			}
		}
		return null;
	}
	
	private boolean connectLinuxServerWebService(Node node){
		logger.info("[connectLinuxServerWebService] Will try to connect linux server webservice until connected. the the maximum attampt time is 30 minute.");
		long retryCount = CONNECT_LINUX_WS_TIMEOUT / CONNECT_LINUX_WS_INTERVAL;
		while (retryCount > 0) {
			//connect
			try {
				String protocol = node.getD2dProtocol() == Protocol.Https.ordinal()?"https":"http";
				BaseWebServiceClientProxy webService = CommonUtil.getLinuxD2DForEdgeService(protocol,node.getHostname(),Integer.valueOf(node.getD2dPort()));
				ILinuximagingService service = (ILinuximagingService)webService.getService();
				if(service != null){
					List<EdgeConnectInfo> connInfoLst = new ArrayList<EdgeConnectInfo>();
					connectDao.as_edge_connect_info_list(node.getId(), connInfoLst);
					service.validateByKey(connInfoLst.get(0).getAuthUuid());
					logger.info("[connectLinuxServerWebService] Connect linux server webservice succeed.");
					return true;
				}
			} catch (Exception e) {
				logger.debug("[connectLinuxServerWebService] Can not connect linux server webservice.",e);
				logger.error("[connectLinuxServerWebService] Can not connect linux server webservice. the error is "+e.getMessage());
			}
			try {
				Thread.sleep(CONNECT_LINUX_WS_INTERVAL);
			} catch (InterruptedException e) {
				logger.error("[connectLinuxServerWebService] thread was interrupted. so exist.");
				break;
			}
			retryCount -= 1;
		}
		logger.info("[connectLinuxServerWebService] Connect linux server webservice timeout.");
		return false;
	}
	
	private boolean updateLinuxServerWithTimeout(Node linuxServerNode){
		logger.info("[updateLinuxServer]: Application Will try to update linux server webservice until update succeed. the the maximum attampt time is 30 minute.");
		long retryCount = CONNECT_LINUX_WS_TIMEOUT / CONNECT_LINUX_WS_INTERVAL;
		while (retryCount > 0) {
			//connect
			try {
				logger.info("[updateLinuxServerWithTimeout]: Begin to update linux server for "+retryCount+" times.");
				IEdgeLinuxNodeService linuxService = new LinuxNodeServiceImpl();
				NodeRegistrationInfo registreationNode = new NodeRegistrationInfo();
				registreationNode.setNodeName(hostName);
				registreationNode.setNodeDescription(linuxServerNode.getNodeDescription());
				registreationNode.setUsername(linuxServerNode.getUsername());
				List<EdgeConnectInfo> connInfoLst = new ArrayList<EdgeConnectInfo>();
				connectDao.as_edge_connect_info_list(linuxServerNode.getId(), connInfoLst);
				if(connInfoLst.isEmpty()){
					registreationNode.setPassword(linuxServerNode.getPassword());
				}else {
					registreationNode.setPassword(connInfoLst.get(0).getPassword());
				}
				registreationNode.setD2dProtocol(linuxServerNode.getD2dProtocol()==Protocol.Https.ordinal() ? Protocol.Https : Protocol.Http);
				registreationNode.setD2dPort(Integer.valueOf(linuxServerNode.getD2dPort()));
				registreationNode.setId(linuxServerNode.getId());
				linuxService.registerLinuxD2DServer(registreationNode, true, true);
				logger.info("[updateLinuxServerWithTimeout]: Update linux server succeed.");
				return true;
			} catch (Exception e) {
				logger.debug("[updateLinuxServerWithTimeout] Can not connect linux server webservice.",e);
				logger.error("[updateLinuxServerWithTimeout] Update linux server failed, the error is: "+e.getMessage());
			}
			try {
				Thread.sleep(CONNECT_LINUX_WS_INTERVAL);
			} catch (InterruptedException e) {
				logger.error("[updateLinuxServerWithTimeout] thread was interrupted. so exist.");
				break;
			}
			retryCount -= 1;
		}
		logger.info("[updateLinuxServerWithTimeout]: Application update linux server timeout.");
		return false;
	}
}
