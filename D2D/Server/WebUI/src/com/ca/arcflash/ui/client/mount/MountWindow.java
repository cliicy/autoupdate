package com.ca.arcflash.ui.client.mount;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.HelpTopics;
import com.ca.arcflash.ui.client.common.Utils;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.DisclosurePanel;

public class MountWindow extends Window implements IMountAsyncCallback{
	private final int WINDOW_HEIGHT = 700;
	private final int WINDOW_WIDTH = 720;
	private MountedVolumeListContainer mountedListContainer;
	private MountVolumeContainer mountVolumeContainer;
	private WindowsMaskAdapter maskAdapter;
	private Button refreshBtn;
	
	public MountWindow(){
		this.maskAdapter = new WindowsMaskAdapter(this);
		this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		
		this.setLayout( new FitLayout() );
		this.setResizable(false);		
		this.setHeadingHtml(UIContext.Constants.mountPanelDesciption());
		
		LayoutContainer container = new LayoutContainer();
		container.setScrollMode(Scroll.AUTOY);
		TableLayout tl = new TableLayout(1);
		tl.setWidth("95%");
//		tl.setHeight("98%");
		container.setLayout(new RowLayout());
		container.setStyleAttribute("background-color","#FFFFFF");
		
		container.add(getHeaderContainer());

		mountedListContainer = new MountedVolumeListContainer(this);
		DisclosurePanel mountedVolsPanel = Utils.getDisclosurePanel(UIContext.Constants.mountPanelMountedVols());
		mountedVolsPanel.add(mountedListContainer);
		container.add(mountedVolsPanel);
		
		mountVolumeContainer = new MountVolumeContainer(this);
		DisclosurePanel selectedVolsPanel = Utils.getDisclosurePanel(UIContext.Constants.mountPanelSelectVols());	

		selectedVolsPanel.add(mountVolumeContainer);
		container.add(selectedVolsPanel);
		
		if(isVsphereVM()){
			mountedVolsPanel.addStyleName("mount_DisclosurePanel");
			selectedVolsPanel.addStyleName("mount_DisclosurePanel");
		}
		
		this.add(container);
		
		this.addButtons();
	}

	private LayoutContainer getHeaderContainer(){
		LayoutContainer headerContainer = new LayoutContainer();
		headerContainer.setStyleAttribute("padding-left", "10px");
		headerContainer.setLayout(new TableLayout(1));
		
		LayoutContainer container = new LayoutContainer();
		container.setLayout(new TableLayout(2));
		container.add(AbstractImagePrototype.create(UIContext.IconBundle.task_mount_volume()).createImage());
		LabelField label = new LabelField(UIContext.Constants.mountPanelDesciption());
		label.addStyleName("restoreWizardTitle");
		container.add(label);
		
		if(isVsphereVM()){
			headerContainer.add(container);
			
			LayoutContainer warningContainer = new LayoutContainer();
			warningContainer.setLayout(new TableLayout(2));
			warningContainer.add(AbstractImagePrototype.create(UIContext.IconBundle.status_small_warning()).createImage());
			label = new LabelField(UIContext.Constants.mountPanelVMNotice());
			label.addStyleName("mount_panel_vm_notice");
			warningContainer.add(label);
			headerContainer.add(warningContainer);
		}else {
			headerContainer.add(container);
		}
		
		return headerContainer;
	}
	
	private void addButtons() {
		refreshBtn = new Button(UIContext.Constants.refreshButtonTitle());
		refreshBtn.setMinWidth(80);
		refreshBtn.setAutoWidth(true);
		refreshBtn.ensureDebugId("83112E44-8D29-4304-B4FA-C589E0C0A2AA");
		refreshBtn.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				refreshUI();
			}
		});
		Utils.addToolTip(refreshBtn, UIContext.Constants.refreshButtonTooltip());
		//refreshBtn.setStyleAttribute("margin-right", "10px");
		refreshBtn.setEnabled(false);
		
		ButtonBar buttonBar = this.getButtonBar();
		buttonBar.setAlignment(HorizontalAlignment.LEFT);
		buttonBar.add(refreshBtn);
		buttonBar.add(new FillToolItem());
		
		Button okButton = new Button();
		okButton.ensureDebugId("9533A551-07A4-477f-96CF-E2103694BB2A");
		okButton.setText(UIContext.Constants.close());
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
			@Override
			public void componentSelected(ButtonEvent ce) {
				hide();
			}
		});
		buttonBar.add(okButton);
		
		if(isVsphereVM()){
			Button helpButton = HelpTopics.createHelpButton(UIContext.externalLinks.getVSphereMountVolumeHelp(), -1);
			helpButton.ensureDebugId("295367A0-4D35-404a-99C7-3C87D89124C7");
			this.addButton(helpButton);
		}
		else{
			Button helpButton = HelpTopics.createHelpButton(UIContext.externalLinks.getMountVolumeHelp(), -1);
			helpButton.ensureDebugId("295367A0-4D35-404a-99C7-3C87D89124C7");
			buttonBar.add(helpButton);
		}
	}
	
	private boolean isVsphereVM(){
		return UIContext.uiType == 1;
	}
	
	public void maskWindow(){
		maskAdapter.maskWindow(UIContext.Constants.disMountMarkText());
	}
	public void unMaskWindow(){
		maskAdapter.unmaskWindow();
	}
	
	public void refreshUI(){
		refreshBtn.setEnabled(false);
		maskAdapter.cleanRefreshStatus();
		mountedListContainer.getAllMountedRecoveryPointItems();
		mountVolumeContainer.getMountedRecoveryPointItem();
	}
	
	public WindowsMaskAdapter getMaskAdapter(){
		return maskAdapter;
	}
	
	public void updateRefreshStatus(String key, Boolean value){
		maskAdapter.updateRefreshStatus(key, value);
	}
	
	@Override
	public void loadComplete() {
		refreshBtn.setEnabled(true);
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();
		resetSize();
	}

	@Override
	protected void onWindowResize(int width, int height) {
		super.onWindowResize(width, height);
		resetSize();
	}
	
	private void resetSize(){
		if(this.getHeight() > Utils.getScreenHeight() * 0.75) {
			this.setHeight((int)(Utils.getScreenHeight() * 0.75));
		}
	}
}
