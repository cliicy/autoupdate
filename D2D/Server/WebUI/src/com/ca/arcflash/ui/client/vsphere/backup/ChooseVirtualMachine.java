package com.ca.arcflash.ui.client.vsphere.backup;

import java.util.ArrayList;
import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.BackupVMModel;
import com.ca.arcflash.ui.client.model.ESXServerModel;
import com.ca.arcflash.ui.client.model.VMItemModel;
import com.ca.arcflash.ui.client.model.VirtualCenterModel;
import com.ca.arcflash.ui.client.restore.BrowseWindow;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;

public class ChooseVirtualMachine extends Window{
	final LoginServiceAsync service = GWT.create(LoginService.class);
	
	private ListStore<VMItemModel> gridStore;
	
	//private List<VMItemModel> vmList;

	private ComboBox<ESXServerModel> esxServerList;
	
	private ListStore<ESXServerModel> esxDataStore = new ListStore<ESXServerModel>();
	
	private Grid<VMItemModel> vmGrid;
	private CheckBox allCheck;

	private VSphereBackupSettingWindow parentWindow;
	
	private ChooseVirtualMachine thisWindow;
	
	private VirtualCenterModel vcModel;
	
	public ChooseVirtualMachine(VSphereBackupSettingWindow window){
		//this.vmList=vmList;
		parentWindow=window;
		thisWindow=this;
		vcModel = new VirtualCenterModel();
		vcModel.setVcName(parentWindow.vcModel.getVcName());
		vcModel.setProtocol(parentWindow.vcModel.getProtocol());
		vcModel.setPort(parentWindow.vcModel.getPort());
		vcModel.setUsername(parentWindow.vcModel.getUsername());
		vcModel.setPassword(parentWindow.vcModel.getPassword());
		
		
		this.setWidth(700);
		this.setClosable(false);
		this.setResizable(false);
		this.setHeadingHtml(UIContext.Constants.chooseVirtualMachineWindowTitle());
		
		TableLayout tableLayout=new TableLayout();
		tableLayout.setWidth("100%");
		this.setLayout(tableLayout);
		
		ContentPanel cPanel=new ContentPanel();
		cPanel.setHeaderVisible(false);
		cPanel.setWidth("100%");
		cPanel.setBodyStyle("background-color: white; padding: 6px;");	
		
		LabelField label = new LabelField();
		label.addStyleName("restoreWizardSubItem");
		label.setValue(UIContext.Constants.chooseVMLabelProtectedVM());
		cPanel.add(label);

		initEsxserver(cPanel);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.chooseVMLabelProtectedVMDescription());
		cPanel.add(label);

		/*LayoutContainer backupTypeContainer = new LayoutContainer();
		TableLayout backupTypeTable = new TableLayout();
		backupTypeTable.setColumns(3);
		backupTypeTable.setCellPadding(5);
		backupTypeTable.setWidth("100%");
		backupTypeContainer.setLayout(backupTypeTable);*/

		/*RadioGroup rg = new RadioGroup();
		Radio destChangedFullBackup = new Radio();
		destChangedFullBackup.setBoxLabel(UIContext.Constants.backupTypeFull());
		destChangedFullBackup.setValue(false);

		rg.add(destChangedFullBackup);
		backupTypeContainer.add(destChangedFullBackup);

		Radio destChangedIncrementalBackup = new Radio();
		destChangedIncrementalBackup.setBoxLabel(UIContext.Constants
				.backupTypeIncremental());
		destChangedIncrementalBackup.setValue(true);
		rg.add(destChangedIncrementalBackup);
		backupTypeContainer.add(destChangedIncrementalBackup);*/

		/*TextField<String> searchText = new TextField<String>();
		searchText.setValue("Enter VM name to filter");
		TableData tb = new TableData();
		tb.setWidth("50%");
		tb.setHorizontalAlign(HorizontalAlignment.RIGHT);
		backupTypeContainer.add(searchText, tb);*/
		//cPanel.add(backupTypeContainer);

		initVMGrid(cPanel);		
		setDestinationButton(cPanel);		
		
		this.add(cPanel);	
		
