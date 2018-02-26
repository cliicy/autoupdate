package com.ca.arcflash.ui.client.restore;

import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.ExchangeDiscoveryModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.data.ModelKeyProvider;
import com.extjs.gxt.ui.client.data.ModelStringProvider;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class BrowseExchOrgDialog extends Dialog {

	public class TreePanelEx<M extends ModelData>  extends TreePanel<M>
	{

		public TreePanelEx(TreeStore<M> store)
		{
			super(store);
			// TODO Auto-generated constructor stub
		}
		
		public void setLoading(M m)
		{
			if (m != null && this.getView() != null && rendered)
			{
				TreeNode node = findNode(m);
				
				if (node != null && node.getElement() != null)
				{
					this.getView().onLoading(node);
				}
				
			}			
		}
		
		public void restoreIcon(M m)
		{
			if (m != null && this.getView() != null && rendered)
			{
				TreeNode node = findNode(m);
				if (node != null && node.getElement() != null)
				{
					view.onIconStyleChange(node, calculateIconStyle(m));
				}
			}
		}
		
	};
	
	private static final int PAGESIZE = 100;
	private static final int PAGESIZE_MAX = 1000;
	
	// dialog items
	private BrowseExchOrgDialog thisWindow;
	private String strDialogResult;

	// service
	final LoginServiceAsync service = GWT.create(LoginService.class);

	// tree
	private LabelField labelTreeStatus; // the status text message

	public LayoutContainer containerTree;

	private TreeLoader<ExchangeDiscoveryModel> treeLoader;
	public TreeStore<ExchangeDiscoveryModel> treeStore;
	private TreePanelEx<ExchangeDiscoveryModel> treePanel;

	private TextField<String> textFieldExchangeDestination;
	private String strDestination;
	private int nDestinationServerVersion;

	private LabelField labelFolder;
	private LabelField labelFolderNote;
	private TextField<String> textFieldFolder;
	private String strFolder;

	// AD Server Account
	private String strADServer;
	private String strUser;
	private String strPassword;
	
	private boolean bDestinationFolderEnable = false;
	private boolean bDestinationFolderNoteShowed = false;

	public BrowseExchOrgDialog(String title) {
		super();
		this.thisWindow = this;
		this.setHeadingHtml(title);	
		this.setButtons(Dialog.OKCANCEL);
		this.setButtonAlign(HorizontalAlignment.RIGHT);
		this.getButtonById(Dialog.CANCEL).setWidth(50);
		this.getButtonById(Dialog.CANCEL).ensureDebugId("4C8C3CEF-2F02-46c9-B768-74B22087CABB");
		this.getButtonById(Dialog.CANCEL).setStyleAttribute("margin-left", "10px");
		this.getButtonById(Dialog.CANCEL).addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				strDialogResult = Dialog.CANCEL;
				thisWindow.hide();
			}
		});

		this.getButtonById(Dialog.OK).setWidth(50);
		this.getButtonById(Dialog.OK).ensureDebugId("3029CD62-C48E-4734-B5EB-DA43504B6354");
		this.getButtonById(Dialog.OK).addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {

				// correct the folder before validate it 
				textFieldFolder.setValue(validateFolder(textFieldFolder.getValue()));
				
				boolean bValid = textFieldFolder.validate();
				
				if (bValid)
				{
				strDialogResult = Dialog.OK;
				thisWindow.hide();
			}

			}
		});
	}

	public void setADServerAccount(String adServer, String user, String password) {
		strADServer = adServer;
		strUser = user;
		strPassword = password;
	}

	public String getDialogResult() {
		return strDialogResult;
	}

	@Override
	protected void onRender(Element parent, int pos) {		
		super.onRender(parent, pos);		
		LayoutContainer container = new LayoutContainer();
		RowLayout rl = new RowLayout();
		container.setLayout(rl);

		RowData rd = new RowData();
		Margins margins = new Margins();
		margins.left = 5;
		margins.right = 5;
		rd.setMargins(margins);

		// description
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.restoreSelectExchDestination());
		container.add(label);

		containerTree = new LayoutContainer();
		containerTree.setLayout(new CenterLayout());
		containerTree.setWidth(370);
		containerTree.setHeight(210);
		container.add(containerTree, rd);

		// tree

		/*
		 * labelTreeStatus = new LabelField();
		 * labelTreeStatus.addStyleName("browseLoading");
		 * labelTreeStatus.setText(UIContext.Constants.restoreLoading());
		 * containerTree.add(labelTreeStatus);
		 */

		// create the Exchange Organization tree
		createTree(containerTree);

		// destination
		label = new LabelField(UIContext.Constants.restoreSelectedItem());
		label.ensureDebugId("10950807-dfea-44c4-8f40-aecfbe6f3150");
		container.add(label, rd);

		textFieldExchangeDestination = new TextField<String>();
		textFieldExchangeDestination.ensureDebugId("1b625fb3-5f6e-4ca7-95db-3c0383b707bb");
		textFieldExchangeDestination.setWidth(370);
		textFieldExchangeDestination.setStyleAttribute("padding-left", "0px");
		textFieldExchangeDestination.setReadOnly(true);
		Utils.addToolTip(textFieldExchangeDestination, UIContext.Constants.restoreSelectDestinationOnTheTree());
		//textFieldExchangeDestination.setAllowBlank(false);
		container.add(textFieldExchangeDestination, rd);

		if (strDestination != null && !strDestination.isEmpty())
		{
			textFieldExchangeDestination.setValue(strDestination);
		}
		
		// folder		
		labelFolder = new LabelField(UIContext.Constants.restoreDestinationFolder());
		labelFolder.setStyleAttribute("padding-top", "10px");
		container.add(labelFolder, rd);
		textFieldFolder = new TextField<String>();
		textFieldFolder.ensureDebugId("18c7b5d2-03fa-49ba-941d-c965c33d5b74");
		textFieldFolder.setWidth(370);	
		textFieldFolder.setStyleAttribute("padding-left", "0px");
		textFieldFolder.setAllowBlank(false);		
		textFieldFolder.setMaxLength(256);
		textFieldFolder.setEnabled(bDestinationFolderEnable);					
		Utils.addToolTip(textFieldFolder, UIContext.Constants.restoreInputFolderName());
		container.add(textFieldFolder, rd);
		if (bDestinationFolderNoteShowed) {			
			labelFolderNote = new LabelField(UIContext.Constants.restoreDestinationFolderNote());
			container.add(labelFolderNote, rd);
		}
		if (strFolder != null && !strFolder.isEmpty())
		{
			textFieldFolder.setValue(strFolder);
		}		
		
		if (strDestination == null || strDestination.isEmpty())
		{
			this.getButtonById(Dialog.OK).setEnabled(false);
		}	

		this.add(container);
		this.setSize(400, bDestinationFolderNoteShowed ? 460 : 420);
	}

	// continue load the children if it has more than PAGESIZE
	protected void load(final ExchangeDiscoveryModel parent, final int offset, final int limit, final MessageBox box)
	{
		if (parent == null)
		{
			return;
		}		
		
		BasePagingLoadConfig pageConfig = new BasePagingLoadConfig(offset, limit);
		
		treePanel.setLoading(parent);		
		service.getPagingTreeExchangeChildren(parent, pageConfig, strUser, strPassword,
				new AsyncCallback<PagingLoadResult<ExchangeDiscoveryModel>>()
		{
			@Override
			public void onFailure(Throwable caught)
			{
				treePanel.restoreIcon(parent);
				box.close();
			}

			@Override
			public void onSuccess(PagingLoadResult<ExchangeDiscoveryModel> result)
			{
				treeStore.add(parent, result.getData(), false);				
				treePanel.restoreIcon(parent);
				
				ProgressBar bar = box.getProgressBar();		
				bar.updateProgress(((double)offset+result.getData().size()) / result.getTotalLength(), (offset+result.getData().size()) + "/" + result.getTotalLength());
				
				
				if (offset + limit < result.getTotalLength())
				{
					thisWindow.load(parent, offset + limit, limit, box);
				}
				else
				{
					box.close();
				}
			}
		});
		
	}

	public void createTree(LayoutContainer container) {
		// data proxy
		RpcProxy<List<ExchangeDiscoveryModel>> proxy = new RpcProxy<List<ExchangeDiscoveryModel>>() {
			@Override
			protected void load(Object loadConfig, final AsyncCallback<List<ExchangeDiscoveryModel>> callback) {
				GWT.log("Proxy Load", null);
				final ExchangeDiscoveryModel exchDiscModel = (ExchangeDiscoveryModel) loadConfig;

//				AsyncCallback<List<ExchangeDiscoveryModel>> proxyCallBack = new BaseAsyncCallback<List<ExchangeDiscoveryModel>>() {
//					
//					@Override
//					public void onFailure(Throwable caught) {
//						callback.onFailure(caught);
//					}
//					
//					@Override
//					public void onSuccess(List<ExchangeDiscoveryModel> result) {
//						callback.onSuccess(result);
//					}
//				};
//				
//				service.getTreeExchangeChildren(exchDiscModel, proxyCallBack);
				
				
				// if the page is not finished, display and load more till it finished, but do not hang
				BasePagingLoadConfig pageConfig = new BasePagingLoadConfig(0, PAGESIZE);
				AsyncCallback<PagingLoadResult<ExchangeDiscoveryModel>> pagingCallback = new AsyncCallback<PagingLoadResult<ExchangeDiscoveryModel>>()
				{
					
					@Override
					public void onFailure(Throwable caught)
					{
						callback.onFailure(caught);
					}
					
					@Override
					public void onSuccess(PagingLoadResult<ExchangeDiscoveryModel> result)
					{
						callback.onSuccess(result.getData());

						// if it has more data
						if (result.getTotalLength() > result.getData().size() && (strDialogResult == null))
						{
							MessageBox box = MessageBox.progress(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D), UIContext.Constants.restoreLoading(), 0 + "/" + result.getTotalLength());							
							Utils.setMessageBoxDebugId(box);
							ProgressBar bar = box.getProgressBar();							
							bar.updateProgress(((double)PAGESIZE) / result.getTotalLength(), PAGESIZE + "/" + result.getTotalLength());
							
							int pageSize = result.getTotalLength() / 7;
							if (pageSize < PAGESIZE)
							{
								pageSize = PAGESIZE;
							}
							else if (pageSize > PAGESIZE_MAX)
							{
								pageSize = PAGESIZE_MAX;
							}
							
							thisWindow.load(exchDiscModel, PAGESIZE, pageSize, box);
						}				
					}
				};
				
				service.getPagingTreeExchangeChildren(exchDiscModel, pageConfig, strUser, strPassword, pagingCallback);
			}
		};

		// tree loader
		treeLoader = new BaseTreeLoader<ExchangeDiscoveryModel>(proxy) {
			@Override
			public boolean hasChildren(ExchangeDiscoveryModel parent) {
				return true;
			}
		};

		// trees store
		treeStore = new TreeStore<ExchangeDiscoveryModel>(treeLoader);

		treeStore.setKeyProvider(new ModelKeyProvider<ExchangeDiscoveryModel>() {
			public String getKey(ExchangeDiscoveryModel model) {
				return "node_" + model.getType().toString() + model.getName() + model.getPath();
			}
		});

                // comment out the sorting in UI since there might be lots of items here, which will cause timeout to execute the script
                // web service will take the sorting instead
		/*treeStore.setStoreSorter(new StoreSorter<ExchangeDiscoveryModel>() {
			@Override
			public int compare(Store<ExchangeDiscoveryModel> store, ExchangeDiscoveryModel m1,
					ExchangeDiscoveryModel m2, String property) {
				return m1.compareTo(m2);
			}
		});*/

		// tree panel
		treePanel = new TreePanelEx<ExchangeDiscoveryModel>(treeStore);
		treePanel.ensureDebugId("d818a7e3-e5ad-413e-b094-c76c3e88b88a");
		treePanel.setBorders(true);
		// treePanel.setStateful(true);
		treePanel.setDisplayProperty("name");
		treePanel.setWidth(370);
		treePanel.setHeight(210);
		treePanel.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		treePanel.setStyleAttribute("background-color", "#FFFFFF");

		treePanel.setLabelProvider(new ModelStringProvider<ExchangeDiscoveryModel>() {

			public String getStringValue(ExchangeDiscoveryModel model, String property) {
				if (model instanceof ExchangeDiscoveryModel) {
					
					String strLabel = "";
//					if (model.getType().intValue() == ExchangeDiscoveryModel.EXCH_DISC_TYPE_ORGANIZATION)
//					{
//						strLabel = UIContext.Constants.restoreExchangeOrganization();
//					}
//					else
					{
						strLabel = model.getName();
					}
					
					return strLabel;
				}
				return null;
			}
		});

		treePanel.setIconProvider(new ModelIconProvider<ExchangeDiscoveryModel>() {

			public AbstractImagePrototype getIcon(ExchangeDiscoveryModel model) {
				if (model instanceof ExchangeDiscoveryModel) {
					return IconHelper.createStyle(model.getIcon());
				}
				return null;
			}
		});

		// selection listener
		treePanel.getSelectionModel().addSelectionChangedListener(
				new SelectionChangedListener<ExchangeDiscoveryModel>() {
					@Override
					public void selectionChanged(SelectionChangedEvent<ExchangeDiscoveryModel> se) {
						if (se.getSelectedItem() != null && se.getSelectedItem().isValidDestination()) {
							ExchangeDiscoveryModel exchangeItem = se.getSelectedItem();
							String dest = "";

							if (treeStore != null)
							{
								ExchangeDiscoveryModel curItem = exchangeItem;
								while (curItem != null
										&& curItem.getName() != null
										&& curItem.getType().intValue() != ExchangeDiscoveryModel.EXCH_DISC_TYPE_ORGANIZATION)
								{
									dest = dest.isEmpty() ? curItem.getName() : curItem.getName() + "\\" + dest;
									
									if (curItem.getType().intValue() == ExchangeDiscoveryModel.EXCH_DISC_TYPE_SERVER)
									{
										nDestinationServerVersion = curItem.getExVersion();
									}
									curItem = treeStore.getParent(curItem);
								}
							}

							textFieldExchangeDestination.setValue(dest);
							thisWindow.getButtonById(Dialog.OK).setEnabled(true);
						}
						else
						{
							textFieldExchangeDestination.setValue("");
							thisWindow.getButtonById(Dialog.OK).setEnabled(false);
						}					
					}
					

				});

		container.add(treePanel);
	}

	public String getDestination() {
		// TODO Auto-generated method stub
		strDestination = "";

		if (textFieldExchangeDestination != null && textFieldExchangeDestination.getValue() != null) {
			strDestination = textFieldExchangeDestination.getValue();
		}

		return strDestination;
	}

	public void setDestination(String strDestination) {
		this.strDestination = strDestination;
	}
	
	public int getDestinationServerVersion() {
		return nDestinationServerVersion;
	}

	public void setDestinationServerVersion(int nDestinationServerVersion) {
		this.nDestinationServerVersion = nDestinationServerVersion;
	}


	public String getFolder()
	{
		strFolder = "";
		if (textFieldFolder != null && textFieldFolder.getValue() != null) {
			strFolder = textFieldFolder.getValue();
		}

		return strFolder;
	}

	public void setFolder(String strFolder)
	{
		this.strFolder = strFolder;
	}

	public String validateFolder(String folder)
	{
			if (folder != null)
		{
			// trim the leading "\\"
			while (folder.startsWith("\\"))
			{
				folder = folder.substring(1);
			}
			
			// trim the ending "\\"
			while (folder.endsWith("\\"))
			{
				folder = folder.substring(0, folder.length() - 1);
			}
			
			// remove the inner "\\\\"
			while (folder.contains("\\\\"))
			{			
				folder = folder.replace('\\', '\01');
				
				folder = folder.replace("\01\01", "\01");
				
				folder = folder.replace('\01', '\\');
			}
			
			folder = folder.trim();
		}
		
		return folder;
	}
	
	public void setDestinationFolderEnable(boolean enable) {
		bDestinationFolderEnable = enable;
	}
	public void setDestinationFolderNoteShowed(boolean showed) {
		bDestinationFolderNoteShowed = showed;
	}	
}
