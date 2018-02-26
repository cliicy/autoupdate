package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement;

public class AssignPolicyResultCodes
{
	public static final int Successful					= 0;
	public static final int Failed_DBOperationError		= -1;
	public static final int Failed_NodeDoesntExist		= -2;
	public static final int Failed_PolicyDoesntExist	= -3;
	public static final int Failed_NodeCannotBeAssigned	= -4;
	public static final int Failed_PolicyIsInUse		= -5;
}
