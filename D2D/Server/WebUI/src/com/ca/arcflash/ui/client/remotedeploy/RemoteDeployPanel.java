package com.ca.arcflash.ui.client.remotedeploy;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.BaseSimpleComboBox;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.HelpTopics;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.RemoteDeployStatus;
import com.ca.arcflash.ui.client.model.ServerInfoModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTip;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Widget;

public class RemoteDeployPanel extends LayoutContainer {
	final CommonServiceAsync service = GWT.create(CommonService.class);

	private RemoteDeployWindow parentWindow;
	private int minButtonWidth = 75;

	private ContentPanel contentPanel;
	private Button btnDeployNow, btnAdd, btnDel;

	private final int X86_PACKAGE = 1;
	private final int X64_PACKAGE = 2;

	private ColumnModel cm;
	private CheckBoxSelectionModel<ServerInfoModel> csm;
	private LabelField lfDeploying;
	

	public RemoteDeployPanel(RemoteDeployWindow parentWindow) {
		this.parentWindow = parentWindow;
		init();
	}

	private void init() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		csm = new ADTCheckBoxSelectionModel<ServerInfoModel>();
		ColumnConfig checkColumn = csm.getColumn();
		configs.add(checkColumn);

		ColumnConfig column = new ColumnConfig("serverName",
				UIContext.Constants.remoteDeployPanelServerName(), 100);
		column.setMenuDisabled(true);
		column.setStyle("vertical-align:middle;");
		TextField<String> serverNameText = new TextField<String>();
		serverNameText.ensureDebugId("a293cad3-7f8b-4f6d-b792-2a61e606afaa");
		serverNameText.setRegex(serverNameReg);
		serverNameText.getMessages().setRegexText(
				UIContext.Messages.remoteDeployMsgServerNameInvalid(UIContext.productNameD2D));
		serverNameText.setAllowBlank(false);
		configs.add(column);

		column = new ColumnConfig("userName", UIContext.Constants
				.remoteDeployPanelUserName(), 100);
		column.setStyle("vertical-align:middle;");
		column.setMenuDisabled(true);
		TextField<String> userNameText = new TextField<String>();
		userNameText.ensureDebugId("412736aa-89d9-48fa-821b-07cd34ba4ff7");
		userNameText.setAllowBlank(false);
		configs.add(column);

		column = new ColumnConfig("protocol", UIContext.Constants.remoteDeployPanelProtocol(), 80);
		column.setRenderer(new GridCellRenderer<ServerInfoModel>(){

			@Override
			public Object render(ServerInfoModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ServerInfoModel> store, Grid<ServerInfoModel> grid) {
				if(model.isUseHttps() != null && model.isUseHttps()) {
					return new LabelField(UIContext.Constants.remoteDeployPanelProtocolHTTPS());
				}else {
					return new LabelField(UIContext.Constants.remoteDeployPanelProtocolHTTP());
				}
			}
		});
		column.setStyle("vertical-align:middle;");		
		column.setMenuDisabled(true);		
		configs.add(column);
		
		column = new ColumnConfig("port", UIContext.Constants
				.remoteDeployPanelPort(), 40);
		column.setStyle("vertical-align:middle;");
		column.setMenuDisabled(true);
		NumberField portText = new NumberField();
		portText.ensureDebugId("742bf490-14b1-4464-867f-8f1b4cccb0ff");
		portText.setPropertyEditorType(Integer.class);
		portText.setAllowBlank(false);
		portText.setAllowDecimals(false);
		portText.setAllowNegative(false);
		portText.setMaxValue(65534);
		portText.setMinValue(1);
		portText.setValidateOnBlur(true);
		configs.add(column);

		column = new ColumnConfig("installPath", UIContext.Constants
				.remoteDeployPanelInstallPath(), 200);
		column.setStyle("vertical-align:middle;");
		column.setMenuDisabled(true);
		TextField<String> installPathText = new TextField<String>();

		installPathText.ensureDebugId("70ef85ae-5226-4a30-bddc-840c898487de");
		installPathText.setRegex(absoluteDirReg);
//		installPathText.getMessages().setRegexText(UIContext.Messages.remoteDeployMsgInstallPathInvalid(UIContext.productNameD2D,UIContext.productNameD2D));
		installPathText.getMessages().setRegexText(UIContext.Messages.remoteDeployMsgInstallPathInvalid(UIContext.productNameD2D));
		column.setRenderer(installPathRenderer);
		configs.add(column);

