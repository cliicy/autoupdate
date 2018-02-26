package com.ca.arcserve.edge.app.base.webservice.contract.recoverypoint;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.webservice.data.restore.RecoveryPoint;

public class RecoveryPointForLinux extends RecoveryPoint {
	
	public static class RecoveryPointItemForLinux {
		private String name;
		private long size;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public long getSize() {
			return size;
		}
		public void setSize(long size) {
			this.size = size;
		}
	}

	private static final long serialVersionUID = 1L;
	private String linuxencryptAlgoName;
	private String linuxencryptionPassword;
	private int linuxcompressLevel;
	private String linuxosType = "unknown";
	private String linuxcpuarch = "unknown";
	private List<RecoveryPointItemForLinux> linuxRecoveryPointItems = new ArrayList<RecoveryPointItemForLinux>();

	public String getLinuxencryptAlgoName() {
		return linuxencryptAlgoName;
	}
	public void setLinuxencryptAlgoName(String linuxencryptAlgoName) {
		this.linuxencryptAlgoName = linuxencryptAlgoName;
	}
	public String getLinuxencryptionPassword() {
		return linuxencryptionPassword;
	}
	public void setLinuxencryptionPassword(String linuxencryptionPassword) {
		this.linuxencryptionPassword = linuxencryptionPassword;
	}
	public int getLinuxcompressLevel() {
		return linuxcompressLevel;
	}
	public void setLinuxcompressLevel(int linuxcompressLevel) {
		this.linuxcompressLevel = linuxcompressLevel;
	}
	public String getLinuxosType() {
		return linuxosType;
	}
	public void setLinuxosType(String linuxosType) {
		this.linuxosType = linuxosType;
	}
	public String getLinuxcpuarch() {
		return linuxcpuarch;
	}
	public void setLinuxcpuarch(String linuxcpuarch) {
		this.linuxcpuarch = linuxcpuarch;
	}
	public List<RecoveryPointItemForLinux> getLinuxRecoveryPointItems() {
		return linuxRecoveryPointItems;
	}
	public void setLinuxRecoveryPointItem( RecoveryPointItemForLinux linuxRecoveryPointItem ) {
		this.linuxRecoveryPointItems.add( linuxRecoveryPointItem );
	}

}
