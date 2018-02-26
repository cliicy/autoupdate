package com.ca.arcserve.edge.app.base.webservice.contract.license;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BeanModelTag;

public class LicensedMachine implements Serializable, BeanModelTag{

	private static final long serialVersionUID = -6315478101078270779L;

	private String hostname;
	private Boolean licensed;

	public LicensedMachine() {
		super();
		// TODO Auto-generated constructor stub
	}
	public LicensedMachine(String hostname, Boolean licensed) {
		super();
		this.hostname = hostname;
		this.licensed = licensed;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public Boolean getLicensed() {
		return licensed;
	}
	public void setLicensed(Boolean licensed) {
		this.licensed = licensed;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((hostname == null) ? 0 : hostname.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LicensedMachine other = (LicensedMachine) obj;
		if (hostname == null) {
			if (other.hostname != null)
				return false;
		} else if (!hostname.equals(other.hostname))
			return false;
		return true;
	}


	protected LicensedMachine copy() {
		LicensedMachine x = new LicensedMachine();
		x.setHostname(this.getHostname());
		x.setLicensed(getLicensed());
		return x;
	}


}
