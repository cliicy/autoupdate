package com.ca.arcflash.ui.client.recoverypoint;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.backup.EncryptionPane;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.BaseSimpleComboBox;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.IConstants;
import com.ca.arcflash.ui.client.common.PasswordTextField;
import com.ca.arcflash.ui.client.common.PathSelectionPanel;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.CopyJobModel;
import com.ca.arcflash.ui.client.model.RecoveryPointModel;
import com.ca.arcflash.ui.client.restore.RestoreContext;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;

public class BaseExportOptionPanel extends LayoutContainer {
	protected PathSelectionPanel pathSelection;
	protected BaseSimpleComboBox<String> compressionOption;
	protected LayoutContainer sourcePassword;
	protected PasswordTextField pwdField;
	protected EncryptionPane encryptionPane;
	protected boolean isGettingPassword = false;
	protected Radio needPwd, noNeedPwd;
	protected RadioGroup group = new RadioGroup();
	protected LabelField compressionOptionlabel;
	protected LabelField noPwddesc;
	
	protected void validateDestinaiontPassword(final AsyncCallback<Boolean> callback) {
		if (encryptionPane.isVisible() && encryptionPane.isEnabled()) {
			if (!encryptionPane.validate()) {
				callback.onSuccess(false);
				return;
			}
		}
		callback.onSuccess(true);
	}
	
	protected boolean isNeedSourcePassword() {
		RecoveryPointModel pointModel = RestoreContext.getRecoveryPointModel();
		return pointModel != null && pointModel.isEncrypted();
	}

	protected String getSourcePassword() {
		if (isGettingPassword)
			return "";
		else
			return pwdField.getValue();
	}
	
	public PathSelectionPanel getPathSelectionPanel() {
		return pathSelection;
	}

	protected void showCompressAndEncyptPanel(boolean isVisible) {
		compressionOptionlabel.setVisible(isVisible);
		compressionOption.setVisible(isVisible);
		encryptionPane.setVisible(isVisible);		
	}
	
	protected LayoutContainer renderHeaderSection() {
		LayoutContainer container = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(2);
		container.setLayout(tl);

		TableData td = new TableData();
		td.setWidth("5%");

		Image image = AbstractImagePrototype.create(UIContext.IconBundle.restore_options()).createImage();
		container.add(image, td);

		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.recoveryPointsExportOptions());
		label.setStyleName("restoreWizardTitle");
		container.add(label);

