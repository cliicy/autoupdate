// May sprint
package com.ca.arcflash.ui.client.homepage;
import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.HelpTopics;
import com.ca.arcflash.ui.client.common.PathSelectionPanel;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.webservice.data.logcollect.DiagInfoCollectorConfiguration;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.Window;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer.HBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import com.sencha.gxt.widget.core.client.toolbar.FillToolItem;



public class CollectDiagnosticDataWindow extends Window{
	protected final CommonServiceAsync service = GWT.create(CommonService.class);
	protected Window window;
	protected LabelField collectDiagnosticDataDescriptionLabel;
	protected LabelField windowsEventLevelLabel;
	protected LabelField destinationLabel;
	protected CheckBox windowsEventInformationLevel;
	protected TextButton collectButton;
	protected TextButton helpButton;
	protected TextButton cancelButton;
	protected ToggleGroup logLevelGrp;
	protected PathSelectionPanel pathSelection;
	public static final int DIAGNOSTIC_MODE = 11;

	protected DiagInfoCollectorConfiguration diagObj = new DiagInfoCollectorConfiguration();
	final CommonServiceAsync commonService = GWT.create(CommonService.class);
	
	public CollectDiagnosticDataWindow(boolean bIsLinuxNodeorServer){
		this.window = this;
		this.setModal(true);
		this.setHeadingHtml(UIContext.Constants.collectDiagnosticDataWindowHeading());
		
		FormPanel fp = new FormPanel();
		fp.ensureDebugId("68954fc2-981d-42bb-80d5-6335bda717f7");
		fp.setHeaderVisible(false);
		fp.setWidth(555);
		fp.setHeight(450);
		fp.setStyleAttribute("background", "white");
		fp.setBodyBorder(false);
		fp.setBorders(false);
		fp.setFrame(false);
		
		VerticalLayoutContainer vlc = new VerticalLayoutContainer();
		vlc.ensureDebugId("b98de9cc-3bda-4e30-afee-42936180c40d");		
		
		collectDiagnosticDataDescriptionLabel = new LabelField();
		collectDiagnosticDataDescriptionLabel.setVisible(true);
		collectDiagnosticDataDescriptionLabel.setValue(UIContext.Constants.collectDiagnosticDataDescription());
		vlc.add(collectDiagnosticDataDescriptionLabel, new VerticalLayoutData(540, 60, new Margins(0,0,0,5)));
		
		DiagProperties diagProperties = GWT.create(DiagProperties.class);
		ListStore<DiagLogModel> collectDiagInfoStore = new ListStore<DiagLogModel>(diagProperties.id());
		collectDiagInfoStore.add(new DiagLogModel(UIContext.Constants.collectDiagDataInfoArcserveUDPDatabase(), "1"));
		collectDiagInfoStore.add(new DiagLogModel(UIContext.Constants.collectDiagDataInfoArcserveUDPConfigurationFiles(),"2"));
		collectDiagInfoStore.add(new DiagLogModel(UIContext.Constants.collectDiagDataInfoArcserveUDPComponentLogs(), "3"));
		collectDiagInfoStore.add(new DiagLogModel(UIContext.Constants.collectDiagDataInfoArcserveUDPDestinationInformation(), "4"));
		collectDiagInfoStore.add(new DiagLogModel(UIContext.Constants.collectDiagDataInfoSystemInformation(), "5"));
		collectDiagInfoStore.add(new DiagLogModel(UIContext.Constants.collectDiagDataInfoNetworkInformation(), "6"));
		collectDiagInfoStore.add(new DiagLogModel(UIContext.Constants.collectDiagDataInfoRegistryInformation(), "7"));
		collectDiagInfoStore.add(new DiagLogModel(UIContext.Constants.collectDiagDataInfoVSSWriterInformation(), "8"));
		collectDiagInfoStore.add(new DiagLogModel(UIContext.Constants.collectDiagDataInfoWindowsEventLogsWarningandErrorLevels(), "9"));
		
		ColumnConfig<DiagLogModel, String> DiagInfoCol = new ColumnConfig<DiagLogModel, String>(diagProperties.logCollectionDesciptionText(), 400, UIContext.Constants.collectDiagDataInfoHeaderTitle());
		DiagInfoCol.setSortable(false);
		DiagInfoCol.setMenuDisabled(true);
		DiagInfoCol.setRowHeader(true);
		DiagInfoCol.setResizable(false);
		List<ColumnConfig<DiagLogModel, ?>> columns = new ArrayList<ColumnConfig<DiagLogModel, ?>>();
		columns.add(DiagInfoCol);
		ColumnModel<DiagLogModel> columnModel = new ColumnModel<DiagLogModel>(columns);
		Grid collectDiagInfoGrid = new Grid<DiagLogModel>(collectDiagInfoStore, columnModel);
		collectDiagInfoGrid.setBorders(true);
		collectDiagInfoGrid.setColumnResize(false);
		collectDiagInfoGrid.setAllowTextSelection(false);
		collectDiagInfoGrid.getView().setAutoFill(true);
		collectDiagInfoGrid.getView().setTrackMouseOver(false);
		
		GridSelectionModel sm = new GridSelectionModel<>();		
		sm.setLocked(true);
		collectDiagInfoGrid.setSelectionModel(sm);
		collectDiagInfoGrid.setPixelSize(352, 238);
		
				
		vlc.add(collectDiagInfoGrid, new VerticalLayoutData(1, -1, new Margins(25, 0, 0, 5)));
		
		windowsEventInformationLevel = new CheckBox();
		windowsEventInformationLevel.setValue(false);
		windowsEventInformationLevel.setBoxLabel(UIContext.Constants.collectDiagDataCollectinformationlevelWindowsEventlogs());
		vlc.add(windowsEventInformationLevel, new VerticalLayoutData(1, -1, new Margins(5, 0, 0, 5)));
		
		if(bIsLinuxNodeorServer)
		{
			windowsEventInformationLevel.disable();
		}
		
		HBoxLayoutContainer hboxDestination=new HBoxLayoutContainer();
		hboxDestination.setWidth("525px");
		hboxDestination.setHeight("50px");
		hboxDestination.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);		
		hboxDestination.ensureDebugId("5be05d76-c18a-4a71-badc-ef5204103ca8");
				
