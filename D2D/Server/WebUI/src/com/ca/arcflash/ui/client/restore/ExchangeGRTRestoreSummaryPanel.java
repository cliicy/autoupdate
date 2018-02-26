package com.ca.arcflash.ui.client.restore;

import java.util.Arrays;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.CatalogModelType;
import com.ca.arcflash.ui.client.model.DestType;
import com.ca.arcflash.ui.client.model.GRTCatalogItemModel;
import com.ca.arcflash.ui.client.model.GridTreeNode;
import com.ca.arcflash.ui.client.model.RestoreJobModel;
import com.ca.arcflash.ui.client.model.SummaryDataModel;
import com.ca.arcflash.ui.client.restore.mailboxexplorer.MailGridPanel;
import com.ca.arcflash.webservice.data.catalog.CatalogType;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.grid.BufferView;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Element;

public class ExchangeGRTRestoreSummaryPanel extends RestoreSummaryPanel
{

	private RestoreWizardContainer wizard;
	private LabelField labelDestination;
	private LabelField lableFieldLocation;
	private LabelField labelOptions;
	private LabelToolItem labelSummary;
	private ListStore<BaseModelData> fileStore;
	private Grid<BaseModelData> fileGrid;

	private LayoutContainer altLoc;

	private LabelField labelFieldResolveConflict;

	public ExchangeGRTRestoreSummaryPanel(RestoreWizardContainer restoreWizardWindow)
	{
		wizard = restoreWizardWindow;
	}

	@Override
	protected void onRender(Element parent, int index)
	{
		super.onRender(parent, index);
		setStyleAttribute("margin", "10px");

		this.setLayout(new RowLayout());

		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.restoreSummary());
		label.addStyleName("restoreWizardTitle");
		this.add(label, new RowData(1, -1));

		label = new LabelField();
		label.setValue(UIContext.Constants.restoreSummaryDescription());
		this.add(label, new RowData(1, -1));

		label = new LabelField();
		label.setValue(UIContext.Constants.restoreSQLSummaryLabel());
		label.addStyleName("restoreWizardSubItem");
		this.add(label, new RowData(1, -1));

		// source
		add(createFileGridPanel(), new RowData(0.95, -1));

		// /////////// Destination//////////////////////
		label = new LabelField();
		label.setValue(UIContext.Constants.restoreDestination());
		label.addStyleName("restoreWizardSubItem");
		this.add(label, new RowData(1, -1));

		labelDestination = new LabelField();
		labelDestination.setValue(wizard.getDestinationString());
		labelDestination.addStyleName("restoreWizardLeftSpacing");
		labelDestination.setStyleAttribute("word-wrap", "break-word");
		labelDestination.setWidth(RestoreWizardContainer.CONTENT_WIDTH);
		this.add(labelDestination, new RowData(1, -1));
		altLoc = createAltLoc();
		altLoc.addStyleName("restoreWizardLeftSpacing");
		altLoc.setVisible(false);
		this.add(altLoc, new RowData(1, -1));

		labelOptions = new LabelField();
		labelOptions.setValue(UIContext.Constants.restoreOptions());
		labelOptions.addStyleName("restoreWizardSubItem");
		this.add(labelOptions, new RowData(1, -1));

		labelFieldResolveConflict = new LabelField();
		labelFieldResolveConflict.addStyleName("restoreWizardLeftSpacing");
		this.add(labelFieldResolveConflict, new RowData(1, -1));

