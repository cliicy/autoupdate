package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class SRMAlertSettingModel extends BaseModelData {
	
	private static final long serialVersionUID = 4897616183214900305L;
	
	public SRMAlertSettingModel() {
		getDefaultValue();
	}
	
	public void getDefaultValue() {
		this.setValidsrm(true);
		this.setUseglobalpolicy(true);
		this.setValidpkiutl(true);
		this.setValidalert(false);
		
		this.setCpuinterval(90);
		this.setCputhreshold(85);
		this.setCpusampleamount(5);
		this.setCpumaxalertnum(5);
		
		this.setMemoryinterval(90);
		this.setMemorythreshold(85);
		this.setMemorysampleamount(5);
		this.setMemorymaxalertnum(5);
		
		this.setDiskinterval(60);
		this.setDiskthreshold(50);
		this.setDisksampleamount(5);
		this.setDiskmaxalertnum(5);
		
		this.setNetworkinterval(90);
		this.setNetworkthreshold(60);
		this.setNetworksampleamount(5);
		this.setNetworkmaxalertnum(5);
		
		this.setUpdatetime( System.currentTimeMillis() );
	}
	
	public Long getUpdatetime() {
		return get("updatetime");
	}
	public void setUpdatetime(Long updatetime) {
		set("updatetime", updatetime);
	}
	public Integer getCpuinterval() {
		return get("cpuinterval");
	}
	public void setCpuinterval(Integer cpuinterval) {
		set("cpuinterval", cpuinterval);
	}
	public Integer getCputhreshold() {
		return get("cputhreshold");
	}
	public void setCputhreshold(Integer cputhreshold) {
		set("cputhreshold", cputhreshold);
	}
	public Integer getCpusampleamount() {
		return get("cpusampleamount");
	}
	public void setCpusampleamount(Integer cpusampleamount) {
		set("cpusampleamount", cpusampleamount);
	}
	public Integer getCpumaxalertnum() {
		return get("cpumaxalertnum");
	}
	public void setCpumaxalertnum(Integer cpumaxalertnum) {
		set("cpumaxalertnum", cpumaxalertnum);
	}
	public Integer getMemoryinterval() {
		return get("memoryinterval");
	}
	public void setMemoryinterval(Integer memoryinterval) {
		set("memoryinterval", memoryinterval);
	}
	public Integer getMemorythreshold() {
		return get("memorythreshold");
	}
	public void setMemorythreshold(Integer memorythreshold) {
		set("memorythreshold", memorythreshold);
	}
	public Integer getMemorysampleamount() {
		return get("memorysampleamount");
	}
	public void setMemorysampleamount(Integer memorysampleamount) {
		set("memorysampleamount", memorysampleamount);
	}
	public Integer getMemorymaxalertnum() {
		return get("memorymaxalertnum");
	}
	public void setMemorymaxalertnum(Integer memorymaxalertnum) {
		set("memorymaxalertnum", memorymaxalertnum);
	}
	public Integer getDiskinterval() {
		return get("diskinterval");
	}
	public void setDiskinterval(Integer diskinterval) {
		set("diskinterval", diskinterval);
	}
	public Integer getDiskthreshold() {
		return get("diskthreshold");
	}
	public void setDiskthreshold(Integer diskthreshold) {
		set("diskthreshold", diskthreshold);
	}
	public Integer getDisksampleamount() {
		return get("disksampleamount");
	}
	public void setDisksampleamount(Integer disksampleamount) {
		set("disksampleamount", disksampleamount);
	}
	public Integer getDiskmaxalertnum() {
		return get("diskmaxalertnum");
	}
	public void setDiskmaxalertnum(Integer diskmaxalertnum) {
		set("diskmaxalertnum", diskmaxalertnum);
	}
	public Integer getNetworkinterval() {
		return get("networkinterval");
	}
	public void setNetworkinterval(Integer networkinterval) {
		set("networkinterval", networkinterval);
	}
	public Integer getNetworkthreshold() {
		return get("networkthreshold");
	}
	public void setNetworkthreshold(Integer networkthreshold) {
		set("networkthreshold", networkthreshold);
	}
	public Integer getNetworksampleamount() {
		return get("networksampleamount");
	}
	public void setNetworksampleamount(Integer networksampleamount) {
		set("networksampleamount", networksampleamount);
	}
	public Integer getNetworkmaxalertnum() {
		return get("networkmaxalertnum");
	}
	public void setNetworkmaxalertnum(Integer networkmaxalertnum) {
		set("networkmaxalertnum", networkmaxalertnum);
	}
	public Boolean isUseglobalpolicy() {
		return get("useglobalpolicy");
	}
	public void setUseglobalpolicy(Boolean useglobalpolicy) {
		set("useglobalpolicy", useglobalpolicy);
	}
	public Integer getAppliedstatus() {
		return get("appliedstatus");
	}
	public void setAppliedstatus(Integer appliedstatus) {
		set("appliedstatus", appliedstatus);
	}
	public Integer getAppliedcode() {
		return get("appliedcode");
	}
	public void setAppliedcode(Integer appliedcode) {
		set("appliedcode", appliedcode);
	}
	public Boolean isValidsrm() {
		return get("validsrm");
	}
	public void setValidsrm(Boolean validsrm) {
		set("validsrm", validsrm);
	}
	public Boolean isValidalert() {
		return get("validalert");
	}
	public void setValidalert(Boolean validalert) {
		set("validalert", validalert);
	}
	public Boolean isValidpkiutl() {
		return get("validpkiutl");
	}
	public void setValidpkiutl(Boolean validpkiutl) {
		set("validpkiutl", validpkiutl);
	}

}
