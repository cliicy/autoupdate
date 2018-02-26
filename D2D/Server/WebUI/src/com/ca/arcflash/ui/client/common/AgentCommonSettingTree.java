package com.ca.arcflash.ui.client.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.ArchiveSettingsContent;
import com.ca.arcflash.ui.client.backup.BackupSettingsContent;
import com.ca.arcflash.ui.client.backup.Settings4Backup;
import com.ca.arcflash.ui.client.common.d2d.presenter.SettingPresenter;
import com.ca.arcflash.ui.client.export.ScheduledExportSettingsContent;
import com.ca.arcflash.ui.client.homepage.PreferencesSettingsContent;
import com.ca.arcflash.ui.client.model.CustomizationModel;
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

public class AgentCommonSettingTree extends BaseCommonSettingTab {
	// private boolean d2dUsingEdgePolicy = false;
	private boolean isD2DSaved = false;
	private TreePanel<ModelData> treePanel;
	private TreeStore<ModelData> treeStore;
	// private TreeLoader<ModelData> treeLoader;
//	private StoreFilterField<ModelData> storeFilter;
	private boolean isInit = false;

	private CardPanel rightPanel;

	private LayoutContainer d2dSettingContentPanel;
	private LayoutContainer preferenceContentPanel;
	private LayoutContainer exportRecPointContentPanel;
	private LayoutContainer archiveSettingContentPanel;
	private LayoutContainer fileArchiveSettingContentPanel;

	// backup Data
	private static ModelData backupSetting = new BaseModelData();
	private static ModelData backupScheduleSummary = new BaseModelData();
//	private static ModelData backupScheduleRepeat = new BaseModelData();
//	private static ModelData backupScheduleDaily = new BaseModelData();

	private static ModelData backupProtection = new BaseModelData();
	private static ModelData backupAdvance = new BaseModelData();
	private static ModelData backupPrePost = new BaseModelData();

	// file copy Data
	private static ModelData fileCopySetting = new BaseModelData();
	private static ModelData fileCopySource = new BaseModelData();
	private static ModelData fileCopySchedule = new BaseModelData();
	private static ModelData fileCopyDesination = new BaseModelData();
	
	// file Archive Data
	private static ModelData fileArchiveSetting = new BaseModelData();
	private static ModelData fileArchiveSource = new BaseModelData();
	private static ModelData fileArchiveSchedule = new BaseModelData();
	private static ModelData fileArchiveDesination = new BaseModelData();

	// CRP Data
	private static ModelData copyRecoveryPointSetting = new BaseModelData();
	private static ModelData copyRecoveryPointCopy = new BaseModelData();

	// preference
	private static ModelData preferenceSetting = new BaseModelData();
	private static ModelData preferenceUpdate = new BaseModelData();
	private static ModelData preferenceGeneral = new BaseModelData();
	private static ModelData preferenceEmailAlert = new BaseModelData();

	private static Map<String, ModelData> leftRightmap = new FastMap<ModelData>();

	private static String[] rootText = new String[] { UIContext.Constants.backupSettingsWindow(),
			UIContext.Constants.preferences(), UIContext.Constants.homepageTasksArchiveSettingLabel(),UIContext.Constants.homepageTasksFileArchiveSettingLabel(),
			UIContext.Constants.scheduledExportSettings() };
	private static ModelData[] rootData = { backupSetting, preferenceSetting, fileCopySetting, fileArchiveSetting,
			copyRecoveryPointSetting, };

	// backup tree
//	private static String[] advScheduleChildren = new String[] { UIContext.Constants.scheduleMenuRepeat(),
//			UIContext.Constants.scheduleMenuDailyWeeklyMonthly() };
	
	private static String[] backupChildrenText = new String[] { UIContext.Constants.backupSettingsDestination(),
			UIContext.Constants.backupSettingsSchedule(), UIContext.Constants.backupSettingsSchedule(),
			UIContext.Constants.backupSettingsSettings(), UIContext.Constants.backupSettingsPrePost() };
	
	private static ModelData[] backupChildrenData = { backupProtection, backupScheduleSummary, backupScheduleSummary,
			backupAdvance, backupPrePost };
			
