package com.ca.arcflash.ui.client.model;

import java.util.Date;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class GRTCatalogItemModel extends BaseModelData {

	private static final long serialVersionUID = -5684151156353976628L;

	public Long getObjType() {
		return (Long) get("objType");
	}

	public void setObjType(Long objType) {
		set("objType", objType);
	}

	public String getObjDate() {
		return get("objDate");
	}

	public void setObjDate(String objDate) {
		set("objDate", objDate);
	}

	public Long getObjFlags() {
		return (Long) get("objFlags");
	}

	public void setObjFlags(Long objFlags) {
		set("objFlags", objFlags);
	}

	public Long getLowObjSize() {
		return (Long) get("lowObjSize");
	}

	public void setLowObjSize(Long lowObjSize) {
		set("lowObjSize", lowObjSize);
	}

	public Long getHighObjSize() {
		return (Long) get("highObjSize");
	}

	public void setHighObjSize(Long highObjSize) {
		set("highObjSize", highObjSize);
	}

	public String getObjName() {
		return get("objName");
	}

	public void setObjName(String objName) {
		set("objName", objName);
	}

	public String getObjInfo() {
		return get("objInfo");
	}

	public void setObjInfo(String objInfo) {
		set("objInfo", objInfo);
	}

	public Long getLowObjSelfid() {
		return (Long) get("lowObjSelfid");
	}

	public void setLowObjSelfid(Long lowObjSelfid) {
		set("lowObjSelfid", lowObjSelfid);
	}

	public Long getHighObjSelfid() {
		return (Long) get("highObjSelfid");
	}

	public void setHighObjSelfid(Long highObjSelfid) {
		set("highObjSelfid", highObjSelfid);
	}

	public Long getLowObjParentid() {
		return (Long) get("lowObjParentid");
	}

	public void setLowObjParentid(Long lowObjParentid) {
		set("lowObjParentid", lowObjParentid);
	}

	public long getHighObjParentid() {
		return (Long) get("highObjParentid");
	}

	public void setHighObjParentid(Long highObjParentid) {
		set("highObjParentid", highObjParentid);
	}

	public Long getLowObjBody() {
		return (Long) get("lowObjBody");
	}

	public void setLowObjBody(Long lowObjBody) {
		set("lowObjBody", lowObjBody);
	}

	public Long getHighObjBody() {
		return (Long) get("highObjBody");
	}

	public void setHighObjBody(Long highObjBody) {
		set("highObjBody", highObjBody);
	}

	public Long getCp_Flag() {
		return (Long) get("cp_Flag");
	}

	public void setCp_Flag(Long cpFlag) {
		set("cp_Flag", cpFlag);
	}

	public String getCatalogFilePath() {
		return (String) get("catalogFilePath");
	}

	public void setCatalofFilePath(String path) {
		set("catalogFilePath", path);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof GRTCatalogItemModel) {
			GRTCatalogItemModel another = (GRTCatalogItemModel) obj;
			boolean isEqual = this.toId().equalsIgnoreCase(another.toId());		
			return isEqual;
		} else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return getId();
	}
	
	private Integer id;
	
	private int getId() {
		if (id == null) {			
			id = this.toId().hashCode();
		}
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String toId() {
		String id = "";
		long selfId = 0;
		if (getLowObjSelfid() != null){				
			selfId = getLowObjSelfid();
		}
		if (getHighObjSelfid() != null){					
			selfId +=getHighObjSelfid()<<32 ;
		}		
		id = String.valueOf(selfId);

		return id;
	}
	
	public Long getChildrenCount() {
		return (Long) get("childrenCount");
	}

	public void setChildrenCount(Long childrenCount) {
		set("childrenCount", childrenCount);
	}
	
	///////////////
	public String getSender() {
		return get("sender");
	}

	public void setSender(String sender) {
		set("sender", sender);
	}
	
	public String getReceiver() {
		return get("receiver");
	}

	public void setReceiver(String receiver) {
		set("receiver", receiver);
	}
	
	public Date getSentTime() {
		return (Date) get("sentTime");
	}

	public void setSentTime(Date sentTime) {
		set("sentTime", sentTime);
	}
	
	public Long getSendTZOffset() {
		return (Long)get("SendTZOffset");
	}
	
	public void setSendTZOffset(Long offset) {
		set("SendTZOffset", offset);
	}
	
	public Date getReceivedTime() {
		return (Date) get("receivedTime");
	}

	public void setReceivedTime(Date receivedTime) {
		set("receivedTime", receivedTime);
	}
	
	public Long getReceivedTZOffset() {
		return (Long)get("ReceivedTZOffset");
	}
	
	public void setReceivedTZOffset(Long offset) {
		set("ReceivedTZOffset", offset);
	}
	

	public Long getFlag() {
		return (Long) get("flag");
	}

	public void setFlag(Long flag) {
		set("flag", flag);
	}
	
	public Long getItemSize() {
		return (Long) get("itemSize");
	}

	public void setItemSize(Long itemSize) {
		set("itemSize", itemSize);
	}


}
