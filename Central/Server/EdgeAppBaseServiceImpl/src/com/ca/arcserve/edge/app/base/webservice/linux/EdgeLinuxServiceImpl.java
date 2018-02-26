package com.ca.arcserve.edge.app.base.webservice.linux;

import java.util.List;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.IDirectWebServiceImpl;
import com.arcserve.edge.common.annotation.NonSecured;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceErrorCode;
import com.ca.arcserve.edge.app.base.serviceexception.EdgeServiceFault;
import com.ca.arcserve.edge.app.base.util.CommonUtil;
import com.ca.arcserve.edge.app.base.webservice.EdgeWebServiceProxyFactory;
import com.ca.arcserve.edge.app.base.webservice.IServiceSecure;
import com.ca.arcserve.edge.app.base.webservice.jni.NativeFacade;
import com.ca.arcserve.edge.app.base.webservice.jni.NativeFacadeImpl;
import com.ca.arcserve.linuximaging.webservice.data.ExportJobResult;
import com.ca.arcserve.linuximaging.webservice.data.NodeConnectionInfo;
import com.ca.arcserve.linuximaging.webservice.data.TargetMachineInfo;
import com.ca.arcserve.linuximaging.webservice.data.backup.BackupConfiguration;
import com.ca.arcserve.linuximaging.webservice.data.license.LicenseResult;
import com.ca.arcserve.linuximaging.webservice.data.license.LicensedMachine;
import com.ca.arcserve.linuximaging.webservice.data.sync.SyncActivityLog;
import com.ca.arcserve.linuximaging.webservice.data.sync.SyncData;
import com.ca.arcserve.linuximaging.webservice.data.sync.SyncDataResult;
import com.ca.arcserve.linuximaging.webservice.data.sync.SyncJobHistory;
import com.ca.arcserve.linuximaging.webservice.data.sync.SyncJobMonitor;
import com.ca.arcserve.linuximaging.webservice.edge.IEdgeService4LinuxD2D;
@WebService(endpointInterface="com.ca.arcserve.linuximaging.webservice.edge.IEdgeService4LinuxD2D")
public class EdgeLinuxServiceImpl implements IEdgeService4LinuxD2D, IDirectWebServiceImpl, IServiceSecure{
	private static Logger logger = Logger.getLogger(EdgeLinuxServiceImpl.class);
	private IEdgeService4LinuxD2D service = EdgeWebServiceProxyFactory.createProxy4LinuxD2D(new EdgeService4LinuxD2DImpl(), IEdgeService4LinuxD2D.class, this);
	private volatile HttpSession session = null;
	private boolean ignoreSessionCheck;
	@Resource
	private WebServiceContext wsContext;
	public EdgeLinuxServiceImpl() {
	}
	
	public void setService(IEdgeService4LinuxD2D service) {
		this.service = service;
	}

	public EdgeLinuxServiceImpl(boolean ignoreSessionCheck) {
		this.ignoreSessionCheck = ignoreSessionCheck;
	}
	@Override
	@NonSecured
	public LicenseResult checkCentralLicense(String authKey, LicensedMachine node) {
		return service.checkCentralLicense(authKey, node);
	}

	@Override
	@NonSecured
	public int validateLinuxD2DByUUID(String uuid) {
		int result = service.validateLinuxD2DByUUID(uuid);
		setUuid(uuid);
		return result;
	}

	@Override
	@NonSecured
	public String validateLinuxD2DByUser(String username, String password) {
		String domainName = getDomainName(username);
		NativeFacade nativeCode = new NativeFacadeImpl();
		try {
			nativeCode.validateUser(username, password, domainName);
		} catch (EdgeServiceFault e) {
			logger.error("validate user failed", e);
			return null;
		}
		HttpSession session = getSession();
		if (session != null) {
			session.setAttribute(CommonUtil.STRING_SESSION_USERNAME, username);
			session.setAttribute(CommonUtil.STRING_SESSION_PASSWORD, password);
			session.setAttribute(CommonUtil.STRING_SESSION_DOMAIN, domainName);
		}
		String uuid = CommonUtil.retrieveCurrentAppUUID();
		setUuid(uuid);
		return uuid;
	}

