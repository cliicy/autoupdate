package com.ca.arcflash.ui.client.restore.ad;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.model.GridTreeNode;
import com.ca.arcflash.ui.client.model.RestoreJobModel;
import com.ca.arcflash.ui.client.restore.RestoreContext;
import com.ca.arcflash.ui.client.restore.RestoreSummaryPanel;
import com.ca.arcflash.ui.client.restore.RestoreWizardContainer;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.Element;

public class ActiveDirectorySummaryPanel extends RestoreSummaryPanel
{

	private RestoreWizardContainer wizard;
	private ListStore<BaseModelData> store;
	private Grid<BaseModelData> grid;
	private LabelField destinationLabel;
	private LabelField optionsLabel_rename;
	private LabelField optionsLabel_remove;
	private LabelField optionsLabel_delete;

	public ActiveDirectorySummaryPanel(RestoreWizardContainer restoreWizardWindow)
	{
		wizard = restoreWizardWindow;
	}

	@Override
	protected void onRender(Element parent, int index)
	{
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
		label.setValue(UIContext.Constants.restoreADExplorerHead());
		label.addStyleName("restoreWizardSubItem");
		this.add(label);

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		ColumnConfig column = new ColumnConfig();
		column.setId("displayName");
		column.setHeaderHtml(UIContext.Constants.restoreNameColumn());
		column.setWidth(150);
		column.setMenuDisabled(true);
		configs.add(column);

		column = new ColumnConfig();
		column.setId("path");
		column.setHeaderHtml(UIContext.Constants.restorePathColumn());
		column.setWidth(300);
		column.setMenuDisabled(true);
		configs.add(column);

		ColumnModel cm = new ColumnModel(configs);

		store = new ListStore<BaseModelData>();

		grid = new Grid<BaseModelData>(store, cm);
		//grid.setStyleAttribute("borderTop", "none");
		grid.setAutoExpandColumn("path");
		grid.setBorders(true);
		grid.setStripeRows(true);
		grid.setWidth(RestoreWizardContainer.CONTENT_WIDTH);     ///D2D Lite Integration
		grid.setHeight(200);
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
		label.setValue(UIContext.Constants.restoreADResolvingChanges());
		label.addStyleName("restoreWizardSubItem");
		label.setStyleAttribute("padding-top", "12px");
		this.add(label);

		optionsLabel_rename = new LabelField();
		optionsLabel_rename.addStyleName("restoreWizardLeftSpacing");
		this.add(optionsLabel_rename);
		
		optionsLabel_remove = new LabelField();
		optionsLabel_remove.addStyleName("restoreWizardLeftSpacing");
		this.add(optionsLabel_remove);
		
		optionsLabel_delete = new LabelField();
		optionsLabel_delete.addStyleName("restoreWizardLeftSpacing");
		this.add(optionsLabel_delete);
//
//		optionsLabel_ReplaceActive = new LabelField();
//		optionsLabel_ReplaceActive.addStyleName("restoreWizardLeftSpacing");
//		this.add(optionsLabel_ReplaceActive);
		

		updateDestinationLabel();
		updateOptionsLabel();
	}

	@Override
	public void updateDestinationLabel() {
		destinationLabel.setValue(wizard.getDestinationString());
	}

	@Override
	public void updateOptionsLabel() {
		RestoreJobModel model = wizard.getRestoreJobModel();
		ADOptionModel option = model.adOption;
		if(option!=null){
			String msg = option.isSkipRenamedObject()?UIContext.Constants.restoreADSkip():UIContext.Constants.restoreADToOriginalName();
			optionsLabel_rename.setValue(UIContext.Messages.restoreOptionADRenamedObjects(msg));
			msg = option.isSkipMovedObject()?UIContext.Constants.restoreADSkip():UIContext.Constants.restoreADToOriginalLocatoion();
			optionsLabel_remove.setValue(UIContext.Messages.restoreOptionADMovedObjects(msg));
			msg = option.isSkipDeletedObject()?UIContext.Constants.restoreADSkip():UIContext.Constants.restoreADWithNewObjectID();
			optionsLabel_delete.setValue(UIContext.Messages.restoreOptionADDeletedObjects(msg));
		}
	}

	public void setSelectedNodes(List<GridTreeNode> restoreSources) {
		for (int i = 0; i < restoreSources.size(); i++) {
			store.add(restoreSources.get(i));
		}
		grid.repaint();
	}

	@Override
	public void updateRecvPointRestoreSource()
	{
		List<GridTreeNode> restoreSources = RestoreContext.getRestoreRecvPointSources();
		store.removeAll();
		for (int i = 0; i < restoreSources.size(); i++) {
			store.add(restoreSources.get(i));
		}
		grid.getView().refresh(false);
//		this.repaint();
	}

	@Override
	protected void updateSearchSource() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateArchiveRestoreSource() {
		// TODO Auto-generated method stub
		
	}
		

}
