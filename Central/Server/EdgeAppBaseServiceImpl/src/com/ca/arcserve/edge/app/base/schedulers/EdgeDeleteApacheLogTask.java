package com.ca.arcserve.edge.app.base.schedulers;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.arcserve.edge.util.WindowsRegistryUtils;

public class EdgeDeleteApacheLogTask implements Runnable{
	private static Logger log = Logger.getLogger(EdgeDeleteApacheLogTask.class);
	private static EdgeDeleteApacheLogTask instance = null;
	
	private static String apacheLogPath = makeFilePath(WindowsRegistryUtils.getUDPInstallationPath(), "Apache\\logs");
	private int maxSSLLogCount = 5;
	private static Map<String, List<String>> logNameMap;
	
	public static synchronized EdgeDeleteApacheLogTask getInstance() {
		if (instance == null) {
			instance = new EdgeDeleteApacheLogTask();
		}
		return instance;
	}
	
	@Override
	public void run() {
		try{
			init();
			doClean();
		}catch(Exception e){
			log.error("EdgeDeleteApacheLogTask, clean apache log failed.", e);
		}
		
	}
	
	private void init(){
		logNameMap = new HashMap<String, List<String>> ();
		// only clean ssl_request.log, rps-ssl-access.log, rps-ssl-error.2015-12-11-09_08_57
		logNameMap.put("ssl_request", new ArrayList<String>());
		logNameMap.put("rps-ssl-access", new ArrayList<String>());
		logNameMap.put("rps-ssl-error", new ArrayList<String>());
	}
	
	private void doClean(){	
		log.info("EdgeDeleteApacheLogTask: start to clean console apache log, apacheLogPath" + apacheLogPath);
		File file = new File(apacheLogPath);
		String [] names = file.list();	
        if(names != null && names.length > maxSSLLogCount){
        	for(String name : names){
        		if(name == null || name.equalsIgnoreCase(""))
        			continue;
        		for(String logName: logNameMap.keySet()){
        			if(name.startsWith(logName)){
        				logNameMap.get(logName).add(name);
        				continue;
        			}
        		}
        	}
        }
        for(String logName: logNameMap.keySet()){
        	int logCount = 0;
        	List<String> logList = logNameMap.get(logName);
        	if(logList.size() > maxSSLLogCount){
        		Collections.sort(logList);
        		for(int i = 0; i < logList.size() - maxSSLLogCount; i++){
        			File logFile = new File(makeFilePath(apacheLogPath, logList.get(i)));
        			if(logFile.exists()){
        				logFile.delete();
        				log.info("EdgeDeleteApacheLogTask: clean log name : " + logList.get(i));
        				logCount++;
        			}
        		}
        	}
        	log.info("EdgeDeleteApacheLogTask: clean " + logName + ", count : " + logCount);
		}
        log.info("EdgeDeleteApacheLogTask: end to clean console apache log");
	}
	
	public static void main(String[] stc){
		EdgeExecutors.start();
		EdgeExecutors.getSchedulePool().scheduleAtFixedRate(EdgeDeleteApacheLogTask.getInstance(), 0, 1000, TimeUnit.MILLISECONDS);
	}
	
	private static String makeFilePath( String folderPath, String fileName )
	{
		if (!folderPath.endsWith( "\\" ))
			folderPath += "\\";
		return folderPath + fileName;
	}

}
