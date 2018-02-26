package com.ca.arcflash.ui.client.model;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class GridTreeNode extends BaseModelData
{
	public static final int SELECTION_TYPE_NONE = 0;
	public static final int SELECTION_TYPE_PARTIAL = 1;
	public static final int SELECTION_TYPE_FULL = 2;
	private static final long serialVersionUID = -3037512242125232582L;
    /**
     * to save guid for system reserved volume Robin
     * Fix issue 18906917
     * @return
     */
	
    public String getGuid(){
          return get("GUID");
    }
    public void setGuid(String guid){
           set("GUID",guid);
    }

	public Boolean getChecked()
	{
		return (Boolean) get("checked");
	}
	public void setChecked(Boolean check)
	{
		set("checked", check);
	}
	
	public String getName()
	{
		return (String)get("name");
	}
	public void setName(String name)
	{
		set("name", name);
	}
	
	public Long getSize()
	{
		return (Long)get("size");
	}
	
	public void setSize(Long size)
	{
		set("size", size);
	}
	
	public Date getDate()
	{
		return (Date)get("date");			
	}
	public void setDate(Date date)
	{
		set("date", date);
	}
	
	public Long getServerTZOffset() {
		return (Long)get("ServerTZOffset");
	}
	
	public void setServerTZOffset(Long offset) {
		set("ServerTZOffset", offset);
	}
	
	//
	public String getCatalogFilePath()
	{
		return (String)get("catalogFilePath");		
	}
	public void setCatalofFilePath(String path)
	{
		set("catalogFilePath", path);
	}
	
	public Long getParentID()
	{
		return (Long)get("parentID");		
	}
	public void setParentID(Long parentID)
	{
		set("parentID", parentID);
	}
	public Integer getType() {
		return (Integer) get("type");
	}

	public void setType(Integer type) {
		set("type", type);
	}
	
	
	//
	public Integer getSubSessionID() {
		return (Integer)get("subSessionID");
	}
	public void setSubSessionID(Integer subSessionID) {
		set("subSessionID", subSessionID);
	}
	
	public String getPath()
	{
		return (String) get("path");
	}
	public void setPath(String path)
	{
		set("path", path);
	}
	
	public String getFullPath()
	{
		return (String) get("fullPath");
	}
	public void setFullPath(String fullPath)
	{
		set("fullPath", fullPath);
	}
	
	public Boolean getPackage()
	{
		return (Boolean) get("package");
	}
	public void setPackage(Boolean b)
	{
		set("package", b);
	}
	
	public Boolean getSelectable()
	{
		return (Boolean) get("selectable");
	}
	public void setSelectable(Boolean selectable)
	{
		set("selectable", selectable);
	}
	
	public String getComponentName()
	{
		return (String)get("name");
	}
	public void setComponentName(String name)
	{
		set("name", name);
	}
	public void setDisplayName(String componentName) {
		set("displayName", componentName);		
	}
	public String getDisplayName()
	{
		return get("displayName");
	}

	public void setUserChecked(Boolean value) {
		set("userChecked", value);
	}

	public Boolean isUserChecked() {
		return get("userChecked");
	}

	public Long getChildrenCount() {
		return (Long) get("childrenCount");
	}

	public void setChildrenCount(Long childrenCount) {
		set("childrenCount", childrenCount);
	}

	public String getDestUser() {
		return (String)get("DestUser");
	}
	
	public void setDestUser(String userName) {
		set("DestUser", userName);
	}
	
	public String getDestPwd() {
		return (String)get("DestPwd");
	}
	
	public void setDestPwd(String pwd) {
		set("DestPwd", pwd);
	}
	
	@Override
	public int hashCode() {
		return getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}

		if (obj instanceof GridTreeNode) {
			GridTreeNode another = (GridTreeNode) obj;
			boolean isEqual = this.toId().equalsIgnoreCase(another.toId());

			if (isEqual) {
				if (this.getGrtCatalogItemModel() == null
						&& another.getGrtCatalogItemModel() == null) {
					isEqual = true;

				} else if ((this.getGrtCatalogItemModel() != null && another
						.getGrtCatalogItemModel() == null)
						|| (this.getGrtCatalogItemModel() == null && another
								.getGrtCatalogItemModel() != null)) {
					isEqual = false;
				} else {
					isEqual = this.getGrtCatalogItemModel().equals(
							another.getGrtCatalogItemModel());
				}
			}
			return isEqual;
		} else {
			return false;
		}
	}

	public String toId() {
		StringBuilder key = new StringBuilder("");
		if (this.getParentID() != null) {
			key.append("@");
			key.append(this.getParentID());
		}
		if (this.getSubSessionID() != null) {
			key.append("@");
			key.append(this.getSubSessionID());
		} else {
			key.append("@0");
		}
		if (this.getGuid() != null) {
			key.append("@");
			key.append(this.getGuid());
		}

		if (this.getCatalogFilePath() != null) {
			key.append("@");
			key.append(this.getCatalogFilePath());
		}

		if (this.getGrtCatalogItemModel() != null)
		{
			key.append("@");
			key.append(this.getGrtCatalogItemModel());
		}
		
		if(this.getComponentName() != null) {
			key.append("@");
			key.append(getComponentName());
		}
		
		//add path
		if(this.getPath() != null) {
			key.append("@");
			key.append(this.getPath());
		}
		
		if (id != null) {
			key.append("@");
			key.append(id);
		}

		return key.toString();
	}

	private Integer id = null;

	public void setId(Integer id) {
		this.id = id;
	}

	public int getId() {
		if (id == null) {
			id = this.toId().hashCode();
		}
		return id;
	}

	private String grtCatalogFile; // alternative catalog file path for GRT
	private Long sessionID;
	private String backupDestination;
	private GRTCatalogItemModel grtCatalogItemModel;

	public String getGrtCatalogFile() {
		return grtCatalogFile;
	}

	public void setGrtCatalogFile(String grtCatalogFile) {
		this.grtCatalogFile = grtCatalogFile;
	}

	public Long getSessionID() {
		return sessionID;
	}

	public void setSessionID(Long sessionID) {
		this.sessionID = sessionID;
	}

	public String getBackupDestination() {
		return backupDestination;
	}

	public void setBackupDestination(String backupDestination) {
		this.backupDestination = backupDestination;
	}

	public GRTCatalogItemModel getGrtCatalogItemModel() {
		return grtCatalogItemModel;
	}

	public void setGrtCatalogItemModel(GRTCatalogItemModel grtCatalogItemModel) {
		this.grtCatalogItemModel = grtCatalogItemModel;
	}
	
	public String getEncryptedKey() {
		return (String)get("EncryptedKey");
	}
	
	public void setEncryptedKey(String key) {
		set("EncryptedKey", key);
	}
	
	private List<GridTreeNode> referNode = new LinkedList<GridTreeNode>();

	public List<GridTreeNode> getReferNode() {
		return referNode;
	}
	public void setReferNode(List<GridTreeNode> referNode) {
		this.referNode = referNode;
	}
	
	// display path is the readable path instead of the internal GUID
	public String getDisplayPath()
	{
		return (String) get("displayPath");
	}
	public void setDisplayPath(String displayPath)
	{
		set("displayPath", displayPath);
	}
	
	public Integer getSelectionType(){
		return (Integer)get("selectionType");
	}
	
	public void setSelectionType(Integer selectionType){
		set("selectionType",selectionType);
	}
	
	public void setIsRefs(Boolean isRefs){
		set("isRefs",isRefs);
	}
	
	public Boolean getIsRefs(){
		return (Boolean)get("isRefs");
	}
	
	public Boolean isHasDriverLetter(){
		return get("hasDriverLetter");
	}
	
	public void setHasDriverLetter(Boolean hasDriverLetter){
		set("hasDriverLetter",hasDriverLetter);
	}
	public Boolean isHasReplicaDB(){
		return get("hasReplicaDB");
	}
	
	public void setHasReplicaDB(Boolean hasReplicaDB){
		set("hasReplicaDB",hasReplicaDB);
	}
	
	public String getVolumeMountPath(){
		return (String)get("VolumeMountPath");
	}
	
	public void setVolumeMountPath(String mountPath){
		set("VolumeMountPath", mountPath);
	}
}
