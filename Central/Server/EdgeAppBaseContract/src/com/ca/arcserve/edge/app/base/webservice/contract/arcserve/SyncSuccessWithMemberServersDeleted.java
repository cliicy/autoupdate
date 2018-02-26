package com.ca.arcserve.edge.app.base.webservice.contract.arcserve;

import java.util.List;

public class SyncSuccessWithMemberServersDeleted extends ASBUSyncResult{
	private static final long serialVersionUID = -6130482378260139108L;
	private List<ASBUServerInfo> deletedServers;
	
	public SyncSuccessWithMemberServersDeleted() {
		super();
	}

	public SyncSuccessWithMemberServersDeleted(List<ASBUServerInfo> deletedServers) {
		super();
		this.deletedServers = deletedServers;
	}

	@Override
	public ResultCode getResultCode() {
		return ResultCode.SYNC_SUCCESS_WITH_MEMBER_SERVERS_DELETED;
	}

	@Override
	public boolean isNeedPopUpMessage() {
		return true;
	}

	@Override
	public List<ASBUServerInfo> getParameter(){
		return deletedServers;
	}
}
