package com.ca.arcflash.ui.client.vsphere.vmbackup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.BaseLicenseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.HelpTopics;
import com.ca.arcflash.ui.client.common.PathSelectionPanel;
import com.ca.arcflash.ui.client.common.UserPasswordWindow;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.homepage.HomepageService;
import com.ca.arcflash.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.BackupScheduleIntervalUnitModel;
import com.ca.arcflash.ui.client.model.BackupScheduleModel;
import com.ca.arcflash.ui.client.model.BackupTypeModel;
import com.ca.arcflash.ui.client.model.BackupVMModel;
import com.ca.arcflash.ui.client.model.DestinationCapacityModel;
import com.ca.arcflash.ui.client.model.VMBackupSettingModel;
import com.ca.arcflash.ui.client.model.VMItemModel;
import com.ca.arcflash.ui.client.model.VSphereBackupSettingModel;
import com.ca.arcflash.ui.client.model.VirtualCenterNodeModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ToggleButton;

public class VMBackupSettingWindow extends Window {

	private VMBackupSettingWindow thisWindow;

	// TODO Default data-1

	public VMBackupSettingModel vmModel;

	final LoginServiceAsync service = GWT.create(LoginService.class);
	private final CommonServiceAsync commonService = GWT
			.create(CommonService.class);
	final HomepageServiceAsync homeService = GWT.create(HomepageService.class);

	public static final int DEFAULT_RETENTION_COUNT = 31;

	private ScheduleSettings schedule;
	private BackupSettings settings;
	private AdvancedSettings advanced;
	private DestinationSettings destination;
	// private VirtualCenterWindow vc;

	final LayoutContainer destinationContainer;
	final LayoutContainer scheduleContainer;
	final LayoutContainer settingsContainer;
	final LayoutContainer advancedContainer;

	private DeckPanel deckPanel;
	// private LayoutContainer sp;
	// private final CardLayout layout;

	private ToggleButton scheduleButton;
	private ToggleButton settingsButton;
	private ToggleButton advancedButton;
	private ToggleButton destinationButton;

	private ToggleButton scheduleLabel;
	private ToggleButton settingsLabel;
	private ToggleButton advancedLabel;
	private ToggleButton destinationLabel;

	private ClickHandler scheduleButtonHandler;
	private ClickHandler settingsButtonHandler;
	private ClickHandler advancedButtonHandler;
	private ClickHandler destinationButtonHandler;

	private Button cancelButton;

	private VerticalPanel toggleButtonPanel;

	public DestinationCapacityModel destModel;

	public final int STACK_DESTINATION = 0;
	public final int STACK_SCHEDULE = 1;
	public final int STACK_SETTINGS = 2;
	public final int STACK_ADVANCED = 3;

	public static final long AF_ERR_DEST_SYSVOL = 3758096417l;
	public static final long AF_ERR_DEST_BOOTVOL = 3758096418l;
	public static final String ERR_REMOTE_DEST_WINSYSMSG = "17179869199";

