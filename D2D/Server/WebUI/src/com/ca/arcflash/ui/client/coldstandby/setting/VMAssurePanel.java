package com.ca.arcflash.ui.client.coldstandby.setting;


class VMAssurePanel {// extends WizardPage {
	
	/*@Override
	public void render(Element target, int index) {
		super.render(target, index);

		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.ensureDebugId("33493d2f-19fb-405f-8690-01a311e40e12");
		verticalPanel.setTableWidth("100%"); 
		verticalPanel.setWidth("100%"); 
		verticalPanel.setScrollMode(Scroll.AUTO);
		
		DisclosurePanel serverSettingPanel = setupSchedulePanel();
		DisclosurePanel verificationPanel = setupVerificationPanel();
		
		verticalPanel.add(serverSettingPanel);
		verticalPanel.add(verificationPanel);
		this.add(verticalPanel);
	}

	@SuppressWarnings("deprecation")
	private DisclosurePanel setupVerificationPanel() {
		DisclosurePanel serverSettingPanel = new DisclosurePanel(
				(DisclourePanelImageBundles)GWT.create(DisclourePanelImageBundles.class),
				UIContext.Constants.coldStandbySettingVMAssureVerificationSettings(), false); 
		serverSettingPanel.ensureDebugId("293ec40f-6b50-470f-ab37-82dc9d727a2f");
		serverSettingPanel.setWidth("100%"); 
		serverSettingPanel.setStylePrimaryName("gwt-DisclosurePanel-coldStandby"); 
		serverSettingPanel.setOpen(true);
		
		FlexTable verticalPanel = new FlexTable();
		verticalPanel.ensureDebugId("ef02b33f-74fc-4d9e-8741-0c8a924e63dd");
		verticalPanel.setCellPadding(0);
		verticalPanel.setCellSpacing(0);
		
		Label titleLabel = new Label();
		titleLabel.setStyleName("setting-text-label"); 
		titleLabel.setText(UIContext.Constants.coldStandbySettingVMAssureApplicationDescription()); 
		verticalPanel.setWidget(0, 0, titleLabel);
		
		verticalPanel.setWidget(1, 0, setupApplicationTable());
		
		CheckBox checkBox = new CheckBox();
		checkBox.ensureDebugId("1fead8b6-bf6a-4006-b34b-a65f3d17e95b");
		checkBox.setValue(true);
		checkBox.setStyleName("setting-text-label"); 
		checkBox.setText(UIContext.Constants.coldStandbySettingVMAssureCustomScript()); 
		verticalPanel.setWidget(2, 0, checkBox);
		
		TextField<String> textFieldScript = new TextField<String>();
		textFieldScript.setStyleAttribute("margin-top", "6px");  
		textFieldScript.setStyleAttribute("margin-left", "30px");  
		textFieldScript.setWidth(400);
		verticalPanel.setWidget(3, 0, textFieldScript);
		
		titleLabel = new Label();
		titleLabel.setStyleName("setting-text-label"); 
		titleLabel.setText(UIContext.Constants.coldStandbySettingVMAssureExitCode()); 
		
		TextField<String> textFieldExitCode = new TextField<String>();
		textFieldExitCode.setValue("0"); 
		textFieldExitCode.setWidth(50);
		
		SimpleComboBox<String> scriptCombo = new SimpleComboBox<String>();
		scriptCombo.add("Assurance Succeeds"); 
		scriptCombo.setEmptyText("Assurance Succeeds"); 
		
		HorizontalPanel scriptResultPanel = new HorizontalPanel();
		scriptResultPanel.getElement().getStyle().setMarginLeft(20, Unit.PX);
		scriptResultPanel.setSpacing(4);
		scriptResultPanel.add(titleLabel);
		scriptResultPanel.add(textFieldExitCode);
		scriptResultPanel.add(scriptCombo);
		verticalPanel.setWidget(4, 0, scriptResultPanel);
		
		serverSettingPanel.add(verticalPanel);
		return serverSettingPanel;
	}

	private Widget setupApplicationTable() {
		ContentPanel panel = new ContentPanel();
		panel.setStyleAttribute("margin-top", "6px");  
		panel.setStyleAttribute("margin-bottom", "6px");  
		panel.setStyleAttribute("margin-left", "10px");  
	    panel.setCollapsible(false);      
	    panel.setHeaderVisible(false);   
	    panel.setWidth(300);   
	    panel.setHeight(100);   

	    ListStore<FakeBean> store = new ListStore<FakeBean>(); 
	    store.add(new FakeBean("File System")); 
	    store.add(new FakeBean("Microsoft SQL Server")); 
	    store.add(new FakeBean("Microsoft Exchange")); 
	    
	    CheckBoxListView<FakeBean> view = new CheckBoxListView<FakeBean>();
	    view.setBorders(false);
	    view.setStore(store);   
	    view.setDisplayProperty("Name");  
	    
	    panel.add(view);
	    return panel;
	}

	@SuppressWarnings("deprecation")
	private DisclosurePanel setupSchedulePanel() {
		DisclosurePanel serverSettingPanel = new DisclosurePanel(
				(DisclourePanelImageBundles)GWT.create(DisclourePanelImageBundles.class),
				UIContext.Constants.coldStandbySettingVMAssureSchedule(), false); 
		serverSettingPanel.setWidth("100%"); 
		serverSettingPanel.setStylePrimaryName("gwt-DisclosurePanel-coldStandby"); 
		serverSettingPanel.setOpen(true);
		
		FlexTable verticalPanel = new FlexTable();
		verticalPanel.setCellPadding(0);
		verticalPanel.setCellSpacing(0);
		
		Label titleLabel = new Label();
		titleLabel.setStyleName("setting-text-label"); 
		titleLabel.setText(UIContext.Constants.coldStandbySettingVMAssureValidateSchedule()); 
		verticalPanel.setWidget(0, 0, titleLabel);
		
		RadioButton radioRepeat = new RadioButton("ScheduleType"); 
		radioRepeat.setValue(true);
		radioRepeat.setText(UIContext.Constants.coldStandbySettingVMAssureScheduleTypeRepeat()); 
		radioRepeat.setStyleName("panel-text-value"); 
		
		titleLabel = new Label();
		titleLabel.getElement().getStyle().setMarginLeft(20, Unit.PX);
		titleLabel.setStyleName("setting-text-label"); 
		titleLabel.setText(UIContext.Constants.coldStandbySettingVMAssureScheduleTypeEvery()); 
		
		TextField<String> textFieldRepeat = new TextField<String>();
		textFieldRepeat.setWidth(50);
		textFieldRepeat.setStyleAttribute("maring-right", "4px");  
		
		SimpleComboBox<String> repeatCombo = new SimpleComboBox<String>();
		repeatCombo.setStyleAttribute("margin-left", "4px");  
		repeatCombo.add(UIContext.Constants.scheduleLabelDays());
		repeatCombo.add(UIContext.Constants.scheduleLabelHours());
		repeatCombo.add(UIContext.Constants.scheduleLabelMinutes());
		repeatCombo.setEmptyText(UIContext.Constants.scheduleLabelDays());
		
		HorizontalPanel repeatSchedulePanel = new HorizontalPanel();
		repeatSchedulePanel.add(radioRepeat);
		repeatSchedulePanel.add(titleLabel);
		repeatSchedulePanel.add(textFieldRepeat);
		repeatSchedulePanel.add(repeatCombo);
		
		verticalPanel.setWidget(1, 0, radioRepeat);
		verticalPanel.setWidget(2, 0, repeatSchedulePanel);
		
		RadioButton radioEvery = new RadioButton("ScheduleType"); 
		radioEvery.setText(UIContext.Constants.coldStandbySettingVMAssureScheduleTypeEveryCopy()); 
		radioEvery.setStyleName("panel-text-value"); 
		verticalPanel.setWidget(3, 0, radioEvery);
		
		RadioButton radioNever = new RadioButton("ScheduleType"); 
		radioNever.setText(UIContext.Constants.coldStandbySettingVMAssureScheduleTypeNever()); 
		radioNever.setStyleName("panel-text-value"); 
		verticalPanel.setWidget(4, 0, radioNever);
		
		serverSettingPanel.add(verticalPanel);
		return serverSettingPanel;
	}
	
	private class FakeBean extends BeanModel{

		private static final long serialVersionUID = 9198824595364478094L;
		
		public FakeBean(String name){
			this.setName(name);
		}
		
		public String getName() {
			return (String)get("Name"); 
		}
		public void setName(String name) {
			set("Name", name);		 
		}
	}

	@Override
	public String getDescription() {
		return UIContext.Constants.coldStandbySettingVMAssureDescription();
	}

	@Override
	public String getTitle() {
		return UIContext.Constants.coldStandbySettingVMAssureTitle();
	}*/
}
