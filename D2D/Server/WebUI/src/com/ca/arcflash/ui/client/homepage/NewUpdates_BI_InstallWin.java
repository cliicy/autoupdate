package com.ca.arcflash.ui.client.homepage;

import java.util.Date;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.FormatUtil;
import com.ca.arcflash.ui.client.common.LoadingStatusWindow;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.BIPatchInfoModel;
import com.ca.arcflash.ui.client.model.PatchInfoModel;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;



public class NewUpdates_BI_InstallWin extends Window {
	private NewUpdates_BI_InstallWin dlgNewUpdatesWindow;
	private LayoutContainer lcNewUpdatesContainer;

	private LayoutContainer UpdatesHeaderContainer;
	private LabelField lblNewUpdatesDlgHeader;
	private LabelField lblNewUpdatesMessage;
	private LabelField lblNewUpdatesRebootMessage;
	private LabelField lblNewUpdatesRestartServiceMessage;

	private Button btInstall;
	private Button btCancel;

	public final int MIN_WIDTH = 90;

	private PatchInfoModel patchInfoModel;

	private int iInstallStatus = -1;
	private boolean bPatchManagerBusy = false;

	final HomepageServiceAsync service = GWT.create(HomepageService.class);
	final CommonServiceAsync commonService = GWT.create(CommonService.class);

	public static final int ERROR_GET_PATCH_INFO_SUCCESS = 0;
	public static final int ERROR_GET_PATCH_INFO_FAIL = 1;

	public static final int INSTALL_ERROR_SUCCESS = 0;
	// public static final int INSTALL_ERROR_ACTIVE_JOBS = 3;
	public static final int INSTALL_ERROR_FAIL = 4;
	public static final int INSTALL_ERROR_MAINAPPNOTINST = -520026879;
	public static final int INSTALL_ERROR_OSTYPEFAILED = -520026878;
	public static final int INSTALL_ERROR_MAINAPP_VER_ERR = -520026877;
	public static final int INSTALL_ERROR_ALREADYINSTALLED = -520026876;
	public static final int INSTALL_ERROR_HIGHERVERSION_PATCH_ALREADYINSTALLED = -520026875;
	public static final int INSTALL_ERROR_JOB_RUNNING = -520026874;
	public static final int INSTALL_ERROR_DISKSPACE_NOT_ENOUGH = -520026873;
	public static final int INSTALL_ERROR_SELFUNCOMPRESS_FAILED = 90000;
	public static final int ERROR_NONEW_PATCHES_AVAILABLE = 5;
	public static final DateTimeFormat serverDate = DateTimeFormat
			.getFormat("MM/dd/yyyy");
	public static final DateTimeFormat dateFormat = FormatUtil
			.getShortDateFormat();

	// Patch Details
	private FieldSet fsPatchDetails;

	public static final String ICON_SMALL_WARNING_URL = "images/status_small_warning.png";
	public static final String ICON_LARGE_WARNING_URL = "images/status_mid_warning.png";
	public static final String ICON_SMALL_INFORMATION_URL = "images/LogMsg.png";
	
	//added for stauts of patches' installation
	public static final String ICON_PATCH_INSTALLED = "images/patch_installed.png";
	public static final String ICON_PATCH_WAITING2INSTALL = "images/install_exc.png";
	public static final String ICON_PATCH_NOREADYINSTALL = "images/install_nag.png";
	private AbstractImagePrototype abPatchInstalledImage;
	private AbstractImagePrototype abPatchNoReadyImage;
	private AbstractImagePrototype abPatchWaiting2InstallImage;
	private Image PatchInstalledImage;
	private Image PatchNoReadyImage;
	private Image PatchWaiting2InstallImage;
	
	private ContentPanel depImagePanel;
	private ContentPanel depInstalledPanel;
	private ClickHandler NewBIUpdatesDepInstallHandler = null;
	
	Label lblInstalldepLink0 ;
	Label lblInstalldepLink1 ;
	Label lblInstalldepLink2 ;
	//added for stauts of patches' installation
	
	private AbstractImagePrototype newUpdatesRebootWarningImage;
	private AbstractImagePrototype newUpdatesRestartServiceImage;
	private Image RebootWarningImage;
	private Image RestartServiceInfoImage;
	private CheckBox cbRebootMachine;
	private Listener<MessageBoxEvent> msgCallback;

	// private ContentPanel cpRebootWarning;
	private LayoutContainer lcRebootWarning;
	private LayoutContainer lcServiceRestartInformation;

	protected LoadingStatusWindow loadingStatus;

