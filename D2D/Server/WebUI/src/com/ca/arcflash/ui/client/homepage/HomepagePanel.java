package com.ca.arcflash.ui.client.homepage;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ca.arcflash.ha.model.VCMConfigStatus;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyHomepage;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyManager;
import com.ca.arcflash.ui.client.coldstandby.VirtualConversionTabPanel;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.BaseComboBox;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.HelpTopics;
import com.ca.arcflash.ui.client.common.IRefreshable;
import com.ca.arcflash.ui.client.common.InternLoginUtils;
import com.ca.arcflash.ui.client.common.LoadingStatusWindow;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.EdgeInfoModel;
import com.ca.arcflash.ui.client.model.PatchInfoModel;
import com.ca.arcflash.ui.client.model.RolePrivilegeModel;
import com.ca.arcflash.ui.client.model.TrustHostModel;
import com.ca.arcflash.ui.client.notifications.MessagesWidget;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.TabPanelEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.ca.arcflash.ui.client.model.BIPatchInfoModel;//added by cliicy.luo

public class HomepagePanel extends LayoutContainer {
	private final HomepageServiceAsync service = GWT.create(HomepageService.class);
	private final LoginServiceAsync loginService = GWT.create(LoginService.class);
	public static final int REFRESH_INTERVAL = 3*1000;
	public static final String SELECT_TAB = "selecttab";
	public static final String VCM_TAB = "vcm";
	
	private BaseComboBox<TrustHostModel> trustHostCombo;
	private TrustHostModel selectedTrustHost;
	private LayoutContainer inRow2;  // container of the trusted servers combobox
	
	//private LoadingStatus lsCheckingUpdates;
	private final CommonServiceAsync Commonservice = GWT.create(CommonService.class);
	private int iPatchManagerStatus = 0;//Not running
	private static boolean bCheckUpdatesJobActive = false;

	private LoadingStatusWindow loadingStatus;
	private Timer updateTimer;
	private VCMConfigStatus vcmConfigStatus;
	private Boolean isShowServerManagment = true;
	private Boolean isSelectedVCM = false;
	private MenuItem knowledgeCenterItem;
//	private MenuItem helpItem;
	private String CHAT_URL = UIContext.externalLinks.getLiveChatURL();
	private MenuItem onlineSupportItem;
	private MenuItem solutionsGuideItem;
	private MenuItem agentForWindowsUserGuideItem;;
	private MenuItem liveChatItem;
	private MenuItem sendFeedbackItem;
	private MenuItem aboutItem;
	private MenuItem videosItem;
	private MenuItem checkupdatesItem;
	private  MenuItem registerEntitlementItem;
	private  Menu helpMenu;
	
	//added by cliicy.luo
	private MenuItem checkBIupdatesItem;
	private int iBIPatchManagerStatus = 0;//Not running
	private static boolean bCheckBIUpdatesJobActive = false;
	private LoadingStatusWindow loadingBIStatus;
	//added by cliicy.luo
		
	@Override
	public void onLoad(){
		List<Component> items=helpMenu.getItems();
		for (Component item:items){
			if(item instanceof MenuItem)item.addStyleName("ARCSERVE-STYLE-MENU-ITEM");	
		}
	}
	
	
	protected void onRender(Element target, int index) {
		super.onRender(target, index);

		isShowServerManagment = UIContext.customizedModel.getShowServerManage() == null ? true
				: UIContext.customizedModel.getShowServerManage();

		final BorderLayout layout = new BorderLayout();
		layout.setContainerStyle("navigation-background");
		setLayout(layout);

		// check if it is ja build, ja build needs more height
		int headerHeight = 95;
		String locale = UIContext.serverVersionInfo.getLocale();			
		
		if (locale != null && locale.toLowerCase().startsWith("ja"))
		{
			headerHeight = 115;
		}

		BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, headerHeight);
		northData.setMargins(new Margins(0, 0, 1, 0));
		add(createHeader(), northData);
		
		if (vcmConfigStatus == null || !(vcmConfigStatus.isMonitor()/* || vcmConfigStatus.isVcmConfigured()*/))
		{
			BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
			centerData.setMargins(new Margins(0));

			add(new D2DHomePageTab(), centerData);
		}
		else
		{
			createTabPanel();
//			this.setStyleAttribute("background-color", "#ebeff2");
		}
		