		final BaseSimpleComboBox<String> combo = new BaseSimpleComboBox<String>();
		combo.ensureDebugId("92c57330-c69a-4735-b007-b7a14019ac24");
		combo.setForceSelection(true);
		combo.setTriggerAction(TriggerAction.ALL);
		combo.add(UIContext.Constants.remoteDeployPanelYes());
		combo.add(UIContext.Constants.remoteDeployPanelNo());
		column = new ColumnConfig("reboot", UIContext.Constants
				.remoteDeployPanelIsReboot(), 50);
		column.setStyle("vertical-align:middle;");
		column.setMenuDisabled(true);
		column.setRenderer(reBootRenderer);
		configs.add(column);

		column = new ColumnConfig("deployPercentage", UIContext.Constants
				.remoteDeployPanelPercentage(), 70);
		column.setStyle("vertical-align:middle;");
		column.setRenderer(percentageRenderer);
		column.setNumberFormat(NumberFormat.getPercentFormat());
		column.setAlignment(HorizontalAlignment.LEFT);
		column.setMenuDisabled(true);
		configs.add(column);

		column = new ColumnConfig("deployStatus", UIContext.Constants
				.remoteDeployPanelDeployStatus(), 90);
		column.setStyle("vertical-align:middle;");
		column.setRenderer(statusRenderer);
		column.setAlignment(HorizontalAlignment.LEFT);
		column.setMenuDisabled(true);
		configs.add(column);

		column = new ColumnConfig("deployMessage", UIContext.Constants
				.remoteDeployPanelMessage(), 200);
		column.setAlignment(HorizontalAlignment.LEFT);
		column.setMenuDisabled(true);
		column.setRenderer(messageRenderer);
		configs.add(column);

		store = new ListStore<ServerInfoModel>();

