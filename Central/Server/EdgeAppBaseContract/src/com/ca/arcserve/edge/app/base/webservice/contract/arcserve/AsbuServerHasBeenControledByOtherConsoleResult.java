package com.ca.arcserve.edge.app.base.webservice.contract.arcserve;

import java.util.Map;

public class AsbuServerHasBeenControledByOtherConsoleResult extends ASBUSyncResult {
	private static final long serialVersionUID = -2922338720408665712L;
	
	public AsbuServerHasBeenControledByOtherConsoleResult() {
		super();
	}

	@Override
	public ResultCode getResultCode() {
		return ResultCode.ASBU_SERVER_HAS_BEEN_CONTROLED_BY_OTHER_CONSOLE;
	}

	@Override
	public boolean isNeedPopUpMessage() {
		return true;
	}

}
