package com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.deploytaskrunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.ca.arcflash.webservice.edge.data.policy.PolicyDeploymentError;
import com.ca.arcserve.edge.app.base.appdaos.EdgeHostPolicyMap;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeConnectInfoDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeEsxDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.common.ExternalLinkManager;
import com.ca.arcserve.edge.app.base.common.IEdgeExternalLinks;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.LogUtility;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.PolicyDeployUIWarningWriter;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.PolicyDeploymentLogWriter;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.PolicyDeploymentTask;
import com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment.interfaces.IDeployTaskRunner;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployReasons;
import com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.PolicyDeployStatus;
import com.ca.arcserve.edge.app.base.webservice.policymanagement.PolicyManagementServiceImpl;

public abstract class AbstractDeployTaskRunner implements IDeployTaskRunner {
	
	@SuppressWarnings("serial")
	protected static class SetDeployStatusException extends Exception {}
	
	//////////////////////////////////////////////////////////////////////////
	
	private static final Logger DEFAULT_LOGGER = Logger.getLogger(AbstractDeployTaskRunner.class);
	
	protected PolicyManagementServiceImpl policyManagementServiceImpl;
	protected PolicyDeploymentLogWriter activityLogWriter;
	protected IEdgePolicyDao edgePolicyDao;
	protected IEdgeConnectInfoDao connectInfoDao;
	protected IEdgeEsxDao esxDao;
	protected IEdgeHostMgrDao edgeHostMgrDao;
	protected LogUtility logUtility;
	protected PolicyDeployUIWarningWriter warningErrorMessageWriter;
	protected IEdgeExternalLinks edgeExternalLinks;
	
	public AbstractDeployTaskRunner() {
		this.policyManagementServiceImpl = PolicyManagementServiceImpl.getInstance();
		this.activityLogWriter = PolicyDeploymentLogWriter.getInstance();
		this.edgePolicyDao = DaoFactory.getDao(IEdgePolicyDao.class);
		this.esxDao = DaoFactory.getDao(IEdgeEsxDao.class);
		this.edgeHostMgrDao = DaoFactory.getDao( IEdgeHostMgrDao.class );
		this.connectInfoDao = DaoFactory.getDao(IEdgeConnectInfoDao.class);
		this.logUtility = new LogUtility(DEFAULT_LOGGER);
		this.warningErrorMessageWriter = new PolicyDeployUIWarningWriter();
		this.edgeExternalLinks = ExternalLinkManager.getInstance().getLinks(IEdgeExternalLinks.class);
	}
	
	public void setPolicyManagementServiceImpl(PolicyManagementServiceImpl impl) {
		this.policyManagementServiceImpl = impl;
	}

	public void setPolicyDeploymentLogWriter(PolicyDeploymentLogWriter writer) {
		this.activityLogWriter = writer;
	}
	
	public void setEdgePolicyDao(IEdgePolicyDao edgePolicyDao) {
		this.edgePolicyDao = edgePolicyDao;
	}

	public void setEsxDao(IEdgeEsxDao dao) {
		this.esxDao = dao;
	}

	public void setLogUtility(LogUtility utility) {
		this.logUtility = utility;
	}

