package com.ca.arcflash.ui.client.backup;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.Element;

public class BackupPanel extends LayoutContainer {
	
	public void render(Element target, int index) {
		super.render(target, index);
		
		Button button = new Button();
		button.ensureDebugId("623DAB2E-04F5-4d20-9657-41DCF9969945");
		button.setText("Backup Settings");
		button.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				BackupSettingsWindow dlg = new BackupSettingsWindow();
				dlg.show();
			}

			});
		this.add(button);
		
		
		
	}
}
