package com.ca.arcflash.ui.client.homepage.navigation;


import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.AppType;
import com.ca.arcflash.ui.client.homepage.NCEPanel;
import com.ca.arcflash.ui.client.homepage.SocialNetworkingPanel;
import com.ca.arcflash.ui.client.homepage.SupportPanel;
import com.ca.arcflash.ui.client.homepage.TaskPanel;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.Element;

public class NavigationPanel extends ContentPanel
{
	protected TaskPanel taskPanel = new TaskPanel();
	protected SupportPanel supportPanel = new SupportPanel(AppType.D2D);
	protected SocialNetworkingPanel socialNWPanel = new SocialNetworkingPanel(AppType.D2D);
	protected NCEPanel ncePanel = new NCEPanel();
	
	@Override
	protected void onRender(Element parent, int pos)
	{
		this.setBodyBorder(false);
		this.setBodyStyle("backgroundColor: transparent;");
		
		super.onRender(parent, pos);
		
		this.setHeaderVisible(true);
		this.setScrollMode(Scroll.AUTOY);
		this.setBorders(false);

		this.setHeadingHtml(UIContext.Constants.homepageNavigation());		
		
		createRightPanel();
	}
	
	protected void createRightPanel()
	{
		LayoutContainer container = new LayoutContainer();
		add(container);

		container.setLayout(new RowLayout());

		RowData rowData = new RowData();
		rowData.setMargins(new Margins(4, 0, 0, 0));
		
		
		container.add(taskPanel, rowData);
		if(UIContext.customizedModel == null || UIContext.customizedModel.getShowSupportHeader()){
			container.add(supportPanel, rowData);
		}
		if(UIContext.customizedModel == null || UIContext.customizedModel.getShowSocialNetworkHeader()) {
			if (UIContext.serverVersionInfo.isShowSocialNW() != null && UIContext.serverVersionInfo.isShowSocialNW())
			{
				container.add(socialNWPanel, rowData);
			}
		}
		if(UIContext.customizedModel == null || UIContext.customizedModel.getShowNCE() == null || UIContext.customizedModel.getShowNCE()){
			container.add(ncePanel,rowData);
			refreshNCEPanel();
			
		}
	}
	
	private void refreshNCEPanel(){
		if(ncePanel!=null){
			if(UIContext.serverVersionInfo.isNCE()){
				ncePanel.setVisible(true);
			}else{
				ncePanel.setVisible(false);
			}
		}
	}
	
	public void refreshLicenseNCE() {
		refreshNCEPanel();
		if(supportPanel!=null){
			supportPanel.refreshLiveChat();
		}
	}
}
