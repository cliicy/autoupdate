package com.ca.arcflash.ui.client.restore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.FlashCheckBox;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.common.gxtex.GridEx;
import com.ca.arcflash.ui.client.homepage.TaskPanel;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.ArchiveCloudDestInfoModel;
import com.ca.arcflash.ui.client.model.ArchiveDestinationDetailsModel;
import com.ca.arcflash.ui.client.model.ArchiveDestinationModel;
import com.ca.arcflash.ui.client.model.ArchiveDiskDestInfoModel;
import com.ca.arcflash.ui.client.model.ArchiveFileVersionNode;
import com.ca.arcflash.ui.client.model.ArchiveGridTreeNode;
import com.ca.arcflash.ui.client.model.ArchiveRestoreDestinationVolumesModel;
import com.ca.arcflash.ui.client.model.ArchiveSettingsModel;
import com.ca.arcflash.ui.client.model.CatalogModelType;
import com.ca.arcflash.ui.client.model.DestType;
import com.ca.arcflash.ui.client.model.FileSystemOptionModel;
import com.ca.arcflash.ui.client.model.RestoreArchiveJobModel;
import com.extjs.gxt.charts.client.model.ToolTip;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.Loader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.data.ModelProcessor;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;

import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.ListModelPropertyEditor;

import com.extjs.gxt.ui.client.widget.form.ComboBox;

import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.treegrid.EditorTreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridView;
import com.extjs.gxt.ui.client.widget.treegrid.WidgetTreeGridCellRenderer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;


public class RestoreArchiveBrowsePanel extends LayoutContainer implements
		RestoreValidator {
	
	final LoginServiceAsync service = GWT.create(LoginService.class);
	
	private RestoreArchiveBrowsePanel thisPanel = null;
	private final static String DISPLAYFIELD="ARCHIVEDESTINATION";
	
	//left panel
	private HashMap<ArchiveGridTreeNode, ArchiveRestoreDestinationVolumesModel> rootItemMap = new HashMap<ArchiveGridTreeNode, ArchiveRestoreDestinationVolumesModel>();
	private EditorTreeGrid<ArchiveGridTreeNode> archiveBrowseTree;
	private TreeStore<ArchiveGridTreeNode> archiveBrowseTreeStore;
	private ColumnModel archiveBrowseTreeColumns;
	private TreeLoader<ArchiveGridTreeNode> loader;
	
	//right panel
	private Grid<ArchiveFileVersionNode> archiveFileVersionsGrid;
	private ListStore<ArchiveFileVersionNode> archiveFileVersionsStore;
	private ColumnModel archiveFileVersionsColumnsModel;

	private GridCellRenderer<ArchiveFileVersionNode> FileVersionRenderer;
	private GridCellRenderer<ArchiveFileVersionNode> DateRenderer;
	private GridCellRenderer<ArchiveFileVersionNode> sizeRenderer;
	
	private HashMap<ArchiveGridTreeNode, FlashCheckBox> table = new HashMap<ArchiveGridTreeNode, FlashCheckBox>();
	private HashMap<ArchiveFileVersionNode, FlashCheckBox> fileVersionsTable = new HashMap<ArchiveFileVersionNode, FlashCheckBox>();

	private boolean isRestoreManager = true;
	public ArchiveDestinationModel archiveDestinationInfo = null;
	private ArchiveDiskDestInfoModel archiveDiskInfo = new ArchiveDiskDestInfoModel();
	private ArchiveCloudDestInfoModel archiveCloudInfo = null;
	
	public ComboBox<ArchiveDestinationModel> comboArchiveDestination;
	public Button btAddArchiveDestination;
	
	private RestoreWizardContainer wizard;
	public static ArchiveGridTreeNode CurrentSelectedNode;
	private ArchivePathSelectionWindow archivePathSelectionWind = null;
	
	private static Map<String,ArchiveDestinationModel> storelist=new HashMap();
	private  LabelField nodeinfolabel;
	
	static String archiveDestination;
/*	ToolTipConfig tipConfig = null;
	ToolTip tip = null;
	*/
	//private Listener<BaseEvent> restoreArchiveBrowsePanelListener = null;

	
	@Override
	public boolean validate(AsyncCallback<Boolean> callback) {
		List<ArchiveGridTreeNode> selectedNodes = GetSelectedNodes();
		
		//Check 1: No selection
		if (selectedNodes.size() == 0) {
			final MessageBox errMessage = MessageBox.info(UIContext.Constants
					.restoreBrowseArchiveButton(), UIContext.Constants
					.restoreSearchMustSelectFiles(),null);
			errMessage.setModal(true);
			errMessage.setIcon(MessageBox.ERROR);
			Utils.setMessageBoxDebugId(errMessage);			
			errMessage.show();

			callback.onSuccess(Boolean.FALSE);
			return false;
		}
		
		boolean isValid = true;

		if (!isValid) {
			MessageBox msg = new MessageBox();
			msg.setIcon(MessageBox.INFO);
			msg.setTitleHtml(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D));
			msg.setMessage(UIContext.Constants
					.restoreBrowseSelectFilesOrDatabases());
			msg.setModal(true);
			Utils.setMessageBoxDebugId(msg);
			msg.show();
		}
		
		if(ArchivePathSelectionWindow.archiveDestination !=null)
		{
			/*
			 * Note: The reason being commented the below code is: some times user create plan with FC encryption. And then move the node to other plan with same FC dest but no encr pwd.
			 * Since FC PFC is optional, this is needed.
			 * Fix for bug: 761806
			 */
			IsDestinationEncrypted(callback); 
			/*if(!ArchivePathSelectionWindow.archiveDestination.equals(archiveDestination))
			{
				IsDestinationEncrypted(callback);
			}
			else
			{
				callback.onSuccess(isValid);	
			}*/
			
		}
		
		else			
			callback.onSuccess(isValid);			
		return isValid;
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
		else if ((wizard.restoreType == RestoreWizardContainer.RESTORE_BY_SEARCH) && (wizard.restoreSearchPanel.bSearchArchives))
			jobModel.setRestoreType(RestoreWizardContainer.RESTORE_BY_SEARCH_ARCHIVE);		
		return jobModel;
	}
	
	public TreeStore<ArchiveGridTreeNode> getTreeStore() {
		return archiveBrowseTreeStore;
	}
	
	public RestoreArchiveBrowsePanel(RestoreWizardContainer in_restoreWizard) {
		wizard = in_restoreWizard;
		thisPanel = this;
	}
	@Override
	protected void onLoad(){
		comboArchiveDestination.getView().setModelProcessor(new ModelProcessor<ArchiveDestinationModel>() {
			   public ArchiveDestinationModel prepareData(ArchiveDestinationModel model) {
				  
			  model.set(DISPLAYFIELD, model.getArchiveDestination());
			    
			     return model;
			   }
			 });
		comboArchiveDestination.setPropertyEditor(new ListModelPropertyEditor<ArchiveDestinationModel>() {
				
			
			   public String getStringValue(ArchiveDestinationModel model) {
				   String key=model.getArchiveDestination();
				   storelist.put(key, model);
				   return key;
			   }
			   
			   @Override
			   public ArchiveDestinationModel convertStringValue(String key){
				   return storelist.get(key);
			   }
			   
			 });
		
		getDefaultSourceValueForArchiveBrowse();
	}
	
