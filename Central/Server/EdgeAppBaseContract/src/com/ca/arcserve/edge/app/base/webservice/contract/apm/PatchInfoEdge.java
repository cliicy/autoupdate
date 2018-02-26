package com.ca.arcserve.edge.app.base.webservice.contract.apm;

import java.io.Serializable;

import com.ca.arcflash.webservice.data.PM.PatchInfo;

public class PatchInfoEdge extends PatchInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5064021224758155705L;
	private EdgePatchType patchType;
	
	public PatchInfoEdge(){
		super();
	}
	
	public PatchInfoEdge(PatchInfo patchInfo){
		super();
		setAvailableStatus(patchInfo.getAvailableStatus());
		setDescription(patchInfo.getDescription());
		setDownloadStatus(patchInfo.getDownloadStatus());
		setError_Status(patchInfo.getError_Status());
		setErrorMessage(patchInfo.getErrorMessage());
		setInstallStatus(patchInfo.getInstallStatus());
		setMajorversion(patchInfo.getMajorversion());
		setMinorVersion(patchInfo.getMinorVersion());
		setPackageID(patchInfo.getPackageID());
		setPatchDownloadLocation(patchInfo.getPatchDownloadLocation());
		setPatchURL(patchInfo.getPatchURL());
		setPatchVersionNumber(patchInfo.getPatchVersionNumber());
		setPublishedDate(patchInfo.getPublishedDate());
		setRebootRequired(patchInfo.getRebootRequired());
		setServicePack(patchInfo.getServicePack());
		setSize(patchInfo.getSize());
		setPackageDepy(patchInfo.getPackageDepy());
	}

	//added by cliicy.luo for hotfix
	//public void setPackageDep(EdgePatchType patchType) {
	//	this.patchType = patchType;
	//}
	//added by cliicy.luo for hotfix
	
	public void setPatchType(EdgePatchType patchType) {
		this.patchType = patchType;
	}

	public EdgePatchType getPatchType() {
		return patchType;
	}
	
}
