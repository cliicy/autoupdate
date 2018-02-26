package com.ca.arcflash.ui.client.restore.ad;


import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.login.LoginService;
import com.ca.arcflash.ui.client.login.LoginServiceAsync;
import com.ca.arcflash.ui.client.model.RestoreJobModel;
import com.ca.arcflash.ui.client.restore.PasswordPane;
import com.ca.arcflash.ui.client.restore.RestoreOptionsPanel;
import com.ca.arcflash.ui.client.restore.RestoreWizardContainer;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

public class ActiveDirectoryOptionPanel extends RestoreOptionsPanel {
	// service

	final LoginServiceAsync service = GWT.create(LoginService.class);
	
	protected PasswordPane archivePwdPane = new PasswordPane();

	AsyncCallback<Boolean> callbackValidate;

	private Radio skip_delete;

	private Radio skip_move;

	private Radio skip_rename;



	public ActiveDirectoryOptionPanel(RestoreWizardContainer restoreWizardWindow) {
		super(restoreWizardWindow);
	}

	@Override
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		setStyleAttribute("margin", "5px");
		TableLayout tableLayout = new TableLayout();
		tableLayout.setWidth("100%");
		tableLayout.setColumns(2);
		tableLayout.setCellPadding(2);
		tableLayout.setCellSpacing(2);
		this.setLayout(tableLayout);
		this.setHeight("100%");
		
		TableData td = new TableData();
		td.setColspan(2);
		
		// Header Section
		this.add(renderHeaderSection(), td);
		// Resolving Object Changes
		TableData tdLeft = new TableData();
		tdLeft.setWidth("30%");		
		tdLeft.setHorizontalAlign(HorizontalAlignment.LEFT);
		TableData tdRight = new TableData();
		tdRight.setWidth("70%");		
		tdRight.setHorizontalAlign(HorizontalAlignment.LEFT);
		
		Label label = new Label(UIContext.Constants.restoreADResolvingChanges());
		label.addStyleName("restoreWizardSubItem");
		this.add(label, td);
		
		//renamed objects
		label = new Label(UIContext.Constants.restoreADRenamedObjects());
		label.addStyleName("restoreWizardSubItemDescription");
		this.add(label, tdLeft);
		
		skip_rename = new Radio();
		skip_rename.setBoxLabel(UIContext.Constants.restoreADSkip());
		skip_rename.setValue(true);

		Radio restore_rename = new Radio();
		restore_rename.setBoxLabel(UIContext.Constants.restoreADToOriginalName());

		RadioGroup rGroup_rename = new RadioGroup();
		rGroup_rename.setFieldLabel(UIContext.Constants.restoreADRenamedObjects());
		rGroup_rename.add(skip_rename);
		rGroup_rename.add(restore_rename);
		rGroup_rename.setStyleAttribute("margin-left", "30px");
//		this.add(rGroup_rename, td);
		this.add(skip_rename, tdRight);
		this.add(new Label(), tdLeft);
		this.add(restore_rename, tdRight);
		
		//removed objects
		label = new Label(UIContext.Constants.restoreADMovedObjects());
		label.addStyleName("restoreWizardSubItemDescription");
		this.add(label, tdLeft);
		
		skip_move = new Radio();
		skip_move.setBoxLabel(UIContext.Constants.restoreADSkip());
		skip_move.setValue(true);

		Radio restore_remove = new Radio();
		restore_remove.setBoxLabel(UIContext.Constants.restoreADToOriginalLocatoion());

		RadioGroup rGroup_remove = new RadioGroup();
		rGroup_remove.setFieldLabel(UIContext.Constants.restoreADMovedObjects());
		rGroup_remove.add(skip_move);
		rGroup_remove.add(restore_remove);
		rGroup_remove.setStyleAttribute("margin-left", "30px");
//		this.add(rGroup_remove, tdLeft);
		this.add(skip_move, tdRight);
		this.add(new Label(), tdLeft);
		this.add(restore_remove, tdRight);
		
		//deleted objects
		label = new Label(UIContext.Constants.restoreADDeletedObjects());
		label.addStyleName("restoreWizardSubItemDescription");
		this.add(label, tdLeft);
		
		skip_delete = new Radio();
		skip_delete.setBoxLabel(UIContext.Constants.restoreADSkip());
		skip_delete.setValue(true);

		Radio restore_delete = new Radio();
		restore_delete.setBoxLabel(UIContext.Constants.restoreADWithNewObjectID());

		RadioGroup rGroup_delete = new RadioGroup();
		rGroup_delete.setFieldLabel(UIContext.Constants.restoreADDeletedObjects());
		rGroup_delete.add(skip_delete);
		rGroup_delete.add(restore_delete);
		rGroup_delete.setStyleAttribute("margin-left", "30px");
//		this.add(rGroup_delete, td);
		this.add(skip_delete, tdRight);
		this.add(new Label(), tdLeft);
		this.add(restore_delete, tdRight);

//		Radio skip_rename = new Radio();
//		skip_rename.ensureDebugId("0EE524EA-690B-46af-9B1D-998A50AB9F6F");
//		skip_rename.setBoxLabel(UIContext.Constants
//				.restoreToOriginalLocation());
//		Utils.addToolTip(skip_rename, UIContext.Constants
//				.restoreToOriginalLocationTooltip());
//		skip_rename.addStyleName("restoreWizardLeftSpacing");
//		this.add(skip_rename, td);
//		lblCloudNotification.setStyleAttribute("margin-left", "10px");
//		lblCloudNotification.setStyleAttribute("margin-bottom", "10px");

		this.add(new Html("<HR>"), td);

		this.add(pwdPane, td);
		
//		archivePwdPane.setEncryptTitle(new Label(UIContext.Constants.archiveEncryptionPassword()));
//		this.add(archivePwdPane,td);
//		archivePwdPane.setVisible(false);
//		this.add(recoveryPointPasswordPanel, td);
	}

	
	private LayoutContainer renderHeaderSection() {
		LayoutContainer container = new LayoutContainer();
		TableLayout tl = new TableLayout();
		tl.setColumns(2);
		container.setLayout(tl);

		TableData td = new TableData();
		td.setWidth("5%");

		Image image = AbstractImagePrototype.create(UIContext.IconBundle.restore_options()).createImage();
		container.add(image, td);

		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.restoreOptions());
		label.setStyleName("restoreWizardTitle");
		container.add(label);

		return container;
	}

	@Override
	public boolean validate(AsyncCallback<Boolean> callback) {
		//for validating the backup encryption passwords.
		checkSessionPassword(callback);
		return true;
	}
	
	@Override
	public int processOptions() {
		RestoreJobModel model = restoreWizardWindow.getRestoreJobModel();
		model.adOption = new ADOptionModel();
		model.adOption.setSkipRenamedObject(skip_rename.getValue());
		model.adOption.setSkipMovedObject(skip_move.getValue());
		model.adOption.setSkipDeletedObject(skip_delete.getValue());
		
		if(pwdPane.isVisible()) {
			String password = pwdPane.getPassword();
			model.setEncryptPassword(password);
		}
		return 0;
	}

}
