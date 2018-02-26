package com.ca.arcflash.ui.client.coldstandby;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ca.arcflash.ha.model.ARCFlashNode;
import com.ca.arcflash.ha.model.ARCFlashNodesSummary;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.homepage.navigation.NavigationBorderLayout;
import com.ca.arcflash.ui.client.monitor.MonitorService;
import com.ca.arcflash.ui.client.monitor.MonitorServiceAsync;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.BorderLayoutEvent;
import com.extjs.gxt.ui.client.event.BoxComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.StoreFilterField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridSelectionModel;
import com.extjs.gxt.ui.client.widget.treegrid.WidgetTreeGridCellRenderer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class VirtualConversionServerNavigator extends ContentPanel {
	
//	private BeanModelFactory factory = BeanModelLookup.get().getFactory(ARCFlashNode.class); 
	private MonitorServiceAsync service = GWT.create(MonitorService.class);
	
	private TreeStore<ModelData> treeStore;
	private static String IS_GROUP = "isGroup";
	private static String SERVER_NAME = "hostname";
	private static String STATUS = "status";
	private static String NODE = "node";
	private static String GROURP = "group";
	private static String NAME_LABEL = "name_label";
	private static String MENU_SHOW ="showmenu";
	private static int NAME_SIZE = 175;
	private static int MENU_SIZE = 25;
	
	ARCFlashNodesSummary nodesSummary;
	BaseTreeModel monitor = new BaseTreeModel();
	BaseTreeModel actionRequired = new BaseTreeModel();
	BaseTreeModel all = new BaseTreeModel();
	BaseTreeModel serverRunning = new BaseTreeModel();
	BaseTreeModel vmRunning = new BaseTreeModel();
//	BaseTreeModel bothDown = new BaseTreeModel();
	
	private ARCFlashNode selectedNode;
	private TreeGrid<ModelData> tree;
	private ColumnModel columnModel;
	
	private static final int REFRESH_INTERVAL = 5000;
	private Timer timer;
	//navigator lock counter, navigator is unlocked only if this lock counter is 0;   
	// this counter is normally 4 which means the lock counter is subtracted by one after each of the refreshing including Summary panel, 
	//VM Information, Provision Points and task panel finishes 
	private int lockCounter;
	
	private Map<String, BaseTreeModel> uuidToTreeModel = new HashMap<String, BaseTreeModel>();
	private Map<String, BaseTreeModel> uuidToAllTreeModel = new HashMap<String, BaseTreeModel>();
	
	private boolean showup = false;
	private boolean firstTimeLoad = true;

	private Menu menu;

	public VirtualConversionServerNavigator(){
		
		getColumnModel();

		getMenu();

		treeStore = new TreeStore<ModelData>();
		
		tree = new TreeGrid<ModelData>(treeStore,
				columnModel);
		tree.ensureDebugId("efwud66k-18xq-6jxx-l8l1-0s4kzmz445hu");
		tree.setHideHeaders(true);
		tree.setAutoExpandColumn(SERVER_NAME);
		
		TreeGridSelectionModel<ModelData> model = new TreeGridSelectionModel<ModelData>() {

			 public void handleEvent(BaseEvent e) {
				 if(e instanceof GridEvent) {
					 GridEvent event = (GridEvent)e;
					 if(event.getModel()!=null && event.getModel().get(IS_GROUP) != null && (Boolean)event.getModel().get(IS_GROUP))
					 	return;
				 }
				 	
				 super.handleEvent(e);
			 }
			
		};
		model.setSelectionMode(SelectionMode.SINGLE);
		tree.setSelectionModel(model);
		
		tree.setIconProvider(new ModelIconProvider<ModelData>() {

			@Override
			public AbstractImagePrototype getIcon(ModelData model) {
				return AbstractImagePrototype.create(UIContext.IconBundle.blank());
			}

		});
		
		tree.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<ModelData>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<ModelData> se) {
				ModelData selected = se.getSelectedItem();
				if(selected != null) {
					onServerSelectionChanged(selected, NODE_TYPE.NEW_NODE);
				}
			}
			
		});
		
