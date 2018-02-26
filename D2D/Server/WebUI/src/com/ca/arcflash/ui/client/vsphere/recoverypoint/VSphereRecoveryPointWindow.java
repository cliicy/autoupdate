package com.ca.arcflash.ui.client.vsphere.recoverypoint;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.BackupSettingsContent;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.HelpTopics;
import com.ca.arcflash.ui.client.common.PathSelectionPanel;
import com.ca.arcflash.ui.client.common.UserPasswordWindow;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.CopyJobModel;
import com.ca.arcflash.ui.client.model.JobLauncher;
import com.ca.arcflash.ui.client.model.RecoveryPointModel;
import com.ca.arcflash.ui.client.restore.RecoveryPointsPanel;
import com.ca.arcflash.ui.client.restore.RestoreConstants;
import com.ca.arcflash.ui.client.restore.RestoreContext;
import com.ca.arcflash.ui.client.vsphere.vmrecover.VMRecoveryPointsPanel;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
//import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class VSphereRecoveryPointWindow extends Window {

	final LoginServiceAsync service = GWT.create(LoginService.class);
	final CommonServiceAsync commonService = GWT.create(CommonService.class);

	private static final int RECOVERY_POINT_WIDTH = 715;
	private static final int RECOVERY_POINT_HEIGHT = 660;
	private static final int BUTTON_MINWIDTH = 100;

	private Button okButton;
	private Button cancelButton;
//	private BaseSimpleComboBox<String> compressionOption;
	private VSphereRecoveryPointWindow thisWindow;
	private VMRecoveryPointsPanel rpPanel;
	
//	private PathSelectionPanel pathSelection;
//	private VerticalPanel recoveryPointPanel;
	private VSphereExportOptionPanel exportOpionPanel;
	
