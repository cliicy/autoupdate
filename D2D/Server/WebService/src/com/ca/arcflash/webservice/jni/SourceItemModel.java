package com.ca.arcflash.webservice.jni;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;

/**
 * typedef struct _t_ha_src_item
{
    wchar_t* pwszPath;
    wchar_t* pwszSFUsername;    // If path is share folder, the user name to access it
    wchar_t* pwszSFPassword;    // If path is share folder, the user password to access it
} HA_SRC_ITEM, PHA_SRC_ITEM;

 * @author gonro07
 *
 */
public class SourceItemModel {
	private String pwszPath;
	private String pwszSFUsername;
	private String pwszSFPassword;
	private int diskCount;
	private List<FileItemModel> files;
	
	@XmlElementWrapper(name="D2DFiles")
	@XmlElements({
		@XmlElement(name="D2DFile",type=FileItemModel.class)
	})
	public List<FileItemModel> getFiles() {
		return files;
	}
	public void setFiles(List<FileItemModel> files) {
		this.files = files;
	}
	public int getDiskCount() {
		return diskCount;
	}
	public void setDiskCount(int diskCount) {
		this.diskCount = diskCount;
	}
	public void setPwszPath(String pwszPath) {
		this.pwszPath = pwszPath;
	}
	public void setPwszSFUsername(String pwszSFUsername) {
		this.pwszSFUsername = pwszSFUsername;
	}
	public void setPwszSFPassword(String pwszSFPassword) {
		this.pwszSFPassword = pwszSFPassword;
	}
	public String getPwszPath() {
		return pwszPath;
	}
	public String getPwszSFUsername() {
		return pwszSFUsername;
	}
	public String getPwszSFPassword() {
		return pwszSFPassword;
	}
	
}
