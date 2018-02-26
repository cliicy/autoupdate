package com.ca.arcserve.edge.app.base.db.impl;

import java.sql.Connection;

public class ConnectionResult {
	private Throwable e;
	private Connection con;
	public Throwable getE() {
		return e;
	}
	public void setE(Throwable e) {
		this.e = e;
	}
	public Connection getCon() {
		return con;
	}
	public void setCon(Connection con) {
		this.con = con;
	}

}
