package com.ca.arcflash.ui.client.restore;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.RecoveryPointModel;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Image;

public class ExchangeGRTSubmitCatalogJobDialog extends Dialog
{	
	private ExchangeGRTSubmitCatalogJobDialog thisWindow;
	private String strDialogResult;
	
	private PasswordPane passwordPaneLite;	
	private RecoveryPointModel recoveryPointModel;  // selected recovery point model	
	private String sessionPath;

	public ExchangeGRTSubmitCatalogJobDialog(RecoveryPointModel recPointModel, String sessPath)
	{
		super();
		this.thisWindow = this;
		this.recoveryPointModel = recPointModel;	
		this.sessionPath = sessPath == null ? "" : sessPath;
		
		this.setResizable(false);
		this.setScrollMode(Scroll.AUTO);
		this.setModal(true);
		this.setHeadingHtml(UIContext.Constants.restoreGRTSubmitGRTCatalogJob());
		this.setButtons(Dialog.OKCANCEL);
		// dialog buttons
				this.getButtonById(Dialog.CANCEL).setMinWidth(50);
				this.getButtonById(Dialog.CANCEL).ensureDebugId("3D64DB4B-39C7-40f7-B986-8B5B7AB31209");
				this.getButtonById(Dialog.CANCEL).setStyleAttribute("margin-left", "10px");
				this.getButtonById(Dialog.CANCEL).addSelectionListener(new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						strDialogResult = Dialog.CANCEL;
						thisWindow.hide();
					}
				});

				this.getButtonById(Dialog.OK).setMinWidth(50);
				this.getButtonById(Dialog.OK).ensureDebugId("055D1E96-C6C5-428f-A0E0-C726848D876A");
				this.getButtonById(Dialog.OK).addSelectionListener(new SelectionListener<ButtonEvent>()
				{
					@Override
					public void componentSelected(ButtonEvent ce)
					{
						if (passwordPaneLite != null && passwordPaneLite.isVisible() && recoveryPointModel != null)
						{
							String password = passwordPaneLite.getPassword();
							if (password != null && password.length() > 0)
							{
								CommonServiceAsync service = GWT.create(CommonService.class);
								Integer sessionNum = recoveryPointModel.getSessionID();
								service.validateSessionPassword(password, sessionPath, sessionNum,
										new BaseAsyncCallback<Boolean>()
										{
											@Override
											public void onFailure(Throwable caught)
											{
												super.onFailure(caught);
											}

											@Override
											public void onSuccess(Boolean isValid)
											{
												if (isValid != null && isValid)
												{
													strDialogResult = Dialog.OK;
													thisWindow.hide();
												}
												else
												{
													thisWindow.showErrorMessage(UIContext.Constants.recoveryPointsInvalidSessionPassword());
												}
											}
										});
							}
							else
							{
								showErrorMessage(UIContext.Constants.restoreGRTPasswordForCatalog());
							}
						}
						else
						{
							strDialogResult = Dialog.OK;
							thisWindow.hide();
						}
					}
				});
	}

	@Override
	protected void onRender(Element parent, int pos)
	{
		// TODO Auto-generated method stub
		super.onRender(parent, pos);
		
		LayoutContainer container = new LayoutContainer();		
		container.setLayout(new RowLayout());

		RowData rd = new RowData();
		Margins margins = new Margins(6);
		rd.setMargins(margins);

		// information
		LayoutContainer lc = new LayoutContainer(new TableLayout(2));
		
		Image image = IconHelper.create("images/default/window/icon-info.gif", 32,32).createImage();
		lc.add(image, new TableData("40px", "32px"));
		
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.restoreConfirmSubmitCatalogJob());
		lc.add(label);	

		container.add(lc, rd);
		
		// password panel
		passwordPaneLite = new PasswordPane();
		passwordPaneLite.ensureDebugId("B21C963D-072C-4077-9467-FA53E17D922E");
		passwordPaneLite.setLitePasswordPane(true);
		passwordPaneLite.setStyleAttribute("padding-top", "10px");
		container.add(passwordPaneLite, rd);	
		
		passwordPaneLite.addListener(Events.Render, new Listener<BaseEvent>()
		{
			@Override
			public void handleEvent(BaseEvent be)
			{				
				if (thisWindow.recoveryPointModel != null)
				{
					// display the encryption password panel if the recovery point is encrypted
					if (thisWindow.recoveryPointModel.isEncrypted())
					{
						passwordPaneLite.setVisible(true);
						passwordPaneLite.autoFillPassword(thisWindow.recoveryPointModel.getSessionGuid());
						thisWindow.setSize(450, 250);
					}
					else
					{
						passwordPaneLite.setVisible(false);
						thisWindow.setSize(450, 150);
					}
				}
				else
				{
					passwordPaneLite.setVisible(false);
				}
			}
		});		

		this.add(container);		
		this.setSize(450, 240);			
	}
	
	public String getPassword()
	{
		String password = "";	
		
		if (passwordPaneLite != null)
		{
			password = passwordPaneLite.getPassword();
			if (password == null)
			{
				password = "";
			}
		}
		
		return password;
	}
	
	public String getDialogResult() {
		return strDialogResult;
	}
	
	protected void showErrorMessage(final String errorMsg)
	{
		if (errorMsg != null && errorMsg.length() > 0)
		{
			MessageBox messageBox = new MessageBox();
			messageBox.addCallback(new Listener<MessageBoxEvent> () {
				@Override
				public void handleEvent(MessageBoxEvent be) {
					if(errorMsg.equals(UIContext.Constants.recoveryPointsInvalidSessionPassword()))
						passwordPaneLite.setFocus();
				}
				
			});
			messageBox.setModal(true);
			messageBox.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
			messageBox.setMessage(errorMsg);
			messageBox.setIcon(MessageBox.ERROR);
			Utils.setMessageBoxDebugId(messageBox);
			messageBox.show();
		}
	}
}