		destinationLabel = new LabelField();
		destinationLabel.setVisible(true);
		destinationLabel.setValue(UIContext.Constants.collectDiagnosticDataDestinationLabel());
		pathSelection = new PathSelectionPanel(true, new Listener<FieldEvent>(){
			@Override
			public void handleEvent(FieldEvent be) {
				if(pathSelection.getPathInputPanel().getValue() != null && Utils.isValidRemotePath(pathSelection.getPathInputPanel().getValue(), true))
					collectButton.setEnabled(true);
				else
					collectButton.setEnabled(false);
			}
		}); 
		pathSelection.setMode(PathSelectionPanel.DIAGNOSTIC_MODE);
		pathSelection.setTooltipMode(PathSelectionPanel.TOOLTIP_DIAGNOSTIC_MODE);
		pathSelection.addDebugId("f9327cd9-6f66-470b-94cb-9ff9a5e6beb0", 
				"83966484-7fd3-4228-8003-f728f5fa5d93", "2c658cad-c903-4708-b346-83685c9a1b08");
		pathSelection.setVisible(true);
		pathSelection.setWidth("315px");
		
		hboxDestination.add(destinationLabel);
		hboxDestination.add(pathSelection);
		
		vlc.add(hboxDestination, new VerticalLayoutData(1, -1, new Margins(5, 0, 0, 5))); // First 1 means width of the panel, -1 means not applicable, Margins is the space from borders of the panel  
		
		fp.add(vlc);
		this.add(fp);
		
		//Buttons
		cancelButton = new TextButton();
		cancelButton.setText(UIContext.Constants.cancel());
		cancelButton.ensureDebugId("a98a780c-5d19-4689-8b54-01e513247ba3");
		cancelButton.addSelectHandler(new SelectHandler(){
				@Override
				public void onSelect(SelectEvent ce) {
					CollectDiagnosticDataWindow.this.hide();
				}
			});
		
		collectButton = new TextButton();
		collectButton.setText(UIContext.Constants.collectDiagData());
		collectButton.ensureDebugId("5998697c-aa6a-475a-83c0-fecbc30ce5ea");
		collectButton.setEnabled(false);
		collectButton.addSelectHandler(new SelectHandler(){
				@Override
				public void onSelect(SelectEvent event) {
					if (pathSelection == null || pathSelection.getDestination().isEmpty() ){
						return;
					}
					else if(pathSelection.getDestination().startsWith("\\\\"))
					{
						if(pathSelection.getUsername() == null)
						{
							pathSelection.showUsernamePasswordDialog(false);
							return;
						}
						else if((pathSelection.getUsername().isEmpty()))
						{
							pathSelection.showUsernamePasswordDialog(false);
							return;
						}
						
						if(pathSelection.getPassword() == null)
						{
							pathSelection.showUsernamePasswordDialog(false);
							return;
						}
						else if((pathSelection.getPassword().isEmpty()))
						{
							pathSelection.showUsernamePasswordDialog(false);
							return;
						}
					}
					
					String maskMsg =  UIContext.Constants.validating();
					mask(maskMsg);
					commonService.validateDest(pathSelection.getDestination(), "", 
							pathSelection.getUsername(), pathSelection.getPassword(), DIAGNOSTIC_MODE,
							new BaseAsyncCallback<Long>()
					{
						@Override
						public void onFailure(Throwable caught) {
							pathSelection.showUsernamePasswordDialog(false);
							unmask();
							return;
						}
						@Override
						public void onSuccess(Long result) 
						{
							if(result == 0)
							{
								collectButton.setEnabled(false);
								cancelButton.setEnabled(false);
								doCollectDiagnosticData();
							}
							else
							{
								pathSelection.showUsernamePasswordDialog(false);
								unmask();
								return;
							}
						}
					});
						
				}
			});
		
