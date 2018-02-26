package com.ca.arcflash.ui.client.restore;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.EncrypedRecoveryPoint;
import com.ca.arcflash.ui.client.model.EncrypedRecoveryPoint.VerifyStatus;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Label;

public class RecoveryPointPasswordWindow extends Window {
	private final CommonServiceAsync service = GWT.create(CommonService.class);

	private RecoveryPointPasswordWindow window;
	private TextField<String> pwdField;
	private EncrypedRecoveryPoint encrypedRecoveryPoint;

	public RecoveryPointPasswordWindow(
			final EncrypedRecoveryPoint encrypedRecoveryPoint) {
		this.encrypedRecoveryPoint = encrypedRecoveryPoint;
		this.window = this;
		this.setWidth(300);
		this.setHeight(120);
		this.setResizable(false);
		this.setHeadingHtml(UIContext.Constants.sessionPasswordWindowTitle());

		LayoutContainer pwsContainer = new LayoutContainer();
		TableLayout layout = new TableLayout();
		layout.setColumns(2);
		layout.setCellPadding(2);
		layout.setCellSpacing(2);
		pwsContainer.setLayout(layout);

		Label encrptlabel = new Label(UIContext.Constants
				.settingsLabelEncyrptionPassword());
		encrptlabel.addStyleName("restoreWizardSubItemDescription");
		pwsContainer.add(encrptlabel);

		pwdField = new TextField<String>();
		pwdField.ensureDebugId("81244DB2-0ECA-4dc1-840D-90AAB4419393");
		pwdField.setAllowBlank(false);
		pwdField.setPassword(true);
		pwdField.setMaxLength(Utils.EncryptionPwdLen);
		pwsContainer.add(pwdField);

		Button okButton = new Button(UIContext.Constants.ok());
		okButton.ensureDebugId("3B62B299-D7D6-4335-BB05-DDED902A77FB");
		Button cancelButton = new Button(UIContext.Constants.cancel());
		cancelButton.ensureDebugId("CB41E1BC-184E-481d-BF90-F7CCAF95139D");

		okButton.addSelectionListener(new OKButtonSelectionListener());

		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				window.hide();
			}

		});

		this.setLayout(new CenterLayout());
		this.addButton(okButton);
		this.addButton(cancelButton);
		this.add(pwsContainer);

	}

	public EncrypedRecoveryPoint getEncrypedRecoveryPoint() {
		return encrypedRecoveryPoint;
	}

	private final class OKButtonSelectionListener extends SelectionListener<ButtonEvent> {

		@Override
		public void componentSelected(ButtonEvent ce) {window.mask(UIContext.Constants.validating());
			
			if (!pwdField.validate())
				return;
			
			service.validateSessionPassword(pwdField.getValue(),
					encrypedRecoveryPoint.getBackupDestination(),
					encrypedRecoveryPoint.getSessionNumber(),
					new BaseAsyncCallback<Boolean>() {

						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
							window.unmask();
						}

						@Override
						public void onSuccess(Boolean result) {
							if (result!=null && result.booleanValue()){
								encrypedRecoveryPoint.setPasswordVerified(VerifyStatus.SUCCESS_VERIFIED);
								encrypedRecoveryPoint.setPassword(pwdField.getValue());
								window.unmask();
								window.hide();
							}else{
								window.unmask();
								MessageBox messageBox = new MessageBox();
								String title = "";
								if(UIContext.uiType ==1){
									title = UIContext.Messages.messageBoxTitleError(UIContext.productNamevSphere);
								}else{
									title = UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D);
								}
								messageBox.setTitleHtml(title);
								messageBox.setMessage(UIContext.Constants.recoveryPointsInvalidSessionPassword());
								messageBox.setIcon(MessageBox.ERROR);
								messageBox.setModal(true);
								Utils.setMessageBoxDebugId(messageBox);
								messageBox.show();
							}
						}

					});
		}
	}
	
	
}
