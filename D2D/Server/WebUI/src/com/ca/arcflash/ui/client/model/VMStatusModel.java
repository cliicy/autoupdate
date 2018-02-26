package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class VMStatusModel extends BaseModelData {
	
	public static final int VM_STATUS_TYPE_WARNING = 0;
	public static final int VM_STATUS_TYPE_ERROR = 1;
	
	public static final int VM_STATUS_WARNING_TYPE_VIX = 0;
	public static final int VM_STATUS_WARNING_TYPE_VM_TOOL = 1;
	public static final int VM_STATUS_WARNING_TYPE_VM_POWER = 2;
	
	public static final int VM_STATUS_ERROR_TYPE_VC = 0;
	public static final int VM_STATUS_ERROR_TYPE_VCLOUD_DIRECTOR = 1;
	
	public static final int VM_STATUS_WARNING_VIX_STATUS_OK = 0;
	public static final int VM_STATUS_WARNING_VIX_STATUS_NOT_INSTALL = 1;
	public static final int VM_STATUS_WARNING_VIX_STATUS_OUT_OF_DATE = 2;
	
	public static final int VM_STATUS_WARNING_VM_TOOL_STATUS_ERROR = -1;
	public static final int VM_STATUS_WARNING_VM_TOOL_STATUS_NOT_INSTALL = 0;
	public static final int VM_STATUS_WARNING_VM_TOOL_STATUS_OUTOFDATE = 1;
	public static final int VM_STATUS_WARNING_VM_TOOL_STATUS_OK = 2;
	
	public static final int VM_STATUS_WARNING_VM_POWER_ERROR = -1;
	public static final int VM_STATUS_WARNING_VM_POWER_ON = 0;
	public static final int VM_STATUS_WARNING_VM_POWER_OFF = 1;
	public static final int VM_STATUS_WARNING_VM_SUSPENDED = 2;
	
	public static final int VM_STATUS_ERROR_VC_OK = 0;
	public static final int VM_STATUS_ERROR_VC_CREDENTIAL_WRONG = 1;
	public static final int VM_STATUS_ERROR_VC_CANNOT_CONNECT = 2;
	
	public void setSubType(Integer subType){
		set("subType",subType);
	}
	
	public Integer getSubType(){
		return get("subType");
	}
	
	public void setStatusType(Integer statusType){
		set("statusType",statusType);
	}
	
	public Integer getStatusType(){
		return get("statusType");
	}
	
	public void setStatus(Integer status){
		set("status",status);
	}
	
	public Integer getStatus(){
		return get("status");
	}
	
	public void setStatusParameter(String[] parameters){
		set("parameters",parameters);
	}
	
	public String[] getStatusParamter(){
		return get("parameters");
	}

}
