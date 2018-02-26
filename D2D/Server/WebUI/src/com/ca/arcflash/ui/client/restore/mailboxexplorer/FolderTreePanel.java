package com.ca.arcflash.ui.client.restore.mailboxexplorer;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.FlashCheckBox;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.GRTBrowsingContextModel;
import com.ca.arcflash.ui.client.model.GRTPagingLoadResult;
import com.ca.arcflash.ui.client.model.GridTreeNode;
import com.ca.arcflash.ui.client.restore.ExchangeGRTRecoveryPointsPanel;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.data.ModelKeyProvider;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridSelectionModel;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridView;
import com.extjs.gxt.ui.client.widget.treegrid.WidgetTreeGridCellRenderer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Widget;

public class FolderTreePanel extends ContentPanel
{
	// inherit the TreeGrid to make the selection change work correctly
	// so that click on the FlashCheckBox won't cause selectChangedEvent
	// case: "click" a FlashCheckBox on the folder tree, it should not "select" the item at the meantime
	
	public class TreeGridEx<M extends ModelData>  extends TreeGrid<M>
	{
		public TreeGridEx(TreeStore<M> store, ColumnModel cm)
		{
			super(store, cm);
			// TODO Auto-generated constructor stub
			this.setSelectionModel(new TreeGridSelectionModel<M>()
			{
				@Override
				protected void handleMouseDown(GridEvent<M> e)
				{
					//== customized code begin
					if (e != null)
					{
						M model = e.getModel();
						if (model != null)
						{
							// do no continue if clicking on the joint
							TreeNode node = findNode(model);
							if (node != null)
							{
								Element jointEl = getTreeView().getJointElement(node);
								if (jointEl != null && e.within(jointEl))
								{
									//toggle(model);
									return;
								}
							}						

							// do not continue if clicking on the FlashCheckBox
							FlashCheckBox fcb = mailboxExplorerWindow.getContext().getCheckbox((GridTreeNode) model);
							if (fcb != null)
							{
								Element element = fcb.getElement();		
								if (element != null && e.within(element))
								{
									return;
								}
							}
						}
					}					
					//== customized code end
					
					super.handleMouseDown(e);
				}
			});
		}
		
	}
	
	private MailboxExplorerPanel mailboxExplorerWindow;

	private TreeLoader<GridTreeNode> loader;
	private TreeStore<GridTreeNode> treeStore;
	private TreeGrid<GridTreeNode> treeGrid;
	private LoginServiceAsync service = GWT.create(LoginService.class);
	private GridTreeNode parentNode;

	public FolderTreePanel(MailboxExplorerPanel window)
	{
		super();
		this.setHeaderVisible(true);
//		this.setScrollMode(Scroll.AUTOX);
		this.setBorders(false);
		this.setHeadingHtml(UIContext.Constants.restoreGRTFolders());
		this.setLayout(new FitLayout());
		
		mailboxExplorerWindow = window;
	}

