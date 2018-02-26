package com.ca.arcserve.edge.app.base.webservice.contract.arcserve;

import java.util.Map;

public class MemberRoleChangedAndHasPlanUseServer extends ASBUSyncResult {
	private static final long serialVersionUID = -2922338720408665710L;
	private Map<String, String[]> serverPlanMap;
	
	public MemberRoleChangedAndHasPlanUseServer() {
		super();
	}
	public MemberRoleChangedAndHasPlanUseServer(Map<String, String[]> serverPlanMap) {
		super();
		this.serverPlanMap = serverPlanMap;
	}
	@Override
	public ResultCode getResultCode() {
		return ResultCode.MEMBER_ROLE_CHANGE_AND_HAS_PLAN_USE_SERVER;
	}

	@Override
	public boolean isNeedPopUpMessage() {
		return true;
	}
	@Override
	public Map<String, String[]> getParameter(){
		return serverPlanMap;
	}
//	public String getMessage() {
//		StringBuffer message =  new StringBuffer("Member server role have changed and there have plan to use servers, please manually terminate the relationship from the plan below: \n");
//		Set<String> keySet = serverPlanMap.keySet();
//		for(String serverName : keySet){
//			message.append("server name: " + serverName + "\n plan name: \n");
//			String[] planNames = serverPlanMap.get(serverName);
//			for(String planName : planNames){
//				message.append(planName + "\n");
//			}
//		}
//		return message.toString();
//	} 
}
