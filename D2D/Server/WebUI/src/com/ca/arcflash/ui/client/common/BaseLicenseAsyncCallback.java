package com.ca.arcflash.ui.client.common;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.exception.BusinessLogicException;
import com.extjs.gxt.ui.client.widget.MessageBox;

public class BaseLicenseAsyncCallback<T> extends BaseAsyncCallback<T>{

	@Override
	public void onFailure(Throwable caught) {
		
		
		if (caught instanceof BusinessLogicException) {
			final String timeoutError = "4294967303";
			BusinessLogicException bl = (BusinessLogicException) caught;
			if (bl.getErrorCode().equals(timeoutError)) {
				if (isShow)
					return;
				
				isShow = true;
				
				String errMsg = UIContext.Constants
						.homepageLicenseCheckTimeout();
				
				String productName = UIContext.productNameD2D;
				if (UIContext.uiType == 1) {
					productName = UIContext.productNamevSphere;
				}

				MessageBox msg = new MessageBox();
				msg.setIcon(MessageBox.ERROR);
				msg.setTitleHtml(UIContext.Messages
						.messageBoxTitleInformation(productName));
				msg.setMessage(errMsg);
				msg.setModal(true);
				msg.setMinWidth(90);
				Utils.setMessageBoxDebugId(msg);
				msg.show();
				isShow = false;
				return;
			}
		}
		
		super.onFailure(caught);
	}
}
