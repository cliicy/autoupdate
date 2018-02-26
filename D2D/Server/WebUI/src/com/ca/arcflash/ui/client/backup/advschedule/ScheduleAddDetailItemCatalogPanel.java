package com.ca.arcflash.ui.client.backup.advschedule;

import com.ca.arcflash.ui.client.FlashUIConstants;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseComboBox;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.ComboBox;

public class ScheduleAddDetailItemCatalogPanel extends LayoutContainer {
	private ComboBox<ModelData> catalogBox;
	private static FlashUIConstants uiConstants=UIContext.Constants;
	private final static String[] setCatalog = { uiConstants.catalogOff(), uiConstants.catalogOn()};

	public ScheduleAddDetailItemCatalogPanel(){		
		addCatalogRow(this);
	}
	
	private void addCatalogRow(LayoutContainer container){
		ListStore<ModelData> catalogStore = new ListStore<ModelData>();
		catalogBox = new BaseComboBox<ModelData>();
		catalogBox.setDisplayField("text");
		catalogBox.setValueField("setCatalog");
		catalogBox.setStore(catalogStore);
		catalogBox.setEditable(false);
		catalogBox.setAllowBlank(false);
		catalogBox.setWidth(180);

		ModelData selectModel = null;
		for (int i = 0; i < setCatalog.length; i++) {
			ModelData md = new BaseModelData();
			md.set("text", setCatalog[i]);
			md.set("setCatalog", i);
			catalogStore.add(md);
			
			if(uiConstants.catalogOff().equals(setCatalog[i])) {
				selectModel = md;
			}
		}
		catalogBox.setValue(selectModel);
		
		this.add(catalogBox);

		}
	
	public boolean getValue(){
		if((Integer)catalogBox.getValue().get("setCatalog") == 1)
			return true;
		else return false;
	}	

	public void setValue(int setCatalog){
		for(ModelData catalogModel : catalogBox.getStore().getModels()) {
			if(((Integer)catalogModel.get("setCatalog")) == setCatalog){
				catalogBox.setValue(catalogModel);
				return;
			}			
		}		
	}
}
