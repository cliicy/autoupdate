package com.ca.arcflash.ui.client.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.BackupSettingsContent;
import com.ca.arcflash.ui.client.common.d2d.presenter.SettingPresenter;
import com.ca.arcflash.ui.client.export.ScheduledExportSettingsContent;
import com.ca.arcflash.ui.client.homepage.PreferencesSettingsContent;
import com.ca.arcflash.ui.client.vsphere.setting.VSphereBackupSettingContent;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.core.FastMap;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.data.ModelKeyProvider;
import com.extjs.gxt.ui.client.data.ModelStringProvider;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.CardPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class VSphereCommonSettingTree extends BaseCommonSettingTab{

		private boolean isD2DSaved = false;
		private TreePanel<ModelData> treePanel;
		private TreeStore<ModelData> treeStore;
//		private StoreFilterField<ModelData> storeFilter;
		protected boolean isInit = false;

		private CardPanel rightPanel;

		private LayoutContainer d2dSettingContentPanel;
		private LayoutContainer preferenceContentPanel;
		private LayoutContainer exportRecPointContentPanel;

		// backup Data
		private static ModelData backupSetting = new BaseModelData();
		private static ModelData backupScheduleSummary = new BaseModelData();
		private static ModelData backupScheduleRepeat = new BaseModelData();
		private static ModelData backupScheduleDaily = new BaseModelData();

		private static ModelData backupProtection = new BaseModelData();
		private static ModelData backupAdvance = new BaseModelData();
		private static ModelData backupPrePost = new BaseModelData();

		// preference
		private static ModelData preferenceSetting = new BaseModelData();
		private static ModelData preferenceUpdate = new BaseModelData();
		private static ModelData preferenceGeneral = new BaseModelData();
		private static ModelData preferenceEmailAlert = new BaseModelData();
		
		// CRP Data
		private static ModelData copyRecoveryPointSetting = new BaseModelData();
		private static ModelData copyRecoveryPointCopy = new BaseModelData();

		private static Map<String, ModelData> leftRightmap = new FastMap<ModelData>();

		private static String[] rootText = new String[] { UIContext.Constants.backupSettingsWindow(),
				UIContext.Constants.preferences(), UIContext.Constants.homepageTasksArchiveSettingLabel(),
				UIContext.Constants.scheduledExportSettings() };
		private static ModelData[] rootData = { backupSetting, preferenceSetting,copyRecoveryPointSetting};

		// backup tree
		private static String[] advScheduleChildren = new String[] { UIContext.Constants.scheduleMenuRepeat(),
				UIContext.Constants.scheduleMenuDailyWeeklyMonthly() };
		private static String[] backupChildrenText = new String[] { UIContext.Constants.backupSettingsDestination(),
				UIContext.Constants.backupSettingsSchedule(), UIContext.Constants.backupSettingsSchedule(),
				UIContext.Constants.backupSettingsSettings(), UIContext.Constants.backupSettingsPrePost() };
		private static ModelData[] backupChildrenData = { backupProtection, backupScheduleSummary, backupScheduleSummary,
				backupAdvance, backupPrePost };
		private static int[] backupChildrenIndex = { BackupSettingsContent.STACK_DESTINATION,
				BackupSettingsContent.STACK_SCHEDULE_Adv, BackupSettingsContent.STACK_SCHEDULE_SIMPLE,
				BackupSettingsContent.STACK_SETTINGS, BackupSettingsContent.STACK_ADVANCED };

		// Preference tree
		private static String[] preferenceChildrenText = new String[] { UIContext.Constants.preferencesGeneralLabel(),
				UIContext.Constants.preferencesEmailAlertsLabel(), UIContext.Constants.preferencesUpdatesLabel() };
		private static ModelData[] preferenceChildrenData = { preferenceGeneral, preferenceEmailAlert, preferenceUpdate };
		private static int[] preferenceChildrenIndex = { PreferencesSettingsContent.STACK_GENERAL,
				PreferencesSettingsContent.STACK_EMAILALERTS, PreferencesSettingsContent.STACK_SELFUPDATE };

		static {
			int i = 0;
			for (ModelData md : rootData) {
				md.set("name", rootText[i++]);
			}
			// backup
			i = 0;
			for (ModelData md : backupChildrenData) {
				md.set("name", backupChildrenText[i]);
				md.set("parent", backupSetting);
				leftRightmap.put(BaseCommonSettingTab.d2dBackupSettingID + ":" + backupChildrenIndex[i], md);
				i++;
			}
			backupScheduleRepeat.set("name", advScheduleChildren[0]);
			backupScheduleRepeat.set("parent", backupScheduleSummary);
			backupScheduleDaily.set("name", advScheduleChildren[1]);
			backupScheduleDaily.set("parent", backupScheduleSummary);

			leftRightmap.put(BaseCommonSettingTab.d2dBackupSettingID + ":" + BackupSettingsContent.STACK_PERIODICALLY,
					backupScheduleDaily);
			leftRightmap.put(BaseCommonSettingTab.d2dBackupSettingID + ":" + BackupSettingsContent.STACK_REPEAT,
					backupScheduleRepeat);

			// preference
			i = 0;
			for (ModelData md : preferenceChildrenData) {
				md.set("name", preferenceChildrenText[i]);
				md.set("parent", preferenceSetting);
				leftRightmap.put(BaseCommonSettingTab.d2dPreferenceSettingID + ":" + preferenceChildrenIndex[i], md);
				i++;
			}

			// CRP
			copyRecoveryPointCopy.set("name", UIContext.Constants.CopySettings());
			copyRecoveryPointCopy.set("parent", copyRecoveryPointSetting);
			leftRightmap.put(BaseCommonSettingTab.scheduledExportSettingsID + ":"
					+ ScheduledExportSettingsContent.STACK_SCHEDULEDEXPORT, copyRecoveryPointCopy);
		}

		public VSphereCommonSettingTree(SettingsGroupType settingsGroupType, boolean isForEdge,
				ISettingsContentHost contentHost) {
			super(settingsGroupType, isForEdge, contentHost, false);
			this.setLayout(new RowLayout());
			addSettingsContent();

			SettingPresenter.getInstance().addListener(new Listener<AppEvent>() {
				@Override
				public void handleEvent(AppEvent be) {
					if (be.<Integer> getData("format") == 0) {
						treePanel.getSelectionModel().setLocked(true);
						treeStore.remove(backupScheduleSummary, backupScheduleRepeat);
						treeStore.remove(backupScheduleSummary, backupScheduleDaily);
						SettingPresenter.getInstance().setAdvSchedule(false);
						treePanel.getSelectionModel().setLocked(false);
					} else {
						treeStore.insert(backupScheduleSummary, backupScheduleRepeat, 0, false);
						treeStore.insert(backupScheduleSummary, backupScheduleDaily, 1, false);
						SettingPresenter.getInstance().setAdvSchedule(true);
					}
				}
			});
			
			
			SettingPresenter.getInstance().addValidateListener(new Listener<AppEvent>() {
				@Override
				public void handleEvent(AppEvent be) {
					switchSelectNode(SettingPresenter.currentRootSelectionIndex,SettingPresenter.currentChildSelectionIndex);
				}			
			});

		}

		private int leftWidth = 180;

		public void setLeftWidth(int leftWidth) {
			this.leftWidth = leftWidth;
		}

		protected void init() {
			LayoutContainer panel = new LayoutContainer();
			BorderLayout bl = new BorderLayout();
			panel.setLayout(bl);

			// treeLoader = new BaseTreeLoader<ModelData>(new
			// TreeModelReader<List<ModelData>>());

			// treeStore = new TreeStore<ModelData>(treeLoader);
			treeStore = new TreeStore<ModelData>();
			treeStore.setKeyProvider(new ModelKeyProvider<ModelData>() {

				@Override
				public String getKey(ModelData model) {

					String key = model.get("key");
					if (key != null) {
						return key;
					}

					key = model.get("name");

					ModelData parent = model;
					while ((parent = parent.get("parent")) != null) {
						key += "@" + parent.get("name");
					}

					model.set("key", key);
					return key;
				}
			});

			treePanel = new TreePanel<ModelData>(treeStore);
			treePanel.setBorders(true);
			treePanel.setDisplayProperty("name");
			treePanel.setWidth(leftWidth);
			treePanel.setHeight(210);
			treePanel.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
			treePanel.setStyleAttribute("background-color", "#FFFFFF");
			treePanel.setLabelProvider(new ModelStringProvider<ModelData>() {
				public String getStringValue(ModelData model, String property) {
					return model.get("name");
				}
			});

			treePanel.setIconProvider(new ModelIconProvider<ModelData>() {
				@Override
				public AbstractImagePrototype getIcon(ModelData model) {

					ImageResource icon = null;
					String name = model.get("name");
					// -----root tree node: 4 settings--------
					if (UIContext.Constants.backupSettingsWindow().equals(name)) {
						icon = UIContext.IconBundle.d2d_backup_settings();
					} else if (UIContext.Constants.homepageTasksArchiveSettingLabel().equals(name)) {
						icon = UIContext.IconBundle.d2d_filecopy_settings();
					} else if (UIContext.Constants.scheduledExportSettings().equals(name)) {
						icon = UIContext.IconBundle.d2d_reconverypoints_settings();
					} else if (UIContext.Constants.preferences().equals(name)) {
						icon = UIContext.IconBundle.d2d_preference_settings();
					}// -----sub tree of : backup settings--------
					else if (UIContext.Constants.backupSettingsDestination().equals(name)) {
						icon = UIContext.IconBundle.tree_backup_settings_protection();
					} else if (UIContext.Constants.backupSettingsSchedule().equals(name)) {
						icon = UIContext.IconBundle.tree_backup_settings_schedule();
					} else if (UIContext.Constants.backupSettingsSettings().equals(name)) {
						icon = UIContext.IconBundle.tree_backup_settings_settings();
					} else if (UIContext.Constants.backupSettingsPrePost().equals(name)) {
						icon = UIContext.IconBundle.tree_backup_settings_advanced();
					}// -----sub tree of : Preference settings--------
					else if (UIContext.Constants.preferencesGeneralLabel().equals(name)) {
						icon = UIContext.IconBundle.tree_pref_settings_general();
					} else if (UIContext.Constants.preferencesEmailAlertsLabel().equals(name)) {
						icon = UIContext.IconBundle.tree_pref_settings_emailalert();
					} else if (UIContext.Constants.preferencesUpdatesLabel().equals(name)) {
						icon = UIContext.IconBundle.tree_pref_settings_update();
					}
					if(icon == null) return null;
					return AbstractImagePrototype.create(icon);
				}

			});

			treePanel.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
				@Override
				public void selectionChanged(SelectionChangedEvent<ModelData> se) {
					handleSelectChanged(se.getSelectedItem());
				}
			});