//		tree.setEnableColumnResize(false);
		tree.setTrackMouseOver(true);
		tree.setStripeRows(true);

		ToolBar toolBar = getBoolBar(treeStore);
		toolBar.ensureDebugId("59zheyd5-eeeo-grx0-38vb-szfiijajnli8");
		toolBar.setBorders(true);

		setHeadingHtml(UIContext.Constants.virtualConversionNavigatorName());
		ensureDebugId("i6s6fj1k-46j1-t745-7lu3-6ka65zjdz5p5");
		setButtonAlign(HorizontalAlignment.CENTER);
		setLayout(new RowLayout());
		setHeight(550);
		setWidth(200);
		tree.setWidth(245);
//		setStyleAttribute("padding-top", "2px");
		setTopComponent(toolBar);
		RowData data = new RowData();
		data.setHeight(1);
		add(tree, data);
//		ContentPanel buttonPanel = getButtonPanel();
//		setBottomComponent(buttonPanel);
		
		NavigationBorderLayout layout = ColdStandbyManager.getInstance().getColdstandbyTabPanel().getLayout();
		
		final Listener<BaseEvent> beforeResizeListener = new Listener<BaseEvent>() {
		      public void handleEvent(BaseEvent e) {
		    	  ColdStandbyManager.getInstance().getHomepage().removeNonResizablePanels();
		      }
		};
		
		final Listener<BaseEvent> afterResizeListener = new Listener<BaseEvent>() {
		      public void handleEvent(BaseEvent e) {
		    	  if(e instanceof BorderLayoutEvent
		    			  && ((BorderLayoutEvent)e).getPanel() == VirtualConversionServerNavigator.this) {
		    		  ColdStandbyManager.getInstance().getHomepage().recreateNonResizablePanels();
		    	  }
		      }
		};
		
		layout.addListener(Events.BeforeExpand, beforeResizeListener);
		layout.addListener(Events.Expand, afterResizeListener);
		
		layout.addListener(Events.BeforeCollapse, beforeResizeListener);
		layout.addListener(Events.Collapse, afterResizeListener);
		
		ColdStandbyManager.getInstance().setVCNavigator(this);
		
		timer = new Timer() {
			public void run() {
				refreshServerList();
			}			
		};

	}

	protected void onServerSelectionChanged(ModelData selectedModel, final NODE_TYPE nodeType) {
		Boolean isGroup = selectedModel.get(IS_GROUP);
		final ARCFlashNode node = selectedModel.get(NODE);
		
		if(node == null || isGroup != null && isGroup) {
			return;
		}
		
		if(selectedNode != null 
				&& isEqual(node.getHostProtocol(), selectedNode.getHostProtocol())
				&& isEqual(node.getHostname(), selectedNode.getHostname()) 
				&& isEqual(node.getHostport(), selectedNode.getHostport())
				&& isEqual(node.getUuid(), selectedNode.getUuid()) && nodeType == NODE_TYPE.NEW_NODE)
			return;
		
		//select the monitor itself
		if(selectedNode == null && node.isMonitor())
			return;
		
		selectedNode = node;
		
		lockNavigator(node, nodeType);
		
		ColdStandbyManager.getInstance().getHomepage().getSummaryPanel().setMoniteeConnectedAfterSelectNode(false);
		ColdStandbyManager.getInstance().fireServerSelectionChangedEvent(node, nodeType);
	}

	private boolean isEqual(String newProtocol, String oldProtocol) {
		return newProtocol == null 	? oldProtocol == null : newProtocol.equals(oldProtocol);
	}
	
	public void lockNavigator(ARCFlashNode node, NODE_TYPE nodeType) {
		lockCounter = 4;
		if(NODE_TYPE.STATUS_CHANGE == nodeType)
			//"The status of server " + node.getHostname() + " changes. Reloading"
			mask(UIContext.Messages.moniteeStatusChange(node.getHostname()));
		else
			//"Switching to Server:" + node.getHostname()
			mask(UIContext.Messages.moniteeSwitching(node.getHostname()));
	}
	
	public void unlockNavigator() {
		if(--lockCounter == 0)
			unmask();
	}

	public void unlockNavigator(int count) {
		if((lockCounter -= count) == 0)
			unmask();
	}
	
	private void getColumnModel() {
		ColumnConfig name = new ColumnConfig(SERVER_NAME, UIContext.Constants.provistionPointColumnName(), NAME_SIZE);
		name.setRenderer(new WidgetTreeGridCellRenderer<ModelData>() {
			@Override
			public Widget getWidget(ModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid) {
				LayoutContainer lc = new LayoutContainer();
				TableLayout layout = new TableLayout();
				layout.setColumns(2);
				lc.setLayout(layout);
				
				Boolean isGroup = model.get(IS_GROUP);
				if(isGroup != null && isGroup){
					Image groupImage = null;
					
					LabelField label = new LabelField();
					if(model == actionRequired) {
						groupImage = AbstractImagePrototype.create(UIContext.IconBundle.vcm_group_actionrequired()).createImage();
						label.setStyleName("vc_navigator_actionrequired_group");
					}
					else {
						if(model == vmRunning) 
							groupImage = AbstractImagePrototype.create(UIContext.IconBundle.vcm_group_vmrunning()).createImage();
						else if(model == serverRunning)
							groupImage = AbstractImagePrototype.create(UIContext.IconBundle.vcm_group_sourcerunning()).createImage();
						else 
							groupImage = AbstractImagePrototype.create(UIContext.IconBundle.vcm_group_all()).createImage();
						
						label.setStyleName("vc_navigator_group");
					}
					
					lc.add(groupImage);
					
					String groupName = getGroupName(model);
					label.setValue(groupName);
					model.set(NAME_LABEL, label);
					
					TableData data = new TableData();
					data.setStyle("padding-left: 2px");
					lc.add(label, data);
				}
				else{
					Integer status = (Integer)model.get(STATUS);
					Image healthImage = null;
					
					ARCFlashNode node = model.get(NODE);
					//To indicate this is a vSphere managed vm
					if(node.isVSphereManagedVM()) {
						if(status == null || status == 0)
							healthImage = AbstractImagePrototype.create(UIContext.IconBundle.vcm_vsphere_vm()).createImage();
						else
							healthImage = AbstractImagePrototype.create(UIContext.IconBundle.vcm_machine_down()).createImage();
					}
					else {
						if(status == null || status == 0) {
							if(node.isLastUpdateFromVM()) {
								// Use some other image to indicate this is a failovered vm(its source machine is vcm monitee) 
								// with vcm installed
								healthImage = AbstractImagePrototype.create(UIContext.IconBundle.vcm_failovered_vm()).createImage();
							}
							else 
								healthImage = AbstractImagePrototype.create(UIContext.IconBundle.vcm_physical_running()).createImage();
						}
						else
							healthImage = AbstractImagePrototype.create(UIContext.IconBundle.vcm_machine_down()).createImage();
						
					}
					lc.add(healthImage);
					
					
					LabelField label = new LabelField();
					label.setStyleName("vc_navigator_servers");
					label.setValue((String)model.get(SERVER_NAME));
					
					TableData data = new TableData();
					data.setStyle("padding-left: 2px");
//					lc.setBorders(true);
					lc.add(label, data);
				}
				
				return lc;
			}
		});
		
		name.setSortable(false);
		name.setMenuDisabled(true);
//		name.setStyle("border: 0px;");
		
			GridCellRenderer<ModelData> renderer = new GridCellRenderer<ModelData>() {
				public Object render(final ModelData model, String property,
						ColumnData config, final int rowIndex, final int colIndex,
						ListStore<ModelData> store, Grid<ModelData> grid) {
					if (model.get(MENU_SHOW) == null
							|| (Boolean) model.get(MENU_SHOW) == false) {
						return "";
					} else {
						final LabelField label = getPopupButton();
//						label.setBorders(true);
						
						return label;
					}
				}
			};
			ColumnConfig menuColumn = new ColumnConfig("menu", "", MENU_SIZE);
			menuColumn.setSortable(false);
			menuColumn.setMenuDisabled(true);
			menuColumn.setRenderer(renderer);
//			menuColumn.setHidden(false);
		
		columnModel = new ColumnModel(Arrays.asList(name, menuColumn));
	}
	
	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		initServerListGroup();
		refreshServerList();
		timer.scheduleRepeating(REFRESH_INTERVAL);
	}
	
	private void initServerListGroup() {
		monitor.set(SERVER_NAME, "Localhost");
		monitor.set(IS_GROUP, true);
		
		actionRequired.set(SERVER_NAME, UIContext.Constants.moniteeGroupActionRequired());
		actionRequired.set(IS_GROUP, true);
		
		all.set(SERVER_NAME, UIContext.Constants.moniteeGroupAll());
		all.set(IS_GROUP, true);
		
		serverRunning.set(SERVER_NAME, UIContext.Constants.moniteeGroupServerRunning());
		serverRunning.set(IS_GROUP, true);
		
		vmRunning.set(SERVER_NAME, UIContext.Constants.moniteeGroupVMRunning());
		vmRunning.set(IS_GROUP, true);
		
//		bothDown.set(SERVER_NAME, UIContext.Constants.moniteeGroupBothDown());
//		bothDown.set(IS_GROUP, true);
		
//		treeStore.add(monitor, true);
//		treeStore.add(actionRequired, true);
//		treeStore.add(all, true);
//		treeStore.add(serverRunning, true);
//		treeStore.add(vmRunning, true);
	}

	private void refreshServerList() {
		//log in vsphere, do not show vcm monitor tree.
		if(UIContext.backupVM != null)
			return;
		
		service.queryFlashNodesSummary(new BaseAsyncCallback<ARCFlashNodesSummary>(){

			@Override
			public void onFailure(Throwable caught) {
				removeAllServers();
			}

			private void removeAllServers() {
				if (treeStore!=null)
					treeStore.removeAll();
			}

			@Override
			public void onSuccess(ARCFlashNodesSummary result) {
				nodesSummary = result;
				
				if(result == null || result.getNodes() == null || result.getNodes().length == 0) {
					
					selectedNode = null;
					
					removeAllServers();
				}
				else{
					int moniteeCount = 0;
					HashMap<String, String> newUuidSet = new HashMap<String, String>();
					for (ARCFlashNode node : result.getNodes()){
//						BeanModel model = factory.createModel(node);
						
						//if it's the monitor itself, add it to Monitor tree Node
						if(node.isMonitor()) {
							if(treeStore.getChildCount(monitor) <= 0) {
								BaseTreeModel treeModel = getTreeModel(node.getHostname(), 0, node);
								addServerToTree(monitor, treeModel);
//								treeStore.add(monitor, treeModel, true);
							}
						} else if (node.isRemoteNode()) {
							addOrUpdateNode(node, 0, all);
						} else {
							boolean health = getServerStatus(node);
							
							int status = 0;
	//						if(node.isVSphereManagedVM())
	//							addOrUpdateNode(node, status, all);
	//						else 
							if(health && !node.isLastUpdateFromVM()) {
								addOrUpdateNode(node, status, serverRunning);
							}
							else if(health && node.isLastUpdateFromVM()) {
								addOrUpdateNode(node, status, vmRunning);
							}
							else {
								status = 1;
								addOrUpdateNode(node, status, actionRequired);
							}
						}
						
						newUuidSet.put(node.getUuid(), node.getUuid());
						moniteeCount++;
						
						if (selectedNode!=null && node.getUuid().equals(selectedNode.getUuid()) ){
							selectedNode = node;
						}
					}
					
					List<String> list = new ArrayList<String>();
					for(String key : uuidToAllTreeModel.keySet()) {
						if(!newUuidSet.keySet().contains(key)) {
							
							BaseTreeModel oldAllModel = uuidToAllTreeModel.get(key);
							removeNodeFromTree(oldAllModel);
							list.add(key);
							
							if(uuidToTreeModel.containsKey(key)) {
								BaseTreeModel oldModel = uuidToTreeModel.get(key);
								removeNodeFromTree(oldModel);
								uuidToTreeModel.remove(key);
							}
						}
					}
					
					for(String key : list) {
						uuidToAllTreeModel.remove(key);
					}
					
					if(moniteeCount == 0) {
						if(showup) {
							ColdStandbyManager.getInstance().getHomepage().removeNonResizablePanels();
							ColdStandbyManager.getInstance().getColdstandbyTabPanel().getLayout().hide(LayoutRegion.WEST);
							showup = false;
							ColdStandbyManager.getInstance().getHomepage().recreateNonResizablePanels();
						}
					}
					else {
						if(!showup) {
							ColdStandbyManager.getInstance().getHomepage().removeNonResizablePanels();
							ColdStandbyManager.getInstance().getColdstandbyTabPanel().getLayout().show(LayoutRegion.WEST);
							showup = true;
							ColdStandbyManager.getInstance().getHomepage().recreateNonResizablePanels();
						}
						
						openActionRequirOrAllNodeFirstTimeLoad();
					}
				}
			}

			private void openActionRequirOrAllNodeFirstTimeLoad() {
				//only after the tree view is ready can the tree.setExpanded() work.
				if(firstTimeLoad && tree.isViewReady()) {
					openActionRequirOrAllNode();
					
					firstTimeLoad = false;
				}

			}
			
			private void addOrUpdateNode(ARCFlashNode node,
					int status, BaseTreeModel parentGroup) {
				String hostname = node.getHostname();
				
				if(!uuidToTreeModel.containsKey(node.getUuid()) && !uuidToAllTreeModel.containsKey(node.getUuid())) {
					if(parentGroup != all) {
						BaseTreeModel treeModel = getTreeModel(hostname,
								status, node);
						treeModel.set(GROURP, parentGroup);
						uuidToTreeModel.put(node.getUuid(), treeModel);
						addServerToTree(parentGroup, treeModel);
					}
					
					BaseTreeModel serverModel = getTreeModel(hostname, status, node);
					serverModel.set(GROURP, all);
					uuidToAllTreeModel.put(node.getUuid(), serverModel);
					addServerToTree(all, serverModel);
				}
				else {
					boolean isStatusChanged = false;
					BaseTreeModel changedTreeModel = null;
					if(uuidToTreeModel.containsKey(node.getUuid()))
					{
						BaseTreeModel oldModel = uuidToTreeModel.get(node.getUuid());
						ARCFlashNode oldNode = oldModel.get(NODE);
						oldModel.set(NODE, node);
						
						if(status == (Integer)oldModel.get(STATUS)) {
							if(!hostname.equals(oldNode.getHostname())) {
								oldModel.set(SERVER_NAME, node.getHostname());
								treeStore.update(oldModel);
							}
						}
						else {
							BaseTreeModel treeModel = getTreeModel(hostname,
									status, node);
							treeModel.set(GROURP, parentGroup);
							uuidToTreeModel.put(node.getUuid(), treeModel);
							
							removeNodeFromTree(oldModel);
								
							addServerToTree(parentGroup, treeModel);
							
							isStatusChanged = true;
							changedTreeModel = treeModel;
						}
					}
					
					if(uuidToAllTreeModel.containsKey(node.getUuid())) {
						BaseTreeModel oldAllModel = uuidToAllTreeModel.get(node.getUuid());
						oldAllModel.set(NODE, node);
						
						oldAllModel.set(STATUS, status);
						oldAllModel.set(SERVER_NAME, node.getHostname());
						treeStore.update(oldAllModel);
						
						if(!isStatusChanged && status != (Integer)oldAllModel.get(STATUS)) {
							isStatusChanged = true;
							changedTreeModel = oldAllModel;
						}
					}
					
					if(isStatusChanged) {
						String uuid = node.getUuid();
						if(selectedNode != null && uuid != null && uuid.equals(selectedNode.getUuid()))
							onServerSelectionChanged(changedTreeModel, NODE_TYPE.STATUS_CHANGE);
					}
				}
			}

			private void addServerToTree(BaseTreeModel parentGroup,
					BaseTreeModel treeModel) {
				if(treeStore.contains(parentGroup)) {
					int total = treeStore.getChildCount(parentGroup);
					int index = 0;
					try {
						String serName = (String)treeModel.get(SERVER_NAME);
						for(index = 0; index < total; index++) {
							ModelData child = treeStore.getChild(parentGroup, index);
							String name = child.get(SERVER_NAME);
							if(name.compareTo(serName) > 0)
								break;
						}
					}
					catch(Exception e) {
						index = total;
					}
					treeStore.insert(parentGroup, treeModel, index, true);
					LabelField label = parentGroup.get(NAME_LABEL);
					//Do not change the group label text in case the group has not been rendered on the tree. 
					if(label != null)
						label.setValue(getGroupName(parentGroup));
				}
				else {
					parentGroup.add(treeModel);
					
					//the following fragment to ensure the order of group on the tree to be 
					//actionRequired, all, serverRunning, vmRunning
					if(parentGroup == vmRunning)
						treeStore.add(parentGroup, true);
					else {
						int index = 0;
						
						if(parentGroup == all && treeStore.contains(actionRequired)) 
							index = 1;
						else if(parentGroup == serverRunning) {
							if(treeStore.contains(actionRequired)) //in this case, "all" is also on the tree
								index = 2;
							else if(treeStore.contains(all))
								index = 1;
						}
						
						treeStore.insert(parentGroup, index, true);
					}
						
				}
			}
			
			private BaseTreeModel getTreeModel(String hostname, int status, ARCFlashNode node) {
				BaseTreeModel treeModel = new BaseTreeModel();
				treeModel.set(SERVER_NAME, hostname);
				treeModel.set(STATUS, status);
				treeModel.set(NODE, node);
				treeModel.set(MENU_SHOW, true);
				return treeModel;
			}
		}
		);

	}

	private void openActionRequirOrAllNode() {
//		System.out.println("select first");
		if(treeStore.getChildCount(actionRequired) > 0) {
//			System.out.println("actionRequired first:" + treeStore.getChildCount(actionRequired));
			tree.setExpanded(actionRequired, true);
			selectTheFirstNode(actionRequired);
		}
		else if(treeStore.getChildCount(monitor) > 0) {
			tree.setExpanded(monitor, true);
			selectTheFirstNode(monitor);
		}
		else if(treeStore.getChildCount(all) > 0) {
//			System.out.println("all first: " + treeStore.getChildCount(all));
			tree.setExpanded(all, true);
			selectTheFirstNode(all);
		}
	}

	private void selectTheFirstNode(BaseTreeModel parentGroup) {
		
		ModelData child = treeStore.getChild(parentGroup, 0);
		if(child != null) {
//			System.out.println("parentGroup.getChild(0):" + child.get(SERVER_NAME) + ":" + child.get(STATUS));
			tree.getSelectionModel().select(false, child);				
		}
//		else
//			System.out.println("parentGroup.getChild(0): null");
	}
	
