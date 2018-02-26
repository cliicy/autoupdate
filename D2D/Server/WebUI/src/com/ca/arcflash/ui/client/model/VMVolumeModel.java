package com.ca.arcflash.ui.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class VMVolumeModel extends BaseModelData {


	public String getVolumeID() {
		return get("volumeID");
	}

	public void setVolumeID(String volumeID) {
		set("volumneID",volumeID);
	}

	public String getDriveLetter() {
		return get("driveLetter");
	}

	public void setDriveLetter(String driveLetter) {
		set("driveLetter",driveLetter);
	}
	
	

}