		return container;
	}
	
	protected void createSourcePasswordPane() {
		sourcePassword = new LayoutContainer();
		sourcePassword.setLayout(new RowLayout());

		LabelField label = new LabelField();
		label.addStyleName("restoreWizardSubItem");
		label.setValue(UIContext.Constants.crpPwdHeader());
		sourcePassword.add(label);

		noNeedPwd = new Radio() {
			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);
				pwdField.disable();
				showCompressAndEncyptPanel(false);			
			}			
		};
		noNeedPwd.setBoxLabel(UIContext.Constants.crpNoPassword());
		sourcePassword.add(noNeedPwd);
		
		noPwddesc = new LabelField(UIContext.Constants.crpNoPwdTip());
		//noPwddesc.addStyleName("restoreWizardSubItemDescription");		
		noPwddesc.setStyleAttribute("margin-bottom", "5px");
		sourcePassword.add(noPwddesc);
		
		needPwd = new Radio() {
			@Override
			protected void onClick(ComponentEvent be) {
				super.onClick(be);				
				pwdField.enable();
				showCompressAndEncyptPanel(true);
			}
		};
		needPwd.setBoxLabel(UIContext.Constants.crpHasPassword());
		needPwd.setValue(true);
		sourcePassword.add(needPwd);

		LayoutContainer pwdContainer = new LayoutContainer();
		TableLayout t = new TableLayout();
		t.setColumns(2);
		t.setCellPadding(2);
		t.setCellSpacing(2);
		pwdContainer.setLayout(t);

		label = new LabelField(UIContext.Constants.settingsLabelEncyrptionPassword());
		label.addStyleName("restoreWizardSubItemDescription");

		pwdField = new PasswordTextField();
		pwdField.ensureDebugId("b78a8774-90bc-4d8d-bb1b-964e7fe9c35a");
		pwdField.setPassword(true);
		pwdField.setMaxLength(Utils.EncryptionPwdLen);

		pwdContainer.add(label);
		pwdContainer.add(pwdField);

		sourcePassword.add(pwdContainer);	

		sourcePassword.add(new Html("<HR>"));

		add(sourcePassword);
		
		group.setName("pwdgroup");
		group.add(needPwd);
		group.add(noNeedPwd);
	}
	
	protected String getProductTitle(){
		return UIContext.Messages.messageBoxTitleError(UIContext.productNameD2D);
	}
	
	protected void showErrorMessage(final AsyncCallback<Boolean> callback, String errorMsg) {
		if (errorMsg != null && errorMsg.length() > 0) {
			MessageBox messageBox = new MessageBox();
			messageBox.setModal(true);
			messageBox.setTitleHtml(getProductTitle());
			messageBox.setMessage(errorMsg);
			messageBox.setIcon(MessageBox.ERROR);
			Utils.setMessageBoxDebugId(messageBox);
			messageBox.show();
		}
		callback.onSuccess(Boolean.FALSE);
	}
	
	public void validate(final AsyncCallback<Boolean> callback) {
		if (isNeedSourcePassword()) {
			if(needPwd.getValue() != null && needPwd.getValue()){
				String password = getSourcePassword();
				if (password == null || password.length() == 0) {
					showErrorMessage(callback, UIContext.Constants.recoveryPointsNeedSessionPassword());
					return;
				} else if (pwdField.isMaxLengthExceeded()) {
					showErrorMessage(callback, UIContext.Constants.PasswordBeyondLength());
					return;
				} else {
					CommonServiceAsync service = GWT.create(CommonService.class);
					String destination = getSessionPath();
					RecoveryPointModel pointModel = RestoreContext.getRecoveryPointModel();
					Integer sessionNum = pointModel.getSessionID();
					service.validateSessionPassword(password, destination, sessionNum, new BaseAsyncCallback<Boolean>() {
						@Override
						public void onFailure(Throwable caught) {
							super.onFailure(caught);
							callback.onSuccess(Boolean.FALSE);
						}

						@Override
						public void onSuccess(Boolean isValid) {
							if (isValid != null && isValid) {
								validateDestinaiontPassword(callback);
							} else {
								BaseExportOptionPanel.this.showErrorMessage(callback, UIContext.Constants.recoveryPointsInvalidSessionPassword());
							}
						}
					});
				}
			}else{
				callback.onSuccess(true);
			}
		} else {
			// callback.onSuccess(Boolean.TRUE);
			validateDestinaiontPassword(callback);
		}

	}

	protected String getSessionPath(){
		return null;
	}
	
	@Override
	public void repaint() {
		super.repaint();
		if (isNeedSourcePassword()) {
			
			sourcePassword.setVisible(true);
			RecoveryPointModel recoveryPointModel = getSelectedRecoveryPoints();
			if (recoveryPointModel == null)
				return;
			
			if(isBackup2RPS()){
				needPwd.enable();
				noNeedPwd.enable();
				noPwddesc.enable();
			}else{
				needPwd.enable();
				noNeedPwd.disable();
				noPwddesc.disable();
			}
			
			String sessionGuid = recoveryPointModel.getSessionGuid();
			if (sessionGuid == null || sessionGuid.isEmpty())
				pwdField.clear();
			else {
				pwdField.disable();
				pwdField.mask(UIContext.Constants.loadingPassword());
				isGettingPassword = true;
				CommonServiceAsync service = GWT.create(CommonService.class);
				service.getSessionPasswordBySessionGuid(new String[] { sessionGuid }, new BaseAsyncCallback<String[]>() {

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						pwdField.unmask();
						pwdField.enable();
						pwdField.clear();
						isGettingPassword = false;
					}

					@Override
					public void onSuccess(String[] result) {
						super.onSuccess(result);
						pwdField.unmask();
						pwdField.enable();
						pwdField.clear();
						if (result.length > 0 && result[0] != null) {
							pwdField.setValue(result[0]);
						}
						isGettingPassword = false;
					}
				});
			}
		} else
			sourcePassword.setVisible(false);
	}

	protected RecoveryPointModel getSelectedRecoveryPoints() {
		return null;
	}

	protected boolean isBackup2RPS() {
		return false;
	}

	public void processOptions(CopyJobModel model) {
		// Set Destination Path and Compression Level
		model.setDestinationPath(pathSelection.getDestination());
		model.setDestinationUserName(pathSelection.getUsername());
		model.setDestinationPassword(pathSelection.getPassword());
		if(noNeedPwd.getValue()!=null)
			model.setRetainEncryptionAsSource(noNeedPwd.getValue());
		
		if (sourcePassword.isVisible() && needPwd.getValue() !=null && needPwd.getValue()) {
			model.setEncryptPassword(getSourcePassword());
		}

		if (compressionOption.getSimpleValue() == UIContext.Constants.settingsCompressionNone())
			model.setCompressionLevel(IConstants.COMPRESSIONNONE);
		else if (compressionOption.getSimpleValue() == UIContext.Constants.settingsCompreesionStandard())
			model.setCompressionLevel(IConstants.COMPRESSIONSTANDARD);
		else if (compressionOption.getSimpleValue() == UIContext.Constants.settingsCompressionMax())
			model.setCompressionLevel(IConstants.COMPRESSIONMAX);
		else if (UIContext.Constants.settingsNoCompressionVHD().equalsIgnoreCase(compressionOption.getSimpleValue()))
			model.setCompressionLevel(IConstants.COMPRESSIONNONEVHD);

		if (encryptionPane.isVisible() && encryptionPane.isEnabled()){
			long encryptType = encryptionPane.getEncryptAlgorithm().intValue() + 0L;
			model.setEncryptTypeCopySession(encryptType);
			model.setEncryptPasswordCopySession(encryptionPane.getEncryptPassword());
		}
	}
	
	@Override
	public void onRender(Element parent, int index) {
		super.onRender(parent, index);

		RowLayout layout = new RowLayout();
		setLayout(layout);
		this.setScrollMode(Scroll.AUTOY);
		// RowData data = new RowData();
		add(renderHeaderSection());
		createSourcePasswordPane();

		LabelField label = new LabelField();
		label.addStyleName("restoreWizardSubItem");
		label.setValue(UIContext.Constants.recoveryPointsExportOptions());
		// label.setStyleAttribute("padding-left", "10px");

		add(label);

		LayoutContainer container = new LayoutContainer();
		// container.setStyleAttribute("padding-left", "10px");

		TableLayout tl = new TableLayout();
		tl.setColumns(2);
		tl.setCellPadding(0);
		tl.setCellSpacing(4);
		tl.setWidth("100%");
		container.setLayout(tl);

		TableData td = new TableData();
		td.setWidth("100%");

		label = new LabelField();
		label.setStyleAttribute("white-space", "nowrap");
		label.setValue(UIContext.Constants.recoveryPointsDestination());
		container.add(label);

		pathSelection = new PathSelectionPanel(new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent be) {
				String newDest = pathSelection.getDestination();
				if (newDest == null || newDest.isEmpty())
					// TODO
					;
				// okButton.disable();
				else
					;
				// okButton.enable();
			}
		});

		pathSelection.setMode(PathSelectionPanel.COPY_MODE);
		pathSelection.setTooltipMode(PathSelectionPanel.TOOLTIP_COPY_MODE);
		pathSelection.setPathFieldLength(395);
		pathSelection.addDebugId("968BAF11-AF25-4af4-ABFA-3A7D2810B560", "B29AFBCE-E7B8-4b63-B36F-77F1DC45ACCF", "B00B67FB-87EC-42f7-8DC4-8A90636BE735");
		container.add(pathSelection, td);

		compressionOptionlabel = new LabelField();
		compressionOptionlabel.setValue(UIContext.Constants.recoveryPointsCompression());
		container.add(compressionOptionlabel);

		LayoutContainer combCon = new LayoutContainer();
		combCon.setStyleAttribute("margin", "2px, 2px, 2px, 10px");

		TableLayout combLayout = new TableLayout();
		combLayout.setColumns(1);
		combLayout.setCellPadding(2);
		combLayout.setCellSpacing(2);
		combCon.setLayout(combLayout);
		compressionOption = new BaseSimpleComboBox<String>();
