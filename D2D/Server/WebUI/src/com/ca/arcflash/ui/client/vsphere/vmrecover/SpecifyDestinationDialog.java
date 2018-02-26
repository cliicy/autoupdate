package com.ca.arcflash.ui.client.vsphere.vmrecover;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.HelpTopics;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.BackupVMModel;
import com.ca.arcflash.ui.client.model.VCloudDirectorModel;
import com.ca.arcflash.ui.client.model.VCloudOrgnizationModel;
import com.ca.arcflash.ui.client.model.VCloudStorageProfileModel;
import com.ca.arcflash.ui.client.model.VCloudVirtualDataCenterModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.core.FastMap;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Element;

public class SpecifyDestinationDialog extends Dialog {
	private static String helpURL = UIContext.externalLinks.getRecoveryVAppSpecifyVDCHelp();
	final LoginServiceAsync service = GWT.create(LoginService.class);
	private SpecifyDestinationDialog thisDialog;

	private ContentPanel centerArea;
	private CardLayout cardlayout;
	private VCloudConnectionPanel directorConnectionPanel;
	private VCloudTreePanel vCloudTreePanel;
	private FastMap<LayoutContainer> pageMap;
	private int currentPageIndex;

	private BaseAsyncCallback<Integer> parentCallback;
	private VCloudDirectorModel vCloudDirectorModel;
	private BackupVMModel.Type vmType;

	private Button helpButton;
	private Button okButton;
	private Button cancelButton;
	
	private final int VCLOUD_CONNECTION_PAGE_INDEX = 0;
	private final int VCLOUD_TREE_PAGE_INDEX = 1;

	public SpecifyDestinationDialog(BaseAsyncCallback<Integer> parentCallback, VCloudDirectorModel vCloudModel, BackupVMModel.Type vmType) {
		this.thisDialog = this;
		this.parentCallback = parentCallback;
		this.vCloudDirectorModel = vCloudModel;
		this.vmType = vmType;
		createContent();
	}
	
	public Button getOkButton() {
		return okButton;
	}
	