	private static int[] backupChildrenIndex = { Settings4Backup.STACK_DESTINATION,
		Settings4Backup.STACK_SCHEDULE_Adv, Settings4Backup.STACK_SCHEDULE_SIMPLE,
		Settings4Backup.STACK_SETTINGS, Settings4Backup.STACK_PrePost };

	// fileCopy tree
	private static String[] fileCopyChildrenText = new String[] { UIContext.Constants.ArchiveSource(),
			UIContext.Constants.ArchiveDestination(), UIContext.Constants.backupSettingsSchedule() };
	private static String[] fileArchiveChildrenText = new String[] { UIContext.Constants.ArchiveSource(),
		UIContext.Constants.ArchiveDestination(), UIContext.Constants.backupSettingsSchedule() };
	private static ModelData[] fileCopyChildrenData = { fileCopySource, fileCopyDesination, fileCopySchedule };
	private static ModelData[] fileArchiveChildrenData = { fileArchiveSource, fileArchiveDesination, fileArchiveSchedule };
	private static int[] fileCopyChildrenIndex = { ArchiveSettingsContent.STACK_ARCHIVE_SOURCE,
			ArchiveSettingsContent.STACK_ARCHIVE_DESTINATION, ArchiveSettingsContent.STACK_ARCHIVE_SCHEDULE };
	
	private static int[] fileArchiveChildrenIndex = { ArchiveSettingsContent.STACK_ARCHIVE_SOURCE,
		ArchiveSettingsContent.STACK_ARCHIVE_DESTINATION, ArchiveSettingsContent.STACK_ARCHIVE_SCHEDULE };

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
//		backupScheduleRepeat.set("name", advScheduleChildren[0]);
//		backupScheduleRepeat.set("parent", backupScheduleSummary);
//		backupScheduleDaily.set("name", advScheduleChildren[1]);
//		backupScheduleDaily.set("parent", backupScheduleSummary);
//
//		leftRightmap.put(BaseCommonSettingTab.d2dBackupSettingID + ":" + BackupSettingsContent.STACK_PERIODICALLY,
//				backupScheduleDaily);
//		leftRightmap.put(BaseCommonSettingTab.d2dBackupSettingID + ":" + BackupSettingsContent.STACK_REPEAT,
//				backupScheduleRepeat);

		// preference
		i = 0;
		for (ModelData md : preferenceChildrenData) {
			md.set("name", preferenceChildrenText[i]);
			md.set("parent", preferenceSetting);
			leftRightmap.put(BaseCommonSettingTab.d2dPreferenceSettingID + ":" + preferenceChildrenIndex[i], md);
			i++;
		}

		// file copy
		i = 0;
		for (ModelData md : fileCopyChildrenData) {
			md.set("name", fileCopyChildrenText[i]);
			md.set("parent", fileCopySetting);
			leftRightmap.put(BaseCommonSettingTab.archiveSettingID + ":" + fileCopyChildrenIndex[i], md);
			i++;
		}
		
		// file Archive
		i = 0;
		for (ModelData md : fileArchiveChildrenData) {
			md.set("name", fileArchiveChildrenText[i]);
			md.set("parent", fileArchiveSetting);
			leftRightmap.put(BaseCommonSettingTab.fileArchiveSettingID + ":" + fileArchiveChildrenIndex[i], md);
			i++;
		}

