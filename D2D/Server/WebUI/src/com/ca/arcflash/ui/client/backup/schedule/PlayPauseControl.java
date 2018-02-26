package com.ca.arcflash.ui.client.backup.schedule;

import com.ca.arcflash.ui.client.UIContext;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class PlayPauseControl extends LayoutContainer {
	private Image playIcon = AbstractImagePrototype.create(UIContext.IconBundle.schedule_play_icon()).createImage();
	private Image pauseIcon = AbstractImagePrototype.create(UIContext.IconBundle.schedule_pause_icon()).createImage();
	private Label labelText = new Label();
	
	private boolean isEnable = true;
	private String playIconText;
	private String playIconTip;
	private String pauseIconText;
	private String pauseIconTip;
	private HandlerRegistration playIconHandler;
	private HandlerRegistration pauseIconHandler;
	private HandlerRegistration labelTextHandler;
	
	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		TableLayout tl = new TableLayout(2);
		//tl.setCellPadding(1);
		//tl.setCellSpacing(1);
		this.setLayout(tl);
		
		labelText.ensureDebugId("bd38e54b-52cf-4acf-98d9-5f818bda122d");
		labelText.setStyleName("homepage_header_hyperlink_label");
		
		playIcon.ensureDebugId("975dc2f1-5187-45d3-a28f-4e18242c425a");
		playIcon.setTitle(playIconTip);
		playIcon.getElement().getStyle().setCursor(Cursor.POINTER);
		
		pauseIcon.ensureDebugId("2c5b6e3e-e459-4bff-a1fc-ef2e3749ccd6");
		pauseIcon.setTitle(pauseIconTip);
		pauseIcon.getElement().getStyle().setCursor(Cursor.POINTER);
		
		if(isEnable){
			labelText.setText(pauseIconText);
			labelText.setTitle(pauseIconTip);
			add(pauseIcon);
			add(labelText);
		}
		else{
			labelText.setText(playIconText);
			labelText.setTitle(playIconTip);
			add(playIcon);
			add(labelText);
		}
		
		registerIconsEvent();
		registerLabelEvent();
	}
	
	private void registerIconsEvent(){
		playIconHandler = playIcon.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				dealClickHandler(true);
			}
		});
		
		pauseIconHandler = pauseIcon.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				dealClickHandler(false);
			}
		});
	}
	
	private void registerLabelEvent(){
		labelTextHandler = labelText.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				dealClickHandler(!isEnable);
			}
		});
	}
	
	private void dealClickHandler(boolean status){
		isEnable = status;
		if(status){
			removeAll();
			labelText.setText(pauseIconText);
			labelText.setTitle(pauseIconTip);
			add(pauseIcon);
			add(labelText);
		}
		else{
			removeAll();
			labelText.setText(playIconText);
			labelText.setTitle(playIconTip);
			add(playIcon);
			add(labelText);
		}
		layout();
	}
	
	public boolean isEnable() {
		return isEnable;
	}

	public void setEnable(boolean isEnable) {
		if(isRendered()){
			dealClickHandler(isEnable);
		}
		else{
			this.isEnable = isEnable;
		}
	}
	
	public void setEditable(boolean isEditable){
		super.setEnabled(isEditable);
		if(labelTextHandler!= null){
			labelTextHandler.removeHandler();
		}
		if(playIconHandler!= null){
			playIconHandler.removeHandler();
		}
		if(pauseIconHandler!= null){
			pauseIconHandler.removeHandler();
		}
		if (isEditable){
			registerIconsEvent();
			registerLabelEvent();
		}
	}
	
	public String getPlayIconText() {
		return playIconText;
	}

	public void setPlayIconText(String playIconText) {
		this.playIconText = playIconText;
	}

	public String getPlayIconTip() {
		return playIconTip;
	}

	public void setPlayIconTip(String playIconTip) {
		this.playIconTip = playIconTip;
	}

	public String getPauseIconText() {
		return pauseIconText;
	}

	public void setPauseIconText(String pauseIconText) {
		this.pauseIconText = pauseIconText;
	}

	public String getPauseIconTip() {
		return pauseIconTip;
	}

	public void setPauseIconTip(String pauseIconTip) {
		this.pauseIconTip = pauseIconTip;
	}
}
