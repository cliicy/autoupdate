package com.ca.arcflash.ui.client.backup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.coldstandby.DisclourePanelImageBundles;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.FlashCheckBox;
import com.ca.arcflash.ui.client.common.LoadingStatus;
import com.ca.arcflash.ui.client.common.PathSelectionPanel;
import com.ca.arcflash.ui.client.common.d2d.presenter.SettingPresenter;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.ApplicationComponentModel;
import com.ca.arcflash.ui.client.model.ApplicationModel;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.ca.arcflash.ui.client.model.BackupVolumeModel;
import com.ca.arcflash.ui.client.model.FileModel;
import com.ca.arcflash.ui.client.model.VolumeGrayedReason;
import com.ca.arcflash.ui.client.model.VolumeLayoutType;
import com.ca.arcflash.ui.client.model.VolumeModel;
import com.ca.arcflash.ui.client.model.VolumeSubStatus;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.SimpleComboValue;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Image;

public abstract class BackupDestinationSettings extends BaseDestinationSettings {
	protected LoginServiceAsync loginService = GWT.create(LoginService.class);
	protected Radio fullBackupRadio;
	protected Radio selectVolumsRadio;
	protected CheckBox selectAllCheckBox;
	protected FieldSet notificationSet;
	protected Grid<VolumeModel> gridVolumes;
	protected LabelField totalSizeLabel;
	protected BackupVolumeModel originalVolumes;	
	protected HashSet<VolumeModel> backupDestChainSet = new HashSet<VolumeModel>();
	protected HashSet<VolumeModel> datastoreVolume = new HashSet<VolumeModel>();
	protected boolean isEditable = true;
	protected List<FileModel> filterBackupVolumes = new ArrayList<FileModel>();
	protected List<FileModel> unsupportedBackupVolumes = new ArrayList<FileModel>();
	//Save refs and ntfs dedup volumes, added by wanqi06
	protected List<FileModel> refsNtfsDedupBackupVolumes = new ArrayList<FileModel>();

	protected String DefaultInstanceName = "Default$Instance$Name#";
	
	protected volatile boolean isVolumnInitialized = false;
	boolean isUEFIFirmware = false;
	protected Map<String, Map<String,  Map<String, List<ApplicationComponentModel>>>> volume2ComponentsMap;
	
	
	public BackupDestinationSettings(BackupSettingsContent w) {
		super(w);
	}
	
	@Override
	protected void initSourceCotainer(LayoutContainer container) {
		initBackupSourceContainer(container);
	}	
	
	private void initBackupSourceContainer(LayoutContainer container) {
		DisclosurePanel sourceSettingsPanel = new DisclosurePanel((DisclourePanelImageBundles) 
				GWT.create(DisclourePanelImageBundles.class),
				UIContext.Constants.destinationBackupSource(), true);
		
		sourceSettingsPanel.ensureDebugId("1D132EB4-E781-40a3-AA07-E574B35539F8");
		sourceSettingsPanel.setWidth("100%");
		sourceSettingsPanel.setStylePrimaryName("gwt-DisclosurePanel-coldStandby");
		sourceSettingsPanel.setOpen(true);
		
		LayoutContainer sourceSettingsContainer = new LayoutContainer();

		RadioGroup rg = new RadioGroup();
		initFullBackupRadio(rg);
		sourceSettingsContainer.add(fullBackupRadio);

		initSelectVolumesRadio(rg);
		sourceSettingsContainer.add(selectVolumsRadio);

		LayoutContainer sourcePane = initVolumesGridContainer();
		initNofiticationSet(sourcePane);
		sourcePane.setWidth("96%");
		sourceSettingsContainer.add(sourcePane);
		sourceSettingsContainer.add(new Html("<HR>"));
		sourceSettingsPanel.add(sourceSettingsContainer);
		container.add(sourceSettingsPanel);
	}	

