package com.ca.arcflash.ui.client.homepage;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.GeneralSettingsModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.ui.DisclosurePanel;

public class GeneralSettingsWindow {
	
	private GeneralSettingsModel generalSettingsModel;
	
	private PreferencesSettingsContent parentWindow;
	private LayoutContainer container;
	
	private LabelField pagelabel;
	
	//Help buttons
	private RadioGroup rgVideosOptions;
	private Radio rUseCASupprotVideos;
	private Radio rUseYouTubeVideos;
	
	// Tray notifications
	private RadioGroup rgTrayIconOptions;
	private Radio rNone;
	private Radio rAll;
	private Radio rErrorsAndWarnings;
	
	// Check boxes
//	private CheckBox cbNewsFeed;
//	private CheckBox cbSocialNetworking;
	//private CheckBox cbTrayNotifications;
	
//	private LayoutContainer lcNewsFeedContainer;
//	private LayoutContainer lcSocialNetworkingContainer;
	private LayoutContainer lcTrayNotificationsContainer;
	private LayoutContainer lcHelpContainer;
	
	private TableLayout tlGeneralPageLayout;
//	private TableLayout tlNewsFeedLayout;
//	private TableLayout tlSocialNetworkingLayout;
	private TableLayout tlTrayNotificationsLayout;
	private TableLayout tlHelpLayout;
	
	private TableData tdPageLabelSettings;
	
	//default values
	private final boolean newsFeed = true;
	private final boolean socialNetwork = true;
	private final int trayIcon = 0;
	private final int useVideos = 1;
	
	public GeneralSettingsWindow(PreferencesSettingsContent wind)
	{
		parentWindow = wind;
	}
	
	public LayoutContainer Render()
	{
		container = new LayoutContainer();
		container.setScrollMode(Scroll.NONE);
		
		tlGeneralPageLayout = new TableLayout();
		tlGeneralPageLayout.setWidth("87%");
//		tlGeneralPageLayout.setHeight("65%");
		container.setLayout(tlGeneralPageLayout);

		pagelabel = new LabelField();
		pagelabel.setValue(UIContext.Constants.generalSettingsHeader());
		pagelabel.addStyleName("restoreWizardTitle");
		
		tdPageLabelSettings = new TableData();
		tdPageLabelSettings.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		container.add(pagelabel,tdPageLabelSettings);
		
//		defineNewsFeedSectionSettings();
//		
//		defineSocialNetworkingSectionSettings();
		
		defineTrayNotificationsSectionSettings();
		
		defineHelpSettings();

		container.repaint();
		return container;
	}

	private void defineHelpSettings() {
		
		DisclosurePanel disPanel = Utils.getDisclosurePanel(UIContext.Constants.preferencesVideosSectionHeader());
		LayoutContainer disContainer = new LayoutContainer();
		
		lcHelpContainer = new LayoutContainer();
		lcHelpContainer.setScrollMode(Scroll.NONE);
		
		tlHelpLayout = defineGeneralPageTableLayout(1);
		lcHelpContainer.setLayout(tlHelpLayout);
		
/*		LabelField lblHelp = new LabelField();
		lblHelp.setText(UIContext.Constants.preferencesVideosSectionHeader());
		lblHelp.setWidth(300);
		lblHelp.addStyleName("restoreWizardSubItem");*/
		
		TableData tdTestDownload = new TableData();
		tdTestDownload.setColspan(1);
		tdTestDownload.setWidth("50%");
		tdTestDownload.setHorizontalAlign(HorizontalAlignment.LEFT);
//		lcHelpContainer.add(lblHelp,tdTestDownload);
		
		rgVideosOptions = new RadioGroup();
		rUseCASupprotVideos = new Radio();
		rUseCASupprotVideos.ensureDebugId("d037294c-d862-4a31-a816-5b17641a1def");
		rUseCASupprotVideos.setStyleName("x-form-field");
		rUseCASupprotVideos.setBoxLabel(UIContext.Messages.UseCASupportVideos(UIContext.companyName));
		rUseCASupprotVideos.setValue(false);
		rUseCASupprotVideos.setWidth(350);
		rgVideosOptions.add(rUseCASupprotVideos);

		lcHelpContainer.add(rUseCASupprotVideos);
		
		rUseYouTubeVideos = new Radio();
		rUseYouTubeVideos.ensureDebugId("af620f90-c3aa-42ee-9dfc-c8be418e48e0");
		rUseYouTubeVideos.setStyleName("x-form-field");
		rUseYouTubeVideos.setBoxLabel(UIContext.Constants.UseYouTubeVideos());
		rUseYouTubeVideos.setValue(true);
		rUseYouTubeVideos.setWidth(350);
		rgVideosOptions.add(rUseYouTubeVideos);
		lcHelpContainer.add(rUseYouTubeVideos);
		
		disContainer.add(lcHelpContainer);
		disContainer.add(new Html("<HR>"));
		disPanel.add(disContainer);
		container.add(disPanel);
		
// 		all videos will only be posted to YouTube and no longer on CA Support.	
		disPanel.setVisible(false);
	}

