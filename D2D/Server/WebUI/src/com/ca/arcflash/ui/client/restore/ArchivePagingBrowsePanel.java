package com.ca.arcflash.ui.client.restore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.FlashCheckBox;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.ArchiveFileVersionNode;
import com.ca.arcflash.ui.client.model.ArchiveGridTreeNode;
import com.ca.arcflash.ui.client.model.CatalogModelType;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.Loader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;

public class ArchivePagingBrowsePanel extends LayoutContainer {

	private static final int PAGESIZE = PagingContext.DEFAULTPAGESIZE;
	private LoginServiceAsync service = GWT.create(LoginService.class);
	private ArchivePagingContext pContext;
	private LabelField descLabel = new LabelField();
	private ArchivePagingBrowsePanel thisPanel;
	private int[] pages = null;
	private int currentPage = 0;
	private HashMap<ArchiveGridTreeNode, FlashCheckBox> allOpenedMap;
	private ListStore<ArchiveGridTreeNode> store;

	//right panel
	private Grid<ArchiveFileVersionNode> archiveFileVersionsGrid;
	private ListStore<ArchiveFileVersionNode> archiveFileVersionsStore;
	private ColumnModel archiveFileVersionsColumnsModel;

	private GridCellRenderer<ArchiveFileVersionNode> FileVersionRenderer;
	private GridCellRenderer<ArchiveFileVersionNode> DateRenderer;
	private GridCellRenderer<ArchiveFileVersionNode> sizeRenderer;

	private HashMap<ArchiveFileVersionNode, FlashCheckBox> fileVersionsTable = new HashMap<ArchiveFileVersionNode, FlashCheckBox>();

	public static ArchiveGridTreeNode CurrentSelectedNode;

	private Listener<BaseEvent> restoreArchiveBrowsePanelListener = null;

	private VerticalPanel panel;

	private PagingToolBar pagingBar;
	
	private Grid<ArchiveGridTreeNode> grid;

	public ArchivePagingBrowsePanel(ArchivePagingContext pageContext) {
		pContext = pageContext;
		if (pContext != null) {
			allOpenedMap = pContext.getChildrenStateMap();
			int num = pContext.calcPages(PAGESIZE);
			if (num >= 0) {
				pages = new int[num];
			}
		}
		thisPanel = this;

	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		setStyleAttribute("margin", "4px");
		RowLayout r1 = new RowLayout();
		this.setLayout(r1);
		this.setHeight("100%");
		
		
		defineRestoreArchiveBrowsePanelListener();
		
		renderPagingPanel();

		renderArchiveFileVersionsPanel();

		descLabel.setValue(UIContext.Constants.browsePagingDesc());
		descLabel.setStyleAttribute("margin-bottom", "4px");
		descLabel.setStyleAttribute("margin-right", "4px");
		this.add(descLabel);

		final LayoutContainer archiveBrowseContainer = new LayoutContainer();
		archiveBrowseContainer.setLayout(new RowLayout());
		archiveBrowseContainer.setSize(750, 350);
		
		final HorizontalPanel horPanel = new HorizontalPanel();
		horPanel.setSize(710, 350);
		horPanel.add(panel);
		horPanel.add(archiveFileVersionsGrid);
		horPanel.setSpacing(3);
		
		archiveBrowseContainer.add(horPanel);
		this.add(archiveBrowseContainer);
		this.add(pagingBar);
	}

