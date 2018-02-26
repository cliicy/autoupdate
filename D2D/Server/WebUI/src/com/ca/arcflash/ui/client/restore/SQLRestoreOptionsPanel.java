package com.ca.arcflash.ui.client.restore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.AlternativePathModel;
import com.ca.arcflash.ui.client.model.CatalogModelType;
import com.ca.arcflash.ui.client.model.DestType;
import com.ca.arcflash.ui.client.model.GridTreeNode;
import com.ca.arcflash.ui.client.model.RecoveryPointModel;
import com.ca.arcflash.ui.client.model.RestoreJobModel;
import com.ca.arcflash.ui.client.model.SQLModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.core.FastMap;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid.ClicksToEdit;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class SQLRestoreOptionsPanel extends RestoreOptionsPanel {
	
	final LoginServiceAsync service = GWT.create(LoginService.class);
	private Radio origLocRd;
	private Radio alterLocRd;
	private Radio dumpFileRd;
	private RadioGroup locationGroup;
	
	private HTML orginalLocationLabel;

	private BrowseWindow destinationDialog;

	private TextField<String> dumpLocTF;
	private LabelField noteLabel;
	private Button browse;

	private SQLRestoreOptionsPanel thisPanel;
	private static int MIN_WIDTH = 90;

	private EditorGrid<SQLModel> grid;
	private ListStore<SQLModel> store;
	private ColumnModel cm;
	private final String masterDBname = "master";
	private final String modelDBName = "model";
	private final String msdbDBName = "msdb";
	private final String tempDBName = "tempdb";
	private String DB_SUFFIX = "_copy";

	private TextField<String> newDbNameText;
	private TextField<String> newFileLocText;

	public SQLRestoreOptionsPanel(RestoreWizardContainer restoreWizardWindow) {      ///D2D Lite Integration
		super(restoreWizardWindow);
		thisPanel = this;
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		setStyleAttribute("margin", "5px");
		TableLayout tl = new TableLayout();
		tl.setWidth("100%");
		tl.setColumns(3);
		tl.setCellPadding(2);
		tl.setCellSpacing(2);
		this.setLayout(tl);

		TableData td = new TableData();
		td.setColspan(3);

		this.add(renderHeaderSection(), td);
		createDest(this, td);

		setDefaults();
	}

	private void createDest(LayoutContainer cp, TableData td) {
		Label label = new Label(UIContext.Constants.restoreDestination());
		label.addStyleName("restoreWizardSubItem");
		cp.add(label, td);

		label = new Label(UIContext.Constants.restoreDestinationDescription());
		label.addStyleName("restoreWizardSubItemDescription");
		cp.add(label, td);

		origLocRd = new Radio();
		origLocRd.ensureDebugId("85128901-3E71-4842-833B-D0C6A1698543");
		origLocRd.setBoxLabel(UIContext.Constants.restoreToOriginalLocation());
		origLocRd.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {

				Boolean checked = thisPanel.origLocRd.getValue();

				thisPanel.browse.setEnabled(!checked);
				thisPanel.dumpLocTF.setEnabled(!checked);
				// thisPanel.repaint();
			}
		});

		origLocRd.addStyleName("restoreWizardLeftSpacing");
		cp.add(origLocRd, td);
		
		orginalLocationLabel = new HTML(UIContext.Messages.restoreToOriginalLocationForVSphere(UIContext.productNameD2D));
		orginalLocationLabel.addStyleName("restoreWizardSubItemDescription");
		orginalLocationLabel.setVisible(false);
		this.add(orginalLocationLabel,td);

		dumpFileRd = new Radio();
		dumpFileRd.ensureDebugId("255F51EC-C56F-4ca2-B638-57B045F21EA1");
		dumpFileRd.setWidth("100%");
		dumpFileRd.setBoxLabel(UIContext.Constants.restoreDumpFileOnly());
		dumpFileRd.addListener(Events.Change, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {

				Boolean checked = thisPanel.dumpFileRd.getValue();

				thisPanel.browse.setEnabled(checked);
				thisPanel.dumpLocTF.setEnabled(checked);
				if (Boolean.TRUE.equals(checked)) {
					dumpLocTF.clearInvalid();
				} else {
					dumpLocTF.validate();
				}
				// thisPanel.repaint();
			}

		});
		dumpFileRd.addStyleName("restoreWizardLeftSpacing");
		TableData dumpTFtd = new TableData();
		dumpTFtd.setWidth("30%");
		cp.add(dumpFileRd, dumpTFtd);

		dumpLocTF = new TextField<String>();
		dumpLocTF.ensureDebugId("1F28F345-63DC-42de-BE21-C5C9BAC28946");
		dumpLocTF.setAllowBlank(false);
		dumpLocTF.setRegex(AbsoluteDirReg);
		dumpLocTF.getMessages().setRegexText(
				UIContext.Constants.restoreInvalidPath());
		
		// show error tip for UNC path
		dumpLocTF.setValidator(new Validator()
		{
			@Override
			public String validate(Field<?> field, String value)
			{
				if (Utils.isValidRemotePath(value))
				{
					String productName = UIContext.productNameD2D;
					if (UIContext.uiType == 1)
					{
						productName = UIContext.productNamevSphere;
					}

					return UIContext.Messages.restoreSQLWriterToRemoteDisk(productName);
				}
				
				return null;
			}
			
		});
		
		dumpLocTF.setWidth(325);
		dumpLocTF.setStyleAttribute("margin-right", "15px");
		TableData dumpLoctd = new TableData();
		dumpLoctd.setWidth("35%");
		dumpLoctd.setHorizontalAlign(HorizontalAlignment.RIGHT);
		cp.add(dumpLocTF, dumpLoctd);

		browse = getBrowseButton(thisPanel.dumpLocTF);
		TableData brtd = new TableData();
		brtd.setHorizontalAlign(HorizontalAlignment.LEFT);
		brtd.setWidth("35%");
		cp.add(browse, brtd);

		alterLocRd = new Radio();
		alterLocRd.ensureDebugId("92077315-FBE0-47a0-A67C-0B05CFE3B27C");
		alterLocRd.setBoxLabel(UIContext.Constants.restoreRestoreToAltLoc());
		alterLocRd.addListener(Events.Change, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				Boolean checked = thisPanel.alterLocRd.getValue();
				thisPanel.grid.setEnabled(checked);
				thisPanel.noteLabel.setEnabled(checked);
				thisPanel.browse.setEnabled(!checked);
				thisPanel.dumpLocTF.setEnabled(!checked);
				// thisPanel.repaint();
			}
		});
		alterLocRd.addStyleName("restoreWizardLeftSpacing");
		cp.add(alterLocRd, td);

		LayoutContainer altlocCont = this.createAltLoc();
		altlocCont.addStyleName("restoreWizardLeftSpacing");
		cp.add(altlocCont, td);

		noteLabel = new LabelField(UIContext.Constants.restoreSQLMasterNote());
		noteLabel.addStyleName("restoreWizardLeftSpacing");
		noteLabel.setVisible(false);
		cp.add(noteLabel, td);
		
		cp.add(pwdPane, td);

		locationGroup = new RadioGroup();
		locationGroup.add(origLocRd);
		locationGroup.add(dumpFileRd);
		locationGroup.add(alterLocRd);
	}

	private Button getBrowseButton(final TextField<String> buttonFor) {
		return getBrowseButton(buttonFor, -1, -1);
	}

	private Button getBrowseButton(final TextField<String> buttonFor,
			final int rowIndex, final int colIndex) {
		Button browse = new Button();
		browse.setText(UIContext.Constants.restoreBrowse());
		browse.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				destinationDialog = new BrowseWindow(false, UIContext.Constants
						.restoreDestinationTitle());
				if (rowIndex >= 0 && colIndex >= 0) {
					String dest = store.getAt(rowIndex).getNewFileLoc();
					
					// show error message for UNC path
					if (Utils.isValidRemotePath(dest))
					{
						String productName = UIContext.productNameD2D;
						if (UIContext.uiType == 1)
						{
							productName = UIContext.productNamevSphere;
						}

						MessageBox msg = new MessageBox();
						msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(productName));
						msg.setMessage(UIContext.Messages.restoreSQLWriterToRemoteDisk(productName));
						msg.setIcon(MessageBox.ERROR);
						Utils.setMessageBoxDebugId(msg);
						msg.show();
						
						return;
					}
					
					if(dest != null && dest.trim().length() > 0){
						destinationDialog.setInputFolder(dest);
					}
				}else // for dump files
				{
					String dumpPath = dumpLocTF.getValue();
					
					// show error message for UNC path
					if (Utils.isValidRemotePath(dumpPath))
					{
						String productName = UIContext.productNameD2D;
						if (UIContext.uiType == 1)
						{
							productName = UIContext.productNamevSphere;
						}

						MessageBox msg = new MessageBox();
						msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(productName));
						msg.setMessage(UIContext.Messages.restoreSQLWriterToRemoteDisk(productName));
						msg.setIcon(MessageBox.ERROR);
						Utils.setMessageBoxDebugId(msg);
						msg.show();
						
						return;
					}
					
					if(dumpPath != null && dumpPath.trim().length() > 0){
						destinationDialog.setInputFolder(dumpPath);
					}
				}
				destinationDialog.setResizable(false);
				destinationDialog.setModal(true);
				destinationDialog.show();

				destinationDialog.addWindowListener(new WindowListener() {
					public void windowHide(WindowEvent we) {
						if (destinationDialog.getLastClicked() == Dialog.CANCEL) {
							// Canceled
						} else {
							if (buttonFor != null) {
								buttonFor.setValue(destinationDialog
										.getDestination());
							} else {
								if (rowIndex >= 0 && colIndex >= 0) {
									store.getAt(rowIndex).setNewFileLoc(
											destinationDialog.getDestination());
									grid.getView().refresh(false);

								}
							}
						}
					}
				});
			}
		});
		browse.setMinWidth(MIN_WIDTH);
		return browse;
	}

	@Override
	public boolean validate(final AsyncCallback<Boolean> callback) {
		boolean isValid = true;
		if (origLocRd.getValue()) {
			if(backupVMModel != null){
				if(backupVMModel.getEsxPassword()==null || backupVMModel.getEsxPassword().equals("")){
					popUpSetCredentialWindow(thisPanel,false,callback);
					return false;
				}
			}
			checkSessionPassword(callback);
		} else if (dumpFileRd.getValue()) {
			isValid = dumpLocTF.validate();
			if(isValid)
				checkSessionPassword(callback);
			else
				callback.onSuccess(false);
		} else if (alterLocRd.getValue()) {
			if (store != null && store.getCount() > 0) {
				final StringBuilder message = new StringBuilder();
				List<SQLModel> models = store.getModels();
				boolean newDbNameNull = true;
				boolean pathvalid = true;
				boolean pathvalid_Remote = true;
				StringBuilder strDbName = new StringBuilder();
				StringBuilder strDbPath = new StringBuilder();
				StringBuilder strUserDbSysName = new StringBuilder();
				
				String[] base = new String[models.size()];
				String[] ins = new  String[models.size()];
				final String[] dbNames = new  String[models.size()];
				final String[] oldDbNames = new String[models.size()];
				int i = 0;
				
				for (SQLModel md : models) {
					String newDbName = md.getNewDbName();
					String dbPath = md.getNewFileLoc();
					String dbName = md.getDbDisplayName();
					if(modelDBName.equalsIgnoreCase(dbName) && modelDBName.equalsIgnoreCase(newDbName))
					{
						message.append(UIContext.Constants.restoreSQLModelChangeName());
					}
					else if(dbName != null && !dbName.equalsIgnoreCase(newDbName) && isSysDBName(newDbName))
					{
						strUserDbSysName.append(", ").append(dbName);
					}
					else if(!masterDBname.equals(dbName) && (newDbName == null || newDbName.length() == 0 || newDbName.length() > 128 || !newDbName.matches(Directroy))) 
					{
						newDbNameNull = false;
						strDbName.append(", ").append(dbName);
					}
					if(dbPath == null || dbPath.length() == 0 || !dbPath.matches(AbsoluteDirReg))
					{
						if (Utils.isValidRemotePath(dbPath))
						{
							pathvalid_Remote = false;
						}
						pathvalid = false;
						strDbPath.append(", ").append(dbName);
					}
//					else {
//						int pathLen = dbPath.endsWith("\\") || dbPath.endsWith("/") ? dbPath.length() : dbPath.length() + 1;
//						int instFolderLen = md.getInstanceName() == null ? 0 : md.getInstanceName().length() + 1;
//						int dbNameFolderLen =  newDbName == null ? 0 : newDbName.length() + 1; 
//						int dbNameLen = dbNameFolderLen == 0 ? 0 : dbNameFolderLen + DB_NAME_SUFFIX_LENGTH; //(-1 + 1) is removed(remove "/" length and add "." length) 
//						int maxLengh = DB_PATH_MAX_LENGTH - instFolderLen - dbNameFolderLen - dbNameLen;
//						if(pathLen > maxLengh)
//						{
//							if(message.length() > 0)
//								message.append(" ");
//							message.append(UIContext.Messages.restoreAlterLongPathError(dbName, maxLengh));
//						}
//					}
					base[i] = dbPath;
					ins[i] = md.getInstanceName();
					dbNames[i] = md.getNewDbName();
					oldDbNames[i] = dbName;
					i++;
				}
				
				if(strUserDbSysName.length() > 0)
				{
					if(message.length() > 0)
						message.append(" ");
					message.append(UIContext.Messages.restoreAlterUserDBSysNameError(strUserDbSysName.substring(1)));
				}
				if (!newDbNameNull) {
					if(message.length() > 0)
						message.append(" ");
					message.append(UIContext.Messages.restoreAlterDBNameError(strDbName.substring(1)));
				}
				if (!pathvalid) {
					if(message.length() > 0)
						message.append(" ");
					message.append(UIContext.Messages.restoreAlterPathError(strDbPath.substring(1)));
					
					// add error message if there is UNC path
					if (!pathvalid_Remote)
					{
						String productName = UIContext.productNameD2D;
						if (UIContext.uiType == 1)
						{
							productName = UIContext.productNamevSphere;
						}

						message.append("  ");
						message.append(UIContext.Messages.restoreSQLWriterToRemoteDisk(productName));
					}
				}
					
				if(message.length() > 0)
				{
					showErrorMessage(message);
					callback.onSuccess(false);
				} else {
					LoginServiceAsync service = GWT.create(LoginService.class);
					service.checkSQLAlternateLocation(base, ins, dbNames,
							new AsyncCallback<AlternativePathModel[]>() {
								@Override
								public void onFailure(Throwable caught) {
									callback.onFailure(caught);
								}

								@Override
								public void onSuccess(
										AlternativePathModel[] result) {
									if (result != null && result.length > 0) {
										for (int j = 0; j < result.length; j++) {
											Long len = result[j]
													.getMaxPathLength();
											if (len != null) {
												String msg = null;
												if (len > 0) {
													msg = UIContext.Messages
															.restoreAlterLongPathError(
																	oldDbNames[j],
																	len
																			.intValue());
												} else if (len < 0) {
													msg = UIContext.Messages
															.restoreAlterPathSysError(oldDbNames[j]);
												}
												if (msg != null) {
													if (message.length() > 0)
														message.append(" ");
													message.append(msg);
												}
											}
										}
									}

									if (message.length() > 0) {
										showErrorMessage(message);
										callback.onSuccess(false);
									} else {
										if (result != null && result.length > 0) {
											List<SQLModel> models = store
													.getModels();
											for (int j = 0; j < result.length; j++) {
												String path = result[j]
														.getAlterPath();
												if (path != null
														&& path.length() > 0)
													models
															.get(j)
															.setTranslatedFilePath(
																	path);
											}
										}
										checkSessionPassword(callback);
									}
								}

							});
				}
			}
		}
		return isValid;
	}

	private void showErrorMessage(final StringBuilder message) {
		String productName = UIContext.productNameD2D;
		if(UIContext.uiType == 1){
			productName = UIContext.productNamevSphere;
		}
		MessageBox msg = new MessageBox();
		msg.setIcon(MessageBox.ERROR);
		msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(productName));
		msg.setMessage(message.toString());
		msg.setModal(true);
		Utils.setMessageBoxDebugId(msg);
		msg.show();
	}

	
	
	private boolean isSysDBName(String dbName) {
		return masterDBname.equalsIgnoreCase(dbName) || modelDBName.equalsIgnoreCase(dbName) 
				|| msdbDBName.equalsIgnoreCase(dbName) || tempDBName.equalsIgnoreCase(dbName);
	}

	private LayoutContainer createAltLoc() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig("instanceDisplayName",
				UIContext.Constants.restoreSQLInstanceName(), 120);
		column.setMenuDisabled(true);
		configs.add(column);
		column = new ColumnConfig("dbDisplayName", UIContext.Constants
				.restoreSQLDatabaseName(), 100);
		column.setMenuDisabled(true);
		column.setRenderer(new GridCellRenderer<SQLModel>() {

			@Override
			public Object render(SQLModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<SQLModel> store, Grid<SQLModel> grid) {
				if (masterDBname.equalsIgnoreCase(model.getDbName())) {
					return model.get(property) + "*";
				}
				return model.get(property);
			}
		});
		configs.add(column);

		column = new ColumnConfig("newDbName", UIContext.Constants
				.restoreSQLDBRename(), 100);
		column.setMenuDisabled(true);
		newDbNameText = new TextField<String>();
		column.setEditor(new CellEditor(newDbNameText));
		configs.add(column);

		column = new ColumnConfig("newFileLoc", UIContext.Constants
				.restoreSQLDBAltLoc(), 180);
		column.setMenuDisabled(true);
		LayoutContainer con = new LayoutContainer();
		newFileLocText = new TextField<String>();
		con.add(newFileLocText);
		CellEditor newFileLocEditor = new CellEditor(newFileLocText);
		newFileLocEditor.setStyleName("celleditor_fileloc");
		newFileLocEditor.setRevertInvalid(false);
		column.setEditor(newFileLocEditor);
		column.setRenderer(new GridCellRenderer() {

			@Override
			public Object render(ModelData model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore store, Grid grid) {
				String path = "";
				if (model.get(property) != null) {
					path = model.get(property);
				}
				LayoutContainer lc = new LayoutContainer();
				lc.setStyleAttribute("Margin-top", "-3");
				lc.setStyleAttribute("Margin-bottom", "-3");
				lc.setStyleAttribute("Margin-right", "-3");
				lc.setStyleAttribute("Margin-left", "-5");
				lc.setStyleAttribute("background-color", "white");
				lc.setStyleName("x-grid3-col x-grid3-cell x-grid3-cell-last ");

				TableLayout tl = new TableLayout();
				tl.setCellSpacing(0);
				tl.setCellPadding(0);
				tl.setWidth("100%");
				tl.setColumns(2);
				lc.setLayout(tl);
				LabelField lf = new LabelField();
				lf.setWidth("100");
				lf.setValue(path);
				lf.setStyleAttribute("overflow", "hidden");
				lf.setStyleAttribute("text-overflow", "ellipsis");
				lf.setStyleName(" x-form-label x-grid3-cell x-grid3-cell-inner");
				lf.setStyleAttribute("background-color", "white");
				lc.setStyleAttribute("padding-left", "1");
				lc.setStyleAttribute("padding-right", "1");
				// lf.setReadOnly(true);
				// lf.setBorders(false);

				TableData td = new TableData();
				td.setWidth("50%");
				lc.add(lf, td);

				Button b = getBrowseButton(null, rowIndex, colIndex);
				b.setWidth(80);
				b.setHeight(20);
				b.setStyleAttribute("padding-left", "0");
				b.setStyleAttribute("padding-right", "1");
				TableData td2 = new TableData();
				td2.setWidth("50%");
				td2.setHorizontalAlign(HorizontalAlignment.RIGHT);
				td2.setVerticalAlign(VerticalAlignment.MIDDLE);
				lc.add(b, td2);

				return lc;
			}
		});
		configs.add(column);

		// column = new ColumnConfig("browseAction", "", 90);
		// column.setMenuDisabled(true);
		// column.setRenderer(browseRenderer);
		// configs.add(column);

		store = new ListStore<SQLModel>();

		cm = new ColumnModel(configs);

		ContentPanel cp = new ContentPanel();
		cp.setHeight(200);
		cp.setFrame(false);
		cp.setBodyBorder(false);
		cp.setLayout(new FitLayout());
		cp.setHeaderVisible(false);
		cp.setCollapsible(false);
		cp.setWidth(RestoreWizardContainer.CONTENT_WIDTH);      ///D2D Lite Integration

		grid = new EditorGrid<SQLModel>(store, cm);

		grid.setClicksToEdit(ClicksToEdit.ONE);
		grid.setBorders(true);
		grid.setAutoExpandColumn("browseAction");
		grid.setWidth(RestoreWizardContainer.CONTENT_WIDTH);      ///D2D Lite Integration
		grid.getView().setAutoFill(true);
		grid.getView().setShowDirtyCells(false);
		grid.getView().setRowSelectorDepth(15);
		grid.getView().setCellSelectorDepth(10);
		grid.addListener(Events.BeforeEdit,
				new Listener<GridEvent<SQLModel>>() {

					@Override
					public void handleEvent(GridEvent<SQLModel> be) {
						SQLModel model = (SQLModel) be.getModel();
						if (model != null
								&& masterDBname.equalsIgnoreCase(model
										.getDbName())) {
							if (be.getColIndex() == 2) {
								be.setCancelled(true);
							}
						}
					}
				});

		cp.add(grid);
		return cp;
	}

	private GridCellRenderer<SQLModel> browseRenderer = new GridCellRenderer<SQLModel>() {
		public Object render(SQLModel model, String property,
				ColumnData config, int rowIndex, int colIndex,
				ListStore<SQLModel> store, Grid<SQLModel> grid) {

			Button b = getBrowseButton(null, rowIndex, colIndex);
			b.setStyleAttribute("Margin-top", "-4");
			b.setStyleAttribute("Margin-bottom", "-6");
			b.setStyleAttribute("background-color", "white");
			b.setWidth(20);
			return b;
		}
	};

	private LayoutContainer renderHeaderSection() {
		LayoutContainer container = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(2);
		container.setLayout(tl);

		TableData td = new TableData();
		td.setWidth("5%");

		Image image = AbstractImagePrototype.create(UIContext.IconBundle.restore_options()).createImage();
		container.add(image, td);

		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.restoreOptions());
		label.setStyleName("restoreWizardTitle");
		container.add(label);

		return container;
	}

	protected void setDefaultValue(){
		RecoveryPointModel rp = restoreWizardWindow.getSelectedRecoveryPoint();
		if(rp.getVMHypervisor()==1){ //1= hyperv. From hyperv vm session we won't support restore to original and alternate location.
			origLocRd.disable();
			origLocRd.setValue(false);
			dumpFileRd.setValue(true);
			browse.setEnabled(true);
			dumpLocTF.setEnabled(true);
			alterLocRd.disable();
		}
		else
			origLocRd.setValue(true);
		grid.setEnabled(false);
		noteLabel.setEnabled(false);
	}
	
	protected void setVSphereDefaultValue(){
		RecoveryPointModel rp = restoreWizardWindow.getSelectedRecoveryPoint();
		origLocRd.disable();
		if(rp.getVMHypervisor()!=1){ //1= hyperv. From hyperv vm session we won't support restore to original location even when agent is installed inside. Also won't support alternate location.
			origLocRd.setBoxLabel(UIContext.Constants
					.restoreToOriginalLocation()+" ("+UIContext.Constants.disable()+")");
			orginalLocationLabel.setVisible(true);
		}
		else{
			alterLocRd.disable();
		}
		dumpFileRd.setValue(true);
		browse.setEnabled(true);
		dumpLocTF.setEnabled(true);
	}
	
	@Override
	public int processOptions() {
		// restore to alternate
		RestoreJobModel model = RestoreContext.getRestoreModel();
		if (dumpFileRd.getValue()) {
			model.setDestType(DestType.DumpFile.getValue());
			model.setDestinationPath(Utils.getNormalizedPath(dumpLocTF.getValue()));
		} else if (origLocRd.getValue()) {
			model.setDestType(DestType.OrigLoc.getValue());
			model.setDestinationPath("");
			processBackupVM(false);
		} else {
			model.setDestType(DestType.AlterLoc.getValue());
			model.setDestinationPath("");
			List<SQLModel> models = store.getModels();
			model.listOfSQLMode = models;
			for(int i = 0, count = models == null ? 0 : models.size(); i < count ;i++) {
				SQLModel m = models.get(i);
				m.setNewFileLoc(Utils.getNormalizedPath(m.getNewFileLoc()));
			}
		}

		model.setEncryptPassword(pwdPane.getPassword());
		return 0;
	}

	private Map<String, SQLModel> map = new FastMap<SQLModel>();

	@Override
	public void repaint() {
		super.repaint();
		updateStore();
	}
	
	private String getKey(String instName, String dbName) {
		return dbName + "@" + instName;
	}

	private ListStore<SQLModel> updateStore() {
		if (store != null && store.getCount() > 0) {
			List<SQLModel> models = store.getModels();
			for (SQLModel md : models) {
				String instName = md.getInstanceDisplayName();
				String dbName = md.getDbName();
				String key = getKey(instName, dbName);
				if (!map.containsKey(key)) {
					map.put(key, md);
				}
			}
		}

		List<GridTreeNode> nodeList = RestoreContext
				.getRestoreRecvPointSources();
		store = new ListStore<SQLModel>();

		if (nodeList != null) {
			TreeStore<GridTreeNode> treeStore = RestoreContext
					.getRestoreRecvPointTreeStore();

			boolean isWriter = false;
			for (GridTreeNode item : nodeList) {
				if (item.getType() != null
						&& item.getType() == SQLNodeType.WRITER.getValue()) {
					isWriter = true;
					if (alterLocRd.getValue()) {
						origLocRd.setValue(true);
					}
					alterLocRd.disable();
					break;
				}
			}
			RecoveryPointModel rp = restoreWizardWindow.getSelectedRecoveryPoint();
			
			if (!isWriter && rp.getVMHypervisor()!=1) { //1= hyperv. From hyperv vm session we won't support restore to original and alternate location.
				alterLocRd.enable();
				boolean isMasterSelected = false;
				for (GridTreeNode item : nodeList) {
					if (item.getType() != null
							&& item.getType() == SQLNodeType.DB.getValue()) {
						GridTreeNode parentNd = treeStore.getParent(item);

						if (masterDBname.equalsIgnoreCase(item.getName())) {
							isMasterSelected = true;
						}

						String key = getKey(parentNd.getName(), item.getName());

						String newDbDispName = "";
						String newFileLoc = "";
						if (map.containsKey(key)) {
							newDbDispName = map.get(key).getNewDbName();
							newFileLoc = map.get(key).getNewFileLoc();
						}
						if((newDbDispName == null || newDbDispName.length() == 0) && !masterDBname.equalsIgnoreCase(item.getName()))
						{
							newDbDispName = item.getName();
							if(newDbDispName.equalsIgnoreCase(modelDBName)) {
								newDbDispName += DB_SUFFIX;
							}
						}
						
						SQLModel sqlModel = new SQLModel(parentNd.getName(),
								parentNd.getDisplayName(), item.getName(), item
										.getDisplayName(), newDbDispName,
								newFileLoc, item.getFullPath() + "\\"
										+ item.getName());

						store.add(sqlModel);
					}
				}

				if (isMasterSelected) {
					noteLabel.setVisible(true);
				} else {
					noteLabel.setVisible(false);
				}
			}
		}
		grid.reconfigure(store, cm);
		return store;
	}

	public enum SQLNodeType {
		WRITER(CatalogModelType.OT_VSS_SQL_WRITER), INSTANCE(
				CatalogModelType.OT_VSS_SQL_LOGICALPATH), DB(
				CatalogModelType.OT_VSS_SQL_COMPONENT_SELECTABLE);
		private int value;

		private SQLNodeType(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}
}
