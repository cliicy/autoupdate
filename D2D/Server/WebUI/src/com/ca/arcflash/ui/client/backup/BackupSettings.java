package com.ca.arcflash.ui.client.backup;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.BaseSimpleComboBox;
import com.ca.arcflash.ui.client.common.PasswordTextField;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.common.d2d.presenter.SettingPresenter;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class BackupSettings {
	
	protected final LoginServiceAsync service = GWT.create(LoginService.class);
	
	public static final Long PURGE_LOG_NONE		=	0L;
	public static final Long PURGE_LOG_DAILY	=	1L;
	public static final Long PURGE_LOG_WEEKLY	=	7L;
	public static final Long PURGE_LOG_MONTHLY	=	30L;
	
	public static final Long EXCHANGE_GRT_ENABLE_AFTER_BACKUP = 1L;
	public static final Long EXCHANGE_GRT_ENABLE_BEFORE_RESTORE = 2L;	
	
	public static final Long SharePoint_GRT_DISABLE = 0L;
	public static final Long SharePoint_GRT_ENABLE = 1L;
	
//	private NumberField retentionCount;	
	private LayoutContainer rowContainer;
//	private SimpleComboBox<String>  compressionOption;	
	private BackupSettingsContent parentWindow;
	
	private CheckBox purgeSQLCheckBox;
	private CheckBox purgeExchangeCheckBox;
	
	private BaseSimpleComboBox<String> purgeSQLComboBox;
	private BaseSimpleComboBox<String> purgeExchangeComboBox;
	
	private BaseSimpleComboBox<Integer> preAllocationComboBox;
	
//	private CheckBox checkboxGenerateGRTCatalog;
	
//    private Radio enableSharePoint;
//	private Radio disableSharePoint;
	
	private static int MIN_FIELD_WIDTH = 250;
	private TextField<String> adminUserNameField;
	private PasswordTextField adminPasswordField;
	private SRMPkiAlertSettingPanel alertSettingPanel;
	private Button connectionButton;
	
	private Radio softwareSnapshotRadio;
	private Radio hardwareSnapshotRadio;
	
	//private CheckBox  failOverToSoftwareSnapshot;
	private CheckBox  useTransportableSnapshot;
	
	private String netConnError = "17179869199";
//	private CheckBox cbCatalog = new CheckBox();
	
//	private Listener<BaseEvent> catalogCheckListener;
	
//	private FieldSet notificationSet;
	
	private static final int DEFAULT_PREALLOCATE_VALUE = 10;
	private DisclosurePanel preAllocationPanel;
	public BackupSettings(BackupSettingsContent w)
	{
		parentWindow = w;
	}
	
	public LayoutContainer Render()
	{
		rowContainer = new LayoutContainer();	
//		RowLayout rl = new RowLayout();	
//		rowContainer.setLayout(rl);
		TableLayout tl = new TableLayout();
		tl.setColumns(1);
		tl.setWidth("97%");
//		tl.setHeight("50%");
		rowContainer.setLayout(tl);
						
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.backupSettingsSettings());
		label.addStyleName("restoreWizardTitle");
		rowContainer.add(label);
		
		/*label = new LabelField();
		label.setText(UIContext.Constants.settingsLabelPurgeLog());
		label.addStyleName("restoreWizardSubItem");*/
		
		DisclosurePanel panel = Utils.getDisclosurePanel(UIContext.Constants.settingsLabelPurgeLog());
		
		LayoutContainer settingsContainer = new LayoutContainer();
		
		label = new LabelField();
		label.setValue(UIContext.Constants.settingsLabelPurgeLogDecription());
		settingsContainer.add(label);
		
		TableLayout tablePurgeLayout = new TableLayout();
		
		tablePurgeLayout.setCellPadding(0);
		tablePurgeLayout.setCellSpacing(0);
		tablePurgeLayout.setColumns(1);
		
		TableData tableData = new TableData();
		tableData.setPadding(4);
		
		LayoutContainer purgeContainer = new LayoutContainer();
		purgeContainer.setStyleAttribute("padding-left", "8px");
		purgeContainer.setLayout(tablePurgeLayout);
		
		purgeSQLCheckBox = new CheckBox();
		purgeSQLCheckBox.ensureDebugId("2F955273-52C2-4bc3-A842-DC7DA004847D");
		purgeSQLCheckBox.setValue(true);
		purgeSQLCheckBox.setBoxLabel(UIContext.Constants.settingsCheckBoxPurgeSQL());
		Utils.addToolTip(purgeSQLCheckBox, UIContext.Constants.settingsCheckBoxPurgeSQLTooltip());
		purgeSQLCheckBox.addListener(Events.Change, new Listener<FieldEvent>()
				{
					@Override
					public void handleEvent(FieldEvent be) {
						purgeSQLComboBox.setEnabled(purgeSQLCheckBox.getValue());		
					}
			
				});
		purgeContainer.add(purgeSQLCheckBox);
		
		purgeSQLComboBox = new BaseSimpleComboBox<String>();
		purgeSQLComboBox.ensureDebugId("CA117B6F-CBF1-4b9a-9212-729DF6558633");
		//purgeSQLComboBox.setAutoWidth(true); // setting this will cause the input box and the select icon separate. ISSUE 91080
		purgeSQLComboBox.setEditable(false);
		purgeSQLComboBox.add(UIContext.Constants.settingsPurgeLogDaily());
		purgeSQLComboBox.add(UIContext.Constants.settingsPurgeLogWeekly());
		purgeSQLComboBox.add(UIContext.Constants.settingsPurgeLogMonthly());
		purgeSQLComboBox.setSimpleValue(UIContext.Constants.settingsPurgeLogWeekly());
		purgeContainer.add(purgeSQLComboBox,tableData);
		
		purgeExchangeCheckBox = new CheckBox();
		purgeExchangeCheckBox.ensureDebugId("AE3C4B8D-EDAD-4f2a-A16E-DF517BBA6BB1");
		purgeExchangeCheckBox.setVisible(true);
		purgeExchangeCheckBox.setBoxLabel(UIContext.Constants.settingsCheckBoxPurgeExchange());
		Utils.addToolTip(purgeExchangeCheckBox, UIContext.Constants.settingsCheckBoxPurgeExchangeTooltip());
		purgeExchangeCheckBox.addListener(Events.Change, new Listener<FieldEvent>()
				{
					@Override
					public void handleEvent(FieldEvent be) {
						purgeExchangeComboBox.setEnabled(purgeExchangeCheckBox.getValue());		
					}
			
				});
		purgeContainer.add(purgeExchangeCheckBox);
		
		purgeExchangeComboBox = new BaseSimpleComboBox<String>();
		purgeExchangeComboBox.ensureDebugId("F06226D2-D510-4c3d-9524-2AF915172F85");
		purgeExchangeComboBox.setEditable(false);
		purgeExchangeComboBox.add(UIContext.Constants.settingsPurgeLogDaily());
		purgeExchangeComboBox.add(UIContext.Constants.settingsPurgeLogWeekly());
		purgeExchangeComboBox.add(UIContext.Constants.settingsPurgeLogMonthly());
		purgeExchangeComboBox.setSimpleValue(UIContext.Constants.settingsPurgeLogWeekly());
		purgeContainer.add(purgeExchangeComboBox,tableData);
		
		settingsContainer.add(purgeContainer);
		settingsContainer.add(new Html("<HR>"));
		panel.add(settingsContainer);
		rowContainer.add(panel);
		// Exchange GRT Settings
//		addExchageGRTAndSharePointSetting();
		//SharePoint Setting end
		
		SettingPresenter.getInstance().addListener(new Listener<AppEvent>(){
			@Override
			public void handleEvent(AppEvent be) {
				if(SettingPresenter.getInstance().isBackupDataFormatNew(be)){
					preAllocationPanel.setVisible(false);
				}else{
					preAllocationPanel.setVisible(true);
				}
				
			}});
		
		addPreAllocationSetting();
		addHardwareSnapshotType();
		
		// Exchange GRT Settings
//		addExchageGRTAndSharePointSetting();
		//SharePoint Setting end
		
		if(!parentWindow.isShowForVSphere()) {
			
			addBackupAdminAccount();
			
			//to recover connection to all the backup destinations
			if (!parentWindow.isForEdge())
				addReestablishConnection();
		}
		/*if (SettingPresenter.isForEdge()) {
			//SRM Alert Setting Panel
			rowContainer.add(new Html("<HR>"));
			
			label = new LabelField();
			label.setText(UIContext.Constants.srm_srmPkiAlertSetting());
			label.addStyleName("restoreWizardSubItem");
			rowContainer.add(label);
			label = new LabelField();
			label.setText(UIContext.Constants.srm_alertCfgSettingsToolTip());
			rowContainer.add(label);
		
			alertSettingPanel = new SRMPkiAlertSettingPanel();
			rowContainer.add(alertSettingPanel);
		}*/
		return rowContainer;
	}
	
	private void addPreAllocationSetting() {
		preAllocationPanel = Utils.getDisclosurePanel(UIContext.Constants.settingsLabelPreAllocation());
		
		LayoutContainer settingsContainer = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setWidth("100%");
		tl.setColumns(2);		
		tl.setCellSpacing(2);
		settingsContainer.setLayout(tl);
		
		TableData td = new TableData();
		td.setColspan(2);
		td.setWidth("98%");
		
		LabelField label = new LabelField(UIContext.Constants.settingsLabelPreAllocationDesp());
		Utils.addToolTip(label, UIContext.Constants.settingsLabelPreAllocationToolTip());
		settingsContainer.add(label, td);
		
		List<Integer> values = new ArrayList<Integer>();
		for(int i = 0; i <= 100;i += 5) {
			values.add(i);
		}
		preAllocationComboBox = new BaseSimpleComboBox<Integer>();
		preAllocationComboBox.setWidth(100);
		preAllocationComboBox.add(values);
		preAllocationComboBox.setEditable(false);
		preAllocationComboBox.setSimpleValue(DEFAULT_PREALLOCATE_VALUE);
		preAllocationComboBox.ensureDebugId("5842C88F-493E-47b6-9E0D-6F451D7B601B");
		Utils.addToolTip(preAllocationComboBox, UIContext.Constants.settingsLabelPreAllocationToolTip());	
		
/*		LayoutContainer con = new LayoutContainer();
		con.setLayout(new FitLayout());
		con.add(preAllocationComboBox);
		LabelField unit = new LabelField(UIContext.Constants.srm_alertUtilThresholdUnit());
		con.add(unit);
		td = new TableData();
		td.setColspan(1);
		td.setWidth("98%");
		settingsContainer.add(con, td);*/
		
		td = new TableData();
		td.setColspan(1);
		td.setWidth("10%");
		settingsContainer.add(preAllocationComboBox, td);
		
		LabelField unit = new LabelField(UIContext.Constants.srm_alertUtilThresholdUnit());
		td = new TableData();
		td.setColspan(1);
		settingsContainer.add(unit, td);
		
		td = new TableData();
		td.setColspan(2);
		td.setWidth("100%");
		settingsContainer.add(new Html("<HR>"), td);
		preAllocationPanel.add(settingsContainer);
		/*fanda03 enhance 102889; because many places of this class use preAllocationComboBox instance(save/load data). so it's difficult to 
		cancel  preallocation container. so we just don't attach preAllocate container do DOM tree if it's not in local D2D*/
		if(!parentWindow.isShowForVSphere() ) {
			rowContainer.add(preAllocationPanel);
		}
	}

