package com.ca.arcflash.webservice.common;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.ca.arcflash.webservice.edge.license.LICENSEDSTATUS;

@XmlRootElement(name = "LicenseObject")
class LicenseObject implements Serializable{
	private static final long serialVersionUID = 5364696857019166891L;
	
	private String licenseSubject;
	private long licenseFetchTime; 
	private LICENSEDSTATUS licenseValue;
	
	@XmlElement
	public String getLicenseSubject() {
		return licenseSubject;
	}

	public void setLicenseSubject(String licenseSubject) {
		this.licenseSubject = licenseSubject;
	}
	
	@XmlElement
	public long getLicenseFetchTime() {
		return licenseFetchTime;
	}
	
	public void setLicenseFetchTime(long time) {
		this.licenseFetchTime = time;
	}
	
	@XmlElement
	public LICENSEDSTATUS getLicenseValue() {
		return licenseValue;
	}
	
	public void setLicenseValue(LICENSEDSTATUS license) {
		this.licenseValue = license;
	}
	
	LicenseObject(){
	}
	
	LicenseObject(long updateTime, LICENSEDSTATUS lic){
		licenseFetchTime = updateTime;
		licenseValue = lic;
	}
	
	@Override
	public String toString() {
		return licenseFetchTime + ", " + licenseSubject + ", " + licenseValue;
	}
	
}