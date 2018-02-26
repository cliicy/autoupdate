package com.ca.arcflash.ui.client.backup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ca.arcflash.ha.model.JobScriptCombo;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyManager;
import com.ca.arcflash.ui.client.coldstandby.DisclourePanelImageBundles;
import com.ca.arcflash.ui.client.coldstandby.VCMMessages;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.FlashCheckBox;
import com.ca.arcflash.ui.client.common.PathSelectionPanel;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.common.d2d.presenter.SettingPresenter;
import com.ca.arcflash.ui.client.comon.widget.ExtLabelField;
import com.ca.arcflash.ui.client.model.ApplicationComponentModel;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.ca.arcflash.ui.client.model.BackupVolumeModel;
import com.ca.arcflash.ui.client.model.FileModel;
import com.ca.arcflash.ui.client.model.FileSystemType;
import com.ca.arcflash.ui.client.model.VolumeGrayedReason;
import com.ca.arcflash.ui.client.model.VolumeLayoutType;
import com.ca.arcflash.ui.client.model.VolumeModel;
import com.ca.arcflash.ui.client.model.VolumeSubStatus;
import com.ca.arcflash.ui.client.model.VolumeType;
import com.ca.arcflash.ui.client.model.encrypt.EncryptionAlgModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Image;

public class D2DDestinationSettings extends BackupDestinationSettings{
	private ListStore<VolumeModel> gridStore;
	private JobScriptCombo localJobScriptCombo;

	private Grid<VolumeModel> gridVolumes;
	private LabelField totalSizeLabel;
	
	private DisclosurePanel estBackupSizePanel;
		
	private BackupSourePieChartPanel pieChartPanel = null;
	
	private boolean isStandard = false;
	private boolean isBackupSet = false;
	public D2DDestinationSettings(BackupSettingsContent w) {
		super(w);
		
		SettingPresenter.getInstance().addListener(new Listener<AppEvent>(){
			@Override
			public void handleEvent(AppEvent be) {
				updatePieChart();	
				if(SettingPresenter.getInstance().isBackupDataFormatNew(be)){
					isStandard = false;
				}else{					
					isStandard = true;
				}
				if(isStandard&&!isBackupSet){
					estBackupSizePanel.setVisible(true);
				}else{
					estBackupSizePanel.setVisible(false);
				}
				
				update2TDiskEnableStatus();
			}});
		
	}
	
	@Override
	public LayoutContainer Render() {
		LayoutContainer container = super.Render();
		
		initEstimatedValuesContainer(container);
		//cold standby setting: check whether the VCM is configured.
		initColdstandbyJobScriptCombo();
		return container;
	}

	protected void initColdstandbyJobScriptCombo(){
		coldStandbyService.getLocalJobScriptCombo(ColdStandbyManager.getVMInstanceUUIDFromURL(), 
				new BaseAsyncCallback<JobScriptCombo>(){
			@Override
			public void onSuccess(JobScriptCombo result) {
				if (result == null || result.getFailoverJobScript() == null
						|| result.getHbJobScript() == null
						|| result.getRepJobScript() == null) {
					localJobScriptCombo=null;
				}
				else{
					localJobScriptCombo=result;
				}
			}
		});
	}	

	protected void initEstimatedValuesContainer(LayoutContainer container) {
		estBackupSizePanel = new DisclosurePanel((DisclourePanelImageBundles) 
				GWT.create(DisclourePanelImageBundles.class),
				UIContext.Constants.destinationEstimatedBackupSize(), true);
		estBackupSizePanel.ensureDebugId("C6B7B14D-9F44-49ec-BD71-BF914E4E1B79");
		estBackupSizePanel.setWidth("100%");
		estBackupSizePanel.setStylePrimaryName("gwt-DisclosurePanel-coldStandby");
		estBackupSizePanel.setOpen(true);		
		pieChartPanel = new BackupSourePieChartPanel(this);
		estBackupSizePanel.add(pieChartPanel);
		estBackupSizePanel.setVisible(false);
		compressionOption.addSelectionChangedListener(pieChartPanel.setupCompressionChangeListener());
		
		for(NumberField nf: getRetentionCountField()){
			if(nf != null){
				nf.addListener(Events.Change, pieChartPanel.getRetentioncountListener());
			}
		}
		container.add(estBackupSizePanel);
	}
	
	protected LayoutContainer initVolumesGridContainer() {
		LayoutContainer sourceVolumesContainer = new LayoutContainer();
		TableLayout layout = new TableLayout();
		layout.setWidth("96%");
		layout.setColumns(1);
		sourceVolumesContainer.setLayout(layout);
		sourceVolumesContainer.setStyleName("sourceVolumesLayout");

		selectAllCheckBox = new CheckBox(){
			@Override
			protected void onClick(ComponentEvent ce) {
				super.onClick(ce);
				if(getValue()) {
					selectAllVolumes(true);
					refreshAllDatas();
				}else {
					selectAllVolumes(false);
					refreshAllDatas();
				}
			}
		};
		selectAllCheckBox.ensureDebugId("D796BDC2-36DA-4d50-B347-DA583CC0F054");
		selectAllCheckBox.setBoxLabel(UIContext.Constants.selectUnselectAll());
		selectAllCheckBox.setVisible(false);
		sourceVolumesContainer.add(selectAllCheckBox);

		ColumnModel columnModel = getColumnModel();
		gridStore = new ListStore<VolumeModel>();
		gridVolumes = new Grid<VolumeModel>(gridStore, columnModel);
		gridVolumes.ensureDebugId("59c32b2d-f60d-4fcf-bf9c-0582c3f31742");
		gridVolumes.setBorders(true);
		gridVolumes.setAutoExpandColumn("status");
		gridVolumes.setTrackMouseOver(true);
		gridVolumes.setHeight(118);
	    gridVolumes.setAutoWidth(true);
	    gridVolumes.setVisible(false);
		TableData data = new TableData();
		data.setWidth("100%");
		sourceVolumesContainer.add(gridVolumes, data);

		totalSizeLabel = new LabelField();
		totalSizeLabel.setVisible(false);
		data = new TableData();
		data.setHorizontalAlign(HorizontalAlignment.RIGHT);
		data.setMargin(2);
		data.setPadding(2);
		sourceVolumesContainer.add(totalSizeLabel, data);

		return sourceVolumesContainer;
	}
	
