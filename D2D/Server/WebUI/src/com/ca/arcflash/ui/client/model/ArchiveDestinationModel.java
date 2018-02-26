package com.ca.arcflash.ui.client.model;

import java.io.UnsupportedEncodingException;

import com.ca.arcflash.common.BucketNameEncoder;
import com.ca.arcflash.ui.client.model.rps.RpsHostModel;
import com.ca.arcflash.webservice.data.archive.ArchiveConfigurationConstants;
import com.extjs.gxt.ui.client.data.BaseModelData;

public class ArchiveDestinationModel extends BaseModelData {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8549833071743752339L;
	
	private ArchiveDiskDestInfoModel diskInfo;
	private ArchiveCloudDestInfoModel cloudConfig;
	private RpsHostModel rpsHostInfo;
	private static final String STRK_PolicyUUID="STRK_PolicyUUID";//only used for rps Archive
	private static final String FK_ARCHIVETYPE="FK_ARCHIVETYPE";//1 for file_archive_copy;2 for file_archive_delete;
	
	public static final String UNKNOWNPATH="";
	public static final String STRK_CatalogPath="STRK_CatalogPath";
	
	private Boolean encryption;
	private String encryptionPassword;
	
	//store the destination path in the case of filecopy from RPS. 
	//private String catalogFolderPath;
	private String catalogFolderUser;
	private String catalogFolderPassword;
	
	public ArchiveDiskDestInfoModel getArchiveDiskDestInfoModel()
	{
		return diskInfo;
	}
	
	public void setArchiveDiskDestInfoModel(ArchiveDiskDestInfoModel in_diskInfo)
	{
		this.diskInfo = in_diskInfo;

	}
	
	public RpsHostModel getRpsHostModel(){
		return this.rpsHostInfo;
	}
	public void setRpsHostModel(RpsHostModel rpsHostInfo){
		this.rpsHostInfo=rpsHostInfo;
	}
	
	
	public Boolean getArchiveToDrive()
	{
		return (Boolean)get("ArchiveToDrive");
	}
	
	public void setArchiveToDrive(Boolean in_bArchiveToDrive)
	{
		set("ArchiveToDrive",in_bArchiveToDrive);
	}
	
	/*public String getArchiveToDrivePath()
	{
		return get("ArchiveToDrivePath");
	}
	
	public void setArchiveToDrivePath(String in_strArchiveToDrivePath)
	{
		set("ArchiveToDrivePath",in_strArchiveToDrivePath);
	}
	
	public String getDestinationPathUserName()
	{
		return get("DestinationPathUserName");
	}
	
	public void setDestinationPathUserName(String in_strDestinationPathUserName)
	{
		set("DestinationPathUserName",in_strDestinationPathUserName);
	}
	
	public String getDestinationPathPassword()
	{
		return get("DestinationPathPassword");
	}
	
	public void setDestinationPathPassword(String in_strDestinationPathPassword)
	{
		set("DestinationPathPassword",in_strDestinationPathPassword);
	}
	*/
	public Boolean getArchiveToCloud()
	{
		return (Boolean)get("ArchiveToCloud");
	}
	
	public void setArchiveToCloud(Boolean in_bArchiveToCloud)
	{
		set("ArchiveToCloud",in_bArchiveToCloud);
	}
	
	public Boolean getArchiveToRPS()
	{
		return (Boolean)get("ArchiveToRPS");
	}
	
	public void setArchiveToRPS(Boolean in_bArchiveToRPS)
	{
		set("ArchiveToRPS",in_bArchiveToRPS);
	}
	
	
	public ArchiveCloudDestInfoModel getCloudConfigModel()
	{
		return cloudConfig;
	}
	
	public void setCloudConfigModel(ArchiveCloudDestInfoModel in_CloudConfigModel)
	{
		this.cloudConfig = in_CloudConfigModel;
	}
	
	public String getHostName()
	{
		return get("HostName");
	}
	
	public void setHostName(String in_Hostname)
	{
		set("HostName",in_Hostname);
	}
	
	public void setArchiveType(int type){
		set(FK_ARCHIVETYPE,type);
	}
	
	public int getArchiveType(){
		return get(FK_ARCHIVETYPE);
	}
	
	public void setCatalogPath(String path){
		set(STRK_CatalogPath,path);
	}
	
	public String getCatalogPath(){
		return get(STRK_CatalogPath);
	}
	
	public void setPolicyUUID(String UUID){
		set(STRK_PolicyUUID,UUID);
	}
	
	public String getPolicyUUID(){
		return get(STRK_PolicyUUID);
	}
	
	
	public String getNodeHostname(){
		String hostname="";
		hostname=getHostName()==null?"":getHostName();
		return hostname;
	}
	
	public String getArchiveDestination(){
		String path="";
		 if(getArchiveToDrive())
			 path= diskInfo.getArchiveDiskDestPath();
	     else if(getArchiveToCloud()){
	    	 path=cloudConfig.getcloudBucketName();
	     }	
		else path=UNKNOWNPATH;
		 
		if(getArchiveToRPS()!=null)
			path="["+(getArchiveToRPS()?"rps":"local")+","+(getArchiveType()==1?"FileCopy":"FileArchive")+"]:"+path;
		 else 
			 path="[Manual]:"+path;
		return path;  
	}
	
	public Boolean getEncryption() {
		return encryption;
	}

	public void setEncryption(Boolean encryption) {
		this.encryption = encryption;
	}

	public String getEncryptionPassword() {
		return encryptionPassword;
	}

	public void setEncryptionPassword(String encryptionPassword) {
		this.encryptionPassword = encryptionPassword;
	}
	
	public boolean equals(ArchiveDestinationModel b){
		return this.getArchiveDestination().equalsIgnoreCase(b.getArchiveDestination());
	}
	
	/*public void setStorePath(String storePath){
		this.catalogFolderPath = storePath;
	}
	
	public String getStorePath(){
		return this.catalogFolderPath;
	}*/
	
	public void setCatalogFolderUser(String storeUser){
		this.catalogFolderUser = storeUser;
	}
	
	public String getCatalogFolderUser(){
		return this.catalogFolderUser;
	}
	
	public void setCatalogFolderPassword(String storePassword){
		this.catalogFolderPassword = storePassword;
	}
	
	public String getCatalogFolderPassword(){
		return this.catalogFolderPassword;
	}

}
