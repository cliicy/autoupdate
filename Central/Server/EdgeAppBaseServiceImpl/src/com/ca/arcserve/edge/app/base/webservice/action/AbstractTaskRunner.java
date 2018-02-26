package com.ca.arcserve.edge.app.base.webservice.action;

import java.io.Serializable;
import java.util.concurrent.CountDownLatch;

import com.ca.arcserve.edge.app.base.webservice.EdgeFactory;
import com.ca.arcserve.edge.app.base.webservice.contract.action.ActionTaskData;
import com.ca.arcserve.edge.app.base.webservice.contract.action.ActionTaskParameter;
import com.ca.arcserve.edge.app.base.webservice.contract.common.ValuePair;
import com.ca.arcserve.edge.app.base.webservice.contract.taskmonitor.TaskStatus;
import com.ca.arcserve.edge.app.base.webservice.gateway.IEdgeGatewayLocalService;
import com.ca.arcserve.edge.app.base.webservice.log.ActivityLogServiceImpl;
import com.ca.arcserve.edge.app.base.webservice.node.NodeServiceImpl;

public abstract class AbstractTaskRunner<T extends Serializable> implements Runnable{
	protected NodeServiceImpl nodeService = null;
	protected IEdgeGatewayLocalService gatewayService = EdgeFactory.getBean(IEdgeGatewayLocalService.class);
	protected ActivityLogServiceImpl logService = new ActivityLogServiceImpl();
	
	protected ActionTaskManager<T> manager;
	protected CountDownLatch doneSignal;
	protected ActionTaskParameter<T> parameter;
	protected ActionTaskData<T> taskData;
	protected T entityKey;
	
	public AbstractTaskRunner(T entityKey, ActionTaskParameter<T> parameter, CountDownLatch doneSignal, ActionTaskManager<T> manager){
		this.entityKey = entityKey;
		this.parameter = parameter;
		this.manager = manager;
		this.doneSignal = doneSignal;
		this.taskData = manager.getData();
		if(manager.getWebService() != null){
			nodeService = new NodeServiceImpl(manager.getWebService());
		}else {
			nodeService = new NodeServiceImpl();
		}
	}
	
	@Override
	public void run() {
		excute();
		manager.updateTask(TaskStatus.InProcess);//update the action data to DB
		doneSignal.countDown();
	}
	
	protected abstract  void excute();
	
	protected void addFailedEntities(T key, Long value){
		synchronized(taskData){
			taskData.getFailedEntities().add(new ValuePair<T,Long>(key,value));
		}
	}
	
	protected void addWarnningEntities(T key, String value){
		synchronized(taskData){
			taskData.getWanningEntities().add(new ValuePair<T,String>(key,value));
		}
	}
	
	protected void addSucceedEntities(T entityKey){
		synchronized(taskData){
			taskData.getSuccessfullEntities().add(entityKey);
		}
	}
}
