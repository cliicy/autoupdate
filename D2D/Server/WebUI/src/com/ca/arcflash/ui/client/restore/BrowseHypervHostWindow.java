package com.ca.arcflash.ui.client.restore;

import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.UserPasswordWindow;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.CatalogModelType;
import com.ca.arcflash.ui.client.model.FileModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.TreeNode;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanelSelectionModel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;

public class BrowseHypervHostWindow extends Dialog {

	private BrowseHypervHostWindow thisWindow;
	public TreeStore<FileModel> store;
	private MyTreePanel tree;
	private TreeLoader<FileModel> loader;
	private TextField<String> folderField;	
	private FieldSet notificationSet;
	private String lastClicked;
	private LabelField treeStatusLabel;
	public LayoutContainer treeContainer;
	public static String localDriveType = "localDrive";
	public static String networkDriveType = "networkDrive";
	final LoginServiceAsync service = GWT.create(LoginService.class);

	private String inputFolder;
	private String hypervServer;
	private String hypervUser;
	private String hypervPassword;
	private boolean isTreeCreated = false;
	
	private LayoutContainer buttonContainer;
	String parentDir;
	
	private String cancelID = "BF79C026-1C1C-407F-A937-DC1397AE2347";
	private String okID = "E64634B3-1509-4DC3-AFDB-F539247105CF"; 

	public String getInputFolder() {
		return inputFolder;
	}

	public void setInputFolder(String inputFolder) {
		if(inputFolder!=null) {
			inputFolder = inputFolder.trim();
		}
		this.inputFolder = inputFolder;
	}

	public String getHypervServer() {
		return hypervServer;
	}

	public void setHypervServer(String hypervServer) {
		this.hypervServer = hypervServer;
	}
	
	public String getHypervUser() {
		return hypervUser;
	}

	public void setHypervUser(String user) {
		this.hypervUser = user;
	}

	public String getHypervPassword() {
		return hypervPassword;
	}

	public void setHypervPassword(String password) {
		this.hypervPassword = password;
	}

	RpcProxy<List<FileModel>> proxy = new RpcProxy<List<FileModel>>() {
		@Override
		protected void load(Object loadConfig,final AsyncCallback<List<FileModel>> callback) {
			FileModel fileModel = (FileModel) loadConfig;
			String fullPath = fileModel != null ? fileModel.getPath() : null;
			service.browseHyperVHostFolder(hypervServer, hypervUser, hypervPassword, fullPath, callback);

		}
	};

	public BrowseHypervHostWindow(String title, String hypervServer, String hypervUserName, String hypervPassword) {
		super();
		this.thisWindow = this;
		this.setHeadingHtml(title);
		this.setHypervServer(hypervServer);
		this.setHypervUser(hypervUserName);
		this.setHypervPassword(hypervPassword);
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		this.setButtons(Dialog.OKCANCEL);

		LayoutContainer container = new LayoutContainer();
		RowLayout rl = new RowLayout();
		container.setLayout(rl);

		final RowData rd = new RowData();
		final Margins margins = new Margins();
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
		label.setValue(UIContext.Constants.browseSelectFolder());
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
				treeContainer.setLayoutOnChange(true);
				treeStatusLabel.setVisible(true);
				treeContainer.add(treeStatusLabel, rd);
				treeStatusLabel.setValue(UIContext.Constants.restoreLoading());
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
		createImage.ensureDebugId("A625A47B-CC45-46E1-89D4-2ACB3842C069");
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
								.equals(com.extjs.gxt.ui.client.widget.Dialog.OK))
						{	
							String subDir = be.getValue();
							final String path;
							if (parentDir.endsWith("\\"))
								path = parentDir + subDir; 
							else
								path = parentDir + "\\" + subDir;
							if(parentDir == null || parentDir.length() == 0 || subDir == null || subDir.length() == 0)
								return;
							service.createHyperVHostFolder(hypervServer, hypervUser, hypervPassword, parentDir, subDir, createFolderHandler(path));
						}
					}
					
				});
				
				Utils.setMessageBoxDebugId(box);
			}
			
		});
		
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
		label.setValue(UIContext.Constants.browseFolderName());
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
//		this.getButtonById(Dialog.CANCEL).addSelectionListener(
//				new SelectionListener<ButtonEvent>() {
//					@Override
//					public void componentSelected(ButtonEvent ce) {
//						lastClicked = Dialog.CANCEL;
//						thisWindow.hide();
//					}
//				});
		
		this.getButtonById(Dialog.OK).setWidth(80);
		this.getButtonById(Dialog.OK).ensureDebugId(okID);
