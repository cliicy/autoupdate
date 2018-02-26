package com.ca.arcflash.webservice.jni.model;

public class JJobStatus {
	private long liGrandTotal = 0;
	private long liVolumeTotal = 0;
	private long liVolumeProcessed = 0;
	private boolean bJobCancled = false;

	public long getLiGrandTotal() {
		return liGrandTotal;
	}

	public void setLiGrandTotal(long liGrandTotal) {
		this.liGrandTotal = liGrandTotal;
	}

	public long getLiVolumeTotal() {
		return liVolumeTotal;
	}

	public void setLiVolumeTotal(long liVolumeTotal) {
		this.liVolumeTotal = liVolumeTotal;
	}

	public long getLiVolumeProcessed() {
		return liVolumeProcessed;
	}

	public void setLiVolumeProcessed(long liVolumeProcessed) {
		this.liVolumeProcessed = liVolumeProcessed;
	}

	public boolean isbJobCancled() {
		return bJobCancled;
	}

	public void setbJobCancled(boolean bJobCancled) {
		this.bJobCancled = bJobCancled;
	}
}
