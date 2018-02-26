/**
 * 
 */
package com.ca.arcserve.edge.app.base.webservice.contract.apm;

import com.ca.arcserve.edge.app.base.webservice.contract.common.EdgeApplicationType;

/**
 * @author wanwe14
 *
 */
public enum EdgePatchType {
	PATCH_COMMON(1),
	PATCH_CM(2),
	PATCH_CM_ENGINE(3),//added by cliicy.luo
	PATCH_VCM(4),
	PATCH_VSPHERE(8),
	PATCH_REPORT(16);
	
	private int typeId;
	
	private EdgePatchType(int typeId){
		this.typeId = typeId;
	}
	
	public int getTypeId(){
		return typeId;
	}
	
	public EdgeApplicationType toEdgeAppType(){
		if( this == PATCH_COMMON )
			throw new Error("Common patch cannot be mapped to any application.");
		else if( this == PATCH_CM )
			return EdgeApplicationType.CentralManagement;
		else if( this == PATCH_VCM )
			return EdgeApplicationType.VirtualConversionManager;
		else if( this == PATCH_VSPHERE )
			return EdgeApplicationType.vShpereManager;
		else
			return EdgeApplicationType.Report;
	}
	
	public static EdgePatchType fromEdgeAppType(EdgeApplicationType appType){
		switch (appType) {
		case CentralManagement:
			return PATCH_CM;
		case VirtualConversionManager:
			return  PATCH_VCM;
		case vShpereManager:
			return PATCH_VSPHERE;
		case Report:
			return PATCH_REPORT;
		default:
			throw new Error("Invalid patch type to map to patch type");
		}
	}
}
