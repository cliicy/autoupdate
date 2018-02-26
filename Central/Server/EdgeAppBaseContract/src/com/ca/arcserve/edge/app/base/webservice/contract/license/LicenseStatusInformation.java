package com.ca.arcserve.edge.app.base.webservice.contract.license;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModelTag;

public class LicenseStatusInformation implements Serializable, BeanModelTag {
	/**
	 *
	 */
	private static final long serialVersionUID = 9014148019675167790L;
	private String compoentID;
	private String componentName;
	private String version = "1.0";
	private int activeLicenseCount = 0;
	private int availableLicenseCount = 0;
	private int totalLicenseCount = 0;
	private int neededLicenseCount = 0;
	private List<LicensedMachine> machines = new ArrayList<LicensedMachine>();

	public List<LicensedMachine> getMachines() {
		return machines;
	}
	public void setMachines(List<LicensedMachine> machines) {
		this.machines = machines;
	}
	public String getComponentName() {
		return componentName;
	}
	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public int getActiveLicenseCount() {
		return activeLicenseCount;
	}
	public void setActiveLicenseCount(int activeLicenseCount) {
		this.activeLicenseCount = activeLicenseCount;
	}
	public int getAvailableLicenseCount() {
		return availableLicenseCount;
	}
	public void setAvailableLicenseCount(int availableLicenseCount) {
		this.availableLicenseCount = availableLicenseCount;
	}
	public int getTotalLicenseCount() {
		return totalLicenseCount;
	}
	public void setTotalLicenseCount(int totalLicenseCount) {
		this.totalLicenseCount = totalLicenseCount;
	}
	public int getNeededLicenseCount() {
		return neededLicenseCount;
	}
	public void setNeededLicenseCount(int neededLicenseCount) {
		this.neededLicenseCount = neededLicenseCount;
	}
	public String getCompoentID() {
		return compoentID;
	}
	public void setCompoentID(String compoentID) {
		this.compoentID = compoentID;
	}



	public LicenseStatusInformation copy()  {
		LicenseStatusInformation t = new LicenseStatusInformation();
		t.compoentID = this.compoentID;
		t.componentName = this.componentName;
		t.version = this.version;
		t.activeLicenseCount = this.activeLicenseCount;
		t.availableLicenseCount = this.availableLicenseCount;
		t.totalLicenseCount = this.totalLicenseCount;
		t.neededLicenseCount = this.neededLicenseCount;
		{
			t.machines= new ArrayList<LicensedMachine>();
			for(LicensedMachine m : this.machines)
			t.machines.add(m.copy());
		}
		return t;
	}



}
