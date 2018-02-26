package com.ca.arcflash.ui.client.homepage;

import java.util.Date;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.AppType;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonSettingWindow;
import com.ca.arcflash.ui.client.common.IRefreshable;
import com.ca.arcflash.ui.client.homepage.navigation.NavigationBorderLayout;
import com.ca.arcflash.ui.client.homepage.navigation.NavigationPanel;
import com.ca.arcflash.ui.client.model.BackupInformationSummaryModel;
import com.ca.arcflash.ui.client.model.LicInfoModel;
import com.ca.arcflash.ui.client.model.ProtectionInformationModel;
import com.ca.arcflash.ui.client.mount.MountWindow;
import com.ca.arcflash.ui.client.recoverypoint.RecoveryPointWindow;
import com.ca.arcflash.ui.client.restore.RestoreWizardWindow;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.state.CookieProvider;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;

public class D2DHomePageTab extends LayoutContainer implements IRefreshable{
	
	private final HomepageServiceAsync service = GWT.create(HomepageService.class);
	
	public static final String LOCATION_PARAM = "location";
	public static final String LOCATION_BACKUP_SETTINGS = "backup";
	public static final String LOCATION_RESTORE = "restore";
	public static final String LOCATION_EXPORT = "CopyRecoveryPoint";
	public static final String LOCATION_GETTINGSTARTED = "gettingstarted";
	public static final String LOCATION_MOUNTVOLUME = "mountVolume";
	
	
	private ProtectionInformationPanel protectionInformationPanel = new ProtectionInformationPanel();
	private BackupHistoryPanel recentBackupPanel = new BackupHistoryPanel();
	//private StatusPieChartPanel statusPieChartPanel = new StatusPieChartPanel();
	private SummaryPanel summaryPanel = new SummaryPanel();
	private MonitorPanel monitorPanel = new MonitorPanel();
	private NavigationPanel navigationPanel = new NavigationPanel();
	
	public D2DHomePageTab() 
	{
		
	}
	
	public void onRender(Element target, int index) {
		super.onRender(target, index);
		
		UIContext.d2dHomepagePanel = this;
		UIContext.hostPage = this;
		
		NavigationBorderLayout layout = new NavigationBorderLayout();
		layout.setContainerStyle("navigation-background");
		setLayout(layout);

		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
		centerData.setMargins(new Margins(0, 1, 0, 5));

		BorderLayoutData eastData = new BorderLayoutData(LayoutRegion.EAST, 285, 250, 400);
		eastData.setSplit(true);
		eastData.setCollapsible(true);
		eastData.setFloatable(true);
		eastData.setMargins(new Margins(0, 5, 0, 0));

		BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH, 30);
		southData.setMargins(new Margins(0, 5, 5, 5));

		add(createLeftPanelWithResizing(), centerData);
		add(navigationPanel, eastData);
		add(new CopyRightPanel(), southData);
			
		String location = Window.Location.getParameter(LOCATION_PARAM);
		if (location != null)
		{
			openLocation(location);
		}
		else
		{
			CookieProvider provider = new CookieProvider(null, new Date(8099, 11, 31), null, false); 
			if (provider.get("donotshowgettingstarted") == null &&  (UIContext.serverVersionInfo.edgeInfoCM == null))
			{						
				openLocation(LOCATION_GETTINGSTARTED);	
			}
		}
	}		
	
	// used for new D2D home page
	private LayoutContainer createLeftPanelWithResizing() {
		
		LayoutContainer container = new LayoutContainer();
		
		container.setScrollMode(Scroll.AUTOY);
		
		RowLayout rowLayout = new RowLayout(Orientation.VERTICAL);
		rowLayout.setAdjustForScroll(true);

		container.setLayout(rowLayout);		
		
		container.add(monitorPanel, new RowData(1, -1, new Margins(0, 0, 0, 0)));
//		container.add(mergePanel, new RowData(1, -1, new Margins(4, 0, 0, 0)));
		container.add(summaryPanel, new RowData(1, -1, new Margins(4, 0, 0, 0)));		
		
		container.add(protectionInformationPanel, new RowData(1, -1, new Margins(4, 0, 0, 0)));	
		
		container.add(recentBackupPanel, new RowData(1, -1, new Margins(4, 0, 0, 0)));
		
		return container;
	}
	
	@Override
	public final void refresh(Object data) {
		GWT.log("Homepage Refresh......", null);
		summaryPanel.mask(UIContext.Messages.LoadingSummaryMessage(UIContext.productNameD2D));
		service.getBackupInforamtionSummaryWithLicInfo(new BaseAsyncCallback<BackupInformationSummaryModel>(){

			@Override
			public void onFailure(Throwable caught) {
				
				summaryPanel.unmask();
				super.onFailure(caught);
			}

					@Override
					public void onSuccess(BackupInformationSummaryModel result) {
						summaryPanel.unmask();
						GWT.log("get Summary model successfully", null);
						//statusPieChartPanel.refresh(result);
						summaryPanel.refresh(result);
					}

				});

		//there's no apparent need to refresh trust host list 
//		refreshTrustHost();
		recentBackupPanel.refresh(null);
		protectionInformationPanel.refresh(null);
	}

	private void openLocation(String location)
	{
		if (location.compareTo(LOCATION_BACKUP_SETTINGS) == 0)
		{
			//BackupSettingsWindow window = new BackupSettingsWindow();
			CommonSettingWindow window = new CommonSettingWindow(AppType.D2D);
			window.setSize(880, 600);
			window.setModal(true);
			window.show();
		}
		else if (location.compareTo(LOCATION_RESTORE) == 0)
		{
			RestoreWizardWindow window = new RestoreWizardWindow();
			window.setModal(true);
			window.show();
		}
		else if (location.compareTo(LOCATION_GETTINGSTARTED) == 0)
		{
			GettingStartedWindow window = new GettingStartedWindow();
			window.setModal(true);
			window.show();			
		}
		else if (location.compareTo(LOCATION_EXPORT) == 0)
		{
			RecoveryPointWindow window = new RecoveryPointWindow();
			window.setModal(true);
			window.show();
		}
		else if (location.compareTo(LOCATION_MOUNTVOLUME) == 0)
		{
			MountWindow window = new MountWindow();
			window.setModal(true);
			window.show();
		}
	}
	
	public final void refreshProtectionSummary(Object data)
	{
		protectionInformationPanel.refresh(null);
	}

	@Override
	public void refresh(Object data, int changeSource) {		
		summaryPanel.refresh(data, changeSource);
		if(changeSource != IRefreshable.CS_D2D_UPDATE && changeSource != IRefreshable.CS_MERGEJOB_STARTED){
			recentBackupPanel.refresh(null);
		}
		service.getProtectionInformation(new BaseAsyncCallback<ProtectionInformationModel[]>(){
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
			}
			@Override
			public void onSuccess(ProtectionInformationModel[] result) {
				if(result==null || result.length == 0){
					return;
				}
				protectionInformationPanel.refresh(result);
			}
		});
				
		if(changeSource == IRefreshable.CS_CONFIG_CHANGED){
			monitorPanel.refresh(null);
		}
	}
	
	public void refreshBackupHistoryLayout()
	{
		if (recentBackupPanel != null )
		{
			recentBackupPanel.refreshLayout();
		}
	}

	public void refreshLicenseNCE() {
		navigationPanel.refreshLicenseNCE();
		
	}
}
