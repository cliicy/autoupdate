package com.ca.arcserve.edge.app.base.webservice.contract.arcserve;

public enum ARCServerType {
	UN_KNOWN(0), PrimaryAsGDB(1), PrimaryAsBranch(2), Primary(3), StandaloneServer(
			4), Member(5);

	int val;

	ARCServerType(int val) {
		this.val = val;
	}

	public int intValue() {
		return val;
	}

}
