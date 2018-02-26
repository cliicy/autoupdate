package com.ca.arcserve.edge.app.msp.webservice.contract;

import com.ca.arcserve.edge.app.base.webservice.contract.common.PagingConfig;

public class CustomerPagingConfig extends PagingConfig {

	private static final long serialVersionUID = -8974324290338244655L;
	
	private boolean asc = true;

	public boolean isAsc() {
		return asc;
	}

	public void setAsc(boolean asc) {
		this.asc = asc;
	}

}
