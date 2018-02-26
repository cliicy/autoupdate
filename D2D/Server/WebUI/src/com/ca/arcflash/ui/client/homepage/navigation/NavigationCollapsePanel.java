package com.ca.arcflash.ui.client.homepage.navigation;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyManager;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyTaskPanel;
import com.ca.arcflash.ui.client.common.AppType;
import com.ca.arcflash.ui.client.homepage.SocialNetworkingPanel;
import com.ca.arcflash.ui.client.homepage.SupportPanel;
import com.ca.arcflash.ui.client.homepage.TaskPanel;
import com.ca.arcflash.ui.client.vsphere.homepage.VSphereTaskPanel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.widget.CollapsePanel;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class NavigationCollapsePanel extends CollapsePanel
{
	private LayoutContainer bodyContainer;
	
	private BorderLayoutData parentData;
	
	private AppType appType = AppType.D2D;
	
	protected static ContentPanel contentPanel;

	public NavigationCollapsePanel(ContentPanel panel, BorderLayoutData data)
	{
		super(panel, data);

		parentData = data;
		createBodyContainer();
	}
	
	public NavigationCollapsePanel(ContentPanel panel, BorderLayoutData data,AppType appType)
	{
		super(panel, data);

		parentData = data;
		this.appType = appType;
		createBodyContainer();
		
	}
	
	protected void createBodyContainer()
	{
		// a layout container for the Navigation buttons
		bodyContainer = new LayoutContainer();

		TableLayout tl = new TableLayout(1);
		tl.setWidth("100%");
		tl.setCellHorizontalAlign(HorizontalAlignment.CENTER);	
		tl.setCellPadding(0);
		
		bodyContainer.setLayout(tl);
		
		TableData td = new TableData();
		td.setHeight("50px");
		
		// tasks
		NavigationButtonItem naviButtonItem = new NavigationButtonItem(AbstractImagePrototype.create(UIContext.IconBundle.tasks_backup()), getTaskPanel(), parentData);
		bodyContainer.add(naviButtonItem,td);
		
		
		// support
		if(UIContext.customizedModel == null || UIContext.customizedModel.getShowSupportHeader()){
			naviButtonItem = new NavigationButtonItem(AbstractImagePrototype.create(UIContext.IconBundle.googleGroup()), new SupportPanel(appType), parentData);
			bodyContainer.add(naviButtonItem,td);
		}
		
		if(UIContext.customizedModel == null || UIContext.customizedModel.getShowSocialNetworkHeader()) {
			// social NW
			if (UIContext.serverVersionInfo.isShowSocialNW() != null && UIContext.serverVersionInfo.isShowSocialNW())
			{
				naviButtonItem = new NavigationButtonItem(AbstractImagePrototype.create(UIContext.IconBundle.userCenter()), new SocialNetworkingPanel(appType), parentData);
				bodyContainer.add(naviButtonItem,td);
			}

		}
		//testMenuBar();

	}

	@Override
	protected void onRender(Element target, int index)
	{
		super.onRender(target, index);		
		
		// the header is OK by the above super.onRender(), below is to prepare the body
		bwrap = el().createChild("<div class=" + bwrapStyle + "></div>");
		Element bw = bwrap.dom;
		body = fly(bw).createChild("<div class=" + bodStyle + "></div>");
		
		// remove the background-color and borders from body
		body.setStyleAttribute("background-color", "transparent");
		body.setBorders(false);		
					
		// render the layout container
		bodyContainer.render(body.dom, 0);
		bodyContainer.layout();	
	}
	
	@Override
	protected void doAttachChildren()
	{
		super.doAttachChildren();
		ComponentHelper.doAttach(bodyContainer);
	}

	@Override
	protected void doDetachChildren()
	{
		super.doDetachChildren();
		ComponentHelper.doDetach(bodyContainer);
	}

	@Override
	public void onComponentEvent(ComponentEvent ce)
	{
		// TODO Auto-generated method stub
		//super.onComponentEvent(ce);
	}
	
	protected ContentPanel getTaskPanel(){
		
		switch(appType){
		case D2D: 
			contentPanel = new TaskPanel(true);
			break;
		case VSPHERE:
			contentPanel = new VSphereTaskPanel();
			break;
		case VCM:
			if(contentPanel == null || !(contentPanel instanceof ColdStandbyTaskPanel)) {
				contentPanel = new ColdStandbyTaskPanel(true);
				ColdStandbyTaskPanel taskPanel = ColdStandbyManager.getInstance().getTaskPanel();
				if(taskPanel != null) {
					((ColdStandbyTaskPanel)contentPanel).getContainerActivityLog()
							.setEnabled(taskPanel.getContainerActivityLog().isEnabled());
					
					((ColdStandbyTaskPanel)contentPanel).getContainerSetting()
							.setEnabled(taskPanel.getContainerSetting().isEnabled());
				}
			}
			break;
		default:
			contentPanel = new TaskPanel(true);
		}
		return contentPanel;
	}
}