	@Override
	protected void onRender(Element parent, int index)
	{
		super.onRender(parent, index);		

		// data proxy
		RpcProxy<List<GridTreeNode>> proxy = new RpcProxy<List<GridTreeNode>>()
		{
			@Override
			protected void load(Object loadConfig, final AsyncCallback<List<GridTreeNode>> callback)
			{				
				GridTreeNode node = loadConfig == null ? parentNode : (GridTreeNode) loadConfig;

				if (node != null)
				{
					final Object floadConfig = loadConfig;
					AsyncCallback<GRTPagingLoadResult> cb = new AsyncCallback<GRTPagingLoadResult>()
					{
						@Override
						public void onFailure(Throwable caught)
						{
							treeGrid.unmask();
							callback.onFailure(caught);
						}

						@Override
						public void onSuccess(GRTPagingLoadResult result)
						{
							BasePagingLoadResult<GridTreeNode> pagingLoadResult = result.getPagingLoadResult();
							if (result != null && pagingLoadResult != null && pagingLoadResult.getData() != null)
							{
								for (int i = pagingLoadResult.getData().size() - 1; i >= 0; i--)
								{
									// set refer node
									GridTreeNode node = pagingLoadResult.getData().get(i);
									node.setReferNode(parentNode.getReferNode()); // this node should have the save refer nodes as its parent
								}
							}

							treeGrid.unmask();
							callback.onSuccess(pagingLoadResult.getData());

							// select the first node after loading the root nodes
							if (floadConfig == null)
							{
								treeGrid.getSelectionModel().select(0, false);
							}
						}
					};

					GRTBrowsingContextModel contextModel = new GRTBrowsingContextModel();
					contextModel.setFolderOnly(true);
								
					service.browseGRTCatalog(node, new BasePagingLoadConfig(0, 0), contextModel, cb);	

					treeGrid.mask(GXT.MESSAGES.loadMask_msg());
				}
			}
		};

		// tree loader
		loader = new BaseTreeLoader<GridTreeNode>(proxy)
		{
			@Override
			public boolean hasChildren(GridTreeNode parent)
			{
				return parent instanceof GridTreeNode;
			}
		};

		// trees store
		treeStore = new TreeStore<GridTreeNode>(loader);

		treeStore.setStoreSorter(new StoreSorter<GridTreeNode>()
		{
			private int fileNameCompare(GridTreeNode m1, GridTreeNode m2)
			{
				int r = 0;
				if (m1.getDisplayName() == null)
				{
					if (m2.getDisplayName() == null)
						return 0;
					else
						return -1;
				}
				else
				{
					if (m2.getDisplayName() == null)
						return 1;
					else
					{
						r = m1.getDisplayName().compareToIgnoreCase(m2.getDisplayName());
						if (r == 0)
							r = m1.getDisplayName().compareTo(m2.getDisplayName());
						return r;
					}
				}
			}

			public int compare(Store<GridTreeNode> store, GridTreeNode m1, GridTreeNode m2, String property)
			{
				if (m1 != null && m2 != null)
				{					
					if (property == null && m1.getDisplayName() != null && m2.getDisplayName() != null)
					{
						return m1.getDisplayName().compareToIgnoreCase(m2.getDisplayName());
					}
					else if (property == "displayName")
					{
						return fileNameCompare(m1, m2);
					}
					else if (property == "date")
					{
						if (m1.getDate() == null)
						{
							if (m2.getDate() == null)
								return fileNameCompare(m1, m2);
							else
								return -1;
						}
						else
						{
							if (m2.getDate() == null)
								return 1;
							else
								return m1.getDate().compareTo(m2.getDate());
						}
					}
					else if (property == "size")
					{
						if (m1.getSize() == null)
						{
							if (m2.getSize() == null)
								return fileNameCompare(m1, m2);
							else
								return -1;
						}
						else
						{
							if (m2.getSize() == null)
								return 1;
							else
							{
								if (m1.getSize() == m2.getSize())
								{
									return fileNameCompare(m1, m2);
								}
								else if (m1.getSize() < m2.getSize())
									return -1;
								else
									return 1;
							}
						}
					}
				}

				return super.compare(store, m1, m2, property);

			}
		});

		treeStore.setKeyProvider(new ModelKeyProvider<GridTreeNode>()
		{

			public String getKey(GridTreeNode model)
			{
				return model.toId();
			}

		});
		
		ColumnModel cm = createColumnModel();

		treeGrid = new TreeGridEx<GridTreeNode>(treeStore, cm);
		treeGrid.ensureDebugId("790b46cf-5d31-4bee-81aa-2058a593bc94");

		// Remove the default icons
		treeGrid.setIconProvider(new ModelIconProvider<GridTreeNode>()
		{
			@Override
			public AbstractImagePrototype getIcon(GridTreeNode model)
			{
				return AbstractImagePrototype.create(UIContext.IconBundle.blank());
			}

		});		
		
		// select listener
		treeGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		treeGrid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<GridTreeNode>()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent<GridTreeNode> se)
			{
				if (se.getSelectedItem() != null)
				{
					GridTreeNode node = se.getSelectedItem();
					onNodeSelected(node);
				}
			}
		});		

		((TreeGridView) treeGrid.getView()).setRowHeight(23);
		treeGrid.setBorders(false);
		treeGrid.setAutoExpandColumn("name");
		treeGrid.setTrackMouseOver(false);
		treeGrid.setHideHeaders(true);

		if (!mailboxExplorerWindow.getContext().isRestoreManager())
		{
			GridSelectionModel<GridTreeNode> sm = new GridSelectionModel<GridTreeNode>();
			sm.setLocked(true);
			treeGrid.setSelectionModel(sm);
			treeGrid.setTrackMouseOver(true);
		}

		ContentPanel panel = new ContentPanel();
		panel.setHeaderVisible(false);
		panel.setLayout(new FitLayout());
		panel.setCollapsible(false);
		panel.setScrollMode(Scroll.AUTOX);
		panel.setBorders(false);

		panel.add(treeGrid);

		this.add(panel);
	}
	
	private ColumnModel createColumnModel()
	{
		CheckColumnConfig checkColumn = new CheckColumnConfig("checked", "", 40);
		checkColumn.setHidden(true);

		ColumnConfig name = new ColumnConfig("name", "Name", 100);
		name.setMenuDisabled(true);
		name.setRenderer(new WidgetTreeGridCellRenderer<ModelData>()
		{

			@Override
			public Widget getWidget(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid)
			{

				LayoutContainer lc = new LayoutContainer();

				TableLayout layout = new TableLayout();
				layout.setColumns(3);
				lc.setLayout(layout);
				
				final GridTreeNode node = (GridTreeNode) model;
				
				GridTreeNode parent = (GridTreeNode) treeGrid.getTreeStore().getParent(node);
				if (parent == null)
				{
					parent = parentNode;
				}
				
				final FlashCheckBox fcb = mailboxExplorerWindow.getContext().createFlashCheckbox(node, parent, -1);

				fcb.addSelectionListener(new SelectionListener<IconButtonEvent>()
				{
					@Override
					public void componentSelected(IconButtonEvent ce)
					{
						if (fcb.isEnabled() == false)
							return;

						selectTreeNodeChildren(node, fcb.getSelectedState(), true);
					}
				});

				if (mailboxExplorerWindow.getContext().isRestoreManager())
				{
					lc.add(fcb);
				}

				IconButton image = ExchangeGRTRecoveryPointsPanel.getNodeIcon(node);
				if (image != null)
					lc.add(image);

				LabelField lf = new LabelField();
				lf.setValue(node.getDisplayName());
				lf.setTitle(node.getDisplayName());
				lc.add(lf);

				return lc;
			}

		});		

		ColumnModel cm = new ColumnModel(Arrays.asList(checkColumn, name));
		
		return cm;
	}

	protected void selectTreeNodeChildren(GridTreeNode node, int state, boolean updateParent)
	{
		// Select this node
		FlashCheckBox fcb = mailboxExplorerWindow.getContext().getCheckbox(node);
		if (fcb != null)
		{
			fcb.setSelectedState(state);			
		}
		
		if (node.equals(treeGrid.getSelectionModel().getSelectedItem()))
		{
			mailboxExplorerWindow.onCheckedFolder(node, state);
		}		

		// select children
		//if (treeGrid.isExpanded(node))
		{
			// Get the children
			List<GridTreeNode> childNodes = treeGrid.getTreeStore().getChildren(node);
			// For each call select Children
			for (int i = 0; i < childNodes.size(); i++)
			{
				selectTreeNodeChildren(childNodes.get(i), state, false);
			}
		}

		// Set the parent
		if (updateParent)
		{
			selectTreeNodeParent(node);
		}
	}

	protected void selectTreeNodeParent(GridTreeNode node)
	{	
		if (node != null)
		{
			int parentState = FlashCheckBox.NONE;
			
			GridTreeNode parent = treeGrid.getTreeStore().getParent(node);
			boolean isParentRoot = false;
			List<GridTreeNode> childNodes = null;
			
			// if the parent is mailbox
			if (parent == null)
			{
				isParentRoot = true;				
				childNodes = treeStore.getRootItems();
				
				parent = parentNode; // set the real parent for use
			}
			else
			{
				isParentRoot = false;
				childNodes = treeStore.getChildren(parent);
			}
			
			if (childNodes != null)
			{
				int state = mailboxExplorerWindow.getContext().calcChildrenState(parent, null);

				switch (state)
				{
				case FlashCheckBox.NONE:
					parentState = FlashCheckBox.NONE;
					break;
				case FlashCheckBox.PARTIAL:
				case FlashCheckBox.FULL:
				default:
					parentState = FlashCheckBox.PARTIAL;
					break;
				}
			}

			FlashCheckBox fcb = mailboxExplorerWindow.getContext().getCheckbox(parent);
			if (fcb != null && parentState != fcb.getSelectedState())
			{
				if (!isParentRoot)
				{
					fcb.setSelectedState(parentState);
					
					// Parent changed, change the parent's parent
					selectTreeNodeParent(parent);
				}
				else
				{
					mailboxExplorerWindow.checkMailboxAndParent(parent, parentState);
				}
			}
		}		
	}

	// set the parent mailbox
	public void setParentNode(GridTreeNode node)
	{
		if (node == null)
		{
			clearPanel();		
		}
		else
		{
			if (!node.equals(parentNode))
			{
				clearPanel();
				
				parentNode = node;
				
				if (loader != null)
				{
					loader.load();
				}
			}
		}
	}
	
	public void clearPanel()
	{
		if (parentNode != null)
		{
			mailboxExplorerWindow.getContext().cleanRedandant(parentNode);
		}
		
		if (treeStore != null)
		{
			treeStore.removeAll();
		}
		
		parentNode = null;
		
		onNodeSelected(null); // update other panels
	}
	
	// called when a folder is selected
	protected void onNodeSelected(GridTreeNode node)
	{
		mailboxExplorerWindow.onSelectedFolder(node);
	}
	
	// called when a mailbox is checked
	public void onParentChecked(GridTreeNode node, int selectState)
	{
		if (node != null && node.equals(parentNode))
		{
			List<GridTreeNode> rootItems = treeStore.getRootItems();
			
			Iterator<GridTreeNode> iterator = rootItems.iterator();
			
			while (iterator.hasNext())
			{				
				selectTreeNodeChildren(iterator.next(), selectState, false);				
			}			
		}
	}
	
	// called when a mail is checked
	public void checkNodeAndParent(GridTreeNode node, int selectState)
	{
		if (node != null)
		{
			FlashCheckBox fcb =  mailboxExplorerWindow.getContext().getCheckbox(node);
			if (fcb != null)
			{
				fcb.setSelectedState(selectState);				
			}
			
			final GridTreeNode tempNode = node;
			DeferredCommand.addCommand(new Command()
			{
				@Override
				public void execute()
				{
					selectTreeNodeParent(tempNode);
				}
			});

		}
	}	
}

