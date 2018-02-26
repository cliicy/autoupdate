package com.ca.arcflash.ui.client.restore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.FlashCheckBox;
import com.ca.arcflash.ui.client.common.PathSelectionPanel;
import com.ca.arcflash.ui.client.common.UserPasswordWindow;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.BackupVolumeModel;
import com.ca.arcflash.ui.client.model.CatalogModelType;
import com.ca.arcflash.ui.client.model.FileModel;
import com.ca.arcflash.ui.client.model.VMItemModel;
import com.ca.arcflash.ui.client.model.VirtualCenterModel;
import com.ca.arcflash.ui.client.model.FileSystemType;
import com.ca.arcflash.ui.client.model.GridTreeNode;
import com.ca.arcflash.ui.client.model.RecoveryPointItemModel;
import com.ca.arcflash.ui.client.model.VolumeModel;
import com.ca.arcflash.ui.client.model.VolumeSubStatus;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanelSelectionModel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanelView;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.TreeNode;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanelView.TreeViewRenderMode;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;

public class BrowseWindow extends Dialog {

	private BrowseWindow thisWindow;
	private boolean showFiles = false;
	public TreeStore<FileModel> store;
	private MyTreePanel tree;
	private TreeLoader<FileModel> loader;
	private TextField<String> folderField;	
	private FieldSet notificationSet;
	private String lastClicked;
	private LabelField treeStatusLabel;
	public LayoutContainer treeContainer;
	final LoginServiceAsync service = GWT.create(LoginService.class);

	private String inputFolder;
	private String user;
	private String password;
	private boolean isTreeCreated = false;
	private List<FileModel> networkPathDrive = null;
	private int testMode = 0;
	
	private LayoutContainer buttonContainer;
	private static final String volumeGuid = "\\\\?\\Volume{";
	String parentDir;
	
	public static String localDriveType = "localDrive";
	public static String networkDriveType = "networkDrive";
	private static String actualPath;
	private static String fileName;
	private List<FileModel> FATVolumes = null;
	private List<FileModel> RefsDedupVolumes = null;
	private BackupVolumeModel selectedBackupVolumes;
	private int browseClient=0;
	private String cancelID = "7BCCEC1E-7A1C-42e8-9247-82E1DF27D3A1";
	private String okID = "01155BB0-71B7-4f35-A550-0038F662FB9C"; 
	private List<FileModel> currentVolumes = null;
	private boolean isForVM;
	private VirtualCenterModel vcModel;
	private VMItemModel vmModel;
	private boolean PathSelectionMode = false;
	
	public boolean isPathSelectionMode() {
		return PathSelectionMode;
	}

	public void setPathSelectionMode(boolean pathSelectionMode) {
		PathSelectionMode = pathSelectionMode;
	}

	public void setBrowseClient(int browseClient){
		this.browseClient = browseClient;
	}

	public String getInputFolder() {
		return inputFolder;
	}

	public void setInputFolder(String inputFolder) {
		if(inputFolder!=null) {
			inputFolder = inputFolder.trim();
		}
		this.inputFolder = inputFolder;
		actualPath = inputFolder;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isRemote() {
		if(this.isGUIDPath(inputFolder))
			return false;
		return inputFolder != null && inputFolder.startsWith("\\\\");
	}

	RpcProxy<List<FileModel>> proxy = new RpcProxy<List<FileModel>>() {
		@Override
		protected void load(Object loadConfig,
				final AsyncCallback<List<FileModel>> callback) {
			GWT.log("Proxy Load", null);
			FileModel fileModel = (FileModel) loadConfig;
			String fullPath = fileModel != null ? fileModel.getPath() : null;
			
			if(isNetworkPath(fileModel))
			{
				fullPath = fileModel.getNetworkPath();
				if(fullPath == null || fullPath.length() == 0)
					fullPath = fileModel.getPath() + "\\" + fileModel.getName();
				
				String userName = fileModel.getUserName();
				if(userName == null || userName.length() == 0) {
					showUsernamePasswordDialog(fullPath, fileModel);
					return;
				}
				
				final String newUserName = fileModel.getUserName();
				final String newPasswd = fileModel.getPassword();
				
				AsyncCallback<List<FileModel>> proxyCallBack = new BaseAsyncCallback<List<FileModel>>() {
					
					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}
					
					@Override
					public void onSuccess(List<FileModel> result) {
						int count = result == null ? 0 : result.size();
						for (int i = 0; i < count; i++) {
							result.get(i).setIsNetworkPath(Boolean.TRUE);
							result.get(i).setUserName(newUserName);
							result.get(i).setPassword(newPasswd);
						}
						callback.onSuccess(result);
					}
				};
				
				service.getFileItems(fullPath, newUserName, newPasswd, false,0, proxyCallBack);
			}
			else if (fullPath == null){
				AsyncCallback<List<FileModel>> proxyCallBackFinal = getVolumeCallBack(callback);
				if(isForVM){
					service.getVolumes(vcModel, vmModel, proxyCallBackFinal);
				}else{
					service.getVolumes(browseClient,proxyCallBackFinal);
				}
				
			}
			else {
				if(((FileModel) loadConfig).isVolume() == null || !((FileModel) loadConfig).isVolume())
				{
//					fullPath = ((FileModel) loadConfig).getPath() + "\\"+ ((FileModel) loadConfig).getName();
					String name = ((FileModel) loadConfig).getName();
					if(name!=null && name.length()>0) {
						fullPath = ((FileModel) loadConfig).getPath() + "\\"+ ((FileModel) loadConfig).getName();
					}
				}
				String userName = fileModel.getUserName();
				if(userName == null || userName.length() == 0) {
					userName = user;
				}
				if (userName == null)
					userName = "";

				String passwd = fileModel.getPassword();
				if (passwd == null || passwd.length() == 0) {
					passwd = password;
				}
				
				if(passwd == null)
					passwd = "";
				if(isForVM){
					service.getFileItems(fullPath, showFiles,vcModel, vmModel, callback);
				}else{
					service.getFileItems(fullPath, userName, passwd, showFiles, browseClient, callback);
				}
				
			}
		}
	};
	private boolean bFilterVolumes = false;

