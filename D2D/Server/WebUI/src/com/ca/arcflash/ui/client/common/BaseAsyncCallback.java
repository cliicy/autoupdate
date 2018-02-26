package com.ca.arcflash.ui.client.common;

import java.util.logging.Logger;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.exception.ServiceConnectException;
import com.ca.arcflash.ui.client.exception.SessionTimeoutException;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.InvocationException;
import com.google.gwt.user.client.rpc.StatusCodeException;

public class BaseAsyncCallback<T> implements AsyncCallback<T> {

	public static Boolean isShow = false;
	public static boolean isServerDown = false;
	static Logger log=Logger.getLogger("BaseAsyncCallback");
	native  public static void close()/*-{
		$wnd.open('', '_self', '');
		$wnd.close();
	}-*/;
	
	protected boolean reload;
	protected String productName;
	protected String formatStr="";
	
	public BaseAsyncCallback() {
		this(true);
	}
	
	public BaseAsyncCallback(boolean reload) {
		this.reload = reload;
	}
	
	public BaseAsyncCallback(String productName) {
		this.productName = productName;
	}
	
	public void setProductName(String productName) {
		this.productName = productName;
	}
	
	private void reload() {
		isShow = false;
		isServerDown = false;
		
		if (reload) {
			Window.Location.reload();
		}
	}
	
	protected void showMessageBoxForReload(String message){		
		isServerDown = true;
		MessageBox messageBox = new MessageBox();
		messageBox.addCallback(new Listener<MessageBoxEvent>(){

			@Override
			public void handleEvent(MessageBoxEvent be) {
//			    RootPanel.get().clear();
//				LoginWindow window = new LoginWindow();
//				window.setClosable(false);
//				window.addWindowListener(new WindowListener(){
//					@Override
//					public void windowHide(WindowEvent event) {
//						super.windowHide(event);
//						createUIContent();
//					}
//				});
//				window.setModal(true);
//				window.show();
				reload();
			}
			
		});
		
		//String prodName = getProductName();
		messageBox.setTitleHtml(UIContext.Constants.errorTitle());
		messageBox.setMessage(message);
		messageBox.setIcon(MessageBox.ERROR);
		messageBox.setModal(true);
		//messageBox.getDialog().ensureDebugId("990fb21f-f04a-4edc-81d4-1429f1cc9edf");
		//setButtonId(messageBox, MessageBox.OK, "dyz0qz3d-8z81-7866-ga1j-zi0lh0ypc136");
		messageBox.setMinWidth(400);
		Utils.setMessageBoxDebugId(messageBox);
		messageBox.show();
	}

	private String getProductName() {
		String prodName = "";
		if(productName != null)
			prodName = productName;
		if(UIContext.uiType == 1){
			prodName = UIContext.productNamevSphere;
		}else{
			prodName = UIContext.productNameD2D;
		}
		return prodName;
	}
	
