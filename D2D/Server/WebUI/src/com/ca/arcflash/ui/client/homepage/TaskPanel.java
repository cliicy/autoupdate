package com.ca.arcflash.ui.client.homepage;

import java.util.HashMap;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyService;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyServiceAsync;
import com.ca.arcflash.ui.client.common.AppType;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.CommonSettingWindow;
import com.ca.arcflash.ui.client.log.LogWindow;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.ArchiveDestinationModel;
import com.ca.arcflash.ui.client.model.CustomizationModel;
import com.ca.arcflash.ui.client.model.RolePrivilegeModel;
import com.ca.arcflash.ui.client.mount.MountWindow;
import com.ca.arcflash.ui.client.recoverypoint.RecoveryPointWindow;
import com.ca.arcflash.ui.client.remotedeploy.RemoteDeployWindow;
import com.ca.arcflash.ui.client.restore.RestoreWizardContainer;
import com.ca.arcflash.ui.client.restore.RestoreWizardWindow;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TaskPanel extends ContentPanel {
	private final LoginServiceAsync loginService = GWT.create(LoginService.class);
	protected final CommonServiceAsync service = GWT.create(CommonService.class);
	
	private VerticalPanel verticalPanel;
	
    LayoutContainer backupContainer;
    LayoutContainer restoreContainer;
	LayoutContainer settingContainer;
	
	boolean isbkpNowClicked = false;
	private boolean isDriverInstalled = false;
	private Image backupIcon;
	private Label backupLabel;
	private Image restoreIcon;
	private Label restoreLabel;
	private Image settingIcon;
	private Label settingLabel;
	protected Listener<ComponentEvent> backupNowHandler;
	protected Listener<ComponentEvent> restoreHandler;
	protected Listener<ComponentEvent> settingHandler;

	private boolean showSubMenu;
    private Menu activeSubMenu;
	protected Listener<ComponentEvent> logHandler;
	protected Listener<ComponentEvent> recoveryPointHandler;
	protected Listener<ComponentEvent> mountHandler;
		
	public static HashMap<String,ArchiveDestinationModel> ManualDestinationCache=new HashMap();
	
	public TaskPanel()
	{
		super();
		this.setCollapsible(true);
		this.setAutoHeight(true);
		this.setHeadingHtml(UIContext.Constants.homepageTasksHeader());
		this.setBodyStyle("padding: 4px;");
	}

	public TaskPanel(boolean showSubMenu)
	{
		this();
		
		this.showSubMenu = showSubMenu;
	}
	
	

	@Override
	protected void onRender(Element parent, int pos)
	{
		verticalPanel = new VerticalPanel();
		verticalPanel.setWidth("100%");
		this.add(verticalPanel);
		    
		super.onRender(parent, pos);
	    
	    backupNowHandler = new Listener<ComponentEvent>(){
			@Override
			public void handleEvent(ComponentEvent be) {
				// TODO Auto-generated method stub
				onBackupNowClick();
			}
		};

		restoreHandler = new Listener<ComponentEvent>(){
			@Override
			public void handleEvent(ComponentEvent be) {
				// TODO Auto-generated method stub
				RestoreWizardWindow window = new RestoreWizardWindow();
				window.setModal(true);
				window.show();
			}
	    };
	    
	   settingHandler = new Listener<ComponentEvent>(){
			@Override
			public void handleEvent(ComponentEvent be) {
				// TODO Auto-generated method stub
				onSettingClick();
			}
	    };
	    
	    /*ClickHandler archiveSettingHandler = new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				ArchiveSettingsContent archiveWind = new ArchiveSettingsContent();
				archiveWind.setModal(true);
				archiveWind.show();
			}
	    };*/
	    
	    logHandler = new Listener<ComponentEvent>(){
			@Override
			public void handleEvent(ComponentEvent be) {
				// TODO Auto-generated method stub
				onLogClick();
			}
	    };
	    
	    Listener<ComponentEvent> deployHandler = new Listener<ComponentEvent>(){
			@Override
			public void handleEvent(ComponentEvent be) {
				// TODO Auto-generated method stub
				RemoteDeployWindow window = new RemoteDeployWindow();
				window.setModal(true);
				window.show();
			}
	    };
	    	    	    
	    recoveryPointHandler = new Listener<ComponentEvent>(){
			@Override
			public void handleEvent(ComponentEvent be) {
				// TODO Auto-generated method stub
				onRecoveryPointClick();
			}
	    };
	    
	    mountHandler = new Listener<ComponentEvent>(){
			@Override
			public void handleEvent(ComponentEvent be) {
				// TODO Auto-generated method stub
				MountWindow window = new MountWindow();
				window.setModal(true);
				window.show();
			}
	    };
	    
	    addTask2Panel();
	    	
	    
/*	    if(UIContext.customizedModel.getShowDeploy() == null || UIContext.customizedModel.getShowDeploy()) {
	    	boolean enabled = UIContext.serverVersionInfo.edgeInfoCM == null
	    	|| UIContext.serverVersionInfo.edgeInfoCM.getEdgeHostName() == null
	    	|| UIContext.serverVersionInfo.edgeInfoCM.getEdgeHostName().isEmpty();
	    	addItem(UIContext.Constants.homepageTasksDeployLabel(),
	    			UIContext.Constants.homepageTasksDeployDescription(),
	    			deployHandler, UIContext.IconBundle.tasks_deploy().createImage(), null, "BC90F343-E863-49b0-80B4-4F691CB3DD7A", enabled,new LayoutContainer());
	    }*/
	    
	    checkDriver();
	}

	private final ColdStandbyServiceAsync coldStandByService = GWT.create(ColdStandbyService.class);
	
	protected void addTask2Panel() 
	{
		String location = Location.getParameter(D2DHomePageTab.LOCATION_PARAM);

		// We are blocking 32-bit proxy, so if current UI is for VM and  proxy is 32-bit we block "Backup Now" and 
		// "Recover VM"
		if(location != null && location.length() > 0 && location.equals(HomeContentFactory.LOCATION_VSPHERE))
		{
			coldStandByService.isHostAMD64Platform(new BaseAsyncCallback<Boolean>()
			{
				@Override
				public void onFailure(Throwable caught)
				{
				    addTask2Panel(true);
				}

				@Override
				public void onSuccess(Boolean result)
				{
				    addTask2Panel(result);
				}
			});
		}
		else
			addTask2Panel(true);
	}

	protected void addTask2Panel(boolean amd64) {
		RolePrivilegeModel privilege=UIContext.RolePrivilege;
		backupIcon = AbstractImagePrototype.create(UIContext.IconBundle.tasks_backup()).createImage();
		backupIcon.ensureDebugId("9CDDB237-E9E4-4562-8D43-241F91929031");
	    if((UIContext.customizedModel.getShowBackup() == null || UIContext.customizedModel.getShowBackup())&&(privilege==null||privilege.getBackupFlag()!=RolePrivilegeModel.DISPLAY_DISABLE)){
	    	backupContainer = new LayoutContainer();
	    	String backupNow = UIContext.Constants.homepageTasksBackupNowLabel();
	    	if (!amd64)
	    		backupNow += " " + UIContext.Constants.notSupport32BitProxy();

	    	backupLabel = addItem(backupNow, 
	    					      UIContext.Constants.homepageTasksBackupNowDescription(),
	    					      null,backupIcon, null, "72A3976C-FE4F-4055-94EB-BA151D2CE223", 
	    					      amd64, 
	    					      backupContainer);
			backupLabel.ensureDebugId("46E7422C-5C3E-497c-8E14-066C845D496E");
		}
	    
	    Menu restoreSubMenu = createRestoreSubMenu();
    	restoreIcon = AbstractImagePrototype.create(UIContext.IconBundle.tasks_restore()).createImage();
    	restoreIcon.ensureDebugId("247D46CF-A4D2-4a11-84FE-BFC11C6AE87B");
    	if((UIContext.customizedModel.getShowRestore() == null || UIContext.customizedModel.getShowRestore())&&(privilege==null||privilege.getRestoreFlag()!=RolePrivilegeModel.DISPLAY_DISABLE)){
            restoreContainer = new LayoutContainer();
    		restoreLabel = addItem(UIContext.Constants.homepageTasksRestoreLabel(), 
    						       UIContext.Constants.homepageTasksRestoreDescription(),null,
    						       restoreIcon, restoreSubMenu, 
    						       "5C025C2D-D2C7-43f9-B961-740CBBD8086B", true, restoreContainer);
    		restoreLabel.ensureDebugId("AF60633F-D354-4c5a-918F-2B603371E960");
    	}
    	
	    settingIcon = AbstractImagePrototype.create(UIContext.IconBundle.tasks_backupSetting()).createImage();
	    settingIcon.ensureDebugId("FDFCB3D6-4065-48db-8FE8-FC33D676DFFA");
	    if((UIContext.customizedModel.getShowSettings() == null || UIContext.customizedModel.getShowSettings())&&(privilege==null||privilege.getSettingFlag()!=RolePrivilegeModel.DISPLAY_DISABLE)){
	    	settingContainer = new LayoutContainer();
	    	settingLabel = addItem(UIContext.Constants.homepageTasksBackupSettingLabel(), UIContext.Constants.homepageTasksBackupSettingDescription(),null,settingIcon, null, "B09DD75C-63BC-4267-B84C-736B9B925635", true,settingContainer);
	    	settingLabel.ensureDebugId("E06F948E-678E-4160-932C-1C45663CBECD");
	    }
	    
	    //addTask(UIContext.Constants.homepageTasksArchiveSettingLabel(),UIContext.Constants.homepageTasksArchiveSettingDescription(),archiveSettingHandler,UIContext.IconBundle.tasks_backupSetting());
	    if((UIContext.customizedModel.getShowCopy() == null || UIContext.customizedModel.getShowCopy())&&(privilege==null||privilege.getCopyRecoverPointFlag()!=RolePrivilegeModel.DISPLAY_DISABLE)) {
	    	addItem(UIContext.Constants.homepageTasksRecoveryPointsLabel(),
	    			UIContext.Constants.homepageTasksRecoveryPointsDescription(),
	    			recoveryPointHandler, AbstractImagePrototype.create(UIContext.IconBundle.tasks_recovery())
	    				.createImage(), null, "05EEB110-4F79-4b7f-9F68-5C8035859A24", true, new LayoutContainer());
	    }
	    	
	    
	    if((UIContext.customizedModel.getShowMountVolume() == null || UIContext.customizedModel.getShowMountVolume())&&(privilege==null||privilege.getMountRecoverPointFlag()!=RolePrivilegeModel.DISPLAY_DISABLE)) {
	    	addItem(UIContext.Constants.mountTaskTitle(),
	    			UIContext.Constants.mountTaskDescription(),
	    			mountHandler,   AbstractImagePrototype.create(UIContext.IconBundle.task_mount_volume()).createImage(), null, "D89C96BD-CAC6-4689-A862-0340268B00BD", true,new LayoutContainer());
	    }
	    
	    if(UIContext.customizedModel.getShowLog() == null || UIContext.customizedModel.getShowLog()) {
	    	addItem(UIContext.Constants.homepageTasksLogsLabel(),
	    			UIContext.Constants.homepageTasksLogsDescription(), logHandler,
	    			AbstractImagePrototype.create(UIContext.IconBundle.tasks_log()).createImage(), null, "4A5253FB-6C8E-4dcf-B6D4-E4C9505835A7", true,new LayoutContainer());
	    }
	}
	
	protected void checkDriver() {
		try {
		loginService.installDriver(new BaseAsyncCallback<Boolean>(){

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
			}

			@Override
			public void onSuccess(Boolean result) {
				isDriverInstalled = result;
				if(result) {
					try {
					loginService.installDriverRestart(new BaseAsyncCallback<Boolean>() {

						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
						}

						@Override
						public void onSuccess(Boolean result) {
							isDriverInstalled = result;
							refresh();
						}
					});
					}catch(Throwable t) {}
				}else {
					refresh();
				}
			}
		});
		}catch(Throwable t) {}
	}
	
	private void refresh() {
		if(!isDriverInstalled) {
			//setOpacity(backupLabel, backupIcon);
			//setOpacity(restoreLabel, restoreIcon);
			//setOpacity(settingLabel, settingIcon);
			if(backupContainer!=null){
				backupContainer.setEnabled(false);
			}
			if(restoreContainer!=null){
				restoreContainer.setEnabled(false);
			}
			if(settingContainer!=null){
				settingContainer.setEnabled(false);
			}
		}else {
			if(backupNowHandler != null && backupContainer != null)
				backupContainer.addListener(Events.OnClick, backupNowHandler);
			if(restoreHandler != null && restoreContainer != null)
				restoreContainer.addListener(Events.OnClick, restoreHandler);
			if(settingHandler != null && settingContainer != null)
				settingContainer.addListener(Events.OnClick, settingHandler);
		}
	}
	
	/*private void setOpacity(Label label, Image icon) {
		label.setStylePrimaryName("TaskOpacity");
		icon.setStylePrimaryName("TaskOpacity");
	}*/
	
	protected Label addItem(String text, String description, Listener<ComponentEvent> handler, Image icon, Menu subMenu, String debugID, boolean enabled, final LayoutContainer container){
		TableLayout layout = new TableLayout();
		layout.setColumns(2);
		layout.setWidth("100%");
		
		container.setEnabled(enabled);
	
		container.sinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT | Event.ONMOUSEMOVE | Event.ONCLICK);
		
		container.addListener(Events.OnMouseOver, new Listener<ComponentEvent>()
		{

			@Override
			public void handleEvent(ComponentEvent be)
			{
				container.addStyleName("navigation-button-over");
				
			// if sub menu is enabled and restore is enabled
				if (showSubMenu && isDriverInstalled)
				{
					Menu subMenu = container.getData("subMenu");
					
					if (subMenu != null)
					{
						String align = "tr-tl";
						int[] adj = new int[] { 0, 1 };
						subMenu.show(container.getElement(), align, adj);
						
						activeSubMenu = subMenu;
					}
					else
					{
						if (activeSubMenu != null && activeSubMenu.isRendered())
						{
							activeSubMenu.hide();
						}
					}
				}
			}

		});
		container.addListener(Events.OnMouseOut, new Listener<ComponentEvent>()
		{

			@Override
			public void handleEvent(ComponentEvent be)
			{
				container.removeStyleName("navigation-button-over");
			}

		});
		container.setStyleAttribute("padding", "2px");
		container.setLayout(layout);
		
		TableData tableData = null;
		tableData = new TableData();
		tableData.setWidth("36");
		tableData.setVerticalAlign(VerticalAlignment.TOP);
		//tableData.setRowspan(2);
		icon.setTitle(description);
		icon.ensureDebugId("E18565EF-6755-452a-8CF5-A1346052C688" + text);
		icon.setStyleName("homepage_task_icon");
		container.add(icon,tableData);
		
		tableData = new TableData();
		tableData.setVerticalAlign(VerticalAlignment.MIDDLE);
		Label label = new Label(text);
		label.ensureDebugId(debugID);
		label.setTitle(description);
		label.setStyleName("homepage_task_label");
		if(handler != null && enabled) {
			container.addListener(Events.OnClick, handler);
		}
		
		container.setData("subMenu", subMenu);
		container.add(label, tableData);
		label.ensureDebugId("156CA535-4EA8-4b51-ACD7-2429CF9C7BC1" + text);
		verticalPanel.add(container);
		
		return label;
		}
		
	// TODO: for the sub menu of restore menu
	protected Menu createRestoreSubMenu()
	{
		Menu menu = new Menu();
		
		MenuItem menuItem = new MenuItem();
		menuItem.setText(UIContext.Constants.restoreBrowseButton());
		menuItem.setIcon(AbstractImagePrototype.create(UIContext.IconBundle.restore_browse_16()));
		menuItem.addSelectionListener(new SelectionListener<MenuEvent>()
		{
			public void componentSelected(MenuEvent ce)
			{
				DeferredCommand.addCommand(new Command()
				{
					@Override
					public void execute()
					{
						RestoreWizardWindow window = new RestoreWizardWindow(RestoreWizardContainer.PAGE_RECOVERY);
						window.setModal(true);
						window.show();
					}
				});
			}
		});
		menu.add(menuItem);
		
		
		CustomizationModel customizedModel = UIContext.customizedModel;
		Boolean isFileCopyEnabled = customizedModel.get("FileCopy");
		
		if(isFileCopyEnabled)
		{	
		menuItem = new MenuItem();
		menuItem.setText(UIContext.Constants.restoreBrowseArchiveButton());
		menuItem.setIcon(AbstractImagePrototype.create(UIContext.IconBundle.restore_destination_16()));
		menuItem.addSelectionListener(new SelectionListener<MenuEvent>()
		{
			public void componentSelected(MenuEvent ce)
			{
				DeferredCommand.addCommand(new Command()
				{
					@Override
					public void execute()
					{
						RestoreWizardWindow window = new RestoreWizardWindow(RestoreWizardContainer.PAGE_ARCHIVE_RECOVERY);
						window.setModal(true);
						window.show();
					}
				});
			}
		});
		menu.add(menuItem);
		}

		
		menuItem = new MenuItem();
		menuItem.setText(UIContext.Constants.restoreSearchButton());
		menuItem.setIcon(AbstractImagePrototype.create(UIContext.IconBundle.restore_search_16()));
		menuItem.addSelectionListener(new SelectionListener<MenuEvent>()
		{
			public void componentSelected(MenuEvent ce)
			{
				DeferredCommand.addCommand(new Command()
				{
					@Override
					public void execute()
					{
						RestoreWizardWindow window = new RestoreWizardWindow(RestoreWizardContainer.PAGE_SEARCH);
						window.setModal(true);
						window.show();
					}
				});
			}
		});
		menu.add(menuItem);

		
		menuItem = new MenuItem();
		menuItem.setText(UIContext.Constants.vmRecoverButton());
		menuItem.setIcon(AbstractImagePrototype.create(UIContext.IconBundle.restore_recover_16()));
		menuItem.addSelectionListener(new SelectionListener<MenuEvent>()
		{
			public void componentSelected(MenuEvent ce)
			{
				DeferredCommand.addCommand(new Command()
				{
					@Override
					public void execute()
					{
						RestoreWizardWindow window = new RestoreWizardWindow(RestoreWizardContainer.PAGE_VM_RECOVERY);
						window.setModal(true);
						window.show();
					}
				});
			}
		});
		menu.add(menuItem);
		
		menuItem = new MenuItem();
		menuItem.setText(UIContext.Constants.restoreBrowseExchangeGRTButton());
		menuItem.setIcon(AbstractImagePrototype.create(UIContext.IconBundle.restore_browse_exchange_grt_16()));
		menuItem.addSelectionListener(new SelectionListener<MenuEvent>()
		{
			public void componentSelected(MenuEvent ce)
			{
				DeferredCommand.addCommand(new Command()
				{
					@Override
					public void execute()
					{
						RestoreWizardWindow window = new RestoreWizardWindow(RestoreWizardContainer.PAGE_EXCHANGE_GRT_RECOVERY);
						window.setModal(true);
						window.show();
					}
				});
			}
		});
		menu.add(menuItem);


		return menu;
	}
	
	
	protected void onBackupNowClick() {
		if (!isbkpNowClicked) {
			isbkpNowClicked = true;					
			service
					.isOnlyFullBackup(new BaseAsyncCallback<Boolean>() {

						@Override
						public void onFailure(Throwable caught) {
							isbkpNowClicked = false;
							BackupNowWindow bkpNowWnd = new BackupNowWindow();
							bkpNowWnd.setModal(true);
							bkpNowWnd.show();
							bkpNowWnd.changeSettings(false);
							super.onFailure(caught);
						}

						@Override
						public void onSuccess(Boolean result) {
							GWT.log("isCompressionLevelChagned:"
									+ result, null);
							isbkpNowClicked = false;
							BackupNowWindow bkpNowWnd = new BackupNowWindow();
							bkpNowWnd.setModal(true);									
							bkpNowWnd.show();
							bkpNowWnd.changeSettings(result);
						}
					});

		}
	}

	protected void onSettingClick() {
		CommonSettingWindow window = new CommonSettingWindow(AppType.D2D);
		window.setSize(880, 600);
		window.setModal(true);
		window.show();
	}

	protected void onLogClick() {
		LogWindow window = new LogWindow(true);
		window.setModal(true);
		window.show();
	}

	protected void onRecoveryPointClick() {
		RecoveryPointWindow window = new RecoveryPointWindow();
		window.setModal(true);
		window.show();
	}

	// TODO: used by VCM
	public static LayoutContainer addTask(VerticalPanel panel, String label, String description, ClickHandler handler, AbstractImagePrototype image, boolean addHR, String debugID){
		TableLayout layout = new TableLayout();
		layout.setColumns(2);
		layout.setWidth("100%");
		
		final LayoutContainer container = new LayoutContainer() {

			@Override
			public void setEnabled(boolean enabled) {
				super.setEnabled(enabled);
			}
			
		};
		
		container.sinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT | Event.ONMOUSEMOVE | Event.ONCLICK);
		
		container.addListener(Events.OnMouseOver, new Listener<ComponentEvent>()
		{

			@Override
			public void handleEvent(ComponentEvent be)
			{
				container.addStyleName("navigation-button-over");
			}

		});
		container.addListener(Events.OnMouseOut, new Listener<ComponentEvent>()
		{

			@Override
			public void handleEvent(ComponentEvent be)
			{
				container.removeStyleName("navigation-button-over");
			}

		});
		
		container.setStyleAttribute("padding", "2px");
		container.setLayout(layout);
		
		TableData tableData = null;
		tableData = new TableData();
		tableData.setWidth("36");
		tableData.setVerticalAlign(VerticalAlignment.TOP);
		//tableData.setRowspan(2);
		
		Image icon = null;
		if (image !=null){
			icon = image.createImage();
			icon.setTitle(description);
			icon.setStyleName("homepage_task_icon");
			if (handler!=null)
				icon.addClickHandler(handler);
			container.add(icon,tableData);
		}else
			container.add(new HTML(), tableData);
		
		tableData = new TableData();
		tableData.setVerticalAlign(VerticalAlignment.MIDDLE);
		Label text = new Label(label);
		text.ensureDebugId(debugID);
		text.setTitle(description);
		text.setStyleName("homepage_task_label");
		
		if (handler!=null) {
			text.addClickHandler(handler);
			//if(icon != null)
			//	icon.addClickHandler(handler);
		}

		container.add(text, tableData);
		
		//tableData = new TableData();
		//tableData.setVerticalAlign(VerticalAlignment.TOP);
		//Text descriptionText = new Text(description);
		//descriptionText.setStyleName("homepage_task_description");
		//container.add(descriptionText, tableData);
		
//		if (addHR){
//			tableData = new TableData();
//			tableData.setColspan(2);
//			HTML hr = new HTML("<hr/>");
//			hr.setStyleName("homepage_task_hr");
//			container.add(hr,tableData);
//		}
		
		panel.add(container);
		return container;
	}
	
	// TODO: used by VCM
	public static LayoutContainer addTask(VerticalPanel panel, String label, String description, ClickHandler handler, AbstractImagePrototype image, String debugID){
		return addTask(panel, label, description, handler, image ,true, debugID);
	}

}
