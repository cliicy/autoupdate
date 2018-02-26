package com.ca.arcflash.ha.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class LRUHashMap<K,V> extends LinkedHashMap<K, V> {

	private static final long serialVersionUID = 1L;
	
	private static final float DEFAULT_LOAD_FACTOR = 0.75f;
	
	private int capacity;
	
	public LRUHashMap(int capacity) {
		
		super(capacity,DEFAULT_LOAD_FACTOR);
		this.capacity = capacity;
		
	}
	
	@Override
	protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
		return size() > capacity;
	}
	
	public static void main(String[] args) {
		
		Map<Short, Short> cache = new LRUHashMap<Short, Short>(5);
		for (short i = 0; i < 6; i++) {
			cache.put(i, i);
		}

		for (Entry<Short, Short> entry : cache.entrySet()) {
			System.out.println(entry.getKey()+ "=" + entry.getValue());
		}
		
	}
	

}