	private void defineTrayNotificationsSectionSettings() {
		
		DisclosurePanel disPanel = Utils.getDisclosurePanel(UIContext.Constants.preferencesTrayNotificationsSectionHeader());
		LayoutContainer disContainer = new LayoutContainer();
		
		
		lcTrayNotificationsContainer = new LayoutContainer();
		lcTrayNotificationsContainer.setScrollMode(Scroll.NONE);	
	
		tlTrayNotificationsLayout = defineGeneralPageTableLayout(1);
		lcTrayNotificationsContainer.setLayout(tlTrayNotificationsLayout);
		
		/*LabelField lblTrayNotifications = new LabelField();
		lblTrayNotifications.setText(UIContext.Constants.preferencesTrayNotificationsSectionHeader());
		lblTrayNotifications.addStyleName("restoreWizardSubItem");
		*/
		
		TableData tdTestDownload = new TableData();
		tdTestDownload.setColspan(1);
		tdTestDownload.setWidth("50%");
		tdTestDownload.setHorizontalAlign(HorizontalAlignment.LEFT);
//		lcTrayNotificationsContainer.add(lblTrayNotifications,tdTestDownload);
		
		// Tray Notifications buttons
		TableData tdTrayNotificationsCheckbox = new TableData();
		tdTrayNotificationsCheckbox = new TableData();
		tdTrayNotificationsCheckbox.setWidth("50%");
		tdTrayNotificationsCheckbox.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		rgTrayIconOptions = new RadioGroup();
		
		rAll = new Radio();
		rAll.ensureDebugId("6cf11f84-87d8-47b6-8b5b-ed45ec083bce");
		rAll.setStyleName("x-form-field");
		rAll.setBoxLabel(UIContext.Constants.preferencesTrayNotificationAll());
		rAll.setValue(true);
		rAll.setWidth("50%");
		rgTrayIconOptions.add(rAll);
		lcTrayNotificationsContainer.add(rAll);
		
		rErrorsAndWarnings = new Radio();
		rErrorsAndWarnings.ensureDebugId("4af2ee75-5027-4a31-a447-47da6032f700");
		rErrorsAndWarnings.setStyleName("x-form-field");
		rErrorsAndWarnings.setBoxLabel(UIContext.Constants.preferencesTrayNotificationErrorsAndWarnings());
		rErrorsAndWarnings.setValue(false);
		rErrorsAndWarnings.setWidth("50%");
		rgTrayIconOptions.add(rErrorsAndWarnings);
		lcTrayNotificationsContainer.add(rErrorsAndWarnings);
		
		rNone = new Radio();
		rNone.ensureDebugId("b98c0bf6-c6e5-47b8-a324-bbc8a80686f2");
		rNone.setStyleName("x-form-field");
		rNone.setBoxLabel(UIContext.Constants.preferencesTrayNotificationNone());
		rNone.setValue(false);
		rNone.setWidth("50%");
		rgTrayIconOptions.add(rNone);
		lcTrayNotificationsContainer.add(rNone);
		
		disContainer.add(lcTrayNotificationsContainer);
		disContainer.add(new Html("<HR>"));
		disPanel.add(disContainer);
		container.add(disPanel);
	}

//	private void defineSocialNetworkingSectionSettings() {
//		
//		DisclosurePanel disPanel = Utils.getDisclosurePanel(UIContext.Constants.preferencesSocialNetworkingSectionHeader());
//		LayoutContainer disContainer = new LayoutContainer();
//		
//		
//		lcSocialNetworkingContainer = new LayoutContainer();
//		lcSocialNetworkingContainer.setScrollMode(Scroll.NONE);	
//	
//		tlSocialNetworkingLayout = defineGeneralPageTableLayout(1);
//		lcSocialNetworkingContainer.setLayout(tlSocialNetworkingLayout);
//		
//		/*LabelField lblSocialNetworking = new LabelField();
//		lblSocialNetworking.setText(UIContext.Constants.preferencesSocialNetworkingSectionHeader());
//		lblSocialNetworking.addStyleName("restoreWizardSubItem");*/
//		
//		TableData tdTestDownload = new TableData();
//		tdTestDownload.setColspan(1);
//		tdTestDownload.setWidth("50%");
//		tdTestDownload.setHorizontalAlign(HorizontalAlignment.LEFT);
//		//lcSocialNetworkingContainer.add(lblSocialNetworking,tdTestDownload);
//		
//		// SocialNetworking checkbox
//		TableData tdSocialNetworkingCheckbox = new TableData();
//		tdSocialNetworkingCheckbox = new TableData();
//		tdSocialNetworkingCheckbox.setWidth("50%");
//		tdSocialNetworkingCheckbox.setHorizontalAlign(HorizontalAlignment.LEFT);
//		
//		cbSocialNetworking = new CheckBox();
//		cbSocialNetworking.ensureDebugId("443b892e-9c4e-4818-951d-d0a52e4a2966");
//		cbSocialNetworking.setBoxLabel(UIContext.Constants.SocialNetworkingDesc());
//		cbSocialNetworking.setVisible(true);
//		cbSocialNetworking.setValue(true);
//		lcSocialNetworkingContainer.add(cbSocialNetworking,tdSocialNetworkingCheckbox);
//		
//		disContainer.add(lcSocialNetworkingContainer);
//		disContainer.add(new Html("<HR>"));
//		disPanel.add(disContainer);
//		container.add(disPanel);
//		
//	}
//
//	private void defineNewsFeedSectionSettings() {
//		
//		DisclosurePanel disPanel = Utils.getDisclosurePanel(UIContext.Constants.preferencesNewsFeedSectionHeader());
//		LayoutContainer disContainer = new LayoutContainer();
//		
//		lcNewsFeedContainer = new LayoutContainer();
//		lcNewsFeedContainer.setScrollMode(Scroll.NONE);	
//	
//		tlNewsFeedLayout = defineGeneralPageTableLayout(1);
//		lcNewsFeedContainer.setLayout(tlNewsFeedLayout);
//		
//		/*LabelField lblNewsFeed = new LabelField();
//		lblNewsFeed.setText(UIContext.Constants.preferencesNewsFeedSectionHeader());
//		lblNewsFeed.addStyleName("restoreWizardSubItem");*/
//		
//		TableData tdTestDownload = new TableData();
//		tdTestDownload.setColspan(1);
//		tdTestDownload.setWidth("50%");
//		tdTestDownload.setHorizontalAlign(HorizontalAlignment.LEFT);
////		lcNewsFeedContainer.add(lblNewsFeed,tdTestDownload);
//		
//		// NewsFeed checkbox
//		TableData tdNewsFeedCheckbox = new TableData();
//		tdNewsFeedCheckbox = new TableData();
//		tdNewsFeedCheckbox.setWidth("50%");
//		tdNewsFeedCheckbox.setHorizontalAlign(HorizontalAlignment.LEFT);
//		
//		cbNewsFeed = new CheckBox();
//		cbNewsFeed.ensureDebugId("d237357b-6a1f-4eba-9133-394dec74f691");
//		cbNewsFeed.setBoxLabel(UIContext.Constants.newsFeedDesc());
//		cbNewsFeed.setVisible(true);
//		cbNewsFeed.setValue(true);
//		lcNewsFeedContainer.add(cbNewsFeed,tdNewsFeedCheckbox);
//		
//		disContainer.add(lcNewsFeedContainer);
//		disContainer.add(new Html("<HR>"));
//		disPanel.add(disContainer);
//		container.add(disPanel);
//		
//	}

