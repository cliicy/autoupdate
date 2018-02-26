package com.ca.arcserve.edge.app.base.webservice.contract.apm;

import java.io.Serializable;


import com.ca.arcflash.webservice.data.PM.BIPatchInfo;
import com.ca.arcflash.webservice.data.PM.PatchInfo;

public class BIPatchInfoEdge extends PatchInfoEdge {
	/**
	 * a
	 */
	//private static final long serialVersionUID = 5064021224758155705L;
	private EdgePatchType patchType;
	
	public BIPatchInfoEdge(){
		aryPatchInfoM = null;
	}
	
	public PatchInfoEdge[] aryPatchInfoM;
	
	public void setPatchInfoEdge(PatchInfoEdge[] aryPack) {
		aryPatchInfoM = aryPack;
	}

	
	public BIPatchInfoEdge(BIPatchInfo patchInfo){
		if ( patchInfo == null )
			return ;
		
		aryPatchInfoM = new PatchInfoEdge[patchInfo.aryPatchInfo.length];
		for (int i = 0; i< patchInfo.aryPatchInfo.length; i++ ){
			aryPatchInfoM[i] = new PatchInfoEdge();
			aryPatchInfoM[i].setAvailableStatus(patchInfo.aryPatchInfo[i].getAvailableStatus());
			aryPatchInfoM[i].setDescription(patchInfo.aryPatchInfo[i].getDescription());
			aryPatchInfoM[i].setDownloadStatus(patchInfo.aryPatchInfo[i].getDownloadStatus());
			aryPatchInfoM[i].setError_Status(patchInfo.getError_Status());
			aryPatchInfoM[i].setErrorMessage(patchInfo.aryPatchInfo[i].getErrorMessage());
			aryPatchInfoM[i].setInstallStatus(patchInfo.aryPatchInfo[i].getInstallStatus());
			aryPatchInfoM[i].setMajorversion(patchInfo.aryPatchInfo[i].getMajorversion());
			aryPatchInfoM[i].setMinorVersion(patchInfo.aryPatchInfo[i].getMinorVersion());
			aryPatchInfoM[i].setPackageID(patchInfo.aryPatchInfo[i].getPackageID());
			aryPatchInfoM[i].setPatchDownloadLocation(patchInfo.getPatchDownloadLocation());
			aryPatchInfoM[i].setPatchURL(patchInfo.aryPatchInfo[i].getPatchURL());
			aryPatchInfoM[i].setPatchVersionNumber(patchInfo.aryPatchInfo[i].getPatchVersionNumber());
			aryPatchInfoM[i].setPublishedDate(patchInfo.aryPatchInfo[i].getPublishedDate());
			aryPatchInfoM[i].setRebootRequired(patchInfo.aryPatchInfo[i].getRebootRequired());
			aryPatchInfoM[i].setServicePack(patchInfo.aryPatchInfo[i].getServicePack());
			aryPatchInfoM[i].setSize(patchInfo.aryPatchInfo[i].getSize());
			aryPatchInfoM[i].setPackageDepy(patchInfo.aryPatchInfo[i].getPackageDepy());
			aryPatchInfoM[i].setPackageUpdateName(patchInfo.aryPatchInfo[i].getPackageUpdateName());
		}
	}

	public void setPatchType(EdgePatchType patchType) {
		this.patchType = patchType;
		for (int i = 0; i< aryPatchInfoM.length; i++ ){
			aryPatchInfoM[i].setPatchType(patchType);
		}
	}

	public EdgePatchType getPatchType() {
		return patchType;
	}
	
}