//		compressionOption.setFieldLabel(UIContext.Constants.recoveryPointsCompression());
		compressionOption.ensureDebugId("fca98e5d-520f-4127-b376-62a45698f322");
		compressionOption.setEditable(false);
		compressionOption.add(UIContext.Constants.settingsCompressionNone());
		compressionOption.add(UIContext.Constants.settingsNoCompressionVHD());
		compressionOption.add(UIContext.Constants.settingsCompreesionStandard());
		compressionOption.add(UIContext.Constants.settingsCompressionMax());
		compressionOption.setSimpleValue(UIContext.Constants.settingsCompreesionStandard());
		Utils.addToolTip(compressionOption, UIContext.Constants.settingsLabelCompressionStandardTooltip());
		compressionOption.setWidth(200);

		compressionOption.addListener(Events.Select, new SelectionListener<FieldEvent>() {
			public void componentSelected(FieldEvent ce) {
				String selString = compressionOption.getSimpleValue();

				if (UIContext.Constants.settingsNoCompressionVHD().equalsIgnoreCase(selString)) {
					Utils.addToolTip(compressionOption, UIContext.Constants.settingsLabelCompressionVHD());
					encryptionPane.disable();
				} else {
					encryptionPane.enable();
				}

				if (selString.compareTo(UIContext.Constants.settingsCompressionNone()) == 0) {
					Utils.addToolTip(compressionOption, UIContext.Constants.settingsLabelCompressionNoneTooltip());
					encryptionPane.setVisible(true);
				} else if (selString.compareTo(UIContext.Constants.settingsCompreesionStandard()) == 0) {
					Utils.addToolTip(compressionOption, UIContext.Constants.settingsLabelCompressionStandardTooltip());
					encryptionPane.setVisible(true);
				} else if (selString.compareTo(UIContext.Constants.settingsCompressionMax()) == 0) {
					Utils.addToolTip(compressionOption, UIContext.Constants.settingsLabelCompressionMaxTooltip());
					encryptionPane.setVisible(true);
				}
			}
		});

		// Put compressionOption control to the LayoutContainer to solve the
		// alignment problem in IE, Chrome and Firefox.
		combCon.add(compressionOption);

		td = new TableData();
		td.setWidth("100%");
		container.add(combCon, td);

		// exportOpionPanel.add(container);
		add(container);

		encryptionPane = new EncryptionPane();
		add(encryptionPane);

		LabelField note = new LabelField();
		note.setValue(UIContext.Constants.recoveryPointsNote());
		note.setStyleAttribute("padding-left", "12px");
		note.setWidth("98%");
		// exportOpionPanel.add(note);
		add(note);
	}
}
