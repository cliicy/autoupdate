package com.ca.arcflash.webservice.replication;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class LRUCache<K, V> {
	private static final float hashTableLoadFactor = 0.75f;
	private LinkedHashMap<K, V> map;
	private int cacheSize = 50;
	private final int CHECK_INTERVAL = 60 * 60 * 1000;
	private final int HOUR_UNIT = 60 * 60 * 1000;
	private Calendar lastCalendar = null;
	private final int IDLE_HOURS = 3;

	public LRUCache(int cacheSize, boolean isAutomaticGC) {
		this.cacheSize = cacheSize;
		int hashTableCapacity = (int) Math.ceil(cacheSize / hashTableLoadFactor) + 1;
		map = new LinkedHashMap<K, V>(hashTableCapacity, hashTableLoadFactor, true) {
			private static final long serialVersionUID = 1;
			@Override
			protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
				return size() > LRUCache.this.cacheSize;
			}
		};

		if (isAutomaticGC) {

			lastCalendar = Calendar.getInstance();
			
			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					Calendar nowCalendar = Calendar.getInstance();
					long hoursDiff = (nowCalendar.getTimeInMillis() - lastCalendar.getTimeInMillis()) / (HOUR_UNIT);
					if (hoursDiff > IDLE_HOURS && LRUCache.this.usedEntries() > 0) {
						clear();
						lastCalendar = Calendar.getInstance();
					}
				}
			}, 1000, CHECK_INTERVAL);
		}
	}

	public synchronized V get(K key) {
		lastCalendar = Calendar.getInstance();
		return map.get(key);
	}

	public synchronized void put(K key, V value) {
		lastCalendar = Calendar.getInstance();
		map.put(key, value);
	}

	public synchronized void clear() {
		map.clear();
	}

	public synchronized int usedEntries() {
		return map.size();
	}

	public synchronized Collection<Map.Entry<K, V>> getAll() {
		return new ArrayList<Map.Entry<K, V>>(map.entrySet());
	}
}
