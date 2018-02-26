package com.ca.arcflash.ui.client.backup;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.AdvScheduleUtil;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.HasValidateValue;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.BackupSettingsModel;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.FlowData;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class CatalogPanel extends LayoutContainer implements HasValidateValue<BackupSettingsModel> {
	private CheckBox checkboxGenerateGRTCatalog;
	private CheckBox cbCatalog = new CheckBox();
	private FieldSet notificationSet;
	private LabelField hbbuCatalogCommentLabel;
	public static final long EXCHANGE_GRT_ENABLE_AFTER_BACKUP = 1L;	
	private LayoutContainer lcCatalogExch32BitWarn;
	public static final long EXCHANGE_GRT_ENABLE_BEFORE_RESTORE = 2L;
	private Widget grtUtilityInfoMessage;

	public CatalogPanel() {
		LayoutContainer GRTContainer = this;
		GRTContainer.setLayout(AdvScheduleUtil.createLineLayout());

		hbbuCatalogCommentLabel = new LabelField(UIContext.Constants.forWindowsVMOnlyLabel());
		if (UIContext.isExchangeGRTFuncEnabled) {
			checkboxGenerateGRTCatalog = new CheckBox();
			checkboxGenerateGRTCatalog.setVisible(true);
			checkboxGenerateGRTCatalog.setBoxLabel(UIContext.Constants.settingsLableGRTEnableAfterBackup());
			Utils.addToolTip(checkboxGenerateGRTCatalog, UIContext.Constants.backupSettingsGRTEnableTooltip());
			checkboxGenerateGRTCatalog.setStyleAttribute("white-space", "normal");			
			checkboxGenerateGRTCatalog.setEnabled(true);			
		}
		// -----end of Exchange GRT Settings ----------

		cbCatalog.setVisible(true);
		cbCatalog.setBoxLabel(UIContext.Constants.destinationCatalogLabel());
		Utils.addToolTip(cbCatalog, UIContext.Constants.destinationCatalogTooltip());
		cbCatalog.setStyleAttribute("white-space", "normal");
		cbCatalog.addListener(Events.Change, catalogCheckListener);
		if (UIContext.isExchangeGRTFuncEnabled) {
			GRTContainer.add(
			AdvScheduleUtil.createLayout(UIContext.Constants.destinationCatalogTitle(), new LabelField[] { hbbuCatalogCommentLabel }, new Widget[] {
					cbCatalog, checkboxGenerateGRTCatalog }), new FlowData(0,0,1,0));
			
			grtUtilityInfoMessage = createGRTUtilityInfoMessage(); 
			GRTContainer.add(grtUtilityInfoMessage, new FlowData(0, 0, 1, 0));
			
			lcCatalogExch32BitWarn = (LayoutContainer)createCatalogExch32BitWarningMessage();		
			lcCatalogExch32BitWarn.setStyleAttribute("white-space", "normal");
			lcCatalogExch32BitWarn.setStyleAttribute("margin-left", "10px");
			lcCatalogExch32BitWarn.setStyleAttribute("margin-bottom", "1px");		
			lcCatalogExch32BitWarn.setVisible(false);		
			GRTContainer.add(lcCatalogExch32BitWarn, new FlowData(0,0,1,20));		
		} else {
			GRTContainer.add(
					AdvScheduleUtil.createLayout(UIContext.Constants.destinationCatalogTitle(), new LabelField[] { hbbuCatalogCommentLabel }, new Widget[] {
							cbCatalog, createCatalogExchInfoMessage() }), new FlowData(0,0,1,0));	
		}					
				
		notificationSet = new FieldSet();
		notificationSet.setHeadingHtml(UIContext.Messages.backupSettingsNodifications(1));
		notificationSet.setCollapsible(true);
		TableLayout warningLayout = new TableLayout();
		warningLayout.setWidth("100%");
		warningLayout.setCellSpacing(1);
		warningLayout.setColumns(2);
		notificationSet.setLayout(warningLayout);
		notificationSet.setVisible(false);
		GRTContainer.add(notificationSet, AdvScheduleUtil.createLineLayoutData());
		
		isEditable = true;
	}

	public void showCheckboxGenerateGRTCatalog(boolean isHBBU) {
		if(UIContext.isExchangeGRTFuncEnabled && isHBBU)
		{	
			lcCatalogExch32BitWarn.setVisible(true);
		}
	}

	@Override
	public void buildValue(BackupSettingsModel value) {		
		if (UIContext.isExchangeGRTFuncEnabled && this.checkboxGenerateGRTCatalog.getValue())
			value.setExchangeGRTSetting(EXCHANGE_GRT_ENABLE_AFTER_BACKUP);
		else
			value.setExchangeGRTSetting(EXCHANGE_GRT_ENABLE_BEFORE_RESTORE);		

		value.setGenerateCatalog(cbCatalog.getValue());
	}

	@Override
	public void applyValue(BackupSettingsModel value) {
		cbCatalog.setValue(value.getGenerateCatalog());
		if(UIContext.isExchangeGRTFuncEnabled) {
			if (value.getExchangeGRTSetting() == EXCHANGE_GRT_ENABLE_BEFORE_RESTORE)
				this.checkboxGenerateGRTCatalog.setValue(false);
			else
				this.checkboxGenerateGRTCatalog.setValue(true);

			checkExchangeService();	
		}		

		updateNotificationSet();
	}

	@Override
	public boolean validate() {
		return true;
	}

	public void setVisible4HbbuCommentLabel(boolean visible) {
		hbbuCatalogCommentLabel.setVisible(visible);
	}

	private boolean isEditable;

	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
		if(UIContext.isExchangeGRTFuncEnabled) {
			checkboxGenerateGRTCatalog.setEnabled(isEditable);
		}
		cbCatalog.setEnabled(isEditable);
	}

	public void checkExchangeService() {
		// check if Exchange Service is installed
		final LoginServiceAsync service = GWT.create(LoginService.class);
		service.checkServiceStatus("msexchangeis", new BaseAsyncCallback<Long>() {

			@Override
			public void onFailure(Throwable caught) {
				checkboxGenerateGRTCatalog.setEnabled(false);
				grtUtilityInfoMessage.setVisible(false);
				super.onFailure(caught);
			}

			@Override
			public void onSuccess(Long result) {
				if (result != null) {
					// Exchange Service is installed
					// if for edge this option should be always enabled
					if (((result.intValue() & 0x01) == 0x01)) {
						if (isEditable) {
							checkboxGenerateGRTCatalog.setEnabled(true);
						} else {
							checkboxGenerateGRTCatalog.setEnabled(false);
						}

					} else {
						checkboxGenerateGRTCatalog.setEnabled(false);
						checkboxGenerateGRTCatalog.setValue(false);
						grtUtilityInfoMessage.setVisible(false);
					}

				} else {
					checkboxGenerateGRTCatalog.setEnabled(false);
					checkboxGenerateGRTCatalog.setValue(false);
					grtUtilityInfoMessage.setVisible(false);
				}

				super.onSuccess(result);
			}

		});
	}

	public void setDoCatlaogValue(boolean enable) {
		if (!enable)
			cbCatalog.setValue(enable);
		cbCatalog.setEnabled(enable);
	}

	public CheckBox getCatalogCheckBox() {
		return this.cbCatalog;
	}

	public void updateNotificationSet() {
		removeNotificationSet();
		if (BackupSettingUtil.getInstance().settingContent.getRefsVolList() != null
				&& BackupSettingUtil.getInstance().settingContent.getRefsVolList().length() > 0) {
			if (cbCatalog.getValue()) {
				addWaringIcon();
				notificationSet.add(new LabelField(UIContext.Messages.refsVolumesSelect(BackupSettingUtil.getInstance().settingContent.getRefsVolList())));
				notificationSet.setVisible(true);
				notificationSet.expand();
				notificationSet.layout(true);
			}
		}
	}

	public void removeNotificationSet() {
		notificationSet.removeAll();
		notificationSet.setVisible(false);
	}

	private Listener<BaseEvent> catalogCheckListener = new Listener<BaseEvent>() {
		//
		@Override
		public void handleEvent(BaseEvent ArchiveEvent) {
			if (ArchiveEvent.getSource() == cbCatalog) {
				if (!cbCatalog.getValue() && BackupContext.isFileCopyEnable()) {
					MessageBox mb = new MessageBox();
					mb.setIcon(MessageBox.WARNING);
					mb.setButtons(MessageBox.YESNO);
					mb.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D));
					mb.setModal(true);
					mb.setMinWidth(400);
					mb.setMessage(UIContext.Constants.settingsIFDisableArchive());
					mb.addCallback(new Listener<MessageBoxEvent>() {
						public void handleEvent(MessageBoxEvent be) {
							if (be.getButtonClicked().getItemId().equals(Dialog.NO)) {
								BackupContext.getArchiveSourceSettings().cbArchiveAfterBackup.setValue(false);
							} else {
								cbCatalog.setValue(true);
							}

						}
					});
					Utils.setMessageBoxDebugId(mb);
					mb.show();
				}
				if (BackupSettingUtil.getInstance().settingContent.getRefsVolList() != null) {
					if (cbCatalog.getValue()) {
						addWaringIcon();
						notificationSet.add(new LabelField(
								UIContext.Messages.refsVolumesSelect(BackupSettingUtil.getInstance().settingContent.getRefsVolList())));
						notificationSet.setVisible(true);
						notificationSet.expand();
						notificationSet.layout(true);
					} else {
						notificationSet.removeAll();
						notificationSet.setVisible(false);

					}
				}

			}

		}
	};

	private Image getWaringIcon() {
		Image warningImage = AbstractImagePrototype.create(UIContext.IconBundle.logWarning()).createImage();
		return warningImage;
	}

	private void addWaringIcon() {
		Image warningImage = getWaringIcon();
		TableData tableData = new TableData();
		tableData.setStyle("padding: 2px 3px 3px 0px;"); // refer to the GWT
															// default setting.
		tableData.setVerticalAlign(VerticalAlignment.TOP);
		notificationSet.add(warningImage, tableData);
	}
	
	private Widget createCatalogExch32BitWarningMessage() {
		LayoutContainer tableContainer = new LayoutContainer();
		
		TableLayout tableLayout = new TableLayout();
		tableLayout.setCellPadding(0);
		tableLayout.setCellSpacing(5);
		tableLayout.setColumns(2);
		tableLayout.setWidth("100%");		
		tableContainer.setLayout(tableLayout);
				
		tableContainer.add(AbstractImagePrototype.create(UIContext.IconBundle.logWarning()).createImage());
		tableContainer.add(new LabelField(UIContext.Constants.scheduleCatalogExch32BitWarn()));
		return tableContainer;
	}

	private Widget createCatalogExchInfoMessage() {
		LayoutContainer container = new LayoutContainer();
		container.addStyleName("catalogExchInfoStyle");
					
		String message = createMessageWithLinkForGrtDisabled();
		container.add(createMessageWithIconContainer(message));
		
		return container;
	}
	
	private Widget createGRTUtilityInfoMessage() {
		LayoutContainer container = new LayoutContainer();
		container.addStyleName("infobox");		
							
		container.add(new LabelField(createMessageWithLinkForGrtEnabled()));
		
		return container;		
	}
	
	private LayoutContainer createMessageWithIconContainer(String message) {
		LayoutContainer tableContainer = new LayoutContainer();
		
		TableLayout tableLayout = new TableLayout();
		tableLayout.setCellPadding(0);
		tableLayout.setCellSpacing(5);
		tableLayout.setColumns(2);
		tableLayout.setCellVerticalAlign(VerticalAlignment.TOP);
		tableLayout.setWidth("100%");		
		tableContainer.setLayout(tableLayout);
		
		Image info = AbstractImagePrototype.create(UIContext.IconBundle.info_16x16()).createImage();
		info.setStyleName("catalogExchConfigInfoImageStyle");
		tableContainer.add(info);
		tableContainer.add(new LabelField(message));
		
		return tableContainer;
	}
	
	private String createMessageWithLinkForGrtDisabled() {				
		String link = createLinkHtml(UIContext.externalLinks.getExchangeGranularRestoreUtility(), 
				UIContext.Constants.scheduleCatalogExchUtilityLink());
		String message = UIContext.Messages.scheduleCatalogExchNotRequired(link);
		return message;
	}
	
	private String createMessageWithLinkForGrtEnabled() {				
		String link = createLinkHtml(UIContext.externalLinks.getExchangeGranularRestoreUtility(), 
				UIContext.Constants.scheduleCatalogExchUtilityLinkShort(), "text-decoration:underline;");
		String message = UIContext.Messages.scheduleGRTUtilityCatalogExchNotNecessary(link);
		return message;
	}
	
	private String createLinkHtml(String url, String label) {
		return createLinkHtml(url, label, null);
	}
	
	private String createLinkHtml(String url, String label, String style) {
		StringBuilder sbLinkHtml = new StringBuilder();
		
		sbLinkHtml.append("<a class=\"catalogExchConfigInfoLinkStyle\" style=\"color:#0066AA;");		
		sbLinkHtml.append(style);		
		sbLinkHtml.append("\" href=\"");
		sbLinkHtml.append(url);
		sbLinkHtml.append("\" target=\"_blank\">");
		sbLinkHtml.append(label);
		sbLinkHtml.append("</a>");
		
		return sbLinkHtml.toString();
	}
}