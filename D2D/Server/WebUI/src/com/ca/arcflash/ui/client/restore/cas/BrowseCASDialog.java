package com.ca.arcflash.ui.client.restore.cas;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.layout.RowData;

public class BrowseCASDialog extends Dialog {

	// dialog items
	private BrowseCASDialog thisWindow;
	private String strDialogResult;
	private String casName;
	private ListView<BaseModelData> listView;

	public BrowseCASDialog(String title, ListStore<BaseModelData> listStore) {
		super();
		this.thisWindow = this;
		this.setHeadingHtml(title);
		this.setButtons(Dialog.OKCANCEL);
		this.setScrollMode(Scroll.AUTOY);
		
		listView = new ListView<BaseModelData>();
		listView.ensureDebugId("89107517-46aa-4174-bf66-4a8007c0a4db");
		listView.setDisplayProperty("name");
		listView.setStore(listStore);
		listView.setTemplate(XTemplate.create("<tpl for=\".\"><div class='x-view-item x-view-item-check'><table cellspacing='2' cellpadding=2><tr><td width='10px'></td><td width='16px'><img src='./images/ex_server.gif'></td><td width='150px'>{name}</td></tr></table></div></tpl>"));
		listView.setBorders(false);
		listView.getStore().setSortField("name");
		listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);   
		listView.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<BaseModelData>(){ 
			@Override
			public void selectionChanged(SelectionChangedEvent<BaseModelData> se) {
				thisWindow.getButtonById(Dialog.OK).setEnabled(true);
			}   
	    });
		listView.setHeight("100%");
		this.add(listView, new RowData(1, 1));
		this.setSize(400, 395);
		
		if (casName == null || casName.isEmpty())
		{
			this.getButtonById(Dialog.OK).setEnabled(false);
		}

		this.getButtonById(Dialog.CANCEL).setWidth(50);
		this.getButtonById(Dialog.CANCEL).ensureDebugId("4C8C3CEF-2F02-46c9-B768-74B22087CABB");
		this.getButtonById(Dialog.CANCEL).setStyleAttribute("margin-left", "10px");
		this.getButtonById(Dialog.CANCEL).addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				strDialogResult = Dialog.CANCEL;
				thisWindow.hide();
			}
		});

		this.getButtonById(Dialog.OK).setWidth(50);
		this.getButtonById(Dialog.OK).ensureDebugId("3029CD62-C48E-4734-B5EB-DA43504B6354");
		this.getButtonById(Dialog.OK).addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				casName=listView.getSelectionModel().getSelectedItem().get("name");
				strDialogResult = Dialog.OK;
				thisWindow.hide();
			}
		});

	}

	public String getDialogResult() {
		return strDialogResult;
	}
	
	public String getCasName() {

		return casName;
	}

	public void setCasName(String casServer) {
		this.casName = casServer;
	}
	
}
