package com.ca.arcflash.ui.client.vsphere.vmbackup;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseSimpleComboBox;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.ca.arcflash.ui.client.model.VMBackupSettingModel;
import com.ca.arcflash.ui.client.model.VSphereBackupSettingModel;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;

public class BackupSettings {
	
	public static final Long PURGE_LOG_NONE		=	0L;
	public static final Long PURGE_LOG_DAILY	=	1L;
	public static final Long PURGE_LOG_WEEKLY	=	7L;
	public static final Long PURGE_LOG_MONTHLY	=	30L;
	
//	private NumberField retentionCount;	
	private LayoutContainer rowContainer;
//	private SimpleComboBox<String>  compressionOption;	
	private VMBackupSettingWindow parentWindow;
	
	private CheckBox purgeSQLCheckBox;
	private CheckBox purgeExchangeCheckBox;
	
	private BaseSimpleComboBox<String> purgeSQLComboBox;
	private BaseSimpleComboBox<String> purgeExchangeComboBox;
	
	private static int MIN_FIELD_WIDTH = 250;
	
	public BackupSettings(VMBackupSettingWindow w)
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
		tl.setHeight("50%");
		rowContainer.setLayout(tl);
						
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.backupSettingsSettings());
		label.addStyleName("restoreWizardTitle");
		rowContainer.add(label);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.settingsLabelPurgeLog());
		label.addStyleName("restoreWizardSubItem");
		rowContainer.add(label);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.settingsLabelPurgeLogDecription());
		rowContainer.add(label);
		
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
		purgeSQLCheckBox.ensureDebugId("80D113E9-F44D-4c1b-AFC5-AB2870BC6731");
		purgeSQLCheckBox.setValue(true);
		purgeSQLCheckBox.setBoxLabel(UIContext.Constants.settingsCheckBoxPurgeSQL());
		purgeSQLCheckBox.addListener(Events.Change, new Listener<FieldEvent>()
				{
					@Override
					public void handleEvent(FieldEvent be) {
						purgeSQLComboBox.setEnabled(purgeSQLCheckBox.getValue());		
					}
			
				});
		purgeContainer.add(purgeSQLCheckBox);
		
		purgeSQLComboBox = new BaseSimpleComboBox<String>();
		
		purgeSQLComboBox.ensureDebugId("E3CB85A9-0DAF-4a72-961B-CCF7F5A0C724");		
		purgeSQLComboBox.setAutoWidth(true);
		purgeSQLComboBox.setEditable(false);
		purgeSQLComboBox.add(UIContext.Constants.settingsPurgeLogDaily());
		purgeSQLComboBox.add(UIContext.Constants.settingsPurgeLogWeekly());
		purgeSQLComboBox.add(UIContext.Constants.settingsPurgeLogMonthly());
		purgeSQLComboBox.setSimpleValue(UIContext.Constants.settingsPurgeLogWeekly());
		purgeContainer.add(purgeSQLComboBox,tableData);
		
		purgeExchangeCheckBox = new CheckBox();
		purgeExchangeCheckBox.ensureDebugId("4E681C2E-7785-456c-AECC-425155F83ED0");
		purgeExchangeCheckBox.setVisible(true);
		purgeExchangeCheckBox.setBoxLabel(UIContext.Constants.settingsCheckBoxPurgeExchange());
		purgeExchangeCheckBox.addListener(Events.Change, new Listener<FieldEvent>()
				{
					@Override
					public void handleEvent(FieldEvent be) {
						purgeExchangeComboBox.setEnabled(purgeExchangeCheckBox.getValue());		
					}
			
				});
		purgeContainer.add(purgeExchangeCheckBox);
		
		purgeExchangeComboBox = new BaseSimpleComboBox<String>();
		purgeExchangeComboBox.ensureDebugId("5031A204-E917-4572-9875-5C2607EFAE11");
		purgeExchangeComboBox.setEditable(false);
		purgeExchangeComboBox.add(UIContext.Constants.settingsPurgeLogDaily());
		purgeExchangeComboBox.add(UIContext.Constants.settingsPurgeLogWeekly());
		purgeExchangeComboBox.add(UIContext.Constants.settingsPurgeLogMonthly());
		purgeExchangeComboBox.setSimpleValue(UIContext.Constants.settingsPurgeLogWeekly());
		purgeContainer.add(purgeExchangeComboBox,tableData);
		
		rowContainer.add(purgeContainer);
		
		return rowContainer;
	}

	public void RefreshData(VMBackupSettingModel model) {
		
		if (model!=null){
			GWT.log(String.valueOf(model.getPurgeSQLLogDays()), null);
			GWT.log(String.valueOf(model.getPurgeExchangeLogDays()), null);
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
			
			
		}
	}
	
}
