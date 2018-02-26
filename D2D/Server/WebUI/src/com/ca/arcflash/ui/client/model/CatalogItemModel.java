package com.ca.arcflash.ui.client.model;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class CatalogItemModel  extends BaseModelData{

	/**
	 * 
	 */
	
	public static final int TYPE_BACKUP = 0;
	public static final int TYPE_ARCHIVE = 1;
	
	private static final long serialVersionUID = 4736226777726861168L;

	public Integer getSessionNumber() {
		return (Integer) get("sessionNumber");
	}

	public void setSessionNumber(Integer sessionNumber) {
		set("sessionNumber", sessionNumber);
	}

	public Integer getSubSessionNumber() {
		return (Integer) get("subSessionNumber");
	}

	public void setSubSessionNumber(Integer subSessionNumber) {
		set("subSessionNumber", subSessionNumber);
	}
	
	public String getPath() {
		return (String) get("path");		
	}

	public void setPath(String path) {
		set("path", path);		
	}

	public Long getId() {
		return (Long) get("id");
	}

	public void setId(Long id) {
		set("id", id);
	}

	public Integer getType() {
		return (Integer) get("type");
	}

	public void setType(Integer type) {
		set("type", type);
	}

	public Long getSize() {
		return (Long) get("size");
	}

	public void setSize(Long size) {
		set("size", size);
	}

	public Date getDate() {
		return (Date) get("date");
	}

	public void setDate(Date date) {
		set("date", date);
	}

	public String getName() {
		return (String)get("name");
	}

	public void setName(String name) {
		set("name",name);
	}
	
	public String getFullPath()
	{
		return (String) get("fullPath");
	}
	public void setFullPath(String fullPath)
	{
		set("fullPath", fullPath);
	}
	
	public Boolean getChecked()
	{
		return (Boolean) get("checked");		
	}
	public void setChecked(Boolean checked)
	{
		set("checked", checked);
	}
	public String getComponentName()
	{
		return (String)get("componentName");
	}
	public void setComponentName(String name)
	{
		set("componentName", name);
	}

	public Long getChildrenCount() {
		return (Long) get("childrenCount");
	}

	public void setChildrenCount(Long childrenCount) {
		set("childrenCount", childrenCount);
	}
	public Boolean isEncrypted() {
		return (Boolean)get("isEncrypted");
	}

	public void setEncrypted(Boolean isEncrypted) {
		set("isEncrypted", isEncrypted);
	}

	public Date getBackupDate() {
		return (Date)get("backupDate");
	}

	public void setBackupDate(Date backupDate) {
		set("backupDate",backupDate);
	}
	
	public Long getBKServerTimeZoneOffset() {
		return (Long)get("BKServerTZOffset");
	}
	
	public void setBKServerTimeZoneOffset(Long tzOffset) {
		set("BKServerTZOffset", tzOffset);
	}

	public String getBackupJobName() {
		return get("backupJobName");
	}

	public void setBackupJobName(String backupJobName) {
		set("backupJobName", backupJobName);
	}

	public String getBackupDestination() {
		return get("backupDestination");
	}

	public void setBackupDestination(String backupDestination) {
		set("backupDestination", backupDestination);
	}

	public Long getFullSessionNumber() {
		return (Long)get("fullSessionNumber");
	}

	public void setFullSessionNumber(Long fullSessionNumber) {
		set("fullSessionNumber", fullSessionNumber);
	}
	public String getPasswordHash() {
		return get("passwordHash");
	}

	public void setPasswordHash(String passwordHash) {
		set("passwordHash", passwordHash);
	}
	
	public String getSessionGuid(){
		return get("sessionGuid");
	}
	public void setSessionGuid(String sessionGuid){
		set("sessionGuid", sessionGuid);
	}
	
	public String getFullSessionGuid(){
		return get("fullSessionGuid");
	}
	public void setFullSessionGuid(String fullSessionGuid){
		set("fullSessionGuid", fullSessionGuid);
	}

	public void setMsgRecModel(MsgSearchRecModel msgRecModel) {
		this.msgRecModel = msgRecModel;
	}

	public MsgSearchRecModel getMsgRecModel() {
		return msgRecModel;
	}

	private MsgSearchRecModel msgRecModel;
	
	public Integer getArchiveType() {
		return (Integer) get("ArchiveType");
	}

	public void setArchiveType(Integer in_ArchiveType) {
		set("ArchiveType", in_ArchiveType);
	}
	public Integer getArchiveVersion() {
		return (Integer) get("ArchiveVersion");
	}

	public void setArchiveVersion(Integer in_ArchiveVersion) {
		set("ArchiveVersion", in_ArchiveVersion);
	}
	
	public Integer getFoundInType()
	{
		return (Integer)get("FoundInType");
	}
	
	public void setFoundInType(Integer in_FoundInType)
	{
		set("FoundInType",in_FoundInType);
	}
	
	public Long getServerTimeZoneOffset() {
		return get("ServerTZOffset");
	}
	
	public void setServerTimeZoneOffset(Long tzOffset) {
		set("ServerTZOffset", tzOffset);
	}
	
	public void setVolAttr(Integer volAttr){
		set("volAttr",volAttr);
	}
	public Integer getVolAttr(){
		return (Integer)get("volAttr");
	}
	public void setDriverLetterAttr(Integer driverLetterAttr){
		set("driverLetterAttr",driverLetterAttr);
	}
	public Integer getDriverLetterAttr(){
		return (Integer)get("driverLetterAttr");
	}
	
	public Boolean isDefaultSessPwd()
	{
		return (Boolean) get("isDefaultSessPwd");		
	}
	public void setDefaultSessPwd(Boolean isDefaultSessPwd)
	{
		set("isDefaultSessPwd", isDefaultSessPwd);
	}
}
