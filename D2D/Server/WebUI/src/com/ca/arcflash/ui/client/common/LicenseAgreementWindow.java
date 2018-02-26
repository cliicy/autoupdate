package com.ca.arcflash.ui.client.common;

import com.ca.arcflash.ui.client.UIContext;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

public class LicenseAgreementWindow extends Window {
	
	protected String agreementText;
	
	protected LayoutContainer agreementPanel;
	
	public LicenseAgreementWindow() {
		this(null);
	}
	
	public LicenseAgreementWindow(String agreementText) {
		this.agreementText = agreementText;
		this.addButton(createCloseButton());
		this.addButton(createPrintButton());
	}
	
	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		
		this.setLayout(new FitLayout());
		this.setSize(700, 500);
		this.setResizable(false);
		this.setModal(true);
		this.setHeadingHtml(UIContext.Constants.licenseAgreementHeader());
		
		this.add(createContent());
	}
	
	protected Button createPrintButton() {
		Button button = new Button(UIContext.Constants.licensePrint());
		button.ensureDebugId("45F14E2A-2A78-42fe-9406-11A4AF8E9696");
		button.addStyleName("ca-tertiaryText");
		button.addListener(Events.Select, createPrintListener());		
		return button;
	}
	
	protected <E extends BaseEvent> Listener<E> createPrintListener() {
		return new Listener<E>() {

			@Override
			public void handleEvent(E be) {
				D2DPrint.print(agreementText);
			}
			
		};
	}
	
	protected Button createCloseButton() {
		Button button = new Button(UIContext.Constants.ok());
		button.ensureDebugId("BB917215-539E-41ed-98F0-B6478E7328EE");
		
		button.addSelectionListener(new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				LicenseAgreementWindow.this.hide();
			}
			
		});
		
		this.setFocusWidget(button);
		
		return button;
	}
	
	private Widget createContent() {
		ContentPanel panel = new ContentPanel();
		panel.ensureDebugId("987310de-3cc1-4b9c-9439-5b3202597b97");
		panel.setHeaderVisible(false);
		panel.setBodyBorder(false);
		panel.setLayout(new RowLayout());
		
		LabelField note = new LabelField();
		note.ensureDebugId("c4bb01ae-5475-4956-9f0f-dba1f512cc95");
		note.addStyleName("aboutText");
		note.setValue(UIContext.Constants.licenseAgreementNotes());
		panel.add(note, new RowData(1, Style.DEFAULT, new Margins(16, 25, 32, 25)));
		
		agreementPanel = new LayoutContainer();
		agreementPanel.ensureDebugId("a3a037b3-f8f6-48ad-aa06-851c068b4047");
		agreementPanel.setBorders(true);
		agreementPanel.setScrollMode(Scroll.AUTOY);
		agreementPanel.add(createAgreementWidget());
		panel.add(agreementPanel, new RowData(1, 1, new Margins(0, 10, 5, 10)));
		
		return panel;
	}
	
	private LabelField createAgreementWidget() {
		final LabelField agreement = new LabelField();
		agreement.ensureDebugId("fc42a235-f9a6-4d65-803b-339fcf203d07");
		
		if (agreementText != null && !agreementText.isEmpty()) {
			agreement.setValue(agreementText);
		} else {
			agreement.addListener(Events.Attach, new Listener<BaseEvent>() {
				
				@Override
				public void handleEvent(BaseEvent be) {
					CommonServiceAsync commonService = GWT.create(CommonService.class);
					commonService.getLicenseText(new BaseAsyncCallback<String>() {
						
						@Override
						public void onSuccess(String result) {
							agreementText = result;
							agreement.setValue(result);
						}
						
					});
				}
				
			});
		}
		
		return agreement;
	}
	
}
