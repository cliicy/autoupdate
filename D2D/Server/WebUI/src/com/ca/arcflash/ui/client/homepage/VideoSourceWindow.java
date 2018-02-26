package com.ca.arcflash.ui.client.homepage;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.model.BackupTypeModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Element;


public class VideoSourceWindow extends Window {
	private final CommonServiceAsync service = GWT.create(CommonService.class);
	private VideoSourceWindow window;
	private Radio radioYouTube;
	private Radio radioCASupport;
	private Button okButton;
	private CheckBox saveSetting;
	private LabelField label;
	private VideoSourceKeyListener keyListener = new VideoSourceKeyListener();
	private final RadioGroup radioGroup = new RadioGroup();
	
	public VideoSourceWindow(final String youtubeURL, final String supportURL)
	{
		this.window = this;
		this.setWidth(350);
		this.setResizable(false);
		this.setHeadingHtml(UIContext.Constants.selectVideoSourceHeader());
		
		TableLayout layout = new TableLayout();
		layout.setWidth("95%");
		layout.setCellPadding(4);
		layout.setCellSpacing(4);
		this.setLayout(layout);
		
		label = new LabelField();		
		label.setValue(UIContext.Messages.selectVideoSourceDescription(UIContext.productNameD2D));
		this.add(label);
		
		radioYouTube = new Radio();
		radioYouTube.ensureDebugId("e0deede6-d1f8-4674-891d-73bc757daf84");
		radioYouTube.setId("BackupNow_Radio_Incremental");
		radioYouTube.setBoxLabel(UIContext.Constants.selectVideoYouTube());
		radioYouTube.addKeyListener(keyListener);
		radioYouTube.setValue(true);
		this.add(radioYouTube);
		radioGroup.add(radioYouTube);
		
		radioCASupport = new Radio();
		radioCASupport.ensureDebugId("937b79f2-7c22-48d9-938d-91510db149da");
		radioCASupport.setId("BackupNow_Radio_Resync");
		radioCASupport.setBoxLabel(UIContext.Messages.selectVideoCASupport(UIContext.companyName));
		radioCASupport.addKeyListener(keyListener);
		radioYouTube.setValue(false);
		this.add(radioCASupport);
		radioGroup.add(radioCASupport);
		
		radioGroup.setValue(radioYouTube);
		
		saveSetting = new CheckBox();
		saveSetting.ensureDebugId("befeaa86-f66a-463e-a1c7-6f81f4058c2d");
		saveSetting.setBoxLabel(UIContext.Constants.neverShowThisDialog());
		this.add(saveSetting);
		
		okButton = new Button();
		okButton.ensureDebugId("c17bea9f-849a-4f9b-ace0-d7a2126bb6cb");
		okButton.setText(UIContext.Constants.ok());
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
				@Override
				public void componentSelected(ButtonEvent ce) {
					okButton.setEnabled(false);
					
					boolean useYouTube = true;
					if (radioCASupport.getValue()) {
						useYouTube = false;
					}
					
					if (saveSetting.getValue() != null && saveSetting.getValue())
					{					
						service.setYouTubeVideoSource(useYouTube, new BaseAsyncCallback<Void>(){
							@Override
							public void onFailure(Throwable caught) {
								super.onFailure(caught);
								okButton.setEnabled(true);						
							}
	
							@Override
							public void onSuccess(Void result) {
								
							}
						});
					}
					
					if (radioCASupport.getValue())
						com.google.gwt.user.client.Window.open(supportURL, "_BLANK", "");								
					else
						com.google.gwt.user.client.Window.open(youtubeURL, "_BLANK", "");
					
					window.hide();
					
				}
			});
		
		this.addButton(okButton);
	}
	
	@Override
	protected void onRender(Element target, int index) {
		  super.onRender(target, index);
		  this.setFocusWidget(radioYouTube);
		  radioYouTube.focus();
		  
		  
	  }	
	
	@Override
	protected void afterShow() {
		super.afterShow();
		radioYouTube.setValue(true);		  
		  radioGroup.setValue(radioYouTube);
	}
	
	class VideoSourceKeyListener extends KeyListener{

		@Override
		public void componentKeyPress(ComponentEvent event) {
			if (event.getKeyCode() == KeyCodes.KEY_ENTER)
				okButton.fireEvent(Events.Select);
		}		
	}
}
