package com.ca.arcflash.ui.client.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.ArchivePoliciesWindow;
import com.ca.arcflash.ui.client.common.icons.FlashImageBundle;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.BackupVolumeModel;
import com.ca.arcflash.ui.client.model.FileModel;
import com.ca.arcflash.ui.client.model.NetworkPathModel;
import com.ca.arcflash.ui.client.model.VMItemModel;
import com.ca.arcflash.ui.client.model.VirtualCenterModel;
import com.ca.arcflash.ui.client.restore.BrowseWindow;
import com.ca.arcflash.ui.client.restore.FSRestoreOptionsPanel;
import com.ca.arcflash.ui.client.restore.RestoreContext;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class PathSelectionPanel extends LayoutContainer {
	
	private BaseCommonSettingTab globalHost;
	
	private static int MIN_WIDTH = 85;
	
	private TextField<String> pathTF = new TextField<String>();
	private TextField<String> hiddenField = new TextField<String>();
	private Button validateButton = new Button();
	private Button browseButton = new Button();
	private PathSelectionPanel thisPanel;
	
	private String oldBackupDest;
	private String username;
	private String password;
	private String domain;
	private String userWithoutDomain;
	private Listener<FieldEvent> listener = null;
	private int mode = 0;
	private int tooltipmode = 0;
	private int pathFieldLength;
	
	final LoginServiceAsync service = GWT.create(LoginService.class);
	final CommonServiceAsync commonService = GWT.create(CommonService.class);
	public static final FlashImageBundle ICONBUNDLE = GWT.create(FlashImageBundle.class);
	
	public static final int BACKUP_MODE = 0;
	public static final int RESTORE_MODE = 1;	
	public static final int COPY_MODE = 2;

	public static final int RESTORE_ALT_MODE = 3;
	public static final int SELFUPDATE_MODE = 4;
	public static final int ARCHIVE_MODE = 5;
	public static final int ARCHIVE_CLOUD_CERTIFICATE_MODE = 6;
	public static final int ARCHIVE_RESTORE_MODE = 7;
	public static final int MOUNT_VOLUME_MODEL = 8;
	public static final int ARCHIVE_DEST_MODE = 9;
	public static final int RESTORE_ALT_VM_MODE = 10;
	// June sprint
	public static final int DIAGNOSTIC_MODE = 11;
	
	public static final int TOOLTIP_BACKUP_MODE = 0;
	public static final int TOOLTIP_RESTORE_MODE = 1;	
	public static final int TOOLTIP_RESTORE_ALT_MODE = 3;
	public static final int TOOLTIP_SELFUPDATE_MODE = 4;
	public static final int TOOLTIP_ARCHIVE_MODE = 5;
	public static final int TOOLTIP_ARCHIVE_CLOUD_CERTIFICATE_MODE = 6;
	public static final int TOOLTIP_ARCHIVE_SOURCE_MODE = 7;
	public static final int TOOLTIP_ARCHIVE_DESTINATION_MODE = 8;
	public static final int TOOLTIP_COPY_MODE = 9;
	public static final int TOOLTIP_RESTORE_ALT_VM_MODE = 10;
	// June sprint
	public static final int TOOLTIP_DIAGNOSTIC_MODE = 11;

	public static final String VALIDATE_BACKUP_DEST_Fail = "17179869200";
	public static final String VALIDATE_ARCHIVE_DEST_Fail = "51539607575";
	public static final String VALIDATE_SOURCE_FAIL = "25769803780";
	public static final String VALIDATE_COPY_DEST_FAIL = "30064771076";
	public static final String Restore_ERR_ValidateAltLocFailed="25769803790";
	public static final String MOUNT_VOLUME_VALIDATE_SOURCE_FAIL = "4294967313";
	public static final String INVALID_SHARE_FOLDER_PATH = "12884901903";
	// June sprint
	public static final String VALIDATE_DIAGNOSTIC_DEST_Fail = "17179869236";
	public static final int REMOTE_DRIVE = 4;
	private final String strErrorMessageTitle = UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D);
	
	private Listener<BaseEvent> changeListener = null;
	private List<Listener<BaseEvent>> changeListenerList = new ArrayList<Listener<BaseEvent>>();
	public static final EventType onDisconnectionEvent = new EventType();
	private boolean networkPathLoaded = false;
	private Map<String, String> letter2NetworkPathMap = new HashMap<String, String>(); 
	public static String localDriveLetter = null;
	public List<FileModel> networkDriveList;
	
	private boolean isForEdge = false;
	
	private boolean isNeedCreateFolder; // if need to create a new folder when the path is not exist.

	private boolean isBrowseBtnSelected = false;
	
	private List<FileModel> FATVolumesList = null;
	//refs and ntfs dedpu volume list, added by wanqi06
	private List<FileModel> RefsDedupVolumesList = null;
	private BackupVolumeModel selectedBackupVolumes = null;
	
	private String validatedPath = "";
	private String validatedUsername = "";
	private String validatedPassword = "";
	
	private String pathID = "F6F0C8A0-FF3B-4111-8523-4CF975CC05EF";
	private String validateID = "A86EF656-3B19-4ec0-AFF9-1FE084D42870";
	private String browseID = "A90CE7BE-C706-4543-B780-FC329DA34336";
	
	private boolean pathSelectionArchiveMode = false;	
	private FSRestoreOptionsPanel optionPanel;
	
	public boolean isPathSelectionArchiveMode() {
		return pathSelectionArchiveMode;
	}

	public void setPathSelectionArchiveMode(boolean pathSelectionArchiveMode) {
		this.pathSelectionArchiveMode = pathSelectionArchiveMode;
	}

	private ArchivePoliciesWindow ArchivePoliciesWindowobj = null;
	
	public void SetArchivePoliciesWindow(ArchivePoliciesWindow wdw) 
	{
		ArchivePoliciesWindowobj = wdw;
	}
	
	static {
		updateLocalDriverLetters();
	}
	//Update the latest local driver letters, added by wanqi06
	public static void updateLocalDriverLetters() {
		if(UIContext.serverVersionInfo==null){
			localDriveLetter="C";
			return;
		}
		List<String> drivers = UIContext.serverVersionInfo.getLocalDriverLetters();
		StringBuffer driverLetters = new StringBuffer();
		for (int i = 0, count = drivers.size(); i < count; i++) {
			String name = drivers.get(i);
			if(name.length() > 1 && name.indexOf(":") == 1)
				driverLetters.append(name.charAt(0));
			else if(name.length() == 1)
				driverLetters.append(name);
		}
		localDriveLetter = driverLetters.toString();
	}
	
	public PathSelectionPanel(Listener<FieldEvent> listener) {
		this(false, listener);
		this.isNeedCreateFolder = true;
		this.isForEdge = UIContext.isLaunchedForEdgePolicy;
	}
	
	public PathSelectionPanel(Listener<FieldEvent> listener,FSRestoreOptionsPanel panel) {
		this(false, listener);
		this.isNeedCreateFolder = true;
		this.isForEdge = UIContext.isLaunchedForEdgePolicy;
		this.optionPanel = panel;
	}
	
	public PathSelectionPanel( boolean isForEdge, Listener<FieldEvent> listener)
	{
		this.isForEdge = isForEdge;
		this.isNeedCreateFolder = true;
		thisPanel = this;
		this.listener = listener;
		commonService.getMappedNetworkPath(UIContext.loginUser, new BaseAsyncCallback<List<NetworkPathModel>>() {
			@Override
			public void onFailure(Throwable caught) {
				networkPathLoaded = true;
			}
			@Override
			public void onSuccess(List<NetworkPathModel> result) {
				networkPathLoaded = true;
				int count = result.size();
				if(count > 0) {
					for (int i = 0; i < count; i++) {
						String driverLetter = result.get(i).getDriverletter();
						if(driverLetter != null && driverLetter.length() > 0) {
							String remotePath = result.get(i).getRemotePath();
							if(remotePath.endsWith("\\"))
								remotePath = remotePath.substring(0, remotePath.length() - 1);
							letter2NetworkPathMap.put(driverLetter.substring(0, 1).toLowerCase(), remotePath);
						}
					}
				}
				
				if(count > 0) {
					networkDriveList = new ArrayList<FileModel>();
					for (int i = 0; i < count; i++) {
						FileModel model = new FileModel();
						model.setName( result.get(i).getDriverletter());
						model.setNetworkPath(result.get(i).getRemotePath());
						model.setIsNetworkPath(Boolean.TRUE);
						networkDriveList.add(model);
					}
					if(needToShowBrowserDialog != null) {
						needToShowBrowserDialog.addNetworkDrive(networkDriveList);
					    needToShowBrowserDialog = null;
					}
				}
					
				changeMappedNetworkPathWhenNeeded();
			}
		});
	}
	
	public PathSelectionPanel(Listener<FieldEvent> listener, boolean isNeedCreateFolder) {
		this(listener);
		this.isNeedCreateFolder = isNeedCreateFolder;
	}
	
	public PathSelectionPanel(boolean localBrowseEnabled, Listener<FieldEvent> listener, boolean isNeedCreateFolder) {
		this(localBrowseEnabled, listener);
		
		this.isNeedCreateFolder = isNeedCreateFolder;
	}
		
	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}
	
	public int getTooltipMode() {
		return tooltipmode;
	}
	
	public void setTooltipMode(int mode) {
		this.tooltipmode = mode;
	}

	private boolean isAllowBlank = true;

	protected BrowseWindow needToShowBrowserDialog = null;

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		// cannot add css for PathSelectionPanel, because it's a base panel, many places may be using it.
//		setStyleAttribute("margin", "2px, 2px, 2px, 10px");
		
		TableLayout tl = new TableLayout();
		tl.setWidth("95%");
		tl.setColumns(3);
		tl.setCellPadding(2);
		tl.setCellSpacing(2);
		if(mode == RESTORE_ALT_VM_MODE){
			tl.setWidth("100%");
			tl.setCellSpacing(0);
			tl.setCellPadding(0);
		}
		setLayout(tl);
		
		TableData td = new TableData();
		td.setWidth("100%");
		
		pathTF.setWidth("100%");
		pathTF.ensureDebugId(pathID);
		if(mode == DIAGNOSTIC_MODE)
			pathTF.setEmptyText(UIContext.Constants.collectDiagDataEnterANetworkPath());

		if (!isAllowBlank()) {
			pathTF.setAllowBlank(false);
		}
		if(getPathFieldlength() > 0) {
			pathTF.setWidth(getPathFieldlength());
			td.setWidth("50%");
		}
		
		if (tooltipmode == TOOLTIP_RESTORE_MODE)
			Utils.addToolTip(pathTF, UIContext.Constants
					.restoreBackupLocationEditBoxTooltip());
		else if (tooltipmode == TOOLTIP_RESTORE_ALT_MODE) {
			Utils.addToolTip(pathTF, UIContext.Constants
					.restoreAltLocationEditBoxTooltip());
		}else if(tooltipmode == TOOLTIP_SELFUPDATE_MODE)
		{
			//Utils.addToolTip(pathTF, "Enter full path to the location where you want to download patches.");
			//pathTF.setToolTip("Select the download location for patches.");
		}else if(tooltipmode == TOOLTIP_ARCHIVE_MODE)
		{
			Utils.addToolTip(pathTF, UIContext.Constants.EnterFullPathForArchiveLocationTooltip());
			//pathTF.setToolTip("Select the download location for patches.");
		}
		else if(tooltipmode == TOOLTIP_ARCHIVE_SOURCE_MODE)
		{
			Utils.addToolTip(pathTF, UIContext.Constants.EnterFullPathForArchiveSourceTooltip());
			//pathTF.setToolTip("Select the download location for patches.");
		}
		else if(tooltipmode == TOOLTIP_ARCHIVE_DESTINATION_MODE)
		{
			Utils.addToolTip(pathTF, UIContext.Constants.EnterFullPathForArchiveDestinationTooltip());
		}
		else if(tooltipmode == TOOLTIP_ARCHIVE_CLOUD_CERTIFICATE_MODE)
		{
			Utils.addToolTip(pathTF, UIContext.Constants.EnterFullPathForCloudCertificateTooltip());
			//pathTF.setToolTip("Select the download location for patches.");
		} else if(tooltipmode == TOOLTIP_COPY_MODE){
			Utils.addToolTip(pathTF, UIContext.Constants.EnterFullPathForCopy());
		} else if (tooltipmode == TOOLTIP_RESTORE_ALT_VM_MODE){
			Utils.addToolTip(pathTF, UIContext.Constants.vmFileRestoreDestinationEditBoxTooltip());
		} else if(tooltipmode == TOOLTIP_DIAGNOSTIC_MODE)
		Utils.addToolTip(pathTF, UIContext.Constants.collectDiagDataSelDestEditBoxTooltipForEdge());
		else{
			if(this.isForEdge){
				Utils.addToolTip(pathTF, UIContext.Constants.destinationSelDestEditBoxTooltipForEdge());
			}else
			Utils.addToolTip(pathTF, UIContext.Constants
					.destinationSelDestEditBoxTooltip());
		}
		add(pathTF, td);
		pathTF.addListener(Events.OnBlur, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				hiddenField.fireEvent(Events.OnBlur);
			}
			
		});
		pathTF.addListener(Events.KeyUp, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				setValidateButtonUsablity();
				setBrowseButtonUsability();
			}
			
		});
		
		pathTF.addListener(Events.KeyUp, new DefaultListener(listener));
		
		//Change mapped network path when needed
		final DefaultListener networkListener = new DefaultListener(new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				changeMappedNetworkPathWhenNeeded();
			}
			
		});
		
		final DefaultListener guidListener = new DefaultListener(new Listener<FieldEvent> (){

			@Override
			public void handleEvent(FieldEvent be) {
				final String tempPath = pathTF.getValue();
				if((tempPath!=null)&&(tempPath.startsWith("\\\\?\\"))){
					commonService.getMntPathFromVolumeGUID( tempPath , new BaseAsyncCallback<String>() {
						@Override
						public void onFailure(Throwable caught) {
							
						}

						@Override
						public void onSuccess(String result) {
							if(result != null && !result.equalsIgnoreCase(tempPath)) {
								pathTF.setValue(result);
							}
						}
					});
				}

			}
		});
		pathTF.addListener(Events.KeyUp, networkListener);

		pathTF.addListener(Events.Change, networkListener);
		
		pathTF.addListener(Events.KeyUp, guidListener);

		pathTF.addListener(Events.Change, guidListener);
		
		pathTF.addKeyListener(new KeyListener() {

			@Override
			public void componentKeyPress(ComponentEvent event) {
				if(event.getKeyCode() == KeyCodes.KEY_ENTER) {
					// if hit enter key, it should have the same behavior as browse button.
					if (browseButton.isEnabled())
						browseDestination();
				}
			}
		});
		
		Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {

	        private String previousValue1 = pathTF.getValue() == null ? "" : pathTF.getValue();

	        @Override
	        public boolean execute() {
	            final String newValue1 = pathTF.getValue();
	            if (newValue1 != null && !"".equals(newValue1) && !previousValue1.equals(newValue1)) {
	                try {
	                    valueChanged();
	                } finally {
	                	previousValue1 = newValue1;
	                }
	            }
	            
	            return true;
	        }

	        private void valueChanged() {
	        	boolean isEnable = Utils.isValidRemotePath( pathTF.getValue() );
	    	
	    		if (isForEdge && (mode != ARCHIVE_MODE)) {
	    			
	    			if(mode != DIAGNOSTIC_MODE)
	    			{
	    				validateButton.setEnabled( isEnable );
	    				validateButton.setVisible(isEnable);
	    				browseButton.setVisible( isEnable );
	    			}
	    			browseButton.setEnabled( isEnable );
	    		}
	        }

	    }, 100);
		
		if (changeListener != null)
		{
			pathTF.addListener(Events.Change, changeListener);
		}
		
		for(Listener<BaseEvent> l:changeListenerList){
			pathTF.addListener(Events.Change, l);
		}
		
		if(validateButton == null)
			validateButton = new Button();
		validateButton.ensureDebugId(validateID);
		if (tooltipmode == TOOLTIP_RESTORE_ALT_MODE) {
			Utils.addToolTip(validateButton, UIContext.Constants
					.restoreAltLocValidateButtonTooltip());
		}else {

			Utils.addToolTip(validateButton, UIContext.Constants
					.destinationSelDestValidateButtonTooltip());
		}
		validateButton.setIcon(AbstractImagePrototype.create(ICONBUNDLE.rightarrow()));
		validateButton.setEnabled(false);
		validateButton.setWidth(25);
		if(mode == ARCHIVE_MODE || mode == RESTORE_ALT_VM_MODE || mode == DIAGNOSTIC_MODE)
		{
			validateButton.setVisible(false);
		}
		
		validateButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce)
			{
				if (isForEdge)
				{
					doValidation();
				}
				else // D2D
				{
					commonService.getDestDriveTypeForModeType(thisPanel.pathTF
							.getValue(),mode ,new BaseAsyncCallback<Long>() {
							@Override
							public void onFailure(Throwable caught) {
								super.onFailure(caught);
							}

					    	@Override
							public void onSuccess(Long result) {
					    		if(result == REMOTE_DRIVE)
					    		{
					    			doValidation();
					    		}
							}
				    	}
			      	);
				}
			}
		});
		td = new TableData();
		add(validateButton, td);
		
		if(mode == SELFUPDATE_MODE)
		{
			validateButton.setEnabled(false);
		}
		
		if(browseButton == null)
			browseButton = new Button();
		browseButton.ensureDebugId(browseID);
		browseButton.setText(UIContext.Constants.restoreBrowse());
		if (tooltipmode == TOOLTIP_RESTORE_ALT_MODE) {
			Utils.addToolTip(browseButton, UIContext.Constants
					.restoreAltLocBrowseButtonTooltip());
		}else if(tooltipmode == TOOLTIP_SELFUPDATE_MODE)
		{
			//Utils.addToolTip(browseButton, "Browse for a Download location");
		}else if(tooltipmode == TOOLTIP_ARCHIVE_MODE)
		{
			Utils.addToolTip(browseButton, UIContext.Constants.BrowseArchiveTooltip());
		}else if(tooltipmode == TOOLTIP_ARCHIVE_SOURCE_MODE)
		{
			Utils.addToolTip(browseButton, UIContext.Constants.BrowseArchiveSourceTooltip());
		}else if(tooltipmode == TOOLTIP_ARCHIVE_DESTINATION_MODE)
		{
			Utils.addToolTip(browseButton, UIContext.Constants.BrowseArchiveSourceTooltip());
		}else if(tooltipmode == TOOLTIP_ARCHIVE_CLOUD_CERTIFICATE_MODE)
		{
			Utils.addToolTip(browseButton, UIContext.Constants.SelectCloudCertificateTooltip());
		}else if(tooltipmode == TOOLTIP_COPY_MODE){
			Utils.addToolTip(browseButton, UIContext.Constants.BrowseCopyDestinationTooltip());
		}else if (tooltipmode == TOOLTIP_RESTORE_ALT_VM_MODE){
			Utils.addToolTip(browseButton, UIContext.Constants.vmFileRestoreBrowseButtonTooltip());
		}else if (tooltipmode == TOOLTIP_DIAGNOSTIC_MODE){
			Utils.addToolTip(browseButton, UIContext.Constants.collectDiagDatadestinationSelDestBrowseButtonTooltip());
		}else{
			Utils.addToolTip(browseButton, UIContext.Constants
					.destinationSelDestBrowseButtonTooltip());
		}
		browseButton.setMinWidth(MIN_WIDTH);
		browseButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				browseDestination();
			}			
		});
		
		td = new TableData();
		if(getPathFieldlength() > 0) {
			td.setWidth("50%");
			td.setHorizontalAlign(HorizontalAlignment.LEFT);
		}
		if(mode == RESTORE_ALT_VM_MODE){
			browseButton.setStyleAttribute("padding-left", "4px");
		}
		if(mode == DIAGNOSTIC_MODE){
			browseButton.setHeight("28px");
			//browseButton.setStyleAttribute("padding-left", "4px");
		}
		add(browseButton, td);
		
		if (this.isForEdge)
		{
			if (mode == ARCHIVE_MODE)
			{
				browseButton.setVisible( false );
				browseButton.setEnabled( false );
			}
			else
			{
				setValidateButtonUsablity();
				setBrowseButtonUsability();
			}
		}
	}
	
	boolean validate()
	{
		return this.ArchivePoliciesWindowobj.validatingSource();
		
	}
	
	private void updateUserPasswordFromCache(){
			//fix issue 19969587 if credentials of destination exists in cache, user needn't to input credentials again
			String destination=thisPanel.getDestination();
			String[] info= Utils.getConnectionInfo(destination);
			
			if(info!=null){
//				domain=info[0];
				thisPanel.setUsername(info[1]);
			    thisPanel.setPassword(info[2]);
			}
	}
	private void browseDestination() {
		if(mode == DIAGNOSTIC_MODE)
		{
			if(thisPanel.getUsername()==null || thisPanel.getPassword()==null){
				showUsernamePasswordDialog(true);
				return;
			}
		}
		if (pathTF.getValue() != null
				&& pathTF.getValue().trim().length() > 0) {
			
			updateUserPasswordFromCache();
			// June sprint
			if (mode == BACKUP_MODE || mode == RESTORE_ALT_MODE 
					|| mode == ARCHIVE_MODE || mode == ARCHIVE_RESTORE_MODE || mode == ARCHIVE_DEST_MODE || mode == DIAGNOSTIC_MODE) {
				enableValidateAndBrowseBtn(false);
				
		        //File copy is not supporting the network drive as source policy so restricting the network drive before its calls service validation
           /*     if(mode == ARCHIVE_MODE && !validate() )
                {   
                	enableValidateAndBrowseBtn(true);                	
    				
                }
                else{*/         	
            
				commonService.validateDest(thisPanel.getDestination(), "", 
						thisPanel.getUsername(), thisPanel.getPassword(), mode,
						new BaseAsyncCallback<Long>()
				{
					@Override
					public void onFailure(Throwable caught) {
						enableValidateAndBrowseBtn(true);
						
						// if invalid share folder path error code, no need to check other return value
						if(caught instanceof BusinessLogicException
								&& INVALID_SHARE_FOLDER_PATH.equals(((BusinessLogicException)caught).getErrorCode())){
							super.onFailure(caught);
							return;
						}
						
						// June sprint
						if((mode == BACKUP_MODE && !processFailure(caught, VALIDATE_BACKUP_DEST_Fail))
								||
								(mode == DIAGNOSTIC_MODE && !processFailure(caught, VALIDATE_DIAGNOSTIC_DEST_Fail))
								||
							(mode == RESTORE_ALT_MODE && !processFailure(caught, Restore_ERR_ValidateAltLocFailed))
								||
								(mode == ARCHIVE_MODE && !processFailure(caught, VALIDATE_BACKUP_DEST_Fail) || (mode == ARCHIVE_RESTORE_MODE && !processFailure(caught, VALIDATE_BACKUP_DEST_Fail))
								|| (mode == ARCHIVE_DEST_MODE && !processFailure(caught, VALIDATE_ARCHIVE_DEST_Fail))))
							super.onFailure(caught);
					}
					@Override
					public void onSuccess(Long result) 
					{
						enableValidateAndBrowseBtn(true);
								if (mode == ARCHIVE_MODE) {
									if (validate()) {
										showBrowseDialog();
									}

								} else {

									showBrowseDialog();
								}
						
					}
				});
		
		}
			else if (mode == RESTORE_MODE || mode == MOUNT_VOLUME_MODEL)
			{
				enableValidateAndBrowseBtn(false);
				
				commonService.validateSource(thisPanel.getDestination(), "", 
						thisPanel.getUsername(), thisPanel.getPassword(),mode, isNeedCreateFolder,
						new BaseAsyncCallback<Long>() {

							@Override
							public void onFailure(Throwable caught) {
								enableValidateAndBrowseBtn(true);
								if((mode == RESTORE_MODE && !processFailure(caught, VALIDATE_SOURCE_FAIL))||
								   (mode == MOUNT_VOLUME_MODEL && !processFailure(caught, MOUNT_VOLUME_VALIDATE_SOURCE_FAIL)))
									super.onFailure(caught);
							}

							@Override
							public void onSuccess(Long result) {
								enableValidateAndBrowseBtn(true);
								showBrowseDialog();
							}
						});
			}
			else if (mode == COPY_MODE)
			{
				enableValidateAndBrowseBtn(false);
				
				commonService.validateCopyDest(thisPanel.getDestination(), "", 
						thisPanel.getUsername(), thisPanel.getPassword(),
						new BaseAsyncCallback<Long>() {

							@Override
							public void onFailure(Throwable caught) {
								enableValidateAndBrowseBtn(true);
								if(!processFailure(caught, VALIDATE_COPY_DEST_FAIL))
									super.onFailure(caught);
							}

							@Override
							public void onSuccess(Long result) {
								enableValidateAndBrowseBtn(true);
								showBrowseDialog();
							}
						});
			}
			else if(mode == ARCHIVE_CLOUD_CERTIFICATE_MODE)
			{
				showBrowseDialog();
			}
			else if(mode == SELFUPDATE_MODE)
			{
				if (pathTF.getValue() != null
						&& pathTF.getValue().trim().length() > 0)
				{						
					showBrowseDialog();
				}
			}
		}
		else
		{
			//There's nothing typed into destination
			showBrowseDialog();
		}
	}
	
	private void enableValidateAndBrowseBtn(boolean enable) {
		if(enable) {
			validateButton.enable();
			browseButton.enable();
		} else {
			validateButton.disable();
			browseButton.disable();
		}
	}
	
	private boolean processFailure(Throwable caught, String processCode) {
		if(caught instanceof BusinessLogicException
				&& processCode.equals(((BusinessLogicException)caught).getErrorCode())){
				final Throwable orginialExc = caught;
			    commonService.getDestDriveType(thisPanel.getDestination(), new BaseAsyncCallback<Long>()
				   {
						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(orginialExc);
						}
				    	@Override
						public void onSuccess(Long result) {
				    		if(result == REMOTE_DRIVE)
				    			showUsernamePasswordDialog(true);
				    		else
				    		{	
				    				showBrowseDialog_(true);
//				    			else
//				    				super.onFailure(orginialExc);
				    		}
						}
			    	}
		      	);
			    return true;
			}
		return false;
	}
	
	public void setChangeListener(Listener<BaseEvent> changeListener )
	{
		this.changeListener = changeListener;
	}

	public void addChangeListener(Listener<BaseEvent> changeListener )
	{
		this.changeListenerList.add(changeListener);
	}
	
	///////////////  Validating Functions ///////////////////
	
	public void setDestination(String dest)
	{
		oldBackupDest = dest;
		pathTF.setValue(dest);
		setValidateButtonUsablity();
		setBrowseButtonUsability();
	}

	public String getDestination()
	{
		return Utils.getNormalizedPath(pathTF.getValue());
	}
	public TextField<String> getDestinationTextField(){
		return pathTF;
	}
	
	public Button getDestinationBrowseButton()
	{
		return browseButton; 
	}
	
	public void markInvalid(String msg){
		pathTF.markInvalid(msg);
	}
	
	public void clearInvalid(){
		pathTF.clearInvalid();
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
		fillDomain();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	private void doValidation()
	{
		/* liuyu07 2011-5-10 fix Issue: 20066876 
		 * We should not have another pop-up to ask user to disconnect from old location. 
		 * User is changing the location and it is expected that we will disconnect from old location.
		 * We don�t have to ask user by showing another pop-up.*/
		showUsernamePasswordDialog(false);
//		if(username != null && username.length() > 0 
//			&& oldBackupDest != null && oldBackupDest.length() > 0) 
//		{
//			disconnectAndReconnect();
//		}
//		else
//		{
//			showUsernamePasswordDialog(false);
//		}
	}
	
	public void showUsernamePasswordDialog(final boolean showBrowseOnSuccess)
	{
		final String path = pathTF.getValue();		
		String user = ""; 
		
		updateUserPasswordFromCache();
		// liuwe05 2010-12-31 fix Issue: 19941215    Title: WRONG CAT STAT 4 SOME SESSION
		// the previous user name & password should not be filled here, especially after it failed for AFCheckFolderAccess
		// for this issue, a wrong user name may pass the following validation per Lianguo's research.
		if (thisPanel.getUsername() != null)
		{
			user = thisPanel.getUsername();
		}
		String pwd = ""; 
		if (thisPanel.getPassword() != null)
		{
			pwd = thisPanel.getPassword();
		}
		
		final UserPasswordWindow dlg = new UserPasswordWindow(path, user, pwd);

		dlg.setMode(getMode());
		dlg.setModal(true);
		
		dlg.addWindowListener(new WindowListener()
		{				
			public void windowHide(WindowEvent we) {
				//TODO: Only do this on ok
				if (dlg.getCancelled() == false)
				{
					/*//liuyu07 2011-5-10 fix Issue: 20066876,disconnect from old location and not have another pop-up to ask user
					if(username != null && username.length() > 0 
					&& oldBackupDest != null && oldBackupDest.length() > 0) 
					{
						commonService.disconnectRemotePath(oldBackupDest, "", username, password, true,new BaseAsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								fireEvent(onDisconnectionEvent);
							}
							@Override
							public void onSuccess(Void result) {
								fireEvent(onDisconnectionEvent);
							}
						});
					}*/
					
					thisPanel.username = dlg.getUsername();
					thisPanel.password = dlg.getPassword();
					fillDomain();
					
					//try to do something using the new credential and remote path.
					pathTF.fireEvent(Events.Change);
					hiddenField.fireEvent(Events.OnBlur);
					oldBackupDest = pathTF.getValue();
					
					if (showBrowseOnSuccess)
					{
						showBrowseDialog();
					}
					
					setValidated();
				}
			}
		});
		dlg.show();
		dlg.setZIndex(5000);
	}
	
	private void disconnectAndReconnect() {
		commonService.checkRemotePathAccess(oldBackupDest, "", username, password, 
				new BaseAsyncCallback<Boolean>() {
			@Override
			public void onSuccess(Boolean result) {
				if(result)
					showConnectEstablishedDlg();
				else
					showUsernamePasswordDialog(false);
			}

			private void showConnectEstablishedDlg() {
				MessageBox messageBox = new MessageBox();
				messageBox.setTitleHtml(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D));
				messageBox.setMessage(UIContext.Messages.destinationRemoteConnectExist(oldBackupDest));
				messageBox.setIcon(MessageBox.INFO);
				messageBox.setModal(true);
				messageBox.setButtons(Dialog.OKCANCEL);
				Utils.setMessageBoxDebugId(messageBox);
				messageBox.addCallback(new Listener<MessageBoxEvent>(){
					@Override
					public void handleEvent(MessageBoxEvent be) {
						if(be.getButtonClicked().getItemId().equals(Dialog.OK)) {
							commonService.disconnectRemotePath(oldBackupDest, "", username, password, true,
									new BaseAsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {
									fireEvent(onDisconnectionEvent);
									showUsernamePasswordDialog(false);
								}
								@Override
								public void onSuccess(Void result) {
									fireEvent(onDisconnectionEvent);
									showUsernamePasswordDialog(false);
								}
							});
						}
						else{
										String dest = pathTF
												.getValue() == null ? ""
												: pathTF
														.getValue()
														.toLowerCase();
							String oldDest = oldBackupDest == null ? "" : oldBackupDest.toLowerCase();
							if(!oldDest.endsWith(dest) && !dest.endsWith(oldDest)) {
											pathTF
													.setValue(oldBackupDest);
											pathTF
													.fireEvent(Events.Change);
							}
						}
					}
				});
				messageBox.show();
			}
		});
	}
	
	public void showBrowseDialog_(boolean defaultVolume) {
		String title = UIContext.Constants.restoreBackupLocation();
		if (mode == PathSelectionPanel.COPY_MODE) {
			title = UIContext.Constants.recoveryPointsCopyBrowseWinTitle();
		} else if (mode == PathSelectionPanel.RESTORE_ALT_MODE) {
			title = UIContext.Constants.restoreAltLocBrowseWinTitle();
		} 
		else if(mode == PathSelectionPanel.SELFUPDATE_MODE)
		{
			//title = "Select updates download location";
		}else if (mode == PathSelectionPanel.ARCHIVE_MODE) {
			title = UIContext.Constants.selectArchiveLocation();
		}
		else if (mode == PathSelectionPanel.ARCHIVE_CLOUD_CERTIFICATE_MODE) {
			title = UIContext.Constants.selectCloudCertificatePath();
		}
		else if (mode == PathSelectionPanel.ARCHIVE_RESTORE_MODE) {
			title = UIContext.Constants.selectArchiveDestinationPath();
		}
		else if (mode == PathSelectionPanel.DIAGNOSTIC_MODE) {
			title = UIContext.Constants.selectDiagDestinationPathTitle();
		}
		
		boolean bShowfiles = false;
		if(mode == ARCHIVE_CLOUD_CERTIFICATE_MODE)
			bShowfiles = true;			
		
		boolean bFilterVolumes = false;
		if(mode == PathSelectionPanel.ARCHIVE_MODE)
			bFilterVolumes = true;
		
		VirtualCenterModel vcModel = null;
		VMItemModel vmModel = null;
		if(mode == PathSelectionPanel.RESTORE_ALT_VM_MODE){
			vcModel = optionPanel.getVCModelFromNewDest();
			vmModel = optionPanel.getVMItemModel();
		}
		//Add param RefsDedupVolumesList to save refs and ntfs dedup volume, added by wanqi06
		final BrowseWindow browseDlg;
		if(mode == PathSelectionPanel.DIAGNOSTIC_MODE)
		{
			browseDlg = new BrowseWindow(false, title);
		}
		else
		{
			browseDlg = new BrowseWindow(bShowfiles, title,bFilterVolumes,FATVolumesList,RefsDedupVolumesList,selectedBackupVolumes,mode == PathSelectionPanel.RESTORE_ALT_VM_MODE?true:false,vcModel,vmModel);
		}
		browseDlg.setDebugID("FADD20FE-AABB-4209-A600-3D823BEBD27D", "62BE7BDE-0136-47e8-9A9E-9AC0486C0981");
		browseDlg.setResizable(false);
		browseDlg.setMode(getMode());
		browseDlg.setModal(true);
		browseDlg.setUser(thisPanel.username );
		browseDlg.setPassword(thisPanel.password);
		if(isPathSelectionArchiveMode())
		{
			browseDlg.setPathSelectionMode(true);
		}		
		if(defaultVolume)
			browseDlg.setInputFolder("");
		else {
			String inputFolder = thisPanel.pathTF.getValue();
			if(inputFolder!=null) {
				inputFolder = inputFolder.trim();
			}
			browseDlg.setInputFolder(inputFolder);
		}		
		browseDlg.addWindowListener(new WindowListener() {
			public void windowHide(WindowEvent we) {
				if (browseDlg.getLastClicked() == Dialog.CANCEL) {
					fetchUserAndPassword();
				} else {
					String newDest = browseDlg.getDestination() == null ? "" : browseDlg.getDestination();
					String oldDest = thisPanel.pathTF.getValue() == null ? ""
							: thisPanel.pathTF.getValue();
					thisPanel.pathTF.setValue(newDest);
					fetchUserAndPassword();

					if(!newDest.equalsIgnoreCase(oldDest)){
						pathTF.fireEvent(Events.Change);
						thisPanel.setValidated();
					}
					pathTF.fireEvent(Events.KeyUp);
					hiddenField.fireEvent(Events.OnBlur);
				}
			}

			private void fetchUserAndPassword() {
				thisPanel.username = browseDlg.getUser();
				thisPanel.password = browseDlg.getPassword();
				fillDomain();
			}
		});
		
		if(networkPathLoaded) {
			browseDlg.addNetworkDrive(networkDriveList);
			needToShowBrowserDialog = null;
		}
		else
			needToShowBrowserDialog  = browseDlg;
		browseDlg.show();
		browseDlg.setZIndex(5000);	
	}
	public void showBrowseDialog() {
		showBrowseDialog_(false);
	}
	
	public void setPathFieldLength(int length) {
		pathFieldLength = length;
	}
	
	public int getPathFieldlength() {
		return pathFieldLength;
	}

	private void setValidateButtonUsablity()
	{
		boolean isEnable = Utils.isValidRemotePath( pathTF.getValue() );
		if(this.mode != DIAGNOSTIC_MODE)
			validateButton.setEnabled( isEnable );
	
		if (this.isForEdge && (this.mode != ARCHIVE_MODE && this.mode != DIAGNOSTIC_MODE))
			validateButton.setVisible(isEnable);
		
		//validateButton.setEnabled(!isLocalPath());
	}
	
	private void setBrowseButtonUsability()
	{
		if (this.isForEdge && (this.mode != ARCHIVE_MODE))
		{
			boolean isEnable = Utils.isValidRemotePath( pathTF.getValue() );
			browseButton.setEnabled( isEnable );
			if(mode != DIAGNOSTIC_MODE)
				browseButton.setVisible( isEnable );
		}
	}

	public static boolean isLocalPath(String path) {
		boolean localPath = false;
		if(path != null && path.length() > 1 && path.charAt(1) == ':') {
//			if(path.length()>=3 && path.charAt(2) != '\\')
//				return false;
			if(localDriveLetter.toLowerCase().indexOf(path.substring(0, 1).toLowerCase()) >= 0)
				localPath = true;
		}
		return localPath;
	}
	
	public static boolean isLocalPathValid(String path){
		if(path == null)
			return true;
		path=path.trim();
		if(path.length() >= 3 && path.charAt(1) == ':' && path.charAt(2) != '\\')
			return false;

		return true;
	}
	
	public boolean isLocalPath() {
		String path = pathTF.getValue();
		return isLocalPath(path);
	}

	public void setAllowBlank(boolean isAllowBlank) {
		this.isAllowBlank = isAllowBlank;
	}

	public boolean isAllowBlank() {
		return isAllowBlank;
	}

	public Button getValidateButton() {
		return validateButton;
	}
	
	private void changeMappedNetworkPathWhenNeeded() {
		final String path = pathTF.getValue();
		if(networkPathLoaded && path != null && path.length() > 1 && path.charAt(1) == ':' && letter2NetworkPathMap.keySet().contains(path.substring(0, 1).toLowerCase()))
			pathTF.setValue(letter2NetworkPathMap.get(path.substring(0, 1).toLowerCase()) + path.substring(2));
	}

	private class DefaultListener implements Listener<FieldEvent>{

		private Listener<FieldEvent> custListener;
		DefaultListener(Listener<FieldEvent> custListener){
			this.custListener = custListener;
		}
		
		@Override
		public void handleEvent(FieldEvent be) {
			if(custListener != null){
				custListener.handleEvent(be);
			}
//			if(mode != SELFUPDATE_MODE)
//			{
//				//validateButton.setEnabled(Utils.isValidRemotePath(pathTF.getValue()));
//				setValidateButtonUsablity();
//				setBrowseButtonUsability();
//			}
		}
		
	}
	//Save refs and ntfs dedup volumes, added by wanqi06
	public void setVolumesList2Filter(List<FileModel> in_FATVolumes, List<FileModel> in_RefsDudupVolumes)
	{
		this.FATVolumesList = in_FATVolumes;
		this.RefsDedupVolumesList = in_RefsDudupVolumes;
	}
	
	public void setSelectedVolumesList2Filter(BackupVolumeModel backupVolumes)
	{
		this.selectedBackupVolumes = backupVolumes;
	}

	
	public boolean isEmptyOrNull(String target){		
		if (target == null || target.equals("") || target.trim().equals(""))
			return true;
		return false;
	}
	public void setValidated()
	{
		this.validatedPath = this.getDestination();
		this.validatedUsername = this.getUsername();
		this.validatedPassword = this.getPassword();
		if(RestoreContext.getBackupModel() != null){
			RestoreContext.getBackupModel().setDestUserName(validatedUsername);
			RestoreContext.getBackupModel().setDestPassword(validatedPassword);
		}
	}
	
	public boolean needValidate()
	{
		if(!validatedPath.equalsIgnoreCase(getDestination()))
				if(validatedPath.startsWith("\\\\") && getDestination().startsWith("\\\\")){
					if(!(validatedPath+"\\").split("\\\\")[2].equalsIgnoreCase((getDestination()+"\\").split("\\\\")[2]))
						return true;
				}
		
		if(Util.isEmptyString( this.getUsername())||
				Util.isEmptyString( this.getPassword())){
			return true;
		}
		if (!this.validatedUsername.equals( this.getUsername() ) ||
			!this.validatedPassword.equals( this.getPassword() ))
			return true;
		
		return false;
	}
	
	public void cacheInfo() {
		Utils.cacheConnectionInfo(getDestination(), getUsername(), getPassword());
	}
	
	private void fillDomain() {
		if(username != null) {
			int index = username.indexOf("\\");
			if(index > 0) {
				domain = username.substring(0, index);
				userWithoutDomain = username.substring(index + 1);
			}else {
				userWithoutDomain = username;
			}
		}
	}
	
	public String getDomain(){
		return domain;
	}
	
	public String getUserWithoutDomain(){
		return userWithoutDomain;
	}
	public void addDebugId(String textID, String validationID, String browseID){
		pathID = textID;
		validateID = validationID;
		this.browseID = browseID;
	}
	public TextField<String> getPathInputPanel() {
		return pathTF;
	}
	public TextField<String> getHiddenField() {
		return hiddenField;
	}
}