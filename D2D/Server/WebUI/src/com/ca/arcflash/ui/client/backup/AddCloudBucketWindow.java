package com.ca.arcflash.ui.client.backup;




import java.net.URLDecoder;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

import com.ca.arcflash.ui.client.ArchiveToCloudErrors;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseSimpleComboBox;
import com.ca.arcflash.ui.client.common.HelpTopics;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.ArchiveCloudDestInfoModel;
import com.ca.arcflash.ui.client.model.CloudModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AddCloudBucketWindow extends Window{
	
	private final static int MAX_LABEL_WIDTH = 150;
	private final static int MAX_VALUE_LENGTH = 306;
	
	private final static int MAX_WIDTH = 500;
	//Change the height of the window, by wanqi06
	private final static int MAX_HEIGHT = 200;
		
	private AddCloudBucketWindow window;
	
	//private HorizontalPanel bucketNamePanel;
	
	private LabelField lblBucketName;
	private LabelField lblD2DArchive;
	private LabelField lblHostName;
	private LabelField lblArchiveBucket;
	
	private TextField<String> txtBucketName;
	
	private LabelField lblRegionName;
	private BaseSimpleComboBox<String> cmbRegion;
	
	private Button okButton;
	private Button cancelButton;
	private Button btHelp;
	
	private String bucketName;
	
	private String encodedBucketName;


	private String regionName;
	
	final LoginServiceAsync service = GWT.create(LoginService.class);
	private List<String> regionsList;
//	boolean regionsLoaded;
	boolean isCancelled;
	String hostName;
	
	ArchiveCloudDestInfoModel cloudInfo = null;
	private LayoutContainer lcAddWindowContainer ;
	
	public AddCloudBucketWindow(ArchiveCloudDestInfoModel in_CloudInfo,List<String> cloudRegionsList, String hostName)
	{		
		cloudInfo = in_CloudInfo;
		this.regionsList = cloudRegionsList;
		this.window = this;
		this.setResizable(false);
		this.setWidth(MAX_WIDTH);
		this.setHeight(MAX_HEIGHT);
		
		if(cloudInfo.getcloudVendorType()==1L)
			this.setHeadingHtml(UIContext.Constants.azureAddNewContainer());
		else	
			this.setHeadingHtml(UIContext.Constants.cloudAddBucketToolTip());

		lcAddWindowContainer = new LayoutContainer();
		
		TableLayout layout = new TableLayout();
		layout.setWidth("95%");
		layout.setHeight("95%");
		layout.setColumns(2);
		layout.setCellPadding(0);
		layout.setCellSpacing(5);		
		lcAddWindowContainer.setLayout(layout);

		
		TableData td = new TableData();
		//tdCloudConfigLabel.setWidth("30%");
		td.setHorizontalAlign(HorizontalAlignment.LEFT);		
		//Bucket name field
		lblBucketName = new LabelField();
		
		if(cloudInfo.getcloudVendorType()==1L)
			lblBucketName.setValue(UIContext.Constants.azureContainerName());
		else 
			lblBucketName.setValue(UIContext.Constants.CloudBucketName());
		
		lblBucketName.setWidth(MAX_LABEL_WIDTH);
		lcAddWindowContainer.add(lblBucketName,td);
		
		//bucketNamePanel = new HorizontalPanel();
		
		//lblD2DArchive = new LabelField(UIContext.cloudBucketD2DArchiveLabel);
		lblD2DArchive = new LabelField(UIContext.cloudBucketARCserveLabel);
		//bucketNamePanel.add(lblD2DArchive);		
		
		lblHostName = new LabelField(hostName+"-");
		//bucketNamePanel.add(lblHostName);
		
		txtBucketName = new TextField<String>();
		txtBucketName.setWidth(300);
		//txtBucketName.setAllowBlank(false);
		txtBucketName.setMaxLength(MAX_VALUE_LENGTH);
		if(cloudInfo.getcloudVendorType()==1L)
			// txtBucketName.setToolTip(UIContext.Constants.azureContainerTooltip());	
			Utils.addToolTip(txtBucketName, UIContext.Constants.azureContainerTooltip());
		else	
			// txtBucketName.setToolTip(UIContext.Constants.CloudBucketNameTooltip());		
			Utils.addToolTip(txtBucketName, UIContext.Constants.CloudBucketNameTooltip());
		//		bucketNamePanel.add(txtBucketName);
		txtBucketName.ensureDebugId("64424FA6-9F43-40da-A977-EE22197995E5");
		lcAddWindowContainer.add(txtBucketName,td);

		if(in_CloudInfo!=null)
		{
			if((in_CloudInfo.getcloudVendorType()!=1L)&& (in_CloudInfo.getcloudVendorType()!=5L))
			{	


				// Region name combo		
				lblRegionName = new LabelField(UIContext.Constants.cloudRegion());
				lblRegionName.setWidth(MAX_LABEL_WIDTH);
				lcAddWindowContainer.add(lblRegionName,td);

				cmbRegion = new BaseSimpleComboBox<String>();
				cmbRegion.setWidth(300);
				cmbRegion.setEditable(false);
				cmbRegion.ensureDebugId("FD8DD196-F04B-45c5-A663-80B5F1F32782");
				//cmbRegion.setMinListWidth(200);
				//cmbRegion.setStyleAttribute("padding-right", "150px");
				Utils.addToolTip(cmbRegion, UIContext.Constants.cloudRegionTooltip());	
				//cmbRegion.setVisible(false);

				if(regionsList == null)
				{
					final MessageBox msgError = new MessageBox();
					msgError.setIcon(MessageBox.ERROR);
					msgError.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
					msgError.setModal(true);
					msgError.setMinWidth(100);
					msgError.getDialog().getButtonById(MessageBox.OK).ensureDebugId("1940FE28-18FE-4624-B4B5-D1DE128E23BD");
					//window.mask(UIContext.Constants.cloudLoadingRegions());
					cmbRegion.mask(UIContext.Constants.cloudLoadingRegions());
					service.getCloudRegions(cloudInfo,new AsyncCallback<String[]>() {

						@Override
						public void onFailure(Throwable caught) {
							cmbRegion.unmask();											
							msgError.setMessage(caught.getMessage());
							Utils.setMessageBoxDebugId(msgError);
							msgError.show();
							isCancelled = true;
							window.hide();
						}

						@Override
						public void onSuccess(String[] result) {				
							cmbRegion.unmask();
							if((result!=null)&& (result.length!=0))
							{
								if( (result.length==1) && result[0].startsWith("Error_"))
								{												
									msgError.setMessage(ArchiveToCloudErrors.getMessage(result[0]));
									Utils.setMessageBoxDebugId(msgError);
									msgError.show();
									isCancelled = true;
									window.hide();
								}			
								else
								{	
									regionsList = Arrays.asList(result);
									cmbRegion.add(regionsList);
									cmbRegion.setVisible(true);
									//window.unmask();
								}
							}

						}
					});
				}

				else {
					cmbRegion.add(regionsList);
					cmbRegion.setVisible(true);
				}
				lcAddWindowContainer.add(cmbRegion,td);
			}
		}
		lblArchiveBucket = new LabelField();
		
		if(cloudInfo.getcloudVendorType()==1L)
			lblArchiveBucket.setValue(UIContext.Constants.azureArchiveContainerLabel()+UIContext.cloudBucketARCserveLabel+hostName+UIContext.Constants.cloudArchiveBucketExtentionLabel());
		else
			lblArchiveBucket.setValue(UIContext.Constants.cloudArchiveBucketLabel()+UIContext.cloudBucketARCserveLabel+hostName+UIContext.Constants.cloudArchiveBucketExtentionLabel());
		
	    lblArchiveBucket.setStyleAttribute("word-wrap", "break-word");
	    lblArchiveBucket.setStyleAttribute("word-break", "break-all");
		lblArchiveBucket.setWidth(480);
					
		td = new TableData();
		td.setColspan(2);
		td.setWidth("95%");
		td.setHorizontalAlign(HorizontalAlignment.LEFT);
		lblArchiveBucket.setStyleAttribute("padding-top", "10px");
		lcAddWindowContainer.add(lblArchiveBucket,td);
		
		// Ok button
		okButton = new Button();
		okButton.setText(UIContext.Constants.ok());
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {

				if(validateBucketNameandRegion() == false)
				{
					return;
				}

				final StringBuffer bucket = new StringBuffer();

				bucket.append(lblD2DArchive.getValue());
				bucket.append(lblHostName.getValue());
				bucket.append(txtBucketName.getValue());

				cloudInfo.setcloudBucketName(bucket.toString());
				
				if((cloudInfo.getcloudVendorType()!=1L)&& (cloudInfo.getcloudVendorType()!=5L))			
					cloudInfo.setcloudBucketRegionName(cmbRegion.getSimpleValue());
				
				String maskMsg = UIContext.Constants.cloudValidatingAndCreatingBucket();
				
				if(cloudInfo.getcloudVendorType()==1L)
					maskMsg = UIContext.Constants.cloudValidatingAndCreatingContainer();
				
				window.mask(maskMsg);					

				service.verifyBucketNameWithCloud(cloudInfo, new AsyncCallback<CloudModel>() {

					@Override
					public void onFailure(Throwable caught) {
						window.unmask();
						isCancelled = true;
						MessageBox msgError = new MessageBox();
						msgError.setIcon(MessageBox.ERROR);
						msgError.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
						msgError.setModal(true);
						msgError.setMinWidth(400);
						msgError.setMessage(((BusinessLogicException)caught).getDisplayMessage());
						Utils.setMessageBoxDebugId(msgError);
						msgError.show();

					}

					@Override
					public void onSuccess(CloudModel result) {
						window.unmask();
						MessageBox msgError = new MessageBox();
						msgError.setIcon(MessageBox.ERROR);
						msgError.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
						msgError.setModal(true);
						msgError.setMinWidth(400);
						if(result.getResult() == -1L)//validating bucket name failed due to reg expression not satisfied
						{
							if((cloudInfo.getcloudVendorType()!=1L)&& (cloudInfo.getcloudVendorType()!=5L))
								msgError.setMessage(UIContext.Constants.cloudBucketNameInvalid());
							else
								msgError.setMessage(UIContext.Constants.azureBucketNameInvalidSpecialChar());
							Utils.setMessageBoxDebugId(msgError);
							msgError.show();								
						}
						else if(result.getResult() == 0L){//success
							bucketName = bucket.toString();
							encodedBucketName = result.getEncodedBucketName();
							if((cloudInfo.getcloudVendorType()!=1L)&& (cloudInfo.getcloudVendorType()!=5L))							
								regionName = cmbRegion.getSimpleValue();									
							window.hide();
						}
						else if(result.getResult() == -2L)//web client not available to communicate with webservice 
						{
							
							if((cloudInfo.getcloudVendorType()==1L))	
								msgError.setMessage(UIContext.Constants.azureBucketVerificationFailed());
							else 	
								msgError.setMessage(UIContext.Constants.cloudBucketVerificationFailed());
							msgError.show();
						}
						else
						{
							if((cloudInfo.getcloudVendorType()==1L))
								msgError.setMessage(ArchiveToCloudErrors.getMessage("AzError_"+result.getResult()));
							else 
								msgError.setMessage(ArchiveToCloudErrors.getMessage("Error_"+result.getResult()));
							
							msgError.show();	
						}
					}
				});

				return;

			}
		});

		// Cancel Button
		cancelButton = new Button();
		cancelButton.setText(UIContext.Constants.cancel());
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
				@Override
				public void componentSelected(ButtonEvent ce) {
					cancelButton.setEnabled(false);	
					isCancelled = true;
					window.hide();					
				}
			});
	
		window.add(lcAddWindowContainer);
		
		okButton.ensureDebugId("FB346567-F5AB-4cd8-A678-D5A2C8A8A7AC");
		cancelButton.ensureDebugId("24C50400-BBC1-43db-BC84-B3AA14524140");
		
		window.addButton(okButton);
		window.addButton(cancelButton);
		
		btHelp = new Button();
		btHelp.setText(UIContext.Constants.help());
		btHelp.setMinWidth(UIContext.MIN_WIDTH);
		btHelp.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {//Add help topics here
				HelpTopics.showHelpURL(UIContext.externalLinks.getaddCloudBucketHelp());
			}
		});
		btHelp.ensureDebugId("07DEA049-AC17-4159-B28A-CCFD0CFEDFF7");
		window.addButton(btHelp);	
	}
	
	
	
	
	@Override
	protected void onRender(Element target, int index) {
		  super.onRender(target, index);
		  this.setFocusWidget(txtBucketName);
		  txtBucketName.focus();
		  
		  
	  }	
	
	
	class AddCloudSourceKeyListener extends KeyListener{

		@Override
		public void componentKeyPress(ComponentEvent event) {
			if (event.getKeyCode() == KeyCodes.KEY_ENTER)
				okButton.fireEvent(Events.Select);
		}		
	}


	public String getBucketName() {
		return bucketName;
	}



	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	
	public String getEncodedBucketName() {
		return encodedBucketName;
	}


	public void setEncodedBucketName(String encodedBucketName) {
		this.encodedBucketName = encodedBucketName;
	}


	public String getRegionName() {
		return regionName;
	}



	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}



	public List<String> getRegionsList() {
		return regionsList;
	}



	public void setRegionsList(List<String> regionsList) {
		this.regionsList = regionsList;
	}

	public boolean isCancelled() {
		return isCancelled;
	}

	public void setCancelled(boolean isCancelled) {
		this.isCancelled = isCancelled;
	}

	private boolean validateBucketNameandRegion()
	{
		boolean bValidated = true;
		MessageBox msgError = new MessageBox();
		msgError.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
		msgError.setIcon(MessageBox.ERROR);
		msgError.setModal(true);
		if((txtBucketName.getValue() == null) || (txtBucketName.getValue().length() == 0))
		{
			window.setFocusWidget(txtBucketName);
			if((cloudInfo.getcloudVendorType()==1L))			
				msgError.setMessage(UIContext.Constants.SelectBucketMessage());
			else
				msgError.setMessage(UIContext.Constants.SelectContainerMessage());
			Utils.setMessageBoxDebugId(msgError);
			msgError.show();
			bValidated = false;
			return bValidated;
		}
		if((cloudInfo.getcloudVendorType()!=1L)&&((cloudInfo.getcloudVendorType()!=5L)))
		{	
			if((cmbRegion.getSimpleValue() == null) || (cmbRegion.getSimpleValue().length() == 0))
			{
				window.setFocusWidget(cmbRegion);
				msgError.setMessage(UIContext.Constants.SelectRegionMessage());
				msgError.show();
				bValidated = false;
				return bValidated;
			}	
		}
		return bValidated;
	}
	
}