//	private void addExchageGRTAndSharePointSetting() {
//		
//		DisclosurePanel panel = Utils.getDisclosurePanel(UIContext.Constants.destinationCatalogTitle());
//		
//		LayoutContainer settingsContainer = new LayoutContainer();
//		
//		LabelField label;
//		
//		
//		/*label = new LabelField();
//		label.setText(UIContext.Constants.settingsLabelExchangeGRT());
//		label.addStyleName("restoreWizardSubItem");
//		rowContainer.add(label);*/
//		
////		label = new LabelField();
////		label.setText(UIContext.Constants.settingsLabelExchangeGRTDescription());
////		rowContainer.add(label);
//		
//		TableLayout tableGRTLayout = new TableLayout();
//		
//		tableGRTLayout.setCellPadding(4);
//		tableGRTLayout.setCellSpacing(4);
//		tableGRTLayout.setColumns(1);
//		tableGRTLayout.setWidth("95%");
//		
//		TableData tableGRTData = new TableData();
//		tableGRTData.setPadding(4);
//		
//		LayoutContainer GRTContainer = new LayoutContainer();
//		GRTContainer.setStyleAttribute("padding-left", "8px");
//		GRTContainer.setLayout(tableGRTLayout);
//		
////		if(!parentWindow.isShowForVSphere()) {
////			checkboxGenerateGRTCatalog = new CheckBox();
////			checkboxGenerateGRTCatalog.ensureDebugId("4834f054-3bd8-4ab0-9e33-ff18dbe0ff0d");
////			checkboxGenerateGRTCatalog.setVisible(true);
////			checkboxGenerateGRTCatalog.setBoxLabel(UIContext.Constants.settingsLableGRTEnableAfterBackup());
////			Utils.addToolTip(checkboxGenerateGRTCatalog, UIContext.Constants.backupSettingsGRTEnableTooltip());
////			checkboxGenerateGRTCatalog.setStyleAttribute("white-space", "normal");
////			
////			GRTContainer.add(checkboxGenerateGRTCatalog);
////			checkboxGenerateGRTCatalog.setEnabled(true);
////			// -----end of Exchange GRT Settings ----------
////		}
//		
//		
//		catalogCheckListener = new Listener<BaseEvent>() {
//
//			@Override
//			public void handleEvent(BaseEvent ArchiveEvent) {
//				if(ArchiveEvent.getSource() == cbCatalog)
//				{
//					if(!cbCatalog.getValue() && BackupContext.isFileCopyEnable()){
//						MessageBox mb = new MessageBox();
//						mb.setIcon(MessageBox.WARNING);
//						mb.setButtons(MessageBox.YESNO);
//						mb.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
//						mb.setModal(true);
//						mb.setMinWidth(400);
//						mb.setMessage(UIContext.Constants.settingsIFDisableArchive());
//						mb.addCallback(new Listener<MessageBoxEvent>()
//						{
//							public void handleEvent(MessageBoxEvent be)
//							{
//								if (be.getButtonClicked().getItemId().equals(Dialog.NO)) {
//									BackupContext.getArchiveSourceSettings().cbArchiveAfterBackup.setValue(false);
//								}
//								else {
//									cbCatalog.setValue(true);
//								}
//									
//							}
//						});
//						Utils.setMessageBoxDebugId(mb);
//						mb.show();
//					}
//					if( parentWindow.getRefsVolList() != null){
//						if (cbCatalog.getValue())
//						{
//							addWaringIcon();	
//							notificationSet.add(new LabelField(UIContext.Messages.refsVolumesSelect(parentWindow.getRefsVolList())));
//							notificationSet.setVisible(true);
//							notificationSet.expand();	
//							notificationSet.layout(true);
//						}						
//						else
//						{
//							notificationSet.removeAll();
//							notificationSet.setVisible(false);				
//							
//						}
//					}
//				
//				}				
//				
//			}
//		};
//		
//		cbCatalog.ensureDebugId("4834f054-3bd8-4ab0-9e33-ff18dbe0ff0f");
//		cbCatalog.setVisible(true);
//		cbCatalog.setBoxLabel(UIContext.Constants.destinationCatalogLabel());
//		Utils.addToolTip(cbCatalog, UIContext.Constants.destinationCatalogTooltip());
//		cbCatalog.setStyleAttribute("white-space", "normal");
//		cbCatalog.addListener(Events.Change,catalogCheckListener);
//		GRTContainer.add(cbCatalog);
//		
//		
//		notificationSet = new FieldSet();		
//		notificationSet.setHeading(UIContext.Messages.backupSettingsNodifications(1));
//		notificationSet.setCollapsible(true);
//		TableLayout warningLayout = new TableLayout();
//		warningLayout.setWidth("100%");
//		warningLayout.setCellSpacing(1);
//		warningLayout.setColumns(2);
//		notificationSet.setLayout(warningLayout);	
//		if(parentWindow.getRefsVolList() != null)
//		{
//			addWaringIcon();	
//			notificationSet.add(new LabelField(UIContext.Messages.refsVolumesSelect(parentWindow.getRefsVolList())));
//			notificationSet.setVisible(true);
//			notificationSet.expand();		
//		}
//		else
//		{
//			notificationSet.removeAll();
//			notificationSet.setVisible(false);
//		}		
//		GRTContainer.add(notificationSet);	
//		
//		settingsContainer.add(GRTContainer);
//		settingsContainer.add(new Html("<HR>"));
//		//SharePoint Setting
//		//rowContainer.add(new Html("<HR>"));
//		
//		if(!parentWindow.isShowForVSphere()) {
//
//			LayoutContainer tableContainer = new LayoutContainer();
//			TableLayout tableLayout = new TableLayout();
//			tableLayout.setCellPadding(4);
//			tableLayout.setCellSpacing(4);
//			tableLayout.setColumns(2);
//			tableLayout.setWidth("95%");
//			tableContainer.setLayout(tableLayout);
//			
//			LabelField label = new LabelField();
//			label.setText(UIContext.Constants.destinationEnableSharePoint());
//			label.addStyleName("restoreWizardSubItem");
//			TableData data = new TableData();
//			data.setColspan(2);
//			tableContainer.add(label, data);
//			
//	        RadioGroup rgSharePoint = new RadioGroup();
//			
//			enableSharePoint = new Radio();
//			enableSharePoint.ensureDebugId("7759A237-2918-4d99-A68E-A8A18D65D10F");
//			enableSharePoint.setBoxLabel(UIContext.Constants.enable());
//			enableSharePoint.setValue(true);
//			rgSharePoint.add(enableSharePoint);
//			tableContainer.add(enableSharePoint);
//			
//			disableSharePoint = new Radio();
//			disableSharePoint.ensureDebugId("2A72EDE8-A72A-4183-8632-655D2BBA55F6");
//			disableSharePoint.setBoxLabel(UIContext.Constants.disable());
//			disableSharePoint.setValue(false);
//			rgSharePoint.add(disableSharePoint);
//			tableContainer.add(disableSharePoint);
//			
//			// hide SharePoint options
//			tableContainer.setVisible(false);
//			settingsContainer.add(tableContainer);
//		}
////		settingsContainer.add(new Html("<HR>"));
//		panel.add(settingsContainer);
//		rowContainer.add(panel);
//	}
	
	
	public void updateNotificationSet()
	{
		removeNotificationSet();
		if(parentWindow.getRefsVolList() != null && parentWindow.getRefsVolList().length() > 0)
		{
//			if (cbCatalog.getValue())
//			{
//				addWaringIcon();	
//				notificationSet.add(new LabelField(UIContext.Messages.refsVolumesSelect(parentWindow.getRefsVolList())));
//				notificationSet.setVisible(true);
//				notificationSet.expand();	
//				notificationSet.layout(true);
//			}						
		/*	else
			{
				notificationSet.removeAll();
				notificationSet.setVisible(false);				
				
			}*/
		}
		/*else{
			notificationSet.removeAll();
			notificationSet.setVisible(false);	
		}*/
	
	}
	
	public void removeNotificationSet()
	{
//		notificationSet.removeAll();
//		notificationSet.setVisible(false);	
	}
	
