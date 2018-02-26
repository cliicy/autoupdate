package com.ca.arcflash.ui.client.coldstandby;

import com.ca.arcflash.ha.model.ARCFlashNode;
import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.HelpTopics;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.monitor.MonitorService;
import com.ca.arcflash.ui.client.monitor.MonitorServiceAsync;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class ProvisionWindow extends Window {
	
	private final ColdStandbyServiceAsync service = GWT.create(ColdStandbyService.class);
	private final MonitorServiceAsync monitorService = GWT.create(MonitorService.class);
	
	protected final static int WIDTH = 550;
	protected final static int HEIGHT = 520;
	
	protected BaseAsyncCallback<Integer> callback;
	protected Button shutdownButton;
	protected Button provisionButton;
	protected ProvisionPointsContainer provisionPanel;
	
	public ProvisionWindow() {
		init();
	}
	
	protected void init() {
		setHeadingHtml(UIContext.Constants.coldStandbySnapshotTitle());
		setWidth(WIDTH);
		setHeight(HEIGHT);
		setResizable(false);

		TableLayout layout = new TableLayout();
		
		setLayout(layout);
		
		LabelField descField = new LabelField();
		descField.setValue(UIContext.Constants.coldStandbySnapshotProvisionDesc());
		descField.setStyleAttribute("padding", "5px, 5px, 10px, 5px");
		
		TableData data = new TableData();
		add(descField);
		
		provisionPanel = getProvisionPanel();
		
		add(provisionPanel, data);
		
		addShutdownVMButton();
		
		addProvisionButton(provisionPanel);
		
		addCloseButton();
		
		addHelpButton();
		
		callback = new BaseAsyncCallback<Integer>(){

				@Override
				public void onFailure(Throwable caught) {
					shutdownButton.enable();
					showFailWarning();
				}

				private void showFailWarning() {
				MessageBox box = MessageBox.alert(UIContext.Messages
						.messageBoxTitleWarning(UIContext.productNameVCM),
						UIContext.Constants.provistionPointShutDownVMFails(),
						null);
				Utils.setMessageBoxDebugId(box);	
				}

				@Override
				public void onSuccess(Integer result) {
//					shutdownButton.enable();
					if(result > 0)
						showFailWarning();
					else {
						Info.display(UIContext.Constants.successful(), UIContext.Constants.provistionPointShutDownVMSucceeds());
						provisionPanel.highlightCurrentSnapShot();
						refreshHighLightedRunningSnapshot();
					}
				}
				
		 };
	}
	
	protected void refreshHighLightedRunningSnapshot() {
		ColdStandbyManager.getInstance().getHomepage().getProvisionPanel().refreshHighLightedRunningSnapshot();
	}
	
	protected ProvisionPointsContainer getProvisionPanel() {
		return new ProvisionPointsContainer(this);
	}

	protected void addShutdownVMButton() {

		shutdownButton = new Button();
		shutdownButton.setText(UIContext.Constants.provistionPointShutDownVM());
		shutdownButton.setIcon(AbstractImagePrototype.create(UIContext.IconBundle.power_off_vm()));
		shutdownButton.ensureDebugId("3cf193bf-753e-4081-a3e0-b9dbfacb89d0");
		shutdownButton.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				MessageBox messageBox = new MessageBox();
				messageBox.setButtons(MessageBox.YESNO);
				messageBox.setIcon(MessageBox.WARNING);
				messageBox.setTitleHtml(UIContext.Messages.messageBoxTitleWarning(UIContext.productNameVCM));
				String msgStr = UIContext.Constants.provistionPointShutDownVMConfirm();
				messageBox.setMessage(msgStr);
				Utils.setMessageBoxDebugId(messageBox);
				messageBox.addCallback(new Listener<MessageBoxEvent>() {

					@Override
					public void handleEvent(MessageBoxEvent be) {
						shutdownButton.disable();
						if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
							shutDownVM();
						}
						else
							shutdownButton.enable();
					}
				});
				
				messageBox.show();
			}
			
		});
		
		shutdownButton.disable();
		
		addButton(shutdownButton);
	
	}
	
	protected void shutDownVM() {
		final ARCFlashNode currentNode = ColdStandbyManager.getInstance().getVCNavigator().getSelectServerNode();
		if(ColdStandbyManager.getInstance().getVCNavigator().isSelectMoniteeFromMonitor()) {
			String uuid = currentNode.getUuid();
			monitorService.shutDownVM(uuid, callback);
		}
		else {
			service.shutDownVM(ColdStandbyManager.getVMInstanceUUID(), callback);
		}
	}

	protected void addCloseButton() {
		Button close = new Button();
		close.setText(UIContext.Constants.cancel());
		close.ensureDebugId("f8879372-410a-4a3e-b7c4-2d6b21ceaddc");
		close.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				hide();
			}
			
		});
		
		addButton(close);
	}
	
	protected void addHelpButton() {
		Button helpButton = new Button();
		helpButton.setText(UIContext.Constants.help());
		helpButton.ensureDebugId("1b8dd4a0-a0ea-417f-8b04-b2c894d06571");
		helpButton.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				String URL = UIContext.externalLinks.getVirtualStandbyRecoveryPointSnapshotsHelp();
				HelpTopics.showHelpURL(URL);
			}
			
		});
		
		addButton(helpButton);
	}

	protected void addProvisionButton(
			final ProvisionPointsContainer provisionPanel) {
		
		provisionButton = new Button();
		provisionButton.setIcon(AbstractImagePrototype.create(UIContext.IconBundle.power_on_vm()));
		provisionButton.setText(UIContext.Constants.provistionPointPowerOnVM());
		provisionPanel.ensureDebugId("c685bb72-1d44-4a2d-9d35-7de52b0e36d2");
		provisionButton.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				ProvisionWindow.this.mask();
				provisionPanel.provisionSelectedPoint();
			}
			
		});
		provisionButton.disable();
		addButton(provisionButton);
	}

}
