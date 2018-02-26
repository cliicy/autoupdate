package com.ca.arcflash.ui.client.vsphere.homepage;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.AppType;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.CommonSettingWindow;
import com.ca.arcflash.ui.client.common.IRefreshable;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.homepage.HomepageService;
import com.ca.arcflash.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcflash.ui.client.homepage.InternalLogin;
import com.ca.arcflash.ui.client.model.BackupInformationSummaryModel;
import com.ca.arcflash.ui.client.remotedeploy.RemoteDeployWindow;
import com.ca.arcflash.ui.client.restore.RestoreWizardWindow;
import com.ca.arcflash.ui.client.vsphere.log.VSphereLogWindow;
import com.ca.arcflash.ui.client.vsphere.recoverypoint.VSphereRecoveryPointWindow;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;

public class VSphereHomepagePanelForEdge extends LayoutContainer implements IRefreshable {
	
	private final HomepageServiceAsync service = GWT.create(HomepageService.class);
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
	private Button deployButton;
	
	public static final int REFRESH_INTERVAL = 10*1000;
	public static final String LOCATION_PARAM = "location";
	public static final String LOCATION_BACKUP_SETTINGS = "backup";
	public static final String LOCATION_RESTORE = "restore";
	public static final String LOCATION_GETTINGSTARTED = "gettingstarted";
	private VSphereProtectionInformationPanel protectionInformationPanel;
	private VSphereBackupHistoryPanel recentBackupPanel;
//	private VSphereStatusPieChartPanel statusPieChartPanel;
	private VSphereSummaryPanel summaryPanel;
	private VSphereMonitorPanel monitorPanel;

	private boolean actionEnabled = true; 
	private boolean isbkpNowClicked = false;
	private AppType applicationType;
	
	public VSphereHomepagePanelForEdge(boolean actionEnabled) {
		this.actionEnabled = actionEnabled;
	}
	
	public VSphereHomepagePanelForEdge(boolean actionEnabled, AppType applicationType) {
		this.actionEnabled = actionEnabled;
		this.applicationType = applicationType;
	}
	
	public VSphereHomepagePanelForEdge(String uuid, String host, int port, boolean isHttps) {
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
		backupNowButton.ensureDebugId("9A7CE792-A237-4324-B836-60276CA94965");
		Utils.addToolTip(backupNowButton, UIContext.Constants.homepageTasksBackupNowDescription());
		backupNowButton.addSelectionListener(buttonSelectionListener);
		bar.add(backupNowButton);
		
		backupSettingButton = new Button(UIContext.Constants.homepageTasksBackupSettingLabel());
		backupSettingButton.ensureDebugId("1F62DC5F-2191-449f-80A1-47E17A6E0964");
		Utils.addToolTip(backupSettingButton, UIContext.Constants.homepageTasksBackupSettingDescription());
		backupSettingButton.addSelectionListener(buttonSelectionListener);
		bar.add(new SeparatorToolItem());
		bar.add(backupSettingButton);
		
		restoreButton = new Button(UIContext.Constants.homepageTasksRestoreLabel());
		restoreButton.ensureDebugId("2F566CE3-1844-4aec-955C-BF3404C1AEBA");
		Utils.addToolTip(restoreButton, UIContext.Constants.homepageTasksRestoreDescription());
		restoreButton.addSelectionListener(buttonSelectionListener);
		bar.add(new SeparatorToolItem());
		bar.add(restoreButton);
		
		exportRecoveryPointButton = new Button(UIContext.Constants.homepageTasksRecoveryPointsLabel());
		exportRecoveryPointButton.ensureDebugId("4FDBD982-27E7-4dbf-BAA8-1A4A71341809");
		Utils.addToolTip(exportRecoveryPointButton, UIContext.Constants.homepageTasksRecoveryPointsDescription());
		exportRecoveryPointButton.addSelectionListener(buttonSelectionListener);
		bar.add(new SeparatorToolItem());
		bar.add(exportRecoveryPointButton);
		
		viewLogButton = new Button(UIContext.Constants.homepageTasksLogsLabel());
		viewLogButton.ensureDebugId("473FD72B-2427-45cd-9C2D-1A546E082704");
		Utils.addToolTip(viewLogButton, UIContext.Constants.homepageTasksLogsDescription());
		viewLogButton.addSelectionListener(buttonSelectionListener);
		bar.add(new SeparatorToolItem());
		bar.add(viewLogButton);
		
		deployButton = new Button(UIContext.Constants.homepageTasksDeployLabel());
		deployButton.ensureDebugId("2998D8AF-5235-47c8-B276-E8E8D2059F3D");
		Utils.addToolTip(deployButton, UIContext.Constants.homepageTasksDeployDescription());
		deployButton.addSelectionListener(buttonSelectionListener);
		bar.add(new SeparatorToolItem());
		bar.add(deployButton);
		
		if (UIContext.serverVersionInfo.edgeInfoCM != null) {
			deployButton.disable();
		}
		
		this.add(bar, new RowData(1, Style.DEFAULT));
	}
	
