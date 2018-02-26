package com.ca.arcflash.ui.server;

import java.util.Arrays;
import java.util.Comparator;

import javax.xml.bind.JAXBException;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.soap.SOAPFaultException;

import org.apache.log4j.Logger;

import com.ca.arcflash.common.CommonUtil;
import com.ca.arcflash.common.StringUtil;
import com.ca.arcflash.ha.model.ARCFlashNode;
import com.ca.arcflash.ha.model.ARCFlashNodesSummary;
import com.ca.arcflash.ha.model.SummaryModel;
import com.ca.arcflash.ha.model.VMSnapshotsInfo;
import com.ca.arcflash.ha.modelWebService.MonitorWebServiceErrorCode;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.exception.ServiceConnectException;
import com.ca.arcflash.ui.client.exception.ServiceInternalException;
import com.ca.arcflash.ui.client.monitor.MonitorService;
import com.ca.arcflash.webservice.ID2DCSFlashService;

/**
 * This class realizes some functions which are specific as a VCM.
 *
 */
public class MonitorServiceImpl extends BaseServiceImpl implements MonitorService {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(MonitorServiceImpl.class);
	
	private static final long serialVersionUID = -6740518521215872790L;
	
	
	@Override
	public ARCFlashNodesSummary queryFlashNodesSummary() throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		logger.debug("queryHeartBeatModel() - start");

		try {
			 ARCFlashNodesSummary result = getD2DCSService().getARCFlashNodesSummary();
			 
			 for(ARCFlashNode node : result.getNodes()) {
				 if(node.isMonitor()) {
					 node.setHostport(getServiceClient().getPort()+"");
					 node.setHostProtocol(getServiceClient().getProtocol());
					 break;
				 }
			 }
			 
			 Arrays.sort(result.getNodes(), new Comparator<ARCFlashNode>() {
					@Override
					public int compare(ARCFlashNode o1, ARCFlashNode o2) {
						return o1.getHostname().compareTo(o2.getHostname());
					}
			 });
			 
			logger.debug("queryHeartBeatModel() - end");
			return result;
		} catch (WebServiceException e) {
			if(e instanceof SOAPFaultException){
				SOAPFaultException se = (SOAPFaultException)e;
				if (se.getFault()!=null && MonitorWebServiceErrorCode.Common_NULL_HeartBeat.equals(se.getFault().getFaultCodeAsQName().getLocalPart()) )
					return null;
			}
			logger.error("queryFlashNodesSummary(String, int, String, String)", e);
			proccessAxisFaultException(e);
		} catch(Exception e){
			logger.error("queryFlashNodesSummary(String, int, String, String)",e);
		}
		
