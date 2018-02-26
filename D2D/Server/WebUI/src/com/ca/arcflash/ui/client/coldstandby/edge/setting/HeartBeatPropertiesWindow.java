package com.ca.arcflash.ui.client.coldstandby.edge.setting;

import com.ca.arcflash.jobscript.heartbeat.HeartBeatJobScript;
import com.ca.arcflash.ui.client.UIContext;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.ui.Label;

class HeartBeatPropertiesWindow extends Window {
	private static final int HEIGHT						=	160;
	private static final int WIDTH						=	300;
	
	static final int TIMEOUT_DEFAULT					=	30;
	private static final int TIMEOUT_MAX				=	60*60;
	private static final int TIMEOUT_MIN				=	1;
	
	static final int FREQUENCY_DEFAULT					=	5;
	private static final int FREQUENCY_MAX				=	60*60;
	private static final int FREQUENCY_MIN				=	1;
	
	private HeartBeatPropertiesWindow thisWindow;
	
	NumberField timeoutField;
	NumberField frequencyField;
	
	public HeartBeatPropertiesWindow(){
		this.thisWindow = this;
		this.setResizable(false);
		this.setWidth(WIDTH);
		this.setHeight(HEIGHT);
		this.setHeadingHtml(UIContext.Constants.HeartBeatPropertiesWindowTitle()); //$NON-NLS-1$
		this.ensureDebugId("cad8cc1d-b0ba-4a9e-bead-59caae91730c"); //$NON-NLS-1$
		
		setupContents();
		setupButtons();
	}

	private void setupButtons() {
		Button okButton = new Button();
		okButton.ensureDebugId("e6015399-5935-470c-967e-9b9289003efe"); //$NON-NLS-1$
		okButton.setText(UIContext.Constants.ok());
		this.addButton(okButton);
		
		Button cancelButton = new Button();
		cancelButton.ensureDebugId("a14c15dd-b36f-4427-84d5-89bc7a58bda8"); //$NON-NLS-1$
		cancelButton.setText(UIContext.Constants.cancel());
		this.addButton(cancelButton);
		
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				if (thisWindow.validate())
					thisWindow.hide();
			}
			
		});
		
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				thisWindow.hide();
			}
			
		});
	}

	private void setupContents() {
		TableLayout tableLayout = new TableLayout();
		tableLayout.setCellPadding(2);
		tableLayout.setCellSpacing(2);
		tableLayout.setCellVerticalAlign(VerticalAlignment.MIDDLE);
		tableLayout.setColumns(3);
		this.setLayout(tableLayout);
		
		Label label = new Label();
		label.ensureDebugId("afb91dc3-9721-489b-80c7-d4e1761b704f"); //$NON-NLS-1$
		label.setStyleName("setting-text-label");  //$NON-NLS-1$
		label.setText(UIContext.Constants.HeartBeatPropertiesWindowTimeout()); //$NON-NLS-1$
		this.add(label);
		
		timeoutField = new NumberField();
		timeoutField.ensureDebugId("5c341c76-3b84-4d79-bad1-5a6f28642cb3"); //$NON-NLS-1$
		timeoutField.setMinValue(TIMEOUT_MIN);
		timeoutField.setMaxValue(TIMEOUT_MAX);
		timeoutField.setAllowBlank(false);
		timeoutField.setAllowDecimals(false);
		this.add(timeoutField);
		
		label = new Label();
		label.setStyleName("setting-text-label");  //$NON-NLS-1$
		label.setText(UIContext.Constants.seconds());
		label.getElement().getStyle().setColor("DarkGray"); //$NON-NLS-1$
		this.add(label);
		
		label = new Label();
		label.ensureDebugId("9d731d62-7e7e-482d-a992-44f6d1cb65e0"); //$NON-NLS-1$
		label.setStyleName("setting-text-label");  //$NON-NLS-1$
		label.setText(UIContext.Constants.HeartBeatPropertiesWindowFrequency()); //$NON-NLS-1$
		this.add(label);
		
		frequencyField= new NumberField();
		frequencyField.ensureDebugId("3f592f99-69f1-4dc5-95d3-6610a1d57946"); //$NON-NLS-1$
		frequencyField.setMinValue(FREQUENCY_MIN);
		frequencyField.setMaxValue(FREQUENCY_MAX);
		frequencyField.setAllowBlank(false);
		frequencyField.setAllowDecimals(false);
		this.add(frequencyField);
		
		label = new Label();
		label.setStyleName("setting-text-label");  //$NON-NLS-1$
		label.setText(UIContext.Constants.seconds());
		label.getElement().getStyle().setColor("Grey"); //$NON-NLS-1$
		this.add(label);
	}
	
	void update(HeartBeatJobScript script){
		if (script == null){
			timeoutField.setValue(TIMEOUT_DEFAULT);
			frequencyField.setValue(FREQUENCY_DEFAULT);
			return;
		}
		
		timeoutField.setValue(script.getHeatBeatTimeoutInSeconds());
		frequencyField.setValue(script.getHeartBeatFrequencyInSeconds());
	}
	
	private boolean validate(){
		return timeoutField.validate() && frequencyField.validate();
	}
}
