/**
 * 
 */
package com.ca.arcflash.ui.client.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.coldstandby.VCMMessages;
import com.ca.arcflash.ui.client.coldstandby.edge.setting.VCMSettingsContent;
import com.ca.arcflash.ui.client.common.d2d.presenter.SettingPresenter;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.core.FastMap;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.data.ModelKeyProvider;
import com.extjs.gxt.ui.client.data.ModelStringProvider;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionEvent;
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

/**
 * @author lijwe02
 * 
 */
public class VCMCommonSettingTree extends BaseCommonSettingTab {
	private static Map<String, ModelData> leftRightmap = new FastMap<ModelData>();

	protected boolean isInit = false;
	protected Map<String, List<String>> map;

	private int leftWidth = 180;
	private TreePanel<ModelData> treePanel;
	private TreeStore<ModelData> treeStore;

	private VCMSettingsContent vcmSettingContent;
	private CommonPreferenceSettings vcmPreference;

	private LayoutContainer settingContentPanel;
	private LayoutContainer preferenceContentPanel;

	private CardPanel rightPanel;

	public VCMCommonSettingTree(SettingsGroupType settingsGroupType, boolean isForEdge, ISettingsContentHost contentHost) {
		super(settingsGroupType, isForEdge, contentHost, false);
		this.setLayout(new RowLayout());
		addSettingsContent();

		SettingPresenter.getInstance().addListener(new Listener<AppEvent>() {
			@Override
			public void handleEvent(AppEvent be) {
			}
		});

		SettingPresenter.getInstance().addValidateListener(new Listener<AppEvent>() {
			@Override
			public void handleEvent(AppEvent be) {
				switchSelectNode(SettingPresenter.currentRootSelectionIndex,
						SettingPresenter.currentChildSelectionIndex);
			}
		});
	}