	public BrowseWindow(boolean showFiles, String title) {
		super();
		this.showFiles = showFiles;
		this.thisWindow = this;
		this.setHeadingHtml(title);
	}
	
	public BrowseWindow(boolean showFiles, String title,boolean filterVolumes,List<FileModel> FATVolumesList,List<FileModel> RefsDedupVolumeList,BackupVolumeModel in_selectedBackupVolumes,boolean isForVM,VirtualCenterModel vcModel,VMItemModel vmModel) {
		this(showFiles,title,filterVolumes,FATVolumesList,RefsDedupVolumeList,in_selectedBackupVolumes);
		this.isForVM = isForVM;
		this.vcModel = vcModel;
		this.vmModel = vmModel;
	}
	
	public BrowseWindow(boolean showFiles, String title,boolean filterVolumes,List<FileModel> FATVolumesList,List<FileModel> RefsDedupVolumeList,BackupVolumeModel in_selectedBackupVolumes) {
		super();
		this.showFiles = showFiles;
		this.thisWindow = this;	
		if(title!=null&&title.length()>52){
			title = title.substring(0, 49)+"...";
		}
		this.setHeadingHtml(title);		
		this.bFilterVolumes = filterVolumes;
		this.FATVolumes = FATVolumesList;
		//Save refs and ntfs dedup volume, added by wanqi06
		this.RefsDedupVolumes = RefsDedupVolumeList;
		this.selectedBackupVolumes = in_selectedBackupVolumes;
	}

	public void addNetworkDrive(List<FileModel> networkDrive) {
		this.networkPathDrive = networkDrive;
		if(store != null && store.getChildCount() > 0 && networkPathDrive != null)
			store.add(networkPathDrive, false);
	}
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		this.setButtons(Dialog.OKCANCEL);

		LayoutContainer container = new LayoutContainer();
		RowLayout rl = new RowLayout();
		container.setLayout(rl);

		RowData rd = new RowData();
		Margins margins = new Margins();
		margins.left = 5;
		margins.right = 5;
		rd.setMargins(margins);
		
		LayoutContainer topContainer = new LayoutContainer();
		topContainer.setStyleAttribute("margin-right", "4px");
		TableLayout tableLayout = new TableLayout();
		tableLayout.setWidth("100%");
		tableLayout.setColumns(2);
		topContainer.setLayout(tableLayout);

		LabelField label = new LabelField();
		if(!showFiles)
			label.setValue(UIContext.Constants.browseSelectFolder());
		else 
			label.setValue(UIContext.Constants.browseSelectFile());
		topContainer.add(label);
		
		TableData tableData = new TableData();
		tableData.setHorizontalAlign(HorizontalAlignment.RIGHT);
		
		buttonContainer = new LayoutContainer();
		TableLayout tblLayout = new TableLayout();
		tblLayout.setColumns(2);
		buttonContainer.setLayout(tblLayout);
		buttonContainer.setWidth(32);
		
		Image upImage = new Image("./images/gxt/icons/up2.gif"); 
		upImage.ensureDebugId("0E276D4E-B443-4380-BB19-E9C9EC6DBF3B");
		upImage.setTitle(UIContext.Constants.browseWindowUpTooltip());
		upImage.setStyleName("homepage_task_icon");
		upImage.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				if (store!=null)
					store.removeAll();
				String parentFolder = getParentFolder();
				inputFolder = parentFolder;
				FileModel fileModel = new FileModel();
				fileModel.setName("");
				fileModel.setPath(inputFolder);
				folderField.setValue(inputFolder);
				
				if( parentFolder == null)
					isTreeCreated = false;
				
