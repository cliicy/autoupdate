package com.ca.arcflash.ui.client.coldstandby.edge.setting;

import com.ca.arcflash.ui.client.UIContext;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Validator;

public class IPValidator implements Validator {
	private static final String pattern = "\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b";
	
	@Override
	public String validate(Field<?> field, String value) {
		if (value.matches(pattern))
			return null;
		else
			return UIContext.Constants.coldStandbySettingInvalidIPAddress();
	}

}
