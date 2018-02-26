package com.ca.arcflash.ui.client.restore;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.DestType;
import com.ca.arcflash.ui.client.model.GridTreeNode;
import com.ca.arcflash.ui.client.model.RestoreJobModel;
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
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.Element;

public class ExchangeRestoreSummaryPanel extends RestoreSummaryPanel {
	private RestoreWizardContainer wizard;     ///D2D Lite Integration
	private LabelField destinationLabel;
	private LabelField optionsLabel;
	private ListStore<BaseModelData> fileStore;
	private Grid<BaseModelData> fileGrid;

	private LayoutContainer altLoc;

	private LabelField dismountAndMountLabel;

	public ExchangeRestoreSummaryPanel(RestoreWizardContainer restoreWizardWindow) {     ///D2D Lite Integration
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
		tl.setWidth("98%");
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
		TableData td = new TableData();
		td.setWidth("100%");
		add(createFileGridPanel());

		// /////////// Destination//////////////////////
		label = new LabelField();
		label.setValue(UIContext.Constants.restoreDestination());
		label.addStyleName("restoreWizardSubItem");
		this.add(label);

		destinationLabel = new LabelField();
		destinationLabel.setValue(wizard.getDestinationString());
		destinationLabel.addStyleName("restoreWizardLeftSpacing");
		destinationLabel.setStyleAttribute("word-wrap", "break-word");
		destinationLabel.setWidth(RestoreWizardContainer.CONTENT_WIDTH);     ///D2D Lite Integration 
		this.add(destinationLabel);
		altLoc = createAltLoc();
		altLoc.addStyleName("restoreWizardLeftSpacing");
		altLoc.setVisible(false);
		this.add(altLoc);

		optionsLabel = new LabelField();
		optionsLabel.setValue(UIContext.Constants.restoreOptions());
		optionsLabel.addStyleName("restoreWizardSubItem");
		this.add(optionsLabel);

		dismountAndMountLabel = new LabelField();
		dismountAndMountLabel.addStyleName("restoreWizardLeftSpacing");
		this.add(dismountAndMountLabel);

		updateDestinationLabel();
		updateOptionsLabel();
	}

	private GridCellRenderer<BaseModelData> pathRenderer = new GridCellRenderer<BaseModelData>() {

		@Override
		public Object render(BaseModelData model, String property,
				ColumnData config, int rowIndex, int colIndex,
				ListStore<BaseModelData> store, Grid<BaseModelData> grid) {
			LabelField messageLabel = new LabelField();
			String path = model.get(property);
			if (path != null && path.trim().length() > 0) {
				messageLabel.setValue(path);
				Utils.addToolTip(messageLabel, path);
				return messageLabel;
			}
			return "";
		}

	};

	private LayoutContainer createFileGridPanel() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		ColumnConfig column = new ColumnConfig();
		column.setId("name");
		column.setHeaderHtml(UIContext.Constants.restoreNameColumn());
		column.setRenderer(new GridCellRenderer<SummaryDataModel> () {
		@Override
		public Object render(SummaryDataModel model, String property,
				ColumnData config, int rowIndex, int colIndex,
				ListStore<SummaryDataModel> store, Grid<SummaryDataModel> grid){
				String name = model.getName();
				if(name != null){
					return UIContext.escapeHTML(name);
				}
		
				return "";
			}
		});
		column.setWidth(200);
		column.setMenuDisabled(true);
		configs.add(column);

		column = new ColumnConfig();
		column.setId("path");
		column.setRenderer(pathRenderer);
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
		fileGrid.setWidth(RestoreWizardContainer.CONTENT_WIDTH);      ///D2D Lite Integration
		
		ContentPanel cp = new ContentPanel();
		cp.setWidth("100%");
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

	LabelField lf1;

	private LayoutContainer createAltLoc() {
		LayoutContainer cp = new LayoutContainer();
		lf1 = new LabelField();
		cp.add(lf1);
		return cp;
	}

	@Override
	public void updateDestinationLabel() {
		RestoreJobModel model = RestoreContext.getRestoreModel();

		Integer destType = model.getDestType();
		if (destType == null)
			return;

		if (destType == DestType.OrigLoc.getValue()) {
			altLoc.setVisible(false);
			destinationLabel.setValue(wizard.getDestinationString());
		} else {
			altLoc.setVisible(true);
		}
		if (destType == DestType.DumpFile.getValue()) {
			destinationLabel.setValue(UIContext.Constants.restoreDumpFileTo()
					+ " " + wizard.getDestinationString());
			if (Boolean.TRUE.equals(model.exchangeOption.isReplayLogOnDB())) {
				lf1.setValue(UIContext.Constants.restoreReplayLogOnDatabase());
			}

		} else if (destType == DestType.ExchRestore2RSG.getValue()) {
			destinationLabel.setValue(UIContext.Constants.restoretoRSG());
			altLoc.setVisible(false);
		} else if (destType == DestType.ExchRestore2RDB.getValue()) {
			destinationLabel.setValue(UIContext.Constants.restoretoRDB());
			lf1.setValue(UIContext.Constants.restoreRDBNameOrGUID() + ": "
					+ model.getRDBName());
		}
	}

	@Override
	public void updateOptionsLabel() {

		RestoreJobModel model = RestoreContext.getRestoreModel();

		Integer destType = model.getDestType();
		if (destType == null)
			return;

		if (destType == DestType.DumpFile.getValue()) {
			dismountAndMountLabel.setVisible(false);
			optionsLabel.setVisible(false);
		} else {
			dismountAndMountLabel.setVisible(true);
			optionsLabel.setVisible(true);
		}

		String yesOrNo = UIContext.Constants.no();
		if (model.exchangeOption != null
				&& Boolean.TRUE.equals(model.exchangeOption
						.isDisMoundAndMountDB())) {
			yesOrNo = UIContext.Constants.yes();
		}
		dismountAndMountLabel.setValue(UIContext.Constants
				.restoreDismountMountDatabase()
				+ ": " + yesOrNo);
	}

	// public void setSelectedNodes(List<GridTreeNode> restoreSources) {
	// for (int i = 0; i < restoreSources.size(); i++) {
	// fileStore.add(restoreSources.get(i));
	// }
	// fileGrid.repaint();
	// }

	@Override
	public void updateRecvPointRestoreSource() {
		List<GridTreeNode> restoreSources = RestoreContext
				.getRestoreRecvPointSources();

		List<GridTreeNode> children = RestoreUtil.packageRSGChildren();
		if (children != null) {
			restoreSources = children;
		}

		fileStore.removeAll();
		for (int i = 0; i < restoreSources.size(); i++) {
			fileStore.add(ConvertToSummaryData(restoreSources.get(i)));

		}
		this.repaint();
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
