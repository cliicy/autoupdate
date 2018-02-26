package com.ca.arcflash.ui.client.export;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.BackupSettingUtil;
import com.ca.arcflash.ui.client.backup.EncryptionPane;
import com.ca.arcflash.ui.client.common.BaseSimpleComboBox;
import com.ca.arcflash.ui.client.common.IConstants;
import com.ca.arcflash.ui.client.common.PathSelectionPanel;
import com.ca.arcflash.ui.client.common.UncPath;
import com.ca.arcflash.ui.client.common.UserPasswordWindow;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.ScheduledExportSettingsModel;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Widget;

public class ScheduledExportSettings {
	private static final int MINBACKUPNUM = 1; 
	public static final int DEFAULTEXPORTINTERVAL = 8;
	public static final int DEFAULTKEEPRP = 1;
	
	private LabelField destination;
	
	CheckBox enableSchedule;
	
	private SimpleComboBox<String> compressionOption;
	
	private NumberField exportInterval;
	private NumberField keepField;
	private EncryptionPane encryptionPane;
	protected PathSelectionPanel pathSelection;
	
	private ScheduledExportSettingsContent parentWindow;
	
	public ScheduledExportSettings(ScheduledExportSettingsContent parentWindow) {
		this.parentWindow = parentWindow;
	}
	
	public Widget render() {
		
		
		DisclosurePanel disPanel = Utils.getDisclosurePanel(UIContext.Constants.scheduledExportSettings());
		
		LayoutContainer disContainer = new LayoutContainer();
		
		LayoutContainer container = new LayoutContainer();
		TableLayout tableLayout = new TableLayout();
		tableLayout.setWidth("100%");
		tableLayout.setTableStyle("padding:10px");
		container.setLayout(tableLayout);
		TableData tableData = new TableData();
		container.add(getEnableScheduleCheckBox());
		
		tableData.setStyle("padding-left: 10px; padding-top: 20px");
		
		container.add(getScheduledSettingsContainer(), tableData);
//		container.add(new HTML("<BR>"));
		
		encryptionPane = new EncryptionPane();
		tableData = new TableData();
		tableData.setStyle("padding-left: 10px; padding-top: 20px");
		// tableData.setPadding(5);
		container.add(encryptionPane, tableData);
		
//		VerticalPanel panel = new VerticalPanel();
//		getEnableScheduleCheckBox();
//		getScheduledSettingsContainer();
//		encryptionPane = new EncryptionPane();
//		
//		panel.add(new HTML("<HR>"));
//		panel.add(encryptionPane);
//		panel.add(new HTML("<HR>"));
		
		disContainer.add(container);
		disPanel.add(disContainer);
		
		return disPanel;
	}
	
