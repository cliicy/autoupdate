package com.ca.arcflash.ui.client.common;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.Container;
import com.extjs.gxt.ui.client.widget.form.ComboBox;

public class BaseComboBox<D extends ModelData> extends ComboBox<D>{
	
	public BaseComboBox(){
		setTriggerAction(TriggerAction.ALL);
	}
	
	@Override
	protected void onResize(int width, int height) {
		super.onResize(width, height);
		getInputEl().setSize(width - 18, height, true);
	}

	public void expand() {
		super.expand();
		getListView().setWidth(getWidth());
		((Container) getListView().getParent()).setWidth(getWidth());
	}
}
