package com.ca.arcserve.edge.app.base.webservice.action;

import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.ca.arcserve.edge.app.base.webservice.contract.taskmonitor.TaskStatus;

public interface IActionTask<P> {
	public void doAction(P actionParameter);
	public int registerTask(Module module);
	public void updateTask(TaskStatus status);
}
