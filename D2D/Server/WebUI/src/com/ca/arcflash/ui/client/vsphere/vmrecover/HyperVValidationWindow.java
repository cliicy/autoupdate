package com.ca.arcflash.ui.client.vsphere.vmrecover;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.restore.RestoreOptionsPanel;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class HyperVValidationWindow extends Window {
	
	private LoginServiceAsync service = GWT.create(LoginService.class);
	
	private int MIN_BUTTON_WIDTH = 80; 
	private HyperVConnectionPanel hyperVConnectionPanel;
	
	public HyperVValidationWindow(final RestoreOptionsPanel optionPanel,final AsyncCallback<Boolean> callback){
		this.setWidth(350);
		this.setHeadingHtml(UIContext.Constants.hyperVCredentialWindowTitle());
		
		TableLayout layout = new TableLayout(1);
		layout.setCellSpacing(6);
		layout.setWidth("100%");
		setLayout(layout);
		
		hyperVConnectionPanel = new HyperVConnectionPanel(optionPanel.getBackupVMModel(), HorizontalAlignment.RIGHT);
		this.add(hyperVConnectionPanel);
		
		layout = new TableLayout();
		layout.setColumns(2);
		layout.setCellSpacing(5);
		
		LayoutContainer container = new LayoutContainer();
		container.setLayout(layout);
		
		TableData tb = new TableData();
		tb.setWidth("100%");
		tb.setHorizontalAlign(HorizontalAlignment.CENTER);
		add(container,tb);
		
		Button okButton = new Button();
		okButton.setText(UIContext.Constants.ok());
		okButton.setMinWidth(MIN_BUTTON_WIDTH);
		container.add(okButton, new TableData(Style.HorizontalAlignment.RIGHT, Style.VerticalAlignment.MIDDLE));
		
		Button cancelButton = new Button();
		cancelButton.setText(UIContext.Constants.cancel());
		cancelButton.setMinWidth(MIN_BUTTON_WIDTH);
		container.add(cancelButton, new TableData(Style.HorizontalAlignment.LEFT, Style.VerticalAlignment.MIDDLE));
		
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				HyperVValidationWindow.this.mask(UIContext.Constants.connectToHyperV());
				if (optionPanel.doesOverwriteVM())
				    service.validateHyperV(hyperVConnectionPanel.getHyperVHostName(),
								hyperVConnectionPanel.getHyperVUserName(), hyperVConnectionPanel.getHyperVPassword(), new BaseAsyncCallback<Void>(){

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						HyperVValidationWindow.this.unmask();
						callback.onSuccess(false);
					}

					@Override
					public void onSuccess(Void result) {
						optionPanel.getBackupVMModel().setEsxUsername(hyperVConnectionPanel.getHyperVUserName());
						optionPanel.getBackupVMModel().setEsxPassword(hyperVConnectionPanel.getHyperVPassword());
						
						HyperVValidationWindow.this.unmask();
						hide();
					}
					
				});
				else
					service.validateHyperVAndCheckIfVMExist(hyperVConnectionPanel.getHyperVHostName(), 
									                        hyperVConnectionPanel.getHyperVUserName(), 
									                        hyperVConnectionPanel.getHyperVPassword(),
									                        hyperVConnectionPanel.getVMInstanceUUID(),
									                        hyperVConnectionPanel.getVMName(),
									                        new BaseAsyncCallback<Void>(){
						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
							HyperVValidationWindow.this.unmask();
							callback.onSuccess(false);
						}

						@Override
						public void onSuccess(Void result) {
							optionPanel.getBackupVMModel().setEsxUsername(hyperVConnectionPanel.getHyperVUserName());
							optionPanel.getBackupVMModel().setEsxPassword(hyperVConnectionPanel.getHyperVPassword());
							
							HyperVValidationWindow.this.unmask();
							hide();
						}						
					});
			}

			
		});

		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				hide();
			}

		});
	}
	
	public String getHyperVHostName(){
		return hyperVConnectionPanel.getHyperVUserName();
	}
	
	public String getHyperVUserName(){
		return hyperVConnectionPanel.getHyperVUserName();
	}
	
	public String getHyperVPassword(){
		return hyperVConnectionPanel.getHyperVPassword();
	}
}