	@Override
	protected LayoutContainer getBackupDestChangedTypePanel() {
		LayoutContainer container = super.getBackupDestChangedTypePanel();
		
		destChangedFullBackup.addListener(Events.Change, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {				
				updateNotificationMsg();
				if (fullBackupRadio.getValue()) {
					updatePieChart();
				}				
			}		
		});
		return container;
	}
	
	private void updatePieChart() {
		pieChartPanel.updateEstimatedSize(getAndUpdateBackupSourceSize(), getRetentionCount());
		pieChartPanel.updateEstimatedPieChart();
	}
	
	protected void loadVolumeTreeData(final BackupSettingsModel model) {
		gridVolumes.mask(UIContext.Constants.destinationLoadingVolumes());
		contentHost.increaseBusyCount();
		loginService.getVolumesWithDetails(0, model.getDestination(), model.getDestUserName(), 
				model.getDestPassword(), new BaseAsyncCallback<List<FileModel>>() {

			@Override
			public void onFailure(Throwable caught) {
				contentHost.decreaseBusyCount();
				warnLoadingStatus.hideIndicator();
			}

			@Override
			public void onSuccess(List<FileModel> result) {
				if(result == null || result.size() == 0){
					contentHost.decreaseBusyCount();
					return;
				}
				final List<FileModel> vols = result;
				//Get and update the latest local driver letters, added by wanqi06
				List<String> volumeList = new ArrayList<String>();
				if(vols != null && vols.size() > 0) {
					for(int i = 0; i < vols.size(); i ++) {
						FileModel fileModel = vols.get(i);
						if(fileModel.getName() != null && !fileModel.getName().startsWith("\\\\?\\Volume"))
							volumeList.add(vols.get(i).getName());
					}
				}
			
				UIContext.serverVersionInfo.setLocalDriverLetters(volumeList);
				PathSelectionPanel.updateLocalDriverLetters();
				
				isUEFIFirmware = UIContext.serverVersionInfo.isUefiFirmware();
				initGridTreeData(vols);
				//after we got the Volumn details, we can call initVolumnRadioAndState, so set the isVolumnInitialized to true.
				isVolumnInitialized = true;
				initVolumeRadioAndState();
				classifyGreyedVolumes(vols);

				gridVolumes.unmask();
				refreshAllDatas();
				contentHost.decreaseBusyCount();
//				getLocalDistDetailsAndUpdateChart();
			}

			private void classifyGreyedVolumes(List<FileModel> result) {
				HashSet<String> unSupportedVolumeSet = new HashSet<String>();
				for(FileModel model : result) {
					VolumeModel vModel = (VolumeModel)model;
					if(isUnsupportedBackupVolumeType(vModel)) {
						if(vModel.getMsgID() == VolumeGrayedReason.EVGR_VOL_ON_BACKUP_DEST_CHAIN)
							backupDestChainSet.add(vModel);
						else if(vModel.getMsgID() == VolumeGrayedReason.EVGR_VOL_NOT_SUPPORTED){
							unSupportedVolumeSet.add(model.getName());
							unsupportedBackupVolumes.add(model);
						}
						
						if(!VolumeLayoutType.isBackupSupport(vModel.getLayout())){
							unsupportedBackupVolumes.add(vModel);
						}
					}
					if(vModel.getDataStore() != null && !vModel.getDataStore().isEmpty()) {
						datastoreVolume.add(vModel);
					}
				}
				if(unSupportedVolumeSet.size() > 0) {
					unsupportedVolumeDesc = new LabelField(
							UIContext.Messages.volumeGreyNotSupported(UIContext.productNameD2D));
				}
			}

			private void initGridTreeData(List<FileModel> result) {
				List<VolumeModel> volumeModelList = new ArrayList<VolumeModel>();
				for (FileModel volume : result) {
					String name = volume.getName();
					name = removeEndSlash(name);
					volume.setName(name);
					volumeModelList.add((VolumeModel)volume);
				}

				gridStore.removeAll();
				gridStore.add(volumeModelList);
			}
		});
	}

	private void initVolumeRadioAndState() {
		BackupVolumeModel volumes = SettingPresenter.model.getBackupVolumes();
		if(volumes != null) {
			if(fullBackupRadio.getValue()) {
				selectAllCheckBox.setValue(true);
				selectAllVolumes(true);
			}
			else {
				Set<String> volumeSet = new HashSet<String>();
				if(volumes.selectedVolumesList != null && volumes.selectedVolumesList.size() > 0) {
					for(String selectedVolume : volumes.selectedVolumesList) {
						volumeSet.add(selectedVolume.toLowerCase());
					}
				}

				for(VolumeModel model : gridStore.getModels()) {
					String volName = model.getName().toLowerCase();
					FlashCheckBox checkBox = getOrCreateFlashCheckBox(model);
					if(volumeSet.isEmpty() || volumeSet.contains(volName)) {
						if(checkBox.isEnabled()){
							checkBox.setSelectedState(FlashCheckBox.FULL);
						}
					}
					if(!isUnsupportedBackupVolumeType(model) && !isEditable)
						checkBox.setEnabled(isEditable);
				}
			}
		}
	}

	@Override
	protected void refreshSkippedDiskIcon() {
		super.refreshSkippedDiskIcon();
		if(!this.isLocalDisk()) {
			// to solve the case that the mount point is a substring of a folder
			// name.
			String newDest = pathSelection.getDestination();
			if(newDest == null)
				return;
			newDest = addPathSplitter(newDest.toLowerCase());
			if (currentBackupDestVolume != null) {
				String destDriver = currentBackupDestVolume.getName().toLowerCase() + "\\";

				if (newDest.startsWith(destDriver)) {
					if (!isExistLongVolumeMatch(newDest, destDriver))
						return;
				}
			}
		}
		pieChartPanel.updateEstimatedSize(getAndUpdateBackupSourceSize(), getRetentionCount());
		
		if(isLocalDisk() && this.currentBackupDestVolume != null)
			pieChartPanel.getLocalDistDetailsAndUpdateChart(
					currentBackupDestVolume.getTotalSize(), currentBackupDestVolume.getFreeSize());
	}
	
	//process the situation when volume is mounted to a folder
	protected boolean isExistLongVolumeMatch(String backupDest, String destDriverWithSplitter) {
		for(int i = 0, count = gridStore.getCount(); i < count; i++) {
			VolumeModel child = gridStore.getModels().get(i);
			String childName = child.getName().toLowerCase() + "\\";
			if(backupDest.startsWith(childName) && childName.length() > destDriverWithSplitter.length())
				return true;
		}
		return false;
	}

	@Override
	protected void updateSourceIcon() {
		List<VolumeModel> children = gridStore.getModels();
		for (VolumeModel child : children) {
			IconButton icon = (IconButton)child.get("IconButton");
			icon.changeStyle(getNodeIconName(child));
		}
	}
	
	private ColumnModel getColumnModel() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
		CheckColumnConfig checkColumn = new CheckColumnConfig("checked", "", 40);
		checkColumn.setHidden(true);
		configs.add(checkColumn);

		ColumnConfig column = new ColumnConfig();
		column.setId("displayName");
		column.setHeaderHtml(UIContext.Constants.restoreNameColumn());
		column.setWidth(102);
		column.setMenuDisabled(true);
		column.setRenderer(new GridCellRenderer<VolumeModel>() {

			@Override
			public Object render(VolumeModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<VolumeModel> store, Grid<VolumeModel> grid) {
				LayoutContainer lc = new LayoutContainer();

				TableLayout layout = new TableLayout();
				layout.setColumns(3);
				lc.setLayout(layout);

				FlashCheckBox box = getOrCreateFlashCheckBox(model);
				if(box == null) {
					box = new FlashCheckBox();
					if(isUnsupportedBackupVolumeType(model)) {
						box.setEnabled(false);
					}
					box.addSelectionListener(new SelectionListener<IconButtonEvent>() {

						@Override
						public void componentSelected(IconButtonEvent ce) {
							refreshAllDatas();
						}
					});

					model.set("FlashCheckBox", box);
				}
				lc.add(box);

				IconButton image = (IconButton)model.get("IconButton");
				if(image == null) {
					image = getNodeIcon(model);
					model.set("IconButton", image);
				}

				if(image != null) {
					lc.add(image);
				}

				LabelField lf = new ExtLabelField();

				// liuwe05 2011-1-8 fix Issue: 19964761    Title: NO GUID DISPLAYED FOR VOLUME
				// if the display name is empty, use its GUID
				String displayName = model.getDisplayName();

				if (displayName == null || displayName.isEmpty())
				{
					displayName = model.getGUID();
				}

				lf.setValue(displayName);
				lc.add(lf);
				return lc;
			}

		});
		configs.add(column);

		column = new ColumnConfig();
		column.setId("layout");
		column.setHeaderHtml(UIContext.Constants.destinationVolumeLayout());
		column.setWidth(70);
		column.setMenuDisabled(true);
		column.setRenderer(new GridCellRenderer<VolumeModel>() {

			@Override
			public Object render(VolumeModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<VolumeModel> store, Grid<VolumeModel> grid) {
				String name = VolumeLayoutType.getDisplayName(model.getLayout());
				return name;
			}
		});
		configs.add(column);

		column = new ColumnConfig();
		column.setId("type");
		column.setHeaderHtml(UIContext.Constants.destinationVolumeType());
		column.setWidth(70);
		column.setMenuDisabled(true);
		column.setRenderer(new GridCellRenderer<VolumeModel>() {

			@Override
			public Object render(VolumeModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<VolumeModel> store, Grid<VolumeModel> grid) {
				String name =  VolumeType.getDisplayName(model.getType());
				return name;
			}
		});
		configs.add(column);

		column = new ColumnConfig();
		column.setId("fileSysType");
		column.setHeaderHtml(UIContext.Constants.destinationVolumeFileSysType());
		column.setWidth(65);
		column.setMenuDisabled(true);
		column.setRenderer(new GridCellRenderer<VolumeModel>() {

			@Override
			public Object render(VolumeModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<VolumeModel> store, Grid<VolumeModel> grid) {
				String typeName =  FileSystemType.getDisplayName(model.getFileSysType());
				return typeName;
			}
		});
		configs.add(column);

		column = new ColumnConfig();
		column.setId("status");
		column.setHeaderHtml(UIContext.Constants.destinationVolumeStatus());
		column.setWidth(157);
		column.setMenuDisabled(true);
		column.setRenderer(new GridCellRenderer<VolumeModel>() {

			@Override
			public Object render(VolumeModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<VolumeModel> store, Grid<VolumeModel> grid) {
//				String status = VolumeStatus.getDisplayName(model.getStatus());
				String subStatus = VolumeSubStatus.getDisplayName(model.getSubStatus());
//				if(subStatus != null) {
//					status += ("(" + subStatus + ")");
//				}				
				if (subStatus == null) {
					if (checkForDedupVolume(model)) {
						subStatus = UIContext.Constants.DeduplicateVolumeDescription();
					}
				}
				if(subStatus != null){
					LabelField label = new LabelField(subStatus);
					Utils.addToolTip(label, subStatus);
					return label;
				}
				return "";
			}
		});

		configs.add(column);

		column = new ColumnConfig();
		column.setId("totalSize");
		column.setHeaderHtml(UIContext.Constants.destinationVolumeTotalSize());
		column.setWidth(76);
		column.setMenuDisabled(true);
		column.setRenderer(new GridCellRenderer<VolumeModel>() {

			@Override
			public Object render(VolumeModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<VolumeModel> store, Grid<VolumeModel> grid) {
				try {
					Long totalSize = model.getTotalSize();
					if (totalSize != null && totalSize > 0) {
						String formattedValue = Utils.bytes2String(totalSize);
						return formattedValue;
					}
				} catch (Exception e) {

				}

				return "";
			}

		});
		configs.add(column);

		column = new ColumnConfig();
		column.setId("usedSize");
		column.setHeaderHtml(UIContext.Constants.destinationVolumeUsedSize());
		column.setWidth(76);
		column.setMenuDisabled(true);
		column.setRenderer(new GridCellRenderer<VolumeModel>() {

			@Override
			public Object render(VolumeModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<VolumeModel> store, Grid<VolumeModel> grid) {
				try {
					Long totalSize = model.getTotalSize();
					if (totalSize != null && totalSize > 0) {
						Long freeSize = model.getFreeSize();
						Long usedSpace = totalSize - freeSize;
						model.set("usedSize", usedSpace);
						String formattedValue = Utils.bytes2String(usedSpace);
						return formattedValue;
					}
				} catch (Exception e) {
				}

				return "";
			}

		});
		configs.add(column);

		ColumnModel columnModel = new ColumnModel(configs);
		return columnModel;
	}		

	protected IconButton getNodeIcon(VolumeModel model) {

		if(model == null)
			return null;

		String name = getNodeIconName(model);
		IconButton image = null;
		if(name != null) {
			image = new IconButton(name);
			image.setWidth(20);
		}

		return image;
	}

	protected String getNodeIconName(VolumeModel model) {
		if(model == null)
			return null;
	
		String image = null;
		if (isSystemVolume(model)) {
			if (isBackupDestination(model))
				image = "system-drive-skipped-icon";
			else
				image = "system-drive-icon";
		}
		else if (isBootVolume(model)) {
			if (isBackupDestination(model))
				image = "boot-drive-skipped-icon";
			else
				image = "boot-drive-icon";
		}
		else
			image = getCommonVolumeIcon(model);
	
		return image;
	}
	

	
	private String getCommonVolumeIcon(VolumeModel model) {
		if(isBackupDestination(model))
			return "drive-skipped-icon";
		else
			return "drive-icon";
	}
	
	private boolean isBackupDestination(VolumeModel model) {
		boolean bRet = false;
		if(isLocalDisk()) {
			String backupDest = pathSelection.getDestination();
	
			if(isEmpty(backupDest))
				return false;
	
			//process the situation when volume is mounted to a folder
			backupDest = addPathSplitter(backupDest.toLowerCase());
			String volumeDriver = model.getName().toLowerCase();
			String volumeDriverWithSplitter = volumeDriver + "\\";
			FlashCheckBox box = getOrCreateFlashCheckBox(model);
			if(checkDestinationWithCurrentDriver(backupDest, volumeDriverWithSplitter)) {
				if(isExistLongVolumeMatch(backupDest, volumeDriverWithSplitter))
					bRet = false;
	
				currentBackupDestVolume = model;
				//disable the check box of the destination volume
				box.setSelectedState(FlashCheckBox.NONE);
				box.setEnabled(false);
				bRet = true;
			}else {
				if(box.isEnabled() && box.getSelectedState() == FlashCheckBox.NONE)
					selectAllCheckBox.setValue(false);
			}
	
			return bRet;
		}
	
		enableOldLocalDestVolume();
		return false;
	}
	
	private boolean checkDestinationWithCurrentDriver(String destination, String curDriver) {
		List<String> driverLetters = new ArrayList<String>();
		List<VolumeModel> volumes = gridStore.getModels();
		for(VolumeModel volume:volumes) {
			String volumeDriver = volume.getName().toLowerCase();
			driverLetters.add(volumeDriver+"\\");
		}
		
		// sort the driver letters by their length. we should compare the driver from long driver letter. 
		if(driverLetters.size()>0) {
			Collections.sort(driverLetters, new Comparator<String>() {
	
				@Override
				public int compare(String o1, String o2) {
					if(o1.length() > o2.length())
						return -1;
					else if(o1.length() < o2.length())
						return 1;
					else
						return 0;
				}
			});
		}
		
		for(String driver:driverLetters) {
			if(destination.startsWith(driver)) {
				if(driver.equalsIgnoreCase(curDriver)) {
					return true;
				} else {
					return false;
				}
			}
		}
		
		return false;
	}
	
	
	
	@Override
	public void RefreshData(BackupSettingsModel model,boolean isEdit) {
		super.RefreshData(model, isEdit);
		if(model.getBackupDataFormat() == null || model.getBackupDataFormat() == 1){
			estBackupSizePanel.setVisible(false);
			return;
		}
		if(model != null && model.isBackupToRps() != null && model.isBackupToRps()){
			estBackupSizePanel.setVisible(false);
			return;
		}
		if(model != null && model.retentionPolicy != null 
				&& model.retentionPolicy.isUseBackupSet() != null 
				&& model.retentionPolicy.isUseBackupSet()){
			this.retentionPolicyChanged(true);
		}else{
			this.retentionPolicyChanged(false);
		}
	}

	@Override
	protected void processSourceAndBackupEstimate(BackupSettingsModel model) {
		super.processSourceAndBackupEstimate(model);
		// Tool tip
		compressionOption.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {
			Boolean fomerCompressLevel = null;
			@Override
			public void selectionChanged(
					SelectionChangedEvent<SimpleComboValue<String>> se) {
				if(fomerCompressLevel == null
						|| fomerCompressLevel && getCompressionLevel() == 0
						|| !fomerCompressLevel && getCompressionLevel() != 0)
					update2TDiskEnableStatus();
			}
		});	
		
		addEncAlgSelectionChangedHandler(new SelectionChangedListener<EncryptionAlgModel>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<EncryptionAlgModel> se) {
				update2TDiskEnableStatus();				
			}		
		});
		
		pieChartPanel.refreshData(model);
	}
	
	private void update2TDiskEnableStatus() {
		List<VolumeModel> models = gridStore.getModels();
		for (VolumeModel model : models) {
			if(VolumeSubStatus.isMountedFrom2TDisk(model)) {
				FlashCheckBox checkBox = getOrCreateFlashCheckBox(model);
				if(!isNeedCheck2T()) {
					if(!isUnsupportedBackupVolumeType(model) || supportedVolumeAfterChangeSetting(model)) {
						checkBox.setEnabled(true);
					}
				}
				else {
					checkBox.setEnabled(false);
					if(checkBox.getSelectedState() == FlashCheckBox.FULL) {
						checkBox.setSelectedState(FlashCheckBox.NONE);
					}
				}
			}
		}
		updateNotificationMsg();
	}
	
	private boolean supportedVolumeAfterChangeSetting(VolumeModel model) {
		return model.getMsgID() == VolumeGrayedReason.EVGR_VOL_ON_BACKUP_DEST_CHAIN && isBackupDestChangedAndNextFull() && model != currentBackupDestVolume;
	}
	
	@Override
	public void Save() {
		super.Save();
		pieChartPanel.saveData(SettingPresenter.model);
	}	

	protected void updateNotificationMsg() {
		List<VolumeModel> volumes = gridStore.getModels();
		//the tree initialization or the application distribution loading is not finished.
		if(volumes.size() < 1 || volume2ComponentsMap == null) {
			updateNotificationPane(0, 0);
			return;
		}

		warnLoadingStatus.hideIndicator();
		notificationSet.removeAll();
		filterBackupVolumes.clear();
		refsNtfsDedupBackupVolumes.clear();
		
		//Save refs and ntfs dedup volumes, added by wanqi06
		for(VolumeModel child : volumes) {
			if(checkForRefsDedupvolumes(child)) {
				if(!refsNtfsDedupBackupVolumes.contains(child)){
					refsNtfsDedupBackupVolumes.add(child);	
				}			   
			}
		}

		int warnNumber = 0;
		int infoNumber = 0;
		List<VolumeModel> unselectedList = new ArrayList<VolumeModel>();
		List<VolumeModel> selectedList = new ArrayList<VolumeModel>();
		List<VolumeModel> refsVolumesList = new ArrayList<VolumeModel>();
		List<VolumeModel> dedupeVolumesList = new ArrayList<VolumeModel>();

 		StringBuilder builder = new StringBuilder();
		int localNumber = 0;
		if(isBackupDestChangedAndNextFull()) {
			if(currentBackupDestVolume != null) {
				if(addNotification(unselectedList, currentBackupDestVolume))
					warnNumber++;
				else
					builder.append(getVolNameOrGuid(currentBackupDestVolume)).append(", ");
				this.filterBackupVolumes.add(currentBackupDestVolume);
			}
		}
		else if(currentBackupDestVolume != null || backupDestChainSet.size() > 0) {

			if(currentBackupDestVolume!= null && !backupDestChainSet.contains(currentBackupDestVolume)) {
				if(addNotification(unselectedList, currentBackupDestVolume))
					warnNumber++;
				else {
					builder.append(getVolNameOrGuid(currentBackupDestVolume)).append(", ");
					localNumber++;
				}
				filterBackupVolumes.add(currentBackupDestVolume);
			}

			for(VolumeModel model : backupDestChainSet) {
				if(addNotification(unselectedList, model))
					warnNumber++;
				else {
					builder.append(getVolNameOrGuid(model)).append(", ");
					localNumber++;
				}
				filterBackupVolumes.add(model);
			}
		}

		if(builder.length() > 0)
		{
			addWaringIcon();
			builder.delete(builder.length() - 2, builder.length());
			if(localNumber > 1)
				notificationSet.add(new LabelField(UIContext.Messages.backupSettingsDestinationIsOnLocalDist(builder.toString())));
			else
				notificationSet.add(new LabelField(UIContext.Messages.backupSettingsDestChainOneOnLocal(builder.toString())));

			warnNumber++;
		}

		if(unsupportedVolumeDesc != null) {
			warnNumber++;
			addWaringIcon();
			notificationSet.add(unsupportedVolumeDesc);
		}

		StringBuilder volume2TBuilder = new StringBuilder();
		for (VolumeModel volume : volumes) {
			if(!unselectedList.contains(volume)) {
				FlashCheckBox box = getOrCreateFlashCheckBox(volume);
				if (selectVolumsRadio.getValue() && box.getSelectedState() == FlashCheckBox.NONE
						|| !selectVolumsRadio.getValue() && !box.isEnabled() && isEditable /*is NOT using edge policy*/) {
					unselectedList.add(volume);
					if (isSystemVolume(volume)) {
						addWaringIcon();
						notificationSet.add(new LabelField(UIContext.Messages
								.backupSettingsOnSystemVolumeNotSelect(volume.getDisplayName())));
						warnNumber++;
					} else if (isBootVolume(volume)) {
						addWaringIcon();
						notificationSet.add(new LabelField(UIContext.Messages
								.backupSettingsOnBootVolumeNotSelect(volume.getDisplayName())));
						warnNumber++;
					}
				}else if (isUEFIFirmware && selectVolumsRadio.getValue() 
						&& box.getSelectedState() != FlashCheckBox.NONE) {
					if(isBootVolume(volume)) {
						addInfoIcon();
						notificationSet.add(new LabelField(UIContext.Messages
								.backupSettingsBootVolumeSelectESP(volume.getDisplayName())));
						infoNumber++;
					}
				}
			}
			
			

			if (VolumeSubStatus.isMountedFrom2TDisk(volume) && this.isNeedCheck2T()) {
				boolean iRet = false;
				boolean bRet = false;
				if (currentBackupDestVolume != null) {
					String curBkVol = currentBackupDestVolume.getDisplayName();
					String curVol = volume.getDisplayName();
					iRet = curBkVol.equalsIgnoreCase(curVol);
				}

				if (!isBackupDestChangedAndNextFull()
						&& backupDestChainSet != null
						&& backupDestChainSet.size() > 0) {
					bRet = backupDestChainSet.contains(volume);
				}

				if (!iRet && !bRet)
					volume2TBuilder.append(volume.getDisplayName()).append(", ");
			}
		}
		
		BackupVolumeModel volumesList = getBackupVolumes();
		if(volumesList.getIsFullMachine()) //For Full machine
		{
			for (VolumeModel volume : volumes) {					
//				if (checkForRefsDedupvolumes(volume)) {
//					refsVolumesList.add(volume);
//				}
				if (checkForRefsVolume(volume)) {
					refsVolumesList.add(volume);
				}
				if (checkForDedupVolume(volume)) {
					dedupeVolumesList.add(volume);
				}
			}
		}
		else
		{
			for (VolumeModel volume : volumes) {
				if(!selectedList.contains(volume)) {			
					FlashCheckBox box = getOrCreateFlashCheckBox(volume);
					if (selectVolumsRadio.getValue() && box.getSelectedState() == FlashCheckBox.FULL) {
						selectedList.add(volume);
//						if (checkForRefsDedupvolumes(volume)) {
//							refsVolumesList.add(volume);
//						}
						if (checkForRefsVolume(volume)) {
							refsVolumesList.add(volume);
						}
						if (checkForDedupVolume(volume)) {
							dedupeVolumesList.add(volume);
						}
						
					}
				}
			}
		}
		
		if(refsVolumesList != null && refsVolumesList.size() > 0)
		{
		  StringBuilder selectedRefsList = new StringBuilder();
		  for(VolumeModel vol : refsVolumesList)
		  {
			  selectedRefsList.append(vol.getDisplayName()+"("+getVolumeTypeName(vol.getFileSysType())+")"+" ");
		  }
		  parentWindow.setRefsVolList(selectedRefsList.toString());
		  addWaringIcon();
		  notificationSet.add(new LabelField(UIContext.Messages.refsVolumesSelect(selectedRefsList.toString())));
		  warnNumber++;
		  parentWindow.updateNotification();
		}
		else
		{				
			 parentWindow.setRefsVolList(null);
			 parentWindow.updateNotification();
		}
		
		if(volume2TBuilder.length() > 0) {
			warnNumber++;
			addWaringIcon();
			volume2TBuilder.delete(volume2TBuilder.length() - 2, volume2TBuilder.length());
			notificationSet.add(new LabelField(UIContext.Messages.backupSettingsVolume2TBType(volume2TBuilder.toString(), UIContext.productNameD2D)));
		}
		if(unselectedList.size() > 0) {
			for (int i = 0, count = unselectedList.size(); i < count; i++) {
				String volume = unselectedList.get(i).getName();
				Map<String, Map<String, List<ApplicationComponentModel>>> writerMap = volume2ComponentsMap.get(volume.toLowerCase());
				if(writerMap != null && writerMap.size() > 0) {
					warnNumber++;
					addWaringIcon();
					StringBuilder strBuilder = new StringBuilder();
					for(String writerName : writerMap.keySet()) {
						strBuilder.append(writerName).append("(");

						Map<String, List<ApplicationComponentModel>> instMap = writerMap.get(writerName);
						for(String instName : instMap.keySet()) {

							if(!DefaultInstanceName.equals(instName))
								strBuilder.append(instName).append(": ");

							List<ApplicationComponentModel> compList = instMap.get(instName);
							for(ApplicationComponentModel comp : compList)
								strBuilder.append(comp.getName()).append(", ");

							if(!DefaultInstanceName.equals(instName)) {
								strBuilder.delete(strBuilder.length() - 2, strBuilder.length());
								strBuilder.append("; ");
							}
						}

						strBuilder.delete(strBuilder.length() - 2, strBuilder.length());
						strBuilder.append("), ");
					}

					strBuilder.delete(strBuilder.length() - 2, strBuilder.length());
					String warnMsg = UIContext.Messages.backupSettingsNotBackedAppComponents(unselectedList.get(i).getDisplayName(), strBuilder.toString());
					notificationSet.add(new LabelField(warnMsg));
				}
			}
		}
		
		for(VolumeModel volume : datastoreVolume) {
			String msg = UIContext.Messages.backupSettingsDataStoreVolume(UIContext.productNameRPS, volume.getDisplayName());
			warnNumber++;
			addWaringIcon();
			notificationSet.add(new LabelField(msg));
			
		}
		
		//if all the selected volumes are refs volume, disable doing catalog
//		if(refsVolumesList.size() >0 ){
//			if(volumesList.getIsFullMachine() == true){
//				if(gridStore.getModels().size() == refsVolumesList.size()){
//					parentWindow.getSettings().setDoCatlaogValue(false);
//					parentWindow.setIsAllVolumeIsRefsOrDedup(true);
//				}else{
//					parentWindow.getSettings().setDoCatlaogValue(true);
//				}
//			}else{
//				if(selectedList.size() == refsVolumesList.size()){
//					parentWindow.getSettings().setDoCatlaogValue(false);
//					parentWindow.setIsAllVolumeIsRefsOrDedup(true);
//				}else{
//					parentWindow.getSettings().setDoCatlaogValue(true);
//				}
//			}
//		}
		if(refsVolumesList.size() >0 || dedupeVolumesList.size()>0){
			int selectedVolumesSize=0;
			if(volumesList.getIsFullMachine() == true){
				selectedVolumesSize=gridStore.getModels().size();
			}else{
				selectedVolumesSize=selectedList.size();
			}
			//if all the selected volumes are refs volume or dedupe volume, disable file copy settings
			if(selectedVolumesSize == (refsVolumesList.size()+dedupeVolumesList.size())){
				if(BackupContext.isFileCopyEnable()){
					popupWarningMessage(UIContext.Constants.ArchiveCanNotEnableFileCopy());
					BackupContext.getArchiveSourceSettings().cbArchiveAfterBackup.setValue(false);
				}
				parentWindow.setIsAllVolumeIsRefsOrDedup(true);
			}else{
				parentWindow.setIsAllVolumeIsRefsOrDedup(false);
			}
			if(selectedVolumesSize == refsVolumesList.size()){
				parentWindow.getSimpleSchedule().getCatalogPanel().setDoCatlaogValue(false);
			}else{
				parentWindow.getSimpleSchedule().getCatalogPanel().setDoCatlaogValue(true);
			}
			
		}
		
		
		
		updateNotificationPane(warnNumber, infoNumber);
//		notificationSet.unmask();
	}
	
	private void popupWarningMessage(String message){
		MessageBox mb = new MessageBox();
		mb.setIcon(MessageBox.WARNING);
		mb.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
		mb.setModal(true);
		mb.setMinWidth(400);
		mb.setMessage(message);
		mb.show();
	}
	
	private String getVolNameOrGuid(VolumeModel volumeModel){
		String path = "";
		if(volumeModel==null){
			return path;
		}
		
		String displayName = volumeModel.getDisplayName();
		if((displayName!=null)&&(!displayName.isEmpty())){
			return displayName;
		}
		else{
			String volGUID = volumeModel.getGUID();
			if((volGUID!=null)&&volGUID.endsWith("\\")){
				volGUID = volGUID.substring(0,volGUID.length()-1);
			}
			return volGUID;
		}
	}

	private boolean isBackupDestChangedAndNextFull() {
		if(destChangedBackupTypeCont.isEnabled() && Boolean.TRUE.equals(destChangedFullBackup.getValue()))
			return true;

		return false;
	}

	private boolean addNotification(List<VolumeModel> unselectedList, VolumeModel vModel) {
		boolean added = false;
		if(isSystemVolume(vModel)) {
			addWaringIcon();
			notificationSet.add(new LabelField(UIContext.Messages.backupSettingsDestinationIsOnSystemVolume(vModel.getDisplayName())));
			added = true;
		}
		else if(isBootVolume(vModel)) {
			addWaringIcon();
			notificationSet.add(new LabelField(UIContext.Messages.backupSettingsDestinationIsOnBootVolume(vModel.getDisplayName())));
			added = true;
		}
		
		unselectedList.add(vModel);
		return added;
	}

	protected void updateNotificationPane(int warnNumber, int infoNumber) {
		notificationSet.setHeadingHtml(UIContext.Messages.backupSettingsNodifications(warnNumber));
		if(warnNumber == 0 && infoNumber == 0)
			notificationSet.collapse();
		else if(!notificationSet.isExpanded())
			notificationSet.expand();

		notificationSet.layout(true);
	}

	protected void addWaringIcon() {
		Image warningImage = getWaringIcon();
//		Image warningImage = UIContext.IconBundle.status_small_warning().createImage();
		addIcon(warningImage);
	}
	
	protected void addIcon(Image image) {
		TableData tableData = new TableData();
		tableData.setStyle("padding: 2px 3px 3px 0px;"); // refer to the GWT default setting.
		tableData.setVerticalAlign(VerticalAlignment.TOP);
		notificationSet.add(image, tableData);
	}
	
	protected void addInfoIcon() {
		Image infoImage = AbstractImagePrototype.create(UIContext.IconBundle.logMsg()).createImage();
		addIcon(infoImage);
	}

	protected Image getWaringIcon() {
		Image warningImage = AbstractImagePrototype.create(UIContext.IconBundle.logWarning()).createImage();
		return warningImage;
	}	
	
	public BackupVolumeModel getSelectedRefsVolumes()
	{		
		BackupVolumeModel volumeModel = new BackupVolumeModel();
		List<VolumeModel> children = gridStore.getModels();
		List<String> volumeList = new ArrayList<String>();
		for(VolumeModel volume : children) {
		if(volume.getFileSysType() == 8)
			volumeList.add((String)volume.get("name"));
		}
		volumeModel.allRefsVolumesList = volumeList;
		if(fullBackupRadio.getValue())
		{
			volumeModel.setIsFullMachine(Boolean.TRUE);			
		}
		else 
		{
			volumeModel.setIsFullMachine(Boolean.FALSE);
		}
		return volumeModel;		
	}
	
	
	public BackupVolumeModel getSelectedNtfsDedupeVolumes() 
	{		
		BackupVolumeModel volumeModel = new BackupVolumeModel();
		List<VolumeModel> children = gridStore.getModels();
		List<String> volumeList = new ArrayList<String>();
		for(VolumeModel volume : children) {
		if((volume.getFileSysType() == 2) && (volume.getIsDeduped().equalsIgnoreCase("1")))
			volumeList.add((String)volume.get("name"));
		}
		volumeModel.allDedupeVolumesList = volumeList;
		if(fullBackupRadio.getValue())
		{
			volumeModel.setIsFullMachine(Boolean.TRUE);			
		}
		else 
		{
			volumeModel.setIsFullMachine(Boolean.FALSE);
		}
		return volumeModel;		
	}
	
	private boolean systemReservedVolume(VolumeModel vModel)
	{
		//check for system Reserved partition
		if( ((vModel.getSubStatus() & VolumeSubStatus.EVSS_SYSTEM) > 0) && (vModel.getName().startsWith("\\\\?\\Volume")) && ((vModel.getSubStatus() & VolumeSubStatus.EVSS_BOOT) == 0))
			return true;
		return false;		
	}

	protected void getVolumeList(BackupVolumeModel volumeModel) {
		/*List<VolumeModel> children = gridStore.getModels();
		List<String> volumeList = new ArrayList<String>();
		for(VolumeModel child : children) {
//			FlashCheckBox checkBox = checkBoxMap.get(child);
			FlashCheckBox checkBox = getOrCreateFlashCheckBox(child);

			if(checkBox.getSelectedState() == FlashCheckBox.FULL)
				volumeList.add((String)child.get("name"));
		}
		volumeModel.selectedVolumesList = volumeList;*/
		
		//Kunma02
		List<VolumeModel> children = gridStore.getModels();
		List<String> volumeList = new ArrayList<String>();
		List<String> backupVolumeList = new ArrayList<String>();				
		List<String> refsVolumeListDetails = new ArrayList<String>();
		for(VolumeModel child : children) {
//			FlashCheckBox checkBox = checkBoxMap.get(child);
			FlashCheckBox checkBox = getOrCreateFlashCheckBox(child);

			if(checkBox.getSelectedState() == FlashCheckBox.FULL)
				volumeList.add((String)child.get("name"));
			
			if(checkBox.getSelectedState() == FlashCheckBox.FULL && !checkForRefsDedupvolumes(child) && !systemReservedVolume(child))
				backupVolumeList.add((String)child.get("name"));
			
			if(checkBox.getSelectedState() == FlashCheckBox.FULL && checkForRefsDedupvolumes(child) )
			{
				refsVolumeListDetails.add((String)child.get("name")+"("+getVolumeTypeName(child.getFileSysType())+")");					   
			}
			
		}
		volumeModel.selectedVolumesList = volumeList;
		volumeModel.backupSelectedVolumesList = backupVolumeList;
		volumeModel.backupSelectedRefsDedupeVolumesListDetails = refsVolumeListDetails;	
	}
	
	private boolean checkForRefsDedupvolumes(VolumeModel vModel)
	{
		//check for Refs FStype = 8
		if(vModel.getFileSysType() == 8 || ((vModel.getFileSysType() == 2) && (vModel.getIsDeduped().equalsIgnoreCase("1"))))
			return true;
		
		return false;
			
	}
	private boolean checkForRefsVolume(VolumeModel vModel)
	{
		//check for Refs FStype = 8
		if(vModel.getFileSysType() == 8)
			return true;
		
		return false;
			
	}
	private boolean checkForDedupVolume(VolumeModel vModel)
	{
		//check for Refs FStype = 8
		if((vModel.getFileSysType() == 2) && (vModel.getIsDeduped().equalsIgnoreCase("1")))
			return true;
		
		return false;
			
	}
	
	private String getVolumeTypeName(int type)
	{
		if(type == 8)
			return UIContext.Messages.refsVolumeName();
		else
			return UIContext.Messages.ntfsDedupeName();
	}
	
	protected String validateBackupSource() {
		String msgStr = null;
		BackupVolumeModel volumes = getBackupVolumes();
		if(volumes.getIsFullMachine()) {
			List<VolumeModel> treeVolumes = gridStore.getModels();
			StringBuilder volume2TBuilder = new StringBuilder();
			for (VolumeModel volume : treeVolumes) {
				if(VolumeSubStatus.isMountedFrom2TDisk(volume) && getCompressionLevel() == 0) {
					if(isLocalDisk()){
						if(currentBackupDestVolume!=null){
							String curBkDestVol = currentBackupDestVolume.getDisplayName();
							String curVol = volume.getDisplayName();
							if (0 != curBkDestVol.compareToIgnoreCase(curVol))
								volume2TBuilder.append(volume.getDisplayName()).append(", ");
						}
					}
					else{
						volume2TBuilder.append(volume.getDisplayName()).append(", ");
					}
				}
			}

			if(volume2TBuilder.length() > 0) {

				volume2TBuilder.delete(volume2TBuilder.length() - 2, volume2TBuilder.length());
				if(isNeedCheck2T())
					msgStr = UIContext.Messages.backupSettingsVolume2TBTypeFullMachineBackup(volume2TBuilder.toString());
			}
		}
		else{
			if(volumes.selectedVolumesList == null || volumes.selectedVolumesList.size() == 0) {
				msgStr = UIContext.Constants.destinationSelectAtLeastOneVolume();
			}
			else if(volumes.selectedVolumesList.size() == 1 && currentBackupDestVolume != null
					&& currentBackupDestVolume.getName().equalsIgnoreCase(volumes.selectedVolumesList.get(0))) {
				msgStr = UIContext.Constants.destinationSelectAtLeastOneVolumeButDest();
			}
			else if(localJobScriptCombo!=null){
				if(!volumes.getIsFullMachine()){
					msgStr = VCMMessages.destinationColdstandbySettingMsg();
				}
			}
		}
		return msgStr;
	}
	
	@Override
	protected boolean isUnsupportedBackupVolumeType(VolumeModel model) {
		return model.getIsShow() == 0 || VolumeSubStatus.isMountedFrom2TDisk(model) && isNeedCheck2T()
				|| !VolumeLayoutType.isBackupSupport(model.getLayout());
	}
	
	private boolean isNeedCheck2T() {		
		return isStandard && getCompressionLevel() == 0 && getEncryptAlgType() == 0;
	}

	@Override
	protected void createPathSelectionPanel() {
		super.createPathSelectionPanel();
		
		pathSelection.setChangeListener(new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				//only after user finish editing remote destination, we fetch remote destination details
				//and update pie chart.
				if(pieChartPanel != null)
					pieChartPanel.loadDestDiskDetailsAndUpdateChart(pathSelection);
			}
		});
	}

	
	@Override
	protected void volumesPane(boolean show) {
		gridVolumes.setVisible(show);
		totalSizeLabel.setVisible(show);
		selectAllCheckBox.setVisible(show);
	}

	private String removeEndSlash(String name) {
		if(name != null && (name.endsWith("\\") || name.endsWith("/")))
			name = name.substring(0, name.length() - 1);
		return name;
	}
	
	protected void refreshAllDatas() {
		updateNotificationMsg();
		pieChartPanel.updateEstimatedSize(this.getAndUpdateBackupSourceSize(), 	getRetentionCount());
		pieChartPanel.updateEstimatedPieChart();
	}

	private void selectAllVolumes(boolean selectedAll) {
		for(VolumeModel model : gridStore.getModels()) {
			FlashCheckBox checkBox = getOrCreateFlashCheckBox(model);
			if(checkBox.isEnabled()) {
				if(selectedAll)
					checkBox.setSelectedState(FlashCheckBox.FULL);
				else
					checkBox.setSelectedState(FlashCheckBox.NONE);
			}

			if(!isUnsupportedBackupVolumeType(model) && !isEditable)
				checkBox.setEnabled(isEditable);
		}
	}
	
	@Override
	public void setEditable(boolean editable) {
		super.setEditable(editable);
		gridVolumes.setEnabled(editable);
		pieChartPanel.setEditable(editable);
		totalSizeLabel.setEnabled(editable);
		selectAllCheckBox.setEnabled(editable);
	}	

	public void retentionPolicyChanged(boolean isBackupSet) {
		this.isBackupSet = isBackupSet;
		if(estBackupSizePanel == null)
			return;
		if(isBackupSet) {
			estBackupSizePanel.setVisible(false);
		}else {
			estBackupSizePanel.setVisible(true);
			pieChartPanel.loadDestDiskDetailsAndUpdateChart(pathSelection);
			this.refreshAllDatas();
		}
	}
	
	private long getAndUpdateBackupSourceSize() {
		long selectedVolSize = 0; 
		List<VolumeModel> volumes = gridStore.getModels();
		if(volumes == null || volumes.size() == 0)
			return 0;

		for (VolumeModel volume : volumes) {
			if(volume != currentBackupDestVolume) {
				FlashCheckBox box = getOrCreateFlashCheckBox(volume);
				Long total = volume.getTotalSize();
				Long free = volume.getFreeSize();
				if ((box.getSelectedState() == FlashCheckBox.FULL ||
						(fullBackupRadio.getValue() 
								&& volume.getMsgID() !=  VolumeGrayedReason.EVGR_VOL_NOT_SUPPORTED 
								&& (!backupDestChainSet.contains(volume) || isBackupDestChangedAndNextFull())))
					 && total != null && free != null)
					selectedVolSize += (volume.getTotalSize() - volume.getFreeSize());
			}
		}
		String sourceSize = Utils.bytes2String(selectedVolSize);
		totalSizeLabel.setValue(UIContext.Messages.backupSettingsSelectVolumesSize(sourceSize));
		return selectedVolSize;
	}
	
}