	@Override
	public int synchronizeActivityLog(List<SyncActivityLog> list) {
		return service.synchronizeActivityLog(list);
	}

	@Override
	public int synchronizeJobHistory(List<SyncJobHistory> list) {
		return service.synchronizeJobHistory(list);
	}

	@Override
	public int synchronizeJobMonitor(List<SyncJobMonitor> list) {
		return service.synchronizeJobMonitor(list);
	}

	@Override
	public int getPlanStatus(int planId, String planUUID, String nodeName) {
		return service.getPlanStatus(planId, planUUID, nodeName);
	}

	@Override
	public SyncDataResult synchronizeData(SyncData syncData) {
		return service.synchronizeData(syncData);
	}

	@Override
	public ExportJobResult importLinuxJobs(String backupServer, List<BackupConfiguration> jobList) {
		return service.importLinuxJobs(backupServer, jobList);
	}

	@Override
	public NodeConnectionInfo registNodeToUDP(TargetMachineInfo targetNode, boolean isForce) {
		return service.registNodeToUDP(targetNode, isForce);
	}

	@Override
	public List<TargetMachineInfo> getNodeList() {
		return service.getNodeList();
	}

	@Override
	public NodeConnectionInfo modifyNodeInUDP(TargetMachineInfo targetNode, boolean isForce) {
		return service.modifyNodeInUDP(targetNode, isForce);
	}

	@Override
	public HttpSession getSession() {
		if (session != null)
			return session;
		else if (wsContext != null) {
			Object requestProperty = wsContext.getMessageContext().get(
					MessageContext.SERVLET_REQUEST);
			if (requestProperty != null
					&& requestProperty instanceof HttpServletRequest) {
				HttpServletRequest request = (HttpServletRequest) requestProperty;
				return request.getSession(true);
			}
			return null;
		}
		return null;
	}

	@Override
	public void setSession(HttpSession session) {
		this.session = session;
	}

	@Override
	public void checkSession() throws EdgeServiceFault {
		if (ignoreSessionCheck) return;
		
		HttpSession session = this.getSession();
		String username = (String) session.getAttribute( CommonUtil.STRING_SESSION_USERNAME );
		String uuid = (String) session.getAttribute( CommonUtil.STRING_SESSION_UUID );
		if ((session == null) || ((username == null) && (uuid == null)))
		{
			throw EdgeService4LinuxD2DUtil.generateSOAPFaultException(EdgeServiceErrorCode.Common_Service_NOT_LOGIN, "Not login");
		}
	}
	
	protected String getUuid() {
		HttpSession session = getSession();
		if (session == null) {
			return "";
		}
		
		Object uuidObject = session.getAttribute(CommonUtil.STRING_SESSION_UUID);
		if (!(uuidObject instanceof String)) {
			return "";
		}
		
		return (String) uuidObject;
	}
	
	protected void setUuid(String uuid) {
		HttpSession session = getSession();
		if (session != null) {
			session.setAttribute(CommonUtil.STRING_SESSION_UUID, uuid);
		}
	}
	
	private String getDomainName (String textValue) {
		String domainName = "";
		if (textValue == null || textValue.isEmpty()) {
			return domainName;
		}
		int pos = textValue.indexOf("\\"); 
		if (pos == -1) {

		} else {
			domainName = textValue.substring(0, pos);
		}
		return domainName;
	}

	@Override
	public void redeployLinuxPlan(String planName,List<String> nodeList) {
		service.redeployLinuxPlan(planName,nodeList);
	}

	@Override
	public void redeployFailedLinuxPlan(int planId, List<String> nodeList) {
		service.redeployFailedLinuxPlan(planId, nodeList);	
	}
}
