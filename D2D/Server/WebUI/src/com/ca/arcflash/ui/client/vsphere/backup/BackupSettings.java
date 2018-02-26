package com.ca.arcflash.ui.client.vsphere.backup;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseSimpleComboBox;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
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
	private VSphereBackupSettingWindow parentWindow;
	
	private CheckBox purgeSQLCheckBox;
	private CheckBox purgeExchangeCheckBox;
	
	private BaseSimpleComboBox<String> purgeSQLComboBox;
	private BaseSimpleComboBox<String> purgeExchangeComboBox;
	
	private static int MIN_FIELD_WIDTH = 250;
	
	public BackupSettings(VSphereBackupSettingWindow w)
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
		purgeSQLCheckBox.ensureDebugId("9ED9EF41-013E-441b-BFFD-7F7F90B7537A");
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
		purgeSQLComboBox.ensureDebugId("FE33F446-475C-4f90-8059-EFE3CC19DEC9");		
		
		purgeSQLComboBox.setAutoWidth(true);
		purgeSQLComboBox.setEditable(false);
		purgeSQLComboBox.add(UIContext.Constants.settingsPurgeLogDaily());
		purgeSQLComboBox.add(UIContext.Constants.settingsPurgeLogWeekly());
		purgeSQLComboBox.add(UIContext.Constants.settingsPurgeLogMonthly());
		purgeSQLComboBox.setSimpleValue(UIContext.Constants.settingsPurgeLogWeekly());
		purgeContainer.add(purgeSQLComboBox,tableData);
		
		purgeExchangeCheckBox = new CheckBox();
		purgeExchangeCheckBox.ensureDebugId("29032AB3-8D4B-4f91-A31A-1F8302E17E76");
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
		purgeExchangeComboBox.ensureDebugId("C370D0EB-AEAA-46d8-9F6D-C72E1BD090D7");
		purgeExchangeComboBox.setEditable(false);
		purgeExchangeComboBox.add(UIContext.Constants.settingsPurgeLogDaily());
		purgeExchangeComboBox.add(UIContext.Constants.settingsPurgeLogWeekly());
		purgeExchangeComboBox.add(UIContext.Constants.settingsPurgeLogMonthly());
		purgeExchangeComboBox.setSimpleValue(UIContext.Constants.settingsPurgeLogWeekly());
		purgeContainer.add(purgeExchangeComboBox,tableData);
		
		rowContainer.add(purgeContainer);
		
		return rowContainer;
	}

	public void RefreshData(VSphereBackupSettingModel model) {
		
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
	
	public void Save()
	{
		try
		{
			//parentWindow.model.setEnableEncryption(false);
			
			if (purgeSQLCheckBox.getValue()){
				if (purgeSQLComboBox.getSimpleValue() == UIContext.Constants.settingsPurgeLogDaily())
					parentWindow.model.setPurgeSQLLogDays(PURGE_LOG_DAILY);
				else if (purgeSQLComboBox.getSimpleValue() == UIContext.Constants.settingsPurgeLogWeekly())
					parentWindow.model.setPurgeSQLLogDays(PURGE_LOG_WEEKLY);
				else
					parentWindow.model.setPurgeSQLLogDays(PURGE_LOG_MONTHLY);
			}else
				parentWindow.model.setPurgeSQLLogDays(PURGE_LOG_NONE);
			
			if (purgeExchangeCheckBox.getValue()){
				if (purgeExchangeComboBox.getSimpleValue() == UIContext.Constants.settingsPurgeLogDaily())
					parentWindow.model.setPurgeExchangeLogDays(PURGE_LOG_DAILY);
				else if (purgeExchangeComboBox.getSimpleValue() == UIContext.Constants.settingsPurgeLogWeekly())
					parentWindow.model.setPurgeExchangeLogDays(PURGE_LOG_WEEKLY);
				else
					parentWindow.model.setPurgeExchangeLogDays(PURGE_LOG_MONTHLY);
			}else
				parentWindow.model.setPurgeExchangeLogDays(PURGE_LOG_NONE);
			
		}
		catch (Exception e)
		{
			
		}		
	}
	public boolean Validate()
	{
		return true;
	}
	
}
