package com.ca.arcflash.ui.client.service;

import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.homepage.HomepageService;
import com.ca.arcflash.ui.client.homepage.HomepageServiceAsync;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.google.gwt.core.client.GWT;

public interface Broker {
	 LoginServiceAsync loginService = GWT.create(LoginService.class);
	 HomepageServiceAsync homeService = GWT.create(HomepageService.class);	
	 CommonServiceAsync commonService = GWT.create(CommonService.class);
}
