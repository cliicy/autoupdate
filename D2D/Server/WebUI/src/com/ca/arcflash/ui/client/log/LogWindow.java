package com.ca.arcflash.ui.client.log;

import java.util.Date;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyService;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyServiceAsync;
import com.ca.arcflash.ui.client.common.AppType;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.HelpTopics;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.ca.arcflash.ui.client.exception.ServiceConnectException;
import com.ca.arcflash.ui.client.exception.ServiceInternalException;
import com.ca.arcflash.ui.client.model.LogEntry;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class LogWindow extends Window {
	public static final int WINDOW_WIDTH 		= 750;
	public static final int WINDOW_HEIGHT		= 500;
	
	private Window window;
	private String viewLogsHelp=UIContext.externalLinks.getViewLogsHelp();
	
	protected LayoutContainer container = null;
	
	public LogWindow(boolean isForD2D){
		LogOperationHelper.isForD2D = isForD2D;
		this.window = this;
		this.setHeadingHtml(UIContext.Constants.activityLogTableHeader());
		this.setClosable(false);
		//this.setScrollMode(Scroll.AUTOY);
		this.setHeight(WINDOW_HEIGHT);
		this.setWidth(WINDOW_WIDTH);
		this.setResizable(true);
		this.setMaximizable(true);  // requested by Issue: 20110290    Title: MAXIMIZE OPTION FOR ALL TASKS
		this.setClosable(true);
		this.setLayout(new FitLayout());
		
		container = new LayoutContainer();
//		container.setAutoHeight(true);
//		container.setAutoWidth(true);
		container.setStyleAttribute("padding", "6px");
		container.setLayout(new FitLayout());
		
		container.add(new LogPanel(AppType.D2D));
		
		Button okButton = new Button(UIContext.Constants.ok());
		okButton.ensureDebugId("53d0e0e5-4e91-4e33-9a3d-fa64a446b304");
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				window.hide();
			}
		});
		
		this.addButton(okButton);
		
		Button helpButton = new Button();
		helpButton.ensureDebugId("822b8091-e7cd-47b2-92ac-d87ede2be684");
		helpButton.setText(UIContext.Constants.help());
		helpButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				HelpTopics.showHelpURL(viewLogsHelp);
			}
		});
		this.addButton(helpButton);
		
		this.add(container);
	}
	
	public void setViewLogsHelp(String viewLogsHelp){
		this.viewLogsHelp = viewLogsHelp;
	}
	
	static class LogOperationHelper {
		private static CommonServiceAsync service = GWT.create(CommonService.class);
		private static ColdStandbyServiceAsync haService = GWT.create(ColdStandbyService.class);
		public static boolean isForD2D = true;
		
		public static void getActivityLogs(PagingLoadConfig config, AsyncCallback<PagingLoadResult<LogEntry>> callback) throws BusinessLogicException, ServiceConnectException, ServiceInternalException{
			if(isForD2D)
				service.getActivityLogs(config,	callback);
			else {
				haService.getActivityLogs(config,	callback);
			}
			
		}
		
		public static void deleteActivityLog(Date date,AsyncCallback<Void> callback) {
			if(isForD2D)
				service.deleteActivityLog(date, callback);
			else
				haService.deleteActivityLog(date, callback);
		}
	}
}
