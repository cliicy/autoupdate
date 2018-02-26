package com.ca.arcserve.edge.app.base.webservice.contract.license;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.extjs.gxt.ui.client.data.BeanModelTag;

public class LicenseKey implements Serializable, BeanModelTag {

	private static final long serialVersionUID = -5873701818313493221L;

	private String key;
	private boolean registered;
	private long startTime;
	private long endTime;
	private int account;
	private long remainingDays;
	private LicenseKeyType keyType;
	private List<LicensedMachine> machines = new ArrayList<LicensedMachine>();
	public LicenseKey() {

	}

	public void setRemainingDays(long remainingDays) {
		this.remainingDays = remainingDays;
	}

	public LicenseKey(String key, boolean registered, long startTime,
			long endTime, int account, LicenseKeyType keyType) {
		super();
		this.key = key;
		this.registered = registered;
		this.startTime = startTime;
		this.endTime = endTime;
		this.account = account;
		this.keyType = keyType;
	}

	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public boolean isRegistered() {
		return registered;
	}
	public void setRegistered(boolean registered) {
		this.registered = registered;
	}
	public long getRemainingDays(){

		return this.remainingDays;
	}

	protected LicenseKey copy() {
		LicenseKey t = new LicenseKey();
		t.setKey(this.getKey());
		t.setRegistered(isRegistered());
		t.setAccount(getAccount());
		t.setStartTime(getStartTime());
		t.setEndTime(getEndTime());

		Date now = new Date();
		long nowLong = now.getTime();
		long days = (this.getEndTime()-nowLong)/(24 * 60 * 60 * 1000);
		this.setRemainingDays(days);
		t.setRemainingDays(this.getRemainingDays());
		t.setKeyType(getKeyType());
		{
			t.machines= new ArrayList<LicensedMachine>();
			for(LicensedMachine m : this.machines)
			t.machines.add(m.copy());
		}
		return t;
	}
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	public int getAccount() {
		return account;
	}
	public void setAccount(int account) {
		this.account = account;
	}
	public LicenseKeyType getKeyType() {
		return keyType;
	}
	public void setKeyType(LicenseKeyType keyType) {
		this.keyType = keyType;
	}

	public List<LicensedMachine> getMachines() {
		return machines;
	}

	public void setMachines(List<LicensedMachine> machines) {
		this.machines = machines;
	}

}