	private TableLayout defineGeneralPageTableLayout(int in_iColumns)
	{
		TableLayout tlGeneralPageSectionLayout = new TableLayout();
		tlGeneralPageSectionLayout.setColumns(in_iColumns);
		tlGeneralPageSectionLayout.setWidth("97%");
		tlGeneralPageSectionLayout.setCellPadding(2);
		tlGeneralPageSectionLayout.setCellSpacing(2);
		return tlGeneralPageSectionLayout;
	}
	
	public void RefreshData(GeneralSettingsModel in_generalSettingsModel) 
	{	
		generalSettingsModel = in_generalSettingsModel;	
		if(generalSettingsModel == null)
		{
			generalSettingsModel = new GeneralSettingsModel();
		}
		
//		cbNewsFeed.setValue(generalSettingsModel.getNewsFeed() != null ? generalSettingsModel
//						.getNewsFeed() : newsFeed);
//		cbSocialNetworking.setValue(generalSettingsModel.getSocialNetworking() != null ? generalSettingsModel
//						.getSocialNetworking() : socialNetwork);
		int trayIconNot = generalSettingsModel.getTrayNotificationType() == null ? trayIcon
				: generalSettingsModel.getTrayNotificationType();
		switch (trayIconNot) {
		case 0:// All
			rAll.setValue(true);
			rErrorsAndWarnings.setValue(false);
			rNone.setValue(false);
			break;
		case 1:// Errors and warnings
			rAll.setValue(false);
			rErrorsAndWarnings.setValue(true);
			rNone.setValue(false);
			break;
		case 2: // None
			rAll.setValue(false);
			rErrorsAndWarnings.setValue(false);
			rNone.setValue(true);
			break;
		}

		int video = generalSettingsModel.getUseVideos() == null ? useVideos : generalSettingsModel.getUseVideos();

		switch (video) {
		case 0:// Local help
			rUseYouTubeVideos.setValue(false);
			rUseCASupprotVideos.setValue(true);
			break;
		case 1:// online help
			rUseYouTubeVideos.setValue(true);
			rUseCASupprotVideos.setValue(false);
			break;
		}
		return;
	}
	
