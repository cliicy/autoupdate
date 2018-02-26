/**
 * 
 */
package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;
import java.util.Date;

import com.ca.arcserve.edge.app.base.webservice.annotations.EncryptSave;

/**
 * @author lijwe02
 * 
 */
public class SessionPassword implements Serializable {
	private static final long serialVersionUID = -9117994728541675450L;
	private int id;
	private int hostId;
	private String password;
	private String pwdComment;
	private Date createTime;
	private Date lastUpdateTime;
	private String encryptPasswordHash;
	private Boolean validate = null;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getHostId() {
		return hostId;
	}

	public void setHostId(int hostId) {
		this.hostId = hostId;
	}

	@EncryptSave
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPwdComment() {
		return pwdComment;
	}

	public void setPwdComment(String pwdComment) {
		this.pwdComment = pwdComment;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLastUpdateTime() {
		return lastUpdateTime;
	}

	public void setLastUpdateTime(Date lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	
	public String getEncryptPasswordHash() {
		return encryptPasswordHash;
	}

	public void setEncryptPasswordHash(String encryptPasswordHash) {
		this.encryptPasswordHash = encryptPasswordHash;
	}

	public Boolean getValidate() {
		return validate;
	}

	public void setValidate(Boolean validate) {
		this.validate = validate;
	}
}