//	private void addWaringIcon() {
//		Image warningImage = getWaringIcon();
//		TableData tableData = new TableData();
//		tableData.setStyle("padding: 2px 3px 3px 0px;"); // refer to the GWT default setting.
//		tableData.setVerticalAlign(VerticalAlignment.TOP);
//		notificationSet.add(warningImage, tableData);
//	}
	
//	private Image getWaringIcon() {
//		Image warningImage = AbstractImagePrototype.create(UIContext.IconBundle.logWarning()).createImage();
//		return warningImage;
//	}

	private void addReestablishConnection(){
		DisclosurePanel panel = Utils.getDisclosurePanel(UIContext.Constants.settingsReEstablishConnection());
		LayoutContainer checkDest = new LayoutContainer();
		TableLayout tlayout = new TableLayout();
		tlayout.setColumns(1);
		tlayout.setWidth("100%");
		checkDest.setLayout(tlayout);
		
		connectionButton = new Button(UIContext.Constants.destinationCheck());
		connectionButton.ensureDebugId("4A056C91-A136-4d5d-B38D-940EFEE3E953");
		connectionButton.addListener(Events.OnClick, new Listener<BaseEvent> () {

			@Override
			public void handleEvent(BaseEvent be) {
				final MessageBox box = MessageBox.wait(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D), UIContext.Constants.settingsEstablishConnection(), "");
				Utils.setMessageBoxDebugId(box);
				service.checkDestChainAccess(new BaseAsyncCallback<String> (){

					@Override
					public void onFailure(Throwable caught) {
						if(caught instanceof BusinessLogicException 
								&& ((BusinessLogicException)caught).getErrorCode().equals(netConnError)){
//							MessageBox abox = new MessageBox();
//							abox.setIcon(MessageBox.ERROR);
//							abox.setMessage(UIContext.Constants.settingsCurrentDestError());
//							abox.show();
							//liuyu07 2011-5-11 fix Issue: 20273286
							String currentDestination=parentWindow.getDestination().getPathSelectionPanel().getDestination();
							UpdateUserNamePassWindow passWindow = new UpdateUserNamePassWindow(currentDestination);
							passWindow.setBackupSettingsWindow(parentWindow);
							passWindow.show();
							MessageBox box = MessageBox.info(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D), UIContext.Messages.settingsUpdateConnection(currentDestination), null);
							Utils.setMessageBoxDebugId(box);
							box.close();
						}else {
							box.close();
							super.onFailure(caught);
						}
					}

					@Override
					public void onSuccess(String result) {
						box.close();
						if(result == null || result.isEmpty()) {
							MessageBox box = MessageBox.info(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D), UIContext.Constants.settingsConnectionFine(), null);
							Utils.setMessageBoxDebugId(box);
						}else {
							UpdateUserNamePassWindow passWindow = new UpdateUserNamePassWindow(result);
							passWindow.show();
							passWindow.setModal(true);
							MessageBox box = MessageBox.info(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D), UIContext.Messages.settingsUpdateConnection(result), null);
							Utils.setMessageBoxDebugId(box);
						}
					}
				});
			}
		});
		connectionButton.setMinWidth(100);	
		TableData tdata = new TableData();
		tdata.setHorizontalAlign(HorizontalAlignment.LEFT);
		checkDest.add(connectionButton, tdata);
		
		LabelField des = new LabelField();
		des.setWidth("98%");
		des.setValue(UIContext.Constants.destinationCheckAndUpdate());
		checkDest.add(des);
		
		panel.add(checkDest);
		rowContainer.add(panel);
	}

	private void addBackupAdminAccount() {
		
		DisclosurePanel panel = Utils.getDisclosurePanel(UIContext.Constants.settingsLabelAdministratorAccount());
		
		LayoutContainer settingsContainer = new LayoutContainer();
		
		LabelField label;
		TableData tableData;
//		rowContainer.add(new Html("<HR>"));
		
		/*label = new LabelField();
		label.setText(UIContext.Constants.settingsLabelAdministratorAccount());
		label.addStyleName("restoreWizardSubItem");
		rowContainer.add(label);*/
		
		label = new LabelField();
		label.setValue(UIContext.Constants.settingsLabelBackupAccountDesc());
		settingsContainer.add(label);
		
		LayoutContainer adminContainer = new LayoutContainer();
		TableLayout adminLayout = new TableLayout();
		adminLayout.setColumns(2);
		adminLayout.setCellPadding(4);
		adminLayout.setCellSpacing(0);
		adminContainer.setLayout(adminLayout);
		adminContainer.setStyleAttribute("padding-left", "4px");
		
		label = new LabelField();
		label.setValue(UIContext.Constants.destinationSettingsUserName());
		tableData = new TableData();
		adminContainer.add(label, tableData);
		
		adminUserNameField = new TextField<String>();
		adminUserNameField.ensureDebugId("DFA49755-7B41-45dc-B01F-EDA5D68056F8");
		adminUserNameField.setWidth(MIN_FIELD_WIDTH);
		tableData = new TableData();
		adminContainer.add(adminUserNameField, tableData);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.destinationSettingsPassword());
		tableData = new TableData();
		adminContainer.add(label, tableData);
		
		adminPasswordField = new PasswordTextField();
		adminPasswordField.ensureDebugId("4F658559-9D12-4ae7-AB7D-5509F4E05A53");
		adminPasswordField.setPassword(true);
		adminPasswordField.setWidth(MIN_FIELD_WIDTH);
		tableData = new TableData();
		adminContainer.add(adminPasswordField, tableData);
		
		settingsContainer.add(adminContainer);
		settingsContainer.add(new Html("<HR>"));
		panel.add(settingsContainer);
		rowContainer.add(panel);
	}
	