//	private native void log(String message)/*-{
//		console.log(message);
//		alert(message);
//}-*/;

	
	@Override
	protected void onRender(Element parent, int index) {
		
		super.onRender(parent, index);
		
		RowLayout rl = new RowLayout();
		this.setLayout(rl);
		this.setHeight("100%");
		renderArchiveBrowseHeaderSection();
		renderArchiveBrowseTreeSection();

	}

	private void renderArchiveBrowseTreeSection() {
		final LayoutContainer archiveBrowseContainer = new LayoutContainer();
		archiveBrowseContainer.setLayout(new BorderLayout());
		//container.setSize(670, 500);
		archiveBrowseContainer.setHeight("100%");
		//container.setStyleAttribute("padding-top", "15px");
		
		defineArchiveTreeLoader();	
		
		defineArchiveBrowseTree();
	
		//creating grid to show the file verions of each file
		
		defineArchiveFileVersionsPanel();
				
		BorderLayoutData bldN = new BorderLayoutData(LayoutRegion.WEST, 360, 60, 480);
		bldN.setMargins(new Margins(0, 4, 0, 0));		
		archiveBrowseTree.getView().setForceFit(false);
		archiveBrowseContainer.add(archiveBrowseTree,bldN);
		
		BorderLayoutData bldC = new BorderLayoutData(LayoutRegion.CENTER, 200);
		bldC.setMargins(new Margins(0, 8, 0, 0));
		archiveBrowseContainer.add(archiveFileVersionsGrid, bldC);
		archiveBrowseContainer.setStyleName("install_Wizard");
		
		//archiveBrowseContainer.setStyleAttribute("padding-top", "10px");
		this.add(archiveBrowseContainer,new RowData(1,1));
		
	}

	private void defineArchiveFileVersionsPanel() {
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
                if(isValidVersionDetails(model))
                {
                	return Utils.formatDateToServerTime(model.getModifiedTime(),
    						model.getModifiedTZOffset() != null? model.getModifiedTZOffset():0);
                }
                else
                {
                	return "N/A";
                }
				//return Utils.formatDateToServerTime(model.getModifiedTime(),
					//	model.getModifiedTZOffset() != null? model.getModifiedTZOffset():0);
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
                if(isValidVersionDetails(model))
                {
                	return Utils.bytes2String(model.getFileSize());
                }
                else
                {
                	return "N/A";
                }
				//return Utils.bytes2String(model.getFileSize());
			}
		};
		
		List<ColumnConfig> columnConfigs = new ArrayList<ColumnConfig>();
		//columnConfigs.add(Utils.createColumnConfig("fileName", UIContext.Constants.restoreNameColumn(), 150,FileNameRenderer));
		CheckColumnConfig FileVersionscheckColumn = new CheckColumnConfig("checked", "", 40);
		FileVersionscheckColumn.setHidden(true);		
//		ColumnConfig FileName = new ColumnConfig("FileName", UIContext.Constants.restoreNameColumn(), 180);
		ColumnConfig FileName = new ColumnConfig("Version", UIContext.Constants.restoreVersionColumn(),60);
//		FileName.setMenuDisabled(false);
		FileName.setRenderer(new GridCellRenderer<ArchiveFileVersionNode>() {

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
						
						FlashCheckBox parentCheckBox = table.get(CurrentSelectedNode);
						parentCheckBox.setSelectedState(fcb.getSelectedState());
						
						SetSelectableStateofOtherVersions(FileVersionNode,FileVersionNode.getChecked());
						if(CurrentSelectedNode.getSelectedVersion()==-1)
						{	
							enableAllVersionsifNoVersionIsSelected();
						}	
						selectTreeNodeParent(CurrentSelectedNode);
					}

				});
				
				FlashCheckBox temp = fileVersionsTable.get(FileVersionNode);
				if (temp == null) {
					fileVersionsTable.put(FileVersionNode, fcb);
					// Check the parent's status
					FlashCheckBox parentCheckBox = table.get(CurrentSelectedNode);
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
				lf.setValue(model.getVersion().toString());
				lc.add(lf);

				lc.setAutoWidth(true);
				lc.setWidth(40);
				lc.setAutoHeight(false);
				return lc;
			}
		});
		
		columnConfigs.add(FileName);
		//columnConfigs.add(Utils.createColumnConfig("version", UIContext.Constants.restoreVersionColumn(), 50,FileVersionRenderer));
		columnConfigs.add(Utils.createColumnConfig("modifiedDate", UIContext.Constants.restoreDateModifiedColumn(), 120,DateRenderer));
		columnConfigs.add(Utils.createColumnConfig("fileSize", UIContext.Constants.restoreSizeColumn(), 120,sizeRenderer));
		
		archiveFileVersionsColumnsModel = new ColumnModel(columnConfigs);
		
		archiveFileVersionsGrid = new GridEx<ArchiveFileVersionNode>(archiveFileVersionsStore, archiveFileVersionsColumnsModel);
//		archiveFileVersionsGrid.setAutoExpandColumn("Version");
		archiveFileVersionsGrid.setBorders(true);
		archiveFileVersionsGrid.setStripeRows(true);
		archiveFileVersionsGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		//archiveFileVersionsGrid.addListener(Events.RowClick, restoreArchiveBrowsePanelListener);
	}

