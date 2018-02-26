package com.ca.arcflash.ui.client.restore;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.PathSelectionPanel;
import com.ca.arcflash.ui.client.common.UserPasswordWindow;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.common.gxtex.GridEx;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.homepage.HomeContentFactory;
import com.ca.arcflash.ui.client.model.BackupD2DModel;
import com.ca.arcflash.ui.client.model.BackupRPSDestSettingsModel;
import com.ca.arcflash.ui.client.model.RpsPolicy4D2DRestoreModel;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.DataProxy;
import com.extjs.gxt.ui.client.data.DataReader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class RestoreSelectRPSSettingWindow extends Window {
	private static final CommonServiceAsync commonService = GWT.create(CommonService.class);
	
	private RestoreRPSSettingsPanel panel;	
	private ListStore<BackupD2DModel> d2dStore;
	private RpsPolicy4D2DRestoreModel currentPolicy;
	private List<BackupD2DModel> allD2ds = new ArrayList<BackupD2DModel>();
	private String currentDataStore;
	private BackupD2DModel currentD2D;
	private GridEx<BackupD2DModel> grid;
	private ColumnModel cModel;
	private GridSelectionModel<BackupD2DModel> gridSelectionModel;
	private BasePagingLoader<PagingLoadResult<ModelData>> loader;
	private static final int PAGESIZE = 10;
	private boolean initialized = false;
	private boolean backupToDataStore = false;
	
	private Radio selectSharedFolder;
	private Radio selectRPS;
	protected PathSelectionPanel pathSelection = new PathSelectionPanel(false, null, false);
	private ContentPanel d2dContainer;
	private Button ok;
	
	private RestoreSelectRPSSettingWindow thisWindow = null;
	
	public RestoreSelectRPSSettingWindow(boolean backupToDataStore) {
		this.backupToDataStore = backupToDataStore;
		thisWindow = this;
		
		this.setResizable(false);
		this.setWidth(770); // For French pseudo to avoid truncate
		this.setAutoHeight(true);
		this.setModal(true);
		this.setHeadingHtml(UIContext.Constants.Source());
		
		LayoutContainer container = new LayoutContainer();
		container.setStyleAttribute("background-color", "#FFFFFF");
		TableLayout tl = new TableLayout();
		tl.setColumns(1);
		tl.setCellPadding(2);
		tl.setCellSpacing(4);
		tl.setWidth("100%");
		container.setLayout(tl);
		
		addRadioButtons(container);
		addPathSelection(container);
		addRPSSelection(container);
		add(container);
		addButtons();
		selectSharedFolder.setValue(!backupToDataStore);
	}
	
	private void addRadioButtons(LayoutContainer container){
		TableLayout tl = new TableLayout();
		tl.setCellPadding(2);
		tl.setCellSpacing(2);
		tl.setWidth("98%");
		LayoutContainer rc = new LayoutContainer();
		rc.setLayout(tl);
		selectSharedFolder = new Radio();
		selectSharedFolder.setBoxLabel(UIContext.Constants.selectSharedFolder());
		selectSharedFolder.ensureDebugId("68DC9006-4521-4B82-B55C-E0324B4DECF2");
		selectSharedFolder.addListener(Events.Change, new Listener<BaseEvent>(){
			@Override
			public void handleEvent(BaseEvent be) {
				if(selectSharedFolder.getValue()) {
					if(pathSelection != null) {
						pathSelection.setVisible(true);
					}
					if(panel != null)
						panel.setVisible(false);
					if(d2dContainer != null)
						d2dContainer.setVisible(false);
				}else {
					if(pathSelection != null)
						pathSelection.setVisible(false);
					if(panel != null)
						panel.setVisible(true);
					if(d2dContainer != null)
						d2dContainer.setVisible(true);
				}	
				thisWindow.syncSize();
			}
		});
		
		selectRPS = new Radio();
		selectRPS.setBoxLabel(UIContext.Messages.selectRPS(UIContext.Constants.productShortNameRPS()));
		selectRPS.ensureDebugId("7BF763A0-08DD-4E40-B0B1-8F30818DC879");
		
		RadioGroup rg = new RadioGroup();
		rg.setOrientation(Orientation.VERTICAL);
		rg.add(selectRPS);
		rg.add(selectSharedFolder);
		rg.setValue(selectRPS);
		
		rc.add(selectSharedFolder);
		rc.add(selectRPS);
		container.add(rc);
	}
	
	private void addPathSelection(LayoutContainer container){
		pathSelection.setWidth(680);
		pathSelection.setPathFieldLength(540);
		pathSelection.setMode(PathSelectionPanel.RESTORE_MODE);
		pathSelection.addDebugId("ABB69ED1-670A-48ab-8601-9861BFF04E53", 
				"51D530D3-2DAB-400d-B640-DED692412EDF", "091D0C8D-C1AC-499e-82EB-EA4E20AB7BEC");
		pathSelection.setVisible(false);
		TableData td = new TableData();
//		td.setColspan(2);
		td.setWidth("100%");
		container.add(pathSelection, td);
	}
	
	private void addRPSSelection(LayoutContainer container){
		LayoutContainer rpsc = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(2);
		tl.setCellPadding(2);
		tl.setCellSpacing(2);
		tl.setWidth("100%");
		rpsc.setLayout(tl);
		TableData td = new TableData();
		td.setWidth("50%");		
		td.setHeight("100%");
		panel = new RestoreRPSSettingsPanel(this);
//		panel.setStyleAttribute("background-color", "#FFFFFF");
		td.setVerticalAlign(VerticalAlignment.TOP);
		rpsc.add(panel, td);
		
		td = new TableData();
		td.setWidth("50%");
		td.setHeight("100%");
		td.setVerticalAlign(VerticalAlignment.TOP);
		rpsc.add(addD2DGrid(), td);
		
		container.add(rpsc);
	}
	
	private void addButtons(){
		ok = new Button(UIContext.Constants.okButton());
		ok.setMinWidth(80);
		ok.setAutoWidth(true);
		ok.setItemId(Dialog.OK);
		ok.ensureDebugId("D74AE163-381D-4570-BB2B-86EA918460C4");
		ok.addListener(Events.Select, new Listener<BaseEvent>(){
			@Override
			public void handleEvent(BaseEvent be) {
				backupToDataStore = selectRPS.getValue();
				if(selectRPS.getValue()){
					if(panel.validate(true)){
						if(allD2ds.isEmpty()){
							panel.popupError(
									UIContext.Messages.d2dEmpty(UIContext.productNameD2D));	
						}else {
							hide(ok);
						}
					}else {
						panel.popupError(panel.getValidationError());
					}
				}else {
					validateSharedFolder();
				}
			}
		});
		this.getButtonBar().add(ok);
		
		final Button cancel = new Button(UIContext.Constants.cancel());
		cancel.setMinWidth(80);
		cancel.setAutoWidth(true);
		cancel.setItemId(Dialog.CANCEL);
		cancel.ensureDebugId("9EDC9C11-1995-4780-B6E5-C0E458E32392");
		cancel.addListener(Events.Select, new Listener<BaseEvent>(){

			@Override
			public void handleEvent(BaseEvent be) {
				hide(cancel);
			}
		});
		this.getButtonBar().add(cancel);
	}
	
	private ContentPanel addD2DGrid(){
		DataProxy<PagingLoadResult<BackupD2DModel>> proxy = new DataProxy<PagingLoadResult<BackupD2DModel>>() {

			@Override
			public void load(
					DataReader<PagingLoadResult<BackupD2DModel>> reader,
					Object loadConfig,
					AsyncCallback<PagingLoadResult<BackupD2DModel>> callback) {
				final PagingLoadConfig conf = (PagingLoadConfig)loadConfig;
					PagingLoadResult<BackupD2DModel> models = loadD2D(conf);
					callback.onSuccess(models);
				}
		};
		
		// loader
		loader = new BasePagingLoader<PagingLoadResult<ModelData>>(
				proxy);		
		
		d2dContainer = new ContentPanel();
		d2dContainer.setHeaderVisible(false);
		d2dContainer.setLayout(new RowLayout());
		d2dContainer.setHeight(265);
		d2dContainer.setWidth("100%");
		
		d2dStore = new ListStore<BackupD2DModel>(loader);
		
		D2DNameFilter4Restore filter = new D2DNameFilter4Restore();
		filter.bind(d2dStore);
//		d2dContainer.add(filter, new RowData(0.9, -1));
		d2dContainer.setTopComponent(filter);
		
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		ColumnConfig column = new ColumnConfig();
		column.setId("hostname");
		column.setHeaderHtml(UIContext.Constants.node());
		column.setWidth(295);
		configs.add(column);
		
		
		column = new ColumnConfig();
		column.setId("username");
		column.setHeaderHtml(UIContext.Constants.userName());
		column.setWidth(120);
		configs.add(column);
		
		
		column = new ColumnConfig();
		column.setId("destPlanName");
		column.setHeaderHtml(UIContext.Constants.destPlanName());
		column.setWidth(200);
		configs.add(column);
		
		column = new ColumnConfig();
		column.setId("sourceRPSServerName");
		column.setHeaderHtml(UIContext.Constants.sourceRPSServerName());
		column.setWidth(120);
		configs.add(column);		
		
		cModel = new ColumnModel(configs);
		
		grid = new GridEx<BackupD2DModel>(d2dStore, cModel);
		grid.setBorders(true);
		grid.setStripeRows(true);
		grid.setHeight(175);
		grid.setAutoExpandColumn("hostname");
		grid.setAutoExpandMin(200);
		gridSelectionModel = new GridSelectionModel<BackupD2DModel>();
		gridSelectionModel.setSelectionMode(SelectionMode.SINGLE);
		grid.setSelectionModel(gridSelectionModel);
		gridSelectionModel.addListener(Events.SelectionChange, 
				new Listener<SelectionChangedEvent<BackupD2DModel>>(){

					@Override
					public void handleEvent(
							SelectionChangedEvent<BackupD2DModel> be) {
						currentD2D = be.getSelectedItem();
					}
				
		});
		d2dContainer.add(grid, new RowData(1, -1));
		
		PagingToolBar toolBar = new PagingToolBar(PAGESIZE){	
			@Override
			protected void onRender(Element target, int index) {				
				super.onRender(target, index);
				this.pageText.setWidth("38px");
				this.pageText.addKeyPressHandler(new KeyPressHandler(){
					@Override
					public void onKeyPress(KeyPressEvent event) {
						char key = event.getCharCode();
						if(event.isControlKeyDown() || key == KeyCodes.KEY_ENTER 
								|| key == KeyCodes.KEY_BACKSPACE || key == KeyCodes.KEY_DELETE){
							return;
						}
						if(!Character.isDigit(key)){
							pageText.cancelKey();
						}
					}
					
				});
				this.next.ensureDebugId("77B8EFD8-8DCD-4189-9BF9-A441E17E6351");
				this.prev.ensureDebugId("C988882D-40D7-4e4a-92FE-AB829061A9B2");
				this.first.ensureDebugId("95447E19-179E-4191-B78B-98B78EA8B059");
				this.last.ensureDebugId("690A5940-0452-4d31-B493-68281BF9C6D8");
				this.refresh.hide();
			}			
		};
		toolBar.bind(loader);
		d2dContainer.setBottomComponent(toolBar);
		return d2dContainer;
	}

	public void refreshData(BackupRPSDestSettingsModel rpsDestModel, 
			BackupD2DModel currentD2D, 
			List<RpsPolicy4D2DRestoreModel> rpsPolicyList) {
		if(backupToDataStore){
			panel.refreshData(rpsDestModel, rpsPolicyList);
			if(rpsDestModel != null)
				currentDataStore = rpsDestModel.getRPSDataStoreUUID();
		}else {
			if(currentD2D != null){
				pathSelection.setDestination(currentD2D.getDestination());
				pathSelection.setUsername(currentD2D.getDesUsername());
				pathSelection.setPassword(currentD2D.getDesPassword());
			}
		}
		
		this.currentD2D = currentD2D;
		grid.reconfigure(d2dStore, cModel);
	}
	
	public BackupRPSDestSettingsModel saveData() {
		if(selectRPS.getValue())
			return panel.saveData();
		else
			return null;
	}
	
	public void onPolicySelectionChanged(RpsPolicy4D2DRestoreModel policy) {
		boolean policyChanged = true;
		if(currentDataStore != null && currentDataStore.equals(policy.getDataStoreName()))
			policyChanged = false;
		currentPolicy = policy;
		currentDataStore = policy.getDataStoreName();
//		d2dList.clear();
		d2dStore.removeAll();
		allD2ds.clear();
		if(policy.getDataStoreStatus() != RestoreSourcePanel.DATASTORE_ABNORMAL_RESTORE_ONLY
				&& policy.getDataStoreStatus() != RestoreSourcePanel.DATASTORE_RUNNING){
			Utils.popupError(UIContext.Constants.datastoreStatusWrong4DatastoreChange(), UIContext.productNameD2D);
		}else {
			if(policy.d2dList != null && policy.d2dList.size() > 0){
				d2dStore.add(policy.d2dList);
				allD2ds.addAll(policy.d2dList);
				if(policyChanged || currentD2D == null){
					currentD2D = d2dStore.getAt(0);				
					loader.load(0, PAGESIZE);
				}else {
					for(BackupD2DModel d2d : policy.d2dList){
						if(d2d.getHostName().equalsIgnoreCase(currentD2D.getHostName())){
							currentD2D = d2d;
						}
					}
					if(!initialized){
						loader.load(0, PAGESIZE);
					}
				}
				initialized = true;
				gridSelectionModel.select(currentD2D, false);
			}
		}
	}
	
	public RpsPolicy4D2DRestoreModel getSelectPolicy(){
		return currentPolicy;
	}
	
	public BackupD2DModel getSelectedD2D() {
		if(!selectRPS.getValue()){
			currentD2D = new BackupD2DModel();
			currentD2D.setDestination(pathSelection.getDestination());
			currentD2D.setDesUsername(pathSelection.getUsername());
			currentD2D.setDesPassword(pathSelection.getPassword());
		}else{
			currentD2D = gridSelectionModel.getSelectedItem();
		}
		return currentD2D;
	}
	
	public String getRPSHostName(){
		return panel.getRPSHost();
	}
	
	public void clearD2DList(){
//		d2dList.clear();
		allD2ds.clear();
		d2dStore.removeAll();
	}
	
	private PagingLoadResult<BackupD2DModel> loadD2D(PagingLoadConfig conf) {
		int start = conf.getOffset();
		int end = start + conf.getLimit();
		end = end > allD2ds.size() ? allD2ds.size() : end; 
		
		List<BackupD2DModel> models = this.allD2ds.subList(start, end);
		
		PagingLoadResult<BackupD2DModel> result = 
			new BasePagingLoadResult<BackupD2DModel>(models, start, allD2ds.size());
		
		return result;
	}
	
	public boolean isBackupToDataStore(){
		return selectRPS.getValue();
	}
	
	private void validateSharedFolder() {
		if(pathSelection.getDestination() == null || pathSelection.getDestination().isEmpty()){
			panel.popupError(UIContext.Constants.backupSettingsDestinationCannotBeBlank());
			return;
		}
		this.mask(UIContext.Constants.validating());
		commonService.validateSource(pathSelection.getDestination(), pathSelection.getDomain(), 
				pathSelection.getUserWithoutDomain(), pathSelection.getPassword(), 
				PathSelectionPanel.RESTORE_MODE, new BaseAsyncCallback<Long>() {			

			@Override
			public void onFailure(Throwable caught) {
				if(((BusinessLogicException)caught).getErrorCode().equals(PathSelectionPanel.VALIDATE_SOURCE_FAIL)){
					commonService.getDestDriveType(pathSelection.getDestination(), new BaseAsyncCallback<Long>()
					{
							@Override
							public void onFailure(Throwable caught) {
								thisWindow.unmask();
								super.onFailure(caught);
							}
							@Override
							public void onSuccess(Long result) {
								thisWindow.unmask();
								if(result == PathSelectionPanel.REMOTE_DRIVE){
									showUsernamePasswordDialog();
								}
						}
					}
					);
				}else {
					thisWindow.unmask();
					super.onFailure(caught);
				}
			}

			@Override
			public void onSuccess(Long result) {
				thisWindow.unmask();
				hide(ok);
			}
		});
	}
	
	private void showUsernamePasswordDialog()
	{	
		final UserPasswordWindow dlg = new UserPasswordWindow(pathSelection.getDestination(), 
					pathSelection.getUsername(), pathSelection.getPassword());		
		dlg.setModal(true);
		
		dlg.addWindowListener(new WindowListener()
		{				
			public void windowHide(WindowEvent we) {
				//Only do this on ok
				if (dlg.getCancelled() == false)
				{	
					pathSelection.setUsername(dlg.getUsername());
					pathSelection.setPassword(dlg.getPassword());					
				}
			}
		});
		dlg.show();
	}
}
