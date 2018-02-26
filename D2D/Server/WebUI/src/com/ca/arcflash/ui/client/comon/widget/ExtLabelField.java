package com.ca.arcflash.ui.client.comon.widget;

import com.ca.arcflash.ui.client.UIContext;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.form.LabelField;

public class ExtLabelField extends LabelField {
	 @Override
	 public void setValue(Object value) {
		 String tmp = value == null?"":value.toString();
		 if(!Util.isEmptyString(tmp)){
			 tmp = UIContext.escapeHTML(tmp);
		 }
		 super.setValue(tmp);
	 }
}