		mask();
		service.getDiagInfoFromXml(new BaseAsyncCallback<DiagInfoCollectorConfiguration>(){

			@Override
			public void onFailure(Throwable caught) {
				unmask();
			}

			@Override
			public void onSuccess(DiagInfoCollectorConfiguration result) {
				if(result != null)
				{
					pathSelection.setDestination(result.getUploadDestination());
					pathSelection.setUsername(result.getUserName());
					pathSelection.setPassword(result.getPassword());
					collectButton.setEnabled(true);
				}
				unmask();
			}
			
		});
		
		helpButton = new TextButton();
		helpButton.setText(UIContext.Constants.help());
		helpButton.ensureDebugId("1e9ded20-1d8b-4228-a305-08be168b69ea");
		helpButton.addSelectHandler(new SelectHandler(){
				@Override
				public void onSelect(SelectEvent event) {
					HelpTopics.showHelpURL(UIContext.externalLinks.getCollectDiagDataHelp());
				}
			});
		
		helpButton.addStyleName("ca-tertiaryText");
		cancelButton.addStyleName("ca-tertiaryText");
		this.getButtonBar().setPack(BoxLayoutPack.START);
		this.getButtonBar().add(helpButton);
		this.getButtonBar().add(new FillToolItem());
		this.getButtonBar().add(collectButton);
		this.getButtonBar().add(cancelButton);
	}
	
	
	protected void doCollectDiagnosticData() {
		diagObj.setUploadDestination(pathSelection.getDestination());
		
		if(pathSelection.getUsername()==null || pathSelection.getUsername().isEmpty())
			diagObj.setUserName("");
		else
			diagObj.setUserName(pathSelection.getUsername());
		if(pathSelection.getPassword()==null || pathSelection.getPassword().isEmpty())
			diagObj.setPassword("");
		else 
			diagObj.setPassword(pathSelection.getPassword());
		// 0 means Network share, hardcoded for now, If FTP (1) supported then depending on the user selection we need to set this.
		diagObj.setDestinationType(0);
		if(windowsEventInformationLevel.getValue())
			diagObj.setAdvancedLogCollection(1); // 1 all logs, 0 for errors and warnings
		else
			diagObj.setAdvancedLogCollection(0);
		window.hide();
		collectDiagInformationSubmissionSuccessPopup(windowsEventInformationLevel.getValue());		
		service.collectDiagnosticInfo(diagObj, new BaseAsyncCallback<Integer>(){

			@Override
			public void onFailure(Throwable caught) {
				collectDiagInformationSubmissionFailedPopup();
			}

			@Override
			public void onSuccess(Integer result) {
				
			}
			
		});
	}
	public void collectDiagInformationSubmissionFailedPopup()
	{
		com.sencha.gxt.widget.core.client.box.MessageBox messageBox = new com.sencha.gxt.widget.core.client.box.MessageBox(UIContext.Messages.messageBoxTitleInformation(UIContext.Constants.collectDiagDataSubmission()), UIContext.Constants.collectDiagnosticDataSubmitFailed());
		messageBox.addDialogHideHandler(new DialogHideHandler() {
            @Override
            public void onDialogHide(DialogHideEvent event) {
				if (event.getHideButton() == PredefinedButton.YES){
					window.show();
					cancelButton.setEnabled(true);
					collectButton.setEnabled(true);
					helpButton.setEnabled(true);
				}
            }
        });		
		messageBox.setPredefinedButtons(PredefinedButton.YES, PredefinedButton.NO);
		messageBox.setIcon(com.sencha.gxt.widget.core.client.box.MessageBox.ICONS.error());
		messageBox.setModal(true);
		messageBox.show();
	}
	public void collectDiagInformationSubmissionSuccessPopup(boolean bAdvancedLogCollection)
	{
		String PopupMessage= "";
		if(bAdvancedLogCollection)
			PopupMessage = UIContext.Constants.collectDiagnosticDataSubmitSuccessfulWithLongTime();
		else
			PopupMessage = UIContext.Constants.collectDiagnosticDataSubmitSuccessful();
		com.sencha.gxt.widget.core.client.box.MessageBox box = new com.sencha.gxt.widget.core.client.box.MessageBox(UIContext.Messages.messageBoxTitleInformation(UIContext.Constants.collectDiagDataSubmission()), PopupMessage);
        box.setIcon(com.sencha.gxt.widget.core.client.box.MessageBox.ICONS.info());
        box.setModal(true);
        box.show();        
	}
}