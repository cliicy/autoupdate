package com.ca.arcserve.edge.app.base.schedulers.policymanagement.policydeployment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ca.arcserve.edge.app.base.appdaos.EdgePolicy;
import com.ca.arcserve.edge.app.base.appdaos.IEdgePolicyDao;
import com.ca.arcserve.edge.app.base.dao.impl.DaoFactory;

public class PolicyContentCache
{
	private static PolicyContentCache instance = null;
	private Map<Integer, EdgePolicy> policyMap;
	private IEdgePolicyDao edgePolicyDao;
	
	//////////////////////////////////////////////////////////////////////////

	private PolicyContentCache()
	{
		this.edgePolicyDao = DaoFactory.getDao( IEdgePolicyDao.class );
		this.policyMap = new HashMap<Integer, EdgePolicy>();
	}
	
	//////////////////////////////////////////////////////////////////////////

	public static synchronized PolicyContentCache getInstance()
	{
		if (instance == null)
			instance = new PolicyContentCache();
		
		return instance;
	}
	
	//////////////////////////////////////////////////////////////////////////

	public synchronized void clear()
	{
		this.policyMap.clear();
	}
	
	//////////////////////////////////////////////////////////////////////////

	public synchronized EdgePolicy getPolicyContent( int policyId )
	{
		if (!this.policyMap.containsKey( policyId ))
		{
			List<EdgePolicy> policyList = new ArrayList<EdgePolicy>();
			int isWithDetails = 1; // without details
			this.edgePolicyDao.as_edge_policy_list( policyId, isWithDetails, policyList );
			this.policyMap.put( policyId, policyList.get( 0 ) );
		}
		
		return this.policyMap.get( policyId );
	}
}
