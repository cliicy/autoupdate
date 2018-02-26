package com.ca.arcflash.ui.client.model;

public enum ExchVersion {
	Exch2003(2003), Exch2007(2007), Exch2010(2010), Exch2013(2013), Exch2016(2016);
	private int version;

	ExchVersion(int version) {
		this.version = version;
	}

	public int getVersion() {
		return version;
	}
}
