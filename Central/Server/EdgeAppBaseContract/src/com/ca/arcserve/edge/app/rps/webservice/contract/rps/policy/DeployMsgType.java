package com.ca.arcserve.edge.app.rps.webservice.contract.rps.policy;

//This type is bit wise, it can be pass into one DAO interface to get multiple type message
public enum DeployMsgType {
	Info(0x00000001),Warning(0x00000002),Error(0x00000004);
	
	private int value;
	DeployMsgType(int value){
		this.value = value;
	}
	
	public int getValue(){
		return this.value;
	}

	public static DeployMsgType parse(int value) {
		switch (value) {
		case 0x00000001:
			return DeployMsgType.Info;
		case 0x00000002:
			return DeployMsgType.Warning;
		case 0x00000004:
			return DeployMsgType.Error;
		default:
			return null;
		}
	}

}
