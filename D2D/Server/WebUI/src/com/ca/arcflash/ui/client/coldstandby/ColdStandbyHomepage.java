package com.ca.arcflash.ui.client.coldstandby;

import com.ca.arcflash.ha.model.ARCFlashNode;
import com.ca.arcflash.ui.client.coldstandby.event.ServerSelectionChangedEvent;
import com.ca.arcflash.ui.client.coldstandby.event.ServerSelectionChangedEventHandler;
import com.ca.arcflash.ui.client.coldstandby.event.SettingChangedEvent;
import com.ca.arcflash.ui.client.coldstandby.event.SettingChangedEventHandler;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class ColdStandbyHomepage extends LayoutContainer{
	private MonitorPanel monitorPanel = new MonitorPanel();
	private ProvisionPointsPanel provistionPanel = new ProvisionPointsPanel();
	private VMInformationPanel vmInformationPanel = new VMInformationPanel();
	
	private VirtualConversionSummaryPanel summaryPanel;
	
	public ColdStandbyHomepage(){
		
		ColdStandbyManager.getInstance().setHomepage(this);
		
		setScrollMode(Scroll.AUTOY);
		RowLayout row = new RowLayout(Orientation.VERTICAL);
		row.setAdjustForScroll(true);
		setLayout(row);
		
		add(monitorPanel, new RowData(1, -1, new Margins(0, 0, 4, 0)));
		
		summaryPanel = new VirtualConversionSummaryPanel();
		add(summaryPanel, new RowData(1, -1, new Margins(0, 0, 4, 0)));
		
		LayoutContainer chilCont = new LayoutContainer();
		chilCont.setHeight("335px");
		RowLayout chLayout = new RowLayout(Orientation.HORIZONTAL);
		 
		chilCont.setLayout(chLayout);
		
		chilCont.add(vmInformationPanel,  new RowData(0.5, 1, new Margins(0, 2, 0, 0)));
		
		chilCont.add(provistionPanel, new RowData(0.5, 1, new Margins(0, 0, 0, 2)));		
		add(chilCont,  new RowData(1, -1, new Margins(0, 0, 4, 0)));
			
		ColdStandbyManager.getInstance().registerEventHandler(new SettingChangedEventHandler(){
			
			@Override
			public void onAddContact(SettingChangedEvent event) {
				if (event.getJobScriptCombo()!=null){
					renderHomepage();
				}
			}
			
		});
		
		ColdStandbyManager.getInstance().registerEventHandler(new ServerSelectionChangedEventHandler() {

			@Override
			public void onServerChanged(ServerSelectionChangedEvent event) {
//				if(!ColdStandbyManager.getInstance().getVCNavigator().isSelectMoniteeFromMonitor()) {
//					coldStandByService.connectMoniteeServer(ColdStandbyManager.getInstance().getVCNavigator().getSelectServerNode(), new BaseAsyncCallback<Void>(){
//						@Override
//						public void onFailure(Throwable caught) {
//							renderWelcomePage();
//						}
//						
//						@Override
//						public void onSuccess(Void result) {
//							refreshUI();
//						}
//						});
//				}
//				else
				ARCFlashNode selectServerNode = ColdStandbyManager.getInstance().getVCNavigator().getSelectServerNode();
				if(selectServerNode != null && !selectServerNode.isMonitor())
					renderHomepage(); /*Since it has been added to navigator, it surely has been configured with VC Setting.
					So, no need to call ColdStandbyManager.getInstance().getHomepage().refreshUI(). */
			}
			
		});
	}
	
	@Override
	protected void afterRender() {
		super.afterRender();
//		new Timer() {
//			public void run() {
				refreshUI();
//			}
//		}.schedule(1000);
	}
	
	
	public void refreshUI() {
//		mask("Loading data...");
//		mainPanelCardLayout.setActiveItem(welcome);
//		welcome.maskPanel();
//		String vmInstanceUUID = ColdStandbyManager.getVMInstanceUUID();
//		coldStandByService.getJobScriptCombo(vmInstanceUUID, new BaseAsyncCallback<JobScriptCombo>(){
//
//			@Override
//			public void onFailure(Throwable caught) {
//				welcome.unmaskPanel();
//				renderWelcomePage();
//			}
//
//			@Override
//			public void onSuccess(JobScriptCombo result) {
//				welcome.unmaskPanel();
//				if (result == null || result.getFailoverJobScript() == null || result.getHbJobScript() == null || result.getRepJobScript() == null) {
//					renderWelcomePage();
//				}
//				
//				else
					renderHomepage();
//			}
//			
//		});
	}
	
	public void deactivate(){
		
	}
	
	public void activate(){
		
	}
	
//	public void renderWelcomePage() {
//		mainPanelCardLayout.setActiveItem(welcome);
//		ColdStandbyManager.getInstance().getVCNavigator().unlockNavigator(3);
//	}

	public void renderHomepage() {
//		mainPanelCardLayout.setActiveItem(homepageContent);
		summaryPanel.update();
		vmInformationPanel.update();
		//the following update is removed because Summary Panel will also fetch provision points and 
		//we update this panel when summary panel updates.
//		provistionPanel.update();
	}
	
	public ProvisionPointsPanel getProvisionPanel() {
		return provistionPanel;
	}
	
	public VirtualConversionSummaryPanel getSummaryPanel() {
		return summaryPanel;
	}
	
	public void recreateNonResizablePanels() {
//		if(mainPanelCardLayout.getActiveItem() == homepageContent) {
			vmInformationPanel.reconstructTable();
			provistionPanel.reconstructGrid();
			layout();
//		}
//		else {
//			welcome.reconstructFlexTable();
//		}
	}

	public void removeNonResizablePanels() {
//		if(mainPanelCardLayout.getActiveItem() == homepageContent) {
			vmInformationPanel.removeFlexTable();
			provistionPanel.removeGrid();
//		}
//		else {
//			welcome.removeFlexTable();
//		}
			
	}
	
}
