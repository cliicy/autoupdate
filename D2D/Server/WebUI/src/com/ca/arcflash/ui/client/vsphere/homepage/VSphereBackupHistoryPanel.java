package com.ca.arcflash.ui.client.vsphere.homepage;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.IRefreshable;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.homepage.BackupHistoryPanel;
import com.ca.arcflash.ui.client.model.RecoveryPointModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class VSphereBackupHistoryPanel extends BackupHistoryPanel implements IRefreshable{
	
	@Override
	protected void onRender(Element parent, int index)
	{
		super.onRender(parent, index);		
		
		datePicker.ensureDebugId("90599BE6-AFDB-4ec6-8163-FCEA38188F40");
	}
	
	@Override
	protected List<ColumnConfig> createColumnConfigList()
	{
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		configs.add(Utils.createColumnConfig("BackupStatus", UIContext.Constants.homepageRecentBackupColumnStatusHeader(), 45, statusRenderer));
		configs.add(Utils.createColumnConfig("Type", UIContext.Constants.homepageRecentBackupColumnSchedTypeHeader(), 80, schedTypeRenderer));
		configs.add(Utils.createColumnConfig("backupType", UIContext.Constants.homepageRecentBackupColumnTypeHeader(), 115, typeRenderer));
		configs.add(Utils.createColumnConfig("Time", UIContext.Constants.homepageRecentBackupColumnDateTimeHeader(), 125, timeRenderer));
		configs.add(Utils.createColumnConfig("logicalSize", UIContext.Constants.homepageRecentBackupColumnLogicalSizeHeader(), 95, logicalSizeRenderer));
		configs.add(Utils.createColumnConfig("dataSize", UIContext.Constants.homepageRecentBackupColumnSizeHeader(), 95, sizeRenderer));
		configs.add(Utils.createColumnConfig("Name", UIContext.Constants.homepageRecentBackupColumnNameHeader(), 80, nameRenderer));
		
		return configs;
	}
	
	@Override
	protected void getRecentBackupsByServerTime(int type, int status, String beginDate, String endDate, boolean needCatalogStatus, AsyncCallback<RecoveryPointModel[]> callback){
		service.getVMRecentBackupsByServerTime(type, status, beginDate, endDate, needCatalogStatus, UIContext.backupVM, callback);
	}
}
