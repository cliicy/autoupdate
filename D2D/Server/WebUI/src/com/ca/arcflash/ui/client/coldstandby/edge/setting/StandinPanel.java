package com.ca.arcflash.ui.client.coldstandby.edge.setting;

import com.ca.arcflash.jobscript.base.JobType;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.heartbeat.HeartBeatJobScript;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.coldstandby.DisclourePanelImageBundles;
import com.ca.arcflash.ui.client.common.Utils;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

class StandinPanel extends LayoutContainer {

	Radio radioAuto;
	Radio radioManual;

	NumberField timeoutField;
	NumberField frequencyField;
	
	public StandinPanel() {
		this.ensureDebugId("4d78f3c5-5e15-4f87-ba7d-d6aa2fb1f5cb");

		LayoutContainer container = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(1);
		tl.setWidth("100%");
		tl.setCellPadding(2);
		container.ensureDebugId("d4af7241-b322-48c7-8494-e7cc1e447ca1");
		container.setLayout(tl);
		container.setScrollMode(Scroll.AUTO);
		
		//title and description
		Label titleLabel = new Label(UIContext.Constants.coldStandbySettingStandinTitle());
		titleLabel.ensureDebugId("568dae9b-06b6-46e0-8ed8-1c92a287e2f9");
		titleLabel.setStyleName("coldStandbySettingTitle");
		container.add(titleLabel);

		Label descriptionLabel = new Label(UIContext.Constants.coldStandbySettingStandinDescription());
		descriptionLabel.ensureDebugId("57954495-e3e2-4bf4-bb84-60e1a7c1d863");
		descriptionLabel.setStyleName("coldStandbySettingDescription");
		container.add(descriptionLabel);

		container.add(getRecoveryLayout());
		
		container.add(getHeatbeatLayout());

		this.add(container);
	}

	@SuppressWarnings("deprecation")
	private Widget getHeatbeatLayout() {
		DisclosurePanel heartBeatPanel = new DisclosurePanel(
				(DisclourePanelImageBundles) GWT.create(DisclourePanelImageBundles.class),
				UIContext.Constants.coldStandbySettingStandinHeartBeatProperties(), false);
		heartBeatPanel.ensureDebugId("1ea58164-29f8-4813-9392-00f07d0137bb");
		heartBeatPanel.setWidth("100%");
		heartBeatPanel.setStylePrimaryName("gwt-DisclosurePanel-coldStandby");
		heartBeatPanel.setOpen(true);

		LayoutContainer heatbeatContainer = new LayoutContainer();
		TableLayout heatbeatTabLayout = new TableLayout();
		heatbeatTabLayout.setColumns(3);
		heatbeatTabLayout.setCellPadding(2);
		heatbeatContainer.ensureDebugId("7cdfe74c-6dbc-416d-9f61-4cf7ee8d60eb");
		heatbeatContainer.setLayout(heatbeatTabLayout);
		
		Label label = new Label();
		label.ensureDebugId("afb91dc3-9721-489b-80c7-d4e1761b704f"); //$NON-NLS-1$
		label.setStyleName("setting-text-label"); //$NON-NLS-1$
		label.setText(UIContext.Constants.HeartBeatPropertiesWindowTimeout()); //$NON-NLS-1$
		heatbeatContainer.add(label);

		timeoutField = new NumberField();
		timeoutField.ensureDebugId("5c341c76-3b84-4d79-bad1-5a6f28642cb3"); //$NON-NLS-1$
		timeoutField.setMinValue(1);
		timeoutField.setMaxValue(60 * 60);
		timeoutField.setValue(30);
		timeoutField.setAllowBlank(false);
		timeoutField.setAllowDecimals(false);
		timeoutField.setRegex("[1-9][0-9]*");
		timeoutField.getMessages().setRegexText(UIContext.Constants.coldStandbySettingInvalidInteger());
		Utils.addToolTip(timeoutField, UIContext.Constants.coldStandbySettingHeartbeatTimeoutTip());
		heatbeatContainer.add(timeoutField);

		label = new Label();
		label.setStyleName("setting-text-label"); //$NON-NLS-1$
		label.setText(UIContext.Constants.seconds());
		label.getElement().getStyle().setColor("DarkGray"); //$NON-NLS-1$
		heatbeatContainer.add(label);

		label = new Label();
		label.ensureDebugId("9d731d62-7e7e-482d-a992-44f6d1cb65e0"); //$NON-NLS-1$
		label.setStyleName("setting-text-label"); //$NON-NLS-1$
		label.setText(UIContext.Constants.HeartBeatPropertiesWindowFrequency()); //$NON-NLS-1$
		heatbeatContainer.add(label);

		frequencyField = new NumberField();
		frequencyField.ensureDebugId("3f592f99-69f1-4dc5-95d3-6610a1d57946"); //$NON-NLS-1$
		frequencyField.setMinValue(1);
		frequencyField.setMaxValue(60 * 60);
		frequencyField.setValue(5);
		frequencyField.setAllowBlank(false);
		frequencyField.setAllowDecimals(false);
		frequencyField.setRegex("[1-9][0-9]*");
		frequencyField.getMessages().setRegexText(UIContext.Constants.coldStandbySettingInvalidInteger());
		Utils.addToolTip(frequencyField, UIContext.Constants.coldStandbySettingHeartbeatFrequencyTip());
		heatbeatContainer.add(frequencyField);

		label = new Label();
		label.setStyleName("setting-text-label"); //$NON-NLS-1$
		label.setText(UIContext.Constants.seconds());
		label.getElement().getStyle().setColor("DarkGray"); //$NON-NLS-1$
		heatbeatContainer.add(label);

		heartBeatPanel.add(heatbeatContainer);
		
		return heartBeatPanel;
	}
	
	
	@SuppressWarnings("deprecation")
	private Widget getRecoveryLayout() {
		DisclosurePanel recoveryPanel = new DisclosurePanel(
				(DisclourePanelImageBundles) GWT
						.create(DisclourePanelImageBundles.class),
				UIContext.Constants.coldStandbySettingStandinRecovery(), false);
		recoveryPanel.ensureDebugId("47553451-d84b-482f-97bb-32c33012b09c");
		recoveryPanel.setWidth("100%");
		recoveryPanel.setStylePrimaryName("gwt-DisclosurePanel-coldStandby");
		recoveryPanel.setOpen(true);

		LayoutContainer recoveryContainer = new LayoutContainer();
		TableLayout recoveryTabLayout = new TableLayout();
		recoveryTabLayout.setColumns(1);
		recoveryTabLayout.setCellPadding(4);
		recoveryTabLayout.setWidth("100%");
		recoveryContainer.ensureDebugId("a6dd9934-d722-41b9-af21-76243c778ef8");
		recoveryContainer.setLayout(recoveryTabLayout);
		
		RadioGroup rgRecoveryType = new RadioGroup();
		radioManual = new Radio(); //new RadioButton("RecoveryType");
		rgRecoveryType.add(radioManual);
		radioManual.ensureDebugId("dbc4ef33-d3de-44de-9a8b-c664ebacd043");
		radioManual.setBoxLabel(UIContext.Constants
				.coldStandbySettingStandinRecoveryTypeManual());
//		radioManual.setStyleName("panel-text-value");
		radioManual.getElement().getStyle().setPaddingLeft(0, Unit.PX);
		radioManual.setTitle(UIContext.Constants.coldStandbySettingHeartbeatManualTip());
		radioManual.setValue(true);
		recoveryContainer.add(radioManual);

		radioAuto = new Radio(); //new RadioButton("RecoveryType");
		rgRecoveryType.add(radioAuto);
		radioAuto.ensureDebugId("e7d7e15d-8910-4b68-83b8-465b3e61881e");
//		radioAuto.setStyleName("panel-text-value");
		radioAuto.getElement().getStyle().setPaddingLeft(0, Unit.PX);
		radioAuto.setBoxLabel(UIContext.Constants
				.coldStandbySettingStandinRecoveryTypeAutomatic());
		//radioAuto.setValue(true);
		radioAuto.setTitle(UIContext.Constants.coldStandbySettingHeartbeatAutoTip());
		recoveryContainer.add(radioAuto);
		recoveryContainer.add(new Html("<HR>"));

		recoveryPanel.add(recoveryContainer);
		
		return recoveryPanel;
		
	}
	@Override
	public void setEnabled(boolean enabled) {
		//super.setEnabled(enabled);
		
		radioAuto.setEnabled(enabled);
		radioManual.setEnabled(enabled);

		timeoutField.setEnabled(enabled);
		frequencyField.setEnabled(enabled);
		
	}
	
	
	public int validate() {
		boolean result = timeoutField.validate() && frequencyField.validate();
		if(result){
			if(timeoutField.getValue().intValue()<=frequencyField.getValue().intValue()){
				return 1;
			}
			else{
				return 0;
			}
		}
		return -1;
	}

