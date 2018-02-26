package com.ca.arcserve.edge.app.base.webservice.contract.arcserve;

public class SyncSuccessWithoutException extends ASBUSyncResult {

	private static final long serialVersionUID = 7536031210491221633L;
	@Override
	public ResultCode getResultCode() {
		return ResultCode.SYNC_SUCCESS_WITHOUT_EXCEPTION;
	}
	@Override
	public boolean isNeedPopUpMessage() {
		return false;
	}
	@Override
	public String getParameter() {
		return "";
	} 
}