	@Override
	public void onFailure(Throwable caught) {
		if (isShow)
			return;
		
		isShow = true;
		log.info("arcflash.ui, caught name :" + caught.getClass().getName());
		log.info("carcflash.ui, caught  info : " + caught.getMessage());
		if (caught instanceof InvocationException){
			log.info("arcflash.ui, caught instanceof InvocationException");
			//when open D2D and vsphere in different tab, if one UI session timeout, the other UI throw StatusCodeException. we should reload this UI.
			if(caught instanceof StatusCodeException){
				StatusCodeException e = (StatusCodeException)caught;
				log.info("arcflash.ui, StatusCode : " + e.getStatusCode());
				if(e.getStatusCode() == 500){
					reload();
				}else if(e.getStatusCode() ==0){
					//When press ESC, FireFox and Chrome may cancel the aysnchrous operation and 
					//cause this status code
					//while Stop tomcat, we may also get this error, so popup an error message for it
//					showMessageBox(UIContext.Constants.reloadForErrorCodeZero());
				}else{
					showMessageBoxForReload(UIContext.Constants.cantConnectToServer());
				}
			}else{
				showMessageBoxForReload(UIContext.Constants.cantConnectToServer());
			}
		}
		
		//UI session timeout
		if (caught instanceof SessionTimeoutException){
			reload();
		}
		
		if (caught instanceof ServiceConnectException){
			MessageBox messageBox = new MessageBox();
			
			messageBox.addCallback(new Listener<MessageBoxEvent>(){

				@Override
				public void handleEvent(MessageBoxEvent be) {
					reload();
				}
				
			});
			
			//String prodName = getProductName();
			messageBox.setTitleHtml(UIContext.Constants.errorTitle());
			messageBox.setMessage(((ServiceConnectException)caught).getDisplayMessage());
			messageBox.setIcon(MessageBox.ERROR);
			messageBox.setModal(true);
			messageBox.setMinWidth(400);
			//messageBox.getDialog().ensureDebugId("2226a44f-b575-496c-a19a-fee7dc69c417");
			//setButtonId(messageBox, MessageBox.OK, "i3d0a80m-97qs-zgxx-jvpt-kit228j97wrt");
			Utils.setMessageBoxDebugId(messageBox);
			messageBox.show();
		}
		
		if (caught instanceof BusinessLogicException){
			
			BusinessLogicException exception = (BusinessLogicException)caught;
			
			GWT.log(exception.getErrorCode().toString(), null);
			
			//web service session timeout
			if (exception.getErrorCode().equals("4294967302")){
				reload();
			}
			
//			if (exception.getErrorCode().equals("4294967298")){
//				isShow = false;
//				return;
//			}
			if (exception.getErrorCode().equals("8053063681")) {
				String link=UIContext.Messages.troubleShootingLink(UIContext.externalLinks==null?"javascript:void(0)":UIContext.externalLinks.getJvmOutOfMemoryHelp());
				exception.setDisplayMessage(UIContext.Messages.jvmOutOfMemoryGuide()+link);
			}
			
			
			if (exception.getDisplayMessage()==null || exception.getDisplayMessage().trim().length()==0)
				return;
			
			showErrorMessage(exception);
		}
	}

	public void showErrorMessage(BusinessLogicException exception) {
		showErrorMessage(exception.getDisplayMessage());		
	}
	
	public void showErrorMessage(String message) {
		MessageBox messageBox = new MessageBox();
		messageBox.addCallback(new Listener<MessageBoxEvent>(){

			@Override
			public void handleEvent(MessageBoxEvent be) {
				isShow = false;
			}
			
		});
		
		//String prodName = getProductName();
		messageBox.setTitleHtml(UIContext.Constants.errorTitle());
		messageBox.setMessage(message);
		messageBox.setIcon(MessageBox.ERROR);
		messageBox.setModal(true);
		messageBox.setMinWidth(400);
		Utils.setMessageBoxDebugId(messageBox);
		messageBox.show();
}

	@Override
	public void onSuccess(T result) {
		
	}
	
//	private void createUIContent() {
//		LoginServiceAsync service = GWT.create(LoginService.class);
//		service.getVersionInfo(new BaseAsyncCallback<VersionInfoModel>() {
//			@Override
//			public void onFailure(Throwable caught) {
//				GWT.log("Error", caught);
//			}
//			@Override
//			public void onSuccess(VersionInfoModel result) {
//				UIContext.serverVersionInfo = result;
//		        RootPanel.get().add(new HomepagePanel());
//			}
//	   });	
//	}
	
	public static void setButtonId(MessageBox messageBox, String buttonId, String id) {
		Button button = messageBox.getDialog().getButtonById(buttonId);
		if(button != null) {
			button.ensureDebugId(id);
		}
	}
	
	public String getFormatStr() {
		return formatStr;
	}

	public void setFormatStr(String formatStr) {
		this.formatStr = formatStr;
	}
}