	public void save()
	{
//		Boolean bNewsFeed = cbNewsFeed.getValue();
//		
//		generalSettingsModel.setNewsFeed(bNewsFeed);
//		
//		Boolean bSocialNW = cbSocialNetworking.getValue();
//		generalSettingsModel.setSocialNetworking(bSocialNW);
		
		int iNotificationType = -1;
		
		if(rNone.getValue())
		{
			iNotificationType = 2; 
		}
		else if(rErrorsAndWarnings.getValue())
		{
			iNotificationType = 1;
		}
		else if(rAll.getValue())
		{
			iNotificationType = 0;
		}
		
		generalSettingsModel.setTrayNotificationType(iNotificationType);
		generalSettingsModel.setUseVideos(rUseYouTubeVideos.getValue() == true ? 1 : 0);

		parentWindow.model.setGeneralSettings(generalSettingsModel);
	}

	public boolean Validate() {
		return true;
	}
	
	public void setEditable(boolean isEditable){
		rUseCASupprotVideos.setEnabled(isEditable);
		rUseYouTubeVideos.setEnabled(isEditable);
		
		rNone.setEnabled(isEditable);
		rAll.setEnabled(isEditable);
		rErrorsAndWarnings.setEnabled(isEditable);
		
//		cbNewsFeed.setEnabled(isEditable);
//		cbSocialNetworking.setEnabled(isEditable);
	}
	
}