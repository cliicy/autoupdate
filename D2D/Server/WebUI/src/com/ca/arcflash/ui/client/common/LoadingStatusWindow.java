package com.ca.arcflash.ui.client.common;

import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.extjs.gxt.ui.client.widget.Window;

public class LoadingStatusWindow extends Window {

	private String title;
	private String message;
	private ProgressBar progressBar = new ProgressBar();
	//private LoadingStatusWindow thisWindow;
	public LoadingStatusWindow(String strTitle,String strMessage)
	{
		this.title = strTitle;
		this.message = strMessage;
		this.setResizable(false);
		this.setMinHeight(50);
		this.setMinWidth(420);
		this.setHeadingHtml(title);
		this.setClosable(false);
		this.setBorders(false);
		this.setBodyBorder(false);
		progressBar.updateText(message);
		progressBar.auto();
		this.add(progressBar);
	}

	public void hideWindow()
	{
		//progressBar.hide();
		this.hide();
	}
	///fanda03 fix 161041 the progress bar's timer and other property is reset when window is hide. very similar as StatusMonitorPanel panel's issue!
	///we don't find the root reason about window hide, need research !
	@Override
	public void afterShow() {
		super.afterShow();
		progressBar.updateText(message);
		progressBar.auto();
	}
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
		progressBar.updateText(message);
		//progressBar.auto();
	}

	




/*	public void setMessage(String message)
	{
		this.message = message;
	}	
*/}