	protected void populateBeatJobScript(HeartBeatJobScript script) {
		/*script.setHeartBeatMonitorHostName(textFieldMonitorServer.getValue());
		script.setHeartBeatMonitorPort(textFieldPort.getValue().intValue());
		script.setHeartBeatMonitorUserName(textFieldUserName.getValue());
		script.setHeartBeatMonitorPassword(textFieldPassword.getValue());
		script.setHeartBeatMonitorProtocol(getProtocol().toString());*/
		
		script.setJobType(JobType.HeartBeat);
		script.setHeartBeatFrequencyInSeconds(frequencyField.getValue().longValue());
		script.setHeatBeatTimeoutInSeconds(timeoutField.getValue().intValue());
	

	}

	protected void populateFailoverJobScript(FailoverJobScript failoverScript) {
	
		failoverScript.setHeartBeatFailoverTimeoutInSecond(timeoutField.getValue().intValue());
		failoverScript.setAutoFailover(getFailoverJobScriptWay());

		/*WizardContext context = WizardContext.getWizardContext();
		if (context.getVirtulizationType() == VirtualizationType.HyperV) {
			
			HyperV hyperV = (HyperV) failoverScript.getFailoverMechanism().get(0);
			hyperV.setHostName(context.getMonitorServer());
			hyperV.setUserName(context.getMonitorUsername());
			hyperV.setPassword(context.getMonitorPassword());
		}*/
	}

	protected void populateUI(HeartBeatJobScript heartBeatScript,FailoverJobScript failoverScript) {
		
		if (failoverScript != null) {
			radioAuto.setValue(failoverScript.isAutoFailover());
			radioManual.setValue(!failoverScript.isAutoFailover());
		}
		
		timeoutField.setValue(heartBeatScript.getHeatBeatTimeoutInSeconds());
		frequencyField.setValue(heartBeatScript.getHeartBeatFrequencyInSeconds());
		
	}


	private Boolean getFailoverJobScriptWay() {
		if (radioAuto.getValue())
			return true;
		else
			return false;
	}

}