		updateDestinationLabel();
		updateOptionsLabel();
	}

	private ColumnModel createColumnModel()
	{
		ColumnConfig iconColumn = new ColumnConfig("type", "", 18);
		iconColumn.setMenuDisabled(true);
		iconColumn.setStyle("vertical-align:middle;");
		iconColumn.setAlignment(HorizontalAlignment.LEFT);
		iconColumn.setResizable(false);
		iconColumn.setFixed(true);
		iconColumn.setSortable(false);
		// iconColumn.setColumnStyleName("exchange_grt_header_of_icon");
		iconColumn.setRenderer(new GridCellRenderer<ModelData>()
		{
			@Override
			public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid)
			{
				try
				{
					LayoutContainer lc = new LayoutContainer();
					lc.setStyleAttribute("margin-left", "-5px");
					TableLayout layout = new TableLayout();
					layout.setCellPadding(0);
					layout.setCellSpacing(0);
					layout.setBorder(0);
					layout.setColumns(1);
					lc.setLayout(layout);

					final Grid<ModelData> thisGrid = grid;
					final GridTreeNode node = (GridTreeNode) model;

					IconButton image = ExchangeGRTRecoveryPointsPanel.getNodeIcon(node);
					if (image != null)
					{
						image.setWidth(16);
						image.setStyleAttribute("font-size", "0");

						image.addSelectionListener(new SelectionListener<IconButtonEvent>()
						{
							@Override
							public void componentSelected(IconButtonEvent ce)
							{
								if (node != null)
								{
									thisGrid.getSelectionModel().select(node, false);
								}
							}
						});

						lc.add(image);
					}

					return lc;
				}
				catch (Exception e)
				{

				}

				return "";
			}

		});

		ColumnConfig subjectColumn = new ColumnConfig("subject", UIContext.Constants.restoreNameColumn(), 100);
		subjectColumn.setMenuDisabled(true);
		subjectColumn.setStyle("vertical-align:middle;");
		subjectColumn.setAlignment(HorizontalAlignment.LEFT);
		subjectColumn.setRenderer(new GridCellRenderer<ModelData>()
		{
			@Override
			public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<ModelData> store, Grid<ModelData> grid)
			{
				try
				{
					LabelField subjectLabel = new LabelField();
					
					subjectLabel.setStyleName("x-grid3-col x-grid3-cell x-grid3-cell-last "); 
					subjectLabel.setStyleAttribute("white-space", "nowrap");
					
					String subject = ((GridTreeNode) model).getDisplayName();
					GRTCatalogItemModel catModel = ((GridTreeNode)model).getGrtCatalogItemModel(); 
					if (null != catModel && null != catModel.getObjType() 
							&& (catModel.getObjType().intValue() == CatalogModelType.OT_GRT_EXCH_CONTACTS_ITEM
								|| catModel.getObjType().intValue() == CatalogModelType.OT_GRT_EXCH_CONTACTS_GROUP)) {
						// Show full name for contact & contact group
						subject = MailGridPanel.getColumnValue(0, catModel.getSender()); 
						catModel.setObjName(subject);
					}					
					if (subject != null && subject.trim().length() > 0)
					{
						subjectLabel.setValue(subject);
						subjectLabel.setTitle(subject);
						return subjectLabel;
					}
					
				}
				catch (Exception e)
				{

				}

				return "";
			}

		});

		ColumnConfig sizeColumn = new ColumnConfig("size", UIContext.Constants.restoreSizeColumn(), 70);
		sizeColumn.setMenuDisabled(true);
		sizeColumn.setStyle("vertical-align:middle;");
		sizeColumn.setAlignment(HorizontalAlignment.RIGHT);
		sizeColumn.setRenderer(new GridCellRenderer<BaseModelData>()
		{

			@Override
			public Object render(BaseModelData model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<BaseModelData> store, Grid<BaseModelData> grid)
			{
				try
				{
					String formattedValue = UIContext.Constants.NA();
					
					if (model != null)
					{
						GRTCatalogItemModel grtModel = ((GridTreeNode) model).getGrtCatalogItemModel();

						if (grtModel != null && grtModel.getItemSize() != null)
						{
							// only mail has size
							if (grtModel.getObjType() != null
									&& CatalogModelType.exchSubItemType_messages.contains(grtModel.getObjType()
											.intValue()))
							{
								formattedValue = Utils.bytes2String(grtModel.getItemSize());
							}
						}
					}
					
					
					LabelField label = new LabelField();

					label.setStyleName("x-grid3-col x-grid3-cell x-grid3-cell-last ");
					label.setStyleAttribute("white-space", "nowrap");

					
					if (formattedValue != null && formattedValue.trim().length() > 0)
					{
						label.setValue(formattedValue);
						label.setTitle(formattedValue);
						return label;
					}
				}
				catch (Exception e)
				{

				}

				return "";
			}

		});

		ColumnConfig pathColumn = new ColumnConfig("displayPath", UIContext.Constants.restorePathColumn(), 280);
		pathColumn.setMenuDisabled(true);
		pathColumn.setRenderer(new GridCellRenderer<BaseModelData>()
		{
			@Override
			public Object render(BaseModelData model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<BaseModelData> store, Grid<BaseModelData> grid)
			{
				try
				{
					LabelField messageLabel = new LabelField();
					
					messageLabel.setStyleName("x-grid3-col x-grid3-cell x-grid3-cell-last "); 
					messageLabel.setStyleAttribute("white-space", "nowrap");
					
					String path = model.get(property);
					if (path != null && path.trim().length() > 0)
					{
						messageLabel.setValue(path);
						messageLabel.setTitle(path);
						return messageLabel;
					}
				}
				catch (Exception e)
				{

				}

				return "";
			}
		});

		List<ColumnConfig> lst = Arrays.asList(iconColumn, subjectColumn, sizeColumn, pathColumn);

		return new ColumnModel(lst);
	}

	private LayoutContainer createFileGridPanel()
	{

		ColumnModel cm = createColumnModel();

		fileStore = new ListStore<BaseModelData>();

		fileGrid = new Grid<BaseModelData>(fileStore, cm);
		fileGrid.setStyleAttribute("borderTop", "none");
		fileGrid.setAutoExpandColumn("subject");
		fileGrid.setAutoExpandMax(5000);
		fileGrid.setBorders(false);
		fileGrid.setStripeRows(true);
		fileGrid.setWidth(RestoreWizardContainer.CONTENT_WIDTH);

		BufferView view = new BufferView();
		view.setRowHeight(23);
		fileGrid.setView(view);

		ContentPanel cp = new ContentPanel();
		cp.setWidth("100%");
		cp.setHeight(200);
		// cp.setFrame(false);
		// cp.setBodyBorder(false);
		cp.setLayout(new FitLayout());
		cp.setHeaderVisible(false);
		cp.setCollapsible(false);
		cp.add(fileGrid);
		cp.setWidth(RestoreWizardContainer.CONTENT_WIDTH);

		ToolBar toolBar = new ToolBar();
		toolBar.add(new FillToolItem());
		labelSummary = new LabelToolItem();
		toolBar.add(labelSummary);
		cp.setBottomComponent(toolBar);

		return cp;
	}

	private LayoutContainer createAltLoc()
	{
		LayoutContainer cp = new LayoutContainer();
		lableFieldLocation = new LabelField();
		cp.add(lableFieldLocation);
		return cp;
	}

	@Override
	public void updateRecvPointRestoreSource()
	{
		List<GridTreeNode> restoreSources = RestoreContext.getRestoreRecvPointSources();

		fileStore.removeAll();
		labelSummary.setHtml("");

		// get the summary by category
		int edbCount = 0;
		int mailboxCount = 0;
		int folderCount = 0;
		int mailCount = 0;
		int contactCount = 0;
		int contactGroupCount = 0;
		int calendarCount = 0;

		for (int i = 0; i < restoreSources.size(); i++)
		{
			GridTreeNode node = restoreSources.get(i);

			if (node != null)
			{
				if (CatalogModelType.rootGRTExchangeTypes.contains(node.getType().intValue()))
				{
					edbCount++;
				}
				else if (CatalogModelType.exchSubItemType_mailboxes.contains(node.getType().intValue()))
				{
					mailboxCount++;
				}
				else if (CatalogModelType.exchSubItemType_folders.contains(node.getType().intValue()))
				{
					folderCount++;
				}
				else if (CatalogModelType.exchSubItemType_messages.contains(node.getType().intValue()))
				{
					mailCount++;
				}
				else if (CatalogModelType.OT_GRT_EXCH_CONTACTS_ITEM == node.getType().intValue())
				{
					contactCount++;
				}
				else if (CatalogModelType.OT_GRT_EXCH_CONTACTS_GROUP == node.getType().intValue())
				{
					contactGroupCount++;
				}
				else if (CatalogModelType.OT_GRT_EXCH_CALENDAR_ITEM == node.getType().intValue())
				{
					calendarCount++;
				}
			}
		}

		fileStore.add(restoreSources);

		// prepare the summary label string
		String label = "";
		
		if (edbCount > 0)
		{
			label += UIContext.Messages.restoreExchangeGRTSummaryCountDatabase(edbCount);
		}
		
		if (mailboxCount > 0)
		{
			if (!label.isEmpty())
			{
				label += ", ";
			}
			
			label += UIContext.Messages.restoreExchangeGRTSummaryCountMailbox(mailboxCount);
		}
		
		if (folderCount > 0)
		{
			if (!label.isEmpty())
			{
				label += ", ";
			}
			
			label += UIContext.Messages.restoreExchangeGRTSummaryCountFolder(folderCount);
		}
		
		if (mailCount > 0)
		{
			if (!label.isEmpty())
			{
				label += ", ";
			}
			
			label += UIContext.Messages.restoreExchangeGRTSummaryCountMail(mailCount);
		}
		
		if (calendarCount > 0)
		{
			if (!label.isEmpty())
			{
				label += ", ";
			}
			
			label += UIContext.Messages.restoreExchangeGRTSummaryCountCalendar(calendarCount);
		}
		
		if (contactCount > 0)
		{
			if (!label.isEmpty())
			{
				label += ", ";
			}
			
			label += UIContext.Messages.restoreExchangeGRTSummaryCountContact(contactCount);
		}
		
		if (contactGroupCount > 0)
		{
			if (!label.isEmpty())
			{
				label += ", ";
			}
			
			label += UIContext.Messages.restoreExchangeGRTSummaryCountContactGroup(contactGroupCount);
		}			
		
		labelSummary.setHtml(label);

//		labelSummary.setLabel(UIContext.Messages.restoreExchangeGRTSummaryCount(restoreSources.size(), edbCount, mailboxCount,
//				folderCount, mailCount));

		this.repaint();
	}

	private SummaryDataModel ConvertToSummaryData(GridTreeNode node)
	{
		return new SummaryDataModel(node.getDisplayName(), node.getFullPath(), node.getSize());
	}

	@Override
	public void updateSearchSource()
	{

	}

	@Override
	public void updateDestinationLabel()
	{
		RestoreJobModel model = RestoreContext.getRestoreModel();

		Integer destType = model.getDestType();
		if (destType == null)
			return;

		if (destType == DestType.OrigLoc.getValue())
		{
			altLoc.setVisible(false);
			labelDestination.setValue(wizard.getDestinationString());
		}
		else if (destType == DestType.DumpFile.getValue())
		{
			altLoc.setVisible(true);
			labelDestination.setValue(UIContext.Constants.restoreDumpFileTo() + " " + wizard.getDestinationString());
		}
		else if (destType == DestType.AlterLoc.getValue())
		{
			if (model.exchangeGRTOption != null && model.exchangeGRTOption.getAlternateServer() != null)
			{
				labelDestination.setValue(UIContext.Constants.restoreToThisExchangeServer() + " "
						+ model.exchangeGRTOption.getAlternateServer());
			}
		}
	}

	@Override
	public void updateOptionsLabel()
	{

		RestoreJobModel model = RestoreContext.getRestoreModel();

		Integer destType = model.getDestType();
		if (destType == null)
			return;

		if (destType == DestType.OrigLoc.getValue())
		{
			labelOptions.setVisible(false);
			labelFieldResolveConflict.setVisible(false);
		}
		else if (destType == DestType.DumpFile.getValue())
		{
			labelOptions.setVisible(true);
			labelFieldResolveConflict.setVisible(true);

			String strOption = "";
			if ((model.exchangeGRTOption.getOption() & ExchangeGRTRestoreOptionPanel.AFDDO_R_EXCH_RESOLVE_NAME_COLLISION) == ExchangeGRTRestoreOptionPanel.AFDDO_R_EXCH_RESOLVE_NAME_COLLISION)
			{
				strOption = UIContext.Constants.restoreResolveConflictRename();
			}
			else
			{
				strOption = UIContext.Constants.restoreResolveConflictOverwrite();
			}

			labelFieldResolveConflict
					.setValue(UIContext.Messages.restoreToFileSystemOverwriteDesc(UIContext.productNameD2D) + ": " + strOption);
		}
		else
		{
			labelOptions.setVisible(false);
			labelFieldResolveConflict.setVisible(false);
		}
	}

	@Override
	public void updateArchiveRestoreSource() {
		// TODO Auto-generated method stub
		
	}

}
