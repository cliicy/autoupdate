package com.ca.arcflash.ui.client.restore;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.DestType;
import com.ca.arcflash.ui.client.model.GridTreeNode;
import com.ca.arcflash.ui.client.model.RestoreJobModel;
import com.ca.arcflash.ui.client.model.SQLModel;
import com.ca.arcflash.ui.client.model.SummaryDataModel;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.Element;

public class SQLRestoreSummaryPanel extends RestoreSummaryPanel {

	private RestoreWizardContainer wizard;     ///D2D Lite Integration
	private LabelField destinationLabel;
	private ListStore<BaseModelData> fileStore;
	private Grid<BaseModelData> fileGrid;

	private ListStore<SQLModel> destStore;
	private ColumnModel desCM;
	private Grid<SQLModel> destGrid;
	private LayoutContainer altLoc;

	public SQLRestoreSummaryPanel(RestoreWizardContainer restoreWizardWindow) {     ///D2D Lite Integration
		wizard = restoreWizardWindow;
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		//setStyleAttribute("margin", "10px");

		TableLayout tl = new TableLayout();
		tl.setColumns(1);
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
		label.setValue(UIContext.Constants.restoreSQLSummaryLabel());
		label.addStyleName("restoreWizardSubItem");
		this.add(label);

		add(createFileGridPanel());

		// /////////// Destination//////////////////////
		label = new LabelField();
		label.setValue(UIContext.Constants.restoreDestination());
		label.addStyleName("restoreWizardSubItem");
		this.add(label);

		destinationLabel = new LabelField();
		destinationLabel.setValue(wizard.getDestinationString());
		destinationLabel.addStyleName("restoreWizardLeftSpacing");
		destinationLabel.setWidth(RestoreWizardContainer.CONTENT_WIDTH);     ///D2D Lite Integration
		destinationLabel.setStyleAttribute("word-wrap", "break-word");
		this.add(destinationLabel);
		altLoc = createAltLoc();
		//altLoc.setVisible(false);
		this.add(altLoc);
	}

	private LayoutContainer createFileGridPanel() {
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

		ColumnModel cm = new ColumnModel(configs);

		fileStore = new ListStore<BaseModelData>();

		fileGrid = new Grid<BaseModelData>(fileStore, cm);
		fileGrid.setStyleAttribute("borderTop", "none");
		fileGrid.setAutoExpandColumn("name");
		fileGrid.setBorders(false);
		fileGrid.setStripeRows(true);
		fileGrid.setWidth(RestoreWizardContainer.CONTENT_WIDTH);     ///D2D Lite Integration
		fileGrid.setHeight(100);
		ContentPanel cp = new ContentPanel();
		cp.setHeight(100);
//		cp.setFrame(false);
//		cp.setBodyBorder(false);
		cp.setLayout(new FitLayout());
		cp.setHeaderVisible(false);
		cp.setCollapsible(false);
		cp.add(fileGrid);
		cp.setWidth(RestoreWizardContainer.CONTENT_WIDTH);     ///D2D Lite Integration
		return cp;
	}

	private LayoutContainer createAltLoc() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig("instanceName",
				UIContext.Constants.restoreSQLInstanceName(), 120);
		column.setMenuDisabled(true);
		configs.add(column);
		column = new ColumnConfig("dbName", UIContext.Constants
				.restoreSQLDatabaseName(), 100);
		column.setMenuDisabled(true);
		configs.add(column);

		column = new ColumnConfig("newDbName", UIContext.Constants
				.restoreSQLDBRename(), 100);
		column.setMenuDisabled(true);
		configs.add(column);

		column = new ColumnConfig("newFileLoc", UIContext.Constants
				.restoreSQLDBAltLoc(), 180);
		GridCellRenderer<SQLModel> messageRenderer = new GridCellRenderer<SQLModel>() {

			@Override
			public Object render(SQLModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<SQLModel> store, Grid<SQLModel> grid) {
				LabelField messageLabel = new LabelField();
				String message = model.getNewFileLoc();
				if(message != null)
				{
					messageLabel.setValue(message);
					Utils.addToolTip(messageLabel, message);
					return messageLabel;
				}
				return "";
			}
		};
		column.setRenderer(messageRenderer);
		column.setMenuDisabled(true);
		configs.add(column);

		destStore = new ListStore<SQLModel>();

		desCM = new ColumnModel(configs);

		ContentPanel cp = new ContentPanel();
		cp.setHeight(100);
		cp.setFrame(false);
		cp.setBodyBorder(false);
		cp.setLayout(new FitLayout());
		cp.setHeaderVisible(false);
		cp.setCollapsible(false);
		cp.setWidth(RestoreWizardContainer.CONTENT_WIDTH);     ///D2D Lite Integration

		destGrid = new Grid<SQLModel>(destStore, desCM);

		destGrid.setBorders(true);
		destGrid.setWidth(RestoreWizardContainer.CONTENT_WIDTH);     ///D2D Lite Integration
		destGrid.getView().setAutoFill(true);
		destGrid.getView().setShowDirtyCells(false);
		destGrid.setHeight(100);
		cp.add(destGrid);
		return cp;
	}

	@Override
	public void updateDestinationLabel() {
		RestoreJobModel model = RestoreContext.getRestoreModel();

		Integer destType = model.getDestType();
		if (destType == null)
			return;

		if (destType == DestType.DumpFile.getValue()) {
			altLoc.setVisible(false);
			destinationLabel.setValue(UIContext.Constants.restoreDumpFileTo()
					+ " " + wizard.getDestinationString());
		} else if (destType == DestType.AlterLoc.getValue()) {
			altLoc.setVisible(true);
			destinationLabel.setValue(UIContext.Constants
					.restoreRestoreToAltLoc());
			if (model != null && model.listOfSQLMode != null) {
				destStore = new ListStore<SQLModel>();
				destStore.add(model.listOfSQLMode);
			}
			destGrid.reconfigure(destStore, desCM);
			altLoc.layout(true);
		} else {
			altLoc.setVisible(false);
			destinationLabel.setValue(wizard.getDestinationString());
		}
	}

	@Override
	public void updateOptionsLabel() {
	}

	@Override
	public void updateRecvPointRestoreSource() {
		List<GridTreeNode> restoreSources = RestoreContext
				.getRestoreRecvPointSources();
		fileStore.removeAll();
		for (int i = 0; i < restoreSources.size(); i++) {
			GridTreeNode node = restoreSources.get(i);
			if (!nodeIsSQLInstance(node)) {
				fileStore.add(ConvertToSummaryData(node));
			}
		}
		this.repaint();
	}

	private boolean nodeIsSQLInstance(GridTreeNode node) {
		Integer nodeType = node.getType();
		if (nodeType != null
				&& nodeType == SQLRestoreOptionsPanel.SQLNodeType.INSTANCE
						.getValue()) {
			return true;
		}
		return false;
	}

	private SummaryDataModel ConvertToSummaryData(GridTreeNode node) {
		return new SummaryDataModel(node.getDisplayName(), node.getFullPath(),
				node.getSize());
	}

	@Override
	public void updateSearchSource() {

	}
	
	@Override
	public void updateArchiveRestoreSource() {
		return;
	}
}
