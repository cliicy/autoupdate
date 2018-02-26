package com.ca.arcserve.edge.app.base.webservice.contract.license;

import java.io.Serializable;

import com.extjs.gxt.ui.client.data.BeanModelTag;

public class LicenseInfomation implements Serializable,BeanModelTag{

	private static final long serialVersionUID = -7804860995561545709L;
	private int licenseId;
	private String licenseName;
	private int licenseTotalNumber;
	private int licenseRemainedNumber;
	private int unlicensedNumber;
	
	public int getLicenseId() {
		return licenseId;
	}
	public void setLicenseId(int licenseId) {
		this.licenseId = licenseId;
	}
	public String getLicenseName() {
		return licenseName;
	}
	public void setLicenseName(String licenseName) {
		this.licenseName = licenseName;
	}
	public int getLicenseTotalNumber() {
		return licenseTotalNumber;
	}
	public void setLicenseTotalNumber(int licenseTotalNumber) {
		this.licenseTotalNumber = licenseTotalNumber;
	}
	public int getLicenseRemainedNumber() {
		return licenseRemainedNumber;
	}
	public void setLicenseRemainedNumber(int licenseRemainedNumber) {
		this.licenseRemainedNumber = licenseRemainedNumber;
	}
	public int getUnlicensedNumber() {
		return unlicensedNumber;
	}
	public void setUnlicensedNumber(int unlicensedNumber) {
		this.unlicensedNumber = unlicensedNumber;
	}

}
