package com.ca.arcflash.ui.client.backup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ca.arcflash.ui.client.ArchiveToCloudErrors;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.HelpTopics;
import com.ca.arcflash.ui.client.common.ICloudSettings;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.ArchiveCloudDestInfoModel;
import com.ca.arcflash.ui.client.model.CloudSubVendorType;
import com.ca.arcflash.ui.client.model.CloudVendorInfoModel;
import com.ca.arcflash.ui.client.model.CloudVendorType;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class CloudConfigWindow extends Window {

	private static final String D2DFILECOPY_TESTCONNECTION = "d2dfilecopy-testconnection-";

	final LoginServiceAsync service = GWT.create(LoginService.class);	

	private ICloudSettings cloudSettings;
	
	private LayoutContainer cloudSettingsAmazon;
	private LayoutContainer cloudSettingsAzure;
	private LayoutContainer cloudSettingsEucalyptus;
	private LayoutContainer cloudSettingsFujitsu;
	
	ICloudSettings amazon;
	ICloudSettings azure;
	ICloudSettings eucalyptus;
	ICloudSettings Fujitsu;
	
	private LabelField lblCloudNotification;
	
	public HashMap<Integer,String> cloudVendoUrlCache;	
	
	
	private CloudConfigWindow thisWindow;

	public static final String ARCHIVE_MODE= "archive";

	public static final String RESTORE_MODE= "restore";


	private LabelField lblVendorType;

	private ComboBox<CloudVendorInfoModel> comboCloudVendorType;
	private ListStore<CloudVendorInfoModel> cloudVendorTypesStore = null;

	public  Button btOK;
	private Button btCancel;
	private Button btHelp;	
	private Button testConnection;
	private String hostName; 

	private final static int MAX_WIDTH = 600;
	private final static int MAX_HEIGHT = 530;

	private final static int MAX_LABEL_WIDTH = 120;
	private final static int MAX_LABEL_WIDTH_AZ = 90;
	private final static int MAX_FIELD_WIDTH = 280;
	private final static int MAX_VALUE_LENGTH = 256;
	private final static int LABEL_WIDTH = 110;
	private static FieldSet connectionFieldSet;
	
	private ArchiveCloudDestInfoModel archiveCloudConfigModel;
	
	private String launchMode;

	private boolean bCancelled = false;

	private final boolean isForEdge;

	public CloudConfigWindow(String mode,boolean isForEdge)
	{
		thisWindow = this;
		cloudVendoUrlCache = new HashMap<Integer,String>();
		
		
		this.launchMode = mode;
		this.isForEdge = isForEdge;
		
		thisWindow.setScrollMode(Scroll.AUTO);
		thisWindow.setResizable(false);
		thisWindow.setHeadingHtml(UIContext.Constants.ArchiveCloudConfigTitle());
		thisWindow.setWidth(MAX_WIDTH);
		thisWindow.setHeight(MAX_HEIGHT);
		thisWindow.setBodyStyle("background-color: white; padding: 6px;");
		
		service.GetHostName(new AsyncCallback<String>() {

			@Override
			public void onSuccess(String result) {
				if(result!=null)
					hostName = result.toLowerCase();
			}

			@Override
			public void onFailure(Throwable caught) {
				hostName = "";
			}
		});
		
		if(mode == ARCHIVE_MODE)
		lblCloudNotification = new LabelField(applyStyleToMessage(UIContext.Constants.cloudFileCopySlowNotification()));
		else
		lblCloudNotification = new LabelField(applyStyleToMessage(UIContext.Constants.cloudFileCopyRestoreSlowNotification()));	
		
		lblCloudNotification.setStyleAttribute("margin-left", "10px");
		lblCloudNotification.setStyleAttribute("margin-bottom", "10px");
		
		thisWindow.add(lblCloudNotification);
		
		
		connectionFieldSet = new FieldSet();   
		connectionFieldSet.ensureDebugId("E2191940-CC35-4ffd-81B1-5E5BF67CB5D9");
//		connectionFieldSet.setHeadingHtml("");
		connectionFieldSet.setHeadingHtml(UIContext.Constants.cloudVendorType());
		connectionFieldSet.setStyleAttribute("margin", "5,5,5,5");

		cloudVendorTypesStore = new ListStore<CloudVendorInfoModel>();
		cloudVendorTypesStore.add(defineCloudVendorTypes());


		lblVendorType = new LabelField(UIContext.Constants.cloudVendorType());
		lblVendorType.setWidth(MAX_LABEL_WIDTH);

		
		comboCloudVendorType = new ComboBox<CloudVendorInfoModel>();
		comboCloudVendorType.ensureDebugId("7AEFE87B-8EE7-4a8f-85A9-888AE04FE1A8");
		comboCloudVendorType.setStore(cloudVendorTypesStore);

		comboCloudVendorType.setDisplayField("name");
		comboCloudVendorType.setWidth(280);
		comboCloudVendorType.setEditable(false);
		comboCloudVendorType.setTriggerAction(TriggerAction.ALL);
		Utils.addToolTip(comboCloudVendorType, UIContext.Constants.CloudVendorTypeToolTip());
		
		
		
		
		comboCloudVendorType.addListener(Events.SelectionChange, new Listener<SelectionChangedEvent<CloudVendorInfoModel>>() {

			@Override
			public void handleEvent(SelectionChangedEvent<CloudVendorInfoModel> be) {
				final int cloudVendorType = be.getSelection().get(0).getType();	
				final int cloudSubVendorType = be.getSelection().get(0).getSubVendorType();
				//TODO [kasra04]: set the container based on the selection i.e. hide the existing contents and show the 
				//				   content for cloud provider selected 
				hideAllContainers();
				showContainer(cloudVendorType,cloudSubVendorType);
			}

		});
		
		HorizontalPanel panel = new HorizontalPanel();
	    panel.setSpacing(10);
		
		panel.add(lblVendorType);
		panel.add(comboCloudVendorType);
		connectionFieldSet.add(panel);
		
		thisWindow.add(connectionFieldSet);
		loadCloudContainers();
		
		defineArchiveSettingsButtons();
		
		// liuwe05 2011-06-01 fix Issue: 20312591    Title: INCORRECT BEHAVIOR EXHIBITED I
		// focus on the OK button, otherwise after press ESC in Firefox/Chrome, it will pop up confirmation to close the setting window.
		// and this window will still be there after the setting window is closed
		this.setFocusWidget(btOK);
	}

	private void showContainer(int cloudVendorType,int cloudSubVendorType)
	{
		if(cloudVendorType==CloudVendorType.AmazonS3.getValue())	
		{	
			cloudSettings = amazon;
			//populateURLField(CloudVendorType.AmazonS3.getValue());
			lblVendorType.setWidth(MAX_LABEL_WIDTH);
			cloudSettingsAmazon.show();
			
			
		}	
		else if(cloudVendorType==CloudVendorType.WindowsAzure.getValue())
		{	
			if(cloudSubVendorType == CloudSubVendorType.WindowsAzure.getValue() || 
					cloudSubVendorType == CloudSubVendorType.WindowsAzureCompatible.getValue())
			{
				cloudSettings = azure;
				//populateURLField(CloudSubVendorType.WindowsAzure.getValue());
				lblVendorType.setWidth(MAX_LABEL_WIDTH_AZ);
				cloudSettingsAzure.show();
				
				
			}
			else if(cloudSubVendorType == CloudSubVendorType.WindowsFujistu.getValue())
			{
				cloudSettings = Fujitsu;
				//populateURLField(CloudSubVendorType.WindowsFujistu.getValue());
				lblVendorType.setWidth(MAX_LABEL_WIDTH_AZ);
				cloudSettingsFujitsu.show();
				
				
			}
		}	
		else if(cloudVendorType==CloudVendorType.Eucalyptus.getValue())
		{	
			cloudSettings = eucalyptus;
			//populateURLField(CloudVendorType.Eucalyptus.getValue());
			lblVendorType.setWidth(MAX_LABEL_WIDTH_AZ);
			cloudSettingsEucalyptus.show();
			
			
		}
		CloudSubVendorType subVendor = CloudSubVendorType.getCloudSubVendorById(cloudSubVendorType);
		cloudSettings.showHideFieldsForCloudSubVendor(subVendor);
		
//		if(cloudVendorType!=CloudVendorType.Eucalyptus.getValue())
//			cloudSettings.setVendorUrl(cloudVendoUrlCache.get(new Integer(cloudVendorType)));
	}
	
	private void hideAllContainers()
	{
		if(cloudSettingsAmazon != null)
		cloudSettingsAmazon.hide();
		
		if(cloudSettingsAzure != null)
		cloudSettingsAzure.hide();
		
		if(cloudSettingsEucalyptus != null)
		cloudSettingsEucalyptus.hide();
		
		if(cloudSettingsFujitsu != null)
		cloudSettingsFujitsu.hide();
	}
	
	
	
	private void loadCloudContainers()
	{
		amazon =  new AmazonS3CloudConfigContainer(launchMode,isForEdge);
		azure =  new AzureCloudConfigContainer(launchMode, isForEdge);
		eucalyptus =  new EucalyptusCloudConfigContainer(launchMode,isForEdge);
		Fujitsu = new FujitsuCloudConfigContainer(launchMode, isForEdge);		
		
		amazon.disableProxyByDefault();
		azure.disableProxyByDefault();
		eucalyptus.disableProxyByDefault();
		Fujitsu.disableProxyByDefault();
		
		cloudSettingsAmazon =amazon.getLcCloudSettingsContainer();		
		cloudSettingsAzure = azure.getLcCloudSettingsContainer();
		cloudSettingsEucalyptus = eucalyptus.getLcCloudSettingsContainer();
		cloudSettingsFujitsu = Fujitsu.getLcCloudSettingsContainer();
		
		thisWindow.add(cloudSettingsAmazon);
		thisWindow.add(cloudSettingsAzure);
		thisWindow.add(cloudSettingsEucalyptus);
		thisWindow.add(cloudSettingsFujitsu);		
		
	}
	
	
	public void loadCloudContainersWithVendorURL(HashMap<String,CloudVendorInfoModel> vendorURLSResult)
	{

		amazon.setVendorURL(vendorURLSResult);
		azure.setVendorURL(vendorURLSResult);
		eucalyptus.setVendorURL(vendorURLSResult);
		Fujitsu.setVendorURL(vendorURLSResult);
	}
	

	/*private LayoutContainer getCloudContainer(int cloudVendorType)
	{
		//TODO: [kasra04] Conditionally load the cloud container
		
		if(cloudVendorType==CloudVendorType.AmazonS3.getValue())		
			cloudSettings = new AmazonS3CloudConfigContainer(launchMode);
		if(cloudVendorType==CloudVendorType.WindowsAzure.getValue())		
			cloudSettings = new AzureCloudConfigContainer(launchMode);
		if(cloudVendorType==CloudVendorType.Eucalyptus.getValue())		
			cloudSettings = new EucalyptusCloudConfigContainer(launchMode);
		
		
		return cloudSettings.getLcCloudSettingsContainer();
	}*/


	private void defineArchiveSettingsButtons()
	{

			testConnection = new Button();
			testConnection.ensureDebugId("A465884E-7136-4ced-9B89-A83A99E67DC7");
			testConnection.setText(UIContext.Constants.cloudTestConnectionLabel());
			Utils.addToolTip(testConnection, UIContext.Constants.cloudTestConnectionToolTip());
			testConnection.addSelectionListener(new SelectionListener<ButtonEvent>(){
				@Override
				public void componentSelected(ButtonEvent ce) {

					//RenderHostName
					if(Validate(true))
					{

						String maskMsg =  UIContext.Constants.validating();					
						thisWindow.mask(maskMsg);
						AsyncCallback<Long> callback = new BaseAsyncCallback<Long>(){

							@Override
							public void onSuccess(Long result) {
								thisWindow.unmask();
								if (result==0L) {
									ArchiveCloudDestInfoModel cloudModel = GetArchiveCloudInfo();
									if(isForEdge)
									{
										cloudModel.setcloudBucketName(D2DFILECOPY_TESTCONNECTION+hostName+"-"+cloudModel.getcloudBucketName());
									}
									thisWindow.mask(UIContext.Constants.cloudTestConnectionMessage());
									service.testConnectionToCloud(cloudModel,new AsyncCallback<Long>() {

						@Override
						public void onFailure(Throwable caught) {
							thisWindow.unmask();

							MessageBox msgBox = new MessageBox();
							msgBox.setIcon(MessageBox.ERROR);
							msgBox.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
							msgBox.setModal(true);
							if(caught instanceof BusinessLogicException){
								msgBox.setMessage(((BusinessLogicException)caught).getDisplayMessage());
							}else{
								msgBox.setMessage(ArchiveToCloudErrors.getMessage(caught.getMessage()));
							}
							Utils.setMessageBoxDebugId(msgBox);
							msgBox.show();
						}

						@Override
						public void onSuccess(Long result) {
							thisWindow.unmask();
							MessageBox msgBox = new MessageBox();						
							msgBox.setModal(true);

							if(result == 0L)
							{
								msgBox.setIcon(MessageBox.INFO);
								msgBox.setTitleHtml(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D));
								msgBox.setMessage(UIContext.Constants.ArchiveCloudConnectionAvailableMessage());
							}
							else
							{	
								msgBox.setIcon(MessageBox.ERROR);
								msgBox.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
								//							msgBox.setMessage(result+"\n"+UIContext.Constants.ArchiveCloudConnectionNotAvailableMessage());
								msgBox.setMessage(ArchiveToCloudErrors.getMessage("Error_"+result));

							}
							Utils.setMessageBoxDebugId(msgBox);
							msgBox.show();
						}
								});							
							}
						}
						
						@Override
						public void onFailure(Throwable caught) {
							thisWindow.unmask();
							super.onFailure(caught);
							
						}
						
					};
					validateForEdge(callback);					
					
					
					
				}
				else
					thisWindow.unmask();
			}
		});	

			this.addButton(testConnection);
		
		btOK = new Button()
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
		btOK.ensureDebugId("30EA9D05-4AE3-48c7-8E35-DAE29BCEE372");
		btOK.setText(UIContext.Constants.ok());
		btOK.setMinWidth(UIContext.MIN_WIDTH);		
		btOK.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				if(Validate(true))
				{
					String maskMsg =  UIContext.Constants.validating();					
					thisWindow.mask(maskMsg);
					AsyncCallback<Long> callback = new BaseAsyncCallback<Long>(){
						
						@Override
						public void onSuccess(Long result) {
							thisWindow.unmask();
							if (result==0L) {
//								save();
//								thisWindow.hide(btOK);
//								return;
								doSave();
							}
						}
						
						@Override
						public void onFailure(Throwable caught) {
							thisWindow.unmask();
							super.onFailure(caught);
							
						}
						
					};
					validateForEdge(callback);
				}
				return;
			}
		});		
		this.addButton(btOK);


		btCancel = new Button()
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
		btCancel.ensureDebugId("92C6915A-04FA-41c8-8F36-8A2D861BE54F");
		btCancel.setText(UIContext.Constants.cancel());
		btCancel.setMinWidth(UIContext.MIN_WIDTH);
		btCancel.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				bCancelled = true;
				thisWindow.hide();

			}});		
		this.addButton(btCancel);	

		btHelp = new Button();
		btHelp.ensureDebugId("2F105C15-F224-4cc4-A61B-1B48D30D7965");
		btHelp.setText(UIContext.Constants.help());
		btHelp.setMinWidth(UIContext.MIN_WIDTH);
		btHelp.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				if(launchMode == ARCHIVE_MODE)
					HelpTopics.showHelpURL(UIContext.externalLinks.getcloudConfigurationSettingsHelp());
				else 
					HelpTopics.showHelpURL(UIContext.externalLinks.getspecifyCloudConfigurationRestore());
			}
		});
		this.addButton(btHelp);	
	}


	public void RefreshData(ArchiveCloudDestInfoModel cloudConfigInfo)
	{
		bCancelled = false;
		archiveCloudConfigModel = cloudConfigInfo;
		populateCloudVendorType(cloudConfigInfo);
		
		cloudSettings.refreshData(cloudConfigInfo);
		
	  
	  
	   
	}

	private void populateCloudVendorType(ArchiveCloudDestInfoModel cloudConfigInfo)
	{
		
		int cloudVendorType;
		int cloudSubVendorType;
		
		if(cloudConfigInfo != null)
		{			
			comboCloudVendorType.setValue(getVendorModel(cloudConfigInfo.getcloudVendorType().intValue(),cloudConfigInfo.getCloudSubVendorType().intValue() ));
			cloudVendorType = cloudConfigInfo.getcloudVendorType().intValue();
			cloudSubVendorType = cloudConfigInfo.getCloudSubVendorType().intValue();
		}
		else
		{
			comboCloudVendorType.setValue(getVendorModel(CloudVendorType.AmazonS3.getValue(),CloudVendorType.AmazonS3.getValue()));
			cloudVendorType = CloudVendorType.AmazonS3.getValue();
			cloudSubVendorType = CloudVendorType.AmazonS3.getValue();
		}
		
		hideAllContainers();		
		showContainer(cloudVendorType,cloudSubVendorType);
		
	}


	private boolean Validate(boolean validateBucket)
	{
		return cloudSettings.validate(validateBucket);
	}
	
	private void validateForEdge(final AsyncCallback<Long> callback)
	{
		cloudSettings.validateForEdge(callback);
	}
	

	public ArchiveCloudDestInfoModel GetArchiveCloudInfo()
	{
		return cloudSettings.getArchiveCloudInfo();
	}

	public boolean getcancelled() {
		return bCancelled;
	}

	public ArchiveCloudDestInfoModel getarchiveCloudConfigModel()
	{	
		if(archiveCloudConfigModel==null)
			archiveCloudConfigModel = cloudSettings.getArchiveCloudConfigModel();
		return archiveCloudConfigModel;
	}
	
	public void doSave() {
		// should validate from cloud first, then save
		ArchiveCloudDestInfoModel cloudModel = GetArchiveCloudInfo();
		if (isForEdge) {
			cloudModel.setcloudBucketName(D2DFILECOPY_TESTCONNECTION + hostName
					+ "-" + cloudModel.getcloudBucketName());
		}
		thisWindow.mask(UIContext.Constants.cloudTestConnectionMessage());
		service.testConnectionToCloud(cloudModel, new AsyncCallback<Long>() {

			@Override
			public void onFailure(Throwable caught) {
				thisWindow.unmask();

				MessageBox msgBox = new MessageBox();
				msgBox.setIcon(MessageBox.ERROR);
				msgBox.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
				msgBox.setModal(true);
				if (caught instanceof BusinessLogicException) {
					msgBox.setMessage(((BusinessLogicException) caught).getDisplayMessage());
				} else {
					msgBox.setMessage(ArchiveToCloudErrors.getMessage(caught.getMessage()));
				}
				Utils.setMessageBoxDebugId(msgBox);
				msgBox.show();
			}

			@Override
			public void onSuccess(Long result) {
				thisWindow.unmask();
				if (result == 0L) {
					save();
					thisWindow.hide(btOK);
				} else {
					MessageBox msgBox = new MessageBox();
					msgBox.setModal(true);
					msgBox.setIcon(MessageBox.ERROR);
					msgBox.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
					msgBox.setMessage(ArchiveToCloudErrors.getMessage("Error_"+ result));
					Utils.setMessageBoxDebugId(msgBox);
					msgBox.show();
				}
			}
		});
	}

	public void save()
	{
		archiveCloudConfigModel = cloudSettings.save();
	}

	private List<CloudVendorInfoModel> defineCloudVendorTypes()
	{
		List<CloudVendorInfoModel> vendorTypesList = new ArrayList<CloudVendorInfoModel>();
		for(CloudVendorType vendorType: CloudVendorType.values())
		{
			switch(vendorType){
			case AmazonS3:
				vendorTypesList.add(getCloudVendorInfoModel(vendorType,CloudSubVendorType.AmazonS3, getSubVendorDisplayName(CloudSubVendorType.AmazonS3)));
				vendorTypesList.add(getCloudVendorInfoModel(vendorType,CloudSubVendorType.AmazonS3Compatible, getSubVendorDisplayName(CloudSubVendorType.AmazonS3Compatible)));
				break;
			case WindowsAzure:
				vendorTypesList.add(getCloudVendorInfoModel(vendorType,CloudSubVendorType.WindowsAzure, getSubVendorDisplayName(CloudSubVendorType.WindowsAzure)));
				vendorTypesList.add(getCloudVendorInfoModel(vendorType,CloudSubVendorType.WindowsAzureCompatible, getSubVendorDisplayName(CloudSubVendorType.WindowsAzureCompatible)));
				vendorTypesList.add(getCloudVendorInfoModel(vendorType,CloudSubVendorType.WindowsFujistu, getSubVendorDisplayName(CloudSubVendorType.WindowsFujistu)));
				break;
			case Eucalyptus:
				vendorTypesList.add(getCloudVendorInfoModel(vendorType,CloudSubVendorType.Eucalyptus, getSubVendorDisplayName(CloudSubVendorType.Eucalyptus)));
				break;
			default:
				break;
			}
		}
		return vendorTypesList;
	}

	private static CloudVendorInfoModel getCloudVendorInfoModel(CloudVendorType vendorType,
			CloudSubVendorType subVendorType, String displayName) {
		CloudVendorInfoModel vendorInfoModel = new CloudVendorInfoModel();
		vendorInfoModel.setType(vendorType.getValue());
		vendorInfoModel.setSubVendorType(subVendorType.getValue());
		vendorInfoModel.setName(displayName);

		return vendorInfoModel;
	}

	public static String getSubVendorDisplayName(CloudSubVendorType vendorSubType)
	{

		switch (vendorSubType) {

		case AmazonS3:
			return UIContext.Constants.AmazonS3();
		case AmazonS3Compatible: 
			return UIContext.Constants.AmazonS3Compatible();
		case WindowsAzure:
			return UIContext.Constants.WindowsAzure();
		case WindowsAzureCompatible:
			return UIContext.Constants.WindowsAzureCompatible();
		case WindowsFujistu:
			return UIContext.Constants.WindowsFujistu();
		case Eucalyptus:
			return UIContext.Constants.Eucalyptus();
		default:
			return null;
		}

	}
	
	private CloudVendorInfoModel getVendorModel(int type,int Subtype)
	{
		CloudVendorType venType = getVendorType(type);
		String displayName = "";
		if(venType.getValue()== CloudVendorType.WindowsAzure.getValue())
		{
			CloudSubVendorType subVenType =  getSubVendorType(Subtype);
			displayName = getAzureDisplayName(subVenType);
		}
		else
		{
		    displayName = getVendorDisplayName(venType);
		}
		String vendorUrl = "";
		CloudVendorInfoModel vendorInfoModel = new CloudVendorInfoModel(type,displayName,vendorUrl,Subtype);
		return vendorInfoModel;
	}
	
	
	private CloudVendorType getVendorType(int type)
	{
		if(type==CloudVendorType.AmazonS3.getValue())
			return CloudVendorType.AmazonS3;
		else if(type==CloudVendorType.WindowsAzure.getValue())
			return CloudVendorType.WindowsAzure;
		else if(type==CloudVendorType.Eucalyptus.getValue())
			return CloudVendorType.Eucalyptus;
		return null;
	}
	
	private CloudSubVendorType getSubVendorType(int type)
	{
		if(type==CloudSubVendorType.WindowsAzure.getValue())
			return CloudSubVendorType.WindowsAzure;
		else if(type==CloudSubVendorType.WindowsFujistu.getValue())
			return CloudSubVendorType.WindowsFujistu;		
		return null;
	}
	
