package com.ca.arcflash.ui.client.coldstandby;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ca.arcflash.ha.model.ARCFlashNode;
import com.ca.arcflash.ha.model.ProductionServerRoot;
import com.ca.arcflash.ha.model.SummaryModel;
import com.ca.arcflash.ha.model.VMSnapshotsInfo;
import com.ca.arcflash.jobscript.replication.VMStorage;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.coldstandby.event.ReplicationJobFinishedEvent;
import com.ca.arcflash.ui.client.coldstandby.event.ReplicationJobFinishedEventHandler;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.LoadingStatus;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.homepage.SummaryPanel;
import com.ca.arcflash.ui.client.model.BackupStatusModel;
import com.ca.arcflash.ui.client.monitor.MonitorService;
import com.ca.arcflash.ui.client.monitor.MonitorServiceAsync;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class VirtualConversionSummaryPanel extends LayoutContainer {

	private final ColdStandbyServiceAsync coldStandByService = GWT.create(ColdStandbyService.class);
	private final MonitorServiceAsync monitorService = GWT.create(MonitorService.class);
	private BaseAsyncCallback<SummaryModel> callback;
//	private Label legendBackupText;
//	private Label legendOthersText;
//	private Label legendFreeText;
//	private HTML destinationHtml;
	private Image totalStatusImage;
//	private HTML destinationDestHtml;
	private LayoutContainer destinationChartContainer;
	private SummaryItemPanel mostRecentColdStandby;
	private SummaryItemPanel destinationStatus;
	private SummaryItemPanel provisionPoints;
	private SummaryItemPanel licensePanel;
	private ContentPanel contentPanel;
	private LayoutContainer legendContainer;
	private LayoutContainer contentTable;
//	private boolean moniteeConnectedAfterNodeSelected;
	private boolean summaryPanelLoad;
	private SummaryType totalSummaryType;
	private Timer loadingTimer = null;
	
	private Label legendBackupText;
	private Label legendOthersText;
	private Label legendFreeText;
	
	public VirtualConversionSummaryPanel() {
		
		legendBackupText = new Label();
		legendOthersText = new Label();
		legendFreeText = new Label();
		
		contentPanel = new ContentPanel();
		contentPanel.setCollapsible(true);
		contentPanel.ensureDebugId("fp4owbcb-3hv8-9qx6-wm31-xkmvp1kxph82");
		contentPanel.setBodyStyle("background-color: white; padding: 6px;"); //border: 1px solid #F00;
	    contentPanel.setHeadingHtml(VCMMessages.virtualConversionSummary(UIContext.Constants.NA()));
//	    contentPanel.setAutoWidth(true);
//	    contentPanel.setAutoHeight(true);
//	    contentPanel.setWidth("100%");
	    
	    contentTable = new LayoutContainer();
//	    contentTable.setStyleAttribute("padding", "6px");
	    contentTable.setWidth("100%");
//	    contentTable.setStyleAttribute("border", "1px solid #00F");
	    contentTable.setAutoWidth(true);
	    contentTable.setAutoHeight(true);
	    
	    TableLayout tLayout = new TableLayout();
	    tLayout.setWidth("100%");
	    tLayout.setColumns(3);
	    
	    contentTable.setLayout(tLayout);
	    
	    AbstractImagePrototype totalStatusImagePrototype = IconHelper.create(SummaryPanel.ICON_LARGE_FINISH_URL, 64,64);
		totalStatusImage = totalStatusImagePrototype.createImage();
		TableData data = new TableData();
		data.setWidth("12%");
		data.setVerticalAlign(VerticalAlignment.TOP);
		data.setHorizontalAlign(HorizontalAlignment.CENTER);
		data.setRowspan(5);
		contentTable.add(totalStatusImage, data);
	    
	    mostRecentColdStandby = new SummaryItemPanel();
	    mostRecentColdStandby.setTitle(VCMMessages.virtualConversionMostRecent());
	    mostRecentColdStandby.setDescription(new String[]{UIContext.Constants.NA()});
	    mostRecentColdStandby.setType(SummaryType.Information);
	    data = new TableData();
	    data.setVerticalAlign(VerticalAlignment.TOP);
	    data.setWidth("44%");
	    contentTable.add(mostRecentColdStandby);
	    
	    destinationStatus = new SummaryItemPanel();
	    destinationStatus.setTitle(UIContext.Constants.virtualConversionDesctinationStatus());
	    destinationStatus.setDescription(new String[]{UIContext.Constants.NA()});
	    destinationStatus.setType(SummaryType.Information);
	    
	    data = new TableData();
	    data.setRowspan(3);
	    data.setWidth("44%");
	    data.setVerticalAlign(VerticalAlignment.TOP);
	    contentTable.add(destinationStatus, data);
	    
	    provisionPoints = new SummaryItemPanel();
	    provisionPoints.setTitle(UIContext.Constants.provistionPointName());
	    provisionPoints.setDescription(new String[]{UIContext.Constants.NA()});
	    provisionPoints.setType(SummaryType.Warning);
	    data = new TableData();
	    data.setVerticalAlign(VerticalAlignment.TOP);
	    data.setWidth("44%");
	    contentTable.add(provisionPoints, data);
	    
	    licensePanel = new SummaryItemPanel();
	    licensePanel.setTitle(UIContext.Constants.homepageSummaryLicLabel());
	    licensePanel.setDescription(new String[]{UIContext.Constants.NA()});
	    licensePanel.setType(SummaryType.Error);
	    data = new TableData();
	    data.setVerticalAlign(VerticalAlignment.TOP);
	    data.setWidth("44%");
	    contentTable.add(licensePanel, data);
	    licensePanel.hide();
	    
	    data = new TableData();
	    data.setStyleName("virtual_conver_summary_charts_panel");
	    data.setWidth("88%");
	    data.setColspan(2);
	    contentTable.add(createDestinationChart(), data);
	    
	    data = new TableData();
	    data.setColspan(2);
	    data.setWidth("88%");
	    contentTable.add(createDestinationLengend(), data);
	    
	    contentPanel.setLayout(new FitLayout());
	    contentPanel.add(contentTable);
	    add(contentPanel);

		callback = new BaseAsyncCallback<SummaryModel>(){

			@Override
			public void onFailure(Throwable caught) {
				summaryPanelLoad = true;
				ColdStandbyManager.getInstance().getVCNavigator().unlockNavigator();
				updateUI(new SummaryModel());
				ColdStandbyManager.getInstance().getHomepage().getProvisionPanel().update(new ArrayList<VMSnapshotsInfo>());
			}

			@Override
			public void onSuccess(SummaryModel result) {
				summaryPanelLoad = true;
				ColdStandbyManager.getInstance().getVCNavigator().unlockNavigator();
				boolean isDataStoreShow = legendContainer.isVisible();
				updateUI(result);
				final List<VMSnapshotsInfo> snapshotsList = result == null ? new ArrayList<VMSnapshotsInfo>() : result.getSnapshots();
				ColdStandbyManager.getInstance().getHomepage().getProvisionPanel().update(snapshotsList);
				if(!isDataStoreShow && legendContainer.isVisible()) {
					ColdStandbyManager.getInstance().getHomepage().removeNonResizablePanels();
					ColdStandbyManager.getInstance().getHomepage().recreateNonResizablePanels();
				}
			}
		};
	}
	
	public void render(Element target, int index) {
		super.render(target, index);
		
	    ColdStandbyManager.getInstance().registerEventHandler(new ReplicationJobFinishedEventHandler(){

			@Override
			public void onJobFinished(ReplicationJobFinishedEvent event) {
				update();
			}
	    	
	    });
	}
	
	public void update() {
		summaryPanelLoad = false;
		
		if(!isRendered()) {
//			System.out.println("not rendered");
			loadingTimer = new Timer() {
				public void run() {
//					System.out.println("running");
					if(isRendered()) {
//						System.out.println("Rendered");
						if(!summaryPanelLoad) {
//							System.out.println("mask");
							contentTable.mask(UIContext.Constants.loadingIndicatorText());
							loadingTimer.cancel();
						}
						else {
//							System.out.println("cancel");
							loadingTimer.cancel();
						}
					}
				}
			};
			loadingTimer.scheduleRepeating(500);
		}
		else {
			contentTable.mask(UIContext.Constants.loadingIndicatorText());
		}
		
		ColdStandbyManager.getInstance().getHomepage().getProvisionPanel().clearProvisionPoints();
		
		if(!ColdStandbyManager.getInstance().getVCNavigator().isSelectMoniteeFromMonitor() 
				&& ColdStandbyManager.getInstance().getVcmStatus() != null 
				&& !ColdStandbyManager.getInstance().getVcmStatus().isVcmConfigured()) {
//			callback.onSuccess(new SummaryModel());
			return;
		}
		
//		  contentPanel.collapse();
//		if(!ColdStandbyManager.getInstance().getVCNavigator().isSelectedServerAccessible()) {
		if(ColdStandbyManager.getInstance().getVCNavigator().isSelectMoniteeFromMonitor()) {
			loadFromVCMServer(ColdStandbyManager.getInstance().getVCNavigator().getSelectServerNode());
		}
		else
			loadFromProductionServer();
	}

	public void setMoniteeConnectedAfterSelectNode(boolean moniteeConnect) {
		if(!moniteeConnect)
			summaryPanelLoad = moniteeConnect;
		
//		moniteeConnectedAfterNodeSelected = moniteeConnect;
//		if(moniteeConnect && summaryPanelLoad) {
//			updateLicensePanelFromMonitee();
//		}
	}
	
	private void loadFromVCMServer(ARCFlashNode currentNode) {
	
		monitorService.getSummaryModel(currentNode.getUuid(), "", "", callback);
	}

	private void loadFromProductionServer() {
		//vmUUID == null if logging in proxy server; otherwise, the vm  
		String vmInstanceUUID = ColdStandbyManager.getVMInstanceUUID();
		coldStandByService.getProductionServerSummaryModel(vmInstanceUUID, callback);
	}

	private Widget createDestinationChart() {
		
		destinationChartContainer = new LayoutContainer();
		TableLayout layout = new TableLayout();
		layout.setWidth("100%");
		destinationChartContainer.setLayout(layout);
		
		return destinationChartContainer;
	}
	
	private Widget createDestinationLengend() {
		TableLayout layout = new TableLayout();
		layout.setColumns(6);
		
		legendContainer = new LayoutContainer();
		legendContainer.setLayout(layout);
		legendContainer.setStyleAttribute("padding", "2px");
		
		AbstractImagePrototype imagePrototype = IconHelper.create("images/legend_incremental.png", 16,16);
		Image backupLegendImage = imagePrototype.createImage();
	    legendContainer.add(backupLegendImage);
	    
	    legendBackupText.setText(UIContext.Constants.virtualStandyNameTranslate());
	    legendBackupText.setStyleName("homepage_summary_legengLabel");
	    legendContainer.add(legendBackupText);
	    
	    Image othersLegendImage = imagePrototype.createImage();
	    othersLegendImage.setUrl("images/legend_others.png");
	    legendContainer.add(othersLegendImage);
	    
	    legendOthersText.setText(UIContext.Constants.virtualConversionSummaryDestOthers());
	    legendOthersText.setStyleName("homepage_summary_legengLabel");
	    legendContainer.add(legendOthersText);
	    
	    Image freeLegendImage = imagePrototype.createImage();
	    freeLegendImage.setUrl("images/legend_freeSpace.png");
	    legendContainer.add(freeLegendImage);
	    
	    legendFreeText.setText(UIContext.Constants.virtualConversionSummaryDestFree());
	    legendFreeText.setStyleName("homepage_summary_legengLabel");
	    legendContainer.add(legendFreeText);
	    
	    legendContainer.setVisible(false);
	    
	    return legendContainer;
	}
	
	public void updateDestinationChart(SummaryModel result){
		
		destinationChartContainer.removeAll();
		
		if(result == null || result.getStorages() == null || result.getStorages().size() == 0) {
			legendContainer.setVisible(false);
			return;
		}
		
		List<VMStorage> vmStorageList = result.getStorages();
		
		legendContainer.setVisible(true);
		for(VMStorage storage : vmStorageList) {
			long vcSpace = storage.getColdStandySize();
			long freeSpace = storage.getFreeSize();
			long othersSpace = storage.getOtherSize();
	
			long total = storage.getTotalSize();
	
			double backup = ((double) vcSpace) / total;
			double free = ((double) freeSpace) / total;
			double others = ((double) othersSpace) / total;
	
			int backupPercent = (int) (backup * 100);
			int freePercent = (int) (free * 100);
			int othersPercent = (int) (others * 100);
			int decrease = 0;
			if(free > 0 && freePercent < 1) {
				decrease++;
				freePercent = 1;
			}
			if(backup > 0 && backupPercent < 1) {
				decrease++;
				backupPercent = 1;
			}
			if(others > 0 && othersPercent < 1) {
				decrease++;
				othersPercent = 1;
			}
			
			if(decrease > 0) {
				if(backupPercent >= freePercent)
				{
					if(backupPercent >= othersPercent)
						backupPercent -= decrease;
					else
						othersPercent -= decrease;
				}
				else if(freePercent >= othersPercent) {
					freePercent -= decrease;
				}
				else
					othersPercent -= decrease;
			}
			String vcSizeStr = Utils.bytes2String(vcSpace);
			String otherSizeStr = Utils.bytes2String(othersSpace);
			String freeSizeStr = Utils.bytes2String(freeSpace);
			
			String vcTitle = VCMMessages.productName + "  " + vcSizeStr;
			String othersTitle = UIContext.Messages.homepageSummaryLegendOthers(otherSizeStr);
			String freeTitle = UIContext.Messages.homepageSummaryLegendFree(freeSizeStr);
	
			legendFreeText.setText(UIContext.Messages.homepageSummaryLegendFree(Utils.bytes2GBString(freeSpace)));
			legendBackupText.setText(UIContext.Messages.virtualStandbySize(Utils.bytes2GBString(vcSpace)));
			legendOthersText.setText(UIContext.Messages.homepageSummaryLegendOthers(Utils.bytes2GBString(othersSpace)));
			
			HTML destinationHtml = new HTML();
//			HTML destinationDestHtml = new HTML();
//			TableData tableData = new TableData();
			
//			tableData.setHorizontalAlign(HorizontalAlignment.CENTER);
//			destinationChartContainer.add(destinationHtml);
			//		tableData = new TableData();
			//		tableData.setHorizontalAlign(HorizontalAlignment.CENTER);
//			destinationChartContainer.add(destinationDestHtml);
			
			StringBuffer buffer = new StringBuffer();
			buffer.append("<table class=\"virtual_conver_summary_table\" CELLPADDING=0 CELLSPACING=0>");
			StringBuffer storeTitle = new StringBuffer("<tr style=\"text-align: left;\"><td colspan=\"3\" class=\"virtual_conver_summary_chart_words\">");
			String name = getShowName(result.isHyperVModel(), storage.getName());
			String destTitle = "";
			if(result.isHyperVModel())
				destTitle = UIContext.Messages.homepageSummaryDestinationVolume(name);
			else
				destTitle = UIContext.Messages.virtualConversionSummaryDestTitle(name);
			
			storeTitle.append(destTitle)
						.append("</td></tr>");
			StringBuffer chartBuffer = new StringBuffer("<tr class=\"virtual_conver_summary_charts\">");
//			StringBuffer hintBuffer = new StringBuffer("<tr class=\"virtual_conver_summary_chart_words\">");
	
			if (total > 0) {
				if (backupPercent > 0)
					appendTDChart(chartBuffer, /*hintBuffer,*/ backupPercent, "images/legend_incremental.png", vcTitle, vcSizeStr, "#2178bb");//08497a
				if (othersPercent > 0)
					appendTDChart(chartBuffer, /*hintBuffer,*/ othersPercent, "images/legend_others.png", othersTitle, otherSizeStr, "#f9b144"); //b5802c
				appendTDChart(chartBuffer, /*hintBuffer,*/ freePercent, "images/legend_freeSpace.png", freeTitle, freeSizeStr, "#727d83");
			} else {
				chartBuffer.append("<td/>");
//				hintBuffer.append("<td/>");
			}
			
			chartBuffer.append("</tr>");
//			hintBuffer.append("</tr>");
			
			String destCharts = buffer.toString()+storeTitle+chartBuffer.append("</table>");
			destinationHtml.setHTML(destCharts);
//			String destLabels = buffer.toString()+hintBuffer.append("</table>");
//			destinationDestHtml.setHTML(destLabels);
			
//			TableData tableData = new TableData();
//			tableData.setHorizontalAlign(HorizontalAlignment.CENTER);
			destinationChartContainer.add(destinationHtml);
//			//		tableData = new TableData();
//			//		tableData.setHorizontalAlign(HorizontalAlignment.CENTER);
//			destinationChartContainer.add(destinationDestHtml);
			destinationChartContainer.layout();
		}
	}
	
	private void appendTDChart(StringBuffer buffer, /*StringBuffer hintBuffer,*/ int percent, String image, String title, String sizeStr,  String color){
		buffer.append("<td ");
		buffer.append(" title=\"");
		buffer.append(title);
		buffer.append("\" width=\"");
		buffer.append(percent);
		buffer.append("%\" style=\"background-image: url(./");
		buffer.append(image);
		buffer.append(");\"/>");
		
//		hintBuffer.append("<td ");
//		hintBuffer.append("width=\"");
//		hintBuffer.append(percent);
//		hintBuffer.append("%\" style=\"overflow:hidden; white-space: nowrap; text-align: center; color:").append(color).append(";\">");
//		hintBuffer.append(sizeStr);
//		hintBuffer.append("</td>");
	}
	
	class SummaryItemPanel extends LayoutContainer{
		private FlexTable contentTable = new FlexTable();
		private Text titleLabel = new Text();
		private VerticalPanel descVerticalPane = new VerticalPanel();
		private Image image; 
		
		public SummaryItemPanel(){
//			contentTable.setCellPadding(2);
//			contentTable.setCellSpacing(2);
			AbstractImagePrototype imagePrototype = IconHelper.create(SummaryPanel.ICON_FINISH_URL, 32,32);
			image = imagePrototype.createImage();
			contentTable.setWidget(0, 0, image);
			contentTable.getFlexCellFormatter().setRowSpan(0, 0, 2);
		    contentTable.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
		    
		    contentTable.setWidget(0, 1, titleLabel);
		    
		    contentTable.setWidget(1, 0, descVerticalPane);
		    
		    titleLabel.setStyleName("homepage_summary_label");
		    
		    add(contentTable);
		}
		
		public void setTitle(String title){
			titleLabel.setText(title);
		}
		
		public void setDescription(String[] description){
			descVerticalPane.clear();
			for (int i = 0; i < description.length; i++) {
				Text descLabel = new Text();
				descLabel.setStyleName("homepage_summary_description");
				descLabel.setText(description[i]);
				
				descVerticalPane.add(descLabel);
			}
		}
		
		public void setType(SummaryType type){
				image.setUrl(getIconURL(type));
		}
		
		public void setLoadingImage() {
			image.setUrl(LoadingStatus.ICON_LOADING);
		}
		
//		public void render(Element target, int index) {
//			super.render(target, index);
//			
//		}
	}
	
	public String getIconURL(SummaryType type){
		if (type == SummaryType.Information) {
			return SummaryPanel.ICON_FINISH_URL;
		}
		else if(type == SummaryType.Warning) {
			return SummaryPanel.ICON_WARNING_URL;
		}
		else {
			return SummaryPanel.ICON_ERROR_URL;
		}
	}
	
	public String getLargeIconURL(SummaryType type){
		if (type == SummaryType.Information) {
			return SummaryPanel.ICON_LARGE_FINISH_URL;
		}
		else if(type == SummaryType.Warning) {
			return SummaryPanel.ICON_LARGE_WARNING_URL;
		}
		else {
			return SummaryPanel.ICON_LARGE_ERROR_URL;
		}
	}
	
	private void updateUI(SummaryModel result) {
		contentTable.unmask();
//		 contentPanel.expand();
		String recentDesc = UIContext.Constants.NA();
		SummaryType recentType = SummaryType.Error;
		String proviDesc = UIContext.Constants.NA();
		SummaryType proviType = SummaryType.Error;
		String[] destDescs = new String[] {UIContext.Constants.NA()};
		SummaryType destType = SummaryType.Error;
		
		String serverName = UIContext.Constants.NA();
		ARCFlashNode selectedServerNode = ColdStandbyManager.getInstance().getVCNavigator().getSelectServerNode();
		if(selectedServerNode != null)
			serverName = selectedServerNode.getHostname();
		
		if(result != null) {
			ProductionServerRoot serverRoot = result.getServerRoot();
			
			if(serverRoot != null) {
//				if(serverRoot.getProductionServerHostname() != null) {
//					serverName = serverRoot.getProductionServerHostname();
//				}
				if(serverRoot.getReplicaRoot() != null 
						&&  serverRoot.getReplicaRoot().getReplicaTime() > serverRoot.getMostRecentRepTimeMilli()) {
					long repliTime = serverRoot.getReplicaRoot().getReplicaTime();
					if(repliTime > 0 && result.getSnapshots() != null && result.getSnapshots().size() > 0) {
						Date date = new Date(repliTime);
						recentDesc =  Utils.formatDateToServerTime(date);
						recentType = SummaryType.Information;
					}
				}
				else if(serverRoot.getMostRecentRepTimeMilli() > 0){
					recentDesc = serverRoot.getMostRecentRepTime();
					int status = serverRoot.getMostRecentRepStatus();
					if(status == BackupStatusModel.Finished) {
						recentType = SummaryType.Information;
					}
					else if(status == BackupStatusModel.Canceled) {
						recentType = SummaryType.Warning;
					}
					else 
						recentType = SummaryType.Error;
				}
			}
			
			List<VMSnapshotsInfo> snapShotList = result.getSnapshots();
			if(snapShotList != null) {
				int size = snapShotList.size();
				String totalSizeStr = UIContext.Constants.NA();
				if(serverRoot != null)
					totalSizeStr = serverRoot.getRetentionCount() + "";
					
				proviDesc = UIContext.Messages.virtualConversionSummaryAvailablePoints(size, totalSizeStr);
				
				if(size <= 0)
					proviType = SummaryType.Error;
				else if(serverRoot == null || size < serverRoot.getRetentionCount())
					proviType = SummaryType.Warning;
				else
					proviType = SummaryType.Information;
			}
			
			List<VMStorage> vmStorageList = result.getStorages();
			if(vmStorageList != null && vmStorageList.size() > 0) {
				destType = SummaryType.Information;
				List<String> descList = new ArrayList<String>();
				for (VMStorage storage : vmStorageList) {
					long freeSize = storage.getFreeSize();
					if(freeSize == 0)
						destType = SummaryType.Warning;
					
					String name = storage.getName();
					name = getShowName(result.isHyperVModel(), name);
					descList.add(UIContext.Messages.virtualConversionSummaryDestFreeSpace(name, Utils.bytes2String(freeSize)));
				}
				destDescs = descList.toArray(new String[0]);
			}
		}
			
		contentPanel.setHeadingHtml(VCMMessages.virtualConversionSummary(serverName));
		mostRecentColdStandby.setDescription(new String[] {recentDesc});
		mostRecentColdStandby.setType(recentType);
		
		provisionPoints.setDescription(new String[] {proviDesc});
		provisionPoints.setType(proviType);
		
		destinationStatus.setDescription(destDescs);
		destinationStatus.setType(destType);
		
		totalSummaryType = SummaryType.Information;
		if(recentType == SummaryType.Error || proviType == SummaryType.Error || destType == SummaryType.Error)
			totalSummaryType = SummaryType.Error;
		else if(recentType == SummaryType.Warning || proviType == SummaryType.Warning 
				|| destType == SummaryType.Warning)
			totalSummaryType = SummaryType.Warning;
		
		totalStatusImage.setUrl(getLargeIconURL(totalSummaryType));
		
//		if(result != null && !ColdStandbyManager.getInstance().getVCNavigator().isSelectMoniteeFromMonitor()) {
//			EdgeLicenseInfo licenseInfo = result.getLicenseInfo();
//			updateLicensePanel(licenseInfo);
//		}
//		else {
//			if(!moniteeConnectedAfterNodeSelected) {
//				licensePanel.show();
//				licensePanel.setLoadingImage();
//				licensePanel.setTitle(UIContext.Constants.coldStandbySettingCheckingLicense());
//				licensePanel.setDescription(new String[] {UIContext.Constants.NA()});
//			}
//			else
//				updateLicensePanelFromMonitee();
//		}
		
		updateDestinationChart(result);
	}

//	public void updateLicensePanelFromMonitee() {
//		coldStandByService.getConversionLicense(ColdStandbyManager.getVMInstanceUUID(), new BaseAsyncCallback<EdgeLicenseInfo>() {
//			@Override
//			public void onFailure(Throwable caught) {
//				if(caught instanceof BusinessLogicException){
//					BusinessLogicException exception = (BusinessLogicException)caught;
//					//failed to connect to the monitee machine
//					if(exception.getErrorCode().equals("38654705665")){
//						return;
//					}
//				}
//				
//				updateLicensePanel(null);
//			}
//			
//			@Override
//			public void onSuccess(EdgeLicenseInfo licInfo) {
//				updateLicensePanel(licInfo);
//			}
//		});		
//	}

//	void updateLicensePanel(EdgeLicenseInfo licenseInfo){
//		licensePanel.setTitle(UIContext.Constants.homepageSummaryLicLabel());
//		SummaryType licenseType = SummaryType.Information;
//		String licenseDesc = UIContext.Constants.NA();
//		boolean showLicensePanel = false;
//		if(licenseInfo != null) {
//			licenseDesc = getLicensStr(licenseInfo, EdgeLicenseInfo.LICENSE_ERR);
//			if(licenseDesc != null && licenseDesc.length() > 0) {
//				licenseType = SummaryType.Error;
//				showLicensePanel = true;
//			}
//			else {
//				licenseDesc = getLicensStr(licenseInfo, EdgeLicenseInfo.LICENSE_WAR);
//				if(licenseDesc != null && licenseDesc.length() > 0) {
//					licenseType = SummaryType.Warning;
//					showLicensePanel = true;
//				}
//			}
//		}
//		else {
//			licenseType = SummaryType.Error;
//		}
//		
//		if (licenseType == SummaryType.Information || !showLicensePanel) {
//			licensePanel.hide();
//		} else {
//			licensePanel.setType(licenseType);
//			String fullDesc = UIContext.Messages.homepageSummaryLicenseFailurefor(licenseDesc);
//			licensePanel.setDescription(new String[] { fullDesc });
//			licensePanel.show();
//		}
//		
//		if (totalSummaryType == SummaryType.Error || (showLicensePanel && licenseType == SummaryType.Error)) {
//			totalSummaryType = SummaryType.Error;
//		} else if (totalSummaryType == SummaryType.Warning || (showLicensePanel && licenseType == SummaryType.Warning)) {
//			totalSummaryType = SummaryType.Warning;
//		}
//		
//		totalStatusImage.setUrl(getLargeIconURL(totalSummaryType));
//	}
	private String getShowName(boolean hyperVModel, String name) {
		if(hyperVModel && name != null && name.length() > 1 && name.charAt(1) == ':') {
			return name.charAt(0) + ":\\";
		}
			
		return name;
	}

//	private String getLicensStr(EdgeLicenseInfo edgeLicenseInfo, int licenseErr) {
//		StringBuilder sb = new StringBuilder();
//		if(edgeLicenseInfo == null ||edgeLicenseInfo.getPhysicalMachineLicense() == null ||  edgeLicenseInfo.getPhysicalMachineLicense() == licenseErr)
//		{
//			sb.append(UIContext.Constants.virtualConversionPhysicalMachineLicense());
//		} 
//		else if(edgeLicenseInfo.getPhysicalMachineLicense() != EdgeLicenseInfo.LICENSE_UNKNOWN)
//			return null;
//		
//		if(edgeLicenseInfo == null ||edgeLicenseInfo.getVSphereVMLicense() == null ||  edgeLicenseInfo.getVSphereVMLicense() == licenseErr) {
//			if(sb.length() > 0)
//				sb.append(", ");
//			sb.append(UIContext.Constants.virtualConversionvSphereVMLicense());
//		}
//		else if(edgeLicenseInfo.getVSphereVMLicense() != EdgeLicenseInfo.LICENSE_UNKNOWN)
//			return null;
//		
//		if(edgeLicenseInfo == null ||edgeLicenseInfo.getHyperVLicense() == null ||  edgeLicenseInfo.getHyperVLicense() == licenseErr) {
//			if(sb.length() > 0)
//				sb.append(", ");
//			sb.append(UIContext.Constants.virtualConversionHyperVVMLicense());
//		}
//		else if(edgeLicenseInfo.getHyperVLicense() != EdgeLicenseInfo.LICENSE_UNKNOWN)
//			return null;
//		
////		return sb.toString();
//		//only returns a general license error, not concrete licenses
//		return UIContext.Constants.virtualStandyNameTranslate();
//	}

	enum SummaryType{
		Information, Warning, Error 
	}

}
