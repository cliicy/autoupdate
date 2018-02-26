package com.ca.arcflash.ui.client.vsphere.homepage;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.AdvancedSettings;
import com.ca.arcflash.ui.client.backup.schedule.EveryDayScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.EveryMonthScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.EveryWeekScheduleModel;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.IRefreshable;
import com.ca.arcflash.ui.client.common.LoadingStatus;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.homepage.BaseSummaryPanel;
import com.ca.arcflash.ui.client.homepage.DataStoreDetailWindow;
import com.ca.arcflash.ui.client.model.BackupInformationSummaryModel;
import com.ca.arcflash.ui.client.model.DataStoreInfoModel;
import com.ca.arcflash.ui.client.model.DataStorePolicyModel;
import com.ca.arcflash.ui.client.model.RecentBackupModel;
import com.ca.arcflash.ui.client.model.VMBackupSettingModel;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class VSphereSummaryPanel extends BaseSummaryPanel implements IRefreshable {
	
	//wanqi06
	private Text recoverySetLargerText;
	private Text recoverySetsDescriptionText;
	private Label recoverySetsDetailMessage;
	private ClickHandler BackupSetDetailHandler = null;	
	private LayoutContainer lcRestorePanel;
	private LayoutContainer lsBackupsetPanel;
	
	private Text recoveryPointsDescriptionText;
	private Text recoveryPointsRepeatDescriptionText;
	private Text recoveryPointsDailyDescriptionText;
	private Text recoveryPointsWeeklyDescriptionText;
	private Text recoveryPointsMonthlyDescriptionText;
	
	private Text recoveryPointsLargerText;
	private Image totalStatusImage;
	private ContentPanel panel;
	private Text destinationText;
	private LoadingStatus status;
	private int LeastBackupSize = 5;
	private ContentPanel licPanel;
	private boolean isAdvanced;
	
	private DataStorePolicyModel dataStorePolicyModel = new DataStorePolicyModel();
	
	private Text destinationHealthText;
	
	private boolean isDayShow =false;
	private boolean isWeekShow=false;
	private boolean isMonthShow= false;
	
	public static VSphereSummaryPanel gSummaryPanel = null;
	
	public void render(Element target, int index) {
		super.render(target, index);
		
		panel = new ContentPanel();
		panel.setCollapsible(true);
//		panel.collapse();
	    panel.setBodyStyle("background-color: white; padding: 6px;");
	    panel.setHeadingHtml(UIContext.Constants.homepageSummaryHeader());
	    
	    TableLayout layout = new TableLayout();
	    layout.setWidth("100%");
	    layout.setColumns(2);
	    panel.setLayout(layout);
	    
	  //fix 18901838
	    status = new LoadingStatus();
	  //fix RTC issue 106924
//		status.addto(panel, 2);	  
	    
	    AbstractImagePrototype totalStatusImagePrototype = IconHelper.create(ICON_LARGE_FINISH_URL, 64,64);
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
	    
	    //wanqi06
	    interLeftPanel = new ContentPanel();
	    interLeftPanel.setHeaderVisible(false);
	    interLeftPanel.setBorders(false);
	    interLeftPanel.setBodyBorder(false);
	    
	    imagePrototype = IconHelper.create(ICON_FINISH_URL, 32,32);
	    createSummaryBackupPart(interLeftPanel);
	    createSummaryRecoveryPointsPart(interLeftPanel);
	    //wanqi06
	    createSummaryRecoverySetsPart(interLeftPanel);
	    //
	    createSummaryDestinationPart(interLeftPanel);   
	    tableData = new TableData();
	    tableData.setWidth("50%");
		tableData.setVerticalAlign(Style.VerticalAlignment.TOP);
	    rightPanel.add(interLeftPanel,tableData);	        
	    
	    ContentPanel interRightPanel = new ContentPanel();
	    interRightPanel.setHeaderVisible(false);
	    interRightPanel.setBorders(false);
	    interRightPanel.setBodyBorder(false);	    
	   
	    createLicStatusPart(interRightPanel);  
	    tableData = new TableData();
		tableData.setVerticalAlign(Style.VerticalAlignment.TOP);
	    rightPanel.add(interRightPanel,tableData);
	    licPanel = interRightPanel;
	    licPanel.hide();
	    
		tableData = new TableData();
		tableData.setColspan(2);
	    createDestinationChart(rightPanel, tableData);
	    createDestinationLengend(rightPanel, tableData);
	    
	    panel.add(rightPanel);
	    add(panel);
	    gSummaryPanel = this;
	    
	    beforeLoadBackupSummary();
    	service.getVMBackupInforamtionSummaryWithLicInfo(UIContext.backupVM,new BaseAsyncCallback<BackupInformationSummaryModel>(){
    		
			@Override
			public void onFailure(Throwable caught) {
				afterLoadBackupSummary();
				super.onFailure(caught);
			}

			@Override
			public void onSuccess(BackupInformationSummaryModel result) {
					
				status.hideIndicator();				
				refresh(result);
			}

		});
	}
	
	private Image licImage;
	private Text inProgressRecoverySet;
	private LayoutContainer lcDestinationPanel;
	private LoadingStatus loadDestCapacity;
	private Label dataStoreDetail;
	private Text licDescText ;
	private void createLicStatusPart(ContentPanel panel) {
		licImage = imagePrototype.createImage();
		licDescText = new Text(UIContext.Constants.NA());
		licDescText.ensureDebugId("F8C71F96-6C5B-47a6-A0A8-2399C38F741E");
		licDescText.setStyleName("homepage_summary_description");	
		//licDescText.setText("License Failure for: SQL Server, Exchange Server or something to that effect. We will need to make that the line can word-wrap it if gets tool long.");
		//addRecentBackup(panel,UIContext.Constants.homepageSummaryLicLabel(), licImage, licDescText);	
	}
	
	private void createSummaryRecoveryPointsPart(ContentPanel panel) {
		recoveryPointsImage = imagePrototype.createImage();
		recoveryPointsDescriptionText = new Text(UIContext.Constants.NA());
		recoveryPointsRepeatDescriptionText = new Text(UIContext.Constants.NA());
		recoveryPointsDailyDescriptionText = new Text(UIContext.Constants.NA());
		recoveryPointsWeeklyDescriptionText = new Text(UIContext.Constants.NA());
		recoveryPointsMonthlyDescriptionText = new Text(UIContext.Constants.NA());
		recoveryPointsDescriptionText.setStyleName("homepage_summary_description");
		recoveryPointsRepeatDescriptionText.setStyleName("homepage_summary_description");
		recoveryPointsDailyDescriptionText.setStyleName("homepage_summary_description");
		recoveryPointsWeeklyDescriptionText.setStyleName("homepage_summary_description");
		recoveryPointsMonthlyDescriptionText.setStyleName("homepage_summary_description");
		recoveryPointsDescriptionText.ensureDebugId("A491CBCA-3FE5-4209-B938-029AFDFBFA59");
		recoveryPointsLargerText = createRecoveryPointsMountedText();
		mergeDelayedText = createRecoveryPointsMountedText();
		lcRestorePanel = new LayoutContainer();
		
		startManualMergeText = new Label(UIContext.Constants.mergeStartManualMerge());
		startManualMergeText.setStyleName("homepage_Manual_Merge_Link");
		startManualMergeText.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				service.resumeMerge(UIContext.backupVM.getVmInstanceUUID(), 
						new BaseAsyncCallback<Integer>(){

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
					}

					@Override
					public void onSuccess(Integer result) {
						mergeDelayedText.hide();
						startManualMergeText.setVisible(false);
						Info.display(UIContext.Messages.messageBoxTitleError(Utils.getProductName()), 
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
	    addRecentBackup(lcRestorePanel, UIContext.Constants.homepageSummaryRecoveryPointsLabel(), 
	    		recoveryPointsImage, widgets, null);
	    startManualMergeText.setVisible(false);
	    lcRestorePanel.hide();
		
//	    addRecentBackup(lcRestorePanel, UIContext.Constants.homepageSummaryRecoveryPointsLabel(), recoveryPointsImage ,recoveryPointsDescriptionText, recoveryPointsLargerText, null);
	
	    lcRestorePanel.hide();
	}
	
	private void createSummaryRecoverySetsPart(ContentPanel panel) {
		
		BackupSetDetailHandler = new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {				
				VSphereBackupSetDetailWindow bsDetail = new VSphereBackupSetDetailWindow();
				bsDetail.setModal(true);
				bsDetail.show();
			}
			};
		
		recoverySetsImage = imagePrototype.createImage();
		recoverySetsDescriptionText = new Text(UIContext.Constants.NA());
		recoverySetsDescriptionText.setStyleName("homepage_summary_description");
		
		inProgressRecoverySet = new Text(UIContext.Constants.inProgressRecoverySet());
		inProgressRecoverySet.addStyleName("homepage_summary_description");
		this.inProgressRecoverySet.hide();
		
		recoverySetsDetailMessage = new Label();		
		recoverySetsDetailMessage.ensureDebugId("8CB61883-2D96-482c-AFD9-EEC5CF7564BB");
		recoverySetsDetailMessage.addClickHandler(BackupSetDetailHandler);
		recoverySetsDetailMessage.setStyleName("homepage_BackupSets_Description_Link");
		
		recoverySetLargerText = createRecoveryPointsMountedText();
		
		List<Widget> widgets = new ArrayList<Widget>();
		widgets.add(recoverySetsDescriptionText);
		widgets.add(inProgressRecoverySet);
		widgets.add(recoverySetsDetailMessage);
		widgets.add(recoverySetLargerText);
		
		lsBackupsetPanel = new LayoutContainer();
		
		addRecentBackup(lsBackupsetPanel, UIContext.Constants.homepageSummaryRecoverySetsLabel(), recoverySetsImage ,widgets, null);

		lsBackupsetPanel.hide();
	}
	
	private void createSummaryDestinationPart(ContentPanel panel) {
		destinationCapacityImage = imagePrototype.createImage();
		destinationCapacityDescriptionText = new Text(UIContext.Constants.NA());
		destinationCapacityDescriptionText.ensureDebugId("EDB6393D-26D4-46b9-896A-F5DFFDFA291E");
		destinationCapacityDescriptionText.setStyleName("homepage_summary_description");
		destinationText = new Text(UIContext.Constants.NA());
		destinationText.ensureDebugId("39AFB4C4-20B2-4bf1-9ACF-8B6764C52FFF");
		destinationText.setStyleName("homepage_summary_description");
		
		destinationHealthText = new Text(UIContext.Constants.NA());
		destinationHealthText.setStyleName("homepage_summary_description");
		
		lcDestinationPanel = new LayoutContainer();
		loadDestCapacity = new LoadingStatus();
//		loadDestCapacity.setStyleName("homepage_summary_description");
		dataStoreDetail = new Label(UIContext.Constants.homepageSummaryDestinationDataStoreDetail());
		dataStoreDetail.setStyleName("homepage_summary_hyperlink_label");
		//By design, hide the link always before new solution
		dataStoreDetail.setVisible(false);
		dataStoreDetail.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				service.getVMDataStoreStatus(UIContext.backupVM, dataStorePolicyModel.getName(), new BaseAsyncCallback<DataStoreInfoModel>() {
					public void onFailure(Throwable caught) {			
						super.onFailure(caught);
					}
					@Override
					public void onSuccess(DataStoreInfoModel result) {
						DataStoreDetailWindow.show(result, dataStorePolicyModel.getDisplayName());
					}
				});		
			}
		});	   
		
		List<Widget> widgets = new ArrayList<Widget>();
		widgets.add(destinationCapacityDescriptionText);
		widgets.add(destinationText);
		widgets.add(destinationHealthText);
		widgets.add(dataStoreDetail);
		
		addRecentBackup(lcDestinationPanel, UIContext.Constants.homepageSummaryDestinationCapacityLabel(), destinationCapacityImage, widgets, this.loadDestCapacity);
	}
	
	private void beforeLoadBackupSummary() {
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
		if (!isBackupSet) {
			gSummaryPanel.recoverySetsDescriptionText.setVisible(false);

			if (!isAdvanced) {
				gSummaryPanel.recoveryPointsDescriptionText.setVisible(true);
			} else {
				gSummaryPanel.recoveryPointsDescriptionText.setVisible(false);
				gSummaryPanel.recoveryPointsRepeatDescriptionText.setVisible(true);
				gSummaryPanel.recoveryPointsDailyDescriptionText.setVisible(isDayShow);
				gSummaryPanel.recoveryPointsWeeklyDescriptionText.setVisible(isWeekShow);
				gSummaryPanel.recoveryPointsMonthlyDescriptionText.setVisible(isMonthShow);
			}
		} else {
			gSummaryPanel.recoverySetsDescriptionText.setVisible(true);
			gSummaryPanel.recoveryPointsDescriptionText.setVisible(false);
			gSummaryPanel.recoveryPointsRepeatDescriptionText.setVisible(false);
			gSummaryPanel.recoveryPointsDailyDescriptionText.setVisible(false);
			gSummaryPanel.recoveryPointsWeeklyDescriptionText.setVisible(false);
			gSummaryPanel.recoveryPointsMonthlyDescriptionText.setVisible(false);
		}
			
		gSummaryPanel.backupDescriptionText.show();
		gSummaryPanel.loadDestCapacity.hideIndicator();
		gSummaryPanel.destinationCapacityDescriptionText.show();
		gSummaryPanel.destinationText.show();
	}
	
	@Override
	public void refresh(Object data) {
		final BackupInformationSummaryModel result = (BackupInformationSummaryModel)data;
		if (result == null){
			panel.collapse();
			return;
		}
		isAdvanced = result.isAdvanced();
		panel.expand();
		if (result!=null){
			isBackupSet = result.isBackupSet();
			if(isBackupSet){
				lsBackupsetPanel.show();
				lcRestorePanel.hide();
			}
			else {
				lcRestorePanel.show();
				lsBackupsetPanel.hide();
			}
			//full backup
			RecentBackupModel recentBackup = getRecentBackup(result);
			if (recentBackup!=null){
				backupImage.setUrl(convertStatusToImageURL(recentBackup.getStatus()));
				backupDescriptionLabel.setText(UIContext.Messages.homepageSummaryMostRecentBackupLabel(recentBackup.getName()));
				backupDescriptionText.setText(Utils.formatDateToServerTime(recentBackup.getTime(), recentBackup.getTimeZoneOffset()));
				backupImage.setTitle(UIContext.Messages.homepageSummaryMostBackupStatus(Utils.backupStatus2String(recentBackup.getStatus())));
			}else{
				String backupMsg = UIContext.Messages.homepageSummaryMostRecentBackupLabel(UIContext.Constants.remoteDeployAddServerNALabel());
				backupDescriptionLabel.setText(backupMsg);
				backupDescriptionText.setText(UIContext.Constants.NA());
				backupImage.setUrl(ICON_WARNING_URL);
				backupImage.setTitle(UIContext.Constants.homepageSummaryMostFullBackupNotRun());
			}
			
			//Recovery Points
			inProgressRecoverySet.hide();
			if(!isBackupSet) {
				refreshRecoveryPoints(result);
			}
			else {
				recoverySetLargerText.hide();
				recoverySetsDescriptionText.setText(UIContext.Messages.homepageSummaryRecoverySets(
						result.getRecoverySetCount(), result.getRetentionCount()));
				recoverySetsDetailMessage.setText(UIContext.Constants.recoverySetsClickHintClickLabel());
				if(result.getRecoverySetCount() <= 0) {
					recoverySetsImage.setUrl(ICON_ERROR_URL);
					recoverySetsImage.setTitle(UIContext.Constants.homepageSummaryRecoverySetError());
				}else if (result.getRecoverySetCount()>0 && result.getRecoverySetCount()<result.getRetentionCount()+1) {
					recoverySetsImage.setUrl(ICON_WARNING_URL);
					recoverySetsImage.setTitle(UIContext.Messages.homepageSummaryRecoverySetWarningTooltip(
							result.getRecoverySetCount(), result.getRetentionCount()));
				}else if (result.getRecoverySetCount()>=result.getRetentionCount()+1) {
					recoverySetsImage.setUrl(ICON_FINISH_URL);
					recoverySetsImage.setTitle(UIContext.Constants.homepageSummaryRecoverySetInfo());
					if(result.getRecoverySetCount() > result.getRetentionCount() + 1){
						refreshMountedSessions(result, recoverySetLargerText);
					}
				}
				
				if(result.getRecoverySetCount() > 0) {
					recoverySetsDescriptionText.setText(UIContext.Messages.homepageSummaryRecoverySets(
							result.getRecoverySetCount()-1, result.getRetentionCount()));
					this.inProgressRecoverySet.show();
				}else {
					recoverySetsDescriptionText.setText(UIContext.Messages.homepageSummaryRecoverySets(
							0, result.getRetentionCount()));
				}
				afterLoadBackupSummary();
			}				
			
			if (result.getRpsHostModel() != null) {
				destinationCapacityDescriptionText.setText(UIContext.Messages.homepageSummaryRPSServerName(result.getRpsHostModel().getHostName()));
				if (result.getRpsPolicy4D2D() != null){
					destinationText.setText(UIContext.Messages.homepageSummaryRPSDataStoreName(result.getRpsPolicy4D2D().getDataStoreDisplayName()));
//					dataStoreDetail.setVisible(true);
					this.dataStorePolicyModel.setName(result.getRpsPolicy4D2D().getDataStoreName());
					this.dataStorePolicyModel.setDisplayName(result.getRpsPolicy4D2D().getDataStoreDisplayName());
				}
				//lcLegendContainer.hide();
				
				String iconTooltip = "";
				if (result.getDSRunningState() == 1) {
		            if (result.getDsHealth().equalsIgnoreCase("GREEN")){
		            	destinationCapacityImage.setUrl(ICON_FINISH_URL);
		            	iconTooltip = UIContext.Messages.homepageSummaryDestinationDataStoreInfoTooltip();
					} else if (result.getDsHealth().equalsIgnoreCase("YELLOW")){
		            	destinationCapacityImage.setUrl(ICON_WARNING_URL);
		            	iconTooltip = UIContext.Messages.homepageSummaryDestinationDataStoreWarnTooltip();
					} else if (result.getDsHealth().equalsIgnoreCase("RED")){
		            	destinationCapacityImage.setUrl(ICON_ERROR_URL);
		            	iconTooltip = UIContext.Messages.homepageSummaryDestinationDataStoreErrorTooltip();
					} else{
		            	destinationCapacityImage.setUrl(ICON_WARNING_URL);
		            	iconTooltip = UIContext.Messages.homepageSummaryDestinationDataStoreUnknownTooltip();
		            	destinationHealthText.setText(iconTooltip);
			            destinationHealthText.show();
					}
				}else{
					destinationCapacityImage.setUrl(ICON_ERROR_URL);
					if(result.getDSRunningState() == 2) // Deleted
						iconTooltip = UIContext.Constants.rpsDedupErrorDedupStoreIsNotExist();
					else 
						iconTooltip = UIContext.Messages.homepageSummaryDestinationDataStoreUnknownTooltip();
					destinationHealthText.setText(iconTooltip);
		            destinationHealthText.show();
				}
				
				destinationCapacityImage.setTitle(iconTooltip);
			}else if (result.getDestinationCapacityModel()!=null){
				final String freeSizeStr = Utils.bytes2String(result.getDestinationCapacityModel().getTotalFreeSize());
				if (result.getErrorCode()!=0)
					destinationCapacityDescriptionText.setText(UIContext.Constants.homepageSummaryDestinationNotAccessible());
				else
					destinationCapacityDescriptionText.setText(UIContext.Messages.homepageSummaryDestinationCapacity(freeSizeStr));
				
				if (result.getDestinationCapacityModel().getTotalVolumeSize() == 0){
					destinationCapacityImage.setUrl(ICON_ERROR_URL);
				}
				
				if (result.getDestination()!=null){
					if (result.getDestination().startsWith("\\\\"))
						destinationText.setText(UIContext.Messages.homepageSummaryDestinationPath(result.getDestination()));
					else{
						//destinationText.setText(UIContext.Messages.homepageSummaryDestinationVolume(getVolumeName(result)));
						destinationText.setText(UIContext.Messages.homepageSummaryDestinationVolume(result.getDestination()));						
					}
				}
				
//				dataStoreDetail.setVisible(false);
			}
			//Destination
		    loginService.getVMBackupConfiguration(UIContext.backupVM, new BaseAsyncCallback<VMBackupSettingModel>() {
		    	
		    	@Override
				public void onFailure(Throwable caught) {
					super.onFailure(caught);
				}

				@Override
				public void onSuccess(VMBackupSettingModel settings) {
					if(settings != null && settings.getEnableSpaceNotification() != null &&  settings.getEnableSpaceNotification()) {
						long threshold = 0;
						if(AdvancedSettings.MeasureUnitPercent.equals(settings.getSpaceMeasureUnit())) {
							if(result.getDestinationCapacityModel() != null) 
								threshold = result.getDestinationCapacityModel().getTotalVolumeSize() * settings.getSpaceMeasureNum().longValue() / 100;
						}
						else
							threshold = settings.getSpaceMeasureNum().longValue() * 1024 * 1024;
						
						checkDestinationFreeThreshold(result, threshold, false);
					}
					else {
						if (settings!=null && settings.rpsDestSettings == null)
							checkDestinationFreeThresholdByPercent(result.getDestinationCapacityModel());				
					}
					
					//Chart
					double total = 0;
					if (result.getDestinationCapacityModel()!=null){
						
						long backupSpace = result.getDestinationCapacityModel().getFullBackupSize() 
										 + result.getDestinationCapacityModel().getIncrementalBackupSize()
										 + result.getDestinationCapacityModel().getResyncBackupSize()
										 + result.getDestinationCapacityModel().getCatalogSize();
						
						long othersSpace = result.getDestinationCapacityModel().getTotalVolumeSize()
								- result.getDestinationCapacityModel().getTotalFreeSize()
								- backupSpace;
						
						if (othersSpace<0)
							othersSpace = 0;
						
						total = result.getDestinationCapacityModel().getTotalVolumeSize();
						
						double backup = ((double)(backupSpace))/total;
						double free = ((double)result.getDestinationCapacityModel().getTotalFreeSize())/total;
						double others = ((double)othersSpace)/total;
						
						GWT.log("Other Percent:"+String.valueOf(others), null);
						int backupPercent = (int)(backup*100);
						int freePercent = (int)(free*100);
						int othersPercent = (int)(others*100);
						
						legendBackupText.setText(UIContext.Messages.homepageSummaryLegendBackup(Utils.bytes2String(backupSpace)));
						legendOthersText.setText(UIContext.Messages.homepageSummaryLegendOthers(Utils.bytes2String(othersSpace)));
						legendFreeText.setText(UIContext.Messages.homepageSummaryLegendFree(Utils.bytes2String(result.getDestinationCapacityModel().getTotalFreeSize())));

						StringBuffer buffer = new StringBuffer();
						buffer.append("<table width=\"550\" height=\"15\" style=\"border:1px solid #000000; margin: 0px;\" CELLPADDING=0 CELLSPACING=0>");
						buffer.append("<tr>");
						
						if (result.getDestinationCapacityModel().getTotalVolumeSize()>0){
							if (backupPercent>0)
								appendTDChart(buffer,backupPercent, "images/legend_incremental.png", legendBackupText.getText());
							if (othersPercent>0)
								appendTDChart(buffer,othersPercent, "images/legend_others.png", legendOthersText.getText());
							appendTDChart(buffer,freePercent, "images/legend_freeSpace.png", legendFreeText.getText());
						}else{
							buffer.append("<td/>");
						}
						buffer.append("</tr></table>");
						
						if (result.getRpsHostModel() == null)
							destinationHtml.setHTML(buffer.toString());
						
						if (result.getErrorCode()!=0)
							destinationCapacityImage.setTitle(UIContext.Constants.homepageSummaryDestinationNotAccessible());
						
						if (settings.rpsDestSettings == null) {
							if (result.getDestinationCapacityModel().getTotalVolumeSize() == 0)
								destinationCapacityImage.setTitle(UIContext.Constants.homepageSummaryDestinationSizeZero());
							else
								destinationCapacityImage.setTitle(UIContext.Messages.homepageSummaryDestinationTooltip((int)freePercent, Utils.bytes2String(result.getDestinationCapacityModel().getTotalFreeSize())));
						}
						backupLegendImage.setTitle(legendBackupText.getText());
						othersLegendImage.setTitle(legendOthersText.getText());
						freeLegendImage.setTitle(legendFreeText.getText());
					}			

					if (backupImage.getUrl().contains(ICON_ERROR_URL)
							|| !result.isBackupSet() && recoveryPointsImage.getUrl().contains(ICON_ERROR_URL)
							|| result.isBackupSet() && recoverySetsImage.getUrl().contains(ICON_ERROR_URL)
							|| destinationCapacityImage.getUrl().contains(ICON_ERROR_URL) 
							|| licImage.getUrl().contains(ICON_ERROR_URL) ){
						totalStatusImage.setUrl(ICON_LARGE_ERROR_URL);
						totalStatusImage.setTitle(getTotalStatusImageTitle(ICON_ERROR_URL));
					}else if (backupImage.getUrl().contains(ICON_WARNING_URL)
							|| recoveryPointsImage.getUrl().contains(ICON_WARNING_URL)
							|| recoverySetsImage.getUrl().contains(ICON_WARNING_URL)
							|| destinationCapacityImage.getUrl().contains(ICON_WARNING_URL)
							|| licImage.getUrl().contains(ICON_WARNING_URL)){
						totalStatusImage.setUrl(ICON_LARGE_WARNING_URL);
						totalStatusImage.setTitle(getTotalStatusImageTitle(ICON_WARNING_URL));
					}else{
						totalStatusImage.setUrl(ICON_LARGE_FINISH_URL);
						totalStatusImage.setTitle(getTotalStatusImageTitle(ICON_LARGE_FINISH_URL));
					}
					
					if (result.getRpsHostModel() == null)
					    lcLegendContainer.show();
					else
						lcLegendContainer.hide();
				}
		    });
		    
			
			
		}
	}

	
	private void refreshRecoveryPoints(final BackupInformationSummaryModel result){
		//Recovery Points
		if(!result.isAdvanced()){
			recoveryPointsDescriptionText.setVisible(true);
			recoveryPointsDescriptionText.setText(UIContext.Messages.homepageSummaryRecoveryPoints(result.getRecoveryPointCount(), result.getRetentionCount()));
			
			recoveryPointsLargerText.hide();
			mergeDelayedText.hide();
			startManualMergeText.setVisible(false);
			if (result.getRecoveryPointCount() <= 0){
				recoveryPointsImage.setUrl(ICON_ERROR_URL);
				recoveryPointsImage.setTitle(UIContext.Constants.homepageSummaryRecoveryPointError());
			}else if (result.getRecoveryPointCount()>0 && result.getRecoveryPointCount()<result.getRetentionCount()){
				recoveryPointsImage.setUrl(ICON_WARNING_URL);
				recoveryPointsImage.setTitle(UIContext.Messages.homepageSummaryRecoveryPointWarningTooltip(result.getRecoveryPointCount(), result.getRetentionCount()));
			}else if (result.getRecoveryPointCount()==result.getRetentionCount()){
				recoveryPointsImage.setUrl(ICON_FINISH_URL);
				recoveryPointsImage.setTitle(UIContext.Constants.homepageSummaryRecoveryPointInfo());
			}else {
				recoveryPointsImage.setUrl(ICON_FINISH_URL);
				recoveryPointsImage.setTitle(UIContext.Constants.homepageSummaryRecoveryPointInfoExceed());
			}
		}else{
			recoveryPointsDescriptionText.setVisible(false);
			// repeat
			recoveryPointsRepeatDescriptionText.setText(UIContext.Messages.repeatRecoveryPointsStatus(result.getRecoveryPointCount4Repeat(), result.getRetentionCount()));
			//daily/weekly/monthly
			isDayShow =false;
			isWeekShow=false;
			isMonthShow= false;
			int dayCnt = result.getRecoveryPointCount4Day();
			int weekCnt = result.getRecoveryPointCount4Week();
			int monthCnt = result.getRecoveryPointCount4Month();
			
			int dayRetention  = 0;
			int weekRetention = 0;
			int monthRetention = 0;		
			
			if(result.isPeriodEnabled()){				
				if(result.getAdvanceScheduleModel() != null && result.getAdvanceScheduleModel().periodScheduleModel != null){
					EveryDayScheduleModel dayScheduleModel = result.getAdvanceScheduleModel().periodScheduleModel.dayScheduleModel;	
					if(dayScheduleModel != null && dayScheduleModel.isEnabled()){
						dayRetention = dayScheduleModel.getRetentionCount();
						isDayShow = true;
					}
					EveryWeekScheduleModel weekScheduleModel= result.getAdvanceScheduleModel().periodScheduleModel.weekScheduleModel;	
					if(weekScheduleModel != null && weekScheduleModel.isEnabled()){
						weekRetention = weekScheduleModel.getRetentionCount();
						isWeekShow = true;
					}
					EveryMonthScheduleModel monthScheduleModel= result.getAdvanceScheduleModel().periodScheduleModel.monthScheduleModel;
					if(monthScheduleModel != null && monthScheduleModel.isEnabled()){
						monthRetention = monthScheduleModel.getRetentionCount();	
						isMonthShow = true;
					}
				}		
				recoveryPointsDailyDescriptionText.setText(UIContext.Messages.dailyRecoveryPointsStatus(dayCnt, dayRetention));
				recoveryPointsWeeklyDescriptionText.setText(UIContext.Messages.weeklyRecoveryPointsStatus(weekCnt, weekRetention));
				recoveryPointsMonthlyDescriptionText.setText(UIContext.Messages.monthlyRecoveryPointsStatus(monthCnt, monthRetention));
			}
			
			recoveryPointsLargerText.hide();
			mergeDelayedText.hide();
			startManualMergeText.setVisible(false);
			if (result.getRecoveryPointCount() <= 0
					&& ((isDayShow && dayCnt<=0) || !isDayShow)
					&& ((isWeekShow && weekCnt<=0)||!isWeekShow)
					&&((isMonthShow && monthCnt<=0) || !isMonthShow)){
				recoveryPointsImage.setUrl(ICON_ERROR_URL);
				recoveryPointsImage.setTitle(UIContext.Constants.homepageSummaryRecoveryPointError());
			}else if (result.getRecoveryPointCount()<result.getRetentionCount() 
					||(isDayShow && dayCnt<dayRetention)
					||(isWeekShow && weekCnt<weekRetention)
					||(isMonthShow && monthCnt<monthRetention)){
				recoveryPointsImage.setUrl(ICON_WARNING_URL);
				recoveryPointsImage.setTitle(UIContext.Messages.homepageSummaryRecoveryPointWarningTooltip(result.getRecoveryPointCount(), result.getRetentionCount()));
			}else if (result.getRecoveryPointCount()==result.getRetentionCount()
					&& (isDayShow && dayCnt == dayRetention)
					&& (isWeekShow && weekCnt == weekRetention)
					&& (isMonthShow && monthCnt == monthRetention)){
				recoveryPointsImage.setUrl(ICON_FINISH_URL);
				recoveryPointsImage.setTitle(UIContext.Constants.homepageSummaryRecoveryPointInfo());
			}else {
				recoveryPointsImage.setUrl(ICON_FINISH_URL);
				recoveryPointsImage.setTitle(UIContext.Constants.homepageSummaryRecoveryPointInfoExceed());
			}
			afterLoadBackupSummary();
		}	
		refreshMountedSessions(result, recoveryPointsLargerText);
	}

	private void checkDestinationFreeThreshold(
			final BackupInformationSummaryModel result, long threshold, boolean estimatedValue) {
		if (result.getDestinationCapacityModel()!=null){
			long freeSize = result.getDestinationCapacityModel().getTotalFreeSize();
			String freeSizeStr = Utils.bytes2String(freeSize);
			
			if (result.getDestinationCapacityModel().getTotalVolumeSize() != 0){
				if (freeSize <= threshold){
					destinationCapacityImage.setUrl(ICON_WARNING_URL);
					String destinationDescription;
					if(!estimatedValue) 
						destinationDescription = UIContext.Messages.homepageSummaryDestThresholdReached(freeSizeStr);
					else
						destinationDescription =  UIContext.Messages.homepageSummaryDestFreeSizeLow(freeSizeStr, LeastBackupSize);
					
					destinationCapacityDescriptionText.setText(destinationDescription);
				}else{
					destinationCapacityImage.setUrl(ICON_FINISH_URL);
				}
			}
		}
	}
	
	@Override
	public void refresh(Object data, int changeSource) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected String getVMInstanceUUID() {
		return UIContext.backupVM.getVmInstanceUUID();
	}
}
