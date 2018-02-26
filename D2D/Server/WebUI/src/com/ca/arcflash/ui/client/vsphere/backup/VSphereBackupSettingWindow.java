package com.ca.arcflash.ui.client.vsphere.backup;

import java.util.Date;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.BaseLicenseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.HelpTopics;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.homepage.HomepageService;
import com.ca.arcflash.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.BackupScheduleIntervalUnitModel;
import com.ca.arcflash.ui.client.model.BackupScheduleModel;
import com.ca.arcflash.ui.client.model.BackupTypeModel;
import com.ca.arcflash.ui.client.model.D2DTimeModel;
import com.ca.arcflash.ui.client.model.DestinationCapacityModel;
import com.ca.arcflash.ui.client.model.JobMonitorModel;
import com.ca.arcflash.ui.client.model.VSphereBackupSettingModel;
import com.ca.arcflash.ui.client.model.VirtualCenterModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.DateWrapper;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.ToggleButton;

public class VSphereBackupSettingWindow extends Window {

	private VSphereBackupSettingWindow thisWindow;

	// TODO Default data-1
	public VSphereBackupSettingModel model = new VSphereBackupSettingModel();
	public VirtualCenterModel vcModel = new VirtualCenterModel();

	final LoginServiceAsync service = GWT.create(LoginService.class);
	private final CommonServiceAsync commonService = GWT
			.create(CommonService.class);
	final HomepageServiceAsync homeService = GWT.create(HomepageService.class);

	public static final int DEFAULT_RETENTION_COUNT = 31;

	private ScheduleSettings schedule;
	private BackupSettings settings;
	private AdvancedSettings advanced;
	private DestinationSettings destination;
	private VirtualCenterWindow vc;
	private EmailAlertSettings emailAlert;

	final LayoutContainer destinationContainer;
	final LayoutContainer scheduleContainer;
	final LayoutContainer settingsContainer;
	final LayoutContainer advancedContainer;
	final LayoutContainer emailAlertContainer;

	private DeckPanel deckPanel;
	// private LayoutContainer sp;
	// private final CardLayout layout;

	private ToggleButton scheduleButton;
	private ToggleButton settingsButton;
	private ToggleButton advancedButton;
	private ToggleButton destinationButton;
	private ToggleButton emailAlertButton;

	private ToggleButton scheduleLabel;
	private ToggleButton settingsLabel;
	private ToggleButton advancedLabel;
	private ToggleButton destinationLabel;
	private ToggleButton emailAlertLabel;

	private ClickHandler scheduleButtonHandler;
	private ClickHandler settingsButtonHandler;
	private ClickHandler advancedButtonHandler;
	private ClickHandler destinationButtonHandler;
	private ClickHandler emailAlertButtonHandler;

	private Button okButton;
	private Button cancelButton;

	private VerticalPanel toggleButtonPanel;

	public DestinationCapacityModel destModel;
	
	private Timer timer;

	public final int STACK_DESTINATION = 0;
	public final int STACK_SCHEDULE = 1;
	public final int STACK_SETTINGS = 2;
	public final int STACK_ADVANCED = 3;
	public final int STACK_EMAILALERT = 4;

	public static final long AF_ERR_DEST_SYSVOL = 3758096417l;
	public static final long AF_ERR_DEST_BOOTVOL = 3758096418l;
	public static final String ERR_REMOTE_DEST_WINSYSMSG = "17179869199";

