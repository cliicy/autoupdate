package com.ca.arcserve.edge.app.base.webservice.contract.configuration;

public enum RebootType {
	RebootAtOnce,
	RebootSchedule;
	
	public static RebootType parseInt(int value){
		switch (value) {
		case 0:
			return RebootAtOnce;
		case 1:
			return RebootSchedule;
		default:
			return RebootAtOnce;
		}
	}
}