//			storeFilter = new StoreFilterField<ModelData>() {
//				@Override
//				protected boolean doSelect(Store<ModelData> store, ModelData parent, ModelData record, String property,
//						String filter) {
//					String name = record.get("name");
//					name = name.toLowerCase();
//					String key = record.get("key");
//					if (filter == null) {
//						return true;
//					}
//
//					filter = filter.trim().toLowerCase();
//
//					if (key != null && key.toLowerCase().indexOf(filter) >= 0) {
//						return true;
//					}
//					return false;
//				}
//			};
//			storeFilter.setEmptyText(UIContext.Constants.typeFilterText());
//
//			storeFilter.bind(treeStore);

			LayoutContainer leftPanel = new LayoutContainer();
			leftPanel.setLayout(new RowLayout());
			leftPanel.addStyleName("x-small-editor");
//			leftPanel.add(storeFilter, new RowData(-1, -1));
			leftPanel.add(treePanel, new RowData(1, 1, new Margins(0, 0, 0, 0)));

			rightPanel = new CardPanel();
			rightPanel.setScrollMode(Scroll.AUTO);
			this.setScrollMode(Scroll.AUTO);

			BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, leftWidth);
			westData.setSplit(true);
			westData.setCollapsible(true);

			BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
			panel.add(leftPanel, westData);
			panel.add(rightPanel, centerData);

			add(panel, new RowData(1, 1));

			isInit = true;
		}

		private void handleSelectChanged(ModelData selModelItem) {
			if (selModelItem == null)
				return;

			ModelData selItem = selModelItem;
			boolean isD2DSetting = false;
			boolean isPreference = false;
			boolean isExportRecPoint = false;

			String selectionName = selItem.get("name");

			ModelData parentNode = selItem.get("parent");
			String rootName = null;
			// get the root Node
			if (parentNode != null) {
				while (parentNode.get("parent") != null) {
					parentNode = parentNode.get("parent");
				}
				rootName = parentNode.get("name");
			} else {
				rootName = selectionName; // root is selected.
			}

			if (UIContext.Constants.backupSettingsWindow().equals(rootName)) {
				isD2DSetting = true;
			} else if (UIContext.Constants.preferences().equals(rootName)) {
				isPreference = true;
			} else if (UIContext.Constants.scheduledExportSettings().equals(rootName)) {
				isExportRecPoint = true;
			}

			if (isD2DSetting) {
				if (UIContext.Constants.backupSettingsPrePost().equals(selItem.get("name"))) { // pre/post
					d2dSettingsContent.deckPanel.showWidget(d2dSettingsContent.STACK_ADVANCED);
					VSphereBackupSettingContent.setButtonSelected(4);
				} else if (UIContext.Constants.backupSettingsDestination().equals(selItem.get("name"))) {
					d2dSettingsContent.deckPanel.showWidget(d2dSettingsContent.STACK_DESTINATION);
					VSphereBackupSettingContent.setButtonSelected(1);
				} else if (UIContext.Constants.backupSettingsSchedule().equals(selItem.get("name"))) {
					VSphereBackupSettingContent.setButtonSelected(2);
					if (SettingPresenter.getInstance().isAdvSchedule()) {
						d2dSettingsContent.deckPanel.showWidget(d2dSettingsContent.STACK_SCHEDULE_Adv);
					} else {
						d2dSettingsContent.deckPanel.showWidget(d2dSettingsContent.STACK_SCHEDULE_SIMPLE);
					}
				} else if (UIContext.Constants.backupSettingsSettings().equals(selItem.get("name"))) { // advanced
					d2dSettingsContent.deckPanel.showWidget(d2dSettingsContent.STACK_SETTINGS);
					VSphereBackupSettingContent.setButtonSelected(3);
				}

//				boolean isSchedule = selItem.get("parent") != null
//						&& UIContext.Constants.backupSettingsSchedule().equals(
//								selItem.<ModelData> get("parent").get("name")) ? true : false;
//				if (isSchedule) {
//					if (advScheduleChildren[0].equals(selItem.get("name"))) {
//						d2dSettingsContent.deckPanel.showWidget(d2dSettingsContent.STACK_REPEAT);
//						VSphereBackupSettingContent.setButtonSelected(5);
//					} else if (advScheduleChildren[1].equals(selItem.get("name"))) {
//						d2dSettingsContent.deckPanel.showWidget(d2dSettingsContent.STACK_PERIODICALLY);
//						VSphereBackupSettingContent.setButtonSelected(6);
//					}
//				}
			}
			
			if (isExportRecPoint) {
				//if (UIContext.Constants.CopySettings().equals(selItem.get("name"))) {
					scheduledExportSettingsContent.deckPanel
							.showWidget(scheduledExportSettingsContent.STACK_SCHEDULEDEXPORT);
					scheduledExportSettingsContent.loadData();
					scheduledExportSettingsContent.enableEditing(false);
				//}
			}

			if (isPreference) {
				preferenceconContent.deckPanel.showWidget(0);
			}

			if (isD2DSetting) {
				// rightPanel.showWidget(0);
				rightPanel.setActiveItem(d2dSettingContentPanel);
				BaseCommonSettingTab.presentTabSelectionIndex = BaseCommonSettingTab.vsphereSettingID;
			} else if (isPreference) {
				// rightPanel.showWidget(1);
				BaseCommonSettingTab.presentTabSelectionIndex = BaseCommonSettingTab.vspherePreferenceID;
				rightPanel.setActiveItem(preferenceContentPanel);
			} else if (isExportRecPoint) {
				// rightPanel.showWidget(2);
				rightPanel.setActiveItem(exportRecPointContentPanel);
				BaseCommonSettingTab.presentTabSelectionIndex = BaseCommonSettingTab.scheduledExportSettingsID;
			}
		}

		private void switchSelectNode(int root, int child) {
			ModelData selNode = leftRightmap.get(root+":"+child);
			this.treePanel.getSelectionModel().select(false, selNode);
		}

		@Override
		protected void addSettingsContent() {
			String hostTitle = initSettingsContentList();
			for (SettingsContentEntry contentEntry : this.settingsContentList) {
				ISettingsContent settingsContent = contentEntry.getContentObject();
				settingsContent.initialize(contentHost, false);
				settingsContent.setId(contentEntry.getId());

				ModelData treeNode = new BaseModelData();
				String rootName = contentEntry.getDisplayName();
				treeNode.set("name", rootName);
				treeStore.add(treeNode, false);
				List<ModelData> mdList = new ArrayList<ModelData>();
				if (map.get(contentEntry.getDisplayName()) != null) {
					for (String value : map.get(contentEntry.getDisplayName())) {
						ModelData md = new BaseModelData();
						md.set("name", value);
						md.set("parent", treeNode);
						mdList.add(md);
					}
				}
				treeStore.insert(treeNode, mdList, 0, true);
//				addSubTreeNode(mdList);
			}

			d2dSettingContentPanel = new LayoutContainer();
			// contentPanel.setWidth("100%");
			// contentPanel.setHeight("100%");
			d2dSettingContentPanel.setLayout(new RowLayout());
			d2dSettingContentPanel.add(d2dSettingsContent.deckPanel);
			rightPanel.add(d2dSettingContentPanel);
			
			exportRecPointContentPanel = new LayoutContainer();
			exportRecPointContentPanel.add(scheduledExportSettingsContent.deckPanel);
			rightPanel.add(exportRecPointContentPanel);

			preferenceContentPanel = new LayoutContainer();
			preferenceContentPanel.add(preferenceconContent.deckPanel);
			rightPanel.add(preferenceContentPanel);

			setHostTitle(hostTitle);
		}

