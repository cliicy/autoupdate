package com.ca.arcserve.edge.app.base.webservice.contract.instantvm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.ca.arcserve.edge.app.base.webservice.contract.node.DNSUpdateSetting;

public class DNSInfoForIVM implements Serializable  {
	
	private static final long serialVersionUID = 1L;
	
	public static int MICROSOFT_DNS = 0;
	public static int BIND_SERVER = 1;
	
	private int timeToLive;
	private int dnsType;
	private String keyFilePath;
	private String dnsAccount;
	private String dnsPassword;
	private List<DNSUpdateSetting> dnsList = new ArrayList<DNSUpdateSetting>();
	public int getTimeToLive() {
		return timeToLive;
	}
	public void setTimeToLive(int timeToLive) {
		this.timeToLive = timeToLive;
	}
	public int getDnsType() {
		return dnsType;
	}
	public void setDnsType(int dnsType) {
		this.dnsType = dnsType;
	}
	public String getKeyFilePath() {
		return keyFilePath;
	}
	public void setKeyFilePath(String keyFilePath) {
		this.keyFilePath = keyFilePath;
	}
	public String getDnsAccount() {
		return dnsAccount;
	}
	public void setDnsAccount(String dnsAccount) {
		this.dnsAccount = dnsAccount;
	}
	public String getDnsPassword() {
		return dnsPassword;
	}
	public void setDnsPassword(String dnsPassword) {
		this.dnsPassword = dnsPassword;
	}
	public List<DNSUpdateSetting> getDnsList() {
		return dnsList;
	}
	public void setDnsList(List<DNSUpdateSetting> dnsList) {
		this.dnsList = dnsList;
	}
	
	

}
