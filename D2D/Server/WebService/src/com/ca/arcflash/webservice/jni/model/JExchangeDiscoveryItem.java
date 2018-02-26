package com.ca.arcflash.webservice.jni.model;

import com.ca.arcflash.webservice.data.restore.ExchangeDiscoveryItem;

// for Exchange discovery item
public class JExchangeDiscoveryItem {
		
	// for server
	private int nExVersion;
	private String pwszObjClass;
	private String pwszVersion;
	
	// for storage group and EDB
	private String pwszOwnerSVR;
	private String pwszLogPath;
	private String pwszSysPath;
	private String pwszEDBFile;
	private int isRecovery;
	private int isPublic;
	
	// for all
	private String pwszDN;
	private String pwszName;
	private String pwszGUID;
	
	
	public int getnExVersion() {
		return nExVersion;
	}
	public void setnExVersion(int nExVersion) {
		this.nExVersion = nExVersion;
	}
	public String getPwszObjClass() {
		return pwszObjClass;
	}
	public void setPwszObjClass(String pwszObjClass) {
		this.pwszObjClass = pwszObjClass;
	}
	public String getPwszVersion() {
		return pwszVersion;
	}
	public void setPwszVersion(String pwszVersion) {
		this.pwszVersion = pwszVersion;
	}
	public String getPwszOwnerSVR() {
		return pwszOwnerSVR;
	}
	public void setPwszOwnerSVR(String pwszOwnerSVR) {
		this.pwszOwnerSVR = pwszOwnerSVR;
	}
	public String getPwszLogPath() {
		return pwszLogPath;
	}
	public void setPwszLogPath(String pwszLogPath) {
		this.pwszLogPath = pwszLogPath;
	}
	public String getPwszSysPath() {
		return pwszSysPath;
	}
	public void setPwszSysPath(String pwszSysPath) {
		this.pwszSysPath = pwszSysPath;
	}
	public String getPwszEDBFile() {
		return pwszEDBFile;
	}
	public void setPwszEDBFile(String pwszEDBFile) {
		this.pwszEDBFile = pwszEDBFile;
	}
	public int getIsRecovery() {
		return isRecovery;
	}
	public void setIsRecovery(int isRecovery) {
		this.isRecovery = isRecovery;
	}
	public int getIsPublic() {
		return isPublic;
	}
	public void setIsPublic(int isPublic) {
		this.isPublic = isPublic;
	}
	public String getPwszDN() {
		return pwszDN;
	}
	public void setPwszDN(String pwszDN) {
		this.pwszDN = pwszDN;
	}
	public String getPwszName() {
		return pwszName;
	}
	public void setPwszName(String pwszName) {
		this.pwszName = pwszName;
	}
	public String getPwszGUID() {
		return pwszGUID;
	}
	public void setPwszGUID(String pwszGUID) {
		this.pwszGUID = pwszGUID;
	}	
	
	// utilities methods
	public ExchangeDiscoveryItem Convert2ExchangeDiscoveryItem()
	{
		ExchangeDiscoveryItem item = null;
		JExchangeDiscoveryItem jItem = this;
		
		if (jItem != null)
		{
			item = new ExchangeDiscoveryItem();
			item.setName(jItem.getPwszName());
					
			item.setnExVersion(jItem.getnExVersion());
			item.setPwszObjClass(jItem.getPwszObjClass());
			item.setPwszVersion(jItem.getPwszVersion());
			
			item.setPwszOwnerSVR(jItem.getPwszOwnerSVR());
			item.setPwszLogPath(jItem.getPwszLogPath());
			item.setPwszSysPath(jItem.getPwszSysPath());
			item.setPwszEDBFile(jItem.getPwszEDBFile());
			
			item.setIsPublic(jItem.getIsPublic());
			item.setIsRecovery(jItem.getIsRecovery());
			
			item.setPwszDN(jItem.getPwszDN());
			item.setPwszName(jItem.getPwszName());
			item.setPwszGUID(jItem.getPwszGUID());			
		}
		
		return item;
	}
}
