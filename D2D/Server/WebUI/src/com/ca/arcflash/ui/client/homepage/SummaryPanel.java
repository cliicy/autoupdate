package com.ca.arcflash.ui.client.homepage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.AdvancedSettings;
import com.ca.arcflash.ui.client.backup.schedule.EveryDayScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.EveryMonthScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.EveryWeekScheduleModel;
import com.ca.arcflash.ui.client.common.BackupSummaryEventListener;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.BaseLicenseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.IRefreshable;
import com.ca.arcflash.ui.client.common.LoadingStatus;
import com.ca.arcflash.ui.client.common.SettingsTypesForUI;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.ArchiveJobInfoModel;
import com.ca.arcflash.ui.client.model.BackupInformationSummaryModel;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.ca.arcflash.ui.client.model.BackupStatusModel;
import com.ca.arcflash.ui.client.model.CustomizationModel;
import com.ca.arcflash.ui.client.model.DataStoreInfoModel;
import com.ca.arcflash.ui.client.model.DataStorePolicyModel;
import com.ca.arcflash.ui.client.model.DestinationCapacityModel;
import com.ca.arcflash.ui.client.model.LicInfoModel;
import com.ca.arcflash.ui.client.model.MountSessionModel;
import com.ca.arcflash.ui.client.model.PatchInfoModel;
import com.ca.arcflash.ui.client.model.PreferencesModel;
import com.ca.arcflash.ui.client.model.RecentBackupModel;
import com.ca.arcflash.ui.client.model.StagingServerModel;
import com.ca.arcflash.ui.client.model.UpdateSettingsModel;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.ca.arcflash.ui.client.model.BIPatchInfoModel;

public class SummaryPanel extends BaseSummaryPanel implements IRefreshable {
	private final HomepageServiceAsync service = GWT
			.create(HomepageService.class);
	private final LoginServiceAsync loginService = GWT
			.create(LoginService.class);
	private final CommonServiceAsync commonService = GWT
			.create(CommonService.class);
	public static final String ICON_FINISH_URL = "images/status_mid_finish.gif";
	public static final String ICON_WARNING_URL = "images/status_mid_warning.gif";
	public static final String ICON_ERROR_URL = "images/status_mid_error.gif";
	public static final String ICON_INFO_URL = "images/status_mid_info.gif";

	public static final String ICON_LARGE_FINISH_URL = "images/status_large_finish.gif";
	public static final String ICON_LARGE_WARNING_URL = "images/status_large_warning.gif";
	public static final String ICON_LARGE_ERROR_URL = "images/status_large_error.gif";

	public static final int WARNING_GET_PATCH_TIMEOUT = -3;
	public static final int WARNING_GET_PATCH_IN_PROGRESS = -2;
	public static final int WARNING_NODE_MANAGED_BY_CPM = -1;
	public static final int ERROR_GET_PATCH_INFO_SUCCESS = 0;
	public static final int ERROR_GET_PATCH_INFO_FAIL = 1;

	public static final int INSTALL_ERROR_SUCCESS = 2;
	public static final int INSTALL_ERROR_ACTIVE_JOBS = 3;
	public static final int INSTALL_ERROR_FAIL = 4;
	public static final int ERROR_NONEW_PATCHES_AVAILABLE = 5;
	public static final int ERROR_PM_BUSY_WITH_SCHEDULER = 6;

	public static final int UPDATE_MANAGER_DOWN = 0;
	public static final int UPDATE_MANAGER_READY = 1;
	public static final int UPDATE_MANAGER_BUSY = 2;

	private Image backupNotConfiguredImage;
	private Text backupNotConfiguredDescriptionText;

	private Label dataStoreDetail;
	// wanqi06 added
	private Text recoverySetsDescriptionText;
	private Label recoverySetsDetailMessage;
	private Text recoverySetLargerText;
	private LayoutContainer lsBackupsetPanel;
	private LoadingStatus loadBacSets;
	private ClickHandler BackupSetDetailHandler = null;
	private Text inProgressRecoverySet;
	//
	private Text recoveryPointsDescriptionText;
	private Text recoveryPointsRepeatDescriptionText;
	private Text recoveryPointsDailyDescriptionText;
	private Text recoveryPointsWeeklyDescriptionText;
	private Text recoveryPointsMonthlyDescriptionText;
	private Text recoveryPointsLargerText;

	private AbstractImagePrototype archiveImagePrototype;
	private AbstractImagePrototype newUpdatesImage;
	private Image totalStatusImage;
	private ContentPanel panel;

	private LayoutContainer lcBackupsNotConfiguredPanel;
	private LayoutContainer noDriverPanel;
	private LayoutContainer lcRestorePanel;
	private LayoutContainer lcDestinationPanel;

	private ContentPanel interRightPanel;

	private Text destinationText;
	private Text destinationHealthText;
	// private LoadingStatus status;
	private int LeastBackupSize = 5;
	private ContentPanel licPanel;
	private static ContentPanel cpNewUpdatePanel;
	private int iUpdateStatus = -1;
	private static int iAutoUpdateConfigurationStatus = -2;
	private Image UpdatesImage;
	private Label NewUpdatesSettingslbl;
	private Label lblUpdateStatusMessage;
	public static boolean bDownloadServerAvailable = false;
	public static int iDownloadServerType = -1;
	private UpdateSettingsModel updateSettings = null;
	private ClickHandler NewUpdatesInstallHandler = null;

	private static PatchInfoModel patchInfo;
	private LoadingStatus lsLoadingUpdates;

	private static ContentPanel cpArchivePanel;
	private Image ArchiveImage;
	private Text archiveSpaceSavedText;
	private static boolean bIsTestConnectionExecuted = false;
	public static SummaryPanel gSummaryPanel = null;

	private boolean isDriverInstalled = false;
	private boolean isRestarted = false;
	// wanqi06

	private Label driverLabel;
	private HTML helpHtml;
	private BackupInformationSummaryModel currentBackup;

	// added by cliicy.luo
	private AbstractImagePrototype newBIUpdatesImage;
	//private Label NewBIUpdatesSettingslbl1;
	public static boolean bDownloadBIServerAvailable = false;
	//private Label lblBIUpdateStatusMessage1;
	private Image UpdatesBIImage;
	private static ContentPanel cpNewBIUpdatePanel;
	private Label NewBIUpdatesSettingslbl;
	private Label lblBIUpdateStatusMessage;
	private UpdateSettingsModel updateBISettings = null;
	private ClickHandler NewBIUpdatesInstallHandler = null;
	private static int iAutoBIUpdateConfigurationStatus = -2;
	private static BIPatchInfoModel patchBIInfo;
	private static boolean bIsTestBIConnectionExecuted = false;
	public static int iDownloadBIServerType = -1;
	private int iBIUpdateStatus = -1;
	private LoadingStatus lsLoadingBIUpdates;
	// added by cliicy.luo

	private LoadingStatus loadRevPoints;
	private LoadingStatus loadDestCapacity;
	private LoadingStatus loadArchive;
	private List<BackupSummaryEventListener> listeners;
	Boolean isFileCopyEnabled;
	private boolean isAdvanced;
	private DataStorePolicyModel dataStorePolicyModel = new DataStorePolicyModel();

	public SummaryPanel() {
		listeners = new ArrayList<BackupSummaryEventListener>();
		CustomizationModel customizedModel = UIContext.customizedModel;
		isFileCopyEnabled = customizedModel.get("FileCopy");
	}

	public void addBackupSummaryListener(BackupSummaryEventListener listener) {
		listeners.add(listener);
	}

	public void render(Element target, int index) {
		super.render(target, index);
		panel = new ContentPanel();
		panel.setCollapsible(true);

		panel.setBodyStyle("background-color: white; padding: 6px; overflow:scroll");
		panel.setHeadingHtml(UIContext.Constants.homepageSummaryHeader());

		TableLayout layout = new TableLayout();
		layout.setWidth("100%");
		layout.setColumns(2);
		panel.setLayout(layout);

		AbstractImagePrototype totalStatusImagePrototype = IconHelper.create(
				ICON_LARGE_FINISH_URL, 64, 64);
		totalStatusImage = totalStatusImagePrototype.createImage();

		TableData tableData = new TableData();
		tableData.setWidth("100");
		tableData.setHorizontalAlign(HorizontalAlignment.CENTER);
		tableData.setVerticalAlign(VerticalAlignment.TOP);
		panel.add(totalStatusImage, tableData);

		ContentPanel rightPanel = new ContentPanel();
		rightPanel.setHeaderVisible(false);
		rightPanel.setBorders(false);
		rightPanel.setBodyBorder(false);
		layout = new TableLayout();
		layout.setWidth("100%");
		layout.setColumns(2);
		rightPanel.setLayout(layout);

		interLeftPanel = new ContentPanel();
		interLeftPanel.setHeaderVisible(false);
		interLeftPanel.setBorders(false);
		interLeftPanel.setBodyBorder(false);

		imagePrototype = IconHelper.create(ICON_FINISH_URL, 32, 32);
		createSummaryBackupPart(interLeftPanel);
		createSummaryRecoveryPointsPart(interLeftPanel);
		createSummaryRecoverySetsPart(interLeftPanel);
		createSummaryDestinationPart(interLeftPanel);
		createBackupNotConfiguredPanel(interLeftPanel);
		createNotInstallDriverPanel(interLeftPanel);
		lcBackupsNotConfiguredPanel.hide();

		tableData = new TableData();
		tableData.setWidth("50%");
		tableData.setVerticalAlign(Style.VerticalAlignment.TOP);
		rightPanel.add(interLeftPanel, tableData);

		interRightPanel = new ContentPanel();
		interRightPanel.setHeaderVisible(false);
		interRightPanel.setBorders(false);
		interRightPanel.setBodyBorder(false);

		licPanel = new ContentPanel();
		licPanel.setHeaderVisible(false);
		licPanel.setBorders(false);
		licPanel.setBodyBorder(false);

		createLicStatusPart(licPanel);
		tableData = new TableData();
		tableData.setVerticalAlign(Style.VerticalAlignment.TOP);
		interRightPanel.add(licPanel);
		licPanel.hide();

		cpNewUpdatePanel = new ContentPanel();
		cpNewUpdatePanel.setHeaderVisible(false);
		cpNewUpdatePanel.setBorders(false);
		cpNewUpdatePanel.setBodyBorder(false);

		newUpdatesImage = IconHelper.create(ICON_WARNING_URL, 32, 32);
		CreateNewUpdatesPart(cpNewUpdatePanel);
		TableData tdNewUpdates = new TableData();
		tdNewUpdates.setVerticalAlign(Style.VerticalAlignment.TOP);
		interRightPanel.add(cpNewUpdatePanel, tdNewUpdates);
		cpNewUpdatePanel.hide();

				
		lsLoadingUpdates = new LoadingStatus();
		lsLoadingUpdates.setLoadingMsg(UIContext.Constants.loadingIndicatorText());
		lsLoadingUpdates.hide();
		interRightPanel.add(lsLoadingUpdates);
		
		// added by cliicy.luo
		cpNewBIUpdatePanel = new ContentPanel();
		cpNewBIUpdatePanel.setHeaderVisible(false);
		cpNewBIUpdatePanel.setBorders(false);
		cpNewBIUpdatePanel.setBodyBorder(false);

		newBIUpdatesImage = IconHelper.create(ICON_WARNING_URL, 32, 32);
		CreateNewBIUpdatesPart(cpNewBIUpdatePanel);
		TableData tdNewBIUpdates = new TableData();
		tdNewBIUpdates.setVerticalAlign(Style.VerticalAlignment.TOP);
		interRightPanel.add(cpNewBIUpdatePanel, tdNewBIUpdates);
		cpNewBIUpdatePanel.hide();

		lsLoadingBIUpdates = new LoadingStatus();
		lsLoadingBIUpdates.setLoadingMsg(UIContext.Constants.loadingIndicatorText());
		lsLoadingBIUpdates.hide();
		interRightPanel.add(lsLoadingBIUpdates);
		// added by cliicy.luo

		cpArchivePanel = new ContentPanel();
		cpArchivePanel.setHeaderVisible(false);
		cpArchivePanel.setBorders(false);
		cpArchivePanel.setBodyBorder(false);

		archiveImagePrototype = IconHelper.create(ICON_FINISH_URL, 32, 32);
		CreateArchivePart(cpArchivePanel);
		tdNewUpdates = new TableData();
		tdNewUpdates.setVerticalAlign(Style.VerticalAlignment.TOP);
		interRightPanel.add(cpArchivePanel, tdNewUpdates);
		if (!isFileCopyEnabled) {
			cpArchivePanel.hide();
		}

		rightPanel.add(interRightPanel, tableData);

		tableData = new TableData();
		tableData.setColspan(2);
		createDestinationChart(rightPanel, tableData);
		createDestinationLengend(rightPanel, tableData);

		panel.add(rightPanel);
		add(panel);
		gSummaryPanel = this;
		hideCommonPanels();
		panel.mask(UIContext.Messages.LoadingSummaryMessage(UIContext.productNameD2D));
		checkDriver();
	}

