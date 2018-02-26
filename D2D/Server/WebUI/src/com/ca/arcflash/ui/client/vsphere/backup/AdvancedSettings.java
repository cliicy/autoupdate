package com.ca.arcflash.ui.client.vsphere.backup;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.PasswordTextField;
import com.ca.arcflash.ui.client.common.Utils;
import com.ca.arcflash.ui.client.model.VSphereBackupSettingModel;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.ca.arcflash.ui.client.common.Utils;

public class AdvancedSettings {
	
	private VSphereBackupSettingWindow parentWindow;

	
	private CheckBox preCheckbox;
	private CheckBox postCheckbox;
	private CheckBox postSnapshotCheckbox;
	
	private CheckBox onExitCodeCheckbox;
	private Radio runJobRadio;
	private Radio failJobRadio;
	
	private TextField<String> preCommand;
	private TextField<String> postCommand;
	private TextField<String> postSnapshotCommand; 
	private NumberField exitCodeNumberField;
		
	private TextField<String> usernameTF;
	private PasswordTextField passwordTF;
	
	private LayoutContainer container;
	
	private VSphereBackupSettingModel settingsModel;	
	
	static final int DEFAULT_EXIT_CODE = 0;
	static final int QJDTO_B_RUN_JOB = 0x00001000;
	static final int QJDTO_B_FAIL_JOB = 0x00002000;	
	public static final String MeasureUnitPercent = "%";
	public static final String MeasureUnitMegabyte = "MB";
	//old value for freespace unit;
	//private String oldValue;
	//private double cachePecentage;
	
	public AdvancedSettings(VSphereBackupSettingWindow w)
	{
		parentWindow = w;
	}
	
	public LayoutContainer Render()
	{
		container = new LayoutContainer();
		container.setScrollMode(Scroll.AUTOY);
		TableLayout rl = new TableLayout();
		rl.setWidth("97%");
		//rl.setHeight("95%");
		container.setLayout(rl);
				
		LabelField label = new LabelField();
		label.setValue(UIContext.Constants.backupSettingsPrePost());
		label.addStyleName("restoreWizardTitle");
		container.add(label);
		
		/*label = new LabelField();
		label.setText(UIContext.Constants.advancedLabelNotifications());
		label.addStyleName("restoreWizardSubItem");
		container.add(label);*/
		
		LayoutContainer tableContainer = new LayoutContainer();
		
		TableLayout tableLayout = new TableLayout();
		tableLayout.setCellPadding(2);
		tableLayout.setCellSpacing(2);
		tableLayout.setColumns(4);
		tableLayout.setWidth("100%");		
		tableContainer.setLayout(tableLayout);

		label = new LabelField();
		label.setValue(UIContext.Constants.advancedLabelActions());
		label.addStyleName("restoreWizardSubItem");
		container.add(label);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.advancedLabelActionsDescription());
		container.add(label);
		
		
		//Username/Password Section
		LayoutContainer tablePasswordContainer = new LayoutContainer();
		
