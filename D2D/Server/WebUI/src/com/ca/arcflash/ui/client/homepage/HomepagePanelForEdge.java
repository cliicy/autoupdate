package com.ca.arcflash.ui.client.homepage;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.AppType;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.CommonSettingWindow;
import com.ca.arcflash.ui.client.common.IRefreshable;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.log.LogWindow;
import com.ca.arcflash.ui.client.model.BackupInformationSummaryModel;
import com.ca.arcflash.ui.client.recoverypoint.RecoveryPointWindow;
import com.ca.arcflash.ui.client.restore.RestoreWizardWindow;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;

public class HomepagePanelForEdge extends LayoutContainer implements IRefreshable {
	
	private final HomepageServiceAsync homepageService = GWT.create(HomepageService.class);
	private final CommonServiceAsync commonService = GWT.create(CommonService.class);
	
	private String uuid;
	private String host;
	private int port;
	private boolean isHttps;
	
	private Button backupNowButton;
	private Button backupSettingButton;
	private Button restoreButton;
	private Button exportRecoveryPointButton;
	private Button viewLogButton;
	
	private ProtectionInformationPanel protectionInformationPanel;
	private BackupHistoryPanel recentBackupPanel;
	private SummaryPanel summaryPanel;
	private MonitorPanel monitorPanel;
	private AppType applicationType;
	
	private boolean actionEnabled = true; 
	private boolean isbkpNowClicked = false;
	
	public HomepagePanelForEdge(boolean actionEnabled) {
		this.actionEnabled = actionEnabled;
	}
	
	public HomepagePanelForEdge(boolean actionEnabled, AppType applicationType) {
		this.actionEnabled = actionEnabled;
		this.applicationType = applicationType;
	}
	
