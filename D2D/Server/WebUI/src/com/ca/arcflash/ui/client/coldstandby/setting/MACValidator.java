package com.ca.arcflash.ui.client.coldstandby.setting;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Validator;

public class MACValidator implements Validator {
	
	public static native boolean validateMacAddress(String macaddr) /*-{
		   var reg1 = /^[A-Fa-f0-9]{1,2}\-[A-Fa-f0-9]{1,2}\-[A-Fa-f0-9]{1,2}\-[A-Fa-f0-9]{1,2}\-[A-Fa-f0-9]{1,2}\-[A-Fa-f0-9]{1,2}$/;
		   var reg2 = /^[A-Fa-f0-9]{1,2}\:[A-Fa-f0-9]{1,2}\:[A-Fa-f0-9]{1,2}\:[A-Fa-f0-9]{1,2}\:[A-Fa-f0-9]{1,2}\:[A-Fa-f0-9]{1,2}$/;
		   if (reg1.test(macaddr)) {
		      return true;
		   }else if (reg2.test(macaddr)) {
		      return true;
		   } else {
		      return false;
		   }
	}-*/;
	
	@Override
	public String validate(Field<?> field, String value) {
		if (validateMacAddress(value))
			return null;
		else
			return "Invalid MAC address";
	}

}