	public void setPolicyDeployUIWarningWriter(PolicyDeployUIWarningWriter warningErrorMessageWriter) {
		this.warningErrorMessageWriter = warningErrorMessageWriter;
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	protected abstract void deployPolicy(PolicyDeploymentTask task);
	protected abstract void removePolicy(PolicyDeploymentTask task);

	@Override
	public void doTask(PolicyDeploymentTask task) {
		switch (task.getDeployReason()) {
		case PolicyDeployReasons.PolicyAssigned:
		case PolicyDeployReasons.PolicyContentChanged:
		case PolicyDeployReasons.ReDeployManually:
		case PolicyDeployReasons.EnablePlan:
		case PolicyDeployReasons.DisablePlan:
			deployPolicy(task);
			break;
		case PolicyDeployReasons.PolicyUnassigned:
			removePolicy(task);
			break;
		default:
			break;
		}
	}
	
	//////////////////////////////////////////////////////////////////////////
	
	protected long getEdgeTaskId() {
		return PolicyManagementServiceImpl.getTaskId();
	}
	
	//////////////////////////////////////////////////////////////////////////

	protected synchronized void setDeployStatusAndIgnoreExceptions(PolicyDeploymentTask task, boolean isSuccessful) {
		try {
			setDeployStatus( task, isSuccessful );
		} catch (Exception e) {
			// do nothing
		}
	}

	protected synchronized void setDeployStatusAndIgnoreExceptions(PolicyDeploymentTask task, boolean isSuccessful, boolean forceunassign) {
		try {
			setDeployStatus( task, isSuccessful, isSuccessful ? 0 : PolicyDeployStatus.DeploymentFailed, forceunassign );
		} catch (Exception e) {
			// do nothing
		}
	}
	
	protected synchronized void setDeployStatusAndIgnoreExceptions(PolicyDeploymentTask task, boolean isSuccessful, int falsereason) {
		try {
			setDeployStatus( task, isSuccessful, falsereason );
		} catch (Exception e) {
			// do nothing
		}
	}

	//////////////////////////////////////////////////////////////////////////

	protected synchronized void setDeployStatus(PolicyDeploymentTask task, boolean isSuccessful) throws SetDeployStatusException {
		setDeployStatus(task, isSuccessful, isSuccessful ? 0 : PolicyDeployStatus.DeploymentFailed);
	}

	//////////////////////////////////////////////////////////////////////////

	protected synchronized void setDeployStatus(PolicyDeploymentTask task, boolean isSuccessful, int falsereason) throws SetDeployStatusException {
		setDeployStatus(task, isSuccessful, falsereason, false);
	}
	protected synchronized void setDeployStatus(PolicyDeploymentTask task, boolean isSuccessful, int falsereason, boolean forceunassign) throws SetDeployStatusException {
		boolean isInTransaction = false;
		boolean isTransactionCompleted = false;

		try {
			this.edgePolicyDao.beginTrans();
			isInTransaction = true;

			List<EdgeHostPolicyMap> mapList = new ArrayList<EdgeHostPolicyMap>();
			this.edgePolicyDao.getHostPolicyMap(
				task.getHostId(), task.getPolicyType(), mapList );
			
			if(mapList.size() <= 0)
				return;
			
			EdgeHostPolicyMap map = mapList.get( 0 );

			if (map.getDeployStatus() != PolicyDeployStatus.Deploying) {
				// the record has been changed
				isTransactionCompleted = true;
				return;
			}

			if ((map.getDeployReason() == PolicyDeployReasons.PolicyUnassigned) && (forceunassign || isSuccessful)) {
				this.edgePolicyDao.deleteHostPolicyMap(task.getHostId(), task.getPolicyType());
				isTransactionCompleted = true;
				return;
			}

			int deployStatus = isSuccessful ? PolicyDeployStatus.DeployedSuccessfully : falsereason;
			map.setDeployStatus(deployStatus);
			map.setTryCount(map.getTryCount() + 1);

			if (isSuccessful) {
				map.setLastSuccDeploy(new Date());
			}

			this.edgePolicyDao.setHostPolicyMap(
				task.getHostId(), task.getPolicyType(),
				map.getPolicyId(), map.getDeployStatus(), map.getDeployReason(),
				map.getDeployFlags(), map.getTryCount(), map.getLastSuccDeploy() );

			isTransactionCompleted = true;
		} catch (Exception e) {
			this.logUtility.writeLog(LogUtility.LogTypes.Error, e, "setDeployStatus(): Failed.");
			throw (SetDeployStatusException) new SetDeployStatusException().initCause(e);
		} finally {
			if (isInTransaction) {
				if (isTransactionCompleted)
					this.edgePolicyDao.commitTrans();
				else // not completed
					this.edgePolicyDao.rollbackTrans();
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////

	protected boolean hasError(List<PolicyDeploymentError> errorList) {
		for (PolicyDeploymentError error : errorList) {
			if (error.getErrorType() == PolicyDeploymentError.ErrorTypes.Error)
				return true;
		}
		
		return false;
	}
	
}
