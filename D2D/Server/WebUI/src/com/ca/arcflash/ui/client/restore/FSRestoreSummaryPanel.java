package com.ca.arcflash.ui.client.restore;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.ArchiveGridTreeNode;
import com.ca.arcflash.ui.client.model.CatalogItemModel;
import com.ca.arcflash.ui.client.model.CatalogModelType;
import com.ca.arcflash.ui.client.model.DestType;
import com.ca.arcflash.ui.client.model.FileSystemOptionModel;
import com.ca.arcflash.ui.client.model.GridTreeNode;
import com.ca.arcflash.ui.client.model.RestoreArchiveJobModel;
import com.ca.arcflash.ui.client.model.RestoreJobModel;
import com.ca.arcflash.ui.client.model.SummaryDataModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.Element;

public class FSRestoreSummaryPanel extends RestoreSummaryPanel {

	private RestoreWizardContainer wizard;      ///D2D Lite Integration
	private LabelField destinationLabel;
	private LabelField optionsLabel_Overwrite;
	private LabelField optionsLabel_Rename;
	private LabelField optionsLabel_Skip;
	private LabelField optionsLabel_ReplaceActive;
	private LabelField optionsLabel_NotCreateBaseFolder;
	private ListStore<BaseModelData> store;
	private Grid<BaseModelData> grid;

	public FSRestoreSummaryPanel(RestoreWizardContainer restoreWizardWindow) {      ///D2D Lite Integration
		wizard = restoreWizardWindow;
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		//setStyleAttribute("margin", "10px");

		TableLayout tl = new TableLayout();
		tl.setCellPadding(2);
		tl.setCellSpacing(2);
		this.setLayout(tl);

		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.restoreSummary());
		label.addStyleName("restoreWizardTitle");
		this.add(label);

		label = new LabelField();
		label.setValue(UIContext.Constants.restoreSummaryDescription());
		this.add(label);

		label = new LabelField();
		label.setValue(UIContext.Constants.restoreFilesToBeRestored());
		label.addStyleName("restoreWizardSubItem");
		this.add(label);

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		ColumnConfig column = new ColumnConfig();
		column.setId("name");
		column.setHeaderHtml(UIContext.Constants.restoreNameColumn());
		column.setWidth(200);
		column.setMenuDisabled(true);
		configs.add(column);

		column = new ColumnConfig();
		column.setId("path");
		column.setHeaderHtml(UIContext.Constants.restorePathColumn());
		column.setWidth(200);
		column.setMenuDisabled(true);
		configs.add(column);

		column = new ColumnConfig();
		column.setId("size");
		column.setHeaderHtml(UIContext.Constants.restoreSizeColumn());
		column.setWidth(100);
		column.setAlignment(HorizontalAlignment.RIGHT);
		column.setMenuDisabled(true);
		column.setRenderer(new GridCellRenderer<BaseModelData>() {

			@Override
			public String render(BaseModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<BaseModelData> store, Grid<BaseModelData> grid) {
				try {
					if (model != null
							&& (((SummaryDataModel) model).getSize()!=null)) {
						Long value = ((SummaryDataModel) model).getSize();
						long size = 0;
						if( value != null )
							size = value;
						
						return Utils.bytes2String(size);
					}else{
						return "";
					}
				} catch (Exception e) {

				}

				return "N/A";
			}
		});
		configs.add(column);

		ColumnModel cm = new ColumnModel(configs);

		store = new ListStore<BaseModelData>();

		grid = new Grid<BaseModelData>(store, cm);
		grid.setAutoExpandColumn("path");
		grid.getView().setAutoFill(true);
		grid.setBorders(true);
		grid.setStripeRows(true);
		grid.setWidth(RestoreWizardContainer.CONTENT_WIDTH);     ///D2D Lite Integration
		grid.setHeight(150);
		add(grid);

		label = new LabelField();
		label.setValue(UIContext.Constants.restoreDestination());
		label.addStyleName("restoreWizardSubItem");
		this.add(label);

