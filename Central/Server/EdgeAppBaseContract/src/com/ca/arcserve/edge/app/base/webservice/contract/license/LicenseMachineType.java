package com.ca.arcserve.edge.app.base.webservice.contract.license;

public enum LicenseMachineType {
	
	/**
	 * The machine has not been detected yet.
	 */
	Undetected(0),
	/**
	 * The machine is a physical machine.
	 */
	PHYSICAL_MACHINE(1),
	/**
	 * The machine is a VMware virtual machine.
	 */
	VSHPERE_VM(2),
	/**
	 * The machine is a Hyper-V virtual machine.
	 */
	HYPER_V_VM(3),
	/**
	 * The type of the machine doesn't belong to other values defined here.
	 */
	Other(100),
	/**
	 * The machine type is not supported.
	 */
	Unsupported(200);
	
	private int value;
	
	LicenseMachineType(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	public static LicenseMachineType parseNative(int value) {
		switch (value) {
		case 0: return LicenseMachineType.PHYSICAL_MACHINE;
		case 1: return LicenseMachineType.VSHPERE_VM;
		case 2: return LicenseMachineType.HYPER_V_VM;
		case 7: return LicenseMachineType.Unsupported;
		default: return LicenseMachineType.Other;
		}
	}
	
	public static LicenseMachineType parseValue(int value) {
		for (LicenseMachineType type : LicenseMachineType.values()) {
			if (type.getValue() == value) {
				return type;
			}
		}
		
		return null;
	}
	
}