		return null;
	}

	@Override
	public FailoverJobScript getFailoverJobScript(String uuid) throws BusinessLogicException, ServiceConnectException, ServiceInternalException{
		logger.debug("getFailoverJobScript(String) - start"); //$NON-NLS-1$

		String jobScriptString;
		try {
			jobScriptString = getD2DCSService().getFailoverJobScriptOfProductionServer(uuid);
			FailoverJobScript jobScript = CommonUtil.unmarshal(jobScriptString, FailoverJobScript.class);

			logger.debug("getFailoverJobScript(String) - end"); //$NON-NLS-1$
			return jobScript;
		} catch (WebServiceException e) {
			logger.error("getFailoverJobScript(String)", e); //$NON-NLS-1$
			proccessAxisFaultException(e);
		} catch (JAXBException e) {
			logger.error("getFailoverJobScript(String)", e); //$NON-NLS-1$
			throw new BusinessLogicException();
		}
		
		return null;
	}
	
	@Override
	public VMSnapshotsInfo[] getSnapshots(String uuid) throws BusinessLogicException, ServiceConnectException, ServiceInternalException{
		logger.debug("getSnapshots(String) - start"); //$NON-NLS-1$

		try {
			VMSnapshotsInfo[] snapShots = getD2DCSService().getVMSnapshots(uuid,"","");
			
			if(snapShots == null || snapShots.length==0)
				return null;
			
			logger.debug("getSnapshots(String) - end"); //$NON-NLS-1$
			return snapShots;
		} catch (Exception e) {
			logger.error("getSnapshots()", e);
			throw new BusinessLogicException();
		}
	}
	
	@Override
	public void startFailover(String uuid, VMSnapshotsInfo vmSnapInfo) throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		logger.debug("startFailover(String, VMSnapshotsInfo) - start"); //$NON-NLS-1$

		try {
			getD2DCSService().startFailover(uuid, vmSnapInfo);
			
			logger.debug("startFailover(String) - end"); //$NON-NLS-1$
		} catch (Exception e) {
			logger.error("getSnapshots()", e);
			throw new BusinessLogicException();
		}

		logger.debug("startFailover(String, VMSnapshotsInfo) - end"); //$NON-NLS-1$
	}
	
	@Override
	public boolean isFailoverJobFinish(String afGuid)
			throws BusinessLogicException, ServiceConnectException, ServiceInternalException {
		logger.debug("isFailoverJobFinish(String) - start"); 
		try {
			final boolean failoverJobFinish = getD2DCSService().isFailoverJobFinish(afGuid);
			logger.debug("isFailoverJobFinish(String) - end " + failoverJobFinish);
			return failoverJobFinish;
		} catch(WebServiceException e) {
			logger.error("isFailoverJobFinish(String)", e); 
			proccessAxisFaultException(e);
			return false;
		}
	}
	
	protected ID2DCSFlashService getD2DCSService() {
//		return getLocalWebServiceClient().getD2DCSService();
		return getServiceClient().getServiceV2();
	}

	@Override
	public SummaryModel getSummaryModel(String afguid, String vmuuid,
			String vmname) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		logger.debug("getSummaryModel(String, String, String) - start"); 
		try {
			SummaryModel model = getD2DCSService().getSummaryModel(afguid, vmuuid, vmname);
			
			if(logger.isDebugEnabled()) {
				logger.debug("model:" + StringUtil.convertObject2String(model));
				logger.debug("getSummaryModel(String, String, String) - end"); //$NON-NLS-1$
			}
			return model;
		} catch (Exception e) {
			logger.error("getSummaryModel(String, String, String)", e);
			throw new BusinessLogicException();
		}
	}

	@Override
	public String getCurrentRunningSnapShotGuid(String afGuid) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {

		logger.debug("getCurrentRunningSnapShotGuid(String, VMSnapshotsInfo) - start"); 
		try {
			String snapshotUid = getD2DCSService().getCurrentRunningSnapShotGuid(afGuid);
			
			if(logger.isDebugEnabled()) {
				logger.debug("snapshotUid:" + snapshotUid);
				logger.debug("getCurrentRunningSnapShotGuid(String, VMSnapshotsInfo) - end"); 
			}
			return snapshotUid;
		} catch (Exception e) {
			logger.error("getCurrentRunningSnapShotGuid(String, VMSnapshotsInfo) - end", e);
			throw new BusinessLogicException();
		}
	
	}
	
	@Override
	public int shutDownVM(String afGuid) throws BusinessLogicException, ServiceConnectException,	ServiceInternalException {
		logger.debug("shutDownVM() - start"); 
		try {
			int ret = getD2DCSService().shutdownVM(afGuid);
			
			if(logger.isDebugEnabled()) {
				logger.debug("return:" + ret);
			}
			else if(ret > 0)
				logger.error("return:" + ret);
			
			return ret;
		} catch (Exception e) {
			logger.error("shutDownVM()", e);
			throw new BusinessLogicException();
		}
	}

	@Override
	public void removeMonitee(String afGuid) throws BusinessLogicException,
			ServiceConnectException, ServiceInternalException {
		logger.info("removeMonitee() - start; afGuid:" + afGuid); 
		System.out.println("removeMonitee() - start; afGuid:" + afGuid);
		
		try {
			getD2DCSService().removeMonitee(afGuid);
		}
		catch(WebServiceException exception) {
			logger.error("proccessAxisFaultException exception", exception);
			proccessAxisFaultException(exception);
		}
		
		logger.info("removeMonitee() - end"); 
	}

}
