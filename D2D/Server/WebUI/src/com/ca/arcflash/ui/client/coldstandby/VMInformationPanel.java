package com.ca.arcflash.ui.client.coldstandby;

import com.ca.arcflash.jobscript.base.GenerateType;
import com.ca.arcflash.jobscript.failover.FailoverJobScript;
import com.ca.arcflash.jobscript.failover.HyperV;
import com.ca.arcflash.jobscript.failover.VMwareESX;
import com.ca.arcflash.jobscript.failover.VMwareVirtualCenter;
import com.ca.arcflash.jobscript.failover.Virtualization;
import com.ca.arcflash.jobscript.failover.VirtualizationType;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.LoadingStatus;
import com.ca.arcflash.ui.client.monitor.MonitorService;
import com.ca.arcflash.ui.client.monitor.MonitorServiceAsync;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class VMInformationPanel extends LayoutContainer {
	private final ColdStandbyServiceAsync service = GWT.create(ColdStandbyService.class);
	private final MonitorServiceAsync monitorService = GWT.create(MonitorService.class);
	private HyperVPanel hypervPanel;
	private ESXPanel esxPanel;
	private VMCenterPanel vmCenterPanel;
	private LayoutContainer contenPanel = new LayoutContainer();
	private ContentPanel panel;
	private BaseAsyncCallback<FailoverJobScript> callback;
	LoadingStatus status = new LoadingStatus();
	
	public VMInformationPanel() {
		this.setLayout(new FitLayout());
		
		status.getLoadingLabelField().setStyleAttribute("color", "DarkGray");
		status.getLoadingLabelField().setStyleAttribute("font-size", "11px");
		
		panel = new ContentPanel();
		panel.setCollapsible(true);
		panel.ensureDebugId("27780633-a6db-4960-b559-37b4b31ba942");
//		panel.setHeight(335);
		panel.setLayout(new FitLayout());
//		panel.setScrollMode(Scroll.AUTO);
	    panel.setBodyStyle("background-color: white;");
	    panel.setHeadingHtml(UIContext.Constants.coldStarndbyVMInformationTitle());
	    
	    contenPanel.setLayout(new RowLayout());
	    contenPanel.setScrollMode(Scroll.AUTOY);
	    contenPanel.setStyleAttribute("padding", "6px");
	    
	    panel.add(contenPanel);
//	    panel.setBorders(true);
//	    panel.setStyleAttribute("border-color", "#000");
//	    panel.setAutoWidth(true);
//	    panel.collapse();
//	    panel.setWidth("90%");
	    add(panel);
	    
	    callback = new BaseAsyncCallback<FailoverJobScript>(){

			@Override
			public void onFailure(Throwable caught) {
				ColdStandbyManager.getInstance().getVCNavigator().unlockNavigator();
//				panel.expand();
//				panel.unmask();
				status.hideIndicator();
				contenPanel.removeAll();
				addEmptyLabel();
				contenPanel.layout();
			}

			private void addEmptyLabel() {
				Label label = new Label();
				label.setStyleName("x-grid-empty");
				label.setText(UIContext.Constants.NA());
				contenPanel.add(label);
			}

			@Override
			public void onSuccess(FailoverJobScript script) {
				ColdStandbyManager.getInstance().getVCNavigator().unlockNavigator();
//				panel.expand();
//				panel.unmask();
				status.hideIndicator();
				if (script == null) {
					contenPanel.removeAll();
					addEmptyLabel();
					return;
				}
				UIContext.isRemoteVCM = script.getGenerateType() == GenerateType.MSPManualConversion || script.getGenerateType() == GenerateType.NoHASupport;
				Virtualization virtulation = script.getFailoverMechanism().get(0);
				if (virtulation.getVirtualizationType() == VirtualizationType.HyperV){
					if (hypervPanel == null)
						hypervPanel = new HyperVPanel();
					hypervPanel.update((HyperV)virtulation);
					if (contenPanel.getItemCount() > 0)
						contenPanel.removeAll();
					contenPanel.add(hypervPanel);
					
//					panel.removeAll();
//					panel.add(hypervPanel);
					
				}else if (virtulation.getVirtualizationType() == VirtualizationType.VMwareESX){
					if (esxPanel == null)
						esxPanel = new ESXPanel();
					esxPanel.update((VMwareESX)virtulation);
					if (contenPanel.getItemCount() > 0)
						contenPanel.removeAll();
					contenPanel.add(esxPanel);
					
//					panel.removeAll();
//					panel.add(esxPanel);
				}else if (virtulation.getVirtualizationType() == VirtualizationType.VMwareVirtualCenter){
					if (vmCenterPanel == null)
						vmCenterPanel = new VMCenterPanel();
					vmCenterPanel.update((VMwareVirtualCenter)virtulation);
					if (contenPanel.getItemCount() > 0)
						contenPanel.removeAll();
					contenPanel.add(vmCenterPanel);
					
//					panel.removeAll();
//					panel.add(vmCenterPanel);
				}
				contenPanel.layout();
			}
			
			
		};
	}
	
//	public void setContentHeight(int height){
//		panel.setHeight(height);
//	};

	public void update(){
//		panel.collapse();
//		El el = panel.mask("Loading...");
//		el.setStyleAttribute("background-color", "#e6e6e6");
		contenPanel.removeAll();
		contenPanel.add(status);
		status.showIndicator();
		contenPanel.layout();
		
		if(!ColdStandbyManager.getInstance().getVCNavigator().isSelectMoniteeFromMonitor() 
				&& ColdStandbyManager.getInstance().getVcmStatus() != null 
				&& !ColdStandbyManager.getInstance().getVcmStatus().isVcmConfigured()) {
//			callback.onSuccess(null);
			return;
		}
		
//		if(!ColdStandbyManager.getInstance().getVCNavigator().isSelectedServerAccessible()) {
		if(ColdStandbyManager.getInstance().getVCNavigator().isSelectMoniteeFromMonitor()) {
			monitorService.getFailoverJobScript(ColdStandbyManager.getInstance().getVCNavigator().getSelectServerNode().getUuid(), callback);
		}
		else {
			String vmInstanceUUID = ColdStandbyManager.getVMInstanceUUID();
			service.getFailoverJobScript(vmInstanceUUID, callback);
		}
	}
	
	public void removeFlexTable() {
//		panel.remove(contenPanel);
	}
	
	public void reconstructTable() {
//		if(jobScript != null) {
//			contenPanel =  new VerticalPanel();
//			panel.add(contenPanel);
//			callback.onSuccess(jobScript);
//		}
//		else {
//			contenPanel =  new VerticalPanel();
//			panel.add(contenPanel);
//			contenPanel.clear();
//			contenPanel.add(status);
//			status.showIndicator();
//		}
//		panel.repaint();
//		panel.recalculate();
//		panel.layout();
	}
}
