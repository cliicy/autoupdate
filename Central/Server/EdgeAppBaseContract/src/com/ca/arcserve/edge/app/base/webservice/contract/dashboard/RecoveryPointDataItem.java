package com.ca.arcserve.edge.app.base.webservice.contract.dashboard;

import java.io.Serializable;
import java.util.Date;

import com.ca.arcserve.edge.app.base.webservice.contract.common.ServerDate;
public class RecoveryPointDataItem implements Serializable {
	private static final long serialVersionUID = 7993195991755798615L;
	
	private long compressedInKB;
	private long rawInKB;
	private long restorableInKB;
	private Date execDate;
	private ServerDate serverDate;
	
	public ServerDate getServerDate() {
		return serverDate;
	}
	public void setServerDate(ServerDate serverDate) {
		this.serverDate = serverDate;
	}
	public long getCompressedInKB() {
		return compressedInKB;
	}
	public void setCompressedInKB(long compressedInKB) {
		this.compressedInKB = compressedInKB;
	}
	public long getRawInKB() {
		return rawInKB;
	}
	public void setRawInKB(long rawInKB) {
		this.rawInKB = rawInKB;
	}
	public long getRestorableInKB() {
		return restorableInKB;
	}
	public void setRestorableInKB(long restorableInKB) {
		this.restorableInKB = restorableInKB;
	}
	public Date getExecDate() {
		return execDate;
	}
	public void setExecDate(Date execDate) {
		this.execDate = execDate;
	}

}