		cm = new ColumnModel(configs);
	}

	@Override
	protected void onRender(Element parent, int index) {

		super.onRender(parent, index);
		this.setStyleAttribute("margin", "4px");

		this.setLayout(new FitLayout());
		LayoutContainer cp = new LayoutContainer();
		cp.setLayout(new RowLayout(Orientation.VERTICAL));

		ButtonBar topBar = new ButtonBar();
		btnAdd = new Button(UIContext.Constants.remoteDeployPanelAddServer()){

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
		Utils.addToolTip(btnAdd, UIContext.Constants
				.remoteDeployPanelAddServerTooltip());
		btnAdd.ensureDebugId("1ac9fc75-94f8-43f3-9d6f-9698578a54c6");
		btnAdd.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {

				AddServerWindow asw = new AddServerWindow(getStore(), grid);
				asw.setModal(true);
				asw.show();
				asw.addWindowListener(new WindowListener() {
					@Override
					public void windowHide(WindowEvent we) {
						if (getStore() != null && getStore().getCount() > 0) {
							contentPanel.setEnabled(true);
						} else {
							contentPanel.setEnabled(false);
						}
					}
				});
			}
		});

		btnDel = new Button(UIContext.Constants.remoteDeployPanelDelete()){

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
		// btnDel.setIcon(IconHelper.create("images/delete.gif"));
		Utils.addToolTip(btnDel, UIContext.Constants.remoteDeployPanelDeleteTooltip());
		btnDel.ensureDebugId("52d2ee67-f702-41e3-a621-c2e8d94dd03d");
		btnDel.setEnabled(false);

		lfDeploying = new LabelField(UIContext.Constants.remoteDeployServerListCantBeChangedDueToIsInDeploying());
		lfDeploying.setVisible(false);
		lfDeploying.setStyleAttribute("padding-left", "20px");
		topBar.add(btnAdd);
		topBar.add(btnDel);
		topBar.add(lfDeploying);

		topBar.setMinButtonWidth(minButtonWidth);
		topBar.setStyleAttribute("margin-bottom", "4px");

		RowData rowDataTopBar = new RowData(1, -1);
		cp.add(topBar, rowDataTopBar);

		contentPanel = new ContentPanel();
		contentPanel.setHeaderVisible(false);
		contentPanel.setCollapsible(false);
		contentPanel.setLayout(new FitLayout());
		contentPanel.setScrollMode(Scroll.NONE);
		contentPanel.setEnabled(false);

		grid = new Grid<ServerInfoModel>(store, cm);
		grid.setSelectionModel(csm);
		grid.setLoadMask(true);
		grid.setBorders(true);
		grid.setAutoExpandColumn("deployMessage");
		grid.addPlugin(csm);

		grid.addListener(Events.CellDoubleClick,
				new Listener<GridEvent<ServerInfoModel>>() {

					@Override
					public void handleEvent(GridEvent<ServerInfoModel> be) {
						if (!grid.getSelectionModel().isLocked()) {
							AddServerWindow asw = new AddServerWindow(
									getStore(), grid);
							asw.setSelectedIndex(be.getRowIndex());
							asw.setModal(true);
							asw.show();
						}
					}
				});

		contentPanel.add(grid);

		RowData rowDataGrid = new RowData(1, 1);
		cp.add(contentPanel, rowDataGrid);

		btnDeployNow = new Button(UIContext.Constants
				.remoteDeployPanelDeployNow()){

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
		
		btnDeployNow.ensureDebugId("baf6ac0a-7865-4fbb-b5dc-f1bdd22590ce");
		btnDeployNow.addSelectionListener(new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						List<String> serverNames = new ArrayList<String>();
						final List<ServerInfoModel> selectedItems = grid
								.getSelectionModel().getSelectedItems();
						if (selectedItems != null && selectedItems.size() > 0) {
							for (ServerInfoModel model : selectedItems) {
								serverNames.add(model.getServerName());
							}
						}
						if (serverNames.isEmpty()) {
							MessageBox messageBox = new MessageBox();
							messageBox.setTitleHtml(UIContext.Messages
									.messageBoxTitleError(UIContext.productNameD2D));
							messageBox.setMessage(UIContext.Constants
									.remoteDeployMsgNoSeletionDeploy());
							messageBox.setIcon(MessageBox.ERROR);
							messageBox.setModal(true);
							Utils.setMessageBoxDebugId(messageBox);
							messageBox.show();
						} else {

							disableUI(true);

							service.startDeploymentServers(serverNames,
									new BaseAsyncCallback<Void>() {

										@Override
										public void onFailure(Throwable caught) {
											super.onFailure(caught);
											disableUI(false);
										}

										@Override
										public void onSuccess(Void result) {
											for (ServerInfoModel model : selectedItems) {
												model
														.setDeployStatus(getRemoteDeployStatus(RemoteDeployStatus.DEPLOY_PENDING_FOR_DEPLOY
																.value()));	
												model.setDeployStatusCode(RemoteDeployStatus.DEPLOY_PENDING_FOR_DEPLOY.value());
												model
														.setDeployMessage(UIContext.Constants
																.remoteDeployStatusNA());
												grid.getStore().update(model);
											}

											Info.display(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D),
															UIContext.Constants.remoteDeployMsgDeployJobSubmitted());
										}
									});
						}
					}
				});

		Utils.addToolTip(btnDeployNow, UIContext.Messages
				.remoteDeployPanelDeployNowTooltip(UIContext.productNameD2D));
		btnDeployNow.setEnabled(false);

		//Button btnExit = new Button(UIContext.Constants.btnExitLabel());
		Button btnExit = new Button(UIContext.Constants
				.remoteDeployPanelCancelButton());
		btnExit.ensureDebugId("962644e1-a09d-48cf-874e-6e3c984acdb4");
		btnExit.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				parentWindow.hide();
			}
		});

		grid.getSelectionModel().addSelectionChangedListener(
				new SelectionChangedListener<ServerInfoModel>() {
					@Override
					public void selectionChanged(
							SelectionChangedEvent<ServerInfoModel> se) {
						List<ServerInfoModel> selItems = grid
								.getSelectionModel().getSelectedItems();

						if (selItems != null && selItems.size() > 0) {
							btnDel.setEnabled(true);
							btnDeployNow.setEnabled(true);
							updateDeployNowButton();
						} else {
							btnDel.setEnabled(false);
							btnDeployNow.setEnabled(false);
						}
					}
				});

		btnDel.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				final List<ServerInfoModel> selectedItems = grid
						.getSelectionModel().getSelectedItems();
				if (selectedItems != null) {
					if (selectedItems.size() > 0) {
						final List<ServerInfoModel> saveList = new ArrayList<ServerInfoModel>();
						for (int i = 0; i < store.getCount(); i++) {
							boolean isDel = false;
							for (ServerInfoModel model : selectedItems) {
								if (store
										.getAt(i)
										.getServerName()
										.equalsIgnoreCase(model.getServerName())) {
									isDel = true;
									break;
								}
							}
							if (!isDel) {
								saveList.add(store.getAt(i));
							}
						}

						service.setDeploymentServers(saveList,
								new BaseAsyncCallback<Void>() {
									@Override
									public void onFailure(Throwable caught) {
										super.onFailure(caught);
									}

									@Override
									public void onSuccess(Void result) {
										for (ServerInfoModel model : selectedItems) {
											store.remove(model);
										}

										grid.getView().refresh(true);

										if (store.getCount() > 0) {
											contentPanel.setEnabled(true);
										} else {
											contentPanel.setEnabled(false);
										}

										// Update buttons.
										List<ServerInfoModel> selItems = grid
												.getSelectionModel()
												.getSelectedItems();

										if (selItems != null) {
											if (selItems.size() > 0) {
												btnDel.setEnabled(true);
												btnDeployNow.setEnabled(true);
											} else {
												btnDel.setEnabled(false);
												btnDeployNow.setEnabled(false);
											}
										} else {
											btnDel.setEnabled(false);
											btnDeployNow.setEnabled(false);
										}
									}
								});
					} else {
						MessageBox messageBox = new MessageBox();
						messageBox.setTitleHtml(UIContext.Messages
								.messageBoxTitleError(UIContext.productNameD2D));
						messageBox.setMessage(UIContext.Constants
								.remoteDeployMsgNoSeletionDelete());
						messageBox.setIcon(MessageBox.ERROR);
						messageBox.setModal(true);
						Utils.setMessageBoxDebugId(messageBox);
						messageBox.show();
					}

				}
			}
		});

		if (UIContext.serverVersionInfo.getLocalADTPackage() != null) {

			int localADT = UIContext.serverVersionInfo.getLocalADTPackage();
			String note = null;
			if (localADT == X86_PACKAGE) {
				note = UIContext.Messages
						.remoteDeployNotex86ToCompletePackage(UIContext.productNameD2D,UIContext.productNameD2D);
			} else if (localADT == X64_PACKAGE) {
				note = UIContext.Messages
						.remoteDeployNotex64ToCompletePackage(UIContext.productNameD2D,UIContext.productNameD2D);
			}

			if (note != null) {
				LabelField lb = new LabelField(note);
				lb.setStyleAttribute("margin-bottom", "4px");
				lb.setStyleAttribute("margin-top", "4px");
				RowData rowDataNote = new RowData(1, -1);
				cp.add(lb, rowDataNote);
			}
		}
		
		String link = UIContext.externalLinks.getRemoteDeployHelp();
		if(UIContext.uiType == 1){
			link = UIContext.externalLinks.getVMRemoteDeployHelp();
		}
		Button helpButton = HelpTopics.createHelpButton(link, -1);

		ButtonBar bottomBar = new ButtonBar();

		bottomBar.add(btnDeployNow);
		bottomBar.add(btnExit);
		bottomBar.add(helpButton);
		bottomBar.setMinButtonWidth(minButtonWidth);
		bottomBar.setStyleAttribute("margin-bottom", "4px");
		bottomBar.setAlignment(HorizontalAlignment.RIGHT);
		RowData rowDataBottomBar = new RowData(1, -1);
		cp.add(bottomBar, rowDataBottomBar);

		add(cp);
	}

	private void disableUI(boolean isDeploying) {
		if (!isDeploying) {
			if (grid.getSelectionModel().getSelectedItems() != null
					&& grid.getSelectionModel().getSelectedItems().size() > 0) {
				btnDeployNow.setEnabled(true);
				btnDel.setEnabled(true);
			}
		} else {
			btnDeployNow.setEnabled(false);
			btnDel.setEnabled(false);
		}

		btnAdd.setEnabled(!isDeploying);
		grid.getSelectionModel().setLocked(isDeploying);
		grid.getView().setSortingEnabled(!isDeploying);
		lfDeploying.setVisible(isDeploying);
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		loadModel();
		timer = new RefreshStatusTimer();
		timer.scheduleRepeating(5000);
	}

	@Override
	protected void onUnload() {
		cancelTimer();
	}

	private void cancelTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}

	private void loadModel() {
		service
				.getDeploymentServers(new BaseAsyncCallback<List<ServerInfoModel>>() {

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
					}

					@Override
					public void onSuccess(List<ServerInfoModel> result) {
						if (result != null) {
							List<ServerInfoModel> items = new ArrayList<ServerInfoModel>();
							for (ServerInfoModel si : result) {
								si.setDeployStatus(getRemoteDeployStatus(si
										.getDeployStatusCode()));
								if (si.getDeployMessage() == null
										|| si.getDeployMessage().isEmpty()) {
									si.setDeployMessage(UIContext.Constants
											.remoteDeployStatusNA());
								}
								if (si.isSelected() && !isDeploySuccessful(si)) {
									items.add(si);
								}
							}
							if (result.size() > 0) {
								contentPanel.setEnabled(true);
							} else {
								contentPanel.setEnabled(false);
							}
							store.add(result);
							grid.getSelectionModel().select(items, false);
						}
					}
				});
	}

	private class RefreshStatusTimer extends Timer {
		@Override
		public void run() {
			service
					.getDeploymentServers(new BaseAsyncCallback<List<ServerInfoModel>>() {

						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
							updateDeployNowButton();
						}

						@Override
						public void onSuccess(List<ServerInfoModel> result) {
							if (result != null) {
								List<ServerInfoModel> currentModels = store
										.getModels();
								for (ServerInfoModel ret : result) {
									for (ServerInfoModel current : currentModels) {
										if (ret
												.getServerName()
												.equalsIgnoreCase(
														current.getServerName())) {

											boolean isStatusUpdate = false;
											boolean isPercentUpdate = false;
											boolean isMessageUpdate = false;

											if (ret.getDeployPercentage() != null) {
												if (current
														.getDeployPercentage() == null
														|| ret
																.getDeployPercentage()
																.intValue() != current
																.getDeployPercentage()
																.intValue()) {
													current
															.setDeployPercentage(ret
																	.getDeployPercentage());
													isPercentUpdate = true;

												}
											}

											if (ret.getDeployStatusCode().intValue() != current
													.getDeployStatusCode().intValue()) {
												current.setDeployStatusCode(ret
														.getDeployStatusCode());
												current
														.setDeployStatus(getRemoteDeployStatus(ret
																.getDeployStatusCode()));
												isStatusUpdate = true;
											}

											if (ret.getDeployMessage() != null
													&& !ret
															.getDeployMessage()
															.equals(
																	current
																			.getDeployMessage())) {
												current.setDeployMessage(ret
														.getDeployMessage());
												isMessageUpdate = true;
											}

											if (isStatusUpdate
													|| isPercentUpdate
													|| isMessageUpdate) {
												int rowIndex = grid
														.getView()
														.findRowIndex(
																grid
																		.getView()
																		.getRow(
																				current));
												if (isPercentUpdate) {
													int percentColumnIndex = grid
															.getColumnModel()
															.getColumnCount() - 3;

													grid
															.getView()
															.getCell(rowIndex,
																	percentColumnIndex)
															.getFirstChildElement()
															.setInnerText(
																	percentFmt
																			.format(int2Percentage(current
																					.getDeployPercentage())));
												}

												if (isStatusUpdate) {

													int statusColumnIndex = grid
															.getColumnModel()
															.getColumnCount() - 2;
													
													Widget widget = grid
													.getView()
													.getWidget(
															rowIndex,
															statusColumnIndex);

													if (widget instanceof LabelField) {
														LabelField msglb = (LabelField) widget;
		
														msglb
																.setValue(current
																		.getDeployStatus());
														Utils.addToolTip(msglb, current.getDeployStatus());
													}
													
													if(isDeploySuccessful(ret)) {
														boolean isLocked = grid.getSelectionModel().isLocked();
														grid.getSelectionModel().setLocked(false);
														grid.getSelectionModel().deselect(current);
														grid.getSelectionModel().setLocked(isLocked);
													}
												}

												if (isMessageUpdate) {
													int messageColumnIndex = grid
															.getColumnModel()
															.getColumnCount() - 1;

													Widget widget = grid
															.getView()
															.getWidget(
																	rowIndex,
																	messageColumnIndex);

													if (widget instanceof LabelField) {
														LabelField msglb = (LabelField) widget;

														msglb
																.setValue(current
																		.getDeployMessage());
														
														Utils.addToolTip(msglb, current.getDeployMessage());
													}

												}
											}
											break;
										}
									}
								}
							}

							updateDeployNowButton();
						}
					});
		}
	}

	private void updateDeployNowButton() {
		List<String> serverNames = new ArrayList<String>();
		List<ServerInfoModel> selectedItems = grid.getSelectionModel()
				.getSelectedItems();
		if (selectedItems != null && selectedItems.size() > 0) {
			for (ServerInfoModel model : selectedItems) {
				if (RemoteDeployStatus.valueOf(model.getDeployStatusCode()) != null) {
					switch (RemoteDeployStatus.valueOf(model
							.getDeployStatusCode())) {
					case DEPLOY_IN_PROGRESS:
					case DEPLOY_THIRD_PARTY:
					case DEPLOY_COPYING_IMAGE:
					case DEPLOY_WAITING:
					case DEPLOY_PENDING_FOR_DEPLOY:
						serverNames.add(model.getServerName());
						break;
					}
				}
			}
		}

		if (serverNames.size() > 0) {
			disableUI(true);
		} else {
			disableUI(false);
		}
	}

	private String getRemoteDeployStatus(int status) {

		if (RemoteDeployStatus.valueOf(status) == null) {
			return UIContext.Constants.remoteDeployStatusNA();
		}
		switch (RemoteDeployStatus.valueOf(status)) {
		case DEPLOY_IN_PROGRESS:
			return UIContext.Constants.remoteDeployStatusDeployInProcess();

		case DEPLOY_SUCCESS:
			return UIContext.Constants.remoteDeployStatusDeploySuccess();

		case DEPLOY_FAILED:
			return UIContext.Constants.remoteDeployStatusDeployFailed();

		case DEPLOY_NOT_STARTED:
			return UIContext.Constants.remoteDeployStatusDeployNotStarted();

		case DEPLOY_THIRD_PARTY:
			return UIContext.Constants.remoteDeployStatusDeployThirdParty();

		case DEPLOY_COPYING_IMAGE:
			return UIContext.Constants.remoteDeployStatusDeployCopyingImage();

		case DEPLOY_WAITING:
			return UIContext.Constants.remoteDeployStatusDeployWaiting();

		case DEPLOY_PENDING_FOR_DEPLOY:
			return UIContext.Constants
					.remoteDeployStatusDeployPendingForDeploy();
			
		case DEPLOY_TEMINATE:
			return UIContext.Constants.remoteDeployStatusTerminateForReboot();			

		default:
			return UIContext.Constants.remoteDeployStatusNA();
		}

	}

	public ListStore<ServerInfoModel> getStore() {
		return store;
	}

	// //////////////////Private Fields/////////////////////////////////

	private ListStore<ServerInfoModel> store;
	private Grid<ServerInfoModel> grid;
	private Timer timer;

	final NumberFormat percentFmt = NumberFormat.getPercentFormat();

	private String boolean2String(boolean isTrue) {
		return isTrue ? UIContext.Constants.remoteDeployPanelYes()
				: UIContext.Constants.remoteDeployPanelNo();
	}

	private GridCellRenderer<ServerInfoModel> reBootRenderer = new GridCellRenderer<ServerInfoModel>() {

		@Override
		public Object render(ServerInfoModel model, String property,
				com.extjs.gxt.ui.client.widget.grid.ColumnData config,
				int rowIndex, int colIndex, ListStore<ServerInfoModel> store,
				Grid<ServerInfoModel> grid) {
			return boolean2String((Boolean) model.get(property));
		}
	};

	private GridCellRenderer<ServerInfoModel> percentageRenderer = new GridCellRenderer<ServerInfoModel>() {

		@Override
		public Object render(ServerInfoModel model, String property,
				com.extjs.gxt.ui.client.widget.grid.ColumnData config,
				int rowIndex, int colIndex, ListStore<ServerInfoModel> store,
				Grid<ServerInfoModel> grid) {
			if(model.getDeployStatusCode() == RemoteDeployStatus.DEPLOY_NA.value())
				return UIContext.Constants.NA();
			return percentFmt
					.format(int2Percentage(model.getDeployPercentage()));
		}
	};

	private double int2Percentage(int deployPercentage) {
		return deployPercentage / 100.0;
	}

	private boolean isDeploySuccessful(ServerInfoModel si) {
		return si.getDeployStatusCode() == RemoteDeployStatus.DEPLOY_SUCCESS.value();
	}

	private GridCellRenderer<ServerInfoModel> pswRenderer = new GridCellRenderer<ServerInfoModel>() {

		@Override
		public Object render(ServerInfoModel model, String property,
				com.extjs.gxt.ui.client.widget.grid.ColumnData config,
				int rowIndex, int colIndex, ListStore<ServerInfoModel> store,
				Grid<ServerInfoModel> grid) {
			String pwd = model.get(property);
			if (pwd == null)
				return "";
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < pwd.length(); i++) {
				sb.append("*");
			}
			return sb.toString();
		}
	};

	private GridCellRenderer<ServerInfoModel> installPathRenderer = new GridCellRenderer<ServerInfoModel>() {

		@Override
		public Object render(ServerInfoModel model, String property,
				ColumnData config, int rowIndex, int colIndex,
				ListStore<ServerInfoModel> store, Grid<ServerInfoModel> grid) {
			String installPath = model.getInstallPath();
			if (model != null && installPath != null) {
				LabelField lf = new LabelField();
				lf.setStyleName("x-grid3-cell-inner");
				lf.setValue(installPath);
				Utils.addToolTip(lf, installPath);
				return lf;
			}
			return "";
		}
	};

	private GridCellRenderer<ServerInfoModel> messageRenderer = new GridCellRenderer<ServerInfoModel>() {

		@Override
		public Object render(ServerInfoModel model, String property,
				ColumnData config, int rowIndex, int colIndex,
				ListStore<ServerInfoModel> store, Grid<ServerInfoModel> grid) {
			String deployMessage = model.getDeployMessage();
			if (model != null && deployMessage != null) {
				ToolTipLabelField lfMessage = new ToolTipLabelField();
				lfMessage.ensureDebugId("c1b091ec-06d8-4cf6-b52b-7eb5109ebf10");
				lfMessage.setStyleAttribute("white-space", "normal");
				lfMessage.setStyleAttribute("font-size", "11px");
				lfMessage.setValue(deployMessage);
				ToolTip msgTip = Utils.addToolTip(lfMessage, deployMessage);
				if(lfMessage.getToolTip() == null)
					lfMessage.setToolTip(msgTip);
				return lfMessage;
			}
			return "";
		}
	};
	
	private GridCellRenderer<ServerInfoModel> statusRenderer = new GridCellRenderer<ServerInfoModel>() {

		@Override
		public Object render(ServerInfoModel model, String property,
				ColumnData config, int rowIndex, int colIndex,
				ListStore<ServerInfoModel> store, Grid<ServerInfoModel> grid) {
			String deployStatus = model.getDeployStatus();
			if (model != null && deployStatus != null) {
				ToolTipLabelField lfStatus = new ToolTipLabelField();
				lfStatus.ensureDebugId("eb1a44fb-119a-4660-9dd9-387fd525dd9f");
				lfStatus.setStyleAttribute("font-size", "11px");
				lfStatus.setStyleAttribute("white-space", "normal");
				ToolTip statusTip = Utils.addToolTip(lfStatus, deployStatus);
				if(lfStatus.getToolTip() == null) {
					lfStatus.setToolTip(statusTip);
				}
				lfStatus.setValue(deployStatus);
				return lfStatus;
			}
			return "";
		}
	};

	private static class ToolTipLabelField extends LabelField {
		public void setToolTip(ToolTip tip) {
			toolTip = tip;
		}
	}
	

	private static class ADTCheckBoxSelectionModel<M extends ModelData> extends
			CheckBoxSelectionModel<ServerInfoModel> {
		@Override
		protected void handleMouseDown(GridEvent<ServerInfoModel> e) {
			if (!this.isLocked()) {
				super.handleMouseDown(e);
			}
		}

		@Override
		protected void onHeaderClick(GridEvent<ServerInfoModel> e) {
			if (!this.isLocked()) {
				super.onHeaderClick(e);
			}
		}
	}

	// Note:Special meaning chars for pattern:[\^$.|?*+()
	// `~!@#$^&*()=+[]{}\|;:'",<>/?
	// pattern: not `~!@#\$\^&\*\(\)=\+\[\]{}\\\|;:'",<>/\?
	private String serverNameReg = "[^`~!@#\\$\\^&\\*\\(\\)=\\+\\[\\]{}\\\\\\|;:'\",<>/\\?]+";
	// \/:*?"<>|
	// pattern: not \\/:\*\?"<>\|
	private String directroy = "[^\\\\/:\\*\\?\"<>\\|]+";
	private String absoluteDirReg = "^([A-Za-z]{1}:|%ProgramFiles%)" + "(\\\\"
			+ directroy + ")*(\\\\)?$";
}
