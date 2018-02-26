package com.ca.arcflash.webservice.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class TheadPoolManager {
	public static final String UtilTheadPool = "utilTheadPool";
	public static final String AsyncTaskRunner = "AsyncTaskRunner";

	private static Map<String, ExecutorService> map = new HashMap<String, ExecutorService>();
	static {
		map.put(UtilTheadPool, Executors.newCachedThreadPool());
		map.put(AsyncTaskRunner, Executors.newCachedThreadPool());

	}

	public static ExecutorService getThreadPool(String key) {

		return (ExecutorService) map.get(key);
	}
	
	public static void destory(){
		for(ExecutorService es: map.values()){
			if(!es.isShutdown()){
				es.shutdownNow();
			}
		}		
	}

}
