package com.ca.arcflash.webservice.edge.policymanagement;

public class PolicyCheckStatus {
	public static final int UNKNOWN=-1;
	public static final int SAMEPOLICY=0;
	public static final int NOPOLICY=2;
	public static final int DIFFERENTPOLICY=1;
	public static final int POLICYFAILED=3;
	public static final int POLICYDEPLOYING=4;
}
