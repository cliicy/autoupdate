package com.ca.arcserve.edge.app.base.webservice.contract.arcserve;

import java.io.Serializable;

public class ASBUMediaInfo implements Serializable {

	private static final long serialVersionUID = -4194238397105907785L;
	private String name;
	private String slotNo;
	private String serialNo;
	private String deviceNo;
	private long randomId;
	private long sequence;
	private long created;
	private int formatCode;
	private long blockSize;
	private int tapeType;
	private long expirationDate;
	private String densityCodeString;
	private String mediumTypeString;
	private long slotFlag;
	private int magazineNo;
	private int slotType;
	private boolean wirteProtected;
	private boolean expiration;
	private long mBWritten;
	public boolean isWirteProtected() {
		return wirteProtected;
	}

	public void setWirteProtected(boolean wirteProtected) {
		this.wirteProtected = wirteProtected;
	}

	public boolean isExpiration() {
		return expiration;
	}

	public void setExpiration(boolean expiration) {
		this.expiration = expiration;
	}

	public long getmBWritten() {
		return mBWritten;
	}

	public void setmBWritten(long mBWritten) {
		this.mBWritten = mBWritten;
	}

	public long getLastWriteTime() {
		return lastWriteTime;
	}

	public void setLastWriteTime(long lastWriteTime) {
		this.lastWriteTime = lastWriteTime;
	}

	public String getMediaPoolName() {
		return mediaPoolName;
	}

	public void setMediaPoolName(String mediaPoolName) {
		this.mediaPoolName = mediaPoolName;
	}

	public int getMediaStatusInPool() {
		return mediaStatusInPool;
	}

	public void setMediaStatusInPool(int mediaStatusInPool) {
		this.mediaStatusInPool = mediaStatusInPool;
	}

	public long getExpirationDateInPool() {
		return expirationDateInPool;
	}

	public void setExpirationDateInPool(long expirationDateInPool) {
		this.expirationDateInPool = expirationDateInPool;
	}

	private long lastWriteTime;
	private String mediaPoolName;
	private int mediaStatusInPool;
	private long expirationDateInPool;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSlotNo() {
		return slotNo;
	}

	public void setSlotNo(String slotNo) {
		this.slotNo = slotNo;
	}

	public String getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(String serialNo) {
		this.serialNo = serialNo;
	}

	public String getDeviceNo() {
		return deviceNo;
	}

	public void setDeviceNo(String deviceNo) {
		this.deviceNo = deviceNo;
	}

	public long getRandomId() {
		return randomId;
	}

	public void setRandomId(long randomId) {
		this.randomId = randomId;
	}

	public long getSequence() {
		return sequence;
	}

	public void setSequence(long sequence) {
		this.sequence = sequence;
	}

	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}

	public int getFormatCode() {
		return formatCode;
	}

	public void setFormatCode(int formatCode) {
		this.formatCode = formatCode;
	}

	public long getBlockSize() {
		return blockSize;
	}

	public void setBlockSize(long blockSize) {
		this.blockSize = blockSize;
	}

	public int getTapeType() {
		return tapeType;
	}

	public void setTapeType(int tapeType) {
		this.tapeType = tapeType;
	}

	public long getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(long expirationDate) {
		this.expirationDate = expirationDate;
	}

	public String getDensityCodeString() {
		return densityCodeString;
	}

	public void setDensityCodeString(String densityCodeString) {
		this.densityCodeString = densityCodeString;
	}

	public String getMediumTypeString() {
		return mediumTypeString;
	}

	public void setMediumTypeString(String mediumTypeString) {
		this.mediumTypeString = mediumTypeString;
	}

	public long getSlotFlag() {
		return slotFlag;
	}

	public void setSlotFlag(long slotFlag) {
		this.slotFlag = slotFlag;
	}

	public int getMagazineNo() {
		return magazineNo;
	}

	public void setMagazineNo(int magazineNo) {
		this.magazineNo = magazineNo;
	}

	public int getSlotType() {
		return slotType;
	}

	public void setSlotType(int slotType) {
		this.slotType = slotType;
	}
}
