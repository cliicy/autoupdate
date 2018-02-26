package com.ca.arcserve.edge.app.base.schedulers;

import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.ca.arcserve.edge.app.base.common.NamingThreadFactory;

public class EdgeExecutors {
	
	private static ScheduledExecutorService schedulePool;
	private static ExecutorService cachedPool;
	private static ExecutorService fixedPool;
	
	public static ScheduledExecutorService getSchedulePool() {
		return schedulePool;
	}
	
	public static ExecutorService getCachedPool() {
		return cachedPool;
	}
	
	public static ExecutorService getFixedPool() {
		return fixedPool;
	}
	
	public static synchronized void start() {
		if (schedulePool == null) {
			schedulePool = Executors.newScheduledThreadPool(8, new NamingThreadFactory( "EdgeExecutors.schedulePool" ));
		}
		
		if (cachedPool == null) {
			cachedPool = Executors.newCachedThreadPool(new NamingThreadFactory( "EdgeExecutors.cachedPool" ));
		}
		
		if (fixedPool == null) {
			fixedPool = Executors.newFixedThreadPool(32, new NamingThreadFactory( "EdgeExecutors.fixedPool" ));
		}
	}
	
	public static synchronized void shutdownNow() {
		if (schedulePool != null) {
			schedulePool.shutdownNow();
			schedulePool = null;
		}
		
		if (cachedPool != null) {
			cachedPool.shutdownNow();
			cachedPool = null;
		}
		
		if (fixedPool != null) {
			fixedPool.shutdownNow();
			fixedPool = null;
		}
	}
	
	public static void submitAndWaitTermination(List<Runnable> tasks) {
		if (tasks == null || tasks.isEmpty()) {
			return;
		}
		
		CompletionService<Void> service = new ExecutorCompletionService<Void>(fixedPool);
		
		for (Runnable task : tasks) {
			service.submit(task, null);
		}
		
		try {
			for (int i = 0; i < tasks.size(); ++i) {
				service.take();
			}
		} catch (InterruptedException e) {
		}
	}

}
