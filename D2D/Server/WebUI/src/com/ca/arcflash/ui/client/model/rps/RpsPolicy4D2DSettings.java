package com.ca.arcflash.ui.client.model.rps;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class RpsPolicy4D2DSettings extends BaseModelData {
	private static final long serialVersionUID = -5781201190375710315L;

	public Integer getPolicyid() {
		return get("policyid");
	}

	public void setPolicyid(Integer policyid) {
		set("policyid", policyid);
	}

	public String getId() {
		return get("policyUUID");
	}

	public void setId(String id) {
		set("policyUUID", id);
	}

	public String getPolicyName() {
		return get("policyName");
	}

	public void setPolicyName(String name) {
		set("policyName", name);
	}

	public Integer getDataStoreId() {
		return get("dataStoreId");
	}

	public void setDataStoreId(Integer dataStoreId) {
		set("dataStoreId", dataStoreId);
	}

	public String getDataStoreName() {
		return get("dataStoreName");
	}

	public void setDataStoreName(String dataStoreName) {
		set("dataStoreName", dataStoreName);
	}

	public String getDataStoreDisplayName() {
		return get("dataStoreDisplayName");
	}

	public void setDataStoreDisplayName(String dataStoreDisplayName) {
		set("dataStoreDisplayName", dataStoreDisplayName);
	}

	public Boolean isEnableGDD() {
		return get("enableGDD");
	}

	public void setEnableGDD(Boolean enableGDD) {
		set("enableGDD", enableGDD);
	}
	
	public void setStorePath(String StorePath){
		set("StorePath", StorePath);
	}
	
	public String getStorePath(){
		return get("StorePath");
	}
	
	public void setStoreUser(String StoreUser){
		set("StoreUser", StoreUser);
	}
	
	public String getStoreUser(){
		return get("StoreUser");
	}
	
	public void setStorePassword(String StorePassword){
		set("StorePassword", StorePassword);
	}
	
	public String getStorePassword(){
		return get("StorePassword");
	}
	
	public void setEnableEncryption(Boolean EnableEncryption){
		set("EnableEncryption", EnableEncryption);
	}
	
	public Boolean getEnableEncryption(){
		return get("EnableEncryption");
	}
	
	public void setEncryptionMethod(Integer EncryptionMethod){
		set("EncryptionMethod", EncryptionMethod);
	}
	
	public Integer getEncryptionMethod(){
		return get("EncryptionMethod");
	}
	
	public void setEncryptionPwd(String EncryptionPwd){
		set("EncryptionPwd", EncryptionPwd);
	}
	
	public String getEncryptionPwd(){
		return get("EncryptionPwd");
	}
	
	public void setEnableCompression(Boolean EnableCompression){
		set("EnableCompression", EnableCompression);
	}
	
	public Boolean getEnableCompression(){
		return (Boolean)get("EnableCompression");
	}
	
	public void setCompressionMethod(Integer CompressionMethod){
		set("CompressionMethod", CompressionMethod);
	}
	
	public Integer getCompressionMethod(){
		return get("CompressionMethod");
	}

	public void setEnableReplication(Boolean replication) {
		set("Replication", replication);
	}
	
	public Boolean isEnableReplication() {
		return get("Replication");
	}
	
	public void setDataStoreSharedPath(String storePath){
		set("DataStoreSharedPath", storePath);
	}
	
	public String getDataStoreSharedPath(){
		return get("DataStoreSharedPath");
	}
	
	public int getRetentionCount() {
		return get("RetentionCount");
	}
	public void setRetentionCount(Integer retentionCount) {
		set("RetentionCount", retentionCount);
	}
	public int getDailyCount() {
		return get("DailyCount");
	}
	public void setDailyCount(Integer dailyCount) {
		set("DailyCount", dailyCount);
	}
	public int getWeeklyCount() {
		return get("WeeklyCount");
	}
	public void setWeeklyCount(Integer weeklyCount) {
		set("WeeklyCount", weeklyCount);
	}
	public int getMonthlyCount() {
		return get("MonthlyCount");
	}
	public void setMonthlyCount(Integer monthlyCount) {
		set("MonthlyCount", monthlyCount);
	}
	
	public void setDataStoreStatus(Long status){
		set("dataStoreStatus", status);
	}
	
	public Long getDataStoreStatus(){
		return (Long)get("dataStoreStatus");
	}
	
	public void copy(RpsPolicy4D2DSettings model){
		setCompressionMethod(model.getCompressionMethod());
		setDataStoreDisplayName(model.getDataStoreDisplayName());
		setDataStoreId(model.getDataStoreId());
		setDataStoreName(model.getDataStoreName());
		setEnableCompression(model.getEnableCompression());
		setEnableEncryption(model.getEnableEncryption());
		setEnableGDD(model.isEnableGDD());
		setEnableReplication(model.isEnableReplication());
		setEncryptionMethod(model.getEncryptionMethod());
		setEncryptionPwd(model.getEncryptionPwd());
		setId(model.getId());
		setPolicyid(model.getPolicyid());
		setPolicyName(model.getPolicyName());
		setStorePassword(model.getStorePassword());
		setStorePath(model.getStorePath());
		setStoreUser(model.getStoreUser());
		setDataStoreSharedPath(model.getDataStoreSharedPath());
		setRetentionCount(model.getRetentionCount());
		setDataStoreStatus(model.getDataStoreStatus());
	}
}
