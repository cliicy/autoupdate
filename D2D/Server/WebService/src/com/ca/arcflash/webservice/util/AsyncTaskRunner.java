package com.ca.arcflash.webservice.util;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;

import org.apache.log4j.Logger;

public class AsyncTaskRunner {
	private static Logger logger = Logger.getLogger(AsyncTaskRunner.class);
	
	private static ExecutorService pool = TheadPoolManager.getThreadPool(TheadPoolManager.AsyncTaskRunner);
	
	public synchronized static void submit(Object instance, Method method, Object...args) {
		
		pool.submit(new AsyncTask(instance, method, args));
	}
	
	private static class AsyncTask implements Runnable {
		private Object instance;
		private Method method;
		private Object[] args;
		
		public AsyncTask(Object instance, Method method, Object... args) {
			this.instance = instance;
			this.method = method;
			this.args = args;
		}
		
		public void run() {
			try {
				method.invoke(instance, args);
			}catch(Exception e) {
				logger.error("Failed to invoke the method " + method 
						+ " with arguments: " + args + " exception is " + e);
			}
		}
	}
}
