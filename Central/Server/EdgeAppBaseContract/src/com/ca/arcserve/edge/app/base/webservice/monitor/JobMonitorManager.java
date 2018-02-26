package com.ca.arcserve.edge.app.base.webservice.monitor;

import java.util.HashMap;

public class JobMonitorManager{
	
	public static final JobMonitorManager manager = new JobMonitorManager();
	
	private HashMap<String,JobMonitor> monitorContainer = new HashMap<String,JobMonitor>();
	
	private JobMonitorManager(){}
	
	public static JobMonitorManager getInstance(){
		return manager;
	}
	
	public synchronized <T extends JobMonitor> T getJobMonitor(String jobID, Class<T> clazz){
		
		if(monitorContainer.containsKey(jobID)){
			return (T)monitorContainer.get(jobID);
		}else{
			try {
				T monitor = clazz.newInstance();
				monitor.setJobId(jobID);
				monitorContainer.put(jobID, monitor);
				return monitor;
			} catch (Exception e) {
				return null;
			} 
		}
	}
	
	public synchronized void removeJobMonitor(String jobID){
		if(monitorContainer.containsKey(jobID)){
			monitorContainer.remove(jobID);
		}
	}
	
	
	public static void main(String[] args) {
		
		JobMonitorManager manager = JobMonitorManager.getInstance();
		JobMonitor m = manager.getJobMonitor("test", ImportNodesJobMonitor.class);
		System.out.println(m.getJobId());
		m = manager.getJobMonitor("test", ImportNodesJobMonitor.class);
		System.out.println(m.getJobId());
		
	}
	
}


