package com.ca.arcflash.common;

import java.util.Hashtable;
import java.util.Map;


public class HACoordinator {

	
//	private static boolean heartBeatStopped;
	
	public static final String HEART_BEAT_STOPPED = "HEART_BEAT_STOPPED";

	public static final String HEART_BEAT_RUNNING = "HEART_BEAT_RUNNING";
	
	private static Map<String, String> heartBeatIndicators = new Hashtable<String, String>();
	
	private static String getHeartBeatState(String afGuid){
		synchronized (heartBeatIndicators) {
			String state = heartBeatIndicators.get(afGuid);
			if(state == null){
				state = HEART_BEAT_RUNNING;
				heartBeatIndicators.put(afGuid, state);
			}
			return state;
		}
	} 
	
	private static void setHeartBeatState(String afGuid,String state){
		heartBeatIndicators.put(afGuid, state);
	}
	
	public static boolean isHeartBeatStopped(String afGuid) {
		synchronized (heartBeatIndicators) {
			String state = getHeartBeatState(afGuid);
			if(HEART_BEAT_STOPPED.equals(state)){
				return true;
			}else{
				return false;
			}
		}
		
//		return heartBeatStopped;
	}

	public static void setHeartBeatStopped(String afGuid,boolean heartBeatStopped) {
		synchronized (heartBeatIndicators) {
			if(heartBeatStopped){
				setHeartBeatState(afGuid,HEART_BEAT_STOPPED);
			}else{
				setHeartBeatState(afGuid, HEART_BEAT_RUNNING);
			}
		}
		
//		HACoordinator.heartBeatStopped = heartBeatStopped;
	}
	
}
