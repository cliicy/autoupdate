package com.ca.arcflash.ui.client.vsphere.vmrecover;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.BackupVMModel;
import com.ca.arcflash.ui.client.model.RestoreJobModel;
import com.ca.arcflash.ui.client.model.VCloudStorageProfileModel;
import com.ca.arcflash.ui.client.model.VCloudVirtualDataCenterModel;
import com.ca.arcflash.ui.client.model.VMStorage;
import com.ca.arcflash.ui.client.restore.RestoreContext;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class VAppChildVMRecoveryWizard extends LayoutContainer {
	private VAppRecoveryOptionsWizard parentWizard;
	private VAppChildVMRecoveryWizard thisWizard;
	
	private LabelField restoreOptionLabel;
	private ComboBox<BackupVMModel> childVMComboBox;
	
	private Map<String, VAppChildVMRecoveryOptionsPanel> childVMPanelMap = new HashMap<>();
	private CardLayout cardLayout = new CardLayout();
	private LayoutContainer contentContainer = new LayoutContainer(cardLayout);
	private String currentVMInstanceUuid;
	
	private BackupVMModel vAppBackupVMModel;
	private VCloudVirtualDataCenterModel vDCModel;
	
	public VAppChildVMRecoveryWizard(VAppRecoveryOptionsWizard parentWizard) {
		this.parentWizard = parentWizard;
		this.thisWizard = this;
		this.setScrollMode(Scroll.NONE);
		this.setWidth("100%");
		this.setLayout(new RowLayout());
		this.vAppBackupVMModel = RestoreContext.getBackupVMModel();
		
		this.add(createHeaderAndChildVMComboBox());
		cardLayout = new CardLayout();
		contentContainer = new LayoutContainer(cardLayout);
		contentContainer.ensureDebugId("f749c6fa-f70f-49b1-b864-e5ed4b6e23d1");
		this.add(contentContainer, new RowData(1, 520));
		
		initChildVMPanelMap();
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		this.ensureDebugId("fd1b947d-d205-4aec-a594-b4493efc9546");
		this.vAppBackupVMModel = RestoreContext.getBackupVMModel();
	}
	
	@Override
	public void repaint() {
		super.repaint();
		
		BackupVMModel originalVMModel = this.vAppBackupVMModel;
		this.vAppBackupVMModel = RestoreContext.getBackupVMModel();
		
		if (originalVMModel != this.vAppBackupVMModel) {
			initChildVMPanelMap();
		}
	}
	
	public void enableNavigatorAndSwitch() {
		childVMComboBox.enable();
		restoreOptionLabel.enable();
	}

	public void disableNavigatorAndSwitch() {
		restoreOptionLabel.disable();
		childVMComboBox.disable();
	}
	
	public void activeSpecifiedVM(String vmInstanceUuid) {
		currentVMInstanceUuid = vmInstanceUuid;
		if (childVMComboBox != null && childVMComboBox.getStore() != null) {
			List<BackupVMModel> vmList = childVMComboBox.getStore().getModels();
			if (vmList != null && !vmList.isEmpty()) {
				for (BackupVMModel vmModel : vmList) {
					if (vmInstanceUuid.equals(vmModel.getVmInstanceUUID())) {
						childVMComboBox.setValue(vmModel);
						break;
					}
				}
			}
		}
		cardLayout.setActiveItem(childVMPanelMap.get(currentVMInstanceUuid));
	}
	
	public void setVCloudVDCModel(VCloudVirtualDataCenterModel vDCModel, final AsyncCallback<List<BackupVMModel>> outerCallBack) {
		this.vDCModel = vDCModel;
		
		final List<BackupVMModel> childVMModelList = new ArrayList<BackupVMModel>();
		if (!childVMPanelMap.isEmpty()) {
			List<String> instanceList = new ArrayList<String>(childVMPanelMap.keySet());
			setVDC2ChildVMRecoveryOptionsPanelRecursively(instanceList, 0, vDCModel, childVMModelList, outerCallBack);
		} else {
			outerCallBack.onSuccess(new ArrayList<BackupVMModel>());
		}
	}
	
	private void setVDC2ChildVMRecoveryOptionsPanelRecursively(final List<String> childVMIdList, final int childVMIndex,
			final VCloudVirtualDataCenterModel vDCModel, final List<BackupVMModel> childVMModelList,
			final AsyncCallback<List<BackupVMModel>> outerCallBack) {
		VAppChildVMRecoveryOptionsPanel panel = childVMPanelMap.get(childVMIdList.get(childVMIndex));
		panel.setVCloudVDCModel(vDCModel, new AsyncCallback<BackupVMModel>() {
			@Override
			public void onFailure(Throwable caught) {
				outerCallBack.onFailure(caught);
			}

			@Override
			public void onSuccess(BackupVMModel result) {
				if (result != null) {
					childVMModelList.add(result);
				}
				int nextVMIndex = childVMIndex + 1;
				if (nextVMIndex < childVMIdList.size()) {
					setVDC2ChildVMRecoveryOptionsPanelRecursively(childVMIdList, nextVMIndex, vDCModel,
							childVMModelList, outerCallBack);
				} else {
					outerCallBack.onSuccess(childVMModelList);
				}
			}
		});
	}
	
	public Map<String, VAppChildVMRecoveryOptionsPanel> getChildVMPanelMap() {
		return childVMPanelMap;
	}
	

	private Widget createHeaderAndChildVMComboBox() {
		FlexTable headerTable = new FlexTable();
		headerTable.setCellPadding(5);
		headerTable.setCellSpacing(5);
		
		Image image = AbstractImagePrototype.create(UIContext.IconBundle.restore_options()).createImage();
		headerTable.setWidget(0, 0, image);

		restoreOptionLabel = new LabelField(UIContext.Constants.restoreOptions() + " > ") {
			@Override
			protected void onRender(Element parent, int index) {
				super.onRender(parent, index);
				Style style = getElement().getStyle();
				style.setVerticalAlign(VerticalAlign.MIDDLE);
				style.setTextDecoration(TextDecoration.UNDERLINE);
				style.setCursor(Cursor.POINTER);
			}
		};
		restoreOptionLabel.ensureDebugId("6f9aec58-709b-458b-80fd-623525f967b8");
		restoreOptionLabel.setValue(UIContext.Constants.restoreOptions());
		restoreOptionLabel.setStyleName("restoreWizardTitle");
		restoreOptionLabel.setToolTip(UIContext.Constants.vAppRestoreNavigateBackTip());
		headerTable.setWidget(0, 1, restoreOptionLabel);
		restoreOptionLabel.addListener(Events.OnClick, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				parentWizard.activeVAppPanel();
			}
		});
		
		LabelField label = new LabelField(">");
		headerTable.setWidget(0, 2, label);
		
		childVMComboBox = new ComboBox<BackupVMModel>();
		childVMComboBox.ensureDebugId("fc566bf5-a958-4f70-8063-28a8cf3f670c");
		childVMComboBox.setStore(parentWizard.getChildBackupVMStore());
		childVMComboBox.setDisplayField("vmHostName");
		childVMComboBox.setTemplate(VAppRecoveryOptionsWizard.getComboBoxTemplate("vmHostName"));
		childVMComboBox.setAllowBlank(false);
		childVMComboBox.setValidateOnBlur(false);
		childVMComboBox.setTriggerAction(TriggerAction.ALL);
		childVMComboBox.setEditable(false);
		childVMComboBox.setWidth(250);
		childVMComboBox.setToolTip(UIContext.Constants.vAppRestoreVMNodeComboBoxTip());
		headerTable.setWidget(0, 3, childVMComboBox);
		
		childVMComboBox.addSelectionChangedListener(new SelectionChangedListener<BackupVMModel>() {
			@Override
			public void selectionChanged(SelectionChangedEvent<BackupVMModel> se) {
				if (se != null) {
					BackupVMModel selectedModel = se.getSelectedItem();
					if (selectedModel != null) {
						currentVMInstanceUuid = selectedModel.getVmInstanceUUID();
						VAppChildVMRecoveryOptionsPanel childPanel = childVMPanelMap.get(currentVMInstanceUuid);
						cardLayout.setActiveItem(childPanel);
					}
				}
			}
		});
		
		return headerTable;
	}
	
	private void initChildVMPanelMap() {
		childVMPanelMap.clear();
		
		if (vAppBackupVMModel.memberVMList != null && vAppBackupVMModel.memberVMList.size() > 0) {
			for (BackupVMModel childVM : vAppBackupVMModel.memberVMList) {
				String instanceUuid = childVM.getVmInstanceUUID();
				VAppChildVMRecoveryOptionsPanel panel = new VAppChildVMRecoveryOptionsPanel(parentWizard, thisWizard, childVM, vAppBackupVMModel.adapterList);
				contentContainer.add(panel);
				childVMPanelMap.put(instanceUuid, panel);
			}
		}
	}
	
	public void validate(AsyncCallback<Boolean> restoreCallback) {
		validateAllChildVMOptionsPanel(restoreCallback);
	}
	
	public void checkAllChildVMName(AsyncCallback<Map<String, ArrayList<String>>> callback) {
		ListStore<BackupVMModel> store = childVMComboBox.getStore();
		if (store == null || store.getCount() <= 0) {
			callback.onSuccess(null);
		} else {
			Map<String, ArrayList<String>> nameHostMap = new HashMap<>();
			List<BackupVMModel> childVMList = store.getModels();
			for (BackupVMModel vmModelOutter : childVMList) {
				for (BackupVMModel vmModelInner : childVMList) {
					if (vmModelOutter == vmModelInner) {
						continue;
					}
					
					String vmNameOutter = vmModelOutter.getVMName();
					String vmNameInner = vmModelInner.getVMName();
					if (vmNameOutter.equals(vmNameInner)) {
						ArrayList<String> instanceList = nameHostMap.get(vmNameOutter);
						if (instanceList == null) {
							instanceList = new ArrayList<String>();
						}
						childVMPanelMap.get(vmModelOutter.getVmInstanceUUID());
						instanceList.add(vmModelOutter.getVmHostName());
					}
				}
			}
			
			if (!nameHostMap.isEmpty()) {
				callback.onSuccess(nameHostMap);
			} else {
				callback.onSuccess(null);
			}
		}
	}
	
	public void checkTotalDatastoreSize(AsyncCallback<List<String[]>> callback) {
		Map<String, StorageStatisticInfo> neededStoageMap = new HashMap<>();
		Iterator<String> instanceIt = childVMPanelMap.keySet().iterator();
		while(instanceIt.hasNext()) {
			String instaceUuid = instanceIt.next();
			VAppChildVMRecoveryOptionsPanel childPanel = childVMPanelMap.get(instaceUuid);
			VMStorage configedStorage = childPanel.getConfigedVMStorage();
			if (configedStorage != null) {
				String storageId = configedStorage.getId();
				StorageStatisticInfo storageStatisticInfo = null;
				if (neededStoageMap.containsKey(storageId)) {
					storageStatisticInfo = neededStoageMap.get(storageId);
				} else {
					storageStatisticInfo = new StorageStatisticInfo(configedStorage);
					neededStoageMap.put(storageId, storageStatisticInfo);
				}
				storageStatisticInfo.addNeededSize(childPanel.getTotalDiskSize());
			}
		}
		
		if (!neededStoageMap.isEmpty() ) {
			List<String[]> reusltList = new ArrayList<>();
			Collection<StorageStatisticInfo> storageList = neededStoageMap.values();
			for (StorageStatisticInfo storage : storageList) {
				if (!storage.isEnough()) {
					reusltList.add(new String[] {storage.getStorageName(),
							Utils.bytes2String(storage.getDataStoreFreeSize()),
							Utils.bytes2String(storage.getTotalDiskSize())});
				}
			}
			if (!reusltList.isEmpty()) {
				callback.onSuccess(reusltList);
			} else {
				callback.onSuccess(null);
			}
		} else {
			callback.onSuccess(null);
		}
	}
	
	public void checkProfileTotalLimitedSizeAfterRequested(AsyncCallback<List<String[]>> callback) {
		Map<String, ProfileStatisticInfo> profileStatisticMap = new HashMap<>();
		Iterator<String> instanceIt = childVMPanelMap.keySet().iterator();
		while(instanceIt.hasNext()) {
			String instaceUuid = instanceIt.next();
			VAppChildVMRecoveryOptionsPanel childPanel = childVMPanelMap.get(instaceUuid);
			VCloudStorageProfileModel configedProfile = childPanel.getConfigedStorageProfile();
			if (configedProfile != null) {
				String profileId = configedProfile.getId();
				ProfileStatisticInfo profileStatisticInfo = null;
				if (profileStatisticMap.containsKey(profileId)) {
					profileStatisticInfo = profileStatisticMap.get(profileId);
				} else {
					profileStatisticInfo = new ProfileStatisticInfo(configedProfile);
					profileStatisticMap.put(profileId, profileStatisticInfo);
				}
				profileStatisticInfo.addNeededSize(childPanel.getTotalDiskSize());
			}
		}
		
		if (!profileStatisticMap.isEmpty() ) {
			List<String[]> reusltList = new ArrayList<>();
			Collection<ProfileStatisticInfo> profileList = profileStatisticMap.values();
			for (ProfileStatisticInfo profile : profileList) {
				if (!profile.isEnough()) {
					reusltList.add(new String[] {profile.getStorageProfileName(),
							Utils.bytes2String(profile.getProfileFreeSize()),
							Utils.bytes2String(profile.getTotalDiskSize())});
				}
			}
			if (!reusltList.isEmpty()) {
				callback.onSuccess(reusltList);
			} else {
				callback.onSuccess(null);
			}
		} else {
			callback.onSuccess(null);
		}
	}
	
	public void checkTotalMemorySize(AsyncCallback<String> callback) {
		//  if VDC memory model is AllocationPool, check the VM total memory size
		long totalMemorySize = 0L;
		Iterator<String> instanceIt = childVMPanelMap.keySet().iterator();
		while(instanceIt.hasNext()) {
			String instaceUuid = instanceIt.next();
			VAppChildVMRecoveryOptionsPanel childPanel = childVMPanelMap.get(instaceUuid);
			Long size = childPanel.getMmemorySizeTextField().getValue();
			if (size != null) {
				totalMemorySize += size;
			}
		}
		
		totalMemorySize = totalMemorySize * 1024 * 1024;
		long limitedSize = vDCModel.getMemoryLimit();
		if (totalMemorySize > limitedSize) {
			callback.onSuccess(UIContext.Messages.vAppRestoreTotalMemorySizeNotEnough(vDCModel.getName(), limitedSize, totalMemorySize));
		} else {
			callback.onSuccess(null);
		}
	}
	
	
	private void validateAllChildVMOptionsPanel(AsyncCallback<Boolean> outCallback) {
		List<String> instanceList = new ArrayList<>(childVMPanelMap.keySet());
		validateChildVMRecursively(instanceList, 0, outCallback);
	}
	
	private void validateChildVMRecursively(final List<String> instanceList, int index, final AsyncCallback<Boolean> outCallback) {
		final int nextIndex = index + 1;
		
		VAppChildVMRecoveryOptionsPanel childPanel = childVMPanelMap.get(instanceList.get(index));
		childPanel.validate(new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				if (result != null && result.equals(Boolean.FALSE)) {
					outCallback.onSuccess(Boolean.FALSE);
				} else if (nextIndex < instanceList.size()) {
					validateChildVMRecursively(instanceList, nextIndex, outCallback);
				} else {
					outCallback.onSuccess(Boolean.TRUE);
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				outCallback.onFailure(caught);
			}
		});
	}
	
	public int processOptions(RestoreJobModel vAppRestoreJob) {
		vAppRestoreJob.childRestoreJobList = null;
		
		if (childVMPanelMap.isEmpty()) {
			return 0;
		}
		
		List<RestoreJobModel> childVMJobList = new ArrayList<>();
		vAppRestoreJob.childRestoreJobList = childVMJobList;
		
		Iterator<String> instanceIt = childVMPanelMap.keySet().iterator();
		while (instanceIt.hasNext()) {
			String childVMInstanceUuid = instanceIt.next();
			VAppChildVMRecoveryOptionsPanel childPanel = childVMPanelMap.get(childVMInstanceUuid);
			RestoreJobModel childJobModel = childPanel.processOptions();
			
			childJobModel.recoverVMOption.setRegisterAsClusterHyperVVM(false);
			childJobModel.recoverVMOption.setOriginalLocation(false);
			childJobModel.recoverVMOption.setOverwriteExistingVM(vAppRestoreJob.recoverVMOption.isOverwriteExistingVM());
			childJobModel.recoverVMOption.setGenerateNewInstVMID(vAppRestoreJob.recoverVMOption.isGenerateNewInstVMID());//<huvfe01>###
			childJobModel.recoverVMOption.setEncryptPassword(vAppRestoreJob.recoverVMOption.getEncryptPassword());
			childJobModel.recoverVMOption.setPowerOnAfterRestore(false);
			childVMJobList.add(childJobModel);
		}
		
		return 0;
	}
	
	private class StorageStatisticInfo {
		private VMStorage dataStore;
		private long totalNeededSize; // bytes
		
		public StorageStatisticInfo(VMStorage dataStore) {
			this.dataStore = dataStore;
		}
		
		public void addNeededSize(long neededSize) {
			totalNeededSize += neededSize;
		}
		
		public boolean isEnough() {
			return dataStore.getFreeSize() >= totalNeededSize;
		}
		
		public String getStorageName() {
			return dataStore.getName();
		}
		
		public long getDataStoreFreeSize() {
			return dataStore.getFreeSize();
		}
		
		public long getTotalDiskSize() {
			return totalNeededSize;
		}
	}
	
	private class ProfileStatisticInfo {
		private VCloudStorageProfileModel profile;
		private long totalNeededSizeInBytes;
		
		public ProfileStatisticInfo(VCloudStorageProfileModel profile) {
			this.profile = profile;
		}
		
		public void addNeededSize(long neededSizeInKB) {
			totalNeededSizeInBytes += neededSizeInKB;
		}
		
		public boolean isEnough() {
			return profile.getFreeSize() >= totalNeededSizeInBytes;
		}
		
		public String getStorageProfileName() {
			return profile.getName();
		}
		
		public long getProfileFreeSize() {
			return profile.getFreeSize();
		}
		
		public long getTotalDiskSize() {
			return totalNeededSizeInBytes;
		}
	}
}