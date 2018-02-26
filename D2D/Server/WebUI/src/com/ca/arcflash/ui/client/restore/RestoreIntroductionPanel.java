package com.ca.arcflash.ui.client.restore;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyService;
import com.ca.arcflash.ui.client.coldstandby.ColdStandbyServiceAsync;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.common.icons.FlashImageBundle;
import com.ca.arcflash.ui.client.model.CustomizationModel;
import com.extjs.gxt.ui.client.Style.ButtonScale;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.Template;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Label;

public class RestoreIntroductionPanel extends LayoutContainer implements RestoreValidator {
	public static final int BUTTON_WIDTH = 550;
	public static final int BUTTON_HEIGHT = 100;
	public static final int BUTTON_OUTER_HEIGHT = 80;
	public static final int BUTTON_INNER_HEIGHT = 70;
	public Button browseButton;
	public Button browseArchiveButton;
	public Button searchButton;
	public Button vmRecoverButton;
    public Button browseExchangeGRTButton;
		
	private RestoreWizardContainer wizard;      ///D2D Lite Integration
	
	public static final FlashImageBundle ICONBUNDLE = GWT.create(FlashImageBundle.class);
	
	public RestoreIntroductionPanel(RestoreWizardContainer w)       ///D2D Lite Integration
	{
		this.wizard = w;	
	}
	
	class CustomRestoreButton extends Button{
		
		public CustomRestoreButton(){
			super();
			setWidth(BUTTON_WIDTH);
			setHeight(BUTTON_HEIGHT);
			setScale(ButtonScale.LARGE);
		}
		
		public CustomRestoreButton(int width, String buttonSelector, String template){
			super();
			setWidth(width);
			setHeight(BUTTON_HEIGHT);
			setScale(ButtonScale.LARGE);
			this.buttonSelector = buttonSelector;
			this.template = new Template(template);
		}
		
		@Override
		protected void onResize(int width, int height) {
			super.onResize(width, height);
			el().setSize(width, BUTTON_OUTER_HEIGHT);
			buttonEl.setSize(width, BUTTON_INNER_HEIGHT);
		}
		
	}
	
	
	private static String generateButtonContent(String text,String description){
		StringBuilder sb = new StringBuilder();
		sb.append("<div style=\"padding-left: 8px; font-size: 14px; text-align: left; width: 520px;\"><B>");
		sb.append(text);
		sb.append("</B></div><table style=\"text-align: left; width: 520px; word-wrap: break-word;white-space: normal;\"><tr><td><p style=\"padding-left: 8px;font-size: 11.5px;\">");
		sb.append(description);
		sb.append("</p></td></tr></table>");
		return sb.toString();
	}
	
	private static final ColdStandbyServiceAsync coldStandByService = GWT.create(ColdStandbyService.class);
	
	private static String generateDisabledButtonTemplate() {
		StringBuffer sb = new StringBuffer();
		
        sb.append("<table cellspacing=\"0\" role=\"presentation\"><tbody class=\"{2}\" >");
        sb.append("<tr><td class=\"{4}-tl\"><i>&#160;</i></td><td class=\"{4}-tc\"></td><td class=\"{4}-tr\"><i>&#160;</i></td></tr>");
        sb.append("<tr><td class=\"{4}-ml\"><i>&#160;</i></td><td class=\"{4}-mc\"><em class=\"{3}\" unselectable=\"on\"><div class=\"{4}-text\" type=\"{1}\" style='font:bold 12px Arial,helvetica,Verdana,sans-serif; margin:0px 14px; padding-bottom:3px; cursor:auto; width:516px; position: static'><div style=\"margin-left:-6px; padding-top:1px; display:table; height:" + BUTTON_INNER_HEIGHT + "px;\"><div style=\"vertical-align:middle; display:table-cell;\">{0}</div></div></div></em></td><td class=\"{4}-mr\"><i>&#160;</i></td></tr>");
        sb.append("<tr><td class=\"{4}-bl\"><i>&#160;</i></td><td class=\"{4}-bc\"></td><td class=\"{4}-br\"><i>&#160;</i></td></tr>");
        sb.append("</tbody></table>");

        return sb.toString();
	}
	