		// CRP
		copyRecoveryPointCopy.set("name", UIContext.Constants.CopySettings());
		copyRecoveryPointCopy.set("parent", copyRecoveryPointSetting);
		leftRightmap.put(BaseCommonSettingTab.scheduledExportSettingsID + ":"
				+ ScheduledExportSettingsContent.STACK_SCHEDULEDEXPORT, copyRecoveryPointCopy);

	}

	public AgentCommonSettingTree(SettingsGroupType settingsGroupType, boolean isForEdge,
			ISettingsContentHost contentHost) {
		super(settingsGroupType, isForEdge, contentHost, false);
		this.setLayout(new RowLayout());
		addSettingsContent();

		SettingPresenter.getInstance().addListener(new Listener<AppEvent>() {
			@Override
			public void handleEvent(AppEvent be) {
				if (be.<Integer> getData("format") == 0) {
//					treePanel.getSelectionModel().setLocked(true);
//					treeStore.remove(backupScheduleSummary, backupScheduleRepeat);
//					treeStore.remove(backupScheduleSummary, backupScheduleDaily);
					SettingPresenter.getInstance().setAdvSchedule(false);
//					treePanel.getSelectionModel().setLocked(false);
				} else {
//					treeStore.insert(backupScheduleSummary, backupScheduleRepeat, 0, false);
//					treeStore.insert(backupScheduleSummary, backupScheduleDaily, 1, false);
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

	private void init() {
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
				} else if (UIContext.Constants.homepageTasksFileArchiveSettingLabel().equals(name)) {
					icon = UIContext.IconBundle.d2d_filecopy_settings();
				}else if (UIContext.Constants.scheduledExportSettings().equals(name)) {
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
				}// -----sub tree of : file copy settings--------
				else if (UIContext.Constants.ArchiveSource().equals(name)) {
					icon = UIContext.IconBundle.tree_filecopy_settings_source();
				} else if (UIContext.Constants.ArchiveDestination().equals(name)) {
					icon = UIContext.IconBundle.tree_filecopy_settings_dest();
				} else if (UIContext.Constants.backupSettingsSchedule().equals(name)) {
					icon = UIContext.IconBundle.tree_filecopy_settings_schedule();
				}// -----sub tree of : copy recovery point settings--------
				else if (UIContext.Constants.CopySettings().equals(name)) {
					icon = UIContext.IconBundle.tree_scheduledexport_settings_protection();
				}// -----sub tree of : Preference settings--------
				else if (UIContext.Constants.preferencesGeneralLabel().equals(name)) {
					icon = UIContext.IconBundle.tree_pref_settings_general();
				} else if (UIContext.Constants.preferencesEmailAlertsLabel().equals(name)) {
					icon = UIContext.IconBundle.tree_pref_settings_emailalert();
				} else if (UIContext.Constants.preferencesUpdatesLabel().equals(name)) {
					icon = UIContext.IconBundle.tree_pref_settings_update();
				}
				if(icon != null){
					return AbstractImagePrototype.create(icon);
				}else {
					return null;
				}
			}

		});

		treePanel.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
			@Override
			public void selectionChanged(SelectionChangedEvent<ModelData> se) {
				handleSelectChanged(se.getSelectedItem());
			}
		});

//		storeFilter = new StoreFilterField<ModelData>() {
//			@Override
//			protected boolean doSelect(Store<ModelData> store, ModelData parent, ModelData record, String property,
//					String filter) {
//				String name = record.get("name");
//				name = name.toLowerCase();
//				String key = record.get("key");
//				if (filter == null) {
//					return true;
//				}
//
//				filter = filter.trim().toLowerCase();
//
//				if (key != null && key.toLowerCase().indexOf(filter) >= 0) {
//					return true;
//				}
//				return false;
//			}
//		};
//		storeFilter.setEmptyText(UIContext.Constants.typeFilterText());
//
//		storeFilter.bind(treeStore);

		// treePanel.getStyle().setLeafIcon(ICONS.Leaf());
		LayoutContainer leftPanel = new LayoutContainer();
		leftPanel.setLayout(new RowLayout());
		leftPanel.addStyleName("x-small-editor");
		// leftPanel.setSpacing(8);
		// leftPanel.add(new
		// Html("<span class=text>Enter a search string such as 'abc'</span>"));
		// leftPanel.add(storeFilter, new RowData(1,-1));