		UIContext.homepagePanel = this;
		shown = false;
	}
	
	@Override
	protected void afterRender() {
		super.afterRender();
		
		showManagedByServerLabel();
	}

	private void showManagedByServerLabel() {
		EdgeInfoModel edgeInfoVS = null;
		if(isSelectedVCM) {
			edgeInfoVS = UIContext.serverVersionInfo.edgeInfoVCM;
		}
		else {
			edgeInfoVS = UIContext.serverVersionInfo.edgeInfoCM;
		}
		
		if(edgeInfoVS != null){
			UIContext.managedByEdgeContainer.setEdgeServer(edgeInfoVS.getEdgeHostName(),edgeInfoVS.getEdgeUrl());
		}else{
			UIContext.managedByEdgeContainer.setEdgeServer(null, null);
		}
	}
	
	/**
	 * Do not remove this method. This method method is used to override its parent's method.
	 */
	 public void setScrollMode(Scroll scroll) {
		 //Do not remove this method. This method method is used to override its parent's method.
	 }
	
	 // header of D2D r16, the root method of header
	private LayoutContainer createHeader()
	{
		final LayoutContainer container = new LayoutContainer();
		container.setStyleAttribute("background", "white");
		TableLayout tableLayout = new TableLayout(1);
		tableLayout.setWidth("100%");
		
		// first row
		container.add(createRow_1_Banner());
		
		// second row
		container.add(createRow_2_User());
		
		// third row
		container.add(createRow_3_Feeds());
		
//		
		reLoadTrustHost();
		if (isShowServerManagment)
		{
			cmTimer = new Timer()
			{
				public void run()
				{
					if (UIContext.homepagePanel.isRendered() && container.isRendered() && container.isVisible())
					{
						if (!refreshRunning)
						{
							refreshTrustHost();
						}
					}
				}
			};
			cmTimer.schedule(REFRESH_CM_INTERVAL);
			cmTimer.scheduleRepeating(REFRESH_CM_INTERVAL);
		}
		
		return container;
	}
	
	private LayoutContainer createRow_1_Banner()
	{
		LayoutContainer container = new LayoutContainer();
		container.addStyleName("homepage_banner_background");
		
		TableLayout tableLayout = new TableLayout(2);
		tableLayout.setWidth("100%");		
		container.setLayout(tableLayout);
		
//		AbstractImagePrototype icon1 = IconHelper.create("images/homepage_logo.gif", 256, 40);
//		Image img = icon1.createImage();
//		img.ensureDebugId("0925257f-393c-4cb0-a18e-025475d1dc09");
//		img.addStyleName("homepage_task_icon");
//		img.addClickHandler(new ClickHandler()
//		{
//			@Override
//			public void onClick(ClickEvent event)
//			{
//				HelpTopics.showHelpURL(UIContext.externalLinks.getHomePage());
//			}
//
//		});
		
		Label prdname = new Label("");
		prdname.addStyleName("homepage_logo");
		TableData td = new TableData();
		td.setVerticalAlign(VerticalAlignment.TOP);
		// first column
		container.add(prdname, td);
		
//		// second column
//		container.add(createWidget_ServerCombo());
		createWidget_ServerCombo();
		
//		HTML blank = new HTML("&nbsp;");
//		blank.setStyleName("header_ca_logo");
//		td = new TableData();
//		td.setWidth("63px");
//		td.setHorizontalAlign(HorizontalAlignment.RIGHT);
//		container.add(blank, td);
				
		return container;
	}
	
	private LayoutContainer createWidget_ServerCombo()
	{
		LayoutContainer container = new LayoutContainer();
		
		if (isShowServerManagment)
		{
			// combo box
			trustHostCombo = new BaseComboBox<TrustHostModel>();
			trustHostCombo.ensureDebugId("411dc79d-2858-4b6b-abc7-d8db03452a39");
			trustHostCombo.setWidth(210);
			trustHostCombo.setDisplayField(TrustHostModel.tag_HostName);
			trustHostCombo.setEmptyText(UIContext.Constants.trustHost());
			Utils.addToolTip(trustHostCombo, UIContext.Constants.trustHostServerTooltip());
			trustHostCombo.setEditable(false);
			trustHostCombo.setStore(initTrustHostStore());
			trustHostCombo.addSelectionChangedListener(new SelectionChangedListener<TrustHostModel>()
			{
				@Override
				public void selectionChanged(SelectionChangedEvent<TrustHostModel> se)
				{
					if (disableSelChgEvt)
						return;

					final TrustHostModel trustHost = se.getSelectedItem();

					if (trustHost != null)
					{
						if (selectedTrustHost != null
								&& selectedTrustHost.getHostName().equalsIgnoreCase(trustHost.getHostName()))
						{
							return;
						}

						// To Support Warning Icon instead of default (Information Icon) : issue : 18630835
						final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>()
						{
							public void handleEvent(MessageBoxEvent be)
							{
								if (be.getButtonClicked().getItemId().equals(com.extjs.gxt.ui.client.widget.Dialog.YES))
								{
									callBecomeTrustHost(trustHost);

								}
							}
						};

						MessageBox mb = new MessageBox();
						mb.setIcon(MessageBox.WARNING);
						mb.setButtons(MessageBox.YESNO);
						mb.setTitleHtml(UIContext.Messages.messageBoxTitleWarning(UIContext.productNameD2D));
						mb.setMessage(UIContext.Messages.trustHostSwitchServer(trustHost.getHostName()));
						//mb.getDialog().ensureDebugId("60b952c8-a510-4b91-bf47-1469d8cb71f0");
						Utils.setMessageBoxDebugId(mb);
						mb.addCallback(l);
						mb.show();
					}
				}

				private void callBecomeTrustHost(final TrustHostModel trustHost)
				{
					if (trustHost.getD2DVersion() <16) {
						//If the D2D is R15, go to the login page
						StringBuilder url = new StringBuilder(trustHost.getProtocol());
						url.append("//");
						url.append(trustHost.getHostName());
						url.append(":");
						url.append(trustHost.getPort());
						Window.open(url.toString(), "", "");
					}else {
						//If the D2D is R16, auto login the d2d
						InternLoginUtils.loginD2DHost(trustHost.getHostName(), trustHost.getUser(), trustHost.getPassword(), trustHost.getPort(), trustHost.getProtocol());
					}
				}
			});
			
			
			container.setStyleAttribute("text-align", "right");
			
			TableLayout tl = new TableLayout();
			tl.setColumns(3);
			tl.setWidth("100%");
			tl.setCellPadding(2);
			tl.setCellSpacing(2);
			container.setLayout(tl);

			Text lf = new Text();
			lf.setStyleName("homepage_banner_label_server");
			lf.setText(UIContext.Constants.trustHostServerLabel());
			
			TableData data = new TableData();
			data.setWidth("100%");
			data.setHorizontalAlign(HorizontalAlignment.RIGHT);
			
			container.add(lf, data);
			data = new TableData();
			container.add(trustHostCombo, data);
			
			Label addServer = new Label();
			addServer.ensureDebugId("2d03d885-f263-4111-93bc-112c2cfc74f5");
			addServer.setStyleName("homepage_banner_link_addserver");
			addServer.setText(UIContext.Constants.addDeleteTrustedHost());
			addServer.addClickHandler(new ClickHandler(){

				@Override
				public void onClick(ClickEvent event) {
					TrustedHostWindow window = new TrustedHostWindow();
					window.setModal(true);
					window.show();
				}
				
			});
			container.add(addServer);
		}
		
		return container;
	}
	
	private LayoutContainer createRow_2_User()
	{
		LayoutContainer container = new LayoutContainer();		
		TableLayout tableLayout = new TableLayout(3);
		tableLayout.setWidth("100%");
		container.setLayout(tableLayout);
		
		// left
		currentTrustedHost = new Text();
		currentTrustedHost.setStyleName("homepage_managing_server_label");
		currentTrustedHost.setStyleAttribute("padding", "2 2 2 5");
		container.add(currentTrustedHost);

		TableData tb = new TableData();
		if(!isShowLogonUser()){
			tb.setWidth("75%");
		}else{
			tb.setWidth("65%");
		}
		tb.setHorizontalAlign(HorizontalAlignment.RIGHT);
		
		UIContext.managedByEdgeContainer = new ManagedByEdgeContainer();
		EdgeInfoModel edgeInfoVS = UIContext.serverVersionInfo.edgeInfoCM;
		if(edgeInfoVS != null){
			UIContext.managedByEdgeContainer.setEdgeServer(edgeInfoVS.getEdgeHostName(),edgeInfoVS.getEdgeUrl());
		}else{
			UIContext.managedByEdgeContainer.setEdgeServer(null, null);
		}
		
		container.add(UIContext.managedByEdgeContainer, tb);
		
		
		// right
		TableData tableData = new TableData();
		tableData.setHorizontalAlign(HorizontalAlignment.RIGHT);
		inRow2 = createWidget_LogonUser();
		container.add(inRow2, tableData);
		
		return container;
	}
	
	private LayoutContainer createWidget_LogonUser()
	{
		LayoutContainer container = new LayoutContainer();
		
		TableLayout tableLayout = new TableLayout(7);
		tableLayout.setCellPadding(4);
		container.setLayout(tableLayout);	
		
		//Add Messages Widget if Agent not Managed by Console
		if(!isManagedByConsole())
		{
			container.add( new MessagesWidget().getNotifications());
		}
		
		Image icon = null;
		if(!isShowLogonUser()){
			tableLayout.setColumns(4);
		}else{
			// login user	
			icon = AbstractImagePrototype.create(UIContext.IconBundle.homepage_user_icon()).createImage();		
			container.add(icon);
			
			Text loginName = new Text();	
			loginName.setText(UIContext.loginUser);
			loginName.setStyleName("homepage_header_user_label");			
			container.add(loginName);	
		}
		
		// log out
		icon = AbstractImagePrototype.create(UIContext.IconBundle.homepage_logout_icon()).createImage();		
		container.add(icon);
		
		Label logoutlf = new Label();
		logoutlf.setText(UIContext.Constants.homepageLogout());
		logoutlf.setStyleName("homepage_header_hyperlink_label");
		logoutlf.ensureDebugId("953947f6-48f0-496c-aefc-5a0342b54fbe");
				
		
		logoutlf.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event)
			{
				// To Support Warning Icon instead of default (Information Icon)
				// : issue : 18630835
				final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>()
				{
					public void handleEvent(MessageBoxEvent be)
					{
						if (be.getButtonClicked().getItemId().equals(com.extjs.gxt.ui.client.widget.Dialog.YES))
						{
							callLogout();
						}
					}
				};

				MessageBox mb = new MessageBox();
				mb.setIcon(MessageBox.WARNING);
				mb.setButtons(MessageBox.YESNO);
				mb.setTitleHtml(UIContext.Messages.messageBoxTitleWarning(UIContext.productNameD2D));
				mb.setMessage(UIContext.Constants.trustHostConfirmMsg());
				mb.addCallback(l);
				Utils.setMessageBoxDebugId(mb);
				mb.show();
			}

			private void callLogout()
			{
				loginService.logout(new BaseAsyncCallback<Boolean>()
				{
					@Override
					public void onFailure(Throwable caught)
					{
						super.onFailure(caught);
					}

					@Override
					public void onSuccess(Boolean result)
					{
						Window.Location.reload();
					}
				});
			}
		});
		
		container.add(logoutlf);
		
		// help menu
		icon = AbstractImagePrototype.create(UIContext.IconBundle.homepage_help_icon()).createImage();		
		container.add(icon);
		container.add(createWidget_HelpMenu());
		
		return container;
	}
	
	
	private LayoutContainer createWidget_HelpMenu()
	{
		LayoutContainer container = new LayoutContainer();
		TableLayout tableLayout = new TableLayout(2);
		container.setLayout(tableLayout);
		 //create the menu we want to assign to the button   
        helpMenu = new Menu();
        helpMenu.ensureDebugId("59c6f893-e41f-4c4a-9121-d8f81eb3a3fb");
        helpMenu.addStyleName("ARCSERVE-STYLE-HELP-MENU");
        //Knowledge Center
        knowledgeCenterItem = new MenuItem();       
        knowledgeCenterItem.setText(UIContext.Constants.knowledgeCenter());      
        knowledgeCenterItem.addSelectionListener(new SelectionListener<MenuEvent>() {
        	public void componentSelected(MenuEvent ce) {           
            		HelpTopics.showHelpURL(UIContext.externalLinks.getKnowledgeCenterURL());
            }           
          });      
        helpMenu.add(knowledgeCenterItem);
       
        
//        helpItem = new MenuItem();
//        helpItem.ensureDebugId("799f6b51-4dae-4d5f-9def-c9f4e69cde4a");
//        helpItem.setText(UIContext.Constants.helphtml());
//       // helpItem.setIcon(AbstractImagePrototype.create(UIContext.IconBundle.homepage_help_menu_online_help()));
//        helpItem.addSelectionListener(new SelectionListener<MenuEvent>() {
//            public void componentSelected(MenuEvent ce) {
//            	if(isSelectedVCM)
//            		HelpTopics.showHelpURL(UIContext.externalLinks.getVirtualStandbyHelp());
//            	else
//            		HelpTopics.showHelpURL(UIContext.externalLinks.getHomePagePanelHelp());
//              }
//          });
//        //helpItem.setStyleName("MenuItem");
//        helpMenu.add(helpItem);
        
        //Online Support
        onlineSupportItem = new MenuItem();
        onlineSupportItem.ensureDebugId("799f6b51-4dae-4d5f-9def-c9f4e69cde4a");
        onlineSupportItem.setText(UIContext.Constants.onlineSupport());
        onlineSupportItem.addSelectionListener(new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
            	HelpTopics.showHelpURL(UIContext.externalLinks.getOnlineSupportURL());
              }
          });
        helpMenu.add(onlineSupportItem);
        
        //Solutions Guide
        solutionsGuideItem = new MenuItem();
        solutionsGuideItem.ensureDebugId("b8466f5b-daa8-4cfd-b4ed-ba2483fb92ea");
        solutionsGuideItem.setText(UIContext.Constants.solutionsGuide());
        solutionsGuideItem.addSelectionListener(new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
            	HelpTopics.showHelpURL(UIContext.externalLinks.getSolutionsGuideURL());
              }
          });
        helpMenu.add(solutionsGuideItem); 
        
        //Agent for Windows User Guide
        agentForWindowsUserGuideItem = new MenuItem();
        agentForWindowsUserGuideItem.ensureDebugId("b8466f5b-daa8-4cfd-b4ed-ba2483fb92ea");
        agentForWindowsUserGuideItem.setText(UIContext.Constants.agentForWindowsUserGuide());
        agentForWindowsUserGuideItem.addSelectionListener(new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
            	HelpTopics.showHelpURL(UIContext.externalLinks.getAgentForWindowsUserGuideURL());
              }
          });
        helpMenu.add(agentForWindowsUserGuideItem);
        
        //Ask Support: Live Chat
        liveChatItem = new MenuItem();
        liveChatItem.ensureDebugId("e7003953-2907-4bff-886c-ee5d9764a2fa");
        liveChatItem.setText(UIContext.Constants.liveChat());
        liveChatItem.addSelectionListener(new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
            	openLiveChatURL();
              }
          });
        helpMenu.add(liveChatItem);
        
        String locale = UIContext.serverVersionInfo.getLocale();
        String sendFeedbackLabel = UIContext.externalLinks.getHomepageSupportSendFeedbackLabel();
        String videosLabel = UIContext.externalLinks.getHomepageSupportVideoLabel();
		if(locale != null && locale.toLowerCase().trim().startsWith("ja")) {
			sendFeedbackLabel = UIContext.externalLinks.getHomepageSupportSendFeedbackLabelOnlyEn();
		}
		if(locale!=null && !locale.trim().equalsIgnoreCase("en")) {
			videosLabel = UIContext.externalLinks.getHomepageSupportVideoLabelOnlyEn();
		}
        //Send Feedback
        sendFeedbackItem = new MenuItem();
        sendFeedbackItem.ensureDebugId("03a9ca0c-9a4e-4853-a0f2-57dd0edec77d");
		sendFeedbackItem.setText(sendFeedbackLabel);
        //sendFeedbackItem.setText(UIContext.Constants.sendFeedback());
        sendFeedbackItem.addSelectionListener(new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
            	HelpTopics.showHelpURL(UIContext.externalLinks.getFeedBackURL());
              }
          });
        helpMenu.add(sendFeedbackItem);
        
        //Videos
        videosItem = new MenuItem();
        videosItem.ensureDebugId("03a9ca0c-9a4e-4853-a0f2-57dd0edec77d");
        videosItem.setText(videosLabel);
        //videosItem.setText(UIContext.Constants.videos());
        videosItem.addSelectionListener(new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
            	HelpTopics.showHelpURL(UIContext.externalLinks.getVideoURL());
              }
          });
        helpMenu.add(videosItem);
        
        final SeparatorMenuItem smItem2 = new SeparatorMenuItem();
        helpMenu.add(smItem2);
        
        checkupdatesItem = new MenuItem();
        checkupdatesItem.ensureDebugId("c2a35005-747e-4ae3-93f9-5c390c29b98a");
        checkupdatesItem.setText(UIContext.Constants.checkForUpdates());
       // checkupdatesItem.setIcon(AbstractImagePrototype.create(UIContext.IconBundle.homepage_help_menu_check_update()));
        checkupdatesItem.addSelectionListener(new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
        		
            		if(bCheckUpdatesJobActive == true)
            		{
            			return;
            		}
            		loadingStatus = null;

            		if(loadingStatus == null)
    				{
    					loadingStatus = new LoadingStatusWindow(UIContext.Messages.D2DAutoUpdateMessageBoxTitle(),UIContext.Messages.CheckingForUpdatesMessage());
    				}
    				loadingStatus.setModal(false);
    				loadingStatus.show();
    				
    				Commonservice.getPatchManagerStatus(new AsyncCallback<Integer>() {
    	
    					@Override
    					public void onFailure(Throwable caught) {
    						iPatchManagerStatus = 0;
    						//lsCheckingUpdates.hideIndicator();
    						//loadingStatus.hideWindow();
    						loadingStatus.hide();
    						MessageBox msgError = new MessageBox();						
    						msgError.setTitleHtml(UIContext.Messages.D2DAutoUpdateMessageBoxTitle());
    						msgError.setModal(true);						
    						msgError.setIcon(MessageBox.ERROR);
    						msgError.setMessage(UIContext.Messages.D2DAutoUpdateFailedToGetStatusError(caught.getMessage()));
    						Utils.setMessageBoxDebugId(msgError);
    						msgError.show();
    					}
    	
    					@Override
    					public void onSuccess(Integer result) {
    						iPatchManagerStatus = result;
    						MessageBox msgError = new MessageBox();						
    						msgError.setTitleHtml(UIContext.Messages.D2DAutoUpdateMessageBoxTitle());
    						msgError.setModal(true);				
    						switch(iPatchManagerStatus)
    						{
    							case SummaryPanel.UPDATE_MANAGER_DOWN:// not running
    								loadingStatus.hide();
    								msgError.setIcon(MessageBox.ERROR);
    								msgError.setMessage(UIContext.Messages.D2DAutoUpdateIsdown(UIContext.productNameD2D));
    								Utils.setMessageBoxDebugId(msgError);
    								msgError.show();
    								break;
    							case SummaryPanel.UPDATE_MANAGER_READY:// ready to process request
    								SubmitCheckUpdatesRequest();
    								break;
    							case SummaryPanel.UPDATE_MANAGER_BUSY:// busy with other request
    								loadingStatus.hide();
    								msgError.setIcon(MessageBox.ERROR);
    								msgError.setMessage(UIContext.Messages.D2DAutoUpdateBusyWithOtherRequest());
    								Utils.setMessageBoxDebugId(msgError);
    								msgError.show();
    								break;
    						}
    					}
    				});
              }
          });
        //checkupdatesItem.setStyleName("MenuItem");
        helpMenu.add(checkupdatesItem);
        
        //added by cliicy.luo
		checkBIupdatesItem = new MenuItem();
		checkBIupdatesItem
				.ensureDebugId("c2a35005-747e-4ae3-93f9-5c390c29b98a");
		checkBIupdatesItem.setText(UIContext.Constants.checkForBIUpdates());
		checkBIupdatesItem
				.addSelectionListener(new SelectionListener<MenuEvent>() {
					public void componentSelected(MenuEvent ce) {

						if (bCheckBIUpdatesJobActive == true) {
							return;
						}
						loadingBIStatus = null;

						if (loadingBIStatus == null) {
							loadingBIStatus = new LoadingStatusWindow(
									UIContext.Messages
											.D2DAutoBIUpdateMessageBoxTitle(),
									UIContext.Messages
											.CheckingForUpdatesMessage());
						}
						loadingBIStatus.setModal(false);
						loadingBIStatus.show();

						Commonservice
								.getPatchManagerStatus(new AsyncCallback<Integer>() {

									@Override
									public void onFailure(Throwable caught) {
										iBIPatchManagerStatus = 0;

										loadingBIStatus.hide();
										MessageBox msgError = new MessageBox();
										msgError.setTitleHtml("Binary updates fail");
										msgError.setModal(true);
										msgError.setIcon(MessageBox.ERROR);
										msgError.setMessage(UIContext.Messages
												.D2DAutoBIUpdateFailedToGetStatusError(caught
														.getMessage()));
										Utils.setMessageBoxDebugId(msgError);
										msgError.show();
									}

									@Override
									public void onSuccess(Integer result) {
										iBIPatchManagerStatus = result;
										MessageBox msgError = new MessageBox();
										msgError.setTitleHtml("Binray updates success");
										msgError.setModal(true);
										switch (iBIPatchManagerStatus) {
										case SummaryPanel.UPDATE_MANAGER_DOWN:// not
																				// running
											loadingBIStatus.hide();
											msgError.setIcon(MessageBox.ERROR);
											msgError.setMessage(UIContext.Messages
													.D2DAutoUpdateIsdown(UIContext.productNameD2D));
											Utils.setMessageBoxDebugId(msgError);
											msgError.show();
											break;
										case SummaryPanel.UPDATE_MANAGER_READY:// ready
																				// to
																				// process
																				// request
											SubmitCheckBIUpdatesRequest();
											break;
										case SummaryPanel.UPDATE_MANAGER_BUSY:// busy
																				// with
																				// other
																				// request
											loadingBIStatus.hide();
											msgError.setIcon(MessageBox.ERROR);
											msgError.setMessage(UIContext.Messages
													.D2DAutoBIUpdateBusyWithOtherRequest());
											Utils.setMessageBoxDebugId(msgError);
											msgError.show();
											break;
										}
									}
								});
					}
				});
		helpMenu.add(checkBIupdatesItem);
		// added by cliicy.luo
         
		if(!isManagedByConsole()){
		SeparatorMenuItem smItem3 = new SeparatorMenuItem();
	    helpMenu.add(smItem3);
        registerEntitlementItem = new MenuItem();
        registerEntitlementItem.ensureDebugId("6c5857d5-2018-4396-af6e-cbbd9ab19090");
        registerEntitlementItem.setText(UIContext.Constants.registerEntitlement());
      //  aboutItem.setIcon(AbstractImagePrototype.create(UIContext.IconBundle.homepage_help_menu_about()));
        registerEntitlementItem.addSelectionListener(new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
            	new EntitlementRegistrationWindow();
            	//EntitlementRegistrationWindow registerWindow = new EntitlementRegistrationWindow();
            	//registerWindow.setModal(true);
            	//registerWindow.show();
              }
          });
        
        helpMenu.add(registerEntitlementItem);
		}

        SeparatorMenuItem smItem4 = new SeparatorMenuItem();
        helpMenu.add(smItem4);
        aboutItem = new MenuItem();
        aboutItem.ensureDebugId("ea7dbba9-cc2d-4d52-8a60-a15fc75741c0");
       
        aboutItem.setText(UIContext.Constants.about());
      //  aboutItem.setIcon(AbstractImagePrototype.create(UIContext.IconBundle.homepage_help_menu_about()));
        aboutItem.addSelectionListener(new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
            	AboutWindow window = new AboutWindow();
				window.setModal(true);
				window.show();
              }
          });
        
        helpMenu.add(aboutItem);
