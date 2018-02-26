package com.ca.arcflash.ui.client.homepage;

import java.util.Date;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.AppType;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.CommonSettingWindow;
import com.ca.arcflash.ui.client.model.TrustHostModel;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.state.CookieProvider;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.layout.ColumnData;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class GettingStartedWindow extends Window {
	private final CommonServiceAsync service = GWT.create(CommonService.class);
	private final HomepageServiceAsync homepageservice = GWT.create(HomepageService.class);
	private GettingStartedWindow window;	
	private LayoutContainer panel;
	private final CheckBox neverShowCheckbox;	
	private final Label gettingStartedLabel; 
	private final Image infoImg;
	public final int MIN_WIDTH = 90;
	
	public GettingStartedWindow()
	{
		this.setResizable(false);
		this.window = this;
		this.setAutoHeight(true);
		this.setHeadingHtml(UIContext.Messages.gettingStartedTitle(UIContext.productNameD2D));
		this.setClosable(true);		
		//this.setSize(750, 350);
		this.setSize(800, 365);
		
		LayoutContainer container = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setCellVerticalAlign(VerticalAlignment.MIDDLE);
		tl.setColumns(2);
		tl.setHeight("98%");
		container.setLayout(tl);		
		
		//LEFT SIDE
		AbstractImagePrototype icon1 = IconHelper.create("images/5.0/gettingstarted.png", 300, 300);
		container.add(icon1.createImage());		
		
		//RIGHT SIDE
		panel = new LayoutContainer();
		TableLayout panelLayout = new TableLayout();
		
		//panelLayout.setCellPadding(7); 	
		panelLayout.setHeight("100%");
		panelLayout.setCellPadding(4);
		
		panel.setLayout(panelLayout);
		panel.setAutoHeight(true);
		panel.setStyleAttribute("margin", "0px, 0px, 0px, 8px");
		
	    //Logo
		icon1 = IconHelper.create("images/5.0/banner.png", 300, 40);
	    panel.add(icon1.createImage());
	    Label subtitle = new Label(UIContext.Messages.gettingStartedTitle(UIContext.productNameD2D));
	    subtitle.setStyleName("getting_started_subtitle");
	    panel.add(subtitle);
	    
	    ClickHandler backupSettingHandler = new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				//BackupSettingsWindow window = new BackupSettingsWindow();
				CommonSettingWindow window = new CommonSettingWindow(AppType.D2D);
				window.setSize(880, 600);
				window.setModal(true);
				window.show();
			}
	    };
	    ClickHandler videoSettingHandler = new ClickHandler()
	    {
			@Override
			public void onClick(ClickEvent event) {
				
				service.isYouTubeVideoSource(new BaseAsyncCallback<Boolean>(){
					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);						
						//Show the selection dialog
						VideoSourceWindow dlg = new VideoSourceWindow(UIContext.externalLinks.getVideoURL(), 
								UIContext.externalLinks.getVideoCASupportURL());
						dlg.setModal(true);
						dlg.show();
					}

					@Override
					public void onSuccess(Boolean result) {
						if (result == null)
						{
							//Show the selection dialog
							VideoSourceWindow dlg = new VideoSourceWindow(UIContext.externalLinks.getVideoURL(), 
									UIContext.externalLinks.getVideoCASupportURL());
							dlg.setModal(true);
							dlg.show();
						}
						
						//Show the YouTube or the CA Support link
						else if (result)
						{
							com.google.gwt.user.client.Window.open(UIContext.externalLinks.getVideoURL(), "_BLANK", "");
						}
						else
						{
							com.google.gwt.user.client.Window.open(UIContext.externalLinks.getVideoCASupportURL(), "_BLANK", "");
						}
					}
				});
				
				
			}	    	
	    };
	    ClickHandler productDocumentationHandler = new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				com.google.gwt.user.client.Window.open(
						UIContext.externalLinks.getHomePagePanelHelp(),"_BLANK", "");				
			}
	    };
	   	
	    if(UIContext.customizedModel.getShowSettings() == null || UIContext.customizedModel.getShowSettings())
	    	addTask(UIContext.Constants.homepageTasksBackupSettingLabel(),UIContext.Constants.gettingStartedBackupLabelDescription(), UIContext.Constants.gettingStartedBackupSettingDesc(),
	    			backupSettingHandler,AbstractImagePrototype.create(UIContext.IconBundle.tasks_backupSetting2()), "30%", "f8400123-9d8d-4f8a-ba53-8031b143273d");
	    if(UIContext.customizedModel.getShowVedios() == null || UIContext.customizedModel.getShowVedios()){
		    String locale = UIContext.serverVersionInfo.getLocale();
		    if(locale!=null && !locale.trim().equalsIgnoreCase("en")) {
		    	addVideoSection(UIContext.externalLinks.getHomepageSupportVideoLabelOnlyEn(), UIContext.externalLinks.getHomepageSupportVideoDescription(), videoSettingHandler,  AbstractImagePrototype.create(UIContext.IconBundle.video()));
		    } else {
		    	addVideoSection(UIContext.externalLinks.getHomepageSupportVideoLabel(), UIContext.externalLinks.getHomepageSupportVideoDescription(), videoSettingHandler,  AbstractImagePrototype.create(UIContext.IconBundle.video()));
		    }
	    }
	    addTask(UIContext.Constants.productDocumentation(), UIContext.externalLinks.getHomepageSupportD2DOnlineHelp(), null, 
	    		productDocumentationHandler,AbstractImagePrototype.create(UIContext.IconBundle.prod_document()), "38%", "af23436a-bedb-44b8-b3d7-4ccbbe338509");	    
	    	    
	    LayoutContainer lc = new LayoutContainer();
	    lc.setLayout(new ColumnLayout());
	    
	    infoImg = AbstractImagePrototype.create(UIContext.IconBundle.logMsg()).createImage();
	    infoImg.ensureDebugId("00e2221b-f528-4c28-ad0f-dc7d71128f7a");
	    infoImg.setStyleName("getting_started_remoteURL");
	    infoImg.setVisible(false);
		lc.add(infoImg);
		
		gettingStartedLabel = new Label("");
		gettingStartedLabel.setStyleName("getting_started_remoteURL");
		
		ColumnData cd = new ColumnData();
		cd.setWidth(420);				
		lc.add(gettingStartedLabel, cd);
		
		panel.add(lc);
	    
	    
	    neverShowCheckbox = new CheckBox();
	    neverShowCheckbox.ensureDebugId("cea7e3d9-6282-4baa-bdb9-ade9beea415f");
		neverShowCheckbox.setBoxLabel(UIContext.Constants.neverShowThisDialog());
		
		
		TableData td = new TableData();
		td.setVerticalAlign(VerticalAlignment.BOTTOM);
		td.setMargin(5);
		panel.add(neverShowCheckbox, td);
	    
	    td = new TableData();
	    td.setVerticalAlign(VerticalAlignment.TOP);
	    container.add(panel, td);
	    
	    Button closeButton = new Button();
	    closeButton.ensureDebugId("ec890fa4-b566-446f-9bf3-2f5c7aa57779");
	    closeButton.setText(UIContext.Constants.close());
	    closeButton.setMinWidth(MIN_WIDTH);
	    closeButton.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {				
				if (neverShowCheckbox.getValue())
				{					
					CookieProvider provider = new CookieProvider(null, 
							new Date(8099, 11, 31), null, false);
					provider.set("donotshowgettingstarted", Boolean.TRUE);				
				}
				window.hide();				
			}});		
		this.addButton(closeButton);
		this.add(container);
	    
		
		
	    this.addWindowListener( new WindowListener(){
			public void	windowHide(WindowEvent we)
			{
				if (neverShowCheckbox.getValue())
				{					
					CookieProvider provider = new CookieProvider(null, 
							new Date(8099, 11, 31), null, false);
					provider.set("donotshowgettingstarted", Boolean.TRUE);				
				}
			}
		});
	}
	
	private void addVideoSection(String label, String description, ClickHandler handler, AbstractImagePrototype image) {
		
		TableLayout layout = new TableLayout();
		layout.setColumns(2);
		layout.setWidth("100%");
		
		LayoutContainer container = new LayoutContainer();
		container.setLayout(layout);
		
		TableData tableData = null;
		tableData = new TableData();		
		tableData.setWidth("36");
		tableData.setRowspan(3);		
		tableData.setVerticalAlign(VerticalAlignment.TOP);
		
		Image icon = image.createImage();
		icon.ensureDebugId("dc6f1901-d818-4878-9dd2-f554ef209bc7");
		icon.setTitle(description);
		icon.setStyleName("homepage_task_icon");
		icon.addClickHandler(handler);
		container.add(icon,tableData);
		
		tableData = new TableData();
		tableData.setVerticalAlign(VerticalAlignment.MIDDLE);
		tableData.setHorizontalAlign(HorizontalAlignment.LEFT);
		Label text = new Label(label);
		text.ensureDebugId("2fecb261-6dd0-4700-8e95-925b5119fd98");
		if (description != null)
		{
			text.setTitle(description);
		}
		text.setStyleName("homepage_task_label");
		text.addClickHandler(handler);
		container.add(wrapperContainer(text, 25), tableData);
		
		
		tableData = new TableData();
		tableData.setVerticalAlign(VerticalAlignment.TOP);
		tableData.setHorizontalAlign(HorizontalAlignment.LEFT);
		Label video = new Label();
		video.setText(UIContext.externalLinks.getIntroVideoName());
		video.setStyleName("getting_started_links");
		video.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				service.isYouTubeVideoSource(new BaseAsyncCallback<Boolean>(){
					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);						
						//Show the selection dialog
						VideoSourceWindow dlg = new VideoSourceWindow(UIContext.externalLinks.getIntroVideoURL(),
								UIContext.externalLinks.getCASupportIntroVideoURL());
						dlg.setModal(true);
						dlg.show();
					}

					@Override
					public void onSuccess(Boolean result) {
						if (result == null)
						{
							//Show the selection dialog
							VideoSourceWindow dlg = new VideoSourceWindow(UIContext.externalLinks.getIntroVideoURL(),
									UIContext.externalLinks.getCASupportIntroVideoURL());
							dlg.setModal(true);
							dlg.show();
						}
						
						//Show the YouTube or the CA Support link
						else if (result)
						{
							com.google.gwt.user.client.Window.open(UIContext.externalLinks.getIntroVideoURL(), "_BLANK", "");
						}
						else
						{
							com.google.gwt.user.client.Window.open(UIContext.externalLinks.getCASupportIntroVideoURL(), "_BLANK", "");
						}
					}
				});				
			}
		});
		
		container.add(wrapperContainer(video, 40), tableData);
		
		tableData = new TableData();
		tableData.setVerticalAlign(VerticalAlignment.TOP);
		tableData.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		//remove the secondVideoName=What's New in this Release, oolong has no plan to do such video.
