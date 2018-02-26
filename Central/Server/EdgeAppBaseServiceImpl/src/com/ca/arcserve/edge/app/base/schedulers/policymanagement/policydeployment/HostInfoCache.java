package com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ca.arcserve.edge.app.base.appdaos.EdgeHost;
import com.ca.arcserve.edge.app.base.appdaos.IEdgeHostMgrDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;

public class HostInfoCache
{
	private static HostInfoCache instance = null;
	private Map<Integer, EdgeHost> hostMap;
	private IEdgeHostMgrDao hostManagerDao;
	
	//////////////////////////////////////////////////////////////////////////

	protected HostInfoCache()
	{
		this.hostManagerDao = DaoFactory.getDao( IEdgeHostMgrDao.class );
		this.hostMap = new HashMap<Integer, EdgeHost>();
	}
	
	//////////////////////////////////////////////////////////////////////////

	public static synchronized HostInfoCache getInstance()
	{
		if (instance == null)
			instance = new HostInfoCache();
		
		return instance;
	}
	
	//////////////////////////////////////////////////////////////////////////

	public synchronized void clear()
	{
		this.hostMap.clear();
	}
	
	//////////////////////////////////////////////////////////////////////////

	public synchronized EdgeHost getHostInfo( int hostId )
	{
		if (!this.hostMap.containsKey( hostId ))
		{
			List<EdgeHost> hostList = new ArrayList<EdgeHost>();
			int isVisible = 1; // true
			this.hostManagerDao.as_edge_host_list( hostId, isVisible, hostList );
			EdgeHost hostInfo = hostList.size() > 0 ? hostList.get( 0 ) : null;
			this.hostMap.put( hostId, hostInfo );
		}
		
		return this.hostMap.get( hostId );
	}
}
