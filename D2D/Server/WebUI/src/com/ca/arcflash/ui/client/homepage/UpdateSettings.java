package com.ca.arcflash.ui.client.homepage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.BaseSimpleComboBox;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.FlashTimeField;
import com.ca.arcflash.ui.client.common.SettingsTypesForUI;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.common.icons.FlashImageBundle;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.StagingServerModel;
import com.ca.arcflash.ui.client.model.UpdateSettingsModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Time;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTip;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class UpdateSettings {
	
	public static final String ICON_SMALL_FINISH_URL	=	"images/status_small_finish.png";
	public static final String ICON_SMALL_WARNING_URL	=	"images/status_small_warning.png";
	public static final String ICON_SMALL_ERROR_URL		=	"images/status_small_error.png";
	public static final String ICON_LOADING = "images/gxt/icons/grid-loading.gif";
	public static final String ICON_SMALL_UNKNOWN_URL = "images/status_small_unknown.png";
	private Image CAServerConnectionStatusImage;
	private final CommonServiceAsync service = GWT.create(CommonService.class);
	private final LoginServiceAsync loginService = GWT.create(LoginService.class);
	
	private IUpdateSettingHost host;
	private LayoutContainer container;
	
	private PreferencesProxySettingsWindow proxySettingsDlg = new PreferencesProxySettingsWindow();
	private StagingServerWindow stagingServerWnd = null;
	private UpdateSettingsModel updateSettingsModel = null;
	
	private LabelField pagelabel;
	
	////download server
	//private RadioButton rbCAServer;
	private RadioGroup rgDownloadServerOptions;
	private Radio rbCAServer;
	private Radio rbStagingServer;
	protected Button btProxysettings;
	
	/// Test Downloads
	private Button btTestConnection;
	
	////Auto checkupdate
	private CheckBox cbAutoCheckUpdates;
	private BaseSimpleComboBox<String> cbWeekCombo;
//	private BaseSimpleComboBox<String> cbHourCombo;
//	private BaseSimpleComboBox<String> amCombo;
	private FlashTimeField timeField;
	
	ToolTipConfig tipConfig = null;
	ToolTip tip = null;
	
	private LayoutContainer lcDownloadServerContainer;
	private LayoutContainer lcTestDownloadsContainer;
	private LayoutContainer lcUpdateScheduleContainer;
	
	protected TableLayout tlUpdatesPageLayout;
	private TableLayout tlDownloadServerLayout;
	private TableLayout tlTestDownloadsLayout;
	private TableLayout tlUpdateScheduleLayout;
	
	private TableData tdPageLabelSettings;
	
	private Listener<BaseEvent> AutoUpdateSettingsListener = null;
	private WindowListener AutoUpdateWindowListener = null;
	
	//multiple staging server support - kappr01
	LayoutContainer lcStagingServersContainer;
	protected Grid<StagingServerModel> StagingServersGrid;
	private ListStore<StagingServerModel> gridStore;
	private ColumnModel StagingServersColumnsModel;
	private GridCellRenderer<StagingServerModel> StagingServerRenderer;
	private GridCellRenderer<StagingServerModel> StagingServerPortRenderer;
	private GridCellRenderer<StagingServerModel> StagingServerStatusImageRenderer;
	
/*	private final int MIN_WIDTH = 90;
	private final int MAX_WIDTH = 700;
	private final int MAX_HEIGHT = 750;*/
	
	protected Button btAddServer;
	protected Button btDeleteServer;
	protected Button btMoveUp;
	protected Button btMoveDown;
	protected Button btEditServer;
	boolean bInitialTestConnection = false;
	boolean bCAServerConnectionTested = false;
	boolean bStagingServerConnectionTested = false;
	boolean bIsReqToUpdateSummaryPanel = true;
	private static final int serverType = 0;
	private static final boolean scheduleType = true;
	private static final int scheduledDay = 1;
	private static final int scheduledHour = 3;
	private boolean isEditable = true;
	
	private boolean forEdge = false;
	
	private StagingServerModel oldstagingserver=null;
	
	//Test connection
	//private LoadingStatus testingConnections = null;
	public UpdateSettings(IUpdateSettingHost host)
	{
		this.host = host;
	}
	
	public LayoutContainer Render()
	{
		container = new LayoutContainer();
		container.setScrollMode(Scroll.NONE);
		tlUpdatesPageLayout = new TableLayout();
		tlUpdatesPageLayout.setWidth("87%");
//		tlUpdatesPageLayout.setHeight("65%");
		container.setLayout(tlUpdatesPageLayout);

		pagelabel = new LabelField();
		pagelabel.setValue(UIContext.Constants.updateSettingsHeader());
		pagelabel.addStyleName("restoreWizardTitle");
		
		tdPageLabelSettings = new TableData();
		tdPageLabelSettings.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		container.add(pagelabel,tdPageLabelSettings);
		
		defineAutoUpdateSettingsListener();
		
		
		DisclosurePanel panel = Utils.getDisclosurePanel(UIContext.Constants.preferencesDownloadServerSectionHeader());		
		panel.add(defineDownloadServerSettings());
		container.add(panel);
		
		panel = Utils.getDisclosurePanel(UIContext.Constants.preferencesTestDownloadsSectionHeader());		
		panel.add(defineTestDownloadSettings());
		container.add(panel);
		
		panel = Utils.getDisclosurePanel(UIContext.Constants.UpdateScheduleSectionHeader());		
		panel.add(defineUpdateScheduleSettings());
		container.add(panel);
		
		return container;
	}
	
	private void EnableDisableButtons()
	{
		StagingServerModel stagingServer = StagingServersGrid.getSelectionModel().getSelectedItem();
		List<StagingServerModel> stagingServerList = gridStore.getModels();
		int iSelectedIndex = stagingServerList.indexOf(stagingServer);
		
		boolean bEnableMoveUp = false;
		boolean bEnableMoveDown = false;
							
		if(iSelectedIndex != 0)
		{
			bEnableMoveUp = true;
		}
							
		if(iSelectedIndex != 4)
		{
			bEnableMoveDown = true;
		}
		
		int icount = gridStore.getCount();
		if(++iSelectedIndex == icount)
		{
			bEnableMoveDown = false;
		}
		
		if(icount == UIContext.MaxStagingServerCount)
			btAddServer.setEnabled(false);
		
		btMoveUp.setEnabled(bEnableMoveUp);
		btMoveDown.setEnabled(bEnableMoveDown);
		btDeleteServer.setEnabled(iSelectedIndex != -1 ? true : false);
		btEditServer.setEnabled(iSelectedIndex != -1 ? true : false);
		if(icount == 0)
			btEditServer.setEnabled(false);
		btTestConnection.setEnabled(iSelectedIndex != -1 ? true : false);
	}

	private LayoutContainer defineTestDownloadSettings() {
		
		lcTestDownloadsContainer = new LayoutContainer();
		lcTestDownloadsContainer.setScrollMode(Scroll.NONE);	
	
		tlTestDownloadsLayout = defineUpdatesPagesectionTableLayout(1);
		tlTestDownloadsLayout.setWidth("100%");
		lcTestDownloadsContainer.setLayout(tlTestDownloadsLayout);
		
//		LabelField lblTestDownloads = new LabelField();
//		lblTestDownloads.setText(UIContext.Constants.preferencesTestDownloadsSectionHeader());
//		lblTestDownloads.addStyleName("restoreWizardSubItem");
		
		TableData tdTestDownload = new TableData();
		tdTestDownload.setColspan(2);
		tdTestDownload.setWidth("50%");
		tdTestDownload.setHorizontalAlign(HorizontalAlignment.LEFT);
//		lcTestDownloadsContainer.add(lblTestDownloads,tdTestDownload);
		
		LabelField lblActionDescription = new LabelField();
		lblActionDescription.setValue(UIContext.Constants.testConnectionDesc());
		
		TableData tdTestDownloadDesc = new TableData();
		tdTestDownloadDesc.setColspan(1);
		tdTestDownloadDesc.setWidth("90%");
		tdTestDownloadDesc.setHorizontalAlign(HorizontalAlignment.LEFT);
		lcTestDownloadsContainer.add(lblActionDescription,tdTestDownloadDesc);
		
		
		btTestConnection = new Button(){

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
		btTestConnection.ensureDebugId("4a48938c-39da-4eeb-9f7d-620b135c04b0");	
		btTestConnection.setText(UIContext.Constants.testConnectionButtonLabel());
		Utils.addToolTip(btTestConnection, UIContext.Constants.testConnectionButtonTooltip());
		btTestConnection.addListener(Events.Select, AutoUpdateSettingsListener);
		btTestConnection.setEnabled(false);
		TableData tdTestConnection = new TableData();
		tdTestConnection.setWidth("35%");
		tdTestConnection.setHorizontalAlign(HorizontalAlignment.LEFT);
		btTestConnection.setAutoWidth(true);
		lcTestDownloadsContainer.add(btTestConnection,tdTestConnection);
				
//		container.add(lcTestDownloadsContainer);

		return lcTestDownloadsContainer;
		
	}

	private TableLayout defineUpdatesPagesectionTableLayout(int in_iColumns)
	{
		TableLayout tlUpdatePageSectionLayout = new TableLayout();
		tlUpdatePageSectionLayout.setColumns(in_iColumns);
		return tlUpdatePageSectionLayout;
	}
	
	private void defineAutoUpdateSettingsListener()
	{
		AutoUpdateSettingsListener = new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent SelfUpdateEvent) {
				MessageBox msgError = new MessageBox();
				msgError.setIcon(MessageBox.ERROR);
				msgError.setTitleHtml(UIContext.Messages.messageBoxTitleError(getProductNameString()));
				msgError.setModal(true);
				
				if(SelfUpdateEvent.getSource() == rgDownloadServerOptions)
				{
					if(rbCAServer.getValue())
					{
						btProxysettings.setEnabled(true);
						CAServerConnectionStatusImage.setVisible(true);
						btTestConnection.setEnabled(true);
						lcStagingServersContainer.hide();
						if(bCAServerConnectionTested == false && bInitialTestConnection ==true)
						{
							CAServerConnectionStatusImage.setUrl(ICON_SMALL_UNKNOWN_URL);
							/*On Selection of " CA Server" radio Button do Test Connection, if CA Server is not already Tested */
							bIsReqToUpdateSummaryPanel = true;
							//TestDownloadServerConnections(false);
							TestBIDownloadServerConnections(false);
							bCAServerConnectionTested = true;
						}
					}
					else if(rbStagingServer.getValue())
					{
						btProxysettings.setEnabled(rbCAServer.getValue());
						CAServerConnectionStatusImage.setVisible(false);
						lcStagingServersContainer.show();
						btTestConnection.setEnabled(StagingServersGrid.getSelectionModel().getSelectedItem() != null ? true : false);
						if(bStagingServerConnectionTested == false && bInitialTestConnection == true)
						{
							/*On Selection of " Staging Server" radio Button do Test Connection, if Staging Server is not already Tested*/
							bIsReqToUpdateSummaryPanel = true;
							//TestDownloadServerConnections(false);
							TestBIDownloadServerConnections(false);
							bStagingServerConnectionTested = true;
						}
					}
				}
				else if(SelfUpdateEvent.getSource() == btProxysettings)
				{
					proxySettingsDlg.setModal(true);
					proxySettingsDlg.RefreshData(updateSettingsModel != null ? updateSettingsModel.getproxySettings() : null);	
					
					proxySettingsDlg.addWindowListener(AutoUpdateWindowListener);
					proxySettingsDlg.show();
					return;
				}
				else if(SelfUpdateEvent.getSource() == cbAutoCheckUpdates)
				{
					cbWeekCombo.setEnabled(cbAutoCheckUpdates.getValue());
					timeField.setEnabled(cbAutoCheckUpdates.getValue());
				}
				else if(SelfUpdateEvent.getSource() == btAddServer)
				{
					if(gridStore.getCount() != UIContext.MaxStagingServerCount)
					{
						stagingServerWnd = new StagingServerWindow();
						stagingServerWnd.setModal(true);
						stagingServerWnd.addWindowListener(AutoUpdateWindowListener);
						stagingServerWnd.show();
					}
					else
					{
						msgError.setMessage(UIContext.Messages.MaximumStagingServerMessage());
						Utils.setMessageBoxDebugId(msgError);
						msgError.show();
						btAddServer.setEnabled(false);
						return;
					}
				}
				else if(SelfUpdateEvent.getSource() == btEditServer)
				{
					List<StagingServerModel> stagingServers = null;
					stagingServers = StagingServersGrid.getSelectionModel().getSelectedItems();
					if(stagingServers == null)
					{
						//None Select
						msgError.setMessage(UIContext.Messages.SelectStagingServerToEdit());
						Utils.setMessageBoxDebugId(msgError);
						msgError.show();
						return;
					}
					if(stagingServers.size()>1)
					{
						//Multi Select
						msgError.setMessage(UIContext.Messages.SelectOneStagingServerToEdit());
						Utils.setMessageBoxDebugId(msgError);
						msgError.show();
						return;
					}
					for(StagingServerModel stagingserver : stagingServers)
					{
						oldstagingserver=stagingserver;
						stagingServerWnd = new StagingServerWindow(oldstagingserver);
						stagingServerWnd.setModal(true);
						stagingServerWnd.addWindowListener(AutoUpdateWindowListener);
						stagingServerWnd.show();
					}
					
					
					
					
					StagingServersGrid.reconfigure(gridStore, StagingServersColumnsModel);
				
				}
				else if(SelfUpdateEvent.getSource() == btMoveUp)
				{
					StagingServerModel stagingServer = StagingServersGrid.getSelectionModel().getSelectedItem();
					List<StagingServerModel> stagingServerList = gridStore.getModels();
					int iSelectedIndex = stagingServerList.indexOf(stagingServer);
					stagingServerList.remove(iSelectedIndex);
					stagingServerList.add(--iSelectedIndex, stagingServer);
					gridStore.removeAll();
					gridStore.add(stagingServerList);
					StagingServersGrid.reconfigure(gridStore, StagingServersColumnsModel);
					
					if(iSelectedIndex != 0)
					{
						StagingServersGrid.getSelectionModel().select(iSelectedIndex, true);
						btMoveDown.setEnabled(true);
					}
					else
					{
						btMoveUp.setEnabled(false);
						btMoveDown.setEnabled(false);
						btDeleteServer.setEnabled(false);
						btTestConnection.setEnabled(false);
					}
				}
				
				
				else if(SelfUpdateEvent.getSource() == btMoveDown)
				{
					StagingServerModel stagingServer = StagingServersGrid.getSelectionModel().getSelectedItem();
					List<StagingServerModel> stagingServerList = gridStore.getModels();
					int iSelectedIndex = stagingServerList.indexOf(stagingServer);
					stagingServerList.remove(iSelectedIndex);
					stagingServerList.add(++iSelectedIndex, stagingServer);
					gridStore.removeAll();
					gridStore.add(stagingServerList);
					StagingServersGrid.reconfigure(gridStore, StagingServersColumnsModel);
					if(iSelectedIndex != (gridStore.getCount()-1))
					{
						StagingServersGrid.getSelectionModel().select(iSelectedIndex, true);
						btMoveUp.setEnabled(true);
					}
					else
					{
						btMoveUp.setEnabled(false);
						btMoveDown.setEnabled(false);
						btDeleteServer.setEnabled(false);
						btTestConnection.setEnabled(false);
					}
				}
				else if(SelfUpdateEvent.getSource() == btDeleteServer)
				{
					List<StagingServerModel> stagingServers = null;
					stagingServers = StagingServersGrid.getSelectionModel().getSelectedItems();
					if(stagingServers == null)
					{
						msgError.setMessage(UIContext.Messages.SelectStagingServerToDelete());
						Utils.setMessageBoxDebugId(msgError);
						msgError.show();
						return;
					}
					for(StagingServerModel stagingserver : stagingServers)
					{
						gridStore.remove(stagingserver);
					}
					
					StagingServersGrid.reconfigure(gridStore, StagingServersColumnsModel);
					if(gridStore.getCount() <UIContext.MaxStagingServerCount)
					{
						btAddServer.setEnabled(true);
					}
					
					btDeleteServer.setEnabled(false);
					btMoveUp.setEnabled(false);
					btMoveDown.setEnabled(false);
					btTestConnection.setEnabled(false);
					//}
				}
				else if((SelfUpdateEvent.getSource() == StagingServersGrid) /*&& (SelfUpdateEvent.getType() == Events.RowClick)*/)//when any row is selected in staging servers grid, this will be triggered
				{
					EnableDisableButtons();
				}
				else if(SelfUpdateEvent.getSource() == btTestConnection)
				{
					if(rbCAServer.getValue())
					{
						CAServerConnectionStatusImage.setUrl(ICON_LOADING);
					}
					if(proxySettingsDlg != null && proxySettingsDlg.getProxySettingsModel() != null && proxySettingsDlg.getProxySettingsModel().getUseProxy() == false && SummaryPanel.iDownloadServerType == 0)
					{
						/*Only In case of CA Server and USE IE settings, The changes made in IE should be Reflected in the summary Panel  */
						bIsReqToUpdateSummaryPanel = true;
					}
					else
					{
						bIsReqToUpdateSummaryPanel = false;
					}
					
					//TestDownloadServerConnections();
					TestBIDownloadServerConnections();
				}
				
				if((SelfUpdateEvent.getSource() == StagingServersGrid) && (SelfUpdateEvent.getType() == Events.CellClick))
				{
					List<StagingServerModel> SelecteStagingServersList = StagingServersGrid.getSelectionModel().getSelectedItems();
					int iSelectedServersCount = SelecteStagingServersList.size();
					if(iSelectedServersCount > 1)
					{
						btMoveUp.setEnabled(false);
						btMoveDown.setEnabled(false);
					}
					
					if(iSelectedServersCount == 0)
					{
						btMoveUp.setEnabled(false);
						btMoveDown.setEnabled(false);
						btDeleteServer.setEnabled(false);
					}	
				}
			}

			
		};
		
		AutoUpdateWindowListener = new WindowListener()
		{
			public void windowHide(WindowEvent windEvent) {
				if(windEvent.getSource() == proxySettingsDlg)
				{
					if (proxySettingsDlg.getButtonClicked().compareToIgnoreCase(Dialog.OK) == 0) 
					{
						if(updateSettingsModel == null)
						{
							updateSettingsModel = new UpdateSettingsModel();
						}
						updateSettingsModel.setproxySettings(proxySettingsDlg.getProxySettingsModel());
					}
				}
				else if(windEvent.getSource() == stagingServerWnd)
				{
					String strButtonClicked=stagingServerWnd.getButtonClicked();
					strButtonClicked=strButtonClicked==null?Dialog.CANCEL:strButtonClicked;
					if (strButtonClicked.compareToIgnoreCase(Dialog.OK) == 0) 
					{
						StagingServerModel stagingServer = null;
						stagingServer = stagingServerWnd.getselectedStagingServer();
						if(stagingServer != null)
						{
							//if(!IsThisStagingServerAlreadyAdded(stagingServer.getStagingServer()))
							//{
								stagingServer.setStagingServerId(-1);
								
								if(stagingServerWnd.editmode&&oldstagingserver!=null)gridStore.remove(oldstagingserver);
								gridStore.add(stagingServer);
								StagingServersGrid.reconfigure(gridStore, StagingServersColumnsModel);
							//}
							
							if(gridStore.getCount() == UIContext.MaxStagingServerCount)
							{
								btAddServer.setEnabled(false);
							}
							btEditServer.setEnabled(false);
							btDeleteServer.setEnabled(false);
							btMoveUp.setEnabled(false);
							btMoveDown.setEnabled(false);
						}
					}
				}
				return;
			}
		};
		return;
	}
	
	private boolean IsThisStagingServerAlreadyAdded(
			String stagingServer,int serverport) {
		boolean bAlreadyAdded = false;
		
		List<StagingServerModel> stagingServersList = gridStore.getModels();
		int iStagingServersCount = stagingServersList != null ? stagingServersList.size() : 0;
		for(int iIndex = 0;iIndex < iStagingServersCount; iIndex++)
		{
			if(stagingServersList.get(iIndex).getStagingServer().compareToIgnoreCase(stagingServer) == 0&&stagingServersList.get(iIndex).getStagingServerPort()==serverport)
			{
				bAlreadyAdded = true;
				MessageBox msgError = new MessageBox();
				msgError.setIcon(MessageBox.ERROR);
				msgError.setTitleHtml(UIContext.Messages.messageBoxTitleError(getProductNameString()));
				msgError.setModal(true);
				msgError.setMessage(UIContext.Messages.SelectedServerAlreadyAddedMessage());
				Utils.setMessageBoxDebugId(msgError);
				msgError.show();
				break;
			}
		}
		
		return bAlreadyAdded;
	}
	
	private void refreshDownloadServerSettings(
			UpdateSettingsModel result) {
		
		SummaryPanel.bDownloadServerAvailable = false;
		SummaryPanel.bDownloadBIServerAvailable = false;
		switch(result.getDownloadServerType())
		{
		case 0://CA server
			//MessageBox msgError = new MessageBox();
			rbCAServer.setValue(true);
			if (btProxysettings.isEnabled()) {
				CAServerConnectionStatusImage.setVisible(true);
			}
			//CAServerConnectionStatusImagePrototype =  IconHelper.create(ICON_SMALL_FINISH_URL, 16,16);
			//CAServerConnectionStatusImage = CAServerConnectionStatusImagePrototype.createImage();
			if(result.getCAServerStatus() == 1)
			{
				CAServerConnectionStatusImage.setUrl(ICON_SMALL_FINISH_URL);
				CAServerConnectionStatusImage.setTitle(UIContext.Messages.ConnectionWithCAServeAvailable(UIContext.companyName));
				/*msgError.setIcon(MessageBox.INFO);
				strConnectionMessage = UIContext.Messages.ConnectionWithCAServeAvailable();
				msgError.setTitle(UIContext.Constants.messageBoxTitleInformation());*/
				//CAServerConnectionstatus = 1; // CA Server available
				SummaryPanel.bDownloadServerAvailable = true;
				SummaryPanel.bDownloadBIServerAvailable = true;
			}
			else
			{
				CAServerConnectionStatusImage.setUrl(ICON_SMALL_ERROR_URL);
				CAServerConnectionStatusImage.setTitle(UIContext.Messages.ConnectionWithCAServerIsNotAvailable(UIContext.companyName));
/*				msgError.setIcon(MessageBox.ERROR);
				strConnectionMessage = UIContext.Messages.ConnectionWithCAServerIsNotAvailable();//show ca server connectio nstatus here
				msgError.setTitle(UIContext.Messages.messageBoxTitleError(getProductNameString()));*/
				//CAServerConnectionstatus = -1; // CA Server not available
                SummaryPanel.bDownloadServerAvailable = false;
                SummaryPanel.bDownloadBIServerAvailable = false;
				
			}
			if((SummaryPanel.iDownloadServerType == 0) && (bIsReqToUpdateSummaryPanel == true))
			{
				/*Updating Auto Update Status In Summary Panel on Selecting the CA Server, Only if Home Page has shown CA Server Status */
				loginService.isUsingEdgePolicySettings(
						SettingsTypesForUI.BackupSettings, new AsyncCallback<Boolean>()
					{
						@Override
						public void onFailure( Throwable caught )
						{
							SummaryPanel.UpdateAutoUpdateStatus();
							SummaryPanel.updatePatchInfo();
							
						}

						@Override
						public void onSuccess( Boolean result )
						{
							boolean isEditable = !result;
							if(isEditable) {
								SummaryPanel.UpdateAutoUpdateStatus();
								SummaryPanel.updatePatchInfo();
								
							}
						}
					});
				

			}
			//rbCAServer.setToolTip(strConnectionMessage);//show ca server connectio nstatus here
			
			//msgError.setModal(true);
			//msgError.setMessage(strConnectionMessage);
			//msgError.show();
			rbStagingServer.setValue(false);
			break;
		case 1://staging server
			CAServerConnectionStatusImage.setVisible(false);
			rbCAServer.setValue(false);
			rbStagingServer.setValue(true);

			lcStagingServersContainer.show();
			break;
		}
		
		int iStagingServers = result.getStagingServers() != null ? result.getStagingServers().length : 0;
		if(iStagingServers != 0)
		{
			StagingServerModel[] StagingServers = result.getStagingServers(); 

			List<StagingServerModel> list = gridStore.getModels();

			for(StagingServerModel stagingServer : StagingServers)
			{
				for(int iIndex = 0;iIndex < list.size();iIndex++)
				{
					StagingServerModel serverFromList = list.get(iIndex);
					
					if(serverFromList.getStagingServer().compareToIgnoreCase(stagingServer.getStagingServer()) == 0&&serverFromList.getStagingServerPort()==stagingServer.getStagingServerPort())
					{
						list.get(iIndex).setStagingServerStatus(stagingServer.getStagingServerStatus());
						if(stagingServer.getStagingServerStatus()== 1)
						{
							SummaryPanel.bDownloadServerAvailable = true;
							SummaryPanel.bDownloadBIServerAvailable = true;
						}
						break;
					}
				}
			}
			if((SummaryPanel.iDownloadServerType == 1)&& (bIsReqToUpdateSummaryPanel == true))
			{
				/*Updating Auto Update Status In Summary Panel on Selecting the CA Server, Only if Home Page has shown Staging Server Status */
				loginService.isUsingEdgePolicySettings(
						SettingsTypesForUI.BackupSettings, new AsyncCallback<Boolean>()
					{
						@Override
						public void onFailure( Throwable caught )
						{
							SummaryPanel.UpdateAutoUpdateStatus();
							
						}

						@Override
						public void onSuccess( Boolean result )
						{
							boolean isEditable = !result;
							if(isEditable) {
								SummaryPanel.UpdateAutoUpdateStatus();
								
							}
						}
					});
				
			}
			gridStore.removeAll();
			gridStore.add(list);
			StagingServersGrid.reconfigure(gridStore, StagingServersColumnsModel);
			if (StagingServersGrid.getSelectionModel().getSelectedItems().size() == 0) {
				btMoveUp.setEnabled(false);
				btMoveDown.setEnabled(false);
				btDeleteServer.setEnabled(false);
			}
		}
	}
	
	private LayoutContainer defineDownloadServerSettings()
	{
		lcDownloadServerContainer = new LayoutContainer();
		lcDownloadServerContainer.setScrollMode(Scroll.NONE);
	
		tlDownloadServerLayout = defineUpdatesPagesectionTableLayout(1);
		tlDownloadServerLayout.setCellSpacing(4);
		tlDownloadServerLayout.setWidth("100%");
		tlDownloadServerLayout.setCellPadding(3);
		lcDownloadServerContainer.setLayout(tlDownloadServerLayout);
		
//		LabelField lblDownloadServer = new LabelField();
//		lblDownloadServer.setText(UIContext.Constants.preferencesDownloadServerSectionHeader());
//		lblDownloadServer.addStyleName("restoreWizardSubItem");
		
		TableData tdDownloadServer = new TableData();
		tdDownloadServer.setHorizontalAlign(HorizontalAlignment.LEFT);
//		lblDownloadServer.setWidth(250);
//		lcDownloadServerContainer.add(lblDownloadServer,tdDownloadServer);
		
		
		LabelField lblDownloadServerDesc = new LabelField();
		lblDownloadServerDesc.setValue(UIContext.Messages.preferencesDownloadServerDescription(UIContext.companyName));
		
		TableData tdDownloadServerDesc = new TableData();
		tdDownloadServerDesc.setHorizontalAlign(HorizontalAlignment.LEFT);
		lblDownloadServerDesc.setWidth(600);
		lcDownloadServerContainer.add(lblDownloadServerDesc,tdDownloadServerDesc);
		
		LayoutContainer lcCAServerContainer = new LayoutContainer();
		TableLayout tlCAServerLayout = defineUpdatesPagesectionTableLayout(3);
//		tlCAServerLayout.setCellVerticalAlign(VerticalAlignment.TOP);
		
		lcCAServerContainer.setLayout(tlCAServerLayout);
		
		rgDownloadServerOptions = new RadioGroup();
		rbCAServer = new Radio();
		rbCAServer.ensureDebugId("822750c5-b39b-48f1-bc02-a5e7290bc03b");
		rbCAServer.setStyleName("x-form-field");
		rbCAServer.setBoxLabel(UIContext.Messages.preferencesDownloadFromCAServerLabel(UIContext.companyName));
		rbCAServer.setValue(false);
		rbCAServer.setWidth(250);
		rbCAServer.setHeight("10");
		rgDownloadServerOptions.add(rbCAServer);
		
		rbStagingServer = new Radio();
		rbStagingServer.ensureDebugId("099f6b83-9598-4f0e-9e7c-4a968f0d90f6");
		rbStagingServer.setStyleName("x-form-field");
		rbStagingServer.setBoxLabel(UIContext.Constants.DownloadFromStagingServerLabel());
		rbStagingServer.setValue(false);
		rbStagingServer.setWidth(280);
		rgDownloadServerOptions.add(rbStagingServer);
		rgDownloadServerOptions.addListener(Events.Change, AutoUpdateSettingsListener);
		
		TableData tdCAServer = new TableData();
		tdCAServer.setHorizontalAlign(HorizontalAlignment.LEFT);
		lcCAServerContainer.add(rbCAServer,tdCAServer);		
		
		btProxysettings = new Button(UIContext.Constants.ProxySettingsButtonLabel())
		{
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
		btProxysettings.ensureDebugId("d44f9632-2d8d-47bf-8334-031ff4e442aa");
		btProxysettings.setEnabled(false);
		btProxysettings.setMinWidth(100);
		btProxysettings.setAutoWidth(true);
		
		
		btProxysettings.addListener(Events.Select, AutoUpdateSettingsListener);
		
		TableData tdProxySettings = new TableData();
		tdProxySettings.setHorizontalAlign(HorizontalAlignment.LEFT);
		lcCAServerContainer.add(btProxysettings,tdProxySettings);
		
		/*TableData tdTestingConnection = new TableData();
		tdTestingConnection.setHorizontalAlign(HorizontalAlignment.CENTER);
		
		testingConnections = new LoadingStatus();
		testingConnections.hide();
		testingConnections.setLoadingMsg("Testing connection...");
		//testingConnections.setStyleName("TestingConnectionStatus");
		lcCAServerContainer.add(testingConnections,tdTestingConnection);
		//lcDownloadServerContainer.add(lcCAServerContainer);
*/	
		TableData tdCAServerConnectionStatusImage = new TableData();
		tdCAServerConnectionStatusImage.setPadding(4);
		tdCAServerConnectionStatusImage.setHorizontalAlign(HorizontalAlignment.RIGHT);
	    AbstractImagePrototype totalStatusImagePrototype = IconHelper.create(ICON_SMALL_UNKNOWN_URL, 16,16);
	    CAServerConnectionStatusImage = totalStatusImagePrototype.createImage();
	    CAServerConnectionStatusImage.setVisible(false);
		//CAServerConnectionStatusImage.setUrl(ICON_SMALL_WARNING_URL);
		lcCAServerContainer.add(CAServerConnectionStatusImage,tdCAServerConnectionStatusImage);
		
		lcDownloadServerContainer.add(lcCAServerContainer);
		
		//staging server button and label only
		LayoutContainer lcStagingServerLabelOnly = new LayoutContainer();
		TableLayout tlStagingServerLabelOnlyLayout = defineUpdatesPagesectionTableLayout(1);
		tlStagingServerLabelOnlyLayout.setCellVerticalAlign(VerticalAlignment.TOP);
		tlStagingServerLabelOnlyLayout.setWidth("100%");
		lcStagingServerLabelOnly.setLayout(tlStagingServerLabelOnlyLayout);
		
		TableData tdStagingServerLabelOnly = new TableData();
		tdStagingServerLabelOnly.setHorizontalAlign(HorizontalAlignment.LEFT);
		tdStagingServerLabelOnly.setVerticalAlign(VerticalAlignment.TOP);
		lcStagingServerLabelOnly.add(rbStagingServer,tdStagingServerLabelOnly);
		lcDownloadServerContainer.add(lcStagingServerLabelOnly);
		
		//staging server
		LayoutContainer lcStagingServerContainer = new LayoutContainer();
		TableLayout tlStagingServerLayout = defineUpdatesPagesectionTableLayout(1);
		tlStagingServerLayout.setCellVerticalAlign(VerticalAlignment.TOP);
		tlStagingServerLayout.setWidth("100%");
		//tlStagingServerLayout.setCellSpacing(2);
		lcStagingServerContainer.setLayout(tlStagingServerLayout);
		
		/*TableData tdStagingServerLabel = new TableData();
		//tdStagingServerLabel.setWidth("35%");
		tdStagingServerLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		tdStagingServerLabel.setVerticalAlign(VerticalAlignment.TOP);
		
		lcStagingServerContainer.add(rbStagingServer,tdStagingServerLabel);*/
		
		//Adding grid for multiple stagig server support
		gridStore = new ListStore<StagingServerModel>();
		StagingServerRenderer = new GridCellRenderer<StagingServerModel>() {

			@Override
			public Object render(StagingServerModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<StagingServerModel> store,
					Grid<StagingServerModel> grid) {
				LayoutContainer lc = new LayoutContainer();
				TableLayout layout = new TableLayout();
				layout.setColumns(1);
				layout.setCellPadding(4);
				//layout.setCellHorizontalAlign(HorizontalAlignment.CENTER);
				//layout.setCellVerticalAlign(VerticalAlignment.MIDDLE);
				lc.setLayout(layout);
				Label lf = new Label();
				lf.setText(model.getStagingServer());
				fileLinkMap.put(lf, model);
				lf.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						Object src = event.getSource();
						if (src instanceof Label) {
							
								StagingServerModel node = fileLinkMap.get(src);
								if (node != null) {
									StagingServersGrid.getSelectionModel().select(node, false);
									EnableDisableButtons();
								}
							
						}
					}
				});
				lf.setStyleName("popupFileText");
				
				
				
				lc.add(lf);
				return lc;
				//return model.getStagingServer();
			}
			HashMap<Widget, StagingServerModel> fileLinkMap = new HashMap<Widget, StagingServerModel>();
		};
		
		StagingServerPortRenderer = new GridCellRenderer<StagingServerModel>() {

			@Override
			public Object render(StagingServerModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<StagingServerModel> store,
					Grid<StagingServerModel> grid) {
				
				LayoutContainer lc = new LayoutContainer();
				TableLayout layout = new TableLayout();
				layout.setColumns(1);
				//layout.setCellHorizontalAlign(HorizontalAlignment.CENTER);
				//layout.setCellVerticalAlign(VerticalAlignment.MIDDLE);
				lc.setLayout(layout);
				layout.setCellPadding(4);
				Label nf = new Label();
				nf.setText(model.getStagingServerPort()+"");
				fileLinkMap.put(nf, model);
				nf.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						Object src = event.getSource();
						if (src instanceof Label) {
							
								StagingServerModel node = fileLinkMap.get(src);
								if (node != null) {
									StagingServersGrid.getSelectionModel().select(node, false);
									EnableDisableButtons();
								}
							
						}
					}
				});
				nf.setStyleName("popupFileText");
				
				
				lc.add(nf);
				return lc;
			}
			HashMap<Widget, StagingServerModel> fileLinkMap = new HashMap<Widget, StagingServerModel>();
		};
		
		StagingServerStatusImageRenderer = new GridCellRenderer<StagingServerModel>() {

			@Override
			public Object render(StagingServerModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<StagingServerModel> store,
					Grid<StagingServerModel> grid) {			
				
				Image connectionStatusImage = new Image();
			    //AbstractImagePrototype connectionlStatusImagePrototype = IconHelper.create(ICON_SMALL_WARNING_URL, 16,16);
			    //connectionStatusImage = connectionlStatusImagePrototype.createImage();
				
				FlashImageBundle IconBundle = GWT.create(FlashImageBundle.class);
				connectionStatusImage = AbstractImagePrototype.create(IconBundle.status_small_warning()).createImage();
				String strConnectionStatus = "";
				LayoutContainer lc = new LayoutContainer();
				
				TableLayout layout = new TableLayout();
				layout.setColumns(2);
				layout.setCellPadding(4);
				lc.setLayout(layout);
				Label lf = new Label();

				switch(model.getStagingServerStatus())
				{
				case -1:
					//connectionStatusImage.setUrl(ICON_SMALL_WARNING_URL);
					connectionStatusImage = AbstractImagePrototype.create(IconBundle.status_small_warning()).createImage();
					connectionStatusImage.setTitle(UIContext.Constants.DownloadServerConnectionStatusNotKnown());
					strConnectionStatus = " " + UIContext.Constants.DownloadServerConnectionStatusNotKnown();
					break;
				case 0:
					connectionStatusImage = AbstractImagePrototype.create(IconBundle.status_small_error()).createImage();
					strConnectionStatus = " " + UIContext.Constants.DownloadServerConnectionStatusNotAvailable();
					//connectionStatusImage.setUrl(ICON_SMALL_ERROR_URL);
					connectionStatusImage.setTitle(UIContext.Constants.DownloadServerConnectionStatusNotAvailable());
					break;
				case 1:
				case 21:
					connectionStatusImage = AbstractImagePrototype.create(IconBundle.status_small_finish()).createImage();
					strConnectionStatus = " " + UIContext.Constants.DownloadServerConnectionStatusAvailable();
					//connectionStatusImage.setUrl(ICON_SMALL_FINISH_URL);
					connectionStatusImage.setTitle(UIContext.Constants.DownloadServerConnectionStatusAvailable());
					break;
				}



				fileMap.put(connectionStatusImage,model);
				connectionStatusImage.addClickListener(new ClickListener() {

					@Override
					public void onClick(Widget sender) {


						StagingServerModel node = fileMap.get(sender);
						if (node != null) {
							//highlightRow(node);
							//if (treeNode.getType() == CatalogModelType.File)
							StagingServersGrid.getSelectionModel().select(node, false);
							EnableDisableButtons();
						}


					}
				});
				lc.add(connectionStatusImage);


				lf.setText(strConnectionStatus);
				fileLinkMap.put(lf, model);
				lf.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						Object src = event.getSource();
						if (src instanceof Label) {
							
								StagingServerModel node = fileLinkMap.get(src);
								if (node != null) {
									StagingServersGrid.getSelectionModel().select(node, false);
									EnableDisableButtons();
								}
							
						}
					}
				});
				lf.setStyleName("popupFileText");
				lc.add(lf);

				return lc;
				}
			HashMap<Widget, StagingServerModel> fileMap = new HashMap<Widget, StagingServerModel>();
			HashMap<Widget, StagingServerModel> fileLinkMap = new HashMap<Widget, StagingServerModel>();
			}; 
			

			List<ColumnConfig> columnConfigs = new ArrayList<ColumnConfig>();
			columnConfigs.add(Utils.createColumnConfig("Staging Server", UIContext.Constants.trustedHostWindowColumnName(), 140,StagingServerRenderer));
			columnConfigs.add(Utils.createColumnConfig("port", UIContext.Constants.settingsMailServerPort(), 70,StagingServerPortRenderer));
			columnConfigs.add(Utils.createColumnConfig("Status", UIContext.Constants.ConnectionStatusColumn(), 150,StagingServerStatusImageRenderer));

			StagingServersColumnsModel = new ColumnModel(columnConfigs);

			StagingServersGrid = new Grid<StagingServerModel>(gridStore, StagingServersColumnsModel);
			//StagingServersGrid.setLoadMask(true);
			//StagingServersGrid.mask(UIContext.Constants.loadingIndicatorText());
			StagingServersGrid.setHeight(170);
			StagingServersGrid.setWidth(360);
			//StagingServersGrid.unmask();
			StagingServersGrid.setAutoHeight(false);
			StagingServersGrid.setTrackMouseOver(true);
			StagingServersGrid.getSelectionModel().setSelectionMode(SelectionMode.MULTI);
			StagingServersGrid.setBorders(true);
			StagingServersGrid.addListener(Events.RowClick, AutoUpdateSettingsListener);
			StagingServersGrid.addListener(Events.CellClick, AutoUpdateSettingsListener);

