package com.ca.arcserve.edge.app.base.schedulers.impl;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.Future;

import com.ca.arcserve.edge.app.base.schedulers.EdgeExecutors;

public class EdgeTaskFactory {
	public static String EDGE_TASK_SRM = "SRM";
	public static String EDGE_TASK_NodeDelete = "NodeDataDelete";
	public static String EDGE_TASK_SRM_PKI_MONITOR = "SRM PKI Monitor";
	public static String EDGE_TASK_ARCSERVE_SYNC = "ARCserveSync";
	public static String EDGE_TASK_EMAIL_SERVICE = "Email Service";
	
	
	private static EdgeTaskFactory instance = null;
	private Hashtable<String, EdgeTask> mTaskTable;
	private Hashtable<String, Future<?>> mRunningTable;
	
	public static synchronized EdgeTaskFactory getInstance() {
		if (instance == null) {
			instance = new EdgeTaskFactory();
		}
		
		return instance;
	}
	
	private EdgeTaskFactory() {
		mTaskTable = new Hashtable<String, EdgeTask>();
		mRunningTable = new Hashtable<String, Future<?>>();
	}
	
	
	
	public void Add(String name, EdgeTask task) {
		synchronized (mTaskTable) {
			if (mTaskTable.containsKey(name))
				return;

			mTaskTable.put(name, task);
		}
	}
	
	public EdgeTask getTask(String name) {
		synchronized (mTaskTable) {
			if (mTaskTable.containsKey(name)) {
				return mTaskTable.get(name);
			} else {
				return null;
			}
		}
	}
	
	public void LanuchTask(String name) {
		synchronized (mTaskTable) {
			if (mTaskTable.containsKey(name)) {
				EdgeTask task = mTaskTable.get(name);
				Future<?> future = EdgeExecutors.getCachedPool().submit(task);
				mRunningTable.put(name, future);
			}
		}
	}
	
	
	public void ShutdownAllTask() {
		synchronized (mRunningTable) {
			Enumeration<String> keysEnumeration = mRunningTable.keys();
			Enumeration<Future<?>> valuesEnumeration = mRunningTable.elements();

			while (valuesEnumeration.hasMoreElements()) {
				Future<?> f = (Future<?>) valuesEnumeration.nextElement();
				f.cancel(false);
			}

			mRunningTable.clear();
			mTaskTable.clear();
		}
	}
	
	public void ShutdownTask(String name) {
		synchronized (mRunningTable) {
			if (mRunningTable.containsKey(name)) {
				Future<?> future = mRunningTable.get(name);
				EdgeTask task = mTaskTable.get(name);
				task.Stop();
				System.out.println("task is stopped");
				future.cancel(false);
				mRunningTable.remove(name);
			}
		}
	}
	
}
