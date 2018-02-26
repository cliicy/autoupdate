package com.ca.arcserve.edge.app.base.webservice.contract.arcserve;

public class PrimaryRoleChangedAndHasPlanUseServer extends ASBUSyncResult {
	private static final long serialVersionUID = -6268521370978263810L;
	private String[] planNames;
	private String hostName;

	public PrimaryRoleChangedAndHasPlanUseServer() {
		super();
	}
	public PrimaryRoleChangedAndHasPlanUseServer(String hostName, String[] planNames) {
		super();
		this.hostName = hostName;
		this.planNames = planNames;
	}
	@Override
	public ResultCode getResultCode() {
		return ResultCode.PRIMARY_ROLE_CHANGE_AND_HAS_PLAN_USE_SERVER;
	}
	@Override
	public boolean isNeedPopUpMessage() {
		return true;
	}
	@Override
	public String[] getParameter(){
		return planNames;
	}
	
	public String getHostName() {
		return hostName;
	}
//	public String getMessage() {
//		StringBuffer message =  new StringBuffer("Primary server role have changed and there have plan to use servers, please manually terminate the relationship from the plan below: \n");
//		for(String planName : planNames){
//			message.append(planName + "\n");
//		}
//		return message.toString();
//	} 
}
