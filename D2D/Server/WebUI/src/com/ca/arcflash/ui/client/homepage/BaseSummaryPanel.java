package com.ca.arcflash.ui.client.homepage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.IRefreshable;
import com.ca.arcflash.ui.client.common.LoadingStatus;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.BackupInformationSummaryModel;
import com.ca.arcflash.ui.client.model.BackupStatusModel;
import com.ca.arcflash.ui.client.model.DestinationCapacityModel;
import com.ca.arcflash.ui.client.model.MountSessionModel;
import com.ca.arcflash.ui.client.model.RecentBackupModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public abstract class BaseSummaryPanel extends LayoutContainer implements IRefreshable{
	protected final HomepageServiceAsync service = GWT.create(HomepageService.class);
	protected final LoginServiceAsync loginService = GWT.create(LoginService.class);
	protected final CommonServiceAsync commonService = GWT.create(CommonService.class);
	
	public static final String ICON_FINISH_URL	=	"images/status_mid_finish.gif";
	public static final String ICON_WARNING_URL	=	"images/status_mid_warning.gif";
	public static final String ICON_ERROR_URL	=	"images/status_mid_error.gif";
	
	public static final String ICON_LARGE_FINISH_URL	=	"images/status_large_finish.gif";
	public static final String ICON_LARGE_WARNING_URL	=	"images/status_large_warning.gif";
	public static final String ICON_LARGE_ERROR_URL		=	"images/status_large_error.gif";
	
	protected Image destinationCapacityImage;
	protected Text destinationCapacityDescriptionText;
	
	protected Image backupImage;
	protected boolean isBackupSet = false;
	protected Image recoveryPointsImage;
	protected Image recoverySetsImage;
	protected HTML destinationHtml;
	
	protected LayoutContainer lcLegendContainer;
	protected Image backupLegendImage;
	protected Image othersLegendImage;
	protected Image freeLegendImage;
	protected Label legendBackupText;
	protected Label legendOthersText;
	protected Label legendFreeText;
	protected Text mergeDelayedText;
	protected Label startManualMergeText;
	protected ContentPanel interLeftPanel;
	protected AbstractImagePrototype imagePrototype;
	protected Text backupDescriptionText;
	protected LayoutContainer lcBackupsPanel;
	protected LoadingStatus loadLastBackup;
	protected Label backupDescriptionLabel;
	
	protected String getVolumeName(final BackupInformationSummaryModel result) {
		String destination = result.getDestination().toLowerCase();
		List<String> driveLetters = UIContext.serverVersionInfo.getLocalDriverLetters();
		String maxLengthMatch = "";
		for (int i = 0, count = driveLetters == null ? 0 : driveLetters.size(); i < count; i++) {
			String driverLetter = driveLetters.get(i).toLowerCase();
			if(destination.startsWith(driverLetter) && driverLetter.length() > maxLengthMatch.length())
				maxLengthMatch = driverLetter;
		}
		return maxLengthMatch;
	}
	
	protected void checkDestinationFreeThresholdByPercent(final DestinationCapacityModel result) {
		if (result!=null){
			long freeSize = result.getTotalFreeSize();
			String freeSizeStr = Utils.bytes2String(freeSize);
			
			if (result.getTotalVolumeSize() != 0){
				long totalVolumnSizeMB = (result.getTotalVolumeSize()) / (1024*1024); // MB
				long freeDiskSpaceMB   = (freeSize) / (1024*1024); // MB
				double freeDiskPercent = 0;
				if (totalVolumnSizeMB != 0)
				{
					freeDiskPercent = ((freeDiskSpaceMB * 1.0) / (totalVolumnSizeMB * 1.0)) * 100;
				}
				else
				{
					freeDiskPercent = 0;
				}
				
				if (freeDiskPercent <= 1.0){
					destinationCapacityImage.setUrl(ICON_WARNING_URL);
					String destinationDescription = UIContext.Messages.homepageSummaryDestFreeSizeLowEx(freeSizeStr);
					destinationCapacityDescriptionText.setText(destinationDescription);
				}else{
					destinationCapacityImage.setUrl(ICON_FINISH_URL);
				}
			}
		}else {
			destinationCapacityImage.setUrl(ICON_ERROR_URL);
		}
	}
	
	protected void appendTDChart(StringBuffer buffer, int percent, String image, String title){
		buffer.append("<td ");
		buffer.append(" title=\"");
		buffer.append(title);
		buffer.append("\" width=\"");
		buffer.append(percent);
		buffer.append("%\" style=\"background-image: url(./");
		buffer.append(image);
		buffer.append(");\"/>");
	}
	
	protected String getTotalStatusImageTitle(String iconURL){
		StringBuffer buffer = new StringBuffer();
		
		if (backupImage.getUrl().contains(ICON_ERROR_URL) && backupImage.getTitle()!=null && !backupImage.getTitle().equals(""))
			buffer.append(backupImage.getTitle());
		//wanqi06
		if(!isBackupSet){
			if (recoveryPointsImage.getUrl().contains(ICON_ERROR_URL) && recoveryPointsImage.getTitle()!=null && !recoveryPointsImage.getTitle().equals("")){
				if (buffer.length()>0)
					buffer.append("\r\n");
				buffer.append(recoveryPointsImage.getTitle());
			}
		}
		else {
			if (recoverySetsImage.getUrl().contains(ICON_ERROR_URL) && recoverySetsImage.getTitle()!=null && !recoverySetsImage.getTitle().equals("")){
				if (buffer.length()>0)
					buffer.append("\r\n");
				buffer.append(recoverySetsImage.getTitle());
			}
		}
				
		//
		if (destinationCapacityImage.getUrl().contains(ICON_ERROR_URL) && destinationCapacityImage.getTitle()!=null && !destinationCapacityImage.getTitle().equals("")){
			if (buffer.length()>0)
				buffer.append("\r\n");
			buffer.append(destinationCapacityImage.getTitle());
		}
			
		return buffer.toString();
	}
	
	protected RecentBackupModel getRecentBackup(BackupInformationSummaryModel result) {
		Date time = null;
		RecentBackupModel backupModel = null;
		if(result.getRecentFullBackup() != null)
		{
			time = result.getRecentFullBackup().getTime();
			backupModel = result.getRecentFullBackup();
			backupModel.setName(UIContext.Constants.scheduleLabelFullBackup());
		}
		if(result.getRecentIncrementalBackup() != null && (time == null || result.getRecentIncrementalBackup().getTime().after(time)))
		{
			time = result.getRecentIncrementalBackup().getTime();
			backupModel = result.getRecentIncrementalBackup();
			backupModel.setName(UIContext.Constants.scheduleLabelIncrementalBackup());
		}
		if(result.getRecentResyncBackup() != null && (time == null || result.getRecentResyncBackup().getTime().after(time)))
		{
			time = result.getRecentResyncBackup().getTime();
			backupModel = result.getRecentResyncBackup();
			backupModel.setName(UIContext.Constants.scheduleLabelResyncBackup());
		}
		
		return backupModel;
	}
	
	protected String convertStatusToImageURL(int backupStatus){
		if (backupStatus == BackupStatusModel.Finished)
			return ICON_FINISH_URL;
		else if (backupStatus == BackupStatusModel.Crashed || backupStatus == BackupStatusModel.Failed)
			return ICON_ERROR_URL;
		else
			return ICON_WARNING_URL;
	}
	
	protected void createDestinationChart(ContentPanel panel, TableData td) {
		TableLayout layout = new TableLayout();
        layout.setWidth("100%");
		layout.setColumns(6);
		
		LayoutContainer container = new LayoutContainer();
		container.setLayout(layout);
		container.setStyleAttribute("padding", "4px");
		container.setStyleAttribute("padding-top", "10px");
		
		TableData tableData = new TableData();
		tableData.setColspan(6);
		destinationHtml = new HTML();
		container.add(destinationHtml,tableData);
	    
		panel.add(container, td);
	}
	
	protected void createDestinationLengend(ContentPanel rightPanel, TableData td) {
		TableLayout layout = new TableLayout();
		layout.setColumns(6);
		
		lcLegendContainer = new LayoutContainer();
		lcLegendContainer.setLayout(layout);
		lcLegendContainer.setStyleAttribute("padding", "2px");
		lcLegendContainer.setStyleAttribute("padding-top", "6px");
		
		AbstractImagePrototype imagePrototype = IconHelper.create("images/legend_incremental.png", 16,16);
		backupLegendImage = imagePrototype.createImage();
		lcLegendContainer.add(backupLegendImage);
	    
//	    legendFullText = new Label();
//	    legendFullText.setStyleName("homepage_summary_legengLabel");
//	    container.add(legendFullText);
	    
//	    backupLegendImage = imagePrototype.createImage();
//	    backupLegendImage.setUrl("images/legend_incremental.png");
//	    container.add(backupLegendImage);
	    
	    legendBackupText = new Label();
	    legendBackupText.setStyleName("homepage_summary_legengLabel");
	    lcLegendContainer.add(legendBackupText);
	    
	    othersLegendImage = imagePrototype.createImage();
	    othersLegendImage.setUrl("images/legend_others.png");
	    lcLegendContainer.add(othersLegendImage);
	    
	    legendOthersText = new Label();
	    legendOthersText.setStyleName("homepage_summary_legengLabel");
	    lcLegendContainer.add(legendOthersText);
	    
	    freeLegendImage = imagePrototype.createImage();
	    freeLegendImage.setUrl("images/legend_freeSpace.png");
	    lcLegendContainer.add(freeLegendImage);
	    
	    legendFreeText = new Label();
	    legendFreeText.setStyleName("homepage_summary_legengLabel");
	    lcLegendContainer.add(legendFreeText);
		
	    rightPanel.add(lcLegendContainer, td);
	}
	
	protected Text createRecoveryPointsMountedText() {
		Text text = new Text();
		text.setStyleName("homepage_summary_description");
		text.setStyleAttribute("color", "#FF0000");
		return text;
	}
	
	/**
	 * Only show this message when there are no sessions mounted.
	 * @param result
	 */
	protected void showStartMerge(BackupInformationSummaryModel result) {
		if(result.isBackupSet())
			return;
		if(result.getMergeStartTime() != null && !result.getMergeStartTime().isEmpty()){
			if(result.isInSchedule()){
				mergeDelayedText.setText(
						UIContext.Constants.startManualMerge());						
			}else{
				mergeDelayedText.setText(
						UIContext.Messages.mergeNotInScheduleSummary(result.getMergeStartTime()));						
			}
			mergeDelayedText.show();
			startManualMergeText.setVisible(true);
		}
	}
	
	protected Widget addRecentBackup(LayoutContainer parent, String label, Image image, List<Widget> descriptions, LoadingStatus status) {
		TableLayout layout = new TableLayout();
		layout.setColumns(2);
		
		//parent = new LayoutContainer();
//		if(destinationText == others)
//			thisPanel = container;
		parent.setLayout(layout);
		parent.setStyleAttribute("padding", "4px");
		
		TableData tableData = null;
		tableData = new TableData();
		tableData.setVerticalAlign(VerticalAlignment.TOP);
		tableData.setHorizontalAlign(HorizontalAlignment.LEFT);
		tableData.setRowspan(2 + descriptions.size());
		parent.add(image,tableData);
		
		tableData = new TableData();
		tableData.setVerticalAlign(VerticalAlignment.TOP);
		tableData.setHorizontalAlign(HorizontalAlignment.LEFT);
		Label text = new Label(label);
		text.setStyleName("homepage_summary_label");
		parent.add(text, tableData);
		
		tableData = new TableData();
		tableData.setVerticalAlign(VerticalAlignment.TOP);
		tableData.setHorizontalAlign(HorizontalAlignment.LEFT);
		for(Widget w : descriptions) {
			parent.add(w, tableData);
		}
		
		if (status != null){
			parent.add(status, tableData);
			status.hide();
		}
		
		//parent = container;
		interLeftPanel.add(parent);
		return text;
	}
	
	protected Widget addRecentBackup(LayoutContainer parent, String label, Image image, Text descriptionText, LoadingStatus status){
		return addRecentBackup(parent, label, image, descriptionText, null, status);
	}
	
	protected Widget addRecentBackup(LayoutContainer parent, String label, Image image, Text descriptionText, Widget others, LoadingStatus status){
		List<Widget> widgets = new ArrayList<Widget>();
		widgets.add(descriptionText);
		if(others != null) {
			widgets.add(others);
		}
		return addRecentBackup(parent, label, image, widgets, status);
	}
	
	protected void createSummaryBackupPart(ContentPanel panel) {
		backupImage = imagePrototype.createImage();
		backupDescriptionText = new Text(UIContext.Constants.NA());
		backupDescriptionText.setStyleName("homepage_summary_description");
		String backupMsg = UIContext.Messages.homepageSummaryMostRecentBackupLabel(UIContext.Constants.remoteDeployAddServerNALabel());
		
		lcBackupsPanel = new LayoutContainer();
		loadLastBackup = new LoadingStatus();
//		loadLastBackup.setStyleName("homepage_summary_description");
	    backupDescriptionLabel = (Label)addRecentBackup(lcBackupsPanel, backupMsg, backupImage,backupDescriptionText, this.loadLastBackup);
	}
	
	protected void refreshMountedSessions(
			final BackupInformationSummaryModel result, final Text largerText) {
		commonService.getMountedSessionToMerge(getVMInstanceUUID(),
				new BaseAsyncCallback<MountSessionModel[]>() {
					@Override
					public void onFailure(Throwable caught) {
						showStartMerge(result);
					}

					@Override
					public void onSuccess(MountSessionModel[] mounted) {
						if (mounted.length > 0) {
							largerText.setText(UIContext.Messages
									.homepageSummaryRecoveryPointsLarger(mounted[0]
											.getSessionPath()));
							largerText.show();
						} else {
							showStartMerge(result);
						}
					}
				});
	}

	protected abstract String getVMInstanceUUID();
}