//	private ContentPanel getButtonPanel() {
//		TableLayout tablelayout = new TableLayout();
//		tablelayout.setColumns(3);
//		tablelayout.setWidth("100%");
//		TableData tbData = new TableData();
//		tbData.setHeight("25px");
//		tbData.setVerticalAlign(VerticalAlignment.MIDDLE);
//		tbData.setPadding(5);
//		tbData.setWidth("33%");
//		tbData.setHorizontalAlign(HorizontalAlignment.CENTER);
//		    
//		ContentPanel buttonPanel = new ContentPanel();
//		buttonPanel.setHeaderVisible(false);
//		buttonPanel.setButtonAlign(HorizontalAlignment.CENTER);
//		buttonPanel.setLayout(tablelayout);
//		Button addButton = new Button();
//		addButton.setText("Add");
//		addButton.setWidth(50);
//		addButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
//
//			@Override
//			public void componentSelected(ButtonEvent ce) {
////						AddVirtualCenterWindow window = new AddVirtualCenterWindow();
////						window.setModal(true);
////						window.show();
//			}
//			
//		});
//		Button modifyButton = new Button();
//		modifyButton.setText("Modify");
//		modifyButton.setWidth(50);
//		modifyButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
//
//			@Override
//			public void componentSelected(ButtonEvent ce) {
////						AddVirtualCenterWindow window = new AddVirtualCenterWindow();
////						window.setDefaultValue((VirtualCenterNodeModel)tree.getSelectionModel().getSelectedItem());
////						window.setModal(true);
////						window.show();
//			}
//			
//		});
//		Button deleteButton = new Button();
//		deleteButton.setText("Delete");
//		deleteButton.setWidth(50);
//		deleteButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
//
//			@Override
//			public void componentSelected(ButtonEvent ce) {
////						AddVirtualCenterWindow window = new AddVirtualCenterWindow();
////						window.setModal(true);
////						window.show();
//			}
//			
//		});
//		buttonPanel.add(addButton,tbData);
//		buttonPanel.add(modifyButton,tbData);
//		buttonPanel.add(deleteButton,tbData);
//		return buttonPanel;
//	}

	private ToolBar getBoolBar(TreeStore<ModelData> treeStore) {
		StoreFilterField<ModelData> filter = new StoreFilterField<ModelData>() {   

			@Override  
			protected boolean doSelect(Store<ModelData> store, ModelData parent,   
					ModelData record, String property, String filter) {   
					
					Boolean isGroup = record.get(IS_GROUP);
					if(isGroup != null && isGroup)
						return true;
						
					// only match leaf nodes   
					String name = (String)record.get(SERVER_NAME);
					if(name != null && name.indexOf(filter) >= 0)
						return true;
					
					return false;   
				}   
		};  
//		filter.setWidth(150);
		filter.setEmptyText(UIContext.Constants.vcmNavigatorFilterEnterServer());
		filter.bind(treeStore);  
		filter.ensureDebugId("x8x7j75l-imfm-jlhf-jo6t-e5y6e5gzumxx");
		filter.setBorders(true);
		
		ToolBar toolBar = new ToolBar();   
		toolBar.setWidth(200);
		TableLayout layout = new TableLayout();
		layout.setWidth("100%");
		toolBar.setLayoutData(layout);
	    toolBar.setBorders(true);   
	    LabelToolItem item = new LabelToolItem(UIContext.Constants.vcmNavigatorFilter());
	    item.ensureDebugId("jky71q76-bgqw-18oc-vdp2-cjsgmu9b1skh");
	    TableData data = new TableData();
	    data.setWidth("30%");
	    toolBar.add(new FillToolItem());
		toolBar.add(item);   
	    toolBar.add(filter);
	    toolBar.ensureDebugId("wf13904x-oy0q-grn5-cb9f-2y2m92th1p5r");
//	    toolBar.setBorders(true);
	    
//		LayoutContainer con = new LayoutContainer();
//		TableLayout layout = new TableLayout();
//		layout.setWidth("100%");
//		con.setLayout(layout);
////		toolBar.setLayoutData(layout);
////	    toolBar.setBorders(true);   
//		LabelField item = new LabelField(UIContext.Constants.vcmNavigatorFilter());
//		item.setStyleName("vc_navigator_servers");
//	    item.ensureDebugId("jky71q76-bgqw-18oc-vdp2-cjsgmu9b1skh");
//	    TableData data = new TableData();
//	    con.add(item, data);
//	    data = new TableData();
//	    data.setHorizontalAlign(HorizontalAlignment.RIGHT);
//	    con.add(filter, data);
		return toolBar;
	}
	private void setButtonId(MessageBox messageBox, String buttonId, String id) {
		Button button = messageBox.getDialog().getButtonById(buttonId);
		if(button != null) {
			button.ensureDebugId(id);
		}
	}
	
	private Menu getMenu() {
		menu = new Menu();
		menu.ensureDebugId("18e35e2b-42be-4d4c-9a09-80f338830483");
	    MenuItem removeItem = new MenuItem(UIContext.Constants.moniteeRemovalButtonName());  
	    removeItem.ensureDebugId("00bc6312-2a99-4bce-b690-57b93a1dbb25");
	    removeItem.addSelectionListener(new SelectionListener<MenuEvent>(){

			@Override
	  		public void componentSelected(MenuEvent ce) {
	  			MessageBox box = new MessageBox();
//	  			box.setIcon(MessageBox.WARNING);
	  			box.setIcon("ext-mb-warning-for-monitor");
	  			box.setButtons(MessageBox.YESNO);
	  			box.setTitleHtml(UIContext.Constants.moniteeRemovalButtonName());
	  			box.setMessage(UIContext.Constants.moniteeRemovalFromMonitor());
	  			box.getDialog().setWidth(400);
	  			Utils.setMessageBoxDebugId(box);
	  			box.show();
	  			
	  			box.addCallback(new Listener<MessageBoxEvent>() {

					@Override
					public void handleEvent(MessageBoxEvent be) {
						final ARCFlashNode node = getSelectServerNode();
						if(node != null && be.getButtonClicked().getItemId().equals(Dialog.YES)) {
//							MessageBox.info("Removal", "Begin to remove", null);
							service.removeMonitee(node.getUuid(), new BaseAsyncCallback<Void>(UIContext.productNameVCM) {
								@Override
								public void onSuccess(Void result) {
									
//									if(uuidToTreeModel.containsKey(node.getUuid())) {
////										BaseTreeModel oldModel = uuidToTreeModel.get(node.getUuid());
////										removeNodeFromTree(oldModel);
//										uuidToTreeModel.remove(node.getUuid());
//										
//									}
//									
//									if(uuidToAllTreeModel.containsKey(node.getUuid())) {
////										BaseTreeModel oldAllModel = uuidToAllTreeModel.get(node.getUuid());
////										removeNodeFromTree(oldAllModel);
//										uuidToAllTreeModel.remove(node.getUuid());
//									}
									firstTimeLoad = true;
									refreshServerList();
									
//									openActionRequirOrAllNode();
								}
								
							});
						}
					}
				});
	  		}
	      	
	    });
	    menu.add(removeItem);   
		   
		return menu;
	}

	public ARCFlashNode getSelectServerNode() {
		return selectedNode;
	}
	
	public boolean isSelectMoniteeFromMonitor() {
		if(selectedNode != null && !selectedNode.isMonitor())
			return true;
		else
			return false;
	}
	
	public boolean isSelectedServerAccessible() {
//		System.out.println(selectedNode != null ? ("current server state:" + Integer.toHexString(selectedNode.getState())) : "current node is null");
//		if(selectedNode != null && (selectedNode.getState() & HeartBeatJobScript.STATE_ACTIVE) == 0) {
		if (selectedNode != null && selectedNode.isMonitor())
			return true;
		if(selectedNode != null && !getServerStatus(selectedNode)) {
//			System.out.println("Not accessible");
			return false;
		}
		
//		System.out.println("Accessible");
		return true;
	}
	
	public boolean isSelectedVShpereManagedVM() {
		return selectedNode != null && selectedNode.isVSphereManagedVM();
	}
	
	public boolean isSelectedRemote() {
		return selectedNode != null && selectedNode.isRemoteNode();
	}
	/**
	 * Returns if the selected machine is physical.
	 * @return
	 */
	public boolean isSelectedServerPhysical() {
//		System.out.println(selectedNode != null ? ("current server state:" + Integer.toHexString(selectedNode.getState())) : "current node is null");
//		if(selectedNode != null && (selectedNode.getState() & HeartBeatJobScript.STATE_ACTIVE) == 0) {
		if(selectedNode != null && selectedNode.isLastUpdateFromVM()) {
//			System.out.println("Not accessible");
			return false;
		}
		
//		System.out.println("Accessible");
		return true;
	}

	private boolean getServerStatus(ARCFlashNode node) {
		long value = nodesSummary.getServerTime() - node.getLastUpdate(); 
		boolean health = value<(node.getHeartBeatFailoverTimeoutInSecond()*1000) || node.isPaused();
		return health;
	}
	
	private String getGroupName(ModelData model) {
		int size = treeStore.getChildCount(model);
		String groupName = (String)model.get(SERVER_NAME) + " (" + size + ")";
		return groupName;
	}
	
	private LabelField getPopupButton() {
		final LabelField label = new LabelField();
		label.setValue("&nbsp;");
		label.setStyleName("vc_navigator_servers");
		label.addListener(Events.OnClick,
				new Listener<BoxComponentEvent>() {

					@Override
					public void handleEvent(BoxComponentEvent be) {
						if (menu != null) {
							menu.showAt(be.getClientX(), be
									.getClientY());
						}
					}

				});
		label.sinkEvents(Events.OnClick.getEventCode());
		
		label.addListener(Events.OnMouseOver, new Listener<BaseEvent>(){

			@Override
			public void handleEvent(BaseEvent be) {
				label.setValue(">>");
//				System.out.println("OnMouseOver");
			}
		}
		);
		
		label.sinkEvents(Events.OnMouseOver.getEventCode());
		
		label.addListener(Events.OnMouseOut, new Listener<BaseEvent>(){

			@Override
			public void handleEvent(BaseEvent be) {
				label.setValue("&nbsp;");
//				System.out.println("OnMouseOut");
			}
		}
		);
		label.sinkEvents(Events.OnMouseOut.getEventCode());
		return label;
	}

	private void removeNodeFromTree(BaseTreeModel oldModel) {
		TreeModel parentModel = oldModel.get(GROURP);
		treeStore.remove(oldModel);
		parentModel.remove(oldModel);
		if(treeStore.getChildCount(parentModel) <= 0)
			treeStore.remove(parentModel);
		else
			treeStore.update(parentModel);
	}

	public static enum NODE_TYPE{
		
		NEW_NODE, STATUS_CHANGE
	}
}