	public HomepagePanelForEdge(String uuid, String host, int port, boolean isHttps) {
		this.uuid = uuid;
		this.host = host;
		this.port = port;
		this.isHttps = isHttps;
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		this.setLayout(new RowLayout());
		
		UIContext.hostPage = this;
		
		if (this.uuid == null) {
			renderHomepage();
			return;
		}
		
		this.add(new Label("Authentication, please wait ..."), new RowData(Style.DEFAULT, Style.DEFAULT, new Margins(4)));
		
		InternalLogin.login(uuid, host, port, isHttps, new BaseAsyncCallback<Void>(false) {
			
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				handleFailure(caught);
			}
			
			@Override
			public void onSuccess(Void result) {
				renderHomepage();
			}
			
		});
	}
	
	private void handleFailure(Throwable caught) {
		this.removeAll();
		this.add(new Label("Authentication failed, please contact the Administrator!"), new RowData(Style.DEFAULT, Style.DEFAULT, new Margins(4)));
		this.layout();
	}

	private void renderHomepage() {
		this.removeAll();
		
		if (actionEnabled) {
			renderTaskToolBar();
		}
		
		renderMainPanel();
		
		this.layout();
	}

	private void renderTaskToolBar() {
		ToolBar bar = new ToolBar();
		
		SelectionListener<ButtonEvent> buttonSelectionListener = new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				onTaskButtonSelection(ce.getButton());
			}
			
		};
		
		backupNowButton = new Button(UIContext.Constants.homepageTasksBackupNowLabel());
		backupNowButton.ensureDebugId("ed7493a8-6cf3-426b-a7d8-83ab0d47d22d");
		Utils.addToolTip(backupNowButton, UIContext.Constants.homepageTasksBackupNowDescription());
		backupNowButton.addSelectionListener(buttonSelectionListener);
		bar.add(backupNowButton);
		
		backupSettingButton = new Button(UIContext.Constants.homepageTasksBackupSettingLabel());
		backupSettingButton.ensureDebugId("5601b1b8-aa98-48b0-adba-dbe9796f5516");
		Utils.addToolTip(backupSettingButton, UIContext.Constants.homepageTasksBackupSettingDescription());
		backupSettingButton.addSelectionListener(buttonSelectionListener);
		bar.add(new SeparatorToolItem());
		bar.add(backupSettingButton);
		
		restoreButton = new Button(UIContext.Constants.homepageTasksRestoreLabel());
		restoreButton.ensureDebugId("941187bc-7359-4ef9-aff5-3e6cacb88ce6");
		Utils.addToolTip(restoreButton, UIContext.Constants.homepageTasksRestoreDescription());
		restoreButton.addSelectionListener(buttonSelectionListener);
		bar.add(new SeparatorToolItem());
		bar.add(restoreButton);
		
		exportRecoveryPointButton = new Button(UIContext.Constants.homepageTasksRecoveryPointsLabel());
		exportRecoveryPointButton.ensureDebugId("a09c9f6b-eb1b-43cf-b83c-030f7bea325b");
		Utils.addToolTip(exportRecoveryPointButton, UIContext.Constants.homepageTasksRecoveryPointsDescription());
		exportRecoveryPointButton.addSelectionListener(buttonSelectionListener);
		bar.add(new SeparatorToolItem());
		bar.add(exportRecoveryPointButton);
		
		viewLogButton = new Button(UIContext.Constants.homepageTasksLogsLabel());
		viewLogButton.ensureDebugId("eb0f828b-8602-417a-b1ee-b7a42cea1cdd");
		Utils.addToolTip(viewLogButton, UIContext.Constants.homepageTasksLogsDescription());
		viewLogButton.addSelectionListener(buttonSelectionListener);
		bar.add(new SeparatorToolItem());
		bar.add(viewLogButton);
		
		this.add(bar, new RowData(1, Style.DEFAULT));
	}
	
	private void onTaskButtonSelection(Button button) {
		if (button == backupNowButton && !isbkpNowClicked) {
			isbkpNowClicked = true;
			commonService.isOnlyFullBackup(new BaseAsyncCallback<Boolean>(false) {

				@Override
				public void onFailure(Throwable caught) {
					backup(false);
					super.onFailure(caught);
				}

				@Override
				public void onSuccess(Boolean result) {
					backup(result);
				}
				
				private void backup(boolean isCompressionLevelChagned) {
					isbkpNowClicked = false;
					BackupNowWindow bkpNowWnd = new BackupNowWindow();
					bkpNowWnd.setModal(true);									
					bkpNowWnd.show();
					bkpNowWnd.changeSettings(isCompressionLevelChagned);
				}
				
			});
		} else if (button == backupSettingButton) {
//			BackupSettingsWindow window = new BackupSettingsWindow();
			CommonSettingWindow window = new CommonSettingWindow(applicationType);
			window.setSize(840, 600);
			window.setModal(true);
			window.show();
		} else if (button == restoreButton) {
			RestoreWizardWindow window = new RestoreWizardWindow();
			window.setModal(true);
			window.show();
		} else if (button == exportRecoveryPointButton) {
			RecoveryPointWindow window = new RecoveryPointWindow();
			window.setModal(true);
			window.show();
		} else if (button == viewLogButton) {
			LogWindow window = new LogWindow(true);
			window.setModal(true);
			window.show();
		}
	}

	private void renderMainPanel() {
		protectionInformationPanel = new ProtectionInformationPanel();
		recentBackupPanel = new BackupHistoryPanel();
		//statusPieChartPanel = new StatusPieChartPanel();
		summaryPanel = new SummaryPanel();
		monitorPanel = new MonitorPanel();
		
		LayoutContainer container = new LayoutContainer();
		container.setScrollMode(Style.Scroll.AUTOY);
		
		RowLayout rowLayout = new RowLayout(Orientation.VERTICAL);
		rowLayout.setAdjustForScroll(true);
		
		container.setLayout(rowLayout);
		
		container.add(monitorPanel, new RowData(1, Style.DEFAULT, new Margins(0, 0, 0, 0)));		
		container.add(summaryPanel, new RowData(1, Style.DEFAULT, new Margins(4, 0, 0, 0)));		
		container.add(protectionInformationPanel, new RowData(1, Style.DEFAULT, new Margins(4, 0, 0, 0)));	
		container.add(recentBackupPanel, new RowData(1, Style.DEFAULT, new Margins(4, 0, 0, 0)));
		
		this.add(container, new RowData(1, 1));
	}

	@Override
	public void refresh(Object data) {
		homepageService.getBackupInforamtionSummaryWithLicInfo(new BaseAsyncCallback<BackupInformationSummaryModel>(false) {

			@Override
			public void onSuccess(BackupInformationSummaryModel result) {
				//statusPieChartPanel.refresh(result);
				summaryPanel.refresh(result);
			}
			
		});
		
		recentBackupPanel.refresh(null);
		protectionInformationPanel.refresh(null);
	}

	@Override
	public void refresh(Object data, int changeSource) {
		summaryPanel.refresh(data, changeSource);
		if(changeSource != IRefreshable.CS_D2D_UPDATE){
			recentBackupPanel.refresh(null);
			protectionInformationPanel.refresh(null);
		}
	}
}
