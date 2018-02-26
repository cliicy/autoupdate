/**
 * 
 */
package com.ca.arcflash.webservice.service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pidma02
 *
 */
public class CSearchSessionManager {

	private static CSearchSessionManager currObj = null;
	
	Map<Long ,CSearchController> mp=new HashMap<Long, CSearchController>();
	Integer mpKeyId;
	
	public static synchronized CSearchSessionManager getInstance()
	{
		if(currObj == null)
		{
			currObj = new CSearchSessionManager();
		}
		
		return currObj;
	}
	/**
	 * 
	 */
	private CSearchSessionManager() {
		mpKeyId = 1;
	}

	public Map<Long, CSearchController> getMp() {
		return mp;
	}

	public void setMp(Map<Long, CSearchController> mp) {
		this.mp = mp;
	}

	public synchronized long insert(CSearchController searchControllerObj)
	{
		Long i = new Long(mpKeyId);
		mp.put(i, searchControllerObj);
		mpKeyId++;
		
		return i.longValue();
	}
	
	public synchronized CSearchController getSearchController(long l)
	{
		return mp.get(new Long(l));
	}
	
	public synchronized void removeOnClose(long id) {
		mp.remove(new Long(id));
	}
}
