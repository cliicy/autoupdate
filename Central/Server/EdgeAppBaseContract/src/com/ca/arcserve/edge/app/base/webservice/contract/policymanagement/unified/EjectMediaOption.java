package com.ca.arcserve.edge.app.base.webservice.contract.policymanagement.unified;

public enum EjectMediaOption {
	EJECT_MEDIA(1), NOT_EJECT_MEDIA(2), USE_DEVICE_SETTING(3);

	private int value;

	private EjectMediaOption(int val) {
		this.value = val;
	}

	public int getValue() {
		return this.value;
	}
	
	public static EjectMediaOption fromValue(int value){
		switch(value){
			case 1 : return EJECT_MEDIA;
			case 2 : return NOT_EJECT_MEDIA;
			case 3 : return USE_DEVICE_SETTING;
			default : return EJECT_MEDIA;
		}
	}
}