/*	private void getCloudVendorUrl()
	{		
		thisWindow.mask("getting vendor URL information");
		service.getCloudProviderInfo(new AsyncCallback<HashMap<String,CloudVendorInfoModel>>(){

			@Override
			public void onFailure(Throwable caught) {
				thisWindow.unmask();
				
			}

			@Override
			public void onSuccess(HashMap<String,CloudVendorInfoModel> result) {				
				if(result != null && result.size() > 0)
				{
					vendorURLSResult = result;
					loadCloudContainersWithVendorURL();		
				}
				thisWindow.unmask();		
				
			}
			
		});
		thisWindow.unmask();
		
	}*/
	
/*	private void populateURLField(int type)
	{
		String url = cloudVendoUrlCache.get(new Integer(type));
		cloudSettings.setVendorUrl(url);
	}*/
	
	
	
/*	private void loadCloudVendorUrls()
	{
		for(CloudVendorType vendorType: CloudVendorType.values())
		{
			if(vendorType!=CloudVendorType.Eucalyptus)
			{
			    if(vendorType.getValue() == CloudSubVendorType.WindowsAzure.getValue())
			    {
			    	for(CloudSubVendorType subVendorType: CloudSubVendorType.values())
					{
			    		getCloudVendorUrl(vendorType.getValue(),subVendorType.getValue());
					}
			    }
			    else
			    {
			    	getCloudVendorUrl(vendorType.getValue(),vendorType.getValue());
			    }
		}
	  }
	}*/
	

	private String getVendorDisplayName( CloudVendorType type)
	{

		switch (type) {
		case AmazonS3:
			return UIContext.Constants.AmazonS3(); // Put Externalized strings here			
		
		case WindowsAzure:	
			return UIContext.Constants.WindowsAzure();

		case Eucalyptus:	
			return UIContext.Constants.Eucalyptus();

		/*case FileSystem:	
			return "File System";
	
		case I365:
			return "I365";
		
		case Invalid:
			return "Invalid";*/
			
		default:
			return null;
		}
		
	}

	
	private String getAzureDisplayName( CloudSubVendorType type)
	{

		switch (type) {	
		
		case WindowsAzure:	
			return UIContext.Constants.WindowsAzure();
			
		case WindowsFujistu:	
			return UIContext.Constants.WindowsFujistu();	
			
		default:
			return null;
		}
		
	}
	
	private String applyStyleToMessage(String msg)
	{
		String prefix = "<div style=\"font-family: Tahoma,Arial;font-size: 9pt;\">";
		String suffix = "</div>";
		String modMsg = prefix + msg + suffix; 
		return modMsg;
	}

}