private void addHardwareSnapshotType() {
		
		DisclosurePanel panel = Utils.getDisclosurePanel(UIContext.Constants.settingsLabelSnapshot());
		
		LayoutContainer settingsContainer = new LayoutContainer();
		
		RadioGroup rg = new RadioGroup();		
		initSoftwareSnapshot(rg);
		settingsContainer.add(softwareSnapshotRadio);

		initHardwareSnapshot(rg);
		settingsContainer.add(hardwareSnapshotRadio);
		
		
		FlexTable hardwareSnapshotOptionsTable = new FlexTable();
		hardwareSnapshotOptionsTable.setSize("240px", "15px");
		
		/*LayoutContainer hardwareSnapshotContainer = new LayoutContainer();
		TableLayout hardwareSnapshotLayout = new TableLayout();
		hardwareSnapshotLayout.setColumns(1);
		hardwareSnapshotLayout.setCellPadding(4);
		hardwareSnapshotLayout.setCellSpacing(0);
		hardwareSnapshotContainer.setLayout(hardwareSnapshotLayout);
		hardwareSnapshotContainer.setStyleAttribute("padding-left", "4px");*/
		
		/*failOverToSoftwareSnapshot = new CheckBox();
		failOverToSoftwareSnapshot.setEnabled(false);
		failOverToSoftwareSnapshot.setValue(true);
		failOverToSoftwareSnapshot.ensureDebugId("91eea874-c6d0-4fcc-a541-541cae0f62c1");
		failOverToSoftwareSnapshot.setBoxLabel(UIContext.Constants.advancedCheckboxFailoverToSoftwareSnapshot());
		failOverToSoftwareSnapshot.setStyleAttribute("padding-left", "25px");
		failOverToSoftwareSnapshot.setToolTip(UIContext.Constants.advancedCheckBoxFailoverToSoftwareSnapshotTooltip());*/
	
		//hardwareSnapshotOptionsTable.setWidget(0, 0, failOverToSoftwareSnapshot);
		/*hardwareSnapshotOptionsTable.getCellFormatter().setVerticalAlignment(0, 0,
				HasVerticalAlignment.ALIGN_BOTTOM);*/

		useTransportableSnapshot = new CheckBox();
		useTransportableSnapshot.setEnabled(false);
		useTransportableSnapshot.ensureDebugId("b1877ca6-699e-4591-8b9d-b1c78fdcf90d");
		useTransportableSnapshot.setBoxLabel(UIContext.Constants.advancedCheckboxUseTransportableSnapshot());
		useTransportableSnapshot.setStyleAttribute("padding-left", "25px");
		useTransportableSnapshot.setToolTip(UIContext.Constants.advancedCheckBoxUseTransportableSnapshotTooltip());
		
		hardwareSnapshotOptionsTable.setWidget(0, 0, useTransportableSnapshot);
		hardwareSnapshotOptionsTable.getCellFormatter().setVerticalAlignment(0, 0,
				HasVerticalAlignment.ALIGN_TOP);
		
		hardwareSnapshotOptionsTable.setWidget(2, 0, new Label());
		settingsContainer.add(hardwareSnapshotOptionsTable);
		
		LayoutContainer snapshotContainer = new LayoutContainer();
		TableLayout snapshotLayout = new TableLayout();
		snapshotLayout.setColumns(1);
		snapshotLayout.setCellPadding(4);
		snapshotLayout.setCellSpacing(0);
		snapshotContainer.setLayout(snapshotLayout);
		snapshotContainer.setStyleAttribute("padding-left", "4px");
		
		settingsContainer.add(snapshotContainer);
		settingsContainer.add(new Html("<HR>"));
		panel.add(settingsContainer);
		rowContainer.add(panel);
	}

	private void initSoftwareSnapshot(RadioGroup rg) {
		softwareSnapshotRadio = new Radio();
		softwareSnapshotRadio.ensureDebugId("5f09d390-00d8-4cda-8549-596766aa5c8c");
		softwareSnapshotRadio.setBoxLabel(UIContext.Constants.advancedSoftwareSnapshotRadioButton());
		softwareSnapshotRadio.setValue(true);		
		softwareSnapshotRadio.addListener(Events.Change, new Listener<FieldEvent>() {
	
			@Override
			public void handleEvent(FieldEvent be) {
				if(softwareSnapshotRadio.getValue())
				{
					hardwareSnapshotRadio.setValue(false);
					//failOverToSoftwareSnapshot.setEnabled(false);
					useTransportableSnapshot.setEnabled(false);
				}
			}
	
		});
		rg.add(softwareSnapshotRadio);
	}

	private void initHardwareSnapshot(RadioGroup rg) {
		hardwareSnapshotRadio = new Radio();
		hardwareSnapshotRadio.ensureDebugId("23379f8d-87ec-4324-952a-6403517b27d8");
		hardwareSnapshotRadio.setBoxLabel(UIContext.Constants.advancedHardwareSnapshotRadioButton());
		hardwareSnapshotRadio.setToolTip(UIContext.Constants.advancedHardwareSnapshotRadioButtonToolTip());
		hardwareSnapshotRadio.setValue(false);
		hardwareSnapshotRadio.addListener(Events.Change, new Listener<FieldEvent>() {
	
			@Override
			public void handleEvent(FieldEvent be) {
				if(hardwareSnapshotRadio.getValue()) {
						hardwareSnapshotRadio.setValue(true);
						//failOverToSoftwareSnapshot.setEnabled(true);
						useTransportableSnapshot.setEnabled(false);
				}
			}
	
		});
		rg.add(hardwareSnapshotRadio);
	}

	public void RefreshData(BackupSettingsModel model, boolean isEdit) {
		
		if (model!=null){
			GWT.log(String.valueOf(model.getPurgeSQLLogDays()), null);
			GWT.log(String.valueOf(model.getPurgeExchangeLogDays()), null);
			
			if(model.isSoftwareOrHardwareSnapshotType() != null)
			{
				if(model.isSoftwareOrHardwareSnapshotType())
				{
					softwareSnapshotRadio.setValue(true);
					hardwareSnapshotRadio.setValue(false);
					useTransportableSnapshot.setEnabled(false); // For agent based it is always disabled
					//failOverToSoftwareSnapshot.setEnabled(false);
				}
				else
				{
					hardwareSnapshotRadio.setValue(true);
					softwareSnapshotRadio.setValue(false);
					useTransportableSnapshot.setEnabled(false); // For agent based it is always disabled
					//failOverToSoftwareSnapshot.setEnabled(true);
				}
				useTransportableSnapshot.setValue(model.isUseTransportableSnapshot());
				//failOverToSoftwareSnapshot.setValue(model.isFailoverToSoftwareSnapshot());
			}
			
			if(isEdit){
				Integer bkpDataFormat = model.getBackupDataFormat();
				if(bkpDataFormat !=null && bkpDataFormat == 1){
					this.preAllocationPanel.setVisible(false);
				}else{
					this.preAllocationPanel.setVisible(true);
				}
			}else{
				this.preAllocationPanel.setVisible(false);
			}
			
			if (model.getPurgeSQLLogDays() == 0){
				purgeSQLCheckBox.setValue(false);
				purgeSQLComboBox.setEnabled(false);
			}else{
				purgeSQLCheckBox.setValue(true);
				if (model.getPurgeSQLLogDays() == 1)
					purgeSQLComboBox.setSimpleValue(UIContext.Constants.settingsPurgeLogDaily());
				else if (model.getPurgeSQLLogDays() == 7)
					purgeSQLComboBox.setSimpleValue(UIContext.Constants.settingsPurgeLogWeekly());
				else
					purgeSQLComboBox.setSimpleValue(UIContext.Constants.settingsPurgeLogMonthly());
			}
			
			
			if (model.getPurgeExchangeLogDays() == 0){
				purgeExchangeCheckBox.setValue(false);
				purgeExchangeComboBox.setEnabled(false);
			}else{
				purgeExchangeCheckBox.setValue(true);
				if (model.getPurgeExchangeLogDays() == 1)
					purgeExchangeComboBox.setSimpleValue(UIContext.Constants.settingsPurgeLogDaily());
				else if (model.getPurgeExchangeLogDays() == 7)
					purgeExchangeComboBox.setSimpleValue(UIContext.Constants.settingsPurgeLogWeekly());
				else
					purgeExchangeComboBox.setSimpleValue(UIContext.Constants.settingsPurgeLogMonthly());
			}
			
			if(model.getPreAllocationValue() != null) {
				Integer value = model.getPreAllocationValue();				
				
				if(preAllocationComboBox.findModel(value) == null){
					preAllocationComboBox.getStore().setStoreSorter(new StoreSorter<SimpleComboValue<Integer>>());
					preAllocationComboBox.add(value);
					preAllocationComboBox.getStore().sort("value",SortDir.ASC);
				}				
				
				preAllocationComboBox.setSimpleValue(model.getPreAllocationValue());
			}
			
			if(!parentWindow.isShowForVSphere()) {
//				Long exchangeGRTSetting = model.getExchangeGRTSetting();
//				if (exchangeGRTSetting == EXCHANGE_GRT_ENABLE_BEFORE_RESTORE.longValue())
//					this.checkboxGenerateGRTCatalog.setValue(false);
//				else
//					this.checkboxGenerateGRTCatalog.setValue(true);
				
//				//set SharePoint support
//				if(model.getSharePointGRTSetting() != null && model.getSharePointGRTSetting() == SharePoint_GRT_ENABLE.longValue()){
//					enableSharePoint.setValue(true);
//				}else{
//					disableSharePoint.setValue(true);
//				}
				
//				checkExchangeService();
				//set administrator account
				adminUserNameField.setValue(model.getAdminUserName());
				adminPasswordField.setValue(model.getAdminPassword());
			}
			
//			if(model != null && model.getGenerateCatalog() != null){
//				cbCatalog.setValue(model.getGenerateCatalog());
//			}
		}
		if(!parentWindow.isShowForVSphere()) {
			updateNotificationSet();	
		}
	}
	
	public void Save()
	{
		try
		{
			if (purgeSQLCheckBox.getValue()){
				if (purgeSQLComboBox.getSimpleValue() == UIContext.Constants.settingsPurgeLogDaily())
					SettingPresenter.model.setPurgeSQLLogDays(PURGE_LOG_DAILY);
				else if (purgeSQLComboBox.getSimpleValue() == UIContext.Constants.settingsPurgeLogWeekly())
					SettingPresenter.model.setPurgeSQLLogDays(PURGE_LOG_WEEKLY);
				else
					SettingPresenter.model.setPurgeSQLLogDays(PURGE_LOG_MONTHLY);
			}else
				SettingPresenter.model.setPurgeSQLLogDays(PURGE_LOG_NONE);
			
			if (purgeExchangeCheckBox.getValue()){
				if (purgeExchangeComboBox.getSimpleValue() == UIContext.Constants.settingsPurgeLogDaily())
					SettingPresenter.model.setPurgeExchangeLogDays(PURGE_LOG_DAILY);
				else if (purgeExchangeComboBox.getSimpleValue() == UIContext.Constants.settingsPurgeLogWeekly())
					SettingPresenter.model.setPurgeExchangeLogDays(PURGE_LOG_WEEKLY);
				else
					SettingPresenter.model.setPurgeExchangeLogDays(PURGE_LOG_MONTHLY);
			}else
				SettingPresenter.model.setPurgeExchangeLogDays(PURGE_LOG_NONE);
			
			SettingPresenter.model.setPreAllocationValue(preAllocationComboBox.getSimpleValue());
			
			if(!parentWindow.isShowForVSphere()) {
//	            if (Boolean.TRUE.equals(this.checkboxGenerateGRTCatalog.getValue()))
//					SettingPresenter.model.setExchangeGRTSetting(EXCHANGE_GRT_ENABLE_AFTER_BACKUP);
//				else
//					SettingPresenter.model.setExchangeGRTSetting(EXCHANGE_GRT_ENABLE_BEFORE_RESTORE);
							
//				if(Boolean.TRUE.equals(enableSharePoint.getValue())){
//					SettingPresenter.model.setSharePointGRTSetting(SharePoint_GRT_ENABLE);
//				}else{
//					SettingPresenter.model.setSharePointGRTSetting(SharePoint_GRT_DISABLE);
//				}
	
				SettingPresenter.model.setAdminUserName(adminUserNameField.getValue());
				SettingPresenter.model.setAdminPassword(adminPasswordField.getValue());
			}
			
			if (softwareSnapshotRadio.getValue())
				SettingPresenter.model.setSoftwareOrHardwareSnapshotType(true);
			else
				SettingPresenter.model.setSoftwareOrHardwareSnapshotType(false);
			
			/*if (failOverToSoftwareSnapshot.getValue())
				SettingPresenter.model.setFailoverToSoftwareSnapshot(true);
			else
				SettingPresenter.model.setFailoverToSoftwareSnapshot(false);*/
			
			// setting FailoverToSofwareSnapshot to true always
			SettingPresenter.model.setFailoverToSoftwareSnapshot(true);
			
			if (useTransportableSnapshot.getValue())
				SettingPresenter.model.setUseTransportableSnapshot(true);
			else
				SettingPresenter.model.setUseTransportableSnapshot(false);
			
//			SettingPresenter.model.setGenerateCatalog(cbCatalog.getValue());
		}
		catch (Exception e)
		{
			
		}		
	}
	public boolean Validate()
	{
		if(parentWindow.isShowForVSphere())
			return true;
		
		// Edge need not to input administrator user name
		if (parentWindow.isForEdge()) {
			return true;
		}
		
		if(adminUserNameField.getValue() == null || adminUserNameField.getValue().length() == 0)
		{
			MessageBox msg = new MessageBox();
			msg.setIcon(MessageBox.ERROR);
			msg.setTitleHtml(UIContext.Constants.backupSettingsSettings());
			msg.setMessage(UIContext.Constants.settingsErrorAdministratorAccount());
			msg.getDialog().getButtonById(Dialog.OK).ensureDebugId("5E102567-0910-4c48-B2AC-BB0D48A5AFBF");
			msg.setModal(true);
			msg.show();
			
			return false;
		}
		return true;
	}	
	
	private boolean isEditable = true;
	
	public void setEditable(boolean isEditable){
		this.isEditable = isEditable;
		purgeSQLCheckBox.setEnabled(isEditable);
		purgeExchangeCheckBox.setEnabled(isEditable);
		purgeSQLComboBox.setEnabled(isEditable);
		purgeExchangeComboBox.setEnabled(isEditable);
		preAllocationComboBox.setEnabled(isEditable);
		softwareSnapshotRadio.setEnabled(isEditable);
		hardwareSnapshotRadio.setEnabled(isEditable);
		useTransportableSnapshot.setEnabled(isEditable); // For agent based it is always disabled
		//failOverToSoftwareSnapshot.setEnabled(isEditable);
//		cbCatalog.setEnabled(isEditable);
		if(!parentWindow.isShowForVSphere()) {
//			checkboxGenerateGRTCatalog.setEnabled(isEditable);
			adminUserNameField.setEnabled(isEditable);
			adminPasswordField.setEnabled(isEditable);

// Always show in D2D, not show in Edge, for issue 20359081
//			if(connectionButton!=null)
//				connectionButton.setEnabled(isEditable);
		}
		
	}
	