		destinationLabel = new LabelField();
		destinationLabel.setValue(wizard.getDestinationString());
		destinationLabel.setWidth(RestoreWizardContainer.CONTENT_WIDTH);      ///D2D Lite Integration
		destinationLabel.addStyleName("restoreWizardLeftSpacing");
		destinationLabel.setStyleAttribute("word-wrap", "break-word");
		this.add(destinationLabel);

		label = new LabelField();
		label.setValue(UIContext.Constants.restoreResolvingConflicts());
		label.addStyleName("restoreWizardSubItem");
		label.setStyleAttribute("padding-top", "12px");
		this.add(label);

		optionsLabel_Overwrite = new LabelField();
		optionsLabel_Overwrite.addStyleName("restoreWizardLeftSpacing");
		this.add(optionsLabel_Overwrite);
		
		optionsLabel_Skip = new LabelField();
		optionsLabel_Skip.addStyleName("restoreWizardLeftSpacing");
		this.add(optionsLabel_Skip);
		
		optionsLabel_Rename = new LabelField();
		optionsLabel_Rename.addStyleName("restoreWizardLeftSpacing");
		this.add(optionsLabel_Rename);

		optionsLabel_ReplaceActive = new LabelField();
		optionsLabel_ReplaceActive.addStyleName("restoreWizardLeftSpacing");
		this.add(optionsLabel_ReplaceActive);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.restoreDirectoryStructure());
		label.addStyleName("restoreWizardSubItem");
		label.setStyleAttribute("padding-top", "12px");
		this.add(label);
		
		optionsLabel_NotCreateBaseFolder = new LabelField();
		optionsLabel_NotCreateBaseFolder
				.addStyleName("restoreWizardLeftSpacing");
		this.add(optionsLabel_NotCreateBaseFolder);

		updateDestinationLabel();
		updateOptionsLabel();
	}

	@Override
	public void updateDestinationLabel() {
		destinationLabel.setValue(wizard.getDestinationString());
	}

	@Override
	public void updateOptionsLabel() {
		
		FileSystemOptionModel fsOptionsModel = null;
		if((wizard.restoreType == RestoreWizardContainer.RESTORE_BY_BROWSE_ARCHIVE) || ((wizard.restoreType == RestoreWizardContainer.RESTORE_BY_SEARCH ) && wizard.restoreSearchPanel.isSearchArchive()))
		{
			RestoreArchiveJobModel model = wizard.getRestoreArchiveJobModel();
			fsOptionsModel = model.getFileSystemOption();
		}
		else if((wizard.restoreType == RestoreWizardContainer.RESTORE_BY_BROWSE) || ((wizard.restoreType == RestoreWizardContainer.RESTORE_BY_SEARCH ) && wizard.restoreSearchPanel.isSearchBackup()))
		{
			RestoreJobModel model = wizard.getRestoreJobModel();
			fsOptionsModel = model.fileSystemOption;
		}
		
		optionsLabel_Overwrite.setVisible(false);
		optionsLabel_ReplaceActive.setVisible(false);
		optionsLabel_Rename.setVisible(false);
		optionsLabel_Skip.setVisible(false);
		
		if (fsOptionsModel != null) {
			if (fsOptionsModel.isOverwriteExistingFiles()){
				optionsLabel_Overwrite.setValue(UIContext.Messages
						.fileRestoreOptionOverwrite(fsOptionsModel
								.isOverwriteExistingFiles() ? UIContext.Constants
								.yes() : UIContext.Constants.no()));
	
				optionsLabel_ReplaceActive.setValue(UIContext.Messages
						.fileRestoreOptionReplaceActive(fsOptionsModel
								.isReplaceActiveFiles() ? UIContext.Constants.yes()
								: UIContext.Constants.no()));
				
				optionsLabel_Overwrite.setVisible(true);
				optionsLabel_ReplaceActive.setVisible(true);
			}else if (fsOptionsModel.isRename()){
				optionsLabel_Rename.setValue(UIContext.Messages.fileRestoreOptionRename(fsOptionsModel
						.isRename() ? UIContext.Constants
						.yes() : UIContext.Constants.no()));
				optionsLabel_Rename.setVisible(true);
			}else{
				optionsLabel_Rename.setValue(UIContext.Messages.fileRestoreOptionSkip(UIContext.Constants.yes()));
				optionsLabel_Rename.setVisible(true);
			}

			optionsLabel_NotCreateBaseFolder
					.setValue(UIContext.Messages
							.fileRestoreOptionBaseFolderWillNotBeCreated(fsOptionsModel
									.isCreateBaseFolder() ? UIContext.Constants
									.yes()
									: UIContext.Constants.no()));
			
			
		}
	}

	public void setSelectedNodes(List<GridTreeNode> restoreSources) {
		for (int i = 0; i < restoreSources.size(); i++) {
			store.add(restoreSources.get(i));
		}
		grid.repaint();
	}

	@Override
	public void updateRecvPointRestoreSource() {
		List<GridTreeNode> restoreSources = RestoreContext
				.getRestoreRecvPointSources();
		store.removeAll();
		RestoreJobModel model = wizard.getRestoreJobModel();
		if (model.getDestType() == DestType.AlterLoc.getValue()) {
			for (int i = 0; i < restoreSources.size(); i++) {
				store.add(ConvertToSummaryData(restoreSources.get(i)));
			}
		}
		else
		{
			boolean existFileWithoutDriveLetter = false;
			for (int i = 0; i < restoreSources.size(); i++) {
				String displayName = restoreSources.get(i).getDisplayName();
				String fullPath = restoreSources.get(i).getPath();
				if(restoreSources.get(i).isHasDriverLetter())
					store.add(ConvertToSummaryData(restoreSources.get(i)));
				else
					existFileWithoutDriveLetter = true;
				
			}
			if(existFileWithoutDriveLetter)
				showWaringDialog();
		}
		this.repaint();
	}
	
	@Override
	public void updateArchiveRestoreSource()
	{
		List<ArchiveGridTreeNode> restoreSources = RestoreContext
		.getRestoreSelectedArchiveNodes();
		store.removeAll();
		RestoreArchiveJobModel model = wizard.getRestoreArchiveJobModel();
		if (model.getDestType() == DestType.AlterLoc.getValue()) {
			for (int i = 0; i < restoreSources.size(); i++) {
				store.add(ConvertSelectedArchiveFilesToSummaryData(restoreSources.get(i)));
			}
		}
		else
		{
			boolean existFileWithoutDriveLetter = false;
			for (int i = 0; i < restoreSources.size(); i++) {
				String displayName = restoreSources.get(i).getDisplayName();
				String fullPath = restoreSources.get(i).getFullPath();
				if(!FSRestoreOptionsPanel.isInDriveWithoutDriveLetter(displayName, fullPath))
					store.add(ConvertSelectedArchiveFilesToSummaryData(restoreSources.get(i)));
				else
					existFileWithoutDriveLetter = true;
				
			}
			if(existFileWithoutDriveLetter)
				showWaringDialog();
		}
		this.repaint();
	}

	private SummaryDataModel ConvertSelectedArchiveFilesToSummaryData(
			ArchiveGridTreeNode archiveGridTreeNode) {
		String strFullPath = archiveGridTreeNode.getVolumeName() + ":\\";
		strFullPath += archiveGridTreeNode.getFullPath() == null ? "" : archiveGridTreeNode.getFullPath();
		strFullPath = strFullPath.substring(0,strFullPath.lastIndexOf("\\"));//to trim the display name from path to make it consistent with backups
		Integer selectVer=archiveGridTreeNode.getSelectedVersion();
		Integer versionCount=archiveGridTreeNode.getVersionsCount();
		selectVer=selectVer==null?-1:selectVer;
		versionCount=versionCount==null?0:versionCount;
		if(selectVer==-1&&versionCount!=0){
			archiveGridTreeNode.setSelectedVersion(archiveGridTreeNode.getVersionsCount());
			archiveGridTreeNode.setSize(archiveGridTreeNode.getfileVersionsList()[archiveGridTreeNode.getVersionsCount() - 1].getFileSize());
		}
		return new SummaryDataModel(archiveGridTreeNode.getDisplayName(), strFullPath,
				archiveGridTreeNode.getSize());
	}

	private void showWaringDialog() {
		MessageBox messageBox = new MessageBox();
		messageBox.setModal(true);
		messageBox.setTitleHtml(UIContext.Messages.messageBoxTitleWarning(UIContext.productNameD2D));
			messageBox.setMessage(UIContext.Constants.restoreFilesPartInDriveWithoutDriveLetter());
			messageBox.setIcon(MessageBox.WARNING);
			Utils.setMessageBoxDebugId(messageBox);
			messageBox.show();
	}

	private SummaryDataModel ConvertToSummaryData(GridTreeNode node) {
		return new SummaryDataModel(node.getDisplayName(), node.getPath(),
				node.getSize());
	}

	@Override
	public void updateSearchSource() {
		List<CatalogItemModel> restoreSearchSources = RestoreContext
				.getRestoreSearchSources();
		
		List<CatalogItemModel> restoreArchiveSearchSources = RestoreContext
		.getRestoreArchiveSearchSources();
		
		store.removeAll();
		Integer iDestType;
		if(wizard.restoreSearchPanel.isSearchBackup())
			iDestType = wizard.getRestoreJobModel().getDestType();
		else
			iDestType = wizard.getRestoreArchiveJobModel().getDestType();
		if (iDestType == DestType.AlterLoc.getValue()) {
			for (int i = 0; i < restoreSearchSources.size(); i++) {
				store.add(convertToSummaryData(restoreSearchSources.get(i)));
			}
			
			for (int i = 0; i < restoreArchiveSearchSources.size(); i++) {
				SummaryDataModel datemodel = convertToSummaryData(restoreArchiveSearchSources.get(i));
				String fullPath = datemodel.getPath();
				datemodel.setPath(fullPath.substring(0,fullPath.lastIndexOf("\\")));
				store.add(datemodel);
			}
		}
		else
		{
			boolean existFileWithoutDriveLetter = false;
			for (int i = 0; i < restoreSearchSources.size(); i++) {
				SummaryDataModel datemodel = convertToSummaryData(restoreSearchSources.get(i));
				String displayName = datemodel.getName();
				String fullPath = datemodel.getPath();
				if(!FSRestoreOptionsPanel.isInDriveWithoutDriveLetter(displayName, fullPath))
				   store.add(datemodel);
				else
				   existFileWithoutDriveLetter = true;
			}
			
			for (int i = 0; i < restoreArchiveSearchSources.size(); i++) {
				SummaryDataModel datemodel = convertToSummaryData(restoreArchiveSearchSources.get(i));
				String displayName = datemodel.getName();
				String fullPath = datemodel.getPath();
				if(!FSRestoreOptionsPanel.isInDriveWithoutDriveLetter(displayName, fullPath))
				{
				   datemodel.setPath(fullPath.substring(0,fullPath.lastIndexOf("\\")));
				   store.add(datemodel);
				}
				else
				   existFileWithoutDriveLetter = true;
			}
			
			if(existFileWithoutDriveLetter)
				showWaringDialog();
		}
		this.repaint();

	}

	public static SummaryDataModel convertToSummaryData(CatalogItemModel item) {
		String path = item.getPath();
		// String parsing to get it in the right format
		if (path.startsWith("\\\\")) {
			// Fix the format of the path
			int pos = path.indexOf("\\", 4);
			path = path.substring(pos + 1);
		}
		Integer type=item.getType();
		if(type!=null&&type.intValue()==CatalogModelType.Folder){
			return new SummaryDataModel(item.getName(), path, null);
		}else{
			return new SummaryDataModel(item.getName(), path, item.getSize());
		}
		
	}

}