/*		TableData tdArchiveFilter = new TableData();
		tdArchiveFilter.setHorizontalAlign(HorizontalAlignment.LEFT);*/
		
		lcStagingServersContainer = new LayoutContainer();
		TableLayout tlServersGrid = new TableLayout();
		tlServersGrid.setColumns(2);
		//tlServersGrid.setCellVerticalAlign(VerticalAlignment.MIDDLE);
		tlServersGrid.setCellHorizontalAlign(HorizontalAlignment.CENTER);
		lcStagingServersContainer.setLayout(tlServersGrid);
		lcStagingServersContainer.setHeight(170);
				
		TableData tdServersGrid = new TableData();
		tdServersGrid.setHorizontalAlign(HorizontalAlignment.LEFT);
		lcStagingServersContainer.add(StagingServersGrid,tdServersGrid);
		
		//Adding the required buttons to control the filters grid.
			
		LayoutContainer lcButtonsContainer = new LayoutContainer();
		TableLayout tlButtonsContainer = new TableLayout();
		tlButtonsContainer.setColumns(1);
		tlButtonsContainer.setCellSpacing(10);
		lcButtonsContainer.setLayout(tlButtonsContainer);
		lcButtonsContainer.setHeight(160);
		
		lcButtonsContainer.setLayout(tlButtonsContainer);  
		
		
		//TableLayout tlButtonsContainer = new TableLayout();
		/*VBoxLayout layout = new VBoxLayout();  
        layout.setPadding(new Padding(5));  
        layout.setVBoxLayoutAlign(VBoxLayoutAlign.STRETCHMAX);  */
		//tlButtonsContainer.setColumns(1);
		//tlButtonsContainer.setCellSpacing(5);
		//lcButtonsContainer.setLayout(tlButtonsContainer);
