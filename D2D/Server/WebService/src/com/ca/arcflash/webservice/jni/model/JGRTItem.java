package com.ca.arcflash.webservice.jni.model;

public class JGRTItem {
	public static final long ERROR_MOUNT_SESSION_TIMEOUT	=	0xf0000002;
	
	public static final long AD_GROUP_USER		=	81;
	public static final long AD_GROUP_COMPUTER	=	82;
	public static final long AD_GROUP_GENERAL	=	83;
	
	public static final long APP_UNKNOWN_TYPE   =0;
	public static final long APP_GRT_AD	        = 0x01;		// AD  GRT
	public static final long APP_GRT_SPS	    = 0x02;		// SPS GRT
	
	public static final long APP_DATA_TYPE_UNKNOWN	= 0x00;
	public static final long APP_DATA_AD_BASE		= 0x01;							// AD data type base
	public static final long APP_DATA_AD_PARTTION	= APP_DATA_AD_BASE + 0x00;		// the child items of AD
	public static final long APP_DATA_AD_CHILD		= APP_DATA_AD_BASE + 0x01;		// the child items of AD
	public static final long APP_DATA_AD_ATTr		= APP_DATA_AD_BASE + 0x02;		// the attributes of current node
	public static final long APP_DATA_SPS_BASE		= 0x200;						// SPS data type base
	
	public static final long GROUP_AD_NODE_GENERAL  = 0;
	public static final long GROUP_AD_NODE_USERS  	= 1;
	public static final long GROUP_AD_NODE_USER  	= 2;
	public static final long GROUP_AD_NODE_COMPUTER = 3;
	public static final long GROUP_AD_NODE_OU  		= 4;
	
	public static final long FLAGS_AD_NODE_LEAF		= 0x01;// the current is leaf node

	private long id;
	private String name;
	private String value; // for AD attribute. AD node doesn't have this field
	private long group;  // AD node type. AD attribute doesn't have this field
	private long flags;  // if AD node is a leaf node. AD attribute doesn't have this field
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public long getGroup() {
		return group;
	}
	public void setGroup(long group) {
		this.group = group;
	}
	public long getFlags() {
		return flags;
	}
	public void setFlags(long flags) {
		this.flags = flags;
	}
	
}
