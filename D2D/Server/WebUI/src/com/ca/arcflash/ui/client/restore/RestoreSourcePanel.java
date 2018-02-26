package com.ca.arcflash.ui.client.restore;

import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.ICommonRPSService4D2D;
import com.ca.arcflash.ui.client.common.ICommonRPSService4D2DAsync;
import com.ca.arcflash.ui.client.common.PathSelectionPanel;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.BackupD2DModel;
import com.ca.arcflash.ui.client.model.BackupRPSDestSettingsModel;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.ca.arcflash.ui.client.model.RpsPolicy4D2DRestoreModel;
import com.ca.arcflash.ui.client.model.rps.RpsHostModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.FitData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class RestoreSourcePanel extends LayoutContainer {
	protected LoginServiceAsync service = GWT.create(LoginService.class);
	protected final ICommonRPSService4D2DAsync configRPSInD2DService 
			= GWT.create(ICommonRPSService4D2D.class);
	protected static final int MIN_FIELD_WIDTH = 230;
	
	protected LabelField rpsDatastore;
	protected LabelField rpsServer;
	private LabelField selectedD2D = new LabelField();
	private LayoutContainer rpsSourceContainer = null;
	
	private LabelField sharedFolder = null; 
	
	public BackupRPSDestSettingsModel rpsDestSettings;	
	protected RestoreSelectRPSSettingWindow rpsSettingWindow;
	
	private List<RpsPolicy4D2DRestoreModel> rpsPolicyList;
	protected BackupD2DModel currentD2D;
	
	protected IRestoreSourceListener listener;
	private String destination;
	private String userName;
	private String password;
	private boolean backupToDataStore = false;
	private Listener<BaseEvent> changeListener;
	private TableLayout panelLayout;
	
	private TableData buttonTD; 
	public void setPanelWidth(String width){
    	panelLayout.setWidth(width);
    }
	
	public void setButtonHAlign(HorizontalAlignment horizontalAlign){
		buttonTD.setHorizontalAlign(horizontalAlign);
    }
	
	public void setPadding(int padding){
		panelLayout.setCellPadding(0);
    }
	
	public RestoreSourcePanel(){
		panelLayout = new TableLayout();
		panelLayout.setColumns(2);
		panelLayout.setWidth("98%");
		panelLayout.setCellPadding(4);
		this.setLayout(panelLayout);
		
		TableData td = new TableData();
		td.setWidth("85%");		
		this.add(renderSourceDetail(), td);
		
		buttonTD = new TableData();
		buttonTD.setWidth("15%");
		buttonTD.setVerticalAlign(VerticalAlignment.TOP);
		this.add(renderChangeButton(), buttonTD);
		
		getDefaultValue();
	}
	
	private LayoutContainer renderSourceDetail() {
		LayoutContainer sourceContainer = new LayoutContainer();
		sourceContainer.setLayout(new FitLayout());
//		sourceContainer.setWidth("98%");
		sourceContainer.setAutoHeight(true);
		
		sharedFolder = new LabelField();
		sharedFolder.setEmptyText("No shared folder");
		sharedFolder.setStyleName("longWord");
		sharedFolder.setWidth(600);
		FitData fd = new FitData(2);
		sourceContainer.add(sharedFolder, fd);
		sharedFolder.setVisible(false);
		
		rpsSourceContainer = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(2);
		tl.setCellPadding(2);
		tl.setCellSpacing(2);
		rpsSourceContainer.setLayout(tl);
	
		// rps sever name
		LabelField serverName = new LabelField();
		String name = UIContext.Constants.productShortNameRPS() + ":";
		serverName.setValue(name);
		TableData td = new TableData();
		td.setColspan(1);
		td.setWidth("50%");
		rpsSourceContainer.add(serverName, td);
		rpsServer = new LabelField();
		rpsServer.setWidth(MIN_FIELD_WIDTH);
		rpsServer.setEmptyText(UIContext.Constants.clickChange());
		rpsSourceContainer.add(rpsServer, td);
		
		// policy and D2d
		LabelField policyLabel = new LabelField();
		String policy = UIContext.Constants.rpsDataStore() + ":";
		policyLabel.setValue(policy);
		rpsSourceContainer.add(policyLabel, td);
		rpsDatastore = new LabelField();
		rpsDatastore.setReadOnly(true);
		rpsDatastore.setWidth(MIN_FIELD_WIDTH);
		rpsSourceContainer.add(rpsDatastore, td);
		
		LabelField d2dLabel = new LabelField();
		String d2d  = UIContext.Constants.VirtualMachineName() + ":";
		d2dLabel.setValue(d2d);
		rpsSourceContainer.add(d2dLabel, td);
		rpsSourceContainer.add(selectedD2D, td);
		sourceContainer.add(rpsSourceContainer);
		
		return sourceContainer;
	}
	
	private Button renderChangeButton(){
		Button changeBtn = new Button(UIContext.Constants.changeServer());
		changeBtn.setMinWidth(80);
		changeBtn.setAutoWidth(true);
		changeBtn.ensureDebugId("191953BA-5580-4A8F-B810-D5104306F20A");
		Utils.addToolTip(changeBtn, UIContext.Messages.rpsChangeTooltip(UIContext.productNameRPS));
		changeBtn.addListener(Events.Select, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {	
				if(rpsSettingWindow == null)
					rpsSettingWindow = new RestoreSelectRPSSettingWindow(backupToDataStore);
				
				rpsSettingWindow.pathSelection.setMode(getMode());
				
				rpsSettingWindow.addWindowListener(new WindowListener(){
					@Override
					public void windowHide(WindowEvent we) {
						if(we.getButtonClicked().getItemId() == MessageBox.OK){
							backupToDataStore = rpsSettingWindow.isBackupToDataStore();
							BackupD2DModel d2d = rpsSettingWindow.getSelectedD2D();
							String remotePath = Utils.trimUselessSplashFromRemotePath(d2d.getDestination());
							if (!d2d.getDestination().equals(remotePath)) {
								d2d.setDestination(remotePath);
							}							
							destination = d2d.getDestination();
							userName = d2d.getDesUsername();
							password = d2d.getDesPassword();
							if(RestoreContext.getBackupModel() != null)
							{
								RestoreContext.getBackupModel().setDestUserName(userName);
								RestoreContext.getBackupModel().setDestPassword(password);
							}
							
							if(backupToDataStore){
								rpsServer.setValue(rpsSettingWindow.getRPSHostName());
								rpsDatastore.setValue(rpsSettingWindow.getSelectPolicy().getDataStoreDisplayName());
								selectedD2D.setValue(d2d.getHostName());
								rpsDestSettings = rpsSettingWindow.saveData();
								rpsDestSettings.setRpsPolicyUUID(d2d.getRpsPolicyUUID());
								setWidgetsVisible(true);
								
							}else {
								rpsDestSettings = null;
								sharedFolder.setValue(d2d.getDestination());
								setWidgetsVisible(false);
							}
							if(currentD2D != null && currentD2D.getDestination() != null
									&& !currentD2D.getDestination().equals(d2d.getDestination())
									|| (currentD2D == null || currentD2D.getDestination() == null))
								changeListener.handleEvent(null);
							currentD2D = d2d;
						}
					}

					@Override
					public void windowShow(WindowEvent we) {
						rpsSettingWindow.refreshData(rpsDestSettings, currentD2D, rpsPolicyList);
					}
					
				});
				rpsSettingWindow.show();				
			}
			
		});
		return changeBtn;
	}
	
	protected void getDefaultValue() {
		service.getBackupConfiguration(new AsyncCallback<BackupSettingsModel>() {
			@Override
			public void onFailure(Throwable caught) {
				// Failed to get a proper value
				// the restore source is initialized	
				listener.onDefaultSourceInitialized(false);
				setWidgetsVisible(false);
			}

			@Override
			public void onSuccess(BackupSettingsModel result) {
				RestoreContext.setBackupModel(result);
				if (result != null && result.getDestination() != null) {
					
					onGetDefaultValueSucceed(result.getDestination(), result.getDestUserName(),
							result.getDestPassword(), result.isBackupToRps(), 
							result.rpsDestSettings, UIContext.serverVersionInfo.getLocalHostName());					
				} else {					
					// the restore source is initialized
					listener.onDefaultSourceInitialized(false);
					setWidgetsVisible(false);
				}
			}
		});
	}
	
	protected void onGetDefaultValueSucceed(String dest, String user, String pwd, 
			Boolean toRPS, final BackupRPSDestSettingsModel rpsSetting, String hostName){
		destination = dest;
		userName = user;
		password = pwd;

		currentD2D = new BackupD2DModel();
		currentD2D.setDesPassword(password);
		currentD2D.setDestination(destination);
		currentD2D.setDesUsername(userName);
		currentD2D.setHostName(hostName);
		selectedD2D.setValue(currentD2D.getHostName());
		
		RestoreContext.getBackupModel().setDestUserName(userName);
		RestoreContext.getBackupModel().setDestPassword(password);
		
		if(toRPS != null && toRPS) {
			rpsDestSettings = rpsSetting;
			rpsServer.setValue(rpsDestSettings.rpsHost.getHostName());
			rpsDatastore.setValue(rpsDestSettings.getRPSDataStoreName());
			currentD2D.setRpsPolicyUUID(rpsDestSettings.getRpsPolicyUUID());
			setWidgetsVisible(true);
			AsyncCallback<Long> callback = createDataStoreStatusCallback();
			checkDataStoreStatus(rpsSetting.rpsHost, rpsSetting.getRPSDataStoreUUID(), callback);
		}else{
			listener.onDefaultSourceInitialized(true);
			setWidgetsVisible(false);
		}
	}
	
	public static final int DATASTORE_RUNNING = 3;
	public static final int DATASTORE_ABNORMAL_RESTORE_ONLY = 6;
	
	protected AsyncCallback<Long> createDataStoreStatusCallback(){
		return new BaseAsyncCallback<Long>(){
			
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				rpsDestSettings = null;
				currentD2D = null;
			}

			@Override
			public void onSuccess(Long result) {
				if(result == DATASTORE_RUNNING
						|| result == DATASTORE_ABNORMAL_RESTORE_ONLY)
					listener.onDefaultSourceInitialized(true);
				else{
					rpsDestSettings = null;
					currentD2D = null;
					Utils.popupError(UIContext.Constants.datastoreStatusWrong(), UIContext.productNameD2D);
				}
			}
		};
	}
	protected void checkDataStoreStatus(RpsHostModel host, String dataStoreUUID, AsyncCallback<Long> callback){
		configRPSInD2DService.getDataStoreStatus(host, dataStoreUUID, callback);
	}
	
	protected void setWidgetsVisible(boolean backupToDataStore){
		this.backupToDataStore = backupToDataStore;
		sharedFolder.setVisible(!backupToDataStore);
		sharedFolder.setValue(destination);
		rpsSourceContainer.setVisible(backupToDataStore);
	}
	
	public void setSourceListener(IRestoreSourceListener listener) {
		this.listener = listener;
	}
	
	public String getBackupDestination() {
		return destination;
	}
	
	public void setBackupDestination(String dest) {
		destination = dest;
	}
	public String getDestUserName() {
		return userName;
	}
	
	public void setDestUserName(String username) {
		userName = username;
	}
	
	
	public String getDestPassword() {
		return password;
	}
	
	public void setDestPassword(String pwd) {
		password = pwd;
	}
	
	public RpsHostModel getRpsHost() {
		if(rpsDestSettings != null){
			return rpsDestSettings.rpsHost;
		}else {
			return null;
		}
	}
	
	public String getRpsPolicy() {
		if(rpsDestSettings != null){
			return rpsDestSettings.getRpsPolicyUUID();
		}else {
			return null;
		}
	}
	
	public String getDatastoreDisplayName(){
		if(rpsDestSettings != null){
			return rpsDestSettings.getRPSDataStoreName();
		}else {
			return null;
		}
	}

	public void setPathChangeListener(Listener<BaseEvent> changeListener) {
		this.changeListener = changeListener;
	}

	public String getRpsDataStore() {
		if(rpsDestSettings != null){
			return rpsDestSettings.getRPSDataStoreUUID();
		}else {
			return null;
		}
	}
	
	public String getRpsDataStoreDisplayName(){
		if(rpsDestSettings != null){
			return rpsDestSettings.getRPSDataStoreName();
		}else {
			return null;
		}
	}
	
	private int mode = PathSelectionPanel.RESTORE_MODE;

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;		
	}
}
