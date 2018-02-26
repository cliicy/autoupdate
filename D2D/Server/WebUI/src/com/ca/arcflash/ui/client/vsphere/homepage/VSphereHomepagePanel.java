package com.ca.arcflash.ui.client.vsphere.homepage;

import java.util.List;

import com.ca.arcflash.ha.model.VCMConfigStatus;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyManager;
import com.ca.arcflash.ui.client.coldstandby.VirtualConversionTabPanel;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.BaseComboBox;
import com.ca.arcflash.ui.client.common.ComboBoxTree;
import com.ca.arcflash.ui.client.common.HelpTopics;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.homepage.AboutWindow;
import com.ca.arcflash.ui.client.homepage.HomepageService;
import com.ca.arcflash.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcflash.ui.client.homepage.ManagedByEdgeContainer;
import com.ca.arcflash.ui.client.model.BackupVMModel;
import com.ca.arcflash.ui.client.model.EdgeInfoModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.TabPanelEvent;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.menu.SeparatorMenuItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.extjs.gxt.ui.client.store.ListStore;

public class VSphereHomepagePanel extends LayoutContainer{
	private final HomepageServiceAsync service = GWT.create(HomepageService.class);
	public static final int REFRESH_INTERVAL = 10*1000;
	public static final String SELECT_TAB = "selecttab";
	public static final String VCM_TAB = "vcm";
	private VCMConfigStatus vcmConfigStatus;
	private Boolean isSelectedVCM = false;
	private MenuItem knowledgeCenterItem;
	private MenuItem helpItem;
	private MenuItem userguideItem;
	private Menu helpMenu;
	//private ComboBoxTree<BackupVMModel> vmCombo;
	//(4th March 2015) changing back to original widget as we are not supporting vapp in tungsten
	private BaseComboBox<BackupVMModel> vmCombo;
	
	private Timer cmTimer;
	private static final int REFRESH_CM_INTERVAL = 30000;
	private boolean refreshRunning = false;

	@Override
	public void onLoad(){
		List<Component> items=helpMenu.getItems();
		for (Component item:items){
			if(item instanceof MenuItem)item.addStyleName("ARCSERVE-STYLE-MENU-ITEM");	
		}
	}
	
	public void onRender(Element target, int index) {
		super.onRender(target, index);
		final BorderLayout layout = new BorderLayout();
		layout.setContainerStyle("navigation-background");
		setLayout(layout);

		BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 75);
		northData.setMargins(new Margins(0, 0, 1, 0));
		
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
		centerData.setMargins(new Margins(0));
		
		add(createHeader(), northData);