	private void renderPagingPanel()
	{

		//proxy 
		RpcProxy<PagingLoadResult<ArchiveGridTreeNode>> proxy = new RpcProxy<PagingLoadResult<ArchiveGridTreeNode>>() {
			@Override
			public void load(Object loadConfig,
					AsyncCallback<PagingLoadResult<ArchiveGridTreeNode>> callback) {
				final PagingLoadConfig plc = (PagingLoadConfig) loadConfig;
				final AsyncCallback<PagingLoadResult<ArchiveGridTreeNode>> fcb = callback;
				AsyncCallback<PagingLoadResult<ArchiveGridTreeNode>> cb = new AsyncCallback<PagingLoadResult<ArchiveGridTreeNode>>() {

					@Override
					public void onFailure(Throwable caught) {
						fcb.onFailure(caught);
					}

					@Override
					public void onSuccess(PagingLoadResult<ArchiveGridTreeNode> result) {
						currentPage = plc.getOffset() / PAGESIZE;
						if (pages.length > currentPage) {
							pages[currentPage]++;
						}
						fcb.onSuccess(result);
					}
				};

				service.getArchivePagingGridTreeNode(UIContext.getCurrentArchiveDestination(),pContext.getParent(), plc, cb); 
			}
		};

		// loader
		final PagingLoader<PagingLoadResult<ModelData>> loader = new BasePagingLoader<PagingLoadResult<ModelData>>(
				proxy);
		loader.setRemoteSort(true);
		loader.addListener(Loader.BeforeLoad, new Listener<LoadEvent>(){

			@Override
			public void handleEvent(LoadEvent be) {
				archiveFileVersionsStore.removeAll();
				thisPanel.mask(UIContext.Constants.loadingIndicatorText());
			}
			});
		
		loader.addListener(Loader.Load, new Listener<LoadEvent>(){

			@Override
			public void handleEvent(LoadEvent be) {
				thisPanel.unmask();
			}
			});
		
		store = new ListStore<ArchiveGridTreeNode>(loader);
		ColumnModel cm = this.createColumnModel();
		
		grid = new Grid<ArchiveGridTreeNode>(store, cm);
		grid.addListener(Events.Attach,
				new Listener<GridEvent<ArchiveGridTreeNode>>() {
					public void handleEvent(GridEvent<ArchiveGridTreeNode> be) {
						loader.load(0, PAGESIZE);
					}
				});

		// add doublic click listener to grid
		grid.addListener(Events.CellDoubleClick,
				new Listener<GridEvent<ArchiveGridTreeNode>>() {

			@Override
			public void handleEvent(GridEvent<ArchiveGridTreeNode> be) {
				final ArchiveGridTreeNode clickedTreeNode = be.getModel();
				FlashCheckBox clickedNodeCheckBox = allOpenedMap
				.get(clickedTreeNode);

				ArchivePagingContext.handleClick(clickedTreeNode, pContext
						.getChildrenContextMap(), thisPanel,
						clickedNodeCheckBox, false, pContext
						.isRestoreManager(), null);
			}
		});

		Listener<GridEvent<ArchiveGridTreeNode>> listListener = new Listener<GridEvent<ArchiveGridTreeNode>>() {

			@Override
			public void handleEvent(GridEvent<ArchiveGridTreeNode> be) {
				ArchiveGridTreeNode treeNodeTemp = be.getModel();				
				handleNodeClick(treeNodeTemp);
			}

		};

		grid.addListener(Events.RowClick, listListener);
		grid.setHeight(340);
		grid.setWidth(400);
		grid.setLoadMask(false);
		grid.setBorders(true);
		grid.setAutoExpandColumn("displayName");
		grid.getView().setAutoFill(true);

		panel = new VerticalPanel();
		//		panel.setHeaderVisible(false);
		panel.setLayout(new FlowLayout(4));
		panel.setHeight(340);
		panel.setWidth(400);
		//		panel.setCollapsible(false);
		panel.setScrollMode(Scroll.AUTOX);


		panel.add(grid);

		pagingBar = new PagingToolBar(PAGESIZE){	
			@Override
			protected void onRender(Element target, int index) {				
				super.onRender(target, index);
				this.pageText.setWidth("38px");
				this.pageText.addKeyPressHandler(new KeyPressHandler(){
					@Override
					public void onKeyPress(KeyPressEvent event) {
						char key = event.getCharCode();
						if(event.isControlKeyDown() || key == KeyCodes.KEY_ENTER || key == KeyCodes.KEY_BACKSPACE || key == KeyCodes.KEY_DELETE){
							return;
						}
						if(!Character.isDigit(key)){
							pageText.cancelKey();
						}
					}
					
				});
				first.ensureDebugId("CFD29215-460E-4235-A008-5AF7A96D4A9C");
				last.ensureDebugId("27D66DE5-9962-418b-81B1-03F3637CD1EE");
				prev.ensureDebugId("0FBCEF87-F800-4350-9BFB-C2B8E61C14AD");
				next.ensureDebugId("341BA83E-1817-41fe-AF79-6179B18B3EF7");
				refresh.ensureDebugId("4E1A0EC2-1B72-4d02-8DB2-8236C63D0CDF");
				pageText.ensureDebugId("0F0E2BA6-7894-4e45-8FDB-5163EE9C7814");
			}			
		};
		pagingBar.bind(loader);
		pagingBar.setSize(700,40);
	}