	public Button getCancelButton() {
		return cancelButton;
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				setFocusWidget(null);
			}
		});
	}
	
	@Override
	protected void onShow() {
		super.onShow();
		
		thisDialog.unmask();
	}
	
	private void addToCenterAreaAndPageMap(int index, LayoutContainer content) {
		centerArea.add(content);
		pageMap.put(String.valueOf(index), content);
	}
	
	private LayoutContainer getFromPageMap(int index) {
		return pageMap.get(String.valueOf(index));
	}
	
	private void setActiveItem(int index) {
		if (index == 0) {
			this.setHeadingHtml(UIContext.Constants.vAppRestoreSpecifyDestTitle());
		}
		currentPageIndex = index;
		cardlayout.setActiveItem(getFromPageMap(index));
	}
	
	private void createContent() {
//		this.setResizable(false);
		this.setSize(600, 500);
		this.setModal(true);
		this.setButtons(Dialog.OKCANCEL);
		this.setScrollMode(Scroll.AUTO);
		this.setLayout(new RowLayout());
		
		centerArea = new ContentPanel();
		centerArea.ensureDebugId("01749ed1-fec5-4405-9840-e33b3c197f3a");
		centerArea.setHeaderVisible(false);
		centerArea.setFrame(false);
		centerArea.setFooter(false);
		Style centerAreaStyle = centerArea.getElement().getStyle();
		centerAreaStyle.setMargin(10, Unit.PX);
		centerAreaStyle.setBackgroundColor("white");
		this.add(centerArea);
		cardlayout = new CardLayout();
		centerArea.setLayout(cardlayout);
		
		helpButton = new Button();
		helpButton.ensureDebugId("9a94f9c1-1e2d-4ced-abe0-fb19d9e0932b");
		helpButton.setText(UIContext.Constants.help());
		helpButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				HelpTopics.showHelpURL(helpURL);
			}
		});

		this.setButtonAlign(HorizontalAlignment.LEFT);
		this.getButtonBar().insert(helpButton, 0);
		this.getButtonBar().insert(new FillToolItem(), 1);

		okButton = this.getButtonById(Dialog.OK);
		okButton.setText(UIContext.Constants.ok());
		okButton.setEnabled(false);
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				if (currentPageIndex == 1) {
					getStoragePoliyListFromVDC();
				} else {
					showErrorMessage(UIContext.Constants.vAppRestoreSpecifyDestNoVDCMsg());
				}
			}
		});

		cancelButton = this.getButtonById(Dialog.CANCEL);
		cancelButton.setText(UIContext.Constants.cancel());
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				parentCallback.onSuccess(1);
				thisDialog.hide();
			}
		});
		
		BaseAsyncCallback<BaseModelData> callback = new BaseAsyncCallback<BaseModelData>() {
			@Override
			public void onSuccess(BaseModelData result) {
				if (VCLOUD_CONNECTION_PAGE_INDEX == currentPageIndex && result instanceof VCloudDirectorModel) {
					vCloudTreePanel.setVCloudDirectorModel((VCloudDirectorModel) result);
					okButton.setEnabled(false);
					setActiveItem(VCLOUD_TREE_PAGE_INDEX);
				} else if (VCLOUD_TREE_PAGE_INDEX == currentPageIndex && result instanceof VCloudVirtualDataCenterModel) {
					VCloudVirtualDataCenterModel selectedVDCModel = (VCloudVirtualDataCenterModel) result;
					List<VCloudOrgnizationModel> orgList = vCloudDirectorModel.getOrganizations();
					if (orgList == null) {
						orgList = new ArrayList<>();
						vCloudDirectorModel.setOrganizations(orgList);
					} else if (!orgList.isEmpty()) {
						orgList.clear();
					}
					
					VCloudOrgnizationModel orgModel = new VCloudOrgnizationModel();
					orgList.add(orgModel);
					
					List<VCloudVirtualDataCenterModel> vdcList = new ArrayList<>();
					vdcList.add(selectedVDCModel);
					orgModel.setVitrualDataCenters(vdcList);
					
					okButton.setEnabled(true);
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				okButton.setEnabled(false);
			}
		};
		
		pageMap = new FastMap<LayoutContainer>();
		if (vmType == BackupVMModel.Type.VMware_VApp) {
			directorConnectionPanel = new VCloudConnectionPanel(callback);
			vCloudTreePanel = new VCloudTreePanel(callback);
			vCloudTreePanel.getReturnButton().addListener(Events.OnClick, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
					directorConnectionPanel.unmask();
					setActiveItem(VCLOUD_CONNECTION_PAGE_INDEX);
				}
			});
			
			addToCenterAreaAndPageMap(VCLOUD_CONNECTION_PAGE_INDEX, directorConnectionPanel);
			addToCenterAreaAndPageMap(VCLOUD_TREE_PAGE_INDEX, vCloudTreePanel);
			
			directorConnectionPanel.setInputVCloudDirectorModel(vCloudDirectorModel);
			setActiveItem(VCLOUD_CONNECTION_PAGE_INDEX);
		}
	}
	
	private void getStoragePoliyListFromVDC() {
		thisDialog.mask(UIContext.Constants.vAppRestoreSpecifyDestLoadingPolicyMask());
		final VCloudDirectorModel vCloudModel = (VCloudDirectorModel) vCloudDirectorModel;
		final VCloudVirtualDataCenterModel vDCModel = vCloudModel.getOrganizations().get(0).getVitrualDataCenters().get(0);
		service.getStorageProfilesOfVDC(vCloudModel, vDCModel.getId(), new BaseAsyncCallback<List<VCloudStorageProfileModel>>() {
			@Override
			public void onSuccess(List<VCloudStorageProfileModel> result) {
				if (result != null && !result.isEmpty()) {
					vDCModel.setStorageProfiles(result);
					vCloudModel.setTargetVDC(vDCModel);
					
					thisDialog.hide();
					thisDialog.unmask();
					parentCallback.onSuccess(0);
				} else {
					thisDialog.unmask();
					showErrorMessage(UIContext.Constants.vAppRestoreSpecifyDestNoPolicyError());
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
				thisDialog.unmask();
				showErrorMessage(UIContext.Constants.vAppRestoreSpecifyDestFailedGetPolicyError());
			}
		});
	}
	
	public static void showErrorMessage(String message) {
		MessageBox messageBox = new MessageBox();
		messageBox.addCallback(new Listener<MessageBoxEvent>(){
			@Override
			public void handleEvent(MessageBoxEvent be) {
			}
		});
		
		messageBox.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.Constants.productNameD2D()));
		messageBox.setMessage(message);
		messageBox.setIcon(MessageBox.ERROR);
		messageBox.setModal(true);
		messageBox.setMinWidth(400);
		Utils.setMessageBoxDebugId(messageBox);
		messageBox.show();
	}
}