				if (isTreeCreated == false)
					createTree();
				else
					loader.load(fileModel);
			}
			
		});
		buttonContainer.add(upImage);
		
		//Create Dir
		Image createImage = new Image("./images/newfolder.gif");
		createImage.ensureDebugId("65DB88CC-73EF-4417-8CEA-3D402C2F76CB");
		createImage.setTitle(UIContext.Constants.browseWindowNewTooltip());
		createImage.setStyleName("homepage_task_icon");
		createImage.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				//Popup a dialog, create the folder from here
				
				if (thisWindow.folderField.getValue() != null && thisWindow.folderField.getValue().length() > 0)
					parentDir = thisWindow.folderField.getValue();
				else
				{
					List<FileModel> list = thisWindow.store.getRootItems();
					if (list != null && list.size() > 0){
						if(parentDir==null||parentDir==""){					
							MessageBox message = new MessageBox();
							message.setIcon(MessageBox.WARNING);
							message.setButtons(MessageBox.OK);
							message.setTitleHtml(UIContext.Constants.destinationFolderCreationTitle());
							message.setMessage(UIContext.Constants.destinationFolderCreationMessage());
							message.setModal(true);
							Utils.setMessageBoxDebugId(message);
							message.show();
							return;
						}
						
						parentDir = list.get(0).getPath();
					}else{
						return;
					}
				}
				
				String message = "<div style=\"word-wrap: break-word; word-break: break-all\"> " 
					+ UIContext.Messages.browseWindowCreateAFolderUnder(parentDir)
					+ "</div>";
				
				MessageBox box = MessageBox.prompt(UIContext.Constants.browseWindowNewTooltip(), 
						message, 
						new Listener<MessageBoxEvent>(){

					@Override
					public void handleEvent(MessageBoxEvent be) {
						if (be.getButtonClicked().getItemId()
								.equals(
										com.extjs.gxt.ui.client.widget.Dialog.OK))
						{	
							String subDir = be.getValue();
							if(testMode == PathSelectionPanel.DIAGNOSTIC_MODE)
							{
								if(subDir==null || subDir.length() == 0){
									Utils.showErrorMessage(UIContext.Constants.destinationInputEmpty());
									return;
								}
							}
							
							final String path;
							if (parentDir.endsWith("\\"))
								path = parentDir + subDir; 
							else
								path = parentDir + "\\" + subDir;
							if(parentDir == null || parentDir.length() == 0 || subDir == null || subDir.length() == 0)
								return;
							if(isForVM){
								service.createFolder(parentDir, subDir,vcModel,vmModel,createFolderHandler(path));
							}else{
								service.createFolder(parentDir, subDir,browseClient,createFolderHandler(path));
							}

						}
					}
					
				});
				
				Utils.setMessageBoxDebugId(box);
			}
			
		});
		
		if(testMode == PathSelectionPanel.ARCHIVE_RESTORE_MODE)
			createImage.setVisible(false);
		
		buttonContainer.add(createImage);		
		buttonContainer.hide();
		topContainer.add(buttonContainer, tableData);
		container.add(topContainer);

		treeContainer = new LayoutContainer();
		treeContainer.setLayout(new CenterLayout());
		treeContainer.setWidth(370);
		treeContainer.setHeight(210);
		container.add(treeContainer, rd);

		treeStatusLabel = new LabelField();
		treeStatusLabel.addStyleName("browseLoading");
		treeStatusLabel.setValue(UIContext.Constants.restoreLoading());
		treeStatusLabel.setWidth(190);
		treeContainer.add(treeStatusLabel);

		label = new LabelField();
		if(!showFiles)
			label.setValue(UIContext.Constants.browseFolderName());
		else
			label.setValue(UIContext.Constants.browseFileName());
		container.add(label, rd);

		folderField = new TextField<String>();
		folderField.ensureDebugId("6FDFA845-BBAB-46a8-8CC4-1A4CF852E8E1");
		folderField.setValue(this.getInputFolder());
		folderField.setWidth(370);
		folderField.disable();
		container.add(folderField, rd);
		
		notificationSet = new FieldSet();
		notificationSet.ensureDebugId("3C490EA4-3A30-4621-BA55-D127C049D9E5");
		notificationSet.setHeadingHtml(UIContext.Messages.backupSettingsNodifications(1));
		notificationSet.setCollapsible(true);
		TableLayout warningLayout = new TableLayout();
		warningLayout.setWidth("100%");
		warningLayout.setCellSpacing(1);
		warningLayout.setColumns(2);
		notificationSet.setLayout(warningLayout);
		notificationSet.setVisible(false);
		TableData data = new TableData();
		data.setWidth("100%");
		container.add(notificationSet,data);
	
		
		createTree();
		
		this.getButtonById(Dialog.CANCEL).setWidth(80);
		this.getButtonById(Dialog.CANCEL).setStyleAttribute("margin-left", "10px");
		this.getButtonById(Dialog.CANCEL).ensureDebugId(cancelID);
		this.getButtonById(Dialog.CANCEL).addSelectionListener(
				new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						lastClicked = Dialog.CANCEL;
						thisWindow.hide();
					}
				});
		
		this.getButtonById(Dialog.OK).setWidth(80);
		this.getButtonById(Dialog.OK).ensureDebugId(okID);
		this.getButtonById(Dialog.OK).addSelectionListener(
				new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						if(testMode == PathSelectionPanel.RESTORE_ALT_MODE){
							Map<GridTreeNode, RecoveryPointItemModel> rootItemMap = RestoreContext.getRootItemMap();
							if(rootItemMap !=null){
								boolean isExistNtfsDedupVolume = false;
								String fullVolumeName = "";
								String partialVolumeName = "";
								for(GridTreeNode node :rootItemMap.keySet()){
									RecoveryPointItemModel rpm = rootItemMap.get(node);
									if(rpm.getVolAttr() != null){
										int volAttr = rpm.getVolAttr();
										if((volAttr & RecoveryPointsPanel.NtfsVol)> 0 && (volAttr & RecoveryPointsPanel.DedupVol) > 0){
											if(node.getSelectionType()!=null){
												if(node.getSelectionType() == FlashCheckBox.FULL){
													isExistNtfsDedupVolume = true;
													fullVolumeName += rpm.getDisplayName() + " ";
												}else if (node.getSelectionType() == FlashCheckBox.PARTIAL){
													isExistNtfsDedupVolume = true;
													partialVolumeName += rpm.getDisplayName() + " ";
												}
											}
										}
									}
								}
								if(isExistNtfsDedupVolume){
									if(UIContext.serverVersionInfo.isWin8()){
										//win8 machine and dedup feature installed
										if(UIContext.serverVersionInfo.isDedupInstalled()){
											//restore to local path
											if(!isRemote()){
												if(!fullVolumeName.equals("")){
													VolumeModel volume = getVolume(folderField.getValue());
													if(volume != null){
														int type = volume.getFileSysType();
														if(type == FileSystemType.EFST_NTFS){
															if(!volume.getIsEmpty() && !((volume.getSubStatus() & VolumeSubStatus.EVSS_SYSTEM)>0)){
																MessageBox mb = new MessageBox();
																mb.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
																mb.setMessage(UIContext.Messages.restoreNtfsDedupVolumeToNonSysteomNonEmptyVolume(fullVolumeName,volume.getDisplayName()));
																mb.setModal(true);
																mb.setIcon(MessageBox.WARNING);
																mb.setButtons(MessageBox.YESNO);
																mb.addCallback(new Listener<MessageBoxEvent>()
																{
																	public void handleEvent(MessageBoxEvent be)
																	{
																		if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
																			lastClicked = Dialog.OK;
																			thisWindow.hide();
																		}
																	}
																});
																Utils.setMessageBoxDebugId(mb);
																mb.show();
																return;
															}
														}
													}
												}
											}
										// win8 machine but dedup feature not installed	
										}else{
											// select full volume to restore
											if(!fullVolumeName.equals("")){
												//restore to local path
												if(!isRemote()){
													VolumeModel volume = getVolume(folderField.getValue());
													if(volume != null){
														int type = volume.getFileSysType();
														int subStatus = volume.getSubStatus();
														// system or refs or fat is not supported 
														if(type == FileSystemType.EFST_FAT16 || type == FileSystemType.EFST_FAT32 || type == FileSystemType.EFST_REFS){
															String typeName = FileSystemType.getDisplayName(type);
															MessageBox errMsg = new MessageBox();
															errMsg.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
															errMsg.setMessage(UIContext.Messages.restoreNtfsDedupVolumeToSystemOrRefsOrFat(fullVolumeName,volume.getDisplayName(),typeName));
															errMsg.setModal(true);
															errMsg.setIcon(MessageBox.ERROR);
															Utils.setMessageBoxDebugId(errMsg);
															errMsg.show();
															return;
														}else if ((subStatus & VolumeSubStatus.EVSS_SYSTEM)>0){
															String subStatusName = VolumeSubStatus.getDisplayName(subStatus);
															MessageBox errMsg = new MessageBox();
															errMsg.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
															errMsg.setMessage(UIContext.Messages.restoreNtfsDedupVolumeToSystemOrRefsOrFat(fullVolumeName,volume.getDisplayName(),subStatusName));
															errMsg.setModal(true);
															errMsg.setIcon(MessageBox.ERROR);
															Utils.setMessageBoxDebugId(errMsg);
															errMsg.show();
															return;
														}else if(type == FileSystemType.EFST_NTFS){
															if(!((volume.getSubStatus() & VolumeSubStatus.EVSS_SYSTEM)>0)){
																if(!volume.getIsEmpty()){
																	MessageBox errMsg = new MessageBox();
																	errMsg.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
																	errMsg.setMessage(UIContext.Messages.restoreNtfsDedupVolumeToNonSysteomNonEmptyVolumeWin8(fullVolumeName,volume.getDisplayName()));
																	errMsg.setModal(true);
																	errMsg.setIcon(MessageBox.ERROR);
																	Utils.setMessageBoxDebugId(errMsg);
																	errMsg.show();
																	return;
																}else{
																	MessageBox mb = new MessageBox();
																	mb.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
																	mb.setMessage(UIContext.Messages.restoreNtfsDedupVolumeToNonSysteomEmptyVolume(fullVolumeName,volume.getDisplayName()));
																	mb.setModal(true);
																	mb.setIcon(MessageBox.WARNING);
																	mb.setButtons(MessageBox.YESNO);
																	mb.addCallback(new Listener<MessageBoxEvent>()
																	{
																		public void handleEvent(MessageBoxEvent be)
																		{
																			if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
																				lastClicked = Dialog.OK;
																				thisWindow.hide();
																			}
																		}
																	});
																	Utils.setMessageBoxDebugId(mb);
																	mb.show();
																	return;
																}
															}
														}
													}
												//restore to remote path
												}else{
													MessageBox errMsg = new MessageBox();
													errMsg.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
													errMsg.setMessage(UIContext.Messages.restoreVolumeSourceIsNTFSDedupVolumeError(fullVolumeName));
													errMsg.setModal(true);
													errMsg.setIcon(MessageBox.ERROR);
													Utils.setMessageBoxDebugId(errMsg);
													errMsg.show();
													return;
												}
											//select file to restore
											}else if (!partialVolumeName.equals("")){
												MessageBox errMsg = new MessageBox();
												errMsg.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
												errMsg.setMessage(UIContext.Messages.restoreFileSourceIsNTFSDedupVolumeError(fullVolumeName));
												errMsg.setModal(true);
												errMsg.setIcon(MessageBox.ERROR);
												Utils.setMessageBoxDebugId(errMsg);
												errMsg.show();
												return;
											}
										}
									}
								}
							}
						}
						lastClicked = Dialog.OK;
						thisWindow.hide();
					}
				});

		this.add(container);
		this.setSize(400, 355);
	}

	private BaseAsyncCallback<Void> createFolderHandler(final String path){
		return new BaseAsyncCallback<Void>(){

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
			}

			@Override
			public void onSuccess(Void result) {
				thisWindow.folderField.setValue(path);
				if (thisWindow.tree != null && thisWindow.tree.getSelectionModel().getSelectedItem() != null)
				{
					FileModel file = thisWindow.tree.getSelectionModel().getSelectedItem();
					store.removeAll(file);
					file.setCreateFolder(path);
					thisWindow.loader.loadChildren(file);	
					
				}
				else
				{
					if (inputFolder.compareTo(parentDir) == 0)
					{
						if (store!=null)
							store.removeAll();
						FileModel fileModel = new FileModel();
						fileModel.setName("");
						fileModel.setPath(inputFolder);
						if(inputFolder.endsWith("\\\\"));
						{
//							fileModel.setIsNetworkPath(Boolean.TRUE);
							fileModel.setUserName(user);
							fileModel.setPassword(password);
						}
						if(loader != null)
						  loader.load(fileModel);
					}
				}
			}
			
		};
	}
	public String getLastClicked() {
		return lastClicked;
	}

	public String getDestination() {
		if (folderField != null) {
			return folderField.getValue();
		}
		return "";
	}

	public boolean isListRoots() {
		return inputFolder == null || inputFolder.trim().length() == 0;
	}

	public void createTree() {
		try {
			if (!isListRoots() || isRemote()) {
				if (user == null)
					user = "";

				if (password == null)
					password = "";

				if(showFiles)
				{
					int iIndex = inputFolder.lastIndexOf("\\");
					inputFolder = actualPath.substring(0, iIndex);
					fileName = actualPath.substring(iIndex+1,actualPath.length());
				}
				
				if(isForVM){
					service.getFileItems(inputFolder, showFiles,vcModel, vmModel, callback);
				}else{
					service.getFileItems(inputFolder, this.user, this.password,
							showFiles, browseClient, callback);
				}
			} else {
				if(isForVM){
					service.getVolumes(vcModel, vmModel, getVolumeCallBack(callback));
				}else{
					service.getVolumes(browseClient,getVolumeCallBack(callback));
				}
			}
		} catch (Exception e) {
			treeContainer.setLayoutOnChange(true);
			treeStatusLabel.setValue(UIContext.Constants
					.restoreUnableToFindVolumes());
		}
	}
	
	public void showUsernamePasswordDialog_for_top( String path)
	{
		String user = ""; 
		if (getUser() != null)
		{
			user = getUser() ;
		}
		String pwd = ""; 
		if (getPassword() != null)
		{
			pwd = getPassword();
		}
		
		final UserPasswordWindow dlg = new UserPasswordWindow(path, user, pwd);
		
		dlg.setModal(true);
		
		dlg.addWindowListener(new WindowListener()
		{				
			public void windowHide(WindowEvent we) {
				//TODO: Only do this on ok
				if (dlg.getCancelled() == false)
				{
					setUser(dlg.getUsername());
					setPassword(dlg.getPassword());
					
					service.getFileItems(inputFolder, getUser(), getPassword(),false, browseClient, callback);
				}else{
					treeStatusLabel.setValue(UIContext.Constants.browseFailed());
					thisWindow.hide();
				}
			}
		});
		dlg.show();
	}
	public void showUsernamePasswordDialog(String path, final FileModel config)
	{
		String user = ""; 
		if (getUser() != null)
		{
			user = getUser() ;
		}
		String pwd = ""; 
		if (getPassword() != null)
		{
			pwd = getPassword();
		}
		
		final UserPasswordWindow dlg = new UserPasswordWindow(path, user, pwd);
		
		dlg.setModal(true);
		dlg.setMode(testMode);
		
		dlg.addWindowListener(new WindowListener()
		{				
			public void windowHide(WindowEvent we) {
				//TODO: Only do this on ok
				if (dlg.getCancelled() == false)
				{
					String username = dlg.getUsername();
					setUser(username);
					String passwd = dlg.getPassword();
					setPassword(passwd);
					config.setUserName(username);
					config.setPassword(passwd);
					if(loader != null)
						  loader.loadChildren(config);
				}
				else
				{
					
					TreeNode node = tree.getNode(config);
					//
					if(node!=null)
						tree.getView().collapse(node);
				}
			}
		});
		dlg.show();
	}
	private BaseAsyncCallback<List<FileModel>> callback = new BaseAsyncCallback<List<FileModel>>() {

		@Override
		public void onFailure(Throwable caught) {
			buttonContainer.show();
			treeContainer.setLayoutOnChange(true);
			if (isRemote() || !isListRoots()) {
				if(caught instanceof BusinessLogicException)
				{
					String errorCode = ((BusinessLogicException)caught).getErrorCode();
					// FlashServiceErrorCode.Browser_GetFolderFailed
						if(errorCode!=null && errorCode.equals("12884901893"))
						{
							showUsernamePasswordDialog_for_top(getDestination());
	
						}
						//FlashServiceErrorCode.Browser_GetFolder_No_Content
						else if(errorCode != null && errorCode.equals("12884901894")) {
							treeContainer.setLayoutOnChange(true);
							treeStatusLabel.setValue(UIContext.Constants
									.noShareFolderAvailable());
						}
						else
						{
							super.onFailure(caught);
							treeStatusLabel.setValue(UIContext.Constants.browseFailed());
						}
				}
				else
				{	
					super.onFailure(caught);
					treeStatusLabel.setValue(UIContext.Constants.browseFailed());
				}
			} else {
				super.onFailure(caught);
				treeStatusLabel.setValue(UIContext.Constants
						.restoreUnableToFindVolumes());
			}
		}

		@Override
		public void onSuccess(List<FileModel> result) {
			
			if (result == null || result.size() == 0) {
				// No volumes found
				treeContainer.setLayoutOnChange(true);
				if (isRemote() || !isListRoots()) {
					treeStatusLabel.setValue(UIContext.Constants
							.browseEmptyFolder());
				} else {
					treeStatusLabel.setValue(UIContext.Constants
							.restoreUnableToFindVolumes());
				}
			} else {
				treeStatusLabel.setVisible(false);
				// Create the Loader
				loader = new BaseTreeLoader<FileModel>(proxy) {
					@Override
					public boolean hasChildren(FileModel parent) {
						if(parent != null)
						{
							Integer type = parent.getType();
							if (type != null && type == CatalogModelType.File) {
								return false;
							} else {
								return true;
							}
						}
						else
						{
							return false;
						}
					}
				};
				loader.addLoadListener(new LoadListener() {

					@Override
					public void loaderLoad(LoadEvent le) {
						// TODO Auto-generated method stub
						//super.loaderLoad(le);
						Object config = le.getConfig();
						if(config instanceof FileModel) {
							FileModel fileModel = (FileModel)config;
							String createFolderPath = fileModel.getCreateFolderPath();
							if((createFolderPath!=null)&&(createFolderPath.length()>0)) {
								
								tree.setExpanded(fileModel, true);
								List<FileModel> listFileModels = (List<FileModel>)le.getData();
								for (FileModel childFileModel : listFileModels) {
									String childDir = childFileModel.getPath()+"\\"+childFileModel.getName();
									if(childDir.equalsIgnoreCase(createFolderPath)) {
										
										fileModel.setCreateFolder("");
										TreePanelSelectionModel<FileModel> selectionModel = tree.getSelectionModel();
										//selectionModel.getSelectedItems().clear();
										//selectionModel.getSelectedItems().add(childFileModel);
										selectionModel.select(childFileModel, false);
										tree.setSelectionModel(selectionModel);
										break;
									}
								}

							}
						}
					}

					@Override
					public void loaderLoadException(LoadEvent le) {
						Object config = le.getConfig();
						boolean showErrDialog = true;
						
						if (config instanceof FileModel) {
							if (isRemote() || !isListRoots()) {
								String fullPath = ((FileModel) config)
										.getPath()
										+ "\\" + ((FileModel) config).getName();
								if (le.exception != null
										&& le.exception instanceof BusinessLogicException) {
									String errorCode = ((BusinessLogicException)le.exception).getErrorCode();
									// FlashServiceErrorCode.Browser_GetFolderFailed
										if(errorCode!=null && errorCode.equals("12884901893"))
									{
										showErrDialog = false;
										showUsernamePasswordDialog(fullPath,(FileModel)config);
									}
								}
							}
						} 
						
						if (showErrDialog && le.exception != null) {
							if (config instanceof FileModel){
								tree.refresh((FileModel) config);
							}
							new BaseAsyncCallback().onFailure(le.exception);
						}

					}
				});
				// Create the treestore
				boolean emptyWindow = false;
				store = new TreeStore<FileModel>(loader);				
				for (int i = 0; i < result.size(); i++) {
					if(isRemote()) {
//						result.get(i).setIsNetworkPath(Boolean.TRUE);
						result.get(i).setUserName(user);
						result.get(i).setPassword(password);
					}
					
					boolean isGuidVolumeAllowed = true;	
					boolean bAddVolume = true;
					boolean beAddVolume = true;
					
					if(bFilterVolumes && FATVolumes != null && selectedBackupVolumes != null)
					{
						if(selectedBackupVolumes.getIsFullMachine())
						{
							bAddVolume = true;
							beAddVolume = true;
							//Check if refs or ntfs dedup volume, added by wanqi06
							if(RefsDedupVolumes != null && RefsDedupVolumes.size() > 0) {
								for (FileModel volume : RefsDedupVolumes) {
									String volName = volume.getName();
									if(!volName.endsWith("\\")) {
										volName += "\\";
									}
									
									if (inputFolder==null || inputFolder.length() == 0)
									{
										if(volName.compareToIgnoreCase(result.get(i).getName()) != 0)
										{
											beAddVolume = true;
										}
										else
										{
											beAddVolume = false;
											break;
										}
									}
									else
									{
										int iIndex = inputFolder.indexOf(":");
										String volumeName = "";
										if(iIndex != -1)
										{
											volumeName = inputFolder.substring(0,iIndex);
										}
										
										String givenVolumeName = "";
										int ivolNameIndex = volName.indexOf(":");
										if(ivolNameIndex != -1)
										{
											givenVolumeName = volName.substring(0,ivolNameIndex);
										}
										if(givenVolumeName.compareToIgnoreCase(volumeName) != 0)
										{
											beAddVolume = true;
										}
										else
										{
											beAddVolume = false;
											break;
										}
									}
								}									
							}
			
							for (FileModel volume : FATVolumes) {
								String volName = volume.getName();
								if(!volName.endsWith("\\")) {
									volName += "\\";
								}
								
								if (inputFolder==null || inputFolder.length() == 0)
								{
									if(volName.compareToIgnoreCase(result.get(i).getName()) != 0)
									{
										bAddVolume = true;
									}
									else
									{
										bAddVolume = false;
										break;
									}
								}
								else
								{
									int iIndex = inputFolder.indexOf(":");
									String volumeName = "";
									if(iIndex != -1)
									{
										volumeName = inputFolder.substring(0,iIndex);
									}
									
									String givenVolumeName = "";
									int ivolNameIndex = volName.indexOf(":");
									if(ivolNameIndex != -1)
									{
										givenVolumeName = volName.substring(0,ivolNameIndex);
									}
									if(givenVolumeName.compareToIgnoreCase(volumeName) != 0)
									{
										bAddVolume = true;
									}
									else
									{
										bAddVolume = false;
										break;
									}
								}
							}
						}
						else
						{	
							if(selectedBackupVolumes.backupSelectedVolumesList != null && selectedBackupVolumes.backupSelectedVolumesList.size() == 0)
							{
								//if there are no backup volumes are selected Empty window is shown
								bAddVolume = false;	
								emptyWindow = true;
								break;
							}
							else
							{							
								for (String selectedBackupVolumeName : selectedBackupVolumes.backupSelectedVolumesList) {
									
									selectedBackupVolumeName += "\\"; 
									
									if (inputFolder==null || inputFolder.length() == 0)
									{
										if(selectedBackupVolumeName.compareToIgnoreCase(result.get(i).getName()) != 0)
										{
											bAddVolume = false;
										}
										else
										{
											bAddVolume = true;
											break;
										}
									}
									else
									{
										int iIndex = inputFolder.indexOf(":");
										String volumeName = "";
										if(iIndex != -1)
										{
											volumeName = inputFolder.substring(0,iIndex);
										}
										
										String givenVolumeName = "";
										int ivolNameIndex = selectedBackupVolumeName.indexOf(":");
										if(ivolNameIndex != -1)
										{
											givenVolumeName = selectedBackupVolumeName.substring(0,ivolNameIndex);
										}
										if(givenVolumeName.compareToIgnoreCase(volumeName) != 0)
										{
											bAddVolume = false;
										}
										else
										{
											bAddVolume = true;
											break;
										}
									}
								}
							}
						}
					}
					
					if(bFilterVolumes)
					{
						String path = result.get(i).getNetworkPath();
						if(path != null && path.startsWith("\\\\"))
						{
							bAddVolume = false;
						}
						if(result.get(i) != null && result.get(i) instanceof VolumeModel)
						{
							String volume = result.get(i).getName();
							if(volume != null && (volume.startsWith(volumeGuid) || volume.length() > 3))
							{
								bAddVolume = false;
							}
						}
					}

					if(bAddVolume && beAddVolume)
					{
						if(testMode == PathSelectionPanel.BACKUP_MODE && isPathSelectionMode())
						{
							if(result.get(i) != null && result.get(i) instanceof VolumeModel)
							{
								String volume = result.get(i).getName();
								if(volume != null && (volume.startsWith(volumeGuid) || volume.length() > 3))
								{
									isGuidVolumeAllowed = false;
								}
							}
						}
					   if(isGuidVolumeAllowed)	
						 {
							 store.add(result.get(i), false);	
						 }
					}
				}
				if(bFilterVolumes)//below code will be executed only incase of FileCopy
				{
					List<String> volumes = new ArrayList<String>();						
					if(selectedBackupVolumes.getIsFullMachine())//For Full Machine
					{
						if(RefsDedupVolumes != null && RefsDedupVolumes.size() > 0) {
							for(FileModel volume : RefsDedupVolumes) {
								 if(!FATVolumes.contains(volume)) {
									 String volName = volume.getName(); 
									 String volType = volume.getIsDeduped().equalsIgnoreCase("1") ? UIContext.Messages.ntfsDedupeName() :UIContext.Messages.refsVolumeName();
									 volumes.add(volName +"("+ volType +")");
								 }	 
							 }
						}
					}
					else
					{
						volumes = selectedBackupVolumes.backupSelectedRefsDedupeVolumesListDetails;
					}					
					if(volumes != null && volumes.size() > 0)
					{	 
					   StringBuilder refsVolList = new StringBuilder();
						for(String volName : volumes)
						{								
							refsVolList.append(volName+" ");							
						}
						thisWindow.setSize(420, 420);
						thisWindow.setScrollMode(Scroll.AUTOY);						
						updateNotificationSet(UIContext.Messages.refsFileCopyDescription(refsVolList.toString()));
					}
				}
				
				
				if(!emptyWindow)
				{
					tree = new MyTreePanel(store);				
					tree.setBorders(true);
					tree.setStateful(true);
					tree.setDisplayProperty("name");
					tree.setWidth(370);
					tree.setHeight(210);			
	
					// selection listener
					tree.getSelectionModel().addSelectionChangedListener(
							new SelectionChangedListener<FileModel>() {
								@Override
								public void selectionChanged(
										SelectionChangedEvent<FileModel> se) {
									if(se.getSelectedItem() != null)
									{
										FileModel fileModel = se.getSelectedItem();
										String dest = se.getSelectedItem().getPath();
										if((dest!=null)&&dest.isEmpty())
											return;
										
										if(isNetworkPath(fileModel)) {
											if(dest == null || dest.length() == 0)
												dest = fileModel.getNetworkPath();
											else
												dest += "\\" + se.getSelectedItem().getName();
										}
										else if (fileModel.isVolume() == null || !fileModel.isVolume()) {
											dest += "\\" + se.getSelectedItem().getName();
	
										}
										thisWindow.folderField.setValue(dest);
										if(isNetworkPath(fileModel) || dest.startsWith("\\\\")) {
											if(se.getSelectedItem().getUserName() != null ) {
												setUser(se.getSelectedItem().getUserName());
												setPassword(se.getSelectedItem().getPassword());
											}
										}
										
									}
								}
	
							});
					treeContainer.setLayoutOnChange(true);
					treeContainer.setLayout(new CenterLayout());				
					treeContainer.add(tree);				
					isTreeCreated = true;
				}
				else
				{
					store = new TreeStore<FileModel>();
					tree = new MyTreePanel(store);
					tree.setBorders(true);
					tree.setStateful(true);
					tree.setDisplayProperty("name");
					tree.setWidth(370);
					tree.setHeight(210);					
					treeContainer.setLayoutOnChange(true);
					treeContainer.setLayout(new CenterLayout());				
					treeContainer.add(tree);				
					isTreeCreated = true;
					
				}
			}
			//After dir tree is created, show topContainer
			buttonContainer.show();
		}
	};
	
	public void updateNotificationSet(String message)
	{
		removeNotificationSet();		
		addWaringIcon();	
		notificationSet.add(new LabelField(message));
		notificationSet.setVisible(true);
		notificationSet.expand();	
		notificationSet.layout(true);							

	}
	
	
	public void removeNotificationSet()
	{
		notificationSet.removeAll();
		notificationSet.setVisible(false);	
	}
	
	private void addWaringIcon() {
		Image warningImage = getWaringIcon();
		TableData tableData = new TableData();
		tableData.setStyle("padding: 2px 3px 3px 0px;"); // refer to the GWT default setting.
		tableData.setVerticalAlign(VerticalAlignment.TOP);
		notificationSet.add(warningImage, tableData);
	}
	
	private Image getWaringIcon() {
		Image warningImage = AbstractImagePrototype.create(UIContext.IconBundle.logWarning()).createImage();
		return warningImage;
	}
	
	private String getParentFolder(){
		if (inputFolder==null || inputFolder.length() == 0)
			return null;
		
		if (inputFolder.charAt(inputFolder.length()-1) == '\\')
			inputFolder = inputFolder.substring(0,inputFolder.length()-1);
		
		if(this.isGUIDPath(inputFolder)) {
			int index1 = inputFolder.indexOf("\\",5);
			int index2 = inputFolder.lastIndexOf("\\");
			
			if (index1 == -1 || index2<index1)
				return null;
			else
				return inputFolder.substring(0,index2);
		}if (this.isRemote()){
			if(networkPathDrive != null && networkPathDrive.size() > 0)
			{
				for (int i = 0, count = networkPathDrive.size(); i < count; i++) {
					String reomtePath = networkPathDrive.get(i).getNetworkPath();
					if (reomtePath != null && reomtePath.charAt(reomtePath.length()-1) == '\\')
						reomtePath = reomtePath.substring(0,reomtePath.length()-1);
					if(inputFolder.equalsIgnoreCase(reomtePath))
						return null;
				}
			}
			
			int index1 = inputFolder.indexOf("\\",3);
			int index2 = inputFolder.lastIndexOf("\\");
			
			// liuwe05 2010-12-25 fix Issue: 19934868    Title: DESTINATION BROWSE CANNOT BACK
			// if index1 == -1, it means the inputFolder is like this \\machine, so there is no upper folder
			if (index1 == -1 || index2<=index1)
				return inputFolder;
			else
				return inputFolder.substring(0,index2);
		}else{
			int index = inputFolder.lastIndexOf("\\");
			if (index>=0){
				return inputFolder.substring(0,index);
			}else
				return null;
		}
	}
	
	public void setMode(int mode) {
		testMode = mode;
	}

	private AsyncCallback<List<FileModel>> getVolumeCallBack(
			final AsyncCallback<List<FileModel>> callBack) {
		AsyncCallback<List<FileModel>> volumeCallBack = new BaseAsyncCallback<List<FileModel>>() {
			
			@Override
			public void onFailure(Throwable caught) {
				callBack.onFailure(caught);
			}
			
			@Override
			public void onSuccess(List<FileModel> result) {
				for (int i = 0, count = result == null ? 0 : result.size(); i < count; i++) {
					FileModel fileModel = result.get(i);
					fileModel.setIsVolume(true);
					fileModel.set("driveType", localDriveType);
				}
				
				if(networkPathDrive != null) {
					result = result == null ? new ArrayList<FileModel>() : result;
					result.addAll(networkPathDrive);
					for (int i = 0, count = networkPathDrive.size(); i < count; i++) {
						FileModel fileModel = networkPathDrive.get(i);
						fileModel.set("driveType", networkDriveType);
					}
				}
				currentVolumes = result;
				callBack.onSuccess(result);
			}
		};
		return volumeCallBack;
	}
	
	private VolumeModel getVolume(String folderPath){
		String volumeName = folderPath.split("\\/")[0];
		if(currentVolumes != null){
			for(FileModel file: currentVolumes){
				if(file.getName().equals(volumeName)){
					return (VolumeModel)file;
				}
			}
		}
		return null;
	}

	private boolean isNetworkPath(FileModel fileModel) {
		return fileModel != null && fileModel.isNetworkPath() != null && fileModel.isNetworkPath();
	}
	
	private boolean isGUIDPath(String path) {
		return path != null && path.startsWith("\\\\?\\");
	}
	
	public void setDebugID(String cancelID, String okID) {
		this.cancelID = cancelID;
		this.okID = okID;
	}
}