	public NewUpdates_BI_InstallWin(final BIPatchInfoModel in_patchInfo) {
		this.setResizable(true);
		dlgNewUpdatesWindow = this;
	
		dlgNewUpdatesWindow.setHeight(600);
		dlgNewUpdatesWindow.setWidth("600");
	
		// comment the fixed height to fix issue: 41303: Chrome - Update warning
		// truncation
		// https://cscrjts001.ca.com/ccm/resource/itemName/com.ibm.team.workitem.WorkItem/41303
		// dlgNewUpdatesWindow.setHeight("400");

		dlgNewUpdatesWindow.setHeadingHtml(UIContext.Constants
				.InstallUpdatesDialogHeader());

		TableLayout tlNewUpdatesLayout = new TableLayout();
		tlNewUpdatesLayout.setColumns(1);
		tlNewUpdatesLayout.setCellPadding(0);
		tlNewUpdatesLayout.setCellSpacing(3);


		dlgNewUpdatesWindow.setLayout(tlNewUpdatesLayout);
		dlgNewUpdatesWindow.setScrollMode(Scroll.ALWAYS);
		
		lcNewUpdatesContainer = new LayoutContainer();
		
		lcNewUpdatesContainer.setStyleName("install_Wizard");

		UpdatesHeaderContainer = new LayoutContainer();
		TableLayout headerLayout = new TableLayout();
		headerLayout.setColumns(2);
		UpdatesHeaderContainer.setLayout(headerLayout);

		newUpdatesRebootWarningImage = IconHelper.create(
				ICON_LARGE_WARNING_URL, 32, 32);
		TableData informationImage = new TableData();
		informationImage.setHorizontalAlign(HorizontalAlignment.LEFT);
		informationImage.setWidth("1%");
		informationImage.setPadding(2);
		RebootWarningImage = newUpdatesRebootWarningImage.createImage();
		UpdatesHeaderContainer.add(RebootWarningImage, informationImage);

		lblNewUpdatesDlgHeader = new LabelField(
				UIContext.Constants.InstallUpdatesDialogTitle());
		lblNewUpdatesDlgHeader.setStyleName("install_Wizard_Title");
		TableData tdNewUpdatesHeader = new TableData();
		tdNewUpdatesHeader.setHorizontalAlign(HorizontalAlignment.LEFT);
		UpdatesHeaderContainer.add(lblNewUpdatesDlgHeader, tdNewUpdatesHeader);

		lcNewUpdatesContainer.add(UpdatesHeaderContainer);

		lblNewUpdatesMessage = new LabelField();
		lblNewUpdatesMessage.setValue(UIContext.Messages
				.PatchDetailsDialogDescription(UIContext.companyName));
		lblNewUpdatesMessage.setStyleName("install_Wizard_Description");
		TableData tdNewUpdatesMessage = new TableData();
		tdNewUpdatesMessage.setHorizontalAlign(HorizontalAlignment.LEFT);
		lcNewUpdatesContainer.add(lblNewUpdatesMessage, tdNewUpdatesMessage);
		
	
		fsPatchDetails = new FieldSet();
		fsPatchDetails.setHeadingHtml(UIContext.Constants.PatchDetailsLabel());

		TableLayout tlPatchID = new TableLayout();
		tlPatchID.setCellPadding(1);
		tlPatchID.setColumns(3);//change 3 to 4 in order to add image to show the status of dependency
		
		fsPatchDetails.setLayout(tlPatchID);
	

		for (int i = 0; i < in_patchInfo.aryPatchInfoM.length; i++) {
			// Separate Patch line
			if ( i != 0 )
			{
				LabelField labeldiffpatch = new LabelField();
				labeldiffpatch.setValue("---------------------------------------------------");
				labeldiffpatch.setWidth(90);
				fsPatchDetails.add(labeldiffpatch);
				// /*
				LabelField labeldiffPatchVerCol = new LabelField();
				labeldiffPatchVerCol.setValue("---------------------------------------------------");
				labeldiffPatchVerCol.setWidth(10);
				fsPatchDetails.add(labeldiffPatchVerCol);
	
				LabelField labeldiffPatchVersionValue = new LabelField();
				labeldiffPatchVersionValue.setValue("---------------------------------------------------");
				fsPatchDetails.add(labeldiffPatchVersionValue);
			}
			// */
						
			patchInfoModel = in_patchInfo.aryPatchInfoM[i];
			// Package Id & Name
			LabelField labelPatchID = new LabelField();
			labelPatchID.setValue(UIContext.Constants.BIPatchID() + " " + i);
			labelPatchID.setWidth(120);
			fsPatchDetails.add(labelPatchID);

			LabelField labelPatchIDValue1 = new LabelField();
			labelPatchIDValue1.setValue(UIContext.Constants.PatchColon());
			labelPatchIDValue1.setWidth(10);
			fsPatchDetails.add(labelPatchIDValue1);

			LabelField labelPatchIDValue2 = new LabelField();
			labelPatchIDValue2.setValue(patchInfoModel.getPatchUpdateName());
			fsPatchDetails.add(labelPatchIDValue2);
			
			//add link to install this hotfix
			LabelField labelHotfixInstall = new LabelField();
			labelHotfixInstall.setValue("  ");
			fsPatchDetails.add(labelHotfixInstall);
			
			LabelField labelHfInstallV1 = new LabelField();
			labelHfInstallV1.setValue("  ");
			fsPatchDetails.add(labelHfInstallV1);
			
			final Label lblInstallLink = new Label();
			lblInstallLink.ensureDebugId("aebc98a2-aacc-488a-b7f7-38645be1ddf8");
			lblInstallLink.setText("Install the hotfix");
			lblInstallLink.setStyleName("install_Wizard_HotfixDetailsLink");
			InstallDepPatch(lblInstallLink,null);
			
			TableData tdInstallPatchLink = new TableData();			
			tdInstallPatchLink.setHorizontalAlign(HorizontalAlignment.LEFT);
			lblInstallLink.setVisible(false);
			fsPatchDetails.add(lblInstallLink, tdInstallPatchLink);

			// Package Dependency
			//StringUtil.isEmptyOrNull(patchInfoModel.getPackageDepy()) 
			if (!patchInfoModel.getPatchDependency().equals("Null")) {
				
				LabelField labelPatchDpy = new LabelField();
				labelPatchDpy.setValue(UIContext.Constants.BIDependency());
				labelPatchDpy.setWidth(120);
				fsPatchDetails.add(labelPatchDpy);

				LabelField labelPatchDpyValue1 = new LabelField();
				labelPatchDpyValue1.setValue(UIContext.Constants.PatchColon());
				labelPatchDpyValue1.setWidth(10);
				fsPatchDetails.add(labelPatchDpyValue1);

				LabelField labelPatchDpyValue2 = new LabelField();
				labelPatchDpyValue2.setValue(patchInfoModel.getPatchDependency());
				fsPatchDetails.add(labelPatchDpyValue2);
				
				//add the link to install dependency 
				
				final Label lblInstalldepLink = new Label();
				lblInstalldepLink.ensureDebugId("aebc98a2-aacc-488a-b7f7-38645be1ddf8");
				lblInstalldepLink.setText("Install this dependency:"+patchInfoModel.getPatchDependency());
				lblInstalldepLink.setStyleName("install_Wizard_PatchDetailsLink");
				InstallDepPatch(lblInstalldepLink,lblInstallLink);
				
				TableData tdInstallPatchDepLink = new TableData();
				tdInstallPatchDepLink.setColspan(3);
				tdInstallPatchDepLink.setHorizontalAlign(HorizontalAlignment.LEFT);
				fsPatchDetails.add(lblInstalldepLink, tdInstallPatchDepLink);
				//patchInfoModel.setInstalldepLink(lblInstalldepLink);
				//lcNewUpdatesContainer.add(fsPatchDetails);
				//add the link to install dependency				
			} else {
				lblInstallLink.setVisible(true);
			}
			
			// Published date
			LabelField labelPublishedDate = new LabelField();
			labelPublishedDate.setValue(UIContext.Constants.PatchPublishedDate());
			labelPublishedDate.setWidth(120);
			fsPatchDetails.add(labelPublishedDate);

			LabelField labelPublishedDateCo = new LabelField();
			labelPublishedDateCo.setValue(UIContext.Constants.PatchColon());
			labelPublishedDateCo.setWidth(10);
			fsPatchDetails.add(labelPublishedDateCo);

			LabelField labelPublishedDateValue = new LabelField();
			Date date = serverDate.parse(patchInfoModel.getPublishedDate());
			// Date date =serverDate.parse("08/07/2016");
			labelPublishedDateValue.setValue(dateFormat.format(date));
			fsPatchDetails.add(labelPublishedDateValue);

			// Patch description
			LabelField labelPatchDescription = new LabelField();
			labelPatchDescription.setValue(UIContext.Constants.PatchDescription());
			labelPatchDescription.setWidth(120);
			fsPatchDetails.add(labelPatchDescription);

			LabelField labelPatchDsCo = new LabelField();
			labelPatchDsCo.setValue(UIContext.Constants.PatchColon());
			labelPatchDsCo.setWidth(10);
			fsPatchDetails.add(labelPatchDsCo);

			LabelField labelPatchDescriptionValue = new LabelField();
			labelPatchDescriptionValue
					.setValue(patchInfoModel.getDescription());
			fsPatchDetails.add(labelPatchDescriptionValue);

			// Patch version
			LabelField labelPatchVersion = new LabelField();
			labelPatchVersion.setValue(UIContext.Constants.PatchVersion());
			labelPatchVersion.setWidth(120);
			fsPatchDetails.add(labelPatchVersion);

			LabelField labelPatchVerCol = new LabelField();
			labelPatchVerCol.setValue(UIContext.Constants.PatchColon());
			labelPatchVerCol.setWidth(10);
			fsPatchDetails.add(labelPatchVerCol);

			LabelField labelPatchVersionValue = new LabelField();
			labelPatchVersionValue.setValue(Integer.toString(patchInfoModel.getPatchVersionNumber()));
			fsPatchDetails.add(labelPatchVersionValue);

			// Patch reboot
			LabelField labelPatchReboot = new LabelField();
			labelPatchReboot.setValue(UIContext.Constants.PatchReboot());
			labelPatchReboot.setWidth(120);
			fsPatchDetails.add(labelPatchReboot);

			LabelField labelPatchRebootCol = new LabelField();
			labelPatchRebootCol.setValue(UIContext.Constants.PatchColon());
			labelPatchRebootCol.setWidth(10);
			fsPatchDetails.add(labelPatchRebootCol);

			LabelField labelPatchRebootValue = new LabelField();
			labelPatchRebootValue.setValue(patchInfoModel.getRebootRequired() == 1 ? UIContext.Constants.yes() : UIContext.Constants.no());
			fsPatchDetails.add(labelPatchRebootValue);

			// Patch status
			LabelField labelStatus = new LabelField();
			labelStatus.setValue(UIContext.Constants.PatchStatus());
			labelStatus.setWidth(120);
			fsPatchDetails.add(labelStatus);

			LabelField labelStatusCol = new LabelField();
			labelStatusCol.setValue(UIContext.Constants.PatchColon());
			labelStatusCol.setWidth(10);
			fsPatchDetails.add(labelStatusCol);

			String strPatchStatusValue = "";

			if (patchInfoModel.getInstallStatus() == 1) {
				strPatchStatusValue = UIContext.Constants.PatchInstalled();
			} else if (patchInfoModel.getDownloadStatus() == 1) {
				strPatchStatusValue = UIContext.Constants.PatchDownloaded();
			} else if ((patchInfoModel.getDownloadStatus() == -1)
					|| (patchInfoModel.getDownloadStatus() == 0)) {
				strPatchStatusValue = UIContext.Constants
						.PatchNotDownloadedYet();
			}
			LabelField labelStatusValue = new LabelField();
			labelStatusValue.setValue(strPatchStatusValue);
			fsPatchDetails.add(labelStatusValue);

			// Patch Size
			LabelField labelPatchSize = new LabelField();
			labelPatchSize.setValue(UIContext.Constants.PatchBISize());
			labelPatchSize.setWidth(120);
			fsPatchDetails.add(labelPatchSize);

			LabelField labelPatchSizeCol = new LabelField();
			labelPatchSizeCol.setValue(UIContext.Constants.PatchColon());
			labelPatchSizeCol.setWidth(10);
			fsPatchDetails.add(labelPatchSizeCol);

			String strPatchSizeValue = "";
			strPatchSizeValue += Integer.toString(patchInfoModel.getSize());
			strPatchSizeValue += " ";
			strPatchSizeValue += UIContext.Constants.KB();

			LabelField labelPatchSizeValue = new LabelField();
			labelPatchSizeValue.setValue(strPatchSizeValue);
			fsPatchDetails.add(labelPatchSizeValue);
			
		}		
		
		// */
		// link

		Label lblPatchDetailsLink = new Label();
		lblPatchDetailsLink
				.ensureDebugId("aebc98a2-aacc-488a-b7f7-38645be1ddf8");
		lblPatchDetailsLink.setText(UIContext.Messages
				.D2DUpdateLinkText(UIContext.companyName));
		lblPatchDetailsLink.setStyleName("install_Wizard_PatchDetailsLink");

		lblPatchDetailsLink.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// com.google.gwt.user.client.Window.open(patchInfoModel.getPatchURL(),
				// "_BLANK", "");
				com.google.gwt.user.client.Window.open("http://www.google.com",
						"_BLANK", "");
			}
		});

		TableData tdPatchDetailsLink = new TableData();
		tdPatchDetailsLink.setColspan(3);
		tdPatchDetailsLink.setHorizontalAlign(HorizontalAlignment.LEFT);
		fsPatchDetails.add(lblPatchDetailsLink, tdPatchDetailsLink);

		lcNewUpdatesContainer.add(fsPatchDetails);

		if (patchInfoModel.getRebootRequired() == 1) {
			lcRebootWarning = new LayoutContainer();
			TableLayout tlRebootWarningLayout = new TableLayout();
			tlRebootWarningLayout.setColumns(2);
			tlRebootWarningLayout.setCellPadding(0);
			tlRebootWarningLayout.setCellSpacing(0);
			tlRebootWarningLayout.setWidth("100%");
			lcRebootWarning.setLayout(tlRebootWarningLayout);

			newUpdatesRebootWarningImage = IconHelper.create(
					ICON_SMALL_WARNING_URL, 16, 16);
			TableData tdRebootWarningImage = new TableData();
			tdRebootWarningImage.setHorizontalAlign(HorizontalAlignment.LEFT);
			tdRebootWarningImage.setWidth("1%");
			tdRebootWarningImage.setPadding(2);
			RebootWarningImage = newUpdatesRebootWarningImage.createImage();
			lcRebootWarning.add(RebootWarningImage, tdRebootWarningImage);

			lblNewUpdatesRebootMessage = new LabelField();
			// lblNewUpdatesRebootMessage.setValue(UIContext.Constants.D2DUpdateRebootDescription());
			lblNewUpdatesRebootMessage.setValue("only binaries' updates");
			lblNewUpdatesRebootMessage
					.setStyleName("install_Wizard_Description");
			TableData tdNewUpdatesRebootMessage = new TableData();
			tdNewUpdatesRebootMessage
					.setHorizontalAlign(HorizontalAlignment.LEFT);
			tdNewUpdatesRebootMessage.setWidth("99%");
			lcRebootWarning.add(lblNewUpdatesRebootMessage,
					tdNewUpdatesRebootMessage);

			cbRebootMachine = new CheckBox();
			cbRebootMachine
					.ensureDebugId("6e7f138d-6540-4def-9ad8-10c20a51815b");
			// cbRebootMachine.setBoxLabel(UIContext.Constants.D2DUpdateRebootAgreementLabel());
			cbRebootMachine.setBoxLabel("agree to reboot");
			// cbRebootMachine.setStyleName("install_Wizard_Description");
			if (patchInfoModel.getInstallStatus() == 1) {
				cbRebootMachine.setEnabled(false);
			}
			cbRebootMachine.addListener(Events.Change,
					new Listener<FieldEvent>() {
						@Override
						public void handleEvent(FieldEvent be) {
							if (cbRebootMachine.getValue()) {
								btInstall.setEnabled(true);
							} else {
								btInstall.setEnabled(false);
							}
						}
					});
			tdNewUpdatesRebootMessage = new TableData();
			tdNewUpdatesRebootMessage
					.setHorizontalAlign(HorizontalAlignment.LEFT);
			tdNewUpdatesRebootMessage.setWidth("100%");
			tdNewUpdatesRebootMessage.setPadding(2);
			tdNewUpdatesRebootMessage.setColspan(2);
			lcRebootWarning.add(cbRebootMachine, tdNewUpdatesRebootMessage);

			lcNewUpdatesContainer.add(lcRebootWarning);
		} else {
			lcServiceRestartInformation = new LayoutContainer();
			TableLayout tlServiceRestartInfoLayout = new TableLayout();
			tlServiceRestartInfoLayout.setColumns(2);
			tlServiceRestartInfoLayout.setCellPadding(0);
			tlServiceRestartInfoLayout.setCellSpacing(0);
			tlServiceRestartInfoLayout.setWidth("100%");
			lcServiceRestartInformation.setLayout(tlServiceRestartInfoLayout);

			newUpdatesRestartServiceImage = IconHelper.create(
					ICON_SMALL_WARNING_URL, 16, 16);
			TableData tdRestartServiceInfoImage = new TableData();
			tdRestartServiceInfoImage
					.setHorizontalAlign(HorizontalAlignment.LEFT);
			tdRestartServiceInfoImage.setWidth("1%");
			tdRestartServiceInfoImage.setPadding(2);
			RestartServiceInfoImage = newUpdatesRestartServiceImage
					.createImage();
			lcServiceRestartInformation.add(RestartServiceInfoImage,
					tdRestartServiceInfoImage);

			lblNewUpdatesRestartServiceMessage = new LabelField();
			lblNewUpdatesRestartServiceMessage
					.setValue(getStringUpdateRestartServiceDescription());
			// lblNewUpdatesRestartServiceMessage.setValue("will restart after update binaries");
			lblNewUpdatesRestartServiceMessage
					.setStyleName("install_Wizard_Description");
			TableData tdNewUpdatesRestartServiceMessage = new TableData();
			tdNewUpdatesRestartServiceMessage
					.setHorizontalAlign(HorizontalAlignment.LEFT);
			tdNewUpdatesRestartServiceMessage.setWidth("99%");
			lcServiceRestartInformation.add(lblNewUpdatesRestartServiceMessage,
					tdNewUpdatesRestartServiceMessage);
			lcNewUpdatesContainer.add(lcServiceRestartInformation);
		}
		
		defineNewUpdatesDialogButtons();

		dlgNewUpdatesWindow.add(lcNewUpdatesContainer);
		this.setFocusWidget(btCancel);
	}
