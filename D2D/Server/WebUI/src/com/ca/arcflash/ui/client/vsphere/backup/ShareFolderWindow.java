package com.ca.arcflash.ui.client.vsphere.backup;

import com.ca.arcflash.ui.client.UIContext;

import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.PathSelectionPanel;
import com.ca.arcflash.ui.client.common.UserPasswordWindow;
import com.ca.arcflash.ui.client.common.Utils;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;

public class ShareFolderWindow extends Window {
	private ShareFolderWindow thisWindow;

	private PathSelectionPanel pathSelection;

	public ShareFolderWindow() {
		thisWindow = this;

		this.setSize(450, 160);
		this.setClosable(false);
		this.setResizable(false);
		this.setHeadingHtml(UIContext.Constants.shareFolderWindowHeading());

		TableLayout tableLayout = new TableLayout();
		tableLayout.setWidth("98%");
		tableLayout.setCellSpacing(5);
		this.setLayout(tableLayout);

		initDestinationContainer(this);

		addButton(this);
	}

	private void addButton(ContentPanel cp) {
		Button okButton = new Button();
		okButton.ensureDebugId("7005DDFF-CEF1-4686-B1F3-34F4FC72452E");
		okButton.setText(UIContext.Constants.ok());
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				thisWindow.mask(UIContext.Constants.saveDestinationMaskText());

				String destination = pathSelection.getDestination();
				if (destination != null && !destination.trim().equals("")) {
					final CommonServiceAsync commonService = GWT.create(CommonService.class);
					commonService.getDestDriveType(pathSelection.getDestination(), new BaseAsyncCallback<Long>() {
						@Override
						public void onFailure(Throwable caught) {
							thisWindow.unmask();
							super.onFailure(caught);
						}

						@Override
						public void onSuccess(Long result) {// share folder
							if (result == PathSelectionPanel.REMOTE_DRIVE) {// remote drive

								String userName = pathSelection.getUsername();
								if (userName != null && userName.length() > 0) {
									// user name is exist
									commonService.checkRemotePathAccess(pathSelection.getDestination(), "",userName, pathSelection.getPassword(),
											new BaseAsyncCallback<Boolean>() {
												@Override
												public void onSuccess(
														Boolean result) {
													if (result) {
//														pathSelection.setUsername(pathSelection.getUsername());
//														pathSelection.setPassword(pathSelection.getPassword());
														thisWindow.unmask();
														thisWindow.hide();
													} else														
													 showUsernamePasswordDialog();
												}
											});

								} else {
									showUsernamePasswordDialog();
								}

							} else {
								thisWindow.unmask();
								thisWindow.hide();
							}

						}

						private void showUsernamePasswordDialog() {
							final UserPasswordWindow dlg = new UserPasswordWindow(pathSelection.getDestination(), "", "");
			    			dlg.setModal(true);
			    			
			    			dlg.addWindowListener(new WindowListener()
			    			{				
			    				public void windowHide(WindowEvent we) {
			    					if (dlg.getCancelled() == false)
			    					{//click ok button
			    						String username = dlg.getUsername();
			    						String password = dlg.getPassword();
			    						pathSelection.setUsername(username);
			    						pathSelection.setPassword(password);													
			    						thisWindow.unmask();
			    						thisWindow.hide();
			    					}//click cancel button
			    					else
			    						thisWindow.unmask();
			    				}
			    			});
			    			dlg.show();
			    		}
					});
				} else {
					MessageBox msg = new MessageBox();					
					msg.setIcon(MessageBox.ERROR);
					msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNamevSphere));
					msg.setMessage(UIContext.Constants.messageBoxAlertChooseDestinationForVM());
					msg.setModal(true);
					Utils.setMessageBoxDebugId(msg);
					msg.show();
					thisWindow.unmask();
				}
			}
		});

		Button cancelButton = new Button();
		cancelButton.ensureDebugId("5E7328CD-CF6A-4b1b-9336-91ABACC3379E");
		cancelButton.setText(UIContext.Constants.cancel());
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				pathSelection.setDestination(null);
				thisWindow.hide();
			}
		});

		cp.setButtonAlign(HorizontalAlignment.RIGHT);
		cp.addButton(okButton);
		cp.addButton(cancelButton);
	}

	private void initDestinationContainer(LayoutContainer container) {

		// LabelField label = new LabelField();
		// label.setText(UIContext.Constants.destinationBackupDestination());
		// label.addStyleName("restoreWizardSubItem");
		// container.add(label);

		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.pathSelectionDescription());
		container.add(label);

		initDestSelectPane();
		container.add(pathSelection);
	}

	private void initDestSelectPane() {
		pathSelection = new PathSelectionPanel(false,new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {

			}
		});

		pathSelection.setMode(PathSelectionPanel.BACKUP_MODE);
		pathSelection.setPathFieldLength(280);
		pathSelection.addDebugId("28D3F0FB-8E63-4ead-87E3-708FDBBBDAA0", 
				"66ACBFDD-1F81-4f46-8402-B807F75397EF", "AFA38846-66F6-4e31-9650-A515363061F8");
	}

	public String getDestination() {
		return pathSelection.getDestination();
	}

	public String getUserName() {
		return pathSelection.getUsername();
	}

	public String getPassword() {
		return pathSelection.getPassword();
	}
}