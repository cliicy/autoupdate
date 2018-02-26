package com.ca.arcflash.ui.client.homepage;

import com.ca.arcflash.ui.client.UIContext;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;

public class NCEPanel extends LayoutContainer {
	private String nceLink ="";
	
	public NCEPanel(){
        super();
		this.setStyleName("nce-panel");
		nceLink = UIContext.externalLinks.getUpgradePaidVersionURL();
	}
	public void render(Element target, int index) {
		super.render(target, index);
		TableLayout tableLayout = new TableLayout();
		tableLayout.setWidth("100%");
		tableLayout.setColumns(2);
		tableLayout.setCellPadding(0);
		tableLayout.setCellSpacing(5);
		this.setLayout(tableLayout);
		this.setHeight("100%");
		
		TableData td = new TableData();
		td.setColspan(2);
		
		// Header Section
		LabelField head = new LabelField(UIContext.Constants.upgrade2PaidVersion());
		head.setStyleAttribute("font-size", "13px");
		this.add(head, td);
		// Resolving Object Changes
		TableData tdLeft = new TableData();
		tdLeft.setWidth("10%");		
		tdLeft.setHorizontalAlign(HorizontalAlignment.CENTER);
		tdLeft.setVerticalAlign(VerticalAlignment.TOP);
		TableData tdRight = new TableData();
		tdRight.setWidth("90%");
		tdRight.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		LabelField lblRPS = new LabelField(UIContext.Constants.useRPSDescription());
		LabelField lblLiveChat = new LabelField(UIContext.Constants.useLiveChatDescription());
		lblRPS.setStyleAttribute("font-family", "Tahoma,Arial");
		lblLiveChat.setStyleAttribute("font-family", "Tahoma,Arial");
		Image iconRPS = AbstractImagePrototype.create(UIContext.IconBundle.rps_node()).createImage();
		iconRPS.setStyleName("nce-icon");
		this.add(iconRPS, tdLeft);
		this.add(lblRPS, tdRight);
		Image iconLiveChat = AbstractImagePrototype.create(UIContext.IconBundle.live_chat()).createImage();
		iconLiveChat.setStyleName("nce-icon");
		this.add(iconLiveChat, tdLeft);
		this.add(lblLiveChat, tdRight);
		
		LayoutContainer learnMoreBtn = createLearnMoreButton();
		this.add(learnMoreBtn,td);
	}
	private LayoutContainer createLearnMoreButton() {
		LayoutContainer container = new LayoutContainer();
		TableLayout layout = new TableLayout();
		layout.setWidth("50%");
		layout.setColumns(1);
		container.setLayout(layout);

		HorizontalPanel learnMore=new HorizontalPanel();
		learnMore.setVerticalAlign(VerticalAlignment.MIDDLE);
		learnMore.setStyleName("nce-btn");
		Image icon = AbstractImagePrototype.create(UIContext.IconBundle.learn_more()).createImage();
		icon.setStyleName("nce-icon");
		learnMore.add(icon);
		LabelField lblLearnMore = new LabelField(UIContext.Constants.learnMore());
		lblLearnMore.setStyleAttribute("margin-left", "4px");
		lblLearnMore.setStyleAttribute("font-family", "Tahoma,Arial");
		learnMore.add(lblLearnMore);
		
		container.add(learnMore);
		container.addListener(Events.OnMouseDown, new Listener<BaseEvent>(){

			@Override
			public void handleEvent(BaseEvent be) {
				Window.open(nceLink, "_BLANK", "");
			}});

		return container;
	}
	
}