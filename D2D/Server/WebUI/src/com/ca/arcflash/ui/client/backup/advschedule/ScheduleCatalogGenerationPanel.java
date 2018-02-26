package com.ca.arcflash.ui.client.backup.advschedule;

import com.ca.arcflash.ui.client.FlashUIConstants;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.schedule.EveryDayScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.EveryMonthScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.EveryWeekScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.PeriodScheduleModel;
import com.ca.arcflash.ui.client.backup.schedule.ScheduleUtils.ScheduleTypeModel;
import com.ca.arcflash.ui.client.common.AdvScheduleUtil;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.HasValidateValue;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.FlowData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class ScheduleCatalogGenerationPanel extends LayoutContainer implements HasValidateValue<BackupSettingsModel>{
	protected static FlashUIConstants uiConstants=UIContext.Constants;
	private CheckBox dailyCheckBox;
	private CheckBox weeklyCheckBox;
	private CheckBox monthlyCheckBox;
	private CheckBox customCheckBox;
	
	private CheckBox exchangeCatalogCheckBox;
	private LayoutContainer exchangePanel;
	private Listener<BaseEvent> listener;
	public static final long EXCHANGE_GRT_ENABLE_AFTER_BACKUP = 1L;
	public static final long EXCHANGE_GRT_ENABLE_BEFORE_RESTORE = 2L;
	private LabelField labelCatalogDesctiption;	
	private LayoutContainer lcCatalogExch32BitWarn;
	private Widget grtUtilityInfoMessage;
	
	public ScheduleCatalogGenerationPanel(){
		this.setStyleAttribute("margin-top", "20px");
		labelCatalogDesctiption = new LabelField(UIContext.Constants.destinationCatalogTitle());		
		LabelField labelFSCatalog = new LabelField(uiConstants.scheduleGenerateFileSystemCatalog());		
		this.add(AdvScheduleUtil.createFormLayout(labelCatalogDesctiption, labelFSCatalog));
		
		LayoutContainer container = new LayoutContainer();
		dailyCheckBox = new CheckBox();
		dailyCheckBox.setBoxLabel(uiConstants.scheduleGenerateCatalogDaily());
		dailyCheckBox.disable();
		dailyCheckBox.addListener(Events.OnClick,listener);
		container.add(dailyCheckBox);
					
		weeklyCheckBox = new CheckBox();
		weeklyCheckBox.setBoxLabel(uiConstants.scheduleGenerateCatalogWeekly());
		weeklyCheckBox.disable();
		weeklyCheckBox.addListener(Events.OnClick,listener);
		container.add(weeklyCheckBox);
		
		monthlyCheckBox = new CheckBox();
		monthlyCheckBox.setBoxLabel(uiConstants.scheduleGenerateCatalogMonthly());
		monthlyCheckBox.disable();
		monthlyCheckBox.addListener(Events.OnClick,listener);
		container.add(monthlyCheckBox);
		
		customCheckBox = new CheckBox();
		customCheckBox.setBoxLabel(uiConstants.scheduleGenerateCatalogCustom());
		customCheckBox.addListener(Events.OnClick,listener);
		container.add(customCheckBox);	
		this.add(AdvScheduleUtil.createFormLayout("",container), AdvScheduleUtil.createLineLayoutData());
		
		if (UIContext.isExchangeGRTFuncEnabled) {
			exchangePanel = new LayoutContainer();
			LabelField exchagelabel = new LabelField(uiConstants.scheduleMicrosoftExchangeServers());
			exchangePanel.add(exchagelabel);		
			
			exchangeCatalogCheckBox = new CheckBox();
			exchangeCatalogCheckBox.setBoxLabel(uiConstants.scheduleGenerateExchangeCatalogForBackupNew());
			exchangeCatalogCheckBox.ensureDebugId("28866937-2d84-4a4b-aa41-547a5a7bb08f");	
			Utils.addToolTip(exchangeCatalogCheckBox, UIContext.Constants.backupSettingsGRTEnableTooltip());
			exchangePanel.add(exchangeCatalogCheckBox);
			grtUtilityInfoMessage = createGRTUtilityInfoMessage();
			exchangePanel.add(grtUtilityInfoMessage);
			this.add(AdvScheduleUtil.createFormLayout("",exchangePanel), AdvScheduleUtil.createLineLayoutData());
	
			lcCatalogExch32BitWarn = (LayoutContainer)createCatalogExch32BitWarningMessage();
			lcCatalogExch32BitWarn.setStyleAttribute("margin-left", "10px");
			lcCatalogExch32BitWarn.setStyleAttribute("margin-bottom", "1px");		
			lcCatalogExch32BitWarn.setVisible(false);
			this.add(AdvScheduleUtil.createFormLayout("", lcCatalogExch32BitWarn), new FlowData(0,0,1,5));			
		} else {
			this.add(AdvScheduleUtil.createFormLayout("", createCatalogExchInfoMessage()), AdvScheduleUtil.createLineLayoutData());
		}		
	}
	
	@Override
	public void buildValue(BackupSettingsModel value) {
		PeriodScheduleModel periodSchedule = value.advanceScheduleModel.periodScheduleModel;
		EveryDayScheduleModel daySchedule = periodSchedule.dayScheduleModel;
		if(daySchedule != null && daySchedule.isEnabled()){
			daySchedule.setGenerateCatalog(dailyCheckBox.getValue());
		}
		
		EveryWeekScheduleModel weekSchedule = periodSchedule.weekScheduleModel;
		if(weekSchedule!=null && weekSchedule.isEnabled()){
			weekSchedule.setGenerateCatalog(weeklyCheckBox.getValue());
		}
		
		EveryMonthScheduleModel monthSchedule = periodSchedule.monthScheduleModel;
		if(monthSchedule!=null && monthSchedule.isEnabled()){
			monthSchedule.setGenerateCatalog(monthlyCheckBox.getValue());
		}
		value.setGenerateCatalog(customCheckBox.getValue());
		
		if(UIContext.isExchangeGRTFuncEnabled && exchangeCatalogCheckBox.getValue()){
			value.setExchangeGRTSetting(EXCHANGE_GRT_ENABLE_AFTER_BACKUP);
		}else{
			value.setExchangeGRTSetting(EXCHANGE_GRT_ENABLE_BEFORE_RESTORE);
		}
	}

	@Override
	public void applyValue(BackupSettingsModel value) {
		PeriodScheduleModel periodSchedule = value.advanceScheduleModel.periodScheduleModel;
		if(periodSchedule != null) {
			EveryDayScheduleModel daySchedule = periodSchedule.dayScheduleModel;
			dailyCheckBox.setValue(daySchedule != null && daySchedule.isEnabled() && daySchedule.isGenerateCatalog());
			EveryWeekScheduleModel weekSchedule = periodSchedule.weekScheduleModel;
			weeklyCheckBox.setValue(weekSchedule!=null && weekSchedule.isEnabled() && weekSchedule.isGenerateCatalog());
			EveryMonthScheduleModel monthSchedule = periodSchedule.monthScheduleModel;
			monthlyCheckBox.setValue(monthSchedule!=null && monthSchedule.isEnabled() && monthSchedule.isGenerateCatalog());
		}
		customCheckBox.setValue(value.getGenerateCatalog());
		if (UIContext.isExchangeGRTFuncEnabled && value.getExchangeGRTSetting()!=null) {
			exchangeCatalogCheckBox.setValue(value.getExchangeGRTSetting() == EXCHANGE_GRT_ENABLE_AFTER_BACKUP);
			checkExchangeService();
		}
	}

	@Override
	public boolean validate() {	
		return true;
	}
	
	public void enableCatalog(int backupType){
		if(backupType == ScheduleTypeModel.OnceDailyBackup){
			dailyCheckBox.enable();
		}else if(backupType == ScheduleTypeModel.OnceWeeklyBackup){
			weeklyCheckBox.enable();
		}else if(backupType == ScheduleTypeModel.OnceMonthlyBackup){
			monthlyCheckBox.enable();
		}
	}
	
	public void disableCatalog(int backupType){
		if(backupType == ScheduleTypeModel.OnceDailyBackup){
			dailyCheckBox.setValue(false);
			dailyCheckBox.disable();
		}else if(backupType == ScheduleTypeModel.OnceWeeklyBackup){
			weeklyCheckBox.setValue(false);
			weeklyCheckBox.disable();
		}else if(backupType == ScheduleTypeModel.OnceMonthlyBackup){
			monthlyCheckBox.setValue(false);
			monthlyCheckBox.disable();
		}
	}
	
	public void showCheckboxGenerateGRTCatalog(boolean isHBBU) {
		if(UIContext.isExchangeGRTFuncEnabled && isHBBU)
		{	
			lcCatalogExch32BitWarn.setVisible(true);
		}
	}
	
	public void setVisible4HbbuCommentLabel() {
		labelCatalogDesctiption.setValue(UIContext.Constants.destinationCatalogTitle() + " (" + UIContext.Constants.forWindowsVMOnlyLabel() + ")");		
	}
	
	private Widget createCatalogExch32BitWarningMessage() {
		LayoutContainer tableContainer = new LayoutContainer();
		
		TableLayout tableLayout = new TableLayout();
		tableLayout.setCellPadding(0);
		tableLayout.setCellSpacing(5);
		tableLayout.setColumns(2);
		tableLayout.setWidth("100%");		
		tableContainer.setLayout(tableLayout);
		
		tableContainer.add(AbstractImagePrototype.create(UIContext.IconBundle.logWarning()).createImage());
		tableContainer.add(new LabelField(UIContext.Constants.scheduleCatalogExch32BitWarn()));		
		return tableContainer;
	}
	
	private Widget createCatalogExchInfoMessage() {
		LayoutContainer container = new LayoutContainer();
		container.addStyleName("catalogExchInfoStyle");
					
		String message = createMessageWithLinkForGrtDisabled();
		container.add(createMessageWithIconContainer(message));
		
		return container;
	}
	
	private Widget createGRTUtilityInfoMessage() {
		LayoutContainer container = new LayoutContainer();
		container.addStyleName("infobox");
		container.setStyleAttribute("margin", "0px");
							
		container.add(new LabelField(createMessageWithLinkForGrtEnabled()));
		
		return container;		
	}
	
	private LayoutContainer createMessageWithIconContainer(String message) {
		LayoutContainer tableContainer = new LayoutContainer();
		
		TableLayout tableLayout = new TableLayout();
		tableLayout.setCellPadding(0);
		tableLayout.setCellSpacing(5);
		tableLayout.setColumns(2);
		tableLayout.setCellVerticalAlign(VerticalAlignment.TOP);
		tableLayout.setWidth("100%");		
		tableContainer.setLayout(tableLayout);
		
		Image info = AbstractImagePrototype.create(UIContext.IconBundle.info_16x16()).createImage();
		info.setStyleName("catalogExchConfigInfoImageStyle");
		tableContainer.add(info);
		tableContainer.add(new LabelField(message));
		
		return tableContainer;
	}
	
	private String createMessageWithLinkForGrtDisabled() {				
		String link = createLinkHtml(UIContext.externalLinks.getExchangeGranularRestoreUtility(), 
				uiConstants.scheduleCatalogExchUtilityLink());
		String message = UIContext.Messages.scheduleCatalogExchNotRequired(link);
		return message;
	}
	
	private String createMessageWithLinkForGrtEnabled() {				
		String link = createLinkHtml(UIContext.externalLinks.getExchangeGranularRestoreUtility(), 
				UIContext.Constants.scheduleCatalogExchUtilityLinkShort(), "text-decoration:underline;");
		String message = UIContext.Messages.scheduleGRTUtilityCatalogExchNotNecessary(link);
		return message;
	}
	
	private String createLinkHtml(String url, String label) {
		return createLinkHtml(url, label, null);
	}
	
	private String createLinkHtml(String url, String label, String style) {
		StringBuilder sbLinkHtml = new StringBuilder();
		
		sbLinkHtml.append("<a class=\"catalogExchConfigInfoLinkStyle\" style=\"color:#0066AA;");		
		sbLinkHtml.append(style);		
		sbLinkHtml.append("\" href=\"");
		sbLinkHtml.append(url);
		sbLinkHtml.append("\" target=\"_blank\">");
		sbLinkHtml.append(label);
		sbLinkHtml.append("</a>");
		
		return sbLinkHtml.toString();
	}
	
	private void checkExchangeService() {
		// check if Exchange Service is installed
		final LoginServiceAsync service = GWT.create(LoginService.class);
		service.checkServiceStatus("msexchangeis", new BaseAsyncCallback<Long>() {

			@Override
			public void onFailure(Throwable caught) {
				exchangeCatalogCheckBox.setEnabled(false);
				exchangeCatalogCheckBox.setValue(false);
				grtUtilityInfoMessage.setVisible(false);
				super.onFailure(caught);
			}

			@Override
			public void onSuccess(Long result) {
				if (result != null) {
					// Exchange Service is installed
					// if for edge this option should be always enabled
					if (((result.intValue() & 0x01) == 0x01)) {
						if (null == UIContext.serverVersionInfo.edgeInfoCM) {
							exchangeCatalogCheckBox.setEnabled(true);
						} else {
							exchangeCatalogCheckBox.setEnabled(false);
						}
					} else {
						exchangeCatalogCheckBox.setEnabled(false);
						exchangeCatalogCheckBox.setValue(false);
						grtUtilityInfoMessage.setVisible(false);
					}

				} else {
					exchangeCatalogCheckBox.setEnabled(false);
					exchangeCatalogCheckBox.setValue(false);
					grtUtilityInfoMessage.setVisible(false);
				}

				super.onSuccess(result);
			}

		});
	}
}