//	private LabelField note;
	
	private Timer t;
	
	private boolean okButtonClicked = false;
	private Button nextPageButton;
	protected int currentPage;
	
	public static final int BROWSE_RECOVERY_POINT = 0;
	public static final int SUBMIT_COPY = 1;
	private CardLayout cardLayout;
	private MessageBox validatingBox;
	
	//wanqi06
	public static final String ERR_REMOTE_DEST_WINSYSMSG = "30064771075";

	public VSphereRecoveryPointWindow() {
		setHeadingHtml(UIContext.Constants.recoveryPointTitle());
		
		LayoutContainer container = new LayoutContainer();
		cardLayout = new CardLayout();
		container.setLayout(cardLayout);
		
//		recoveryPointPanel = new VerticalPanel();
		
		rpPanel = new VMRecoveryPointsPanel(this);
		rpPanel.setStyleName("exportMainStyle");
		rpPanel.setAutoHeight(true);
		rpPanel.setRestoreManager(false);
//		recoveryPointPanel.add(rpPanel);
		container.add(rpPanel);
		
		thisWindow = this;
		exportOpionPanel = new VSphereExportOptionPanel(thisWindow);
//		exportOpionPanel.setWidth("100%");
		exportOpionPanel.setAutoHeight(true);
		exportOpionPanel.setAutoWidth(true);
		exportOpionPanel.addStyleName("exportMainStyle");
		container.add(exportOpionPanel);
		
		add(container);
//		createExportSection();
		createButtons();
		
		this.setResizable(true);
		this.setWidth(RECOVERY_POINT_WIDTH);
		this.setHeight(RECOVERY_POINT_HEIGHT);
		
		this.addListener(Events.Hide, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				if (t != null) {
					t.cancel();
				}
				
				RestoreContext.destory();
			}
		});
		
	}
	
	public boolean isBackupToRps() {
		return rpPanel.getSrcRPSHost() != null;
	}

	public void createButtons() {
		nextPageButton = new Button();
		nextPageButton.ensureDebugId("BD7014FD-3F26-4998-9879-8B5B468945E9");
		nextPageButton.setMinWidth(BUTTON_MINWIDTH);
		nextPageButton.setText(UIContext.Constants.restoreNext());
		nextPageButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				if(currentPage == BROWSE_RECOVERY_POINT) {
					if(validate()) {
						RestoreContext.setRecoveryPointModel(rpPanel.getSelectedRecoveryPoint());
						cardLayout.setActiveItem(exportOpionPanel);
						nextPageButton.setText(UIContext.Constants.restorePrevious());
						okButton.setVisible(true);
						exportOpionPanel.repaint();
						currentPage = SUBMIT_COPY;
					}
				}
				else {
					cardLayout.setActiveItem(rpPanel);
					nextPageButton.setText(UIContext.Constants.restoreNext());
					okButton.setVisible(false);
					currentPage = BROWSE_RECOVERY_POINT;
				}
			}
		});
		
		addButton(nextPageButton);
		
		okButton = new Button(){

			@Override
			protected void onDisable() {
				addStyleName("item-disabled");
				super.onDisable();		   
			}

			@Override
			protected void onEnable() {
				removeStyleName("item-disabled");
				super.onEnable();
			}
			
		};
		
		okButton.ensureDebugId("FC394D2D-FFD4-4a22-AE55-52033149996");
		okButton.setMinWidth(BUTTON_MINWIDTH);
		okButton.setText(UIContext.Constants.recoveryPointsCreateACopy());
		okButton.disable();
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				okButtonClicked = true;
				okButton.disable();
				
				thisWindow.createRecoveryPointCopy();
			}

		});
		okButton.setVisible(false);
		addButton(okButton);
		
		cancelButton = new Button();
		cancelButton.ensureDebugId("{CE979C83-667F-42e2-AD95-CF01BC6929CA}");
		cancelButton.setMinWidth(BUTTON_MINWIDTH);
		cancelButton.setText(UIContext.Constants.cancel());
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				thisWindow.hide();
			}

		});
		addButton(cancelButton);
		
		Button helpButton = HelpTopics.createHelpButton(UIContext.externalLinks.getVMCopyRecoveryPointHelp(), BUTTON_MINWIDTH);
		addButton(helpButton);
	}

	@Override
	protected void onLoad() {		
		super.onLoad();
		t = new Timer() {
			@Override
			public void run() {
				PathSelectionPanel pathSelection = exportOpionPanel.getPathSelectionPanel();
				if(pathSelection  == null || !pathSelection.isRendered()) return;
				
				String newDest = pathSelection.getDestination();
				if (newDest == null || newDest.isEmpty()) {
					if (okButton.isEnabled()) {
						okButton.disable();
					}
				} else {
					if (!okButton.isEnabled() && !okButtonClicked) {
						okButton.enable();
					}
				}
			}
		};

		t.scheduleRepeating(300);
	}

	public void createRecoveryPointCopy() {
		
		AsyncCallback<Boolean> cb = new BaseAsyncCallback<Boolean>() {
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				if (validatingBox != null) {
					validatingBox.close();
				}
			}

			@Override
			public void onSuccess(Boolean result) {
				
				if (validatingBox != null) {
					validatingBox.close();
				}
				
				if(result) {
					CopyJobModel model = new CopyJobModel();
	
					exportOpionPanel.processOptions(model);
					
					RecoveryPointModel m = rpPanel.getSelectedRecoveryPoint();
	
					model.setSessionNumber(m.getSessionID());
					model.setJobType(2);
					model.setSessionPath(rpPanel.getSessionPath());
					model.setUserName(rpPanel.getUserName());
					model.setPassword(rpPanel.getPassword());
					model.setVMInstanceUUID(UIContext.backupVM.getVmInstanceUUID());
					model.setJobLauncher(JobLauncher.VSPHERE.getValue());
					model.setRestPoint(-1); // -1 is for manual export.
					model.rpsHost = rpPanel.getSrcRPSHost();
					model.setRpsPolicyUUID(rpPanel.getRpsPolicy());
					model.setRPSDataStoreUUID(rpPanel.getRpsDataStore());
					model.setRpsDataStoreDisplayName(rpPanel.getRpsDSDisplayName());
					submitCopy(model);
				}
				else
					enableOkButton();
			}
		};

		validatingBox = MessageBox.wait(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D), UIContext.Constants
				.validating(), "");
		Utils.setMessageBoxDebugId(validatingBox);
		
		exportOpionPanel.validate(cb);
	}

	private void submitCopy(final CopyJobModel model) {
		service.submitVMCopyJob(model, new BaseAsyncCallback<Integer>() {

			@Override
			public void onFailure(Throwable arg0) {
				if(arg0 instanceof BusinessLogicException) {
					BusinessLogicException ble = (BusinessLogicException)arg0;
					if(("4294967314".equals((ble).getErrorCode()))) {
						MessageBox.info(UIContext.Messages.messageBoxTitleInformation(
								Utils.getProductName()), 
								(ble).getDisplayMessage(), null);
						thisWindow.setVisible(false);
						return ;
					}
				}
				String dest = model.getDestinationPath();
				if(arg0 instanceof BusinessLogicException 
						&& ERR_REMOTE_DEST_WINSYSMSG.equals(((BusinessLogicException)arg0).getErrorCode())
					&& dest != null && dest.startsWith("\\\\"))
				{

					final UserPasswordWindow dlg = new UserPasswordWindow(dest, model.getDestinationUserName(), model.getDestinationPassword());
					dlg.setModal(true);
					
					dlg.addWindowListener(new WindowListener()
					{				
						public void windowHide(WindowEvent we) {
							if (dlg.getCancelled() == false)
							{
								String username = dlg.getUsername();
								String password = dlg.getPassword();
								model.setDestinationUserName(username);
								model.setDestinationPassword(password);
								PathSelectionPanel pathSelection = exportOpionPanel.getPathSelectionPanel();
								pathSelection.setUsername(username);
								pathSelection.setPassword(password);
								submitCopy(model);
							}
							else {
								enableOkButton();
							}
						}
					});
					dlg.show();
				}
				else {
					super.onFailure(arg0);
					enableOkButton();
				}
			}

			@Override
			public void onSuccess(Integer result) {
				if (result == 0) {
					GWT.log("Submit Copy Job Successfully", null);
					thisWindow.setVisible(false);

					Info.display(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D), UIContext.Constants
							.recoveryPointsJobSubmittedSuccessfully());
				} else {
					enableOkButton();
					GWT.log("Submit Copy Job Failed = " + result, null);
					MessageBox msg = new MessageBox();
					msg.setIcon(MessageBox.ERROR);
					msg
							.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNamevSphere));
					msg.setMessage(UIContext.Constants
							.recoveryPointsJobFailedToSubmit());
					msg.setModal(true);
					Utils.setMessageBoxDebugId(msg);
					msg.show();
				}
			}

		});
	}

	private boolean validate() {
		RecoveryPointModel model = rpPanel.getSelectedRecoveryPoint(); 
		if (model == null) {
			showErrorMessage(UIContext.Constants
					.recoveryPointsSelectOneRecoveryPoint());
			return false;
//		}else if(model.getFSCatalogStatus()!= RestoreConstants.FSCAT_FINISH) {
//			showErrorMessage(UIContext.Constants.recoveryPointsNoCatalog());
//			return false;
		}
		
		/*if (model.listOfRecoveryPointItems==null || model.listOfRecoveryPointItems.size()==0){
			showErrorMessage(UIContext.Constants.warningMessageNoVolumeInfo());
			return false;
		}*/
		
		return true;
	}

	private void showErrorMessage(String msgStr) {
		MessageBox msg = new MessageBox();
		msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNamevSphere));
		msg.setIcon(MessageBox.ERROR);
		msg.setMessage(msgStr);
		msg.setModal(true);
		Utils.setMessageBoxDebugId(msg);
		msg.show();
	}

	private void enableOkButton() {
		okButtonClicked = false;
		okButton.enable();
	}
	
	public String getSessionPath() {
		return rpPanel.getSessionPath();
	}
	
	public RecoveryPointModel getSelectedRecoveryPoint() {
		return rpPanel.getSelectedRecoveryPoint();
	}
	
}