		LayoutContainer buttonContainer = new LayoutContainer();
		buttonContainer.setStyleAttribute("background-color","#DFE8F6");
		buttonContainer.setHeight(20);
		
		TableLayout buttonLayout = new TableLayout();
		buttonLayout.setWidth("100%");
		buttonLayout.setCellPadding(4);
		buttonLayout.setCellSpacing(4);
		buttonLayout.setColumns(3);	
		buttonContainer.setLayout(buttonLayout);
		
		initButtonContainer(buttonContainer);
		
		this.add(buttonContainer);
	}
	private void initEsxserver(ContentPanel cPanel){
		LayoutContainer esxContainer = new LayoutContainer();
		TableLayout esxTable = new TableLayout();
		esxTable.setColumns(2);
		esxTable.setCellPadding(1);
		esxTable.setWidth("100%");
		esxContainer.setLayout(esxTable);
		
		TableData tb = new TableData();
		tb.setWidth("15%");
		
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.vmRecoveryEsxServerLabel());
		esxContainer.add(label,tb);
		
		tb = new TableData();
		tb.setWidth("85%");
		
		esxServerList = new ComboBox<ESXServerModel>();
		esxServerList.ensureDebugId("57F82EC9-778A-42d7-BCA5-CBE152C8D06D");
		esxServerList.setWidth(200);
		esxServerList.setDisplayField("esxName");
		esxServerList.setAllowBlank(false);
		esxServerList.setValidateOnBlur(false);
		esxServerList.setEditable(false);
		esxServerList.setTriggerAction(TriggerAction.ALL);
		esxServerList.setStore(esxDataStore);
		esxServerList.setEmptyText(UIContext.Constants.trustHost());
		esxServerList.setFieldLabel(UIContext.Constants.vmRecoveryEsxServerLabel());
		esxServerList.addSelectionChangedListener(new SelectionChangedListener<ESXServerModel>(){

			@Override
			public void selectionChanged(
					SelectionChangedEvent<ESXServerModel> se) {
				refreshVMGrid(se.getSelectedItem());
			}
			
		});
		esxContainer.add(esxServerList,tb);
		
		cPanel.add(esxContainer);
		
		
	}
	private void refreshESXServer(){
		service.getESXServer(vcModel, new BaseAsyncCallback<List<ESXServerModel>>(){
			@Override
			public void onFailure(Throwable caught){
				super.onFailure(caught);
			}
			@Override
			public void onSuccess(List<ESXServerModel> result){
				esxServerList.getStore().removeAll();
				esxServerList.getStore().add(result);
				esxServerList.setValue(result.get(0));
			}
		});
	}
	private void refreshVMGrid(ESXServerModel esxServerModel){
		vmGrid.mask(UIContext.Constants.loadingVMMaskText());
		service.getVMItem(vcModel, esxServerModel, new BaseAsyncCallback<List<VMItemModel>>(){
			@Override
			public void onFailure(Throwable caught){
				super.onFailure(caught);
				vmGrid.unmask();
			}
			@Override
			public void onSuccess(List<VMItemModel> result){
				vmGrid.getStore().removeAll();
				vmGrid.getStore().add(result);
				vmGrid.getView().refresh(false);
				vmGrid.unmask();
			}
		});
	}
	private void initButtonContainer(LayoutContainer buttonContainer) {		
		//Repeat Section
		TableData td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.LEFT);
		td.setVerticalAlign(VerticalAlignment.BOTTOM);
		td.setWidth("100%");
					
		
		LabelField leftSpace = new LabelField();
		buttonContainer.add(leftSpace, td);
		
		td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.RIGHT);
		td.setVerticalAlign(VerticalAlignment.BOTTOM);
		
		Button okButton = new Button();
		okButton.ensureDebugId("4E45013A-E8D0-4bdb-857B-53961C835735");
		okButton.setMinWidth(80);
		okButton.setText(UIContext.Constants.backupSettingsOk());
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				//OK Clicked, save it						
				thisWindow.mask(UIContext.Constants.settingsMaskText());				
				boolean check = thisWindow.saveSettings();
				if(!check)
					thisWindow.unmask();
			}		
		});		
		buttonContainer.add(okButton, td);
		
		Button cancelButton = new Button();	
		cancelButton.ensureDebugId("87378BFC-298C-410e-B311-D6F890810BAD");
		cancelButton.setMinWidth(80);
		cancelButton.setText(UIContext.Constants.backupSettingsCancel());
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				//Cancel Clicked hide the dialog 				
				thisWindow.hide();
			}
		
		});		
		td = new TableData();
		td.setHorizontalAlign(HorizontalAlignment.RIGHT);
		td.setVerticalAlign(VerticalAlignment.BOTTOM);		
		buttonContainer.add(cancelButton, td);		
	}

	private void setDestinationButton(ContentPanel cPanel) {
		LayoutContainer buttonContainer = new LayoutContainer();
		TableLayout buttonTable = new TableLayout();
		buttonTable.setColumns(2);
		buttonTable.setCellPadding(5);
		buttonContainer.setLayout(buttonTable);

		Button setCredential = new Button(UIContext.Constants.setCredentialButton());
		setCredential.ensureDebugId("2B6D8B60-DC70-43a1-B4B4-8672F68A000C");
		buttonContainer.add(setCredential);
		setCredential
				.addSelectionListener(new SelectionListener<ButtonEvent>() {
					@Override 
					public void componentSelected(ButtonEvent ce) {
						SetCredentialWindow window = new SetCredentialWindow(thisWindow);
						window.setModal(true);
						window.show();
					}

				});

		Button setDestination = new Button(UIContext.Constants.setDestinationButton());
		setDestination.ensureDebugId("F16BAAF7-A5CD-487a-B2C0-FCBB01536F8F");
		buttonContainer.add(setDestination);
		setDestination
		.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				final ShareFolderWindow shareFolder = new ShareFolderWindow();
				shareFolder.setModal(true);
				shareFolder.show();						

				shareFolder.addListener(Events.Hide,
						new Listener<BaseEvent>() {
							@Override
							public void handleEvent(BaseEvent be) {
								String destination = shareFolder.getDestination();
								if (destination != null && !destination.equals("")) {
									for (VMItemModel model : vmGrid.getSelectionModel().getSelectedItems()) {
										model.setDestination(destination);
										String username = shareFolder.getUserName();
										String password = shareFolder.getPassword();
										model.setDesUsername(username);
										model.setDesPassword(password);
									}
									vmGrid.getView().refresh(false);
								}										
							}
						});

				/*
				 * final BrowseWindow browseDlg = new
				 * BrowseWindow(false, ""); browseDlg.setModal(true);
				 * browseDlg.show();
				 * 
				 * browseDlg.addListener(Events.Hide, new
				 * Listener<BaseEvent>() {
				 * 
				 * @Override public void handleEvent(BaseEvent be) {
				 * String destination = browseDlg.getDestination(); if
				 * (destination != null && !destination.equals("")) {
				 * for (VMItemModel model :
				 * vmGrid.getSelectionModel().getSelectedItems()) {
				 * model.setDestination(destination); }
				 * vmGrid.getView().refresh(false); } }
				 * 
				 * });
				 */
			}

		});
      cPanel.add(buttonContainer);		
	}

	private void initVMGrid(ContentPanel cPanel) {
		allCheck = new CheckBox();
		allCheck.ensureDebugId("648C61D1-1156-4f41-85A7-0F331DC48508");
		allCheck.setBoxLabel(UIContext.Constants.vmGridChooseAllVMLabel());
		allCheck.addListener(Events.OnClick, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				if (allCheck.getValue()) {
					List<VMItemModel> store = vmGrid.getStore().getModels();
					for (VMItemModel item : store) {
						if (!item.isVmChoose())
							item.setVmChoose(true);
					}
				} else {
					List<VMItemModel> store = vmGrid.getStore().getModels();
					for (VMItemModel item : store) {
						if (item.isVmChoose())
							item.setVmChoose(false);
					}
				}
				vmGrid.getView().refresh(false);
			}
		});

		cPanel.add(allCheck);

		final CheckColumnConfig checkColumn = new CheckColumnConfig("vmChoose",
				UIContext.Constants.vmGridColumnLabelChoose(), 50) {
			protected void onMouseDown(GridEvent<ModelData> ge) {
				super.onMouseDown(ge);

				List<VMItemModel> store = vmGrid.getStore().getModels();
				boolean allChoose = true;
				for (VMItemModel item : store) {
					if (!allChoose)
						break;
					if (!item.isVmChoose())
						allChoose = false;
				}
				if (allChoose && !allCheck.getValue())
					allCheck.setValue(true);
				if (!allChoose && allCheck.getValue())
					allCheck.setValue(false);
			}
		};

		checkColumn.setSortable(false);
		checkColumn.setResizable(false);
		checkColumn.setFixed(true);
		checkColumn.setMenuDisabled(true);

		ColumnConfig vmName = new ColumnConfig("vmName",UIContext.Constants.vmGridColumnLabelVMName(), 200);
		ColumnConfig esxName = new ColumnConfig("esxServer", UIContext.Constants.vmGridColumnLabelEsxServer(),200);
		ColumnConfig username = new ColumnConfig("username", UIContext.Constants.vmGridColumnLabelUsername(), 100);
		ColumnConfig destination = new ColumnConfig("destination",UIContext.Constants.vmGridColumnLabelDestination(), 100);

		List<ColumnConfig> config = new ArrayList<ColumnConfig>();

		config.add(checkColumn);
		config.add(vmName);
		config.add(esxName);
		config.add(username);
		config.add(destination);

		final ColumnModel cm = new ColumnModel(config);

		gridStore = new GroupingStore<VMItemModel>();
		vmGrid = new Grid<VMItemModel>(gridStore, cm);
		vmGrid.setBorders(true);
		vmGrid.addPlugin(checkColumn);
		vmGrid.setHeight(300);
		vmGrid.setAutoExpandColumn("vmName");
		vmGrid.mask(UIContext.Constants.AddingVMMaskText());
		//vmGrid.setAutoWidth(true);
		vmGrid.setStripeRows(true);
		

		cPanel.add(vmGrid);
	}
	
	@Override
	protected void afterShow() {
		super.afterShow();
		RefreshData();
	}
	
	public void RefreshData() {
		/*vmGrid.mask(UIContext.Constants.AddingVMMaskText());
		
		if(vmList!=null){
			gridStore.removeAll();
		    gridStore.add(vmList);
//		    checkBackupVM();
		}
		vmGrid.unmask();
		
		vmGrid.getView().refresh(false);*/
		refreshESXServer();
	}
	
	private void checkBackupVM() {
		List<BackupVMModel> backupModelList = parentWindow.model.backupVMList;
		if (backupModelList != null && backupModelList.size() > 0) {
			List<VMItemModel> vmItemList = gridStore.getModels();

			for (VMItemModel item : vmItemList) {
				for (BackupVMModel backupModel : backupModelList) {
					if (item.getVMUUID().equals(backupModel.getUUID())) {						
						item.setVmChoose(true);
						item.setUsername(backupModel.getUsername());
						item.setPassword(backupModel.getPassword());
						item.setDestination(backupModel.getDestination());
						vmGrid.getSelectionModel().select(item, true);
					}

				}
			}
			vmGrid.getView().refresh(false);
		}
	}
	
	public boolean Validate() {
		boolean isValid = true;
		String title = null;
		String msgStr = null;
		
		int size = 0;		
		if (isValid) {
			List<VMItemModel> itemList = vmGrid.getStore().getModels();
			ArrayList<String> vmList = new ArrayList<String>();

			for (VMItemModel item : itemList) {
				if (item.isVmChoose()) {
					size++;
					if (item.getDestination() == null
							|| item.getDestination().trim().equals(""))
						vmList.add(item.getVmName());
				}
			}
			if (!vmList.isEmpty()) {
				StringBuffer vm = new StringBuffer();
				for (String item : vmList)
					vm.append("<br>" + item + "</br>");
				title = UIContext.Constants.backupSettingsSettings();
				msgStr = "<p>"+UIContext.Constants.messageBoxAlertSettingDestinationForChosedVM()+"</p>"
						+ vm.toString();
				isValid = false;
			}
		}

		if (isValid) {			
			if (size == 0) {
				title = UIContext.Constants.backupSettingsSettings();
				msgStr = UIContext.Constants.messageBoxAlertChooseOneVMAtLeast();
				isValid = false;
			}
		}

		if (!isValid) {
			MessageBox msg = new MessageBox();
			msg.setIcon(MessageBox.ERROR);
			msg.setTitleHtml(title);
			msg.setMessage(msgStr);
			msg.setModal(true);
			Utils.setMessageBoxDebugId(msg);
			msg.show();
			return false;
		}
		return true;
	}
	
	protected boolean saveSettings() {
		if(Validate()){
			parentWindow.model.backupVMList = getBackupVMModelList();		
			
		}else{
			return false;
		}		
		
		return Save();
	}
	
	private boolean Save() {		
		try {
			service.saveVShpereBackupSetting(parentWindow.model,
					new BaseAsyncCallback<Long>() {
						@Override
						public void onSuccess(Long result) {
							// fix 18898048
							// launchFirstBackupJobifNeeded();

							thisWindow.unmask();
							thisWindow.hide();
							MessageBox msg = new MessageBox();
							msg.setIcon(MessageBox.INFO);
							msg.setTitleHtml(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D));
							msg.setMessage(UIContext.Constants.messageBoxSaveBackupSettingSucceed());
							msg.setModal(true);
							Utils.setMessageBoxDebugId(msg);
							msg.show();
							//parentWindow.hide();
//							UIContext.homepagePanel.refresh(null);
						}

						@Override
						public void onFailure(Throwable caught) {
							thisWindow.unmask();
							/*MessageBox msg = new MessageBox();
							msg.setIcon(MessageBox.ERROR);
							msg.setTitle(UIContext.Constants.messageBoxTitleError());
							msg.setMessage(UIContext.Constants.messageBoxSaveBackupSettingFail());
							msg.setModal(true);
							msg.show();*/
							super.onFailure(caught);
						}
					});
		} catch (Exception ce) {
			ce.printStackTrace();
		}
		return true;
	}
	
	private List<BackupVMModel> getBackupVMModelList() {
		List<VMItemModel> itemList = vmGrid.getStore().getModels();
		List<BackupVMModel> backupModelList = new ArrayList<BackupVMModel>();
		BackupVMModel temp = null;
		for (VMItemModel model : itemList) {
			if (model.isVmChoose()) {
				temp = new BackupVMModel();
				temp.setDestination(model.getDestination());				
				if(model.getDesUsername()!=null && !model.getDesUsername().trim().equals(""))
					temp.setDesUsername(model.getDesUsername().trim());								
				else
					temp.setDesUsername("");								
				
				if(model.getDesPassword()!=null && !model.getDesPassword().trim().equals(""))
					temp.setDesPassword(model.getDesPassword().trim());
				else
					temp.setDesPassword("");
				
				temp.setEsxServerName(parentWindow.vcModel.getVcName());
				temp.setEsxUsername(parentWindow.vcModel.getUsername());
				temp.setEsxPassword(parentWindow.vcModel.getPassword());
				temp.setProtocol(parentWindow.vcModel.getProtocol());
				temp.setPort(parentWindow.vcModel.getPort());
				temp.setPassword(model.getPassword());
				temp.setUsername(model.getUsername());
				temp.setUUID(model.getVMUUID());
				temp.setVmInstanceUUID(model.getVmInstanceUUID());
//				temp.setEsxServerName(model.getEsxServer());
				temp.setVMName(model.getVmName());
				temp.setVmVMX(model.getVmVMX());
				temp.setVmHostName(model.getVmHostName());
				backupModelList.add(temp);
			}
		}
		return backupModelList;
	}
	
	public ListStore<VMItemModel> getGridStore() {
		return gridStore;
	}

	public void setGridStore(ListStore<VMItemModel> gridStore) {
		this.gridStore = gridStore;
	}

	public Grid<VMItemModel> getVmGrid() {
		return vmGrid;
	}

	public void setVmGrid(Grid<VMItemModel> vmGrid) {
		this.vmGrid = vmGrid;
	}


}
