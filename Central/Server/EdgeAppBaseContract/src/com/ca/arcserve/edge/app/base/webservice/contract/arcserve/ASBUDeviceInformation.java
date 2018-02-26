package com.ca.arcserve.edge.app.base.webservice.contract.arcserve;

import java.io.Serializable;
import java.util.List;

public class ASBUDeviceInformation implements Serializable {
	private static final long serialVersionUID = 9219846177817941372L;
	private String groupName;
	private int scsiID;
	private int lun;
	private int scsiBusNo;
	private int logicalDeviceNo;
	private long tapeFlags;
	private long capFlags;
	private String vendorID;
	private String productID;
	private String firmware;
	private int cartridgeType;
	private int compliance;
	private long deviceType;
	private int hostBoardNo;
	private int formatCode;
	private long blockSize;
	private long writeShots;
	private long readShots;
	private int noOfDrives;
	private int noOfIeElement;
	private int noOfSlots;
	private List<Integer> reservedDrives;
	private List<Integer> unReservedDrives;
	private int noOfMagazines;
	private String fsdPath;
	
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public int getScsiID() {
		return scsiID;
	}
	public void setScsiID(int scsiID) {
		this.scsiID = scsiID;
	}
	public int getLun() {
		return lun;
	}
	public void setLun(int lun) {
		this.lun = lun;
	}
	public int getScsiBusNo() {
		return scsiBusNo;
	}
	public void setScsiBusNo(int scsiBusNo) {
		this.scsiBusNo = scsiBusNo;
	}
	public int getLogicalDeviceNo() {
		return logicalDeviceNo;
	}
	public void setLogicalDeviceNo(int logicalDeviceNo) {
		this.logicalDeviceNo = logicalDeviceNo;
	}
	public long getTapeFlags() {
		return tapeFlags;
	}
	public void setTapeFlags(long tapeFlags) {
		this.tapeFlags = tapeFlags;
	}
	public long getCapFlags() {
		return capFlags;
	}
	public void setCapFlags(long capFlags) {
		this.capFlags = capFlags;
	}
	public String getVendorID() {
		return vendorID;
	}
	public void setVendorID(String vendorID) {
		this.vendorID = vendorID;
	}
	public String getProductID() {
		return productID;
	}
	public void setProductID(String productID) {
		this.productID = productID;
	}
	public String getFirmware() {
		return firmware;
	}
	public void setFirmware(String firmware) {
		this.firmware = firmware;
	}
	public int getCartridgeType() {
		return cartridgeType;
	}
	public void setCartridgeType(int cartridgeType) {
		this.cartridgeType = cartridgeType;
	}
	public int getCompliance() {
		return compliance;
	}
	public void setCompliance(int compliance) {
		this.compliance = compliance;
	}
	public long getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(long deviceType) {
		this.deviceType = deviceType;
	}
	public int getHostBoardNo() {
		return hostBoardNo;
	}
	public void setHostBoardNo(int hostBoardNo) {
		this.hostBoardNo = hostBoardNo;
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
	public long getWriteShots() {
		return writeShots;
	}
	public void setWriteShots(long writeShots) {
		this.writeShots = writeShots;
	}
	public long getReadShots() {
		return readShots;
	}
	public void setReadShots(long readShots) {
		this.readShots = readShots;
	}
	public int getNoOfDrives() {
		return noOfDrives;
	}
	public void setNoOfDrives(int noOfDrives) {
		this.noOfDrives = noOfDrives;
	}
	public int getNoOfIeElement() {
		return noOfIeElement;
	}
	public void setNoOfIeElement(int noOfIeElement) {
		this.noOfIeElement = noOfIeElement;
	}
	public int getNoOfSlots() {
		return noOfSlots;
	}
	public void setNoOfSlots(int noOfSlots) {
		this.noOfSlots = noOfSlots;
	}
	public List<Integer> getReservedDrives() {
		return reservedDrives;
	}
	public void setReservedDrives(List<Integer> reservedDrives) {
		this.reservedDrives = reservedDrives;
	}
	public List<Integer> getUnReservedDrives() {
		return unReservedDrives;
	}
	public void setUnReservedDrives(List<Integer> unReservedDrives) {
		this.unReservedDrives = unReservedDrives;
	}
	public int getNoOfMagazines() {
		return noOfMagazines;
	}
	public void setNoOfMagazines(int noOfMagazines) {
		this.noOfMagazines = noOfMagazines;
	}
	public String getFsdPath() {
		return fsdPath;
	}
	public void setFsdPath(String fsdPath) {
		this.fsdPath = fsdPath;
	}
	
}