	private void onTaskButtonSelection(Button button) {
		if (button == backupNowButton && !isbkpNowClicked) {
			isbkpNowClicked = true;
			commonService.isVMCompressionLevelChagned(UIContext.backupVM,new BaseAsyncCallback<Boolean>() {

				@Override
				public void onFailure(Throwable caught) {
					backup(false);backup(false);
					super.onFailure(caught);
				}

				@Override
				public void onSuccess(Boolean result) {
					GWT.log("isCompressionLevelChagned:"
							+ result, null);
					backup(result);
				}
				
				private void backup(boolean isCompressionLevelChagned) {
					isbkpNowClicked = false;
					VSphereBackupNowWindow bkpNowWnd = new VSphereBackupNowWindow();
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
			VSphereRecoveryPointWindow window = new VSphereRecoveryPointWindow();
			window.setModal(true);
			window.show();
		} else if (button == viewLogButton) {
			VSphereLogWindow window = new VSphereLogWindow();
			window.setModal(true);
			window.show();
		} else if (button == deployButton) {
			RemoteDeployWindow window = new RemoteDeployWindow();
			window.setModal(true);
			window.show();
		}
	}

	private void renderMainPanel() {
		protectionInformationPanel = new VSphereProtectionInformationPanel();
		recentBackupPanel = new VSphereBackupHistoryPanel();
//		statusPieChartPanel = new VSphereStatusPieChartPanel();
		summaryPanel = new VSphereSummaryPanel();
		monitorPanel = new VSphereMonitorPanel();
		LayoutContainer container = new LayoutContainer();
		container.setScrollMode(Style.Scroll.AUTOY);
		
		RowLayout rowLayout = new RowLayout(Orientation.VERTICAL);
		rowLayout.setAdjustForScroll(true);
		container.setLayout(rowLayout);
		container.setAutoWidth(true);

		container.add(monitorPanel, new RowData(1, Style.DEFAULT, new Margins(0, 0, 0, 0)));
		container.add(summaryPanel, new RowData(1, Style.DEFAULT, new Margins(4, 0, 0, 0)));
		container.add(protectionInformationPanel, new RowData(1, Style.DEFAULT, new Margins(4, 0, 0, 0)));
		container.add(recentBackupPanel, new RowData(1, Style.DEFAULT, new Margins(4, 0, 0, 0)));

		this.add(container, new RowData(1, 1));
	}

	@Override
	public void refresh(Object data) {
		GWT.log("Homepage Refresh......", null);
		if(UIContext.uiType == 0){
			service.getBackupInforamtionSummaryWithLicInfo(new BaseAsyncCallback<BackupInformationSummaryModel>(){
	
				@Override
				public void onFailure(Throwable caught) {
					
					super.onFailure(caught);
				}
	
						@Override
						public void onSuccess(BackupInformationSummaryModel result) {
							GWT.log("get Summary model successfully", null);
//							statusPieChartPanel.refresh(result);
							summaryPanel.refresh(result);
						}
	
					});
		}else{
			service.getVMBackupInforamtionSummaryWithLicInfo(UIContext.backupVM,new BaseAsyncCallback<BackupInformationSummaryModel>(){
				
				@Override
				public void onFailure(Throwable caught) {
					
					super.onFailure(caught);
				}
	
						@Override
						public void onSuccess(BackupInformationSummaryModel result) {
							GWT.log("get Summary model successfully", null);
//							statusPieChartPanel.refresh(result);
							summaryPanel.refresh(result);
						}
	
					});
		}
		
		recentBackupPanel.refresh(null);
		protectionInformationPanel.refresh(null);
	}

	@Override
	public void refresh(Object data, int changeSource) {
		// TODO Auto-generated method stub
		
	}

}