//		leftPanel.add(storeFilter, new RowData(-1, -1));
		leftPanel.add(treePanel, new RowData(1, 1, new Margins(0, 0, 0, 0)));

		rightPanel = new CardPanel();
		rightPanel.setScrollMode(Scroll.AUTO);
		rightPanel.setDeferredRender(false);
		this.setScrollMode(Scroll.AUTO);

		// LayoutContainer rightContainer = new LayoutContainer();
		// rightContainer.setScrollMode(Scroll.AUTOY);
		// CardLayout cardLayout = new CardLayout();
		// rightContainer.setLayout(cardLayout);
		// rightContainer.add(rightPanel);

		BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, leftWidth);
		westData.setSplit(true);
		westData.setCollapsible(true);
		// westData.setMargins(new Margins(5));

		BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
		// centerData.setMargins(new Margins(5, 0, 5, 0));

		// panel.add(treePanel,new RowData(leftWidth, 1));
		// panel.add(rightPanel,new RowData(1, 1));

		panel.add(leftPanel, westData);
		panel.add(rightPanel, centerData);

		add(panel, new RowData(1, 1));

		isInit = true;
	}

	private void handleSelectChanged(ModelData selModelItem) {
		//
		// rightContainer.add(d2dSettingsContent.advancedContainer);
		// rightContainer.add(d2dSettingsContent.destinationContainer);
		// rightContainer.add(d2dSettingsContent.scheduleContainer);
		// rightContainer.add(d2dSettingsContent.settingsContainer);
		// rightContainer.layout(true);
		//
		// if(UIContext.Constants.backupSettingsAdvanced().equals(se.getSelectedItem().get("name"))){
		// // pre/post
		// cardLayout.setActiveItem(d2dSettingsContent.advancedContainer);
		// } else
		// if(UIContext.Constants.backupSettingsDestination().equals(se.getSelectedItem().get("name"))){
		// cardLayout.setActiveItem(d2dSettingsContent.destinationContainer);
		// } else
		// if(UIContext.Constants.backupSettingsSchedule().equals(se.getSelectedItem().get("name"))){
		// cardLayout.setActiveItem(d2dSettingsContent.scheduleContainer);
		// } else
		// if(UIContext.Constants.backupSettingsSettings().equals(se.getSelectedItem().get("name"))){
		// // advanced
		// cardLayout.setActiveItem(d2dSettingsContent.settingsContainer);
		// }

		// rightContainer.add(d2dSettingsContent.deckPanel);
		// rightContainer.layout(true);

		if (selModelItem == null)
			return;

		ModelData selItem = selModelItem;
		boolean isD2DSetting = false;
		boolean isPreference = false;
		boolean isFileCopy = false;
		boolean isFileArchive = false;
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
		} else if (UIContext.Constants.scheduledExportSettings().equals(rootName)) {
			isExportRecPoint = true;
		} else if (UIContext.Constants.preferences().equals(rootName)) {
			isPreference = true;
		} else if (UIContext.Constants.homepageTasksArchiveSettingLabel().equals(rootName)) {
			isFileCopy = true;
		}else if(UIContext.Constants.homepageTasksFileArchiveSettingLabel().equals(rootName)){
			isFileArchive = true;
		}
		
		int selectIndex = 1;

		if (isD2DSetting) {
			if (UIContext.Constants.backupSettingsPrePost().equals(selItem.get("name"))) { // pre/post
				d2dSettingsContent.deckPanel.showWidget(d2dSettingsContent.STACK_PrePost);
				selectIndex = 3;
			} else if (UIContext.Constants.backupSettingsDestination().equals(selItem.get("name"))) {
				d2dSettingsContent.deckPanel.showWidget(d2dSettingsContent.STACK_DESTINATION);
				selectIndex = 1;
			} else if (UIContext.Constants.backupSettingsSchedule().equals(selItem.get("name"))) {
				if (SettingPresenter.getInstance().isAdvSchedule()) {
					//d2dSettingsContent.deckPanel.showWidget(d2dSettingsContent.STACK_STACK_SCHEDULE_SUMMARRY);
					d2dSettingsContent.deckPanel.showWidget(d2dSettingsContent.STACK_SCHEDULE_Adv);
					selectIndex = 2;
				} else {
					d2dSettingsContent.deckPanel.showWidget(d2dSettingsContent.STACK_SCHEDULE_SIMPLE);
					selectIndex = 7;
				}				
			} else if (UIContext.Constants.backupSettingsSettings().equals(selItem.get("name"))) { // advanced
				d2dSettingsContent.deckPanel.showWidget(d2dSettingsContent.STACK_SETTINGS);				
				selectIndex = 4;
			}

//			boolean isSchedule = selItem.get("parent") != null
//					&& UIContext.Constants.backupSettingsSchedule().equals(
//							selItem.<ModelData> get("parent").get("name")) ? true : false;
//			if (isSchedule) {
//				if (advScheduleChildren[0].equals(selItem.get("name"))) {
//					d2dSettingsContent.deckPanel.showWidget(d2dSettingsContent.STACK_REPEAT);
//					selectIndex = 5;
//				} else if (advScheduleChildren[1].equals(selItem.get("name"))) {
//					d2dSettingsContent.deckPanel.showWidget(d2dSettingsContent.STACK_PERIODICALLY);
//					selectIndex = 6;
//				}
//			}
		}

		if (isPreference) {
			if (UIContext.Constants.preferencesEmailAlertsLabel().equals(selItem.get("name"))) {
				preferenceconContent.deckPanel.showWidget(preferenceconContent.STACK_EMAILALERTS);
				selectIndex = 2;
			} else if (UIContext.Constants.preferencesGeneralLabel().equals(selItem.get("name"))) {
				preferenceconContent.deckPanel.showWidget(preferenceconContent.STACK_GENERAL);
				selectIndex = 1;
			} else if (UIContext.Constants.preferencesUpdatesLabel().equals(selItem.get("name"))) {
				preferenceconContent.deckPanel.showWidget(preferenceconContent.STACK_SELFUPDATE);
				selectIndex = 3;
			}
		}

		if (isExportRecPoint) {
			if (UIContext.Constants.CopySettings().equals(selItem.get("name"))) {
				scheduledExportSettingsContent.deckPanel
						.showWidget(scheduledExportSettingsContent.STACK_SCHEDULEDEXPORT);
			}
		}

		if (isFileCopy) {
			if (UIContext.Constants.ArchiveSource().equals(selItem.get("name"))) {
				archiveSettingContent.archiveDeckPanel.showWidget(archiveSettingContent.STACK_ARCHIVE_SOURCE);
				selectIndex = 1;
			} else if (UIContext.Constants.backupSettingsSchedule().equals(selItem.get("name"))) {
				archiveSettingContent.archiveDeckPanel.showWidget(archiveSettingContent.STACK_ARCHIVE_SCHEDULE);
				selectIndex = 3;
			} else if (UIContext.Constants.ArchiveDestination().equals(selItem.get("name"))) {
				archiveSettingContent.archiveDeckPanel.showWidget(archiveSettingContent.STACK_ARCHIVE_DESTINATION);
				selectIndex = 2;
			}
		}
		
		if (isFileArchive) {
			if (UIContext.Constants.ArchiveSource().equals(selItem.get("name"))) {
				fileArchiveSettingContent.archiveDeckPanel.showWidget(fileArchiveSettingContent.STACK_ARCHIVE_SOURCE);
				selectIndex = 1;
			} else if (UIContext.Constants.backupSettingsSchedule().equals(selItem.get("name"))) {
				fileArchiveSettingContent.archiveDeckPanel.showWidget(fileArchiveSettingContent.STACK_ARCHIVE_SCHEDULE);
				selectIndex = 3;
			} else if (UIContext.Constants.ArchiveDestination().equals(selItem.get("name"))) {
				fileArchiveSettingContent.archiveDeckPanel.showWidget(fileArchiveSettingContent.STACK_ARCHIVE_DESTINATION);
				selectIndex = 2;
			}
		}

		if (isD2DSetting) {
			// rightPanel.showWidget(0);
			rightPanel.setActiveItem(d2dSettingContentPanel);
			BaseCommonSettingTab.presentTabSelectionIndex = BaseCommonSettingTab.d2dBackupSettingID;
			BackupSettingsContent.setButtonSelected(selectIndex);
		} else if (isPreference) {
			// rightPanel.showWidget(1);
			BaseCommonSettingTab.presentTabSelectionIndex = BaseCommonSettingTab.d2dPreferenceSettingID;
			rightPanel.setActiveItem(preferenceContentPanel);
			PreferencesSettingsContent.setButtonSelected(selectIndex);
		} else if (isExportRecPoint) {
			// rightPanel.showWidget(2);
			rightPanel.setActiveItem(exportRecPointContentPanel);
			BaseCommonSettingTab.presentTabSelectionIndex = BaseCommonSettingTab.scheduledExportSettingsID;
		} else if(isFileCopy){
			// rightPanel.showWidget(3);
			rightPanel.setActiveItem(archiveSettingContentPanel);
			BaseCommonSettingTab.presentTabSelectionIndex = BaseCommonSettingTab.archiveSettingID;
			ArchiveSettingsContent.setButtonSelected(selectIndex);
		}else if(isFileArchive){
			rightPanel.setActiveItem(fileArchiveSettingContentPanel);
			BaseCommonSettingTab.presentTabSelectionIndex = BaseCommonSettingTab.fileArchiveSettingID;
			ArchiveSettingsContent.setButtonSelected(selectIndex);
		}
		// d2dSettingsContent.deckPanel.showWidget(0);
		// d2dSettingsContent.settingsButton.fireEvent(new
		// GwtEvent<ClickHandler>() {
		// @Override
		// public com.google.gwt.event.shared.GwtEvent.Type<ClickHandler>
		// getAssociatedType() {
		// return ClickEvent.getType();
		// }
		// @Override
		// protected void dispatch(ClickHandler handler) {
		// handler.onClick(null);
		// }
		// });

	}

	private void switchSelectNode(int root, int child) {

		// boolean isD2DSetting = false;
		// boolean isPreference = false;
		// boolean isFileCopy = false;
		// boolean isExportRecPoint = false;
		// switch (root) {
		// case BaseCommonSettingTab.d2dBackupSettingID:
		// isD2DSetting = true;
		// break;
		// case BaseCommonSettingTab.d2dPreferenceSettingID:
		// isPreference = true;
		// break;
		// case BaseCommonSettingTab.archiveSettingID:
		// isFileCopy = true;
		// break;
		// case BaseCommonSettingTab.scheduledExportSettingsID:
		// isExportRecPoint = true;
		// break;
		// }
		//
		// if(isD2DSetting){
		// if(child == d2dSettingsContent.STACK_ADVANCED){
		//
		//
		// }else if(child == d2dSettingsContent.STACK_DESTINATION){
		//
		//
		// }else if(child == d2dSettingsContent.STACK_STACK_SCHEDULE_SUMMARRY){
		//
		//
		// }else if(child == d2dSettingsContent.STACK_SCHEDULE_SIMPLE){
		//
		//
		// }else if(child == d2dSettingsContent.STACK_REPEAT){
		//
		//
		// }else if(child == d2dSettingsContent.STACK_PERIODICALLY){
		//
		//
		// }
		// }
		//
		// if(isPreference){
		// if(child == preferenceconContent.STACK_EMAILALERTS){
		//
		//
		// }else if(child == preferenceconContent.STACK_GENERAL){
		//
		//
		// }else if(child == preferenceconContent.STACK_SELFUPDATE){
		//
		//
		// }
		// }
		//
		// if(isExportRecPoint){
		// if(child == scheduledExportSettingsContent.STACK_SCHEDULEDEXPORT){
		//
		// }
		// }
		//
		// if(isFileCopy){
		// if(child == archiveSettingContent.STACK_ARCHIVE_SOURCE){
		//
		//
		// }else if(child == archiveSettingContent.STACK_ARCHIVE_SCHEDULE){
		//
		//
		// }else if(child == archiveSettingContent.STACK_ARCHIVE_DESTINATION){
		//
		//
		// }
		// }

		// BaseCommonSettingTab.presentTabSelectionIndex = root;

		ModelData selNode = leftRightmap.get(root+":"+child);
//		handleSelectChanged(selNode);
		
		//this.treePanel.getSelectionModel().handleEvent(new TreePanelEvent<ModelData>(this.treePanel, selNode));
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
		//	addSubTreeNode(mdList);
		}

		d2dSettingContentPanel = new LayoutContainer();
		// contentPanel.setWidth("100%");
		// contentPanel.setHeight("100%");
		d2dSettingContentPanel.setLayout(new RowLayout());
		d2dSettingContentPanel.add(d2dSettingsContent.deckPanel);
		rightPanel.add(d2dSettingContentPanel);

		preferenceContentPanel = new LayoutContainer();
		preferenceContentPanel.add(preferenceconContent.deckPanel);
		rightPanel.add(preferenceContentPanel);

		exportRecPointContentPanel = new LayoutContainer();
		exportRecPointContentPanel.add(scheduledExportSettingsContent.deckPanel);
		rightPanel.add(exportRecPointContentPanel);

		archiveSettingContentPanel = new LayoutContainer();
		archiveSettingContentPanel.add(archiveSettingContent.archiveDeckPanel);
		rightPanel.add(archiveSettingContentPanel);
		
		fileArchiveSettingContentPanel = new LayoutContainer();
		fileArchiveSettingContentPanel.add(fileArchiveSettingContent.archiveDeckPanel);
		rightPanel.add(fileArchiveSettingContentPanel);

		setHostTitle(hostTitle);
	}

