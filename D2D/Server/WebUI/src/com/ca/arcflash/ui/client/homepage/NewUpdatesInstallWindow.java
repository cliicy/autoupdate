package com.ca.arcflash.ui.client.homepage;

import java.util.Date;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.FormatUtil;
import com.ca.arcflash.ui.client.common.LoadingStatusWindow;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.PatchInfoModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.Dialog;
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

public class NewUpdatesInstallWindow extends Window{
	private NewUpdatesInstallWindow dlgNewUpdatesWindow;
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
	
	public static final int ERROR_GET_PATCH_INFO_SUCCESS=0;
	public static final int ERROR_GET_PATCH_INFO_FAIL =1;
	
	public static final int INSTALL_ERROR_SUCCESS=0;
	//public static final int INSTALL_ERROR_ACTIVE_JOBS = 3;
	public static final int INSTALL_ERROR_FAIL=4;
	public static final int INSTALL_ERROR_MAINAPPNOTINST = -520026879;
	public static final int INSTALL_ERROR_OSTYPEFAILED = -520026878;
	public static final int INSTALL_ERROR_MAINAPP_VER_ERR = -520026877;
	public static final int INSTALL_ERROR_ALREADYINSTALLED = -520026876;
	public static final int INSTALL_ERROR_HIGHERVERSION_PATCH_ALREADYINSTALLED = -520026875;
	public static final int INSTALL_ERROR_JOB_RUNNING = -520026874;
	public static final int INSTALL_ERROR_DISKSPACE_NOT_ENOUGH = -520026873;
	public static final int INSTALL_ERROR_SELFUNCOMPRESS_FAILED = 90000;
	public static final int ERROR_NONEW_PATCHES_AVAILABLE =5;
	public static final DateTimeFormat serverDate = DateTimeFormat.getFormat("MM/dd/yyyy");
	public static final DateTimeFormat dateFormat = FormatUtil.getShortDateFormat();
	
	//Patch Details
	private FieldSet fsPatchDetails;
	
	public static final String ICON_SMALL_WARNING_URL	=	"images/status_small_warning.png";
	public static final String ICON_LARGE_WARNING_URL	=	"images/status_mid_warning.png";
	public static final String ICON_SMALL_INFORMATION_URL	=	"images/LogMsg.png";
	private AbstractImagePrototype newUpdatesRebootWarningImage;
	private AbstractImagePrototype newUpdatesRestartServiceImage;
	private Image RebootWarningImage;
	private Image RestartServiceInfoImage;
	private CheckBox cbRebootMachine;
	private Listener<MessageBoxEvent> msgCallback;
	
	//private ContentPanel cpRebootWarning;
	private LayoutContainer lcRebootWarning;
	private LayoutContainer lcServiceRestartInformation;
	
		
	protected LoadingStatusWindow loadingStatus;
	