/*	
	private void InstallDepPatch(final Label lblInstalldepLink) {
		lblInstalldepLink.addClickHandler(new ClickHandler() {
		String strText = lblInstalldepLink.getText();
		
			@Override
			public void onClick(ClickEvent event) {
				Info.display(strText, "");
				lblInstalldepLink.removeStyleName("install_Wizard_PatchDetailsLink");
				lblInstalldepLink.setText("Done to install dependency");
				lblInstalldepLink.setStyleName("install_Wizard");
			}
		});
	}
*/
	private void InstallDepPatch(final Label lblInstalldepLink,final Label lblInstallDeperLink) {
		lblInstalldepLink.addClickHandler(new ClickHandler() {
		String strText = lblInstalldepLink.getText();
		
			@Override
			public void onClick(ClickEvent event) {
				Info.display(strText, "");
				lblInstalldepLink.removeStyleName("install_Wizard_PatchDetailsLink");
				lblInstalldepLink.setText("Done to install dependency");
				lblInstalldepLink.setStyleName("install_Wizard");
				if ( lblInstallDeperLink != null )
					lblInstallDeperLink.setVisible(true);
			}
		});
	}
	
	private void CreateImagePart(ContentPanel in_cpNewUpdatePanel) {
		NewBIUpdatesDepInstallHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
			
					Info.display(UIContext.Messages.messageBoxTitleError(Utils
							.getProductName()), "installing the dependency of Patch");
					
					refresh(null);
					
			}
		};
		PatchWaiting2InstallImage = abPatchWaiting2InstallImage.createImage();
		PatchWaiting2InstallImage.addClickHandler(NewBIUpdatesDepInstallHandler);
		addImage2Win(in_cpNewUpdatePanel, PatchWaiting2InstallImage); 
	}
	
	private Widget addImage2Win(LayoutContainer in_cpNewUpdatePanel, Image in_NewUpdatesImage) {
		TableLayout layout = new TableLayout();
		layout.setColumns(1);

		LayoutContainer container = new LayoutContainer();

		container.setLayout(layout);
		container.setStyleAttribute("padding", "30px");
	
		TableData tableData = new TableData();
		tableData.setVerticalAlign(VerticalAlignment.MIDDLE);
		//tableData.setRowspan(2);
		container.add(in_NewUpdatesImage, tableData);

		in_cpNewUpdatePanel.add(container);

		return container;
	}
	
	private void CreateInstalledImagePart(ContentPanel in_cpNewUpdatePanel) {

		PatchInstalledImage = abPatchInstalledImage.createImage();
		addImage2Win(in_cpNewUpdatePanel, PatchInstalledImage); 
	}
	
	public void refresh(Object data) {
	
		lcNewUpdatesContainer.unmask();
		PatchWaiting2InstallImage.setUrl(ICON_PATCH_INSTALLED);
		
	}	

	private void defineNewUpdatesDialogButtons() {
		btInstall = new Button();
		btInstall.ensureDebugId("7a44f38f-c32f-47e3-a161-6d03aad1afa4");
		btInstall.setText(UIContext.Constants.D2DUpdateInstallButtonLabel());
		btInstall.setMinWidth(MIN_WIDTH);
		btInstall.setEnabled(false);
		if (patchInfoModel.getRebootRequired() == 1) {
			btInstall.setEnabled(false);
		}

		if (patchInfoModel.getInstallStatus() == 1) {
			btInstall.setEnabled(false);
		}

		if (loadingStatus == null) {
			loadingStatus = new LoadingStatusWindow(
					getStringLoadingStatusWindowTitle(), "");
			// loadingStatus.setBorders(false);
		}
		loadingStatus.setModal(false);

		btInstall.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				btInstall.setEnabled(false);
				getPatchManagerStatus();
			}
		});
		dlgNewUpdatesWindow.addButton(btInstall);

		btCancel = new Button();
		btCancel.ensureDebugId("83686109-b176-40da-b9bc-74a362ca1df7");
		btCancel.setText(UIContext.Constants.cancel());
		btCancel.setMinWidth(MIN_WIDTH);
		btCancel.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				iInstallStatus = -1;
				dlgNewUpdatesWindow.hide();

			}
		});
		dlgNewUpdatesWindow.addButton(btCancel);
		msgCallback = new Listener<MessageBoxEvent>() {
			@Override
			public void handleEvent(MessageBoxEvent be) {
				if (be.getButtonClicked().getItemId().equals(Dialog.OK))
					hide();
			}
		};
	}

	private void CheckDownloadStatusAndSubmitInstallRequest() {
		// loadingStatus = new
		// LoadingStatusWindow(getStringLoadingStatusWindowTitle(),UIContext.Messages.CheckingDownloadStatus());
		loadingStatus.setMessage(UIContext.Messages.CheckingDownloadStatus());
		loadingStatus.show();

		getBIUpdateInfo();
	}

	private void SubmitInstallRequest(PatchInfoModel patchInfoModel) {
		if (cbRebootMachine != null)
			patchInfoModel
					.setRebootRequired(cbRebootMachine.getValue() == true ? 1
							: 0);
		this.hide();
		PMInstallPatch(patchInfoModel);
	}

	/**
	 * EDGE apm will override this method.
	 */
	protected void getPatchManagerStatus() {
		commonService.getPatchManagerStatus(new AsyncCallback<Integer>() {
			public void onFailure(Throwable caught) {
				onGetPatchManagerStatusFailure(caught);
			}

			public void onSuccess(Integer result) {
				onGetPatchManagerStatusSuccess(result);
			}

		});
	}

	protected void onGetPatchManagerStatusSuccess(Integer result) {
		MessageBox msgError = new MessageBox();
		msgError.setTitleHtml(UIContext.Messages
				.D2DAutoBIUpdateMessageBoxTitle());
		msgError.setModal(true);
		switch (result) {
		case SummaryPanel.UPDATE_MANAGER_DOWN:// not running
			loadingStatus.hide();
			msgError.setIcon(MessageBox.ERROR);
			msgError.setMessage(getStringInstallFailedAsAutoUpdateIsdown());
			msgError.addCallback(msgCallback);
			Utils.setMessageBoxDebugId(msgError);
			msgError.show();
			break;
		case SummaryPanel.UPDATE_MANAGER_READY:// ready to process request
		{
			CheckDownloadStatusAndSubmitInstallRequest();
			break;
		}
		case SummaryPanel.UPDATE_MANAGER_BUSY:// busy with other request
		{
			loadingStatus.hide();
			msgError.setIcon(MessageBox.ERROR);
			msgError.setMessage(getStringAutoUpdateBusyWithOtherRequest());
			msgError.addCallback(msgCallback);
			Utils.setMessageBoxDebugId(msgError);
			msgError.show();
			break;
		}
		}
		return;
	}

	protected void onGetPatchManagerStatusFailure(Throwable caught) {
		loadingStatus.hide();
		MessageBox msgError = new MessageBox();
		msgError.setTitleHtml(getStringAutoUpdateMessageBoxTitle());
		msgError.setModal(true);
		msgError.setIcon(MessageBox.ERROR);
		msgError.setMessage(getStringAutoUpdateFailedToGetStatusError(caught
				.getMessage()));
		msgError.addCallback(msgCallback);
		Utils.setMessageBoxDebugId(msgError);
		msgError.show();
		return;
	}

	/**
	 * EDGE apm will override this method.
	 */
	protected void getBIUpdateInfo() {
		commonService.getBIUpdateInfo(new AsyncCallback<BIPatchInfoModel>() {
			public void onFailure(Throwable caught) {
				onGetPatchManagerStatusFailure(caught);
			}

			public void onSuccess(BIPatchInfoModel result) {
				onGetUpdateInfoSuccess(result);
			}

		});
	}

	protected void onGetUpdateInfoFailure(Throwable caught) {
		loadingStatus.hide();
		MessageBox msgError = new MessageBox();
		msgError.setTitleHtml(getStringAutoUpdateMessageBoxTitle());
		msgError.setModal(true);
		msgError.setIcon(MessageBox.ERROR);
		msgError.setMessage(getStringAutoUpdateFailedToGetStatusError(caught
				.getMessage()));
		Utils.setMessageBoxDebugId(msgError);
		msgError.show();
	}

	protected void onGetUpdateInfoSuccess(BIPatchInfoModel result) {
		patchInfoModel = result;
		if (patchInfoModel.getDownloadStatus() != 1) {
			// loadingStatus = new
			// LoadingStatusWindow(getStringLoadingStatusWindowTitle(),UIContext.Messages.DownloadingUpdateMessage());
			loadingStatus.setMessage(UIContext.Messages
					.DownloadingUpdateMessage());
			SubmitRequest(102);
		} else {
			// loadingStatus = new
			// LoadingStatusWindow(getStringLoadingStatusWindowTitle(),UIContext.Messages.InstallingUpdateMessage());
			loadingStatus.setMessage(UIContext.Messages
					.InstallingUpdateMessage());
			SubmitInstallRequest(patchInfoModel);
		}
	}

	/**
	 * EDGE apm will override this method.
	 */
	protected void PMInstallPatch(PatchInfoModel patchInfoModel) {
		service.PMInstallBIPatch(patchInfoModel,
				new BaseAsyncCallback<Integer>() {
					public void onFailure(Throwable caught) {
						// onPMInstallPatchFailure(caught);
					}

					public void onSuccess(Integer in_iInstallstatus) {
						onPMInstallSuccess(in_iInstallstatus);
					}
				});
	}

	protected void onPMInstallPatchFailure(Throwable caught) {
		loadingStatus.hide();
		MessageBox msgError = new MessageBox();
		msgError.setTitleHtml(getStringAutoUpdateMessageBoxTitle());
		msgError.setModal(true);
		msgError.setIcon(MessageBox.ERROR);
		msgError.addCallback(msgCallback);
		if (caught.getMessage() != "") {
			msgError.setMessage(UIContext.Messages
					.D2DErroredInstallingUpdate(caught.getMessage()));
			Utils.setMessageBoxDebugId(msgError);
			msgError.show();
		}
	}

	protected void onPMInstallSuccess(Integer in_iInstallstatus) {
		loadingStatus.hide();
		MessageBox msgError = new MessageBox();
		msgError.setTitleHtml(getStringAutoUpdateMessageBoxTitle());
		msgError.setModal(true);

		switch (in_iInstallstatus) {
		case INSTALL_ERROR_SUCCESS:
			msgError.setIcon(MessageBox.INFO);
			msgError.setMessage(UIContext.Messages.D2DUpdateInstallSuccess());
			iInstallStatus = INSTALL_ERROR_SUCCESS;
			break;
		default:
			btInstall.setEnabled(true);
			msgError.setIcon(MessageBox.ERROR);
			msgError.setMessage(UIContext.Messages.D2DAutoUpdateInstallFailed());
			msgError.addCallback(msgCallback);
			iInstallStatus = INSTALL_ERROR_FAIL;
			break;
		}
		Utils.setMessageBoxDebugId(msgError);
		msgError.show();
	}

	/**
	 * EDGE apm will override this method.
	 */
	protected void SubmitRequest(int in_iRequestType) {
		// NewUpdatesInstallWindow only show when success to check update. So no
		// need to start checking update again.
		loadingStatus.hide();
		MessageBox msgError = new MessageBox();
		msgError.setTitleHtml(getStringAutoUpdateMessageBoxTitle());
		msgError.setModal(true);
		msgError.setIcon(MessageBox.ERROR);
		msgError.setMessage(UIContext.Messages.FailedToGetUpdates());
		msgError.addCallback(msgCallback);
		Utils.setMessageBoxDebugId(msgError);
		msgError.show();
	}

	protected void onSubmitRequestSuccess(PatchInfoModel result) {
		patchInfoModel = result;
		loadingStatus.setMessage(UIContext.Messages
				.InstallingDownloadedUpdate());
		SubmitInstallRequest(patchInfoModel);
	}

	protected void onSubmitRequestFailure(Throwable caught) {
		loadingStatus.hide();
		MessageBox msgError = new MessageBox();
		msgError.setTitleHtml(getStringAutoUpdateMessageBoxTitle());
		msgError.setModal(true);
		msgError.setIcon(MessageBox.ERROR);
		msgError.setMessage(getStringDownloadExceptionMessage(caught
				.getMessage()));
		msgError.addCallback(msgCallback);
		Utils.setMessageBoxDebugId(msgError);
		msgError.show();
	}

	/**
	 * EDGE apm will override this method.
	 */
	protected String getStringUpdateRestartServiceDescription() {
		return UIContext.Messages.D2DUpdateRestartServiceDescription(
				UIContext.productNameD2D, UIContext.productNameD2D);
	}

	/**
	 * EDGE apm will override this method.
	 */
	protected String getStringLoadingStatusWindowTitle() {
		return UIContext.Messages.LoadingStatusWindowtitle();
		// return "D2D Updates";
	}

	/**
	 * EDGE apm will override this method.
	 */
	protected String getStringInstallFailedAsAutoUpdateIsdown() {
		return UIContext.Messages
				.InstallFailedAsD2DAutoUpdateIsdown(UIContext.productNameD2D);
	}

	/**
	 * EDGE apm will override this method.
	 */
	protected String getStringAutoUpdateBusyWithOtherRequest() {
		return UIContext.Messages.D2DAutoBIUpdateBusyWithOtherRequest();
	}

	/**
	 * EDGE apm will override this method.
	 */
	protected String getStringAutoUpdateMessageBoxTitle() {
		return UIContext.Messages.D2DAutoBIUpdateMessageBoxTitle();
	}

	/**
	 * EDGE apm will override this method.
	 */
	protected String getStringAutoUpdateFailedToGetStatusError(String message) {
		return UIContext.Messages
				.D2DAutoBIUpdateFailedToGetStatusError(message);
	}

	protected String getStringDownloadExceptionMessage(String message) {
		return UIContext.Messages.DownloadExceptionMessage(message);
	}
	
}
