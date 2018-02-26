package com.ca.arcflash.ui.client;

import com.ca.arcflash.ha.model.VCMConfigStatus;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyManager;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyService;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyServiceAsync;
import com.ca.arcflash.ui.client.coldstandby.setting.SettingWindow;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.homepage.D2DHomePageTab;
import com.ca.arcflash.ui.client.homepage.HomeContentFactory;
import com.ca.arcflash.ui.client.homepage.TNewUpdates_BI_InstallWin;
import com.ca.arcflash.ui.client.login.LoginForm;
import com.ca.arcflash.ui.client.login.LoginPanel;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.BackupVMModel;
import com.ca.arcflash.ui.client.model.CustomizationModel;
import com.ca.arcflash.ui.client.model.ExternalLinksModel;
import com.ca.arcflash.ui.client.model.RolePrivilegeModel;
import com.ca.arcflash.ui.client.model.VMBackupSettingModel;
import com.ca.arcflash.ui.client.model.VersionInfoModel;
import com.ca.arcflash.ui.client.vsphere.backup.VSphereBackupSettingWindow;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ARCFlashWebUI implements EntryPoint {
	
	private static final int INTERVAL_ACTIVE_TIMEOUT	=	60*60*1000;
	private Timer detectInactiveTimer;
	final CommonServiceAsync commmonService = GWT.create(CommonService.class);
	final ColdStandbyServiceAsync vcmService = GWT.create(ColdStandbyService.class); 
	private final LoginServiceAsync loginService = GWT.create(LoginService.class);
	
	@Override
	public void onModuleLoad() {
		loginService.getCustomizedModel(new AsyncCallback<CustomizationModel>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("onModuleLoad onFailure Error", caught);
			}

			@Override
			public void onSuccess(CustomizationModel result) {				
				
				if(result == null)
				{	
					CustomizationModel customizationModel = new CustomizationModel(); 
					if(customizationModel.get("FileCopyToCloud")==null)
					{		
						customizationModel.set("FileCopyToCloud", true);
					}
					if(customizationModel.get("FileCopy")==null)
					{
						customizationModel.set("FileCopy", true);
					}
					if(customizationModel.get("FileArchive")==null)
					{
						customizationModel.set("FileArchive", true);
					}
					
					UIContext.customizedModel = customizationModel ;
				}	
				
				else
				{
					if(result.get("FileCopyToCloud")==null)
					{		
						result.set("FileCopyToCloud", true);
					}
					if(result.get("FileCopy")==null)
					{
						result.set("FileCopy", true);
					}
					if(result.get("FileArchive")==null)
					{
						result.set("FileArchive", true);
					}
					UIContext.customizedModel = result;
				}
				
				UIContext.productNameD2D = UIContext.customizedModel.getProductName() == null?
						UIContext.Constants.productNameD2D() : UIContext.customizedModel.getProductName();
				if(UIContext.customizedModel.getCompanyName() != null) {
					UIContext.companyName = UIContext.customizedModel.getCompanyName();
				}
				
				load();
			}
		});
	}
	
	private void load() {
		if(UIContext.customizedModel.getProductName() != null)
			Window.setTitle(UIContext.Messages.windowTitle(UIContext.customizedModel.getProductName()));
		else
			Window.setTitle(UIContext.Messages.windowTitle(UIContext.productNameD2D));
		
		loginService.getLogonUser(new AsyncCallback<String>(){
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Error", caught);
			}

			@Override
			public void onSuccess(String result) {
				if (result == null){
					final LoginPanel panel = new LoginPanel();			
					RootPanel.get().add(panel);
					RootPanel.get().addStyleName("main");
					LoginForm window = panel.getLoginWindow();
					window.addListener(Events.Hide, new Listener<BaseEvent>()
					{
						@Override
						public void handleEvent(BaseEvent be) {												
							RootPanel.get().remove(panel);							
							getRolePrivilege();
						}
					});
					
				}else {
					getRolePrivilege();
					UIContext.loginUser = result;
				}
				
			}
		});
		
		commmonService.isExchangeGRTFuncEnabled(new AsyncCallback<Boolean>(){
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Error", caught);
			}

			@Override
			public void onSuccess(Boolean result) {
				UIContext.isExchangeGRTFuncEnabled = result.booleanValue();
			}			
		});
	}
	
	
	private void getRolePrivilege(){
		
		loginService.getRolePrivilegeModel(new AsyncCallback<RolePrivilegeModel>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Error", caught);
			}

			@Override
			public void onSuccess(RolePrivilegeModel rolePrivilege) {
				UIContext.RolePrivilege = rolePrivilege;
				createUIContent();
			}
		});
	}

	private void createUIContent() {
		
		String location = Location.getParameter(D2DHomePageTab.LOCATION_PARAM);
		
		if(location == null){
			location = HomeContentFactory.LOCATION_D2D;
		}
		
		//vSphere configuration page
		if(location.equals("config")){
			loginService.getVersionInfo(new BaseAsyncCallback<VersionInfoModel>() {
				@Override
				public void onFailure(Throwable caught) {
					GWT.log("Error", caught);
				}
				@Override
				public void onSuccess(VersionInfoModel result) {
					UIContext.serverVersionInfo = result;
					
					commmonService.getExternalLinks(result.getLocale(), result.getCountry(), new BaseAsyncCallback<ExternalLinksModel>(){

						@Override
						public void onFailure(Throwable caught) {
							GWT.log("Error", caught);
						}

						@Override
						public void onSuccess(ExternalLinksModel result) {
							UIContext.externalLinks = result;
							VSphereBackupSettingWindow window = new VSphereBackupSettingWindow();
							window.startTimerAvoidSessionTimeout();
							window.setModal(true);
							window.show();
					        
						}
						
					});

				}
		   });	
		}
		
		//vcm configuration page
		else if(location.equals("vcm")){
			loginService.getVersionInfo(new BaseAsyncCallback<VersionInfoModel>() {
				@Override
				public void onFailure(Throwable caught) {
					GWT.log("Error", caught);
				}
				@Override
				public void onSuccess(VersionInfoModel result) {
					UIContext.serverVersionInfo = result;
					
					commmonService.getExternalLinks(result.getLocale(), result.getCountry(), new BaseAsyncCallback<ExternalLinksModel>(){

						@Override
						public void onFailure(Throwable caught) {
							GWT.log("Error", caught);
						}

						@Override
						public void onSuccess(ExternalLinksModel result) {
							UIContext.externalLinks = result;
							SettingWindow window = new SettingWindow();
							window.setModal(true);
							window.show();
							
						}
						
					});

				}
		   });	
		}
		
		//vSphere homepage
		else if(location.equals(HomeContentFactory.LOCATION_VSPHERE)){
			UIContext.uiType = HomeContentFactory.UI_TYPE_VSPHERE;
			BackupVMModel backupVM = new BackupVMModel();
			String vmName = Location.getParameter("vmname");
			String instanceUUID = Location.getParameter("instanceuuid");
			backupVM.setVMName(vmName);
			backupVM.setVmInstanceUUID(instanceUUID);
			UIContext.backupVM = backupVM;
			
			loginService.getVMBackupConfiguration(UIContext.backupVM, new BaseAsyncCallback<VMBackupSettingModel>(){
				@Override
				public void onFailure(Throwable caught) {
					GWT.log("Error", caught);
				}

				@Override
				public void onSuccess(VMBackupSettingModel result) {
					if(result != null){
						if(result.backupVM.getVmHostName() == null || result.backupVM.getVmHostName().equals("")){
							result.backupVM.setVmHostName(UIContext.Messages.unknown_vm(result.backupVM.getVMName()));
						}
						
						UIContext.backupVM = result.backupVM;
						UIContext.isRemoteVCM = (result.getGenerateType() == 1);
						ILoadHomePage homePage = new ILoadHomePage() {

							@Override
							public Widget getHomeContentPanel(VCMConfigStatus result) {
								return HomeContentFactory.getVsphereHomeContentPanel(result);
							}

							@Override
							public boolean isNeedToCheckVCMConfig() {
								String withVCM = Location.getParameter("withvcm");
								if(withVCM != null && withVCM.equalsIgnoreCase("true"))
									return false;
								return true;
							}
							
						};
						addHomeContentPanel(homePage);	
					}else{
						MessageBox messageBox = new MessageBox();
						messageBox.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNamevSphere));
						messageBox.setMessage(UIContext.Constants.vSphereVMNotProtected());
						messageBox.setIcon(MessageBox.ERROR);
						messageBox.setModal(true);
						Utils.setMessageBoxDebugId(messageBox);
						messageBox.show();
					}
				}
			});
			
			
		}
		//Local D2D Homapage
		else{
			UIContext.uiType = HomeContentFactory.UI_TYPE_D2D;
			addHomeContentPanel(new ILoadHomePage() {

				@Override
				public Widget getHomeContentPanel(VCMConfigStatus result) {
					return HomeContentFactory.getHomeContentPanel(result);
				}

				@Override
				public boolean isNeedToCheckVCMConfig() {
					String withVCM = Location.getParameter("withvcm");
					if(withVCM != null && withVCM.equalsIgnoreCase("true"))
						return false;
							
					String location = Window.Location.getParameter(D2DHomePageTab.LOCATION_PARAM);
					return location == null 
						|| (!HomeContentFactory.LOCATION_BACKUP_SETTINGS.equalsIgnoreCase(location)
						    &&  !HomeContentFactory.LOCATION_RESTORE_BROWSE.equalsIgnoreCase(location) 
						    &&  !HomeContentFactory.LOCATION_RESTORE_SEARCH.equalsIgnoreCase(location)
						    &&  !HomeContentFactory.LOCATION_RESTORE_MAIN.equalsIgnoreCase(location) 
						    );
				}
			});	
		}
	}

	interface ILoadHomePage{
		Widget getHomeContentPanel(VCMConfigStatus result);
		boolean isNeedToCheckVCMConfig();
	}
	
	private void setWindowTitle() {
		String title = UIContext.productNameD2D;
		
		if(UIContext.uiType == HomeContentFactory.UI_TYPE_VSPHERE) {
			title += " " + UIContext.Constants.vSphereVMName()
					+ Window.Location.getParameter("vmname");
		} else {
			String hostName = UIContext.serverVersionInfo.getLocalHostName(); 
			if(hostName == null || hostName.isEmpty()){
				hostName = Window.Location.getHostName();
			}
			title += " " + UIContext.Constants.trustedHostWindowColumnName()
				+ ": " + hostName;
		}
		Window.setTitle(title);
	}
	
	private void addHomeContentPanel(final ILoadHomePage homeContent) {
		loginService.getVersionInfo(new BaseAsyncCallback<VersionInfoModel>() {
			@Override
			public void onFailure(Throwable caught) {
				GWT.log("Error", caught);
			}
			@Override
			public void onSuccess(VersionInfoModel result) {
				UIContext.serverVersionInfo = result;
				setWindowTitle();
				
				commmonService.getExternalLinks(result.getLocale(), result.getCountry(), new BaseAsyncCallback<ExternalLinksModel>(){

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Error", caught);
					}

					@Override
					public void onSuccess(ExternalLinksModel result) {
						UIContext.externalLinks = result;

						vcmService.getVCMConfigStatus(ColdStandbyManager.getVMInstanceUUIDFromURL(), new BaseAsyncCallback<VCMConfigStatus>(){

							@Override
							public void onFailure(Throwable caught) {
								GWT.log("Error", caught);
								showHomePageWithOrNotVCM(null, homeContent);
							}
							
							private void showHomePageWithOrNotVCM(VCMConfigStatus vcmStatus,
									final ILoadHomePage homeContent) {
								ColdStandbyManager.getInstance().setVcmStatus(vcmStatus);
								if(homeContent.isNeedToCheckVCMConfig())
									RootPanel.get().add(homeContent.getHomeContentPanel(vcmStatus));
								else{
									VCMConfigStatus status = null;
									String withVCM = Location.getParameter("withvcm");
									if(withVCM != null && withVCM.equalsIgnoreCase("true")) {
										status = new VCMConfigStatus();
										status.setVcmConfigured(true);
									}
									RootPanel.get().add(homeContent.getHomeContentPanel(status));
								}
							}
							
							@Override
							public void onSuccess(VCMConfigStatus vcmStatus) {
								showHomePageWithOrNotVCM(vcmStatus, homeContent);
							}
						});
						addTimerAndEventHandler();
					}
				});

			}
		});
	}

	private void addTimerAndEventHandler() {
		detectInactiveTimer = new Timer() {
			public void run() {
				commmonService.logout(new BaseAsyncCallback<Void>(){

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(Void result) {
						Window.Location.reload();
					}
					
				});
			}
		};
		detectInactiveTimer.schedule(INTERVAL_ACTIVE_TIMEOUT);
		
		Event.addNativePreviewHandler(new NativePreviewHandler(){

			@Override
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				if (event.getTypeInt() == Event.ONKEYPRESS
						|| event.getTypeInt() == Event.ONMOUSEMOVE){
					detectInactiveTimer.schedule(INTERVAL_ACTIVE_TIMEOUT);
				}
			}
			
		});
	}
}