		add(createTabPanel(),centerData);
	
	}
	
	private Widget createTabPanel() {
			return showContentWithVCM(false);
	}
	
	private Widget showContentWithVCM(boolean withVCM) {
		Widget content = null;
		if(withVCM && !UIContext.isRemoteVCM) {
			final TabPanel tabPanel = new TabPanel();
			
			tabPanel.setStyleAttribute("overflow", "visible");
			
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
			
			tabPanel.setResizeTabs(true);
			
			tabPanel.add(new VSphereHomePageTab());
			VirtualConversionTabPanel vcm = new VirtualConversionTabPanel();
			tabPanel.add(vcm);
			tabPanel.setMinTabWidth(250);
			
			tabPanel.addListener(Events.Select, new Listener<TabPanelEvent>() {
				@Override
				public void handleEvent(TabPanelEvent be) {
					if(be.getItem() instanceof VirtualConversionTabPanel){
						isSelectedVCM = true;
						helpItem.setText(UIContext.Messages.arcserveD2DHelp(UIContext.productNameVCM));
						showManagedByEdgeLabel();
					}
					else{
						isSelectedVCM = false;
						helpItem.setText(UIContext.Messages.arcserveD2DHelp(UIContext.productNamevSphere));
						showManagedByEdgeLabel();
						UIContext.vSphereHomepagePanel.refreshWarningUI();
					}
				}
			});
			String tabName = Location.getParameter(SELECT_TAB);
			if(VCM_TAB.equalsIgnoreCase(tabName))
				tabPanel.setSelection(vcm);
			
			content = tabPanel;
		} else if (withVCM && UIContext.isRemoteVCM) {
			content = new VirtualConversionTabPanel();
			isSelectedVCM = true;
			helpItem.setText(UIContext.Messages.arcserveD2DHelp(UIContext.productNameVCM));
			showManagedByEdgeLabel();
		}
		else {
			content = new VSphereHomePageTab();
		}
		return content;
	}
	
	private LayoutContainer createHeader()
	{
		final LayoutContainer container = new LayoutContainer();		
		TableLayout tableLayout = new TableLayout(1);
		tableLayout.setWidth("100%");
		
		// first row
		container.add(createRow_1_Banner());
		
		// second row
		container.add(createRow_2_User());
		refreshVMList();
		if (!UIContext.isRemoteVCM) {
			cmTimer = new Timer()
			{
				public void run()
				{
					if (UIContext.vSphereHomepagePanel.isRendered() && container.isRendered() && container.isVisible())
					{
						if (!refreshRunning)
						{
							refreshVMList();
						}
					}
				}
			};
			cmTimer.schedule(REFRESH_CM_INTERVAL);
			cmTimer.scheduleRepeating(REFRESH_CM_INTERVAL);
		}
		
		return container;
	}
	
	private LayoutContainer createRow_2_User()
	{
		LayoutContainer container = new LayoutContainer();		
		TableLayout tableLayout = new TableLayout(5);
		tableLayout.setWidth("100%");
		container.setLayout(tableLayout);
		
		TableData td = new TableData();
		td.setWidth("5%");
		td.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		TableData td1 = new TableData();
		td1.setWidth("25%");
		td1.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		// left
		Text text = new Text(UIContext.Constants.vSphereVMHostname());
		text.ensureDebugId("550C8385-7A15-4e82-9285-C85FA1ACCF7D");
		text.setStyleName("homepage_header_user_label");
		text.setStyleAttribute("padding", "2 2 2 5");
		container.add(text,td);
		
		text = new Text(UIContext.backupVM.getVMName());
		text.ensureDebugId("66FC95D0-94F5-4eac-B7B7-439275366874");
		text.setStyleName("homepage_managing_server_label");
		container.add(text,td1);
		
		td = new TableData();
		td.setWidth("65%");
		td.setHorizontalAlign(HorizontalAlignment.RIGHT);
		UIContext.managedByEdgeContainer = new ManagedByEdgeContainer();
		
		container.add(UIContext.managedByEdgeContainer,td);
		
		td1 = new TableData();
		td1.setWidth("5%");
		td1.setHorizontalAlign(HorizontalAlignment.RIGHT);
		container.add(createWidget_LogonUser(),td1);
		
		return container;
	}
	
	
	@Override
	protected void afterRender() {
		super.afterRender();
		
		showManagedByEdgeLabel();
	}

	private void showManagedByEdgeLabel() {
		EdgeInfoModel edgeInfoVS = null;
		if(isSelectedVCM) {
			edgeInfoVS = UIContext.serverVersionInfo.edgeInfoVCM;
		}
		else {
			edgeInfoVS = UIContext.serverVersionInfo.edgeInfoVS;
		}
		
		if(edgeInfoVS != null){
			UIContext.managedByEdgeContainer.setEdgeServer(edgeInfoVS.getEdgeHostName(),edgeInfoVS.getEdgeUrl());
		}else{
			UIContext.managedByEdgeContainer.setEdgeServer(null, null);
		}
	}

	private LayoutContainer createWidget_LogonUser()
	{
		LayoutContainer container = new LayoutContainer();
		
		TableLayout tableLayout = new TableLayout(5);
		tableLayout.setCellPadding(4);
		container.setLayout(tableLayout);	
		
		Text lf = new Text();
		lf.ensureDebugId("A191D1B2-48DB-44df-8928-880B9C5FFABB");
		lf.setStyleName("homepage_header_user_label");
		lf.setText(UIContext.Messages.vSphereVMProtectedByProxy(UIContext.serverVersionInfo.getLocalHostName()));
		container.add(lf);
		
		// help menu
		Image icon = AbstractImagePrototype.create(UIContext.IconBundle.homepage_help_icon()).createImage();		
		container.add(icon);
		container.add(createWidget_HelpMenu());
		
		return container;
	}
	
	private LayoutContainer createWidget_HelpMenu()
	{
		LayoutContainer container = new LayoutContainer();
		TableLayout tableLayout = new TableLayout(2);
		container.setLayout(tableLayout);
		
		helpMenu = new Menu();
 
        knowledgeCenterItem = new MenuItem();       
        knowledgeCenterItem.setText(UIContext.Constants.knowledgeCenter());
        helpMenu.addStyleName("ARCSERVE-STYLE-HELP-MENU");
        knowledgeCenterItem.addSelectionListener(new SelectionListener<MenuEvent>() {
        	public void componentSelected(MenuEvent ce) {           
            		HelpTopics.showHelpURL(UIContext.externalLinks.getKnowledgeCenterURL());
            }           
          });      
        helpMenu.add(knowledgeCenterItem);
        
        helpItem = new MenuItem();
        helpItem.ensureDebugId("799f6b51-4dae-4d5f-9def-c9f4e69cde4a");
        helpItem.setText(UIContext.Constants.helphtml());
        helpItem.addSelectionListener(new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
            	if(isSelectedVCM)
            		HelpTopics.showHelpURL(UIContext.externalLinks.getVirtualStandbyHelp());
            	else
            		HelpTopics.showHelpURL(UIContext.externalLinks.getHomePagePanelHelp());
              }
          });
        helpMenu.add(helpItem);

        userguideItem = new MenuItem();
        userguideItem.ensureDebugId("b8466f5b-daa8-4cfd-b4ed-ba2483fb92ea");
        userguideItem.setText(UIContext.Constants.userGuidePDF());
        userguideItem.addSelectionListener(new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
            	if(isSelectedVCM)
            		HelpTopics.showHelpURL(UIContext.externalLinks.getVirtualStandbyUserGuideHelp());
            	else
            		HelpTopics.showHelpURL(UIContext.externalLinks.getOnlineUserGuideURL());
              }
          });
        helpMenu.add(userguideItem);        
        
        final SeparatorMenuItem smItem2 = new SeparatorMenuItem();
        helpMenu.add(smItem2);

        MenuItem aboutItem = new MenuItem();
        aboutItem.ensureDebugId("ea7dbba9-cc2d-4d52-8a60-a15fc75741c0");
        aboutItem.setText(UIContext.Constants.about());
        aboutItem.addSelectionListener(new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
            	AboutWindow window = new AboutWindow();
				window.setModal(true);
				window.show();
              }
          });
        
        helpMenu.add(aboutItem);
        
        final com.extjs.gxt.ui.client.widget.Label link = new com.extjs.gxt.ui.client.widget.Label(){
			@Override
			protected void onAttach() {
				super.onAttach();
				sinkEvents(Event.ONMOUSEUP);
			}

			public void onBrowserEvent(Event event) {

	            switch (DOM.eventGetType(event)) {
	                    case Event.ONMOUSEUP: {
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
		arrowImage.getElement().getStyle().setCursor(Style.Cursor.POINTER);
		arrowImage.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				helpMenu.showAt(link.getAbsoluteLeft(), link.getAbsoluteTop()+link.getHeight());
			}
			
		});
        
        container.add(link);
        container.add(arrowImage);
        return container;
	}
	
	//we are not supporting vapp now in tungsten. Also the below code is not working. Hence comment this and use the BaseComboBox instead of ComboBoxTree
	/*private void refreshVMList(){
		refreshRunning = true;
		vmCombo.mask();
		service.getConfiguredVM(new BaseAsyncCallback<BackupVMModel[]>(){
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				refreshRunning = false;
				vmCombo.unmask();
			}

			@Override
			public void onSuccess(BackupVMModel[] result) {
				try {
					if(result != null && result.length > 0){
						TreeStore<BackupVMModel> store = vmCombo.getStore();
						store.removeAll();
						for (int i = 0; i < result.length; i++) {
							processBackupVMModel(result[i]);
							store.add(result[i], false);
							if(result[i].memberVMList != null) {
								for(BackupVMModel child : result[i].memberVMList) {
									processBackupVMModel(child);
									store.add(result[i], child, false);
									if(child.getVmInstanceUUID().equals(UIContext.backupVM.getVmInstanceUUID())){
										vmCombo.setValue(child);
									}
								}
							}
							if(result[i].getVmInstanceUUID().equals(UIContext.backupVM.getVmInstanceUUID())){
								vmCombo.setValue(result[i]);
							}
						}
						vmCombo.getTreePanel().expandAll();
					}else{
						vmCombo.setEmptyText(UIContext.Constants.vSphereNoVMProtected());
					}
					refreshRunning = false;
					vmCombo.unmask();
				} catch (Exception e) {
					refreshRunning = false;
					vmCombo.unmask();
				}
			}
		});

	}*/

	private void refreshVMList(){
		refreshRunning = true;
		vmCombo.mask();
		service.getConfiguredVM(new BaseAsyncCallback<BackupVMModel[]>(){
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				refreshRunning = false;
				vmCombo.unmask();
			}
			
			@Override
			public void onSuccess(BackupVMModel[] result) {
				try {
					if(result != null && result.length > 0){
						ListStore<BackupVMModel> store = vmCombo.getStore();
						store.removeAll();
						for (int i = 0; i < result.length; i++) {
							processBackupVMModel(result[i]);
							store.add(result[i]);
							/*if(result[i].memberVMList != null) {
								for(BackupVMModel child : result[i].memberVMList) {
									processBackupVMModel(child);
									store.add(result[i], child, false);
									if(child.getVmInstanceUUID().equals(UIContext.backupVM.getVmInstanceUUID())){
										vmCombo.setValue(child);
									}
								}
							}*/
							if(result[i].getVmInstanceUUID().equals(UIContext.backupVM.getVmInstanceUUID())){
								vmCombo.setValue(result[i]);
							}
						}
						
					}else{
						vmCombo.setEmptyText(UIContext.Constants.vSphereNoVMProtected());
					}
					refreshRunning = false;
					vmCombo.unmask();
				} catch (Exception e) {
					refreshRunning = false;
					vmCombo.unmask();
				}
			}
		});

	}
	
	private void processBackupVMModel(BackupVMModel model){
		if(model.getVmHostName() == null || model.getVmHostName().equals("")){
			if(model.getVMType() == BackupVMModel.Type.VMware_VApp.ordinal()) {
				model.setVmHostName(model.getVMName());
			} else {
				model.setVmHostName(UIContext.Messages.unknown_vm(model.getVMName()));
			}
			model.set("displayName", model.getVmHostName());
			
		} else {
			model.set("displayName", model.getVmHostName()+"("+model.getVMName()+")");
		}
	}
	
	private LayoutContainer createRow_1_Banner()
	{
		LayoutContainer container = new LayoutContainer();
		container.addStyleName("homepage_banner_background");
		
		TableLayout tableLayout = new TableLayout(2);
		tableLayout.setWidth("100%");		
		container.setLayout(tableLayout);
		
		Label prdname = new Label("");
		prdname.addStyleName("homepage_logo");
		TableData td = new TableData();
		td.setVerticalAlign(VerticalAlignment.TOP);
		td.setWidth("100%");
		// first column
		container.add(prdname, td);
		
		// second column
		td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.RIGHT);
		td.setVerticalAlign(VerticalAlignment.MIDDLE);
		container.add(createWidget_VMCombo(), td);
		
		return container;
	}
	
	private LayoutContainer createWidget_VMCombo(){
		HorizontalPanel container = new HorizontalPanel();
		container.setVerticalAlign(VerticalAlignment.MIDDLE);
		if (UIContext.isRemoteVCM) {
			return container;
		}
		ListStore<BackupVMModel> vmListStore = new ListStore<BackupVMModel>();
		vmListStore.setStoreSorter(new StoreSorter<BackupVMModel>(){
			@Override
			public int compare(Store<BackupVMModel> store, BackupVMModel m1, BackupVMModel m2, String property) {
				return (m1.getVmHostName()+"("+m1.getVMName()+")").compareTo((m2.getVmHostName()+"("+m2.getVMName()+")"));
			}
		});
		vmCombo = new BaseComboBox<BackupVMModel>();
		vmCombo.ensureDebugId("D1BE5E3E-EED8-48ab-A2CE-FBCB0DC960A2");
		vmCombo.setWidth(210);
		vmCombo.setDisplayField("displayName");
		vmCombo.setEmptyText(UIContext.Constants.trustHost());
		Utils.addToolTip(vmCombo, UIContext.Constants.vSphereChangeToViewVMToolTip());
		vmCombo.setEditable(false);
		vmCombo.setTemplate(getTemplate());
		vmCombo.setStore(vmListStore);
		vmCombo.addSelectionChangedListener(new SelectionChangedListener<BackupVMModel>(){
			@Override
			public void selectionChanged(SelectionChangedEvent<BackupVMModel> se){
				final BackupVMModel selectedVM = se.getSelectedItem();
				if(!UIContext.backupVM.getVmInstanceUUID().equals(selectedVM.getVmInstanceUUID())){
					
					// To Support Warning Icon instead of default (Information Icon) : issue : 18630835
					final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>()
					{
						public void handleEvent(MessageBoxEvent be)
						{
							if (be.getButtonClicked().getItemId().equals(com.extjs.gxt.ui.client.widget.Dialog.YES))
							{
								String url = Window.Location.getProtocol()+"//"+Window.Location.getHost()+"/?location=vm&vmname="+URL.encodeComponent(selectedVM.getVMName())+"&instanceuuid="+selectedVM.getVmInstanceUUID();
								Window.Location.assign(url);
							}
						}
					};
					MessageBox mb = new MessageBox();
					mb.setIcon(MessageBox.WARNING);
					mb.setButtons(MessageBox.YESNO);
					mb.setTitleHtml(UIContext.Messages.messageBoxTitleWarning(UIContext.productNameD2D));
					mb.setMessage(UIContext.Messages.vSphereChangeToOtherVM(selectedVM.getVmHostName()+"("+selectedVM.getVMName()+")"));
					Utils.setMessageBoxDebugId(mb);
					mb.addCallback(l);
					mb.show();
				}
				
			}
		});

		container.add(vmCombo);
		
		return container;
		
	}
		
	/*private LayoutContainer createWidget_VMCombo(){
		HorizontalPanel container = new HorizontalPanel();
		container.setVerticalAlign(VerticalAlignment.MIDDLE);
		if (UIContext.isRemoteVCM) {
			return container;
		}
		TreeStore<BackupVMModel> vmTreeStore = new TreeStore<BackupVMModel>();
		vmTreeStore.setStoreSorter(new StoreSorter<BackupVMModel>(){
			@Override
			public int compare(Store<BackupVMModel> store, BackupVMModel m1, BackupVMModel m2, String property) {
				return (m1.getVmHostName()+"("+m1.getVMName()+")").compareTo((m2.getVmHostName()+"("+m2.getVMName()+")"));
			}
		});
		vmCombo = new ComboBoxTree<BackupVMModel>(vmTreeStore);
		vmCombo.ensureDebugId("D1BE5E3E-EED8-48ab-A2CE-FBCB0DC960A2");
		vmCombo.setForceSelection(true);
		vmCombo.setWidth(240);
		vmCombo.setDisplayField("displayName");
		vmCombo.setEmptyText(UIContext.Constants.trustHost());
		
		vmCombo.getTreePanel().setAutoHeight(true);
		vmCombo.getTreePanel().setAutoExpand(true);
		vmCombo.setDisplayProperty("vmHostName");
		
		Utils.addToolTip(vmCombo, UIContext.Constants.vSphereChangeToViewVMToolTip());
		vmCombo.setEditable(false);
		vmCombo.addListener(Events.Change, new Listener<BaseEvent>() {
		@Override
		public void handleEvent(BaseEvent be) {
			final BackupVMModel selectedVM = vmCombo.getValue();
			String displayName = selectedVM.getVmHostName();
			if(selectedVM.getVMType() == BackupVMModel.Type.VMware_VApp.ordinal()) {
				displayName = selectedVM.getVMName();
			}
			if(!UIContext.backupVM.getVmInstanceUUID().equals(selectedVM.getVmInstanceUUID())){
				
				// To Support Warning Icon instead of default (Information Icon) : issue : 18630835
				final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>()
				{
					public void handleEvent(MessageBoxEvent be)
					{
						if (be.getButtonClicked().getItemId().equals(com.extjs.gxt.ui.client.widget.Dialog.YES))
						{
							String url = Window.Location.getProtocol()+"//"+Window.Location.getHost()+"/?location=vm&vmname="+URL.encodeComponent(selectedVM.getVMName())+"&instanceuuid="+selectedVM.getVmInstanceUUID();
							Window.Location.assign(url);
						}
					}
				};
				MessageBox mb = new MessageBox();
				mb.setIcon(MessageBox.WARNING);
				mb.setButtons(MessageBox.YESNO);
				mb.setTitleHtml(UIContext.Messages.messageBoxTitleWarning(UIContext.productNamevSphere));
				mb.setMessage(UIContext.Messages.vSphereChangeToOtherVM(displayName));
				Utils.setMessageBoxDebugId(mb);
				mb.addCallback(l);
				mb.show();
			}
		}
	});
		
		vmCombo.getTreePanel().setIconProvider(new ModelIconProvider<BackupVMModel>() {
			@Override
			public AbstractImagePrototype getIcon(BackupVMModel model) {
				if(BackupVMModel.Type.VMware_VApp.ordinal() == model.getVMType())
					return AbstractImagePrototype.create(UIContext.IconBundle.vsphere_vapp());
				else
					return AbstractImagePrototype.create(UIContext.IconBundle.vsphere_vm());
			}
		});
		container.add(vmCombo);
		
		return container;
		
	}
	*/
	private native String getTemplate() /*-{ 
	    return  [ 
	    '<tpl for=".">', 
	    '<div class="x-combo-list-item" qtip="{displayName}" qtitle="">{displayName}</div>', 
	    '</tpl>' 
	    ].join(""); 
	}-*/;  

	public VCMConfigStatus getVcmConfigStatus() {
		return vcmConfigStatus;
	}

	public void setVcmConfigStatus(VCMConfigStatus vcmConfigStatus) {
		this.vcmConfigStatus = vcmConfigStatus;
	}
	
	

}