	public NewUpdatesInstallWindow(final PatchInfoModel in_patchInfo)
	{
		this.setResizable(false);
		dlgNewUpdatesWindow = this;
		dlgNewUpdatesWindow.setWidth("600");
		//dlgNewUpdatesWindow.setHeight("420");
		//dlgNewUpdatesWindow.setStyleName("install_Wizard");
		
		patchInfoModel = in_patchInfo;
		
		// comment the fixed height to fix issue: 41303:  Chrome - Update warning truncation
		// https://cscrjts001.ca.com/ccm/resource/itemName/com.ibm.team.workitem.WorkItem/41303
		//dlgNewUpdatesWindow.setHeight("400");	
		
		dlgNewUpdatesWindow.setHeadingHtml(UIContext.Constants.InstallUpdatesDialogHeader());
		
		TableLayout tlNewUpdatesLayout = new TableLayout();
		tlNewUpdatesLayout.setColumns(1);
		tlNewUpdatesLayout.setCellPadding(0);
		tlNewUpdatesLayout.setCellSpacing(3);
		tlNewUpdatesLayout.setWidth("100%");
		tlNewUpdatesLayout.setHeight("100%");
		
		lcNewUpdatesContainer = new LayoutContainer();
		
		lcNewUpdatesContainer.setLayout(tlNewUpdatesLayout);
		lcNewUpdatesContainer.setStyleName("install_Wizard");
		
		
		UpdatesHeaderContainer = new LayoutContainer();
		TableLayout headerLayout = new TableLayout();
		headerLayout.setColumns(2);		
		//headerLayout.setCellSpacing(2);
		//headerLayout.setWidth("100%");
		UpdatesHeaderContainer.setLayout(headerLayout);
		
		newUpdatesRebootWarningImage = IconHelper.create(ICON_LARGE_WARNING_URL,32,32);
		TableData informationImage = new TableData();
		informationImage.setHorizontalAlign(HorizontalAlignment.LEFT);
		informationImage.setWidth("1%");
		informationImage.setPadding(2);
		RebootWarningImage = newUpdatesRebootWarningImage.createImage();
		UpdatesHeaderContainer.add(RebootWarningImage,informationImage);
		
		lblNewUpdatesDlgHeader = new LabelField(UIContext.Constants.InstallUpdatesDialogTitle());
		lblNewUpdatesDlgHeader.setStyleName("install_Wizard_Title");
		TableData tdNewUpdatesHeader = new TableData();
		//tdNewUpdatesHeader.setColspan(2);		
		tdNewUpdatesHeader.setHorizontalAlign(HorizontalAlignment.LEFT);		
		UpdatesHeaderContainer.add(lblNewUpdatesDlgHeader,tdNewUpdatesHeader);
		
		lcNewUpdatesContainer.add(UpdatesHeaderContainer);
		
		lblNewUpdatesMessage = new LabelField();
		lblNewUpdatesMessage.setValue(UIContext.Messages.PatchDetailsDialogDescription(UIContext.companyName));
		lblNewUpdatesMessage.setStyleName("install_Wizard_Description");
		TableData tdNewUpdatesMessage = new TableData();
		//tdNewUpdatesMessage.setColspan(2);		
		tdNewUpdatesMessage.setHorizontalAlign(HorizontalAlignment.LEFT);
		lcNewUpdatesContainer.add(lblNewUpdatesMessage,tdNewUpdatesMessage);
			
		fsPatchDetails = new FieldSet();
		fsPatchDetails.setHeadingHtml(UIContext.Constants.PatchDetailsLabel());   
		  
		
		TableLayout tlPatchID = new TableLayout();
		tlPatchID.setCellPadding(1);
		tlPatchID.setColumns(3);
		tlPatchID.setWidth("100%");		
		fsPatchDetails.setLayout(tlPatchID);
		
		//Package Id
		LabelField labelPatchID = new LabelField();
		labelPatchID.setValue(UIContext.Constants.PatchID());	
		labelPatchID.setWidth(120);
		fsPatchDetails.add(labelPatchID);
		
		LabelField labelPatchIDValue1 = new LabelField();
		labelPatchIDValue1.setValue(UIContext.Constants.PatchColon());
		labelPatchIDValue1.setWidth(10);
		fsPatchDetails.add(labelPatchIDValue1);
		
		LabelField labelPatchIDValue = new LabelField();
		labelPatchIDValue.setValue(patchInfoModel.getPackageID());
		fsPatchDetails.add(labelPatchIDValue);
		
		//Published date
		LabelField labelPublishedDate = new LabelField();
		labelPublishedDate.setValue(UIContext.Constants.PatchPublishedDate());	
		labelPublishedDate.setWidth(120);
		fsPatchDetails.add(labelPublishedDate);
		
		LabelField labelPatchIDValue2 = new LabelField();
		labelPatchIDValue2.setValue(UIContext.Constants.PatchColon());
		labelPatchIDValue2.setWidth(10);
		fsPatchDetails.add(labelPatchIDValue2);
		
		LabelField labelPublishedDateValue = new LabelField();	
		Date date =serverDate.parse(patchInfoModel.getPublishedDate());
		labelPublishedDateValue.setValue(dateFormat.format(date));	
		//labelPublishedDateValue.setText(patchInfoModel.getPublishedDate());		
		fsPatchDetails.add(labelPublishedDateValue);

		//Patch description
		LabelField labelPatchDescription = new LabelField();
		labelPatchDescription.setValue(UIContext.Constants.PatchDescription());
		labelPatchDescription.setWidth(120);
		fsPatchDetails.add(labelPatchDescription);
		
		LabelField labelPatchIDValue3 = new LabelField();
		labelPatchIDValue3.setValue(UIContext.Constants.PatchColon());
		labelPatchIDValue3.setWidth(10);
		fsPatchDetails.add(labelPatchIDValue3);
		
		LabelField labelPatchDescriptionValue = new LabelField();
		labelPatchDescriptionValue.setValue(patchInfoModel.getDescription());		
		fsPatchDetails.add(labelPatchDescriptionValue);

		//Patch version
		LabelField labelPatchVersion = new LabelField();
		labelPatchVersion.setValue(UIContext.Constants.PatchVersion());	
		labelPatchVersion.setWidth(120);
		fsPatchDetails.add(labelPatchVersion);
		
		LabelField labelPatchIDValue4 = new LabelField();
		labelPatchIDValue4.setValue(UIContext.Constants.PatchColon());
		labelPatchIDValue4.setWidth(10);
		fsPatchDetails.add(labelPatchIDValue4);
		
		LabelField labelPatchVersionValue = new LabelField();
		labelPatchVersionValue.setValue(Integer.toString(patchInfoModel.getPatchVersionNumber()));		
		fsPatchDetails.add(labelPatchVersionValue);

		//Patch reboot
		LabelField labelPatchReboot = new LabelField();
		labelPatchReboot.setValue(UIContext.Constants.PatchReboot());
		labelPatchReboot.setWidth(120);
		fsPatchDetails.add(labelPatchReboot);
		
		LabelField labelPatchIDValue5 = new LabelField();
		labelPatchIDValue5.setValue(UIContext.Constants.PatchColon());
		labelPatchIDValue5.setWidth(10);
		fsPatchDetails.add(labelPatchIDValue5);
		
		LabelField labelPatchRebootValue = new LabelField();
		labelPatchRebootValue.setValue(patchInfoModel.getRebootRequired() == 1 ? UIContext.Constants.yes() : UIContext.Constants.no());		
		fsPatchDetails.add(labelPatchRebootValue);
		
		//Patch status
		LabelField labelStatus = new LabelField();
		labelStatus.setValue(UIContext.Constants.PatchStatus());	
		labelStatus.setWidth(120);		
		fsPatchDetails.add(labelStatus);		
		
		LabelField labelPatchIDValue6 = new LabelField();
		labelPatchIDValue6.setValue(UIContext.Constants.PatchColon());
		labelPatchIDValue6.setWidth(10);
		fsPatchDetails.add(labelPatchIDValue6);
		
		String strPatchStatusValue = "";
		if(patchInfoModel.getInstallStatus() == 1)
		{
			strPatchStatusValue = UIContext.Constants.PatchInstalled();
		}
		else if(patchInfoModel.getDownloadStatus() == 1)
		{
			strPatchStatusValue = UIContext.Constants.PatchDownloaded();
		}
		else if((patchInfoModel.getDownloadStatus() == -1) || (patchInfoModel.getDownloadStatus() == 0))
		{
			strPatchStatusValue = UIContext.Constants.PatchNotDownloadedYet();
		}
		LabelField labelStatusValue = new LabelField();
		labelStatusValue.setValue(strPatchStatusValue);		
		fsPatchDetails.add(labelStatusValue);
		
		//Patch Size
		LabelField labelPatchSize = new LabelField();
		labelPatchSize.setValue(UIContext.Constants.PatchSize());
		labelPatchSize.setWidth(120);
		fsPatchDetails.add(labelPatchSize);
		
		LabelField labelPatchIDValue7 = new LabelField();
		labelPatchIDValue7.setValue(UIContext.Constants.PatchColon());
		labelPatchIDValue7.setWidth(10);
		fsPatchDetails.add(labelPatchIDValue7);
		
		String strPatchSizeValue = "";
		strPatchSizeValue += Integer.toString(patchInfoModel.getSize());
		strPatchSizeValue += " ";
		strPatchSizeValue += UIContext.Constants.KB();
		
		LabelField labelPatchSizeValue = new LabelField();
		labelPatchSizeValue.setValue(strPatchSizeValue);		
		fsPatchDetails.add(labelPatchSizeValue);
		
		//link
		Label lblPatchDetailsLink = new Label();
		lblPatchDetailsLink.ensureDebugId("aebc98a2-aacc-488a-b7f7-38645be1ddf8");
		lblPatchDetailsLink.setText(UIContext.Messages.D2DUpdateLinkText(UIContext.companyName));
		lblPatchDetailsLink.setStyleName("install_Wizard_PatchDetailsLink");
		lblPatchDetailsLink.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				com.google.gwt.user.client.Window.open(patchInfoModel.getPatchURL(), "_BLANK", "");
			}
		});
		
		TableData tdPatchDetailsLink = new TableData();
		tdPatchDetailsLink.setColspan(3);
		tdPatchDetailsLink.setHorizontalAlign(HorizontalAlignment.LEFT);
		fsPatchDetails.add(lblPatchDetailsLink,tdPatchDetailsLink);
		
		lcNewUpdatesContainer.add(fsPatchDetails);
		
		if(patchInfoModel.getRebootRequired() == 1)
		{	
			lcRebootWarning = new LayoutContainer();
			TableLayout tlRebootWarningLayout = new TableLayout();
			tlRebootWarningLayout.setColumns(2);
			tlRebootWarningLayout.setCellPadding(0);
			tlRebootWarningLayout.setCellSpacing(0);
			tlRebootWarningLayout.setWidth("100%");
			lcRebootWarning.setLayout(tlRebootWarningLayout);
			
			newUpdatesRebootWarningImage = IconHelper.create(ICON_SMALL_WARNING_URL,16,16);
			TableData tdRebootWarningImage = new TableData();
			tdRebootWarningImage.setHorizontalAlign(HorizontalAlignment.LEFT);
			tdRebootWarningImage.setWidth("1%");
			tdRebootWarningImage.setPadding(2);
			RebootWarningImage = newUpdatesRebootWarningImage.createImage();
			lcRebootWarning.add(RebootWarningImage,tdRebootWarningImage);
			
			lblNewUpdatesRebootMessage = new LabelField();
			lblNewUpdatesRebootMessage.setValue(UIContext.Constants.D2DUpdateRebootDescription());
			lblNewUpdatesRebootMessage.setStyleName("install_Wizard_Description");
			TableData tdNewUpdatesRebootMessage = new TableData();
			tdNewUpdatesRebootMessage.setHorizontalAlign(HorizontalAlignment.LEFT);
			tdNewUpdatesRebootMessage.setWidth("99%");
			lcRebootWarning.add(lblNewUpdatesRebootMessage,tdNewUpdatesRebootMessage);	
			
			cbRebootMachine = new CheckBox();
			cbRebootMachine.ensureDebugId("6e7f138d-6540-4def-9ad8-10c20a51815b");
			cbRebootMachine.setBoxLabel(UIContext.Constants.D2DUpdateRebootAgreementLabel());
			//cbRebootMachine.setStyleName("install_Wizard_Description");
			if(patchInfoModel.getInstallStatus() == 1)
			{
				cbRebootMachine.setEnabled(false);
			}
			cbRebootMachine.addListener(Events.Change, new Listener<FieldEvent>()
				{
				@Override
				public void handleEvent(FieldEvent be) {
					if(cbRebootMachine.getValue())
					{
						btInstall.setEnabled(true);
					}
					else
					{
						btInstall.setEnabled(false);
					}
				}
			});
			tdNewUpdatesRebootMessage = new TableData();
			tdNewUpdatesRebootMessage.setHorizontalAlign(HorizontalAlignment.LEFT);
			tdNewUpdatesRebootMessage.setWidth("100%");
			tdNewUpdatesRebootMessage.setPadding(2);
			tdNewUpdatesRebootMessage.setColspan(2);
			lcRebootWarning.add(cbRebootMachine,tdNewUpdatesRebootMessage);
			
			lcNewUpdatesContainer.add(lcRebootWarning);
		}
		else
		{
			lcServiceRestartInformation = new LayoutContainer();
			TableLayout tlServiceRestartInfoLayout = new TableLayout();
			tlServiceRestartInfoLayout.setColumns(2);
			tlServiceRestartInfoLayout.setCellPadding(0);
			tlServiceRestartInfoLayout.setCellSpacing(0);
			tlServiceRestartInfoLayout.setWidth("100%");
			lcServiceRestartInformation.setLayout(tlServiceRestartInfoLayout);
			
			newUpdatesRestartServiceImage = IconHelper.create(ICON_SMALL_WARNING_URL,16,16);
			TableData tdRestartServiceInfoImage = new TableData();
			tdRestartServiceInfoImage.setHorizontalAlign(HorizontalAlignment.LEFT);
			tdRestartServiceInfoImage.setWidth("1%");
			tdRestartServiceInfoImage.setPadding(2);
			RestartServiceInfoImage = newUpdatesRestartServiceImage.createImage();
			lcServiceRestartInformation.add(RestartServiceInfoImage,tdRestartServiceInfoImage);
			
			lblNewUpdatesRestartServiceMessage = new LabelField();
			lblNewUpdatesRestartServiceMessage.setValue(getStringUpdateRestartServiceDescription());
			lblNewUpdatesRestartServiceMessage.setStyleName("install_Wizard_Description");
			TableData tdNewUpdatesRestartServiceMessage = new TableData();
			tdNewUpdatesRestartServiceMessage.setHorizontalAlign(HorizontalAlignment.LEFT);
			tdNewUpdatesRestartServiceMessage.setWidth("99%");
			lcServiceRestartInformation.add(lblNewUpdatesRestartServiceMessage,tdNewUpdatesRestartServiceMessage);	
			lcNewUpdatesContainer.add(lcServiceRestartInformation);
		}
		defineNewUpdatesDialogButtons();
		
		dlgNewUpdatesWindow.add(lcNewUpdatesContainer);
		this.setFocusWidget(btCancel);
	}
	
	private void defineNewUpdatesDialogButtons()
	{
		btInstall = new Button();
		btInstall.ensureDebugId("7a44f38f-c32f-47e3-a161-6d03aad1afa4");
		btInstall.setText(UIContext.Constants.D2DUpdateInstallButtonLabel());
		btInstall.setMinWidth(MIN_WIDTH);
		if(patchInfoModel.getRebootRequired() == 1)
		{
			btInstall.setEnabled(false);
		}
		
		if(patchInfoModel.getInstallStatus() == 1)
		{
			btInstall.setEnabled(false);
		}
		
		if(loadingStatus == null)
		{
			loadingStatus = new LoadingStatusWindow(getStringLoadingStatusWindowTitle(),"");
			//loadingStatus.setBorders(false);
		}
		loadingStatus.setModal(false);
		
		btInstall.addSelectionListener(new SelectionListener<ButtonEvent>()
		{
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
		btCancel.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				iInstallStatus = -1;
				dlgNewUpdatesWindow.hide();
				
			}});		
		dlgNewUpdatesWindow.addButton(btCancel);	
		msgCallback = new Listener<MessageBoxEvent>(){
			@Override
			public void handleEvent(MessageBoxEvent be) {
				if(be.getButtonClicked().getItemId().equals(Dialog.OK))
					hide();
			}
		};
	}
	
	private void CheckDownloadStatusAndSubmitInstallRequest() {
//		 loadingStatus = new LoadingStatusWindow(getStringLoadingStatusWindowTitle(),UIContext.Messages.CheckingDownloadStatus());
		loadingStatus.setMessage(UIContext.Messages.CheckingDownloadStatus());
		loadingStatus.show();
		
		getUpdateInfo();
	}
	
	private void SubmitInstallRequest(PatchInfoModel patchInfoModel)
	{
		if(cbRebootMachine != null)
			patchInfoModel.setRebootRequired(cbRebootMachine.getValue() == true ? 1 : 0);
		this.hide();
		PMInstallPatch(patchInfoModel);
	}
	
	/**
	 * EDGE apm will override this method.
	 */
	protected void getPatchManagerStatus(){
		commonService.getPatchManagerStatus(new AsyncCallback<Integer>(){
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
		msgError.setTitleHtml(UIContext.Messages.D2DAutoUpdateMessageBoxTitle());
		msgError.setModal(true);				
		switch(result)
		{
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
		msgError.setMessage(getStringAutoUpdateFailedToGetStatusError(caught.getMessage()));
		msgError.addCallback(msgCallback);
		Utils.setMessageBoxDebugId(msgError);
		msgError.show();
		return;
	}
	
	/**
	 * EDGE apm will override this method.
	 */
	protected void getUpdateInfo(){
		commonService.getUpdateInfo(new AsyncCallback<PatchInfoModel>(){
			public void onFailure(Throwable caught) {
				onGetPatchManagerStatusFailure(caught);
			}
			public void onSuccess(PatchInfoModel result) {
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
		msgError.setMessage(getStringAutoUpdateFailedToGetStatusError(caught.getMessage()));
		Utils.setMessageBoxDebugId(msgError);
		msgError.show();
	}
	
	protected void onGetUpdateInfoSuccess(PatchInfoModel result) {
		patchInfoModel = result;
		if(patchInfoModel.getDownloadStatus() != 1)
		{
			 //loadingStatus = new LoadingStatusWindow(getStringLoadingStatusWindowTitle(),UIContext.Messages.DownloadingUpdateMessage());
			loadingStatus.setMessage(UIContext.Messages.DownloadingUpdateMessage());
			SubmitRequest(102);
		}
		else
		{
			//loadingStatus = new LoadingStatusWindow(getStringLoadingStatusWindowTitle(),UIContext.Messages.InstallingUpdateMessage());
			loadingStatus.setMessage(UIContext.Messages.InstallingUpdateMessage());
			SubmitInstallRequest(patchInfoModel);
		}
	}
	
	/**
	 * EDGE apm will override this method.
	 */
	protected void PMInstallPatch(PatchInfoModel patchInfoModel){
		service.PMInstallPatch(patchInfoModel, new BaseAsyncCallback<Integer>(){
			public void onFailure(Throwable caught) {
				//onPMInstallPatchFailure(caught);
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
		if(caught.getMessage() != "")
		{
			msgError.setMessage(UIContext.Messages.D2DErroredInstallingUpdate(caught.getMessage()));
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
	protected void SubmitRequest(int in_iRequestType){
//		commonService.SubmitRequest(in_iRequestType, new AsyncCallback<PatchInfoModel>() {
//			public void onFailure(Throwable caught) {
//				onSubmitRequestFailure(caught);
//			}
//			public void onSuccess(PatchInfoModel result) {
//				onSubmitRequestSuccess(result);
//			}
//		});
		
		//NewUpdatesInstallWindow only show when success to check update. So no need to start checking update again. 
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
//		loadingStatus = new LoadingStatusWindow(getStringLoadingStatusWindowTitle(),UIContext.Messages.InstallingDownloadedUpdate());
		loadingStatus.setMessage(UIContext.Messages.InstallingDownloadedUpdate());
		SubmitInstallRequest(patchInfoModel);
	}
	
	protected void onSubmitRequestFailure(Throwable caught) {
		loadingStatus.hide();
		MessageBox msgError = new MessageBox();						
		msgError.setTitleHtml(getStringAutoUpdateMessageBoxTitle());
		msgError.setModal(true);						
		msgError.setIcon(MessageBox.ERROR);							
		msgError.setMessage(getStringDownloadExceptionMessage(caught.getMessage()));
		msgError.addCallback(msgCallback);
		Utils.setMessageBoxDebugId(msgError);
		msgError.show();
	}
	
	/**
	 * EDGE apm will override this method.
	 */
	protected String getStringUpdateRestartServiceDescription(){
		return UIContext.Messages.D2DUpdateRestartServiceDescription(UIContext.productNameD2D, UIContext.productNameD2D);
	}
	
	/**
	 * EDGE apm will override this method.
	 */
	protected String getStringLoadingStatusWindowTitle(){
		return UIContext.Messages.LoadingStatusWindowtitle();
		//return "D2D Updates";
	}
	
	/**
	 * EDGE apm will override this method.
	 */
	protected String getStringInstallFailedAsAutoUpdateIsdown() {
		return UIContext.Messages.InstallFailedAsD2DAutoUpdateIsdown(UIContext.productNameD2D);
	}
	
	/**
	 * EDGE apm will override this method.
	 */
	protected String getStringAutoUpdateBusyWithOtherRequest(){
		return UIContext.Messages.D2DAutoUpdateBusyWithOtherRequest();
	}
	
	/**
	 * EDGE apm will override this method.
	 */
	protected String getStringAutoUpdateMessageBoxTitle(){
		return UIContext.Messages.D2DAutoUpdateMessageBoxTitle();
	}
	
	/**
	 * EDGE apm will override this method.
	 */
	protected String getStringAutoUpdateFailedToGetStatusError(String message){
		return UIContext.Messages.D2DAutoUpdateFailedToGetStatusError(message);
	}
	
	protected String getStringDownloadExceptionMessage(String message){
		return UIContext.Messages.DownloadExceptionMessage(message);
	}
}
