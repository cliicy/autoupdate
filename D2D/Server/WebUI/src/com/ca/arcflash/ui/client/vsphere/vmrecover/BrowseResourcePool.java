package com.ca.arcflash.ui.client.vsphere.vmrecover;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.ESXServerModel;
import com.ca.arcflash.ui.client.model.ResourcePoolModel;
import com.ca.arcflash.ui.client.model.VirtualCenterModel;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.data.ModelStringProvider;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.util.KeyNav;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class BrowseResourcePool extends Dialog {
	
	private BrowseResourcePool thisWindow;
	public TreeStore<ResourcePoolModel> treeStore;
	private TreePanel<ResourcePoolModel> treePanel;
	private TreeLoader<ResourcePoolModel> treeLoader;
	public LayoutContainer containerTree;
	private LabelField labelTreeStatus;
	final LoginServiceAsync service = GWT.create(LoginService.class);
	private ESXServerModel esxServerModel;
	private VirtualCenterModel vcModel;
	private String poolName = "";
	private String poolMoref = "";
	private boolean isCreatedTree;
	private KeyNav<ComponentEvent> enterKey;
	
	private List<ResourcePoolModel> resourcePoolModels = new ArrayList<ResourcePoolModel>();
	
	public BrowseResourcePool(VirtualCenterModel vcModel,ESXServerModel esxServerModel){
		this.vcModel = vcModel;
		this.esxServerModel = esxServerModel;
		thisWindow = this;
		enterKey = new KeyNav<ComponentEvent>(this) {
			public void handleEvent(ComponentEvent ce) {
				if (ce.getKeyCode() == 13)
					thisWindow.getButtonById(Dialog.OK).fireEvent(Events.Select);
				else if (ce.getKeyCode() == 27)
					thisWindow.getButtonById(Dialog.CANCEL).fireEvent(Events.Select);
			}
		};
	}
	
	
	@Override
	protected void onRender(Element parent, int pos) {
		// TODO Auto-generated method stub
		super.onRender(parent, pos);
		this.setButtons(Dialog.OKCANCEL);
		this.setHeadingHtml(UIContext.Constants.selectResorucePool());
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
		label.setValue(UIContext.Constants.selectResorucePool());
		container.add(label);

		containerTree = new LayoutContainer();
		containerTree.setLayout(new CenterLayout());
		containerTree.setWidth(370);
		containerTree.setHeight(210);
		container.add(containerTree, rd);
		
		labelTreeStatus = new LabelField();
		labelTreeStatus.addStyleName("browseLoading");
		labelTreeStatus.setValue(UIContext.Constants.loadingResourcePool());
		containerTree.add(labelTreeStatus);
		
		createTree();
		
		this.getButtonById(Dialog.CANCEL).setWidth(80);
		this.getButtonById(Dialog.CANCEL).setStyleAttribute("margin-left", "10px");
		this.getButtonById(Dialog.CANCEL).addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				thisWindow.hide();
			}
		});

		this.getButtonById(Dialog.OK).setWidth(80);
		this.getButtonById(Dialog.OK).addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				thisWindow.hide();
			}
			
		});

		this.add(container);
		this.setSize(400, 355);
	}
	
	RpcProxy<List<ResourcePoolModel>> proxy = new RpcProxy<List<ResourcePoolModel>>() {
		@Override
		protected void load(Object loadConfig, final AsyncCallback<List<ResourcePoolModel>> callback) {
			GWT.log("Proxy Load", null);
			
			AsyncCallback<List<ResourcePoolModel>> proxyCallBack = new AsyncCallback<List<ResourcePoolModel>>() {
				
				@Override
				public void onFailure(Throwable caught) {
					containerTree.setLayoutOnChange(true);
					labelTreeStatus.setValue(UIContext.Constants.failedLoadResourcePool());
					labelTreeStatus.setVisible(true);
					treePanel.setVisible(false);
					callback.onFailure(caught);
				}
				
				@Override
				public void onSuccess(List<ResourcePoolModel> result) {
					containerTree.setLayoutOnChange(true);
					if(result == null || result.size()==0){
						if(!isCreatedTree){
							labelTreeStatus.setValue(UIContext.Constants.noResourcePoolFound());
							labelTreeStatus.setVisible(true);
							treePanel.setVisible(false);
						}
						
					}else{
						labelTreeStatus.setVisible(false);
						treePanel.setVisible(true);
						
						resourcePoolModels.addAll(result);
					}
					callback.onSuccess(result);
				}
			};
		    if(loadConfig != null){
				final ResourcePoolModel resourcePoolModel = (ResourcePoolModel) loadConfig;
				isCreatedTree = true;
				service.getResoucePool(vcModel, esxServerModel,resourcePoolModel, proxyCallBack);
			}else{
				isCreatedTree = false;
				service.getResoucePool(vcModel, esxServerModel,null, proxyCallBack);
			}
			
		}
	};
	
	public void createTree() {
		containerTree.setLayoutOnChange(true);
		// tree loader
		treeLoader = new BaseTreeLoader<ResourcePoolModel>(proxy) {
			@Override
			public boolean hasChildren(ResourcePoolModel parent) {
				return true;
			}
		};

		// trees store
		treeStore = new TreeStore<ResourcePoolModel>(treeLoader);

		/*treeStore.setKeyProvider(new ModelKeyProvider<ExchangeDiscoveryModel>() {
			public String getKey(ExchangeDiscoveryModel model) {
				return "node_" + model.getType().toString() + model.getName() + model.getPath();
			}
		});*/

		// tree panel
		treePanel = new TreePanel<ResourcePoolModel>(treeStore);
		treePanel.setBorders(true);
		//treePanel.setStateful(true);
		treePanel.setDisplayProperty("name");
		treePanel.setWidth(370);
		treePanel.setHeight(210);
		treePanel.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		treePanel.setStyleAttribute("background-color", "white");

		treePanel.setLabelProvider(new ModelStringProvider<ResourcePoolModel>() {

			public String getStringValue(ResourcePoolModel model, String property) {
				if (model instanceof ResourcePoolModel) {
					return model.getPoolName();
				}
				return null;
			}
		});

		treePanel.setIconProvider(new ModelIconProvider<ResourcePoolModel>() {

			public AbstractImagePrototype getIcon(ResourcePoolModel model) {
				if (model instanceof ResourcePoolModel) {
					return IconHelper.createStyle("resource-pool-icon");
				}
				return null;
			}
		});
		
		
		treePanel.getSelectionModel().addSelectionChangedListener(
				new SelectionChangedListener<ResourcePoolModel>() {
					@Override
					public void selectionChanged(SelectionChangedEvent<ResourcePoolModel> se) {
						if (se.getSelectedItem() != null ) {
							ResourcePoolModel selectedItem = se.getSelectedItem();
							
							thisWindow.poolName = selectedItem.getPoolName();
							thisWindow.poolMoref = selectedItem.getPoolMoref();
							thisWindow.getButtonById(Dialog.OK).setEnabled(true);
						}
						else
						{
							thisWindow.getButtonById(Dialog.OK).setEnabled(false);
						}					
					}
					

				});
		
		treePanel.setVisible(false);
		containerTree.add(treePanel);
		
	}
		
		
	
	public String getPoolName(){
		return thisWindow.poolName;
	}
	
	public String getPoolMoref(){
		return thisWindow.poolMoref;
	}
	
	public String getPoolPath(){
		StringBuilder strPath = new StringBuilder();
		
		getPoolModelByPoolRef(poolMoref, strPath);
		
		return strPath.toString();
	}

	protected int getPoolModelByPoolRef(String parentPoolRef,StringBuilder strBufPath) {
		for (ResourcePoolModel poolModel : resourcePoolModels) {
			if(poolModel.getPoolMoref().equals(parentPoolRef)){
				if(strBufPath.toString().trim().isEmpty()){
					strBufPath.append(poolModel.getPoolName());
				}
				else{
					strBufPath.insert(0, poolModel.getPoolName()+"\\");
				}
				
				getPoolModelByPoolRef(poolModel.getParentPoolMoref(), strBufPath);
			}
		}
		return 0;
	}
}
