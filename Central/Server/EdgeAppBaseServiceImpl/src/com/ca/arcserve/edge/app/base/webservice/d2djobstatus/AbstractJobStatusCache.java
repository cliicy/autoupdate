package com.ca.arcserve.edge.app.base.webservice.d2djobstatus;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;

abstract class AbstractJobStatusCache<T> implements Runnable {
	
	protected static class TimeoutJobStatus<T> extends DelayJobs<D2DJobStatusPair<Integer, T>> {

		public TimeoutJobStatus(int nodeId, T status, long timeout) {
			super(new D2DJobStatusPair<Integer, T>(nodeId, status), timeout);
		}
		
		public int getNodeId() {
			return getItem().nodeId;
		}
		
		public T getJobStatus() {
			return getItem().infoBean;
		}
		
	}
	
	protected ConcurrentMap<Integer, TimeoutJobStatus<T>> cache = new ConcurrentHashMap<Integer, TimeoutJobStatus<T>>();
	private DelayQueue<TimeoutJobStatus<T>> delayQueue = new DelayQueue<TimeoutJobStatus<T>>();
	private long delayTime = TimeUnit.NANOSECONDS.convert(30, TimeUnit.SECONDS);
	
	public AbstractJobStatusCache() {
		Thread delayCheckThread = new Thread(this);
		delayCheckThread.setDaemon(true);
		delayCheckThread.setName("D2DJobStatusDelayCheckThread - " + getClass().getName());
		delayCheckThread.start();
	}
	
	protected abstract void onTimeout(TimeoutJobStatus<T> timeoutJobStatus);
	
	@Override
	public void run() {
		while (true) {
			try {
				onTimeout(delayQueue.take());
			} catch (InterruptedException e) {
				break;
			}
		}
	}
	
	protected TimeoutJobStatus<T> createTimeoutJobStatus(int nodeId, T status) {
		return new TimeoutJobStatus<T>(nodeId, status, delayTime);
	}
	
	public void add(int nodeId, T status) {
		TimeoutJobStatus<T> newTimeoutJobStatus = createTimeoutJobStatus(nodeId, status);
		TimeoutJobStatus<T> oldTimeoutJobStatus = cache.put(nodeId, newTimeoutJobStatus);
		if (oldTimeoutJobStatus != null) {
			delayQueue.remove(oldTimeoutJobStatus);
		}
		
		if (checkTimeout(status)) {
			delayQueue.put(newTimeoutJobStatus);
		}
	}
	
	protected boolean checkTimeout(T status) {
		return true;
	}
	
	public T get(int nodeId) {
		TimeoutJobStatus<T> timeoutJobStatus = cache.get(nodeId);
		return timeoutJobStatus == null ? null : timeoutJobStatus.getJobStatus();
	}

}