	private void initSelectVolumesRadio(RadioGroup rg) {
		selectVolumsRadio = new Radio();
		selectVolumsRadio.ensureDebugId("1C466E57-C76F-4291-A51C-369240C8DACF");
		selectVolumsRadio.setBoxLabel(UIContext.Constants.destinationBackupVolumes());
		selectVolumsRadio.setValue(false);
		selectVolumsRadio.addListener(Events.Change, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				if(selectVolumsRadio.getValue())
					volumesPane(true);
				else
					volumesPane(false);
				refreshAllDatas();
			}

		});
		rg.add(selectVolumsRadio);
	}

	private void initFullBackupRadio(RadioGroup rg) {
		fullBackupRadio = new Radio();
		fullBackupRadio.ensureDebugId("180DAFC7-63C3-40a5-A9A5-4F17C7FAEFE3");
		fullBackupRadio.setBoxLabel(UIContext.Constants.destinationBackupAll());
		fullBackupRadio.setValue(true);
		fullBackupRadio.addListener(Events.Change, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				if(fullBackupRadio.getValue()) {
					volumesPane(false);
				}
				else {
					volumesPane(true);
				}
				refreshAllDatas();
			}

		});
		rg.add(fullBackupRadio);
	}	
	
	private void initNofiticationSet(LayoutContainer container){
		notificationSet = new FieldSet();
		notificationSet.ensureDebugId("3C490EA4-3A30-4621-BA55-D127C049D9E5");
		notificationSet.setHeadingHtml(UIContext.Messages.backupSettingsNodifications(0));
		notificationSet.setCollapsible(true);
		TableLayout warningLayout = new TableLayout();
		warningLayout.setWidth("100%");
		warningLayout.setCellSpacing(1);
		warningLayout.setColumns(2);
		notificationSet.setLayout(warningLayout);

		warnLoadingStatus = new LoadingStatus();
		warnLoadingStatus.setLoadingMsg(UIContext.Constants.destinationLoadingWarnMsg());
		notificationSet.add(warnLoadingStatus);
		TableData data = new TableData();
		data.setWidth("100%");
		container.add(notificationSet, data);
	}
	
	protected abstract LayoutContainer initVolumesGridContainer();
	
	@Override
	protected LayoutContainer getBackupDestChangedTypePanel() {
		LayoutContainer container = super.getBackupDestChangedTypePanel();
		
		destChangedFullBackup.addListener(Events.Change, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				updateDestChainSelectable(false);
			}
		});
		return container;
	}
	


	private void updateDestChainSelectable(boolean forceDiable) {
		if(backupDestChainSet.size() > 0) {
			for (VolumeModel model : backupDestChainSet) {
//						if(!isUnsupportedBackupVolumeType(model)) {
				FlashCheckBox box = getOrCreateFlashCheckBox(model);
					if(model != currentBackupDestVolume) {
						if(!forceDiable) {
							Boolean destChanged = destChangedFullBackup.getValue();
							if(destChanged && (!VolumeSubStatus.isMountedFrom2TDisk(model) || getCompressionLevel() != 0))
								box.setEnabled(true);
							else
								box.setEnabled(false);
						}
						else
							box.setEnabled(false);
						box.setSelectedState(FlashCheckBox.NONE);
					}
//						}
			}
		}
	}
	
	protected abstract void loadVolumeTreeData(BackupSettingsModel model);	

	protected void refreshSkippedDiskIcon() {

		if(!isLocalDisk()) {
			if(currentBackupDestVolume == null)
				return;
		}
		else {
			String newDest = pathSelection.getDestination();
			if (isEmpty(newDest))
				return;

		}
		enableOldLocalDestVolume();
		updateSourceIcon();
		updateNotificationMsg();
	}

	protected void updateSourceIcon() {
		
	}
	
	protected void enableOldLocalDestVolume() {
		if(currentBackupDestVolume != null) {
			FlashCheckBox checkBox = getOrCreateFlashCheckBox(currentBackupDestVolume);
			if (!isUnsupportedBackupVolumeType(currentBackupDestVolume)
					|| currentBackupDestVolume.getMsgID() == VolumeGrayedReason.EVGR_VOL_ON_BACKUP_DEST_CHAIN
					&& isBackupDestChangedAndNextFull()
					&& !(VolumeSubStatus.isMountedFrom2TDisk(currentBackupDestVolume) && getCompressionLevel() == 0)) {
				checkBox.setEnabled(true);
			}
			currentBackupDestVolume = null;
		}
	}	
	
	protected String addPathSplitter(String dest) {
		if(!(dest.endsWith("\\") || dest.endsWith("/")))
			dest += "\\";
		return dest;
	}	

	@Override
	public void RefreshData(BackupSettingsModel model,boolean isEdit) {
		super.RefreshData(model, isEdit);
		if (model != null) {
			originalVolumes = model.getBackupVolumes();
			if (originalVolumes != null) {
				if (originalVolumes.getIsFullMachine() == null
						|| originalVolumes.getIsFullMachine()
						|| originalVolumes.selectedVolumesList == null
						|| originalVolumes.selectedVolumesList.size() == 0)
					fullBackupRadio.setValue(true);
				else {
					selectVolumsRadio.setValue(true);
				}
			}
		}
		
		processSourceAndBackupEstimate(model);
	}


	protected void processSourceAndBackupEstimate(BackupSettingsModel model) {
		// Tool tip
		compressionOption.addSelectionChangedListener(new SelectionChangedListener<SimpleComboValue<String>>() {
			Boolean fomerCompressLevel = null;
			@Override
			public void selectionChanged(
					SelectionChangedEvent<SimpleComboValue<String>> se) {
				CommonServiceAsync service = GWT.create(CommonService.class);
				service.isBackupCompressionLevelChangedWithLevel(getCompressionLevel(), new AsyncCallback<Boolean>(){
					@Override
					public void onFailure(Throwable caught) {
					}
					@Override
					public void onSuccess(Boolean result) {
						if(result.booleanValue()){
							warningContainer.setVisible(true);
						}else{
							warningContainer.setVisible(false);
						}
					}
				});
			}
		});		

		loadVolumeTreeData(model);
		loadAppVolumeDistr();
	}
	
	@Override
	public void Save() {
		super.Save();
		BackupVolumeModel volumes = getBackupVolumes();
		SettingPresenter.model.setBackupVolumes(volumes);
	}

	protected int getCompressionLevel(){
		int compressionLevel = -1;
		if (compressionOption.getSimpleValue() == UIContext.Constants.settingsCompressionNone())
			compressionLevel = 0;
		else if (compressionOption.getSimpleValue() == UIContext.Constants.settingsCompreesionStandard())
			compressionLevel = 1;
		else if (compressionOption.getSimpleValue() == UIContext.Constants.settingsCompressionMax())
			compressionLevel = 9;
		return compressionLevel;
	}

	private void loadAppVolumeDistr() {
		loginService.getExcludedAppComponents(null,
				new BaseAsyncCallback<ApplicationModel[]>() {

					public void onFailure(Throwable caught) {
						volume2ComponentsMap = new HashMap<String, Map<String,  
						Map<String, List<ApplicationComponentModel>>>>();
						updateNotificationMsg();
					}
					@Override
					public void onSuccess(ApplicationModel[] result) {
						volume2ComponentsMap = new HashMap<String, Map<String,  
						Map<String, List<ApplicationComponentModel>>>>();

						if (result != null && result.length > 0) {
							convertAppModel2Map(result);
						}
						updateNotificationMsg();
					}

					private void convertAppModel2Map(ApplicationModel[] result) {
						for (ApplicationModel model : result) {
							ApplicationComponentModel[] comps = model.getComponents();
							if (comps != null && comps.length > 0) {
								for (ApplicationComponentModel comModel : comps) {
									String[] mnts = comModel.getAffectedMnt();
									if (mnts != null && mnts.length > 0) {
										String appName = model.getAppName();
										String compName = comModel.getName();
										String instName = DefaultInstanceName;
										int index = compName.indexOf(":");
										if(index > 0) {
											instName = compName.substring(0, index);
											compName = compName.substring(index + 1);
											comModel.setName(compName);
										}

										Set<String> set = new HashSet<String>();
										for (String mnt : mnts) {
											String mntLower = removeEndSlash(mnt).toLowerCase();
											if(set.contains(mntLower))
												continue;
											set.add(mntLower);
											Map<String, Map<String, List<ApplicationComponentModel>>> writerMap = volume2ComponentsMap.get(mntLower);
											if (writerMap == null) {
												writerMap = new HashMap<String,  Map<String, List<ApplicationComponentModel>>>();
												volume2ComponentsMap.put(mntLower,writerMap);

											}

											Map<String, List<ApplicationComponentModel>> instMap = writerMap.get(appName);
											if(instMap == null) {
												instMap = new HashMap<String, List<ApplicationComponentModel>>();
												writerMap.put(appName, instMap);
											}

											List<ApplicationComponentModel> compList = instMap.get(instName);
											if(compList == null) {
												compList = new ArrayList<ApplicationComponentModel>();
												instMap.put(instName, compList);
											}
											compList.add(comModel);
											comModel.set("parent", model);
										}
									}
								}
							}
						}
					}
				});
	}	

	protected abstract void updateNotificationMsg();	
	

	private boolean isBackupDestChangedAndNextFull() {
		if(destChangedBackupTypeCont.isEnabled() && Boolean.TRUE.equals(destChangedFullBackup.getValue()))
			return true;

		return false;
	}

	protected boolean isSystemVolume(VolumeModel model) {
		return (model.getSubStatus() & VolumeSubStatus.EVSS_SYSTEM) > 0;
	}
	
	protected boolean isBootVolume(VolumeModel model) {
		return (model.getSubStatus() & VolumeSubStatus.EVSS_BOOT) > 0;
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

	protected abstract void getVolumeList(BackupVolumeModel volumeModel);
	
	public BackupVolumeModel getBackupVolumes() {
		BackupVolumeModel volumeModel = new BackupVolumeModel();

		if(fullBackupRadio.getValue())
			volumeModel.setIsFullMachine(Boolean.TRUE);
		else {
			volumeModel.setIsFullMachine(Boolean.FALSE);
			getVolumeList(volumeModel);
		}

		return volumeModel;
	}	
	
	@Override
	protected String getErrorMessageTitle(){
		return UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D);
	}
	
	@Override
	protected String validateSource() {
		return validateBackupSource();
	}
	
	protected abstract String validateBackupSource();
	
	@Override
	protected void createPathSelectionPanel() {
		pathSelection = new PathSelectionPanel(this.parentWindow.isForEdge(), new Listener<FieldEvent>(){
			@Override
			public void handleEvent(FieldEvent be) {
				String newDest = pathSelection.getDestination();
				setDestChangedBackupType(newDest);
				refreshSkippedDiskIcon();
			}
		});
	}
	
	@Override
	protected void onBackupTypeChangedPathSame() {
		if(destChangedBackupTypeCont.isEnabled() && destChangedFullBackup.getValue()) {
			updateDestChainSelectable(true);
		}
		destChangedBackupTypeCont.disable();
		updateNotificationMsg();
	}

	public boolean isVolumeSelectionChanges() {
		BackupVolumeModel volumes = getBackupVolumes();
		//check whether volume selection changes if not first time configuring backup setting.
		boolean isVolumeSelectChanges = false;
		if(oldDestinationPath != null && oldDestinationPath.length() > 0)
		{
			if(!volumes.getIsFullMachine().equals(originalVolumes.getIsFullMachine())) {
				isVolumeSelectChanges = true;
			}
			else if(!volumes.getIsFullMachine()) {
				if(originalVolumes.selectedVolumesList == null || originalVolumes.selectedVolumesList.size() == 0)
					isVolumeSelectChanges = true;
				else if(!volumes.selectedVolumesList.containsAll(originalVolumes.selectedVolumesList)
						|| !originalVolumes.selectedVolumesList.containsAll(volumes.selectedVolumesList))
					isVolumeSelectChanges = true;
			}
		}

		return isVolumeSelectChanges;
	}

	protected abstract void volumesPane(boolean show);

	private String removeEndSlash(String name) {
		if(name != null && (name.endsWith("\\") || name.endsWith("/")))
			name = name.substring(0, name.length() - 1);
		return name;
	}
	
	protected abstract void refreshAllDatas();	

	protected boolean isUnsupportedBackupVolumeType(VolumeModel model) {
		return model.getIsShow() == 0 ||  VolumeSubStatus.isMountedFrom2TDisk(model) && getCompressionLevel() == 0
				|| !VolumeLayoutType.isBackupSupport(model.getLayout());
	}

	protected FlashCheckBox getOrCreateFlashCheckBox(VolumeModel model) {
		FlashCheckBox box = (FlashCheckBox)model.get("FlashCheckBox");
		if(box == null) {
			box = new FlashCheckBox();
			final FlashCheckBox fbox = box;
			if(isUnsupportedBackupVolumeType(model)) {
				box.setEnabled(false);
			}
			
			if(model.getDataStore() != null && !model.getDataStore().isEmpty())
				box.setEnabled(false);
			
			box.addSelectionListener(new SelectionListener<IconButtonEvent>() {

				@Override
				public void componentSelected(IconButtonEvent ce) {
					if(fbox.getSelectedState() == FlashCheckBox.NONE)
						if(selectAllCheckBox != null)
							selectAllCheckBox.setValue(false);
					refreshAllDatas();
				}

			});

			model.set("FlashCheckBox", box);
		}
		return box;
	}	

	@Override
	public void setEditable(boolean editable) {
		super.setEditable(editable);
		isEditable  = editable;
		//
		fullBackupRadio.setEnabled(editable);
		selectVolumsRadio.setEnabled(editable);	
	}	

	
	public List<FileModel> getFilterBackupVolumesForFullMachine() {
		List<FileModel> models = new ArrayList<FileModel>();
		models.addAll(filterBackupVolumes);
		models.addAll(unsupportedBackupVolumes);
		models.addAll(datastoreVolume);
		return models;
	}	
	//Return all the refs and ntfs dedup volumes, added by wanqi06
	public List<FileModel> getRefsNtfsVolumesForFullMachine() {
		List<FileModel> models = new ArrayList<FileModel>();
		models.addAll(refsNtfsDedupBackupVolumes);
		return models;
	}
	
//	public int getRetentionCount(){
//		return backupSetContainer.getRetentionCount();
//	}
}
