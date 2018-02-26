package com.ca.arcflash.ui.client.restore;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Label;

public class RestorePanel extends LayoutContainer {
	public void render(Element target, int index) {
		super.render(target, index);
		this.add(new Label("Restore"));
		
		Button button = new Button();
		button.ensureDebugId("7F2352DA-5AC0-4b1d-8C29-A279B657AECF");
		button.setText("Restore Wizard");
		button.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				RestoreWizardWindow dlg = new RestoreWizardWindow();
				dlg.setResizable(false);
				dlg.show();
			}

		});
		this.add(button);
	}
}