	@Override  
	protected void onRender(Element parent, int index) {  
		super.onRender(parent, index);  
		this.setScrollMode(Scroll.AUTOY);
		//setStyleAttribute("margin", "10px");
		
		//TODO: Change this.  Its done this way because the button's alignment of text is always centered
		//we need to figure out a work around
		
		CustomizationModel customizedModel = UIContext.customizedModel;
		Boolean isFileCopyEnabled = customizedModel.get("FileCopy");

		browseButton = new CustomRestoreButton();
		browseButton.ensureDebugId("F1643291-5F2C-4f6c-A709-CB14FA65C9EA");
		browseButton.setHtml(generateButtonContent(UIContext.Constants.restoreBrowseButton(),UIContext.Constants.restoreBrowseDescription()));		
		browseButton.setIcon(AbstractImagePrototype.create(ICONBUNDLE.restore_browse()));
		browseButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				wizard.restoreType = RestoreWizardContainer.RESTORE_BY_BROWSE; // /D2D Lite Integration
				wizard.SetPage(RestoreWizardContainer.PAGE_RECOVERY);
				wizard.setButtonsVisible(true);
				wizard.setButtonsEnable(true);
				wizard.nextButton.setEnabled(wizard.getRecoveryPointsPanel()
						.getIsNextButtonEnable());// add for issue 121671.
			}
		});
		
		if(isFileCopyEnabled)
		{

			//Archive Browse Button
			browseArchiveButton = new CustomRestoreButton();	
			browseArchiveButton.ensureDebugId("ED6F59F7-2D29-46a8-9FD7-10B64E0C5D90");
			browseArchiveButton.setHtml(generateButtonContent(UIContext.Constants.restoreBrowseArchiveButton(),UIContext.Constants.restoreBrowseArchivesDescription()));		
			browseArchiveButton.setIcon(AbstractImagePrototype.create(ICONBUNDLE.restore_destination()));
			browseArchiveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
				
						@Override
						public void componentSelected(ButtonEvent ce) {
							wizard.restoreType = RestoreWizardContainer.RESTORE_BY_BROWSE_ARCHIVE; // /D2D Lite Integration
							wizard.SetPage(RestoreWizardContainer.PAGE_ARCHIVE_RECOVERY);
							wizard.setButtonsVisible(true);
							wizard.setButtonsEnable(true);
						}
					});

		}
		
		searchButton = new CustomRestoreButton();
		searchButton.ensureDebugId("B61124B3-1750-4551-B384-77A3D7D9399B");
		searchButton.setIcon(AbstractImagePrototype.create(ICONBUNDLE.restore_search()));
		searchButton.setHtml(generateButtonContent(UIContext.Constants.restoreSearchButton(),UIContext.Constants.restoreSearchDescription()));
		searchButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				wizard.restoreType = RestoreWizardContainer.RESTORE_BY_SEARCH; // D2D Lite Integration
				wizard.SetPage(RestoreWizardContainer.PAGE_SEARCH);
				wizard.setButtonsVisible(true);
				wizard.setButtonsEnable(true);
				if(wizard.restoreSearchPanel!=null)
					wizard.restoreSearchPanel.refreshSearchContainer();

			}
		});
		
		vmRecoverButton = new CustomRestoreButton();
		vmRecoverButton.ensureDebugId("21021D49-9D46-4bf2-9DED-FD5D92B8A5DA");
		vmRecoverButton.setIcon(AbstractImagePrototype.create(ICONBUNDLE.restore_recover()));
		vmRecoverButton.setHtml(generateButtonContent(UIContext.Constants.vmRecoverButton(),UIContext.Constants.vmRecoverDescription()));
		vmRecoverButton.addSelectionListener(new SelectionListener<ButtonEvent>(){

			@Override
			public void componentSelected(ButtonEvent ce) {
				wizard.restoreType = RestoreWizardContainer.RECOVER_VM;
				wizard.SetPage(RestoreWizardContainer.PAGE_VM_RECOVERY);
				wizard.setButtonsVisible(true);
				wizard.setButtonsEnable(true);
				

		}});
		
		coldStandByService.isHostAMD64Platform(new BaseAsyncCallback<Boolean>()
		{
			@Override
			public void onFailure(Throwable caught)
			{
				// nothing to do
			}

			@Override
			public void onSuccess(Boolean result)
			{
			    if (!result)
				{
					vmRecoverButton.setHtml(generateButtonContent(UIContext.Constants.vmRecoverButton() + " " +
									        UIContext.Constants.notSupport32BitProxy(),
									        UIContext.Constants.vmRecoverDescription()));
					vmRecoverButton.disable();
				}
			}
		});

		
		// Browse Exchange GRT Button
		if (UIContext.isExchangeGRTFuncEnabled) {
			browseExchangeGRTButton = new CustomRestoreButton() {
				private String linkStyle = "color: #F7E41B !important; "
						+ "text-decoration: underline;"
						+ "position: absolute; "
						+ "margin-left: -526px; "
						+ "margin-top: 50px; "
						+ "font: bold 12px Arial,helvetica,Verdana,sans-serif;";
				{														
					String linkHtml = createLinkHtml(UIContext.externalLinks.getExchangeGranularRestoreUtility(), 
							UIContext.Constants.restoreBrowseExchangeGRTButtonHelpLinkText(), 
							linkStyle);
					template = new Template(generateLinkOnButtonTemplate(linkHtml));
				}
				
				@Override
				public void setHtml(String html) {
					html += "<br />";
					super.setHtml(html);
				}
				
				@Override
				protected void onMouseOver(ComponentEvent ce) {
					if (doesLinkGetFocused()) {
						return;
					}
					super.onMouseOver(ce);
				}
				
				@Override
				protected void onClick(ComponentEvent ce) {
					if (doesLinkGetFocused()) {
						return;
					}
					super.onClick(ce);
				}
				
				@Override
				protected void onMouseDown(ComponentEvent ce) {
					if (doesLinkGetFocused()) {
						return;
					}
					super.onMouseDown(ce);
				}
				
				private boolean doesLinkGetFocused() {
					El link = this.el().selectNode("a");
					if (null != link && "yes".equals(link.dom.getAttribute("isfocused"))) {
						return true;
					}
					return false;
				}
			};			
			browseExchangeGRTButton.ensureDebugId("758A01BB-C2A2-4357-84E3-EFB90DA990FD");
			browseExchangeGRTButton.setHtml(generateButtonContent(UIContext.Constants.restoreBrowseExchangeGRTButton(),UIContext.Constants.restoreBrowseExchangeGRTDescription()));
			browseExchangeGRTButton.setIcon(AbstractImagePrototype.create(ICONBUNDLE.restore_browse_exchange_grt()));
			
			browseExchangeGRTButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
			
						@Override
						public void componentSelected(ButtonEvent ce) {
							wizard.restoreType = RestoreWizardContainer.RESTORE_BY_BROWSE_EXCHANGE_GRT;
							// set the button status before going to next page.
							//otherwise, it might overwrite the status set by the
							// next page
							wizard.setButtonsVisible(true);
							wizard.nextButton.setEnabled(true);
							wizard.prevButton.setEnabled(true);
							wizard.SetPage(RestoreWizardContainer.PAGE_EXCHANGE_GRT_RECOVERY);
						}
				}
			);		
		} else {
			browseExchangeGRTButton = new CustomRestoreButton(516, "div", generateDisabledButtonTemplate());
			browseExchangeGRTButton.ensureDebugId("758A01BB-C2A2-4357-84E3-EFB90DA990FD");		
			browseExchangeGRTButton.setHtml(generateButtonContent(UIContext.Constants.restoreBrowseExchangeDataButton(), createMessageWithLink()));
			browseExchangeGRTButton.setIcon(AbstractImagePrototype.create(ICONBUNDLE.restore_browse_exchange_grt_utility()));
			browseExchangeGRTButton.addStyleName("catalogExchDisabled");	
		}		
		
		RowLayout rl = new RowLayout();
		this.setLayout(rl);
		
		Label label = new Label(UIContext.Constants.restoreIntroductionLabel());
		label.addStyleName("restoreWizardTitle");
		if(!isFileCopyEnabled)
		{
			this.setStyleAttribute("margin-top", "20px");
		}
		this.add(label);	
		
		RowData rd = new RowData();
		rd.setHeight(120);
		Margins m = new Margins();
		m.bottom = 5;
		m.left = 50;
		m.right = 10;
		m.top = 10;
		rd.setMargins(m);		
		this.add(browseButton, rd);
		
		rd = new RowData();
		rd.setHeight(BUTTON_OUTER_HEIGHT);
		m = new Margins();
		m.bottom = 5;
		m.left = 50;
		m.right = 10;
		m.top = 5;
		rd.setMargins(m);	
		
		if(isFileCopyEnabled)
		{
			this.add(browseArchiveButton, rd);
		}	
		
		this.add(searchButton, rd);
		
		this.add(vmRecoverButton, rd);

		this.add(browseExchangeGRTButton, rd);
		
		if(UIContext.uiType == Utils.UI_TYPE_D2D) {
			this.add(createADGRTButton(), rd);
		}

		if(UIContext.uiType == Utils.UI_TYPE_VSPHERE) {
			browseArchiveButton.hide();
		}
		
		this.setHeight(500);
	}
	
	private Button createADGRTButton(){
		// Browse Exchange GRT Button
		Button browseADGRTButton = new CustomRestoreButton();
		
		browseADGRTButton.ensureDebugId("ba7d7685-bc61-4ba5-981b-61cb88ab32e5");
		browseADGRTButton.setHtml(generateButtonContent(UIContext.Constants.restoreBrowseADButton(),UIContext.Constants.restoreBrowseADDescription()));
		browseADGRTButton.setIcon(AbstractImagePrototype.create(ICONBUNDLE.restore_browse_ad_grt()));
		
		browseADGRTButton.addSelectionListener(new SelectionListener<ButtonEvent>(){
		
			@Override
			public void componentSelected(ButtonEvent ce) {
				wizard.restoreType = RestoreWizardContainer.RECOVER_AD;
				
				// set the button status before going to next page. otherwise, it might overwrite the status set by the next page
				wizard.setButtonsVisible(true);
				wizard.nextButton.setEnabled(true);
				wizard.prevButton.setEnabled(true);
				
				wizard.SetPage(RestoreWizardContainer.PAGE_AD_RECOVERY);		
			}}
		);
		
		return browseADGRTButton;
	}

	@Override
	public boolean validate(AsyncCallback<Boolean> callback) {
		// TODO Auto-generated method stub
		return false;
	}	
	
	private String createMessageWithLink() {				
		String link = createLinkHtml(UIContext.externalLinks.getExchangeGranularRestoreUtility(), 
				UIContext.Constants.scheduleCatalogExchUtilityLink());
		String message = UIContext.Messages.restoreBrowseExchangeDataDescription(link);
		return message;
	}
	
	private String createLinkHtml(String url, String label) {
		return createLinkHtml(url, label, null);
	}
	
	private String createLinkHtml(String url, String label, String style) {
		StringBuilder sbLinkHtml = new StringBuilder();
		
		sbLinkHtml.append("<a class=\"catalogExchConfigInfoLinkStyle\" style=\"");
		sbLinkHtml.append(style);
		sbLinkHtml.append("\" onmouseover=\"this.setAttribute('isFocused', 'yes');\" onmouseout=\"this.removeAttribute('isFocused');\" onclick=\"window.open('");
		sbLinkHtml.append(url);
		sbLinkHtml.append("', '_blank');\">");
		sbLinkHtml.append(label);
		sbLinkHtml.append("</a>");
		
		return sbLinkHtml.toString();
	}
	
	private String generateLinkOnButtonTemplate(String linkHtml) {
		StringBuffer sb = new StringBuffer();
		
        sb.append("<table cellspacing=\"0\" role=\"presentation\"><tbody class=\"{2}\" >");
        sb.append("<tr><td class=\"{4}-tl\"><i>&#160;</i></td><td class=\"{4}-tc\"></td><td class=\"{4}-tr\"><i>&#160;</i></td></tr>");
        sb.append("<tr><td class=\"{4}-ml\"><i>&#160;</i></td><td class=\"{4}-mc\"><em class=\"{3}\" unselectable=\"on\"><button class=\"{4}-text\" type=\"{1}\" style='position: static'>{0}</button>");
        sb.append(linkHtml);
        sb.append("</em></td><td class=\"{4}-mr\"><i>&#160;</i></td></tr>");
        sb.append("<tr><td class=\"{4}-bl\"><i>&#160;</i></td><td class=\"{4}-bc\"></td><td class=\"{4}-br\"><i>&#160;</i></td></tr>");
        sb.append("</tbody></table>");
        
        return sb.toString();
	} 
}
