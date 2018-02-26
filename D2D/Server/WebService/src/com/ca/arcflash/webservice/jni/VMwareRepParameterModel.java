package com.ca.arcflash.webservice.jni;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;

import com.ca.arcflash.webservice.data.VMwareConnParams;

public class VMwareRepParameterModel {
	
	private String pwszEsxHostName;
	private String pwszEsxUserName;
	private String pwszEsxPassword;
	private String pwszMoref;	
	private int ulVDDKPort;
	private int isSAN;
	private String snapshotUrl;
	private String pwszEsxThumbprint;
	
	public VMwareRepParameterModel() {
	}
	
	public VMwareRepParameterModel( String pwszEsxHostName,
									String pwszEsxUserName,
									String pwszEsxPassword,
									String pwszMoref,
									int ulVDDKPort,
									VMwareConnParams exParams) {
		this.pwszEsxHostName = pwszEsxHostName;
		this.pwszEsxUserName = pwszEsxUserName;
		this.pwszEsxPassword = pwszEsxPassword;
		this.pwszMoref = pwszMoref;
		this.ulVDDKPort = ulVDDKPort;
		this.pwszEsxThumbprint = exParams.getThumbprint();
	}
	
	private List<FileItemModel> files;
	
	@XmlElements({
		@XmlElement(name="D2DFile",type=FileItemModel.class)
	})
	@XmlElementWrapper(name="D2DFileList")
	public List<FileItemModel> getFiles() {
		return files;
	}

	public void setFiles(List<FileItemModel> files) {
		this.files = files;
	}

	public String getPwszEsxHostName() {
		return pwszEsxHostName;
	}

	public void setPwszEsxHostName(String pwszEsxHostName) {
		this.pwszEsxHostName = pwszEsxHostName;
	}

	public String getPwszEsxUserName() {
		return pwszEsxUserName;
	}

	public void setPwszEsxUserName(String pwszEsxUserName) {
		this.pwszEsxUserName = pwszEsxUserName;
	}

	public String getPwszEsxPassword() {
		return pwszEsxPassword;
	}

	public void setPwszEsxPassword(String pwszEsxPassword) {
		this.pwszEsxPassword = pwszEsxPassword;
	}
	
	public String getpwszEsxThumbprint() {
		return pwszEsxThumbprint;
	}

	public void setpwszEsxThumbprint(String pwszEsxThumbprint) {
		this.pwszEsxThumbprint = pwszEsxThumbprint;
	}

	public String getPwszMoref() {
		return pwszMoref;
	}

	public void setPwszMoref(String pwszMoref) {
		this.pwszMoref = pwszMoref;
	}

	public int getUlVDDKPort() {
		return ulVDDKPort;
	}

	public void setUlVDDKPort(int ulVDDKPort) {
		this.ulVDDKPort = ulVDDKPort;
	}

	public int getIsSAN() {
		return isSAN;
	}

	public void setIsSAN(int isSAN) {
		this.isSAN = isSAN;
	}

	public String getSnapshotUrl() {
		return snapshotUrl;
	}

	public void setSnapshotUrl(String snapshotUrl) {
		this.snapshotUrl = snapshotUrl;
	}

}
