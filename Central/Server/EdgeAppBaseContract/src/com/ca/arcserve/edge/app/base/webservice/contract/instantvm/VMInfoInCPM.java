package com.ca.arcserve.edge.app.base.webservice.contract.instantvm;

import java.io.Serializable;
import java.util.List;

import com.ca.arcserve.edge.app.base.webservice.contract.node.DNSUpdateSetting;

public class VMInfoInCPM implements Serializable {
	private static final long serialVersionUID = 1L;
	
//	public static int MICROSOFT_DNS = 1;
//	public static int BIND_SERVER = 2;
	
	private boolean needpowerOn;
	
	private String vmNamePrefix;  // prefix of the display name
	private long memorySize;  // in MB
	private long CPU;
	private String vmConfigPath;  // VM config path, for VMWare, it is the nfs path.
	private String vmPath;
	private String vmUUID;
	private String vmDisplayName;
	private List<AdapterForInstantVM> adapterSetting;
	private String datastoreName;
	private String description;
	private String hostname;
	private String username; // for change hostname, the machine in domain
	private String password; // for change hostname, the machine in domain
	
	//for update DNS
//	private int timeToLive;
//	private int dnsType;
//	private boolean needUpdateDNS;
//	private String keyFilePath;
//	private String dnsAccount;
//	private String dnsPassword;
//	private List<DNSUpdateSetting> dnsList;
	
	private DNSInfoForIVM updateDNSInfo;
	
	public boolean isNeedpowerOn() {
		return needpowerOn;
	}
	public void setNeedpowerOn(boolean needpowerOn) {
		this.needpowerOn = needpowerOn;
	}

	public String getDatastoreName() {
		return datastoreName;
	}
	public void setDatastoreName(String datastoreName) {
		this.datastoreName = datastoreName;
	}
	public long getMemorySize() {
		return memorySize;
	}
	public void setMemorySize(long memorySize) {
		this.memorySize = memorySize;
	}
	public long getCPU() {
		return CPU;
	}
	public void setCPU(long cPU) {
		CPU = cPU;
	}
	public String getVmConfigPath() {
		return vmConfigPath;
	}
	public void setVmConfigPath(String vmConfigPath) {
		this.vmConfigPath = vmConfigPath;
	}
	public String getVmNamePrefix() {
		return vmNamePrefix;
	}
	public void setVmNamePrefix(String vmNamePrefix) {
		this.vmNamePrefix = vmNamePrefix;
	}
	public String getVmPath() {
		return vmPath;
	}
	public void setVmPath(String vmPath) {
		this.vmPath = vmPath;
	}
	public String getVmUUID() {
		return vmUUID;
	}
	public void setVmUUID(String vmUUID) {
		this.vmUUID = vmUUID;
	}
	public List<AdapterForInstantVM> getAdapterSetting() {
		return adapterSetting;
	}
	public void setAdapterSetting(List<AdapterForInstantVM> adapterSetting) {
		this.adapterSetting = adapterSetting;
	}
	public String getVmDisplayName() {
		return vmDisplayName;
	}
	public void setVmDisplayName(String vmDisplayName) {
		this.vmDisplayName = vmDisplayName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
//	public int getTimeToLive() {
//		return timeToLive;
//	}
//	public void setTimeToLive(int timeToLive) {
//		this.timeToLive = timeToLive;
//	}
//	public int getDnsType() {
//		return dnsType;
//	}
//	public void setDnsType(int dnsType) {
//		this.dnsType = dnsType;
//	}
//	public boolean isNeedUpdateDNS() {
//		return needUpdateDNS;
//	}
//	public void setNeedUpdateDNS(boolean needUpdateDNS) {
//		this.needUpdateDNS = needUpdateDNS;
//	}
//	public String getKeyFilePath() {
//		return keyFilePath;
//	}
//	public void setKeyFilePath(String keyFilePath) {
//		this.keyFilePath = keyFilePath;
//	}
//	public String getDnsAccount() {
//		return dnsAccount;
//	}
//	public void setDnsAccount(String dnsAccount) {
//		this.dnsAccount = dnsAccount;
//	}
//	public String getDnsPassword() {
//		return dnsPassword;
//	}
//	public void setDnsPassword(String dnsPassword) {
//		this.dnsPassword = dnsPassword;
//	}
//	public List<DNSUpdateSetting> getDnsList() {
//		return dnsList;
//	}
//	public void setDnsList(List<DNSUpdateSetting> dnsList) {
//		this.dnsList = dnsList;
//	}
	public DNSInfoForIVM getUpdateDNSInfo() {
		return updateDNSInfo;
	}
	public void setUpdateDNSInfo(DNSInfoForIVM updateDNSInfo) {
		this.updateDNSInfo = updateDNSInfo;
	}
	
	
}
