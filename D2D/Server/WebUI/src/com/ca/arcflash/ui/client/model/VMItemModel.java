package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.widget.form.CheckBox;

public class VMItemModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8205249829472288983L;

	//TODO CheckBox-2
	public boolean isVmChoose() {
		return (Boolean) get("vmChoose");
	}

	public void setVmChoose(boolean vmChoose) {
		set("vmChoose", vmChoose);
	}

	public void setVmName(String vmName) {
		set("vmName", vmName);
	}

	public String getVmName() {
		return (String) get("vmName");
	}

	public void setEsxServer(String esxServer) {
		set("esxServer", esxServer);
	}

	public String getEsxServer() {
		return (String) get("esxServer");
	}

	public void setUsername(String username) {
		set("username", username);
	}

	public String getUsername() {
		return (String) get("username");
	}

	public void setPassword(String password) {
		set("password", password);
	}

	public String getPassword() {
		return (String) get("password");
	}

	public void setDestination(String destination) {
		set("destination", destination);
	}

	public String getDestination() {
		return (String) get("destination");
	}
	
	public void setDesUsername(String desUsername){
		set("desUsername",desUsername);
	}
	
	public String getDesUsername(){
		return (String)get("desUsername");
	}
	
	public void setDesPassword(String desPassword){
		set("desPassword",desPassword);
	}
	
	public String getDesPassword(){
		return (String)get("desPassword");
	}

	public void setVMUUID(String vmUUID) {
		set("vmUUID", vmUUID);
	}

	public String getVMUUID() {
		return (String) get("vmUUID");
	}

	public void setVmVMX(String vmVMX) {
		set("vmVMX", vmVMX);
	}

	public String getVmVMX() {
		return (String) get("vmVMX");
	}

	public void setVmHostName(String vmHostName) {
		set("vmHostName", vmHostName);
	}

	public String getVmHostName() {
		return (String) get("vmHostName");
	}
	public void setVmInstanceUUID(String vmInstanceUUID){
		set("vmInstanceUUID",vmInstanceUUID);
	}
	public String getVmInstanceUUID(){
		return (String)get("vmInstanceUUID");
	}
	
	public void setVMPowerState(Integer state){
		set("vmPowerState",state);
	}
	
	public Integer getVMPowerState(){
		return get("vmPowerState");
	}

}
