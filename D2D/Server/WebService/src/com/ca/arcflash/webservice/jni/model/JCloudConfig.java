package com.ca.arcflash.webservice.jni.model;

public class JCloudConfig {
	private long VendorType;
	private String VendorURL;
	
	private String VendorUsername;
	private String VendorPassword;
	
	private String VendorCertificatePath;
	private String CertificatePassword;
	
	private String VendorHostname;
	private long VendorPort;
	
	private String strCloudBucketName;
	
	private boolean useProxy;
	private String proxyServerName;
	private long proxyServerPort;
	private boolean proxyRequiresAuth;
	private String proxyUserName;
	private String proxyPassword;
		
	public void setVendorType(long in_iVendorType) {
		this.VendorType = in_iVendorType;
	}
	public long getVendorType() {
		return VendorType;
	}
	
	public void setVendorURL(String in_strVendorURL) {
		this.VendorURL = in_strVendorURL;
	}
	public String getVendorURL() {
		return VendorURL;
	}	
	
	public void setVendorUsername(String in_strVendorUsername) {
		this.VendorUsername = in_strVendorUsername;
	}
	public String getVendorUsername() {
		return VendorUsername;
	}
	
	public void setVendorPassword(String in_strVendorPassword) {
		this.VendorPassword = in_strVendorPassword;
	}
	public String getVendorPassword() {
		return VendorPassword;
	}
	
	public void setVendorCertificatePath(String in_strVendorCertificatePath) {
		this.VendorCertificatePath = in_strVendorCertificatePath;
	}
	public String getVendorCertificatePath() {
		return VendorCertificatePath;
	}
	
	public void setCertificatePassword(String in_strCertificatePassword) {
		this.CertificatePassword = in_strCertificatePassword;
	}
	public String getCertificatePassword() {
		return CertificatePassword;
	}
	
	public void setVendorHostname(String in_strVendorHostname) {
		this.VendorHostname = in_strVendorHostname;
	}
	public String getVendorHostname() {
		return VendorHostname;
	}
	
	public void setVendorPort(long in_iVendorPort) {
		this.VendorPort = in_iVendorPort;
	}
	public long getVendorPort() {
		return VendorPort;
	}
	
	public void setStrCloudBucketName(String strCloudBucketName) {
		this.strCloudBucketName = strCloudBucketName;
	}
	public String getStrCloudBucketName() {
		return strCloudBucketName;
	}
	public void setUseProxy(boolean useProxy) {
		this.useProxy = useProxy;
	}
	public boolean isUseProxy() {
		return useProxy;
	}
	public void setProxyServerName(String proxyServerName) {
		this.proxyServerName = proxyServerName;
	}
	public String getProxyServerName() {
		return proxyServerName;
	}
	public void setProxyServerPort(long proxyServerPort) {
		this.proxyServerPort = proxyServerPort;
	}
	public long getProxyServerPort() {
		return proxyServerPort;
	}
	public void setProxyRequiresAuth(boolean proxyRequiresAuth) {
		this.proxyRequiresAuth = proxyRequiresAuth;
	}
	public boolean isProxyRequiresAuth() {
		return proxyRequiresAuth;
	}
	public void setProxyUserName(String proxyUserName) {
		this.proxyUserName = proxyUserName;
	}
	public String getProxyUserName() {
		return proxyUserName;
	}
	public void setProxyPassword(String proxyPassword) {
		this.proxyPassword = proxyPassword;
	}
	public String getProxyPassword() {
		return proxyPassword;
	}	
}
