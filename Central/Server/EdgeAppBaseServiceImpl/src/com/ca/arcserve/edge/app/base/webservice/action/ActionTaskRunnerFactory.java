package com.ca.arcserve.edge.app.base.webservice.action;

import java.util.concurrent.CountDownLatch;

import com.ca.arcserve.edge.app.base.webservice.contract.action.ActionTaskParameter;
import com.ca.arcserve.edge.app.base.webservice.contract.jobhistory.CancelJobParameter;
import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;

public class ActionTaskRunnerFactory {
	
	private static ActionTaskRunnerFactory instance = null;
	
	private ActionTaskRunnerFactory(){};
	
	public static ActionTaskRunnerFactory getInstance(){
		if(instance == null){
			instance = new ActionTaskRunnerFactory();
		}
		return instance;
	}

	public Runnable createTaskRunner(ActionTaskParameter parameter,Object entityId, CountDownLatch doneSignal, ActionTaskManager manager){
		if(Module.UpdateMutipleNode==parameter.getModule()){
			Integer nodeId = (Integer)entityId;
			return new UpdateNodeTaskRunner(nodeId, parameter, doneSignal,manager);
		}else if(Module.CancelMutipleJob == parameter.getModule()){
			Integer id = entityId.hashCode();
			return new CancelJobTaskRunner(id, (CancelJobParameter) entityId,parameter,doneSignal,manager);
		}else if (Module.ManageMultipleNodes == parameter.getModule()) {
			Integer nodeId = (Integer)entityId;
			return new ManageNodeTaskRunner(nodeId, parameter, doneSignal,manager);
		}else if (Module.SubmitD2DJob == parameter.getModule()) {
			Integer nodeId = (Integer)entityId;
			return new BackupNowTaskRunner(nodeId, parameter, doneSignal, manager);
		}else if (Module.SendRegistrationEmails == parameter.getModule()) {
			Integer key =  (Integer)entityId;
			return new SendRegistrationEmailsTaskRunner(key, parameter, doneSignal, manager);
		}
		return null;
	}
}