class MyTreePanelView<M extends ModelData> extends TreePanelView<FileModel> {
	 public void onTextChange(TreeNode node, String text) {
		 Element textEl = getTextElement(node);
		 if (textEl != null) {
			 if(GXT.isIE) {
				 text = text.replace("&nbsp;", " ");
				 textEl.setInnerText(Util.isEmptyString(text) ? "&#160;" : text);
			 }else {
				 text = text.replace(" ", "&nbsp;");
				 textEl.setInnerHTML(Util.isEmptyString(text) ? "&#160;" : text);
			 }
		 }
	 }
}

class MyTreePanel extends TreePanel<FileModel>{
	
	public MyTreePanel(TreeStore<FileModel> store) {
		super(store);
		view = new MyTreePanelView<FileModel>();
		view.bind(this, store);
	}	
	
	public TreeNode getNode(FileModel model){
		return this.findNode(model);
	}	

	@Override
	public void refresh(FileModel model) {		
		super.refresh(model);
	}
	
	protected String getText(FileModel model) {
		String text = model.getName();
		text = text.replace(" ", "&nbsp;");
		return text;
	}
	
	@Override
	protected AbstractImagePrototype calculateIconStyle(FileModel model) {
		String driveType = (String)model.get("driveType");
		if(driveType != null) {
			if(BrowseWindow.networkDriveType.equals(driveType))
				return IconHelper.createStyle("network-drive-icon");
			else
				return IconHelper.createStyle("drive-icon");
		}
		else if(model != null)
		{
			switch(model.getType())
			{
			case CatalogModelType.File:
				return IconHelper.createStyle("file-icon");
			case CatalogModelType.Folder:
				return IconHelper.createStyle("folder-icon");
			}
		}
		return super.calculateIconStyle(model);
	};
}
