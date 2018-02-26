package com.ca.arcflash.ui.client.backup;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.ArchiveGridTreeNode;
import com.ca.arcflash.ui.client.model.ArchiveSourceInfoModel;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class PreviewWindow extends Window {
	
	private PreviewWindow thisWindow = null;
	final LoginServiceAsync service = GWT.create(LoginService.class);
	
	private final static int MAX_WIDTH = 650;
	private final static int MAX_HEIGHT = 550;
	
	public final int MIN_WIDTH = 90;
	
	private Grid<ArchiveGridTreeNode> archiveFilesGrid;
	private ListStore<ArchiveGridTreeNode> archiveFilesStore;
	private ColumnModel archiveFilesColumnsModel;

	private GridCellRenderer<ArchiveGridTreeNode> FileNameRenderer;
	private GridCellRenderer<ArchiveGridTreeNode> FileFullPathRenderer;
	private GridCellRenderer<ArchiveGridTreeNode> DateRenderer;
	private GridCellRenderer<ArchiveGridTreeNode> sizeRenderer;
	
	private Button btOK;
	private Button btPrevious;
	private Button btNext;
	//private Button btHelp;
	
	ArchiveSourceInfoModel sourceInfo;
	
	private NumberField nfStartEntry;
	private NumberField nfEndEntry;
	private NumberField nfTotalEntries;
	
	public PreviewWindow(ArchiveSourceInfoModel in_sourceInfo)
	{
		sourceInfo = in_sourceInfo;
		thisWindow = this;
		thisWindow.setScrollMode(Scroll.AUTO);
		thisWindow.setResizable(false);
		thisWindow.setHeadingHtml(UIContext.Constants.ArchiveSettingsButtonPreview());
		thisWindow.setWidth(MAX_WIDTH);
		thisWindow.setHeight(MAX_HEIGHT - 50);
		//thisWindow.setStyleName("Wizard_BackGround");

		LayoutContainer lcPreviewLayout = new LayoutContainer();
		TableLayout tlPreview = new TableLayout();
		tlPreview.setColumns(1);
		tlPreview.setCellPadding(0);
		lcPreviewLayout.setHeight(MAX_HEIGHT - 60);
		lcPreviewLayout.setStyleName("Preview_BackGround");
		lcPreviewLayout.setLayout(tlPreview);
		
		LayoutContainer lcPreviewHeader = new LayoutContainer();
		TableLayout tlPreviewHeader = new TableLayout();
		//tlPreviewHeader.setWidth("100%");
		tlPreviewHeader.setColumns(2);
		tlPreviewHeader.setCellPadding(0);
		//tlPreviewHeader.setCellSpacing(10);
		lcPreviewHeader.setWidth(MAX_WIDTH - 60);
		lcPreviewHeader.setLayout(tlPreviewHeader);
		LabelField lblPreviewHeader = new LabelField("Filtered list of files for Archive:");
		lblPreviewHeader.setStyleName("restoreWizardSubItem");
		lblPreviewHeader.setWidth(350);
		lcPreviewHeader.add(lblPreviewHeader);
		
		
		LayoutContainer lcPreviewPageNumbersContainer = new LayoutContainer();
		TableLayout tlPreviewPageNumbers = new TableLayout();
		//tlPreviewPageNumbers.setWidth("100%");
		tlPreviewPageNumbers.setColumns(5);
		tlPreviewPageNumbers.setCellPadding(0);
		tlPreviewPageNumbers.setCellSpacing(5);
		lcPreviewPageNumbersContainer.setLayout(tlPreviewPageNumbers);
		
		nfStartEntry = new NumberField();
		nfStartEntry.setWidth(50);
		nfStartEntry.setAllowBlank(false);
		nfStartEntry.setAllowNegative(false);
		nfStartEntry.setReadOnly(true);
		lcPreviewPageNumbersContainer.add(nfStartEntry);
		
		LabelField lblTo = new LabelField("to");			
		lcPreviewPageNumbersContainer.add(lblTo);
		
		nfEndEntry = new NumberField();
		nfEndEntry.ensureDebugId("F14158AC-630A-427a-BBA5-403316F72C6C");
		nfEndEntry.setWidth(50);
		nfEndEntry.setAllowBlank(false);
		nfEndEntry.setAllowNegative(false);
		nfEndEntry.setReadOnly(true);
		lcPreviewPageNumbersContainer.add(nfEndEntry);
		
		LabelField lblOf = new LabelField("of");
		lcPreviewPageNumbersContainer.add(lblOf);
		
		nfTotalEntries = new NumberField();
		nfTotalEntries.ensureDebugId("A1F511C0-B34F-498f-8C0C-F201A4710FF0");
		nfTotalEntries.setWidth(60);
		nfTotalEntries.setAllowBlank(false);
		nfTotalEntries.setAllowNegative(false);
		nfTotalEntries.setReadOnly(true);
		lcPreviewPageNumbersContainer.add(nfTotalEntries);
		lcPreviewHeader.add(lcPreviewPageNumbersContainer);

		Html line = new Html("<HR>");
 		line.setWidth(MAX_WIDTH - 60);
 		TableData tdLine = new TableData();
 		tdLine.setColspan(2);
 		
		lcPreviewHeader.add(line,tdLine);	
		
		lcPreviewLayout.add(lcPreviewHeader);
		
		FileNameRenderer = new GridCellRenderer<ArchiveGridTreeNode>() {

			@Override
			public Object render(ArchiveGridTreeNode model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ArchiveGridTreeNode> store, Grid<ArchiveGridTreeNode> grid) {
				return model.getName();
			}
		};
		
		FileFullPathRenderer = new GridCellRenderer<ArchiveGridTreeNode>() {

			@Override
			public Object render(ArchiveGridTreeNode model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ArchiveGridTreeNode> store, Grid<ArchiveGridTreeNode> grid) {

				return model.getPath();
			}
		};
		
		DateRenderer = new GridCellRenderer<ArchiveGridTreeNode>() {

			@Override
			public Object render(ArchiveGridTreeNode model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ArchiveGridTreeNode> store, Grid<ArchiveGridTreeNode> grid) {

				return model.getDate();
			}
		};
		
		sizeRenderer = new GridCellRenderer<ArchiveGridTreeNode>() {

			@Override
			public Object render(ArchiveGridTreeNode model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ArchiveGridTreeNode> store, Grid<ArchiveGridTreeNode> grid) {
				String strFileSize = "";
				strFileSize = Long.toString(model.getSize()) + " kb";
				return strFileSize;
			}
		};
		
		List<ColumnConfig> columnConfigs = new ArrayList<ColumnConfig>();
		columnConfigs.add(Utils.createColumnConfig("FileName", "File Name", 80,FileNameRenderer));
		columnConfigs.add(Utils.createColumnConfig("fullPath", "Path", 150,FileFullPathRenderer));
		columnConfigs.add(Utils.createColumnConfig("modifiedDate", UIContext.Constants.restoreDateModifiedColumn(), 80,DateRenderer));
		columnConfigs.add(Utils.createColumnConfig("creationDate", UIContext.Constants.ArchiveCreationDateColumnTitle(), 80,DateRenderer));
		columnConfigs.add(Utils.createColumnConfig("accessedDate", UIContext.Constants.ArchiveAccessedDateColumnTitle(), 90,DateRenderer));
		columnConfigs.add(Utils.createColumnConfig("fileSize", UIContext.Constants.restoreSizeColumn(), 110,sizeRenderer));
		
		archiveFilesColumnsModel = new ColumnModel(columnConfigs);
		
		archiveFilesStore = new ListStore<ArchiveGridTreeNode>();
		
		archiveFilesGrid = new Grid<ArchiveGridTreeNode>(archiveFilesStore, archiveFilesColumnsModel);
		archiveFilesGrid.setWidth(MAX_WIDTH - 60);
		archiveFilesGrid.setHeight(MAX_HEIGHT - 150);
		archiveFilesGrid.setAutoHeight(false);
		archiveFilesGrid.setAutoWidth(false);
		archiveFilesGrid.setBorders(true);
		archiveFilesGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		archiveFilesGrid.setStyleAttribute("padding-top", "5px");

		lcPreviewLayout.add(archiveFilesGrid);
		
		definePreviewWindowButtons();
		
		thisWindow.add(lcPreviewLayout);
	}
	
	private void definePreviewWindowButtons()
	{
		btPrevious = new Button()
		{
			@Override
			protected void onDisable() {
				addStyleName("item-disabled");
				super.onDisable();		   
			}

			@Override
			protected void onEnable() {
				removeStyleName("item-disabled");
				super.onEnable();
			}
		};
		btPrevious.ensureDebugId("3EBE3152-6903-441f-A05F-60739FF64293");
		btPrevious.setText("Previous");
		btPrevious.setEnabled(false);
		btPrevious.setMinWidth(MIN_WIDTH);
		btPrevious.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				//thisWindow.hide();
				
			}});		
		this.addButton(btPrevious);
		
		btNext = new Button()
		{
			@Override
			protected void onDisable() {
				addStyleName("item-disabled");
				super.onDisable();		   
			}

			@Override
			protected void onEnable() {
				removeStyleName("item-disabled");
				super.onEnable();
			}
		};
		btNext.ensureDebugId("7C5009A0-2DCA-4777-AD10-AD6CD16CBED8");
		btNext.setText("Next");
		btNext.setEnabled(false);
		btNext.setMinWidth(MIN_WIDTH);
		btNext.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				//thisWindow.hide();
				
			}});		
		this.addButton(btNext);	
		
		btOK = new Button();
		btOK.ensureDebugId("E9FFEC73-3578-4376-8C16-D507EB7CC426");
		btOK.setText(UIContext.Constants.ok());
		btOK.setMinWidth(MIN_WIDTH);		
		btOK.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				thisWindow.hide(btOK);
				return;
			}
			});		
		this.addButton(btOK);
		
		/*btHelp = new Button();
		btHelp.setText(UIContext.Constants.help());
		btHelp.setMinWidth(MIN_WIDTH);
		btHelp.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				HelpTopics.showHelpURL(UIContext.externalLinks.getBackupSettingEmailHelp());
			}
		});
		this.addButton(btHelp);	*/
	}

	public void loadArchivableFiles() {
		thisWindow.mask("loading data");
		service.getArchivableFilesList(sourceInfo, new AsyncCallback<List<ArchiveGridTreeNode>>() {

			@Override
			public void onFailure(Throwable caught) {
				thisWindow.unmask();
				thisWindow.hide();
				
				MessageBox msgError = new MessageBox();
				msgError.setIcon(MessageBox.ERROR);
				msgError.setTitleHtml("Error");
				msgError.setModal(true);
				msgError.setMessage("Failed to get preview of files information. Please try later.");
				Utils.setMessageBoxDebugId(msgError);
				msgError.show();
			}

			@Override
			public void onSuccess(List<ArchiveGridTreeNode> result) {
				populateArchivableFilesGrid(result);
				thisWindow.unmask();
			}
		});
	}
	
	private void populateArchivableFilesGrid(List<ArchiveGridTreeNode> result) {
		archiveFilesStore.removeAll();
		
		int iarchiveFilesCount = result != null ? result.size() : 0;		
		
		for(int iarchiveFileVersionIndex = 0;iarchiveFileVersionIndex < iarchiveFilesCount;iarchiveFileVersionIndex++)
		{
			ArchiveGridTreeNode fileNode = result.get(iarchiveFileVersionIndex);
			/*ArchiveFileVersionNode FileInfo = new ArchiveFileVersionNode();
			FileInfo.setVersion(Node.getVersion());
			FileInfo.setFileSize(Node.getFileSize());
			FileInfo.setModifiedTime(Node.getModifiedTime());
			FileInfo.setArchivedTime(Node.getArchivedTime());
			FileInfo.setFileType(Node.getFileType());
			
			if(iarchiveFileVersionIndex == treeNode.getSelectedVersion())
			{
				FileInfo.setChecked(true);
				SelectedFileVersionNode = FileInfo;
			}*/
			
			archiveFilesStore.add(fileNode);
		}
		archiveFilesGrid.reconfigure(archiveFilesStore, archiveFilesColumnsModel);
				
		return;
	}
}
