package com.ca.arcflash.ui.client.vsphere.homepage;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.AppType;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonSettingWindow;
import com.ca.arcflash.ui.client.common.IRefreshable;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.homepage.CopyRightPanel;
import com.ca.arcflash.ui.client.homepage.HomepageService;
import com.ca.arcflash.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcflash.ui.client.homepage.navigation.NavigationBorderLayout;
import com.ca.arcflash.ui.client.model.BackupInformationSummaryModel;
import com.ca.arcflash.ui.client.model.VMStatusModel;
import com.ca.arcflash.ui.client.restore.RestoreWizardWindow;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class VSphereHomePageTab extends TabItem implements IRefreshable {
	
	private final HomepageServiceAsync service = GWT.create(HomepageService.class);
	public static final int REFRESH_INTERVAL = 10*1000;
	public static final String TASK_PARAM = "task";
	public static final String TASK_BACKUP_SETTINGS = "backup";
	public static final String TASK_RESTORE = "restore";
	public static final String TASK_GETTINGSTARTED = "gettingstarted";
	private VSphereProtectionInformationPanel protectionInformationPanel = new VSphereProtectionInformationPanel();
	//private VSphereRecentBackupPanel recentBackupPanel = new VSphereRecentBackupPanel();
	//private VSphereStatusPieChartPanel statusPieChartPanel = new VSphereStatusPieChartPanel();
	private VSphereSummaryPanel summaryPanel = new VSphereSummaryPanel();
	private VSphereMonitorPanel monitorPanel = new VSphereMonitorPanel();
	private VSphereBackupHistoryPanel backupHistoryPanel = new VSphereBackupHistoryPanel();
//	private VSphereMergePanel mergePanel = new VSphereMergePanel();
	private LayoutContainer warningContainer;
	public static int NORTH_HEIGHT = 0;
	public static int WARNING_HEIGHT = 22;
	private BorderLayoutData northData;
	
	/*private Timer cmTimer;
	private static final int REFRESH_CM_INTERVAL = 60000;
	private boolean refreshRunning = false;*/
	
	
	public void onRender(Element target, int index) {
		super.onRender(target, index);
		setText(UIContext.Messages.windowTitle(UIContext.productNameD2D));
		UIContext.vSphereHomepagePanel = this;
		
		NavigationBorderLayout layout = new NavigationBorderLayout(AppType.VSPHERE);
		layout.setContainerStyle("navigation-background");
		setLayout(layout);
		
		northData = new BorderLayoutData(LayoutRegion.NORTH,NORTH_HEIGHT);
		northData.setMargins(new Margins(0, 0, 1, 0));
		
		add(createHeader(), northData);
		
		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
		centerData.setMargins(new Margins(0, 1, 0, 5));

		BorderLayoutData eastData = new BorderLayoutData(LayoutRegion.EAST, 250, 200, 600);
		eastData.setSplit(true);
		eastData.setCollapsible(true);
		eastData.setFloatable(true);
		eastData.setMargins(new Margins(2, 5, 5, 0));

		BorderLayoutData southData = new BorderLayoutData(LayoutRegion.SOUTH, 30);
		southData.setMargins(new Margins(0, 5, 0, 5));

		add(createLeftPanelWithResizing(), centerData);
		add(new VSphereNavigationPanel(), eastData);
		add(new CopyRightPanel(), southData);
		
		String task = Window.Location.getParameter(TASK_PARAM);
		openTask(task);
	}
	
	// Currently, vSphere only support restore task.
	private void openTask(String task) {
		if (task == null) {
			return;
		} else if (task.equalsIgnoreCase(TASK_RESTORE)) {
			RestoreWizardWindow window = new RestoreWizardWindow();
			window.setModal(true);
			window.show();
		} else if (task.equalsIgnoreCase(TASK_BACKUP_SETTINGS)) {
			CommonSettingWindow window = new CommonSettingWindow(AppType.VSPHERE);
			window.setSize(880, 655);
			window.setModal(true);
			window.show();
		}
	}

	private LayoutContainer createHeader()
	{
		final LayoutContainer container = new LayoutContainer();		
		TableLayout tableLayout = new TableLayout(1);
		tableLayout.setWidth("100%");
		
		// second row
		//container.add(createRow_2_User());
		
		//third
		container.add(createRow_3_Warning());
		refreshWarning();
		/*cmTimer = new Timer()
		{
			public void run()
			{
				if (UIContext.vSphereHomepagePanel.isRendered() && container.isRendered() && container.isVisible())
				{
					if (!refreshRunning)
					{
						refreshWarning();
					}
				}
			}
		};
		cmTimer.schedule(REFRESH_CM_INTERVAL);
		cmTimer.scheduleRepeating(REFRESH_CM_INTERVAL);*/
		return container;
	}
	
	private LayoutContainer createRow_3_Warning(){
		warningContainer = new LayoutContainer(){
			protected void onWindowResize(int width, int height) {
				northData.setSize(NORTH_HEIGHT+warningContainer.getHeight());
				UIContext.vSphereHomepagePanel.layout(true);
			}
		};
		warningContainer.setMonitorWindowResize(true);
		warningContainer.setWindowResizeDelay(110);
		TableLayout tableLayout = new TableLayout();
		tableLayout.setWidth("100%");
		warningContainer.setLayout(tableLayout);
		return warningContainer;
	}
	
	private void refreshWarning(){
		service.getVMStatusModel(UIContext.backupVM, new BaseAsyncCallback<VMStatusModel[]>(){
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
			}

			@Override
			public void onSuccess(VMStatusModel[] result) {
				warningContainer.removeAll();
				northData.setSize(NORTH_HEIGHT);
				if(result !=null && result.length >0){
					TableLayout tableLayout = new TableLayout(2);
					tableLayout.setCellVerticalAlign(VerticalAlignment.MIDDLE);
					tableLayout.setWidth("100%");
					LayoutContainer container = new LayoutContainer();
					//container.setHeight(WARNING_HEIGHT);
					container.setStyleAttribute("background-color", "#FFFF99");
					container.setLayout(tableLayout);
					for(VMStatusModel status :result){
						String statusMessage = Utils.getStatusMessage(status.getStatusType(), status.getSubType(), status.getStatus(), status.getStatusParamter()); 
						//NORTH_HEIGHT = NORTH_HEIGHT+WARNING_HEIGHT;
						TableData tb = new TableData();
						tb.setHeight("25");
						Image image = getStatusImage(status.getStatusType());
						container.add(image,tb);
						
						Label text = new Label(statusMessage);
						text.setStyleName("homepage_header_warning_label");
						//text.setStyleName("homepage_header_user_label");
						container.add(text,tb);
						
					}
					warningContainer.add(container);
					warningContainer.layout();
					northData.setSize(NORTH_HEIGHT+warningContainer.getHeight());
				}
				layout(true);
			}
		});
	}
	
	public void refreshWarningUI(){
		northData.setSize(NORTH_HEIGHT+warningContainer.getHeight());
		layout(true);
	}
	
	private Image getStatusImage(int statusType){
		if(statusType == VMStatusModel.VM_STATUS_TYPE_ERROR){
			return AbstractImagePrototype.create(UIContext.IconBundle.status_small_error()).createImage();
		}else{
			return AbstractImagePrototype.create(UIContext.IconBundle.status_small_warning()).createImage();
		}
	}
	
	private LayoutContainer createLeftPanelWithResizing() {
		
		LayoutContainer container = new LayoutContainer();
		
		container.setScrollMode(Scroll.AUTOY);
		
		RowLayout rowLayout = new RowLayout(Orientation.VERTICAL);
		rowLayout.setAdjustForScroll(true);

		container.setLayout(rowLayout);		
		
		container.add(monitorPanel, new RowData(1, -1, new Margins(2, 0, 0, 0)));
//		container.add(mergePanel, new RowData(1, -1, new Margins(4, 0, 0, 0)));
		container.add(summaryPanel, new RowData(1, -1, new Margins(4, 0, 0, 0)));		
		container.add(protectionInformationPanel, new RowData(1, -1, new Margins(4, 0, 0, 0)));	
		
		container.add(backupHistoryPanel, new RowData(1, -1, new Margins(4, 0, 0, 0)));
		
		return container;
	}

	@Override
	public void refresh(Object data) {
		GWT.log("VSphereHomepage Refresh......", null);
		service.getVMBackupInforamtionSummaryWithLicInfo(UIContext.backupVM,new BaseAsyncCallback<BackupInformationSummaryModel>(){
			
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
			}

			@Override
			public void onSuccess(BackupInformationSummaryModel result) {
				GWT.log("get Summary model successfully", null);
				//statusPieChartPanel.refresh(result);
				summaryPanel.refresh(result);
			}

		});
		//refreshTrustHost();
		backupHistoryPanel.refresh(null);
		protectionInformationPanel.refresh(null);
		monitorPanel.refresh(null);
	}

	@Override
	public void refresh(Object data, int changeSource) {

	}

}
