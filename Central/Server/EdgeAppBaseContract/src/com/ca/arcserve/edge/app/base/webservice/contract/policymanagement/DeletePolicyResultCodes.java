package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement;

public class DeletePolicyResultCodes
{
	public static final int Successful				= 0;
	public static final int Failed_PolicyIsInUse	= -1;
	public static final int Failed_DBOperationError	= -2;
}
