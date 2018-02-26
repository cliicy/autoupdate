package com.ca.arcflash.ui.client.homepage;


import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.FormatUtil;
import com.ca.arcflash.ui.client.common.LoadingStatusWindow;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.ArchiveJobInfoModel;
import com.ca.arcflash.ui.client.model.BIPatchInfoModel;
import com.ca.arcflash.ui.client.model.BackupInformationSummaryModel;
import com.ca.arcflash.ui.client.model.LicInfoModel;
import com.ca.arcflash.ui.client.model.PatchInfoModel;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
//import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
//import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.MarginData;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HTML;
//import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;


public class TNewUpdates_BI_InstallWin extends Window {
	private TNewUpdates_BI_InstallWin dlgNewUpdatesWindow;
	//private LayoutContainer lcNewUpdatesContainer;
	private VerticalPanel lcNewUpdatesContainer;
	//private VerticalLayoutContainer lcNewUpdatesContainer;
	private LayoutContainer UpdatesHeaderContainer;
	private ContentPanel depImagePanel;
	private ContentPanel depInstalledPanel;
	private ContentPanel depPanel;
	private LabelField lblNewUpdatesDlgHeader;
	private LabelField lblNewUpdatesMessage;
	private LabelField lblNewUpdatesRebootMessage;
	private LabelField lblNewUpdatesRestartServiceMessage;
	private ClickHandler NewBIUpdatesDepInstallHandler = null;
	
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
	//added for stauts of patches' installation
	
	private AbstractImagePrototype newUpdatesRebootWarningImage;
//	private AbstractImagePrototype newUpdatesRestartServiceImage;
	private Image RebootWarningImage;
//	private Image RestartServiceInfoImage;
//	private CheckBox cbRebootMachine;
//	private Listener<MessageBoxEvent> msgCallback;

	// private ContentPanel cpRebootWarning;
//	private LayoutContainer lcRebootWarning;
//	private LayoutContainer lcServiceRestartInformation;

	protected LoadingStatusWindow loadingStatus;

	public TNewUpdates_BI_InstallWin(final BIPatchInfoModel in_patchInfo) {
		this.setResizable(true);
		dlgNewUpdatesWindow = this;
		//this.window = this;
		//this.window.setEnabled(true);
		dlgNewUpdatesWindow.setHeight(800);
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
		
		lcNewUpdatesContainer = new VerticalPanel();
		//lcNewUpdatesContainer.setScrollMode(Scroll.ALWAYS);
		
		
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
		tlPatchID.setColumns(3);
	
		fsPatchDetails.setLayout(tlPatchID);
		
		
		String str;
		for (int i = 0; i <10; i++) {
			// Package Id & Name
			LabelField labelPatchID = new LabelField();
			labelPatchID.setValue("ID" + " " + i);
			labelPatchID.setWidth(60);
			fsPatchDetails.add(labelPatchID);

			LabelField labelPatchIDValue1 = new LabelField();
			labelPatchIDValue1.setValue(":");
			labelPatchIDValue1.setWidth(5);
			fsPatchDetails.add(labelPatchIDValue1);

			LabelField labelPatchIDValue2 = new LabelField();
			str = "A" + i ;
			labelPatchIDValue2.setValue(str);
			fsPatchDetails.add(labelPatchIDValue2);

			// Package Dependency
			depPanel = new ContentPanel();
			depPanel.setHeaderVisible(false);
			depPanel.setBorders(false);
			depPanel.setBodyBorder(false);
			
			LabelField labelPatchDpy = new LabelField();
			str = "Dependency: " + "deww" + i ;
			labelPatchDpy.setValue(str);
			labelPatchDpy.setWidth(20);
			fsPatchDetails.add(labelPatchDpy);
			//depPanel.add(labelPatchDpy);
			
			//add image
			depImagePanel = new ContentPanel();
			depImagePanel.setHeaderVisible(false);
			depImagePanel.setBorders(false);
			depImagePanel.setBodyBorder(false);
			//depImagePanel.setStyleAttribute("padding", "4px");
			
			abPatchWaiting2InstallImage = IconHelper.create(ICON_PATCH_WAITING2INSTALL, 16, 16);
			CreateImagePart(depImagePanel);
			TableData dPImage = new TableData();
			dPImage.setVerticalAlign(Style.VerticalAlignment.MIDDLE);
			//dPImage.setWidth("5");
			fsPatchDetails.add(depImagePanel, dPImage);
	

			//add image

			//add patch-installed image/*
			depInstalledPanel = new ContentPanel();
			depInstalledPanel.setHeaderVisible(false);
			depInstalledPanel.setBorders(false);
			depInstalledPanel.setBodyBorder(false);
			
			
			abPatchInstalledImage = IconHelper.create(ICON_PATCH_INSTALLED, 16, 16);
			CreateInstalledImagePart(depInstalledPanel);
			TableData dPedImage = new TableData();
			//dPedImage.setWidth("1");
			TableData dPInstalledImage = new TableData();
			dPedImage.setVerticalAlign(Style.VerticalAlignment.TOP);

			fsPatchDetails.add(depInstalledPanel, dPInstalledImage);
			depInstalledPanel.hide();
			//depPanel.add(fsPatchDetails);
//*/			
			//add patch-installed image
			
			// Patch description
			LabelField labelPatchDescription = new LabelField();
			labelPatchDescription.setValue("description");
			labelPatchDescription.setWidth(60);
			fsPatchDetails.add(labelPatchDescription);

			LabelField labelPatchDsCo = new LabelField();
			labelPatchDsCo.setValue(":");
			labelPatchDsCo.setWidth(5);
			fsPatchDetails.add(labelPatchDsCo);

			LabelField labelPatchDescriptionValue = new LabelField();
			labelPatchDescriptionValue.setValue("testing testing");
			fsPatchDetails.add(labelPatchDescriptionValue);

						
			// Patch Size
			LabelField labelPatchSize = new LabelField();
			labelPatchSize.setValue("size");
			labelPatchSize.setWidth(60);
			fsPatchDetails.add(labelPatchSize);

			LabelField labelPatchSizeCol = new LabelField();
			labelPatchSizeCol.setValue(":");
			labelPatchSizeCol.setWidth(5);
			fsPatchDetails.add(labelPatchSizeCol);

			String strPatchSizeValue = "";
			strPatchSizeValue += "1258";
			strPatchSizeValue += " ";
			strPatchSizeValue += "KB";

			LabelField labelPatchSizeValue = new LabelField();
			labelPatchSizeValue.setValue(strPatchSizeValue);
			fsPatchDetails.add(labelPatchSizeValue);
		}
		lcNewUpdatesContainer.add(fsPatchDetails);
		dlgNewUpdatesWindow.add(lcNewUpdatesContainer);
		
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
		tableData.setRowspan(2);
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
}