	private void checkDriver() {
		try {
			loginService.installDriver(new BaseAsyncCallback<Boolean>() {

				@Override
				public void onFailure(Throwable caught) {
					super.onFailure(caught);
				}

				@Override
				public void onSuccess(Boolean result) {
					isDriverInstalled = result;
					if (result) {
						try {
							loginService.installDriverRestart(new BaseAsyncCallback<Boolean>() {

										@Override
										public void onFailure(Throwable caught) {
											super.onFailure(caught);
										}

										@Override
										public void onSuccess(Boolean result) {
											isRestarted = result;
											if (result) {
												refreshWithSummary();
											} else
												refresh(null);
										}
									});

						} catch (Throwable t) {
						}
					} else {
						refresh(null);
					}
				}
			});
		} catch (Throwable t) {

		}
	}

	private void refreshWithSummary() {
		service.getBackupInforamtionSummaryWithLicInfo(new BaseAsyncCallback<BackupInformationSummaryModel>() {

			@Override
			public void onFailure(Throwable caught) {

				super.onFailure(caught);
			}

			@Override
			public void onSuccess(BackupInformationSummaryModel result) {

				currentBackup = result;

				refresh(result);
			}

		});
	}

	private void createNotInstallDriverPanel(ContentPanel interLeftPanel) {
		imagePrototype = IconHelper.create(ICON_ERROR_URL, 32, 32);
		Image driverImage = imagePrototype.createImage();
		noDriverPanel = new LayoutContainer();
		noDriverPanel.setLayoutOnChange(true);
		TableLayout layout = new TableLayout();
		layout.setColumns(2);
		noDriverPanel.setLayout(layout);
		TableData data = new TableData();
		data.setVerticalAlign(VerticalAlignment.TOP);
		noDriverPanel.add(driverImage, data);
		LayoutContainer container = new LayoutContainer();
		driverLabel = new Label(
				UIContext.Messages.notInstallDriver(UIContext.productNameD2D));
		driverLabel.setStyleName("homepage_summary_description");
		container.add(driverLabel);

		helpHtml = new HTML(
				UIContext.Messages.installDriverStep(UIContext.productNameD2D));
		helpHtml.setStyleName("homepage_summary_description");
		container.add(helpHtml);
		noDriverPanel.add(container, data);
		interLeftPanel.add(noDriverPanel);
		noDriverPanel.hide();
	}

	private void createBackupNotConfiguredPanel(ContentPanel interLeftPanel2) {
		imagePrototype = IconHelper.create(ICON_ERROR_URL, 32, 32);
		backupNotConfiguredImage = imagePrototype.createImage();

		backupNotConfiguredDescriptionText = new Text(
				UIContext.Messages
						.D2DBackupsNotconfiguredMessage(UIContext.productNameD2D));
		backupNotConfiguredDescriptionText
				.setStyleName("homepage_summary_description");
		lcBackupsNotConfiguredPanel = new LayoutContainer();
		addRecentBackup(lcBackupsNotConfiguredPanel,
				UIContext.Messages.D2DBackups(UIContext.productNameD2D),
				backupNotConfiguredImage, backupNotConfiguredDescriptionText,
				null);
	}

	private Image licImage;
	private Text licDescText;
	private HTML purchaseLicenseLink;
	private LicInfoModel licenseInfoModel;

	private void createLicStatusPart(ContentPanel panel) {
		licImage = imagePrototype.createImage();
		licDescText = new Text(UIContext.Constants.NA());
		licDescText.setStyleName("homepage_summary_description");
		String html = UIContext.Messages.purchaseLicDescription("<a href=\""
				+ UIContext.externalLinks.getUpgradePaidVersionURL()
				+ "\" target=\"_blank\">"
				+ UIContext.Constants.purchaseLicense() + "</a> ");
		purchaseLicenseLink = new HTML(html);
		purchaseLicenseLink.setStyleName("homepage_summary_description");
		addRecentBackup(panel,
				UIContext.Constants.homepageSummarySoftwareLicenses(),
				licImage, licDescText, purchaseLicenseLink, null);
		purchaseLicenseLink.setVisible(false);

	}