	private Widget getEnableScheduleCheckBox() {
		enableSchedule = new CheckBox();
		enableSchedule.ensureDebugId("c632ac12-bc80-47bb-beb2-c79acf707187");
		enableSchedule.setBoxLabel(UIContext.Constants.EnableScheduledExport());
		enableSchedule.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				boolean enabled = enableSchedule.getValue();
				setEnabledScheduledSettingsField(enabled);
				if(!enabled) {
					exportInterval.clearInvalid();
					keepField.clearInvalid();
//					exportInterval.setValue(DEFAULTEXPORTINTERVAL);
//					keepField.setValue(DEFAULTKEEPRP);
				} else {
					exportInterval.validate();
					keepField.validate();
				}
			}
		});
		
		return enableSchedule;
	}
	
	private void setEnabledScheduledSettingsField(boolean enabled) {
		exportInterval.setEnabled(enabled);
		keepField.setEnabled(enabled);
		pathSelection.setEnabled(enabled);
		compressionOption.setEnabled(enabled);
		encryptionPane.setEnabled(enabled);
		// operate specially for VHD.
		if(enabled) {
			String selString = compressionOption.getSimpleValue();
			if(UIContext.Constants.settingsNoCompressionVHD().equalsIgnoreCase(selString)) {
				encryptionPane.disable();
			}
		}
	}
	
	private LayoutContainer	getScheduledSettingsContainer() {
		LayoutContainer container = new LayoutContainer();
		TableLayout layout = new TableLayout();
		layout.setCellPadding(2);
		layout.setCellSpacing(2);
		layout.setColumns(2);
//		 layout.setWidth("100%");
		container.setLayout(layout);
		
		// Destination
		destination = new LabelField();
		destination.setValue(UIContext.Constants.Destination());
		TableData tableData = new TableData();
		tableData.setWidth("14%");
		container.add(destination, tableData);

		pathSelection = new PathSelectionPanel(null, true);

		pathSelection.setPathFieldLength(305);
		pathSelection.setMode(PathSelectionPanel.COPY_MODE);
		pathSelection.setTooltipMode(PathSelectionPanel.TOOLTIP_COPY_MODE);	
		pathSelection.addDebugId("6AB61928-CEC0-4b37-B8CB-EF8D85F9ED10", 
				"7E66679A-3546-4670-B79B-59D71E8B9180", 
				"8F678EBC-D090-472a-A110-D21F5BBE2876");
		
		tableData = new TableData();
		tableData.setWidth("86%");
		tableData.setStyle("padding-left:9px;");
		container.add(pathSelection, tableData);
		
		// copy after
		LabelField beforeLabel = new LabelField();
		beforeLabel.setValue(UIContext.Constants.ExportAfter());
		tableData = new TableData();
		tableData.setColspan(2);
		container.add(beforeLabel, tableData);

		tableData = new TableData();
		tableData.setWidth("14%");
		LabelField lf = new LabelField();		
		container.add(lf, tableData);
		
		exportInterval = new NumberField();
		exportInterval.ensureDebugId("493a51d4-2915-4832-aee6-43c55e6699bb");
		exportInterval.setAllowBlank(false);
		exportInterval.setAllowDecimals(false);
		exportInterval.setAllowNegative(false);
		exportInterval.setMinValue(MINBACKUPNUM);
//		exportInterval.setMaxValue(1344);
		exportInterval.setMaxValue(UIContext.maxRPLimitDEFAULT);
		exportInterval.setWidth(100);
		//exportInterval.setStyleAttribute("margin-left", "1px");
		Utils.addToolTip(exportInterval, UIContext.Constants.scheduledExportToolTipBackupNumber());
		tableData = new TableData();
		tableData.setStyle("padding-left:13px;");
		tableData.setColspan(1);
		container.add(exportInterval, tableData);
		
		// keep
		LabelField keepLabel = new LabelField();
		keepLabel.setValue(UIContext.Constants.Keep());
		tableData = new TableData();
		tableData.setColspan(2);
		container.add(keepLabel, tableData);
		
		tableData = new TableData();
		tableData.setWidth("14%");
		lf = new LabelField();		
		container.add(lf, tableData);
		
		keepField = new NumberField();
		keepField.ensureDebugId("0a2de97c-c9e0-4733-a6fa-8fa45bcba97e");
		keepField.setAllowBlank(false);
		keepField.setAllowDecimals(false);
		keepField.setAllowNegative(false);
		keepField.setMinValue(1);
//		keepField.setMaxValue(1344);
		keepField.setMaxValue(UIContext.maxRPLimitDEFAULT);
		keepField.setWidth(100);
		//keepField.setStyleAttribute("margin-left", "1px");
		Utils.addToolTip(keepField, UIContext.Messages.scheduledExportToolTipRecoveryPoint(UIContext.productNameD2D));
		tableData = new TableData();
		tableData.setColspan(1);
		tableData.setStyle("padding-left:13px;");
		container.add(keepField, tableData);
		
		// format
		LabelField formatLabel = new LabelField();
		formatLabel.setValue(UIContext.Constants.Format());
		tableData = new TableData();
		tableData.setWidth("14%");
		container.add(formatLabel, tableData);
		
		compressionOption = new BaseSimpleComboBox<String>();
		compressionOption.ensureDebugId("18f45e2a-509b-4610-b990-8784e6bf96bc");
		compressionOption.setEditable(false);
		compressionOption.setWidth(200);
		compressionOption.add(UIContext.Constants.settingsCompressionNone());
		compressionOption.add(UIContext.Constants.settingsNoCompressionVHD());
		compressionOption.add(UIContext.Constants.settingsCompreesionStandard());
		compressionOption.add(UIContext.Constants.settingsCompressionMax());
		compressionOption.setSimpleValue(UIContext.Constants.settingsCompreesionStandard());
		Utils.addToolTip(compressionOption, UIContext.Constants.settingsLabelCompressionStandardTooltip());
		compressionOption.addListener(Events.Select, new SelectionListener<ComponentEvent>() {

			@Override
			public void componentSelected(ComponentEvent ce) {
				String selString = compressionOption.getSimpleValue();
				if(UIContext.Constants.settingsNoCompressionVHD().equalsIgnoreCase(selString)) {
					encryptionPane.disable();
				} else {
					encryptionPane.enable();
				}
				
				if (selString.compareTo(UIContext.Constants.settingsCompressionNone()) == 0){
					Utils.addToolTip(compressionOption, UIContext.Constants.settingsLabelCompressionNoneTooltip());
				}else if (selString.compareTo(UIContext.Constants.settingsCompreesionStandard()) == 0){
					Utils.addToolTip(compressionOption, UIContext.Constants.settingsLabelCompressionStandardTooltip());
				}else if (selString.compareTo(UIContext.Constants.settingsCompressionMax()) == 0){
					Utils.addToolTip(compressionOption, UIContext.Constants.settingsLabelCompressionMaxTooltip());
				}else if( selString.compareTo(UIContext.Constants.settingsNoCompressionVHD()) == 0) {
					Utils.addToolTip(compressionOption, UIContext.Constants.settingsLabelCompressionVHD());
				}
			}
		});
		tableData = new TableData();
		tableData.setStyle("padding-left:13px;padding-top:5px");
		// tableData.setStyle("padding-left:10px;");
		container.add(compressionOption, tableData);
		
		return container;
	}
	
	public boolean validate() {
		boolean isValid = true;
		String title = null;
		String msgStr = null;
		
		if(!enableSchedule.getValue()) {
			return true;
		}
		
		Number exportAfterNum = exportInterval.getValue();
		if(exportAfterNum==null || exportAfterNum.intValue()<MINBACKUPNUM) {
			title = UIContext.Constants.scheduledExportSettings();
			msgStr = UIContext.Constants.exportIntervalTooLow();
			isValid = false;
		} else if(exportAfterNum.intValue()>UIContext.maxRPLimit) {
			title = UIContext.Constants.scheduledExportSettings();
			msgStr = UIContext.Messages.settingsExportIntervalExceedMax(UIContext.maxRPLimit);
			isValid = false;
		}
		
		if (isValid) {
			if (pathSelection.getDestination() == null
					|| pathSelection.getDestination().trim().length() == 0) {
				title = UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D);
				msgStr = UIContext.Constants.scheduledExportSettingsDestinationCannotBeBlank();
				isValid = false;
			}
		}
		
		if(isValid) {
			Number keepNum = keepField.getValue();
			if(keepNum==null || keepNum.intValue()==0) {
				title = UIContext.Constants.scheduledExportSettings();
				msgStr = UIContext.Constants.keepRecoveryPointsCountTooLow();
				isValid = false;
			} else if(keepNum.intValue()>UIContext.maxRPLimit) {
				title = UIContext.Constants.scheduledExportSettings();
				msgStr = UIContext.Messages.keepRecoveryPointsCountExceedMax(UIContext.maxRPLimit);
				isValid = false;
			}
		}
		
		if(isValid) {
			if(pathSelection.getDestination().trim()
					.equals(BackupSettingUtil.getInstance().getBackupDestination())){
				title = UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D);
				msgStr = UIContext.Constants.settingCopyDestSameBkDestErr();
				isValid = false;
			}	
		}

		if(isValid) {
			String path=pathSelection.getDestination();
			if(!PathSelectionPanel.isLocalPathValid(path)){
				title = UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D);
				msgStr = UIContext.Constants.scheduledExportSettingsDestinationError();
				isValid = false;
			}	
		}
		
		if(isValid) {
			if(encryptionPane.isEnabled() && !encryptionPane.validate()) {
				return false;
			}
		}
		
		if(!isValid) {
			MessageBox msg = new MessageBox();
			msg.setIcon(MessageBox.ERROR);
			msg.setTitleHtml(title);
			msg.setMessage(msgStr);
			msg.setModal(true);
			Utils.setMessageBoxDebugId(msg);
			msg.show();
		}
		
		return isValid;
	}
	
	public void refreshData(ScheduledExportSettingsModel model) {
		if(model != null) {
			enableSchedule.setValue(model.getEnableScheduledExport());
			exportInterval.setValue(model.getExportInterval());
			exportInterval.clearInvalid();
			keepField.setValue(model.getKeepRecoveryPoints());
			keepField.clearInvalid();
			encryptionPane.setEncryptAlogrithm(model.getEncryptionAlgorithm());
			encryptionPane.setEncryptPassword(model.getEncryptionKey());
			pathSelection.setDestination(model.getDestination());
			String destination = model.getDestination();
			if(destination!=null && !PathSelectionPanel.isLocalPath(destination)) {
				pathSelection.setUsername(model.getDestUserName());
				pathSelection.setPassword(model.getDestPassword());
				pathSelection.cacheInfo();
				if ((model.getDestUserName() != null) &&
						(model.getDestUserName().length() > 0))
						pathSelection.setValidated();
			}

			if(model.getCompressionLevel() != null) {
				if(IConstants.COMPRESSIONSTANDARD == model.getCompressionLevel()) {
					compressionOption.setSimpleValue(UIContext.Constants.settingsCompreesionStandard());
					Utils.addToolTip(compressionOption, UIContext.Constants.settingsLabelCompressionStandardTooltip());
				} else if(IConstants.COMPRESSIONMAX == model.getCompressionLevel()) {
					compressionOption.setSimpleValue(UIContext.Constants.settingsCompressionMax());
					Utils.addToolTip(compressionOption, UIContext.Constants.settingsLabelCompressionMaxTooltip());
				} else if(IConstants.COMPRESSIONNONEVHD == model.getCompressionLevel()) {
					compressionOption.setSimpleValue(UIContext.Constants.settingsNoCompressionVHD());
					Utils.addToolTip(compressionOption, UIContext.Constants.settingsLabelCompressionVHD());
				} else {
					compressionOption.setSimpleValue(UIContext.Constants.settingsCompressionNone());
					Utils.addToolTip(compressionOption, UIContext.Constants.settingsLabelCompressionNoneTooltip());
				}
			}
			
			setEnabledScheduledSettingsField(model.getEnableScheduledExport());
		}
	}
	
	private int getCompressionLevel(String compressionValue){
		int compressionLevel = -1;
		if (compressionValue == UIContext.Constants.settingsCompressionNone())
			compressionLevel = IConstants.COMPRESSIONNONE;
		else if (compressionValue == UIContext.Constants.settingsCompreesionStandard())
			compressionLevel = IConstants.COMPRESSIONSTANDARD;
		else if (compressionValue == UIContext.Constants.settingsCompressionMax())
			compressionLevel = IConstants.COMPRESSIONMAX;
		else if(compressionValue == UIContext.Constants.settingsNoCompressionVHD()) {
			compressionLevel = IConstants.COMPRESSIONNONEVHD;
		}
		return compressionLevel;
	}
	
	public void save() {
		parentWindow.model.setEnableScheduledExport(enableSchedule.getValue());
		String path = pathSelection.getDestination();
		parentWindow.model.setDestination(path);
		if (!PathSelectionPanel.isLocalPath(path)) {
			String destination=pathSelection.getDestination();
			String[] info= Utils.getConnectionInfo(destination);
			if(info!=null){
				parentWindow.model.setDestUserName(info[1]);
				parentWindow.model.setDestPassword(info[2]);
			}
			else{
			parentWindow.model.setDestUserName(pathSelection.getUsername());
				parentWindow.model.setDestPassword(pathSelection.getUsername());
			}
			
			//parentWindow.model.setDestUserName(pathSelection.getUsername());
			//parentWindow.model.setDestPassword(pathSelection.getUsername());
		} else {
			parentWindow.model.setDestUserName(null);
			parentWindow.model.setDestPassword(null);
		}
		
		parentWindow.model.setEncryptionAlgorithm(encryptionPane.getEncryptAlgorithm());
		parentWindow.model.setEncryptionKey(encryptionPane.getEncryptPassword());
		
		int interval = exportInterval.getValue() == null ? 0 : exportInterval
				.getValue().intValue();
		parentWindow.model.setExportInterval(interval);
		int keep = keepField.getValue() == null ? 0 : keepField.getValue()
				.intValue();
		parentWindow.model.setKeepRecoveryPoints(keep);

		String compressionValue = compressionOption.getSimpleValue();
		int level = getCompressionLevel(compressionValue);
		if (level != -1) {
			parentWindow.model.setCompressionLevel(level);
		}
	}
	
	public void setEditable(boolean isEditable){
		enableSchedule.setEnabled(isEditable);
		compressionOption.setEnabled(isEditable);
		
		exportInterval.setEnabled(isEditable);
		keepField.setEnabled(isEditable);
		encryptionPane.setEnabled(isEditable);
		pathSelection.setEnabled(isEditable);
	}
	public void validateRemotePath(final AsyncCallback<Boolean> callback) {
		if(this.enableSchedule==null || !this.enableSchedule.getValue()){
			callback.onSuccess(true);
			return ;
		}
		if (pathSelection.isLocalPath()) {
			callback.onSuccess(true);
			return;
		}

		if (!pathSelection.needValidate()) {
			callback.onSuccess(true);
			return;
		}

		final UserPasswordWindow dlg = new UserPasswordWindow(pathSelection.getDestination(), "", "");
		dlg.setModal(true);

		dlg.addWindowListener(new WindowListener() {

			@Override
			public void windowHide(WindowEvent we) {
				if (dlg.getCancelled()) {
					callback.onSuccess(false);
					return;
				}

				pathSelection.setUsername(dlg.getUsername());
				pathSelection.setPassword(dlg.getPassword());
				pathSelection.setValidated();
				parentWindow.model.setDestUserName(dlg.getUsername());
				parentWindow.model.setDestPassword(dlg.getPassword());

				callback.onSuccess(true);
			}

		});

		dlg.show();
	}	
	public boolean checkShareFolder()
	{
		if(this.enableSchedule==null || !this.enableSchedule.getValue()){
			return true;
		}
		try
		{
			if (pathSelection.isLocalPath())
				return true;
			
			String title = "";
			//FIXME, maybe we should use Edge server name here instead of D2D for box title
			{
				title = UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D);
			}

			String destPath = pathSelection.getDestination();
			UncPath uncPath = new UncPath();
			uncPath.setUncPath( destPath );
			if (uncPath.getShareFolder().length() == 0)
			{
				MessageBox messageBox = new MessageBox();
				messageBox.setTitleHtml( title );
				messageBox.setMessage( UIContext.Constants.recoveryPointsDest_Error_NoShareFolder() );
				messageBox.setIcon( MessageBox.ERROR );
				messageBox.setButtons( MessageBox.OK );
				Utils.setMessageBoxDebugId(messageBox);
				messageBox.show();
				return false;
			}
			
			if (uncPath.getComputerName().equalsIgnoreCase( "localhost" ) ||
				uncPath.getComputerName().equalsIgnoreCase( "127.0.0.1" ))
			{
				MessageBox messageBox = new MessageBox();
				messageBox.setTitleHtml( title );
				messageBox.setMessage( UIContext.Constants.recoveryPointsDest_Error_NotAllowLocalHost() );
				messageBox.setIcon( MessageBox.ERROR );
				messageBox.setButtons( MessageBox.OK );
				Utils.setMessageBoxDebugId(messageBox);
				messageBox.show();
				return false;
			}

			return true;
		}
		catch (UncPath.InvalidUncPathException e)
		{
			MessageBox messageBox = new MessageBox();
			String title = "";
			{
				title = UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D);
			}
			messageBox.setTitleHtml( title );
			messageBox.setMessage( UIContext.Constants.recoveryPointsDest_Error_InvalidUncPath() );
			messageBox.setIcon( MessageBox.ERROR );
			messageBox.setButtons( MessageBox.OK );
			Utils.setMessageBoxDebugId(messageBox);
			messageBox.show();
			return false;
		}
	}
	
}
