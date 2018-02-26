package com.ca.arcflash.ha.utils;

/*
 * Due to potentail network fault, some operations may fail.
 * Once these operations failed, retry these operations on base of retry strategy
 */
public class HARetryStrategy {
	
	private int interval;  // in seconds
	private int times; //retry times
	
	private HARetryStrategy() {}
	
	public static HARetryStrategy newStrategy(int interval, int times){
		HARetryStrategy strategy = new HARetryStrategy();
		strategy.interval = interval;
		strategy.times = times;
		return strategy;
	}

	public int getInterval() {
		return interval;
	}

	public int getTimes() {
		return times;
	}

}