	public VMBackupSettingWindow() {
		this.setResizable(false);
		thisWindow = this;
		TableLayout topLayout = new TableLayout();
		topLayout.setHeight("100%");
		topLayout.setWidth("100%");
		topLayout.setCellVerticalAlign(VerticalAlignment.TOP);
		topLayout.setColumns(2);
		this.setLayout(topLayout);
		this.setStyleAttribute("background-color", "#DFE8F6");

		this.setWidth(840);
		this.setHeight(600);

		deckPanel = new DeckPanel();
		deckPanel.setStyleName("backupSettingCenter");
		deckPanel.setHeight("520px");

		// layout = new CardLayout();
		// layout.setDeferredRender(false);
		// sp.setLayout(layout);

		// StartPanel start=new StartPanel();
		// LayoutContainer startContainer=new LayoutContainer();
		// startContainer.add(start.Render());
		// startContainer.setStyleAttribute("padding", "10px");
		// deckPanel.add(startContainer);

		destination = new DestinationSettings(this);
		destinationContainer = new LayoutContainer();
		destinationContainer.add(destination.Render());
		destinationContainer.setStyleAttribute("padding", "10px");
		deckPanel.add(destinationContainer);

		schedule = new ScheduleSettings(this);
		scheduleContainer = new LayoutContainer();
		scheduleContainer.add(schedule.Render());
		scheduleContainer.setStyleAttribute("padding", "10px");
		scheduleContainer.setStyleName("backupsetting_schedule_panel");
		deckPanel.add(scheduleContainer);

		settings = new BackupSettings(this);
		settingsContainer = new LayoutContainer();
		settingsContainer.add(settings.Render());
		settingsContainer.setStyleAttribute("padding", "10px");
		settingsContainer.setStyleName("backupsetting_inner_panel");
		deckPanel.add(settingsContainer);

		advanced = new AdvancedSettings(this);
		advancedContainer = new LayoutContainer();
		advancedContainer.add(advanced.Render());
		advancedContainer.setStyleAttribute("padding", "10px");
		deckPanel.add(advancedContainer);

		toggleButtonPanel = new VerticalPanel();
		toggleButtonPanel.setVerticalAlign(VerticalAlignment.MIDDLE);
		toggleButtonPanel.setHorizontalAlign(HorizontalAlignment.CENTER);
		toggleButtonPanel.setHeight(520);
		toggleButtonPanel.setStyleAttribute("background-color", "#DFE8F6");

		destinationButton = new ToggleButton(AbstractImagePrototype.create(UIContext.IconBundle
				.backupDestination()).createImage());
		destinationButton.ensureDebugId("B6C64B03-9375-4515-B570-C61CB9416553");
		destinationButton.setStylePrimaryName("demo-ToggleButton");
		destinationButton.setDown(true);

		destinationButtonHandler = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				deckPanel.showWidget(STACK_DESTINATION);
				destinationButton.setDown(true);
				advancedButton.setDown(false);
				settingsButton.setDown(false);
				scheduleButton.setDown(false);
				destinationLabel.setDown(true);
				advancedLabel.setDown(false);
				settingsLabel.setDown(false);
				scheduleLabel.setDown(false);
				// Updating heading.
				setHeadingHtml(UIContext.Messages
						.backupSettingsWindowWithTap(UIContext.Constants
								.backupSettingsDestination()));
			}
		};
		scheduleButtonHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				deckPanel.showWidget(STACK_SCHEDULE);
				destinationButton.setDown(false);
				advancedButton.setDown(false);
				settingsButton.setDown(false);
				scheduleButton.setDown(true);

				destinationLabel.setDown(false);
				advancedLabel.setDown(false);
				settingsLabel.setDown(false);
				scheduleLabel.setDown(true);
				// Updating heading.
				setHeadingHtml(UIContext.Messages
						.backupSettingsWindowWithTap(UIContext.Constants
								.backupSettingsSchedule()));
			}

		};

		settingsButtonHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				deckPanel.showWidget(STACK_SETTINGS);
				destinationButton.setDown(false);
				advancedButton.setDown(false);
				settingsButton.setDown(true);
				scheduleButton.setDown(false);

				destinationLabel.setDown(false);
				advancedLabel.setDown(false);
				settingsLabel.setDown(true);
				scheduleLabel.setDown(false);
				// Updating heading.
				setHeadingHtml(UIContext.Messages
						.backupSettingsWindowWithTap(UIContext.Constants
								.backupSettingsSettings()));
			}

		};

		advancedButtonHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				deckPanel.showWidget(STACK_ADVANCED);
				destinationButton.setDown(false);
				advancedButton.setDown(true);
				settingsButton.setDown(false);
				scheduleButton.setDown(false);

				destinationLabel.setDown(false);
				advancedLabel.setDown(true);
				settingsLabel.setDown(false);
				scheduleLabel.setDown(false);
				// Updating heading.
				setHeadingHtml(UIContext.Messages
						.backupSettingsWindowWithTap(UIContext.Constants
								.backupSettingsPrePost()));
			}

		};

		destinationButton.addClickHandler(destinationButtonHandler);

		/*
		 * tip = new ToolTip((Component)destinationButton, tipConfig);
		 * destinationButton.addSelectionListener(new
		 * SelectionListener<ButtonEvent>(){
		 * 
		 * @Override public void componentSelected(ButtonEvent ce) {
		 * sp.showStack(STACK_DESTINATION); advancedButton.toggle(false);
		 * settingsButton.toggle(false); scheduleButton.toggle(false);
		 * destinationButton.toggle(true);
		 * 
		 * // Updating heading.
		 * setHeading(UIContext.Messages.backupSettingsWindowWithTap
		 * (UIContext.Constants.backupSettingsDestination())); }
		 * 
		 * });
		 */
		toggleButtonPanel.add(destinationButton);
		destinationLabel = new ToggleButton(UIContext.Constants
				.backupSettingsDestination());
		destinationLabel.ensureDebugId("F235267C-25D0-49bb-BB40-BF149DD2B7D3");
		destinationLabel.setStylePrimaryName("tb-settings");
		destinationLabel.setDown(true);
		destinationLabel.addClickHandler(destinationButtonHandler);
		toggleButtonPanel.add(destinationLabel);

		scheduleButton = new ToggleButton(AbstractImagePrototype.create(UIContext.IconBundle.backupSchedule())
				.createImage());
		scheduleButton.ensureDebugId("2543DCA4-E5E7-45cd-B27E-DFE8A16328F7");
		scheduleButton.setStylePrimaryName("demo-ToggleButton");
		scheduleButton.addClickHandler(scheduleButtonHandler);
		/*
		 * tip = new ToolTip(scheduleButton, tipConfig);
		 * scheduleButton.setHeight(100); scheduleButton.setWidth(100);
		 * scheduleButton.addSelectionListener(new
		 * SelectionListener<ButtonEvent>(){
		 * 
		 * @Override public void componentSelected(ButtonEvent ce) {
		 * sp.showStack(STACK_SCHEDULE); advancedButton.toggle(false);
		 * settingsButton.toggle(false); scheduleButton.toggle(true);
		 * destinationButton.toggle(false);
		 * 
		 * // Updating heading.
		 * setHeading(UIContext.Messages.backupSettingsWindowWithTap
		 * (UIContext.Constants.backupSettingsSchedule()));
		 * 
		 * }
		 * 
		 * });
		 */
		toggleButtonPanel.add(scheduleButton);
		scheduleLabel = new ToggleButton(UIContext.Constants
				.backupSettingsSchedule());
		scheduleLabel.ensureDebugId("AC2288DE-271C-4aa9-916E-9F61A5D11723");
		scheduleLabel.setStylePrimaryName("tb-settings");
		scheduleLabel.addClickHandler(scheduleButtonHandler);
		toggleButtonPanel.add(scheduleLabel);

		settingsButton = new ToggleButton(AbstractImagePrototype.create(UIContext.IconBundle.backupSettings())
				.createImage());
		settingsButton.ensureDebugId("2896F446-8C04-453a-BB34-5CA7141C7B5A");
		settingsButton.setStylePrimaryName("demo-ToggleButton");
		settingsButton.addClickHandler(settingsButtonHandler);
		/*
		 * tip = new ToolTip(settingsButton, tipConfig);
		 * settingsButton.setHeight(100); settingsButton.setWidth(100);
		 * settingsButton.addSelectionListener(new
		 * SelectionListener<ButtonEvent>(){
		 * 
		 * @Override public void componentSelected(ButtonEvent ce) {
		 * sp.showStack(STACK_SETTINGS); advancedButton.toggle(false);
		 * settingsButton.toggle(true); scheduleButton.toggle(false);
		 * destinationButton.toggle(false);
		 * 
		 * // Updating heading.
		 * setHeading(UIContext.Messages.backupSettingsWindowWithTap
		 * (UIContext.Constants.backupSettingsSettings())); }
		 * 
		 * });
		 */
		toggleButtonPanel.add(settingsButton);
		settingsLabel = new ToggleButton(UIContext.Constants
				.backupSettingsSettings());
		settingsLabel.ensureDebugId("A01FCC12-D6DF-409e-81CB-164E96FE9FFF");
		settingsLabel.setStylePrimaryName("tb-settings");
		settingsLabel.addClickHandler(settingsButtonHandler);
		toggleButtonPanel.add(settingsLabel);

		advancedButton = new ToggleButton(AbstractImagePrototype.create(UIContext.IconBundle.backupAdvanced())
				.createImage());
		advancedButton.ensureDebugId("A3D185D4-61BC-4a14-94A1-820D7CC585D2");
		advancedButton.setStylePrimaryName("demo-ToggleButton");
		advancedButton.addClickHandler(advancedButtonHandler);
		/*
		 * tip = new ToolTip(advancedButton, tipConfig);
		 * advancedButton.setHeight(100); advancedButton.setWidth(100);
		 * advancedButton.addSelectionListener(new
		 * SelectionListener<ButtonEvent>(){
		 * 
		 * @Override public void componentSelected(ButtonEvent ce) {
		 * sp.showStack(STACK_ADVANCED); advancedButton.toggle(true);
		 * settingsButton.toggle(false); scheduleButton.toggle(false);
		 * destinationButton.toggle(false);
		 * 
		 * // Updating heading.
		 * setHeading(UIContext.Messages.backupSettingsWindowWithTap
		 * (UIContext.Constants.backupSettingsAdvanced())); }
		 * 
		 * });
		 */
		toggleButtonPanel.add(advancedButton);
		advancedLabel = new ToggleButton(UIContext.Constants
				.backupSettingsAdvanced());
		advancedLabel.ensureDebugId("1D5A58A0-F986-4ed0-9E87-9245C6D574E3");
		advancedLabel.setStylePrimaryName("tb-settings");
		advancedLabel.addClickHandler(advancedButtonHandler);
		toggleButtonPanel.add(advancedLabel);

		this.add(toggleButtonPanel);

		TableData tableData = new TableData();
		tableData.setWidth("90%");
		this.add(deckPanel, tableData);

		LayoutContainer buttonContainer = new LayoutContainer();
		buttonContainer.setStyleAttribute("background-color", "#DFE8F6");
		buttonContainer.setHeight(80);

		TableLayout tableLayout = new TableLayout();
		tableLayout.setWidth("100%");
		tableLayout.setCellPadding(4);
		tableLayout.setCellSpacing(4);
		tableLayout.setColumns(4);

		// Repeat Section
		TableData td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.LEFT);
		td.setVerticalAlign(VerticalAlignment.BOTTOM);
		td.setWidth("100%");
		buttonContainer.setLayout(tableLayout);

		LabelField leftSpace = new LabelField();
		buttonContainer.add(leftSpace, td);

		td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.RIGHT);
		td.setVerticalAlign(VerticalAlignment.BOTTOM);

		
		buttonContainer.add(new HTML(""), td);

		cancelButton = new Button();
		cancelButton.ensureDebugId("5D4E378A-5E14-4253-B289-59C9AFEB14F3");
		cancelButton.setMinWidth(80);
		cancelButton.setText(UIContext.Constants.backupSettingsCancel());
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				// Cancel Clicked hide the dialog
				thisWindow.hide();
			}

		});
		td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.RIGHT);
		td.setVerticalAlign(VerticalAlignment.BOTTOM);
		buttonContainer.add(cancelButton, td);

		Button helpButton = new Button();
		helpButton.ensureDebugId("9BFE9E4A-925F-4528-AA0F-A0013ECE209F");
		helpButton.setMinWidth(80);
		helpButton.setText(UIContext.Constants.help());
		helpButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			private String url = UIContext.externalLinks
					.getBackupSettingsHelp();

			@Override
			public void componentSelected(ButtonEvent ce) {
				if (destinationButton.isDown())
					url = UIContext.externalLinks
							.getBackupSettingDestinationHelp();
				else if (scheduleButton.isDown())
					url = UIContext.externalLinks
							.getBackupSettingScheduleHelp();
				else if (settingsButton.isDown())
					url = UIContext.externalLinks
							.getBackupSettingSettingsHelp();
				else if (advancedButton.isDown())
					url = UIContext.externalLinks
							.getBackupSettingAdvancedHelp();
				HelpTopics.showHelpURL(url);
			}
		});

		td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.RIGHT);
		td.setVerticalAlign(VerticalAlignment.BOTTOM);
		buttonContainer.add(helpButton, td);

		td = new TableData();
		td.setWidth("100%");
		td.setColspan(2);
		td.setHorizontalAlign(HorizontalAlignment.RIGHT);
		td.setVerticalAlign(VerticalAlignment.MIDDLE);
		this.add(buttonContainer, td);

		// setHeading(UIContext.Constants.backupSettingsWindow());
		// Default Tab - destination.
		setHeadingHtml(UIContext.Messages
				.backupSettingsWindowWithTap(UIContext.Constants
						.backupSettingsDestination()));

		// Load the Backup Settings
		deckPanel.showWidget(STACK_DESTINATION);
	}

	@Override
	protected void afterShow() {
		super.afterShow();
		LoadSettings();
	}

	private void LoadSettings() {
		thisWindow.mask(UIContext.Constants.settingsLoadingConfigMaskText());
		service.getVMBackupConfiguration(UIContext.backupVM,
				new BaseAsyncCallback<VMBackupSettingModel>() {
					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						thisWindow.unmask();
					}

					@Override
					public void onSuccess(VMBackupSettingModel result) {
						if (result != null) {
							backupSettingFileExist_b = true;
							thisWindow.vmModel = result;
							// TODO ??!!!
							thisWindow.RefreshData();
							thisWindow.unmask();
						} else {
							service
									.getServerTime(new BaseAsyncCallback<Date>() {
										@Override
										public void onFailure(Throwable caught) {
											thisWindow.unmask();
											super.onFailure(caught);
										}

										@Override
										public void onSuccess(Date result) {
											thisWindow.vmModel = getDefaultModel();
											long serverTimeInMilliseconds = result
													.getTime();
											// set backup start time plus 5
											// minutes
											serverTimeInMilliseconds += 5 * 60 * 1000;
											vmModel
													.setBackupStartTime(serverTimeInMilliseconds);
											thisWindow.RefreshData();
											thisWindow.unmask();
										}

										private VMBackupSettingModel getDefaultModel() {

											backupSettingFileExist_b = false;
											VMBackupSettingModel model = new VMBackupSettingModel();
											// model = new
											// VSphereBackupSettingModel();

											model.fullSchedule = new BackupScheduleModel();
											model.fullSchedule
													.setEnabled(false);

											model.incrementalSchedule = new BackupScheduleModel();
											model.incrementalSchedule
													.setEnabled(true);
											model.incrementalSchedule
													.setInterval(1);
											model.incrementalSchedule
													.setIntervalUnit(BackupScheduleIntervalUnitModel.Day);

											model.resyncSchedule = new BackupScheduleModel();
											model.resyncSchedule
													.setEnabled(false);

											model
													.setRetentionCount(DEFAULT_RETENTION_COUNT);
											model.setCompressionLevel(1); // Standard
											model.setEnableEncryption(false);

											model.setPurgeSQLLogDays(0L);
											model.setPurgeExchangeLogDays(0L);

											return model;
										}
									});
						}
					}
				});
	}


	public void RefreshData() {
		schedule.RefreshData(vmModel);
		settings.RefreshData(vmModel);
		advanced.RefreshData(vmModel);
		destination.RefreshData(vmModel);
	}

	boolean backupSettingFileExist_b = false;


	@Override
	protected void onLoad() {
		super.onLoad();
		commonService.checkBLILic(new BaseLicenseAsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				UIContext.hasBLILic = false;
			}

			@Override
			public void onSuccess(Boolean result) {
				if (result != null) {
					UIContext.hasBLILic = result;
				} else {
					UIContext.hasBLILic = false;
				}
			}
		});

		commonService.getMaxRPLimit(new BaseAsyncCallback<Long>() {
			@Override
			public void onSuccess(Long result) {
				if (result != null && result > 0) {
					UIContext.maxRPLimit = result;
				} else {
					UIContext.maxRPLimit = UIContext.maxRPLimitDEFAULT;
				}
			}
		});
	}
}
