package com.ca.arcserve.edge.app.base.webservice.contract.node;

import java.io.Serializable;
import java.util.List;

public class VMVerifyStatus implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2100951085175206701L;
	private int status;
	private List<VMStatusDetail> details;
	
	public enum CheckStatus{
		INVALID(0),
		WAITING(1),
		CHECKING(2),
		OK(3),
		WARNING(4),
		ERROR(5);
		
		private final int value;

		CheckStatus(int value) {
			this.value = value;
		}

		public int value() {
			return value;
		}
		
		public static CheckStatus valueOf(int value) {
			for (CheckStatus status : CheckStatus.values()) {
				if (status.value() == value) {
					return status;
				}
			}
			return null;
		}
	}

	public enum CheckType{
		VERIFICATION,
		ESXSERVER,
		CBT,
		APPLICATIONS,
		VMTOOLS,
		DISK_INFO,
		POWER_STATUS,
		CREDENTIAL,
		VIX,
		INTEGRATION_SERVICE,
		HYPERV_CREDENTIAL,
		HYPERV_SERVER,
		HYPERV_DATACONSISTENCY,
		HWSNAPSHOT_SUPPORT
	}
	
	public List<VMStatusDetail> getDetails() {
		return details;
	}

	public void setDetails(List<VMStatusDetail> details) {
		this.details = details;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
