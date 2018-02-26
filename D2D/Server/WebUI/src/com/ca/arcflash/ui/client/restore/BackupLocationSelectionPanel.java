package com.ca.arcflash.ui.client.restore;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.PathSelectionPanel;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.Element;

public class BackupLocationSelectionPanel extends LayoutContainer {
	
	private boolean isForEdge = false;
	private PathSelectionPanel pathSelection;
	private RPSSelectionPanel rpsSelection;
	private LayoutContainer selectionContainer;
	
	private Radio localOrShare;
	private Radio rpsServer;
	private RadioGroup locationGroup;
	private boolean isShowLabel = true;
	
	public BackupLocationSelectionPanel(){
		pathSelection = new PathSelectionPanel(isForEdge, null, false);
		pathSelection.setWidth(482);
		pathSelection.setMode(PathSelectionPanel.RESTORE_MODE);
		pathSelection.setPathFieldLength(350);
		pathSelection.addDebugId("ABB69ED1-670A-48ab-8601-9861BFF04E53", 
				"51D530D3-2DAB-400d-B640-DED692412EDF", "091D0C8D-C1AC-499e-82EB-EA4E20AB7BEC");
		
		rpsSelection = new RPSSelectionPanel();
		rpsSelection.setVisible(false);
	}
	
	public BackupLocationSelectionPanel(boolean isForEdge){
		this();
		this.isForEdge = isForEdge;
	}
	
	public BackupLocationSelectionPanel(boolean isForEdge,boolean isShowLable){
		this();
		this.isForEdge = isForEdge;
		this.isShowLabel = isShowLable;
	}
	
	public void setChangeListener(Listener<BaseEvent> changeListener )
	{
		pathSelection.setChangeListener(changeListener);
		rpsSelection.setChangeListener(changeListener);
	}
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		TableLayout layout = new TableLayout();
		if(isShowLabel)
			layout.setColumns(2);
		layout.setWidth("100%");
		setLayout(layout);
		
		TableData tb = new TableData();
		tb.setWidth("167px");
		
		if(isShowLabel){
			LabelField label = new LabelField();
			label.addStyleName("restoreWizardSubItemDescription");
			label.setValue(UIContext.Constants.restoreLocationType());
			add(label,tb);
		}
		LayoutContainer locationTypeContainer = new LayoutContainer();
		locationTypeContainer.setLayout(new TableLayout(2));
		
		localOrShare = new Radio();
		localOrShare.ensureDebugId("d9af59d5-8414-4a4c-b340-fa6c08fa3be3");
		localOrShare.setBoxLabel(UIContext.Constants.restoreLocationLocalOrShare());
		localOrShare.setValue(true);
		localOrShare.addListener(Events.Change, new Listener<FieldEvent>(){
			@Override
			public void handleEvent(FieldEvent be) {
				if(localOrShare.getValue()==true){
					pathSelection.setVisible(true);
					rpsSelection.setVisible(false);
				}
			}
		});
		locationTypeContainer.add(localOrShare);
		
		rpsServer = new Radio();
		rpsServer.ensureDebugId("a96de886-0cd9-4b8c-9b5b-da76f142e1ca");
		rpsServer.setBoxLabel(UIContext.Messages.restoreLocationRPS(UIContext.productNameRPS));
		rpsServer.addListener(Events.Change, new Listener<FieldEvent>(){
			@Override
			public void handleEvent(FieldEvent be) {
				if(rpsServer.getValue()==true){
					pathSelection.setVisible(false);
					rpsSelection.setVisible(true);
					
				}
			}
		});
		locationTypeContainer.add(rpsServer);
		
		locationGroup = new RadioGroup();
		locationGroup.add(localOrShare);
		locationGroup.add(rpsServer);
		
		add(locationTypeContainer);
		if(isShowLabel){
			LabelField label = new LabelField();
			label.addStyleName("restoreWizardSubItemDescription");
			label.setValue(UIContext.Constants.restoreBackupLocation());
			add(label,tb);
		}
		selectionContainer = new LayoutContainer();
		selectionContainer.setLayout(new FlowLayout());
		
		
		selectionContainer.add(pathSelection);
		
		selectionContainer.add(rpsSelection);
		
		add(selectionContainer);
		
	}
	
	public void setDefaultLocaiton(boolean isLocal){
		localOrShare.setValue(isLocal);
		rpsServer.setValue(!isLocal);
		
	}
	
	public void addListenerForPathSelection(EventType eventType, Listener<? extends BaseEvent> listener){
		pathSelection.addListener(eventType, listener);
	}
	
	public void setDestination(String destination){
		if(localOrShare.getValue())
			pathSelection.setDestination(destination);
		else
			rpsSelection.setDestination(destination);
	}
	
	public String getDestination(){
		if(localOrShare.getValue())
			return pathSelection.getDestination();
		else
			return rpsSelection.getDestination();
	}
	
	public void setUsername(String username){
		if(localOrShare.getValue())
			pathSelection.setUsername(username);
		else
			rpsSelection.setUsername(username);
	}

	public String getUsername(){
		if(localOrShare.getValue())
			return pathSelection.getUsername();
		else
			return rpsSelection.getUsername();
	}
	
	public void setPassword(String password){
		if(localOrShare.getValue())
			pathSelection.setPassword(password);
		else
			rpsSelection.setPassword(password);
	}
	
	public String getPassword(){
		if(localOrShare.getValue())
			return pathSelection.getPassword();
		else
			return rpsSelection.getPassword();
	}
	
	public void setRpsServerName(String rpsServer){
		rpsSelection.setRPSServerNameOnly(rpsServer);
	}
	
	public String getRpsServerName(){
		return rpsSelection.getRPSServerName();
	}
	
	public boolean isRpsServer(){
		return rpsServer.getValue();
	}
	
	public String getDataStoreName(){
		return rpsSelection.getDataStoreName();
	}
	
	public void setDataStoreName(String dataStoreName){
		rpsSelection.setDataStoreName(dataStoreName);
	}
}
