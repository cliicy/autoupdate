package com.ca.arcflash.ui.client.vsphere.homepage;

import com.ca.arcflash.ui.client.UIContext;
import com.ca.arcflash.ui.client.common.AppType;
import com.ca.arcflash.ui.client.common.BaseAsyncCallback;
import com.ca.arcflash.ui.client.common.CommonService;
import com.ca.arcflash.ui.client.common.CommonServiceAsync;
import com.ca.arcflash.ui.client.common.CommonSettingWindow;
import com.ca.arcflash.ui.client.homepage.TaskPanel;
import com.ca.arcflash.ui.client.model.RolePrivilegeModel;
import com.ca.arcflash.ui.client.mount.MountWindow;
import com.ca.arcflash.ui.client.restore.RestoreWizardWindow;
import com.ca.arcflash.ui.client.vsphere.log.VSphereLogWindow;
import com.ca.arcflash.ui.client.vsphere.recoverypoint.VSphereRecoveryPointWindow;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class VSphereTaskPanel extends TaskPanel {
	boolean isbkpNowClicked = false;
	private Menu restoreSubMenu;
	
	public VSphereTaskPanel()
	{
		super();
	}

	@Override
	protected void addTask2Panel() {
		RolePrivilegeModel privilege=UIContext.RolePrivilege;
		if(privilege==null||privilege.getBackupFlag()!=RolePrivilegeModel.DISPLAY_DISABLE)
			addItem(UIContext.Constants.homepageTasksBackupNowLabel(),UIContext.Constants.homepageTasksBackupNowDescription(),backupNowHandler,AbstractImagePrototype.create(UIContext.IconBundle.tasks_backup()).createImage(),null,"4a31d990-6248-41ac-b8c9-3632d0f325e3", true, new LayoutContainer());
		if(privilege==null||privilege.getRestoreFlag()!=RolePrivilegeModel.DISPLAY_DISABLE)
		addItem(UIContext.Constants.homepageTasksRestoreLabel(),UIContext.Constants.homepageTasksRestoreDescription(),restoreHandler,AbstractImagePrototype.create(UIContext.IconBundle.tasks_restore()).createImage(),restoreSubMenu,"7cc60280-cbdc-4d88-b94c-7f435508c5a0", true, new LayoutContainer());
		if(privilege==null||privilege.getSettingFlag()!=RolePrivilegeModel.DISPLAY_DISABLE)
		addItem(UIContext.Constants.homepageTasksBackupSettingLabel(),UIContext.Constants.homepageTasksBackupSettingDescription(),settingHandler,AbstractImagePrototype.create(UIContext.IconBundle.tasks_backupSetting()).createImage(),null,"2cbea22c-63c6-4fe0-8ec7-1ef8352f464f", true, new LayoutContainer());
		if(privilege==null||privilege.getCopyRecoverPointFlag()!=RolePrivilegeModel.DISPLAY_DISABLE)
		addItem(UIContext.Constants.homepageTasksRecoveryPointsLabel(), UIContext.Constants.homepageTasksRecoveryPointsDescription(), recoveryPointHandler, AbstractImagePrototype.create(UIContext.IconBundle.tasks_recovery()).createImage(),null,"fa6dabc0-d981-43ec-bb1b-ac71da0dc27c", true, new LayoutContainer());
		if(privilege==null||privilege.getMountRecoverPointFlag()!=RolePrivilegeModel.DISPLAY_DISABLE)
		addItem(UIContext.Constants.mountTaskTitle(), UIContext.Constants.mountTaskDescription(),mountHandler,  AbstractImagePrototype.create(UIContext.IconBundle.task_mount_volume()).createImage(), null, "1c175499-57f0-4fec-b916-262e66942340", true, new LayoutContainer());
	    addItem(UIContext.Constants.homepageTasksLogsLabel(),UIContext.Constants.homepageTasksLogsDescription(),logHandler,AbstractImagePrototype.create(UIContext.IconBundle.tasks_log()).createImage(),null,"766642e0-6683-4b9a-b854-3b8d9b6b0e2e", true, new LayoutContainer());
	}


	@Override
	protected void checkDriver() {
		
	}

	@Override
	protected void onBackupNowClick() {
		if (!isbkpNowClicked) {
			isbkpNowClicked = true;					
				service
				.isVMCompressionLevelChagned(UIContext.backupVM,new BaseAsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						isbkpNowClicked = false;
						VSphereBackupNowWindow bkpNowWnd = new VSphereBackupNowWindow();
						bkpNowWnd.setModal(true);
						bkpNowWnd.show();
						bkpNowWnd.changeSettings(false);
						super.onFailure(caught);
					}

					@Override
					public void onSuccess(Boolean result) {
						isbkpNowClicked = false;
						VSphereBackupNowWindow bkpNowWnd = new VSphereBackupNowWindow();
						bkpNowWnd.setModal(true);									
						bkpNowWnd.show();
						bkpNowWnd.changeSettings(result);
					}
				});
			}
	}

	@Override
	protected void onSettingClick() {
		CommonSettingWindow window = new CommonSettingWindow(AppType.VSPHERE);
		window.setSize(880, 655);
		window.setModal(true);
		window.show();
	}

	@Override
	protected void onLogClick() {
		VSphereLogWindow window = new VSphereLogWindow();
		window.setModal(true);
		window.show();
	}

	@Override
	protected void onRecoveryPointClick() {
		VSphereRecoveryPointWindow window = new VSphereRecoveryPointWindow();
		window.setModal(true);
		window.show();
	}
		
}
