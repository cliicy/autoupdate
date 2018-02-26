package com.ca.arcflash.ui.client.common;

import java.io.Serializable;


public class Account implements Serializable {
	
	private static final long serialVersionUID = 3130467618100979804L;
	private String domain;
	private String username;
	private String password;
	
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
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
	
	public void setQualityUsername(String username){
		if(username==null)
			username="";
		int pos = username.indexOf("\\");
		if(pos>0){
			this.domain=username.substring(0,pos);
			this.username=username.substring(pos+1);
		}else{
			this.domain="";
			this.username=username;
		}
	}
	
	public String getQualityUsername(){
		if(domain!=null && !domain.isEmpty()){
			return domain+"\\"+username;
		}
		return username;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((domain == null) ? 0 : domain.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Account other = (Account) obj;
		if (domain == null) {
			if (other.domain != null)
				return false;
		} else if (!domain.equals(other.domain))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
}
