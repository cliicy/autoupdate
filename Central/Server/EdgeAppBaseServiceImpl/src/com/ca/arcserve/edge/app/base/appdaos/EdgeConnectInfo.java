package com.ca.arcserve.edge.app.base.appdaos;

import com.ca.arcflash.common.NotPrintAttribute;
import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;
import com.ca.arcserve.edge.app.base.webservice.contract.configuration.ProductType;

public class EdgeConnectInfo {
	private int hostid;
	private String username;
	private @NotPrintAttribute String password;
	private @NotPrintAttribute String uuid;
	private @NotPrintAttribute String authUuid;
	private int protocol;
	private int port;
	private int type;
	private String majorversion;
	private String minorversion;
	private String updateversionnumber;
	private String buildnumber;
	private int managed;
	private int rpsmanaged;
	private int status;
	private String rhostname;
	private String productType;
	private String osName;
	private boolean d2dInstalledByReg;
	private boolean rpsInstalledByReg;
	private boolean sqlServerByReg;
	private boolean msExchangeByReg;
	
	public boolean isD2DInstalled() {
		return ProductType.ProductD2D.equals(productType) || ProductType.ProductRPS.equals(productType);
	}
	public boolean isD2DODInstalled() {
		return ProductType.ProductD2DOD.equals(productType);
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getRhostname() {
		return rhostname;
	}

	public void setRhostname(String rhostname) {
		this.rhostname = rhostname;
	}

	public int getManaged() {
		return managed;
	}

	public void setManaged(int managed) {
		this.managed = managed;
	}

	public int getRpsmanaged() {
		return rpsmanaged;
	}
	
	public void setRpsmanaged(int rpsmanaged) {
		this.rpsmanaged = rpsmanaged;
	}
	
	public String getMajorversion() {
		return majorversion;
	}

	public void setMajorversion(String majorversion) {
		this.majorversion = majorversion;
	}

	public String getMinorversion() {
		return minorversion;
	}

	public void setMinorversion(String minorversion) {
		this.minorversion = minorversion;
	}

	public String getUpdateversionnumber() {
		return updateversionnumber;
	}

	public void setUpdateversionnumber(String updateversionnumber) {
		this.updateversionnumber = updateversionnumber;
	}
	
	public String getBuildnumber() {
		return buildnumber;
	}

	public void setBuildnumber(String buildnumber) {
		this.buildnumber = buildnumber;
	}

	public int getHostid() {
		return hostid;
	}

	public void setHostid(int hostid) {
		this.hostid = hostid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	@EncryptSave
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	@EncryptSave
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public int getProtocol() {
		return protocol;
	}

	public void setProtocol(int protocol) {
		this.protocol = protocol;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}
	
	public String getOsName() {
		return osName;
	}
	public void setOsName(String osName) {
		this.osName = osName;
	}
	public boolean isD2dInstalledByReg() {
		return d2dInstalledByReg;
	}
	public void setD2dInstalledByReg(boolean d2dInstalledByReg) {
		this.d2dInstalledByReg = d2dInstalledByReg;
	}
	public boolean isRpsInstalledByReg() {
		return rpsInstalledByReg;
	}
	public void setRpsInstalledByReg(boolean rpsInstalledByReg) {
		this.rpsInstalledByReg = rpsInstalledByReg;
	}
	public boolean isSqlServerByReg() {
		return sqlServerByReg;
	}
	public void setSqlServerByReg(boolean sqlServerByReg) {
		this.sqlServerByReg = sqlServerByReg;
	}
	public boolean isMsExchangeByReg() {
		return msExchangeByReg;
	}
	public void setMsExchangeByReg(boolean msExchangeByReg) {
		this.msExchangeByReg = msExchangeByReg;
	}
	@EncryptSave
	public String getAuthUuid() {
		return authUuid;
	}
	public void setAuthUuid(String authUuid) {
		this.authUuid = authUuid;
	}

	
}
