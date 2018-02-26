package com.ca.arcserve.edge.app.base.webservice.policymanagement.validator;

import java.util.HashSet;
import java.util.Set;

/**
 * The data structure used to keep data in one validation sessions. This allows
 * task validators to share data and validation results, and enable them to do
 * optimizations via these results.
 * 
 * @author panbo01
 *
 */
public class ValidationSession
{
	private int validatedReplicationCount;
	private Set<String> usedRpsSet;
	
	public ValidationSession()
	{
		this.validatedReplicationCount = 0;
		this.usedRpsSet = new HashSet<String>();
	}

	public int getValidatedReplicationCount()
	{
		return validatedReplicationCount;
	}

	public void increaseValidatedReplicationCount()
	{
		this.validatedReplicationCount ++;
	}

	public void registerUsedRps( String rpsName )
	{
		this.usedRpsSet.add( createUsedRpsSetEntry( rpsName ) );
	}
	
	public boolean isRpsUsed( String rpsName )
	{
		return this.usedRpsSet.contains( createUsedRpsSetEntry( rpsName ) );
	}
	
	private String createUsedRpsSetEntry( String rpsName )
	{
		return rpsName.trim().toUpperCase();
	}
}
