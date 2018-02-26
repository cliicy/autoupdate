package com.ca.arcserve.edge.app.base.webservice.contract.taskmonitor;

import java.io.Serializable;
import java.util.Date;

import com.ca.arcserve.edge.app.base.webservice.contract.log.Module;
import com.extjs.gxt.ui.client.data.BeanModelTag;

public class Task implements Serializable, BeanModelTag{
	private static final long serialVersionUID = 1L;
	private int id;
	private Module module;
	private String target;
	private TaskStatus status;
	private TaskDetail details;
	private Date startTime;
	private Date endTime;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public TaskStatus getStatus() {
		return status;
	}
	public void setStatus(TaskStatus status) {
		this.status = status;
	}
	public TaskDetail getDetails() {
		return details;
	}
	public void setDetails(TaskDetail details) {
		this.details = details;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	public Module getModule() {
		return module;
	}
	public void setModule(Module module) {
		this.module = module;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Task other = (Task) obj;
		if (id != other.id)
			return false;
		return true;
	}
} 