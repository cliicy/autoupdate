package com.ca.arcflash.ui.client.homepage;

import com.ca.arcflash.ui.client.model.UpdateSettingsModel;
import com.extjs.gxt.ui.client.core.El;

public interface IUpdateSettingHost {
	
	El mask(String message);
	void unmask();
	UpdateSettingsModel getUpdateSettingModel();
	void setUpdateSettingModel(UpdateSettingsModel model);

}