	private void openSubFolder(final ArchiveGridTreeNode clickedTreeNode) {
		if (clickedTreeNode == null)
			return;

		final FlashCheckBox clickedNodeCheckBox = allOpenedMap.get(clickedTreeNode);
		thisPanel.mask(UIContext.Constants.loadingIndicatorText());
		service.getArchiveTreeGridChildrenCount(UIContext.getCurrentArchiveDestination(),clickedTreeNode, new BaseAsyncCallback<Long>() {
			
			@Override
			public void onSuccess(Long result) {
				thisPanel.unmask();
				long childCount = result.longValue();
				clickedTreeNode.setChildrenCount(childCount);
			
				ArchivePagingContext.handleClick(clickedTreeNode, pContext
						.getChildrenContextMap(), thisPanel, clickedNodeCheckBox,
						false, pContext.isRestoreManager(), null);
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				thisPanel.unmask();
				super.onFailure(caught);
			}
		});
		

		
	}

	private ColumnModel createColumnModel() {
		ColumnConfig nameColumn = new ColumnConfig("displayName",
				UIContext.Constants.restoreNameColumn(), 295);
		nameColumn.setMenuDisabled(true);
		nameColumn.setStyle("vertical-align:middle;");
		nameColumn.setRenderer(new GridCellRenderer<ModelData>() {
			@Override
			public Object render(ModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid) {

				LayoutContainer lc = new LayoutContainer();
				TableLayout layout = new TableLayout();
				layout.setColumns(2);
				lc.setLayout(layout);

				final ArchiveGridTreeNode node = (ArchiveGridTreeNode) model;
				if (pContext.isRestoreManager()) {
					layout.setColumns(3);
					int defaultSelState = FlashCheckBox.NONE;
					if (pages[currentPage] == 1) {
						FlashCheckBox pcb = pContext.getParentCheckBox();
						if (pcb != null
								&& pcb.getSelectedState() == FlashCheckBox.FULL) {
							defaultSelState = FlashCheckBox.FULL;
						}
					}
					final FlashCheckBox fcb = new FlashCheckBox();
					if (allOpenedMap.containsKey(node)) {
						FlashCheckBox tfcb = allOpenedMap.get(node);
						fcb.setSelectedState(tfcb.getSelectedState());
					}
					allOpenedMap.put(node, fcb);

					if (defaultSelState == FlashCheckBox.FULL) {
						fcb.setSelectedState(FlashCheckBox.FULL);
					}

					final FlashCheckBox ffcb = fcb;
					fcb
					.addSelectionListener(new SelectionListener<IconButtonEvent>() {
						@Override
						public void componentSelected(IconButtonEvent ce) {
							if (ffcb.isEnabled() == false)
								return;
							if (pContext != null
									&& pContext.getChildrenContextMap() != null
									&& pContext.getChildrenContextMap()
									.get(node) != null) {
								pContext.getChildrenContextMap()
								.remove(node);
							}
							highlightRow(node);
							if(fcb.getSelectedState() == FlashCheckBox.FULL)
							{
								node.setChecked(true);
								if(node.getSelectedVersion() == -1)
								{
									node.setSelectedVersion(node.getVersionsCount());
									//treeNode.setSize(treeNod)
									ArchiveFileVersionNode[] versions = node.getfileVersionsList();
									
									if(versions != null)
										node.setSize(versions[node.getVersionsCount() - 1].getFileSize());
									
									if(CurrentSelectedNode == node)
									{
										ArchiveFileVersionNode versionNode = CurrentSelectedNode.getfileVersionsList()[node.getVersionsCount() - 1];
										SetSelectableStateofOtherVersions(versionNode, true);
									}
									else{
										CurrentSelectedNode = node;
										if(CurrentSelectedNode != null)
										{
											PopulateVersionsGrid(CurrentSelectedNode);
										}
									}
								}
							}
							else if(fcb.getSelectedState() == FlashCheckBox.NONE)
							{
								node.setChecked(false);
								if(CurrentSelectedNode == node)
								{
									if(CurrentSelectedNode.getType() == CatalogModelType.File)
									{
										int versionToUnselect  = 0;
										if(CurrentSelectedNode.getSelectedVersion()!=-1)
											versionToUnselect = CurrentSelectedNode.getSelectedVersion()-1;				
									     ArchiveFileVersionNode versionNode = node.getfileVersionsList()[versionToUnselect];
										//ArchiveFileVersionNode versionNode = node.getfileVersionsList()[CurrentSelectedNode.getSelectedVersion() - 1];
										CurrentSelectedNode.setSelectedVersion(-1);
										SetSelectableStateofOtherVersions(versionNode, false);
									}
								}
								else{
									CurrentSelectedNode = node;
									if(CurrentSelectedNode != null)
									{
										PopulateVersionsGrid(CurrentSelectedNode);
									}
								}
								node.setSelectedVersion(-1);
								enableAllVersionsifNoVersionIsSelected();
								node.setSize(0L);
							}
						}
					});

					lc.add(ffcb);
				}

				if (node.getType() == CatalogModelType.Folder) {
					IconButton image = new IconButton("popupFolder-icon");
					image.setWidth(20);
					folderMap.put(image, node);
					image
					.addSelectionListener(new SelectionListener<IconButtonEvent>() {

						@Override
						public void componentSelected(IconButtonEvent ce) {
							IconButton src = ce.getIconButton();
							
							ArchiveGridTreeNode node = folderMap.get(src);						
							handleNodeClick(node);
							if (node != null) {
								openSubFolder(node);
							}

						}
					});
					lc.add(image);
				} else if (node.getType() == CatalogModelType.File) {
					IconButton image = new IconButton("file-icon");
					image.setWidth(20);
					fileMap.put(image, node);
					image
					.addSelectionListener(new SelectionListener<IconButtonEvent>() {

						@Override
						public void componentSelected(IconButtonEvent ce) {
							IconButton src = ce.getIconButton();							
							ArchiveGridTreeNode node = fileMap.get(src);
							if (node != null) {
								handleNodeClick(node);
							}
							

						}
					});
					
					
					lc.add(image);
				}

				// ln.setText();
				if (node.getType() == CatalogModelType.Folder) {
					Hyperlink ln = new Hyperlink(node.getDisplayName(), false,
					"");
					ln.addClickHandler(folderClickHandler);
					ln.setStyleName("popupFolderText");
					linkMap.put(ln, node);
					lc.add(ln);
					ToolTipConfig tooltip=new ToolTipConfig(new Html("<pre style=\"word-wrap:break-word\">"+node.getDisplayName()+"</pre>").getHtml());
					tooltip.setMaxWidth(400);
					lc.setToolTip(tooltip);	
				} else {
					Label la = new Label();
					la.addClickHandler(nodeclickHandler);
					la.setStyleName("popupFileText");
					
					String strFileName = node.getDisplayName();
					
					if(node.getType() == CatalogModelType.File)
					{
						strFileName += "("+Integer.toString(node.getVersionsCount())+")";
					}
					
					la.setText(strFileName);
					fileLinkMap.put(la, node);
					lc.add(la);
					ToolTipConfig tooltip=new ToolTipConfig(new Html("<pre style=\"word-wrap:break-word\">"+strFileName+"</pre>").getHtml());
					tooltip.setMaxWidth(400);
					lc.setToolTip(tooltip);	
				}

				return lc;
			}

			ClickHandler folderClickHandler = new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Object src = event.getSource();
					if (src instanceof Hyperlink) {
						ArchiveGridTreeNode node = linkMap.get(src);
						handleNodeClick(node);
						if(node.getType() == CatalogModelType.Folder)
							openSubFolder(node);
					}
				}
			};
			
			ClickHandler nodeclickHandler = new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Object src = event.getSource();
					if (src instanceof Label) {
						ArchiveGridTreeNode node = fileLinkMap.get(src);
						if(node.getType() == CatalogModelType.File)
						{							
							handleNodeClick(node);
						}
					}
				}
			};

			HashMap<Hyperlink, ArchiveGridTreeNode> linkMap = new HashMap<Hyperlink, ArchiveGridTreeNode>();
			HashMap<IconButton, ArchiveGridTreeNode> folderMap = new HashMap<IconButton, ArchiveGridTreeNode>();
			HashMap<IconButton, ArchiveGridTreeNode> fileMap = new HashMap<IconButton, ArchiveGridTreeNode>();
			HashMap<Label, ArchiveGridTreeNode> fileLinkMap = new HashMap<Label, ArchiveGridTreeNode>();
		});

		List<ColumnConfig> lst = Arrays.asList(nameColumn);
		for(ColumnConfig cfg : lst)	{
			cfg.setSortable(false);
		}
		ColumnModel cm = new ColumnModel(lst);


		return cm;
	}
	
	@SuppressWarnings("unchecked")
	private void enableAllVersionsifNoVersionIsSelected()
	{
		if(CurrentSelectedNode.getSelectedVersion()==-1 )
		{	
			Iterator iterator = fileVersionsTable.entrySet().iterator();
			while(iterator.hasNext())
			{
				Map.Entry Temp  = (Map.Entry) iterator.next();
				FlashCheckBox fcbTemp = (FlashCheckBox)Temp.getValue();
				fcbTemp.setEnabled(true);
				fcbTemp.setSelectedState(FlashCheckBox.NONE);
			}	
		}		
	}
	
	
	private void handleNodeClick(ArchiveGridTreeNode node)
	{
		ArchiveGridTreeNode treeNodeTemp = node;
		highlightRow(node);
		if(treeNodeTemp != CurrentSelectedNode)
		{
			FlashCheckBox fcbTemp = allOpenedMap.get(treeNodeTemp);
			if((treeNodeTemp.getSelectedVersion() == -1) && (fcbTemp.getSelectedState() == FlashCheckBox.FULL))
			{
				treeNodeTemp.setSelectedVersion(treeNodeTemp.getVersionsCount());
				treeNodeTemp.setSize(treeNodeTemp.getfileVersionsList()[treeNodeTemp.getVersionsCount() - 1].getFileSize());
			}

			CurrentSelectedNode = treeNodeTemp;
			if(CurrentSelectedNode != null)
			{
				PopulateVersionsGrid(CurrentSelectedNode);
			}
		}
	}
	

	private void renderArchiveFileVersionsPanel() {
		archiveFileVersionsStore = new ListStore<ArchiveFileVersionNode>();

		FileVersionRenderer = new GridCellRenderer<ArchiveFileVersionNode>() {

			@Override
			public Object render(ArchiveFileVersionNode model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ArchiveFileVersionNode> store,
					Grid<ArchiveFileVersionNode> grid) {

				return model.getVersion();
			}
		};

		DateRenderer = new GridCellRenderer<ArchiveFileVersionNode>() {

			@Override
			public Object render(ArchiveFileVersionNode model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ArchiveFileVersionNode> store, Grid<ArchiveFileVersionNode> grid) {

				return Utils.formatDateToServerTime(model.getModifiedTime(),
						model.getModifiedTZOffset() != null ? model.getModifiedTZOffset():0);
				//return Utils.formatDate(dt);
			}
		};

		sizeRenderer = new GridCellRenderer<ArchiveFileVersionNode>() {

			@Override
			public Object render(ArchiveFileVersionNode model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ArchiveFileVersionNode> store, Grid<ArchiveFileVersionNode> grid) {
				//String strFileSize = "";
				//strFileSize = Long.toString(model.getFileSize()) + " " + UIContext.Constants.KB();
				return Utils.bytes2String(model.getFileSize());
			}
		};

		List<ColumnConfig> columnConfigs = new ArrayList<ColumnConfig>();

		CheckColumnConfig FileVersionscheckColumn = new CheckColumnConfig("checked", "", 40);
		FileVersionscheckColumn.setHidden(true);

//		ColumnConfig fileName = new ColumnConfig("FileName", UIContext.Constants.restoreNameColumn(), 180);
		ColumnConfig fileName = new ColumnConfig("Version", UIContext.Constants.restoreVersionColumn(), 60);
		fileName.setRenderer(new GridCellRenderer<ArchiveFileVersionNode>() {

			@Override
			public Object render(ArchiveFileVersionNode model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ArchiveFileVersionNode> store,
					Grid<ArchiveFileVersionNode> grid) {

				LayoutContainer lc = new LayoutContainer();

				TableLayout layout = new TableLayout();
				layout.setColumns(3);
				lc.setLayout(layout);
				final FlashCheckBox fcb = new FlashCheckBox();

				final ArchiveFileVersionNode FileVersionNode = model;

				if (FileVersionNode.getChecked() != null) {
					if (FileVersionNode.getChecked())
						fcb.setSelectedState(FlashCheckBox.FULL);
					else
						fcb.setSelectedState(FlashCheckBox.NONE);
				} else {						
					fcb.setSelectedState(FlashCheckBox.NONE);
				}

				fcb.addSelectionListener(new SelectionListener<IconButtonEvent>() {

					@Override
					public void componentSelected(IconButtonEvent ce) {

						if (fcb.isEnabled() == false)
							return;

						if(fcb.getSelectedState() == FlashCheckBox.FULL)
						{
							FileVersionNode.setChecked(true);
							CurrentSelectedNode.setSelectedVersion(FileVersionNode.getVersion());
							CurrentSelectedNode.setChecked(true);
							CurrentSelectedNode.setSize(FileVersionNode.getFileSize());							

							//table.get(CurrentSelectedNode).setSelectedState(FlashCheckBox.FULL);
						}
						else{
							FileVersionNode.setChecked(false);
							CurrentSelectedNode.setSelectedVersion(-1);
							CurrentSelectedNode.setChecked(false);
						}
						FlashCheckBox parentCheckBox = allOpenedMap.get(CurrentSelectedNode);
						parentCheckBox.setSelectedState(fcb.getSelectedState());

						SetSelectableStateofOtherVersions(FileVersionNode,FileVersionNode.getChecked());
						if(CurrentSelectedNode.getSelectedVersion()==-1)
						{	
							enableAllVersionsifNoVersionIsSelected();
						}
						selectTreeNodeParent(CurrentSelectedNode,fcb.getSelectedState()); 

					}

				});

				FlashCheckBox temp = fileVersionsTable.get(FileVersionNode);
				if (temp == null) {
					fileVersionsTable.put(FileVersionNode, fcb);
					// Check the parent's status
					FlashCheckBox parentCheckBox = allOpenedMap.get(CurrentSelectedNode);
					if(parentCheckBox.getSelectedState() == FlashCheckBox.FULL)
					{
						if(CurrentSelectedNode.getSelectedVersion() == FileVersionNode.getVersion())
						{
							fcb.setSelectedState(FlashCheckBox.FULL);
						}
						else if(CurrentSelectedNode.getSelectedVersion() == -1)
						{
							if(FileVersionNode.getVersion() == (CurrentSelectedNode.getVersionsCount()))
							{
								fcb.setSelectedState(FlashCheckBox.FULL);
							}
						}
					}
				} else {
					fileVersionsTable.remove(FileVersionNode);
					fcb.setSelectedState(temp.getSelectedState());
					fcb.setEnabled(temp.isEnabled());
					fileVersionsTable.put(FileVersionNode, fcb);
				}

				lc.add(fcb);

				/*IconButton image = null;
				image = new IconButton("file-icon");
				if(image != null)
				{				
					image.setWidth(20);
					image.setStyleAttribute("font-size", "0");
					lc.add(image);
				}*/

				LabelField lf = new LabelField();
//				lf.setText(CurrentSelectedNode.getDisplayName());
				lf.setValue(model.getVersion().toString());
				lc.add(lf);

				lc.setAutoWidth(true);
				lc.setAutoHeight(false);
				return lc;
			}
		});

		columnConfigs.add(fileName);



//		columnConfigs.add(Utils.createColumnConfig("version", UIContext.Constants.restoreVersionColumn(), 50,FileVersionRenderer));
		columnConfigs.add(Utils.createColumnConfig("modifiedDate", UIContext.Constants.restoreDateModifiedColumn(), 120,DateRenderer));
		columnConfigs.add(Utils.createColumnConfig("fileSize", UIContext.Constants.restoreSizeColumn(), 110,sizeRenderer));

		archiveFileVersionsColumnsModel = new ColumnModel(columnConfigs);

		archiveFileVersionsGrid = new Grid<ArchiveFileVersionNode>(archiveFileVersionsStore, archiveFileVersionsColumnsModel);
		archiveFileVersionsGrid.setAutoExpandColumn("Version");
		archiveFileVersionsGrid.setBorders(true);
		archiveFileVersionsGrid.setStripeRows(true);
		archiveFileVersionsGrid.setWidth(300);
		archiveFileVersionsGrid.setHeight(340);
		archiveFileVersionsGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		archiveFileVersionsGrid.addListener(Events.RowClick, restoreArchiveBrowsePanelListener);

	}	


	private void defineRestoreArchiveBrowsePanelListener() {
		restoreArchiveBrowsePanelListener = new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent restoreArchiveBrowseEvent) {
				if(restoreArchiveBrowseEvent.getSource() == panel)
				{

				}
				else if(restoreArchiveBrowseEvent.getSource() == archiveFileVersionsGrid)
				{

				}
			}
		};
	}

	@SuppressWarnings("unchecked")
	private void SetSelectableStateofOtherVersions(ArchiveFileVersionNode in_fileVersionNode, Boolean checked) {

		Iterator iterator = fileVersionsTable.entrySet().iterator();
		while(iterator.hasNext())
		{
			Map.Entry Temp  = (Map.Entry) iterator.next();
			FlashCheckBox fcbTemp = (FlashCheckBox)Temp.getValue();

			fcbTemp.setEnabled(!checked);
			if(!checked)
			{	
				fcbTemp.setEnabled(false);
				fcbTemp.setSelectedState(FlashCheckBox.NONE);
			}
			if(checked)
			{
				ArchiveFileVersionNode node = (ArchiveFileVersionNode)Temp.getKey();
				if(checked && (node.getVersion() == in_fileVersionNode.getVersion()))
				{
					fcbTemp.setEnabled(true);
					fcbTemp.setSelectedState(FlashCheckBox.FULL);
				}
			}
		}
	}

	private boolean PopulateVersionsGrid(ArchiveGridTreeNode treeNode) {
		archiveFileVersionsStore.removeAll();

		ArchiveFileVersionNode[] FileVersions = treeNode.getfileVersionsList();	
		int iarchiveFileVersionsCount = FileVersions != null ? FileVersions.length : 0;		

		ArchiveFileVersionNode SelectedFileVersionNode = null;

		for(int iarchiveFileVersionIndex = 0;iarchiveFileVersionIndex < iarchiveFileVersionsCount;iarchiveFileVersionIndex++)
		{
			ArchiveFileVersionNode Node = FileVersions[iarchiveFileVersionIndex];
			ArchiveFileVersionNode FileInfo = new ArchiveFileVersionNode();
			FileInfo.setVersion(Node.getVersion());
			FileInfo.setFileSize(Node.getFileSize());
			FileInfo.setModifiedTime(Node.getModifiedTime());
			FileInfo.setArchivedTime(Node.getArchivedTime());
			FileInfo.setFileType(Node.getFileType());
			FileInfo.setArchivedTZOffset(Node.getArchivedTZOffset());
			FileInfo.setModifiedTZOffset(Node.getModifiedTZOffset());

			if(treeNode.getSelectedVersion()!=-1)
			{	
				if(iarchiveFileVersionIndex == treeNode.getSelectedVersion()-1)
				{
					FileInfo.setChecked(true);
					SelectedFileVersionNode = FileInfo;
				}
				else {
					FileInfo.setChecked(false);
				}
			}
			archiveFileVersionsStore.add(FileInfo);
		}
		archiveFileVersionsGrid.reconfigure(archiveFileVersionsStore, archiveFileVersionsColumnsModel);

		if(SelectedFileVersionNode != null)
			SetSelectableStateofOtherVersions(SelectedFileVersionNode,SelectedFileVersionNode.getChecked());

		return true;
	}


	private void selectTreeNodeParent(ArchiveGridTreeNode node, int state)
	{


		ArchiveGridTreeNode parent = CurrentSelectedNode;
		if (parent != null)
		{
				if((state == FlashCheckBox.PARTIAL) || (state == FlashCheckBox.FULL))
					parent.setChecked(true);
				else
					parent.setChecked(false);
		}
	}	
	
	private void highlightRow(ArchiveGridTreeNode treeNode)
	{
		grid.getSelectionModel().deselectAll();
		grid.getSelectionModel().select(treeNode, false);
	}
	

}
