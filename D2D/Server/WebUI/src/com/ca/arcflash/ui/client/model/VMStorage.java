package com.ca.arcflash.ui.client.model;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class VMStorage extends BaseModelData implements Comparable<VMStorage> {
	
	public String getName() {
		return get("name");
	}
	
	public void setName(String name) {
		set("name", name);
	}
	
	public String getId() {
		return get("id");
	}
	
	public void setId(String id) {
		set("id", id);
	}
	
	public Long getFreeSize() { //bytes
		return get("freeSize");
	}
	
	public void setFreeSize(long freeSize) { //bytes
		set("freeSize", freeSize);
	}
	
	public Long getTotalSize() {
		return get("totalSize");
	}
	
	public void setTotalSize(long totalSize) {
		set("totalSize", totalSize);
	}
	
	public Long getOtherSize(){
		return get("otherSize");
	}
	
	public void setOtherSize(long otherSize){
		set("otherSize",otherSize);
	}
	
	public Boolean getAccessible(){
		return get("accessible");
	}
	
	public void setAccessible(boolean accessible){
		set("accessible",accessible);
	}
	
	public String getDisplayName(){
		return get("displayName");
	}
	
	public void setDisplayName(String displayName){
		set("displayName",displayName);
	}
	
	public String getMoRef() {
		return get("moRef");
	}
	
	public void setMoRef(String moRef) {
		set("moRef", moRef);
	}
	
	public List<DiskModel> diskList = new ArrayList<DiskModel>();

	@Override
	public int compareTo(VMStorage o) {
		// TODO Auto-generated method stub
		return getName().compareTo(o.getName());
	}
}
