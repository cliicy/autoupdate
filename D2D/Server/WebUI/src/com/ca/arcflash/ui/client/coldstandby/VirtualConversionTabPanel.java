package com.ca.arcflash.ui.client.coldstandby;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.AppType;
import com.ca.arcflash.ui.client.homepage.CopyRightPanel;
import com.ca.arcflash.ui.client.homepage.SocialNetworkingPanel;
import com.ca.arcflash.ui.client.homepage.SupportPanel;
import com.ca.arcflash.ui.client.homepage.navigation.NavigationBorderLayout;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TabPanelEvent;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.Element;

public class VirtualConversionTabPanel extends TabItem {
	
//	private Viewport port;
	private LayoutContainer port = new LayoutContainer();
	private NavigationBorderLayout borderLayout;

	public VirtualConversionTabPanel() {

		ColdStandbyManager.getInstance().setColdstandbyTabPanel(this);
		setText(UIContext.Constants.vcmTabName());
//		setStyleAttribute("background-color", "white");
		
		borderLayout = new NavigationBorderLayout(AppType.VCM);
	}

	private void createContentPanel() {
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);  
		centerData.setMargins(new Margins(0, 1, 0, 1));
		final ColdStandbyHomepage homePage = new ColdStandbyHomepage();
//		container.setStyleAttribute("background-color", "white");
		
//		TableData data = new TableData();
//		data.setWidth("75%");
//		data.setVerticalAlign(Style.VerticalAlignment.TOP);
//		data.setPadding(2);
//		container.add(homePage, data);
//		
//		data = new TableData();
//		data.setWidth("25%");
//		data.setVerticalAlign(Style.VerticalAlignment.TOP);
//		data.setPadding(2);
//		container.add(createRightPanel(), data);
//		
//		port.add(container, centerData);
		
		port.add(homePage, centerData);
	}
	
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		setLayout(new FitLayout());
		port.setStyleAttribute("background-color", "#ebeff2");
		
		port.setLayout(borderLayout);

		createServerNavigatorPanel();
		createContentPanel();
		
		createRightPanel();
		createCopyRightPanel();
		
		add(port);
		
		borderLayout.hide(LayoutRegion.WEST);
		ColdStandbyManager.getInstance().startHeartBeatStateTimer();
		addListener(Events.Select, new Listener<TabPanelEvent>() {

			@Override
			public void handleEvent(TabPanelEvent be) {
				if(ColdStandbyManager.getInstance().getHomepage() != null)
	        		ColdStandbyManager.getInstance().getHomepage().removeNonResizablePanels();
				
	        	if(tabPanel != null)
	        		tabPanel.getLayout().layout();
	        	
	        	if(ColdStandbyManager.getInstance().getHomepage() != null)
	        		ColdStandbyManager.getInstance().getHomepage().recreateNonResizablePanels();
			}
			
		});
	}

	private void createCopyRightPanel() {
		BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH, 30);  		
		southData.setSplit(false);  
		southData.setCollapsible(true); 
		CopyRightPanel copyRight = new CopyRightPanel();
		port.add(copyRight, southData);
	}

	private void createServerNavigatorPanel() {
		VirtualConversionServerNavigator natigator = new VirtualConversionServerNavigator();
		
		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 245);
		westData.setSplit(false);  
		westData.setCollapsible(true); 
//		westData.setFloatable(true);
		westData.setMargins(new Margins(0,2,0,0));  
		port.add(natigator, westData);
	}
	
	private void createRightPanel() {
		
		BorderLayoutData eastData = new BorderLayoutData(LayoutRegion.EAST, 245, 250, 400);
		eastData.setSplit(true);
		eastData.setCollapsible(true);
		eastData.setFloatable(true);
		eastData.setMargins(new Margins(0, 2, 0, 0));
		
		ContentPanel rightPanel = new ContentPanel();
		rightPanel.setBodyBorder(false);
		rightPanel.setBodyStyle("backgroundColor: transparent;");
		
		rightPanel.setHeaderVisible(true);
		rightPanel.setScrollMode(Scroll.AUTOY);
		rightPanel.setBorders(false);
		
		rightPanel.setHeadingHtml(UIContext.Constants.homepageNavigation());		
		LayoutContainer container = new LayoutContainer();
		rightPanel.add(container);
		
		container.setLayout(new RowLayout());
		
		RowData rowData = new RowData();
		rowData.setMargins(new Margins(4,0,0,0));
		container.add(new ColdStandbyTaskPanel(false),rowData);
		
		rowData = new RowData();
		rowData.setMargins(new Margins(4,0,0,0));
		container.add(new SupportPanel(AppType.VCM), rowData);
		
		if (UIContext.serverVersionInfo.isShowSocialNW() != null
				&& UIContext.serverVersionInfo.isShowSocialNW()) {
			rowData = new RowData();
			rowData.setMargins(new Margins(4, 0, 0, 0));
			container.add(new SocialNetworkingPanel(AppType.VCM), rowData);
		}
		
		port.add(rightPanel, eastData);
	}

	public NavigationBorderLayout getLayout() {
		return borderLayout;
	}
}
