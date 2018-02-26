package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class RestoreArchiveJobModel extends BaseModelData{
/**
	 * 
	 */
	private static final long serialVersionUID = -5623505982787442196L;

	private ArchiveDiskDestInfoModel archiveDiskInfo;
	private ArchiveCloudDestInfoModel archiveCloudInfo;
	public RestoreJobArchiveVolumeNodeModel[] listofArchiveVolumes;
	private FileSystemOptionModel fileSystemOption;
	private String encrpytionPassword;
	
	//store the destination path in the case of filecopy from RPS. 
	private String catalogFolderPath;
	private String catalogFolderUser;
	private String catalogFolderPassword;
	
	public String getEncrpytionPassword() {
		return encrpytionPassword;
	}
	public void setEncrpytionPassword(String encrpytionPassword) {
		this.encrpytionPassword = encrpytionPassword;
	}
	public void setArchiveCloudInfo(ArchiveCloudDestInfoModel in_cloudDestInfo){
		this.archiveCloudInfo = in_cloudDestInfo;
	}
	public ArchiveCloudDestInfoModel getArchiveCloudInfo(){
		return this.archiveCloudInfo;
	}
	public void setArchiveDiskInfo(ArchiveDiskDestInfoModel diskDestInfo){
		this.archiveDiskInfo = diskDestInfo;
	}
	public ArchiveDiskDestInfoModel getArchiveDiskInfo(){
		return this.archiveDiskInfo ;
	}
	
	public Long getArchiveDestType(){
		return (Long)get("ArchiveDestType");
	}
	public void setArchiveDestType(long dwArchiveDestType){
		set("ArchiveDestType",dwArchiveDestType);
	}
	
	public FileSystemOptionModel getFileSystemOption() {
		return fileSystemOption;
	}
	public void setFileSystemOption(FileSystemOptionModel fileSystemOption) {
		this.fileSystemOption = fileSystemOption;
	}

	public Integer getJobType() {
		return (Integer)get("jobType");
	}
	public void setJobType(Integer jobType) {
		set("jobType",jobType);
	}

	/*public RestoreJobArchiveNode[] getNodes() {
		return nodes;
	}
	public void setNodes(RestoreJobArchiveNode[] in_ArchiveNodes) {
		this.nodes = in_ArchiveNodes;
	}*/
	public String getSessionPath() {// in catalog sync job this param holds the hostname
		return get("sessionPath");
	}
	public void setSessionPath(String sessionPath) {
		set("sessionPath",sessionPath);
	}
	public String getarchiveRestoreDestinationPath() {
		return get("archiveRestoreDestinationPath");
	}
	public void setarchiveRestoreDestinationPath(String destinationPath) {
		set("archiveRestoreDestinationPath",destinationPath);
	}
	public String getarchiveUserName() {
		return get("archiveUserName");
	}
	public void setarchiveUserName(String userName) {
		set("archiveUserName",userName);
	}
	public String getarchivePassword() {
		return get("archivePassword");
	}
	public void setarchivePassword(String password) {
		set("archivePassword",password);
	}
	public Integer getRestoreType() {
		return (Integer)get("restoreType");
	}
	public void setRestoreType(int type) {
		set("restoreType",type);
	}
	
	public Integer getDestType() {
		return (Integer) get("destType");
	}

	public void setDestType(Integer DestType) {
		set("destType", DestType);
	}
	
	public void setProductType(Long productType){
		set("productType",productType);
	}
	
	public Long getProductType(){
		return get("productType");
	}
	
	public void setVMInstanceUUID(String vmInstanceUUID){
		set("vmInstanceUUID",vmInstanceUUID);
	}
	
	public String getVMInstanceUUID(){
		return get("vmInstanceUUID");
	}
	public long getRRSFlag() {
		return (Long)get("rrsFlag");
	}
	public void setRRSFlag(long rrsFlag) {
		set("rrsFlag", rrsFlag);
	}
	
/*	public RestoreJobArchiveVolumeNodeModel[] getlistofArchiveVolumes() {
		return get("listofArchiveVolumes");
	}

	public void setlistofArchiveVolumes(RestoreJobArchiveVolumeNodeModel[] in_volumesList) {
		set("listofArchiveVolumes", in_volumesList);
	}*/
	
	public void setCatalogFolderPath(String storePath){
		this.catalogFolderPath = storePath;
	}
	
	public String getCatalogFolderPath(){
		return this.catalogFolderPath;
	}
	
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
