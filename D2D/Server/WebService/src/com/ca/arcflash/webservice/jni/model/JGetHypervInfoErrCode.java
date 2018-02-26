package com.ca.arcflash.webservice.jni.model;

public enum JGetHypervInfoErrCode {
		NO_ERROR(0),  ///success
		ACCESS_DENIED(1),   ///Check if the specified credentials are correct and the account has administrator permission for this Hyper-V server.For the non-built-in administrator user, the remote UAC need to be disabled.
	    NOT_INSTALL_HYPERV_ROLE(2),///Verify that the server have enabled the hyper-v service
	    SERVER_UNAVAILABLE(3),///Check if the host {0} is available in the network and its Hyper-V service is allowed to be communicated through Windows firewall .
	    OTHER_ERROR(4);  ///other error
	    
	    private int value;
		
		private JGetHypervInfoErrCode(int value) {
			this.value = value;
		}
		
		public int getValue() {
			return value;
		}
	}