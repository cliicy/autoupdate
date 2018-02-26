package com.ca.arcflash.ui.client.model;


import com.ca.arcflash.webservice.data.PM.BIPatchInfo;
import com.extjs.gxt.ui.client.data.BaseModelData;

public class BIPatchInfoModel extends PatchInfoModel {

	public PatchInfoModel[] aryPatchInfoM;
	
	public void setPatchInfoMdl(PatchInfoModel[] aryPack) {
		aryPatchInfoM = aryPack;
	}
	public PatchInfoModel[] getPatchInfoMdl() {
		return aryPatchInfoM;
	}
	
	public static BIPatchInfoModel ConvertBIPatchInfoModel(BIPatchInfo in_patchInfo)
	{
		if ( in_patchInfo.aryPatchInfo == null )
			return null;
		int nL = in_patchInfo.aryPatchInfo.length ;
		BIPatchInfoModel patchInfoModel = new BIPatchInfoModel();
		patchInfoModel.setError_Status(in_patchInfo.ERROR_NONEW_PATCHES_AVAILABLE);
		patchInfoModel.aryPatchInfoM = new PatchInfoModel[nL];	
		
		for (int i=0; i< nL; i++)
		{
			patchInfoModel.aryPatchInfoM[i] = new PatchInfoModel();
				// product information
			patchInfoModel.aryPatchInfoM[i].setMajorversion(in_patchInfo.aryPatchInfo[i].getMajorversion());
			patchInfoModel.aryPatchInfoM[i].setMinorVersion(in_patchInfo.aryPatchInfo[i].getMinorVersion());
			patchInfoModel.aryPatchInfoM[i].setServicePack(in_patchInfo.aryPatchInfo[i].getServicePack());

			// //patch information
			patchInfoModel.aryPatchInfoM[i].setPackageID(in_patchInfo.aryPatchInfo[i].getPackageID());
			patchInfoModel.aryPatchInfoM[i].setPatchUpdateName(in_patchInfo.aryPatchInfo[i].getPackageUpdateName());
			patchInfoModel.aryPatchInfoM[i].setPatchDependency(in_patchInfo.aryPatchInfo[i].getPackageDepy());
			patchInfoModel.aryPatchInfoM[i].setPublishedDate(in_patchInfo.aryPatchInfo[i].getPublishedDate());
			patchInfoModel.aryPatchInfoM[i].setDescription(in_patchInfo.aryPatchInfo[i].getDescription());
			patchInfoModel.aryPatchInfoM[i].setPatchDownloadLocation(in_patchInfo.aryPatchInfo[i].getPatchDownloadLocation());
			patchInfoModel.aryPatchInfoM[i].setPatchURL(in_patchInfo.aryPatchInfo[i].getPatchURL());
			patchInfoModel.aryPatchInfoM[i].setRebootRequired(in_patchInfo.aryPatchInfo[i].getRebootRequired());
			patchInfoModel.aryPatchInfoM[i].setSize(in_patchInfo.aryPatchInfo[i].getSize());
			patchInfoModel.aryPatchInfoM[i].setPatchVersionNumber(in_patchInfo.aryPatchInfo[i].getPatchVersionNumber());
			patchInfoModel.aryPatchInfoM[i].setAvailableStatus(in_patchInfo.aryPatchInfo[i].getAvailableStatus());
			patchInfoModel.aryPatchInfoM[i].setDownloadStatus(in_patchInfo.aryPatchInfo[i].getDownloadStatus());
			patchInfoModel.aryPatchInfoM[i].setInstallStatus(in_patchInfo.aryPatchInfo[i].getInstallStatus());
			patchInfoModel.aryPatchInfoM[i].setErrorMessage(in_patchInfo.aryPatchInfo[i].getErrorMessage());
			patchInfoModel.aryPatchInfoM[i].setError_Status(in_patchInfo.aryPatchInfo[i].getError_Status());
			
			patchInfoModel.setErrorMessage(in_patchInfo.getErrorMessage());
			patchInfoModel.setError_Status(in_patchInfo.getError_Status());
			patchInfoModel.setInstallStatus(in_patchInfo.getInstallStatus());
			patchInfoModel.setAvailableStatus(in_patchInfo.getAvailableStatus());
			patchInfoModel.setDownloadStatus(in_patchInfo.getDownloadStatus());
			patchInfoModel.setPatchVersionNumber(in_patchInfo.getPatchVersionNumber());
		}

		return patchInfoModel;
	}
}
