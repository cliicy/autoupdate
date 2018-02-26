package com.ca.arcflash.ui.client.restore.cas;

import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.restore.ExchangeGRTRestoreOptionPanel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;

public class CASSelectionPanel extends LayoutContainer {
	final LoginServiceAsync service = GWT.create(LoginService.class);
	public final static int CAS_TYPE_ORIGINAL = 1;
	public final static int CAS_TYPE_ALTERNATIVE = 2;
	private static int MIN_WIDTH = 90;
	private static int MIN_FIELD_WIDTH = 250;
	private TextField<String> txtClientAccessServer;
	private Button buttonBrowseCas;
	private String userName;
	private String password;
	private ExchangeGRTRestoreOptionPanel parentPanel;
	private int type;  // 1: CAS_TYPE_ORIGINAL  2: CAS_TYPE_ALTERNATIVE
	
	public CASSelectionPanel(ExchangeGRTRestoreOptionPanel parentPal, int iType){
		TableLayout layoutDest = new TableLayout();
		layoutDest.setWidth("380px");
		layoutDest.setColumns(2);
		layoutDest.setCellPadding(0);
		layoutDest.setCellSpacing(0);
		layoutDest.setBorder(0);
		this.setLayout(layoutDest);
		this.setStyleAttribute("padding-top", "0px");
		this.setStyleAttribute("padding-left", "0px");
		parentPanel = parentPal;
		type = iType;
		
		TableData tableDataDest1 = new TableData();
				
		txtClientAccessServer = new TextField<String>();
		txtClientAccessServer.ensureDebugId("de376a30-85fa-4416-812f-c5f76d88ee8c");
		txtClientAccessServer.setWidth(MIN_FIELD_WIDTH);
		txtClientAccessServer.setReadOnly(true);
		txtClientAccessServer.setAllowBlank(false);
		txtClientAccessServer.getMessages().setBlankText(UIContext.Constants.casErrorMsg());		
		txtClientAccessServer.setBorders(false);
		
		
		this.add(txtClientAccessServer, tableDataDest1);

		buttonBrowseCas = new Button();
		buttonBrowseCas.setText(UIContext.Constants.restoreBrowse() /*restoreToAlternateChooseDestination()*/);
		buttonBrowseCas.setMinWidth(MIN_WIDTH);

		buttonBrowseCas.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				
				String productName = UIContext.productNameD2D;
				final MessageBox validatingBox = MessageBox.wait(UIContext.Messages.messageBoxTitleInformation(productName), UIContext.Constants
						.validating(), "");
				Utils.setMessageBoxDebugId(validatingBox);
				// validate the account
				if(type == CAS_TYPE_ORIGINAL) // original
				{
					userName = parentPanel.getTextFieldOriginalUser().getValue();
				    password = parentPanel.getTextFieldOriginalPassword().getValue();
				}
				else
				{
					userName = parentPanel.getTextFieldExchangeUser().getValue();
					password = parentPanel.getTextFieldExchangePassword().getValue();
				}
				
				service.getE15CASList(userName, password, new BaseAsyncCallback<List<String>>()
				{
					private BrowseCASDialog dialogBrowseCas;

					@Override
					public void onFailure(Throwable caught)
					{
						if (validatingBox != null) 
						{
							validatingBox.close();
						}
						
						super.onFailure(caught);
					}

					@Override
					public void onSuccess(List<String> result)
					{
						if (validatingBox != null) 
						{
							validatingBox.close();
						}
						
						// show error message if validation failed
						GWT.log("getExchangeCASServerList Successfully", null);
						if (result != null && result.size()>0)
						{   
							ListStore<BaseModelData> listStore = new ListStore<BaseModelData>();
							for(String name : result){
								BaseModelData model = new BaseModelData();
								model.set("name", name);
								listStore.add(model);
							}

							dialogBrowseCas = new BrowseCASDialog(UIContext.Constants.browseExchCasTitle(), listStore);
							dialogBrowseCas.ensureDebugId("0f19b6d4-93f9-4c7b-b1b0-9bfc6ece1bb6");
							dialogBrowseCas.setResizable(true);
							dialogBrowseCas.setModal(true);
							dialogBrowseCas.show();

							dialogBrowseCas.addWindowListener(new WindowListener() {
								public void windowHide(WindowEvent we) {
									if (dialogBrowseCas.getDialogResult() == Dialog.OK) 
									{
										txtClientAccessServer.setValue(dialogBrowseCas.getCasName());
									}
								}
							});
						}
						else
						{
							CASSelectionPanel.this.showErrorMessage();
						}
					}
				});
			}

		});

		TableData tableDataDest2 = new TableData();
		tableDataDest2.setWidth("100px");
		tableDataDest2.setHorizontalAlign(HorizontalAlignment.RIGHT);
		this.add(buttonBrowseCas, tableDataDest2);
		init();
	}
	
	
	private void init() {
		service.getDefaultE15CAS(userName, password, new BaseAsyncCallback<String>(){
			@Override
			public void onFailure(Throwable caught)
			{
				super.onFailure(caught);
			}

			@Override
			public void onSuccess(String result)
			{
				if(result!=null&!result.equals("")){
					txtClientAccessServer.setValue(result);
				}
			}
		});
	}


	protected void showErrorMessage()
	{
		Utils.showErrorMessage(UIContext.Constants.failedToGetCas());
	}
	
	public String getClientAccessServer(){
		if(txtClientAccessServer==null||txtClientAccessServer.getValue()==null){
			return null;
		}
		return txtClientAccessServer.getValue();
	}


	public boolean validate() {
		if(CASSelectionPanel.this.isVisible()){
			return txtClientAccessServer.validate();
		}else{
			return true;
		}
	}
	
	public void setCredential(String strUserName, String strPwd) {
		userName = strUserName;
		password = strPwd;
	}
}
