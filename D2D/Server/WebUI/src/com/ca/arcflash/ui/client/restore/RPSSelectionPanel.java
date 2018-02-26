package com.ca.arcflash.ui.client.restore;

import com.ca.arcflash.ui.client.UIContext;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.Element;

public class RPSSelectionPanel extends LayoutContainer {
	
	private Listener<BaseEvent> changeListener = null;
	private LabelField label = new LabelField();
	private Button changeButton = new Button();
	private String destination;
	private String username;
	private String password;
	private String dataStoreName;
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		TableLayout layout = new TableLayout();
		layout.setColumns(2);
		layout.setWidth("100%");
		layout.setCellPadding(2);
		layout.setCellSpacing(2);
		setLayout(layout);
		
		TableData tb = new TableData();
		tb.setWidth("45%");
		
		label.setValue(UIContext.Messages.restoreNoRPSServerSelected(UIContext.productNameRPS));
		if (changeListener != null)
		{
			label.addListener(Events.Change, changeListener);
		}
		
		add(label,tb);
		
		changeButton.setText(UIContext.Messages.restoreChangeRPSServer(UIContext.productNameRPS));
		changeButton.ensureDebugId("5fefb40e-962a-47f1-900b-c8cb2b9f3aa7");
		changeButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				browseRPSServer();
			}			
		});
		
		tb = new TableData();
		tb.setWidth("55%");
		add(changeButton,tb);
		
	}
	
	private void browseRPSServer(){
		D2DSelectionWindow selectionWindow = new D2DSelectionWindow(this);
		selectionWindow.setModal(true);
		selectionWindow.show();
	}
	
	public void setRPSServerName(String serverName){
		label.setValue(serverName);
		label.fireEvent(Events.Change);
	}
	public void setRPSServerNameOnly(String serverName){
		label.setValue(serverName);
	}
	
	public String getRPSServerName(){
		return label.getValue()==null?"":String.valueOf(label.getValue());
	}
	
	public void setChangeListener(Listener<BaseEvent> changeListener )
	{
		this.changeListener = changeListener;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDataStoreName() {
		return dataStoreName;
	}

	public void setDataStoreName(String dataStoreName) {
		this.dataStoreName = dataStoreName;
	}

}