//        helpMenu.setStyleName("gwt-MenuBar");
//        helpMenu.setBorders(false);
        
        final com.extjs.gxt.ui.client.widget.Label link = new com.extjs.gxt.ui.client.widget.Label(){
			@Override
			protected void onAttach() {
				super.onAttach();
				sinkEvents(Event.ONMOUSEUP);
			}

			public void onBrowserEvent(Event event) {

	            switch (DOM.eventGetType(event)) {
	                    case Event.ONMOUSEUP: {
	                    	if(UIContext.serverVersionInfo.isShowUpdate() != null 
	                    			&& !UIContext.serverVersionInfo.isShowUpdate()){
	                    		checkupdatesItem.hide();
	                    		smItem2.hide();
	                    	}
	                    	if (UIContext.serverVersionInfo.isNCE()) {
	                			liveChatItem.setVisible(false);
	                		} else {
	                			if (UIContext.customizedModel.getShowLiveChat() == null || UIContext.customizedModel.getShowLiveChat()) {
	                				liveChatItem.setVisible(true);
	                			} else {
	                				liveChatItem.setVisible(false);
	                			}
	                		}

	                    	helpMenu.showAt(getAbsoluteLeft(), getAbsoluteTop()+this.getHeight());
	                    	break;
	                    }
	            }

			}

		};

		link.setStyleName("homepage_header_helplink_label");
		link.setHtml(UIContext.Constants.help());
		link.ensureDebugId("a9e16c55-a9c1-4c4c-9b76-c765587cd4d2");		
		
		TableData td = new TableData();
		td.setVerticalAlign(VerticalAlignment.MIDDLE);
		td.setHorizontalAlign(HorizontalAlignment.CENTER);
		Image arrowImage = AbstractImagePrototype.create(UIContext.IconBundle.down_arrow()).createImage();
		arrowImage.getElement().getStyle().setMarginRight(2, Unit.PX);
		//arrowImage.getElement().getStyle().setMarginBottom(2, Unit.PX);
		arrowImage.getElement().getStyle().setCursor(Style.Cursor.POINTER);
		arrowImage.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				helpMenu.showAt(link.getAbsoluteLeft(), link.getAbsoluteTop()+link.getHeight());
			}
			
		});
        
