package com.ca.arcserve.edge.app.base.webservice.contract.arcserve;

public class PrimaryRoleChangedAndNoPlanUseServer extends ASBUSyncResult{
	private static final long serialVersionUID = -6130482378260139108L;
	private String domain;
	
	public PrimaryRoleChangedAndNoPlanUseServer() {
		super();
	}

	public PrimaryRoleChangedAndNoPlanUseServer(String domain) {
		super();
		this.domain = domain;
	}

	@Override
	public ResultCode getResultCode() {
		return ResultCode.PRIMARY_ROLE_CHANGE_AND_NO_PLAN_USE_SERVER;
	}

	@Override
	public boolean isNeedPopUpMessage() {
		return true;
	}

	@Override
	public String getParameter(){
		return domain;
	}
//	public String getMessage() {
//		return "Primary server role have changed and there have no plan to use servers, delete the all servers under this domain: "+domain;
//	} 
}