//		this.getButtonById(Dialog.OK).addSelectionListener(
//				new SelectionListener<ButtonEvent>() {
//					@Override
//					public void componentSelected(ButtonEvent ce) {
//						lastClicked = Dialog.OK;
//						thisWindow.hide();
//					}
//				});

		this.add(container);
		this.setSize(400, 355);
	}

	private BaseAsyncCallback<Boolean> createFolderHandler(final String path){
		return new BaseAsyncCallback<Boolean>(){

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
			}

			@Override
			public void onSuccess(Boolean result) {
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
							fileModel.setUserName(hypervUser);
							fileModel.setPassword(hypervPassword);
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
			service.browseHyperVHostFolder(getHypervServer(), getHypervUser(), getHypervPassword(),inputFolder, callback);
		} catch (Exception e) {
			treeContainer.setLayoutOnChange(true);
			treeStatusLabel.setValue(UIContext.Constants.restoreUnableToFindVolumes());
		}
	}
	
	public void showUsernamePasswordDialog_for_top( String path)
	{
		String user = ""; 
		if (getHypervUser() != null)
		{
			user = getHypervUser() ;
		}
		String pwd = ""; 
		if (getHypervPassword() != null)
		{
			pwd = getHypervPassword();
		}
		
		final UserPasswordWindow dlg = new UserPasswordWindow(path, user, pwd);
		
		dlg.setModal(true);
		
		dlg.addWindowListener(new WindowListener()
		{				
			public void windowHide(WindowEvent we) {
				if (dlg.getCancelled() == false)
				{
					setHypervUser(dlg.getUsername());
					setHypervPassword(dlg.getPassword());
					service.browseHyperVHostFolder(getHypervServer(), getHypervUser(), getHypervPassword(),inputFolder, callback);
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
		if (getHypervUser() != null)
		{
			user = getHypervUser() ;
		}
		String pwd = ""; 
		if (getHypervPassword() != null)
		{
			pwd = getHypervPassword();
		}
		
		final UserPasswordWindow dlg = new UserPasswordWindow(path, user, pwd);
		
		dlg.setModal(true);
		
		dlg.addWindowListener(new WindowListener()
		{				
			public void windowHide(WindowEvent we) {
				if (dlg.getCancelled() == false)
				{
					String username = dlg.getUsername();
					setHypervUser(username);
					String passwd = dlg.getPassword();
					setHypervPassword(passwd);
					config.setUserName(username);
					config.setPassword(passwd);
					if(loader != null)
						  loader.loadChildren(config);
				}
				else
				{
					TreeNode node = tree.getNode(config);
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
			if(folderField.getValue() != null && !folderField.getValue().isEmpty()) {
				service.browseHyperVHostFolder(hypervServer, hypervUser, hypervPassword, "", callback);
				folderField.clear();
			} else {
				super.onFailure(caught);
				treeStatusLabel.setValue(UIContext.Constants.browseFailed());
			}
		}

		@Override
		public void onSuccess(List<FileModel> result) {
			
			if (result == null || result.size() == 0) {
				// No volumes found
				treeContainer.setLayoutOnChange(true);
				if (!isListRoots()) {
					treeStatusLabel.setValue(UIContext.Constants.browseEmptyFolder());
				} else {
					treeStatusLabel.setValue(UIContext.Constants.restoreUnableToFindVolumes());
				}
			} else {
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
										selectionModel.select(childFileModel, false);
										tree.setSelectionModel(selectionModel);
										break;
									}
								}

							}
						}
						treeStatusLabel.setVisible(false);
					}

					@Override
					public void loaderLoadException(LoadEvent le) {
						Object config = le.getConfig();
						boolean showErrDialog = true;
						
						if (config instanceof FileModel) {
							if (!isListRoots()) {
								String fullPath = ((FileModel) config)
										.getPath()
										+ "\\" + ((FileModel) config).getName();
								if (le.exception != null && le.exception instanceof BusinessLogicException) {
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
						treeStatusLabel.setVisible(false);
					}
				});
				store = new TreeStore<FileModel>(loader);				
				for (int i = 0; i < result.size(); i++) {
					 store.add(result.get(i), false);	
				}
				
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
									String dest = se.getSelectedItem().getPath();
									if((dest!=null)&&dest.isEmpty())
										return;
									thisWindow.folderField.setValue(dest);
								}
							}

						});
				treeContainer.setLayoutOnChange(true);
				treeContainer.setLayout(new CenterLayout());				
				treeContainer.add(tree);				
				isTreeCreated = true;
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
		
		int index = inputFolder.lastIndexOf("\\");
		if (index>=0){
			return inputFolder.substring(0,index);
		}else
			return null;
	}


	
	public void setDebugID(String cancelID, String okID) {
		this.cancelID = cancelID;
		this.okID = okID;
	}
	
	@Override
	protected void onButtonPressed(Button button) {
		if (button == getButtonBar().getItemByItemId(Dialog.CANCEL)) {
			lastClicked = Dialog.CANCEL;
		} else if (button == getButtonBar().getItemByItemId(Dialog.OK)) {
			lastClicked = Dialog.OK;
		} else {
			lastClicked = null;
		}
		thisWindow.setHideOnButtonClick(true);
		super.onButtonPressed(button);
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
				if(BrowseHypervHostWindow.networkDriveType.equals(driveType))
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
}