	protected void init() {
		LayoutContainer panel = new LayoutContainer();
		BorderLayout bl = new BorderLayout();
		panel.setLayout(bl);

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
				// -----root tree node: 2 settings--------
				if (UIContext.Constants.virtualStandyNameTranslate().equals(name)) {
					icon = UIContext.IconBundle.d2d_backup_settings();
				} else if (UIContext.Constants.preferences().equals(name)) {
					icon = UIContext.IconBundle.d2d_preference_settings();
				}// -----sub tree of : virtual standby --------
				else if (UIContext.Constants.coldStandbySettingVirtualizationTitle().equals(name)) {
					icon = UIContext.IconBundle.tree_backup_settings_protection();
				} else if (UIContext.Constants.coldStandbySettingVMTitle().equals(name)) {
					icon = UIContext.IconBundle.tree_backup_settings_schedule();
				} else if (UIContext.Constants.coldStandbySettingStandinTitle().equals(name)) {
					icon = UIContext.IconBundle.tree_backup_settings_settings();
				}// -----sub tree of : Preference settings--------
				else if (UIContext.Constants.preferencesEmailAlertsLabel().equals(name)) {
					icon = UIContext.IconBundle.tree_pref_settings_emailalert();
				}
				if (icon == null)
					return null;
				return AbstractImagePrototype.create(icon);
			}

		});

		treePanel.getSelectionModel().addListener(Events.BeforeSelect, new Listener<SelectionEvent<ModelData>>() {

			@Override
			public void handleEvent(SelectionEvent<ModelData> be) {
				// Cancel select for the root item
				ModelData md = be.getModel();
				ModelData parentModelData = md.get("parent");
				if (parentModelData == null) {
					be.setCancelled(true);
				}
			}
		});
		treePanel.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<ModelData>() {
			@Override
			public void selectionChanged(SelectionChangedEvent<ModelData> se) {
				handleSelectChanged(se.getSelectedItem());
			}
		});

		LayoutContainer leftPanel = new LayoutContainer();
		leftPanel.setLayout(new RowLayout());
		leftPanel.addStyleName("x-small-editor");
		// leftPanel.add(storeFilter, new RowData(-1, -1));
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
			// addSubTreeNode(mdList);
		}

		settingContentPanel = new LayoutContainer();
		// contentPanel.setWidth("100%");
		// contentPanel.setHeight("100%");
		settingContentPanel.setLayout(new RowLayout());
		settingContentPanel.add(vcmSettingContent.getDeckPanel());
		rightPanel.add(settingContentPanel);

		preferenceContentPanel = new LayoutContainer();
		preferenceContentPanel.add(vcmPreference.deckPanel);
		rightPanel.add(preferenceContentPanel);

		setHostTitle(hostTitle);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ca.arcflash.ui.client.common.BaseCommonSettingTab#initSettingsContentList()
	 */
	@Override
	protected String initSettingsContentList() {
		if (!isInit) {
			init();
		}
		map = new FastMap<List<String>>();
		String hostTitle = VCMMessages.coldStandbyTaskSettings();
		vcmSettingContent = new VCMSettingsContent(this.settingsGroupType == SettingsGroupType.RemoteVCMSettings);

		String virtualConversionTab = UIContext.Constants.virtualStandyNameTranslate();
		settingsContentList.add(new SettingsContentEntry(vcmSettingID, virtualConversionTab, vcmSettingContent,
				vcmSettingID, AbstractImagePrototype.create(UIContext.IconBundle.vcm_virtualstandby_settings())));
		map.put(virtualConversionTab, vcmSettingContent.getSettingNameList());

		vcmPreference = new CommonPreferenceSettings(this.settingsGroupType);
		String prefrenceTab = UIContext.Constants.preferences();
		settingsContentList.add(new SettingsContentEntry(vcmPreferenceID, prefrenceTab, vcmPreference, vcmPreferenceID,
				AbstractImagePrototype.create(UIContext.IconBundle.vcm_preference_settings())));
		map.put(prefrenceTab, vcmPreference.getSettingNameList());

		Utils.connectionCache = new HashMap<String, String[]>();
		return hostTitle;
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		treePanel.expandAll();
		ModelData defaultSelectionParent = new BaseModelData();
		defaultSelectionParent.set("name", UIContext.Constants.virtualStandyNameTranslate());
		ModelData defaultSelection = new BaseModelData();

		defaultSelection.set("name", UIContext.Constants.coldStandbySettingVirtualizationTitle());
		defaultSelection.set("parent", defaultSelectionParent);
		treePanel.getSelectionModel().select(defaultSelection, false);

	}

	@Override
	protected void enableEdit(int settingsId, ISettingsContent content) {
		if (content instanceof VCMSettingsContent) {
			((VCMSettingsContent) content).enableEditing(isForEdge);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ca.arcflash.ui.client.common.BaseCommonSettingTab#saveSetting()
	 */
	@Override
	protected void saveSetting() {
		saveVCMSettings();
	}

	private void saveVCMSettings() {
		saveQueue.clear();
		for (SettingsContentEntry contentEntry : this.settingsContentList) {
			if (contentEntry.getId() == vcmSettingID) {
				ISettingsContent vcmSettingsContent = contentEntry.getContentObject();
				saveQueue.add(vcmSettingsContent);
				break;
			}

		}

		saveItem();
	}

	private void switchSelectNode(int root, int child) {
		ModelData selNode = leftRightmap.get(root + ":" + child);
		this.treePanel.getSelectionModel().select(false, selNode);
	}

	private void handleSelectChanged(ModelData selModelItem) {
		if (selModelItem == null) {
			return;
		}

		ModelData selItem = selModelItem;
		boolean isVcmSetting = false;

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

		if (UIContext.Constants.virtualStandyNameTranslate().equals(rootName)) {
			isVcmSetting = true;
		}

		if (isVcmSetting) {
			int tabId = -1;
			if (UIContext.Constants.coldStandbySettingVirtualizationTitle().equals(selItem.get("name"))) { // virtualization
				tabId = vcmSettingContent.STACK_VIRTULIZATION;
			} else if (UIContext.Constants.coldStandbySettingVMTitle().equals(selItem.get("name"))) {
				tabId = vcmSettingContent.STACK_VIRTULMACHINE;
			} else {
				tabId = vcmSettingContent.STACK_STANDIN;
			}
			vcmSettingContent.getDeckPanel().showWidget(tabId);
			VCMSettingsContent.setButtonSelected(tabId);
			BaseCommonSettingTab.presentTabSelectionIndex = BaseCommonSettingTab.vcmSettingID;
			rightPanel.setActiveItem(settingContentPanel);
		} else {
			vcmPreference.deckPanel.showWidget(0);
			BaseCommonSettingTab.presentTabSelectionIndex = BaseCommonSettingTab.vcmPreferenceID;
			rightPanel.setActiveItem(preferenceContentPanel);
		}
	}
}