		TableLayout tablePasswordLayout = new TableLayout();
		tablePasswordLayout.setCellPadding(2);
		tablePasswordLayout.setCellSpacing(2);
		tablePasswordLayout.setColumns(2);
		tablePasswordLayout.setWidth("100%");		
		tablePasswordContainer.setLayout(tablePasswordLayout);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.settingsLabelEncryptionUser());
		tablePasswordContainer.add(label);				
				
		usernameTF = new TextField<String>();
		usernameTF.ensureDebugId("374E6AA5-1598-4ff3-A414-A1E3D53844C9");		
		Utils.addToolTip(usernameTF, UIContext.Constants.advancedActionsUserNameTooltip());
		usernameTF.setWidth(300);		
		tablePasswordContainer.add(usernameTF);
		
		label = new LabelField();
		label.setValue(UIContext.Constants.settingsLabelEncyrptionPassword());
		tablePasswordContainer.add(label);		
		
		passwordTF = new PasswordTextField();
		passwordTF.ensureDebugId("0CE9F9CA-CA88-4cc5-ACB6-857D0A7FE285");
		passwordTF.setPassword(true);		
		Utils.addToolTip(passwordTF, UIContext.Constants.advancedActionsPasswordTooltip());
		passwordTF.setWidth(300);		
		tablePasswordContainer.add(passwordTF);
		
		container.add(tablePasswordContainer);
		
		//Pre/Post Section		
		LayoutContainer commandContainer = new LayoutContainer();
				
		TableLayout layout = new TableLayout();
		layout.setCellPadding(2);
		layout.setCellSpacing(2);
		layout.setColumns(2);
		layout.setWidth("100%");
		
		commandContainer.setLayout(layout);
		
		TableData td = new TableData();
		td.setWidth("100%");
		td.setColspan(2);
		
		preCheckbox = new CheckBox();
		preCheckbox.ensureDebugId("999DDDEF-82F5-4846-BE03-73447EF2BD3E");
		preCheckbox.setBoxLabel(UIContext.Constants.advancedCheckboxRunCommandBefore());
		preCheckbox.addListener(Events.Change, new Listener<FieldEvent>()
		{

			@Override
			public void handleEvent(FieldEvent be) {
				preCommand.setEnabled(preCheckbox.getValue());
				exitCodeNumberField.setEnabled(preCheckbox.getValue() && onExitCodeCheckbox.getValue());
				onExitCodeCheckbox.setEnabled(preCheckbox.getValue());
				
				runJobRadio.setEnabled(onExitCodeCheckbox.getValue() && preCheckbox.getValue());
				failJobRadio.setEnabled(onExitCodeCheckbox.getValue()  && preCheckbox.getValue());
			}
	
		});		
		commandContainer.add(preCheckbox, td);
		
		preCommand = new TextField<String>();
		preCommand.ensureDebugId("38135CD9-6267-481e-AAC3-B963E70CE7FD");
		preCommand.setWidth(450);
		Utils.addToolTip(preCommand, UIContext.Constants.advancedActionsCmdEditBoxTooltip());
		commandContainer.add(preCommand, td);
		
		td = new TableData();		
		td.setColspan(1);
		td.setWidth("30%");
		
		onExitCodeCheckbox = new CheckBox();
		onExitCodeCheckbox.ensureDebugId("F750B954-3894-42a3-A749-16C81FCE9928");
		onExitCodeCheckbox.setBoxLabel(UIContext.Constants.advancedCheckboxOnExitCode());
		onExitCodeCheckbox.addStyleName("restoreWizardLeftSpacing");
		onExitCodeCheckbox.addListener(Events.Change, new Listener<FieldEvent>()
		{

			@Override
			public void handleEvent(FieldEvent be) {
				runJobRadio.setEnabled(onExitCodeCheckbox.getValue());
				failJobRadio.setEnabled(onExitCodeCheckbox.getValue());
				exitCodeNumberField.setEnabled(onExitCodeCheckbox.getValue());
				
			}
	
		});
		commandContainer.add(onExitCodeCheckbox, td);
		
		
		
		td = new TableData();
		td.setColspan(1);
		td.setWidth("70%");
		
		exitCodeNumberField = new NumberField();
		exitCodeNumberField.ensureDebugId("AD2D8FEA-8739-4e95-BB60-31AFE6516AEB");
		exitCodeNumberField.setWidth(200);
		Utils.addToolTip(exitCodeNumberField, UIContext.Constants.advancedActionsCmdOnExitCodeTooltip());
		commandContainer.add(exitCodeNumberField, td);
		
		td = new TableData();
		td.setWidth("100%");
		td.setColspan(2);
				
		runJobRadio = new Radio();
		runJobRadio.ensureDebugId("87B53C60-89FF-46f8-A0E4-BBD613BFCD60");
		runJobRadio.addStyleName("leftIndentFifty");
		runJobRadio.setBoxLabel(UIContext.Constants.advancedCheckboxRunJob());
		Utils.addToolTip(runJobRadio, UIContext.Constants.advancedActionsCmdRunJobTooltip());
		commandContainer.add(runJobRadio, td);
		
		failJobRadio = new Radio();
		failJobRadio.ensureDebugId("087A2762-F465-457a-872C-B778A3BD946B");
		failJobRadio.addStyleName("leftIndentFifty");
		failJobRadio.setBoxLabel(UIContext.Constants.advancedCheckboxFailJob());
		Utils.addToolTip(failJobRadio, UIContext.Constants.advancedActionsCmdFailJobTooltip());
		commandContainer.add(failJobRadio, td);
		
		RadioGroup group = new RadioGroup();
		group.setName("runfailgroup");
		group.add(runJobRadio);
		group.add(failJobRadio);
		
		//preCommand = new TextField<String>();
		//preCommand.setWidth(450);
		//commandContainer.add(preCommand, td);
		
		
		//Post Snapshot
		postSnapshotCheckbox= new CheckBox();
		postSnapshotCheckbox.ensureDebugId("ECD069FF-918A-4b68-9CA0-7816D0ABE35E");
		postSnapshotCheckbox.setBoxLabel(UIContext.Constants.advancedCheckboxRunCommandAfterSnapshot());
		postSnapshotCheckbox.addListener(Events.Change, new Listener<FieldEvent>()
		{
			@Override
			public void handleEvent(FieldEvent be) {
				postSnapshotCommand.setEnabled(postSnapshotCheckbox.getValue());						
			}
		});		
		commandContainer.add(postSnapshotCheckbox, td);
		
		postSnapshotCommand = new TextField<String>();
		postSnapshotCommand.ensureDebugId("8F99E623-1B09-4e8c-8EAD-A5E81C037179");
		postSnapshotCommand.setWidth(450);
		Utils.addToolTip(postSnapshotCommand, UIContext.Constants.advancedActionsSnapshotEditboxTooltip());
		commandContainer.add(postSnapshotCommand, td);
		
		
		
				
		postCheckbox = new CheckBox();
		postCheckbox.ensureDebugId("CF9F3AA9-766F-404f-933E-685980053957");
		postCheckbox.setBoxLabel(UIContext.Constants.advancedCheckboxRunCommandAfter());
		postCheckbox.addListener(Events.Change, new Listener<FieldEvent>()
		{
			@Override
			public void handleEvent(FieldEvent be) {
				postCommand.setEnabled(postCheckbox.getValue());				
			}
	
		});
		commandContainer.add(postCheckbox, td);
		
		postCommand = new TextField<String>();
		postCommand.ensureDebugId("38BE9B95-D44F-41e0-8B2E-2236C82255C3");
		postCommand.setWidth(450);
		Utils.addToolTip(postCommand, UIContext.Constants.advancedActionsAfterBackupEditboxTooltip());
		commandContainer.add(postCommand, td);
		
		container.add(commandContainer);
		
		return container;
	}

	public void RefreshData(VSphereBackupSettingModel model) {
		
		if (model != null)
		{
			settingsModel = model;
			
			String pre = model.getCommandBeforeBackup();
			
			if (pre == null || pre.trim().length() == 0)
			{
				//Default		
				preCheckbox.setValue(false);
				preCommand.setEnabled(false);
				exitCodeNumberField.setEnabled(false);
				onExitCodeCheckbox.setEnabled(false);
				runJobRadio.setEnabled(false);
				failJobRadio.setEnabled(false);
			}
			else 
			{
				preCheckbox.setValue(true);
				preCommand.setValue(pre);
				preCommand.setEnabled(true);
			}
						
			String post = model.getCommandAfterBackup();
			if (post == null || post.trim().length() == 0)
			{
				//Default
				postCheckbox.setValue(false);
				postCommand.setEnabled(false);
			}
			else 
			{
				postCheckbox.setValue(true);
				postCommand.setValue(post);
				postCommand.setEnabled(true);
				
			}
			
			if (model.getEnablePreExitCode() == null || model.getEnablePreExitCode() == false)
			{
				onExitCodeCheckbox.setValue(false);
				exitCodeNumberField.setEnabled(false);
				exitCodeNumberField.setValue(0);
			}
			else
			{
				Integer preExitCode = model.getPreExitCode();
				onExitCodeCheckbox.setValue(true);
				exitCodeNumberField.setEnabled(true);
				exitCodeNumberField.setValue(preExitCode);	
			}
			
			Boolean skipJob = model.getSkipJob();
			if (skipJob == null)
			{
				//Default check runJob
				runJobRadio.setValue(true);
				runJobRadio.setEnabled(false);
				failJobRadio.setEnabled(false);
			}
			else
			{
				failJobRadio.setValue(skipJob);
				runJobRadio.setValue(!skipJob);
			}
			
			String postSnap = model.getCommandAfterSnapshot();
			if (postSnap == null || postSnap.trim().isEmpty())
			{
				postSnapshotCheckbox.setValue(false);
				postSnapshotCommand.setEnabled(false);
			}
			else
			{
				postSnapshotCheckbox.setValue(true);
				postSnapshotCommand.setValue(postSnap);
				postSnapshotCommand.setEnabled(true);
			}
			
		}
		else
		{
			exitCodeNumberField.setValue(0);
		}
		
		this.container.repaint();
		
	}
	
	public void Save()
	{
		if (preCheckbox.getValue())
		{
			parentWindow.model.setCommandBeforeBackup(preCommand.getValue());
			
			
			if (onExitCodeCheckbox.getValue())
			{
				parentWindow.model.setEnablePreExitCode(true);
				
				Number n = exitCodeNumberField.getValue();
				if (n != null)
				{
					Integer exitCode = n.intValue();
					parentWindow.model.setPreExitCode(exitCode);
					if (runJobRadio.getValue())
					{
						parentWindow.model.setSkipJob(false);
					}
					else
					{
						parentWindow.model.setSkipJob(true);
					}
				}
			}
			else
			{
				parentWindow.model.setEnablePreExitCode(false);
			}
		}
		else
		{
			parentWindow.model.setCommandBeforeBackup("");
			parentWindow.model.setEnablePreExitCode(false);
		}
		
		if (postCheckbox.getValue())
		{
			parentWindow.model.setCommandAfterBackup(postCommand.getValue());
		}
		else
		{
			parentWindow.model.setCommandAfterBackup("");
		}
		
		if (postSnapshotCheckbox.getValue())
		{
			parentWindow.model.setCommandAfterSnapshot(postSnapshotCommand.getValue());		
		}
		else
		{
			parentWindow.model.setCommandAfterSnapshot("");
		}
		
		if (usernameTF.getValue() != null)
		{
			parentWindow.model.setActionsUserName(usernameTF.getValue());
		}
		if (passwordTF.getValue() != null)
		{
			parentWindow.model.setActionsPassword(passwordTF.getValue());
		}
		
	}
	public boolean Validate()
	{		

		if (preCheckbox.getValue())
		{
			String val = preCommand.getValue();
			if (val == null || val.trim().isEmpty())
			{
				//Fail empty pre command
				MessageBox msg = new MessageBox();
				msg.setIcon(MessageBox.ERROR);
				msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNamevSphere));
				msg.setMessage(UIContext.Constants.advancedPreCommandBlank());
				msg.setModal(true);
				Utils.setMessageBoxDebugId(msg);
				msg.show();
				return false;
			}
			else if (!validateCommandExists(val))
			{
				MessageBox msg = new MessageBox();
				msg.setIcon(MessageBox.ERROR);
				msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNamevSphere));
				msg.setMessage(UIContext.Constants.advancedPreCommandDNE());
				msg.setModal(true);
				Utils.setMessageBoxDebugId(msg);
				msg.show();
				return false;
			}
			
		}
		if (postSnapshotCheckbox.getValue())
		{
			String val = postSnapshotCommand.getValue();
			if (val == null || val.trim().isEmpty())
			{
				//Fail empty postSnapshotCommand
				MessageBox msg = new MessageBox();
				msg.setIcon(MessageBox.ERROR);
				msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNamevSphere));
				msg.setMessage(UIContext.Constants.advancedPostSnapCommandBlank());
				msg.setModal(true);
				Utils.setMessageBoxDebugId(msg);
				msg.show();
				return false;
			}
			else if (!validateCommandExists(val))
			{
				MessageBox msg = new MessageBox();
				msg.setIcon(MessageBox.ERROR);
				msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNamevSphere));
				msg.setMessage(UIContext.Constants.advancedPostSnapCommandDNE());
				msg.setModal(true);
				Utils.setMessageBoxDebugId(msg);
				msg.show();
				return false;
			}
		}		
		if (postCheckbox.getValue())
		{
			String val = postCommand.getValue();
			if (val == null || val.trim().isEmpty())
			{
				//Fail empty postCommand
				MessageBox msg = new MessageBox();
				msg.setIcon(MessageBox.ERROR);
				msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNamevSphere));
				msg.setMessage(UIContext.Constants.advancedPostCommandBlank());
				msg.setModal(true);
				Utils.setMessageBoxDebugId(msg);
				msg.show();
				return false;
			}
			else if (!validateCommandExists(val))
			{
				MessageBox msg = new MessageBox();
				msg.setIcon(MessageBox.ERROR);
				msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNamevSphere));
				msg.setMessage(UIContext.Constants.advancedPostCommandDNE());
				msg.setModal(true);
				Utils.setMessageBoxDebugId(msg);
				msg.show();
				return false;
			}
		}
		if ((preCheckbox.getValue() || 
				postCheckbox.getValue()  ||
				postSnapshotCheckbox.getValue()) &&
				(usernameTF.getValue() == null || 
						usernameTF.getValue().trim().isEmpty()))
		{
			MessageBox msg = new MessageBox();
			msg.setIcon(MessageBox.ERROR);
			msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNamevSphere));
			msg.setMessage(UIContext.Constants.advancedUserNameCannotBeBlank());
			msg.setModal(true);
			Utils.setMessageBoxDebugId(msg);
			msg.show();
			return false;
		}
		
		if ((preCheckbox.getValue() || 
				postCheckbox.getValue()  ||
				postSnapshotCheckbox.getValue()) &&
				(passwordTF.getValue() == null || 
						passwordTF.getValue().trim().isEmpty()))
			{
				MessageBox msg = new MessageBox();
				msg.setIcon(MessageBox.ERROR);
				msg.setTitleHtml(UIContext.Messages.messageBoxTitleError(UIContext.productNamevSphere));
				msg.setMessage(UIContext.Constants.advancedPasswordCannotBeBlank());
				msg.setModal(true);
				Utils.setMessageBoxDebugId(msg);
				msg.show();
				return false;
			}
			
		return true;
	}

	private boolean validateCommandExists(String val) {
		//TODO: Check that the command actually exists on the server
		return true;
		
	}
	
}
