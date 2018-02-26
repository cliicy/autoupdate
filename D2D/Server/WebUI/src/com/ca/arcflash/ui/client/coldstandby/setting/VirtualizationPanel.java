package com.ca.arcflash.ui.client.coldstandby.setting;

import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.failover.VirtualizationType;
import com.ca.arcflash.jobscript.replication.ReplicationJobScript;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.coldstandby.DisclourePanelImageBundles;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

class VirtualizationPanel extends WizardPage {
	private RadioButton radioVMware;
	private RadioButton radioHyperV;
	
	VirtualizationHyperVPanel hyperVPanel;
	VirtualizationVMWarePanel vmwarePanel;
	private VerticalPanel contentVerticalPanel = new VerticalPanel();
	
	public VirtualizationPanel(){
		this.ensureDebugId("8c6aa1f1-6f7a-4ae0-b350-f672bfd3af4c");
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void render(Element target, int index) {
		super.render(target, index);
		
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.ensureDebugId("4210d5b9-b213-4638-96a4-0788c96dd24a");
		verticalPanel.setWidth("100%"); 
		
		DisclosurePanel serverSettingPanel = new DisclosurePanel(
				(DisclourePanelImageBundles)GWT.create(DisclourePanelImageBundles.class),
				UIContext.Constants.coldStandbySettingVirtualizationServerSetting(), false);
		serverSettingPanel.ensureDebugId("50083c32-68ab-4c33-a5c8-dc8e1724e03a");
		serverSettingPanel.setWidth("100%"); 
		serverSettingPanel.setStylePrimaryName("gwt-DisclosurePanel-coldStandby"); 
		serverSettingPanel.setOpen(true);

		contentVerticalPanel.ensureDebugId("a25056a0-08cd-46f9-81dc-4f9c0855c842");
		contentVerticalPanel.setWidth("100%"); 
		contentVerticalPanel.setSpacing(8);
		
		serverSettingPanel.add(contentVerticalPanel);
		contentVerticalPanel.add(setupVirtulizationType());
		renderVMwareVPanel();
		
		verticalPanel.add(serverSettingPanel);
		this.add(verticalPanel);
	}

	private Widget setupVirtulizationType() {
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.ensureDebugId("5d2d6795-6abc-4b1a-8b11-912a58da4e4d");
		
		Label titleLabel = new Label();
		titleLabel.setStyleName("setting-text-label"); 
		titleLabel.setText(UIContext.Constants.coldStandbySettingVirtualizationType()); 
		hPanel.add(titleLabel);
		
		radioVMware = new RadioButton("VirtualizationType"); 
		radioVMware.ensureDebugId("36772876-44c7-428b-b6d0-b2114731a533");
		radioVMware.setStyleName("panel-text-value"); 
		radioVMware.getElement().getStyle().setPaddingLeft(0, Unit.PX);
		radioVMware.setText(UIContext.Constants.coldStandbySettingVirtualizationTypeVMware()); 
		radioVMware.setValue(true);
		
		radioHyperV = new RadioButton("VirtualizationType"); 
		radioHyperV.ensureDebugId("3e234718-745c-4358-8f6b-ffe8d39c8f8f");
		radioHyperV.setText(UIContext.Constants.coldStandbySettingVirtualizationHyperV()); 
		radioHyperV.setStyleName("panel-text-value"); 
		radioHyperV.getElement().getStyle().setPaddingLeft(6, Unit.PX);
		
		
		radioVMware.addValueChangeHandler(new ValueChangeHandler<Boolean>()
		{
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if (event.getValue())
					renderVMwareVPanel();
				else
					renderHyperVPanel();
			}
			
		});				
		
		radioHyperV.addValueChangeHandler(new ValueChangeHandler<Boolean>()
		{
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if (event.getValue())
					renderHyperVPanel();
				else
					renderVMwareVPanel();
			}
			
		});					
		
		HorizontalPanel typePanel = new HorizontalPanel();
		typePanel.ensureDebugId("9f21bdee-899a-49c5-ae26-1e164912a208");
		typePanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		typePanel.add(radioVMware);
		typePanel.add(radioHyperV);
		
		hPanel.add(typePanel);
		
		return hPanel;
	}
	
	private void renderHyperVPanel(){
		if (contentVerticalPanel.getWidgetCount()>1)
			contentVerticalPanel.remove(1);	
		
		if (hyperVPanel == null){
			hyperVPanel = new VirtualizationHyperVPanel();
			hyperVPanel.ensureDebugId("b6b7b665-b6e3-49c0-ad15-bc4bfeda386c");
		}
		contentVerticalPanel.add(hyperVPanel);	
	}
	
	private void renderVMwareVPanel(){
		if (contentVerticalPanel.getWidgetCount()>1)
			contentVerticalPanel.remove(1);
		
		if (vmwarePanel == null){
			vmwarePanel = new VirtualizationVMWarePanel();
			vmwarePanel.ensureDebugId("daa5f7ef-a13a-489f-859e-e4abb998a009");
		}
		contentVerticalPanel.add(vmwarePanel);
	}
	
	@Override
	protected boolean validate() {
		if (radioHyperV.getValue())
			return hyperVPanel.validate();
		else
			return vmwarePanel.validate();
	}

	@Override
	public String getDescription() {
		return UIContext.Constants.coldStandbySettingVirtualizationDescription();
	}

	@Override
	public String getTitle() {
		return UIContext.Constants.coldStandbySettingVirtualizationTitle();
	}
	
	protected VirtualizationType getVirtulizationType(){
		if (radioHyperV.getValue())
			return VirtualizationType.HyperV;
		else
			return vmwarePanel.getVirtulizationType();
	}
	
	protected boolean isSelectHyperV() {
		if(radioHyperV.getValue()){
			return true;
		}
		else {
			return false;
		}
	}
	
	protected void setVMWareType(int type) {
		vmwarePanel.setVMWareType(type);
	}
	protected void setVMWareVersion(String version) {
		vmwarePanel.setVMWareVersion(version);
	}
	

	
	protected void populateFailoverJobScript(FailoverJobScript failoverScript){
		if (radioHyperV.getValue())
			hyperVPanel.populateFailoverJobScript(failoverScript);
		else{
			vmwarePanel.populateFailoverJobScript(failoverScript);
		}
	}
	
	protected void populateUI(FailoverJobScript failoverScript, ReplicationJobScript replicationScript){
		if (failoverScript.getVirtualType() == VirtualizationType.HyperV){
			radioHyperV.setValue(true);
			renderHyperVPanel();
			hyperVPanel.populateUI(failoverScript, replicationScript);
		}else{
			radioVMware.setValue(true);
			renderVMwareVPanel();
			vmwarePanel.populateUI(failoverScript);
		}
	}

	public void populateReplicationJobScript(ReplicationJobScript replicationScript) {
		if (radioHyperV.getValue())
			hyperVPanel.populateReplicationJobScript(replicationScript);
		else
			vmwarePanel.populateReplicationJobScript(replicationScript);
			
	}

}