//	private void addSubTreeNode(List<ModelData> parentList) {
//		for (ModelData parent : parentList) {
//			addSubTreeNode(parent);
//		}
//	}

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

//	private void addSubTreeNode(ModelData parent) {
//		ModelData parentofParentNode = parent.get("parent");
//		boolean isBackupSetting = UIContext.Constants.backupSettingsWindow().equals(parentofParentNode.get("name")) ? true
//				: false;
//
//		if (isBackupSetting && UIContext.Constants.backupSettingsSchedule().equals(parent.get("name"))) {
//
//			// d2dSettingsContent.deckPanel.showWidget(d2dSettingsContent.STACK_SCHEDULE);
//
//			List<ModelData> mdList = new ArrayList<ModelData>();
//			for (String value : advScheduleChildren) {
//				ModelData md = new BaseModelData();
//				md.set("name", value);
//				md.set("parent", parent);
//				mdList.add(md);
//			}
//
//			treeStore.insert(parent, mdList, 0, true);
//		}
//	}

	private Map<String, List<String>> map;

	private Settings4Backup d2dSettingsContent;
	private ArchiveSettingsContent archiveSettingContent;
	private ArchiveSettingsContent fileArchiveSettingContent;
	private ScheduledExportSettingsContent scheduledExportSettingsContent;
	private PreferencesSettingsContent preferenceconContent;

	@Override
	protected String initSettingsContentList() {
		if (!isInit)
			init();
		map = new FastMap<List<String>>();
		String hostTitle = UIContext.Constants.homepageTasksBackupSettingLabel();
		d2dSettingsContent = new Settings4Backup();	
		d2dSettingsContent.setD2D(true);

		String d2dSettingTab = UIContext.Constants.backupSettingsWindow();
		settingsContentList.add(new SettingsContentEntry(d2dBackupSettingID, d2dSettingTab, d2dSettingsContent,
				d2dBackupSettingID,  AbstractImagePrototype.create(UIContext.IconBundle.d2d_backup_settings())));

		map.put(d2dSettingTab, d2dSettingsContent.itemsToDisplay);

		CustomizationModel customizedModel = UIContext.customizedModel;
		Boolean isFileCopyEnabled = customizedModel.get("FileCopy");
		Boolean isFileArchiveEnabled = customizedModel.get("FileArchive");
		if (isFileCopyEnabled) {
			archiveSettingContent = new ArchiveSettingsContent((BackupSettingsContent) d2dSettingsContent, false);
			String archiveSettingTab = UIContext.Constants.homepageTasksArchiveSettingLabel();
			settingsContentList.add(new SettingsContentEntry(archiveSettingID, archiveSettingTab,
					archiveSettingContent, archiveSettingID, AbstractImagePrototype.create(UIContext.IconBundle.d2d_filecopy_settings())));
			map.put(archiveSettingTab, archiveSettingContent.itemsToDisplay);
			
		}
		if(isFileArchiveEnabled){
			fileArchiveSettingContent = new ArchiveSettingsContent((BackupSettingsContent) d2dSettingsContent, true);
			String fileArchiveSettingTab = UIContext.Constants.homepageTasksFileArchiveSettingLabel();
			settingsContentList.add(new SettingsContentEntry(fileArchiveSettingID, fileArchiveSettingTab,
					fileArchiveSettingContent, fileArchiveSettingID, AbstractImagePrototype.create(UIContext.IconBundle.d2d_filecopy_settings())));
			map.put(fileArchiveSettingTab, fileArchiveSettingContent.itemsToDisplay);
			
		}

		scheduledExportSettingsContent = new ScheduledExportSettingsContent();
		String scheduledExportSettingsTab = UIContext.Constants.scheduledExportSettings();
		settingsContentList.add(new SettingsContentEntry(scheduledExportSettingsID, scheduledExportSettingsTab,
				scheduledExportSettingsContent, scheduledExportSettingsID, AbstractImagePrototype.create(UIContext.IconBundle
						.d2d_reconverypoints_settings())));

		map.put(scheduledExportSettingsTab, scheduledExportSettingsContent.itemsToDisplay);

		preferenceconContent = new PreferencesSettingsContent();
		String d2dPrefrenceTab = UIContext.Constants.preferences();
		settingsContentList.add(new SettingsContentEntry(d2dPreferenceSettingID, d2dPrefrenceTab, preferenceconContent,
				d2dPreferenceSettingID, AbstractImagePrototype.create(UIContext.IconBundle.d2d_preference_settings())));

		map.put(d2dPrefrenceTab, preferenceconContent.itemsToDisplay);

		Utils.connectionCache = new HashMap<String, String[]>();
		return hostTitle;
	}

	@Override
	protected void saveSetting() {
		isD2DSaved = false;
		saveQueue.clear();
		for (SettingsContentEntry contentEntry : this.settingsContentList) {
			ISettingsContent settingsContent = contentEntry.getContentObject();
			this.saveQueue.add(settingsContent);
		}

		this.saveItem();
	}

	@Override
	protected void onSaveQueueEmpty() {
		if (!isD2DSaved) {
			saveD2DConfiguration();
		} else {
			super.onSaveQueueEmpty();
		}
	}

	private BackupSettingsContent getBackupSettingContent() {
		BackupSettingsContent backupSettingsContent = null;
		for (SettingsContentEntry contentEntry : this.settingsContentList) {
			if (contentEntry.getId() == d2dBackupSettingID) {
				backupSettingsContent = (BackupSettingsContent) contentEntry.getContentObject();
				break;
			}
		}
		return backupSettingsContent;
	}

	@Override
	protected void enableEdit(int settingsId, ISettingsContent content) {
		CustomizationModel customizedModel = UIContext.customizedModel;
		if (d2dUsingEdgePolicy && !this.isForEdge)
			switch (settingsId) {
			case archiveSettingID:
				Boolean isFileCopyEnabled = customizedModel.get("FileCopy");

				if (isFileCopyEnabled) {
					((ArchiveSettingsContent) content).enableEditing(false);
				}
				break;
			case fileArchiveSettingID:
				Boolean isFileArchiveEnabled = customizedModel.get("FileArchive");

				if (isFileArchiveEnabled) {
					((ArchiveSettingsContent) content).enableEditing(false);
				}
				break;
			case d2dBackupSettingID:
				((BackupSettingsContent) content).enableEditing(false);
				break;
			case d2dPreferenceSettingID:
				((PreferencesSettingsContent) content).enableEditing(false);
				break;
			case scheduledExportSettingsID:
				((ScheduledExportSettingsContent) content).enableEditing(false);
				break;

			default:
				break;
			}
	}
	
	
	@Override
	protected void enableEdit(int settingsId, ISettingsContent content, boolean isEnabled) {	
		CustomizationModel customizedModel = UIContext.customizedModel;
			switch (settingsId) {
			case archiveSettingID:
				Boolean isFileCopyEnabled = customizedModel.get("FileCopy");

				if (isFileCopyEnabled) {
					((ArchiveSettingsContent) content).enableEditing(isEnabled);
				}
				break;
			case fileArchiveSettingID:
				Boolean isFileArchiveEnabled = customizedModel.get("FileArchive");

				if (isFileArchiveEnabled) {
					((ArchiveSettingsContent) content).enableEditing(isEnabled);
				}
				break;
				
			case d2dBackupSettingID:
				((BackupSettingsContent) content).enableEditing(isEnabled);
				break;
			case d2dPreferenceSettingID:
				((PreferencesSettingsContent) content).enableEditing(isEnabled);
				break;
			case scheduledExportSettingsID:
				((ScheduledExportSettingsContent) content).enableEditing(isEnabled);
				break;

			default:
				break;
			}
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
