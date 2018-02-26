package com.ca.arcflash.ui.client.backup;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import com.ca.arcflash.ui.client.ArchiveToCloudErrors;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseSimpleComboBox;
import com.ca.arcflash.ui.client.common.ICloudSettings;
import com.ca.arcflash.ui.client.common.PasswordTextField;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.ArchiveCloudDestInfoModel;
import com.ca.arcflash.ui.client.model.BucketDetailsModel;
import com.ca.arcflash.ui.client.model.CloudModel;
import com.ca.arcflash.ui.client.model.CloudSubVendorType;
import com.ca.arcflash.ui.client.model.CloudVendorInfoModel;
import com.ca.arcflash.ui.client.model.CloudVendorType;
import com.ca.arcflash.webservice.data.archive.CloudProviderInfo;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.FieldSetEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.Validator;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class FujitsuCloudConfigContainer extends LayoutContainer implements ICloudSettings {
	
	private LayoutContainer lcCloudSettingsContainer;
	
	private LabelField lblVendorURL;
	private TextField<String> txtVendorURL;
	
	private LabelField lblVendorUsername;
	private TextField<String> txtVendorUsername;
	private LabelField lblVendorPassword;
	private PasswordTextField txtVendorPassword;
	private LabelField accessKeyLable = new LabelField();

	private String vendorUrl;	

	public static final String ARCHIVE_MODE= "archive";
	public static final String RESTORE_MODE= "restore";

	private LabelField lblBucketName;
	
//	private TextField<String> txtRegionName;
	private ComboBox<BucketDetailsModel> cmbBucket;
	
//	private LabelField lblRegionName;
	private LabelField lblRefresh;
	
	private ListStore<BucketDetailsModel> bucketsStore = null;
	private String hostName; 

	private Button btnAddBucket;
	private Button refreshBuckets;

	private TextField<String> txtBucketName;
	
//	private HashMap<String,String> bucketCache;
	
	private List<String> regionsList;

	//proxy settings
	private static FieldSet proxyFieldSet;
	private static FieldSet connectionFieldSet;
	private static FieldSet advSettingsFieldSet;
	
	private boolean useProxy = false;
	private boolean useProxyAuth = false;
	
	private CheckBox cbProxyRequiresAuth;
	
	private LabelField lblProxyUserName;
	private TextField<String> txtProxyUsername;
	
	private LabelField lblProxyPassword;
	private PasswordTextField txtProxyPassword;
	private LabelField proxyLble;
	
	private LabelField lblProxyServer;
	private TextField<String> txtProxyServer;
	private LabelField lblProxyPort;
	private NumberField ProxyPort;
	
	private String launchMode;
	
	private final static int MAX_LABEL_WIDTH = 90;
	private final static int MAX_FIELD_WIDTH = 280;
	private final static int MAX_VALUE_LENGTH = 256;
	private final static int BUCKET_LABEL_WIDTH = 110;
	
	private String curSelectedBucket=null;
	
	private String encodeBucketForEdge;
	
	private ArchiveCloudDestInfoModel archiveCloudConfigModel = null;
	
	final LoginServiceAsync service = GWT.create(LoginService.class);

	private final boolean isForEdge;

	public FujitsuCloudConfigContainer(String mode, boolean isForEdge)
	{	
		//initialize cache
//		bucketCache = new HashMap<String,String>();
		
		this.launchMode = mode;
		this.isForEdge = isForEdge;

		lcCloudSettingsContainer = new LayoutContainer();
		
		connectionFieldSet = new FieldSet();   
		connectionFieldSet.setHeadingHtml(UIContext.Constants.cloudConnectionHeading());
		connectionFieldSet.setStyleAttribute("margin", "5,5,5,5");
		
		TableLayout tlConnSettings = new TableLayout();
		tlConnSettings.setWidth("70%");
		tlConnSettings.setColumns(2);
		tlConnSettings.setCellPadding(0);
		tlConnSettings.setCellSpacing(10);
		connectionFieldSet.setLayout(tlConnSettings);
		
		TableData tdCloudConfigLabel = new TableData();
		//tdCloudConfigLabel.setWidth("30%");
		tdCloudConfigLabel.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		TableData tdCloudConfigField = new TableData();
		tdCloudConfigField.setWidth("15%");		
		tdCloudConfigField.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		lblVendorURL = new LabelField(UIContext.Constants.CloudVendorURL());
		lblVendorURL.setWidth(MAX_LABEL_WIDTH);
		connectionFieldSet.add(lblVendorURL,tdCloudConfigLabel);
		txtVendorURL = new TextField<String>();
		txtVendorURL.setWidth(MAX_FIELD_WIDTH);
//		txtVendorURL.setAllowBlank(false);
		txtVendorURL.setMaxLength(MAX_VALUE_LENGTH);
		txtVendorURL.setValue(UIContext.Fujitsu_URL);
		txtVendorURL.setStyleAttribute("padding-right","5px");
		//txtVendorURL.setReadOnly(true);
		
		TableData tdCloudConfigField1 = new TableData();
//		tdCloudConfigField1.setWidth("40%");		
		tdCloudConfigField1.setHorizontalAlign(HorizontalAlignment.LEFT);
		connectionFieldSet.add(txtVendorURL,tdCloudConfigField1);

		lblVendorUsername = new LabelField(UIContext.Constants.azureAccountName());
		lblVendorUsername.setWidth(MAX_LABEL_WIDTH);
		
		connectionFieldSet.add(lblVendorUsername,tdCloudConfigLabel);
		txtVendorUsername = new TextField<String>();
		txtVendorUsername.setWidth(MAX_FIELD_WIDTH);
//		txtVendorUsername.setAllowBlank(false);
		txtVendorUsername.setMaxLength(MAX_VALUE_LENGTH);
		Utils.addToolTip(txtVendorUsername, UIContext.Constants.azureAccountNameToolTip());
		connectionFieldSet.add(txtVendorUsername,tdCloudConfigField1);

		lblVendorPassword = new LabelField(UIContext.Constants.azureSecretKey());
		lblVendorPassword.setWidth(MAX_LABEL_WIDTH);
		connectionFieldSet.add(lblVendorPassword,tdCloudConfigLabel);
		txtVendorPassword = new PasswordTextField();
		txtVendorPassword.setWidth(MAX_FIELD_WIDTH);
//		txtVendorPassword.setAllowBlank(false);
		txtVendorPassword.setPassword(true);
		txtVendorPassword.setMaxLength(MAX_VALUE_LENGTH);
		Utils.addToolTip(txtVendorPassword, UIContext.Constants.azureSecretKeyToolTip());
		connectionFieldSet.add(txtVendorPassword,tdCloudConfigField1);
		
				
		LayoutContainer lcProxySettings = defineProxySettingsSection(); 
		TableData tdProxySettings = new TableData();
		tdProxySettings.setColspan(2);
		tdProxySettings.setHorizontalAlign(HorizontalAlignment.LEFT);
				
		connectionFieldSet.add(lcProxySettings,tdProxySettings);
		
		
		TableLayout tlConnSettings2 = new TableLayout();
		tlConnSettings2.setWidth("85%");
		tlConnSettings2.setColumns(2);
		tlConnSettings2.setCellPadding(0);
		tlConnSettings2.setCellSpacing(1);
		
		
		advSettingsFieldSet = new FieldSet();
		advSettingsFieldSet.setHeadingHtml(UIContext.Constants.ArchiveAdvanced());
		advSettingsFieldSet.setLayout(tlConnSettings2);
		advSettingsFieldSet.setStyleAttribute("margin", "5,5,5,5");
		
		
		TableData tdCloudConfigField2 = new TableData();
		tdCloudConfigField2.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		lblBucketName = new LabelField(UIContext.Constants.azureContainer());
		lblBucketName.setWidth(BUCKET_LABEL_WIDTH);
		if(this.isForEdge)
		{
			lblBucketName.setWidth(90);
		}
		lblBucketName.setStyleAttribute("margin-left", "10px");
		advSettingsFieldSet.add(lblBucketName,tdCloudConfigField2);

		HorizontalPanel panel =new HorizontalPanel();
		panel.setSpacing(5);

		if(!this.isForEdge)
		{
			bucketsStore = new ListStore<BucketDetailsModel>();
			bucketsStore.setDefaultSort("bucketName", SortDir.ASC);

			cmbBucket = new ComboBox<BucketDetailsModel>();
			cmbBucket.setStore(bucketsStore);
			cmbBucket.setDisplayField("name");
			cmbBucket.setWidth(250);
			cmbBucket.setEditable(false);
			cmbBucket.setTriggerAction(TriggerAction.ALL);
					Utils.addToolTip(cmbBucket, UIContext.Constants.azureContainerTooltip());
			cmbBucket.addListener(Events.OnClick, new Listener<FieldEvent>() {

				@Override
				public void handleEvent(FieldEvent be) {
					if(cmbBucket.getValue()==null)
						doRefreshBuckets();
				}
			});
			/*cmbBucket.addListener(Events.SelectionChange, new Listener<SelectionChangedEvent<BucketDetailsModel>>() {
            public void handleEvent(SelectionChangedEvent<BucketDetailsModel> be) {
	            final String bucketName = be.getSelection().get(0).getBucketName();	
	            curSelectedBucket = bucketName;
            	if(!isRegionAvailableForBucketInCache(bucketName))
            	{
					txtRegionName.mask(UIContext.Constants.cloudLoadingRegion());
            		service.getRegionForBucket(getArchiveCloudInfo(),new AsyncCallback<String>() {
    					
    					@Override
    					public void onFailure(Throwable caught) {
    						txtRegionName.unmask();
    						
    					}

    					@Override
    					public void onSuccess(String result) {
    						updateBucketsCache(new BucketDetailsModel(bucketName,result));
    						txtRegionName.unmask();
    						txtRegionName.setValue(result);  
    					}
    				});	
            	}
            	else{
            		txtRegionName.setValue(be.getSelection().get(0).getRegion());
            	}
            }
        });*/

			panel.add(cmbBucket);

			//RenderHostName
			//TODO : REMOVE COMMENTS BELOW
			service.GetHostName(new AsyncCallback<String>() {

				@Override
				public void onSuccess(String result) {
					if(result!=null)
						hostName = result.toLowerCase();
				}

				@Override
				public void onFailure(Throwable caught) {
					hostName = null;
				}
			});

			//TODO : ADD COMMENTS BELOW
			//hostName = "kasra04-w71";


			btnAddBucket = new Button(UIContext.Constants.ArchiveAddSource());
			//	btnAddBucket.setIcon( IconHelper.create("images/default/grid/add.gif"));
			Utils.addToolTip(btnAddBucket, UIContext.Constants.azureAddNewContainer());
			//btnAddBucket.setStyleAttribute("padding-right", "30px");

			btnAddBucket.addSelectionListener(new SelectionListener<ButtonEvent>(){

				@Override
				public void componentSelected(ButtonEvent ce) {

					if(hostName != null)
					{
						if(validate(false) == true)
						{
							final AddCloudBucketWindow addCloudWindow = new AddCloudBucketWindow(getArchiveCloudInfo(),regionsList,hostName);				
							addCloudWindow.setModal(true);				
							addCloudWindow.addWindowListener(new WindowListener()
							{
								public void windowHide(WindowEvent we) {							
									if(!addCloudWindow.isCancelled())
									{
										if ((addCloudWindow.getBucketName()!=null) && (addCloudWindow.getEncodedBucketName() !=null))
										{    					
											BucketDetailsModel newBucket = new BucketDetailsModel(addCloudWindow.getBucketName(),"",addCloudWindow.getEncodedBucketName());
											//										updateBucketsCache(newBucket);
											bucketsStore.add(newBucket);										
											cmbBucket.setValue(newBucket);
										}	
										/*if(addCloudWindow.getRegionsList()!=null)
										regionsList = addCloudWindow.getRegionsList();*/
									}
								}
							});
							addCloudWindow.show();
						}
					}
					else{
						MessageBox msgError = new MessageBox();
						msgError.setIcon(MessageBox.ERROR);
						msgError.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
						msgError.setModal(true);
						msgError.setMinWidth(100);
						msgError.setMessage(UIContext.Constants.cloudHostNameNotFound());

					}
				}
			});	

			if(!mode.equals(ARCHIVE_MODE))
				btnAddBucket.setVisible(false);
			else
				btnAddBucket.setVisible(true);

			panel.add(btnAddBucket);


			refreshBuckets = new Button();
			refreshBuckets.setIcon( IconHelper.create("images/default/grid/refresh.gif"));
			Utils.addToolTip(refreshBuckets, UIContext.Constants.azureRefreshContainers());
			refreshBuckets.addSelectionListener(new SelectionListener<ButtonEvent>(){
				@Override
				public void componentSelected(ButtonEvent ce) {
					doRefreshBuckets();
				}
			});	

			panel.add(refreshBuckets);
			advSettingsFieldSet.add(panel,tdCloudConfigField2);
			TableData tdRefreshLable = new TableData();
			tdRefreshLable.setColspan(2);
			tdRefreshLable.setHorizontalAlign(HorizontalAlignment.LEFT);    
			lblRefresh = new LabelField(UIContext.Constants.azureRefreshLabel()+"<BR><BR>");
			lblRefresh.setWidth(400);
			lblRefresh.setStyleAttribute("padding-left", "110px");
			advSettingsFieldSet.add(lblRefresh,tdRefreshLable);			


			/*tdCloudConfigField2 = new TableData();
		tdCloudConfigField2.setHorizontalAlign(HorizontalAlignment.LEFT);*/
		}
		else{
			txtBucketName = new TextField<String>();
			txtBucketName.setWidth(250);
			txtBucketName.setToolTip(UIContext.Constants.azureContainerTooltip());
			txtBucketName.setStyleAttribute("margin-left", "15px");
			panel.add(txtBucketName);
			advSettingsFieldSet.add(panel,tdCloudConfigField2);
			
			TableData bucketFormatText = new TableData();
			bucketFormatText.setColspan(2);
			bucketFormatText.setHorizontalAlign(HorizontalAlignment.LEFT);   
			lblRefresh = new LabelField();

			lblRefresh.setValue(UIContext.Constants.azureArchiveContainerLabel()+UIContext.cloudBucketARCserveLabel+
					UIContext.Constants.cloudHostName()+UIContext.Constants.cloudArchiveBucketExtentionLabel()+"<BR><BR>");

//			lblRefresh.setWidth(400);
			lblRefresh.setStyleAttribute("padding-left", "120px");
			advSettingsFieldSet.add(lblRefresh,bucketFormatText);
			
		}


		//		tdCloudConfigField2.setWidth("15%");		
		/*lblRegionName = new LabelField(UIContext.Constants.cloudRegion());
		lblRegionName.setWidth(MAX_LABEL_WIDTH);
		lblRegionName.setToolTip(UIContext.Constants.cloudRegion());
		advSettingsFieldSet.add(lblRegionName,tdCloudConfigField2);*/
		

		/*tdCloudConfigField2 = new TableData();
		tdCloudConfigField2.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		txtRegionName = new TextField<String>();
		txtRegionName.setWidth(250);
		txtRegionName.setAllowBlank(false);
		txtRegionName.setMaxLength(MAX_VALUE_LENGTH);
		txtRegionName.setToolTip(UIContext.Constants.CloudBucketNameTooltip());
		txtRegionName.setReadOnly(true);
		advSettingsFieldSet.add(txtRegionName,tdCloudConfigField2);*/
		
		
		lcCloudSettingsContainer.add(connectionFieldSet);
		lcCloudSettingsContainer.add(advSettingsFieldSet);
		
		
	}
	
	
	
	protected void doRefreshBuckets() {
		if(validate(false) == true)
		{
			cmbBucket.mask(UIContext.Constants.azureLoadingContainers());
			service.getCloudBuckets(getArchiveCloudInfo(),new AsyncCallback<CloudModel[]>() {

				@Override
				public void onFailure(Throwable caught) {
					cmbBucket.unmask();
					MessageBox msgError = new MessageBox();
					msgError.setIcon(MessageBox.ERROR);
					msgError.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
					msgError.setModal(true);
					msgError.setMinWidth(400);							
					msgError.setMessage(((BusinessLogicException)caught).getDisplayMessage());
					msgError.show();

				}

				@Override
				public void onSuccess(CloudModel[] result) {

					MessageBox msg = new MessageBox();
					msg.setIcon(MessageBox.ERROR);							
					msg.setModal(true);
					msg.setMinWidth(100);							

					if( (result!=null) && (result.length==1) && result[0].getBucketName().startsWith("Error_"))
					{								
						msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
						msg.setMessage(ArchiveToCloudErrors.getMessage(result[0].getBucketName()));
						msg.show();
					}

					else 
					{	
						CloudModel[] buckets= filterBuckets(result,launchMode);

						if((buckets!=null)&& (buckets.length!=0))
						{	
							BucketDetailsModel oldBucket = cmbBucket.getValue();
							bucketsStore.removeAll();					
							//									cacheBuckets(addBucketsToModel(buckets));
							bucketsStore.add(addBucketsToModel(buckets));
							cmbBucket.unmask();	
							if(oldBucket!=null)
								cmbBucket.setValue(oldBucket);
							else	
								cmbBucket.setValue(bucketsStore.getAt(0));
							msg.setIcon(MessageBox.INFO);
							msg.setTitleHtml(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D));
							msg.setMessage(UIContext.Constants.cloudContainersRetreived());
							msg.show();
						}
						else if(buckets.length==0)
						{
							msg.setIcon(MessageBox.INFO);
							msg.setTitleHtml(UIContext.Messages.messageBoxTitleInformation(UIContext.productNameD2D));
							msg.setMessage(UIContext.Messages.cloudNoContainersRetreived(hostName,hostName));
							msg.show();
						}
					}

					cmbBucket.unmask();							
				}
			});	
		}
		
	}



	public LayoutContainer getLcCloudSettingsContainer() {
		return lcCloudSettingsContainer;
	}



	private LayoutContainer defineProxySettingsSection()
	{
		LayoutContainer lcProxySettings = new LayoutContainer();
		TableLayout tlCloudProxySettings = new TableLayout();
		tlCloudProxySettings.setWidth("100%");
		tlCloudProxySettings.setCellPadding(0);
		tlCloudProxySettings.setCellSpacing(5);
		lcProxySettings.setLayout(tlCloudProxySettings);
		
		proxyFieldSet = new FieldSet();   
	    proxyFieldSet.setHeadingHtml(UIContext.Constants.UseProxyLabel());   
	    proxyFieldSet.setCheckboxToggle(true);
		{
			TableLayout tableProxyLayout = new TableLayout();
			tableProxyLayout.setColumns(4);
			tableProxyLayout.setCellPadding(0);
			tableProxyLayout.setCellSpacing(4);
			tableProxyLayout.setWidth("100%");		
			proxyFieldSet.setLayout(tableProxyLayout);	
		}

		proxyFieldSet.addListener(Events.Expand, new Listener<FieldSetEvent>()
				{
					@Override
					public void handleEvent(FieldSetEvent be) {
						setUseProxy(true);
						setProxyFieldsEnabled(true);	
					}
				});

		proxyFieldSet.addListener(Events.Collapse, new Listener<FieldSetEvent>()
				{
					@Override
					public void handleEvent(FieldSetEvent be) {
						setUseProxy(false);
						setProxyFieldsEnabled(false);	
					}
				});
		
		lblProxyServer = new LabelField(UIContext.Constants.ProxyServerLabel());
		lblProxyServer.setWidth(80);
		proxyFieldSet.add(lblProxyServer);
		
		txtProxyServer = new TextField<String>();
		txtProxyServer.setWidth(180);
		//txtProxyServer.setStyleAttribute("padding-left", "5px");
//		txtProxyServer.setAllowBlank(false);
		Utils.addToolTip(txtProxyServer, UIContext.Constants.ProxyServerTooltip());
		proxyFieldSet.add(txtProxyServer);
		
		lblProxyPort = new LabelField(UIContext.Constants.ProxyServerPortLabel());
		lblProxyPort.setWidth(35);
		proxyFieldSet.add(lblProxyPort);
		
		ProxyPort = new NumberField();
//		ProxyPort.setAllowBlank(false);
		ProxyPort.setWidth(60);
		Utils.addToolTip(ProxyPort, UIContext.Constants.ProxyPortTooltip());
		ProxyPort.setValidator(new Validator(){
			@Override
			public String validate(Field<?> field, String value) {
				int iProxyPort = ProxyPort.getValue().intValue();
				
				MessageBox msgError = new MessageBox();
				msgError.setIcon(MessageBox.ERROR);
				msgError.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
				msgError.setModal(true);
				if(iProxyPort < 0 || iProxyPort > 65535)
				{
					ProxyPort.setValue(null);
					msgError.setMessage(UIContext.Constants.EnterValidProxyServerPortMessage());
					msgError.show();
				}
				return null;
			}
			
		});
		
		ProxyPort.setValidateOnBlur(true);
		proxyFieldSet.add(ProxyPort);
		
		cbProxyRequiresAuth = new CheckBox();
		cbProxyRequiresAuth.setBoxLabel(UIContext.Constants.IsProxyRequiresAuthenticationLabel());
		//cbProxyRequiresAuth.setVisible(true);
		
		cbProxyRequiresAuth.addListener(Events.Change, new Listener<FieldEvent>()
		{
			@Override
			public void handleEvent(FieldEvent be) {
				if(cbProxyRequiresAuth.getValue())
				{
					useProxyAuth = true;
					proxyAuthEnable(true);
				}
				else
				{
					useProxyAuth = false;
					proxyAuthEnable(false);
					//btOK.setEnabled(true);
				}
			}
		});
		
		TableData tdProxyRequireAuth = new TableData();
		tdProxyRequireAuth.setColspan(4);
		tdProxyRequireAuth.setHorizontalAlign(HorizontalAlignment.LEFT);
		proxyFieldSet.add(cbProxyRequiresAuth,tdProxyRequireAuth);
		
		lblProxyUserName = new LabelField(UIContext.Constants.ProxyUsernameLabel());
		 
		proxyFieldSet.add(lblProxyUserName);
		
		txtProxyUsername = new TextField<String>();
		txtProxyUsername.setWidth(180);
//		txtProxyUsername.setAllowBlank(false);
		txtProxyUsername.setValidator(new Validator() {
			@Override
			public String validate(Field<?> field, String value) {
				if (value == null
				//		|| value.indexOf('\\') < 1	// 21101280-1 koyto01  commented out
						|| (value.trim().indexOf('\\') == (value.trim()
								.length() - 1))) {
					return UIContext.Constants
							.backupSettingsDestInvalidUserName();
				}

				return null;
			}
		});
		//txtProxyUsername.setStyleAttribute("padding-left", "20px");
		Utils.addToolTip(txtProxyUsername, UIContext.Constants.ProxyUsernameTooltip());
		TableData tdProxyUsername = new TableData();
		tdProxyUsername.setColspan(3);
		tdProxyUsername.setHorizontalAlign(HorizontalAlignment.LEFT);

		proxyFieldSet.add(txtProxyUsername,tdProxyUsername);
		
		lblProxyPassword = new LabelField(UIContext.Constants.ProxyPasswordLabel());
		proxyFieldSet.add(lblProxyPassword);
		
		txtProxyPassword = new PasswordTextField();
		txtProxyPassword.setWidth(180);
//		txtProxyPassword.setAllowBlank(false);
		txtProxyPassword.setPassword(true);
		//txtProxyPassword.setStyleAttribute("padding-left", "20px");
		Utils.addToolTip(txtProxyPassword, UIContext.Constants.ProxyPasswordTooltip());
		proxyFieldSet.add(txtProxyPassword,tdProxyUsername);		
		
		
		proxyLble = new LabelField(UIContext.Constants.loginUsernameExample());
		TableData tdProxy = new TableData();
		tdProxy.setColspan(4);
		tdProxy.setHorizontalAlign(HorizontalAlignment.LEFT);			
		proxyFieldSet.add(proxyLble,tdProxy);

		
		
		lcProxySettings.add(proxyFieldSet);
		
		return lcProxySettings;
	}
	
	public void setProxyFieldsEnabled(boolean enabled)
	{
		setUseProxy(enabled);
		
		lblProxyServer.setEnabled(enabled);
		lblProxyPort.setEnabled(enabled);
		txtProxyServer.setEnabled(enabled);
		ProxyPort.setEnabled(enabled);
		cbProxyRequiresAuth.setEnabled(enabled);
		
		if(enabled)
		{
			boolean bEnabled = cbProxyRequiresAuth.getValue();  
			lblProxyUserName.setEnabled(bEnabled);
			lblProxyPassword.setEnabled(bEnabled);
			txtProxyUsername.setEnabled(bEnabled);
			txtProxyPassword.setEnabled(bEnabled);
		}
	}
	
	public void setUseProxy(boolean useProxy) {
		this.useProxy = useProxy;
	}
	
	private void proxyAuthEnable(boolean bEnabled) 
	{
		lblProxyUserName.setEnabled(bEnabled);
		txtProxyUsername.setEnabled(bEnabled);
		lblProxyPassword.setEnabled(bEnabled);
		txtProxyPassword.setEnabled(bEnabled);
	}
	
	@Override
	public void refreshData(ArchiveCloudDestInfoModel in_CloudConfig)
	{
		if(in_CloudConfig == null)
		{
			useProxy = false;
			proxyFieldSet.collapse();
			return;
		}
		
		if(in_CloudConfig.getcloudVendorURL() != null)
		{
			txtVendorURL.setValue(in_CloudConfig.getcloudVendorURL());
		}
		
		if(in_CloudConfig.getcloudVendorUserName() != null)
		{
			txtVendorUsername.setValue(in_CloudConfig.getcloudVendorUserName());
		}
		
		if(in_CloudConfig.getcloudVendorPassword() != null)
		{
			txtVendorPassword.setValue(in_CloudConfig.getcloudVendorPassword());
		}
		
		/*if(in_CloudConfig.getVendorCertificatePath() != null)
		{
			vendorCertificatePath.setDestination(in_CloudConfig.getVendorCertificatePath());
		}
		
		if(in_CloudConfig.getCertificatePassword() != null)
		{
			txtCertificatePassword.setValue(in_CloudConfig.getCertificatePassword());
		}
		
		if(in_CloudConfig.getVendorHostname() != null)
		{
			txtVendorHostname.setValue(in_CloudConfig.getVendorHostname());
		}
		
		nfVendorPort.setValue(in_CloudConfig.getVendorPort());*/
		
		if(in_CloudConfig.getcloudBucketName() != null)
		{	

			if(!isForEdge)
			{
				BucketDetailsModel bucket = new BucketDetailsModel(in_CloudConfig.getcloudBucketName(),"",in_CloudConfig.getencodedBucketName());

				//			bucketCache.put(bucket.getBucketName(), bucket.getRegion());
				bucketsStore.removeAll();
				bucketsStore.add(bucket);

				cmbBucket.setValue(bucket);
				//			txtRegionName.setValue(bucket.getRegion());
			}
			else
			{
				txtBucketName.setValue(in_CloudConfig.getcloudBucketName());
				encodeBucketForEdge= in_CloudConfig.getencodedBucketName();
			}

		}
		
		boolean bUseProxy = in_CloudConfig.getcloudUseProxy();
		
		if(bUseProxy)
		{
			useProxy = true;
			
			proxyFieldSet.collapse();
			proxyFieldSet.expand();
			setProxyFieldsEnabled(true);
			
			txtProxyServer.setValue(in_CloudConfig.getcloudProxyServerName());
			ProxyPort.setValue(in_CloudConfig.getcloudProxyPort());
			
			Boolean bProxyRequiresAuth = in_CloudConfig.getcloudProxyRequireAuth();
			
			cbProxyRequiresAuth.setValue(bProxyRequiresAuth);
			proxyAuthEnable(bProxyRequiresAuth);
			
			if(bProxyRequiresAuth)
			{
				txtProxyUsername.setValue(in_CloudConfig.getcloudProxyUserName());
				txtProxyPassword.setValue(in_CloudConfig.getcloudProxyPassword());
			}
		}
		else
		{
			useProxy = false;
			proxyFieldSet.collapse();
			setProxyFieldsEnabled(false);
		}
	}
	
	
	public boolean validate(boolean validateBucket) 
	{
		boolean bValidated = true;
		MessageBox msgError = new MessageBox();
		msgError.setIcon(MessageBox.ERROR);
		msgError.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
		msgError.setModal(true);
		msgError.setMinWidth(100);
		
		if((txtVendorURL.getValue() == null) || (txtVendorURL.getValue().length() > MAX_VALUE_LENGTH))
		{
			msgError.setMessage(UIContext.Constants.EnterValidURLMessage());
			msgError.show();
			bValidated = false;
			return bValidated;
		}
		
		if((txtVendorUsername.getValue() == null) || (txtVendorUsername.getValue().length() > MAX_VALUE_LENGTH))
		{
			msgError.setMessage(UIContext.Constants.EnterValidAccountNameMessage());
			msgError.show();
			bValidated = false;
			return bValidated;
		}
		
		if((txtVendorPassword.getValue() == null) || (txtVendorPassword.getValue().length() > MAX_VALUE_LENGTH))
		{
			msgError.setMessage(UIContext.Constants.EnterValidSecretKeyMessage());
			msgError.show();
			bValidated = false;
			return bValidated;
		}
		
		if(useProxy)
		{
			if((txtProxyServer.getValue() == null) || (txtProxyServer.getValue().length() > MAX_VALUE_LENGTH))
			{
				msgError.setMessage(UIContext.Constants.EnterValidProxyServerMessage());
				msgError.show();
				bValidated = false;
				return bValidated;
			}
			
			if(ProxyPort.getValue() == null || ProxyPort.getValue().intValue() < 1 || ProxyPort.getValue().intValue() > 65535)
			{
				msgError.setMessage(UIContext.Constants.EnterValidProxyServerPortMessage());
				msgError.show();
				bValidated = false;
				return bValidated;
			}
			
			if(useProxyAuth)
			{
				if((txtProxyUsername.getValue() == null) || (txtProxyUsername.getValue().length() > MAX_VALUE_LENGTH))
				{
					msgError.setMessage(UIContext.Constants.EnterValidProxyUsernameMessage());
					msgError.show();
					bValidated = false;
					return bValidated;
				}
				
				if((txtProxyPassword.getValue() == null) || (txtProxyPassword.getValue().length() > MAX_VALUE_LENGTH))
				{
					msgError.setMessage(UIContext.Constants.EnterValidProxyPasswordMessage());
					msgError.show();
					bValidated = false;
					return bValidated;
				}
			}
		}
		if(validateBucket)
		{	
			if(!isForEdge)
			{
				if((cmbBucket.getValue() != null))
				{
					if((cmbBucket.getValue().getBucketName() == null))
					{
						msgError.setMessage(UIContext.Constants.SelectContainerMessage());
						msgError.show();
						bValidated = false;
						return bValidated;
					}

					/*if((txtRegionName.getValue()==null) || (cmbBucket.getValue().getRegion()==null))
				{			
					msgError.setMessage(UIContext.Constants.SelectRegionMessage());
					msgError.show();
					bValidated = false;
					return bValidated;
				}		*/		
				}
				else if(cmbBucket.getValue()==null)
				{
					msgError.setMessage(UIContext.Constants.SelectContainerMessage());
					msgError.show();
					bValidated = false;
					return bValidated;
				}
			}			
		}
		return bValidated;
	}
	
	public ArchiveCloudDestInfoModel save()
	{
		if(archiveCloudConfigModel == null)
		{
			archiveCloudConfigModel = new ArchiveCloudDestInfoModel();
		}
		
		archiveCloudConfigModel.setcloudVendorType(new Long(CloudVendorType.WindowsAzure.getValue()));
		archiveCloudConfigModel.setCloudSubVendorType(new Long(CloudSubVendorType.WindowsFujistu.getValue()));
		archiveCloudConfigModel.setcloudVendorURL(txtVendorURL.getValue());
		archiveCloudConfigModel.setcloudVendorUserName(txtVendorUsername.getValue());
		archiveCloudConfigModel.setcloudVendorPassword(txtVendorPassword.getValue());
		//TO DO
		if(!isForEdge){
		archiveCloudConfigModel.setcloudBucketName(cmbBucket.getValue() != null ? cmbBucket.getValue().getBucketName() : null);
		archiveCloudConfigModel.setencodedBucketName(cmbBucket.getValue() != null ? cmbBucket.getValue().getEncodedBucketName() : null);
		//		archiveCloudConfigModel.setcloudBucketRegionName(txtRegionName.getValue());
		}
		else
		{
			archiveCloudConfigModel.setcloudBucketName(txtBucketName.getValue() != null ? txtBucketName.getValue() : null);
			archiveCloudConfigModel.setencodedBucketName(encodeBucketForEdge != null ? encodeBucketForEdge : null);
		}
		if(useProxy)
		{
			archiveCloudConfigModel.setcloudProxyServerName(txtProxyServer.getValue());
			archiveCloudConfigModel.setcloudProxyPort(ProxyPort.getValue().longValue());
						
			if(useProxyAuth)
			{
				archiveCloudConfigModel.setcloudProxyUserName(txtProxyUsername.getValue());
				archiveCloudConfigModel.setcloudProxyPassword(txtProxyPassword.getValue());
			}
			
			archiveCloudConfigModel.setcloudProxyRequireAuth(useProxyAuth);
		}

		archiveCloudConfigModel.setcloudUseProxy(useProxy);
		
		return archiveCloudConfigModel;
	}
	
	@Override
	public ArchiveCloudDestInfoModel getArchiveCloudInfo()
	{
		ArchiveCloudDestInfoModel cloudInfo = new ArchiveCloudDestInfoModel();
				
		cloudInfo.setcloudVendorURL(txtVendorURL.getValue());
		cloudInfo.setcloudVendorUserName(txtVendorUsername.getValue());
		cloudInfo.setcloudVendorPassword(txtVendorPassword.getValue());

		if(!isForEdge)
		{	
		cloudInfo.setcloudBucketName(cmbBucket.getValue() == null ? "" :cmbBucket.getValue().getBucketName());
		cloudInfo.setencodedBucketName(cmbBucket.getValue() == null ? "" :cmbBucket.getValue().getEncodedBucketName());
		}
		else
		{
			cloudInfo.setcloudBucketName(txtBucketName.getValue() != null ? txtBucketName.getValue() : null);
			cloudInfo.setencodedBucketName(encodeBucketForEdge != null ? encodeBucketForEdge : null);
		}

		if(useProxy)
		{
			cloudInfo.setcloudProxyServerName(txtProxyServer.getValue());
			cloudInfo.setcloudProxyPort(ProxyPort.getValue().longValue());
						
			if(useProxyAuth)
			{
				cloudInfo.setcloudProxyUserName(txtProxyUsername.getValue());
				cloudInfo.setcloudProxyPassword(txtProxyPassword.getValue());
			}
			
			cloudInfo.setcloudProxyRequireAuth(useProxyAuth);
		}
		cloudInfo.setcloudVendorType(new Long(CloudVendorType.WindowsAzure.getValue()));
		cloudInfo.setCloudSubVendorType(new Long(CloudSubVendorType.WindowsFujistu.getValue()));
		cloudInfo.setcloudUseProxy(useProxy);
		
		return cloudInfo;
	}
	
	public ArchiveCloudDestInfoModel getArchiveCloudConfigModel()
	{		
		return archiveCloudConfigModel;
	}

	
	
	
	
	/*private List<BucketDetailsModel> addBucketsToModel(String[] buckets)
	{
		List<BucketDetailsModel> bucketsList = new ArrayList<BucketDetailsModel>();
		
		for (String bucket : buckets) {
			BucketDetailsModel bucketModel = new BucketDetailsModel();
			bucketModel.setBucketName(bucket);
			bucketsList.add(bucketModel);
			
		}
		return bucketsList;
	}*/
	
	private List<BucketDetailsModel> addBucketsToModel(CloudModel[] buckets)
	{
		List<BucketDetailsModel> bucketsList = new ArrayList<BucketDetailsModel>();
		
		for (CloudModel bucket : buckets) {
			BucketDetailsModel bucketModel = new BucketDetailsModel();
			bucketModel.setBucketName(bucket.getBucketName());
			bucketModel.setEncodedBucketName(bucket.getEncodedBucketName());
			bucketsList.add(bucketModel);		
		}
		return bucketsList;
	}
		
	
	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.backup.ICloudBucketsCacheManager#cacheBuckets(java.util.List)
	 
	
	
	 (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.backup.ICloudBucketsCacheManager#updateBucketsCache(com.ca.arcflash.ui.client.model.BucketDetailsModel)
	 
	public void updateBucketsCache(BucketDetailsModel bucket)
	{
		bucketCache.put(bucket.getBucketName(), bucket.getRegion());
		bucketsStore.removeAll();
		bucketsStore.add(getBucketsListFromCache());
	}
	
	
	 (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.backup.ICloudBucketsCacheManager#getBucketsListFromCache()
	 
	public List<BucketDetailsModel> getBucketsListFromCache()
	{
		List<BucketDetailsModel> buckets = new ArrayList<BucketDetailsModel>();		
		
		for (Map.Entry<String,String> entry : bucketCache.entrySet()) {
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();
			buckets.add(new BucketDetailsModel(key,value));
		}

		return buckets;		
	}
	
	 (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.backup.ICloudBucketsCacheManager#isRegionAvailableForBucketInCache(java.lang.String)
	 
	public boolean isRegionAvailableForBucketInCache(String bucket)
	{
		String region = bucketCache.get(bucket);
		return (region==null)?false:true;
	}*/

	/* (non-Javadoc)
	 * @see com.ca.arcflash.ui.client.backup.ICloudBucketsCacheManager#filterBuckets(java.lang.String[], java.lang.String)
	 */
	/*public String[] filterBuckets(String[] buckets, String mode)
	{
		Vector<String> vector = new Vector<String>(buckets.length);
		
		StringBuffer filterToken = new StringBuffer(UIContext.cloudBucketD2DArchiveLabel);
		StringBuffer filterTokenV2 = new StringBuffer(UIContext.cloudBucketD2DF2CLabel);
		
		if(mode.equals(ARCHIVE_MODE))
		{
			filterToken.append(hostName);
			filterTokenV2.append(hostName);
		}
		
		for (int i = 0; i < buckets.length; i++) {
			if(buckets[i].startsWith(filterToken.toString()) || buckets[i].startsWith(filterTokenV2.toString()))
			{
				vector.add(buckets[i]);
			}
		}

		String[] filteredBuckets = new String[vector.size()];
		vector.copyInto(filteredBuckets);
		return filteredBuckets;
	}*/

	public CloudModel[] filterBuckets(CloudModel[] buckets, String mode)
	{
		return Utils.filterBuckets(buckets, mode, hostName);
	}
	
	 public void disableProxyByDefault()
	    {
	    	useProxy = false;
			proxyFieldSet.collapse();
	    }

	public String getVendorUrl() {
		return vendorUrl;
	}



	public void setVendorUrl(String vendorUrl) {
		this.vendorUrl = vendorUrl;
		txtVendorURL.setValue(vendorUrl);
	}
	public void validateForEdge(final AsyncCallback<Long> callback)
	{
		
		if(!isForEdge)
			callback.onSuccess(0L);
		else
		{
			MessageBox msgError = new MessageBox();
			msgError.setIcon(MessageBox.ERROR);
			msgError.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
			msgError.setModal(true);
			msgError.setMinWidth(100);
			
			if (txtBucketName.getValue() == null) {
				msgError.setMessage(UIContext.Constants
						.SelectContainerMessage());
				msgError.show();
				callback.onSuccess(1L);
				return;
			}			
			
			String bucketName = txtBucketName.getValue();			

			verifyBucketNameWithCloud(bucketName,callback);
				
		}
	}
	
	private void verifyBucketNameWithCloud(String bucketName, final AsyncCallback callback)
	{
		
		
		service.validateBucketNameForAzure(bucketName,isForEdge, new AsyncCallback<Long>() {

			@Override
			public void onFailure(Throwable caught) {
				MessageBox msgError = new MessageBox();
				msgError.setIcon(MessageBox.ERROR);
				msgError.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
				msgError.setModal(true);
				msgError.setMinWidth(400);
				msgError.setMessage(((BusinessLogicException)caught).getDisplayMessage());
				msgError.show();
				callback.onSuccess(-1L);
			}

			@Override
			public void onSuccess(Long result) {
				MessageBox msgError = new MessageBox();
				msgError.setIcon(MessageBox.ERROR);
				msgError.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
				msgError.setModal(true);
				msgError.setMinWidth(400);
				if(result == -1L)//validating bucket name failed due to reg expression not satisfied
				{
					msgError.setMessage(UIContext.Constants.azureBucketNameInvalidSpecialChar());
					msgError.show();
					callback.onSuccess(-1L);
				}
				else if(result == 0L){//success
					callback.onSuccess(0L);
				}
				else if(result == -2L)//web client not available to communicate with webservice 
				{			
					msgError.setMessage(UIContext.Constants.azureBucketVerificationFailed());
					msgError.show();
					callback.onSuccess(-2L);
				}
				else
				{
					msgError.setMessage(ArchiveToCloudErrors.getMessage("AzError_"+result));					
					msgError.show();	
					callback.onSuccess(result);
				}
			}
		});
		
	
	}



	@Override
	public void setVendorURL(HashMap<String, CloudVendorInfoModel> providerInfo) {
		if(providerInfo != null && providerInfo.size() > 0)
		{
			if(providerInfo.get(2+"") != null)
			{
				CloudVendorInfoModel model = providerInfo.get(2+"");
				String vendorURL = model.getUrl();
				txtVendorURL.setValue(vendorURL);
			}
		}
		
	}



	@Override
	public void showHideFieldsForCloudSubVendor(
			CloudSubVendorType cloudSubVendor) {
		// TODO Auto-generated method stub
		
	}

}
