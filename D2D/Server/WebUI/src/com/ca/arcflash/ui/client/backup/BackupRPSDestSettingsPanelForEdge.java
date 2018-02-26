package com.ca.arcflash.ui.client.backup;

import java.util.List;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseComboBox;
import com.ca.arcflash.ui.client.model.rps.RpsHostModel;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class BackupRPSDestSettingsPanelForEdge extends BackupRPSDestSettingsPanel{
	private boolean isRpsHostLoaded;
	
	public BackupRPSDestSettingsPanelForEdge(
			BaseDestinationSettings parentWindow) {
		super(parentWindow);
	}

	private BaseComboBox<RpsHostModel> rpsHostField;
	private ListStore<RpsHostModel> hostStore;

	protected void addRpsHostField(LayoutContainer rpsServerSettingContainer){
		rpsHostField = new BaseComboBox<RpsHostModel>();

		rpsHostField.setTriggerAction(TriggerAction.ALL);
		hostStore = new ListStore<RpsHostModel>();
		rpsHostField.setStore(hostStore);
		rpsHostField.setEditable(true);
		rpsHostField.setDisplayField("hostName");
		rpsHostField.setWidth(MIN_FIELD_WIDTH);
		rpsHostField.ensureDebugId("97A65965-219F-4eb2-ACBB-3E671E6C148B");

		rpsHostField.addListener(Events.OnMouseDown, this.setupMouseDownListener());
		rpsHostField.addSelectionChangedListener(this.setupChangeListener());
		rpsHostField.addListener(Events.Select, this.setupSelectListener());
		rpsHostField.addListener(Events.KeyUp, this.setupKeyupListener());
		TableData tableData = new TableData();
		tableData.setWidth("85%");
		rpsServerSettingContainer.add(rpsHostField, tableData);
	}
	
	private void clearPoliccyList(){
		isPolicyLoaded = false;
		rpsPolicy.clear();
		policyStore.removeAll();
	}
	
	@Override
	protected String getRPSHostName() {
		return rpsHostField.getRawValue();
	}	 

	private int rpsHostFieldContains(String value){
		List<RpsHostModel> hostList = rpsHostField.getStore().getModels();
		
		for(int i = 0; i < hostList.size(); i++)
			if(hostList.get(i).getHostName().compareTo(value) == 0)
				return i;
		return -1;
	}
	
	@Override
	public void setRpsHostValue(RpsHostModel host) {
		RpsHostModel rpsModel = setRpsHost(host);
		hostStore.add(rpsModel);
	}

	public RpsHostModel setRpsHost(RpsHostModel host) {
		RpsHostModel rpsModel = new RpsHostModel();
		rpsModel.setHostName(host.getHostName());
		rpsModel.setUserName(host.getUserName());
		rpsModel.setPassword(host.getPassword());
		
		if(host.getPort() != null && host.getPort() > 0){
			rpsModel.setPort(host.getPort());
		}else {
			rpsModel.setPort(DEFAULT_HTTP_PORT);
		}
		
		if (host.getIsHttpProtocol() != null && host.getIsHttpProtocol())
			rpsModel.setIsHttpProtocol(Boolean.TRUE);
		else
			rpsModel.setIsHttpProtocol(Boolean.FALSE);
		
		rpsHostField.setValue(rpsModel);
		return rpsModel;
	}
	
	private Listener<FieldEvent> setupMouseDownListener() {
		return new Listener<FieldEvent>() {			

			@Override
			public void handleEvent(FieldEvent be) {
				if(isRpsHostLoaded)
					return;
				
				mask(UIContext.Messages.loadingRpsHostInfo(UIContext.productNameRPS));
				configRPSInD2DService
						.getRPSHostList(new AsyncCallback<List<RpsHostModel>>() {

							@Override
							public void onFailure(Throwable caught) {
								thisWindow.unmask();
								rpsHostField.focus();
							}

							@Override
							public void onSuccess(
									List<RpsHostModel> result) {
								hostStore.removeAll();
								hostStore.add(result);
								rpsHostField.focus();
								rpsHostField.expand();
								isRpsHostLoaded = true;
								thisWindow.unmask();
							}

						});
			}
		};
	}
	
	private Listener<BaseEvent> setupKeyupListener() {
		return new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				ComponentEvent ke = ((ComponentEvent) be);
				int index = -1;
				if (ke.getKeyCode() == 13)
					if ((index = rpsHostFieldContains(getRPSHostName())) != -1) {
						setRpsServerValue(hostStore.getAt(index));
						return;
					}

//				parentWindow.getDestChangePanel().disable();
				rpsUsernameField.setValue("");
				rpsPasswordField.setValue("");
				rpsPortField.setValue(DEFAULT_HTTP_PORT);
				httpProtocolRadio.setValue(true);
				clearPoliccyList();
			}
		};
	}
	
	private SelectionChangedListener<RpsHostModel> setupChangeListener() {
		return new SelectionChangedListener<RpsHostModel>() {

			@Override
			public void selectionChanged(
					SelectionChangedEvent<RpsHostModel> se) {
				RpsHostModel rhmHost = se.getSelectedItem();
				setRpsServerValue(rhmHost);
				clearPoliccyList();
			}
		};
	}
	
	private Listener<FieldEvent> setupSelectListener(){
		return new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				RpsHostModel rhmHost = (RpsHostModel) be.getField().getModel();
				
				int index = -1;
				if (rhmHost == null
						&& (index = rpsHostFieldContains(getRPSHostName())) != -1)
					setRpsServerValue(hostStore.getAt(index));
				else
					setRpsServerValue(rhmHost);
			}
		};
	}

	@Override
	public void setEditable(boolean editable) {
		super.setEditable(editable);
		rpsHostField.setEnabled(editable);
	}
}
