package com.ca.arcflash.ui.client.restore;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.model.BackupD2DModel;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.widget.form.StoreFilterField;
import com.google.gwt.event.dom.client.KeyCodes;

public class D2DNameFilter4Restore extends StoreFilterField<BackupD2DModel> {
	
	private boolean doFilter = false;
	
	public D2DNameFilter4Restore(){
		this.setToolTip(UIContext.Constants.filterFieldTooltip());
		super.addInputStyleName("arcserve-hidden-ms-clear");
		this.addKeyListener(new KeyListener(){
			
			@Override
			public void componentKeyUp(ComponentEvent event) {
				super.componentKeyUp(event);
				doFilter = KeyCodes.KEY_ENTER == event.getKeyCode();
			}
			
		});
	}
	
	@Override
	protected boolean doSelect(Store<BackupD2DModel> store,
			BackupD2DModel parent, BackupD2DModel record,
			String property, String filter) {
		return !doFilter || doFilter(record, filter);
	}

	private boolean doFilter(BackupD2DModel record, String filter) {
		String regex = filter == null ? "" : filter.trim().toLowerCase();
		String value = record.getHostName();
		
		if (regex.isEmpty()) {
			return true;
		}
		
		if (!regex.endsWith("*")) {
			regex += "*";
		}
		
		regex = regex.replace(".", "\\.").replaceAll("[*]+", ".*").replaceAll("[?]", ".");
		
		value = value == null ? "" : value.toLowerCase();
		return value.matches(regex);
	}
}	