/**
 * This method is to validate version details w.r.to the default date and size.
 * If the model values equals the default values show values as N/A on UI.
 * Fixed w.r.to the TFS Issue : 760213	
 * Calculating modifiedTime as per webservice backend logic RPSFileCopyUtil - convert2ArchiveCatalogItem()
 * Default time from JNI/C++ is 0 for 0 bytes files.
 * @param model
 * @return
 */
	public boolean isValidVersionDetails(ArchiveFileVersionNode model)
	{
		boolean isValidVersionDtls = true;
		Date modifiedTime = model.getModifiedTime();
		Long size = model.getFileSize();
	    try {
	    	long longModifytime = 0;
			// Filetime Epoch is JAN 01 1601
			// java date Epoch is January 1, 1970
			// so take the number and subtract java Epoch:
			long javaModifyTime = longModifytime - 0x19db1ded53e8000L;
			// convert UNITS from (100 nano-seconds) to (milliseconds)
			javaModifyTime /= 10000;
			// the specified number of milliseconds since the standard base
			// time known as "the epoch", namely January 1, 1970, 00:00:00 GMT.
	         Date defaultVrsnDate = new Date(javaModifyTime);
	         if(defaultVrsnDate.compareTo(modifiedTime) == 0 && size == 0)
	         {
	        	 isValidVersionDtls = false;
	         }
	      } catch (Exception e) {
	    	  e.printStackTrace();
	      }
		return isValidVersionDtls;
	}
	
	private void defineArchiveBrowseTree() {
		
		StoreSorter<ArchiveGridTreeNode> sorter = defineArchiveTreeSorter();
		
		archiveBrowseTreeStore = new TreeStore<ArchiveGridTreeNode>(loader);
		archiveBrowseTreeStore.setStoreSorter(sorter);
				
		CheckColumnConfig checkColumn = new CheckColumnConfig("checked", "", 40);
		checkColumn.setHidden(true);		
		ColumnConfig name = new ColumnConfig("FileName", UIContext.Constants.restoreNameColumn(), 355);
		name.setMenuDisabled(true);

		name.setRenderer(new WidgetTreeGridCellRenderer<ModelData>() {

			@Override
			public Widget getWidget(ModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid) {
				
				LayoutContainer lc = new LayoutContainer();
				
				TableLayout layout = new TableLayout();
				layout.setColumns(3);
				//lc.setHeight(14);
				lc.setLayout(layout);
				lc.setStyleName("ArchiveBrowseTreeStyles");
				final FlashCheckBox fcb = new FlashCheckBox();

				final ArchiveGridTreeNode treeNode = (ArchiveGridTreeNode) model;
				if (treeNode.getSelectable() != null
						&& treeNode.getSelectable() == false) {
					fcb.setEnabled(false);
				} else {
					if (treeNode.getChecked() != null) {
						if (treeNode.getChecked())
							fcb.setSelectedState(FlashCheckBox.FULL);
						else
							fcb.setSelectedState(FlashCheckBox.NONE);

					} else {						
						fcb.setSelectedState(FlashCheckBox.NONE);
					}
				}
				
				fcb.addSelectionListener(new SelectionListener<IconButtonEvent>() {
					@Override
					public void componentSelected(IconButtonEvent ce) {
						if (fcb.isEnabled() == false)
							return;

						if (archiveBrowseTree instanceof ExtEditorTreeGrid<?>) {
							ExtEditorTreeGrid<ArchiveGridTreeNode> extTree = (ExtEditorTreeGrid<ArchiveGridTreeNode>) archiveBrowseTree;
							extTree.getNodeContextMap().remove(treeNode);
						}

						if (!archiveBrowseTree.isExpanded(treeNode)) {
							List<ArchiveGridTreeNode> childNodes = archiveBrowseTree
									.getTreeStore().getChildren(treeNode);
							if (childNodes == null
									|| childNodes.size() == 0) {
								if (archiveBrowseTreeStore.getLoader() != null) {
									archiveBrowseTreeStore.getLoader().loadChildren(
											treeNode);
								}
							}
						}

						selectTreeNodeChildren(treeNode, fcb.getSelectedState(), true);
						highlightRow(treeNode);
						if(fcb.getSelectedState() == FlashCheckBox.FULL)
						{
							if(treeNode != null)
							{	
								treeNode.setChecked(true);
								if(treeNode.getSelectedVersion() == -1)
								{
									treeNode.setSelectedVersion(treeNode.getVersionsCount());
									//treeNode.setSize(treeNod)
									ArchiveFileVersionNode[] versions = treeNode.getfileVersionsList();

									if(versions != null)
										treeNode.setSize(versions[treeNode.getVersionsCount() - 1].getFileSize());

									if(CurrentSelectedNode == treeNode)
									{
										if((CurrentSelectedNode != null) && CurrentSelectedNode.getType() == CatalogModelType.File)
										{
											ArchiveFileVersionNode versionNode = CurrentSelectedNode.getfileVersionsList()[treeNode.getVersionsCount() - 1];
											SetSelectableStateofOtherVersions(versionNode, true);
										}
									}
									else{
										CurrentSelectedNode = treeNode;
										if(CurrentSelectedNode != null)
										{
											PopulateVersionsGrid(CurrentSelectedNode);
										}
									}
								}
							}
						}
						else if(fcb.getSelectedState() == FlashCheckBox.NONE)
						{
							if(treeNode != null)
							{
							treeNode.setChecked(false);
							if(CurrentSelectedNode == treeNode)
							{
								if((CurrentSelectedNode != null) && CurrentSelectedNode.getType() == CatalogModelType.File)
								{
									int versionToUnselect  = 0;
									if(CurrentSelectedNode.getSelectedVersion()!=-1)
										versionToUnselect = CurrentSelectedNode.getSelectedVersion()-1;									
									ArchiveFileVersionNode versionNode = treeNode.getfileVersionsList()[versionToUnselect];								
									//ArchiveFileVersionNode versionNode = treeNode.getfileVersionsList()[CurrentSelectedNode.getSelectedVersion() - 1];
									CurrentSelectedNode.setSelectedVersion(-1);
									SetSelectableStateofOtherVersions(versionNode, false);
								}
								else
								{
									selectTreeNodeChildren(treeNode, fcb.getSelectedState(), true);
								}
							}
							else{
								CurrentSelectedNode = treeNode;
								if(CurrentSelectedNode != null)
								{
									PopulateVersionsGrid(CurrentSelectedNode);
								}
							}
							treeNode.setSelectedVersion(-1);
							enableAllVersionsifNoVersionIsSelected();
							treeNode.setSize(0L);
						}}
					}
				});
				
				FlashCheckBox temp = table.get(treeNode);
				if (temp == null) {
					table.put(treeNode, fcb);
					// Check the parent's status
					ArchiveGridTreeNode parent = archiveBrowseTree.getTreeStore().getParent(treeNode);
					FlashCheckBox parentCheckBox = table.get(parent);
					if (parentCheckBox != null) {
						if (parentCheckBox.getSelectedState() == FlashCheckBox.FULL) {
							fcb.setSelectedState(FlashCheckBox.FULL);
						}
					}
				} else {
					table.remove(treeNode);
					fcb.setSelectedState(temp.getSelectedState());
					fcb.setEnabled(temp.isEnabled());
					table.put(treeNode, fcb);
				}

				if (isRestoreManager) {
					lc.add(fcb);
				}
				
				IconButton image = getNodeIcon(treeNode);
				if(image != null)
				{
					
//					if (treeNode.getType() == CatalogModelType.File)
//					{	
						fileMap.put(image, treeNode);
					image
					.addSelectionListener(new SelectionListener<IconButtonEvent>() {

						@Override
						public void componentSelected(IconButtonEvent ce) {
							IconButton src = ce.getIconButton();
							ArchiveGridTreeNode node = fileMap.get(src);
							CurrentSelectedNode = treeNode;
							if (node != null) {
								//highlightRow(node);
								//if (treeNode.getType() == CatalogModelType.File)
									handleNodeClick(node);
							}
							

						}
					});
//					}
					
					lc.add(image);
				}	
				
				LabelField lf = new LabelField();
				String strFileName = treeNode.getDisplayName();
				
				if(treeNode.getType() == CatalogModelType.File)
				{
					strFileName += "("+Integer.toString(treeNode.getVersionsCount())+")";
				}
				lf.setValue(new Html("<pre style=\"font-family: Tahoma,Arial;font-size: 11px;\">"+strFileName+"</pre>").getHtml());
				ToolTipConfig tooltip=new ToolTipConfig(new Html("<pre style=\"word-wrap:break-word\">"+strFileName+"</pre>").getHtml());
				tooltip.setMaxWidth(400);
				lf.setToolTip(tooltip);
				
				lc.add(lf);

				lc.setAutoWidth(true);
				lc.setAutoHeight(true);
				
				/*if(treeNode.getType() == CatalogModelType.File)
				{
					String strFileVersions = GetFileVersionsList((ArchiveGridTreeNode)model);
					LabelField label = new LabelField(strFileVersions);
					//label.setStyleName("Label_BackGround");
					tipConfig = new ToolTipConfig(strFileVersions);
					tip = new ToolTip(label, tipConfig);
					//tip.setStyleName("Label_BackGround");
					lc.setToolTip(tipConfig);
				}*/
				
				return lc;				
			}
			HashMap<IconButton, ArchiveGridTreeNode> fileMap = new HashMap<IconButton, ArchiveGridTreeNode>();
		});
		archiveBrowseTreeColumns = new ColumnModel(Arrays.asList(checkColumn, name));
		
		archiveBrowseTree = new ExtEditorArchiveTreeGrid<ArchiveGridTreeNode>(archiveBrowseTreeStore, archiveBrowseTreeColumns,table,this.isRestoreManager,thisPanel);
		
		((TreeGridView)archiveBrowseTree.getView()).setRowHeight(23);
		// Remove the default icons
		archiveBrowseTree.setIconProvider(new ModelIconProvider<ArchiveGridTreeNode>() {

			@Override
			public AbstractImagePrototype getIcon(ArchiveGridTreeNode model) {
				return AbstractImagePrototype.create(UIContext.IconBundle.blank());
			}

		});
		archiveBrowseTree.setBorders(true);
//		archiveBrowseTree.setAutoExpandColumn("FileName");
		archiveBrowseTree.setTrackMouseOver(false);
		archiveBrowseTree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		Listener<GridEvent<ArchiveGridTreeNode>> treeListener = new Listener<GridEvent<ArchiveGridTreeNode>>() {

			@Override
			public void handleEvent(GridEvent<ArchiveGridTreeNode> be) {
				ArchiveGridTreeNode treeNodeTemp = be.getModel();				
				handleNodeClick(treeNodeTemp);
			}
		};
		
		archiveBrowseTree.addListener(Events.RowClick, treeListener);
		if (!this.isRestoreManager) {
			GridSelectionModel<ArchiveGridTreeNode> sm = new GridSelectionModel<ArchiveGridTreeNode>();
			sm.setLocked(true);
			archiveBrowseTree.setSelectionModel(sm);
			archiveBrowseTree.setTrackMouseOver(true);
		}
	}

	private StoreSorter<ArchiveGridTreeNode> defineArchiveTreeSorter() {
		StoreSorter<ArchiveGridTreeNode> sorter = new StoreSorter<ArchiveGridTreeNode>(){
			private int fileNameCompare( ArchiveGridTreeNode m1, ArchiveGridTreeNode m2 )
			{
				int r = 0;
				if( m1.getDisplayName() == null )
				{
					if( m2.getDisplayName() == null )
						return 0;
					else
						return -1;
				}
				else
				{
					if( m2.getDisplayName() == null )
						return 1;
					else
					{
						r = m1.getDisplayName().compareToIgnoreCase(m2.getDisplayName());
						if( r == 0 )
							r = m1.getDisplayName().compareTo(m2.getDisplayName());
						return r;
					}
				}
			}
			public int compare(Store<ArchiveGridTreeNode> store, ArchiveGridTreeNode m1, ArchiveGridTreeNode m2, String property) {
				
				if(m1 == null)
					return -1;
				
				if(m2 == null)
					return 1;
				
				if( m1.getType() != m2.getType() )
					return (int)(m1.getType() - m2.getType());
				else if( property == null ){
					return m1.getDisplayName().compareToIgnoreCase(m2.getDisplayName());
				}
				else if( property == "displayName" ){
					return fileNameCompare(m1, m2);
				}
				else if( property == "date" )
				{
					if( m1.getDate() == null)
					{
						if( m2.getDate() == null )
							return fileNameCompare(m1,m2);
						else
							return -1;
					}
					else
					{
						if( m2.getDate() == null )
							return 1;
						else
							return m1.getDate().compareTo(m2.getDate());
					}
				}
				else if( property == "size" ){
					if( m1.getSize() == null )
					{
						if( m2.getSize() == null )
							return fileNameCompare(m1,m2);
						else
							return -1;
					}
					else
					{
						if(m2.getSize() == null)
							return 1;
						else
						{
							if( m1.getSize() == m2.getSize() )
							{
								return fileNameCompare(m1, m2);
							}
							else if( m1.getSize() < m2.getSize() )
								return -1;
							else
								return 1;
						}
					}
				}
				else
					return super.compare(store, m1, m2, property);
			}
		};
		return sorter;
	}

	private void defineArchiveTreeLoader() {
		//creation of tree grid
		RpcProxy<List<ArchiveGridTreeNode>> proxy = new RpcProxy<List<ArchiveGridTreeNode>>() {
			@Override
			protected void load(Object loadConfig,
					AsyncCallback<List<ArchiveGridTreeNode>> callback) {
				
				int iFileType = -1;
				if(loadConfig != null)
				{
					iFileType = ((ArchiveGridTreeNode)loadConfig).getType();
				}
				if(iFileType == CatalogModelType.Folder || iFileType == CatalogModelType.OT_VSS_FILESYSTEM_WRITER)
				{
					thisPanel.mask(UIContext.Constants.loadingIndicatorText());
					service.getArchiveTreeGridChildren(archiveDestinationInfo,(ArchiveGridTreeNode)loadConfig,callback);
				}	
				else if(iFileType == CatalogModelType.File)
					thisPanel.unmask();
			}
		};
		
		loader = new BaseTreeLoader<ArchiveGridTreeNode>(proxy) {
			public boolean hasChildren(ArchiveGridTreeNode parent) {
				
				if(parent != null)
				{
					Integer type = parent.getType();
					if (type != null && type == CatalogModelType.File) {
						return false;
					} else {
						return true;
					}
				}
				else
				{
					return false;
				}
			}
		};	
		
		/*loader.addListener(Loader.BeforeLoad, new Listener<LoadEvent>(){

			@Override
			public void handleEvent(LoadEvent be) {
				thisPanel.mask(UIContext.Constants.loadingIndicatorText());
			}
			});*/
		
		loader.addListener(Loader.Load, new Listener<LoadEvent>(){

			@Override
			public void handleEvent(LoadEvent be) {
				thisPanel.unmask();
			}
			});
	}

	private void renderArchiveBrowseHeaderSection() {
		//Icon and Title
		LayoutContainer container = new LayoutContainer();
		container.setLayout(new TableLayout(2));
		TableData td = new TableData();
		td.setWidth("5%");
		Image image = AbstractImagePrototype.create(UIContext.IconBundle.restore_destination()).createImage();
		container.add(image, td);
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.restoreBrowseFileCopy());
		label.setStyleName("restoreWizardTitle");
		container.add(label);
		
		//Introduction Label
		LayoutContainer introductioncontainer = new LayoutContainer();
		introductioncontainer.setLayout( new TableLayout(1));
		TableData td2 = new TableData();
		td2.setWidth("100%");
		LabelField introduction = new LabelField();
		introduction.setValue(UIContext.Constants.restoreBrowseFileCopyIntroduction());
		introductioncontainer.add(introduction,td2);
		
		//nodeinfo Label
		LayoutContainer nodeinfo = new LayoutContainer();
		nodeinfo.setLayout( new TableLayout(1));
		TableData td3 = new TableData();
		td3.setWidth("100%");
		 nodeinfolabel = new LabelField();
		nodeinfolabel.setValue(UIContext.Constants.BrowseFileCopiesNodeLabel());
		nodeinfolabel.addStyleName("WidgetPaddingLeft");
		nodeinfo.add(nodeinfolabel,td3);

		//Destination
		LayoutContainer lcArchiveDestinationHeader = new LayoutContainer();
		TableLayout tlArchiveDestinationHeader = new TableLayout(3);
		tlArchiveDestinationHeader.setCellHorizontalAlign(HorizontalAlignment.LEFT);
		tlArchiveDestinationHeader.setWidth("100%");
		lcArchiveDestinationHeader.setLayout(tlArchiveDestinationHeader);
		lcArchiveDestinationHeader.setHeight(40);
			
		LabelField lblArchiveBrowseRestore = new LabelField(UIContext.Constants.FileCopyLocation());
		lblArchiveBrowseRestore.setWidth(150);
		lblArchiveBrowseRestore.addStyleName("restoreWizardSubItem");
		lblArchiveBrowseRestore.addStyleName("WidgetPaddingLeft");
		lcArchiveDestinationHeader.add(lblArchiveBrowseRestore);
		
		comboArchiveDestination= new ComboBox<ArchiveDestinationModel>();
	
		comboArchiveDestination.ensureDebugId("97D76BA0-A7F4-409f-825A-5ED46E797CC5");
		comboArchiveDestination.setEditable(false);
		comboArchiveDestination.setWidth(380);

		ListStore<ArchiveDestinationModel> store=new ListStore<ArchiveDestinationModel> ();
		
		comboArchiveDestination.setTemplate(getTemplate(DISPLAYFIELD));
		comboArchiveDestination.setStore(store);
		comboArchiveDestination.setTriggerAction(ComboBox.TriggerAction.ALL);
		comboArchiveDestination.setDisplayField(DISPLAYFIELD);
		
		comboArchiveDestination.addSelectionChangedListener(new SelectionChangedListener<ArchiveDestinationModel>(){
			@Override
			public void selectionChanged(SelectionChangedEvent<ArchiveDestinationModel> se){
				archiveDestinationInfo=se.getSelectedItem();
				UIContext.setCurrentArchiveDestination(archiveDestinationInfo);
				RefreshArchiveDestinationTree();
				if(archiveDestinationInfo.getArchiveToRPS()!=null && archiveDestinationInfo.getArchiveToRPS())
					nodeinfolabel.setValue(UIContext.Constants.BrowseFileCopiesNodeLabel()+": "+archiveDestinationInfo.getRpsHostModel().getHostName());
				else
					nodeinfolabel.setValue(UIContext.Constants.BrowseFileCopiesNodeLabel()+": "+archiveDestinationInfo.getNodeHostname());
			 }
		});

		lcArchiveDestinationHeader.add(comboArchiveDestination);
		
		btAddArchiveDestination = new Button(UIContext.Constants.ArchiveChangeDestinationButton());
		btAddArchiveDestination.setToolTip(UIContext.Constants.ArchiveChangeDestinationButtonTooltip()); // Fix 149278 (Tooltip issue for change button)
		btAddArchiveDestination.ensureDebugId("C0618958-944F-400d-A003-9E9021DE1EF3");
		btAddArchiveDestination.setWidth(90);	
		btAddArchiveDestination.setStyleAttribute("margin-right", "40px");
		btAddArchiveDestination.addListener(Events.Select, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				archivePathSelectionWind = new ArchivePathSelectionWindow();
				archivePathSelectionWind.addWindowListener(new WindowListener(){
				public void windowHide(WindowEvent we) {
					if (archivePathSelectionWind.getCancelled() == false)
					{
						archiveDestinationInfo = archivePathSelectionWind.getArchiveDestinationModel();						
						switch (archivePathSelectionWind.NextAction) {
						case ArchivePathSelectionWindow.ARCHIVE_READ_EXISTING_CATALOG:
						{
							if(!comboArchiveDestination.getView().getStore().contains(archiveDestinationInfo)){
								comboArchiveDestination.getView().getStore().add(archiveDestinationInfo);
								comboArchiveDestination.setValue(archiveDestinationInfo);
								TaskPanel.ManualDestinationCache.put(archiveDestinationInfo.getArchiveDestination(), archiveDestinationInfo);
							}
							break;
						}
						case ArchivePathSelectionWindow.ARCHIVE_SYNC_CATALOG:
							if(!comboArchiveDestination.getView().getStore().contains(archiveDestinationInfo))
								TaskPanel.ManualDestinationCache.put(archiveDestinationInfo.getArchiveDestination(), archiveDestinationInfo);
							wizard.submitD2DArchiveCatalogSyncJob(archiveDestinationInfo);
							break;
						default:
							break;
						}
						
					}
				}
				});
				archivePathSelectionWind.refresh(null);
				archivePathSelectionWind.setModal(true);
				archivePathSelectionWind.show();
			}
		});
		lcArchiveDestinationHeader.add(btAddArchiveDestination);
		this.add(container,new RowData(1,-1));
		this.add(introductioncontainer,new RowData(1,-1));
		
		this.add(lcArchiveDestinationHeader, new RowData(1, -1));
		this.add(nodeinfo,new RowData(1,-1));
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
	/*private void defineRestoreArchiveBrowsePanelListener() {
		restoreArchiveBrowsePanelListener = new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent restoreArchiveBrowseEvent) {
				if(restoreArchiveBrowseEvent.getSource() == archiveBrowseTree)
				{
					
				}
				else if(restoreArchiveBrowseEvent.getSource() == archiveFileVersionsGrid)
				{
					
				}
			}
		};
	}*/

	private void selectTreeNodeChildren(ArchiveGridTreeNode node, int state, boolean updateParent)
	{
		//Select this node
		FlashCheckBox fcb = table.get(node);
		if (fcb != null)
		{
			fcb.setSelectedState(state);
		}
		
		if(node.getType()==CatalogModelType.File)
		{
			selectTreeNodeLeaf(node,state);
		}
		

		/*if (archiveBrowseTree.isExpanded(node))
		{*/

		//Get the children
		List<ArchiveGridTreeNode> childNodes = archiveBrowseTree.getTreeStore().getChildren(node);
		//For each call select Children
		for (int i = 0 ; i < childNodes.size(); i++)
		{
			selectTreeNodeChildren(childNodes.get(i), state, false);

			if( CurrentSelectedNode != null && node.getType()==CatalogModelType.File)
			{
				ArchiveFileVersionNode[] versionNodes = CurrentSelectedNode.getfileVersionsList();
				if(state==FlashCheckBox.NONE)
				{
					if(versionNodes != null && versionNodes.length!=0 )
					{
						for (int j = 0; j < versionNodes.length; j++) {
							ArchiveFileVersionNode versionNode = versionNodes[j];						
							SetSelectableStateofOtherVersions(versionNode, false);
						}
					}
				}
				else if(state==FlashCheckBox.FULL)
				{ 
					if(versionNodes != null && versionNodes.length!=0 )
					{
						boolean versionAlreadySelected = false;
						Iterator iterator = fileVersionsTable.entrySet().iterator();
						while(iterator.hasNext())
						{
							Map.Entry Temp  = (Map.Entry) iterator.next();
							FlashCheckBox fcbTemp = (FlashCheckBox)Temp.getValue();
							if(fcbTemp.getSelectedState()==FlashCheckBox.FULL)
								versionAlreadySelected = true;
						}

						if(!versionAlreadySelected)
						{
							ArchiveFileVersionNode versionNode = CurrentSelectedNode.getfileVersionsList()[CurrentSelectedNode.getVersionsCount() - 1];						
							SetSelectableStateofOtherVersions(versionNode, true);
						}	
					}
				}
			}
		}
		//		} 

		//Set the parent
		if (updateParent)
		{
			selectTreeNodeParent(node);
		}
	}
	
	private void selectTreeNodeLeaf(ArchiveGridTreeNode node, int state)
	{
		ArchiveFileVersionNode[] versionNodes = node.getfileVersionsList();
		if(state==FlashCheckBox.NONE)
		{
			node.setSelectedVersion(-1);
			if(versionNodes != null && versionNodes.length!=0 )
			{
				for (int j = 0; j < versionNodes.length; j++) {
					
					ArchiveFileVersionNode versionNode = versionNodes[j];						
					SetSelectableStateofOtherVersions(versionNode, false);
				}
			}
		}
		else if(state==FlashCheckBox.FULL)
		{ 
			if(versionNodes != null && versionNodes.length!=0 )
			{
				boolean versionAlreadySelected = false;
				Iterator iterator = fileVersionsTable.entrySet().iterator();
				while(iterator.hasNext())
				{
					Map.Entry Temp  = (Map.Entry) iterator.next();
					FlashCheckBox fcbTemp = (FlashCheckBox)Temp.getValue();
					if(fcbTemp.getSelectedState()==FlashCheckBox.FULL)
						versionAlreadySelected = true;
				}

				if(!versionAlreadySelected)
				{
					ArchiveFileVersionNode versionNode = node.getfileVersionsList()[node.getVersionsCount() - 1];						
					SetSelectableStateofOtherVersions(versionNode, true);
				}	
			}
		}
	}
	
	
	
	private void selectTreeNodeParent(ArchiveGridTreeNode node)
	{
		ArchiveGridTreeNode parent = archiveBrowseTree.getTreeStore().getParent(node);
		int parentState = FlashCheckBox.NONE;
		if (parent != null)
		{
			int fullCount = 0;
			int partialCount = 0;
			int emptyCount = 0;
			int nullCount = 0;
			
			List<ArchiveGridTreeNode> childNodes = archiveBrowseTree.getTreeStore().getChildren(parent);
			//For each call select Children
			for (int i = 0 ; i < childNodes.size(); i++)
			{
				FlashCheckBox fcb = table.get(childNodes.get(i));
				if (fcb != null)
				{
					switch (fcb.getSelectedState())
					{
						case FlashCheckBox.FULL:
							fullCount++;
							break;
						case FlashCheckBox.PARTIAL:
							partialCount++;
							break;
						case FlashCheckBox.NONE:
						default:
							emptyCount++;
							break;
					}
				}
				else 
				{
					nullCount++;
				}
			}
			
			if (emptyCount + nullCount == childNodes.size())
			{
				parentState = FlashCheckBox.NONE;
			}
			else
			{
				parentState = FlashCheckBox.PARTIAL;
			}
			
			FlashCheckBox fcb = table.get(parent);
			if (fcb != null)
			{
				fcb.setSelectedState(parentState);
				if(parentState == FlashCheckBox.PARTIAL)
					parent.setChecked(true);
				else
					parent.setChecked(false);
				//Parent changed, change the parent's parent
				selectTreeNodeParent(parent);
			}
		}
	}
	
	private IconButton getNodeIcon(ArchiveGridTreeNode node){
		
		if(node == null)
			return null;
		
		IconButton image = null;
		int nodeType = node.getType();
		switch (nodeType) {
			case CatalogModelType.Folder:
				image = new IconButton("folder-icon");
				break;
			case CatalogModelType.File:
				image = new IconButton("file-icon");
				break;
			case CatalogModelType.OT_VSS_FILESYSTEM_WRITER://to show drive icon for volumes
				image = new IconButton("drive-icon");
				break;
			default:
				break;
		}
		if(image != null){
			image.setWidth(20);
			image.setStyleAttribute("font-size", "0");
		}
		
		return image;
	}
	
	public void RefreshArchiveDestinationTree() {
		wizard.setButtonsEnable(false);
		thisPanel.mask(UIContext.Constants.LoadingArchivedDataMessage());
//		archiveDestinationInfo.setArchiveToRPS(true);
//		RpsHostModel mo=new RpsHostModel();
//		
//		archiveDestinationInfo.setRpsHostModel(mo);
		service.getArchiveDestinationItems(archiveDestinationInfo, new BaseAsyncCallback<ArchiveRestoreDestinationVolumesModel[]>() {

			@Override
			public void onFailure(Throwable caught) {
				thisPanel.unmask();
				wizard.setButtonsEnable(true);
				wizard.nextButton.setEnabled(false);
				//super.onFailure(caught);
				final Listener<MessageBoxEvent> messageBoxHandler = new Listener<MessageBoxEvent>() {
					public void handleEvent(MessageBoxEvent be) {
						if (be.getButtonClicked().getItemId().equals(Dialog.YES))
						{
							//Code for resync
							archiveDestinationInfo.setArchiveToRPS(null);
							TaskPanel.ManualDestinationCache.put(archiveDestinationInfo.getArchiveDestination(), archiveDestinationInfo);
							wizard.submitD2DArchiveCatalogSyncJob(archiveDestinationInfo);
						}
					}
				};
				MessageBox mb = new MessageBox();
				mb.setIcon(MessageBox.ERROR);
				mb.setButtons(MessageBox.YESNO);
				mb.setMinWidth(550);
				mb.setTitleHtml(UIContext.Messages.messageBoxTitleWarning(productName));
				if(archiveDestinationInfo.getArchiveToRPS()!=null && archiveDestinationInfo.getArchiveToRPS()){
					mb.setMessage(UIContext.Messages.restoreFailureForRPS(archiveDestinationInfo.getRpsHostModel().getHostName()));
				}
				else{
					mb.setMessage(UIContext.Messages.restoreFailureForNode(archiveDestinationInfo.getNodeHostname()));
				}
				mb.addCallback(messageBoxHandler);
				Utils.setMessageBoxDebugId(mb);
				mb.show();
				/*final MessageBox msgError = new MessageBox();
				msgError.setTitle(UIContext.Constants.messageBoxTitleError());
				msgError.setMessage(caught.getMessage() == null ? "Error occurred while retrieving the archived data. Please try later or check logs for more information" : caught.getMessage());
				msgError.setModal(true);
				msgError.setIcon(MessageBox.ERROR);
				msgError.setMinWidth(400);
				msgError.show();*/
			}

			@Override
			public void onSuccess(ArchiveRestoreDestinationVolumesModel[] result) {
				thisPanel.unmask();
				//clearing if the data exists in file versions grid
				archiveFileVersionsStore.removeAll();
				archiveFileVersionsGrid.reconfigure(archiveFileVersionsStore, archiveFileVersionsColumnsModel);
				
				if(result == null)
				{
					MessageBox msgBox = new MessageBox();
					msgBox.setTitleHtml(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D));
					msgBox.setModal(true);
					msgBox.setIcon(MessageBox.INFO);
					msgBox.setMessage(UIContext.Messages.FileCopyDestEmptyMessage());
					Utils.setMessageBoxDebugId(msgBox);
					msgBox.show();
					
					// clear the tree and caches, otherwise the items for the previous destination will still be there
					archiveBrowseTreeStore.removeAll();
					rootItemMap.clear();
					table.clear();
					fileVersionsTable.clear();
					CurrentSelectedNode = null;
				}
				else
				{
					//populating the tree
					PopulateArchiveBrowseTree(result);
				}
				
				wizard.setButtonsEnable(true);
			}
		});
	}
	
	private void PopulateArchiveBrowseTree(ArchiveRestoreDestinationVolumesModel[] volumesList)
	{
		archiveBrowseTreeStore.removeAll();
		// 1) Create tree nodes from the selection.items
		if (volumesList != null) {
			try {
				rootItemMap.clear();
				List<ArchiveGridTreeNode> newstore = new ArrayList<ArchiveGridTreeNode>();
					for (int i = 0; i < volumesList.length; i++) {
						ArchiveRestoreDestinationVolumesModel rpDestVolModel = volumesList[i];
						ArchiveGridTreeNode node = ConvertToGridTreeNode(rpDestVolModel);
						newstore.add(node);
						rootItemMap.put(node, rpDestVolModel);
		
					}
					archiveBrowseTreeStore.add(newstore, false);
					archiveBrowseTree.getView().scrollToTop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return;
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
			FileInfo.setModifiedTZOffset(Node.getModifiedTZOffset());;
			
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
	
	private ArchiveGridTreeNode ConvertToGridTreeNode(ArchiveRestoreDestinationVolumesModel model) {
		ArchiveGridTreeNode node = new ArchiveGridTreeNode();
		node.setDate(null);
		node.setSize(model.getVolDataSize());
		node.setName(model.getDisplayName());
		node.setVolumeName(model.getDisplayName());
		node.setDisplayName(model.getDisplayName());
		node.setVolumeHandle(model.getvolumeHandle());
		node.setCatalogFilePath(model.getCatalogFilePath());
		node.setChildrenCount(model.getChildrenCount());
		node.setArchiveType(model.getArchiveType());
		node.setVersionsCount(model.getVersionsCount());
		node.setSelectable(true);
		node.setSelectedVersion(-1);
		node.setChecked(false);
		node.setGuid(model.getGuid());
		node.setType(CatalogModelType.OT_VSS_FILESYSTEM_WRITER);
		return node;
	}
	
	/*protected String GetFileVersionsList(ArchiveGridTreeNode model) {
		String strFileDetails = "";
		
		if(model == null)
			return "";
		
		ArchiveFileVersionNode[] archiveFileVersions = model.getfileVersionsList();
		if(archiveFileVersions != null)
		{
			for(int iArchiveFileIndex = 0;iArchiveFileIndex < archiveFileVersions.length;iArchiveFileIndex++)
			{
				ArchiveFileVersionNode archiveFile = archiveFileVersions[iArchiveFileIndex];
				strFileDetails += model.getDisplayName() + "\t";
				strFileDetails += archiveFile.getVersion() + "\t";
				strFileDetails += archiveFile.getFileSize() + "\t";
				strFileDetails += archiveFile.getFileType() == 1 ? "Archive" : "File copy" + "\t";
				strFileDetails += archiveFile.getModifiedTime() + "\t";
				strFileDetails += "\n";
			}
		}
		else 
		{
			strFileDetails = "No Versions are available";
		}
		
		return strFileDetails;
	}*/
	
	public List<ArchiveGridTreeNode> GetSelectedNodes() {
		List<ArchiveGridTreeNode> nodes = new ArrayList<ArchiveGridTreeNode>();

		List<ArchiveGridTreeNode> selectedNodesFromGridTree = GetSelectedNodesFromGridTree();
		if (selectedNodesFromGridTree != null
				&& selectedNodesFromGridTree.size() > 0) {
			nodes.addAll(selectedNodesFromGridTree);
		}
		List<ArchiveGridTreeNode> selectedPagedNodes = getPagedSelectedNodes();
		if (selectedPagedNodes != null && selectedPagedNodes.size() > 0) {
			nodes.addAll(selectedPagedNodes);
		}

		return nodes;
	}
	
	List<ArchiveGridTreeNode> selectionList = null;//new ArrayList<ArchiveGridTreeNode>();
	public List<ArchiveGridTreeNode> GetSelectedNodesFromGridTree() {
		selectionList = new ArrayList<ArchiveGridTreeNode>();
		getSelectedSubNodes(null);
		return selectionList;
	}
	
	public void getSelectedSubNodes(ArchiveGridTreeNode parent)
	{
		List<ArchiveGridTreeNode> roots;
		if (parent == null)
		{
			roots = archiveBrowseTree.getTreeStore().getRootItems();
		}
		else
		{
			roots = archiveBrowseTree.getTreeStore().getChildren(parent);
		}
		for (int i = 0; i < roots.size() ; i++)
		{
			ArchiveGridTreeNode node = roots.get(i);
			FlashCheckBox fcb = table.get(node);
			
			if (fcb != null)
			{
				if (fcb.getSelectedState() == FlashCheckBox.FULL && fcb.isEnabled())
				{
					//Package it!
					selectionList.add(node);
				}
				else if (fcb.getSelectedState() == FlashCheckBox.PARTIAL || 
						(fcb.getSelectedState() == FlashCheckBox.FULL && !fcb.isEnabled()))
				{
					//get this node's children
					getSelectedSubNodes(node);
				}
			}
			else
			{
				//Error
				
			}
		}
	}
	

	private List<ArchiveGridTreeNode> getPagedSelectedNodes() {
		List<ArchiveGridTreeNode> nodes = new ArrayList<ArchiveGridTreeNode>();
		if (archiveBrowseTree instanceof ExtEditorArchiveTreeGrid) {
			ExtEditorArchiveTreeGrid<ArchiveGridTreeNode> extTree = (ExtEditorArchiveTreeGrid<ArchiveGridTreeNode>) archiveBrowseTree;
			nodes = extTree.getPagedSelectedNodes();
		}
		return nodes;
	}
	
	public HashMap<ArchiveGridTreeNode, ArchiveRestoreDestinationVolumesModel> getRestoreArchiveRootItemsMap()
	{
		return rootItemMap;
	}
	
	private void getDefaultSourceValueForArchiveBrowse() {
		thisPanel.mask(UIContext.Constants.LoadingArchiveDestinationDetailsMessage());
		final List<ArchiveDestinationModel> list=new ArrayList<ArchiveDestinationModel>();
		list.addAll(TaskPanel.ManualDestinationCache.values());
		
		//749534 fix: The below commented code is having 2 async calls made the UI not getting full list of destination items (manual + auto). Hence handled in the server side.
		service.getAllArchiveDestinationDetails(list,
				new BaseAsyncCallback<List<ArchiveDestinationModel>>() {

			@Override
			public void onSuccess(
					List<ArchiveDestinationModel> resultlist) {
				thisPanel.unmask();
				if (resultlist == null || resultlist.size() == 0)
					return;
				comboArchiveDestination.getStore().add(resultlist);
				comboArchiveDestination.setValue(resultlist.get(0));
				if(resultlist.get(0).getArchiveToCloud())
					archiveDestination = resultlist.get(0).getCloudConfigModel().getcloudVendorURL()+resultlist.get(0).getCloudConfigModel().getcloudBucketName();
				else
					archiveDestination = resultlist.get(0).getArchiveDiskDestInfoModel().getArchiveDiskDestPath();
				ArchivePathSelectionWindow.archiveDestination =archiveDestination;
			}
			@Override
			public void onFailure(Throwable caught) {
				thisPanel.unmask();
				wizard.nextButton.setEnabled(false);
			}
		});
	}
		
		/*if (!list.isEmpty()) {
			service.getArchiveChangedDestinationDetailList(
					list,
					new BaseAsyncCallback<List<ArchiveDestinationDetailsModel>>() {

						@Override
						public void onSuccess(
								List<ArchiveDestinationDetailsModel> resultlist) {
							if (resultlist == null || resultlist.size() == 0)
								return;
							int count = 0;
							for (ArchiveDestinationDetailsModel result : resultlist) {
								if (result.getCatalogAvailable()) {
									comboArchiveDestination.getStore().add(
											list.get(count));
								}
								count++;
							}
						}
					});
		}
		service.getArchiveConfigurations(new AsyncCallback<List<ArchiveSettingsModel>>() {
			
			@Override
			public void onSuccess(List<ArchiveSettingsModel> list) {
				
				thisPanel.unmask();

				if(list != null&&!list.isEmpty()){
					ArchiveDestinationModel destinationinfo=new ArchiveDestinationModel();
					for(ArchiveSettingsModel model:list){
						if(model.getArchiveToDrive())
						{
							if(model.getArchiveToDrivePath().length() != 0)
							{
								ArchiveDiskDestInfoModel diskinfo=new ArchiveDiskDestInfoModel();
								diskinfo.setArchiveDiskDestPath(model.getArchiveToDrivePath());
								diskinfo.setArchiveDiskUserName(model.getDestinationPathUserName());
								diskinfo.setArchiveDiskPassword(model.getDestinationPathPassword());
								archiveDestination = model.getArchiveToDrivePath();
								ArchivePathSelectionWindow.archiveDestination = archiveDestination;
								destinationinfo = new ArchiveDestinationModel();
								destinationinfo.setArchiveToDrive(true);
								destinationinfo.setArchiveToCloud(false);
								destinationinfo.setArchiveDiskDestInfoModel(diskinfo);
								destinationinfo.setArchiveToRPS(model.isArchiveToRPS());
								destinationinfo.setPolicyUUID(model.getPolicyUUID());
								destinationinfo.setRpsHostModel(model.getHost());
								destinationinfo.setHostName(model.getHostName());
								destinationinfo.setArchiveType(model.getType());
								destinationinfo.setCatalogPath(model.getCatalogPath());
								destinationinfo.setEncryption(model.getEncryption());
								destinationinfo.setEncryptionPassword(model.getEncryptionPassword());
								
								//if(destinationinfo.getArchiveToRPS()){
								destinationinfo.setCatalogPath(model.getCatalogPath());
								destinationinfo.setCatalogFolderUser(model.getCatalogFolderUser());
								destinationinfo.setCatalogFolderPassword(model.getCatalogFolderPassword());
								//}
								comboArchiveDestination.getStore().add(destinationinfo);
								wizard.archiveConfig = model;
								
							}
						}
						else if(model.getArchiveToCloud())
						{
							ArchiveCloudDestInfoModel cloudConfig = model.getCloudConfigModel();
							if(cloudConfig != null)
							{
								archiveCloudInfo = model.getCloudConfigModel();
								archiveDestination = archiveCloudInfo.getcloudVendorURL()+archiveCloudInfo.getcloudBucketName();
								ArchivePathSelectionWindow.archiveDestination = archiveDestination;		
								destinationinfo = new ArchiveDestinationModel();
								destinationinfo.setCloudConfigModel(model.getCloudConfigModel());
								destinationinfo.setArchiveToCloud(true);
								destinationinfo.setArchiveToDrive(false);
								destinationinfo.setArchiveToRPS(model.isArchiveToRPS());
								destinationinfo.setArchiveType(model.getType());
								destinationinfo.setPolicyUUID(model.getPolicyUUID());
								destinationinfo.setRpsHostModel(model.getHost());
								destinationinfo.setHostName(model.getHostName());
								destinationinfo.setCatalogPath(model.getCatalogPath());
								destinationinfo.setEncryption(model.getEncryption());
								destinationinfo.setEncryptionPassword(model.getEncryptionPassword());
								
								//if(destinationinfo.getArchiveToRPS()){
									destinationinfo.setCatalogPath(model.getCatalogPath());
									destinationinfo.setCatalogFolderUser(model.getCatalogFolderUser());
									destinationinfo.setCatalogFolderPassword(model.getCatalogFolderPassword());
								//}
								comboArchiveDestination.getStore().add(destinationinfo);
								wizard.archiveConfig = model;
							}
		
						}
					}
					if(!list.isEmpty())comboArchiveDestination.setValue(destinationinfo);//This action will trigger selection change event;
					//wizard.nextButton.setEnabled(true);

				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				thisPanel.unmask();
				wizard.nextButton.setEnabled(false);
			}
		});
	}	*/

	private void handleNodeClick(ArchiveGridTreeNode node)
	{
		ArchiveGridTreeNode treeNodeTemp = node;
		highlightRow(treeNodeTemp);
//		if(treeNodeTemp != CurrentSelectedNode)
//		{
			
			FlashCheckBox fcbTemp = table.get(treeNodeTemp);
			if((treeNodeTemp != null) && (treeNodeTemp.getType() ==  CatalogModelType.File))
			{
				if((treeNodeTemp.getSelectedVersion() == -1) && (fcbTemp.getSelectedState() == FlashCheckBox.FULL))
				{
					
						treeNodeTemp.setSelectedVersion(treeNodeTemp.getVersionsCount());
						treeNodeTemp.setSize(treeNodeTemp.getfileVersionsList()[treeNodeTemp.getVersionsCount() - 1].getFileSize());
				}
			}
			CurrentSelectedNode = treeNodeTemp;
			if(CurrentSelectedNode != null)
			{
				PopulateVersionsGrid(CurrentSelectedNode);
			}
//		}
	}
	
	private void highlightRow(ArchiveGridTreeNode treeNode)
	{
		
		archiveBrowseTree.getSelectionModel().deselectAll();
		archiveBrowseTree.getSelectionModel().select(treeNode, false);
	}
	private native String getTemplate(String displayName) /*-{  
    return  [  
    '<tpl for=".">',  
    '<div class="x-combo-list-item" qtip="{'+displayName+'}">{'+displayName+'}</div>',  
    '</tpl>'  
    ].join("");  
 }-*/;
	
}
