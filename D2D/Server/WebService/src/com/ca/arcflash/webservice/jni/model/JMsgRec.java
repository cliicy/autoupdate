package com.ca.arcflash.webservice.jni.model;

public class JMsgRec {
	private long objType;
	private long objDate;
	private long objFlags;
	private long lowObjSize; //the low 4 bytes of size
	private long highObjSize; //the high 4 bytes
	private String objName;
	private String objInfo;
	private long lowObjSelfid; //the low 4 bytes of self id
	private long highObjSelfid; //the high 4 bytes of self id
	private long lowObjParentid;
	private long highObjParentid;
	private long lowObjBody;
	private long highObjBody;
	private long cp_Flag;
	private long childrenCount;
	
	private String sender;
	private String receiver;
	private long sentTime;
	private long receivedTime;
	private long flag;
	private long itemSize;
	
	public long getObjType() {
		return objType;
	}
	public void setObjType(long objType) {
		this.objType = objType;
	}
	public long getObjDate() {
		return objDate;
	}
	public void setObjDate(long objDate) {
		this.objDate = objDate;
	}
	public long getObjFlags() {
		return objFlags;
	}
	public void setObjFlags(long objFlags) {
		this.objFlags = objFlags;
	}
	public long getLowObjSize() {
		return lowObjSize;
	}
	public void setLowObjSize(long lowObjSize) {
		this.lowObjSize = lowObjSize;
	}
	public long getHighObjSize() {
		return highObjSize;
	}
	public void setHighObjSize(long highObjSize) {
		this.highObjSize = highObjSize;
	}
	public String getObjName() {
		return objName;
	}
	public void setObjName(String objName) {
		this.objName = objName;
	}
	public String getObjInfo() {
		return objInfo;
	}
	public void setObjInfo(String objInfo) {
		this.objInfo = objInfo;
	}
	public long getLowObjSelfid() {
		return lowObjSelfid;
	}
	public void setLowObjSelfid(long lowObjSelfid) {
		this.lowObjSelfid = lowObjSelfid;
	}
	public long getHighObjSelfid() {
		return highObjSelfid;
	}
	public void setHighObjSelfid(long highObjSelfid) {
		this.highObjSelfid = highObjSelfid;
	}
	public long getLowObjParentid() {
		return lowObjParentid;
	}
	public void setLowObjParentid(long lowObjParentid) {
		this.lowObjParentid = lowObjParentid;
	}
	public long getHighObjParentid() {
		return highObjParentid;
	}
	public void setHighObjParentid(long highObjParentid) {
		this.highObjParentid = highObjParentid;
	}
	public long getLowObjBody() {
		return lowObjBody;
	}
	public void setLowObjBody(long lowObjBody) {
		this.lowObjBody = lowObjBody;
	}
	public long getHighObjBody() {
		return highObjBody;
	}
	public void setHighObjBody(long highObjBody) {
		this.highObjBody = highObjBody;
	}
	public long getCp_Flag() {
		return cp_Flag;
	}
	public void setCp_Flag(long cpFlag) {
		cp_Flag = cpFlag;
	}
	public void setChildrenCount(long childrenCount) {
		this.childrenCount = childrenCount;
	}
	public long getChildrenCount() {
		return childrenCount;
	}	
	public String getSender()
	{
		return sender;
	}
	public void setSender(String sender)
	{
		this.sender = sender;
	}
	public String getReceiver()
	{
		return receiver;
	}
	public void setReceiver(String receiver)
	{
		this.receiver = receiver;
	}
	public long getSentTime()
	{
		return sentTime;
	}
	public void setSentTime(long sentTime)
	{
		this.sentTime = sentTime;
	}
	public long getReceivedTime()
	{
		return receivedTime;
	}
	public void setReceivedTime(long receivedTime)
	{
		this.receivedTime = receivedTime;
	}
	public long getFlag()
	{
		return flag;
	}
	public void setFlag(long flag)
	{
		this.flag = flag;
	}
	public long getItemSize()
	{
		return itemSize;
	}
	public void setItemSize(long itemSize)
	{
		this.itemSize = itemSize;
	}	
}
