package com.ca.arcflash.ui.client.coldstandby.edge.setting;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.exception.ClientException;
import com.ca.arcflash.ui.client.exception.ServiceConnectException;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.MessageBox.MessageBoxType;
import com.extjs.gxt.ui.client.widget.button.Button;

public class TestMonitorConnectionCallback extends BaseAsyncCallback<Void> {

	private Button buttonConnect;
	private String server;
	private int port;
	private String protocal;

	public TestMonitorConnectionCallback(String server, int port, String protocal, Button buttonConnect){
		this.buttonConnect = buttonConnect;
		this.server = server;
		this.port = port;
		this.protocal = protocal;
	}
	
	@Override
	public void onFailure(Throwable caught) {
		String errorMessage = null;
		if (caught instanceof ServiceConnectException)
			errorMessage = UIContext.Messages.testMonitorConnectionError(
					server, port , protocal);
		else
			errorMessage = ((ClientException) caught).getDisplayMessage();

		MessageBox messageBox = new MessageBox();
		messageBox.getDialog().ensureDebugId("84e3cfdb-947e-47d2-8923-933ba088a668");
		messageBox.setMinWidth(200);
		messageBox.setType(MessageBoxType.ALERT);
		messageBox.setIcon(MessageBox.ERROR);
		messageBox.setTitleHtml(UIContext.Constants.failed());
		messageBox.setMessage(errorMessage);
		Utils.setMessageBoxDebugId(messageBox);
		messageBox.show();
		buttonConnect.enable();
	}

	@Override
	public void onSuccess(Void result) {
		MessageBox messageBox = new MessageBox();
		messageBox.getDialog().ensureDebugId("23b7c75f-4254-40ab-96b2-e2a9bb20b099");
		messageBox.setType(MessageBoxType.CONFIRM);
		messageBox.setIcon(MessageBox.INFO);
		messageBox.setTitleHtml(UIContext.Constants.successful());
		messageBox.setMessage(UIContext.Constants.testConnectionSuccessful());
		Utils.setMessageBoxDebugId(messageBox);
		messageBox.show();
		buttonConnect.enable();
	}
}