//		private void addSubTreeNode(List<ModelData> parentList) {
//			for (ModelData parent : parentList) {
//				addSubTreeNode(parent);
//			}
//		}

		@Override
		protected void onLoad() {
			super.onLoad();
			treePanel.expandAll();
			ModelData mdDefaultSelectionParent = new BaseModelData();
			mdDefaultSelectionParent.set("name", UIContext.Constants.backupSettingsWindow());
			ModelData mdDefaultSelection = new BaseModelData();

			mdDefaultSelection.set("name", UIContext.Constants.backupSettingsDestination());
			mdDefaultSelection.set("parent", mdDefaultSelectionParent);
			treePanel.getSelectionModel().select(mdDefaultSelection, false);
			// treePanel.fireEvent(Events.SelectionChange,
			// new SelectionChangedEvent<ModelData>(null, mdDefaultSelection));
			// handleSelectChanged(mdDefaultSelection);
			// rightPanel.showWidget(0);

		}

//		private void addSubTreeNode(ModelData parent) {
//			ModelData parentofParentNode = parent.get("parent");
//			boolean isBackupSetting = UIContext.Constants.backupSettingsWindow().equals(parentofParentNode.get("name")) ? true
//					: false;
//
//			if (isBackupSetting && UIContext.Constants.backupSettingsSchedule().equals(parent.get("name"))) {
//
//				// d2dSettingsContent.deckPanel.showWidget(d2dSettingsContent.STACK_SCHEDULE);
//
//				List<ModelData> mdList = new ArrayList<ModelData>();
//				for (String value : advScheduleChildren) {
//					ModelData md = new BaseModelData();
//					md.set("name", value);
//					md.set("parent", parent);
//					mdList.add(md);
//				}
//
//				treeStore.insert(parent, mdList, 0, true);
//			}
//		}

		protected Map<String, List<String>> map;

		private BackupSettingsContent d2dSettingsContent;
		private CommonPreferenceSettings preferenceconContent;
		private ScheduledExportSettingsContent scheduledExportSettingsContent;

		@Override
		protected String initSettingsContentList() {
			if (!isInit)
				init();
			map = new FastMap<List<String>>();
			String hostTitle = UIContext.Constants.homepageTasksBackupSettingLabel();
			d2dSettingsContent = new VSphereBackupSettingContent(settingsGroupType);
			d2dSettingsContent.setD2D(false);

			String vsphereTab = UIContext.Constants.backupSettingsWindow();
			settingsContentList.add(new SettingsContentEntry(vsphereSettingID, vsphereTab, d2dSettingsContent,
					vsphereSettingID, AbstractImagePrototype.create(UIContext.IconBundle.vsphere_backup_settings())));

			map.put(vsphereTab, d2dSettingsContent.itemsToDisplay);
			
			scheduledExportSettingsContent = new ScheduledExportSettingsContent(SettingsGroupType.VMBackupSettings);
			String scheduledExportSettingsTab = UIContext.Constants.scheduledExportSettings();
			settingsContentList.add(new SettingsContentEntry(scheduledExportSettingsID, scheduledExportSettingsTab,
					scheduledExportSettingsContent, scheduledExportSettingsID, AbstractImagePrototype.create(UIContext.IconBundle
							.d2d_reconverypoints_settings())));

			map.put(scheduledExportSettingsTab, scheduledExportSettingsContent.itemsToDisplay);

			preferenceconContent = new CommonPreferenceSettings(SettingsGroupType.VMBackupSettings);
			String vspherePrefrenceTab = UIContext.Constants.preferences();
			settingsContentList.add(new SettingsContentEntry(vspherePreferenceID, vspherePrefrenceTab, preferenceconContent,
					vspherePreferenceID, AbstractImagePrototype.create(UIContext.IconBundle.vsphere_preference_settings())));

			map.put(vspherePrefrenceTab, preferenceconContent.settingNameList);

			Utils.connectionCache = new HashMap<String, String[]>();
			return hostTitle;
		}
		
		@Override
		protected void enableEdit(int settingsId, ISettingsContent content) {
			if(content instanceof VSphereBackupSettingContent)
				((VSphereBackupSettingContent)content).enableEditing(this.isForEdge);
			
		}

		@Override
		protected void saveSetting() {
			saveVsphereSettings();
		}

		@Override
		protected void onSaveQueueEmpty() {
			if (!isD2DSaved) {
				saveD2DConfiguration();
			} else {
				super.onSaveQueueEmpty();
			}
		}
		
		private void saveVsphereSettings() {
			saveQueue.clear();
			for (SettingsContentEntry contentEntry : this.settingsContentList)
			{
				if(contentEntry.getId() == vsphereSettingID) {
					ISettingsContent vcmSettingsContent = contentEntry.getContentObject();
					saveQueue.add(vcmSettingsContent);
					break;
				}
				
			}
			
			saveItem();
		}


		private BackupSettingsContent getBackupSettingContent() {
			BackupSettingsContent backupSettingsContent = null;
			for (SettingsContentEntry contentEntry : this.settingsContentList) {
				if (contentEntry.getId() == vsphereSettingID) {
					backupSettingsContent = (BackupSettingsContent) contentEntry.getContentObject();
					break;
				}
			}
			return backupSettingsContent;
		}

		@Override
		public void loadSetting() {
			settingPresenter.loadSetting();
		}

		private void saveD2DConfiguration() {
			isD2DSaved = true;
			settingPresenter.saveSetting(getBackupSettingContent());
		}
}