//		video = new Label();
//		video.ensureDebugId("be7c4cc9-ed7f-4d95-8dd2-6474306131a6");
//		video.setText(UIContext.externalLinks.getSecondVideoName());
//		video.setStyleName("getting_started_links");
//		video.addClickHandler(new ClickHandler(){
//			@Override
//			public void onClick(ClickEvent event) {
//				
//				service.isYouTubeVideoSource(new BaseAsyncCallback<Boolean>(){
//					@Override
//					public void onFailure(Throwable caught) {
//						super.onFailure(caught);						
//						//Show the selection dialog
//						VideoSourceWindow dlg = new VideoSourceWindow(UIContext.externalLinks.getSecondVideoURL(),
//								UIContext.externalLinks.getCASupportSecondVideoURL());
//						dlg.setModal(true);
//						dlg.show();
//					}
//
//					@Override
//					public void onSuccess(Boolean result) {
//						if (result == null)
//						{
//							//Show the selection dialog
//							VideoSourceWindow dlg = new VideoSourceWindow(UIContext.externalLinks.getSecondVideoURL(),
//									UIContext.externalLinks.getCASupportSecondVideoURL());
//							dlg.setModal(true);
//							dlg.show();
//						}
//						
//						//Show the YouTube or the CA Support link
//						else if (result)
//						{
//							com.google.gwt.user.client.Window.open(UIContext.externalLinks.getSecondVideoURL(), "_BLANK", "");
//						}
//						else
//						{
//							com.google.gwt.user.client.Window.open(UIContext.externalLinks.getCASupportSecondVideoURL(), "_BLANK", "");
//						}
//					}
//				});					
//			}
//		});
//		container.add(wrapperContainer(video, 45), tableData);
		
		panel.add(container);
	}

	private Widget wrapperContainer(Label video, int controlWidth) {
		LayoutContainer wrapper = new LayoutContainer();
		TableLayout tlayout = new TableLayout();
		tlayout.setColumns(2);
		
		wrapper.setLayout(tlayout);
		TableData data = new TableData();
		data.setWidth(controlWidth + "%");
		wrapper.add(video);
		
		data = new TableData();
		data.setWidth((100 - controlWidth) + "%");
		Label label = new Label(" ");
		label.setStyleName("gettingStartVideo");
		wrapper.add(label);
		return wrapper;
	}

	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);	
		refreshHost();
	}
	
	
	
	private void refreshHost() {
		homepageservice.getTrustHosts(new BaseAsyncCallback<TrustHostModel[]>() {
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);				
			}

			@Override
			public void onSuccess(TrustHostModel[] result) {
				TrustHostModel selectedHost = null;
				for (TrustHostModel model : result)
				{
					if (model.isSelected()!=null && model.isSelected())
					{
						selectedHost = model;
					}
				}
				if (selectedHost != null)
				{
					StringBuilder url = new StringBuilder();
					url.append(selectedHost.getProtocol());
					url.append("//");
					url.append(selectedHost.getHostName());
					url.append(":");
					url.append(selectedHost.getPort());					
					
					gettingStartedLabel.setText(UIContext.Messages.gettingStartedRemoteURL(UIContext.productNameD2D, url.toString()));
					infoImg.setVisible(true);
				}
				else
				{	
					//Need to refresh again, IE 6 seems to fail if called to early
					window.refreshHost();
				}
			}
		});
	    
	}

	private void addTask(String label, String tip, String desc, ClickHandler handler, AbstractImagePrototype image, String emptyWidth, String debugID){
		TableLayout layout = new TableLayout();
		layout.setColumns(3);
		//layout.setCellPadding(2);
		layout.setWidth("100%");
		
		LayoutContainer container = new LayoutContainer();
		container.setLayout(layout);
		
		TableData tableData = null;
		tableData = new TableData();		
		tableData.setWidth("5");
		if (desc != null && desc.trim().length() > 0)
		{
			tableData.setRowspan(2);
		}
		tableData.setVerticalAlign(VerticalAlignment.TOP);
		
		Image icon = image.createImage();
		if (tip != null && tip.trim().length() > 0){
			icon.setTitle(tip);
		}
		icon.setStyleName("homepage_task_icon");
		icon.addClickHandler(handler);
		container.add(icon,tableData);
		
		tableData = new TableData();
		tableData.setVerticalAlign(VerticalAlignment.MIDDLE);
		tableData.setHorizontalAlign(HorizontalAlignment.LEFT);
		Label text = new Label(label);
		text.ensureDebugId(debugID);
		text.setStyleName("homepage_task_label");
		text.getElement().getStyle().setPaddingLeft(5, Unit.PX);
		if (tip != null && tip.trim().length() > 0)
		{
			text.setTitle(tip);
		}
		text.addClickHandler(handler);
		container.add(text, tableData);
		
		tableData = new TableData();		
		tableData.setWidth(emptyWidth);
		container.add(new Label("  "), tableData); 
		
		if (desc != null && desc.trim().length() > 0)
		{		
			tableData = new TableData();
			tableData.setColspan(2);
			tableData.setVerticalAlign(VerticalAlignment.TOP);
			tableData.setHorizontalAlign(HorizontalAlignment.LEFT);
			Label descriptionText = new Label(desc);
			descriptionText.setStyleName("homepage_task_description");
			descriptionText.getElement().getStyle().setPaddingLeft(5, Unit.PX);
			container.add(descriptionText, tableData);
		}	
						
		panel.add(container);
	}
}