	public VSphereBackupSettingWindow() {
		this.setResizable(false);
		thisWindow = this;
		/*TableLayout topLayout = new TableLayout();
		topLayout.setHeight("100%");
		topLayout.setWidth("100%");
		topLayout.setCellVerticalAlign(VerticalAlignment.TOP);
		topLayout.setColumns(2);
		this.setLayout(topLayout);
		this.setStyleAttribute("background-color", "#DFE8F6");*/
		LayoutContainer contentPanel = new LayoutContainer();
		contentPanel.setLayout( new RowLayout( Orientation.HORIZONTAL ) );
		this.setLayout( new RowLayout( Orientation.VERTICAL ) );
		this.setStyleAttribute("background-color", "#DFE8F6");

		this.setWidth(840);
		this.setHeight(640);

		deckPanel = new DeckPanel();
		deckPanel.setStyleName("backupSettingCenter");
		//deckPanel.setHeight("520px");

		// layout = new CardLayout();
		// layout.setDeferredRender(false);
		// sp.setLayout(layout);

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
		
		emailAlert = new EmailAlertSettings(this);
		emailAlertContainer = new LayoutContainer();
		emailAlertContainer.add(emailAlert.Render());
		emailAlertContainer.setStyleAttribute("padding", "10px");
		deckPanel.add(emailAlertContainer);

		toggleButtonPanel = new VerticalPanel();
		toggleButtonPanel.setVerticalAlign(VerticalAlignment.MIDDLE);
		toggleButtonPanel.setHorizontalAlign(HorizontalAlignment.CENTER);
		toggleButtonPanel.setHeight(560);
		toggleButtonPanel.setStyleAttribute("background-color", "#DFE8F6");

		destinationButton = new ToggleButton(AbstractImagePrototype.create(UIContext.IconBundle
				.backupDestination()).createImage());
		destinationButton.ensureDebugId("55FB1B22-C137-49f0-9AAC-AF71361C460C");
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
				emailAlertButton.setDown(false);
				
				destinationLabel.setDown(true);
				advancedLabel.setDown(false);
				settingsLabel.setDown(false);
				scheduleLabel.setDown(false);
				emailAlertLabel.setDown(false);
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
				emailAlertButton.setDown(false);

				destinationLabel.setDown(false);
				advancedLabel.setDown(false);
				settingsLabel.setDown(false);
				scheduleLabel.setDown(true);
				emailAlertLabel.setDown(false);
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
				emailAlertButton.setDown(false);

				destinationLabel.setDown(false);
				advancedLabel.setDown(false);
				settingsLabel.setDown(true);
				scheduleLabel.setDown(false);
				emailAlertLabel.setDown(false);
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
				emailAlertButton.setDown(false);

				destinationLabel.setDown(false);
				advancedLabel.setDown(true);
				settingsLabel.setDown(false);
				scheduleLabel.setDown(false);
				emailAlertLabel.setDown(false);
				// Updating heading.
				setHeadingHtml(UIContext.Messages
						.backupSettingsWindowWithTap(UIContext.Constants
								.backupSettingsPrePost()));
			}

		};
		
		emailAlertButtonHandler = new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				deckPanel.showWidget(STACK_EMAILALERT);
				destinationButton.setDown(false);
				advancedButton.setDown(false);
				settingsButton.setDown(false);
				scheduleButton.setDown(false);
				emailAlertButton.setDown(true);
				
				destinationLabel.setDown(false);
				advancedLabel.setDown(false);
				settingsLabel.setDown(false);
				scheduleLabel.setDown(false);
				emailAlertLabel.setDown(true);
				// Updating heading.
				setHeadingHtml(UIContext.Messages
						.backupSettingsWindowWithTap(UIContext.Constants
								.vSphereSettingEmailAlert()));
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
		destinationLabel.ensureDebugId("6A7A4946-E836-4145-973D-EAC44DF61894");
		destinationLabel.setStylePrimaryName("tb-settings");
		destinationLabel.setDown(true);
		destinationLabel.addClickHandler(destinationButtonHandler);
		toggleButtonPanel.add(destinationLabel);

		scheduleButton = new ToggleButton(AbstractImagePrototype.create(UIContext.IconBundle.backupSchedule())
				.createImage());
		scheduleButton.ensureDebugId("2292759C-7AC6-4768-BE24-3C305DAD0BD7");
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
		scheduleLabel.ensureDebugId("5E49ED29-D5FF-4bf7-B2D9-3FF0D8EA4BCB");
		scheduleLabel.setStylePrimaryName("tb-settings");
		scheduleLabel.addClickHandler(scheduleButtonHandler);
		toggleButtonPanel.add(scheduleLabel);

		settingsButton = new ToggleButton(AbstractImagePrototype.create(UIContext.IconBundle.backupSettings())
				.createImage());
		settingsButton.ensureDebugId("D3A9EBB3-9793-4612-B253-A05829655542");
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
		settingsLabel.ensureDebugId("7C9A55D4-3F2F-4aad-BA90-41C758B5C568");
		settingsLabel.setStylePrimaryName("tb-settings");
		settingsLabel.addClickHandler(settingsButtonHandler);
		toggleButtonPanel.add(settingsLabel);

		advancedButton = new ToggleButton(AbstractImagePrototype.create(UIContext.IconBundle.backupAdvanced())
				.createImage());
		advancedButton.ensureDebugId("64992B9D-899B-4905-9BCB-54E5B26DB61E");
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
		advancedLabel.ensureDebugId("6ECC7267-6C17-4642-8957-39AD63944E1C");
		advancedLabel.setStylePrimaryName("tb-settings");
		advancedLabel.addClickHandler(advancedButtonHandler);
		toggleButtonPanel.add(advancedLabel);

		emailAlertButton = new ToggleButton(AbstractImagePrototype.create(UIContext.IconBundle.backupAdvanced())
				.createImage());
		emailAlertButton.ensureDebugId("50DE3898-B489-4abb-A68A-639E46AD3BE1");
		emailAlertButton.setStylePrimaryName("demo-ToggleButton");
		emailAlertButton.addClickHandler(emailAlertButtonHandler);
		
		toggleButtonPanel.add(emailAlertButton);
		emailAlertLabel = new ToggleButton(UIContext.Constants.vSphereSettingEmailAlert());
		emailAlertLabel.ensureDebugId("FAEAC50F-E9C3-4e4d-BF5C-021B858FEC0B");
		emailAlertLabel.setStylePrimaryName("tb-settings");
		emailAlertLabel.addClickHandler(emailAlertButtonHandler);
		toggleButtonPanel.add(emailAlertLabel);
		
		/*this.add(toggleButtonPanel);

		TableData tableData = new TableData();
		tableData.setWidth("90%");
		this.add(deckPanel, tableData);*/
		
		contentPanel.add( toggleButtonPanel, new RowData( -1, 1 ) );
		
		contentPanel.add( deckPanel, new RowData( 1, 1 ) );
		
		this.add( contentPanel, new RowData( 1, 1 ) );
		

		LayoutContainer buttonContainer = new LayoutContainer();
		buttonContainer.setStyleAttribute("background-color", "#DFE8F6");
		//buttonContainer.setHeight(80);

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

		okButton = new Button();
		okButton.ensureDebugId("B193FA4C-5403-4b1b-8B33-5AA9CC90C60F");
		okButton.setMinWidth(80);
		okButton.setText(UIContext.Constants.backupSettingsOk());
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				// OK Clicked, save it
				thisWindow.mask(UIContext.Constants.settingsMaskText());
				boolean check = thisWindow.SaveSettings();
				if (check) {
					thisWindow.unmask();
					vc = new VirtualCenterWindow(thisWindow);
					vc.setModal(true);
					vc.show();
				} else {
					thisWindow.unmask();
				}
			}

		});
		buttonContainer.add(okButton, td);

		cancelButton = new Button();
		cancelButton.ensureDebugId("1795A48C-ED65-4c47-9F59-9F8EF01B6C2E");
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
		helpButton.ensureDebugId("6E4749E2-A81A-40d2-9440-B4D198459B5B");
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
		//this.add(buttonContainer, td);
		this.add(buttonContainer,  new RowData( 1, -1 ) );

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
		/*
		 * VirtualCenterNodeModel vcNodeModel = new VirtualCenterNodeModel();
		 * vcNodeModel.set("name", "155.35.66.21"); vcNodeModel.set("username",
		 * "root"); vcNodeModel.set("password", "caworld");
		 * vcNodeModel.set("protocol", "https"); vcNodeModel.set("port", 8014);
		 * vcNodeModel.set("type", 1);
		 */

		/*
		 * service.getVSphereBackupSetting(vcNodeModel, new
		 * BaseAsyncCallback<VSphereBackupSettingModel>() {
		 * 
		 * @Override public void onFailure(Throwable caught) {
		 * thisWindow.unmask(); super.onFailure(caught); }
		 * 
		 * @Override public void onSuccess(VSphereBackupSettingModel result) {
		 * if (result != null) { backupSettingFileExist_b = true;
		 * thisWindow.model = result; thisWindow.RefreshData();
		 * thisWindow.unmask(); } else {
		 */
		service.getServerTime(new BaseAsyncCallback<Date>() {
			@Override
			public void onSuccess(Date result) {
				thisWindow.model = getDefaultModel();
				long serverTimeInMilliseconds = result.getTime();
				// set backup start time plus 5
				// minutes
				serverTimeInMilliseconds += 5 * 60 * 1000;
				model.setBackupStartTime(serverTimeInMilliseconds);
				D2DTimeModel time = new D2DTimeModel();
				Date serverDate = Utils.localTimeToServerTime(new Date(serverTimeInMilliseconds));
				time.fromJavaDate(serverDate);
				model.startTime = time;
				thisWindow.RefreshData();
				thisWindow.unmask();
			}

			@Override
			public void onFailure(Throwable caught) {
				thisWindow.unmask();
				super.onFailure(caught);
			}
		});
	}

	// }

	private VSphereBackupSettingModel getDefaultModel() {

		backupSettingFileExist_b = false;
		VSphereBackupSettingModel model = new VSphereBackupSettingModel();
		model = new VSphereBackupSettingModel();

		model.fullSchedule = new BackupScheduleModel();
		model.fullSchedule.setEnabled(false);

		model.incrementalSchedule = new BackupScheduleModel();
		model.incrementalSchedule.setEnabled(true);
		model.incrementalSchedule.setInterval(1);
		model.incrementalSchedule
				.setIntervalUnit(BackupScheduleIntervalUnitModel.Day);

		model.resyncSchedule = new BackupScheduleModel();
		model.resyncSchedule.setEnabled(false);

		model.setRetentionCount(DEFAULT_RETENTION_COUNT);
		model.setCompressionLevel(1); // Standard
		model.setEnableEncryption(false);

		model.setPurgeSQLLogDays(0L);
		model.setPurgeExchangeLogDays(0L);

		return model;
	}

	// });
	/*
	 * VSphereBackupSettingModel backupModel = new VSphereBackupSettingModel();
	 * List<BackupVMModel> vmList = new ArrayList<BackupVMModel>();
	 * 
	 * backupModel.backupVMList = vmList; backupModel.setBackupStartTime(0L);
	 * this.model = backupModel; thisWindow.RefreshData(); thisWindow.unmask();
	 */
	// }

	public void RefreshData() {
		schedule.RefreshData(model);
		settings.RefreshData(model);
		advanced.RefreshData(model);
		destination.RefreshData(model);
		emailAlert.RefreshData(model);
	}

	boolean backupSettingFileExist_b = false;


	public boolean SaveSettings() {
		// model = new BackupSettingsModel();

		if (advanced.Validate()) {
			this.advanced.Save();
		} else {
			deckPanel.showWidget(STACK_ADVANCED);
			// layout.setActiveItem(advancedContainer);
			return false;
		}

		if (destination.Validate()) {
			this.destination.Save();
		} else {
			deckPanel.showWidget(STACK_DESTINATION);
			// layout.setActiveItem(destinationContainer);
			return false;
		}

		if (schedule.Validate()) {
			this.schedule.Save();
		} else {
			deckPanel.showWidget(STACK_SCHEDULE);
			// layout.setActiveItem(scheduleContainer);
			return false;
		}

		if (settings.Validate()) {
			this.settings.Save();
		} else {
			deckPanel.showWidget(STACK_SETTINGS);
			// layout.setActiveItem(settingsContainer);
			return false;
		}

		if(emailAlert.Validate()){
			this.emailAlert.Save();
		}else{
			deckPanel.showWidget(STACK_EMAILALERT);
			return false;
		}
		return true;
	}

	/**
	 * fix 18898048 The first time should meet below criteria: 1.Our backup
	 * configuration file does not exist. 2.There must be at least one type of
	 * backup job (full/incr/resync) which is scheduled as repeatable. 3.the
	 * start time is earlier than the time when user save the backup settings.
	 * 
	 * If these conditions are met, we prompt the user whether we should start a
	 * backup job for him/her at once. If user choose Yes, then we launch the
	 * backup job immediately. Or, we just schedule the job as usual.
	 */
	long currentTime = 0;
	int firstLaunchBackupType = BackupTypeModel.Full;

	private void launchFirstBackupJobifNeeded() {

		// the first condition
		if (backupSettingFileExist_b)
			return;
		// decide the needed bakup type;
		BackupScheduleModel tagetType = null;
		if (model.fullSchedule.isEnabled()) {
			tagetType = model.fullSchedule;
			firstLaunchBackupType = BackupTypeModel.Full;
		} else if (model.resyncSchedule.isEnabled()) {
			tagetType = model.resyncSchedule;
			firstLaunchBackupType = BackupTypeModel.Resync;
		} else if (model.incrementalSchedule.isEnabled()) {
			tagetType = model.incrementalSchedule;
			firstLaunchBackupType = BackupTypeModel.Incremental;
		}
		// now decide where to go according to the second condition
		if (tagetType == null)
			return;

		// I can not use the new Date() to get the current time because the
		// ROOT.war can be on different machine
		// than webservice machine
		// one minute gap
		service.getServerTime(new BaseAsyncCallback<Date>() {
			@Override
			public void onSuccess(Date result) {
				currentTime = result.getTime();
				launchFirstJobWindow(firstLaunchBackupType);
			}

			@Override
			public void onFailure(Throwable caught) {
				// if failed, select a earlier day, so that the first launch box
				// will not pop up. Is it wise or foolproof?
				Date date = new Date(0);
				DateWrapper dw = new DateWrapper(date);
				dw.addDays(1);
				currentTime = dw.getTime();
				launchFirstJobWindow(firstLaunchBackupType);
			}

		});

	}

	private void launchFirstJobWindow(int firstLaunchBackupType) {
		DateWrapper currdw = new DateWrapper(currentTime);
		currdw = currdw.addMinutes(-1);
		// if start time is after the server's current enough, we should not
		// bother to pop up the first launch box
		if (model.getBackupStartTime() > currdw.getTime())
			return;

		String startTime = Utils.formatTimeToServerTime(new Date(model
				.getBackupStartTime()));
		String messageText = Format.substitute(UIContext.Constants
				.firstJobDescription(), new Object[] { startTime });

		final MessageBox message = new MessageBox();
		message.setTitleHtml(UIContext.Constants.fisrtLaunchWindowTitle());
		message.setIcon(MessageBox.INFO);
		message.setMessage(messageText);
		message.setButtons(Dialog.OKCANCEL);
		message.setModal(true);
		final int fullBackup = firstLaunchBackupType;
		message.addCallback(new Listener<MessageBoxEvent>() {
			@Override
			public void handleEvent(MessageBoxEvent be) {
				if (be.getButtonClicked().getItemId().equals(Dialog.OK)) {
					CommonServiceAsync service = GWT
							.create(CommonService.class);
					service.backup(fullBackup, UIContext.Constants
							.firstLaunchedJobName(),
							new BaseAsyncCallback<Void>() {

								@Override
								public void onFailure(Throwable caught) {
									super.onFailure(caught);
								}

								@Override
								public void onSuccess(Void result) {
									// fix bug 18925882
									message.close();
									MessageBox box = MessageBox
											.info(
													UIContext.Messages.messageBoxTitleInformation(UIContext.productNamevSphere),
													UIContext.Constants
															.backupNowWindowSubmitSuccessful(),
													null);
									Utils.setMessageBoxDebugId(box);

								}

							});
				}
			}
		});
		Utils.setMessageBoxDebugId(message);
		message.show();
	}

	private boolean Save() {
		try {
			service.saveVShpereBackupSetting(thisWindow.model,
					new BaseAsyncCallback<Long>() {
						@Override
						public void onSuccess(Long result) {
							// fix 18898048
							// launchFirstBackupJobifNeeded();

							thisWindow.unmask();
							thisWindow.hide();
							UIContext.d2dHomepagePanel.refresh(null);
						}

						@Override
						public void onFailure(Throwable caught) {

							thisWindow.unmask();
							super.onFailure(caught);
						}
					});
		} catch (Exception ce) {
			ce.printStackTrace();
		}
		return true;

	}

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
	
	//this method is just to avoid the session timeout.
	public void startTimerAvoidSessionTimeout() {
		if(timer == null) {
			timer = new Timer() {

				@Override
				public void run() {
					commonService.getJobMonitor(String.valueOf(JobMonitorModel.JOBTYPE_BACKUP),-1L, 
							new BaseAsyncCallback<JobMonitorModel>(){
						public void onFailure(Throwable caught) {							
						}
						public void onSuccess(JobMonitorModel result) {
						}
					});
				}
			};
			
			timer.scheduleRepeating(10000);
		}
	}
}