	private void CreateNewUpdatesPart(ContentPanel in_cpNewUpdatePanel) {
		NewUpdatesInstallHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				if (patchInfo != null) {
					NewUpdatesInstallWindow dlgNewUpdates = new NewUpdatesInstallWindow(
							patchInfo);
					dlgNewUpdates.setModal(true);
					dlgNewUpdates.show();
				}
			}
		};

		UpdatesImage = newUpdatesImage.createImage();

		NewUpdatesSettingslbl = new Label(UIContext.Constants.InstallUpdatesDescription());

		NewUpdatesSettingslbl.setStyleName("homepage_summary_description");

		lblUpdateStatusMessage = new Label();
		lblUpdateStatusMessage.ensureDebugId("c86a1fc9-128e-4148-9a38-05df5d31fa2a");
		lblUpdateStatusMessage.addClickHandler(NewUpdatesInstallHandler);
		lblUpdateStatusMessage.setStyleName("homepage_NewUpdates_Description");

		addNewUpdatestoSummaryPanel(in_cpNewUpdatePanel,UIContext.Constants.D2DNewUpdatesAvailableLabel(),
				UpdatesImage, NewUpdatesSettingslbl, NewUpdatesInstallHandler,
				lblUpdateStatusMessage);

	}

	// added by cliicy.luo
	private void CreateNewBIUpdatesPart(ContentPanel in_cpNewUpdatePanel) {
		NewBIUpdatesInstallHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				if (patchBIInfo != null) {
					NewUpdates_BI_InstallWin dlgNewUpdates = new NewUpdates_BI_InstallWin(patchBIInfo);
					//TNewUpdates_BI_InstallWin dlgNewUpdates = new TNewUpdates_BI_InstallWin(null);
					dlgNewUpdates.setModal(true);
					dlgNewUpdates.show();
				} else {//added by cliicy.luo
					Info.display(UIContext.Messages.messageBoxTitleError(Utils
							.getProductName()), "OOOO patchBIInfo == null");
				}
			}
		};
		
		UpdatesBIImage = newBIUpdatesImage.createImage();

		NewBIUpdatesSettingslbl = new Label("test click here for update binary");
		NewBIUpdatesSettingslbl.setStyleName("homepage_summary_description");

		lblBIUpdateStatusMessage = new Label();
		lblBIUpdateStatusMessage.ensureDebugId("c86a1fc9-128e-4148-9a38-05df5d31fa2a");
		lblBIUpdateStatusMessage.addClickHandler(NewBIUpdatesInstallHandler);
		lblBIUpdateStatusMessage.setStyleName("homepage_NewUpdates_Description");
		addNewUpdatesBItoSummaryPanel(in_cpNewUpdatePanel, "For binary updating",UpdatesBIImage, NewBIUpdatesSettingslbl,
				NewBIUpdatesInstallHandler, lblBIUpdateStatusMessage);//cliicy.luo ????*/
	}
	// added by cliicy.luo
	
	private void createSummaryRecoveryPointsPart(ContentPanel panel) {
		recoveryPointsImage = imagePrototype.createImage();
		recoveryPointsDescriptionText = new Text(UIContext.Constants.NA());
		recoveryPointsRepeatDescriptionText = new Text(UIContext.Constants.NA());
		recoveryPointsDailyDescriptionText = new Text(UIContext.Constants.NA());
		recoveryPointsWeeklyDescriptionText = new Text(UIContext.Constants.NA());
		recoveryPointsMonthlyDescriptionText = new Text(
				UIContext.Constants.NA());
		recoveryPointsDescriptionText
				.setStyleName("homepage_summary_description");
		recoveryPointsRepeatDescriptionText
				.setStyleName("homepage_summary_description");
		recoveryPointsDailyDescriptionText
				.setStyleName("homepage_summary_description");
		recoveryPointsWeeklyDescriptionText
				.setStyleName("homepage_summary_description");
		recoveryPointsMonthlyDescriptionText
				.setStyleName("homepage_summary_description");
		recoveryPointsLargerText = createRecoveryPointsMountedText();
		mergeDelayedText = createRecoveryPointsMountedText();
		lcRestorePanel = new LayoutContainer();
		loadRevPoints = new LoadingStatus();

		startManualMergeText = new Label(
				UIContext.Constants.mergeStartManualMerge());
		startManualMergeText.setStyleName("homepage_Manual_Merge_Link");
		startManualMergeText.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				service.resumeMerge(null, new BaseAsyncCallback<Integer>() {

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
					}

					@Override
					public void onSuccess(Integer result) {
						mergeDelayedText.hide();
						startManualMergeText.setVisible(false);
						Info.display(UIContext.Messages
								.messageBoxTitleError(Utils.getProductName()),
								UIContext.Constants.mergeJobSubmited());
					}
				});
			}
		});
		List<Widget> widgets = new ArrayList<Widget>();
		widgets.add(recoveryPointsDescriptionText);
		widgets.add(recoveryPointsRepeatDescriptionText);
		widgets.add(recoveryPointsDailyDescriptionText);
		widgets.add(recoveryPointsWeeklyDescriptionText);
		widgets.add(recoveryPointsMonthlyDescriptionText);
		widgets.add(recoveryPointsLargerText);
		widgets.add(mergeDelayedText);
		widgets.add(startManualMergeText);
		addRecentBackup(lcRestorePanel,
				UIContext.Constants.homepageSummaryRecoveryPointsLabel(),
				recoveryPointsImage, widgets, this.loadRevPoints);
		startManualMergeText.setVisible(false);
	}

	// wanqi06 add
	private void createSummaryRecoverySetsPart(ContentPanel panel) {
		// TODO Auto-generated method stub
		BackupSetDetailHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				BackupSetsDetailWindow bsDetail = new BackupSetsDetailWindow();
				bsDetail.setModal(true);
				bsDetail.show();
			}
		};

		recoverySetsImage = imagePrototype.createImage();
		recoverySetsDescriptionText = new Text(UIContext.Constants.NA());
		recoverySetsDescriptionText
				.setStyleName("homepage_summary_description");

		inProgressRecoverySet = new Text(
				UIContext.Constants.inProgressRecoverySet());
		inProgressRecoverySet.addStyleName("homepage_summary_description");
		this.inProgressRecoverySet.hide();
		recoverySetsDetailMessage = new Label();
		recoverySetsDetailMessage
				.ensureDebugId("DED01DD8-5C3B-4cd0-922D-CD36E30A3133");
		recoverySetsDetailMessage.addClickHandler(BackupSetDetailHandler);
		recoverySetsDetailMessage
				.addStyleName("homepage_BackupSets_Description_Link");

		recoverySetLargerText = createRecoveryPointsMountedText();

		List<Widget> widgets = new ArrayList<Widget>();
		widgets.add(recoverySetsDescriptionText);
		widgets.add(inProgressRecoverySet);
		widgets.add(recoverySetsDetailMessage);
		widgets.add(recoverySetLargerText);

		lsBackupsetPanel = new LayoutContainer();
		loadBacSets = new LoadingStatus();

		addRecentBackup(lsBackupsetPanel,
				UIContext.Constants.homepageSummaryRecoverySetsLabel(),
				recoverySetsImage, widgets, this.loadBacSets);
		lsBackupsetPanel.hide();
	}

	private void createSummaryDestinationPart(ContentPanel panel) {
		destinationCapacityImage = imagePrototype.createImage();
		destinationCapacityDescriptionText = new Text(UIContext.Constants.NA());
		destinationCapacityDescriptionText
				.setStyleName("homepage_summary_description");
		destinationText = new Text(UIContext.Constants.NA());
		destinationText.setStyleName("homepage_summary_description");
		destinationHealthText = new Text(UIContext.Constants.NA());
		destinationHealthText.setStyleName("homepage_summary_description");
		lcDestinationPanel = new LayoutContainer();
		loadDestCapacity = new LoadingStatus();

		dataStoreDetail = new Label(
				UIContext.Constants.homepageSummaryDestinationDataStoreDetail());
		dataStoreDetail.setStyleName("homepage_summary_hyperlink_label");
		// By design, hide the link always before new solution
		dataStoreDetail.setVisible(false);
		dataStoreDetail.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				service.getDataStoreStatus(dataStorePolicyModel.getName(),
						new BaseAsyncCallback<DataStoreInfoModel>() {
							public void onFailure(Throwable caught) {
								super.onFailure(caught);
							}

							@Override
							public void onSuccess(DataStoreInfoModel result) {
								DataStoreDetailWindow.show(result,
										dataStorePolicyModel.getDisplayName());
							}
						});
			}
		});

		List<Widget> widgets = new ArrayList<Widget>();
		widgets.add(destinationCapacityDescriptionText);
		widgets.add(destinationText);
		widgets.add(destinationHealthText);
		widgets.add(dataStoreDetail);
		addRecentBackup(lcDestinationPanel,
				UIContext.Constants.homepageSummaryDestinationCapacityLabel(),
				destinationCapacityImage, widgets, this.loadDestCapacity);
	}

	private Widget addNewUpdatestoSummaryPanel(
			LayoutContainer in_cpNewUpdatePanel, String in_strNewUpdateslabel,
			Image in_NewUpdatesImage, Label in_NewUpdatesDesclbl,
			ClickHandler in_Clickhandler, Label lblUpdateStatusMessage2) {
		TableLayout layout = new TableLayout();
		layout.setColumns(2);

		LayoutContainer container = new LayoutContainer();

		container.setLayout(layout);
		container.setStyleAttribute("padding", "4px");

		TableData tableData = null;
		tableData = new TableData();
		tableData.setVerticalAlign(VerticalAlignment.TOP);
		if (lblUpdateStatusMessage2 != null)
			tableData.setRowspan(3);
		else
			tableData.setRowspan(2);

		container.add(in_NewUpdatesImage, tableData);

		tableData = new TableData();
		tableData.setVerticalAlign(VerticalAlignment.TOP);
		Label NewUpdateslbl = new Label(in_strNewUpdateslabel);
		NewUpdateslbl.setStyleName("homepage_summary_label");

		container.add(NewUpdateslbl, tableData);

		tableData = new TableData();
		tableData.setVerticalAlign(VerticalAlignment.TOP);
		container.add(in_NewUpdatesDesclbl, tableData);

		if (lblUpdateStatusMessage2 != null)
			container.add(lblUpdateStatusMessage2, tableData);

		in_cpNewUpdatePanel.add(container);
		return container;
	}

	//added by cliicy.luo for BI
	private Widget addNewUpdatesBItoSummaryPanel(
			LayoutContainer in_cpNewUpdatePanel, String in_strNewUpdateslabel,
			Image in_NewUpdatesImage, Label in_NewUpdatesDesclbl,
			ClickHandler in_Clickhandler, Label lblUpdateStatusMessage2) {
		TableLayout layout = new TableLayout();
		layout.setColumns(2);

		LayoutContainer container = new LayoutContainer();

		container.setLayout(layout);
		container.setStyleAttribute("padding", "4px");

		TableData tableData = null;
		tableData = new TableData();
		tableData.setVerticalAlign(VerticalAlignment.TOP);
		if (lblUpdateStatusMessage2 != null)
			tableData.setRowspan(3);
		else
			tableData.setRowspan(2);

		container.add(in_NewUpdatesImage, tableData);

		tableData = new TableData();
		tableData.setVerticalAlign(VerticalAlignment.TOP);
		Label NewUpdateslbl = new Label(in_strNewUpdateslabel);
		NewUpdateslbl.setStyleName("homepage_summary_label");

		container.add(NewUpdateslbl, tableData);

		tableData = new TableData();
		tableData.setVerticalAlign(VerticalAlignment.TOP);
		container.add(in_NewUpdatesDesclbl, tableData);

		if (lblUpdateStatusMessage2 != null)
			container.add(lblUpdateStatusMessage2, tableData);

		in_cpNewUpdatePanel.add(container);

		return container;
	}
	//added by cliicy.luo for BI
	
	
	private Text archiveDescText;

	private void CreateArchivePart(ContentPanel in_cpNewUpdatePanel) {
		ArchiveImage = archiveImagePrototype.createImage();
		archiveDescText = new Text(UIContext.Constants.NA());
		archiveDescText.setStyleName("homepage_summary_description");

		archiveSpaceSavedText = new Text();
		archiveSpaceSavedText.setStyleName("homepage_summary_description");
		loadArchive = new LoadingStatus();

		addRecentBackup(in_cpNewUpdatePanel,
				UIContext.Messages.homepageSummaryMostRecentArchiveLabel(),
				ArchiveImage, archiveDescText, archiveSpaceSavedText,
				loadArchive);
	}

	private void hideCommonPanels() {
		lcBackupsPanel.hide();
		lcRestorePanel.hide();
		lsBackupsetPanel.hide();
		lcDestinationPanel.hide();
		lcLegendContainer.hide();
		if (isFileCopyEnabled) {
			cpArchivePanel.hide();
		}
		totalStatusImage.setVisible(false);
	}

	private void showCommonPanels() {
		lcBackupsPanel.show();
		if (!isBackupSet) {
			lcRestorePanel.show();
			lsBackupsetPanel.hide();
		}

		else {
			lsBackupsetPanel.show();
			lcRestorePanel.hide();
		}

		lcDestinationPanel.show();
		if (isFileCopyEnabled) {
			cpArchivePanel.show();
		}
		totalStatusImage.setVisible(true);
	}

	private void hideBackupLabels() {
		lcBackupsPanel.hide();
		lcRestorePanel.hide();
		lsBackupsetPanel.hide();
		lcDestinationPanel.hide();
		lcLegendContainer.hide();
		lcBackupsNotConfiguredPanel.show();
		lcLegendContainer.hide();
	}

	private void showBackupLabels() {
		lcBackupsPanel.show();
		if (!isBackupSet) {
			lcRestorePanel.show();
			lsBackupsetPanel.hide();
		}

		else {
			lsBackupsetPanel.show();
			lcRestorePanel.hide();
		}

		lcDestinationPanel.show();
		lcLegendContainer.show();
		lcBackupsNotConfiguredPanel.hide();
		lcLegendContainer.show();
	}

	@Override
	public void refresh(Object data) {
		final BackupInformationSummaryModel result = (BackupInformationSummaryModel) data;
		panel.unmask();

		if (!isDriverInstalled || !isRestarted) {

			noDriverPanel.show();
			if (isDriverInstalled) {
				helpHtml.setText("");

				driverLabel.setText(UIContext.Messages.notRestartAfterInstall(UIContext.productNameD2D));
			}

			totalStatusImage.setUrl(ICON_LARGE_ERROR_URL);
			UpdatesImage.setUrl(ICON_ERROR_URL);
			UpdatesImage.setTitle(UIContext.Messages.D2DAutoUpdateNotConfigured());
			NewUpdatesSettingslbl.setText(UIContext.Messages.D2DAutoUpdateNotConfigured());

			lblUpdateStatusMessage.removeStyleName("homepage_NewUpdates_Description");
			lblUpdateStatusMessage.addStyleName("homepage_summary_description");

			checkNewUpdatePanel();
			
			// added by cliicy.luo
			UpdatesBIImage.setUrl(ICON_ERROR_URL);
			UpdatesBIImage.setTitle(UIContext.Messages.D2DAutoUpdateNotConfigured());
			NewBIUpdatesSettingslbl.setText(UIContext.Messages.D2DAutoUpdateNotConfigured());

			lblBIUpdateStatusMessage.removeStyleName("homepage_NewUpdates_Description");
			lblBIUpdateStatusMessage.addStyleName("homepage_summary_description");
			checkNewBIUpdatePanel();
			// added by cliicy.luo
			return;
		}

		if (result == null) {

			lcBackupsNotConfiguredPanel.show();

			totalStatusImage.setUrl(ICON_LARGE_ERROR_URL);
			UpdatesImage.setUrl(ICON_ERROR_URL);
			UpdatesImage.setTitle(UIContext.Messages.D2DAutoUpdateNotConfigured());
			NewUpdatesSettingslbl.setText(UIContext.Messages.D2DAutoUpdateNotConfigured());

			lblUpdateStatusMessage.removeStyleName("homepage_NewUpdates_Description");
			lblUpdateStatusMessage.addStyleName("homepage_summary_description");

			checkNewUpdatePanel();

			// added by cliicy.luo
			UpdatesBIImage.setUrl(ICON_ERROR_URL);
			UpdatesBIImage.setTitle(UIContext.Messages.D2DAutoUpdateNotConfigured());
			NewBIUpdatesSettingslbl.setText(UIContext.Messages.D2DAutoUpdateNotConfigured());

			lblBIUpdateStatusMessage.removeStyleName("homepage_NewUpdates_Description");
			lblBIUpdateStatusMessage.addStyleName("homepage_summary_description");
			checkNewBIUpdatePanel();
			// added by cliicy.luo
			return;
		}

		cpNewUpdatePanel.hide();
		cpNewBIUpdatePanel.hide();//added by cliicy.luo
		showCommonPanels();

		if (result != null) {
			// backup: last backup, recovery points, destination capacity
			refreshBackup(result);

			if (result.getDestinationCapacityModel() != null) {
				refreshBackupDestInfo(result.getDestinationCapacityModel());
			}
			// auto update
			refreshAutoUpdate(result.getupdateSettingsModel());//marked by cliicy.luo for debug
			refreshBIAutoUpdate(result.getupdateSettingsModel());//added by cliicy.luo
			// license
			LicInfoModel licInfo = result.getLicInfo();
			refreshLicense(licInfo);
			// updating archive job status
			ArchiveJobInfoModel archiveJobInfo = result.getArchiveJobInfo();
			refreshArchive(archiveJobInfo);

			refreshTotalStatus();
		}
	}

	private void checkDestinationFreeThreshold(
			final DestinationCapacityModel result, long threshold,
			boolean estimatedValue) {
		if (result != null) {
			long freeSize = result.getTotalFreeSize();
			String freeSizeStr = Utils.bytes2String(freeSize);

			if (result.getTotalVolumeSize() != 0) {
				if (freeSize <= threshold) {
					destinationCapacityImage.setUrl(ICON_WARNING_URL);
					String destinationDescription;
					if (!estimatedValue)
						destinationDescription = UIContext.Messages
								.homepageSummaryDestThresholdReached(freeSizeStr);
					else
						destinationDescription = UIContext.Messages
								.homepageSummaryDestFreeSizeLow(freeSizeStr,
										LeastBackupSize);

					destinationCapacityDescriptionText
							.setText(destinationDescription);
				} else {
					destinationCapacityImage.setUrl(ICON_FINISH_URL);
				}
			} else {
				destinationCapacityImage.setUrl(ICON_ERROR_URL);
			}
		} else {
			destinationCapacityImage.setUrl(ICON_ERROR_URL);
		}
	}

	public static void setPatchInfo(PatchInfoModel in_patchInfo) {
		patchInfo = in_patchInfo;
		updatePatchInfo();
	}

	//added by cliicy.luo
	public static void setBIPatchInfo(BIPatchInfoModel in_patchInfo) {
		patchBIInfo = in_patchInfo;
		updateBIPatchInfo();
	}
	//added by cliicy.luo
	
	public static void updatePatchInfo() {
		if (patchInfo != null) {
			gSummaryPanel.iUpdateStatus = 1;
			gSummaryPanel.lblUpdateStatusMessage
					.removeStyleName("homepage_NewUpdates_Description");
			gSummaryPanel.lblUpdateStatusMessage
					.removeStyleName("homepage_NewUpdates_Description_Link");
			gSummaryPanel.lblUpdateStatusMessage
					.addStyleName("homepage_summary_description");
			switch (patchInfo.getError_Status()) {
			case ERROR_GET_PATCH_INFO_SUCCESS:
				gSummaryPanel.iUpdateStatus = 0;
				gSummaryPanel.lblUpdateStatusMessage.setText(UIContext.Messages
						.NewD2DUpdatesareAvailableMessage());
				gSummaryPanel.lblUpdateStatusMessage
						.removeStyleName("homepage_summary_description");
				gSummaryPanel.lblUpdateStatusMessage
						.addStyleName("homepage_NewUpdates_Description");
				gSummaryPanel.lblUpdateStatusMessage
						.addStyleName("homepage_NewUpdates_Description_Link");
				
				gSummaryPanel.lblUpdateStatusMessage.setVisible(true);
				break;
			case ERROR_NONEW_PATCHES_AVAILABLE:
				gSummaryPanel.lblUpdateStatusMessage.setText("");

				gSummaryPanel.lblUpdateStatusMessage.setVisible(false);
				break;
			case ERROR_GET_PATCH_INFO_FAIL:
				gSummaryPanel.lblUpdateStatusMessage.setText("");
				break;
			}

			if (patchInfo.getInstallStatus() == 1) {
				gSummaryPanel.iUpdateStatus = 1;

				gSummaryPanel.lblUpdateStatusMessage.setText("");
				gSummaryPanel.lblUpdateStatusMessage.setVisible(false);
			}

			if (!bDownloadServerAvailable) {
				if ((patchInfo != null) && (patchInfo.getDownloadStatus() != 1)) {
					gSummaryPanel.lblUpdateStatusMessage.setText("");
				}
			}
		}
	}

	public static void UpdateAutoUpdateStatus() {
		if (iAutoUpdateConfigurationStatus == -2) {
			gSummaryPanel.UpdatesImage.setVisible(false);
			gSummaryPanel.NewUpdatesSettingslbl.setVisible(false);
		} else {
			gSummaryPanel.UpdatesImage.setVisible(true);
			gSummaryPanel.NewUpdatesSettingslbl.setVisible(true);
		}
		if (iAutoUpdateConfigurationStatus == -1) {
			gSummaryPanel.UpdatesImage.setUrl(ICON_ERROR_URL);
			gSummaryPanel.UpdatesImage.setTitle(UIContext.Messages.D2DAutoUpdateNotConfigured());
			gSummaryPanel.NewUpdatesSettingslbl.setText(UIContext.Messages.D2DAutoUpdateNotConfigured());
		} else if (bIsTestConnectionExecuted && !bDownloadServerAvailable) {
			gSummaryPanel.UpdatesImage.setUrl(ICON_ERROR_URL);
			gSummaryPanel.UpdatesImage.setTitle(UIContext.Messages.D2DUnableToConnectDownloadServer(UIContext.productNameD2D));
			gSummaryPanel.NewUpdatesSettingslbl.setText(UIContext.Messages.D2DUnableToConnectDownloadServer(UIContext.productNameD2D));
		} else if ((iAutoUpdateConfigurationStatus == 0)
				|| (gSummaryPanel.iUpdateStatus == 0)) {
			gSummaryPanel.UpdatesImage.setUrl(ICON_WARNING_URL);

			if (iAutoUpdateConfigurationStatus == 0) {
				gSummaryPanel.UpdatesImage.setTitle(UIContext.Messages.D2DAutoUpdateNotEnabled());
				gSummaryPanel.NewUpdatesSettingslbl.setText(UIContext.Messages.D2DAutoUpdateNotEnabled());
			} else {
				gSummaryPanel.UpdatesImage.setTitle("");
				gSummaryPanel.NewUpdatesSettingslbl.setText("");
			}

		} else if (iAutoUpdateConfigurationStatus == 1) {
			gSummaryPanel.UpdatesImage.setUrl(ICON_FINISH_URL);
			gSummaryPanel.UpdatesImage.setTitle(UIContext.Messages.AutoUpdatesAreEnabledMessage());
			gSummaryPanel.NewUpdatesSettingslbl.setText(UIContext.Messages.AutoUpdatesAreEnabledMessage());

		}

		if (isShowNewUpdatePanel()) {
			if ((gSummaryPanel.totalStatusImage.getUrl() == ICON_LARGE_ERROR_URL)
					|| (gSummaryPanel.UpdatesImage.getUrl() == ICON_ERROR_URL)) {
				gSummaryPanel.totalStatusImage.setUrl(ICON_LARGE_ERROR_URL);
				gSummaryPanel.totalStatusImage.setTitle(gSummaryPanel
						.getTotalStatusImageTitle(ICON_ERROR_URL));
			} else if ((gSummaryPanel.totalStatusImage.getUrl() == ICON_LARGE_WARNING_URL)
					|| (gSummaryPanel.UpdatesImage.getUrl() == ICON_WARNING_URL)) {
				gSummaryPanel.totalStatusImage.setUrl(ICON_LARGE_WARNING_URL);
				gSummaryPanel.totalStatusImage.setTitle(gSummaryPanel
						.getTotalStatusImageTitle(ICON_WARNING_URL));
			} else if ((gSummaryPanel.totalStatusImage.getUrl() == ICON_LARGE_FINISH_URL)
					&& (gSummaryPanel.UpdatesImage.getUrl() == ICON_FINISH_URL)) {
				gSummaryPanel.totalStatusImage.setUrl(ICON_LARGE_FINISH_URL);
				gSummaryPanel.totalStatusImage.setTitle(gSummaryPanel
						.getTotalStatusImageTitle(ICON_FINISH_URL));
			}
		} else if ((gSummaryPanel.totalStatusImage.getUrl() == ICON_LARGE_WARNING_URL)
				|| (gSummaryPanel.UpdatesImage.getUrl() == ICON_WARNING_URL)) {
			gSummaryPanel.totalStatusImage.setUrl(ICON_LARGE_WARNING_URL);
			gSummaryPanel.totalStatusImage.setTitle(gSummaryPanel
					.getTotalStatusImageTitle(ICON_WARNING_URL));
		} else if ((gSummaryPanel.totalStatusImage.getUrl() == ICON_LARGE_FINISH_URL)
				&& (gSummaryPanel.UpdatesImage.getUrl() == ICON_FINISH_URL)) {
			gSummaryPanel.totalStatusImage.setUrl(ICON_LARGE_FINISH_URL);
			gSummaryPanel.totalStatusImage.setTitle(gSummaryPanel
					.getTotalStatusImageTitle(ICON_FINISH_URL));
		}

		checkNewUpdatePanel();//marked by cliicy.luo for debug
	
		
		gSummaryPanel.lsLoadingUpdates.hideIndicator();
		gSummaryPanel.refreshTotalStatus();
		return;
	}

	private static void checkNewUpdatePanel() {
		if (UIContext.serverVersionInfo.isShowUpdate() != null
				&& !UIContext.serverVersionInfo.isShowUpdate()) {
			cpNewUpdatePanel.hide();
		} else {
			cpNewUpdatePanel.show();
		}
	}

	
	private static boolean isShowNewUpdatePanel() {
		if (UIContext.serverVersionInfo.isShowUpdate() != null
				&& !UIContext.serverVersionInfo.isShowUpdate()) {
			return false;
		} else {
			return true;
		}
	}			

	private void beforeLoadBackupSummary() {
		this.loadLastBackup.showIndicator();
		if (!isBackupSet)
			this.loadRevPoints.showIndicator();
		else
			this.loadBacSets.showIndicator();
		this.loadDestCapacity.showIndicator();
		this.backupDescriptionText.hide();
		this.recoveryPointsDescriptionText.setVisible(false);
		recoveryPointsRepeatDescriptionText.setVisible(false);
		recoveryPointsDailyDescriptionText.setVisible(false);
		recoveryPointsWeeklyDescriptionText.setVisible(false);
		recoveryPointsMonthlyDescriptionText.setVisible(false);
		this.recoverySetsDescriptionText.setVisible(false);
		this.destinationCapacityDescriptionText.hide();
		this.destinationText.hide();
		this.destinationHealthText.hide();
		lcLegendContainer.hide();
		destinationHtml.setHTML("");
	}

	private void afterLoadBackupSummary() {
		gSummaryPanel.loadLastBackup.hideIndicator();
		gSummaryPanel.loadRevPoints.hideIndicator();
		gSummaryPanel.loadBacSets.hideIndicator();
		if (!isBackupSet) {
			gSummaryPanel.recoverySetsDescriptionText.setVisible(false);

			if (!isAdvanced) {
				gSummaryPanel.recoveryPointsDescriptionText.setVisible(true);
				gSummaryPanel.recoveryPointsRepeatDescriptionText
						.setVisible(false);
				gSummaryPanel.recoveryPointsDailyDescriptionText
						.setVisible(false);
				gSummaryPanel.recoveryPointsWeeklyDescriptionText
						.setVisible(false);
				gSummaryPanel.recoveryPointsMonthlyDescriptionText
						.setVisible(false);
			} else {
				gSummaryPanel.recoveryPointsDescriptionText.setVisible(false);
				gSummaryPanel.recoveryPointsRepeatDescriptionText
						.setVisible(true);
				gSummaryPanel.recoveryPointsDailyDescriptionText
						.setVisible(isDayShow);
				gSummaryPanel.recoveryPointsWeeklyDescriptionText
						.setVisible(isWeekShow);
				gSummaryPanel.recoveryPointsMonthlyDescriptionText
						.setVisible(isMonthShow);
			}
		} else {
			gSummaryPanel.recoverySetsDescriptionText.setVisible(true);
			gSummaryPanel.recoveryPointsDescriptionText.setVisible(false);
			gSummaryPanel.recoveryPointsRepeatDescriptionText.setVisible(false);
			gSummaryPanel.recoveryPointsDailyDescriptionText.setVisible(false);
			gSummaryPanel.recoveryPointsWeeklyDescriptionText.setVisible(false);
			gSummaryPanel.recoveryPointsMonthlyDescriptionText
					.setVisible(false);
		}

		gSummaryPanel.backupDescriptionText.show();
		gSummaryPanel.loadDestCapacity.hideIndicator();
		gSummaryPanel.destinationCapacityDescriptionText.show();
		gSummaryPanel.destinationText.show();
	}

	private void refreshBackup(final BackupInformationSummaryModel result) {
		if (result == null) {
			beforeLoadBackupSummary();
			service.getBackupInforamtionSummary(new BaseAsyncCallback<BackupInformationSummaryModel>() {

				@Override
				public void onFailure(Throwable caught) {
					afterLoadBackupSummary();

					super.onFailure(caught);
				}

				@Override
				public void onSuccess(BackupInformationSummaryModel result) {
					if (result != null)
						SummaryPanel.this.refreshBackup(result);
					else {
						// TODO pop up error
					}
					currentBackup = result;
					afterLoadBackupSummary();

				}
			});
			return;
		} else {
			currentBackup = result;
		}
		if (currentBackup.getDestinationCapacityModel() != null) {
			this.refreshBackupDestInfo(currentBackup
					.getDestinationCapacityModel());
		}
		isBackupSet = result.isBackupSet();
		isAdvanced = result.isAdvanced();

		if (isBackupSet) {
			lsBackupsetPanel.show();
			lcRestorePanel.hide();
		} else {
			lcRestorePanel.show();
			lsBackupsetPanel.hide();
		}
		if (currentBackup.getDestination() != null
				&& !currentBackup.getDestination().isEmpty()) {
			showBackupLabels();
			backupImage.setUrl(ICON_FINISH_URL);
			if (currentBackup.getDestinationCapacityModel() != null) {
				this.refreshBackupDestInfo(currentBackup
						.getDestinationCapacityModel());
			}

			// full backup
			RecentBackupModel recentBackup = getRecentBackup(result);
			if (recentBackup != null) {
				backupImage.setUrl(convertStatusToImageURL(recentBackup
						.getStatus()));
				backupDescriptionLabel.setText(UIContext.Messages
						.homepageSummaryMostRecentBackupLabel(recentBackup
								.getName()));
				backupDescriptionText.setText(Utils.formatDateToServerTime(
						recentBackup.getTime(),
						recentBackup.getTimeZoneOffset()));
				backupImage
						.setTitle(UIContext.Messages
								.homepageSummaryMostBackupStatus(Utils
										.backupStatus2String(recentBackup
												.getStatus())));
			} else {
				String backupMsg = UIContext.Messages
						.homepageSummaryMostRecentBackupLabel(UIContext.Constants
								.remoteDeployAddServerNALabel());
				backupDescriptionLabel.setText(backupMsg);
				backupDescriptionText.setText(UIContext.Constants.NA());
				backupImage.setUrl(ICON_WARNING_URL);
				backupImage.setTitle(UIContext.Constants
						.homepageSummaryMostFullBackupNotRun());
			}
			// wanqi06
			if (!isBackupSet)
				refreshRecoveryPoints(result);
			else
				refreshRecoverySets(result);
			refreshDest(result);
		} else {
			hideBackupLabels();
			backupImage.setUrl(ICON_ERROR_URL);
		}

		refreshTotalStatus();
		for (BackupSummaryEventListener listener : listeners) {
			listener.backupSummaryUpdated(currentBackup);
		}
	}

	private void refreshRecoverySets(final BackupInformationSummaryModel result) {
		this.inProgressRecoverySet.hide();
		recoverySetLargerText.hide();
		recoverySetsDetailMessage.setText(UIContext.Constants
				.recoverySetsClickHintClickLabel());
		if (result.getRecoverySetCount() <= 0) {
			recoverySetsImage.setUrl(ICON_ERROR_URL);
			recoverySetsImage.setTitle(UIContext.Constants
					.homepageSummaryRecoverySetError());
		} else if (result.getRecoverySetCount() > 0
				&& result.getRecoverySetCount() < result.getRetentionCount() + 1) {
			recoverySetsImage.setUrl(ICON_WARNING_URL);
			recoverySetsImage.setTitle(UIContext.Messages
					.homepageSummaryRecoverySetWarningTooltip(
							result.getRecoverySetCount() - 1,
							result.getRetentionCount()));
		} else if (result.getRecoverySetCount() >= result.getRetentionCount() + 1) {
			recoverySetsImage.setUrl(ICON_FINISH_URL);
			recoverySetsImage.setTitle(UIContext.Constants
					.homepageSummaryRecoverySetInfo());
			if (result.getRecoverySetCount() > result.getRetentionCount() + 1) {
				refreshMountedSessions(result, recoverySetLargerText);
			}
		}
		if (result.getRecoverySetCount() > 0) {
			recoverySetsDescriptionText.setText(UIContext.Messages
					.homepageSummaryRecoverySets(
							result.getRecoverySetCount() - 1,
							result.getRetentionCount()));
			this.inProgressRecoverySet.show();
		} else {
			recoverySetsDescriptionText
					.setText(UIContext.Messages.homepageSummaryRecoverySets(0,
							result.getRetentionCount()));
		}

		refreshDest(result);
		refreshTotalStatus();
	}

	private static int RECOVERY_POINT_THRESHHOLD = 200;

	private boolean isDayShow = false;
	private boolean isWeekShow = false;
	private boolean isMonthShow = false;

	private void refreshRecoveryPoints(
			final BackupInformationSummaryModel result) {
		// Recovery Points
		if (!result.isAdvanced()) {
			recoveryPointsDescriptionText.setVisible(true);
			recoveryPointsDescriptionText.setText(UIContext.Messages
					.homepageSummaryRecoveryPoints(
							result.getRecoveryPointCount(),
							result.getRetentionCount()));

			recoveryPointsLargerText.hide();
			mergeDelayedText.hide();
			startManualMergeText.setVisible(false);
			if (result.getRecoveryPointCount() <= 0) {
				recoveryPointsImage.setUrl(ICON_ERROR_URL);
				recoveryPointsImage.setTitle(UIContext.Constants
						.homepageSummaryRecoveryPointError());
			} else if (result.getRecoveryPointCount() > 0
					&& result.getRecoveryPointCount() < result
							.getRetentionCount()) {
				recoveryPointsImage.setUrl(ICON_WARNING_URL);
				recoveryPointsImage.setTitle(UIContext.Messages
						.homepageSummaryRecoveryPointWarningTooltip(
								result.getRecoveryPointCount(),
								result.getRetentionCount()));
			} else if (result.getRecoveryPointCount() == result
					.getRetentionCount()) {
				recoveryPointsImage.setUrl(ICON_FINISH_URL);
				recoveryPointsImage.setTitle(UIContext.Constants
						.homepageSummaryRecoveryPointInfo());
			} else {
				recoveryPointsImage.setUrl(ICON_FINISH_URL);
				recoveryPointsImage.setTitle(UIContext.Constants
						.homepageSummaryRecoveryPointInfoExceed());

			}
		} else {
			recoveryPointsDescriptionText.setVisible(false);

			// daily/weekly/monthly
			isDayShow = false;
			isWeekShow = false;
			isMonthShow = false;
			int dayCnt = result.getRecoveryPointCount4Day();
			int weekCnt = result.getRecoveryPointCount4Week();
			int monthCnt = result.getRecoveryPointCount4Month();

			int dayRetention = 0;
			int weekRetention = 0;
			int monthRetention = 0;

			if (result.isPeriodEnabled()) {
				if (result.getAdvanceScheduleModel() != null
						&& result.getAdvanceScheduleModel().periodScheduleModel != null) {
					EveryDayScheduleModel dayScheduleModel = result
							.getAdvanceScheduleModel().periodScheduleModel.dayScheduleModel;
					if (dayScheduleModel != null
							&& dayScheduleModel.isEnabled()) {
						dayRetention = dayScheduleModel.getRetentionCount();
						isDayShow = true;
					}
					EveryWeekScheduleModel weekScheduleModel = result
							.getAdvanceScheduleModel().periodScheduleModel.weekScheduleModel;
					if (weekScheduleModel != null
							&& weekScheduleModel.isEnabled()) {
						weekRetention = weekScheduleModel.getRetentionCount();
						isWeekShow = true;
					}
					EveryMonthScheduleModel monthScheduleModel = result
							.getAdvanceScheduleModel().periodScheduleModel.monthScheduleModel;
					if (monthScheduleModel != null
							&& monthScheduleModel.isEnabled()) {
						monthRetention = monthScheduleModel.getRetentionCount();
						isMonthShow = true;
					}
				}
				recoveryPointsDailyDescriptionText.setText(UIContext.Messages
						.dailyRecoveryPointsStatus(dayCnt, dayRetention));
				recoveryPointsWeeklyDescriptionText.setText(UIContext.Messages
						.weeklyRecoveryPointsStatus(weekCnt, weekRetention));
				recoveryPointsMonthlyDescriptionText.setText(UIContext.Messages
						.monthlyRecoveryPointsStatus(monthCnt, monthRetention));
			}

			// repeat
			int repeatCount = result.getRecoveryPointCount4Repeat();
			if (!isDayShow) {
				repeatCount += dayCnt;
			}
			if (!isWeekShow) {
				repeatCount += weekCnt;
			}
			if (!isMonthShow) {
				repeatCount += monthCnt;
			}
			recoveryPointsRepeatDescriptionText.setText(UIContext.Messages
					.repeatRecoveryPointsStatus(repeatCount,
							result.getRetentionCount()));

			recoveryPointsLargerText.hide();
			mergeDelayedText.hide();
			startManualMergeText.setVisible(false);

			if (result.getRecoveryPointCount() <= 0
					&& ((isDayShow && dayCnt <= 0) || !isDayShow)
					&& ((isWeekShow && weekCnt <= 0) || !isWeekShow)
					&& ((isMonthShow && monthCnt <= 0) || !isMonthShow)) {
				recoveryPointsImage.setUrl(ICON_ERROR_URL);
				recoveryPointsImage.setTitle(UIContext.Constants
						.homepageSummaryRecoveryPointError());
			} else if (repeatCount < result.getRetentionCount()
					|| (isDayShow && dayCnt < dayRetention)
					|| (isWeekShow && weekCnt < weekRetention)
					|| (isMonthShow && monthCnt < monthRetention)) {
				recoveryPointsImage.setUrl(ICON_WARNING_URL);
				recoveryPointsImage.setTitle(UIContext.Constants
						.homepageSummaryRecoveryPointWarning());
			} else if (repeatCount == result.getRetentionCount()
					&& (isDayShow && dayCnt == dayRetention || !isDayShow)
					&& (isWeekShow && weekCnt == weekRetention || !isWeekShow)
					&& (isMonthShow && monthCnt == monthRetention || !isMonthShow)) {
				recoveryPointsImage.setUrl(ICON_FINISH_URL);
				recoveryPointsImage.setTitle(UIContext.Constants
						.homepageSummaryRecoveryPointInfo());
			} else {
				recoveryPointsImage.setUrl(ICON_FINISH_URL);
				recoveryPointsImage.setTitle(UIContext.Constants
						.homepageSummaryRecoveryPointInfoExceed());

			}

		}

		afterLoadBackupSummary();

		refreshMountedSessions(result, recoveryPointsLargerText);

		refreshDest(result);
		refreshTotalStatus();
	}

	private void refreshDest(final BackupInformationSummaryModel result) {
		destinationHealthText.hide();

		if (result.getRpsHostModel() != null) {
			destinationCapacityDescriptionText.setText(UIContext.Messages
					.homepageSummaryRPSServerName(result.getRpsHostModel()
							.getHostName()));
			String dsDisplayName = "";
			if (result.getRpsPolicy4D2D() != null) {
				dsDisplayName = result.getRpsPolicy4D2D()
						.getDataStoreDisplayName();
				destinationText.setText(UIContext.Messages
						.homepageSummaryRPSDataStoreName(dsDisplayName));

				this.dataStorePolicyModel.setName(result.getRpsPolicy4D2D()
						.getDataStoreName());
				this.dataStorePolicyModel.setDisplayName(dsDisplayName);
			}
			lcLegendContainer.hide();
			String iconTooltip = "";
			if (result.getDSRunningState() == 1) {
				if (result.getDsHealth().equalsIgnoreCase("GREEN")) {
					destinationCapacityImage.setUrl(ICON_FINISH_URL);
					iconTooltip = UIContext.Messages
							.homepageSummaryDestinationDataStoreInfoTooltip();
				} else if (result.getDsHealth().equalsIgnoreCase("YELLOW")) {
					destinationCapacityImage.setUrl(ICON_WARNING_URL);
					iconTooltip = UIContext.Messages
							.homepageSummaryDestinationDataStoreWarnTooltip();
				} else if (result.getDsHealth().equalsIgnoreCase("RED")) {
					destinationCapacityImage.setUrl(ICON_ERROR_URL);
					iconTooltip = UIContext.Messages
							.homepageSummaryDestinationDataStoreErrorTooltip();
				} else {
					destinationCapacityImage.setUrl(ICON_ERROR_URL);
					iconTooltip = UIContext.Messages
							.homepageSummaryDestinationDataStoreUnknownTooltip();
					destinationHealthText.setText(iconTooltip);
					destinationHealthText.show();

				}
			} else {
				destinationCapacityImage.setUrl(ICON_ERROR_URL);
				if (result.getDSRunningState() == 2) // Deleted
					iconTooltip = UIContext.Constants
							.rpsDedupErrorDedupStoreIsNotExist();
				else
					iconTooltip = UIContext.Messages
							.homepageSummaryDestinationDataStoreUnknownTooltip();
				destinationHealthText.setText(iconTooltip);
				destinationHealthText.show();

			}
			destinationCapacityImage.setTitle(iconTooltip);
		} else {
			if (result.getDestinationCapacityModel() != null) {
				final String freeSizeStr = Utils.bytes2String(result
						.getDestinationCapacityModel().getTotalFreeSize());
				if (result.getErrorCode() != 0) {
					destinationCapacityDescriptionText
							.setText(UIContext.Constants
									.homepageSummaryDestinationNotAccessible());
					destinationCapacityImage.setUrl(ICON_ERROR_URL);
				} else
					destinationCapacityDescriptionText
							.setText(UIContext.Messages
									.homepageSummaryDestinationCapacity(freeSizeStr));

				if (result.getDestinationCapacityModel().getTotalVolumeSize() == 0) {
					destinationCapacityImage.setUrl(ICON_ERROR_URL);
				}

				if (result.getDestination() != null) {
					if (result.getDestination().startsWith("\\\\"))
						destinationText.setText(UIContext.Messages
								.homepageSummaryDestinationPath(result
										.getDestination()));
					else {
						destinationText.setText(UIContext.Messages
								.homepageSummaryDestinationPath(result
										.getDestination()));
					}
				}
			} else {
				destinationCapacityImage.setUrl(ICON_ERROR_URL);
			}

		}
	}

	private void refreshBackupDestInfo(final BackupSettingsModel setting,
			final DestinationCapacityModel destCapacity) {
		if (setting != null && setting.getEnableSpaceNotification() != null
				&& setting.getEnableSpaceNotification()) {
			long threshold = 0;
			if (AdvancedSettings.MeasureUnitPercent.equals(setting
					.getSpaceMeasureUnit())) {
				if (destCapacity != null)
					threshold = destCapacity.getTotalVolumeSize()
							* setting.getSpaceMeasureNum().longValue() / 100;
			} else
				threshold = setting.getSpaceMeasureNum().longValue() * 1024 * 1024;

			if (setting.rpsDestSettings == null)
				checkDestinationFreeThreshold(destCapacity, threshold, false);
		} else {
			if (setting.rpsDestSettings == null)
				checkDestinationFreeThresholdByPercent(destCapacity);

		}

		// Chart
		double total = 0;
		if (destCapacity != null) {
			long backupSpace = destCapacity.getFullBackupSize()
					+ destCapacity.getIncrementalBackupSize()
					+ destCapacity.getResyncBackupSize()
					+ destCapacity.getCatalogSize();

			long othersSpace = destCapacity.getTotalVolumeSize()
					- destCapacity.getTotalFreeSize() - backupSpace;

			if (othersSpace < 0)
				othersSpace = 0;

			total = destCapacity.getTotalVolumeSize();

			double backup = ((double) (backupSpace)) / total;
			double free = ((double) destCapacity.getTotalFreeSize()) / total;
			double others = ((double) othersSpace) / total;

			GWT.log("Other Percent:" + String.valueOf(others), null);
			int backupPercent = (int) (backup * 100);
			int freePercent = (int) (free * 100);
			int othersPercent = (int) (others * 100);

			legendBackupText.setText(UIContext.Messages
					.homepageSummaryLegendBackup(Utils
							.bytes2String(backupSpace)));
			legendOthersText.setText(UIContext.Messages
					.homepageSummaryLegendOthers(Utils
							.bytes2String(othersSpace)));
			legendFreeText.setText(UIContext.Messages
					.homepageSummaryLegendFree(Utils.bytes2String(destCapacity
							.getTotalFreeSize())));

			StringBuffer buffer = new StringBuffer();
			buffer.append("<table width=\"85%\" height=\"15\" style=\"border:1px solid #000000; margin: 0px;\" CELLPADDING=0 CELLSPACING=0>");
			buffer.append("<tr>");

			if (destCapacity.getTotalVolumeSize() > 0) {
				if (backupPercent > 0)
					appendTDChart(buffer, backupPercent,
							"images/legend_incremental.png",
							legendBackupText.getText());
				if (othersPercent > 0)
					appendTDChart(buffer, othersPercent,
							"images/legend_others.png",
							legendOthersText.getText());
				appendTDChart(buffer, freePercent,
						"images/legend_freeSpace.png", legendFreeText.getText());
			} else {
				buffer.append("<td/>");
			}
			buffer.append("</tr></table>");
			if (setting.rpsDestSettings == null)
				destinationHtml.setHTML(buffer.toString());

			if (setting.rpsDestSettings == null) {
				if (destCapacity.getTotalVolumeSize() == 0)
					destinationCapacityImage.setTitle(UIContext.Constants
							.homepageSummaryDestinationSizeZero());
				else
					destinationCapacityImage.setTitle(UIContext.Messages
							.homepageSummaryDestinationTooltip(
									(int) freePercent, Utils
											.bytes2String(destCapacity
													.getTotalFreeSize())));
			}
			backupLegendImage.setTitle(legendBackupText.getText());
			othersLegendImage.setTitle(legendOthersText.getText());
			freeLegendImage.setTitle(legendFreeText.getText());
			if (setting.rpsDestSettings == null)
				lcLegendContainer.show();
		}
	}

	private void refreshBackupDestInfo(
			DestinationCapacityModel destCapacityModel) {
		final DestinationCapacityModel destCapacity = destCapacityModel;
		// Destination
		loginService
				.getBackupConfiguration(new BaseAsyncCallback<BackupSettingsModel>() {

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
					}

					@Override
					public void onSuccess(BackupSettingsModel settings) {
						refreshBackupDestInfo(settings, destCapacity);
					}
				});
	}

	private void refreshTotalStatus() {
		if (backupImage.getUrl().contains(ICON_ERROR_URL)
				// wanqi06
				|| recoveryPointsImage.getUrl().contains(ICON_ERROR_URL)
				|| recoverySetsImage.getUrl().contains(ICON_ERROR_URL)
				|| destinationCapacityImage.getUrl().contains(ICON_ERROR_URL)
				|| licImage.getUrl().contains(ICON_ERROR_URL)
				|| ArchiveImage.getUrl().contains(ICON_ERROR_URL)) {
			totalStatusImage.setUrl(ICON_LARGE_ERROR_URL);
			totalStatusImage.setTitle(getTotalStatusImageTitle(ICON_ERROR_URL));
		} else if (backupImage.getUrl().contains(ICON_WARNING_URL)
				// wanqi06
				|| recoveryPointsImage.getUrl().contains(ICON_WARNING_URL)
				|| recoverySetsImage.getUrl().contains(ICON_WARNING_URL)
				|| destinationCapacityImage.getUrl().contains(ICON_WARNING_URL)
				|| licImage.getUrl().contains(ICON_WARNING_URL)
				|| ArchiveImage.getUrl().contains(ICON_WARNING_URL)) {
			totalStatusImage.setUrl(ICON_LARGE_WARNING_URL);
			totalStatusImage
					.setTitle(getTotalStatusImageTitle(ICON_WARNING_URL));
		} else {
			totalStatusImage.setUrl(ICON_LARGE_FINISH_URL);
			totalStatusImage
					.setTitle(getTotalStatusImageTitle(ICON_LARGE_FINISH_URL));
		}
	}

	private void refreshLicense(LicInfoModel licInfo) {
		if (licInfo == null) {
			service.getLicInfo(new BaseLicenseAsyncCallback<LicInfoModel>() {

				@Override
				public void onSuccess(LicInfoModel result) {
					if (result == null) {
						// pop up error
					} else {
						SummaryPanel.this.refreshLicense(result);
					}
				}
			});
			return;
		}
		refreshUDPLicense(licInfo);

	}

	private void refreshUDPLicense(LicInfoModel licInfo) {
		if (licInfo == null) {
			return;
		}
		int flag1 = UIContext.serverVersionInfo.isNCE() ? 1 : -1;
		UIContext.serverVersionInfo
				.setNCE(licInfo.getBaseLic() == LicInfoModel.LICENSE_BASE_NCE);
		int flag2 = UIContext.serverVersionInfo.isNCE() ? 1 : -1;
		if (flag1 + flag2 == 0) {
			UIContext.d2dHomepagePanel.refreshLicenseNCE();
		}

		boolean visible = false;
		String imagUrl = ICON_FINISH_URL;
		String licMsg = "";
		switch (licInfo.getBaseLic()) {
		case LicInfoModel.LICENSE_BASE_ERR_WORKSTATION:
			visible = true;
			imagUrl = ICON_ERROR_URL;
			licMsg = UIContext.Messages
					.homepageSummaryLicenseFailurefor(UIContext.Constants
							.homepageSummaryLicWorkstation());
			break;
		case LicInfoModel.LICENSE_BASE_ERR_STANDARD_SOCKET:
			visible = true;
			imagUrl = ICON_ERROR_URL;
			licMsg = UIContext.Messages
					.homepageSummaryLicenseFailurefor(UIContext.Constants
							.homepageSummaryLicStandardSocket());
			break;
		case LicInfoModel.LICENSE_BASE_ERR_ADVANCED_SOCKET:
			visible = true;
			imagUrl = ICON_ERROR_URL;
			licMsg = UIContext.Messages
					.homepageSummaryLicenseFailurefor(UIContext.Constants
							.homepageSummaryLicAdvancedSocket());
			break;
		case LicInfoModel.LICENSE_BASE_WARN_TRIAL:
			visible = true;
			imagUrl = ICON_FINISH_URL;
			licMsg = UIContext.Constants.homepageSummaryLicTrial();
			break;
		case LicInfoModel.LICENSE_BASE_NCE:
			visible = true;
			imagUrl = ICON_INFO_URL;
			licMsg = UIContext.Constants.homepageSummaryLicNCE();
			break;
		case LicInfoModel.LICENSE_BASE_SUCCESS:
			visible = false;
			break;
		default:
		}
		licImage.setUrl(imagUrl);
		licDescText.setText(licMsg);
		licenseInfoModel = licInfo;
		if (licInfo.getBaseLic() == LicInfoModel.LICENSE_BASE_NCE) {
			purchaseLicenseLink.setVisible(true);
		} else {
			purchaseLicenseLink.setVisible(false);
		}
		if (visible) {
			licPanel.show();
		} else {
			licPanel.hide();
		}

	}

	private void refreshAutoUpdate(final UpdateSettingsModel result) {
		updateSettings = result;
		if (updateSettings != null) {
			if (!updateSettings.getAutoCheckupdate()) {
				iAutoUpdateConfigurationStatus = 0;
			} else {
				iAutoUpdateConfigurationStatus = 1;
			}

		} else {
			iAutoUpdateConfigurationStatus = -2;

		}

		lsLoadingUpdates.showIndicator();
		if (bIsTestConnectionExecuted == false) {
			bIsTestConnectionExecuted = true;
			loginService.testDownloadServerConnection(updateSettings,
					new AsyncCallback<UpdateSettingsModel>() {				

						@Override
						public void onSuccess(UpdateSettingsModel result) {
							if (result != null) {
								bDownloadServerAvailable = false;
								switch (result.getDownloadServerType()) {
								case 0:
									iDownloadServerType = 0;
									if (result.getCAServerStatus() == 1) {
										bDownloadServerAvailable = true;
									}
									break;
								case 1: {
									iDownloadServerType = 1;
									StagingServerModel[] StagingServers = result
											.getStagingServers();
									for (StagingServerModel stagingServer : StagingServers) {
										if (stagingServer
												.getStagingServerStatus() == 1) {
											bDownloadServerAvailable = true;
											break;
										}
									}
								}
									break;
								}
							}

							updatePatchStatus();
						}

						@Override
						public void onFailure(Throwable caught) {
							lsLoadingUpdates.hideIndicator();
						}
					});
		} else {
			updatePatchStatus();
		}
	}

	private void updatePatchStatus() {

		loginService.isUsingEdgePolicySettings(
				SettingsTypesForUI.BackupSettings,
				new AsyncCallback<Boolean>() {
					@Override
					public void onFailure(Throwable caught) {
						UpdateAutoUpdateStatus();
						
					}

					@Override
					public void onSuccess(Boolean result) {
						boolean isEditable = !result;
						if (isEditable) {
							commonService
									.getUpdateInfo(new BaseAsyncCallback<PatchInfoModel>() {

										@Override
										public void onFailure(Throwable caught) {
											super.onFailure(caught);
											UpdateAutoUpdateStatus();
											
										}

										@Override
										public void onSuccess(
												PatchInfoModel result) {
											patchInfo = result;
											updatePatchInfo();
											UpdateAutoUpdateStatus();
											
										}
									});
						} else {
							cpNewUpdatePanel.hide();
							lsLoadingUpdates.hideIndicator();
						}
					}
				});
	}

	private void refreshArchive(ArchiveJobInfoModel archiveJobInfo) {
		if (archiveJobInfo != null) {
			if (isFileCopyEnabled) {
				cpArchivePanel.show();
			}

			if (archiveJobInfo.getlastJobDateTime().equalsIgnoreCase("N/A")) {
				archiveDescText.setText(UIContext.Constants.NA());
			} else {
				String lastJobDate = null;
				// Date dt = archiveJobInfo.getlastJobDateTime();
				if (archiveJobInfo.getlastJobDateTime() != null) {
					Date date = Utils.serverString2LocalDate(archiveJobInfo
							.getlastJobDateTime());
					lastJobDate = Utils.localDate2LocalString(date);
				}
				if (lastJobDate != null)
					archiveDescText.setText(lastJobDate);
				else
					archiveDescText
							.setText(archiveJobInfo.getlastJobDateTime());
			}

			switch (archiveJobInfo.getarchiveJobStatus()) {
			case Utils.ArchiveJobFinished:// success
				ArchiveImage.setUrl(ICON_FINISH_URL);
				archiveSpaceSavedText.show();
				long archiveDataSize = archiveJobInfo.getArchiveDataSize();
				if (!(archiveDataSize == 0L))
					archiveSpaceSavedText.setText(Utils
							.bytes2String(archiveDataSize)
							+ " "
							+ UIContext.Constants.SpaceSavedMessage());
				else
					archiveSpaceSavedText.hide();
				break;
			case Utils.ArchiveJobFailed:// failed
			case Utils.ArchiveJobCrashed:// crashed
				ArchiveImage.setUrl(ICON_ERROR_URL);
				archiveSpaceSavedText.hide();
				break;
			case Utils.ArchiveJobCancelled:
			case Utils.ArchiveJobIncomplete: // incomplete
				ArchiveImage.setUrl(ICON_WARNING_URL);
				archiveSpaceSavedText.hide();
				break;
			}
			ArchiveImage.setTitle(UIContext.Messages
					.homepageSummaryMostRecentArchiveStatusLabel(Utils
							.ConvertArchiveJobStatusToString(archiveJobInfo
									.getarchiveJobStatus())));
			refreshTotalStatus();
		} else {
			if (isFileCopyEnabled) {
				cpArchivePanel.hide();
			}
		}

	}

	@Override
	public void refresh(Object data, int changeSource) {
		mask(UIContext.Messages.LoadingSummaryMessage(UIContext.productNameD2D));
		switch (changeSource) {
		case IRefreshable.CS_ARCHIVE_FINISHED:
			updateArchive();
			break;
		case IRefreshable.CS_COPY_FINISHED:
			this.refreshLicense(null);
			break;
		case IRefreshable.CS_BACKUP_FINISHED:
		case IRefreshable.CS_FSCATALOG_FINISHED:
		case IRefreshable.CS_MERGEJOB_FINISHED:
		case IRefreshable.CS_MERGEJOB_STARTED:
			this.refreshBackup((BackupInformationSummaryModel) data);
			this.refreshLicense(null);
			break;
		case IRefreshable.CS_CONFIG_CHANGED:
			if (lcBackupsNotConfiguredPanel.isVisible()) {
				lcBackupsPanel.show();
				if (!isBackupSet) {
					lcRestorePanel.show();
					lsBackupsetPanel.hide();
				} else {
					lsBackupsetPanel.show();
					lcRestorePanel.hide();
				}

				lcDestinationPanel.show();
				lcBackupsNotConfiguredPanel.hide();
			}

			this.refreshBackup(null);
			updateArchive();
			loginService.getPreferences(new BaseAsyncCallback<PreferencesModel>() {
						@Override
						public void onSuccess(PreferencesModel result) {
							if (result != null) {
								result.getupdateSettings()
										.setD2DBackupsConfigured(true);
								gSummaryPanel.refreshAutoUpdate(result.getupdateSettings());//marked by cliicy.luo for debug
								gSummaryPanel.refreshBIAutoUpdate(result.getupdateSettings());//added by cliicy.luo
							}
						}
					});
			this.refreshLicense(null);
			break;
		default:
			break;
		}
		this.updatePatchStatus();
		this.updateBIPatchStatus();
		unmask();
	}

	// added by cliicy.luo
	private void refreshBIAutoUpdate(final UpdateSettingsModel result) {
		updateBISettings = result;
		if (updateBISettings != null) {
			if (!updateBISettings.getAutoCheckupdate()) {
				iAutoBIUpdateConfigurationStatus = 0;
			} else {
				iAutoBIUpdateConfigurationStatus = 1;
			}
		} else {
			iAutoBIUpdateConfigurationStatus = -2;
		}

		lsLoadingUpdates.showIndicator();
		if (bIsTestBIConnectionExecuted == false) {
			bIsTestBIConnectionExecuted = true;
			loginService.testBIDownloadServerConnection(updateBISettings,
					new AsyncCallback<UpdateSettingsModel>() {

						@Override
						public void onSuccess(UpdateSettingsModel result) {
							if (result != null) {
								bDownloadBIServerAvailable = false;
								switch (result.getDownloadServerType()) {
								case 0:
									iDownloadBIServerType = 0;
									if (result.getCAServerStatus() == 1) {
										bDownloadBIServerAvailable = true;
									}
									break;
								case 1: {
									iDownloadBIServerType = 1;
									StagingServerModel[] StagingServers = result.getStagingServers();
									for (StagingServerModel stagingServer : StagingServers) {
										if (stagingServer.getStagingServerStatus() == 1) {
											bDownloadBIServerAvailable = true;
											break;
										}
									}
								}
									break;
								}
							}

							updateBIPatchStatus();
						}

						@Override
						public void onFailure(Throwable caught) {
							lsLoadingBIUpdates.hideIndicator();
						}
					});
		} else {
			updateBIPatchStatus();
		}
	}

	private void updateBIPatchStatus() {

		loginService.isUsingEdgePolicySettings(SettingsTypesForUI.BackupSettings,new AsyncCallback<Boolean>() {
					@Override
					public void onFailure(Throwable caught) {
						UpdateBIAutoUpdateStatus();
					}

					@Override
					public void onSuccess(Boolean result) {
						boolean isEditable = !result;
						if (isEditable) {
							commonService.getBIUpdateInfo(new BaseAsyncCallback<BIPatchInfoModel>() {

										@Override
										public void onFailure(Throwable caught) {
											super.onFailure(caught);
											UpdateBIAutoUpdateStatus();
										}

										@Override
										public void onSuccess(BIPatchInfoModel result) {
											patchBIInfo = result;
											updateBIPatchInfo();
											UpdateBIAutoUpdateStatus();
										}
									});
						} else {
							cpNewBIUpdatePanel.hide();
							lsLoadingBIUpdates.hideIndicator();
						}
					}
				});
	}

	private static void checkNewBIUpdatePanel() {

		if (UIContext.serverVersionInfo.isShowUpdate() != null
				&& !UIContext.serverVersionInfo.isShowUpdate()) {
			cpNewBIUpdatePanel.hide();
		} else {
			cpNewBIUpdatePanel.show();
		}
	}
	
	
	public static void UpdateBIAutoUpdateStatus() {
		if (iAutoBIUpdateConfigurationStatus == -2) {
			gSummaryPanel.UpdatesBIImage.setVisible(false);
			gSummaryPanel.NewBIUpdatesSettingslbl.setVisible(false);
		} else {
			gSummaryPanel.UpdatesBIImage.setVisible(true);
			gSummaryPanel.NewBIUpdatesSettingslbl.setVisible(true);
		}
		if (iAutoBIUpdateConfigurationStatus == -1) {
			gSummaryPanel.UpdatesBIImage.setUrl(ICON_ERROR_URL);
			gSummaryPanel.UpdatesBIImage.setTitle(UIContext.Messages.D2DAutoUpdateNotConfigured());
			gSummaryPanel.NewBIUpdatesSettingslbl.setText(UIContext.Messages.D2DAutoUpdateNotConfigured());
		}else if (bIsTestBIConnectionExecuted && !bDownloadBIServerAvailable)																	// by																		// 
		{
			gSummaryPanel.UpdatesBIImage.setUrl(ICON_ERROR_URL);
			gSummaryPanel.UpdatesBIImage.setTitle(UIContext.Messages.D2DUnableToConnectDownloadBIServer(UIContext.productNameD2D));
			gSummaryPanel.NewBIUpdatesSettingslbl.setText(UIContext.Messages.D2DUnableToConnectDownloadBIServer(UIContext.productNameD2D));
		}
		else if ((iAutoBIUpdateConfigurationStatus == 0)
				|| (gSummaryPanel.iBIUpdateStatus == 0)) {
			gSummaryPanel.UpdatesBIImage.setUrl(ICON_WARNING_URL);

			if (iAutoBIUpdateConfigurationStatus == 0) {
				gSummaryPanel.UpdatesBIImage.setTitle(UIContext.Messages.D2DAutoUpdateNotEnabled());
				gSummaryPanel.NewBIUpdatesSettingslbl.setText(UIContext.Messages.D2DAutoUpdateNotEnabled());
			} else {
				gSummaryPanel.UpdatesBIImage.setTitle("");
				gSummaryPanel.NewBIUpdatesSettingslbl.setText("");
			}

		} else if (iAutoBIUpdateConfigurationStatus == 1) {
			gSummaryPanel.UpdatesBIImage.setUrl(ICON_FINISH_URL);
			gSummaryPanel.UpdatesBIImage.setTitle(UIContext.Messages.AutoUpdatesAreEnabledMessage());
			gSummaryPanel.NewBIUpdatesSettingslbl.setText(UIContext.Messages.AutoUpdatesAreEnabledMessage());

		}
		/*
		if (isShowNewUpdatePanel()) {
			if ((gSummaryPanel.totalStatusImage.getUrl() == ICON_LARGE_ERROR_URL)
					|| (gSummaryPanel.UpdatesImage.getUrl() == ICON_ERROR_URL)) {
				gSummaryPanel.totalStatusImage.setUrl(ICON_LARGE_ERROR_URL);
				gSummaryPanel.totalStatusImage.setTitle(gSummaryPanel
						.getTotalStatusImageTitle(ICON_ERROR_URL));
			} else if ((gSummaryPanel.totalStatusImage.getUrl() == ICON_LARGE_WARNING_URL)
					|| (gSummaryPanel.UpdatesImage.getUrl() == ICON_WARNING_URL)) {
				gSummaryPanel.totalStatusImage.setUrl(ICON_LARGE_WARNING_URL);
				gSummaryPanel.totalStatusImage.setTitle(gSummaryPanel
						.getTotalStatusImageTitle(ICON_WARNING_URL));
			} else if ((gSummaryPanel.totalStatusImage.getUrl() == ICON_LARGE_FINISH_URL)
					&& (gSummaryPanel.UpdatesImage.getUrl() == ICON_FINISH_URL)) {
				gSummaryPanel.totalStatusImage.setUrl(ICON_LARGE_FINISH_URL);
				gSummaryPanel.totalStatusImage.setTitle(gSummaryPanel
						.getTotalStatusImageTitle(ICON_FINISH_URL));
			}
		} else if ((gSummaryPanel.totalStatusImage.getUrl() == ICON_LARGE_WARNING_URL)
				|| (gSummaryPanel.UpdatesImage.getUrl() == ICON_WARNING_URL)) {
			gSummaryPanel.totalStatusImage.setUrl(ICON_LARGE_WARNING_URL);
			gSummaryPanel.totalStatusImage.setTitle(gSummaryPanel
					.getTotalStatusImageTitle(ICON_WARNING_URL));
		} else if ((gSummaryPanel.totalStatusImage.getUrl() == ICON_LARGE_FINISH_URL)
				&& (gSummaryPanel.UpdatesImage.getUrl() == ICON_FINISH_URL)) {
			gSummaryPanel.totalStatusImage.setUrl(ICON_LARGE_FINISH_URL);
			gSummaryPanel.totalStatusImage.setTitle(gSummaryPanel
					.getTotalStatusImageTitle(ICON_FINISH_URL));
		}*/


		checkNewBIUpdatePanel(); //added by cliicy.luo
		
		gSummaryPanel.lsLoadingBIUpdates.hideIndicator();
		gSummaryPanel.refreshTotalStatus();
		return;
	}
	
	public static void updateBIPatchInfo() {
		if (patchBIInfo != null) {
			gSummaryPanel.iBIUpdateStatus = 1;
			gSummaryPanel.lblBIUpdateStatusMessage.removeStyleName("homepage_NewUpdates_Description");
			gSummaryPanel.lblBIUpdateStatusMessage.removeStyleName("homepage_NewUpdates_Description_Link");
			gSummaryPanel.lblBIUpdateStatusMessage.addStyleName("homepage_summary_description");
			
			Integer nResult = patchBIInfo.getError_Status();
			switch (nResult) {
			case ERROR_GET_PATCH_INFO_SUCCESS:
				gSummaryPanel.iBIUpdateStatus = 0;
				gSummaryPanel.lblBIUpdateStatusMessage.setText(UIContext.Messages.NewD2DBIUpdatesareAvailableMessage());
				gSummaryPanel.lblBIUpdateStatusMessage.removeStyleName("homepage_summary_description");
				gSummaryPanel.lblBIUpdateStatusMessage.addStyleName("homepage_NewUpdates_Description");
				gSummaryPanel.lblBIUpdateStatusMessage.addStyleName("homepage_NewUpdates_Description_Link");
				gSummaryPanel.lblBIUpdateStatusMessage.setVisible(true);
				break;
			case ERROR_NONEW_PATCHES_AVAILABLE:
				gSummaryPanel.lblBIUpdateStatusMessage.setText("");

				gSummaryPanel.lblBIUpdateStatusMessage.setVisible(false);
				break;
			case ERROR_GET_PATCH_INFO_FAIL:
				gSummaryPanel.lblBIUpdateStatusMessage.setText("");
				break;
			}

			if (patchBIInfo.getInstallStatus() == 1) {
				gSummaryPanel.iBIUpdateStatus = 1;

				gSummaryPanel.lblBIUpdateStatusMessage.setText("");
				gSummaryPanel.lblBIUpdateStatusMessage.setVisible(false);
			}

			if (!bDownloadBIServerAvailable) {
				if ((patchBIInfo != null) && (patchBIInfo.getDownloadStatus() != 1)) {
					gSummaryPanel.lblBIUpdateStatusMessage.setText("");
				}
			}
		}
	}
	// added by cliicy.luo
	
	private void updateArchive() {
		this.archiveDescText.hide();
		this.archiveSpaceSavedText.hide();
		this.loadArchive.showIndicator();
		service.getArchiveInfoSummary(new BaseAsyncCallback<ArchiveJobInfoModel>() {

			@Override
			public void onFailure(Throwable caught) {
				SummaryPanel.this.refreshArchive(null);
				gSummaryPanel.archiveDescText.show();
				gSummaryPanel.archiveSpaceSavedText.show();
				gSummaryPanel.loadArchive.hideIndicator();
				super.onFailure(caught);
			}

			@Override
			public void onSuccess(ArchiveJobInfoModel result) {
				SummaryPanel.this.refreshArchive(result);
				gSummaryPanel.archiveDescText.show();
				gSummaryPanel.archiveSpaceSavedText.show();
				gSummaryPanel.loadArchive.hideIndicator();
			}
		});
	}

	@Override
	protected String getVMInstanceUUID() {
		return null;
	}

}


	

