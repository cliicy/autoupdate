package com.ca.arcflash.ui.client.homepage;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.ExternalLinksModel;
import com.ca.arcflash.ui.client.model.VersionInfoModel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class InternalLogin {
	
	private static final LoginServiceAsync loginService = GWT.create(LoginService.class);
	private static final CommonServiceAsync commonService = GWT.create(CommonService.class);
	
	private static final String INTERNAL_LOGIN_USER = "administrator";
	
	private InternalLogin() {
	}
	
	private static String getProtocol(boolean isHttps) {
		return isHttps ? "https:" : "http:";
	}
	
	public static void validate(String username, String password, String domain, String host, int port, boolean isHttps, final AsyncCallback<Void> callback) {
		UIContext.loginUser = INTERNAL_LOGIN_USER;
		String protocol = getProtocol(isHttps);
		
		loginService.validateUser(protocol, host, port, domain, username, password, new AsyncCallback<Boolean>() {
			
			@Override
			public void onFailure(Throwable caught) {
				if (callback != null) {
					callback.onFailure(caught);
				}
			}
			
			@Override
			public void onSuccess(Boolean result) {
				if (callback != null) {
					callback.onSuccess(null);
				}
			}
			
		});
	}
	
	public static void validate(String uuid, String host, int port, boolean isHttps, final AsyncCallback<Void> callback) {
		UIContext.loginUser = INTERNAL_LOGIN_USER;
		String protocol = getProtocol(isHttps);
		
		loginService.validateUserByUuid(uuid, host, port, protocol, new AsyncCallback<Boolean>() {
			
			@Override
			public void onFailure(Throwable caught) {
				if (callback != null) {
					callback.onFailure(caught);
				}
			}
			
			@Override
			public void onSuccess(Boolean result) {
				if (callback != null) {
					callback.onSuccess(null);
				}
			}
			
		});
	}
	
	public static void login(String uuid, String host, int port, boolean isHttps, final AsyncCallback<Void> callback) {
		UIContext.loginUser = INTERNAL_LOGIN_USER;
		String protocol = getProtocol(isHttps);
		
		loginService.validateUserByUuid(uuid, host, port, protocol, new AsyncCallback<Boolean>() {
			
			@Override
			public void onFailure(Throwable caught) {
				if (callback != null) {
					callback.onFailure(caught);
				}
			}
			
			@Override
			public void onSuccess(Boolean result) {
				getServerVersion(callback);
			}
			
		});
	}
	
	public static void loadContextData(final AsyncCallback<Void> callback) {
		getServerVersion(callback);
	}
	
	private static void getServerVersion(final AsyncCallback<Void> callback) {
		loginService.getVersionInfo(new AsyncCallback<VersionInfoModel>() {

			@Override
			public void onFailure(Throwable caught) {
				if (callback != null) {
					callback.onFailure(caught);
				}
			}

			@Override
			public void onSuccess(VersionInfoModel result) {
				UIContext.serverVersionInfo = result;
				setLocaleSession(callback, result.getLocale(), result.getCountry());
			}
			
		});
	}
	
	private static void setLocaleSession(final AsyncCallback<Void> callback, final String locale, final String country) {
		loginService.saveLocaleSession(locale, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				if (callback != null) {
					callback.onFailure(caught);
				}
			}

			@Override
			public void onSuccess(Void result) {
				getExternalLink(callback, locale, country);
			}
			
		});
	}

	private static void getExternalLink(final AsyncCallback<Void> callback, String language, String country) {
		commonService.getExternalLinks(language, country, new AsyncCallback<ExternalLinksModel>() {

			@Override
			public void onFailure(Throwable caught) {
				if (callback != null) {
					callback.onFailure(caught);
				}
			}

			@Override
			public void onSuccess(ExternalLinksModel result) {
				UIContext.externalLinks = result;
				callback.onSuccess(null);
			}
			
		});
	}

}
