package com.ca.arcflash.ui.client.restore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.HelpTopics;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.ArchiveDestinationModel;
import com.ca.arcflash.ui.client.model.ArchiveGridTreeNode;
import com.ca.arcflash.ui.client.model.ArchiveRestoreDestinationVolumesModel;
import com.ca.arcflash.ui.client.model.ArchiveSettingsModel;
import com.ca.arcflash.ui.client.model.BackupVMModel;
import com.ca.arcflash.ui.client.model.CatalogItemModel;
import com.ca.arcflash.ui.client.model.CustomizationModel;
import com.ca.arcflash.ui.client.model.DestType;
import com.ca.arcflash.ui.client.model.JobLauncher;
import com.ca.arcflash.ui.client.model.JobMonitorModel;
import com.ca.arcflash.ui.client.model.RecoveryPointModel;
import com.ca.arcflash.ui.client.model.RestoreArchiveJobModel;
import com.ca.arcflash.ui.client.model.RestoreJobArchiveItemNodeModel;
import com.ca.arcflash.ui.client.model.RestoreJobArchiveVolumeNodeModel;
import com.ca.arcflash.ui.client.model.RestoreJobModel;
import com.ca.arcflash.ui.client.model.RestoreJobNodeModel;
import com.ca.arcflash.ui.client.model.RestoreJobType;
import com.ca.arcflash.ui.client.restore.ad.ADRecoveryPointsPanel;
import com.ca.arcflash.ui.client.restore.ad.ActiveDirectoryExplorerPanel;
import com.ca.arcflash.ui.client.restore.mailboxexplorer.MailboxExplorerContext;
import com.ca.arcflash.ui.client.restore.mailboxexplorer.MailboxExplorerPanel;
import com.ca.arcflash.ui.client.vsphere.vmrecover.VMRecoveryOptionsPanel;
import com.ca.arcflash.ui.client.vsphere.vmrecover.VMRecoveryPointsPanel;
import com.ca.arcflash.ui.client.vsphere.vmrecover.VMRestoreSearchPanel;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.core.FastMap;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class RestoreWizardContainer extends ContentPanel
{
	final LoginServiceAsync service = GWT.create(LoginService.class);

	private RestoreWizardContainer thisWindow; ///D2D Lite Integration
	private Map<String, LayoutContainer> pages;
	private int pageIndex = 0;

	public static final int RESTORE_WIZARD_WIDTH = 700;
	public static final int RESTORE_WIZARD_HEIGHT = 595;
    public static final int CONTENT_WIDTH = 638;
	
	public static final int PAGE_INTRO = 0;
	public static final int PAGE_RECOVERY = 1;
	public static final int PAGE_ARCHIVE_RECOVERY = 2;
	public static final int PAGE_SEARCH = 3;
	public static final int PAGE_ARCHIVE_SEARCH = 4;
	public static final int PAGE_OPTIONS = 5;
	public static final int PAGE_SUMMARY = 6;
	public static final int PAGE_VM_RECOVERY = 7;
	public static final int PAGE_VM_OPTION = 8;
	public static final int PAGE_VM_SUMMARY = 9;
    public static final int PAGE_EXCHANGE_GRT_RECOVERY = 10;      // Exchange GRT choose recovery point and EDB
	public static final int PAGE_EXCHANGE_GRT_MAIL_EXPLORER = 11; // Exchange GRT mail explorer
	public static final int PAGE_RESTORE_SEARCH_RESULT = 12; //restore search result panel
	public static final int PAGE_VAPP_RECOVERY = 13;
	public static final int PAGE_AD_RECOVERY = 14; //AD choose recovery point
	public static final int PAGE_AD_EXPLORER = 15; //AD node explorer

	public static final int PAGE_End = PAGE_SUMMARY;
	public static final int MOD = 100;
	public static final int PAGE_OPTIONS_FS = PAGE_OPTIONS * MOD
			+ RestoreJobType.FileSystem.getValue();
	public static final int PAGE_OPTIONS_SQL = PAGE_OPTIONS * MOD
			+ RestoreJobType.VSS_SQLServer.getValue();
	public static final int PAGE_OPTIONS_Exch = PAGE_OPTIONS * MOD
			+ RestoreJobType.VSS_Exchange.getValue();
	public static final int PAGE_OPTIONS_AD = PAGE_OPTIONS * MOD
			+ RestoreJobType.ActiveDirectory.getValue();
	public static final int PAGE_OPTIONS_VM = PAGE_OPTIONS * MOD
	+ RestoreJobType.VM_Recovery.getValue();
	public static final int PAGE_OPTIONS_Exch_GRT = PAGE_OPTIONS * MOD
			+ RestoreJobType.GRT_Exchange.getValue();

	public static final int PAGE_SUMMARY_FS = PAGE_SUMMARY * MOD
			+ RestoreJobType.FileSystem.getValue();
	public static final int PAGE_SUMMARY_SQL = PAGE_SUMMARY * MOD
			+ RestoreJobType.VSS_SQLServer.getValue();
	public static final int PAGE_SUMMARY_Exch = PAGE_SUMMARY * MOD
			+ RestoreJobType.VSS_Exchange.getValue();
	public static final int PAGE_SUMMARY_AD = PAGE_SUMMARY * MOD
			+ RestoreJobType.ActiveDirectory.getValue();
	public static final int PAGE_SUMMARY_VM = PAGE_SUMMARY * MOD
	+ RestoreJobType.VM_Recovery.getValue();
	public static final int PAGE_SUMMARY_Exch_GRT = PAGE_SUMMARY * MOD
			+ RestoreJobType.GRT_Exchange.getValue();
	
	
	public static final int RESTORE_BY_SEARCH = 0;
	public static final int RESTORE_BY_BROWSE = 1;
	public static final int RECOVER_VM = 2;
    public static final int RESTORE_BY_BROWSE_EXCHANGE_GRT = 3;
    public static final int RESTORE_BY_BROWSE_ARCHIVE = 4;
	public static final int RESTORE_BY_SEARCH_ARCHIVE = 5;
	public static final int RESTORE_RECOVERYPOINT_SNAPSHOT = 5;
	public static final int RECOVER_AD = 6;
	
	public static final int JOBTYPE_VMWARE_VM_RECOVERY = 5;
	public static final int JOBTYPE_HYPERV_VM_RECOVERY = 50;
	public final static int JOBTYPE_VMWARE_VAPP_RECOVERY = 26;
	public final static int JOBTYPE_HYPERV_CLUSTER_RECOVERY = JOBTYPE_HYPERV_VM_RECOVERY;
	
	public Button prevButton;
	public Button nextButton;
	public Button cancelButton;
	//private LayoutContainer dialog;
	private LayoutContainer centerArea;
	private final CardLayout cardlayout;
	private String sessionPath = "";
	private RestoreJobModel model;
	private RestoreArchiveJobModel archiveJobModel;

	public int restoreType = RESTORE_BY_BROWSE;
	
	public RecoveryPointsPanel recvPointPanel = null;
	public ActiveDirectoryExplorerPanel adExplorerPanel = null;
	public RestoreArchiveBrowsePanel restoreArchiveBrowsePanel = null;
	public RestoreSearchPanel restoreSearchPanel = null;
	public VMRecoveryPointsPanel vmrecvPointPanel = null;
	public RestoreSearchResultPanel restoreSearchResult = null;
	
	public static final EventType onRestoreDateChanged = new EventType();
	
	private int helpIndex;

	private boolean cancel = true;
	
	private boolean isEncrypted = false;
	
	private boolean grtMailExplorer = true;
	
	public boolean isEncrypted() {
		return isEncrypted;
	}

	public void setEncrypted(boolean isEncrypted) {
		this.isEncrypted = isEncrypted;
	}

	public boolean isCancel() {
		return this.cancel;
	}
	
	private int defaultPage = PAGE_INTRO;            ///D2D Lite Integration
	public static EventType CloseMessage = new EventType(1025);

	public RestoreWizardContainer(int nPageIndex) {
		thisWindow = this;
		defaultPage = nPageIndex;
		RestoreContext.init();
		model = RestoreContext.getRestoreModel();
		archiveJobModel = RestoreContext.getRestoreArchiveJobModel();
		this.setScrollMode(Scroll.AUTOY);
		this.setHeaderVisible(false);                ///D2D Lite Integration
		this.setBodyBorder(false);

		setButtonAlign(HorizontalAlignment.RIGHT);

//		dialog = new LayoutContainer();
//		RowLayout rl = new RowLayout();
//		dialog.setLayout(rl);

		this.setLayout(new RowLayout(Orientation.VERTICAL));

		centerArea = new LayoutContainer();
		centerArea.setStyleAttribute("margin", "10px");
//		dialog.add(centerArea);

		cardlayout = new CardLayout();
		centerArea.setLayout(cardlayout);

		pages = new FastMap<LayoutContainer>();

		RestoreIntroductionPanel introPanel = new RestoreIntroductionPanel(this);
		centerArea.add(introPanel);
		pages.put(indexToPageKey(PAGE_INTRO), introPanel);

		Listener<BaseEvent> listener = new Listener<BaseEvent>() {
			
			@Override
			public void handleEvent(BaseEvent be) {
				if(be instanceof AppEvent) {
					if(be.getSource() != null && be.getSource() instanceof Integer 
							&& (Integer)be.getSource() != pageIndex){
						return;
					}
					if(((Integer)((AppEvent)be).getData()) > 0)
						nextButton.setEnabled(true);
					else
						nextButton.setEnabled(false);
						
					if((Integer)be.getSource()==PAGE_AD_EXPLORER){
						if(((Integer)((AppEvent)be).getData()) > 0)
							prevButton.setEnabled(true);
						else
							prevButton.setEnabled(false);
					}
					
					if((Integer)be.getSource()==PAGE_EXCHANGE_GRT_MAIL_EXPLORER)
					{
						grtMailExplorer = ((Integer)((AppEvent)be).getData()) > 0;
					}
					
				}
			}
		};
		
		recvPointPanel = new RecoveryPointsPanel(this);
		recvPointPanel.addListener(onRestoreDateChanged, listener);
		centerArea.add(recvPointPanel);
		pages.put(indexToPageKey(PAGE_RECOVERY), recvPointPanel);
		
		if(UIContext.uiType == Utils.UI_TYPE_D2D) {
			restoreArchiveBrowsePanel = new RestoreArchiveBrowsePanel(this);
			restoreArchiveBrowsePanel.addListener(onRestoreDateChanged, listener);
			centerArea.add(restoreArchiveBrowsePanel);
			pages.put(indexToPageKey(PAGE_ARCHIVE_RECOVERY), restoreArchiveBrowsePanel);
			
			restoreSearchPanel = new RestoreSearchPanel(this);
			restoreSearchPanel.addListener(onRestoreDateChanged, listener);
			centerArea.add(restoreSearchPanel);
			pages.put(indexToPageKey(PAGE_SEARCH), restoreSearchPanel);
			
			vmrecvPointPanel = new VMRecoveryPointsPanel(null);
			vmrecvPointPanel.addListener(onRestoreDateChanged, listener);
			centerArea.add(vmrecvPointPanel);
			pages.put(indexToPageKey(PAGE_VM_RECOVERY), vmrecvPointPanel);
			
			// Exchange GRT
			ExchangeGRTRecoveryPointsPanel exchangeGRTRecvPointPanel = new ExchangeGRTRecoveryPointsPanel(this);
			exchangeGRTRecvPointPanel.addListener(onRestoreDateChanged, listener);
			centerArea.add(exchangeGRTRecvPointPanel);
			pages.put(indexToPageKey(PAGE_EXCHANGE_GRT_RECOVERY), exchangeGRTRecvPointPanel);		
			
			MailboxExplorerPanel mailboxExplorerPanel = new MailboxExplorerPanel(null);
			mailboxExplorerPanel.addListener(onRestoreDateChanged, listener);
			centerArea.add(mailboxExplorerPanel);
			pages.put(indexToPageKey(PAGE_EXCHANGE_GRT_MAIL_EXPLORER), mailboxExplorerPanel);
			
			ADRecoveryPointsPanel adRecvPointPanel = new ADRecoveryPointsPanel(this);
			adRecvPointPanel.addListener(onRestoreDateChanged, listener);
			centerArea.add(adRecvPointPanel);
			pages.put(indexToPageKey(PAGE_AD_RECOVERY), adRecvPointPanel);
			
			adExplorerPanel = new ActiveDirectoryExplorerPanel(this);
			adExplorerPanel.addListener(onRestoreDateChanged, listener);
			centerArea.add(adExplorerPanel);
			pages.put(indexToPageKey(PAGE_AD_EXPLORER), adExplorerPanel);
		}else if(UIContext.uiType == Utils.UI_TYPE_VSPHERE) {
			restoreSearchPanel = new VMRestoreSearchPanel(this);
			restoreSearchPanel.addListener(onRestoreDateChanged, listener);
			centerArea.add(restoreSearchPanel);
			pages.put(indexToPageKey(PAGE_SEARCH), restoreSearchPanel);
			
			vmrecvPointPanel = new VMRecoveryPointsPanel(null);
			vmrecvPointPanel.addListener(onRestoreDateChanged, listener);
			centerArea.add(vmrecvPointPanel);
			pages.put(indexToPageKey(PAGE_VM_RECOVERY), vmrecvPointPanel);
			
			
			// Exchange GRT
			ExchangeGRTRecoveryPointsPanel exchangeGRTRecvPointPanel = new ExchangeGRTRecoveryPointsPanel(this);
			exchangeGRTRecvPointPanel.addListener(onRestoreDateChanged, listener);
			centerArea.add(exchangeGRTRecvPointPanel);
			pages.put(indexToPageKey(PAGE_EXCHANGE_GRT_RECOVERY), exchangeGRTRecvPointPanel);		
			
			MailboxExplorerPanel mailboxExplorerPanel = new MailboxExplorerPanel(null);
			mailboxExplorerPanel.addListener(onRestoreDateChanged, listener);
			centerArea.add(mailboxExplorerPanel);
			pages.put(indexToPageKey(PAGE_EXCHANGE_GRT_MAIL_EXPLORER), mailboxExplorerPanel);
		}
		
		restoreSearchResult = new RestoreSearchResultPanel(this); 
		restoreSearchResult.addListener(onRestoreDateChanged, listener);
		centerArea.add(restoreSearchResult);
		pages.put(indexToPageKey(PAGE_RESTORE_SEARCH_RESULT), restoreSearchResult);
		
		buildOptionsPanel();
		buildSummaryPanel();

		prevButton = new Button(){

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
		prevButton.ensureDebugId("D5187738-7A55-4080-B88F-3004AE7C00CB");
		prevButton.setText(UIContext.Constants.restorePrevious());
		prevButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				thisWindow.PreviousPage();
			}

		});
		prevButton.setVisible(false);
		prevButton.setEnabled(false);
		addButton(prevButton);

		nextButton = new Button(){

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
		nextButton.ensureDebugId("6DD55CF3-9F78-4081-A6A2-6A0629FB4C07");
		nextButton.setText(UIContext.Constants.restoreNext());
		nextButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				thisWindow.NextPage();

			}
		});
		nextButton.setVisible(false);
		nextButton.setEnabled(false);
		addButton(nextButton);
		
		if(defaultPage != PAGE_INTRO)    ///D2D Lite Integration
		{
			// it can skip the introduction page from restore sub menu
			// so if the default page is specified, set the correct restoreType. (refer to the same logic in RestoreInstroductionPanel)
			switch (defaultPage)
			{
			case RestoreWizardContainer.PAGE_RECOVERY:
				restoreType = RestoreWizardContainer.RESTORE_BY_BROWSE;
				
				SetPage(defaultPage);
				this.setButtonsVisible(true);
				this.setButtonsEnable(true);
				
				break;
				
			case RestoreWizardContainer.PAGE_ARCHIVE_RECOVERY:
				restoreType = RestoreWizardContainer.RESTORE_BY_BROWSE_ARCHIVE;
				
				SetPage(defaultPage);
				this.setButtonsVisible(true);
				this.setButtonsEnable(true);
				
				break;
				
			case RestoreWizardContainer.PAGE_SEARCH:
				restoreType = RestoreWizardContainer.RESTORE_BY_SEARCH;
				
				SetPage(defaultPage);
				this.setButtonsVisible(true);
				this.setButtonsEnable(true);
				
				break;
				
			case RestoreWizardContainer.PAGE_VM_RECOVERY:
				restoreType = RestoreWizardContainer.RECOVER_VM;
				
				SetPage(defaultPage);
				this.setButtonsVisible(true);
				this.setButtonsEnable(true);
				
				break;
				
			case RestoreWizardContainer.PAGE_EXCHANGE_GRT_RECOVERY:
				restoreType = RestoreWizardContainer.RESTORE_BY_BROWSE_EXCHANGE_GRT;
				
				this.setButtonsVisible(true);
				this.nextButton.setEnabled(true);
				this.prevButton.setEnabled(true);				
				this.SetPage(defaultPage);	
				
				break;
			}
			
			// hide the previous button
			prevButton.setVisible(false);			
		}

		Button helpButton = new Button();
		helpButton.ensureDebugId("753C8AB4-8359-47df-8D63-C3A788F92839");
		helpButton.setText(UIContext.Constants.help());
		helpButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
			private String url;

			@Override
			public void componentSelected(ButtonEvent ce) {
				if(pageIndex == PAGE_INTRO)
					url = UIContext.externalLinks.getRetoreHelp();//first restore page
				else if(pageIndex == PAGE_RECOVERY)
					url = UIContext.externalLinks.getRetoreByRecoveryPointsHelp();//for recovery
				else if(pageIndex == PAGE_OPTIONS && helpIndex == PAGE_RECOVERY)
					url = UIContext.externalLinks.getRetoreByRecoveryPointsOptionHelp();//for recovery option
				else if(pageIndex == PAGE_SUMMARY && helpIndex == PAGE_RECOVERY)
					url = UIContext.externalLinks.getRetoreByRecoveryPointsSummaryHelp();//for recovery summary
				else if(pageIndex == PAGE_ARCHIVE_RECOVERY)
					url = UIContext.externalLinks.getRestoreByBrowseHelp();//for browse file copies
				else if(pageIndex == PAGE_OPTIONS && helpIndex == PAGE_ARCHIVE_RECOVERY)
					url = UIContext.externalLinks.getRestoreByBrowseOptionHelp();//for browse file copies	option
				else if(pageIndex == PAGE_SUMMARY && helpIndex == PAGE_ARCHIVE_RECOVERY)
					url = UIContext.externalLinks.getRestoreByBrowseSummaryHelp();//for browse file copies	summary
				else if(pageIndex == PAGE_SEARCH)
					url = UIContext.externalLinks.getRetoreByFindHelp();//find files
				else if(pageIndex == PAGE_RESTORE_SEARCH_RESULT && helpIndex == PAGE_SEARCH)
					url = UIContext.externalLinks.getRetoreByFindSearchHelp();//find files search
				else if(pageIndex == PAGE_OPTIONS && helpIndex == PAGE_SEARCH)
					url = UIContext.externalLinks.getRetoreByFindOptionHelp();//find files option
				else if(pageIndex == PAGE_SUMMARY && helpIndex == PAGE_SEARCH)
					url = UIContext.externalLinks.getRetoreByFindSummaryHelp();//find files summary
				else if(pageIndex == PAGE_VM_RECOVERY)
					url = UIContext.externalLinks.getRecoveryVMHelp();//for VM recovery
				else if(pageIndex == PAGE_VM_OPTION && helpIndex == PAGE_VM_RECOVERY)
					url = UIContext.externalLinks.getRecoveryVMHelp();//for VM recovery
				else if(pageIndex == PAGE_VM_OPTION && helpIndex == PAGE_VAPP_RECOVERY)
					url = UIContext.externalLinks.getRecoveryVAppHelp();//for vApp recovery
				else if(pageIndex == PAGE_VM_SUMMARY && (helpIndex == PAGE_VM_RECOVERY || helpIndex == PAGE_VAPP_RECOVERY))
					url = UIContext.externalLinks.getRecoveryVMSummaryHelp();//for VM and VApp recovery
				else if(pageIndex == PAGE_EXCHANGE_GRT_RECOVERY)
					url = UIContext.externalLinks.getExchangeMailHelp();//exchange mail
				else if(pageIndex == PAGE_EXCHANGE_GRT_MAIL_EXPLORER && helpIndex == PAGE_EXCHANGE_GRT_RECOVERY)
					url = UIContext.externalLinks.getExchangeMailExplorerHelp();//exchange mail explorer
				else if(pageIndex == PAGE_OPTIONS && helpIndex == PAGE_EXCHANGE_GRT_RECOVERY)
					url = UIContext.externalLinks.getExchangeMailOptionHelp();//exchange mail option
				else if(pageIndex == PAGE_SUMMARY && helpIndex == PAGE_EXCHANGE_GRT_RECOVERY)
					url = UIContext.externalLinks.getExchangeMailSummaryHelp();//exchange mail summary
				else if(pageIndex == PAGE_AD_RECOVERY)
					url = UIContext.externalLinks.getRetoreByADRecoveryPointsHelp();//for AD recovery AD
				else if(pageIndex == PAGE_AD_EXPLORER)
					url = UIContext.externalLinks.getRetoreByADExplorerHelp();//for AD recovery
				else if(pageIndex == PAGE_OPTIONS && helpIndex == PAGE_AD_RECOVERY)
					url = UIContext.externalLinks.getRetoreByADOptionHelp();//for AD recovery option
				else if(pageIndex == PAGE_SUMMARY && helpIndex == PAGE_AD_RECOVERY)
					url = UIContext.externalLinks.getRetoreByADSummaryHelp();//for AD recovery summary
				else if((thisWindow.getActivePage(pageIndex) == PAGE_OPTIONS_SQL ||
						thisWindow.getActivePage(pageIndex) == PAGE_SUMMARY_SQL)
						&& UIContext.externalLinks.getApplicationRestoreSQLHelpURL() != null)
					url = UIContext.externalLinks.getApplicationRestoreSQLHelpURL();
				else if((thisWindow.getActivePage(pageIndex) == PAGE_OPTIONS_Exch ||
						thisWindow.getActivePage(pageIndex) == PAGE_SUMMARY_Exch)
						&& UIContext.externalLinks.getApplicationRestoreExHelpURL() != null)
//					url = UIContext.externalLinks.getApplicationRestoreExHelpURL();
					url = UIContext.externalLinks.getExchangeMailHelp();
				
				//show urls according to former selection
				else if(helpIndex == PAGE_RECOVERY)
					url = UIContext.externalLinks.getRetoreByRecoveryPointsHelp();
				else if(helpIndex == PAGE_EXCHANGE_GRT_RECOVERY)
					url = UIContext.externalLinks.getExchangeMailHelp();
//					url = UIContext.externalLinks.getRetoreByRecoveryPointsHelp();
				else if(helpIndex == PAGE_SEARCH)
					url = UIContext.externalLinks.getRetoreByFindHelp();
				HelpTopics.showHelpURL(url);
			}
		});
		
		
		
			cancelButton = new Button();
			cancelButton.ensureDebugId("3C9BD592-E317-42f6-A193-ADCA767A6BC1");
			cancelButton.setText(UIContext.Constants.cancel());
			cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
	
				@Override
				public void componentSelected(ButtonEvent ce) {
					final Listener<MessageBoxEvent> messageBoxHandler = new Listener<MessageBoxEvent>() {
						public void handleEvent(MessageBoxEvent be) {
							if (be.getButtonClicked().getItemId().equals(Dialog.YES))
							{
								ClosePage(false);           ///D2D Lite Integration
							}
						}
					};
					String productName = UIContext.productNameD2D;
					if(UIContext.uiType == 1){
						productName = UIContext.productNamevSphere;
					}
					MessageBox mb = new MessageBox();
					mb.setIcon(MessageBox.WARNING);
					mb.setButtons(MessageBox.YESNO);
					mb.setTitleHtml(UIContext.Messages.messageBoxTitleWarning(productName));
					mb.setMessage(UIContext.Constants.restoreWizardExistAlert());
					mb.addCallback(messageBoxHandler);
					Utils.setMessageBoxDebugId(mb);
					mb.show();
				}
				
			});
		
			addButton(cancelButton);
		addButton(helpButton);
		
		CustomizationModel customizedModel = UIContext.customizedModel;
		Boolean isFileCopyEnabled = customizedModel.get("FileCopy");
		
		
		if(!isFileCopyEnabled)
		{
			this.add(centerArea, new RowData(1, 0.98));
		}
		else
		{	
			if(GXT.isChrome || GXT.isSafari)		
				this.add(centerArea, new RowData(0.97, 1));			
			else
				this.add(centerArea, new RowData(1, 1));
		}
		
		this.setWidth(RESTORE_WIZARD_WIDTH);
		this.setHeight(RESTORE_WIZARD_HEIGHT);

		this.addListener(Events.Hide, new Listener<BaseEvent>(){         ///D2D Lite Integration

			@Override
			public void handleEvent(BaseEvent be) {

                // release all the remained handles
				MailboxExplorerPanel explorer = (MailboxExplorerPanel) pages.get(indexToPageKey(PAGE_EXCHANGE_GRT_MAIL_EXPLORER));
				if (explorer != null)
				{
					explorer.clearMailboxExplorerPanel();
				}
				// end of release handles

				RestoreContext.destory();
			}
			
		});
	}

	private String indexToPageKey(int index) {
		return String.valueOf(index);
	}

	private void buildOptionsPanel() {
		LayoutContainer fsOptionsPanel = RestoreFactory.getInstance()
				.getRestoreOptionPanal(RestoreJobType.FileSystem, this);
		LayoutContainer sqlOptionsPanel = RestoreFactory.getInstance()
				.getRestoreOptionPanal(RestoreJobType.VSS_SQLServer, this);

		LayoutContainer exchOptionsPanel = RestoreFactory.getInstance()
				.getRestoreOptionPanal(RestoreJobType.VSS_Exchange, this);
		LayoutContainer adOptionsPanel = RestoreFactory.getInstance()
				.getRestoreOptionPanal(RestoreJobType.ActiveDirectory, this);
		
		LayoutContainer vmOptionsPanel = RestoreFactory.getInstance()
		.getRestoreOptionPanal(RestoreJobType.VM_Recovery, this);

        LayoutContainer exchGRTOptionsPanel = RestoreFactory.getInstance()
				.getRestoreOptionPanal(RestoreJobType.GRT_Exchange, this);
        
        
		centerArea.add(fsOptionsPanel);
		centerArea.add(sqlOptionsPanel);
		centerArea.add(exchOptionsPanel);
		centerArea.add(adOptionsPanel);
		centerArea.add(vmOptionsPanel);
        centerArea.add(exchGRTOptionsPanel);

		pages.put(indexToPageKey(PAGE_OPTIONS_FS), fsOptionsPanel);
		pages.put(indexToPageKey(PAGE_OPTIONS_SQL), sqlOptionsPanel);
		pages.put(indexToPageKey(PAGE_OPTIONS_Exch), exchOptionsPanel);
		pages.put(indexToPageKey(PAGE_OPTIONS_AD), adOptionsPanel);
		pages.put(indexToPageKey(PAGE_VM_OPTION), vmOptionsPanel);
        pages.put(indexToPageKey(PAGE_OPTIONS_Exch_GRT), exchGRTOptionsPanel);
	}

	private void buildSummaryPanel() {
		RestoreSummaryPanel fsSummaryPanel = RestoreFactory.getInstance()
				.getRestoreSummaryPanel(RestoreJobType.FileSystem, this);
		RestoreSummaryPanel sqlSummaryPanel = RestoreFactory.getInstance()
				.getRestoreSummaryPanel(RestoreJobType.VSS_SQLServer, this);
		RestoreSummaryPanel exchSummarysPanel = RestoreFactory.getInstance()
				.getRestoreSummaryPanel(RestoreJobType.VSS_Exchange, this);
		RestoreSummaryPanel adSummarysPanel = RestoreFactory.getInstance()
				.getRestoreSummaryPanel(RestoreJobType.ActiveDirectory, this);
		RestoreSummaryPanel vmSummarysPanel = RestoreFactory.getInstance()
		.getRestoreSummaryPanel(RestoreJobType.VM_Recovery, this);
        RestoreSummaryPanel exchGRTSummarysPanel = RestoreFactory.getInstance()
				.getRestoreSummaryPanel(RestoreJobType.GRT_Exchange, this);

		centerArea.add(fsSummaryPanel);
		centerArea.add(sqlSummaryPanel);
		centerArea.add(exchSummarysPanel);
		centerArea.add(adSummarysPanel);
		centerArea.add(vmSummarysPanel);
        centerArea.add(exchGRTSummarysPanel);
        
		pages.put(indexToPageKey(PAGE_SUMMARY_FS), fsSummaryPanel);
		pages.put(indexToPageKey(PAGE_SUMMARY_SQL), sqlSummaryPanel);
		pages.put(indexToPageKey(PAGE_SUMMARY_Exch), exchSummarysPanel);
		pages.put(indexToPageKey(PAGE_SUMMARY_AD), adSummarysPanel);
		pages.put(indexToPageKey(PAGE_VM_SUMMARY), vmSummarysPanel);
        pages.put(indexToPageKey(PAGE_SUMMARY_Exch_GRT), exchGRTSummarysPanel);
	}

	public void SetPage(int page) {
		pageIndex = page;
		int activePageIndex = getActivePage(pageIndex);
		cardlayout.setActiveItem(pages.get(indexToPageKey(activePageIndex)));
	}

	private int getActivePage(int curPageIndex) {
		if (curPageIndex == PAGE_OPTIONS) {
			curPageIndex = getOptionsPageIndex();
		} else if (curPageIndex == PAGE_SUMMARY) {
			curPageIndex = getSummaryPageIndex();
		}
		return curPageIndex;
	}

	private int getOptionsPageIndex() {
		int pageIdx = PAGE_OPTIONS_FS;
		if (restoreType == RESTORE_BY_BROWSE
				&& RestoreContext.getRestoreType() != null) {
			pageIdx = PAGE_OPTIONS * MOD
					+ RestoreContext.getRestoreType().getValue();
		}else if (restoreType == RECOVER_AD
				&& RestoreContext.getRestoreType() != null) {
			pageIdx = PAGE_OPTIONS * MOD
					+ RestoreContext.getRestoreType().getValue();
		}else if (restoreType == RECOVER_VM){
			pageIdx = PAGE_VM_OPTION;
		}		
        else if (restoreType == RESTORE_BY_BROWSE_EXCHANGE_GRT && RestoreContext.getRestoreType() != null)
		{
			pageIdx = PAGE_OPTIONS * MOD + RestoreContext.getRestoreType().getValue();
		}
		else
		{
			RestoreJobType st = RestoreContext.getRestoreSearchType();
			if (st != null)
			{
				pageIdx = PAGE_OPTIONS * MOD + st.getValue();
			}
		}
		return pageIdx;
	}

	private int getSummaryPageIndex() {
		int pageIdx = PAGE_SUMMARY_FS;
		if (restoreType == RESTORE_BY_BROWSE
				&& RestoreContext.getRestoreType() != null) {
			pageIdx = PAGE_SUMMARY * MOD
					+ RestoreContext.getRestoreType().getValue();
		}else if (restoreType == RECOVER_AD){
			pageIdx = PAGE_SUMMARY * MOD
					+ RestoreContext.getRestoreType().getValue();
		}else if (restoreType == RECOVER_VM){
			pageIdx = PAGE_VM_SUMMARY;
		}
	    else if (restoreType == RESTORE_BY_BROWSE_EXCHANGE_GRT && RestoreContext.getRestoreType() != null)
		{
			pageIdx = PAGE_SUMMARY * MOD + RestoreContext.getRestoreType().getValue();
		}
		return pageIdx;
	}

	public void NextPage() {

		RestoreValidator validator = getWizPage();

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

				if (!result) {
					return;
				}
				
				if(pageIndex == PAGE_EXCHANGE_GRT_RECOVERY)
				{	
					nextButton.setEnabled(grtMailExplorer);
					prevButton.setVisible(true);					
				}
				else if(defaultPage != PAGE_INTRO)    ///D2D Lite Integration
				{
					setButtonsVisible(true);
				}

				switch (pageIndex) {
				case PAGE_INTRO:
				{
					if(restoreType == RESTORE_BY_BROWSE)
					{
						restoreType = RestoreWizardContainer.RESTORE_BY_BROWSE;
						SetPage(RestoreWizardContainer.PAGE_RECOVERY);
					}
					else if(restoreType == RESTORE_BY_SEARCH)
					{
						restoreType = RestoreWizardContainer.RESTORE_BY_SEARCH;
						SetPage(RestoreWizardContainer.PAGE_SEARCH);
						
						//restoreSearchPanel.getFindText().focus();
					}
					else if(restoreType == RESTORE_BY_BROWSE_ARCHIVE)
					{
						SetPage(RestoreWizardContainer.PAGE_ARCHIVE_RECOVERY);
						restoreArchiveBrowsePanel.RefreshArchiveDestinationTree();
					}
					else if(restoreType == RECOVER_AD){
						restoreType = RestoreWizardContainer.RECOVER_AD;
						SetPage(RestoreWizardContainer.PAGE_AD_RECOVERY);
					}
					setButtonsVisible(true);
					prevButton.setEnabled(true);
					break;
				}
				
				case PAGE_OPTIONS: {
					RestoreOptionsPanel panel = (RestoreOptionsPanel) pages
							.get(indexToPageKey(getOptionsPageIndex()));
					panel.processOptions();
					pageIndex = PAGE_SUMMARY;
					showNextPage();
					break;
				}
				case PAGE_RECOVERY: {
					RecoveryPointsPanel panel = (RecoveryPointsPanel) pages
							.get(indexToPageKey(PAGE_RECOVERY));
					RestoreContext.setRestoreRecvPointSources(panel.GetSelectedNodes());
					RestoreContext.setRestoreRecvPointTreeStore(panel.getTreeStore());
					RestoreContext.setRecoveryPointModel(panel.getSelectedRecoveryPoint());
					RestoreContext.setRootItemMap(panel.getRootItemMap());					
					RestoreContext.isBackupToDataStore = panel.getRpsDataStore() != null; 
					pageIndex = PAGE_OPTIONS;
					helpIndex = PAGE_RECOVERY;
					showNextPage();
					break;

				}
				case PAGE_AD_RECOVERY: {
					ADRecoveryPointsPanel panel = (ADRecoveryPointsPanel) pages.get(indexToPageKey(PAGE_AD_RECOVERY));
					RestoreContext.setRecoveryPointModel(panel.getSelectedRecoveryPoint());
					RestoreContext.setRootItemMap(panel.getRootItemMap());
					RestoreContext.isBackupToDataStore = panel.getRpsDataStore() != null;
					ActiveDirectoryExplorerPanel explorer = (ActiveDirectoryExplorerPanel) pages.get(indexToPageKey(PAGE_AD_EXPLORER));
					if(!panel.getSessionPath().equals(explorer.getDestination())||panel.getSelectedSessionID()!=(explorer.getSessionNumber())){
						//recovery point changed
						explorer.setChanged(true);
						explorer.clear();
						explorer.init(panel.GetSelectedNodes().get(0),panel.getRootItemMap().get(panel.GetSelectedNodes().get(0)));
					}else{
						explorer.setChanged(false);
					}
					
					pageIndex = PAGE_AD_EXPLORER;
					helpIndex = PAGE_AD_RECOVERY;
					
					showNextPage();
					break;

				}
				case PAGE_AD_EXPLORER:{
					ActiveDirectoryExplorerPanel panel = (ActiveDirectoryExplorerPanel) pages.get(indexToPageKey(PAGE_AD_EXPLORER));
					RestoreContext.setRestoreRecvPointSources(panel.getSelectedNodes());
					pageIndex = PAGE_OPTIONS;
					helpIndex = PAGE_AD_RECOVERY;
					showNextPage();
					break;
				}
				case PAGE_VM_RECOVERY:{
					VMRecoveryPointsPanel panel = (VMRecoveryPointsPanel) pages
					.get(indexToPageKey(PAGE_VM_RECOVERY));
					
					BackupVMModel backupVMModel = panel.getSelectedBackupVMModel();
					RestoreContext.setRecoveryPointModel(panel.getSelectedRecoveryPoint());
					RestoreContext.setBackupVMModel(backupVMModel);
					pageIndex = PAGE_VM_OPTION;
					helpIndex = PAGE_VM_RECOVERY;
					if (backupVMModel != null && BackupVMModel.Type.VMware_VApp.ordinal() == backupVMModel.getVMType()) {
						helpIndex = PAGE_VAPP_RECOVERY;
					}
					showNextPage();
					break;
				}
				case PAGE_VM_OPTION :{
					VMRecoveryOptionsPanel panel = (VMRecoveryOptionsPanel) pages
					.get(indexToPageKey(getOptionsPageIndex()));
			        
					panel.processOptions();
					pageIndex = PAGE_VM_SUMMARY;
					showNextPage();
					break;
				}
				case PAGE_SEARCH: {
					RestoreSearchPanel panel = (RestoreSearchPanel) pages
							.get(indexToPageKey(PAGE_SEARCH));
					pageIndex = PAGE_RESTORE_SEARCH_RESULT;
					helpIndex = PAGE_SEARCH;
					showNextPage();
					break;
				}
				
				case PAGE_RESTORE_SEARCH_RESULT: {
					RestoreSearchResultPanel panel = (RestoreSearchResultPanel) pages
							.get(indexToPageKey(PAGE_RESTORE_SEARCH_RESULT));
					RestoreContext.setRestoreSearchSources(panel
							.getSelectedBackupNodes());
			
					RestoreContext.setRestoreArchiveSearchSources(panel
							.getSelectedArchiveNodes());
					panel.nextPage();
					pageIndex = PAGE_OPTIONS;
					helpIndex = PAGE_SEARCH;
					showNextPage();
					break;
				}
                case PAGE_EXCHANGE_GRT_RECOVERY: {
					ExchangeGRTRecoveryPointsPanel panel = (ExchangeGRTRecoveryPointsPanel) pages
							.get(indexToPageKey(PAGE_EXCHANGE_GRT_RECOVERY));

					MailboxExplorerPanel explorer = (MailboxExplorerPanel) pages
							.get(indexToPageKey(PAGE_EXCHANGE_GRT_MAIL_EXPLORER));
					
					// initialize the panel					
					MailboxExplorerContext.initExplorer(explorer, panel);					
					
					RestoreContext.setRecoveryPointModel(panel.getSelectedRecoveryPoint());					
					pageIndex = PAGE_EXCHANGE_GRT_MAIL_EXPLORER;
					helpIndex = PAGE_EXCHANGE_GRT_RECOVERY;
					showNextPage();
					break;

				}
				case PAGE_EXCHANGE_GRT_MAIL_EXPLORER: {
					MailboxExplorerPanel explorer = (MailboxExplorerPanel) pages
					.get(indexToPageKey(PAGE_EXCHANGE_GRT_MAIL_EXPLORER));	
					
					ExchangeGRTRecoveryPointsPanel panel = (ExchangeGRTRecoveryPointsPanel) pages
							.get(indexToPageKey(PAGE_EXCHANGE_GRT_RECOVERY));

					RestoreContext.setRestoreRecvPointSources(panel.GetSelectedNodes());
					RestoreContext.setRestoreRecvPointTreeStore(panel.getTreeStore());
					pageIndex = PAGE_OPTIONS;
					helpIndex = PAGE_EXCHANGE_GRT_RECOVERY;
					showNextPage();
					break;

				}
				case PAGE_ARCHIVE_RECOVERY:
				{
					RestoreArchiveBrowsePanel archiveBrowsePanel = (RestoreArchiveBrowsePanel) pages
					.get(indexToPageKey(PAGE_ARCHIVE_RECOVERY));
					
					//save the nodes selected in browse page.
					RestoreContext.setRestoreSelectedArchiveNodes(archiveBrowsePanel.GetSelectedNodes());
					
					RestoreContext.setRestoreArchiveTreeStore(archiveBrowsePanel.getTreeStore());
					
					pageIndex = PAGE_OPTIONS;
					helpIndex = PAGE_ARCHIVE_RECOVERY;
					showNextPage();
					break;
				}
				case PAGE_SUMMARY: {
					handleSummary();
					break;

				}
				case PAGE_VM_SUMMARY: {
					handleVMSummary();
					break;
					
				}
				}
			}
		};

		if (validator != null) {
			String productName = UIContext.productNameD2D;
			if(UIContext.uiType == 1){
				productName = UIContext.productNamevSphere;
			}
			validatingBox = MessageBox.wait(UIContext.Messages.messageBoxTitleInformation(productName), UIContext.Constants
					.validating(), "");
			Utils.setMessageBoxDebugId(validatingBox);
			validator.validate(cb);
		} else {
			cb.onSuccess(Boolean.TRUE);
		}
	}
	//wanqi06: close validatingBox
	public void closeValidate() {
		if(validatingBox != null) {
			validatingBox.close();
		}
	}
	
	

	private MessageBox validatingBox = null;

	private RestoreValidator getWizPage() {
		switch (pageIndex) {
		case PAGE_OPTIONS: {
			RestoreOptionsPanel panel = (RestoreOptionsPanel) pages
					.get(indexToPageKey(getOptionsPageIndex()));
			return panel;
		}
		case PAGE_RECOVERY: {
			RecoveryPointsPanel panel = (RecoveryPointsPanel) pages
					.get(indexToPageKey(PAGE_RECOVERY));
			return panel;
		}
		case PAGE_AD_RECOVERY: {
			ADRecoveryPointsPanel panel = (ADRecoveryPointsPanel) pages
					.get(indexToPageKey(PAGE_AD_RECOVERY));
			return panel;
		}
		case PAGE_AD_EXPLORER: {
			ActiveDirectoryExplorerPanel panel = (ActiveDirectoryExplorerPanel)pages.get(indexToPageKey(PAGE_AD_EXPLORER));
			return panel;
		}
		case PAGE_VM_OPTION:{
			VMRecoveryOptionsPanel panel=(VMRecoveryOptionsPanel)pages.get(indexToPageKey(PAGE_VM_OPTION));
			return panel;
		}
		case PAGE_VM_RECOVERY:{
			VMRecoveryPointsPanel panel = (VMRecoveryPointsPanel) pages
					.get(indexToPageKey(PAGE_VM_RECOVERY));
			return panel;

		}
		case PAGE_SEARCH: {
			RestoreSearchPanel panel = (RestoreSearchPanel) pages
					.get(indexToPageKey(PAGE_SEARCH));
			return panel;

		}
	    case PAGE_EXCHANGE_GRT_RECOVERY: {
			ExchangeGRTRecoveryPointsPanel panel = (ExchangeGRTRecoveryPointsPanel) pages
					.get(indexToPageKey(PAGE_EXCHANGE_GRT_RECOVERY));
			return panel;

		}
		case PAGE_EXCHANGE_GRT_MAIL_EXPLORER: {
			MailboxExplorerPanel panel = (MailboxExplorerPanel) pages
					.get(indexToPageKey(PAGE_EXCHANGE_GRT_MAIL_EXPLORER));
			return panel;

		}case PAGE_ARCHIVE_RECOVERY:
		{
			//return null;
			RestoreArchiveBrowsePanel panel = (RestoreArchiveBrowsePanel) pages.get(indexToPageKey(PAGE_ARCHIVE_RECOVERY));
			return panel;
		}
		case PAGE_RESTORE_SEARCH_RESULT:
		{
			RestoreSearchResultPanel panel = (RestoreSearchResultPanel) pages.get(indexToPageKey(PAGE_RESTORE_SEARCH_RESULT));
			return panel;
		}
		}
		return null;
	}

	private void showNextPage() {
		
		SetPage(pageIndex);
        
		if (PAGE_VM_OPTION == pageIndex){
			VMRecoveryOptionsPanel panel = ((VMRecoveryOptionsPanel) pages
					.get(indexToPageKey(getOptionsPageIndex())));
			panel.updateConflictLabel();
		}
		
		// Last Page Change Next Button To Finish
		if (pageIndex == PAGE_SUMMARY || pageIndex== PAGE_VM_SUMMARY) {
			nextButton.setText(UIContext.Constants.restoreFinish());
			RestoreSummaryPanel panel = ((RestoreSummaryPanel) pages
					.get(indexToPageKey(getSummaryPageIndex())));
			panel.updateDestinationLabel();
			panel.updateOptionsLabel();

			if (restoreType == RESTORE_BY_BROWSE) {
				panel.updateRecvPointRestoreSource();
			}
			if(restoreType == RECOVER_AD){
				panel.updateRecvPointRestoreSource();
			}
			if(restoreType == RESTORE_BY_BROWSE_ARCHIVE)
			{
				panel.updateArchiveRestoreSource();
			}
            else if (restoreType == RESTORE_BY_BROWSE_EXCHANGE_GRT)
			{
				panel.updateRecvPointRestoreSource();
			}
            else if (restoreType == RESTORE_BY_SEARCH){
				panel.updateSearchSource();
			}
		}
		
		pages.get(indexToPageKey(getActivePage(pageIndex))).repaint();
		this.repaint();
	}
	
	private void handleVMSummary(){
		VMRecoveryPointsPanel panel = ((VMRecoveryPointsPanel) pages.get(indexToPageKey(PAGE_VM_RECOVERY)));
		handleVMSummary(model, panel, null);
		
		if (model.childRestoreJobList != null && model.childRestoreJobList.size() > 0) {
			Map<String, RecoveryPointModel> childRPMap = panel.getSelectedRecoveryPoint().childVMRecoveryPointModelMap;
			for (RestoreJobModel childModel: model.childRestoreJobList) {
				String childInsUuid = childModel.getVMInstanceUUID();
				RecoveryPointModel childRP = childRPMap.get(childInsUuid);
				SessionPathNumberPair childSessionPathPair = new SessionPathNumberPair(childInsUuid, childModel.getDestinationPath(), childRP.getSessionID());
				handleVMSummary(childModel, panel, childSessionPathPair);
				childModel.setDestinationPath(null);
			}
		}
		
		nextButton.disable();
		service.submitRecoveryVMJob(model, new BaseAsyncCallback<Integer>() {
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				nextButton.setEnabled(true);
				prevButton.setEnabled(true);
			}

			@Override
			public void onSuccess(Integer result) {
				// Check the result, Notify the user that the
				// restore has been successfully submitted
				//GWT.log("Restore Successfully", null);
				if (result == 0) {
					thisWindow.cancel = false;
					ClosePage(true);      
					String title = "";
					if(UIContext.uiType ==1){
						title = UIContext.Messages.messageBoxTitleError(UIContext.productNamevSphere);
					}else{
						title = UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D);
					}
					Info.display(title, UIContext.Constants
							.restoreJobSubmittedSuccessfully());
				} else {
					MessageBox msg = new MessageBox();					
					String title = "";
					if(UIContext.uiType ==1){
						title = UIContext.Messages.messageBoxTitleError(UIContext.productNamevSphere);
					}else{
						title = UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D);
					}
					msg.setTitleHtml(title);
					msg.setMessage(UIContext.Constants
							.restoreJobFailedToSubmit());
					msg.setIcon(MessageBox.ERROR);
					Utils.setMessageBoxDebugId(msg);
					msg.show();

					nextButton.setEnabled(true);
					prevButton.setEnabled(true);
				}
			}
		});
	}

	private void handleVMSummary(RestoreJobModel model, VMRecoveryPointsPanel panel, SessionPathNumberPair childSessionInfo){
		String sessionPath = panel.getSessionPath();
		int selectedSessionId = panel.getSelectedSessionID();
		if (childSessionInfo != null) {
			sessionPath = childSessionInfo.getSessionPath();
			selectedSessionId = childSessionInfo.getSessionId();
		}
		
		model.setSessionPath(sessionPath);
		String userName = panel.getUserName();
		String pwd = panel.getPassword();
		if (userName != null && userName.trim().length() > 0) {
			model.setUserName(userName);
			if (pwd != null) {
				model.setPassword(pwd);
			}
		}
		//for vsphere
		if(UIContext.uiType == 1 && UIContext.backupVM !=null){
			model.setJobLauncher(JobLauncher.VSPHERE.getValue());
			model.setVMInstanceUUID(UIContext.backupVM.getVmInstanceUUID());
		}else{
			model.setJobLauncher(JobLauncher.D2D.getValue());
		}
		model.recoverVMOption.setSessionNumber(selectedSessionId);
		if (RestoreContext.getBackupVMModel().getVMType() == BackupVMModel.Type.VMware.ordinal()) {
			model.setJobType(JOBTYPE_VMWARE_VM_RECOVERY);
		} else if (RestoreContext.getBackupVMModel().getVMType() == BackupVMModel.Type.HyperV.ordinal()) {
			model.setJobType(JOBTYPE_HYPERV_VM_RECOVERY);
		} else if (RestoreContext.getBackupVMModel().getVMType() == BackupVMModel.Type.VMware_VApp.ordinal()) {
			model.setJobType(JOBTYPE_VMWARE_VAPP_RECOVERY);
		} else if (RestoreContext.getBackupVMModel().getVMType() == BackupVMModel.Type.HyperV_Cluster.ordinal()) {
			model.setJobType(JOBTYPE_HYPERV_CLUSTER_RECOVERY);
		}
		model.setRpsDataStoreName(panel.getRpsDataStore());
		model.setRpsPolicy(panel.getRpsPolicy());
		model.sourceRPSHost = panel.getSrcRPSHost();
		model.setRpsDataStoreDisplayName(panel.getRpsDSDisplayName());
	}
	
	public RestoreArchiveJobModel cloneModelData()
	{
		RestoreArchiveJobModel m_LocalObj = new RestoreArchiveJobModel();		
		Iterator<String> iterator = archiveJobModel.getPropertyNames().iterator();
        while (iterator.hasNext()) 
        {
            String key = iterator.next();
            m_LocalObj.set(key, archiveJobModel.get(key));
        }
      // m_LocalObj.setEncrpytionPassword(archiveJobModel.getEncrpytionPassword());
      // m_LocalObj.setFileSystemOption(archiveJobModel.getFileSystemOption());
	    return m_LocalObj;
	}
	
	public RestoreArchiveJobModel getArchiveRestoreJobModel()
	{
			
		
		// Create a proper RestoreJobModel object
		model.setSessionPath(sessionPath);
		model.listOfRestoreJobNodes = new ArrayList<RestoreJobNodeModel>();
		RestoreArchiveJobModel m_LocalObj = cloneModelData();

		if(restoreType == RESTORE_BY_BROWSE_ARCHIVE)
		{
			RestoreArchiveBrowsePanel archiveBrowsePanel = (RestoreArchiveBrowsePanel)pages.get(indexToPageKey(PAGE_ARCHIVE_RECOVERY));
			if(archiveBrowsePanel.archiveDestinationInfo.getArchiveToDrive())
			{
				m_LocalObj.setArchiveDestType(4); // Disk file system
				m_LocalObj.setArchiveDiskInfo(archiveBrowsePanel.archiveDestinationInfo.getArchiveDiskDestInfoModel());
			}
			else if(archiveBrowsePanel.archiveDestinationInfo.getArchiveToCloud())
			{
				m_LocalObj.setArchiveDestType(archiveBrowsePanel.archiveDestinationInfo.getCloudConfigModel().getcloudVendorType());//cloud type amazon s3 
				m_LocalObj.setArchiveCloudInfo(archiveBrowsePanel.archiveDestinationInfo.getCloudConfigModel());
				if(archiveBrowsePanel.archiveDestinationInfo.getCloudConfigModel().getcloudVendorType() == 0L)
				{
					m_LocalObj.setRRSFlag(archiveBrowsePanel.archiveDestinationInfo.getCloudConfigModel().getrrsFlag());
				}
				else
				{
					m_LocalObj.setRRSFlag(0L);
				}
			}
			
			HashMap<ArchiveGridTreeNode, ArchiveRestoreDestinationVolumesModel> volumesMap = archiveBrowsePanel.getRestoreArchiveRootItemsMap();
			
			//RestoreJobArchiveVolumeNodeModel[] volumes = new RestoreJobArchiveVolumeNodeModel[volumesMap.size()];
			Iterator iterator = volumesMap.entrySet().iterator();
			
			int iSelectedVolumeCount = 0;
			while(iterator.hasNext())
			{
				Map.Entry Temp  = (Map.Entry) iterator.next();
				if(((ArchiveGridTreeNode)Temp.getKey()).getChecked())
				{
					iSelectedVolumeCount++;
				}
			}
			
			int iVolumeIndex = 0;
			Iterator iterator1 = volumesMap.entrySet().iterator();
			RestoreJobArchiveVolumeNodeModel[] selectedVolumes = new RestoreJobArchiveVolumeNodeModel[iSelectedVolumeCount];
			while(iterator1.hasNext())
			{
				Map.Entry Temp  = (Map.Entry) iterator1.next();
				
				if(!((ArchiveGridTreeNode)Temp.getKey()).getChecked())
				{
					continue;
				}
				
				ArchiveRestoreDestinationVolumesModel model = (ArchiveRestoreDestinationVolumesModel)Temp.getValue();
				RestoreJobArchiveVolumeNodeModel volumeNode = new RestoreJobArchiveVolumeNodeModel();
				volumeNode.setdestVolumName(model.getDisplayName());
				
				if(RestoreContext.getRestoreSelectedArchiveNodes() == null)
				{
					RestoreArchiveBrowsePanel archiveBrowsePanelobj = (RestoreArchiveBrowsePanel) pages
					.get(indexToPageKey(PAGE_ARCHIVE_RECOVERY));			
				
					RestoreContext.setRestoreSelectedArchiveNodes(archiveBrowsePanelobj.GetSelectedNodes());					
					RestoreContext.setRestoreArchiveTreeStore(archiveBrowsePanelobj.getTreeStore());
				}
                
				RestoreJobArchiveItemNodeModel[] items = ConvertSelectedArchiveNodes(model.getDisplayName(),RestoreContext.getRestoreSelectedArchiveNodes());
				
				if(items != null)
				{
					volumeNode.ArchiveItemsList = items;
				}
				//volumes[iVolumeIndex++] = volumeNode;
				selectedVolumes[iVolumeIndex++] = volumeNode;
			}
			
			m_LocalObj.listofArchiveVolumes = selectedVolumes;
			m_LocalObj.setJobType(RestoreJobType.FileSystem.getValue());
			
			m_LocalObj.setSessionPath(archiveBrowsePanel.archiveDestinationInfo.getHostName());//setting the hostname
			if(UIContext.uiType == 1 && UIContext.backupVM !=null){
				m_LocalObj.setProductType(JobLauncher.VSPHERE.getValue());
				m_LocalObj.setVMInstanceUUID(UIContext.backupVM.getVmInstanceUUID());
			}else if(archiveBrowsePanel.archiveDestinationInfo.getArchiveToRPS()!=null && archiveBrowsePanel.archiveDestinationInfo.getArchiveToRPS()){
				m_LocalObj.setProductType(JobLauncher.RPS.getValue());
			}else
				m_LocalObj.setProductType(JobLauncher.D2D.getValue());
		}
        else  if(restoreType == RESTORE_BY_SEARCH)
        {
			RestoreSearchPanel searchPanel = ((RestoreSearchPanel) pages
					.get(indexToPageKey(PAGE_SEARCH)));
			
			
			if(restoreSearchPanel.bSearchArchives)
			{
				if(searchPanel.archiveDestinationInfo.getArchiveToDrive())
				{
					m_LocalObj.setArchiveDestType(4); // DISK file system
					m_LocalObj.setArchiveDiskInfo(searchPanel.archiveDestinationInfo.getArchiveDiskDestInfoModel());
				}
				else if(searchPanel.archiveDestinationInfo.getArchiveToCloud())
				{
					m_LocalObj.setArchiveDestType(searchPanel.archiveDestinationInfo.getCloudConfigModel().getcloudVendorType());//cloud type amazon s3 
					m_LocalObj.setArchiveCloudInfo(searchPanel.archiveDestinationInfo.getCloudConfigModel());
					if(searchPanel.archiveDestinationInfo.getCloudConfigModel().getcloudVendorType() == 0L)
					{
						m_LocalObj.setRRSFlag(searchPanel.archiveDestinationInfo.getCloudConfigModel().getrrsFlag());
					}
					else
					{
						m_LocalObj.setRRSFlag(0L);
					}
					
				}
				
				Map<String, List<RestoreJobArchiveItemNodeModel>> selectedArchiveItems = ConvertSelectedArchiveSearchNodes(restoreSearchResult.getSelectedArchiveNodes());
				
				if(selectedArchiveItems != null)
				{
				
					RestoreJobArchiveVolumeNodeModel[] selectedVolumes = new RestoreJobArchiveVolumeNodeModel[selectedArchiveItems.size()];
					
					Iterator itr = selectedArchiveItems.keySet().iterator();
					int iVolumeIndex = 0;
					while(itr.hasNext())
					{
						String strDriveName = (String)(itr.next());
						
						RestoreJobArchiveVolumeNodeModel volumeNode = new RestoreJobArchiveVolumeNodeModel();
						volumeNode.setdestVolumName(strDriveName);
						
						List<RestoreJobArchiveItemNodeModel> items = selectedArchiveItems.get(strDriveName);
						
						if(items.size() != 0)
						{
							RestoreJobArchiveItemNodeModel[] arrayOfItems = items.toArray(new RestoreJobArchiveItemNodeModel[0]);
						
							volumeNode.ArchiveItemsList = arrayOfItems;
						}
						selectedVolumes[iVolumeIndex++] = volumeNode;
					}
	
					m_LocalObj.listofArchiveVolumes = selectedVolumes;
					m_LocalObj.setJobType(RestoreJobType.FileSystem.getValue());
					
					m_LocalObj.setSessionPath(searchPanel.archiveDestinationInfo.getHostName());//setting the hostname
					
				}
			}
		}
		
		return m_LocalObj;
	}
	
	@SuppressWarnings("unchecked")
	private void handleSummary() {

		nextButton.setEnabled(false);
		prevButton.setEnabled(false);
		cancelButton.setEnabled(false);

		// Create a proper RestoreJobModel object
		model.setSessionPath(sessionPath);
		model.listOfRestoreJobNodes = new ArrayList<RestoreJobNodeModel>();

		if(restoreType == RESTORE_BY_BROWSE_ARCHIVE)
		{
			RestoreArchiveBrowsePanel archiveBrowsePanel = (RestoreArchiveBrowsePanel)pages.get(indexToPageKey(PAGE_ARCHIVE_RECOVERY));
			if(archiveBrowsePanel.archiveDestinationInfo.getArchiveToDrive())
			{
				archiveJobModel.setArchiveDestType(4); // Disk file system
				archiveJobModel.setArchiveDiskInfo(archiveBrowsePanel.archiveDestinationInfo.getArchiveDiskDestInfoModel());
			}
			else if(archiveBrowsePanel.archiveDestinationInfo.getArchiveToCloud())
			{
				archiveJobModel.setArchiveDestType(archiveBrowsePanel.archiveDestinationInfo.getCloudConfigModel().getcloudVendorType());//cloud type amazon s3 
				archiveJobModel.setArchiveCloudInfo(archiveBrowsePanel.archiveDestinationInfo.getCloudConfigModel());
				if(archiveBrowsePanel.archiveDestinationInfo.getCloudConfigModel().getcloudVendorType() == 0L)
				{
					archiveJobModel.setRRSFlag(archiveBrowsePanel.archiveDestinationInfo.getCloudConfigModel().getrrsFlag());
				}
				else
				{
					archiveJobModel.setRRSFlag(0L);
				}
			}
			
			HashMap<ArchiveGridTreeNode, ArchiveRestoreDestinationVolumesModel> volumesMap = archiveBrowsePanel.getRestoreArchiveRootItemsMap();
			
			//RestoreJobArchiveVolumeNodeModel[] volumes = new RestoreJobArchiveVolumeNodeModel[volumesMap.size()];
			Iterator iterator = volumesMap.entrySet().iterator();
			
			int iSelectedVolumeCount = 0;
			while(iterator.hasNext())
			{
				Map.Entry Temp  = (Map.Entry) iterator.next();
				if(((ArchiveGridTreeNode)Temp.getKey()).getChecked())
				{
					iSelectedVolumeCount++;
				}
			}
			
			int iVolumeIndex = 0;
			Iterator iterator1 = volumesMap.entrySet().iterator();
			RestoreJobArchiveVolumeNodeModel[] selectedVolumes = new RestoreJobArchiveVolumeNodeModel[iSelectedVolumeCount];
			while(iterator1.hasNext())
			{
				Map.Entry Temp  = (Map.Entry) iterator1.next();
				
				if(!((ArchiveGridTreeNode)Temp.getKey()).getChecked())
				{
					continue;
				}
				
				ArchiveRestoreDestinationVolumesModel model = (ArchiveRestoreDestinationVolumesModel)Temp.getValue();
				RestoreJobArchiveVolumeNodeModel volumeNode = new RestoreJobArchiveVolumeNodeModel();
				volumeNode.setdestVolumName(model.getDisplayName());

				RestoreJobArchiveItemNodeModel[] items = ConvertSelectedArchiveNodes(model.getDisplayName(),RestoreContext.getRestoreSelectedArchiveNodes());
				
				if(items != null)
				{
					volumeNode.ArchiveItemsList = items;
				}
				//volumes[iVolumeIndex++] = volumeNode;
				selectedVolumes[iVolumeIndex++] = volumeNode;
			}
			
			archiveJobModel.listofArchiveVolumes = selectedVolumes;
			archiveJobModel.setJobType(RestoreJobType.FileSystem.getValue());
			
			archiveJobModel.setSessionPath(archiveBrowsePanel.archiveDestinationInfo.getHostName());//setting the hostname
			
			if(UIContext.uiType == Utils.UI_TYPE_VSPHERE && UIContext.backupVM !=null){
				archiveJobModel.setProductType(JobLauncher.VSPHERE.getValue());
				archiveJobModel.setVMInstanceUUID(UIContext.backupVM.getVmInstanceUUID());
			}else if(archiveBrowsePanel.archiveDestinationInfo.getArchiveToRPS()!=null&&archiveBrowsePanel.archiveDestinationInfo.getArchiveToRPS()){
				archiveJobModel.setProductType(JobLauncher.RPS.getValue());
			}else
				archiveJobModel.setProductType(JobLauncher.D2D.getValue());
			
			//set the datastore path in the case of rps filecopy as the datastore dest is being used for filecopy catalog
			//if(archiveBrowsePanel.archiveDestinationInfo.getArchiveToRPS()!=null&&archiveBrowsePanel.archiveDestinationInfo.getArchiveToRPS()){
				archiveJobModel.setCatalogFolderPath(archiveBrowsePanel.archiveDestinationInfo.getCatalogPath());
				archiveJobModel.setCatalogFolderUser(archiveBrowsePanel.archiveDestinationInfo.getCatalogFolderUser());
				archiveJobModel.setCatalogFolderPassword(archiveBrowsePanel.archiveDestinationInfo.getCatalogFolderPassword());
			//}
			
			
			submitD2DArchivesRestoreJob(archiveJobModel);
		}
		else if (restoreType == RESTORE_BY_BROWSE) {
			RecoveryPointsPanel panel = getRecoveryPointsPanel();
			model.setSessionPath(panel.getSessionPath());
			String userName = panel.getUserName();
			String pwd = panel.getPassword();
			if (userName != null && userName.trim().length() > 0) {
				model.setUserName(userName);
				if (pwd != null) {
					model.setPassword(pwd);
				}
			}

			RestoreContext.setRestoreRecvPointSources(panel.GetSelectedNodes());

			model.setJobType(RestoreContext.getRestoreType().getValue());
			//If this is one vm backup session and restore to original, we will convert restore job type to new one to let backend know this.
			if(RestoreContext.getVMModel()!=null){
				int restoreType = model.getJobType();
				if(model.getDestType() == DestType.OrigLoc.getValue()){
					if(restoreType == RestoreJobType.FileSystem.getValue()){
						model.setJobType(RestoreJobType.VM_RESTORE_FILE_TO_ORIGINAL.getValue());
					}else if (restoreType == RestoreJobType.VSS_Exchange.getValue()){
						model.setJobType(RestoreJobType.VM_RESTORE_EXCHANGE_TO_ORIGINAL.getValue());
					}else if (restoreType == RestoreJobType.VSS_SQLServer.getValue()){
						model.setJobType(RestoreJobType.VM_RESTORE_SQLSERVER_TO_ORIGINAL.getValue());
					}
				}else if(model.getDestType() == DestType.AlterVM.getValue()){
					if(restoreType == RestoreJobType.FileSystem.getValue()){
						model.setJobType(RestoreJobType.VM_RESTORE_FILE_TO_ALTER.getValue());
					}else if (restoreType == RestoreJobType.VSS_Exchange.getValue()){
						model.setJobType(RestoreJobType.VM_RESTORE_EXCHANGE_TO_ALTER.getValue());
					}else if (restoreType == RestoreJobType.VSS_SQLServer.getValue()){
						model.setJobType(RestoreJobType.VM_RESTORE_SQLSERVER_TO_ALTER.getValue());
					}
				}
				
			}

			// Restore by Browse can only have one RestoreNode
			RestoreJobNodeModel nodeModel = new RestoreJobNodeModel();
			nodeModel.setSessionNumber(panel.getSelectedSessionID());
			RestoreUtil.processRestoreSource(nodeModel, model.getJobType());

			model.listOfRestoreJobNodes.add(nodeModel);
			//for vsphere
			if(UIContext.uiType == Utils.UI_TYPE_VSPHERE && UIContext.backupVM !=null){
				model.setJobLauncher(JobLauncher.VSPHERE.getValue());
				model.setVMInstanceUUID(UIContext.backupVM.getVmInstanceUUID());
			}else{
				model.setJobLauncher(JobLauncher.D2D.getValue());
			}
			model.sourceRPSHost = panel.getSrcRPSHost();
			model.setRpsPolicy(panel.getRpsPolicy());
			model.setRpsDataStoreName(panel.getRpsDataStore());
			model.setRpsDataStoreDisplayName(panel.getRpsDSDisplayName());
			submitD2DBackupsRestoreJob(model);
		}
        else if (restoreType == RESTORE_BY_BROWSE_EXCHANGE_GRT) {
			ExchangeGRTRecoveryPointsPanel panel = ((ExchangeGRTRecoveryPointsPanel) pages
					.get(indexToPageKey(PAGE_EXCHANGE_GRT_RECOVERY)));
			model.setSessionPath(panel.getSessionPath());
			String userName = panel.getUserName();
			String pwd = panel.getPassword();
			if (userName != null && userName.trim().length() > 0) {
				model.setUserName(userName);
				if (pwd != null) {
					model.setPassword(pwd);
				}
			}

			RestoreContext.setRestoreRecvPointSources(panel.GetSelectedNodes());

			model.setJobType(RestoreContext.getRestoreType().getValue());

			// Restore by Browse can only have one RestoreNode
			RestoreJobNodeModel nodeModel = new RestoreJobNodeModel();
			nodeModel.setSessionNumber(panel.getSelectedSessionID());
			RestoreUtil.processRestoreSource(nodeModel, model.getJobType());

			model.listOfRestoreJobNodes.add(nodeModel);
			
			//for vsphere
			if(UIContext.uiType == Utils.UI_TYPE_VSPHERE && UIContext.backupVM !=null){
				model.setJobLauncher(JobLauncher.VSPHERE.getValue());
				model.setVMInstanceUUID(UIContext.backupVM.getVmInstanceUUID());
			}else{
				model.setJobLauncher(JobLauncher.D2D.getValue());
			}
			
			// liuwe05 2011-04-11 fix Issue: 20118552    Title: RESTORE WINDOW NOT CLOSING
			// close the opened files here
			MailboxExplorerPanel explorer = this.getMailboxExplorerPanel();
			if (explorer != null)
			{
				explorer.clearMailboxExplorerPanel();
			}
			model.sourceRPSHost = panel.getSrcRPSHost();
			model.setRpsPolicy(panel.getRpsPolicy());
			model.setRpsDataStoreName(panel.getRpsDataStore());
			model.setRpsDataStoreDisplayName(panel.getRpsDSDisplayName());
			submitD2DBackupsRestoreJob(model);
		}	
        else  if(restoreType == RESTORE_BY_SEARCH){
			RestoreSearchPanel searchPanel = ((RestoreSearchPanel) pages
					.get(indexToPageKey(PAGE_SEARCH)));
			
			if(restoreSearchPanel.bSearchBackups)
			{
				model.setSessionPath(searchPanel.getSessionPath());
	
				String userName = searchPanel.getUserName();
				String pwd = searchPanel.getPassword();
	
				if (userName != null && userName.trim().length() > 0) {
					model.setUserName(userName);
					if (pwd != null) {
						model.setPassword(pwd);
					}
				}
	
				RestoreUtil.processRestorSearch();
				
				//for vsphere
				if(UIContext.uiType == Utils.UI_TYPE_VSPHERE && UIContext.backupVM !=null){
					model.setJobLauncher(JobLauncher.VSPHERE.getValue());
					model.setVMInstanceUUID(UIContext.backupVM.getVmInstanceUUID());
				}else{
					model.setJobLauncher(JobLauncher.D2D.getValue());
				}
				model.sourceRPSHost = searchPanel.getRPSHost();
				model.setRpsPolicy(searchPanel.getRpsPolicy());
				model.setRpsDataStoreName(searchPanel.getRpsDataStore());
				model.setRpsDataStoreDisplayName(searchPanel.getRpsDSDisplayName());
				
				if(model.listOfRestoreJobNodes.size() != 0)
				{
					submitD2DBackupsRestoreJob(model);
				}
			}
			
			if(restoreSearchPanel.bSearchArchives)
			{
				if(searchPanel.archiveDestinationInfo.getArchiveToDrive())
				{
					archiveJobModel.setArchiveDestType(4); // DISK file system
					archiveJobModel.setArchiveDiskInfo(searchPanel.archiveDestinationInfo.getArchiveDiskDestInfoModel());
				}
				else if(searchPanel.archiveDestinationInfo.getArchiveToCloud())
				{
					archiveJobModel.setArchiveDestType(searchPanel.archiveDestinationInfo.getCloudConfigModel().getcloudVendorType());//cloud type amazon s3 
					archiveJobModel.setArchiveCloudInfo(searchPanel.archiveDestinationInfo.getCloudConfigModel());
					if(searchPanel.archiveDestinationInfo.getCloudConfigModel().getcloudVendorType() == 0L)
					{
						archiveJobModel.setRRSFlag(searchPanel.archiveDestinationInfo.getCloudConfigModel().getrrsFlag());
					}
					else
					{
						archiveJobModel.setRRSFlag(0L);
					}
					
				}
				
				Map<String, List<RestoreJobArchiveItemNodeModel>> selectedArchiveItems = ConvertSelectedArchiveSearchNodes(restoreSearchResult.getSelectedArchiveNodes());
				
				if(selectedArchiveItems != null)
				{
				
					RestoreJobArchiveVolumeNodeModel[] selectedVolumes = new RestoreJobArchiveVolumeNodeModel[selectedArchiveItems.size()];
					
					Iterator itr = selectedArchiveItems.keySet().iterator();
					int iVolumeIndex = 0;
					while(itr.hasNext())
					{
						String strDriveName = (String)(itr.next());
						
						RestoreJobArchiveVolumeNodeModel volumeNode = new RestoreJobArchiveVolumeNodeModel();
						volumeNode.setdestVolumName(strDriveName);
						
						List<RestoreJobArchiveItemNodeModel> items = selectedArchiveItems.get(strDriveName);
						
						if(items.size() != 0)
						{
							RestoreJobArchiveItemNodeModel[] arrayOfItems = items.toArray(new RestoreJobArchiveItemNodeModel[0]);
						
							volumeNode.ArchiveItemsList = arrayOfItems;
						}
						selectedVolumes[iVolumeIndex++] = volumeNode;
					}
	
					archiveJobModel.listofArchiveVolumes = selectedVolumes;
					archiveJobModel.setJobType(RestoreJobType.FileSystem.getValue());
					
					archiveJobModel.setSessionPath(searchPanel.archiveDestinationInfo.getHostName());//setting the hostname
					
					//set the datastore path in the case of rps filecopy as the datastore dest is being used for filecopy catalog
					//if(searchPanel.archiveDestinationInfo.getArchiveToRPS()!=null&&searchPanel.archiveDestinationInfo.getArchiveToRPS()){
						archiveJobModel.setCatalogFolderPath(searchPanel.archiveDestinationInfo.getCatalogPath());
						archiveJobModel.setCatalogFolderUser(searchPanel.archiveDestinationInfo.getCatalogFolderUser());
						archiveJobModel.setCatalogFolderPassword(searchPanel.archiveDestinationInfo.getCatalogFolderPassword());
					//}
					
					submitD2DArchivesRestoreJob(archiveJobModel);
				}
			}
		}else if(restoreType == RECOVER_AD){
			ADRecoveryPointsPanel panel = getADRecoveryPointsPanel();
			model.setSessionPath(panel.getSessionPath());
			String userName = panel.getUserName();
			String pwd = panel.getPassword();
			if (userName != null && userName.trim().length() > 0) {
				model.setUserName(userName);
				if (pwd != null) {
					model.setPassword(pwd);
				}
			}

			RestoreContext.setRestoreRecvPointSources(RestoreContext.getRestoreRecvPointSources());

			model.setJobType(RestoreContext.getRestoreType().getValue());
			// Restore AD can only have one RestoreNode
			RestoreJobNodeModel nodeModel = new RestoreJobNodeModel();
			nodeModel.setSessionNumber(panel.getSelectedSessionID());
			RestoreUtil.processRestoreSource4AD(nodeModel);

			model.listOfRestoreJobNodes.add(nodeModel);
			model.setJobLauncher(JobLauncher.D2D.getValue());
			model.sourceRPSHost = panel.getSrcRPSHost();
			model.setRpsPolicy(panel.getRpsPolicy());
			model.setRpsDataStoreName(panel.getRpsDataStore());
			model.setRpsDataStoreDisplayName(panel.getRpsDSDisplayName());
			submitD2DBackupsRestoreJob(model);
		}

		/*// Attempt to submit the restore job
		service.submitRestoreJob(model, new BaseAsyncCallback<Integer>() {

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				nextButton.setEnabled(true);
				prevButton.setEnabled(true);
			}

			@Override
			public void onSuccess(Integer result) {
				// Check the result, Notify the user that the
				// restore has been successfully submitted
				GWT.log("Restore Successfully", null);
				if (result == 0) {
					
					thisWindow.cancel = false;
					ClosePage(true);       ///D2D Lite Integration

					Info.display(UIContext.Constants.messageBoxTitleInformation(), UIContext.Constants
							.restoreJobSubmittedSuccessfully());
				} else {
					MessageBox msg = new MessageBox();
					msg.setTitle(UIContext.Constants.messageBoxTitleError());
					msg.setMessage(UIContext.Constants
							.restoreJobFailedToSubmit());
					msg.setIcon(MessageBox.ERROR);
					msg.show();

					nextButton.setEnabled(true);
					prevButton.setEnabled(true);
				}
			}
		});*/
	}
	
	private boolean popInfoForMergeRunning(Throwable e) {
		if(e instanceof BusinessLogicException) {
			BusinessLogicException ble = (BusinessLogicException)e;
			if(("4294967315".equals((ble).getErrorCode()))) {
				MessageBox.info(UIContext.Messages.messageBoxTitleInformation(
						Utils.getProductName()), 
						(ble).getDisplayMessage(), null);
				ClosePage(true);
				return true;
			}
		}
		
		return false;
	}
	
	private void submitD2DBackupsRestoreJob(RestoreJobModel in_model) {
		// Attempt to submit the restore job
		service.submitRestoreJob(in_model, new BaseAsyncCallback<Integer>() {

			@Override
			public void onFailure(Throwable caught) {
				if(!popInfoForMergeRunning((BusinessLogicException)caught)){
					super.onFailure(caught);
					if(caught instanceof BusinessLogicException) {
						if(((BusinessLogicException)caught).getErrorCode().equals("25769803791")) {
							//Session merged, we clear it from recovery points table
							getRecoveryPointsPanel().setFailure();
						}
					}
					nextButton.setEnabled(true);
					prevButton.setEnabled(true);
					cancelButton.setEnabled(true);
				}
			}

			@Override
			public void onSuccess(Integer result) {
				// Check the result, Notify the user that the
				// restore has been successfully submitted
				//GWT.log("Restore Successfully", null);
				if (result == 0) {
					
					thisWindow.cancel = false;
					ClosePage(true);       ///D2D Lite Integration
					String productName = UIContext.productNameD2D;
					if(UIContext.uiType == 1){
						productName = UIContext.productNamevSphere;
					}
					Info.display(UIContext.Messages.messageBoxTitleInformation(productName), UIContext.Constants
							.restoreJobSubmittedSuccessfully());
				} else {
					MessageBox msg = new MessageBox();
					String title = "";
					if(UIContext.uiType ==1){
						title = UIContext.Messages.messageBoxTitleError(UIContext.productNamevSphere);
					}else{
						title = UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D);
					}
					msg.setTitleHtml(title);
					msg.setMessage(UIContext.Constants
							.restoreJobFailedToSubmit());
					msg.setIcon(MessageBox.ERROR);
					Utils.setMessageBoxDebugId(msg);
					msg.show();

					nextButton.setEnabled(true);
					prevButton.setEnabled(true);
					cancelButton.setEnabled(true);
				}
			}
		});
		
	}

	public RecoveryPointsPanel getRecoveryPointsPanel() {
		RecoveryPointsPanel panel = ((RecoveryPointsPanel) pages
				.get(indexToPageKey(PAGE_RECOVERY)));
		return panel;
	}
	
	public RestoreSearchPanel getRestoreSearchPanel() {
		RestoreSearchPanel panel = ((RestoreSearchPanel) pages
				.get(indexToPageKey(PAGE_SEARCH)));
		return panel;
	}
	
	public VMRecoveryPointsPanel getVMRecoveryPointsPanel(){
		VMRecoveryPointsPanel panel = (VMRecoveryPointsPanel) pages
		.get(indexToPageKey(PAGE_VM_RECOVERY));
		
		return panel;
	}

	public ExchangeGRTRecoveryPointsPanel getExchangeGRTRecoveryPointsPanel() {
		ExchangeGRTRecoveryPointsPanel panel = ((ExchangeGRTRecoveryPointsPanel) pages
				.get(indexToPageKey(PAGE_EXCHANGE_GRT_RECOVERY)));
		return panel;
	}

	public MailboxExplorerPanel getMailboxExplorerPanel() {
		MailboxExplorerPanel panel = ((MailboxExplorerPanel) pages
				.get(indexToPageKey(PAGE_EXCHANGE_GRT_MAIL_EXPLORER)));
		return panel;
	}
	
	public ADRecoveryPointsPanel getADRecoveryPointsPanel() {
		ADRecoveryPointsPanel panel = ((ADRecoveryPointsPanel) pages
				.get(indexToPageKey(PAGE_AD_RECOVERY)));
		return panel;
	}

	public void PreviousPage() {
		// special cases
		if(pageIndex == PAGE_AD_RECOVERY){
			pageIndex = PAGE_INTRO;
		}else if(pageIndex == PAGE_AD_EXPLORER){
			pageIndex = PAGE_AD_RECOVERY;
		}else if (pageIndex == PAGE_SEARCH) {
			pageIndex = PAGE_INTRO;
		}else if(pageIndex == PAGE_ARCHIVE_RECOVERY){
			pageIndex = PAGE_INTRO;
		}else if(pageIndex == PAGE_RESTORE_SEARCH_RESULT){
			pageIndex = PAGE_SEARCH;
		}else if (pageIndex == PAGE_OPTIONS) {
			if (this.restoreType == RECOVER_AD) {
				pageIndex = PAGE_AD_EXPLORER;
			}else if (this.restoreType == RESTORE_BY_SEARCH) {
				pageIndex = PAGE_RESTORE_SEARCH_RESULT;
			}else if (this.restoreType == RESTORE_BY_BROWSE){
				pageIndex = PAGE_RECOVERY;
			}else if (this.restoreType == RESTORE_BY_BROWSE_EXCHANGE_GRT)
			{
				pageIndex = PAGE_EXCHANGE_GRT_MAIL_EXPLORER;
				
				// liuwe05 2011-04-11 fix Issue: 20118552    Title: RESTORE WINDOW NOT CLOSING
				// the mailbox explorer might be already cleared if user has clicked finish button.
				// But if the job cannot be submitted due to the validation failure, user might go back here.
				// so we need to reload the explorer's content
				MailboxExplorerPanel explorer = this.getMailboxExplorerPanel();
				if (explorer != null)
				{
					explorer.reloadMailboxExplorerIfNecessary();
				}
			}
			else if(this.restoreType == RESTORE_BY_BROWSE_ARCHIVE)
			{
				pageIndex = PAGE_ARCHIVE_RECOVERY;
			}
            else {
				pageIndex = PAGE_RECOVERY;
			}
		}else if(pageIndex == PAGE_VM_RECOVERY){
			pageIndex = PAGE_INTRO;
		}else if(pageIndex == PAGE_VM_OPTION){
			pageIndex = PAGE_VM_RECOVERY;
		} 
	    else if (pageIndex == PAGE_EXCHANGE_GRT_RECOVERY)
		{
			pageIndex = PAGE_INTRO;
		}
		else if (pageIndex == PAGE_EXCHANGE_GRT_MAIL_EXPLORER)
		{
			pageIndex = PAGE_EXCHANGE_GRT_RECOVERY;
		} else if (pageIndex > 0) {
			pageIndex--;
		}

		// If the target is the intro page, hide the buttons
		if (pageIndex == PAGE_INTRO) {
			setButtonsVisible(false);
			prevButton.setVisible(false);
			nextButton.setEnabled(true);
			repaint();
		} 
		else if((defaultPage != PAGE_INTRO) && (defaultPage == pageIndex))       ///D2D Lite Integration
		{
			nextButton.setVisible(true);
			prevButton.setVisible(false);
		}
		else if (pageIndex == PAGE_OPTIONS) {
			nextButton.setText(UIContext.Constants.restoreNext());
		}else if (pageIndex == PAGE_VM_OPTION){
			nextButton.setText(UIContext.Constants.restoreNext());
		}else if (PAGE_SEARCH == pageIndex){//<huvfe01>2015/7/13[221842] Unable to click On next Button in Recovery file/ folder to restore.
			setButtonsVisible(true);
			nextButton.setEnabled(true);
			prevButton.setEnabled(true);
		}

		SetPage(pageIndex);
	}

	public void setButtonsVisible(boolean vis) {
		prevButton.setVisible(vis);
		nextButton.setVisible(vis);
	}

	public RestoreJobModel getRestoreJobModel() {
		return model;
	}

	public String getDestinationString() {
		String destination = UIContext.Constants.restoreToOriginalLocation();
		switch(restoreType)
		{
			case RESTORE_BY_BROWSE:			
			case RESTORE_BY_BROWSE_EXCHANGE_GRT:
				if (model != null) {
					if (model.getDestinationPath() != null
							&& model.getDestinationPath().trim().length() > 0) {
						return model.getDestinationPath();
					}
				}
				break;
			case RESTORE_BY_SEARCH:
				if (model != null) {
					if (model.getDestinationPath() != null
							&& model.getDestinationPath().trim().length() > 0) {
							return model.getDestinationPath();
					}else if(archiveJobModel != null && archiveJobModel.getarchiveRestoreDestinationPath() != null
									&& archiveJobModel.getarchiveRestoreDestinationPath().trim().length() > 0 )
						{
							return archiveJobModel.getarchiveRestoreDestinationPath();
						}
				}else if (archiveJobModel != null) {
							if (archiveJobModel.getarchiveRestoreDestinationPath() != null
									&& archiveJobModel.getarchiveRestoreDestinationPath().trim().length() > 0) {
								return archiveJobModel.getarchiveRestoreDestinationPath();
							}
					}				
				break;
			case RESTORE_BY_BROWSE_ARCHIVE:
				if (archiveJobModel != null) {
					if (archiveJobModel.getarchiveRestoreDestinationPath() != null
							&& archiveJobModel.getarchiveRestoreDestinationPath().trim().length() > 0) {
						return archiveJobModel.getarchiveRestoreDestinationPath();
					}
				}
				break;
		}
		
		
		return destination;
	}
	
	public void ClosePage(Boolean bFinished)    ///D2D Lite Integration
	{	
		AppEvent appEvent = new AppEvent(CloseMessage, bFinished);
		fireEvent(CloseMessage, appEvent);	
	}
	
	public void setButtonsEnable(boolean bEnabled) {		
		
		//if(pageIndex != PAGE_SUMMARY)
		nextButton.setEnabled(bEnabled);
		
		if(pageIndex != PAGE_INTRO)
			prevButton.setEnabled(bEnabled);
	}
	private Map<String, List<RestoreJobArchiveItemNodeModel>> ConvertSelectedArchiveSearchNodes(
			List<CatalogItemModel> selectedArchiveNodes) {
		
		Map<String, List<RestoreJobArchiveItemNodeModel>> searchNodes = new HashMap<String, List<RestoreJobArchiveItemNodeModel>>();//<List<RestoreJobArchiveItemNodeModel>>();
		
		for (CatalogItemModel item : selectedArchiveNodes) {
			int idriveIndex = item.getPath().indexOf(":");
			String drive = item.getPath().substring(0,idriveIndex);
			
			RestoreJobArchiveItemNodeModel archiveItem = ConvertCatalogItem2ArchiveItem(item,drive);
			
			if(searchNodes.containsKey(drive))
			{
				searchNodes.get(drive).add(archiveItem);
			}
			else
			{
				List<RestoreJobArchiveItemNodeModel> archiveItems = new ArrayList();
				archiveItems.add(archiveItem);
				searchNodes.put(drive,archiveItems);
			}
		}
		
		if(selectedArchiveNodes.size() == 0)
			return null;
		
		return searchNodes;
	}

	private RestoreJobArchiveItemNodeModel ConvertCatalogItem2ArchiveItem(
			CatalogItemModel item,String drive) {
		RestoreJobArchiveItemNodeModel archiveNode = new RestoreJobArchiveItemNodeModel();
	//	archiveNode.setVolumeHandle(gridTreeNode.getVolumeHandle());
		archiveNode.setVolumeName(drive);
		archiveNode.setSize(item.getSize());
		archiveNode.setDate(item.getDate());
		
		//trim the path to remove the volume name
		int idriveColonIndex = item.getPath().indexOf(":");
		
		if(idriveColonIndex != -1)
		{
			//String driveName = item.getPath().substring(idriveIndex+2,item.getPath().length());
			archiveNode.setFullPath(item.getPath().substring(idriveColonIndex+2,item.getPath().length()));
		}
		archiveNode.setType(item.getType());
		if(item.getType() == 6)//folder
			archiveNode.setVersion(0);
		else//file
			archiveNode.setVersion(item.getSubSessionNumber() == -1 ? item.getSubSessionNumber() : item.getSubSessionNumber());
		archiveNode.setArchiveType(item.getArchiveType());
		return archiveNode;
	}

	private void submitD2DArchivesRestoreJob(RestoreArchiveJobModel in_archiveJobModel) {
		service.submitRestoreArchiveJob(in_archiveJobModel,new BaseAsyncCallback<Long>() {

			@Override
			public void onFailure(Throwable caught) {
				/*MessageBox msg = new MessageBox();
				msg.setTitle(UIContext.Constants.messageBoxTitleError());
				msg.setMessage(caught.getMessage());
				msg.setIcon(MessageBox.ERROR);
				msg.show();*/
				if(!popInfoForMergeRunning(caught)) {
					super.onFailure(caught);
					nextButton.setEnabled(true);
					prevButton.setEnabled(true);
					cancelButton.setEnabled(true);
				}
			}

			@Override
			public void onSuccess(Long result) {
				// Check the result, Notify the user that the
				// restore has been successfully submitted
				//GWT.log("Restore Successfully", null);
				String productName = UIContext.productNameD2D;
				if(UIContext.uiType == 1){
					productName = UIContext.productNamevSphere;
				}
				if (result == 1) {
					thisWindow.setVisible(false);
					thisWindow.cancel = false;
					ClosePage(true);       ///D2D Lite Integration
					Info.display(UIContext.Messages.messageBoxTitleInformation(productName), UIContext.Constants
							.restoreJobSubmittedSuccessfully());
				} else {
					MessageBox msg = new MessageBox();
					msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(productName));
					msg.setMessage(UIContext.Constants
							.restoreJobFailedToSubmit());
					msg.setIcon(MessageBox.ERROR);
					Utils.setMessageBoxDebugId(msg);
					msg.show();

					nextButton.setEnabled(true);
					prevButton.setEnabled(true);
					cancelButton.setEnabled(true);
				}
			}
		});
		
	}
	
	private RestoreJobArchiveItemNodeModel[] ConvertSelectedArchiveNodes(String in_strVolumeName,
			List<ArchiveGridTreeNode> in_selectedNodes) {
		RestoreJobArchiveItemNodeModel[] nodes = new RestoreJobArchiveItemNodeModel[in_selectedNodes.size()];
		int iAddedItems = 0;
		int iItemIndex = 0;
		for(int iIndex = 0;iIndex < in_selectedNodes.size();iIndex++)
		{
			ArchiveGridTreeNode gridTreeNode = in_selectedNodes.get(iIndex);
			
			if(in_strVolumeName.compareToIgnoreCase(gridTreeNode.getVolumeName()) != 0)
				continue;
			
			if((gridTreeNode.getCatalogFilePath() == null) || (gridTreeNode.getCatalogFilePath().length() == 0))
				continue;
			RestoreJobArchiveItemNodeModel archiveNode = new RestoreJobArchiveItemNodeModel();
			archiveNode.setVolumeHandle(gridTreeNode.getVolumeHandle());
			archiveNode.setVolumeName(gridTreeNode.getVolumeName());
			archiveNode.setSize(gridTreeNode.getSize());
			archiveNode.setDate(gridTreeNode.getDate());
			archiveNode.setFullPath(gridTreeNode.getCatalogFilePath());
			
			archiveNode.setType(gridTreeNode.getType());
			if(gridTreeNode.getType() == 6)//folder
				archiveNode.setVersion(0);
			else//file
				archiveNode.setVersion(gridTreeNode.getSelectedVersion() == -1 ? gridTreeNode.getfileVersionsList().length : gridTreeNode.getSelectedVersion());
			archiveNode.setArchiveType(gridTreeNode.getArchiveType());
			
			nodes[iItemIndex++] = archiveNode;
			iAddedItems++;
		}
		
		if(iAddedItems == 0)
			return null;
		return nodes;
	}
	
	public RestoreArchiveJobModel getRestoreArchiveJobModel() {
		return archiveJobModel;
	}

	public RecoveryPointModel getSelectedRecoveryPoint()
	{
		RecoveryPointModel model = null;
		
		switch(restoreType)
		{
		// for File System/Exchange Writer/SQL Writer
		case RESTORE_BY_BROWSE:
			model = getRecoveryPointsPanel().getSelectedRecoveryPoint();
			break;
			
		// for Exchange GRT
		case RESTORE_BY_BROWSE_EXCHANGE_GRT:
			model = getExchangeGRTRecoveryPointsPanel().getSelectedRecoveryPoint();
			break;	
		//for vsphere
		case RECOVER_VM:
			model = getVMRecoveryPointsPanel().getSelectedRecoveryPoint();
			break;
		case RECOVER_AD:
			model = getADRecoveryPointsPanel().getSelectedRecoveryPoint();
		};
			
		return model;
	}
	
	public String getSessionPath()
	{
		String path = "";
		
		switch(restoreType)
		{
		// for File System/Exchange Writer/SQL Writer
		case RESTORE_BY_BROWSE:
			path = getRecoveryPointsPanel().getSessionPath();
			break;
			
		// for Exchange GRT
		case RESTORE_BY_BROWSE_EXCHANGE_GRT:
			path = getExchangeGRTRecoveryPointsPanel().getSessionPath();
			break;	
			
		//for vsphere
		case RECOVER_VM:
			path = getVMRecoveryPointsPanel().getSessionPath();
			break;
		//for search
		case RESTORE_BY_SEARCH:
			path = getRestoreSearchPanel().getSessionPath();
			break;
		//for AD GRT
		case RECOVER_AD:
			path = getADRecoveryPointsPanel().getSessionPath();
			break;
		};
		
		return path;		
	}
	
	
	//instance variables
	ArchiveSettingsModel archiveConfig = null;
	public ArchiveSettingsModel getarchiveConfig()
	{
		return archiveConfig;
	}
	
	public ArchiveDestinationModel getArchiveDestinationModel()
	{
		RestoreArchiveBrowsePanel archiveBrowsePanel = (RestoreArchiveBrowsePanel)pages.get(indexToPageKey(PAGE_ARCHIVE_RECOVERY));
		return archiveBrowsePanel.archiveDestinationInfo;
	}
	
	private int iCatalogJobSubmitted = 0;
	public void submitD2DArchiveCatalogSyncJob(ArchiveDestinationModel in_archiveDestnationModel) {
		
		if(iCatalogJobSubmitted == 1)
			return;
		
		if(in_archiveDestnationModel.getArchiveToDrive())
		{
			archiveJobModel.setArchiveDestType(4); // Disk file system
			archiveJobModel.setArchiveDiskInfo(in_archiveDestnationModel.getArchiveDiskDestInfoModel());
		}
		else if(in_archiveDestnationModel.getArchiveToCloud())
		{
			archiveJobModel.setArchiveDestType(in_archiveDestnationModel.getCloudConfigModel().getcloudVendorType());//cloud type amazon s3 
			archiveJobModel.setArchiveCloudInfo(in_archiveDestnationModel.getCloudConfigModel());
			if(in_archiveDestnationModel.getCloudConfigModel().getcloudVendorType() == 0L)
			{
				archiveJobModel.setRRSFlag(in_archiveDestnationModel.getCloudConfigModel().getrrsFlag());
			}
			else
			{
				archiveJobModel.setRRSFlag(0L);
			}
		}
		archiveJobModel.setDestType(RestoreJobType.FileSystem.getValue());
		
		//archiveJobModel.setSessionPath(in_HostName);// session path holds the hostname in archive catalog sync job
		archiveJobModel.setSessionPath(in_archiveDestnationModel.getHostName());
		archiveJobModel.setJobType(JobMonitorModel.JOBTYPE_ARCHIVE_CATALOG_SYNC);
		//fix TFS issue: 752482 (NPE)
		archiveJobModel.setProductType(JobLauncher.D2D.getValue());
		iCatalogJobSubmitted = 1;
		service.submitArchiveCatalogSyncJob(archiveJobModel, new BaseAsyncCallback<Long>() {

			@Override
			public void onFailure(Throwable caught) {
				if(!popInfoForMergeRunning(caught)) {
					iCatalogJobSubmitted = 0;
					super.onFailure(caught);
				}
			}

			@Override
			public void onSuccess(Long result) {
				iCatalogJobSubmitted = 0;
				thisWindow.setVisible(false);
				ClosePage(true);       ///D2D Lite Integration
				if(result == 0)
					Info.display(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D), UIContext.Constants.ArchiveCatalogSyncJobSubmittedSyccessfullyMessage());
				else
					Info.display(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D), UIContext.Constants.FailedToSubmitArchiveCatalogSyncJobMessage());
			}
		});
	}
	
	private class SessionPathNumberPair {
		private String instanceUuid;
		private String sessionPath;
		private int sessionId;
		
		public SessionPathNumberPair(String instanceUuid, String sessionPath, int sessionId) {
			this.instanceUuid = instanceUuid;
			this.sessionPath = sessionPath;
			this.sessionId = sessionId;
		}
		

		public String getInstanceUuid() {
			return instanceUuid;
		}
		
		public String getSessionPath() {
			return sessionPath;
		}

		public int getSessionId() {
			return sessionId;
		}

	}
}