//		lcButtonsContainer.setHeight(120);
//		lcButtonsContainer.setWidth("100%");
		
		btAddServer = new Button(UIContext.Constants.ADDStagingServerLabel())
		{
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
		
		//btAddServer.setWidth(70);
		//btAddServer.setWidth("100%");
		//TableData tdAddFilter = new TableData();
		//tdAddFilter.setHorizontalAlign(HorizontalAlignment.RIGHT);
		//tdAddFilter.setWidth("30%");
		//btAddServer.addStyleName("buttnwidth");
		btAddServer.ensureDebugId("f8d8877c-fe98-4c53-b4c2-d5f9fe5acdb3");
		btAddServer.setMinWidth(110);
		btAddServer.setAutoWidth(true);
		btAddServer.addListener(Events.Select, AutoUpdateSettingsListener);
		TableData tdAddFilter = new TableData();
		lcButtonsContainer.add(btAddServer,tdAddFilter);
		
		btEditServer = new Button(UIContext.Constants.EditStagingServerLabel())
		{
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
		
		//btEditServer.ensureDebugId("");
		btEditServer.setMinWidth(110);
		btEditServer.setAutoWidth(true);
		btEditServer.setEnabled(false);
		btEditServer.addListener(Events.Select, AutoUpdateSettingsListener);
		TableData tdEditFilter = new TableData();
		lcButtonsContainer.add(btEditServer,tdEditFilter);
		
		btMoveUp = new Button(UIContext.Constants.MoveUpServerLabel())
		{
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
		//btMoveUp.setWidth(70);
		//btMoveUp.setWidth("100%");
		//btMoveUp.setWidth("30%");
		btMoveUp.ensureDebugId("4e37f3ce-6653-4a49-b330-d311142d3087");
		btMoveUp.setMinWidth(110);
		btMoveUp.setAutoWidth(true);
		btMoveUp.addListener(Events.Select, AutoUpdateSettingsListener);
		btMoveUp.setEnabled(false);		
		lcButtonsContainer.add(btMoveUp,tdAddFilter);
		
		btMoveDown = new Button(UIContext.Constants.MoveDownServerLabel())
		{
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
		//btMoveDown.setWidth(70);
		//btMoveDown.setWidth("100%");
		//btMoveDown.setWidth("30%");
		btMoveDown.ensureDebugId("349581db-a8cb-46e1-a8c7-24120147626f");
		btMoveDown.setMinWidth(110);
		btMoveDown.setAutoWidth(true);
		btMoveDown.addListener(Events.Select, AutoUpdateSettingsListener);
		btMoveDown.setEnabled(false);
		lcButtonsContainer.add(btMoveDown,tdAddFilter);
		
		btDeleteServer = new Button(UIContext.Constants.DeleteStagingServerLabel())
		{
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
		//btDeleteServer.setWidth(70);
		//btDeleteServer.setWidth("100%");
		//btDeleteServer.setWidth("30%");
		btDeleteServer.ensureDebugId("3b5fadfe-84f9-4fa2-b4c1-230f42e7b49d");
		btDeleteServer.setMinWidth(110);
		btDeleteServer.setAutoWidth(true);
//		TableData tdDeleteFilter = new TableData();
//		tdDeleteFilter.setHorizontalAlign(HorizontalAlignment.RIGHT);
		btDeleteServer.setEnabled(false);
		btDeleteServer.addListener(Events.Select, AutoUpdateSettingsListener);
		
		lcButtonsContainer.add(btDeleteServer,tdAddFilter);		
	//	lcButtonsContainer.setWidth(90);
		
		TableData tdFiltersGridButtonsPanel = new TableData();
		tdFiltersGridButtonsPanel.setHorizontalAlign(HorizontalAlignment.LEFT);
		tdFiltersGridButtonsPanel.setWidth("100%");
		tdFiltersGridButtonsPanel.setStyleName("buttnwidth");
		lcStagingServersContainer.add(lcButtonsContainer,tdFiltersGridButtonsPanel);
		TableData tdStagingServersPanel = new TableData();
		tdStagingServersPanel.setHorizontalAlign(HorizontalAlignment.LEFT);
		lcStagingServersContainer.hide();//if staging server is selected, it will showed.
		lcStagingServersContainer.setStyleAttribute("padding-left", "35px");
		lcStagingServerContainer.add(lcStagingServersContainer,tdStagingServersPanel);
		
		lcDownloadServerContainer.add(lcStagingServerContainer);
//		lcDownloadServerContainer.add(new Html("<HR>"));
		return lcDownloadServerContainer;
//		container.add(lcDownloadServerContainer);
		
		
		//container.add(new Html("<HR>"));
	}
	
	private LayoutContainer defineUpdateScheduleSettings()
	{
		lcUpdateScheduleContainer = new LayoutContainer();
		lcUpdateScheduleContainer.setScrollMode(Scroll.NONE);
	
		int iColumns = 1;
			
		tlUpdateScheduleLayout = defineUpdatesPagesectionTableLayout(iColumns);
		lcUpdateScheduleContainer.setLayout(tlUpdateScheduleLayout);
		
		//Update Schedule
//		LabelField lblUpdateSchedule = new LabelField();
//		lblUpdateSchedule.setText(UIContext.Constants.UpdateScheduleSectionHeader());
//		lblUpdateSchedule.addStyleName("restoreWizardSubItem");
//		
		TableData tdUpdateScheduleHeader = new TableData();
		tdUpdateScheduleHeader.setColspan(iColumns);
		tdUpdateScheduleHeader.setWidth("50%");
		tdUpdateScheduleHeader.setHorizontalAlign(HorizontalAlignment.LEFT);
//		lcUpdateScheduleContainer.add(lblUpdateSchedule,tdUpdateScheduleHeader);

		//Update Schedule Description
		LabelField lblUpdateDescription = new LabelField();
		lblUpdateDescription.setValue(getScheduleDescriptionString());
		
		TableData tdUpdateDescription = new TableData();
		tdUpdateDescription.setColspan(iColumns);
		tdUpdateDescription.setWidth("50%");
		tdUpdateDescription.setHorizontalAlign(HorizontalAlignment.LEFT);
		lcUpdateScheduleContainer.add(lblUpdateDescription,tdUpdateDescription);
		
		// check for updates checkbox
		TableData tdCheckupdatesCheckbox = new TableData();
		tdCheckupdatesCheckbox = new TableData();
		tdCheckupdatesCheckbox.setColspan(iColumns);
		tdCheckupdatesCheckbox.setWidth("60%");
		tdCheckupdatesCheckbox.setHorizontalAlign(HorizontalAlignment.LEFT);
//		tdCheckupdatesCheckbox.setVerticalAlign(VerticalAlignment.TOP);
		
		cbAutoCheckUpdates = new CheckBox();
		cbAutoCheckUpdates.ensureDebugId("d1156029-062e-4190-bf2f-a4aa2f3b966e");
		cbAutoCheckUpdates.setBoxLabel(UIContext.Constants.automaticallyCheckforupdatesLabel());
		cbAutoCheckUpdates.setVisible(true);
		cbAutoCheckUpdates.addListener(Events.Change, AutoUpdateSettingsListener);
		lcUpdateScheduleContainer.add(cbAutoCheckUpdates,tdCheckupdatesCheckbox);
	
		TableLayout layout = defineUpdatesPagesectionTableLayout(4);
		LayoutContainer container = new LayoutContainer();
		container.setLayout(layout);
		lcUpdateScheduleContainer.add(container);
		
		//	Every label
		LabelField lblEvery = new LabelField();
		lblEvery.setValue(UIContext.Constants.preferencesevery());
		lblEvery.setStyleAttribute("white-space", "nowrap");
		
		TableData tdEverylabel = new TableData();
		tdEverylabel.setWidth("8%");
		tdEverylabel.setHorizontalAlign(HorizontalAlignment.LEFT);
//		tdEverylabel.setVerticalAlign(VerticalAlignment.TOP);
		container.add(lblEvery,tdEverylabel);
		
		cbWeekCombo = new BaseSimpleComboBox<String>();
		cbWeekCombo.ensureDebugId("0e28dcf8-feb2-4097-8e46-4e616ef94355");
		tipConfig = new ToolTipConfig(UIContext.Constants.scheduleStartDayAutoUpdateTooltip());
		tip = new ToolTip(cbWeekCombo, tipConfig);
		tip.setHeaderVisible(false);
		cbWeekCombo.setEditable(false);
		cbWeekCombo.setWidth(120);
		
		//cbWeekCombo.setStore(CreateWeekDayInfo());
		
		cbWeekCombo.add(UIContext.Constants.Day());
		cbWeekCombo.add(UIContext.Constants.Sunday());
		cbWeekCombo.add(UIContext.Constants.Monday());
		cbWeekCombo.add(UIContext.Constants.Tuesday());
		cbWeekCombo.add(UIContext.Constants.Wednesday());
		cbWeekCombo.add(UIContext.Constants.Thursday());
		cbWeekCombo.add(UIContext.Constants.Friday());
		cbWeekCombo.add(UIContext.Constants.Saturday());
		
		Utils.setComboboxValue(cbWeekCombo, 0);
		cbWeekCombo.setEnabled(false);
		TableData tdWeekCombo = new TableData();
//		tdWeekCombo.setVerticalAlign(VerticalAlignment.TOP);
		tdWeekCombo.setWidth("10%");
		tdWeekCombo.setHorizontalAlign(HorizontalAlignment.CENTER);
		container.add(cbWeekCombo, tdWeekCombo);

		
//		At label
		LabelField lblAt = new LabelField();
		lblAt.setValue(UIContext.Constants.at());
		lblAt.setStyleAttribute("white-space", "nowrap");
		
		TableData tdAtLabel = new TableData();
		tdAtLabel.setWidth("8%");
		tdAtLabel.setHorizontalAlign(HorizontalAlignment.RIGHT);
//		tdAtLabel.setVerticalAlign(VerticalAlignment.TOP);
		container.add(lblAt,tdAtLabel);
		
		timeField = new FlashTimeField(-1, -1, 
				UIContext.Constants.scheduleStartHourForAutoUpdateTooltip(), 
				"", UIContext.Constants.scheduleStartTimeAutoUpdateTooltip1());		
		timeField.setDebugId("3B5E5A09-A82F-41e0-9C86-DA71D4DA8888", 
				"C5D7A7A2-A81C-4759-A46A-844E2FEB8C10", 
				"FB97697E-1726-4428-BA21-AC2CD0426BA9");
//		td.setVerticalAlign(VerticalAlignment.TOP);
		TableData td = new TableData();
		container.add(timeField, td);
		timeField.setMinutesEnabled(false);	
		/*cbHourCombo = new BaseSimpleComboBox<String>();
		cbHourCombo.setEditable(false);
		cbHourCombo.setTriggerAction(TriggerAction.ALL);
		for (int i = 1; i <= 12; i++) {
			String val;
			if(isHourPrefix())
				val = prefixZero( i, 2 );
			else
			val = new Integer(i).toString();
			cbHourCombo.add(val);
		}
		cbHourCombo.setWidth(40);
		cbHourCombo.setFieldLabel(UIContext.Constants.scheduleStartTime());
		cbHourCombo.setEnabled(false);
		// Tool tip
		tipConfig = new ToolTipConfig(UIContext.Constants.scheduleStartHourForAutoUpdateTooltip());
		tip = new ToolTip(cbHourCombo, tipConfig);
		tip.setHeaderVisible(false);
		
		TableData tabData = new TableData();
		tabData.setHorizontalAlign(HorizontalAlignment.LEFT);
		tabData.setVerticalAlign(VerticalAlignment.TOP);
		lcUpdateScheduleContainer.add(cbHourCombo, tabData);
		
		amCombo = new BaseSimpleComboBox<String>();
		amCombo.ensureDebugId("6836473e-a9ca-420e-b9b6-55ec5733aa8a");
		tipConfig = new ToolTipConfig(UIContext.Constants.scheduleStartTimeAutoUpdateTooltip1());
		tip = new ToolTip(amCombo, tipConfig);
		tip.setHeaderVisible(false);
		amCombo.setEditable(false);
		amCombo.setTriggerAction(TriggerAction.ALL);	
		amCombo.add(UIContext.Constants.scheduleStartTimeAM());
		amCombo.add(UIContext.Constants.scheduleStartTimePM());
		amCombo.setWidth(48);
		amCombo.setEnabled(false);
		amCombo.setStyleAttribute("padding-left", "3px");
		tabData = new TableData();
		//tabData.setPadding(3);
		tabData.setHorizontalAlign(HorizontalAlignment.LEFT);
		tabData.setVerticalAlign(VerticalAlignment.TOP);
		lcUpdateScheduleContainer.add(amCombo, tabData);*/
		
		// for edge , it should generate the random default value of update schedule
		if(UIContext.isLaunchedForEdgePolicy){
			cbWeekCombo.setSimpleValue(GetScheduledWeekDay(new java.util.Random().nextInt(8)));
//			SetScheduledHour(new java.util.Random().nextInt(24));
			timeField.setValue(new Time(new java.util.Random().nextInt(24), 0));
		}
		
//		container.add(lcUpdateScheduleContainer);
//		lcUpdateScheduleContainer.add(new Html("<HR>"));
		return lcUpdateScheduleContainer;
	}
	
	public void RefreshData(UpdateSettingsModel model) 
	{
		updateSettingsModel = model;
		if(updateSettingsModel == null) {
			updateSettingsModel = new UpdateSettingsModel();
		}
				
		int iStagingServers = updateSettingsModel.getStagingServers() != null ? updateSettingsModel
				.getStagingServers().length : 0;
		if (iStagingServers != 0) {
			StagingServerModel[] StagingServers = updateSettingsModel
					.getStagingServers();

			List<StagingServerModel> list = gridStore.getModels();
			list.clear();
			for (int iIndex = 0; iIndex < iStagingServers; iIndex++) {
				StagingServerModel StagingServer = StagingServers[iIndex];
				list.add(iIndex, StagingServer);
			}

			if (list.size() == 5) {
				btAddServer.setEnabled(false);
			}

			gridStore.removeAll();
			gridStore.add(list);
			StagingServersGrid.reconfigure(gridStore,
					StagingServersColumnsModel);
		}

		int sType = updateSettingsModel.getDownloadServerType() == null ? serverType : updateSettingsModel.getDownloadServerType();  
		switch (sType) {
		case 0:// CA server
			if(!bCAServerConnectionTested)
				bInitialTestConnection = true;
			
			rbCAServer.setValue(true);
			rbStagingServer.setValue(false);
			CAServerConnectionStatusImage.setVisible(true);
			break;
		case 1:// staging server
			if(!bStagingServerConnectionTested)
				bInitialTestConnection = true;
			
			rbCAServer.setValue(false);
			rbStagingServer.setValue(true);

			lcStagingServersContainer.show();
			CAServerConnectionStatusImage.setVisible(false);
			break;
		}


		boolean autoupdate = updateSettingsModel.getAutoCheckupdate() == null ? scheduleType : updateSettingsModel.getAutoCheckupdate(); 
		if (autoupdate) {
			cbAutoCheckUpdates.setValue(true);
			cbWeekCombo.setEnabled(true);
			/*cbHourCombo.setEnabled(true);
			amCombo.setEnabled(true);*/
			
			int day = updateSettingsModel.getScheduledWeekDay() == null ? scheduledDay
					: updateSettingsModel.getScheduledWeekDay();			
			String strWeekDay = GetScheduledWeekDay(day);
			cbWeekCombo.setSimpleValue(strWeekDay);

			int hour = updateSettingsModel.getScheduledHour() == null ? scheduledHour
					: updateSettingsModel.getScheduledHour();
			/*String strWeekHour = SetScheduledHour(hour);
			cbHourCombo.setSimpleValue(strWeekHour);*/
			timeField.setValue(new Time(hour, 0));
		} else {
			cbAutoCheckUpdates.setValue(false);
			cbWeekCombo.setEnabled(false);
			/*cbHourCombo.setEnabled(false);
			amCombo.setEnabled(false);*/
			timeField.setEnabled(false);
		}

		// proxySettingsDlg.RefreshData(updateSettingsModel.getproxySettings());

		this.container.repaint();

		return;
	}
	
	public static String GetScheduledWeekDay(int in_iSelectedWeekDay)
	{
		String strSelectedWeekDay = "";
		switch(in_iSelectedWeekDay)
		{
		case 0:
			strSelectedWeekDay = UIContext.Constants.Day();
			break;
		case 1:
			strSelectedWeekDay = UIContext.Constants.Sunday();
			break;
		case 2:
			strSelectedWeekDay = UIContext.Constants.Monday();
			break;
		case 3:
			strSelectedWeekDay = UIContext.Constants.Tuesday();
			break;
		case 4:
			strSelectedWeekDay = UIContext.Constants.Wednesday();
			break;
		case 5:
			strSelectedWeekDay = UIContext.Constants.Thursday();
			break;
		case 6:
			strSelectedWeekDay = UIContext.Constants.Friday();
			break;
		case 7:
			strSelectedWeekDay = UIContext.Constants.Saturday();
			break;
		default:
			strSelectedWeekDay = "";
			break;
		}
		return strSelectedWeekDay;
	}
	
	public static String getScheduledHour( int in_iSelectedHour )
	{
		String strSelectedHour = "";
		switch(in_iSelectedHour)
		{
		case 0:
		case 12:
			strSelectedHour = "1";
			break;
		case 1:
		case 13:
			strSelectedHour = "2";
			break;
		case 2:
		case 14:
			strSelectedHour = "3";
			break;
		case 3:
		case 15:
			strSelectedHour = "4";
			break;
		case 4:
		case 16:
			strSelectedHour = "5";
			break;
		case 5:
		case 17:
			strSelectedHour = "6";
			break;
		case 6:
		case 18:
			strSelectedHour = "7";
			break;
		case 7:
		case 19:
			strSelectedHour = "8";
			break;
		case 8:
		case 20:
			strSelectedHour = "9";
			break;
		case 9:
		case 21:
			strSelectedHour = "10";
			break;
		case 10:
		case 22:
			strSelectedHour = "11";
			break;
		case 11:
		case 23:
			strSelectedHour = "12";
			break;
		default:
			strSelectedHour = "";
			break;
		}
		return strSelectedHour;
	}
	
	public static String getScheduledHourAMorPM(int in_iSelectedHour)
	{
		if(in_iSelectedHour >=0 && in_iSelectedHour <12)
		{
			return UIContext.Constants.scheduleStartTimeAM();
		}
		else
		{
			return UIContext.Constants.scheduleStartTimePM();	
		}
	}
	
/*	private String SetScheduledHour(int in_iSelectedHour)
	{
		String strSelectedHour = getScheduledHour(in_iSelectedHour);
		cbHourCombo.setSimpleValue(strSelectedHour);
		amCombo.setSimpleValue(getScheduledHourAMorPM(in_iSelectedHour));
		return strSelectedHour;
	}*/
	
	public boolean Validate()
	{
		boolean bValidated = true;
		
		MessageBox msgError = new MessageBox();
		msgError.setIcon(MessageBox.ERROR);
		msgError.setTitleHtml(UIContext.Messages.messageBoxTitleError(getProductNameString()));
		msgError.setModal(true);
		
		if(rbStagingServer.getValue())
		{
			if(gridStore.getCount() == 0)
			{
				msgError.setMessage(UIContext.Messages.SelectStagingServersMessage());
				Utils.setMessageBoxDebugId(msgError);
				msgError.show();
				bValidated = false;
				return bValidated;
			}
		}
		
		if(!(rbCAServer.getValue()) && !(rbStagingServer.getValue()))
		{
			msgError.setMessage(UIContext.Messages.SelectDownloadServerMessage());
			Utils.setMessageBoxDebugId(msgError);
			msgError.show();
			bValidated = false;
			return bValidated;
		}
		
		if(cbAutoCheckUpdates.getValue())
		{
			if(cbWeekCombo.getSelectedIndex() == -1)
			{
				msgError.setMessage(UIContext.Messages.SelectDayForAutoUpdatesToRunMessage());
				Utils.setMessageBoxDebugId(msgError);
				msgError.show();
				bValidated = false;
				return bValidated;
			}
		/*	
			if(cbHourCombo.getSelectedIndex() == -1)
			{
				msgError.setMessage(UIContext.Messages.SelectHourForAutoUpdatesToRunMessage());
				msgError.show();
				bValidated = false;
				return bValidated;
			}
			if(amCombo.getSelectedIndex() == -1)
			{
				msgError.setMessage(UIContext.Messages.SelectAPMPMForAutoUpdatesToRunMessage());
				msgError.show();
				bValidated = false;
				return bValidated;
			}*/
		}
		
		return bValidated;
	}
	
	public void save()
	{
		if(host.getUpdateSettingModel() == null)
		{
			updateSettingsModel = new UpdateSettingsModel();
		}
		if(updateSettingsModel == null)
		{
			return;
		}
		
		Integer iServerType = -1;
		if(rbCAServer.getValue())
		{
			iServerType = 0;
		}
		else if(rbStagingServer.getValue())
		{
			iServerType = 1;
		}
		updateSettingsModel.setDownloadServerType(iServerType);
		
		//saving staging servers information
		int istagingServersCount = gridStore.getCount();
		StagingServerModel[] stagingServersList = null;
		if(istagingServersCount > 0)
		{
			stagingServersList = new StagingServerModel[istagingServersCount];
			for(int iIndex = 0;iIndex < istagingServersCount;iIndex++)
			{
				StagingServerModel stagingServerModel = gridStore.getAt(iIndex);
				stagingServersList[iIndex] = new StagingServerModel();
				stagingServersList[iIndex].setStagingServerId(iIndex);
				stagingServersList[iIndex].setStagingServer(stagingServerModel.getStagingServer());
				stagingServersList[iIndex].setStagingServerPort(stagingServerModel.getStagingServerPort());
				stagingServersList[iIndex].setStagingServerStatus(stagingServerModel.getStagingServerStatus());
			}
		}
		updateSettingsModel.setStagingServers(stagingServersList);
		
		if(cbAutoCheckUpdates.getValue())
		{
			updateSettingsModel.setAutoCheckupdate(true);
			updateSettingsModel.setScheduledWeekDay(cbWeekCombo.getSelectedIndex());
			/*int iHour = cbHourCombo.getSelectedIndex();
			int iRange = amCombo.getSelectedIndex();
			if(iRange == 1)
			{
				iHour += 12;
			}*/
			updateSettingsModel.setScheduledHour( timeField.getValue().getHour() );
		}
		else
		{
			updateSettingsModel.setAutoCheckupdate(false);
		}
				
		//proxySettingsDlg.Save(updateSettingsModel);
		host.setUpdateSettingModel(updateSettingsModel);
	}
	
//	private int processUpdatingHour(int hour, int diff){
//		return (hour + diff + 24)%24;
//	}
	
	public boolean TestDownloadServerConnections(){
		return TestDownloadServerConnections(true);
	}
	
	public boolean TestDownloadServerConnections(boolean check) {	
		host.mask(UIContext.Messages.DownloadServerTestWaitMessage());
		
		UpdateSettingsModel testSettings = new UpdateSettingsModel();
		
		boolean bTestConnections = false;
		btTestConnection.setEnabled(false);
		if(rbCAServer.getValue())
		{
			bCAServerConnectionTested = true;
			
			bTestConnections = true;
			testSettings.setDownloadServerType(0);
			if(proxySettingsDlg.getProxySettingsModel()== null)
			{
				proxySettingsDlg.RefreshData(updateSettingsModel.getproxySettings());
			}
			testSettings.setproxySettings(proxySettingsDlg.getProxySettingsModel());
		}
		else if(rbStagingServer.getValue())
		{
			bStagingServerConnectionTested = true;
			testSettings.setDownloadServerType(1);
			List<StagingServerModel> selectedStagingServersList = StagingServersGrid.getSelectionModel().getSelectedItems();
			
			if(selectedStagingServersList.size() == 0)
			{
				selectedStagingServersList = gridStore.getModels();
			}
			if(selectedStagingServersList.size() != 0)
			{
				StagingServerModel[] stagingServers = new StagingServerModel[selectedStagingServersList.size()]; 
				
				for(int iIndex = 0;iIndex < selectedStagingServersList.size();iIndex++)
				{
					StagingServerModel stagingserver = selectedStagingServersList.get(iIndex);
					stagingServers[iIndex] = new StagingServerModel();
					stagingServers[iIndex].setStagingServer(stagingserver.getStagingServer());
					stagingServers[iIndex].setStagingServerPort(stagingserver.getStagingServerPort());
					stagingServers[iIndex].setStagingServerId(iIndex);
				}
				bTestConnections = true;
				testSettings.setStagingServers(stagingServers);
			}
		}
			
		if(bTestConnections&&check)
		{
			TestDownloadServerConnections(testSettings);
		}
		else 
		{
			host.unmask();
			btTestConnection.setEnabled(isEditable);
			
		}
		
		return true;
	}
	
	//added by cliicy.luo
	public boolean TestBIDownloadServerConnections(){
		return TestBIDownloadServerConnections(true);
	}

	public boolean TestBIDownloadServerConnections(boolean check) {	
		host.mask(UIContext.Messages.DownloadServerTestWaitMessage());
		
		UpdateSettingsModel testSettings = new UpdateSettingsModel();
		
		boolean bTestConnections = false;
		btTestConnection.setEnabled(false);
		if(rbCAServer.getValue())
		{
			bCAServerConnectionTested = true;
			
			bTestConnections = true;
			testSettings.setDownloadServerType(0);
			if(proxySettingsDlg.getProxySettingsModel()== null)
			{
				proxySettingsDlg.RefreshData(updateSettingsModel.getproxySettings());
			}
			testSettings.setproxySettings(proxySettingsDlg.getProxySettingsModel());
		}
		else if(rbStagingServer.getValue())
		{
			bStagingServerConnectionTested = true;
			testSettings.setDownloadServerType(1);
			List<StagingServerModel> selectedStagingServersList = StagingServersGrid.getSelectionModel().getSelectedItems();
			
			if(selectedStagingServersList.size() == 0)
			{
				selectedStagingServersList = gridStore.getModels();
			}
			if(selectedStagingServersList.size() != 0)
			{
				StagingServerModel[] stagingServers = new StagingServerModel[selectedStagingServersList.size()]; 
				
				for(int iIndex = 0;iIndex < selectedStagingServersList.size();iIndex++)
				{
					StagingServerModel stagingserver = selectedStagingServersList.get(iIndex);
					stagingServers[iIndex] = new StagingServerModel();
					stagingServers[iIndex].setStagingServer(stagingserver.getStagingServer());
					stagingServers[iIndex].setStagingServerPort(stagingserver.getStagingServerPort());
					stagingServers[iIndex].setStagingServerId(iIndex);
				}
				bTestConnections = true;
				testSettings.setStagingServers(stagingServers);
			}
		}
			
		if(bTestConnections&&check)
		{
			TestBIDownloadServerConnections(testSettings);
		}
		else 
		{
			host.unmask();
			btTestConnection.setEnabled(isEditable);
			
		}
		
		return true;
	}
	
	protected void TestBIDownloadServerConnections(UpdateSettingsModel model){
		loginService.testBIDownloadServerConnection(model,new AsyncCallback<UpdateSettingsModel>() {

			@Override
			public void onFailure(Throwable caught) {
				onTestBIDownloadServerConnectionsFail(caught);
			}

			@Override
			public void onSuccess(UpdateSettingsModel result) {
				onTestBIDownloadServerConnectionsSuccess(result);
			}
		});
	}
	
	protected void onTestBIDownloadServerConnectionsFail(Throwable caught){
		
		btTestConnection.setEnabled(isEditable);
		
		host.unmask();
		MessageBox msgError = new MessageBox();
		msgError.setIcon(MessageBox.ERROR);
		msgError.setTitleHtml(UIContext.Messages.messageBoxTitleError(getProductNameString()));
		msgError.setModal(true);
		String strMsg = null ;
		strMsg = UIContext.Messages.DownloadServerConnectionVerificationFailureError(caught.getMessage());

		msgError.setMessage(strMsg);
		Utils.setMessageBoxDebugId(msgError);
		msgError.show();

	}
	
	/**This method is extracted for the call from subclass in edge ui
	 * @param caught
	 */
	protected void onTestBIDownloadServerConnectionsSuccess(UpdateSettingsModel result){
		if(result != null)
		{
			refreshDownloadServerSettings(result);
		}
		
		btTestConnection.setEnabled(isEditable);
		
		host.unmask();
		
	}
	
	//added by cliicy.luo
	
	/**This method is extracted from TestDownloadServerConnections() for the override of its subclass in edge ui.
	 * Since Edge APM use a different implemention to test connection, so it will override this method .
	 * @param model
	 */
	protected void TestDownloadServerConnections(UpdateSettingsModel model){
		loginService.testDownloadServerConnection(model,new AsyncCallback<UpdateSettingsModel>() {

			@Override
			public void onFailure(Throwable caught) {
				onTestDownloadServerConnectionsFail(caught);
			}

			@Override
			public void onSuccess(UpdateSettingsModel result) {
				onTestDownloadServerConnectionsSuccess(result);
			}
		});
	}
	
	/**This method is extracted for the call from subclass in edge ui
	 * @param caught
	 */
	protected void onTestDownloadServerConnectionsFail(Throwable caught){
		
		btTestConnection.setEnabled(isEditable);
		
		host.unmask();
		MessageBox msgError = new MessageBox();
		msgError.setIcon(MessageBox.ERROR);
		msgError.setTitleHtml(UIContext.Messages.messageBoxTitleError(getProductNameString()));
		msgError.setModal(true);
		String strMsg = null ;
		strMsg = UIContext.Messages.DownloadServerConnectionVerificationFailureError(caught.getMessage());

		msgError.setMessage(strMsg);
		Utils.setMessageBoxDebugId(msgError);
		msgError.show();

	}
	
	/**This method is extracted for the call from subclass in edge ui
	 * @param caught
	 */
	protected void onTestDownloadServerConnectionsSuccess(UpdateSettingsModel result){
		if(result != null)
		{
			refreshDownloadServerSettings(result);
		}
		
		btTestConnection.setEnabled(isEditable);
		
		host.unmask();
		
	}
	
	public class StagingServerWindow extends Window
	{
		private Button btOK;
		private Button btCancel;
		private String strButtonClicked;
		final private StagingServerWindow thisWindow;
		
		private TextField<String> txtStagingServer;
		private NumberField StagingServerPort;
		
		private StagingServerModel stagingServer = null;
		private Validator AutoUpdateSettingsValidator = null;
		private  boolean editmode;

		
		public StagingServerWindow()
		{
			thisWindow = this;
			thisWindow.setResizable(false);
			thisWindow.setAutoHeight(true);
			thisWindow.setAutoHeight(true);
			thisWindow.setHeadingHtml(UIContext.Constants.DownloadFromStagingServerLabel());
						
			TableLayout tlStagingServerLayout = new TableLayout(2);
			tlStagingServerLayout.setCellSpacing(5);
			tlStagingServerLayout.setCellPadding(2);
			thisWindow.setLayout(tlStagingServerLayout);
			
			LabelField lblStagingServer = new LabelField(UIContext.Constants.serverName());
			thisWindow.add(lblStagingServer);
			
			AutoUpdateSettingsValidator = new Validator() {
			@Override
			public String validate(Field<?> field, String value) {
				if(field == txtStagingServer)
				{
					MessageBox msgError = new MessageBox();
					msgError.setIcon(MessageBox.ERROR);
					msgError.setTitleHtml(UIContext.Messages.messageBoxTitleError(getProductNameString()));
					msgError.setModal(true);
					if((txtStagingServer.getValue() == null) || (txtStagingServer.getValue().length() == 0))
					{
						msgError.setMessage(UIContext.Messages.EnterValidStagingServerName());
						Utils.setMessageBoxDebugId(msgError);
						msgError.show();
						return null;
					}
					ValidateStagingServer(false);
				}
				else if(field == StagingServerPort)
				{
					int iStagingServerPort = StagingServerPort.getValue().intValue();
					
					MessageBox msgError = new MessageBox();
					msgError.setIcon(MessageBox.ERROR);
					msgError.setTitleHtml(UIContext.Messages.messageBoxTitleError(getProductNameString()));
					msgError.setModal(true);
					if(iStagingServerPort < 1 || iStagingServerPort > 65535)
					{
						StagingServerPort.setValue(null);
						msgError.setMessage(UIContext.Messages.EnterValidPortMessage(UIContext.Constants.StagingServer()));
						Utils.setMessageBoxDebugId(msgError);
						msgError.show();
					}
				}
				return null;
			}
		};
			
			txtStagingServer = new TextField<String>();
			txtStagingServer.ensureDebugId("e08ff65a-342f-4d9d-b775-1867615d8c73");
			txtStagingServer.setWidth(120);
			txtStagingServer.setMaxLength(128);
			txtStagingServer.setAllowBlank(false);
			txtStagingServer.setValidateOnBlur(true);
			txtStagingServer.setValidator(AutoUpdateSettingsValidator);
			
			TableData tdStagingServer = new TableData();
			tdStagingServer.setHorizontalAlign(HorizontalAlignment.LEFT);
			thisWindow.add(txtStagingServer,tdStagingServer);
			
			LabelField lblStagingServerPort = new LabelField(UIContext.Constants.PreferencesProxyServerPortLabel());

			TableData tlStagingServerportLabel = new TableData();
			tlStagingServerportLabel.setHorizontalAlign(HorizontalAlignment.RIGHT);
			thisWindow.add(lblStagingServerPort,tlStagingServerportLabel);

			StagingServerPort = new NumberField();
			StagingServerPort.ensureDebugId("f0647a1b-388c-4739-9329-bd29ddb9e62d");
			StagingServerPort.setWidth(100);
			StagingServerPort.setValue(Integer.parseInt(com.google.gwt.user.client.Window.Location.getPort()));
			StagingServerPort.setMinValue(1);
			StagingServerPort.setMaxValue(65535);
			StagingServerPort.setAllowBlank(false);
			StagingServerPort.setAllowNegative(false);
			StagingServerPort.setAllowDecimals(false);
			StagingServerPort.setValidateOnBlur(true);
			StagingServerPort.setValidator(AutoUpdateSettingsValidator);			
			
			TableData tdStagingServerPort = new TableData();
			tdStagingServerPort.setHorizontalAlign(HorizontalAlignment.LEFT);
			thisWindow.add(StagingServerPort,tdStagingServerPort);
			
			Listener<BaseEvent> StagingServerListerner = new Listener<BaseEvent>() {

				@Override
				public void handleEvent(BaseEvent event) {
					if(event.getSource() == btOK)
					{
						ValidateAndSaveStagingServerDetails();
					}
					else if(event.getSource() == btCancel)
					{
						strButtonClicked = "CANCEL";
						thisWindow.hide(btCancel);
					} 
				}
			};
			
			btOK = new Button();
			btOK.ensureDebugId("fac925b2-0970-4d0c-9d38-ac9d7ac9ed82");
			btOK.setText(UIContext.Constants.ok());
			btOK.setMinWidth(50);
			btOK.addListener(Events.Select, StagingServerListerner);
			thisWindow.addButton(btOK);
			
			
			btCancel = new Button();
			btCancel.ensureDebugId("8d06c2c6-9a51-4de6-b7a3-08e17e7c000b");
			btCancel.setText(UIContext.Constants.cancel());
			btCancel.setMinWidth(50);
			btCancel.setAutoWidth(true);
			btCancel.addListener(Events.Select, StagingServerListerner);
			thisWindow.addButton(btCancel);
			thisWindow.setFocusWidget(txtStagingServer);
			this.editmode=false;
		}

		public StagingServerWindow(StagingServerModel stagingserver){
			this();
			txtStagingServer.setValue(stagingserver.getStagingServer());
			StagingServerPort.setValue((stagingserver.getStagingServerPort()));
			
			this.editmode=true;
		}
		
		public String getButtonClicked()
		{
			return strButtonClicked;
		}
		
		public StagingServerModel getselectedStagingServer()
		{
			return stagingServer;
		}
		
		private boolean ValidateAndSaveStagingServerDetails()
		{
			boolean bValidated = true;
			MessageBox msgError = new MessageBox();
			msgError.setIcon(MessageBox.ERROR);
			msgError.setTitleHtml(UIContext.Messages.messageBoxTitleError(getProductNameString()));
			msgError.setModal(true);
			if((txtStagingServer.getValue() == null) || (txtStagingServer.getValue().length() == 0) || (txtStagingServer.getValue().length() > 128))
			{
				msgError.setMessage(UIContext.Messages.EnterValidStagingServerName());
				Utils.setMessageBoxDebugId(msgError);
				msgError.show();
				bValidated = false;
				return bValidated;
			}
			
			if(StagingServerPort.getValue() == null)
			{
				msgError.setMessage(UIContext.Messages.EnterValidPortMessage(UIContext.Constants.StagingServer()));
				Utils.setMessageBoxDebugId(msgError);
				msgError.show();
				bValidated = false;
				return bValidated;
			}
			
			if(StagingServerPort.getValue().intValue() < 1 || StagingServerPort.getValue().intValue() > 65535)
			{
				bValidated = false;
				return bValidated;
			}
		
			if(IsThisStagingServerAlreadyAdded(txtStagingServer.getValue(),StagingServerPort.getValue().intValue()))
			{
				txtStagingServer.setValue("");
				bValidated = false;
				return bValidated;
			}
			
			ValidateStagingServer(true);
			
			return bValidated;
		}
		
		private void ValidateStagingServer(final boolean bSave)
		{
			service.isLocalHost(txtStagingServer.getValue(), new BaseAsyncCallback<Boolean>(){
				
				@Override
				public void onFailure(Throwable caught)
				{
					super.onFailure(caught);
					return;
				}
				
				@Override
				public void onSuccess(Boolean result)
				{
					MessageBox msgError = new MessageBox();
					msgError.setIcon(MessageBox.ERROR);
					msgError.setTitleHtml(UIContext.Messages.messageBoxTitleError(getProductNameString()));
					msgError.setModal(true);
					super.onSuccess(result);
					if(result && !forEdge)
					{
						txtStagingServer.setValue(null);
						if(!bSave)
						{
							msgError.setMessage(UIContext.Messages.D2DAutoUpdateStagingServerCannotBeLocalMachineMessage());
							Utils.setMessageBoxDebugId(msgError);
							msgError.show();
						}
					}
					else
					{
						loginService.ValidateServerName(txtStagingServer.getValue(), new AsyncCallback<Boolean>() {
							
							@Override
							public void onSuccess(Boolean result) {
								if(!result)
								{
									txtStagingServer.setValue(null);
									if(!bSave)
									{
										MessageBox msgError = new MessageBox();
										msgError.setIcon(MessageBox.ERROR);
										msgError.setTitleHtml(UIContext.Messages.messageBoxTitleError(getProductNameString()));
										msgError.setModal(true);
										msgError.setMessage(UIContext.Messages.InValidCharactersServerNameFoundMessage(UIContext.Constants.StagingServer()));
										Utils.setMessageBoxDebugId(msgError);
										msgError.show();
										return;
									}
								}
								else
								{
									if(bSave)
									{
										if(stagingServer == null)
										{
											stagingServer = new StagingServerModel();
										}
										stagingServer.setStagingServer(txtStagingServer.getValue());
										stagingServer.setStagingServerPort(StagingServerPort.getValue().intValue());
										stagingServer.setStagingServerStatus(-1);
										strButtonClicked = "OK";
										thisWindow.hide(btOK);
									}
								}
							}
							
							@Override
							public void onFailure(Throwable caught) {
								MessageBox msgError = new MessageBox();
								msgError.setIcon(MessageBox.ERROR);
								msgError.setTitleHtml(UIContext.Messages.messageBoxTitleError(getProductNameString()));
								msgError.setModal(true);
								
								String strMessage = "";
								strMessage += UIContext.Messages.FailedToValidateStagingServerName();
								strMessage += caught.getMessage();
								
								msgError.setMessage(strMessage);
								Utils.setMessageBoxDebugId(msgError);
								msgError.show();
								return;					
							}
						});
					}
					return;
				}
			});
			return;
		}
	}
	
	/**This function is designed to be override by EDGE to change the description label content for schedule
	 * @return
	 */
	protected String getScheduleDescriptionString(){
		return UIContext.Messages.updateScheduleDescription(getProductNameString());
	}
	
	protected String getProductNameString()
	{
		return UIContext.productNameD2D;		
	}	
	
	public void setEditable(boolean isEditable){
		this.isEditable = isEditable;
		rbCAServer.setEnabled(isEditable);
		rbStagingServer.setEnabled(isEditable);
		btProxysettings.setEnabled(isEditable);
		CAServerConnectionStatusImage.setVisible(isEditable);
		btTestConnection.setEnabled(isEditable);
		////Auto checkupdate
		cbAutoCheckUpdates.setEnabled(isEditable);
		cbWeekCombo.setEnabled(isEditable);
		/*cbHourCombo.setEnabled(isEditable);
		amCombo.setEnabled(isEditable);*/
		timeField.setEnabled(isEditable);
		
		StagingServersGrid.setEnabled(isEditable);
		btAddServer.setEnabled(isEditable);
		btDeleteServer.setEnabled(isEditable);
		btMoveUp.setEnabled(isEditable);
		btMoveDown.setEnabled(isEditable);
		btTestConnection.setEnabled(isEditable);
		
	}
	
	//This is designed for EDGE app to inject a derived PreferencesProxySettingsWindow.
	public void setProxySettingsWindow(PreferencesProxySettingsWindow proxyWindow){
		proxySettingsDlg = proxyWindow;
	}

	public boolean isForEdge() {
		return forEdge;
	}

	public void setForEdge(boolean forEdge) {
		this.forEdge = forEdge;
	}
}
