package com.ca.arcflash.webservice;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.xml.ws.WebServiceContext;

import org.apache.log4j.Logger;

import com.ca.arcflash.instantvm.InstantVHDResult;
import com.ca.arcflash.instantvm.InstantVMConfig;
import com.ca.arcflash.instantvm.InstantVMStatus;
import com.ca.arcflash.instantvm.PrecheckCriteria;
import com.ca.arcflash.instantvm.PrecheckResult;
import com.ca.arcflash.webservice.data.IVMJobMonitor;
import com.ca.arcflash.webservice.data.job.rps.IVMJobArg;
import com.ca.arcflash.webservice.service.InstantVMService;
import com.ca.arcflash.webservice.service.ServiceException;
import com.ca.arcflash.webservice.util.CommonServiceUtilImpl;

@WebService(endpointInterface="com.ca.arcflash.webservice.IInstantVMService")
@Path("/AgentService")
public class InstantVMServiceImpl implements IInstantVMService {
	
	private static final Logger logger = Logger.getLogger(FlashServiceImpl.class);
	CommonServiceUtilImpl commonUtil = null;
	private Object newLock = new Object();
	
	@Resource
	private WebServiceContext context;
	@Context HttpServletRequest httpRequest;
	
	private CommonServiceUtilImpl getCommonUtil() {
		if (commonUtil == null) {
			synchronized (newLock) {
				if (commonUtil == null)
					commonUtil = new CommonServiceUtilImpl(context, httpRequest);
			}
		}
		return commonUtil;
	}
	
	@Override
	public String testFun() {
		checkSession();
		return "Hello world!";
	}
	
	@Override
	public int pauseMerge(String afGuid) {
		checkSession();
		
		return InstantVMService.getInstance().pauseMerge(afGuid);
	}

	@Override
	public int resumeMerge(String afGuid) {
		checkSession();
		
		return InstantVMService.getInstance().resumeMerge(afGuid);
	}
	
	@Override
	public String startInstantVM(InstantVMConfig para) {
		checkSession();

		String instantVMUUID = "";
		try {
			instantVMUUID = InstantVMService.getInstance().startInstantVM(para);
		} catch (ServiceException e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault(e.getMessage(), e.getErrorCode());
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
			throw AxisFault.fromAxisFault(t.getMessage(),t);
		}
		
		return instantVMUUID;
	}
	
	@Override
	public long restartInstantVM(String instantVMUUID) {
		checkSession();
		return InstantVMService.getInstance().restartInstantVM(instantVMUUID);
	}
	
	@Override
	public InstantVHDResult startInstantVHD(InstantVMConfig para, int timeout) {
		String jobUUID = startInstantVM(para);
		try {
			return InstantVMService.getInstance().getInstantVHD(jobUUID, timeout);
		} catch (ServiceException e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault(e.getMessage(), e.getErrorCode());
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
			throw AxisFault.fromAxisFault(t.getMessage(),t);
		}
	}
	
	@Override
	public int validateUserByUUID(String uuid) {
		return getCommonUtil().validateUserByUUID(uuid);
	}
	
	@Override
	public long startInstantVMFromRPS(IVMJobArg arg) {
		return InstantVMService.getInstance().Start(arg.getInstantVMConfig(), 
				InstantVMService.INSTANT_VM_CONFIG_PATH + arg.getInstantVMConfig().getIVMJobUUID() + InstantVMService.INSTANT_VM_JOB_FILENAME_EXTENSION);
	}
	
	@Override
	public long stopInstantVM(String instantVMUUID, boolean delete) {
		checkSession();
		
		long ret = 1;
		try {
			ret = InstantVMService.getInstance().stopInstantVM(instantVMUUID, delete);
		} catch (ServiceException e) {
			logger.error(e.getMessage(), e);
			throw AxisFault.fromAxisFault(e.getMessage(), e.getErrorCode());
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
			throw AxisFault.fromAxisFault(t.getMessage(),t);
		}
		
		return ret;
	}
	
	@Override
	public long startHydration(String instantVMUUID) {
		checkSession();
		long ret = 1;
		try {
			ret  = InstantVMService.getInstance().startHydration(instantVMUUID);
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
			throw AxisFault.fromAxisFault(t.getMessage(),t);
		}
		return ret;
	}
	
	@Override
	public long stopHydration(String instantVMUUID) {
		checkSession();
		long ret = 1;
		try {
			ret  = InstantVMService.getInstance().stopHydration(instantVMUUID);
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
			throw AxisFault.fromAxisFault(t.getMessage(),t);
		}
		return ret;
	}
	
	@Override
	public IVMJobMonitor queryInstantVM(String instantVMJobUUID) {
		checkSession();
		return InstantVMService.getInstance().getIVMJobMonitor(instantVMJobUUID);
	}
	
	@Override
	public String validateUser(String username, String password, String domain) {
		return getCommonUtil().validateUser(username, password, domain);
	}
	
	@Override
	public InstantVMStatus GetIVMStatus(String instantVMJobUUID) {
		checkSession();
		return InstantVMService.getInstance().GetIVMStatus(instantVMJobUUID);
	}
	
	@Override
	public long PowerOnIvm(String instantVMJobUUID, String ivmUUID) {
		checkSession();
		return InstantVMService.getInstance().PowerOnIvm(instantVMJobUUID, ivmUUID);
	}
	
	@Override
	public long PowerOffIvm(String instantVMJobUUID, String ivmUUID) {
		checkSession();
		return InstantVMService.getInstance().PowerOffIvm(instantVMJobUUID, ivmUUID);
	}
	
	protected void checkSession() {
		getCommonUtil().checkSession();
	}

	public PrecheckResult checkPrerequisites(InstantVMConfig para, PrecheckCriteria criteria) {
		checkSession();
		return InstantVMService.getInstance().checkPrerequisites(para, criteria);
	}
}