//        MenuBar bar = new MenuBar()
//        {
//			@Override
//			protected void expand(MenuBarItem item)
//			{
//				item.getMenu().setFocusOnShow(false);
//				item.getMenu().showAt(getAbsoluteLeft(), getAbsoluteTop()+this.getHeight());
//				
//				//item.expanded = true;
//			}
//        	
//        };
//        
//        bar.setBorders(false);
//        bar.setStyleAttribute("borderTop", "none");
//        bar.add(new MenuBarItem(UIContext.Constants.help(), helpMenu));
//        bar.setStyleName("gwt-MenuBar");
        
        container.add(link);
        container.add(arrowImage);
        return container;
	}
	
	private LayoutContainer createRow_3_Feeds()
	{
		LayoutContainer container = new LayoutContainer();		
		TableLayout tableLayout = new TableLayout(2);
		tableLayout.setWidth("100%");		
		container.setLayout(tableLayout);
		
		// feeds panel
		container.add(createWidget_News());
		
		// all feeds link
		TableData tableData = new TableData();
		tableData.setHorizontalAlign(HorizontalAlignment.RIGHT);
		tableData.setWidth("10px");
		container.add(createWidget_FeedsLink(), tableData);
		
		return container;
	}
	
	private LayoutContainer createWidget_News()
	{
		LayoutContainer container = new LayoutContainer();

		Boolean isShowNewFeed = UIContext.customizedModel.getShowRSSFeed() == null ? true : UIContext.customizedModel
				.getShowRSSFeed();

		if (isShowNewFeed)
		{
			String locale = UIContext.serverVersionInfo.getLocale();
			int indexOf = locale.indexOf("_");
			String lang = "en";
			String country = "";
			if (indexOf != -1)
			{
				lang = locale.substring(0, indexOf);
				country = locale.substring(indexOf + 1);
			}

			container = new NewsPanel(lang, country);
		}
		
		return container;
	}
	 
	private LayoutContainer createWidget_FeedsLink()
	{
		LayoutContainer container = new LayoutContainer() ;
		
		Boolean isShowAllFeed = UIContext.customizedModel.getShowAllFeeds() == null?
				true : UIContext.customizedModel.getShowAllFeeds();
		
		if(isShowAllFeed) 
		{
			TableLayout layout = new TableLayout(2);
			layout.setCellPadding(2);
			container.setLayout(layout);
			
			ClickHandler feedHandler = new ClickHandler(){
				@Override
				public void onClick(ClickEvent event) {
					FeedsWindow dlg = new FeedsWindow();
					dlg.setModal(true);
					dlg.show();
				}
			};	
			
			// icon
			Image icon = AbstractImagePrototype.create(UIContext.IconBundle.rssIcon()).createImage();		
			icon.addClickHandler(feedHandler);
			icon.addStyleName("homepage_trusthost_hyperlink_label");
			
			container.add(icon);
			
			// link
			Label feedslf = new Label();
			feedslf.ensureDebugId("b3212660-0b52-464b-bbd2-840afa240f91");
			feedslf.setText(UIContext.Constants.allFeeds());
			feedslf.setStyleName("homepage_header_allfeeds_link_label");
			feedslf.addClickHandler(feedHandler);
			
			container.add(feedslf);			
		}
		
		return container;
	}
	
	private Timer cmTimer;
	private static final int REFRESH_CM_INTERVAL = 10000;
	private boolean refreshRunning = false;

	public ListStore<TrustHostModel> initTrustHostStore() {
		ListStore<TrustHostModel> trustHostListStore = new ListStore<TrustHostModel>();
		return trustHostListStore;
	}

	public void refreshTrustHost() {
		reLoadTrustHost();
	}

	private void reLoadTrustHost() {
		if(!isShowServerManagment) {
			service.getLocalHost(new BaseAsyncCallback<TrustHostModel> () {
				@Override
				public void onSuccess(TrustHostModel result) {
					if(result != null)
						currentTrustedHost.setText(UIContext.Messages.homepageServerName(result.getHostName()));
				}
			});
		}else {
			refreshRunning = true;
			service.getTrustHosts(new BaseAsyncCallback<TrustHostModel[]>() {
				@Override
				public void onFailure(Throwable caught) {
					super.onFailure(caught);
					toggleTrustHostCombo(null);
					refreshRunning = false;
				}

				@Override
				public void onSuccess(TrustHostModel[] result) {
					ListStore<TrustHostModel> store = trustHostCombo.getStore();
					store.removeAll();
					if (result != null) {
						for (int i = 0; i < result.length; i++) {
							store.add(result[i]);
						}
					}
					toggleTrustHostCombo(null);
					refreshRunning = false;
					service.getLocalHost(new BaseAsyncCallback<TrustHostModel>() {
						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
						}
						@Override
						public void onSuccess(TrustHostModel result) {
							if(result!=null && selectedTrustHost!=null){
								if(result.getHostName()!=null && selectedTrustHost.getHostName()!=null){
									if(!result.getHostName().equalsIgnoreCase(selectedTrustHost.getHostName())){
										inRow2.setVisible(false);
							    	}else{
							    		inRow2.setVisible(true);
							    	}
								}
							}
						}
					});
				}
			});
		}
	}

	private boolean disableSelChgEvt = false;
	
	private Text currentTrustedHost;

	private void toggleTrustHostCombo(TrustHostModel selected) {
		ListStore<TrustHostModel> store = trustHostCombo.getStore();
		if (store.getCount() <= 0) {
			if (trustHostCombo.isRendered()) {
				trustHostCombo.disable();
			} else {
				trustHostCombo.setEnabled(false);
			}
		} else {
			if (trustHostCombo.isRendered()) {
				trustHostCombo.enable();
			} else {
				trustHostCombo.setEnabled(true);
			}
		}

		if (store != null && store.getCount() > 0) {
			if (selected != null) {
				disableSelChgEvt = true;
				trustHostCombo.setValue(selected);
				disableSelChgEvt = false;
				for (int i = 0; i < store.getCount(); i++) {
					TrustHostModel model = store.getAt(i);
					if (model.getHostName().equalsIgnoreCase(
							selected.getHostName())) {
						model.setSelected(true);
					} else {
						model.setSelected(false);
					}
				}
			} else {
				for (int i = 0; i < store.getCount(); i++) {
					TrustHostModel model = store.getAt(i);
					if (model.isSelected()) {
						selectedTrustHost = model;
						disableSelChgEvt = true;
						trustHostCombo.setValue(model);
						disableSelChgEvt = false;
						currentTrustedHost.setText(UIContext.Messages.homepageServerName(model.getHostName()));
						break;
					}
				}
			}
		}
	}

	private void createTabPanel() {
		if(vcmConfigStatus != null 
				&& (/*vcmConfigStatus.isVcmConfigured() || */vcmConfigStatus.isMonitor())
				&&UIContext.RolePrivilege.getVcmConfigFlag()!=RolePrivilegeModel.DISPLAY_DISABLE) 
			showContentWithVCM(true);
		else 
			showContentWithVCM(false);	
	}

	private void showContentWithVCM(boolean withVCM) {
		Widget content = null;
		if(withVCM) {
			final TabPanel tabPanel = new TabPanel();
			
			//make the Tab content relayout when the size of the window changes.
			Window.addResizeHandler(new ResizeHandler() {
		        public void onResize(ResizeEvent event) {
		        	if(ColdStandbyManager.getInstance().getHomepage() != null)
		        		ColdStandbyManager.getInstance().getHomepage().removeNonResizablePanels();
		        	
		        	tabPanel.getLayout().layout();
		        	
		        	if(ColdStandbyManager.getInstance().getHomepage() != null)
		        		ColdStandbyManager.getInstance().getHomepage().recreateNonResizablePanels();
		        }
		      });
			
			TabItem d2dTab = new TabItem(UIContext.Messages.windowTitle(UIContext.productNameD2D));
			d2dTab.ensureDebugId("457da520-45a6-4057-bbfb-f6e0966e5951");
			d2dTab.setLayout( new FitLayout());
			d2dTab.add(new D2DHomePageTab());
			tabPanel.add(d2dTab);
			
			VirtualConversionTabPanel vcm = new VirtualConversionTabPanel();
			vcm.ensureDebugId("58f71af4-f9e3-4de5-89b1-355fe61825c8");
			tabPanel.add(vcm);
			tabPanel.setMinTabWidth(60);
			
			tabPanel.addListener(Events.Select, new Listener<TabPanelEvent>() {
				@Override
				public void handleEvent(TabPanelEvent be) {
					if(be.getItem() instanceof VirtualConversionTabPanel){
						isSelectedVCM = true;
//						helpItem.setText(UIContext.Messages.arcserveD2DHelp(UIContext.productNameVCM));
						agentForWindowsUserGuideItem.hide();
						showManagedByServerLabel();
						ColdStandbyHomepage homepage = ColdStandbyManager.getInstance().getHomepage();
						if(homepage != null && homepage.getProvisionPanel() != null) {
							homepage.getProvisionPanel().refreshGrid();
						}
					}
					else{
						isSelectedVCM = false;
//						helpItem.setText(UIContext.Messages.arcserveD2DHelp(UIContext.productNameD2D));
						agentForWindowsUserGuideItem.show();
						showManagedByServerLabel();
						
						// if the d2d tab is not the default tab, the backup history's grid won't layout well at the first time
						// so layout it again when switching to d2d tab 
						if (UIContext.d2dHomepagePanel != null)
						{
							UIContext.d2dHomepagePanel.refreshBackupHistoryLayout();
						}
					}
				}
			});
			String tabName = Location.getParameter(SELECT_TAB);
			if(VCM_TAB.equalsIgnoreCase(tabName))
				tabPanel.setSelection(vcm);
			
			content = tabPanel;
		}
		else {
			content = new D2DHomePageTab();
		}
		
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
		centerData.setMargins(new Margins(0));
		add(content, centerData);
	}

	public ComboBox<TrustHostModel> getTrustHostCombo() {
		return trustHostCombo;
	}
	
	private void displayInfo(String title, String message, String icon) {
		/*Info info = new Info();
		info.setAutoHeight(true);
		info.show(new InfoConfig(title, message));*/
		
		MessageBox box = new MessageBox();
		box.setButtons(Dialog.OK);
		box.setTitleHtml(title);
		box.setMessage(message);
		box.setIcon(icon);
		box.show();
	}
	

	
	private void SubmitCheckUpdatesRequest(){
		if(bCheckUpdatesJobActive == false)
		{
			bCheckUpdatesJobActive = true;
		
			updateTimer = new Timer() {
				public void run() {
					checkUpdates();
				}
			};
	//		updateTimer.schedule(REFRESH_INTERVAL);
			updateTimer.scheduleRepeating(REFRESH_INTERVAL);
		}
		else 
		{
			displayInfo(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D), 
					UIContext.Constants.checkupdateisBeingProcessed(), MessageBox.ERROR);
			loadingStatus.hide();
		}
	}
	
	private void checkUpdates()
	{
		Commonservice.checkUpdate(new BaseAsyncCallback<PatchInfoModel>()
			{
				@Override
				public void onFailure(Throwable caught) {
					bCheckUpdatesJobActive = false;
					//lsCheckingUpdates.hideIndicator();
					//loadingStatus.hideWindow();
					loadingStatus.hide();
					super.onFailure(caught);
				}

				@Override
				public void onSuccess(PatchInfoModel patchInfo) {
					int iReturCode = patchInfo.getError_Status();
					
					if(iReturCode == SummaryPanel.WARNING_GET_PATCH_IN_PROGRESS){
						return;
					}
					
					loadingStatus.hide();
					bCheckUpdatesJobActive = false;
					updateTimer.cancel();
					switch(iReturCode)
					{
						case SummaryPanel.ERROR_GET_PATCH_INFO_SUCCESS:
							CommonServiceAsync commonService = GWT.create(CommonService.class);
							commonService.getUpdateInfo(new BaseAsyncCallback<PatchInfoModel>() {

								@Override
								public void onFailure(Throwable caught) {
									String strMessage = null;
									strMessage += UIContext.Constants.failedtoGetUpdatesInformation();
									strMessage += caught.getMessage();
									strMessage += UIContext.Constants.pleaseTryLater();
									displayInfo(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D), 
											strMessage, MessageBox.ERROR);
								}

								@Override
								public void onSuccess(PatchInfoModel result) {
									SummaryPanel.setPatchInfo(result);
									
								//	SummaryPanel.ShowNewUpdatesPanel(true);
									displayInfo(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D),
												UIContext.Messages.CheckSummaryPanelNewUpdatesMessage(UIContext.productNameD2D),
												MessageBox.INFO);
									//show the install dialog when users click 'check for update'
									NewUpdatesInstallWindow dlgNewUpdates = new NewUpdatesInstallWindow(result);
									dlgNewUpdates.setModal(true);
									dlgNewUpdates.show();
								}
							});
							
							break;
						case SummaryPanel.ERROR_NONEW_PATCHES_AVAILABLE:
							String ErorrMessage = UIContext.Messages.D2DIsUptoDate(UIContext.productNameD2D);
							patchInfo.setErrorMessage(ErorrMessage);
							displayInfo(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D), 
									patchInfo.getErrorMessage(), MessageBox.INFO);
							//msgError.setMessage("There are no new patches available. ARCserve D2D is up to date.");
							break;
						case SummaryPanel.ERROR_GET_PATCH_INFO_FAIL:
							if(patchInfo.getErrorMessage() == null || patchInfo.getErrorMessage().length() == 0)
								patchInfo.setErrorMessage(UIContext.Messages.FailedToGetUpdates());
							displayInfo(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D), 
									patchInfo.getErrorMessage(), MessageBox.ERROR);
							//msgError.setMessage("Failed to latest patch information. Please try after some time.");
							break;
						case SummaryPanel.ERROR_PM_BUSY_WITH_SCHEDULER:
							displayInfo(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D), 
									patchInfo.getErrorMessage(), MessageBox.ERROR);
							//msgError.setMessage("ARCserve D2D patch manager is performing scheduler request, please try after some time.");
							break;
						case SummaryPanel.WARNING_NODE_MANAGED_BY_CPM:
							displayInfo(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D), 
									patchInfo.getErrorMessage(), MessageBox.WARNING);							
							break;
						case SummaryPanel.WARNING_GET_PATCH_TIMEOUT:
							displayInfo(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D), 
									patchInfo.getErrorMessage(), MessageBox.WARNING);							
							break;
					}
					
					UIContext.d2dHomepagePanel.refresh(null, IRefreshable.CS_D2D_UPDATE);
				}
			});
		return;
	}
	
	public VCMConfigStatus getVcmConfigStatus() {
		return vcmConfigStatus;
	}

	// added by cliicy.luo
	private void SubmitCheckBIUpdatesRequest() {
		if (bCheckBIUpdatesJobActive == false) {
			bCheckBIUpdatesJobActive = true;

			updateTimer = new Timer() {
				public void run() {
					checkBIUpdates();
				}
			};
			updateTimer.scheduleRepeating(REFRESH_INTERVAL);
		} else {
			// displayInfo(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D),
			// UIContext.Constants.checkupdateisBeingProcessed(),
			// MessageBox.ERROR);
			displayInfo("SubmitCheckBIUpdatesRequest",
					"bCheckUpdatesJobActive == true", "ERROR");
			loadingStatus.hide();
		}
	}

	private void checkBIUpdates() {
		Commonservice.checkBIUpdate(new BaseAsyncCallback<BIPatchInfoModel>() {
			@Override
			public void onFailure(Throwable caught) {
				bCheckBIUpdatesJobActive = false;

				loadingBIStatus.hide();
				super.onFailure(caught);
			}

			@Override
			public void onSuccess(BIPatchInfoModel patchInfo) {
				int iReturCode = patchInfo.getError_Status();

				if (iReturCode == SummaryPanel.WARNING_GET_PATCH_IN_PROGRESS) {
					return;
				}

				loadingBIStatus.hide();
				bCheckBIUpdatesJobActive = false;
				updateTimer.cancel();
				switch (iReturCode) {
				case SummaryPanel.ERROR_GET_PATCH_INFO_SUCCESS:
					CommonServiceAsync commonService = GWT.create(CommonService.class);
					commonService.getBIUpdateInfo(new BaseAsyncCallback<BIPatchInfoModel>() {

								@Override
								public void onFailure(Throwable caught) {
									String strMessage = null;
									strMessage += UIContext.Constants
											.failedtoGetUpdatesInformation();
									strMessage += caught.getMessage();
									strMessage += UIContext.Constants
											.pleaseTryLater();
									// displayInfo(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D),
									// strMessage, MessageBox.ERROR);
									displayInfo(
											"ooooooooooooSummaryPanel.ERROR_GET_PATCH_INFO_SUCCESS",
											"oooooooooooooooooooofailedtoGetUpdatesInformation",
											"ERROR");
								}

								@Override
								public void onSuccess(BIPatchInfoModel result) {
									SummaryPanel.setBIPatchInfo(result);
									//TNewUpdates_BI_InstallWin dlgNewUpdates = new TNewUpdates_BI_InstallWin(null);
									NewUpdates_BI_InstallWin dlgNewUpdates = new NewUpdates_BI_InstallWin(result);
									dlgNewUpdates.setModal(true);
									dlgNewUpdates.show();
								}
							});

					break;
				case SummaryPanel.ERROR_NONEW_PATCHES_AVAILABLE:
					String ErorrMessage = UIContext.Messages.D2DIsUptoDate(UIContext.productNameD2D);
					patchInfo.setErrorMessage(ErorrMessage);
					// displayInfo(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D),
					// patchInfo.getErrorMessage(), MessageBox.INFO);
					displayInfo(
							"oooo ERROR_NONEW_PATCHES_AVAILABLE",
							"oooo There are no new patches available. ARCserve D2D is up to date",
							"INFO");
					// msgError.setMessage("There are no new patches available. ARCserve D2D is up to date.");
					break;
				case SummaryPanel.ERROR_GET_PATCH_INFO_FAIL:
					if (patchInfo.getErrorMessage() == null
							|| patchInfo.getErrorMessage().length() == 0)
						patchInfo.setErrorMessage(UIContext.Messages
								.FailedToGetUpdates());
					// displayInfo(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D),
					// patchInfo.getErrorMessage(), MessageBox.ERROR);
					displayInfo(
							"oooo ERROR_GET_PATCH_INFO_FAIL",
							"oooo Failed to latest patch information. Please try after some time.",
							"ERROR");
					// msgError.setMessage("Failed to latest patch information. Please try after some time.");
					break;
				case SummaryPanel.ERROR_PM_BUSY_WITH_SCHEDULER:
					// displayInfo(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D),
					// patchInfo.getErrorMessage(), MessageBox.ERROR);
					displayInfo(
							"ooo ERROR_PM_BUSY_WITH_SCHEDULER",
							"ooo ARCserve D2D patch manager is performing scheduler request, please try after some time.",
							"ERROR");
					// msgError.setMessage("ARCserve D2D patch manager is performing scheduler request, please try after some time.");
					break;
				case SummaryPanel.WARNING_NODE_MANAGED_BY_CPM:
					// displayInfo(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D),
					// patchInfo.getErrorMessage(), MessageBox.WARNING);
					displayInfo("ooo WARNING_NODE_MANAGED_BY_CPM",
							"oooo WARNING_NODE_MANAGED_BY_CPM", "WARNING");
					break;
				case SummaryPanel.WARNING_GET_PATCH_TIMEOUT:
					// displayInfo(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D),
					// patchInfo.getErrorMessage(), MessageBox.WARNING);
					displayInfo("oooo WARNING_GET_PATCH_TIMEOUT",
							"oooo WARNING_GET_PATCH_TIMEOUT", "WARNING");
					break;
				}

				UIContext.d2dHomepagePanel.refresh(null,
						IRefreshable.CS_D2D_UPDATE);
			}
		});
		return;
	}
	// added by cliicy.luo
		
	public void setVcmConfigStatus(VCMConfigStatus vcmConfigStatus) {
		this.vcmConfigStatus = vcmConfigStatus;
	}
	
	private boolean isShowLogonUser(){
		if(UIContext.loginUser == null || UIContext.loginUser.equals("")){
			return false;
		}else{
			return true;
		}
	}
	
	
	private Map<String, Long> failedMethods = new HashMap<String, Long>();
	private static boolean shown = false;
	private long MAX_DIFF = 15 * 1000;
	protected boolean processServerDown(Throwable caught, String methodName) {
		if(caught instanceof StatusCodeException 
				&&( ((StatusCodeException)caught).getStatusCode() == 0)) {
			
			Long currentTime = new Date().getTime();
			for(Map.Entry<String, Long> entry : failedMethods.entrySet()) {
				Long diff = currentTime - entry.getValue();
				//this entry is too old
				if(diff > MAX_DIFF) {
					failedMethods.clear();
				}
			}
			failedMethods.put(methodName, currentTime);			
			if(failedMethods.size() < 2) {
				return false;
			}else {
				Long diff = 0L;
				for(Long time : failedMethods.values()) {
					Long temp = currentTime - time;
					diff = diff <= temp? temp : diff;
				}
				failedMethods.clear();
				if(diff <  MAX_DIFF && !shown && !BaseAsyncCallback.isServerDown) {
					shown = true;
					BaseAsyncCallback.isServerDown = true;
					BaseAsyncCallback.isShow = true;
					return true;
				}else {
					return false;
				}
			}	
		}
		
		return false;
	}
	
	private void openLiveChatURL() {
		Window.open(this.CHAT_URL+UriUtils.encode(Window.Location.getHref()), "_blank", "resizable=yes,scrollbars=yes,location=yes,width=400,height=400");
	}
	
	private boolean isManagedByConsole()
	{
		boolean isManagedByConsole = false;
		 EdgeInfoModel edgeInfoVS = UIContext.serverVersionInfo.edgeInfoCM;
		if(edgeInfoVS != null && !edgeInfoVS.getEdgeHostName().isEmpty() && !edgeInfoVS.getEdgeUrl().isEmpty()){
			isManagedByConsole = true;
		}
		return isManagedByConsole;
	}
}