//	public void setDoCatlaogValue(boolean enable){
//		if(!enable)
//			cbCatalog.setValue(enable);
//		cbCatalog.setEnabled(enable);
//	}
//	
//	public CheckBox getCatalogCheckBox(){
//		return this.cbCatalog;
//	}
	
//	public void checkExchangeService(){
//		// check if Exchange Service is installed
//		service.checkServiceStatus("msexchangeis", new BaseAsyncCallback<Long>()
//		{
//
//			@Override
//			public void onFailure(Throwable caught)
//			{
//				checkboxGenerateGRTCatalog.setEnabled(false);
//				super.onFailure(caught);
//			}
//
//			@Override
//			public void onSuccess(Long result)
//			{
//				if (result != null)
//				{
//					// Exchange Service is installed
//					// if for edge this option should be always enabled
//					if (((result.intValue() & 0x01) == 0x01) || parentWindow.isForEdge())
//					{
//						if(isEditable){
//							checkboxGenerateGRTCatalog.setEnabled(true);
//						}else{
//							checkboxGenerateGRTCatalog.setEnabled(false);
//						}
////						checkboxGenerateGRTCatalog.setValue(true);
//						
//					}else{
//						checkboxGenerateGRTCatalog.setEnabled(false);
//						checkboxGenerateGRTCatalog.setValue(false);
//					}
//				
//				}else{
//					checkboxGenerateGRTCatalog.setEnabled(false);
//					checkboxGenerateGRTCatalog.setValue(false);
//				}
//				
//				super.onSuccess(result);
//			}
//
//		});
//	}
}
