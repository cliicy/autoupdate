package com.ca.arcflash.ui.client.restore;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.FlashCheckBox;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.CatalogItemModel;
import com.ca.arcflash.ui.client.model.CatalogModelType;
import com.ca.arcflash.ui.client.model.DestType;
import com.ca.arcflash.ui.client.model.FileSystemOptionModel;
import com.ca.arcflash.ui.client.model.RecoveryPointModel;
import com.ca.arcflash.ui.client.model.RestoreArchiveJobModel;
import com.ca.arcflash.ui.client.model.SearchContextModel;
import com.ca.arcflash.ui.client.model.SearchResultModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.core.FastMap;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridGroupRenderer;
import com.extjs.gxt.ui.client.widget.grid.GroupColumnData;
import com.extjs.gxt.ui.client.widget.grid.GroupingView;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.IncrementalCommand;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;

public class RestoreSearchResultPanel extends LayoutContainer implements
		RestoreValidator {
	
	private LoginServiceAsync service = GWT.create(LoginService.class);
	
	private List<CatalogItemModel> archiveItems = new ArrayList<CatalogItemModel>();
	private List<CatalogItemModel> backupItems = new ArrayList<CatalogItemModel>();
	
	private RestoreWizardContainer wizard;
	private RestoreSearchResultPanel thisPanel;
	private RestoreSearchPanel searchPanel;
	private static int MIN_WIDTH = 90;
	private TextField<String> findText;
	private TextField<String> findPath;
	private Button findButton;
	private int limit = 100;
	
	private LayoutContainer findContainer;
	private Image busyImage = Utils.getBusyImage();
	private Grid<CatalogItemModel> grid;
	private ColumnConfig itemLocColumn = new ColumnConfig();
	private CheckBox includeSubdirectories;

	private GroupingStore<CatalogItemModel> store;
	private List<CatalogItemModel> backupModel;
	private boolean searchingState = true;

	private ColumnModel findColumnModel;
	
	private static int QJDTO_R_INCLUDE_SUBDIRECTORIES = 0x00000001;
	private static int QJDTO_R_CASE_SENSITIVE_SEARCH = 0x00000002;
	
	private Map<FlashCheckBox, CatalogItemModel> table = new HashMap<FlashCheckBox, CatalogItemModel>();
	private Map<FlashCheckBox, CatalogItemModel> table_ArchiveNodes = new HashMap<FlashCheckBox, CatalogItemModel>();
	private Map<String, ArrayList<FlashCheckBox>> fcbMap = new FastMap<ArrayList<FlashCheckBox>>();
	private Map<CatalogItemModel, FlashCheckBox> fcbCache = new HashMap<CatalogItemModel, FlashCheckBox>();
	
	private boolean isWndClosed = false;
	private boolean searchFinished = false;
	private SearchContextModel searchContext;
	private LabelField searchLabel = new LabelField();
	
	public RestoreSearchResultPanel(RestoreWizardContainer restoreWizard) {
		wizard = restoreWizard;
		searchPanel = wizard.restoreSearchPanel;
		thisPanel = this;
		
		wizard.addListener(Events.Hide, new Listener<BaseEvent>(){

			@Override
			public void handleEvent(BaseEvent be) {
				isWndClosed = true;
				
			}

		});
		
		wizard.addListener(Events.Show, new Listener<BaseEvent>(){

			@Override
			public void handleEvent(BaseEvent be) {
				isWndClosed = false;
			}
		});
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		this.setHeight("100%");
		this.setWidth("95%");
		this.setScrollMode(Scroll.AUTOY);
		RowLayout rl = new RowLayout();
		this.setLayout(rl);
		
		add(renderHeaderSection());
		
		RowData data = new RowData();
		data.setMargins(new Margins(5, 0, 0, 0));
		LabelField findLabel = new LabelField();
		findLabel.addStyleName("restoreWizardSubItem");
		findLabel.setValue(UIContext.Constants.restorePath());
		add(findLabel, data);
		
		add(renderFindSection());

		LabelField selectLabel = new LabelField();
		selectLabel.addStyleName("restoreWizardSubItem");
		selectLabel.setValue(UIContext.Constants.restoreVersion());
		this.add(selectLabel);

		add(renderTableSection());
		
		searchLabel.setStyleAttribute("color", "#0011F0");
		searchLabel.setWidth("95%");
		add(searchLabel);
	}

	private LayoutContainer renderHeaderSection() {
		LayoutContainer container = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(2);
		container.setLayout(tl);

		TableData td = new TableData();
		td.setWidth("5%");

		Image image = AbstractImagePrototype.create(UIContext.IconBundle.restore_search()).createImage();
		container.add(image, td);

		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.restoreFind());
		label.setStyleName("restoreWizardTitle");
		container.add(label);

		return container;
	}
	
	private LayoutContainer renderFindSection() {
		findContainer = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(4);
		tl.setCellPadding(2);
		tl.setCellSpacing(2);
		tl.setWidth("98%");
		findContainer.setLayout(tl);
		
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.restoreNamePattern());
		
		TableData td = new TableData();
		td.setWidth("30%");
		
		findContainer.add(label,td);

		findText = new TextField<String>();
        findText.ensureDebugId("D126BC25-7370-4ba3-BD0D-30D0E8F0BE9D");
		Utils.addToolTip(findText, UIContext.Constants.restoreNameFileNameTooltip());
		findText.setWidth(320);
		findText.setAllowBlank(false);
		Validator v = new Validator() {
			@Override
			public String validate(Field<?> field, String value) {
				if (value != null) {
					value = value.trim();
					if (fullQuery(value)) {
						return UIContext.Constants
								.restoreSearchNotAllowedPattern();
					}
				}
				return null;
			}
			
			private native boolean fullQuery(String value) /*-{
				var pt = /^(\*)+$|^(\*+\.\*+)$/;
				return pt.test(value);
			}-*/;
		};

		findText.setValidator(v);
		td = new TableData();
		td.setWidth("50%");
		td.setColspan(3);
		findContainer.add(findText, td);

		findButton = new Button();
		findButton.ensureDebugId("49F03813-FD7F-4ee4-819F-7D84E452562E");
		findButton.setMinWidth(MIN_WIDTH);
		//findButton.setStyleAttribute("margin", "10px" );
		setupFindButton();
		
		label = new LabelField();
		label.setValue(UIContext.Constants.restoreSearchPath());
		
		TableData td1 = new TableData();
		td1.setWidth("30%");		
		
		findContainer.add(label,td1);
		
		td1 = new TableData();
		td1.setWidth("50%");
		findPath = new TextField<String>();
		findPath.ensureDebugId("A2EF1EBA-4F5A-498b-897C-1FCCC6F3C54F");
		Utils.addToolTip(findPath, UIContext.Constants.restoreNameSearchPathTooltip());
		findPath.setWidth(320);
		//findPath.setValidator(v);
		findContainer.add(findPath,td1);
		
		td1 = new TableData();
		td1.setWidth("10%");
		findContainer.add(findButton,td1);

		td1 = new TableData();
		td1.setWidth("5%");
		busyImage.setVisible(false);
		findContainer.add(busyImage);
		
		includeSubdirectories = new CheckBox();
		includeSubdirectories.ensureDebugId("F2E23F41-2DA1-4c6a-8F5C-217030D11D4B");
		Utils.addToolTip(includeSubdirectories, UIContext.Constants.restoreIncludeSubdirectoriesTooltip());
		includeSubdirectories.setBoxLabel(UIContext.Constants
				.restoreIncludeSubdirectories());
		includeSubdirectories.setValue(true);
		
		findPath.addKeyListener(new KeyListener()
		{
			@Override
			public void componentKeyUp(ComponentEvent event)
			{
				super.componentKeyUp(event);
				
				// enable the include sub directories when the path is not empty
				includeSubdirectories.setEnabled(isPathNotEmpty());
			}			
		});
		
		findContainer.add(includeSubdirectories,td1);
		
		return findContainer;
	}
	
	private LayoutContainer renderTableSection() {
		LayoutContainer container = new LayoutContainer();

		TableLayout tl = new TableLayout();
		tl.setCellSpacing(4);
		tl.setCellPadding(4);
		container.setLayout(tl);

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig();
		column.setId("name");
		column.setHeaderHtml(UIContext.Constants.restoreNameColumn());
		column.setWidth(150);
		column.setMenuDisabled(true);
		column.setRenderer(new GridCellRenderer<CatalogItemModel>()
		{
			@Override
			public Object render(final CatalogItemModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<CatalogItemModel> store,
					Grid<CatalogItemModel> grid) {
				LayoutContainer lc = new LayoutContainer();
				lc.setLayout(new ColumnLayout());
				
				FlashCheckBox box = fcbCache.get(model);
				if(box == null) {
					box = new FlashCheckBox();
					fcbCache.put(model, box);
				}
				final FlashCheckBox fcb = box;				
				fcb.addSelectionListener(new SelectionListener<IconButtonEvent>(){
					@Override
					public void componentSelected(IconButtonEvent ce) {
						if (fcb.getSelectedState() == FlashCheckBox.FULL)
						{
							setSelectedVersionOfOtherItemsinGroup(true,model,fcb);
							
							if(model.getFoundInType() == CatalogItemModel.TYPE_BACKUP)
								table.put(fcb, model);
							else if(model.getFoundInType() == CatalogItemModel.TYPE_ARCHIVE)
								table_ArchiveNodes.put(fcb, model);
						}
						else
						{
							setSelectedVersionOfOtherItemsinGroup(false,model,fcb);
							
							if(model.getFoundInType() == CatalogItemModel.TYPE_BACKUP)
								table.remove(fcb);
							else if(model.getFoundInType() == CatalogItemModel.TYPE_ARCHIVE)
								table_ArchiveNodes.remove(fcb);
						}
					}
					
					private void setSelectedVersionOfOtherItemsinGroup(boolean bChecked,CatalogItemModel model,FlashCheckBox fcb) 
					{
						ArrayList<FlashCheckBox> fcbListTemp = fcbMap.get(model.getFullPath());
						for (FlashCheckBox fcbTemp : fcbListTemp) {
							if(fcbTemp != fcb)
							{
								fcbTemp.setEnabled(!bChecked);
							}
						}
					}
				});
				
				
				if(fcbMap.containsKey(model.getFullPath()))
				{
					ArrayList<FlashCheckBox> fcbListTemp = fcbMap.get(model.getFullPath());
					if(!fcbListTemp.contains(fcb))
					{
						fcbListTemp.add(fcb);
						fcbMap.put(model.getFullPath(),fcbListTemp);
					}
				}
				else
				{
					ArrayList<FlashCheckBox> fcbListTemp = new ArrayList<FlashCheckBox>();
					fcbListTemp.add(fcb);
					fcbMap.put(model.getFullPath(), fcbListTemp);
				}
				
				if(model.getVolAttr()!=null){
					int volAttr = model.getVolAttr();
					if((volAttr & RecoveryPointsPanel.RefsVol) > 0){
						if(!UIContext.serverVersionInfo.isWin8()){
							fcb.setToolTip(getToolTip(volAttr));
							fcb.setEnabled(false);
						}
					}else if(((volAttr & RecoveryPointsPanel.NtfsVol) > 0 && (volAttr & RecoveryPointsPanel.DedupVol) > 0)){
						if(!UIContext.serverVersionInfo.isWin8()){
							fcb.setToolTip(getToolTip(volAttr));
							fcb.setEnabled(false);
						}else{
							if(!UIContext.serverVersionInfo.isDedupInstalled()){
								fcb.setToolTip(getToolTip(volAttr));
								fcb.setEnabled(false);
							}
						}
					}
				}
				
				lc.add(fcb);
				
				GWT.log(String.valueOf(model.getType()), null);
				
				if (model.getType() == CatalogModelType.Folder)
					lc.add(AbstractImagePrototype.create(UIContext.IconBundle.folder()).createImage());
				else if (model.getType() == CatalogModelType.File)
					lc.add(AbstractImagePrototype.create(UIContext.IconBundle.file()).createImage());
				else if (model.getMsgRecModel() != null
						&& model.getMsgRecModel().getMsgRec() != null) {
					Long type = model.getMsgRecModel().getMsgRec().getObjType();
					if (type != null
							&& type.longValue() == CatalogModelType.OT_GRT_EXCH_MESSAGE) {
						lc.add(AbstractImagePrototype.create(UIContext.IconBundle.exchGRTMsg()).createImage());
					}
				}
				LabelField lf = new LabelField();
				lf.setValue(model.getName());
				lc.add(lf);
				
				return lc;
			}
			
		});
		configs.add(column);

		column = new ColumnConfig();
		column.setId("path");
		column.setHeaderHtml(UIContext.Constants.restorePathColumn());
		//column.setWidth(200);
		column.setHidden(true);
		column.setMenuDisabled(true);
		configs.add(column);

		column = new ColumnConfig();
		column.setId("size");
		column.setHeaderHtml(UIContext.Constants.restoreSizeColumn());
		column.setWidth(100);
		column.setAlignment(HorizontalAlignment.RIGHT);
		column.setMenuDisabled(true);
		column.setRenderer(new GridCellRenderer<CatalogItemModel>() {

			@Override
			public Object render(final CatalogItemModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<CatalogItemModel> store,
					Grid<CatalogItemModel> grid) {
				try {
					if (model != null && (model.getType() == CatalogModelType.File || model.getSize() > 0)) {
						String formattedValue = Utils.bytes2String(((CatalogItemModel) model).getSize());
						return formattedValue;
					}
				} catch (Exception e) {

				}

				return "";
			}

		});
		configs.add(column);

		column = new ColumnConfig();
		column.setId("fullPath");
		column.setHidden(true);
		column.setMenuDisabled(true);
		configs.add(column);

		column = new ColumnConfig();
		column.setId("date");
		column.setHeaderHtml(UIContext.Constants.restoreBackupDateColumn());
		column.setWidth(150);
		column.setMenuDisabled(true);
		column.setRenderer(new GridCellRenderer() {
			@Override
			public Object render(ModelData model, String property,
					com.extjs.gxt.ui.client.widget.grid.ColumnData config,
					int rowIndex, int colIndex, ListStore store, Grid grid) {
				try {
					if (model != null) {
						return Utils.formatDateToServerTime(((CatalogItemModel) model).getDate());
					}
				} catch (Exception e) {

				}
				return "";
			}
		});
		configs.add(column);
		
		column = new ColumnConfig();
		column.setId("machineId");
		column.setHeaderHtml(UIContext.Constants.restoreMachineName());
		column.setWidth(150);
		column.setMenuDisabled(true);
		column.setRenderer(new GridCellRenderer<CatalogItemModel>() {

			@Override
			public Object render(final CatalogItemModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<CatalogItemModel> store,
					Grid<CatalogItemModel> grid) {
				
				return "";
			}

		});
		column.setHidden(true);
		configs.add(column);

		itemLocColumn = new ColumnConfig();
		itemLocColumn.setId("itemLocation");
		itemLocColumn.setHeaderHtml(UIContext.Constants.restoreItemLocation());
		itemLocColumn.setWidth(120);
		itemLocColumn.setMenuDisabled(true);
		itemLocColumn.setRenderer(new GridCellRenderer<CatalogItemModel>() {

			@Override
			public Object render(final CatalogItemModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<CatalogItemModel> store,
					Grid<CatalogItemModel> grid) {
				
				if(model.getFoundInType() ==  CatalogItemModel.TYPE_BACKUP)
				{
					return UIContext.Constants.jobMonitorTypeBackup();
				}
				else if(model.getFoundInType() ==  CatalogItemModel.TYPE_ARCHIVE)
				{
					return UIContext.Constants.ArchiveFilesLabel();
				}
				return "";
			}

		});
//		column.setHidden(true);
		configs.add(itemLocColumn);
		
		findColumnModel = new ColumnModel(configs);

		store = new GroupingStore<CatalogItemModel>();
		store.groupBy("fullPath");

		GroupingView view = new GroupingView();
		view.setShowGroupedColumn(false);
		view.setGroupRenderer(new GridGroupRenderer() {
			public String render(GroupColumnData data) {
				String path = data.group;
				// String parsing to get it in the right format
				if (path.startsWith("\\\\")) {
					// Fix the format of the path
					int pos = path.indexOf("\\", 4);
					path = path.substring(pos + 1);
				}
				
				return path;
			}
		});

		grid = new Grid<CatalogItemModel>(store, findColumnModel);
		grid.setAutoExpandColumn("name");
		grid.setBorders(true);
		grid.setStripeRows(true);
		grid.setWidth(650);
		grid.setHeight(330);
		grid.setView(view);

		//TableData tdGrid = new TableData();
		//tdGrid.setWidth("100%");
		container.add(grid);

		return container;
	}
	
	private String getToolTip(Integer volAttr){
		String message = null;
		if(volAttr!=null ){
			if((volAttr & RecoveryPointsPanel.RefsVol) > 0){
				if(!UIContext.serverVersionInfo.isWin8()){
					message = UIContext.Messages.restoreSearchSourceIsRefsAndNotWin8(UIContext.serverVersionInfo.getOsName());
				}
			}else if(((volAttr & RecoveryPointsPanel.NtfsVol) > 0 && (volAttr & RecoveryPointsPanel.DedupVol) > 0)){
				if(!UIContext.serverVersionInfo.isWin8()){
					message = UIContext.Messages.restoreSearchSourceIsNtfsDedupAndNotWin8(UIContext.serverVersionInfo.getOsName());
				}else{
					if(!UIContext.serverVersionInfo.isDedupInstalled()){
						message = UIContext.Messages.restoreSearchSourceIsNtfsDedupAndDedupNotInstall();
					}
				}
			}
		}
		return message;
	}
	
	protected void updatePanelForSearch(boolean searchStart) {

		this.busyImage.setVisible(searchStart);

		if (searchStart) {
			setupCancelButton();
		} else {
			setupFindButton();
		}

		includeSubdirectories.setEnabled(!searchStart && isPathNotEmpty());
		findText.setEnabled(!searchStart);
		findPath.setEnabled(!searchStart);
		if(searchStart) {
			grid.mask();
			String searchTxt="";
			if(searchPanel.bSearchBackups){
				if(searchPanel.isSearchInAll()) {
					searchTxt=UIContext.Messages.restoreSearchFindAll(findText.getValue());
				}else {
					int len = searchPanel.getSelectRecoveryPoint().size();
					Date startTime = Utils.localTimeToServerTime(searchPanel.getSelectRecoveryPoint().get(0).getTime(),
							searchPanel.getSelectRecoveryPoint().get(len-1).getTimeZoneOffset());
					Date endTime = Utils.localTimeToServerTime(searchPanel.getSelectRecoveryPoint().get(len -1).getTime(),
							searchPanel.getSelectRecoveryPoint().get(0).getTimeZoneOffset());
					searchTxt=UIContext.Messages.restoreSearchFindInSession(findText.getValue(), 
							len, Utils.formatDate(startTime),Utils.formatDate(endTime));
				}
			}
			if(searchPanel.bSearchArchives){
				if(searchPanel.bSearchBackups){
					searchTxt+="<br>";
				}
				searchTxt+=UIContext.Messages.restoreSearchFindFileCopyLocation(findText.getValue(),searchPanel.getArchiveDestination());
			}
			searchLabel.setValue(searchTxt);
			searchLabel.show();
		}else {
			searchLabel.hide();
			if(grid.isMasked())
				grid.unmask();
		}

		this.wizard.prevButton.setEnabled(!searchStart);
	}

	public void setupCancelButton() {
		findButton.setText(UIContext.Constants.btnStopLabel());
		findButton.removeAllListeners();
		findButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// Cancel the search
				String productName = UIContext.productNameD2D;
				if(UIContext.uiType == 1){
					productName = UIContext.productNamevSphere;
				}
				MessageBox msg = new MessageBox();
				msg.setIcon(MessageBox.INFO);
				msg.setTitleHtml(UIContext.Messages.messageBoxTitleInformation(productName));
				msg.setMessage(UIContext.Constants.restoreFindCancelled());
				msg.setModal(true);
		        Utils.setMessageBoxDebugId(msg);
				msg.show();

				searchingState = false;
				updatePanelForSearch(false);
			}

		});
	}

	public void setupFindButton() {
		findButton.setText(UIContext.Constants.restoreFindButton());
		Utils.addToolTip(findButton, UIContext.Constants.restoreFindButtonTooltip());
		findButton.removeAllListeners();		
		findButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				searchingState = true;
				String path = findPath.getValue();
				String fileName = findText.getValue();
				
				if (fileName == null || fileName.length() == 0 || fileName.trim().isEmpty()) {
					String productName = UIContext.productNameD2D;
					if(UIContext.uiType == 1){
						productName = UIContext.productNamevSphere;
					}
					MessageBox msg = new MessageBox();
					msg.setIcon(MessageBox.ERROR);
					msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(productName));
					msg.setMessage(UIContext.Constants
							.restoreFindPathCannotBeBlank());
					msg.setModal(true);
                    Utils.setMessageBoxDebugId(msg);
					msg.show();
					return;
				}

				
				if (!findText.validate() || !findPath.validate()) {
					return;
				}

				// Disable the textfields until the search completes
				updatePanelForSearch(true);
				//clearing previously selected files
				table.clear();
				table_ArchiveNodes.clear();
				store.removeAll();
				backupModel = null;
				backupItems.clear();
				archiveItems.clear();
				fcbCache.clear();
				
				if(searchPanel.isSearchBackup())
				{	
					String destination = searchPanel.getSessionPath();
					//may not need this validation now, it's validated in the previous page
					if (destination == null || destination.length() == 0) {
						String productName = UIContext.productNameD2D;
						if(UIContext.uiType == 1){
							productName = UIContext.productNamevSphere;
						}
						MessageBox msg = new MessageBox();
						msg.setIcon(MessageBox.ERROR);
						msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(productName));
						msg.setMessage(UIContext.Constants
								.restoreDestinationPathCannotBeBlank());
						msg.setModal(true);
                        Utils.setMessageBoxDebugId(msg);
						msg.show();
						return;
					}
					
					if(path != null && (path.endsWith("\\") || path.endsWith("/"))) {
						path = path.substring(0, path.length() - 1);
					}
					RecoveryPointModel[] models = searchPanel.isSearchInAll() ? new RecoveryPointModel[0] 
					               : searchPanel.getSelectRecoveryPoint().toArray(new RecoveryPointModel[0]); 
					service.openSearchCatalog(models,destination, path, false, includeSubdirectories.getValue(), 
							fileName, "",
							searchPanel.getUserName(), searchPanel.getPassword(),
							searchPanel.getEncryptedPwd().keySet().toArray(new String[0]), 
							searchPanel.getEncryptedPwd().values().toArray(new String[0]),
							new BaseAsyncCallback<SearchContextModel>() {
								@Override
								public void onFailure(Throwable caught) {
									if (!isWndClosed && searchingState) {
										super.onFailure(caught);
										store.removeAll();
										fireSearchedDataChanged(0);
										updatePanelForSearch(false);
									}
								}

								@Override
								public void onSuccess(SearchContextModel result) {
									searchContext = result;
									if (!isWndClosed) {
										if(searchingState)
											thisPanel.Populate(result);
									} else {
										thisPanel.closeSearchContext(result);
									}
								}
							});
					
				}
				else if(searchPanel.isSearchArchive())
				{
					if(searchingState)
						thisPanel.Populate(null);
				}	
			}
		});
	}
	
	private void closeSearchContext(SearchContextModel contextResult) {
		service.closeSearchCatalog(contextResult, new BaseAsyncCallback<Integer>() {
		});
		
		searchContext = null;
	}
	
	private void fireSearchedDataChanged(Integer size) {        ///D2D Lite Integration
		AppEvent event = new AppEvent(RestoreWizardContainer.onRestoreDateChanged, size);
		event.setSource(RestoreWizardContainer.PAGE_RESTORE_SEARCH_RESULT);
		fireEvent(RestoreWizardContainer.onRestoreDateChanged, event);
	}
	
	protected void Populate(final SearchContextModel contextResult) {
		fireSearchedDataChanged(0);
		searchFinished = false;
		table = new HashMap<FlashCheckBox, CatalogItemModel>();
		final HashMap<String, CatalogItemModel> cache = new HashMap<String, CatalogItemModel>();
		store.setSortField("date");
		store.setSortDir(SortDir.DESC);
		final boolean bSearchBackups = searchPanel.isSearchBackup();
		
		if(bSearchBackups)
		{
			contextResult.setExcludeFileSystem(false);
	
			int searchkind = 0;
			searchkind += 1;
			contextResult.setCurrKind(1);
			contextResult.setSearchkind(searchkind);
		}
		
		IncrementalCommand iCommand = new IncrementalCommand() {
			@Override
			public boolean execute() {
				if (!searchRunning) {
					searchRunning = true;
					if (!searchingState || searchFinished) {
						
						if(!searchingState) {
							
							if(bSearchBackups)
								closeSearchContext(contextResult);							
						}
						
						return false;
					}else {
						if(bSearchBackups && !searchingBackupsCompleted)
						{
							searchBackup();
						}
						else
						{
							searchArchive();
						}
						return true;
					}
				}else {
					return true;
				}
			}
			

			private boolean searchRunning = false;
			private boolean searchingBackupsCompleted = false;
			private boolean searchingArchivesCompleted = false;
			private int limit = 100;
			boolean isExceedLimit = false;
			final String timeoutError = "4294967303";
			
			private void searchFailed(String errMsg, Throwable caught) {
				if(!searchingState) {
					searchRunning = false;
					return;
				}
				searchFinished = true;
				
				if(caught instanceof BusinessLogicException) {
					BusinessLogicException bl = (BusinessLogicException)caught;
					if(bl.getErrorCode().equals(timeoutError)){
						errMsg = UIContext.Constants.restoreSearchTimeOut();						
					}else if(bl.getDisplayMessage() != null && !bl.getDisplayMessage().isEmpty()){
						errMsg = ((BusinessLogicException) caught).getDisplayMessage();
					}
				}
				
				if (!isWndClosed) {
					String productName = UIContext.productNameD2D;
					if(UIContext.uiType == 1){
						productName = UIContext.productNamevSphere;
					}
					
					MessageBox msg = new MessageBox();
					msg.setIcon(MessageBox.ERROR);
					msg.setTitleHtml(UIContext.Messages.messageBoxTitleInformation(productName));
					msg.setMessage(errMsg);
                    msg.setModal(true);
                    Utils.setMessageBoxDebugId(msg);
					msg.show();
				}
				// Done searching close the context
				thisPanel.closeSearchContext(contextResult);
				thisPanel.updatePanelForSearch(false);
//				loadSearchedData();
			}
			
			private void searchBackup() {
				service.searchNext(contextResult,new BaseAsyncCallback<SearchResultModel>() 
				{
					@Override
					public void onFailure(Throwable caught) {
						searchFailed(UIContext.Constants.restoreOpenSearchCatalogError(), caught);
						searchingBackupsCompleted = true;
						searchRunning = false;
					}
					
					@Override
					public void onSuccess(SearchResultModel result) {
						searchRunning = false;
						if(!searchingState) {
							return;
						}
						
						if(result == null) {
							searchingBackupsCompleted = true;
							if(isWndClosed) {
								thisPanel.closeSearchContext(contextResult);
								searchFinished = true;
							}
							return;
						}
						
						contextResult.setCurrKind(result.getNextKind());
								
						findItems(result.listOfItems, backupItems, CatalogItemModel.TYPE_BACKUP);
									
						if(backupModel.size() == limit) {
							if(result.hasNext()){
								isExceedLimit = true;
							}
						}

						// Get the next results
						if ((!result.hasNext() && result.getNextKind() == 0)|| backupModel.size() >= limit) {
							searchingBackupsCompleted = true;
							thisPanel.closeSearchContext(contextResult);
						}
						if (isWndClosed) {
							thisPanel.closeSearchContext(contextResult);
							searchingBackupsCompleted = true;
							searchFinished = true;
						}

						searchRunning = false;
					}
								
				});
			}
			
			private void findItems(List<CatalogItemModel> catModels, List<CatalogItemModel> targetModels, int type) {
				if (backupModel == null) {
					backupModel = new ArrayList<CatalogItemModel>();
				}

				List<CatalogItemModel> curUniVerList = new ArrayList<CatalogItemModel>(11);
				if ((catModels != null) && (catModels.size() > 0)) {
					wizard.prevButton.setEnabled(true);
					if(grid.isMasked())
						grid.unmask();
					for (CatalogItemModel item : catModels) {
						String key = item.getFullPath()
								+ "@" + item.getDate()
								+ "@" + item.getType()
								+ "@" + item.getSize();
						if(type == CatalogItemModel.TYPE_ARCHIVE)
								key += "@" + item.getArchiveVersion();
						if (item.getMsgRecModel() != null) {
							key = item.getMsgRecModel().getEdbFullPath()
									+ "\\" + item.getMsgRecModel().getMsgRec().getObjInfo()
									+ "@" + item.getMsgRecModel().getMsgRec().getObjDate();
						}
						
						if (!cache.containsKey(key)) {
							item.setFoundInType(type);//to identify that this file is found in archive's
							cache.put(key, item);
							curUniVerList.add(item);
						}
					}
					if (backupModel.size()
							+ curUniVerList.size() <= limit) {						
						backupModel.addAll(curUniVerList);
						fireSearchedDataChanged(catModels.size());
						store.add(curUniVerList);
						targetModels.addAll(curUniVerList);
					} else {
						isExceedLimit = true;
						int copySize = limit - backupModel.size();
						thisPanel.fireSearchedDataChanged(copySize);
						
						for (int i = 0; i < copySize
								&& i < curUniVerList.size(); i++) {
							backupModel.add(curUniVerList
									.get(i));
							store.add(curUniVerList.get(i));
							targetModels.add(curUniVerList.get(i));
						}
					}
				}
			}
			
			private void searchArchive() {

				if (backupModel == null) {
					backupModel = new ArrayList<CatalogItemModel>();
				}
				String archivePath = searchPanel.getArchiveDestination();
				if(searchPanel.isSearchArchive() && archivePath != null && !(backupModel.size() >= limit))
				{
					String path = findPath.getValue();
					if (path != null && (path.endsWith("\\") || path.endsWith("/"))) {
						path = path.substring(0, path.length() - 1);
					}		
					
					String fileName = findText.getValue();
					if (fileName == null || fileName.length() == 0) {
						String productName = UIContext.productNameD2D;
						if(UIContext.uiType == 1){
							productName = UIContext.productNamevSphere;
						}
						MessageBox msg = new MessageBox();
						msg.setIcon(MessageBox.ERROR);
						msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(productName));
						msg.setMessage(UIContext.Constants
								.restoreFindPathCannotBeBlank());
						msg.setModal(true);
                        Utils.setMessageBoxDebugId(msg);
						msg.show();
						return;
					}
					
					long lSearchOptions = 0;
					
					if(includeSubdirectories.getValue())
						lSearchOptions = lSearchOptions | QJDTO_R_INCLUDE_SUBDIRECTORIES;
					
					service.searchArchiveDestinationItems(searchPanel.getArchiveDestinationModel(),path,lSearchOptions, 
							fileName,0,(limit-backupModel.size()),new BaseAsyncCallback<List<CatalogItemModel>>() {

						@Override
						public void onFailure(Throwable caught) {
							searchFailed(UIContext.Constants.ArchiveRestoreSearchCatalogError(), caught);							
							searchFinished = true;
							searchingArchivesCompleted = true;
							searchRunning = false;
						}

						@Override
						public void onSuccess(List<CatalogItemModel> result) {
							if(!searchingState) {
								searchRunning = false;
								return;
							}
							
							findItems(result, archiveItems, CatalogItemModel.TYPE_ARCHIVE);
														
							if( backupModel.size() >= limit)
								isExceedLimit = true;
							
							searchArchiveFinished();
							
							searchingArchivesCompleted = true;

							if (isWndClosed) {
								thisPanel.closeSearchContext(contextResult);
								searchFinished = true;
							}
						}
					});
				}
				else
				{
					searchArchiveFinished();
				}			
			}
			
			private void searchArchiveFinished() {
				searchRunning = false;
				searchFinished = true;
//				loadSearchedData();
				
				if (!isWndClosed) {
					String productName = UIContext.productNameD2D;
					if(UIContext.uiType == 1){
						productName = UIContext.productNamevSphere;
					}
					MessageBox msgBox = new MessageBox();
					msgBox.setIcon(MessageBox.INFO);
					msgBox.setTitleHtml(UIContext.Messages.messageBoxTitleInformation(productName));
					
					String strMessage = "";
					strMessage += UIContext.Constants.restoreFindCompleted();									
					
					if (store.getCount() == 0 && !isWndClosed) 
					{
						strMessage += " ";
						strMessage += UIContext.Constants.restoreNoResultsFound();
					}
					msgBox.setMessage(strMessage);
					
					msgBox.setModal(true);
					msgBox.show();
					msgBox.addCallback(new Listener<MessageBoxEvent>() {
						@Override
						public void handleEvent(
								MessageBoxEvent be) {
							if(isExceedLimit){
								maxmimumNumExceed();
							}
						}
					});
				}
				thisPanel.updatePanelForSearch(false);
			}
		};

		DeferredCommand.addCommand(iCommand);		
	}

	private void loadSearchedData() {
		if (!isWndClosed) {
			if (backupModel != null && backupModel.size() > 0) {
				store.add(backupModel);
				fireSearchedDataChanged(backupModel.size());
			}
		}
	}
	
	private boolean isPathNotEmpty()
	{
		boolean bIsPathNotEmpty = (findPath != null) 
		                       && (findPath.getValue() != null) 
		                       && (!findPath.getValue().trim().isEmpty());
		
		return bIsPathNotEmpty;
	}
	
	public TextField<String> getFindText()
	{
		return findText;
	}
	
	private void maxmimumNumExceed() 
	{
		if (!isWndClosed) {
			String productName = UIContext.productNameD2D;
			if(UIContext.uiType == 1){
				productName = UIContext.productNamevSphere;
			}
			MessageBox maxNumBox = new MessageBox();
			maxNumBox.setIcon(MessageBox.INFO);
			maxNumBox
					.setTitleHtml(UIContext.Messages.messageBoxTitleInformation(productName));
			maxNumBox
					.setMessage(UIContext.Messages
							.restoreSearchmaxAllowShowItems(String
									.valueOf(limit)));
			maxNumBox.setModal(true);
			maxNumBox.show();
		}
	}
	
	@Override
	public void show() {
		super.show();
//		fireSearchedDataChanged(store == null ? 0 : store.getCount());
	}
	
	private void IsDestinationEncrypted(final AsyncCallback<Boolean> callback)
	{		
		RestoreArchiveJobModel jobModel = getRestoreArchiveJobModel();
		service.ValidateRestoreJob(jobModel,new BaseAsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) 
			{			
				super.onFailure(caught);
				callback.onSuccess(Boolean.FALSE);			
				
			}
			@Override
			public void onSuccess(Boolean result)
			{							
				if(result)
					wizard.setEncrypted(true);
				else
					wizard.setEncrypted(false);
				callback.onSuccess(Boolean.TRUE);	
					
			}
		});
	}
	
	private RestoreArchiveJobModel getRestoreArchiveJobModel()
	{
		RestoreArchiveJobModel jobModel = wizard.getArchiveRestoreJobModel();
		jobModel.setDestType(DestType.OrigLoc.getValue());
		jobModel.setarchiveRestoreDestinationPath("");
		FileSystemOptionModel fileSystemOption = new FileSystemOptionModel();
		fileSystemOption.setOverwriteExistingFiles(false);
		fileSystemOption.setReplaceActiveFiles(false);
		fileSystemOption.setCreateBaseFolder(false);
		fileSystemOption.setRename(false);
		jobModel.setFileSystemOption(fileSystemOption);
		jobModel.setEncrpytionPassword("");	
		if(wizard.restoreType == RestoreWizardContainer.RESTORE_BY_BROWSE_ARCHIVE)
			jobModel.setRestoreType(RestoreWizardContainer.RESTORE_BY_BROWSE_ARCHIVE);
		else if ((wizard.restoreType == RestoreWizardContainer.RESTORE_BY_SEARCH) && (searchPanel.isSearchArchive()))
			jobModel.setRestoreType(RestoreWizardContainer.RESTORE_BY_SEARCH_ARCHIVE);	
		return jobModel;
	}
	
	@Override
	public boolean validate(AsyncCallback<Boolean> callback) {
		HashMap<String, String> map_ValidatingVersions = new HashMap<String, String>();
		
		boolean bFilesSelected = false;
		
		List<CatalogItemModel> allSelectionsBackup = getSelectedBackupNodes();
		if(searchPanel.isSearchBackup())
		{	
			if (allSelectionsBackup.size() != 0){
				bFilesSelected = true;
				
				for (int i = 0; i < allSelectionsBackup.size(); i++) {
					CatalogItemModel temp = allSelectionsBackup.get(i);

					if (map_ValidatingVersions.containsKey(temp.getFullPath())) {
						String productName = UIContext.productNameD2D;
						if(UIContext.uiType == 1){
							productName = UIContext.productNamevSphere;
						}
						MessageBox msg = new MessageBox();
						msg.setIcon(MessageBox.ERROR);
						msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(productName));
						msg.setMessage(UIContext.Constants
								.restoreFindSelectedTwoVersions());
						msg.show();
						callback.onSuccess(Boolean.FALSE);
						return false;
					} else {
						map_ValidatingVersions.put(temp.getFullPath(), temp.getFullPath());
					}
				}
			}
		}
		
		List<CatalogItemModel> allSelectionsArchive = getSelectedArchiveNodes();
		if(searchPanel.isSearchArchive())
		{	
			if (allSelectionsArchive.size() != 0) {
				bFilesSelected = true;

				for (int i = 0; i < allSelectionsArchive.size(); i++) {
					CatalogItemModel temp = allSelectionsArchive.get(i);
					String productName = UIContext.productNameD2D;
					if(UIContext.uiType == 1){
						productName = UIContext.productNamevSphere;
					}
					if (map_ValidatingVersions.containsKey(temp.getFullPath())) {
						MessageBox msg = new MessageBox();
						msg.setIcon(MessageBox.ERROR);
						msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(productName));
						msg.setMessage(UIContext.Constants
								.restoreFindSelectedTwoVersions());
						msg.show();
						callback.onSuccess(Boolean.FALSE);
						return false;
					} else {
						map_ValidatingVersions.put(temp.getFullPath(), temp.getFullPath());
					}
				}
			}
		}
		
		if(bFilesSelected == false)
		{
			final MessageBox errMessage = MessageBox.info(UIContext.Constants
					.restoreFind(), UIContext.Constants
					.restoreSearchMustSelectFiles(), new Listener<MessageBoxEvent>() {

				@Override
				public void handleEvent(MessageBoxEvent be) {
					// don't care about the result
				}
			});
			errMessage.setModal(true);
			errMessage.setIcon(MessageBox.ERROR);
			errMessage.show();
			callback.onSuccess(Boolean.FALSE);
			return false;
		}
		
		if(searchPanel.isSearchArchive() && allSelectionsArchive.size() != 0)
			
		{
			IsDestinationEncrypted(callback);
			/*if(!ArchivePathSelectionWindow.archiveDestination.equals(searchPanel.getArchiveDestination()))
			{
				IsDestinationEncrypted(callback);
			}
			else
			{
				callback.onSuccess(Boolean.TRUE);
			}*/
			
		}
		else
		{
			callback.onSuccess(Boolean.TRUE);
		}
		return false;
	}
	
	public List<CatalogItemModel> getSelectedBackupNodes() {	
		List<CatalogItemModel> items_Backup = new ArrayList<CatalogItemModel>();
		items_Backup.addAll(table.values());
		return items_Backup;
	}
	
	public List<CatalogItemModel> getSelectedArchiveNodes() {	
		List<CatalogItemModel> items_Archive = new ArrayList<CatalogItemModel>();
		items_Archive.addAll(table_ArchiveNodes.values());
		return items_Archive;
	}
	
	public boolean isArchiveSelected()
	{
		if(table_ArchiveNodes != null && table_ArchiveNodes.size() > 0)
		{
			return true;
		}
		return false;
	}
	
	public void nextPage() {/*
		if (!searchingState || searchFinished) {
			if(searchPanel.isSearchBackup() && searchContext != null)
				this.closeSearchContext(searchContext);
			searchTimer.cancel();
		}
	*/
		this.updatePanelForSearch(false);
	}
}
